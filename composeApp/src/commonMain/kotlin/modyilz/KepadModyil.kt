package modyilz

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import yuteledez.HeksagonDjeyometre
import yuteledez.AngolSpelenqMelxod
import wedjets.HeksagonWedjet
import wedjets.EnirRenqWedjet
import wedjets.AwdirRenqWedjet
import modalz.KepadKonfeg
import modalz.HeksagonPozecon
import kotlin.math.pow
import kotlin.math.sqrt

private const val TAG = "KepadModyilCompose"

/**
 * Multiplatform implementation of the Angol Hexagonal Keypad.
 */
@Composable
fun KepadModyil(
    keyboardController: KeyboardController?,
    platformServices: PlatformServices,
    voiceService: VoiceService,
    isLetterMode: Boolean,
    isPunctuationMode: Boolean,
    onToggleMode: () -> Unit,
    onSetPunctuationMode: (Boolean) -> Unit,
    isAngolMode: Boolean,
    onToggleAngol: () -> Unit,
    onStartAiVoice: () -> Unit,
    ignoreSelectionUpdate: () -> Unit,
    geometryOverride: HeksagonDjeyometre? = null
) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    
    // Ensure we always use the latest values from parent
    val currentIsLetterMode by rememberUpdatedState(isLetterMode)
    val currentIsPunctuationMode by rememberUpdatedState(isPunctuationMode)
    val currentOnToggleMode by rememberUpdatedState(onToggleMode)
    
    // Buffer to capture the current word being typed
    val currentWordBuffer = remember { StringBuilder() }
    var displayText by remember { mutableStateOf("") }

    val hoveredHexIndex = remember { mutableStateOf<Int?>(null) }
    val longPressJob = remember { mutableStateOf<Job?>(null) }
    val gestureStartedOnVowelIndex = remember { mutableStateOf<Int?>(null) }
    val isCapitalized = remember { mutableStateOf(false) }
    val isCenterTranslateActive = remember { mutableStateOf(false) }
    val initialY = remember { mutableStateOf(0f) }
    val longPressStartOffset = remember { mutableStateOf<androidx.compose.ui.geometry.Offset?>(null) }
    var isCenterHexPressed by remember { mutableStateOf(false) }

    fun calculateDeleteCount(text: String): Int {
        if (text.isEmpty()) return 0
        if (!text.contains(" ")) return if (text.length > 10) 12 else text.length
        var i = text.length - 1
        var count = 0
        while (i >= 0 && text[i].isWhitespace()) {
            count++; i--
        }
        var wordsFound = 0
        while (i >= 0) {
            val wordEnd = i
            while (i >= 0 && !text[i].isWhitespace()) i--
            val wordStart = i + 1
            val wordLen = wordEnd - wordStart + 1
            if (wordLen >= 5) {
                if (wordsFound == 0) count += wordLen
                break
            } else {
                count += wordLen; wordsFound++
                while (i >= 0 && text[i].isWhitespace()) {
                    count++; i--
                }
            }
        }
        return if (count > 0) count else 1
    }

    fun handleKeyPress(char: String, isLongPress: Boolean, primaryChar: String?) {
        if (char == "TRANSLATE") {
            platformServices.log(TAG, "handleKeyPress: TRANSLATE triggered")
            onStartAiVoice()
            return
        }

        val controller = keyboardController ?: return
        platformServices.log(TAG, "handleKeyPress: char='$char', isLongPress=$isLongPress")

        if (char == "\n") {
            if (isLongPress && primaryChar != null) {
                ignoreSelectionUpdate()
                controller.deleteSurroundingText(primaryChar.length, 0)
            }
            ignoreSelectionUpdate()
            controller.commitText("\n")
            if (currentWordBuffer.isNotEmpty()) {
                platformServices.addToCorpus(currentWordBuffer.toString())
                currentWordBuffer.setLength(0)
                displayText = ""
            }
            platformServices.addToCorpus("\n")
            return
        }

        if (char == "⌫") {
            if (primaryChar != null && primaryChar.isNotEmpty()) {
                if (currentWordBuffer.length >= primaryChar.length) {
                    currentWordBuffer.delete(currentWordBuffer.length - primaryChar.length, currentWordBuffer.length)
                } else {
                    currentWordBuffer.setLength(0)
                }
            } else if (currentWordBuffer.isNotEmpty()) {
                currentWordBuffer.deleteCharAt(currentWordBuffer.length - 1)
            }
            displayText = currentWordBuffer.toString()

            if (isLongPress) {
                ignoreSelectionUpdate()
                val textBefore = controller.getTextBeforeCursor(100) ?: ""
                val deleteCount = calculateDeleteCount(textBefore.toString())
                controller.deleteSurroundingText(deleteCount, 0)
                currentWordBuffer.setLength(0)
                displayText = ""
            } else {
                if (primaryChar != null && primaryChar.isNotEmpty()) {
                    ignoreSelectionUpdate()
                    controller.deleteSurroundingText(primaryChar.length, 0)
                } else {
                    ignoreSelectionUpdate()
                    controller.deleteSurroundingText(1, 0) // Basic backspace
                }
            }
            return
        }

        if (char == " " || char == ".") {
            if (currentWordBuffer.isNotEmpty()) {
                platformServices.addToCorpus(currentWordBuffer.toString())
                currentWordBuffer.setLength(0)
                displayText = ""
            }
        } else {
            currentWordBuffer.append(char)
            displayText = currentWordBuffer.toString()
        }

        if (isLongPress) {
            if (primaryChar != null) {
                if (currentWordBuffer.length >= primaryChar.length) currentWordBuffer.delete(currentWordBuffer.length - primaryChar.length, currentWordBuffer.length)
                ignoreSelectionUpdate()
                controller.deleteSurroundingText(primaryChar.length, 0)
                ignoreSelectionUpdate()
                controller.commitText(char)
            } else {
                if (currentWordBuffer.isNotEmpty()) currentWordBuffer.deleteCharAt(currentWordBuffer.length - 1)
                ignoreSelectionUpdate()
                controller.deleteSurroundingText(1, 0)
                ignoreSelectionUpdate()
                controller.commitText(char)
            }
            displayText = currentWordBuffer.toString()
        } else {
            ignoreSelectionUpdate()
            controller.commitText(char)
        }
    }

    fun getHexIndexFromPosition(offsetX: Float, offsetY: Float, w: Float, h: Float, allHexPositions: List<HeksagonPozecon>, hexSize: Double): Int? {
        val localX = offsetX - w / 2
        val localY = offsetY - h / 2
        var closestIndex: Int? = null
        var minDistSq = Double.MAX_VALUE
        for (i in allHexPositions.indices) {
            val hexPos = allHexPositions[i]
            val distSq = (localX - hexPos.x).pow(2) + (localY - hexPos.y).pow(2)
            if (distSq < minDistSq) {
                minDistSq = distSq; closestIndex = i
            }
        }
        if (closestIndex != null) {
            val center = allHexPositions[closestIndex]
            val dx = kotlin.math.abs(localX - center.x)
            val dy = kotlin.math.abs(localY - center.y)
            val sqrt3Val = 1.73205080757
            if (dx > hexSize * sqrt3Val / 2.0) return null
            if ((dx + sqrt3Val * dy) <= (sqrt3Val * hexSize)) return closestIndex
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
        } else if (currentIsLetterMode) KepadKonfeg.outerTap else KepadKonfeg.outerTapNumber
        val center = if (currentIsLetterMode) " " else "."
        val labels = inner + outer + listOf(center)
        return if (isCapitalized.value) labels.map { it.uppercase() } else labels
    }

    fun startLongPressTimer(index: Int): Job {
        return scope.launch {
            delay(500)
            if (hoveredHexIndex.value != index) return@launch
            if (index == 18) {
                if (currentIsLetterMode) onSetPunctuationMode(true)
                val oldChar = if (currentIsLetterMode) " " else "."
                currentOnToggleMode()
                delay(50)
                val newChar = if (currentIsLetterMode) " " else "."
                handleKeyPress("⌫", false, oldChar)
                handleKeyPress(newChar, false, null)
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
                    handleKeyPress("⌫", true, null)
                    delay(500)
                }
            } else { handleKeyPress(lpLabel, true, primaryLabel) }
        }
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        contentAlignment = Alignment.TopCenter
    ) {
        val maxWidthDp = maxWidth
        
        val geometry = remember(maxWidthDp, geometryOverride) {
            if (geometryOverride != null) {
                geometryOverride
            } else {
                // Perfect fit matching DaylSkren: 5 hexagons across
                val hexWidth = maxWidthDp.value / 5.0
                val hexSize = hexWidth / sqrt(3.0)
                HeksagonDjeyometre(heksSayz = hexSize, sentir = HeksagonPozecon(x = 0.0, y = 0.0), ezLeterMod = true)
            }
        }
        
        val allHexPositions = remember(geometry) {
            val inner = geometry.getEnirRenqKowordenats().map { geometry.aksyalTuPeksel(it.q, it.r) }
            val outer = geometry.getAwdirRenqKowordenats().map { geometry.aksyalTuPeksel(it.q, it.r) }
            inner + outer + listOf(HeksagonPozecon(0.0, 0.0))
        }

        // Height to fit 4 hexagons (8 * radius)
        val gridHeightDp = (geometry.heksSayz * 8.0).dp

        Box(modifier = Modifier.fillMaxWidth().height(gridHeightDp), contentAlignment = Alignment.Center) {
            val hexWidthDp = geometry.heksWidlx.dp
            
            val currentLabels = getCurrentAllLabels(gestureStartedOnVowelIndex.value)
            val displayOuterLabels = currentLabels.subList(6, 18)
            val outerLongPressLabels = if (currentIsLetterMode && gestureStartedOnVowelIndex.value == null) {
                val raw = KepadKonfeg.outerLongPress
                if (isCapitalized.value) raw.map { it.uppercase() } else raw
            } else emptyList()

            Box(
                modifier = Modifier.fillMaxSize()
                    .pointerInput(Unit) {
                        val wDp = size.width.toDp().value
                        val hDp = size.height.toDp().value
                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            val xDp = down.position.x.toDp().value
                            val yDp = down.position.y.toDp().value
                            
                            val downIndex = getHexIndexFromPosition(xDp, yDp, wDp, hDp, allHexPositions, geometry.heksSayz)
                            val gestureStartedIndex = downIndex
                            initialY.value = down.position.y
                            longPressStartOffset.value = down.position
                            isCapitalized.value = false
                            isCenterTranslateActive.value = false
                            
                            if (downIndex != null) {
                                hoveredHexIndex.value = downIndex
                                if (downIndex in 0..4 && currentIsLetterMode && !currentIsPunctuationMode) {
                                    gestureStartedOnVowelIndex.value = downIndex
                                } else {
                                    gestureStartedOnVowelIndex.value = null
                                }

                                if (downIndex == 18) {
                                    isCenterHexPressed = true
                                    voiceService.startListening()
                                }
                                
                                val downLabels = getCurrentAllLabels(gestureStartedOnVowelIndex.value)
                                if (downIndex < downLabels.size && downLabels[downIndex].isNotEmpty()) {
                                    handleKeyPress(downLabels[downIndex], false, null)
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
                                val upThreshold = with(density) { geometry.heksSayz.dp.toPx() } * 0.4f
                                val downThreshold = with(density) { geometry.heksSayz.dp.toPx() } * 0.2f
                                
                                if (!isCenterTranslateActive.value && !isCapitalized.value && dy < -upThreshold) {
                                    if (gestureStartedIndex != 18) {
                                        isCapitalized.value = true
                                        hoveredHexIndex.value?.let { idx ->
                                            val upLabels = getCurrentAllLabels(gestureStartedOnVowelIndex.value)
                                            if (idx < upLabels.size) {
                                                val old = upLabels[idx].lowercase(); val new = upLabels[idx].uppercase()
                                                if (old != new) { handleKeyPress("⌫", false, old); handleKeyPress(new, false, null) }
                                            }
                                        }
                                    }
                                    longPressJob.value?.cancel()
                                } else if ((isCenterTranslateActive.value || isCapitalized.value) && dy > -downThreshold) {
                                    isCenterTranslateActive.value = false; isCapitalized.value = false
                                    if (hoveredHexIndex.value != null && gestureStartedIndex != 18) {
                                        hoveredHexIndex.value?.let { idx ->
                                            val dtLabels = getCurrentAllLabels(gestureStartedOnVowelIndex.value)
                                            if (idx < dtLabels.size) {
                                                val old = dtLabels[idx].uppercase(); val new = dtLabels[idx].lowercase()
                                                if (old != new) { handleKeyPress("⌫", false, old); handleKeyPress(new, false, null) }
                                            }
                                        }
                                    }
                                }
                                
                                val moveXDp = change.position.x.toDp().value
                                val moveYDp = change.position.y.toDp().value
                                val moveIndex = getHexIndexFromPosition(moveXDp, moveYDp, wDp, hDp, allHexPositions, geometry.heksSayz)
                                
                                if (moveIndex != hoveredHexIndex.value) {
                                    longPressJob.value?.cancel()
                                    if (gestureStartedIndex != 18) { initialY.value = change.position.y; isCapitalized.value = false }
                                    longPressStartOffset.value = change.position
                                    
                                    val oldVowelIndex = gestureStartedOnVowelIndex.value
                                    
                                    if (gestureStartedIndex == 18 && moveIndex != 18) {
                                        isCenterHexPressed = false
                                        voiceService.stopListening()
                                    }

                                    if (moveIndex != null && moveIndex in 0..4 && currentIsLetterMode && !currentIsPunctuationMode) {
                                        gestureStartedOnVowelIndex.value = moveIndex
                                    } else if (moveIndex != null && moveIndex >= 6 && oldVowelIndex != null) {
                                        // Keep vowel for fast number
                                    } else {
                                        gestureStartedOnVowelIndex.value = null
                                    }
                                    
                                    hoveredHexIndex.value?.let { idx ->
                                        val oldLabels = getCurrentAllLabels(oldVowelIndex)
                                        if (idx < oldLabels.size && oldLabels[idx].isNotEmpty()) handleKeyPress("⌫", false, oldLabels[idx])
                                    }
                                    if (moveIndex != null) {
                                        val moveLabels = getCurrentAllLabels(gestureStartedOnVowelIndex.value)
                                        if (moveIndex < moveLabels.size && moveLabels[moveIndex].isNotEmpty()) {
                                            handleKeyPress(moveLabels[moveIndex], false, null)
                                            longPressJob.value = startLongPressTimer(moveIndex)
                                        }
                                    }
                                    hoveredHexIndex.value = moveIndex
                                } else {
                                    longPressStartOffset.value?.let { start ->
                                        val dist = kotlin.math.sqrt((change.position.x - start.x).pow(2) + (change.position.y - start.y).pow(2))
                                        if (dist > with(density) { geometry.heksSayz.dp.toPx() } * 0.5) longPressJob.value?.cancel()
                                    }
                                }
                            }
                            longPressJob.value?.cancel()
                            if (gestureStartedIndex == 18) {
                                isCenterHexPressed = false
                                voiceService.stopListening()
                            }
                            if (currentIsPunctuationMode) onSetPunctuationMode(false)
                            hoveredHexIndex.value = null
                            gestureStartedOnVowelIndex.value = null
                            isCapitalized.value = false
                            isCenterTranslateActive.value = false
                            longPressStartOffset.value = null
                        }
                    }
            )

            EnirRenqWedjet(geometry = geometry, stackWidth = maxWidthDp, stackHeight = gridHeightDp) {
                val innerLabelsToDisplay = when {
                    currentIsPunctuationMode -> KepadKonfeg.innerPunctuationMode
                    currentIsLetterMode -> KepadKonfeg.innerLetterMode
                    else -> KepadKonfeg.innerNumberMode
                }
                innerLabelsToDisplay.forEachIndexed { index, label ->
                    val lpLabel = if (currentIsLetterMode) "" else KepadKonfeg.innerLongPressNumber.getOrNull(index) ?: ""
                    HeksagonWedjet(label = label, secondaryLabel = if (lpLabel.isNotEmpty() && lpLabel != "⌫") lpLabel else null, backgroundColor = KepadKonfeg.innerRingColors[index], textColor = KepadKonfeg.getComplementaryColor(KepadKonfeg.innerRingColors[index]), size = hexWidthDp, fontSize = (geometry.heksWidlx * if (currentIsLetterMode) 1.8 else 2.4).toFloat(), isPressed = hoveredHexIndex.value == index)
                }
            }
            AwdirRenqWedjet(geometry = geometry, onHexKeyPress = { _, _, _ -> }, tapLabels = displayOuterLabels, longPressLabels = outerLongPressLabels, initialLetterMode = currentIsLetterMode, stackWidth = maxWidthDp, stackHeight = gridHeightDp, pressedIndex = if (hoveredHexIndex.value != null && hoveredHexIndex.value!! in 6..17) hoveredHexIndex.value!! - 6 else null, handleGestures = false, isPopup = gestureStartedOnVowelIndex.value != null)
            val centerLabel = (if (currentIsLetterMode) " " else ".").let { if (isCapitalized.value) it.uppercase() else it }
            
            // Explicitly center the hub hexagon within the grid height
            Box(modifier = Modifier.fillMaxWidth().height(gridHeightDp), contentAlignment = Alignment.Center) {
                HeksagonWedjet(
                    label = centerLabel, 
                    backgroundColor = if (voiceService.isListening.value) Color.Red else if (currentIsLetterMode) Color.White else Color.Black, 
                    textColor = if (voiceService.isListening.value) Color.White else if (currentIsLetterMode) Color.Black else Color.White, 
                    size = hexWidthDp, 
                    fontSize = (geometry.heksWidlx * 1.8).toFloat(), 
                    isPressed = hoveredHexIndex.value == 18
                )
            }

            // Overlay Controls (Tucked into top corners)
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 0.dp)
                    .size(72.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { onToggleAngol() },
                            onLongPress = { onStartAiVoice() }
                        )
                    },
                contentAlignment = Alignment.TopStart
            ) {
                androidx.compose.material.Text(
                    text = "angol", 
                    color = if (isAngolMode) Color.White else Color.Gray.copy(alpha = 0.4f), 
                    fontSize = 12.sp, 
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
