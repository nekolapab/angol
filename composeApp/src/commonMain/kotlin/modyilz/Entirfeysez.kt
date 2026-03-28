package modyilz

import androidx.compose.runtime.State

interface KeyboardController {
    fun commitText(text: String)
    fun deleteSurroundingText(beforeLength: Int, afterLength: Int)
    fun sendKeyEvent(keyCode: Int)
    fun getTextBeforeCursor(n: Int): String?
    fun getTextAfterCursor(n: Int): String?
}

interface PlatformServices {
    fun log(tag: String, message: String)
    fun toast(message: String)
    fun playClickSound()
    fun addToCorpus(word: String)
    suspend fun getCorpus(): String
    fun openSettings()
    fun speak(text: String)
}

interface VoiceService {
    val isListening: State<Boolean>
    val angolSpelenqMod: State<Int> // 0=Off, 1=Angol1, 2=Angol2
    fun startListening(isAiMode: Boolean = false)
    fun stopListening()
    fun togilAngolMod(isLongPress: Boolean = false)
}
