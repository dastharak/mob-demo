package com.github.dastharak.lifecycle

import java.util.concurrent.atomic.AtomicInteger

// Thread-safe integer starting at 1
private val counter = AtomicInteger(1)

data class CallbackLog(
    val id: Int = counter.getAndIncrement(),//UUID.randomUUID().toString(),
    val message: String,
    val timestamp: String
)
