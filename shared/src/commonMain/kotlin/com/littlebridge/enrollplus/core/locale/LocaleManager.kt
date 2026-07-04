/*
 * File: LocaleManager.kt
 * Module: core.locale
 *
 * Manages the user's locale preference on the client side.
 * - Reads/writes to DataStore (via PreferenceRepository) for offline persistence
 * - Syncs to server via LanguageRepository when online
 * - Exposes currentLocale as StateFlow for Compose to react to language changes
 * - Instant language switch (no app restart needed)
 *
 * Spec ref: MULTI_LANGUAGE_SPEC.md §11.7
 */
package com.littlebridge.enrollplus.core.locale

import com.littlebridge.enrollplus.core.prefs.PreferenceRepository
import com.littlebridge.enrollplus.feature.i18n.domain.repository.LanguageRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LocaleManager(
    private val preferenceRepository: PreferenceRepository,
    private val languageRepository: LanguageRepository,
    private val networkMonitor: NetworkMonitor,
) {
    private val _currentLocale = MutableStateFlow("en")
    val currentLocale: StateFlow<String> = _currentLocale.asStateFlow()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var syncJob: Job? = null

    init {
        scope.launch {
            val saved = preferenceRepository.getLanguagePref().first()
            _currentLocale.value = saved.ifBlank { "en" }
        }
    }

    fun setLocale(lang: String) {
        _currentLocale.value = lang
        scope.launch {
            preferenceRepository.setLanguagePref(lang)
        }
        // MULTI_LANGUAGE_SPEC.md §10.9: 2-second debounce before server sync
        // so rapid language switches only fire one PATCH with the final value.
        syncJob?.cancel()
        syncJob = scope.launch {
            delay(2000)
            syncToServer(lang)
        }
    }

    fun setLocaleFromServer(lang: String) {
        if (lang.isBlank()) return
        _currentLocale.value = lang
        scope.launch {
            preferenceRepository.setLanguagePref(lang)
        }
    }

    private suspend fun syncToServer(lang: String) {
        if (!networkMonitor.isOnline()) {
            retrySync(lang)
            return
        }

        val token = preferenceRepository.getUserToken().first() ?: return
        runCatching {
            languageRepository.updateLanguagePref(token, lang)
        }.onFailure {
            retrySync(lang)
        }
    }

    private suspend fun retrySync(lang: String, maxRetries: Int = 3) {
        var attempt = 0
        while (attempt < maxRetries) {
            kotlinx.coroutines.delay(5000L * (attempt + 1))
            if (!networkMonitor.isOnline()) {
                attempt++
                continue
            }
            val token = preferenceRepository.getUserToken().first() ?: return
            val result = runCatching {
                languageRepository.updateLanguagePref(token, lang)
            }
            if (result.isSuccess) return
            attempt++
        }
    }
}
