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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.feature.teacher.presentation.AttendanceStatus
import com.littlebridge.enrollplus.feature.teacher.presentation.StudentAttendance
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
) {
    val state by viewModel.state.collectAsStateV2()

    LaunchedEffect(assignmentId) {
        if (assignmentId.isNotBlank() && state.assignmentId != assignmentId) viewModel.load(assignmentId)
    }

    Box(modifier.fillMaxSize().background(VColors.cream)) {
        when {
            state.isLoading && state.students.isEmpty() -> VtCenterState { TeacherSpinner() }
            state.error != null && state.students.isEmpty() -> VtErrorState(
                title = appString(StringKeys.TC_COULDNT_LOAD_ATTENDANCE),
                detail = state.error,
                retryLabel = appString(StringKeys.COMMON_BUTTON_RETRY),
                onRetry = { viewModel.retry() },
            )
            else -> AttendanceBody(state.students, viewModel, scopeLabel)
        }
    }
}

@Composable
private fun AttendanceBody(
    students: List<StudentAttendance>,
    viewModel: TeacherAttendanceViewModel,
    scopeLabel: String,
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
