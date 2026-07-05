package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

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
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.parent.presentation.TrackProgressViewModel
import com.littlebridge.enrollplus.ui.v2.components.carousel.VStaggeredItem
import com.littlebridge.enrollplus.ui.v2.components.progress.VProgressBar
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ParentTutorProgressScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TrackProgressViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    ParentOverlayScaffold(title = "Tutor Progress", onBack = onBack, modifier = modifier) {
        if (state.isLoading) {
            VStaggeredItem(delayMs = 0) { SkeletonCard(variant = "card") }
            VStaggeredItem(delayMs = 60) { SkeletonCard(variant = "card") }
            return@ParentOverlayScaffold
        }
        if (state.error != null) {
            ErrorStateCard(
                message = state.error ?: "Unknown error",
                onRetry = null,
                modifier = Modifier.padding(vertical = 24.dp),
            )
            return@ParentOverlayScaffold
        }
        if (state.academicCompetencies.isEmpty() && state.badges.isEmpty()) {
            EmptyStateCard(
                title = "No Progress Data",
                body = "Tutoring progress will appear here once available.",
                icon = Icons.AutoMirrored.Filled.MenuBook,
            )
            return@ParentOverlayScaffold
        }

        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            VStaggeredItem(delayMs = 0) {
                Text(state.journeyDescription.ifBlank { "Your child's tutoring progress." }, style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
            }
            Spacer(Modifier.height(20.dp))
            state.academicCompetencies.forEachIndexed { i, comp ->
                VStaggeredItem(delayMs = 60 + i * 60) {
                    SubjectProgress(comp.title, comp.progress)
                }
                Spacer(Modifier.height(12.dp))
            }
            if (state.badges.isNotEmpty()) {
                Spacer(Modifier.height(20.dp))
                VStaggeredItem(delayMs = 240) { Text("Achievements", style = VTypography.SectionHeader.copy(color = VColors.OnSurface)) }
                Spacer(Modifier.height(12.dp))
                state.badges.forEachIndexed { i, badge ->
                    VStaggeredItem(delayMs = 300 + i * 60) {
                        BadgeRow(badge.title, badge.isLocked)
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun SubjectProgress(subject: String, progress: Float) {
    Column(Modifier.fillMaxWidth().clip(VShapes.Lg).background(VColors.SurfaceContainerLow).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(subject, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold))
            Text("${(progress * 100).toInt()}%", style = VTypography.SectionLink.copy(color = VColors.Primary, fontWeight = FontWeight.SemiBold))
        }
        Spacer(Modifier.height(8.dp))
        VProgressBar(progress = progress, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun BadgeRow(title: String, isLocked: Boolean) {
    Row(
        Modifier.fillMaxWidth().clip(VShapes.Lg).background(VColors.SurfaceContainerLow).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(if (isLocked) VColors.Outline else VColors.Tertiary))
        Text(title, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold), modifier = Modifier.weight(1f))
        Text(if (isLocked) "Locked" else "Earned", style = VTypography.NavLabel.copy(color = if (isLocked) VColors.Outline else VColors.Tertiary, fontWeight = FontWeight.SemiBold))
    }
}
