/*
 * File: NetworkMonitor.kt
 * Module: core.locale
 *
 * expect/actual for checking network connectivity.
 * Used by LocaleManager to retry server sync when offline.
 *
 * Spec ref: MULTI_LANGUAGE_SPEC.md §11.7
 */
package com.littlebridge.enrollplus.core.locale

expect class NetworkMonitor() {
    fun isOnline(): Boolean
}
