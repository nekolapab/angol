package com.example.angol.ime

import android.inputmethodservice.InputMethodService
import android.view.View
import com.example.angol.ime.R

class AngolImeService : InputMethodService() {

    override fun onCreateInputView(): View {
        return layoutInflater.inflate(R.layout.keyboard_layout, null)
    }
}
