import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'dart:developer' as dev;
import 'sqlite.dart' as db;

// Entry point of the Flutter app
void main() => runApp(const MyApp());

// Root widget of the app
class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return const MaterialApp(
      title: 'Persistence Demo', // Top bar and runtime name
      home: MyHomePage(), // Main screen of the app
    );
  }
}

/*
// Screen of the app is a StatefulWidget:
// Acts as a factory for the State object
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

// The "State" holds mutable data
class _MyHomePageState extends State<MyHomePage> {
  int _count = 0; // count variable
  static const Map<int, String> _dataSrcs = {
    0: "Prefs",
    1: "SQLite",
    2: "Memory"
  };
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

  // Load saved counter value from SharedPreferences
  Future<void> _loadConfig() async {
    final prefs = await SharedPreferences.getInstance();
    int? i = prefs.getInt('data_source');
    if (i != null && _dataSrcs.containsKey(i)) {
      _dataSource = i;
    } else {
      _dataSource = 0; //default source
    }
  }

  // Load saved counter value from SharedPreferences
  Future<void> _loadCounter(int ii) async {
    if (ii == 0) {
      final prefs = await SharedPreferences
          .getInstance(); // Load stored counter value from local storage
      int? c = prefs.getInt('counter');
      print("Shared Prefs Loaded : $c");
      _count = c ?? 0;
      setState(() => _count);
    } else if (ii == 1) {
      final c = await db.CounterDB.getCounter();
      print("SQLite DB Loaded : $c");
      _count = c;
      setState(() => _count);
    }
  }

  // Increase counter and save it to SharedPreferences
  Future<void> _incrementCounter() async {
    setState(() => _count++); // Update UI
    if (_dataSource == 0) {
      print("Preferences Updating to $_count");
      final prefs = await SharedPreferences.getInstance();
      await prefs.setInt('counter', _count); // Save value locally
    } else if (_dataSource == 1) {
      print("SQLite DB Updating to $_count");
      await db.CounterDB.updateCounter(_count);
    }
  }

  // Change data source (menu action)
  void _changeDataSource(String newSource) {
    setState(() {
      _dataSource = int.parse(newSource);
    });
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text("Data source changed to $_dataSource")),
    );
  }

  // Build the UI (re-runs every time setState is called)
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Persistence Demo'), // Title on app bar
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
        actions: [
          // Popup menu button in AppBar
          PopupMenuButton<String>(
            onSelected: _changeDataSource,
            itemBuilder: (context) => [
              const PopupMenuItem(value: "0", child: Text("Shared Prefs")),
              const PopupMenuItem(value: "1", child: Text("Local DB")),
              const PopupMenuItem(value: "2", child: Text("In-Memory")),
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
              '$_count', // Show counter value
              style: Theme.of(context).textTheme.headlineMedium,
            ),
            const Text(' times.'),
          ],
        ),
      ),
      // Floating button to increment counter
      floatingActionButton: FloatingActionButton(
        onPressed: _incrementCounter, // Increase + save counter
        tooltip: 'Increment',
        child: const Icon(Icons.add),
      ),
    );
  }
}
