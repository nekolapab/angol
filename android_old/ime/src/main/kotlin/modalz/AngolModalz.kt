package modalz

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

/**
 * Represents a position in pixel space.
 */
data class HeksagonPozecon(val x: Double, val y: Double)

/**
 * Represents a coordinate in the axial coordinate system for hexagons.
 */
data class AksyalKowordenat(val q: Int, val r: Int)

/**
 * Holds data for a module.
 *
 * Note: For robust JSON serialization in a production application,
 * it is recommended to use a library like `kotlinx.serialization`
 * with a custom serializer for the Color class.
 */
data class ModyilDeyda(
    val id: String,
    val neym: String,
    val kulor: Color,
    val pozecon: Int,
    val ezAktiv: Boolean = false
) {
    /**
     * Converts the object to a JSON-like map.
     */
    fun toJson(): Map<String, Any> = mapOf(
        "id" to id,
        "neym" to neym,
        "kulor" to kulor.toArgb(), // Serialize color as an ARGB Int
        "pozecon" to pozecon,
        "ezAktiv" to ezAktiv
    )

    companion object {
        /**
         * Creates a ModyilDeyda object from a JSON-like map.
         */
        fun fromJson(json: Map<String, Any>): ModyilDeyda {
            // Safely handle the color value, which might be an Int or Long from JSON parsing.
            val colorValue = when (val color = json["kulor"]) {
                is Number -> color.toLong()
                else -> 0xFF000000 // Default to black or handle error appropriately
            }

            return ModyilDeyda(
                id = json["id"] as? String ?: "",
                neym = json["neym"] as? String ?: "",
                kulor = Color(colorValue),
                pozecon = json["pozecon"] as? Int ?: 0,
                ezAktiv = json["ezAktiv"] as? Boolean ?: false
            )
        }
    }
}
