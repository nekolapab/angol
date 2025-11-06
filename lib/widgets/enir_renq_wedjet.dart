import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/enpit_sirves.dart';
import '../models/kepad_konfeg.dart';
import '../utils/heksagon_djeyometre.dart';
import 'heksagon_wedjet.dart';

class EnirRenqWedjet extends StatelessWidget {
  final HeksagonDjeyometre geometry;
  final void Function(String, {bool isLongPress, String? primaryChar})
      onHexKeyPress;

  const EnirRenqWedjet({
    super.key,
    required this.geometry,
    required this.onHexKeyPress,
  });

  @override
  Widget build(BuildContext context) {
    return Consumer<EnpitSirves>(
      builder: (context, inputService, child) {
        final innerCoords = geometry.getInnerRingCoordinates();
        final innerLabels = inputService.isLetterMode
            ? KepadKonfeg.innerLetterMode
            : KepadKonfeg.innerNumberMode;
        final innerLongPress = inputService.isLetterMode
            ? KepadKonfeg.innerLetterMode.map((label) {
                return label == '⌫' ? '⌫' : '';
              }).toList()
            : KepadKonfeg.innerLongPressNumber;

        return Stack(
          children: innerCoords.asMap().entries.map((entry) {
            final index = entry.key;
            final coord = entry.value;
            final tapLabel = innerLabels[index];
            final position = geometry.axialToPixel(coord.q, coord.r);
            final hexColor = KepadKonfeg.innerRingColors[index % 6];

            return Positioned(
              left: MediaQuery.of(context).size.width / 2 +
                  position.x -
                  geometry.hexWidth / 2,
              top: MediaQuery.of(context).size.height / 2 +
                  position.y -
                  geometry.hexHeight / 2,
              child: HeksagonWedjet(
                label: tapLabel,
                secondaryLabel: innerLongPress[index].isNotEmpty
                    ? innerLongPress[index]
                    : null,
                backgroundColor: hexColor,
                textColor: KepadKonfeg.getComplementaryColor(hexColor),
                size: geometry.hexWidth,
                rotationAngle: geometry.rotationAngle,
                onTap: () => onHexKeyPress(tapLabel, isLongPress: false),
                onLongPress: innerLongPress[index].isNotEmpty
                    ? () => onHexKeyPress(innerLongPress[index],
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
