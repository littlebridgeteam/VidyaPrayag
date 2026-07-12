/*
 * File: NumberFormatter.kt
 * Module: core.locale
 *
 * expect/actual for locale-aware number formatting.
 *
 * Spec ref: MULTI_LANGUAGE_SPEC.md §11.6
 */
package com.littlebridge.enrollplus.core.locale

expect class NumberFormatter(locale: String) {
    fun format(value: Int): String
    fun format(value: Long): String
    fun format(value: Double, maxFractionDigits: Int = 2): String
    fun formatPercent(value: Double, maxFractionDigits: Int = 1): String
}
