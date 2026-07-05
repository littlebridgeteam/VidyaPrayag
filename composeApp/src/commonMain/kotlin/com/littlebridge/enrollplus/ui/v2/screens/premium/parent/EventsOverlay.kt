package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.event.domain.model.ParentEventDto
import com.littlebridge.enrollplus.feature.event.presentation.ParentEventRegistrationViewModel
import com.littlebridge.enrollplus.ui.v2.components.buttons.VPrimaryButton
import com.littlebridge.enrollplus.ui.v2.components.misc.VShimmerBoxPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VStateHostPremium
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EventsOverlay(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ParentEventRegistrationViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()

    ParentOverlayScaffold(
        title = "Events",
        onBack = onBack,
        modifier = modifier,
    ) {
        VStateHostPremium(
            loading = state.isLoading,
            error = state.errorMessage,
            isEmpty = state.events.isEmpty() && !state.isLoading,
            modifier = Modifier.fillMaxSize(),
            emptyTitle = "No upcoming events",
            emptyIcon = Icons.Filled.CalendarToday,
            onRetry = { viewModel.loadEvents() },
            skeleton = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    repeat(3) { VShimmerBoxPremium(height = 120.dp, shape = VShapes.Lg) }
                }
            },
        ) {
            state.events.forEach { event ->
                EventCard(
                    event = event,
                    onRegister = { viewModel.register(event.id, null, null, 1) },
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun EventCard(event: ParentEventDto, onRegister: () -> Unit) {
    val isRegistered = event.myRegistrationStatus != null

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.Lg)
            .background(VColors.SurfaceContainerLow)
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = event.title,
                style = VTypography.UpdateTitle.copy(color = VColors.OnSurface),
                modifier = Modifier.weight(1f),
            )
            if (isRegistered) {
                val statusColor = when (event.myRegistrationStatus?.lowercase()) {
                    "registered" -> VColors.Primary
                    "cancelled" -> VColors.Error
                    else -> VColors.OnSurfaceVariant
                }
                Box(
                    modifier = Modifier
                        .clip(VShapes.Full)
                        .background(statusColor.copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = event.myRegistrationStatus ?: "",
                        style = VTypography.ThreadTime.copy(color = statusColor),
                    )
                }
            }
        }

        if (event.description.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = event.description,
                style = VTypography.BodyMedium.copy(color = VColors.OnSurfaceVariant),
            )
        }

        Spacer(Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(Icons.Filled.CalendarToday, contentDescription = null, tint = VColors.Outline, modifier = Modifier.size(14.dp))
            Text(
                text = if (event.allDay) event.startDate else "${event.startDate} - ${event.endDate}",
                style = VTypography.ThreadPreview.copy(color = VColors.OnSurfaceVariant),
            )
        }

        if (!event.venue.isNullOrBlank()) {
            val venue = event.venue!!
            Spacer(Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(Icons.Filled.LocationOn, contentDescription = null, tint = VColors.Outline, modifier = Modifier.size(14.dp))
                Text(
                    text = venue,
                    style = VTypography.ThreadPreview.copy(color = VColors.OnSurfaceVariant),
                )
            }
        }

        if (event.registrationEnabled && !isRegistered) {
            Spacer(Modifier.height(12.dp))
            VPrimaryButton(
                text = "Register",
                onClick = onRegister,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (event.maxAttendees != null) {
            Spacer(Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(Icons.Filled.Person, contentDescription = null, tint = VColors.Outline, modifier = Modifier.size(14.dp))
                Text(
                    text = "Max ${event.maxAttendees} attendees",
                    style = VTypography.ThreadTime.copy(color = VColors.Outline),
                )
            }
        }
    }
}
