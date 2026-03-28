package com.example.angol.ime

import android.content.Context
import android.content.Intent
import android.inputmethodservice.InputMethodService
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.google.firebase.Firebase
import com.google.firebase.initialize
import com.google.firebase.ai.ai
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import com.google.firebase.ai.type.RequestOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
    
    private val ezLisenenq = mutableStateOf(false)
    private val angolSpelenqModSteyt = mutableIntStateOf(1)
    private var ezLedirMod by mutableStateOf(true)
    private var ezPunctceyconMod by mutableStateOf(false)
    private var angolSpelenqMod by angolSpelenqModSteyt
    private var ezAiVoysAktev by mutableStateOf(false)
    private var ezSentirButonPresd by mutableStateOf(false)
    
    private var isProcessingResults = false
    private var originalLedirMod = true
    private var ignoreSelectionUpdateCount = 0
    private var isClosing = false

    private val scope = CoroutineScope(Dispatchers.Main)
    
    private lateinit var kebordKontrolir: AndroidKeyboardController
    private lateinit var platfOrmSirvesez: AndroidPlatformServices
    private lateinit var firebaseService: AndroidFirebaseService
    private lateinit var voiceService: AndroidVoiceService

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry
    override val viewModelStore = ViewModelStore()

    override fun onCreate() {
        super.onCreate()
        try {
            savedStateRegistryController.performRestore(null)
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

            kebordKontrolir = AndroidKeyboardController { currentInputConnection }
            platfOrmSirvesez = AndroidPlatformServices(this, scope)
            firebaseService = AndroidFirebaseService(null)
            voiceService = AndroidVoiceService(
                onStart = { isAi -> 
                    originalLedirMod = ezLedirMod
                    ezAiVoysAktev = isAi
                    ezSentirButonPresd = true
                    startVoysEnpit() 
                },
                onStop = { 
                    ezSentirButonPresd = false
                    stopVoysEnpit() 
                },
                onTogilAngol = { isLong -> 
                    if (isLong) {
                        angolSpelenqMod = 1
                    } else {
                        angolSpelenqMod = if (angolSpelenqMod == 0) 2 else 0
                    }
                },
                isListening = ezLisenenq,
                angolSpelenqMod = angolSpelenqModSteyt
            )

            Firebase.initialize(this)

            if (SpeechRecognizer.isRecognitionAvailable(this)) {
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this).apply {
                    setRecognitionListener(object : RecognitionListener {
                        override fun onReadyForSpeech(params: Bundle?) { ezLisenenq.value = true }
                        override fun onBeginningOfSpeech() { isProcessingResults = false }
                        override fun onRmsChanged(rmsdB: Float) {}
                        override fun onBufferReceived(buffer: ByteArray?) {}
                        override fun onEndOfSpeech() { ezLisenenq.value = false }
                        override fun onError(error: Int) {
                            ezLisenenq.value = false
                            ezAiVoysAktev = false
                        }
                        override fun onResults(results: Bundle?) {
                            if (isProcessingResults) return
                            if (angolSpelenqMod == 1) { 
                                currentInputConnection?.finishComposingText()
                                ezLisenenq.value = false
                                return 
                            }
                            
                            isProcessingResults = true
                            val text = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull() ?: ""
                            ezLisenenq.value = false
                            if (text.isNotEmpty()) {
                                val ic = currentInputConnection
                                scope.launch {
                                    try {
                                        var processedText = if (ezAiVoysAktev) {
                                            android.widget.Toast.makeText(this@DaylEnpitMelxod, "AI conversion...", android.widget.Toast.LENGTH_SHORT).show()
                                            val model = Firebase.ai.generativeModel(modelName = "gemini-3.1-flash", generationConfig = generationConfig { }, safetySettings = emptyList(), requestOptions = RequestOptions())
                                            val promptMode = if (angolSpelenqMod == 2) "Angol 2" else "Angol 1"
                                            val prompt = "Transcribe into Angol phonetic system. Mode: $promptMode. Text: $text"
                                            val response = withContext(Dispatchers.IO) { model.generateContent(content { text(prompt) }) }
                                            response.text?.trim() ?: text
                                        } else {
                                            when (angolSpelenqMod) {
                                                2 -> AngolSpelenqMelxod.convertToAngolSpelling(text, mode = 2)
                                                else -> text
                                            }
                                        }
                                        if (!processedText.endsWith(".")) processedText = "$processedText."
                                        ic?.let {
                                            it.beginBatchEdit()
                                            val before = it.getTextBeforeCursor(1, 0)
                                            val needsSpace = before != null && before.isNotEmpty() && !before.endsWith(" ") && !before.endsWith("\n")
                                            it.commitText(if (needsSpace) " $processedText" else processedText, 1)
                                            it.endBatchEdit()
                                        }
                                        ezLedirMod = originalLedirMod
                                    } catch (e: Exception) {
                                        Log.e(TAG, "Speech failed: ${e.message}")
                                    } finally {
                                        ezAiVoysAktev = false
                                        isProcessingResults = false
                                    }
                                }
                            }
                        }
                        override fun onPartialResults(partialResults: Bundle?) {
                            if (isProcessingResults) return
                            val text = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull() ?: ""
                            if (text.isNotEmpty()) {
                                currentInputConnection?.let {
                                    val targetMode = when (angolSpelenqMod) {
                                        2 -> 1
                                        1 -> 1
                                        else -> 2
                                    }
                                    it.setComposingText(AngolSpelenqMelxod.convertToAngolSpelling(text, mode = targetMode), 1)
                                }
                            }
                        }
                        override fun onEvent(eventType: Int, params: Bundle?) {}
                    })
                }
                speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 5000L)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 5000L)
                        putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 5000L)
                    }
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "onCreate failed: ${e.message}")
        }
    }

    private fun startVoysEnpit() {
        if (ezLisenenq.value) return
        val recognizer = speechRecognizer ?: return
        val intent = speechIntent ?: return
        ezLisenenq.value = true
        try { recognizer.startListening(intent) } catch (e: Exception) { ezLisenenq.value = false }
    }

    private fun stopVoysEnpit() {
        speechRecognizer?.stopListening()
        ezLisenenq.value = false
    }

    override fun onCreateInputView(): View {
        window?.window?.decorView?.let { decorView ->
            decorView.setViewTreeLifecycleOwner(this)
            decorView.setViewTreeViewModelStoreOwner(this)
            decorView.setViewTreeSavedStateRegistryOwner(this)
        }
        return try {
            ComposeView(this).apply {
                setViewTreeLifecycleOwner(this@DaylEnpitMelxod)
                setViewTreeViewModelStoreOwner(this@DaylEnpitMelxod)
                setViewTreeSavedStateRegistryOwner(this@DaylEnpitMelxod)
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                setContent {
                    skrenz.DaylSkrenEntry(
                        keyboardController = kebordKontrolir,
                        platformServices = platfOrmSirvesez,
                        voiceService = voiceService,
                        isLetterMode = ezLedirMod,
                        isPunctuationMode = ezPunctceyconMod,
                        onTogilMod = { ezLedirMod = !ezLedirMod },
                        onSetPunkcuweyconMod = { ezPunctceyconMod = it },
                        ezAngolMod = angolSpelenqMod > 0,
                        onTogilAngol = { voiceService.togilAngolMod(it) }, 
                        onStartAiVoys = { voiceService.startListening(isAiMode = true) },
                        ignoreSelectionUpdate = { ignoreSelectionUpdateCount++ },
                        firebaseService = firebaseService,
                        isApp = false
                    )
                }
            }
        } catch (e: Exception) {
            android.widget.TextView(this).apply { text = "Error: ${e.message}" }
        }
    }

    override fun onUpdateSelection(oldSelStart: Int, oldSelEnd: Int, newSelStart: Int, newSelEnd: Int, candidatesStart: Int, candidatesEnd: Int) {
        super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesStart, candidatesEnd)
        if (ignoreSelectionUpdateCount > 0) { ignoreSelectionUpdateCount--; return }
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        isClosing = false
        super.onStartInputView(info, restarting)
        window?.window?.let { win ->
            win.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            win.navigationBarColor = android.graphics.Color.TRANSPARENT
        }
    }

    override fun onEvaluateFullscreenMode(): Boolean = false

    override fun onComputeInsets(outInsets: Insets?) {
        super.onComputeInsets(outInsets)
        if (outInsets == null) return
        val inputView = window?.window?.decorView ?: return
        val totalHeight = inputView.height
        val totalWidth = inputView.width
        if (totalHeight <= 0) return
        val screenWidth = resources.displayMetrics.widthPixels
        val hexSizePx = screenWidth / (5.0 * kotlin.math.sqrt(3.0))
        val clusterHeightPx = (hexSizePx * 7.8).toInt()
        val top = totalHeight - clusterHeightPx
        outInsets.contentTopInsets = top
        outInsets.visibleTopInsets = top
        outInsets.touchableInsets = Insets.TOUCHABLE_INSETS_REGION
        outInsets.touchableRegion.setEmpty()
        outInsets.touchableRegion.union(android.graphics.Rect(0, top, totalWidth, totalHeight))
    }

    override fun onWindowShown() {
        super.onWindowShown()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun onWindowHidden() {
        isClosing = true
        super.onWindowHidden()
        stopVoysEnpit()
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
