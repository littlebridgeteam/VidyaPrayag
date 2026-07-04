package com.littlebridge.enrollplus.core.locale

actual class CurrencyFormatter actual constructor(private val locale: String) {
    actual fun format(amount: Double, currencyCode: String): String {
        val symbol = if (currencyCode == "INR") "₹" else currencyCode
        return "$symbol${"%.2f".format(amount)}"
    }

    actual fun format(amount: Long, currencyCode: String): String {
        return format(amount.toDouble(), currencyCode)
    }
}
