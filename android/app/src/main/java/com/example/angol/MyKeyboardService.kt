package com.example.angol

import android.inputmethodservice.InputMethodService
import android.util.Log
import android.view.View
import android.widget.TextView

class MyKeyboardService : InputMethodService() {

    override fun onCreate() {
        super.onCreate()
        Log.d("MyKeyboardService", "onCreate called")
    }

    override fun onCreateInputView(): View {
        Log.d("MyKeyboardService", "onCreateInputView called")
        // For now, return a simple TextView.
        // We will replace this with our Flutter KepadModyil later.
        val tv = TextView(this)
        tv.text = "Hello from My Kepad!"
        return tv
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MyKeyboardService", "onDestroy called")
    }
}
