package com.example.angol.ime

import android.content.Context
import android.content.Intent
import android.inputmethodservice.InputMethodService
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
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

class AngolImeService : InputMethodService(), LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    private var speechRecognizer: SpeechRecognizer? = null
    private var speechIntent: Intent? = null
    private var isListening by mutableStateOf(false)
    private var isLetterMode by mutableStateOf(true)
    private var audioFocusRequest: Any? = null // AudioFocusRequest on API 26+
    
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
        val ic = currentInputConnection ?: return
        // Send a space or dot depending on mode transition if desired, 
        // but for now let's just toggle the UI mode.
        Log.d(TAG, "toggleLetterMode: isLetterMode = $isLetterMode")
    }

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    override val viewModelStore = ViewModelStore()

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: IME Service created")
        
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
                        isListening = false
                        abandonAudioPriority()
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
                        isListening = false
                        abandonAudioPriority()
                    }

                    override fun onResults(results: Bundle?) {
                        isListening = false
                        abandonAudioPriority()
                        val matches = results
                            ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull() ?: return
                        
                        // Use Gemini for intelligent conversion
                        CoroutineScope(Dispatchers.Main).launch {
                            try {
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
                                val angolText = response.text?.trim() ?: convertToAngolSpelling(text)

                                val ic = currentInputConnection
                                if (ic != null) {
                                    ic.commitText(angolText, 1)
                                    // Add the committed text to corpus as well
                                    addToCorpus(angolText)
                                    Log.d(TAG, "Gemini conversion successful: $angolText")
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Gemini conversion failed: ${e.message}")
                                // Fallback to rule-based conversion
                                val angolText = convertToAngolSpelling(text)
                                val ic = currentInputConnection
                                if (ic != null) {
                                    ic.commitText(angolText, 1)
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

    private fun convertToAngolSpelling(text: String): String {
        // Simple rule-based converter for Angol spelling
        // These rules are derived from the project's documentation (GEMINI.md, blueprint.md)
        val words = text.split(" ")
        val convertedWords = words.map { word ->
            var lower = word.lowercase()
            
            // Basic replacements
            lower = lower.replace("the", "lha")
            lower = lower.replace("to", "tu")
            lower = lower.replace("application", "aplekeycon")
            lower = lower.replace("information", "enformeycon")
            lower = lower.replace("service", "sirves")
            lower = lower.replace("input", "enpit")
            lower = lower.replace("method", "melxod")
            lower = lower.replace("voice", "voys")
            lower = lower.replace("text", "tekst")
            lower = lower.replace("typing", "taypenq")
            lower = lower.replace("spelling", "spelenq")
            lower = lower.replace("perfect", "pirfekt")
            lower = lower.replace("work", "wirk")
            lower = lower.replace("button", "buton")
            lower = lower.replace("number", "numbir")
            lower = lower.replace("letter", "ledir")
            lower = lower.replace("center", "sentir")
            lower = lower.replace("circle", "sirkol")
            lower = lower.replace("inner", "enir")
            lower = lower.replace("outer", "awdir")
            lower = lower.replace("keyboard", "kepad")
            lower = lower.replace("this", "lhes")
            lower = lower.replace("with", "welx")
            lower = lower.replace("have", "hav")
            lower = lower.replace("been", "bin")
            lower = lower.replace("was", "waz")
            lower = lower.replace("does", "duz")
            lower = lower.replace("doesn't", "duznt")
            lower = lower.replace("nothing", "naixenq")
            lower = lower.replace("through", "lru")
            lower = lower.replace("all", "ol")
            lower = lower.replace("mode", "mod")
            
            // Rule-based phonetic adjustments (simplified)
            if (lower.endsWith("ing")) {
                lower = lower.substring(0, lower.length - 3) + "enq"
            }
            if (lower.endsWith("tion")) {
                lower = lower.substring(0, lower.length - 4) + "con"
            }
            
            lower
        }
        return convertedWords.joinToString(" ")
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
        
        // Check for permission at runtime (even if declared in manifest)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "startVoiceInput: Missing RECORD_AUDIO permission")
                return
            }
        }

        val recognizer = speechRecognizer
        val intent = speechIntent
        if (recognizer == null || intent == null) {
            Log.w(TAG, "startVoiceInput: SpeechRecognizer or intent is null")
            return
        }

        // Request priority to interrupt other apps
        if (!requestAudioPriority()) {
            Log.w(TAG, "startVoiceInput: Failed to gain audio focus priority")
        }

        // Forcefully pause/stop other media/TTS
        val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        am.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PAUSE))
        am.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PAUSE))
        am.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_STOP))
        am.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_STOP))

        try {
            recognizer.startListening(intent)
            Log.d(TAG, "startVoiceInput: Listening started")
        } catch (e: Exception) {
            Log.e(TAG, "startVoiceInput: Failed to start listening: ${e.message}", e)
            abandonAudioPriority()
        }
    }

    private fun stopVoiceInput() {
        val recognizer = speechRecognizer ?: return
        try {
            recognizer.stopListening()
            isListening = false
            Log.d(TAG, "stopVoiceInput: Listening stopped")
            abandonAudioPriority()
        } catch (e: Exception) {
            Log.e(TAG, "stopVoiceInput: Failed to stop listening: ${e.message}", e)
            abandonAudioPriority()
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
                onToggleVoice = {
                    if (isListening) {
                        stopVoiceInput()
                    } else {
                        startVoiceInput()
                    }
                },
                onToggleMode = {
                    toggleLetterMode()
                },
                onHexKeyPress = { char, isLongPress, primaryChar ->
                    val ic = currentInputConnection
                    if (ic == null) {
                        Log.e(TAG, "onHexKeyPress: currentInputConnection is NULL")
                        return@KepadModyil
                    }
                    
                    Log.d(TAG, "onHexKeyPress: char='$char', isLongPress=$isLongPress")

                    if (char == "\n") {
                        if (isLongPress && primaryChar != null) {
                            ic.deleteSurroundingText(primaryChar.length, 0)
                        }
                        // Use both commitText and sendKeyEvent for maximum compatibility
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
                        if (currentWordBuffer.isNotEmpty()) {
                            currentWordBuffer.deleteCharAt(currentWordBuffer.length - 1)
                        }

                        if (isLongPress) {
                            ic.beginBatchEdit()
                            try {
                                val before = ic.getTextBeforeCursor(100, 0) ?: ""
                                if (before.isNotEmpty()) {
                                    val len = before.length
                                    val lastSpace = before.lastIndexOf(' ')
                                    
                                    val toDelete = if (lastSpace == -1) {
                                        // No spaces in the last 100 chars: delete exactly 12
                                        minOf(len, 12)
                                    } else if (lastSpace == len - 1) {
                                        // Space is right at the cursor: delete the space and the word before it
                                        val textBeforeSpace = before.substring(0, len - 1)
                                        val secondToLastSpace = textBeforeSpace.lastIndexOf(' ')
                                        val wordLen = if (secondToLastSpace == -1) len else len - 1 - secondToLastSpace
                                        minOf(wordLen, 12)
                                    } else {
                                        // Delete from cursor back to the last space, capped at 12
                                        val wordLen = len - 1 - lastSpace
                                        minOf(wordLen, 12)
                                    }
                                    
                                    if (toDelete > 0) {
                                        ic.deleteSurroundingText(toDelete, 0)
                                        if (currentWordBuffer.length >= toDelete) {
                                            currentWordBuffer.setLength(currentWordBuffer.length - toDelete)
                                        } else {
                                            currentWordBuffer.clear()
                                        }
                                    }
                                } else {
                                    ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                                    ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL))
                                    currentWordBuffer.clear()
                                }
                            } finally {
                                ic.endBatchEdit()
                            }
                        } else {
                            if (primaryChar != null && primaryChar.isNotEmpty()) {
                                ic.deleteSurroundingText(primaryChar.length, 0)
                            } else {
                                ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                                ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL))
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
                        // Also add the separator itself if it's a punctuation mark? 
                        // Maybe not necessary for style, but good for context.
                    } else {
                        // Append to buffer
                        currentWordBuffer.append(char)
                    }

                    if (isLongPress) {
                        if (primaryChar != null) {
                            ic.deleteSurroundingText(primaryChar.length, 0)
                            ic.commitText(char, 1)
                        } else {
                            ic.deleteSurroundingText(1, 0)
                            ic.commitText(char, 1)
                        }
                    } else {
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

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        Log.d(TAG, "onStartInput: Input started, restarting: $restarting, editorType: ${attribute?.imeOptions}")
        // Automatically start voice input when the user focuses/touches an input field.
        // startVoiceInput()
    }

    override fun onFinishInput() {
        super.onFinishInput()
        Log.d(TAG, "onFinishInput: Input finished")
        // Hide your UI if it's visible
        stopVoiceInput()
    }

    override fun onWindowShown() {
        super.onWindowShown()
        Log.d(TAG, "onWindowShown: IME window shown")
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun onWindowHidden() {
        super.onWindowHidden()
        Log.d(TAG, "onWindowHidden: IME window hidden")
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: IME Service destroyed")
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        viewModelStore.clear()
        speechRecognizer?.destroy()
        speechRecognizer = null
        speechIntent = null
    }
}
