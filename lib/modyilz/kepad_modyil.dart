import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/enpit_sirves.dart';
import '../utils/heksagon_djeyometre.dart';
import '../widgets/sentir_mod_wedjet.dart';
import '../widgets/enir_renq_wedjet.dart';
import '../widgets/awdir_renq_wedjet.dart';
import '../widgets/awtpit_tekst_wedjet.dart';
import '../models/kepad_konfeg.dart';

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

        return Stack(
          children: [
            SentirModWedjet(
              geometry: widget.geometry,
              onPressedChanged: _onCenterHexPressedChanged, // Pass the callback
            ),
            EnirRenqWedjet(
              geometry: widget.geometry,
              onHexKeyPress: widget.onHexKeyPress,
              tapLabels: innerTapLabels,
              longPressLabels: innerLongPressLabels,
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
                        ? (inputService.isLetterMode ? Colors.white : Colors.black) // Invert color when pressed
                        : (inputService.isLetterMode ? Colors.black : Colors.white),
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
