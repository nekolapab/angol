package yuteledez

/**
 * Pure Phonetic Sound-Stream Engine for the Angol 36-character system.
 * 100% Sound-to-Symbol mapping. No English spelling rules.
 */
object AngolSpelenqMelxod {

    fun convertToAngolSpelling(text: String): String {
        if (text.isBlank()) return text
        
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

        return tokens.joinToString("") { token ->
            if (token.any { it.isLetter() }) {
                val result = transformToPureSound(token.lowercase())
                capitalize(token, result)
            } else {
                token
            }
        }
    }

    private fun transformToPureSound(input: String): String {
        var res = input

        // Stage 1: Consonant Sounds (Direct 1:1)
        res = res.replace("tch", "tc")
        res = res.replace("ch", "tc")
        res = res.replace("sh", "c")
        res = res.replace("ck", "k")
        res = res.replace("ph", "f")
        res = res.replace("qu", "kw")
        res = res.replace(Regex("^th"), "lh")
        res = res.replace("th", "lx")
        
        // Nasal Logic: nq for nasal sound, nqg if hard g follows
        res = res.replace(Regex("ng(?=[aeiou])"), "nqg")
        res = res.replace("ng", "nq")

        // Stage 2: Vowel Sounds (Direct 1:1 Mapping to 36-char symbols)
        // 1=ah, 2=at, 3=eh, 4=it, 5=ee, 6=er, 7=put/schwa, 8=up, 9=too, 0=go, A=oh, O=all.

        // Handle common digraphs as single vowel sounds
        res = res.replace("ee", "5")
        res = res.replace("ea", "5")
        res = res.replace("oo", "9")
        res = res.replace("ai", "3") // rain -> r3n
        res = res.replace("ay", "3")
        res = res.replace("ie", "5")
        res = res.replace("oa", "0")
        res = res.replace("ou", "2") // about -> 2b2t (simplified)
        res = res.replace("ow", "2")
        res = res.replace("ir", "6")
        res = res.replace("ur", "6")
        res = res.replace("er", "6")

        // Stage 3: Consonant Polish (Hard/Soft)
        res = res.replace(Regex("c(?=[eiy])"), "s")
        res = res.replace(Regex("c(?![eiy])"), "k")
        res = res.replace(Regex("(?i)j|(?<=^|[^aeiou])g(?=[eiy])"), "j")
        res = res.replace("x", "ks")
        res = res.replace("q", "k")

        // Stage 4: Map remaining English vowels to Angol symbols
        res = res.replace("a", "2")
        res = res.replace("e", "3")
        res = res.replace("i", "4")
        res = res.replace("o", "O")
        res = res.replace("u", "8")
        
        // Stage 5: Special Acoustic Cases (The "Ear" Logic)
        // 'handle' -> h2nd7l (schwa + l)
        res = res.replace(Regex("3l$"), "7l")
        
        // Terminal 'y' as '5' (be)
        if (res.endsWith("y")) {
            res = res.substring(0, res.length - 1) + "5"
        }

        // Stage 6: Acoustic Simplification (No double sounds)
        val sb = StringBuilder()
        if (res.isNotEmpty()) {
            sb.append(res[0])
            for (i in 1 until res.length) {
                if (res[i] != res[i - 1]) sb.append(res[i])
            }
        }
        res = sb.toString()

        return res
    }

    private fun capitalize(original: String, replacement: String): String {
        return when {
            original.all { it.isUpperCase() } -> replacement.uppercase()
            original.firstOrNull()?.isUpperCase() == true -> replacement.replaceFirstChar { it.uppercase() }
            else -> replacement
        }
    }
}
