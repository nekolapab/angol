import 'package:angol/sirvesez/platform_sirves.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'dart:developer' as developer;
import 'dart:math' as math;
import '../modalz/angol_modalz.dart';
import '../yutelez/heksagon_djeyometre.dart';
import '../sirvesez/enpit_sirves.dart';
import '../steyt/angol_steyt.dart';
import '../modyilz/dayl_modyil.dart';

class DaylSkren extends StatefulWidget {
  const DaylSkren({super.key});

  @override
  State<DaylSkren> createState() => _DaylSkrenSteyt();
}

class _DaylSkrenSteyt extends State<DaylSkren> {
  late EnpitSirves inputService;

  final FocusNode _textFieldFocus = FocusNode();
  final TextEditingController _textController = TextEditingController();

  @override
  void initState() {
    super.initState();
    Provider.of<AngolSteyt>(context, listen: false).reset(notify: false);
    inputService = Provider.of<EnpitSirves>(context, listen: false);
    _textFieldFocus.addListener(() {
      inputService.setTextFieldFocus(_textFieldFocus.hasFocus);
    });
    inputService.addListener(_syncTextController);
  }

  @override
  void reassemble() {
    super.reassemble();
    Provider.of<AngolSteyt>(context, listen: false).reset(notify: false);
  }

  void _syncTextController() {
    developer.log(
        'DaylSkren: _syncTextController called. Current inputService.inputText: "${inputService.inputText}"');
    if (_textController.text != inputService.inputText) {
      _textController.text = inputService.inputText;
      _textController.selection = TextSelection.fromPosition(
        TextPosition(offset: _textController.text.length),
      );
      developer.log(
          'DaylSkren: _textController updated to: "${_textController.text}"');
    } else {
      developer.log(
          'DaylSkren: _textController already matches inputService.inputText.');
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
            child: LayoutBuilder(
              builder: (context, constraints) {
                final screenWidth = constraints.maxWidth;
                final screenHeight = constraints.maxHeight;
                developer.log(
                    'DaylSkren: screenWidth: $screenWidth, screenHeight: $screenHeight');
                if (screenWidth == 0 || screenHeight == 0) {
                  return const SizedBox.shrink(); // Or a loading indicator
                }

                final hexSize = math.min(screenWidth, screenHeight) /
                    8.0; // Adjust the divisor for best fit
                developer.log('DaylSkren: Calculated hexSize: $hexSize');

                return Consumer2<EnpitSirves, AngolSteyt>(
                  builder: (context, inputServiceFromProvider,
                      angolStateFromProvider, _) {
                    final geometry = HeksagonDjeyometre(
                      heksSayz: hexSize,
                      sentir: const HeksagonPozecon(x: 0, y: 0),
                      ezLeterMod: inputServiceFromProvider.isLetterMode,
                    );

                    return Stack(
                      alignment: Alignment.center,
                      children: [
                        // Flutter version of KepadModyil removed - using Compose IME only
                        DaylModyil(
                          geometry: geometry,
                          modyilz: angolStateFromProvider.modyilz,
                          onToggleModule: angolStateFromProvider.togilModyil,
                        ),
                        // Added TextField to trigger the System IME (Compose version)
                        Positioned(
                          top: 60,
                          left: 20,
                          right: 20,
                          child: TextField(
                            focusNode: _textFieldFocus,
                            controller: _textController,
                            decoration: InputDecoration(
                              hintText: 'tap her tu test Compose IME',
                              hintStyle:
                                  const TextStyle(color: Colors.cyanAccent),
                              filled: true,
                              fillColor: Colors.black.withValues(alpha: 0.5),
                              border: const OutlineInputBorder(),
                            ),
                            style: const TextStyle(color: Colors.white),
                          ),
                        ),
                        // Settings button, only visible when DaylModyil is active
                        if (!angolStateFromProvider.ezKepadVezebil)
                          Align(
                            alignment: Alignment
                                .bottomCenter, // Moved to bottomCenter to avoid central overlap
                            child: IconButton(
                              icon: const Icon(Icons.settings,
                                  color: Colors.white),
                              onPressed: () {
                                PlatformSirves.openImeSettings();
                              },
                            ),
                          ),
                      ],
                    );
                  },
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
