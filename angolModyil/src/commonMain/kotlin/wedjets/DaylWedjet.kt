package wedjets

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import yuteledez.HeksagonDjeyometre
import modalz.ModyilDeyda
import modalz.HeksagonKonfeg
import androidx.compose.ui.graphics.Color

@Composable
fun DaylWedjet(
    geometry: HeksagonDjeyometre,
    modyilz: List<ModyilDeyda>,
    activeParentId: String,
    onToggleModule: (Int) -> Unit,
    onMuvModjil: (Int, Int) -> Unit,
    onCopyToEmpty: (Int, Int) -> Unit,
    onMuvTuSentir: (Int, Boolean) -> Unit,
    onDropOnFoldir: (Int, Int, Boolean) -> Unit,
    stackWidth: Dp,
    stackHeight: Dp,
    allowSwap: Boolean = true,
    onRepleys: ((Int, Int, Boolean, String?) -> Unit)? = null,
    onRotate: ((Double) -> Unit)? = null,
    onLonqPresUydem: ((Int) -> Unit)? = null
) {
    val filteredModyilz = modyilz.filter { it.parentId == activeParentId || it.id == if (activeParentId == "dayl") "angol" else "dayl" }
    
    val gredItems = filteredModyilz.mapNotNull { mod ->
        val hazTravlir = mod.glefs.isNotEmpty() && mod.glefs[0].isNotBlank() && mod.glefs[0] != mod.neym && mod.glefs[0] != " "
        val label = if (hazTravlir) mod.glefs[0] else mod.neym
        
        val rawColor = if (mod.id == "dayl" && (mod.kulorLong == 0L || mod.kulorLong == 4278190080L || mod.kulorLong == -16777216L)) {
            Color(0xFFFF0000)
        } else {
            mod.kulor
        }
        val finalColor = if (hazTravlir) Color.Black else if (mod.ezAkdev) Color.White else rawColor
        GredUydem(
            index = mod.pozecon - 1,
            label = label,
            color = finalColor,
            isFolder = (mod.type == "kepad" || mod.type == "rebeld" || mod.type == "beld" || mod.id == "beldir"),
            deyda = mod
        )
    }.toMutableList()

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        HeksagonGred(
            geometry = geometry,
            items = gredItems,
            sentirLeybil = activeParentId,
            centerColor = if (filteredModyilz.any { it.ezAkdev }) Color.Black else Color.White,
            onMove = onMuvModjil,
            onCopyToEmpty = onCopyToEmpty,
            onMuvTuSentir = onMuvTuSentir,
            onDropOnFoldir = onDropOnFoldir,
            onRepleys = onRepleys,
            onRotate = onRotate,
            onLonqPresUydem = onLonqPresUydem,
            modifier = Modifier.fillMaxSize(),
            onTap = onToggleModule,
            allowSwap = allowSwap,
            fontSizeFactor = 10f / 12f,
            fixedLabelLength = 5f
        )
    }
}


