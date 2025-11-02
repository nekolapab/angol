import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/enpit_sirves.dart';
import '../models/kepad_konfeg.dart';
import '../utils/heksagon_djeyometre.dart';
import 'heksagon_wedjet.dart';

class SentirHeksagonWedjet extends StatefulWidget {
  final HeksagonDjeyometre geometry;

  const SentirHeksagonWedjet({
    super.key,
    required this.geometry,
  });

  @override
  State<SentirHeksagonWedjet> createState() => _SentirHeksagonWedjetState();
}

class _SentirHeksagonWedjetState extends State<SentirHeksagonWedjet> {
  bool _isCenterHexPressed = false;

  @override
  Widget build(BuildContext context) {
    final inputService = Provider.of<EnpitSirves>(context);

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

    return Positioned(
      left: MediaQuery.of(context).size.width / 2 - widget.geometry.hexWidth / 2,
      top: MediaQuery.of(context).size.height / 2 -
          widget.geometry.hexHeight / 2,
      child: HeksagonWedjet(
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
  }
}
