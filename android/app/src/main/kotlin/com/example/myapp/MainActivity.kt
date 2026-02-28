package com.example.myapp

import android.content.Intent
import android.provider.Settings
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity: FlutterActivity() {
    private val CHANNEL = "com.example.angol/ime"

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler {
            call, result ->
            when (call.method) {
                "openImeSettings" -> {
                    val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
                    startActivity(intent)
                    result.success(null)
                }
                "isImeEnabled" -> {
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                    val enabledMethods = imm.enabledInputMethodList
                    val targetComponent = "$packageName/com.example.angol.ime.DaylEnpitMelxod"
                    // Check for both full component name and partial match to be safe
                    val isEnabled = enabledMethods.any { 
                        val componentId = "${it.packageName}/${it.serviceName}"
                        componentId == targetComponent || 
                        (it.packageName == packageName && it.serviceName.contains("DaylEnpitMelxod"))
                    }
                    result.success(isEnabled)
                }
                "isImeSelected" -> {
                    val currentId = Settings.Secure.getString(contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD)
                    val targetComponent = "$packageName/com.example.angol.ime.DaylEnpitMelxod"
                    val isSelected = currentId != null && (
                        currentId == targetComponent || 
                        (currentId.contains(packageName) && currentId.contains("DaylEnpitMelxod"))
                    )
                    result.success(isSelected)
                }
                "openInputMethodPicker" -> {
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                    imm.showInputMethodPicker()
                    result.success(null)
                }
                else -> result.notImplemented()
            }
        }
    }
}
