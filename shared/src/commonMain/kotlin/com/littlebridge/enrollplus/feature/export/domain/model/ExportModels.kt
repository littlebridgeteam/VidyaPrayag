package com.littlebridge.enrollplus.feature.export.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExportTypeDto(
    val type: String,
    val label: String,
    val category: String,
    val formats: List<String>,
    val filters: List<String>,
    val icon: String,
    @SerialName("admin_only") val adminOnly: Boolean,
)

@Serializable
data class ExportTypesResponse(
    val exports: List<ExportTypeDto>,
)

@Serializable
data class ExportRequest(
    val type: String,
    val format: String,
    @SerialName("class_id") val classId: String? = null,
    @SerialName("assessment_id") val assessmentId: String? = null,
    @SerialName("event_id") val eventId: String? = null,
    @SerialName("route_id") val routeId: String? = null,
    @SerialName("homework_id") val homeworkId: String? = null,
    val status: String? = null,
    @SerialName("date_from") val dateFrom: String? = null,
    @SerialName("date_to") val dateTo: String? = null,
)

@Serializable
data class ExportResponse(
    @SerialName("download_url") val downloadUrl: String? = null,
    @SerialName("file_name") val fileName: String? = null,
    @SerialName("file_size") val fileSize: Long = 0,
    val format: String,
    val message: String? = null,
)
