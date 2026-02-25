package wedjets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import modalz.KepadKonfeg
import sirvesez.EnpitSirves
import yuteledez.HeksagonDjeyometre
import kotlin.math.roundToInt

/**
 * A layout composable that generates and arranges hexagon widgets in an outer ring.
 *
 * This composable creates its own children (`HeksagonWedjet`) based on the provided
 * labels and arranges them in a circle using a custom `Layout`. It observes state
 * from `EnpitSirves` to adjust its appearance.
 *
 * @param onHexKeyPress Callback for when a hexagon key is pressed.
 *                      Params: (label: String, isLongPress: Boolean, primaryChar: String?)
 */
@Composable
fun AwdirRenqWedjet(
    modifier: Modifier = Modifier,
    geometry: HeksagonDjeyometre,
    onHexKeyPress: (String, Boolean, String?) -> Unit,
    tapLabels: List<String>,
    longPressLabels: List<String>,
    enpitSirves: EnpitSirves? = null,
    initialLetterMode: Boolean = true,
    onHover: ((Boolean) -> Unit)? = null,
    stackWidth: Dp,
    stackHeight: Dp,
    pressedIndex: Int? = null,
    handleGestures: Boolean = true,
    isPopup: Boolean = false
) {
    val isLetterMode = if (enpitSirves != null) enpitSirves.isLetterMode.collectAsState().value else initialLetterMode
    val outerCoords = remember(geometry) { geometry.getAwdirRenqKowordenats() }
    val density = LocalDensity.current

    Layout(
        modifier = modifier,
        content = {
            // Generate the HeksagonWedjet children dynamically
            outerCoords.forEachIndexed { index, _ ->
                if (index < tapLabels.size) {
                    val tapLabel = tapLabels[index]
                    val longPressLabel = longPressLabels.getOrNull(index) ?: ""
                    val hexColor = KepadKonfeg.rainbowColors[index]
                    
                    val textColor = KepadKonfeg.getComplementaryColor(hexColor)

                    val hexSizeDp = with(density) { geometry.heksWidlx.toFloat().toDp() }

                    HeksagonWedjet(
                        label = tapLabel,
                        secondaryLabel = if (isLetterMode && longPressLabel.isNotEmpty()) longPressLabel else null,
                        backgroundColor = hexColor,
                        textColor = textColor,
                        size = hexSizeDp,
                        fontSize = (geometry.heksWidlx * if (isLetterMode) 0.6 else 0.8).toFloat(),
                        verticalOffset = 0.dp, // Reset to centered
                        rotationAngle = geometry.roteyconAngol.toFloat(),
                        isPressed = pressedIndex == index,
                        onTap = if (handleGestures) { { onHexKeyPress(tapLabel, false, null) } } else null,
                        onLongPress = if (handleGestures && isLetterMode && longPressLabel.isNotEmpty()) {
                            { onHexKeyPress(longPressLabel, true, tapLabel) }
                        } else null,
                        onHover = onHover
                    )
                }
            }
        }
    ) { measurables, constraints ->
        val stackWidthPx = stackWidth.toPx()
        val stackHeightPx = stackHeight.toPx()

        val hexWidthPx = geometry.heksWidlx.toFloat()
        val hexHeightPx = geometry.heksHayt.toFloat()

        // Measure each child with fixed constraints based on hexagon geometry.
        val childConstraints = androidx.compose.ui.unit.Constraints.fixed(
            hexWidthPx.roundToInt(),
            hexHeightPx.roundToInt()
        )
        val placeables = measurables.map { it.measure(childConstraints) }

        layout(stackWidthPx.roundToInt(), stackHeightPx.roundToInt()) {
            placeables.forEachIndexed { index, placeable ->
                if (index < outerCoords.size) {
                    val coord = outerCoords[index]
                    val position = geometry.aksyalTuPeksel(coord.q, coord.r)

                    val x = stackWidthPx / 2 + position.x - (placeable.width / 2)
                    val y = stackHeightPx / 2 + position.y - (placeable.height / 2)

                    placeable.placeRelative(x.roundToInt(), y.roundToInt())
                }
            }
        }
    }
}
