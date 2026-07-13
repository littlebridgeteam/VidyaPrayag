package com.littlebridge.enrollplus.util

actual object AnalyticsTracker {
    actual fun event(name: String, params: Map<String, Any?>) {}
    actual fun setUserProperty(key: String, value: String?) {}
    actual fun setCustomKey(key: String, value: String) {}
    actual fun recordException(throwable: Throwable) {}
    actual fun log(message: String) {}
    actual fun setUserId(userId: String?) {}
    actual fun setAnalyticsCollectionEnabled(enabled: Boolean) {}
    actual fun setCurrentScreenName(screenName: String) {}
    actual fun setCustomTag(key: String, value: String) {}
    actual fun setCustomUserId(userId: String?) {}
}
