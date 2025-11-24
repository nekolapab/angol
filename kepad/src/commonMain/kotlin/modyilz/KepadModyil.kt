package modyilz

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import modalz.HexagonPosition
import modalz.KepadKonfeg
import sirvesez.EnpitSirves
import wedjets.AwtpitTekstWedjet
import wedjets.HeksagonWedjet
import yuteledez.HeksagonDjeyometre
import kotlin.math.roundToInt

private data class HexHitbox(val index: Int, val label: String, val path: androidx.compose.ui.graphics.Path)

@Composable
fun KepadModyil(
    onHexKeyPress: (String, Boolean, String?) -> Unit,
    isKeypadVisible: Boolean,
    displayLength: Int
) {
    if (!isKeypadVisible) return

    val enpitSirves = remember { EnpitSirves() }
    val isLetterMode by enpitSirves.isLetterMode.collectAsState()
    val inputText by enpitSirves.inputText.collectAsState()
    var isCenterHexPressed by remember { mutableStateOf(false) }

    BoxWithConstraints(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val geometry = remember(maxWidth, maxHeight) {
            HeksagonDjeyometre(
                hexSize = minOf(maxWidth.value, maxHeight.value) / 8.0,
                center = HexagonPosition(0.0, 0.0)
            )
        }

        val hitboxes = remember { mutableStateListOf<HexHitbox>() }
        var hoveredHexIndex by remember { mutableStateOf<Int?>(null) }

        val modifierWithPan = Modifier.fillMaxSize().pointerInput(Unit) {
            // Simplified pan gesture handling. A full implementation would be more complex.
            // This demonstrates the concept of hit-testing against cached paths.
        }

        Layout(
            modifier = modifierWithPan,
            content = {
                // Render all hexagons and let the Layout place them
                val innerTapLabels = if (isLetterMode) KepadKonfeg.innerLetterMode else KepadKonfeg.innerNumberMode
                val outerTapLabels = if (isLetterMode) KepadKonfeg.outerTap else KepadKonfeg.outerTapNumber
                val outerLongPressLabels = if (isLetterMode) KepadKonfeg.outerLongPress else KepadKonfeg.outerLongPressNumber

                // Center Hex
                val centerBgColor = if (isLetterMode) Color.Black else Color.White
                HeksagonWedjet(
                    label = if (isLetterMode) " " else ".",
                    backgroundColor = if (isLetterMode) Color.White else Color.Black,
                    textColor = centerBgColor,
                    size = geometry.hexWidth.dp,
                    rotationAngle = geometry.rotationAngle.toFloat(),
                    onTap = { enpitSirves.addCharacter(if (isLetterMode) " " else ".") },
                    onLongPress = { enpitSirves.toggleMode() },
                    onPressedChanged = { isCenterHexPressed = it }
                )

                // Inner Ring
                innerTapLabels.forEachIndexed { index, label ->
                    HeksagonWedjet(
                        label = label,
                        backgroundColor = KepadKonfeg.innerRingColors[index],
                        textColor = KepadKonfeg.getComplementaryColor(KepadKonfeg.innerRingColors[index]),
                        size = geometry.hexWidth.dp,
                        onTap = { onHexKeyPress(label, false, null) }
                    )
                }

                // Outer Ring
                outerTapLabels.forEachIndexed { index, label ->
                    HeksagonWedjet(
                        label = label,
                        secondaryLabel = if (isLetterMode) outerLongPressLabels[index] else null,
                        backgroundColor = KepadKonfeg.rainbowColors[index],
                        textColor = KepadKonfeg.getComplementaryColor(KepadKonfeg.rainbowColors[index]),
                        size = geometry.hexWidth.dp,
                        onTap = { onHexKeyPress(label, false, null) },
                        onLongPress = { onHexKeyPress(outerLongPressLabels[index], true, label) }
                    )
                }
            }
        ) { measurables, constraints ->
            val stackWidth = constraints.maxWidth
            val stackHeight = constraints.maxHeight
            hitboxes.clear()

            layout(stackWidth, stackHeight) {
                val centerHex = measurables[0].measure(constraints)
                centerHex.placeRelative(
                    (stackWidth - centerHex.width) / 2,
                    (stackHeight - centerHex.height) / 2
                )

                val innerCoords = geometry.getInnerRingCoordinates()
                val outerCoords = geometry.getOuterRingCoordinates()

                // Place Inner Ring
                for (i in 0 until innerCoords.size) {
                    val placeable = measurables[i + 1].measure(constraints)
                    val coord = innerCoords[i]
                    val pos = geometry.axialToPixel(coord.q, coord.r)
                    val x = stackWidth / 2 + pos.x - (placeable.width / 2)
                    val y = stackHeight / 2 + pos.y - (placeable.height / 2)
                    placeable.placeRelative(x.roundToInt(), y.roundToInt())
                }

                // Place Outer Ring
                for (i in 0 until outerCoords.size) {
                    val placeable = measurables[i + 1 + innerCoords.size].measure(constraints)
                    val coord = outerCoords[i]
                    val pos = geometry.axialToPixel(coord.q, coord.r)
                    val x = stackWidth / 2 + pos.x - (placeable.width / 2)
                    val y = stackHeight / 2 + pos.y - (placeable.height / 2)
                    placeable.placeRelative(x.roundToInt(), y.roundToInt())
                }
            }
        }

        // Display Text on top
        AwtpitTekstWedjet(
            text = enpitSirves.getDisplayText(displayLength),
            style = TextStyle(
                color = if (isCenterHexPressed) (if (isLetterMode) Color.White else Color.Black) else (if (isLetterMode) Color.Black else Color.White),
                fontSize = (geometry.hexWidth * 0.33).sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}
