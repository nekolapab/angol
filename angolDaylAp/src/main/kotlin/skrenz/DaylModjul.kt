package skrenz
import yuteledez.padenq

import androidx.compose.foundation.background
import yuteledez.klekabil
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
import androidx.compose.ui.unit.sp
import wedjets.DaylWedjet
import modjilz.KepadModyil
import modjilz.RebeldModjul
import steyt.AngolSteyt
import modjilz.PlatformServices
import modjilz.VoiceService
import modjilz.KeyboardController
import sirvesez.FirebaseSirves
import yuteledez.HeksagonDjeyometre
import modalz.HeksagonPozecon
import modalz.ModyilDeyda
import kotlin.math.sqrt

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun DaylSkrenEntre(
    kebordKontrolir: modjilz.KeyboardController?,
    platformServices: modjilz.PlatformServices,
    voiceService: modjilz.VoiceService,
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
    daylSteyt: AngolSteyt = remember { AngolSteyt() }
) {
    val userState = firebaseSirves?.authStateChanges?.collectAsState(initial = firebaseSirves.currentUser)
    val user = userState?.value
    var kurentSkren by remember { mutableStateOf("main") }
    var ezGestMod by remember { mutableStateOf(false) }

    var entirnalEzLedirMod by remember { mutableStateOf(ezLeterMod) }
    var entirnalEzPunkcuweyconMod by remember { mutableStateOf(ezPunkcuweyconMod) }

    val actualOnTogilMod = {
        entirnalEzLedirMod = !entirnalEzLedirMod
        onTogilMod()
    }
    val actualOnSetPunkcuweyconMod = { mode: Boolean ->
        entirnalEzPunkcuweyconMod = mode
        onSetPunkcuweyconMod(mode)
    }

    if (isApp) {
        if (user == null && !ezGestMod && firebaseSirves != null) {
            SaynEnSkren(firebaseSirves, onBypass = { ezGestMod = true })
        } else {
            when (kurentSkren) {
                "main" -> DaylModjul(
                    kebordKontrolir, platformServices, voiceService,
                    entirnalEzLedirMod, entirnalEzPunkcuweyconMod, ezUpsayddawn, actualOnTogilMod, actualOnSetPunkcuweyconMod,
                    ezAngolMod, onTogilAngol, onStartAiVoys, ignoreSelectionUpdate,
                    firebaseSirves, isApp = true, daylSteyt = daylSteyt,
                    onGoToHome = { kurentSkren = "home" }
                )
                "home" -> if (firebaseSirves != null) AfdirLogenSkren(firebaseSirves, onContinue = { kurentSkren = "main" }) else DaylModjul(
                    kebordKontrolir, platformServices, voiceService,
                    entirnalEzLedirMod, entirnalEzPunkcuweyconMod, ezUpsayddawn, actualOnTogilMod, actualOnSetPunkcuweyconMod,
                    ezAngolMod, onTogilAngol, onStartAiVoys, ignoreSelectionUpdate,
                    firebaseSirves, isApp = true, daylSteyt = daylSteyt
                )
            }
        }
    } else {
        DaylModjul(
            kebordKontrolir, platformServices, voiceService,
            entirnalEzLedirMod, entirnalEzPunkcuweyconMod, ezUpsayddawn, actualOnTogilMod, actualOnSetPunkcuweyconMod,
            ezAngolMod, onTogilAngol, onStartAiVoys, ignoreSelectionUpdate,
            firebaseSirves, isApp = false, daylSteyt = daylSteyt
        )
    }
}

@Composable
fun DaylModjul(
    kebordKontrolir: modjilz.KeyboardController?,
    platformServices: modjilz.PlatformServices,
    voiceService: modjilz.VoiceService,
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
    daylSteyt: AngolSteyt = remember { AngolSteyt() }
) {
    val scope = rememberCoroutineScope()
    val userState = firebaseSirves?.authStateChanges?.collectAsState(initial = firebaseSirves.currentUser)

    val seyvLeyawt: (String) -> Unit = { env ->
        if (firebaseSirves != null) {
            scope.launch {
                if (env == "rebeld_steyt") {
                    firebaseSirves.seyvModjilLeyawt(daylSteyt.rebeldModyilz, env)
                } else {
                    firebaseSirves.seyvModjilLeyawt(daylSteyt.modjilz, env)
                }
            }
        }
    }
    
    val saveLayoutRepleys: () -> Unit = {
        if (firebaseSirves != null) {
            scope.launch {
                firebaseSirves.seyvModjilLeyawt(daylSteyt.modjilz, "current", true)
            }
        }
    }
    

    var enecolLodDun by remember { mutableStateOf(false) }

    LaunchedEffect(isApp) {
        if (!isApp && daylSteyt.activeModule == null) {
            val firstKepad = daylSteyt.modjilz.find { it.type == "kepad" && it.id != "dayl" }
            if (firstKepad != null) daylSteyt.akdeveytModyil(firstKepad.id)
            else daylSteyt.akdeveytModyil("kepad")
        }
        if (isApp && firebaseSirves != null) {
            scope.launch {
                firebaseSirves.watcModjilLeyawt("current").collect { updatedModules ->
                    if (updatedModules.isNotEmpty()) {
                        var modified = false
                        var currentList = updatedModules.toMutableList()

                        if (isApp && !enecolLodDun) {
                            enecolLodDun = true
                            currentList = currentList.map { it.copyWith(ezAkdev = false) }.toMutableList()
                            modified = true
                        }

                        val (normalizedMods, layoutModified) = daylSteyt.normalizeLayout(currentList, "current")
                        var isModified = modified || layoutModified

                        daylSteyt.updateModules(normalizedMods)
                        if (isModified) {
                            firebaseSirves.seyvModjilLeyawt(normalizedMods, "current")
                        }
                    }
                }
            }
            scope.launch {
                firebaseSirves.watcModjilLeyawt("rebeld_steyt").collect { updatedModules ->
                    if (updatedModules.isNotEmpty()) {
                        var modified = false
                        var currentList = updatedModules.toMutableList()

                        val (normalizedMods, layoutModified) = daylSteyt.normalizeLayout(currentList, "rebeld_steyt")
                        var isModified = modified || layoutModified

                        daylSteyt.updeytRebeldModjilz(normalizedMods)
                        if (isModified) {
                            firebaseSirves.seyvModjilLeyawt(normalizedMods, "rebeld_steyt")
                        }
                    }
                }
            }
        }
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight
        
        val activeMod = daylSteyt.activeModule
        val activeIndices = remember(activeMod?.glefs) {
            val indices = activeMod?.glefs?.mapIndexedNotNull { i, s -> if (s.isNotEmpty()) i else null }?.toMutableSet() ?: mutableSetOf()
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
                ezLeterMod = true,
                roteyconAngol = daylSteyt.roteyconAngol
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
                kebordKontrolir = kebordKontrolir,
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
                onSaveLayout = seyvLeyawt,
                onRepleys = saveLayoutRepleys,
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
                    .fillMaxWidth()
                    .padenq(start = 32.dp, end = 32.dp, bottom = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
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
    daylSteyt: AngolSteyt,
    geometry: HeksagonDjeyometre,
    kebordKontrolir: modjilz.KeyboardController?,
    platformServices: modjilz.PlatformServices,
    voiceService: modjilz.VoiceService,
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
    onRepleys: () -> Unit = {},
    screenWidth: androidx.compose.ui.unit.Dp,
    screenHeight: androidx.compose.ui.unit.Dp,
    contentWidth: androidx.compose.ui.unit.Dp = screenWidth,
    isApp: Boolean
) {
    val activeMod = daylSteyt.activeModule
    val currentType = activeMod?.type ?: if (!isApp) "kepad" else "hub"
    Box(modifier = Modifier.fillMaxSize()) {
        when {
            activeMod?.id == "dayl" -> {
            val mod = activeMod!!
            modjilz.RebeldWedjet(
                daylSteyt = daylSteyt,
                mod = mod,
                path = "angol",
                onBack = {
                    daylSteyt.togilModyil(mod.pozecon)
                    onSaveLayout("current")
                },
                onReneymGlef = { index, label ->
                    daylSteyt.reneymGlef(mod.id, index, label)
                    onSaveLayout("current")
                },
                onMuvGlef = { from, to ->
                    daylSteyt.muvGlef(mod.id, from, to)
                    onSaveLayout("current")
                },
                onCopyToEmpty = { from, to ->
                    if (to == -1) {
                        daylSteyt.muvGlefTuHub(mod.id, from, isCopy = true)
                    } else {
                        daylSteyt.kopeGlefTuEmpt(mod.id, from, to)
                    }
                    onSaveLayout("current")
                },
                onMuvTuParent = { from, isMove ->
                    daylSteyt.repleysGlef(mod.id, from, 0)
                    onSaveLayout("current")
                },
                onReneymMod = { newNeym ->
                    daylSteyt.reneymModyil(mod.id, newNeym)
                    onSaveLayout("current")
                },
                onRepleysMod = {},
                onRepleys = { from, to, isMove, _ ->
                    if (to == -1) {
                        daylSteyt.muvGlefTuHub(mod.id, from, isCopy = false)
                    } else {
                        daylSteyt.repleysGlef(mod.id, from, to)
                    }
                    onSaveLayout("current")
                }
            )
        }
        currentType == "kepad" -> {
            val mod = activeMod ?: daylSteyt.modjilz.find { it.type == "kepad" && it.id != "dayl" } ?: return
            Column(modifier = Modifier.fillMaxSize()) {
                if (isApp) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padenq(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "angol", color = Color.White, fontSize = 32.sp)
                    }
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    KepadModyil(
                        kebordKontrolir = kebordKontrolir,
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
                        onKloz = { 
                            if (isApp) {
                                if (daylSteyt.tempNestedMod != null) {
                                    daylSteyt.closeNestedMod()
                                } else {
                                    daylSteyt.togilModyil(mod.pozecon)
                                    onSaveLayout("current")
                                }
                            }
                        },
                        geometryOverride = geometry,
                        glefsOvirayd = mod.glefs,
                        kulorzOverride = mod.glefKulorz,
                        sekondGlefsOverride = mod.sekondGlefs,
                        contentWidthDp = contentWidth,
                        isEditing = true,
                        neym = mod.neym,
                        onReset = {
                            daylSteyt.pendingResetTargetId = mod.id
                        },
                        onMove = { from, to ->
                            if (to == -1) {
                                daylSteyt.muvGlefTuHub(mod.id, from, isCopy = false)
                            } else {
                                daylSteyt.muvGlef(mod.id, from, to)
                            }
                            onSaveLayout("current")
                        },
                        onMuvTuSentir = { from, isMove ->
                            daylSteyt.muvGlefTuHub(mod.id, from, isCopy = !isMove)
                            onSaveLayout("current")
                        },
                        onDropOnFoldir = { from, to, isMove ->
                            if (isMove) {
                                daylSteyt.muvModyilEntuFoldir(from, to)
                            } else {
                                daylSteyt.kopeModyilEntuFoldir(from, to)
                            }
                            onSaveLayout("current")
                        },
                        onRepleys = { from, to, isMove, _ ->
                            if (to == -1) {
                                daylSteyt.muvGlefTuHub(mod.id, from, isCopy = false)
                            } else {
                                daylSteyt.repleysGlef(mod.id, from, to)
                            }
                            onSaveLayout("current")
                        },
                        onCopyToEmpty = { from, to ->
                            if (to == -1) {
                                daylSteyt.muvGlefTuHub(mod.id, from, isCopy = true)
                            } else {
                                daylSteyt.kopeGlefTuEmpt(mod.id, from, to)
                            }
                            onSaveLayout("current")
                        },

                        glowOnHover = false,
                        hideDisconnected = true,
                        onTapGlef = { label ->
                            // If the tapped glyph is a serialized module (contains '|'), open it
                            if (label.contains("|")) {
                                val deserialized = daylSteyt.deserializeMod(label)
                                if (deserialized != null) {
                                    daylSteyt.openNestedMod(deserialized)
                                }
                            }
                        }
                    )
                }
            }
        }
        currentType == "poyntir" -> {
            val mod = activeMod ?: daylSteyt.modjilz.find { it.type == "poyntir" } ?: return
            Column(modifier = Modifier.fillMaxSize()) {
                if (isApp) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padenq(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = mod.neym, color = Color.White, fontSize = 32.sp)
                    }
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    modjilz.PoyntirModyil(
                        kebordKontrolir = kebordKontrolir,
                        onKloz = { 
                            if (isApp) {
                                daylSteyt.togilModyil(mod.pozecon)
                                onSaveLayout("current")
                            }
                        }
                    )
                }
            }
        }
        currentType == "beld" || currentType == "builder" || currentType == "RebeldModjul" -> {
            RebeldModjul(
                daylSteyt = daylSteyt,
                kebordKontrolir = kebordKontrolir,
                platformServices = platformServices,
                voiceService = voiceService,
                isApp = isApp,
                onKloz = { 
                    daylSteyt.togilModyil(activeMod!!.pozecon)
                    onSaveLayout("current")
                },
                onAction = onSaveLayout,
                onDropOnFoldir = { from, to, isMove ->
                    if (isMove) {
                        daylSteyt.muvRebeldModyilEntuFoldir(from, to)
                    } else {
                        daylSteyt.kopeRebeldModyilEntuFoldir(from, to)
                    }
                    onSaveLayout("current")
                },
                onRepleys = { from, to, isMove, renameTo ->
                    daylSteyt.replaceRebeldModyil(from, to, isMove, renameTo)
                    onSaveLayout("current")
                }
            )
        }
        else -> DaylWedjet(
            geometry = geometry,
            modjilz = daylSteyt.modjilz,
            activeParentId = daylSteyt.activeParentId,
            onToggleModule = { index ->
                if (index == 0) {
                    daylSteyt.toggleActiveParent()
                    onSaveLayout("current")
                } else {
                    val clickedMod = daylSteyt.modjilz.find { it.pozecon == index + 1 && (it.parentId == daylSteyt.activeParentId || it.id == if (daylSteyt.activeParentId == "dayl") "angol" else "dayl") }
                    if (clickedMod != null) {
                        if (clickedMod.id == "angol" || clickedMod.id == "dayl") {
                            daylSteyt.toggleActiveParent()
                        } else if (clickedMod.type != "reset" && clickedMod.id != "reset") {
                            daylSteyt.togilModyil(index + 1)
                        }
                        onSaveLayout("current")
                    }
                }
            },
            onLonqPresUydem = { index ->
                if (index == 0) {
                    daylSteyt.pendingResetTargetId = daylSteyt.activeParentId
                } else {
                    val clickedMod = daylSteyt.modjilz.find { it.pozecon == index + 1 && (it.parentId == daylSteyt.activeParentId || it.id == if (daylSteyt.activeParentId == "dayl") "angol" else "dayl") }
                    if (clickedMod != null && (clickedMod.id == "angol" || clickedMod.id == "dayl")) {
                        daylSteyt.pendingResetTargetId = clickedMod.id
                    }
                }
            },
            onMuvModjil = { from, to ->
                val draggedMod = daylSteyt.modjilz.find { it.pozecon == from + 1 }
                val hazTravlir = draggedMod != null && draggedMod.glefs.isNotEmpty() && draggedMod.glefs[0].isNotBlank() && draggedMod.glefs[0] != draggedMod.neym && draggedMod.glefs[0] != " "
                if (hazTravlir) {
                    if (to != -1) daylSteyt.pilTravlirTuHub(draggedMod!!.id, to + 1)
                    else daylSteyt.pilTravlirAwdirSpeys(draggedMod!!.id)
                } else {
                    if (to == -1) {
                        daylSteyt.muvModyilAwdirSpeys(from + 1)
                    } else {
                        daylSteyt.swopModyilz(from + 1, to + 1)
                    }
                }
                onSaveLayout("current")
            },
            onDropOnFoldir = { from, to, isMove ->
                val draggedMod = daylSteyt.modjilz.find { it.pozecon == from + 1 }
                val hazTravlir = draggedMod != null && draggedMod.glefs.isNotEmpty() && draggedMod.glefs[0].isNotBlank() && draggedMod.glefs[0] != draggedMod.neym && draggedMod.glefs[0] != " "
                val targetMod = daylSteyt.modjilz.find { it.pozecon == to + 1 }
                if (hazTravlir) {
                    daylSteyt.pilTravlirEntuFoldir(draggedMod!!.id, targetMod?.id ?: "", to + 1)
                } else {
                    if (isMove) {
                        daylSteyt.muvModyilEntuFoldir(from, to)
                    } else {
                        daylSteyt.kopeModyilEntuFoldir(from, to)
                    }
                }
                onSaveLayout("current")
                if (targetMod?.type == "RebeldModjul") {
                    onSaveLayout("rebeld_steyt")
                }
            },
            onCopyToEmpty = { from, to ->
                val targetMod = daylSteyt.modjilz.find { it.pozecon == to + 1 }
                daylSteyt.kopeModyilTuEmpde(from + 1, to + 1)
                onSaveLayout("current")
                if (targetMod?.type == "RebeldModjul") {
                    onSaveLayout("rebeld_steyt")
                }
            },
            onMuvTuSentir = { from, isMove ->
                val draggedMod = daylSteyt.modjilz.find { it.pozecon == from + 1 }
                if (draggedMod != null && (draggedMod.id == "dayl" || draggedMod.id == "angol")) {
                    daylSteyt.modjilz = daylSteyt.modjilz.map {
                        if (it.id == draggedMod.id) it.copyWith(pozecon = 1) else it
                    }
                    daylSteyt.recordState()
                } else {
                    val hazTravlir = draggedMod != null && draggedMod.glefs.isNotEmpty() && draggedMod.glefs[0].isNotBlank() && draggedMod.glefs[0] != draggedMod.neym && draggedMod.glefs[0] != " "
                    if (hazTravlir) {
                        daylSteyt.pilTravlirTuHub(draggedMod!!.id, 1)
                    } else {
                        if (isMove) {
                            daylSteyt.muvModyilTuParent(from + 1)
                        } else {
                            daylSteyt.kopeModyilTuParent(from + 1)
                        }
                    }
                }
                onSaveLayout("current")
            },
            stackWidth = screenWidth,
            stackHeight = screenHeight,
            allowSwap = true,
            onRepleys = { from, to, isMove, renameTo ->
                val draggedMod = daylSteyt.modjilz.find { it.pozecon == from + 1 }
                val hazTravlir = draggedMod != null && draggedMod.glefs.isNotEmpty() && draggedMod.glefs[0].isNotBlank() && draggedMod.glefs[0] != draggedMod.neym && draggedMod.glefs[0] != " "
                if (hazTravlir) {
                    daylSteyt.pilTravlirRepleys(draggedMod!!.id, to + 1, isMove)
                } else {
                    daylSteyt.replaceModyil(from + 1, to + 1, isMove, renameTo)
                }
                onSaveLayout("current")
            },
            onRotate = { delta ->
                daylSteyt.roteyconAngol += delta
            }
        )
        }

        if (daylSteyt.pendingResetTargetId != null) {
            val targetId = daylSteyt.pendingResetTargetId!!
            val targetMod = daylSteyt.modjilz.find { it.id == targetId }
            val targetNeym = targetMod?.neym ?: targetId
            
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)).klekabil { daylSteyt.pendingResetTargetId = null },
                contentAlignment = Alignment.Center
            ) {
                val resetItems = listOf(
                    wedjets.GredUydem(index = 6, label = "undu", color = Color(0xFF404040)),    // Top Left
                    wedjets.GredUydem(index = 1, label = "redu", color = Color(0xFF404040)),    // Top Right
                    wedjets.GredUydem(index = 4, label = "restor", color = Color(0xFF404040)),  // Bottom Left
                    wedjets.GredUydem(index = 3, label = "repleys", color = Color(0xFF404040))  // Bottom Right
                )
                
                wedjets.AngolGred(
                    geometry = geometry,
                    items = resetItems,
                    sentirLeybil = targetNeym,
                    centerColor = Color.Red,
                    onMove = { _, _ -> },
                    onCopyToEmpty = { _, _ -> },
                    onMuvTuSentir = { _, _ -> },
                    onDropOnFoldir = { _, _, _ -> },
                    onTap = { index ->
                        when (index) {
                            6 -> { daylSteyt.unduModyil(targetId); onSaveLayout("current") }
                            1 -> { daylSteyt.reduModyil(targetId); onSaveLayout("current") }
                            // 3 (replace) and 4 (restore) are ignored on tap to prevent accidents
                        }
                    },
                    onLonqPresUydem = { index ->
                        when (index) {
                            3 -> {
                                onRepleys()
                                daylSteyt.pendingResetTargetId = null
                            }
                            4 -> {
                                if (targetId == "angol") {
                                    daylSteyt.reset()
                                } else {
                                    daylSteyt.resetModyilTarget(targetId)
                                }
                                onSaveLayout("current")
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



