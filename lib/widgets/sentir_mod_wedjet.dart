import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/enpit_sirves.dart';

import '../utils/heksagon_djeyometre.dart';
import 'heksagon_wedjet.dart';

class SentirModWedjet extends StatefulWidget {
  final HeksagonDjeyometre geometry;

  const SentirModWedjet({
    super.key,
    required this.geometry,
  });

  @override
  State<SentirModWedjet> createState() => _SentirModWedjetState();
}

class _SentirModWedjetState extends State<SentirModWedjet> {
  @override
  Widget build(BuildContext context) {
    final inputService = Provider.of<EnpitSirves>(context);

    Color baseBackgroundColor =
        inputService.isLetterMode ? Colors.black : Colors.white;
    Color baseTextColor =
        inputService.isLetterMode ? Colors.white : Colors.black;
    Color centerHexBackgroundColor = baseTextColor;
    Color centerHexTextColor = baseBackgroundColor;

    return Positioned(
      left:
          MediaQuery.of(context).size.width / 2 - widget.geometry.hexWidth / 2,
      top: MediaQuery.of(context).size.height / 2 -
          widget.geometry.hexHeight / 2,
      child: HeksagonWedjet(
        label: inputService.isLetterMode ? 'ABC' : '123',
        backgroundColor: centerHexBackgroundColor,
        textColor: centerHexTextColor,
        size: widget.geometry.hexWidth,
        rotationAngle: widget.geometry.rotationAngle,
        onTap: () {
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
        child: Container(),
      ),
    );
  }
}
