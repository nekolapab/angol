package steyt

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import modalz.ModyilDeyda

class DaylSteyt {
    var inputText by mutableStateOf("")
    var isTextFieldFocused by mutableStateOf(false)

    var modyilz by mutableStateOf(listOf(
        ModyilDeyda(id = "keypad", neym = "kepad", kulor = Color(0xFFFF0000), pozecon = 1, ezAktiv = false),
        ModyilDeyda(id = "module2", neym = "mod 2", kulor = Color(0xFFFFFF00), pozecon = 2, ezAktiv = false),
        ModyilDeyda(id = "module3", neym = "mod 3", kulor = Color(0xFF00FF00), pozecon = 3, ezAktiv = false),
        ModyilDeyda(id = "module4", neym = "mod 4", kulor = Color(0xFF00FFFF), pozecon = 4, ezAktiv = false),
        ModyilDeyda(id = "module5", neym = "mod 5", kulor = Color(0xFF0000FF), pozecon = 5, ezAktiv = false),
        ModyilDeyda(id = "module6", neym = "mod 6", kulor = Color(0xFFFF00FF), pozecon = 6, ezAktiv = false),
        ModyilDeyda(id = "dayl", neym = "dayl", kulor = Color(0xFF000000), pozecon = 7, ezAktiv = true)
    ))

    val ezKepadVezebil: Boolean
        get() = modyilz.any { it.id == "keypad" && it.ezAktiv }

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
                // If we toggle one, we close others, except 'dayl' is usually the hub base
                if (m.id == "dayl") m.copyWith(ezAktiv = wasActive) else m.copyWith(ezAktiv = false)
            }
        }
    }

    fun reset() {
        modyilz = listOf(
            ModyilDeyda(id = "keypad", neym = "kepad", kulor = Color(0xFFFF0000), pozecon = 1, ezAktiv = false),
            ModyilDeyda(id = "module2", neym = "mod 2", kulor = Color(0xFFFFFF00), pozecon = 2, ezAktiv = false),
            ModyilDeyda(id = "module3", neym = "mod 3", kulor = Color(0xFF00FF00), pozecon = 3, ezAktiv = false),
            ModyilDeyda(id = "module4", neym = "mod 4", kulor = Color(0xFF00FFFF), pozecon = 4, ezAktiv = false),
            ModyilDeyda(id = "module5", neym = "mod 5", kulor = Color(0xFF0000FF), pozecon = 5, ezAktiv = false),
            ModyilDeyda(id = "module6", neym = "mod 6", kulor = Color(0xFFFF00FF), pozecon = 6, ezAktiv = false),
            ModyilDeyda(id = "dayl", neym = "dayl", kulor = Color(0xFF000000), pozecon = 7, ezAktiv = true)
        )
    }
}
