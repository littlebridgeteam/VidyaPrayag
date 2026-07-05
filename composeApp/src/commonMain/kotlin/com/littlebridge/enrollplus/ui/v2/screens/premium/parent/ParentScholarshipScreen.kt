package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

@Composable
fun ParentScholarshipScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ParentOverlayScaffold(title = "Scholarships", onBack = onBack, modifier = modifier) {
        Text("Available scholarship opportunities for your child.", style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
        Spacer(Modifier.height(20.dp))
        ScholarshipCard("Merit Scholarship", "Up to 50% fee waiver", "Based on academic performance", "Deadline: Mar 15")
        Spacer(Modifier.height(12.dp))
        ScholarshipCard("Need-Based Aid", "Up to 100% fee waiver", "For families with financial need", "Deadline: Apr 30")
        Spacer(Modifier.height(12.dp))
        ScholarshipCard("Sports Excellence", "Up to 25% fee waiver", "For outstanding athletic achievement", "Deadline: May 15")
    }
}

@Composable
private fun ScholarshipCard(title: String, amount: String, desc: String, deadline: String) {
    Column(
        Modifier.fillMaxWidth().clip(VShapes.Lg).background(VColors.SurfaceContainerLow).padding(20.dp),
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
