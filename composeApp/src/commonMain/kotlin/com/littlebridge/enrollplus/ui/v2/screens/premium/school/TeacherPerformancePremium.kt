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
import com.littlebridge.enrollplus.feature.admin.presentation.TeacherPerformanceViewModel
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
fun TeacherPerformancePremium(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: TeacherPerformanceViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    LaunchedEffect(Unit) { viewModel.load() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .navigationBarsPadding(),
    ) {
        VBackHeader(title = "Teacher Performance", onBack = onBack)

        VStateHostPremium(
            loading = state.isLoading,
            error = state.errorMessage,
            isEmpty = state.starFaculty.isEmpty() && !state.isLoading,
            emptyTitle = "No performance data",
            onRetry = { viewModel.load() },
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
                    title = "Faculty Performance",
                    subtitle = "Compliance: ${state.aggregateCompliance}",
                    stats = listOf(
                        HeroStatPremium(state.aggregateCompliance, "Compliance"),
                        HeroStatPremium(state.complianceTrend, "Trend"),
                        HeroStatPremium("${state.starFaculty.size}", "Stars"),
                    ),
                    onClick = {},
                    modifier = Modifier.padding(horizontal = 20.dp),
                )

                Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Star Faculty", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                    state.starFaculty.forEach { t ->
                        VListTilePremium(
                            title = t.name,
                            subtitle = "${t.department} - Score: ${t.score}",
                            onClick = {},
                            leadingIcon = VIcons.Star,
                            trailingText = "#${t.rank}",
                        )
                    }

                    Text("Accountability Matrix", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                    state.accountabilityMatrix.forEach { f ->
                        VListTilePremium(
                            title = f.name,
                            subtitle = "${f.department} | Compliance: ${f.complianceScore}% | Delay: ${f.avgUpdateDelay}",
                            onClick = {},
                            leadingIcon = VIcons.Users,
                            trailingText = f.riskCorrelation,
                        )
                    }

                    Text("Department Efficiency", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                    state.deptEfficiencies.forEach { d ->
                        VStatCardPremium(value = "${d.percentage}%", label = d.name, onClick = {}, icon = VIcons.TrendingUp)
                    }
                }
            }
        }
    }
}
