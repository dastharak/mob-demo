package com.github.dastharak.lifecycle

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat.startActivityForResult
import com.github.dastharak.lifecycle.ui.theme.LifeCycleTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val tag:String = MainActivity::class.simpleName.toString()

class MainActivity :  ComponentActivity() {
    private val cvm = CallbackViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val timeStamp = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Date())
        Log.d(tag,"$timeStamp : onCreate()")
        //enableEdgeToEdge()// Configures the system bars to draw the UI behind them transparently.
        // setContent acts as the bridge between legacy views and the Compose runtime.
        setContent {
            LifeCycleTheme {
                //  context from the environment provider
                val context = LocalContext.current
                CallbackLogScreen(cvm,context,this)
            }
        }
        cvm.addCallbackLog("onCreate(${savedInstanceState})")
    }

    override fun onStart() {
        super.onStart()
        cvm.addCallbackLog("onStart()")
    }

    override fun onResume() {
        super.onResume()
        cvm.addCallbackLog("onResume()")
    }

    override fun onPause() {
        super.onPause()
        cvm.addCallbackLog("onPause()")
    }

    override fun onStop() {
        super.onStop()
        cvm.addCallbackLog("onStop()")
    }

    override fun onRestart() {
        super.onRestart()
        cvm.addCallbackLog("onRestart()")
    }

    override fun onDestroy() {
        super.onDestroy()
        cvm.addCallbackLog("onDestroy()")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        cvm.addCallbackLog("onSaveInstanceState(Bundle)")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        cvm.addCallbackLog("onRestoreInstanceState(Bundle)")
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        cvm.addCallbackLog("onNewIntent(Intent)")
    }

    @Deprecated(message = "TODO upgrade later")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK){
            Log.d(tag2,"Success")
            val r1 = data?.getStringExtra("key1")
            val r2 = data?.getDoubleExtra("key2",-1.0)
            Toast.makeText(this,"Results: r1:$r1\nr2:$r2\n",Toast.LENGTH_LONG).show()
        }
    }
}

@Composable
fun CallbackLogScreen(viewModel: CallbackViewModel,context:Context,m: Activity?) {
    // Collect updates reactively
    val logs by viewModel.logs.collectAsState()
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
                                .padding(16.dp)
                                .background(Color.LightGray)
                                .weight(0.9f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Use the id as a key for peak rendering performance
            items(items = logs, key = { log -> log.id }) { log ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    // Gray timestamp
                    Text(
                        text = "[${log.timestamp}]",
                        color = Color.DarkGray,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    // Callback description
                    Text(
                        text = log.message,
                        color = Color.Black,
                        fontFamily = FontFamily.Serif,
                        fontSize = 16.sp
                    )
                }
            }
        }
        Column(modifier = Modifier.fillMaxWidth()
            .padding(16.dp)
            .background(Color.Blue),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = {
                val i = Intent(context, SecondActivity::class.java)
                context.startActivity(i)
            }, modifier = Modifier.align(alignment = Alignment.CenterHorizontally)) {
                Text("Jump To Second")
            }

            Button(onClick = {
                val i = Intent(context, SecondActivity::class.java)
                //TODO this call to be updated with recent APIs
                if (m !=null)
                    startActivityForResult(m,i,7,null)
            }, modifier = Modifier.align(alignment = Alignment.CenterHorizontally)) {
                Text("Start Second for Result")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewScreen(){
    LifeCycleTheme {
        val cvm = CallbackViewModel()
        val ctx = LocalContext.current
        CallbackLogScreen(cvm, ctx,null)
    }
}
