package wedjets

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
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

@Composable
fun HeksagonGred(
    geometry: HeksagonDjeyometre,
    items: List<GredItem>,
    onSwap: (Int, Int) -> Unit,
    onCopyToEmpty: (Int, Int) -> Unit,
    onMoveToCenter: (Int) -> Unit,
    onDropOnFolder: (Int, Int) -> Unit,
    modifier: Modifier = Modifier,
    centerLabel: String = "",
    centerColor: Color = Color.Black,
    onTap: (Int) -> Unit = {}
) {
    val haptic = LocalHapticFeedback.current
    val density = LocalDensity.current
    var draggingIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var currentHoverIndex by remember { mutableStateOf<Int?>(null) }
    var lastSameSpotIndex by remember { mutableStateOf<Int?>(null) }

    val allHexPositions = remember(geometry) {
        val inner = geometry.getEnirRenqKowordenats().map { geometry.aksyalTuPeksel(it.q, it.r) }
        val outer = geometry.getAwdirRenqKowordenats().map { geometry.aksyalTuPeksel(it.q, it.r) }
        inner + outer + listOf(geometry.sentir)
    }

    /** Geometry uses dp-like numbers (same as `.dp` in layout). Hit-test in px so taps register on all densities. */
    val allHexPositionsPx = remember(geometry, density, allHexPositions) {
        allHexPositions.map { pos ->
            HeksagonPozecon(
                x = with(density) { pos.x.dp.toPx().toDouble() },
                y = with(density) { pos.y.dp.toPx().toDouble() }
            )
        }
    }
    val hexSizePx = remember(geometry, density) {
        with(density) { geometry.heksSayz.dp.toPx() }
    }

    fun applyDragEnd(fromIdx: Int?, toIdx: Int?, copyThisDrag: Boolean) {
        if (fromIdx != null) {
            val sameSpot = (toIdx == fromIdx)
            if (sameSpot) {
                lastSameSpotIndex = fromIdx
            } else {
                lastSameSpotIndex = null
                if (toIdx == null) {
                    // void / cancel
                } else if (copyThisDrag) {
                    when (toIdx) {
                        18 -> { /* no copy-to-center */ }
                        else -> {
                            val targetItem = items.find { it.index == toIdx }
                            if (targetItem == null) {
                                onCopyToEmpty(fromIdx, toIdx)
                            }
                        }
                    }
                } else {
                    when {
                        toIdx == 18 -> onMoveToCenter(fromIdx)
                        else -> {
                            val targetItem = items.find { it.index == toIdx }
                            if (targetItem != null) {
                                if (targetItem.isFolder) {
                                    onDropOnFolder(fromIdx, toIdx)
                                } else {
                                    onSwap(fromIdx, toIdx)
                                }
                            } else {
                                onCopyToEmpty(fromIdx, toIdx)
                            }
                        }
                    }
                }
            }
        }
    }

    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        val wPx = constraints.maxWidth.toFloat()
        val hPx = constraints.maxHeight.toFloat()

        val hexWidthDp = geometry.heksWidlx.dp

        HeksagonWedjet(
            label = centerLabel,
            backgroundColor = if (currentHoverIndex == 18) Color.White.copy(alpha = 0.5f) else centerColor,
            textColor = KepadKonfeg.getComplementaryColor(centerColor),
            size = hexWidthDp,
            fontSize = (geometry.heksWidlx * 0.5).toFloat(),
            modifier = Modifier.align(Alignment.Center).offset(x = geometry.sentir.x.dp, y = geometry.sentir.y.dp),
            onTap = null,
            onLongPress = null
        )

        allHexPositions.forEachIndexed { index, pos ->
            if (index < 18) {
                val item = items.find { it.index == index }
                val isDragging = draggingIndex == index
                val isHovered = currentHoverIndex == index && draggingIndex != null

                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(x = pos.x.dp, y = pos.y.dp)
                ) {
                    if (item != null) {
                        HeksagonWedjet(
                            label = item.label,
                            backgroundColor = if (isHovered) Color.White.copy(alpha = 0.5f) else item.color,
                            textColor = KepadKonfeg.getComplementaryColor(item.color),
                            size = hexWidthDp,
                            fontSize = (geometry.heksWidlx * 0.5).toFloat(),
                            onTap = null,
                            onLongPress = null,
                            modifier = if (isDragging) Modifier.offset(
                                x = (dragOffset.x - pos.x - this@BoxWithConstraints.constraints.maxWidth / 2).dp,
                                y = (dragOffset.y - pos.y - this@BoxWithConstraints.constraints.maxHeight / 2).dp
                            ) else Modifier
                        )
                    } else if (isHovered) {
                        HeksagonWedjet(
                            label = "+",
                            backgroundColor = Color.Gray.copy(alpha = 0.3f),
                            textColor = Color.White,
                            size = hexWidthDp,
                            fontSize = (geometry.heksWidlx * 0.5).toFloat()
                        )
                    }
                }
            }
        }

        Box(
            Modifier
                .fillMaxSize()
                .align(Alignment.Center)
                .pointerInput(geometry, items, lastSameSpotIndex, wPx, hPx, allHexPositionsPx, hexSizePx) {
                    val longMs = viewConfiguration.longPressTimeoutMillis
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        val start = down.position

                        val upBeforeLongPress = withTimeoutOrNull(longMs) {
                            waitForUpOrCancellation()
                            true
                        }
                        if (upBeforeLongPress == true) {
                            val idx = getHexIndexFromPosition(
                                start.x, start.y, wPx, hPx, allHexPositionsPx, hexSizePx
                            )
                            if (idx != null) {
                                when {
                                    idx == 18 -> onTap(18)
                                    items.any { it.index == idx } -> onTap(idx)
                                }
                            }
                            return@awaitEachGesture
                        }

                        var idx = getHexIndexFromPosition(
                            start.x, start.y, wPx, hPx, allHexPositionsPx, hexSizePx
                        )
                        if (idx == null || !items.any { it.index == idx }) {
                            waitForUpOrCancellation()
                            return@awaitEachGesture
                        }

                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        val copyThisDrag = (lastSameSpotIndex == idx)
                        draggingIndex = idx
                        dragOffset = start
                        currentHoverIndex = idx

                        try {
                            while (true) {
                                val event = awaitPointerEvent(PointerEventPass.Main)
                                val change = event.changes.find { it.id == down.id } ?: break
                                if (change.changedToUpIgnoreConsumed()) break
                                val delta = change.positionChange()
                                dragOffset = Offset(dragOffset.x + delta.x, dragOffset.y + delta.y)
                                currentHoverIndex = getHexIndexFromPosition(
                                    dragOffset.x, dragOffset.y, wPx, hPx, allHexPositionsPx, hexSizePx
                                )
                                change.consume()
                            }
                        } finally {
                            applyDragEnd(draggingIndex, currentHoverIndex, copyThisDrag)
                            draggingIndex = null
                            currentHoverIndex = null
                            dragOffset = Offset.Zero
                        }
                    }
                }
        )
    }
}

data class GredItem(
    val index: Int,
    val label: String,
    val color: Color,
    val isFolder: Boolean = false,
    val deyda: Any? = null
)

private fun getHexIndexFromPosition(
    offsetX: Float,
    offsetY: Float,
    w: Float,
    h: Float,
    allHexPositionsPx: List<HeksagonPozecon>,
    hexSizePx: Float
): Int? {
    val localX = offsetX - w / 2f
    val localY = offsetY - h / 2f
    var closestIndex: Int? = null
    var minDistSq = Double.MAX_VALUE
    for (i in allHexPositionsPx.indices) {
        val hexPos = allHexPositionsPx[i]
        val distSq = (localX - hexPos.x).toDouble().pow(2) + (localY - hexPos.y).toDouble().pow(2)
        if (distSq < minDistSq) {
            minDistSq = distSq
            closestIndex = i
        }
    }
    if (closestIndex != null) {
        val center = allHexPositionsPx[closestIndex]
        val dx = kotlin.math.abs(localX - center.x)
        val dy = kotlin.math.abs(localY - center.y)
        val sqrt3Val = sqrt(3.0).toFloat()
        if (dx > hexSizePx * sqrt3Val / 2f) return null
        if ((dx + sqrt3Val * dy) <= (sqrt3Val * hexSizePx)) return closestIndex
    }
    return null
}
