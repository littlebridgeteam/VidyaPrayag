package com.littlebridge.enrollplus.core.locale

import kotlinx.browser.window

actual class NetworkMonitor actual constructor() {
    actual fun isOnline(): Boolean {
        return try { window.navigator.onLine } catch (e: Exception) { true }
    }
}
