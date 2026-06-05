package wedjets

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
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
import modalz.HeksagonKonfeg
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
    onDropOnFoldir: (Int, Int) -> Unit,
    onReplace: ((Int, Int) -> Unit)? = null,
    modifier: Modifier = Modifier,
    centerLabel: String = "",
    centerColor: Color = Color.Black,
    onTap: (Int) -> Unit = {},
    onDelete: (Int) -> Unit = {},
    copyDragPolicy: CopyDragPolicy = CopyDragPolicy.TwoStepArmed,
    allowSwap: Boolean = true,
    fontSizeFactor: Float = 6f / 12f,
    ezKonsestentSayz: Boolean = false
) {
    var pendingReplaceFrom by remember { mutableStateOf<Int?>(null) }
    var pendingReplaceTo by remember { mutableStateOf<Int?>(null) }
    var showReplaceDialog by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current
    val density = LocalDensity.current
    var draggingIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var currentHoverIndex by remember { mutableStateOf<Int?>(null) }
    var lastSameSpotIndex by remember { mutableStateOf<Int?>(null) }
    var isCopyDragActive by remember { mutableStateOf(false) }
    var presdEndeks by remember { mutableStateOf<Int?>(null) }
    var glowenqEndeks by remember { mutableStateOf<Int?>(null) }
    var disconnectedArmedIndex by remember { mutableStateOf<Int?>(null) }

    val allHexPositions = remember(geometry, items) {
        val maxIndex = items.maxOfOrNull { it.index } ?: 0
        var ringsNeeded = 1
        while (3 * ringsNeeded * (ringsNeeded + 1) < maxIndex) ringsNeeded++
        // Default to 2 rings minimum for matching keypad scale
        val displayRings = maxOf(2, ringsNeeded)
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

    // BFS: compute which indices are reachable from index 0 via occupied neighbours
    val konektedIndeksez = remember(items) {
        val occupiedSet = items.filter { it.label.isNotEmpty() }.map { it.index }.toSet()
        val visited = mutableSetOf(0)
        val queue = ArrayDeque<Int>()
        queue.add(0)
        while (queue.isNotEmpty()) {
            val cur = queue.removeFirst()
            geometry.getNeybirIndesiz(cur).forEach { nb ->
                if (nb >= 0 && nb in occupiedSet && nb !in visited) {
                    visited.add(nb)
                    queue.add(nb)
                }
            }
        }
        visited
    }

    fun applyDragEnd(fromIdx: Int?, toIdx: Int?, isMoveDrag: Boolean) {
        if (fromIdx != null && toIdx != null) {
            val sameSpot = (toIdx == fromIdx)
            if (sameSpot) {
                if (copyDragPolicy == CopyDragPolicy.TwoStepArmed) lastSameSpotIndex = fromIdx
            } else {
                if (copyDragPolicy == CopyDragPolicy.TwoStepArmed) lastSameSpotIndex = null
                if (toIdx == 0) {
                    onMoveToCenter(fromIdx)
                    return
                }

                val draggedItem = items.firstOrNull { it.index == fromIdx }
                val targetItem = items.firstOrNull { it.index == toIdx }
                
                if (draggedItem != null && targetItem != null && 
                    draggedItem.label.isNotEmpty() && targetItem.label.isNotEmpty() && 
                    !targetItem.isFolder && draggedItem.label == targetItem.label
                ) {
                    if (onReplace != null) {
                        pendingReplaceFrom = fromIdx
                        pendingReplaceTo = toIdx
                        showReplaceDialog = true
                        return
                    }
                }

                if (isMoveDrag) {
                    if (targetItem?.isFolder == true) {
                        onDropOnFoldir(fromIdx, toIdx)
                    } else if (targetItem != null && targetItem.label.isNotEmpty()) {
                        if (allowSwap) onMove(fromIdx, toIdx)
                    } else {
                        onMove(fromIdx, toIdx)
                    }
                } else {
                    if (toIdx != 0) {
                        if (targetItem == null || targetItem.label.isEmpty()) onCopyToEmpty(fromIdx, toIdx)
                    }
                }
            }
        }
    }

    val isAnyTouchActive = glowenqEndeks != null || draggingIndex != null

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val wPx = constraints.maxWidth.toFloat()
        val hPx = constraints.maxHeight.toFloat()
        val hexWidthDp = geometry.heksWidlx.dp
        
        val draggedItem = if (draggingIndex != null) items.firstOrNull { it.index == draggingIndex } else null

        allHexPositions.forEachIndexed { index, pos ->
            val item = items.firstOrNull { it.index == index }
            val isDragging = draggingIndex == index
            val isHovered = currentHoverIndex == index && draggingIndex != null

            Box(modifier = Modifier.align(Alignment.Center).offset(x = pos.x.dp, y = pos.y.dp)) {
                if (index == 0 && item == null && centerLabel.isNotEmpty()) {
                    Heksagon(
                        label = centerLabel, backgroundColor = centerColor,
                        textColor = HeksagonKonfeg.getComplementaryColor(centerColor), size = hexWidthDp, fontSizeFactor = fontSizeFactor,
                        ezKonsestentSayz = ezKonsestentSayz,
                        onTap = null, onLongPress = null,
                        ezGlowenq = isAnyTouchActive || glowenqEndeks == index,
                        ezPresd = presdEndeks == index
                    )
                } else if (item != null) {
                    val isArmed = index == disconnectedArmedIndex
                    val showContrast = if (isCopyDragActive && isDragging) {
                        false // Original item stays normal while copying
                    } else if (isDragging) {
                        true // Flip immediately for move
                    } else {
                        false
                    }

                    if (!isArmed) {
                        Heksagon(
                            label = item.label, backgroundColor = item.color,
                            textColor = HeksagonKonfeg.getComplementaryColor(item.color), size = hexWidthDp, fontSizeFactor = fontSizeFactor,
                            ezKonsestentSayz = ezKonsestentSayz,
                            onTap = null, onLongPress = null,
                            ezPresd = showContrast,
                            ezGlowenq = glowenqEndeks == index,
                            modifier = if (isDragging && !isCopyDragActive) {
                                val fingerXDp = with(density) { (dragOffset.x - wPx / 2f).toDp() }
                                val fingerYDp = with(density) { (dragOffset.y - hPx / 2f).toDp() }
                                Modifier.offset(x = fingerXDp - pos.x.dp, y = fingerYDp - pos.y.dp)
                            } else Modifier
                        )
                    }
                } else if (isHovered && index > 0 && draggedItem != null) {
                    Heksagon(
                        label = draggedItem.label, 
                        backgroundColor = draggedItem.color, 
                        textColor = HeksagonKonfeg.getComplementaryColor(draggedItem.color), 
                        size = hexWidthDp,
                        fontSizeFactor = fontSizeFactor, 
                        ezKonsestentSayz = ezKonsestentSayz,
                        ezPresd = false,
                        ezGlowenq = true
                    )
                }
            }
        }

        val ghostItem = remember(items, draggingIndex) { items.firstOrNull { it.index == draggingIndex } }
        if (isCopyDragActive && ghostItem != null && draggingIndex != null) {
            val xDp = with(density) { dragOffset.x.toDp() }; val yDp = with(density) { dragOffset.y.toDp() }
            Heksagon(
                label = ghostItem.label, backgroundColor = ghostItem.color, textColor = HeksagonKonfeg.getComplementaryColor(ghostItem.color),
                size = hexWidthDp, fontSizeFactor = fontSizeFactor, onTap = null, onLongPress = null,
                ezPresd = true, // Flip immediately for copy ghost
                ezGlowenq = true,
                modifier = Modifier.align(Alignment.TopStart).offset(x = xDp - (hexWidthDp / 2), y = yDp - ((hexWidthDp * (2f / kotlin.math.sqrt(3f))) / 2))
            )
        }

        if (showReplaceDialog) {
            AlertDialog(
                onDismissRequest = { showReplaceDialog = false },
                title = { Text("Replace?") },
                text = { Text("A file/module with the same name already exists. Do you want to replace it?") },
                confirmButton = {
                    Button(onClick = {
                        val from = pendingReplaceFrom
                        val to = pendingReplaceTo
                        if (from != null && to != null) onReplace?.invoke(from, to)
                        showReplaceDialog = false
                    }) { Text("Yes") }
                },
                dismissButton = {
                    Button(onClick = { showReplaceDialog = false }) { Text("No") }
                }
            )
        }

        Box(Modifier.fillMaxSize().align(Alignment.Center).pointerInput(geometry, allHexPositionsPx, items, konektedIndeksez, lastSameSpotIndex, disconnectedArmedIndex) {
            val longMs = viewConfiguration.longPressTimeoutMillis
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false)
                val start = down.position
                val downIdx = getHexIndexFromPosition(start.x, start.y, wPx, hPx, allHexPositionsPx, hexSizePx)
                
                if (downIdx == null) return@awaitEachGesture
                
                val isCenterOccupied = downIdx == 0 && centerLabel.isNotEmpty()
                val isItemAtIdx = items.any { it.index == downIdx && it.label.isNotEmpty() }
                
                if (!isCenterOccupied && !isItemAtIdx && disconnectedArmedIndex != downIdx) {
                    return@awaitEachGesture
                }

                glowenqEndeks = downIdx
                presdEndeks = downIdx

                try {
                    val upBeforeLongPress = withTimeoutOrNull(longMs) {
                        while (true) {
                            val event = awaitPointerEvent()
                            if (event.changes.any { it.changedToUpIgnoreConsumed() }) return@withTimeoutOrNull true
                        }
                    }
                    if (upBeforeLongPress == true) {
                        onTap(downIdx)
                        if (copyDragPolicy == CopyDragPolicy.TwoStepArmed) lastSameSpotIndex = downIdx
                        return@awaitEachGesture
                    }

                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                    val isDiskonekded = downIdx != 0 && downIdx !in konektedIndeksez && isItemAtIdx

                    if (isDiskonekded && disconnectedArmedIndex == downIdx) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        disconnectedArmedIndex = null
                        lastSameSpotIndex = null
                        presdEndeks = null
                        waitForUpOrCancellation()
                        onDelete(downIdx)
                        return@awaitEachGesture
                    }

                    if (isDiskonekded && disconnectedArmedIndex != downIdx) {
                        disconnectedArmedIndex = downIdx
                        lastSameSpotIndex = null
                        draggingIndex = downIdx; isCopyDragActive = false; dragOffset = start; currentHoverIndex = downIdx
                        presdEndeks = downIdx
                        var hasMuvd = false
                        try {
                            while (true) {
                                val event = awaitPointerEvent(PointerEventPass.Main)
                                val change = event.changes.find { it.id == down.id } ?: break
                                if (change.changedToUpIgnoreConsumed()) break
                                val delta = change.positionChange()
                                dragOffset = Offset(dragOffset.x + delta.x, dragOffset.y + delta.y)
                                val newHover = getHexIndexFromPosition(dragOffset.x, dragOffset.y, wPx, hPx, allHexPositionsPx, hexSizePx)
                                if (newHover != downIdx) hasMuvd = true
                                currentHoverIndex = newHover
                                presdEndeks = currentHoverIndex
                                change.consume()
                            }
                        } finally {
                            if (hasMuvd) {
                                disconnectedArmedIndex = null
                                applyDragEnd(draggingIndex, currentHoverIndex, true)
                            }
                            draggingIndex = null; currentHoverIndex = null; dragOffset = Offset.Zero; isCopyDragActive = false
                            presdEndeks = null
                        }
                        return@awaitEachGesture
                    }

                    val isMoveDrag = when (copyDragPolicy) {
                        CopyDragPolicy.AlwaysCopy -> false; CopyDragPolicy.NeverCopy -> true; CopyDragPolicy.TwoStepArmed -> (lastSameSpotIndex != downIdx)
                    }
                    draggingIndex = downIdx; isCopyDragActive = !isMoveDrag; dragOffset = start; currentHoverIndex = downIdx
                    presdEndeks = downIdx
                    try {
                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Main); val change = event.changes.find { it.id == down.id } ?: break
                            if (change.changedToUpIgnoreConsumed()) break
                            val delta = change.positionChange(); dragOffset = Offset(dragOffset.x + delta.x, dragOffset.y + delta.y)
                            currentHoverIndex = getHexIndexFromPosition(
                                        dragOffset.x, dragOffset.y, wPx, hPx, allHexPositionsPx, hexSizePx
                                    )
                            presdEndeks = currentHoverIndex
                            change.consume()
                        }
                    } finally {
                        applyDragEnd(draggingIndex, currentHoverIndex, isMoveDrag)
                        draggingIndex = null; currentHoverIndex = null; dragOffset = Offset.Zero; isCopyDragActive = false
                        presdEndeks = null
                    }
                } finally {
                    glowenqEndeks = null
                    presdEndeks = null
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
