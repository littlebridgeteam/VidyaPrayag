package com.littlebridge.enrollplus.ui.v2.screens.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentHomeworkAttachmentDto
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentHomeworkItemDto
import com.littlebridge.enrollplus.feature.parent.presentation.ParentHomeworkViewModel
import com.littlebridge.enrollplus.platform.rememberMediaPicker
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.components.VBadge
import com.littlebridge.enrollplus.ui.v2.components.VBadgeTone
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonSize
import com.littlebridge.enrollplus.ui.v2.components.VButtonTone
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VInput
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.screens.parent.PremiumOverlayHeader
import org.koin.compose.viewmodel.koinViewModel

/**
 * ParentHomeworkScreenV2 — overlay where a parent sees the child's active homework
 * and submits written text + photo attachments for each assignment.
 *
 * Wired to ParentHomeworkViewModel → /api/v1/parent/child/{childId}/homework.
 */
@Composable
fun ParentHomeworkScreenV2(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ParentHomeworkViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()

    LaunchedEffect(Unit) {
        viewModel.loadList()
    }

    Column(
        modifier
            .fillMaxSize()
            .background(VColors.cream),
    ) {
        PremiumOverlayHeader(title = "Homework", onBack = onBack)
        Box(Modifier.fillMaxSize().statusBarsPadding().imePadding().navigationBarsPadding()) {
            when {
                state.selectedHomework != null -> HomeworkSubmissionSheet(
                    homework = state.selectedHomework!!,
                    text = state.submissionText,
                    attachments = state.attachments,
                    isUploading = state.isUploadingAttachment,
                    isSubmitting = state.isSubmitting,
                    error = state.submitError ?: state.uploadError,
                    success = state.submitSuccess,
                    onTextChange = viewModel::setSubmissionText,
                    onUpload = { bytes, fileName, mimeType -> viewModel.uploadAttachment(bytes, fileName, mimeType) },
                    onRemove = viewModel::removeAttachment,
                    onSubmit = viewModel::submit,
                    onBack = { viewModel.selectHomework(null) },
                )
                else -> HomeworkListContent(
                    items = state.items,
                    isLoading = state.isLoading,
                    error = state.error,
                    onRetry = { viewModel.loadList() },
                    onSelect = viewModel::selectHomework,
                )
            }
        }
    }
}

@Composable
private fun HomeworkListContent(
    items: List<ParentHomeworkItemDto>,
    isLoading: Boolean,
    error: String?,
    onRetry: () -> Unit,
    onSelect: (ParentHomeworkItemDto) -> Unit,
) {
    VStateHost(
        loading = isLoading && items.isEmpty(),
        error = error,
        isEmpty = items.isEmpty(),
        emptyTitle = "No active homework",
        emptyBody = "Your child has no pending homework right now.",
        onRetry = onRetry,
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp).padding(top = 16.dp, bottom = 24.dp),
    ) {
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(items, key = { it.id }) { hw ->
                HomeworkListCard(hw, onClick = { onSelect(hw) })
            }
        }
    }
}

@Composable
private fun HomeworkListCard(hw: ParentHomeworkItemDto, onClick: () -> Unit) {
    val (statusLabel, statusTone) = when (hw.status) {
        "graded" -> "Graded" to VBadgeTone.Accent
        "submitted" -> "Submitted" to VBadgeTone.Success
        "late" -> "Late" to VBadgeTone.Warning
        else -> "Pending" to VBadgeTone.Danger
    }

    VCard(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(Modifier.size(40.dp).clip(CircleShape).background(VColors.mint.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                    Icon(VIcons.FileText, contentDescription = null, tint = VColors.mint, modifier = Modifier.size(20.dp))
                }
                Column(Modifier.weight(1f)) {
                    Text(hw.title, style = VTypography.body.copy(fontWeight = FontWeight.Bold, color = VColors.ink), maxLines = 1)
                    Text("${hw.subject} · Due ${hw.dueDate}", style = VTypography.caption.copy(color = VColors.ink2))
                }
                VBadge(statusLabel, tone = statusTone)
            }
            if (hw.submissionText.isNotBlank() || hw.attachments.isNotEmpty()) {
                Text("Tap to view or update submission", style = VTypography.caption.copy(color = VColors.ink3))
            }
        }
    }
}

@Composable
private fun HomeworkSubmissionSheet(
    homework: ParentHomeworkItemDto,
    text: String,
    attachments: List<ParentHomeworkAttachmentDto>,
    isUploading: Boolean,
    isSubmitting: Boolean,
    error: String?,
    success: Boolean,
    onTextChange: (String) -> Unit,
    onUpload: (ByteArray, String, String) -> Unit,
    onRemove: (ParentHomeworkAttachmentDto) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit,
) {
    val scrollState = rememberScrollState()
    var unsupportedMessage by remember { mutableStateOf<String?>(null) }
    val picker = rememberMediaPicker(
        onPicked = { bytes, mimeType, fileName -> onUpload(bytes, fileName, mimeType) },
        onUnsupported = { unsupportedMessage = it },
    )

    Column(
        Modifier.fillMaxSize().verticalScroll(scrollState).padding(horizontal = 20.dp).padding(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(34.dp).clip(RoundedCornerShape(10.dp)).background(VColors.creamDeep).clickable(enabled = !isSubmitting) { onBack() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(VIcons.ArrowLeft, contentDescription = "Back", tint = VColors.ink, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(homework.title, style = VTypography.h3.copy(color = VColors.ink, fontWeight = FontWeight.Bold), maxLines = 1)
                Text("${homework.subject} · Due ${homework.dueDate}", style = VTypography.caption.copy(color = VColors.ink2))
            }
        }

        // Instructions / description
        if (homework.description.isNotBlank()) {
            VCard(modifier = Modifier.fillMaxWidth()) {
                Text("Instructions", style = VTypography.body.copy(fontWeight = FontWeight.Bold, color = VColors.ink))
                Spacer(Modifier.height(6.dp))
                Text(homework.description, style = VTypography.body.copy(color = VColors.ink2))
            }
        }

        // Written answer
        VInput(
            value = text,
            onValueChange = onTextChange,
            label = "Written answer / notes",
            placeholder = "Type your child's answer here…",
            singleLine = false,
            modifier = Modifier.fillMaxWidth().height(140.dp),
        )

        // Attachments
        Text("Photo attachments", style = VTypography.body.copy(fontWeight = FontWeight.Bold, color = VColors.ink))
        if (attachments.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                attachments.forEach { att ->
                    AttachmentRow(att, onRemove = { onRemove(att) }, enabled = !isSubmitting)
                }
            }
        }

        // Add photo button
        VButton(
            text = if (isUploading) "Uploading…" else "Add photo",
            onClick = { picker.launchImage() },
            full = true,
            variant = VButtonVariant.Secondary,
            tone = VButtonTone.Mint,
            loading = isUploading,
            enabled = !isUploading && !isSubmitting,
            leading = { Icon(VIcons.Upload, contentDescription = null, modifier = Modifier.size(18.dp)) },
        )
        unsupportedMessage?.let {
            Text(it, style = VTypography.caption.copy(color = VColors.coral))
        }

        if (error != null) {
            Text(error, style = VTypography.caption.copy(color = VColors.coral))
        }

        if (success) {
            Text("Homework submitted successfully!", style = VTypography.caption.copy(color = VColors.success))
        }

        Spacer(Modifier.height(8.dp))
        VButton(
            text = if (isSubmitting) "Submitting…" else "Submit homework",
            onClick = onSubmit,
            full = true,
            tone = VButtonTone.Mint,
            loading = isSubmitting,
            enabled = !isSubmitting && !isUploading,
        )
    }
}

@Composable
private fun AttachmentRow(
    attachment: ParentHomeworkAttachmentDto,
    onRemove: () -> Unit,
    enabled: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(VColors.creamDeep).border(1.dp, VColors.line, RoundedCornerShape(12.dp)).padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(Modifier.size(56.dp).clip(RoundedCornerShape(8.dp)).background(VColors.surfaceCard)) {
            if (attachment.url.isNotBlank()) {
                AsyncImage(
                    model = attachment.url,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                Icon(VIcons.Upload, contentDescription = null, tint = VColors.ink3, modifier = Modifier.size(24.dp).align(Alignment.Center))
            }
        }
        Column(Modifier.weight(1f)) {
            Text(attachment.filename.ifBlank { "Attachment" }, style = VTypography.body.copy(fontWeight = FontWeight.SemiBold, color = VColors.ink), maxLines = 1)
            Text(attachment.mime, style = VTypography.caption.copy(color = VColors.ink2))
        }
        VButton(
            text = "Remove",
            onClick = onRemove,
            variant = VButtonVariant.Ghost,
            size = VButtonSize.Sm,
            enabled = enabled,
        )
    }
}
