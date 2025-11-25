package com.example.angol.ime.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.input.pointer.pointerInput
import androidx.wear.compose.material.Text
import kotlin.math.sqrt

@Composable
fun HexagonKey(
    modifier: Modifier = Modifier,
    label: String,
    secondaryLabel: String? = null,
    color: Color,
    textColor: Color, // This might be redundant if we calculate contrast, but keeping for API consistency
    onTap: () -> Unit,
    onLongPress: (() -> Unit)? = null
) {
    var isPressed by remember { mutableStateOf(false) }

    // Logic to switch colors on press
    // Normal: BG = color, Text = textColor
    // Pressed: BG = textColor, Text = color
    val displayBackgroundColor = if (isPressed) textColor else color
    val displayTextColor = if (isPressed) color else textColor

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { onTap() },
                    onLongPress = { onLongPress?.invoke() }
                )
            }
        ) {
            val path = createHexagonPath(size)
            drawPath(
                path = path,
                color = displayBackgroundColor,
                style = Fill
            )
        }

        // Label(s)
        Text(
            text = if (secondaryLabel != null) "$label $secondaryLabel" else label,
            color = displayTextColor
        )
    }
}

fun createHexagonPath(size: Size): Path {
    val path = Path()
    val centerX = size.width / 2
    val centerY = size.height / 2
    
    // Calculate side length based on height (pointy top)
    // Height = 2 * side
    val side = size.height / 2

    // Pointy-top hexagon vertices
    val sqrt3 = sqrt(3.0f)
    
    path.moveTo(centerX, centerY - side)
    path.lineTo(centerX + side * sqrt3 / 2, centerY - side / 2)
    path.lineTo(centerX + side * sqrt3 / 2, centerY + side / 2)
    path.lineTo(centerX, centerY + side)
    path.lineTo(centerX - side * sqrt3 / 2, centerY + side / 2)
    path.lineTo(centerX - side * sqrt3 / 2, centerY - side / 2)
    path.close()
    
    return path
}
