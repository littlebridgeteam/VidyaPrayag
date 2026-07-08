package com.littlebridge.enrollplus.ui.v2.screens.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.feature.admin.domain.model.TimetableChangeRequestDto
import com.littlebridge.enrollplus.feature.teacher.domain.model.ResolvedPeriodDto
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherTimetableViewModel
import com.littlebridge.enrollplus.ui.v2.components.VBadge
import com.littlebridge.enrollplus.ui.v2.components.VBadgeTone
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonTone
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VEmptyState
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VInput
import com.littlebridge.enrollplus.ui.v2.components.VTopTabs
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import org.koin.compose.viewmodel.koinViewModel

private val WEEKDAY_LABELS = mapOf(
    1 to "Mon", 2 to "Tue", 3 to "Wed", 4 to "Thu", 5 to "Fri", 6 to "Sat", 7 to "Sun",
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TeacherTimetableScreenV2(
    modifier: Modifier = Modifier,
    viewModel: TeacherTimetableViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    val c = VtC
    val thisWeekLabel = appString(StringKeys.TC_THIS_WEEK)
    var activeTab by remember { mutableStateOf(thisWeekLabel) }
    var selectedDay by remember { mutableStateOf(1) }
    var showRequestDialog by remember { mutableStateOf(false) }
    var requestKind by remember { mutableStateOf("NEW_PERIOD") }
    var requestPeriod by remember { mutableStateOf<ResolvedPeriodDto?>(null) }

    Column(modifier.fillMaxSize().background(c.cream).statusBarsPadding()) {
        VTopTabs(
            tabs = listOf(appString(StringKeys.TC_THIS_WEEK), appString(StringKeys.TC_CHANGE_REQUESTS)),
            selected = activeTab,
            onSelect = { activeTab = it },
        )

        when (activeTab) {
            appString(StringKeys.TC_THIS_WEEK) -> {
                // Day selector
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    (1..6).forEach { day ->
                        VBadge(
                            text = WEEKDAY_LABELS[day] ?: "",
                            tone = if (selectedDay == day) VBadgeTone.Arctic else VBadgeTone.Neutral,
                            modifier = Modifier.clickable { selectedDay = day },
                        )
                    }
                }

                val dayData = state.week.find { it.weekday == selectedDay }
                val periods = dayData?.periods ?: emptyList()

                Column(
                    Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (periods.isEmpty()) {
                        VEmptyState(
                            title = appString(StringKeys.TC_NO_PERIODS_FOR_DAY, "day" to (WEEKDAY_LABELS[selectedDay] ?: "")),
                            icon = VIcons.Calendar,
                            body = appString(StringKeys.TC_WEEKLY_SCHEDULE_APPEAR),
                        )
                    } else {
                        periods.forEach { period ->
                            TeacherPeriodCard(
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

                    VButton(
                        text = appString(StringKeys.TC_REQUEST_NEW_PERIOD),
                        onClick = {
                            requestKind = "NEW_PERIOD"
                            requestPeriod = null
                            showRequestDialog = true
                        },
                        full = true,
                        variant = VButtonVariant.Primary,
                        tone = VButtonTone.Teal,
                    )

                    state.infoMessage?.let {
                        Text(it, style = VtT.caption.coloredV(VtC.successInk))
                    }
                    state.errorMessage?.let {
                        Text(it, style = VtT.caption.coloredV(VtC.dangerInk))
                    }

                    Spacer(Modifier.height(120.dp))
                }
            }

            appString(StringKeys.TC_CHANGE_REQUESTS) -> {
                Column(
                    Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (state.changeRequests.isEmpty()) {
                        VEmptyState(
                            title = appString(StringKeys.TC_NO_CHANGE_REQUESTS),
                            icon = VIcons.Calendar,
                            body = appString(StringKeys.TC_CHANGE_REQUESTS_APPEAR),
                        )
                    } else {
                        state.changeRequests.forEach { req ->
                            ChangeRequestItemCard(req)
                        }
                    }
                    Spacer(Modifier.height(120.dp))
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

@Composable
private fun TeacherPeriodCard(
    period: ResolvedPeriodDto,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val c = VtC
    VCard(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            // Time block
            Column(
                Modifier.width(64.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(period.startTime, style = VtT.bodyStrong.coloredV(c.ink))
                Text("↓", style = VtT.caption.coloredV(c.ink3))
                Text(period.endTime, style = VtT.body.coloredV(c.ink2))
            }

            Box(Modifier.width(1.dp).height(40.dp).background(c.hairline))

            // Content
            Column(Modifier.weight(1f)) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    VBadge(text = period.className, tone = VBadgeTone.Arctic)
                    if (period.section.isNotBlank()) VBadge(text = period.section, tone = VBadgeTone.Accent)
                }
                Spacer(Modifier.height(2.dp))
                Text(period.subject, style = VtT.bodyStrong.coloredV(c.ink))
                if (period.room.isNotBlank()) {
                    Text(appString(StringKeys.TC_ROOM_N, "n" to period.room), style = VtT.caption.coloredV(c.ink3))
                }
            }

            // Edit + Delete buttons
            Box(
                Modifier.size(28.dp).clip(CircleShape)
                    .background(c.teal.copy(alpha = 0.1f))
                    .clickable { onEdit() },
                contentAlignment = Alignment.Center,
            ) {
                Text("✎", color = c.tealDeep, fontWeight = FontWeight.Bold)
            }
            Box(
                Modifier.size(28.dp).clip(CircleShape)
                    .background(c.dangerInk.copy(alpha = 0.1f))
                    .clickable { onDelete() },
                contentAlignment = Alignment.Center,
            ) {
                Text("×", color = c.dangerInk, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ChangeRequestItemCard(req: TimetableChangeRequestDto) {
    val c = VtC
    VCard(Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "${req.className} · ${req.subject}",
                        style = VtT.bodyStrong.coloredV(c.ink),
                    )
                    Text(
                        "${WEEKDAY_LABELS[req.weekday] ?: ""} ${req.startTime ?: ""}–${req.endTime ?: ""}",
                        style = VtT.caption.coloredV(c.ink2),
                    )
                }
                VBadge(
                    text = req.kind.replace("_", " "),
                    tone = VBadgeTone.Accent,
                )
                VBadge(
                    text = req.status,
                    tone = when (req.status) {
                        "PENDING" -> VBadgeTone.Warning
                        "APPROVED" -> VBadgeTone.Success
                        "REJECTED" -> VBadgeTone.Neutral
                        else -> VBadgeTone.Neutral
                    },
                )
            }

            if (req.reason.isNotBlank()) {
                Text(appString(StringKeys.TC_REASON_COLON, "reason" to req.reason), style = VtT.caption.coloredV(c.ink3))
            }
            if (req.adminNote.isNotBlank()) {
                Text(appString(StringKeys.TC_ADMIN_NOTE_COLON, "note" to req.adminNote), style = VtT.caption.coloredV(c.ink3))
            }
        }
    }
}

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
                    style = VtT.h3, fontWeight = FontWeight.Bold, color = VtC.ink,
                )
                Text(
                    appString(StringKeys.TC_SENT_TO_ADMIN_FOR_APPROVAL),
                    style = VtT.caption.coloredV(VtC.ink2),
                )

                if (kind == "NEW_PERIOD") {
                    // Assignment selector
                    Text(appString(StringKeys.TC_CLASS_SUBJECT), style = VtT.caption.coloredV(VtC.ink2))
                    if (assignments.isEmpty()) {
                        Text(appString(StringKeys.TC_NO_ASSIGNMENTS_FOUND), style = VtT.caption.coloredV(VtC.ink3))
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
                    // Show period info for UPDATE/DELETE
                    VCard(Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("${period.className}-${period.section} ${period.subject}", style = VtT.bodyStrong.coloredV(VtC.ink))
                            Text("${period.startTime}–${period.endTime} · ${appString(StringKeys.TC_ROOM_N, "n" to period.room)}", style = VtT.caption.coloredV(VtC.ink2))
                        }
                    }
                }

                // Weekday selector
                if (kind != "DELETE_PERIOD") {
                    Text(appString(StringKeys.TC_DAY), style = VtT.caption.coloredV(VtC.ink2))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        (1..6).forEach { day ->
                            VBadge(
                                text = WEEKDAY_LABELS[day] ?: "",
                                tone = if (selectedWeekday == day) VBadgeTone.Arctic else VBadgeTone.Neutral,
                                modifier = Modifier.clickable { selectedWeekday = day },
                            )
                        }
                    }

                    // Time inputs
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

                // Reason (always required)
                VInput(
                    value = reason,
                    onValueChange = { reason = it },
                    label = appString(StringKeys.TC_REASON),
                    hint = appString(StringKeys.TC_WHY_CHANGE_NEEDED),
                    placeholder = appString(StringKeys.TC_CHANGE_REASON_PH),
                )

                // Actions
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
                        tone = VButtonTone.Teal,
                        loading = isSaving,
                    )
                }
            }
        }
    }
}
