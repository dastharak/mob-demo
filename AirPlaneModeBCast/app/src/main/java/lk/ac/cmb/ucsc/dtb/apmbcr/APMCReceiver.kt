package lk.ac.cmb.ucsc.dtb.apmbcr

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class APMCReceiver(private val onChange: ()-> Unit) : BroadcastReceiver() {

    private val tag = APMCReceiver::class.java.name

    override fun onReceive(context: Context, intent: Intent) {
        Toast.makeText(context, "Airplane mode changed", Toast.LENGTH_SHORT).show()
        if (intent.action == Intent.ACTION_AIRPLANE_MODE_CHANGED) {
            onChange()//lambda function to transfer the event to the ViewModel
            // achieving the the decoupling of BCR from its users
        }
        Log.d(tag, "Broadcast Intent$intent")
    }
}