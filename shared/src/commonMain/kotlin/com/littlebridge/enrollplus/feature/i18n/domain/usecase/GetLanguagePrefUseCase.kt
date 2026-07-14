/*
 * File: GetLanguagePrefUseCase.kt
 * Module: feature.i18n.domain.usecase
 *
 * Spec ref: MULTI_LANGUAGE_SPEC.md §10
 */
package com.littlebridge.enrollplus.feature.i18n.domain.usecase

import com.littlebridge.enrollplus.core.model.ApiResponse
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.feature.i18n.domain.model.LanguagePrefResponse
import com.littlebridge.enrollplus.feature.i18n.domain.repository.LanguageRepository

class GetLanguagePrefUseCase(
    private val languageRepository: LanguageRepository,
) {
    suspend operator fun invoke(token: String): NetworkResult<ApiResponse<LanguagePrefResponse>> {
        return languageRepository.getLanguagePref(token)
    }
}
