import 'package:sqflite/sqflite.dart';
import 'package:path/path.dart'; //for constructing file paths in a platform-independent way

class CounterDB {
  // _getDatabase : private method to the class as no need to access from outside
  static Future<Database> _getDatabase() async {
    final path = join(await getDatabasesPath(), 'counter.db');
    // Open or create the database at the specified path
    return openDatabase(
      path,
      //Single Instance per Path: The path parameter in openDatabase acts as a key.
      //For 'counter.db', only one Database object is maintained,
      // regardless of how many times _getDatabase is called.
      version: 1, // Database version for schema upgrades
      // Called only when the database is created for the first time
      onCreate: (db, version) async {
        await db.execute(
            'CREATE TABLE config(id INTEGER PRIMARY KEY, value INTEGER)');
        // Insert an initial row with id=0 and value=0
        await db.insert('config', {'id': 0, 'value': 0});
      },
    );
  }

  static Future<int> getCounter() async {
    final db = await _getDatabase();
    // Query the 'counter' table for the row where id=0
    final result = await db.query('config', where: 'id = ?', whereArgs: [0]);
    // Return the 'value' field from the first row as an integer
    return result.first['value'] as int;
  }

  static Future<void> updateCounter(int value) async {
    // Get the database instance
    final db = await _getDatabase();
    // Update the 'value' field for the row where id=0
    await db.update('config', {'value': value},
        where: 'id = ?', whereArgs: [0]);
  }
}
