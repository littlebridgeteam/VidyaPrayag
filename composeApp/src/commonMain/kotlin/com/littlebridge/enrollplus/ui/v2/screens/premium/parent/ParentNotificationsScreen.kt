package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.core.notification.presentation.NotificationsViewModel
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ParentNotificationsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NotificationsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    ParentOverlayScaffold(title = "Notifications", onBack = onBack, modifier = modifier) {
        if (state.isLoading) {
            StatusBox("Loading notifications...")
        } else if (state.notifications.isEmpty()) {
            StatusBox("No notifications")
        } else {
            state.notifications.forEach { notif ->
                val category = notif.category.lowercase()
                val (icon, iconColor, bgColor) = when {
                    category.contains("fee") || category.contains("payment") -> Triple(Icons.Filled.CreditCard, VColors.Primary, VColors.PrimaryContainer)
                    category.contains("message") || category.contains("chat") -> Triple(Icons.AutoMirrored.Filled.Message, VColors.Tertiary, VColors.TertiaryContainer)
                    category.contains("transport") || category.contains("bus") -> Triple(Icons.Filled.DirectionsBus, VColors.WarmOrange, VColors.WarmOrangeContainer)
                    else -> Triple(Icons.AutoMirrored.Filled.Message, VColors.Primary, VColors.PrimaryContainer)
                }
                NotificationCard(
                    source = notif.category.replaceFirstChar { it.uppercase() },
                    time = notif.time,
                    title = notif.title,
                    body = notif.body,
                    icon = icon,
                    iconColor = iconColor,
                    bgColor = bgColor,
                    unread = notif.unread,
                )
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun StatusBox(msg: String) {
    Box(Modifier.fillMaxWidth().height(120.dp).clip(VShapes.Lg).background(VColors.SurfaceContainerLow), contentAlignment = Alignment.Center) {
        Text(msg, style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
    }
}

@Composable
private fun NotificationCard(
    source: String,
    time: String,
    title: String,
    body: String,
    icon: ImageVector,
    iconColor: Color,
    bgColor: Color,
    unread: Boolean,
) {
    Row(
        Modifier.fillMaxWidth().clip(VShapes.Lg).background(VColors.SurfaceContainerLowest).padding(16.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(Modifier.size(40.dp).clip(CircleShape).background(bgColor), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
        }
        Column(Modifier.weight(1f)) {
            Text("$source · $time", style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant, fontWeight = FontWeight.SemiBold))
            Spacer(Modifier.height(4.dp))
            Text(title, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = if (unread) FontWeight.Bold else FontWeight.SemiBold))
            Spacer(Modifier.height(4.dp))
            Text(body, style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant), maxLines = 2)
        }
    }
}
