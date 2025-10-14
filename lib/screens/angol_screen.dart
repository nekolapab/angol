
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import '../models/hexagon_models.dart';
import '../models/keypad_config.dart';
import '../utils/hex_geometry.dart';
import '../services/input_service.dart';
import '../services/firebase_service.dart';
import '../widgets/hexagon_widget.dart';

class AngolScreen extends StatefulWidget {
  const AngolScreen({super.key});

  @override
  State<AngolScreen> createState() => _AngolScreenState();
}

class _AngolScreenState extends State<AngolScreen> {
  final InputService inputService = InputService();
  late final FirebaseService firebaseService;
  final FocusNode _textFieldFocus = FocusNode();
  final TextEditingController _textController = TextEditingController();
  
  List<ModuleData> modules = [
    const ModuleData(
      id: 'dayl',
      name: 'dayl',
      color: Color(0xFFFF0000),
      position: 0,
    ),
    const ModuleData(
      id: 'keypad',
      name: 'keypad',
      color: Color(0xFFFFFF00),
      position: 1,
    ),
    const ModuleData(
      id: 'module3',
      name: '',
      color: Color(0xFF00FF00),
      position: 2,
    ),
    const ModuleData(
      id: 'module4',
      name: '',
      color: Color(0xFF00FFFF),
      position: 3,
    ),
    const ModuleData(
      id: 'module5',
      name: '',
      color: Color(0xFF0000FF),
      position: 4,
    ),
    const ModuleData(
      id: 'module6',
      name: '',
      color: Color(0xFFFF00FF),
      position: 5,
    ),
  ];
  
  String _pressedHex = '';
  bool _angolPressed = false;

  @override
  void initState() {
    super.initState();
    firebaseService = FirebaseService();
    
    // Sync initial text
    _textController.text = inputService.inputText;
    
    _textFieldFocus.addListener(() {
      inputService.setTextFieldFocus(_textFieldFocus.hasFocus);
    });
    
    // Sync controller with input service
    inputService.addListener(_syncTextController);
  }
  
  void _syncTextController() {
    if (_textController.text != inputService.inputText) {
      _textController.text = inputService.inputText;
      // Move cursor to end
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
    setState(() => _pressedHex = char);
    HapticFeedback.lightImpact();
    inputService.addCharacter(char);
    
    Future.delayed(const Duration(milliseconds: 200), () {
      if (mounted) setState(() => _pressedHex = '');
    });
  }

  void _onHexLongPress(String char) {
    setState(() => _pressedHex = char);
    HapticFeedback.mediumImpact();
    
    if (char == '⌫') {
      inputService.deleteRight();
    } else {
      inputService.addCharacter(char);
    }
    
    Future.delayed(const Duration(milliseconds: 200), () {
      if (mounted) setState(() => _pressedHex = '');
    });
  }

  void _toggleModule(int index) {
    setState(() {
      modules = modules.map((m) {
        if (m.position == index) {
          return m.copyWith(isActive: !m.isActive);
        }
        return m;
      }).toList();
    });
  }

  Widget _buildCenterAngol() {
    const centerColor = Colors.black;
    final complementaryColor = KeypadConfig.getComplementaryColor(centerColor);
    
    return Positioned(
      left: MediaQuery.of(context).size.width / 2 - geometry.hexWidth / 2,
      top: MediaQuery.of(context).size.height / 2 - geometry.hexHeight / 2,
      child: GestureDetector(
        onTapDown: (_) => setState(() => _angolPressed = true),
        onTapUp: (_) => setState(() => _angolPressed = false),
        onTapCancel: () => setState(() => _angolPressed = false),
        onTap: () {
          // Tap: space in letter mode, period in number mode
          if (inputService.isLetterMode) {
            inputService.addCharacter(' ');
          } else {
            inputService.addCharacter('.');
          }
        },
        onLongPress: () {
          // Long press: toggle mode
          inputService.toggleMode();
        },
        onVerticalDragUpdate: (details) {
          if (details.delta.dy < -5) {
            inputService.setCapitalize();
          }
        },
        child: HexagonWidget(
          label: '',
          backgroundColor: centerColor,
          textColor: complementaryColor,
          size: geometry.hexWidth,
          isPressed: _angolPressed,
          rotationAngle: geometry.rotationAngle,
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Icon(
                inputService.isLetterMode ? Icons.text_fields : Icons.numbers,
                color: _angolPressed ? complementaryColor : KeypadConfig.getComplementaryColor(complementaryColor),
                size: 24,
              ),
              const SizedBox(height: 4),
              Text(
                inputService.getDisplayText(),
                style: TextStyle(
                  color: _angolPressed ? complementaryColor : KeypadConfig.getComplementaryColor(complementaryColor),
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

  Widget _buildModuleRing() {
    final innerCoords = geometry.getInnerRingCoordinates();

    return Stack(
      children: innerCoords.asMap().entries.map((entry) {
        final index = entry.key;
        final coord = entry.value;
        final module = modules.firstWhere((m) => m.position == index);

        // Hide 'dayl' module when text field is focused
        if (inputService.isTextFieldFocused && module.id == 'dayl') {
          return const SizedBox.shrink();
        }

        final position = geometry.axialToPixel(coord.q, coord.r);

        return Positioned(
          left: MediaQuery.of(context).size.width / 2 + position.x - geometry.hexWidth / 2,
          top: MediaQuery.of(context).size.height / 2 + position.y - geometry.hexHeight / 2,
          child: HexagonWidget(
            label: module.name,
            backgroundColor: module.color,
            textColor: KeypadConfig.getComplementaryColor(module.color),
            size: geometry.hexWidth,
            isPressed: false,
            rotationAngle: geometry.rotationAngle,
            onTap: () => _toggleModule(index),
          ),
        );
      }).toList(),
    );
  }

  Widget _buildInnerRing() {
    // Show inner ring when text field is focused
    if (!inputService.isTextFieldFocused) {
      return const SizedBox.shrink();
    }

    final innerCoords = geometry.getInnerRingCoordinates();
    final innerLabels = inputService.isLetterMode
        ? KeypadConfig.innerLetterMode
        : KeypadConfig.innerNumberMode;
    final innerLongPress = inputService.isLetterMode
        ? List.filled(6, '')
        : KeypadConfig.innerLongPressNumber;

    return Stack(
      children: innerCoords.asMap().entries.map((entry) {
        final index = entry.key;
        final coord = entry.value;
        final tapLabel = innerLabels[index];
        final longPressLabel = innerLongPress[index];
        final position = geometry.axialToPixel(coord.q, coord.r);

        // Rainbow colors for letter mode, yellow for number mode
        final hexColor = inputService.isLetterMode
            ? KeypadConfig.rainbowColors[index % 6]
            : const Color(0xFFFFFF00);

        return Positioned(
          left: MediaQuery.of(context).size.width / 2 + position.x - geometry.hexWidth / 2,
          top: MediaQuery.of(context).size.height / 2 + position.y - geometry.hexHeight / 2,
          child: HexagonWidget(
            label: tapLabel,
            secondaryLabel: longPressLabel.isNotEmpty ? longPressLabel : null,
            backgroundColor: hexColor,
            textColor: KeypadConfig.getComplementaryColor(hexColor),
            size: geometry.hexWidth,
            isPressed: _pressedHex == tapLabel || _pressedHex == longPressLabel,
            rotationAngle: geometry.rotationAngle,
            onTap: () => _onHexTap(tapLabel),
            onLongPress: longPressLabel.isNotEmpty
                ? () => _onHexLongPress(longPressLabel)
                : null,
          ),
        );
      }).toList(),
    );
  }

  Widget _buildOuterRing() {
    // Always show outer ring when text field focused
    if (!inputService.isTextFieldFocused) {
      return const SizedBox.shrink();
    }

    final outerCoords = geometry.getOuterRingCoordinates();

    return Stack(
      children: outerCoords.asMap().entries.map((entry) {
        final index = entry.key;
        final coord = entry.value;
        final tapLabel = KeypadConfig.outerTap[index];
        final longPressLabel = KeypadConfig.outerLongPress[index];
        final position = geometry.axialToPixel(coord.q, coord.r);
        final hexColor = KeypadConfig.rainbowColors[index];

        return Positioned(
          left: MediaQuery.of(context).size.width / 2 + position.x - geometry.hexWidth / 2,
          top: MediaQuery.of(context).size.height / 2 + position.y - geometry.hexHeight / 2,
          child: HexagonWidget(
            label: tapLabel,
            secondaryLabel: longPressLabel,
            backgroundColor: hexColor,
            textColor: KeypadConfig.getComplementaryColor(hexColor),
            size: geometry.hexWidth,
            isPressed: _pressedHex == tapLabel || _pressedHex == longPressLabel,
            rotationAngle: geometry.rotationAngle,
            onTap: () => _onHexTap(tapLabel),
            onLongPress: () => _onHexLongPress(longPressLabel),
          ),
        );
      }).toList(),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        decoration: const BoxDecoration(
          gradient: RadialGradient(
            colors: [
              Color(0xFF1A1A2E),
              Color(0xFF0F0F1E),
              Colors.black,
            ],
            stops: [0.0, 0.5, 1.0],
          ),
        ),
        child: ListenableBuilder(
          listenable: inputService,
          builder: (context, _) {
            return Stack(
              children: [
                Center(
                  child: Stack(
                    children: [
                      // Order: outer ring (bottom), inner ring, module ring, center (top)
                      _buildOuterRing(),
                      _buildInnerRing(),
                      _buildModuleRing(),
                      _buildCenterAngol(),
                    ],
                  ),
                ),
                Positioned(
                  top: 40,
                  left: 0,
                  right: 0,
                  child: Center(
                    child: Container(
                      width: 300,
                      padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 10),
                      decoration: BoxDecoration(
                        color: Colors.black.withOpacity(0.7),
                        borderRadius: BorderRadius.circular(8),
                        border: Border.all(
                          color: inputService.isTextFieldFocused 
                              ? const Color(0xFF60A5FA) 
                              : const Color(0xFF4A90E2), 
                          width: 2,
                        ),
                      ),
                      child: TextField(
                        controller: _textController,
                        focusNode: _textFieldFocus,
                        style: const TextStyle(color: Colors.white, fontSize: 16),
                        decoration: const InputDecoration(
                          hintText: 'Tap to activate',
                          hintStyle: TextStyle(color: Colors.white38),
                          border: InputBorder.none,
                          isDense: true,
                        ),
                      ),
                    ),
                  ),
                ),
                Positioned(
                  top: 100,
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
                        inputService.isLetterMode ? 'Letter Mode' : 'Number Mode',
                        style: const TextStyle(
                          color: Colors.white70,
                          fontSize: 12,
                        ),
                      ),
                      if (inputService.isTextFieldFocused)
                        const Text(
                          'Cursor Mode',
                          style: TextStyle(
                            color: Colors.greenAccent,
                            fontSize: 10,
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
    );
  }

  @override
  void dispose() {
    _textFieldFocus.dispose();
    _textController.dispose();
    inputService.removeListener(_syncTextController);
    inputService.dispose();
    super.dispose();
  }
}
