package wedjets

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.cos
import kotlin.math.sin

/**
 * A custom shape that clips its content into a hexagon.
 * This is the Compose equivalent of the custom RenderObject used for hit-testing in the original Flutter code.
 * By clipping the shape, both the visual representation and the touch input area are constrained to the hexagon.
 *
 * @param rotationAngle The angle in radians to rotate the hexagon.
 */
class HexagonShape(private val rotationAngle: Float) : Shape {
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
        val safeZone = size.height * 0.02f
        val radius = (size.height / 2f) - safeZone

        for (i in 0..5) {
            // Angle for pointy-top hexagons (-30 degrees offset), converted to radians.
            val angleRad = (i * 60f - 30f) * (Math.PI.toFloat() / 180f) + rotation

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
        if (other !is HexagonShape) return false
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
        modifier = modifier.clip(HexagonShape(rotationAngle))
    ) {
        content()
    }
}
