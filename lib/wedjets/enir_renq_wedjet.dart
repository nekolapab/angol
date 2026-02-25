import 'package:flutter/material.dart';
import 'heksagon_wedjet.dart';
import '../yutelez/heksagon_djeyometre.dart';

class EnirRenqWedjet extends StatelessWidget {
  final HeksagonDjeyometre geometry;
  final List<Widget> children;
  final Function(bool)? onHover;
  final double stackWidth;
  final double stackHeight;

  const EnirRenqWedjet({
    super.key,
    required this.geometry,
    required this.children,
    this.onHover,
    required this.stackWidth,
    required this.stackHeight,
  });

  @override
  Widget build(BuildContext context) {
    final innerCoords = geometry.getEnirRenqKowordenats();

    return Stack(
      children: children.asMap().entries.map((entry) {
        final index = entry.key;
        if (index >= innerCoords.length) {
          return const SizedBox
              .shrink(); // Return an empty widget if out of bounds
        }

        final coord = innerCoords[index];
        final childWidget = entry.value;
        final position = geometry.aksyalTuPeksel(coord.q, coord.r);

        Widget positionedChild = childWidget;
        if (childWidget is HeksagonWedjet) {
          positionedChild = HeksagonWedjet(
            key: childWidget.key,
            label: childWidget.label,
            secondaryLabel: childWidget.secondaryLabel,
            backgroundColor: childWidget.backgroundColor,
            textColor: childWidget.textColor,
            size: childWidget.size,
            isPressed: childWidget.isPressed,
            isHovering: childWidget.isHovering,
            onTap: childWidget.onTap,
            onLongPress: childWidget.onLongPress,
            onHover: onHover, // Pass the onHover callback
            rotationAngle: geometry.roteyconAngol,
            fontSize: childWidget.fontSize,
            onPressedChanged: childWidget.onPressedChanged,
            child: childWidget.child,
          );
        }

        return Positioned(
          left: stackWidth / 2 + position.x - geometry.heksWidlx / 2,
          top: stackHeight / 2 + position.y - geometry.heksHayt / 2,
          child: positionedChild,
        );
      }).toList(),
    );
  }
}
