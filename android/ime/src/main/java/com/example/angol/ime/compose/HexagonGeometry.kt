package com.example.angol.ime.compose

import androidx.compose.ui.geometry.Offset
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
