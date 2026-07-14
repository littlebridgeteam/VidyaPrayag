package com.littlebridge.enrollplus.ui.v2.screens.teacher

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import com.littlebridge.enrollplus.feature.teacher.domain.model.TeacherMessageAttachment
import com.littlebridge.enrollplus.feature.teacher.domain.model.TeacherMessageDto
import com.littlebridge.enrollplus.feature.teacher.domain.model.TeacherMessageThreadDto
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherMessageViewModel
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherMessageState
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
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TeacherMessagesScreenV2(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    initialThreadId: String? = null,
    viewModel: TeacherMessageViewModel = koinViewModel(),
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

    val backHandler: () -> Unit = {
        when {
            state.openThreadId != null -> viewModel.closeThread()
            else -> onBack()
        }
    }
    val title = when {
        state.openThreadId != null -> state.openThreadName.ifBlank { "Conversation" }
        else -> "Messages"
    }

    Column(modifier.fillMaxSize().statusBarsPadding()) {
        VBackHeader(title = title, onBack = backHandler)
        TeacherMessagesContent(
            state = state,
            onOpenThread = { t ->
                viewModel.markAsRead(t.id)
                viewModel.openThread(t.id, t.senderName)
            },
            onSend = viewModel::reply,
            onSendWithAttachment = viewModel::replyWithAttachment,
            onDismissReplyError = viewModel::clearReplyError,
            onRetry = { state.openThreadId?.let { viewModel.openThread(it, state.openThreadName) } },
            onRetryThreads = viewModel::loadThreads,
            modifier = Modifier.weight(1f).fillMaxWidth(),
        )
    }
}

@Composable
private fun TeacherMessagesContent(
    state: TeacherMessageState,
    onOpenThread: (TeacherMessageThreadDto) -> Unit,
    onSend: (String) -> Unit,
    onSendWithAttachment: (String, ByteArray?, String?, String?) -> Unit,
    onDismissReplyError: () -> Unit,
    onRetry: () -> Unit,
    onRetryThreads: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        state.openThreadId != null -> {
            TeacherConversationContent(
                messages = state.messages,
                loading = state.conversationLoading,
                error = state.conversationError,
                isEmpty = state.conversationEmpty,
                sending = state.sending,
                replyError = state.replyError,
                onSend = onSend,
                onSendWithAttachment = onSendWithAttachment,
                onDismissReplyError = onDismissReplyError,
                onRetry = onRetry,
                modifier = modifier,
            )
        }
        else -> {
            TeacherThreadListContent(
                threads = state.threads,
                loading = state.loading,
                error = state.error,
                isEmpty = state.isEmpty,
                onOpenThread = onOpenThread,
                onRetry = onRetryThreads,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun TeacherThreadListContent(
    threads: List<TeacherMessageThreadDto>,
    loading: Boolean,
    error: String?,
    isEmpty: Boolean,
    onOpenThread: (TeacherMessageThreadDto) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        VStateHost(
            loading = loading,
            error = error,
            isEmpty = isEmpty,
            emptyTitle = "No messages yet",
            emptyBody = "Messages from parents and school admin will appear here.",
            emptyIcon = VIcons.Chat,
            onRetry = onRetry,
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 0.dp, vertical = 8.dp),
            ) {
                items(threads, key = { it.id }) { thread ->
                    TeacherThreadRow(
                        thread = thread,
                        onClick = { onOpenThread(thread) },
                    )
                }
            }
        }
    }
}

@Composable
private fun TeacherThreadRow(thread: TeacherMessageThreadDto, onClick: () -> Unit) {
    val c = VtC
    val interaction = remember { MutableInteractionSource() }
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            VAvatar(
                name = thread.senderName.ifBlank { "?" },
                src = thread.senderImageUrl,
                size = 52.dp,
            )
        }

        Column(Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    thread.senderName,
                    style = VtT.bodyStrong.coloredV(c.ink).copy(fontWeight = if (thread.isRead) FontWeight.SemiBold else FontWeight.Bold),
                    modifier = Modifier.weight(1f, fill = false),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    thread.time,
                    style = VtT.caption.coloredV(if (thread.isRead) c.ink3 else c.accent),
                )
            }
            Spacer(Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    thread.lastMessage,
                    style = VtT.body.coloredV(if (thread.isRead) c.ink3 else c.ink2),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                if (thread.unreadCount > 0) {
                    Box(
                        Modifier
                            .clip(CircleShape)
                            .background(c.accent)
                            .padding(horizontal = 7.dp, vertical = 2.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            if (thread.unreadCount > 99) "99+" else thread.unreadCount.toString(),
                            style = VtT.caption.coloredV(Color.White).copy(fontWeight = FontWeight.Bold),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TeacherConversationContent(
    messages: List<TeacherMessageDto>,
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
    val c = VtC
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

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    Column(modifier) {
        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(c.accentTint),
        ) {
            VStateHost(
                loading = loading,
                error = error,
                isEmpty = isEmpty,
                emptyTitle = "No messages yet",
                emptyBody = "Send a message below to start the conversation.",
                emptyIcon = VIcons.Chat,
                onRetry = onRetry,
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
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
                        .background(c.danger)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        errMsg,
                        style = VtT.caption.coloredV(c.dangerInk),
                        modifier = Modifier.weight(1f),
                    )
                    val dismissInteraction = remember { MutableInteractionSource() }
                    Box(
                        Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(c.dangerInk.copy(alpha = 0.12f))
                            .clickable(interactionSource = dismissInteraction, indication = null, onClick = {
                                onDismissReplyError()
                                pickerError = null
                            }),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            VIcons.Close,
                            contentDescription = "Dismiss",
                            tint = c.dangerInk,
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

/** Adapter: Map TeacherMessageDto to PremiumMessageData for shared bubble rendering. */
private fun TeacherMessageDto.toPremiumMessageData(): PremiumMessageData = object : PremiumMessageData {
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

/** Adapter: Map TeacherMessageAttachment to PremiumAttachmentData. */
private fun TeacherMessageAttachment.toPremiumAttachmentData(): PremiumAttachmentData = object : PremiumAttachmentData {
    override val storageUrl: String get() = this@toPremiumAttachmentData.storageUrl
    override val thumbnailUrl: String? get() = this@toPremiumAttachmentData.thumbnailUrl
    override val attachmentType: String get() = this@toPremiumAttachmentData.attachmentType
    override val fileName: String get() = this@toPremiumAttachmentData.fileName
}
