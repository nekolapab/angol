package sirvesez

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * A service to manage the input state of the keyboard.
 *
 * This class is a translation of a Dart ChangeNotifier, using StateFlow for reactive
 * state management in Kotlin. In a real application, consider using a
 * proper logging library instead of println.
 */
class EnpitSirves {

    private val _inputText = MutableStateFlow("")
    val inputText = _inputText.asStateFlow()

    private val _isLetterMode = MutableStateFlow(true)
    val isLetterMode = _isLetterMode.asStateFlow()

    private var _shouldCapitalize = false

    private val _isTextFieldFocused = MutableStateFlow(false)
    val isTextFieldFocused = _isTextFieldFocused.asStateFlow()

    fun addCharacter(char: String) {
        println("EnpitSirves: addCharacter called with: $char")
        if (char == "⌫") {
            deleteLeft()
            println("EnpitSirves: Deleting left via addCharacter.")
            return
        }

        val finalChar = if (_shouldCapitalize) char.uppercase() else char
        _inputText.update { it + finalChar }
        _shouldCapitalize = false
        println("EnpitSirves: _inputText updated to: \"${_inputText.value}\" ")
    }

    fun deleteLeft() {
        _inputText.update { it.dropLast(1) }
    }

    fun deleteWord() {
        if (_inputText.value.isEmpty()) return

        val currentText = _inputText.value
        val trimmedText = currentText.trimEnd()
        val lastSpaceIndex = trimmedText.lastIndexOf(' ')

        _inputText.value = if (lastSpaceIndex != -1) {
            trimmedText.substring(0, lastSpaceIndex)
        } else {
            ""
        }
    }

    fun deleteCharacters(count: Int) {
        val currentText = _inputText.value
        if (currentText.length >= count) {
            _inputText.value = currentText.dropLast(count)
        } else {
            _inputText.value = ""
        }
    }

    fun toggleMode() {
        _isLetterMode.update { !it }
    }

    fun setCapitalize() {
        _shouldCapitalize = true
    }

    fun setTextFieldFocus(focused: Boolean) {
        _isTextFieldFocused.value = focused
    }

    fun getDisplayText(displayLength: Int): String {
        val currentText = _inputText.value
        return when {
            currentText.isEmpty() -> " ".repeat(displayLength)
            currentText.length >= displayLength -> currentText.takeLast(displayLength)
            else -> currentText.padStart(displayLength, ' ')
        }
    }

    fun clearText() {
        _inputText.value = ""
        println("InputService: _inputText cleared. New value: \"${_inputText.value}\" ")
    }
}
