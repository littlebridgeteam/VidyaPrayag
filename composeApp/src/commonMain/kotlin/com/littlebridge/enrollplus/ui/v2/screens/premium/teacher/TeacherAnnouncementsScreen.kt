package com.littlebridge.enrollplus.ui.v2.screens.premium.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.components.buttons.VPrimaryButton
import com.littlebridge.enrollplus.ui.v2.components.form.VTextInput
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

@Composable
fun TeacherAnnouncementsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TeacherOverlayScaffold(title = "Announcements", onBack = onBack, modifier = modifier) {
        Text("Create Announcement", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
        Spacer(Modifier.height(16.dp))
        VTextInput(value = "", onValueChange = {}, label = "Title", placeholder = "Announcement title", authStyle = false, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        VTextInput(value = "", onValueChange = {}, label = "Message", placeholder = "Announcement message", authStyle = false, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        VTextInput(value = "", onValueChange = {}, label = "Audience", placeholder = "Select class or all", authStyle = false, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        VPrimaryButton(text = "Post Announcement", onClick = {}, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(24.dp))
        Text("Recent Announcements", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
        Spacer(Modifier.height(12.dp))
        AnnouncementItem("PTM Reminder", "Parent-Teacher Meeting on Feb 20", "Jan 18")
        Spacer(Modifier.height(8.dp))
        AnnouncementItem("Homework Update", "New homework assigned for Math", "Jan 15")
    }
}

@Composable
private fun AnnouncementItem(title: String, body: String, time: String) {
    Column(Modifier.fillMaxWidth().clip(VShapes.Lg).background(VColors.SurfaceContainerLow).padding(16.dp)) {
        Text(title, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold))
        Spacer(Modifier.height(4.dp))
        Text(body, style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
        Spacer(Modifier.height(4.dp))
        Text(time, style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant))
    }
}
