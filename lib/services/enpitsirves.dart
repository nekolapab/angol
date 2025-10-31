import 'package:flutter/foundation.dart';
import 'dart:developer' as developer;

class EnpitSirves extends ChangeNotifier {
  static final EnpitSirves _instance = EnpitSirves._();

  factory EnpitSirves() {
    return _instance;
  }

  EnpitSirves._();

  String _inputText = '';
  bool _isLetterMode = true;
  bool _shouldCapitalize = false;
  bool _isTextFieldFocused = false;

  String get inputText => _inputText;
  bool get isLetterMode => _isLetterMode;
  bool get isTextFieldFocused => _isTextFieldFocused;

  void addCharacter(String char) {
    if (char == '⌫') {
      deleteLeft();
      return;
    }

    String finalChar = _shouldCapitalize ? char.toUpperCase() : char;
    _inputText += finalChar;
    _shouldCapitalize = false;
    notifyListeners();
  }

  void deleteLeft() {
    if (_inputText.isNotEmpty) {
      _inputText = _inputText.substring(0, _inputText.length - 1);
      notifyListeners();
    }
  }

  void deleteWord() {
    if (_inputText.isEmpty) return;

    String trimmedText = _inputText.trimRight();
    int lastSpaceIndex = trimmedText.lastIndexOf(' ');

    if (lastSpaceIndex != -1) {
      _inputText = trimmedText.substring(0, lastSpaceIndex);
    } else {
      _inputText = '';
    }
    notifyListeners();
  }

  void deleteCharacters(int count) {
    if (_inputText.isNotEmpty && _inputText.length >= count) {
      _inputText = _inputText.substring(0, _inputText.length - count);
      notifyListeners();
    } else if (_inputText.isNotEmpty && _inputText.length < count) {
      _inputText = '';
      notifyListeners();
    }
  }

  void toggleMode() {
    _isLetterMode = !_isLetterMode;
    notifyListeners();
  }

  void setCapitalize() {
    _shouldCapitalize = true;
  }

  void setTextFieldFocus(bool focused) {
    _isTextFieldFocused = focused;
    notifyListeners();
  }

  String getDisplayText() {
    if (_inputText.isEmpty) return '';
    if (_inputText.length <= 7) return _inputText;
    return _inputText.substring(_inputText.length - 7);
  }

  void clearText() {
    _inputText = '';
    developer.log('InputService: _inputText cleared. New value: "$_inputText"');
    notifyListeners();
  }
}
