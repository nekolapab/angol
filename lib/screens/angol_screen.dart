import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:provider/provider.dart';
import '../models/hexagon_models.dart';
import '../utils/hex_geometry.dart';
import '../services/input_service.dart';
import '../state/angol_state.dart';
import '../widgets/center_angol_widget.dart';
import '../widgets/keypad_ring_widget.dart';
import '../widgets/module_ring_widget.dart';

class AngolScreen extends StatefulWidget {
  const AngolScreen({super.key});

  @override
  State<AngolScreen> createState() => _AngolScreenState();
}

class _AngolScreenState extends State<AngolScreen> {
  late InputService inputService;




  final FocusNode _textFieldFocus = FocusNode();
  final TextEditingController _textController = TextEditingController();

  @override
  void initState() {
    super.initState();
    inputService = Provider.of<InputService>(context, listen: false);
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

  HexGeometry get geometry => HexGeometry(
        center: const HexagonPosition(x: 0, y: 0),
        isLetterMode: inputService.isLetterMode,
      );

  void _onHexTap(String char) {
    HapticFeedback.lightImpact();
    inputService.addCharacter(char);
  }

  void _onHexLongPress(String char) {
    HapticFeedback.mediumImpact();
    if (char == '⌫') {
      inputService.deleteRight();
    } else {
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
            child: Consumer2<InputService, AngolState>(
              builder: (context, inputService, angolState, _) {
                return Stack(
                  children: [
                    Center(
                      child: Stack(
                        children: [
                          if (angolState.isKeypadVisible)
                            KeypadRingWidget(
                              geometry: geometry,
                              onHexTap: _onHexTap,
                              onHexLongPress: _onHexLongPress,
                            )
                          else
                            ModuleRingWidget(
                              geometry: geometry,
                              modules: angolState.modules,
                              onToggleModule: angolState.toggleModule,
                            ),
                          CenterAngolWidget(
                            geometry: geometry,
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