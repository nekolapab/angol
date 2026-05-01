package modyilz

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import modalz.ModyilDeyda
import steyt.DaylSteyt
import wedjets.GredItem
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
    var editingModuleId by remember { mutableStateOf<String?>(null) }
    var newNeym by remember { mutableStateOf("") }

    if (selectedModuleId != null) {
        val mod = daylSteyt.modyilz.find { it.id == selectedModuleId }
        if (mod != null) {
            GlefzEditSkren(
                mod = mod,
                onBack = { selectedModuleId = null },
                onReneymGlef = { index, label ->
                    daylSteyt.reneymGlef(mod.id, index, label)
                    onAction()
                },
                onSwopGlefz = { from, to ->
                    daylSteyt.swopGlefz(mod.id, from, to)
                    onAction()
                },
                onCopyToEmpty = { from, to ->
                    daylSteyt.kopeGlefTuEmpt(mod.id, from, to)
                    onAction()
                },
                onMoveToParent = { from ->
                    daylSteyt.muvGlefTuHub(mod.id, from)
                    onAction()
                }
            )
            return
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "beldir modyil",
                color = Color.White,
                fontSize = 24.sp,
                style = MaterialTheme.typography.h5
            )
            Button(onClick = onClose) {
                Text("kloz")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(daylSteyt.modyilz) { mod ->
                ModyilRow(
                    mod = mod,
                    isEditing = editingModuleId == mod.id,
                    newNeym = if (editingModuleId == mod.id) newNeym else mod.neym,
                    onNeymChange = { newNeym = it },
                    onEdit = {
                        editingModuleId = mod.id
                        newNeym = mod.neym
                    },
                    onSave = {
                        daylSteyt.reneymModyil(mod.id, newNeym)
                        editingModuleId = null
                        onAction()
                    },
                    onCancel = { editingModuleId = null },
                    onCopy = { 
                        daylSteyt.kopeModyil(mod.id)
                        onAction()
                    },
                    onDelete = { 
                        daylSteyt.deletModyil(mod.id)
                        onAction()
                    },
                    onSelect = { selectedModuleId = mod.id }
                )
            }
        }
    }
}

@Composable
fun ModyilRow(
    mod: ModyilDeyda,
    isEditing: Boolean,
    newNeym: String,
    onNeymChange: (String) -> Unit,
    onEdit: () -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    onCopy: () -> Unit,
    onDelete: () -> Unit,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onSelect() },
        backgroundColor = Color.DarkGray,
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (isEditing) {
                TextField(
                    value = newNeym,
                    onValueChange = onNeymChange,
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = Color.White,
                        backgroundColor = Color.Black
                    )
                )
                IconButton(onClick = onSave) {
                    Icon(Icons.Default.Edit, contentDescription = "Save", tint = Color.Green)
                }
                IconButton(onClick = onCancel) {
                    Text("X", color = Color.Red)
                }
            } else {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = mod.neym, color = Color.White, fontSize = 18.sp)
                    Text(text = "id: ${mod.id}", color = Color.Gray, fontSize = 12.sp)
                }
                
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Rename", tint = Color.Cyan)
                    }
                    IconButton(onClick = onCopy) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color.Yellow)
                    }
                    if (mod.id != "dayl" && mod.id != "keypad" && mod.id != "beldir") {
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GlefzEditSkren(
    mod: ModyilDeyda,
    onBack: () -> Unit,
    onReneymGlef: (Int, String) -> Unit,
    onSwopGlefz: (Int, Int) -> Unit,
    onCopyToEmpty: (Int, Int) -> Unit,
    onMoveToParent: (Int) -> Unit
) {
    var editingGlefIndex by remember { mutableStateOf<Int?>(null) }
    var newGlefLabel by remember { mutableStateOf("") }

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
            GredItem(
                index = index,
                label = label,
                color = mod.kulor
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = onBack) { Text("Bek") }
                Text(text = "beldir: ${mod.neym}", color = Color.White, fontSize = 20.sp)
                Spacer(modifier = Modifier.width(48.dp))
            }

            Box(modifier = Modifier.weight(1f)) {
                HeksagonGred(
                    geometry = geometry,
                    items = gredItems,
                    centerLabel = "Hub", // Center moves to parent Hub
                    centerColor = Color.DarkGray,
                    onSwap = onSwopGlefz,
                    onCopyToEmpty = onCopyToEmpty,
                    onMoveToCenter = onMoveToParent,
                    onDropOnFolder = { from, to -> /* Handle folder drop if needed */ }
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
                    "Long-press tu drag. Center tu Hub. Empty tu Kope.",
                    color = Color.Gray,
                    modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
                )
            }
        }
        
        // Overlay for tap-to-rename since HeksagonGred consumes gestures for drag
        // We might want to add an 'onTap' to GredItem or HeksagonGred
    }
}
