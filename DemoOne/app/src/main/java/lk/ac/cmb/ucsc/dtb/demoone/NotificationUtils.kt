package lk.ac.cmb.ucsc.dtb.demoone

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

object NotificationUtils {
    private const val CHANNEL_ID = "foreground_channel_id"
    private const val CHANNEL_NAME = "Foreground Service Channel"

    fun createNotificationChannel(context: Context) {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = context.getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(serviceChannel)
    }

    fun getNotification(context: Context, message: String): Notification {
        val notificationIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Uses the builder pattern to make a  NotificationCompat.Builder
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Service Running Task")
            .setContentText(message)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentIntent(pendingIntent)
            .build()
    }
}
