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
fun TeacherReportReviewScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TeacherOverlayScaffold(title = "Report Review Queue", onBack = onBack, modifier = modifier) {
        Text("Pending Review", style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
        Spacer(Modifier.height(16.dp))
        ReviewRow("Aarav Sharma", "Grade 5-A", "Draft Submitted", "Jan 18")
        Spacer(Modifier.height(8.dp))
        ReviewRow("Priya Patel", "Grade 5-B", "Draft Submitted", "Jan 17")
        Spacer(Modifier.height(8.dp))
        ReviewRow("Ishaan Gupta", "Grade 6-A", "Under Review", "Jan 15")
        Spacer(Modifier.height(8.dp))
        ReviewRow("Sneha Verma", "Grade 6-B", "Approved", "Jan 12")
    }
}

@Composable
private fun ReviewRow(name: String, classInfo: String, status: String, date: String) {
    val statusColor = when (status) {
        "Approved" -> VColors.Tertiary
        "Under Review" -> VColors.WarmOrange
        else -> VColors.Primary
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
            Text("$classInfo · $date", style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant))
        }
        Text(status, style = VTypography.NavLabel.copy(color = statusColor, fontWeight = FontWeight.SemiBold))
    }
}
