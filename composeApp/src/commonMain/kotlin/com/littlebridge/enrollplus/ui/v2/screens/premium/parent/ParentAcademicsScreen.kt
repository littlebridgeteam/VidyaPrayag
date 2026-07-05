package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentQuizDto
import com.littlebridge.enrollplus.feature.parent.presentation.ParentAcademicsState
import com.littlebridge.enrollplus.feature.parent.presentation.ParentAcademicsViewModel
import com.littlebridge.enrollplus.ui.v2.components.carousel.VStaggeredItem
import com.littlebridge.enrollplus.ui.v2.components.misc.VPullRefreshPremium
import com.littlebridge.enrollplus.ui.v2.components.navigation.VFilterChip
import com.littlebridge.enrollplus.ui.v2.components.progress.VProgressBar
import com.littlebridge.enrollplus.ui.v2.components.typography.VSectionHeader
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
import com.littlebridge.enrollplus.ui.v2.modifiers.shapeMorph
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.PremiumTheme
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VMotion
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

/**
 * Premium parent academics — rebuilt with 7 sub-tabs (Overview, Attendance,
 * Marks, Syllabus, Homework, Quizzes, Report), AnimatedContent transitions,
 * attendance calendar, premium loading/error/empty states, pull-to-refresh.
 */
@Composable
fun ParentAcademicsScreen(
    modifier: Modifier = Modifier,
    viewModel: ParentAcademicsViewModel = koinViewModel(),
    onOpenLeave: () -> Unit = {},
    onOpenHealth: () -> Unit = {},
    onOpenQuizDetail: (String) -> Unit = {},
) = PremiumTheme(isDark = false) {
    val state by viewModel.state.collectAsStateV2()
    val tabs = listOf("Overview", "Attendance", "Marks", "Syllabus", "Homework", "Quizzes", "Report")
    var selectedTab by remember { mutableStateOf(0) }

    VPullRefreshPremium(
        isRefreshing = state.childrenLoading,
        onRefresh = { viewModel.loadChildren() },
        modifier = modifier.fillMaxSize().background(VColors.Surface),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 140.dp),
        ) {
            Spacer(Modifier.height(16.dp))

            // ── Action Cards (Leave + Health) ──
            VStaggeredItem(delayMs = 0) {
                Row(
                    Modifier.padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    ActionCard(
                        icon = { Icon(Icons.Filled.CalendarToday, contentDescription = null, tint = VColors.OnError, modifier = Modifier.size(20.dp)) },
                        title = "Apply Leave",
                        subtitle = "Leave requests",
                        bgColor = VColors.ErrorContainer,
                        iconBg = VColors.Error,
                        onClick = onOpenLeave,
                        modifier = Modifier.weight(1f),
                    )
                    ActionCard(
                        icon = { Icon(Icons.Filled.Favorite, contentDescription = null, tint = VColors.OnTertiary, modifier = Modifier.size(20.dp)) },
                        title = "Health",
                        subtitle = "View health records",
                        bgColor = VColors.TertiaryContainer,
                        iconBg = VColors.Tertiary,
                        onClick = onOpenHealth,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Sub-tab selector ──
            VStaggeredItem(delayMs = 60) {
                LazyRow(
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(tabs) { tabLabel ->
                        val index = tabs.indexOf(tabLabel)
                        VFilterChip(
                            label = tabLabel,
                            active = selectedTab == index,
                            onClick = {
                                selectedTab = index
                                when (index) {
                                    1 -> viewModel.loadAttendance()
                                    2 -> viewModel.loadMarks()
                                    3 -> viewModel.loadSyllabus()
                                    4 -> viewModel.loadDailySummary()
                                    5 -> viewModel.loadQuizzes()
                                }
                            },
                            activeBg = VColors.PrimaryContainer,
                            activeFg = VColors.OnPrimaryContainer,
                            activeFontWeight = FontWeight.Bold,
                            fontSize = 13,
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Child selector ──
            if (state.children.size > 1) {
                VStaggeredItem(delayMs = 100) {
                    Row(
                        Modifier.padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        state.children.forEach { child ->
                            VFilterChip(
                                label = child.name,
                                active = state.selectedChildId == child.id,
                                onClick = { viewModel.selectChild(child.id) },
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }

            // ── Tab content with AnimatedContent ──
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    fadeIn(tween(VMotion.DurMedium1)).togetherWith(fadeOut(tween(VMotion.DurShort2)))
                },
                label = "academicsTab",
            ) { tab ->
                when (tab) {
                    0 -> OverviewTab(state)
                    1 -> AttendanceTab(state, viewModel)
                    2 -> MarksTab(state, viewModel)
                    3 -> SyllabusTab(state, viewModel)
                    4 -> HomeworkTab(state, viewModel)
                    5 -> QuizzesTab(state, viewModel, onOpenQuizDetail)
                    6 -> ReportTab(state)
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ── Action Card ──────────────────────────────────────────────────────────────

@Composable
private fun ActionCard(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    bgColor: Color = VColors.SurfaceContainerLow,
    iconBg: Color = VColors.Primary,
) {
    val interaction = remember { MutableInteractionSource() }
    Column(
        modifier
            .pressScale(interaction, pressedScale = 0.96f)
            .shapeMorph(interaction, VShapes.LgDp, VShapes.XlDp, VMotion.DurShort2)
            .clip(VShapes.Lg)
            .background(bgColor)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            Modifier.size(36.dp).clip(VShapes.Md).background(iconBg),
            contentAlignment = Alignment.Center,
        ) { icon() }
        Text(title, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp))
        Text(subtitle, style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant, fontSize = 11.sp))
    }
}

// ── Progress Ring Card ───────────────────────────────────────────────────────

@Composable
private fun ProgressRingCard(
    title: String,
    score: Int,
    maxScore: Int = 100,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    val ringColor = when {
        score >= 85 -> VColors.Tertiary
        score >= 60 -> VColors.Primary
        else -> VColors.WarmOrange
    }
    val trackColorResolved = VColors.TertiaryContainer
    val ringColorResolved = ringColor
    val sweep = (score.toFloat() / maxScore.toFloat()) * 360f

    Column(
        modifier
            .clip(VShapes.Xl)
            .background(VColors.SurfaceContainerLowest)
            .padding(24.dp),
    ) {
        Text(title, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.Bold))
        Spacer(Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Box(
                Modifier.size(80.dp),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    Modifier
                        .size(80.dp)
                        .drawBehind {
                            drawCircle(trackColorResolved, style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round))
                            drawArc(ringColorResolved, startAngle = -90f, sweepAngle = sweep, useCenter = false, style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round))
                        },
                )
                Text("$score%", style = VTypography.StatValue.copy(color = ringColorResolved, fontSize = 22.sp))
            }
            Column(Modifier.weight(1f)) {
                Text(subtitle, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.Bold, fontSize = 16.sp))
            }
        }
    }
}

// ── Overview Tab ─────────────────────────────────────────────────────────────

@Composable
private fun OverviewTab(state: ParentAcademicsState) {
    val child = state.selectedChild
    if (child == null) {
        EmptyState("No child selected")
        return
    }

    Column(Modifier.padding(horizontal = 20.dp)) {
        // Overall Performance card with ring + text (matching HTML)
        val overallPct = child.overallProgress.toInt()
        ProgressRingCard(
            title = "Overall Performance",
            score = overallPct,
            subtitle = "Good Progress",
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(24.dp))

        // Subject Breakdown with bars
        VSectionHeader("Subject Breakdown")
        Spacer(Modifier.height(12.dp))
        if (state.marks == null || state.marks!!.results.isEmpty()) {
            EmptyState("No subject data available")
        } else {
            state.marks!!.results.forEach { result ->
                val pct = if (result.maxMarks > 0) (result.marks ?: 0.0) / result.maxMarks * 100 else 0.0
                SubjectBreakdownRow(result.subject, pct.toInt())
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

// ── Attendance Tab ───────────────────────────────────────────────────────────

@Composable
private fun AttendanceTab(state: ParentAcademicsState, viewModel: ParentAcademicsViewModel) {
    Column(Modifier.padding(horizontal = 20.dp)) {
        if (state.attendanceLoading) {
            SkeletonCard(variant = "card")
            Spacer(Modifier.height(12.dp))
            SkeletonCard(variant = "card")
        } else if (state.attendanceError != null) {
            ErrorStateCard(message = state.attendanceError!!, onRetry = { viewModel.loadAttendance() })
        } else if (state.attendance == null) {
            EmptyState("No attendance data available")
        } else {
            val att = state.attendance!!
            val rating = when {
                att.attendanceRate >= 90 -> "Excellent"
                att.attendanceRate >= 75 -> "Good"
                else -> "Needs Improvement"
            }
            ProgressRingCard(
                title = "Attendance Summary",
                score = att.attendanceRate,
                subtitle = "$rating · ${att.presentDays} present · ${att.absentDays} absent · ${att.lateDays} late",
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(24.dp))

            // This Month breakdown bars
            VSectionHeader("This Month")
            Spacer(Modifier.height(12.dp))
            val totalAtt = att.totalDays.coerceAtLeast(1)
            SubjectBreakdownRow("Days Present", att.presentDays, totalAtt, VColors.Tertiary)
            Spacer(Modifier.height(10.dp))
            SubjectBreakdownRow("Absent", att.absentDays, totalAtt, VColors.Error)
            Spacer(Modifier.height(10.dp))
            SubjectBreakdownRow("Late Arrival", att.lateDays, totalAtt, VColors.WarmOrange)

            Spacer(Modifier.height(24.dp))

            // Attendance Calendar
            VSectionHeader("Calendar")
            Spacer(Modifier.height(12.dp))
            val calendarDays = buildAttendanceCalendarDays(att)
            AttendanceCalendar(
                monthName = "This Month",
                days = calendarDays,
                onPrevMonth = {},
                onNextMonth = {},
            )
        }
    }
}

// ── Marks Tab ────────────────────────────────────────────────────────────────

@Composable
private fun MarksTab(state: ParentAcademicsState, viewModel: ParentAcademicsViewModel) {
    var selectedFilter by remember { mutableStateOf("All") }
    VSectionHeader("Recent Test Scores")
    Column(Modifier.padding(horizontal = 20.dp)) {
        if (state.marksLoading) {
            SkeletonCard(variant = "list")
            Spacer(Modifier.height(10.dp))
            SkeletonCard(variant = "list")
        } else if (state.marksError != null) {
            ErrorStateCard(message = state.marksError!!, onRetry = { viewModel.loadMarks() })
        } else if (state.marks == null || state.marks!!.results.isEmpty()) {
            EmptyState("No marks published yet")
        } else {
            val allResults = state.marks!!.results
            val subjects = allResults.map { it.subject }.distinct()
            val filteredResults = if (selectedFilter == "All") allResults else allResults.filter { it.subject == selectedFilter }

            // Filter chips
            if (subjects.size > 1) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(listOf("All") + subjects) { label ->
                        VFilterChip(
                            label = label,
                            active = label == selectedFilter,
                            onClick = { selectedFilter = label },
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            filteredResults.forEach { result ->
                MarkRow(result.subject, result.marks ?: 0.0, result.maxMarks.toDouble(), result.examName)
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

// ── Syllabus Tab ─────────────────────────────────────────────────────────────

@Composable
private fun SyllabusTab(state: ParentAcademicsState, viewModel: ParentAcademicsViewModel) {
    var expandedSubject by remember { mutableStateOf<String?>(null) }
    VSectionHeader("Syllabus Coverage")
    Column(Modifier.padding(horizontal = 20.dp)) {
        if (state.syllabusLoading) {
            SkeletonCard(variant = "list")
            Spacer(Modifier.height(10.dp))
            SkeletonCard(variant = "list")
        } else if (state.syllabusError != null) {
            ErrorStateCard(message = state.syllabusError!!, onRetry = { viewModel.loadSyllabus() })
        } else if (state.syllabus == null) {
            EmptyState("No syllabus data available")
        } else {
            val syllabus = state.syllabus!!
            val avgProgress = if (syllabus.subjects.isNotEmpty()) syllabus.subjects.map { it.progress }.average() else 0.0
            InfoCard("Overall Coverage", "${avgProgress.toInt()}%")
            Spacer(Modifier.height(12.dp))
            VProgressBar(progress = (avgProgress / 100f).toFloat(), modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(20.dp))
            syllabus.subjects.forEach { subj ->
                ExpandableSyllabusRow(
                    subject = subj.subject,
                    coverage = subj.progress.toDouble(),
                    units = subj.units.map { it.title },
                    isExpanded = expandedSubject == subj.subject,
                    onToggle = {
                        expandedSubject = if (expandedSubject == subj.subject) null else subj.subject
                    },
                )
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

// ── Homework Tab ─────────────────────────────────────────────────────────────

@Composable
private fun HomeworkTab(state: ParentAcademicsState, viewModel: ParentAcademicsViewModel) {
    VSectionHeader("Homework")
    Column(Modifier.padding(horizontal = 20.dp)) {
        if (state.dailySummaryLoading) {
            LoadingState()
        } else if (state.dailySummary == null) {
            EmptyState("No homework data available")
        } else {
            val summary = state.dailySummary!!
            if (summary.entries.isEmpty()) {
                EmptyState("No homework assigned")
            } else {
                summary.entries.forEach { entry ->
                    HomeworkRow(entry.subject, entry.summaryText, entry.date, if (entry.coveragePct >= 100) "Done" else "Pending")
                    Spacer(Modifier.height(10.dp))
                }
            }
        }
    }
}

// ── Quizzes Tab ──────────────────────────────────────────────────────────────

@Composable
private fun QuizzesTab(
    state: ParentAcademicsState,
    viewModel: ParentAcademicsViewModel,
    onOpenQuizDetail: (String) -> Unit,
) {
    VSectionHeader("Quizzes")
    Column(Modifier.padding(horizontal = 20.dp)) {
        if (state.quizzesLoading) {
            SkeletonCard(variant = "list")
            Spacer(Modifier.height(10.dp))
            SkeletonCard(variant = "list")
        } else if (state.quizzesError != null) {
            ErrorStateCard(message = state.quizzesError!!, onRetry = { viewModel.loadQuizzes() })
        } else if (state.quizzes.isEmpty()) {
            EmptyStateCard(
                title = "No Quizzes Available",
                body = "Quizzes will appear here when assigned by the teacher.",
                icon = Icons.Filled.Quiz,
            )
        } else {
            state.quizzes.forEach { quiz ->
                QuizRow(quiz, onClick = { onOpenQuizDetail(quiz.id) })
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun QuizRow(quiz: ParentQuizDto, onClick: () -> Unit) {
    val isCompleted = quiz.status.equals("COMPLETED", ignoreCase = true)
    val statusColor = if (isCompleted) VColors.Tertiary else VColors.WarmOrange
    val statusBg = if (isCompleted) VColors.TertiaryContainer else VColors.WarmOrangeContainer
    val statusFg = if (isCompleted) VColors.OnTertiaryContainer else VColors.WarmOrange
    val interaction = remember { MutableInteractionSource() }
    Row(
        Modifier.fillMaxWidth().clip(VShapes.Xl).background(VColors.SurfaceContainerLowest)
            .pressScale(interaction, pressedScale = 0.98f)
            .shapeMorph(interaction, VShapes.XlDp, VShapes.TwoXlDp, VMotion.DurShort2)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(44.dp).clip(VShapes.Md).background(statusBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Filled.Quiz,
                contentDescription = null,
                tint = statusColor,
                modifier = Modifier.size(22.dp),
            )
        }
        Column(Modifier.weight(1f)) {
            Text(quiz.title.ifBlank { "Quiz" }, style = VTypography.HwTitle.copy(color = VColors.OnSurface))
            Text("${quiz.subject}${if (quiz.unitTitle.isNotBlank()) " · ${quiz.unitTitle}" else ""}", style = VTypography.HwSub.copy(color = VColors.OnSurfaceVariant))
            Spacer(Modifier.height(2.dp))
            Text("${quiz.numQuestions} questions · ${quiz.totalMarks} marks", style = VTypography.HwSub.copy(color = VColors.OnSurfaceVariant))
        }
        Text(
            quiz.status.uppercase(),
            style = VTypography.HwStatus.copy(color = statusFg),
            modifier = Modifier.clip(VShapes.Full).background(statusBg).padding(horizontal = 12.dp, vertical = 6.dp),
        )
    }
}

// ── Report Tab ───────────────────────────────────────────────────────────────

@Composable
private fun ReportTab(state: ParentAcademicsState) {
    Column(Modifier.padding(horizontal = 20.dp)) {
        if (state.marks == null || state.marks!!.results.isEmpty()) {
            EmptyState("No report card data available")
        } else {
            val results = state.marks!!.results

            // Report card with subject bars + grades (matching HTML)
            Column(
                Modifier.fillMaxWidth().clip(VShapes.Xl).background(VColors.SurfaceContainerLowest).padding(24.dp),
            ) {
                Text("Term Report Card", style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.Bold))
                Spacer(Modifier.height(16.dp))
                results.forEach { result ->
                    val pct = if (result.maxMarks > 0) ((result.marks ?: 0.0) / result.maxMarks * 100).toInt() else 0
                    val grade = when {
                        pct >= 90 -> "A+"
                        pct >= 80 -> "A"
                        pct >= 70 -> "B+"
                        pct >= 60 -> "B"
                        else -> "C"
                    }
                    val barColor = when {
                        pct >= 85 -> VColors.Primary
                        pct >= 60 -> VColors.Tertiary
                        else -> VColors.WarmOrange
                    }
                    Column(Modifier.fillMaxWidth()) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(result.subject, style = VTypography.SyllabusName.copy(color = VColors.OnSurface))
                            Text("$pct% · $grade", style = VTypography.SyllabusPct.copy(color = barColor))
                        }
                        Spacer(Modifier.height(8.dp))
                        Box(
                            Modifier.fillMaxWidth().height(6.dp).clip(VShapes.Full).background(VColors.SurfaceContainerHigh),
                        ) {
                            Box(
                                Modifier.fillMaxWidth(pct / 100f).height(6.dp).clip(VShapes.Full).background(barColor),
                            )
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                }
            }
            Spacer(Modifier.height(16.dp))

            // Teacher Remarks card
            val remarks = state.dailySummary?.aiSummary ?: "No remarks available."
            Column(
                Modifier.fillMaxWidth().clip(VShapes.Xl).background(VColors.SurfaceContainerLowest).padding(24.dp),
            ) {
                Text("Teacher Remarks", style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.Bold))
                Spacer(Modifier.height(12.dp))
                Text(remarks, style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
            }
        }
    }
}

// ── Helpers ──────────────────────────────────────────────────────────────────

private fun buildAttendanceCalendarDays(
    att: com.littlebridge.enrollplus.feature.parent.domain.model.ParentAttendanceData,
): List<AttendanceCalendarDay> {
    return att.records.map { rec ->
        val status = when (rec.status.lowercase()) {
            "present" -> AttendanceCellStatus.Present
            "absent" -> AttendanceCellStatus.Absent
            "late" -> AttendanceCellStatus.Late
            else -> AttendanceCellStatus.NoData
        }
        val dayOfMonth = rec.date.substringAfterLast("-").toIntOrNull() ?: 1
        AttendanceCalendarDay(dayOfMonth = dayOfMonth, status = status, isToday = false)
    }
}

@Composable
private fun ExpandableSyllabusRow(
    subject: String,
    coverage: Double,
    units: List<String>,
    isExpanded: Boolean,
    onToggle: () -> Unit,
) {
    val coverageColor = when {
        coverage >= 75 -> VColors.Tertiary
        coverage >= 50 -> VColors.Primary
        else -> VColors.WarmOrange
    }
    val interaction = remember { MutableInteractionSource() }
    Column(
        Modifier.fillMaxWidth().clip(VShapes.Xl).background(VColors.SurfaceContainerLowest)
            .pressScale(interaction, pressedScale = 0.98f)
            .clickable(interactionSource = interaction, indication = null, onClick = onToggle)
            .padding(16.dp),
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(subject, style = VTypography.SyllabusName.copy(color = VColors.OnSurface))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("${coverage.toInt()}%", style = VTypography.SyllabusPct.copy(color = coverageColor))
                Icon(
                    if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = VColors.OnSurfaceVariant,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        Box(
            Modifier.fillMaxWidth().height(6.dp).clip(VShapes.Full).background(VColors.SurfaceContainerHigh),
        ) {
            Box(
                Modifier.fillMaxWidth((coverage / 100f).toFloat()).height(6.dp).clip(VShapes.Full)
                    .background(coverageColor),
            )
        }
        if (isExpanded && units.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            units.forEach { unit ->
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(Modifier.size(6.dp).clip(VShapes.Full).background(coverageColor.copy(alpha = 0.5f)))
                    Text(unit, style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant))
                }
            }
        }
    }
}

// ── Reusable UI elements ─────────────────────────────────────────────────────

@Composable
private fun SubjectBreakdownRow(
    name: String,
    pct: Int,
    barColor: Color = VColors.Primary,
) {
    Column(
        Modifier.fillMaxWidth().clip(VShapes.Xl).background(VColors.SurfaceContainerLowest).padding(16.dp),
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(name, style = VTypography.SyllabusName.copy(color = VColors.OnSurface))
            Text("$pct%", style = VTypography.SyllabusPct.copy(color = barColor))
        }
        Spacer(Modifier.height(10.dp))
        Box(
            Modifier.fillMaxWidth().height(6.dp).clip(VShapes.Full).background(VColors.SurfaceContainerHigh),
        ) {
            Box(
                Modifier.fillMaxWidth(pct / 100f).height(6.dp).clip(VShapes.Full).background(barColor),
            )
        }
    }
}

@Composable
private fun SubjectBreakdownRow(
    name: String,
    value: Int,
    total: Int,
    barColor: Color = VColors.Primary,
) {
    val pct = if (total > 0) (value * 100 / total) else 0
    Column(
        Modifier.fillMaxWidth().clip(VShapes.Xl).background(VColors.SurfaceContainerLowest).padding(16.dp),
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(name, style = VTypography.SyllabusName.copy(color = VColors.OnSurface))
            Text("$value", style = VTypography.SyllabusPct.copy(color = barColor))
        }
        Spacer(Modifier.height(10.dp))
        Box(
            Modifier.fillMaxWidth().height(6.dp).clip(VShapes.Full).background(VColors.SurfaceContainerHigh),
        ) {
            Box(
                Modifier.fillMaxWidth(pct / 100f).height(6.dp).clip(VShapes.Full).background(barColor),
            )
        }
    }
}

@Composable
private fun StatPill(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier
            .clip(VShapes.Lg)
            .background(color.copy(alpha = 0.12f))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(value, style = VTypography.StatValue.copy(color = color, fontSize = 24.sp))
        Spacer(Modifier.height(4.dp))
        Text(label, style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant))
    }
}

@Composable
private fun InfoCard(label: String, value: String) {
    Row(
        Modifier.fillMaxWidth().clip(VShapes.Lg).background(VColors.SurfaceContainerLow).padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
        Text(value, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold))
    }
}

@Composable
private fun MarkRow(subject: String, score: Double, maxScore: Double, examName: String) {
    val pct = if (maxScore > 0) (score / maxScore * 100).toInt() else 0
    val scoreColor = when {
        pct >= 85 -> VColors.Tertiary
        pct >= 60 -> VColors.Primary
        else -> VColors.WarmOrange
    }
    Row(
        Modifier.fillMaxWidth().clip(VShapes.Xl).background(VColors.SurfaceContainerLowest).padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(subject, style = VTypography.MarkName.copy(color = VColors.OnSurface))
            Text(examName, style = VTypography.MarkDate.copy(color = VColors.OnSurfaceVariant))
        }
        Column(horizontalAlignment = Alignment.End) {
            Text("$score", style = VTypography.MarkScoreVal.copy(color = scoreColor))
            Text("/ $maxScore", style = VTypography.MarkScoreMax.copy(color = VColors.OnSurfaceVariant))
        }
    }
}

@Composable
private fun HomeworkRow(subject: String, description: String, dueDate: String, status: String) {
    val isDone = status.lowercase() in listOf("done", "completed", "submitted")
    val statusColor = if (isDone) VColors.Tertiary else VColors.WarmOrange
    val statusBg = if (isDone) VColors.TertiaryContainer else VColors.WarmOrangeContainer
    val statusFg = if (isDone) VColors.OnTertiaryContainer else VColors.WarmOrange
    val iconBg = if (isDone) VColors.TertiaryContainer else VColors.WarmOrangeContainer
    val iconColor = if (isDone) VColors.Tertiary else VColors.WarmOrange
    Row(
        Modifier.fillMaxWidth().clip(VShapes.Xl).background(VColors.SurfaceContainerLowest)
            .padding(horizontal = 20.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(44.dp).clip(VShapes.Md).background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                if (isDone) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(22.dp),
            )
        }
        Column(Modifier.weight(1f)) {
            Text(subject, style = VTypography.HwTitle.copy(color = VColors.OnSurface))
            Text(description, style = VTypography.HwSub.copy(color = VColors.OnSurfaceVariant))
            Spacer(Modifier.height(2.dp))
            Text("Due: $dueDate", style = VTypography.HwSub.copy(color = VColors.OnSurfaceVariant))
        }
        Text(
            status.uppercase(),
            style = VTypography.HwStatus.copy(color = statusFg),
            modifier = Modifier.clip(VShapes.Full).background(statusBg).padding(horizontal = 12.dp, vertical = 6.dp),
        )
    }
}

@Composable
private fun LoadingState() {
    SkeletonCard(variant = "list")
}

@Composable
private fun ErrorState(message: String, onRetry: (() -> Unit)? = null) {
    ErrorStateCard(message = message, onRetry = onRetry, modifier = Modifier.padding(vertical = 8.dp))
}

@Composable
private fun EmptyState(message: String) {
    EmptyStateCard(
        title = message,
        icon = Icons.AutoMirrored.Filled.MenuBook,
        modifier = Modifier.padding(vertical = 8.dp),
    )
}

