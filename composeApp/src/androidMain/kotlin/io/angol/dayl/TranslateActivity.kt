package io.angol.dayl

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.google.firebase.Firebase
import com.google.firebase.initialize
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import yuteledez.AngolSpelenqMelxod

private const val TAG = "TranslateActivity"

/**
 * An activity that handles the ACTION_PROCESS_TEXT intent, 
 * providing an "angol" option in the Android text selection menu.
 */
class TranslateActivity : ComponentActivity() {

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

        setContent {
            TranslateScreen(
                text = text,
                isReadOnly = isReadOnly,
                onSuccess = { convertedText ->
                    if (!isReadOnly) {
                        val resultIntent = Intent()
                        resultIntent.putExtra(Intent.EXTRA_PROCESS_TEXT, convertedText)
                        setResult(RESULT_OK, resultIntent)
                    } else {
                         // Copy to clipboard or show in a dialog could be an option here
                         Toast.makeText(this, "Copied: $convertedText", Toast.LENGTH_LONG).show()
                    }
                    finish()
                },
                onError = { error ->
                    Toast.makeText(this, "Translation failed: $error", Toast.LENGTH_LONG).show()
                    setResult(RESULT_CANCELED)
                    finish()
                }
            )
        }
    }
}

@Composable
fun TranslateScreen(
    text: CharSequence,
    isReadOnly: Boolean,
    onSuccess: (CharSequence) -> Unit,
    onError: (String) -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(text) {
        try {
            // Ensure Firebase is initialized (might be redundant if app is running, but safe)
            // Note: Firebase.initialize needs Context, but in Composable we can't easily access it without LocalContext.
            // However, it's static. But better to do it before launched effect or assume it's done.
            // Since this is an Activity, we can rely on Application onCreate or do it in Activity onCreate.
            // We'll assume it's initialized in onCreate of Activity or Application.
            
            val model = Firebase.ai.generativeModel("gemini-3.1-flash")
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
            onSuccess(convertedText)

        } catch (e: Throwable) {
            Log.e(TAG, "Gemini failed, trying local fallback", e)
            try {
                // Fallback to local logic immediately if Gemini fails
                val fallbackText = AngolSpelenqMelxod.convertToAngolSpelling(text.toString())
                onSuccess(fallbackText)
            } catch (fallbackError: Throwable) {
                Log.e(TAG, "Local fallback also failed", fallbackError)
                onError("Both cloud and local translation failed.")
            }
        } finally {
            isLoading = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Translating...")
                }
            }
        }
    }
}
