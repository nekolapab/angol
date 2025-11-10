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
  final List<String> tapLabels;
  final List<String> longPressLabels;

  const EnirRenqWedjet({
    super.key,
    required this.geometry,
    required this.onHexKeyPress,
    required this.tapLabels,
    required this.longPressLabels,
  });

  @override
  Widget build(BuildContext context) {
    return Consumer<EnpitSirves>(
      builder: (context, inputService, child) {
        final innerCoords = geometry.getInnerRingCoordinates();

        return Stack(
          children: innerCoords.asMap().entries.map((entry) {
            final index = entry.key;
            final coord = entry.value;
            final tapLabel = tapLabels[index];
            final longPressLabel = longPressLabels[index];
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
                secondaryLabel: longPressLabel.isNotEmpty ? longPressLabel : null,
                backgroundColor: hexColor,
                textColor: KepadKonfeg.getComplementaryColor(hexColor),
                size: geometry.hexWidth,
                rotationAngle: geometry.rotationAngle,
                onTap: () => onHexKeyPress(tapLabel, isLongPress: false),
                onLongPress: longPressLabel.isNotEmpty
                    ? () => onHexKeyPress(longPressLabel,
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
