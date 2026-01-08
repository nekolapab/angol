package com.example.angol.ime

import android.inputmethodservice.InputMethodService
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import modyilz.KepadModyil

private const val TAG = "AngolImeService"

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
        Log.d(TAG, "onCreate: IME Service created")
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        
        // Attempt to attach owners to the window's decor view
        try {
            window.window?.decorView?.let {
                it.setViewTreeLifecycleOwner(this)
                it.setViewTreeViewModelStoreOwner(this)
                it.setViewTreeSavedStateRegistryOwner(this)
            }
        } catch (e: Exception) {
            Log.e(TAG, "onCreate: Failed to attach lifecycle/viewmodel/savedstate owners to window decor view: ${e.message}")
        }
    }

    override fun onCreateInputView(): View {
        Log.d(TAG, "onCreateInputView: Creating input view")
        val composeView = ComposeView(this)
        
        // Set owners directly on the ComposeView
        composeView.setViewTreeLifecycleOwner(this)
        composeView.setViewTreeViewModelStoreOwner(this)
        composeView.setViewTreeSavedStateRegistryOwner(this)

        composeView.setContent {
            KepadModyil(
                isKeypadVisible = true, // IME service controls visibility, so always true when shown
                displayLength = 7,
                onHexKeyPress = { char, isLongPress, primaryChar ->
                    val ic = currentInputConnection ?: return@KepadModyil
                    
                    if (isLongPress) {
                        if (char == "⌫") {
                            // Delete word logic
                            val before = ic.getTextBeforeCursor(50, 0)
                            if (!before.isNullOrEmpty()) {
                                val trimmed = before.trimEnd()
                                val diff = before.length - trimmed.length
                                val lastSpace = trimmed.lastIndexOf(' ')
                                val toDelete = if (lastSpace != -1) trimmed.length - 1 - lastSpace else trimmed.length
                                // Delete only the word characters (toDelete), preserving trailing spaces (diff)
                                ic.deleteSurroundingText(toDelete, 0)
                            }
                        } else {
                            if (primaryChar != null) {
                                // Replace primary char
                                ic.deleteSurroundingText(primaryChar.length, 0)
                                ic.commitText(char, 1)
                            } else {
                                ic.deleteSurroundingText(1, 0)
                                ic.commitText(char, 1)
                            }
                        }
                    } else {
                        // Short press
                        if (char == "⌫") {
                            ic.deleteSurroundingText(1, 0)
                        } else {
                            ic.commitText(char, 1)
                        }
                    }
                }
            )
        }
        return composeView
    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        Log.d(TAG, "onStartInput: Input started, restarting: $restarting, editorType: ${attribute?.imeOptions}")
        // Here you might decide to show your UI if it's not already visible
        // currentInputConnection?.let { it.reportFullscreenMode(false) } // Example: not in fullscreen
    }

    override fun onFinishInput() {
        super.onFinishInput()
        Log.d(TAG, "onFinishInput: Input finished")
        // Hide your UI if it's visible
    }

    override fun onWindowShown() {
        super.onWindowShown()
        Log.d(TAG, "onWindowShown: IME window shown")
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun onWindowHidden() {
        super.onWindowHidden()
        Log.d(TAG, "onWindowHidden: IME window hidden")
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: IME Service destroyed")
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        viewModelStore.clear()
    }
}
