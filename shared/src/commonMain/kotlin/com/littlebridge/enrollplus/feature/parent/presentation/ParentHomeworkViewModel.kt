package com.littlebridge.enrollplus.feature.parent.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.core.prefs.PreferenceRepository
import com.littlebridge.enrollplus.core.state.SelectedChildHolder
import com.littlebridge.enrollplus.feature.admin.data.remote.MediaApi
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentHomeworkAttachmentDto
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentHomeworkItemDto
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentSubmitHomeworkAttachmentDto
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentSubmitHomeworkRequest
import com.littlebridge.enrollplus.feature.parent.domain.repository.ParentRepository
import kotlin.random.Random
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ParentHomeworkState(
    val childId: String = "",
    val items: List<ParentHomeworkItemDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,

    // Submission sheet state
    val selectedHomework: ParentHomeworkItemDto? = null,
    val submissionText: String = "",
    val attachments: List<ParentHomeworkAttachmentDto> = emptyList(),
    val isUploadingAttachment: Boolean = false,
    val uploadError: String? = null,
    val isSubmitting: Boolean = false,
    val submitError: String? = null,
    val submitSuccess: Boolean = false,
)

class ParentHomeworkViewModel(
    private val repository: ParentRepository,
    private val mediaApi: MediaApi,
    private val preferenceRepository: PreferenceRepository,
    private val selectedChildHolder: SelectedChildHolder,
) : ViewModel() {
    private val _state = MutableStateFlow(ParentHomeworkState())
    val state: StateFlow<ParentHomeworkState> = _state.asStateFlow()

    private suspend fun token(): String? = preferenceRepository.getUserToken().first()

    fun setChildId(childId: String) {
        _state.update { it.copy(childId = childId) }
        loadList(childId)
    }

    fun loadList(childId: String? = null) {
        val resolved = childId ?: state.value.childId.ifBlank { selectedChildHolder.selectedChildId.value ?: return }
        _state.update { it.copy(childId = resolved, isLoading = true, error = null) }
        viewModelScope.launch {
            val token = token() ?: run {
                _state.update { it.copy(isLoading = false, error = "Not authenticated") }
                return@launch
            }
            when (val r = repository.getParentHomeworkList(token, resolved)) {
                is NetworkResult.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            items = r.data.data.items,
                            error = null,
                        )
                    }
                }
                is NetworkResult.Error -> _state.update { it.copy(isLoading = false, error = r.message) }
                is NetworkResult.ConnectionError -> _state.update { it.copy(isLoading = false, error = "Connection error") }
            }
        }
    }

    fun selectHomework(homework: ParentHomeworkItemDto?) {
        _state.update {
            it.copy(
                selectedHomework = homework,
                submissionText = homework?.submissionText ?: "",
                attachments = homework?.attachments ?: emptyList(),
                submitError = null,
                submitSuccess = false,
            )
        }
    }

    fun setSubmissionText(text: String) {
        _state.update { it.copy(submissionText = text, submitError = null) }
    }

    fun addAttachment(url: String, filename: String, mime: String, sizeBytes: Long) {
        _state.update {
            it.copy(
                attachments = it.attachments + ParentHomeworkAttachmentDto(
                    id = "local-${Random.nextLong()}",
                    url = url,
                    filename = filename,
                    mime = mime,
                    sizeBytes = sizeBytes,
                ),
                submitError = null,
            )
        }
    }

    fun removeAttachment(attachment: ParentHomeworkAttachmentDto) {
        _state.update { it.copy(attachments = it.attachments - attachment) }
    }

    fun uploadAttachment(bytes: ByteArray, fileName: String, mimeType: String) {
        _state.update { it.copy(isUploadingAttachment = true, uploadError = null) }
        viewModelScope.launch {
            val token = token() ?: run {
                _state.update { it.copy(isUploadingAttachment = false, uploadError = "Not authenticated") }
                return@launch
            }
            when (val r = mediaApi.uploadMedia(token, bytes, fileName, mimeType, "IMAGE")) {
                is NetworkResult.Success -> {
                    val url = r.data.data?.url
                    _state.update { it.copy(isUploadingAttachment = false) }
                    if (url.isNullOrBlank()) {
                        _state.update { it.copy(uploadError = "Upload succeeded but no URL returned") }
                    } else {
                        addAttachment(url, fileName, mimeType, bytes.size.toLong())
                    }
                }
                is NetworkResult.Error -> _state.update { it.copy(isUploadingAttachment = false, uploadError = r.message) }
                is NetworkResult.ConnectionError -> _state.update { it.copy(isUploadingAttachment = false, uploadError = "Connection error") }
            }
        }
    }

    fun submit() {
        val hw = state.value.selectedHomework ?: return
        val childId = state.value.childId.ifBlank { selectedChildHolder.selectedChildId.value ?: return }
        val text = state.value.submissionText
        val attachments = state.value.attachments
        _state.update { it.copy(isSubmitting = true, submitError = null, submitSuccess = false) }
        viewModelScope.launch {
            val token = token() ?: run {
                _state.update { it.copy(isSubmitting = false, submitError = "Not authenticated") }
                return@launch
            }
            val request = ParentSubmitHomeworkRequest(
                text = text,
                attachments = attachments.map { att ->
                    ParentSubmitHomeworkAttachmentDto(
                        url = att.url,
                        filename = att.filename,
                        mime = att.mime,
                        sizeBytes = att.sizeBytes,
                    )
                },
            )
            when (val r = repository.submitParentHomework(token, childId, hw.id, request)) {
                is NetworkResult.Success -> {
                    _state.update {
                        it.copy(
                            isSubmitting = false,
                            submitSuccess = true,
                            submitError = null,
                        )
                    }
                    // Refresh list so the submitted status appears.
                    loadList(childId)
                }
                is NetworkResult.Error -> _state.update { it.copy(isSubmitting = false, submitError = r.message) }
                is NetworkResult.ConnectionError -> _state.update { it.copy(isSubmitting = false, submitError = "Connection error") }
            }
        }
    }
}
