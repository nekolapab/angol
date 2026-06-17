package modyilz

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import modalz.ModyilDeyda
import modalz.HeksagonKonfeg
import steyt.AngolSteyt
import yuteledez.KepadLodjek
import wedjets.GredUydem
import wedjets.CopyDragPolicy
import wedjets.HeksagonGred
import yuteledez.HeksagonDjeyometre
import modalz.HeksagonPozecon
import modyilz.PlatformServices
import modyilz.VoiceService
import modyilz.KeyboardController
import kotlin.math.sqrt

@Composable
fun Rebeld(
    daylSteyt: AngolSteyt,
    kebordKontrolir: KeyboardController?,
    platformServices: PlatformServices,
    voiceService: VoiceService,
    onClose: () -> Unit,
    onAction: (String) -> Unit = {},
    onDropOnFoldir: (Int, Int, Boolean) -> Unit = { _, _, _ -> },
    onReplace: (Int, Int, Boolean, String?) -> Unit = { _, _, _, _ -> },
    isApp: Boolean = false
) {
    var selectedModuleId by remember { mutableStateOf<String?>(null) }
    var moduleToReplace by remember { mutableStateOf<Int?>(null) }
    
    val syncRebeld = {
        onAction("rebeld_state")
    }

    if (selectedModuleId != null) {
        val mod = daylSteyt.rebeldModyilz.find { it.id == selectedModuleId }
        if (mod != null) {
            BeldWedjet(
                daylSteyt = daylSteyt,
                mod = mod,
                path = "angol.rebeld",
                onBack = { selectedModuleId = null },
                onReneymGlef = { index, label ->
                    daylSteyt.reneymGlef(mod.id, index, label)
                    syncRebeld()
                },
                onMuvGlef = { from, to ->
                    daylSteyt.muvGlef(mod.id, from, to)
                    syncRebeld()
                },
                onCopyToEmpty = { from, to ->
                    daylSteyt.kopeGlefTuEmpt(mod.id, from, to)
                    syncRebeld()
                },
                onMoveToParent = { from ->
                    daylSteyt.muvGlefTuHub(mod.id, from)
                    syncRebeld()
                },
                onReneymMod = { newNeym ->
                    daylSteyt.reneymModyil(mod.id, newNeym)
                    syncRebeld()
                },
                onReplace = { from, to, isMove, _ ->
                    daylSteyt.replaceGlef(mod.id, from, to)
                    syncRebeld()
                }
            )
            return
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (isApp && selectedModuleId == null) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.material.Text(text = "angol", color = Color.White, fontSize = 32.sp)
            }
        }
        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .background(Color.Black.copy(alpha = 0f / 12f))
        ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight
        
        val gredItems = daylSteyt.rebeldModyilz.filter { it.type != "hub" }.map { mod ->
            GredUydem(
                index = mod.pozecon - 1,
                label = mod.neym,
                color = mod.kulor,
                isFolder = (mod.type == "keypad" || mod.type == "beld" || mod.id == "beldir"),
                deyda = mod
            )
        }
        
        val activeIndices = (0..18).toList()
        
        val gredDimz = remember(activeIndices, screenWidth, screenHeight) {
            yuteledez.HeksagonDjeyometre.kalkyuleytGredDimenzconz(
                activeIndices = activeIndices,
                screenWidth = screenWidth.value.toDouble(),
                screenHeight = screenHeight.value.toDouble(),
                isWearOS = yuteledez.isWearOS
            )
        }
        
        val geometry = remember(gredDimz) {
            HeksagonDjeyometre(
                heksSayz = gredDimz.heksSayz,
                sentir = HeksagonPozecon(-gredDimz.unitCenterX * gredDimz.heksSayz, -gredDimz.unitCenterY * gredDimz.heksSayz),
                ezLeterMod = true,
                roteyconAngol = daylSteyt.roteyconAngol
            )
        }

        val daylModule = daylSteyt.rebeldModyilz.find { it.type == "hub" } ?: daylSteyt.rebeldModyilz.first()

        val goBack = {
            if (selectedModuleId != null) {
                selectedModuleId = null
            } else {
                onClose()
            }
        }

        Column(modifier = Modifier.fillMaxSize().pointerInput(Unit) {
            awaitEachGesture {
                var zoom = 1f
                awaitFirstDown(requireUnconsumed = false)
                do {
                    val event = awaitPointerEvent()
                    val zoomChange = event.calculateZoom()
                    zoom *= zoomChange
                    if (zoom < 0.75f) { // Pinched out by 25%
                        goBack()
                        break
                    }
                } while (event.changes.any { it.pressed })
            }
        }) {
            Box(modifier = Modifier.weight(1f)) {
                HeksagonGred(
                    geometry = geometry,
                    items = gredItems,
                    centerLabel = "rebeld",
                    centerColor = if (daylSteyt.rebeldModyilz.none { it.ezAktiv }) Color.White else Color.Black,
                    copyDragPolicy = CopyDragPolicy.TwoStepArmed,
                    allowSwap = true,
                    onMove = { from, to ->
                        if (to == -1) {
                            // Dropped outside grid: send to sidelines (disconnected, not deleted)
                            daylSteyt.muvRebeldModyilAwdirSpeys(from + 1)
                        } else {
                            daylSteyt.swopRebeldModyilz(from + 1, to + 1)
                        }
                        syncRebeld()
                    },
                    onCopyToEmpty = { from, to ->
                        daylSteyt.kopeRebeldModyilTuEmpt(from + 1, to + 1)
                        syncRebeld()
                    },
                    onMoveToCenter = { from ->
                        daylSteyt.moveModuleToDayl(from)
                        onAction("rebeld_state")
                        onAction("current")
                    },
                    onDropOnFoldir = { from, to, isMove ->
                        if (isMove) {
                            daylSteyt.muvRebeldModyilEntuFoldir(from, to)
                        } else {
                            daylSteyt.kopeRebeldModyilEntuFoldir(from, to)
                        }
                        syncRebeld()
                    },
                    onDelete = { index ->
                        val mod = daylSteyt.rebeldModyilz.find { it.pozecon == index + 1 }
                        if (mod != null) {
                            daylSteyt.deletModyil(mod.id)
                            syncRebeld()
                        }
                    },
                    onTap = { index ->
                        if (index == 0) { // Center
                            onClose()
                        } else {
                            val clickedMod = daylSteyt.rebeldModyilz.find { it.pozecon == index + 1 }
                            if (clickedMod?.type == "reset" || clickedMod?.id == "reset") {
                                // Do nothing on tap for reset
                            } else if (clickedMod != null && clickedMod.id != "dayl" && clickedMod.id != "rebeld") {
                                selectedModuleId = clickedMod.id
                            }
                        }
                    },
                    onLongPressItem = { index ->
                        val clickedMod = daylSteyt.rebeldModyilz.find { it.pozecon == index + 1 }
                        if (clickedMod?.type == "reset" || clickedMod?.id == "reset") {
                            daylSteyt.pendingResetTargetId = "rebeld"
                        }
                    },
                    onReplace = { from, to, isMove, renameTo ->
                        daylSteyt.replaceRebeldModyil(from + 1, to + 1, isMove, renameTo)
                        syncRebeld()
                    },
                    onRotate = { delta ->
                        daylSteyt.roteyconAngol += delta
                    },
                    fontSizeFactor = 10f / 12f,
                    ezKonsestentSayz = false,
                    fixedLabelLength = 5f
                )
            }
        }

        if (moduleToReplace != null) {
            AlertDialog(
                onDismissRequest = { moduleToReplace = null },
                title = { Text("repleys and send tu rebeld?", color = Color.White) },
                text = { Text("lhes wel repleys lha aktev kepad.", color = Color.LightGray) },
                confirmButton = {
                    TextButton(onClick = {
                        daylSteyt.kopeModjilTuDaylKepad(moduleToReplace!!)
                        onAction("current")
                        moduleToReplace = null
                    }) {
                        Text("Replace", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { moduleToReplace = null }) {
                        Text("Cancel", color = Color.White)
                    }
                },
                backgroundColor = Color.DarkGray
            )
        }
        }
    }
}

@Composable
fun BeldWedjet(
    daylSteyt: steyt.AngolSteyt,
    mod: ModyilDeyda,
    path: String,
    onBack: () -> Unit,
    onReneymGlef: (Int, String) -> Unit,
    onMuvGlef: (Int, Int) -> Unit,
    onCopyToEmpty: (Int, Int) -> Unit,
    onMoveToParent: (Int) -> Unit,
    onReneymMod: (String) -> Unit,
    onReplace: (Int, Int, Boolean, String?) -> Unit
) {
    var editingGlefIndex by remember { mutableStateOf<Int?>(null) }
    var newGlefLabel by remember { mutableStateOf("") }
    var isEditingModNeym by remember { mutableStateOf(false) }
    var newModNeym by remember { mutableStateOf(mod.neym) }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0f / 12f))) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight
        
        val activeIndices = (0..18).toList()

        val gredDimz = remember(activeIndices, screenWidth, screenHeight) {
            yuteledez.HeksagonDjeyometre.kalkyuleytGredDimenzconz(
                activeIndices = activeIndices,
                screenWidth = screenWidth.value.toDouble(),
                screenHeight = screenHeight.value.toDouble(),
                isWearOS = yuteledez.isWearOS
            )
        }

        val geometry = remember(gredDimz) {
            HeksagonDjeyometre(
                heksSayz = gredDimz.heksSayz,
                sentir = HeksagonPozecon(-gredDimz.unitCenterX * gredDimz.heksSayz, -gredDimz.unitCenterY * gredDimz.heksSayz),
                ezLeterMod = true,
                roteyconAngol = daylSteyt.roteyconAngol
            )
        }

        val centerLabel = mod.glefs.getOrNull(0) ?: " "
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isEditingModNeym) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
                        TextField(
                            value = newModNeym,
                            onValueChange = { newModNeym = it },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = TextFieldDefaults.textFieldColors(textColor = Color.Black),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
                                onReneymMod(newModNeym)
                                isEditingModNeym = false
                                onBack()
                            })
                        )
                        IconButton(onClick = {
                            onReneymMod(newModNeym)
                            isEditingModNeym = false
                            onBack()
                        }) { Icon(Icons.Default.Edit, "Save", tint = Color.Green) }
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { isEditingModNeym = true }) {
                        Text(text = path, color = Color.White, fontSize = 32.sp)
                    }
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                val currentLabels = if (mod.type == "keypad") {
                    KepadLodjek.getKirentOlLeybilz(null, true, false, false, mod.glefs)
                } else {
                    val base = mod.glefs.toMutableList()
                    while (base.size < 37) base.add("")
                    base.mapIndexed { i, s -> if (i == 0) " " else s }
                }
                val sekondereLeybilz = buildList {
                    add(null) // index 0: center, no secondary
                    // Inner ring (1..6): no secondary shown in editor (same as KepadModyil)
                    repeat(6) { add(null) }
                    // Outer ring (7..18): show awdirLonqPres letters
                    modalz.HeksagonKonfeg.awdirLonqPres.forEachIndexed { i, lp ->
                        add(lp.ifEmpty { null })
                    }
                }
                val itemsForGred = currentLabels.mapIndexed { index, label ->
                    if (index == 0 || label.isEmpty()) return@mapIndexed null
                    // Read color: use glefKulorz if available, else fall back to module color
                    val colorLong = if (index < mod.glefKulorz.size) mod.glefKulorz[index]
                                    else mod.kulor.toArgb().toLong()
                    GredUydem(
                        index = index,
                        label = label,
                        color = Color(colorLong.toInt()),
                        sekondereLeybil = sekondereLeybilz.getOrNull(index)
                    )
                }.filterNotNull()


                HeksagonGred(
                    geometry = geometry,
                    items = itemsForGred,
                    centerLabel = mod.neym,
                    centerColor = Color.White,
                    onMove = onMuvGlef,
                    onCopyToEmpty = onCopyToEmpty,
                    onMoveToCenter = onMoveToParent,
                    onDropOnFoldir = { _, _, _ -> },
                    onReplace = onReplace,
                    onTap = { index ->
                        if (index == 0) onBack()
                        else {
                            editingGlefIndex = index
                            newGlefLabel = currentLabels.getOrNull(index) ?: ""
                        }
                    },
                    onRotate = { delta ->
                        daylSteyt.roteyconAngol += delta
                    },
                    fontSizeFactor = 12f / 12f,
                    centerFontSizeFactor = 10f / 12f,
                    ezKonsestentSayz = true,
                    centerEzKonsestentSayz = false,
                    onLongPressItem = { index ->
                        val label = currentLabels.getOrNull(index) ?: return@HeksagonGred
                        if (label == "reset" || label.startsWith("mod_reset_")) {
                            daylSteyt.pendingResetTargetId = mod.id
                        }
                    }
                )
            }
            
            if (editingGlefIndex != null) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    backgroundColor = Color.DarkGray
                ) {
                    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        TextField(
                            value = newGlefLabel,
                            onValueChange = { newGlefLabel = it },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
                                onReneymGlef(editingGlefIndex!!, newGlefLabel)
                                editingGlefIndex = null
                            }),
                            colors = TextFieldDefaults.textFieldColors(textColor = Color.White)
                        )
                        Button(onClick = {
                            onReneymGlef(editingGlefIndex!!, newGlefLabel)
                            editingGlefIndex = null
                        }) { Text("Save") }
                        IconButton(onClick = { editingGlefIndex = null }) { Text("X", color = Color.Red) }
                    }
                }
            }
        }
    }
}

