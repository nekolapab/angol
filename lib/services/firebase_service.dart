import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_auth/firebase_auth.dart';
import '../models/hexagon_models.dart';

class FirebaseService {
  final FirebaseFirestore _firestore = FirebaseFirestore.instance;
  final FirebaseAuth _auth = FirebaseAuth.instance;
  
  Future<void> signInWithGitHub() async {
    try {
      final githubProvider = GithubAuthProvider();
      await _auth.signInWithProvider(githubProvider);
    } catch (e) {
      print('GitHub sign-in error: $e');
      rethrow;
    }
  }
  
  Future<void> signOut() async {
    await _auth.signOut();
  }
  
  User? get currentUser => _auth.currentUser;
  
  Stream<User?> get authStateChanges => _auth.authStateChanges();
  
  Future<void> saveModuleLayout(List<ModuleData> modules) async {
    final user = currentUser;
    if (user == null) return;
    
    try {
      await _firestore
          .collection('users')
          .doc(user.uid)
          .collection('layouts')
          .doc('current')
          .set({
        'modules': modules.map((m) => m.toJson()).toList(),
        'updatedAt': FieldValue.serverTimestamp(),
      });
    } catch (e) {
      print('Error saving layout: $e');
    }
  }
  
  Stream<List<ModuleData>> watchModuleLayout() {
    final user = currentUser;
    if (user == null) return Stream.value([]);
    
    return _firestore
        .collection('users')
        .doc(user.uid)
        .collection('layouts')
        .doc('current')
        .snapshots()
        .map((snapshot) {
      if (!snapshot.exists) return [];
      final data = snapshot.data();
      if (data == null || data['modules'] == null) return [];
      return (data['modules'] as List)
          .map((m) => ModuleData.fromJson(m))
          .toList();
    });
  }
}
