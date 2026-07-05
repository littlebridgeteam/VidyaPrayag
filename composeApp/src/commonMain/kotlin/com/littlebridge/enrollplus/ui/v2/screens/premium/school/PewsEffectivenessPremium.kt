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
import com.littlebridge.enrollplus.feature.pews.presentation.PewsEffectivenessViewModel
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
fun PewsEffectivenessPremium(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: PewsEffectivenessViewModel = koinViewModel(),
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
        VBackHeader(title = "PEWS Effectiveness", onBack = onBack)

        VStateHostPremium(
            loading = state.isLoading,
            error = state.error,
            isEmpty = state.trend == null && !state.isLoading,
            emptyTitle = "No effectiveness data",
            onRetry = { viewModel.load() },
            skeleton = {
                Column(
                    Modifier.fillMaxSize().padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    VShimmerListPremium(itemCount = 4)
                }
            },
        ) {
            val trend = state.trend ?: return@VStateHostPremium
            val eff = trend.effectiveness

            Column(
                Modifier.verticalScroll(rememberScrollState()).padding(bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                VGradientHeroPremium(
                    title = "Intervention Effectiveness",
                    subtitle = "${eff.done}/${eff.total} resolved",
                    stats = listOf(
                        HeroStatPremium("${eff.total}", "Total"),
                        HeroStatPremium("${eff.open}", "Open"),
                        HeroStatPremium("${eff.done}", "Done"),
                    ),
                    onClick = {},
                    modifier = Modifier.padding(horizontal = 20.dp),
                )

                Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Outcomes", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                    VStatCardPremium(value = "${eff.improved}", label = "Improved", onClick = {}, icon = VIcons.TrendingUp)
                    VStatCardPremium(value = "${eff.unchanged}", label = "Unchanged", onClick = {}, icon = VIcons.Check)
                    VStatCardPremium(value = "${eff.worsened}", label = "Worsened", onClick = {}, icon = VIcons.AlertTriangle)
                    VStatCardPremium(value = "${eff.dismissed}", label = "Dismissed", onClick = {}, icon = VIcons.Close)

                    Text("Trend Timeline", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                    trend.points.forEach { p ->
                        VListTilePremium(
                            title = p.runDate,
                            subtitle = "High: ${p.high} | Medium: ${p.medium} | Watch: ${p.watch} | Total: ${p.total}",
                            onClick = {},
                            leadingIcon = VIcons.TrendingUp,
                        )
                    }
                }
            }
        }
    }
}
