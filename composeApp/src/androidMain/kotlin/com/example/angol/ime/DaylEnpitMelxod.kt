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
import android.view.inputmethod.InputConnection
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

class DaylEnpitMelxod : InputMethodService(), LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    private var speechRecognizer: SpeechRecognizer? = null
    private var speechIntent: Intent? = null
    
    private var isListening = mutableStateOf(false)
    private var isLetterMode by mutableStateOf(true)
    private var isPunctuationMode by mutableStateOf(false)
    private var isAngolMode by mutableStateOf(true)
    private var isAiVoiceActive by mutableStateOf(false)
    private var isCenterButtonPressed by mutableStateOf(false)
    
    private var audioFocusRequest: Any? = null
    private var lastButtonDownTime = 0L
    private var ignoreSelectionUpdateCount = 0
    private var wasStartedByUser = false
    private var isClosing = false

    private val scope = CoroutineScope(Dispatchers.Main)
    
    private lateinit var keyboardController: AndroidKeyboardController
    private lateinit var platformServices: AndroidPlatformServices
    private lateinit var voiceService: AndroidVoiceService

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry
    override val viewModelStore = ViewModelStore()

    override fun onCreate() {
        Log.d(TAG, "onCreate - started")
        super.onCreate()
        
        try {
            savedStateRegistryController.performRestore(null)
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
            Log.d(TAG, "Lifecycle and SavedState initialized")

            keyboardController = AndroidKeyboardController { currentInputConnection }
            platformServices = AndroidPlatformServices(this, scope)
            voiceService = AndroidVoiceService(
                onStart = { isAi -> 
                    isAiVoiceActive = isAi
                    wasStartedByUser = true
                    startVoiceInput() 
                },
                onStop = { stopVoiceInput() },
                isListening = isListening
            )
            Log.d(TAG, "Controllers and services initialized")

            try {
                Firebase.initialize(this)
                Log.d(TAG, "Firebase initialized successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Firebase init failed: ${e.message}")
            }

            if (SpeechRecognizer.isRecognitionAvailable(this)) {
                Log.d(TAG, "Setting up SpeechRecognizer")
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this).apply {
                    setRecognitionListener(object : RecognitionListener {
                        override fun onReadyForSpeech(params: Bundle?) { isListening.value = true; Log.d(TAG, "Voice: Ready") }
                        override fun onBeginningOfSpeech() { Log.d(TAG, "Voice: Beginning") }
                        override fun onRmsChanged(rmsdB: Float) {}
                        override fun onBufferReceived(buffer: ByteArray?) {}
                        override fun onEndOfSpeech() {
                            Log.d(TAG, "Voice: End")
                            isListening.value = false
                        }
                        override fun onError(error: Int) {
                            Log.e(TAG, "Speech error: $error")
                            isListening.value = false
                            isAiVoiceActive = false
                            abandonAudioPriority()
                            unmuteSystemStream()
                            
                            if (isCenterButtonPressed && error != SpeechRecognizer.ERROR_RECOGNIZER_BUSY) {
                                scope.launch {
                                    delay(500)
                                    if (isCenterButtonPressed) startVoiceInput()
                                }
                            } else if (!isCenterButtonPressed) {
                                wasStartedByUser = false
                            }
                        }
                        override fun onResults(results: Bundle?) {
                            val text = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull() ?: ""
                            Log.d(TAG, "Voice Results received: '$text', isAiVoiceActive=$isAiVoiceActive")
                            isListening.value = false
                            abandonAudioPriority()
                            unmuteSystemStream()

                            if (text.isNotEmpty()) {
                                // CRITICAL: Capture InputConnection immediately outside the launch scope
                                val ic: InputConnection? = currentInputConnection
                                
                                scope.launch {
                                    try {
                                        val finalStr = if (isAiVoiceActive) {
                                            Log.d(TAG, "Starting Gemini transcription for: '$text'")
                                            android.widget.Toast.makeText(this@DaylEnpitMelxod, "AI conversion...", android.widget.Toast.LENGTH_SHORT).show()
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
                                            val res = response.text?.trim() ?: text
                                            Log.d(TAG, "Gemini response: '$res'")
                                            res
                                        } else {
                                            if (isAngolMode) {
                                                AngolSpelenqMelxod.convertToAngolSpelling(text)
                                            } else text
                                        }

                                        Log.d(TAG, "Attempting to commit text: '$finalStr'")
                                        if (ic != null) {
                                            val before = ic.getTextBeforeCursor(1, 0)
                                            val needsLeadingSpace = before != null && before.isNotEmpty() && !before.endsWith(" ")
                                            val commitStr = if (needsLeadingSpace) " $finalStr" else finalStr

                                            ignoreSelectionUpdateCount++
                                            ic.beginBatchEdit()
                                            ic.commitText(commitStr, 1)
                                            ic.endBatchEdit()
                                            Log.d(TAG, "commitText completed")
                                        } else {
                                            Log.e(TAG, "Cannot commit text: InputConnection was NULL at capture time")
                                        }
                                    } catch (e: Exception) {
                                        Log.e(TAG, "Commit voice results failed: ${e.message}")
                                        e.printStackTrace()
                                    } finally {
                                        isAiVoiceActive = false
                                    }
                                }
                            } else {
                                Log.d(TAG, "Voice results were empty")
                                isAiVoiceActive = false
                            }
                            
                            if (isCenterButtonPressed) {
                                scope.launch {
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
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 10000L)
                        putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 10000L)
                    }
                }
            }
            Log.d(TAG, "onCreate - completed successfully")
        } catch (e: Exception) {
            Log.e(TAG, "CRITICAL: onCreate failed: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun startVoiceInput() {
        if (isListening.value) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(this, PermissionActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
            startActivity(intent)
            return
        }
        
        val recognizer = speechRecognizer ?: return
        val intent = speechIntent ?: return

        isListening.value = true
        requestAudioPriority()
        val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                am.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_MUTE, 0)
                am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0)
            }
        } catch (e: Exception) {}

        if (wasStartedByUser && !isCenterButtonPressed && !isAiVoiceActive) {
            isListening.value = false
            unmuteSystemStream()
            return
        }

        try { 
            recognizer.startListening(intent) 
        } catch (e: Exception) { 
            isListening.value = false
            unmuteSystemStream()
            Log.e(TAG, "Voice start failed: ${e.message}")
        }
    }

    private fun stopVoiceInput() {
        speechRecognizer?.stopListening()
        isListening.value = false
        abandonAudioPriority()
        unmuteSystemStream()
    }

    private fun unmuteSystemStream() {
        scope.launch {
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
        
        // Critically important for Compose in a Service: 
        // Set the owners on the window's decor view so Compose can find them during traversal.
        window?.window?.decorView?.let { decorView ->
            decorView.setViewTreeLifecycleOwner(this)
            decorView.setViewTreeViewModelStoreOwner(this)
            decorView.setViewTreeSavedStateRegistryOwner(this)
        }

        return try {
            val composeView = ComposeView(this).apply {
                // Also set them on the view itself as a double-layer of protection
                setViewTreeLifecycleOwner(this@DaylEnpitMelxod)
                setViewTreeViewModelStoreOwner(this@DaylEnpitMelxod)
                setViewTreeSavedStateRegistryOwner(this@DaylEnpitMelxod)
                
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
                
                setContent {
                    KepadModyil(
                        keyboardController = keyboardController,
                        platformServices = platformServices,
                        voiceService = voiceService,
                        isLetterMode = isLetterMode,
                        isPunctuationMode = isPunctuationMode,
                        isAngolMode = isAngolMode,
                        onToggleMode = { isLetterMode = !isLetterMode },
                        onSetPunctuationMode = { isPunctuationMode = it },
                        onToggleAngol = { isAngolMode = !isAngolMode },
                        onStartAiVoice = {
                            isAiVoiceActive = true
                            wasStartedByUser = true
                            startVoiceInput()
                        },
                        ignoreSelectionUpdate = { ignoreSelectionUpdateCount++ }
                    )
                }
            }
            Log.d(TAG, "onCreateInputView - successful")
            composeView
        } catch (e: Exception) {
            Log.e(TAG, "CRITICAL: onCreateInputView failed: ${e.message}")
            e.printStackTrace()
            // Fallback to a simple view if Compose fails
            android.widget.TextView(this).apply {
                text = "Error: ${e.message}"
            }
        }
    }

    override fun onUpdateSelection(oldSelStart: Int, oldSelEnd: Int, newSelStart: Int, newSelEnd: Int, candidatesStart: Int, candidatesEnd: Int) {
        super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesStart, candidatesEnd)
        if (ignoreSelectionUpdateCount > 0) { ignoreSelectionUpdateCount--; return }
    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        isClosing = false
        super.onStartInputView(info, restarting)
    }

    override fun onWindowShown() {
        super.onWindowShown()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun onWindowHidden() {
        isClosing = true
        super.onWindowHidden()
        stopVoiceInput()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        viewModelStore.clear()
        speechRecognizer?.destroy()
    }
}
