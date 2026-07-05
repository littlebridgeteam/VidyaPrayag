package com.littlebridge.enrollplus.ui.v2.screens.premium.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
fun TeacherPewsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TeacherOverlayScaffold(title = "PEWS — Early Warning", onBack = onBack, modifier = modifier) {
        Text("Students at Risk", style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
        Spacer(Modifier.height(16.dp))
        PewsCard("Aarav Sharma", "Grade 5-A", "High Risk", "Attendance dropping, grades declining", VColors.Error)
        Spacer(Modifier.height(12.dp))
        PewsCard("Priya Patel", "Grade 5-B", "Medium Risk", "Homework incomplete for 3 days", VColors.WarmOrange)
        Spacer(Modifier.height(12.dp))
        PewsCard("Ishaan Gupta", "Grade 6-A", "Low Risk", "Slight dip in participation", VColors.Primary)
    }
}

@Composable
private fun PewsCard(name: String, classInfo: String, riskLevel: String, description: String, color: androidx.compose.ui.graphics.Color) {
    Column(Modifier.fillMaxWidth().clip(VShapes.Lg).background(VColors.SurfaceContainerLow).padding(16.dp)) {
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically, horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Column {
                Text(name, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold))
                Text(classInfo, style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant))
            }
            Box(Modifier.padding(horizontal = 8.dp, vertical = 3.dp).clip(VShapes.Full).background(color.copy(alpha = 0.15f))) {
                Text(riskLevel, style = VTypography.NavLabel.copy(color = color, fontWeight = FontWeight.SemiBold), modifier = Modifier.padding(horizontal = 6.dp))
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(description, style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
    }
}
