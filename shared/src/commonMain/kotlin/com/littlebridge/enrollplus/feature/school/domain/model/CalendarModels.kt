package com.littlebridge.enrollplus.feature.school.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CalendarEventDto(
    val date: String,
    val day: String,
    @SerialName("event_id") val eventId: String,
    @SerialName("event_title") val eventTitle: String,
    @SerialName("event_description") val eventDescription: String = ""
)

@Serializable
data class CalendarSummaryDto(
    @SerialName("working_days") val workingDays: Int = 0,
    @SerialName("total_working_days") val totalWorkingDays: Int = 0,
    @SerialName("public_holidays") val publicHolidays: Int = 0,
    @SerialName("school_holidays") val schoolHolidays: Int = 0
) {
    val effectiveWorkingDays: Int get() = if (workingDays > 0) workingDays else totalWorkingDays
}

@Serializable
data class CalendarResponse(
    @SerialName("calendar_events") val calendarEvents: List<CalendarEventDto> = emptyList(),
    val summary: CalendarSummaryDto
)
