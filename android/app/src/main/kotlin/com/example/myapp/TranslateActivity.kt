package com.example.myapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.initialize
import com.google.firebase.vertexai.vertexAI
import com.google.firebase.vertexai.type.content
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "TranslateActivity"

/**
 * An activity that handles the ACTION_PROCESS_TEXT intent, 
   * providing an "angol" option in the Android text selection menu. */
class TranslateActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val text = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)
        val isReadOnly = intent.getBooleanExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, false)

        if (text == null) {
            finish()
            return
        }

        try {
            Firebase.initialize(this)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Firebase: ${e.message}")
        }

        // Use a coroutine to handle translation via Gemini
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val model = Firebase.vertexAI.generativeModel("gemini-1.5-flash")
                val prompt = """
                    Convert the following text between 'Angol' spelling and standard English. 
                    If the text is in standard English, convert it to Angol spelling.
                    If the text is in Angol spelling, convert it to standard English.
                    
                    Angol is a phonetic spelling where 'the' is 'lha', 'to' is 'tu', 'ing' is 'enq', 'tion' is 'con', etc.
                    Only output the converted text, no explanations or extra text.
                    Text: $text
                """.trimIndent()

                val response = withContext(Dispatchers.IO) {
                    model.generateContent(content { text(prompt) })
                }
                
                val convertedText = response.text?.trim() ?: text.toString()

                if (!isReadOnly) {
                    val resultIntent = Intent()
                    resultIntent.putExtra(Intent.EXTRA_PROCESS_TEXT, convertedText)
                    setResult(RESULT_OK, resultIntent)
                } else {
                    // If read-only, we could show a dialog, but for now just finish
                    Log.d(TAG, "Text is read-only, cannot replace: $convertedText")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Translation failed: ${e.message}")
                // Fallback: don't change anything
                setResult(RESULT_CANCELED)
            } finally {
                finish()
            }
        }
    }
}
