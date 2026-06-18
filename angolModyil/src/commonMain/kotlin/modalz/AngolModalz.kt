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
    val ezAkdev: Boolean = false,
    val glefs: List<String> = emptyList(),
    val glefKulorz: List<Long> = emptyList(),
    val type: String = "app"
) {
    val kulor: Color
        get() = Color(kulorLong.toInt())

    fun copyWith(
        id: String? = null,
        neym: String? = null,
        kulor: Color? = null,
        kulorLong: Long? = null,
        pozecon: Int? = null,
        ezAkdev: Boolean? = null,
        glefs: List<String>? = null,
        glefKulorz: List<Long>? = null,
        type: String? = null
    ): ModyilDeyda {
        val targetPozecon = pozecon ?: this.pozecon
        val absoluteColor = when (targetPozecon) {
            2 -> 0xFFFF0000L // Red
            3 -> 0xFFFFFF00L // Yellow
            4 -> 0xFF00FF00L // Green
            5 -> 0xFF00FFFFL // Cyan
            6 -> 0xFF0000FFL // Blue
            7 -> 0xFFFF00FFL // Magenta
            else -> null
        }
        val targetKulorLong = absoluteColor ?: kulorLong ?: kulor?.toArgb()?.toLong() ?: this.kulorLong

        return ModyilDeyda(
            id = id ?: this.id,
            neym = neym ?: this.neym,
            kulorLong = targetKulorLong,
            pozecon = targetPozecon,
            ezAkdev = ezAkdev ?: this.ezAkdev,
            glefs = glefs ?: this.glefs,
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
            "ezAkdev" to ezAkdev,
            "glefs" to glefs,
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
                ezAkdev = json["ezAkdev"] as? Boolean ?: false,
                glefs = ((json["glefs"] ?: json["glefz"]) as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
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
