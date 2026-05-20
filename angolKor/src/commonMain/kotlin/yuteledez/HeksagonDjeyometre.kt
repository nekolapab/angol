package yuteledez

import modalz.AksyalKowordenat
import modalz.HeksagonPozecon
import kotlin.math.sqrt

data class GredDimenzconz(
    val heksSayz: Double,
    val width: Double,
    val height: Double,
    val unitCenterX: Double,
    val unitCenterY: Double
)

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
        val rawX = heksSayz * (sqrt(3.0) * q + sqrt(3.0) / 2.0 * r)
        val rawY = heksSayz * (1.5) * r
        
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

    fun getKowordenatsForRenq(renqIndeks: Int): List<AksyalKowordenat> {
        if (renqIndeks == 0) return listOf(AksyalKowordenat(0, 0))
        val kowordenats = mutableListOf<AksyalKowordenat>()
        var q = renqIndeks
        var r = -renqIndeks
        val directions = listOf(
            AksyalKowordenat(0, 1), AksyalKowordenat(-1, 1), AksyalKowordenat(-1, 0),
            AksyalKowordenat(0, -1), AksyalKowordenat(1, -1), AksyalKowordenat(1, 0)
        )
        for (dir in directions) {
            for (step in 0 until renqIndeks) {
                kowordenats.add(AksyalKowordenat(q, r)); q += dir.q; r += dir.r
            }
        }
        return kowordenats
    }

    fun getEnirRenqKowordenats(): List<AksyalKowordenat> = getKowordenatsForRenq(1)
    fun getAwdirRenqKowordenats(): List<AksyalKowordenat> = getKowordenatsForRenq(2)
    fun getLxirdRenqKowordenats(): List<AksyalKowordenat> = getKowordenatsForRenq(3)

    fun indeksTuAksyal(indeks: Int): AksyalKowordenat {
        if (indeks <= 0) return AksyalKowordenat(0, 0)
        var currentIdx = 1; var ring = 1
        while (true) {
            val count = ring * 6
            if (currentIdx + count > indeks) {
                val stepInRing = indeks - currentIdx; val side = stepInRing / ring; val posInSide = stepInRing % ring
                var q = ring; var r = -ring
                val directions = listOf(
                    AksyalKowordenat(0, 1), AksyalKowordenat(-1, 1), AksyalKowordenat(-1, 0),
                    AksyalKowordenat(0, -1), AksyalKowordenat(1, -1), AksyalKowordenat(1, 0)
                )
                for (s in 0 until side) { q += directions[s].q * ring; r += directions[s].r * ring }
                q += directions[side].q * posInSide; r += directions[side].r * posInSide
                return AksyalKowordenat(q, r)
            }
            currentIdx += count; ring++
            if (ring > 20) break
        }
        return AksyalKowordenat(0, 0)
    }

    fun aksyalTuIndeks(q: Int, r: Int): Int {
        if (q == 0 && r == 0) return 0
        val ring = maxOf(kotlin.math.abs(q), maxOf(kotlin.math.abs(r), kotlin.math.abs(-q - r)))
        var startIdx = 1
        for (i in 1 until ring) startIdx += i * 6
        val coordsInRing = getKowordenatsForRenq(ring)
        val indexInRing = coordsInRing.indexOfFirst { it.q == q && it.r == r }
        return if (indexInRing != -1) startIdx + indexInRing else -1
    }

    fun getNeybirIndesiz(indeks: Int): List<Int> {
        val axial = indeksTuAksyal(indeks)
        val neighbors = listOf(
            AksyalKowordenat(axial.q + 1, axial.r - 1), AksyalKowordenat(axial.q + 1, axial.r),
            AksyalKowordenat(axial.q, axial.r + 1), AksyalKowordenat(axial.q - 1, axial.r + 1),
            AksyalKowordenat(axial.q - 1, axial.r), AksyalKowordenat(axial.q, axial.r - 1)
        )
        return neighbors.map { aksyalTuIndeks(it.q, it.r) }
    }

    companion object {
        fun kalkyuleytGredDimenzconz(
            activeIndices: List<Int>,
            screenWidth: Double,
            screenHeight: Double,
            isWearOS: Boolean
        ): GredDimenzconz {
            val tempGeo = HeksagonDjeyometre(heksSayz = 1.0, sentir = modalz.HeksagonPozecon(0.0, 0.0))
            val positions = activeIndices.map { idx ->
                val axial = tempGeo.indeksTuAksyal(idx)
                tempGeo.aksyalTuPeksel(axial.q, axial.r)
            }
            
            val unitHexWidth = kotlin.math.sqrt(3.0)
            val unitHexHeight = 2.0
            
            val minX = if (positions.isEmpty()) 0.0 else positions.minOf { it.x } - (unitHexWidth / 2.0)
            val maxX = if (positions.isEmpty()) 0.0 else positions.maxOf { it.x } + (unitHexWidth / 2.0)
            val minY = if (positions.isEmpty()) 0.0 else positions.minOf { it.y } - (unitHexHeight / 2.0)
            val maxY = if (positions.isEmpty()) 0.0 else positions.maxOf { it.y } + (unitHexHeight / 2.0)
            
            val minFitRings = if (isWearOS) 1.0 else 2.0
            val minFitWidth = (minFitRings * 2.0 + 1.0) * unitHexWidth
            val minFitHeight = minFitRings * 3.0 + 2.0 

            val contentWidth = maxOf(maxX - minX, minFitWidth)
            val contentHeight = maxOf(maxY - minY, minFitHeight)
            
            // No margin, touch the edges
            val sizeW = screenWidth / contentWidth
            val sizeH = screenHeight / contentHeight
            val finalHexSize = minOf(sizeW, sizeH).coerceAtLeast(10.0)

            return GredDimenzconz(
                heksSayz = finalHexSize,
                width = contentWidth * finalHexSize,
                height = contentHeight * finalHexSize,
                unitCenterX = (minX + maxX) / 2.0,
                unitCenterY = (minY + maxY) / 2.0
            )
        }
    }
}
