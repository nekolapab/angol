package modyilz

import android.util.Log
import android.view.Surface
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
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
import modalz.HeksagonPozecon
import kotlin.math.pow

private const val TAG = "KepadModyilCompose"

/**
 * Android implementation of the Angol Hexagonal Keypad.
 * Used for Android keyboard extensions (IME).
 */
@Composable
fun KepadModyil(
    onHexKeyPress: (String, Boolean, String?) -> Unit,
    isKeypadVisible: Boolean,
    displayLength: Int,
    isListening: Boolean,
    isLetterMode: Boolean,
    isPunctuationMode: Boolean,
    onToggleVoice: () -> Unit,
    onToggleMode: () -> Unit,
    onSetPunctuationMode: (Boolean) -> Unit,
    isAngolMode: Boolean,
    onToggleAngol: () -> Unit,
    displayText: String
) {
    if (!isKeypadVisible) return
    Log.e(TAG, "KepadModyil: isListening = $isListening, isLetterMode = $isLetterMode")

    val currentOnHexKeyPress by rememberUpdatedState(onHexKeyPress)
    val currentOnToggleVoice by rememberUpdatedState(onToggleVoice)
    val currentOnToggleMode by rememberUpdatedState(onToggleMode)
    val currentIsLetterMode by rememberUpdatedState(isLetterMode)
    val currentIsPunctuationMode by rememberUpdatedState(isPunctuationMode)
    val currentOnSetPunctuationMode by rememberUpdatedState(onSetPunctuationMode)
    val currentIsListening by rememberUpdatedState(isListening)
    val currentIsAngolMode by rememberUpdatedState(isAngolMode)
    val currentOnToggleAngol by rememberUpdatedState(onToggleAngol)
    val currentDisplayText by rememberUpdatedState(displayText)

    val hoveredHexIndex = remember { mutableStateOf<Int?>(null) }
    val longPressJob = remember { mutableStateOf<Job?>(null) }
    val gestureStartedOnVowelIndex = remember { mutableStateOf<Int?>(null) }
    val isCapitalized = remember { mutableStateOf(false) }
    val isCenterTranslateActive = remember { mutableStateOf(false) }
    val initialY = remember { mutableStateOf(0f) }
    val longPressStartOffset = remember { mutableStateOf<androidx.compose.ui.geometry.Offset?>(null) }
    var isCenterHexPressed by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun getHexIndexFromPosition(offsetX: Float, offsetY: Float, w: Float, h: Float, allHexPositions: List<HeksagonPozecon>, hexSize: Double): Int? {
        val localX = offsetX - w / 2
        val localY = offsetY - h / 2
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
            val dx = kotlin.math.abs(localX - center.x)
            val dy = kotlin.math.abs(localY - center.y)
            val r = hexSize
            val sqrt3 = 1.73205080757
            val inHex = if (dx > r * sqrt3 / 2.0) false else (dx + sqrt3 * dy) <= (sqrt3 * r)
            if (inHex) return closestIndex
        }
        return null
    }

    fun getCurrentAllLabels(vowelIndex: Int?): List<String> {
        val inner = when {
            currentIsPunctuationMode -> KepadKonfeg.innerPunctuationMode
            currentIsLetterMode -> KepadKonfeg.innerLetterMode
            else -> KepadKonfeg.innerNumberMode
        }
        val outer = if (vowelIndex != null && currentIsLetterMode) {
            when (vowelIndex) {
                0 -> listOf("2") + List(10) { "" } + listOf("1") // a -> 1, 2
                1 -> listOf("") + listOf("3", "4", "5") + List(8) { "" } // e -> 3, 4, 5
                2 -> List(4) { "" } + listOf("6", "7") + List(6) { "" } // i -> 6, 7
                3 -> List(6) { "" } + listOf("8", "9") + List(4) { "" } // u -> 8, 9
                4 -> List(8) { "" } + listOf("0", "A", "O") + listOf("") // o -> 0, A, O
                else -> KepadKonfeg.outerTap
            }
        } else if (currentIsLetterMode) {
            KepadKonfeg.outerTap
        } else {
            KepadKonfeg.outerTapNumber
        }
        val center = if (currentIsLetterMode) " " else "."
        val labels = inner + outer + listOf(center)
        return if (isCapitalized.value) labels.map { it.uppercase() } else labels
    }

    fun startLongPressTimer(index: Int): Job {
        return scope.launch {
            delay(500)
            if (hoveredHexIndex.value != index) return@launch
            if (index == 18) {
                // Keep punctuation popup on if they are long-pressing in letter mode
                if (currentIsLetterMode) currentOnSetPunctuationMode(true)
                
                try {
                    while (hoveredHexIndex.value == 18) {
                        val oldChar = if (currentIsLetterMode) " " else "."
                        currentOnToggleMode()
                        delay(50) 
                        val newChar = if (currentIsLetterMode) " " else "."
                        currentOnHexKeyPress("⌫", false, oldChar)
                        currentOnHexKeyPress(newChar, false, null)
                        delay(1000)
                    }
                } catch (e: kotlinx.coroutines.CancellationException) {
                    // Normal cancellation, no action needed
                } catch (e: Exception) {
                    Log.e(TAG, "Error in center hex long press", e) 
                }
                return@launch
            }
            val isInner = index < 6
            val startedVowelIndex = gestureStartedOnVowelIndex.value
            val lpLabelRaw = if (isInner) {
                if (currentIsLetterMode) KepadKonfeg.innerLetterMode.map { if (it == "⌫") "⌫" else "" }.getOrNull(index) ?: ""
                else KepadKonfeg.innerLongPressNumber.getOrNull(index) ?: ""
            } else {
                if (currentIsLetterMode && startedVowelIndex == null) KepadKonfeg.outerLongPress.getOrNull(index - 6) ?: ""
                else KepadKonfeg.outerLongPressNumber.getOrNull(index - 6) ?: ""
            }
            if (lpLabelRaw.isEmpty()) return@launch
            val lpLabel = if (isCapitalized.value && lpLabelRaw != "⌫") lpLabelRaw.uppercase() else lpLabelRaw
            val primaryLabel = getCurrentAllLabels(startedVowelIndex).getOrNull(index) ?: ""
            if (lpLabel == "⌫") {
                while (hoveredHexIndex.value == index) {
                    currentOnHexKeyPress("⌫", true, null)
                    delay(500)
                }
            } else { currentOnHexKeyPress(lpLabel, true, primaryLabel) }
        }
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth().wrapContentHeight().background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        val maxWidthPx = constraints.maxWidth.toFloat()
        val maxHeightPx = if (constraints.hasBoundedHeight) constraints.maxHeight.toFloat() else 1000f
        val geometry = remember(maxWidthPx, maxHeightPx) {
            val hexSizeWidth = maxWidthPx / 8.66
            val hexSizeHeight = maxHeightPx / 8.0
            val hexSize = if (maxWidthPx > 0 && maxHeightPx > 0) minOf(hexSizeWidth, hexSizeHeight) else maxWidthPx / 8.66
            HeksagonDjeyometre(heksSayz = hexSize, sentir = HeksagonPozecon(x = 0.0, y = 0.0), ezLeterMod = true)
        }
        val allHexPositions = remember(geometry) {
            val inner = geometry.getEnirRenqKowordenats().map { geometry.aksyalTuPeksel(it.q, it.r) }
            val outer = geometry.getAwdirRenqKowordenats().map { geometry.aksyalTuPeksel(it.q, it.r) }
            inner + outer + listOf(HeksagonPozecon(0.0, 0.0))
        }

        Box(modifier = Modifier.fillMaxWidth().wrapContentHeight(), contentAlignment = Alignment.TopCenter) {
            val gridHeightDp = with(LocalDensity.current) { (geometry.heksHayt * 4.0).toFloat().toDp() }
            Box(modifier = Modifier.fillMaxWidth().height(gridHeightDp), contentAlignment = Alignment.Center) {
                val hexWidthDp = with(LocalDensity.current) { geometry.heksWidlx.toFloat().toDp() }
                val innerLabels = if (isCapitalized.value) (if (currentIsLetterMode) KepadKonfeg.innerLetterMode else KepadKonfeg.innerNumberMode).map { it.uppercase() }
                                  else (if (currentIsLetterMode) KepadKonfeg.innerLetterMode else KepadKonfeg.innerNumberMode)
                val displayOuterLabels = getCurrentAllLabels(gestureStartedOnVowelIndex.value).subList(6, 18)
                val outerLongPressLabels = if (currentIsLetterMode && gestureStartedOnVowelIndex.value == null) {
                    val raw = KepadKonfeg.outerLongPress
                    if (isCapitalized.value) raw.map { it.uppercase() } else raw
                } else emptyList()

                Box(
                    modifier = Modifier.fillMaxSize().background(Brush.radialGradient(colors = listOf(Color(0xFF1A1A2E), Color(0xFF0F0F1E), Color.Black)))
                        .pointerInput(maxWidthPx, maxHeightPx) {
                            val w = size.width.toFloat()
                            val h = size.height.toFloat()
                            awaitEachGesture {
                                val down = awaitFirstDown(requireUnconsumed = false)
                                val downIndex = getHexIndexFromPosition(down.position.x, down.position.y, w, h, allHexPositions, geometry.heksSayz)
                                val gestureStartedIndex = downIndex
                                initialY.value = down.position.y
                                longPressStartOffset.value = down.position
                                isCapitalized.value = false
                                isCenterTranslateActive.value = false
                                
                                if (downIndex != null) {
                                    hoveredHexIndex.value = downIndex
                                    if (downIndex in 0..4 && currentIsLetterMode && !currentIsPunctuationMode) gestureStartedOnVowelIndex.value = downIndex
                                    else gestureStartedOnVowelIndex.value = null

                                    if (downIndex == 18 && currentIsLetterMode) {
                                        currentOnSetPunctuationMode(true)
                                    } 
                                    
                                    val currentLabels = getCurrentAllLabels(gestureStartedOnVowelIndex.value)
                                    if (downIndex < currentLabels.size && currentLabels[downIndex].isNotEmpty()) {
                                        currentOnHexKeyPress(currentLabels[downIndex], false, null)
                                        longPressJob.value = startLongPressTimer(downIndex)
                                    }
                                } else {
                                    hoveredHexIndex.value = null
                                    gestureStartedOnVowelIndex.value = null
                                }
                                while (true) {
                                    val event = awaitPointerEvent()
                                    val change = event.changes.firstOrNull { it.pressed } ?: break
                                    val dy = change.position.y - initialY.value
                                    val upThreshold = if (gestureStartedIndex == 18) geometry.heksSayz.toFloat() * 0.15f else geometry.heksSayz.toFloat() * 0.4f
                                    val downThreshold = geometry.heksSayz.toFloat() * 0.2f
                                    if (!isCenterTranslateActive.value && !isCapitalized.value && dy < -upThreshold) {
                                        if (gestureStartedIndex == 18) {
                                            currentOnHexKeyPress("TRANSLATE", false, null)
                                            isCenterTranslateActive.value = true
                                        } else {
                                            isCapitalized.value = true
                                            hoveredHexIndex.value?.let { idx ->
                                                val labels = getCurrentAllLabels(gestureStartedOnVowelIndex.value)
                                                if (idx < labels.size) {
                                                    val old = labels[idx].lowercase(); val new = labels[idx].uppercase()
                                                    if (old != new) { currentOnHexKeyPress("⌫", false, old); currentOnHexKeyPress(new, false, null) }
                                                }
                                            }
                                        }
                                        longPressJob.value?.cancel()
                                    } else if ((isCenterTranslateActive.value || isCapitalized.value) && dy > -downThreshold) {
                                        isCenterTranslateActive.value = false; isCapitalized.value = false
                                        if (hoveredHexIndex.value != null && gestureStartedIndex != 18) {
                                            hoveredHexIndex.value?.let { idx ->
                                                val labels = getCurrentAllLabels(gestureStartedOnVowelIndex.value)
                                                if (idx < labels.size) {
                                                    val old = labels[idx].uppercase(); val new = labels[idx].lowercase()
                                                    if (old != new) { currentOnHexKeyPress("⌫", false, old); currentOnHexKeyPress(new, false, null) }
                                                }
                                            }
                                        }
                                    }
                                    val moveIndex = getHexIndexFromPosition(change.position.x, change.position.y, w, h, allHexPositions, geometry.heksSayz)
                                    if (moveIndex != hoveredHexIndex.value) {
                                        longPressJob.value?.cancel()
                                        if (gestureStartedIndex != 18) { initialY.value = change.position.y; isCapitalized.value = false }
                                        longPressStartOffset.value = change.position
                                        val oldVowelIndex = gestureStartedOnVowelIndex.value
                                        if (moveIndex != null && moveIndex in 0..4 && currentIsLetterMode && !currentIsPunctuationMode) gestureStartedOnVowelIndex.value = moveIndex
                                        else if (moveIndex != null && moveIndex >= 6 && oldVowelIndex != null) { /* keep */ }
                                        else gestureStartedOnVowelIndex.value = null
                                        hoveredHexIndex.value?.let { idx ->
                                            val labels = getCurrentAllLabels(oldVowelIndex)
                                            if (idx < labels.size && labels[idx].isNotEmpty()) currentOnHexKeyPress("⌫", false, labels[idx])
                                        }
                                        if (moveIndex != null) {
                                            val labels = getCurrentAllLabels(gestureStartedOnVowelIndex.value)
                                            if (moveIndex < labels.size && labels[moveIndex].isNotEmpty()) {
                                                currentOnHexKeyPress(labels[moveIndex], false, null)
                                                longPressJob.value = startLongPressTimer(moveIndex)
                                            }
                                        }
                                        hoveredHexIndex.value = moveIndex
                                    } else {
                                        longPressStartOffset.value?.let { start ->
                                            val dist = kotlin.math.sqrt((change.position.x - start.x).pow(2) + (change.position.y - start.y).pow(2))
                                            if (dist > geometry.heksSayz * 0.5) longPressJob.value?.cancel()
                                        }
                                    }
                                }
                                longPressJob.value?.cancel()
                                if (currentIsPunctuationMode) {
                                    currentOnSetPunctuationMode(false)
                                }
                                hoveredHexIndex.value = null; gestureStartedOnVowelIndex.value = null; isCapitalized.value = false; isCenterTranslateActive.value = false; longPressStartOffset.value = null
                            }
                        }
                )

                val maxWidthDpVal = with(LocalDensity.current) { this@BoxWithConstraints.constraints.maxWidth.toDp() }
                val maxHeightDpVal = with(LocalDensity.current) { this@BoxWithConstraints.constraints.maxHeight.toDp() }
                EnirRenqWedjet(geometry = geometry, stackWidth = maxWidthDpVal, stackHeight = maxHeightDpVal) {
                    val innerLabelsToDisplay = when {
                        currentIsPunctuationMode -> KepadKonfeg.innerPunctuationMode
                        currentIsLetterMode -> KepadKonfeg.innerLetterMode
                        else -> KepadKonfeg.innerNumberMode
                    }
                    innerLabelsToDisplay.forEachIndexed { index, label ->
                        val lpLabel = if (currentIsLetterMode) "" else KepadKonfeg.innerLongPressNumber.getOrNull(index) ?: ""
                        HeksagonWedjet(label = label, secondaryLabel = if (lpLabel.isNotEmpty() && lpLabel != "⌫") lpLabel else null, backgroundColor = KepadKonfeg.innerRingColors[index], textColor = KepadKonfeg.getComplementaryColor(KepadKonfeg.innerRingColors[index]), size = hexWidthDp, fontSize = (geometry.heksWidlx * if (currentIsLetterMode) 0.6 else 0.8).toFloat(), isPressed = hoveredHexIndex.value == index)
                    }
                }
                AwdirRenqWedjet(geometry = geometry, onHexKeyPress = { _, _, _ -> }, tapLabels = displayOuterLabels, longPressLabels = outerLongPressLabels, initialLetterMode = currentIsLetterMode, stackWidth = maxWidthDpVal, stackHeight = maxHeightDpVal, pressedIndex = if (hoveredHexIndex.value != null && hoveredHexIndex.value!! in 6..17) hoveredHexIndex.value!! - 6 else null, handleGestures = false, isPopup = gestureStartedOnVowelIndex.value != null)
                val centerLabel = (if (currentIsLetterMode) " " else ".").let { if (isCapitalized.value) it.uppercase() else it }
                HeksagonWedjet(label = centerLabel, backgroundColor = if (currentIsListening) Color.Red else if (currentIsLetterMode) Color.White else Color.Black, textColor = if (currentIsListening) Color.White else if (currentIsLetterMode) Color.Black else Color.White, size = hexWidthDp, fontSize = (geometry.heksWidlx * 0.6).toFloat(), isPressed = hoveredHexIndex.value == 18, onPressedChanged = { isCenterHexPressed = it })
            }

            Row(modifier = Modifier.fillMaxWidth().height(24.dp).background(Color.Transparent).pointerInput(Unit) { detectTapGestures { currentOnToggleVoice() } }.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                androidx.compose.material.TextButton(onClick = { currentOnToggleAngol() }, modifier = Modifier.height(24.dp), contentPadding = PaddingValues(horizontal = 4.dp)) {
                    androidx.compose.material.Text(text = "angol", color = if (currentIsAngolMode) Color.White else Color.Gray.copy(alpha = 0.25f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                androidx.compose.material.Text(text = currentDisplayText, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp, fontWeight = FontWeight.Normal, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.End)
            }
        }
    }
}
