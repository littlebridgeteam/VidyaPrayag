package com.littlebridge.enrollplus.util

import java.util.Calendar
import java.util.TimeZone

private val IST = TimeZone.getTimeZone("Asia/Kolkata")

actual fun todayIso(): String {
    val cal = Calendar.getInstance(IST)
    val y = cal.get(Calendar.YEAR)
    val m = cal.get(Calendar.MONTH) + 1
    val d = cal.get(Calendar.DAY_OF_MONTH)
    return isoOf(y, m, d)
}

actual fun nowMinutesOfDay(): Int {
    val cal = Calendar.getInstance(IST)
    return cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
}
