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
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.parent.presentation.FeeAnnouncement
import com.littlebridge.enrollplus.feature.parent.presentation.FeeState
import com.littlebridge.enrollplus.feature.parent.presentation.FeeViewModel
import com.littlebridge.enrollplus.ui.v2.components.cards.VFeesHeroCard
import com.littlebridge.enrollplus.ui.v2.components.misc.VPullRefreshPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VShimmerBoxPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VStateHostPremium
import com.littlebridge.enrollplus.ui.v2.components.typography.VSectionHeader
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ParentFeesScreen(
    onOpenOverlay: (ParentOverlay) -> Unit,
    onSwitchTab: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: FeeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()

    VPullRefreshPremium(
        isRefreshing = state.isLoading,
        onRefresh = { viewModel.reload() },
        modifier = modifier.fillMaxSize(),
    ) {
        VStateHostPremium(
            loading = state.isLoading,
            error = state.error,
            isEmpty = false,
            modifier = Modifier.fillMaxSize(),
            onRetry = { viewModel.reload() },
            skeleton = { FeesSkeleton() },
        ) {
            FeesContent(state = state)
        }
    }
}

@Composable
private fun FeesContent(state: FeeState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(bottom = 140.dp),
    ) {
        // 1. Balance hero card
        val hasOutstanding = state.outstandingFees.isNotBlank() && state.outstandingFees != "$0"
        VFeesHeroCard(
            label = if (hasOutstanding) "Outstanding Fees" else "Fees Status",
            amount = if (hasOutstanding) state.outstandingFees else "All Clear",
            dueDate = if (state.overdueCount > 0) "${state.overdueCount} overdue" else "No overdue payments",
            onPayClick = { /* TODO: navigate to payment */ },
        )

        Spacer(Modifier.height(24.dp))

        // 2. Collection progress
        VSectionHeader(title = "Collection Progress", modifier = Modifier.padding(horizontal = 4.dp))
        Spacer(Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(VShapes.Xl)
                .background(VColors.SurfaceContainerLowest)
                .padding(20.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Collected",
                    style = VTypography.BodyMedium.copy(color = VColors.OnSurfaceVariant),
                )
                Text(
                    text = state.totalCollected,
                    style = VTypography.QuickStatValue.copy(color = VColors.OnSurface),
                )
            }
            Spacer(Modifier.height(12.dp))
            val pct = state.collectionProgress
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(VShapes.Full)
                    .background(VColors.SurfaceContainerHigh),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(pct.coerceIn(0f, 1f))
                        .height(8.dp)
                        .clip(VShapes.Full)
                        .background(VColors.Primary),
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = "${(pct * 100).toInt()}% collected",
                style = VTypography.ThreadTime.copy(color = VColors.Outline),
            )
        }

        Spacer(Modifier.height(24.dp))

        // 3. Fee announcements
        if (state.announcements.isNotEmpty()) {
            VSectionHeader(title = "Fee Announcements", modifier = Modifier.padding(horizontal = 4.dp))
            Spacer(Modifier.height(8.dp))
            state.announcements.forEach { announcement ->
                FeeAnnouncementCard(announcement = announcement)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun FeeAnnouncementCard(announcement: FeeAnnouncement) {
    val typeColor = when (announcement.type.lowercase()) {
        "emergency" -> VColors.Error
        "payment" -> VColors.Primary
        else -> VColors.Tertiary
    }
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
                    Modifier.size(32.dp).clip(VShapes.Md).background(VColors.PrimaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Filled.Payments,
                        contentDescription = null,
                        tint = VColors.Primary,
                        modifier = Modifier.size(16.dp),
                    )
                }
                Text(
                    text = announcement.title,
                    style = VTypography.UpdateTitle.copy(color = VColors.OnSurface),
                    modifier = Modifier.weight(1f),
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = announcement.description,
            style = VTypography.BodyMedium.copy(color = VColors.OnSurfaceVariant),
        )
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Box(
                    Modifier.size(6.dp).clip(CircleShape).background(typeColor),
                )
                Text(
                    text = announcement.type,
                    style = VTypography.ThreadTime.copy(color = typeColor),
                )
            }
            Text(
                text = announcement.time,
                style = VTypography.ThreadTime.copy(color = VColors.Outline),
            )
        }
    }
}

@Composable
private fun FeesSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        VShimmerBoxPremium(height = 180.dp, shape = VShapes.TwoXl)
        VShimmerBoxPremium(height = 100.dp, shape = VShapes.Xl)
        repeat(3) { VShimmerBoxPremium(height = 80.dp, shape = VShapes.Lg) }
    }
}
