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
                        onTap = { onTogilAngol(false) }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            val centerBg = if (isListening) Color.Red else if (isLetterMode) Color.White else Color.Black
            
            // Mode 0: Off (1/12 alpha)
            // Mode 1: Grey (Angol 1 enecol, Angol 2 faynol) -> 12/12 alpha (user wants it "grey" but visible)
            // Mode 2: Black (Angol 1 enecol, Angol 1 faynol) -> 12/12 alpha
            val alpha = when (currentAngolMode) {
                0 -> 1f / 12f
                else -> 1.0f
            }
            
            val textColor = when (centerBg) {
                Color.Red -> Color.Cyan
                Color.White -> Color.Black
                else -> {
                    // Mode 2 is "Black" mode, use darker color or Yellow for visibility
                    if (currentAngolMode == 2) Color.Yellow else Color.White
                }
            }

            androidx.compose.material.Text(
                text = "angol",
                color = if (currentAngolMode == 1 && centerBg == Color.Black) Color.LightGray else textColor.copy(alpha = alpha),
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
