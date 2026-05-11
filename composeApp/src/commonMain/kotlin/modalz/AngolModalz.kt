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
    val ezAktiv: Boolean = false,
    val glefz: List<String> = emptyList(),
    val glefKulorz: List<Long> = emptyList(),
    val type: String = "app"
) {
    fun copyWith(
        id: String? = null,
        neym: String? = null,
        kulor: Color? = null,
        pozecon: Int? = null,
        ezAktiv: Boolean? = null,
        glefz: List<String>? = null,
        glefKulorz: List<Long>? = null,
        type: String? = null
    ): ModyilDeyda = ModyilDeyda(
        id = id ?: this.id,
        neym = neym ?: this.neym,
        kulor = kulor ?: this.kulor,
        pozecon = pozecon ?: this.pozecon,
        ezAktiv = ezAktiv ?: this.ezAktiv,
        glefz = glefz ?: this.glefz,
        glefKulorz = glefKulorz ?: this.glefKulorz,
        type = type ?: this.type
    )

    /**
     * Converts the object to a JSON-like map.
     */
    fun toJson(): Map<String, Any> = mapOf(
        "id" to id,
        "neym" to neym,
        "kulor" to kulor.toArgb().toLong(), // Serialize color as a Long to be safe
        "pozecon" to pozecon,
        "ezAktiv" to ezAktiv,
        "glefz" to glefz,
        "glefKulorz" to glefKulorz,
        "type" to type
    )

    companion object {
        /**
         * Creates a ModyilDeyda object from a JSON-like map.
         */
        fun fromJson(json: Map<String, Any>): ModyilDeyda {
            val colorValue = when (val color = json["kulor"]) {
                is Number -> color.toLong()
                else -> 0xFF000000
            }

            return ModyilDeyda(
                id = json["id"] as? String ?: "",
                neym = json["neym"] as? String ?: "",
                kulor = Color(colorValue.toInt()),
                pozecon = json["pozecon"] as? Int ?: 0,
                ezAktiv = json["ezAktiv"] as? Boolean ?: false,
                glefz = (json["glefz"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                glefKulorz = (json["glefKulorz"] as? List<*>)?.mapNotNull { (it as? Number)?.toLong() } ?: emptyList(),
                type = json["type"] as? String ?: "app"
            )
        }
    }
}
