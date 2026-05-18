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
            .background(Color.Black.copy(alpha = 0.9f))
            .padding(16.dp)
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight
        
        // Use exact cluster geometry for fitting:
        // On WearOS, fit at least 1 ring (3 hexes wide, 4 radii tall)
        // On Mobile, fit at least 2 rings (5 hexes wide, 8 radii tall)
        val fitRings = if (yuteledez.isWearOS) 1.0 else 2.0
        val fitWidth = (fitRings * 2.0 + 1.0) * sqrt(3.0)
        val fitHeight = (fitRings * 2.0 + 2.0) * 2.0 // Radii height

        val hexSize = minOf(
            (screenWidth.value * 0.98) / fitWidth,
            (screenHeight.value * 0.98) / 8.0 // Keep 2-ring height as standard limit
        ).dp
        
        val geometry = remember(hexSize) {
            HeksagonDjeyometre(
                heksSayz = hexSize.value.toDouble(),
                sentir = HeksagonPozecon(0.0, 0.0),
                ezLeterMod = true
            )
        }

        val daylModule = daylSteyt.modyilz.find { it.type == "hub" } ?: daylSteyt.modyilz.first()
        val gredItems = daylSteyt.modyilz.filter { it.type != "hub" }.map { mod ->
            GredItem(
                index = mod.pozecon - 1,
                label = mod.neym,
                color = mod.kulor,
                isFolder = mod.type == "keypad",
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
                    text = "beld modyil",
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
                    fontSizeFactor = 0.458f
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

        val hexSize = remember(activeIndices, screenWidth, screenHeight) {
            val tempGeo = HeksagonDjeyometre(heksSayz = 1.0, sentir = HeksagonPozecon(0.0, 0.0))
            val positions = activeIndices.map { idx ->
                val axial = tempGeo.indeksTuAksyal(idx)
                tempGeo.aksyalTuPeksel(axial.q, axial.r)
            }
            
            val unitHexWidth = sqrt(3.0)
            val unitHexHeight = 2.0
            
            val minX = positions.minOf { it.x }
            val maxX = positions.maxOf { it.x }
            val minY = positions.minOf { it.y }
            val maxY = positions.maxOf { it.y }
            
            // On WearOS, we want to ensure we fit at least a 1-ring cluster area
            // On Mobile, at least a 2-ring cluster area.
            val minFitRings = if (yuteledez.isWearOS) 1.0 else 2.0
            val minFitWidth = (minFitRings * 2.0 + 1.0) * unitHexWidth
            val minFitHeight = (minFitRings * 2.0 + 2.0) * 1.5 // 1.5 radii per ring-height

            val contentWidth = maxOf(maxX - minX + unitHexWidth, minFitWidth)
            val contentHeight = maxOf(maxY - minY + unitHexHeight, minFitHeight)
            
            val sizeW = (screenWidth.value * 0.98) / contentWidth
            val sizeH = (screenHeight.value * 0.98) / contentHeight
            minOf(sizeW, sizeH).coerceAtLeast(10.0).dp
        }

        val geometry = remember(hexSize, activeIndices) {
            val tempGeo = HeksagonDjeyometre(heksSayz = 1.0, sentir = HeksagonPozecon(0.0, 0.0))
            val positions = activeIndices.map { idx ->
                val axial = tempGeo.indeksTuAksyal(idx)
                tempGeo.aksyalTuPeksel(axial.q, axial.r)
            }
            val unitCenterX = (positions.minOf { it.x } + positions.maxOf { it.x }) / 2.0
            val unitCenterY = (positions.minOf { it.y } + positions.maxOf { it.y }) / 2.0
            HeksagonDjeyometre(
                heksSayz = hexSize.value.toDouble(),
                sentir = HeksagonPozecon(-unitCenterX * hexSize.value, -unitCenterY * hexSize.value),
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
                        Text(text = "beld: ${mod.neym}", color = Color.White, fontSize = 20.sp)
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
                    fontSizeFactor = 1.0f
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