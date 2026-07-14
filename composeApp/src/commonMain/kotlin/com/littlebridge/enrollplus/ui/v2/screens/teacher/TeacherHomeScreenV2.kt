package com.littlebridge.enrollplus.ui.v2.screens.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.feature.event.domain.model.TeacherPtmEventDto
import com.littlebridge.enrollplus.feature.event.presentation.TeacherEventRegistrationState
import com.littlebridge.enrollplus.feature.event.presentation.TeacherEventRegistrationViewModel
import com.littlebridge.enrollplus.feature.teacher.domain.model.TeacherClassSummaryDto
import com.littlebridge.enrollplus.feature.teacher.presentation.ResolvedPeriodUi
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherCheckInState
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherCheckInViewModel
import com.littlebridge.enrollplus.feature.teacher.presentation.InsightCard
import com.littlebridge.enrollplus.feature.teacher.presentation.InsightSeverity
import com.littlebridge.enrollplus.feature.teacher.presentation.InsightTarget
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherClassesState
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherClassesViewModel
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherInsightsViewModel
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherObligationsState
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherObligationsViewModel
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherTodayState
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherTodayViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VPullRefresh
import com.littlebridge.enrollplus.ui.v2.components.VStaleChip
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel

/**
 * TeacherHomeScreenV2 — rebuilt to match the premium parent-portal home structure.
 *
 * Sections (top to bottom):
 *   1. TeacherPremiumHeader — shared wordmark, greeting, notification bell (used by every tab).
 *   2. NowTeachingCard    — the live/current class hero with Mark attendance + Lesson plan CTAs.
 *   3. Today's schedule   — horizontal scroll of class cards (NOW / NEXT / LATER).
 *   4. Pending actions    — real obligations from TeacherObligationsViewModel.
 *   5. Quick actions      — 4-tap grid to Attendance, Marks, Homework, Messages.
 *   6. My classes         — the teacher's allocated classes from TeacherClassesViewModel.
 *   7. Upcoming events    — teacher PTM events from TeacherEventRegistrationViewModel.
 *
 * Base backdrop is the same warm cream used on the parent home tab ([VColors.cream]).
 * Cards use [VColors.surfaceCard] with a soft outline; type uses [VTypography] tokens as-is.
 */
@Composable
fun TeacherHomeScreenV2(
    onOpenAttendanceForAssignment: (assignmentId: String, scope: String) -> Unit,
    onOpenLessonPlanForAssignment: (assignmentId: String, scope: String) -> Unit,
    onOpenUpdateTab: () -> Unit,
    onOpenUpdateTool: (UpdateTool) -> Unit,
    onOpenClasses: () -> Unit,
    onOpenLeaveRequests: () -> Unit = {},
    onOpenHealthAlerts: () -> Unit,
    onOpenTransportAttendance: () -> Unit,
    onOpenPews: () -> Unit,
    onOpenReportReview: () -> Unit,
    onOpenHeatmap: () -> Unit,
    onOpenIdCard: () -> Unit,
    onOpenScheduledMessages: () -> Unit,
    onOpenEvents: () -> Unit,
    onOpenMessages: () -> Unit,
    onOpenExamTimetable: () -> Unit = {},
    onOpenExport: () -> Unit = {},
    onOpenNotifications: () -> Unit = {},
    unreadCount: Int = 0,
    modifier: Modifier = Modifier,
    todayViewModel: TeacherTodayViewModel = koinViewModel(),
    checkInViewModel: TeacherCheckInViewModel = koinViewModel(),
    obligationsViewModel: TeacherObligationsViewModel = koinViewModel(),
    classesViewModel: TeacherClassesViewModel = koinViewModel(),
    eventsViewModel: TeacherEventRegistrationViewModel = koinViewModel(),
    insightsViewModel: TeacherInsightsViewModel = koinViewModel(),
) {
    val today by todayViewModel.state.collectAsStateV2()
    val checkIn by checkInViewModel.state.collectAsStateV2()
    val obligations by obligationsViewModel.state.collectAsStateV2()
    val classesState by classesViewModel.state.collectAsStateV2()
    val eventsState by eventsViewModel.state.collectAsStateV2()
    val insightsState by insightsViewModel.state.collectAsStateV2()

    // Pull classes + events when the home tab appears; refresh every 60s alongside today/obligations.
    LaunchedEffect(Unit) {
        obligationsViewModel.load()
        todayViewModel.load()
        classesViewModel.load()
        eventsViewModel.loadPtmEvents()
        while (true) {
            delay(60_000L)
            obligationsViewModel.load()
            todayViewModel.load()
            classesViewModel.load()
            eventsViewModel.loadPtmEvents()
        }
    }

    LaunchedEffect(classesState.classes) {
        insightsViewModel.deriveFromClassSummaries(classesState.classes)
    }

    // First-login-of-day check-in popup gate (kept from the previous rebuild).
    var popupDismissedForDate by rememberSaveable { mutableStateOf<String?>(null) }
    val popupVisible = !checkIn.isLoading &&
        !checkIn.statusUnavailable &&
        !checkIn.checkedIn &&
        checkIn.date.isNotBlank() &&
        popupDismissedForDate != checkIn.date

    // Pull-to-refresh: isRefreshing resets when both today + obligations refreshEpochs bump.
    var isRefreshing by remember { mutableStateOf(false) }
    LaunchedEffect(today.refreshEpoch, obligations.refreshEpoch) {
        if ((today.refreshEpoch > 0 || obligations.refreshEpoch > 0) && !today.isLoading && !obligations.isLoading) {
            isRefreshing = false
        }
    }

    VPullRefresh(
        isRefreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            todayViewModel.refresh()
            obligationsViewModel.refresh()
            classesViewModel.refreshForPull()
        },
        modifier = modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(VColors.cream)
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .padding(horizontal = 24.dp)
                .padding(top = 12.dp, bottom = TeacherDockClearance),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
        TeacherPremiumHeader(
            teacherName = today.teacherName,
            lead = "here's",
            accent = appString(StringKeys.TC_YOUR_DAY),
            unreadCount = unreadCount,
            onOpenNotifications = onOpenNotifications,
        )

        if (today.isStale || obligations.isStale) {
            VStaleChip()
        }

        NowTeachingCard(
            today = today,
            onMarkAttendance = onOpenAttendanceForAssignment,
            onLessonPlan = onOpenLessonPlanForAssignment,
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SectionHeader(
                title = appString(StringKeys.TC_TODAYS_SCHEDULE),
                actionLabel = appString(StringKeys.TC_ALL_CLASSES),
                onAction = onOpenClasses,
            )
            TodaysScheduleRow(
                today = today,
                onOpenLessonPlan = onOpenLessonPlanForAssignment,
                onOpenAttendance = onOpenAttendanceForAssignment,
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SectionHeader(title = appString(StringKeys.TC_PENDING_ACTIONS))
            PendingActionsList(
                obligations = obligations,
                onOpenUpdate = onOpenUpdateTab,
                onOpenUpdateTool = onOpenUpdateTool,
                onOpenClasses = onOpenClasses,
                onOpenLeaveRequests = onOpenLeaveRequests,
            )
        }

        if (insightsState.insights.isNotEmpty()) {
            NeedsAttentionSection(
                insights = insightsState.insights,
                onOpenPews = onOpenPews,
                onOpenAttendanceForAssignment = onOpenAttendanceForAssignment,
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SectionHeader(title = appString(StringKeys.TC_QUICK_ACTIONS))
            QuickActionsGrid(
                onAttendance = { onOpenUpdateTool(UpdateTool.Attendance) },
                onMarks = { onOpenUpdateTool(UpdateTool.Marks) },
                onHomework = { onOpenUpdateTool(UpdateTool.Homework) },
                onMessages = onOpenMessages,
                onExams = onOpenExamTimetable,
                onReports = onOpenReportReview,
                onExport = onOpenExport,
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SectionHeader(
                title = appString(StringKeys.TEACHER_CLASSES),
                actionLabel = appString(StringKeys.HOME_SEE_ALL),
                onAction = onOpenClasses,
            )
            MyClassesList(
                classes = classesState,
                onOpenClasses = onOpenClasses,
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SectionHeader(
                title = appString(StringKeys.TC_UPCOMING_EVENTS),
                actionLabel = appString(StringKeys.HOME_SEE_ALL),
                onAction = onOpenEvents,
            )
            UpcomingEventsList(
                events = eventsState,
                onOpenEvents = onOpenEvents,
            )
        }
    }

    if (popupVisible) {
        TeacherCheckInPopup(
            state = checkIn,
            visible = popupVisible,
            onDismiss = { popupDismissedForDate = checkIn.date.ifBlank { com.littlebridge.enrollplus.util.todayIso() } },
            onCheckIn = { method -> checkInViewModel.checkIn(method) },
        )
    }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Now Teaching — violet hero card with live/next class and two CTAs.
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun NowTeachingCard(
    today: TeacherTodayState,
    onMarkAttendance: (assignmentId: String, scope: String) -> Unit,
    onLessonPlan: (assignmentId: String, scope: String) -> Unit,
) {
    val day = today.day
    val current = day?.periods?.getOrNull(day.nowIndex)
    val next = day?.periods?.getOrNull(day.nextIndex)
    val period = current ?: next

    val gradient = Brush.linearGradient(
        colors = listOf(VColors.violet, VColors.violetHover),
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.xl)
            .background(gradient)
            .padding(20.dp),
    ) {
        Column {
            // eyebrow + status pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = appString(StringKeys.TC_NOW_TEACHING).uppercase(),
                    style = VTypography.label.copy(
                        color = VColors.white.copy(alpha = 0.85f),
                    ),
                )
                StatusPill(
                    label = if (current != null) appString(StringKeys.TC_NOW) else appString(StringKeys.TC_NEXT),
                    bg = VColors.white.copy(alpha = 0.20f),
                    fg = VColors.white,
                )
            }

            Spacer(Modifier.height(12.dp))

            if (period != null) {
                Text(
                    text = "Class ${period.classLabel}",
                    style = VTypography.h2.copy(color = VColors.white),
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = buildString {
                        append(period.subject)
                        if (period.room.isNotBlank()) append(" · ${period.room}")
                    },
                    style = VTypography.body.copy(color = VColors.white.copy(alpha = 0.85f)),
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    val scope = if (period.section.isBlank()) {
                        "${period.className} · ${period.subject}"
                    } else {
                        "${period.className}-${period.section} · ${period.subject}"
                    }
                    HeroCta(
                        text = appString(StringKeys.TC_MARK_ATTENDANCE),
                        icon = VIcons.ListChecks,
                        tone = VColors.white,
                        onClick = { period.assignmentId?.let { onMarkAttendance(it, scope) } },
                        modifier = Modifier.weight(1f),
                    )
                    HeroCta(
                        text = appString(StringKeys.TC_LESSON),
                        icon = VIcons.ClipboardList,
                        tone = VColors.white.copy(alpha = 0.85f),
                        onClick = { period.assignmentId?.let { onLessonPlan(it, scope) } },
                        modifier = Modifier.weight(1f),
                    )
                }
            } else {
                Text(
                    text = appString(StringKeys.TC_NO_PERIOD_RIGHT_NOW),
                    style = VTypography.h3.copy(color = VColors.white),
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = appString(StringKeys.TC_NO_CLASSES_SCHEDULED_TODAY),
                    style = VTypography.body.copy(color = VColors.white.copy(alpha = 0.75f)),
                )
            }
        }
    }
}

@Composable
private fun HeroCta(
    text: String,
    icon: ImageVector,
    tone: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(VShapes.lg)
            .background(tone.copy(alpha = 0.14f))
            .border(1.dp, tone.copy(alpha = 0.25f), VShapes.lg)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(icon, contentDescription = null, tint = tone, modifier = Modifier.size(18.dp))
        Text(
            text = text,
            style = VTypography.label.copy(color = tone),
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Today's schedule — horizontal scroll of class cards.
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun TodaysScheduleRow(
    today: TeacherTodayState,
    onOpenLessonPlan: (assignmentId: String, scope: String) -> Unit,
    onOpenAttendance: (assignmentId: String, scope: String) -> Unit,
) {
    val day = today.day
    val periods = day?.periods.orEmpty()

    if (today.isLoading && day == null) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(3) { SkeletonScheduleCard() }
        }
        return
    }

    if (day == null || periods.isEmpty() || day.isHoliday) {
        EmptyCard(
            text = if (day?.isHoliday == true) appString(StringKeys.TC_HOLIDAY) else appString(StringKeys.TC_NO_CLASSES_SCHEDULED_TODAY),
        )
        return
    }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(end = 4.dp),
    ) {
        items(periods, key = { it.periodId ?: it.hashCode() }) { p ->
            val index = periods.indexOf(p)
            val isNow = day.nowIndex == index
            val isNext = day.nextIndex == index
            val label = when {
                isNow -> appString(StringKeys.TC_NOW)
                isNext -> appString(StringKeys.TC_NEXT)
                else -> appString(StringKeys.TC_LATER)
            }
            val scope = if (p.section.isBlank()) "${p.className} · ${p.subject}" else "${p.className}-${p.section} · ${p.subject}"
            ScheduleCard(
                period = p,
                label = label,
                isNow = isNow,
                onClick = {
                    p.assignmentId?.let { aid ->
                        if (p.attendanceMarked) onOpenLessonPlan(aid, scope)
                        else onOpenAttendance(aid, scope)
                    }
                },
            )
        }
    }
}

@Composable
private fun ScheduleCard(
    period: ResolvedPeriodUi,
    label: String,
    isNow: Boolean,
    onClick: () -> Unit,
) {
    val accent = if (isNow) VColors.violet else VColors.ink3
    Column(
        modifier = Modifier
            .width(160.dp)
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .border(
                width = if (isNow) 2.dp else 1.dp,
                color = if (isNow) VColors.violet.copy(alpha = 0.35f) else VColors.line,
                shape = VShapes.lg,
            )
            .clickable { onClick() }
            .padding(16.dp),
    ) {
        StatusPill(
            label = label,
            bg = if (isNow) VColors.violetSoft else VColors.lineSoft,
            fg = if (isNow) VColors.violet else VColors.ink3,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = period.classLabel,
            style = VTypography.h3.copy(color = VColors.ink),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = period.subject,
            style = VTypography.caption.copy(color = VColors.ink2),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(VIcons.Clock, contentDescription = null, tint = VColors.ink3, modifier = Modifier.size(14.dp))
            Text(
                text = "${period.startTime} – ${period.endTime}",
                style = VTypography.caption.copy(color = VColors.ink3),
            )
        }
        Spacer(Modifier.height(6.dp))
        if (period.room.isNotBlank()) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(VIcons.MapPin, contentDescription = null, tint = VColors.ink3, modifier = Modifier.size(14.dp))
                Text(
                    text = "Room ${period.room}",
                    style = VTypography.caption.copy(color = VColors.ink3),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun SkeletonScheduleCard() {
    Column(
        modifier = Modifier
            .width(160.dp)
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.lg)
            .padding(16.dp),
    ) {
        Box(Modifier.size(48.dp, 22.dp).clip(VShapes.full).background(VColors.lineSoft))
        Spacer(Modifier.height(12.dp))
        Box(Modifier.size(90.dp, 20.dp).clip(VShapes.sm).background(VColors.lineSoft))
        Spacer(Modifier.height(8.dp))
        Box(Modifier.size(120.dp, 14.dp).clip(VShapes.sm).background(VColors.lineSoft))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Pending actions — honest list built from real obligation counts.
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun PendingActionsList(
    obligations: TeacherObligationsState,
    onOpenUpdate: () -> Unit,
    onOpenUpdateTool: (UpdateTool) -> Unit,
    onOpenClasses: () -> Unit,
    onOpenLeaveRequests: () -> Unit = {},
) {
    if (obligations.unavailable) {
        EmptyCard(text = appString(StringKeys.COMMON_ERROR_GENERIC))
        return
    }

    val items = buildList {
        if (obligations.unmarkedClasses > 0) {
            add(PendingItem(
                label = appString(StringKeys.TC_ATTENDANCE),
                count = obligations.unmarkedClasses,
                suffix = appString(StringKeys.TC_CLASSES),
                icon = VIcons.ListChecks,
                tint = VColors.gold,
                onClick = onOpenUpdate,
            ))
        }
        if (obligations.submissionsToReview > 0) {
            add(PendingItem(
                label = appString(StringKeys.TEACHER_HOMEWORK),
                count = obligations.submissionsToReview,
                suffix = appString(StringKeys.TC_SUBMISSIONS),
                icon = VIcons.FileText,
                tint = VColors.sky,
                onClick = { onOpenUpdateTool(UpdateTool.Homework) },
            ))
        }
        if (obligations.unpublishedResults > 0) {
            add(PendingItem(
                label = appString(StringKeys.TC_RESULTS),
                count = obligations.unpublishedResults,
                suffix = appString(StringKeys.TC_TO_PUBLISH),
                icon = VIcons.GraduationCap,
                tint = VColors.violet,
                onClick = { onOpenUpdateTool(UpdateTool.Marks) },
            ))
        }
        if (obligations.pendingLeaveDecisions > 0) {
            add(PendingItem(
                label = appString(StringKeys.TC_LEAVE_REQUESTS),
                count = obligations.pendingLeaveDecisions,
                suffix = appString(StringKeys.TC_PENDING_COUNT),
                icon = VIcons.Calendar,
                tint = VColors.coral,
                onClick = onOpenLeaveRequests,
            ))
        }
    }

    if (items.isEmpty()) {
        EmptyCard(
            icon = VIcons.Sparkles,
            text = appString(StringKeys.TC_ALL_CAUGHT_UP),
            subtext = appString(StringKeys.TC_NOTHING_PENDING),
        )
        return
    }

    SurfaceCard {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items.forEach { item ->
                PendingRow(item = item)
            }
        }
    }
}

private data class PendingItem(
    val label: String,
    val count: Int,
    val suffix: String,
    val icon: ImageVector,
    val tint: Color,
    val onClick: () -> Unit,
)

@Composable
private fun PendingRow(item: PendingItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.md)
            .clickable { item.onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(item.tint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(item.icon, contentDescription = null, tint = item.tint, modifier = Modifier.size(20.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(
                text = item.label,
                style = VTypography.body.copy(color = VColors.ink),
            )
            Text(
                text = "${item.count} ${item.suffix}",
                style = VTypography.caption.copy(color = VColors.ink3),
            )
        }
        Text(
            text = item.count.toString(),
            style = VTypography.h3.copy(color = item.tint),
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Quick actions — 2×4 grid with fixed, non-repeated actions.
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun QuickActionsGrid(
    onAttendance: () -> Unit,
    onMarks: () -> Unit,
    onHomework: () -> Unit,
    onMessages: () -> Unit,
    onExams: () -> Unit = {},
    onReports: () -> Unit = {},
    onExport: () -> Unit = {},
) {
    val actions = listOf(
        QuickAction(appString(StringKeys.TEACHER_ATTENDANCE), VColors.violetSoft, VColors.violet, VIcons.ListChecks, onAttendance),
        QuickAction(appString(StringKeys.TC_MARKS), VColors.mintSoft, VColors.mint, VIcons.GraduationCap, onMarks),
        QuickAction(appString(StringKeys.TEACHER_HOMEWORK), VColors.goldSoft, VColors.gold, VIcons.FileText, onHomework),
        QuickAction(appString(StringKeys.TC_MESSAGES), VColors.coralSoft, VColors.coral, VIcons.Chat, onMessages),
        QuickAction("Exams", VColors.skySoft, VColors.sky, VIcons.Calendar, onExams),
        QuickAction("Reports", VColors.violetSoft, VColors.violetInk, VIcons.ClipboardList, onReports),
        QuickAction("Export", VColors.mintSoft, VColors.mint, VIcons.FileText, onExport),
    )

    SurfaceCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            actions.chunked(2).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    row.forEach { action ->
                        QuickActionTile(
                            action = action,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

private data class QuickAction(
    val label: String,
    val bg: Color,
    val iconTint: Color,
    val icon: ImageVector,
    val onClick: () -> Unit,
)

@Composable
private fun QuickActionTile(action: QuickAction, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.lg)
            .clickable { action.onClick() }
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(action.bg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(action.icon, contentDescription = null, tint = action.iconTint, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = action.label,
            style = VTypography.body.copy(color = VColors.ink),
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// My classes — list of allocated classes with real counts.
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun MyClassesList(
    classes: TeacherClassesState,
    onOpenClasses: () -> Unit,
) {
    if (classes.isLoading && classes.classes.isEmpty()) {
        SurfaceCard {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                repeat(3) { SkeletonClassRow() }
            }
        }
        return
    }

    if (classes.classes.isEmpty()) {
        EmptyCard(text = appString(StringKeys.TC_NO_ALLOCATIONS))
        return
    }

    SurfaceCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            classes.classes.take(4).forEach { cls ->
                ClassRow(cls = cls, onClick = onOpenClasses)
            }
        }
    }
}

@Composable
private fun ClassRow(cls: TeacherClassSummaryDto, onClick: () -> Unit) {
    val accent = subjectColor(VColors, cls.subject.ifBlank { cls.className })
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.md)
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(accent.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = cls.className.take(1).uppercase(),
                style = VTypography.h3.copy(color = accent),
            )
        }
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Class ${cls.className.removePrefix("Class ").removePrefix("class ")}-${cls.section}",
                    style = VTypography.body.copy(color = VColors.ink),
                )
                if (cls.isClassTeacher) {
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "CT",
                        style = VTypography.caption.copy(color = VColors.violet),
                        modifier = Modifier
                            .clip(VShapes.full)
                            .background(VColors.violetSoft)
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                    )
                }
            }
            Text(
                text = "${cls.studentCount} students · ${cls.subject}",
                style = VTypography.caption.copy(color = VColors.ink2),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (!cls.todayAttendanceMarked) {
            StatusPill(
                label = appString(StringKeys.TC_PENDING),
                bg = VColors.gold.copy(alpha = 0.16f),
                fg = VColors.gold,
            )
        } else {
            Icon(VIcons.Check, contentDescription = null, tint = VColors.success, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun SkeletonClassRow() {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(Modifier.size(44.dp).clip(CircleShape).background(VColors.lineSoft))
        Column(Modifier.weight(1f)) {
            Box(Modifier.size(120.dp, 16.dp).clip(VShapes.sm).background(VColors.lineSoft))
            Spacer(Modifier.height(6.dp))
            Box(Modifier.size(160.dp, 12.dp).clip(VShapes.sm).background(VColors.lineSoft))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Upcoming events — teacher PTM events from the event registration VM.
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun UpcomingEventsList(
    events: TeacherEventRegistrationState,
    onOpenEvents: () -> Unit,
) {
    if (events.isLoading && events.events.isEmpty()) {
        SurfaceCard {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                repeat(2) { SkeletonEventRow() }
            }
        }
        return
    }

    if (events.events.isEmpty()) {
        EmptyCard(text = appString(StringKeys.CAL_NO_EVENTS))
        return
    }

    SurfaceCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            events.events.take(4).forEach { event ->
                EventRow(event = event, onClick = onOpenEvents)
            }
        }
    }
}

@Composable
private fun EventRow(event: TeacherPtmEventDto, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.md)
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Column(
            modifier = Modifier
                .size(48.dp)
                .clip(VShapes.md)
                .background(VColors.violetSoft),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            val (day, mon) = event.date.prettyDayMon()
            Text(text = day, style = VTypography.body.copy(color = VColors.violet))
            Text(text = mon, style = VTypography.caption.copy(color = VColors.violetInk))
        }
        Column(Modifier.weight(1f)) {
            Text(
                text = event.title,
                style = VTypography.body.copy(color = VColors.ink),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = event.date.prettyDate() ?: "",
                style = VTypography.caption.copy(color = VColors.ink2),
            )
        }
        Icon(VIcons.ChevronRight, contentDescription = null, tint = VColors.ink3, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun SkeletonEventRow() {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        Box(Modifier.size(48.dp).clip(VShapes.md).background(VColors.lineSoft))
        Column(Modifier.weight(1f)) {
            Box(Modifier.size(140.dp, 16.dp).clip(VShapes.sm).background(VColors.lineSoft))
            Spacer(Modifier.height(6.dp))
            Box(Modifier.size(100.dp, 12.dp).clip(VShapes.sm).background(VColors.lineSoft))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Needs Attention — insight cards derived from class summaries.
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun NeedsAttentionSection(
    insights: List<InsightCard>,
    onOpenPews: () -> Unit,
    onOpenAttendanceForAssignment: (assignmentId: String, scope: String) -> Unit,
) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader(title = "Needs Attention")
        SurfaceCard {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                insights.take(4).forEachIndexed { idx, insight ->
                    if (idx > 0) {
                        Box(
                            Modifier.fillMaxWidth().height(1.dp).background(VColors.lineSoft),
                        )
                    }
                    InsightRow(
                        insight = insight,
                        onTap = {
                            when (insight.target) {
                                InsightTarget.Pews -> onOpenPews()
                                InsightTarget.Attendance -> insight.assignmentId?.let { aid ->
                                    onOpenAttendanceForAssignment(aid, insight.scopeLabel)
                                }
                            }
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun InsightRow(insight: InsightCard, onTap: () -> Unit) {
    val dotColor = when (insight.severity) {
        InsightSeverity.HIGH -> VColors.coral
        InsightSeverity.MEDIUM -> VColors.gold
        InsightSeverity.LOW -> VColors.violet
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
            .clip(VShapes.md)
            .clickable { onTap() }
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .padding(top = 5.dp)
                .size(7.dp)
                .clip(CircleShape)
                .background(dotColor),
        )
        Column(Modifier.weight(1f).fillMaxWidth().wrapContentHeight()) {
            Text(
                text = insight.title,
                style = VTypography.body.copy(color = VColors.ink),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (insight.description.isNotBlank()) {
                Text(
                    text = insight.description,
                    style = VTypography.caption.copy(color = VColors.ink3),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(top = 4.dp),
            ) {
                Text(
                    text = insight.actionLabel,
                    style = VTypography.caption.copy(color = VColors.violet),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Icon(VIcons.ChevronRight, contentDescription = null, tint = VColors.violet, modifier = Modifier.size(12.dp))
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Shared atoms — section header, surface card, status pill, empty card.
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SectionHeader(
    title: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title,
            style = VTypography.h3.copy(color = VColors.ink),
        )
        if (actionLabel != null && onAction != null) {
            Text(
                text = actionLabel,
                style = VTypography.label.copy(color = VColors.violet),
                modifier = Modifier.clickable { onAction() },
            )
        }
    }
}

@Composable
private fun SurfaceCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(VShapes.xl)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.xl)
            .padding(16.dp),
    ) {
        content()
    }
}

@Composable
private fun StatusPill(
    label: String,
    bg: Color,
    fg: Color,
    modifier: Modifier = Modifier,
) {
    Text(
        text = label,
        style = VTypography.caption.copy(color = fg),
        modifier = modifier
            .clip(VShapes.full)
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp),
    )
}

@Composable
private fun EmptyCard(
    text: String,
    subtext: String? = null,
    icon: ImageVector? = null,
) {
    SurfaceCard {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        ) {
            if (icon != null) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(VColors.success.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(icon, contentDescription = null, tint = VColors.success, modifier = Modifier.size(22.dp))
                }
            }
            Text(
                text = text,
                style = VTypography.body.copy(color = VColors.ink),
                textAlign = TextAlign.Center,
            )
            if (subtext != null) {
                Text(
                    text = subtext,
                    style = VTypography.caption.copy(color = VColors.ink3),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Helpers — local formatting extensions.
// ─────────────────────────────────────────────────────────────────────────────

private fun subjectColor(c: VColors, key: String): Color {
    val palette = listOf(c.violet, c.mint, c.sky, c.coral, c.gold)
    if (key.isBlank()) return palette.first()
    val idx = ((key.hashCode() % palette.size) + palette.size) % palette.size
    return palette[idx]
}

private fun String?.prettyDate(): String? {
    if (this.isNullOrBlank()) return null
    val parts = split("-")
    if (parts.size != 3) return this
    val y = parts[0]
    val m = parts[1].toIntOrNull() ?: return this
    val d = parts[2].toIntOrNull() ?: return this
    val mon = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec").getOrNull(m - 1) ?: return this
    return "$d $mon $y"
}

private fun String?.prettyDayMon(): Pair<String, String> {
    if (this.isNullOrBlank()) return "" to ""
    val parts = split("-")
    if (parts.size != 3) return "" to ""
    val m = parts[1].toIntOrNull() ?: return "" to ""
    val d = parts[2].toIntOrNull() ?: return "" to ""
    val mon = listOf("JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC").getOrNull(m - 1) ?: return "" to ""
    return d.toString() to mon
}
