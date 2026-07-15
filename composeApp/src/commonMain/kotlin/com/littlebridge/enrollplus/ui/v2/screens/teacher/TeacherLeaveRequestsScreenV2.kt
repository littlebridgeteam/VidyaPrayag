package com.littlebridge.enrollplus.ui.v2.screens.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.feature.teacher.domain.model.TeacherLeaveDto
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherLeaveState
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherLeaveViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VBadge
import com.littlebridge.enrollplus.ui.v2.components.VBadgeTone
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonSize
import com.littlebridge.enrollplus.ui.v2.components.VButtonTone
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VPullRefresh
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import org.koin.compose.viewmodel.koinViewModel

/**
 * TeacherLeaveRequestsScreenV2 — student leave requests routed to this teacher
 * for approval. Backed by [TeacherLeaveViewModel] which calls
 * `GET /api/v1/teacher/leave-requests` and `PATCH /api/v1/teacher/leave-requests/{id}`.
 *
 * Each pending request shows student name, class/section, date range, reason,
 * and Approve / Reject buttons. Decided requests show a status badge.
 */
@Composable
fun TeacherLeaveRequestsScreenV2(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: TeacherLeaveViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()

    Column(modifier.fillMaxSize().statusBarsPadding().imePadding().navigationBarsPadding()) {
        VBackHeader(title = "Leave Requests", onBack = onBack)
        VPullRefresh(
            isRefreshing = state.loading && state.requests.isNotEmpty(),
            onRefresh = { viewModel.load() },
        ) {
            LeaveRequestsContent(
                state = state,
                onApprove = viewModel::approve,
                onReject = viewModel::reject,
                onRetry = { viewModel.load() },
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun LeaveRequestsContent(
    state: TeacherLeaveState,
    onApprove: (String) -> Unit,
    onReject: (String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier.verticalScroll(rememberScrollState()).padding(horizontal = 20.dp).padding(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (state.pendingCount > 0) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(VIcons.Calendar, contentDescription = null, tint = VColors.coral, modifier = Modifier.size(16.dp))
                Text(
                    "${state.pendingCount} pending",
                    style = VTypography.label.copy(fontWeight = FontWeight.SemiBold, color = VColors.coral),
                )
            }
        }

        VStateHost(
            loading = state.loading,
            error = state.error,
            isEmpty = state.isEmpty,
            emptyTitle = "No leave requests",
            emptyBody = "There are no student leave requests for your classes right now.",
            emptyIcon = VIcons.Calendar,
            onRetry = onRetry,
        ) {
            state.requests.forEach { req ->
                LeaveRequestCard(
                    req = req,
                    isDeciding = state.decidingId == req.id,
                    onApprove = { onApprove(req.id) },
                    onReject = { onReject(req.id) },
                )
            }
        }

        state.decisionError?.let { err ->
            VCard {
                Text(err, style = VTypography.caption, color = VColors.error)
            }
        }
    }
}

@Composable
private fun LeaveRequestCard(
    req: TeacherLeaveDto,
    isDeciding: Boolean,
    onApprove: () -> Unit,
    onReject: () -> Unit,
) {
    val isPending = req.status.equals("Pending", ignoreCase = true)
    VCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    req.studentName,
                    style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold).copy(color = VColors.ink),
                    modifier = Modifier.weight(1f, fill = false),
                )
                if (!isPending) {
                    val tone = when {
                        req.status.equals("Approved", ignoreCase = true) -> VBadgeTone.Success
                        req.status.equals("Rejected", ignoreCase = true) -> VBadgeTone.Danger
                        else -> VBadgeTone.Neutral
                    }
                    VBadge(text = req.status, tone = tone)
                }
            }
            val classLabel = listOfNotNull(req.className, req.section).joinToString(" · ")
            if (classLabel.isNotBlank()) {
                Text(classLabel, style = VTypography.caption, color = VColors.ink3)
            }
            val dateRange = if (req.dateFrom == req.dateTo) req.dateFrom else "${req.dateFrom} – ${req.dateTo}"
            Text(dateRange, style = VTypography.caption, color = VColors.ink3)
            if (req.reason.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(req.reason, style = VTypography.body, color = VColors.ink2)
            }
            if (isPending) {
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(Modifier.weight(1f)) {
                        VButton(
                            text = "Reject",
                            onClick = onReject,
                            full = true,
                            variant = VButtonVariant.Secondary,
                            tone = VButtonTone.Navy,
                            size = VButtonSize.Sm,
                            enabled = !isDeciding,
                        )
                    }
                    Box(Modifier.weight(1f)) {
                        VButton(
                            text = "Approve",
                            onClick = onApprove,
                            full = true,
                            variant = VButtonVariant.Primary,
                            tone = VButtonTone.Teal,
                            size = VButtonSize.Sm,
                            enabled = !isDeciding,
                        )
                    }
                }
            }
        }
    }
}
