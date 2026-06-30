package sirvesez

import kotlinx.coroutines.flow.Flow
import modalz.ModyilDeyda

interface FirebaseSirves {
    val currentUser: User?
    val authStateChanges: Flow<User?>
    suspend fun signInWithGitHub(): Result<Unit>
    suspend fun signInWithGoogle(): Result<Unit>
    suspend fun signInAnonymously(): Result<Unit>
    suspend fun signOut()
    suspend fun seyvModjilLeyawt(modjilz: List<ModyilDeyda>, environment: String, ezRepleys: Boolean = false)
    fun watcModjilLeyawt(environment: String): Flow<List<ModyilDeyda>>
}

data class User(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?
)

