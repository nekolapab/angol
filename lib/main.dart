import 'package:flutter/material.dart';
import 'package:flutter/foundation.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:provider/provider.dart';
import 'screens/angol_screen.dart';
import 'services/input_service.dart';
import 'firebase_options.dart';
import 'dart:developer';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp(
    options: DefaultFirebaseOptions.currentPlatform,
  );
  runApp(
    ChangeNotifierProvider(
      create: (context) => InputService(),
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
      home: const AuthWrapper(),
    );
  }
}

class AuthWrapper extends StatefulWidget {
  const AuthWrapper({super.key});

  @override
  State<AuthWrapper> createState() => _AuthWrapperState();
}

class _AuthWrapperState extends State<AuthWrapper> {
  @override
  void initState() {
    super.initState();
    if (kIsWeb) {
      // Handle the redirect result to complete the sign-in process
      FirebaseAuth.instance.getRedirectResult().then((result) {
        log('getRedirectResult success: ${result.user}');
      }).catchError((error) {
        log('getRedirectResult error: $error');
      });
    }
  }



  @override
  Widget build(BuildContext context) {
    return const AngolScreen();
  }
}
