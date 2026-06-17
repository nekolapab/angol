import re

file_path = r"C:\Users\nicli\angol\angolModyil\src\commonMain\kotlin\steyt\AngolSteyt.kt"

with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

# 1. Add global history variables
add_history_pattern = r"""(    val moduleHistory = mutableMapOf<String, MutableList<ModyilDeyda>>\(\)
    val moduleRedo = mutableMapOf<String, MutableList<ModyilDeyda>>\(\))"""

add_history_new = """    val moduleHistory = mutableMapOf<String, MutableList<ModyilDeyda>>()
    val moduleRedo = mutableMapOf<String, MutableList<ModyilDeyda>>()
    val globalModyilzHistory = mutableListOf<List<ModyilDeyda>>()
    val globalRebeldHistory = mutableListOf<List<ModyilDeyda>>()
    val globalModyilzRedo = mutableListOf<List<ModyilDeyda>>()
    val globalRebeldRedo = mutableListOf<List<ModyilDeyda>>()"""

content = re.sub(add_history_pattern, add_history_new, content)

# 2. Update recordState to record global history
record_state_pattern = r"""(    fun recordState\(\) \{)"""

record_state_new = """    fun recordState() {
        if (modyilz != lastModyilz) {
            globalModyilzHistory.add(lastModyilz)
            if (globalModyilzHistory.size > 20) globalModyilzHistory.removeAt(0)
            globalModyilzRedo.clear()
        }
        if (rebeldModyilz != lastRebeldModyilz) {
            globalRebeldHistory.add(lastRebeldModyilz)
            if (globalRebeldHistory.size > 20) globalRebeldHistory.removeAt(0)
            globalRebeldRedo.clear()
        }"""

content = re.sub(record_state_pattern, record_state_new, content)

# 3. Update undoModule
undo_module_pattern = r"""(    fun undoModule\(id: String\) \{)"""

undo_module_new = """    fun undoModule(id: String) {
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
        }"""

content = re.sub(undo_module_pattern, undo_module_new, content)

# 4. Update redoModule
redo_module_pattern = r"""(    fun redoModule\(id: String\) \{)"""

redo_module_new = """    fun redoModule(id: String) {
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
        }"""

content = re.sub(redo_module_pattern, redo_module_new, content)

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)
print("AngolSteyt.kt patched successfully")
