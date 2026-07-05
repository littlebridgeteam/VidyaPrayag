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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.admin.presentation.PaceAlertsViewModel
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
fun PaceAlertsPremium(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PaceAlertsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .navigationBarsPadding(),
    ) {
        VBackHeader(title = "Pace Alerts", onBack = onBack)

        VStateHostPremium(
            loading = state.isLoading,
            error = state.errorMessage,
            isEmpty = state.alerts.isEmpty() && state.snapshots.isEmpty() && !state.isLoading,
            emptyTitle = "No pace alerts",
            emptyBody = "All classes are on track.",
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
            Column(
                Modifier.verticalScroll(rememberScrollState()).padding(bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                VGradientHeroPremium(
                    title = "Pace Alerts",
                    subtitle = "${state.alerts.size} active alerts",
                    stats = listOf(
                        HeroStatPremium("${state.alerts.size}", "Alerts"),
                        HeroStatPremium("${state.snapshots.size}", "Snapshots"),
                        HeroStatPremium(if (state.isRecalculating) "..." else "Ready", "Status"),
                    ),
                    onClick = {},
                    modifier = Modifier.padding(horizontal = 20.dp),
                )

                Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (state.alerts.isNotEmpty()) {
                        Text("Active Alerts", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                        state.alerts.forEach { alert ->
                            VListTilePremium(
                                title = "${alert.className} - ${alert.section} | ${alert.subject}",
                                subtitle = alert.message,
                                onClick = { viewModel.resolveAlert(alert.id) },
                                leadingIcon = VIcons.AlertTriangle,
                                trailingText = alert.level,
                            )
                        }
                    }

                    if (state.snapshots.isNotEmpty()) {
                        Text("Pace Snapshots", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                        state.snapshots.forEach { snap ->
                            VStatCardPremium(
                                value = "${snap.actualPct}%",
                                label = "${snap.className} - ${snap.subject}",
                                onClick = {},
                                icon = VIcons.TrendingUp,
                                trend = snap.status,
                            )
                        }
                    }
                }
            }
        }
    }
}
