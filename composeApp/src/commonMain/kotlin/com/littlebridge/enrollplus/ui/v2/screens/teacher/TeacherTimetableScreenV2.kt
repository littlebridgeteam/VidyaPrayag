package com.littlebridge.enrollplus.ui.v2.screens.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.feature.admin.domain.model.TimetableChangeRequestDto
import com.littlebridge.enrollplus.feature.teacher.domain.model.ResolvedPeriodDto
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherTimetableViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.components.VBadge
import com.littlebridge.enrollplus.ui.v2.components.VBadgeTone
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonTone
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VEmptyState
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VInput
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import org.koin.compose.viewmodel.koinViewModel

private val WEEKDAY_LABELS = mapOf(
    1 to "Mon", 2 to "Tue", 3 to "Wed", 4 to "Thu", 5 to "Fri", 6 to "Sat", 7 to "Sun",
)

/** Give each subject a stable accent from the premium palette. */
private fun periodAccent(subject: String): Color {
    val palette = listOf(VColors.violet, VColors.mint, VColors.gold, VColors.sky, VColors.coral)
    val idx = (subject.hashCode().and(0x7FFFFFFF)) % palette.size
    return palette[idx]
}

private fun periodSoft(subject: String): Color {
    val palette = listOf(VColors.violetSoft, VColors.mintSoft, VColors.goldSoft, VColors.skySoft, VColors.coralSoft)
    val idx = (subject.hashCode().and(0x7FFFFFFF)) % palette.size
    return palette[idx]
}

/**
 * TeacherTimetableScreenV2 — the TIMETABLE tab, rebuilt from scratch on the premium
 * cream/violet token system.
 *
 * Structure (top → bottom):
 *   1. [TeacherPremiumHeader]  — the shared portal header ("your week").
 *   2. Segment switch          — a clean Schedule / Requests pill segment (replaces
 *                                the old VTopTabs).
 *   3. Day rail                — Mon…Sat day chips; the active day wears a violet fill.
 *   4. Timeline list           — period cards with an accent spine + time/room, or the
 *                                change-request feed. Both own their own LazyColumn
 *                                scroll and reserve [TeacherDockClearance] at the bottom
 *                                so nothing hides behind the floating dock.
 *
 * VM + public signature are PRESERVED; only header params are added so every tab shows
 * the same premium header.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TeacherTimetableScreenV2(
    modifier: Modifier = Modifier,
    teacherName: String = "",
    unreadCount: Int = 0,
    onOpenNotifications: () -> Unit = {},
    initialShowRequests: Boolean = false,
    viewModel: TeacherTimetableViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()

    var showSchedule by remember { mutableStateOf(!initialShowRequests) }

    LaunchedEffect(initialShowRequests) {
        if (initialShowRequests) showSchedule = false
    }
    var selectedDay by remember { mutableStateOf(1) }
    var showRequestDialog by remember { mutableStateOf(false) }
    var requestKind by remember { mutableStateOf("NEW_PERIOD") }
    var requestPeriod by remember { mutableStateOf<ResolvedPeriodDto?>(null) }

    Column(
        modifier
            .fillMaxSize()
            .background(VColors.cream)
            .statusBarsPadding()
            .padding(top = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // 1 — shared premium header.
        TeacherPremiumHeader(
            teacherName = teacherName,
            lead = appString(StringKeys.TC_YOUR),
            accent = appString(StringKeys.TC_WEEK_ACCENT),
            unreadCount = unreadCount,
            onOpenNotifications = onOpenNotifications,
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        // 2 — segment switch.
        SegmentSwitch(
            showSchedule = showSchedule,
            pendingRequests = state.changeRequests.count { it.status == "PENDING" },
            onSelect = { showSchedule = it },
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        if (showSchedule) {
            // 3 — day rail.
            DayRail(
                selectedDay = selectedDay,
                onSelect = { selectedDay = it },
                modifier = Modifier.padding(horizontal = 16.dp),
            )

            val dayData = state.week.find { it.weekday == selectedDay }
            val periods = dayData?.periods ?: emptyList()

            // 4 — timeline list (owns its own scroll + dock clearance).
            LazyColumn(
                Modifier.fillMaxWidth().weight(1f),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 16.dp, end = 16.dp, top = 4.dp, bottom = TeacherDockClearance,
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                if (periods.isEmpty()) {
                    item {
                        VEmptyState(
                            title = appString(
                                StringKeys.TC_NO_PERIODS_FOR_DAY,
                                "day" to (WEEKDAY_LABELS[selectedDay] ?: ""),
                            ),
                            icon = VIcons.Calendar,
                            body = appString(StringKeys.TC_WEEKLY_SCHEDULE_APPEAR),
                        )
                    }
                } else {
                    items(periods) { period ->
                        PeriodTimelineCard(
                            period = period,
                            onEdit = {
                                requestKind = "UPDATE_PERIOD"
                                requestPeriod = period
                                showRequestDialog = true
                            },
                            onDelete = {
                                requestKind = "DELETE_PERIOD"
                                requestPeriod = period
                                showRequestDialog = true
                            },
                        )
                    }
                }

                item {
                    Spacer(Modifier.height(4.dp))
                    VButton(
                        text = appString(StringKeys.TC_REQUEST_NEW_PERIOD),
                        onClick = {
                            requestKind = "NEW_PERIOD"
                            requestPeriod = null
                            showRequestDialog = true
                        },
                        full = true,
                        variant = VButtonVariant.Secondary,
                        tone = VButtonTone.Lavender,
                    )
                    state.infoMessage?.let {
                        Spacer(Modifier.height(8.dp))
                        InfoLine(it, VColors.success)
                    }
                    state.errorMessage?.let {
                        Spacer(Modifier.height(8.dp))
                        InfoLine(it, VColors.error)
                    }
                }
            }
        } else {
            // Requests feed.
            LazyColumn(
                Modifier.fillMaxWidth().weight(1f),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 16.dp, end = 16.dp, top = 4.dp, bottom = TeacherDockClearance,
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                if (state.changeRequests.isEmpty()) {
                    item {
                        VEmptyState(
                            title = appString(StringKeys.TC_NO_CHANGE_REQUESTS),
                            icon = VIcons.Calendar,
                            body = appString(StringKeys.TC_CHANGE_REQUESTS_APPEAR),
                        )
                    }
                } else {
                    items(state.changeRequests) { req ->
                        ChangeRequestCard(req)
                    }
                }
            }
        }
    }

    if (showRequestDialog) {
        ChangeRequestDialog(
            state = state,
            kind = requestKind,
            period = requestPeriod,
            isSaving = state.isSaving,
            onSubmit = { assignmentId, periodId, weekday, startTime, endTime, room, reason ->
                viewModel.submitChangeRequest(
                    kind = requestKind,
                    assignmentId = assignmentId,
                    periodId = periodId,
                    weekday = weekday,
                    startTime = startTime,
                    endTime = endTime,
                    room = room,
                    reason = reason,
                )
                showRequestDialog = false
            },
            onDismiss = { showRequestDialog = false },
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Segment switch — Schedule | Requests.
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SegmentSwitch(
    showSchedule: Boolean,
    pendingRequests: Int,
    onSelect: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .fillMaxWidth()
            .clip(VShapes.full)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.full)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        SegmentTab(
            label = appString(StringKeys.TC_SCHEDULE_TAB),
            active = showSchedule,
            badge = 0,
            onClick = { onSelect(true) },
            modifier = Modifier.weight(1f),
        )
        SegmentTab(
            label = appString(StringKeys.TC_REQUESTS_TAB),
            active = !showSchedule,
            badge = pendingRequests,
            onClick = { onSelect(false) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun SegmentTab(
    label: String,
    active: Boolean,
    badge: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val ix = remember { MutableInteractionSource() }
    val base = modifier
        .clip(VShapes.full)
        .clickable(interactionSource = ix, indication = null) { onClick() }
    val inner: @Composable () -> Unit = {
        Row(
            Modifier.fillMaxWidth().padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                label,
                style = VTypography.label.copy(
                    color = if (active) VColors.white else VColors.ink2,
                    fontWeight = if (active) FontWeight.Bold else FontWeight.SemiBold,
                ),
            )
            if (badge > 0) {
                Spacer(Modifier.width(6.dp))
                Box(
                    Modifier.size(18.dp).clip(VShapes.full)
                        .background(if (active) VColors.white.copy(alpha = 0.25f) else VColors.coral),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        badge.toString(),
                        style = VTypography.caption.copy(
                            color = if (active) VColors.white else VColors.white,
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                }
            }
        }
    }
    if (active) {
        Box(base.background(Brush.horizontalGradient(listOf(VColors.violet, VColors.violetHover)))) { inner() }
    } else {
        Box(base) { inner() }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Day rail — horizontal day chips.
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun DayRail(
    selectedDay: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        (1..6).forEach { day ->
            DayChip(
                label = WEEKDAY_LABELS[day] ?: "",
                active = selectedDay == day,
                onClick = { onSelect(day) },
            )
        }
    }
}

@Composable
private fun DayChip(label: String, active: Boolean, onClick: () -> Unit) {
    val ix = remember { MutableInteractionSource() }
    val base = Modifier
        .clip(VShapes.full)
        .clickable(interactionSource = ix, indication = null) { onClick() }
        .padding(horizontal = 18.dp, vertical = 10.dp)
    if (active) {
        Box(
            Modifier
                .clip(VShapes.full)
                .background(Brush.horizontalGradient(listOf(VColors.violet, VColors.violetHover)))
                .clickable(interactionSource = ix, indication = null) { onClick() }
                .padding(horizontal = 18.dp, vertical = 10.dp),
        ) {
            Text(label, style = VTypography.label.copy(color = VColors.white, fontWeight = FontWeight.Bold))
        }
    } else {
        Box(
            Modifier
                .clip(VShapes.full)
                .background(VColors.surfaceCard)
                .border(1.dp, VColors.line, VShapes.full)
                .clickable(interactionSource = ix, indication = null) { onClick() }
                .padding(horizontal = 18.dp, vertical = 10.dp),
        ) {
            Text(label, style = VTypography.label.copy(color = VColors.ink2, fontWeight = FontWeight.SemiBold))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Period timeline card — accent spine + time / subject / room.
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun PeriodTimelineCard(
    period: ResolvedPeriodDto,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val accent = periodAccent(period.subject)
    val soft = periodSoft(period.subject)
    Row(
        Modifier
            .fillMaxWidth()
            .clip(VShapes.xl)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.xl),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Accent spine.
        Box(Modifier.width(5.dp).height(72.dp).background(accent))

        // Time block.
        Column(
            Modifier.width(66.dp).padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(period.startTime, style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold, color = VColors.ink))
            Box(Modifier.width(1.dp).height(10.dp).background(VColors.line))
            Text(period.endTime, style = VTypography.caption.copy(color = VColors.ink3))
        }

        // Content.
        Column(Modifier.weight(1f).padding(vertical = 12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                VBadge(
                    text = if (period.section.isBlank()) period.className else "${period.className}-${period.section}",
                    tone = VBadgeTone.Arctic,
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                period.subject,
                style = VTypography.body.copy(fontWeight = FontWeight.Bold, color = VColors.ink),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (period.room.isNotBlank()) {
                Spacer(Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(VIcons.MapPin, contentDescription = null, tint = VColors.ink3, modifier = Modifier.size(13.dp))
                    Text(
                        appString(StringKeys.TC_ROOM_N, "n" to period.room),
                        style = VTypography.caption.copy(color = VColors.ink3),
                    )
                }
            }
        }

        // Actions.
        Row(
            Modifier.padding(end = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ActionDisc(icon = VIcons.Edit3, tint = accent, bg = soft, onClick = onEdit)
            ActionDisc(icon = VIcons.Close, tint = VColors.error, bg = VColors.errorSoft, onClick = onDelete)
        }
    }
}

@Composable
private fun ActionDisc(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    bg: Color,
    onClick: () -> Unit,
) {
    val ix = remember { MutableInteractionSource() }
    Box(
        Modifier
            .size(34.dp)
            .clip(VShapes.full)
            .background(bg)
            .clickable(interactionSource = ix, indication = null) { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Change-request card.
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ChangeRequestCard(req: TimetableChangeRequestDto) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(VShapes.xl)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.xl)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    "${req.className} · ${req.subject}",
                    style = VTypography.body.copy(fontWeight = FontWeight.Bold, color = VColors.ink),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    "${WEEKDAY_LABELS[req.weekday] ?: ""} ${req.startTime ?: ""}–${req.endTime ?: ""}",
                    style = VTypography.caption.copy(color = VColors.ink2),
                )
            }
            VBadge(text = req.kind.replace("_", " "), tone = VBadgeTone.Accent)
            VBadge(
                text = req.status,
                tone = when (req.status) {
                    "PENDING" -> VBadgeTone.Warning
                    "APPROVED" -> VBadgeTone.Success
                    else -> VBadgeTone.Neutral
                },
            )
        }
        if (req.reason.isNotBlank()) {
            Text(
                appString(StringKeys.TC_REASON_COLON, "reason" to req.reason),
                style = VTypography.caption.copy(color = VColors.ink3),
            )
        }
        if (req.adminNote.isNotBlank()) {
            Text(
                appString(StringKeys.TC_ADMIN_NOTE_COLON, "note" to req.adminNote),
                style = VTypography.caption.copy(color = VColors.ink3),
            )
        }
    }
}

@Composable
private fun InfoLine(text: String, tint: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Icon(VIcons.AlertCircle, contentDescription = null, tint = tint, modifier = Modifier.size(14.dp))
        Text(text, style = VTypography.caption.copy(color = tint))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Change-request dialog — new / update / delete a period.
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChangeRequestDialog(
    state: com.littlebridge.enrollplus.feature.teacher.presentation.TeacherTimetableState,
    kind: String,
    period: ResolvedPeriodDto?,
    isSaving: Boolean,
    onSubmit: (assignmentId: String?, periodId: String?, weekday: Int, startTime: String, endTime: String, room: String, reason: String) -> Unit,
    onDismiss: () -> Unit,
) {
    val assignments = state.assignments
    var selectedAssignmentId by remember { mutableStateOf(period?.assignmentId ?: assignments.firstOrNull()?.assignmentId ?: "") }
    var selectedWeekday by remember { mutableStateOf(1) }
    var startTime by remember { mutableStateOf(period?.startTime ?: "09:00") }
    var endTime by remember { mutableStateOf(period?.endTime ?: "10:00") }
    var room by remember { mutableStateOf(period?.room ?: "") }
    var reason by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        VCard(Modifier.fillMaxWidth().padding(16.dp)) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.verticalScroll(rememberScrollState()).imePadding(),
            ) {
                Text(
                    when (kind) {
                        "NEW_PERIOD" -> appString(StringKeys.TC_REQUEST_NEW_PERIOD)
                        "UPDATE_PERIOD" -> appString(StringKeys.TC_REQUEST_PERIOD_UPDATE)
                        "DELETE_PERIOD" -> appString(StringKeys.TC_REQUEST_PERIOD_DELETION)
                        else -> appString(StringKeys.TC_CHANGE_REQUEST)
                    },
                    style = VTypography.h3.copy(fontWeight = FontWeight.Bold, color = VColors.ink),
                )
                Text(
                    appString(StringKeys.TC_SENT_TO_ADMIN_FOR_APPROVAL),
                    style = VTypography.caption.copy(color = VColors.ink2),
                )

                if (kind == "NEW_PERIOD") {
                    Text(appString(StringKeys.TC_CLASS_SUBJECT), style = VTypography.caption.copy(color = VColors.ink2))
                    if (assignments.isEmpty()) {
                        Text(appString(StringKeys.TC_NO_ASSIGNMENTS_FOUND), style = VTypography.caption.copy(color = VColors.ink3))
                    } else {
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            assignments.forEach { asg ->
                                VBadge(
                                    text = "${asg.className}-${asg.section} ${asg.subject}",
                                    tone = if (selectedAssignmentId == asg.assignmentId) VBadgeTone.Arctic else VBadgeTone.Neutral,
                                    modifier = Modifier.clickable { selectedAssignmentId = asg.assignmentId },
                                )
                            }
                        }
                    }
                } else if (period != null) {
                    VCard(Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("${period.className}-${period.section} ${period.subject}", style = VTypography.body.copy(fontWeight = FontWeight.Bold, color = VColors.ink))
                            Text("${period.startTime}–${period.endTime} · ${appString(StringKeys.TC_ROOM_N, "n" to period.room)}", style = VTypography.caption.copy(color = VColors.ink2))
                        }
                    }
                }

                if (kind != "DELETE_PERIOD") {
                    Text(appString(StringKeys.TC_DAY), style = VTypography.caption.copy(color = VColors.ink2))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        (1..6).forEach { day ->
                            VBadge(
                                text = WEEKDAY_LABELS[day] ?: "",
                                tone = if (selectedWeekday == day) VBadgeTone.Arctic else VBadgeTone.Neutral,
                                modifier = Modifier.clickable { selectedWeekday = day },
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(Modifier.weight(1f)) {
                            VInput(value = startTime, onValueChange = { startTime = it }, label = appString(StringKeys.TC_START), hint = "HH:mm", placeholder = "09:00")
                        }
                        Box(Modifier.weight(1f)) {
                            VInput(value = endTime, onValueChange = { endTime = it }, label = appString(StringKeys.TC_END), hint = "HH:mm", placeholder = "10:00")
                        }
                    }
                    VInput(value = room, onValueChange = { room = it }, label = appString(StringKeys.TC_ROOM), hint = appString(StringKeys.TC_ROOM_HINT), placeholder = "101")
                }

                VInput(
                    value = reason,
                    onValueChange = { reason = it },
                    label = appString(StringKeys.TC_REASON),
                    hint = appString(StringKeys.TC_WHY_CHANGE_NEEDED),
                    placeholder = appString(StringKeys.TC_CHANGE_REASON_PH),
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    VButton(text = appString(StringKeys.COMMON_BUTTON_CANCEL), onClick = onDismiss, variant = VButtonVariant.Ghost)
                    VButton(
                        text = appString(StringKeys.TC_SUBMIT_REQUEST),
                        onClick = {
                            val r = reason.trim().ifBlank { return@VButton }
                            val aid = selectedAssignmentId.ifBlank { null }
                            val pid = period?.periodId
                            if (kind != "DELETE_PERIOD") {
                                onSubmit(aid, pid, selectedWeekday, startTime.trim(), endTime.trim(), room.trim(), r)
                            } else {
                                onSubmit(aid, pid, selectedWeekday, "", "", "", r)
                            }
                        },
                        variant = VButtonVariant.Primary,
                        tone = VButtonTone.Lavender,
                        loading = isSaving,
                    )
                }
            }
        }
    }
}
