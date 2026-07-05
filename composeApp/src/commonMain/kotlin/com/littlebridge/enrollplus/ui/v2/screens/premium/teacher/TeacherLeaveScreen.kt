package com.littlebridge.enrollplus.ui.v2.screens.premium.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherLeaveViewModel
import com.littlebridge.enrollplus.ui.v2.components.buttons.VPrimaryButton
import com.littlebridge.enrollplus.ui.v2.components.form.VTextInput
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TeacherLeaveScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TeacherLeaveViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    TeacherOverlayScaffold(title = "Leave Requests", onBack = onBack, modifier = modifier) {
        VSectionHeader("Pending Requests")
        Spacer(Modifier.height(12.dp))

        if (state.loading) {
            StatusBox("Loading leave requests...")
            return@TeacherOverlayScaffold
        }

        if (state.error != null) {
            StatusBox(state.error!!, isError = true)
            return@TeacherOverlayScaffold
        }

        if (state.requests.isEmpty()) {
            StatusBox("No leave requests to review")
        } else {
            state.requests.forEach { req ->
                LeaveRequestCard(req.studentName, req.className ?: "", req.dateFrom, req.dateTo, req.reason, req.status)
                Spacer(Modifier.height(12.dp))
            }
        }

        Spacer(Modifier.height(24.dp))
        Text("Apply for Leave", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
        Spacer(Modifier.height(12.dp))
        VTextInput(value = "", onValueChange = {}, label = "From Date", placeholder = "YYYY-MM-DD", authStyle = false, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        VTextInput(value = "", onValueChange = {}, label = "To Date", placeholder = "YYYY-MM-DD", authStyle = false, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        VTextInput(value = "", onValueChange = {}, label = "Reason", placeholder = "Reason for leave", authStyle = false, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        VPrimaryButton(text = "Submit Leave Request", onClick = {}, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun LeaveRequestCard(name: String, className: String, from: String, to: String, reason: String, status: String) {
    val statusColor = when (status) {
        "Approved" -> VColors.Tertiary
        "Rejected" -> VColors.Error
        else -> VColors.WarmOrange
    }
    Column(Modifier.fillMaxWidth().clip(VShapes.Lg).background(VColors.SurfaceContainerLow).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(name, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold))
            Text(status, style = VTypography.NavLabel.copy(color = statusColor, fontWeight = FontWeight.SemiBold))
        }
        Spacer(Modifier.height(4.dp))
        Text("$className · $from to $to", style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant))
        Spacer(Modifier.height(4.dp))
        Text(reason, style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
    }
}

@Composable
private fun StatusBox(msg: String, isError: Boolean = false) {
    Box(
        Modifier.fillMaxWidth().height(120.dp).clip(VShapes.Lg)
            .background(if (isError) VColors.ErrorContainer else VColors.SurfaceContainerLow),
        contentAlignment = Alignment.Center,
    ) {
        Text(msg, style = VTypography.UpdateText.copy(color = if (isError) VColors.OnErrorContainer else VColors.OnSurfaceVariant))
    }
}

@Composable
private fun VSectionHeader(text: String) {
    Text(text, style = VTypography.SectionHeader.copy(color = VColors.OnSurface, fontWeight = FontWeight.Bold))
}
