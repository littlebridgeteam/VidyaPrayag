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
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherLessonPlanViewModel
import com.littlebridge.enrollplus.ui.v2.components.buttons.VPrimaryButton
import com.littlebridge.enrollplus.ui.v2.components.form.VTextInput
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TeacherLessonPlanScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TeacherLessonPlanViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    TeacherOverlayScaffold(title = "Lesson Plans", onBack = onBack, modifier = modifier) {
        if (state.isLoading && state.items.isEmpty()) {
            StatusBox("Loading lesson plans...")
            return@TeacherOverlayScaffold
        }

        if (state.error != null && state.items.isEmpty()) {
            StatusBox(state.error!!, isError = true)
            return@TeacherOverlayScaffold
        }

        if (state.items.isEmpty()) {
            StatusBox("No lesson plans created yet")
            return@TeacherOverlayScaffold
        }

        state.items.forEach { plan ->
            LessonPlanRow(plan.title, plan.status, plan.subjectName)
            Spacer(Modifier.height(8.dp))
        }

        Spacer(Modifier.height(20.dp))
        VPrimaryButton(text = "Create Lesson Plan", onClick = {}, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun LessonPlanRow(title: String, status: String, className: String) {
    val statusColor = when (status) {
        "planned" -> VColors.Primary
        "in_progress" -> VColors.WarmOrange
        "completed" -> VColors.Tertiary
        else -> VColors.Outline
    }
    Row(
        Modifier.fillMaxWidth().clip(VShapes.Lg).background(VColors.SurfaceContainerLow).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold))
            Text(className, style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant))
        }
        Text(status.replace("_", " ").replaceFirstChar { it.uppercase() }, style = VTypography.NavLabel.copy(color = statusColor, fontWeight = FontWeight.SemiBold))
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
