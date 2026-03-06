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

        // 0. Preliminary cleanup
        result = result.replace("wh", "w")
        result = result.replace("kn", "n")
        result = result.replace("gn", "n")
        result = result.replace("wr", "r")

        // 1. Specific clusters first (ordered to prevent double-conversion)
        result = result.replace("tch", "tc")
        result = result.replace("ch", "tc")
        result = result.replace("sh", "c")
        result = result.replace("ck", "k")
        result = result.replace("ph", "f")
        result = result.replace("qu", "kw")
        result = result.replace(Regex("^th"), "lh")
        result = result.replace("th", "lx")

        // 2. Suffixes
        result = result.replace(Regex("tion$"), "con")
        result = result.replace(Regex("sion$"), "con")
        result = result.replace(Regex("ing$"), "enq")
        result = result.replace(Regex("ought$"), "ot")
        result = result.replace(Regex("aught$"), "ot")

        // 3. Vowel Shifts (Long vowels with silent 'e')
        result = result.replace(Regex("a([bcdfghjklmnpqrstvwxyz])e$"), "ey$1")
        result = result.replace(Regex("i([bcdfghjklmnpqrstvwxyz])e$"), "ay$1")
        result = result.replace(Regex("o([bcdfghjklmnpqrstvwxyz])e$"), "o$1")
        result = result.replace(Regex("u([bcdfghjklmnpqrstvwxyz])e$"), "uw$1")
        
        // 4. General Consonants
        result = result.replace(Regex("c(?=[eiy])"), "s") // soft c
        result = result.replace(Regex("c(?![eiy])"), "k") // hard c
        result = result.replace(Regex("(?i)j|(?<=^|[^aeiou])g(?=[eiy])"), "dj") // j or soft g
        result = result.replace("x", "ks")
        result = result.replace("q", "k")

        // 5. Short vowels and common combinations
        result = result.replace("is", "ez")
        result = result.replace("it", "et")
        result = result.replace("ee", "e")
        result = result.replace("oo", "uw")
        result = result.replace("ea", "e")
        result = result.replace("ai", "ey")
        result = result.replace("ay", "ey")
        result = result.replace("ie", "e")
        result = result.replace("oa", "o")
        
        // 6. Double consonants -> single
        val sb = StringBuilder()
        if (result.isNotEmpty()) {
            sb.append(result[0])
            for (i in 1 until result.length) {
                if (result[i] != result[i - 1]) {
                    sb.append(result[i])
                }
            }
        }
        result = sb.toString()

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
