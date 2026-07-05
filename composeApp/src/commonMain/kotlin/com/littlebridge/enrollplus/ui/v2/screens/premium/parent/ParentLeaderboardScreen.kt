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
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

@Composable
fun ParentLeaderboardScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    quizId: String = "",
) {
    ParentOverlayScaffold(title = "Quiz Leaderboard", onBack = onBack, modifier = modifier) {
        Text("Science Quiz 5 — Top Performers", style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold))
        Spacer(Modifier.height(16.dp))
        LeaderboardRow(1, "Aarav Sharma", "10/10", true)
        Spacer(Modifier.height(8.dp))
        LeaderboardRow(2, "Priya Patel", "9/10", false)
        Spacer(Modifier.height(8.dp))
        LeaderboardRow(3, "Your Child", "8/10", true)
        Spacer(Modifier.height(8.dp))
        LeaderboardRow(4, "Ishaan Gupta", "8/10", false)
        Spacer(Modifier.height(8.dp))
        LeaderboardRow(5, "Ananya Singh", "7/10", false)
        Spacer(Modifier.height(8.dp))
        LeaderboardRow(6, "Vikram Reddy", "7/10", false)
        Spacer(Modifier.height(8.dp))
        LeaderboardRow(7, "Sneha Verma", "6/10", false)
    }
}

@Composable
private fun LeaderboardRow(rank: Int, name: String, score: String, isYourChild: Boolean) {
    Row(
        Modifier.fillMaxWidth().clip(VShapes.Lg).background(if (isYourChild) VColors.PrimaryContainer else VColors.SurfaceContainerLow).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            Modifier.size(32.dp).clip(CircleShape).background(if (rank <= 3) VColors.Primary else VColors.SurfaceContainerHigh),
            contentAlignment = Alignment.Center,
        ) {
            Text(rank.toString(), style = VTypography.NavLabel.copy(color = if (rank <= 3) VColors.OnPrimary else VColors.OnSurfaceVariant, fontWeight = FontWeight.Bold))
        }
        Text(name, style = VTypography.UpdateTitle.copy(color = if (isYourChild) VColors.OnPrimaryContainer else VColors.OnSurface, fontWeight = FontWeight.SemiBold), modifier = Modifier.weight(1f))
        Text(score, style = VTypography.UpdateText.copy(color = if (isYourChild) VColors.OnPrimaryContainer else VColors.OnSurfaceVariant))
    }
}
