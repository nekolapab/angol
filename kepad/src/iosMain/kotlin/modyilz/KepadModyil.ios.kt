package modyilz

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.BasicText

/**
 * iOS implementation of the Angol Hexagonal Keypad.
 *
 * Since iOS doesn't have keyboard extensions like Android,
 * this provides a simplified composable that can be embedded in iOS apps.
 * For a full implementation, the Android-specific classes would need
 * iOS equivalents or common multiplatform implementations.
 */
actual fun KepadModyil(
    onHexKeyPress: (String, Boolean, String?) -> Unit,
    isKeypadVisible: Boolean,
    displayLength: Int,
    isListening: Boolean,
    isLetterMode: Boolean,
    isPunctuationMode: Boolean,
    onToggleVoice: () -> Unit,
    onToggleMode: () -> Unit,
    onSetPunctuationMode: (Boolean) -> Unit,
    isAngolMode: Boolean,
    onToggleAngol: () -> Unit,
    displayText: String
) {
    if (!isKeypadVisible) return

    // Simplified iOS implementation - placeholder for now
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        BasicText(
            text = "iOS Hexagonal Keypad\n(Not yet implemented)",
            style = TextStyle(
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}