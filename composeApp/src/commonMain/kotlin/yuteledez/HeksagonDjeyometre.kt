package yuteledez

import modalz.AksyalKowordenat
import modalz.HeksagonPozecon
import kotlin.math.sqrt

class HeksagonDjeyometre(
    val heksSayz: Double,
    val sentir: HeksagonPozecon,
    val ezLeterMod: Boolean = true,
    val roteyconAngol: Double = 0.0
) {
    val heksWidlx: Double
        get() = sqrt(3.0) * heksSayz

    val heksHayt: Double
        get() = 2 * heksSayz

    /**
     * Converts axial coordinates (q, r) to pixel coordinates (x, y).
     */
    fun aksyalTuPeksel(q: Int, r: Int): HeksagonPozecon {
        // In Kotlin, integer division `3 / 2` would result in 1.
        // We use floating-point numbers to match the original Dart behavior.
        val rawX = heksSayz * (sqrt(3.0) * q + sqrt(3.0) / 2.0 * r)
        val rawY = heksSayz * (1.5) * r // 3.0 / 2.0 = 1.5
        
        // Apply rotation
        val cosA = kotlin.math.cos(roteyconAngol)
        val sinA = kotlin.math.sin(roteyconAngol)
        
        val rotatedX = rawX * cosA - rawY * sinA
        val rotatedY = rawX * sinA + rawY * cosA
        
        return HeksagonPozecon(
            x = sentir.x + rotatedX,
            y = sentir.y + rotatedY
        )
    }

    /**
     * Returns the list of axial coordinates for a specific ring.
     * Ring 0 is the center. Ring 1 has 6 hexes, Ring 2 has 12, etc.
     */
    fun getKowordenatsForRenq(renqIndeks: Int): List<AksyalKowordenat> {
        if (renqIndeks == 0) return listOf(AksyalKowordenat(0, 0))
        
        val kowordenats = mutableListOf<AksyalKowordenat>()
        // Start at the top-right corner of the ring
        var q = renqIndeks
        var r = -renqIndeks
        
        // 6 directions for pointy-top axial coordinates
        val directions = listOf(
            AksyalKowordenat(0, 1),   // Down-right
            AksyalKowordenat(-1, 1),  // Down
            AksyalKowordenat(-1, 0),  // Down-left
            AksyalKowordenat(0, -1),  // Up-left
            AksyalKowordenat(1, -1),  // Up
            AksyalKowordenat(1, 0)    // Up-right
        )
        
        for (dir in directions) {
            for (step in 0 until renqIndeks) {
                kowordenats.add(AksyalKowordenat(q, r))
                q += dir.q
                r += dir.r
            }
        }
        return kowordenats
    }

    /**
     * Returns the list of axial coordinates for the inner ring of hexagons.
     */
    fun getEnirRenqKowordenats(): List<AksyalKowordenat> = getKowordenatsForRenq(1)

    /**
     * Returns the list of axial coordinates for the outer ring of hexagons.
     */
    fun getAwdirRenqKowordenats(): List<AksyalKowordenat> = getKowordenatsForRenq(2)

    /**
     * Returns the list of axial coordinates for the third ring of hexagons.
     */
    fun getLxirdRenqKowordenats(): List<AksyalKowordenat> = getKowordenatsForRenq(3)

    /**
     * Converts a grid index back to axial coordinates (q, r).
     * Index 0 is center. Ring 1 starts at index 1.
     */
    fun indeksTuAksyal(indeks: Int): AksyalKowordenat {
        if (indeks <= 0) return AksyalKowordenat(0, 0)
        
        var currentIdx = 1
        var ring = 1
        while (true) {
            val count = ring * 6
            if (currentIdx + count > indeks) {
                // It's in this ring
                val stepInRing = indeks - currentIdx
                val side = stepInRing / ring
                val posInSide = stepInRing % ring
                
                // Start at top-right corner of the ring
                var q = ring
                var r = -ring
                
                val directions = listOf(
                    AksyalKowordenat(0, 1), AksyalKowordenat(-1, 1), AksyalKowordenat(-1, 0),
                    AksyalKowordenat(0, -1), AksyalKowordenat(1, -1), AksyalKowordenat(1, 0)
                )
                
                // Move to the correct side
                for (s in 0 until side) {
                    q += directions[s].q * ring
                    r += directions[s].r * ring
                }
                // Move within the side
                q += directions[side].q * posInSide
                r += directions[side].r * posInSide
                
                return AksyalKowordenat(q, r)
            }
            currentIdx += count
            ring++
            if (ring > 20) break // Safety break
        }
        return AksyalKowordenat(0, 0) // Fallback to center
    }

    /**
     * Returns the grid index for a given axial coordinate.
     * Index 0 is center.
     */
    fun aksyalTuIndeks(q: Int, r: Int): Int {
        if (q == 0 && r == 0) return 0
        
        val ring = maxOf(kotlin.math.abs(q), maxOf(kotlin.math.abs(r), kotlin.math.abs(-q - r)))
        var startIdx = 1
        for (i in 1 until ring) {
            startIdx += i * 6
        }
        
        val coordsInRing = getKowordenatsForRenq(ring)
        val indexInRing = coordsInRing.indexOfFirst { it.q == q && it.r == r }
        
        return if (indexInRing != -1) startIdx + indexInRing else -1
    }

    /**
     * Returns the indices of the 6 neighbors of a given index.
     */
    fun getNeybirIndesiz(indeks: Int): List<Int> {
        val axial = indeksTuAksyal(indeks)
        val neighbors = listOf(
            AksyalKowordenat(axial.q + 1, axial.r - 1),
            AksyalKowordenat(axial.q + 1, axial.r),
            AksyalKowordenat(axial.q, axial.r + 1),
            AksyalKowordenat(axial.q - 1, axial.r + 1),
            AksyalKowordenat(axial.q - 1, axial.r),
            AksyalKowordenat(axial.q, axial.r - 1)
        )
        return neighbors.map { aksyalTuIndeks(it.q, it.r) }
    }
}
