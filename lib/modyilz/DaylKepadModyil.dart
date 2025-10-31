// ignore_for_file: file_names, non_constant_identifier_names

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/EnpitSirves.dart';
import '../models/KepadKonfeg.dart';
import '../utils/HeksagonDjeyometre.dart';
import '../widgets/HeksagonWedjet.dart';

class DaylKepadModyil extends StatefulWidget {
  final HeksagonDjeyometre geometry;
  final void Function(String, {bool isLongPress, String? primaryChar})
      onHexKeyPress;
  final bool isKeypadVisible;

  const DaylKepadModyil({
    super.key,
    required this.geometry,
    required this.onHexKeyPress,
    required this.isKeypadVisible,
  });

  @override
  State<DaylKepadModyil> createState() => _DaylKepadModyilState();
}

class _DaylKepadModyilState extends State<DaylKepadModyil> {
  bool _isCenterHexPressed = false;

  @override
  Widget build(BuildContext context) {
    final inputService = Provider.of<EnpitSirves>(context);

    // Logic for the center module (from kepadsentirmodyil.dart)
    Color baseBackgroundColor =
        inputService.isLetterMode ? Colors.black : Colors.white;
    Color baseTextColor =
        inputService.isLetterMode ? Colors.white : Colors.black;
    Color centerHexBackgroundColor = baseTextColor;
    Color centerHexTextColor = baseBackgroundColor;

    if (_isCenterHexPressed) {
      centerHexTextColor =
          KepadKonfeg.getComplementaryColor(centerHexTextColor);
    }

    final SentirModyilWedjet = Positioned(
      left: MediaQuery.of(context).size.width / 2 - widget.geometry.hexWidth / 2,
      top: MediaQuery.of(context).size.height / 2 -
          widget.geometry.hexHeight / 2,
              child: HeksagonWedjet(        backgroundColor: centerHexBackgroundColor,
        textColor: centerHexTextColor,
        size: widget.geometry.hexWidth,
        rotationAngle: widget.geometry.rotationAngle,
        onTapDown: (_) {
          if (inputService.isLetterMode) {
            inputService.addCharacter(' ');
          } else {
            inputService.addCharacter('.');
          }
        },
        onLongPress: () {
          final wasLetterMode = inputService.isLetterMode;
          inputService.toggleMode();
          // Remove the character added by the preceding onTapDown
          inputService.deleteLeft();
          if (wasLetterMode) {
            inputService.addCharacter('.');
          } else {
            inputService.addCharacter(' ');
          }
        },
        onVerticalDragUpdate: (details) {
          if (details.delta.dy < -5) inputService.setCapitalize();
        },
        onPressedStateChanged: (isPressed) {
          setState(() {
            _isCenterHexPressed = isPressed;
          });
        },
        child: Consumer<EnpitSirves>(
          builder: (context, inputService, child) {
            return OverflowBox(
              maxWidth: double.infinity,
              alignment: Alignment.center,
              child: Align(
                alignment: Alignment.centerLeft,
                child: Text(
                  inputService.getDisplayText(),
                  style: TextStyle(
                    color: centerHexTextColor,
                    fontSize: widget.geometry.hexWidth * 0.33,
                    fontWeight: FontWeight.bold,
                  ),
                  textAlign: TextAlign.start,
                  maxLines: 1,
                ),
              ),
            );
          },
        ),
      ),
    );

    // Logic for the keypad rings (from daylkepadwedjet.dart)
    // Build inner ring
    final innerCoords = widget.geometry.getInnerRingCoordinates();
    final innerLabels = inputService.isLetterMode
        ? KepadKonfeg.innerLetterMode
        : KepadKonfeg.innerNumberMode;
    final innerLongPress = inputService.isLetterMode
        ? KepadKonfeg.innerLetterMode.map((label) {
            // In letter mode, only backspace has a long press action (delete word)
            // The character passed is still '⌫' but the handler in angol_screen knows it's a long press.
            return label == '⌫' ? '⌫' : '';
          }).toList()
        : KepadKonfeg.innerLongPressNumber;

    final EnirRenqWedjets = innerCoords.asMap().entries.map((entry) {
      final index = entry.key;
      final coord = entry.value;
      final tapLabel = innerLabels[index];
      final position = widget.geometry.axialToPixel(coord.q, coord.r);
      final hexColor = KepadKonfeg.innerRingColors[index % 6];

      return Positioned(
        left: MediaQuery.of(context).size.width / 2 +
            position.x -
            widget.geometry.hexWidth / 2,
        top: MediaQuery.of(context).size.height / 2 +
            position.y -
            widget.geometry.hexHeight / 2,
        child: HeksagonWedjet(
          label: tapLabel,
          backgroundColor: hexColor,
          textColor: KepadKonfeg.getComplementaryColor(hexColor),
          size: widget.geometry.hexWidth,
          onTapDown: (_) => widget.onHexKeyPress(tapLabel, isLongPress: false),
          onLongPress: innerLongPress[index].isNotEmpty
              ? () => widget.onHexKeyPress(innerLongPress[index],
                  isLongPress: true, primaryChar: tapLabel)
              : null,
          fontSize: inputService.isLetterMode
              ? widget.geometry.hexWidth * 0.5
              : widget.geometry.hexWidth * 0.67,
        ),
      );
    }).toList();

    // Build outer ring
    final outerCoords = widget.geometry.getOuterRingCoordinates();
    final outerTapLabels = inputService.isLetterMode
        ? KepadKonfeg.outerTap
        : KepadKonfeg.outerTapNumber;
    final outerLongPressLabels = inputService.isLetterMode
        ? KepadKonfeg.outerLongPress
        : KepadKonfeg.outerLongPressNumber;

    final AwdirRenqWedjets = outerCoords.asMap().entries.map((entry) {
      final index = entry.key;
      final coord = entry.value;
      final tapLabel = outerTapLabels[index];
      final position = widget.geometry.axialToPixel(coord.q, coord.r);
      final hexColor = KepadKonfeg.rainbowColors[index];

      return Positioned(
        left: MediaQuery.of(context).size.width / 2 +
            position.x -
            widget.geometry.hexWidth / 2,
        top: MediaQuery.of(context).size.height / 2 +
            position.y -
            widget.geometry.hexHeight / 2,
                  child: HeksagonWedjet(
                    label: tapLabel,
                    secondaryLabel: outerLongPressLabels[index].isNotEmpty              ? outerLongPressLabels[index]
              : null,
          backgroundColor: hexColor,
          textColor: KepadKonfeg.getComplementaryColor(hexColor),
          size: widget.geometry.hexWidth,
          rotationAngle: widget.geometry.rotationAngle,
          onTapDown: (_) => widget.onHexKeyPress(tapLabel, isLongPress: false),
          onLongPress: inputService.isLetterMode
              ? () => widget.onHexKeyPress(outerLongPressLabels[index],
                  isLongPress: true, primaryChar: tapLabel)
              : null,
          fontSize: inputService.isLetterMode
              ? widget.geometry.hexWidth * 0.5
              : widget.geometry.hexWidth * 0.67,
        ),
      );
    }).toList();

    return Stack(
      children: [
        SentirModyilWedjet,
        ...AwdirRenqWedjets,
        ...EnirRenqWedjets,
      ],
    );
  }
}
