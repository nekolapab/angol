import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/enpit_sirves.dart';
import '../utils/heksagon_djeyometre.dart';
import '../widgets/sentir_mod_wedjet.dart';
import '../widgets/enir_renq_wedjet.dart';
import '../widgets/awdir_renq_wedjet.dart';
import '../widgets/awtpit_tekst_wedjet.dart';

class KepadModyil extends StatefulWidget {
  final HeksagonDjeyometre geometry;
  final void Function(String, {bool isLongPress, String? primaryChar})
      onHexKeyPress;
  final bool isKeypadVisible;

  const KepadModyil({
    super.key,
    required this.geometry,
    required this.onHexKeyPress,
    required this.isKeypadVisible,
  });

  @override
  State<KepadModyil> createState() => _KepadModyilState();
}

class _KepadModyilState extends State<KepadModyil> {

  @override
  Widget build(BuildContext context) {

    return Consumer<EnpitSirves>(
      builder: (context, inputService, child) {
        return Stack(
          children: [
            SentirModWedjet(
              geometry: widget.geometry,
              onTapDown: (_) {
                if (inputService.isLetterMode) {
                  inputService.addCharacter(' ');
                } else {
                  inputService.addCharacter('.');
                }
              },
            ),
            EnirRenqWedjet(
              geometry: widget.geometry,
              onHexKeyPress: widget.onHexKeyPress,
            ),
            AwdirRenqWedjet(
              geometry: widget.geometry,
              onHexKeyPress: widget.onHexKeyPress,
            ),
            IgnorePointer(
              child: Center(
                child: AwtpitTekstWedjet(
                  text: inputService.getDisplayText(),
                  style: TextStyle(
                    color: inputService.isLetterMode ? Colors.black : Colors.white,
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

