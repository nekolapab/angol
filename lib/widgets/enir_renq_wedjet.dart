import 'package:flutter/material.dart';
import 'heksagon_wedjet.dart';
import '../utils/heksagon_djeyometre.dart';

class EnirRenqWedjet extends StatelessWidget {
  final HeksagonDjeyometre geometry;
  final List<Widget> children;
  final Function(bool)? onHover; // Add this line

  const EnirRenqWedjet({
    super.key,
    required this.geometry,
    required this.children,
    this.onHover, // Add this line
  });

  @override
  Widget build(BuildContext context) {
    final innerCoords = geometry.getInnerRingCoordinates();

    return Stack(
      children: children.asMap().entries.map((entry) {
        final index = entry.key;
        if (index >= innerCoords.length) {
          return const SizedBox
              .shrink(); // Return an empty widget if out of bounds
        }

        final coord = innerCoords[index];
        final childWidget = entry.value;
        final position = geometry.axialToPixel(coord.q, coord.r);

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
            rotationAngle: childWidget.rotationAngle,
            fontSize: childWidget.fontSize,
            onPressedChanged: childWidget.onPressedChanged,
            child: childWidget.child,
          );
        }

        return Positioned(
          left: MediaQuery.of(context).size.width / 2 +
              position.x -
              geometry.hexWidth / 2,
          top: MediaQuery.of(context).size.height / 2 +
              position.y -
              geometry.hexHeight / 2,
          child: positionedChild,
        );
      }).toList(),
    );
  }
}
