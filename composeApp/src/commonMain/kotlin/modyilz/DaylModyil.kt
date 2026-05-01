package modyilz

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import yuteledez.HeksagonDjeyometre
import modalz.ModyilDeyda
import modalz.KepadKonfeg
import wedjets.SentirModWedjet
import wedjets.EnirRenqWedjet
import wedjets.HeksagonWedjet
import wedjets.HeksagonGred
import wedjets.GredItem

@Composable
fun DaylModyil(
    geometry: HeksagonDjeyometre,
    modyilz: List<ModyilDeyda>,
    onToggleModule: (Int) -> Unit,
    onSwapModules: (Int, Int) -> Unit,
    onCopyToEmpty: (Int, Int) -> Unit,
    onMoveToCenter: (Int) -> Unit,
    stackWidth: Dp,
    stackHeight: Dp
) {
    val daylModule = modyilz.find { it.id == "dayl" } ?: modyilz.first()
    
    // Map modules to GredItem for HeksagonGred
    val gredItems = modyilz.filter { it.id != "dayl" }.map { mod ->
        GredItem(
            index = mod.pozecon,
            label = mod.neym,
            color = mod.kulor,
            isFolder = true, // Modules act as folders for glefz
            deyda = mod
        )
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        HeksagonGred(
            geometry = geometry,
            items = gredItems,
            centerLabel = daylModule.neym,
            centerColor = daylModule.kulor,
            onSwap = onSwapModules,
            onCopyToEmpty = onCopyToEmpty,
            onMoveToCenter = onMoveToCenter,
            onDropOnFolder = { from, to -> /* Handle folder drop */ },
            modifier = Modifier.fillMaxSize()
        )
    }
}
