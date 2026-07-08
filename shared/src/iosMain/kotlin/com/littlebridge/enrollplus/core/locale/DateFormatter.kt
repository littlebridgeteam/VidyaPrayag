package com.littlebridge.enrollplus.core.locale

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSTimeZone
import platform.Foundation.NSLocale

actual class DateFormatter actual constructor(private val locale: String) {
    private fun formatter(pattern: String): NSDateFormatter {
        val fmt = NSDateFormatter()
        fmt.dateFormat = pattern
        fmt.timeZone = NSTimeZone.timeZoneWithName("Asia/Kolkata")
        fmt.locale = NSLocale.localeWithLocaleIdentifier(locale)
        return fmt
    }

    actual fun formatDate(isoDate: String, pattern: String): String {
        return try {
            val parseFmt = NSDateFormatter()
            parseFmt.dateFormat = "yyyy-MM-dd"
            parseFmt.timeZone = NSTimeZone.timeZoneWithName("Asia/Kolkata")
            val nsDate = parseFmt.dateFromString(isoDate) ?: return isoDate
            formatter(pattern).stringFromDate(nsDate)
        } catch (e: Exception) {
            isoDate
        }
    }

    actual fun formatDateTime(isoDateTime: String, pattern: String): String {
        return try {
            val parseFmt = NSDateFormatter()
            parseFmt.dateFormat = "yyyy-MM-dd'T'HH:mm:ss"
            parseFmt.timeZone = NSTimeZone.timeZoneWithName("Asia/Kolkata")
            val nsDate = parseFmt.dateFromString(isoDateTime) ?: return isoDateTime
            formatter(pattern).stringFromDate(nsDate)
        } catch (e: Exception) {
            isoDateTime
        }
    }

    actual fun formatInstant(epochMillis: Long, pattern: String): String {
        val nsDate = NSDate.dateWithTimeIntervalSince1970(epochMillis / 1000.0)
        return formatter(pattern).stringFromDate(nsDate)
    }
}
