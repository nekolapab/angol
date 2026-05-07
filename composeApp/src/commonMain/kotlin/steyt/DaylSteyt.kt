package steyt

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import modalz.ModyilDeyda
import yuteledez.getCurrentTimeMillis

class DaylSteyt {
    companion object {
        val PROTECTED_IDS = setOf("dayl", "keypad", "beldir")
    }

    var inputText by mutableStateOf("")
    var isTextFieldFocused by mutableStateOf(false)

    var modyilz by mutableStateOf(listOf(
        ModyilDeyda(
            id = "keypad", 
            neym = "kepad", 
            kulor = Color(0xFFFF0000), 
            pozecon = 2, 
            ezAktiv = false,
            glefz = listOf(" ") + modalz.KepadKonfeg.innerLetterMode + modalz.KepadKonfeg.outerTap
        ),
        ModyilDeyda(id = "beldir", neym = "beldir", kulor = Color(0xFFFFFF00), pozecon = 3, ezAktiv = false),
        ModyilDeyda(id = "module3", neym = "mod 3", kulor = Color(0xFF00FF00), pozecon = 4, ezAktiv = false),
        ModyilDeyda(id = "module4", neym = "mod 4", kulor = Color(0xFF00FFFF), pozecon = 5, ezAktiv = false),
        ModyilDeyda(id = "module5", neym = "mod 5", kulor = Color(0xFF0000FF), pozecon = 6, ezAktiv = false),
        ModyilDeyda(id = "module6", neym = "mod 6", kulor = Color(0xFFFF00FF), pozecon = 7, ezAktiv = false),
        ModyilDeyda(id = "dayl", neym = "dayl", kulor = Color(0xFF000000), pozecon = 1, ezAktiv = true, glefz = listOf("dayl"))
    ))

    val activeModule: ModyilDeyda?
        get() = modyilz.find { it.ezAktiv && it.id != "dayl" }

    val ezKepadVezebil: Boolean
        get() = modyilz.any { it.id == "keypad" && it.ezAktiv }

    val ezBeldirVezebil: Boolean
        get() = modyilz.any { it.id == "beldir" && it.ezAktiv }

    fun updateModules(newModules: List<ModyilDeyda>) {
        modyilz = newModules
    }

    fun togilModyil(index: Int) {
        val tappedModule = modyilz.find { it.pozecon == index } ?: return
        val wasActive = tappedModule.ezAktiv
        modyilz = modyilz.map { m ->
            if (m.pozecon == index) m.copyWith(ezAktiv = !wasActive)
            else if (m.id == "dayl") m.copyWith(ezAktiv = wasActive) else m.copyWith(ezAktiv = false)
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
                val targetLabel = newGlefz.getOrNull(toIndex) ?: ""
                if (targetLabel.isNotEmpty()) return@map mod
                val maxIdx = maxOf(fromIndex, toIndex)
                while (newGlefz.size <= maxIdx) newGlefz.add("")
                while (newKulorz.size <= maxIdx) newKulorz.add(mod.kulor.toArgbLong())
                newGlefz[toIndex] = newGlefz[fromIndex]
                if (fromIndex in newKulorz.indices) newKulorz[toIndex] = newKulorz[fromIndex]
                newGlefz[fromIndex] = ""
                mod.copyWith(glefz = newGlefz, glefKulorz = newKulorz)
            } else mod
        }
    }

    fun muvModyil(fromPozecon: Int, toPozecon: Int) {
        if (fromPozecon == toPozecon) return
        val fromMod = modyilz.find { it.pozecon == fromPozecon } ?: return
        if (PROTECTED_IDS.contains(fromMod.id)) return
        if (modyilz.any { it.pozecon == toPozecon }) return
        modyilz = modyilz.map { m -> if (m.pozecon == fromPozecon) m.copyWith(pozecon = toPozecon) else m }
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
                    if (fromIndex in newKulorz.indices) newKulorz[toIndex] = newKulorz[fromIndex]
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
                newGlefz.removeAt(glefIndex)
                if (glefIndex in newKulorz.indices) newKulorz.removeAt(glefIndex)
                mod.copyWith(glefz = newGlefz, glefKulorz = newKulorz)
            } else mod
        }
        val newId = "mod_${modyilz.size + 1}_${getCurrentTimeMillis()}"
        val newPozecon = (modyilz.maxOfOrNull { it.pozecon } ?: 0) + 1
        modyilz = modyilz + ModyilDeyda(id = newId, neym = glefLabel, kulor = sourceMod.kulor, pozecon = newPozecon, glefz = listOf(glefLabel))
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
            ModyilDeyda(id = "keypad", neym = "kepad", kulor = Color(0xFFFF0000), pozecon = 2, glefz = listOf(" ") + modalz.KepadKonfeg.innerLetterMode + modalz.KepadKonfeg.outerTap),
            ModyilDeyda(id = "beldir", neym = "beldir", kulor = Color(0xFF888888), pozecon = 3),
            ModyilDeyda(id = "module3", neym = "mod 3", kulor = Color(0xFF00FF00), pozecon = 4),
            ModyilDeyda(id = "module4", neym = "mod 4", kulor = Color(0xFF00FFFF), pozecon = 5),
            ModyilDeyda(id = "module5", neym = "mod 5", kulor = Color(0xFF0000FF), pozecon = 6),
            ModyilDeyda(id = "module6", neym = "mod 6", kulor = Color(0xFFFF00FF), pozecon = 7),
            ModyilDeyda(id = "dayl", neym = "dayl", kulor = Color(0xFF000000), pozecon = 1, ezAktiv = true)
        )
    }

    private fun Color.toArgbLong(): Long {
        return ((alpha * 255.0f).toInt().toLong() shl 24) or
               ((red * 255.0f).toInt().toLong() shl 16) or
               ((green * 255.0f).toInt().toLong() shl 8) or
               ((blue * 255.0f).toInt().toLong())
    }
}
