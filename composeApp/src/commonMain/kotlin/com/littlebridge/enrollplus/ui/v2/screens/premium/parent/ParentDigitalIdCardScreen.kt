package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.components.carousel.VStaggeredItem
import com.littlebridge.enrollplus.ui.v2.components.misc.VBrandLogoPremium
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

@Composable
fun ParentDigitalIdCardScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    childId: String = "",
    isTeacher: Boolean = false,
) {
    ParentOverlayScaffold(title = "Digital ID Card", onBack = onBack, modifier = modifier) {
        VStaggeredItem(delayMs = 0) {
            // ID card
            Column(
            Modifier.fillMaxWidth().clip(VShapes.Xl).background(
                androidx.compose.ui.graphics.Brush.linearGradient(listOf(VColors.Primary, VColors.PrimaryDeep))
            ).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            VBrandLogoPremium(size = 60.dp)
            Spacer(Modifier.height(16.dp))
            Text("Enroll+", style = VTypography.BrandText.copy(color = VColors.OnPrimary))
            Spacer(Modifier.height(24.dp))
            Box(
                Modifier.size(80.dp).clip(CircleShape).background(VColors.GlassWhite15),
                contentAlignment = Alignment.Center,
            ) {
                Text("S", style = VTypography.LandingStatValue.copy(color = VColors.OnPrimary))
            }
            Spacer(Modifier.height(12.dp))
            Text("Student ID Card", style = VTypography.Eyebrow.copy(color = VColors.OnPrimary.copy(alpha = 0.7f)))
            Text("Student Name", style = VTypography.GreetingTitle.copy(color = VColors.OnPrimary), textAlign = TextAlign.Center)
            Text("Grade 5 · Section A", style = VTypography.UpdateText.copy(color = VColors.OnPrimary.copy(alpha = 0.7f)))
            Spacer(Modifier.height(16.dp))
            Text("ID: SVM2025001", style = VTypography.NavLabel.copy(color = VColors.OnPrimary.copy(alpha = 0.7f)))
            Text("Valid: 2025-2026", style = VTypography.NavLabel.copy(color = VColors.OnPrimary.copy(alpha = 0.7f)))
        }
        }
    }
}
