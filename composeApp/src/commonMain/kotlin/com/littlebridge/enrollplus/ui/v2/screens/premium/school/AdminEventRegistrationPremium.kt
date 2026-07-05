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
import com.littlebridge.enrollplus.feature.event.presentation.AdminEventRegistrationViewModel
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
fun AdminEventRegistrationPremium(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: AdminEventRegistrationViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    LaunchedEffect(Unit) { viewModel.loadEvents() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .navigationBarsPadding(),
    ) {
        VBackHeader(title = "Event Management", onBack = onBack)

        VStateHostPremium(
            loading = state.isLoading,
            error = state.errorMessage,
            isEmpty = state.events.isEmpty() && !state.isLoading,
            emptyTitle = "No events",
            onRetry = { viewModel.loadEvents() },
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
                    title = "Event Management",
                    subtitle = "${state.events.size} events | ${state.registrations.size} registrations",
                    stats = listOf(
                        HeroStatPremium("${state.events.size}", "Events"),
                        HeroStatPremium("${state.registrations.size}", "Registrations"),
                        HeroStatPremium("${state.events.count { it.registrationEnabled } }", "Open"),
                    ),
                    onClick = {},
                    modifier = Modifier.padding(horizontal = 20.dp),
                )

                Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Events", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                    state.events.forEach { e ->
                        VListTilePremium(
                            title = e.title,
                            subtitle = "${e.startDate} | ${e.type} | ${e.status}",
                            onClick = {},
                            leadingIcon = VIcons.Calendar,
                            trailingText = if (e.registrationEnabled) "Registration Open" else "No Registration",
                        )
                    }

                    if (state.registrations.isNotEmpty()) {
                        Text("Registrations", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                        state.registrations.forEach { r ->
                            VListTilePremium(
                                title = r.parentName,
                                subtitle = "${r.eventTitle} | ${r.eventDate} | ${r.slotTime ?: "—"}",
                                onClick = {},
                                leadingIcon = VIcons.Users,
                                trailingText = r.status,
                            )
                        }
                    }
                }
            }
        }
    }
}
