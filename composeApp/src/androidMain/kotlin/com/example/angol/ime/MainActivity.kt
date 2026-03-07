package com.example.angol.ime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import modyilz.App
import androidx.compose.runtime.remember
import kotlinx.coroutines.MainScope

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val scope = MainScope()
        val platformServices = AndroidPlatformServices(this, scope)
        val keyboardController = AndroidKeyboardController { null } // Dummy for preview
        val isListening = mutableStateOf(false)
        val voiceService = AndroidVoiceService(
            onStart = { isListening.value = true },
            onStop = { isListening.value = false },
            isListening = isListening
        )

        setContent {
            MaterialTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    App(
                        keyboardController = keyboardController,
                        platformServices = platformServices,
                        voiceService = voiceService
                    )
                }
            }
        }
    }
}
