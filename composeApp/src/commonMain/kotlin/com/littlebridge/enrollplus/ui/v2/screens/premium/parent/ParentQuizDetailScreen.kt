package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

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
import com.littlebridge.enrollplus.ui.v2.components.buttons.VPrimaryButton
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

@Composable
fun ParentQuizDetailScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    quizId: String = "",
) {
    ParentOverlayScaffold(title = "Quiz Details", onBack = onBack, modifier = modifier) {
        Text("Math Quiz 3", style = VTypography.GreetingTitle.copy(color = VColors.OnSurface))
        Spacer(Modifier.height(8.dp))
        Text("Algebra & Geometry · 10 questions · 30 min", style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
        Spacer(Modifier.height(24.dp))
        Text("Instructions", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
        Spacer(Modifier.height(8.dp))
        Text("• Each question has one correct answer\n• You can review answers before submitting\n• The quiz will auto-submit when time expires", style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
        Spacer(Modifier.height(24.dp))
        VPrimaryButton(text = "Start Quiz", onClick = {}, modifier = Modifier.fillMaxWidth())
    }
}
