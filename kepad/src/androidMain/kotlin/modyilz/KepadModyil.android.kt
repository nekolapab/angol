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
            10.0 // Smaller screens (WearOS) - restore to previous perfect size
        } else {
            8.0 // Larger screens (Phone)
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
            val innerLongPressLabels = if (isLetterMode) {
                KepadKonfeg.innerLetterMode.map { if (it == "⌫") "⌫" else "" }
            } else {
                KepadKonfeg.innerLongPressNumber
            }

            innerLabels.forEachIndexed { index, label ->
                val longPressLabel = innerLongPressLabels[index]
                HeksagonWedjet(
                    label = label,
                    backgroundColor = KepadKonfeg.innerRingColors[index],
                    textColor = KepadKonfeg.getComplementaryColor(KepadKonfeg.innerRingColors[index]),
                    size = hexWidthDp,
                    fontSize = (geometry.hexWidth * if (isLetterMode) 0.6 else 0.8).toFloat(), // Increased size
                    verticalOffset = 0.dp, // Reset to centered
                    onTap = {
                        enpitSirves.addCharacter(label)
                        onHexKeyPress(label, false, null)
                    },
                    onLongPress = if (longPressLabel.isNotEmpty()) {
                        {
                            // If backspace, isLongPress=true triggers word delete.
                            // If other char, usually we type the long press label.
                            if (label == "⌫") {
                                onHexKeyPress(label, true, null)
                            } else {
                                enpitSirves.addCharacter(longPressLabel)
                                onHexKeyPress(longPressLabel, false, null)
                            }
                        }
                    } else null
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
        val centerLabel = if (isLetterMode) " " else "."
        // Set secondary label to null in Number Mode to center the dot
        val centerSecondaryLabel = if (isLetterMode) "." else null
        
        // Colors reversed to match Flutter (Letter: White, Number: Black)
        val centerHexColor = if (isLetterMode) Color.White else Color.Black
        val centerTextColor = if (isLetterMode) Color.Black else Color.White

        HeksagonWedjet(
            label = centerLabel,
            secondaryLabel = centerSecondaryLabel,
            backgroundColor = centerHexColor,
            textColor = centerTextColor,
            size = hexWidthDp,
            fontSize = (geometry.hexWidth * if (isLetterMode) 0.6 else 0.8).toFloat(),
            onTap = {
                val charToType = if (isLetterMode) " " else "."
                enpitSirves.addCharacter(charToType)
                onHexKeyPress(charToType, false, null)
            },
            onLongPress = {
                val wasLetterMode = isLetterMode
                enpitSirves.toggleMode()
                
                // Logic: Delete the char typed by onTap (' ' or '.'), then type the other one
                val charToType = if (wasLetterMode) "." else " "
                val charToDelete = if (wasLetterMode) " " else "."
                
                enpitSirves.deleteLeft() // Update internal state
                enpitSirves.addCharacter(charToType)
                
                onHexKeyPress(charToType, true, charToDelete)
            },
            onPressedChanged = { pressed -> isCenterHexPressed = pressed }
        )


    }
}