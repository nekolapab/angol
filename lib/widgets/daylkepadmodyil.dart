import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/enpitsirves.dart';
import '../models/kepadkonfeg.dart';
import '../utils/heksagondjeyometre.dart';
import 'heksagonwedjet.dart';

class DaylKepadModyil extends StatefulWidget {
  final Heksagondjeyometre geometry;
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
    final inputService = Provider.of<Enpitsirves>(context);

    // Logic for the center module (from kepadsentirmodyil.dart)
    Color baseBackgroundColor =
        inputService.isLetterMode ? Colors.black : Colors.white;
    Color baseTextColor =
        inputService.isLetterMode ? Colors.white : Colors.black;
    Color centerHexBackgroundColor = baseTextColor;
    Color centerHexTextColor = baseBackgroundColor;

    if (_isCenterHexPressed) {
      centerHexTextColor =
          Kepadkonfeg.getComplementaryColor(centerHexTextColor);
    }

    final centerModuleWidget = Positioned(
      left: MediaQuery.of(context).size.width / 2 - widget.geometry.hexWidth / 2,
      top: MediaQuery.of(context).size.height / 2 -
          widget.geometry.hexHeight / 2,
      child: Heksagonwedjet(
        label: widget.isKeypadVisible
            ? (inputService.isLetterMode ? ' .' : '. ')
            : '', // Conditionally hide label
        backgroundColor: centerHexBackgroundColor,
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
        child: Consumer<Enpitsirves>(
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
        ? Kepadkonfeg.innerLetterMode
        : Kepadkonfeg.innerNumberMode;
    final innerLongPress = inputService.isLetterMode
        ? Kepadkonfeg.innerLetterMode.map((label) {
            // In letter mode, only backspace has a long press action (delete word)
            // The character passed is still '⌫' but the handler in angol_screen knows it's a long press.
            return label == '⌫' ? '⌫' : '';
          }).toList()
        : Kepadkonfeg.innerLongPressNumber;

    final innerRingWidgets = innerCoords.asMap().entries.map((entry) {
      final index = entry.key;
      final coord = entry.value;
      final tapLabel = innerLabels[index];
      final position = widget.geometry.axialToPixel(coord.q, coord.r);
      final hexColor = Kepadkonfeg.innerRingColors[index % 6];
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
            widget.geometry.hexWidth / 2,
        top: MediaQuery.of(context).size.height / 2 +
            position.y -
            widget.geometry.hexHeight / 2,
        child: Heksagonwedjet(
          label: tapLabel,
          secondaryLabel: currentSecondaryLabel,
          backgroundColor: hexColor,
          textColor: Kepadkonfeg.getComplementaryColor(hexColor),
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
        ? Kepadkonfeg.outerTap
        : Kepadkonfeg.outerTapNumber;
    final outerLongPressLabels = inputService.isLetterMode
        ? Kepadkonfeg.outerLongPress
        : Kepadkonfeg.outerLongPressNumber;

    final outerRingWidgets = outerCoords.asMap().entries.map((entry) {
      final index = entry.key;
      final coord = entry.value;
      final tapLabel = outerTapLabels[index];
      final position = widget.geometry.axialToPixel(coord.q, coord.r);
      final hexColor = Kepadkonfeg.rainbowColors[index];

      return Positioned(
        left: MediaQuery.of(context).size.width / 2 +
            position.x -
            widget.geometry.hexWidth / 2,
        top: MediaQuery.of(context).size.height / 2 +
            position.y -
            widget.geometry.hexHeight / 2,
        child: Heksagonwedjet(
          label: tapLabel,
          secondaryLabel: outerLongPressLabels[index].isNotEmpty
              ? outerLongPressLabels[index]
              : null,
          backgroundColor: hexColor,
          textColor: Kepadkonfeg.getComplementaryColor(hexColor),
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
        centerModuleWidget,
        ...outerRingWidgets,
        ...innerRingWidgets,
      ],
    );
  }
}
