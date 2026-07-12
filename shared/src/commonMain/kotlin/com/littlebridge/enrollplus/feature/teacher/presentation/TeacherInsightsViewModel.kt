package com.littlebridge.enrollplus.feature.teacher.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlebridge.enrollplus.core.prefs.PreferenceRepository
import com.littlebridge.enrollplus.feature.teacher.domain.model.TeacherClassSummaryDto
import com.littlebridge.enrollplus.feature.teacher.domain.repository.TeacherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class InsightCard(
    val id: String,
    val title: String,
    val description: String,
    val severity: InsightSeverity,
    val assignmentId: String?,
    val scopeLabel: String,
    val actionLabel: String,
    val icon: String,
    val target: InsightTarget = InsightTarget.Attendance,
)

enum class InsightSeverity { HIGH, MEDIUM, LOW }

enum class InsightTarget { Pews, Attendance }

data class TeacherInsightsState(
    val insights: List<InsightCard> = emptyList(),
    val isLoading: Boolean = false,
)

class TeacherInsightsViewModel(
    private val repository: TeacherRepository,
    private val preferenceRepository: PreferenceRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(TeacherInsightsState())
    val state: StateFlow<TeacherInsightsState> = _state.asStateFlow()

    fun deriveFromClassSummaries(classes: List<TeacherClassSummaryDto>) {
        val insights = buildList {
            classes.forEach { cls ->
                val scope = if (cls.section.isBlank()) {
                    "${cls.className} · ${cls.subject}"
                } else {
                    "${cls.className}-${cls.section} · ${cls.subject}"
                }

                if (cls.atRiskCount > 0) {
                    add(InsightCard(
                        id = "at_risk_${cls.assignmentId}",
                        title = "${cls.atRiskCount} at-risk student${if (cls.atRiskCount > 1) "s" else ""} in ${cls.className}-${cls.section}",
                        description = "Attendance below 75%. Review and notify parents.",
                        severity = if (cls.atRiskCount >= 3) InsightSeverity.HIGH else InsightSeverity.MEDIUM,
                        assignmentId = cls.assignmentId,
                        scopeLabel = scope,
                        actionLabel = "View Insights",
                        icon = "trending_down",
                        target = InsightTarget.Pews,
                    ))
                }

                if (!cls.todayAttendanceMarked && cls.isClassTeacher) {
                    add(InsightCard(
                        id = "unmarked_${cls.assignmentId}",
                        title = "Attendance not marked for ${cls.className}-${cls.section}",
                        description = "Mark today's attendance to keep records current.",
                        severity = InsightSeverity.MEDIUM,
                        assignmentId = cls.assignmentId,
                        scopeLabel = scope,
                        actionLabel = "Mark Now",
                        icon = "clock",
                        target = InsightTarget.Attendance,
                    ))
                }
            }
        }.sortedByDescending { severityWeight(it.severity) }

        _state.update { it.copy(insights = insights) }
    }

    private fun severityWeight(s: InsightSeverity) = when (s) {
        InsightSeverity.HIGH -> 3
        InsightSeverity.MEDIUM -> 2
        InsightSeverity.LOW -> 1
    }
}
