package modyilz

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
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
        val density = LocalDensity.current
        
        // Calculate constraints in Pixels for Geometry
        val maxWidthPx = with(density) { maxWidth.toPx() }
        val maxHeightPx = with(density) { maxHeight.toPx() }

        val geometry = remember(maxWidthPx, maxHeightPx) {
            HeksagonDjeyometre(
                hexSize = minOf(maxWidthPx, maxHeightPx) / 8.0,
                center = HexagonPosition(0.0, 0.0)
            )
        }

        // Convert geometry size (Pixels) back to Dp for Widgets
        val hexWidthDp = with(density) { geometry.hexWidth.toFloat().toDp() }

        val hitboxes = remember { mutableStateListOf<HexHitbox>() }

        val modifierWithPan = Modifier.fillMaxSize().pointerInput(Unit) {
            // Simplified pan gesture handling.
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
                    size = hexWidthDp,
                    rotationAngle = geometry.rotationAngle.toFloat(),
                    onTap = {
                        val char = if (isLetterMode) " " else "."
                        enpitSirves.addCharacter(char)
                        onHexKeyPress(char, false, null)
                    },
                    onLongPress = { enpitSirves.toggleMode() },
                    onPressedChanged = { isCenterHexPressed = it }
                )

                // Inner Ring
                innerTapLabels.forEachIndexed { index, label ->
                    HeksagonWedjet(
                        label = label,
                        backgroundColor = KepadKonfeg.innerRingColors[index],
                        textColor = KepadKonfeg.getComplementaryColor(KepadKonfeg.innerRingColors[index]),
                        size = hexWidthDp,
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
                        size = hexWidthDp,
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

                // Place Inner Ring
                for (i in 0 until innerCoords.size) {
                    val placeable = measurables[i + 1].measure(constraints)
                    val coord = innerCoords[i]
                    val pos = geometry.axialToPixel(coord.q, coord.r)
                    
                    // geometry.axialToPixel now returns Pixels, which matches Layout coordinates
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

        // --- Start of debugging Canvas ---
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerPxX = size.width / 2
            val centerPxY = size.height / 2
            // Draw center point for reference (Red)
            drawCircle(Color.Red, radius = 5.dp.toPx(), center = Offset(centerPxX, centerPxY))
            // Inner Ring Coordinates (Green)
            innerCoords.forEach { coord ->
                val pos = geometry.axialToPixel(coord.q, coord.r)
                val pixelX = centerPxX + pos.x
                val pixelY = centerPxY + pos.y
                drawCircle(Color.Green, radius = 5.dp.toPx(), center = Offset(pixelX.toFloat(), pixelY.toFloat()))
            }
            // Outer Ring Coordinates (Blue)
            outerCoords.forEach { coord ->
                val pos = geometry.axialToPixel(coord.q, coord.r)
                val pixelX = centerPxX + pos.x
                val pixelY = centerPxY + pos.y
                drawCircle(Color.Blue, radius = 5.dp.toPx(), center = Offset(pixelX.toFloat(), pixelY.toFloat()))
            }
        }
        // --- End of debugging Canvas ---

        // Display Text on top
        AwtpitTekstWedjet(
            text = enpitSirves.getDisplayText(inputText, displayLength),
            style = TextStyle(
                color = if (isCenterHexPressed) (if (isLetterMode) Color.White else Color.Black) else (if (isLetterMode) Color.Black else Color.White),
                fontSize = with(density) { (geometry.hexWidth * 0.33).toFloat().toSp() }, // Convert pixels to Sp
                fontWeight = FontWeight.Bold
            )
        )
    }
}
