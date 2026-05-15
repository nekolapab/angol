package com.example.angol.ime

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import modalz.ModyilDeyda
import sirvesez.FirebaseSirves
import sirvesez.User
import java.io.File
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

class AndroidFirebaseSirves(
    context: Context
) : FirebaseSirves {
    private val appContext = context.applicationContext
    private val activity: Activity? = context as? Activity
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        encodeDefaults = true
    }

    /** Same path for Activity + IME service process — enables layout sync without cloud. */
    private val localLayoutFile: File = File(appContext.filesDir, "local_layout.json")

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
        val act = activity ?: throw Exception("Activity is required for GitHub sign-in")
        val provider = OAuthProvider.newBuilder("github.com")
        provider.addCustomParameter("allow_signup", "true")
        
        auth.startActivityForSignInWithProvider(act, provider.build()).await()
        Log.d("AndroidFirebaseSirves", "GitHub sign-in successful")
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e("AndroidFirebaseSirves", "GitHub sign-in error: ${e.message}", e)
        Result.failure(e)
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override suspend fun saveModuleLayout(modyilz: List<ModyilDeyda>) {
        val user = auth.currentUser
        if (user != null) {
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
                Log.e("AndroidFirebaseSirves", "Error saving layout to cloud", e)
            }
        }

        saveLocally(modyilz)
    }

    private fun saveLocally(modyilz: List<ModyilDeyda>) {
        try {
            val content = json.encodeToString(modyilz)
            localLayoutFile.writeText(content)
            Log.d("AndroidFirebaseSirves", "Saved locally to ${localLayoutFile.absolutePath}")
        } catch (e: Exception) {
            Log.e("AndroidFirebaseSirves", "Error saving locally", e)
        }
    }

    override fun watchModuleLayout(): Flow<List<ModyilDeyda>> = callbackFlow {
        val user = auth.currentUser

        val local = loadLocally()
        if (local.isNotEmpty()) {
            trySend(local)
        }

        if (user == null) {
            var lastMod = if (localLayoutFile.exists()) localLayoutFile.lastModified() else 0L
            val job = launch {
                while (isActive) {
                    delay(800)
                    if (!localLayoutFile.exists()) continue
                    val m = localLayoutFile.lastModified()
                    if (m != lastMod) {
                        lastMod = m
                        val updated = loadLocally()
                        if (updated.isNotEmpty()) trySend(updated)
                    }
                }
            }
            awaitClose { job.cancel() }
            return@callbackFlow
        }

        val registration = db.collection("users")
            .document(user.uid)
            .collection("layouts")
            .document("current")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("AndroidFirebaseSirves", "Error watching layout", error)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    @Suppress("UNCHECKED_CAST")
                    val modyilzData = snapshot.get("modyilz") as? List<Map<String, Any>>
                    val modyilz = modyilzData?.map { ModyilDeyda.fromJson(it) } ?: emptyList()
                    if (modyilz.isNotEmpty()) {
                        trySend(modyilz)
                        saveLocally(modyilz)
                    }
                }
            }
        awaitClose { registration.remove() }
    }

    private fun loadLocally(): List<ModyilDeyda> {
        try {
            if (!localLayoutFile.exists()) return emptyList()
            val content = localLayoutFile.readText()
            return if (content.isEmpty()) emptyList() else json.decodeFromString(content)
        } catch (e: Exception) {
            Log.e("AndroidFirebaseSirves", "Error loading locally", e)
            return emptyList()
        }
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
