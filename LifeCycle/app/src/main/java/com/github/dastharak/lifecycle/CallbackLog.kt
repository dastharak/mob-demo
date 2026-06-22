package com.github.dastharak.lifecycle

import java.util.concurrent.atomic.AtomicInteger

// Thread-safe integer starting at 1
private val counter = AtomicInteger(1)

data class CallbackLog(
    val id: Int = counter.getAndIncrement(),// Get the current then increment
    val message: String,
    val timestamp: String
)
