package com.littlebridge.enrollplus.platform

import androidx.compose.runtime.Composable

@Composable
actual fun rememberShareHelper(): ShareHelper = object : ShareHelper {
    override fun shareText(text: String, subject: String?) {
        // JVM desktop — no native share sheet. No-op stub.
    }
}
