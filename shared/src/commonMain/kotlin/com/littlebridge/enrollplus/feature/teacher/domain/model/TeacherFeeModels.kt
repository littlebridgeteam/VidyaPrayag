package com.littlebridge.enrollplus.feature.teacher.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeacherFeeStudentDto(
    @SerialName("child_id") val childId: String,
    @SerialName("child_name") val childName: String,
    @SerialName("class_name") val className: String? = null,
    @SerialName("parent_id") val parentId: String,
    val month: String,
    @SerialName("due_amount") val dueAmount: Double,
    val status: String,
)

@Serializable
data class TeacherFeeListResponse(
    val students: List<TeacherFeeStudentDto>,
    @SerialName("total_due") val totalDue: Double,
)

@Serializable
data class EscalateFeeRequest(
    @SerialName("child_ids") val childIds: List<String>,
    val message: String? = null,
)
