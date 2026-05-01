package wedjets

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import yuteledez.HeksagonDjeyometre
import modalz.HeksagonPozecon
import modalz.KepadKonfeg
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.math.sqrt

@Composable
fun HeksagonGred(
    geometry: HeksagonDjeyometre,
    items: List<GredItem>,
    onSwap: (Int, Int) -> Unit,
    onCopyToEmpty: (Int, Int) -> Unit,
    onMoveToCenter: (Int) -> Unit,
    onDropOnFolder: (Int, Int) -> Unit,
    modifier: Modifier = Modifier,
    centerLabel: String = "",
    centerColor: Color = Color.Black
) {
    var draggingIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var currentHoverIndex by remember { mutableStateOf<Int?>(null) }

    val allHexPositions = remember(geometry) {
        val inner = geometry.getEnirRenqKowordenats().map { geometry.aksyalTuPeksel(it.q, it.r) }
        val outer = geometry.getAwdirRenqKowordenats().map { geometry.aksyalTuPeksel(it.q, it.r) }
        // Order: 0-5 Inner, 6-17 Outer, 18 Center
        inner + outer + listOf(geometry.sentir)
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(geometry, items) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { offset ->
                        val idx = getHexIndexFromPosition(
                            offset.x, offset.y, 
                            size.width.toFloat(), size.height.toFloat(), 
                            allHexPositions, geometry.heksSayz
                        )
                        if (idx != null && items.any { it.index == idx }) {
                            draggingIndex = idx
                            dragOffset = offset
                        }
                    },
                    onDrag = { change, dragAmount ->
                        dragOffset += dragAmount
                        val idx = getHexIndexFromPosition(
                            dragOffset.x, dragOffset.y, 
                            size.width.toFloat(), size.height.toFloat(), 
                            allHexPositions, geometry.heksSayz
                        )
                        currentHoverIndex = idx
                    },
                    onDragEnd = {
                        val fromIdx = draggingIndex
                        val toIdx = currentHoverIndex
                        
                        if (fromIdx != null) {
                            when {
                                toIdx == 18 -> onMoveToCenter(fromIdx)
                                toIdx != null -> {
                                    val targetItem = items.find { it.index == toIdx }
                                    if (targetItem != null) {
                                        if (targetItem.isFolder) {
                                            onDropOnFolder(fromIdx, toIdx)
                                        } else {
                                            onSwap(fromIdx, toIdx)
                                        }
                                    } else {
                                        onCopyToEmpty(fromIdx, toIdx)
                                    }
                                }
                            }
                        }
                        draggingIndex = null
                        currentHoverIndex = null
                        dragOffset = Offset.Zero
                    },
                    onDragCancel = {
                        draggingIndex = null
                        currentHoverIndex = null
                        dragOffset = Offset.Zero
                    }
                )
            }
    ) {
        val hexWidthDp = geometry.heksWidlx.dp

        // Render Center
        HeksagonWedjet(
            label = centerLabel,
            backgroundColor = if (currentHoverIndex == 18) Color.White.copy(alpha = 0.5f) else centerColor,
            textColor = KepadKonfeg.getComplementaryColor(centerColor),
            size = hexWidthDp,
            fontSize = (geometry.heksWidlx * 0.8).toFloat(),
            modifier = Modifier.align(Alignment.Center).offset(x = geometry.sentir.x.dp, y = geometry.sentir.y.dp)
        )

        // Render Items
        allHexPositions.forEachIndexed { index, pos ->
            if (index < 18) {
                val item = items.find { it.index == index }
                val isDragging = draggingIndex == index
                val isHovered = currentHoverIndex == index && draggingIndex != null

                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(x = pos.x.dp, y = pos.y.dp)
                ) {
                    if (item != null) {
                        HeksagonWedjet(
                            label = item.label,
                            backgroundColor = if (isHovered) Color.White.copy(alpha = 0.5f) else item.color,
                            textColor = KepadKonfeg.getComplementaryColor(item.color),
                            size = hexWidthDp,
                            fontSize = (geometry.heksWidlx * 0.8).toFloat(),
                            modifier = if (isDragging) Modifier.offset(
                                x = (dragOffset.x - pos.x - this@BoxWithConstraints.constraints.maxWidth / 2).dp,
                                y = (dragOffset.y - pos.y - this@BoxWithConstraints.constraints.maxHeight / 2).dp
                            ) else Modifier
                        )
                    } else if (isHovered) {
                        // Ghost for empty target
                        HeksagonWedjet(
                            label = "+",
                            backgroundColor = Color.Gray.copy(alpha = 0.3f),
                            textColor = Color.White,
                            size = hexWidthDp,
                            fontSize = (geometry.heksWidlx * 0.5).toFloat()
                        )
                    }
                }
            }
        }
    }
}

data class GredItem(
    val index: Int,
    val label: String,
    val color: Color,
    val isFolder: Boolean = false,
    val deyda: Any? = null
)

private fun getHexIndexFromPosition(
    offsetX: Float, offsetY: Float, 
    w: Float, h: Float, 
    allHexPositions: List<HeksagonPozecon>, 
    hexSize: Double
): Int? {
    val localX = offsetX - w / 2
    val localY = offsetY - h / 2
    var closestIndex: Int? = null
    var minDistSq = Double.MAX_VALUE
    for (i in allHexPositions.indices) {
        val hexPos = allHexPositions[i]
        val distSq = (localX - hexPos.x).pow(2) + (localY - hexPos.y).pow(2)
        if (distSq < minDistSq) {
            minDistSq = distSq; closestIndex = i
        }
    }
    if (closestIndex != null) {
        val center = allHexPositions[closestIndex]
        val dx = kotlin.math.abs(localX - center.x)
        val dy = kotlin.math.abs(localY - center.y)
        val sqrt3Val = 1.73205080757
        if (dx > hexSize * sqrt3Val / 2.0) return null
        if ((dx + sqrt3Val * dy) <= (sqrt3Val * hexSize)) return closestIndex
    }
    return null
}
