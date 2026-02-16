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

    val displayLength = 7



    Box(

        modifier = Modifier

            .fillMaxSize()

            .background(Color(0xFF1A1A2E))

    ) {

        // Show keypad directly (moved to back)

        KepadModyil(

            isKeypadVisible = true,

            displayLength = displayLength,

            onHexKeyPress = { char: String, isLongPress: Boolean, primaryChar: String? ->

                android.util.Log.d("ComposeMainActivity", "onHexKeyPress: char=$char, isLongPress=$isLongPress, primary=$primaryChar")

                if (char == "⌫") {

                    if (isLongPress) {

                        currentText = ""

                    } else if (currentText.isNotEmpty()) {

                        currentText = currentText.dropLast(1)

                    }

                } else {

                    if (isLongPress && primaryChar != null) {

                        currentText = currentText.dropLast(primaryChar.length) + char

                    } else {

                        currentText += char

                    }

                }

                android.util.Log.d("ComposeMainActivity", "currentText is now: \"$currentText\"")

            }

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
