package com.littlebridge.enrollplus.feature.school.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StaffDto(
    val id: String,
    @SerialName("full_name") val fullName: String,
    val role: String,
    val department: String? = null,
    val phone: String? = null,
    val email: String? = null,
    @SerialName("photo_url") val photoUrl: String? = null
)

@Serializable
data class StaffListResponse(val staff: List<StaffDto>)

@Serializable
data class CreateStaffRequest(
    @SerialName("full_name") val fullName: String,
    val role: String,
    val department: String? = null,
    val phone: String? = null,
    val email: String? = null
)

@Serializable
data class UpdateStaffRequest(
    @SerialName("full_name") val fullName: String? = null,
    val role: String? = null,
    val department: String? = null,
    val phone: String? = null,
    val email: String? = null
)
