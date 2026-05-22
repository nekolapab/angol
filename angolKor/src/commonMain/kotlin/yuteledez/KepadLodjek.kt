package yuteledez

import modalz.HeksagonPozecon
import modalz.KepadKonfeg
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

    fun getCurrentOlLeybelz(
        vowelIndex: Int?,
        isLetterMode: Boolean,
        isPunctuationMode: Boolean,
        isCapitalized: Boolean,
        glefzOverride: List<String>?
    ): List<String> {
        val baseLabels = if (glefzOverride != null) {
            // Use custom labels if provided
            if (vowelIndex != null && vowelIndex in 1..5 && isLetterMode && !isPunctuationMode) {
                val expanded = MutableList(37) { "" }
                when (vowelIndex) {
                    1 -> { expanded[18] = "1"; expanded[7] = "2" }
                    2 -> { expanded[8] = "3"; expanded[9] = "5" }
                    3 -> { expanded[10] = "4"; expanded[11] = "6"; expanded[12] = "7" }
                    4 -> { expanded[13] = "0"; expanded[14] = "A"; expanded[15] = "O" }
                    5 -> { expanded[16] = "8"; expanded[17] = "9" }
                }
                return expanded
            } else {
                if (isCapitalized) glefzOverride.map { it.uppercase() } else glefzOverride
            }
        } else {
            val center = if (isLetterMode) " " else "."
            val inner = when {
                vowelIndex == 18 -> KepadKonfeg.innerPunctuationMode
                isPunctuationMode -> KepadKonfeg.innerPunctuationMode
                isLetterMode -> KepadKonfeg.innerLetterMode
                else -> KepadKonfeg.innerNumberMode
            }
            val outer = if (vowelIndex != null && vowelIndex in 1..5 && isLetterMode) {
                val expanded = MutableList(37) { "" }
                KepadKonfeg.innerLetterMode.forEachIndexed { i, s -> if (i < 5) expanded[i + 1] = s }

                when (vowelIndex) {
                    1 -> { expanded[18] = "1"; expanded[7] = "2" }
                    2 -> { expanded[8] = "3"; expanded[9] = "4"; expanded[10] = "5" }
                    3 -> { expanded[11] = "6"; expanded[12] = "7" }
                    4 -> { expanded[13] = "8"; expanded[14] = "9" }
                    5 -> { expanded[15] = "0"; expanded[16] = "A"; expanded[17] = "O" }
                }
                return if (isCapitalized) expanded.map { it.uppercase() } else expanded
            } else if (isLetterMode) KepadKonfeg.outerTap else KepadKonfeg.outerTapNumber
            
            val labels = listOf(center) + inner + outer
            if (isCapitalized) labels.map { it.uppercase() } else labels
        }
        
        // Ensure index 0 has a center label if using override
        val finalLabels = baseLabels.toMutableList()
        if (glefzOverride != null && finalLabels.isEmpty()) finalLabels.add(" ")
        
        // Dynamic padding: at least 19 (2 rings), or 37 (3 rings) if needed
        val maxIdx = finalLabels.mapIndexedNotNull { i, s -> if (s.isNotEmpty()) i else null }.maxOrNull() ?: 0
        val targetSize = if (maxIdx > 18) 37 else 19
        
        if (finalLabels.size > targetSize) {
             return finalLabels.subList(0, targetSize)
        }
        while (finalLabels.size < targetSize) finalLabels.add("")
        return finalLabels
    }
}
