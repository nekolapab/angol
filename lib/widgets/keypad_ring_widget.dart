import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/input_service.dart';
import '../models/keypad_config.dart';
import '../utils/hex_geometry.dart';
import 'hexagon_widget.dart';

class KeypadRingWidget extends StatelessWidget {
  final HexGeometry geometry;
  final void Function(String, {bool isLongPress}) onHexKeyPress;

  const KeypadRingWidget({
    super.key,
    required this.geometry,
    required this.onHexKeyPress,
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
        ? KeypadConfig.innerLetterMode.map((label) {
            // In letter mode, only backspace has a long press action (delete word)
            // The character passed is still '⌫' but the handler in angol_screen knows it's a long press.
            return label == '⌫' ? '⌫' : '';
          }).toList()
        : KeypadConfig.innerLongPressNumber;

    final innerRingWidgets = innerCoords.asMap().entries.map((entry) {
      final index = entry.key;

      final coord = entry.value;

      final tapLabel = innerLabels[index];

      final position = geometry.axialToPixel(coord.q, coord.r);

      final hexColor = KeypadConfig.innerRingColors[index % 6];

      String? currentSecondaryLabel;

      if (tapLabel == '⌫') {
        currentSecondaryLabel = null; // Hide secondary backspace in all modes
      } else {
        currentSecondaryLabel =
            innerLongPress[index].isNotEmpty ? innerLongPress[index] : null;
      }

      return Positioned(
        left: MediaQuery.of(context).size.width / 2 +
            position.x -
            geometry.hexWidth / 2,
        top: MediaQuery.of(context).size.height / 2 +
            position.y -
            geometry.hexHeight / 2,
        child: HexagonWidget(
          label: tapLabel,
          secondaryLabel: currentSecondaryLabel,
          backgroundColor: hexColor,
          textColor: KeypadConfig.getComplementaryColor(hexColor),
          size: geometry.hexWidth,
          onTapDown: (_) => onHexKeyPress(tapLabel, isLongPress: false),
          onLongPress: innerLongPress[index].isNotEmpty
              ? () => onHexKeyPress(innerLongPress[index], isLongPress: true)
              : null,
          fontSize: inputService.isLetterMode
              ? geometry.hexWidth * 0.5
              : geometry.hexWidth * 0.67,
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
          secondaryLabel: outerLongPressLabels[index].isNotEmpty
              ? outerLongPressLabels[index]
              : null,
          backgroundColor: hexColor,
          textColor: KeypadConfig.getComplementaryColor(hexColor),
          size: geometry.hexWidth,
          rotationAngle: geometry.rotationAngle,
          onTapDown: (_) => onHexKeyPress(tapLabel, isLongPress: false),
          onLongPress: inputService.isLetterMode
              ? () =>
                  onHexKeyPress(outerLongPressLabels[index], isLongPress: true)
              : null,
          fontSize: inputService.isLetterMode
              ? geometry.hexWidth * 0.5
              : geometry.hexWidth * 0.67,
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
