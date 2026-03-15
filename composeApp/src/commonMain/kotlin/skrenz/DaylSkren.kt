package skrenz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
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

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun DaylSkren(
    keyboardController: modyilz.KeyboardController?,
    platformServices: modyilz.PlatformServices,
    voiceService: modyilz.VoiceService,
    firebaseService: sirvesez.FirebaseService? = null,
    isApp: Boolean = true,
    onGoToHome: (() -> Unit)? = null
) {
    val daylSteyt = remember { DaylSteyt() }
    val scope = rememberCoroutineScope()
    
    // Auth and Layout Sync
    val userState = firebaseService?.authStateChanges?.collectAsState(initial = firebaseService.currentUser)
    val user = userState?.value
    
    LaunchedEffect(user) {
        if (user != null && firebaseService != null) {
            firebaseService.watchModuleLayout().collectLatest { remoteModules ->
                if (remoteModules.isNotEmpty()) {
                    daylSteyt.updateModules(remoteModules)
                }
            }
        }
    }
    
    // If it's the IME (not isApp), we should probably start in keypad mode?
    // Or maybe the user wants the dial even in IME. 
    // Let's force keypad mode if isApp is false.
    LaunchedEffect(isApp) {
        if (!isApp) {
            daylSteyt.togilModyil(1) // Toggle keypad (id="keypad", pozecon=1)
        }
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight
        
        // Perfect fit: 5 hexagons across the screen width OR fit within height.
        val hexSize = minOf(
            screenWidth.value / (5.0 * sqrt(3.0)),
            screenHeight.value / 10.0 // Safer 10-radii height
        ).dp

        val geometry = remember(hexSize, screenWidth, screenHeight, isApp) {
            val hexWidth = hexSize.value * sqrt(3.0)

            val isLandscape = screenWidth > screenHeight
            val sentirX = if (!isApp && isLandscape) {
                -screenWidth.value / 2.0 + 2.6666 * hexWidth
            } else {
                0.0
            }

            HeksagonDjeyometre(
                heksSayz = hexSize.value.toDouble(),
                sentir = HeksagonPozecon(sentirX, 0.0),
                ezLeterMod = true
            )        }

        // Limit the height of the IME container to fit the keyboard perfectly (9 radii tall)
        val contentModifier = if (isApp) {
            Modifier.fillMaxSize().background(
                Brush.radialGradient(
                    0.0f to Color(0xFF1A1A2E),
                    0.5f to Color(0xFF0F0F1E),
                    1.0f to Color.Black,
                    radius = 2000f
                )
            )
        } else {
            Modifier.fillMaxWidth().height(hexSize * 8.0f)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        0.0f to Color.Black.copy(alpha = 0.7f),
                        1.0f to Color.Black
                    )
                )
        }

        // Absolute centering/bottom alignment for the Hub/Keypad
        Box(
            modifier = contentModifier, 
            contentAlignment = if (isApp) Alignment.Center else Alignment.BottomCenter
        ) {
            if (daylSteyt.ezKepadVezebil) {
                var isLetterMode by remember { mutableStateOf(true) }
                var isPunctuationMode by remember { mutableStateOf(false) }

                KepadModyil(
                    keyboardController = keyboardController,
                    platformServices = platformServices,
                    voiceService = voiceService,
                    isLetterMode = isLetterMode,
                    isPunctuationMode = isPunctuationMode,
                    onToggleMode = { isLetterMode = !isLetterMode },
                    onSetPunctuationMode = { isPunctuationMode = it },
                    isAngolMode = voiceService.isAngolMode.value,
                    onToggleAngol = { voiceService.toggleAngolMode() },
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
                        
                        // Save to Firebase if logged in
                        if (user != null && firebaseService != null) {
                            scope.launch {
                                firebaseService.saveModuleLayout(daylSteyt.modyilz)
                            }
                        }
                    },
                    stackWidth = screenWidth,
                    stackHeight = if (isApp) screenHeight else hexSize * 8f
                )
            }
        }
        
        // Settings and Account icons at the bottom, absolutely positioned
        if (!daylSteyt.ezKepadVezebil) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                IconButton(onClick = { platformServices.openSettings() }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(32.dp)
                    )
                }
                if (isApp) {
                    IconButton(onClick = { onGoToHome?.invoke() }) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Account",
                            tint = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }
    }
}
