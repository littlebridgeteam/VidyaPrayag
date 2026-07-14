package com.littlebridge.enrollplus.util

/**
 * Cross-platform analytics tracker. Android implementation uses Firebase Analytics + Crashlytics + Microsoft Clarity.
 * Other platforms are no-ops.
 */
expect object AnalyticsTracker {

    /** Log a custom event with optional params. Also fires Clarity sendCustomEvent. */
    fun event(name: String, params: Map<String, Any?> = emptyMap())

    /** Set a user property (e.g. role, school_id). */
    fun setUserProperty(key: String, value: String?)

    /** Set a Crashlytics custom key. */
    fun setCustomKey(key: String, value: String)

    /** Record a non-fatal exception to Crashlytics. */
    fun recordException(throwable: Throwable)

    /** Log a breadcrumb message to Crashlytics. */
    fun log(message: String)

    /** Set the user ID for analytics + Clarity custom user ID. */
    fun setUserId(userId: String?)

    /** Enable/disable analytics collection. */
    fun setAnalyticsCollectionEnabled(enabled: Boolean)

    /** Set the current screen name for Clarity session tracking. */
    fun setCurrentScreenName(screenName: String)

    /** Set a Clarity custom tag for session filtering. Also sets Firebase user property. */
    fun setCustomTag(key: String, value: String)

    /** Set the Clarity custom user ID for session correlation. */
    fun setCustomUserId(userId: String?)
}
