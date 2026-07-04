package com.littlebridge.enrollplus.core.locale

import java.text.NumberFormat
import java.util.Locale

actual class NumberFormatter actual constructor(private val locale: String) {
    private val javaLocale = runCatching { Locale.forLanguageTag(locale) }.getOrDefault(Locale.ENGLISH)

    actual fun format(value: Int): String {
        return NumberFormat.getInstance(javaLocale).format(value)
    }

    actual fun format(value: Long): String {
        return NumberFormat.getInstance(javaLocale).format(value)
    }

    actual fun format(value: Double, maxFractionDigits: Int): String {
        val fmt = NumberFormat.getInstance(javaLocale)
        fmt.maximumFractionDigits = maxFractionDigits
        return fmt.format(value)
    }

    actual fun formatPercent(value: Double, maxFractionDigits: Int): String {
        val fmt = NumberFormat.getPercentInstance(javaLocale)
        fmt.maximumFractionDigits = maxFractionDigits
        return fmt.format(value / 100.0)
    }
}
