package skrenz

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import modyilz.KepadModyil
import modyilz.Rebeld
import steyt.AngolSteyt
import modyilz.PlatformServices
import modyilz.VoiceService
import modyilz.KeyboardController
import sirvesez.FirebaseSirves
import yuteledez.HeksagonDjeyometre
import modalz.HeksagonPozecon
import modalz.ModyilDeyda
import kotlin.math.sqrt

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun DaylSkrenEntry(
    kebordKontrolir: modyilz.KeyboardController?,
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
    daylSteyt: AngolSteyt = remember { AngolSteyt() }
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
                    kebordKontrolir, platformServices, voiceService,
                    ezLeterMod, ezPunkcuweyconMod, ezUpsayddawn, onTogilMod, onSetPunkcuweyconMod,
                    ezAngolMod, onTogilAngol, onStartAiVoys, ignoreSelectionUpdate,
                    firebaseSirves, isApp = true, daylSteyt = daylSteyt,
                    onGoToHome = { kurentSkren = "home" }
                )
                "home" -> if (firebaseSirves != null) AfdirLogenSkren(firebaseSirves, onContinue = { kurentSkren = "main" }) else DaylSkren(
                    kebordKontrolir, platformServices, voiceService,
                    ezLeterMod, ezPunkcuweyconMod, ezUpsayddawn, onTogilMod, onSetPunkcuweyconMod,
                    ezAngolMod, onTogilAngol, onStartAiVoys, ignoreSelectionUpdate,
                    firebaseSirves, isApp = true, daylSteyt = daylSteyt
                )
            }
        }
    } else {
        DaylSkren(
            kebordKontrolir, platformServices, voiceService,
            ezLeterMod, ezPunkcuweyconMod, ezUpsayddawn, onTogilMod, onSetPunkcuweyconMod,
            ezAngolMod, onTogilAngol, onStartAiVoys, ignoreSelectionUpdate,
            firebaseSirves, isApp = false, daylSteyt = daylSteyt
        )
    }
}

@Composable
fun DaylSkren(
    kebordKontrolir: modyilz.KeyboardController?,
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
    daylSteyt: AngolSteyt = remember { AngolSteyt() }
) {
    val scope = rememberCoroutineScope()
    val userState = firebaseSirves?.authStateChanges?.collectAsState(initial = firebaseSirves.currentUser)

    val saveLayout: (String) -> Unit = { env ->
        if (firebaseSirves != null) {
            scope.launch {
                if (env == "rebeld_state") {
                    firebaseSirves.saveModuleLayout(daylSteyt.rebeldModyilz, env)
                } else {
                    firebaseSirves.saveModuleLayout(daylSteyt.modyilz, env)
                }
            }
        }
    }
    
    if (daylSteyt.pendingResetTargetId != null) {
        val targetId = daylSteyt.pendingResetTargetId!!
        val targetMod = daylSteyt.modyilz.find { it.id == targetId } ?: daylSteyt.rebeldModyilz.find { it.id == targetId }
        val targetNeym = targetMod?.neym ?: targetId
        
        androidx.compose.material.AlertDialog(
            onDismissRequest = { daylSteyt.pendingResetTargetId = null },
            title = null,
            text = {
                androidx.compose.foundation.layout.Column {
                    androidx.compose.foundation.layout.Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceEvenly
                    ) {
                        androidx.compose.material.Button(
                            onClick = {
                                daylSteyt.undoModule(targetId)
                                daylSteyt.pendingResetTargetId = null
                                saveLayout("current")
                                saveLayout("rebeld_state")
                            }
                        ) {
                            androidx.compose.material.Text("undo", fontSize = 20.sp)
                        }
                        androidx.compose.material.Button(
                            onClick = {
                                daylSteyt.redoModule(targetId)
                                daylSteyt.pendingResetTargetId = null
                                saveLayout("current")
                                saveLayout("rebeld_state")
                            }
                        ) {
                            androidx.compose.material.Text("redo", fontSize = 20.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    androidx.compose.material.Button(
                        onClick = {
                            if (targetId == "dayl") {
                                daylSteyt.reset()
                            } else {
                                daylSteyt.resetModyilTarget(targetId)
                            }
                            daylSteyt.pendingResetTargetId = null
                            saveLayout("current")
                            saveLayout("rebeld_state")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        androidx.compose.material.Text("restor $targetNeym", fontSize = 24.sp)
                    }
                }
            },
            confirmButton = {},
            dismissButton = null,
            backgroundColor = Color(0xFF1E1E1E)
        )
    }

    LaunchedEffect(isApp) {
        if (!isApp && daylSteyt.activeModule == null) {
            daylSteyt.activateModyil("keypad")
        }
        if (isApp && firebaseSirves != null) {
            scope.launch {
                firebaseSirves.watchModuleLayout("current").collect { updatedModules ->
                    if (updatedModules.isNotEmpty()) {
                        var modified = false
                        var currentList = updatedModules.toMutableList()

                        // 1. Force absolute colors and dayl properties
                        currentList = currentList.map { mod ->
                            var updatedMod = mod.copyWith() // applies absolute colors
                            if (updatedMod.kulorLong != mod.kulorLong) modified = true
                            if (updatedMod.id == "dayl" && (updatedMod.neym != "dayl" || updatedMod.pozecon != 2 || updatedMod.glefs != listOf("dayl"))) {
                                modified = true
                                updatedMod = updatedMod.copyWith(neym = "dayl", pozecon = 2, glefs = listOf("dayl"))
                            }
                            if (updatedMod.id == "reset" && updatedMod.kulorLong == 0xFFFF0000L) {
                                modified = true
                                updatedMod = updatedMod.copyWith(kulorLong = 0xFF000000L)
                            }
                            updatedMod
                        }.toMutableList()

                        // 2. Ensure all protected modules exist
                        val hasDayl = currentList.any { it.id == "dayl" }
                        val hasKeypad = currentList.any { it.id == "keypad" }
                        val hasRebeld = currentList.any { it.id == "rebeld" }
                        val hasReset = currentList.any { it.id == "reset" || it.type == "reset" }

                        if (!hasDayl) {
                            currentList.add(ModyilDeyda(id = "dayl", neym = "dayl", kulorLong = 0xFFFF0000L, pozecon = 2, ezAktiv = false, glefs = listOf("dayl"), type = "hub"))
                            modified = true
                        }
                        if (!hasKeypad) {
                            currentList.add(ModyilDeyda(id = "keypad", neym = "kepad", kulorLong = 0xFFFFFF00L, pozecon = 3, ezAktiv = false, glefs = listOf(" ") + modalz.HeksagonKonfeg.innerLetterMode + modalz.HeksagonKonfeg.outerTap, type = "keypad"))
                            modified = true
                        }
                        if (!hasRebeld) {
                            currentList.add(ModyilDeyda(id = "rebeld", neym = "rebeld", kulorLong = 0xFF00FF00L, pozecon = 4, ezAktiv = false, type = "rebeld"))
                            modified = true
                        }
                        if (!hasReset) {
                            var newPozecon = 8
                            while (currentList.any { it.pozecon == newPozecon }) newPozecon++
                            currentList.add(ModyilDeyda(id = "reset", neym = "reset", kulorLong = 0xFF000000L, pozecon = newPozecon, ezAktiv = false, type = "reset"))
                            modified = true
                        }

                        // 3. Resolve pozecon conflicts for protected positions (1, 2)
                        val protectedIds = setOf("dayl")
                        val occupiedPozecons = currentList.map { it.pozecon }.toMutableList()
                        currentList = currentList.map { mod ->
                            if (!protectedIds.contains(mod.id) && (mod.pozecon == 1 || mod.pozecon == 2)) {
                                var newPozecon = 3
                                while (occupiedPozecons.contains(newPozecon)) {
                                    newPozecon++
                                }
                                occupiedPozecons.add(newPozecon)
                                modified = true
                                mod.copyWith(pozecon = newPozecon)
                            } else mod
                        }.toMutableList()

                        // 5. Make sure only one folder/module is active
                        val activeFolder = currentList.find { it.ezAktiv && it.type != "hub" }
                        if (activeFolder != null) {
                            currentList = currentList.map { mod ->
                                if (mod.id != activeFolder.id && mod.ezAktiv) {
                                    modified = true
                                    mod.copyWith(ezAktiv = false)
                                } else mod
                            }.toMutableList()
                        }

                        daylSteyt.updateModules(currentList)
                        if (modified) {
                            firebaseSirves.saveModuleLayout(currentList, "current")
                        }
                    }
                }
            }
            scope.launch {
                firebaseSirves.watchModuleLayout("rebeld_state").collect { updatedModules ->
                    if (updatedModules.isNotEmpty()) {
                        var modified = false
                        var currentList = updatedModules.toMutableList()

                        // 1. Force absolute colors and dayl properties
                        currentList = currentList.map { mod ->
                            var updatedMod = mod.copyWith() // applies absolute colors
                            if (updatedMod.kulorLong != mod.kulorLong) modified = true
                            if (updatedMod.id == "dayl" && (updatedMod.neym != "dayl" || updatedMod.pozecon != 2 || updatedMod.glefs != listOf("dayl"))) {
                                modified = true
                                updatedMod = updatedMod.copyWith(neym = "dayl", pozecon = 2, glefs = listOf("dayl"))
                            }
                            updatedMod
                        }.toMutableList()

                        val hasDayl = currentList.any { it.id == "dayl" }
                        val hasBeldir = currentList.any { it.id == "beldir" } || daylSteyt.modyilz.any { it.id == "beldir" }

                        if (!hasDayl) {
                            currentList.add(ModyilDeyda(id = "dayl", neym = "dayl", kulorLong = 0xFFFF0000L, pozecon = 2, ezAktiv = false, glefs = listOf("dayl"), type = "hub"))
                            modified = true
                        }
                        if (!hasBeldir) {
                            currentList.add(ModyilDeyda(id = "beldir", neym = "beldir", kulorLong = 0xFF00FFCCL, pozecon = 3, ezAktiv = false, type = "keypad"))
                            modified = true
                        }

                        // 2. Resolve pozecon conflicts for protected positions (1, 2)
                        val protectedIds = setOf("dayl")
                        val occupiedPozecons = currentList.map { it.pozecon }.toMutableList()
                        currentList = currentList.map { mod ->
                            if (!protectedIds.contains(mod.id) && (mod.pozecon == 1 || mod.pozecon == 2)) {
                                var newPozecon = 3
                                while (occupiedPozecons.contains(newPozecon)) {
                                    newPozecon++
                                }
                                occupiedPozecons.add(newPozecon)
                                modified = true
                                mod.copyWith(pozecon = newPozecon)
                            } else mod
                        }.toMutableList()

                        daylSteyt.updateRebeldModules(currentList)
                        if (modified) {
                            firebaseSirves.saveModuleLayout(currentList, "rebeld_state")
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
    daylSteyt: AngolSteyt,
    geometry: HeksagonDjeyometre,
    kebordKontrolir: modyilz.KeyboardController?,
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
        activeMod?.id == "dayl" -> {
            val mod = activeMod!!
            modyilz.BeldWedjet(
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
                    daylSteyt.kopeGlefTuEmpt(mod.id, from, to)
                    onSaveLayout("current")
                },
                onMoveToParent = { from ->
                    daylSteyt.muvGlefTuHub(mod.id, from)
                    onSaveLayout("current")
                },
                onReneymMod = { newNeym ->
                    daylSteyt.reneymModyil(mod.id, newNeym)
                    onSaveLayout("current")
                },
                onReplace = { from, to, isMove, _ ->
                    daylSteyt.replaceGlef(mod.id, from, to)
                    onSaveLayout("current")
                }
            )
        }
        currentType == "keypad" -> {
            val mod = activeMod ?: daylSteyt.modyilz.find { it.type == "keypad" } ?: return
            Column(modifier = Modifier.fillMaxSize()) {
                if (isApp) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
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
                        onClose = { 
                            if (isApp) {
                                daylSteyt.togilModyil(mod.pozecon)
                                onSaveLayout("current")
                            }
                        },
                        geometryOverride = geometry,
                        glefsOvirayd = mod.glefs,
                        kulorzOverride = mod.glefKulorz,
                        contentWidthDp = contentWidth,
                        isEditing = true,
                        neym = mod.neym,
                        onReset = {
                            daylSteyt.pendingResetTargetId = mod.id
                        },
                        onMove = { from, to ->
                            if (daylSteyt.kreyeytBakupEfNeded()) onSaveLayout("rebeld_state")
                            daylSteyt.muvGlef(mod.id, from, to)
                            onSaveLayout("current")
                        },
                        onDropOnFoldir = { from, to, isMove ->
                            if (isMove) {
                                if (daylSteyt.kreyeytBakupEfNeded()) onSaveLayout("rebeld_state")
                                daylSteyt.muvModyilEntuFoldir(from, to)
                            } else {
                                daylSteyt.kopeModyilEntuFoldir(from, to)
                            }
                            onSaveLayout("current")
                        },
                        onReplace = { from, to, isMove, _ ->
                            if (daylSteyt.kreyeytBakupEfNeded()) onSaveLayout("rebeld_state")
                            daylSteyt.replaceGlef(mod.id, from, to)
                            onSaveLayout("current")
                        },
                        glowOnHover = false,
                        hideDisconnected = true
                    )
                }
            }
        }
        currentType == "beld" || currentType == "builder" || currentType == "rebeld" -> {
            Rebeld(
                daylSteyt = daylSteyt,
                kebordKontrolir = kebordKontrolir,
                platformServices = platformServices,
                voiceService = voiceService,
                isApp = isApp,
                onClose = { 
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
                onReplace = { from, to, isMove, renameTo ->
                    daylSteyt.replaceRebeldModyil(from, to, isMove, renameTo)
                    onSaveLayout("current")
                }
            )
        }
        else -> DaylWedjet(
            geometry = geometry,
            modyilz = daylSteyt.modyilz,
            onToggleModule = { index ->
                if (index == 0) {
                    daylSteyt.deactivateAll()
                    onSaveLayout("current")
                } else {
                    val clickedMod = daylSteyt.modyilz.find { it.pozecon == index + 1 }
                    if (clickedMod?.type == "reset" || clickedMod?.id == "reset") {
                        // Do nothing on tap for reset
                    } else {
                        daylSteyt.togilModyil(index + 1)
                        onSaveLayout("current")
                    }
                }
            },
            onLongPressItem = { index ->
                val clickedMod = daylSteyt.modyilz.find { it.pozecon == index + 1 }
                if (clickedMod?.type == "reset" || clickedMod?.id == "reset") {
                    daylSteyt.pendingResetTargetId = "dayl"
                }
            },
            onMoveModule = { from, to ->
                if (to == -1) {
                    daylSteyt.muvModyilAwdirSpeys(from + 1)
                } else {
                    daylSteyt.swopModyilz(from + 1, to + 1)
                }
                onSaveLayout("current")
            },
            onDropOnFoldir = { from, to, isMove ->
                val targetMod = daylSteyt.modyilz.find { it.pozecon == to + 1 }
                if (isMove) {
                    daylSteyt.muvModyilEntuFoldir(from, to)
                } else {
                    daylSteyt.kopeModyilEntuFoldir(from, to)
                }
                onSaveLayout("current")
                if (targetMod?.type == "rebeld") {
                    onSaveLayout("rebeld_state")
                }
            },
            onCopyToEmpty = { from, to ->
                val targetMod = daylSteyt.modyilz.find { it.pozecon == to + 1 }
                daylSteyt.kopeModyilTuEmpt(from + 1, to + 1)
                onSaveLayout("current")
                if (targetMod?.type == "rebeld") {
                    onSaveLayout("rebeld_state")
                }
            },
            onMoveToCenter = { from ->
                daylSteyt.muvModyilTuParent(from + 1)
                onSaveLayout("current")
            },
            stackWidth = screenWidth,
            stackHeight = screenHeight,
            allowSwap = true,
            onReplace = { from, to, isMove, renameTo ->
                daylSteyt.replaceModyil(from + 1, to + 1, isMove, renameTo)
                onSaveLayout("current")
            },
            onRotate = { delta ->
                daylSteyt.roteyconAngol += delta
            }
        )
    }
}
