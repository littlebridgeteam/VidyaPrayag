package com.littlebridge.enrollplus.ui.v2.screens.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.feature.reportcard.domain.model.ReportCardModels
import com.littlebridge.enrollplus.feature.reportcard.presentation.TeacherReportReviewViewModel
import com.littlebridge.enrollplus.ui.v2.components.VBadge
import com.littlebridge.enrollplus.ui.v2.components.VBadgeTone
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonSize
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.screens.teacher.TeacherSpinner

/**
 * TeacherReportReviewQueueScreen — shows AI-generated report card drafts
 * for a class/term, allowing teachers to review, edit, approve, or regenerate.
 */
@Composable
fun TeacherReportReviewQueueScreen(
    className: String,
    section: String,
    term: String,
    onBack: () -> Unit,
    onEditDraft: (String) -> Unit,
    viewModel: TeacherReportReviewViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    val c = VtC

    LaunchedEffect(className, section, term) {
        viewModel.loadReviewQueue(className, section, term)
    }

    Column(
        Modifier.fillMaxSize().background(c.background)
            .statusBarsPadding().navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        VBackHeader(title = appString(StringKeys.TC_REPORT_CARD_REVIEW), onBack = onBack)

        // Context line
        Text(
            "$className $section • $term",
            style = VtT.caption.coloredV(c.ink2),
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        // Summary bar
        if (state.drafts.isNotEmpty()) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SummaryChip(appString(StringKeys.TC_TOTAL), state.drafts.size, c.accent)
                SummaryChip(appString(StringKeys.TC_DRAFT), state.drafts.count { it.status == "draft" }, c.warning)
                SummaryChip(appString(StringKeys.TC_FLAGGED), state.drafts.count { it.status == "flagged_for_review" }, c.danger)
                SummaryChip(appString(StringKeys.SW_APPROVED), state.drafts.count { it.status == "approved" }, c.success)
            }
        }

        state.message?.let { msg ->
            Box(Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                VCard(Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(VIcons.Check, contentDescription = null, tint = c.success, modifier = Modifier.size(16.dp))
                        Text(msg, style = VtT.body.coloredV(c.ink))
                    }
                }
            }
            LaunchedEffect(msg) { kotlinx.coroutines.delay(3000); viewModel.clearMessage() }
        }

        when {
            state.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    TeacherSpinner()
                }
            }
            state.error != null -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(state.error ?: "", style = VtT.body.coloredV(c.danger))
                        Spacer(Modifier.height(8.dp))
                        VButton(text = appString(StringKeys.COMMON_BUTTON_RETRY), onClick = { viewModel.loadReviewQueue(className, section, term) })
                    }
                }
            }
            state.isEmpty -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(VIcons.ClipboardList, contentDescription = null, tint = c.ink2, modifier = Modifier.size(40.dp))
                        Spacer(Modifier.height(12.dp))
                        Text(appString(StringKeys.TC_NO_DRAFTS_FOUND), style = VtT.body.coloredV(c.ink2))
                        if (className.isBlank()) {
                            Spacer(Modifier.height(4.dp))
                            Text("Select a class to view report card drafts", style = VtT.caption.coloredV(c.ink3))
                        }
                    }
                }
            }
            else -> {
                LazyColumn(
                    Modifier.fillMaxSize().padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(state.drafts) { draft ->
                        DraftReviewCard(
                            draft = draft,
                            approving = state.approvingId == draft.id,
                            regenerating = state.regeneratingId == draft.id,
                            onApprove = { viewModel.approveDraft(draft.id) },
                            onRegenerate = { viewModel.regenerateDraft(draft.id) },
                            onEdit = { onEditDraft(draft.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryChip(label: String, count: Int, color: androidx.compose.ui.graphics.Color) {
    val c = VtC
    Column(
        Modifier.clip(RoundedCornerShape(8.dp)).background(color.copy(alpha = 0.1f)).padding(horizontal = 10.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("$count", style = VtT.h3.coloredV(color).copy(fontSize = 16.sp))
        Text(label, style = VtT.caption.coloredV(c.ink3).copy(fontSize = 10.sp))
    }
}

@Composable
private fun DraftReviewCard(
    draft: ReportCardModels.DraftDto,
    approving: Boolean,
    regenerating: Boolean,
    onApprove: () -> Unit,
    onRegenerate: () -> Unit,
    onEdit: () -> Unit,
) {
    val c = VtC
    val statusTone = when (draft.status) {
        "draft" -> VBadgeTone.Neutral
        "flagged_for_review" -> VBadgeTone.Warning
        "approved" -> VBadgeTone.Success
        "published" -> VBadgeTone.Arctic
        else -> VBadgeTone.Neutral
    }
    val statusLabel = when (draft.status) {
        "draft" -> appString(StringKeys.TC_DRAFT)
        "flagged_for_review" -> appString(StringKeys.TC_FLAGGED)
        "approved" -> appString(StringKeys.SW_APPROVED)
        "published" -> appString(StringKeys.TC_PUBLISHED)
        else -> draft.status
    }

    VCard(Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(appString(StringKeys.TC_STUDENT_COLON, "id" to draft.studentId.take(8)), style = VtT.body.coloredV(c.ink).copy(fontWeight = FontWeight.Medium))
                VBadge(text = statusLabel, tone = statusTone)
            }

            if (draft.groundingFlags != null) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(VIcons.AlertCircle, contentDescription = null, tint = c.warning, modifier = Modifier.size(12.dp))
                    Text(appString(StringKeys.TC_GROUNDING_FLAGS_DETECTED), style = VtT.caption.coloredV(c.warning).copy(fontSize = 11.sp))
                }
            }

            val aiDraft = draft.aiDraft
            if (aiDraft != null) {
                val preview = aiDraft.take(120) + if (aiDraft.length > 120) "…" else ""
                Text(preview, style = VtT.body.coloredV(c.ink2).copy(fontSize = 12.sp), maxLines = 3)
            }

            if (draft.status == "draft" || draft.status == "flagged_for_review") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    VButton(text = appString(StringKeys.COMMON_BUTTON_EDIT), onClick = onEdit, variant = VButtonVariant.Secondary, size = VButtonSize.Sm)
                    VButton(
                        text = if (approving) "…" else appString(StringKeys.TC_APPROVE),
                        onClick = onApprove,
                        variant = VButtonVariant.Primary,
                        size = VButtonSize.Sm,
                        enabled = !approving,
                    )
                    VButton(
                        text = if (regenerating) "…" else appString(StringKeys.TC_REGENERATE),
                        onClick = onRegenerate,
                        variant = VButtonVariant.Secondary,
                        size = VButtonSize.Sm,
                        enabled = !regenerating,
                    )
                }
            }
        }
    }
}
