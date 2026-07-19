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
    val address: String? = null,
    @SerialName("photo_url") val photoUrl: String? = null,
    @SerialName("employee_id") val employeeId: String? = null,
    val shift: String? = null,
    val status: String = "active",
    @SerialName("joined_year") val joinedYear: String? = null,
    @SerialName("joined_date") val joinedDate: String? = null,
    @SerialName("today_items") val todayItems: List<TodayItemDto> = emptyList()
)

@Serializable
data class StaffListResponse(val staff: List<StaffDto>)

@Serializable
data class StaffPaginationDto(
    val page: Int,
    val pageSize: Int,
    val totalRecords: Int,
    val hasNext: Boolean
)

@Serializable
data class StaffListPaginatedResponse(
    val staff: List<StaffDto>,
    val pagination: StaffPaginationDto
)

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
