package modyilz

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sirvesez.EnpitSirves
import yuteledez.HeksagonDjeyometre
import wedjets.HeksagonWedjet
import wedjets.EnirRenqWedjet
import wedjets.AwdirRenqWedjet
import wedjets.AwtpitTekstWedjet
import modalz.KepadKonfeg
import modalz.HexagonPosition
import kotlin.math.pow

private const val TAG = "KepadModyilCompose"

/**
 * Android implementation of the Angol Hexagonal Keypad.
 * Used for Android keyboard extensions (IME).
 */
@Composable
actual fun KepadModyil(
    onHexKeyPress: (String, Boolean, String?) -> Unit,
    isKeypadVisible: Boolean,
    displayLength: Int,
    isListening: Boolean,
    onToggleVoice: () -> Unit
) {
    if (!isKeypadVisible) return
    Log.e(TAG, "KepadModyil: isListening = $isListening")

    val enpitSirves = remember { EnpitSirves() }
    val isLetterMode by enpitSirves.isLetterMode.collectAsState()
    val inputText by enpitSirves.inputText.collectAsState()
    val hoveredHexIndex = remember { mutableStateOf<Int?>(null) }
    val gestureStartedOnVowel = remember { mutableStateOf(false) }
    var isCenterHexPressed by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Fixed height Box to ensure it docks at the bottom of the screen
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val maxWidthPx = constraints.maxWidth.toFloat()
            val maxHeightPx = constraints.maxHeight.toFloat()

            val minDimension = minOf(maxWidthPx, maxHeightPx)
            val divisor = if (minDimension < 500) 10.0 else 8.0
            val hexSize = minDimension / divisor
            val geometry = HeksagonDjeyometre(
                hexSize = hexSize.toDouble(),
                center = HexagonPosition(x = 0.0, y = 0.0),
                isLetterMode = isLetterMode
            )
            val hexWidthDp = with(LocalDensity.current) { geometry.hexWidth.toFloat().toDp() }

            val allHexPositions = remember(geometry) {
                val inner = geometry.getInnerRingCoordinates().map { geometry.axialToPixel(it.q, it.r) }
                val outer = geometry.getOuterRingCoordinates().map { geometry.axialToPixel(it.q, it.r) }
                val all = inner + outer + listOf(HexagonPosition(0.0, 0.0))
                all
            }

            fun isPointInHexagon(px: Float, py: Float, hexCenter: HexagonPosition, size: Double): Boolean {
                val dx = kotlin.math.abs(px - hexCenter.x)
                val dy = kotlin.math.abs(py - hexCenter.y)
                val r = size
                val sqrt3 = 1.73205080757
                if (dx > r * sqrt3 / 2.0) return false
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
                    if (isPointInHexagon(localX, localY, center, hexSize)) {
                        return closestIndex
                    }
                }
                return null
            }

            val innerLabels = if (isLetterMode) KepadKonfeg.innerLetterMode else KepadKonfeg.innerNumberMode
            val outerLabels = if (isLetterMode) KepadKonfeg.outerTap else KepadKonfeg.outerTapNumber
            val displayOuterLabels = if (gestureStartedOnVowel.value) KepadKonfeg.outerTapNumber else outerLabels
            val centerLabel = if (isLetterMode) " " else "."

            val innerLongPressLabels = if (isLetterMode) {
                KepadKonfeg.innerLetterMode.map { if (it == "⌫") "⌫" else "" }
            } else {
                KepadKonfeg.innerLongPressNumber
            }
            val outerLongPressLabels = if (isLetterMode) KepadKonfeg.outerLongPress else KepadKonfeg.outerLongPressNumber

            fun startLongPressTimer(index: Int): Job {
                return scope.launch {
                    delay(400)
                    if (hoveredHexIndex.value == index) {
                        if (index == 18) {
                            val wasLetterMode = enpitSirves.isLetterMode.value
                            enpitSirves.toggleMode()
                            val charToType = if (wasLetterMode) "." else " "
                            val charToDelete = if (wasLetterMode) " " else "."
                            enpitSirves.deleteLeft()
                            enpitSirves.addCharacter(charToType)
                            onHexKeyPress(charToType, true, charToDelete)
                        } else {
                            val isInner = index < 6
                            val lpLabel = if (isInner) innerLongPressLabels[index] else outerLongPressLabels[index - 6]
                            val currentAllLabels = innerLabels + displayOuterLabels + listOf(centerLabel)
                            val primaryLabel = currentAllLabels[index]
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

            // Main Background and Gesture Handler
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFF1A1A2E), Color(0xFF0F0F1E), Color.Black)
                        )
                    )
                    .pointerInput(isLetterMode) {
                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            val downIndex = getHexIndexFromPosition(down.position.x, down.position.y)
                            var longPressJob: Job? = null
                            
                            fun getCurrentAllLabels(startedOnVowel: Boolean): List<String> {
                                val inner = if (isLetterMode) KepadKonfeg.innerLetterMode else KepadKonfeg.innerNumberMode
                                val outer = if (startedOnVowel) KepadKonfeg.outerTapNumber 
                                            else if (isLetterMode) KepadKonfeg.outerTap 
                                            else KepadKonfeg.outerTapNumber
                                val center = if (isLetterMode) " " else "."
                                return inner + outer + listOf(center)
                            }

                            if (downIndex != null) {
                                hoveredHexIndex.value = downIndex
                                
                                // Toggling voice input by touching the center (index 18)
                                if (downIndex == 18) {
                                    onToggleVoice()
                                }

                                if (downIndex in 0..5) {
                                    gestureStartedOnVowel.value = isLetterMode && downIndex in 0..4
                                } else {
                                    gestureStartedOnVowel.value = false
                                }

                                val currentLabels = getCurrentAllLabels(gestureStartedOnVowel.value)
                                if (downIndex < currentLabels.size) {
                                    val label = currentLabels[downIndex]
                                    if (label.isNotEmpty()) {
                                        enpitSirves.addCharacter(label)
                                        onHexKeyPress(label, false, null)
                                    }
                                    longPressJob = startLongPressTimer(downIndex)
                                }
                            } else {
                                hoveredHexIndex.value = null
                                gestureStartedOnVowel.value = false
                            }

                            var pointer = down.id
                            while (true) {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull { it.id == pointer }
                                if (change == null || !change.pressed) break
                                val moveIndex = getHexIndexFromPosition(change.position.x, change.position.y)
                                if (moveIndex != hoveredHexIndex.value) {
                                    longPressJob?.cancel() 

                                    val oldPopupMode = gestureStartedOnVowel.value

                                    // Update popup mode if moving to a different inner ring key
                                    if (moveIndex != null && moveIndex in 0..5) {
                                        gestureStartedOnVowel.value = isLetterMode && moveIndex in 0..4
                                    } else if (moveIndex == 18) {
                                        gestureStartedOnVowel.value = false
                                    }
                                    
                                    val newPopupMode = gestureStartedOnVowel.value

                                    // Note: we don't change gestureStartedOnVowel when moving to the outer ring (6..17)
                                    // to allow selecting the popup numbers if they are currently shown.

                                    if (hoveredHexIndex.value != null) {
                                        val currentLabels = getCurrentAllLabels(oldPopupMode)
                                        if (hoveredHexIndex.value!! < currentLabels.size) {
                                            val oldLabel = currentLabels[hoveredHexIndex.value!!]
                                            if (oldLabel.isNotEmpty()) {
                                                enpitSirves.deleteCharacters(oldLabel.length)
                                                onHexKeyPress("⌫", false, oldLabel)
                                            }
                                        }
                                    }
                                    if (moveIndex != null) {
                                        val currentLabels = getCurrentAllLabels(newPopupMode)
                                        if (moveIndex < currentLabels.size) {
                                            val newLabel = currentLabels[moveIndex]
                                            if (newLabel.isNotEmpty()) {
                                                enpitSirves.addCharacter(newLabel)
                                                onHexKeyPress(newLabel, false, null)
                                            }
                                            longPressJob = startLongPressTimer(moveIndex)
                                        }
                                    }
                                    hoveredHexIndex.value = moveIndex
                                }
                            }
                            longPressJob?.cancel()
                            hoveredHexIndex.value = null
                            gestureStartedOnVowel.value = false
                        }
                    }
            )

            // Rings
            EnirRenqWedjet(
                geometry = geometry,
                stackWidth = maxWidth,
                stackHeight = maxHeight
            ) {
                innerLabels.forEachIndexed { index, label ->
                    HeksagonWedjet(
                        label = label,
                        backgroundColor = KepadKonfeg.innerRingColors[index],
                        textColor = KepadKonfeg.getComplementaryColor(KepadKonfeg.innerRingColors[index]),
                        size = hexWidthDp,
                        fontSize = (geometry.hexWidth * if (isLetterMode) 0.6 else 0.8).toFloat(),
                        isPressed = hoveredHexIndex.value == index
                    )
                }
            }

            AwdirRenqWedjet(
                geometry = geometry,
                onHexKeyPress = { _, _, _ -> },
                tapLabels = displayOuterLabels,
                longPressLabels = outerLongPressLabels,
                enpitSirves = enpitSirves,
                stackWidth = maxWidth,
                stackHeight = maxHeight,
                pressedIndex = if (hoveredHexIndex.value != null && hoveredHexIndex.value!! in 6..17) hoveredHexIndex.value!! - 6 else null,
                handleGestures = false,
                isPopup = gestureStartedOnVowel.value
            )

            // Center Hex
            val centerHexColor = if (isListening) Color.Red else (if (isLetterMode) Color.White else Color.Black)
            val centerTextColor = if (isListening) Color.White else (if (isLetterMode) Color.Black else Color.White)

            HeksagonWedjet(
                label = centerLabel,
                backgroundColor = centerHexColor,
                textColor = centerTextColor,
                size = hexWidthDp,
                fontSize = (geometry.hexWidth * if (isLetterMode) 0.6 else 0.8).toFloat(),
                isPressed = hoveredHexIndex.value == 18,
                onPressedChanged = { pressed -> isCenterHexPressed = pressed }
            )

            // --- EXISTING OUTPUT TEXT (Toggles Voice) ---
            Box(
                modifier = Modifier.size(hexWidthDp),
                contentAlignment = Alignment.Center
            ) {
                AwtpitTekstWedjet(
                    text = enpitSirves.getDisplayText(inputText, displayLength),
                    style = TextStyle(
                        color = centerTextColor,
                        fontSize = (geometry.hexWidth * 0.33).sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}
