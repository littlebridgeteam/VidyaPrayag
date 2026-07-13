/*
 * File: DateFormatter.kt
 * Module: core.locale
 *
 * expect/actual for locale-aware date formatting.
 * All dates are displayed in IST (Asia/Kolkata) per spec.
 * Uses ISO date strings ("yyyy-MM-dd" / "yyyy-MM-dd'T'HH:mm:ss") to avoid
 * pulling in kotlinx-datetime (not in the dependency catalog).
 *
 * Spec ref: MULTI_LANGUAGE_SPEC.md §11.6
 */
package com.littlebridge.enrollplus.core.locale

expect class DateFormatter(locale: String) {
    fun formatDate(isoDate: String, pattern: String = "dd MMM yyyy"): String
    fun formatDateTime(isoDateTime: String, pattern: String = "dd MMM yyyy, HH:mm"): String
    fun formatInstant(epochMillis: Long, pattern: String = "dd MMM yyyy, HH:mm"): String
}
