package com.littlebridge.enrollplus.core.locale

actual class DateFormatter actual constructor(private val locale: String) {
    private val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

    actual fun formatDate(isoDate: String, pattern: String): String {
        return try {
            val parts = isoDate.split("-")
            if (parts.size < 3) return isoDate
            val day = parts[2].padStart(2, '0')
            val monthIdx = parts[1].toIntOrNull()?.minus(1) ?: return isoDate
            val year = parts[0]
            "$day ${monthNames.getOrElse(monthIdx) { "Jan" }} $year"
        } catch (e: Exception) {
            isoDate
        }
    }

    actual fun formatDateTime(isoDateTime: String, pattern: String): String {
        return try {
            val datePart = isoDateTime.substringBefore("T").ifBlank { isoDateTime }
            val timePart = isoDateTime.substringAfter("T", "").substringBefore(".")
            val dateStr = formatDate(datePart, pattern)
            if (timePart.isBlank()) dateStr else {
                val h = timePart.substringBefore(":").padStart(2, '0')
                val m = timePart.substringAfter(":").take(2).padStart(2, '0')
                "$dateStr, $h:$m"
            }
        } catch (e: Exception) {
            isoDateTime
        }
    }

    actual fun formatInstant(epochMillis: Long, pattern: String): String {
        return epochMillis.toString()
    }
}
