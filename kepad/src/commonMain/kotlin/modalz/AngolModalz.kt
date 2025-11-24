package modalz

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

/**
 * Represents a position in pixel space.
 */
data class HexagonPosition(val x: Double, val y: Double)

/**
 * Represents a coordinate in the axial coordinate system for hexagons.
 */
data class AxialCoordinate(val q: Int, val r: Int)

/**
 * Holds data for a module.
 *
 * Note: For robust JSON serialization in a production application,
 * it is recommended to use a library like `kotlinx.serialization`
 * with a custom serializer for the Color class.
 */
data class ModuleData(
    val id: String,
    val name: String,
    val color: Color,
    val position: Int,
    val isActive: Boolean = false
) {
    /**
     * Converts the object to a JSON-like map.
     */
    fun toJson(): Map<String, Any> = mapOf(
        "id" to id,
        "name" to name,
        "color" to color.toArgb(), // Serialize color as an ARGB Int
        "position" to position,
        "isActive" to isActive
    )

    companion object {
        /**
         * Creates a ModuleData object from a JSON-like map.
         */
        fun fromJson(json: Map<String, Any>): ModuleData {
            // Safely handle the color value, which might be an Int or Long from JSON parsing.
            val colorValue = when (val color = json["color"]) {
                is Number -> color.toLong()
                else -> 0xFF000000 // Default to black or handle error appropriately
            }

            return ModuleData(
                id = json["id"] as? String ?: "",
                name = json["name"] as? String ?: "",
                color = Color(colorValue),
                position = json["position"] as? Int ?: 0,
                isActive = json["isActive"] as? Boolean ?: false
            )
        }
    }
}
