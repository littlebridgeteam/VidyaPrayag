package com.littlebridge.enrollplus.ui.v2.screens.teacher

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.teacher.domain.model.TeacherFeeStudentDto
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherSalaryViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VBadge
import com.littlebridge.enrollplus.ui.v2.components.VBadgeTone
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VConfirmDialog
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VPullRefresh
import com.littlebridge.enrollplus.ui.v2.components.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.SkeletonList
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TeacherFeeEscalationScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TeacherSalaryViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    var isRefreshing by remember { mutableStateOf(false) }
    var selectedIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    var showEscalateDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadUnpaidFees()
    }

    LaunchedEffect(state.feeStudents) {
        if (isRefreshing) isRefreshing = false
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        VBackHeader(title = "Fee Escalation", onBack = onBack)

        VPullRefresh(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                viewModel.loadUnpaidFees()
            },
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Summary
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text("Total Unpaid", style = VTypography.caption, color = VColors.ink2)
                        Text(
                            "₹${"%,.0f".format(state.totalDue)}",
                            style = VTypography.h3,
                            fontWeight = FontWeight.Bold,
                            color = VColors.error,
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Students", style = VTypography.caption, color = VColors.ink2)
                        Text(
                            "${state.feeStudents.size}",
                            style = VTypography.h3,
                            fontWeight = FontWeight.Bold,
                            color = VColors.ink,
                        )
                    }
                }

                state.escalationMessage?.let { msg ->
                    VCard {
                        Text(msg, style = VTypography.body, color = VColors.success)
                    }
                }

                state.errorMessage?.let { err ->
                    VCard {
                        Text(err, style = VTypography.body, color = VColors.error)
                    }
                }

                if (state.feeStudents.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        VButton(
                            text = if (selectedIds.size == state.feeStudents.size) "Deselect All" else "Select All",
                            onClick = {
                                selectedIds = if (selectedIds.size == state.feeStudents.size) {
                                    emptySet()
                                } else {
                                    state.feeStudents.map { it.childId }.toSet()
                                }
                            },
                            variant = VButtonVariant.Ghost,
                            modifier = Modifier.weight(1f),
                        )
                        VButton(
                            text = "Escalate (${selectedIds.size})",
                            onClick = { showEscalateDialog = true },
                            enabled = selectedIds.isNotEmpty() && !state.isEscalating,
                            leading = { Icon(VIcons.Wallet, contentDescription = null) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }

                VStateHost(
                    loading = state.isFeeLoading,
                    error = state.errorMessage,
                    isEmpty = state.feeStudents.isEmpty() && !state.isFeeLoading,
                    emptyTitle = "No Unpaid Fees",
                    emptyBody = "All students in your classes have paid their fees. Great!",
                    emptyIcon = VIcons.Check,
                    onRetry = { viewModel.loadUnpaidFees() },
                    skeleton = { SkeletonList(rows = 4) },
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        state.feeStudents.forEach { student ->
                            FeeEscalationCard(
                                student = student,
                                isSelected = student.childId in selectedIds,
                                onToggle = {
                                    selectedIds = if (student.childId in selectedIds) {
                                        selectedIds - student.childId
                                    } else {
                                        selectedIds + student.childId
                                    }
                                },
                            )
                        }
                    }
                }
            }
        }
    }

    if (showEscalateDialog) {
        VConfirmDialog(
            visible = true,
            title = "Send Fee Reminder",
            message = "Send a fee payment reminder to parents of ${selectedIds.size} student(s)?",
            confirmLabel = "Send Reminder",
            onConfirm = {
                viewModel.escalateFees(
                    childIds = selectedIds.toList(),
                )
                showEscalateDialog = false
                selectedIds = emptySet()
            },
            onDismiss = { showEscalateDialog = false },
            icon = VIcons.Wallet,
        )
    }
}

@Composable
private fun FeeEscalationCard(
    student: TeacherFeeStudentDto,
    isSelected: Boolean,
    onToggle: () -> Unit,
) {
    VCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                if (isSelected) VIcons.Check else VIcons.Close,
                contentDescription = null,
                tint = if (isSelected) VColors.violet else VColors.ink3,
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(student.childName, style = VTypography.body, fontWeight = FontWeight.SemiBold, color = VColors.ink)
                student.className?.let {
                    Text(it, style = VTypography.caption, color = VColors.ink3)
                }
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "₹${"%,.0f".format(student.dueAmount)} due",
                        style = VTypography.body,
                        fontWeight = FontWeight.Bold,
                        color = VColors.error,
                    )
                    VBadge(
                        text = student.status,
                        tone = if (student.status == "OVERDUE") VBadgeTone.Danger else VBadgeTone.Neutral,
                    )
                }
            }
        }
    }
}
