package com.github.dastharak.lifecycle


import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CallbackViewModel : ViewModel() {
    private val _logs = MutableStateFlow<List<CallbackLog>>(emptyList())
    val logs: StateFlow<List<CallbackLog>> = _logs.asStateFlow()

    fun addCallbackLog(message: String) {
        val timeStamp = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Date())
        val newLog = CallbackLog(message = message, timestamp = timeStamp)

        // Append the new log to the existing list safely
        _logs.value += newLog
    }
}

