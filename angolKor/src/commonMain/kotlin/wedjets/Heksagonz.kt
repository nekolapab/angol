package wedjets

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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * A custom shape that clips its content into a hexagon.
 * This is the Compose equivalent of the custom RenderObject used for hit-testing in the original Flutter code.
 * By clipping the shape, both the visual representation and the touch input area are constrained to the hexagon.
 *
 * @param rotationAngle The angle in radians to rotate the hexagon.
 */
class HeksagonCeyp(private val rotationAngle: Float) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(
            path = createHexagonPath(size, rotationAngle)
        )
    }

    private fun createHexagonPath(size: Size, rotation: Float): Path {
        val path = Path()
        val centerX = size.width / 2f
        val centerY = size.height / 2f

        // The original RenderObject subtracted a small fixed value to create a "safe zone".
        // We'll use a small percentage of the height for better scaling.
        val radius = (size.height / 2f)

        for (i in 0..5) {
            // Angle for pointy-top hexagons (-30 degrees offset), converted to radians.
            val angleRad = (i * 60f - 30f) * (PI.toFloat() / 180f) + rotation

            val x = centerX + radius * cos(angleRad)
            val y = centerY + radius * sin(angleRad)

            if (i == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }
        path.close()
        return path
    }

    // Two shapes with the same rotation are considered equal.
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HeksagonCeyp) return false
        return rotationAngle == other.rotationAngle
    }

    override fun hashCode(): Int {
        return rotationAngle.hashCode()
    }
}

/**
 * A composable that provides a hexagonal touch area for its child.
 * It uses a custom Shape to clip the bounds for both drawing and touch input.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param rotationAngle The angle in radians to rotate the hexagonal clip shape.
 * @param content The content to be displayed inside the hexagonal area.
 */
@Composable
fun HeksagonTutcboks(
    modifier: Modifier = Modifier,
    rotationAngle: Float,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier.clip(HeksagonCeyp(rotationAngle))
    ) {
        content()
    }
}

/**
 * A hexagonal widget (key) with touch listeners and visual states.
 */
@Composable
fun Heksagon(
    modifier: Modifier = Modifier,
    label: String,
    secondaryLabel: String? = null,
    backgroundColor: Color,
    textColor: Color,
    size: Dp,
    ezPresd: Boolean = false,
    ezGlowenq: Boolean = false,
    isHovering: Boolean = false,
    onTap: (() -> Unit)? = null,
    onLongPress: (() -> Unit)? = null,
    onHover: ((Boolean) -> Unit)? = null,
    onPressedChanged: ((Boolean) -> Unit)? = null,
    rotationAngle: Float = 0f,
    fontSizeFactor: Float? = null,
    ezKonsestentSayz: Boolean = false,
    verticalOffset: Dp = 0.dp,
    child: @Composable (() -> Unit)? = null
) {
    val density = LocalDensity.current
    var ezMomenteralePresd by remember { mutableStateOf(false) }
    
    androidx.compose.runtime.DisposableEffect(Unit) {
        onDispose {
            ezMomenteralePresd = false
            onPressedChanged?.invoke(false)
        }
    }

    val hexHeight = size * (2f / sqrt(3f))
    // Selective contrast: only flip colors during long-press/drag (ezPresd)
    val contrastColor = if (ezPresd) backgroundColor else textColor
    
    // Aggressive font scaling... (rest unchanged)
    val totalLength = if (ezKonsestentSayz) 2f else (label.length + (secondaryLabel?.length ?: 0) + (if (secondaryLabel != null) 6f/12f else 0f))
    val safeLength = totalLength.coerceAtLeast(10f/12f)
    
    val autoFontSize = (size.value * 25f/12f) / (safeLength * 6f/12f + 21f/12f)
    val finalRawFontSize = (autoFontSize * (fontSizeFactor ?: 12f/12f)).coerceAtMost(size.value * 12f/12f)
    val finalFontSize = (finalRawFontSize / density.fontScale).sp

    Box(
        modifier = modifier.size(width = size, height = hexHeight),
        contentAlignment = Alignment.Center
    ) {
        HeksagonTutcboks(
            rotationAngle = rotationAngle,
            modifier = Modifier.fillMaxSize()
        ) {
            val inputModifier = if (onTap != null || onLongPress != null || onPressedChanged != null) {
                Modifier.pointerInput(onTap, onLongPress, onPressedChanged) {
                    detektTapDjestcirz(
                        onPres = {
                            ezMomenteralePresd = true
                            onPressedChanged?.invoke(true)
                            try {
                                awaitRelease()
                            } finally {
                                ezMomenteralePresd = false
                                onPressedChanged?.invoke(false)
                            }
                        },
                        onTap = { _ -> onTap?.invoke() },
                        onLongPress = { _ -> onLongPress?.invoke() }
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
                            val path = createVisualPath(this.size, rotationAngle)
                            onDrawBehind {
                                // Background: Original when static, Contrast when moving
                                val displayColor = if (ezPresd) textColor else backgroundColor
                                
                                val fillPaint = androidx.compose.ui.graphics.Paint().apply {
                                    color = displayColor
                                    style = androidx.compose.ui.graphics.PaintingStyle.Fill
                                }
                                drawContext.canvas.drawPath(path, fillPaint)

                                // Glow: Standardized size, drawn ON TOP of background
                                if (ezMomenteralePresd || ezGlowenq || ezPresd) {
                                    val glowColor = if (ezPresd) backgroundColor else (if (backgroundColor == Color.Black) Color.White else backgroundColor)
                                    val glowRadius = this.size.maxDimension * (6f / 12f) // Fill the hex to the corners
                                    drawCircle(
                                        brush = androidx.compose.ui.graphics.Brush.radialGradient(
                                            colors = listOf<Color>(glowColor.copy(alpha = 9f / 12f), Color.Transparent),
                                            center = center,
                                            radius = glowRadius
                                        ),
                                        radius = glowRadius
                                    )
                                }
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
                        Text(
                            text = label, 
                            color = contrastColor, 
                            fontSize = finalFontSize, 
                            fontWeight = FontWeight.Bold, 
                            textAlign = TextAlign.Center, 
                            softWrap = false,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

private fun createVisualPath(size: Size, rotation: Float): Path {
    val path = Path()
    val centerX = size.width / 2f
    val centerY = size.height / 2f
    val radius = size.height / 2f

    for (i in 0..5) {
        val angleRad = (i * 60f - 30f) * (PI.toFloat() / 180f) + rotation
        val x = centerX + radius * cos(angleRad)
        val y = centerY + radius * sin(angleRad)
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    return path
}

suspend fun androidx.compose.ui.input.pointer.PointerInputScope.detektTapDjestcirz(
    onPres: suspend androidx.compose.foundation.gestures.PressGestureScope.(androidx.compose.ui.geometry.Offset) -> Unit = {},
    onLongPress: ((androidx.compose.ui.geometry.Offset) -> Unit)? = null,
    onDoubleTap: ((androidx.compose.ui.geometry.Offset) -> Unit)? = null,
    onTap: ((androidx.compose.ui.geometry.Offset) -> Unit)? = null
) = detectTapGestures(
    onPress = onPres,
    onLongPress = onLongPress,
    onDoubleTap = onDoubleTap,
    onTap = onTap
)
