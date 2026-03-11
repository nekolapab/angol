package com.example.angol.ime

import android.content.Context
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.InputConnection
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import modyilz.KeyboardController
import modyilz.PlatformServices
import modyilz.VoiceService
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

class AndroidKeyboardController(private val getIc: () -> InputConnection?) : KeyboardController {
    override fun commitText(text: String) {
        getIc()?.commitText(text, 1)
    }

    override fun deleteSurroundingText(beforeLength: Int, afterLength: Int) {
        getIc()?.deleteSurroundingText(beforeLength, afterLength)
    }

    override fun sendKeyEvent(keyCode: Int) {
        val ic = getIc() ?: return
        ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, keyCode))
        ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, keyCode))
    }

    override fun getTextBeforeCursor(n: Int): String? {
        return getIc()?.getTextBeforeCursor(n, 0)?.toString()
    }

    override fun getTextAfterCursor(n: Int): String? {
        return getIc()?.getTextAfterCursor(n, 0)?.toString()
    }
}

class AndroidPlatformServices(
    private val context: Context,
    private val corpusScope: kotlinx.coroutines.CoroutineScope
) : PlatformServices {
    private val CORPUS_FILE = "angol_corpus.txt"
    private val MAX_CORPUS_SIZE = 2000

    override fun log(tag: String, message: String) {
        Log.d(tag, message)
    }

    override fun toast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun playClickSound() {
        // Implementation for click sound if needed
    }

    override fun addToCorpus(word: String) {
        if (word.isBlank()) return
        corpusScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val file = File(context.filesDir, CORPUS_FILE)
                file.appendText("$word ")
                if (file.length() > MAX_CORPUS_SIZE) {
                    val content = file.readText()
                    val keepLength = (MAX_CORPUS_SIZE * 0.8).toInt()
                    val start = content.length - keepLength
                    val trimmed = content.substring(start)
                    val cleanStart = trimmed.indexOfFirst { it.isWhitespace() }
                    val finalContent = if (cleanStart != -1) trimmed.substring(cleanStart + 1) else trimmed
                    file.writeText(finalContent)
                }
            } catch (e: IOException) {
                Log.e("AndroidBridge", "Failed to update corpus: ${e.message}")
            }
        }
    }

    override suspend fun getCorpus(): String {
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val file = File(context.filesDir, CORPUS_FILE)
                if (file.exists()) file.readText() else ""
            } catch (e: IOException) {
                ""
            }
        }
    }

    override fun openSettings() {
        val intent = android.content.Intent(android.provider.Settings.ACTION_INPUT_METHOD_SETTINGS).apply {
            addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}

class AndroidVoiceService(
    private val onStart: (Boolean) -> Unit,
    private val onStop: () -> Unit,
    override val isListening: State<Boolean>
) : VoiceService {
    override fun startListening(isAiMode: Boolean) {
        onStart(isAiMode)
    }

    override fun stopListening() {
        onStop()
    }
}
