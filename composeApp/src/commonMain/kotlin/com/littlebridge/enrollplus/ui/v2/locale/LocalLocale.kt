/*
 * File: LocalLocale.kt
 * Module: ui.v2.locale
 *
 * CompositionLocal for the current locale and composable accessors for
 * AppStrings. Screens read strings via `appString("common.button_save")`
 * instead of hardcoding English text.
 *
 * Lives in :composeApp (not :shared) because it requires the Compose
 * compiler plugin for inline function expansion.
 *
 * Spec ref: MULTI_LANGUAGE_SPEC.md §11
 */
package com.littlebridge.enrollplus.ui.v2.locale

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import com.littlebridge.enrollplus.core.locale.AppStrings

val LocalLocale = compositionLocalOf<String> { "en" }

@Composable
fun appString(key: String, vararg args: Pair<String, Any?>): String {
    val locale = LocalLocale.current
    val template = AppStrings.get(key, locale)
    return if (args.isEmpty()) {
        template
    } else {
        var result = template
        args.forEach { (k, v) ->
            result = result.replace("{$k}", v?.toString() ?: "")
        }
        result
    }
}

@Composable
fun appPlural(key: String, count: Int): String {
    val locale = LocalLocale.current
    return AppStrings.getPlural(key, locale, count)
}
