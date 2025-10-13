import 'package:flutter/material.dart';

class KeypadConfig {
  static const List<String> innerLetterMode = ['a', 'e', 'i', 'u', 'o', '⌫'];
  static const List<String> innerNumberMode = ['+', '*', '=', '(', '{', '⌫'];
  
  // Clockwise from 1 o'clock
  static const List<String> outerTap = [
    's', 'l', 'lx', 'x', 'd', 't', 
    'c', 'g', 'k', 'f', 'b', 'p'
  ];
  
  static const List<String> outerLongPress = [
    'z', 'lh', 'h', 'n', 'y', 'r', 
    'j', 'ng', 'q', 'v', 'w', 'm'
  ];
  
  static const List<String> innerLongPressNumber = ['-', '/', ':', ')', '}', ''];
  
  // 12 rainbow colors for outer ring
  static const List<Color> rainbowColors = [
    Color(0xFFFF0000), // red
    Color(0xFFFF8000), // orange
    Color(0xFFFFFF00), // yellow
    Color(0xFF80FF00), // chartreuse
    Color(0xFF00FF00), // green
    Color(0xFF00FF80), // turquoise
    Color(0xFF00FFFF), // aqua
    Color(0xFF0080FF), // azure
    Color(0xFF0000FF), // blue
    Color(0xFF8000FF), // purple
    Color(0xFFFF00FF), // fuchsia
    Color(0xFF800000), // maroon
  ];
  
  // Complementary colors
  static Color getComplementaryColor(Color color) {
    return Color.fromARGB(
      color.alpha,
      255 - color.red,
      255 - color.green,
      255 - color.blue,
    );
  }
}
