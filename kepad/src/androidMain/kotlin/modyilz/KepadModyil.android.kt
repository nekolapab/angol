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
    isLetterMode: Boolean,
    onToggleVoice: () -> Unit,
    onToggleMode: () -> Unit
) {
    if (!isKeypadVisible) return
    Log.e(TAG, "KepadModyil: isListening = $isListening, isLetterMode = $isLetterMode")

    val currentOnHexKeyPress by rememberUpdatedState(onHexKeyPress)
    val currentOnToggleVoice by rememberUpdatedState(onToggleVoice)
    val currentOnToggleMode by rememberUpdatedState(onToggleMode)
    val currentIsLetterMode by rememberUpdatedState(isLetterMode)
    val currentIsListening by rememberUpdatedState(isListening)

    val hoveredHexIndex = remember { mutableStateOf<Int?>(null) }
    val gestureStartedOnVowel = remember { mutableStateOf(false) }
    val isCapitalized = remember { mutableStateOf(false) }
    val isCenterTranslateActive = remember { mutableStateOf(false) }
    val initialY = remember { mutableStateOf(0f) }
    val longPressStartOffset = remember { mutableStateOf<androidx.compose.ui.geometry.Offset?>(null) }
    var isCenterHexPressed by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Top-level variables for layout calculations
    var maxWidthPx by remember { mutableStateOf(0f) }
    var maxHeightPx by remember { mutableStateOf(0f) }

    // Use a derived geometry for all calculations
    val geometry = remember(maxWidthPx, maxHeightPx, currentIsLetterMode) {
        val minDimension = if (maxWidthPx > 0) minOf(maxWidthPx, maxHeightPx) else 500f
        // Base hexSize on width for automatic fit (5 * sqrt(3) ~= 8.66)
        val hexSize = if (maxWidthPx > 0) maxWidthPx / 8.66 else minDimension / 8.0
        HeksagonDjeyometre(
            hexSize = hexSize.toDouble(),
            center = HexagonPosition(x = 0.0, y = 0.0),
            isLetterMode = currentIsLetterMode
        )
    }

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
            if (isPointInHexagon(localX, localY, center, geometry.hexSize)) {
                return closestIndex
            }
        }
        return null
    }

    fun getCurrentAllLabels(startedOnVowel: Boolean): List<String> {
        val inner = if (currentIsLetterMode) KepadKonfeg.innerLetterMode else KepadKonfeg.innerNumberMode
        val outer = if (startedOnVowel) KepadKonfeg.outerTapNumber 
                    else if (currentIsLetterMode) KepadKonfeg.outerTap 
                    else KepadKonfeg.outerTapNumber
        val center = if (currentIsLetterMode) " " else "."
        val labels = inner + outer + listOf(center)
        return if (isCapitalized.value) {
            labels.map { it.uppercase() }
        } else {
            labels
        }
    }

    fun startLongPressTimer(index: Int): Job {
        return scope.launch {
            // Initial long-press threshold (default Android long-press delay)
            delay(500)
            if (hoveredHexIndex.value != index) return@launch

            // Center hex: long-press is a "peek" mode toggle only
            if (index == 18) {
                // "Double time hold" - wait longer for center toggle (1000ms total)
                delay(500)
                if (hoveredHexIndex.value != index) return@launch
                
                try {
                    // Toggle mode (Shift) briefly - "peek" behavior only.
                    // Do NOT commit newline, to avoid apps treating it as DONE
                    // and collapsing/hiding the keypad.
                    currentOnToggleMode()
                    delay(1000) // Increased to 1000ms for a more deliberate feel
                    currentOnToggleMode() // Toggle back before letting go
                    
                } catch (e: Exception) {
                    Log.e(TAG, "Error in center hex long press", e)
                }
                return@launch
            }

            // Other keys (inner/outer rings)
            val isInner = index < 6
            val startedOnVowel = gestureStartedOnVowel.value

            val lpLabelRaw = if (isInner) {
                val labels = if (currentIsLetterMode) {
                    KepadKonfeg.innerLetterMode.map { if (it == "⌫") "⌫" else "" }
                } else {
                    KepadKonfeg.innerLongPressNumber
                }
                labels.getOrNull(index) ?: ""
            } else {
                val labels = if (currentIsLetterMode && !startedOnVowel) {
                    KepadKonfeg.outerLongPress
                } else {
                    KepadKonfeg.outerLongPressNumber
                }
                labels.getOrNull(index - 6) ?: ""
            }

            if (lpLabelRaw.isEmpty()) return@launch
            
            val lpLabel = if (isCapitalized.value && lpLabelRaw != "⌫") lpLabelRaw.uppercase() else lpLabelRaw
            val currentAllLabels = getCurrentAllLabels(startedOnVowel)
            val primaryLabel = currentAllLabels.getOrNull(index) ?: ""

                                            // For backspace (⌫): while the key is held down, keep
                                            // sending long-press events at a repeat interval, so
                                            // holding the key repeatedly deletes words/spaces.
                                            if (lpLabel == "⌫") {
                                                while (hoveredHexIndex.value == index) {
                                                    currentOnHexKeyPress("⌫", true, null)
                                                    // Reverted to default repeat delay
                                                    delay(500)
                                                }
                                            } else {                // Other long-press keys fire once
                currentOnHexKeyPress(lpLabel, true, primaryLabel)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        val gridHeightDp = with(LocalDensity.current) { (geometry.hexHeight * 4.0).toFloat().toDp() }
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth().height(gridHeightDp),
            contentAlignment = Alignment.Center
        ) {
            maxWidthPx = constraints.maxWidth.toFloat()
            maxHeightPx = constraints.maxHeight.toFloat()
            val hexWidthDp = with(LocalDensity.current) { geometry.hexWidth.toFloat().toDp() }

            val innerLabelsRaw = if (currentIsLetterMode) KepadKonfeg.innerLetterMode else KepadKonfeg.innerNumberMode
            val innerLabels = if (isCapitalized.value) innerLabelsRaw.map { it.uppercase() } else innerLabelsRaw
            
            val displayOuterLabelsRaw = if (gestureStartedOnVowel.value) KepadKonfeg.outerTapNumber else (if (currentIsLetterMode) KepadKonfeg.outerTap else KepadKonfeg.outerTapNumber)
            val displayOuterLabels = if (isCapitalized.value) displayOuterLabelsRaw.map { it.uppercase() } else displayOuterLabelsRaw
            
            val outerLongPressLabelsRaw = if (currentIsLetterMode && !gestureStartedOnVowel.value) KepadKonfeg.outerLongPress else KepadKonfeg.outerLongPressNumber
            val outerLongPressLabels = if (isCapitalized.value) outerLongPressLabelsRaw.map { it.uppercase() } else outerLongPressLabelsRaw

            // Main Gesture Layer
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFF1A1A2E), Color(0xFF0F0F1E), Color.Black)
                        )
                    )
                    .pointerInput(allHexPositions, maxWidthPx, maxHeightPx) {
                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            val downIndex = getHexIndexFromPosition(down.position.x, down.position.y)
                            val gestureStartedIndex = downIndex
                            initialY.value = down.position.y
                            longPressStartOffset.value = down.position
                            isCapitalized.value = false
                            isCenterTranslateActive.value = false
                            
                            var longPressJob: Job? = null
                            if (downIndex != null) {
                                hoveredHexIndex.value = downIndex
                                if (downIndex in 0..5) {
                                    gestureStartedOnVowel.value = currentIsLetterMode && downIndex in 0..4
                                } else {
                                    gestureStartedOnVowel.value = false
                                }
                                val currentLabels = getCurrentAllLabels(gestureStartedOnVowel.value)
                                if (downIndex < currentLabels.size) {
                                    val label = currentLabels[downIndex]
                                    if (label.isNotEmpty()) {
                                        currentOnHexKeyPress(label, false, null)
                                    }
                                    longPressJob = startLongPressTimer(downIndex)
                                }
                            } else {
                                currentOnToggleVoice()
                                hoveredHexIndex.value = null
                                gestureStartedOnVowel.value = false
                            }
                            var pointer = down.id
                            while (true) {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull { it.id == pointer }
                                if (change == null || !change.pressed) break
                                
                                // Detect Swipe Up/Down for Capitalization or Translation
                                val dy = change.position.y - initialY.value
                                val upThreshold = if (gestureStartedIndex == 18) geometry.hexSize.toFloat() * 0.15f else geometry.hexSize.toFloat() * 0.4f
                                val downThreshold = geometry.hexSize.toFloat() * 0.2f
                                
                                if (!isCenterTranslateActive.value && !isCapitalized.value && dy < -upThreshold) {
                                    // Swipe UP detected
                                    if (gestureStartedIndex == 18) {
                                        // Swipe UP on Center Hex -> Translate
                                        currentOnHexKeyPress("TRANSLATE", false, null)
                                        isCenterTranslateActive.value = true
                                    } else {
                                        isCapitalized.value = true
                                        // Replace current character with uppercase
                                        if (hoveredHexIndex.value != null) {
                                            val lowercaseLabels = getCurrentAllLabels(gestureStartedOnVowel.value).map { it.lowercase() }
                                            val uppercaseLabels = getCurrentAllLabels(gestureStartedOnVowel.value).map { it.uppercase() }
                                            if (hoveredHexIndex.value!! < lowercaseLabels.size) {
                                                val oldLabel = lowercaseLabels[hoveredHexIndex.value!!]
                                                val newLabel = uppercaseLabels[hoveredHexIndex.value!!]
                                                if (oldLabel != newLabel) {
                                                    currentOnHexKeyPress("⌫", false, oldLabel)
                                                    currentOnHexKeyPress(newLabel, false, null)
                                                }
                                            }
                                        }
                                    }
                                    longPressJob?.cancel() // Cancel long-press on swipe
                                } else if ((isCenterTranslateActive.value || isCapitalized.value) && dy > -downThreshold) {
                                    // Swipe DOWN detected (or returned to center)
                                    isCenterTranslateActive.value = false
                                    isCapitalized.value = false
                                    // Replace current character with lowercase
                                    if (hoveredHexIndex.value != null && gestureStartedIndex != 18) {
                                        val uppercaseLabels = getCurrentAllLabels(gestureStartedOnVowel.value).map { it.uppercase() }
                                        val lowercaseLabels = getCurrentAllLabels(gestureStartedOnVowel.value).map { it.lowercase() }
                                        if (hoveredHexIndex.value!! < uppercaseLabels.size) {
                                            val oldLabel = uppercaseLabels[hoveredHexIndex.value!!]
                                            val newLabel = uppercaseLabels[hoveredHexIndex.value!!]
                                            if (oldLabel != newLabel) {
                                                currentOnHexKeyPress("⌫", false, oldLabel)
                                                currentOnHexKeyPress(newLabel, false, null)
                                            }
                                        }
                                    }
                                }

                                val moveIndex = getHexIndexFromPosition(change.position.x, change.position.y)
                                if (moveIndex != hoveredHexIndex.value) {
                                    longPressJob?.cancel() 
                                    // Reset initialY and capitalization when moving to a new key
                                    if (gestureStartedIndex != 18) {
                                        initialY.value = change.position.y
                                        isCapitalized.value = false
                                    }
                                    longPressStartOffset.value = change.position

                                    val oldPopupMode = gestureStartedOnVowel.value
                                    if (moveIndex != null && moveIndex in 0..5) {
                                        gestureStartedOnVowel.value = currentIsLetterMode && moveIndex in 0..4
                                    } else if (moveIndex == 18) {
                                        gestureStartedOnVowel.value = false
                                    }
                                    val newPopupMode = gestureStartedOnVowel.value
                                    if (hoveredHexIndex.value != null) {
                                        val currentLabels = getCurrentAllLabels(oldPopupMode)
                                        if (hoveredHexIndex.value!! < currentLabels.size) {
                                            val oldLabel = currentLabels[hoveredHexIndex.value!!]
                                            if (oldLabel.isNotEmpty()) {
                                                currentOnHexKeyPress("⌫", false, oldLabel)
                                            }
                                        }
                                    }
                                    if (moveIndex != null) {
                                        val currentLabels = getCurrentAllLabels(newPopupMode)
                                        if (moveIndex < currentLabels.size) {
                                            val newLabel = currentLabels[moveIndex]
                                            if (newLabel.isNotEmpty()) {
                                                currentOnHexKeyPress(newLabel, false, null)
                                            }
                                            longPressJob = startLongPressTimer(moveIndex)
                                        }
                                    }
                                    hoveredHexIndex.value = moveIndex
                                } else {
                                    // Same hex, check distance for long-press cancellation
                                    val startPos = longPressStartOffset.value
                                    if (startPos != null) {
                                        val dist = kotlin.math.sqrt(
                                            (change.position.x - startPos.x).pow(2) + 
                                            (change.position.y - startPos.y).pow(2)
                                        )
                                        if (dist > geometry.hexSize * 0.5) {
                                            longPressJob?.cancel()
                                        }
                                    }
                                }
                            }
                            longPressJob?.cancel()
                            hoveredHexIndex.value = null
                            gestureStartedOnVowel.value = false
                            isCapitalized.value = false
                            isCenterTranslateActive.value = false
                            longPressStartOffset.value = null
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
                    val lpLabel = if (currentIsLetterMode) "" else KepadKonfeg.innerLongPressNumber.getOrNull(index) ?: ""
                    HeksagonWedjet(
                        label = label,
                        secondaryLabel = if (lpLabel.isNotEmpty() && lpLabel != "⌫") lpLabel else null,
                        backgroundColor = KepadKonfeg.innerRingColors[index],
                        textColor = KepadKonfeg.getComplementaryColor(KepadKonfeg.innerRingColors[index]),
                        size = hexWidthDp,
                        fontSize = (geometry.hexWidth * if (currentIsLetterMode) 0.6 else 0.8).toFloat(),
                        isPressed = hoveredHexIndex.value == index
                    )
                }
            }

            AwdirRenqWedjet(
                geometry = geometry,
                onHexKeyPress = { _, _, _ -> },
                tapLabels = displayOuterLabels,
                longPressLabels = outerLongPressLabels,
                initialLetterMode = currentIsLetterMode,
                stackWidth = maxWidth,
                stackHeight = maxHeight,
                pressedIndex = if (hoveredHexIndex.value != null && hoveredHexIndex.value!! in 6..17) hoveredHexIndex.value!! - 6 else null,
                handleGestures = false,
                isPopup = gestureStartedOnVowel.value
            )

            // Center Hex
            val centerLabel = if (isCenterTranslateActive.value) "TRANS" else ((if (currentIsLetterMode) " " else ".").let { if (isCapitalized.value) it.uppercase() else it })
            val centerHexColor = if (currentIsListening) Color.Red else (if (currentIsLetterMode) Color.White else Color.Black)
            val centerTextColor = if (currentIsListening) Color.White else (if (currentIsLetterMode) Color.Black else Color.White)

            HeksagonWedjet(
                label = centerLabel,
                backgroundColor = centerHexColor,
                textColor = centerTextColor,
                size = hexWidthDp,
                fontSize = (geometry.hexWidth * if (isCenterTranslateActive.value) 0.4 else if (currentIsLetterMode) 0.6 else 0.8).toFloat(),
                isPressed = hoveredHexIndex.value == 18,
                onPressedChanged = { pressed -> isCenterHexPressed = pressed }
            )
        }
    }
}
