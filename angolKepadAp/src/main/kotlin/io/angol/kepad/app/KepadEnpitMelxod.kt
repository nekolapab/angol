package io.angol.kepad.app
import yuteledez.padenq

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
import androidx.compose.ui.platform.LocalView
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
import steyt.AngolSteyt
import modalz.ModyilDeyda
import modalz.HeksagonPozecon
import yuteledez.HeksagonDjeyometre
import modyilz.KepadModyil
import modyilz.KeyboardController
import modyilz.PlatformServices
import modyilz.VoiceService
import yuteledez.AngolSpelenqMelxod
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import yuteledez.klekabil
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.sp
import kotlin.math.sqrt
import kotlinx.serialization.json.Json
import com.example.angol.ime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text

private const val TAG = "KepadEnpitMelxod"

class KepadEnpitMelxod : InputMethodService(), LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    private var speechRecognizer: SpeechRecognizer? = null
    private var speechIntent: Intent? = null
    
    private val ezLisenenq = mutableStateOf(false)
    private val ezSpoken = mutableStateOf(false)
    private val angolSpelenqModSteyt = mutableIntStateOf(0)
    private var ezLeterMod by mutableStateOf(true)
    private var ezPunkcuweyconMod by mutableStateOf(false)
    private var angolSpelenqMod by angolSpelenqModSteyt
    private var ezAiVoysAktev by mutableStateOf(false)
    private var ezSentirButonPresd by mutableStateOf(false)
    private var ezUpsayddawn by mutableStateOf(false)
    private var ezVoysSutdawnRekwested = true
    private var orientationListener: android.view.OrientationEventListener? = null
    
    private var isProcessingResults = false
    private var originalLeterMod = true
    private var ignoreSelectionUpdateCount = 0
    private var isClosing = false
    private var isNumberField = false

    private val scope = CoroutineScope(Dispatchers.Main)
    
    private lateinit var kebordKontrolir: AndroidKeyboardController
    private lateinit var platfOrmSirvesez: AndroidPlatformServices
    private lateinit var firebaseSirves: AndroidFirebaseSirves
    private lateinit var voiceService: AndroidVoiceService
    private val daylSteyt = AngolSteyt()
    private lateinit var audioManager: AudioManager
    private var originalSystemVol = -1
    private var dynamicGridHeightPx = 0
    private var dynamicGridWidthPx = 0
    private var dynamicAdjustedHeightPx = 0
    private var dynamicGridCenterYPx = 0f
    private var hexCentersDp: List<Pair<Float, Float>> = emptyList()
    private var hexSayzDp: Float = 0f

    private var layoutUpdateReceiver: android.content.BroadcastReceiver? = null

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
            firebaseSirves = AndroidFirebaseSirves(this)
            voiceService = AndroidVoiceService(
                onStart = { isAi -> 
                    originalLeterMod = ezLeterMod
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
                    // Bi-state: toggle between Off (0) and Black (1)
                    angolSpelenqMod = if (angolSpelenqMod == 0) 1 else 0
                    Log.e(TAG, "Angol Toggle: new mod = $angolSpelenqMod")
                },
                isListening = ezLisenenq,
                hasSpoken = ezSpoken,
                angolSpelenqMod = angolSpelenqModSteyt
            )

            Firebase.initialize(this)
            initSpeechRecognizer()

            // Broadcast Sync: Listen for layout updates from the Hub/Builder
            layoutUpdateReceiver = object : android.content.BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    if (intent?.action == AndroidFirebaseSirves.ACTION_UPDATE_LAYOUT) {
                        val jsonString = intent.getStringExtra(AndroidFirebaseSirves.EXTRA_LAYOUT_JSON)
                        val env = intent.getStringExtra(AndroidFirebaseSirves.EXTRA_ENVIRONMENT) ?: "current"
                        
                        if (jsonString != null && env == "current") {
                            try {
                                val jsonParser = Json { 
                                    ignoreUnknownKeys = true 
                                }
                                val updatedModules: List<ModyilDeyda> = jsonParser.decodeFromString(jsonString)
                                if (updatedModules.isNotEmpty()) {
                                    Log.d(TAG, "Received broadcast layout update ($env)")
                                    daylSteyt.updateModules(updatedModules)
                                    // NOTE: Do NOT call seyvModjilLeyawt here — it would
                                    // broadcast again, creating an infinite loop.
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Error parsing broadcast layout", e)
                            }
                        }
                    }
                }
            }
            val filter = android.content.IntentFilter(AndroidFirebaseSirves.ACTION_UPDATE_LAYOUT)
            androidx.core.content.ContextCompat.registerReceiver(
                this,
                layoutUpdateReceiver,
                filter,
                androidx.core.content.ContextCompat.RECEIVER_EXPORTED
            )

        } catch (e: Exception) {
            Log.e(TAG, "onCreate failed: ${e.message}")
        }
    }
private fun startVoysEnpit() {
    if (ezLisenenq.value && !ezVoysSutdawnRekwested) return

    if (android.content.pm.PackageManager.PERMISSION_GRANTED != 
        androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)) {
        val intent = Intent(this, PirmeconAktevede::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
        return
    }

    ezVoysSutdawnRekwested = false

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
                            scope.launch {
                                try {
                                    val ic = currentInputConnection ?: return@launch
                                    
                                    var processedText = if (ezAiVoysAktev) {
                                        android.widget.Toast.makeText(this@KepadEnpitMelxod, "AI conversion...", android.widget.Toast.LENGTH_SHORT).show()
                                        val model = Firebase.ai.generativeModel(modelName = "gemini-3.1-flash", generationConfig = generationConfig { }, safetySettings = emptyList(), requestOptions = RequestOptions())
                                        // Mode 1: final is Angol 2 (standard vowels)
                                        val promptMode = if (angolSpelenqMod == 1) "Angol (Phonetic spelling with standard vowels a,e,i,u,o)" else "Standard English"
                                        val prompt = "Output only the phonetic transcription in Angol. No intro, no extra punctuation. Mode: $promptMode. Text: $text"
                                        val response = withContext(Dispatchers.IO) { model.generateContent(content { text(prompt) }) }
                                        response.text?.trim() ?: text
                                    } else {
                                        when (angolSpelenqMod) {
                                            1 -> AngolSpelenqMelxod.convertToAngolSpelling(text, mode = 1) // FINAL: Angol 2 (standard vowels)
                                            else -> text // OFF Mode: final is Standard English
                                        }
                                    }
                                    val finalResult = processedText.trim()
                                    Log.e(TAG, "Final Results: mod=$angolSpelenqMod, text='$text', final='$finalResult'")
                                    if (finalResult.isEmpty()) return@launch
                                    
                                    ic.beginBatchEdit()
                                    ic.setComposingText(finalResult, 1)
                                    ic.commitText(finalResult, 1)
                                    ic.endBatchEdit()
                                    
                                    ezLeterMod = originalLeterMod
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
                                // If Angol is BLACK (1), PREVIEW in Angol 1 (mode 2 - numbers)
                                // If Angol is OFF (0), PREVIEW in Angol 2 (mode 1 - standard vowels)
                                val previewMode = if (angolSpelenqMod == 1) 2 else 1
                                val previewText = AngolSpelenqMelxod.convertToAngolSpelling(text, mode = previewMode)
                                Log.e(TAG, "Partial Results: mod=$angolSpelenqMod, pMode=$previewMode, text='$text', preview='$previewText'")
                                it.setComposingText(previewText, 1)
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
        
        speechRecognizer?.stopListening()
        ezLisenenq.value = false
        
        // Restore volume after a longer delay to fully cover shutdown beeps
        scope.launch {
            delay(1000)
        }
    }

    override fun onCreateInputView(): View {
        window?.window?.apply {
            setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT))
            setDimAmount(0f)
            clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }

        window?.window?.decorView?.let { decorView ->
            decorView.setViewTreeLifecycleOwner(this)
            decorView.setViewTreeViewModelStoreOwner(this)
            decorView.setViewTreeSavedStateRegistryOwner(this)
        }
        return try {
            ComposeView(this).apply {
                setViewTreeLifecycleOwner(this@KepadEnpitMelxod)
                setViewTreeViewModelStoreOwner(this@KepadEnpitMelxod)
                setViewTreeSavedStateRegistryOwner(this@KepadEnpitMelxod)
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                setContent {
                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        val screenWidth = maxWidth
                        val screenHeight = maxHeight
                        var isEditingKepad by remember { mutableStateOf(false) }

                        LaunchedEffect(Unit) {
                            firebaseSirves.watcModjilLeyawt("current").collect { updatedModules ->
                                if (updatedModules.isNotEmpty()) {
                                    Log.d(TAG, "Keyboard loaded initial layout from Firebase/Local")
                                    val (normalizedMods, modified) = daylSteyt.normalizeLayout(updatedModules, "current")
                                    if (modified) {
                                        scope.launch { firebaseSirves.seyvModjilLeyawt(normalizedMods, "current") }
                                    }
                                    daylSteyt.updateModules(normalizedMods)
                                    
                                    // AUTO-ACTIVATE: If nothing is active, find the first keypad module (not dayl) and show it!
                                    if (daylSteyt.activeModule == null) {
                                        val firstKepad = updatedModules.find { it.type == "kepad" && it.id != "dayl" }
                                        if (firstKepad != null) {
                                            Log.d(TAG, "Auto-activating module: ${firstKepad.neym}")
                                            daylSteyt.akdeveytModyil(firstKepad.id)
                                        }
                                    }
                                }
                            }
                        }

                        val activeMod = daylSteyt.modyilz.find { it.ezAkdev && it.type == "kepad" && it.id != "dayl" } ?: daylSteyt.modyilz.find { it.type == "kepad" && it.id != "dayl" } ?: return@BoxWithConstraints
                        val activeIndices = remember(activeMod.glefs) {
                            val set: MutableSet<Int> = activeMod.glefs.mapIndexedNotNull { i: Int, s: String -> if (s.isNotEmpty()) i else null }.toMutableSet()
                            set.add(0)
                            set.toList().sorted()
                        }

                        val gredDimz = remember(activeIndices, screenWidth, screenHeight) {
                            HeksagonDjeyometre.kalkyuleytGredDimenzconz(
                                activeIndices = activeIndices,
                                screenWidth = screenWidth.value.toDouble(),
                                screenHeight = screenHeight.value.toDouble(),
                                isWearOS = yuteledez.isWearOS,
                                ezKepad = true
                            )
                        }

                        val geometry = remember(gredDimz) {
                            HeksagonDjeyometre(
                                heksSayz = gredDimz.heksSayz,
                                sentir = HeksagonPozecon(-gredDimz.unitCenterX * gredDimz.heksSayz, -gredDimz.unitCenterY * gredDimz.heksSayz),
                                ezLeterMod = true
                            )
                        }

                        val density = resources.displayMetrics.density
                        val hexHeightDp = (gredDimz.heksSayz * 2.0).dp
                        val bottomShiftDp = hexHeightDp * 0.5f
                        val topReductionDp = hexHeightDp * 0.333f // 1/3 less overlap at top
                        
                        // To keep hit-testing correct, we compute the expanded box dimensions
                        val shiftUpDp = hexHeightDp * 1.5f

                        val localView = LocalView.current
                        val parentView = localView.parent as? android.view.View

                        LaunchedEffect(geometry, gredDimz, parentView?.height) {
                            val originalGridHeightPx = (gredDimz.height * density).toInt()
                            val bottomShiftPx = (bottomShiftDp.value * density).toInt()
                            val topReductionPx = (topReductionDp.value * density).toInt()
                            
                            dynamicGridHeightPx = originalGridHeightPx
                            dynamicGridWidthPx = (gredDimz.width * density).toInt()
                            hexSayzDp = gredDimz.heksSayz.toFloat()
                            
                            // App needs to be pushed up to accommodate the bottom shift + top overlap reduction
                            dynamicAdjustedHeightPx = originalGridHeightPx + bottomShiftPx + topReductionPx
                            
                            // The visual center of the grid from the screen's top
                            val screenHeight = parentView?.height ?: 0
                            if (screenHeight > 0) {
                                dynamicGridCenterYPx = screenHeight - bottomShiftPx - originalGridHeightPx / 2f
                            }
                            
                            // Compute all displayed hex centers in dp (relative to box center)
                            val maxIdx = activeMod.glefs.size - 1
                            var rings = 1
                            while (3 * rings * (rings + 1) < maxIdx) rings++
                            val displayRings = maxOf(2, rings)
                            val centers = mutableListOf<Pair<Float, Float>>()
                            centers.add(Pair(geometry.sentir.x.toFloat(), geometry.sentir.y.toFloat()))
                            for (r in 1..displayRings) {
                                geometry.getKowordenatsForRenq(r).forEach { coord ->
                                    val p = geometry.aksyalTuPeksel(coord.q, coord.r)
                                    centers.add(Pair(p.x.toFloat(), p.y.toFloat()))
                                }
                            }
                            hexCentersDp = centers
                            // Force onComputeInsets to re-run with the correct hex positions
                            localView.post { localView.requestLayout() }
                        }

                        // Expand Box bounds to capture touches on protruding hexagons (overlap strip).
                        // By increasing height symmetrically (x2) and offsetting, the visual center remains exactly the same.
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(gredDimz.height.dp + shiftUpDp * 2)
                                .offset(y = shiftUpDp - bottomShiftDp)
                                .align(Alignment.BottomCenter),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                        KepadModyil(
                                kebordKontrolir = kebordKontrolir,
                                platformServices = platfOrmSirvesez,
                                voiceService = voiceService,
                                ezLeterMod = ezLeterMod,
                                ezPunkcuweyconMod = ezPunkcuweyconMod,
                                ezUpsayddawn = ezUpsayddawn,
                                onTogilMod = {
                                    if (!isNumberField) {
                                        ezLeterMod = !ezLeterMod
                                    }
                                },
                                onSetPunkcuweyconMod = { ezPunkcuweyconMod = it },
                                ezAngolMod = angolSpelenqMod > 0,
                                onTogilAngol = { voiceService.togilAngolMod(it) }, 
                                onStartAiVoys = { voiceService.startListening(isAiMode = true) },
                                ignoreSelectionUpdate = { ignoreSelectionUpdateCount++ },
                                geometryOverride = geometry,
                                glefsOvirayd = activeMod.glefs,
                                kulorzOverride = activeMod.glefKulorz,
                                sekondGlefsOverride = activeMod.sekondGlefs,
                                contentWidthDp = gredDimz.width.dp,
                                neym = activeMod.id,
                                isEditing = isEditingKepad,
                                onMove = { from, to ->
                                    if (to == -1) daylSteyt.muvGlefTuHub(activeMod.id, from, isCopy = false)
                                    else daylSteyt.muvGlef(activeMod.id, from, to)
                                    scope.launch { firebaseSirves.seyvModjilLeyawt(daylSteyt.modyilz, "current") }
                                },
                                onCopyToEmpty = { from, to ->
                                    if (to == -1) daylSteyt.muvGlefTuHub(activeMod.id, from, isCopy = true)
                                    else daylSteyt.kopeGlefTuEmpt(activeMod.id, from, to)
                                    scope.launch { firebaseSirves.seyvModjilLeyawt(daylSteyt.modyilz, "current") }
                                },
                                onDelete = { from ->
                                    daylSteyt.muvGlef(activeMod.id, from, -1)
                                    scope.launch { firebaseSirves.seyvModjilLeyawt(daylSteyt.modyilz, "current") }
                                },
                                onMuvTuSentir = { from, isMove ->
                                    daylSteyt.muvGlefTuHub(activeMod.id, from, isCopy = !isMove)
                                    scope.launch { firebaseSirves.seyvModjilLeyawt(daylSteyt.modyilz, "current") }
                                },
                                onRepleys = { from, to, isMove, _ ->
                                    if (to == -1) daylSteyt.muvGlefTuHub(activeMod.id, from, isCopy = !isMove)
                                    else daylSteyt.repleysGlef(activeMod.id, from, to)
                                    scope.launch { firebaseSirves.seyvModjilLeyawt(daylSteyt.modyilz, "current") }
                                },
                                onReset = {
                                    if (isEditingKepad) {
                                        isEditingKepad = false
                                    } else {
                                        daylSteyt.pendingResetTargetId = activeMod.id
                                    }
                                },
                                onKloz = if (isEditingKepad) {
                                    { isEditingKepad = false }
                                } else if (daylSteyt.tempNestedMod != null) {
                                    { daylSteyt.closeNestedMod() }
                                } else null,
                                onTapGlef = { label ->
                                    if (label.contains("|")) {
                                        val deserialized = daylSteyt.deserializeMod(label)
                                        if (deserialized != null) {
                                            daylSteyt.openNestedMod(deserialized)
                                        }
                                    }
                                }
                            )
                        }
                        
                        if (daylSteyt.pendingResetTargetId != null) {
                            val targetId = daylSteyt.pendingResetTargetId!!
                            val targetMod = daylSteyt.modyilz.find { it.id == targetId }
                            val targetNeym = targetMod?.neym ?: targetId
                            Box(
                                modifier = Modifier.fillMaxSize().background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.5f)).klekabil { daylSteyt.pendingResetTargetId = null },
                                contentAlignment = Alignment.Center
                            ) {
                                val resetItems = listOf(
                                    wedjets.GredUydem(index = 6, label = "undu", color = androidx.compose.ui.graphics.Color(0xFF404040)),    // Top Left
                                    wedjets.GredUydem(index = 1, label = "redu", color = androidx.compose.ui.graphics.Color(0xFF404040)),    // Top Right
                                    wedjets.GredUydem(index = 4, label = "restor", color = androidx.compose.ui.graphics.Color(0xFF404040)),  // Bottom Left
                                    wedjets.GredUydem(index = 3, label = "repleys", color = androidx.compose.ui.graphics.Color(0xFF404040))  // Bottom Right
                                )
                                
                                wedjets.HeksagonGred(
                                    geometry = geometry,
                                    items = resetItems,
                                    sentirLeybil = targetNeym,
                                    centerColor = androidx.compose.ui.graphics.Color.Red,
                                    onMove = { _, _ -> },
                                    onCopyToEmpty = { _, _ -> },
                                    onMuvTuSentir = { _, _ -> },
                                    onDropOnFoldir = { _, _, _ -> },
                                    onTap = { index ->
                                        when (index) {
                                            6 -> { 
                                                daylSteyt.unduModyil(targetId)
                                                scope.launch { firebaseSirves.seyvModjilLeyawt(daylSteyt.modyilz, "current") }
                                            }
                                            1 -> { 
                                                daylSteyt.reduModyil(targetId)
                                                scope.launch { firebaseSirves.seyvModjilLeyawt(daylSteyt.modyilz, "current") }
                                            }
                                        }
                                    },
                                    onLonqPresUydem = { index ->
                                        when (index) {
                                            3 -> {
                                                // Long pressing repleys in Kepad IME toggles editing mode so they can move keys
                                                isEditingKepad = true
                                                daylSteyt.pendingResetTargetId = null
                                            }
                                            4 -> {
                                                if (targetId == "angol") {
                                                    daylSteyt.reset()
                                                } else {
                                                    daylSteyt.resetModyilTarget(targetId)
                                                }
                                                scope.launch { firebaseSirves.seyvModjilLeyawt(daylSteyt.modyilz, "current") }
                                                daylSteyt.pendingResetTargetId = null
                                            }
                                        }
                                    },
                                    fontSizeFactor = 12f/12f,
                                    centerFontSizeFactor = 10f/12f,
                                    ezKonsestentSayz = false,
                                    centerEzKonsestentSayz = false,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
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

        val inputClass = info?.inputType?.and(android.text.InputType.TYPE_MASK_CLASS)
        isNumberField = inputClass == android.text.InputType.TYPE_CLASS_NUMBER ||
                        inputClass == android.text.InputType.TYPE_CLASS_PHONE
        if (isNumberField) {
            ezLeterMod = false
            ezPunkcuweyconMod = false
        } else {
            ezLeterMod = true
            ezPunkcuweyconMod = false
        }

        window?.window?.let { win ->
            win.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            win.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            win.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
            win.navigationBarColor = android.graphics.Color.TRANSPARENT
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                win.setDecorFitsSystemWindows(false)
            }
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
        // Use dynamic height if available, otherwise fallback to massive scale floor
        val adjustedHeightPx = if (dynamicAdjustedHeightPx > 0) {
            dynamicAdjustedHeightPx
        } else {
            val clusterHeightPx = if (dynamicGridHeightPx > 0) {
                dynamicGridHeightPx
            } else {
                val screenWidth = resources.displayMetrics.widthPixels
                val hexSizePx = screenWidth / (2.0 * 2.6 + 1.0) / kotlin.math.sqrt(3.0)
                (hexSizePx * 6.0).toInt()
            }
            clusterHeightPx
        }

        val top = totalHeight - adjustedHeightPx
        outInsets.contentTopInsets = top
        outInsets.visibleTopInsets = top
        outInsets.touchableInsets = Insets.TOUCHABLE_INSETS_REGION
        outInsets.touchableRegion.setEmpty()

        val density = resources.displayMetrics.density
        val hexSizePx = if (hexSayzDp > 0f) hexSayzDp * density
        else (resources.displayMetrics.widthPixels / (2.0 * 2.6 + 1.0) / kotlin.math.sqrt(3.0)).toFloat()

        if (hexCentersDp.isNotEmpty()) {
            // Exact hexagonal touch regions â€” one per visible hexagon
            val gridCenterX = totalWidth / 2f
            val gridCenterY = if (dynamicGridCenterYPx > 0f) dynamicGridCenterYPx else top + adjustedHeightPx / 2f
            val combinedPath = android.graphics.Path()
            hexCentersDp.forEach { (cxDp, cyDp) ->
                val cx = gridCenterX + cxDp * density
                val cy = gridCenterY + cyDp * density
                val hexPath = android.graphics.Path()
                for (i in 0..5) {
                    // Pointy-top hex: vertices at 30Â°, 90Â°, 150Â°, 210Â°, 270Â°, 330Â°
                    val rad = Math.toRadians(60.0 * i + 30.0)
                    val vx = (cx + hexSizePx * kotlin.math.cos(rad)).toFloat()
                    val vy = (cy + hexSizePx * kotlin.math.sin(rad)).toFloat()
                    if (i == 0) hexPath.moveTo(vx, vy) else hexPath.lineTo(vx, vy)
                }
                hexPath.close()
                combinedPath.addPath(hexPath)
            }
            val boundsF = android.graphics.RectF()
            combinedPath.computeBounds(boundsF, true)
            val clipRegion = android.graphics.Region(
                (boundsF.left - 1).toInt(), (boundsF.top - 1).toInt(),
                (boundsF.right + 1).toInt(), (boundsF.bottom + 1).toInt()
            )
            val hexRegion = android.graphics.Region()
            hexRegion.setPath(combinedPath, clipRegion)
            outInsets.touchableRegion.op(hexRegion, android.graphics.Region.Op.UNION)
        } else {
            // Fallback: bounding rect of keyboard + overlap strip
            val touchTop = (top - hexSizePx * 2).toInt().coerceAtLeast(0)
            outInsets.touchableRegion.union(android.graphics.Rect(0, touchTop, totalWidth, totalHeight))
        }
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
        layoutUpdateReceiver?.let { unregisterReceiver(it) }
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        viewModelStore.clear()
        speechRecognizer?.destroy()
    }
}




