package com.littlebridge.enrollplus.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberPhoneHelper(): PhoneHelper = remember { JvmPhoneHelper() }

private class JvmPhoneHelper : PhoneHelper {
    override fun dialPhone(phoneNumber: String) {
        // No native dialer on JVM/desktop
    }

    override fun sendSms(phoneNumber: String, body: String?) {
        // No native SMS composer on JVM/desktop
    }
}
