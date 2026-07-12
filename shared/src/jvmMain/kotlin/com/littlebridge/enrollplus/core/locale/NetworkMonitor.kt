package com.littlebridge.enrollplus.core.locale

import java.net.InetAddress

actual class NetworkMonitor actual constructor() {
    actual fun isOnline(): Boolean {
        return try {
            val address = InetAddress.getByName("8.8.8.8")
            address.isReachable(2000)
        } catch (e: Exception) {
            false
        }
    }
}
