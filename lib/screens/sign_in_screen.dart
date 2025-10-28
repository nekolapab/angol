import 'package:flutter/material.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'dart:developer' as developer;

class SignInScreen extends StatelessWidget {
  const SignInScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Sign In'),
      ),
      body: Center(
        child: ElevatedButton(
          onPressed: () async {
            try {
              // Using Google as an example
              GoogleAuthProvider googleProvider = GoogleAuthProvider();
              await FirebaseAuth.instance.signInWithRedirect(googleProvider);
            } catch (e) {
              developer.log('Error signing in with redirect: $e');
            }
          },
          child: const Text('Sign in with Google'),
        ),
      ),
    );
  }
}
