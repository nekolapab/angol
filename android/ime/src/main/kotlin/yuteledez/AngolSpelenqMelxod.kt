package yuteledez

/**
 * Utility class for phonetic conversion of English text to 'Angol' spelling.
 * This provides a robust, rule-based transformation engine.
 *
 * Vowel Phonetic Mapping (Reference for Orthography):
 * - a1: /ɑ/ (pasta) -> a     - i6: /ɝ/ (her)   -> ir    - o0: /oʊ/ (go)  -> o
 * - a2: /æ/ (cat)   -> a     - i7: /ʊ/ (book)  -> i     - oA: /o/ (beau)  -> o
 * - e3: /ɛ/ (bed)   -> e                              - oO: /ɔ/ (all)   -> o
 * - e4: /ɪ/ (bit)   -> e     - u8: /ʌ/ (but)   -> u
 * - e5: /i/ (keep)  -> e     - u9: /u/ (too)   -> u
 */
object AngolSpelenqMelxod {
    private val replacements = mapOf(
        "the" to "lha",
        "to" to "tu",
        "application" to "aplekeycon",
        "information" to "enformeycon",
        "service" to "sirves",
        "input" to "enpit",
        "method" to "melxod",
        "voice" to "voys",
        "text" to "tekst",
        "typing" to "taypenq",
        "spelling" to "spelenq",
        "perfect" to "pirfekt",
        "work" to "wirk",
        "button" to "buton",
        "number" to "numbir",
        "letter" to "ledir",
        "center" to "sentir",
        "circle" to "sirkol",
        "inner" to "enir",
        "outer" to "awdir",
        "keyboard" to "kepad",
        "this" to "lhes",
        "with" to "welx",
        "have" to "hav",
        "been" to "bin",
        "was" to "waz",
        "does" to "duz",
        "doesn't" to "duznt",
        "nothing" to "naixenq",
        "through" to "lru",
        "all" to "ol",
        "mode" to "mod",
        "photo" to "fowto",
        "when" to "wen",
        "well" to "wel",
        "she" to "ci",
        "he" to "hi",
        "me" to "mi",
        "be" to "bi",
        "we" to "wi"
    )

    fun convertToAngolSpelling(text: String): String {
        if (text.isBlank()) return text
        
        // Split while preserving delimiters (whitespace/punctuation)
        val regex = Regex("(\\s+|[^a-zA-Z\\s]+)")
        val tokens = mutableListOf<String>()
        var lastEnd = 0
        regex.findAll(text).forEach { match ->
            if (match.range.first > lastEnd) {
                tokens.add(text.substring(lastEnd, match.range.first))
            }
            tokens.add(match.value)
            lastEnd = match.range.last + 1
        }
        if (lastEnd < text.length) {
            tokens.add(text.substring(lastEnd))
        }

        val convertedTokens = tokens.map { token ->
            if (token.any { it.isLetter() }) {
                val cleanWord = token.lowercase()
                var result = replacements[cleanWord] ?: transformWord(cleanWord)
                capitalize(token, result)
            } else {
                token
            }
        }
        
        return convertedTokens.joinToString("")
    }

    private fun transformWord(word: String): String {
        var result = word

        // 0. Handle known roots (like work in working)
        for ((root, replacement) in replacements) {
            if (root.length > 3 && word.startsWith(root) && (word.endsWith("ing") || word.endsWith("s") || word.endsWith("ed"))) {
                result = replacement + word.substring(root.length)
                break
            }
        }

        // 1. Specific clusters first
        result = result.replace("ch", "tc")
        result = result.replace("sh", "c")
        result = result.replace("ck", "k")
        result = result.replace("ph", "f")
        result = result.replace("wh", "w")
        result = result.replace(Regex("^th"), "lh")
        result = result.replace("th", "lx")

        // 2. Suffixes (using placeholders for 'c' in 'con')
        result = result.replace(Regex("tion$"), "C_ON")
        result = result.replace(Regex("sion$"), "C_ON")
        result = result.replace(Regex("ing$"), "enq")

        // 3. Vowel Shifts (Long vowels)
        result = result.replace(Regex("a([bcdfghjklmnpqrstvwxyz])e$"), "EY$1")
        result = result.replace(Regex("i([bcdfghjklmnpqrstvwxyz])e$"), "AY$1")
        
        // 4. General Consonants
        result = result.replace("tc", "T_C")
        result = result.replace(Regex("c(?=[eiy])"), "s") // soft c
        result = result.replace(Regex("c(?![eiy])"), "k") // hard c
        result = result.replace(Regex("(?i)j|(?<=^|[^aeiou])g(?=[eiy])"), "dj") // j or soft g
        result = result.replace("T_C", "tc")

        // 5. Short vowels and common combinations
        result = result.replace("is", "ez")
        result = result.replace("it", "et")
        
        result = result
            .replace("ee", "e")
            .replace("oo", "uw")
            .replace("ai", "ey")
            .replace("ay", "ey")
            
        // 6. Restore placeholders
        result = result
            .replace("C_ON", "con")
            .replace("EY", "ey")
            .replace("AY", "ay")

        return result
    }

    private fun capitalize(original: String, replacement: String): String {
        return when {
            original.all { it.isUpperCase() } -> replacement.uppercase()
            original.firstOrNull()?.isUpperCase() == true -> replacement.replaceFirstChar { it.uppercase() }
            else -> replacement
        }
    }
}
