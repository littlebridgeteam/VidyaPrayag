package com.littlebridge.enrollplus.ui.v2.screens.premium.school

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.core.notification.presentation.NotificationsViewModel
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.PremiumTheme
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SchoolNotificationsScreen(
    onBack: () -> Unit,
    onDeepLink: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: NotificationsViewModel = koinViewModel(),
) = PremiumTheme(isDark = false) {
    val state by viewModel.state.collectAsStateV2()

    SchoolOverlayScaffold(title = "Notifications", onBack = onBack, modifier = modifier) {
        if (state.isLoading) {
            StatusBox("Loading notifications...")
        } else if (state.error != null) {
            StatusBox(state.error!!, isError = true)
        } else if (state.notifications.isEmpty()) {
            StatusBox("No notifications")
        } else {
            state.notifications.forEach { notif ->
                Row(
                    Modifier.fillMaxWidth().clip(VShapes.Lg).background(VColors.SurfaceContainerLow).padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(Modifier.size(8.dp).clip(CircleShape).background(if (notif.unread) VColors.Primary else VColors.Outline))
                    Column(Modifier.weight(1f)) {
                        Text(notif.title, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold))
                        Text(notif.body, style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant), maxLines = 2)
                    }
                    Text(notif.time, style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant))
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun StatusBox(msg: String, isError: Boolean = false) {
    Box(
        Modifier.fillMaxWidth().height(120.dp).clip(VShapes.Lg).background(VColors.SurfaceContainerLow),
        contentAlignment = Alignment.Center,
    ) {
        Text(msg, style = VTypography.UpdateText.copy(color = if (isError) VColors.Error else VColors.OnSurfaceVariant))
    }
}
