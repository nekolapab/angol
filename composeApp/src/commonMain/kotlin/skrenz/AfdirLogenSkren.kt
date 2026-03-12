package skrenz

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import sirvesez.FirebaseService

@Composable
fun AfdirLogenSkren(firebaseService: FirebaseService, onContinue: () -> Unit) {
    val scope = rememberCoroutineScope()
    val user = firebaseService.currentUser

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home Page") },
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            firebaseService.signOut()
                        }
                    }) {
                        Icon(Icons.Default.Logout, contentDescription = "Sign Out")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Welcome, ${user?.displayName ?: user?.email ?: "User"}!", style = MaterialTheme.typography.h5)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onContinue) {
                Text("Go to Keypad")
            }
        }
    }
}
