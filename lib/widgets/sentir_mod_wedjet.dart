import 'package:flutter/material.dart';
import '../utils/heksagon_djeyometre.dart';
import 'heksagon_wedjet.dart';

class SentirModWedjet extends StatelessWidget {
  final HeksagonDjeyometre geometry;
  final ValueChanged<bool>? onPressedChanged;
  final VoidCallback? onTap;
  final VoidCallback? onLongPress;
  final Color backgroundColor;
  final Color textColor;
  final String label;

  const SentirModWedjet({
    super.key,
    required this.geometry,
    this.onPressedChanged,
    this.onTap,
    this.onLongPress,
    required this.backgroundColor,
    required this.textColor,
    this.label = '',
  });

  @override
  Widget build(BuildContext context) {
    return Positioned(
      left:
          MediaQuery.of(context).size.width / 2 - geometry.hexWidth / 2,
      top: MediaQuery.of(context).size.height / 2 -
          geometry.hexHeight / 2,
      child: HeksagonWedjet(
        label: label,
        backgroundColor: backgroundColor,
        textColor: textColor,
        size: geometry.hexWidth,
        rotationAngle: geometry.rotationAngle,
        onTap: onTap,
        onLongPress: onLongPress,
        onPressedChanged: onPressedChanged,
      ),
    );
  }
}
