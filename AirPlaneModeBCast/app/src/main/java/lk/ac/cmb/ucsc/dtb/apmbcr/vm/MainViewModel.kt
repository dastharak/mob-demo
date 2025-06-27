package lk.ac.cmb.ucsc.dtb.apmbcr.vm

import android.app.Application
import android.provider.Settings
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

/**
 * ViewModel Holds and manages UI-related data over lifecycle events
 * while keeping the data separated from the Controllers such as Activities
 * and UI generation code such as Composable functions separated
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val context = getApplication<Application>()
    // Mutable state variable to hold the current airplane mode status
    // Initialized by checking the current airplane mode at startup
    var isAirplaneModeOn by mutableStateOf(checkAirplaneMode())
        private set// Make setter private to restrict external modification

    // Public method to refresh status by accessing the system setting
    fun refreshAirplaneMode() {
        isAirplaneModeOn = checkAirplaneMode()
    }

    private fun checkAirplaneMode(): Boolean {
        return Settings.Global.getInt(context.contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0) != 0 //0 = OFF else ON
    }
}
