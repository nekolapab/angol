import 'package:firebase_core/firebase_core.dart' show FirebaseOptions;
import 'package:flutter/foundation.dart'
    show defaultTargetPlatform, kIsWeb, TargetPlatform;

class DefaultFirebaseOptions {
  static FirebaseOptions get currentPlatform {
    if (kIsWeb) {
      return web;
    }
    switch (defaultTargetPlatform) {
      case TargetPlatform.android:
        return android;
      case TargetPlatform.iOS:
        return ios;
      default:
        throw UnsupportedError('DefaultFirebaseOptions not configured');
    }
  }

  static const FirebaseOptions web = FirebaseOptions(
    apiKey: 'AIzaSyDUZZLgF2AmrMiRxLyf6QNxgUFXQosVhAs',
    appId: '1:697321034497:web:90642338da14ca26da8100',
    messagingSenderId: '697321034497',
    projectId: 'angol-38753',
    authDomain: 'angol-38753.firebaseapp.com',
    storageBucket: 'angol-38753.firebasestorage.app',
    measurementId: 'G-DK3PN3HXJM',
  );

  static const FirebaseOptions android = FirebaseOptions(
    apiKey: 'AIzaSyA90vksMh_iC-Bn2q0TiprVA2Ue1J8E0Uw',
    appId: '1:697321034497:android:a077e11aa9631f59da8100',
    messagingSenderId: '697321034497',
    projectId: 'angol-38753',
    storageBucket: 'angol-38753.appspot.com',
  );

  static const FirebaseOptions ios = FirebaseOptions(
    apiKey: 'AIzaSyC9r4nG7Aw-HUNam1APnEQjQzdfW-hYpng',
    appId: '1:697321034497:ios:f1bc5af9a8345bbfda8100',
    messagingSenderId: '697321034497',
    projectId: 'angol-38753',
    storageBucket: 'angol-38753.appspot.com',
    iosBundleId: 'com.nekolapab.angol',
  );
}
