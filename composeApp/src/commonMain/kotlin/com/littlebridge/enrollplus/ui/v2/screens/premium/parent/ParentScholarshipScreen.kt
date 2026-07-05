package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.parent.presentation.ScholarshipsViewModel
import com.littlebridge.enrollplus.ui.v2.components.buttons.VPrimaryButton
import com.littlebridge.enrollplus.ui.v2.components.carousel.VStaggeredItem
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
import com.littlebridge.enrollplus.ui.v2.modifiers.shapeMorph
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VMotion
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ParentScholarshipScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ScholarshipsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    ParentOverlayScaffold(title = "Scholarships", onBack = onBack, modifier = modifier) {
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
        if (state.scholarships.isEmpty()) {
            EmptyStateCard(
                title = "No Scholarships",
                body = "Available scholarship opportunities will appear here.",
                icon = Icons.AutoMirrored.Filled.MenuBook,
            )
            return@ParentOverlayScaffold
        }

        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            VStaggeredItem(delayMs = 0) {
                Text("Available scholarship opportunities for your child.", style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
            }
            Spacer(Modifier.height(20.dp))
            state.scholarships.forEachIndexed { i, s ->
                VStaggeredItem(delayMs = 60 + i * 60) {
                    ScholarshipCard(s.title, s.amount, s.description, s.timeLeft)
                }
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun ScholarshipCard(title: String, amount: String, desc: String, deadline: String) {
    val interaction = remember { MutableInteractionSource() }
    Column(
        Modifier.fillMaxWidth().clip(VShapes.Lg).background(VColors.SurfaceContainerLow)
            .pressScale(interaction, pressedScale = 0.98f)
            .shapeMorph(interaction, VShapes.LgDp, VShapes.XlDp, VMotion.DurShort2)
            .clickable(interactionSource = interaction, indication = null) { }
            .padding(20.dp),
    ) {
        Text(title, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold))
        Spacer(Modifier.height(4.dp))
        Text(amount, style = VTypography.SectionLink.copy(color = VColors.Primary, fontWeight = FontWeight.SemiBold))
        Spacer(Modifier.height(8.dp))
        Text(desc, style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
        Spacer(Modifier.height(8.dp))
        Text(deadline, style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant))
        Spacer(Modifier.height(16.dp))
        VPrimaryButton(text = "Apply Now", onClick = {}, modifier = Modifier.fillMaxWidth())
    }
}
