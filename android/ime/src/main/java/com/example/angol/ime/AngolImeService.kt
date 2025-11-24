package com.example.angol.ime

import android.inputmethodservice.InputMethodService
import android.view.View
import androidx.compose.ui.platform.ComposeView
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text

class AngolImeService : InputMethodService() {

    override fun onCreateInputView(): View {
        return ComposeView(this).apply {
            setContent {
                MaterialTheme {
                    Text("Hello Wear OS")
                }
            }
        }
    }
}
