package com.littlebridge.enrollplus.util

actual fun currentTimeMillis(): Long = System.currentTimeMillis()

actual fun randomUUID(): String = java.util.UUID.randomUUID().toString()
