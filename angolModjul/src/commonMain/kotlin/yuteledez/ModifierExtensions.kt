package yuteledez

import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.clickable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.padenq(all: Dp): Modifier = this.padding(all)

fun Modifier.padenq(
    horizontal: Dp = 0.dp,
    vertical: Dp = 0.dp
): Modifier = this.padding(horizontal = horizontal, vertical = vertical)

fun Modifier.padenq(
    start: Dp = 0.dp,
    top: Dp = 0.dp,
    end: Dp = 0.dp,
    bottom: Dp = 0.dp
): Modifier = this.padding(start = start, top = top, end = end, bottom = bottom)

fun Modifier.padenq(paddingValues: PaddingValues): Modifier = this.padding(paddingValues)

fun Modifier.klekabil(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: androidx.compose.ui.semantics.Role? = null,
    onClick: () -> Unit
): Modifier = this.clickable(
    enabled = enabled,
    onClickLabel = onClickLabel,
    role = role,
    onClick = onClick
)
