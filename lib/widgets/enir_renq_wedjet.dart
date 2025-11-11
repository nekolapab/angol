import 'package:flutter/material.dart';
import '../utils/heksagon_djeyometre.dart';

class EnirRenqWedjet extends StatelessWidget {
  final HeksagonDjeyometre geometry;
  final List<Widget> children;

  const EnirRenqWedjet({
    super.key,
    required this.geometry,
    required this.children,
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

        return Positioned(
          left: MediaQuery.of(context).size.width / 2 +
              position.x -
              geometry.hexWidth / 2,
          top: MediaQuery.of(context).size.height / 2 +
              position.y -
              geometry.hexHeight / 2,
          child: childWidget,
        );
      }).toList(),
    );
  }
}
