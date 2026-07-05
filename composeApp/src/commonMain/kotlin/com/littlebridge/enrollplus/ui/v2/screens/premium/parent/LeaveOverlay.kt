package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentLeaveDto
import com.littlebridge.enrollplus.feature.parent.presentation.ParentLeaveViewModel
import com.littlebridge.enrollplus.ui.v2.components.buttons.VPrimaryButton
import com.littlebridge.enrollplus.ui.v2.components.misc.VShimmerBoxPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VStateHostPremium
import com.littlebridge.enrollplus.ui.v2.components.navigation.VFilterChip
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LeaveOverlay(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ParentLeaveViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()

    LaunchedEffect(state.submittedOk, state.submitError) {
        if (state.submittedOk || state.submitError != null) {
            kotlinx.coroutines.delay(1500)
            viewModel.consumeSubmitResult()
        }
    }

    ParentOverlayScaffold(
        title = "Leave Application",
        onBack = onBack,
        modifier = modifier,
    ) {
        // Child selector
        if (state.children.size > 1) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                state.children.forEach { child ->
                    VFilterChip(
                        label = child.name,
                        active = child.id == state.selectedChildId,
                        onClick = { viewModel.selectChild(child.id) },
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        // Apply form
        LeaveForm(
            state = state,
            onApply = { from, to, reason -> viewModel.apply(from, to, reason) },
        )

        Spacer(Modifier.height(24.dp))

        // Leave history
        Text(
            text = "Leave History",
            style = VTypography.SectionHeader.copy(color = VColors.OnSurface),
        )
        Spacer(Modifier.height(12.dp))

        VStateHostPremium(
            loading = state.loading,
            error = state.error,
            isEmpty = state.isEmpty,
            modifier = Modifier.fillMaxSize(),
            emptyTitle = "No leave applications yet",
            emptyIcon = Icons.Filled.Description,
            onRetry = { viewModel.load() },
            skeleton = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    repeat(3) { VShimmerBoxPremium(height = 80.dp, shape = VShapes.Lg) }
                }
            },
        ) {
            state.requests.forEach { leave ->
                LeaveHistoryCard(leave = leave)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun LeaveForm(
    state: com.littlebridge.enrollplus.feature.parent.presentation.ParentLeaveState,
    onApply: (String, String, String) -> Unit,
) {
    var dateFrom by remember { mutableStateOf("") }
    var dateTo by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.Xl)
            .background(VColors.SurfaceContainerLowest)
            .padding(20.dp),
    ) {
        Text(
            text = "Apply for Leave",
            style = VTypography.UpdateTitle.copy(color = VColors.OnSurface),
        )
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = dateFrom,
            onValueChange = { dateFrom = it },
            label = { Text("From (YYYY-MM-DD)", style = VTypography.ThreadPreview) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = outlinedTextFieldColors(),
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = dateTo,
            onValueChange = { dateTo = it },
            label = { Text("To (YYYY-MM-DD)", style = VTypography.ThreadPreview) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = outlinedTextFieldColors(),
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = reason,
            onValueChange = { reason = it },
            label = { Text("Reason", style = VTypography.ThreadPreview) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            colors = outlinedTextFieldColors(),
        )
        Spacer(Modifier.height(16.dp))

        // Submit feedback
        if (state.submitting) {
            Text(
                text = "Submitting...",
                style = VTypography.ThreadPreview.copy(color = VColors.OnSurfaceVariant),
            )
        } else if (state.submittedOk) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(Icons.Filled.Check, contentDescription = null, tint = VColors.Primary, modifier = Modifier.size(16.dp))
                Text(
                    text = "Leave application submitted",
                    style = VTypography.ThreadPreview.copy(color = VColors.Primary),
                )
            }
        } else if (state.submitError != null) {
            val errMsg = state.submitError!!
            Text(
                text = errMsg,
                style = VTypography.ThreadPreview.copy(color = VColors.Error),
            )
        }

        Spacer(Modifier.height(12.dp))

        VPrimaryButton(
            text = "Apply",
            onClick = { onApply(dateFrom, dateTo, reason) },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun LeaveHistoryCard(leave: ParentLeaveDto) {
    val statusColor = when (leave.status.lowercase()) {
        "approved" -> VColors.Primary
        "pending" -> VColors.WarmOrange
        "rejected" -> VColors.Error
        else -> VColors.Outline
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.Lg)
            .background(VColors.SurfaceContainerLow)
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = leave.childName,
                style = VTypography.UpdateTitle.copy(color = VColors.OnSurface),
            )
            Box(
                modifier = Modifier
                    .clip(VShapes.Full)
                    .background(statusColor.copy(alpha = 0.12f))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Text(
                    text = leave.status,
                    style = VTypography.ThreadTime.copy(color = statusColor),
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = VColors.Outline, modifier = Modifier.size(14.dp))
            Text(
                text = "${leave.dateFrom} → ${leave.dateTo}",
                style = VTypography.ThreadPreview.copy(color = VColors.OnSurfaceVariant),
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = leave.reason,
            style = VTypography.BodyMedium.copy(color = VColors.OnSurfaceVariant),
        )
    }
}

@Composable
private fun outlinedTextFieldColors() = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
    focusedBorderColor = VColors.Primary,
    unfocusedBorderColor = VColors.SurfaceContainerHigh,
    focusedLabelColor = VColors.Primary,
    unfocusedLabelColor = VColors.OnSurfaceVariant,
)
