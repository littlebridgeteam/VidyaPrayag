package com.littlebridge.enrollplus.ui.v2.screens.premium.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherClassesViewModel
import com.littlebridge.enrollplus.ui.v2.components.typography.VSectionHeader
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.PremiumTheme
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TeacherClassesScreen(
    modifier: Modifier = Modifier,
    viewModel: TeacherClassesViewModel = koinViewModel(),
) = PremiumTheme(isDark = false) {
    val state by viewModel.state.collectAsStateV2()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VColors.Surface)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
    ) {
        Spacer(Modifier.height(16.dp))

        if (state.isLoading && state.classes.isEmpty()) {
            StatusBox("Loading classes...")
            return@Column
        }

        if (state.error != null && state.classes.isEmpty()) {
            StatusBox(state.error!!, isError = true)
            return@Column
        }

        if (state.classes.isEmpty()) {
            StatusBox("No classes assigned yet")
            return@Column
        }

        VSectionHeader("My Classes")
        Spacer(Modifier.height(12.dp))

        state.classes.forEach { cls ->
            ClassCard(
                className = cls.className,
                section = cls.section,
                subject = cls.subject,
                studentCount = cls.studentCount,
                attendanceMarked = cls.todayAttendanceMarked,
                atRiskCount = cls.atRiskCount,
                isClassTeacher = cls.isClassTeacher,
            )
            Spacer(Modifier.height(12.dp))
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ClassCard(
    className: String,
    section: String,
    subject: String,
    studentCount: Int,
    attendanceMarked: Boolean,
    atRiskCount: Int,
    isClassTeacher: Boolean,
) {
    Column(
        Modifier.fillMaxWidth().clip(VShapes.Xl).background(VColors.SurfaceContainerLow).padding(20.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("$className-$section", style = VTypography.GreetingTitle.copy(color = VColors.OnSurface))
            if (isClassTeacher) {
                Box(Modifier.padding(horizontal = 6.dp, vertical = 2.dp).clip(VShapes.Full).background(VColors.PrimaryContainer)) {
                    Text("Class Teacher", style = VTypography.NavLabel.copy(color = VColors.OnPrimaryContainer, fontWeight = FontWeight.SemiBold), modifier = Modifier.padding(horizontal = 4.dp))
                }
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(subject, style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(Icons.Filled.Group, contentDescription = null, tint = VColors.OnSurfaceVariant, modifier = Modifier.size(16.dp))
                Text("$studentCount students", style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant))
            }
            if (atRiskCount > 0) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Filled.Warning, contentDescription = null, tint = VColors.WarmOrange, modifier = Modifier.size(16.dp))
                    Text("$atRiskCount at risk", style = VTypography.NavLabel.copy(color = VColors.WarmOrange))
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(
            Modifier.fillMaxWidth().clip(VShapes.Lg)
                .background(if (attendanceMarked) VColors.TertiaryContainer else VColors.PrimaryContainer)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                if (attendanceMarked) "Attendance Marked ✓" else "Mark Attendance",
                style = VTypography.NavLabel.copy(
                    color = if (attendanceMarked) VColors.OnTertiaryContainer else VColors.OnPrimaryContainer,
                    fontWeight = FontWeight.SemiBold,
                ),
            )
        }
    }
}

@Composable
private fun StatusBox(msg: String, isError: Boolean = false) {
    Box(
        Modifier.fillMaxWidth().height(120.dp).clip(VShapes.Lg)
            .background(if (isError) VColors.ErrorContainer else VColors.SurfaceContainerLow),
        contentAlignment = Alignment.Center,
    ) {
        Text(msg, style = VTypography.UpdateText.copy(color = if (isError) VColors.OnErrorContainer else VColors.OnSurfaceVariant))
    }
}
