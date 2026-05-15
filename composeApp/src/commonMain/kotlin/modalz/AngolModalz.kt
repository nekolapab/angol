package modalz

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlinx.serialization.Serializable

/**
 * Represents a position in pixel space.
 */
@Serializable
data class HeksagonPozecon(val x: Double, val y: Double)

/**
 * Represents the data for a hexagonal module in the Hub or a key in the keypad.
 */
@Serializable
data class ModyilDeyda(
    val id: String,
    val neym: String,
    val kulorLong: Long = Color.Gray.toArgb().toLong(),
    val pozecon: Int,
    val ezAktiv: Boolean = false,
    val glefz: List<String> = emptyList(),
    val glefKulorz: List<Long> = emptyList(),
    val type: String = "app"
) {
    val kulor: Color
        get() = Color(kulorLong.toInt())

    fun copyWith(
        id: String? = null,
        neym: String? = null,
        kulor: Color? = null,
        pozecon: Int? = null,
        ezAktiv: Boolean? = null,
        glefz: List<String>? = null,
        glefKulorz: List<Long>? = null,
        type: String? = null
    ): ModyilDeyda {
        return ModyilDeyda(
            id = id ?: this.id,
            neym = neym ?: this.neym,
            kulorLong = kulor?.toArgb()?.toLong() ?: this.kulorLong,
            pozecon = pozecon ?: this.pozecon,
            ezAktiv = ezAktiv ?: this.ezAktiv,
            glefz = glefz ?: this.glefz,
            glefKulorz = glefKulorz ?: this.glefKulorz,
            type = type ?: this.type
        )
    }

    fun toJson(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "neym" to neym,
            "kulor" to kulorLong,
            "pozecon" to pozecon,
            "ezAktiv" to ezAktiv,
            "glefz" to glefz,
            "glefKulorz" to glefKulorz,
            "type" to type
        )
    }

    companion object {
        fun fromJson(json: Map<String, Any>): ModyilDeyda {
            return ModyilDeyda(
                id = json["id"] as? String ?: "",
                neym = json["neym"] as? String ?: "",
                kulorLong = (json["kulor"] as? Number)?.toLong() ?: 0L,
                pozecon = (json["pozecon"] as? Number)?.toInt() ?: 0,
                ezAktiv = json["ezAktiv"] as? Boolean ?: false,
                glefz = (json["glefz"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                glefKulorz = (json["glefKulorz"] as? List<*>)?.mapNotNull { (it as? Number)?.toLong() } ?: emptyList(),
                type = json["type"] as? String ?: "app"
            )
        }
    }
}

/**
 * Represents axial coordinates (q, r) in a pointy-top hexagonal grid.
 */
@Serializable
data class AksyalKowordenat(val q: Int, val r: Int)
