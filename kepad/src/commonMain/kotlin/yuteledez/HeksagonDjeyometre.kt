package yuteledez

import modalz.AxialCoordinate
import modalz.HexagonPosition
import kotlin.math.sqrt

class HeksagonDjeyometre(
    val hexSize: Double,
    val center: HexagonPosition,
    val isLetterMode: Boolean = true
) {
    val hexWidth: Double
        get() = sqrt(3.0) * hexSize

    val hexHeight: Double
        get() = 2 * hexSize

    val rotationAngle: Double = 0.0

    /**
     * Converts axial coordinates (q, r) to pixel coordinates (x, y).
     */
    fun axialToPixel(q: Int, r: Int): HexagonPosition {
        // In Kotlin, integer division `3 / 2` would result in 1.
        // We use floating-point numbers to match the original Dart behavior.
        val x = hexSize * (sqrt(3.0) * q + sqrt(3.0) / 2.0 * r)
        val y = hexSize * (1.5) * r // 3.0 / 2.0 = 1.5
        return HexagonPosition(
            x = center.x + x,
            y = center.y + y
        )
    }

    /**
     * Returns the list of axial coordinates for the inner ring of hexagons.
     */
    fun getInnerRingCoordinates(): List<AxialCoordinate> {
        return listOf(
            AxialCoordinate(q = 1, r = -1),
            AxialCoordinate(q = 1, r = 0),
            AxialCoordinate(q = 0, r = 1),
            AxialCoordinate(q = -1, r = 1),
            AxialCoordinate(q = -1, r = 0),
            AxialCoordinate(q = 0, r = -1)
        )
    }

    /**
     * Returns the list of axial coordinates for the outer ring of hexagons.
     * Note: The original Dart code had 12 coordinates here.
     */
    fun getOuterRingCoordinates(): List<AxialCoordinate> {
        return listOf(
            AxialCoordinate(q = 2, r = -2),
            AxialCoordinate(q = 2, r = -1),
            AxialCoordinate(q = 2, r = 0),
            AxialCoordinate(q = 1, r = 1),
            AxialCoordinate(q = 0, r = 2),
            AxialCoordinate(q = -1, r = 2),
            AxialCoordinate(q = -2, r = 2),
            AxialCoordinate(q = -2, r = 1),
            AxialCoordinate(q = -2, r = 0),
            AxialCoordinate(q = -1, r = -1),
            AxialCoordinate(q = 0, r = -2),
            AxialCoordinate(q = 1, r = -2)
        )
    }
}
