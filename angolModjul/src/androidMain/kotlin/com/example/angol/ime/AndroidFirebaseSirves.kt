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
    companion object {
        const val ACTION_UPDATE_LAYOUT = "io.angol.ACTION_UPDATE_LAYOUT"
        const val EXTRA_LAYOUT_JSON = "layout_json"
        const val EXTRA_ENVIRONMENT = "environment"
    }

    private val appContext = context.applicationContext
    private val activity: Activity? = context as? Activity
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        encodeDefaults = true
    }

    /** Same path for Activity + IME service process â€” enables layout sync without cloud. */
    private fun getLocalFile(env: String): File = File(appContext.filesDir, "layout_$env.json")

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
        val firebaseError = (e as? com.google.firebase.auth.FirebaseAuthException)?.errorCode ?: "UNKNOWN"
        Log.e("AndroidFirebaseSirves", "GitHub sign-in error [$firebaseError]: ${e.message}", e)
        Result.failure(Exception("[$firebaseError] ${e.message}"))
    }

    override suspend fun signInWithGoogle(): Result<Unit> = try {
        val act = activity ?: throw Exception("Activity is required for Google sign-in")
        val provider = OAuthProvider.newBuilder("google.com")
        
        auth.startActivityForSignInWithProvider(act, provider.build()).await()
        Log.d("AndroidFirebaseSirves", "Google sign-in successful")
        Result.success(Unit)
    } catch (e: Exception) {
        val firebaseError = (e as? com.google.firebase.auth.FirebaseAuthException)?.errorCode ?: "UNKNOWN"
        Log.e("AndroidFirebaseSirves", "Google sign-in error [$firebaseError]: ${e.message}", e)
        Result.failure(Exception("[$firebaseError] ${e.message}"))
    }

    override suspend fun signInAnonymously(): Result<Unit> = try {
        auth.signInAnonymously().await()
        Log.d("AndroidFirebaseSirves", "Anonymous sign-in successful")
        Result.success(Unit)
    } catch (e: Exception) {
        val firebaseError = (e as? com.google.firebase.auth.FirebaseAuthException)?.errorCode ?: "UNKNOWN"
        Log.e("AndroidFirebaseSirves", "Anonymous sign-in error [$firebaseError]: ${e.message}", e)
        Result.failure(Exception("[$firebaseError] ${e.message}"))
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override suspend fun seyvModjilLeyawt(modjilz: List<ModyilDeyda>, environment: String, ezRepleys: Boolean) {
        if (ezRepleys) {
            try {
                val replaceFile = File(appContext.filesDir, "replace_request.json")
                replaceFile.writeText(json.encodeToString(modjilz))
                Log.d("AndroidFirebaseSirves", "Saved replace request locally to ${replaceFile.absolutePath}")
            } catch (e: Exception) {
                Log.e("AndroidFirebaseSirves", "Error saving replace request", e)
            }
        }

        val user = auth.currentUser
        if (user != null) {
            try {
                val data = mapOf(
                    "modjilz" to modjilz.map { it.toJson() },
                    "updatedAt" to com.google.firebase.Timestamp.now(),
                    "ezRepleys" to ezRepleys
                )
                db.collection("users")
                    .document(user.uid)
                    .collection("layouts")
                    .document(environment)
                    .set(data)
                    .await()
            } catch (e: Exception) {
                Log.e("AndroidFirebaseSirves", "Error saving layout to cloud ($environment)", e)
            }
        }

        try {
            val jsonString = json.encodeToString(modjilz)
            seyvLokale(modjilz, environment)

            // Broadcast to other apps (bridge dayl -> kepad), but ONLY if not already
            // called from within the broadcast receiver to prevent an infinite loop.
            if (!ezRepleys) {
                val intent = Intent(ACTION_UPDATE_LAYOUT).apply {
                    putExtra(EXTRA_LAYOUT_JSON, jsonString)
                    putExtra(EXTRA_ENVIRONMENT, environment)
                }
                appContext.sendBroadcast(intent)
            }
        } catch (e: Exception) {
            Log.e("AndroidFirebaseSirves", "Error encoding layout for broadcast/local save", e)
        }
    }

    private fun seyvLokale(modjilz: List<ModyilDeyda>, environment: String) {
        try {
            val content = json.encodeToString(modjilz)
            val file = getLocalFile(environment)
            file.writeText(content)
            Log.d("AndroidFirebaseSirves", "Saved locally to ${file.absolutePath}")
            brodkastLeyawt(content, environment)
        } catch (e: Exception) {
            Log.e("AndroidFirebaseSirves", "Error saving locally", e)
        }
    }

    /** Writes to disk WITHOUT broadcasting â€” used by cloud sync to avoid overwriting IME state. */
    private fun seyvLokaleSaylent(modjilz: List<ModyilDeyda>, environment: String) {
        try {
            val content = json.encodeToString(modjilz)
            val file = getLocalFile(environment)
            file.writeText(content)
            Log.d("AndroidFirebaseSirves", "Saved locally (silent) to ${file.absolutePath}")
        } catch (e: Exception) {
            Log.e("AndroidFirebaseSirves", "Error saving locally (silent)", e)
        }
    }

    private fun brodkastLeyawt(jsonString: String, environment: String) {
        val intent = Intent(ACTION_UPDATE_LAYOUT).apply {
            putExtra(EXTRA_LAYOUT_JSON, jsonString)
            putExtra(EXTRA_ENVIRONMENT, environment)
        }
        appContext.sendBroadcast(intent)
        Log.d("AndroidFirebaseSirves", "Broadcasted layout update ($environment)")
    }

    override fun watcModjilLeyawt(environment: String): Flow<List<ModyilDeyda>> = callbackFlow {
        val user = auth.currentUser
        Log.d("AndroidFirebaseSirves", "Starting watch for environment: $environment. User logged in: ${user != null}, UID: ${user?.uid}")

        val local = loadLocally(environment)
        if (local.isNotEmpty()) {
            trySend(local)
            // Do NOT broadcast on startup â€” only broadcast on explicit saves.
            // Startup broadcast caused a race: Dayl and Kepad IME would overwrite each other's
            // live layout with the stale local file whenever either app started.
        }

        if (user == null) {
            val localFile = getLocalFile(environment)
            var lastMod = if (localFile.exists()) localFile.lastModified() else 0L
            val job = launch {
                while (isActive) {
                    delay(800)
                    if (!localFile.exists()) continue
                    val m = localFile.lastModified()
                    if (m != lastMod) {
                        lastMod = m
                        val updated = loadLocally(environment)
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
            .document(environment)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("AndroidFirebaseSirves", "Error watching layout ($environment) for UID: ${user.uid}", error)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    Log.d("AndroidFirebaseSirves", "Received cloud update ($environment) for UID: ${user.uid}")
                    @Suppress("UNCHECKED_CAST")
                    val modyilzData = snapshot.get("modjilz") as? List<Map<String, Any>>
                    val modjilz = modyilzData?.map { ModyilDeyda.fromJson(it) } ?: emptyList()
                    if (modjilz.isNotEmpty()) {
                        trySend(modjilz)
                        seyvLokaleSaylent(modjilz, environment)
                        // Cloud sync only updates the local file silently.
                        // Kepad IME is updated only by explicit user-action saves.
                    }
                } else {
                    Log.d("AndroidFirebaseSirves", "Cloud document ($environment) empty or non-existent for UID: ${user.uid}")
                }
            }
        awaitClose { registration.remove() }
    }

    private fun loadLocally(environment: String): List<ModyilDeyda> {
        try {
            val file = getLocalFile(environment)
            if (!file.exists()) return emptyList()
            val content = file.readText()
            return if (content.isEmpty()) emptyList() else json.decodeFromString(content)
        } catch (e: Exception) {
            Log.e("AndroidFirebaseSirves", "Error loading locally ($environment)", e)
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


