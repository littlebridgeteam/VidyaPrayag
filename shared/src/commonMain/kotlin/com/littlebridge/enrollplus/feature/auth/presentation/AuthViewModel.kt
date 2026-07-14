package com.littlebridge.enrollplus.feature.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.feature.auth.domain.model.AuthResponse
import com.littlebridge.enrollplus.feature.auth.domain.model.LoginRequest
import com.littlebridge.enrollplus.feature.auth.domain.model.SchoolRegisterRequest
import com.littlebridge.enrollplus.feature.auth.domain.model.SignupRequest
import com.littlebridge.enrollplus.feature.auth.domain.repository.AuthRepository
import com.littlebridge.enrollplus.util.AnalyticsTracker
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val otpSent: Boolean = false,
    val otpIdentifier: String = "",
    val otpPurpose: String = "",
    val error: String? = null,
    val authResponse: AuthResponse? = null,
    val resendCountdown: Int = 0,
    val canResend: Boolean = false,
)

class AuthViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    private var countdownJob: Job? = null

    // ── Parent Login: phone → OTP → login ──

    fun sendOtpForLogin(phone: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val normalized = normalizePhone(phone)
            AnalyticsTracker.event("vp_auth_login_started", mapOf(
                "auth_method" to "otp",
                "role" to "parent",
            ))
            when (val result = authRepository.sendOtp(normalized, "login")) {
                is NetworkResult.Success -> {
                    AnalyticsTracker.event("vp_auth_otp_requested", mapOf(
                        "phone_masked" to normalized.takeLast(4),
                        "purpose" to "login",
                    ))
                    _state.value = _state.value.copy(
                        isLoading = false,
                        otpSent = true,
                        otpIdentifier = normalized,
                        otpPurpose = "login",
                        canResend = false,
                        resendCountdown = 30,
                    )
                    startResendCountdown()
                }
                is NetworkResult.Error -> {
                    AnalyticsTracker.event("vp_auth_otp_request_failed", mapOf(
                        "purpose" to "login",
                        "error_reason" to (result.message ?: "unknown"),
                    ))
                    _state.value = _state.value.copy(
                    isLoading = false,
                    error = result.message,
                )}
                is NetworkResult.ConnectionError -> {
                    AnalyticsTracker.event("vp_auth_otp_request_failed", mapOf(
                        "purpose" to "login",
                        "error_reason" to "no_connection",
                    ))
                    _state.value = _state.value.copy(
                    isLoading = false,
                    error = "No internet connection. Please check your network.",
                )}
            }
        }
    }

    fun verifyAndLoginParent(code: String) {
        val currentState = _state.value
        val phone = currentState.otpIdentifier
        if (phone.isBlank()) {
            _state.value = _state.value.copy(error = "Phone number missing. Please request OTP again.")
            return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = authRepository.login(
                LoginRequest(
                    identifier = phone,
                    otp = code,
                    role = "parent",
                )
            )) {
                is NetworkResult.Success -> {
                    val resp = result.data
                    AnalyticsTracker.setUserId(resp.userId)
                    AnalyticsTracker.setUserProperty("role", resp.role)
                    AnalyticsTracker.setCustomKey("role", resp.role)
                    AnalyticsTracker.setCustomKey("user_id", resp.userId)
                    AnalyticsTracker.setCustomTag("role", resp.role)
                    AnalyticsTracker.setCustomTag("user_id", resp.userId)
                    AnalyticsTracker.setCustomTag("auth_status", "authenticated")
                    AnalyticsTracker.event("vp_auth_otp_verified", mapOf(
                        "phone_masked" to phone.takeLast(4),
                    ))
                    AnalyticsTracker.event("vp_auth_login_success", mapOf(
                        "role" to resp.role,
                        "auth_method" to "otp",
                    ))
                    _state.value = _state.value.copy(
                        isLoading = false,
                        authResponse = resp,
                    )
                }
                is NetworkResult.Error -> {
                    AnalyticsTracker.event("vp_auth_otp_failed", mapOf(
                        "phone_masked" to phone.takeLast(4),
                        "error_reason" to (result.message ?: "unknown"),
                    ))
                    AnalyticsTracker.event("vp_auth_login_failed", mapOf(
                        "error_reason" to (result.message ?: "unknown"),
                        "auth_method" to "otp",
                    ))
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message,
                    )
                }
                is NetworkResult.ConnectionError -> {
                    AnalyticsTracker.event("vp_auth_login_failed", mapOf(
                        "error_reason" to "no_connection",
                        "auth_method" to "otp",
                    ))
                    _state.value = _state.value.copy(
                    isLoading = false,
                    error = "No internet connection. Please check your network.",
                )}
            }
        }
    }

    // ── Parent Signup: name+phone → OTP → signup ──

    fun sendOtpForSignup(phone: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val normalized = normalizePhone(phone)
            AnalyticsTracker.event("vp_auth_signup_started", mapOf(
                "auth_method" to "otp",
                "role" to "parent",
            ))
            when (val result = authRepository.sendOtp(normalized, "signup")) {
                is NetworkResult.Success -> {
                    AnalyticsTracker.event("vp_auth_otp_requested", mapOf(
                        "phone_masked" to normalized.takeLast(4),
                        "purpose" to "signup",
                    ))
                    _state.value = _state.value.copy(
                        isLoading = false,
                        otpSent = true,
                        otpIdentifier = normalized,
                        otpPurpose = "signup",
                        canResend = false,
                        resendCountdown = 30,
                    )
                    startResendCountdown()
                }
                is NetworkResult.Error -> {
                    AnalyticsTracker.event("vp_auth_otp_request_failed", mapOf(
                        "purpose" to "signup",
                        "error_reason" to (result.message ?: "unknown"),
                    ))
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message,
                    )
                }
                is NetworkResult.ConnectionError -> {
                    AnalyticsTracker.event("vp_auth_otp_request_failed", mapOf(
                        "purpose" to "signup",
                        "error_reason" to "no_connection",
                    ))
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "No internet connection. Please check your network.",
                    )
                }
            }
        }
    }

    fun verifyAndSignupParent(name: String, code: String) {
        val currentState = _state.value
        val phone = currentState.otpIdentifier
        if (phone.isBlank()) {
            _state.value = _state.value.copy(error = "Phone number missing. Please request OTP again.")
            return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = authRepository.signup(
                SignupRequest(
                    name = name,
                    identifier = phone,
                    otp = code,
                    role = "parent",
                )
            )) {
                is NetworkResult.Success -> {
                    val resp = result.data
                    AnalyticsTracker.setUserId(resp.userId)
                    AnalyticsTracker.setUserProperty("role", resp.role)
                    AnalyticsTracker.setCustomKey("role", resp.role)
                    AnalyticsTracker.setCustomKey("user_id", resp.userId)
                    AnalyticsTracker.setCustomTag("role", resp.role)
                    AnalyticsTracker.setCustomTag("user_id", resp.userId)
                    AnalyticsTracker.setCustomTag("auth_status", "authenticated")
                    AnalyticsTracker.event("vp_auth_otp_verified", mapOf(
                        "phone_masked" to phone.takeLast(4),
                    ))
                    AnalyticsTracker.event("vp_auth_signup_success", mapOf(
                        "role" to resp.role,
                    ))
                    _state.value = _state.value.copy(
                        isLoading = false,
                        authResponse = resp,
                    )
                }
                is NetworkResult.Error -> {
                    AnalyticsTracker.event("vp_auth_otp_failed", mapOf(
                        "phone_masked" to phone.takeLast(4),
                        "error_reason" to (result.message ?: "unknown"),
                    ))
                    AnalyticsTracker.event("vp_auth_signup_failed", mapOf(
                        "error_reason" to (result.message ?: "unknown"),
                    ))
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message,
                    )
                }
                is NetworkResult.ConnectionError -> {
                    AnalyticsTracker.event("vp_auth_signup_failed", mapOf(
                        "error_reason" to "no_connection",
                    ))
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "No internet connection. Please check your network.",
                    )
                }
            }
        }
    }

    // ── Staff Login: email+password → login ──

    fun loginStaff(email: String, password: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val normalized = email.trim().lowercase()
            AnalyticsTracker.event("vp_auth_login_started", mapOf(
                "auth_method" to "password",
                "role" to "admin",
            ))
            when (val result = authRepository.login(
                LoginRequest(
                    identifier = normalized,
                    password = password,
                    role = "admin",
                )
            )) {
                is NetworkResult.Success -> {
                    val resp = result.data
                    AnalyticsTracker.setUserId(resp.userId)
                    AnalyticsTracker.setUserProperty("role", resp.role)
                    AnalyticsTracker.setCustomKey("role", resp.role)
                    AnalyticsTracker.setCustomKey("user_id", resp.userId)
                    AnalyticsTracker.setCustomTag("role", resp.role)
                    AnalyticsTracker.setCustomTag("user_id", resp.userId)
                    AnalyticsTracker.setCustomTag("auth_status", "authenticated")
                    AnalyticsTracker.event("vp_auth_login_success", mapOf(
                        "role" to resp.role,
                        "auth_method" to "password",
                    ))
                    _state.value = _state.value.copy(
                        isLoading = false,
                        authResponse = resp,
                    )
                }
                is NetworkResult.Error -> {
                    AnalyticsTracker.event("vp_auth_login_failed", mapOf(
                        "error_reason" to (result.message ?: "unknown"),
                        "auth_method" to "password",
                    ))
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message,
                    )
                }
                is NetworkResult.ConnectionError -> {
                    AnalyticsTracker.event("vp_auth_login_failed", mapOf(
                        "error_reason" to "no_connection",
                        "auth_method" to "password",
                    ))
                    _state.value = _state.value.copy(
                    isLoading = false,
                    error = "No internet connection. Please check your network.",
                )}
            }
        }
    }

    // ── Staff Signup: 3-step form → registerSchool ──

    fun registerSchool(
        adminName: String,
        email: String,
        password: String,
        schoolName: String,
        board: String?,
        schoolType: String?,
        city: String?,
        state: String?,
        contactPhone: String?,
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            AnalyticsTracker.event("vp_auth_signup_started", mapOf(
                "auth_method" to "password",
                "role" to "admin",
            ))
            when (val result = authRepository.registerSchool(
                SchoolRegisterRequest(
                    name = adminName.trim(),
                    identifier = email.trim().lowercase(),
                    password = password,
                    schoolName = schoolName.trim(),
                    board = board?.takeIf { it.isNotBlank() },
                    schoolType = schoolType?.takeIf { it.isNotBlank() },
                    city = city?.takeIf { it.isNotBlank() },
                    state = state?.takeIf { it.isNotBlank() },
                    contactPhone = contactPhone?.takeIf { it.isNotBlank() },
                )
            )) {
                is NetworkResult.Success -> {
                    val resp = result.data
                    AnalyticsTracker.setUserId(resp.userId)
                    AnalyticsTracker.setUserProperty("role", resp.role)
                    AnalyticsTracker.setCustomKey("role", resp.role)
                    AnalyticsTracker.setCustomKey("user_id", resp.userId)
                    AnalyticsTracker.setCustomTag("role", resp.role)
                    AnalyticsTracker.setCustomTag("user_id", resp.userId)
                    AnalyticsTracker.setCustomTag("auth_status", "authenticated")
                    AnalyticsTracker.event("vp_auth_signup_success", mapOf(
                        "role" to resp.role,
                    ))
                    _state.value = _state.value.copy(
                        isLoading = false,
                        authResponse = resp,
                    )
                }
                is NetworkResult.Error -> {
                    AnalyticsTracker.event("vp_auth_signup_failed", mapOf(
                        "error_reason" to (result.message ?: "unknown"),
                    ))
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message,
                    )
                }
                is NetworkResult.ConnectionError -> {
                    AnalyticsTracker.event("vp_auth_signup_failed", mapOf(
                        "error_reason" to "no_connection",
                    ))
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "No internet connection. Please check your network.",
                    )
                }
            }
        }
    }

    // ── Resend OTP ──

    fun resendOtp() {
        val currentState = _state.value
        if (!currentState.canResend || currentState.otpIdentifier.isBlank()) return
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = authRepository.sendOtp(
                currentState.otpIdentifier,
                currentState.otpPurpose.ifBlank { "login" }
            )) {
                is NetworkResult.Success -> {
                    AnalyticsTracker.event("vp_auth_otp_resent", mapOf(
                        "phone_masked" to currentState.otpIdentifier.takeLast(4),
                    ))
                    _state.value = _state.value.copy(
                        isLoading = false,
                        canResend = false,
                        resendCountdown = 30,
                    )
                    startResendCountdown()
                }
                is NetworkResult.Error -> _state.value = _state.value.copy(
                    isLoading = false,
                    error = result.message,
                )
                is NetworkResult.ConnectionError -> _state.value = _state.value.copy(
                    isLoading = false,
                    error = "No internet connection. Please check your network.",
                )
            }
        }
    }

    // ── Utility ──

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    fun resetOtpState() {
        countdownJob?.cancel()
        _state.value = _state.value.copy(
            otpSent = false,
            otpIdentifier = "",
            otpPurpose = "",
            resendCountdown = 0,
            canResend = false,
            error = null,
        )
    }

    fun resetAll() {
        countdownJob?.cancel()
        _state.value = AuthUiState()
    }

    private fun startResendCountdown() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            for (i in 30 downTo 1) {
                _state.value = _state.value.copy(resendCountdown = i, canResend = false)
                delay(1000)
            }
            _state.value = _state.value.copy(resendCountdown = 0, canResend = true)
        }
    }

    private fun normalizePhone(raw: String): String {
        val digits = raw.trim().replace("\\s|-".toRegex(), "")
        return when {
            digits.startsWith("+") -> digits
            digits.length == 10 && digits.all { it.isDigit() } -> "+91$digits"
            digits.length == 12 && digits.startsWith("91") -> "+$digits"
            else -> digits
        }
    }
}
