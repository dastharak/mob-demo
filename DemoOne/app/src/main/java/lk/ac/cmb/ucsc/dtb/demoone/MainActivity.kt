package lk.ac.cmb.ucsc.dtb.demoone

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val tag: String = MainActivity::class.java.name

class MainActivity : Activity() {
    // A CoroutineScope that runs on the Main (UI) thread.
    // This tied to the Activity's lifecycle
    // Job() allows to cancel all coroutines launched in this scope
    // when the Activity is destroyed
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())

    private lateinit var btnStartCoroutine: Button
    private lateinit var btnStartService: Button
    private lateinit var btnBlockingDelay: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Here we use the layout based UI definition
        setContentView(R.layout.activity_main)
        btnStartCoroutine = findViewById(R.id.btnStartCoroutine)
        btnStartService = findViewById(R.id.btnStartService)

        btnBlockingDelay = findViewById(R.id.btnBlockingDelay)

        //Setting a click listener to make the Button live
        btnStartCoroutine.setOnClickListener {
            simulateCoroutineTask()
        }

        btnStartService.setOnClickListener {
            Toast.makeText(applicationContext, "Start Foreground Service", Toast.LENGTH_SHORT)
                .show()
            //Send the intent to Foreground service
            val intent = Intent(this, ForegroundService::class.java)
            startForegroundService(intent)
        }

        btnBlockingDelay.setOnClickListener {
            try {
                Thread.sleep(2500)
            } catch (_: Exception) {

            }
            Toast.makeText(applicationContext, "Ended blocking", Toast.LENGTH_SHORT).show()
        }

    }

    private fun simulateCoroutineTask() {
        btnStartCoroutine.text = getString(R.string.starting_coroutine)
        Toast.makeText(applicationContext, "Coroutine Task", Toast.LENGTH_SHORT).show()

        // Launch a coroutine on the Main thread - Yet no freezing of UI
        coroutineScope.launch {
            // This suspends the coroutine without blocking the main thread (non-blocking delay)
            delay(5000)

            // After 5 seconds, update the UI with completion message
            btnStartCoroutine.text = getString(R.string.coroutine_task_complete)
        }

    }

    override fun onResume() {
        super.onResume()
        val intent = Intent(this, BoundService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        Log.d(tag, "bindService(...)")
    }

    override fun onPause() {
        super.onPause()
        if (isBound) {
            unbindService(serviceConnection)
            Log.d(tag, "unbindService(...)")
            isBound = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    //Service binding code
    private var boundService: BoundService? = null
    private var isBound = false

    //Implement the ServiceConnection interface
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d(tag, "onServiceConnected(..)")
            val binder = service as BoundService.LocalBinder
            boundService = binder.getService()
            isBound = true
            Toast.makeText(this@MainActivity, "Service Bound", Toast.LENGTH_SHORT).show()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(tag, "onServiceDisconnected(.)")
            boundService = null
            isBound = false
        }
    }

    // This is method is referred by the onClick property
    // of the Button in the layout definition
    fun getTimeFromService(view: View) {
        //This view is an instance of the Button
        view.setOnClickListener {
            if (isBound) {
                val time = boundService?.getCurrentTimestamp()
                Toast.makeText(this, "Timestamp: $time", Toast.LENGTH_SHORT).show()
            }
        }


    }


}
