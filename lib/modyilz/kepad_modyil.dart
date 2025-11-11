import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/enpit_sirves.dart';
import '../utils/heksagon_djeyometre.dart';
import '../widgets/sentir_mod_wedjet.dart';
import '../widgets/enir_renq_wedjet.dart';
import '../widgets/awdir_renq_wedjet.dart';
import '../widgets/awtpit_tekst_wedjet.dart';
import '../models/kepad_konfeg.dart';
import '../widgets/heksagon_wedjet.dart';

class KepadModyil extends StatefulWidget {
  final HeksagonDjeyometre geometry;
  final void Function(String, {bool isLongPress, String? primaryChar})
      onHexKeyPress;
  final bool isKeypadVisible;
  final int displayLength;

  const KepadModyil({
    super.key,
    required this.geometry,
    required this.onHexKeyPress,
    required this.isKeypadVisible,
    required this.displayLength,
  });

  @override
  State<KepadModyil> createState() => _KepadModyilState();
}

class _KepadModyilState extends State<KepadModyil> {
  bool _isCenterHexPressed = false; // New state variable

  void _onCenterHexPressedChanged(bool isPressed) {
    setState(() {
      _isCenterHexPressed = isPressed;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Consumer<EnpitSirves>(
      builder: (context, inputService, child) {
        // Get the correct labels from KepadKonfeg based on the mode
        final innerTapLabels = inputService.isLetterMode
            ? KepadKonfeg.innerLetterMode
            : KepadKonfeg.innerNumberMode;
        final innerLongPressLabels = inputService.isLetterMode
            ? KepadKonfeg.innerLetterMode.map((label) {
                return label == '⌫' ? '⌫' : '';
              }).toList()
            : KepadKonfeg.innerLongPressNumber;

        final outerTapLabels = inputService.isLetterMode
            ? KepadKonfeg.outerTap
            : KepadKonfeg.outerTapNumber;
        final outerLongPressLabels = inputService.isLetterMode
            ? KepadKonfeg.outerLongPress
            : KepadKonfeg.outerLongPressNumber;

        // Logic for the center hex (moved from SentirModWedjet)
        final Color baseBackgroundColor =
            inputService.isLetterMode ? Colors.black : Colors.white;
        final Color baseTextColor =
            inputService.isLetterMode ? Colors.white : Colors.black;
        final Color centerHexBackgroundColor = baseTextColor;
        final Color centerHexTextColor = baseBackgroundColor;

        void centerOnTap() {
          if (inputService.isLetterMode) {
            inputService.addCharacter(' ');
          } else {
            inputService.addCharacter('.');
          }
        }

        void centerOnLongPress() {
          final wasLetterMode = inputService.isLetterMode;
          inputService.toggleMode();
          inputService.deleteLeft();
          if (wasLetterMode) {
            inputService.addCharacter('.');
          } else {
            inputService.addCharacter(' ');
          }
        }

        // Build the inner ring widgets manually now
        final innerCoords = widget.geometry.getInnerRingCoordinates();
        final List<Widget> innerRingWidgets =
            innerCoords.asMap().entries.map((entry) {
          final index = entry.key;
          final tapLabel = innerTapLabels[index];
          final longPressLabel = innerLongPressLabels[index];
          final hexColor = KepadKonfeg.innerRingColors[index % 6];

          return HeksagonWedjet(
            label: tapLabel,
            secondaryLabel: longPressLabel.isNotEmpty ? longPressLabel : null,
            backgroundColor: hexColor,
            textColor: KepadKonfeg.getComplementaryColor(hexColor),
            size: widget.geometry.hexWidth,
            rotationAngle: widget.geometry.rotationAngle,
            onTap: () => widget.onHexKeyPress(tapLabel, isLongPress: false),
            onLongPress: longPressLabel.isNotEmpty
                ? () => widget.onHexKeyPress(longPressLabel,
                    isLongPress: true, primaryChar: tapLabel)
                : null,
            fontSize: inputService.isLetterMode
                ? widget.geometry.hexWidth * 0.5
                : widget.geometry.hexWidth * 0.67,
          );
        }).toList();

        return Stack(
          children: [
            SentirModWedjet(
              geometry: widget.geometry,
              onPressedChanged: _onCenterHexPressedChanged,
              backgroundColor: centerHexBackgroundColor,
              textColor: centerHexTextColor,
              onTap: centerOnTap,
              onLongPress: centerOnLongPress,
            ),
            EnirRenqWedjet(
              geometry: widget.geometry,
              children: innerRingWidgets,
            ),
            AwdirRenqWedjet(
              geometry: widget.geometry,
              onHexKeyPress: widget.onHexKeyPress,
              tapLabels: outerTapLabels,
              longPressLabels: outerLongPressLabels,
            ),
            IgnorePointer(
              child: Center(
                child: AwtpitTekstWedjet(
                  text: inputService.getDisplayText(widget.displayLength),
                  style: TextStyle(
                    color: _isCenterHexPressed
                        ? (inputService.isLetterMode
                            ? Colors.white
                            : Colors.black) // Invert color when pressed
                        : (inputService.isLetterMode
                            ? Colors.black
                            : Colors.white),
                    fontSize: widget.geometry.hexWidth * 0.33,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
            ),
          ],
        );
      },
    );
  }
}
