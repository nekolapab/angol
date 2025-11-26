package com.example.angol.ime.compose

import androidx.compose.ui.geometry.Offset
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sqrt

data class AxialCoordinate(val q: Int, val r: Int)

class HexagonGeometry(
    val hexSize: Float,
    private val center: Offset
) {
    val hexWidth: Float get() = (sqrt(3.0) * hexSize).toFloat()
    val hexHeight: Float get() = (2 * hexSize)

    fun axialToPixel(q: Int, r: Int): Offset {
        val x = hexSize * (sqrt(3.0) * q + sqrt(3.0) / 2 * r)
        val y = hexSize * (3.0 / 2.0) * r
        return Offset(
            x = (center.x + x).toFloat(),
            y = (center.y + y).toFloat()
        )
    }

    fun pixelToAxial(offset: Offset): AxialCoordinate {
        val x = (offset.x - center.x) / hexSize
        val y = (offset.y - center.y) / hexSize

        val q = (sqrt(3.0) / 3.0 * x - 1.0 / 3.0 * y)
        val r = (2.0 / 3.0 * y)

        return axialRound(q, r)
    }

    private fun axialRound(q: Double, r: Double): AxialCoordinate {
        var rq = q.roundToInt()
        var rr = r.roundToInt()
        var rs = (-q - r).roundToInt()

        val qDiff = abs(rq - q)
        val rDiff = abs(rr - r)
        val sDiff = abs(rs - (-q - r))

        if (qDiff > rDiff && qDiff > sDiff) {
            rq = -rr - rs
        } else if (rDiff > sDiff) {
            rr = -rq - rs
        }
        
        return AxialCoordinate(rq, rr)
    }

    fun getInnerRingCoordinates(): List<AxialCoordinate> {
        return listOf(
            AxialCoordinate(1, -1),
            AxialCoordinate(1, 0),
            AxialCoordinate(0, 1),
            AxialCoordinate(-1, 1),
            AxialCoordinate(-1, 0),
            AxialCoordinate(0, -1)
        )
    }

    fun getOuterRingCoordinates(): List<AxialCoordinate> {
        return listOf(
            AxialCoordinate(2, -2),
            AxialCoordinate(2, -1),
            AxialCoordinate(2, 0),
            AxialCoordinate(1, 1),
            AxialCoordinate(0, 2),
            AxialCoordinate(-1, 2),
            AxialCoordinate(-2, 2),
            AxialCoordinate(-2, 1),
            AxialCoordinate(-2, 0),
            AxialCoordinate(-1, -1),
            AxialCoordinate(0, -2),
            AxialCoordinate(1, -2)
        )
    }
}
