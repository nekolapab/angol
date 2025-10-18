import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:provider/provider.dart';
import '../models/hexagon_models.dart';
import '../utils/hex_geometry.dart';
import '../services/input_service.dart';
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

  List<ModuleData> modules = [
    const ModuleData(
        id: 'dayl', name: 'dayl', color: Color(0xFFFF0000), position: 0),
    const ModuleData(
        id: 'keypad', name: 'kepad', color: Color(0xFFFFFF00), position: 1),
    const ModuleData(
        id: 'module3', name: '', color: Color(0xFF00FF00), position: 2),
    const ModuleData(
        id: 'module4', name: '', color: Color(0xFF00FFFF), position: 3),
    const ModuleData(
        id: 'module5', name: '', color: Color(0xFF0000FF), position: 4),
    const ModuleData(
        id: 'module6', name: '', color: Color(0xFFFF00FF), position: 5),
  ];

  String _pressedHex = '';
  bool _angolPressed = false;

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
    _syncTextController();
    setState(() => _pressedHex = char);
    Future.delayed(const Duration(milliseconds: 200), () {
      if (mounted) setState(() => _pressedHex = '');
    });
  }

  void _onHexLongPress(String char) {
    HapticFeedback.mediumImpact();
    if (char == '⌫') {
      inputService.deleteRight();
    } else {
      inputService.addCharacter(char);
    }
    _syncTextController();
    setState(() => _pressedHex = char);
    Future.delayed(const Duration(milliseconds: 200), () {
      if (mounted) setState(() => _pressedHex = '');
    });
  }

  void _toggleModule(int index) {
    setState(() {
      final tappedModule = modules.firstWhere((m) => m.position == index);
      final bool wasActive = tappedModule.isActive;

      modules = modules.map((m) {
        if (m.position == index) {
          return m.copyWith(isActive: !wasActive);
        } else {
          return m.copyWith(isActive: false);
        }
      }).toList();
    });
  }

  bool get _isKeypadVisible {
    final keypadModule = modules.firstWhere((m) => m.id == 'keypad');
    return inputService.isTextFieldFocused || keypadModule.isActive;
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
            child: Consumer<InputService>(
              builder: (context, inputService, _) {
                return Stack(
                  children: [
                    Center(
                      child: Stack(
                        children: [
                          if (_isKeypadVisible)
                            KeypadRingWidget(
                              geometry: geometry,
                              pressedHex: _pressedHex,
                              onHexTap: _onHexTap,
                              onHexLongPress: _onHexLongPress,
                            )
                          else
                            ModuleRingWidget(
                              geometry: geometry,
                              modules: modules,
                              onToggleModule: _toggleModule,
                            ),
                          CenterAngolWidget(
                            geometry: geometry,
                            isPressed: _angolPressed,
                            onTapDown: () => setState(() => _angolPressed = true),
                            onTapUp: () => setState(() => _angolPressed = false),
                            onTapCancel: () => setState(() => _angolPressed = false),
                          ),
                        ],
                      ),
                    ),
                    Positioned(
                      top: 100,
                      left: 20,
                      right: 20,
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.end,
                        children: [
                          const Text(
                            'ANGOL',
                            style: TextStyle(
                              color: Color(0xFF4A90E2),
                              fontSize: 24,
                              fontWeight: FontWeight.bold,
                              letterSpacing: 2,
                            ),
                          ),
                          Text(
                            inputService.isLetterMode
                                ? 'Letter Mode'
                                : 'Number Mode',
                            style: const TextStyle(
                              color: Colors.white70,
                              fontSize: 12,
                            ),
                          ),
                          const SizedBox(height: 20),
                          Container(
                            width: double.infinity,
                            padding: const EdgeInsets.symmetric(
                                horizontal: 20, vertical: 10),
                            decoration: BoxDecoration(
                              color: Colors.black.withOpacity(0.7),
                              borderRadius: BorderRadius.circular(8),
                              border: Border.all(
                                color: _textFieldFocus.hasFocus
                                    ? const Color(0xFF60A5FA)
                                    : const Color(0xFF4A90E2),
                                width: 2,
                              ),
                            ),
                            child: TextField(
                              controller: _textController,
                              focusNode: _textFieldFocus,
                              style: const TextStyle(
                                  color: Colors.white, fontSize: 16),
                              decoration: const InputDecoration(
                                hintText: 'Tap to activate keypad',
                                hintStyle: TextStyle(color: Colors.white38),
                                border: InputBorder.none,
                                isDense: true,
                              ),
                            ),
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