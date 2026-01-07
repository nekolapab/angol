package modyilz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.selection.SelectionContainer
import sirvesez.EnpitSirves
import yuteledez.HeksagonDjeyometre
import wedjets.HeksagonWedjet
import wedjets.EnirRenqWedjet
import wedjets.AwdirRenqWedjet
import modalz.KepadKonfeg
import modalz.HexagonPosition
import kotlin.math.min

/**
 * Android implementation of the Angol Hexagonal Keypad.
 * Used for Android keyboard extensions (IME).
 */
@Composable
actual fun KepadModyil(
    onHexKeyPress: (String, Boolean, String?) -> Unit,
    isKeypadVisible: Boolean,
    displayLength: Int
) {
    if (!isKeypadVisible) return

    val enpitSirves = remember { EnpitSirves() }
    val isLetterMode by enpitSirves.isLetterMode.collectAsState()
    val inputText by enpitSirves.inputText.collectAsState()
    var isCenterHexPressed by remember { mutableStateOf(false) }

    // Constrain the height for IME usage.
    // fillMaxWidth() ensures it takes the full width of the screen.
    // height(350.dp) sets a fixed height typical for keyboards.
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp), // Set a fixed height for the keyboard
        contentAlignment = Alignment.Center
    ) {
        val maxWidthPx = constraints.maxWidth.toFloat()
        val maxHeightPx = constraints.maxHeight.toFloat()

        // Typical screens: smaller divisor = larger hexagons
        val minDimension = minOf(maxWidthPx, maxHeightPx)
        val divisor = if (minDimension < 500) {
            10.0 // Smaller screens - larger hexagons
        } else {
            8.5 // Larger screens
        }

        val hexSize = minDimension / divisor
        val geometry = HeksagonDjeyometre(
            hexSize = hexSize.toDouble(),
            center = HexagonPosition(x = 0.0, y = 0.0),
            isLetterMode = isLetterMode
        )
        val hexWidthDp = with(LocalDensity.current) { geometry.hexWidth.toFloat().toDp() }

        // Background gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF1A1A2E),
                            Color(0xFF0F0F1E),
                            Color.Black
                        )
                    )
                )
        )

        // Inner ring of hexagons
        EnirRenqWedjet(
            geometry = geometry,
            stackWidth = maxWidth,
            stackHeight = maxHeight
        ) {
            val innerLabels = if (isLetterMode) KepadKonfeg.innerLetterMode else KepadKonfeg.innerNumberMode
            innerLabels.forEachIndexed { index, label ->
                HeksagonWedjet(
                    label = label,
                    backgroundColor = KepadKonfeg.innerRingColors[index],
                    textColor = KepadKonfeg.getComplementaryColor(KepadKonfeg.innerRingColors[index]),
                    size = hexWidthDp,
                    onTap = {
                        enpitSirves.addCharacter(label)
                        onHexKeyPress(label, false, null)
                    }
                )
            }
        }

        // Outer ring of hexagons
        AwdirRenqWedjet(
            geometry = geometry,
            onHexKeyPress = onHexKeyPress,
            tapLabels = if (isLetterMode) KepadKonfeg.outerTap else KepadKonfeg.outerTapNumber,
            longPressLabels = if (isLetterMode) KepadKonfeg.outerLongPress else KepadKonfeg.outerLongPressNumber,
            enpitSirves = enpitSirves,
            stackWidth = maxWidth,
            stackHeight = maxHeight
        )

        // Center Hexagon (Toggle Mode / Space)
        val centerLabel = if (isLetterMode) "123" else "ABC"
        val centerSecondaryLabel = if (isLetterMode) "Space" else "Space"
        val centerHexColor = if (isCenterHexPressed) Color.DarkGray else Color.Gray

        HeksagonWedjet(
            label = centerLabel,
            secondaryLabel = centerSecondaryLabel,
            backgroundColor = centerHexColor,
            textColor = Color.White,
            size = hexWidthDp,
            onTap = {
                if (isLetterMode) {
                    enpitSirves.toggleMode()
                } else {
                    enpitSirves.addCharacter(" ")
                    onHexKeyPress(" ", false, null)
                }
            },
            onLongPress = {
                if (isLetterMode) {
                    enpitSirves.addCharacter(" ")
                    onHexKeyPress(" ", false, null)
                } else {
                    enpitSirves.toggleMode()
                }
            },
            onPressedChanged = { pressed -> isCenterHexPressed = pressed }
        )

        // Debug text overlay (top-left)
        Box(modifier = Modifier.align(Alignment.TopStart).padding(8.dp)) {
            SelectionContainer {
                BasicText(
                    text = "v2 UPDATED | W:${maxWidthPx.toInt()} H:${maxHeightPx.toInt()} hexSize:${geometry.hexSize.toInt()}",
                    style = TextStyle(
                        color = Color.Green,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}