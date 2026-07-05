package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.feature.parent.presentation.ParentMessageViewModel
import com.littlebridge.enrollplus.ui.v2.components.cards.VUpdateCard
import com.littlebridge.enrollplus.ui.v2.components.cards.UpdateAction
import com.littlebridge.enrollplus.ui.v2.components.navigation.VFilterChip
import com.littlebridge.enrollplus.ui.v2.components.typography.VSectionHeader
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.PremiumTheme
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

/**
 * Premium parent conversations — matches parent-portal.html Messages tab.
 * Top bar with search, segment selector (Messages/Announcements),
 * thread rows with colored avatars, announcements using VUpdateCard.
 */
@Composable
fun ParentConversationsScreen(
    modifier: Modifier = Modifier,
    viewModel: ParentMessageViewModel = koinViewModel(),
    onOpenThread: (String) -> Unit = {},
) = PremiumTheme(isDark = false) {
    val state by viewModel.state.collectAsStateV2()
    var segment by remember { mutableStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VColors.Surface)
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(Modifier.height(16.dp))

        // ── Segment selector — flex:1 equal width, primary active ──
        Row(Modifier.padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            VFilterChip(
                label = "Messages",
                active = segment == 0,
                onClick = { segment = 0 },
                activeBg = VColors.Primary,
                activeFg = VColors.OnPrimary,
                inactiveBg = VColors.SurfaceContainer,
                inactiveFg = VColors.OnSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            VFilterChip(
                label = "Announcements",
                active = segment == 1,
                onClick = { segment = 1 },
                activeBg = VColors.Primary,
                activeFg = VColors.OnPrimary,
                inactiveBg = VColors.SurfaceContainer,
                inactiveFg = VColors.OnSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(Modifier.height(20.dp))

        when (segment) {
            0 -> {
                if (state.loading) {
                    StatusBox("Loading messages...")
                } else if (state.error != null) {
                    StatusBox(state.error!!, isError = true)
                } else if (state.threads.isEmpty()) {
                    StatusBox("No conversations yet")
                } else {
                    Column(Modifier.padding(horizontal = 0.dp)) {
                        state.threads.forEach { thread ->
                            ThreadRow(
                                name = thread.senderName,
                                role = thread.senderRole,
                                preview = thread.lastMessage,
                                time = thread.time,
                                unread = thread.unreadCount,
                                isRead = thread.isRead,
                                onClick = { onOpenThread(thread.id) },
                            )
                        }
                    }
                }
            }
            1 -> {
                VSectionHeader("School Announcements")
                Column(Modifier.padding(horizontal = 20.dp)) {
                    VUpdateCard(
                        source = "Principal",
                        timestamp = "2h ago",
                        title = "Annual Sports Day",
                        text = "Sports Day will be held on January 20th. All parents are invited to attend.",
                        avatarIcon = { Icon(Icons.Filled.Info, contentDescription = null, tint = VColors.Primary, modifier = Modifier.size(20.dp)) },
                        actions = listOf(
                            UpdateAction("Read more", isPrimary = true, onClick = { }),
                        ),
                        onClick = { },
                    )
                    Spacer(Modifier.height(10.dp))
                    VUpdateCard(
                        source = "School Office",
                        timestamp = "1d ago",
                        title = "Parent-Teacher Meeting",
                        text = "PTM scheduled for January 15th, 10:00 AM to 1:00 PM. Please confirm your attendance.",
                        avatarIcon = { Icon(Icons.Filled.Info, contentDescription = null, tint = VColors.Tertiary, modifier = Modifier.size(20.dp)) },
                        actions = listOf(
                            UpdateAction("Confirm", isPrimary = true, onClick = { }),
                        ),
                        onClick = { },
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ThreadRow(
    name: String,
    role: String,
    preview: String,
    time: String,
    unread: Int,
    isRead: Boolean,
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val avatarBg = when (role.lowercase()) {
        "teacher" -> VColors.PrimaryContainer
        "admin", "schooladmin" -> VColors.WarmOrangeContainer
        else -> VColors.TertiaryContainer
    }
    val avatarFg = when (role.lowercase()) {
        "teacher" -> VColors.OnPrimaryContainer
        "admin", "schooladmin" -> VColors.WarmOrange
        else -> VColors.OnTertiaryContainer
    }
    val initials = name.split(" ").take(2).mapNotNull { it.firstOrNull()?.toString() }.joinToString("")

    Row(
        Modifier
            .fillMaxWidth()
            .background(VColors.Surface)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            Modifier.size(48.dp).clip(CircleShape).background(avatarBg),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                initials,
                style = VTypography.ThreadName.copy(color = avatarFg, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold),
            )
        }
        Column(Modifier.weight(1f)) {
            Text(
                name,
                style = VTypography.ThreadName.copy(
                    color = VColors.OnSurface,
                    fontWeight = if (!isRead) FontWeight.ExtraBold else FontWeight.Bold,
                ),
            )
            Spacer(Modifier.height(2.dp))
            Text(
                preview,
                style = VTypography.ThreadPreview.copy(
                    color = VColors.OnSurfaceVariant,
                    fontWeight = if (!isRead) FontWeight.SemiBold else FontWeight.Medium,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(time, style = VTypography.ThreadTime.copy(color = VColors.Outline))
            if (unread > 0) {
                Spacer(Modifier.height(4.dp))
                Box(
                    Modifier.padding(horizontal = 6.dp, vertical = 2.dp).clip(VShapes.Full).background(VColors.Error),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        unread.toString(),
                        style = VTypography.ThreadBadge.copy(color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold),
                    )
                }
            }
        }
    }
    // Border-bottom divider
    Box(
        Modifier.fillMaxWidth().height(1.dp).background(VColors.SurfaceContainer)
            .padding(start = 82.dp),
    )
}

@Composable
private fun StatusBox(msg: String, isError: Boolean = false) {
    Box(
        Modifier.fillMaxWidth().height(120.dp).clip(VShapes.Lg)
            .background(if (isError) VColors.ErrorContainer else VColors.SurfaceContainerLow),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            msg,
            style = VTypography.UpdateText.copy(
                color = if (isError) VColors.OnErrorContainer else VColors.OnSurfaceVariant,
            ),
        )
    }
}
