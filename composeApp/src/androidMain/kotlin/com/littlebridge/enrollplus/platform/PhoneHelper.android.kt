package com.littlebridge.enrollplus.platform

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberPhoneHelper(): PhoneHelper {
    val context = LocalContext.current
    return remember(context) { AndroidPhoneHelper(context) }
}

private class AndroidPhoneHelper(private val context: Context) : PhoneHelper {
    override fun dialPhone(phoneNumber: String) {
        val digits = phoneNumber.filter { it.isDigit() || it == '+' }
        if (digits.isBlank()) {
            Log.w("PhoneHelper", "dialPhone: phone number is blank, skipping")
            return
        }
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$digits")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        runCatching { context.startActivity(intent) }
            .onFailure { Log.e("PhoneHelper", "dialPhone: startActivity failed", it) }
    }

    override fun sendSms(phoneNumber: String, body: String?) {
        val digits = phoneNumber.filter { it.isDigit() || it == '+' }
        if (digits.isBlank()) {
            Log.w("PhoneHelper", "sendSms: phone number is blank, skipping")
            return
        }
        val uri = Uri.parse("smsto:$digits")
        val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            body?.let { putExtra("sms_body", it) }
        }
        runCatching { context.startActivity(intent) }
            .onFailure { Log.e("PhoneHelper", "sendSms: startActivity failed", it) }
    }
}
