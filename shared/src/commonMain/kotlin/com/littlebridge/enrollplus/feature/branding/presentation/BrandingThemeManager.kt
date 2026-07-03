package com.littlebridge.enrollplus.feature.branding.presentation

import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.core.prefs.PreferenceRepository
import com.littlebridge.enrollplus.feature.branding.domain.model.SchoolBranding
import com.littlebridge.enrollplus.feature.branding.domain.repository.BrandingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json

/**
 * App-lifecycle singleton that holds the current school's branding.
 *
 * Fetched once after login; observed by [NavGraphV2] to override the
 * active theme's accent colors with the school's brand palette.
 *
 * Branding is also persisted to [PreferenceRepository] so that on the next
 * app launch the splash and login screens can show the school's brand
 * immediately (before authentication completes).
 *
 * Call [loadBranding] after authentication succeeds.
 * Call [loadCached] on app startup (before auth) to restore the last session's branding.
 * Call [clear] on logout.
 */
class BrandingThemeManager(
    private val repository: BrandingRepository,
    private val prefs: PreferenceRepository,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = false }

    private val _branding = MutableStateFlow<SchoolBranding?>(null)
    val branding: StateFlow<SchoolBranding?> = _branding.asStateFlow()

    /**
     * Load branding from the persisted cache (non-network).
     * Call on app startup so splash/login screens can use the school's brand.
     */
    fun loadCached() {
        scope.launch {
            val cached = prefs.getCachedBranding().first()
            if (cached != null) {
                val parsed = runCatching { json.decodeFromString<SchoolBranding>(cached) }.getOrNull()
                if (parsed != null && parsed.isCustomized) {
                    _branding.value = parsed
                }
            }
        }
    }

    fun loadBranding() {
        scope.launch {
            val token = prefs.getUserToken().first() ?: return@launch
            when (val result = repository.getBranding(token)) {
                is NetworkResult.Success -> {
                    val branding = result.data.data
                    if (branding != null && branding.isCustomized) {
                        _branding.value = branding
                        // Persist for next app launch (pre-auth branding)
                        runCatching {
                            prefs.setCachedBranding(json.encodeToString(SchoolBranding.serializer(), branding))
                        }
                    } else {
                        _branding.value = null
                        runCatching { prefs.setCachedBranding(null) }
                    }
                }
                is NetworkResult.Error,
                is NetworkResult.ConnectionError -> {
                    // Silently fail — app falls back to default theme
                }
            }
        }
    }

    fun clear() {
        _branding.value = null
        scope.launch {
            runCatching { prefs.setCachedBranding(null) }
        }
    }
}
