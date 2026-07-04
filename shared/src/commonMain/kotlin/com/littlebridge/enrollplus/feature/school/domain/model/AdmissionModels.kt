package com.littlebridge.enrollplus.feature.school.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Enquiry(
    val id: String? = null,
    @SerialName("student_name") val studentName: String,
    @SerialName("parent_name") val parentName: String,
    @SerialName("class") val className: String,
    val date: String,
    val status: String,
    @SerialName("profile_pic") val profilePic: String? = null
) {
    companion object {
        const val STATUS_NEW = "new"
        const val STATUS_FOLLOWUP = "followup"
        const val STATUS_CONVERTED = "converted"
        const val STATUS_REJECTED = "rejected"

        val ALLOWED_STATUSES = setOf(STATUS_NEW, STATUS_FOLLOWUP, STATUS_CONVERTED, STATUS_REJECTED)
    }
}

@Serializable
data class EnquirySummaryCount(
    val total: Int,
    val new: Int,
    @SerialName("follow_ups") val followUps: Int,
    val converted: Int
)

@Serializable
data class EnquirySummary(
    @SerialName("summary_count") val summaryCount: EnquirySummaryCount,
    @SerialName("recent_enquiries") val recentEnquiries: List<Enquiry>,
    val efficiency: String
)

@Serializable
data class EnquiryPagination(
    @SerialName("current_page") val currentPage: Int,
    @SerialName("total_pages") val totalPages: Int,
    @SerialName("total_records") val totalRecords: Int
)

@Serializable
data class EnquiryListResponse(
    val enquiries: List<Enquiry>,
    val pagination: EnquiryPagination
)

@Serializable
data class CreateEnquiryRequest(
    @SerialName("student_name") val studentName: String,
    @SerialName("parent_name") val parentName: String,
    @SerialName("parent_phone") val parentPhone: String? = null,
    @SerialName("parent_email") val parentEmail: String? = null,
    @SerialName("class") val className: String,
    val source: String? = null,
    val notes: String? = null,
    @SerialName("school_id") val schoolId: String? = null
)

@Serializable
data class UpdateEnquiryStatusRequest(
    val status: String
)
