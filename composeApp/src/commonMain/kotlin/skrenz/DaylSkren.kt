package skrenz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.geometry.Offset
import modyilz.DaylModyil
import modyilz.KepadModyil
import steyt.DaylSteyt
import yuteledez.HeksagonDjeyometre
import modalz.HeksagonPozecon
import kotlin.math.sqrt

@Composable
fun DaylSkren(
    keyboardController: modyilz.KeyboardController?,
    platformServices: modyilz.PlatformServices,
    voiceService: modyilz.VoiceService,
    isApp: Boolean = true
) {
    val daylSteyt = remember { DaylSteyt() }
    
    // If it's the IME (not isApp), we should probably start in keypad mode?
    // Or maybe the user wants the dial even in IME. 
    // Let's force keypad mode if isApp is false.
    LaunchedEffect(isApp) {
        if (!isApp) {
            daylSteyt.togilModyil(1) // Toggle keypad (id="keypad", pozecon=1)
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    0.0f to Color(0xFF1A1A2E),
                    0.5f to Color(0xFF0F0F1E),
                    1.0f to Color.Black,
                    center = Offset.Unspecified,
                    radius = 1000f
                )
            )
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight
        
        // Perfect fit: 5 hexagons across the screen width OR fit within height.
        // hexWidth = ScreenWidth / 5.0
        // hexSize (radius) = hexWidth / sqrt(3.0)
        // gridHeight = 8 * hexSize (to fit 4 hexagons vertically)
        val hexSize = minOf(
            screenWidth.value / (5.0 * sqrt(3.0)),
            screenHeight.value / 8.0
        ).dp

        val geometry = remember(hexSize) {
            HeksagonDjeyometre(
                heksSayz = hexSize.value.toDouble(),
                sentir = HeksagonPozecon(0.0, 0.0),
                ezLeterMod = true
            )
        }

        // Absolute centering for the Hub/Keypad
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (daylSteyt.ezKepadVezebil) {
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
                    ignoreSelectionUpdate = { },
                    geometryOverride = geometry
                )
            } else {
                DaylModyil(
                    geometry = geometry,
                    modyilz = daylSteyt.modyilz,
                    onToggleModule = { index ->
                        // Tapping 'kepad' (index 1) toggles state which replaces Hub with in-app Keypad
                        // We do NOT call keyboardController.show() here to avoid the bottom system IME
                        daylSteyt.togilModyil(index)
                    },
                    stackWidth = screenWidth,
                    stackHeight = this@BoxWithConstraints.maxHeight
                )
            }
        }
        
        // Settings icon at the bottom, absolutely positioned
        if (!daylSteyt.ezKepadVezebil) {
            IconButton(
                onClick = { platformServices.openSettings() },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}
