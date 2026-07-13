/*
 * File: CurrencyFormatter.kt
 * Module: core.locale
 *
 * expect/actual for locale-aware currency formatting (INR).
 *
 * Spec ref: MULTI_LANGUAGE_SPEC.md §11.6
 */
package com.littlebridge.enrollplus.core.locale

expect class CurrencyFormatter(locale: String) {
    fun format(amount: Double, currencyCode: String = "INR"): String
    fun format(amount: Long, currencyCode: String = "INR"): String
}
