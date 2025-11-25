package com.example.angol.ime.compose

import androidx.compose.ui.graphics.Color

object KeypadConfig {
    val innerLetterMode = listOf("a", "e", "i", "u", "o", "⌫")
    val innerNumberMode = listOf("+", "*", "=", "(", "{", "⌫")

    val outerTap = listOf(
        "s", "l", "lx", "x", "d", "t", "c", "g", "k", "f", "b", "p"
    )

    val outerLongPress = listOf(
        "z", "lh", "h", "n", "y", "r", "j", "nq", "q", "v", "w", "m"
    )

    val outerTapNumber = listOf(
        "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "A", "O"
    )

    val outerLongPressNumber = listOf(
        "!", "!#", "\$", "%", "^", "&", "*", "(", ")", "_", "|", "~"
    )

    val innerLongPressNumber = listOf(
        "-", "/", ":", ")", "}", "⌫"
    )

    val rainbowColors = listOf(
        Color(0xFFFF0000), // red
        Color(0xFFFF8000), // orange
        Color(0xFFFFFF00), // yellow
        Color(0xFF80FF00), // chartreuse
        Color(0xFF00FF00), // green
        Color(0xFF00FF80), // turquoise
        Color(0xFF00FFFF), // aqua
        Color(0xFF0080FF), // azure
        Color(0xFF0000FF), // blue
        Color(0xFF8000FF), // purple
        Color(0xFFFF00FF), // fuchsia
        Color(0xFF800000)  // maroon
    )

    val innerRingColors = listOf(
        rainbowColors[0], // red
        rainbowColors[2], // yellow
        rainbowColors[4], // green
        rainbowColors[6], // aqua
        rainbowColors[8], // blue
        rainbowColors[10] // fuchsia
    )

    fun getComplementaryColor(color: Color): Color {
        return Color(
            red = 1f - color.red,
            green = 1f - color.green,
            blue = 1f - color.blue,
            alpha = color.alpha
        )
    }
}
