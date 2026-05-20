package wedjets

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.withTimeoutOrNull
import modalz.HeksagonPozecon
import modalz.KepadKonfeg
import yuteledez.HeksagonDjeyometre
import kotlin.math.pow
import kotlin.math.sqrt

enum class CopyDragPolicy {
    TwoStepArmed,
    AlwaysCopy,
    NeverCopy
}

data class GredItem(
    val index: Int,
    val label: String,
    val color: Color,
    val isFolder: Boolean = false,
    val deyda: Any? = null
)

@Composable
fun HeksagonGred(
    geometry: HeksagonDjeyometre,
    items: List<GredItem>,
    onMove: (Int, Int) -> Unit,
    onCopyToEmpty: (Int, Int) -> Unit,
    onMoveToCenter: (Int) -> Unit,
    onDropOnFolder: (Int, Int) -> Unit,
    modifier: Modifier = Modifier,
    centerLabel: String = "",
    centerColor: Color = Color.Black,
    onTap: (Int) -> Unit = {},
    copyDragPolicy: CopyDragPolicy = CopyDragPolicy.TwoStepArmed,
    allowSwap: Boolean = true,
    fontSizeFactor: Float = 6f / 12f,
    useConsistentSize: Boolean = false
) {
    val haptic = LocalHapticFeedback.current
    val density = LocalDensity.current
    var draggingIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var currentHoverIndex by remember { mutableStateOf<Int?>(null) }
    var lastSameSpotIndex by remember { mutableStateOf<Int?>(null) }
    var isCopyDragActive by remember { mutableStateOf(false) }

    val allHexPositions = remember(geometry, items) {
        val maxIndex = items.maxOfOrNull { it.index } ?: 0
        var ringsNeeded = 1
        while (3 * ringsNeeded * (ringsNeeded + 1) < maxIndex) ringsNeeded++
        val displayRings = maxOf(3, ringsNeeded + 1)
        val posList = mutableListOf<HeksagonPozecon>()
        posList.add(geometry.sentir)
        for (r in 1..displayRings) {
            val coords = geometry.getKowordenatsForRenq(r)
            posList.addAll(coords.map { geometry.aksyalTuPeksel(it.q, it.r) })
        }
        posList
    }

    val allHexPositionsPx = remember(geometry, density, allHexPositions) {
        allHexPositions.map { pos ->
            HeksagonPozecon(
                x = with(density) { pos.x.dp.toPx().toDouble() },
                y = with(density) { pos.y.dp.toPx().toDouble() }
            )
        }
    }
    val hexSizePx = remember(geometry, density) { with(density) { geometry.heksSayz.dp.toPx() } }

    fun applyDragEnd(fromIdx: Int?, toIdx: Int?, isMoveDrag: Boolean) {
        if (fromIdx != null && toIdx != null) {
            val sameSpot = (toIdx == fromIdx)
            if (sameSpot) {
                if (copyDragPolicy == CopyDragPolicy.TwoStepArmed) lastSameSpotIndex = fromIdx
            } else {
                if (copyDragPolicy == CopyDragPolicy.TwoStepArmed) lastSameSpotIndex = null
                if (isMoveDrag) {
                    if (toIdx == 0) onMoveToCenter(fromIdx)
                    else {
                        val targetItem = items.firstOrNull { it.index == toIdx }
                        val isTargetOccupied = targetItem != null && targetItem.label.isNotEmpty()
                        if (isTargetOccupied) {
                            if (targetItem.isFolder) onDropOnFolder(fromIdx, toIdx)
                            else if (allowSwap) onMove(fromIdx, toIdx)
                        } else {
                            onMove(fromIdx, toIdx)
                        }
                    }
                } else {
                    if (toIdx != 0) {
                        val targetItem = items.firstOrNull { it.index == toIdx }
                        if (targetItem == null || targetItem.label.isEmpty()) onCopyToEmpty(fromIdx, toIdx)
                    }
                }
            }
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val wPx = constraints.maxWidth.toFloat()
        val hPx = constraints.maxHeight.toFloat()
        val hexWidthDp = geometry.heksWidlx.dp

        allHexPositions.forEachIndexed { index, pos ->
            val item = items.firstOrNull { it.index == index }
            val isDragging = draggingIndex == index
            val isHovered = currentHoverIndex == index && draggingIndex != null
            Box(modifier = Modifier.align(Alignment.Center).offset(x = pos.x.dp, y = pos.y.dp)) {
                if (index == 0 && item == null && centerLabel.isNotEmpty()) {
                    HeksagonWedjet(
                        label = centerLabel, backgroundColor = centerColor,
                        textColor = KepadKonfeg.getComplementaryColor(centerColor), size = hexWidthDp, fontSizeFactor = fontSizeFactor,
                        useConsistentSize = useConsistentSize,
                        onTap = null, onLongPress = null,
                        modifier = if (isHovered) {
                            Modifier.drawBehind {
                                val glowRadius = size.maxDimension * 1f
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        colors = listOf(Color.White.copy(alpha = 10f/12f), Color.Transparent),
                                        center = center,
                                        radius = glowRadius
                                    ),
                                    radius = glowRadius
                                )
                            }
                        } else Modifier
                    )
                } else if (item != null) {
                    HeksagonWedjet(
                        label = item.label, backgroundColor = item.color,
                        textColor = KepadKonfeg.getComplementaryColor(item.color), size = hexWidthDp, fontSizeFactor = fontSizeFactor,
                        useConsistentSize = useConsistentSize,
                        onTap = null, onLongPress = null,
                        modifier = if (isDragging && !isCopyDragActive) {
                            val fingerXDp = with(density) { (dragOffset.x - wPx / 2f).toDp() }
                            val fingerYDp = with(density) { (dragOffset.y - hPx / 2f).toDp() }
                            Modifier.offset(x = fingerXDp - pos.x.dp, y = fingerYDp - pos.y.dp)
                                .drawBehind {
                                    val glowRadius = size.maxDimension * 1f
                                    drawCircle(
                                        brush = Brush.radialGradient(
                                            colors = listOf(item.color.copy(alpha = 10f/12f), Color.Transparent),
                                            center = center,
                                            radius = glowRadius
                                        ),
                                        radius = glowRadius
                                    )
                                }
                        } else if (isHovered) {
                            Modifier.drawBehind {
                                val glowRadius = size.maxDimension * 1f
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        colors = listOf(Color.White.copy(alpha = 10f/12f), Color.Transparent),
                                        center = center,
                                        radius = glowRadius
                                    ),
                                    radius = glowRadius
                                )
                            }
                        } else Modifier
                    )
                } else if (isHovered && index > 0) {
                    HeksagonWedjet(
                        label = "+", backgroundColor = Color.Gray.copy(alpha = 4f / 12f), textColor = Color.White, size = hexWidthDp,
                        fontSizeFactor = fontSizeFactor, useConsistentSize = useConsistentSize
                    )
                }
            }
        }

        val ghostItem = remember(items, draggingIndex) { items.firstOrNull { it.index == draggingIndex } }
        if (isCopyDragActive && ghostItem != null && draggingIndex != null) {
            val xDp = with(density) { dragOffset.x.toDp() }; val yDp = with(density) { dragOffset.y.toDp() }
            HeksagonWedjet(
                label = ghostItem.label, backgroundColor = ghostItem.color.copy(alpha = 9f / 12f), textColor = KepadKonfeg.getComplementaryColor(ghostItem.color),
                size = hexWidthDp, fontSizeFactor = fontSizeFactor, onTap = null, onLongPress = null,
                modifier = Modifier.align(Alignment.TopStart).offset(x = xDp - (hexWidthDp / 2), y = yDp - ((hexWidthDp * (2f / kotlin.math.sqrt(3f))) / 2))
                    .drawBehind {
                        val glowRadius = size.maxDimension * 1f
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color.Yellow.copy(alpha = 10f/12f), Color.Transparent),
                                center = center,
                                radius = glowRadius
                            ),
                            radius = glowRadius
                        )
                    }
            )
        }

        Box(Modifier.fillMaxSize().align(Alignment.Center).pointerInput(geometry, items, lastSameSpotIndex, wPx, hPx, allHexPositionsPx, hexSizePx) {
            val longMs = viewConfiguration.longPressTimeoutMillis
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false)
                val start = down.position
                val upBeforeLongPress = withTimeoutOrNull(longMs) { waitForUpOrCancellation(); true }
                if (upBeforeLongPress == true) {
                    val idx = getHexIndexFromPosition(start.x, start.y, wPx, hPx, allHexPositionsPx, hexSizePx)
                    if (idx != null) { onTap(idx); if (copyDragPolicy == CopyDragPolicy.TwoStepArmed) lastSameSpotIndex = idx }
                    return@awaitEachGesture
                }
                var idx = getHexIndexFromPosition(start.x, start.y, wPx, hPx, allHexPositionsPx, hexSizePx)
                if (idx == null || !items.any { it.index == idx && it.label.isNotEmpty() }) {
                    if (idx == null || !items.any { it.index == idx }) { waitForUpOrCancellation(); return@awaitEachGesture }
                }
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                val isMoveDrag = when (copyDragPolicy) {
                    CopyDragPolicy.AlwaysCopy -> false; CopyDragPolicy.NeverCopy -> true; CopyDragPolicy.TwoStepArmed -> (lastSameSpotIndex != idx)
                }
                draggingIndex = idx; isCopyDragActive = !isMoveDrag; dragOffset = start; currentHoverIndex = idx
                try {
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Main); val change = event.changes.find { it.id == down.id } ?: break
                        if (change.changedToUpIgnoreConsumed()) break
                        val delta = change.positionChange(); dragOffset = Offset(dragOffset.x + delta.x, dragOffset.y + delta.y)
                        currentHoverIndex = getHexIndexFromPosition(
                                    dragOffset.x, dragOffset.y, wPx, hPx, allHexPositionsPx, hexSizePx
                                )
                        change.consume()
                    }
                } finally {
                    applyDragEnd(draggingIndex, currentHoverIndex, isMoveDrag)
                    draggingIndex = null; currentHoverIndex = null; dragOffset = Offset.Zero; isCopyDragActive = false
                }
            }
        })
    }
}

private fun getHexIndexFromPosition(offsetX: Float, offsetY: Float, w: Float, h: Float, allHexPositionsPx: List<HeksagonPozecon>, hexSizePx: Float): Int? {
    val localX = offsetX - w / 2f; val localY = offsetY - h / 2f
    var closestIndex: Int? = null; var minDistSq = Double.MAX_VALUE
    for (i in allHexPositionsPx.indices) {
        val hexPos = allHexPositionsPx[i]; val distSq = (localX - hexPos.x).toDouble().pow(2) + (localY - hexPos.y).toDouble().pow(2)
        if (distSq < minDistSq) { minDistSq = distSq; closestIndex = i }
    }
    if (closestIndex != null) {
        val center = allHexPositionsPx[closestIndex]; val dx = kotlin.math.abs(localX - center.x); val dy = kotlin.math.abs(localY - center.y)
        val sqrt3Val = sqrt(3.0).toFloat()
        if (dx > hexSizePx * sqrt3Val / 2f) return null
        if ((dx + sqrt3Val * dy) <= (sqrt3Val * hexSizePx)) return closestIndex
    }
    return null
}
