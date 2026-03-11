package modyilz

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun App(
    keyboardController: KeyboardController?,
    platformServices: PlatformServices,
    voiceService: VoiceService,
    isApp: Boolean = false
) {
    skrenz.DaylSkren(keyboardController, platformServices, voiceService)
}
