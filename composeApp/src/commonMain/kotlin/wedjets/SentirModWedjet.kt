package wedjets

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import yuteledez.HeksagonDjeyometre

/**
 * A wrapper composable for creating the central hexagon widget.
 *
 * It simplifies the creation of the main hexagon by pre-configuring it
 * with properties derived from the HeksagonDjeyometre.
 */
@Composable
fun SentirModWedjet(
    geometry: HeksagonDjeyometre,
    onPressedChanged: ((Boolean) -> Unit)? = null,
    onTap: (() -> Unit)? = null,
    onLongPress: (() -> Unit)? = null,
    backgroundColor: Color,
    textColor: Color,
    label: String = "",
    fontSize: Float? = null,
    onHover: ((Boolean) -> Unit)? = null,
    child: @Composable (() -> Unit)? = null
) {
    val sizeInDp = geometry.heksWidlx.toFloat().dp

    HeksagonWedjet(
        label = label,
        backgroundColor = backgroundColor,
        textColor = textColor,
        size = sizeInDp,
        fontSize = fontSize,
        rotationAngle = geometry.roteyconAngol.toFloat(),
        onTap = onTap,
        onLongPress = onLongPress,
        onPressedChanged = onPressedChanged,
        onHover = onHover,
        child = child
    )
}
