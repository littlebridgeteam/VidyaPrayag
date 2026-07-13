/*
 * File: LanguageRepositoryImpl.kt
 * Module: feature.i18n.data.repository
 *
 * Spec ref: MULTI_LANGUAGE_SPEC.md §10
 */
package com.littlebridge.enrollplus.feature.i18n.data.repository

import com.littlebridge.enrollplus.core.model.ApiResponse
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.feature.i18n.data.remote.LanguageApi
import com.littlebridge.enrollplus.feature.i18n.domain.model.LanguagePrefResponse
import com.littlebridge.enrollplus.feature.i18n.domain.repository.LanguageRepository

class LanguageRepositoryImpl(
    private val api: LanguageApi,
) : LanguageRepository {

    override suspend fun getLanguagePref(token: String) = api.getLanguagePref(token)

    override suspend fun updateLanguagePref(token: String, language: String) =
        api.updateLanguagePref(token, language)
}
