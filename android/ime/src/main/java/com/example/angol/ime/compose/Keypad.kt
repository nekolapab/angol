package com.example.angol.ime.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun Keypad(
    onKeyPress: (String) -> Unit,
    onDelete: () -> Unit,
    onSpace: () -> Unit,
    onSwitchMode: () -> Unit
) {
    var isLetterMode by remember { mutableStateOf(true) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        val density = LocalDensity.current
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { maxHeight.toPx() }
        
        // Hexagon size calculation
        // The grid is roughly 5 hexes wide (center + 2 rings).
        // 10 is a safe divisor to fit everything.
        val minDim = minOf(widthPx, heightPx)
        val hexSizePx = minDim / 10f 
        
        val centerOffset = Offset(widthPx / 2, heightPx / 2)
        
        val geometry = remember(hexSizePx, centerOffset) {
            HexagonGeometry(hexSizePx, centerOffset)
        }
        
        val hexWidthDp = with(density) { geometry.hexWidth.toDp() }
        val hexHeightDp = with(density) { geometry.hexHeight.toDp() }

        // Helper for placing keys
        @Composable
        fun PositionedHexKey(
            q: Int, 
            r: Int, 
            label: String, 
            secondaryLabel: String? = null,
            color: Color,
            textColor: Color,
            onTap: () -> Unit,
            onLongPress: (() -> Unit)? = null
        ) {
            val pos = geometry.axialToPixel(q, r)
            // Offset logic: absolute position of top-left corner of the hex box
            val xDp = with(density) { (pos.x - geometry.hexWidth / 2).toDp() }
            val yDp = with(density) { (pos.y - geometry.hexHeight / 2).toDp() }
            
            HexagonKey(
                modifier = Modifier
                    .offset(x = xDp, y = yDp)
                    .size(width = hexWidthDp, height = hexHeightDp),
                label = label,
                secondaryLabel = secondaryLabel,
                color = color,
                textColor = textColor,
                onTap = onTap,
                onLongPress = onLongPress
            )
        }

        // --- Center Key ---
        val centerBg = if (isLetterMode) Color.White else Color.Black
        val centerFg = if (isLetterMode) Color.Black else Color.White
        
        PositionedHexKey(
            q = 0, r = 0,
            label = if (isLetterMode) " " else ".",
            color = centerBg,
            textColor = centerFg,
            onTap = { 
                if (isLetterMode) onSpace() else onKeyPress(".")
            },
            onLongPress = {
                isLetterMode = !isLetterMode
                onSwitchMode()
            }
        )

        // --- Inner Ring ---
        val innerLabels = if (isLetterMode) KeypadConfig.innerLetterMode else KeypadConfig.innerNumberMode
        val innerLongLabels = if (isLetterMode) emptyList<String>() else KeypadConfig.innerLongPressNumber
        
        geometry.getInnerRingCoordinates().forEachIndexed { index, coord ->
            val label = innerLabels.getOrElse(index) { "" }
            val longLabel = if (!isLetterMode) innerLongLabels.getOrElse(index) { "" } else null
            val color = KeypadConfig.innerRingColors[index % KeypadConfig.innerRingColors.size]
            
            PositionedHexKey(
                q = coord.q, r = coord.r,
                label = label,
                secondaryLabel = longLabel,
                color = color,
                textColor = KeypadConfig.getComplementaryColor(color),
                onTap = {
                    if (label == "⌫") onDelete() else onKeyPress(label)
                },
                onLongPress = {
                     if (longLabel != null && longLabel.isNotEmpty()) {
                         if (longLabel == "⌫") onDelete() else onKeyPress(longLabel)
                     }
                }
            )
        }

        // --- Outer Ring ---
        val outerLabels = if (isLetterMode) KeypadConfig.outerTap else KeypadConfig.outerTapNumber
        val outerLongLabels = if (isLetterMode) KeypadConfig.outerLongPress else KeypadConfig.outerLongPressNumber

        geometry.getOuterRingCoordinates().forEachIndexed { index, coord ->
            val label = outerLabels.getOrElse(index) { "" }
            val longLabel = outerLongLabels.getOrElse(index) { "" }
            val color = KeypadConfig.rainbowColors[index % KeypadConfig.rainbowColors.size]

            PositionedHexKey(
                q = coord.q, r = coord.r,
                label = label,
                secondaryLabel = longLabel,
                color = color,
                textColor = KeypadConfig.getComplementaryColor(color),
                onTap = { onKeyPress(label) },
                onLongPress = { if (longLabel.isNotEmpty()) onKeyPress(longLabel) }
            )
        }
    }
}
