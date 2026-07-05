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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherSyllabusViewModel
import com.littlebridge.enrollplus.ui.v2.components.progress.VProgressBar
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TeacherSyllabusScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TeacherSyllabusViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    TeacherOverlayScaffold(title = "Syllabus", onBack = onBack, modifier = modifier) {
        Text(state.className, style = VTypography.GreetingTitle.copy(color = VColors.OnSurface))
        Spacer(Modifier.height(16.dp))

        if (state.isLoading && state.units.isEmpty()) {
            StatusBox("Loading syllabus...")
            return@TeacherOverlayScaffold
        }

        if (state.error != null && state.units.isEmpty()) {
            StatusBox(state.error!!, isError = true)
            return@TeacherOverlayScaffold
        }

        if (state.units.isEmpty()) {
            StatusBox("No syllabus units available")
            return@TeacherOverlayScaffold
        }

        val progress = if (state.totalCount > 0) state.coveredCount.toFloat() / state.totalCount else 0f
        Row(
            Modifier.fillMaxWidth().clip(VShapes.Lg).background(VColors.SurfaceContainerLow).padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Coverage", style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
            Text("${state.coveredCount}/${state.totalCount}", style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold))
        }
        Spacer(Modifier.height(8.dp))
        VProgressBar(progress = progress, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(20.dp))

        state.units.forEach { unit ->
            SyllabusUnitRow(unit.title, unit.isCovered)
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SyllabusUnitRow(title: String, isCovered: Boolean) {
    Row(
        Modifier.fillMaxWidth().clip(VShapes.Lg).background(VColors.SurfaceContainerLow).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(Modifier.size(8.dp).clip(androidx.compose.foundation.shape.CircleShape).background(if (isCovered) VColors.Tertiary else VColors.Outline))
        Text(title, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold), modifier = Modifier.weight(1f))
        Text(if (isCovered) "Covered" else "Pending", style = VTypography.NavLabel.copy(color = if (isCovered) VColors.Tertiary else VColors.OnSurfaceVariant))
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
