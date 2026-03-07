package yuteledez

import org.junit.Assert.assertEquals
import org.junit.Test

class AngolSpelenqMelxodTest {

    private fun assertConversion(expected: String, input: String) {
        val actual = AngolSpelenqMelxod.convertToAngolSpelling(input)
        if (expected != actual) {
            println("FAILED: Input: '$input', Expected: '$expected', Actual: '$actual'")
        }
        assertEquals(expected, actual)
    }

    @Test
    fun testCoreVocabulary() {
        assertConversion("lha", "the")
        assertConversion("tu", "to")
        assertConversion("enpit", "input")
        assertConversion("melxod", "method")
    }

    @Test
    fun testSuffixes() {
        assertConversion("pleyenq", "playing")
        assertConversion("akcon", "action")
        assertConversion("enformeycon", "information")
    }

    @Test
    fun testConsonants() {
        assertConversion("tcek", "check")
        assertConversion("ci", "she")
        assertConversion("welx", "with")
        assertConversion("fowto", "photo")
        assertConversion("wen", "when")
    }

    @Test
    fun testVowels() {
        assertConversion("et", "it")
        assertConversion("ez", "is")
        assertConversion("meyk", "make")
        assertConversion("beys", "base")
        assertConversion("layk", "like")
        assertConversion("taym", "time")
    }

    @Test
    fun testCapitalization() {
        assertConversion("Lha", "The")
        assertConversion("ENPIT", "INPUT")
    }

    @Test
    fun testSentences() {
        assertConversion("lha aplekeycon ez wirkenq wel.", "the application is working well.")
    }
}
