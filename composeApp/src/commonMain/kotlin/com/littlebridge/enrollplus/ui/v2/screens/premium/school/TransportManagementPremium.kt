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
import com.littlebridge.enrollplus.feature.transport.presentation.TransportViewModel
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
fun TransportManagementPremium(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: TransportViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    LaunchedEffect(Unit) {
        viewModel.loadRoutes()
        viewModel.loadVehicles()
        viewModel.loadAssignments()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .navigationBarsPadding(),
    ) {
        VBackHeader(title = "Transport Management", onBack = onBack)

        VStateHostPremium(
            loading = state.isLoading,
            error = state.error,
            isEmpty = state.routes.isEmpty() && state.vehicles.isEmpty() && state.assignments.isEmpty() && !state.isLoading,
            emptyTitle = "No transport data",
            onRetry = { viewModel.loadRoutes() },
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
                    title = "Transport Management",
                    subtitle = "${state.routes.size} routes | ${state.vehicles.size} vehicles",
                    stats = listOf(
                        HeroStatPremium("${state.routes.size}", "Routes"),
                        HeroStatPremium("${state.vehicles.size}", "Vehicles"),
                        HeroStatPremium("${state.assignments.size}", "Assignments"),
                    ),
                    onClick = {},
                    modifier = Modifier.padding(horizontal = 20.dp),
                )

                Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Routes", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                    state.routes.forEach { r ->
                        VListTilePremium(
                            title = r.name,
                            subtitle = "${r.stops.size} stops | ${r.description ?: "No description"}",
                            onClick = {},
                            leadingIcon = VIcons.MapPin,
                            trailingText = if (r.isActive) "Active" else "Inactive",
                        )
                    }

                    Text("Vehicles", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                    state.vehicles.forEach { v ->
                        VListTilePremium(
                            title = v.busNumber,
                            subtitle = "Capacity: ${v.capacity} | Driver: ${v.driverName ?: "Unassigned"}",
                            onClick = {},
                            leadingIcon = VIcons.MapPin,
                            trailingText = if (v.isActive) "Active" else "Inactive",
                        )
                    }

                    Text("Assignments", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                    state.assignments.forEach { a ->
                        VListTilePremium(
                            title = a.studentName ?: "Student",
                            subtitle = "Route: ${a.routeName ?: "—"} | Stop: ${a.stopName ?: "—"}",
                            onClick = {},
                            leadingIcon = VIcons.Users,
                        )
                    }
                }
            }
        }
    }
}
