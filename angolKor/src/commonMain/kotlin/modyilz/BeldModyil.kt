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
import modalz.KepadKonfeg
import steyt.DaylSteyt
import wedjets.GredItem
import wedjets.CopyDragPolicy
import wedjets.HeksagonGred
import yuteledez.HeksagonDjeyometre
import modalz.HeksagonPozecon
import kotlin.math.sqrt

@Composable
fun BeldModyil(
    daylSteyt: DaylSteyt,
    onClose: () -> Unit,
    onAction: (String) -> Unit = {}
) {
    var selectedModuleId by remember { mutableStateOf<String?>(null) }
    
    val syncBoth = {
        onAction("current")
        onAction("production")
    }

    if (selectedModuleId != null) {
        val mod = daylSteyt.modyilz.find { it.id == selectedModuleId }
        if (mod != null) {
            GlefsEdetSkren(
                mod = mod,
                onBack = { selectedModuleId = null },
                onReneymGlef = { index, label ->
                    daylSteyt.reneymGlef(mod.id, index, label)
                    syncBoth()
                },
                onMuvGlef = { from, to ->
                    daylSteyt.muvGlef(mod.id, from, to)
                    syncBoth()
                },
                onCopyToEmpty = { from, to ->
                    daylSteyt.kopeGlefTuEmpt(mod.id, from, to)
                    syncBoth()
                },
                onMoveToParent = { from ->
                    daylSteyt.muvGlefTuHub(mod.id, from)
                    syncBoth()
                },
                onReneymMod = { newNeym ->
                    daylSteyt.reneymModyil(mod.id, newNeym)
                    syncBoth()
                }
            )
            return
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 11f / 12f))
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight
        
        val gredItems = daylSteyt.modyilz.filter { it.type != "hub" }.map { mod ->
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

        val daylModule = daylSteyt.modyilz.find { it.type == "hub" } ?: daylSteyt.modyilz.first()

        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "beld modyil",
                    color = Color.White.copy(alpha = 6f / 12f),
                    style = MaterialTheme.typography.caption
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                HeksagonGred(
                    geometry = geometry,
                    items = gredItems,
                    centerLabel = daylModule.neym,
                    centerColor = daylModule.kulor,
                    copyDragPolicy = CopyDragPolicy.TwoStepArmed,
                    allowSwap = false,
                    onMove = { from, to ->
                        daylSteyt.swopModyilz(from + 1, to + 1)
                        syncBoth()
                    },
                    onCopyToEmpty = { from, to ->
                        daylSteyt.kopeModyilTuEmpt(from + 1, to + 1)
                        syncBoth()
                    },
                    onMoveToCenter = { from ->
                        daylSteyt.muvModyilTuParent(from + 1)
                        syncBoth()
                    },
                    onDropOnFolder = { _, _ -> },
                    onTap = { index ->
                        if (index == 0) { // Center
                            onClose()
                        } else {
                            val clickedMod = daylSteyt.modyilz.find { it.pozecon == index + 1 }
                            if (clickedMod != null) {
                                selectedModuleId = clickedMod.id
                            }
                        }
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
    onReneymMod: (String) -> Unit
) {
    var editingGlefIndex by remember { mutableStateOf<Int?>(null) }
    var newGlefLabel by remember { mutableStateOf("") }
    var isEditingModNeym by remember { mutableStateOf(false) }
    var newModNeym by remember { mutableStateOf(mod.neym) }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(Color.Black)) {
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

        val gredItems = mod.glefz.mapIndexed { index, label ->
            if (index == 0 || label.isEmpty()) return@mapIndexed null

            val colorLong = mod.glefKulorz.getOrNull(index) ?: mod.kulor.toArgb().toLong()
            val color = Color(colorLong.toInt())
            
            GredItem(
                index = index,
                label = label,
                color = color
            )
        }.filterNotNull()

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
                        Text(text = "beld: ${mod.neym}", color = Color.White.copy(alpha = 6f / 12f), style = MaterialTheme.typography.caption)
                        Icon(Icons.Default.Edit, "Rename", tint = Color.Cyan, modifier = Modifier.size(12.dp).padding(start = 4.dp))
                    }
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                HeksagonGred(
                    geometry = geometry,
                    items = gredItems,
                    centerLabel = centerLabel,
                    centerColor = Color.DarkGray,
                    copyDragPolicy = CopyDragPolicy.TwoStepArmed,
                    allowSwap = false,
                    onMove = onMuvGlef,
                    onCopyToEmpty = onCopyToEmpty,
                    onMoveToCenter = onMoveToParent,
                    onDropOnFolder = { from, to -> },
                    onTap = { index ->
                        if (index == 0) onBack()
                        else {
                            editingGlefIndex = index
                            newGlefLabel = mod.glefz.getOrNull(index) ?: ""
                        }
                    },
                    fontSizeFactor = 12f / 12f
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