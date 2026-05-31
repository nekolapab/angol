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
import modyilz.DaylModal
import modyilz.KepadModyil
import modyilz.Beldir
import steyt.DaylSteyt
import modyilz.PlatformServices
import modyilz.VoiceService
import modyilz.KeyboardController
import sirvesez.FirebaseSirves
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
    val userState = firebaseSirves?.authStateChanges?.collectAsState(initial = firebaseSirves.currentUser)

    val saveLayout: (String) -> Unit = { env ->
        if (firebaseSirves != null) {
            scope.launch {
                firebaseSirves.saveModuleLayout(daylSteyt.modyilz, "current")
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
        
        val activeMod = daylSteyt.activeModule
        val activeIndices = remember(activeMod?.glefz) {
            val indices = activeMod?.glefz?.mapIndexedNotNull { i, s -> if (s.isNotEmpty()) i else null }?.toMutableSet() ?: mutableSetOf()
            indices.add(0)
            indices.toList().sorted()
        }

        val gredDimz = remember(activeIndices, screenWidth, screenHeight) {
            yuteledez.HeksagonDjeyometre.kalkyuleytGredDimenzconz(
                activeIndices = activeIndices,
                screenWidth = screenWidth.value.toDouble(),
                screenHeight = screenHeight.value.toDouble(),
                isWearOS = yuteledez.isWearOS
            )
        }

        val geometry = remember(gredDimz, isApp, activeIndices) {
            val hexWidth = gredDimz.heksSayz * sqrt(3.0)
            val isLandscape = screenWidth > screenHeight
            
            val isHub = activeMod == null || activeMod.type == "hub"
            val baseSentirX = if (isHub) 0.0 else if (!isApp && isLandscape) {
                -screenWidth.value / 2.0 + (if (yuteledez.isWearOS) 1.5 else 0.5) * hexWidth
            } else {
                -gredDimz.unitCenterX * gredDimz.heksSayz
            }
            val baseSentirY = if (isHub) 0.0 else -gredDimz.unitCenterY * gredDimz.heksSayz - (gredDimz.heksSayz * 2.0 / 12.0)

            HeksagonDjeyometre(
                heksSayz = gredDimz.heksSayz,
                sentir = HeksagonPozecon(baseSentirX, baseSentirY),
                ezLeterMod = true
            )
        }

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
            Modifier.fillMaxWidth().height(gredDimz.height.dp).align(Alignment.BottomCenter)
        }

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
        
        if (!daylSteyt.ezKepadVezebil && isApp) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                IconButton(onClick = { platformServices.openSettings() }) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings", tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(32.dp))
                }
                IconButton(onClick = { onGoToHome?.invoke() }) {
                    Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "Account", tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(32.dp))
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
                contentWidthDp = contentWidth,
                onMove = { from, to ->
                    daylSteyt.muvGlef(mod.id, from, to)
                    onSaveLayout("current")
                },
                onDropOnFoldir = { from, to ->
                    daylSteyt.muvModyilEntuFoldir(from, to)
                    onSaveLayout("current")
                },
                onReplace = { from, to ->
                    daylSteyt.replaceGlef(mod.id, from, to)
                    onSaveLayout("current")
                }
            )
        }
        currentType == "beld" || currentType == "builder" || currentType == "rebeld" -> Beldir(
            daylSteyt = daylSteyt,
            keyboardController = keyboardController,
            platformServices = platformServices,
            voiceService = voiceService,
            onClose = { 
                daylSteyt.togilModyil(activeMod!!.pozecon)
                onSaveLayout("current")
            },
            onAction = onSaveLayout,
            onDropOnFoldir = { from, to ->
                daylSteyt.muvBeldirModyilEntuFoldir(from, to)
                onSaveLayout("current")
            },
            onReplace = { from, to ->
                daylSteyt.replaceBeldirModyil(from, to)
                onSaveLayout("current")
            }
        )
        else -> DaylModal(
            geometry = geometry,
            modyilz = daylSteyt.modyilz,
            onToggleModule = { index ->
                if (index == 0) daylSteyt.togilModyil(1)
                else daylSteyt.togilModyil(index + 1)
                onSaveLayout("current")
            },
            onMoveModule = { from, to ->
                daylSteyt.swopModyilz(from + 1, to + 1)
                onSaveLayout("current")
            },
            onDropOnFoldir = { from, to ->
                daylSteyt.muvModyilEntuFoldir(from, to)
                onSaveLayout("current")
            },
            onCopyToEmpty = { from, to ->
                daylSteyt.kopeModyilTuEmpt(from + 1, to + 1)
                onSaveLayout("current")
            },
            onMoveToCenter = { from ->
                daylSteyt.muvModyilTuParent(from + 1)
                onSaveLayout("current")
            },
            stackWidth = screenWidth,
            stackHeight = screenHeight,
            allowSwap = false,
            onReplace = { from, to ->
                daylSteyt.replaceModyil(from + 1, to + 1)
                onSaveLayout("current")
            }
        )
    }
}
