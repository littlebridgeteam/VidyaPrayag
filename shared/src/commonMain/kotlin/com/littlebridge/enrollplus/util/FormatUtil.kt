package com.littlebridge.enrollplus.util

import kotlin.math.pow
import kotlin.math.round

/**
 * Formats a Double with a fixed number of decimal places.
 * Common-compatible replacement for "%.Nf".format(value).
 */
fun formatDecimal(value: Double, decimals: Int = 2): String {
    if (decimals == 0) return round(value).toLong().toString()
    val multiplier = 10.0.pow(decimals)
    val scaled = round(value * multiplier) / multiplier
    val intPart = scaled.toLong()
    val fracPart = round((scaled - intPart) * multiplier).toLong()
    val fracStr = fracPart.toString().padStart(decimals, '0')
    return "$intPart.$fracStr"
}
