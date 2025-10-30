import 'package:flutter/material.dart';
import '../models/angolmodalz.dart';
import '../models/kepadkonfeg.dart';
import '../utils/heksagondjeyometre.dart';
import 'heksagonwedjet.dart';

class Daylwedjet extends StatelessWidget {
  final Heksagondjeyometre geometry;
  final List<ModuleData> modules;
  final Function(int) onToggleModule;

  const Daylwedjet({
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
          child: Heksagonwedjet(
            label: module.name,
            backgroundColor: module.color,
            textColor: Kepadkonfeg.getComplementaryColor(module.color),
            size: geometry.hexWidth,
            isPressed: module.isActive,
            rotationAngle: geometry.rotationAngle,
            onTapDown: (_) => onToggleModule(index),
          ),
        );
      }).toList(),
    );
  }
}
