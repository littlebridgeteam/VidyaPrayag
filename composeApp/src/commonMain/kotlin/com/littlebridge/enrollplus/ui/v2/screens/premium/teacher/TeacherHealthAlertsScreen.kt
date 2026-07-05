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
fun TeacherHealthAlertsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TeacherOverlayScaffold(title = "Health Alerts", onBack = onBack, modifier = modifier) {
        Text("Students with health alerts", style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
        Spacer(Modifier.height(16.dp))
        HealthAlertCard("Aarav Sharma", "Grade 5-A", "Asthma", "Keep inhaler accessible during PE")
        Spacer(Modifier.height(12.dp))
        HealthAlertCard("Priya Patel", "Grade 5-B", "Peanut Allergy", "Avoid peanut-containing foods in class")
        Spacer(Modifier.height(12.dp))
        HealthAlertCard("Ishaan Gupta", "Grade 6-A", "Diabetes", "Allow snacks during class if needed")
    }
}

@Composable
private fun HealthAlertCard(name: String, classInfo: String, condition: String, note: String) {
    Column(Modifier.fillMaxWidth().clip(VShapes.Lg).background(VColors.ErrorContainer).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(Modifier.size(8.dp).clip(CircleShape).background(VColors.Error))
            Text(name, style = VTypography.UpdateTitle.copy(color = VColors.OnErrorContainer, fontWeight = FontWeight.SemiBold))
            Text(classInfo, style = VTypography.NavLabel.copy(color = VColors.OnErrorContainer.copy(alpha = 0.7f)))
        }
        Spacer(Modifier.height(8.dp))
        Text(condition, style = VTypography.UpdateText.copy(color = VColors.OnErrorContainer, fontWeight = FontWeight.SemiBold))
        Text(note, style = VTypography.NavLabel.copy(color = VColors.OnErrorContainer.copy(alpha = 0.7f)))
    }
}
