package lk.ac.cmb.ucsc.dtb.demoone

import android.content.Intent
import android.widget.Toast
// LifecycleService is a special Service class that has a Lifecycle (like an Activity),
// Allows using lifecycle-aware components such as lifecycleScope
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
// Coroutines support
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * This service demonstrates how to use a Foreground Service together with Kotlin coroutines.
 * Foreground Services are used for long-running tasks that must continue even when the app is not in the foreground.
 */
class ForegroundService : LifecycleService() {

    override fun onCreate() {
        super.onCreate()
        // Create a notification channel for Android 8.0+ (required for foreground services)
        NotificationUtils.createNotificationChannel(this)
        // Build the actual notification to display while the service is running
        val notification = NotificationUtils.getNotification(this, "Foreground Task Running")
        // Promote this service to a foreground service using the built notification
        // Must be called within a few seconds after the service starts (for Android 8+)
        startForeground(1, notification)
    }

    /**
     * Called every time the service is explicitly started using startForegroundService().
     * We use this to launch a coroutine that simulates a background task.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Call to the superclass (good practice when overriding)
        super.onStartCommand(intent, flags, startId)
        // Launch a coroutine that runs on the main thread, but does not block it
        lifecycleScope.launch {
            // Simulate a 5s delay to represent a long-running operation
            delay(5000)
            // Show a message when the task completes (Note: Toast must run on the main thread)
            Toast.makeText(this@ForegroundService, "Foreground task completed", Toast.LENGTH_SHORT).show()
            // Stop the service after completing the task
            stopSelf()
        }
        // Indicate that the system should not try to restart the service if it's killed
        return START_NOT_STICKY
    }

}
