package lk.ac.cmb.ucsc.dtb.apmbcr

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

import androidx.lifecycle.ViewModelProvider

import lk.ac.cmb.ucsc.dtb.apmbcr.ui.theme.AirPlaneModeBCastTheme
import lk.ac.cmb.ucsc.dtb.apmbcr.vm.MainViewModel

class MainActivity : ComponentActivity() {

    val TAG = MainActivity::class.java.name//"MainActivity"
    // Create a ViewModel scoped to this activity
    private lateinit var viewModel: MainViewModel

    private val receiver = APMCReceiver {
        //Here we implement the onChange() function
        viewModel.refreshAirplaneMode()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG,"onCreate(.)")
        // Initialize the ViewModel
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        /*enableEdgeToEdge()*/ // : leaving the control and notification area alone
        setContent {
            AirPlaneModeBCastTheme {
                if(viewModel.isAirplaneModeOn) {
                    AirplaneModeScreen("ON")
                }else{
                    AirplaneModeScreen("OFF")
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG,"onResume()")
        //get current APM
        viewModel.refreshAirplaneMode()
        //register the BCReceiver for APMC
        registerReceiver(receiver, IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED))

    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG,"onPause()")
        //un register the BCReceiver for APMC
        unregisterReceiver(receiver)
    }

}

@Composable
fun AirplaneModeScreen(mode: String) {
    Box(//A layout composable like a Box or a Frame
        modifier = Modifier.fillMaxSize(), //use the max size of the activity
        contentAlignment = Alignment.Center // align the child in the center
    ) {
        AirPlaneModeLabel(mode)
    }
}


@Composable
fun AirPlaneModeLabel(mode: String) {
    Text(
        text = "Air Plane Mode : $mode",
        fontSize = 24.sp,//scale-independent-pixels are good for font sizes
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)//density-independent-pixels good for layouts and dimensions
    )
}

@Preview(showBackground = true)
@Composable
fun UIPreview() {
    AirPlaneModeBCastTheme {
        AirPlaneModeLabel("Waiting")
    }
}