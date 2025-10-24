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
  final List<Color> moduleColors;

  const KeypadRingWidget({
    super.key,
    required this.geometry,
    required this.pressedHex,
    required this.onHexTap,
    required this.onHexLongPress,
    required this.moduleColors,
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

      final hexColor = moduleColors[index];

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
          fontSize: geometry.hexWidth * 0.5,
        ),
      );
    }).toList();

    // Build outer ring
    final outerCoords = geometry.getOuterRingCoordinates();
    final outerTapLabels = inputService.isLetterMode
        ? KeypadConfig.outerTap
        : KeypadConfig.outerTapNumber;
    final outerLongPressLabels = inputService.isLetterMode
        ? KeypadConfig.outerLongPress
        : KeypadConfig.outerLongPressNumber;

    final outerRingWidgets = outerCoords.asMap().entries.map((entry) {
      final index = entry.key;
      final coord = entry.value;
      final tapLabel = outerTapLabels[index];
      final longPressLabel = outerLongPressLabels[index];
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
          secondaryLabel: inputService.isLetterMode ? longPressLabel : null,
          backgroundColor: hexColor,
          textColor: KeypadConfig.getComplementaryColor(hexColor),
          size: geometry.hexWidth,
          isPressed: pressedHex == tapLabel || pressedHex == longPressLabel,
          rotationAngle: geometry.rotationAngle,
          onTap: () => onHexTap(tapLabel),
          onLongPress: inputService.isLetterMode
              ? () => onHexLongPress(longPressLabel)
              : null,
          fontSize: geometry.hexWidth * 0.5,
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
