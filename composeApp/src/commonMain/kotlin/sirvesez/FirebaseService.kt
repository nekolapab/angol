package sirvesez

import kotlinx.coroutines.flow.Flow
import modalz.ModyilDeyda

interface FirebaseService {
    val currentUser: User?
    val authStateChanges: Flow<User?>
    suspend fun signInWithGitHub(): Result<Unit>
    suspend fun signOut()
    suspend fun saveModuleLayout(modyilz: List<ModyilDeyda>)
    fun watchModuleLayout(): Flow<List<ModyilDeyda>>
}

data class User(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?
)
