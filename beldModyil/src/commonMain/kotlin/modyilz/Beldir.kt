package modyilz

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.toArgb
import modalz.ModyilDeyda
import modalz.HeksagonKonfeg
import steyt.DaylSteyt
import yuteledez.KepadLodjek
import wedjets.GredItem
import wedjets.CopyDragPolicy
import wedjets.HeksagonGred
import yuteledez.HeksagonDjeyometre
import modalz.HeksagonPozecon
import modyilz.PlatformServices
import modyilz.VoiceService
import modyilz.KeyboardController
import kotlin.math.sqrt

@Composable
fun Beldir(
    daylSteyt: DaylSteyt,
    keyboardController: KeyboardController?,
    platformServices: PlatformServices,
    voiceService: VoiceService,
    onClose: () -> Unit,
    onAction: (String) -> Unit = {},
    onDropOnFoldir: (Int, Int) -> Unit = { _, _ -> },
    onReplace: (Int, Int) -> Unit = { _, _ -> }
) {
    var selectedModuleId by remember { mutableStateOf<String?>(null) }
    
    val syncBeldir = {
        onAction("current")
    }

    if (selectedModuleId != null) {
        val mod = daylSteyt.beldirModyilz.find { it.id == selectedModuleId }
        if (mod != null) {
            GlefsEdetSkren(
                mod = mod,
                onBack = { selectedModuleId = null },
                onReneymGlef = { index, label ->
                    daylSteyt.reneymGlef(mod.id, index, label)
                    syncBeldir()
                },
                onMuvGlef = { from, to ->
                    daylSteyt.muvGlef(mod.id, from, to)
                    syncBeldir()
                },
                onCopyToEmpty = { from, to ->
                    daylSteyt.kopeGlefTuEmpt(mod.id, from, to)
                    syncBeldir()
                },
                onMoveToParent = { from ->
                    daylSteyt.muvGlefTuHub(mod.id, from)
                    syncBeldir()
                },
                onReneymMod = { newNeym ->
                    daylSteyt.reneymModyil(mod.id, newNeym)
                    syncBeldir()
                },
                onReplace = { from, to ->
                    daylSteyt.replaceGlef(mod.id, from, to)
                    syncBeldir()
                }
            )
            return
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0f / 12f))
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight
        
        val gredItems = daylSteyt.beldirModyilz.filter { it.type != "hub" }.map { mod ->
            GredItem(
                index = mod.pozecon - 1,
                label = mod.neym,
                color = mod.kulor,
                isFolder = mod.type == "keypad",
                deyda = mod
            )
        }
        
        val activeIndices = (gredItems.map { it.index } + 0).toList().sorted()
        
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
                ezLeterMod = true
            )
        }

        val daylModule = daylSteyt.beldirModyilz.find { it.type == "hub" } ?: daylSteyt.beldirModyilz.first()

        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "rebeld",
                    color = Color.White,
                    fontSize = 32.sp
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                HeksagonGred(
                    geometry = geometry,
                    items = gredItems,
                    centerLabel = daylModule.neym,
                    centerColor = daylModule.kulor,
                    copyDragPolicy = CopyDragPolicy.TwoStepArmed,
                    allowSwap = true,
                    onMove = { from, to ->
                        daylSteyt.swopBeldirModyilz(from + 1, to + 1)
                        syncBeldir()
                    },
                    onCopyToEmpty = { from, to ->
                        daylSteyt.kopeBeldirModyilTuEmpt(from + 1, to + 1)
                        syncBeldir()
                    },
                    onMoveToCenter = { from ->
                        daylSteyt.copyModuleToDaylKeypad(from)
                        onAction("current")
                    },
                    onDropOnFoldir = { from, to ->
                        daylSteyt.muvBeldirModyilEntuFoldir(from, to)
                        syncBeldir()
                    },
                    onDelete = { index ->
                        val mod = daylSteyt.beldirModyilz.find { it.pozecon == index + 1 }
                        if (mod != null) {
                            daylSteyt.deletModyil(mod.id)
                            syncBeldir()
                        }
                    },
                    onTap = { index ->
                        if (index == 0) { // Center
                            onClose()
                        } else {
                            val clickedMod = daylSteyt.beldirModyilz.find { it.pozecon == index + 1 }
                            if (clickedMod != null && clickedMod.id != "dayl" && clickedMod.id != "rebeld") {
                                selectedModuleId = clickedMod.id
                            }
                        }
                    },
                    onReplace = { from, to ->
                        daylSteyt.replaceBeldirModyil(from + 1, to + 1)
                        syncBeldir()
                    },
                    fontSizeFactor = 10f / 12f
                )
            }
        }
    }
}

@Composable
fun GlefsEdetSkren(
    mod: ModyilDeyda,
    onBack: () -> Unit,
    onReneymGlef: (Int, String) -> Unit,
    onMuvGlef: (Int, Int) -> Unit,
    onCopyToEmpty: (Int, Int) -> Unit,
    onMoveToParent: (Int) -> Unit,
    onReneymMod: (String) -> Unit,
    onReplace: (Int, Int) -> Unit
) {
    var editingGlefIndex by remember { mutableStateOf<Int?>(null) }
    var newGlefLabel by remember { mutableStateOf("") }
    var isEditingModNeym by remember { mutableStateOf(false) }
    var newModNeym by remember { mutableStateOf(mod.neym) }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0f / 12f))) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight
        
        val activeIndices = remember(mod.glefz) {
            val indices = mod.glefz.mapIndexedNotNull { i, s -> if (s.isNotEmpty()) i else null }.toMutableSet()
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

        val geometry = remember(gredDimz) {
            HeksagonDjeyometre(
                heksSayz = gredDimz.heksSayz,
                sentir = HeksagonPozecon(-gredDimz.unitCenterX * gredDimz.heksSayz, -gredDimz.unitCenterY * gredDimz.heksSayz),
                ezLeterMod = true
            )
        }

        val centerLabel = mod.glefz.getOrNull(0) ?: " "
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
                            colors = TextFieldDefaults.textFieldColors(textColor = Color.White)
                        )
                        IconButton(onClick = {
                            onReneymMod(newModNeym)
                            isEditingModNeym = false
                        }) { Icon(Icons.Default.Edit, "Save", tint = Color.Green) }
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { isEditingModNeym = true }) {
                        Text(text = "beld: ${mod.neym}", color = Color.White, fontSize = 32.sp)
                        Icon(Icons.Default.Edit, "Rename", tint = Color.Cyan, modifier = Modifier.size(24.dp).padding(start = 8.dp))
                    }
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                val currentLabels = KepadLodjek.getCurrentOlLeybelz(null, true, false, false, mod.glefz)
                val itemsForGred = currentLabels.mapIndexed { index, label ->
                    if (index == 0 || label.isEmpty()) return@mapIndexed null
                    val colorLong = mod.glefKulorz.getOrNull(index) ?: mod.kulor.toArgb().toLong()
                    GredItem(
                        index = index,
                        label = label,
                        color = Color(colorLong.toInt())
                    )
                }.filterNotNull()

                HeksagonGred(
                    geometry = geometry,
                    items = itemsForGred,
                    centerLabel = currentLabels.getOrNull(0) ?: " ",
                    centerColor = mod.kulor,
                    onMove = onMuvGlef,
                    onCopyToEmpty = onCopyToEmpty,
                    onMoveToCenter = onMoveToParent,
                    onDropOnFoldir = { _, _ -> },
                    onReplace = onReplace,
                    onTap = { index ->
                        if (index == 0) onBack()
                        else {
                            editingGlefIndex = index
                            newGlefLabel = currentLabels.getOrNull(index) ?: ""
                        }
                    },
                    fontSizeFactor = 13f/12f
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
