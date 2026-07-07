package com.littlebridge.enrollplus.ui.v2.screens.parent

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentMessageDto
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentMessageThreadDto
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentRecipientDto
import com.littlebridge.enrollplus.feature.parent.presentation.ParentMessageViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.components.VAvatar
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.theme.VTheme
import com.littlebridge.enrollplus.ui.v2.theme.colored
import org.koin.compose.viewmodel.koinViewModel

/**
 * RA-51: parent Messages inbox + conversation detail. Mirror of the admin
 * [com.littlebridge.enrollplus.ui.v2.screens.school.MessagesScreenV2] but on
 * the parent endpoints. Wired to the real [ParentMessageViewModel]
 * (`GET /api/v1/parent/messages/threads`, `…/{id}/messages`, `POST /parent/messages`).
 *
 * No MockV2 — replaces the old hardcoded fake-thread stub. Three states via
 * [VStateHost] (LAW 3) for both the list and the open conversation.
 */
@Composable
fun ParentMessagesScreenV2(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    initialThreadId: String? = null,
    viewModel: ParentMessageViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()

    LaunchedEffect(Unit) { viewModel.loadThreads() }

    // Deep-link: auto-open a specific conversation when initialThreadId is provided.
    LaunchedEffect(initialThreadId, state.threads) {
        if (initialThreadId != null && state.openThreadId == null && !state.loading && state.threads.isNotEmpty()) {
            val thread = state.threads.firstOrNull { it.id == initialThreadId }
            if (thread != null) {
                viewModel.markAsRead(thread.id)
                viewModel.openThread(thread.id, thread.senderName)
            }
        }
    }

    // Back peels layers in order: compose-new → open conversation → exit.
    val backHandler: () -> Unit = {
        when {
            state.composeOpen -> viewModel.closeCompose()
            state.openThreadId != null -> viewModel.closeThread()
            else -> onBack()
        }
    }
    val title = when {
        state.composeOpen -> appString(StringKeys.PM_NEW_MESSAGE)
        state.openThreadId != null -> state.openThreadName.ifBlank { appString(StringKeys.PM_CONVERSATION) }
        else -> appString(StringKeys.PM_MESSAGES)
    }

    Column(modifier.fillMaxSize().statusBarsPadding()
        .imePadding()
        .navigationBarsPadding()) {
        VBackHeader(title = title, onBack = backHandler)

        ParentMessagesBody(viewModel = viewModel, modifier = Modifier.weight(1f).fillMaxWidth())
    }
}

/**
 * Phase 3 (commit 9) — the chrome-less messaging body, hosted *inside* the Conversations tab.
 *
 * Identical messaging surface as [ParentMessagesScreenV2] (inbox → conversation → compose-new
 * layers driven by the SAME [ParentMessageViewModel]), but WITHOUT the standalone status-bar
 * padding + [VBackHeader] — the Conversations hub owns that chrome. Drilling into a thread or
 * compose-new is handled by the shared VM state, so the segmented hub's back is layered by the
 * caller via [ParentMessageViewModel.composeOpen]/[ParentMessageViewModel.openThreadId].
 */
@Composable
fun ParentMessagesBody(
    viewModel: ParentMessageViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateV2()

    // P3-1: Only load if not already loaded (prevents double-fetch flash when
    // hosted inside ParentMessagesScreenV2 which also calls loadThreads).
    LaunchedEffect(Unit) {
        if (state.threads.isEmpty() && !state.loading) {
            viewModel.loadThreads()
        }
    }

    Column(modifier) {
        when {
            // RA-S07: compose-new is the topmost layer (back closes it first).
            state.composeOpen -> {
                Column(Modifier.fillMaxSize()) {
                    VBackHeader(
                        title = appString(StringKeys.PM_NEW_MESSAGE),
                        onBack = viewModel::closeCompose,
                    )
                    ParentComposeNewContent(
                        recipients = state.composeRecipients,
                        loading = state.composeLoadingRecipients,
                        error = state.composeError,
                        isEmpty = state.composeEmpty,
                        sending = state.sending,
                        onSend = viewModel::composeNew,
                        onRetry = viewModel::openCompose,
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                    )
                }
            }
            state.openThreadId != null -> {
                Column(Modifier.fillMaxSize()) {
                    VBackHeader(
                        title = state.openThreadName.ifBlank { appString(StringKeys.PM_CONVERSATION) },
                        onBack = viewModel::closeThread,
                    )
                    ParentConversationContent(
                        messages = state.messages,
                        loading = state.conversationLoading,
                        error = state.conversationError,
                        isEmpty = state.conversationEmpty,
                        sending = state.sending,
                        replyError = state.replyError,
                        onSend = viewModel::reply,
                        onDismissReplyError = viewModel::clearReplyError,
                        onRetry = { state.openThreadId?.let { viewModel.openThread(it, state.openThreadName) } },
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                    )
                }
            }
            else -> {
                ParentThreadListContent(
                    threads = state.threads,
                    loading = state.loading,
                    error = state.error,
                    isEmpty = state.isEmpty,
                    onOpenThread = { t ->
                        viewModel.markAsRead(t.id)
                        viewModel.openThread(t.id, t.senderName)
                    },
                    onCompose = viewModel::openCompose,
                    onRetry = viewModel::loadThreads,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
private fun ParentThreadListContent(
    threads: List<ParentMessageThreadDto>,
    loading: Boolean,
    error: String?,
    isEmpty: Boolean,
    onOpenThread: (ParentMessageThreadDto) -> Unit,
    onCompose: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        when {
            loading && threads.isEmpty() ->
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = VColors.violet, modifier = Modifier.size(36.dp))
                }

            error != null && threads.isEmpty() ->
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    ThreadEmptyCard(
                        title = "Couldn't load messages",
                        body = error,
                        icon = VIcons.Chat,
                    )
                }

            isEmpty ->
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    ThreadEmptyCard(
                        title = "No messages yet",
                        body = "Start a conversation with your child's teacher or school office.",
                        icon = VIcons.Chat,
                    )
                }

            else ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(threads, key = { it.id }) { thread ->
                        ParentThreadRow(
                            thread = thread,
                            onClick = { onOpenThread(thread) },
                        )
                    }
                }
        }

        // Floating compose-new FAB — premium violet
        Box(
            Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 20.dp)
                .size(56.dp)
                .clip(CircleShape)
                .background(VColors.violet)
                .clickable(onClick = onCompose),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                VIcons.Edit3,
                contentDescription = "New message",
                tint = VColors.white,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Composable
private fun ParentThreadRow(thread: ParentMessageThreadDto, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.lg)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        VAvatar(
            name = thread.senderName.ifBlank { "?" },
            src = thread.senderImageUrl,
            size = 52.dp,
        )

        Column(Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    thread.senderName,
                    style = VTypography.body.copy(
                        fontWeight = if (thread.unreadCount > 0) FontWeight.Bold else FontWeight.SemiBold,
                    ),
                    color = VColors.ink,
                    modifier = Modifier.weight(1f, fill = false),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    thread.time,
                    style = VTypography.caption,
                    color = if (thread.unreadCount > 0) VColors.violet else VColors.ink3,
                )
            }

            if (thread.senderRole.isNotBlank()) {
                Text(
                    thread.senderRole,
                    style = VTypography.caption.copy(fontSize = 11.sp),
                    color = VColors.ink3,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Spacer(Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    thread.lastMessage,
                    style = VTypography.bodySmall,
                    color = if (thread.unreadCount > 0) VColors.ink2 else VColors.ink3,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                if (thread.unreadCount > 0) {
                    Box(
                        Modifier
                            .clip(CircleShape)
                            .background(VColors.violet)
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            if (thread.unreadCount > 99) "99+" else thread.unreadCount.toString(),
                            style = VTypography.caption.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                            ),
                            color = VColors.white,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ThreadEmptyCard(
    title: String,
    body: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
) {
    Column(
        Modifier
            .padding(horizontal = 32.dp)
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.lg)
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier.size(64.dp).clip(CircleShape).background(VColors.violetSoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = VColors.violet, modifier = Modifier.size(28.dp))
        }
        Spacer(Modifier.height(16.dp))
        Text(title, style = VTypography.body.copy(fontWeight = FontWeight.SemiBold), color = VColors.ink)
        Spacer(Modifier.height(4.dp))
        Text(body, style = VTypography.caption, color = VColors.ink2)
    }
}

/**
 * RA-S07 — parent compose-NEW: pick a recipient (the child's class teacher / school office),
 * type a message, send. `onSend(recipientUserId, body)` starts a real 1:1 conversation.
 * Rebuilt with the Academics premium design language.
 */
@Composable
private fun ParentComposeNewContent(
    recipients: List<ParentRecipientDto>,
    loading: Boolean,
    error: String?,
    isEmpty: Boolean,
    sending: Boolean,
    onSend: (String, String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selected by remember { mutableStateOf<ParentRecipientDto?>(null) }
    var body by remember { mutableStateOf("") }
    val keyboard = LocalSoftwareKeyboardController.current

    Column(modifier) {
        Box(Modifier.weight(1f).fillMaxWidth()) {
            when {
                loading && recipients.isEmpty() ->
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = VColors.violet, modifier = Modifier.size(36.dp))
                    }

                error != null && recipients.isEmpty() ->
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        ThreadEmptyCard(
                            title = "Couldn't load contacts",
                            body = error,
                            icon = VIcons.Chat,
                        )
                    }

                isEmpty ->
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        ThreadEmptyCard(
                            title = "No contacts",
                            body = "Your school hasn't added any teachers or staff to message yet.",
                            icon = VIcons.Chat,
                        )
                    }

                else ->
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        item {
                            Text(
                                "Select recipient",
                                style = VTypography.caption.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 0.8.sp,
                                ),
                                color = VColors.ink3,
                                modifier = Modifier.padding(bottom = 6.dp),
                            )
                        }
                        items(recipients, key = { it.id }) { recipient ->
                            ParentRecipientRow(
                                recipient = recipient,
                                isSelected = selected?.id == recipient.id,
                                onClick = { selected = recipient },
                            )
                        }
                    }
            }
        }

        ParentComposeBar(
            text = body,
            onTextChange = { body = it },
            placeholder = if (selected == null) "Pick a recipient…" else "Message ${selected!!.name}",
            enabled = selected != null && !sending,
            sending = sending,
            onSend = {
                val r = selected
                if (r != null && body.isNotBlank()) {
                    onSend(r.id, body.trim())
                    body = ""
                    keyboard?.hide()
                }
            },
        )
    }
}

@Composable
private fun ParentRecipientRow(recipient: ParentRecipientDto, isSelected: Boolean, onClick: () -> Unit) {
    val bg = if (isSelected) VColors.violetSoft else VColors.surfaceCard
    val borderColor = if (isSelected) VColors.violet else VColors.line

    Row(
        Modifier
            .fillMaxWidth()
            .clip(VShapes.lg)
            .background(bg)
            .border(1.dp, borderColor, VShapes.lg)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        VAvatar(name = recipient.name.ifBlank { "?" }, src = recipient.imageUrl, size = 48.dp)
        Column(Modifier.weight(1f)) {
            Text(
                recipient.name,
                style = VTypography.body.copy(fontWeight = FontWeight.SemiBold),
                color = VColors.ink,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (recipient.subtitle.isNotBlank()) {
                Text(
                    recipient.subtitle,
                    style = VTypography.caption,
                    color = VColors.ink2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        if (isSelected) {
            Box(
                Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(VColors.violet),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    VIcons.Check,
                    contentDescription = "Selected",
                    tint = VColors.white,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

@Composable
private fun ParentConversationContent(
    messages: List<ParentMessageDto>,
    loading: Boolean,
    error: String?,
    isEmpty: Boolean,
    sending: Boolean,
    replyError: String? = null,
    onSend: (String) -> Unit,
    onDismissReplyError: () -> Unit = {},
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var reply by remember { mutableStateOf("") }
    val keyboard = LocalSoftwareKeyboardController.current
    val listState = rememberLazyListState()

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    Column(modifier) {
        // Chat surface — warm cream background, premium bubbles
        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(VColors.cream),
        ) {
            when {
                loading && messages.isEmpty() ->
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = VColors.violet, modifier = Modifier.size(36.dp))
                    }

                error != null && messages.isEmpty() ->
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        ThreadEmptyCard(
                            title = "Couldn't load conversation",
                            body = error,
                            icon = VIcons.Chat,
                        )
                    }

                isEmpty ->
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        ThreadEmptyCard(
                            title = "Start the conversation",
                            body = "Send a message to your child's teacher or school office.",
                            icon = VIcons.Chat,
                        )
                    }

                else ->
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            horizontal = 16.dp,
                            vertical = 12.dp,
                        ),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        items(messages, key = { it.id }) { msg ->
                            ParentMessageBubble(msg)
                        }
                    }
            }
        }

        // Inline reply error banner
        AnimatedVisibility(
            visible = replyError != null,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut(),
        ) {
            if (replyError != null) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(VColors.errorSoft)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        replyError,
                        style = VTypography.caption,
                        color = VColors.error,
                        modifier = Modifier.weight(1f),
                    )
                    Box(
                        Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(VColors.error.copy(alpha = 0.12f))
                            .clickable(onClick = onDismissReplyError),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            VIcons.Close,
                            contentDescription = "Dismiss",
                            tint = VColors.error,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            }
        }

        ParentComposeBar(
            text = reply,
            onTextChange = { reply = it },
            placeholder = "Type a message…",
            enabled = !sending,
            sending = sending,
            onSend = {
                if (reply.isNotBlank()) {
                    onSend(reply.trim())
                    reply = ""
                    keyboard?.hide()
                }
            },
        )
    }
}

@Composable
private fun ParentMessageBubble(msg: ParentMessageDto) {
    val isMine = msg.isMine
    val isDeleted = msg.deletedAt != null

    val bubbleColor = if (isMine) VColors.violet else VColors.surfaceCard
    val textColor = if (isMine) VColors.white else VColors.ink
    val timeColor = if (isMine) VColors.white.copy(alpha = 0.7f) else VColors.ink3
    val bubbleBorder = if (isMine) null else VColors.line

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start,
    ) {
        val bubbleShape = if (isMine) {
            RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 4.dp)
        } else {
            RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 4.dp, bottomEnd = 18.dp)
        }

        val bubbleModifier = Modifier
            .widthIn(max = 280.dp)
            .clip(bubbleShape)
            .background(bubbleColor)
            .then(
                if (bubbleBorder != null) Modifier.border(1.dp, bubbleBorder, bubbleShape) else Modifier
            )
            .padding(horizontal = 14.dp, vertical = 10.dp)

        Column(bubbleModifier) {
            if (isDeleted) {
                Text(
                    "This message was deleted",
                    style = VTypography.body.copy(
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    ),
                    color = textColor,
                )
            } else {
                Text(
                    msg.body,
                    style = VTypography.bodySmall,
                    color = textColor,
                )
            }
            Spacer(Modifier.height(4.dp))
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                // P2-5: Status ticks for own messages
                if (isMine && !isDeleted) {
                    when (msg.status?.uppercase()) {
                        "READ" -> {
                            Icon(
                                VIcons.Check,
                                contentDescription = null,
                                tint = VColors.white.copy(alpha = 0.9f),
                                modifier = Modifier.size(14.dp),
                            )
                            Spacer(Modifier.size(2.dp))
                            Icon(
                                VIcons.Check,
                                contentDescription = "Read",
                                tint = VColors.white.copy(alpha = 0.9f),
                                modifier = Modifier.size(14.dp),
                            )
                        }
                        "DELIVERED" -> {
                            Icon(
                                VIcons.Check,
                                contentDescription = "Delivered",
                                tint = VColors.white.copy(alpha = 0.7f),
                                modifier = Modifier.size(14.dp),
                            )
                            Spacer(Modifier.size(2.dp))
                            Icon(
                                VIcons.Check,
                                contentDescription = null,
                                tint = VColors.white.copy(alpha = 0.7f),
                                modifier = Modifier.size(14.dp),
                            )
                        }
                        "SENT" -> {
                            Icon(
                                VIcons.Check,
                                contentDescription = "Sent",
                                tint = VColors.white.copy(alpha = 0.5f),
                                modifier = Modifier.size(14.dp),
                            )
                        }
                    }
                    Spacer(Modifier.size(4.dp))
                }
                Text(
                    msg.time,
                    style = VTypography.caption.copy(fontSize = 10.sp),
                    color = timeColor,
                )
                // P2-10: Edited label
                if (msg.editedAt != null && !isDeleted) {
                    Spacer(Modifier.size(4.dp))
                    Text(
                        "Edited",
                        style = VTypography.caption.copy(fontSize = 9.sp),
                        color = timeColor,
                    )
                }
            }
        }
    }
}

/**
 * Shared compose bar used by both the conversation and compose-new screens.
 * Premium cream input pill with violet send button.
 */
@Composable
private fun ParentComposeBar(
    text: String,
    onTextChange: (String) -> Unit,
    placeholder: String,
    enabled: Boolean,
    sending: Boolean,
    onSend: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val canSend = text.isNotBlank() && enabled

    Column(
        modifier
            .fillMaxWidth()
            .background(VColors.surfaceCard),
    ) {
        // Subtle top hairline
        Box(Modifier.fillMaxWidth().height(1.dp).background(VColors.line))

        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Text input in a rounded pill
            Box(
                Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(22.dp))
                    .background(VColors.creamDeep)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
            ) {
                OutlinedTextField(
                    value = text,
                    onValueChange = onTextChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            placeholder,
                            style = VTypography.body,
                            color = VColors.ink3,
                        )
                    },
                    enabled = enabled,
                    singleLine = false,
                    maxLines = 4,
                    shape = RoundedCornerShape(22.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        disabledBorderColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        cursorColor = VColors.violet,
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
                    keyboardActions = KeyboardActions(onSend = { if (canSend) onSend() }),
                    textStyle = VTypography.body.copy(color = VColors.ink),
                )
            }

            // Circular send button
            Box(
                Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (canSend) VColors.violet else VColors.lineSoft)
                    .clickable(enabled = canSend, onClick = onSend),
                contentAlignment = Alignment.Center,
            ) {
                if (sending) {
                    CircularProgressIndicator(
                        color = VColors.white,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp),
                    )
                } else {
                    Icon(
                        VIcons.Send,
                        contentDescription = "Send",
                        tint = VColors.white,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}
