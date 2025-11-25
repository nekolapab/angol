package com.example.angol.ime

import android.inputmethodservice.InputMethodService
import android.view.View
import androidx.compose.ui.platform.ComposeView
import androidx.wear.compose.material.MaterialTheme
import com.example.angol.ime.compose.Keypad

class AngolImeService : InputMethodService() {

    override fun onCreateInputView(): View {
        return ComposeView(this).apply {
            setContent {
                MaterialTheme {
                    Keypad(
                        onKeyPress = { text -> 
                            currentInputConnection?.commitText(text, 1) 
                        },
                        onDelete = { 
                            currentInputConnection?.deleteSurroundingText(1, 0) 
                        },
                        onSpace = { 
                            currentInputConnection?.commitText(" ", 1) 
                        },
                        onSwitchMode = { 
                            // Optional: Vibrate or sound feedback
                        }
                    )
                }
            }
        }
    }
}