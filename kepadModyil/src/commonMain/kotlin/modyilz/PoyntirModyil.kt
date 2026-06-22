package modyilz

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
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

/**
 * PoyntirModyil — Typewise-style cursor navigation and delete/undelete.
 *
 * ONE finger swipe (6-way hexagonal):
 *   3  o'clock  (right)     → DPAD_RIGHT (20)
 *   9  o'clock  (left)      → DPAD_LEFT  (19)
 *   5  o'clock  (down-right)→ keycode 22
 *   A/11 o'clock(up-left)   → keycode 21
 *   7  o'clock  (down-left) → keycode 23
 *   1  o'clock  (up-right)  → keycode 24
 *
 * TWO fingers — the SECOND finger's direction determines mode:
 *   Horizontal (3/9 o'clock): delete/undelete individual CHARACTERS
 *   Diagonal   (5/A or 1/7 o'clock): delete/undelete ROWS (lines)
 *
 *   Swiping toward 3 o'clock (right) → delete
 *   Swiping toward 9 o'clock (left)  → undelete
 *   Swiping toward 5/7 o'clock (down)→ delete row
 *   Swiping toward A/1 o'clock (up)  → undelete row
 */
@Composable
fun PoyntirModyil(
    kebordKontrolir: KeyboardController?,
    onClose: (() -> Unit)? = null
) {
    // Buffer for undoing deletes: each entry is the deleted text chunk
    val charUndoBuffer = remember { mutableListOf<String>() }
    val rowUndoBuffer  = remember { mutableListOf<String>() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)

                    // --- one-finger cursor state ---
                    var oneAccX = 0f
                    var oneAccY = 0f
                    val oneThreshold = 40f

                    // --- two-finger delete state ---
                    // We track the SECOND pointer's accumulated movement
                    var twoAccX = 0f
                    var twoAccY = 0f
                    val twoThreshold = 40f

                    // Pointer ID for first vs second finger
                    var firstPointerId: Long? = null

                    var event = awaitPointerEvent()

                    do {
                        val pointers = event.changes

                        // Assign first pointer on very first event
                        if (firstPointerId == null && pointers.isNotEmpty()) {
                            firstPointerId = pointers.first().id.value
                        }

                        if (pointers.size == 1) {
                            // ── ONE finger: cursor movement ──
                            twoAccX = 0f
                            twoAccY = 0f
                            val change = pointers.first()
                            if (change.pressed) {
                                oneAccX += change.position.x - change.previousPosition.x
                                oneAccY += change.position.y - change.previousPosition.y
                                val dist = sqrt(oneAccX * oneAccX + oneAccY * oneAccY)
                                if (dist > oneThreshold) {
                                    val keyCode = angleToKeyCode(oneAccX, oneAccY)
                                    kebordKontrolir?.sendKeyEvent(keyCode)
                                    oneAccX = 0f
                                    oneAccY = 0f
                                }
                                change.consume()
                            }
                        } else if (pointers.size >= 2) {
                            // ── TWO fingers: delete / undelete ──
                            oneAccX = 0f
                            oneAccY = 0f

                            // Accumulate movement of the SECOND finger only
                            val secondPointer = pointers.firstOrNull { it.id.value != firstPointerId }
                            if (secondPointer != null && secondPointer.pressed) {
                                twoAccX += secondPointer.position.x - secondPointer.previousPosition.x
                                twoAccY += secondPointer.position.y - secondPointer.previousPosition.y
                                secondPointer.consume()
                            }

                            val twoDist = sqrt(twoAccX * twoAccX + twoAccY * twoAccY)
                            if (twoDist > twoThreshold) {
                                val angleDeg = atan2(twoAccY.toDouble(), twoAccX.toDouble()) * 180 / PI
                                val isHorizontal = abs(angleDeg) < 30 || abs(angleDeg) > 150

                                if (isHorizontal) {
                                    // 3/9 o'clock → delete/undelete CHARACTERS
                                    if (twoAccX > 0) {
                                        // Swipe right: delete one character
                                        val ch = kebordKontrolir?.getTextBeforeCursor(1)
                                        if (!ch.isNullOrEmpty()) {
                                            charUndoBuffer.add(ch)
                                            kebordKontrolir?.deletSirawndenqTekst(1, 0)
                                        }
                                    } else {
                                        // Swipe left: undelete one character
                                        if (charUndoBuffer.isNotEmpty()) {
                                            kebordKontrolir?.commitText(charUndoBuffer.removeLast())
                                        }
                                    }
                                } else {
                                    // 5/A or 1/7 o'clock → delete/undelete ROWS (lines)
                                    val isDown = twoAccY > 0
                                    if (isDown) {
                                        // Swipe toward 5 or 7 o'clock: delete the row before cursor
                                        val textBefore = kebordKontrolir?.getTextBeforeCursor(500) ?: ""
                                        val lastNewline = textBefore.lastIndexOf('\n')
                                        val rowText = if (lastNewline >= 0) {
                                            // delete from after previous newline up to cursor (including the newline)
                                            textBefore.substring(lastNewline) // includes '\n' + row
                                        } else {
                                            textBefore // whole first line
                                        }
                                        if (rowText.isNotEmpty()) {
                                            rowUndoBuffer.add(rowText)
                                            kebordKontrolir?.deletSirawndenqTekst(rowText.length, 0)
                                        }
                                    } else {
                                        // Swipe toward A or 1 o'clock: undelete the last deleted row
                                        if (rowUndoBuffer.isNotEmpty()) {
                                            kebordKontrolir?.commitText(rowUndoBuffer.removeLast())
                                        }
                                    }
                                }

                                twoAccX = 0f
                                twoAccY = 0f
                            }
                        }

                        event = awaitPointerEvent()
                    } while (event.changes.any { it.pressed })
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text("poyntir", color = Color.White.copy(alpha = 0.5f), fontSize = 24.sp)
    }
}

/**
 * Maps accumulated X/Y swipe to a 6-way hexagonal keycode:
 *   3  o'clock → 20 (DPAD_RIGHT)
 *   9  o'clock → 19 (DPAD_LEFT)
 *   5  o'clock → 22
 *   A/11 o'clock→ 21
 *   7  o'clock → 23
 *   1  o'clock → 24
 */
private fun angleToKeyCode(dx: Float, dy: Float): Int {
    val deg = atan2(dy.toDouble(), dx.toDouble()) * 180 / PI
    return when {
        deg >= -30 && deg < 30   -> 20 // 3 o'clock  → right
        deg >= 30  && deg < 90   -> 22 // 5 o'clock  → down-right
        deg >= 90  && deg < 150  -> 23 // 7 o'clock  → down-left
        deg >= 150 || deg < -150 -> 19 // 9 o'clock  → left
        deg >= -150 && deg < -90 -> 21 // 11/A o'clock → up-left
        else                     -> 24 // 1 o'clock  → up-right
    }
}
