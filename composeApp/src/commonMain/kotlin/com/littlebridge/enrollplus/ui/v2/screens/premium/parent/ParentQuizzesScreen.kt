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
fun ParentQuizzesScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ParentOverlayScaffold(title = "Quizzes", onBack = onBack, modifier = modifier) {
        QuizCard("Math Quiz 3", "Algebra & Geometry", "10 questions", "Available", true)
        Spacer(Modifier.height(12.dp))
        QuizCard("Science Quiz 5", "Living Things", "15 questions", "Completed · 8/10", false)
        Spacer(Modifier.height(12.dp))
        QuizCard("English Quiz 2", "Grammar Basics", "10 questions", "Completed · 9/10", false)
    }
}

@Composable
private fun QuizCard(title: String, topic: String, questions: String, status: String, canStart: Boolean) {
    Column(Modifier.fillMaxWidth().clip(VShapes.Lg).background(VColors.SurfaceContainerLow).padding(20.dp)) {
        Text(title, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold))
        Spacer(Modifier.height(4.dp))
        Text(topic, style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
        Spacer(Modifier.height(4.dp))
        Text(questions, style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant))
        Spacer(Modifier.height(8.dp))
        Text(status, style = VTypography.NavLabel.copy(color = if (canStart) VColors.Primary else VColors.Tertiary, fontWeight = FontWeight.SemiBold))
        if (canStart) {
            Spacer(Modifier.height(16.dp))
            VPrimaryButton(text = "Start Quiz", onClick = { /* TODO: start quiz */ }, modifier = Modifier.fillMaxWidth())
        }
    }
}
