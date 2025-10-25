import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/input_service.dart';
import '../utils/hex_geometry.dart';
import 'hexagon_widget.dart';

class CenterAngolWidget extends StatelessWidget {
  final HexGeometry geometry;

  const CenterAngolWidget({
    super.key,
    required this.geometry,
  });

  @override
  Widget build(BuildContext context) {
    final inputService = Provider.of<InputService>(context);
    final centerHexBackgroundColor = inputService.isLetterMode ? Colors.black : Colors.white;
    final centerHexTextColor = inputService.isLetterMode ? Colors.white : Colors.black;

    return Positioned(
      left: MediaQuery.of(context).size.width / 2 - geometry.hexWidth / 2,
      top: MediaQuery.of(context).size.height / 2 - geometry.hexHeight / 2,
      child: HexagonWidget(
        label: inputService.isLetterMode ? ' .' : '. ',
        backgroundColor: centerHexBackgroundColor,
        textColor: centerHexTextColor,
        size: geometry.hexWidth,
        rotationAngle: geometry.rotationAngle,
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
          if (wasLetterMode) {
            inputService.addCharacter('.');
          } else {
            inputService.addCharacter(' ');
          }
        },
        onVerticalDragUpdate: (details) {
          if (details.delta.dy < -5) inputService.setCapitalize();
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
                    fontSize: geometry.hexWidth * 0.33,
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
