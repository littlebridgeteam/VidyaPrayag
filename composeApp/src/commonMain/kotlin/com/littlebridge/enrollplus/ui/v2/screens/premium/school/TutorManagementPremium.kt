package com.littlebridge.enrollplus.ui.v2.screens.premium.school

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.cards.HeroStatPremium
import com.littlebridge.enrollplus.ui.v2.components.cards.VGradientHeroPremium
import com.littlebridge.enrollplus.ui.v2.components.cards.VListTilePremium
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

@Composable
fun TutorManagementPremium(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .navigationBarsPadding(),
    ) {
        VBackHeader(title = "Tutor Management", onBack = onBack)

        Column(
            Modifier.verticalScroll(rememberScrollState()).padding(bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            VGradientHeroPremium(
                title = "AI Tutor Configuration",
                subtitle = "Manage tutor availability and AI provider settings",
                stats = listOf(
                    HeroStatPremium("5", "Modules"),
                    HeroStatPremium("AI", "Powered"),
                    HeroStatPremium("RAG", "Enabled"),
                ),
                onClick = {},
                modifier = Modifier.padding(horizontal = 20.dp),
            )

            Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Tutor Features", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                VListTilePremium(title = "Doubt Resolution", subtitle = "AI-powered doubt resolution with RAG-based context retrieval", onClick = {}, leadingIcon = VIcons.Sparkles)
                VListTilePremium(title = "Pace Tracking", subtitle = "Monitors syllabus coverage pace and generates alerts", onClick = {}, leadingIcon = VIcons.TrendingUp)
                VListTilePremium(title = "Narrator", subtitle = "Generates personalised report card narratives using AI", onClick = {}, leadingIcon = VIcons.FileText)
                VListTilePremium(title = "Caseworker", subtitle = "Tracks at-risk students with AI-driven interventions", onClick = {}, leadingIcon = VIcons.Users)

                Text("Module Status", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                VListTilePremium(title = "Sense", subtitle = "Data collection and signal detection", onClick = {}, leadingIcon = VIcons.Activity)
                VListTilePremium(title = "Triage", subtitle = "Risk scoring and prioritisation", onClick = {}, leadingIcon = VIcons.AlertTriangle)
                VListTilePremium(title = "Learn", subtitle = "Intervention effectiveness tracking", onClick = {}, leadingIcon = VIcons.TrendingUp)
                VListTilePremium(title = "Act", subtitle = "Automated intervention dispatch", onClick = {}, leadingIcon = VIcons.Sparkles)
                VListTilePremium(title = "Insights", subtitle = "Pattern detection and reporting", onClick = {}, leadingIcon = VIcons.Sparkles)
            }
        }
    }
}
