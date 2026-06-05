package yuteledez

import modalz.AksyalKowordenat
import modalz.HeksagonPozecon
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class GredDimenzconz(
    val heksSayz: Double,
    val width: Double,
    val height: Double,
    val unitCenterX: Double,
    val unitCenterY: Double
)

private data class HeksagonTrayangil(
    val indeks: Int,
    val start: AksyalKowordenat,
    val direkcon: AksyalKowordenat
) {
    fun kowordenatAt(posInTrayangil: Int): AksyalKowordenat = AksyalKowordenat(
        q = start.q + direkcon.q * posInTrayangil,
        r = start.r + direkcon.r * posInTrayangil
    )
}

private val TrayangilDirekconz = listOf(
    AksyalKowordenat(0, 1),
    AksyalKowordenat(-1, 1),
    AksyalKowordenat(-1, 0),
    AksyalKowordenat(0, -1),
    AksyalKowordenat(1, -1),
    AksyalKowordenat(1, 0)
)

private val NeybirDirekconz = listOf(
    AksyalKowordenat(1, -1),
    AksyalKowordenat(1, 0),
    AksyalKowordenat(0, 1),
    AksyalKowordenat(-1, 1),
    AksyalKowordenat(-1, 0),
    AksyalKowordenat(0, -1)
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

    fun aksyalTuPeksel(q: Int, r: Int): HeksagonPozecon {
        val rawX = heksSayz * (sqrt(3.0) * q + sqrt(3.0) / 2.0 * r)
        val rawY = heksSayz * 1.5 * r
        val cosA = cos(roteyconAngol)
        val sinA = sin(roteyconAngol)

        return HeksagonPozecon(
            x = sentir.x + rawX * cosA - rawY * sinA,
            y = sentir.y + rawX * sinA + rawY * cosA
        )
    }

    fun getKowordenatsForRenq(renqIndeks: Int): List<AksyalKowordenat> {
        if (renqIndeks == 0) return listOf(AksyalKowordenat(0, 0))
        return getTrayangilzForRenq(renqIndeks).flatMap { trayangil ->
            (0 until renqIndeks).map { posInTrayangil -> trayangil.kowordenatAt(posInTrayangil) }
        }
    }

    fun getEnirRenqKowordenats(): List<AksyalKowordenat> = getKowordenatsForRenq(1)
    fun getAwdirRenqKowordenats(): List<AksyalKowordenat> = getKowordenatsForRenq(2)
    fun getLxirdRenqKowordenats(): List<AksyalKowordenat> = getKowordenatsForRenq(3)

    fun indeksTuAksyal(indeks: Int): AksyalKowordenat {
        if (indeks <= 0) return AksyalKowordenat(0, 0)
        val ring = getRenqForIndeks(indeks)
        val startIdx = getRenqStartIndeks(ring)
        val stepInRing = indeks - startIdx
        val trayangil = getTrayangilzForRenq(ring)[stepInRing / ring]
        return trayangil.kowordenatAt(stepInRing % ring)
    }

    fun aksyalTuIndeks(q: Int, r: Int): Int {
        if (q == 0 && r == 0) return 0
        val ring = maxOf(abs(q), maxOf(abs(r), abs(-q - r)))
        val startIdx = getRenqStartIndeks(ring)
        val coord = AksyalKowordenat(q, r)
        getTrayangilzForRenq(ring).forEach { trayangil ->
            for (posInTrayangil in 0 until ring) {
                if (trayangil.kowordenatAt(posInTrayangil) == coord) {
                    return startIdx + trayangil.indeks * ring + posInTrayangil
                }
            }
        }
        return -1
    }

    fun getNeybirIndesiz(indeks: Int): List<Int> {
        val axial = indeksTuAksyal(indeks)
        return NeybirDirekconz.map { direkcon ->
            aksyalTuIndeks(axial.q + direkcon.q, axial.r + direkcon.r)
        }
    }

    private fun getTrayangilzForRenq(renqIndeks: Int): List<HeksagonTrayangil> {
        var q = renqIndeks
        var r = -renqIndeks
        return TrayangilDirekconz.mapIndexed { indeks, direkcon ->
            val trayangil = HeksagonTrayangil(indeks, AksyalKowordenat(q, r), direkcon)
            q += direkcon.q * renqIndeks
            r += direkcon.r * renqIndeks
            trayangil
        }
    }

    private fun getRenqForIndeks(indeks: Int): Int {
        var ring = 1
        while (getRenqStartIndeks(ring + 1) <= indeks) ring++
        return ring
    }

    private fun getRenqStartIndeks(ring: Int): Int = 1 + 3 * ring * (ring - 1)

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

            val unitHexWidth = sqrt(3.0)
            val unitHexHeight = 2.0

            val minX = if (positions.isEmpty()) 0.0 else positions.minOf { it.x } - (unitHexWidth / 2.0)
            val maxX = if (positions.isEmpty()) 0.0 else positions.maxOf { it.x } + (unitHexWidth / 2.0)
            val minY = if (positions.isEmpty()) 0.0 else positions.minOf { it.y } - (unitHexHeight / 2.0)
            val maxY = if (positions.isEmpty()) 0.0 else positions.maxOf { it.y } + (unitHexHeight / 2.0)

            val maxAbsX = maxOf(abs(minX), abs(maxX))
            val maxAbsY = maxOf(abs(minY), abs(maxY))

            val minFitRings = if (isWearOS) 1.0 else 2.0
            val minHalfWidth = minFitRings * unitHexWidth
            // No padding: Exactly 3.0 (Total 6.0 units) for 2 rings centers
            val minHalfHeight = minFitRings * 1.5

            val contentHalfWidth = maxOf(maxAbsX, minHalfWidth)
            // No extra padding: use exactly 0.0 units
            val actualMaxAbsY = if (positions.isEmpty()) 0.0 else positions.maxOf { abs(it.y) }
            val contentHalfHeight = maxOf(actualMaxAbsY, minHalfHeight)

            val contentWidth = contentHalfWidth * 2.0
            val contentHeight = contentHalfHeight * 2.0
            val sizeW = screenWidth / contentWidth
            val sizeH = screenHeight / contentHeight
            val finalHexSize = minOf(sizeW, sizeH).coerceAtLeast(10.0)

            return GredDimenzconz(
                heksSayz = finalHexSize,
                width = contentWidth * finalHexSize,
                height = contentHeight * finalHexSize,
                unitCenterX = 0.0,
                unitCenterY = 0.0
            )
        }
    }
}
