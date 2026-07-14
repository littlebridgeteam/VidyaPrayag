package com.littlebridge.enrollplus.feature.parent.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ParentHomeworkAttachmentDto(
    val id: String,
    val url: String,
    val filename: String = "",
    val mime: String = "",
    @SerialName("size_bytes") val sizeBytes: Long = 0,
)

@Serializable
data class ParentHomeworkItemDto(
    val id: String,
    val title: String,
    val description: String = "",
    val subject: String = "",
    @SerialName("due_date") val dueDate: String,
    @SerialName("due_time") val dueTime: String? = null,
    @SerialName("allow_late") val allowLate: Boolean = false,
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("is_past_due") val isPastDue: Boolean = false,
    val status: String = "not_submitted",
    @SerialName("submitted_at") val submittedAt: String? = null,
    @SerialName("submission_text") val submissionText: String = "",
    val attachments: List<ParentHomeworkAttachmentDto> = emptyList(),
    @SerialName("has_extension") val hasExtension: Boolean = false,
    @SerialName("extended_to") val extendedTo: String? = null,
)

@Serializable
data class ParentHomeworkListData(
    val items: List<ParentHomeworkItemDto> = emptyList(),
)

@Serializable
data class ParentHomeworkListResponse(
    val success: Boolean = true,
    val message: String = "",
    val data: ParentHomeworkListData = ParentHomeworkListData(),
)

@Serializable
data class ParentHomeworkDetailResponse(
    val success: Boolean = true,
    val message: String = "",
    val data: ParentHomeworkDetailData = ParentHomeworkDetailData(),
)

@Serializable
data class ParentHomeworkDetailData(
    val homework: ParentHomeworkItemDto? = null,
)

@Serializable
data class ParentSubmitHomeworkAttachmentDto(
    val url: String,
    val filename: String = "",
    val mime: String = "",
    @SerialName("size_bytes") val sizeBytes: Long = 0,
)

@Serializable
data class ParentSubmitHomeworkRequest(
    val text: String = "",
    val attachments: List<ParentSubmitHomeworkAttachmentDto> = emptyList(),
)

@Serializable
data class ParentHomeworkMutationData(
    val success: Boolean = true,
    val message: String = "",
)

@Serializable
data class ParentHomeworkMutationResponse(
    val success: Boolean = true,
    val message: String = "",
    val data: ParentHomeworkMutationData = ParentHomeworkMutationData(),
)
