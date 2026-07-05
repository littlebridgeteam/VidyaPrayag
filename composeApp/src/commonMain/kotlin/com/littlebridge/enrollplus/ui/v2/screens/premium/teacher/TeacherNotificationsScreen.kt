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
fun TeacherNotificationsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TeacherOverlayScaffold(title = "Notifications", onBack = onBack, modifier = modifier) {
        NotificationRow("New Leave Request", "Aarav Sharma's parent submitted a leave request", "2 min ago", true)
        Spacer(Modifier.height(8.dp))
        NotificationRow("Homework Submitted", "5 students submitted Math homework", "1 hour ago", false)
        Spacer(Modifier.height(8.dp))
        NotificationRow("PTM Scheduled", "PTM for Grade 5-A on Feb 20", "3 hours ago", false)
        Spacer(Modifier.height(8.dp))
        NotificationRow("Syllabus Update", "Science syllabus coverage updated", "Yesterday", false)
        Spacer(Modifier.height(8.dp))
        NotificationRow("New Message", "Principal sent a message to all teachers", "2 days ago", false)
    }
}

@Composable
private fun NotificationRow(title: String, body: String, time: String, unread: Boolean) {
    Row(
        Modifier.fillMaxWidth().clip(VShapes.Lg).background(VColors.SurfaceContainerLow).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(if (unread) VColors.Primary else VColors.Outline))
        Column(Modifier.weight(1f)) {
            Text(title, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold))
            Text(body, style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant), maxLines = 2)
        }
        Text(time, style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant))
    }
}
