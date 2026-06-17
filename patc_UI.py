import re

file_path_dayl = r"C:\Users\nicli\angol\angolDaylAp\src\main\kotlin\skrenz\DaylSkren.kt"
file_path_rebeld = r"C:\Users\nicli\angol\angolRebeldAp\src\commonMain\kotlin\modyilz\Rebeld.kt"

# Patch DaylSkren.kt
with open(file_path_dayl, "r", encoding="utf-8") as f:
    content = f.read()

# 1. BeldWedjet onDropOnFoldir (line 521)
pattern1 = r"""(                        onDropOnFoldir = \{ from, to, isMove ->
                            if \(isMove\) \{
                                if \(daylSteyt\.kreyeytBakupEfNeded\(\)\) onSaveLayout\("rebeld_state"\)
                                daylSteyt\.muvModyilEntuFoldir\(from, to\)
                            \} else \{
                                daylSteyt\.kopeModyilEntuFoldir\(from, to\)
                            \}
                            onSaveLayout\("current"\)
                        \},)"""

new1 = """                        onDropOnFoldir = { from, to, isMove ->
                            val sourceMod = daylSteyt.modyilz.find { it.pozecon == from + 1 }
                            if (sourceMod?.type == "reset" || sourceMod?.id == "reset") {
                                val targetMod = daylSteyt.modyilz.find { it.pozecon == to + 1 }
                                daylSteyt.pendingResetTargetId = targetMod?.id ?: "dayl"
                            } else {
                                if (isMove) {
                                    if (daylSteyt.kreyeytBakupEfNeded()) onSaveLayout("rebeld_state")
                                    daylSteyt.muvModyilEntuFoldir(from, to)
                                } else {
                                    daylSteyt.kopeModyilEntuFoldir(from, to)
                                }
                                onSaveLayout("current")
                            }
                        },"""

# 2. BeldWedjet onReplace (line 530)
pattern2 = r"""(                        onReplace = \{ from, to, isMove, _ ->
                            if \(daylSteyt\.kreyeytBakupEfNeded\(\)\) onSaveLayout\("rebeld_state"\)
                            daylSteyt\.replaceGlef\(mod\.id, from, to\)
                            onSaveLayout\("current"\)
                        \},)"""

new2 = """                        onReplace = { from, to, isMove, _ ->
                            val sourceMod = daylSteyt.modyilz.find { it.pozecon == from + 1 }
                            if (sourceMod?.type == "reset" || sourceMod?.id == "reset") {
                                val targetMod = daylSteyt.modyilz.find { it.pozecon == to + 1 }
                                daylSteyt.pendingResetTargetId = targetMod?.id ?: "dayl"
                            } else {
                                if (daylSteyt.kreyeytBakupEfNeded()) onSaveLayout("rebeld_state")
                                daylSteyt.replaceGlef(mod.id, from, to)
                                onSaveLayout("current")
                            }
                        },"""

# 3. Rebeld onDropOnFoldir (line 553)
pattern3 = r"""(                onDropOnFoldir = \{ from, to, isMove ->
                    if \(isMove\) \{
                        daylSteyt\.muvRebeldModyilEntuFoldir\(from, to\)
                    \} else \{
                        daylSteyt\.kopeRebeldModyilEntuFoldir\(from, to\)
                    \}
                    onSaveLayout\("current"\)
                \},)"""

new3 = """                onDropOnFoldir = { from, to, isMove ->
                    val sourceMod = daylSteyt.rebeldModyilz.find { it.pozecon == from + 1 }
                    if (sourceMod?.type == "reset" || sourceMod?.id == "reset") {
                        val targetMod = daylSteyt.modyilz.find { it.pozecon == to + 1 } ?: daylSteyt.rebeldModyilz.find { it.pozecon == to + 1 }
                        daylSteyt.pendingResetTargetId = targetMod?.id ?: "rebeld"
                    } else {
                        if (isMove) {
                            daylSteyt.muvRebeldModyilEntuFoldir(from, to)
                        } else {
                            daylSteyt.kopeRebeldModyilEntuFoldir(from, to)
                        }
                        onSaveLayout("current")
                    }
                },"""

# 4. Rebeld onReplace (line 561)
pattern4 = r"""(                onReplace = \{ from, to, isMove, renameTo ->
                    daylSteyt\.replaceRebeldModyil\(from, to, isMove, renameTo\)
                    onSaveLayout\("current"\)
                \})"""

new4 = """                onReplace = { from, to, isMove, renameTo ->
                    val sourceMod = daylSteyt.rebeldModyilz.find { it.pozecon == from + 1 }
                    if (sourceMod?.type == "reset" || sourceMod?.id == "reset") {
                        val targetMod = daylSteyt.rebeldModyilz.find { it.pozecon == to + 1 }
                        daylSteyt.pendingResetTargetId = targetMod?.id ?: "rebeld"
                    } else {
                        daylSteyt.replaceRebeldModyil(from, to, isMove, renameTo)
                        onSaveLayout("current")
                    }
                }"""

# 5. DaylWedjet onDropOnFoldir (line 597)
pattern5 = r"""(            onDropOnFoldir = \{ from, to, isMove ->
                val targetMod = daylSteyt\.modyilz\.find \{ it\.pozecon == to \+ 1 \}
                if \(isMove\) \{
                    daylSteyt\.muvModyilEntuFoldir\(from, to\)
                \} else \{
                    daylSteyt\.kopeRebeldTuDaylFoldir\(from, to\)
                \}
                onSaveLayout\("current"\)
                if \(targetMod\?\.type == "rebeld"\) \{
                    onSaveLayout\("rebeld_state"\)
                \}
            \},)"""

new5 = """            onDropOnFoldir = { from, to, isMove ->
                val sourceMod = daylSteyt.modyilz.find { it.pozecon == from + 1 }
                val targetMod = daylSteyt.modyilz.find { it.pozecon == to + 1 }
                if (sourceMod?.type == "reset" || sourceMod?.id == "reset") {
                    daylSteyt.pendingResetTargetId = targetMod?.id ?: "dayl"
                } else {
                    if (isMove) {
                        daylSteyt.muvModyilEntuFoldir(from, to)
                    } else {
                        daylSteyt.kopeModyilEntuFoldir(from, to)
                    }
                    onSaveLayout("current")
                    if (targetMod?.type == "rebeld") {
                        onSaveLayout("rebeld_state")
                    }
                }
            },"""

# 6. DaylWedjet onReplace (line 624)
pattern6 = r"""(            onReplace = \{ from, to, isMove, renameTo ->
                daylSteyt\.replaceModyil\(from \+ 1, to \+ 1, isMove, renameTo\)
                onSaveLayout\("current"\)
            \},)"""

new6 = """            onReplace = { from, to, isMove, renameTo ->
                val sourceMod = daylSteyt.modyilz.find { it.pozecon == from + 1 }
                if (sourceMod?.type == "reset" || sourceMod?.id == "reset") {
                    val targetMod = daylSteyt.modyilz.find { it.pozecon == to + 1 }
                    daylSteyt.pendingResetTargetId = targetMod?.id ?: "dayl"
                } else {
                    daylSteyt.replaceModyil(from + 1, to + 1, isMove, renameTo)
                    onSaveLayout("current")
                }
            },"""


content = re.sub(pattern1, new1, content)
content = re.sub(pattern2, new2, content)
content = re.sub(pattern3, new3, content)
content = re.sub(pattern4, new4, content)
content = re.sub(pattern5, new5, content)
content = re.sub(pattern6, new6, content)

with open(file_path_dayl, "w", encoding="utf-8") as f:
    f.write(content)

# Patch Rebeld.kt
with open(file_path_rebeld, "r", encoding="utf-8") as f:
    content = f.read()

# 1. Rebeld onDropOnFoldir (line 192)
pattern_rebeld1 = r"""(                    onDropOnFoldir = \{ from, to, isMove ->
                        if \(isMove\) \{
                            daylSteyt\.muvRebeldModyilEntuFoldir\(from, to\)
                        \} else \{
                            daylSteyt\.kopeRebeldModyilEntuFoldir\(from, to\)
                        \}
                        syncRebeld\(\)
                    \},)"""

new_rebeld1 = """                    onDropOnFoldir = { from, to, isMove ->
                        val sourceMod = daylSteyt.rebeldModyilz.find { it.pozecon == from + 1 }
                        if (sourceMod?.type == "reset" || sourceMod?.id == "reset") {
                            val targetMod = daylSteyt.rebeldModyilz.find { it.pozecon == to + 1 }
                            daylSteyt.pendingResetTargetId = targetMod?.id ?: "rebeld"
                        } else {
                            if (isMove) {
                                daylSteyt.muvRebeldModyilEntuFoldir(from, to)
                            } else {
                                daylSteyt.kopeRebeldModyilEntuFoldir(from, to)
                            }
                            syncRebeld()
                        }
                    },"""

# 2. Rebeld onReplace (line 220)
pattern_rebeld2 = r"""(                    onReplace = \{ from, to, isMove, renameTo ->
                        daylSteyt\.replaceRebeldModyil\(from, to, isMove, renameTo\)
                        syncRebeld\(\)
                    \},)"""

new_rebeld2 = """                    onReplace = { from, to, isMove, renameTo ->
                        val sourceMod = daylSteyt.rebeldModyilz.find { it.pozecon == from + 1 }
                        if (sourceMod?.type == "reset" || sourceMod?.id == "reset") {
                            val targetMod = daylSteyt.rebeldModyilz.find { it.pozecon == to + 1 }
                            daylSteyt.pendingResetTargetId = targetMod?.id ?: "rebeld"
                        } else {
                            daylSteyt.replaceRebeldModyil(from, to, isMove, renameTo)
                            syncRebeld()
                        }
                    },"""

content = re.sub(pattern_rebeld1, new_rebeld1, content)
content = re.sub(pattern_rebeld2, new_rebeld2, content)

with open(file_path_rebeld, "w", encoding="utf-8") as f:
    f.write(content)

print("DaylSkren.kt and Rebeld.kt patched successfully")
