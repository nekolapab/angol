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
import kotlin.math.sqrt

@Composable
fun HeksagonWedjet(
    modifier: Modifier = Modifier,
    label: String,
    secondaryLabel: String? = null,
    backgroundColor: Color,
    textColor: Color,
    size: Dp,
    isPressed: Boolean = false, // Represents the "active" state from the model
    isHovering: Boolean = false, // Represents the "hover" state from the model
    onTap: (() -> Unit)? = null,
    onLongPress: (() -> Unit)? = null,
    onHover: ((Boolean) -> Unit)? = null,
    onPressedChanged: ((Boolean) -> Unit)? = null,
    rotationAngle: Float = 0f,
    fontSize: Float? = null,
    verticalOffset: Dp = 0.dp,
    child: @Composable (() -> Unit)? = null
) {
    val scope = rememberCoroutineScope()
    var isMomentarilyPressed by remember { mutableStateOf(false) }
    // var isPointerOver by remember { mutableStateOf(false) } // Hover logic removed for KMP compatibility

    val hexHeight = size * (2f / sqrt(3f))
    val density = LocalDensity.current

    val contrastColor = if (isMomentarilyPressed) {
        backgroundColor // If pressed, background is inverted, so text is original bg color
    } else {
        textColor // Use the provided text color
    }
    
    // Dynamic font scaling removed to keep sizes uniform. Px->Sp conversion fixed clipping.
    val baseFontSize = fontSize ?: (size.value / 4f)
    val scaledFontSize = baseFontSize

    HeksagonTutcboks(
        rotationAngle = rotationAngle,
        modifier = modifier.size(width = size, height = hexHeight)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(onTap, onLongPress) {
                    detectTapGestures(
                        onPress = {
                            isMomentarilyPressed = true
                            onPressedChanged?.invoke(true)
                            onTap?.invoke() // Fire action immediately on press down
                            try {
                                awaitRelease()
                            } finally {
                                // Coroutine ensures this runs even if gesture is cancelled.
                                isMomentarilyPressed = false
                                onPressedChanged?.invoke(false)
                            }
                        },
                        onTap = { /* Action handled in onPress */ },
                        onLongPress = { onLongPress?.invoke() }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            // Drawing Canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasSize = this.size
                drawIntoCanvas { canvas ->
                    val displayColor = if (isMomentarilyPressed) {
                        textColor // If pressed, swap background to text color
                    } else {
                        backgroundColor
                    }

                    val hexagonPath = createVisualPath(canvasSize)

                    // Glow effect REMOVED for KMP compatibility (depended on Android-specific BlurMaskFilter)
                    /*
                    if (isPointerOver || isHovering) {
                         // ...
                    }
                    */

                    // Fill
                    val fillPaint = Paint().apply {
                        color = displayColor
                        style = PaintingStyle.Fill
                    }
                    canvas.drawPath(hexagonPath, fillPaint)
                }
            }

            // Label(s) or custom child
            Box(
                modifier = Modifier
                    .rotate(-rotationAngle * (180f / Math.PI.toFloat()))
                    .offset(y = verticalOffset),
                contentAlignment = Alignment.Center
            ) {
                if (child != null) {
                    child()
                } else if (secondaryLabel != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = label,
                            color = contrastColor,
                            fontSize = with(density) { scaledFontSize.toSp() },
                            fontWeight = FontWeight.Bold,
                            softWrap = false
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = secondaryLabel,
                            color = contrastColor,
                            fontSize = with(density) { scaledFontSize.toSp() },
                            fontWeight = FontWeight.Bold,
                            softWrap = false
                        )
                    }
                } else {
                    Text(
                        text = label,
                        color = contrastColor,
                        fontSize = with(density) { scaledFontSize.toSp() },
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        softWrap = false
                    )
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

    // Vertices for a pointy-top hexagon
    path.moveTo(centerX, centerY - side)
    path.lineTo(centerX + side * sqrt(3f) / 2f, centerY - side / 2f)
    path.lineTo(centerX + side * sqrt(3f) / 2f, centerY + side / 2f)
    path.lineTo(centerX, centerY + side)
    path.lineTo(centerX - side * sqrt(3f) / 2f, centerY + side / 2f)
    path.lineTo(centerX - side * sqrt(3f) / 2f, centerY - side / 2f)
    path.close()
    return path
}
