import 'package:flutter/foundation.dart';

class InputService extends ChangeNotifier {
  static final InputService _instance = InputService._internal();

  factory InputService() {
    return _instance;
  }

  InputService._internal();

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
    return '...${_inputText.substring(_inputText.length - 7)}';
  }

  void clearText() {
    _inputText = '';
    notifyListeners();
  }
}
