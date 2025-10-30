import 'package:flutter/material.dart';
import 'package:firebase_auth/firebase_auth.dart';

class Afdirlogen extends StatelessWidget {
  const Afdirlogen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Home Page'),
        actions: [
          IconButton(
            icon: const Icon(Icons.logout),
            onPressed: () async {
              await FirebaseAuth.instance.signOut();
            },
          ),
        ],
      ),
      body: const Center(
        child: Text('Welcome! You are signed in.'),
      ),
    );
  }
}
