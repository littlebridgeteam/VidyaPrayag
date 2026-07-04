package com.littlebridge.enrollplus.feature.school.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LinkRequestDto(
    val id: String,
    @SerialName("parent_id") val parentId: String,
    @SerialName("parent_name") val parentName: String? = null,
    @SerialName("parent_phone") val parentPhone: String? = null,
    @SerialName("student_code") val studentCode: String? = null,
    @SerialName("roll_number") val rollNumber: String? = null,
    @SerialName("child_name") val childName: String? = null,
    @SerialName("class_name") val className: String? = null,
    val section: String? = null,
    val status: String,
    @SerialName("review_reason") val reviewReason: String? = null,
    @SerialName("requested_at") val requestedAt: String,
)

@Serializable
data class LinkRequestsResponse(
    val requests: List<LinkRequestDto> = emptyList()
)

@Serializable
data class LinkDecisionResult(
    @SerialName("child_id") val childId: String? = null,
    val status: String = "",
)
