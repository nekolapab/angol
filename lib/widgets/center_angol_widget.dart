import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/input_service.dart';
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
    final centerHexBackgroundColor = inputService.isLetterMode ? Colors.white : Colors.black;
    final centerHexTextColor = inputService.isLetterMode ? Colors.black : Colors.white;

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
          label: inputService.isLetterMode ? ' ' : '.',
          backgroundColor: centerHexBackgroundColor,
          textColor: centerHexTextColor,
          size: geometry.hexWidth,
          isPressed: isPressed,
          rotationAngle: geometry.rotationAngle,
          child: Consumer<InputService>(
            builder: (context, inputService, child) {
              return Text(
                inputService.getDisplayText(),
                style: TextStyle(
                  color: centerHexTextColor,
                  fontSize: geometry.hexWidth * 0.25,
                  fontWeight: FontWeight.bold,
                ),
                textAlign: TextAlign.center,
                maxLines: 1,
                overflow: TextOverflow.ellipsis,
              );
            },
          ),
        ),
      ),
    );
  }
}
