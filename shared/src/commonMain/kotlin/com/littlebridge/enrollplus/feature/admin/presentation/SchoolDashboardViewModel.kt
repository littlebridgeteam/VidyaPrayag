package com.littlebridge.enrollplus.feature.admin.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.core.prefs.PreferenceRepository
import com.littlebridge.enrollplus.feature.admin.domain.model.AdminDashboardActivity
import com.littlebridge.enrollplus.feature.admin.domain.model.AdminDashboardAnalytics
import com.littlebridge.enrollplus.feature.admin.domain.model.AdminDashboardOverview
import com.littlebridge.enrollplus.feature.admin.domain.model.AdminDashboardSummary
import com.littlebridge.enrollplus.feature.admin.domain.model.OnboardingStep
import com.littlebridge.enrollplus.feature.admin.domain.repository.AdminDashboardRepository
import com.littlebridge.enrollplus.feature.auth.domain.model.OnboardingStepData
import com.littlebridge.enrollplus.feature.auth.domain.model.UserDetailsData
import com.littlebridge.enrollplus.feature.auth.domain.repository.AuthRepository
import com.littlebridge.enrollplus.util.AppLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * High-level onboarding state of the logged-in school admin, as known by the
 * server. Mirrors the values the API returns under
 * `data.onboarding_details.onboarding_status`.
 */
enum class DashboardOnboardingStatus {
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETED,
    /** Returned when the response hasn't loaded yet, or didn't match a known value. */
    UNKNOWN;

    companion object {
        fun fromServer(value: String?): DashboardOnboardingStatus = when (value?.uppercase()) {
            "NOT_STARTED" -> NOT_STARTED
            "IN_PROGRESS" -> IN_PROGRESS
            "COMPLETED" -> COMPLETED
            else -> UNKNOWN
        }
    }
}

/**
 * Consolidated UI state for SchoolHomeScreenV2 (PRF-034).
 *
 * Replaces 10 individual StateFlow fields with a single StateFlow to minimize
 * recompositions — each field change now emits one new state object instead
 * of triggering N independent recompositions.
 */
data class SchoolDashboardState(
    val steps: List<OnboardingStep> = emptyList(),
    val progress: Float = 0f,
    val onboardingStatus: DashboardOnboardingStatus = DashboardOnboardingStatus.UNKNOWN,
    val adminName: String = "Admin",
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val summary: AdminDashboardSummary? = null,
    val analytics: AdminDashboardAnalytics? = null,
    val activity: AdminDashboardActivity? = null,
    val overview: AdminDashboardOverview? = null,
    val isStale: Boolean = false,
    val isOffline: Boolean = false,
)

/**
 * Drives the SchoolDashboard. Pulls `GET /api/v1/user/details` on init and
 * exposes a single consolidated [state] (PRF-034).
 *
 * The screen uses these to decide whether to show:
 *  (a) The "Welcome, Admin — let's onboard" hero with real progress + a
 *      Start/Continue button that jumps to the first PENDING step, OR
 *  (b) The "All set, your campus is live" hero (when COMPLETED).
 */
class SchoolDashboardViewModel(
    private val authRepository: AuthRepository,
    private val preferenceRepository: PreferenceRepository,
    private val dashboardRepository: AdminDashboardRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SchoolDashboardState(steps = DEFAULT_STEPS))
    val state: StateFlow<SchoolDashboardState> = _state.asStateFlow()

    init {
        refresh()
    }

    /**
     * Re-fetch everything the home screen needs:
     *   1. `/user/details`             — onboarding progress + greeting (existing)
     *   2. `/api/admin/dashboard/`    — summary, analytics, activity (new)
     *
     * Called on init and any time we want the dashboard to re-sync (screen
     * resume or post-onboarding navigation). The three dashboard reads are
     * best-effort: a failure on any one of them logs and leaves that section in
     * its previous (null/empty) state without blocking the others or surfacing a
     * blocking error — onboarding/user-details remains the source of the
     * top-level loading + error state.
     */

    fun refresh() {
        viewModelScope.launch {
            val hasData = _state.value.overview != null
            _state.update { it.copy(isLoading = !hasData, isRefreshing = hasData, errorMessage = null) }

            val token = preferenceRepository.getUserToken().first()
            if (token.isNullOrBlank()) {
                AppLogger.d("SchoolDashboardVM", "No auth token in prefs; skipping refresh")
                _state.update { it.copy(isLoading = false, isRefreshing = false) }
                return@launch
            }

            when (val result = authRepository.getUserDetails(token)) {
                is NetworkResult.Success -> {
                    applyUserDetails(result.data.data)
                    _state.update { it.copy(isStale = result.isStale, isOffline = result.isOffline) }
                }
                is NetworkResult.Error -> {
                    _state.update { it.copy(errorMessage = result.message) }
                    AppLogger.e("SchoolDashboardVM", "getUserDetails failed: ${result.message}")
                }
                is NetworkResult.ConnectionError -> {
                    _state.update { it.copy(errorMessage = "Connection error") }
                    AppLogger.e("SchoolDashboardVM", "getUserDetails connection error")
                }
            }

            loadDashboard(token)

            _state.update { it.copy(isLoading = false, isRefreshing = false) }
        }
    }

    /**
     * Best-effort fetch of the three dashboard endpoints. Each result is applied
     * independently; a failure on one does not clear the others. The admin name
     * from the summary header takes precedence over the user-details name when
     * present (it's the same person but the summary is the home's canonical
     * source).
     */
    private suspend fun loadDashboard(token: String) {
        when (val r = dashboardRepository.getOverview(token)) {
            is NetworkResult.Success -> {
                r.data.data?.let { o ->
                    _state.update { s ->
                        s.copy(
                            overview = o,
                            adminName = o.header.adminName.takeIf { it.isNotBlank() } ?: s.adminName,
                            isStale = r.isStale,
                            isOffline = r.isOffline,
                        )
                    }
                }
            }
            is NetworkResult.Error -> AppLogger.e("SchoolDashboardVM", "getOverview failed: ${r.message}")
            is NetworkResult.ConnectionError -> AppLogger.e("SchoolDashboardVM", "getOverview connection error")
        }

        when (val r = dashboardRepository.getSummary(token)) {
            is NetworkResult.Success -> {
                r.data.data?.let { s ->
                    _state.update { st ->
                        st.copy(
                            summary = s,
                            adminName = s.admin.name.takeIf { it.isNotBlank() } ?: st.adminName,
                            isStale = r.isStale,
                            isOffline = r.isOffline,
                        )
                    }
                }
            }
            is NetworkResult.Error -> AppLogger.e("SchoolDashboardVM", "getSummary failed: ${r.message}")
            is NetworkResult.ConnectionError -> AppLogger.e("SchoolDashboardVM", "getSummary connection error")
        }

        when (val r = dashboardRepository.getAnalytics(token)) {
            is NetworkResult.Success -> r.data.data?.let { a -> _state.update { it.copy(analytics = a, isStale = r.isStale, isOffline = r.isOffline) } }
            is NetworkResult.Error -> AppLogger.e("SchoolDashboardVM", "getAnalytics failed: ${r.message}")
            is NetworkResult.ConnectionError -> AppLogger.e("SchoolDashboardVM", "getAnalytics connection error")
        }

        when (val r = dashboardRepository.getActivity(token)) {
            is NetworkResult.Success -> r.data.data?.let { a -> _state.update { it.copy(activity = a, isStale = r.isStale, isOffline = r.isOffline) } }
            is NetworkResult.Error -> AppLogger.e("SchoolDashboardVM", "getActivity failed: ${r.message}")
            is NetworkResult.ConnectionError -> AppLogger.e("SchoolDashboardVM", "getActivity connection error")
        }
    }

    /**
     * The first step the user still needs to complete. Used by the
     * "Start/Continue Onboarding" button. Returns null when everything is
     * COMPLETED or the data hasn't loaded yet.
     */
    fun firstPendingStep(): OnboardingStep? =
        _state.value.steps.firstOrNull { it.status.equals(OnboardingStep.STATUS_PENDING, ignoreCase = true) }
            ?: _state.value.steps.firstOrNull { !it.status.equals(OnboardingStep.STATUS_COMPLETED, ignoreCase = true) }

    private fun applyUserDetails(data: UserDetailsData) {
        val name = data.personalDetails.name.takeIf { it.isNotBlank() } ?: "Admin"
        val ob = data.onboardingDetails
        val status = DashboardOnboardingStatus.fromServer(ob.onboardingStatus)

        val mapped = ob.listOfSteps.mapIndexed { idx, s -> s.toUiStep(idx + 1) }

        val finalSteps = if (status == DashboardOnboardingStatus.COMPLETED) {
            mapped.map { it.copy(status = OnboardingStep.STATUS_COMPLETED, isEnabled = true) }
        } else {
            mapped
        }

        _state.update {
            it.copy(
                adminName = name,
                onboardingStatus = status,
                steps = if (finalSteps.isNotEmpty()) finalSteps else DEFAULT_STEPS,
                progress = computeProgress(if (finalSteps.isNotEmpty()) finalSteps else DEFAULT_STEPS)
            )
        }
    }

    private fun computeProgress(steps: List<OnboardingStep>): Float {
        if (steps.isEmpty()) return 0f
        val completed = steps.count { it.isCompleted }
        return completed.toFloat() / steps.size
    }

    companion object {
        /** Shown until `/user/details` resolves. Same titles the server uses. */
        val DEFAULT_STEPS: List<OnboardingStep> = listOf(
            OnboardingStep(
                id = 1,
                serverKey = OnboardingStep.SERVER_KEY_BASIC,
                title = "Institutional Basics",
                description = "School name, location, and IDs.",
                status = OnboardingStep.STATUS_PENDING,
                isEnabled = true
            ),
            OnboardingStep(
                id = 2,
                serverKey = OnboardingStep.SERVER_KEY_BRANDING,
                title = "Branding & Identity",
                description = "Upload logos and color themes.",
                status = OnboardingStep.STATUS_LOCKED,
                isEnabled = false
            ),
            OnboardingStep(
                id = 3,
                serverKey = OnboardingStep.SERVER_KEY_ACADEMIC,
                title = "Academic Setup",
                description = "Classes, subjects, and teachers.",
                status = OnboardingStep.STATUS_LOCKED,
                isEnabled = false
            ),
            OnboardingStep(
                id = 4,
                serverKey = OnboardingStep.SERVER_KEY_REVIEW,
                title = "Final Launch",
                description = "Verify and go live.",
                status = OnboardingStep.STATUS_LOCKED,
                isEnabled = false
            )
        )
    }
}

/**
 * Convert the server's per-step description into the UI model. We infer the
 * canonical [OnboardingStep.serverKey] from the server's display name (the
 * server doesn't return the key directly on this endpoint).
 */
private fun OnboardingStepData.toUiStep(id: Int): OnboardingStep {
    val key = when {
        name.contains("Basic", ignoreCase = true) -> OnboardingStep.SERVER_KEY_BASIC
        name.contains("Brand", ignoreCase = true) -> OnboardingStep.SERVER_KEY_BRANDING
        name.contains("Academic", ignoreCase = true) -> OnboardingStep.SERVER_KEY_ACADEMIC
        name.contains("Launch", ignoreCase = true) ||
                name.contains("Review", ignoreCase = true) -> OnboardingStep.SERVER_KEY_REVIEW
        else -> when (id) {
            1 -> OnboardingStep.SERVER_KEY_BASIC
            2 -> OnboardingStep.SERVER_KEY_BRANDING
            3 -> OnboardingStep.SERVER_KEY_ACADEMIC
            else -> OnboardingStep.SERVER_KEY_REVIEW
        }
    }
    return OnboardingStep(
        id = id,
        serverKey = key,
        title = name,
        description = description,
        status = status.uppercase(),
        // We deliberately drop the server's `icon` URL here — the lh3
        // googleusercontent links are private and 403 on device. The screen
        // uses a Material icon fallback.
        iconUrl = null,
        isEnabled = isEnabled
    )
}
