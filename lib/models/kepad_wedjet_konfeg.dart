import 'package:flutter/material.dart';

class Kepadkonfeg {
  static const List<String> innerLetterMode = ['a', 'e', 'i', 'u', 'o', '⌫'];
  static const List<String> innerNumberMode = ['+', '*', '=', '(', '{', '⌫'];

  static const List<String> outerTap = [
    's',
    'l',
    'lx',
    'x',
    'd',
    't',
    'c',
    'g',
    'k',
    'f',
    'b',
    'p'
  ];

  static const List<String> outerLongPress = [
    'z',
    'lh',
    'h',
    'n',
    'y',
    'r',
    'j',
    'nq',
    'q',
    'v',
    'w',
    'm'
  ];

  static const List<String> outerTapNumber = [
    '1',
    '2',
    '3',
    '4',
    '5',
    '6',
    '7',
    '8',
    '9',
    '0',
    'A',
    'O'
  ];

  static const List<String> outerLongPressNumber = [
    '!',
    '!#',
    '\$',
    '%',
    '^',
    '&',
    '*',
    '(',
    ')',
    '_',
    '|',
    '~'
  ];

  static const List<String> innerLongPressNumber = [
    '-',
    '/',
    ':',
    ')',
    '}',
    '⌫'
  ];

  static const List<Color> rainbowColors = [
    Color(0xFFFF0000),
    Color(0xFFFF8000),
    Color(0xFFFFFF00),
    Color(0xFF80FF00),
    Color(0xFF00FF00),
    Color(0xFF00FF80),
    Color(0xFF00FFFF),
    Color(0xFF0080FF),
    Color(0xFF0000FF),
    Color(0xFF8000FF),
    Color(0xFFFF00FF),
    Color(0xFF800000),
  ];

  static final List<Color> innerRingColors = [
    rainbowColors[0],
    rainbowColors[2],
    rainbowColors[4],
    rainbowColors[6],
    rainbowColors[8],
    rainbowColors[10],
  ];

  static Color getComplementaryColor(Color color) {
    return Color.fromARGB(
      color.a.round(),
      (255 - color.r).round(),
      (255 - color.g).round(),
      (255 - color.b).round(),
    );
  }
}

