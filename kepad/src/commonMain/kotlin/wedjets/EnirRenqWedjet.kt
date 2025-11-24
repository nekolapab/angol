package wedjets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Dp
import yuteledez.HeksagonDjeyometre
import kotlin.math.roundToInt

/**
 * A layout composable that arranges its children in a hexagonal inner ring.
 *
 * This is the Compose equivalent of the original Flutter widget that used a Stack
 * with Positioned children. It uses the custom `Layout` composable to measure and
 * place its children according to the hexagonal geometry.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param geometry The hexagon geometry utility to calculate positions.
 * @param stackWidth The total width of the layout area.
 * @param stackHeight The total height of the layout area.
 * @param content The child composables to be laid out.
 */
@Composable
fun EnirRenqWedjet(
    modifier: Modifier = Modifier,
    geometry: HeksagonDjeyometre,
    stackWidth: Dp,
    stackHeight: Dp,
    content: @Composable () -> Unit
) {
    // Get the coordinates for the inner ring.
    // `remember` ensures this list is not recalculated on every recomposition.
    val innerCoords = remember(geometry) { geometry.getInnerRingCoordinates() }

    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val stackWidthPx = stackWidth.toPx()
        val stackHeightPx = stackHeight.toPx()

        // Measure each child composable passed in the content lambda.
        val placeables = measurables.map { it.measure(constraints.copy(minWidth = 0, minHeight = 0)) }

        // Define the size of the layout.
        layout(stackWidthPx.roundToInt(), stackHeightPx.roundToInt()) {
            // Place each child at its calculated hexagonal position.
            placeables.forEachIndexed { index, placeable ->
                if (index < innerCoords.size) {
                    val coord = innerCoords[index]
                    val position = geometry.axialToPixel(coord.q, coord.r)

                    // Calculate the top-left (x, y) for placing the composable,
                    // ensuring it's centered on its hexagonal grid position.
                    val x = stackWidthPx / 2 + position.x - (placeable.width / 2)
                    val y = stackHeightPx / 2 + position.y - (placeable.height / 2)

                    placeable.placeRelative(x.roundToInt(), y.roundToInt())
                }
            }
        }
    }
}
