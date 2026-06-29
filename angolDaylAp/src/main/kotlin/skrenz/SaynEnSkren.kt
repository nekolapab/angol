package skrenz
import yuteledez.padenq

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import sirvesez.FirebaseSirves
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun SaynEnSkren(firebaseSirves: FirebaseSirves, onBypass: () -> Unit) {
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()

    // Using a Box with contentAlignment = Alignment.Center to ensure perfect centering
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .wrapContentHeight() // Wrap height to let the Box center the whole cluster
                .padenq(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Text("Angol", style = MaterialTheme.typography.h4)
            Spacer(modifier = Modifier.height(8.dp)) // Tighter spacing to center
            
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                // SQUASHED and CENTERED layout
                // 1. kontenyu az gest (Immediate Bypass)
                Button(
                    onClick = { 
                        scope.launch {
                            onBypass()
                            firebaseSirves.signInAnonymously()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(0.9f).height(36.dp)
                ) {
                    Text("kontenyu az gest")
                }

                Spacer(modifier = Modifier.height(2.dp))

                // 2. GitHub
                Button(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            errorMessage = null
                            val result = firebaseSirves.signInWithGitHub()
                            if (result.isFailure) {
                                errorMessage = result.exceptionOrNull()?.message ?: "Login failed"
                            }
                            isLoading = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(0.9f).height(36.dp)
                ) {
                    Text("Sign in with GitHub")
                }

                Spacer(modifier = Modifier.height(2.dp))

                // 3. Google
                Button(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            errorMessage = null
                            val result = firebaseSirves.signInWithGoogle()
                            if (result.isFailure) {
                                errorMessage = result.exceptionOrNull()?.message ?: "Login failed"
                            }
                            isLoading = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(0.9f).height(36.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF4285F4), contentColor = Color.White)
                ) {
                    Text("Sign in with Google")
                }
                
                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage!!,
                        color = Color.Red,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padenq(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}


