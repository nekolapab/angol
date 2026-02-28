import 'package:flutter/services.dart';
import 'dart:developer' as developer;

class PlatformSirves {
  static const MethodChannel _channel = MethodChannel('com.example.angol/ime');

  static Future<void> openImeSettings() async {
    try {
      await _channel.invokeMethod('openImeSettings');
    } on PlatformException catch (e) {
      developer.log("Failed to open IME settings: '${e.message}'.");
    }
  }

  static Future<bool> isImeEnabled() async {
    try {
      return await _channel.invokeMethod('isImeEnabled') ?? false;
    } on PlatformException catch (e) {
      developer.log("Failed to check if IME is enabled: '${e.message}'.");
      return false;
    }
  }

  static Future<bool> isImeSelected() async {
    try {
      return await _channel.invokeMethod('isImeSelected') ?? false;
    } on PlatformException catch (e) {
      developer.log("Failed to check if IME is selected: '${e.message}'.");
      return false;
    }
  }

  static Future<void> openInputMethodPicker() async {
    try {
      await _channel.invokeMethod('openInputMethodPicker');
    } on PlatformException catch (e) {
      developer.log("Failed to open input method picker: '${e.message}'.");
    }
  }
}
