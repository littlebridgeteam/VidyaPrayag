package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

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
fun ParentAnnouncementsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ParentOverlayScaffold(title = "Announcements", onBack = onBack, modifier = modifier) {
        AnnouncementCard("School Closure", "The school will remain closed on Jan 26 for Republic Day.", "Jan 20", "Important")
        Spacer(Modifier.height(12.dp))
        AnnouncementCard("PTM Scheduled", "Parent-Teacher Meeting on Feb 20 from 10 AM to 1 PM.", "Jan 18", "Event")
        Spacer(Modifier.height(12.dp))
        AnnouncementCard("Fee Reminder", "Q4 fees due by Jan 31. Please pay before the deadline.", "Jan 15", "Fees")
        Spacer(Modifier.height(12.dp))
        AnnouncementCard("Sports Day", "Annual Sports Day on Feb 14. All parents are invited.", "Jan 12", "Event")
    }
}

@Composable
private fun AnnouncementCard(title: String, body: String, time: String, tag: String) {
    val tagColor = when (tag) {
        "Important" -> VColors.Error
        "Fees" -> VColors.WarmOrange
        else -> VColors.Primary
    }
    Column(Modifier.fillMaxWidth().clip(VShapes.Lg).background(VColors.SurfaceContainerLow).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(Modifier.padding(horizontal = 8.dp, vertical = 3.dp).clip(VShapes.Full).background(tagColor.copy(alpha = 0.15f))) {
                Text(tag, style = VTypography.NavLabel.copy(color = tagColor, fontWeight = FontWeight.SemiBold), modifier = Modifier.padding(horizontal = 6.dp))
            }
            Text(time, style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant))
        }
        Spacer(Modifier.height(8.dp))
        Text(title, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold))
        Spacer(Modifier.height(4.dp))
        Text(body, style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
    }
}
