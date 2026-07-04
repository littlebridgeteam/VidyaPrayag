package com.littlebridge.enrollplus.core.locale

actual class NumberFormatter actual constructor(private val locale: String) {
    actual fun format(value: Int): String {
        return try {
            js("new Intl.NumberFormat('$locale').format($value)")
        } catch (e: Exception) {
            value.toString()
        }
    }

    actual fun format(value: Long): String {
        return try {
            js("new Intl.NumberFormat('$locale').format($value)")
        } catch (e: Exception) {
            value.toString()
        }
    }

    actual fun format(value: Double, maxFractionDigits: Int): String {
        return try {
            js("new Intl.NumberFormat('$locale', { maximumFractionDigits: $maxFractionDigits }).format($value)")
        } catch (e: Exception) {
            value.toString()
        }
    }

    actual fun formatPercent(value: Double, maxFractionDigits: Int): String {
        return try {
            js("new Intl.NumberFormat('$locale', { style: 'percent', maximumFractionDigits: $maxFractionDigits }).format(${value / 100.0})")
        } catch (e: Exception) {
            "$value%"
        }
    }
}
