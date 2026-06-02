package steyt

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import modalz.ModyilDeyda
import yuteledez.getCurrentTimeMillis

class AngolSteyt {
    companion object {
        val PROTECTED_IDS = setOf("dayl", "keypad", "rebeld")
    }

    var inputText by mutableStateOf("")
    var isTextFieldFocused by mutableStateOf(false)

    var modyilz by mutableStateOf(listOf(
        ModyilDeyda(id = "dayl", neym = "angol", kulorLong = Color(0xFF000000).toArgb().toLong(), pozecon = 1, ezAktiv = false, glefz = listOf("angol"), type = "hub"),
        ModyilDeyda(
            id = "keypad", 
            neym = "kepad", 
            kulorLong = Color(0xFFFF0000).toArgb().toLong(), 
            pozecon = 2, 
            ezAktiv = false,
            glefz = listOf(" ") + modalz.HeksagonKonfeg.innerLetterMode + modalz.HeksagonKonfeg.outerTap,
            glefKulorz = listOf(Color.White.toArgbLong()) + 
                         modalz.HeksagonKonfeg.innerRingColors.map { it.toArgbLong() } + 
                         modalz.HeksagonKonfeg.rainbowColors.map { it.toArgbLong() },
            type = "keypad"
        ),
        ModyilDeyda(id = "rebeld", neym = "rebeld", kulorLong = Color(0xFFFFFF00).toArgb().toLong(), pozecon = 3, ezAktiv = false, type = "rebeld"),
        ModyilDeyda(id = "module3", neym = "mod 3", kulorLong = Color(0xFF00FF00).toArgb().toLong(), pozecon = 4, ezAktiv = false),
        ModyilDeyda(id = "module4", neym = "mod 4", kulorLong = Color(0xFF00FFFF).toArgb().toLong(), pozecon = 5, ezAktiv = false),
        ModyilDeyda(id = "module5", neym = "mod 5", kulorLong = Color(0xFF0000FF).toArgb().toLong(), pozecon = 6, ezAktiv = false),
        ModyilDeyda(id = "module6", neym = "mod 6", kulorLong = Color(0xFFFF00FF).toArgb().toLong(), pozecon = 7, ezAktiv = false),
    ))

    var beldirModyilz by mutableStateOf(listOf(
        ModyilDeyda(id = "dayl", neym = "angol", kulorLong = Color(0xFF000000).toArgb().toLong(), pozecon = 1, ezAktiv = false, glefz = listOf("angol"), type = "hub"),
        ModyilDeyda(
            id = "keypad", 
            neym = "kepad", 
            kulorLong = Color(0xFFFF0000).toArgb().toLong(), 
            pozecon = 2, 
            ezAktiv = false,
            glefz = listOf(" ") + modalz.HeksagonKonfeg.innerLetterMode + modalz.HeksagonKonfeg.outerTap,
            glefKulorz = listOf(Color.White.toArgbLong()) + 
                         modalz.HeksagonKonfeg.innerRingColors.map { it.toArgbLong() } + 
                         modalz.HeksagonKonfeg.rainbowColors.map { it.toArgbLong() },
            type = "keypad"
        ),
        ModyilDeyda(id = "rebeld", neym = "rebeld", kulorLong = Color(0xFFFFFF00).toArgb().toLong(), pozecon = 3, ezAktiv = false, type = "rebeld"),
        ModyilDeyda(id = "module3", neym = "mod 3", kulorLong = Color(0xFF00FF00).toArgb().toLong(), pozecon = 4, ezAktiv = false),
        ModyilDeyda(id = "module4", neym = "mod 4", kulorLong = Color(0xFF00FFFF).toArgb().toLong(), pozecon = 5, ezAktiv = false),
        ModyilDeyda(id = "module5", neym = "mod 5", kulorLong = Color(0xFF0000FF).toArgb().toLong(), pozecon = 6, ezAktiv = false),
        ModyilDeyda(id = "module6", neym = "mod 6", kulorLong = Color(0xFFFF00FF).toArgb().toLong(), pozecon = 7, ezAktiv = false),
    ))

    val activeModule: ModyilDeyda?
        get() = modyilz.find { it.ezAktiv && it.type != "hub" }

    val ezKepadVezebil: Boolean
        get() = modyilz.any { it.type == "keypad" && it.ezAktiv }

    val ezBeldirVezebil: Boolean
        get() = modyilz.any { it.type == "rebeld" && it.ezAktiv }

    fun updateModules(newModules: List<ModyilDeyda>) {
        modyilz = newModules
    }

    fun updateBeldirModules(newModules: List<ModyilDeyda>) {
        beldirModyilz = newModules
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
        if (beldirModyilz.any { it.id == id }) {
            val modToCopy = beldirModyilz.find { it.id == id } ?: return
            val newId = "mod_${beldirModyilz.size + 1}_${getCurrentTimeMillis()}"
            val newPozecon = (beldirModyilz.maxOfOrNull { it.pozecon } ?: 0) + 1
            beldirModyilz = beldirModyilz + modToCopy.copyWith(id = newId, pozecon = newPozecon, ezAktiv = false)
        } else {
            val modToCopy = modyilz.find { it.id == id } ?: return
            val newId = "mod_${modyilz.size + 1}_${getCurrentTimeMillis()}"
            val newPozecon = (modyilz.maxOfOrNull { it.pozecon } ?: 0) + 1
            modyilz = modyilz + modToCopy.copyWith(id = newId, pozecon = newPozecon, ezAktiv = false)
        }
    }

    fun deletModyil(id: String) {
        if (PROTECTED_IDS.contains(id)) return
        if (beldirModyilz.any { it.id == id }) {
            beldirModyilz = beldirModyilz.filter { it.id != id }
        } else {
            modyilz = modyilz.filter { it.id != id }
        }
    }

    fun reneymModyil(id: String, newNeym: String) {
        if (beldirModyilz.any { it.id == id }) {
            beldirModyilz = beldirModyilz.map { if (it.id == id) it.copyWith(neym = newNeym) else it }
        } else {
            modyilz = modyilz.map { if (it.id == id) it.copyWith(neym = newNeym) else it }
        }
    }

    fun reneymGlef(modId: String, index: Int, newLabel: String) {
        if (beldirModyilz.any { it.id == modId }) {
            beldirModyilz = beldirModyilz.map { mod ->
                if (mod.id == modId) {
                    val newGlefz = mod.glefz.toMutableList()
                    while (newGlefz.size <= index) newGlefz.add("")
                    newGlefz[index] = newLabel
                    mod.copyWith(glefz = newGlefz)
                } else mod
            }
        } else {
            modyilz = modyilz.map { mod ->
                if (mod.id == modId) {
                    val newGlefz = mod.glefz.toMutableList()
                    while (newGlefz.size <= index) newGlefz.add("")
                    newGlefz[index] = newLabel
                    mod.copyWith(glefz = newGlefz)
                } else mod
            }
        }
    }

    fun muvGlef(modId: String, fromIndex: Int, toIndex: Int) {
        val updateFunc = { mod: ModyilDeyda ->
            val newGlefz = mod.glefz.toMutableList()
            val newKulorz = mod.glefKulorz.toMutableList()
            val maxIdx = maxOf(fromIndex, toIndex)
            while (newGlefz.size <= maxIdx) newGlefz.add("")
            while (newKulorz.size <= maxIdx) newKulorz.add(0xFF333333)
            
            // SWAP labels and colors
            val tempGlef = newGlefz[toIndex]
            val tempKulor = newKulorz[toIndex]
            
            newGlefz[toIndex] = newGlefz[fromIndex]
            newKulorz[toIndex] = newKulorz[fromIndex]
            
            newGlefz[fromIndex] = tempGlef
            newKulorz[fromIndex] = tempKulor
            
            mod.copyWith(glefz = newGlefz, glefKulorz = newKulorz)
        }
        if (beldirModyilz.any { it.id == modId }) {
            beldirModyilz = beldirModyilz.map { if (it.id == modId) updateFunc(it) else it }
        } else {
            modyilz = modyilz.map { if (it.id == modId) updateFunc(it) else it }
        }
    }

    fun kopeGlefTuEmpt(modId: String, fromIndex: Int, toIndex: Int) {
        val updateFunc = { mod: ModyilDeyda ->
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
        }
        if (beldirModyilz.any { it.id == modId }) {
            beldirModyilz = beldirModyilz.map { if (it.id == modId) updateFunc(it) else it }
        } else {
            modyilz = modyilz.map { if (it.id == modId) updateFunc(it) else it }
        }
    }

    fun muvGlefTuHub(modId: String, glefIndex: Int) {
        val isBeldir = beldirModyilz.any { it.id == modId }
        val list = if (isBeldir) beldirModyilz else modyilz
        val sourceMod = list.find { it.id == modId } ?: return
        val glefLabel = sourceMod.glefz.getOrNull(glefIndex) ?: return
        if (glefLabel.isEmpty() || glefIndex == 0) return

        val newId = "mod_${list.size + 1}_${getCurrentTimeMillis()}"
        val newPozecon = (list.maxOfOrNull { it.pozecon } ?: 0) + 1
        val newMod = ModyilDeyda(id = newId, neym = glefLabel, kulorLong = sourceMod.kulorLong, pozecon = newPozecon, glefz = listOf(glefLabel))

        val updatedList = list.map { mod ->
            if (mod.id == modId) {
                val newGlefz = mod.glefz.toMutableList()
                val newKulorz = mod.glefKulorz.toMutableList()
                newGlefz[glefIndex] = ""
                if (glefIndex in newKulorz.indices) newKulorz[glefIndex] = mod.kulorLong
                mod.copyWith(glefz = newGlefz, glefKulorz = newKulorz)
            } else mod
        } + newMod

        if (isBeldir) {
            beldirModyilz = updatedList
        } else {
            modyilz = updatedList
        }
    }

    fun replaceGlef(modId: String, fromIndex: Int, toIndex: Int) {
        val updateFunc = { mod: ModyilDeyda ->
            val newGlefz = mod.glefz.toMutableList()
            val newKulorz = mod.glefKulorz.toMutableList()
            val maxIdx = maxOf(fromIndex, toIndex)
            while (newGlefz.size <= maxIdx) newGlefz.add("")
            while (newKulorz.size <= maxIdx) newKulorz.add(0xFF333333)
            newGlefz[toIndex] = newGlefz[fromIndex]
            newKulorz[toIndex] = newKulorz[fromIndex]
            newGlefz[fromIndex] = ""
            newKulorz[fromIndex] = 0xFF333333
            mod.copyWith(glefz = newGlefz, glefKulorz = newKulorz)
        }
        if (beldirModyilz.any { it.id == modId }) {
            beldirModyilz = beldirModyilz.map { if (it.id == modId) updateFunc(it) else it }
        } else {
            modyilz = modyilz.map { if (it.id == modId) updateFunc(it) else it }
        }
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

    fun swopBeldirModyilz(fromPozecon: Int, toPozecon: Int) {
        beldirModyilz = beldirModyilz.map { mod ->
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

    fun kopeBeldirModyilTuEmpt(fromPozecon: Int, toPozecon: Int) {
        val modToCopy = beldirModyilz.find { it.pozecon == fromPozecon } ?: return
        val newId = "mod_${beldirModyilz.size + 1}_${getCurrentTimeMillis()}"
        beldirModyilz = beldirModyilz + modToCopy.copyWith(id = newId, pozecon = toPozecon, ezAktiv = false)
    }

    fun muvModyilTuParent(index: Int) {
        // No-op for Hub
    }

    fun copyModuleToDaylKeypad(fromIndexInBeldir: Int) {
        val sourceMod = beldirModyilz.find { it.pozecon == fromIndexInBeldir + 1 } ?: return
        modyilz = modyilz.map { mod ->
            if (mod.type == "keypad") {
                mod.copyWith(
                    neym = sourceMod.neym,
                    kulor = sourceMod.kulor,
                    glefz = sourceMod.glefz,
                    glefKulorz = sourceMod.glefKulorz
                )
            } else mod
        }
    }

    fun copyModuleToBeldir(fromIndexInDayl: Int) {
        val sourceMod = modyilz.find { it.pozecon == fromIndexInDayl + 1 } ?: return
        val newId = "mod_${beldirModyilz.size + 1}_${getCurrentTimeMillis()}"
        val occupiedPozecons = beldirModyilz.map { it.pozecon }.toSet()
        var newPozecon = 4
        while (occupiedPozecons.contains(newPozecon)) {
            newPozecon++
        }
        val copiedMod = sourceMod.copyWith(
            id = newId,
            pozecon = newPozecon,
            ezAktiv = false
        )
        beldirModyilz = beldirModyilz + copiedMod
    }

    fun replaceModyil(fromPozecon: Int, toPozecon: Int) {
        val sourceMod = modyilz.find { it.pozecon == fromPozecon } ?: return
        modyilz = modyilz.map { mod ->
            if (mod.pozecon == toPozecon) {
                sourceMod.copyWith(pozecon = toPozecon)
            } else mod
        }.filter { it.pozecon != fromPozecon }
    }

    fun replaceBeldirModyil(fromPozecon: Int, toPozecon: Int) {
        val sourceMod = beldirModyilz.find { it.pozecon == fromPozecon } ?: return
        beldirModyilz = beldirModyilz.map { mod ->
            if (mod.pozecon == toPozecon) {
                sourceMod.copyWith(pozecon = toPozecon)
            } else mod
        }.filter { it.pozecon != fromPozecon }
    }

    fun muvBeldirModyilEntuFoldir(sourceIndex: Int, targetIndex: Int) {
        val sourceMod = beldirModyilz.find { it.pozecon == sourceIndex + 1 } ?: return
        val targetMod = beldirModyilz.find { it.pozecon == targetIndex + 1 } ?: return
        if (PROTECTED_IDS.contains(sourceMod.id)) return
        if (targetMod.type != "keypad") return
        val newGlefz = targetMod.glefz.toMutableList()
        val newKulorz = targetMod.glefKulorz.toMutableList()
        if (newGlefz.isEmpty()) {
            newGlefz.add(targetMod.neym)
            newKulorz.add(targetMod.kulorLong)
        }
        var emptyIdx = -1
        for (i in 1 until newGlefz.size) {
            if (newGlefz[i].isEmpty()) {
                emptyIdx = i
                break
            }
        }
        if (emptyIdx == -1) {
            emptyIdx = newGlefz.size
        }
        while (newGlefz.size <= emptyIdx) newGlefz.add("")
        while (newKulorz.size <= emptyIdx) newKulorz.add(0xFF333333)
        newGlefz[emptyIdx] = sourceMod.neym
        newKulorz[emptyIdx] = sourceMod.kulorLong
        beldirModyilz = beldirModyilz.map { m ->
            if (m.id == targetMod.id) {
                m.copyWith(glefz = newGlefz, glefKulorz = newKulorz)
            } else m
        }.filter { it.id != sourceMod.id }
    }

    fun muvModyilEntuFoldir(sourceIndex: Int, targetIndex: Int) {
        val sourceMod = modyilz.find { it.pozecon == sourceIndex + 1 } ?: return
        val targetMod = modyilz.find { it.pozecon == targetIndex + 1 } ?: return
        if (PROTECTED_IDS.contains(sourceMod.id)) return
        if (targetMod.type == "rebeld") {
            copyModuleToBeldir(sourceIndex)
            return
        }
        if (targetMod.type != "keypad") return
        val newGlefz = targetMod.glefz.toMutableList()
        val newKulorz = targetMod.glefKulorz.toMutableList()
        if (newGlefz.isEmpty()) {
            newGlefz.add(targetMod.neym)
            newKulorz.add(targetMod.kulorLong)
        }
        var emptyIdx = -1
        for (i in 1 until newGlefz.size) {
            if (newGlefz[i].isEmpty()) {
                emptyIdx = i
                break
            }
        }
        if (emptyIdx == -1) {
            emptyIdx = newGlefz.size
        }
        while (newGlefz.size <= emptyIdx) newGlefz.add("")
        while (newKulorz.size <= emptyIdx) newKulorz.add(0xFF333333)
        newGlefz[emptyIdx] = sourceMod.neym
        newKulorz[emptyIdx] = sourceMod.kulorLong
        modyilz = modyilz.map { m ->
            if (m.id == targetMod.id) {
                m.copyWith(glefz = newGlefz, glefKulorz = newKulorz)
            } else m
        }.filter { it.id != sourceMod.id }
    }

    fun reset() {
        modyilz = listOf(
            ModyilDeyda(id = "dayl", neym = "angol", kulorLong = Color(0xFF000000).toArgb().toLong(), pozecon = 1, ezAktiv = false, glefz = listOf("angol"), type = "hub"),
            ModyilDeyda(id = "keypad", neym = "kepad", kulorLong = Color(0xFFFF0000).toArgb().toLong(), pozecon = 2, glefz = listOf(" ") + modalz.HeksagonKonfeg.innerLetterMode + modalz.HeksagonKonfeg.outerTap, type = "keypad"),
            ModyilDeyda(id = "rebeld", neym = "rebeld", kulorLong = Color(0xFFFFFF00).toArgb().toLong(), pozecon = 3, type = "rebeld"),
            ModyilDeyda(id = "mod4", neym = "mod4", kulorLong = Color(0xFF00FF00).toArgb().toLong(), pozecon = 4, ezAktiv = false),
            ModyilDeyda(id = "mod5", neym = "mod5", kulorLong = Color(0xFF0000FF).toArgb().toLong(), pozecon = 5, ezAktiv = false),
            ModyilDeyda(id = "mod6", neym = "mod6", kulorLong = Color(0xFFFF00FF).toArgb().toLong(), pozecon = 6, ezAktiv = false)
        )
    }

    private fun Color.toArgbLong(): Long {
        return ((alpha * 255.0f).toInt().toLong() shl 24) or
               ((red * 255.0f).toInt().toLong() shl 16) or
               ((green * 255.0f).toInt().toLong() shl 8) or
               ((blue * 255.0f).toInt().toLong())
    }
}
