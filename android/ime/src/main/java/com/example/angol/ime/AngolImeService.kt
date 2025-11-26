package com.example.angol.ime

import android.content.Intent
import android.provider.Settings
import android.inputmethodservice.InputMethodService
import android.view.View
import androidx.compose.ui.platform.ComposeView
import androidx.wear.compose.material.MaterialTheme
import com.example.angol.ime.compose.Keypad
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.plugin.common.MethodChannel
import io.flutter.view.FlutterMain

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
    }

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