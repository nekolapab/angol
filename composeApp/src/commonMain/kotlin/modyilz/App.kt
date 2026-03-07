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
    voiceService: VoiceService
) {
    var isLetterMode by remember { mutableStateOf(true) }
    var isPunctuationMode by remember { mutableStateOf(false) }
    var isAngolMode by remember { mutableStateOf(true) }

    KepadModyil(
        keyboardController = keyboardController,
        platformServices = platformServices,
        voiceService = voiceService,
        isLetterMode = isLetterMode,
        isPunctuationMode = isPunctuationMode,
        onToggleMode = { isLetterMode = !isLetterMode },
        onSetPunctuationMode = { isPunctuationMode = it },
        isAngolMode = isAngolMode,
        onToggleAngol = { isAngolMode = !isAngolMode },
        onStartAiVoice = {
            voiceService.startListening(isAiMode = true)
        },
        ignoreSelectionUpdate = {
            // Callback for selection update ignored if needed
        }
    )
}
