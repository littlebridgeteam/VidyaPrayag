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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.schools.presentation.SchoolDiscoveryViewModel
import com.littlebridge.enrollplus.ui.v2.components.buttons.VPrimaryButton
import com.littlebridge.enrollplus.ui.v2.components.misc.VStateHostPremium
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SchoolDetailOverlay(
    onBack: () -> Unit,
    onLinkChild: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: SchoolDiscoveryViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()

    ParentOverlayScaffold(
        title = "School Detail",
        onBack = onBack,
        modifier = modifier,
    ) {
        VStateHostPremium(
            loading = state.isLoading,
            error = state.errorMessage,
            isEmpty = state.schools.isEmpty() && !state.isLoading,
            modifier = Modifier.fillMaxSize(),
            emptyTitle = "School not found",
            emptyIcon = Icons.Filled.School,
            onRetry = { viewModel.load() },
        ) {
            // Show first school as placeholder — in real usage, a schoolId would be passed
            val school = state.schools.firstOrNull() ?: return@VStateHostPremium

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                // Header
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(VShapes.Xl)
                        .background(VColors.SurfaceContainerLowest)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(VColors.PrimaryContainer),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = school.name.take(1).uppercase(),
                            style = VTypography.SectionHeader.copy(color = VColors.OnPrimaryContainer),
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = school.name,
                        style = VTypography.SectionHeader.copy(color = VColors.OnSurface),
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(Icons.Filled.LocationOn, contentDescription = null, tint = VColors.OnSurfaceVariant, modifier = Modifier.size(14.dp))
                        Text(
                            text = school.location,
                            style = VTypography.ThreadPreview.copy(color = VColors.OnSurfaceVariant),
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Stats row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    SchoolStat(
                        icon = Icons.Filled.Star,
                        label = "Rating",
                        value = if (school.rating > 0) String.format("%.1f", school.rating) else "—",
                        modifier = Modifier.weight(1f),
                    )
                    SchoolStat(
                        icon = Icons.Filled.School,
                        label = "Board",
                        value = school.board ?: "—",
                        modifier = Modifier.weight(1f),
                    )
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    SchoolStat(
                        icon = Icons.Filled.School,
                        label = "Medium",
                        value = school.medium ?: "—",
                        modifier = Modifier.weight(1f),
                    )
                    SchoolStat(
                        icon = Icons.Filled.School,
                        label = "Type",
                        value = school.schoolGender ?: "—",
                        modifier = Modifier.weight(1f),
                    )
                }

                if (!school.address.isNullOrBlank()) {
                    val address = school.address!!
                    Spacer(Modifier.height(20.dp))
                    Text(
                        text = "Address",
                        style = VTypography.SectionHeader.copy(color = VColors.OnSurface),
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = address,
                        style = VTypography.BodyMedium.copy(color = VColors.OnSurfaceVariant),
                    )
                }

                Spacer(Modifier.height(24.dp))

                // Link child button
                VPrimaryButton(
                    text = "Link Child to This School",
                    onClick = onLinkChild,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun SchoolStat(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(VShapes.Lg)
            .background(VColors.SurfaceContainerLow)
            .padding(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(icon, contentDescription = null, tint = VColors.OnSurfaceVariant, modifier = Modifier.size(16.dp))
            Text(
                text = label,
                style = VTypography.QuickStatLabel.copy(color = VColors.OnSurfaceVariant),
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = value,
            style = VTypography.QuickStatValue.copy(color = VColors.OnSurface),
        )
    }
}
