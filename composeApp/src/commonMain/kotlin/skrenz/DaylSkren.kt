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
import modyilz.DaylModyil
import modyilz.KepadModyil
import steyt.DaylSteyt
import yuteledez.HeksagonDjeyometre
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
    isApp: Boolean = false
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
                    firebaseSirves, isApp = true
                )
                "home" -> if (firebaseSirves != null) AfdirLogenSkren(firebaseSirves, onContinue = { kurentSkren = "main" }) else DaylSkren(
                    keyboardController, platformServices, voiceService,
                    ezLeterMod, ezPunkcuweyconMod, ezUpsayddawn, onTogilMod, onSetPunkcuweyconMod,
                    ezAngolMod, onTogilAngol, onStartAiVoys, ignoreSelectionUpdate,
                    firebaseSirves, isApp = true
                )
            }
        }
    } else {
        DaylSkren(
            keyboardController, platformServices, voiceService,
            ezLeterMod, ezPunkcuweyconMod, ezUpsayddawn, onTogilMod, onSetPunkcuweyconMod,
            ezAngolMod, onTogilAngol, onStartAiVoys, ignoreSelectionUpdate,
            firebaseSirves, isApp = false
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
    onGoToHome: (() -> Unit)? = null
) {
    val daylSteyt = remember { DaylSteyt() }
    val scope = rememberCoroutineScope()
    
    // Auth and Layout Sync
    val userState = firebaseSirves?.authStateChanges?.collectAsState(initial = firebaseSirves.currentUser)
    val user = userState?.value

    val saveLayout = {
        if (user != null && firebaseSirves != null) {
            scope.launch {
                firebaseSirves.saveModuleLayout(daylSteyt.modyilz)
            }
        }
    }
    
    LaunchedEffect(user) {
        if (user != null && firebaseSirves != null) {
            firebaseSirves.watchModuleLayout().collectLatest { remoteModules ->
                if (remoteModules.isNotEmpty()) {
                    daylSteyt.updateModules(remoteModules)
                }
            }
        }
    }
    
    LaunchedEffect(isApp) {
        if (!isApp) {
            daylSteyt.togilModyil(1) // Toggle keypad (id="keypad", pozecon=1)
        }
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight
        
        val hexSize = minOf(
            screenWidth.value / (5.0 * sqrt(3.0)),
            screenHeight.value / 10.0 // Safer 10-radii height
        ).dp

        val geometry = remember(hexSize, screenWidth, screenHeight, isApp) {
            val hexWidth = hexSize.value * sqrt(3.0)
            val isLandscape = screenWidth > screenHeight
            val sentirX = if (!isApp && isLandscape) {
                -screenWidth.value / 2.0 + 2.6666 * hexWidth
            } else {
                0.0
            }

            HeksagonDjeyometre(
                heksSayz = hexSize.value.toDouble(),
                sentir = HeksagonPozecon(sentirX, 0.0),
                ezLeterMod = true
            )
        }

        // Limit the height of the IME container to fit the keyboard perfectly (8 radii tall)
        val contentModifier = if (isApp) {
            Modifier.fillMaxSize().background(
                Brush.radialGradient(
                    0.0f to Color(0xFF1A1A2E),
                    0.5f to Color(0xFF0F0F1E),
                    1.0f to Color.Black,
                    radius = 2000f
                )
            )
        } else {
            Modifier.fillMaxWidth().height(hexSize * 8.0f)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        0.0f to Color.Black.copy(alpha = 0.7f),
                        1.0f to Color.Black
                    )
                )
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
                screenHeight = if (isApp) screenHeight else hexSize * 8f
            )
        }
        
        // Settings and Account icons at the bottom
        if (!daylSteyt.ezKepadVezebil) {
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
                        tint = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(32.dp)
                    )
                }
                if (isApp) {
                    IconButton(onClick = { onGoToHome?.invoke() }) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Account",
                            tint = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(32.dp)
                        )
                    }
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
    onSaveLayout: () -> Unit,
    screenWidth: androidx.compose.ui.unit.Dp,
    screenHeight: androidx.compose.ui.unit.Dp
) {
    val activeMod = daylSteyt.activeModule
    
    when (activeMod?.id) {
        "keypad" -> KepadModyil(
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
            geometryOverride = geometry,
            glefzOverride = activeMod.glefz
        )
        "beldir" -> modyilz.BeldirModyil(
            daylSteyt = daylSteyt,
            onClose = { 
                daylSteyt.togilModyil(activeMod.pozecon)
                onSaveLayout()
            },
            onAction = onSaveLayout
        )
        else -> DaylModyil(
            geometry = geometry,
            modyilz = daylSteyt.modyilz,
            onToggleModule = { index ->
                if (index == 0) {
                    daylSteyt.togilModyil(1)
                } else {
                    daylSteyt.togilModyil(index + 1)
                }
                onSaveLayout()
            },
            onMoveModule = { from, to ->
                daylSteyt.swopModyilz(from + 1, to + 1)
                onSaveLayout()
            },
            onCopyToEmpty = { from, to ->
                // UI is 0-based; state is 1-based.
                daylSteyt.kopeModyilTuEmpt(from + 1, to + 1)
                onSaveLayout()
            },
            onMoveToCenter = { from ->
                // UI is 0-based; state is 1-based.
                daylSteyt.muvModyilTuParent(from + 1)
                onSaveLayout()
            },
            stackWidth = screenWidth,
            stackHeight = screenHeight
        )
    }
}
