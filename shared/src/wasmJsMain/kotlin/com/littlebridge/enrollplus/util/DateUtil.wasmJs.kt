package com.littlebridge.enrollplus.util

import kotlin.js.Date

private const val IST_OFFSET_MS = 5.5 * 60 * 60 * 1000 // UTC+5:30

actual fun todayIso(): String {
    val istDate = Date(Date().getTime() + IST_OFFSET_MS)
    val y = istDate.getUTCFullYear()
    val m = istDate.getUTCMonth() + 1
    val d = istDate.getUTCDate()
    return isoOf(y, m, d)
}

actual fun nowMinutesOfDay(): Int {
    val istDate = Date(Date().getTime() + IST_OFFSET_MS)
    return istDate.getUTCHours() * 60 + istDate.getUTCMinutes()
}
