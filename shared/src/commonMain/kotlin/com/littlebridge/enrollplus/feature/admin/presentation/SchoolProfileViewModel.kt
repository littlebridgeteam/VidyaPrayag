/*
 * File: SchoolProfileViewModel.kt
 * Module: feature.admin.presentation
 *
 * RA-47: drives the editable institutional-profile screen. Loads the schools
 * row via GET /api/v1/school/profile, lets the admin edit the core fields, and
 * persists via PUT /api/v1/school/profile (server enforces school-admin + JWT
 * school scoping). Three states (loading / error+retry / loaded) so the screen
 * is never a bare happy-path form.
 */
package com.littlebridge.enrollplus.feature.admin.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.core.prefs.PreferenceRepository
import com.littlebridge.enrollplus.feature.admin.domain.model.UpdateSchoolProfileRequest
import com.littlebridge.enrollplus.feature.admin.domain.repository.SchoolProfileRepository
import com.littlebridge.enrollplus.util.AppLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class SchoolProfileState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val loadError: String? = null,    // fatal load failure → show retry
    val errorMessage: String? = null, // transient save error → toast
    val infoMessage: String? = null,
    val saved: Boolean = false,

    // Editable fields
    val name: String = "",
    val board: String = "",
    val customBoard: String = "",
    val medium: String = "",
    val schoolGender: String = "",
    val contactPhone: String = "",
    val contactEmail: String = "",
    val principalName: String = "",
    val principalPhone: String = "",
    val principalEmail: String = "",
    val fullAddress: String = "",
    val city: String = "",
    val district: String = "",
    val state: String = "",
    val pincode: String = "",
    val logoUrl: String = "",
    val brandColor: String = "#2563EB",
    val isStale: Boolean = false,
    val isOffline: Boolean = false,
    val fieldErrors: Map<String, String> = emptyMap(),
)

class SchoolProfileViewModel(
    private val repository: SchoolProfileRepository,
    private val preferenceRepository: PreferenceRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SchoolProfileState())
    val state: StateFlow<SchoolProfileState> = _state.asStateFlow()

    init { load() }

    fun load() {
        if (_state.value.isLoading) return
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, loadError = null)
            val token = preferenceRepository.getUserToken().first()
            if (token.isNullOrBlank()) {
                _state.value = _state.value.copy(isLoading = false, loadError = "You are not signed in. Please log in again.")
                return@launch
            }
            when (val r = repository.getProfile(token)) {
                is NetworkResult.Success -> {
                    val d = r.data.data
                    if (d == null) {
                        _state.value = _state.value.copy(isLoading = false, loadError = "School profile not found.")
                        return@launch
                    }
                    val knownBoards = listOf("CBSE", "ICSE", "UP State")
                    val loadedBoard = d.board
                    val isKnownBoard = loadedBoard in knownBoards
                    _state.value = _state.value.copy(
                        isLoading = false,
                        loadError = null,
                        name = d.name,
                        board = if (isKnownBoard) loadedBoard else if (loadedBoard.isNotBlank()) "Other" else "",
                        customBoard = if (!isKnownBoard) loadedBoard else "",
                        medium = d.medium,
                        schoolGender = d.schoolGender,
                        contactPhone = d.contactPhone.orEmpty(),
                        contactEmail = d.contactEmail.orEmpty(),
                        principalName = d.principalName.orEmpty(),
                        principalPhone = d.principalPhone.orEmpty(),
                        principalEmail = d.principalEmail.orEmpty(),
                        fullAddress = d.fullAddress.orEmpty(),
                        city = d.city,
                        district = d.district,
                        state = d.state,
                        pincode = d.pincode.orEmpty(),
                        logoUrl = d.logoUrl.orEmpty(),
                        brandColor = d.brandColor,
                        isStale = r.isStale,
                        isOffline = r.isOffline,
                    )
                }
                is NetworkResult.Error -> {
                    AppLogger.e("SchoolProfileVM", "getProfile error: ${r.message}")
                    _state.value = _state.value.copy(isLoading = false, loadError = r.message)
                }
                is NetworkResult.ConnectionError -> {
                    _state.value = _state.value.copy(isLoading = false, loadError = "Connection error. Check your internet.")
                }
            }
        }
    }

    // -------- field editors (clear field-specific errors on edit) --------
    fun onName(v: String) { _state.value = _state.value.copy(name = v, fieldErrors = _state.value.fieldErrors - "name") }
    fun onBoard(v: String) { _state.value = _state.value.copy(board = v, customBoard = if (v != "Other") "" else _state.value.customBoard, fieldErrors = _state.value.fieldErrors - "board") }
    fun onCustomBoard(v: String) { _state.value = _state.value.copy(customBoard = v, fieldErrors = _state.value.fieldErrors - "customBoard") }
    fun onMedium(v: String) { _state.value = _state.value.copy(medium = v, fieldErrors = _state.value.fieldErrors - "medium") }
    fun onSchoolGender(v: String) { _state.value = _state.value.copy(schoolGender = v, fieldErrors = _state.value.fieldErrors - "schoolGender") }
    fun onContactPhone(v: String) { _state.value = _state.value.copy(contactPhone = v, fieldErrors = _state.value.fieldErrors - "contactPhone") }
    fun onContactEmail(v: String) { _state.value = _state.value.copy(contactEmail = v, fieldErrors = _state.value.fieldErrors - "contactEmail") }
    fun onPrincipalName(v: String) { _state.value = _state.value.copy(principalName = v, fieldErrors = _state.value.fieldErrors - "principalName") }
    fun onPrincipalPhone(v: String) { _state.value = _state.value.copy(principalPhone = v, fieldErrors = _state.value.fieldErrors - "principalPhone") }
    fun onPrincipalEmail(v: String) { _state.value = _state.value.copy(principalEmail = v, fieldErrors = _state.value.fieldErrors - "principalEmail") }
    fun onFullAddress(v: String) { _state.value = _state.value.copy(fullAddress = v, fieldErrors = _state.value.fieldErrors - "fullAddress") }
    fun onCity(v: String) { _state.value = _state.value.copy(city = v, fieldErrors = _state.value.fieldErrors - "city") }
    fun onDistrict(v: String) { _state.value = _state.value.copy(district = v, fieldErrors = _state.value.fieldErrors - "district") }
    fun onState(v: String) { _state.value = _state.value.copy(state = v, fieldErrors = _state.value.fieldErrors - "state") }
    fun onPincode(v: String) { _state.value = _state.value.copy(pincode = v, fieldErrors = _state.value.fieldErrors - "pincode") }

    fun clearMessages() {
        _state.value = _state.value.copy(errorMessage = null, infoMessage = null, saved = false, fieldErrors = emptyMap())
    }

    fun save() {
        val s = _state.value
        val errors = validateSchoolProfileFields(s)

        if (errors.isNotEmpty()) {
            _state.value = s.copy(fieldErrors = errors, errorMessage = "Please fix the highlighted fields.")
            return
        }
        viewModelScope.launch {
            val token = preferenceRepository.getUserToken().first()
            if (token.isNullOrBlank()) {
                _state.value = _state.value.copy(errorMessage = "You are not signed in. Please log in again.")
                return@launch
            }
            _state.value = _state.value.copy(isSaving = true, errorMessage = null, infoMessage = null, saved = false)
            val req = UpdateSchoolProfileRequest(
                name = s.name.trim(),
                board = if (s.board == "Other") s.customBoard.trim().ifBlank { "Other" } else s.board.trim(),
                medium = s.medium.trim(),
                schoolGender = s.schoolGender.trim(),
                contactPhone = s.contactPhone.trim(),
                contactEmail = s.contactEmail.trim(),
                principalName = s.principalName.trim(),
                principalPhone = s.principalPhone.trim(),
                principalEmail = s.principalEmail.trim(),
                fullAddress = s.fullAddress.trim(),
                city = s.city.trim(),
                district = s.district.trim(),
                state = s.state.trim(),
                pincode = s.pincode.trim()
            )
            when (val r = repository.updateProfile(token, req)) {
                is NetworkResult.Success -> {
                    _state.value = _state.value.copy(isSaving = false, infoMessage = "Institutional profile updated", saved = true)
                }
                is NetworkResult.Error -> {
                    AppLogger.e("SchoolProfileVM", "updateProfile error: ${r.message}")
                    _state.value = _state.value.copy(isSaving = false, errorMessage = r.message)
                }
                is NetworkResult.ConnectionError -> {
                    _state.value = _state.value.copy(isSaving = false, errorMessage = "Connection error. Check your internet.")
                }
            }
        }
    }
}

public val PHONE_REGEX_10 = Regex("^\\d{10}$")
public val EMAIL_REGEX_PROFILE = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
public val PINCODE_REGEX_6 = Regex("^\\d{6}$")

public val CITY_PINCODE_PREFIX: Map<String, String> = mapOf(
    "New Delhi" to "110",
    "Mumbai" to "400",
    "Pune" to "411",
    "Bangalore" to "560",
    "Chennai" to "600",
    "Kolkata" to "700",
    "Hyderabad" to "500",
    "Ahmedabad" to "380",
    "Jaipur" to "302",
    "Lucknow" to "226",
    "Kanpur" to "208",
    "Varanasi" to "221",
    "Meerut" to "250",
    "Noida" to "201",
    "Ghaziabad" to "201",
    "Gurugram" to "122",
)

public fun validateSchoolProfileFields(s: SchoolProfileState): Map<String, String> {
    val errors = mutableMapOf<String, String>()
    if (s.name.isBlank()) errors["name"] = "School name is required"
    if (s.board == "Other" && s.customBoard.isBlank())
        errors["customBoard"] = "Board name is required when 'Other' is selected"
    if (s.city.isBlank()) errors["city"] = "City is required"
    if (s.district.isBlank()) errors["district"] = "District is required"
    if (s.pincode.isBlank()) errors["pincode"] = "PIN code is required"

    if (s.contactPhone.isNotBlank() && !s.contactPhone.matches(PHONE_REGEX_10))
        errors["contactPhone"] = "Phone must be exactly 10 digits"
    if (s.principalPhone.isNotBlank() && !s.principalPhone.matches(PHONE_REGEX_10))
        errors["principalPhone"] = "Phone must be exactly 10 digits"
    if (s.pincode.isNotBlank() && !s.pincode.matches(PINCODE_REGEX_6))
        errors["pincode"] = "PIN must be exactly 6 digits"
    if (s.contactEmail.isNotBlank() && !s.contactEmail.matches(EMAIL_REGEX_PROFILE))
        errors["contactEmail"] = "Invalid email format"
    if (s.principalEmail.isNotBlank() && !s.principalEmail.matches(EMAIL_REGEX_PROFILE))
        errors["principalEmail"] = "Invalid email format"
    if (s.pincode.isNotBlank() && s.pincode.matches(PINCODE_REGEX_6)) {
        val expectedPrefix = CITY_PINCODE_PREFIX[s.city]
        if (expectedPrefix != null && !s.pincode.startsWith(expectedPrefix))
            errors["pincode"] = "PIN code does not match ${s.city}. Expected prefix: $expectedPrefix"
    }
    return errors
}
