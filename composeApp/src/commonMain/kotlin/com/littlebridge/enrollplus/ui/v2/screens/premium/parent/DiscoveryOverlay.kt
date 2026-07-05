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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.schools.presentation.DiscoveredSchool
import com.littlebridge.enrollplus.feature.schools.presentation.SchoolDiscoveryViewModel
import com.littlebridge.enrollplus.ui.v2.components.misc.VShimmerBoxPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VStateHostPremium
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DiscoveryOverlay(
    onBack: () -> Unit,
    onOpenSchool: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SchoolDiscoveryViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()

    ParentOverlayScaffold(
        title = "Discover Schools",
        onBack = onBack,
        modifier = modifier,
    ) {
        // Search bar
        OutlinedTextField(
            value = state.query,
            onValueChange = { viewModel.setQuery(it) },
            placeholder = { Text("Search by name or location...", style = VTypography.BodyMedium.copy(color = VColors.OnSurfaceVariant)) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = VColors.OnSurfaceVariant, modifier = Modifier.size(18.dp)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                focusedBorderColor = VColors.Primary,
                unfocusedBorderColor = VColors.SurfaceContainerHigh,
            ),
            shape = VShapes.Full,
        )

        Spacer(Modifier.height(16.dp))

        // Filter schools by query
        val filtered = if (state.query.isBlank()) {
            state.schools
        } else {
            state.schools.filter {
                it.name.contains(state.query, ignoreCase = true) ||
                it.location.contains(state.query, ignoreCase = true)
            }
        }

        VStateHostPremium(
            loading = state.isLoading,
            error = state.errorMessage,
            isEmpty = filtered.isEmpty() && !state.isLoading,
            modifier = Modifier.fillMaxSize(),
            emptyTitle = if (state.query.isNotBlank()) "No schools match \"${state.query}\"" else "No schools found",
            emptyIcon = Icons.Filled.Search,
            onRetry = { viewModel.load() },
            skeleton = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    repeat(4) { VShimmerBoxPremium(height = 100.dp, shape = VShapes.Lg) }
                }
            },
        ) {
            filtered.forEach { school ->
                SchoolCard(school = school, onClick = { onOpenSchool(school.id) })
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun SchoolCard(school: DiscoveredSchool, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.Lg)
            .background(VColors.SurfaceContainerLow)
            .clickable(onClick = onClick)
            .padding(16.dp),
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
            Text(
                text = school.name.take(1).uppercase(),
                style = VTypography.QuickStatValue.copy(color = VColors.OnPrimaryContainer),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = school.name,
                style = VTypography.BodyLarge.copy(color = VColors.OnSurface),
            )
            Spacer(Modifier.height(2.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(Icons.Filled.LocationOn, contentDescription = null, tint = VColors.Outline, modifier = Modifier.size(12.dp))
                Text(
                    text = school.location,
                    style = VTypography.ThreadPreview.copy(color = VColors.OnSurfaceVariant),
                )
            }
            if (school.board != null) {
                val board = school.board!!
                Spacer(Modifier.height(2.dp))
                Text(
                    text = board,
                    style = VTypography.ThreadTime.copy(color = VColors.Outline),
                )
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Icon(Icons.Filled.Star, contentDescription = null, tint = VColors.WarmOrange, modifier = Modifier.size(14.dp))
                Text(
                    text = if (school.rating > 0) String.format("%.1f", school.rating) else "—",
                    style = VTypography.QuickStatValue.copy(color = VColors.OnSurface),
                )
            }
            if (school.distanceLabel != null) {
                val dist = school.distanceLabel!!
                Spacer(Modifier.height(2.dp))
                Text(
                    text = dist,
                    style = VTypography.ThreadTime.copy(color = VColors.OnSurfaceVariant),
                )
            }
        }
    }
}
