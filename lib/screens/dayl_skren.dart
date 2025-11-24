import 'package:angol/services/platform_sirves.dart';
import 'package:flutter/material.dart';
 import 'package:flutter/services.dart';
 import 'package:provider/provider.dart';
 import 'dart:developer' as developer;
 import 'dart:math' as math;
 import '../models/angol_modalz.dart';
 import '../utils/heksagon_djeyometre.dart';
 import '../services/enpit_sirves.dart';
 import '../state/angol_steyt.dart';
 import '../modyilz/kepad_modyil.dart';
 import '../modyilz/dayl_modyil.dart';

 class DaylSkren extends StatefulWidget {
   const DaylSkren({super.key});

   @override
   State<DaylSkren> createState() => _DaylSkrenSteyt();
 }

 class _DaylSkrenSteyt extends State<DaylSkren> {
   late EnpitSirves inputService;
   final int _defaultDisplayLength = 7; // Default number of glyphs to display

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
     developer.log('DaylSkren: _syncTextController called. Current inputService.inputText: "${inputService.inputText}"');
     if (_textController.text != inputService.inputText) {
       _textController.text = inputService.inputText;
       _textController.selection = TextSelection.fromPosition(
         TextPosition(offset: _textController.text.length),
       );
       developer.log('DaylSkren: _textController updated to: "${_textController.text}"');
     } else {
       developer.log('DaylSkren: _textController already matches inputService.inputText.');
     }
   }

   void _onHexKeyPress(String char,
       {bool isLongPress = false, String? primaryChar}) {
     developer.log('DaylSkren: _onHexKeyPress called for char: $char, isLongPress: $isLongPress');
     if (isLongPress) {
       HapticFeedback.mediumImpact();
       if (char == '⌫') {
         inputService.deleteWord();
         developer.log('DaylSkren: Deleting word.');
       } else {
         if (primaryChar != null) {
           inputService.deleteCharacters(primaryChar.length);
           developer.log('DaylSkren: Deleting ${primaryChar.length} characters.');
         }
         else {
           inputService.deleteLeft();
           developer.log('DaylSkren: Deleting left.');
         }
         inputService.addCharacter(char);
         developer.log('DaylSkren: Adding character (long press): $char');
       }
     } else {
       HapticFeedback.lightImpact();
       inputService.addCharacter(char);
       developer.log('DaylSkren: Adding character (short press): $char');
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
                 developer.log('DaylSkren: screenWidth: $screenWidth, screenHeight: $screenHeight');
                 if (screenWidth == 0 || screenHeight == 0) {
                   return const SizedBox.shrink(); // Or a loading indicator
                 }

                 final hexSize = math.min(screenWidth, screenHeight) / 8.5; // Adjust the divisor for best fit
                 developer.log('DaylSkren: Calculated hexSize: $hexSize');

                 return Consumer2<EnpitSirves, AngolSteyt>(
                   builder: (context, inputServiceFromProvider, angolStateFromProvider, _) {
                     final geometry = HeksagonDjeyometre(
                       hexSize: hexSize,
                       center: const HexagonPosition(x: 0, y: 0),
                       isLetterMode: inputServiceFromProvider.isLetterMode,
                     );

                     return Stack(
                       alignment: Alignment.center,
                       children: [
                         if (angolStateFromProvider.isKeypadVisible)
                           KepadModyil(
                             displayLength: _defaultDisplayLength,
                             geometry: geometry,
                             onHexKeyPress: _onHexKeyPress,
                             isKeypadVisible: angolStateFromProvider.isKeypadVisible,
                           )
                         else
                           DaylModyil(
                             geometry: geometry,
                             modules: angolStateFromProvider.modules,
                             onToggleModule: angolStateFromProvider.toggleModule,
                           ),
                       ],
                     );
                   },
                 );
               },
             ),
           ),
           Positioned(
             top: 40,
             right: 20,
             child: IconButton(
               icon: const Icon(Icons.settings, color: Colors.white),
               onPressed: () {
                 PlatformSirves.openImeSettings();
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
