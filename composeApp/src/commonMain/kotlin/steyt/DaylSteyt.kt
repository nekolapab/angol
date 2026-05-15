package steyt

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import modalz.ModyilDeyda
import yuteledez.getCurrentTimeMillis

class DaylSteyt {
    companion object {
        val PROTECTED_IDS = setOf("dayl", "keypad", "beld")
    }

    var inputText by mutableStateOf("")
    var isTextFieldFocused by mutableStateOf(false)

    var modyilz by mutableStateOf(listOf(
        ModyilDeyda(id = "dayl", neym = "dayl", kulorLong = Color(0xFF000000).toArgb().toLong(), pozecon = 1, ezAktiv = false, glefz = listOf("dayl"), type = "hub"),
        ModyilDeyda(
            id = "keypad", 
            neym = "kepad", 
            kulorLong = Color(0xFFFF0000).toArgb().toLong(), 
            pozecon = 2, 
            ezAktiv = false,
            glefz = listOf(" ") + modalz.KepadKonfeg.innerLetterMode + modalz.KepadKonfeg.outerTap,
            glefKulorz = listOf(Color.White.toArgbLong()) + 
                         modalz.KepadKonfeg.innerRingColors.map { it.toArgbLong() } + 
                         modalz.KepadKonfeg.rainbowColors.map { it.toArgbLong() },
            type = "keypad"
        ),
        ModyilDeyda(id = "beld", neym = "beld", kulorLong = Color(0xFFFFFF00).toArgb().toLong(), pozecon = 3, ezAktiv = false, type = "beld"),
        ModyilDeyda(id = "module3", neym = "mod 3", kulorLong = Color(0xFF00FF00).toArgb().toLong(), pozecon = 4, ezAktiv = false),
        ModyilDeyda(id = "module4", neym = "mod 4", kulorLong = Color(0xFF00FFFF).toArgb().toLong(), pozecon = 5, ezAktiv = false),
        ModyilDeyda(id = "module5", neym = "mod 5", kulorLong = Color(0xFF0000FF).toArgb().toLong(), pozecon = 6, ezAktiv = false),
        ModyilDeyda(id = "module6", neym = "mod 6", kulorLong = Color(0xFFFF00FF).toArgb().toLong(), pozecon = 7, ezAktiv = false)
    ))

    val activeModule: ModyilDeyda?
        get() = modyilz.find { it.ezAktiv && it.type != "hub" }

    val ezKepadVezebil: Boolean
        get() = modyilz.any { it.type == "keypad" && it.ezAktiv }

    val ezBeldirVezebil: Boolean
        get() = modyilz.any { it.type == "beld" && it.ezAktiv }

    fun updateModules(newModules: List<ModyilDeyda>) {
        modyilz = newModules
    }

    fun activateModyil(id: String) {
        modyilz = modyilz.map { m ->
            m.copyWith(ezAktiv = (m.id == id))
        }
    }

    fun togilModyil(index: Int) {
        val tappedModule = modyilz.find { it.pozecon == index } ?: return
        val wasActive = tappedModule.ezAktiv
        modyilz = modyilz.map { m ->
            if (m.pozecon == index) m.copyWith(ezAktiv = !wasActive)
            else if (m.type == "hub") m.copyWith(ezAktiv = wasActive) else m.copyWith(ezAktiv = false)
        }
    }

    fun kopeModyil(id: String) {
        val modToCopy = modyilz.find { it.id == id } ?: return
        val newId = "mod_${modyilz.size + 1}_${getCurrentTimeMillis()}"
        val newPozecon = (modyilz.maxOfOrNull { it.pozecon } ?: 0) + 1
        modyilz = modyilz + modToCopy.copyWith(id = newId, pozecon = newPozecon, ezAktiv = false)
    }

    fun deletModyil(id: String) {
        if (PROTECTED_IDS.contains(id)) return
        modyilz = modyilz.filter { it.id != id }
    }

    fun reneymModyil(id: String, newNeym: String) {
        modyilz = modyilz.map { if (it.id == id) it.copyWith(neym = newNeym) else it }
    }

    fun reneymGlef(modId: String, index: Int, newLabel: String) {
        modyilz = modyilz.map { mod ->
            if (mod.id == modId) {
                val newGlefz = mod.glefz.toMutableList()
                while (newGlefz.size <= index) newGlefz.add("")
                newGlefz[index] = newLabel
                mod.copyWith(glefz = newGlefz)
            } else mod
        }
    }

    fun muvGlef(modId: String, fromIndex: Int, toIndex: Int) {
        modyilz = modyilz.map { mod ->
            if (mod.id == modId) {
                val newGlefz = mod.glefz.toMutableList()
                val newKulorz = mod.glefKulorz.toMutableList()
                val maxIdx = maxOf(fromIndex, toIndex)
                
                // Ensure list is long enough
                while (newGlefz.size <= maxIdx) newGlefz.add("")
                while (newKulorz.size <= maxIdx) newKulorz.add(0xFF333333) // Neutral Dark Gray
                
                // STRICT MOVE: Only move if target is empty, and do NOT shift the list.
                // Just clear the old spot and set the new spot.
                if (newGlefz[toIndex].isEmpty()) {
                    newGlefz[toIndex] = newGlefz[fromIndex]
                    newKulorz[toIndex] = newKulorz[fromIndex]
                    
                    newGlefz[fromIndex] = ""
                    newKulorz[fromIndex] = 0xFF333333 // Neutral Dark Gray (Not module color)
                }
                
                mod.copyWith(glefz = newGlefz, glefKulorz = newKulorz)
            } else mod
        }
    }

    fun kopeGlefTuEmpt(modId: String, fromIndex: Int, toIndex: Int) {
        modyilz = modyilz.map { mod ->
            if (mod.id == modId) {
                val newGlefz = mod.glefz.toMutableList()
                val newKulorz = mod.glefKulorz.toMutableList()
                
                while (newGlefz.size <= toIndex) newGlefz.add("")
                while (newKulorz.size <= toIndex) newKulorz.add(mod.kulor.toArgbLong())
                
                if (fromIndex in newGlefz.indices) {
                    newGlefz[toIndex] = newGlefz[fromIndex]
                    val sourceColor = if (fromIndex < newKulorz.size) newKulorz[fromIndex] else mod.kulor.toArgbLong()
                    newKulorz[toIndex] = sourceColor
                }
                mod.copyWith(glefz = newGlefz, glefKulorz = newKulorz)
            } else mod
        }
    }

    fun muvGlefTuHub(modId: String, glefIndex: Int) {
        val sourceMod = modyilz.find { it.id == modId } ?: return
        val glefLabel = sourceMod.glefz.getOrNull(glefIndex) ?: return
        if (glefLabel.isEmpty() || glefIndex == 0) return

        modyilz = modyilz.map { mod ->
            if (mod.id == modId) {
                val newGlefz = mod.glefz.toMutableList()
                val newKulorz = mod.glefKulorz.toMutableList()
                
                // FIXED: Clear the spot instead of removing it to avoid shifting
                newGlefz[glefIndex] = ""
                if (glefIndex in newKulorz.indices) newKulorz[glefIndex] = mod.kulorLong
                
                mod.copyWith(glefz = newGlefz, glefKulorz = newKulorz)
            } else mod
        }
        val newId = "mod_${modyilz.size + 1}_${getCurrentTimeMillis()}"
        val newPozecon = (modyilz.maxOfOrNull { it.pozecon } ?: 0) + 1
        modyilz = modyilz + ModyilDeyda(id = newId, neym = glefLabel, kulorLong = sourceMod.kulorLong, pozecon = newPozecon, glefz = listOf(glefLabel))
    }

    fun swopModyilz(fromPozecon: Int, toPozecon: Int) {
        modyilz = modyilz.map { mod ->
            when (mod.pozecon) {
                fromPozecon -> mod.copyWith(pozecon = toPozecon)
                toPozecon -> mod.copyWith(pozecon = fromPozecon)
                else -> mod
            }
        }
    }

    fun kopeModyilTuEmpt(fromPozecon: Int, toPozecon: Int) {
        val modToCopy = modyilz.find { it.pozecon == fromPozecon } ?: return
        val newId = "mod_${modyilz.size + 1}_${getCurrentTimeMillis()}"
        modyilz = modyilz + modToCopy.copyWith(id = newId, pozecon = toPozecon, ezAktiv = false)
    }

    fun muvModyilTuParent(index: Int) {
        val modToMuv = modyilz.find { it.pozecon == index } ?: return
        if (PROTECTED_IDS.contains(modToMuv.id)) return
        modyilz = modyilz.filter { it.pozecon != index }
    }

    fun reset() {
        modyilz = listOf(
            ModyilDeyda(id = "dayl", neym = "dayl", kulorLong = Color(0xFF000000).toArgb().toLong(), pozecon = 1, ezAktiv = false, glefz = listOf("dayl"), type = "hub"),
            ModyilDeyda(id = "keypad", neym = "kepad", kulorLong = Color(0xFFFF0000).toArgb().toLong(), pozecon = 2, glefz = listOf(" ") + modalz.KepadKonfeg.innerLetterMode + modalz.KepadKonfeg.outerTap, type = "keypad"),
            ModyilDeyda(id = "beld", neym = "beld", kulorLong = Color(0xFFFFFF00).toArgb().toLong(), pozecon = 3, type = "beld"),
            ModyilDeyda(id = "module3", neym = "mod 3", kulorLong = Color(0xFF00FF00).toArgb().toLong(), pozecon = 4),
            ModyilDeyda(id = "module4", neym = "mod 4", kulorLong = Color(0xFF00FFFF).toArgb().toLong(), pozecon = 5),
            ModyilDeyda(id = "module5", neym = "mod 5", kulorLong = Color(0xFF0000FF).toArgb().toLong(), pozecon = 6),
            ModyilDeyda(id = "module6", neym = "mod 6", kulorLong = Color(0xFFFF00FF).toArgb().toLong(), pozecon = 7)
        )
    }

    private fun Color.toArgbLong(): Long {
        return ((alpha * 255.0f).toInt().toLong() shl 24) or
               ((red * 255.0f).toInt().toLong() shl 16) or
               ((green * 255.0f).toInt().toLong() shl 8) or
               ((blue * 255.0f).toInt().toLong())
    }
}
