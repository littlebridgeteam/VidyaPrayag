/*
 * File: LanguageApi.kt
 * Module: feature.i18n.data.remote
 *
 * Ktor client for language preference endpoints.
 *
 * Spec ref: MULTI_LANGUAGE_SPEC.md §9
 */
package com.littlebridge.enrollplus.feature.i18n.data.remote

import com.littlebridge.enrollplus.core.model.ApiResponse
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.core.network.safeApiCall
import com.littlebridge.enrollplus.feature.i18n.domain.model.LanguagePrefResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class LanguageApi(
    private val client: HttpClient,
    private val baseUrl: String,
) {
    private fun url(path: String): String {
        val base = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        val cleanPath = if (path.startsWith("/")) path.substring(1) else path
        return "$base$cleanPath"
    }

    suspend fun getLanguagePref(token: String): NetworkResult<ApiResponse<LanguagePrefResponse>> = safeApiCall {
        client.get(url("api/v1/user/language-pref")) {
            bearerAuth(token)
        }
    }

    suspend fun updateLanguagePref(token: String, language: String): NetworkResult<ApiResponse<LanguagePrefResponse>> = safeApiCall {
        client.patch(url("api/v1/user/language-pref")) {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(mapOf("language" to language))
        }
    }
}
