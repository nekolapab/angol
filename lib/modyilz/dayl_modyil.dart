import 'package:flutter/material.dart';
import '../models/angol_modalz.dart';
import '../models/kepad_konfeg.dart';
import '../utils/heksagon_djeyometre.dart';
import '../widgets/heksagon_wedjet.dart';

class DaylModyil extends StatelessWidget {
  final HeksagonDjeyometre geometry;
  final List<ModuleData> modules;
  final Function(int) onToggleModule;

  const DaylModyil({
    super.key,
    required this.geometry,
    required this.modules,
    required this.onToggleModule,
  });

  @override
  Widget build(BuildContext context) {
    final innerCoords = geometry.getInnerRingCoordinates();
    final daylModule = modules.firstWhere((m) => m.id == 'dayl'); // Get the 'dayl' module

    // Filter out the 'dayl' module for the inner ring
    final innerRingModules = modules.where((m) => m.id != 'dayl').toList();

    List<Widget> children = [];

    // Add the center 'dayl' hexagon
    children.add(
      Positioned(
        left: MediaQuery.of(context).size.width / 2 - geometry.hexWidth / 2,
        top: MediaQuery.of(context).size.height / 2 - geometry.hexHeight / 2,
        child: HeksagonWedjet(
          label: daylModule.name,
          backgroundColor: daylModule.color,
          textColor: KepadKonfeg.getComplementaryColor(daylModule.color),
          size: geometry.hexWidth,
          isPressed: daylModule.isActive,
          rotationAngle: geometry.rotationAngle,
          onTap: () => onToggleModule(daylModule.position),
        ),
      ),
    );

    // Add the inner ring hexagons
    for (var module in innerRingModules) {
      final index = module.position; // Use module's position directly
      if (index == 0 || index > innerCoords.length) continue; // Skip center module and invalid indices
      final coord = innerCoords[index - 1]; // Adjust for 0-based index
      final position = geometry.axialToPixel(coord.q, coord.r);

        children.add(
          Positioned(
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
              onTap: () => onToggleModule(module.position),
            ),
          ),
        );
      }

    return Stack(
      children: children,
    );
  }
}
