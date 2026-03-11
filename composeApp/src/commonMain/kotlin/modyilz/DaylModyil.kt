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

@Composable
fun DaylModyil(
    geometry: HeksagonDjeyometre,
    modyilz: List<ModyilDeyda>,
    onToggleModule: (Int) -> Unit,
    stackWidth: Dp,
    stackHeight: Dp
) {
    val daylModule = modyilz.find { it.id == "dayl" } ?: modyilz.first()
    // Ring modules are anything that is NOT the center 'dayl' hub
    val ringModules = modyilz.filter { it.id != "dayl" }.sortedBy { it.pozecon }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Center hexagon - only show if no other module is active? 
        // Or always show as the 'Hub' base.
        SentirModWedjet(
            label = daylModule.neym,
            geometry = geometry,
            backgroundColor = daylModule.kulor,
            textColor = KepadKonfeg.getComplementaryColor(daylModule.kulor),
            fontSize = (geometry.heksWidlx.toFloat() * 0.75f), // Tripled font size
            onTap = { onToggleModule(daylModule.pozecon) }
        )

        // Inner ring layout
        EnirRenqWedjet(
            geometry = geometry,
            stackWidth = stackWidth,
            stackHeight = stackHeight
        ) {
            ringModules.forEach { module ->
                HeksagonWedjet(
                    label = module.neym,
                    backgroundColor = module.kulor,
                    textColor = KepadKonfeg.getComplementaryColor(module.kulor),
                    size = geometry.heksWidlx.toFloat().dp,
                    fontSize = (geometry.heksWidlx.toFloat() * 0.75f), // Tripled font size
                    isPressed = module.ezAktiv,
                    rotationAngle = geometry.roteyconAngol.toFloat(),
                    onTap = { onToggleModule(module.pozecon) }
                )
            }
        }
    }
}
