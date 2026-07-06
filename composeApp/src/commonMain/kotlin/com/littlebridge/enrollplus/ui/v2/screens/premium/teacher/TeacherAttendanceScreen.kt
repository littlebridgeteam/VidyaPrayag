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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherAttendanceViewModel
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TeacherAttendanceScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    assignmentId: String = "",
    viewModel: TeacherAttendanceViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    TeacherOverlayScaffold(title = "Attendance", onBack = onBack, modifier = modifier) {
        Text(state.scope, style = VTypography.GreetingTitle.copy(color = VColors.OnSurface))
        Spacer(Modifier.height(16.dp))

        if (state.isLoading) {
            StatusBox("Loading students...")
            return@TeacherOverlayScaffold
        }

        if (state.error != null) {
            StatusBox(state.error!!, isError = true)
            return@TeacherOverlayScaffold
        }

        if (state.students.isEmpty()) {
            StatusBox("No students in this class")
            return@TeacherOverlayScaffold
        }

        state.students.forEach { student ->
            AttendanceRow(
                name = student.name,
                rollNumber = student.rollNo,
                status = student.status,
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun AttendanceRow(name: String, rollNumber: String, status: String) {
    val statusColor = when (status) {
        "present" -> VColors.Tertiary
        "absent" -> VColors.Error
        "late" -> VColors.WarmOrange
        else -> VColors.Outline
    }
    Row(
        Modifier.fillMaxWidth().clip(VShapes.Lg).background(VColors.SurfaceContainerLow).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(Modifier.size(40.dp).clip(CircleShape).background(VColors.PrimaryContainer), contentAlignment = Alignment.Center) {
            Text(name.firstOrNull()?.toString() ?: "?", style = VTypography.NavLabel.copy(color = VColors.OnPrimaryContainer, fontWeight = FontWeight.SemiBold))
        }
        Column(Modifier.weight(1f)) {
            Text(name, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold))
            Text("Roll #$rollNumber", style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant))
        }
        Box(Modifier.size(24.dp).clip(CircleShape).background(statusColor.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
            Icon(
                if (status == "present") Icons.Filled.Check else Icons.Filled.Close,
                contentDescription = null,
                tint = statusColor,
                modifier = Modifier.size(16.dp),
            )
        }
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
