import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:provider/provider.dart';
import '../models/AngolModalz.dart';
import '../utils/HeksagonDjeyometre.dart';
import '../services/EnpitSirves.dart';
import '../state/AngolSteyt.dart';
import '../widgets/AngolSentirWedjet.dart';
import '../modyilz/DaylKepadModyil.dart';
import '../widgets/DaylWedjet.dart';

class DaylModal extends StatefulWidget {
  const DaylModal({super.key});

  @override
  State<DaylModal> createState() => _DaylModalState();
}

class _DaylModalState extends State<DaylModal> {
  late EnpitSirves inputService;

  final FocusNode _textFieldFocus = FocusNode();
  final TextEditingController _textController = TextEditingController();

  @override
  void initState() {
    super.initState();
    inputService = Provider.of<EnpitSirves>(context, listen: false);
    _textFieldFocus.addListener(() {
      inputService.setTextFieldFocus(_textFieldFocus.hasFocus);
    });
    inputService.addListener(_syncTextController);
  }

  void _syncTextController() {
    if (_textController.text != inputService.inputText) {
      _textController.text = inputService.inputText;
      _textController.selection = TextSelection.fromPosition(
        TextPosition(offset: _textController.text.length),
      );
    }
  }

  HeksagonDjeyometre get geometry => HeksagonDjeyometre(
        center: const HexagonPosition(x: 0, y: 0),
        isLetterMode: inputService.isLetterMode,
      );

  void _onHexKeyPress(String char,
      {bool isLongPress = false, String? primaryChar}) {
    if (isLongPress) {
      HapticFeedback.mediumImpact();
      if (char == '⌫') {
        inputService.deleteWord();
      } else {
        // On long press, first remove the character added by the initial onTapDown
        if (primaryChar != null) {
          inputService.deleteCharacters(primaryChar.length);
        } else {
          inputService
              .deleteLeft(); // Fallback for single character primary glyphs
        }
        inputService.addCharacter(char);
      }
    } else {
      HapticFeedback.lightImpact();
      inputService.addCharacter(char);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Stack(
        children: [
          Container(
            width: double.infinity,
            height: double.infinity,
            decoration: const BoxDecoration(
              gradient: RadialGradient(
                colors: [Color(0xFF1A1A2E), Color(0xFF0F0F1E), Colors.black],
                stops: [0.0, 0.5, 1.0],
              ),
            ),
            child: Consumer2<EnpitSirves, AngolSteyt>(
              builder: (context, inputService, angolState, _) {
                return Stack(
                  children: [
                    Center(
                      child: Stack(
                        children: [
                          if (angolState.isKeypadVisible)
                            DaylKepadModyil(
                              geometry: geometry,
                              onHexKeyPress: _onHexKeyPress,
                              isKeypadVisible: angolState.isKeypadVisible,
                            )
                          else
                            DaylWedjet(
                              geometry: geometry,
                              modules: angolState.modules,
                              onToggleModule: angolState.toggleModule,
                            ),
                          AngolSentirWedjet(
                            geometry: geometry,
                            isKeypadVisible: angolState.isKeypadVisible,
                          ),
                        ],
                      ),
                    ),
                  ],
                );
              },
            ),
          ),
        ],
      ),
    );
  }

  @override
  void dispose() {
    _textFieldFocus.dispose();
    _textController.dispose();
    inputService.removeListener(_syncTextController);
    super.dispose();
  }
}
