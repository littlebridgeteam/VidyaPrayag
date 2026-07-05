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
import com.littlebridge.enrollplus.feature.admin.presentation.AcademicCalendarPlatformViewModel
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.cards.HeroStatPremium
import com.littlebridge.enrollplus.ui.v2.components.cards.VGradientHeroPremium
import com.littlebridge.enrollplus.ui.v2.components.cards.VListTilePremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VStateHostPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VShimmerListPremium
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AcademicCalendarPlatformPremium(
    onBack: () -> Unit,
    onCreateEvent: () -> Unit = {},
    onOpenEvent: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: AcademicCalendarPlatformViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    LaunchedEffect(Unit) { viewModel.refresh() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .navigationBarsPadding(),
    ) {
        VBackHeader(title = "Academic Calendar", onBack = onBack)

        VStateHostPremium(
            loading = state.isLoading,
            error = state.errorMessage,
            isEmpty = state.events.isEmpty() && state.dashboard == null && !state.isLoading,
            emptyTitle = "No calendar events",
            onRetry = { viewModel.refresh() },
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
                val dashboard = state.dashboard
                VGradientHeroPremium(
                    title = "Academic Calendar",
                    subtitle = "${state.events.size} events",
                    stats = listOf(
                        HeroStatPremium("${dashboard?.upcomingHighlights?.size ?: 0}", "Upcoming"),
                        HeroStatPremium("${dashboard?.draftEvents?.size ?: 0}", "Drafts"),
                        HeroStatPremium("${dashboard?.publishedEvents?.size ?: 0}", "Published"),
                    ),
                    onClick = onCreateEvent,
                    modifier = Modifier.padding(horizontal = 20.dp),
                )

                Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Events", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                    state.events.forEach { e ->
                        VListTilePremium(
                            title = e.title,
                            subtitle = "${e.startDate} | ${e.type} | ${e.status}",
                            onClick = { onOpenEvent(e.id) },
                            leadingIcon = VIcons.Calendar,
                            trailingText = e.status,
                        )
                    }

                    val milestones = dashboard?.milestones.orEmpty()
                    if (milestones.isNotEmpty()) {
                        Text("Milestones", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                        milestones.forEach { m ->
                            VListTilePremium(
                                title = m.title,
                                subtitle = m.description,
                                onClick = { onOpenEvent(m.id) },
                                leadingIcon = VIcons.Star,
                            )
                        }
                    }
                }
            }
        }
    }
}
