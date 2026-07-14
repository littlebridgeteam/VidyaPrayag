package com.littlebridge.enrollplus.core.locale

actual class NumberFormatter actual constructor(private val locale: String) {
    actual fun format(value: Int): String = value.toString()

    actual fun format(value: Long): String = value.toString()

    actual fun format(value: Double, maxFractionDigits: Int): String {
        return "%.${maxFractionDigits}f".format(value)
    }

    actual fun formatPercent(value: Double, maxFractionDigits: Int): String {
        return "%.${maxFractionDigits}f%%".format(value)
    }
}
