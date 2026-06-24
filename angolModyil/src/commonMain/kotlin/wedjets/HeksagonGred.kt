package wedjets
import yuteledez.padenq

import androidx.compose.material.AlertDialog as AlirtDayalog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.ui.unit.sp
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
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

data class GredUydem(
    val index: Int,
    val label: String,
    val color: Color,
    val isFolder: Boolean = false,
    val sekondLeybil: String? = null,
    val deyda: Any? = null
)

@Composable
fun HeksagonGred(
    geometry: HeksagonDjeyometre,
    items: List<GredUydem>,
    onMove: (Int, Int) -> Unit,
    onCopyToEmpty: (Int, Int) -> Unit,
    onMuvTuSentir: (Int) -> Unit = {},
    onDropOnFoldir: (Int, Int, Boolean) -> Unit,
    onRepleys: ((Int, Int, Boolean, String?) -> Unit)? = null,
    onRotate: ((Double) -> Unit)? = null,
    modifier: Modifier = Modifier,
    sentirLeybil: String = "",
    centerColor: Color = Color.Black,
    onTap: (Int) -> Unit = {},
    onLonqPresUydem: ((Int) -> Unit)? = null,
    onDelete: (Int) -> Unit = {},
    copyDragPolicy: CopyDragPolicy = CopyDragPolicy.TwoStepArmed,
    allowSwap: Boolean = true,
    fontSizeFactor: Float = 6f / 12f,
    centerFontSizeFactor: Float = fontSizeFactor,
    ezKonsestentSayz: Boolean = false,
    centerEzKonsestentSayz: Boolean = ezKonsestentSayz,
    fixedLabelLength: Float? = null,
    glowOnHover: Boolean = true,
    hideDisconnected: Boolean = false,
    ezKepad: Boolean = false
) {
    var pendingReplaceFrom by remember { mutableStateOf<Int?>(null) }
    var pendingReplaceTo by remember { mutableStateOf<Int?>(null) }
    var showReplaceDialog by remember { mutableStateOf(false) }

    var pendingIsMoveDrag by remember { mutableStateOf(true) }
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
    var longPressedIndex by remember { mutableStateOf<Int?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        lastSameSpotIndex = null
    }

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
    
    val expandedHexPositionsPx = remember(geometry, density, items) {
        val maxIndex = items.maxOfOrNull { it.index } ?: 0
        var ringsNeeded = 1
        while (3 * ringsNeeded * (ringsNeeded + 1) < maxIndex) ringsNeeded++
        val displayRings = maxOf(2, ringsNeeded) + 1 // phantom ring for hit testing
        val posList = mutableListOf<HeksagonPozecon>()
        posList.add(geometry.sentir)
        for (r in 1..displayRings) {
            val coords = geometry.getKowordenatsForRenq(r)
            posList.addAll(coords.map { geometry.aksyalTuPeksel(it.q, it.r) })
        }
        posList.map { pos ->
            HeksagonPozecon(
                x = with(density) { pos.x.dp.toPx().toDouble() },
                y = with(density) { pos.y.dp.toPx().toDouble() }
            )
        }
    }
    
    val hexSizePx = remember(geometry, density) { with(density) { geometry.heksSayz.dp.toPx() } }

    // BFS: compute which indices are reachable from index 0 via occupied neighbours
    val konekdedEndeksez = remember(items) {
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
        if (fromIdx != null) {
            val draggedItem = items.firstOrNull { it.index == fromIdx }
            if (draggedItem?.label == "Ã¢Å’Â«") return

            // If target is null or not connectable in keypad folder mode, move back to parent screen
            if (toIdx == null || (hideDisconnected && toIdx != 0 && !geometry.getNeybirIndesiz(toIdx).any { it in konekdedEndeksez && it != fromIdx })) {
                if (!hideDisconnected) {
                    if (isMoveDrag) {
                        onMove(fromIdx, -1)
                    } else {
                        onCopyToEmpty(fromIdx, -1)
                    }
                }
                return
            }

            val targetUydem = items.firstOrNull { it.index == toIdx }
            if (targetUydem?.label == "âŒ«") return

            val sameSpot = (toIdx == fromIdx)
            if (sameSpot) {
                if (copyDragPolicy == CopyDragPolicy.TwoStepArmed) lastSameSpotIndex = fromIdx
            } else {
                if (copyDragPolicy == CopyDragPolicy.TwoStepArmed) lastSameSpotIndex = null
                if (toIdx == 0) {
                    onMuvTuSentir(fromIdx)
                    return
                }

                val ezRepleysKonflekt = draggedItem?.label == targetUydem?.label
                if (draggedItem != null && targetUydem != null && 
                    draggedItem.label.isNotEmpty() && targetUydem.label.isNotEmpty() && 
                    ezRepleysKonflekt
                ) {
                    if (onRepleys != null) {
                        pendingReplaceFrom = fromIdx
                        pendingReplaceTo = toIdx
                        pendingIsMoveDrag = isMoveDrag
                        showReplaceDialog = true
                        return
                    }
                }

                if (targetUydem?.isFolder == true) {
                    onDropOnFoldir(fromIdx, toIdx, isMoveDrag)
                } else if (isMoveDrag) {
                    if (targetUydem != null && targetUydem.label.isNotEmpty()) {
                        if (allowSwap) onMove(fromIdx, toIdx)
                    } else {
                        onMove(fromIdx, toIdx)
                    }
                } else {
                    if (toIdx != 0 && targetUydem == null) {
                        onCopyToEmpty(fromIdx, toIdx)
                    } else if (targetUydem != null && targetUydem.label.isNotEmpty()) {
                        if (onRepleys != null) {
                            pendingReplaceFrom = fromIdx
                            pendingReplaceTo = toIdx
                            pendingIsMoveDrag = isMoveDrag
                            showReplaceDialog = true
                        }
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
                if (index == 0 && item == null && sentirLeybil.isNotEmpty()) {
                    Heksagon(
                        label = sentirLeybil, backgroundColor = centerColor,
                        textColor = HeksagonKonfeg.getComplementaryColor(centerColor), size = hexWidthDp, fontSizeFactor = centerFontSizeFactor,
                        ezKonsestentSayz = centerEzKonsestentSayz,
                        fixedLabelLength = fixedLabelLength,
                        onTap = null, onLongPress = null,
                        ezGlowenq = isAnyTouchActive || glowenqEndeks == index,
                        ezPresd = presdEndeks == index,
                        rotationAngle = geometry.roteyconAngol.toFloat()
                    )
                } else if (item != null && (!hideDisconnected || index == 0 || index in konekdedEndeksez)) {
                    val isArmed = index == disconnectedArmedIndex
                    val showContrast = if (isCopyDragActive && isDragging) {
                        false // Original item stays normal while copying
                    } else if (isDragging) {
                        true // Move-drag traveling shows contrast
                    } else {
                        index == longPressedIndex
                    }

                    Heksagon(
                        label = item.label,
                        sekondLeybil = item.sekondLeybil,
                        backgroundColor = if (isArmed) item.color.copy(alpha = 0.5f) else item.color,
                        textColor = HeksagonKonfeg.getComplementaryColor(item.color).copy(alpha = if (isArmed) 0.5f else 1f),
                        size = hexWidthDp, fontSizeFactor = fontSizeFactor,
                        ezKonsestentSayz = ezKonsestentSayz,
                        fixedLabelLength = fixedLabelLength,
                        onTap = null, onLongPress = null,
                        ezPresd = showContrast || (glowOnHover && isHovered && !isDragging) || isArmed,
                        ezGlowenq = glowenqEndeks == index || (glowOnHover && isHovered && !isDragging) || isArmed,
                        modifier = if (isDragging && !isCopyDragActive) {
                            val fingerXDp = with(density) { (dragOffset.x - wPx / 2f).toDp() }
                            val fingerYDp = with(density) { (dragOffset.y - hPx / 2f).toDp() }
                            Modifier.offset(x = fingerXDp - pos.x.dp, y = fingerYDp - pos.y.dp)
                        } else Modifier,
                        rotationAngle = geometry.roteyconAngol.toFloat()
                    )
                } else if (isHovered && index > 0 && draggedItem != null) {
                    Heksagon(
                        label = draggedItem.label, 
                        backgroundColor = draggedItem.color, 
                        textColor = HeksagonKonfeg.getComplementaryColor(draggedItem.color), 
                        size = hexWidthDp,
                        fontSizeFactor = fontSizeFactor, 
                        ezKonsestentSayz = ezKonsestentSayz,
                        ezPresd = false,
                        ezGlowenq = true,
                        rotationAngle = geometry.roteyconAngol.toFloat()
                    )
                }
            }
        }

        val ghostItem = remember(items, draggingIndex) { items.firstOrNull { it.index == draggingIndex } }
        
        if (!ezKepad && !isCopyDragActive && ghostItem != null && draggingIndex != null && currentHoverIndex == null) {
            val xDp = with(density) { dragOffset.x.toDp() }; val yDp = with(density) { dragOffset.y.toDp() }
            Heksagon(
                label = ghostItem.label, backgroundColor = ghostItem.color, textColor = HeksagonKonfeg.getComplementaryColor(ghostItem.color),
                size = hexWidthDp, fontSizeFactor = fontSizeFactor, onTap = null, onLongPress = null,
                ezPresd = true,
                ezGlowenq = true,
                rotationAngle = geometry.roteyconAngol.toFloat(),
                modifier = Modifier.align(Alignment.TopStart).offset(x = xDp - (hexWidthDp / 2), y = yDp - ((hexWidthDp * (2f / kotlin.math.sqrt(3f))) / 2))
            )
        }

        if (!ezKepad && isCopyDragActive && ghostItem != null && draggingIndex != null) {
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
            AlirtDayalog(
                onDismissRequest = { showReplaceDialog = false },
                title = null,
                text = {
                    Row(
                        modifier = Modifier.fillMaxWidth().padenq(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                val from = pendingReplaceFrom
                                val to = pendingReplaceTo
                                if (from != null && to != null) onRepleys?.invoke(from, to, pendingIsMoveDrag, null)
                                showReplaceDialog = false
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("delet", fontSize = 28.sp)
                        }
                        Button(
                            onClick = {
                                val from = pendingReplaceFrom
                                val to = pendingReplaceTo
                                if (from != null && to != null) onMove?.invoke(from, to)
                                showReplaceDialog = false
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("reneym", fontSize = 28.sp)
                        }
                    }
                },
                confirmButton = {},
                dismissButton = null
            )
        }

        Box(Modifier.fillMaxSize().align(Alignment.Center).pointerInput(geometry, expandedHexPositionsPx, items, konekdedEndeksez, lastSameSpotIndex, disconnectedArmedIndex) {
            val longMs = viewConfiguration.longPressTimeoutMillis
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false)
                val start = down.position
                val downIdx = getHeksEndeksFrumPozecon(start.x, start.y, wPx, hPx, expandedHexPositionsPx, hexSizePx, geometry.roteyconAngol.toFloat())
                
                if (downIdx == null) return@awaitEachGesture
                
                val isCenterOccupied = downIdx == 0 && sentirLeybil.isNotEmpty()
                val isItemAtIdx = items.any { it.index == downIdx && it.label.isNotEmpty() }
                
                if (!isCenterOccupied && !isItemAtIdx && disconnectedArmedIndex != downIdx) {
                    return@awaitEachGesture
                }

                // Vibrate on tap-down (matches KepadModyil behaviour)
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                glowenqEndeks = downIdx
                presdEndeks = downIdx

                var isDrag = false
                var dragPointerId = down.id
                var hasMuvd = false
                val touchSlop = viewConfiguration.touchSlop
                var lastVibratedHoverIndex: Int? = downIdx
                var ezLonqPresd = false

                val longPressJob = scope.launch {
                    delay(longMs)
                    ezLonqPresd = true
                    longPressedIndex = downIdx
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }

                try {
                    var previousRotationAngle: Double? = null

                    while (true) {
                        val event = awaitPointerEvent()

                        // Rotation Gesture Logic
                        if (onRotate != null && event.changes.size == 2) {
                            val p1 = event.changes[0].position
                            val p2 = event.changes[1].position
                            
                            val center = Offset(wPx / 2f, hPx / 2f)
                            val dist1 = (p1 - center).getDistance()
                            val dist2 = (p2 - center).getDistance()
                            
                            // "must touch the dial from outer space at opposite sides"
                            if (dist1 > hexSizePx * 1.5f && dist2 > hexSizePx * 1.5f) {
                                val currentAngle = kotlin.math.atan2((p2.y - p1.y).toDouble(), (p2.x - p1.x).toDouble())
                                if (previousRotationAngle != null) {
                                    var delta = currentAngle - previousRotationAngle
                                    while (delta > kotlin.math.PI) delta -= 2 * kotlin.math.PI
                                    while (delta < -kotlin.math.PI) delta += 2 * kotlin.math.PI
                                    onRotate(delta)
                                }
                                previousRotationAngle = currentAngle
                                
                                // Consume so we don't drag
                                event.changes.forEach { it.consume() }
                                continue
                            } else {
                                previousRotationAngle = null
                            }
                        } else {
                            previousRotationAngle = null
                        }

                        val change = event.changes.find { it.id == dragPointerId } ?: break
                        if (change.changedToUpIgnoreConsumed()) {
                            if (!isDrag) {
                                val isDiskonekded = downIdx != 0 && downIdx !in konekdedEndeksez && isItemAtIdx
                                if (isDiskonekded) {
                                    if (ezLonqPresd && onLonqPresUydem != null) {
                                        onLonqPresUydem(downIdx)
                                    } else {
                                        disconnectedArmedIndex = if (disconnectedArmedIndex == downIdx) null else downIdx
                                    }
                                } else {
                                    if (ezLonqPresd && onLonqPresUydem != null) {
                                        onLonqPresUydem(downIdx)
                                    } else {
                                        onTap(downIdx)
                                    }
                                    if (copyDragPolicy == CopyDragPolicy.TwoStepArmed) lastSameSpotIndex = downIdx
                                }
                            }
                            break
                        }

                        val position = change.position
                        if (!isDrag) {
                            val distance = (position - start).getDistance()
                            if (distance > touchSlop) {
                                val itemAtIdx = items.firstOrNull { it.index == downIdx }
                                if (itemAtIdx?.label != "Ã¢Å’Â«") {
                                    isDrag = true
                                    longPressJob?.cancel()
                                    longPressedIndex = null
                                }
                            }
                        }

                        if (isDrag) {
                            val isDiskonekded = downIdx != 0 && downIdx !in konekdedEndeksez && isItemAtIdx
                            val isMoveDrag = if (isDiskonekded) {
                                true
                            } else {
                                when (copyDragPolicy) {
                                    CopyDragPolicy.AlwaysCopy -> false
                                    CopyDragPolicy.NeverCopy -> true
                                    CopyDragPolicy.TwoStepArmed -> (lastSameSpotIndex != downIdx)
                                }
                            }
                            draggingIndex = downIdx
                            isCopyDragActive = !isMoveDrag
                            dragOffset = position
                            val newHover = getHeksEndeksFrumPozecon(
                                dragOffset.x, dragOffset.y, wPx, hPx, expandedHexPositionsPx, hexSizePx, geometry.roteyconAngol.toFloat()
                            )
                            if (newHover != downIdx) {
                                hasMuvd = true
                            }
                            
                            // Vibrate only at destination for copy and move drags
                            if (newHover != null && newHover != downIdx) {
                                if (newHover != lastVibratedHoverIndex) {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    lastVibratedHoverIndex = newHover
                                }
                            } else {
                                lastVibratedHoverIndex = newHover
                            }

                            currentHoverIndex = newHover
                            presdEndeks = currentHoverIndex
                            change.consume()
                        }
                    }
                } finally {
                    longPressJob?.cancel()
                    longPressedIndex = null
                    if (isDrag) {
                        val isDiskonekded = downIdx != 0 && downIdx !in konekdedEndeksez && isItemAtIdx
                        if (isDiskonekded && hasMuvd) {
                            disconnectedArmedIndex = null
                        }
                        val isMoveDrag = if (isDiskonekded) true else {
                            when (copyDragPolicy) {
                                CopyDragPolicy.AlwaysCopy -> false
                                CopyDragPolicy.NeverCopy -> true
                                CopyDragPolicy.TwoStepArmed -> (lastSameSpotIndex != downIdx)
                            }
                        }
                        applyDragEnd(draggingIndex, currentHoverIndex, isMoveDrag)
                    }
                    draggingIndex = null
                    currentHoverIndex = null
                    dragOffset = Offset.Zero
                    isCopyDragActive = false
                }
                glowenqEndeks = null
                presdEndeks = null
            }
        })
    }
}

private fun getHeksEndeksFrumPozecon(offsetX: Float, offsetY: Float, w: Float, h: Float, allHexPositionsPx: List<HeksagonPozecon>, hexSizePx: Float, roteyconAngol: Float): Int? {
    val localX = offsetX - w / 2f; val localY = offsetY - h / 2f
    var closestIndex: Int? = null; var minDistSq = Double.MAX_VALUE
    for (i in allHexPositionsPx.indices) {
        val hexPos = allHexPositionsPx[i]; val distSq = (localX - hexPos.x).toDouble().pow(2) + (localY - hexPos.y).toDouble().pow(2)
        if (distSq < minDistSq) { minDistSq = distSq; closestIndex = i }
    }
    if (closestIndex != null) {
        val center = allHexPositionsPx[closestIndex]
        val unrotatedX = localX - center.x
        val unrotatedY = localY - center.y
        val angleRad = kotlin.math.PI / 180.0 * -roteyconAngol
        val cosA = kotlin.math.cos(angleRad).toFloat()
        val sinA = kotlin.math.sin(angleRad).toFloat()
        val rotatedX = unrotatedX * cosA - unrotatedY * sinA
        val rotatedY = unrotatedX * sinA + unrotatedY * cosA
        val dx = kotlin.math.abs(rotatedX)
        val dy = kotlin.math.abs(rotatedY)
        val sqrt3Val = sqrt(3.0).toFloat()
        if (dx > hexSizePx * sqrt3Val / 2f) return null
        if ((dx + sqrt3Val * dy) <= (sqrt3Val * hexSizePx)) return closestIndex
    }
    return null
}




