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
fun TeacherStudentProfileScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    studentId: String = "",
) {
    TeacherOverlayScaffold(title = "Student Profile", onBack = onBack, modifier = modifier) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Box(Modifier.size(72.dp).clip(CircleShape).background(VColors.PrimaryContainer), contentAlignment = Alignment.Center) {
                Text("S", style = VTypography.LandingStatValue.copy(color = VColors.OnPrimaryContainer))
            }
        }
        Spacer(Modifier.height(12.dp))
        Text("Student Name", style = VTypography.GreetingTitle.copy(color = VColors.OnSurface), modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        Spacer(Modifier.height(4.dp))
        Text("Grade 5 · Section A · Roll #12", style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant), modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        Spacer(Modifier.height(24.dp))
        InfoRow("Attendance", "95%")
        Spacer(Modifier.height(8.dp))
        InfoRow("Overall Grade", "A")
        Spacer(Modifier.height(8.dp))
        InfoRow("Subjects", "6")
        Spacer(Modifier.height(8.dp))
        InfoRow("At Risk", "No")
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        Modifier.fillMaxWidth().clip(VShapes.Lg).background(VColors.SurfaceContainerLow).padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
        Text(value, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold))
    }
}
