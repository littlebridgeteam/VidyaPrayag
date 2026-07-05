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
fun TeacherReportDraftScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TeacherOverlayScaffold(title = "Report Draft", onBack = onBack, modifier = modifier) {
        Text("Draft Report Card", style = VTypography.GreetingTitle.copy(color = VColors.OnSurface))
        Spacer(Modifier.height(16.dp))
        DraftRow("Mathematics", "Excellent", "Consistent performance")
        Spacer(Modifier.height(8.dp))
        DraftRow("Science", "Good", "Improving in practicals")
        Spacer(Modifier.height(8.dp))
        DraftRow("English", "Very Good", "Strong writing skills")
        Spacer(Modifier.height(8.dp))
        DraftRow("Hindi", "Good", "Needs to practice speaking")
        Spacer(Modifier.height(20.dp))
        Text("Overall Remarks", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
        Spacer(Modifier.height(8.dp))
        Text("The student has shown good progress this term. Focus on consistent effort across all subjects.", style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
    }
}

@Composable
private fun DraftRow(subject: String, grade: String, remark: String) {
    Row(
        Modifier.fillMaxWidth().clip(VShapes.Lg).background(VColors.SurfaceContainerLow).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(subject, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold), modifier = Modifier.weight(1f))
        Column(horizontalAlignment = Alignment.End) {
            Text(grade, style = VTypography.NavLabel.copy(color = VColors.Primary, fontWeight = FontWeight.SemiBold))
            Text(remark, style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant))
        }
    }
}
