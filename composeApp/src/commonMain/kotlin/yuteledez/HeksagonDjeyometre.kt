package yuteledez

import modalz.AksyalKowordenat
import modalz.HeksagonPozecon
import kotlin.math.sqrt

class HeksagonDjeyometre(
    val heksSayz: Double,
    val sentir: HeksagonPozecon,
    val ezLeterMod: Boolean = true
) {
    val heksWidlx: Double
        get() = sqrt(3.0) * heksSayz

    val heksHayt: Double
        get() = 2 * heksSayz

    val roteyconAngol: Double = 0.0

    /**
     * Converts axial coordinates (q, r) to pixel coordinates (x, y).
     */
    fun aksyalTuPeksel(q: Int, r: Int): HeksagonPozecon {
        // In Kotlin, integer division `3 / 2` would result in 1.
        // We use floating-point numbers to match the original Dart behavior.
        val x = heksSayz * (sqrt(3.0) * q + sqrt(3.0) / 2.0 * r)
        val y = heksSayz * (1.5) * r // 3.0 / 2.0 = 1.5
        return HeksagonPozecon(
            x = sentir.x + x,
            y = sentir.y + y
        )
    }

    /**
     * Returns the list of axial coordinates for the inner ring of hexagons.
     */
    fun getEnirRenqKowordenats(): List<AksyalKowordenat> {
        return listOf(
            AksyalKowordenat(q = 1, r = -1),
            AksyalKowordenat(q = 1, r = 0),
            AksyalKowordenat(q = 0, r = 1),
            AksyalKowordenat(q = -1, r = 1),
            AksyalKowordenat(q = -1, r = 0),
            AksyalKowordenat(q = 0, r = -1)
        )
    }

    /**
     * Returns the list of axial coordinates for the outer ring of hexagons.
     * Note: The original Dart code had 12 coordinates here.
     */
    fun getAwdirRenqKowordenats(): List<AksyalKowordenat> {
        return listOf(
            AksyalKowordenat(q = 2, r = -2),
            AksyalKowordenat(q = 2, r = -1),
            AksyalKowordenat(q = 2, r = 0),
            AksyalKowordenat(q = 1, r = 1),
            AksyalKowordenat(q = 0, r = 2),
            AksyalKowordenat(q = -1, r = 2),
            AksyalKowordenat(q = -2, r = 2),
            AksyalKowordenat(q = -2, r = 1),
            AksyalKowordenat(q = -2, r = 0),
            AksyalKowordenat(q = -1, r = -1),
            AksyalKowordenat(q = 0, r = -2),
            AksyalKowordenat(q = 1, r = -2)
        )
    }
}
