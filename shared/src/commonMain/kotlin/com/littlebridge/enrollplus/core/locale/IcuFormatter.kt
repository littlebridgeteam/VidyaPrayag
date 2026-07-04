/*
 * File: IcuFormatter.kt
 * Module: core.locale
 *
 * expect/actual for ICU MessageFormat pluralization.
 * Platform-specific implementations handle the actual formatting.
 *
 * Spec ref: MULTI_LANGUAGE_SPEC.md §11.4
 */
package com.littlebridge.enrollplus.core.locale

expect fun icuFormat(pattern: String, locale: String, vararg args: Pair<String, Any?>): String
