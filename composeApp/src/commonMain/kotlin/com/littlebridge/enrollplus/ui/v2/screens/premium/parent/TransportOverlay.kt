package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BusAlert
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.core.state.SelectedChildHolder
import com.littlebridge.enrollplus.feature.transport.domain.model.RouteProgress
import com.littlebridge.enrollplus.feature.transport.domain.model.TransportStop
import com.littlebridge.enrollplus.feature.transport.presentation.TransportViewModel
import com.littlebridge.enrollplus.ui.v2.components.misc.VShimmerBoxPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VStateHostPremium
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TransportOverlay(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TransportViewModel = koinViewModel(),
    selectedChildHolder: SelectedChildHolder = koinInject(),
) {
    val state by viewModel.state.collectAsStateV2()
    val childId by selectedChildHolder.selectedChildId.collectAsStateV2()

    LaunchedEffect(childId) {
        childId?.let {
            viewModel.loadChildRoute(it)
            viewModel.startPollingLiveLocation(it)
        }
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.stopPolling() }
    }

    ParentOverlayScaffold(
        title = "Transport",
        onBack = onBack,
        modifier = modifier,
    ) {
        VStateHostPremium(
            loading = state.isLoading && state.childRoute == null,
            error = state.error,
            isEmpty = state.childRoute == null && !state.isLoading && state.error == null,
            modifier = Modifier.fillMaxSize(),
            emptyTitle = "No transport assigned",
            emptyIcon = Icons.Filled.DirectionsBus,
            onRetry = { childId?.let { viewModel.loadChildRoute(it) } },
            skeleton = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    VShimmerBoxPremium(height = 140.dp, shape = VShapes.Xl)
                    VShimmerBoxPremium(height = 80.dp, shape = VShapes.Lg)
                    repeat(3) { VShimmerBoxPremium(height = 56.dp, shape = VShapes.Lg) }
                }
            },
        ) {
            val route = state.childRoute ?: return@VStateHostPremium

            // 1. Route header card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(VShapes.Xl)
                    .background(VColors.SurfaceContainerLowest)
                    .padding(20.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(VColors.PrimaryContainer),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Filled.DirectionsBus, contentDescription = null, tint = VColors.Primary, modifier = Modifier.size(22.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = route.name,
                            style = VTypography.UpdateTitle.copy(color = VColors.OnSurface),
                        )
                        if (!route.description.isNullOrBlank()) {
                            val desc = route.description!!
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = desc,
                                style = VTypography.ThreadPreview.copy(color = VColors.OnSurfaceVariant),
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // 2. Live tracking card
            state.routeProgress?.let { progress ->
                LiveTrackingCard(progress = progress)
                Spacer(Modifier.height(20.dp))
            }

            // 3. Route stops
            if (route.stops.isNotEmpty()) {
                Text(
                    text = "Route Stops",
                    style = VTypography.SectionHeader.copy(color = VColors.OnSurface),
                )
                Spacer(Modifier.height(12.dp))
                route.stops.sortedBy { it.sequence }.forEach { stop ->
                    StopRow(stop = stop, isNext = state.routeProgress?.nextStop?.id == stop.id)
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun LiveTrackingCard(progress: RouteProgress) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.Xl)
            .background(VColors.PrimaryContainer.copy(alpha = 0.3f))
            .padding(20.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(Icons.Filled.MyLocation, contentDescription = null, tint = VColors.Primary, modifier = Modifier.size(20.dp))
            Text(
                text = "Live Tracking",
                style = VTypography.UpdateTitle.copy(color = VColors.OnSurface),
            )
        }
        Spacer(Modifier.height(16.dp))

        InfoRow(
            icon = Icons.Filled.DirectionsBus,
            label = "Bus",
            value = progress.busNumber,
        )
        Spacer(Modifier.height(8.dp))

        progress.nextStop?.let { nextStop ->
            InfoRow(
                icon = Icons.Filled.LocationOn,
                label = "Next Stop",
                value = nextStop.name,
            )
            Spacer(Modifier.height(8.dp))
        }

        progress.etaMinutes?.let { eta ->
            InfoRow(
                icon = Icons.Filled.Schedule,
                label = "ETA",
                value = "$eta min",
            )
        }

        if (progress.etaMinutes == null && progress.nextStop == null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(Icons.Filled.BusAlert, contentDescription = null, tint = VColors.OnSurfaceVariant, modifier = Modifier.size(16.dp))
                Text(
                    text = "Bus not currently tracking",
                    style = VTypography.BodyMedium.copy(color = VColors.OnSurfaceVariant),
                )
            }
        }
    }
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(icon, contentDescription = null, tint = VColors.OnSurfaceVariant, modifier = Modifier.size(16.dp))
        Text(
            text = label,
            style = VTypography.QuickStatLabel.copy(color = VColors.OnSurfaceVariant),
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = value,
            style = VTypography.BodyLarge.copy(color = VColors.OnSurface),
        )
    }
}

@Composable
private fun StopRow(stop: TransportStop, isNext: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.Lg)
            .background(if (isNext) VColors.PrimaryContainer.copy(alpha = 0.4f) else VColors.SurfaceContainerLow)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(if (isNext) VColors.Primary else VColors.SurfaceContainerHigh),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stop.sequence.toString(),
                style = VTypography.ThreadTime.copy(
                    color = if (isNext) VColors.OnPrimary else VColors.OnSurfaceVariant,
                ),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stop.name,
                style = VTypography.BodyLarge.copy(color = VColors.OnSurface),
            )
            if (!stop.estimatedTime.isNullOrBlank()) {
                val estTime = stop.estimatedTime!!
                Spacer(Modifier.height(2.dp))
                Text(
                    text = estTime,
                    style = VTypography.ThreadTime.copy(color = VColors.Outline),
                )
            }
        }
        if (isNext) {
            Box(
                modifier = Modifier
                    .clip(VShapes.Full)
                    .background(VColors.Primary)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Text(
                    text = "NEXT",
                    style = VTypography.ThreadTime.copy(color = VColors.OnPrimary),
                )
            }
        }
    }
}
