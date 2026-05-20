package com.example.angol.ime

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import skrenz.DaylSkrenEntry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import steyt.DaylSteyt

class DaylViewModel : ViewModel() {
    val daylSteyt = DaylSteyt()
}

class MeynAktevede : ComponentActivity() {
    private val scope = CoroutineScope(Dispatchers.Main)
    private lateinit var firebaseSirves: AndroidFirebaseSirves

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

        val firebaseSirves = AndroidFirebaseSirves(this)

        setContent {
            val viewModel: DaylViewModel = viewModel()
            DaylSkrenEntry(
                keyboardController = keyboardController,
                platformServices = platformServices,
                voiceService = voiceService,
                firebaseSirves = firebaseSirves,
                isApp = true,
                daylSteyt = viewModel.daylSteyt
            )
        }
    }
}
