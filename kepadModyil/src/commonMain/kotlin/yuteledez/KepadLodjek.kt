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

    fun getHeksIndeksFromPozecon(offsetX: Float, offsetY: Float, w: Float, h: Float, allHexPositions: List<HeksagonPozecon>, hexSize: Double): Int? {
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
            val expanded = MutableList(37) { "" }
            glefsOvirayd.forEachIndexed { i, s -> if (i < expanded.size) expanded[i] = s }
            if (vowelIndex != null && vowelIndex in 1..5 && isLetterMode) {
                // Clear the outer ring (7..18)
                for (i in 7..18) expanded[i] = " "
                when (vowelIndex) {
                    1 -> { expanded[18] = "1"; expanded[7] = "2" }
                    2 -> { expanded[8] = "3"; expanded[9] = "4"; expanded[10] = "5" }
                    3 -> { expanded[11] = "6"; expanded[12] = "7" }
                    4 -> { expanded[13] = "8"; expanded[14] = "9" }
                    5 -> { expanded[15] = "0"; expanded[16] = "A"; expanded[17] = "O" }
                }
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
                val outerNumbers = MutableList(12) { " " }
                when (vowelIndex) {
                    1 -> { outerNumbers[11] = "1"; outerNumbers[0] = "2" } // 18 and 7
                    2 -> { outerNumbers[1] = "3"; outerNumbers[2] = "4"; outerNumbers[3] = "5" } // 8, 9, 10
                    3 -> { outerNumbers[4] = "6"; outerNumbers[5] = "7" } // 11, 12
                    4 -> { outerNumbers[6] = "8"; outerNumbers[7] = "9" } // 13, 14
                    5 -> { outerNumbers[8] = "0"; outerNumbers[9] = "A"; outerNumbers[10] = "O" } // 15, 16, 17
                }
                outerNumbers
            } else if (isLetterMode) modalz.HeksagonKonfeg.outerTap else modalz.HeksagonKonfeg.outerTapNumber
            
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
