package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentMarkDto
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentSyllabusSubjectDto
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentQuizDto
import com.littlebridge.enrollplus.feature.parent.presentation.ParentAcademicsState
import com.littlebridge.enrollplus.feature.parent.presentation.ParentAcademicsViewModel
import com.littlebridge.enrollplus.ui.v2.components.misc.VShimmerBoxPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VStateHostPremium
import com.littlebridge.enrollplus.ui.v2.components.navigation.VFilterChip
import com.littlebridge.enrollplus.ui.v2.components.typography.VSectionHeader
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

private val subTabs = listOf("Overview", "Attendance", "Marks", "Syllabus", "Homework", "Quizzes", "Report")

@Composable
fun ParentAcademicsScreen(
    onOpenOverlay: (ParentOverlay) -> Unit,
    onSwitchTab: (Int) -> Unit = {},
    initialSubTab: Int = 0,
    modifier: Modifier = Modifier,
    viewModel: ParentAcademicsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    var subTab by rememberSaveable { mutableIntStateOf(initialSubTab) }

    LaunchedEffect(subTab) {
        val childId = state.selectedChild?.id ?: return@LaunchedEffect
        when (subTabs[subTab]) {
            "Attendance" -> if (state.attendance == null) viewModel.loadAttendance(childId)
            "Marks" -> if (state.marks == null) viewModel.loadMarks(childId)
            "Syllabus" -> if (state.syllabus == null) viewModel.loadSyllabus(childId)
            "Homework" -> if (state.dailySummary == null) viewModel.loadDailySummary(childId)
            "Quizzes" -> if (state.quizzes.isEmpty()) viewModel.loadQuizzes(childId)
            "Report" -> if (state.marks == null) viewModel.loadMarks(childId)
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Sub-tab chips row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            subTabs.forEachIndexed { index, label ->
                VFilterChip(
                    label = label,
                    active = subTab == index,
                    onClick = { subTab = index },
                )
            }
        }

        // Content area: weight(1f) + verticalScroll (layout safety Rule 5)
        Box(Modifier.weight(1f).fillMaxWidth()) {
            AnimatedContent(
                targetState = subTab,
                transitionSpec = { fadeIn(tween(300)).togetherWith(fadeOut(tween(200))) },
                label = "academicsSubTab",
            ) { current ->
                when (subTabs[current]) {
                    "Overview" -> OverviewTab(state, viewModel)
                    "Attendance" -> AttendanceTab(state, viewModel)
                    "Marks" -> MarksTab(state)
                    "Syllabus" -> SyllabusTab(state)
                    "Homework" -> HomeworkTab(state)
                    "Quizzes" -> QuizzesTab(state, onOpenOverlay)
                    "Report" -> ReportTab(state)
                }
            }
        }
    }
}

// ── Overview ──────────────────────────────────────────────────────────────

@Composable
private fun OverviewTab(
    state: ParentAcademicsState,
    viewModel: ParentAcademicsViewModel,
) {
    val childId = state.selectedChild?.id
    LaunchedEffect(childId) {
        if (childId != null && state.attendance == null) viewModel.loadAttendance(childId)
    }

    AcademicsScrollContent(
        loading = state.childrenLoading,
        error = state.childrenError,
        isEmpty = state.children.isEmpty() && !state.childrenLoading,
        onRetry = { viewModel.loadChildren() },
        emptyTitle = "No data yet",
        emptyIcon = Icons.Filled.School,
    ) {
        // Attendance ring
        val attendanceRate = state.attendance?.attendanceRate ?: 0
        StatRingCard(
            label = "Attendance",
            percentage = attendanceRate,
            subtitle = "${state.attendance?.presentDays ?: 0} present / ${state.attendance?.totalDays ?: 0} total",
        )

        Spacer(Modifier.height(16.dp))

        // Marks summary
        val marks = state.marks?.results ?: emptyList()
        if (marks.isNotEmpty()) {
            VSectionHeader(title = "Latest Marks", modifier = Modifier.padding(horizontal = 4.dp))
            Spacer(Modifier.height(8.dp))
            marks.take(3).forEach { mark ->
                MarkCard(mark = mark)
                Spacer(Modifier.height(8.dp))
            }
        }

        Spacer(Modifier.height(16.dp))

        // Syllabus coverage
        val subjects = state.syllabus?.subjects ?: emptyList()
        if (subjects.isNotEmpty()) {
            VSectionHeader(title = "Syllabus Coverage", modifier = Modifier.padding(horizontal = 4.dp))
            Spacer(Modifier.height(8.dp))
            subjects.forEach { subject ->
                SubjectProgressRow(subject = subject)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

// ── Attendance ────────────────────────────────────────────────────────────

@Composable
private fun AttendanceTab(
    state: ParentAcademicsState,
    viewModel: ParentAcademicsViewModel,
) {
    val childId = state.selectedChild?.id
    LaunchedEffect(childId) {
        if (childId != null && state.attendance == null) viewModel.loadAttendance(childId)
    }

    AcademicsScrollContent(
        loading = state.attendanceLoading,
        error = state.attendanceError,
        isEmpty = state.attendance == null && !state.attendanceLoading,
        onRetry = { childId?.let { viewModel.loadAttendance(it) } },
        emptyTitle = "No attendance records",
        emptyIcon = Icons.Filled.School,
    ) {
        val att = state.attendance ?: return@AcademicsScrollContent

        StatRingCard(
            label = "Attendance Rate",
            percentage = att.attendanceRate,
            subtitle = "${att.presentDays} present, ${att.absentDays} absent, ${att.lateDays} late",
        )

        Spacer(Modifier.height(16.dp))

        VSectionHeader(title = "Breakdown", modifier = Modifier.padding(horizontal = 4.dp))
        Spacer(Modifier.height(8.dp))

        BreakdownBar(label = "Present", count = att.presentDays, total = att.totalDays, color = VColors.Primary)
        Spacer(Modifier.height(6.dp))
        BreakdownBar(label = "Absent", count = att.absentDays, total = att.totalDays, color = VColors.Error)
        Spacer(Modifier.height(6.dp))
        BreakdownBar(label = "Late", count = att.lateDays, total = att.totalDays, color = VColors.WarmOrange)

        Spacer(Modifier.height(16.dp))

        VSectionHeader(title = "Recent Records", modifier = Modifier.padding(horizontal = 4.dp))
        Spacer(Modifier.height(8.dp))

        att.records.takeLast(10).forEach { record ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(VShapes.Lg)
                    .background(VColors.SurfaceContainerLow)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier.size(8.dp).clip(CircleShape).background(
                        when (record.status.lowercase()) {
                            "present" -> VColors.Primary
                            "absent" -> VColors.Error
                            "late" -> VColors.WarmOrange
                            else -> VColors.Outline
                        },
                    ),
                )
                Text(
                    text = record.date,
                    style = VTypography.BodyMedium.copy(color = VColors.OnSurface),
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = record.status.replaceFirstChar { it.uppercase() },
                    style = VTypography.ThreadPreview.copy(color = VColors.OnSurfaceVariant),
                )
            }
            Spacer(Modifier.height(6.dp))
        }
    }
}

// ── Marks ─────────────────────────────────────────────────────────────────

@Composable
private fun MarksTab(state: ParentAcademicsState) {
    AcademicsScrollContent(
        loading = state.marksLoading,
        error = state.marksError,
        isEmpty = (state.marks?.results?.isEmpty() ?: true) && !state.marksLoading,
        onRetry = null,
        emptyTitle = "No marks published",
        emptyIcon = Icons.Filled.School,
    ) {
        val marks = state.marks?.results ?: return@AcademicsScrollContent

        // Group by subject for filter chips
        val subjects = marks.map { it.subject }.distinct()
        var selectedSubject by rememberSaveable { mutableIntStateOf(0) }

        if (subjects.size > 1) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                subjects.forEachIndexed { index, subject ->
                    VFilterChip(
                        label = subject,
                        active = selectedSubject == index,
                        onClick = { selectedSubject = index },
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        val filtered = if (subjects.size > 1) marks.filter { it.subject == subjects[selectedSubject] } else marks
        filtered.forEach { mark ->
            MarkCard(mark = mark)
            Spacer(Modifier.height(8.dp))
        }
    }
}

// ── Syllabus ──────────────────────────────────────────────────────────────

@Composable
private fun SyllabusTab(state: ParentAcademicsState) {
    AcademicsScrollContent(
        loading = state.syllabusLoading,
        error = state.syllabusError,
        isEmpty = (state.syllabus?.subjects?.isEmpty() ?: true) && !state.syllabusLoading,
        onRetry = null,
        emptyTitle = "No syllabus data",
        emptyIcon = Icons.Filled.School,
    ) {
        val subjects = state.syllabus?.subjects ?: return@AcademicsScrollContent

        // Overall coverage card
        val avgProgress = if (subjects.isNotEmpty()) subjects.sumOf { it.progress } / subjects.size else 0
        StatRingCard(
            label = "Overall Coverage",
            percentage = avgProgress,
            subtitle = "${subjects.size} subjects",
        )

        Spacer(Modifier.height(16.dp))

        VSectionHeader(title = "Subjects", modifier = Modifier.padding(horizontal = 4.dp))
        Spacer(Modifier.height(8.dp))

        subjects.forEach { subject ->
            SubjectProgressRow(subject = subject)
            Spacer(Modifier.height(8.dp))
        }
    }
}

// ── Homework ──────────────────────────────────────────────────────────────

@Composable
private fun HomeworkTab(state: ParentAcademicsState) {
    AcademicsScrollContent(
        loading = state.dailySummaryLoading,
        error = state.dailySummaryError,
        isEmpty = (state.dailySummary?.entries?.isEmpty() ?: true) && !state.dailySummaryLoading,
        onRetry = null,
        emptyTitle = "No homework entries",
        emptyIcon = Icons.Filled.School,
    ) {
        val entries = state.dailySummary?.entries ?: return@AcademicsScrollContent

        entries.forEach { entry ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(VShapes.Lg)
                    .background(VColors.SurfaceContainerLow)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Box(
                    Modifier.size(36.dp).clip(VShapes.Md).background(VColors.PrimaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = entry.subject.take(1).uppercase(),
                        style = VTypography.QuickStatValue.copy(color = VColors.OnPrimaryContainer),
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = entry.subject,
                        style = VTypography.UpdateTitle.copy(color = VColors.OnSurface),
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = entry.summaryText,
                        style = VTypography.BodyMedium.copy(color = VColors.OnSurfaceVariant),
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            text = entry.date,
                            style = VTypography.ThreadTime.copy(color = VColors.Outline),
                        )
                        if (entry.isAiEstimated) {
                            Text(
                                text = "AI estimated",
                                style = VTypography.ThreadTime.copy(color = VColors.Tertiary),
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

// ── Quizzes ───────────────────────────────────────────────────────────────

@Composable
private fun QuizzesTab(
    state: ParentAcademicsState,
    onOpenOverlay: (ParentOverlay) -> Unit,
) {
    AcademicsScrollContent(
        loading = state.quizzesLoading,
        error = state.quizzesError,
        isEmpty = state.quizzes.isEmpty() && !state.quizzesLoading,
        onRetry = null,
        emptyTitle = "No quizzes available",
        emptyIcon = Icons.Filled.Quiz,
    ) {
        state.quizzes.forEach { quiz ->
            QuizCard(quiz = quiz)
            Spacer(Modifier.height(8.dp))
        }
    }
}

// ── Report ────────────────────────────────────────────────────────────────

@Composable
private fun ReportTab(state: ParentAcademicsState) {
    AcademicsScrollContent(
        loading = state.marksLoading,
        error = state.marksError,
        isEmpty = (state.marks?.results?.isEmpty() ?: true) && !state.marksLoading,
        onRetry = null,
        emptyTitle = "No report data",
        emptyIcon = Icons.Filled.School,
    ) {
        val marks = state.marks?.results ?: return@AcademicsScrollContent

        // Group by exam name
        val grouped = marks.groupBy { it.examName }
        grouped.forEach { (examName, examMarks) ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(VShapes.Xl)
                    .background(VColors.SurfaceContainerLowest)
                    .padding(20.dp),
            ) {
                Text(
                    text = examName,
                    style = VTypography.SectionHeader.copy(color = VColors.OnSurface),
                )
                Spacer(Modifier.height(12.dp))
                examMarks.forEach { mark ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = mark.subject,
                            style = VTypography.BodyMedium.copy(color = VColors.OnSurface),
                            modifier = Modifier.weight(1f),
                        )
                        val scoreText = mark.marks?.let { "${it.toInt()}/${mark.maxMarks}" } ?: "Pending"
                        Text(
                            text = scoreText,
                            style = VTypography.QuickStatValue.copy(color = VColors.OnSurface),
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

// ── Shared components ─────────────────────────────────────────────────────

@Composable
private fun AcademicsScrollContent(
    loading: Boolean,
    error: String?,
    isEmpty: Boolean,
    onRetry: (() -> Unit)?,
    emptyTitle: String,
    emptyIcon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit,
) {
    VStateHostPremium(
        loading = loading,
        error = error,
        isEmpty = isEmpty,
        modifier = Modifier.fillMaxSize(),
        emptyTitle = emptyTitle,
        emptyIcon = emptyIcon,
        onRetry = onRetry,
        skeleton = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                VShimmerBoxPremium(height = 120.dp, shape = VShapes.TwoXl)
                repeat(4) { VShimmerBoxPremium(height = 60.dp, shape = VShapes.Lg) }
            }
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 140.dp),
        ) {
            content()
        }
    }
}

@Composable
private fun StatRingCard(label: String, percentage: Int, subtitle: String) {
    val primaryColor = VColors.Primary
    val trackColor = VColors.SurfaceContainerHigh
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.TwoXl)
            .background(VColors.SurfaceContainerLowest)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Box(
            modifier = Modifier.size(80.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .drawBehind {
                        drawArc(
                            color = trackColor,
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round),
                        )
                        drawArc(
                            color = primaryColor,
                            startAngle = -90f,
                            sweepAngle = 360f * percentage / 100f,
                            useCenter = false,
                            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round),
                        )
                    },
            )
            Text(
                text = "$percentage%",
                style = VTypography.QuickStatValue.copy(color = VColors.OnSurface),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = VTypography.UpdateTitle.copy(color = VColors.OnSurface),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = VTypography.BodyMedium.copy(color = VColors.OnSurfaceVariant),
            )
        }
    }
}

@Composable
private fun BreakdownBar(label: String, count: Int, total: Int, color: androidx.compose.ui.graphics.Color) {
    val pct = if (total > 0) count.toFloat() / total else 0f
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "$label ($count)",
                style = VTypography.BodyMedium.copy(color = VColors.OnSurface),
            )
            Text(
                text = "${(pct * 100).toInt()}%",
                style = VTypography.ThreadPreview.copy(color = VColors.OnSurfaceVariant),
            )
        }
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(VShapes.Full)
                .background(VColors.SurfaceContainerHigh),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(pct)
                    .height(8.dp)
                    .clip(VShapes.Full)
                    .background(color),
            )
        }
    }
}

@Composable
private fun MarkCard(mark: ParentMarkDto) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.Lg)
            .background(VColors.SurfaceContainerLow)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = mark.subject,
                style = VTypography.UpdateTitle.copy(color = VColors.OnSurface),
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = mark.examName,
                style = VTypography.ThreadPreview.copy(color = VColors.OnSurfaceVariant),
            )
            if (mark.examDate != null) {
                val date = mark.examDate!!
                Text(
                    text = date,
                    style = VTypography.ThreadTime.copy(color = VColors.Outline),
                )
            }
        }
        val scoreText = mark.marks?.let { "${it.toInt()}/${mark.maxMarks}" } ?: "Pending"
        Text(
            text = scoreText,
            style = VTypography.QuickStatValue.copy(color = VColors.OnSurface),
        )
    }
}

@Composable
private fun SubjectProgressRow(subject: ParentSyllabusSubjectDto) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.Lg)
            .background(VColors.SurfaceContainerLow)
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = subject.subject,
                style = VTypography.UpdateTitle.copy(color = VColors.OnSurface),
            )
            Text(
                text = "${subject.progress}%",
                style = VTypography.QuickStatValue.copy(color = VColors.OnSurface),
            )
        }
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(VShapes.Full)
                .background(VColors.SurfaceContainerHigh),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(subject.progress / 100f)
                    .height(6.dp)
                    .clip(VShapes.Full)
                    .background(VColors.Primary),
            )
        }
        if (subject.units.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            subject.units.take(3).forEach { unit ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        Modifier.size(6.dp).clip(CircleShape).background(
                            if (unit.isCovered) VColors.Primary else VColors.Outline,
                        ),
                    )
                    Text(
                        text = unit.title,
                        style = VTypography.ThreadPreview.copy(color = VColors.OnSurfaceVariant),
                    )
                }
                Spacer(Modifier.height(4.dp))
            }
        }
    }
}

@Composable
private fun QuizCard(quiz: ParentQuizDto) {
    val statusColor = when (quiz.status.uppercase()) {
        "COMPLETED" -> VColors.Primary
        "PENDING" -> VColors.WarmOrange
        "EXPIRED" -> VColors.Error
        else -> VColors.Outline
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.Lg)
            .background(VColors.SurfaceContainerLow)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(40.dp).clip(VShapes.Md).background(VColors.PrimaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.Quiz, contentDescription = null, tint = VColors.Primary, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = quiz.title.ifBlank { quiz.subject },
                style = VTypography.UpdateTitle.copy(color = VColors.OnSurface),
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "${quiz.subject} - ${quiz.numQuestions} questions",
                style = VTypography.ThreadPreview.copy(color = VColors.OnSurfaceVariant),
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = quiz.status,
                style = VTypography.ThreadTime.copy(color = statusColor),
            )
            if (quiz.totalMarks > 0) {
                Text(
                    text = "${quiz.totalMarks} marks",
                    style = VTypography.ThreadTime.copy(color = VColors.Outline),
                )
            }
        }
    }
}
