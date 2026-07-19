package com.littlebridge.enrollplus.feature.admin.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.core.prefs.PreferenceRepository
import com.littlebridge.enrollplus.feature.admin.domain.model.ObPayloadKeys
import com.littlebridge.enrollplus.feature.admin.domain.model.ObStepType
import com.littlebridge.enrollplus.feature.admin.domain.model.OnboardingSubmitRequest
import com.littlebridge.enrollplus.feature.admin.domain.repository.OnboardingRepository
import com.littlebridge.enrollplus.feature.auth.domain.model.AuthResponse
import com.littlebridge.enrollplus.feature.auth.domain.repository.AuthRepository
import com.littlebridge.enrollplus.util.AppLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * ViewModel for the active school registration and workspace activation flow.
 * It owns the single four-step registration journey and its success handoff:
 *
 *   Step 1: Basic Details (admin name, role, school name, board, type) — pre-auth
 *   Step 2: Create Password — pre-auth, calls registerSchool()
 *   Step 3: School Identity (contact, address, principal) — post-auth, calls submitStep(BASIC)
 *   Step 4: Academic Year (label, dates, working days, timings) — post-auth, calls submitStep(ACADEMIC)
 *   Success: calls completeOnboarding()
 *
 * Steps 1-2 are unauthenticated and use [AuthViewModel.registerSchool] via
 * [AuthRepository]. Steps 3-4 are authenticated and use [OnboardingRepository].
 */
class RegistrationOnboardingViewModel(
    private val authRepository: AuthRepository,
    private val onboardingRepository: OnboardingRepository,
    private val preferenceRepository: PreferenceRepository,
) : ViewModel() {

    enum class FlowStep { One, Two, Three, Four, Success }

    data class State(
        val step: FlowStep = FlowStep.One,
        val isLoading: Boolean = false,
        val error: String? = null,
        val isAccountCreated: Boolean = false,
        // Held in-memory after createAccount — NOT persisted to preferences until
        // completeOnboarding, so the app-level auth observer doesn't rip the user
        // out of this flow mid-registration.
        val pendingAuth: AuthResponse? = null,
        // Step 1 fields
        val adminName: String = "",
        val adminRole: String = "",
        val schoolName: String = "",
        val board: String = "",
        val customBoard: String = "",
        val schoolType: String = "",
        // Step 2 fields
        val email: String = "",
        val password: String = "",
        val confirmPassword: String = "",
        // Step 3 fields
        val shortName: String = "",
        val affiliationNumber: String = "",
        val medium: String = "English",
        val schoolGender: String = "co_ed",
        val contactEmail: String = "",
        val contactPhone: String = "",
        val principalName: String = "",
        val principalPhone: String = "",
        val city: String = "",
        val district: String = "",
        val state: String = "",
        val pincode: String = "",
        val fullAddress: String = "",
        // Step 4 fields
        val academicYearLabel: String = "",
        val yearStartDate: String = "",
        val yearEndDate: String = "",
        val workingDays: String = "",
        val schoolStartTime: String = "",
        val schoolEndTime: String = "",
        val periodsPerDay: String = "",
    )

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    fun update(update: (State) -> State) {
        _state.value = update(_state.value)
    }

    fun goToStep(step: FlowStep) {
        _state.value = _state.value.copy(step = step, error = null)
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    fun goBack() {
        val s = _state.value
        when (s.step) {
            FlowStep.One -> {}
            FlowStep.Two -> _state.value = s.copy(step = FlowStep.One, error = null)
            FlowStep.Three -> _state.value = s.copy(step = FlowStep.Two, error = null)
            FlowStep.Four -> _state.value = s.copy(step = FlowStep.Three, error = null)
            FlowStep.Success -> {}
        }
    }

    /**
     * Step 1: Basic Details — just advances to Step 2 (no API call).
     */
    fun submitBasicDetails(onSuccess: () -> Unit) {
        _state.value = _state.value.copy(step = FlowStep.Two, error = null)
        onSuccess()
    }

    /**
     * Step 2: Create Account — calls registerSchool with Step 1 data.
     * On success, advances to Step 3.
     */
    fun createAccount(onSuccess: () -> Unit) {
        val s = _state.value
        if (s.isLoading) return

        _state.value = s.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                val result = authRepository.registerSchoolWithoutSession(
                    com.littlebridge.enrollplus.feature.auth.domain.model.SchoolRegisterRequest(
                        name = s.adminName.trim(),
                        identifier = s.email.trim().lowercase(),
                        password = s.password,
                        adminRole = s.adminRole.takeIf { it.isNotBlank() },
                        schoolName = s.schoolName.takeIf { it.isNotBlank() },
                        board = if (s.board == "Other") s.customBoard.trim().takeIf { it.isNotBlank() } ?: "Other" else s.board.takeIf { it.isNotBlank() },
                        schoolType = s.schoolType.takeIf { it.isNotBlank() },
                    )
                )
                when (result) {
                    is NetworkResult.Success -> {
                        AppLogger.d("RegOB", "Account created, advancing to Step 3")
                        // Persist session immediately so the user can resume
                        // onboarding if the app closes before all steps complete.
                        // The NavGraphV2 onboarding gate will route them back to
                        // the registration flow since profile_completed=false.
                        authRepository.saveSession(result.data)
                        _state.value = _state.value.copy(
                            isLoading = false,
                            isAccountCreated = true,
                            pendingAuth = result.data,
                            step = FlowStep.Three,
                            error = null,
                        )
                        onSuccess()
                    }
                    is NetworkResult.Error -> {
                        AppLogger.e("RegOB", "Create account failed: ${result.message}")
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = result.message,
                        )
                    }
                    is NetworkResult.ConnectionError -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = "No internet connection. Please check your network.",
                        )
                    }
                }
            } catch (e: Exception) {
                AppLogger.e("RegOB", "Create account exception: ${e.message}")
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "An unexpected error occurred. Please try again.",
                )
            }
        }
    }

    /**
     * Step 3: Submit School Identity — calls onboarding/submit with BASIC step.
     * On success, advances to Step 4.
     */
    fun submitSchoolIdentity(onSuccess: () -> Unit) {
        val s = _state.value
        if (s.isLoading) return

        _state.value = s.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                val token = _state.value.pendingAuth?.token
                if (token.isNullOrBlank()) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "Session expired. Please log in again.",
                    )
                    return@launch
                }

                val payload = JsonObject(buildMap {
                    put(ObPayloadKeys.SCHOOL_NAME, JsonPrimitive(s.schoolName.trim()))
                    s.shortName.takeIf { it.isNotBlank() }?.let {
                        put(ObPayloadKeys.SHORT_NAME, JsonPrimitive(it.trim()))
                    }
                    put(ObPayloadKeys.BOARD, JsonPrimitive(if (s.board == "Other") s.customBoard.trim().ifBlank { "Other" } else s.board.trim()))
                    s.schoolType.takeIf { it.isNotBlank() }?.let {
                        put(ObPayloadKeys.SCHOOL_TYPE, JsonPrimitive(it.trim()))
                    }
                    s.affiliationNumber.takeIf { it.isNotBlank() }?.let {
                        put(ObPayloadKeys.AFFILIATION_NUMBER, JsonPrimitive(it.trim()))
                    }
                    put(ObPayloadKeys.MEDIUM, JsonPrimitive(s.medium.trim()))
                    put(ObPayloadKeys.SCHOOL_GENDER, JsonPrimitive(s.schoolGender.trim()))
                    s.contactEmail.takeIf { it.isNotBlank() }?.let {
                        put(ObPayloadKeys.CONTACT_EMAIL, JsonPrimitive(it.trim()))
                    }
                    s.contactPhone.takeIf { it.isNotBlank() }?.let {
                        put(ObPayloadKeys.CONTACT_PHONE, JsonPrimitive(it.trim()))
                    }
                    s.principalName.takeIf { it.isNotBlank() }?.let {
                        put(ObPayloadKeys.PRINCIPAL_NAME, JsonPrimitive(it.trim()))
                    }
                    s.principalPhone.takeIf { it.isNotBlank() }?.let {
                        put(ObPayloadKeys.PRINCIPAL_PHONE, JsonPrimitive(it.trim()))
                    }
                    s.city.takeIf { it.isNotBlank() }?.let {
                        put(ObPayloadKeys.CITY, JsonPrimitive(it.trim()))
                    }
                    s.district.takeIf { it.isNotBlank() }?.let {
                        put(ObPayloadKeys.DISTRICT, JsonPrimitive(it.trim()))
                    }
                    s.state.takeIf { it.isNotBlank() }?.let {
                        put(ObPayloadKeys.STATE, JsonPrimitive(it.trim()))
                    }
                    s.pincode.takeIf { it.isNotBlank() }?.let {
                        put(ObPayloadKeys.PINCODE, JsonPrimitive(it.trim()))
                    }
                    s.fullAddress.takeIf { it.isNotBlank() }?.let {
                        put(ObPayloadKeys.FULL_ADDRESS, JsonPrimitive(it.trim()))
                    }
                })

                val request = OnboardingSubmitRequest(
                    obStepType = ObStepType.BASIC,
                    isFinalSubmission = false,
                    dataPayload = payload,
                )

                when (val result = onboardingRepository.submitStep(token, request)) {
                    is NetworkResult.Success -> {
                        AppLogger.d("RegOB", "BASIC step submitted. nextStep=${result.data.nextStep}")
                        _state.value = _state.value.copy(
                            isLoading = false,
                            step = FlowStep.Four,
                            error = null,
                        )
                        onSuccess()
                    }
                    is NetworkResult.Error -> {
                        AppLogger.e("RegOB", "BASIC submit failed: ${result.message}")
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = if (result.code == 401) {
                                "Connection issue. Please try again."
                            } else {
                                result.message
                            },
                        )
                    }
                    is NetworkResult.ConnectionError -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = "No internet connection. Please check your network.",
                        )
                    }
                }
            } catch (e: Exception) {
                AppLogger.e("RegOB", "BASIC submit exception: ${e.message}")
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "An unexpected error occurred. Please try again.",
                )
            }
        }
    }

    /**
     * Step 4: Submit Academic Year — calls onboarding/submit with ACADEMIC step.
     * On success, advances to Success screen.
     */
    fun submitAcademicYear(onSuccess: () -> Unit) {
        val s = _state.value
        if (s.isLoading) return

        _state.value = s.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                val token = _state.value.pendingAuth?.token
                if (token.isNullOrBlank()) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "Session expired. Please log in again.",
                    )
                    return@launch
                }

                val payload = JsonObject(buildMap {
                    s.academicYearLabel.takeIf { it.isNotBlank() }?.let {
                        put(ObPayloadKeys.ACADEMIC_YEAR_LABEL, JsonPrimitive(it.trim()))
                    }
                    s.yearStartDate.takeIf { it.isNotBlank() }?.let {
                        put(ObPayloadKeys.ACADEMIC_YEAR_START_DATE, JsonPrimitive(it.trim()))
                    }
                    s.yearEndDate.takeIf { it.isNotBlank() }?.let {
                        put(ObPayloadKeys.ACADEMIC_YEAR_END_DATE, JsonPrimitive(it.trim()))
                    }
                    s.workingDays.takeIf { it.isNotBlank() }?.let {
                        put(ObPayloadKeys.WORKING_DAYS, JsonPrimitive(it.trim()))
                    }
                    s.schoolStartTime.takeIf { it.isNotBlank() }?.let {
                        put(ObPayloadKeys.SCHOOL_START_TIME, JsonPrimitive(it.trim()))
                    }
                    s.schoolEndTime.takeIf { it.isNotBlank() }?.let {
                        put(ObPayloadKeys.SCHOOL_END_TIME, JsonPrimitive(it.trim()))
                    }
                    s.periodsPerDay.takeIf { it.isNotBlank() }?.let {
                        put(ObPayloadKeys.PERIODS_PER_DAY, JsonPrimitive(it.toIntOrNull() ?: 0))
                    }
                })

                val request = OnboardingSubmitRequest(
                    obStepType = ObStepType.ACADEMIC,
                    isFinalSubmission = false,
                    dataPayload = payload,
                )

                when (val result = onboardingRepository.submitStep(token, request)) {
                    is NetworkResult.Success -> {
                        AppLogger.d("RegOB", "ACADEMIC step submitted. nextStep=${result.data.nextStep}")
                        val auth = _state.value.pendingAuth
                        if (auth == null || auth.token.isBlank()) {
                            _state.value = _state.value.copy(
                                isLoading = false,
                                error = "Session expired. Please log in again.",
                            )
                            return@launch
                        }
                        when (val completeResult = onboardingRepository.completeOnboarding(auth.token)) {
                            is NetworkResult.Success -> {
                                AppLogger.d("RegOB", "Onboarding completed: ${completeResult.data.schoolId}")
                                authRepository.saveSession(auth)
                                _state.value = _state.value.copy(
                                    isLoading = false,
                                    step = FlowStep.Success,
                                    error = null,
                                )
                                onSuccess()
                            }
                            is NetworkResult.Error -> {
                                AppLogger.e("RegOB", "Complete failed: ${completeResult.message}")
                                _state.value = _state.value.copy(
                                    isLoading = false,
                                    error = completeResult.message,
                                )
                            }
                            is NetworkResult.ConnectionError -> {
                                _state.value = _state.value.copy(
                                    isLoading = false,
                                    error = "No internet connection. Please check your network.",
                                )
                            }
                        }
                    }
                    is NetworkResult.Error -> {
                        AppLogger.e("RegOB", "ACADEMIC submit failed: ${result.message}")
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = if (result.code == 401) {
                                "Connection issue. Please try again."
                            } else {
                                result.message
                            },
                        )
                    }
                    is NetworkResult.ConnectionError -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = "No internet connection. Please check your network.",
                        )
                    }
                }
            } catch (e: Exception) {
                AppLogger.e("RegOB", "ACADEMIC submit exception: ${e.message}")
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "An unexpected error occurred. Please try again.",
                )
            }
        }
    }

    /**
     * Success screen: Complete onboarding — calls /onboarding/complete to stamp
     * onboarded_at and flip profile_completed=true.
     */
    fun completeOnboarding(onComplete: () -> Unit) {
        val s = _state.value
        if (s.isLoading) return

        _state.value = s.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                val auth = s.pendingAuth
                if (auth == null || auth.token.isBlank()) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "Session expired. Please log in again.",
                    )
                    return@launch
                }

                when (val result = onboardingRepository.completeOnboarding(auth.token)) {
                    is NetworkResult.Success -> {
                        AppLogger.d("RegOB", "Onboarding completed: ${result.data.schoolId}")
                        authRepository.saveSession(auth)
                        _state.value = _state.value.copy(isLoading = false, error = null)
                        onComplete()
                    }
                    is NetworkResult.Error -> {
                        AppLogger.e("RegOB", "Complete failed: ${result.message}")
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = result.message,
                        )
                    }
                    is NetworkResult.ConnectionError -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = "No internet connection. Please check your network.",
                        )
                    }
                }
            } catch (e: Exception) {
                AppLogger.e("RegOB", "Complete onboarding exception: ${e.message}")
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "An unexpected error occurred. Please try again.",
                )
            }
        }
    }
}
