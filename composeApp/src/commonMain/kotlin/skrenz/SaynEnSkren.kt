package skrenz

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import sirvesez.FirebaseService

@Composable
fun SaynEnSkren(firebaseService: FirebaseService, onBypass: () -> Unit) {
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { onBypass() })
            },
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
                Button(onClick = {
                    scope.launch {
                        isLoading = true
                        firebaseService.signInWithGitHub()
                        isLoading = false
                    }
                }) {
                    Text("Sign in with GitHub")
                }
            }
        }
    }
}
