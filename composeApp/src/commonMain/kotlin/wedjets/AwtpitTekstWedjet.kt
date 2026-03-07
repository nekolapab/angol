package wedjets

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

/**
 * A simple composable for displaying text, equivalent to the original StatelessWidget.
 *
 * @param text The text to be displayed.
 * @param style The style to apply to the text.
 * @param modifier The modifier to be applied to the Text composable.
 */
@Composable
fun AwtpitTekstWedjet(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = style,
        modifier = modifier,
        color = style.color // Explicitly set color to ensure it's applied
    )
}
