package com.example.angol.ime

import android.content.Context
import android.content.Intent
import android.inputmethodservice.InputMethodService
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
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

private const val TAG = "AngolImeService"

class AngolImeService : InputMethodService(), LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    private var speechRecognizer: SpeechRecognizer? = null
    private var speechIntent: Intent? = null
    private var isListening by mutableStateOf(false)
    private var audioFocusRequest: Any? = null // AudioFocusRequest on API 26+

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    override val viewModelStore = ViewModelStore()

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: IME Service created")
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

                        val ic = currentInputConnection
                        if (ic != null) {
                            ic.commitText(text, 1)
                            Log.d(TAG, "SpeechRecognizer: Committed final text: $text")
                        } else {
                            Log.w(TAG, "SpeechRecognizer: currentInputConnection is null")
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
                .setUsage(AudioAttributes.USAGE_VOICE_COMM_ACCESSIBILITY)
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
                onToggleVoice = {
                    if (isListening) {
                        stopVoiceInput()
                    } else {
                        startVoiceInput()
                    }
                },
                onHexKeyPress = { char, isLongPress, primaryChar ->
                    val ic = currentInputConnection ?: return@KepadModyil
                    
                    if (isLongPress) {
                        if (char == "⌫") {
                            val before = ic.getTextBeforeCursor(50, 0)
                            if (!before.isNullOrEmpty()) {
                                val trimmed = before.trimEnd()
                                val lastSpace = trimmed.lastIndexOf(' ')
                                val toDelete = if (lastSpace != -1) trimmed.length - 1 - lastSpace else trimmed.length
                                ic.deleteSurroundingText(toDelete, 0)
                            }
                        } else {
                            if (primaryChar != null) {
                                ic.deleteSurroundingText(primaryChar.length, 0)
                                ic.commitText(char, 1)
                            } else {
                                ic.deleteSurroundingText(1, 0)
                                ic.commitText(char, 1)
                            }
                        }
                    } else {
                        if (char == "⌫") {
                            val count = primaryChar?.length ?: 1
                            ic.deleteSurroundingText(count, 0)
                        } else {
                            ic.commitText(char, 1)
                        }
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
