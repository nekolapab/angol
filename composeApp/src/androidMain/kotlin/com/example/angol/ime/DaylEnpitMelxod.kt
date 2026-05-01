package com.example.angol.ime

import android.content.Context
import android.content.Intent
import android.inputmethodservice.InputMethodService
import android.media.AudioManager
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
    private val ezSpoken = mutableStateOf(false)
    private val angolSpelenqModSteyt = mutableIntStateOf(2)
    private var ezLedirMod by mutableStateOf(true)
    private var ezPunctceyconMod by mutableStateOf(false)
    private var angolSpelenqMod by angolSpelenqModSteyt
    private var ezAiVoysAktev by mutableStateOf(false)
    private var ezSentirButonPresd by mutableStateOf(false)
    private var ezUpsayddawn by mutableStateOf(false)
    private var ezVoysSutdawnRekwested = true
    private var orientationListener: android.view.OrientationEventListener? = null
    
    private var isProcessingResults = false
    private var originalLedirMod = true
    private var ignoreSelectionUpdateCount = 0
    private var isClosing = false

    private val scope = CoroutineScope(Dispatchers.Main)
    
    private lateinit var kebordKontrolir: AndroidKeyboardController
    private lateinit var platfOrmSirvesez: AndroidPlatformServices
    private lateinit var firebaseService: AndroidFirebaseService
    private lateinit var voiceService: AndroidVoiceService
    private lateinit var audioManager: AudioManager
    private var originalSystemVol = -1
    private var originalNotificationVol = -1
    private var originalMusicVol = -1

    private fun dipVolume() {
        try {
            originalSystemVol = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM)
            originalNotificationVol = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION)
            originalMusicVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            
            // Lower to near-zero
            audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, 0, 0)
            audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, 0)
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0)
        } catch (e: Exception) {
            Log.e(TAG, "dipVolume failed: ${e.message}")
        }
    }

    private fun restoreVolume() {
        try {
            if (originalSystemVol != -1) {
                audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, originalSystemVol, 0)
            }
            if (originalNotificationVol != -1) {
                audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, originalNotificationVol, 0)
            }
            if (originalMusicVol != -1) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, originalMusicVol, 0)
            }
        } catch (e: Exception) {
            Log.e(TAG, "restoreVolume failed: ${e.message}")
        }
    }

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry
    override val viewModelStore = ViewModelStore()

    override fun onCreate() {
        super.onCreate()
        try {
            audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            savedStateRegistryController.performRestore(null)
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

            orientationListener = object : android.view.OrientationEventListener(this) {
                override fun onOrientationChanged(orientation: Int) {
                    if (orientation == android.view.OrientationEventListener.ORIENTATION_UNKNOWN) return
                    
                    // Tightened range: 180 degrees +/- 20 degrees
                    val isPhysicallyUpsideDown = orientation in 160..200
                    
                    val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        display
                    } else {
                        @Suppress("DEPRECATION")
                        (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
                    }
                    val systemRotation = display?.rotation ?: android.view.Surface.ROTATION_0
                    val systemAlreadyFlipped = (systemRotation == android.view.Surface.ROTATION_180)

                    // Only flip manually if physically inverted AND system hasn't flipped the window
                    val shouldFlip = isPhysicallyUpsideDown && !systemAlreadyFlipped
                    
                    if (shouldFlip != ezUpsayddawn) {
                        ezUpsayddawn = shouldFlip
                    }
                }
            }
            if (orientationListener?.canDetectOrientation() == true) {
                orientationListener?.enable()
            }

            kebordKontrolir = AndroidKeyboardController(
                getIc = { currentInputConnection },
                onSmartEnter = {
                    val ic = currentInputConnection ?: return@AndroidKeyboardController
                    val info = currentInputEditorInfo ?: return@AndroidKeyboardController
                    val action = info.imeOptions and EditorInfo.IME_MASK_ACTION
                    
                    if (action != EditorInfo.IME_ACTION_NONE && action != EditorInfo.IME_ACTION_UNSPECIFIED) {
                        // Standard: In multi-line (chat), type NEWLINE
                        ic.commitText("\n", 1)
                        platfOrmSirvesez.addToCorpus("\n")
                    } else {
                        // Standard: In single-line (rename), perform the ACTION (Done)
                        sendDefaultEditorAction(true)
                    }
                },
                onForcedSubmit = {
                    // Always perform the ACTION (Done/Submit/Go) regardless of context
                    sendDefaultEditorAction(true)
                }
            )
            platfOrmSirvesez = AndroidPlatformServices(this, scope)
            firebaseService = AndroidFirebaseService(null)
            voiceService = AndroidVoiceService(
                onStart = { isAi -> 
                    originalLedirMod = ezLedirMod
                    ezAiVoysAktev = isAi
                    ezSentirButonPresd = true
                    ezSpoken.value = false
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
                hasSpoken = ezSpoken,
                angolSpelenqMod = angolSpelenqModSteyt
            )

            Firebase.initialize(this)
            initSpeechRecognizer()
        } catch (e: Exception) {
            Log.e(TAG, "onCreate failed: ${e.message}")
        }
    }

    private fun startVoysEnpit() {
        if (ezLisenenq.value && !ezVoysSutdawnRekwested) return
        
        if (android.content.pm.PackageManager.PERMISSION_GRANTED != 
            androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)) {
            val intent = Intent(this, PermissionActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
            return
        }

        ezVoysSutdawnRekwested = false
        dipVolume()

        if (speechRecognizer == null) {
            initSpeechRecognizer()
        }

        val recognizer = speechRecognizer ?: return
        val intent = speechIntent ?: return
        ezLisenenq.value = true
        try { recognizer.startListening(intent) } catch (e: Exception) { ezLisenenq.value = false }
    }

    private fun initSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) { 
                        ezLisenenq.value = true 
                    }
                    override fun onBeginningOfSpeech() { 
                        isProcessingResults = false
                        ezSpoken.value = true
                        // Restore volume AFTER start-up beep is definitely over
                        scope.launch {
                            delay(200)
                            restoreVolume()
                        }
                    }
                    override fun onRmsChanged(rmsdB: Float) {
                        if (rmsdB > 2.0f) ezSpoken.value = true
                    }
                    override fun onBufferReceived(buffer: ByteArray?) {
                        if (buffer != null && buffer.isNotEmpty()) ezSpoken.value = true
                    }
                    override fun onEndOfSpeech() { 
                        ezLisenenq.value = false 
                        if (!ezVoysSutdawnRekwested) {
                            scope.launch {
                                delay(100)
                                startVoysEnpit()
                            }
                        }
                    }
                    override fun onError(error: Int) {
                        Log.e(TAG, "Speech recognizer error: $error")
                        ezLisenenq.value = false
                        restoreVolume()

                        if (!ezVoysSutdawnRekwested && (error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT || error == SpeechRecognizer.ERROR_NO_MATCH)) {
                            scope.launch {
                                delay(100)
                                startVoysEnpit()
                            }
                        } else {
                            ezAiVoysAktev = false
                        }
                    }
                    override fun onResults(results: Bundle?) {
                        if (isProcessingResults) return
                        
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
                                        val promptMode = if (angolSpelenqMod == 1) "Angol 1" else "Angol 2"
                                        val prompt = "Output only the phonetic transcription in Angol. No intro, no extra punctuation. Mode: $promptMode. Text: $text"
                                        val response = withContext(Dispatchers.IO) { model.generateContent(content { text(prompt) }) }
                                        response.text?.trim() ?: text
                                    } else {
                                        when (angolSpelenqMod) {
                                            1 -> AngolSpelenqMelxod.convertToAngolSpelling(text, mode = 1)
                                            2 -> AngolSpelenqMelxod.convertToAngolSpelling(text, mode = 2)
                                            else -> text
                                        }
                                    }
                                    val finalResult = processedText.trim()
                                    if (finalResult.isEmpty()) return@launch
                                    ic?.let {
                                        it.beginBatchEdit()
                                        
                                        // 1. CLEAR underlining partial results first
                                        it.setComposingText("", 1)
                                        it.finishComposingText()
                                        
                                        // 2. Robust Shortcut Clean-up
                                        val contextBefore = it.getTextBeforeCursor(2, 0) ?: ""
                                        if (contextBefore.endsWith(". ")) {
                                            it.deleteSurroundingText(2, 0)
                                        } else if (contextBefore.endsWith(".")) {
                                            it.deleteSurroundingText(1, 0)
                                        }

                                        // 3. Leading Space (Added BEFORE text)
                                        val before = it.getTextBeforeCursor(1, 0) ?: ""
                                        if (before.isNotEmpty() && !before.last().isWhitespace()) {
                                            it.commitText(" ", 1)
                                        }

                                        // 4. Commit Dictation
                                        it.commitText(finalResult, 1)
                                        
                                        // 5. Trailing Space
                                        val after = it.getTextAfterCursor(1, 0) ?: ""
                                        if (after.isNotEmpty() && !after.first().isWhitespace()) {
                                            it.commitText(" ", 1)
                                        }
                                        
                                        it.endBatchEdit()
                                    }
                                    ezLedirMod = originalLedirMod
                                } catch (e: Exception) {
                                    Log.e(TAG, "Speech failed: ${e.message}")
                                } finally {
                                    ezAiVoysAktev = false
                                    isProcessingResults = false
                                    if (!ezVoysSutdawnRekwested) {
                                        delay(100)
                                        startVoysEnpit()
                                    }
                                }
                            }
                        } else {
                            if (!ezVoysSutdawnRekwested) {
                                scope.launch {
                                    delay(100)
                                    startVoysEnpit()
                                }
                            }
                        }
                    }
                    override fun onPartialResults(partialResults: Bundle?) {
                        if (isProcessingResults) return
                        val text = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull() ?: ""
                        if (text.isNotEmpty()) {
                            currentInputConnection?.let {
                                val targetMode = if (angolSpelenqMod in 1..2) angolSpelenqMod else 2
                                it.setComposingText(AngolSpelenqMelxod.convertToAngolSpelling(text, mode = targetMode), 1)
                            }
                        }
                    }
                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }
            speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                // Set very long timeouts to prevent stopping on "pause" (silence)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 60000L)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 60000L)
                    putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 60000L)
                }
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            }
        }
    }

    private fun stopVoysEnpit() {
        ezVoysSutdawnRekwested = true
        dipVolume()
        
        speechRecognizer?.stopListening()
        ezLisenenq.value = false
        
        // Restore volume after a longer delay to fully cover shutdown beeps
        scope.launch {
            delay(1000)
            restoreVolume()
        }
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
                        ezUpsayddawn = ezUpsayddawn,
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

    override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {
        super.onConfigurationChanged(newConfig)
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
