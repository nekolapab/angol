package com.example.myapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import modyilz.KepadModyil

class ComposeMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearApp()
        }
    }
}

@Composable

fun WearApp() {

    var currentText by remember { mutableStateOf("") }
    var isListening by remember { mutableStateOf(false) }
    var isLetterMode by remember { mutableStateOf(true) }
    var isPunctuationMode by remember { mutableStateOf(false) }
    var isAngolMode by remember { mutableStateOf(true) }

    val displayLength = 7



    Box(

        modifier = Modifier

            .fillMaxSize()

            .background(Color(0xFF1A1A2E))

    ) {

        // Show keypad directly (moved to back)

        KepadModyil(
            ic = null,
            isListening = isListening,
            isLetterMode = isLetterMode,
            isPunctuationMode = isPunctuationMode,
            onToggleVoice = { isListening = !isListening },
            onStartVoice = { isListening = true },
            onStopVoice = { isListening = false },
            onToggleMode = { isLetterMode = !isLetterMode },
            onSetPunctuationMode = { isPunctuationMode = it },
            isAngolMode = isAngolMode,
            onToggleAngol = { isAngolMode = !isAngolMode },
            ignoreSelectionUpdate = { }
        )



        // Text overlay at the top (ensure it's on top of KepadModyil)

        Box(

            modifier = Modifier

                .align(Alignment.TopCenter)

                .padding(top = 32.dp) // More padding for visibility

                .background(Color.Black.copy(alpha = 0.8f))

                .padding(horizontal = 16.dp, vertical = 8.dp),

            contentAlignment = Alignment.Center

        ) {

            Text(

                text = if (currentText.isEmpty()) "Start typing..." else

                       if (currentText.length > displayLength)

                           "..." + currentText.takeLast(displayLength)

                       else currentText,

                style = TextStyle(

                    color = Color.Cyan, // Brighter color for visibility

                    fontSize = 20.sp,   // Larger font

                    fontWeight = FontWeight.Bold,

                    textAlign = TextAlign.Center

                )

            )

        }

    }

}
