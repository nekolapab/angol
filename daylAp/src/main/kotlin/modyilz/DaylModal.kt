package modyilz

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import steyt.DaylSteyt
import yuteledez.HeksagonDjeyometre
import modalz.ModyilDeyda
import modalz.HeksagonKonfeg
import wedjets.Heksagon
import wedjets.HeksagonGred
import wedjets.GredItem
import wedjets.CopyDragPolicy

@Composable
fun DaylModal(
    geometry: HeksagonDjeyometre,
    modyilz: List<ModyilDeyda>,
    onToggleModule: (Int) -> Unit,
    onMoveModule: (Int, Int) -> Unit,
    onCopyToEmpty: (Int, Int) -> Unit,
    onMoveToCenter: (Int) -> Unit,
    onDropOnFoldir: (Int, Int) -> Unit,
    stackWidth: Dp,
    stackHeight: Dp,
    allowSwap: Boolean = true,
    onReplace: ((Int, Int) -> Unit)? = null
) {
    val daylModule = modyilz.find { it.id == "dayl" } ?: modyilz.first()
    val gredItems = modyilz.filter { it.id != "dayl" }.map { mod ->
        GredItem(
            index = mod.pozecon - 1,
            label = mod.neym,
            color = mod.kulor,
            isFolder = mod.type == "keypad" || mod.type == "beld",
            deyda = mod
        )
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        HeksagonGred(
            geometry = geometry,
            items = gredItems,
            centerLabel = daylModule.neym,
            centerColor = daylModule.kulor,
            onMove = onMoveModule,
            onCopyToEmpty = onCopyToEmpty,
            onMoveToCenter = onMoveToCenter,
            onDropOnFoldir = onDropOnFoldir,
            onReplace = onReplace,
            modifier = Modifier.fillMaxSize(),
            onTap = onToggleModule,
            allowSwap = allowSwap,
            fontSizeFactor = 10f / 12f
        )
    }
}
