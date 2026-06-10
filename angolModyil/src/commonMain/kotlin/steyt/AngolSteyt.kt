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
        ModyilDeyda(id = "dayl", neym = "angol", kulorLong = Color(0xFF000000).toArgb().toLong(), pozecon = 1, ezAktiv = false, glefs = listOf("angol"), type = "hub"),
        ModyilDeyda(
            id = "keypad", 
            neym = "kepad", 
            kulorLong = Color(0xFFFF0000).toArgb().toLong(), 
            pozecon = 2, 
            ezAktiv = false,
            glefs = listOf(" ") + modalz.HeksagonKonfeg.innerLetterMode + modalz.HeksagonKonfeg.outerTap,
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

    var rebeldModyilz by mutableStateOf(listOf(
        ModyilDeyda(id = "dayl", neym = "angol", kulorLong = Color(0xFF000000).toArgb().toLong(), pozecon = 1, ezAktiv = false, glefs = listOf("angol"), type = "hub")
    ))



    val activeModule: ModyilDeyda?
        get() = modyilz.find { it.ezAktiv && it.type != "hub" }

    val ezKepadVezebil: Boolean
        get() = modyilz.any { it.type == "keypad" && it.ezAktiv }

    val ezRebeldVezebil: Boolean
        get() = modyilz.any { it.type == "rebeld" && it.ezAktiv }

    fun updateModules(newModules: List<ModyilDeyda>) {
        modyilz = newModules
    }

    fun updateRebeldModules(newModules: List<ModyilDeyda>) {
        rebeldModyilz = newModules
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
        if (rebeldModyilz.any { it.id == id }) {
            val modToCopy = rebeldModyilz.find { it.id == id } ?: return
            val newId = "mod_${rebeldModyilz.size + 1}_${getCurrentTimeMillis()}"
            val newPozecon = (rebeldModyilz.maxOfOrNull { it.pozecon } ?: 0) + 1
            rebeldModyilz = rebeldModyilz + modToCopy.copyWith(id = newId, pozecon = newPozecon, ezAktiv = false)
        } else {
            val modToCopy = modyilz.find { it.id == id } ?: return
            val newId = "mod_${modyilz.size + 1}_${getCurrentTimeMillis()}"
            val newPozecon = (modyilz.maxOfOrNull { it.pozecon } ?: 0) + 1
            modyilz = modyilz + modToCopy.copyWith(id = newId, pozecon = newPozecon, ezAktiv = false)
        }
    }

    fun deletModyil(id: String) {
        if (PROTECTED_IDS.contains(id)) return
        if (rebeldModyilz.any { it.id == id }) {
            rebeldModyilz = rebeldModyilz.filter { it.id != id }
        } else {
            modyilz = modyilz.filter { it.id != id }
        }
    }

    private fun convertAutoBackupToManual(modId: String) {
        if (modId == "auto_backup") {
            rebeldModyilz = rebeldModyilz.map { mod ->
                if (mod.id == "auto_backup") {
                    mod.copyWith(id = "backup_${getCurrentTimeMillis()}", neym = "Backup ${getCurrentTimeMillis() % 1000}")
                } else mod
            }
        }
    }

    fun reneymModyil(id: String, newNeym: String) {
        convertAutoBackupToManual(id)
        if (rebeldModyilz.any { it.id == id }) {
            rebeldModyilz = rebeldModyilz.map { if (it.id == id) it.copyWith(neym = newNeym) else it }
        } else {
            modyilz = modyilz.map { if (it.id == id) it.copyWith(neym = newNeym) else it }
        }
    }

    fun reneymGlef(modId: String, index: Int, newLabel: String) {
        convertAutoBackupToManual(modId)
        if (rebeldModyilz.any { it.id == modId }) {
            rebeldModyilz = rebeldModyilz.map { mod ->
                if (mod.id == modId) {
                    val newGlefs = mod.glefs.toMutableList()
                    while (newGlefs.size <= index) newGlefs.add("")
                    newGlefs[index] = newLabel
                    mod.copyWith(glefs = newGlefs)
                } else mod
            }
        } else {
            modyilz = modyilz.map { mod ->
                if (mod.id == modId) {
                    val newGlefs = mod.glefs.toMutableList()
                    while (newGlefs.size <= index) newGlefs.add("")
                    newGlefs[index] = newLabel
                    mod.copyWith(glefs = newGlefs)
                } else mod
            }
        }
    }

    fun muvGlef(modId: String, fromIndex: Int, toIndex: Int) {
        convertAutoBackupToManual(modId)
        val updateFunc = { mod: ModyilDeyda ->
            val newGlefs = mod.glefs.toMutableList()
            val newKulorz = mod.glefKulorz.toMutableList()
            if (toIndex == -1) {
                if (fromIndex in newGlefs.indices) newGlefs[fromIndex] = ""
                if (fromIndex in newKulorz.indices) newKulorz[fromIndex] = 0xFF333333
            } else {
                val maxIdx = maxOf(fromIndex, toIndex)
                while (newGlefs.size <= maxIdx) newGlefs.add("")
                while (newKulorz.size <= maxIdx) newKulorz.add(0xFF333333)
                
                // SWAP labels and colors
                val tempGlef = newGlefs[toIndex]
                val tempKulor = newKulorz[toIndex]
                
                newGlefs[toIndex] = newGlefs[fromIndex]
                newKulorz[toIndex] = newKulorz[fromIndex]
                
                newGlefs[fromIndex] = tempGlef
                newKulorz[fromIndex] = tempKulor
            }
            
            mod.copyWith(glefs = newGlefs, glefKulorz = newKulorz)
        }
        if (rebeldModyilz.any { it.id == modId }) {
            rebeldModyilz = rebeldModyilz.map { if (it.id == modId) updateFunc(it) else it }
        } else {
            modyilz = modyilz.map { if (it.id == modId) updateFunc(it) else it }
        }
    }

    fun kopeGlefTuEmpt(modId: String, fromIndex: Int, toIndex: Int) {
        convertAutoBackupToManual(modId)
        val updateFunc = { mod: ModyilDeyda ->
            val newGlefs = mod.glefs.toMutableList()
            val newKulorz = mod.glefKulorz.toMutableList()
            if (toIndex != -1) {
                while (newGlefs.size <= toIndex) newGlefs.add("")
                while (newKulorz.size <= toIndex) newKulorz.add(mod.kulor.toArgbLong())
                if (fromIndex in newGlefs.indices) {
                    newGlefs[toIndex] = newGlefs[fromIndex]
                    val sourceColor = if (fromIndex < newKulorz.size) newKulorz[fromIndex] else mod.kulor.toArgbLong()
                    newKulorz[toIndex] = sourceColor
                }
            }
            mod.copyWith(glefs = newGlefs, glefKulorz = newKulorz)
        }
        if (rebeldModyilz.any { it.id == modId }) {
            rebeldModyilz = rebeldModyilz.map { if (it.id == modId) updateFunc(it) else it }
        } else {
            modyilz = modyilz.map { if (it.id == modId) updateFunc(it) else it }
        }
    }

    fun muvGlefTuHub(modId: String, glefIndex: Int) {
        convertAutoBackupToManual(modId)
        val isRebeld = rebeldModyilz.any { it.id == modId }
        val list = if (isRebeld) rebeldModyilz else modyilz
        val sourceMod = list.find { it.id == modId } ?: return
        val glefLabel = sourceMod.glefs.getOrNull(glefIndex) ?: return
        if (glefLabel.isEmpty() || glefIndex == 0) return

        val newId = "mod_${list.size + 1}_${getCurrentTimeMillis()}"
        val newPozecon = (list.maxOfOrNull { it.pozecon } ?: 0) + 1
        val newMod = ModyilDeyda(id = newId, neym = glefLabel, kulorLong = sourceMod.kulorLong, pozecon = newPozecon, glefs = listOf(glefLabel))

        val updatedList = list.map { mod ->
            if (mod.id == modId) {
                val newGlefs = mod.glefs.toMutableList()
                val newKulorz = mod.glefKulorz.toMutableList()
                newGlefs[glefIndex] = ""
                if (glefIndex in newKulorz.indices) newKulorz[glefIndex] = mod.kulorLong
                mod.copyWith(glefs = newGlefs, glefKulorz = newKulorz)
            } else mod
        } + newMod

        if (isRebeld) {
            rebeldModyilz = updatedList
        } else {
            modyilz = updatedList
        }
    }

    fun replaceGlef(modId: String, fromIndex: Int, toIndex: Int) {
        convertAutoBackupToManual(modId)
        val updateFunc = { mod: ModyilDeyda ->
            val newGlefs = mod.glefs.toMutableList()
            val newKulorz = mod.glefKulorz.toMutableList()
            if (toIndex == -1) {
                if (fromIndex in newGlefs.indices) newGlefs[fromIndex] = ""
                if (fromIndex in newKulorz.indices) newKulorz[fromIndex] = 0xFF333333
            } else {
                val maxIdx = maxOf(fromIndex, toIndex)
                while (newGlefs.size <= maxIdx) newGlefs.add("")
                while (newKulorz.size <= maxIdx) newKulorz.add(0xFF333333)
                newGlefs[toIndex] = newGlefs[fromIndex]
                newKulorz[toIndex] = newKulorz[fromIndex]
                newGlefs[fromIndex] = ""
                newKulorz[fromIndex] = 0xFF333333
            }
            mod.copyWith(glefs = newGlefs, glefKulorz = newKulorz)
        }
        if (rebeldModyilz.any { it.id == modId }) {
            rebeldModyilz = rebeldModyilz.map { if (it.id == modId) updateFunc(it) else it }
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

    fun swopRebeldModyilz(fromPozecon: Int, toPozecon: Int) {
        rebeldModyilz = rebeldModyilz.map { mod ->
            when (mod.pozecon) {
                fromPozecon -> mod.copyWith(pozecon = toPozecon)
                toPozecon -> mod.copyWith(pozecon = fromPozecon)
                else -> mod
            }
        }
    }


    /** Move a rebeld module to the next free sideline position (disconnected but not deleted). */
    fun muvRebeldModjilAwdirSpeys(pozecon: Int) {
        val mod = rebeldModyilz.find { it.pozecon == pozecon } ?: return
        if (PROTECTED_IDS.contains(mod.id)) return
        val occupiedPozecons = rebeldModyilz.map { it.pozecon }.toSet()
        var newPozecon = (rebeldModyilz.maxOfOrNull { it.pozecon } ?: 0) + 1
        while (occupiedPozecons.contains(newPozecon)) newPozecon++
        rebeldModyilz = rebeldModyilz.map {
            if (it.pozecon == pozecon) it.copyWith(pozecon = newPozecon) else it
        }
    }

    fun kopeModyilTuEmpt(fromPozecon: Int, toPozecon: Int) {
        val modToCopy = modyilz.find { it.pozecon == fromPozecon } ?: return
        val newId = "mod_${modyilz.size + 1}_${getCurrentTimeMillis()}"
        modyilz = modyilz + modToCopy.copyWith(id = newId, pozecon = toPozecon, ezAktiv = false)
    }

    fun kopeRebeldModyilTuEmpt(fromPozecon: Int, toPozecon: Int) {
        val modToCopy = rebeldModyilz.find { it.pozecon == fromPozecon } ?: return
        val newId = "mod_${rebeldModyilz.size + 1}_${getCurrentTimeMillis()}"
        rebeldModyilz = rebeldModyilz + modToCopy.copyWith(id = newId, pozecon = toPozecon, ezAktiv = false)
    }

    fun muvModyilTuParent(index: Int) {
        // No-op for Hub
    }

    fun kreyeytBakupEfNeded(): Boolean {
        val currentKeypad = modyilz.find { it.type == "keypad" } ?: return false
        val existingAutoBackup = rebeldModyilz.find { it.id == "auto_backup" }
        if (existingAutoBackup != null) return false
        
        val isDuplicate = rebeldModyilz.any { it.glefs == currentKeypad.glefs && it.glefKulorz == currentKeypad.glefKulorz }
        if (isDuplicate) return false
        
        val newPozecon = (rebeldModyilz.maxOfOrNull { it.pozecon } ?: 0) + 1
        val backupMod = currentKeypad.copyWith(
            id = "auto_backup",
            neym = "kepad beld",
            pozecon = newPozecon,
            ezAktiv = false
        )
        rebeldModyilz = rebeldModyilz + backupMod
        return true
    }

    fun kopeModjilTuDaylKepad(fromIndexInRebeld: Int) {
        val sourceMod = rebeldModyilz.find { it.pozecon == fromIndexInRebeld + 1 } ?: return
        
        val currentKeypad = modyilz.find { it.type == "keypad" }
        if (currentKeypad != null) {
            val isDuplicate = rebeldModyilz.any { it.glefs == currentKeypad.glefs && it.glefKulorz == currentKeypad.glefKulorz }
            if (!isDuplicate) {
                rebeldModyilz = rebeldModyilz.map { 
                    if (it.id == "auto_backup") it.copyWith(id = "backup_${getCurrentTimeMillis()}", neym = "Backup ${getCurrentTimeMillis() % 1000}") else it 
                }
                val newPozecon = (rebeldModyilz.maxOfOrNull { it.pozecon } ?: 0) + 1
                val backupMod = currentKeypad.copyWith(
                    id = "auto_backup",
                    neym = "kepad beld",
                    pozecon = newPozecon,
                    ezAktiv = false
                )
                rebeldModyilz = rebeldModyilz + backupMod
            }
        }

        modyilz = modyilz.map { mod ->
            if (mod.type == "keypad") {
                mod.copyWith(
                    neym = sourceMod.neym,
                    kulor = sourceMod.kulor,
                    glefs = sourceMod.glefs,
                    glefKulorz = sourceMod.glefKulorz
                )
            } else mod
        }
    }

    fun copyModuleToRebeld(fromIndexInDayl: Int) {
        val sourceMod = modyilz.find { it.pozecon == fromIndexInDayl + 1 } ?: return
        val newId = "mod_${rebeldModyilz.size + 1}_${getCurrentTimeMillis()}"
        val occupiedPozecons = rebeldModyilz.map { it.pozecon }.toSet()
        var newPozecon = 4
        while (occupiedPozecons.contains(newPozecon)) {
            newPozecon++
        }
        val copiedMod = sourceMod.copyWith(
            id = newId,
            pozecon = newPozecon,
            ezAktiv = false
        )
        rebeldModyilz = rebeldModyilz + copiedMod
    }

    fun replaceModyil(fromPozecon: Int, toPozecon: Int) {
        val sourceMod = modyilz.find { it.pozecon == fromPozecon } ?: return
        modyilz = modyilz.map { mod ->
            if (mod.pozecon == toPozecon) {
                sourceMod.copyWith(pozecon = toPozecon)
            } else mod
        }.filter { it.pozecon != fromPozecon }
    }

    fun replaceRebeldModyil(fromPozecon: Int, toPozecon: Int) {
        val sourceMod = rebeldModyilz.find { it.pozecon == fromPozecon } ?: return
        rebeldModyilz = rebeldModyilz.map { mod ->
            if (mod.pozecon == toPozecon) {
                sourceMod.copyWith(pozecon = toPozecon)
            } else mod
        }.filter { it.pozecon != fromPozecon }
    }

    fun muvRebeldModyilEntuFoldir(sourceIndex: Int, targetIndex: Int) {
        val sourceMod = rebeldModyilz.find { it.pozecon == sourceIndex + 1 } ?: return
        val targetMod = rebeldModyilz.find { it.pozecon == targetIndex + 1 } ?: return
        if (PROTECTED_IDS.contains(sourceMod.id)) return
        if (targetMod.type != "keypad") return
        convertAutoBackupToManual(targetMod.id)
        val newGlefs = targetMod.glefs.toMutableList()
        val newKulorz = targetMod.glefKulorz.toMutableList()
        if (newGlefs.isEmpty()) {
            newGlefs.add(targetMod.neym)
            newKulorz.add(targetMod.kulorLong)
        }
        var emptyIdx = -1
        for (i in 1 until newGlefs.size) {
            if (newGlefs[i].isEmpty()) {
                emptyIdx = i
                break
            }
        }
        if (emptyIdx == -1) {
            emptyIdx = newGlefs.size
        }
        while (newGlefs.size <= emptyIdx) newGlefs.add("")
        while (newKulorz.size <= emptyIdx) newKulorz.add(0xFF333333)
        newGlefs[emptyIdx] = sourceMod.neym
        newKulorz[emptyIdx] = sourceMod.kulorLong
        rebeldModyilz = rebeldModyilz.map { m ->
            if (m.id == targetMod.id) {
                m.copyWith(glefs = newGlefs, glefKulorz = newKulorz)
            } else m
        }.filter { it.id != sourceMod.id }
    }

    fun muvModyilEntuFoldir(sourceIndex: Int, targetIndex: Int) {
        val sourceMod = modyilz.find { it.pozecon == sourceIndex + 1 } ?: return
        val targetMod = modyilz.find { it.pozecon == targetIndex + 1 } ?: return
        if (targetMod.type == "rebeld" && sourceMod.type == "keypad") {
            copyModuleToRebeld(sourceIndex)
            return
        }
        if (PROTECTED_IDS.contains(sourceMod.id)) return
        if (targetMod.type == "rebeld") {
            copyModuleToRebeld(sourceIndex)
            return
        }
        if (targetMod.type != "keypad") return
        val newGlefs = targetMod.glefs.toMutableList()
        val newKulorz = targetMod.glefKulorz.toMutableList()
        if (newGlefs.isEmpty()) {
            newGlefs.add(targetMod.neym)
            newKulorz.add(targetMod.kulorLong)
        }
        var emptyIdx = -1
        for (i in 1 until newGlefs.size) {
            if (newGlefs[i].isEmpty()) {
                emptyIdx = i
                break
            }
        }
        if (emptyIdx == -1) {
            emptyIdx = newGlefs.size
        }
        while (newGlefs.size <= emptyIdx) newGlefs.add("")
        while (newKulorz.size <= emptyIdx) newKulorz.add(0xFF333333)
        newGlefs[emptyIdx] = sourceMod.neym
        newKulorz[emptyIdx] = sourceMod.kulorLong
        modyilz = modyilz.map { m ->
            if (m.id == targetMod.id) {
                m.copyWith(glefs = newGlefs, glefKulorz = newKulorz)
            } else m
        }.filter { it.id != sourceMod.id }
    }

    fun reset() {
        modyilz = listOf(
            ModyilDeyda(id = "dayl", neym = "angol", kulorLong = Color(0xFF000000).toArgb().toLong(), pozecon = 1, ezAktiv = false, glefs = listOf("angol"), type = "hub"),
            ModyilDeyda(id = "keypad", neym = "kepad", kulorLong = Color(0xFFFF0000).toArgb().toLong(), pozecon = 2, glefs = listOf(" ") + modalz.HeksagonKonfeg.innerLetterMode + modalz.HeksagonKonfeg.outerTap, type = "keypad"),
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
