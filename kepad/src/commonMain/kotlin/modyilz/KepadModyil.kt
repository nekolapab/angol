package modyilz

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.text.selection.SelectionContainer
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
        
        // For WearOS (smaller screens), use a larger divisor to make hexagons smaller
        // Typical WearOS screens are ~390x390, so we need smaller hexagons
        val minDimension = minOf(maxWidthPx, maxHeightPx)
        val divisor = if (minDimension < 500) {
            12.0 // Smaller screens (WearOS) - larger divisor = smaller hexagons
        } else {
            8.5 // Larger screens (Android phones/tablets)
        }

        val geometry = remember(maxWidthPx, maxHeightPx) {
            HeksagonDjeyometre(
                hexSize = minDimension / divisor,
                center = HexagonPosition(0.0, 0.0)
            )
        }

        // Get coordinate lists from geometry
        val innerCoords = remember(geometry) { geometry.getInnerRingCoordinates() }
        val outerCoords = remember(geometry) { geometry.getOuterRingCoordinates() }

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

            // Convert hexWidthDp to pixels for constraints
            val hexWidthPx = with(density) { hexWidthDp.toPx() }
            val hexHeightPx = with(density) { geometry.hexHeight.toFloat().toDp().toPx() }
            
            // Create constraints that limit hexagon size
            val hexConstraints = Constraints(
                maxWidth = hexWidthPx.roundToInt(),
                maxHeight = hexHeightPx.roundToInt()
            )

            layout(stackWidth, stackHeight) {
                val centerHex = measurables[0].measure(hexConstraints)
                centerHex.placeRelative(
                    (stackWidth - centerHex.width) / 2,
                    (stackHeight - centerHex.height) / 2
                )

                // Place Inner Ring
                for (i in 0 until innerCoords.size) {
                    val placeable = measurables[i + 1].measure(hexConstraints)
                    val coord = innerCoords[i]
                    val pos = geometry.axialToPixel(coord.q, coord.r)
                    
                    // geometry.axialToPixel now returns Pixels, which matches Layout coordinates
                    val x = stackWidth / 2.0 + pos.x - (placeable.width / 2.0)
                    val y = stackHeight / 2.0 + pos.y - (placeable.height / 2.0)
                    placeable.placeRelative(x.roundToInt(), y.roundToInt())
                }

                // Place Outer Ring
                for (i in 0 until outerCoords.size) {
                    val placeable = measurables[i + 1 + innerCoords.size].measure(hexConstraints)
                    val coord = outerCoords[i]
                    val pos = geometry.axialToPixel(coord.q, coord.r)
                    
                    val x = stackWidth / 2.0 + pos.x - (placeable.width / 2.0)
                    val y = stackHeight / 2.0 + pos.y - (placeable.height / 2.0)
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
        
        // Debug text showing geometry sizes - always white and selectable
        Box(modifier = Modifier.align(Alignment.TopStart).padding(8.dp)) {
            SelectionContainer {
                androidx.compose.foundation.text.BasicText(
                    text = "W:${maxWidthPx.toInt()} H:${maxHeightPx.toInt()} hexSize:${geometry.hexSize.toInt()} hexWidth:${geometry.hexWidth.toInt()} divisor:$divisor",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal
                    )
                )
            }
        }

        // Display Text on top - always white and selectable
        SelectionContainer {
            androidx.compose.foundation.text.BasicText(
                text = enpitSirves.getDisplayText(inputText, displayLength),
                style = TextStyle(
                    color = Color.White,
                    fontSize = with(density) { (geometry.hexWidth * 0.33).toFloat().toSp() }, // Convert pixels to Sp
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}
