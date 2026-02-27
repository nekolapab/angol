package modyilz

import android.content.Context
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.InputConnection
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.vertexai.type.content
import com.google.firebase.vertexai.vertexAI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import yuteledez.HeksagonDjeyometre
import yuteledez.AngolSpelenqMelxod
import wedjets.HeksagonWedjet
import wedjets.EnirRenqWedjet
import wedjets.AwdirRenqWedjet
import modalz.KepadKonfeg
import modalz.HeksagonPozecon
import java.io.File
import java.io.IOException
import kotlin.math.pow

private const val TAG = "KepadModyilCompose"
private const val CORPUS_FILE = "angol_corpus.txt"
private const val MAX_CORPUS_SIZE = 2000 // characters

/**
 * Android implementation of the Angol Hexagonal Keypad.
 * Used for Android keyboard extensions (IME).
 */
@Composable
fun KepadModyil(
    ic: InputConnection?,
    isListening: Boolean,
    isLetterMode: Boolean,
    isPunctuationMode: Boolean,
    onToggleVoice: () -> Unit,
    onToggleMode: () -> Unit,
    onSetPunctuationMode: (Boolean) -> Unit,
    isAngolMode: Boolean,
    onToggleAngol: () -> Unit,
    ignoreSelectionUpdate: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
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

    fun addToCorpus(word: String) {
        if (word.isBlank()) return
        scope.launch(Dispatchers.IO) {
            try {
                val file = File(context.filesDir, CORPUS_FILE)
                file.appendText("$word ")
                if (file.length() > MAX_CORPUS_SIZE) {
                    val content = file.readText()
                    val keepLength = (MAX_CORPUS_SIZE * 0.8).toInt()
                    val start = content.length - keepLength
                    val trimmed = content.substring(start)
                    val cleanStart = trimmed.indexOfFirst { it.isWhitespace() }
                    val finalContent = if (cleanStart != -1) trimmed.substring(cleanStart + 1) else trimmed
                    file.writeText(finalContent)
                }
            } catch (e: IOException) {
                Log.e(TAG, "Failed to update corpus: ${e.message}")
            }
        }
    }

    suspend fun getCorpus(): String {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(context.filesDir, CORPUS_FILE)
                if (file.exists()) file.readText() else ""
            } catch (e: IOException) {
                ""
            }
        }
    }

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
        if (ic == null) return
        Log.d(TAG, "handleKeyPress: char='$char', isLongPress=$isLongPress")

        if (char == "TRANSLATE") {
            val before = ic.getTextBeforeCursor(1000, 0) ?: ""
            val after = ic.getTextAfterCursor(1000, 0) ?: ""
            val fullText = "$before$after"
            android.widget.Toast.makeText(context, "angol...", android.widget.Toast.LENGTH_SHORT).show()
            if (fullText.isNotBlank()) {
                scope.launch {
                    try {
                        val model = Firebase.vertexAI.generativeModel("gemini-1.5-flash")
                        val prompt = """
                            Convert the following text between 'Angol' spelling and standard English. 
                            If the text is in standard English, convert it to Angol spelling.
                            If the text is in Angol spelling, convert it to standard English.
                            Only output the converted text, no explanations or extra text.
                            Text: $fullText
                        """.trimIndent()
                        val response = withContext(Dispatchers.IO) { model.generateContent(content { text(prompt) }) }
                        val convertedText = response.text?.trim()
                        if (convertedText != null) {
                            ic.beginBatchEdit()
                            ignoreSelectionUpdate()
                            ic.deleteSurroundingText(before.length, after.length)
                            ignoreSelectionUpdate()
                            ic.commitText(convertedText, 1)
                            ic.endBatchEdit()
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Keyboard translation failed: ${e.message}")
                    }
                }
            }
            return
        }

        if (char == "\n") {
            if (isLongPress && primaryChar != null) {
                ignoreSelectionUpdate()
                ic.deleteSurroundingText(primaryChar.length, 0)
            }
            ignoreSelectionUpdate()
            ic.commitText("\n", 1)
            ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
            ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER))
            if (currentWordBuffer.isNotEmpty()) {
                addToCorpus(currentWordBuffer.toString())
                currentWordBuffer.setLength(0)
                displayText = ""
            }
            addToCorpus("\n")
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
                val textBefore = ic.getTextBeforeCursor(100, 0) ?: ""
                val deleteCount = calculateDeleteCount(textBefore.toString())
                ic.deleteSurroundingText(deleteCount, 0)
                currentWordBuffer.setLength(0)
                displayText = ""
            } else {
                if (primaryChar != null && primaryChar.isNotEmpty()) {
                    ignoreSelectionUpdate()
                    ic.deleteSurroundingText(primaryChar.length, 0)
                } else {
                    ignoreSelectionUpdate()
                    ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                    ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL))
                }
            }
            return
        }

        if (char == " " || char == ".") {
            if (currentWordBuffer.isNotEmpty()) {
                addToCorpus(currentWordBuffer.toString())
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
                ignoreSelectionUpdate(); ignoreSelectionUpdate()
                ic.deleteSurroundingText(primaryChar.length, 0)
                ic.commitText(char, 1)
            } else {
                if (currentWordBuffer.isNotEmpty()) currentWordBuffer.deleteCharAt(currentWordBuffer.length - 1)
                ignoreSelectionUpdate(); ignoreSelectionUpdate()
                ic.deleteSurroundingText(1, 0)
                ic.commitText(char, 1)
            }
            displayText = currentWordBuffer.toString()
        } else {
            ignoreSelectionUpdate()
            ic.commitText(char, 1)
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
            val sqrt3 = 1.73205080757
            if (dx > hexSize * sqrt3 / 2.0) return null
            if ((dx + sqrt3 * dy) <= (sqrt3 * hexSize)) return closestIndex
        }
        return null
    }

    fun getCurrentAllLabels(vowelIndex: Int?): List<String> {
        val inner = when {
            isPunctuationMode -> KepadKonfeg.innerPunctuationMode
            isLetterMode -> KepadKonfeg.innerLetterMode
            else -> KepadKonfeg.innerNumberMode
        }
        val outer = if (vowelIndex != null && isLetterMode) {
            when (vowelIndex) {
                0 -> List(11) { "" } + listOf("1") // a -> 1
                1 -> listOf("2", "3", "4") + List(9) { "" } // e -> 2, 3, 4
                2 -> List(3) { "" } + listOf("5", "6") + List(7) { "" } // i -> 5, 6
                3 -> List(5) { "" } + listOf("7", "8", "9") + List(4) { "" } // u -> 7, 8, 9
                4 -> List(8) { "" } + listOf("0", "A", "O") + listOf("") // o -> 0, A, O
                else -> KepadKonfeg.outerTap
            }
        } else if (isLetterMode) KepadKonfeg.outerTap else KepadKonfeg.outerTapNumber
        val center = if (isLetterMode) " " else "."
        val labels = inner + outer + listOf(center)
        return if (isCapitalized.value) labels.map { it.uppercase() } else labels
    }

    fun startLongPressTimer(index: Int): Job {
        return scope.launch {
            delay(500)
            if (hoveredHexIndex.value != index) return@launch
            if (index == 18) {
                if (isLetterMode) onSetPunctuationMode(true)
                try {
                    while (hoveredHexIndex.value == 18) {
                        val oldChar = if (isLetterMode) " " else "."
                        onToggleMode()
                        delay(50)
                        val newChar = if (isLetterMode) " " else "."
                        handleKeyPress("⌫", false, oldChar)
                        handleKeyPress(newChar, false, null)
                        delay(1000)
                    }
                } catch (e: Exception) { }
                return@launch
            }
            val isInner = index < 6
            val startedVowelIndex = gestureStartedOnVowelIndex.value
            val lpLabelRaw = if (isInner) {
                if (isLetterMode) KepadKonfeg.innerLetterMode.map { if (it == "⌫") "⌫" else "" }.getOrNull(index) ?: ""
                else KepadKonfeg.innerLongPressNumber.getOrNull(index) ?: ""
            } else {
                if (isLetterMode && startedVowelIndex == null) KepadKonfeg.outerLongPress.getOrNull(index - 6) ?: ""
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

        val gridHeightDp = with(LocalDensity.current) { (geometry.heksHayt * 4.0).toFloat().toDp() }
        Box(modifier = Modifier.fillMaxWidth().height(gridHeightDp), contentAlignment = Alignment.Center) {
            val hexWidthDp = with(LocalDensity.current) { geometry.heksWidlx.toFloat().toDp() }
            val displayOuterLabels = getCurrentAllLabels(gestureStartedOnVowelIndex.value).subList(6, 18)
            val outerLongPressLabels = if (isLetterMode && gestureStartedOnVowelIndex.value == null) {
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
                                if (downIndex in 0..4 && isLetterMode && !isPunctuationMode) gestureStartedOnVowelIndex.value = downIndex
                                else gestureStartedOnVowelIndex.value = null

                                if (downIndex == 18 && isLetterMode) onSetPunctuationMode(true)
                                
                                val currentLabels = getCurrentAllLabels(gestureStartedOnVowelIndex.value)
                                if (downIndex < currentLabels.size && currentLabels[downIndex].isNotEmpty()) {
                                    if (downIndex == 18 && displayText.isEmpty()) onToggleVoice()
                                    else handleKeyPress(currentLabels[downIndex], false, null)
                                    longPressJob.value = startLongPressTimer(downIndex)
                                }
                            } else {
                                hoveredHexIndex.value = null; gestureStartedOnVowelIndex.value = null
                            }
                            while (true) {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull { it.pressed } ?: break
                                val dy = change.position.y - initialY.value
                                val upThreshold = if (gestureStartedIndex == 18) geometry.heksSayz.toFloat() * 0.15f else geometry.heksSayz.toFloat() * 0.4f
                                val downThreshold = geometry.heksSayz.toFloat() * 0.2f
                                if (!isCenterTranslateActive.value && !isCapitalized.value && dy < -upThreshold) {
                                    if (gestureStartedIndex == 18) {
                                        handleKeyPress("TRANSLATE", false, null)
                                        isCenterTranslateActive.value = true
                                    } else {
                                        isCapitalized.value = true
                                        hoveredHexIndex.value?.let { idx ->
                                            val labels = getCurrentAllLabels(gestureStartedOnVowelIndex.value)
                                            if (idx < labels.size) {
                                                val old = labels[idx].lowercase(); val new = labels[idx].uppercase()
                                                if (old != new) { handleKeyPress("⌫", false, old); handleKeyPress(new, false, null) }
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
                                                if (old != new) { handleKeyPress("⌫", false, old); handleKeyPress(new, false, null) }
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
                                    if (moveIndex != null && moveIndex in 0..4 && isLetterMode && !isPunctuationMode) gestureStartedOnVowelIndex.value = moveIndex
                                    else if (moveIndex != null && moveIndex >= 6 && oldVowelIndex != null) { }
                                    else gestureStartedOnVowelIndex.value = null
                                    hoveredHexIndex.value?.let { idx ->
                                        val labels = getCurrentAllLabels(oldVowelIndex)
                                        if (idx < labels.size && labels[idx].isNotEmpty()) handleKeyPress("⌫", false, labels[idx])
                                    }
                                    if (moveIndex != null) {
                                        val labels = getCurrentAllLabels(gestureStartedOnVowelIndex.value)
                                        if (moveIndex < labels.size && labels[moveIndex].isNotEmpty()) {
                                            handleKeyPress(labels[moveIndex], false, null)
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
                            if (isPunctuationMode) onSetPunctuationMode(false)
                            hoveredHexIndex.value = null; gestureStartedOnVowelIndex.value = null; isCapitalized.value = false; isCenterTranslateActive.value = false; longPressStartOffset.value = null
                        }
                    }
            )

            val maxWidthDpVal = with(LocalDensity.current) { maxWidthPx.toDp() }
            val maxHeightDpVal = with(LocalDensity.current) { maxHeightPx.toDp() }
            EnirRenqWedjet(geometry = geometry, stackWidth = maxWidthDpVal, stackHeight = maxHeightDpVal) {
                val innerLabelsToDisplay = when {
                    isPunctuationMode -> KepadKonfeg.innerPunctuationMode
                    isLetterMode -> KepadKonfeg.innerLetterMode
                    else -> KepadKonfeg.innerNumberMode
                }
                innerLabelsToDisplay.forEachIndexed { index, label ->
                    val lpLabel = if (isLetterMode) "" else KepadKonfeg.innerLongPressNumber.getOrNull(index) ?: ""
                    HeksagonWedjet(label = label, secondaryLabel = if (lpLabel.isNotEmpty() && lpLabel != "⌫") lpLabel else null, backgroundColor = KepadKonfeg.innerRingColors[index], textColor = KepadKonfeg.getComplementaryColor(KepadKonfeg.innerRingColors[index]), size = hexWidthDp, fontSize = (geometry.heksWidlx * if (isLetterMode) 0.6 else 0.8).toFloat(), isPressed = hoveredHexIndex.value == index)
                }
            }
            AwdirRenqWedjet(geometry = geometry, onHexKeyPress = { _, _, _ -> }, tapLabels = displayOuterLabels, longPressLabels = outerLongPressLabels, initialLetterMode = isLetterMode, stackWidth = maxWidthDpVal, stackHeight = maxHeightDpVal, pressedIndex = if (hoveredHexIndex.value != null && hoveredHexIndex.value!! in 6..17) hoveredHexIndex.value!! - 6 else null, handleGestures = false, isPopup = gestureStartedOnVowelIndex.value != null)
            val centerLabel = (if (isLetterMode) " " else ".").let { if (isCapitalized.value) it.uppercase() else it }
            HeksagonWedjet(label = centerLabel, backgroundColor = if (isListening) Color.Red else if (isLetterMode) Color.White else Color.Black, textColor = if (isListening) Color.White else if (isLetterMode) Color.Black else Color.White, size = hexWidthDp, fontSize = (geometry.heksWidlx * 0.6).toFloat(), isPressed = hoveredHexIndex.value == 18, onPressedChanged = { isCenterHexPressed = it })

            Row(
                modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter).padding(horizontal = 16.dp, vertical = 8.dp).pointerInput(Unit) { detectTapGestures { onToggleVoice() } },
                horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.material.TextButton(onClick = { onToggleAngol() }, modifier = Modifier.height(24.dp), contentPadding = PaddingValues(horizontal = 4.dp)) {
                    androidx.compose.material.Text(text = "angol", color = if (isAngolMode) Color.White else Color.Gray.copy(alpha = 0.25f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                androidx.compose.material.Text(text = displayText, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp, fontWeight = FontWeight.Normal, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.End)
            }
        }
    }
}
