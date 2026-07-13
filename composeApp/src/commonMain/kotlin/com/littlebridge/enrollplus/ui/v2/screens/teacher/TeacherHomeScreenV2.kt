package com.littlebridge.enrollplus.ui.v2.screens.teacher

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.feature.event.domain.model.TeacherPtmEventDto
import com.littlebridge.enrollplus.feature.event.presentation.TeacherEventRegistrationState
import com.littlebridge.enrollplus.feature.event.presentation.TeacherEventRegistrationViewModel
import com.littlebridge.enrollplus.feature.teacher.domain.model.TeacherClassSummaryDto
import com.littlebridge.enrollplus.feature.teacher.presentation.ResolvedPeriodUi
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherCheckInState
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherCheckInViewModel
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherClassesState
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherClassesViewModel
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherObligationsState
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherObligationsViewModel
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherTodayState
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherTodayViewModel
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel
import com.littlebridge.enrollplus.ui.v2.theme.VTheme
import androidx.compose.foundation.shape.RoundedCornerShape

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
 * Base backdrop is the same warm cream used on the parent home tab ([VTheme.colors.cream]).
 * Cards use [VTheme.colors.surfaceCard] with a soft outline; type uses [VTypography] tokens as-is.
 */
@Composable
fun TeacherHomeScreenV2(
    onOpenAttendanceForAssignment: (assignmentId: String, scope: String) -> Unit,
    onOpenLessonPlanForAssignment: (assignmentId: String, scope: String) -> Unit,
    onOpenUpdateTab: () -> Unit,
    onOpenUpdateTool: (UpdateTool) -> Unit,
    onOpenClasses: () -> Unit,
    onOpenHealthAlerts: () -> Unit,
    onOpenTransportAttendance: () -> Unit,
    onOpenPews: () -> Unit,
    onOpenReportReview: () -> Unit,
    onOpenHeatmap: () -> Unit,
    onOpenIdCard: () -> Unit,
    onOpenScheduledMessages: () -> Unit,
    onOpenEvents: () -> Unit,
    onOpenMessages: () -> Unit,
    onOpenNotifications: () -> Unit = {},
    unreadCount: Int = 0,
    modifier: Modifier = Modifier,
    todayViewModel: TeacherTodayViewModel = koinViewModel(),
    checkInViewModel: TeacherCheckInViewModel = koinViewModel(),
    obligationsViewModel: TeacherObligationsViewModel = koinViewModel(),
    classesViewModel: TeacherClassesViewModel = koinViewModel(),
    eventsViewModel: TeacherEventRegistrationViewModel = koinViewModel(),
) {
    val today by todayViewModel.state.collectAsStateV2()
    val checkIn by checkInViewModel.state.collectAsStateV2()
    val obligations by obligationsViewModel.state.collectAsStateV2()
    val classesState by classesViewModel.state.collectAsStateV2()
    val eventsState by eventsViewModel.state.collectAsStateV2()

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

    // First-login-of-day check-in popup gate (kept from the previous rebuild).
    var popupDismissedForDate by rememberSaveable { mutableStateOf<String?>(null) }
    val popupVisible = !checkIn.isLoading &&
        !checkIn.statusUnavailable &&
        !checkIn.checkedIn &&
        checkIn.date.isNotBlank() &&
        popupDismissedForDate != checkIn.date

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VTheme.colors.cream)
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
                onOpenClasses = onOpenClasses,
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SectionHeader(title = appString(StringKeys.TC_QUICK_ACTIONS))
            QuickActionsGrid(
                onAttendance = { onOpenUpdateTool(UpdateTool.Attendance) },
                onMarks = { onOpenUpdateTool(UpdateTool.Marks) },
                onHomework = { onOpenUpdateTool(UpdateTool.Homework) },
                onMessages = onOpenMessages,
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
        colors = listOf(VTheme.colors.violet, VTheme.colors.violetHover),
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
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
                    style = VTheme.type.label.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        color = VTheme.colors.white.copy(alpha = 0.85f),
                    ),
                )
                StatusPill(
                    label = if (current != null) appString(StringKeys.TC_NOW) else appString(StringKeys.TC_NEXT),
                    bg = VTheme.colors.white.copy(alpha = 0.20f),
                    fg = VTheme.colors.white,
                )
            }

            Spacer(Modifier.height(12.dp))

            if (period != null) {
                Text(
                    text = "Class ${period.classLabel}",
                    style = VTheme.type.h2.copy(color = VTheme.colors.white),
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = buildString {
                        append(period.subject)
                        if (period.room.isNotBlank()) append(" · ${period.room}")
                    },
                    style = VTheme.type.body.copy(color = VTheme.colors.white.copy(alpha = 0.85f)),
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
                        tone = VTheme.colors.white,
                        onClick = { period.assignmentId?.let { onMarkAttendance(it, scope) } },
                        modifier = Modifier.weight(1f),
                    )
                    HeroCta(
                        text = appString(StringKeys.TC_LESSON),
                        icon = VIcons.ClipboardList,
                        tone = VTheme.colors.white.copy(alpha = 0.85f),
                        onClick = { period.assignmentId?.let { onLessonPlan(it, scope) } },
                        modifier = Modifier.weight(1f),
                    )
                }
            } else {
                Text(
                    text = appString(StringKeys.TC_NO_PERIOD_RIGHT_NOW),
                    style = VTheme.type.h3.copy(color = VTheme.colors.white),
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = appString(StringKeys.TC_NO_CLASSES_SCHEDULED_TODAY),
                    style = VTheme.type.body.copy(color = VTheme.colors.white.copy(alpha = 0.75f)),
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
    val ix = remember { MutableInteractionSource() }
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(tone.copy(alpha = 0.14f))
            .border(1.dp, tone.copy(alpha = 0.25f), RoundedCornerShape(18.dp))
            .clickable(interactionSource = ix, indication = null) { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(icon, contentDescription = "", tint = tone, modifier = Modifier.size(18.dp))
        Text(
            text = text,
            style = VTheme.type.label.copy(
                fontSize = 12.sp,
                color = tone,
                fontWeight = FontWeight.Bold,
            ),
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
    val accent = if (isNow) VTheme.colors.violet else VTheme.colors.ink3
    val ix = remember { MutableInteractionSource() }
    Column(
        modifier = Modifier
            .width(160.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(VTheme.colors.surfaceCard)
            .border(
                width = if (isNow) 2.dp else 1.dp,
                color = if (isNow) VTheme.colors.violet.copy(alpha = 0.35f) else VTheme.colors.line,
                shape = RoundedCornerShape(18.dp),
            )
            .clickable(interactionSource = ix, indication = null) { onClick() }
            .padding(16.dp),
    ) {
        StatusPill(
            label = label,
            bg = if (isNow) VTheme.colors.violetSoft else VTheme.colors.lineSoft,
            fg = if (isNow) VTheme.colors.violet else VTheme.colors.ink3,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = period.classLabel,
            style = VTheme.type.h3.copy(fontSize = 18.sp, color = VTheme.colors.ink),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = period.subject,
            style = VTheme.type.caption.copy(color = VTheme.colors.ink2),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(VIcons.Clock, contentDescription = "", tint = VTheme.colors.ink3, modifier = Modifier.size(14.dp))
            Text(
                text = "${period.startTime} – ${period.endTime}",
                style = VTheme.type.caption.copy(color = VTheme.colors.ink3),
            )
        }
        Spacer(Modifier.height(6.dp))
        if (period.room.isNotBlank()) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(VIcons.MapPin, contentDescription = "", tint = VTheme.colors.ink3, modifier = Modifier.size(14.dp))
                Text(
                    text = "Room ${period.room}",
                    style = VTheme.type.caption.copy(color = VTheme.colors.ink3),
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
            .clip(RoundedCornerShape(18.dp))
            .background(VTheme.colors.surfaceCard)
            .border(1.dp, VTheme.colors.line, RoundedCornerShape(18.dp))
            .padding(16.dp),
    ) {
        Box(Modifier.size(48.dp, 22.dp).clip(RoundedCornerShape(50)).background(VTheme.colors.lineSoft))
        Spacer(Modifier.height(12.dp))
        Box(Modifier.size(90.dp, 20.dp).clip(RoundedCornerShape(10.dp)).background(VTheme.colors.lineSoft))
        Spacer(Modifier.height(8.dp))
        Box(Modifier.size(120.dp, 14.dp).clip(RoundedCornerShape(10.dp)).background(VTheme.colors.lineSoft))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Pending actions — honest list built from real obligation counts.
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun PendingActionsList(
    obligations: TeacherObligationsState,
    onOpenUpdate: () -> Unit,
    onOpenClasses: () -> Unit,
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
                tint = VTheme.colors.gold,
                onClick = onOpenUpdate,
            ))
        }
        if (obligations.submissionsToReview > 0) {
            add(PendingItem(
                label = appString(StringKeys.TEACHER_HOMEWORK),
                count = obligations.submissionsToReview,
                suffix = appString(StringKeys.TC_SUBMISSIONS),
                icon = VIcons.FileText,
                tint = VTheme.colors.sky,
                onClick = onOpenUpdate,
            ))
        }
        if (obligations.unpublishedResults > 0) {
            add(PendingItem(
                label = appString(StringKeys.TC_RESULTS),
                count = obligations.unpublishedResults,
                suffix = appString(StringKeys.TC_TO_PUBLISH),
                icon = VIcons.GraduationCap,
                tint = VTheme.colors.violet,
                onClick = onOpenUpdate,
            ))
        }
        if (obligations.pendingLeaveDecisions > 0) {
            add(PendingItem(
                label = appString(StringKeys.TC_LEAVE_REQUESTS),
                count = obligations.pendingLeaveDecisions,
                suffix = appString(StringKeys.TC_PENDING_COUNT),
                icon = VIcons.Calendar,
                tint = VTheme.colors.coral,
                onClick = onOpenClasses,
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
    val ix = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(interactionSource = ix, indication = null) { item.onClick() }
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
            Icon(item.icon, contentDescription = "", tint = item.tint, modifier = Modifier.size(20.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(
                text = item.label,
                style = VTheme.type.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = VTheme.colors.ink,
                ),
            )
            Text(
                text = "${item.count} ${item.suffix}",
                style = VTheme.type.caption.copy(color = VTheme.colors.ink3),
            )
        }
        Text(
            text = item.count.toString(),
            style = VTheme.type.h3.copy(fontSize = 20.sp, color = item.tint),
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Quick actions — 2×2 grid with fixed, non-repeated actions.
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun QuickActionsGrid(
    onAttendance: () -> Unit,
    onMarks: () -> Unit,
    onHomework: () -> Unit,
    onMessages: () -> Unit,
) {
    val actions = listOf(
        QuickAction(appString(StringKeys.TEACHER_ATTENDANCE), VTheme.colors.violetSoft, VTheme.colors.violet, VIcons.ListChecks, onAttendance),
        QuickAction(appString(StringKeys.TC_MARKS), VTheme.colors.mintSoft, VTheme.colors.mint, VIcons.GraduationCap, onMarks),
        QuickAction(appString(StringKeys.TEACHER_HOMEWORK), VTheme.colors.goldSoft, VTheme.colors.gold, VIcons.FileText, onHomework),
        QuickAction(appString(StringKeys.TC_MESSAGES), VTheme.colors.coralSoft, VTheme.colors.coral, VIcons.Chat, onMessages),
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
    val ix = remember { MutableInteractionSource() }
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(VTheme.colors.surfaceCard)
            .border(1.dp, VTheme.colors.line, RoundedCornerShape(18.dp))
            .clickable(interactionSource = ix, indication = null) { action.onClick() }
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
            Icon(action.icon, contentDescription = "", tint = action.iconTint, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = action.label,
            style = VTheme.type.bodySmall.copy(
                fontWeight = FontWeight.Bold,
                color = VTheme.colors.ink,
            ),
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
    val ix = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(interactionSource = ix, indication = null) { onClick() }
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
                style = VTheme.type.h3.copy(fontSize = 18.sp, color = accent),
            )
        }
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Class ${cls.className}-${cls.section}",
                    style = VTheme.type.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = VTheme.colors.ink,
                    ),
                )
                if (cls.isClassTeacher) {
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "CT",
                        style = VTheme.type.caption.copy(
                            fontWeight = FontWeight.Bold,
                            color = VTheme.colors.violet,
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(VTheme.colors.violetSoft)
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                    )
                }
            }
            Text(
                text = "${cls.studentCount} students · ${cls.subject}",
                style = VTheme.type.caption.copy(color = VTheme.colors.ink2),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (!cls.todayAttendanceMarked) {
            StatusPill(
                label = appString(StringKeys.TC_PENDING),
                bg = VTheme.colors.gold.copy(alpha = 0.16f),
                fg = VTheme.colors.gold,
            )
        } else {
            Icon(VIcons.Check, contentDescription = "", tint = VTheme.colors.success, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun SkeletonClassRow() {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(Modifier.size(44.dp).clip(CircleShape).background(VTheme.colors.lineSoft))
        Column(Modifier.weight(1f)) {
            Box(Modifier.size(120.dp, 16.dp).clip(RoundedCornerShape(10.dp)).background(VTheme.colors.lineSoft))
            Spacer(Modifier.height(6.dp))
            Box(Modifier.size(160.dp, 12.dp).clip(RoundedCornerShape(10.dp)).background(VTheme.colors.lineSoft))
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
    val ix = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(interactionSource = ix, indication = null) { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Column(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(VTheme.colors.violetSoft),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            val (day, mon) = event.date.prettyDayMon()
            Text(text = day, style = VTheme.type.bodySmall.copy(fontWeight = FontWeight.ExtraBold, color = VTheme.colors.violet))
            Text(text = mon, style = VTheme.type.caption.copy(fontWeight = FontWeight.SemiBold, color = VTheme.colors.violetInk))
        }
        Column(Modifier.weight(1f)) {
            Text(
                text = event.title,
                style = VTheme.type.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = VTheme.colors.ink,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = event.date.prettyDate() ?: "",
                style = VTheme.type.caption.copy(color = VTheme.colors.ink2),
            )
        }
        Icon(VIcons.ChevronRight, contentDescription = "", tint = VTheme.colors.ink3, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun SkeletonEventRow() {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        Box(Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(VTheme.colors.lineSoft))
        Column(Modifier.weight(1f)) {
            Box(Modifier.size(140.dp, 16.dp).clip(RoundedCornerShape(10.dp)).background(VTheme.colors.lineSoft))
            Spacer(Modifier.height(6.dp))
            Box(Modifier.size(100.dp, 12.dp).clip(RoundedCornerShape(10.dp)).background(VTheme.colors.lineSoft))
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
            style = VTheme.type.h3.copy(fontSize = 18.sp, color = VTheme.colors.ink),
        )
        if (actionLabel != null && onAction != null) {
            val ix = remember { MutableInteractionSource() }
            Text(
                text = actionLabel,
                style = VTheme.type.label.copy(color = VTheme.colors.violet),
                modifier = Modifier.clickable(interactionSource = ix, indication = null) { onAction() },
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
            .clip(RoundedCornerShape(24.dp))
            .background(VTheme.colors.surfaceCard)
            .border(1.dp, VTheme.colors.line, RoundedCornerShape(24.dp))
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
        style = VTheme.type.caption.copy(
            fontWeight = FontWeight.Bold,
            color = fg,
        ),
        modifier = modifier
            .clip(RoundedCornerShape(50))
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
                        .background(VTheme.colors.success.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(icon, contentDescription = "", tint = VTheme.colors.success, modifier = Modifier.size(22.dp))
                }
            }
            Text(
                text = text,
                style = VTheme.type.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = VTheme.colors.ink,
                ),
                textAlign = TextAlign.Center,
            )
            if (subtext != null) {
                Text(
                    text = subtext,
                    style = VTheme.type.caption.copy(color = VTheme.colors.ink3),
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
