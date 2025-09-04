import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'sqlite.dart' as db;
import 'package:hive_flutter/hive_flutter.dart';

void main() => runApp(const MyApp());

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
}

//Enum makes it easier to handle the need
// rather than defining constants
enum DataSource {
  sharedprefs(0, "SharedPrefs"),
  sqlite(1, "SQLite DB"),
  hive(2, "In Memory"),
  firebase(2, "FireBase");

  final int num;
  final String str;
  const DataSource(this.num, this.str);
}

// The "State" holds mutable data
class _MyHomePageState extends State<MyHomePage> {
  int _count = 0; // count variable
  int _dataSource = 0; // Default data source

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
      // Read from Firebase
      //TODO
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
      //TODO
    } else {
      print('Warn:Must not reach 2!');
    }
  }

  // Change data source (menu action)
  void _changeDataSource(String newSource) {
    setState(() {
      _dataSource = int.parse(newSource);
    });
    // a temporary message
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text("Data source changed to $_dataSource")),
    );
  }

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
    );
  }
}
