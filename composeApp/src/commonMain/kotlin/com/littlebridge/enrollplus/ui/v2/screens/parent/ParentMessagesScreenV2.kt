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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
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
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentMessageAttachment
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentMessageDto
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentMessageThreadDto
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentRecipientDto
import com.littlebridge.enrollplus.feature.parent.presentation.ParentMessageState
import com.littlebridge.enrollplus.feature.parent.presentation.ParentMessageViewModel
import com.littlebridge.enrollplus.platform.rememberMediaPicker
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.components.PremiumAttachmentData
import com.littlebridge.enrollplus.ui.v2.components.PremiumComposeBar
import com.littlebridge.enrollplus.ui.v2.components.PremiumDateHeader
import com.littlebridge.enrollplus.ui.v2.components.PremiumMessageBubble
import com.littlebridge.enrollplus.ui.v2.components.PremiumMessageData
import com.littlebridge.enrollplus.ui.v2.components.VAvatar
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import org.koin.compose.viewmodel.koinViewModel

/**
 * RA-51: parent Messages inbox + conversation detail. Mirror of the admin
 * [com.littlebridge.enrollplus.ui.v2.screens.school.MessagesScreenV2] but on
 * the parent endpoints. Wired to the real [ParentMessageViewModel]
 * (`GET /api/v1/parent/messages/threads`, `…/{id}/messages`, `POST /parent/messages`).
 *
 * No MockV2 — replaces the old hardcoded fake-thread stub. Inline loading/error/empty states
 * for both the list and the open conversation.
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
    Column(
        modifier
            .fillMaxSize()
            .background(VColors.cream)
            .statusBarsPadding(),
    ) {
        PremiumMessageHeader(
            state = state,
            onBack = backHandler,
        )

        ParentMessagesBody(
            viewModel = viewModel,
            modifier = Modifier.weight(1f).fillMaxWidth(),
            showInnerHeaders = false,
        )
    }
}

@Composable
private fun PremiumMessageHeader(
    state: ParentMessageState,
    onBack: () -> Unit,
) {
    val title = when {
        state.composeOpen -> "New Message"
        state.openThreadId != null -> state.openThreadName.ifBlank { "Conversation" }
        else -> "Messages"
    }
    val subtitle = if (state.openThreadId != null && !state.composeOpen) {
        state.threads.firstOrNull { it.id == state.openThreadId }?.senderRole ?: ""
    } else {
        ""
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(VColors.surfaceCard)
                .border(1.dp, VColors.line, CircleShape)
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = VColors.ink,
                modifier = Modifier.size(20.dp),
            )
        }

        if (state.openThreadId != null && !state.composeOpen) {
            val thread = state.threads.firstOrNull { it.id == state.openThreadId }
            VAvatar(
                name = thread?.senderName?.ifBlank { "?" } ?: "?",
                src = thread?.senderImageUrl,
                size = 44.dp,
            )
        }

        Column(Modifier.weight(1f)) {
            Text(
                title,
                style = VTypography.body.copy(fontWeight = FontWeight.Bold),
                color = VColors.ink,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (subtitle.isNotBlank()) {
                Text(
                    subtitle,
                    style = VTypography.caption,
                    color = VColors.ink3,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
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
    showInnerHeaders: Boolean = true,
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
                    if (showInnerHeaders) {
                        VBackHeader(
                            title = "New Message",
                            onBack = viewModel::closeCompose,
                        )
                    }
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
                    if (showInnerHeaders) {
                        VBackHeader(
                            title = state.openThreadName.ifBlank { "Conversation" },
                            onBack = viewModel::closeThread,
                        )
                    }
                    ParentConversationContent(
                        messages = state.messages,
                        loading = state.conversationLoading,
                        error = state.conversationError,
                        isEmpty = state.conversationEmpty,
                        sending = state.sending,
                        replyError = state.replyError,
                        onSend = viewModel::reply,
                        onSendWithAttachment = viewModel::replyWithAttachment,
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

        PremiumComposeBar(
            text = body,
            onTextChange = { body = it },
            placeholder = if (selected == null) "Pick a recipient…" else "Message ${selected?.name ?: ""}",
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
            onAttach = {},
            modifier = Modifier.imePadding().navigationBarsPadding(),
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
    onSendWithAttachment: (String, ByteArray?, String?, String?) -> Unit,
    onDismissReplyError: () -> Unit = {},
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var reply by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    var pickedImage: Triple<ByteArray, String, String>? by remember { mutableStateOf(null) }
    var pickerError by remember { mutableStateOf<String?>(null) }
    val mediaPicker = rememberMediaPicker(
        onPicked = { bytes, mimeType, fileName ->
            pickedImage = Triple(bytes, mimeType, fileName)
        },
        onUnsupported = { msg -> pickerError = msg },
    )

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
                            horizontal = 20.dp,
                            vertical = 16.dp,
                        ),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        items(messages, key = { it.id }) { msg ->
                            PremiumMessageBubble(
                                msg = msg.toPremiumMessageData(),
                                isGroupStart = true,
                            )
                        }
                    }
            }
        }

        // Inline reply error banner
        AnimatedVisibility(
            visible = replyError != null || pickerError != null,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut(),
        ) {
            val errMsg = replyError ?: pickerError
            if (errMsg != null) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(VColors.errorSoft)
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        errMsg,
                        style = VTypography.caption,
                        color = VColors.error,
                        modifier = Modifier.weight(1f),
                    )
                    Box(
                        Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(VColors.error.copy(alpha = 0.12f))
                            .clickable(onClick = {
                                onDismissReplyError()
                                pickerError = null
                            }),
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

        // Premium compose bar with image attachment support
        PremiumComposeBar(
            text = reply,
            onTextChange = { reply = it },
            placeholder = "Type a message…",
            enabled = !sending,
            sending = sending,
            onSend = {
                val img = pickedImage
                if (img != null) {
                    onSendWithAttachment(reply.trim(), img.first, img.third, img.second)
                    pickedImage = null
                } else if (reply.isNotBlank()) {
                    onSend(reply.trim())
                }
                reply = ""
            },
            onAttach = { mediaPicker.launchImage() },
            imagePreviewBytes = pickedImage?.first,
            imagePreviewName = pickedImage?.third ?: "",
            onRemoveImage = { pickedImage = null },
            modifier = Modifier.imePadding().navigationBarsPadding(),
        )
    }
}

/** Adapter: Map ParentMessageDto to PremiumMessageData for shared bubble rendering. */
private fun ParentMessageDto.toPremiumMessageData(): PremiumMessageData = object : PremiumMessageData {
    override val id: String get() = this@toPremiumMessageData.id
    override val body: String get() = this@toPremiumMessageData.body
    override val isMine: Boolean get() = this@toPremiumMessageData.isMine
    override val time: String get() = this@toPremiumMessageData.time
    override val status: String? get() = this@toPremiumMessageData.status
    override val editedAt: String? get() = this@toPremiumMessageData.editedAt
    override val deletedAt: String? get() = this@toPremiumMessageData.deletedAt
    override val attachments: List<PremiumAttachmentData>
        get() = this@toPremiumMessageData.attachments.map { it.toPremiumAttachmentData() }
}

/** Adapter: Map ParentMessageAttachment to PremiumAttachmentData. */
private fun ParentMessageAttachment.toPremiumAttachmentData(): PremiumAttachmentData = object : PremiumAttachmentData {
    override val storageUrl: String get() = this@toPremiumAttachmentData.storageUrl
    override val thumbnailUrl: String? get() = this@toPremiumAttachmentData.thumbnailUrl
    override val attachmentType: String get() = this@toPremiumAttachmentData.attachmentType
    override val fileName: String get() = this@toPremiumAttachmentData.fileName
}
