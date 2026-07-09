package com.littlebridge.enrollplus.ui.v2.screens.teacher

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.feature.teacher.domain.model.ClassAssessmentDto
import com.littlebridge.enrollplus.feature.teacher.domain.model.ClassDetailData
import com.littlebridge.enrollplus.feature.teacher.domain.model.ClassHomeworkDto
import com.littlebridge.enrollplus.feature.teacher.domain.model.RosterStudentDto
import com.littlebridge.enrollplus.feature.teacher.domain.model.TeacherClassSummaryDto
import com.littlebridge.enrollplus.feature.teacher.domain.model.WeeklyPeriodDto
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherClassesViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonSize
import com.littlebridge.enrollplus.ui.v2.components.VButtonTone
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VInput
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import org.koin.compose.viewmodel.koinViewModel

/**
 * CLASSES tab — the teacher's roster plane.
 *
 *   list → tap a class → rich composite detail (next class, weekly timetable,
 *   attendance summary, scheduled tests, active homework, full roster) → tap a
 *   student → scoped read-only profile (attendance / performance / flags / parent
 *   contact).
 *
 * Built ground-up from the Parents-Portal vocabulary: lavender canvas, white
 * rounded TCards with hairline borders, Canvas TRings, brand violet reserved for
 * active/brand moments. No old teacher layout is mimicked.
 *
 * Self-contained: detail and student-profile are rendered in-tab (the shell does
 * not need to know about them) so the dock/header stay put.
 */
@Composable
fun TeacherClassesScreenV2(
    modifier: Modifier = Modifier,
    teacherName: String = "",
    unreadCount: Int = 0,
    onOpenNotifications: () -> Unit = {},
    viewModel: TeacherClassesViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    val c = VtC

    // Student-profile drill-down lives here (over the class detail).
    var openStudentId by remember { mutableStateOf<String?>(null) }

    AnimatedContent(
        targetState = Triple(state.openAssignmentId, openStudentId, state.classes.size),
        transitionSpec = { (fadeIn() togetherWith fadeOut()) },
        label = "classes-nav",
        modifier = modifier.fillMaxSize().background(VColors.cream),
    ) { (assignmentId, studentId, _) ->
        when {
            studentId != null -> TeacherStudentProfilePane(
                studentId = studentId,
                onBack = { openStudentId = null },
            )
            assignmentId != null -> ClassDetailPane(
                state = state,
                onBack = { viewModel.closeClass() },
                onRetry = { viewModel.retryDetail() },
                onOpenStudent = { openStudentId = it },
            )
            else -> ClassListPane(
                state = state,
                teacherName = teacherName,
                unreadCount = unreadCount,
                onOpenNotifications = onOpenNotifications,
                onSearch = viewModel::setSearch,
                onCycleFilter = viewModel::cycleFilter,
                onOpenClass = viewModel::openClass,
                onRefresh = viewModel::refresh,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// LIST
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ClassListPane(
    state: com.littlebridge.enrollplus.feature.teacher.presentation.TeacherClassesState,
    teacherName: String,
    unreadCount: Int,
    onOpenNotifications: () -> Unit,
    onSearch: (String) -> Unit,
    onCycleFilter: () -> Unit,
    onOpenClass: (String) -> Unit,
    onRefresh: () -> Unit,
) {
    val c = VtC

    // The shared premium header sits at the very top of the tab (identical to
    // Home/Update/Timetable/Profile) so the whole portal wears one chrome — this
    // replaces the old slim canonical header the shell used to mount for Classes.
    val premiumHeader: @Composable () -> Unit = {
        TeacherPremiumHeader(
            teacherName = teacherName,
            lead = appString(StringKeys.TC_YOUR),
            accent = appString(StringKeys.TC_CLASSES_ACCENT),
            unreadCount = unreadCount,
            onOpenNotifications = onOpenNotifications,
            modifier = Modifier.padding(start = 4.dp, end = 4.dp, top = 12.dp),
        )
    }

    when {
        state.isLoading && state.classes.isEmpty() -> Column(Modifier.fillMaxSize().statusBarsPadding()) {
            Box(Modifier.padding(horizontal = 16.dp)) { premiumHeader() }
            TeacherCenterState { TeacherSpinner() }
        }
        state.error != null && state.classes.isEmpty() -> Column(Modifier.fillMaxSize().statusBarsPadding()) {
            Box(Modifier.padding(horizontal = 16.dp)) { premiumHeader() }
            TeacherCenterState {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(appString(StringKeys.TC_COULDNT_LOAD_CLASSES), style = VtT.bodyStrong.coloredV(c.navyDeep))
                    Spacer(Modifier.height(4.dp))
                    Text(state.error ?: "", style = VtT.caption.coloredV(c.ink3))
                    Spacer(Modifier.height(14.dp))
                    VButton(appString(StringKeys.COMMON_BUTTON_TRY_AGAIN), onClick = onRefresh, size = VButtonSize.Sm, tone = VButtonTone.Lavender)
                }
            }
        }
        else -> LazyColumn(
            modifier = Modifier.fillMaxSize().statusBarsPadding(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 0.dp, bottom = TeacherDockClearance),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item { premiumHeader() }

            // Premium overview strip — a violet "you teach" hero with live totals.
            item { ClassesOverviewStrip(state = state) }

            // Search + filter live in one clean control block.
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    VInput(
                        value = state.search,
                        onValueChange = onSearch,
                        placeholder = appString(StringKeys.TC_SEARCH_CLASS),
                        leadingIcon = VIcons.Search,
                    )
                    FilterChipRow(filter = state.classTeacherFilter, onCycle = onCycleFilter)
                }
            }
            if (state.visibleClasses.isEmpty()) {
                item {
                    TCard {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Text(appString(StringKeys.TC_NO_CLASSES_MATCH), style = VtT.bodyStrong.coloredV(c.navyDeep))
                            Spacer(Modifier.height(4.dp))
                            Text(appString(StringKeys.TC_TRY_DIFFERENT_SEARCH), style = VtT.caption.coloredV(c.ink3))
                        }
                    }
                }
            } else {
                items(state.visibleClasses, key = { it.assignmentId }) { cls ->
                    ClassCard(cls = cls, onClick = { onOpenClass(cls.assignmentId) })
                }
            }
        }
    }
}

/**
 * ClassesOverviewStrip — a premium violet-gradient hero that summarises the whole
 * teaching load at a glance: total classes, total students, classes still needing
 * attendance, and at-risk students. All figures come straight from the live
 * [TeacherClassesState] (no hardcoded data).
 */
@Composable
private fun ClassesOverviewStrip(
    state: com.littlebridge.enrollplus.feature.teacher.presentation.TeacherClassesState,
) {
    val classes = state.classes
    val totalStudents = classes.sumOf { it.studentCount }
    val pendingAttendance = classes.count { !it.todayAttendanceMarked }
    val atRisk = classes.sumOf { it.atRiskCount }

    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                androidx.compose.ui.graphics.Brush.linearGradient(
                    listOf(VColors.violet, VColors.violetHover),
                ),
            )
            .padding(18.dp),
    ) {
        Column {
            Text(
                appString(StringKeys.TC_CLASSES).uppercase(),
                style = VtT.label.coloredV(VColors.white.copy(alpha = 0.75f)).copy(
                    fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 0.8.sp,
                ),
            )
            Spacer(Modifier.height(6.dp))
            Text(
                appString(
                    StringKeys.TC_CLASSES_YOU_TEACH,
                    "count" to classes.size.toString(),
                    "plural" to if (classes.size == 1) "class" else "classes",
                ),
                style = VtT.h3.coloredV(VColors.white),
            )
            Spacer(Modifier.height(16.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                OverviewStat(value = totalStudents.toString(), label = appString(StringKeys.TC_STUDENTS), modifier = Modifier.weight(1f))
                OverviewStat(value = pendingAttendance.toString(), label = appString(StringKeys.TC_PENDING), modifier = Modifier.weight(1f))
                OverviewStat(value = atRisk.toString(), label = appString(StringKeys.TC_AT_RISK), modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun OverviewStat(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier
            .clip(RoundedCornerShape(16.dp))
            .background(VColors.white.copy(alpha = 0.14f))
            .padding(vertical = 12.dp, horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(value, style = VtT.h2.coloredV(VColors.white))
        Spacer(Modifier.height(2.dp))
        Text(
            label,
            style = VtT.caption.coloredV(VColors.white.copy(alpha = 0.8f)).copy(fontSize = 10.sp),
            maxLines = 1,
        )
    }
}

@Composable
private fun FilterChipRow(filter: Boolean?, onCycle: () -> Unit) {
    val c = VtC
    val (label, active) = when (filter) {
        null -> appString(StringKeys.TC_ALL_CLASSES) to false
        true -> appString(StringKeys.TC_CLASS_TEACHER) to true
        false -> appString(StringKeys.TC_SUBJECT_ONLY) to true
    }
    val ix = remember { MutableInteractionSource() }
    Row(
        Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (active) c.accentTint else c.cream)
            .border(1.dp, if (active) c.accent.copy(alpha = 0.35f) else c.hairline, RoundedCornerShape(999.dp))
            .clickable(interactionSource = ix, indication = null, onClick = onCycle)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(VIcons.Filter, contentDescription = null, tint = if (active) c.accentDeep else c.ink3, modifier = Modifier.size(15.dp))
        Text(
            label,
            style = VtT.label.coloredV(if (active) c.accentDeep else c.ink2).copy(fontWeight = FontWeight.Bold),
        )
        Text(appString(StringKeys.TC_TAP_TO_SWITCH), style = VtT.label.coloredV(c.ink3).copy(fontSize = 9.sp))
    }
}

/**
 * ClassCard — a premium, fully token-based class row: a subject-coloured accent
 * spine on the left, an initial disc, class/section + subject, a class-teacher
 * badge, live meta chips (students · attendance · at-risk) and the next period.
 * Rebuilt from scratch off [VColors]/[VShapes] (no legacy TCard/TIconDisc/TPill).
 */
@Composable
private fun ClassCard(cls: TeacherClassSummaryDto, onClick: () -> Unit) {
    val c = VtC
    val subjectColor = vtSubjectColor(cls.subject)
    val ix = remember { MutableInteractionSource() }
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, RoundedCornerShape(20.dp))
            .clickable(interactionSource = ix, indication = null, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Subject accent spine.
        Box(Modifier.width(5.dp).height(112.dp).background(subjectColor))

        Column(Modifier.weight(1f).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Initial disc.
                Box(
                    Modifier.size(46.dp).clip(CircleShape).background(subjectColor.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        cls.className.take(1).uppercase(),
                        style = VtT.h3.coloredV(subjectColor).copy(fontWeight = FontWeight.ExtraBold),
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            "${cls.className} · ${cls.section}",
                            style = VtT.h3.coloredV(c.navyDeep),
                        )
                        if (cls.isClassTeacher) {
                            Text(
                                appString(StringKeys.TC_CLASS_TEACHER),
                                style = VtT.label.coloredV(VColors.violet).copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(VColors.violetSoft)
                                    .padding(horizontal = 8.dp, vertical = 3.dp),
                            )
                        }
                    }
                    Spacer(Modifier.height(2.dp))
                    Text(cls.subject, style = VtT.body.coloredV(subjectColor).copy(fontWeight = FontWeight.SemiBold))
                }
                Icon(VIcons.ChevronRight, contentDescription = null, tint = c.ink3, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetaChip(icon = VIcons.Users, text = appString(StringKeys.TC_N_STUDENTS, "count" to cls.studentCount.toString()))
                if (cls.todayAttendanceMarked) {
                    MetaChip(icon = VIcons.Check, text = appString(StringKeys.TC_ATTENDANCE_DONE), tint = VColors.success, bg = VColors.success.copy(alpha = 0.14f))
                } else {
                    MetaChip(icon = VIcons.ClipboardList, text = appString(StringKeys.TC_MARK_ATTENDANCE), tint = VColors.gold, bg = VColors.gold.copy(alpha = 0.16f))
                }
                if (cls.atRiskCount > 0) {
                    MetaChip(icon = VIcons.AlertTriangle, text = appString(StringKeys.TC_N_AT_RISK, "count" to cls.atRiskCount.toString()), tint = VColors.coral, bg = VColors.coral.copy(alpha = 0.12f))
                }
            }
            cls.nextPeriod?.let { np ->
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(VIcons.Clock, contentDescription = null, tint = c.ink3, modifier = Modifier.size(13.dp))
                    Text(
                        nextPeriodLabel(np.dayLabel, np.startTime, np.endTime, np.room, np.isToday),
                        style = VtT.caption.coloredV(c.ink2),
                    )
                }
            }
        }
    }
}

@Composable
private fun MetaChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    tint: Color = VtC.ink2,
    bg: Color = VtC.cream,
) {
    Row(
        Modifier.clip(RoundedCornerShape(999.dp)).background(bg).padding(horizontal = 9.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(12.dp))
        Text(text, style = VtT.label.coloredV(tint).copy(fontWeight = FontWeight.SemiBold, fontSize = 10.sp))
    }
}

private fun nextPeriodLabel(day: String, start: String, end: String, room: String, isToday: Boolean): String {
    val when0 = if (isToday) "Today" else day
    val room0 = if (room.isBlank()) "" else " · $room"
    return "Next: $when0 $start–$end$room0"
}

// ─────────────────────────────────────────────────────────────────────────────
// DETAIL
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ClassDetailPane(
    state: com.littlebridge.enrollplus.feature.teacher.presentation.TeacherClassesState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onOpenStudent: (String) -> Unit,
) {
    val c = VtC
    val detail = state.detail
    Column(Modifier.fillMaxSize()) {
        TeacherSubHeader(
            title = detail?.let { "${it.header.className} · ${it.header.section}" } ?: appString(StringKeys.TC_CLASS),
            subtitle = detail?.header?.subject,
            onBack = onBack,
        )
        when {
            state.detailLoading -> TeacherCenterState { TeacherSpinner() }
            state.detailError != null -> TeacherCenterState {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(appString(StringKeys.TC_COULDNT_LOAD_CLASS), style = VtT.bodyStrong.coloredV(c.navyDeep))
                    Spacer(Modifier.height(4.dp))
                    Text(state.detailError ?: "", style = VtT.caption.coloredV(c.ink3))
                    Spacer(Modifier.height(14.dp))
                    VButton(appString(StringKeys.COMMON_BUTTON_TRY_AGAIN), onClick = onRetry, size = VButtonSize.Sm, tone = VButtonTone.Lavender)
                }
            }
            detail != null -> ClassDetailBody(detail = detail, onOpenStudent = onOpenStudent)
            else -> TeacherCenterState { TeacherSpinner() }
        }
    }
}

@Composable
private fun ClassDetailBody(detail: ClassDetailData, onOpenStudent: (String) -> Unit) {
    val c = VtC
    val subjectColor = vtSubjectColor(detail.header.subject)
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Next class + attendance hero
        item { NextClassCard(detail, subjectColor) }
        item { AttendanceSnapshotCard(detail) }

        // Weekly timetable
        if (detail.weeklyTimetable.isNotEmpty()) {
            item { TimetableCard(detail.weeklyTimetable) }
        }

        // Scheduled tests
        if (detail.assessmentSchedule.isNotEmpty()) {
            item { AssessmentScheduleCard(detail.assessmentSchedule) }
        }

        // Active homework
        if (detail.activeHomework.isNotEmpty()) {
            item { ActiveHomeworkCard(detail.activeHomework) }
        }

        // Roster
        item {
            Row(
                Modifier.fillMaxWidth().padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(appString(StringKeys.TC_STUDENTS), style = VtT.h3.coloredV(c.navyDeep))
                Text("${detail.roster.size}", style = VtT.bodyStrong.coloredV(c.ink3))
            }
        }
        items(detail.roster, key = { it.studentId }) { student ->
            RosterRow(student = student, onClick = { onOpenStudent(student.studentId) })
        }
        if (detail.roster.isEmpty()) {
            item {
                TCard {
                    Text(appString(StringKeys.TC_NO_STUDENTS_ENROLLED), style = VtT.body.coloredV(c.ink3), modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
private fun NextClassCard(detail: ClassDetailData, subjectColor: Color) {
    val c = VtC
    TCard {
        Column {
            TEyebrow(appString(StringKeys.TC_NEXT_CLASS), dot = subjectColor)
            Spacer(Modifier.height(10.dp))
            val np = detail.nextPeriod
            if (np == null) {
                Text(appString(StringKeys.TC_NO_UPCOMING_PERIOD), style = VtT.body.coloredV(c.ink2))
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TIconDisc(VIcons.Clock, subjectColor, subjectColor.copy(alpha = 0.12f), size = 44.dp, glyph = 20.dp)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            if (np.isToday) appString(StringKeys.TC_TODAY) else np.dayLabel,
                            style = VtT.h3.coloredV(c.navyDeep),
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            buildString {
                                append("${np.startTime}–${np.endTime}")
                                if (np.room.isNotBlank()) append(" · ${np.room}")
                            },
                            style = VtT.body.coloredV(c.ink2),
                        )
                    }
                    if (np.isToday) TPill(appString(StringKeys.TC_TODAY), c.accentTint, c.accentDeep)
                }
            }
        }
    }
}

@Composable
private fun AttendanceSnapshotCard(detail: ClassDetailData) {
    val c = VtC
    val a = detail.attendanceSummary
    val total = (a.presentToday + a.absentToday + a.lateToday + a.leaveToday).coerceAtLeast(1)
    val presentPct = ((a.presentToday + a.lateToday).toFloat() / total * 100f).toInt()
    TCard {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                TEyebrow(appString(StringKeys.TC_ATTENDANCE_TODAY))
                if (a.todayMarked) TPill(appString(StringKeys.TC_MARKED), c.success.copy(alpha = 0.16f), c.successInk)
                else TPill(appString(StringKeys.TC_NOT_MARKED), c.warning.copy(alpha = 0.18f), c.warningInk)
            }
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                TRing(
                    percent = if (a.todayMarked) presentPct else 0,
                    accent = c.success,
                    modifier = Modifier.size(72.dp),
                    label = if (a.todayMarked) "$presentPct%" else "—",
                    labelSize = 16.sp,
                )
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        TMetricTile("${a.presentToday}", appString(StringKeys.ATT_PRESENT), c.success, Modifier.weight(1f))
                        TMetricTile("${a.absentToday}", appString(StringKeys.ATT_ABSENT), c.danger, Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        TMetricTile("${a.lateToday}", appString(StringKeys.ATT_LATE), c.warning, Modifier.weight(1f))
                        TMetricTile("${a.leaveToday}", appString(StringKeys.ATT_LEAVE), c.accent, Modifier.weight(1f))
                    }
                }
            }
            if (a.weekRate != null || a.monthRate != null) {
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    a.weekRate?.let {
                        TMetricTile("${(it * 100).toInt()}%", appString(StringKeys.TC_THIS_WEEK), c.tealDeep, Modifier.weight(1f))
                    }
                    a.monthRate?.let {
                        TMetricTile("${(it * 100).toInt()}%", appString(StringKeys.TC_THIS_MONTH), c.navy, Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun TimetableCard(periods: List<WeeklyPeriodDto>) {
    val c = VtC
    TCard {
        Column {
            TEyebrow(appString(StringKeys.TC_WEEKLY_TIMETABLE))
            Spacer(Modifier.height(10.dp))
            periods.forEachIndexed { i, p ->
                if (i > 0) Spacer(Modifier.height(8.dp))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (p.isToday) c.accentTint else c.cream)
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        p.dayLabel,
                        style = VtT.bodyStrong.coloredV(if (p.isToday) c.accentDeep else c.navyDeep),
                        modifier = Modifier.width(48.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "${p.startTime}–${p.endTime}",
                        style = VtT.body.coloredV(c.ink2),
                        modifier = Modifier.weight(1f),
                    )
                    if (p.room.isNotBlank()) {
                        Text(p.room, style = VtT.caption.coloredV(c.ink3))
                    }
                    if (p.isToday) {
                        Spacer(Modifier.width(8.dp))
                        TPill(appString(StringKeys.TC_TODAY), c.accent.copy(alpha = 0.18f), c.accentDeep)
                    }
                }
            }
        }
    }
}

@Composable
private fun AssessmentScheduleCard(items: List<ClassAssessmentDto>) {
    val c = VtC
    TCard {
        Column {
            TEyebrow(appString(StringKeys.TC_SCHEDULED_TESTS))
            Spacer(Modifier.height(10.dp))
            items.forEachIndexed { i, a ->
                if (i > 0) Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TIconDisc(VIcons.GraduationCap, c.navy, c.navy.copy(alpha = 0.10f), size = 38.dp, glyph = 18.dp)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(a.name, style = VtT.bodyStrong.coloredV(c.navyDeep))
                        Spacer(Modifier.height(2.dp))
                        Text(
                            buildString {
                                append(a.type)
                                if (!a.examDate.isNullOrBlank()) append(" · ${prettyDateShort(a.examDate)}")
                            },
                            style = VtT.caption.coloredV(c.ink3),
                        )
                    }
                    AssessmentStatusPill(a.status)
                }
            }
        }
    }
}

@Composable
private fun AssessmentStatusPill(status: String) {
    val c = VtC
    val (bg, fg, label) = when (status.lowercase()) {
        "published" -> Triple(c.success.copy(alpha = 0.16f), c.successInk, "PUBLISHED")
        "graded", "completed" -> Triple(c.teal.copy(alpha = 0.18f), c.tealDeep, "GRADED")
        "scheduled", "upcoming" -> Triple(c.accentTint, c.accentDeep, "SCHEDULED")
        else -> Triple(c.cream, c.ink2, status.uppercase())
    }
    TPill(label, bg, fg)
}

@Composable
private fun ActiveHomeworkCard(items: List<ClassHomeworkDto>) {
    val c = VtC
    TCard {
        Column {
            TEyebrow(appString(StringKeys.TC_ACTIVE_HOMEWORK))
            Spacer(Modifier.height(10.dp))
            items.forEachIndexed { i, h ->
                if (i > 0) Spacer(Modifier.height(8.dp))
                val total = (h.submittedCount + h.notSubmittedCount).coerceAtLeast(1)
                val pct = (h.submittedCount.toFloat() / total * 100f).toInt()
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TRing(percent = pct, accent = c.teal, modifier = Modifier.size(42.dp), labelSize = 11.sp)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(h.title, style = VtT.bodyStrong.coloredV(c.navyDeep))
                        Spacer(Modifier.height(2.dp))
                        Text(
                            buildString {
                                append(appString(StringKeys.TC_N_TURNED_IN, "submitted" to h.submittedCount.toString(), "total" to total.toString()))
                                if (!h.dueDate.isNullOrBlank()) append(" · ${appString(StringKeys.TC_DUE_LABEL, "date" to prettyDateShort(h.dueDate))}")
                            },
                            style = VtT.caption.coloredV(c.ink3),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RosterRow(student: RosterStudentDto, onClick: () -> Unit) {
    val c = VtC
    val flag = primaryFlag(student.flags)
    TCard(onClick = onClick, padding = 12.dp) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // avatar / roll disc
            Box(
                Modifier.size(40.dp).clip(CircleShape).background(c.lavenderLight),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    student.roll?.toString() ?: student.name.take(1).uppercase(),
                    style = VtT.bodyStrong.coloredV(c.accentDeep),
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(student.name, style = VtT.bodyStrong.coloredV(c.navyDeep))
                Spacer(Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    student.attendanceRate?.let {
                        Text(appString(StringKeys.TC_N_PERCENT_PRESENT, "pct" to (it * 100).toInt().toString()), style = VtT.caption.coloredV(c.ink3))
                    }
                    student.latestMark?.let { m ->
                        Text("${fmt1(m.marks.toFloat())}/${m.max} · ${m.name}", style = VtT.caption.coloredV(c.ink3))
                    }
                }
            }
            if (flag != null) {
                Box(Modifier.size(8.dp).clip(CircleShape).background(flag))
                Spacer(Modifier.width(6.dp))
            }
            Icon(VIcons.ChevronRight, contentDescription = null, tint = c.ink3, modifier = Modifier.size(18.dp))
        }
    }
}

/** Map a student's flag codes (Doc 09 §5) to a severity dot colour; null if benign. */
@Composable
private fun primaryFlag(flags: List<String>): Color? {
    val c = VtC
    return when {
        flags.any { it in DANGER_FLAGS } -> c.danger
        flags.any { it in WARNING_FLAGS } -> c.warning
        else -> null
    }
}

private val DANGER_FLAGS = setOf("low_attendance", "recent_absences", "failing_trend")
private val WARNING_FLAGS = setOf("dropping")
