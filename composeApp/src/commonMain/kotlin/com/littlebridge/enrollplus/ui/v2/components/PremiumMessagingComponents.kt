/*
 * File: PremiumMessagingComponents.kt
 * Module: ui.v2.components
 *
 * Shared premium messaging UI components used across all three portals
 * (Admin, Teacher, Parent). Provides a WhatsApp-grade messaging experience
 * with rounded message bubbles, read-receipt ticks, image attachment support,
 * and a polished compose bar with attachment button.
 *
 * Components:
 *   - [PremiumMessageBubble]  — message row with bubble, status ticks, attachments
 *   - [PremiumDateHeader]     — date separator chip
 *   - [PremiumComposeBar]     — input bar with attach button, image preview, send button
 *   - [PremiumThreadRow]      — thread list row with avatar, name, preview, unread badge
 *   - [PremiumImagePreview]   — inline image preview strip above compose bar
 */
package com.littlebridge.enrollplus.ui.v2.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography

// ── Data interface for shared bubble ──────────────────────────────────────────

/**
 * Portal-agnostic message data for [PremiumMessageBubble].
 * Each portal maps its DTO to this interface.
 */
interface PremiumMessageData {
    val id: String
    val body: String
    val isMine: Boolean
    val time: String
    val status: String? // SENT | DELIVERED | READ
    val editedAt: String?
    val deletedAt: String?
    val attachments: List<PremiumAttachmentData>
}

/** Portal-agnostic attachment metadata. */
interface PremiumAttachmentData {
    val storageUrl: String
    val thumbnailUrl: String?
    val attachmentType: String
    val fileName: String
}

// ── Message Bubble ────────────────────────────────────────────────────────────

/**
 * Premium message bubble with WhatsApp-style rounded corners, read-receipt
 * ticks, attachment rendering, and edited/deleted states.
 *
 * @param msg the message data
 * @param isGroupStart true if this is the first message in a consecutive group
 *                     from the same sender (controls tail corner radius)
 */
@Composable
fun PremiumMessageBubble(
    msg: PremiumMessageData,
    isGroupStart: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val isMine = msg.isMine
    val isDeleted = msg.deletedAt != null

    val bubbleColor = if (isMine) VColors.violet else VColors.surfaceCard
    val textColor = if (isMine) VColors.white else VColors.ink
    val timeColor = if (isMine) VColors.white.copy(alpha = 0.7f) else VColors.ink3
    val bubbleBorder = if (isMine) null else VColors.line

    Row(
        modifier.fillMaxWidth(),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start,
    ) {
        val bubbleShape = if (isMine) {
            RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (isGroupStart) 18.dp else 4.dp,
                bottomEnd = 4.dp,
            )
        } else {
            RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = 4.dp,
                bottomEnd = if (isGroupStart) 18.dp else 4.dp,
            )
        }

        val bubbleModifier = Modifier
            .widthIn(max = 300.dp)
            .clip(bubbleShape)
            .background(bubbleColor)
            .then(
                if (bubbleBorder != null) Modifier.border(1.dp, bubbleBorder, bubbleShape) else Modifier
            )
            .padding(horizontal = 14.dp, vertical = 10.dp)

        Column(bubbleModifier) {
            // Render image attachments above text
            if (!isDeleted && msg.attachments.isNotEmpty()) {
                msg.attachments.forEach { att ->
                    if (att.attachmentType == "IMAGE") {
                        AsyncImage(
                            model = att.thumbnailUrl ?: att.storageUrl,
                            contentDescription = att.fileName,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop,
                        )
                        Spacer(Modifier.height(6.dp))
                    }
                }
            }

            if (isDeleted) {
                Text(
                    "This message was deleted",
                    style = VTypography.body.copy(fontStyle = FontStyle.Italic),
                    color = textColor,
                )
            } else if (msg.body.isNotBlank()) {
                Text(
                    msg.body,
                    style = VTypography.body,
                    color = textColor,
                )
            }

            if (isDeleted || msg.body.isNotBlank() || msg.attachments.isNotEmpty()) {
                Spacer(Modifier.height(4.dp))
            }
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (isMine && !isDeleted) {
                    PremiumReadReceipt(status = msg.status, tint = if (isMine) VColors.white.copy(alpha = 0.9f) else VColors.ink3)
                    Spacer(Modifier.size(4.dp))
                }
                Text(
                    msg.time,
                    style = VTypography.caption.copy(fontSize = 10.sp),
                    color = timeColor,
                )
                if (msg.editedAt != null && !isDeleted) {
                    Spacer(Modifier.size(4.dp))
                    Text(
                        "edited",
                        style = VTypography.caption.copy(fontSize = 9.sp),
                        color = timeColor,
                    )
                }
            }
        }
    }
}

// ── Read Receipt Ticks ────────────────────────────────────────────────────────

/**
 * Premium read-receipt ticks:
 *   SENT     → single check
 *   DELIVERED → double check (grey)
 *   READ     → double check (bright)
 */
@Composable
fun PremiumReadReceipt(
    status: String?,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    when (status?.uppercase()) {
        "READ" -> {
            Icon(
                VIcons.Check,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(14.dp),
            )
            Spacer(Modifier.size(2.dp))
            Icon(
                VIcons.Check,
                contentDescription = "Read",
                tint = tint,
                modifier = Modifier.size(14.dp),
            )
        }
        "DELIVERED" -> {
            Icon(
                VIcons.Check,
                contentDescription = "Delivered",
                tint = tint.copy(alpha = if (tint.alpha < 1f) tint.alpha else 0.7f),
                modifier = Modifier.size(14.dp),
            )
            Spacer(Modifier.size(2.dp))
            Icon(
                VIcons.Check,
                contentDescription = null,
                tint = tint.copy(alpha = if (tint.alpha < 1f) tint.alpha else 0.7f),
                modifier = Modifier.size(14.dp),
            )
        }
        "SENT" -> {
            Icon(
                VIcons.Check,
                contentDescription = "Sent",
                tint = tint.copy(alpha = if (tint.alpha < 1f) tint.alpha else 0.5f),
                modifier = Modifier.size(14.dp),
            )
        }
    }
}

// ── Date Header ───────────────────────────────────────────────────────────────

@Composable
fun PremiumDateHeader(date: String, modifier: Modifier = Modifier) {
    Box(
        modifier.fillMaxWidth().padding(vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(VColors.line)
                .padding(horizontal = 10.dp, vertical = 4.dp),
        ) {
            Text(
                date,
                style = VTypography.caption.copy(fontSize = 11.sp),
                color = VColors.ink3,
            )
        }
    }
}

// ── Thread Row ────────────────────────────────────────────────────────────────

/**
 * Premium thread list row with avatar, sender name, last message preview,
 * timestamp, and unread badge.
 */
@Composable
fun PremiumThreadRow(
    name: String,
    lastMessage: String,
    time: String,
    unreadCount: Int,
    imageUrl: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        VAvatar(name = name, size = 48.dp, src = imageUrl)

        Column(Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    name,
                    style = VTypography.body.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold),
                    color = VColors.ink,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    time,
                    style = VTypography.caption.copy(fontSize = 11.sp),
                    color = VColors.ink3,
                )
            }
            Spacer(Modifier.height(2.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    lastMessage,
                    style = VTypography.caption,
                    color = VColors.ink2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                if (unreadCount > 0) {
                    Spacer(Modifier.size(8.dp))
                    Box(
                        Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(VColors.violet),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            if (unreadCount > 99) "99+" else unreadCount.toString(),
                            style = VTypography.caption.copy(fontSize = 10.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                            color = VColors.white,
                        )
                    }
                }
            }
        }
    }
}

// ── Image Preview Strip ───────────────────────────────────────────────────────

/**
 * Inline image preview shown above the compose bar when an image is picked
 * but not yet sent. Shows a thumbnail with a remove button.
 */
@Composable
fun PremiumImagePreview(
    imageBytes: ByteArray,
    fileName: String,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box {
            AsyncImage(
                model = imageBytes,
                contentDescription = fileName,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop,
            )
            Box(
                Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(VColors.ink.copy(alpha = 0.6f))
                    .clickable(onClick = onRemove)
                    .align(Alignment.TopEnd),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    VIcons.Close,
                    contentDescription = "Remove",
                    tint = VColors.white,
                    modifier = Modifier.size(12.dp),
                )
            }
        }
        Text(
            fileName,
            style = VTypography.caption,
            color = VColors.ink2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
    }
}

// ── Compose Bar ───────────────────────────────────────────────────────────────

/**
 * Premium compose bar with:
 *   - Paperclip button for image attachment
 *   - Rounded pill text input
 *   - Circular send button with loading state
 *   - Optional image preview strip above
 *
 * @param text current input text
 * @param onTextChange callback when text changes
 * @param placeholder placeholder text for the input
 * @param enabled whether input is enabled
 * @param sending whether a send is in progress (shows spinner)
 * @param onSend callback when send button is pressed
 * @param onAttach callback when attach button is pressed
 * @param imagePreviewBytes optional byte array for image preview; null = no preview
 * @param imagePreviewName file name for the preview image
 * @param onRemoveImage callback to remove the picked image
 * @param modifier modifier for the composable
 */
@Composable
fun PremiumComposeBar(
    text: String,
    onTextChange: (String) -> Unit,
    placeholder: String,
    enabled: Boolean,
    sending: Boolean,
    onSend: () -> Unit,
    onAttach: () -> Unit,
    modifier: Modifier = Modifier,
    imagePreviewBytes: ByteArray? = null,
    imagePreviewName: String = "",
    onRemoveImage: () -> Unit = {},
) {
    val canSend = text.isNotBlank() && enabled && !sending
    val keyboard = LocalSoftwareKeyboardController.current

    Column(
        modifier
            .fillMaxWidth()
            .background(VColors.surfaceCard),
    ) {
        // Image preview strip
        if (imagePreviewBytes != null) {
            PremiumImagePreview(
                imageBytes = imagePreviewBytes,
                fileName = imagePreviewName,
                onRemove = onRemoveImage,
            )
        }

        // Divider
        Box(Modifier.fillMaxWidth().height(1.dp).background(VColors.line))

        Row(
            Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Attach button — same size as send button for visual balance
            Box(
                Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(VColors.creamDeep)
                    .clickable(enabled = enabled && !sending, onClick = onAttach),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    VIcons.Paperclip,
                    contentDescription = "Attach image",
                    tint = VColors.ink2,
                    modifier = Modifier.size(22.dp),
                )
            }

            // Text input pill — single padding layer, not double
            Box(
                Modifier.weight(1f)
                    .clip(RoundedCornerShape(22.dp))
                    .background(VColors.cream)
                    .padding(horizontal = 14.dp, vertical = 2.dp),
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
                    keyboardActions = KeyboardActions(onSend = {
                        if (canSend) {
                            onSend()
                            keyboard?.hide()
                        }
                    }),
                    textStyle = VTypography.body.copy(color = VColors.ink),
                )
            }

            // Send button
            Box(
                Modifier.size(44.dp)
                    .clip(CircleShape)
                    .background(if (canSend) VColors.violet else VColors.line)
                    .clickable(enabled = canSend, onClick = {
                        onSend()
                        keyboard?.hide()
                    }),
                contentAlignment = Alignment.Center,
            ) {
                if (sending) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp),
                    )
                } else {
                    Icon(
                        VIcons.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }
        }
    }
}
