import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/input_service.dart';
import '../models/keypad_config.dart';
import '../utils/hex_geometry.dart';
import 'hexagon_widget.dart';

class CenterAngolWidget extends StatelessWidget {
  final HexGeometry geometry;
  final bool isPressed;
  final VoidCallback onTapDown;
  final VoidCallback onTapUp;
  final VoidCallback onTapCancel;

  const CenterAngolWidget({
    super.key,
    required this.geometry,
    required this.isPressed,
    required this.onTapDown,
    required this.onTapUp,
    required this.onTapCancel,
  });

  @override
  Widget build(BuildContext context) {
    final inputService = Provider.of<InputService>(context);
    const centerColor = Colors.black;
    final complementaryColor = KeypadConfig.getComplementaryColor(centerColor);

    return Positioned(
      left: MediaQuery.of(context).size.width / 2 - geometry.hexWidth / 2,
      top: MediaQuery.of(context).size.height / 2 - geometry.hexHeight / 2,
      child: GestureDetector(
        onTapDown: (_) => onTapDown(),
        onTapUp: (_) => onTapUp(),
        onTapCancel: onTapCancel,
        onTap: () {
          if (inputService.isLetterMode) {
            inputService.addCharacter(' ');
          } else {
            inputService.addCharacter('.');
          }
        },
        onLongPress: () => inputService.toggleMode(),
        onVerticalDragUpdate: (details) {
          if (details.delta.dy < -5) inputService.setCapitalize();
        },
        child: HexagonWidget(
          label: '',
          backgroundColor: centerColor,
          textColor: complementaryColor,
          size: geometry.hexWidth,
          isPressed: isPressed,
          rotationAngle: geometry.rotationAngle,
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Icon(
                inputService.isLetterMode ? Icons.text_fields : Icons.numbers,
                color: isPressed
                    ? complementaryColor
                    : KeypadConfig.getComplementaryColor(complementaryColor),
                size: 24,
              ),
              const SizedBox(height: 4),
              Text(
                inputService.getDisplayText(),
                style: TextStyle(
                  color: isPressed
                      ? complementaryColor
                      : KeypadConfig.getComplementaryColor(complementaryColor),
                  fontSize: 12,
                  fontWeight: FontWeight.bold,
                ),
                textAlign: TextAlign.center,
                maxLines: 1,
                overflow: TextOverflow.ellipsis,
              ),
            ],
          ),
        ),
      ),
    );
  }
}
