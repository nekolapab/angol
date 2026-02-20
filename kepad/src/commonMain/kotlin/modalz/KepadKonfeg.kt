package modalz

import androidx.compose.ui.graphics.Color

object KepadKonfeg {
    val innerLetterMode: List<String> = listOf("a", "e", "i", "u", "o", "⌫")
    val innerNumberMode: List<String> = listOf("+", "*", "=", "(", "{", "⌫")

    // Clockwise from 1 o'clock
    val outerTap: List<String> = listOf(
        "l", "lx", "x", "d", "t", "c", "g", "k", "f", "b", "p", "s"
    )

    val outerLongPress: List<String> = listOf(
        "lh", "h", "n", "y", "r", "j", "nq", "q", "v", "w", "m", "z"
    )

    val outerTapNumber: List<String> = listOf(
        "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "A", "O"
    )

    val outerLongPressNumber: List<String> = listOf(
        "!", "!#", "$", "%", "^", "&", "*", "(", ")", "_", "|", "~"
    )

    val innerLongPressNumber: List<String> = listOf(
        "-", "/", ":", ")", "}", "⌫"
    )

    // 12 rainbow colors for outer ring
    // 12 colors for outer ring: Black/White at specified indices, rainbow elsewhere
    val rainbowColors: List<Color> = listOf(
        Color.Black,       // 0: Red -> Black
        Color(0xFFFF8000), // 1: Orange
        Color.White,       // 2: Yellow -> White
        Color(0xFF80FF00), // 3: Chartreuse
        Color.Black,       // 4: Green -> Black
        Color(0xFF00FF80), // 5: Turquoise (6 o'clock)
        Color.White,       // 6: Aqua -> White
        Color(0xFF0080FF), // 7: Azure
        Color.Black,       // 8: Blue -> Black
        Color(0xFF8000FF), // 9: Purple
        Color.White,       // 10: Fuchsia -> White
        Color(0xFF800000)  // 11: Maroon (12 o'clock)
    )

    val innerRingColors: List<Color> = listOf(
        Color(0xFFFF0000), // red
        Color(0xFFFFFF00), // yellow
        Color(0xFF00FF00), // green
        Color(0xFF00FFFF), // aqua
        Color(0xFF0000FF), // blue
        Color(0xFFFF00FF)  // fuchsia
    )

    /**
     * Calculates the complementary (inverted) color.
     * Note: The original Dart code for this function appeared to be incorrect.
     * It has been translated to correctly calculate the inverted color for Compose.
     */
    fun getComplementaryColor(color: Color): Color {
        // Compose Color components (red, green, blue) are Floats from 0.0f to 1.0f.
        return Color(
            red = 1.0f - color.red,
            green = 1.0f - color.green,
            blue = 1.0f - color.blue,
            alpha = color.alpha
        )
    }
}
