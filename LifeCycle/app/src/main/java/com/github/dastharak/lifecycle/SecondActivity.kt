package com.github.dastharak.lifecycle

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import java.util.Date

val tag2:String = SecondActivity::class.java.toString()

class SecondActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_second)
        Log.d(tag2,"onCreate(.)")

        val ca = callingActivity
        Log.d(tag2,"SecondActivity is called by $ca")

    }

    fun onClickHandler(v: View){
        val returnIntent = Intent().apply {
            //set results
            putExtra("key1", "payload-1")
            putExtra("key2", Date())//more payload
        }
        // Set the result status (RESULT_OK or RESULT_CANCELED) and attach the intent
        setResult(RESULT_OK, returnIntent)
        this.finish()
    }
}

