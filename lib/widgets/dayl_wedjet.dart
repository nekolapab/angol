import 'package:flutter/material.dart';
import '../models/angol_modalz.dart';
import '../models/kepad_konfeg.dart';
import '../utils/heksagon_djeyometre.dart';
import 'heksagon_wedjet.dart';

class DaylWedjet extends StatelessWidget {
  final HeksagonDjeyometre geometry;
  final List<ModuleData> modules;
  final Function(int) onToggleModule;

  const DaylWedjet({
    super.key,
    required this.geometry,
    required this.modules,
    required this.onToggleModule,
  });

  @override
  Widget build(BuildContext context) {
    final innerCoords = geometry.getInnerRingCoordinates();
    return Stack(
      children: innerCoords.asMap().entries.map((entry) {
        final index = entry.key;
        final coord = entry.value;
        final module = modules.firstWhere((m) => m.position == index);
        final position = geometry.axialToPixel(coord.q, coord.r);
        return Positioned(
          left: MediaQuery.of(context).size.width / 2 +
              position.x -
              geometry.hexWidth / 2,
          top: MediaQuery.of(context).size.height / 2 +
              position.y -
              geometry.hexHeight / 2,
          child: HeksagonWedjet(
            label: module.name,
            backgroundColor: module.color,
            textColor: KepadKonfeg.getComplementaryColor(module.color),
            size: geometry.hexWidth,
            isPressed: module.isActive,
            rotationAngle: geometry.rotationAngle,
            onTap: () => onToggleModule(index),
          ),
        );
      }).toList(),
    );
  }
}
