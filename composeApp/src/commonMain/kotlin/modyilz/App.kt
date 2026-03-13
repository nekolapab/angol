package modyilz

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import sirvesez.FirebaseService
import skrenz.AfdirLogenSkren
import skrenz.DaylSkren
import skrenz.SaynEnSkren

@Composable
fun App(
    keyboardController: KeyboardController?,
    platformServices: PlatformServices,
    voiceService: VoiceService,
    firebaseService: FirebaseService,
    isApp: Boolean = false
) {
    val user by firebaseService.authStateChanges.collectAsState(initial = firebaseService.currentUser)
    var currentScreen by remember { mutableStateOf("main") }
    var isGuestMode by remember { mutableStateOf(false) }

    if (isApp) {
        if (user == null && !isGuestMode) {
            SaynEnSkren(firebaseService, onBypass = { isGuestMode = true })
        } else {
            when (currentScreen) {
                "main" -> DaylSkren(keyboardController, platformServices, voiceService, firebaseService, isApp = true)
                "home" -> AfdirLogenSkren(firebaseService, onContinue = { currentScreen = "main" })
            }
        }
    } else {
        // IME mode: just show the Dial/Keypad.
        // It can use firebaseService internally for layout sync if logged in.
        DaylSkren(keyboardController, platformServices, voiceService, firebaseService, isApp = false)
    }
}
