package modyilz

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

/**
 * Entry point for iOS application to consume the shared Compose UI.
 * Note: Implementations for KeyboardController, PlatformServices, and VoiceService
 * must be provided from the iOS native side or implemented in iosMain.
 */
fun MainViewController(
    kebordKontrolir: KeyboardController,
    platformServices: PlatformServices,
    voiceService: VoiceService
): UIViewController = ComposeUIViewController {
    skrenz.DaylSkrenEntry(
        kebordKontrolir = kebordKontrolir,
        platformServices = platformServices,
        voiceService = voiceService,
        isApp = true
    )
}
