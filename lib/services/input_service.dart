import 'package:flutter/material.dart';

class InputService extends ChangeNotifier {
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
  
  void deleteRight() {
    deleteLeft();
  }
  
  void setCapitalize() {
    _shouldCapitalize = true;
  }
  
  void toggleMode() {
    _isLetterMode = !_isLetterMode;
    notifyListeners();
  }
  
  void setTextFieldFocus(bool focused) {
    _isTextFieldFocused = focused;
    notifyListeners();
  }
  
  void clearText() {
    _inputText = '';
    notifyListeners();
  }
  
  String getDisplayText() {
    if (_inputText.isEmpty) return '';
    if (_inputText.length <= 7) return _inputText;
    return '...${_inputText.substring(_inputText.length - 7)}';
  }
}
