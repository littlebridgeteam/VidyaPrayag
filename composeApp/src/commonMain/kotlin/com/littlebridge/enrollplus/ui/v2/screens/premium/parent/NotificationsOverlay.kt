package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.core.notification.presentation.NotificationItem
import com.littlebridge.enrollplus.core.notification.presentation.NotificationsViewModel
import com.littlebridge.enrollplus.ui.v2.components.misc.VShimmerBoxPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VStateHostPremium
import com.littlebridge.enrollplus.ui.v2.components.navigation.VFilterChip
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NotificationsOverlay(
    onBack: () -> Unit,
    onDeepLink: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NotificationsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    var filter by rememberSaveable { mutableIntStateOf(0) }

    ParentOverlayScaffold(
        title = "Notifications",
        onBack = onBack,
        modifier = modifier,
    ) {
        // Filter chips: All | Unread
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            VFilterChip(
                label = "All",
                active = filter == 0,
                onClick = { filter = 0 },
            )
            VFilterChip(
                label = "Unread",
                active = filter == 1,
                onClick = { filter = 1 },
            )
        }

        Spacer(Modifier.height(8.dp))

        val filtered = if (filter == 1) state.notifications.filter { it.unread } else state.notifications

        VStateHostPremium(
            loading = state.isLoading,
            error = state.error,
            isEmpty = filtered.isEmpty() && !state.isLoading,
            modifier = Modifier.fillMaxSize(),
            emptyTitle = if (filter == 1) "No unread notifications" else "No notifications",
            emptyIcon = Icons.Filled.Notifications,
            onRetry = { viewModel.load() },
            skeleton = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    repeat(6) { VShimmerBoxPremium(height = 72.dp, shape = VShapes.Lg) }
                }
            },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp),
            ) {
                // Mark all read button
                if (state.unreadCount > 0) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(VShapes.Lg)
                            .background(VColors.PrimaryContainer)
                            .clickable { viewModel.markAllRead() }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            Icons.Filled.Notifications,
                            contentDescription = null,
                            tint = VColors.OnPrimaryContainer,
                            modifier = Modifier.size(18.dp),
                        )
                        Text(
                            text = "Mark all ${state.unreadCount} as read",
                            style = VTypography.UpdateAction.copy(color = VColors.OnPrimaryContainer),
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                }

                filtered.forEach { notification ->
                    NotificationCard(
                        notification = notification,
                        onTap = {
                            viewModel.markRead(notification.id)
                            notification.deepLink?.let { onDeepLink(it) }
                        },
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun NotificationCard(
    notification: NotificationItem,
    onTap: () -> Unit,
) {
    val categoryColor = when (notification.category.lowercase()) {
        "fees" -> VColors.Primary
        "academic" -> VColors.Tertiary
        "attendance" -> VColors.WarmOrange
        "announcement" -> VColors.Secondary
        else -> VColors.Outline
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.Lg)
            .background(
                if (notification.unread) VColors.SurfaceContainerLowest
                else VColors.SurfaceContainerLow,
            )
            .clickable(onClick = onTap)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        // Category dot
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(categoryColor)
                .padding(top = 6.dp),
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = notification.title,
                style = VTypography.UpdateTitle.copy(color = VColors.OnSurface),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = notification.body,
                style = VTypography.BodyMedium.copy(color = VColors.OnSurfaceVariant),
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = notification.time,
                style = VTypography.ThreadTime.copy(color = VColors.Outline),
            )
        }

        if (notification.unread) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(VColors.Primary),
            )
        }
    }
}
