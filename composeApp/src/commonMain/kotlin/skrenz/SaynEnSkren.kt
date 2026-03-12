package skrenz

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import sirvesez.FirebaseService

@Composable
fun SaynEnSkren(firebaseService: FirebaseService) {
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
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
