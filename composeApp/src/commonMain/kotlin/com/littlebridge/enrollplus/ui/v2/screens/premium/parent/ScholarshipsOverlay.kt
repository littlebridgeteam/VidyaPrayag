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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.parent.presentation.Scholarship
import com.littlebridge.enrollplus.feature.parent.presentation.ScholarshipApplication
import com.littlebridge.enrollplus.feature.parent.presentation.ScholarshipsViewModel
import com.littlebridge.enrollplus.ui.v2.components.misc.VShimmerBoxPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VStateHostPremium
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ScholarshipsOverlay(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ScholarshipsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()

    ParentOverlayScaffold(
        title = "Scholarships",
        onBack = onBack,
        modifier = modifier,
    ) {
        VStateHostPremium(
            loading = state.isLoading,
            error = state.error,
            isEmpty = state.scholarships.isEmpty() && state.applications.isEmpty() && !state.isLoading,
            modifier = Modifier.fillMaxSize(),
            emptyTitle = "No scholarships available",
            emptyIcon = Icons.Filled.School,
            onRetry = { /* VM auto-loads in init */ },
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
            // Available scholarships
            if (state.scholarships.isNotEmpty()) {
                Text(
                    text = "Available Scholarships",
                    style = VTypography.SectionHeader.copy(color = VColors.OnSurface),
                )
                Spacer(Modifier.height(12.dp))
                state.scholarships.forEach { scholarship ->
                    ScholarshipCard(scholarship = scholarship)
                    Spacer(Modifier.height(8.dp))
                }
            }

            // My applications
            if (state.applications.isNotEmpty()) {
                Spacer(Modifier.height(20.dp))
                Text(
                    text = "My Applications",
                    style = VTypography.SectionHeader.copy(color = VColors.OnSurface),
                )
                Spacer(Modifier.height(12.dp))
                state.applications.forEach { application ->
                    ApplicationCard(application = application)
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun ScholarshipCard(scholarship: Scholarship) {
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(VColors.PrimaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = VColors.Primary, modifier = Modifier.size(18.dp))
                }
                Text(
                    text = scholarship.amount,
                    style = VTypography.QuickStatValue.copy(color = VColors.Primary),
                )
            }
            if (scholarship.isCritical) {
                Box(
                    modifier = Modifier
                        .clip(VShapes.Full)
                        .background(VColors.Error.copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = "URGENT",
                        style = VTypography.ThreadTime.copy(color = VColors.Error),
                    )
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = scholarship.title,
            style = VTypography.UpdateTitle.copy(color = VColors.OnSurface),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = scholarship.description,
            style = VTypography.BodyMedium.copy(color = VColors.OnSurfaceVariant),
        )
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = scholarship.category,
                style = VTypography.ThreadTime.copy(color = VColors.OnSurfaceVariant),
            )
            Text(
                text = scholarship.timeLeft,
                style = VTypography.ThreadTime.copy(color = VColors.WarmOrange),
            )
        }
    }
}

@Composable
private fun ApplicationCard(application: ScholarshipApplication) {
    val statusColor = when (application.status.lowercase()) {
        "shortlisted" -> VColors.Primary
        "under review" -> VColors.WarmOrange
        "received" -> VColors.OnSurfaceVariant
        else -> VColors.Outline
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.Lg)
            .background(VColors.SurfaceContainerLow)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(statusColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = statusColor, modifier = Modifier.size(18.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = application.institution,
                style = VTypography.BodyLarge.copy(color = VColors.OnSurface),
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = application.program,
                style = VTypography.ThreadPreview.copy(color = VColors.OnSurfaceVariant),
            )
        }
        Box(
            modifier = Modifier
                .clip(VShapes.Full)
                .background(statusColor.copy(alpha = 0.12f))
                .padding(horizontal = 10.dp, vertical = 4.dp),
        ) {
            Text(
                text = application.status,
                style = VTypography.ThreadTime.copy(color = statusColor),
            )
        }
    }
}
