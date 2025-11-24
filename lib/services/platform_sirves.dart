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
}
