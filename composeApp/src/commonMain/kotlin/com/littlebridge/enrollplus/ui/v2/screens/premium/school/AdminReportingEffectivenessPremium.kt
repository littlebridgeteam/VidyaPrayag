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
import com.littlebridge.enrollplus.feature.reportcard.presentation.AdminReportEffectivenessViewModel
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
fun AdminReportingEffectivenessPremium(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AdminReportEffectivenessViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    LaunchedEffect(Unit) { viewModel.loadEffectiveness() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .navigationBarsPadding(),
    ) {
        VBackHeader(title = "Reporting Effectiveness", onBack = onBack)

        VStateHostPremium(
            loading = state.isLoading,
            error = state.error,
            isEmpty = state.effectiveness.isEmpty() && !state.isLoading,
            emptyTitle = "No effectiveness data",
            onRetry = { viewModel.loadEffectiveness() },
            skeleton = {
                Column(
                    Modifier.fillMaxSize().padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    VShimmerListPremium(itemCount = 4)
                }
            },
        ) {
            Column(
                Modifier.verticalScroll(rememberScrollState()).padding(bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                val projection = state.projectionAccuracy
                VGradientHeroPremium(
                    title = "Effectiveness & Learning",
                    subtitle = if (projection != null) "Accuracy: ${"%.1f".format(projection.accuracyRate * 100)}%" else "Loading...",
                    stats = listOf(
                        HeroStatPremium(if (projection != null) "${projection.totalProjections}" else "—", "Projections"),
                        HeroStatPremium(if (projection != null) "${projection.accurateWithin5Pct}" else "—", "Accurate"),
                        HeroStatPremium(if (projection != null) projection.confidence else "—", "Confidence"),
                    ),
                    onClick = {},
                    modifier = Modifier.padding(horizontal = 20.dp),
                )

                Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Effectiveness Reports", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                    state.effectiveness.forEach { e ->
                        VStatCardPremium(
                            value = "${e.studentsImproved}/${e.studentsTargeted}",
                            label = e.focusArea,
                            onClick = {},
                            icon = VIcons.TrendingUp,
                        )
                    }

                    val patterns = state.patterns
                    if (patterns != null) {
                        Text("Cohort Patterns", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                        VStatCardPremium(value = "${patterns.totalStudents}", label = "Total Students", onClick = {}, icon = VIcons.Users)
                        patterns.topFocusAreas.forEach { fa ->
                            VListTilePremium(
                                title = fa.focusArea,
                                subtitle = "${fa.count} students | Avg: ${"%.1f".format(fa.avgPct)}%",
                                onClick = {},
                                leadingIcon = VIcons.Sparkles,
                            )
                        }
                    }
                }
            }
        }
    }
}
