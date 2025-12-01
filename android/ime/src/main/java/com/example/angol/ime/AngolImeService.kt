package com.example.angol.ime

import android.inputmethodservice.InputMethodService
import android.view.View
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
    }

    override fun onCreateInputView(): View {
        val view = ComposeView(this)
        
        // Set the owners for the ComposeView to work within a Service
        view.setViewTreeLifecycleOwner(this)
        view.setViewTreeViewModelStoreOwner(this)
        view.setViewTreeSavedStateRegistryOwner(this)

        view.setContent {
            KepadModyil(
                isKeypadVisible = true,
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
                                ic.deleteSurroundingText(toDelete + diff, 0)
                            }
                        } else {
                            if (primaryChar != null) {
                                // Replace primary char
                                ic.deleteSurroundingText(primaryChar.length, 0)
                                ic.commitText(char, 1)
                            } else {
                                // Special case: replace last char (e.g. long press on a key that has no secondary but user wants alt behavior?)
                                // Or just consistency with Flutter logic
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
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        viewModelStore.clear()
    }
}