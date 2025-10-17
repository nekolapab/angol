import 'package:flutter/material.dart';
import 'package:flutter/foundation.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'screens/angol_screen.dart';
import 'firebase_options.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp(
    options: DefaultFirebaseOptions.currentPlatform,
  );
  runApp(const AngolApp());
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
        // The auth state will be updated automatically via the stream
        // No need to do anything here unless you want to handle success/failure
      }).catchError((error) {
        // Silently ignore if no redirect is pending, or log if needed
        // You can add logging here if desired
      });
    }
  }

  Future<void> _signInWithGoogle(BuildContext context) async {
    try {
      if (kIsWeb) {
        // Use signInWithRedirect for web to avoid popup blockers
        await FirebaseAuth.instance.signInWithRedirect(GoogleAuthProvider());
      } else {
        // Use signInWithPopup for mobile platforms
        await FirebaseAuth.instance.signInWithPopup(GoogleAuthProvider());
      }
    } catch (e) {
      if (!context.mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          backgroundColor: Colors.redAccent,
          content: Text('Failed to sign in with Google: $e'),
        ),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return StreamBuilder<User?>(
      stream: FirebaseAuth.instance.authStateChanges(),
      builder: (context, snapshot) {
        // User is logged in
        if (snapshot.hasData) {
          return const AngolScreen();
        }

        // User is not logged in, or the auth state is loading
        if (snapshot.connectionState == ConnectionState.waiting) {
          return const Scaffold(
            body: Center(
              child: CircularProgressIndicator(),
            ),
          );
        }

        // Show the login button
        return Scaffold(
          body: Center(
            child: ElevatedButton(
              style: ElevatedButton.styleFrom(
                foregroundColor: Colors.blue.shade900,
                backgroundColor: Colors.white,
                padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 12),
              ),
              onPressed: () => _signInWithGoogle(context),
              child: const Text('Sign in with Google'),
            ),
          ),
        );
      },
    );
  }
}
