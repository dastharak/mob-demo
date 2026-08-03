import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'sqlite.dart' as db;
import 'package:hive_flutter/hive_flutter.dart';
import 'package:connectivity_plus/connectivity_plus.dart';
import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:flutter/foundation.dart' show kIsWeb;
import 'firebase_options.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  // If running on Android, Firebase reads google-services.json automatically
  if (!kIsWeb) {
    await Firebase.initializeApp();
    print("Firebase init non-web");
  } else {
    await Firebase.initializeApp(
      options: DefaultFirebaseOptions.currentPlatform,
    );
    print("Firebase init web");
  }
  runApp(const MyApp());
}

// Root widget of the app
class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return const MaterialApp(
      title: 'Persistence', // Runtime name
      home: MyHomePage(), // Main screen of the app
    );
  }
}

/*
// Screen of the app is a StatefulWidget:
// Acts as a container and a factory for the State object
// and doesn’t directly handle UI rendering.
//
// UI making is delegated to the State class’s build method.
// Uses setState to trigger UI rebuilds when data changes.
// setState ensures the Text widget updates to show the new _count value.
*/
class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key});

  @override
  State<MyHomePage> createState() => _MyHomePageState();
  //createState is a method from State class that we override
}

// Enum makes it easier to handle the need
// rather than defining constants
enum DataSource {
  sharedprefs(0, "SharedPrefs"),
  sqlite(1, "SQLite DB"),
  hive(2, "In Memory"),
  firebase(3, "FireBase");

  final int num;
  final String str;
  const DataSource(this.num, this.str);
}

// The "State" holds mutable data
class _MyHomePageState extends State<MyHomePage> {
  int _count = 0; // count variable
  int _dataSource = 0; // Default data source
  bool _isChecking = false; // connectivity to fbase

  // Check if device can reach Firebase specifically
  Future<bool> _checkFirebaseConnection() async {
    bool ret;
    try {
      // Check if default app is actually initialized first
      if (Firebase.apps.isEmpty) {
        print("Firebase is not initialized yet!");
        ret = false;
      }

      await FirebaseFirestore.instance
          .collection('_ping')
          .limit(1)
          .get(const GetOptions(source: Source.server))
          .timeout(const Duration(seconds: 10));

      ret = true;
    } on FirebaseException catch (e) {
      if (e.code == 'permission-denied') return true; // Server responded
      ret = false;
    } catch (e) {
      // Catches web JS/DDC interop exceptions safely
      print("Firebase check failed with error: $e");
      ret = false;
    }
    setState(() =>
        _isChecking = ret); //We notify the framework as this var is used in UI
    return ret;
  }

  Future<void> _verifyFullConnectivity() async {
    setState(() => _isChecking = true);

    // Check basic device network connection
    final connectivityResults = await Connectivity().checkConnectivity();
    final bool hasDeviceNetwork =
        !connectivityResults.contains(ConnectivityResult.none);

    if (!hasDeviceNetwork) {
      print("No network connection on device.");
      setState(() => _isChecking = false);
      return;
    }

    // Check actual connection to Firebase
    final bool canReachFirebase = await _checkFirebaseConnection();

    if (canReachFirebase) {
      print("Successfully connected to Firebase!");
    } else {
      print("Network connected, but Firebase is unreachable.");
    }

    setState(() => _isChecking = false);
  }

  // Called when widget is created (before UI build)
  @override
  void initState() {
    super.initState();
    print('initState()');
    _loadConfig();
    print("_dataSource $_dataSource");
    _loadCounter(_dataSource);
  }

  //Some callbacks from State class, not required to implement
  // Called when the parent widget is rebuilt with new configuration
  @override
  void didUpdateWidget(covariant MyHomePage oldWidget) {
    super.didUpdateWidget(oldWidget);
    print('didUpdateWidget(.)');
  }

  // Called when the State is permanently removed
  @override
  void dispose() {
    super.dispose();
    print('dispose()');
  }

  // Load config value from SharedPreferences
  Future<void> _loadConfig() async {
    final prefs = await SharedPreferences.getInstance();
    int? i = prefs.getInt('data_source');
    if (i != null && -1 < i && i < DataSource.values.length) {
      _dataSource = i;
    } else {
      _dataSource = 0; //default source
    }
  }

  Future<void> _loadCounter(int ii) async {
    print("_loadCounter $ii");
    if (ii == 0) {
      // Load stored count from Shared Prefs
      final prefs = await SharedPreferences.getInstance();
      int? c = prefs.getInt('counter');
      print("Shared Prefs Loaded : $c");
      _count = c ?? 0;
      setState(() => _count);
    } else if (ii == 1) {
      int c = await db.CounterDB.getCounter();
      print("SQLite DB Loaded : $c");
      _count = c;
      setState(() => _count);
    } else if (ii == 2) {
      WidgetsFlutterBinding.ensureInitialized();
      await Hive.initFlutter();
      final box = await Hive.openBox<int>('counterHV');
      int? c = box.get('counterHV', defaultValue: 0);
      _count = c ?? 0;
      setState(() => _count);
    } else if (ii == 3) {
      print("NYI..");
    } else {
      print('Warn:Must not reach 1!');
    }
  }

  // Increase count and save it to SharedPreferences
  Future<void> _incrementCounter() async {
    setState(() => _count++); // Update UI
    if (_dataSource == 0) {
      print("Preferences Updating to $_count");
      final prefs = await SharedPreferences.getInstance();
      await prefs.setInt('counter', _count); // Save value locally
    } else if (_dataSource == 1) {
      print("SQLite DB Updating to $_count");
      await db.CounterDB.updateCounter(_count);
    } else if (_dataSource == 2) {
      print("Hive DB Updating to $_count");
      WidgetsFlutterBinding.ensureInitialized();
      // Initialize Hive for Flutter, setting up the storage directory
      await Hive.initFlutter();
      final box = await Hive.openBox<int>('counterHV');
      box.put('counter', _count);
    } else if (_dataSource == 3) {
      // Connection to Firebase
      print("Verifying connectivyt with fbase 1");
      await _verifyFullConnectivity();
      print("Setting the count...");
      await setCount(_count);
    } else {
      print('Warn:Must not reach 2!');
    }
  }

  //Update the count in Firestore
  Future<void> setCount(int num) async {
    try {
      await FirebaseFirestore.instance
          .collection('PersistenceDemoTable')
          .doc('Config') // document ID
          .set({
        'id': 'count',
        'value': num.toString(),
      });
      print('Firestore updated successfully!');
    } catch (e) {
      print('Failed to set data: $e');
    }
  }

  // A temporary message
  void snackMsg(String txt, String var1) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(txt + var1)),
    );
  }

  // Change data source (menu action)
  void _changeDataSource(String newSource) {
    var t = int.parse(newSource);
    setState(() {
      //do minimal work inside set state
      _dataSource = t >= 0 && t < 4 ? t : 0;
    });
    String s = "Data source changed to ";
    snackMsg(s, _dataSource.toString());
    print(s + _dataSource.toString());
  }

  //This is t description of app UI
  // Build the UI (re-runs every time setState is called)
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Persistence Types'), // Title on app bar
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
        actions: [
          // Popup menu button in AppBar
          PopupMenuButton<String>(
            onSelected: _changeDataSource,
            itemBuilder: (context) => [
              PopupMenuItem(
                  value: DataSource.sharedprefs.num.toString(),
                  child: Text(DataSource.sharedprefs.str)),
              PopupMenuItem(
                  value: DataSource.sqlite.num.toString(),
                  child: Text(DataSource.sqlite.str)),
              PopupMenuItem(
                  value: DataSource.hive.num.toString(),
                  child: Text(DataSource.hive.str)),
              PopupMenuItem(
                  value: DataSource.firebase.num.toString(),
                  child: Text(DataSource.firebase.str)),
            ],
          )
        ],
      ),
      body: Center(
        // Column places widgets vertically
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Text('You have pushed the button '),
            Text(
              '$_count', // Show count value
              style: Theme.of(context).textTheme.headlineMedium,
            ),
            const Text(' times.'),
          ],
        ),
      ),
      // Floating button to increment count
      floatingActionButton: FloatingActionButton(
        onPressed: _incrementCounter, // Increase and save count
        tooltip: 'Increment',
        child: const Icon(Icons.add),
      ),
      bottomSheet:
          Text("Firebase state ${_isChecking ? "connecting" : "idle"}"),
    );
  }
}
