package com.littlebridge.enrollplus.ui.v2.screens.teacher

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.feature.teacher.domain.model.ObligationItemDto
import com.littlebridge.enrollplus.feature.teacher.presentation.BellSlotUi
import com.littlebridge.enrollplus.feature.teacher.presentation.ResolvedDayUi
import com.littlebridge.enrollplus.feature.teacher.presentation.ResolvedPeriodUi
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherCheckInState
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherCheckInViewModel
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherObligationsState
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherObligationsViewModel
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherTodayState
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherTodayViewModel
import com.littlebridge.enrollplus.platform.BiometricMethod
import com.littlebridge.enrollplus.ui.v2.components.VActionCard
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.theme.VTheme
import com.littlebridge.enrollplus.ui.v2.theme.colored
import com.littlebridge.enrollplus.util.todayIso
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel

/**
 * TeacherHomeScreenV2 — the rebuilt-from-scratch Home tab, built on the Parents Portal's design
 * language (lavender canvas, white rounded cards, Canvas rings, the signature swipe-to-expand
 * cards) and the teacher's REAL data spine (Today / CheckIn / Obligations view-models).
 *
 * It deliberately replaces the cluttered "20-class list": today's classes are CLUBBED into a single
 * swipe-expand attendance card (face 0 = the day's progress ring + clubbed metrics; face 1 = the
 * per-class list). The greeting hero carries the time-sensitive greeting + a one-tap check-in ring.
 * A first-login-of-day check-in popup appears over this screen (closeable). Below, today's schedule
 * and the "what needs me" reminders are each their own card. Every number is server-authoritative.
 */
@Composable
fun TeacherHomeScreenV2(
    onOpenAttendanceForAssignment: (assignmentId: String, scope: String) -> Unit,
    onOpenUpdateTab: () -> Unit,
    onOpenClasses: () -> Unit,
    onOpenLessonPlanForAssignment: (assignmentId: String, scope: String) -> Unit = { _, _ -> },
    onOpenHealthAlerts: () -> Unit = {},
    onOpenTransportAttendance: () -> Unit = {},
    onOpenPews: () -> Unit = {},
    onOpenReportReview: () -> Unit = {},
    onOpenHeatmap: () -> Unit = {},
    onOpenIdCard: () -> Unit = {},
    onOpenScheduledMessages: () -> Unit = {},
    onOpenEvents: () -> Unit = {},
    onOpenMessages: () -> Unit = {},
    modifier: Modifier = Modifier,
    todayViewModel: TeacherTodayViewModel = koinViewModel(),
    checkInViewModel: TeacherCheckInViewModel = koinViewModel(),
    obligationsViewModel: TeacherObligationsViewModel = koinViewModel(),
) {
    val today by todayViewModel.state.collectAsStateV2()
    val checkIn by checkInViewModel.state.collectAsStateV2()
    val obligations by obligationsViewModel.state.collectAsStateV2()

    // Refresh obligations + today's schedule on appear + periodically (every 60s)
    // so the attendance card and reminders stay in sync after marking attendance
    // or returning from another tab.
    LaunchedEffect(Unit) {
        obligationsViewModel.load()
        todayViewModel.load()
        while (true) {
            delay(60_000L)
            obligationsViewModel.load()
            todayViewModel.load()
        }
    }

    // First-login-of-day popup gate: show once per day, tracked in saveable state. It pops only when
    // the status has resolved as "not checked in" and the teacher hasn't dismissed it this session.
    var popupDismissedForDate by rememberSaveable { mutableStateOf<String?>(null) }
    val popupVisible = !checkIn.isLoading &&
        !checkIn.statusUnavailable &&
        !checkIn.checkedIn &&
        checkIn.date.isNotBlank() &&
        popupDismissedForDate != checkIn.date

    Box(modifier.fillMaxSize().background(VTheme.colors.background)) {
        val scroll = rememberScrollState()
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(horizontal = 16.dp)
                .padding(top = 14.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            GreetingHeroCard(
                teacherName = today.teacherName,
                checkIn = checkIn,
                obligations = obligations,
                onCheckIn = { method -> checkInViewModel.checkIn(method) },
            )

            AttendanceSummaryCard(
                today = today,
                obligations = obligations,
                onOpenAttendance = onOpenAttendanceForAssignment,
                onOpenUpdate = onOpenUpdateTab,
            )

            ScheduleCard(today = today, onOpenLessonPlan = onOpenLessonPlanForAssignment)

            RemindersCard(
                obligations = obligations,
                onOpenUpdate = onOpenUpdateTab,
                onOpenClasses = onOpenClasses,
            )

            VActionCard(
                title = appString(StringKeys.TC_NEEDS_ATTENTION),
                subtitle = appString(StringKeys.TC_NEEDS_ATTENTION_DESC),
                icon = VIcons.AlertTriangle,
                onClick = onOpenPews,
            )

            VActionCard(
                title = appString(StringKeys.TC_REPORT_CARD_REVIEW),
                subtitle = appString(StringKeys.TC_REPORT_CARD_REVIEW_DESC),
                icon = VIcons.FileText,
                onClick = onOpenReportReview,
            )

            VActionCard(
                title = appString(StringKeys.TC_HEALTH_ALERTS),
                subtitle = appString(StringKeys.TC_HEALTH_ALERTS_DESC),
                icon = VIcons.Heart,
                onClick = onOpenHealthAlerts,
            )

            VActionCard(
                title = appString(StringKeys.TC_TRANSPORT_ATTENDANCE),
                subtitle = appString(StringKeys.TC_TRANSPORT_ATTENDANCE_DESC),
                icon = VIcons.MapPin,
                onClick = onOpenTransportAttendance,
            )

            VActionCard(
                title = appString(StringKeys.TC_DIGITAL_ID_CARD),
                subtitle = appString(StringKeys.TC_DIGITAL_ID_CARD_DESC),
                icon = VIcons.IdCard,
                onClick = onOpenIdCard,
            )

            VActionCard(
                title = appString(StringKeys.TC_MESSAGES),
                subtitle = appString(StringKeys.TC_MESSAGES_DESC),
                icon = VIcons.Chat,
                onClick = onOpenMessages,
            )

            VActionCard(
                title = appString(StringKeys.TC_SCHEDULED_MESSAGES),
                subtitle = appString(StringKeys.TC_SCHEDULED_MESSAGES_DESC),
                icon = VIcons.Clock,
                onClick = onOpenScheduledMessages,
            )

            VActionCard(
                title = appString(StringKeys.TC_PTM_EVENTS),
                subtitle = appString(StringKeys.TC_PTM_EVENTS_DESC),
                icon = VIcons.Calendar,
                onClick = onOpenEvents,
            )
        }

        // The first-login fingerprint check-in popup rides above everything.
        TeacherCheckInPopup(
            state = checkIn,
            visible = popupVisible,
            onDismiss = { popupDismissedForDate = checkIn.date.ifBlank { todayIso() } },
            onCheckIn = { method -> checkInViewModel.checkIn(method) },
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Greeting hero — time-sensitive greeting + a one-tap check-in ring.
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun GreetingHeroCard(
    teacherName: String,
    checkIn: TeacherCheckInState,
    obligations: TeacherObligationsState,
    onCheckIn: (method: String) -> Unit,
) {
    val c = VTheme.colors
    val name = teacherName.trim().substringBefore(" ").ifBlank { appString(StringKeys.TEACHER_TITLE) }
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    listOf(c.accent.copy(alpha = 0.10f), c.accentSoft.copy(alpha = 0.05f), c.card),
                ),
            )
            .border(1.dp, c.hairline, RoundedCornerShape(24.dp))
            .padding(18.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                TEyebrow(teacherGreeting().uppercase(), dot = c.accent)
                Spacer(Modifier.height(6.dp))
                Text(
                    appString(StringKeys.TC_HI_NAME, "name" to name),
                    style = VTheme.type.h1.colored(c.navyDeep).copy(fontWeight = FontWeight.ExtraBold, fontSize = 26.sp),
                )
                Spacer(Modifier.height(4.dp))
                val line = when {
                    obligations.isAllCaughtUp -> appString(StringKeys.TC_ALL_CAUGHT_UP_DAY)
                    obligations.totalOutstanding > 0 -> appString(StringKeys.TC_THINGS_NEED_ATTENTION, "count" to obligations.totalOutstanding.toString(), "plural" to if (obligations.totalOutstanding == 1) "" else "s")
                    else -> appString(StringKeys.TC_DAY_AT_A_GLANCE)
                }
                Text(line, style = VTheme.type.body.colored(c.ink2).copy(fontSize = 13.5.sp))
            }
            Spacer(Modifier.width(12.dp))
            CheckInRing(checkIn = checkIn, onCheckIn = onCheckIn)
        }
    }
}

@Composable
private fun CheckInRing(checkIn: TeacherCheckInState, onCheckIn: (method: String) -> Unit) {
    val c = VTheme.colors
    val checkedIn = checkIn.checkedIn
    val accent = if (checkedIn) c.successInk else c.warningInk
    val ix = remember { MutableInteractionSource() }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .size(78.dp)
                .clip(androidx.compose.foundation.shape.CircleShape)
                .clickable(
                    interactionSource = ix,
                    indication = null,
                    enabled = !checkedIn && !checkIn.isCheckingIn,
                ) { onCheckIn(BiometricMethod.Manual.wire) },
            contentAlignment = Alignment.Center,
        ) {
            TRing(
                percent = if (checkedIn) 100 else 0,
                modifier = Modifier.fillMaxSize(),
                accent = accent,
                stroke = 6.dp,
                label = "",
            )
            if (checkIn.isCheckingIn) {
                TeacherSpinner(26.dp)
            } else {
                Icon(
                    if (checkedIn) VIcons.Check else VIcons.ShieldCheck,
                    contentDescription = appString(StringKeys.TC_CHECK_IN),
                    tint = accent,
                    modifier = Modifier.size(30.dp),
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            if (checkedIn) appString(StringKeys.TC_CHECKED_IN) else appString(StringKeys.TC_TAP_TO_CHECK_IN),
            style = VTheme.type.label.colored(accent).copy(fontSize = 9.5.sp, fontWeight = FontWeight.Bold),
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Attendance summary swipe card — clubbed metrics (face 0) / per-class list (face 1).
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AttendanceSummaryCard(
    today: TeacherTodayState,
    obligations: TeacherObligationsState,
    onOpenAttendance: (assignmentId: String, scope: String) -> Unit,
    onOpenUpdate: () -> Unit,
) {
    val c = VTheme.colors
    var face by remember { mutableStateOf(0) }

    // Classes with a real, teachable period today (skip holiday/cancelled rows for the count).
    val periods = today.day?.periods.orEmpty().filter { !it.isCancelled }
    val totalToday = obligations.classesTodayTotal.takeIf { it > 0 } ?: periods.size
    val unmarked = obligations.unmarkedClasses
    val done = (totalToday - unmarked).coerceIn(0, totalToday)
    val percent = if (totalToday == 0) 0 else (done * 100) / totalToday

    SwipeExpandCard(face = face, faceCount = 2, onFaceChange = { face = it }, padding = 18.dp) { f ->
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TEyebrow(appString(StringKeys.TC_ATTENDANCE_TODAY), dot = if (unmarked == 0) c.successInk else c.warningInk)
                Spacer(Modifier.weight(1f))
                FaceDots(face, 2)
            }
            Spacer(Modifier.height(12.dp))

            when (f) {
                0 -> {
                    // Clubbed metrics — the single answer to "how many classes is attendance done".
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TRing(
                            percent = percent,
                            modifier = Modifier.size(86.dp),
                            accent = if (unmarked == 0) c.successInk else c.accent,
                            label = "$done/$totalToday",
                            labelSize = 17.sp,
                        )
                        Spacer(Modifier.width(16.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                if (totalToday == 0) appString(StringKeys.TC_NO_CLASSES_TODAY)
                                else if (unmarked == 0) appString(StringKeys.TC_ALL_ATTENDANCE_DONE)
                                else appString(StringKeys.TC_CLASSES_TO_MARK, "count" to unmarked.toString(), "plural" to if (unmarked == 1) "" else "es"),
                                style = VTheme.type.h3.colored(c.navyDeep).copy(fontWeight = FontWeight.ExtraBold, fontSize = 16.sp),
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                appString(StringKeys.TC_CLASSES_MARKED, "done" to done.toString(), "total" to totalToday.toString()),
                                style = VTheme.type.caption.colored(c.ink2).copy(fontSize = 12.sp),
                            )
                            Spacer(Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                TMetricTile(done.toString(), appString(StringKeys.TC_DONE), c.successInk, Modifier.weight(1f))
                                TMetricTile(unmarked.toString(), appString(StringKeys.TC_PENDING), c.warningInk, Modifier.weight(1f))
                            }
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    TSwipeHint(appString(StringKeys.TC_SWIPE_SEE_CLASSES))
                }
                else -> {
                    // Per-class list — the detail in-place (no navigation), tap a row to mark.
                    if (periods.isEmpty()) {
                        Text(appString(StringKeys.TC_NO_CLASSES_SCHEDULED_TODAY), style = VTheme.type.body.colored(c.ink2).copy(fontSize = 13.sp))
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            periods.forEach { p ->
                                AttendanceClassRow(p, onOpenAttendance)
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    TSwipeHint(appString(StringKeys.TC_SWIPE_BACK_TO_SUMMARY))
                }
            }
        }
    }
}

@Composable
private fun AttendanceClassRow(p: ResolvedPeriodUi, onOpen: (assignmentId: String, scope: String) -> Unit) {
    val c = VTheme.colors
    val accent = teacherSubjectColor(c, p.subject.ifBlank { p.className })
    val asg = p.assignmentId
    val ix = remember { MutableInteractionSource() }
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(c.cream)
            .clickable(interactionSource = ix, indication = null, enabled = asg != null) {
                if (asg != null) onOpen(asg, "${p.classLabel} · ${p.subject}")
            }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        TIconDisc(VIcons.ListChecks, tint = accent, bg = accent.copy(alpha = 0.12f), size = 36.dp, glyph = 18.dp)
        Column(Modifier.weight(1f)) {
            Text("${p.classLabel} · ${p.subject}", style = VTheme.type.bodyStrong.colored(c.ink).copy(fontSize = 13.5.sp, fontWeight = FontWeight.Bold))
            Text(
                "${p.startTime}–${p.endTime}${if (p.room.isNotBlank()) " · ${p.room}" else ""}",
                style = VTheme.type.caption.colored(c.ink3).copy(fontSize = 11.sp),
            )
        }
        if (p.attendanceMarked) {
            TPill(appString(StringKeys.TC_DONE), bg = c.success.copy(alpha = 0.16f), fg = c.successInk)
        } else {
            TPill(appString(StringKeys.TC_MARK), bg = c.accent.copy(alpha = 0.12f), fg = c.accentDeep)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Schedule card — today's timetable, with a live "now / next" cue.
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ScheduleCard(today: TeacherTodayState, onOpenLessonPlan: (assignmentId: String, scope: String) -> Unit = { _, _ -> }) {
    val c = VTheme.colors
    val day = today.day
    val periods = day?.periods.orEmpty()
    val hasPeriods = periods.isNotEmpty()
    var face by remember { mutableStateOf(0) }
    val maxFace = if (hasPeriods) 1 else 0

    SwipeExpandCard(face = face, faceCount = maxFace + 1, onFaceChange = { face = it }, padding = 18.dp) { f ->
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TEyebrow(appString(StringKeys.TC_TODAYS_SCHEDULE), dot = c.accent)
                Spacer(Modifier.weight(1f))
                if (maxFace > 0) FaceDots(f, maxFace + 1)
                Spacer(Modifier.width(8.dp))
                Text(prettyDate(day?.date), style = VTheme.type.caption.colored(c.ink3).copy(fontSize = 11.sp))
            }
            Spacer(Modifier.height(12.dp))
            when {
                today.isLoading && day == null -> Box(Modifier.fillMaxWidth().height(60.dp), contentAlignment = Alignment.Center) { TeacherSpinner(26.dp) }
                day == null -> Text(appString(StringKeys.TC_COULDNT_LOAD_SCHEDULE), style = VTheme.type.body.colored(c.ink2).copy(fontSize = 13.sp))
                day.isHoliday -> HolidayRow(day)
                day.periods.isEmpty() -> Text(appString(StringKeys.TC_NO_PERIODS_TODAY), style = VTheme.type.body.colored(c.ink2).copy(fontSize = 13.sp))
                else -> when (f) {
                    0 -> {
                        // Compact face: current or next class only
                        val currentPeriod = periods.getOrNull(day.nowIndex)
                        val nextPeriod = periods.getOrNull(day.nextIndex)
                        if (currentPeriod != null) {
                            SchedulePeriodRow(
                                currentPeriod,
                                isNow = true,
                                isNext = false,
                                onOpenLessonPlan = onOpenLessonPlan,
                            )
                        } else if (nextPeriod != null) {
                            SchedulePeriodRow(
                                nextPeriod,
                                isNow = false,
                                isNext = true,
                                onOpenLessonPlan = onOpenLessonPlan,
                            )
                        } else {
                            // No current or next — show first period
                            periods.firstOrNull()?.let {
                                SchedulePeriodRow(it, isNow = false, isNext = false, onOpenLessonPlan = onOpenLessonPlan)
                            } ?: Text(appString(StringKeys.TC_NO_PERIODS_TODAY), style = VTheme.type.body.colored(c.ink2).copy(fontSize = 13.sp))
                        }
                        Spacer(Modifier.height(10.dp))
                        TSwipeHint(appString(StringKeys.TC_SWIPE_FULL_SCHEDULE))
                    }
                    else -> {
                        // Expanded face: all periods
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            periods.forEachIndexed { i, p ->
                                SchedulePeriodRow(
                                    p,
                                    isNow = i == day.nowIndex,
                                    isNext = i == day.nextIndex,
                                    onOpenLessonPlan = onOpenLessonPlan,
                                )
                            }
                            if (day.bellSchedule.isNotEmpty()) {
                                Spacer(Modifier.height(4.dp))
                                BellScheduleSection(slots = day.bellSchedule)
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        TSwipeHint(appString(StringKeys.TC_SWIPE_BACK_TO_CURRENT))
                    }
                }
            }
        }
    }
}

@Composable
private fun HolidayRow(day: ResolvedDayUi) {
    val c = VTheme.colors
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(c.navy.copy(alpha = 0.06f)).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        TIconDisc(VIcons.Calendar, tint = c.navy, bg = c.navy.copy(alpha = 0.12f), size = 36.dp, glyph = 18.dp)
        Column {
            Text(appString(StringKeys.TC_HOLIDAY), style = VTheme.type.bodyStrong.colored(c.ink).copy(fontSize = 14.sp, fontWeight = FontWeight.Bold))
            val holidayName = day.holidayName
            if (!holidayName.isNullOrBlank()) Text(holidayName, style = VTheme.type.caption.colored(c.ink3).copy(fontSize = 12.sp))
        }
    }
}

@Composable
private fun SchedulePeriodRow(
    p: ResolvedPeriodUi,
    isNow: Boolean,
    isNext: Boolean,
    onOpenLessonPlan: (assignmentId: String, scope: String) -> Unit = { _, _ -> },
) {
    val c = VTheme.colors
    val accent = teacherSubjectColor(c, p.subject.ifBlank { p.className })
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (isNow) accent.copy(alpha = 0.10f) else c.cream)
            .then(if (isNow) Modifier.border(1.dp, accent.copy(alpha = 0.4f), RoundedCornerShape(14.dp)) else Modifier)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Column(Modifier.width(54.dp)) {
            Text(p.startTime, style = VTheme.type.bodyStrong.colored(if (isNow) accent else c.ink).copy(fontSize = 13.sp, fontWeight = FontWeight.Bold))
            Text(p.endTime, style = VTheme.type.caption.colored(c.ink3).copy(fontSize = 10.5.sp))
        }
        Box(Modifier.size(6.dp).clip(androidx.compose.foundation.shape.CircleShape).background(accent))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    if (p.isCancelled) appString(StringKeys.TC_CLASS_CANCELLED, "label" to "${p.classLabel} · ${p.subject}") else "${p.classLabel} · ${p.subject}",
                    style = VTheme.type.bodyStrong.colored(c.ink).copy(fontSize = 13.5.sp, fontWeight = FontWeight.Bold),
                )
                if (isNow) TPill(appString(StringKeys.TC_NOW), bg = accent.copy(alpha = 0.18f), fg = accent)
                else if (isNext) TPill(appString(StringKeys.TC_NEXT), bg = c.accent.copy(alpha = 0.10f), fg = c.accentDeep)
                // Lesson plan chip (LESSON_PLANNING_SPEC §7.3)
                p.lessonPlanStatus?.let { lps ->
                    val lpColor = when (lps) {
                        "completed" -> c.tealDeep
                        "skipped" -> c.ink3
                        else -> c.accent
                    }
                    val lpIcon = when (lps) {
                        "completed" -> VIcons.Check
                        "skipped" -> VIcons.Close
                        else -> VIcons.ClipboardList
                    }
                    val lpIx = remember { MutableInteractionSource() }
                    val scopeLabel = if (p.section.isBlank()) "${p.className} · ${p.subject}" else "${p.className}-${p.section} · ${p.subject}"
                    TPill(
                        label = lps.uppercase(),
                        bg = lpColor.copy(alpha = 0.12f),
                        fg = lpColor,
                        leading = { Icon(lpIcon, contentDescription = null, modifier = Modifier.size(10.dp)) },
                        modifier = Modifier.clickable(interactionSource = lpIx, indication = null) {
                            p.assignmentId?.let { aid -> onOpenLessonPlan(aid, scopeLabel) }
                        },
                    )
                }
            }
            val sub = p.substituteTeacherName
            if (p.room.isNotBlank() || sub != null) {
                Text(
                    buildString {
                        if (p.room.isNotBlank()) append(p.room)
                        if (sub != null) {
                            if (isNotEmpty()) append(" · ")
                            append(appString(StringKeys.TC_SUB_COLON, "name" to sub))
                        }
                    },
                    style = VTheme.type.caption.colored(c.ink3).copy(fontSize = 11.sp),
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Reminders / "what needs me" card — server obligations, honest when caught up.
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun RemindersCard(
    obligations: TeacherObligationsState,
    onOpenUpdate: () -> Unit,
    onOpenClasses: () -> Unit,
) {
    val c = VTheme.colors
    if (obligations.unavailable) return // honest: hide rather than fake "all caught up"
    TCard(padding = 18.dp) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TEyebrow(appString(StringKeys.TC_WHAT_NEEDS_YOU), dot = if (obligations.isAllCaughtUp) c.successInk else c.warningInk)
                Spacer(Modifier.weight(1f))
                if (obligations.totalOutstanding > 0) {
                    TPill(obligations.totalOutstanding.toString(), bg = c.warning.copy(alpha = 0.18f), fg = c.warningInk)
                }
            }
            Spacer(Modifier.height(12.dp))
            if (obligations.isAllCaughtUp || obligations.items.isEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    TIconDisc(VIcons.Sparkles, tint = c.successInk, bg = c.success.copy(alpha = 0.16f), size = 40.dp, glyph = 20.dp)
                    Column {
                        Text(appString(StringKeys.TC_ALL_CAUGHT_UP), style = VTheme.type.bodyStrong.colored(c.ink).copy(fontSize = 14.sp, fontWeight = FontWeight.Bold))
                        Text(appString(StringKeys.TC_NOTHING_PENDING), style = VTheme.type.caption.colored(c.ink3).copy(fontSize = 12.sp))
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    obligations.items.take(5).forEach { item ->
                        ReminderRow(item, onOpenUpdate, onOpenClasses)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReminderRow(item: ObligationItemDto, onOpenUpdate: () -> Unit, onOpenClasses: () -> Unit) {
    val c = VTheme.colors
    val tint = when (item.type) {
        "attendance" -> c.warningInk
        "marks" -> c.accent
        "homework" -> c.tealDeep
        "leave" -> c.dangerInk
        else -> c.ink2
    }
    val ix = remember { MutableInteractionSource() }
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(c.cream)
            .clickable(interactionSource = ix, indication = null) {
                if (item.type == "leave") onOpenClasses() else onOpenUpdate()
            }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        TIconDisc(obligationIcon(item.type), tint = tint, bg = tint.copy(alpha = 0.12f), size = 36.dp, glyph = 18.dp)
        Column(Modifier.weight(1f)) {
            Text(item.title, style = VTheme.type.bodyStrong.colored(c.ink).copy(fontSize = 13.5.sp, fontWeight = FontWeight.Bold), maxLines = 1)
            if (item.subtitle.isNotBlank()) {
                Text(item.subtitle, style = VTheme.type.caption.colored(c.ink3).copy(fontSize = 11.sp), maxLines = 1)
            }
        }
        if (item.count > 0) TPill(item.count.toString(), bg = tint.copy(alpha = 0.14f), fg = tint)
        Icon(VIcons.ChevronRight, contentDescription = null, tint = c.ink3, modifier = Modifier.size(18.dp))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Bell schedule section — renders the school day config slots (breaks, assembly, etc.)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun BellScheduleSection(slots: List<BellSlotUi>) {
    val c = VTheme.colors
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(c.navy.copy(alpha = 0.04f))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(VIcons.Clock, contentDescription = null, tint = c.ink3, modifier = Modifier.size(12.dp))
            Text(
                appString(StringKeys.TC_BELL_SCHEDULE),
                style = VTheme.type.label.colored(c.ink3).copy(fontWeight = FontWeight.Bold, fontSize = 9.5.sp),
            )
        }
        slots.forEach { slot ->
            BellSlotRow(slot)
        }
    }
}

@Composable
private fun BellSlotRow(slot: BellSlotUi) {
    val c = VTheme.colors
    val typeColor = when (slot.slotType) {
        "TEACHING" -> c.accent
        "BREAK" -> c.warning
        "ASSEMBLY" -> c.teal
        "LAB" -> c.lavenderLight
        "FREE" -> c.ink3
        "ZERO" -> c.ink3
        else -> c.ink3
    }
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(typeColor.copy(alpha = 0.12f))
                .padding(horizontal = 6.dp, vertical = 2.dp),
        ) {
            Text(
                slot.slotType.take(4),
                style = VTheme.type.label.colored(typeColor).copy(fontWeight = FontWeight.Bold, fontSize = 8.5.sp),
            )
        }
        Column(Modifier.weight(1f)) {
            if (slot.label.isNotBlank()) {
                Text(slot.label, style = VTheme.type.caption.colored(c.ink).copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold))
            }
            Text(
                "${slot.startTime} – ${slot.endTime}",
                style = VTheme.type.caption.colored(c.ink3).copy(fontSize = 10.sp),
            )
        }
        Text(
            "#${slot.slotIndex}",
            style = VTheme.type.label.colored(c.ink3).copy(fontSize = 9.sp),
        )
    }
}
