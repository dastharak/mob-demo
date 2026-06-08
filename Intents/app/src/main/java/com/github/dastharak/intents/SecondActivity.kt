package com.github.dastharak.intents

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity

val tag2:String = SecondActivity::class.java.toString()

class SecondActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_second)
        val title = intent.getStringExtra("T")
        val description = intent.getStringExtra("D")

        Log.d(tag2,"title:$title\ndescription:$description")
    }
}

