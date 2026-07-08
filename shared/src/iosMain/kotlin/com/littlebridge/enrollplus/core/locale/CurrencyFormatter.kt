package com.littlebridge.enrollplus.core.locale

import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSLocale

actual class CurrencyFormatter actual constructor(private val locale: String) {
    private fun formatter(currencyCode: String): NSNumberFormatter {
        val fmt = NSNumberFormatter()
        fmt.numberStyle = NSNumberFormatterCurrencyStyle
        fmt.currencyCode = currencyCode
        fmt.locale = NSLocale.localeWithLocaleIdentifier(locale)
        return fmt
    }

    actual fun format(amount: Double, currencyCode: String): String {
        return formatter(currencyCode).stringFromNumber(amount as NSNumber) ?: "₹$amount"
    }

    actual fun format(amount: Long, currencyCode: String): String {
        return format(amount.toDouble(), currencyCode)
    }
}
