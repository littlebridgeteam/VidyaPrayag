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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherHomeworkViewModel
import com.littlebridge.enrollplus.ui.v2.components.buttons.VPrimaryButton
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TeacherHomeworkScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TeacherHomeworkViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    TeacherOverlayScaffold(title = "Homework", onBack = onBack, modifier = modifier) {
        if (state.isLoading && state.items.isEmpty()) {
            StatusBox("Loading homework...")
            return@TeacherOverlayScaffold
        }

        if (state.error != null && state.items.isEmpty()) {
            StatusBox(state.error!!, isError = true)
            return@TeacherOverlayScaffold
        }

        if (state.items.isEmpty()) {
            StatusBox("No homework assigned yet")
            return@TeacherOverlayScaffold
        }

        state.items.forEach { hw ->
            HomeworkCard(
                title = hw.title,
                description = hw.description,
                className = "${hw.className}-${hw.section}",
                subject = hw.subject,
                dueDate = hw.dueDate,
                submitted = hw.submittedCount,
                total = hw.totalCount,
            )
            Spacer(Modifier.height(12.dp))
        }

        Spacer(Modifier.height(16.dp))
        VPrimaryButton(text = "Assign Homework", onClick = {}, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun HomeworkCard(title: String, description: String, className: String, subject: String, dueDate: String, submitted: Int, total: Int) {
    Column(Modifier.fillMaxWidth().clip(VShapes.Lg).background(VColors.SurfaceContainerLow).padding(20.dp)) {
        Text(title, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold))
        Spacer(Modifier.height(4.dp))
        Text(description, style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(className, style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant))
            Text(subject, style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant))
            Text("Due: $dueDate", style = VTypography.NavLabel.copy(color = VColors.WarmOrange))
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text("$submitted / $total submitted", style = VTypography.NavLabel.copy(color = VColors.Tertiary, fontWeight = FontWeight.SemiBold))
            if (total - submitted > 0) {
                Text("${total - submitted} pending", style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant))
            }
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
