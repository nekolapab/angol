package yuteledez

import modalz.HeksagonPozecon
import modalz.HeksagonKonfeg
import kotlin.math.pow

object KepadLodjek {
    fun kalkyuleytDeletKawnt(text: String): Int {
        if (text.isEmpty()) return 0
        if (!text.contains(" ")) return if (text.length > 10) 12 else text.length
        var i = text.length - 1
        var count = 0
        while (i >= 0 && text[i].isWhitespace()) {
            count++; i--
        }
        var wordsFound = 0
        while (i >= 0) {
            val wordEnd = i
            while (i >= 0 && !text[i].isWhitespace()) i--
            val wordStart = i + 1
            val wordLen = wordEnd - wordStart + 1
            if (wordLen >= 5) {
                if (wordsFound == 0) count += wordLen
                break
            } else {
                count += wordLen; wordsFound++
                while (i >= 0 && text[i].isWhitespace()) {
                    count++; i--
                }
            }
        }
        return if (count > 0) count else 1
    }

    fun getHeksEndeksFrumPozecon(offsetX: Float, offsetY: Float, w: Float, h: Float, allHexPositions: List<HeksagonPozecon>, hexSize: Double): Int? {
        val localX = offsetX - w / 2
        val localY = offsetY - h / 2
        var closestIndex: Int? = null
        var minDistSq = Double.MAX_VALUE
        for (i in allHexPositions.indices) {
            val hexPos = allHexPositions[i]
            val distSq = (localX - hexPos.x).toDouble().pow(2) + (localY - hexPos.y).toDouble().pow(2)
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

    fun getKirentOlLeybilz(
        vowelIndex: Int?,
        isLetterMode: Boolean,
        isPunctuationMode: Boolean,
        isCapitalized: Boolean,
        glefsOvirayd: List<String>?
    ): List<String> {
        val usePunctuation = isPunctuationMode || vowelIndex == 0

        // 1. Base layout (Override or Default)
        val hasActualText = glefsOvirayd?.any { it.trim().isNotEmpty() } == true
        val baseLabels = if (glefsOvirayd != null && hasActualText && isLetterMode && !usePunctuation) {
            val parseLabel = { raw: String ->
                if (raw.contains("id=") && raw.contains("neym=")) {
                    raw.split("|").find { it.startsWith("neym=") }?.substringAfter("neym=") ?: raw
                } else if (raw == " ") {
                    ""
                } else {
                    raw
                }
            }
            val expanded = MutableList(37) { "" }
            glefsOvirayd.forEachIndexed { i, s -> if (i < expanded.size) expanded[i] = parseLabel(s) }
            if (vowelIndex != null && vowelIndex in 1..5 && isLetterMode) {
                // Populate the entire outer ring with outerTapNumber
                for (i in 7..18) expanded[i] = modalz.HeksagonKonfeg.outerTapNumber.getOrNull(i - 7) ?: " "
            }
            expanded
        } else {
            val center = if (isLetterMode) " " else "."
            val inner = when {
                usePunctuation -> modalz.HeksagonKonfeg.innerPunctuationMode
                isLetterMode -> modalz.HeksagonKonfeg.innerLetterMode
                else -> modalz.HeksagonKonfeg.innerNumberMode
            }
            val outer = if (vowelIndex != null && vowelIndex in 1..5 && isLetterMode) {
                modalz.HeksagonKonfeg.outerTapNumber
            } else if (!isLetterMode) modalz.HeksagonKonfeg.outerTapNumber else modalz.HeksagonKonfeg.outerTap
            
            listOf(center) + inner + outer
        }

        // 2. Minimal Padding: only pad to the actual maximum occupied index
        val finalLabels = baseLabels.toMutableList()
        val maxIdx = finalLabels.mapIndexedNotNull { i, s -> if (s.isNotEmpty()) i else null }.maxOrNull() ?: 0
        while (finalLabels.size <= maxIdx) finalLabels.add("")
        
        // 3. Capitalization and return
        return if (isCapitalized) finalLabels.map { it.uppercase() } else finalLabels
    }
}
