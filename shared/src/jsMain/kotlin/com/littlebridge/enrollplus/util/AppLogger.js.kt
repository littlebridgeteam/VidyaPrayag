package com.littlebridge.enrollplus.util

actual object AppLogger {
    actual fun d(tag: String, message: String) {
        console.log("DEBUG: [$tag] $message")
    }

    actual fun i(tag: String, message: String) {
        console.log("INFO: [$tag] $message")
    }

    actual fun w(tag: String, message: String, throwable: Throwable?) {
        console.warn("WARN: [$tag] $message", throwable)
    }

    actual fun e(tag: String, message: String, throwable: Throwable?) {
        console.error("ERROR: [$tag] $message", throwable)
    }
}
