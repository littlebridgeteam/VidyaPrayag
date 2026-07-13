package com.littlebridge.enrollplus.core.locale

actual class DateFormatter actual constructor(private val locale: String) {
    actual fun formatDate(isoDate: String, pattern: String): String {
        return try {
            val jsDate = js("new Date(${'$'}{isoDate}T00:00:00+05:30)")
            jsDate.toLocaleDateString(locale, js("{ timeZone: 'Asia/Kolkata' }"))
        } catch (e: Exception) {
            isoDate
        }
    }

    actual fun formatDateTime(isoDateTime: String, pattern: String): String {
        return try {
            val jsDate = js("new Date(${'$'}{isoDateTime}+05:30)")
            jsDate.toLocaleString(locale, js("{ timeZone: 'Asia/Kolkata' }"))
        } catch (e: Exception) {
            isoDateTime
        }
    }

    actual fun formatInstant(epochMillis: Long, pattern: String): String {
        return try {
            val jsDate = js("new Date($epochMillis)")
            jsDate.toLocaleString(locale, js("{ timeZone: 'Asia/Kolkata' }"))
        } catch (e: Exception) {
            epochMillis.toString()
        }
    }
}
