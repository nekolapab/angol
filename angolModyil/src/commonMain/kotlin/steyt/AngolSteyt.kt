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
        val PROTECTED_IDS = setOf("dayl")
    }

    var inputText by mutableStateOf("")
    var roteyconAngol by mutableStateOf(0.0)
    var isTextFieldFocused by mutableStateOf(false)

    var modyilz by mutableStateOf(listOf(
        ModyilDeyda(id = "dayl", neym = "dayl", kulorLong = Color(0xFFFF0000).toArgb().toLong(), pozecon = 2, ezAktiv = false, glefs = listOf("dayl"), type = "hub"),
        ModyilDeyda(
            id = "keypad", 
            neym = "kepad", 
            kulorLong = Color(0xFFFFFF00).toArgb().toLong(), 
            pozecon = 3, 
            ezAktiv = false,
            glefs = listOf(" ") + modalz.HeksagonKonfeg.innerLetterMode + modalz.HeksagonKonfeg.outerTap,
            glefKulorz = listOf(Color.White.toArgbLong()) + 
                         modalz.HeksagonKonfeg.innerRingColors.map { it.toArgbLong() } + 
                         modalz.HeksagonKonfeg.rainbowColors.map { it.toArgbLong() },
            type = "keypad"
        ),
        ModyilDeyda(id = "rebeld", neym = "rebeld", kulorLong = Color(0xFF00FF00).toArgb().toLong(), pozecon = 4, ezAktiv = false, type = "rebeld"),
        ModyilDeyda(id = "module3", neym = "mod 3", kulorLong = Color(0xFF00FF00).toArgb().toLong(), pozecon = 5, ezAktiv = false),
        ModyilDeyda(id = "module4", neym = "mod 4", kulorLong = Color(0xFF00FFFF).toArgb().toLong(), pozecon = 6, ezAktiv = false),
        ModyilDeyda(id = "module5", neym = "mod 5", kulorLong = Color(0xFF0000FF).toArgb().toLong(), pozecon = 7, ezAktiv = false),
        ModyilDeyda(id = "reset", neym = "reset", kulorLong = Color(0xFFFF0000).toArgb().toLong(), pozecon = 8, type = "reset"),
    ))

    var rebeldModyilz by mutableStateOf(listOf(
        ModyilDeyda(id = "dayl", neym = "dayl", kulorLong = Color(0xFFFF0000).toArgb().toLong(), pozecon = 2, ezAktiv = false, glefs = listOf("dayl"), type = "hub"),
        ModyilDeyda(id = "beldir", neym = "beldir", kulorLong = Color(0xFF00FFCC).toArgb().toLong(), pozecon = 3, ezAktiv = false, type = "keypad")
    ))

    val moduleHistory = mutableMapOf<String, MutableList<ModyilDeyda>>()
    val moduleRedo = mutableMapOf<String, MutableList<ModyilDeyda>>()
    val globalModyilzHistory = mutableListOf<List<ModyilDeyda>>()
    val globalRebeldHistory = mutableListOf<List<ModyilDeyda>>()
    val globalModyilzRedo = mutableListOf<List<ModyilDeyda>>()
    val globalRebeldRedo = mutableListOf<List<ModyilDeyda>>()

    private var lastModyilz = modyilz
    private var lastRebeldModyilz = rebeldModyilz

    fun recordState() {
        if (modyilz != lastModyilz) {
            globalModyilzHistory.add(lastModyilz)
            if (globalModyilzHistory.size > 20) globalModyilzHistory.removeAt(0)
            globalModyilzRedo.clear()
        }
        if (rebeldModyilz != lastRebeldModyilz) {
            globalRebeldHistory.add(lastRebeldModyilz)
            if (globalRebeldHistory.size > 20) globalRebeldHistory.removeAt(0)
            globalRebeldRedo.clear()
        }
        for (mod in lastModyilz) {
            val currentMod = modyilz.find { it.id == mod.id }
            if (currentMod != mod) {
                val history = moduleHistory.getOrPut(mod.id) { mutableListOf() }
                if (history.lastOrNull() != mod) {
                    history.add(mod)
                    if (history.size > 12) history.removeAt(0)
                    moduleRedo.remove(mod.id)
                }
            }
        }
        for (mod in lastRebeldModyilz) {
            val currentMod = rebeldModyilz.find { it.id == mod.id }
            if (currentMod != mod) {
                val history = moduleHistory.getOrPut(mod.id) { mutableListOf() }
                if (history.lastOrNull() != mod) {
                    history.add(mod)
                    if (history.size > 12) history.removeAt(0)
                    moduleRedo.remove(mod.id)
                }
            }
        }
        lastModyilz = modyilz
        lastRebeldModyilz = rebeldModyilz
    }
    val activeModule: ModyilDeyda?
        get() = modyilz.find { it.ezAktiv }

    var pendingResetTargetId by mutableStateOf<String?>(null)

    val ezKepadVezebil: Boolean
        get() = modyilz.any { it.type == "keypad" && it.ezAktiv }

    val ezRebeldVezebil: Boolean
        get() = modyilz.any { it.type == "rebeld" && it.ezAktiv }

    fun updateModules(newModules: List<ModyilDeyda>) {
        modyilz = newModules
    }

    fun updateRebeldModules(newModules: List<ModyilDeyda>) {
        rebeldModyilz = newModules.filter { it.type != "hub" && it.id != "dayl" }
    }

    fun activateModyil(id: String) {
        modyilz = modyilz.map { m ->
            m.copyWith(ezAktiv = (m.id == id))
        }
        recordState()
    }

    fun togilModyil(index: Int) {
        val tappedModule = modyilz.find { it.pozecon == index } ?: return
        val wasActive = tappedModule.ezAktiv
        modyilz = modyilz.map { m ->
            if (m.pozecon == index) m.copyWith(ezAktiv = !wasActive)
            else m.copyWith(ezAktiv = false)
        }
        recordState()
    }

    fun deactivateAll() {
        modyilz = modyilz.map { it.copyWith(ezAktiv = false) }
        rebeldModyilz = rebeldModyilz.map { it.copyWith(ezAktiv = false) }
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
        recordState()
    }

    fun deletModyil(id: String) {
        if (PROTECTED_IDS.contains(id)) return
        if (rebeldModyilz.any { it.id == id }) {
            rebeldModyilz = rebeldModyilz.filter { it.id != id }
        } else {
            modyilz = modyilz.filter { it.id != id }
        }
        recordState()
    }

    private fun convertAutoBackupToManual(modId: String): String {
        if (modId == "auto_backup") {
            val newId = "backup_${getCurrentTimeMillis()}"
            rebeldModyilz = rebeldModyilz.map { mod ->
                if (mod.id == "auto_backup") {
                    mod.copyWith(id = newId)
                } else mod
            }
            return newId
        }
        return modId
    }

    fun reneymModyil(id: String, newNeym: String) {
        val activeId = convertAutoBackupToManual(id)
        if (activeId == "rebeld") {
            rebeldModyilz = rebeldModyilz.map { if (it.id == activeId) it.copyWith(neym = newNeym) else it }
        } else {
            modyilz = modyilz.map { if (it.id == activeId) it.copyWith(neym = newNeym) else it }
        }
        recordState()
    }

    fun reneymGlef(modId: String, index: Int, newLabel: String) {
        val activeId = convertAutoBackupToManual(modId)
        if (rebeldModyilz.any { it.id == activeId }) {
            rebeldModyilz = rebeldModyilz.map { mod ->
                if (mod.id == activeId) {
                    val newGlefs = mod.glefs.toMutableList()
                    while (newGlefs.size <= index) newGlefs.add("")
                    val oldLabel = newGlefs.getOrNull(index) ?: ""
                    val updatedLabel = if (oldLabel.contains("|")) {
                        val rest = oldLabel.substringAfter("|")
                        "$newLabel|$rest"
                    } else {
                        newLabel
                    }
                    newGlefs[index] = updatedLabel
                    mod.copyWith(glefs = newGlefs)
                } else mod
            }
        } else {
            modyilz = modyilz.map { mod ->
                if (mod.id == activeId) {
                    val newGlefs = mod.glefs.toMutableList()
                    while (newGlefs.size <= index) newGlefs.add("")
                    val oldLabel = newGlefs.getOrNull(index) ?: ""
                    val updatedLabel = if (oldLabel.contains("|")) {
                        val rest = oldLabel.substringAfter("|")
                        "$newLabel|$rest"
                    } else {
                        newLabel
                    }
                    newGlefs[index] = updatedLabel
                    mod.copyWith(glefs = newGlefs)
                } else mod
            }
        }
        recordState()
    }

    fun muvGlef(modId: String, fromIndex: Int, toIndex: Int) {
        val activeId = convertAutoBackupToManual(modId)
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
        if (rebeldModyilz.any { it.id == activeId }) {
            rebeldModyilz = rebeldModyilz.map { if (it.id == activeId) updateFunc(it) else it }
        } else {
            modyilz = modyilz.map { if (it.id == activeId) updateFunc(it) else it }
        }
        recordState()
    }

    fun kopeGlefTuEmpt(modId: String, fromIndex: Int, toIndex: Int) {
        val activeId = convertAutoBackupToManual(modId)
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
        if (rebeldModyilz.any { it.id == activeId }) {
            rebeldModyilz = rebeldModyilz.map { if (it.id == activeId) updateFunc(it) else it }
        } else {
            modyilz = modyilz.map { if (it.id == activeId) updateFunc(it) else it }
        }
        recordState()
    }

    fun muvGlefTuHub(modId: String, glefIndex: Int) {
        val activeId = convertAutoBackupToManual(modId)
        val isRebeld = rebeldModyilz.any { it.id == activeId }
        val list = if (isRebeld) rebeldModyilz else modyilz
        val sourceMod = list.find { it.id == activeId } ?: return
        val glefLabel = sourceMod.glefs.getOrNull(glefIndex) ?: return
        if (glefLabel.isEmpty() || glefIndex == 0) return

        val newId = "mod_${list.size + 1}_${getCurrentTimeMillis()}"
        
        val occupiedPozecons = list.map { it.pozecon }.toSet()
        var newPozecon = sourceMod.pozecon + 1
        while (occupiedPozecons.contains(newPozecon)) {
            newPozecon++
        }
        
        val deserialized = deserializeMod(glefLabel)
        val newMod = if (deserialized != null) {
            deserialized.copyWith(pozecon = newPozecon)
        } else {
            ModyilDeyda(id = newId, neym = glefLabel, kulorLong = sourceMod.kulorLong, pozecon = newPozecon, glefs = listOf(glefLabel))
        }

        val updatedList = list.map { mod ->
            if (mod.id == activeId) {
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
        recordState()
    }

    fun replaceGlef(modId: String, fromIndex: Int, toIndex: Int) {
        val activeId = convertAutoBackupToManual(modId)
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
        if (rebeldModyilz.any { it.id == activeId }) {
            rebeldModyilz = rebeldModyilz.map { if (it.id == activeId) updateFunc(it) else it }
        } else {
            modyilz = modyilz.map { if (it.id == activeId) updateFunc(it) else it }
        }
        recordState()
    }

    fun swopModyilz(fromPozecon: Int, toPozecon: Int) {
        val sourceMod = modyilz.find { it.pozecon == fromPozecon }
        val targetMod = modyilz.find { it.pozecon == toPozecon }

        modyilz = modyilz.map { mod ->
            when (mod.pozecon) {
                fromPozecon -> mod.copyWith(pozecon = toPozecon)
                toPozecon -> mod.copyWith(pozecon = fromPozecon)
                else -> mod
            }
        }
        recordState()
    }

    fun swopRebeldModyilz(fromPozecon: Int, toPozecon: Int) {
        val sourceMod = rebeldModyilz.find { it.pozecon == fromPozecon }
        val targetMod = rebeldModyilz.find { it.pozecon == toPozecon }

        rebeldModyilz = rebeldModyilz.map { mod ->
            when (mod.pozecon) {
                fromPozecon -> mod.copyWith(pozecon = toPozecon)
                toPozecon -> mod.copyWith(pozecon = fromPozecon)
                else -> mod
            }
        }
        recordState()
    }


    /** Move a rebeld module to the next free sideline position (disconnected but not deleted). */
    fun muvRebeldModyilAwdirSpeys(pozecon: Int) {
        val modToMove = rebeldModyilz.find { it.pozecon == pozecon } ?: return
        val occupiedPozecons = rebeldModyilz.map { it.pozecon }.toSet()
        var newPozecon = (rebeldModyilz.maxOfOrNull { it.pozecon } ?: 0) + 1
        while (occupiedPozecons.contains(newPozecon)) newPozecon++
        rebeldModyilz = rebeldModyilz.map {
            if (it.pozecon == pozecon) it.copyWith(pozecon = newPozecon) else it
        }
        recordState()
    }

    /** Move a dayl module to the next free sideline position (disconnected). */
    fun muvModyilAwdirSpeys(pozecon: Int) {
        val modToMove = modyilz.find { it.pozecon == pozecon } ?: return
        val occupiedPozecons = modyilz.map { it.pozecon }.toSet()
        var newPozecon = maxOf(7, (modyilz.maxOfOrNull { it.pozecon } ?: 0)) + 1
        while (occupiedPozecons.contains(newPozecon)) newPozecon++
        modyilz = modyilz.map {
            if (it.pozecon == pozecon) it.copyWith(pozecon = newPozecon) else it
        }
        recordState()
    }

    fun kopeModyilTuEmpt(fromPozecon: Int, toPozecon: Int) {
        val modToCopy = modyilz.find { it.pozecon == fromPozecon } ?: return
        val targetMod = modyilz.find { it.pozecon == toPozecon }
        if (targetMod != null && (targetMod.type == "keypad" || targetMod.type == "beld" || targetMod.type == "rebeld" || targetMod.id == "beldir" || targetMod.id == "dayl" || targetMod.type == "hub")) {
            kopeModyilEntuFoldir(fromPozecon - 1, toPozecon - 1)
        } else {
            if (modToCopy.type == "rebeld" || modToCopy.id == "rebeld") {
                val newGlefs = mutableListOf<String>()
                val newKulorz = mutableListOf<Long>()
                newGlefs.add(modToCopy.neym)
                newKulorz.add(modToCopy.kulorLong)
                
                rebeldModyilz.forEach { m ->
                    newGlefs.add(serializeMod(m))
                    newKulorz.add(m.kulorLong)
                }
                
                val newId = "mod_${modyilz.size + 1}_${getCurrentTimeMillis()}"
                val snapshotMod = modToCopy.copyWith(
                    id = newId,
                    pozecon = toPozecon,
                    ezAktiv = false,
                    type = "keypad",
                    glefs = newGlefs,
                    glefKulorz = newKulorz
                )
                modyilz = modyilz.filter { it.pozecon != toPozecon } + snapshotMod
            } else {
                val newId = "mod_${modyilz.size + 1}_${getCurrentTimeMillis()}"
                modyilz = modyilz.filter { it.pozecon != toPozecon } + modToCopy.copyWith(id = newId, pozecon = toPozecon, ezAktiv = false)
            }
        }
        recordState()
    }

    fun kopeRebeldModyilTuEmpt(fromPozecon: Int, toPozecon: Int) {
        val modToCopy = rebeldModyilz.find { it.pozecon == fromPozecon } ?: return
        val targetMod = rebeldModyilz.find { it.pozecon == toPozecon }
        if (targetMod != null && (targetMod.type == "keypad" || targetMod.type == "beld" || targetMod.id == "beldir" || targetMod.id == "dayl" || targetMod.type == "hub")) {
            kopeRebeldModyilEntuFoldir(fromPozecon - 1, toPozecon - 1)
        } else {
            val newId = "mod_${rebeldModyilz.size + 1}_${getCurrentTimeMillis()}"
            rebeldModyilz = rebeldModyilz.filter { it.pozecon != toPozecon } + modToCopy.copyWith(id = newId, pozecon = toPozecon, ezAktiv = false)
        }
        recordState()
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
        recordState()
        return true
    }

    fun kopeModjilTuDaylKepad(fromIndexInRebeld: Int) {
        val sourceMod = rebeldModyilz.find { it.pozecon == fromIndexInRebeld + 1 } ?: return
        
        val currentKeypad = modyilz.find { it.type == "keypad" }
        if (currentKeypad != null) {
            val isDuplicate = rebeldModyilz.any { it.glefs == currentKeypad.glefs && it.glefKulorz == currentKeypad.glefKulorz }
            if (!isDuplicate) {
                rebeldModyilz = rebeldModyilz.map { 
                    if (it.id == "auto_backup") it.copyWith(id = "backup_${getCurrentTimeMillis()}") else it 
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
        recordState()
    }

    fun copyModuleToRebeld(fromIndexInDayl: Int, isMove: Boolean = false) {
        val sourceMod = modyilz.find { it.pozecon == fromIndexInDayl + 1 } ?: return
        val newId = if (isMove) sourceMod.id else "mod_${rebeldModyilz.size + 1}_${getCurrentTimeMillis()}"
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
        recordState()
    }

    private fun getUniqueNeym(baseNeym: String, list: List<ModyilDeyda>): String {
        var suffix = 2
        var candidate = "$baseNeym $suffix"
        while (list.any { it.neym == candidate }) {
            suffix++
            candidate = "$baseNeym $suffix"
        }
        return candidate
    }

    fun replaceModyil(fromPozecon: Int, toPozecon: Int, isMove: Boolean, renameTo: String? = null) {
        val sourceMod = if (isMove) modyilz.find { it.pozecon == fromPozecon } else rebeldModyilz.find { it.pozecon == fromPozecon }
        if (sourceMod == null) return
        val targetMod = modyilz.find { it.pozecon == toPozecon }
        
        val newId = if (isMove) sourceMod.id else "mod_${modyilz.size + 1}_${getCurrentTimeMillis()}"

        if (renameTo != null) {
            val newNeym = renameTo
            val isFolder = targetMod != null && (targetMod.type == "keypad" || targetMod.type == "beld" || targetMod.id == "beldir" || targetMod.id == "dayl" || targetMod.type == "hub" || targetMod.type == "rebeld")
            if (isFolder && targetMod != null) {
                val renamedMod = sourceMod.copyWith(id = newId, neym = newNeym)
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
                if (emptyIdx == -1) emptyIdx = newGlefs.size
                while (newGlefs.size <= emptyIdx) newGlefs.add("")
                while (newKulorz.size <= emptyIdx) newKulorz.add(0xFF333333)
                newGlefs[emptyIdx] = serializeMod(renamedMod)
                newKulorz[emptyIdx] = renamedMod.kulorLong
                
                var updated = modyilz.map { m ->
                    if (m.pozecon == toPozecon) m.copyWith(glefs = newGlefs, glefKulorz = newKulorz) else m
                }
                if (isMove) updated = updated.filter { it.pozecon != fromPozecon }
                modyilz = updated
                recordState()
                return
            }

            val occupiedPozecons = modyilz.map { it.pozecon }.toSet()
            var newPozecon = 5
            while (occupiedPozecons.contains(newPozecon)) {
                newPozecon++
            }
            val renamedMod = sourceMod.copyWith(id = newId, neym = newNeym, pozecon = newPozecon, ezAktiv = false)
            var updated = modyilz
            if (isMove) {
                updated = updated.filter { it.pozecon != fromPozecon }
            }
            modyilz = updated + renamedMod
            recordState()
            return
        }

        if (targetMod == null) {
            var updated = modyilz
            if (isMove) {
                updated = updated.map { if (it.pozecon == fromPozecon) it.copyWith(pozecon = toPozecon) else it }
            } else {
                updated = updated + sourceMod.copyWith(id = newId, pozecon = toPozecon, ezAktiv = false)
            }
            modyilz = updated
            recordState()
            return
        }

        if (PROTECTED_IDS.contains(targetMod.id)) {
            val targetId = convertAutoBackupToManual(targetMod.id)
            var updated = modyilz.map { mod ->
                if (mod.id == targetId) {
                    mod.copyWith(
                        neym = sourceMod.neym,
                        kulor = sourceMod.kulor,
                        glefs = sourceMod.glefs,
                        glefKulorz = sourceMod.glefKulorz,
                        type = sourceMod.type
                    )
                } else mod
            }
            if (isMove) {
                updated = updated.filter { it.pozecon != fromPozecon }
            }
            modyilz = updated
        } else {
            var updated = modyilz.map { mod ->
                if (mod.pozecon == toPozecon) {
                    sourceMod.copyWith(id = newId, pozecon = toPozecon)
                } else mod
            }
            if (isMove) {
                updated = updated.filter { it.pozecon != fromPozecon }
            }
            modyilz = updated
        }
        recordState()
    }

    fun replaceRebeldModyil(fromPozecon: Int, toPozecon: Int, isMove: Boolean, renameTo: String? = null) {
        val sourceMod = if (isMove) rebeldModyilz.find { it.pozecon == fromPozecon } else modyilz.find { it.pozecon == fromPozecon }
        if (sourceMod == null) return
        val targetMod = rebeldModyilz.find { it.pozecon == toPozecon }
        
        val newId = if (isMove) sourceMod.id else "mod_${rebeldModyilz.size + 1}_${getCurrentTimeMillis()}"

        if (renameTo != null) {
            val newNeym = renameTo
            val isFolder = targetMod != null && (targetMod.type == "keypad" || targetMod.type == "beld" || targetMod.id == "beldir" || targetMod.id == "dayl" || targetMod.type == "hub" || targetMod.type == "rebeld")
            if (isFolder && targetMod != null) {
                val renamedMod = sourceMod.copyWith(id = newId, neym = newNeym)
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
                if (emptyIdx == -1) emptyIdx = newGlefs.size
                while (newGlefs.size <= emptyIdx) newGlefs.add("")
                while (newKulorz.size <= emptyIdx) newKulorz.add(0xFF333333)
                newGlefs[emptyIdx] = serializeMod(renamedMod)
                newKulorz[emptyIdx] = renamedMod.kulorLong
                
                var updated = rebeldModyilz.map { m ->
                    if (m.pozecon == toPozecon) m.copyWith(glefs = newGlefs, glefKulorz = newKulorz) else m
                }
                if (isMove) updated = updated.filter { it.pozecon != fromPozecon }
                rebeldModyilz = updated
                recordState()
                return
            }

            val occupiedPozecons = rebeldModyilz.map { it.pozecon }.toSet()
            var newPozecon = 5
            while (occupiedPozecons.contains(newPozecon)) {
                newPozecon++
            }
            val renamedMod = sourceMod.copyWith(id = newId, neym = newNeym, pozecon = newPozecon, ezAktiv = false)
            var updated = rebeldModyilz
            if (isMove) {
                updated = updated.filter { it.pozecon != fromPozecon }
            }
            rebeldModyilz = updated + renamedMod
            recordState()
            return
        }

        if (targetMod == null) {
            var updated = rebeldModyilz
            if (isMove) {
                updated = updated.map { if (it.pozecon == fromPozecon) it.copyWith(pozecon = toPozecon) else it }
            } else {
                updated = updated + sourceMod.copyWith(id = newId, pozecon = toPozecon, ezAktiv = false)
            }
            rebeldModyilz = updated
            recordState()
            return
        }

        if (PROTECTED_IDS.contains(targetMod.id)) {
            val targetId = convertAutoBackupToManual(targetMod.id)
            var updated = rebeldModyilz.map { mod ->
                if (mod.id == targetId) {
                    mod.copyWith(
                        neym = sourceMod.neym,
                        kulor = sourceMod.kulor,
                        glefs = sourceMod.glefs,
                        glefKulorz = sourceMod.glefKulorz,
                        type = sourceMod.type
                    )
                } else mod
            }
            if (isMove) {
                updated = updated.filter { it.pozecon != fromPozecon }
            }
            rebeldModyilz = updated
        } else {
            var updated = rebeldModyilz.map { mod ->
                if (mod.pozecon == toPozecon) {
                    sourceMod.copyWith(id = newId, pozecon = toPozecon)
                } else mod
            }
            if (isMove) {
                updated = updated.filter { it.pozecon != fromPozecon }
            }
            rebeldModyilz = updated
        }
        recordState()
    }

    fun moveModuleToDayl(fromIndexInRebeld: Int) {
        val sourceMod = rebeldModyilz.find { it.pozecon == fromIndexInRebeld + 1 } ?: return
        val isMove = !PROTECTED_IDS.contains(sourceMod.id) || sourceMod.id == "beldir"
        val newId = if (isMove) sourceMod.id else "mod_${modyilz.size + 1}_${getCurrentTimeMillis()}"
        val occupiedPozecons = modyilz.map { it.pozecon }.toSet()
        
        val rebeldMod = modyilz.find { it.type == "rebeld" || it.id == "rebeld" }
        val basePozecon = rebeldMod?.pozecon ?: 4
        var newPozecon = basePozecon + 1
        while (occupiedPozecons.contains(newPozecon)) {
            newPozecon++
        }
        val copiedMod = sourceMod.copyWith(
            id = newId,
            pozecon = newPozecon,
            ezAktiv = false
        )
        modyilz = modyilz + copiedMod
        
        // Remove from rebeld if not protected OR if it's beldir
        if (!PROTECTED_IDS.contains(sourceMod.id) || sourceMod.id == "beldir") {
            rebeldModyilz = rebeldModyilz.filter { it.id != sourceMod.id }
        }
    }

    fun muvRebeldModyilEntuFoldir(sourceIndex: Int, targetIndex: Int) {
        val sourceMod = rebeldModyilz.find { it.pozecon == sourceIndex + 1 } ?: return
        val targetMod = rebeldModyilz.find { it.pozecon == targetIndex + 1 } ?: return
        if (PROTECTED_IDS.contains(sourceMod.id)) return
        val targetId = convertAutoBackupToManual(targetMod.id)
        if (targetMod.type != "keypad" && targetMod.type != "beld" && targetId != "beldir" && targetId != "keypad" && targetMod.id != "dayl" && targetMod.type != "hub") return
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
        newGlefs[emptyIdx] = serializeMod(sourceMod)
        newKulorz[emptyIdx] = sourceMod.kulorLong
        rebeldModyilz = rebeldModyilz.map { m ->
            if (m.id == targetId) {
                m.copyWith(glefs = newGlefs, glefKulorz = newKulorz)
            } else m
        }.filter { it.id != sourceMod.id }
    }

    fun muvModyilEntuFoldir(sourceIndex: Int, targetIndex: Int) {
        val sourceMod = modyilz.find { it.pozecon == sourceIndex + 1 } ?: return
        val targetMod = modyilz.find { it.pozecon == targetIndex + 1 } ?: return
        if (targetMod.type == "rebeld" && PROTECTED_IDS.contains(sourceMod.id) && sourceMod.id != "beldir") {
            copyModuleToRebeld(sourceIndex)
            return
        }
        if (PROTECTED_IDS.contains(sourceMod.id) && sourceMod.id != "beldir") return
        if (targetMod.type == "rebeld") {
            copyModuleToRebeld(sourceIndex, isMove = true)
            modyilz = modyilz.filter { it.id != sourceMod.id }
            return
        }
        if (targetMod.type != "keypad" && targetMod.type != "beld" && targetMod.id != "beldir") return
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
        newGlefs[emptyIdx] = serializeMod(sourceMod)
        newKulorz[emptyIdx] = sourceMod.kulorLong
        modyilz = modyilz.map { m ->
            if (m.id == targetMod.id) {
                m.copyWith(glefs = newGlefs, glefKulorz = newKulorz)
            } else m
        }.filter { it.id != sourceMod.id }
    }

    fun kopeRebeldModyilEntuFoldir(sourceIndex: Int, targetIndex: Int) {
        val sourceMod = rebeldModyilz.find { it.pozecon == sourceIndex + 1 } ?: return
        val targetMod = rebeldModyilz.find { it.pozecon == targetIndex + 1 } ?: return
        if (PROTECTED_IDS.contains(sourceMod.id)) return
        val targetId = convertAutoBackupToManual(targetMod.id)
        if (targetMod.type != "keypad" && targetMod.type != "beld" && targetId != "beldir" && targetId != "keypad" && targetMod.id != "dayl" && targetMod.type != "hub") return
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
        newGlefs[emptyIdx] = serializeMod(sourceMod)
        newKulorz[emptyIdx] = sourceMod.kulorLong
        rebeldModyilz = rebeldModyilz.map { m ->
            if (m.id == targetId) {
                m.copyWith(glefs = newGlefs, glefKulorz = newKulorz)
            } else m
        }
        recordState()
    }

    fun kopeRebeldTuDaylFoldir(sourceIndex: Int, targetIndex: Int) {
        val sourceMod = rebeldModyilz.find { it.pozecon == sourceIndex + 1 } ?: return
        val targetMod = modyilz.find { it.pozecon == targetIndex + 1 } ?: return
        val targetId = convertAutoBackupToManual(targetMod.id)
        if (targetMod.type != "keypad" && targetMod.type != "beld" && targetId != "beldir" && targetId != "keypad" && targetMod.id != "dayl" && targetMod.type != "hub") return
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
        newGlefs[emptyIdx] = serializeMod(sourceMod)
        newKulorz[emptyIdx] = sourceMod.kulorLong
        modyilz = modyilz.map { m ->
            if (m.id == targetId) {
                m.copyWith(glefs = newGlefs, glefKulorz = newKulorz)
            } else m
        }
        recordState()
    }

    fun kopeModyilEntuFoldir(sourceIndex: Int, targetIndex: Int) {
        val sourceMod = modyilz.find { it.pozecon == sourceIndex + 1 } ?: return
        val targetMod = modyilz.find { it.pozecon == targetIndex + 1 } ?: return
        if (PROTECTED_IDS.contains(sourceMod.id)) return
        if (targetMod.type == "rebeld") {
            copyModuleToRebeld(sourceIndex)
            return
        }
        if (targetMod.type != "keypad" && targetMod.type != "beld" && targetMod.id != "beldir" && targetMod.id != "keypad" && targetMod.id != "dayl" && targetMod.type != "hub") return
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
        newGlefs[emptyIdx] = serializeMod(sourceMod)
        newKulorz[emptyIdx] = sourceMod.kulorLong
        modyilz = modyilz.map { m ->
            if (m.id == targetMod.id) {
                m.copyWith(glefs = newGlefs, glefKulorz = newKulorz)
            } else m
        }
    }

    fun reset() {
        modyilz = listOf(
            ModyilDeyda(id = "dayl", neym = "dayl", kulorLong = Color(0xFFFF0000).toArgb().toLong(), pozecon = 2, ezAktiv = false, glefs = listOf("dayl"), type = "hub"),
            ModyilDeyda(id = "keypad", neym = "kepad", kulorLong = Color(0xFFFFFF00).toArgb().toLong(), pozecon = 3, glefs = listOf(" ") + modalz.HeksagonKonfeg.innerLetterMode + modalz.HeksagonKonfeg.outerTap, type = "keypad"),
            ModyilDeyda(id = "rebeld", neym = "rebeld", kulorLong = Color(0xFFFFFF00).toArgb().toLong(), pozecon = 4, type = "rebeld"),
            ModyilDeyda(id = "mod4", neym = "mod4", kulorLong = Color(0xFF00FF00).toArgb().toLong(), pozecon = 5, ezAktiv = false),
            ModyilDeyda(id = "mod5", neym = "mod5", kulorLong = Color(0xFF0000FF).toArgb().toLong(), pozecon = 6, ezAktiv = false),
            ModyilDeyda(id = "reset", neym = "reset", kulorLong = Color(0xFFFF0000).toArgb().toLong(), pozecon = 8, type = "reset")
        )
    }

    fun resetModyilTarget(id: String) {
        val defaultMod = when (id) {
            "dayl" -> ModyilDeyda(id = "dayl", neym = "dayl", kulorLong = Color(0xFFFF0000).toArgb().toLong(), pozecon = 2, ezAktiv = false, glefs = listOf("dayl"), type = "hub")
            "keypad" -> ModyilDeyda(id = "keypad", neym = "kepad", kulorLong = Color(0xFFFFFF00).toArgb().toLong(), pozecon = 3, glefs = listOf(" ") + modalz.HeksagonKonfeg.innerLetterMode + modalz.HeksagonKonfeg.outerTap, type = "keypad", glefKulorz = listOf(Color.White.toArgbLong()) + modalz.HeksagonKonfeg.innerRingColors.map { it.toArgbLong() } + modalz.HeksagonKonfeg.rainbowColors.map { it.toArgbLong() })
            "rebeld" -> ModyilDeyda(id = "rebeld", neym = "rebeld", kulorLong = Color(0xFF00FF00).toArgb().toLong(), pozecon = 4, type = "rebeld")
            "beldir" -> ModyilDeyda(id = "beldir", neym = "beldir", kulorLong = Color(0xFF00FFCC).toArgb().toLong(), pozecon = 3, ezAktiv = false, type = "keypad")
            else -> return
        }
        
        modyilz = modyilz.map { if (it.id == id) defaultMod.copyWith(pozecon = it.pozecon, ezAktiv = it.ezAktiv) else it }
        rebeldModyilz = rebeldModyilz.map { if (it.id == id) defaultMod.copyWith(pozecon = it.pozecon, ezAktiv = it.ezAktiv) else it }
        
        if (id == "rebeld") {
            rebeldModyilz = listOf(
                ModyilDeyda(id = "beldir", neym = "beldir", kulorLong = Color(0xFF00FFCC).toArgb().toLong(), pozecon = 3, ezAktiv = false, type = "beld")
            )
        }
        recordState()
    }

    fun undoModule(id: String) {
        if (id == "dayl") {
            if (globalModyilzHistory.isNotEmpty()) {
                val prevState = globalModyilzHistory.removeLast()
                globalModyilzRedo.add(modyilz)
                modyilz = prevState
                lastModyilz = prevState
            }
            return
        }
        if (id == "rebeld") {
            if (globalRebeldHistory.isNotEmpty()) {
                val prevState = globalRebeldHistory.removeLast()
                globalRebeldRedo.add(rebeldModyilz)
                rebeldModyilz = prevState
                lastRebeldModyilz = prevState
            }
            return
        }
        val history = moduleHistory[id]
        if (history.isNullOrEmpty()) {
            resetModyilTarget(id)
            return
        }
        val prevState = history.removeLast()
        val currentMod = modyilz.find { it.id == id } ?: rebeldModyilz.find { it.id == id }
        if (currentMod != null) {
            moduleRedo.getOrPut(id) { mutableListOf() }.add(currentMod)
            applyModuleState(id, prevState, currentMod.pozecon)
        }
    }

    fun redoModule(id: String) {
        if (id == "dayl") {
            if (globalModyilzRedo.isNotEmpty()) {
                val nextState = globalModyilzRedo.removeLast()
                globalModyilzHistory.add(modyilz)
                modyilz = nextState
                lastModyilz = nextState
            }
            return
        }
        if (id == "rebeld") {
            if (globalRebeldRedo.isNotEmpty()) {
                val nextState = globalRebeldRedo.removeLast()
                globalRebeldHistory.add(rebeldModyilz)
                rebeldModyilz = nextState
                lastRebeldModyilz = nextState
            }
            return
        }
        val redoStack = moduleRedo[id] ?: return
        if (redoStack.isEmpty()) return
        
        val nextState = redoStack.removeLast()
        val currentMod = modyilz.find { it.id == id } ?: rebeldModyilz.find { it.id == id }
        if (currentMod != null) {
            val history = moduleHistory.getOrPut(id) { mutableListOf() }
            history.add(currentMod)
            if (history.size > 12) history.removeAt(0)
            applyModuleState(id, nextState, currentMod.pozecon)
        }
    }

    private fun applyModuleState(id: String, state: ModyilDeyda, oldPozecon: Int) {
        val isModyilz = modyilz.any { it.id == id }
        val targetList = if (isModyilz) modyilz else rebeldModyilz
        
        val occupant = targetList.find { it.pozecon == state.pozecon && it.id != id }
        
        var updated = targetList.map { mod ->
            if (mod.id == id) state
            else if (occupant != null && mod.id == occupant.id) occupant.copyWith(pozecon = oldPozecon)
            else mod
        }
        
        if (isModyilz) {
            modyilz = updated
            lastModyilz = updated
        } else {
            rebeldModyilz = updated
            lastRebeldModyilz = updated
        }
    }

    fun resetAktevModyil() {
        val active = activeModule ?: return
        resetModyilTarget(active.id)
    }

    private fun Color.toArgbLong(): Long {
        return ((alpha * 255.0f).toInt().toLong() shl 24) or
               ((red * 255.0f).toInt().toLong() shl 16) or
               ((green * 255.0f).toInt().toLong() shl 8) or
               ((blue * 255.0f).toInt().toLong())
    }

    fun serializeMod(mod: ModyilDeyda): String {
        val glefsStr = mod.glefs.joinToString("\u0001")
        val colorsStr = mod.glefKulorz.joinToString("\u0001")
        val cleanNeym = mod.neym.replace("|", " ")
        return "$cleanNeym|${mod.id}\u0002${mod.kulorLong}\u0002${mod.pozecon}\u0002${mod.ezAktiv}\u0002${mod.type}\u0002$glefsStr\u0002$colorsStr"
    }

    fun deserializeMod(serialized: String): ModyilDeyda? {
        if (!serialized.contains("|")) return null
        val neym = serialized.substringBefore("|")
        val rest = serialized.substringAfter("|")
        val parts = rest.split("\u0002")
        if (parts.size < 6) return null
        val id = parts[0]
        val kulorLong = parts[1].toLongOrNull() ?: 0L
        val pozecon = parts[2].toIntOrNull() ?: 0
        val ezAktiv = parts[3].toBoolean()
        val type = parts[4]
        val glefs = if (parts[5].isEmpty()) emptyList() else parts[5].split("\u0001")
        val glefKulorz = if (parts.size > 6 && parts[6].isNotEmpty()) parts[6].split("\u0001").mapNotNull { it.toLongOrNull() } else emptyList()
        return ModyilDeyda(id, neym, kulorLong, pozecon, ezAktiv, glefs, glefKulorz, type)
    }
}
