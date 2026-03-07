package modyilz

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

/**
 * Entry point for iOS application to consume the shared Compose UI.
 * Note: Implementations for KeyboardController, PlatformServices, and VoiceService
 * must be provided from the iOS native side or implemented in iosMain.
 */
fun MainViewController(
    keyboardController: KeyboardController,
    platformServices: PlatformServices,
    voiceService: VoiceService
): UIViewController = ComposeUIViewController {
    App(
        keyboardController = keyboardController,
        platformServices = platformServices,
        voiceService = voiceService
    )
}
