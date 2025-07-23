package lk.ac.cmb.ucsc.dtb.senseone.vm

import android.app.Application
import android.hardware.Sensor
import android.hardware.SensorManager
import android.util.Log
import androidx.lifecycle.AndroidViewModel


val TAG: String = MainViewModel::class.java.name

data class SensorInfo(val name: String, val type: String, val sensor: Sensor)

/**
 * ViewModel Holds and manages UI-related data over lifecycle events
 * while keeping the data separated from the Controllers such as Activities
 * and UI generation code such as Composable functions separated
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val context = getApplication<Application>()

    val sensorList = mutableListOf<SensorInfo>()

    init {
        loadSensors()
    }

    /*
    * While it is possible to call the loadSensors at every listing it is unnecessary here.
    * We will only call it once per application ans save the sensor objects
    * */
    private fun loadSensors() {
        val sensorManager = context.getSystemService(SensorManager::class.java)
        sensorManager?.getSensorList(Sensor.TYPE_ALL)?.forEach {
            Log.d(TAG, "Name:" + it.name + "| Type:" + it.type + "| String:" + it.toString())
            sensorList.add(SensorInfo(it.name, sensorTypeName(it.type), it))
        }
    }

    private fun sensorTypeName(type: Int): String = when (type) {
        Sensor.TYPE_ACCELEROMETER -> "Accelerometer"
        Sensor.TYPE_AMBIENT_TEMPERATURE -> "Temperature"
        Sensor.TYPE_GYROSCOPE -> "Gyroscope"
        Sensor.TYPE_LIGHT -> "Light"
        Sensor.TYPE_MAGNETIC_FIELD -> "Magnetic Field"
        Sensor.TYPE_PRESSURE -> "Pressure"
        Sensor.TYPE_PROXIMITY -> "Proximity"
        Sensor.TYPE_GRAVITY -> "Gravity"
        Sensor.TYPE_ROTATION_VECTOR -> "Rotation Vector"
        Sensor.TYPE_GAME_ROTATION_VECTOR -> "Game Rotation Vector"
        Sensor.TYPE_RELATIVE_HUMIDITY -> "Relative Humidity"
        Sensor.TYPE_MOTION_DETECT -> "Motion Detect"
        else -> "Other ($type)"
    }
}
