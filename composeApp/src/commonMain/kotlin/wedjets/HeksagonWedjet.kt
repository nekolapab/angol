package wedjets

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import modalz.KepadKonfeg
import kotlin.math.PI
import kotlin.math.sqrt

@Composable
fun HeksagonWedjet(
    modifier: Modifier = Modifier,
    label: String,
    secondaryLabel: String? = null,
    backgroundColor: Color,
    textColor: Color,
    size: Dp,
    isPressed: Boolean = false,
    isHovering: Boolean = false,
    onTap: (() -> Unit)? = null,
    onLongPress: (() -> Unit)? = null,
    onHover: ((Boolean) -> Unit)? = null,
    onPressedChanged: ((Boolean) -> Unit)? = null,
    rotationAngle: Float = 0f,
    fontSize: Float? = null,
    verticalOffset: Dp = 0.dp,
    child: @Composable (() -> Unit)? = null
) {
    val density = LocalDensity.current
    var isMomentarilyPressed by remember { mutableStateOf(false) }
    
    androidx.compose.runtime.DisposableEffect(Unit) {
        onDispose {
            isMomentarilyPressed = false
            onPressedChanged?.invoke(false)
        }
    }

    val hexHeight = size * (2f / sqrt(3f))
    val contrastColor = if (isMomentarilyPressed || isPressed) backgroundColor else textColor
    
    // DELETE font scaling: divide by fontScale to ignore system settings
    val rawFontSize = fontSize ?: (size.value / 4f)
    val finalFontSize = (rawFontSize / density.fontScale).sp

    HeksagonTutcboks(
        rotationAngle = rotationAngle,
        modifier = modifier.size(width = size, height = hexHeight)
    ) {
        val inputModifier = if (onTap != null || onLongPress != null || onPressedChanged != null) {
            Modifier.pointerInput(onTap, onLongPress, onPressedChanged) {
                detectTapGestures(
                    onPress = {
                        isMomentarilyPressed = true
                        onPressedChanged?.invoke(true)
                        onTap?.invoke()
                        try {
                            awaitRelease()
                        } finally {
                            isMomentarilyPressed = false
                            onPressedChanged?.invoke(false)
                        }
                    },
                    onTap = { },
                    onLongPress = { onLongPress?.invoke() }
                )
            }
        } else {
            Modifier
        }

        Box(
            modifier = Modifier.fillMaxSize().then(inputModifier),
            contentAlignment = Alignment.Center
        ) {
            Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .drawWithCache {
                        val path = createVisualPath(this.size)
                        onDrawBehind {
                            val displayColor = if (isMomentarilyPressed || isPressed) textColor else backgroundColor
                            val fillPaint = Paint().apply {
                                color = displayColor
                                style = PaintingStyle.Fill
                            }
                            drawContext.canvas.drawPath(path, fillPaint)
                        }
                    }
            )

            Box(
                modifier = Modifier.rotate(-rotationAngle * (180f / PI.toFloat())).offset(y = verticalOffset),
                contentAlignment = Alignment.Center
            ) {
                if (child != null) {
                    child()
                } else if (secondaryLabel != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = label, color = contrastColor, fontSize = finalFontSize, fontWeight = FontWeight.Bold, softWrap = false)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = secondaryLabel, color = contrastColor, fontSize = finalFontSize, fontWeight = FontWeight.Bold, softWrap = false)
                    }
                } else {
                    Text(text = label, color = contrastColor, fontSize = finalFontSize, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, softWrap = false)
                }
            }
        }
    }
}

private fun createVisualPath(size: Size): Path {
    val path = Path()
    val centerX = size.width / 2f
    val centerY = size.height / 2f
    val side = size.height / 2f
    path.moveTo(centerX, centerY - side)
    path.lineTo(centerX + side * sqrt(3f) / 2f, centerY - side / 2f)
    path.lineTo(centerX + side * sqrt(3f) / 2f, centerY + side / 2f)
    path.lineTo(centerX, centerY + side)
    path.lineTo(centerX - side * sqrt(3f) / 2f, centerY + side / 2f)
    path.lineTo(centerX - side * sqrt(3f) / 2f, centerY - side / 2f)
    path.close()
    return path
}
