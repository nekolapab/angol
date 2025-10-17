
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import '../models/hexagon_models.dart';
import '../models/keypad_config.dart';
import '../utils/hex_geometry.dart';
import '../services/input_service.dart';
import '../widgets/hexagon_widget.dart';

class AngolScreen extends StatefulWidget {
  const AngolScreen({super.key});

  @override
  State<AngolScreen> createState() => _AngolScreenState();
}

class _AngolScreenState extends State<AngolScreen> {
  final InputService inputService = InputService();
  final FocusNode _textFieldFocus = FocusNode();
  final TextEditingController _textController = TextEditingController();

  List<ModuleData> modules = [
    const ModuleData(id: 'dayl', name: 'dayl', color: Color(0xFFFF0000), position: 0),
    const ModuleData(id: 'keypad', name: 'kepad', color: Color(0xFFFFFF00), position: 1),
    const ModuleData(id: 'module3', name: '', color: Color(0xFF00FF00), position: 2),
    const ModuleData(id: 'module4', name: '', color: Color(0xFF00FFFF), position: 3),
    const ModuleData(id: 'module5', name: '', color: Color(0xFF0000FF), position: 4),
    const ModuleData(id: 'module6', name: '', color: Color(0xFFFF00FF), position: 5),
  ];

  String _pressedHex = '';
  bool _angolPressed = false;

  @override
  void initState() {
    super.initState();
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
          isPressed: _angolPressed,
          rotationAngle: geometry.rotationAngle,
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Icon(
                inputService.isLetterMode ? Icons.text_fields : Icons.numbers,
                color: _angolPressed
                    ? complementaryColor
                    : KeypadConfig.getComplementaryColor(complementaryColor),
                size: 24,
              ),
              const SizedBox(height: 4),
              Text(
                inputService.getDisplayText(),
                style: TextStyle(
                  color: _angolPressed
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

  Widget _buildModuleRing() {
    if (_isKeypadVisible) return const SizedBox.shrink();

    final innerCoords = geometry.getInnerRingCoordinates();
    return Stack(
      children: innerCoords.asMap().entries.map((entry) {
        final index = entry.key;
        final coord = entry.value;
        final module = modules.firstWhere((m) => m.position == index);
        final position = geometry.axialToPixel(coord.q, coord.r);
        return Positioned(
          left: MediaQuery.of(context).size.width / 2 +
              position.x -
              geometry.hexWidth / 2,
          top: MediaQuery.of(context).size.height / 2 +
              position.y -
              geometry.hexHeight / 2,
          child: HexagonWidget(
            label: module.name,
            backgroundColor: module.color,
            textColor: KeypadConfig.getComplementaryColor(module.color),
            size: geometry.hexWidth,
            isPressed: module.isActive,
            rotationAngle: geometry.rotationAngle,
            onTap: () => _toggleModule(index),
          ),
        );
      }).toList(),
    );
  }
  
  Widget _buildKeypadRing() {
    if (!_isKeypadVisible) return const SizedBox.shrink();
    
    // Build inner ring
    final innerCoords = geometry.getInnerRingCoordinates();
    final innerLabels =
        inputService.isLetterMode ? KeypadConfig.innerLetterMode : KeypadConfig.innerNumberMode;
    final innerLongPress = inputService.isLetterMode
        ? List.filled(6, '')
        : KeypadConfig.innerLongPressNumber;

    final innerRingWidgets = innerCoords.asMap().entries.map((entry) {
      final index = entry.key;
      final coord = entry.value;
      final tapLabel = innerLabels[index];
      final longPressLabel = innerLongPress[index];
      final position = geometry.axialToPixel(coord.q, coord.r);

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
    }).toList();

    // Build outer ring
    final outerCoords = geometry.getOuterRingCoordinates();
    final outerRingWidgets = outerCoords.asMap().entries.map((entry) {
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
    }).toList();

    return Stack(
      children: [
        ...outerRingWidgets,
        ...innerRingWidgets,
      ],
    );
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
            child: ListenableBuilder(
              listenable: inputService,
              builder: (context, _) {
                return Stack(
                  children: [
                    Center(
                      child: Stack(
                        children: [
                          _buildKeypadRing(),
                          _buildModuleRing(),
                          _buildCenterAngol(),
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
                            inputService.isLetterMode ? 'Letter Mode' : 'Number Mode',
                            style: const TextStyle(
                              color: Colors.white70,
                              fontSize: 12,
                            ),
                          ),
                          const SizedBox(height: 20),
                          Container(
                            width: double.infinity,
                            padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 10),
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
                              style: const TextStyle(color: Colors.white, fontSize: 16),
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
    inputService.dispose();
    super.dispose();
  }
}
