package modyilz

import androidx.compose.runtime.Composable

// Expect/actual pattern for cross-platform keypad
@Composable
expect fun KepadModyil(
    onHexKeyPress: (String, Boolean, String?) -> Unit,
    isKeypadVisible: Boolean,
    displayLength: Int
)