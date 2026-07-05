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
fun ParentDailySummaryScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ParentOverlayScaffold(title = "Daily Summary", onBack = onBack, modifier = modifier) {
        Text("Today's Summary", style = VTypography.GreetingTitle.copy(color = VColors.OnSurface))
        Spacer(Modifier.height(16.dp))
        SummaryRow("Attendance", "Present")
        Spacer(Modifier.height(8.dp))
        SummaryRow("Classes Attended", "6 / 6")
        Spacer(Modifier.height(8.dp))
        SummaryRow("Homework Assigned", "2 tasks")
        Spacer(Modifier.height(8.dp))
        SummaryRow("Topics Covered", "Algebra, Photosynthesis")
        Spacer(Modifier.height(8.dp))
        SummaryRow("Quiz Completed", "Science Quiz — 8/10")
        Spacer(Modifier.height(20.dp))
        Text("Teacher's Note", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
        Spacer(Modifier.height(8.dp))
        Text("Good participation in class today. Completed all assigned work.", style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        Modifier.fillMaxWidth().clip(VShapes.Lg).background(VColors.SurfaceContainerLow).padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
        Text(value, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold))
    }
}
