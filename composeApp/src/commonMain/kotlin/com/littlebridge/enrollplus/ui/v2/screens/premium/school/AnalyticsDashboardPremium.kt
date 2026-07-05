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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.admin.presentation.AnalyticsDashboardViewModel
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.cards.HeroStatPremium
import com.littlebridge.enrollplus.ui.v2.components.cards.VGradientHeroPremium
import com.littlebridge.enrollplus.ui.v2.components.cards.VListTilePremium
import com.littlebridge.enrollplus.ui.v2.components.cards.VStatCardPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VStateHostPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VShimmerListPremium
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AnalyticsDashboardPremium(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: AnalyticsDashboardViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    LaunchedEffect(Unit) { viewModel.loadOverview() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .navigationBarsPadding(),
    ) {
        VBackHeader(title = "Analytics", onBack = onBack)

        VStateHostPremium(
            loading = state.isLoading,
            error = state.errorMessage,
            isEmpty = state.cards.isEmpty() && !state.isLoading,
            emptyTitle = "No analytics data",
            onRetry = { viewModel.loadOverview() },
            skeleton = {
                Column(
                    Modifier.fillMaxSize().padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    VShimmerListPremium(itemCount = 5)
                }
            },
        ) {
            Column(
                Modifier.verticalScroll(rememberScrollState()).padding(bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                VGradientHeroPremium(
                    title = "Analytics Overview",
                    subtitle = "Growth: ${state.currentGrowth}",
                    stats = listOf(
                        HeroStatPremium(state.currentGrowth, "Growth"),
                        HeroStatPremium("${state.cards.size}", "Metrics"),
                        HeroStatPremium("${state.insights.size}", "Insights"),
                    ),
                    onClick = {},
                    modifier = Modifier.padding(horizontal = 20.dp),
                )

                Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Metrics", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                    state.cards.forEach { card ->
                        VStatCardPremium(
                            value = card.value,
                            label = card.title,
                            onClick = {},
                            icon = VIcons.TrendingUp,
                            trend = card.trend,
                        )
                    }

                    Text("Insights", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                    state.insights.forEach { insight ->
                        VListTilePremium(
                            title = insight.title,
                            subtitle = insight.description,
                            onClick = {},
                            leadingIcon = VIcons.Sparkles,
                        )
                    }
                }
            }
        }
    }
}
