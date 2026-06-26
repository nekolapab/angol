package modyilz
import yuteledez.padenq

import androidx.compose.foundation.background
import yuteledez.klekabil
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.AlertDialog as AlirtDayalog
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
    onKloz: () -> Unit,
    onAction: (String) -> Unit = {},
    onDropOnFoldir: (Int, Int, Boolean) -> Unit = { _, _, _ -> },
    onRepleys: (Int, Int, Boolean, String?) -> Unit = { _, _, _, _ -> },
    isApp: Boolean = false
) {
    var selectedModuleId by remember { mutableStateOf<String?>(null) }
    var modjilTuRepleys by remember { mutableStateOf<Int?>(null) }
    
    val syncRebeld = {
        onAction("rebeld_steyt")
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
                    if (to == -1) {
                        daylSteyt.muvGlefTuHub(mod.id, from, isCopy = true)
                    } else {
                        daylSteyt.kopeGlefTuEmpt(mod.id, from, to)
                    }
                    syncRebeld()
                },
                onMuvTuParent = { from ->
                    daylSteyt.muvGlefTuHub(mod.id, from)
                    syncRebeld()
                },
                onReneymMod = { newNeym ->
                    daylSteyt.reneymModyil(mod.id, newNeym)
                    syncRebeld()
                },
                onRepleysMod = { 
                    modjilTuRepleys = mod.pozecon 
                },
                onRepleys = { from, to, isMove, _ ->
                    if (to == -1) {
                        daylSteyt.muvGlefTuHub(mod.id, from, isCopy = false)
                    } else {
                        daylSteyt.repleysGlef(mod.id, from, to)
                    }
                    syncRebeld()
                }
            )
            return
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (isApp && selectedModuleId == null) {
            Row(
                modifier = Modifier.fillMaxWidth().padenq(16.dp),
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
            val hazTravlir = mod.glefs.isNotEmpty() && mod.glefs[0].isNotBlank() && mod.glefs[0] != mod.neym && mod.glefs[0] != " "
            val label = if (hazTravlir) daylSteyt.deserializeMod(mod.glefs[0])?.neym ?: mod.glefs[0] else mod.neym
            val color = if (hazTravlir) Color.Black else mod.kulor
            GredUydem(
                index = mod.pozecon - 1,
                label = label,
                color = color,
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
                onKloz()
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
                    sentirLeybil = "rebeld",
                    centerColor = if (daylSteyt.rebeldModyilz.none { it.ezAkdev }) Color.White else Color.Black,
                    copyDragPolicy = CopyDragPolicy.TwoStepArmed,
                    allowSwap = true,
                    onMove = { from, to ->
                        val draggedMod = daylSteyt.rebeldModyilz.find { it.pozecon == from + 1 }
                        val hazTravlir = draggedMod != null && draggedMod.glefs.isNotEmpty() && draggedMod.glefs[0].isNotBlank() && draggedMod.glefs[0] != draggedMod.neym && draggedMod.glefs[0] != " "
                        if (hazTravlir) {
                            if (to != -1) daylSteyt.pilTravlirTuHub(draggedMod!!.id, to + 1)
                            else daylSteyt.pilTravlirAwdirSpeys(draggedMod!!.id)
                        } else {
                            if (to == -1) {
                                daylSteyt.muvRebeldModyilAwdirSpeys(from + 1)
                            } else {
                                daylSteyt.swopRebeldModyilz(from + 1, to + 1)
                            }
                        }
                        syncRebeld()
                    },
                    onCopyToEmpty = { from, to ->
                        daylSteyt.kopeRebeldModyilTuEmpt(from + 1, to + 1)
                        syncRebeld()
                    },
                    onMuvTuSentir = { from ->
                        daylSteyt.muvModjilTuDayl(from)
                        onAction("rebeld_steyt")
                        onAction("current")
                    },
                    onDropOnFoldir = { from, to, isMove ->
                        val draggedMod = daylSteyt.rebeldModyilz.find { it.pozecon == from + 1 }
                        val hazTravlir = draggedMod != null && draggedMod.glefs.isNotEmpty() && draggedMod.glefs[0].isNotBlank() && draggedMod.glefs[0] != draggedMod.neym && draggedMod.glefs[0] != " "
                        val targetMod = daylSteyt.rebeldModyilz.find { it.pozecon == to + 1 }
                        if (hazTravlir) {
                            daylSteyt.pilTravlirEntuFoldir(draggedMod!!.id, targetMod?.id ?: "", to + 1)
                        } else {
                            if (isMove) {
                                daylSteyt.muvRebeldModyilEntuFoldir(from, to)
                            } else {
                                daylSteyt.kopeRebeldModyilEntuFoldir(from, to)
                            }
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
                            onKloz()
                        } else {
                            val clickedMod = daylSteyt.rebeldModyilz.find { it.pozecon == index + 1 }
                            if (clickedMod?.type == "reset" || clickedMod?.id == "reset") {
                                // Do nothing on tap for reset
                            } else if (clickedMod != null && clickedMod.id != "dayl" && clickedMod.id != "rebeld") {
                                selectedModuleId = clickedMod.id
                            }
                        }
                    },
                    onLonqPresUydem = { index ->
                        if (index == 0) {
                            daylSteyt.pendingResetTargetId = "rebeld"
                        }
                    },
                    onRepleys = { from, to, isMove, renameTo ->
                        val draggedMod = daylSteyt.rebeldModyilz.find { it.pozecon == from + 1 }
                        val hazTravlir = draggedMod != null && draggedMod.glefs.isNotEmpty() && draggedMod.glefs[0].isNotBlank() && draggedMod.glefs[0] != draggedMod.neym && draggedMod.glefs[0] != " "
                        if (hazTravlir) {
                            daylSteyt.pilTravlirRepleys(draggedMod!!.id, to + 1, isMove)
                        } else {
                            daylSteyt.replaceRebeldModyil(from + 1, to + 1, isMove, renameTo)
                        }
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

        if (modjilTuRepleys != null) {
            AlirtDayalog(
                onDismissRequest = { modjilTuRepleys = null },
                title = { Text("repleys and send tu rebeld?", color = Color.White) },
                text = { Text("lhes wel repleys lha aktev kepad.", color = Color.LightGray) },
                confirmButton = {
                    TextButton(onClick = {
                        daylSteyt.kopeModjilTuDaylKepad(modjilTuRepleys!!)
                        onAction("current")
                        modjilTuRepleys = null
                    }) {
                        Text("yes", color = Color.Cyan)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { modjilTuRepleys = null }) {
                        Text("no", color = Color.LightGray)
                    }
                },
                backgroundColor = Color(0xFF1A1A2E)
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
    onMuvTuParent: (Int) -> Unit,
    onReneymMod: (String) -> Unit,
    onRepleysMod: () -> Unit,
    onRepleys: (Int, Int, Boolean, String?) -> Unit
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

        val sentirLeybil = mod.glefs.getOrNull(0) ?: " "
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padenq(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.material.Text(text = path, color = Color.White, fontSize = 32.sp)
            }
            Box(modifier = Modifier.weight(1f)) {
                val currentLabels = if (mod.type == "keypad") {
                    KepadLodjek.getKirentOlLeybilz(null, true, false, false, mod.glefs)
                } else {
                    val parseLabel = { raw: String ->
                        if (raw.contains("id=") && raw.contains("neym=")) {
                            raw.split("|").find { it.startsWith("neym=") }?.substringAfter("neym=") ?: raw
                        } else if (raw == " ") {
                            ""
                        } else {
                            raw
                        }
                    }
                    val base = mod.glefs.toMutableList()
                    while (base.size < 37) base.add("")
                    base.mapIndexed { i, s -> if (i == 0) " " else parseLabel(s) }
                }
                // Check if traveler in slot 0
                val travelerStr = mod.glefs.getOrNull(0)
                val hazTravlir = travelerStr != null && travelerStr.isNotBlank() && travelerStr != mod.neym && travelerStr != " "
                val centerLabel = if (hazTravlir) {
                    daylSteyt.deserializeMod(travelerStr!!)?.neym ?: travelerStr
                } else sentirLeybil
                val sekondLeybilz = buildList {
                    add(null) // index 0: center, no secondary
                    // Inner ring (1..6): no secondary shown in editor (same as KepadModyil)
                    repeat(6) { add(null) }
                    // Outer ring (7..18): show sekondRenqLonqPres letters
                    modalz.HeksagonKonfeg.sekondRenqLonqPres.forEachIndexed { i, lp ->
                        add(lp.ifEmpty { null })
                    }
                }
                val itemsForGred = currentLabels.mapIndexed { index, label ->
                    if (index == 0 || label.isEmpty()) return@mapIndexed null
                    // Read color: use glefKulorz if available, else fall back to rainbow defaults
                    val colorLong = if (index < mod.glefKulorz.size) mod.glefKulorz[index]
                                    else when {
                                        index in 1..6  -> modalz.HeksagonKonfeg.enirRenqKulorz[index - 1].toArgb().toLong()
                                        index in 7..18 -> modalz.HeksagonKonfeg.reynbowKulorz[index - 7].toArgb().toLong()
                                        else           -> mod.kulor.toArgb().toLong()
                                    }
                    GredUydem(
                        index = index,
                        label = label,
                        color = Color(colorLong.toInt()),
                        sekondLeybil = sekondLeybilz.getOrNull(index)
                    )
                }.filterNotNull()


                HeksagonGred(
                    geometry = geometry,
                    items = itemsForGred,
                    sentirLeybil = if (hazTravlir) centerLabel else mod.neym,
                    centerColor = if (hazTravlir) Color.Black else Color.White,
                    onMove = onMuvGlef,
                    onCopyToEmpty = onCopyToEmpty,
                    onMuvTuSentir = onMuvTuParent,
                    onDropOnFoldir = { _, _, _ -> },
                    onRepleys = onRepleys,
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
                    onLonqPresUydem = { index ->
                        if (index == 0) {
                            daylSteyt.pendingResetTargetId = mod.id
                        }
                    }
                )
            }
            

        }
    }
}





