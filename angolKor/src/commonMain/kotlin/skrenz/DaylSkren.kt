package skrenz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.geometry.Offset
import modyilz.DaylModal
import modyilz.KepadModyil
import steyt.DaylSteyt
import yuteledez.HeksagonDjeyometre
import yuteledez.GredDimenzconz
import modalz.HeksagonPozecon
import kotlin.math.sqrt

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun DaylSkrenEntry(
    keyboardController: modyilz.KeyboardController?,
    platformServices: modyilz.PlatformServices,
    voiceService: modyilz.VoiceService,
    ezLeterMod: Boolean = true,
    ezPunkcuweyconMod: Boolean = false,
    ezUpsayddawn: Boolean = false,
    onTogilMod: () -> Unit = {},
    onSetPunkcuweyconMod: (Boolean) -> Unit = {},
    ezAngolMod: Boolean = true,
    onTogilAngol: (Boolean) -> Unit = {},
    onStartAiVoys: () -> Unit = {},
    ignoreSelectionUpdate: () -> Unit = {},
    firebaseSirves: sirvesez.FirebaseSirves? = null,
    isApp: Boolean = false,
    daylSteyt: DaylSteyt = remember { DaylSteyt() }
) {
    val userState = firebaseSirves?.authStateChanges?.collectAsState(initial = firebaseSirves.currentUser)
    val user = userState?.value
    var kurentSkren by remember { mutableStateOf("main") }
    var ezGestMod by remember { mutableStateOf(false) }

    if (isApp) {
        if (user == null && !ezGestMod && firebaseSirves != null) {
            SaynEnSkren(firebaseSirves, onBypass = { ezGestMod = true })
        } else {
            when (kurentSkren) {
                "main" -> DaylSkren(
                    keyboardController, platformServices, voiceService,
                    ezLeterMod, ezPunkcuweyconMod, ezUpsayddawn, onTogilMod, onSetPunkcuweyconMod,
                    ezAngolMod, onTogilAngol, onStartAiVoys, ignoreSelectionUpdate,
                    firebaseSirves, isApp = true, daylSteyt = daylSteyt,
                    onGoToHome = { kurentSkren = "home" }
                )
                "home" -> if (firebaseSirves != null) AfdirLogenSkren(firebaseSirves, onContinue = { kurentSkren = "main" }) else DaylSkren(
                    keyboardController, platformServices, voiceService,
                    ezLeterMod, ezPunkcuweyconMod, ezUpsayddawn, onTogilMod, onSetPunkcuweyconMod,
                    ezAngolMod, onTogilAngol, onStartAiVoys, ignoreSelectionUpdate,
                    firebaseSirves, isApp = true, daylSteyt = daylSteyt
                )
            }
        }
    } else {
        DaylSkren(
            keyboardController, platformServices, voiceService,
            ezLeterMod, ezPunkcuweyconMod, ezUpsayddawn, onTogilMod, onSetPunkcuweyconMod,
            ezAngolMod, onTogilAngol, onStartAiVoys, ignoreSelectionUpdate,
            firebaseSirves, isApp = false, daylSteyt = daylSteyt
        )
    }
}

@Composable
fun DaylSkren(
    keyboardController: modyilz.KeyboardController?,
    platformServices: modyilz.PlatformServices,
    voiceService: modyilz.VoiceService,
    ezLeterMod: Boolean,
    ezPunkcuweyconMod: Boolean,
    ezUpsayddawn: Boolean,
    onTogilMod: () -> Unit,
    onSetPunkcuweyconMod: (Boolean) -> Unit,
    ezAngolMod: Boolean,
    onTogilAngol: (Boolean) -> Unit,
    onStartAiVoys: () -> Unit,
    ignoreSelectionUpdate: () -> Unit,
    firebaseSirves: sirvesez.FirebaseSirves? = null,
    isApp: Boolean = true,
    onGoToHome: (() -> Unit)? = null,
    daylSteyt: DaylSteyt = remember { DaylSteyt() }
) {
    val scope = rememberCoroutineScope()
    
    // Auth and Layout Sync
    val userState = firebaseSirves?.authStateChanges?.collectAsState(initial = firebaseSirves.currentUser)
    val user = userState?.value
    var kurentEnv by remember { mutableStateOf("current") }

    val saveLayout: (String) -> Unit = { env ->
        if (firebaseSirves != null) {
            scope.launch {
                firebaseSirves.saveModuleLayout(daylSteyt.modyilz, env)
            }
        }
    }
    
    LaunchedEffect(firebaseSirves, isApp, user, kurentEnv) {
        val fs = firebaseSirves ?: return@LaunchedEffect
        fs.watchModuleLayout(kurentEnv).collectLatest { remoteModules ->
            if (remoteModules.isNotEmpty()) {
                daylSteyt.updateModules(remoteModules)
                if (!isApp) {
                    // Only force activation if nothing is currently active in the synced data
                    if (daylSteyt.activeModule == null) {
                        daylSteyt.activateModyil("keypad")
                    }
                }
            }
        }
    }
    
    LaunchedEffect(isApp) {
        if (!isApp && daylSteyt.activeModule == null) {
            daylSteyt.activateModyil("keypad")
        }
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight
        
        // Hub and Keypad scaling: Unified sizing logic
        val activeMod = daylSteyt.activeModule
        val activeIndices = remember(activeMod?.glefz) {
            val indices = activeMod?.glefz?.mapIndexedNotNull { i, s -> if (s.isNotEmpty()) i else null }?.toMutableSet() ?: mutableSetOf()
            indices.add(0)
            indices.toList().sorted()
        }

        val gredDimz = remember(activeIndices, screenWidth, screenHeight) {
            HeksagonDjeyometre.kalkyuleytGredDimenzconz(
                activeIndices = activeIndices,
                screenWidth = screenWidth.value.toDouble(),
                screenHeight = screenHeight.value.toDouble(),
                isWearOS = yuteledez.isWearOS
            )
        }

        val hexSize = gredDimz.heksSayz.dp

        val geometry = remember(gredDimz, isApp, activeIndices) {
            val hexWidth = gredDimz.heksSayz * sqrt(3.0)
            val isLandscape = screenWidth > screenHeight
            
            // For App/Hub, we center it. For IME in landscape, we might shift it.
            val baseSentirX = if (!isApp && isLandscape) {
                -screenWidth.value / 2.0 + (if (yuteledez.isWearOS) 1.5 else 0.5) * hexWidth
            } else {
                -gredDimz.unitCenterX * gredDimz.heksSayz
            }

            HeksagonDjeyometre(
                heksSayz = gredDimz.heksSayz,
                sentir = HeksagonPozecon(baseSentirX, -gredDimz.unitCenterY * gredDimz.heksSayz - (gredDimz.heksSayz * 2.0 / 12.0)),
                ezLeterMod = true
            )
        }

        // Limit the height of the IME container to fit the keyboard perfectly
        val contentModifier = if (isApp) {
            Modifier.fillMaxSize().background(
                Brush.radialGradient(
                    0f/12f to Color(0xFF1A1A2E),
                    6f/12f to Color(0xFF0F0F1E),
                    12f/12f to Color.Black,
                    radius = 24000f/12f
                )
            )
        } else {
            // Precise height with no extra margins
            Modifier.fillMaxWidth().height(gredDimz.height.dp)
                .align(Alignment.BottomCenter)
        }

        // Absolute centering/bottom alignment for the Hub/Keypad
        Box(
            modifier = contentModifier, 
            contentAlignment = if (isApp) Alignment.Center else Alignment.BottomCenter
        ) {
            ModuleContent(
                daylSteyt = daylSteyt,
                geometry = geometry,
                keyboardController = keyboardController,
                platformServices = platformServices,
                voiceService = voiceService,
                ezLeterMod = ezLeterMod,
                ezPunkcuweyconMod = ezPunkcuweyconMod,
                ezUpsayddawn = ezUpsayddawn,
                onTogilMod = onTogilMod,
                onSetPunkcuweyconMod = onSetPunkcuweyconMod,
                ezAngolMod = ezAngolMod,
                onTogilAngol = onTogilAngol,
                onStartAiVoys = onStartAiVoys,
                ignoreSelectionUpdate = ignoreSelectionUpdate,
                onSaveLayout = saveLayout,
                screenWidth = screenWidth,
                screenHeight = if (isApp) screenHeight else gredDimz.height.dp,
                contentWidth = if (isApp) screenWidth else gredDimz.width.dp,
                isApp = isApp
            )
        }
        
        // Settings and Account icons at the bottom
        if (!daylSteyt.ezKepadVezebil && isApp) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                IconButton(onClick = { platformServices.openSettings() }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.White.copy(alpha = 6f / 12f),
                        modifier = Modifier.size(32.dp)
                    )
                }
                IconButton(onClick = { onGoToHome?.invoke() }) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Account",
                        tint = Color.White.copy(alpha = 6f / 12f),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ModuleContent(
    daylSteyt: DaylSteyt,
    geometry: HeksagonDjeyometre,
    keyboardController: modyilz.KeyboardController?,
    platformServices: modyilz.PlatformServices,
    voiceService: modyilz.VoiceService,
    ezLeterMod: Boolean,
    ezPunkcuweyconMod: Boolean,
    ezUpsayddawn: Boolean,
    onTogilMod: () -> Unit,
    onSetPunkcuweyconMod: (Boolean) -> Unit,
    ezAngolMod: Boolean,
    onTogilAngol: (Boolean) -> Unit,
    onStartAiVoys: () -> Unit,
    ignoreSelectionUpdate: () -> Unit,
    onSaveLayout: (String) -> Unit,
    screenWidth: androidx.compose.ui.unit.Dp,
    screenHeight: androidx.compose.ui.unit.Dp,
    contentWidth: androidx.compose.ui.unit.Dp = screenWidth,
    isApp: Boolean
) {
    val activeMod = daylSteyt.activeModule
    val currentType = activeMod?.type ?: if (!isApp) "keypad" else "hub"
    
    when {
        currentType == "keypad" -> {
            val mod = activeMod ?: daylSteyt.modyilz.find { it.type == "keypad" } ?: return
            KepadModyil(
                keyboardController = keyboardController,
                platformServices = platformServices,
                voiceService = voiceService,
                ezLeterMod = ezLeterMod,
                ezPunkcuweyconMod = ezPunkcuweyconMod,
                ezUpsayddawn = ezUpsayddawn,
                onTogilMod = onTogilMod,
                onSetPunkcuweyconMod = onSetPunkcuweyconMod,
                ezAngolMod = ezAngolMod,
                onTogilAngol = onTogilAngol,
                onStartAiVoys = onStartAiVoys,
                ignoreSelectionUpdate = ignoreSelectionUpdate,
                onClose = { 
                    if (isApp) {
                        daylSteyt.togilModyil(mod.pozecon)
                        onSaveLayout("current")
                    }
                },
                geometryOverride = geometry,
                glefzOverride = mod.glefz,
                kulorzOverride = mod.glefKulorz,
                contentWidthDp = contentWidth
            )
        }
        currentType == "beld" || currentType == "builder" -> modyilz.BeldModyil(
            daylSteyt = daylSteyt,
            onClose = { 
                daylSteyt.togilModyil(activeMod!!.pozecon)
                onSaveLayout("current")
            },
            onAction = onSaveLayout
        )
        else -> DaylModal(
            geometry = geometry,
            modyilz = daylSteyt.modyilz,
            onToggleModule = { index ->
                if (index == 0) {
                    daylSteyt.togilModyil(1)
                } else {
                    daylSteyt.togilModyil(index + 1)
                }
                onSaveLayout("current")
            },
            onMoveModule = { from, to ->
                daylSteyt.swopModyilz(from + 1, to + 1)
                onSaveLayout("current")
            },
            onCopyToEmpty = { from, to ->
                // UI is 0-based; state is 1-based.
                daylSteyt.kopeModyilTuEmpt(from + 1, to + 1)
                onSaveLayout("current")
            },
            onMoveToCenter = { from ->
                // UI is 0-based; state is 1-based.
                daylSteyt.muvModyilTuParent(from + 1)
                onSaveLayout("current")
            },
            stackWidth = screenWidth,
            stackHeight = screenHeight,
            allowSwap = false
        )
    }
}
