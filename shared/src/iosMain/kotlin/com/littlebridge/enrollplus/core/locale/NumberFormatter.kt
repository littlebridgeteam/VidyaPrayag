package com.littlebridge.enrollplus.core.locale

import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSLocale

actual class NumberFormatter actual constructor(private val locale: String) {
    private val nsLocale = NSLocale.localeWithLocaleIdentifier(locale)

    actual fun format(value: Int): String {
        val fmt = NSNumberFormatter()
        fmt.locale = nsLocale
        return fmt.stringFromNumber(value as NSNumber) ?: value.toString()
    }

    actual fun format(value: Long): String {
        val fmt = NSNumberFormatter()
        fmt.locale = nsLocale
        return fmt.stringFromNumber(value as NSNumber) ?: value.toString()
    }

    actual fun format(value: Double, maxFractionDigits: Int): String {
        val fmt = NSNumberFormatter()
        fmt.locale = nsLocale
        fmt.maximumFractionDigits = maxFractionDigits.toULong()
        return fmt.stringFromNumber(value as NSNumber) ?: value.toString()
    }

    actual fun formatPercent(value: Double, maxFractionDigits: Int): String {
        val fmt = NSNumberFormatter()
        fmt.locale = nsLocale
        fmt.numberStyle = NSNumberFormatterPercentStyle
        fmt.maximumFractionDigits = maxFractionDigits.toULong()
        return fmt.stringFromNumber((value / 100.0) as NSNumber) ?: "$value%"
    }
}
