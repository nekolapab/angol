package modyilz

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
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
import wedjets.AngolSpelenqTogil
import wedjets.GredItem
import modalz.HeksagonKonfeg
import modalz.HeksagonPozecon
import kotlin.math.pow
import kotlin.math.sqrt

@Composable
fun KepadModyil(
    keyboardController: KeyboardController?,
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
    glefzOverride: List<String>? = null,
    kulorzOverride: List<Long>? = null,
    contentWidthDp: androidx.compose.ui.unit.Dp? = null,
    isEditing: Boolean = false,
    onMove: (Int, Int) -> Unit = { _, _ -> },
    onCopyToEmpty: (Int, Int) -> Unit = { _, _ -> },
    onMoveToCenter: (Int) -> Unit = { _ -> },
    onDropOnFoldir: (Int, Int) -> Unit = { _, _ -> },
    onDelete: (Int) -> Unit = { _ -> },
    onReplace: ((Int, Int) -> Unit)? = null
) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    
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
        if (kulorzOverride != null && index < kulorzOverride.size) {
            val c = Color(kulorzOverride[index].toInt())
            if (c != Color.Transparent) return c
        }
        return when {
            index == 0 -> Color.White
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

        val controller = keyboardController ?: return

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

        if (char == "⌫") {
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
            
            if (index == 0) { // Center
                if (kurentEzLeterMod) {
                    handilKePres(".", false, null)
                }
                delay(1500)
                if (huvirdHeksIndeks.value != 0) return@launch
                if (kurentEzLeterMod) {
                    keyboardController?.finishComposingText()
                    handilKePres("⌫", false, null)
                }
                return@launch
            }
            
            // Map index back to config logic
            val actualIdx = index

            val isInner = actualIdx in 1..6
            val startedVowelIndex = djestcirStartidOnVowalIndeks.value
            
            val lpLabelRaw = if (isInner) {
                val configIdx = actualIdx - 1
                if (kurentEzLeterMod) HeksagonKonfeg.innerLetterMode.map { if (it == "⌫") "⌫" else "" }.getOrNull(configIdx) ?: ""
                else HeksagonKonfeg.innerLongPressNumber.getOrNull(configIdx) ?: ""
            } else {
                val configIdx = actualIdx - 7
                if (kurentEzLeterMod && startedVowelIndex == null) HeksagonKonfeg.outerLongPress.getOrNull(configIdx) ?: ""
                else HeksagonKonfeg.outerLongPressNumber.getOrNull(configIdx) ?: ""
            }
            if (lpLabelRaw.isEmpty()) return@launch
            val lpLabel = if (ezKapetalayzd && lpLabelRaw != "⌫") lpLabelRaw.uppercase() else lpLabelRaw
            val primaryLabel = KepadLodjek.getCurrentOlLeybelz(startedVowelIndex, kurentEzLeterMod, kurentEzPunkcuweyconMod, ezKapetalayzd, glefzOverride).getOrNull(actualIdx) ?: ""
            if (lpLabel == "⌫") {
                while (huvirdHeksIndeks.value == index) {
                    handilKePres("⌫", true, null)
                    delay(500)
                }
            } else { handilKePres(lpLabel, true, primaryLabel) }
        }
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        val maxWidthDp = maxWidth
        val currentGeometry = remember(maxWidthDp, geometryOverride, daylRoteconAngol, kurentEzLeterMod) {
            if (geometryOverride != null) {
                geometryOverride
            } else {
                val maxIdx = glefzOverride?.mapIndexedNotNull { i, s -> if (s.isNotEmpty()) i else null }?.maxOrNull() ?: 0
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

        val allHexPositions = remember(currentGeometry, glefzOverride) {
            val maxIdx = glefzOverride?.mapIndexedNotNull { i, s -> if (s.isNotEmpty()) i else null }?.maxOrNull() ?: 0
            val rings = if (maxIdx > 36) 4 else if (maxIdx > 18) 3 else 2
            val totalHexCount = 3 * rings * (rings + 1) + 1
            (0 until totalHexCount).map { idx ->
                val axial = currentGeometry.indeksTuAksyal(idx)
                currentGeometry.aksyalTuPeksel(axial.q, axial.r)
            }
        }

        val gridHeightDp = maxHeight

        if (isEditing) {
            val currentLabels = KepadLodjek.getCurrentOlLeybelz(null, kurentEzLeterMod, kurentEzPunkcuweyconMod, ezKapetalayzd, glefzOverride)
            val gredItems = currentLabels.mapIndexed { index, label ->
                if (index == 0 || label.isEmpty()) return@mapIndexed null
                GredItem(
                    index = index,
                    label = label,
                    color = getKulor(index)
                )
            }.filterNotNull()

            val centerLabel = currentLabels.getOrNull(0) ?: " "

            wedjets.HeksagonGred(
                geometry = currentGeometry,
                items = gredItems,
                centerLabel = centerLabel,
                centerColor = getKulor(0),
                onMove = onMove,
                onCopyToEmpty = onCopyToEmpty,
                onMoveToCenter = onMoveToCenter,
                onDropOnFoldir = onDropOnFoldir,
                onReplace = onReplace,
                onDelete = onDelete,
                onTap = { index -> if (index == 0) onClose?.invoke() },
                fontSizeFactor = 13f/12f
            )
        } else {
            Box(modifier = Modifier.fillMaxWidth().height(gridHeightDp), contentAlignment = Alignment.Center) {
                val hexWidthDp = currentGeometry.heksWidlx.dp
                val currentLabels = KepadLodjek.getCurrentOlLeybelz(djestcirStartidOnVowalIndeks.value, kurentEzLeterMod, kurentEzPunkcuweyconMod, ezKapetalayzd, glefzOverride)

                Box(
                    modifier = Modifier.fillMaxSize()
                        .pointerInput(allHexPositions, ezUpsayddawn) {
                            val wDp = size.width.toDp().value
                            val hDp = size.height.toDp().value
                            awaitEachGesture {
                                val down = awaitFirstDown(requireUnconsumed = false)
                                val startTime = getCurrentTimeMillis()
                                val rawXDp = down.position.x.toDp().value
                                val rawYDp = down.position.y.toDp().value
                                val xDp = if (ezUpsayddawn) wDp - rawXDp else rawXDp
                                val yDp = if (ezUpsayddawn) hDp - rawYDp else rawYDp
                                val downIndex = KepadLodjek.getHeksIndeksFromPozecon(xDp, yDp, wDp, hDp, allHexPositions, currentGeometry.heksSayz)
                                val gestureStartedIndex = downIndex
                                enecalY.value = down.position.y
                                lonqPresStartOfset.value = down.position
                                ezKapetalayzd = false
                                ezSentirTranzleytAktev = false
                                var rotationTriggered = false
                                var initialAngle: Float? = null
                                val startDaylAngle = daylRoteconAngol

                                if (downIndex != null) {
                                    huvirdHeksIndeks.value = downIndex
                                    kepadLongPresEndeks.value = null
                                    val actualDownIdx = downIndex

                                    if (actualDownIdx in 1..5 && kurentEzLeterMod && !kurentEzPunkcuweyconMod) {
                                        djestcirStartidOnVowalIndeks.value = actualDownIdx
                                    } else {
                                        djestcirStartidOnVowalIndeks.value = null
                                    }

                                    if (actualDownIdx == 0) {
                                        ezSentirHeksPresd = true
                                        voiceService.startListening()
                                    }
                                    if (actualDownIdx != 0) {
                                        val downLabels = KepadLodjek.getCurrentOlLeybelz(djestcirStartidOnVowalIndeks.value, kurentEzLeterMod, kurentEzPunkcuweyconMod, ezKapetalayzd, glefzOverride)
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
                                    val moveIndex = KepadLodjek.getHeksIndeksFromPozecon(moveXDp, moveYDp, wDp, hDp, allHexPositions, currentGeometry.heksSayz)
                                    val dy = change.position.y - enecalY.value
                                    val upThreshold = with(density) { currentGeometry.heksSayz.dp.toPx() } * 0.4f
                                    val downThreshold = with(density) { currentGeometry.heksSayz.dp.toPx() } * 0.2f

                                    if (!ezSentirTranzleytAktev && !ezKapetalayzd && dy < -upThreshold) {
                                        if (gestureStartedIndex != 0 && gestureStartedIndex != null) {
                                            ezKapetalayzd = true
                                            huvirdHeksIndeks.value?.let { idx ->
                                                val upLabels = KepadLodjek.getCurrentOlLeybelz(djestcirStartidOnVowalIndeks.value, kurentEzLeterMod, kurentEzPunkcuweyconMod, false, glefzOverride)
                                                val actualIdx = idx
                                                if (actualIdx < upLabels.size) {
                                                    val old = upLabels[actualIdx].lowercase(); val new = upLabels[actualIdx].uppercase()
                                                    if (old != new) { handilKePres("⌫", false, old); handilKePres(new, false, null) }
                                                }
                                            }
                                        }
                                        lonqPresDjob.value?.cancel()
                                    } else if ((ezSentirTranzleytAktev || ezKapetalayzd) && dy > -downThreshold) {
                                        ezSentirTranzleytAktev = false; ezKapetalayzd = false
                                        if (huvirdHeksIndeks.value != null && gestureStartedIndex != 0) {
                                            huvirdHeksIndeks.value?.let { idx ->
                                                val dtLabels = KepadLodjek.getCurrentOlLeybelz(djestcirStartidOnVowalIndeks.value, kurentEzLeterMod, kurentEzPunkcuweyconMod, true, glefzOverride)
                                                val actualIdx = idx
                                                if (actualIdx < dtLabels.size) {
                                                    val old = dtLabels[actualIdx].uppercase(); val new = dtLabels[actualIdx].lowercase()
                                                    if (old != new) { handilKePres("⌫", false, old); handilKePres(new, false, null) }
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

                                        if (gestureStartedIndex == 0 && moveIndex != 0) {
                                            ezSentirHeksPresd = false
                                        }

                                        val actualMoveIdx = moveIndex

                                        if (gestureStartedIndex == 0) {
                                            if (actualMoveIdx != null && actualMoveIdx in 1..5) {
                                                djestcirStartidOnVowalIndeks.value = actualMoveIdx
                                            } else if (actualMoveIdx == 0) {
                                                djestcirStartidOnVowalIndeks.value = null
                                            } else if (moveIndex == null) {
                                                djestcirStartidOnVowalIndeks.value = null
                                            }
                                        }

                                        huvirdHeksIndeks.value?.let { idx ->
                                            if (idx != 0) {
                                                val actualIdx = idx
                                                val oldLabels = KepadLodjek.getCurrentOlLeybelz(oldVowelIndex, kurentEzLeterMod, kurentEzPunkcuweyconMod, ezKapetalayzd, glefzOverride)
                                                if (actualIdx < oldLabels.size && oldLabels[actualIdx].isNotEmpty()) handilKePres("⌫", false, oldLabels[actualIdx])
                                            }
                                        }
                                        if (moveIndex != null) {
                                            val actualIdx = moveIndex
                                            val moveLabels = KepadLodjek.getCurrentOlLeybelz(djestcirStartidOnVowalIndeks.value, kurentEzLeterMod, kurentEzPunkcuweyconMod, ezKapetalayzd, glefzOverride)
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
                                        // Center tap: Go up directory (Close)
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
                                                    keyboardController?.performSubmitAction()
                                                }
                                            }
                                        }
                                    }
                                } else if (gestureStartedIndex == null) {
                                    // Tapping outside does NOTHING (Deleted 'onClose' here)
                                }
                                daylRoteconAngol = 0f
                                if (kurentEzPunkcuweyconMod) onSetPunkcuweyconMod(false)
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
                    // Render ALL heksagon positions (active keys + background rings)
                    allHexPositions.forEachIndexed { index, pos ->
                        val label = currentLabels.getOrNull(index) ?: ""

                        val isInner = index in 1..6
                        val startedVowelIndex = djestcirStartidOnVowalIndeks.value

                        val lpLabel = when {
                            isInner -> {
                                val configIdx = index - 1
                                if (kurentEzLeterMod) "" else HeksagonKonfeg.innerLongPressNumber.getOrNull(configIdx) ?: ""
                            }
                            index in 7..18 -> {
                                val configIdx = index - 7
                                if (kurentEzLeterMod && startedVowelIndex == null) HeksagonKonfeg.outerLongPress.getOrNull(configIdx) ?: ""
                                else HeksagonKonfeg.outerLongPressNumber.getOrNull(configIdx) ?: ""
                            }
                            else -> ""
                        }

                        val hexColor = getKulor(index)

                        Heksagon(
                            label = label,
                            secondaryLabel = if (label.isNotEmpty() && lpLabel.isNotEmpty() && lpLabel != "⌫") lpLabel else null,
                            backgroundColor = if (index == 0 && voiceService.isListening.value) Color.Red else hexColor,
                            textColor = if (index == 0 && voiceService.isListening.value) Color.White else HeksagonKonfeg.getComplementaryColor(hexColor),
                            size = hexWidthDp,
                            fontSizeFactor = 13f/12f,
                            ezKonsestentSayz = true,
                            rotationAngle = currentGeometry.roteyconAngol.toFloat(),
                            ezPresd = kepadLongPresEndeks.value == index,
                            ezGlowenq = huvirdHeksIndeks.value == index && kepadLongPresEndeks.value != index,
                            modifier = Modifier.offset(x = pos.x.dp, y = pos.y.dp)
                        )
                    }

                    AngolSpelenqTogil(
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
