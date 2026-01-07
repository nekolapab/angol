package com.example.angol.ime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.BasicText

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MobileImeTestApp()
        }
    }
}

@Composable
fun MobileImeTestApp() {
    var text by remember { mutableStateOf("Tap here to test Android IME Hexagons (v2)") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E)), // Dark radial gradient base color
        contentAlignment = Alignment.Center
    ) {
        BasicText(
            text = text,
            style = TextStyle(color = Color.White, fontSize = 16.sp),
            modifier = Modifier
                .background(Color.Black.copy(alpha = 0.7f))
                .padding(16.dp)
        )
    }
}