package com.littlebridge.enrollplus.core.locale

actual class CurrencyFormatter actual constructor(private val locale: String) {
    actual fun format(amount: Double, currencyCode: String): String {
        return try {
            js("new Intl.NumberFormat('$locale', { style: 'currency', currency: '$currencyCode' }).format($amount)")
        } catch (e: Exception) {
            "₹$amount"
        }
    }

    actual fun format(amount: Long, currencyCode: String): String {
        return format(amount.toDouble(), currencyCode)
    }
}
