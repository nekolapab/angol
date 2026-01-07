import 'package:flutter/material.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:provider/provider.dart';
import 'screens/dayl_skren.dart';
import 'services/enpit_sirves.dart';
import 'state/angol_steyt.dart';
import 'firebase_options.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  try {
    await Firebase.initializeApp(
      options: DefaultFirebaseOptions.currentPlatform,
    );
  } catch (e) {
    // Firebase already initialized, ignore
  }
  runApp(
    MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => EnpitSirves()),
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
      title: 'dayl',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        brightness: Brightness.dark,
        scaffoldBackgroundColor: const Color(0xFF111111),
        useMaterial3: true,
      ),
      home: const DaylSkren(),
    ); 
  }
}

