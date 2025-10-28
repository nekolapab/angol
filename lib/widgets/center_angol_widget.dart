import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/input_service.dart';
import '../utils/hex_geometry.dart';
import 'hexagon_widget.dart';
import '../models/keypad_config.dart'; // Import KeypadConfig

class CenterAngolWidget extends StatefulWidget {
  final HexGeometry geometry;

  const CenterAngolWidget({
    super.key,
    required this.geometry,
  });

  @override
  State<CenterAngolWidget> createState() => _CenterAngolWidgetState();
}

class _CenterAngolWidgetState extends State<CenterAngolWidget> {
  bool _isCenterHexPressed = false;

  @override
  Widget build(BuildContext context) {
    final inputService = Provider.of<InputService>(context);
    final centerHexBackgroundColor =
        inputService.isLetterMode ? Colors.black : Colors.white;
    Color centerHexTextColor =
        inputService.isLetterMode ? Colors.white : Colors.black;

    if (_isCenterHexPressed) {
      centerHexTextColor =
          KeypadConfig.getComplementaryColor(centerHexTextColor);
    }

    return Positioned(
      left:
          MediaQuery.of(context).size.width / 2 - widget.geometry.hexWidth / 2,
      top: MediaQuery.of(context).size.height / 2 -
          widget.geometry.hexHeight / 2,
      child: HexagonWidget(
        label: inputService.isLetterMode ? ' .' : '. ',
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
        child: Consumer<InputService>(
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
