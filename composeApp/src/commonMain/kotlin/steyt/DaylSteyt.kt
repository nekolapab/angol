package steyt

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import modalz.ModyilDeyda

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
            pozecon = 1, 
            ezAktiv = false,
            glefz = modalz.KepadKonfeg.innerLetterMode + modalz.KepadKonfeg.outerTap + listOf(" ")
        ),
        ModyilDeyda(id = "beldir", neym = "beldir", kulor = Color(0xFF888888), pozecon = 2, ezAktiv = false),
        ModyilDeyda(id = "module3", neym = "mod 3", kulor = Color(0xFF00FF00), pozecon = 3, ezAktiv = false),
        ModyilDeyda(id = "module4", neym = "mod 4", kulor = Color(0xFF00FFFF), pozecon = 4, ezAktiv = false),
        ModyilDeyda(id = "module5", neym = "mod 5", kulor = Color(0xFF0000FF), pozecon = 5, ezAktiv = false),
        ModyilDeyda(id = "module6", neym = "mod 6", kulor = Color(0xFFFF00FF), pozecon = 6, ezAktiv = false),
        ModyilDeyda(id = "dayl", neym = "dayl", kulor = Color(0xFF000000), pozecon = 7, ezAktiv = true)
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
            if (m.pozecon == index) {
                m.copyWith(ezAktiv = !wasActive)
            } else {
                if (m.id == "dayl") m.copyWith(ezAktiv = wasActive) else m.copyWith(ezAktiv = false)
            }
        }
    }

    fun kopeModyil(id: String) {
        val modToCopy = modyilz.find { it.id == id } ?: return
        val newId = "mod_${modyilz.size + 1}_${System.currentTimeMillis()}"
        val newNeym = "${modToCopy.neym} kope"
        val newPozecon = (modyilz.maxOfOrNull { it.pozecon } ?: 0) + 1
        
        modyilz = modyilz + modToCopy.copyWith(id = newId, neym = newNeym, pozecon = newPozecon, ezAktiv = false)
    }

    fun deletModyil(id: String) {
        if (PROTECTED_IDS.contains(id)) return
        modyilz = modyilz.filter { it.id != id }
    }

    fun reneymModyil(id: String, newNeym: String) {
        modyilz = modyilz.map { 
            if (it.id == id) it.copyWith(neym = newNeym) else it
        }
    }

    fun reneymGlef(modId: String, index: Int, newLabel: String) {
        modyilz = modyilz.map { mod ->
            if (mod.id == modId) {
                val newGlefz = mod.glefz.toMutableList()
                if (index in newGlefz.indices) {
                    newGlefz[index] = newLabel
                }
                mod.copyWith(glefz = newGlefz)
            } else mod
        }
    }

    fun swopGlefz(modId: String, fromIndex: Int, toIndex: Int) {
        modyilz = modyilz.map { mod ->
            if (mod.id == modId) {
                val newGlefz = mod.glefz.toMutableList()
                while (newGlefz.size <= fromIndex || newGlefz.size <= toIndex) {
                    newGlefz.add("")
                }
                val temp = newGlefz[fromIndex]
                newGlefz[fromIndex] = newGlefz[toIndex]
                newGlefz[toIndex] = temp
                mod.copyWith(glefz = newGlefz)
            } else mod
        }
    }

    fun kopeGlefTuEmpt(modId: String, fromIndex: Int, toIndex: Int) {
        modyilz = modyilz.map { mod ->
            if (mod.id == modId) {
                val newGlefz = mod.glefz.toMutableList()
                while (newGlefz.size <= toIndex) {
                    newGlefz.add("")
                }
                if (fromIndex in newGlefz.indices) {
                    newGlefz[toIndex] = newGlefz[fromIndex]
                }
                mod.copyWith(glefz = newGlefz)
            } else mod
        }
    }

    fun muvGlefTuHub(modId: String, glefIndex: Int) {
        val sourceMod = modyilz.find { it.id == modId } ?: return
        val glefLabel = sourceMod.glefz.getOrNull(glefIndex) ?: return
        
        // Create a new module in the hub from this glyph
        val newId = "mod_${modyilz.size + 1}_${System.currentTimeMillis()}"
        val newPozecon = (modyilz.maxOfOrNull { it.pozecon } ?: 0) + 1
        val newMod = ModyilDeyda(
            id = newId,
            neym = glefLabel,
            kulor = sourceMod.kulor,
            pozecon = newPozecon,
            ezAktiv = false,
            glefz = listOf(glefLabel)
        )
        
        modyilz = modyilz + newMod
    }

    fun swopModyilz(fromIndex: Int, toIndex: Int) {
        val fromMod = modyilz.find { it.pozecon == fromIndex } ?: return
        val toMod = modyilz.find { it.pozecon == toIndex }
        
        modyilz = modyilz.map { mod ->
            when (mod.pozecon) {
                fromIndex -> mod.copyWith(pozecon = toIndex)
                toIndex -> mod.copyWith(pozecon = fromIndex)
                else -> mod
            }
        }
    }

    fun kopeModyilTuEmpt(fromIndex: Int, toIndex: Int) {
        val modToCopy = modyilz.find { it.pozecon == fromIndex } ?: return
        val newId = "mod_${modyilz.size + 1}_${System.currentTimeMillis()}"
        val newMod = modToCopy.copyWith(id = newId, pozecon = toIndex, ezAktiv = false)
        modyilz = modyilz + newMod
    }

    fun muvModyilTuParent(index: Int) {
        val modToMuv = modyilz.find { it.pozecon == index } ?: return
        if (PROTECTED_IDS.contains(modToMuv.id)) return
        // In the Hub, 'moving to parent' might mean deletion or moving to a different category
        // For now, let's treat it as deletion (removing from current ring)
        modyilz = modyilz.filter { it.pozecon != index }
    }

    fun reset() {
        modyilz = listOf(
            ModyilDeyda(
                id = "keypad", 
                neym = "kepad", 
                kulor = Color(0xFFFF0000), 
                pozecon = 1, 
                ezAktiv = false,
                glefz = modalz.KepadKonfeg.innerLetterMode + modalz.KepadKonfeg.outerTap + listOf(" ")
            ),
            ModyilDeyda(id = "beldir", neym = "beldir", kulor = Color(0xFF888888), pozecon = 2, ezAktiv = false),
            ModyilDeyda(id = "module3", neym = "mod 3", kulor = Color(0xFF00FF00), pozecon = 3, ezAktiv = false),
            ModyilDeyda(id = "module4", neym = "mod 4", kulor = Color(0xFF00FFFF), pozecon = 4, ezAktiv = false),
            ModyilDeyda(id = "module5", neym = "mod 5", kulor = Color(0xFF0000FF), pozecon = 5, ezAktiv = false),
            ModyilDeyda(id = "module6", neym = "mod 6", kulor = Color(0xFFFF00FF), pozecon = 6, ezAktiv = false),
            ModyilDeyda(id = "dayl", neym = "dayl", kulor = Color(0xFF000000), pozecon = 7, ezAktiv = true)
        )
    }
}
