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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.clipPath
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * A custom shape that clips its content into a hexagon.
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
        val radius = (size.height / 2f)

        for (i in 0..5) {
            val angleRad = (i * 60f - 30f) * (PI.toFloat() / 180f) + rotation
            val x = centerX + radius * cos(angleRad)
            val y = centerY + radius * sin(angleRad)
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        return path
    }
}

@Composable
fun HeksagonTutcboks(
    modifier: Modifier = Modifier,
    rotationAngle: Float,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
    ) {
        content()
    }
}

/**
 * A hexagonal widget with unified glow and contrast logic.
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
    onTap: (() -> Unit)? = null,
    onLongPress: (() -> Unit)? = null,
    onPressedChanged: ((Boolean) -> Unit)? = null,
    rotationAngle: Float = 0f,
    fontSizeFactor: Float? = null,
    ezKonsestentSayz: Boolean = false,
    verticalOffset: Dp = 0.dp,
    child: @Composable (() -> Unit)? = null
) {
    val density = LocalDensity.current
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current
    var ezMomenteralePresd by remember { mutableStateOf(false) }
    
    androidx.compose.runtime.DisposableEffect(Unit) {
        onDispose {
            ezMomenteralePresd = false
            onPressedChanged?.invoke(false)
        }
    }

    val hexHeight = size * (2f / sqrt(3f))
    val isActivelyGlowing = ezMomenteralePresd || ezGlowenq || ezPresd
    
    // Unified Contrast: Flip colors during ezPresd (Move-drag traveling or Ghost drag traveling)
    val finalBgColor = if (ezPresd) textColor else backgroundColor
    val finalTextColor = if (ezPresd) backgroundColor else textColor

    // Aggressive font scaling
    val totalLength = if (ezKonsestentSayz) 2f else (label.length + (secondaryLabel?.length ?: 0) + (if (secondaryLabel != null) 0.5f else 0f))
    val safeLength = totalLength.coerceAtLeast(0.8f)
    val autoFontSize = (size.value * 2f) / (safeLength * 0.5f + 1.75f)
    val finalRawFontSize = (autoFontSize * (fontSizeFactor ?: 1f)).coerceAtMost(size.value * 1f)
    val finalFontSize = (finalRawFontSize / density.fontScale).sp

    // Glow config: 12/12 radius, 12/12 intensity (alpha 1.0)
    val glowRadiusFactor = 12f / 12f
    val glowAlpha = 1.0f

    Box(
        modifier = modifier
            .size(width = size, height = hexHeight),
        contentAlignment = Alignment.Center
    ) {
        HeksagonTutcboks(
            rotationAngle = rotationAngle,
            modifier = Modifier.fillMaxSize()
        ) {
            val inputModifier = if (onTap != null || onLongPress != null || onPressedChanged != null) {
                Modifier.pointerInput(onTap, onLongPress, onPressedChanged) {
                    detectTapGestures(
                        onPress = {
                            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
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
                modifier = Modifier
                    .fillMaxSize()
                    .drawBehind {
                        val path = createVisualPath(this.size, rotationAngle)
                        // 1. Base Fill: Only draw if not transparent
                        if (finalBgColor.alpha > 0.01f) {
                            drawPath(path, finalBgColor)
                            // Fill + Stroke of same color removes the anti-aliasing border artifact
                            drawPath(
                                path = path,
                                color = finalBgColor,
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f)
                            )
                        }
                       
                        // 2. Beautiful Glow: High intensity, Massive diameter (200% / 24/12)
                        if (isActivelyGlowing) {
                            val activeGlowColor = finalBgColor
                            
                            // Attribute A: Core Intensity (Inner 50%) - 100% opacity (12/12)
                            // Clipped to the hexagon path to remove circular artifacts at the points
                            val coreRadius = this.size.maxDimension / 2f
                            clipPath(path) {
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        colors = listOf(activeGlowColor.copy(alpha = 1.0f), activeGlowColor.copy(alpha = 0.66f)),
                                        center = center,
                                        radius = coreRadius
                                    ),
                                    radius = coreRadius
                                )
                            }
                            
                            // Attribute B: Expansion Aura (24/12 scale = 200% bigger) - 8/12 intensity
                            // Unclipped to maintain the massive bleeding ora diameter
                            val glowRadius = this.size.maxDimension 
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(activeGlowColor.copy(alpha = 0.66f), Color.Transparent),
                                    center = center,
                                    radius = glowRadius
                                ),
                                radius = glowRadius
                            )
                        }
                    }
                    .then(inputModifier),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.rotate(-rotationAngle * (180f / PI.toFloat())).offset(y = verticalOffset),
                    contentAlignment = Alignment.Center
                ) {
                    if (child != null) {
                        child()
                    } else if (secondaryLabel != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = label, color = finalTextColor, fontSize = finalFontSize, fontWeight = FontWeight.Bold, softWrap = false)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = secondaryLabel, color = finalTextColor, fontSize = finalFontSize, fontWeight = FontWeight.Bold, softWrap = false)
                        }
                    } else {
                        Text(
                            text = label, 
                            color = finalTextColor, 
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
