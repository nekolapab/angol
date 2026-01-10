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
  final List<String> tapLabels;
  final List<String> longPressLabels;
  final List<GlobalKey>? keys;
  final Function(bool)? onHover;
  final double stackWidth;
  final double stackHeight;
  final int? pressedIndex;

  const AwdirRenqWedjet({
    super.key,
    required this.geometry,
    required this.onHexKeyPress,
    required this.tapLabels,
    required this.longPressLabels,
    this.keys,
    this.onHover,
    required this.stackWidth,
    required this.stackHeight,
    this.pressedIndex,
  });

  @override
  Widget build(BuildContext context) {
    return Consumer<EnpitSirves>(
      builder: (context, inputService, child) {
        final outerCoords = geometry.getOuterRingCoordinates();

        return Stack(
          children: outerCoords.asMap().entries.map((entry) {
            final index = entry.key;
            final coord = entry.value;
            final tapLabel = tapLabels[index];
            final longPressLabel = longPressLabels[index];
            final position = geometry.axialToPixel(coord.q, coord.r);
            final hexColor = KepadKonfeg.rainbowColors[index];

            return Positioned(
              left: stackWidth / 2 +
                  position.x -
                  geometry.hexWidth / 2,
              top: stackHeight / 2 +
                  position.y -
                  geometry.hexHeight / 2,
              child: HeksagonWedjet(
                key: (keys != null && keys!.length > index) ? keys![index] : null,
                label: tapLabel,
                secondaryLabel:
                    inputService.isLetterMode && longPressLabel.isNotEmpty
                        ? longPressLabel
                        : null,
                backgroundColor: hexColor,
                textColor: KepadKonfeg.getComplementaryColor(hexColor),
                size: geometry.hexWidth,
                rotationAngle: geometry.rotationAngle,
                isPressed: pressedIndex == index,
                onTap: () => onHexKeyPress(tapLabel, isLongPress: false),
                onLongPress:
                    inputService.isLetterMode && longPressLabel.isNotEmpty
                        ? () => onHexKeyPress(longPressLabel,
                            isLongPress: true, primaryChar: tapLabel)
                        : null,
                onHover: onHover, // Pass the onHover callback
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
