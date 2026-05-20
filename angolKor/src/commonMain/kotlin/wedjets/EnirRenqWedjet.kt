package wedjets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import yuteledez.HeksagonDjeyometre
import kotlin.math.roundToInt

/**
 * A layout composable that arranges its children in a hexagonal inner ring.
 */
@Composable
fun EnirRenqWedjet(
    modifier: Modifier = Modifier,
    geometry: HeksagonDjeyometre,
    stackWidth: Dp,
    stackHeight: Dp,
    content: @Composable () -> Unit
) {
    val innerCoords = remember(geometry) { geometry.getEnirRenqKowordenats() }

    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val stackWidthPx = stackWidth.toPx()
        val stackHeightPx = stackHeight.toPx()

        // geometry values (heksSayz, etc) are in DP. Convert to pixels for layout.
        val hexWidthPx = geometry.heksWidlx.dp.toPx()
        val hexHeightPx = geometry.heksHayt.dp.toPx()

        val childConstraints = androidx.compose.ui.unit.Constraints.fixed(
            hexWidthPx.roundToInt(),
            hexHeightPx.roundToInt()
        )
        val placeables = measurables.map { it.measure(childConstraints) }

        layout(stackWidthPx.roundToInt(), stackHeightPx.roundToInt()) {
            placeables.forEachIndexed { index, placeable ->
                if (index < innerCoords.size) {
                    val coord = innerCoords[index]
                    val positionDp = geometry.aksyalTuPeksel(coord.q, coord.r)

                    val x = stackWidthPx / 2 + positionDp.x.dp.toPx() - (placeable.width / 2)
                    val y = stackHeightPx / 2 + positionDp.y.dp.toPx() - (placeable.height / 2)

                    placeable.placeRelative(x.roundToInt(), y.roundToInt())
                }
            }
        }
    }
}
