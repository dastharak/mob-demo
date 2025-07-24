package lk.ac.cmb.ucsc.dtb.senseone

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import lk.ac.cmb.ucsc.dtb.senseone.vm.MainViewModel
import lk.ac.cmb.ucsc.dtb.senseone.vm.SensorInfo

// Tag used for logging
val TAG: String = MainActivity::class.java.name

class MainActivity : ComponentActivity() {
    // Reference to ViewModel for accessing sensor data
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(.)")

        // Initialize ViewModel for the activity
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        // Set the UI content using Jetpack Compose
        setContent {
            MaterialTheme {
                SensorViewer(viewModel) // Root composable

                //Without mutable state variable and back handler
                // Dialog does not seem to launch i.e.:onBackPressed
                var showDialog by remember { mutableStateOf(false) }

                // Handle back press
                BackHandler(enabled = true) {
                    showDialog = true
                }

                ConfirmDialog(show = showDialog,
                    onConfirm = { finish() },
                    onDismiss = {
                        showDialog = false
                    }
                )

            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume()")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause()")
    }
}

@Composable
fun ConfirmDialog(
    show: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = { TextButton(onClick = onConfirm) { Text("Yes") } },
            dismissButton = { TextButton(onClick = onDismiss) { Text("No") } },
            title = { Text("Exit SensorOne ?") }
        )
    }
}

// Displays either the sensor list or a detail screen based on selection
@Composable
fun SensorViewer(viewModel: MainViewModel) {
    Log.d(TAG, "SensorViewer(.)")
    // Refers the currently selected sensor
    var selectedSensor by remember { mutableStateOf<SensorInfo?>(null) }

    if (selectedSensor == null) {
        // Show list of sensors
        // Last parameter is taken as a lambda expression and selectedSensor is updated
        Log.d(TAG, "SensorListView(..)")
        SensorListView(viewModel) { sensor ->
            selectedSensor = sensor
        }
    } else {
        // Show selected sensor details
        Log.d(TAG, "SensorDetailScreen(..)")
        SensorDetailScreen(sensorInfo = selectedSensor!!) {
            selectedSensor = null // Go back to list
        }
    }
}

// Shows a table of available sensors
@Composable
fun SensorListView(viewModel: MainViewModel, onSensorClick: (SensorInfo) -> Unit) {
    Log.d(TAG, "SensorListView(..)")
    val sensorList = viewModel.sensorList

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Table header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Name", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
            Text("Type", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
            Text("Description", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
        }

        HorizontalDivider() // Separates header from body

        // Table rows using LazyColumn for performance
        LazyColumn {
            items(sensorList) { sensorInfo ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(sensorInfo.name,
                        color = Color.Blue,
                        modifier = Modifier
                            .weight(1.5f)
                            .clickable {
                                Log.d(TAG, "onSensorClick(.)")
                                onSensorClick(sensorInfo)
                            }
                    )
                    Text(sensorInfo.type, modifier = Modifier.weight(1f))
                    Text(sensorInfo.sensor.vendor, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

// Details for one sensor
@Composable
fun SensorDetailScreen(sensorInfo: SensorInfo, onBack: () -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Sensor Details", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Name: ${sensorInfo.name}")
        Text("Type: ${sensorInfo.type}")
        Text("Description: ${sensorInfo.sensor}") // Tries to convert the object to String
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            Log.d(TAG, "onBack()")
            onBack()
        }) {
            Text("Back To List")
        }
    }
}

@Composable
fun Test(mode: String) {
    Text(
        text = "Test : $mode",
        fontSize = 24.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun UIPreview() {
    MaterialTheme {
        Test("Waiting")
    }
}
