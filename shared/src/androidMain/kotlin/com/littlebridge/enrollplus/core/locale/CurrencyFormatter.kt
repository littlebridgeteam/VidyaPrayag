package com.littlebridge.enrollplus.core.locale

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

actual class CurrencyFormatter actual constructor(private val locale: String) {
    private val javaLocale = runCatching { Locale.forLanguageTag(locale) }.getOrDefault(Locale.ENGLISH)

    actual fun format(amount: Double, currencyCode: String): String {
        val fmt = NumberFormat.getCurrencyInstance(javaLocale)
        fmt.currency = Currency.getInstance(currencyCode)
        return fmt.format(amount)
    }

    actual fun format(amount: Long, currencyCode: String): String {
        return format(amount.toDouble(), currencyCode)
    }
}
