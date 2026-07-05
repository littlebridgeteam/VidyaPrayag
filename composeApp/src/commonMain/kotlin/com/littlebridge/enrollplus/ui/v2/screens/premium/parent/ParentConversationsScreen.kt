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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.feature.parent.presentation.ParentMessageViewModel
import com.littlebridge.enrollplus.ui.v2.components.cards.VUpdateCard
import com.littlebridge.enrollplus.ui.v2.components.cards.UpdateAction
import com.littlebridge.enrollplus.ui.v2.components.navigation.VFilterChip
import com.littlebridge.enrollplus.ui.v2.components.typography.VSectionHeader
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
import com.littlebridge.enrollplus.ui.v2.modifiers.shapeMorph
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.PremiumTheme
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VMotion
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

        // ── Top bar with title + search + compose ──
        Row(
            Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Conversations", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                val searchInteraction = remember { MutableInteractionSource() }
                Box(
                    Modifier.size(40.dp).clip(CircleShape).background(VColors.SurfaceContainerHigh)
                        .pressScale(searchInteraction, pressedScale = 0.9f)
                        .clickable(interactionSource = searchInteraction, indication = null) { },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.Search, contentDescription = "Search", tint = VColors.OnSurfaceVariant, modifier = Modifier.size(22.dp))
                }
                val composeInteraction = remember { MutableInteractionSource() }
                Box(
                    Modifier.size(40.dp).clip(CircleShape).background(VColors.Primary)
                        .pressScale(composeInteraction, pressedScale = 0.9f)
                        .clickable(interactionSource = composeInteraction, indication = null) { },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = "Compose", tint = VColors.OnPrimary, modifier = Modifier.size(20.dp))
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // ── Segment selector ──
        Row(Modifier.padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            VFilterChip(label = "Messages", active = segment == 0, onClick = { segment = 0 })
            VFilterChip(label = "Announcements", active = segment == 1, onClick = { segment = 1 })
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
                    Column(Modifier.padding(horizontal = 20.dp)) {
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
                            Spacer(Modifier.height(10.dp))
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
    val avatarColor = when (role.lowercase()) {
        "teacher" -> VColors.Primary
        "admin", "schooladmin" -> VColors.Tertiary
        else -> VColors.WarmOrange
    }
    Row(
        Modifier
            .fillMaxWidth()
            .clip(VShapes.Xl)
            .background(if (!isRead) VColors.PrimaryContainer.copy(alpha = 0.3f) else VColors.SurfaceContainerLowest)
            .pressScale(interaction, pressedScale = 0.97f)
            .shapeMorph(interaction, VShapes.XlDp, VShapes.TwoXlDp, VMotion.DurMedium2)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            Modifier.size(48.dp).clip(CircleShape).background(avatarColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                name.firstOrNull()?.toString() ?: "?",
                style = VTypography.HeroStatValue.copy(color = avatarColor, fontSize = 20.sp),
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
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(time, style = VTypography.ThreadTime.copy(color = VColors.OnSurfaceVariant))
            if (unread > 0) {
                Spacer(Modifier.height(6.dp))
                Box(
                    Modifier.padding(horizontal = 8.dp, vertical = 3.dp).clip(VShapes.Full).background(VColors.Error),
                ) {
                    Text(
                        unread.toString(),
                        style = VTypography.ThreadBadge.copy(color = Color.White),
                        modifier = Modifier.padding(horizontal = 4.dp),
                    )
                }
            }
        }
    }
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
