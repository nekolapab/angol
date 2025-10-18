import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/input_service.dart';
import '../models/keypad_config.dart';
import '../utils/hex_geometry.dart';
import 'hexagon_widget.dart';

class KeypadRingWidget extends StatelessWidget {
  final HexGeometry geometry;
  final String pressedHex;
  final Function(String) onHexTap;
  final Function(String) onHexLongPress;

  const KeypadRingWidget({
    super.key,
    required this.geometry,
    required this.pressedHex,
    required this.onHexTap,
    required this.onHexLongPress,
  });

  @override
  Widget build(BuildContext context) {
    final inputService = Provider.of<InputService>(context);

    // Build inner ring
    final innerCoords = geometry.getInnerRingCoordinates();
    final innerLabels = inputService.isLetterMode
        ? KeypadConfig.innerLetterMode
        : KeypadConfig.innerNumberMode;
    final innerLongPress = inputService.isLetterMode
        ? List.filled(6, '')
        : KeypadConfig.innerLongPressNumber;

    final innerRingWidgets = innerCoords.asMap().entries.map((entry) {
      final index = entry.key;
      final coord = entry.value;
      final tapLabel = innerLabels[index];
      final longPressLabel = innerLongPress[index];
      final position = geometry.axialToPixel(coord.q, coord.r);

      final hexColor = inputService.isLetterMode
          ? KeypadConfig.rainbowColors[index % 6]
          : const Color(0xFFFFFF00);

      return Positioned(
        left: MediaQuery.of(context).size.width / 2 +
            position.x -
            geometry.hexWidth / 2,
        top: MediaQuery.of(context).size.height / 2 +
            position.y -
            geometry.hexHeight / 2,
        child: HexagonWidget(
          label: tapLabel,
          secondaryLabel: longPressLabel.isNotEmpty ? longPressLabel : null,
          backgroundColor: hexColor,
          textColor: KeypadConfig.getComplementaryColor(hexColor),
          size: geometry.hexWidth,
          isPressed: pressedHex == tapLabel || pressedHex == longPressLabel,
          rotationAngle: geometry.rotationAngle,
          onTap: () => onHexTap(tapLabel),
          onLongPress: longPressLabel.isNotEmpty
              ? () => onHexLongPress(longPressLabel)
              : null,
        ),
      );
    }).toList();

    // Build outer ring
    final outerCoords = geometry.getOuterRingCoordinates();
    final outerRingWidgets = outerCoords.asMap().entries.map((entry) {
      final index = entry.key;
      final coord = entry.value;
      final tapLabel = KeypadConfig.outerTap[index];
      final longPressLabel = KeypadConfig.outerLongPress[index];
      final position = geometry.axialToPixel(coord.q, coord.r);
      final hexColor = KeypadConfig.rainbowColors[index];

      return Positioned(
        left: MediaQuery.of(context).size.width / 2 +
            position.x -
            geometry.hexWidth / 2,
        top: MediaQuery.of(context).size.height / 2 +
            position.y -
            geometry.hexHeight / 2,
        child: HexagonWidget(
          label: tapLabel,
          secondaryLabel: longPressLabel,
          backgroundColor: hexColor,
          textColor: KeypadConfig.getComplementaryColor(hexColor),
          size: geometry.hexWidth,
          isPressed: pressedHex == tapLabel || pressedHex == longPressLabel,
          rotationAngle: geometry.rotationAngle,
          onTap: () => onHexTap(tapLabel),
          onLongPress: () => onHexLongPress(longPressLabel),
        ),
      );
    }).toList();

    return Stack(
      children: [
        ...outerRingWidgets,
        ...innerRingWidgets,
      ],
    );
  }
}
