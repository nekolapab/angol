package com.example.angol.ime

import android.app.Activity
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import modalz.ModyilDeyda
import sirvesez.FirebaseService
import sirvesez.User

class AndroidFirebaseService(
    private val activity: Activity? = null
) : FirebaseService {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override val currentUser: User?
        get() = auth.currentUser?.toCommonUser()

    override val authStateChanges: Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser?.toCommonUser()
            trySend(user)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun signInWithGitHub(): Result<Unit> = try {
        if (activity == null) {
            Result.failure(Exception("Activity is required for GitHub sign-in"))
        } else {
            val provider = OAuthProvider.newBuilder("github.com")
            auth.startActivityForSignInWithProvider(activity, provider.build()).await()
            Result.success(Unit)
        }
    } catch (e: Exception) {
        Log.e("AndroidFirebaseService", "GitHub sign-in error", e)
        Result.failure(e)
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override suspend fun saveModuleLayout(modyilz: List<ModyilDeyda>) {
        val user = auth.currentUser ?: return
        try {
            val data = mapOf(
                "modyilz" to modyilz.map { it.toJson() },
                "updatedAt" to com.google.firebase.Timestamp.now()
            )
            db.collection("users")
                .document(user.uid)
                .collection("layouts")
                .document("current")
                .set(data)
                .await()
        } catch (e: Exception) {
            Log.e("AndroidFirebaseService", "Error saving layout", e)
        }
    }

    override fun watchModuleLayout(): Flow<List<ModyilDeyda>> = callbackFlow {
        val user = auth.currentUser
        if (user == null) {
            trySend(emptyList())
            // Don't close, keep it open in case user logs in later? 
            // Actually, we'll re-trigger it from UI if needed.
            awaitClose { }
            return@callbackFlow
        }

        val registration = db.collection("users")
            .document(user.uid)
            .collection("layouts")
            .document("current")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("AndroidFirebaseService", "Error watching layout", error)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    @Suppress("UNCHECKED_CAST")
                    val modyilzData = snapshot.get("modyilz") as? List<Map<String, Any>>
                    val modyilz = modyilzData?.map { ModyilDeyda.fromJson(it) } ?: emptyList()
                    trySend(modyilz)
                } else {
                    trySend(emptyList())
                }
            }
        awaitClose { registration.remove() }
    }
}

private fun com.google.firebase.auth.FirebaseUser.toCommonUser(): User {
    return User(
        uid = uid,
        email = email,
        displayName = displayName,
        photoUrl = photoUrl?.toString()
    )
}
