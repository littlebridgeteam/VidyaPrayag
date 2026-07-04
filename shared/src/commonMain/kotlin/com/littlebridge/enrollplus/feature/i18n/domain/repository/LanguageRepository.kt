/*
 * File: LanguageRepository.kt
 * Module: feature.i18n.domain.repository
 *
 * Spec ref: MULTI_LANGUAGE_SPEC.md §10
 */
package com.littlebridge.enrollplus.feature.i18n.domain.repository

import com.littlebridge.enrollplus.core.model.ApiResponse
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.feature.i18n.domain.model.LanguagePrefResponse

interface LanguageRepository {
    suspend fun getLanguagePref(token: String): NetworkResult<ApiResponse<LanguagePrefResponse>>
    suspend fun updateLanguagePref(token: String, language: String): NetworkResult<ApiResponse<LanguagePrefResponse>>
}
