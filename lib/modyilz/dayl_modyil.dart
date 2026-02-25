import 'package:flutter/material.dart';
import '../modalz/angol_modalz.dart';
import '../modalz/kepad_konfeg.dart';
import '../yutelez/heksagon_djeyometre.dart';
import '../wedjets/heksagon_wedjet.dart';
import '../wedjets/sentir_mod_wedjet.dart';
import '../wedjets/enir_renq_wedjet.dart';

class DaylModyil extends StatelessWidget {
  final HeksagonDjeyometre geometry;
  final List<ModyilDeyda> modyilz;
  final Function(int) onToggleModule;

  const DaylModyil({
    super.key,
    required this.geometry,
    required this.modyilz,
    required this.onToggleModule,
  });

  @override
  Widget build(BuildContext context) {
    final daylModule =
        modyilz.firstWhere((m) => m.id == 'dayl', orElse: () => modyilz.first);

    return LayoutBuilder(
      builder: (context, constraints) {
        final stackWidth = constraints.maxWidth;
        final stackHeight = constraints.maxHeight;
        return Stack(
          alignment: Alignment.center,
          children: [
            // Center hexagon
            SentirModWedjet(
              label: daylModule.neym,
              geometry: geometry,
              backgroundColor: daylModule.kulor,
              textColor: KepadKonfeg.getComplementaryColor(daylModule.kulor),
              onTap: () => onToggleModule(daylModule.pozecon),
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
    final innerRingModules = modyilz.where((m) => m.id != 'dayl').toList();
    return innerRingModules.map((module) {
      return HeksagonWedjet(
        label: module.neym,
        backgroundColor: module.kulor,
        textColor: KepadKonfeg.getComplementaryColor(module.kulor),
        size: geometry.heksWidlx,
        isPressed: module.ezAktiv,
        rotationAngle: geometry.roteyconAngol,
        onTap: () => onToggleModule(module.pozecon),
      );
    }).toList();
  }
}
