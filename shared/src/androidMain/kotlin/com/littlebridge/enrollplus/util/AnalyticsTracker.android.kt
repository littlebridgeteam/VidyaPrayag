package com.littlebridge.enrollplus.util

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.microsoft.clarity.Clarity

actual object AnalyticsTracker {

    private val analytics: FirebaseAnalytics? by lazy {
        runCatching { ContextHolder.appContext?.let { FirebaseAnalytics.getInstance(it) } }.getOrNull()
    }

    private val crashlytics: FirebaseCrashlytics? by lazy {
        runCatching { FirebaseCrashlytics.getInstance() }.getOrNull()
    }

    actual fun event(name: String, params: Map<String, Any?>) {
        runCatching {
            analytics?.logEvent(name) {
                params.forEach { (key, value) ->
                    when (value) {
                        is String -> param(key, value)
                        is Long -> param(key, value)
                        is Int -> param(key, value.toLong())
                        is Double -> param(key, value)
                        is Float -> param(key, value.toDouble())
                        is Boolean -> param(key, value.toString())
                        else -> {}
                    }
                }
            }
        }
        runCatching { Clarity.sendCustomEvent(name) }
    }

    actual fun setUserProperty(key: String, value: String?) {
        runCatching { analytics?.setUserProperty(key, value) }
    }

    actual fun setCustomKey(key: String, value: String) {
        runCatching { crashlytics?.setCustomKey(key, value) }
    }

    actual fun recordException(throwable: Throwable) {
        runCatching { crashlytics?.recordException(throwable) }
    }

    actual fun log(message: String) {
        runCatching { crashlytics?.log(message) }
    }

    actual fun setUserId(userId: String?) {
        runCatching {
            analytics?.setUserProperty("_user_id", userId)
            crashlytics?.setCustomKey("user_id", userId ?: "")
            if (!userId.isNullOrBlank()) Clarity.setCustomUserId(userId)
        }
    }

    actual fun setAnalyticsCollectionEnabled(enabled: Boolean) {
        runCatching {
            analytics?.setAnalyticsCollectionEnabled(enabled)
            crashlytics?.isCrashlyticsCollectionEnabled = enabled
        }
    }

    actual fun setCurrentScreenName(screenName: String) {
        runCatching { Clarity.setCurrentScreenName(screenName) }
    }

    actual fun setCustomTag(key: String, value: String) {
        runCatching {
            Clarity.setCustomTag(key, value)
            analytics?.setUserProperty(key, value)
        }
    }

    actual fun setCustomUserId(userId: String?) {
        runCatching {
            if (!userId.isNullOrBlank()) Clarity.setCustomUserId(userId)
        }
    }
}
