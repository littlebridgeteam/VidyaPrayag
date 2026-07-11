/*
 * File: PinnedScreensModels.kt
 * Module: feature.admin.domain.model
 *
 * Client DTOs for the home-screen pinned shortcuts endpoints.
 */
package com.littlebridge.enrollplus.feature.admin.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PinnedScreens(
    @SerialName("pinned_screens") val pinnedScreens: List<String> = emptyList()
)
