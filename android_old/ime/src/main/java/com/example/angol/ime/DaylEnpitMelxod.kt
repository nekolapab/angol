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
import com.google.firebase.vertexai.GenerativeModel
import com.google.firebase.vertexai.type.content
import com.google.firebase.vertexai.type.generationConfig
import com.google.firebase.vertexai.type.RequestOptions
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
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
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
import yuteledez.AngolSpelenqMelxod

private const val TAG = "DaylEnpitMelxod"
private const val PREFS_NAME = "AngolImePrefs"
private const val KEY_LETTER_MODE = "isLetterMode"

class DaylEnpitMelxod : InputMethodService(), LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    private var speechRecognizer: SpeechRecognizer? = null
    private var speechIntent: Intent? = null
    
    private var isListening by mutableStateOf(false)
    private var isLetterMode by mutableStateOf(true)
    private var isPunctuationMode by mutableStateOf(false)
    private var isAngolMode by mutableStateOf(true)
    private var isAiVoiceActive by mutableStateOf(false)
    private var inputConnection by mutableStateOf<android.view.inputmethod.InputConnection?>(null)
    private var isCenterButtonPressed by mutableStateOf(false)
    
    private var audioFocusRequest: Any? = null
    private var lastButtonDownTime = 0L
    private var ignoreSelectionUpdateCount = 0
    private var wasStartedByUser = false
    private var isClosing = false

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry
    override val viewModelStore = ViewModelStore()

    override fun onCreate() {
        Log.d(TAG, "onCreate - started")
        super.onCreate()
        
        try {
            Firebase.initialize(this)
            Log.d(TAG, "Firebase initialized")
        } catch (e: Exception) {
            Log.e(TAG, "Firebase init failed: ${e.message}")
        }

        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        Log.d(TAG, "Lifecycle state: ${lifecycleRegistry.currentState}")
        
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            Log.d(TAG, "Speech recognition is available")
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) { isListening = true; Log.d(TAG, "Voice: Ready") }
                    override fun onBeginningOfSpeech() { Log.d(TAG, "Voice: Beginning") }
                    override fun onRmsChanged(rmsdB: Float) {}
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEndOfSpeech() {
                        Log.d(TAG, "Voice: End")
                        isListening = false
                        // Don't restart here, wait for results or error
                    }
                    override fun onError(error: Int) {
                        Log.e(TAG, "Speech error: $error")
                        isListening = false
                        isAiVoiceActive = false
                        abandonAudioPriority()
                        unmuteSystemStream()
                        
                        if (isCenterButtonPressed && error != SpeechRecognizer.ERROR_RECOGNIZER_BUSY) {
                            // Restart after a short delay to avoid loops
                            CoroutineScope(Dispatchers.Main).launch {
                                delay(500)
                                if (isCenterButtonPressed) startVoiceInput()
                            }
                        } else if (!isCenterButtonPressed) {
                            wasStartedByUser = false
                        }
                    }
                    override fun onResults(results: Bundle?) {
                        Log.d(TAG, "Voice: Results")
                        isListening = false
                        abandonAudioPriority()
                        unmuteSystemStream()
                        val text = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull() ?: ""
                        
                        if (text.isNotEmpty()) {
                            CoroutineScope(Dispatchers.Main).launch {
                                try {
                                    val finalStr = if (isAiVoiceActive) {
                                        android.widget.Toast.makeText(this@DaylEnpitMelxod, "AI phonetic conversion...", android.widget.Toast.LENGTH_SHORT).show()
                                        val model = Firebase.vertexAI.generativeModel(
                                            modelName = "gemini-1.5-flash",
                                            generationConfig = generationConfig { },
                                            safetySettings = emptyList(),
                                            requestOptions = RequestOptions()
                                        )
                                        val prompt = """
                                            Transcribe the following text into the 'Angol' phonetic system. 
                                            Rules:
                                            1. Use exactly 36 characters: 24 consonants (l, lx, x, d, t, c, g, k, f, b, p, s, lh, h, n, y, r, j, nq, q, v, w, m, z) and 12 vowel symbols (1, 2, 3, 4, 5, 6, 7, 8, 9, 0, A, O).
                                            2. Transcription must be PURELY PHONETIC. If a sound is not spoken (like the 't' in 'exactly' if skipped), do not write it.
                                            3. 'nq' is the nasal sound in 'thing'. If followed by a 'g' sound (like 'Angol'), write 'nqg'.
                                            4. 'c' is the 'sh' sound, 'tc' is 'ch'. 'lh' is voiced 'the', 'lx' is unvoiced 'thin'.
                                            5. Vowels: 1=ah, 2=at, 3=eh, 4=it, 5=ee, 6=er, 7=put, 8=up, 9=too, 0=go, A=oh, O=all.
                                            6. Output ONLY the transcribed characters.
                                            Text: $text
                                        """.trimIndent()
                                        val response = withContext(Dispatchers.IO) { model.generateContent(content { text(prompt) }) }
                                        response.text?.trim() ?: text
                                    } else {
                                        if (isAngolMode) {
                                            yuteledez.AngolSpelenqMelxod.convertToAngolSpelling(text)
                                        } else text
                                    }

                                    val activeIc = inputConnection ?: return@launch
                                    val before = activeIc.getTextBeforeCursor(1, 0)
                                    val needsLeadingSpace = before != null && before.isNotEmpty() && !before.endsWith(" ")
                                    val commitStr = if (needsLeadingSpace) " $finalStr" else finalStr
                                    
                                    ignoreSelectionUpdateCount++
                                    activeIc.commitText(commitStr, 1)
                                } catch (e: Exception) {
                                    Log.e(TAG, "Commit voice results failed: ${e.message}")
                                } finally {
                                    isAiVoiceActive = false
                                }
                            }
                        } else {
                            isAiVoiceActive = false
                        }
                        
                        if (isCenterButtonPressed) {
                            CoroutineScope(Dispatchers.Main).launch {
                                delay(500)
                                if (isCenterButtonPressed) startVoiceInput()
                            }
                        } else {
                            wasStartedByUser = false
                        }
                    }
                    override fun onPartialResults(partialResults: Bundle?) {}
                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }
            speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                // Try to prevent early timeout
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 10000L)
                    putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 10000L)
                }
            }
        }
        Log.d(TAG, "onCreate - completed")
    }

    private fun startVoiceInput() {
        Log.d(TAG, "startVoiceInput - isListening=$isListening, pressed=$isCenterButtonPressed")
        if (isListening) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Requesting microphone permission via Activity")
            val intent = Intent(this, PermissionActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
            return
        }
        
        val recognizer = speechRecognizer ?: return
        val intent = speechIntent ?: return

        isListening = true
        requestAudioPriority()
        val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                am.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_MUTE, 0)
                am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0)
            }
        } catch (e: Exception) {}

        // Ensure we only start if the button is still actually pressed (for PTT)
        // or if it was a toggle start (not PTT).
        if (wasStartedByUser && !isCenterButtonPressed) {
            Log.d(TAG, "Cancelling voice start: button already released")
            isListening = false
            unmuteSystemStream()
            return
        }

        try { 
            recognizer.startListening(intent) 
        } catch (e: Exception) { 
            isListening = false
            unmuteSystemStream()
            Log.e(TAG, "Voice start failed: ${e.message}")
        }
    }

    private fun stopVoiceInput() {
        Log.d(TAG, "stopVoiceInput")
        speechRecognizer?.stopListening()
        isListening = false
        abandonAudioPriority()
        unmuteSystemStream()
    }

    private fun unmuteSystemStream() {
        CoroutineScope(Dispatchers.Main).launch {
            delay(1000)
            val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    am.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_UNMUTE, 0)
                    am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, 0)
                }
            } catch (e: Exception) {}
        }
    }

    private fun requestAudioPriority() {
        val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        am.mode = AudioManager.MODE_IN_COMMUNICATION
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val attr = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION).setContentType(AudioAttributes.CONTENT_TYPE_SPEECH).build()
            val request = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE).setAudioAttributes(attr).build()
            audioFocusRequest = request
            am.requestAudioFocus(request)
        }
    }

    private fun abandonAudioPriority() {
        val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        am.mode = AudioManager.MODE_NORMAL
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            (audioFocusRequest as? AudioFocusRequest)?.let { am.abandonAudioFocusRequest(it) }
        }
    }

    override fun onCreateInputView(): View {
        Log.d(TAG, "onCreateInputView - started")
        val composeView = ComposeView(this)
        
        // Set owners on the specific view
        composeView.setViewTreeLifecycleOwner(this)
        composeView.setViewTreeViewModelStoreOwner(this)
        composeView.setViewTreeSavedStateRegistryOwner(this)
        
        // Attempt to set on window decor view as fallback for stricter hierarchy checks
        try {
            window?.window?.decorView?.let { decorView ->
                Log.d(TAG, "Setting owners on decorView")
                decorView.setViewTreeLifecycleOwner(this)
                decorView.setViewTreeViewModelStoreOwner(this)
                decorView.setViewTreeSavedStateRegistryOwner(this)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set owners on decorView: ${e.message}")
        }

        composeView.setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
        
        composeView.setContent {
            Log.d(TAG, "Compose content block started")
            KepadModyil(
                ic = inputConnection,
                isListening = isListening,
                isLetterMode = isLetterMode,
                isPunctuationMode = isPunctuationMode,
                isAngolMode = isAngolMode,
                onToggleVoice = { if (isListening) stopVoiceInput() else { wasStartedByUser = true; startVoiceInput() } },
                onStartVoice = { 
                    isCenterButtonPressed = true
                    lastButtonDownTime = System.currentTimeMillis()
                    wasStartedByUser = true
                    // Start after a tiny delay to see if it's a tap or a hold
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(150)
                        if (isCenterButtonPressed) startVoiceInput()
                    }
                },
                onStopVoice = { 
                    isCenterButtonPressed = false
                    stopVoiceInput() 
                },
                onStartAiVoice = {
                    Log.d(TAG, "onStartAiVoice triggered")
                    isAiVoiceActive = true
                    wasStartedByUser = true
                    startVoiceInput()
                },
                onToggleMode = { isLetterMode = !isLetterMode },
                onSetPunctuationMode = { isPunctuationMode = it },
                onToggleAngol = { isAngolMode = !isAngolMode },
                ignoreSelectionUpdate = { ignoreSelectionUpdateCount++ }
            )
        }
        Log.d(TAG, "onCreateInputView - returning view")
        return composeView
    }

    override fun onUpdateSelection(oldSelStart: Int, oldSelEnd: Int, newSelStart: Int, newSelEnd: Int, candidatesStart: Int, candidatesEnd: Int) {
        super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesStart, candidatesEnd)
        Log.d(TAG, "onUpdateSelection: $oldSelStart -> $newSelStart")
        if (ignoreSelectionUpdateCount > 0) { ignoreSelectionUpdateCount--; return }
        // Automatic voice input removed to avoid conflicts
    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        inputConnection = currentInputConnection
        Log.d(TAG, "onStartInput - updated inputConnection")
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        Log.d(TAG, "onStartInputView")
        isClosing = false
        super.onStartInputView(info, restarting)
        inputConnection = currentInputConnection
        // Automatic voice input removed to avoid conflicts
    }

    private fun isInputEmpty(): Boolean {
        val ic = currentInputConnection ?: return false
        return ic.getTextBeforeCursor(1, 0).isNullOrEmpty() && ic.getTextAfterCursor(1, 0).isNullOrEmpty()
    }

    override fun onWindowShown() {
        Log.d(TAG, "onWindowShown")
        super.onWindowShown()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun onWindowHidden() {
        Log.d(TAG, "onWindowHidden")
        isClosing = true
        super.onWindowHidden()
        stopVoiceInput()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        super.onDestroy()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        viewModelStore.clear()
        speechRecognizer?.destroy()
    }
}
