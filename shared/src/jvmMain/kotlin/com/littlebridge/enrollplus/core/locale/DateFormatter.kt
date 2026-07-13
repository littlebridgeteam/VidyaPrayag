package com.littlebridge.enrollplus.core.locale

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

actual class DateFormatter actual constructor(private val locale: String) {
    private val javaLocale = runCatching { Locale.forLanguageTag(locale) }.getOrDefault(Locale.ENGLISH)
    private val istZone = TimeZone.getTimeZone("Asia/Kolkata")

    private fun sdf(pattern: String): SimpleDateFormat {
        return SimpleDateFormat(pattern, javaLocale).apply { timeZone = istZone }
    }

    actual fun formatDate(isoDate: String, pattern: String): String {
        return try {
            val parsed = java.time.LocalDate.parse(isoDate)
            sdf(pattern).format(java.util.Date.from(
                parsed.atStartOfDay(java.time.ZoneId.of("Asia/Kolkata")).toInstant()
            ))
        } catch (e: Exception) {
            isoDate
        }
    }

    actual fun formatDateTime(isoDateTime: String, pattern: String): String {
        return try {
            val parsed = java.time.LocalDateTime.parse(isoDateTime)
            sdf(pattern).format(java.util.Date.from(
                parsed.atZone(java.time.ZoneId.of("Asia/Kolkata")).toInstant()
            ))
        } catch (e: Exception) {
            isoDateTime
        }
    }

    actual fun formatInstant(epochMillis: Long, pattern: String): String {
        return sdf(pattern).format(java.util.Date(epochMillis))
    }
}
