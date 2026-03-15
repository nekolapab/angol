package com.example.angol.ime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import modyilz.App
import androidx.compose.runtime.remember
import androidx.core.view.WindowCompat
import kotlinx.coroutines.MainScope

class MeynAktevede : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        
        val scope = MainScope()
        val platformServices = AndroidPlatformServices(this, scope)
        val firebaseService = AndroidFirebaseService(this)
        val keyboardController = AndroidKeyboardController { null } // Dummy for preview
        val isListening = mutableStateOf(false)
        val isAngolMode = mutableStateOf(true)
        val voiceService = AndroidVoiceService(
            onStart = { isListening.value = true },
            onStop = { isListening.value = false },
            onToggleAngol = { isAngolMode.value = !isAngolMode.value },
            isListening = isListening,
            isAngolMode = isAngolMode
        )

        setContent {
            MaterialTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    App(
                        keyboardController = keyboardController,
                        platformServices = platformServices,
                        voiceService = voiceService,
                        firebaseService = firebaseService,
                        isApp = true
                    )
                }
            }
        }
    }
}
