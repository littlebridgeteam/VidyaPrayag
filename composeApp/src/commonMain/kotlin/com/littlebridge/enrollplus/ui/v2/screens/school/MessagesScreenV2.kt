package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.littlebridge.enrollplus.feature.admin.domain.model.Message
import com.littlebridge.enrollplus.feature.admin.domain.model.MessageAttachment
import com.littlebridge.enrollplus.feature.admin.domain.model.MessageThread
import com.littlebridge.enrollplus.feature.admin.presentation.ComposeState
import com.littlebridge.enrollplus.feature.admin.presentation.ConversationState
import com.littlebridge.enrollplus.feature.admin.presentation.MessageRecipient
import com.littlebridge.enrollplus.feature.admin.presentation.MessagesState
import com.littlebridge.enrollplus.feature.admin.presentation.MessagesViewModel
import com.littlebridge.enrollplus.platform.MediaPicker
import com.littlebridge.enrollplus.platform.rememberMediaPicker
import com.littlebridge.enrollplus.ui.v2.components.PremiumComposeBar
import com.littlebridge.enrollplus.ui.v2.components.PremiumDateHeader
import com.littlebridge.enrollplus.ui.v2.components.PremiumMessageBubble
import com.littlebridge.enrollplus.ui.v2.components.PremiumMessageData
import com.littlebridge.enrollplus.ui.v2.components.PremiumAttachmentData
import com.littlebridge.enrollplus.ui.v2.components.VAvatar
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VPullRefresh
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.SkeletonList
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.theme.staggeredItemEntrance
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

/**
 * MessagesScreenV2 — admin Messages inbox + conversation detail.
 *
 * Wired to the real [MessagesViewModel] (`GET /api/v1/school/messages/threads`,
 * `POST /api/v1/school/messages`, `POST /threads/{id}/read`).
 *
 * Two modes:
 *   • Thread list (default) — VCard rows with avatar, sender name/role, last message,
 *     time, unread VBadge (Arctic tone).
 *   • Conversation detail (when [ConversationState.threadId] is non-null) — bubble
 *     messages (mine = teal-tint, theirs = cream), bottom VInput + Send VButton.
 *
 * Rendered as a portal overlay; back chevron returns to [SchoolPortalV2] tabs.
 * No MockV2. Three states via [VStateHost] (LAW 3).
 */
@Composable
fun MessagesScreenV2(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    initialThreadId: String? = null,
    viewModel: MessagesViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    val isLoading by viewModel.isLoading.collectAsStateV2()
    val errorMessage by viewModel.errorMessage.collectAsStateV2()
    val conversation by viewModel.conversation.collectAsStateV2()
    val compose by viewModel.compose.collectAsStateV2()

    // Deep-link: auto-open a specific conversation when initialThreadId is provided.
    LaunchedEffect(initialThreadId, state.threads) {
        if (initialThreadId != null && conversation.threadId == null && !isLoading && state.threads.isNotEmpty()) {
            val thread = state.threads.firstOrNull { it.id == initialThreadId }
            if (thread != null) {
                viewModel.markAsRead(thread.id)
                viewModel.openConversation(thread.id)
            }
        }
    }

    // RA-S07: the compose-new sheet is the topmost layer — back closes it first.
    if (compose.isOpen) {
        ComposeNewContent(
            compose = compose,
            isSending = state.isSending,
            onSend = viewModel::composeNew,
            onClose = viewModel::closeCompose,
            modifier = modifier.fillMaxSize(),
        )
        return
    }

    // If a conversation is open, back closes it first; otherwise back exits the overlay.
    val backHandler: () -> Unit = {
        if (conversation.threadId != null) viewModel.closeConversation() else onBack()
    }

    val title = if (conversation.threadId != null) {
        conversation.senderName.ifBlank { "Conversation" }
    } else {
        "Messages"
    }

    Column(modifier.fillMaxSize().statusBarsPadding()) {
        VBackHeader(title = title, onBack = backHandler, pinRouteId = "overlay_messages")

        if (conversation.threadId != null) {
            ConversationContent(
                conversation = conversation,
                onSend = viewModel::sendReply,
                onSendWithAttachment = viewModel::sendReplyWithAttachment,
                onClearError = viewModel::clearConversationError,
                onMarkRead = viewModel::markAsRead,
                modifier = Modifier.weight(1f).fillMaxWidth(),
            )
        } else {
            VPullRefresh(isRefreshing = isLoading && state.threads.isNotEmpty(), onRefresh = { viewModel.refresh() }) {
            ThreadListContent(
                state = state,
                isLoading = isLoading,
                error = errorMessage,
                onOpenThread = { threadId ->
                    viewModel.markAsRead(threadId)
                    viewModel.openConversation(threadId)
                },
                onRetry = viewModel::refresh,
                onClearError = viewModel::clearError,
                onCompose = viewModel::openCompose,
                modifier = Modifier.fillMaxSize(),
            )
            }
        }
    }
}

@Composable
private fun ThreadListContent(
    state: MessagesState,
    isLoading: Boolean,
    error: String?,
    onOpenThread: (String) -> Unit,
    onRetry: () -> Unit,
    onClearError: () -> Unit,
    onCompose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        VStateHost(
            loading = isLoading,
            error = error,
            isEmpty = state.threads.isEmpty(),
            emptyTitle = "No messages yet",
            emptyBody = "Your inbox will populate as parents and teachers reach out.",
            emptyIcon = VIcons.Chat,
            onRetry = onRetry,
            skeleton = { SkeletonList(rows = 6, withAvatar = true) },
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 0.dp,
                    end = 0.dp,
                    top = 8.dp,
                    bottom = 80.dp,
                ),
            ) {
                itemsIndexed(state.threads, key = { _, it -> it.id }) { index, thread ->
                    ThreadRow(thread = thread, onClick = { onOpenThread(thread.id) }, modifier = Modifier.staggeredItemEntrance(index, state.threads.isNotEmpty()))
                }
            }
        }

        // Floating compose-new FAB — WhatsApp-style
        val interaction = remember { MutableInteractionSource() }
        Box(
            Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 20.dp)
                .size(56.dp)
                .clip(CircleShape)
                .background(VColors.violet)
                .clickable(interactionSource = interaction, indication = null, onClick = onCompose),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                VIcons.Edit3,
                contentDescription = "New message",
                tint = Color.White,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Composable
private fun ThreadRow(thread: MessageThread, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val interaction = remember { MutableInteractionSource() }
    Row(
        modifier
            .fillMaxWidth()
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        VAvatar(name = thread.senderName.ifBlank { "?" }, src = thread.senderImageUrl, size = 52.dp)
        Column(Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    thread.senderName,
                    style = VTypography.bodySmall.copy(
                        fontWeight = if (thread.isRead) FontWeight.SemiBold else FontWeight.Bold,
                    ),
                    color = VColors.ink,
                    modifier = Modifier.weight(1f, fill = false),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    thread.time,
                    style = VTypography.caption,
                    color = if (thread.isRead) VColors.ink3 else VColors.violet,
                )
            }
            Spacer(Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    thread.lastMessage.ifBlank { "No message yet" },
                    style = VTypography.body,
                    color = if (thread.isRead) VColors.ink3 else VColors.ink2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                if (thread.unreadCount > 0) {
                    Box(
                        Modifier
                            .clip(CircleShape)
                            .background(VColors.violet)
                            .padding(horizontal = 7.dp, vertical = 2.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            if (thread.unreadCount > 99) "99+" else thread.unreadCount.toString(),
                            style = VTypography.caption.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ConversationContent(
    conversation: ConversationState,
    onSend: (String) -> Unit,
    onSendWithAttachment: (String, ByteArray?, String?, String?) -> Unit,
    onClearError: () -> Unit,
    onMarkRead: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var reply by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    var pickedImage: Triple<ByteArray, String, String>? by remember { mutableStateOf(null) }
    var conversationError by remember { mutableStateOf<String?>(null) }
    val mediaPicker = rememberMediaPicker(
        onPicked = { bytes, mimeType, fileName ->
            pickedImage = Triple(bytes, mimeType, fileName)
        },
        onUnsupported = { msg ->
            conversationError = msg
        },
    )

    // P1-13: Mark thread as read on open
    LaunchedEffect(conversation.threadId) {
        conversation.threadId?.let { onMarkRead(it) }
    }

    // P1-9: Auto-scroll to bottom when new messages arrive
    LaunchedEffect(conversation.messages.size) {
        if (conversation.messages.isNotEmpty()) {
            listState.animateScrollToItem(conversation.messages.lastIndex)
        }
    }

    Column(modifier) {
        // Chat surface
        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(VColors.cream),
        ) {
            VStateHost(
                loading = conversation.isLoading,
                error = conversation.error ?: conversationError,
                isEmpty = conversation.messages.isEmpty(),
                emptyTitle = "No messages yet",
                emptyBody = "Start the conversation by sending a message below.",
                emptyIcon = VIcons.Chat,
                onRetry = onClearError,
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        horizontal = 16.dp,
                        vertical = 12.dp,
                    ),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    itemsIndexed(conversation.messages, key = { _, msg -> msg.id }) { index, msg ->
                        val showDateHeader = index == 0 ||
                            !msg.time.contentEquals(conversation.messages[index - 1].time, true)
                        val isGroupStart = index == 0 ||
                            conversation.messages[index - 1].senderId != msg.senderId

                        if (showDateHeader && msg.time.isNotBlank()) {
                            PremiumDateHeader(date = msg.time)
                        }
                        PremiumMessageBubble(
                            msg = msg.toPremiumMessageData(),
                            isGroupStart = isGroupStart,
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
            enabled = !conversation.isSending,
            sending = conversation.isSending,
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

/** Adapter: Map admin Message to PremiumMessageData for shared bubble rendering. */
private fun Message.toPremiumMessageData(): PremiumMessageData = object : PremiumMessageData {
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

/** Adapter: Map admin MessageAttachment to PremiumAttachmentData. */
private fun MessageAttachment.toPremiumAttachmentData(): PremiumAttachmentData = object : PremiumAttachmentData {
    override val storageUrl: String get() = this@toPremiumAttachmentData.storageUrl
    override val thumbnailUrl: String? get() = this@toPremiumAttachmentData.thumbnailUrl
    override val attachmentType: String get() = this@toPremiumAttachmentData.attachmentType
    override val fileName: String get() = this@toPremiumAttachmentData.fileName
}

/**
 * RA-S07 — compose-new screen: pick a recipient (a teacher in the school), type a message, send.
 * `onSend(recipientUserId, body)` starts a real 1:1 conversation via the two-row engine.
 */
@Composable
private fun ComposeNewContent(
    compose: ComposeState,
    isSending: Boolean,
    onSend: (String, String) -> Unit,
    onClose: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selected by remember { mutableStateOf<MessageRecipient?>(null) }
    var body by remember { mutableStateOf("") }
    val keyboard = LocalSoftwareKeyboardController.current

    // BUG-023: Apply imePadding only to the bottom compose bar, NOT to the root Column.
    // Applying it to the root caused double insets — the recipient list was pushed up
    // while the compose bar still sat at the bottom edge behind the keyboard.
    Column(modifier.statusBarsPadding()) {
        VBackHeader(title = "New message", onBack = onClose)
        Box(Modifier.weight(1f).fillMaxWidth()) {
            VStateHost(
                loading = compose.isLoadingRecipients,
                error = compose.error,
                isEmpty = compose.candidates.isEmpty(),
                emptyTitle = "No recipients",
                emptyBody = "Add teachers or enroll students with parents to start a conversation.",
                emptyIcon = VIcons.Chat,
                onRetry = onClose,
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    item {
                        Text(
                            "Select recipient",
                            style = VTypography.label,
                            color = VColors.ink3,
                            modifier = Modifier.padding(bottom = 8.dp),
                        )
                    }
                    // BUG-021: Search bar to filter recipients by name/subtitle.
                    item {
                        OutlinedTextField(
                            value = compose.searchQuery,
                            onValueChange = onSearchQueryChange,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            placeholder = {
                                Text(
                                    "Search teachers, staff, parents…",
                                    style = VTypography.body,
                                    color = VColors.ink3,
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    VIcons.Search,
                                    contentDescription = null,
                                    tint = VColors.ink3,
                                    modifier = Modifier.size(20.dp),
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VColors.violet,
                                unfocusedBorderColor = VColors.line,
                                focusedContainerColor = VColors.cream,
                                unfocusedContainerColor = VColors.cream,
                                cursorColor = VColors.violet,
                            ),
                            textStyle = VTypography.body.copy(color = VColors.ink),
                        )
                    }
                    items(compose.filteredCandidates, key = { it.id }) { recipient ->
                        RecipientRow(
                            recipient = recipient,
                            isSelected = selected?.id == recipient.id,
                            onClick = { selected = recipient },
                        )
                    }
                }
            }
        }

        // BUG-023: Apply imePadding + navigationBarsPadding ONLY to the compose bar
        // so it floats above the keyboard without double-insetting the content area.
        PremiumComposeBar(
            text = body,
            onTextChange = { body = it },
            placeholder = if (selected == null) "Pick a recipient above…" else "Message ${selected?.name ?: ""}…",
            enabled = selected != null && !isSending,
            sending = isSending,
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
private fun RecipientRow(recipient: MessageRecipient, isSelected: Boolean, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val bg = if (isSelected) VColors.violetSoft else Color.Transparent
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        VAvatar(name = recipient.name.ifBlank { "?" }, src = recipient.imageUrl, size = 48.dp)
        Column(Modifier.weight(1f)) {
            Text(
                recipient.name,
                style = VTypography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = VColors.ink,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                recipient.subtitle,
                style = VTypography.caption,
                color = VColors.ink3,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
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
                    tint = Color.White,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}
