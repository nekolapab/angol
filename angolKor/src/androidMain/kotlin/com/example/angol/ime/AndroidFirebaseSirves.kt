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

    /** Same path for Activity + IME service process — enables layout sync without cloud. */
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
        Log.e("AndroidFirebaseSirves", "GitHub sign-in error: ${e.message}", e)
        Result.failure(e)
    }

    override suspend fun signInWithGoogle(): Result<Unit> = try {
        val act = activity ?: throw Exception("Activity is required for Google sign-in")
        val provider = OAuthProvider.newBuilder("google.com")
        
        auth.startActivityForSignInWithProvider(act, provider.build()).await()
        Log.d("AndroidFirebaseSirves", "Google sign-in successful")
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e("AndroidFirebaseSirves", "Google sign-in error: ${e.message}", e)
        Result.failure(e)
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override suspend fun saveModuleLayout(modyilz: List<ModyilDeyda>, environment: String) {
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
                    .document(environment)
                    .set(data)
                    .await()
            } catch (e: Exception) {
                Log.e("AndroidFirebaseSirves", "Error saving layout to cloud ($environment)", e)
            }
        }

        val jsonString = json.encodeToString(modyilz)
        saveLocally(modyilz, environment)
        
        // Broadcast to other apps (bridge dayl -> kepad)
        val intent = Intent(ACTION_UPDATE_LAYOUT).apply {
            putExtra(EXTRA_LAYOUT_JSON, jsonString)
            putExtra(EXTRA_ENVIRONMENT, environment)
        }
        appContext.sendBroadcast(intent)
    }

    private fun saveLocally(modyilz: List<ModyilDeyda>, environment: String) {
        try {
            val content = json.encodeToString(modyilz)
            val file = getLocalFile(environment)
            file.writeText(content)
            Log.d("AndroidFirebaseSirves", "Saved locally to ${file.absolutePath}")
            broadcastLayout(content, environment)
        } catch (e: Exception) {
            Log.e("AndroidFirebaseSirves", "Error saving locally", e)
        }
    }

    private fun broadcastLayout(jsonString: String, environment: String) {
        val intent = Intent(ACTION_UPDATE_LAYOUT).apply {
            putExtra(EXTRA_LAYOUT_JSON, jsonString)
            putExtra(EXTRA_ENVIRONMENT, environment)
        }
        appContext.sendBroadcast(intent)
        Log.d("AndroidFirebaseSirves", "Broadcasted layout update ($environment)")
    }

    override fun watchModuleLayout(environment: String): Flow<List<ModyilDeyda>> = callbackFlow {
        val user = auth.currentUser
        Log.d("AndroidFirebaseSirves", "Starting watch for environment: $environment. User logged in: ${user != null}, UID: ${user?.uid}")

        val local = loadLocally(environment)
        if (local.isNotEmpty()) {
            trySend(local)
            // Ensure other apps (kepad) see this local layout on startup
            broadcastLayout(json.encodeToString(local), environment)
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
                    val modyilzData = snapshot.get("modyilz") as? List<Map<String, Any>>
                    val modyilz = modyilzData?.map { ModyilDeyda.fromJson(it) } ?: emptyList()
                    if (modyilz.isNotEmpty()) {
                        trySend(modyilz)
                        saveLocally(modyilz, environment)
                        
                        // Broadcast update received from cloud
                        val intent = Intent(ACTION_UPDATE_LAYOUT).apply {
                            action = ACTION_UPDATE_LAYOUT
                            putExtra(EXTRA_LAYOUT_JSON, json.encodeToString(modyilz))
                            putExtra(EXTRA_ENVIRONMENT, environment)
                        }
                        appContext.sendBroadcast(intent)
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
