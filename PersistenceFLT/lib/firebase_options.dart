import 'package:firebase_core/firebase_core.dart' show FirebaseOptions;
import 'package:flutter/foundation.dart'
    show defaultTargetPlatform, kIsWeb, TargetPlatform;

class DefaultFirebaseOptions {
  static FirebaseOptions get currentPlatform {
    if (kIsWeb) {
      print("Target Web");
      return web;
    }
    switch (defaultTargetPlatform) {
      case TargetPlatform.android:
        print("Target Android");
        return android;
      case TargetPlatform.windows:
        throw UnsupportedError(
          'DefaultFirebaseOptions have not been configured for windows - '
          'you can reconfigure this by running the FlutterFire CLI again.',
        );
      case TargetPlatform.linux:
        throw UnsupportedError(
          'DefaultFirebaseOptions have not been configured for linux - '
          'you can reconfigure this by running the FlutterFire CLI again.',
        );
      default:
        throw UnsupportedError(
          'DefaultFirebaseOptions are not configured for this platform.',
        );
    }
  }

  // Web Credentials : Get from Firebase Console, Web App
  static const FirebaseOptions web = FirebaseOptions(
    apiKey: 'abc',
    appId: '1:999:web:856687e68784f9',
    messagingSenderId: '96787879892',
    projectId: 'p-417',
    authDomain: 'p-417.firebaseapp.com',
    storageBucket: 'p-417.firebasestorage.app',
  );

  // Android Credentials inside google-services.json file
  static const FirebaseOptions android = FirebaseOptions(
    apiKey: 'abc',
    appId: '1:999:web:856687e68784f9',
    messagingSenderId: '96787879892',
    projectId: 'p-417',
    authDomain: 'p-417.firebaseapp.com',
    storageBucket: 'p-417.firebasestorage.app',
  );
}
