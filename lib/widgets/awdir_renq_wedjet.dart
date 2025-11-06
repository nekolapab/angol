import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/enpit_sirves.dart';
import '../models/kepad_konfeg.dart';
import '../utils/heksagon_djeyometre.dart';
import 'heksagon_wedjet.dart';

class AwdirRenqWedjet extends StatelessWidget {
  final HeksagonDjeyometre geometry;
  final void Function(String, {bool isLongPress, String? primaryChar})
      onHexKeyPress;

  const AwdirRenqWedjet({
    super.key,
    required this.geometry,
    required this.onHexKeyPress,
  });

  @override
  Widget build(BuildContext context) {
    return Consumer<EnpitSirves>(
      builder: (context, inputService, child) {
        final outerCoords = geometry.getOuterRingCoordinates();
        final outerTapLabels = inputService.isLetterMode
            ? KepadKonfeg.outerTap
            : KepadKonfeg.outerTapNumber;
        final outerLongPressLabels = inputService.isLetterMode
            ? KepadKonfeg.outerLongPress
            : KepadKonfeg.outerLongPressNumber;

        return Stack(
          children: outerCoords.asMap().entries.map((entry) {
            final index = entry.key;
            final coord = entry.value;
            final tapLabel = outerTapLabels[index];
            final position = geometry.axialToPixel(coord.q, coord.r);
            final hexColor = KepadKonfeg.rainbowColors[index];

            return Positioned(
              left: MediaQuery.of(context).size.width / 2 +
                  position.x -
                  geometry.hexWidth / 2,
              top: MediaQuery.of(context).size.height / 2 +
                  position.y -
                  geometry.hexHeight / 2,
              child: HeksagonWedjet(
                label: tapLabel,
                secondaryLabel: outerLongPressLabels[index].isNotEmpty
                    ? outerLongPressLabels[index]
                    : null,
                backgroundColor: hexColor,
                textColor: KepadKonfeg.getComplementaryColor(hexColor),
                size: geometry.hexWidth,
                rotationAngle: geometry.rotationAngle,
                onTap: () => onHexKeyPress(tapLabel, isLongPress: false),
                onLongPress: inputService.isLetterMode
                    ? () => onHexKeyPress(outerLongPressLabels[index],
                        isLongPress: true, primaryChar: tapLabel)
                    : null,
                fontSize: inputService.isLetterMode
                    ? geometry.hexWidth * 0.5
                    : geometry.hexWidth * 0.67,
              ),
            );
          }).toList(),
        );
      },
    );
  }
}
