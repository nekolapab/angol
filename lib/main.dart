import 'package:flutter/material.dart';
import 'dart:developer' as developer;
import 'package:firebase_core/firebase_core.dart';
import 'package:provider/provider.dart';
import 'package:permission_handler/permission_handler.dart';
import 'screens/dayl_skren.dart';
import 'services/enpit_sirves.dart';
import 'state/angol_steyt.dart';
import 'firebase_options.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  
  // Request Microphone permission and check status
  var status = await Permission.microphone.status;
  if (status.isDenied) {
    status = await Permission.microphone.request();
  }

  if (status.isPermanentlyDenied) {
    // Ideally show a dialog, but for now we just log it
    developer.log('Microphone permission permanently denied. Please enable in settings.');
  }

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

