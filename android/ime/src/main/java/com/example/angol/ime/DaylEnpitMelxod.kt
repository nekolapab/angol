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
    
    private var audioFocusRequest: Any? = null
    private var ignoreSelectionUpdateCount = 0
    private var lastVoiceTriggerTime = 0L
    private var wasStartedByUser = false
    private var isClosing = false

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry
    override val viewModelStore = ViewModelStore()

    override fun onCreate() {
        super.onCreate()
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        isLetterMode = prefs.getBoolean(KEY_LETTER_MODE, true)
        
        try {
            Firebase.initialize(this)
        } catch (e: Exception) {
            Log.e(TAG, "Firebase init failed: ${e.message}")
        }

        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) { isListening = true }
                    override fun onBeginningOfSpeech() {}
                    override fun onRmsChanged(rmsdB: Float) {}
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEndOfSpeech() {
                        isListening = false
                        abandonAudioPriority()
                        unmuteSystemStream()
                        wasStartedByUser = false
                    }
                    override fun onError(error: Int) {
                        Log.e(TAG, "Speech error: $error")
                        isListening = false
                        abandonAudioPriority()
                        unmuteSystemStream()
                        wasStartedByUser = false
                    }
                    override fun onResults(results: Bundle?) {
                        isListening = false
                        abandonAudioPriority()
                        unmuteSystemStream()
                        val text = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull() ?: return
                        
                        CoroutineScope(Dispatchers.Main).launch {
                            val ic = currentInputConnection ?: return@launch
                            val before = ic.getTextBeforeCursor(1, 0)
                            val needsLeadingSpace = before != null && before.isNotEmpty()
                            
                            val angolText = if (isAngolMode) {
                                try {
                                    val model = Firebase.vertexAI.generativeModel("gemini-1.5-flash")
                                    val prompt = "Convert to Angol spelling: $text"
                                    val response = withContext(Dispatchers.IO) { model.generateContent(content { text(prompt) }) }
                                    response.text?.trim() ?: yuteledez.AngolSpelenqMelxod.convertToAngolSpelling(text)
                                } catch (e: Exception) {
                                    yuteledez.AngolSpelenqMelxod.convertToAngolSpelling(text)
                                }
                            } else text

                            val finalStr = if (needsLeadingSpace) " $angolText" else angolText
                            ignoreSelectionUpdateCount++
                            ic.commitText(finalStr, 1)
                        }
                    }
                    override fun onPartialResults(partialResults: Bundle?) {}
                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }
            speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            }
        }
    }

    private fun debouncedStartVoiceInput() {
        val now = System.currentTimeMillis()
        if (now - lastVoiceTriggerTime > 1500) {
            lastVoiceTriggerTime = now
            wasStartedByUser = true
            startVoiceInput()
        }
    }

    private fun startVoiceInput() {
        if (isListening) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) != android.content.pm.PackageManager.PERMISSION_GRANTED) return
        
        isListening = true
        val recognizer = speechRecognizer ?: return
        val intent = speechIntent ?: return

        requestAudioPriority()
        val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                am.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_MUTE, 0)
                am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0)
            }
        } catch (e: Exception) {}

        CoroutineScope(Dispatchers.Main).launch {
            delay(200)
            try { recognizer.startListening(intent) } catch (e: Exception) { 
                isListening = false
                unmuteSystemStream()
            }
        }
    }

    private fun stopVoiceInput() {
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
        return ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@DaylEnpitMelxod)
            setViewTreeViewModelStoreOwner(this@DaylEnpitMelxod)
            setViewTreeSavedStateRegistryOwner(this@DaylEnpitMelxod)
            setContent {
                KepadModyil(
                    ic = currentInputConnection,
                    isListening = isListening,
                    isLetterMode = isLetterMode,
                    isPunctuationMode = isPunctuationMode,
                    isAngolMode = isAngolMode,
                    onToggleVoice = { if (isListening) stopVoiceInput() else { wasStartedByUser = true; startVoiceInput() } },
                    onToggleMode = { isLetterMode = !isLetterMode; getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putBoolean(KEY_LETTER_MODE, isLetterMode).apply() },
                    onSetPunctuationMode = { isPunctuationMode = it },
                    onToggleAngol = { isAngolMode = !isAngolMode },
                    ignoreSelectionUpdate = { ignoreSelectionUpdateCount++ }
                )
            }
        }
    }

    override fun onUpdateSelection(oldSelStart: Int, oldSelEnd: Int, newSelStart: Int, newSelEnd: Int, candidatesStart: Int, candidatesEnd: Int) {
        super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesStart, candidatesEnd)
        if (ignoreSelectionUpdateCount > 0) { ignoreSelectionUpdateCount--; return }
        if (isInputViewShown && newSelStart == newSelEnd && (newSelStart != oldSelStart || newSelEnd != oldSelEnd) && !isListening) debouncedStartVoiceInput()
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        isClosing = false
        super.onStartInputView(info, restarting)
        if (isInputEmpty() && !isListening) debouncedStartVoiceInput()
    }

    private fun isInputEmpty(): Boolean {
        val ic = currentInputConnection ?: return false
        return ic.getTextBeforeCursor(1, 0).isNullOrEmpty() && ic.getTextAfterCursor(1, 0).isNullOrEmpty()
    }

    override fun onWindowShown() { super.onWindowShown(); lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME) }
    override fun onWindowHidden() { isClosing = true; super.onWindowHidden(); stopVoiceInput(); lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE) }
    override fun onDestroy() { super.onDestroy(); lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY); speechRecognizer?.destroy() }
}
