package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.components.buttons.VPrimaryButton
import com.littlebridge.enrollplus.ui.v2.components.carousel.VStaggeredItem
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
import com.littlebridge.enrollplus.ui.v2.modifiers.shapeMorph
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VMotion
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

@Composable
fun ParentScholarshipScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ParentOverlayScaffold(title = "Scholarships", onBack = onBack, modifier = modifier) {
        VStaggeredItem(delayMs = 0) {
            Text("Available scholarship opportunities for your child.", style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
        }
        Spacer(Modifier.height(20.dp))
        VStaggeredItem(delayMs = 60) {
            ScholarshipCard("Merit Scholarship", "Up to 50% fee waiver", "Based on academic performance", "Deadline: Mar 15")
        }
        Spacer(Modifier.height(12.dp))
        VStaggeredItem(delayMs = 120) {
            ScholarshipCard("Need-Based Aid", "Up to 100% fee waiver", "For families with financial need", "Deadline: Apr 30")
        }
        Spacer(Modifier.height(12.dp))
        VStaggeredItem(delayMs = 180) {
            ScholarshipCard("Sports Excellence", "Up to 25% fee waiver", "For outstanding athletic achievement", "Deadline: May 15")
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
            .clickable(interactionSource = interaction, indication = null) { /* TODO: view scholarship detail */ }
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
        VPrimaryButton(text = "Apply Now", onClick = { /* TODO: apply for scholarship */ }, modifier = Modifier.fillMaxWidth())
    }
}
