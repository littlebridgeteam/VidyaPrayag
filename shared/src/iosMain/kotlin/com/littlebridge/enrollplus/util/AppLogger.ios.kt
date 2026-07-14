package com.littlebridge.enrollplus.util

import platform.Foundation.NSLog

actual object AppLogger {
    actual fun d(tag: String, message: String) {
        NSLog("DEBUG: [$tag] $message")
    }

    actual fun i(tag: String, message: String) {
        NSLog("INFO: [$tag] $message")
    }

    actual fun w(tag: String, message: String, throwable: Throwable?) {
        NSLog("WARN: [$tag] $message - ${throwable?.message}")
    }

    actual fun e(tag: String, message: String, throwable: Throwable?) {
        NSLog("ERROR: [$tag] $message - ${throwable?.message}")
    }
}
