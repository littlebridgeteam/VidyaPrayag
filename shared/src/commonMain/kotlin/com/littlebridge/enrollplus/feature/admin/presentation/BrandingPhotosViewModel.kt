package com.littlebridge.enrollplus.feature.admin.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.core.prefs.PreferenceRepository
import com.littlebridge.enrollplus.feature.admin.data.remote.MediaApi
import com.littlebridge.enrollplus.feature.admin.domain.model.GalleryRequest
import com.littlebridge.enrollplus.feature.admin.domain.model.PickedMedia
import com.littlebridge.enrollplus.feature.admin.domain.model.UpdateSchoolProfileRequest
import com.littlebridge.enrollplus.feature.admin.domain.repository.SchoolProfileRepository
import com.littlebridge.enrollplus.feature.admin.domain.repository.UserProfileRepository
import com.littlebridge.enrollplus.feature.auth.domain.repository.AuthRepository
import com.littlebridge.enrollplus.util.AppLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class GalleryPhoto(
    val id: String,
    val url: String,
)

data class BrandingPhotosState(
    val adminName: String = "",
    val adminProfilePicUrl: String = "",
    val schoolName: String = "",
    val schoolLogoUrl: String = "",
    val coverImageUrl: String = "",
    val galleryPhotos: List<GalleryPhoto> = emptyList(),
    val storageUsedHuman: String = "0 B",
    val totalStorageHuman: String = "10 GB",
    val uploadingSlot: String? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null,
)

class BrandingPhotosViewModel(
    private val authRepository: AuthRepository,
    private val schoolProfileRepository: SchoolProfileRepository,
    private val userProfileRepository: UserProfileRepository,
    private val mediaApi: MediaApi,
    private val preferenceRepository: PreferenceRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(BrandingPhotosState())
    val state: StateFlow<BrandingPhotosState> = _state.asStateFlow()

    init { load() }

    fun load() {
        if (_state.value.isLoading) return
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null, infoMessage = null)
            val token = preferenceRepository.getUserToken().first()
            if (token.isNullOrBlank()) {
                _state.value = _state.value.copy(isLoading = false)
                return@launch
            }

            val userDetailsResult = authRepository.getUserDetails(token)
            val schoolResult = schoolProfileRepository.getProfile(token)
            val profileResult = userProfileRepository.getProfile(token)

            val adminName = when (userDetailsResult) {
                is NetworkResult.Success -> userDetailsResult.data.data?.personalDetails?.name.orEmpty()
                else -> _state.value.adminName
            }
            val adminPic = when (userDetailsResult) {
                is NetworkResult.Success -> userDetailsResult.data.data?.personalDetails?.profilePic.orEmpty()
                else -> _state.value.adminProfilePicUrl
            }

            val schoolData = when (schoolResult) {
                is NetworkResult.Success -> schoolResult.data.data
                else -> null
            }

            val galleryData = when (profileResult) {
                is NetworkResult.Success -> profileResult.data.data?.gallery
                else -> null
            }

            if (schoolResult is NetworkResult.Error || profileResult is NetworkResult.Error || userDetailsResult is NetworkResult.Error) {
                val msg = listOfNotNull(
                    (userDetailsResult as? NetworkResult.Error)?.message,
                    (schoolResult as? NetworkResult.Error)?.message,
                    (profileResult as? NetworkResult.Error)?.message
                ).firstOrNull() ?: "Failed to load branding data"
                AppLogger.e("BrandingPhotosVM", "load error: $msg")
                _state.value = _state.value.copy(isLoading = false, errorMessage = msg)
                return@launch
            }

            if (schoolResult is NetworkResult.ConnectionError || profileResult is NetworkResult.ConnectionError || userDetailsResult is NetworkResult.ConnectionError) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Connection error. Check your internet."
                )
                return@launch
            }

            val photos = galleryData?.images.orEmpty().mapIndexed { idx, url ->
                GalleryPhoto(id = (idx + 1).toString(), url = url)
            }

            _state.value = _state.value.copy(
                isLoading = false,
                adminName = adminName,
                adminProfilePicUrl = adminPic,
                schoolName = schoolData?.name.orEmpty(),
                schoolLogoUrl = schoolData?.logoUrl.orEmpty(),
                coverImageUrl = schoolData?.coverImageUrl.orEmpty(),
                galleryPhotos = photos,
                storageUsedHuman = galleryData?.storageUsed ?: "0 B",
                totalStorageHuman = galleryData?.totalStorage ?: "10 GB",
            )
        }
    }

    fun uploadAdminPhoto(picked: PickedMedia) {
        uploadToSlot(
            slot = "admin",
            kind = "PROFILE",
            fileName = picked.fileName,
            bytes = picked.bytes,
            mimeType = picked.mimeType,
            onUrl = { url ->
                viewModelScope.launch {
                    _state.value = _state.value.copy(isSaving = true)
                    when (val r = authRepository.updateProfilePic(url)) {
                        is NetworkResult.Success -> {
                            _state.value = _state.value.copy(
                                isSaving = false,
                                adminProfilePicUrl = url,
                                infoMessage = "Profile picture updated"
                            )
                        }
                        is NetworkResult.Error -> {
                            _state.value = _state.value.copy(isSaving = false, errorMessage = r.message)
                        }
                        is NetworkResult.ConnectionError -> {
                            _state.value = _state.value.copy(
                                isSaving = false,
                                errorMessage = "Connection error. Check your internet."
                            )
                        }
                    }
                }
            }
        )
    }

    fun uploadLogo(picked: PickedMedia) {
        uploadToSlot(
            slot = "logo",
            kind = "LOGO",
            fileName = picked.fileName,
            bytes = picked.bytes,
            mimeType = picked.mimeType,
            onUrl = { url ->
                updateSchoolProfile(
                    request = UpdateSchoolProfileRequest(logoUrl = url),
                    onSuccess = { _state.value.copy(schoolLogoUrl = url, infoMessage = "School logo updated") }
                )
            }
        )
    }

    fun uploadCover(picked: PickedMedia) {
        uploadToSlot(
            slot = "cover",
            kind = "IMAGE",
            fileName = picked.fileName,
            bytes = picked.bytes,
            mimeType = picked.mimeType,
            onUrl = { url ->
                updateSchoolProfile(
                    request = UpdateSchoolProfileRequest(coverImageUrl = url),
                    onSuccess = { _state.value.copy(coverImageUrl = url, infoMessage = "Campus cover updated") }
                )
            }
        )
    }

    fun uploadGalleryPhoto(picked: PickedMedia) {
        uploadToSlot(
            slot = "gallery",
            kind = "IMAGE",
            fileName = picked.fileName,
            bytes = picked.bytes,
            mimeType = picked.mimeType,
            onUrl = { url ->
                val current = _state.value.galleryPhotos.map { it.url }
                saveGallery(current + url, info = "Gallery photo added")
            }
        )
    }

    fun deleteGalleryPhoto(url: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true, errorMessage = null)
            val token = preferenceRepository.getUserToken().first()
            if (token.isNullOrBlank()) {
                _state.value = _state.value.copy(isSaving = false)
                return@launch
            }
            when (val r = mediaApi.deleteMedia(token, url)) {
                is NetworkResult.Success -> {
                    val remaining = _state.value.galleryPhotos.map { it.url } - url
                    saveGallery(remaining, info = "Photo removed")
                }
                is NetworkResult.Error -> {
                    _state.value = _state.value.copy(isSaving = false, errorMessage = r.message)
                }
                is NetworkResult.ConnectionError -> {
                    _state.value = _state.value.copy(
                        isSaving = false,
                        errorMessage = "Connection error. Check your internet."
                    )
                }
            }
        }
    }

    fun clearMessages() {
        _state.value = _state.value.copy(errorMessage = null, infoMessage = null)
    }

    private fun uploadToSlot(
        slot: String,
        kind: String,
        fileName: String,
        bytes: ByteArray,
        mimeType: String,
        onUrl: (String) -> Unit,
    ) {
        if (_state.value.uploadingSlot != null) return
        viewModelScope.launch {
            _state.value = _state.value.copy(uploadingSlot = slot, errorMessage = null)
            val token = preferenceRepository.getUserToken().first()
            if (token.isNullOrBlank()) {
                _state.value = _state.value.copy(uploadingSlot = null)
                return@launch
            }
            when (val r = mediaApi.uploadMedia(token, bytes, fileName, mimeType, kind)) {
                is NetworkResult.Success -> {
                    val url = r.data.data?.url
                    _state.value = _state.value.copy(uploadingSlot = null)
                    if (url.isNullOrBlank()) {
                        _state.value = _state.value.copy(errorMessage = "Upload succeeded but no URL was returned.")
                    } else {
                        onUrl(url)
                    }
                }
                is NetworkResult.Error -> {
                    _state.value = _state.value.copy(
                        uploadingSlot = null,
                        errorMessage = if (r.code == 503) {
                            "Media storage isn't configured on the server yet. " +
                                "Set SUPABASE_URL and SUPABASE_SERVICE_KEY, then try again."
                        } else r.message
                    )
                }
                is NetworkResult.ConnectionError -> {
                    _state.value = _state.value.copy(
                        uploadingSlot = null,
                        errorMessage = "No internet connection. Please try again."
                    )
                }
            }
        }
    }

    private fun updateSchoolProfile(
        request: UpdateSchoolProfileRequest,
        onSuccess: (BrandingPhotosState) -> BrandingPhotosState,
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true)
            val token = preferenceRepository.getUserToken().first()
            if (token.isNullOrBlank()) {
                _state.value = _state.value.copy(isSaving = false)
                return@launch
            }
            when (val r = schoolProfileRepository.updateProfile(token, request)) {
                is NetworkResult.Success -> {
                    _state.value = onSuccess(_state.value.copy(isSaving = false))
                }
                is NetworkResult.Error -> {
                    _state.value = _state.value.copy(isSaving = false, errorMessage = r.message)
                }
                is NetworkResult.ConnectionError -> {
                    _state.value = _state.value.copy(
                        isSaving = false,
                        errorMessage = "Connection error. Check your internet."
                    )
                }
            }
        }
    }

    private fun saveGallery(urls: List<String>, info: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true)
            val token = preferenceRepository.getUserToken().first()
            if (token.isNullOrBlank()) {
                _state.value = _state.value.copy(isSaving = false)
                return@launch
            }
            when (val r = userProfileRepository.updateGallery(token, GalleryRequest(urls))) {
                is NetworkResult.Success -> {
                    _state.value = _state.value.copy(
                        isSaving = false,
                        galleryPhotos = urls.mapIndexed { idx, url -> GalleryPhoto(id = (idx + 1).toString(), url = url) },
                        storageUsedHuman = r.data.data?.storageUsed ?: _state.value.storageUsedHuman,
                        totalStorageHuman = r.data.data?.totalStorage ?: _state.value.totalStorageHuman,
                        infoMessage = info
                    )
                }
                is NetworkResult.Error -> {
                    _state.value = _state.value.copy(isSaving = false, errorMessage = r.message)
                }
                is NetworkResult.ConnectionError -> {
                    _state.value = _state.value.copy(
                        isSaving = false,
                        errorMessage = "Connection error. Check your internet."
                    )
                }
            }
        }
    }
}
