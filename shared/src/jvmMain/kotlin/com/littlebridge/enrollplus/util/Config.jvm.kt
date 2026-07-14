package com.littlebridge.enrollplus.util

actual object Config {
    private val fallback = "https://vidyaprayag-1.onrender.com"
    private val resolvedUrl: String =
        System.getProperty("devBaseUrl")
            ?: System.getenv("DEV_BASE_URL")
            ?: fallback
    actual val authBaseUrl: String = resolvedUrl
    actual val schoolBaseUrl: String = resolvedUrl
    actual val isDev: Boolean = resolvedUrl != fallback
}
