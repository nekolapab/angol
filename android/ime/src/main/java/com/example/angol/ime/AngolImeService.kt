package com.example.angol.ime

import android.content.Intent
import android.provider.Settings
import android.inputmethodservice.InputMethodService
import android.view.View
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.LifecycleOwner
import com.example.angol.ime.compose.Keypad
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.plugin.common.MethodChannel
import io.flutter.view.FlutterMain

<<<<<<< HEAD
class AngolImeService : InputMethodService(), LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    override val viewModelStore = ViewModelStore()

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
=======
class AngolImeService : InputMethodService() {
    private lateinit var channel: MethodChannel
    private lateinit var flutterEngine: FlutterEngine

    override fun onCreate() {
        super.onCreate()
        // Initialize FlutterEngine
        flutterEngine = FlutterEngine(this)
        flutterEngine.dartExecutor.executeDartEntrypoint(
            DartExecutor.DartEntrypoint(
                FlutterMain.findAppBundlePath(),
                "main" // This is the entrypoint in your Flutter app
            )
        )

        // Setup MethodChannel
        channel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger, "com.example.angol/ime")
        channel.setMethodCallHandler { call, result ->
            if (call.method == "openImeSettings") {
                val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                result.success(null)
            } else {
                result.notImplemented()
            }
        }
    }

    override fun onDestroy() {
        flutterEngine.destroy()
        super.onDestroy()
>>>>>>> e2b2f4873d1e9f147e4a24bfc002ac0e52bf1a3d
    }

    override fun onCreateInputView(): View {
        val view = ComposeView(this)
        
        // Set the owners for the ComposeView to work within a Service
        view.setViewTreeLifecycleOwner(this)
        view.setViewTreeViewModelStoreOwner(this)
        view.setViewTreeSavedStateRegistryOwner(this)

        view.setContent {
            Keypad(
                onKeyPress = { label ->
                    currentInputConnection?.commitText(label, 1)
                },
                onDelete = {
                    currentInputConnection?.deleteSurroundingText(1, 0)
                },
                onSpace = {
                    currentInputConnection?.commitText(" ", 1)
                },
                onSwitchMode = {
                    // Handle mode switch if necessary, or leave it to the Keypad state
                }
            )
        }
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        viewModelStore.clear()
    }
}