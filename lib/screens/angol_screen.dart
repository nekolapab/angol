
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
  late InputService inputService;
  late FirebaseService firebaseService;
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
    inputService = InputService();
    firebaseService = FirebaseService();
    
    _textFieldFocus.addListener(() {
      inputService.setTextFieldFocus(_textFieldFocus.hasFocus);
    });
  }

  HexGeometry get geometry => HexGeometry(
    hexSize: 35,
    center: const HexagonPosition(x: 0, y: 0),
    isLetterMode: inputService.isLetterMode,
  );

  void _onHexTap(String char) {
    setState(() => _pressedHex = char);
    HapticFeedback.lightImpact();
    inputService.addCharacter(char);
    _textController.text = inputService.inputText;
    
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
    _textController.text = inputService.inputText;
    
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
    return Positioned(
      left: MediaQuery.of(context).size.width / 2 - geometry.hexWidth / 2,
      top: MediaQuery.of(context).size.height / 2 - geometry.hexHeight / 2,
      child: GestureDetector(
        onTapDown: (_) => setState(() => _angolPressed = true),
        onTapUp: (_) => setState(() => _angolPressed = false),
        onTapCancel: () => setState(() => _angolPressed = false),
        onTap: () => inputService.toggleMode(),
        onVerticalDragUpdate: (details) {
          if (details.delta.dy < -5) {
            inputService.setCapitalize();
          }
        },
        child: HexagonWidget(
          label: '',
          backgroundColor: Colors.black,
          textColor: Colors.white,
          size: geometry.hexWidth,
          isPressed: _angolPressed,
          rotationAngle: geometry.rotationAngle,
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Icon(
                inputService.isLetterMode ? Icons.text_fields : Icons.numbers,
                color: _angolPressed ? Colors.black : Colors.white,
                size: 16,
              ),
              const SizedBox(height: 2),
              Text(
                inputService.getDisplayText(),
                style: TextStyle(
                  color: _angolPressed ? Colors.black : Colors.white,
                  fontSize: 8,
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

  Widget _buildInnerRing() {
    if (!_angolPressed && !inputService.isTextFieldFocused) {
      return const SizedBox.shrink();
    }
    
    final innerCoords = geometry.getInnerRingCoordinates();
    
    return Stack(
      children: innerCoords.asMap().entries.map((entry) {
        final index = entry.key;
        final coord = entry.value;
        final module = modules.firstWhere((m) => m.position == index);
        final position = geometry.axialToPixel(coord.q, coord.r);

        return Positioned(
          left: MediaQuery.of(context).size.width / 2 + position.x - geometry.hexWidth / 2,
          top: MediaQuery.of(context).size.height / 2 + position.y - geometry.hexHeight / 2,
          child: HexagonWidget(
            label: module.name,
            backgroundColor: module.color,
            textColor: Colors.white,
            size: geometry.hexWidth,
            isPressed: false,
            rotationAngle: geometry.rotationAngle,
            onTap: () => _toggleModule(index),
          ),
        );
      }).toList(),
    );
  }

  Widget _buildOuterRing() {
    final keypadModule = modules.firstWhere((m) => m.id == 'keypad');
    if (!keypadModule.isActive && !inputService.isTextFieldFocused) {
      return const SizedBox.shrink();
    }
    
    final outerCoords = geometry.getOuterRingCoordinates();
    final innerCoords = geometry.getInnerRingCoordinates();
    final innerLabels = inputService.isLetterMode
        ? KeypadConfig.innerLetterMode
        : KeypadConfig.innerNumberMode;
    final innerLongPress = inputService.isLetterMode
        ? List.filled(6, '')
        : KeypadConfig.innerLongPressNumber;

    return Stack(
      children: [
        // Outer ring - rainbow colors
        ...outerCoords.asMap().entries.map((entry) {
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
              textColor: Colors.white,
              size: geometry.hexWidth,
              isPressed: _pressedHex == tapLabel || _pressedHex == longPressLabel,
              rotationAngle: geometry.rotationAngle,
              onTap: () => _onHexTap(tapLabel),
              onLongPress: () => _onHexLongPress(longPressLabel),
            ),
          );
        }),
        
        // Inner ring - keypad characters
        ...innerCoords.asMap().entries.map((entry) {
          final index = entry.key;
          final coord = entry.value;
          final tapLabel = innerLabels[index];
          final longPressLabel = innerLongPress[index];
          final position = geometry.axialToPixel(coord.q, coord.r);
          
          final scaledX = position.x * 0.5;
          final scaledY = position.y * 0.5;

          return Positioned(
            left: MediaQuery.of(context).size.width / 2 + scaledX - geometry.hexWidth / 2,
            top: MediaQuery.of(context).size.height / 2 + scaledY - geometry.hexHeight / 2,
            child: HexagonWidget(
              label: tapLabel,
              secondaryLabel: longPressLabel.isNotEmpty ? longPressLabel : null,
              backgroundColor: keypadModule.color.withOpacity(0.9),
              textColor: Colors.black,
              size: geometry.hexWidth,
              isPressed: _pressedHex == tapLabel || _pressedHex == longPressLabel,
              rotationAngle: geometry.rotationAngle,
              onTap: () => _onHexTap(tapLabel),
              onLongPress: longPressLabel.isNotEmpty 
                  ? () => _onHexLongPress(longPressLabel)
                  : null,
            ),
          );
        }),
      ],
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
                      _buildOuterRing(),
                      _buildInnerRing(),
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
                      padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 10),
                      decoration: BoxDecoration(
                        color: Colors.black.withOpacity(0.7),
                        borderRadius: BorderRadius.circular(8),
                        border: Border.all(color: const Color(0xFF4A90E2), width: 2),
                      ),
                      child: TextField(
                        controller: _textController,
                        focusNode: _textFieldFocus,
                        style: const TextStyle(color: Colors.white, fontSize: 16),
                        decoration: const InputDecoration(
                          hintText: 'Focus here for cursor mode...',
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
                      if (_angolPressed || inputService.isTextFieldFocused)
                        Text(
                          inputService.isTextFieldFocused ? 'Cursor Mode' : 'Pointer Mode',
                          style: const TextStyle(
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
    inputService.dispose();
    super.dispose();
  }
}
