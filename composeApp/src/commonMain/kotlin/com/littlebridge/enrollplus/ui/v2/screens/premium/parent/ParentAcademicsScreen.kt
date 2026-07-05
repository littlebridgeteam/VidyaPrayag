package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
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
import com.littlebridge.enrollplus.feature.parent.presentation.ParentAcademicsState
import com.littlebridge.enrollplus.feature.parent.presentation.ParentAcademicsViewModel
import com.littlebridge.enrollplus.feature.parent.presentation.TrackProgressViewModel
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
 * Premium parent academics — matches parent-portal.html Academics tab.
 * Action cards (leave, health), 6 sub-tabs (Overview, Attendance, Marks,
 * Syllabus, Homework, Report), progress ring cards, subject breakdowns.
 */
@Composable
fun ParentAcademicsScreen(
    modifier: Modifier = Modifier,
    viewModel: ParentAcademicsViewModel = koinViewModel(),
    trackProgressViewModel: TrackProgressViewModel = koinViewModel(),
    onOpenLeave: () -> Unit = {},
    onOpenHealth: () -> Unit = {},
) = PremiumTheme(isDark = false) {
    val state by viewModel.state.collectAsStateV2()
    val tabs = listOf("Overview", "Attendance", "Marks", "Syllabus", "Homework", "Report")
    var selectedTab by remember { mutableStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VColors.Surface)
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(Modifier.height(16.dp))

        // ── Action Cards (Leave + Health) ──
        Row(
            Modifier.padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ActionCard(
                icon = { Icon(Icons.Filled.CalendarToday, contentDescription = null, tint = VColors.OnError, modifier = Modifier.size(20.dp)) },
                title = "Apply Leave",
                subtitle = "Submit a leave request",
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

        Spacer(Modifier.height(24.dp))

        // ── Sub-tab selector (primary-container active style) ──
        Row(
            Modifier.padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            tabs.forEachIndexed { index, label ->
                VFilterChip(
                    label = label,
                    active = selectedTab == index,
                    onClick = { selectedTab = index },
                    activeBg = VColors.PrimaryContainer,
                    activeFg = VColors.OnPrimaryContainer,
                    activeFontWeight = FontWeight.Bold,
                    fontSize = 13,
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // ── Child selector ──
        if (state.children.size > 1) {
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

        // ── Tab content ──
        when (selectedTab) {
            0 -> OverviewTab(state)
            1 -> AttendanceTab(state)
            2 -> MarksTab(state)
            3 -> SyllabusTab(state)
            4 -> HomeworkTab(state)
            5 -> ReportTab(state)
        }

        Spacer(Modifier.height(24.dp))
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
    val trackColor = VColors.TertiaryContainer
    val trackColorResolved = trackColor
    val ringColorResolved = ringColor
    val sweep = (score.toFloat() / maxScore.toFloat()) * 360f

    Column(
        modifier
            .clip(VShapes.Xl)
            .background(VColors.SurfaceContainerLowest)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
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
            Text("$score", style = VTypography.StatValue.copy(color = ringColorResolved, fontSize = 22.sp))
        }
        Spacer(Modifier.height(16.dp))
        Text(title, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.Bold))
        Spacer(Modifier.height(4.dp))
        Text(subtitle, style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
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

    VSectionHeader("Academic Overview")
    Column(Modifier.padding(horizontal = 20.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ProgressRingCard(
                title = "Overall Progress",
                score = child.overallProgress.toInt(),
                subtitle = "Level ${child.currentLevel}",
                modifier = Modifier.weight(1f),
            )
            ProgressRingCard(
                title = "Attendance",
                score = state.attendance?.attendanceRate ?: 0,
                subtitle = "${state.attendance?.presentDays ?: 0} days present",
                modifier = Modifier.weight(1f),
            )
        }
    }
}

// ── Attendance Tab ───────────────────────────────────────────────────────────

@Composable
private fun AttendanceTab(state: ParentAcademicsState) {
    VSectionHeader("Attendance")
    Column(Modifier.padding(horizontal = 20.dp)) {
        if (state.attendanceLoading) {
            LoadingState()
        } else if (state.attendanceError != null) {
            ErrorState(state.attendanceError!!)
        } else if (state.attendance == null) {
            EmptyState("No attendance data available")
        } else {
            val att = state.attendance!!
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                StatPill("Present", att.presentDays.toString(), VColors.Tertiary, Modifier.weight(1f))
                StatPill("Absent", att.absentDays.toString(), VColors.Error, Modifier.weight(1f))
                StatPill("Late", att.lateDays.toString(), VColors.WarmOrange, Modifier.weight(1f))
            }
            Spacer(Modifier.height(16.dp))
            InfoCard("Attendance Rate", "${att.attendanceRate}%")
            Spacer(Modifier.height(12.dp))
            VProgressBar(progress = att.attendanceRate / 100f, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(16.dp))
            InfoCard("Total Days", att.totalDays.toString())
        }
    }
}

// ── Marks Tab ────────────────────────────────────────────────────────────────

@Composable
private fun MarksTab(state: ParentAcademicsState) {
    VSectionHeader("Latest Marks")
    Column(Modifier.padding(horizontal = 20.dp)) {
        if (state.marksLoading) {
            LoadingState()
        } else if (state.marksError != null) {
            ErrorState(state.marksError!!)
        } else if (state.marks == null || state.marks!!.results.isEmpty()) {
            EmptyState("No marks published yet")
        } else {
            state.marks!!.results.forEach { result ->
                MarkRow(result.subject, result.marks ?: 0.0, result.maxMarks.toDouble(), result.examName)
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

// ── Syllabus Tab ─────────────────────────────────────────────────────────────

@Composable
private fun SyllabusTab(state: ParentAcademicsState) {
    VSectionHeader("Syllabus Coverage")
    Column(Modifier.padding(horizontal = 20.dp)) {
        if (state.syllabusLoading) {
            LoadingState()
        } else if (state.syllabusError != null) {
            ErrorState(state.syllabusError!!)
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
                SyllabusRow(subj.subject, subj.units.firstOrNull()?.title ?: "", subj.progress.toDouble())
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

// ── Homework Tab ─────────────────────────────────────────────────────────────

@Composable
private fun HomeworkTab(state: ParentAcademicsState) {
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

// ── Report Tab ───────────────────────────────────────────────────────────────

@Composable
private fun ReportTab(state: ParentAcademicsState) {
    VSectionHeader("Report Card")
    Column(Modifier.padding(horizontal = 20.dp)) {
        if (state.marks == null || state.marks!!.results.isEmpty()) {
            EmptyState("No report card data available")
        } else {
            val results = state.marks!!.results
            val avgScore = results.mapNotNull { it.marks }.let { marks ->
                if (marks.isNotEmpty()) marks.average() else 0.0
            }
            val avgMax = results.map { it.maxMarks.toDouble() }.let { maxes ->
                if (maxes.isNotEmpty()) maxes.average() else 100.0
            }
            val avgPct = if (avgMax > 0) (avgScore / avgMax * 100).toInt() else 0

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                ProgressRingCard(
                    title = "Overall Average",
                    score = avgPct,
                    subtitle = "${results.size} subjects",
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(20.dp))
            results.forEach { result ->
                MarkRow(result.subject, result.marks ?: 0.0, result.maxMarks.toDouble(), result.examName)
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

// ── Reusable UI elements ─────────────────────────────────────────────────────

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
private fun SyllabusRow(subject: String, topic: String, coverage: Double) {
    val coverageColor = when {
        coverage >= 75 -> VColors.Tertiary
        coverage >= 50 -> VColors.Primary
        else -> VColors.WarmOrange
    }
    Column(
        Modifier.fillMaxWidth().clip(VShapes.Xl).background(VColors.SurfaceContainerLowest).padding(16.dp),
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(subject, style = VTypography.SyllabusName.copy(color = VColors.OnSurface))
            Text("${coverage.toInt()}%", style = VTypography.SyllabusPct.copy(color = coverageColor))
        }
        if (topic.isNotBlank()) {
            Spacer(Modifier.height(4.dp))
            Text(topic, style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant))
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
    val interaction = remember { MutableInteractionSource() }
    Row(
        Modifier.fillMaxWidth().clip(VShapes.Xl).background(VColors.SurfaceContainerLowest)
            .pressScale(interaction, pressedScale = 0.98f)
            .shapeMorph(interaction, VShapes.XlDp, VShapes.TwoXlDp, VMotion.DurShort2)
            .clickable(interactionSource = interaction, indication = null) { }
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
    Box(Modifier.fillMaxWidth().height(120.dp).clip(VShapes.Lg).background(VColors.SurfaceContainerLow), contentAlignment = Alignment.Center) {
        Text("Loading...", style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
    }
}

@Composable
private fun ErrorState(message: String) {
    Box(Modifier.fillMaxWidth().height(120.dp).clip(VShapes.Lg).background(VColors.ErrorContainer), contentAlignment = Alignment.Center) {
        Text(message, style = VTypography.UpdateText.copy(color = VColors.OnErrorContainer))
    }
}

@Composable
private fun EmptyState(message: String) {
    Box(Modifier.fillMaxWidth().height(120.dp).clip(VShapes.Lg).background(VColors.SurfaceContainerLow), contentAlignment = Alignment.Center) {
        Text(message, style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
    }
}

