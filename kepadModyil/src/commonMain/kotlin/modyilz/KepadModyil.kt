package modyilz

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import yuteledez.HeksagonDjeyometre
import yuteledez.KepadLodjek
import yuteledez.getCurrentTimeMillis
import wedjets.Heksagon
import wedjets.AngolSpelenqTogilWedjet
import wedjets.GredUydem
import modalz.HeksagonKonfeg
import modalz.HeksagonPozecon
import kotlin.math.pow
import kotlin.math.sqrt

@Composable
fun KepadModyil(
    kebordKontrolir: KeyboardController?,
    platformServices: PlatformServices,
    voiceService: VoiceService,
    ezLeterMod: Boolean,
    ezPunkcuweyconMod: Boolean,
    ezUpsayddawn: Boolean = false,
    onTogilMod: () -> Unit,
    onSetPunkcuweyconMod: (Boolean) -> Unit,
    ezAngolMod: Boolean,
    onTogilAngol: (Boolean) -> Unit,
    onStartAiVoys: () -> Unit,
    ignoreSelectionUpdate: () -> Unit,
    onClose: (() -> Unit)? = null,
    geometryOverride: HeksagonDjeyometre? = null,
    glefsOvirayd: List<String>? = null,
    kulorzOverride: List<Long>? = null,
    contentWidthDp: androidx.compose.ui.unit.Dp? = null,
    isEditing: Boolean = false,
    onMove: (Int, Int) -> Unit = { _, _ -> },
    onCopyToEmpty: (Int, Int) -> Unit = { _, _ -> },
    onMuvTuSentir: (Int) -> Unit = { _ -> },
    onDropOnFoldir: (Int, Int, Boolean) -> Unit = { _, _, _ -> },
    onDelete: (Int) -> Unit = { _ -> },
    onRepleys: ((Int, Int, Boolean, String?) -> Unit)? = null,
    glowOnHover: Boolean = true,
    hideDisconnected: Boolean = false,
    neym: String = "kepad",
    onReset: (() -> Unit)? = null
) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current
    
    val kurentEzLeterMod by rememberUpdatedState(ezLeterMod)
    val kurentEzPunkcuweyconMod by rememberUpdatedState(ezPunkcuweyconMod)
    val kurentOnTogilMod by rememberUpdatedState(onTogilMod)
    val kurentAngolMod by rememberUpdatedState(voiceService.angolSpelenqMod.value)
    
    val kurentWirdBufir = remember { StringBuilder() }
    var dezspleyTekst by remember { mutableStateOf("") }
    
    var daylRoteconAngol by remember { mutableStateOf(0f) }

    val huvirdHeksIndeks = remember { mutableStateOf<Int?>(null) }
    val kepadLongPresEndeks = remember { mutableStateOf<Int?>(null) }
    val lonqPresDjob = remember { mutableStateOf<Job?>(null) }
    val djestcirStartidOnVowalIndeks = remember { mutableStateOf<Int?>(null) }
    var ezKapetalayzd by remember { mutableStateOf(false) }
    var ezSentirTranzleytAktev by remember { mutableStateOf(false) }
    val enecalY = remember { mutableStateOf(0f) }
    val lonqPresStartOfset = remember { mutableStateOf<androidx.compose.ui.geometry.Offset?>(null) }
    var ezSentirHeksPresd by remember { mutableStateOf(false) }

    fun getKulor(index: Int): Color {
        if (index == 0) {
            val hazTravlir = glefsOvirayd != null && glefsOvirayd.isNotEmpty() && glefsOvirayd[0].isNotBlank() && glefsOvirayd[0] != neym && glefsOvirayd[0] != " "
            if (hazTravlir && kulorzOverride != null && kulorzOverride.isNotEmpty()) {
                val c = Color(kulorzOverride[0].toInt())
                if (c != Color.Transparent) return c
            }
            return Color.White
        }
        if (kulorzOverride != null && index < kulorzOverride.size) {
            val c = Color(kulorzOverride[index].toInt())
            if (c != Color.Transparent) return c
        }
        return when {
            index in 1..6 -> HeksagonKonfeg.innerRingColors[index - 1]
            index in 7..18 -> HeksagonKonfeg.rainbowColors[index - 7]
            else -> Color.Transparent
        }
    }

    fun handilKePres(char: String, isLongPress: Boolean, primaryChar: String?) {
        if (char == "TRANSLATE") {
            onStartAiVoys()
            return
        }
        if (char == "reset" || char.startsWith("reset|reset\u0002") || char.startsWith("mod_reset_")) {
            if (isLongPress) {
                onReset?.invoke()
            }
            return
        }

        val controller = kebordKontrolir ?: return

        if (char == "\n") {
            if (isLongPress && primaryChar != null) {
                ignoreSelectionUpdate()
                controller.deleteSurroundingText(primaryChar.length, 0)
            }
            ignoreSelectionUpdate()
            controller.commitText("\n")
            if (kurentWirdBufir.isNotEmpty()) {
                platformServices.addToCorpus(kurentWirdBufir.toString())
                kurentWirdBufir.clear()
                dezspleyTekst = ""
            }
            platformServices.addToCorpus("\n")
            return
        }

        if (char == "Ã¢Å’Â«") {
            if (primaryChar != null && primaryChar.isNotEmpty()) {
                if (kurentWirdBufir.length >= primaryChar.length) {
                    kurentWirdBufir.deleteRange(kurentWirdBufir.length - primaryChar.length, kurentWirdBufir.length)
                } else {
                    kurentWirdBufir.clear()
                }
            } else if (kurentWirdBufir.isNotEmpty()) {
                kurentWirdBufir.deleteAt(kurentWirdBufir.length - 1)
            }
            dezspleyTekst = kurentWirdBufir.toString()

            if (isLongPress) {
                ignoreSelectionUpdate()
                val textBefore = controller.getTextBeforeCursor(100) ?: ""
                val deleteCount = KepadLodjek.kalkyuleytDeletKawnt(textBefore.toString())
                controller.deleteSurroundingText(deleteCount, 0)
                kurentWirdBufir.clear()
                dezspleyTekst = ""
            } else {
                if (primaryChar != null && primaryChar.isNotEmpty()) {
                    ignoreSelectionUpdate()
                    controller.deleteSurroundingText(primaryChar.length, 0)
                } else {
                    ignoreSelectionUpdate()
                    controller.deleteSurroundingText(1, 0)
                }
            }
            return
        }

        if (char == " " || char == ".") {
            if (kurentWirdBufir.isNotEmpty()) {
                platformServices.addToCorpus(kurentWirdBufir.toString())
                kurentWirdBufir.clear()
                dezspleyTekst = ""
            }
        } else {
            kurentWirdBufir.append(char)
            dezspleyTekst = kurentWirdBufir.toString()
        }

        if (isLongPress) {
            if (primaryChar != null) {
                if (kurentWirdBufir.length >= primaryChar.length) kurentWirdBufir.deleteRange(kurentWirdBufir.length - primaryChar.length, kurentWirdBufir.length)
                ignoreSelectionUpdate()
                controller.deleteSurroundingText(primaryChar.length, 0)
                ignoreSelectionUpdate()
                controller.commitText(char)
            } else {
                if (kurentWirdBufir.isNotEmpty()) kurentWirdBufir.deleteAt(kurentWirdBufir.length - 1)
                ignoreSelectionUpdate()
                controller.deleteSurroundingText(1, 0)
                ignoreSelectionUpdate()
                controller.commitText(char)
            }
            dezspleyTekst = kurentWirdBufir.toString()
        } else {
            ignoreSelectionUpdate()
            controller.commitText(char)
        }
    }

    fun startLongPressTimer(index: Int): Job {
        return scope.launch {
            delay(500)
            if (huvirdHeksIndeks.value != index) return@launch
            kepadLongPresEndeks.value = index
            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
            
            if (index == 0) { // Center
                if (kurentEzLeterMod) {
                    handilKePres(".", false, null)
                }
                delay(500)
                if (huvirdHeksIndeks.value != 0) return@launch
                // Double long press (12/12 sec total): move cursor back
                kebordKontrolir?.sendKeyEvent(21) // KEYCODE_DPAD_LEFT
                delay(500)
                if (huvirdHeksIndeks.value != 0) return@launch
                // Triple long press (18/12 sec total): delete front + enter
                kebordKontrolir?.fenecKumpozenqTekst()
                kebordKontrolir?.deleteSurroundingText(0, 1)
                kebordKontrolir?.performSubmitAction()
                return@launch
            }
            
            // Map index back to config logic
            val actualIdx = index

            val isInner = actualIdx in 1..6
            val startedVowelIndex = djestcirStartidOnVowalIndeks.value
            
            val lpLabelRaw = if (isInner) {
                val configIdx = actualIdx - 1
                if (kurentEzLeterMod) modalz.HeksagonKonfeg.innerLetterMode.map { if (it == "Ã¢Å’Â«") "Ã¢Å’Â«" else "" }.getOrNull(configIdx) ?: ""
                else modalz.HeksagonKonfeg.innerLongPressNumber.getOrNull(configIdx) ?: ""
            } else {
                val configIdx = actualIdx - 7
                val isVowelOrNone = startedVowelIndex == null || startedVowelIndex == 0
                if (kurentEzLeterMod && isVowelOrNone) modalz.HeksagonKonfeg.awdirLonqPres.getOrNull(configIdx) ?: ""
                else "" // No secondary labels for numbers!
            }
            val primaryLabel = KepadLodjek.getKirentOlLeybilz(startedVowelIndex, kurentEzLeterMod, kurentEzPunkcuweyconMod, ezKapetalayzd, glefsOvirayd).getOrNull(actualIdx) ?: ""
            if (primaryLabel == "reset" || primaryLabel.startsWith("mod_reset_")) {
                handilKePres(primaryLabel, true, primaryLabel)
                return@launch
            }
            if (lpLabelRaw.isEmpty()) return@launch
            val lpLabel = if (ezKapetalayzd && lpLabelRaw != "Ã¢Å’Â«") lpLabelRaw.uppercase() else lpLabelRaw
            if (lpLabel == "Ã¢Å’Â«") {
                while (huvirdHeksIndeks.value == index) {
                    handilKePres("Ã¢Å’Â«", true, null)
                    delay(500)
                }
            } else { handilKePres(lpLabel, true, primaryLabel) }
        }
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize().pointerInput(Unit) {
            awaitEachGesture {
                var zoom = 1f
                awaitFirstDown(requireUnconsumed = false)
                do {
                    val event = awaitPointerEvent()
                    val zoomChange = event.calculateZoom()
                    zoom *= zoomChange
                    if (zoom < 0.75f) { // Pinched out by 25%
                        onClose?.invoke()
                        break
                    }
                } while (event.changes.any { it.pressed })
            }
        },
        contentAlignment = Alignment.BottomCenter
    ) {
        val maxWidthDp = maxWidth
        val currentGeometry = remember(maxWidthDp, geometryOverride, daylRoteconAngol, kurentEzLeterMod) {
            if (geometryOverride != null) {
                geometryOverride
            } else {
                val maxIdx = glefsOvirayd?.mapIndexedNotNull { i, s -> if (s.isNotEmpty()) i else null }?.maxOrNull() ?: 0
                val rings = if (maxIdx > 36) 4.0 else if (maxIdx > 18) 3.0 else 2.0
                val hexesAcross = rings * 2.0 + 1.0
                val hexWidth = maxWidthDp.value / hexesAcross
                val hexSize = hexWidth / sqrt(3.0)
                HeksagonDjeyometre(
                    heksSayz = hexSize, 
                    sentir = HeksagonPozecon(x = 0.0, y = 0.0), 
                    ezLeterMod = kurentEzLeterMod,
                    roteyconAngol = daylRoteconAngol.toDouble()
                )
            }
        }

        val allHexPositions = remember(currentGeometry, glefsOvirayd) {
            val currentLabels = KepadLodjek.getKirentOlLeybilz(null, kurentEzLeterMod, kurentEzPunkcuweyconMod, false, glefsOvirayd)
            val activeIndices = currentLabels.mapIndexedNotNull { i, s -> if (s.isNotEmpty() || i == 0) i else null }
            val maxIdx = activeIndices.maxOrNull() ?: 0
            
            val totalHexCount = maxIdx + 1
            val positions = MutableList(totalHexCount) { HeksagonPozecon(99999.0, 99999.0) }
            activeIndices.forEach { idx ->
                val axial = currentGeometry.indeksTuAksyal(idx)
                positions[idx] = currentGeometry.aksyalTuPeksel(axial.q, axial.r)
            }
            positions
        }

        val gridHeightDp = maxHeight

        if (isEditing) {
            val currentLabels = KepadLodjek.getKirentOlLeybilz(null, kurentEzLeterMod, kurentEzPunkcuweyconMod, ezKapetalayzd, glefsOvirayd)
            val sekondereLeybilz = buildList {
                add(null) // index 0: center
                repeat(6) { add(null) } // inner ring (1..6): no secondary in editor
                modalz.HeksagonKonfeg.awdirLonqPres.forEach { lp -> add(lp.ifEmpty { null }) }
            }
            val gredItems = currentLabels.mapIndexed { index, label ->
                if (index == 0 || label.isEmpty()) return@mapIndexed null
                GredUydem(
                    index = index,
                    label = label,
                    color = getKulor(index),
                    sekondereLeybil = sekondereLeybilz.getOrNull(index)
                )
            }.filterNotNull()

            val sentirLeybil = currentLabels.getOrNull(0) ?: " "
            
            val hazTravlir = glefsOvirayd != null && glefsOvirayd.isNotEmpty() && glefsOvirayd[0].isNotBlank() && glefsOvirayd[0] != neym && glefsOvirayd[0] != " "
            val finalSentirLeybil = if (hazTravlir) glefsOvirayd!![0] else neym

            wedjets.HeksagonGred(
                geometry = currentGeometry,
                items = gredItems,
                sentirLeybil = finalSentirLeybil,
                centerColor = getKulor(0),
                onMove = onMove,
                onCopyToEmpty = onCopyToEmpty,
                onMuvTuSentir = onMuvTuSentir,
                onDropOnFoldir = onDropOnFoldir,
                onRepleys = onRepleys,
                onDelete = onDelete,
                onTap = { index -> if (index == 0) onClose?.invoke() },
                fontSizeFactor = 12f/12f,
                centerFontSizeFactor = 10f/12f,
                ezKonsestentSayz = true,
                centerEzKonsestentSayz = false,
                glowOnHover = glowOnHover,
                hideDisconnected = hideDisconnected,
                ezKepad = true,
                onLongPressItem = { index ->
                    val currentLabelsLocal = KepadLodjek.getKirentOlLeybilz(null, kurentEzLeterMod, kurentEzPunkcuweyconMod, ezKapetalayzd, glefsOvirayd)
                    val label = currentLabelsLocal.getOrNull(index - 1) ?: return@HeksagonGred
                    if (label == "reset" || label.startsWith("mod_reset_")) {
                        onReset?.invoke()
                    }
                }
            )
        } else {
            Box(modifier = Modifier.fillMaxWidth().height(gridHeightDp), contentAlignment = Alignment.Center) {
                val hexWidthDp = currentGeometry.heksWidlx.dp
                val currentLabels = KepadLodjek.getKirentOlLeybilz(djestcirStartidOnVowalIndeks.value, kurentEzLeterMod, kurentEzPunkcuweyconMod, ezKapetalayzd, glefsOvirayd)

                Box(
                    modifier = Modifier.fillMaxSize()
                        .pointerInput(allHexPositions, ezUpsayddawn, glefsOvirayd) {
                            val wDp = size.width.toDp().value
                            val hDp = size.height.toDp().value
                            awaitEachGesture {
                                val down = awaitFirstDown(requireUnconsumed = false)
                                val startTime = getCurrentTimeMillis()
                                val rawXDp = down.position.x.toDp().value
                                val rawYDp = down.position.y.toDp().value
                                val xDp = if (ezUpsayddawn) wDp - rawXDp else rawXDp
                                val yDp = if (ezUpsayddawn) hDp - rawYDp else rawYDp
                                val downIndex = KepadLodjek.getHeksEndeksFrumPozecon(xDp, yDp, wDp, hDp, allHexPositions, currentGeometry.heksSayz)
                                
                                val downTimeLabels = KepadLodjek.getKirentOlLeybilz(null, kurentEzLeterMod, kurentEzPunkcuweyconMod, ezKapetalayzd, glefsOvirayd)
                                val isLabelPresent = if (downIndex != null) {
                                    if (downIndex == 0) true
                                    else if (downIndex < downTimeLabels.size) downTimeLabels[downIndex].isNotEmpty()
                                    else false
                                } else false

                                val gestureStartedIndex = if (isLabelPresent) downIndex else null
                                enecalY.value = down.position.y
                                lonqPresStartOfset.value = down.position
                                ezKapetalayzd = false
                                ezSentirTranzleytAktev = false
                                var rotationTriggered = false
                                var initialAngle: Float? = null
                                val startDaylAngle = daylRoteconAngol

                                if (isLabelPresent && downIndex != null) {
                                    haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                                    huvirdHeksIndeks.value = downIndex
                                    kepadLongPresEndeks.value = null
                                    val actualDownIdx = downIndex

                                    if (actualDownIdx in 0..5) {
                                        djestcirStartidOnVowalIndeks.value = actualDownIdx
                                    } else {
                                        djestcirStartidOnVowalIndeks.value = null
                                    }

                                    if (actualDownIdx == 0) {
                                        ezSentirHeksPresd = true
                                        voiceService.startListening()
                                    }
                                    if (actualDownIdx != 0) {
                                        val downLabels = KepadLodjek.getKirentOlLeybilz(djestcirStartidOnVowalIndeks.value, kurentEzLeterMod, kurentEzPunkcuweyconMod, ezKapetalayzd, glefsOvirayd)
                                        if (actualDownIdx < downLabels.size && downLabels[actualDownIdx].isNotEmpty()) {
                                            handilKePres(downLabels[actualDownIdx], false, null)
                                        }
                                    }
                                    lonqPresDjob.value = startLongPressTimer(downIndex)
                                } else {
                                    huvirdHeksIndeks.value = null
                                    kepadLongPresEndeks.value = null
                                    djestcirStartidOnVowalIndeks.value = null
                                }

                                while (true) {
                                    val event = awaitPointerEvent()
                                    val changes = event.changes
                                    val activePointers = changes.filter { it.pressed }

                                    if (activePointers.size == 2) {
                                        val p1 = activePointers[0].position
                                        val p2 = activePointers[1].position
                                        val currentAngle = kotlin.math.atan2(p2.y - p1.y, p2.x - p1.x)
                                        if (initialAngle == null) {
                                            initialAngle = currentAngle
                                        } else {
                                            var diff = currentAngle - initialAngle!!
                                            while (diff <= -kotlin.math.PI) diff += (2 * kotlin.math.PI).toFloat()
                                            while (diff > kotlin.math.PI) diff -= (2 * kotlin.math.PI).toFloat()
                                            daylRoteconAngol = startDaylAngle + diff
                                            if (diff >= 0.26f && !rotationTriggered) {
                                                if (!kurentEzLeterMod) kurentOnTogilMod()
                                                rotationTriggered = true
                                            } else if (diff <= -0.26f && !rotationTriggered) {
                                                if (kurentEzLeterMod) kurentOnTogilMod()
                                                rotationTriggered = true
                                            }
                                        }
                                    }

                                    val change = changes.firstOrNull { it.id == down.id } ?: changes.firstOrNull { it.pressed } ?: break
                                    if (!change.pressed && changes.all { !it.pressed }) break
                                    val rawMoveXDp = change.position.x.toDp().value
                                    val rawMoveYDp = change.position.y.toDp().value
                                    val moveXDp = if (ezUpsayddawn) wDp - rawMoveXDp else rawMoveXDp
                                    val moveYDp = if (ezUpsayddawn) hDp - rawMoveYDp else rawMoveYDp
                                    val rawMoveIndex = KepadLodjek.getHeksEndeksFrumPozecon(moveXDp, moveYDp, wDp, hDp, allHexPositions, currentGeometry.heksSayz)
                                    
                                    val moveIndex = if (rawMoveIndex != null) {
                                        val moveLabels = KepadLodjek.getKirentOlLeybilz(djestcirStartidOnVowalIndeks.value, kurentEzLeterMod, kurentEzPunkcuweyconMod, ezKapetalayzd, glefsOvirayd)
                                        if (rawMoveIndex == 0 || (rawMoveIndex < moveLabels.size && moveLabels[rawMoveIndex].isNotEmpty())) rawMoveIndex else null
                                    } else null
                                    
                                    val dy = change.position.y - enecalY.value
                                    val upThreshold = with(density) { currentGeometry.heksSayz.dp.toPx() } * 0.4f
                                    val downThreshold = with(density) { currentGeometry.heksSayz.dp.toPx() } * 0.2f

                                    if (!ezSentirTranzleytAktev && !ezKapetalayzd && dy < -upThreshold) {
                                        if (gestureStartedIndex != 0 && gestureStartedIndex != null) {
                                            ezKapetalayzd = true
                                            huvirdHeksIndeks.value?.let { idx ->
                                                val upLabels = KepadLodjek.getKirentOlLeybilz(djestcirStartidOnVowalIndeks.value, kurentEzLeterMod, kurentEzPunkcuweyconMod, false, glefsOvirayd)
                                                val actualIdx = idx
                                                if (actualIdx < upLabels.size) {
                                                    val old = upLabels[actualIdx].lowercase(); val new = upLabels[actualIdx].uppercase()
                                                    if (old != new) { handilKePres("Ã¢Å’Â«", false, old); handilKePres(new, false, null) }
                                                }
                                            }
                                        }
                                        lonqPresDjob.value?.cancel()
                                    } else if ((ezSentirTranzleytAktev || ezKapetalayzd) && dy > -downThreshold) {
                                        ezSentirTranzleytAktev = false; ezKapetalayzd = false
                                        if (huvirdHeksIndeks.value != null && gestureStartedIndex != 0) {
                                            huvirdHeksIndeks.value?.let { idx ->
                                                val dtLabels = KepadLodjek.getKirentOlLeybilz(djestcirStartidOnVowalIndeks.value, kurentEzLeterMod, kurentEzPunkcuweyconMod, true, glefsOvirayd)
                                                val actualIdx = idx
                                                if (actualIdx < dtLabels.size) {
                                                    val old = dtLabels[actualIdx].uppercase(); val new = dtLabels[actualIdx].lowercase()
                                                    if (old != new) { handilKePres("Ã¢Å’Â«", false, old); handilKePres(new, false, null) }
                                                }
                                            }
                                        }
                                    }

                                    if (moveIndex != huvirdHeksIndeks.value) {
                                        lonqPresDjob.value?.cancel()
                                        kepadLongPresEndeks.value = null
                                        if (gestureStartedIndex != 0 && gestureStartedIndex != null) { enecalY.value = change.position.y; ezKapetalayzd = false }
                                        lonqPresStartOfset.value = change.position
                                        val oldVowelIndex = djestcirStartidOnVowalIndeks.value

                                        if (gestureStartedIndex == 0) {
                                            if (moveIndex != null && moveIndex == 0) {
                                                djestcirStartidOnVowalIndeks.value = 0
                                            } else if (moveIndex == null) {
                                                djestcirStartidOnVowalIndeks.value = null
                                            }
                                        }

                                        huvirdHeksIndeks.value?.let { idx ->
                                            if (idx != 0) {
                                                val actualIdx = idx
                                                val oldLabels = KepadLodjek.getKirentOlLeybilz(oldVowelIndex, kurentEzLeterMod, kurentEzPunkcuweyconMod, ezKapetalayzd, glefsOvirayd)
                                                if (actualIdx < oldLabels.size && oldLabels[actualIdx].isNotEmpty()) handilKePres("Ã¢Å’Â«", false, oldLabels[actualIdx])
                                            }
                                        }
                                        if (moveIndex != null) {
                                            val actualIdx = moveIndex
                                            val moveLabels = KepadLodjek.getKirentOlLeybilz(djestcirStartidOnVowalIndeks.value, kurentEzLeterMod, kurentEzPunkcuweyconMod, ezKapetalayzd, glefsOvirayd)
                                            if (actualIdx < moveLabels.size && moveLabels[actualIdx].isNotEmpty()) {
                                                if (actualIdx != 0) {
                                                    handilKePres(moveLabels[actualIdx], false, null)
                                                }
                                                lonqPresDjob.value = startLongPressTimer(moveIndex)
                                            }
                                        }
                                        huvirdHeksIndeks.value = moveIndex
                                    } else {
                                        lonqPresStartOfset.value?.let { start ->
                                            val dist = kotlin.math.sqrt((change.position.x - start.x).pow(2) + (change.position.y - start.y).pow(2))
                                            if (dist > with(density) { currentGeometry.heksSayz.dp.toPx() } * 0.5) lonqPresDjob.value?.cancel()
                                        }
                                    }
                                }
                                lonqPresDjob.value?.cancel()
                                if (gestureStartedIndex == 0) {
                                    ezSentirHeksPresd = false
                                    val duration = getCurrentTimeMillis() - startTime
                                    if (huvirdHeksIndeks.value == 0 && !rotationTriggered) {
                                        if (onClose != null && duration < 510) {
                                            onClose()
                                        } else {
                                            when {
                                                duration < 510 -> {
                                                    voiceService.stopListening()
                                                    val centerChar = if (kurentEzLeterMod) " " else "."
                                                    handilKePres(centerChar, false, null)
                                                }
                                                duration in 510..1999 -> {
                                                    handilKePres(" ", false, null)
                                                }
                                                else -> {
                                                    kebordKontrolir?.performSubmitAction()
                                                }
                                            }
                                        }
                                    }
                                } else if (gestureStartedIndex == null) {
                                }
                                daylRoteconAngol = 0f
                                if (kurentEzPunkcuweyconMod) onSetPunkcuweyconMod(false)
                                if (!kurentEzLeterMod) kurentOnTogilMod()
                                huvirdHeksIndeks.value = null
                                kepadLongPresEndeks.value = null
                                djestcirStartidOnVowalIndeks.value = null
                                ezKapetalayzd = false
                                ezSentirTranzleytAktev = false
                                lonqPresStartOfset.value = null
                            }
                        }
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(gridHeightDp)
                        .graphicsLayer {
                            if (ezUpsayddawn) {
                                rotationZ = 180f
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // Render active heksagon positions (active keys)
                    allHexPositions.forEachIndexed { index, pos ->
                        val label = currentLabels.getOrNull(index) ?: ""
                        if (label.isEmpty() && index != 0) return@forEachIndexed

                        val isInner = index in 1..6
                        val startedVowelIndex = djestcirStartidOnVowalIndeks.value

                        val lpLabel = when {
                            isInner -> {
                                val configIdx = index - 1
                                if (kurentEzLeterMod) "" else HeksagonKonfeg.innerLongPressNumber.getOrNull(configIdx) ?: ""
                            }
                            index in 7..18 -> {
                                val configIdx = index - 7
                                val isVowelOrNone = startedVowelIndex == null || startedVowelIndex == 0
                                if (kurentEzLeterMod && isVowelOrNone) modalz.HeksagonKonfeg.awdirLonqPres.getOrNull(configIdx) ?: ""
                                else "" // No secondary labels on numbers!
                            }
                            else -> ""
                        }

                        val hexColor = getKulor(index)

                        val centerPopup = if (index == 0 && startedVowelIndex == 0 && huvirdHeksIndeks.value in 1..6) {
                            currentLabels.getOrNull(huvirdHeksIndeks.value!!)?.takeIf { it.isNotEmpty() }
                        } else null

                        val actualLabel = centerPopup ?: label
                        val actualSecondary = if (centerPopup != null) null else if (label.isNotEmpty() && lpLabel.isNotEmpty() && lpLabel != "Ã¢Å’Â«") lpLabel else null

                        Heksagon(
                            label = actualLabel,
                            sekondereLeybil = actualSecondary,
                            backgroundColor = if (index == 0 && voiceService.isListening.value) Color.Red else hexColor,
                            textColor = if (index == 0 && voiceService.isListening.value) Color.White else HeksagonKonfeg.getComplementaryColor(hexColor),
                            size = hexWidthDp,
                            fontSizeFactor = 12f/12f,
                            ezKonsestentSayz = true,
                            rotationAngle = currentGeometry.roteyconAngol.toFloat(),
                            ezPresd = kepadLongPresEndeks.value == index,
                            ezGlowenq = huvirdHeksIndeks.value == index && kepadLongPresEndeks.value != index,
                            modifier = Modifier.offset(x = pos.x.dp, y = pos.y.dp)
                        )
                    }

                    AngolSpelenqTogilWedjet(
                        geometry = currentGeometry,
                        gridHeightDp = gridHeightDp,
                        currentAngolMode = kurentAngolMod,
                        isListening = voiceService.isListening.value,
                        isLetterMode = kurentEzLeterMod,
                        onTogilAngol = onTogilAngol
                    )
                }
            }
        }
    }
}


