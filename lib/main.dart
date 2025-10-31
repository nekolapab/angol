import 'package:flutter/material.dart';
import 'package:firebase_core/firebase_core.dart';

import 'package:provider/provider.dart';
import 'screens/DaylModal.dart';
import 'services/EnpitSirves.dart';
import 'state/AngolSteyt.dart';
import 'firebase_options.dart';

late final EnpitSirves inputService;

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp(
    options: DefaultFirebaseOptions.currentPlatform,
  );
  inputService = EnpitSirves();
  runApp(
    MultiProvider(
      providers: [
        ChangeNotifierProvider<EnpitSirves>(create: (_) => inputService),
        ChangeNotifierProvider(create: (context) => AngolSteyt()),
      ],
      child: const AngolApp(),
    ),
  );
}

class AngolApp extends StatelessWidget {
  const AngolApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Angol',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        brightness: Brightness.dark,
        scaffoldBackgroundColor: const Color(0xFF111111),
        useMaterial3: true,
      ),
          home: DaylModal(),
    );
  }
}
