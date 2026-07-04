/*
 * File: LanguageModels.kt
 * Module: feature.i18n.domain.model
 *
 * Domain models for multi-language support.
 *
 * Spec ref: MULTI_LANGUAGE_SPEC.md §10
 */
package com.littlebridge.enrollplus.feature.i18n.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class LanguagePreference(
    val code: String,
    val nativeName: String,
    val englishName: String,
    val script: String,
)

val SUPPORTED_LANGUAGES: List<LanguagePreference> = listOf(
    LanguagePreference("en", "English", "English", "Latin"),
    LanguagePreference("hi", "हिन्दी", "Hindi", "Devanagari"),
    LanguagePreference("bn", "বাংলা", "Bengali", "Bengali"),
    LanguagePreference("ta", "தமிழ்", "Tamil", "Tamil"),
    LanguagePreference("te", "తెలుగు", "Telugu", "Telugu"),
    LanguagePreference("mr", "मराठी", "Marathi", "Devanagari"),
    LanguagePreference("gu", "ગુજરાતી", "Gujarati", "Gujarati"),
    LanguagePreference("kn", "ಕನ್ನಡ", "Kannada", "Kannada"),
    LanguagePreference("ml", "മലയാളം", "Malayalam", "Malayalam"),
    LanguagePreference("pa", "ਪੰਜਾਬੀ", "Punjabi", "Gurmukhi"),
)

val SUPPORTED_LANG_CODES: Set<String> = SUPPORTED_LANGUAGES.map { it.code }.toSet()

fun supportedLanguage(code: String): LanguagePreference =
    SUPPORTED_LANGUAGES.firstOrNull { it.code == code } ?: SUPPORTED_LANGUAGES.first()

@Serializable
data class LanguagePrefResponse(
    val language: String,
)
