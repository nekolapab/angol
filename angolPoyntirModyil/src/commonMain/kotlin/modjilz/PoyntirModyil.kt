package modjilz

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

enum class DeleteType { CHAR, ROW }
data class DeleteDirection(val isHorizontal: Boolean, val isPositive: Boolean)

/**
 * PoyntirModyil â€” Typewise-style cursor navigation and delete/undelete.
 *
 * 1st finger:
 *   Always moves the cursor (6-way hexagonal directions).
 *
 * 2nd finger:
 *   Its initial movement direction locks the "delete" direction.
 *   Moving in that direction deletes. Moving in the opposite direction undeletes.
 *   If initial direction is horizontal (3 or 9 o'clock), it deletes/undeletes CHARACTERS.
 *   If initial direction is vertical (5/7 or 1/A o'clock), it deletes/undeletes ROWS (lines).
 */
@Composable
fun PoyntirModyil(
    kebordKontrolir: KeyboardController?,
    onKloz: (() -> Unit)? = null
) {
    val charUndoBuffer = remember { mutableListOf<String>() }
    val rowUndoBuffer  = remember { mutableListOf<String>() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)

                    var oneAccX = 0f
                    var oneAccY = 0f
                    val oneThreshold = 40f

                    var twoAccX = 0f
                    var twoAccY = 0f
                    val twoThreshold = 40f

                    var firstPointerId: Long? = null
                    var secondPointerId: Long? = null
                    
                    var deleteDir: DeleteDirection? = null

                    var event = awaitPointerEvent()

                    do {
                        val pointers = event.changes

                        var firstPointer = pointers.find { it.id.value == firstPointerId }
                        if (firstPointer == null || !firstPointer.pressed) {
                            firstPointer = pointers.firstOrNull { it.pressed }
                            firstPointerId = firstPointer?.id?.value
                            oneAccX = 0f
                            oneAccY = 0f
                        }

                        var secondPointer = pointers.find { it.id.value == secondPointerId }
                        if (secondPointer == null || !secondPointer.pressed) {
                            secondPointer = pointers.firstOrNull { it.pressed && it.id.value != firstPointerId }
                            secondPointerId = secondPointer?.id?.value
                            twoAccX = 0f
                            twoAccY = 0f
                            deleteDir = null
                        }

                        if (firstPointer != null && firstPointer.pressed) {
                            oneAccX += firstPointer.position.x - firstPointer.previousPosition.x
                            oneAccY += firstPointer.position.y - firstPointer.previousPosition.y
                            val dist = sqrt(oneAccX * oneAccX + oneAccY * oneAccY)
                            if (dist > oneThreshold) {
                                val keyCode = angleToKeyCode(oneAccX, oneAccY)
                                kebordKontrolir?.sendKeyEvent(keyCode)
                                oneAccX = 0f
                                oneAccY = 0f
                            }
                            firstPointer.consume()
                        }

                        if (secondPointer != null && secondPointer.pressed) {
                            twoAccX += secondPointer.position.x - secondPointer.previousPosition.x
                            twoAccY += secondPointer.position.y - secondPointer.previousPosition.y
                            val twoDist = sqrt(twoAccX * twoAccX + twoAccY * twoAccY)
                            if (twoDist > twoThreshold) {
                                val angleDeg = atan2(twoAccY.toDouble(), twoAccX.toDouble()) * 180 / PI
                                val isHorizontal = abs(angleDeg) < 30 || abs(angleDeg) > 150
                                val isPositive = if (isHorizontal) twoAccX > 0 else twoAccY > 0

                                if (deleteDir == null) {
                                    deleteDir = DeleteDirection(isHorizontal, isPositive)
                                }

                                if (isHorizontal == deleteDir?.isHorizontal) {
                                    val isDelete = (isPositive == deleteDir?.isPositive)
                                    if (isHorizontal) {
                                        if (isDelete) {
                                            val ch = kebordKontrolir?.getTextBeforeCursor(1)
                                            if (!ch.isNullOrEmpty()) {
                                                charUndoBuffer.add(ch)
                                                kebordKontrolir?.deletSirawndenqTekst(1, 0)
                                            }
                                        } else {
                                            if (charUndoBuffer.isNotEmpty()) {
                                                kebordKontrolir?.commitText(charUndoBuffer.removeLast())
                                            }
                                        }
                                    } else {
                                        if (isDelete) {
                                            val textBefore = kebordKontrolir?.getTextBeforeCursor(500) ?: ""
                                            val lastNewline = textBefore.lastIndexOf('\n')
                                            val rowText = if (lastNewline >= 0) textBefore.substring(lastNewline) else textBefore
                                            if (rowText.isNotEmpty()) {
                                                rowUndoBuffer.add(rowText)
                                                kebordKontrolir?.deletSirawndenqTekst(rowText.length, 0)
                                            }
                                        } else {
                                            if (rowUndoBuffer.isNotEmpty()) {
                                                kebordKontrolir?.commitText(rowUndoBuffer.removeLast())
                                            }
                                        }
                                    }
                                }

                                twoAccX = 0f
                                twoAccY = 0f
                            }
                            secondPointer.consume()
                        }

                        event = awaitPointerEvent()
                    } while (event.changes.any { it.pressed })
                }
            },
        contentAlignment = Alignment.Center
    ) {
        wedjets.Heksagon(
            label = "poyntir",
            backgroundColor = Color.Transparent,
            textColor = Color.White,
            size = 80.dp,
            fontSizeFactor = 10f / 12f,
            ezKonsestentSayz = true
        )
    }
}

private fun angleToKeyCode(dx: Float, dy: Float): Int {
    val deg = atan2(dy.toDouble(), dx.toDouble()) * 180 / PI
    return when {
        deg >= -30 && deg < 30   -> 20 // right
        deg >= 30  && deg < 90   -> 22 // down-right
        deg >= 90  && deg < 150  -> 23 // down-left
        deg >= 150 || deg < -150 -> 19 // left
        deg >= -150 && deg < -90 -> 21 // up-left
        else                     -> 24 // up-right
    }
}

