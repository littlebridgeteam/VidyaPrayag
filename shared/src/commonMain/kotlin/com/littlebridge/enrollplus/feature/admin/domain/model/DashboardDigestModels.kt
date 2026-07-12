/*
 * File: DashboardDigestModels.kt
 * Module: feature.admin.domain.model
 *
 * Client DTOs for GET /api/admin/dashboard/digest.
 */
package com.littlebridge.enrollplus.feature.admin.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DigestTask(
    val id: String,
    val label: String,
    val icon: String,
    @SerialName("action_label") val actionLabel: String,
    @SerialName("route_id") val routeId: String? = null,
    val count: Int = 0,
    val priority: String = "normal",
)

@Serializable
data class DailyDigest(
    val headline: String,
    val focus: String,
    val tasks: List<DigestTask> = emptyList(),
)
