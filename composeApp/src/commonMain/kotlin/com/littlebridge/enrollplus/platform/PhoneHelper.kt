package com.littlebridge.enrollplus.platform

/**
 * Platform-abstracted helper for launching the system dialer and SMS composer.
 *
 * On Android this uses ACTION_DIAL / ACTION_SENDTO intents.
 * On iOS this uses UIApplication.openURL with tel: / sms: schemes.
 * On JVM/Web these are no-ops (no native dialer available).
 */
interface PhoneHelper {

    /**
     * Opens the system dialer with [phoneNumber] pre-filled.
     * Does NOT place a call directly — no CALL_PHONE permission needed.
     */
    fun dialPhone(phoneNumber: String)

    /**
     * Opens the SMS / messaging composer with [phoneNumber] pre-filled.
     */
    fun sendSms(phoneNumber: String, body: String? = null)
}

/**
 * Composable-scoped factory that returns the platform-specific [PhoneHelper].
 * Must be called inside a @Composable function.
 */
@androidx.compose.runtime.Composable
expect fun rememberPhoneHelper(): PhoneHelper
