import 'package:flutter/foundation.dart';
import 'dart:developer' as developer;

class EnpitSirves extends ChangeNotifier {

  final StringBuffer _inputText = StringBuffer();
  bool _isLetterMode = true;
  bool _shouldCapitalize = false;
  bool _isTextFieldFocused = false;

  String get inputText => _inputText.toString();
  bool get isLetterMode => _isLetterMode;
  bool get isTextFieldFocused => _isTextFieldFocused;

  void addCharacter(String char) {
    developer.log('EnpitSirves: addCharacter called with: $char');
    if (char == '⌫') {
      deleteLeft();
      developer.log('EnpitSirves: Deleting left via addCharacter.');
      return;
    }

    String finalChar = _shouldCapitalize ? char.toUpperCase() : char;
    _inputText.write(finalChar);
    _shouldCapitalize = false;
    developer.log('EnpitSirves: _inputText updated to: "${_inputText.toString()}"');
    notifyListeners();
    developer.log('EnpitSirves: notifyListeners called.');
  }

  void deleteLeft() {
    if (_inputText.isNotEmpty) {
      String currentText = _inputText.toString();
      _inputText.clear();
      _inputText.write(currentText.substring(0, currentText.length - 1));
      notifyListeners();
    }
  }

  void deleteWord() {
    if (_inputText.isEmpty) return;

    String currentText = _inputText.toString();
    String trimmedText = currentText.trimRight();
    int lastSpaceIndex = trimmedText.lastIndexOf(' ');

    _inputText.clear();
    if (lastSpaceIndex != -1) {
      _inputText.write(trimmedText.substring(0, lastSpaceIndex));
    }
    else {
      _inputText.write('');
    }
    notifyListeners();
  }

  void deleteCharacters(int count) {
    String currentText = _inputText.toString();
    if (currentText.isNotEmpty && currentText.length >= count) {
      _inputText.clear();
      _inputText.write(currentText.substring(0, currentText.length - count));
      notifyListeners();
    } else if (currentText.isNotEmpty && currentText.length < count) {
      _inputText.clear();
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

  String getDisplayText(int displayLength) {
    if (_inputText.isEmpty) return ' ' * displayLength;
    String currentText = _inputText.toString();
    if (currentText.length >= displayLength) {
      return currentText.substring(currentText.length - displayLength);
    } else {
      return currentText.padLeft(displayLength, ' ');
    }
  }

  void clearText() {
    _inputText.clear();
    developer.log(
        'InputService: _inputText cleared. New value: "${_inputText.toString()}"');
    notifyListeners();
  }
}
