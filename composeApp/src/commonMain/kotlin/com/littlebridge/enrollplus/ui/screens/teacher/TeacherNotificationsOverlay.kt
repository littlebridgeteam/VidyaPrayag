package com.littlebridge.enrollplus.ui.screens.teacher

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.domain.util.UiState
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentNotificationDto
import com.littlebridge.enrollplus.presentation.TeacherViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors

@Composable
fun TeacherNotificationsOverlay(
    viewModel: TeacherViewModel,
    visible: Boolean,
    onDismiss: () -> Unit,
    onDeepLink: (String) -> Unit,
) {
    LaunchedEffect(visible) {
        if (visible) viewModel.loadNotifications()
    }

    val notificationsState by viewModel.notificationsState.collectAsState()

    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
        exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(VColors.cream),
        ) {
            // Overlay header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(VColors.white)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(VColors.surfaceTint, CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { onDismiss() },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = TIChevronRight,
                        contentDescription = "Back",
                        tint = VColors.ink,
                        modifier = Modifier
                            .size(17.dp)
                            .graphicsLayer(rotationZ = 180f),
                    )
                }
                Text(
                    text = "Notifications",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp,
                    color = VColors.ink,
                )
            }

            // Content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
            ) {
                when (val state = notificationsState) {
                    is UiState.Loading -> {
                        Text(
                            text = "Loading…",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = VColors.ink3,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                        )
                    }
                    is UiState.Error -> {
                        Text(
                            text = state.message,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = VColors.ink3,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                        )
                    }
                    is UiState.Success -> {
                        val notifications = state.data.data.notifications
                        if (notifications.isEmpty()) {
                            Text(
                                text = "No notifications",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = VColors.ink3,
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                            )
                        } else {
                            notifications.forEach { notif ->
                                NotificationItem(
                                    notif = notif,
                                    onClick = {
                                        viewModel.markNotificationRead(notif.id)
                                        notif.deepLink?.let { onDeepLink(it) }
                                        onDismiss()
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationItem(
    notif: ParentNotificationDto,
    onClick: () -> Unit,
) {
    val icon = iconForCategory(notif.category)
    val isUnread = notif.unread

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isUnread) VColors.violetSoft.copy(alpha = 0.3f) else VColors.cream)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // Icon circle
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(VColors.surfaceTint, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = VColors.ink2,
                modifier = Modifier.size(18.dp),
            )
        }

        // Content
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = notif.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = VColors.ink,
            )
            Text(
                text = notif.body,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = VColors.ink2,
                modifier = Modifier.padding(top = 2.dp),
            )
            Text(
                text = notif.time,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = VColors.ink3,
                modifier = Modifier.padding(top = 4.dp),
            )
        }

        // Unread dot
        if (isUnread) {
            Box(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .size(8.dp)
                    .background(VColors.violet, CircleShape),
            )
        }
    }
}

private fun iconForCategory(category: String): ImageVector = when (category.lowercase()) {
    "attendance" -> TICheck
    "academic", "marks" -> TIBook
    "leave" -> TIClock
    "announcement" -> TIBell
    "message", "messages" -> TIEdit
    "fees" -> TIAward
    "health", "pews" -> TIAlert
    "homework" -> TIBook
    else -> TIBell
}
