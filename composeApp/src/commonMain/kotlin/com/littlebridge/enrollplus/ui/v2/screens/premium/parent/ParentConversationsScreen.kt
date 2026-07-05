package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentMessageDto
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentMessageThreadDto
import com.littlebridge.enrollplus.feature.parent.presentation.ParentAnnouncement
import com.littlebridge.enrollplus.feature.parent.presentation.ParentAnnouncementViewModel
import com.littlebridge.enrollplus.feature.parent.presentation.ParentMessageViewModel
import com.littlebridge.enrollplus.ui.v2.components.misc.VShimmerBoxPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VStateHostPremium
import com.littlebridge.enrollplus.ui.v2.components.navigation.VFilterChip
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ParentConversationsScreen(
    onOpenOverlay: (ParentOverlay) -> Unit,
    onSwitchTab: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
    messageViewModel: ParentMessageViewModel = koinViewModel(),
    announcementViewModel: ParentAnnouncementViewModel = koinViewModel(),
) {
    var segment by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        messageViewModel.loadThreads()
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Segment row: Messages | Announcements
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            VFilterChip(
                label = "Messages",
                active = segment == 0,
                onClick = { segment = 0 },
            )
            VFilterChip(
                label = "Announcements",
                active = segment == 1,
                onClick = { segment = 1 },
            )
        }

        // Content area: weight(1f) — LazyColumn as root inside Box (spec: CRITICAL)
        Box(Modifier.weight(1f).fillMaxWidth()) {
            AnimatedContent(
                targetState = segment,
                transitionSpec = { fadeIn(tween(300)).togetherWith(fadeOut(tween(200))) },
                label = "conversationsSegment",
            ) { current ->
                when (current) {
                    0 -> MessagesSegment(viewModel = messageViewModel)
                    1 -> AnnouncementsSegment(viewModel = announcementViewModel)
                }
            }
        }
    }
}

// ── Messages ──────────────────────────────────────────────────────────────

@Composable
private fun MessagesSegment(viewModel: ParentMessageViewModel) {
    val state by viewModel.state.collectAsStateV2()

    if (state.openThreadId != null) {
        ConversationView(
            state = state,
            onBack = { viewModel.closeThread() },
            onSend = { viewModel.reply(it) },
        )
    } else {
        VStateHostPremium(
            loading = state.loading,
            error = state.error,
            isEmpty = state.isEmpty,
            modifier = Modifier.fillMaxSize(),
            emptyTitle = "No messages yet",
            emptyIcon = Icons.AutoMirrored.Filled.Chat,
            onRetry = { viewModel.loadThreads() },
            skeleton = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    repeat(6) { VShimmerBoxPremium(height = 72.dp, shape = VShapes.Lg) }
                }
            },
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 20.dp, end = 20.dp, top = 8.dp, bottom = 140.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(state.threads, key = { it.id }) { thread ->
                    ThreadCard(
                        thread = thread,
                        onClick = {
                            viewModel.markAsRead(thread.id)
                            viewModel.openThread(thread.id, thread.senderName)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun ThreadCard(
    thread: ParentMessageThreadDto,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.Lg)
            .background(VColors.SurfaceContainerLow)
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(VColors.PrimaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = thread.senderName.take(1).uppercase(),
                style = VTypography.QuickStatValue.copy(color = VColors.OnPrimaryContainer),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = thread.senderName,
                    style = VTypography.ThreadName.copy(color = VColors.OnSurface),
                )
                Text(
                    text = thread.time,
                    style = VTypography.ThreadTime.copy(color = VColors.Outline),
                )
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = thread.lastMessage,
                style = VTypography.ThreadPreview.copy(color = VColors.OnSurfaceVariant),
                maxLines = 1,
            )
        }
        if (thread.unreadCount > 0) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(VColors.Primary),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = thread.unreadCount.toString(),
                    style = VTypography.ThreadBadge.copy(color = VColors.OnPrimary),
                )
            }
        }
    }
}

@Composable
private fun ConversationView(
    state: com.littlebridge.enrollplus.feature.parent.presentation.ParentMessageState,
    onBack: () -> Unit,
    onSend: (String) -> Unit,
) {
    var input by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        // Conversation header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(VColors.Surface)
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = VColors.OnSurface)
            }
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(VColors.PrimaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = state.openThreadName.take(1).uppercase(),
                    style = VTypography.QuickStatValue.copy(color = VColors.OnPrimaryContainer),
                )
            }
            Text(
                text = state.openThreadName,
                style = VTypography.ThreadName.copy(color = VColors.OnSurface),
            )
        }

        // Messages list
        if (state.conversationLoading) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                repeat(8) { VShimmerBoxPremium(height = 48.dp, shape = VShapes.Md) }
            }
        } else if (state.conversationError != null) {
            val errorMsg = state.conversationError!!
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = errorMsg,
                        style = VTypography.BodyMedium.copy(color = VColors.OnSurfaceVariant),
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 20.dp, end = 20.dp, top = 16.dp, bottom = 16.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                items(state.messages, key = { it.id }) { msg ->
                    MessageBubble(message = msg)
                }
            }
        }

        // Compose bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(VColors.SurfaceContainerLow)
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .imePadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(VShapes.Full)
                    .background(VColors.SurfaceContainerHigh)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                androidx.compose.material3.OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    placeholder = { Text("Type a message...", style = VTypography.BodyMedium.copy(color = VColors.OnSurfaceVariant)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                    ),
                )
            }
            IconButton(
                onClick = {
                    if (input.isNotBlank()) {
                        onSend(input)
                        input = ""
                    }
                },
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = VColors.Primary,
                )
            }
        }
    }
}

@Composable
private fun MessageBubble(message: ParentMessageDto) {
    val isMine = message.isMine
    val bg = if (isMine) VColors.Primary else VColors.SurfaceContainerHigh
    val fg = if (isMine) VColors.OnPrimary else VColors.OnSurface
    val alignment = if (isMine) Alignment.End else Alignment.Start

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment,
    ) {
        Box(
            modifier = Modifier
                .width(280.dp)
                .clip(
                    if (isMine) VShapes.Lg else VShapes.Lg,
                )
                .background(bg)
                .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            Column {
                Text(
                    text = message.body,
                    style = VTypography.BodyMedium.copy(color = fg),
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = message.time,
                    style = VTypography.ThreadTime.copy(color = if (isMine) VColors.OnPrimary.copy(alpha = 0.7f) else VColors.Outline),
                )
            }
        }
    }
}

// ── Announcements ─────────────────────────────────────────────────────────

@Composable
private fun AnnouncementsSegment(viewModel: ParentAnnouncementViewModel) {
    val state by viewModel.state.collectAsStateV2()

    VStateHostPremium(
        loading = state.isLoading,
        error = state.error,
        isEmpty = state.announcements.isEmpty() && !state.isLoading,
        modifier = Modifier.fillMaxSize(),
        emptyTitle = "No announcements",
        emptyIcon = Icons.Filled.Campaign,
        onRetry = null,
        skeleton = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                repeat(5) { VShimmerBoxPremium(height = 100.dp, shape = VShapes.Lg) }
            }
        },
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 20.dp, end = 20.dp, top = 8.dp, bottom = 140.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(state.announcements, key = { it.id }) { announcement ->
                AnnouncementCard(announcement = announcement)
            }
        }
    }
}

@Composable
private fun AnnouncementCard(announcement: ParentAnnouncement) {
    val categoryColor = when (announcement.category.lowercase()) {
        "holidays" -> VColors.Tertiary
        "ptm" -> VColors.Primary
        "events" -> VColors.Secondary
        "reminder" -> VColors.WarmOrange
        else -> VColors.Outline
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.Lg)
            .background(VColors.SurfaceContainerLow)
            .padding(16.dp),
    ) {
        if (announcement.isFeatured) {
            Box(
                modifier = Modifier
                    .clip(VShapes.Full)
                    .background(VColors.PrimaryContainer)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Text(
                    text = "Featured",
                    style = VTypography.ThreadTime.copy(color = VColors.OnPrimaryContainer),
                )
            }
            Spacer(Modifier.height(8.dp))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Box(Modifier.size(6.dp).clip(CircleShape).background(categoryColor))
                Text(
                    text = announcement.category,
                    style = VTypography.ThreadTime.copy(color = categoryColor),
                )
            }
            Text(
                text = announcement.date,
                style = VTypography.ThreadTime.copy(color = VColors.Outline),
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = announcement.title,
            style = VTypography.UpdateTitle.copy(color = VColors.OnSurface),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = announcement.description,
            style = VTypography.BodyMedium.copy(color = VColors.OnSurfaceVariant),
        )
    }
}
