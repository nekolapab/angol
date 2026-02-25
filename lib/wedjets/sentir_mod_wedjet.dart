import 'package:flutter/material.dart';
import '../yutelez/heksagon_djeyometre.dart';
import 'heksagon_wedjet.dart';

class SentirModWedjet extends StatelessWidget {
  final HeksagonDjeyometre geometry;
  final ValueChanged<bool>? onPressedChanged;
  final VoidCallback? onTap;
  final VoidCallback? onLongPress;
  final Color backgroundColor;
  final Color textColor;
  final String label;
  final Function(bool)? onHover;
  final Widget? child; // Add this line

  const SentirModWedjet({
    super.key,
    required this.geometry,
    this.onPressedChanged,
    this.onTap,
    this.onLongPress,
    required this.backgroundColor,
    required this.textColor,
    this.label = '',
    this.onHover,
    this.child, // Add this line
  });

  @override
  Widget build(BuildContext context) {
    return HeksagonWedjet(
      label: label,
      backgroundColor: backgroundColor,
      textColor: textColor,
      size: geometry.heksWidlx,
      rotationAngle: geometry.roteyconAngol,
      onTap: onTap,
      onLongPress: onLongPress,
      onPressedChanged: onPressedChanged,
      onHover: onHover,
      child: child, // Pass the child here
    );
  }
}
