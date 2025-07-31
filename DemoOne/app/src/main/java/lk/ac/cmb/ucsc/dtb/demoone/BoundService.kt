package lk.ac.cmb.ucsc.dtb.demoone

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log

class BoundService : Service() {

    private val tag = BoundService::class.java.name
    // Binder given to clients
    private val binder = LocalBinder()

    // Expose this class to clients
    inner class LocalBinder : Binder() {
        fun getService(): BoundService = this@BoundService
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.d(tag,"onBind(.)")
        return binder
    }

    // Simple method for client(s) to call
    fun getCurrentTimestamp(): Long {
        return System.currentTimeMillis()
    }
}
