package skrenz

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

@Composable
fun SaynEnSkren(firebaseSirves: FirebaseSirves, onBypass: () -> Unit) {
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Angol", style = MaterialTheme.typography.h4)
            Spacer(modifier = Modifier.height(32.dp))
            
            if (isLoading) {
                CircularProgressIndicator()
            } else {
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
                    modifier = Modifier.fillMaxWidth(0.7f)
                ) {
                    Text("Sign in with GitHub")
                }
                
                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = errorMessage!!,
                        color = Color.Red,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                TextButton(onClick = { onBypass() }) {
                    Text("Continue as Guest", color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f))
                }
            }
        }
    }
}
