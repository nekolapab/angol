package modyilz

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sirvesez.EnpitSirves
import yuteledez.HeksagonDjeyometre
import wedjets.HeksagonWedjet
import wedjets.EnirRenqWedjet
import wedjets.AwdirRenqWedjet
import modalz.KepadKonfeg
import modalz.HexagonPosition
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Android implementation of the Angol Hexagonal Keypad.
 * Used for Android keyboard extensions (IME).
 */
@Composable
actual fun KepadModyil(
    onHexKeyPress: (String, Boolean, String?) -> Unit,
    isKeypadVisible: Boolean,
    displayLength: Int
) {
    if (!isKeypadVisible) return

    val enpitSirves = remember { EnpitSirves() }
    val isLetterMode by enpitSirves.isLetterMode.collectAsState()
    var isCenterHexPressed by remember { mutableStateOf(false) } // Kept for API compatibility, but effectively driven by hoveredHexIndex now
    var hoveredHexIndex by remember { mutableStateOf<Int?>(null) }
    val scope = rememberCoroutineScope()

    // Constrain the height for IME usage.
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp), // Set a fixed height for the keyboard
        contentAlignment = Alignment.Center
    ) {
        val maxWidthPx = constraints.maxWidth.toFloat()
        val maxHeightPx = constraints.maxHeight.toFloat()

        val minDimension = minOf(maxWidthPx, maxHeightPx)
        val divisor = if (minDimension < 500) {
            10.0 // Smaller screens (WearOS)
        } else {
            8.0 // Larger screens (Phone)
        }

        val hexSize = minDimension / divisor
        val geometry = HeksagonDjeyometre(
            hexSize = hexSize.toDouble(),
            center = HexagonPosition(x = 0.0, y = 0.0),
            isLetterMode = isLetterMode
        )
        val hexWidthDp = with(LocalDensity.current) { geometry.hexWidth.toFloat().toDp() }

        // Pre-calculate all hexagon positions: Inner (0-5), Outer (6-17), Center (18)
        val allHexPositions = remember(geometry) {
            val inner = geometry.getInnerRingCoordinates().map { geometry.axialToPixel(it.q, it.r) }
            val outer = geometry.getOuterRingCoordinates().map { geometry.axialToPixel(it.q, it.r) }
            inner + outer + listOf(HexagonPosition(0.0, 0.0))
        }

        // Helper for strict Pointy-Top Hexagon Hit Testing
        fun isPointInHexagon(px: Float, py: Float, hexCenter: HexagonPosition, size: Double): Boolean {
            val dx = kotlin.math.abs(px - hexCenter.x)
            val dy = kotlin.math.abs(py - hexCenter.y)
            val r = size // Exact hexagon boundary
            val sqrt3 = 1.73205080757
            
            // Check vertical bounds (width)
            if (dx > r * sqrt3 / 2.0) return false
            
            // Check diagonal edge (and implicitly height)
            // Equation: dx + sqrt(3) * dy <= sqrt(3) * r
            return (dx + sqrt3 * dy) <= (sqrt3 * r)
        }

        fun getHexIndexFromPosition(offsetX: Float, offsetY: Float): Int? {
            val localX = offsetX - maxWidthPx / 2
            val localY = offsetY - maxHeightPx / 2
            
            var closestIndex: Int? = null
            var minDistSq = Double.MAX_VALUE

            for (i in allHexPositions.indices) {
                val hexPos = allHexPositions[i]
                val distSq = (localX - hexPos.x).pow(2) + (localY - hexPos.y).pow(2)
                if (distSq < minDistSq) {
                    minDistSq = distSq
                    closestIndex = i
                }
            }
            
            if (closestIndex != null) {
                val center = allHexPositions[closestIndex]
                if (isPointInHexagon(localX, localY.toFloat(), center, hexSize)) {
                    return closestIndex
                }
            }
            return null
        }

        val innerLabels = if (isLetterMode) KepadKonfeg.innerLetterMode else KepadKonfeg.innerNumberMode
        val outerLabels = if (isLetterMode) KepadKonfeg.outerTap else KepadKonfeg.outerTapNumber
        val centerLabel = if (isLetterMode) " " else "."
        val allLabels = innerLabels + outerLabels + listOf(centerLabel)

        val innerLongPressLabels = if (isLetterMode) {
            KepadKonfeg.innerLetterMode.map { if (it == "⌫") "⌫" else "" }
        } else {
            KepadKonfeg.innerLongPressNumber
        }
        val outerLongPressLabels = if (isLetterMode) KepadKonfeg.outerLongPress else KepadKonfeg.outerLongPressNumber

        // Helper to launch Long Press logic
        fun startLongPressTimer(index: Int): Job {
            return scope.launch {
                delay(400)
                if (hoveredHexIndex == index) {
                    if (index == 18) { // Center
                        val wasLetterMode = enpitSirves.isLetterMode.value
                        enpitSirves.toggleMode()
                        val charToType = if (wasLetterMode) "." else " "
                        val charToDelete = if (wasLetterMode) " " else "."
                        enpitSirves.deleteLeft()
                        enpitSirves.addCharacter(charToType)
                        onHexKeyPress(charToType, true, charToDelete)
                    } else { // Keys
                        val isInner = index < 6
                        val lpLabel = if (isInner) innerLongPressLabels[index] else outerLongPressLabels[index - 6]
                        val primaryLabel = allLabels[index]
                        
                        if (lpLabel.isNotEmpty()) {
                            if (lpLabel == "⌫") {
                                onHexKeyPress("⌫", true, null)
                            } else {
                                enpitSirves.deleteLeft()
                                enpitSirves.addCharacter(lpLabel)
                                onHexKeyPress(lpLabel, true, primaryLabel)
                            }
                        }
                    }
                }
            }
        }

        // Background gradient and Gesture Handler
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF1A1A2E),
                            Color(0xFF0F0F1E),
                            Color.Black
                        )
                    )
                )
                .pointerInput(isLetterMode) {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        val downIndex = getHexIndexFromPosition(down.position.x, down.position.y)
                        var longPressJob: Job? = null
                        
                        // --- DOWN EVENT ---
                        if (downIndex != null && downIndex < allLabels.size) {
                            hoveredHexIndex = downIndex
                            val label = allLabels[downIndex]
                            if (label.isNotEmpty()) {
                                enpitSirves.addCharacter(label)
                                onHexKeyPress(label, false, null)
                            }
                            longPressJob = startLongPressTimer(downIndex)
                        } else {
                            hoveredHexIndex = null
                        }

                        // --- DRAG LOOP ---
                        var pointer = down.id
                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull { it.id == pointer }
                            if (change == null || !change.pressed) {
                                break // Up or Cancel
                            }

                            val moveIndex = getHexIndexFromPosition(change.position.x, change.position.y)
                            if (moveIndex != hoveredHexIndex) {
                                longPressJob?.cancel() 

                                if (moveIndex != null && moveIndex < allLabels.size) {
                                    val newLabel = allLabels[moveIndex]
                                    if (newLabel.isNotEmpty()) {
                                        enpitSirves.addCharacter(newLabel)
                                        onHexKeyPress(newLabel, false, null)
                                    }
                                    // RESTART Long Press Timer for the new hexagon!
                                    longPressJob = startLongPressTimer(moveIndex)
                                }
                                
                                hoveredHexIndex = moveIndex
                            }
                        }
                        
                        // --- UP EVENT ---
                        longPressJob?.cancel()
                        hoveredHexIndex = null
                    }
                }
        )

        // Inner ring of hexagons
        EnirRenqWedjet(
            geometry = geometry,
            stackWidth = maxWidth,
            stackHeight = maxHeight
        ) {
            innerLabels.forEachIndexed { index, label ->
                val longPressLabel = innerLongPressLabels[index]
                HeksagonWedjet(
                    label = label,
                    backgroundColor = KepadKonfeg.innerRingColors[index],
                    textColor = KepadKonfeg.getComplementaryColor(KepadKonfeg.innerRingColors[index]),
                    size = hexWidthDp,
                    fontSize = (geometry.hexWidth * if (isLetterMode) 0.6 else 0.8).toFloat(),
                    verticalOffset = 0.dp,
                    isPressed = hoveredHexIndex == index,
                    onTap = null, // Handled by parent
                    onLongPress = null // Handled by parent
                )
            }
        }

        // Outer ring of hexagons
        AwdirRenqWedjet(
            geometry = geometry,
            onHexKeyPress = { _, _, _ -> }, // Ignored, handled by parent
            tapLabels = outerLabels,
            longPressLabels = outerLongPressLabels,
            enpitSirves = enpitSirves,
            stackWidth = maxWidth,
            stackHeight = maxHeight,
            pressedIndex = if (hoveredHexIndex != null && hoveredHexIndex!! in 6..17) hoveredHexIndex!! - 6 else null,
            handleGestures = false
        )

        // Center Hexagon (Toggle Mode / Space)
        val centerSecondaryLabel = if (isLetterMode) "." else null
        val centerHexColor = if (isLetterMode) Color.White else Color.Black
        val centerTextColor = if (isLetterMode) Color.Black else Color.White

        HeksagonWedjet(
            label = centerLabel,
            secondaryLabel = centerSecondaryLabel,
            backgroundColor = centerHexColor,
            textColor = centerTextColor,
            size = hexWidthDp,
            fontSize = (geometry.hexWidth * if (isLetterMode) 0.6 else 0.8).toFloat(),
            onTap = null, // Handled by parent
            onLongPress = null, // Handled by parent
            isPressed = hoveredHexIndex == 18,
            onPressedChanged = { pressed -> isCenterHexPressed = pressed }
        )
    }
}