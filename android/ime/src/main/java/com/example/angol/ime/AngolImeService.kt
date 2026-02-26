package com.example.angol.ime

import android.content.Context
import android.content.Intent
import android.inputmethodservice.InputMethodService
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.google.firebase.Firebase
import com.google.firebase.initialize
import com.google.firebase.vertexai.vertexAI
import com.google.firebase.vertexai.type.content
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import modyilz.KepadModyil

import java.io.File
import java.io.IOException


private const val TAG = "AngolImeService"
private const val CORPUS_FILE = "angol_corpus.txt"
private const val MAX_CORPUS_SIZE = 2000 // characters
private const val PREFS_NAME = "AngolImePrefs"
private const val KEY_LETTER_MODE = "isLetterMode"

class AngolImeService : InputMethodService(), LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    private var speechRecognizer: SpeechRecognizer? = null
    private var speechIntent: Intent? = null
    private var isListening by mutableStateOf(false)
    private var isLetterMode by mutableStateOf(true)
    private var isPunctuationMode by mutableStateOf(false)
    private var isAngolMode by mutableStateOf(true)
    private var audioFocusRequest: Any? = null // AudioFocusRequest on API 26+
    
    private var ignoreSelectionUpdateCount = 0
    private var lastVoiceTriggerTime = 0L
    private var wasStartedByUser = false
    private var isClosing = false
    private var lastSelStart = -1
    private var lastSelEnd = -1
    
        private fun debouncedStartVoiceInput() {
            val now = System.currentTimeMillis()
            if (now - lastVoiceTriggerTime > 1500) {
                lastVoiceTriggerTime = now
                Log.d(TAG, "debouncedStartVoiceInput: Triggering voice input")
                wasStartedByUser = true
                
                startVoiceInput()
            }
        }
    
        // Buffer to capture the current word being typed
        private var currentWordBuffer = StringBuilder()
    
        private fun addToCorpus(word: String) {
            if (word.isBlank()) return
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val file = File(filesDir, CORPUS_FILE)
                    // Append the new word
                    file.appendText("$word ")
                    
                    // Keep the file size manageable (keep the tail)
                    if (file.length() > MAX_CORPUS_SIZE) {
                        val content = file.readText()
                        val keepLength = (MAX_CORPUS_SIZE * 0.8).toInt() // Keep 80%
                        val start = content.length - keepLength
                        val trimmed = content.substring(start)
                        // Try to start at a whitespace to avoid cutting a word
                        val cleanStart = trimmed.indexOfFirst { it.isWhitespace() }
                        val finalContent = if (cleanStart != -1) trimmed.substring(cleanStart + 1) else trimmed
                        file.writeText(finalContent)
                    }
                } catch (e: IOException) {
                    Log.e(TAG, "Failed to update corpus: ${e.message}")
                }
            }
        }
    
        private suspend fun getCorpus(): String {
            return withContext(Dispatchers.IO) {
                try {
                    val file = File(filesDir, CORPUS_FILE)
                    if (file.exists()) file.readText() else ""
                } catch (e: IOException) {
                    ""
                }
            }
        }
    
            private fun toggleLetterMode() {
                isLetterMode = !isLetterMode
                // Save the new mode to SharedPreferences
                val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                prefs.edit().putBoolean(KEY_LETTER_MODE, isLetterMode).apply()
                Log.d(TAG, "toggleLetterMode: isLetterMode = $isLetterMode")
            }
        
                private fun changePunctuationMode(enabled: Boolean) {
                    isPunctuationMode = enabled
                }        
            override val lifecycle: Lifecycle            get() = lifecycleRegistry
    
        override val savedStateRegistry: SavedStateRegistry
            get() = savedStateRegistryController.savedStateRegistry
    
        override val viewModelStore = ViewModelStore()
    
        override fun onCreate() {
            super.onCreate()
            Log.d(TAG, "onCreate: IME Service created")
            
            // Load the saved mode from SharedPreferences
            val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            isLetterMode = prefs.getBoolean(KEY_LETTER_MODE, true)
            
            try {
                Firebase.initialize(this)
                Log.d(TAG, "Firebase initialized")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize Firebase: ${e.message}")
            }
    
            savedStateRegistryController.performRestore(null)
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
            
            // Attempt to attach owners to the window's decor view
            try {
                window.window?.decorView?.let {
                    it.setViewTreeLifecycleOwner(this)
                    it.setViewTreeViewModelStoreOwner(this)
                    it.setViewTreeSavedStateRegistryOwner(this)
                }
            } catch (e: Exception) {
                Log.e(TAG, "onCreate: Failed to attach lifecycle/viewmodel/savedstate owners to window decor view: ${e.message}")
            }
    
            // Initialize speech recognizer for voice input
            if (SpeechRecognizer.isRecognitionAvailable(this)) {
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this).apply {
                    setRecognitionListener(object : RecognitionListener {
                        override fun onReadyForSpeech(params: Bundle?) {
                            Log.d(TAG, "SpeechRecognizer: Ready for speech")
                            isListening = true
                        }
    
                        override fun onBeginningOfSpeech() {
                            Log.d(TAG, "SpeechRecognizer: Beginning of speech")
                        }
    
                        override fun onRmsChanged(rmsdB: Float) {}
    
                        override fun onBufferReceived(buffer: ByteArray?) {}
    
                        override fun onEndOfSpeech() {
                            Log.d(TAG, "SpeechRecognizer: End of speech")
                            val wasListening = isListening
                            isListening = false
                            abandonAudioPriority()
                            unmuteSystemStream()
                            
                            // Play quiet tone only if it was user-started and we are not closing
                            if (wasStartedByUser && wasListening && !isClosing) {
                                // REMOVED toneGenerator
                            }
                            wasStartedByUser = false
                        }
    
                        override fun onError(error: Int) {
                            val errorMessage = when (error) {
                                SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                                SpeechRecognizer.ERROR_CLIENT -> "Client side error"
                                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                                SpeechRecognizer.ERROR_NETWORK -> "Network error"
                                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                                SpeechRecognizer.ERROR_NO_MATCH -> "No match"
                                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RecognitionService busy"
                                SpeechRecognizer.ERROR_SERVER -> "Error from server"
                                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
                                else -> "Unknown error"
                            }
                            Log.e(TAG, "SpeechRecognizer error: $error ($errorMessage)")
                            android.widget.Toast.makeText(this@AngolImeService, "Error: $errorMessage", android.widget.Toast.LENGTH_SHORT).show()
                            isListening = false
                            abandonAudioPriority()
                            unmuteSystemStream()
                            wasStartedByUser = false
                        }
    
                                            override fun onResults(results: Bundle?) {
                                                isListening = false
                                                abandonAudioPriority()
                                                unmuteSystemStream()
                                                val matches = results
                                                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                                                val text = matches?.firstOrNull() ?: return
                                                
                                                // Use Gemini for intelligent conversion if Angol mode is enabled
                                                CoroutineScope(Dispatchers.Main).launch {
                                                    try {
                                                        val ic = currentInputConnection
                                                        if (ic == null) return@launch
                        
                                                        val before = ic.getTextBeforeCursor(1, 0)
                                                        val needsLeadingSpace = before != null && before.isNotEmpty()
                                                        
                                                        if (!isAngolMode) {
                                                            ignoreSelectionUpdateCount++
                                                            val commitStr = if (needsLeadingSpace) " $text" else text
                                                            ic.commitText(commitStr, 1)
                                                            addToCorpus(text)
                                                            return@launch
                                                        }
                        
                                                        val corpus = getCorpus()
                                                        val model = Firebase.vertexAI.generativeModel("gemini-1.5-flash")
                                                        val prompt = """
                                                            Convert the following English text to 'Angol' spelling. 
                                                            Angol is a phonetic spelling where 'the' is 'lha', 'to' is 'tu', 'ing' is 'enq', 'tion' is 'con', etc.
                                                            
                                                            Here are examples of my writing style (Corpus):
                                                            $corpus
                                                            
                                                            Follow the project's established spelling conventions and the style in the corpus.
                                                            Only output the converted text, no explanations or extra text.
                                                            Text: $text
                                                        """.trimIndent()
                                                        
                                                        val response = withContext(Dispatchers.IO) {
                                                            model.generateContent(content { text(prompt) })
                                                         }
                                                        val angolText = response.text?.trim() ?: yuteledez.AngolSpelenqMelxod.convertToAngolSpelling(text)
                        
                                                        // Add leading space if needed
                                                        val finalAngolText = if (needsLeadingSpace) " $angolText" else angolText
                                                        
                                                        ignoreSelectionUpdateCount++
                                                        ic.commitText(finalAngolText, 1)
                                                        // Add the committed text to corpus as well
                                                        addToCorpus(angolText)
                                                        Log.d(TAG, "Gemini conversion successful: $angolText")
                                                    } catch (e: Exception) {
                                                        Log.e(TAG, "Gemini conversion failed: ${e.message}")
                                                        // Fallback to rule-based conversion
                                                        val angolText = yuteledez.AngolSpelenqMelxod.convertToAngolSpelling(text)
                                                        val ic = currentInputConnection
                                                        if (ic != null) {
                                                            val before = ic.getTextBeforeCursor(1, 0)
                                                            val needsLeadingSpace = before != null && before.isNotEmpty()
                                                            val finalAngolText = if (needsLeadingSpace) " $angolText" else angolText
                                                            
                                                            ignoreSelectionUpdateCount++
                                                            ic.commitText(finalAngolText, 1)
                                                            addToCorpus(angolText)
                                                        }
                                                    }
                                                }
                                            }    
                        override fun onPartialResults(partialResults: Bundle?) {
                            // Optionally handle partial results. For now, ignore to avoid noisy commits.
                        }
    
                        override fun onEvent(eventType: Int, params: Bundle?) {}
                    })
                }
    
                speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(
                        RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                    )
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                }
            } else {
                Log.w(TAG, "Speech recognition not available on this device")
            }
        }
    
        private fun requestAudioPriority(): Boolean {
            val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            Log.d(TAG, "requestAudioPriority: Setting mode to MODE_IN_COMMUNICATION")
            am.mode = AudioManager.MODE_IN_COMMUNICATION
            
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val attr = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
                val request = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE)
                    .setAudioAttributes(attr)
                    .setAcceptsDelayedFocusGain(false)
                    .setOnAudioFocusChangeListener { focusChange ->
                        Log.d(TAG, "OnAudioFocusChangeListener: $focusChange")
                    }
                    .build()
                audioFocusRequest = request
                val result = am.requestAudioFocus(request)
                Log.d(TAG, "requestAudioPriority (O+): result = $result")
                result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
            } else {
                @Suppress("DEPRECATION")
                val result = am.requestAudioFocus(null, AudioManager.STREAM_VOICE_CALL, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE)
                Log.d(TAG, "requestAudioPriority (Pre-O): result = $result")
                result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
            }
        }
    
        private fun abandonAudioPriority() {
            val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            Log.d(TAG, "abandonAudioPriority: Resetting mode to MODE_NORMAL")
            am.mode = AudioManager.MODE_NORMAL
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val request = audioFocusRequest as? AudioFocusRequest
                if (request != null) {
                    am.abandonAudioFocusRequest(request)
                    audioFocusRequest = null
                }
            } else {
                @Suppress("DEPRECATION")
                am.abandonAudioFocus(null)
            }
        }
    
        private fun startVoiceInput() {
            if (isListening) return
            
            // Check for permission at runtime
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "startVoiceInput: Missing RECORD_AUDIO permission")
                    android.widget.Toast.makeText(this, "Microphone permission required", android.widget.Toast.LENGTH_SHORT).show()
                    return
                }
            }
            
            // Set isListening to true immediately to avoid multiple triggers before onReadyForSpeech
            isListening = true
    
            val recognizer = speechRecognizer
            val intent = speechIntent
            if (recognizer == null || intent == null) {
                Log.w(TAG, "startVoiceInput: SpeechRecognizer or intent is null")
                isListening = false
                return
            }
    
            // Request priority to interrupt other apps
            if (!requestAudioPriority()) {
                Log.w(TAG, "startVoiceInput: Failed to gain audio focus priority")
            }
    
            val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            // Mute system, music, notification and alarm sounds to suppress recognizer ding
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    am.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_MUTE, 0)
                    am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0)
                    am.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_MUTE, 0)
                    am.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_MUTE, 0)
                    am.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_MUTE, 0)
                } else {
                    @Suppress("DEPRECATION")
                    am.setStreamMute(AudioManager.STREAM_SYSTEM, true)
                    @Suppress("DEPRECATION")
                    am.setStreamMute(AudioManager.STREAM_MUSIC, true)
                    @Suppress("DEPRECATION")
                    am.setStreamMute(AudioManager.STREAM_NOTIFICATION, true)
                    @Suppress("DEPRECATION")
                    am.setStreamMute(AudioManager.STREAM_ALARM, true)
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to mute streams: ${e.message}")
            }
    
            // Forcefully pause/stop other media/TTS
            am.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PAUSE))
            am.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PAUSE))
    
            // Give the system a moment to apply mute before starting
            CoroutineScope(Dispatchers.Main).launch {
                delay(200) // Increased delay to 200ms
                try {
                    recognizer.startListening(intent)
                    Log.d(TAG, "startVoiceInput: Listening started")
                } catch (e: Exception) {
                    Log.e(TAG, "startVoiceInput: Failed to start listening: ${e.message}", e)
                    isListening = false
                    abandonAudioPriority()
                    unmuteSystemStream()
                }
            }
        }
    
        private fun unmuteSystemStream() {
            CoroutineScope(Dispatchers.Main).launch {
                delay(1000) // Delay unmuting to swallow the end sound
                val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        am.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_UNMUTE, 0)
                        am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, 0)
                        am.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_UNMUTE, 0)
                        am.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_UNMUTE, 0)
                        am.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_UNMUTE, 0)
                    } else {
                        @Suppress("DEPRECATION")
                        am.setStreamMute(AudioManager.STREAM_SYSTEM, false)
                        @Suppress("DEPRECATION")
                        am.setStreamMute(AudioManager.STREAM_MUSIC, false)
                        @Suppress("DEPRECATION")
                        am.setStreamMute(AudioManager.STREAM_NOTIFICATION, false)
                        @Suppress("DEPRECATION")
                        am.setStreamMute(AudioManager.STREAM_ALARM, false)
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to unmute streams: ${e.message}")
                }
            }
        }
    
        private fun stopVoiceInput() {
            val recognizer = speechRecognizer ?: return
            try {
                recognizer.stopListening()
                isListening = false
                Log.d(TAG, "stopVoiceInput: Listening stopped")
                abandonAudioPriority()
                unmuteSystemStream()
            } catch (e: Exception) {
                Log.e(TAG, "stopVoiceInput: Failed to stop listening: ${e.message}", e)
                isListening = false
                abandonAudioPriority()
                unmuteSystemStream()
            }
        }
    
        override fun onViewClicked(focusChanged: Boolean) {
            super.onViewClicked(focusChanged)
            Log.d(TAG, "onViewClicked: focusChanged = $focusChanged")
            if (!isListening) {
                debouncedStartVoiceInput()
            }
        }
    
        override fun onCreateInputView(): View {
            Log.d(TAG, "onCreateInputView: Creating input view")
            val composeView = ComposeView(this).apply {
                layoutParams = android.view.ViewGroup.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
            
            composeView.setViewTreeLifecycleOwner(this)
            composeView.setViewTreeViewModelStoreOwner(this)
            composeView.setViewTreeSavedStateRegistryOwner(this)
    
            composeView.setContent {
                Log.e(TAG, "recomposing KepadModyil, isListening: $isListening")
                KepadModyil(
                    isKeypadVisible = true,
                    displayLength = 7,
                    isListening = isListening,
                    isLetterMode = isLetterMode,
                    isPunctuationMode = isPunctuationMode,
                    isAngolMode = isAngolMode,
                    displayText = currentWordBuffer.toString(),
                    onToggleVoice = {
                        if (isListening) {
                            stopVoiceInput()
                        } else {
                            wasStartedByUser = true
                            startVoiceInput()
                        }
                    },
                                                    onToggleMode = {
                                                        toggleLetterMode()
                                                    },
                                                    onSetPunctuationMode = {
                                                        changePunctuationMode(it)
                                                    },
                                                    onToggleAngol = {                        isAngolMode = !isAngolMode
                        Log.d(TAG, "onToggleAngol: isAngolMode = $isAngolMode")
                    },
                    onHexKeyPress = { char, isLongPress, primaryChar ->
                        val ic = currentInputConnection
                        if (ic == null) {
                            Log.e(TAG, "onHexKeyPress: currentInputConnection is NULL")
                            return@KepadModyil
                        }
                        
                        Log.d(TAG, "onHexKeyPress: char='$char', isLongPress=$isLongPress")
    
                        if (char == "TRANSLATE") {
                            Log.d(TAG, "TRANSLATE command received")
                            val before = ic.getTextBeforeCursor(1000, 0) ?: ""
                            val after = ic.getTextAfterCursor(1000, 0) ?: ""
                            val fullText = "$before$after"
                            
                            // Show immediate feedback regardless of text presence to confirm gesture works
                            android.widget.Toast.makeText(this@AngolImeService, "angol...", android.widget.Toast.LENGTH_SHORT).show()
                            
                            if (fullText.isNotBlank()) {
                                CoroutineScope(Dispatchers.Main).launch {
                                    try {
                                        val model = Firebase.vertexAI.generativeModel("gemini-1.5-flash")
                                        val prompt = """
                                            Convert the following text between 'Angol' spelling and standard English. 
                                            If the text is in standard English, convert it to Angol spelling.
                                            If the text is in Angol spelling, convert it to standard English.
                                            
                                            Angol is a phonetic spelling where 'the' is 'lha', 'to' is 'tu', 'ing' is 'enq', 'tion' is 'con', etc.
                                            Only output the converted text, no explanations or extra text.
                                            Text: $fullText
                                        """.trimIndent()
    
                                        val response = withContext(Dispatchers.IO) {
                                            model.generateContent(content { text(prompt) })
                                        }
                                        
                                        val convertedText = response.text?.trim()
                                        if (convertedText != null) {
                                            ic.beginBatchEdit()
                                            ignoreSelectionUpdateCount++
                                            ic.deleteSurroundingText(before.length, after.length)
                                            ignoreSelectionUpdateCount++
                                            ic.commitText(convertedText, 1)
                                            ic.endBatchEdit()
                                        }
                                    } catch (e: Exception) {
                                        Log.e(TAG, "Keyboard translation failed: ${e.message}")
                                    }
                                }
                            }
                            return@KepadModyil
                        }
    
                        if (char == "\n") {
                            if (isLongPress && primaryChar != null) {
                                ignoreSelectionUpdateCount++
                                ic.deleteSurroundingText(primaryChar.length, 0)
                            }
                            // Use both commitText and sendKeyEvent for maximum compatibility
                            ignoreSelectionUpdateCount++
                            ic.commitText("\n", 1)
                            ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
                            ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER))
                            
                            if (currentWordBuffer.isNotEmpty()) {
                                addToCorpus(currentWordBuffer.toString())
                                currentWordBuffer.clear()
                            }
                            addToCorpus("\n")
                            return@KepadModyil
                        }
    
                        if (char == "⌫") {
                            if (primaryChar != null && primaryChar.isNotEmpty()) {
                                if (currentWordBuffer.length >= primaryChar.length) {
                                    currentWordBuffer.delete(currentWordBuffer.length - primaryChar.length, currentWordBuffer.length)
                                } else {
                                    currentWordBuffer.clear()
                                }
                            } else if (currentWordBuffer.isNotEmpty()) {
                                currentWordBuffer.deleteCharAt(currentWordBuffer.length - 1)
                            }
    
                                                    if (isLongPress) {
                                                        ignoreSelectionUpdateCount++
                                                        val textBefore = ic.getTextBeforeCursor(100, 0) ?: ""
                                                        val deleteCount = calculateDeleteCount(textBefore.toString())
                                                        ic.deleteSurroundingText(deleteCount, 0)
                                                        currentWordBuffer.clear()
                                                    } else {
                                                        if (primaryChar != null && primaryChar.isNotEmpty()) {
                                                            ignoreSelectionUpdateCount++
                                                            ic.deleteSurroundingText(primaryChar.length, 0)
                                                        } else {
                                                            ignoreSelectionUpdateCount++
                                                            ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                                                            ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL))
                                                        }
                                                    }
                                                    // Launch a coroutine to check if the input is empty after the delete operation
                                                    CoroutineScope(Dispatchers.Main).launch {
                                                        delay(100) // A short delay to allow the input connection to update
                                                        if (isInputEmpty() && !isListening) {
                                                            debouncedStartVoiceInput()
                                                        }
                                                    }
                                                    return@KepadModyil
                                                }    
                        // Don't commit characters for the center hex if it was just a voice toggle
                        if (char == " " || char == ".") {
                            // Save the current word to corpus
                            if (currentWordBuffer.isNotEmpty()) {
                                addToCorpus(currentWordBuffer.toString())
                                currentWordBuffer.clear()
                            }
                        } else {
                            // Append to buffer
                            currentWordBuffer.append(char)
                        }
    
                        if (isLongPress) {
                            if (primaryChar != null) {
                                if (currentWordBuffer.length >= primaryChar.length) {
                                    currentWordBuffer.delete(currentWordBuffer.length - primaryChar.length, currentWordBuffer.length)
                                }
                                ignoreSelectionUpdateCount += 2
                                ic.deleteSurroundingText(primaryChar.length, 0)
                                ic.commitText(char, 1)
                            } else {
                                if (currentWordBuffer.isNotEmpty()) {
                                    currentWordBuffer.deleteCharAt(currentWordBuffer.length - 1)
                                }
                                ignoreSelectionUpdateCount += 2
                                ic.deleteSurroundingText(1, 0)
                                ic.commitText(char, 1)
                            }
                        } else {
                            ignoreSelectionUpdateCount++
                            ic.commitText(char, 1)
                        }
                    }
                )
            }
            return composeView
        }
    
        override fun onEvaluateInputViewShown(): Boolean {
            super.onEvaluateInputViewShown()
            return true // Force the input view to be shown
        }
    
        override fun onEvaluateFullscreenMode(): Boolean {
            return false // Disable fullscreen mode to keep the app visible above
        }
    
        override fun onUpdateSelection(
            oldSelStart: Int,
            oldSelEnd: Int,
            newSelStart: Int,
            newSelEnd: Int,
            candidatesStart: Int,
            candidatesEnd: Int
        ) {
            super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesStart, candidatesEnd)
            
            if (ignoreSelectionUpdateCount > 0) {
                ignoreSelectionUpdateCount--
                lastSelStart = newSelStart
                lastSelEnd = newSelEnd
                return
            }
    
            // Restore: Trigger voice input on tap (selection change) if cursor is not highlighting text.
            if (isInputViewShown && (newSelStart != oldSelStart || newSelEnd != oldSelEnd)) {
                if (newSelStart == newSelEnd && !isListening) {
                    debouncedStartVoiceInput()
                }
            }
            
            lastSelStart = newSelStart
            lastSelEnd = newSelEnd
        }
    
        override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
            isClosing = false
            super.onStartInputView(info, restarting)
            Log.d(TAG, "onStartInputView: Input view started")
            
            // Auto-activate STT if starting with no text
            if (isInputEmpty() && !isListening) {
                debouncedStartVoiceInput()
            }
        }
    
        override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
            isClosing = false
            super.onStartInput(attribute, restarting)
            Log.d(TAG, "onStartInput: Input started")
        }
    
        override fun onFinishInput() {
            isClosing = true
            super.onFinishInput()
            Log.d(TAG, "onFinishInput: Input finished")
            // Hide your UI if it's visible
            stopVoiceInput()
        }
    
        override fun onWindowShown() {
            isClosing = false
            super.onWindowShown()
            Log.d(TAG, "onWindowShown: IME window shown")
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        }
    
        override fun onWindowHidden() {
            isClosing = true
            super.onWindowHidden()
            Log.d(TAG, "onWindowHidden: IME window hidden")
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        }
    
        private fun isInputEmpty(): Boolean {
            val ic = currentInputConnection ?: return false
            val before = ic.getTextBeforeCursor(1, 0)
            val after = ic.getTextAfterCursor(1, 0)
            return (before == null || before.isEmpty()) && (after == null || after.isEmpty())
        }
    
        private fun calculateDeleteCount(text: String): Int {
            if (text.isEmpty()) return 0
            
            // Rule: If no spaces (one long word)
            if (!text.contains(" ")) {
                return if (text.length > 10) 12 else text.length
            }
    
            // Rule: If spaces exist
            // 1. Capture trailing whitespace
            var i = text.length - 1
            var count = 0
            while (i >= 0 && text[i].isWhitespace()) {
                count++
                i--
            }
    
            // 2. Find words backwards
            var wordsFound = 0
            while (i >= 0) {
                // Find start of word
                val wordEnd = i
                while (i >= 0 && !text[i].isWhitespace()) {
                    i--
                }
                val wordStart = i + 1
                val wordLen = wordEnd - wordStart + 1
                
                if (wordLen >= 5) {
                    // If it's a "big" word, delete it (and any trailing spaces we already counted)
                    // If it's the first word we found, just delete it.
                    if (wordsFound == 0) {
                        count += wordLen
                    }
                    break 
                } else {
                    // Small word (< 5), combine it
                    count += wordLen
                    wordsFound++
                    
                    // Also consume preceding spaces for this word
                    while (i >= 0 && text[i].isWhitespace()) {
                        count++
                        i--
                    }
                }
            }
            
            return if (count > 0) count else 1
        }
    
        override fun onDestroy() {
            isClosing = true
            super.onDestroy()
            Log.d(TAG, "onDestroy: IME Service destroyed")
            // Save the current mode to SharedPreferences
            val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putBoolean(KEY_LETTER_MODE, isLetterMode).apply()
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            viewModelStore.clear()
            speechRecognizer?.destroy()
            speechRecognizer = null
            speechIntent = null
        }
    }
    

