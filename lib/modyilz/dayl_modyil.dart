import 'package:flutter/material.dart';
import '../models/angol_modalz.dart';
import '../models/kepad_konfeg.dart';
import '../utils/heksagon_djeyometre.dart';
import '../widgets/heksagon_wedjet.dart';
import '../widgets/sentir_mod_wedjet.dart';
import '../widgets/enir_renq_wedjet.dart';

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
    final daylModule =
        modules.firstWhere((m) => m.id == 'dayl', orElse: () => modules.first);



    return LayoutBuilder(
      builder: (context, constraints) {
        final stackWidth = constraints.maxWidth;
        final stackHeight = constraints.maxHeight;
        return Stack(
          alignment: Alignment.center,
          children: [
            // Center hexagon
            SentirModWedjet(
              label: daylModule.name,
              geometry: geometry,
              backgroundColor: daylModule.color,
              textColor: KepadKonfeg.getComplementaryColor(daylModule.color),
              onTap: () => onToggleModule(daylModule.position),
            ),
            // Inner ring layout
            EnirRenqWedjet(
              geometry: geometry,
              stackWidth: stackWidth,
              stackHeight: stackHeight,
              children: _innerRingWidgets,
            ),
          ],
        );
      },
    );
  }

  List<Widget> get innerRingWidgets => _innerRingWidgets;

  List<Widget> get _innerRingWidgets {
    final innerRingModules = modules.where((m) => m.id != 'dayl').toList();
    return innerRingModules.map((module) {
      return HeksagonWedjet(
        label: module.name,
        backgroundColor: module.color,
        textColor: KepadKonfeg.getComplementaryColor(module.color),
        size: geometry.hexWidth,
        isPressed: module.isActive,
        rotationAngle: geometry.rotationAngle,
        onTap: () => onToggleModule(module.position),
      );
    }).toList();
  }
}
