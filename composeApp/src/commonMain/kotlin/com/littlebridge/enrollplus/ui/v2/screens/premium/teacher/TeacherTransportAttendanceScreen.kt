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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

@Composable
fun TeacherTransportAttendanceScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TeacherOverlayScaffold(title = "Transport Attendance", onBack = onBack, modifier = modifier) {
        Text("Bus Route 12 — Morning", style = VTypography.GreetingTitle.copy(color = VColors.OnSurface))
        Spacer(Modifier.height(4.dp))
        Text("28 students assigned", style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
        Spacer(Modifier.height(20.dp))
        TransportStudentRow("Aarav Sharma", "Stop 1", true)
        Spacer(Modifier.height(8.dp))
        TransportStudentRow("Priya Patel", "Stop 1", true)
        Spacer(Modifier.height(8.dp))
        TransportStudentRow("Ishaan Gupta", "Stop 2", false)
        Spacer(Modifier.height(8.dp))
        TransportStudentRow("Sneha Verma", "Stop 3", false)
        Spacer(Modifier.height(8.dp))
        TransportStudentRow("Vikram Reddy", "Stop 3", true)
    }
}

@Composable
private fun TransportStudentRow(name: String, stop: String, boarded: Boolean) {
    Row(
        Modifier.fillMaxWidth().clip(VShapes.Lg).background(VColors.SurfaceContainerLow).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(Modifier.size(20.dp).clip(CircleShape).background(if (boarded) VColors.Tertiary else VColors.SurfaceContainerHigh))
        Column(Modifier.weight(1f)) {
            Text(name, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold))
            Text(stop, style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant))
        }
        Text(if (boarded) "Boarded" else "Waiting", style = VTypography.NavLabel.copy(color = if (boarded) VColors.Tertiary else VColors.OnSurfaceVariant))
    }
}
