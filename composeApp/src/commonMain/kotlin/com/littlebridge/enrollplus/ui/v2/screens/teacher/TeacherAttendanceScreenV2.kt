package com.littlebridge.enrollplus.ui.v2.screens.teacher

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.feature.teacher.domain.model.AtRiskStudentDto
import com.littlebridge.enrollplus.feature.teacher.domain.model.AttendanceAnalyticsDto
import com.littlebridge.enrollplus.feature.teacher.domain.model.DailyAttendanceDto
import com.littlebridge.enrollplus.feature.teacher.domain.model.WeeklyTrendDto
import com.littlebridge.enrollplus.feature.teacher.presentation.AttendanceStatus
import com.littlebridge.enrollplus.feature.teacher.presentation.StudentAttendance
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherAttendanceAnalyticsViewModel
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherAttendanceViewModel
import com.littlebridge.enrollplus.ui.v2.components.VAvatar
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonSize
import com.littlebridge.enrollplus.ui.v2.components.VButtonTone
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VDatePicker
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import org.koin.compose.viewmodel.koinViewModel

/**
 * TeacherAttendanceScreenV2 — the scoped attendance plane (Doc 06 §3). Reached PRE-SCOPED with a
 * pre-authorized [assignmentId] from the Update scope gate or a Home/Classes CTA. It loads the typed
 * roster, defaults the date to today (correctable), pre-sets approved-leave students to "leave"
 * (locked), supports the 4-state space (present · absent · late · leave), a bulk "mark all present",
 * a live running counter, and a result-driven Save that NEVER auto-publishes.
 */
@Composable
fun TeacherAttendanceScreenV2(
    assignmentId: String,
    scopeLabel: String,
    modifier: Modifier = Modifier,
    viewModel: TeacherAttendanceViewModel = koinViewModel(),
    analyticsViewModel: TeacherAttendanceAnalyticsViewModel = koinViewModel(),
    onOpenMessages: () -> Unit = {},
) {
    val state by viewModel.state.collectAsStateV2()
    var showInsights by remember { mutableStateOf(false) }

    LaunchedEffect(assignmentId) {
        if (assignmentId.isNotBlank() && state.assignmentId != assignmentId) viewModel.load(assignmentId)
    }

    LaunchedEffect(showInsights, assignmentId) {
        if (showInsights && assignmentId.isNotBlank() && analyticsViewModel.state.value.assignmentId != assignmentId) {
            analyticsViewModel.load(assignmentId)
        }
    }

    Box(modifier.fillMaxSize().background(VColors.cream)) {
        when {
            state.isLoading && state.students.isEmpty() && !showInsights -> VtCenterState { TeacherSpinner() }
            state.error != null && state.students.isEmpty() && !showInsights -> VtErrorState(
                title = appString(StringKeys.TC_COULDNT_LOAD_ATTENDANCE),
                detail = state.error,
                retryLabel = appString(StringKeys.COMMON_BUTTON_RETRY),
                onRetry = { viewModel.retry() },
            )
            showInsights -> AttendanceInsightsBody(analyticsViewModel, scopeLabel, assignmentId) { showInsights = false }
            else -> AttendanceBody(state.students, viewModel, scopeLabel, { showInsights = true }, onOpenMessages)
        }
    }
}

@Composable
private fun AttendanceBody(
    students: List<StudentAttendance>,
    viewModel: TeacherAttendanceViewModel,
    scopeLabel: String,
    onShowInsights: () -> Unit,
    onOpenMessages: () -> Unit = {},
) {
    val state by viewModel.state.collectAsStateV2()

    LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 14.dp, bottom = TeacherDockClearance),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // ── Scope + date + running counter header ──
        item {
            VtCard(padding = 16.dp) {
                Column {
                    VtEyebrow(appString(StringKeys.TC_MARKING_ATTENDANCE), dot = VColors.violet)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        scopeLabel.ifBlank { "${state.className}-${state.section} · ${state.subject}" },
                        style = VTypography.h3.copy(fontSize = 18.sp, color = VColors.ink, fontWeight = FontWeight.ExtraBold),
                    )
                    Spacer(Modifier.height(12.dp))
                    VDatePicker(
                        value = state.date,
                        onValueChange = { viewModel.changeDate(it) },
                        label = appString(StringKeys.SCH_DATE),
                    )
                    if (state.isHoliday || state.isCancelled) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            if (state.isHoliday) appString(StringKeys.TC_HOLIDAY_NOTICE, "name" to (state.holidayName?.let { " — $it" } ?: "")) else appString(StringKeys.TC_CLASS_CANCELLED_DATE),
                            style = VTypography.caption.copy(fontSize = 12.sp, color = VColors.gold),
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        VtMetricTile(state.presentCount.toString(), appString(StringKeys.ATT_PRESENT), VColors.success, Modifier.weight(1f))
                        VtMetricTile(state.absentCount.toString(), appString(StringKeys.ATT_ABSENT), VColors.coral, Modifier.weight(1f))
                        VtMetricTile(state.lateCount.toString(), appString(StringKeys.ATT_LATE), VColors.gold, Modifier.weight(1f))
                        VtMetricTile(state.leaveCount.toString(), appString(StringKeys.TEACHER_LEAVE), VColors.sky, Modifier.weight(1f))
                    }
                    if (state.alreadyMarked && state.lastMarkedBy != null) {
                        Spacer(Modifier.height(10.dp))
                        Text(
                            appString(StringKeys.TC_LAST_MARKED_BY, "name" to (state.lastMarkedBy ?: ""), "date" to (state.lastMarkedAt?.let { " · ${prettyDate(it.take(10))}" } ?: "")),
                            style = VTypography.caption.copy(fontSize = 11.sp, color = VColors.ink3),
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    VButton(
                        text = appString(StringKeys.TC_MARK_ALL_PRESENT),
                        onClick = { viewModel.markAllPresent() },
                        full = true,
                        variant = VButtonVariant.Secondary,
                        tone = VButtonTone.Mint,
                        size = VButtonSize.Md,
                        leading = { Icon(VIcons.Check, contentDescription = null, modifier = Modifier.size(15.dp)) },
                    )
                    Spacer(Modifier.height(8.dp))
                    VButton(
                        text = "Insights",
                        onClick = onShowInsights,
                        full = true,
                        variant = VButtonVariant.Ghost,
                        size = VButtonSize.Sm,
                        leading = { Icon(VIcons.Activity, contentDescription = null, modifier = Modifier.size(15.dp)) },
                    )
                }
            }
        }

        items(students, key = { it.studentId }) { s ->
            AttendanceStudentRow(s, onSetStatus = { status -> viewModel.setStatus(s.studentId, status) })
        }

        // ── Save footer ──
        item {
            Spacer(Modifier.height(4.dp))
            if (state.saveError != null) {
                Text(state.saveError ?: "", style = VTypography.caption.copy(fontSize = 12.sp, color = VColors.coral))
                Spacer(Modifier.height(8.dp))
            }
            VButton(
                text = if (state.alreadyMarked) appString(StringKeys.TC_UPDATE_ATTENDANCE) else appString(StringKeys.TC_SAVE_ATTENDANCE),
                onClick = { viewModel.save() },
                full = true,
                tone = VButtonTone.Lavender,
                size = VButtonSize.Lg,
                loading = state.isSaving,
                success = state.saveSuccess,
                successLabel = appString(StringKeys.TC_SAVED),
                stateful = true,
                enabled = students.isNotEmpty() && !state.isHoliday,
            )
        }

        // ── Notify absent parents banner (post-save) ──
        if (state.saveSuccess && state.absentCount > 0) {
            item {
                AbsentNotifyBanner(
                    absentCount = state.absentCount,
                    onNotify = onOpenMessages,
                )
            }
        }
    }
}

@Composable
private fun AttendanceStudentRow(s: StudentAttendance, onSetStatus: (String) -> Unit) {
    val locked = s.isOnApprovedLeave
    Column(
        Modifier
            .fillMaxWidth()
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.lg)
            .padding(12.dp),
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            VAvatar(name = s.name, size = 38.dp)
            Column(Modifier.weight(1f)) {
                Text(s.name, style = VTypography.bodySmall.copy(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = VColors.ink), maxLines = 1)
                Text(
                    if (locked) appString(StringKeys.TC_ROLL_ON_LEAVE, "no" to s.rollNo) else appString(StringKeys.TC_ROLL_NO, "no" to s.rollNo),
                    style = VTypography.caption.copy(fontSize = 11.sp, color = if (locked) VColors.sky else VColors.ink3),
                )
            }
        }
        // The 4-state segmented control sits on its own line under the identity for tap comfort.
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            StatusChip(appString(StringKeys.TC_P), AttendanceStatus.PRESENT, s.status, VColors.success, locked, onSetStatus, Modifier.weight(1f))
            StatusChip(appString(StringKeys.TC_A), AttendanceStatus.ABSENT, s.status, VColors.coral, locked, onSetStatus, Modifier.weight(1f))
            StatusChip(appString(StringKeys.ATT_LATE), AttendanceStatus.LATE, s.status, VColors.gold, locked, onSetStatus, Modifier.weight(1f))
            StatusChip(appString(StringKeys.TEACHER_LEAVE), AttendanceStatus.LEAVE, s.status, VColors.sky, locked, onSetStatus, Modifier.weight(1f))
        }
    }
}

@Composable
private fun StatusChip(
    label: String,
    status: String,
    current: String,
    tint: androidx.compose.ui.graphics.Color,
    locked: Boolean,
    onSet: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val active = current == status
    val ix = remember { MutableInteractionSource() }
    Box(
        modifier
            .clip(VShapes.md)
            .background(if (active) tint.copy(alpha = 0.16f) else VColors.creamDeep)
            .border(1.dp, if (active) tint.copy(alpha = 0.5f) else VColors.line, VShapes.md)
            .clickable(interactionSource = ix, indication = null, enabled = !locked) { onSet(status) }
            .padding(vertical = 9.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            style = VTypography.bodySmall.copy(
                fontSize = 12.5.sp,
                color = if (active) tint else VColors.ink2,
                fontWeight = if (active) FontWeight.ExtraBold else FontWeight.Medium,
            ),
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Absent Notify Banner — post-save CTA to message absent student parents.
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AbsentNotifyBanner(
    absentCount: Int,
    onNotify: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(VShapes.lg)
            .background(VColors.coral.copy(alpha = 0.06f))
            .border(1.dp, VColors.coral.copy(alpha = 0.2f), VShapes.lg)
            .padding(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                Modifier.size(36.dp).clip(androidx.compose.foundation.shape.CircleShape).background(VColors.coral.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(VIcons.Mail, contentDescription = null, tint = VColors.coral, modifier = Modifier.size(18.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(
                    "$absentCount student${if (absentCount > 1) "s" else ""} absent today",
                    style = VTypography.bodySmall.copy(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = VColors.ink),
                )
                Text(
                    "Notify parents about the absence via Messages.",
                    style = VTypography.caption.copy(fontSize = 12.sp, color = VColors.ink3),
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        VButton(
            text = "Notify Parents",
            onClick = onNotify,
            full = true,
            variant = VButtonVariant.Secondary,
            tone = VButtonTone.Rose,
            size = VButtonSize.Sm,
            leading = { Icon(VIcons.Send, contentDescription = null, modifier = Modifier.size(14.dp)) },
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// ATTENDANCE INSIGHTS — Analytics dashboard
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AttendanceInsightsBody(
    viewModel: TeacherAttendanceAnalyticsViewModel,
    scopeLabel: String,
    assignmentId: String,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateV2()
    val a = state.analytics

    LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 14.dp, bottom = TeacherDockClearance),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Header with back button
        item {
            VtCard(padding = 16.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val ix = remember { MutableInteractionSource() }
                    Box(
                        Modifier.size(34.dp).clip(androidx.compose.foundation.shape.CircleShape).background(VColors.creamDeep)
                            .clickable(interactionSource = ix, indication = null) { onBack() },
                        contentAlignment = Alignment.Center,
                    ) { Icon(VIcons.ArrowLeft, contentDescription = "Back", tint = VColors.ink2, modifier = Modifier.size(18.dp)) }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        VtEyebrow("Attendance Insights", dot = VColors.violet)
                        Spacer(Modifier.height(2.dp))
                        Text(
                            scopeLabel.ifBlank { "${a?.className}-${a?.section} · ${a?.subject}" },
                            style = VTypography.h3.copy(fontSize = 16.sp, color = VColors.ink, fontWeight = FontWeight.ExtraBold),
                            maxLines = 1,
                        )
                    }
                }
            }
        }

        if (state.isLoading && a == null) {
            item { Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) { TeacherSpinner() } }
        } else if (state.error != null && a == null) {
            item {
                VtCard {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text("Couldn't load analytics", style = VTypography.h3.copy(color = VColors.ink))
                        Spacer(Modifier.height(8.dp))
                        VButton("Retry", onClick = { viewModel.retry() }, tone = VButtonTone.Lavender, size = VButtonSize.Sm)
                    }
                }
            }
        } else if (a == null || a.totalMarkedDays == 0) {
            item {
                VtCard {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        VtIconDisc(VIcons.Activity, tint = VColors.violet, bg = VColors.violet.copy(alpha = 0.14f), size = 48.dp, glyph = 24.dp)
                        Spacer(Modifier.height(10.dp))
                        Text("No attendance data yet", style = VTypography.h3.copy(color = VColors.ink))
                        Spacer(Modifier.height(4.dp))
                        Text("Start marking attendance to see insights", style = VTypography.caption.copy(fontSize = 12.sp, color = VColors.ink3))
                        Spacer(Modifier.height(12.dp))
                        VButton("Mark Attendance", onClick = onBack, tone = VButtonTone.Lavender, size = VButtonSize.Sm)
                    }
                }
            }
        } else {
            // Hero ring + overall stats
            item { AnalyticsHeroCard(a) }

            // Status breakdown
            item { AnalyticsStatusBreakdown(a) }

            // Weekly trend bar chart
            if (a.weeklyTrend.isNotEmpty()) {
                item { AnalyticsWeeklyTrendCard(a.weeklyTrend, a.trendDirection) }
            }

            // At-risk students
            if (a.atRiskStudents.isNotEmpty()) {
                item { AnalyticsAtRiskHeader(a.atRiskStudents.size) }
                items(a.atRiskStudents, key = { it.studentId }) { student ->
                    AtRiskStudentRow(student) { viewModel.loadStudentAnalytics(student.studentId) }
                }
            }

            // Daily heatmap (last 30 days)
            if (a.dailyBreakdown.isNotEmpty()) {
                item { AnalyticsHeatmapCard(a.dailyBreakdown) }
            }
        }

        // Student detail drill-down
        if (state.selectedStudentId != null && state.studentAnalytics != null) {
            item { StudentDetailCard(state.studentAnalytics!!, onClose = { viewModel.closeStudentDetail() }) }
        }
    }
}

@Composable
private fun AnalyticsHeroCard(a: AttendanceAnalyticsDto) {
    VtCard(padding = 16.dp) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            // Ring
            AnalyticsRing(a.overallPercentage, Modifier.size(100.dp))
            // Stats
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("${a.totalMarkedDays} days marked", style = VTypography.body.copy(fontSize = 14.sp, color = VColors.ink, fontWeight = FontWeight.Bold))
                Text("${a.totalStudents} students in class", style = VTypography.caption.copy(fontSize = 12.sp, color = VColors.ink3))
                val trendIcon = when (a.trendDirection) {
                    "up" -> VIcons.TrendingUp to VColors.success
                    "down" -> VIcons.TrendingDown to VColors.coral
                    else -> VIcons.Activity to VColors.ink3
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(trendIcon.first, contentDescription = null, tint = trendIcon.second, modifier = Modifier.size(14.dp))
                    Text(
                        when (a.trendDirection) { "up" -> "Trending up"; "down" -> "Trending down"; else -> "Stable" },
                        style = VTypography.caption.copy(fontSize = 12.sp, color = trendIcon.second, fontWeight = FontWeight.Bold),
                    )
                }
            }
        }
    }
}

@Composable
private fun AnalyticsRing(percent: Int, modifier: Modifier = Modifier) {
    val accent = when {
        percent >= 85 -> VColors.success
        percent >= 75 -> VColors.violet
        percent >= 60 -> VColors.gold
        else -> VColors.coral
    }
    val track = accent.copy(alpha = 0.15f)
    val sweep = (percent / 100f).coerceIn(0f, 1f)
    Box(modifier, contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val stroke = 8.dp.toPx()
            val inset = stroke / 2f
            val arcSize = Size(size.width - stroke, size.height - stroke)
            drawArc(track, 0f, 360f, false, Offset(inset, inset), arcSize, style = Stroke(width = stroke, cap = StrokeCap.Round))
            drawArc(
                color = accent,
                startAngle = -90f, sweepAngle = 360f * sweep, useCenter = false,
                topLeft = Offset(inset, inset), size = arcSize, style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
        }
        Text("$percent%", style = VTypography.h3.copy(fontSize = 22.sp, color = VColors.ink, fontWeight = FontWeight.ExtraBold))
    }
}

@Composable
private fun AnalyticsStatusBreakdown(a: AttendanceAnalyticsDto) {
    VtCard(padding = 16.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Status Breakdown", style = VTypography.body.copy(fontSize = 14.sp, color = VColors.ink, fontWeight = FontWeight.Bold))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                VtMetricTile(a.presentCount.toString(), "Present", VColors.success, Modifier.weight(1f))
                VtMetricTile(a.absentCount.toString(), "Absent", VColors.coral, Modifier.weight(1f))
                VtMetricTile(a.lateCount.toString(), "Late", VColors.gold, Modifier.weight(1f))
                VtMetricTile(a.leaveCount.toString(), "Leave", VColors.sky, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun AnalyticsWeeklyTrendCard(trend: List<WeeklyTrendDto>, direction: String) {
    VtCard(padding = 16.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Weekly Trend", style = VTypography.body.copy(fontSize = 14.sp, color = VColors.ink, fontWeight = FontWeight.Bold), modifier = Modifier.weight(1f))
                val dirColor = when (direction) { "up" -> VColors.success; "down" -> VColors.coral; else -> VColors.ink3 }
                val dirIcon = when (direction) { "up" -> VIcons.TrendingUp; "down" -> VIcons.TrendingDown; else -> VIcons.Activity }
                Icon(dirIcon, contentDescription = null, tint = dirColor, modifier = Modifier.size(16.dp))
            }
            // Bar chart
            val maxPct = 100
            Row(
                Modifier.fillMaxWidth().height(120.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                trend.forEach { w ->
                    val barHeight = (w.attendancePercentage.toFloat() / maxPct.toFloat()).coerceIn(0f, 1f)
                    val barColor = when {
                        w.attendancePercentage >= 85 -> VColors.success
                        w.attendancePercentage >= 75 -> VColors.violet
                        w.attendancePercentage >= 60 -> VColors.gold
                        else -> VColors.coral
                    }
                    Column(
                        Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Box(
                            Modifier.weight(1f).fillMaxWidth(),
                            contentAlignment = Alignment.BottomCenter,
                        ) {
                            Box(
                                Modifier
                                    .fillMaxWidth(barHeight.coerceAtLeast(0.02f))
                                    .height(120.dp * barHeight)
                                    .clip(VShapes.sm)
                                    .background(barColor.copy(alpha = 0.8f)),
                            )
                        }
                        Text(
                            w.week.takeLast(2),
                            style = VTypography.caption.copy(fontSize = 9.sp, color = VColors.ink3),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnalyticsAtRiskHeader(count: Int) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        VtIconDisc(VIcons.TrendingDown, tint = VColors.coral, bg = VColors.coral.copy(alpha = 0.12f), size = 32.dp, glyph = 16.dp)
        Text("At-Risk Students", style = VTypography.body.copy(fontSize = 15.sp, color = VColors.ink, fontWeight = FontWeight.ExtraBold))
        VtPill("$count", bg = VColors.coral.copy(alpha = 0.12f), fg = VColors.coral)
    }
}

@Composable
private fun AtRiskStudentRow(student: AtRiskStudentDto, onTap: () -> Unit) {
    val ix = remember { MutableInteractionSource() }
    VtCard(padding = 12.dp, onClick = onTap) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            VAvatar(name = student.name, size = 38.dp)
            Column(Modifier.weight(1f)) {
                Text(student.name, style = VTypography.bodySmall.copy(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = VColors.ink), maxLines = 1)
                Text("Roll ${student.rollNo} · ${student.absentDays} abs · ${student.lateDays} late", style = VTypography.caption.copy(fontSize = 11.sp, color = VColors.ink3))
            }
            // Mini percentage ring
            Box(Modifier.size(44.dp), contentAlignment = Alignment.Center) {
                val pct = student.attendancePercentage
                val color = if (pct >= 75) VColors.success else VColors.coral
                Canvas(Modifier.fillMaxSize()) {
                    val stroke = 4.dp.toPx()
                    val inset = stroke / 2f
                    val arcSize = Size(size.width - stroke, size.height - stroke)
                    drawArc(color.copy(alpha = 0.15f), 0f, 360f, false, Offset(inset, inset), arcSize, style = Stroke(width = stroke))
                    drawArc(color, -90f, 360f * (pct / 100f), false, Offset(inset, inset), arcSize, style = Stroke(width = stroke, cap = StrokeCap.Round))
                }
                Text("$pct%", style = VTypography.caption.copy(fontSize = 10.sp, color = color, fontWeight = FontWeight.ExtraBold))
            }
        }
    }
}

@Composable
private fun AnalyticsHeatmapCard(daily: List<DailyAttendanceDto>) {
    VtCard(padding = 16.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Last 30 Days", style = VTypography.body.copy(fontSize = 14.sp, color = VColors.ink, fontWeight = FontWeight.Bold))
            // Grid: 6 columns x 5 rows
            val rows = daily.chunked(6)
            rows.forEach { week ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    week.forEach { day ->
                        val color = when {
                            day.attendancePercentage >= 85 -> VColors.success.copy(alpha = 0.7f)
                            day.attendancePercentage >= 75 -> VColors.violet.copy(alpha = 0.5f)
                            day.attendancePercentage >= 60 -> VColors.gold.copy(alpha = 0.5f)
                            day.attendancePercentage > 0 -> VColors.coral.copy(alpha = 0.5f)
                            else -> VColors.creamDeep
                        }
                        Box(
                            Modifier.weight(1f).height(28.dp).clip(VShapes.sm).background(color),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                day.date.takeLast(2),
                                style = VTypography.caption.copy(fontSize = 8.sp, color = VColors.ink3),
                            )
                        }
                    }
                    // Fill remaining slots
                    repeat(6 - week.size) {
                        Box(Modifier.weight(1f).height(28.dp))
                    }
                }
            }
            // Legend
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                HeatmapLegend(VColors.success.copy(alpha = 0.7f), "≥85%")
                HeatmapLegend(VColors.violet.copy(alpha = 0.5f), "≥75%")
                HeatmapLegend(VColors.gold.copy(alpha = 0.5f), "≥60%")
                HeatmapLegend(VColors.coral.copy(alpha = 0.5f), "<60%")
            }
        }
    }
}

@Composable
private fun HeatmapLegend(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(Modifier.size(10.dp).clip(VShapes.sm).background(color))
        Text(label, style = VTypography.caption.copy(fontSize = 10.sp, color = VColors.ink3))
    }
}

@Composable
private fun StudentDetailCard(
    student: com.littlebridge.enrollplus.feature.teacher.domain.model.StudentAnalyticsDto,
    onClose: () -> Unit,
) {
    VtCard(padding = 16.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                VAvatar(name = student.name, size = 36.dp)
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(student.name, style = VTypography.body.copy(fontSize = 15.sp, color = VColors.ink, fontWeight = FontWeight.Bold))
                    Text("Roll ${student.rollNo}", style = VTypography.caption.copy(fontSize = 11.sp, color = VColors.ink3))
                }
                val ix = remember { MutableInteractionSource() }
                Box(
                    Modifier.size(28.dp).clip(androidx.compose.foundation.shape.CircleShape).background(VColors.creamDeep)
                        .clickable(interactionSource = ix, indication = null) { onClose() },
                    contentAlignment = Alignment.Center,
                ) { Icon(VIcons.Close, contentDescription = "Close", tint = VColors.ink3, modifier = Modifier.size(14.dp)) }
            }
            // Summary stats
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                VtMetricTile("${student.attendancePercentage}%", "Attendance", VColors.violet, Modifier.weight(1f))
                VtMetricTile(student.presentDays.toString(), "Present", VColors.success, Modifier.weight(1f))
                VtMetricTile(student.absentDays.toString(), "Absent", VColors.coral, Modifier.weight(1f))
                VtMetricTile(student.lateDays.toString(), "Late", VColors.gold, Modifier.weight(1f))
            }
            // History list
            if (student.history.isNotEmpty()) {
                Text("Recent History", style = VTypography.body.copy(fontSize = 13.sp, color = VColors.ink, fontWeight = FontWeight.Bold))
                student.history.takeLast(10).forEach { day ->
                    val statusColor = when {
                        day.presentCount > 0 -> VColors.success
                        day.lateCount > 0 -> VColors.gold
                        day.absentCount > 0 -> VColors.coral
                        else -> VColors.sky
                    }
                    val statusLabel = when {
                        day.presentCount > 0 -> "Present"
                        day.lateCount > 0 -> "Late"
                        day.absentCount > 0 -> "Absent"
                        else -> "Leave"
                    }
                    Row(
                        Modifier.fillMaxWidth().clip(VShapes.sm).background(statusColor.copy(alpha = 0.06f)).padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Box(Modifier.size(6.dp).clip(androidx.compose.foundation.shape.CircleShape).background(statusColor))
                        Text(prettyDate(day.date), style = VTypography.caption.copy(fontSize = 12.sp, color = VColors.ink2), modifier = Modifier.weight(1f))
                        Text(statusLabel, style = VTypography.caption.copy(fontSize = 11.sp, color = statusColor, fontWeight = FontWeight.Bold))
                    }
                }
            }
        }
    }
}
