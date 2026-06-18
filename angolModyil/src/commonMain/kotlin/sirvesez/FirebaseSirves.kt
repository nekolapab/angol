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
    suspend fun saveModuleLayout(modyilz: List<ModyilDeyda>, environment: String = "current")
    fun watcModjilLeyawt(environment: String = "current"): Flow<List<ModyilDeyda>>
}

data class User(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?
)
