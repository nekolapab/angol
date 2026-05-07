package wedjets

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import yuteledez.HeksagonDjeyometre

@Composable
fun AngolTogilWedjet(
    geometry: HeksagonDjeyometre,
    gridHeightDp: androidx.compose.ui.unit.Dp,
    currentAngolMode: Int,
    isListening: Boolean,
    isLetterMode: Boolean,
    onTogilAngol: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(gridHeightDp)
            .offset(y = (geometry.heksSayz * -8/12).dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .height(32.dp)
                .width((geometry.heksWidlx * 2.1).dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { onTogilAngol(false) },
                        onLongPress = { onTogilAngol(true) }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            val centerBg = if (isListening) Color.Red else if (isLetterMode) Color.White else Color.Black
            
            val alpha = when (currentAngolMode) {
                2 -> 1.0f        // 12/12 (Angol 1)
                1 -> 4f / 12f    // 4/12 (Angol 2)
                else -> 1f / 12f // 1/12 (Of)
            }
            
            val textColor = when (centerBg) {
                Color.Red -> Color.Cyan   // Contrast on Red hex
                Color.White -> Color.Black // Contrast on White hex
                else -> if (currentAngolMode == 2) Color.Yellow else Color.White
            }

            androidx.compose.material.Text(
                text = "angol",
                color = textColor.copy(alpha = alpha),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                style = TextStyle(
                    shadow = Shadow(
                        color = if (centerBg == Color.White) Color.White.copy(alpha = 0.5f) else Color.Black,
                        offset = Offset(1f, 1f),
                        blurRadius = 2f
                    )
                )
            )
        }
    }
}
