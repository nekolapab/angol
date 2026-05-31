package io.angol.dayl.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import steyt.DaylSteyt
import skrenz.DaylSkrenEntry
import com.example.angol.ime.AndroidPlatformServices
import com.example.angol.ime.AndroidFirebaseSirves
import com.example.angol.ime.AndroidVoiceService
import com.example.angol.ime.AndroidKeyboardController
import kotlinx.coroutines.MainScope

class MeynAktevede : ComponentActivity() {
    private val daylSteyt = DaylSteyt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val lifecycleScope = MainScope()
        val platformServices = AndroidPlatformServices(this, lifecycleScope)
        val firebaseSirves = AndroidFirebaseSirves(this)
        val voiceService = AndroidVoiceService(
            { _: Boolean -> }, 
            { }, 
            { _: Boolean -> }, 
            mutableStateOf(false), 
            mutableStateOf(false), 
            mutableStateOf(0)
        )
        val keyboardController = AndroidKeyboardController({ null }, { }, { })

        setContent {
            DaylSkrenEntry(
                keyboardController = keyboardController,
                platformServices = platformServices,
                voiceService = voiceService,
                firebaseSirves = firebaseSirves,
                isApp = true,
                daylSteyt = daylSteyt
            )
        }
    }
}
