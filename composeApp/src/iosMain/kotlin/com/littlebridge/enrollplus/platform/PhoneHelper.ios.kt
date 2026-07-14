package com.littlebridge.enrollplus.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenURLOptionsKey

@Composable
actual fun rememberPhoneHelper(): PhoneHelper = remember { IosPhoneHelper() }

private class IosPhoneHelper : PhoneHelper {
    override fun dialPhone(phoneNumber: String) {
        val digits = phoneNumber.filter { it.isDigit() || it == '+' }
        if (digits.isBlank()) return
        val url = NSURL.URLWithString("tel:$digits") ?: return
        UIApplication.sharedApplication.openURL(url, options = emptyMap<UIApplicationOpenURLOptionsKey, Any>(), completionHandler = null)
    }

    override fun sendSms(phoneNumber: String, body: String?) {
        val digits = phoneNumber.filter { it.isDigit() || it == '+' }
        if (digits.isBlank()) return
        val urlString = if (body != null) {
            "sms:$digits&body=${body}"
        } else {
            "sms:$digits"
        }
        val url = NSURL.URLWithString(urlString) ?: return
        UIApplication.sharedApplication.openURL(url, options = emptyMap<UIApplicationOpenURLOptionsKey, Any>(), completionHandler = null)
    }
}
