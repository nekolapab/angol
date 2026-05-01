package com.example.angol.ime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import skrenz.DaylSkrenEntry

class MeynAktevede : ComponentActivity() {
    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val keyboardController = AndroidKeyboardController(
            getIc = { null },
            onSmartEnter = {},
            onForcedSubmit = {}
        )
        val platformServices = AndroidPlatformServices(this, scope)
        val voiceService = AndroidVoiceService(
            onStart = { },
            onStop = { },
            onTogilAngol = { },
            isListening = mutableStateOf(false),
            hasSpoken = mutableStateOf(false),
            angolSpelenqMod = mutableIntStateOf(0)
        )

        setContent {
            DaylSkrenEntry(
                keyboardController = keyboardController,
                platformServices = platformServices,
                voiceService = voiceService,
                firebaseService = null,
                isApp = true
            )
        }
    }
}
