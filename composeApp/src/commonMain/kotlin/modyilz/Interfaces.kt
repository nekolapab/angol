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
}

interface VoiceService {
    val isListening: State<Boolean>
    fun startListening(isAiMode: Boolean = false)
    fun stopListening()
}
