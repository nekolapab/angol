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
fun BeldirModyil(
    daylSteyt: DaylSteyt,
    onClose: () -> Unit,
    onAction: () -> Unit = {}
) {
    var selectedModuleId by remember { mutableStateOf<String?>(null) }

    if (selectedModuleId != null) {
        val mod = daylSteyt.modyilz.find { it.id == selectedModuleId }
        if (mod != null) {
            GlefsEdetSkren(
                mod = mod,
                onBack = { selectedModuleId = null },
                onReneymGlef = { index, label ->
                    daylSteyt.reneymGlef(mod.id, index, label)
                    onAction()
                },
                onMuvGlef = { from, to ->
                    daylSteyt.muvGlef(mod.id, from, to)
                    onAction()
                },
                onCopyToEmpty = { from, to ->
                    daylSteyt.kopeGlefTuEmpt(mod.id, from, to)
                    onAction()
                },
                onMoveToParent = { from ->
                    daylSteyt.muvGlefTuHub(mod.id, from)
                    onAction()
                },
                onReneymMod = { newNeym ->
                    daylSteyt.reneymModyil(mod.id, newNeym)
                    onAction()
                }
            )
            return
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f))
            .padding(16.dp)
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight
        val hexSize = minOf(screenWidth.value / (5.0 * sqrt(3.0)), screenHeight.value / 10.0).dp
        
        val geometry = remember(hexSize) {
            HeksagonDjeyometre(
                heksSayz = hexSize.value.toDouble(),
                sentir = HeksagonPozecon(0.0, 0.0),
                ezLeterMod = true
            )
        }

        val daylModule = daylSteyt.modyilz.find { it.id == "dayl" } ?: daylSteyt.modyilz.first()
        val gredItems = daylSteyt.modyilz.filter { it.id != "dayl" }.map { mod ->
            GredItem(
                index = mod.pozecon - 1,
                label = mod.neym,
                color = mod.kulor,
                isFolder = false,
                deyda = mod
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "beldir modyil",
                    color = Color.White,
                    fontSize = 24.sp,
                    style = MaterialTheme.typography.h5
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                HeksagonGred(
                    geometry = geometry,
                    items = gredItems,
                    centerLabel = daylModule.neym,
                    centerColor = daylModule.kulor,
                    copyDragPolicy = CopyDragPolicy.TwoStepArmed,
                    onMove = { from, to ->
                        daylSteyt.muvModyil(from + 1, to + 1)
                        onAction()
                    },
                    onCopyToEmpty = { from, to ->
                        daylSteyt.kopeModyilTuEmpt(from + 1, to + 1)
                        onAction()
                    },
                    onMoveToCenter = { from ->
                        daylSteyt.muvModyilTuParent(from + 1)
                        onAction()
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
                    }
                )
            }
            
            Text(
                "Dreq tu kope. Tap tu edet/arm muv. Lonq-pres agen den dreq tu skwez/muv. Center → gow bak.",
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp).align(Alignment.CenterHorizontally)
            )
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
        val hexSize = minOf(screenWidth.value / (5.0 * sqrt(3.0)), screenHeight.value / 10.0).dp
        
        val geometry = remember(hexSize) {
            HeksagonDjeyometre(
                heksSayz = hexSize.value.toDouble(),
                sentir = HeksagonPozecon(0.0, 0.0),
                ezLeterMod = true
            )
        }

        val gredItems = mod.glefz.mapIndexed { index, label ->
            if (index == 0 || label.isEmpty()) return@mapIndexed null
            val ringIndex = index - 1
            val color = when {
                ringIndex < 6 -> KepadKonfeg.innerRingColors.getOrNull(ringIndex) ?: mod.kulor
                ringIndex < 18 -> KepadKonfeg.rainbowColors.getOrNull(ringIndex - 6) ?: mod.kulor
                else -> mod.kulor
            }
            GredItem(
                index = index,
                label = label,
                color = color
            )
        }.filterNotNull()

        val centerLabel = mod.glefz.getOrNull(0) ?: " "

        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
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
                        Text(text = "beldir: ${mod.neym}", color = Color.White, fontSize = 20.sp)
                        Icon(Icons.Default.Edit, "Rename", tint = Color.Cyan, modifier = Modifier.size(16.dp).padding(start = 4.dp))
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
                            colors = TextFieldDefaults.textFieldColors(textColor = Color.White)
                        )
                        Button(onClick = {
                            onReneymGlef(editingGlefIndex!!, newGlefLabel)
                            editingGlefIndex = null
                        }) { Text("Save") }
                        IconButton(onClick = { editingGlefIndex = null }) { Text("X", color = Color.Red) }
                    }
                }
            } else {
                Text(
                    "Dreq tu kope. Tap tu edet/arm muv. Lonq-pres agen den dreq tu skwez/muv. Center → gow bak.",
                    color = Color.Gray,
                    modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}
