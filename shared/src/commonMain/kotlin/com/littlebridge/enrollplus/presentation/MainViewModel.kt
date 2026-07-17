package com.littlebridge.enrollplus.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlebridge.enrollplus.core.prefs.PreferenceRepository
import com.littlebridge.enrollplus.core.network.TokenRefreshManager
import com.littlebridge.enrollplus.feature.schools.domain.usecase.GetSchoolsUseCase
import com.littlebridge.enrollplus.feature.schools.domain.model.School
import com.littlebridge.enrollplus.domain.util.UiState
import com.littlebridge.enrollplus.util.AnalyticsTracker
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AuthState(
    val role: String? = null,
    val token: String? = null,
    val isLoaded: Boolean = false
)

class MainViewModel(
    private val getSchoolsUseCase: GetSchoolsUseCase,
    private val preferenceRepository: PreferenceRepository,
    private val authRepository: com.littlebridge.enrollplus.feature.auth.domain.repository.AuthRepository,
    private val notificationService: com.littlebridge.enrollplus.feature.notification.domain.service.NotificationService,
    private val silentTokenRefreshManager: TokenRefreshManager,
) : ViewModel() {

    private val _schools = MutableStateFlow<UiState<List<School>>>(UiState.Loading)
    val schools: StateFlow<UiState<List<School>>> = _schools.asStateFlow()

    val themeName: StateFlow<String> = preferenceRepository.getThemeName()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "LIGHT")

    val themeMode: StateFlow<String> = preferenceRepository.getThemeMode()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "system")

    val customThemeId: StateFlow<String?> = preferenceRepository.getCustomThemeId()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val authState: StateFlow<AuthState> = combine(
        preferenceRepository.getUserRole(),
        preferenceRepository.getUserToken()
    ) { role, token ->
        AuthState(role, token, isLoaded = true)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AuthState())

    val userRole: StateFlow<String?> = preferenceRepository.getUserRole()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val userToken: StateFlow<String?> = preferenceRepository.getUserToken()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        refreshSchools()

        // Proactive silent token refresh — check on app start if the access
        // token is about to expire and refresh it before any API call hits a
        // 401. This eliminates the Render spin-down race condition where a
        // reactive 401 refresh fails because the server is asleep.
        viewModelScope.launch {
            userToken.collect { token ->
                if (!token.isNullOrBlank()) {
                    silentTokenRefreshManager.refreshIfNeeded()
                    notificationService.syncDeviceToken()
                }
            }
        }

        // Set Clarity session tags when auth state changes
        viewModelScope.launch {
            authState.collect { state ->
                if (state.isLoaded) {
                    val role = state.role ?: "guest"
                    AnalyticsTracker.setCustomTag("role", role)
                    if (state.token.isNullOrBlank()) {
                        AnalyticsTracker.setCustomTag("auth_status", "unauthenticated")
                    } else {
                        AnalyticsTracker.setCustomTag("auth_status", "authenticated")
                    }
                }
            }
        }

        // Set user_id and user_name tags when available
        viewModelScope.launch {
            preferenceRepository.getUserId().collect { userId ->
                if (!userId.isNullOrBlank()) {
                    AnalyticsTracker.setCustomTag("user_id", userId)
                    AnalyticsTracker.setCustomUserId(userId)
                }
            }
        }
        viewModelScope.launch {
            preferenceRepository.getUserName().collect { name ->
                if (!name.isNullOrBlank()) {
                    AnalyticsTracker.setCustomTag("user_name", name)
                }
            }
        }
    }

    fun refreshSchools() {
        viewModelScope.launch {
            _schools.value = UiState.Loading
            try {
                getSchoolsUseCase().collect { schoolList ->
                    _schools.value = UiState.Success(schoolList)
                }
            } catch (e: Exception) {
                _schools.value = UiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    /**
     * Called when the app comes to the foreground (ON_RESUME lifecycle event).
     * Triggers a proactive token refresh so the access token is fresh before
     * the user interacts with any screen. This is the key difference from
     * reactive refresh — we refresh BEFORE the token expires, not after.
     */
    fun refreshOnForeground() {
        viewModelScope.launch {
            val token = preferenceRepository.getUserToken().first()
            if (!token.isNullOrBlank()) {
                silentTokenRefreshManager.refreshIfNeeded()
            }
        }
    }

    fun setTheme(name: String) {
        viewModelScope.launch {
            preferenceRepository.setThemeName(name)
        }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            preferenceRepository.setThemeMode(mode)
        }
    }

    fun setCustomThemeId(id: String?) {
        viewModelScope.launch {
            preferenceRepository.setCustomThemeId(id)
        }
    }

    fun setRole(role: String) {
        viewModelScope.launch {
            preferenceRepository.setUserRole(role)
        }
    }

    fun logout() {
        viewModelScope.launch {
            val role = authState.value.role
            AnalyticsTracker.event("vp_auth_logout", mapOf(
                "role" to (role ?: "unknown"),
            ))
            AnalyticsTracker.setUserId(null)
            AnalyticsTracker.setCustomUserId(null)
            AnalyticsTracker.setUserProperty("role", null)
            AnalyticsTracker.setCustomKey("user_id", "")
            AnalyticsTracker.setCustomTag("role", "guest")
            AnalyticsTracker.setCustomTag("auth_status", "unauthenticated")
            AnalyticsTracker.setCustomTag("user_id", "")
            AnalyticsTracker.setCustomTag("user_name", "")
            authRepository.logout()
        }
    }
}
