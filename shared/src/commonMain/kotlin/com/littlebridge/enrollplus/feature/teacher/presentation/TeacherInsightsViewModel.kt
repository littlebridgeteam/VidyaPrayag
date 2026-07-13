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
            // Group by className-section to avoid duplicate at-risk cards when a teacher
            // teaches multiple subjects for the same class.
            val byClassSection = classes.groupBy { "${it.className}-${it.section}" }

            byClassSection.forEach { (_, sectionClasses) ->
                val first = sectionClasses.first()
                val scope = if (first.section.isBlank()) {
                    "${first.className} · ${first.subject}"
                } else {
                    "${first.className}-${first.section} · ${first.subject}"
                }
                val maxAtRisk = sectionClasses.maxOf { it.atRiskCount }
                val anyAssignmentId = sectionClasses.firstOrNull { it.assignmentId.isNotBlank() }?.assignmentId

                if (maxAtRisk > 0) {
                    add(InsightCard(
                        id = "at_risk_${first.className}_${first.section}",
                        title = "$maxAtRisk at-risk student${if (maxAtRisk > 1) "s" else ""} in ${first.className}-${first.section}",
                        description = "Attendance below 75%. Review and notify parents.",
                        severity = if (maxAtRisk >= 3) InsightSeverity.HIGH else InsightSeverity.MEDIUM,
                        assignmentId = anyAssignmentId,
                        scopeLabel = scope,
                        actionLabel = "View Insights",
                        icon = "trending_down",
                        target = InsightTarget.Pews,
                    ))
                }

                // Unmarked attendance: only one card per class section (not per subject)
                val unmarkedClass = sectionClasses.firstOrNull { !it.todayAttendanceMarked && it.isClassTeacher }
                if (unmarkedClass != null) {
                    add(InsightCard(
                        id = "unmarked_${first.className}_${first.section}",
                        title = "Attendance not marked for ${first.className}-${first.section}",
                        description = "Mark today's attendance to keep records current.",
                        severity = InsightSeverity.MEDIUM,
                        assignmentId = unmarkedClass.assignmentId,
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
