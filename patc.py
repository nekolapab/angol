import re

file_path = r"C:\Users\nicli\angol\angolModyil\src\commonMain\kotlin\steyt\AngolSteyt.kt"

with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

# Replace replaceModyil
replace_mod_pattern = r"""(        if \(renameTo != null\) \{
            val newNeym = renameTo
            val occupiedPozecons = modyilz\.map \{ it\.pozecon \}\.toSet\(\)
            var newPozecon = 5
            while \(occupiedPozecons\.contains\(newPozecon\)\) \{
                newPozecon\+\+
            \}
            val renamedMod = sourceMod\.copyWith\(id = newId, neym = newNeym, pozecon = newPozecon, ezAktiv = false\)
            var updated = modyilz
            if \(isMove\) \{
                updated = updated\.filter \{ it\.pozecon != fromPozecon \}
            \}
            modyilz = updated \+ renamedMod
            recordState\(\)
            return
        \})"""

replace_mod_new = """        if (renameTo != null) {
            val newNeym = renameTo
            val isFolder = targetMod != null && (targetMod.type == "keypad" || targetMod.type == "beld" || targetMod.id == "beldir" || targetMod.id == "dayl" || targetMod.type == "hub" || targetMod.type == "rebeld")
            if (isFolder) {
                val renamedMod = sourceMod.copyWith(id = newId, neym = newNeym)
                val newGlefs = targetMod!!.glefs.toMutableList()
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
                    if (m.pozecon == toPozecon) {
                        m.copyWith(glefs = newGlefs, glefKulorz = newKulorz)
                    } else m
                }
                if (isMove) {
                    updated = updated.filter { it.pozecon != fromPozecon }
                }
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
        }"""

# Replace replaceRebeldModyil
replace_rebeld_pattern = r"""(        if \(renameTo != null\) \{
            val newNeym = renameTo
            val occupiedPozecons = rebeldModyilz\.map \{ it\.pozecon \}\.toSet\(\)
            var newPozecon = 5
            while \(occupiedPozecons\.contains\(newPozecon\)\) \{
                newPozecon\+\+
            \}
            val renamedMod = sourceMod\.copyWith\(id = newId, neym = newNeym, pozecon = newPozecon, ezAktiv = false\)
            var updated = rebeldModyilz
            if \(isMove\) \{
                updated = updated\.filter \{ it\.pozecon != fromPozecon \}
            \}
            rebeldModyilz = updated \+ renamedMod
            recordState\(\)
            return
        \})"""

replace_rebeld_new = """        if (renameTo != null) {
            val newNeym = renameTo
            val isFolder = targetMod != null && (targetMod.type == "keypad" || targetMod.type == "beld" || targetMod.id == "beldir" || targetMod.id == "dayl" || targetMod.type == "hub" || targetMod.type == "rebeld")
            if (isFolder) {
                val renamedMod = sourceMod.copyWith(id = newId, neym = newNeym)
                val newGlefs = targetMod!!.glefs.toMutableList()
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
                    if (m.pozecon == toPozecon) {
                        m.copyWith(glefs = newGlefs, glefKulorz = newKulorz)
                    } else m
                }
                if (isMove) {
                    updated = updated.filter { it.pozecon != fromPozecon }
                }
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
        }"""

content = re.sub(replace_mod_pattern, replace_mod_new, content)
content = re.sub(replace_rebeld_pattern, replace_rebeld_new, content)

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)
print("Patched successfully!")
