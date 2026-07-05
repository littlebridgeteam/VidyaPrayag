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
import com.littlebridge.enrollplus.feature.admin.presentation.ClassPerformanceViewModel
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
fun ClassPerformancePremium(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ClassPerformanceViewModel = koinViewModel(),
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
        VBackHeader(title = "Class Performance", onBack = onBack)

        VStateHostPremium(
            loading = state.isLoading,
            error = state.errorMessage,
            isEmpty = state.gradeDistribution.isEmpty() && !state.isLoading,
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
                    title = "Class Performance",
                    subtitle = "Avg Proficiency: ${state.avgProficiency}",
                    stats = listOf(
                        HeroStatPremium(state.avgProficiency, "Proficiency"),
                        HeroStatPremium("${state.activeStudents}", "Active"),
                        HeroStatPremium(state.medianGrade, "Median"),
                    ),
                    onClick = {},
                    modifier = Modifier.padding(horizontal = 20.dp),
                )

                Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Grade Distribution", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                    state.gradeDistribution.forEach { g ->
                        VStatCardPremium(value = "${g.percentage}%", label = g.grade, onClick = {}, icon = VIcons.TrendingUp)
                    }

                    Text("Subjects", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                    state.subjectMatrix.forEach { s ->
                        VListTilePremium(
                            title = s.name,
                            subtitle = "${s.percentage}%",
                            onClick = {},
                            leadingIcon = VIcons.Bookmark,
                            trailingText = s.trend,
                        )
                    }

                    Text("Risk Summary", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                    VStatCardPremium(value = "${state.criticalRiskCount}", label = "Critical Risk", onClick = {}, icon = VIcons.AlertTriangle)
                    VStatCardPremium(value = "${state.moderateRiskCount}", label = "Moderate Risk", onClick = {}, icon = VIcons.AlertCircle)

                    if (state.topPerformerName.isNotBlank()) {
                        Text("Top Performer", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                        VListTilePremium(
                            title = state.topPerformerName,
                            subtitle = state.topPerformerDetails,
                            onClick = {},
                            leadingIcon = VIcons.Star,
                        )
                    }

                    Text("Progress Monitoring", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                    state.recentProgress.forEach { p ->
                        VListTilePremium(
                            title = p.name,
                            subtitle = "Math: ${p.math} | Sci: ${p.science} | Lit: ${p.literature}",
                            onClick = {},
                            leadingIcon = VIcons.Users,
                            trailingText = p.status,
                        )
                    }
                }
            }
        }
    }
}
