/*
 * File: PewsStudentDetailScreenV2.kt
 * Module: ui.v2.screens.school
 *
 * The school-admin PEWS student detail — one at-risk student's current snapshot
 * (GET /api/v1/school/pews/student/{code}): the deterministic metrics + signal
 * bundle, the (nullable) AI explanation, the open interventions, and a short
 * history. Lets the admin mark an intervention done / dismissed and record an
 * outcome (the LEARN loop).
 *
 * Honesty (RA-S10 / LAW 6): metrics and signals are deterministic and always
 * shown; the AI cause/recommendation render only when the server provides them.
 */
package com.littlebridge.enrollplus.ui.v2.screens.school

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.littlebridge.enrollplus.feature.pews.domain.model.ParentDraftDto
import com.littlebridge.enrollplus.feature.pews.domain.model.PewsInterventionDto
import com.littlebridge.enrollplus.feature.pews.domain.model.PewsStudentDto
import com.littlebridge.enrollplus.feature.pews.presentation.PewsStudentDetailState
import com.littlebridge.enrollplus.feature.pews.presentation.PewsStudentDetailViewModel
import com.littlebridge.enrollplus.ui.v2.components.VBadge
import com.littlebridge.enrollplus.ui.v2.components.VBadgeTone
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonSize
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.SkeletonProfile
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.locale.appString
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PewsStudentDetailScreenV2(
    studentCode: String,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: PewsStudentDetailViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()

    LaunchedEffect(studentCode) { viewModel.load(studentCode) }

    Column(modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()) {
        VBackHeader(title = appString(StringKeys.PEWS_STUDENT_SIGNAL), onBack = onBack)
        PewsStudentDetailContent(
            state = state,
            onRetry = { viewModel.load(studentCode) },
            onStart = { id -> viewModel.updateIntervention(id, status = "in_progress") },
            onMarkDone = { id, outcome -> viewModel.updateIntervention(id, status = "done", outcome = outcome) },
            onDismiss = { id -> viewModel.updateIntervention(id, status = "dismissed") },
            onGenerateDraft = { id, lang -> viewModel.generateParentDraft(id, lang) },
            onSendParentMessage = viewModel::sendParentMessage,
            onClearDraft = viewModel::clearDraft,
            onClearMessage = viewModel::clearMessages,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun PewsStudentDetailContent(
    state: PewsStudentDetailState,
    onRetry: () -> Unit,
    onStart: (String) -> Unit,
    onMarkDone: (String, String) -> Unit,
    onDismiss: (String) -> Unit,
    onGenerateDraft: (String, String) -> Unit,
    onSendParentMessage: (String) -> Unit,
    onClearDraft: (String) -> Unit,
    onClearMessage: () -> Unit,
    modifier: Modifier = Modifier,
) {
        VStateHost(
        loading = state.isLoading,
        error = state.error,
        isEmpty = state.isEmpty,
        emptyIcon = VIcons.ShieldCheck,
        emptyTitle = appString(StringKeys.PEWS_NO_SIGNAL),
        emptyBody = appString(StringKeys.PEWS_NO_SIGNAL_DESC),
        onRetry = onRetry,
        modifier = modifier,
        skeleton = { SkeletonProfile() },
    ) {
        val detail = state.detail ?: return@VStateHost
        val cur = detail.current ?: return@VStateHost
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            HeaderCard(cur)
            MetricsCard(cur)
            if (cur.signals.isNotEmpty()) SignalsCard(cur)
            AiExplanationCard(cur)
            if (state.interventions.isNotEmpty()) {
                Text(
                    appString(StringKeys.PEWS_INTERVENTIONS),
                    style = VTypography.label.copy(color = VColors.ink3).copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                )
                state.interventions.forEach { iv ->
                    InterventionCard(
                        iv = iv,
                        isUpdating = iv.id in state.updatingIds,
                        parentDrafts = state.parentDrafts,
                        draftLoadingIds = state.draftLoadingIds,
                        onStart = onStart,
                        onMarkDone = onMarkDone,
                        onDismiss = onDismiss,
                        onGenerateDraft = onGenerateDraft,
                        onSendParentMessage = onSendParentMessage,
                        onClearDraft = onClearDraft,
                    )
                }
            }
            if (detail.history.size > 1) HistoryCard(detail.history)
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun HeaderCard(s: PewsStudentDto) {
        val (tone, levelLabel) = when (s.riskLevel) {
        "high" -> VBadgeTone.Danger to appString(StringKeys.PEWS_HIGH_RISK)
        "medium" -> VBadgeTone.Warning to appString(StringKeys.PEWS_MEDIUM_RISK)
        else -> VBadgeTone.Success to appString(StringKeys.PEWS_WATCH)
    }
    VCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(s.name, style = VTypography.h3.copy(color = VColors.ink))
                Spacer(Modifier.height(2.dp))
                Text(
                    "Class ${s.className}${if (s.section.isNotBlank()) "-${s.section}" else ""}",
                    style = VTypography.caption.copy(color = VColors.ink3),
                )
            }
            VBadge(text = levelLabel, tone = tone)
            if (s.hasOpenIntervention) {
                Spacer(Modifier.width(4.dp))
                VBadge(text = appString(StringKeys.PEWS_UNDER_INTERVENTION), tone = VBadgeTone.Neutral)
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            appString(StringKeys.PEWS_RISK_SCORE, "score" to s.riskScore, "date" to s.runDate),
            style = VTypography.caption.copy(color = VColors.ink3).copy(fontSize = 12.sp),
        )
    }
}

@Composable
private fun MetricsCard(s: PewsStudentDto) {
        VCard {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Metric(appString(StringKeys.PEWS_ATTENDANCE), s.attendancePct?.let { "$it%" } ?: "—", s.attendanceSlope, Modifier.weight(1f))
            Metric(appString(StringKeys.PEWS_MARKS), s.marksPct?.let { "$it%" } ?: "—", s.marksSlope, Modifier.weight(1f))
            Metric(appString(StringKeys.PEWS_LEAVES), "${s.leaveCount}", null, Modifier.weight(1f))
        }
    }
}

@Composable
private fun Metric(label: String, value: String, slope: Double?, modifier: Modifier = Modifier) {
        Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = VTypography.bodySmall.copy(color = VColors.ink).copy(fontWeight = FontWeight.Bold))
        Spacer(Modifier.height(2.dp))
        Text(label, style = VTypography.caption.copy(color = VColors.ink3).copy(fontSize = 11.sp))
        if (slope != null && slope != 0.0) {
            Spacer(Modifier.height(4.dp))
            val falling = slope < 0
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                Icon(
                    if (falling) VIcons.TrendingDown else VIcons.TrendingUp,
                    contentDescription = null,
                    tint = if (falling) VColors.error else VColors.success,
                    modifier = Modifier.size(13.dp),
                )
                Text(
                    if (falling) appString(StringKeys.PEWS_FALLING) else appString(StringKeys.PEWS_RISING),
                    style = VTypography.caption.copy(color = if (falling) VColors.error else VColors.success).copy(fontSize = 10.sp),
                )
            }
        }
    }
}

@Composable
private fun SignalsCard(s: PewsStudentDto) {
        VCard {
        Text(appString(StringKeys.PEWS_WHY_STUDENT), style = VTypography.label.copy(color = VColors.ink3).copy(fontWeight = FontWeight.Bold, fontSize = 11.sp))
        Spacer(Modifier.height(10.dp))
        s.signals.forEach { sig ->
            Row(
                Modifier.fillMaxWidth().padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val dot = when {
                    sig.severity >= 3 -> VColors.error
                    sig.severity == 2 -> VColors.gold
                    else -> VColors.success
                }
                Box(Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(dot))
                Text(sig.label, style = VTypography.body.copy(color = VColors.ink2).copy(fontSize = 13.sp), modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun AiExplanationCard(s: PewsStudentDto) {
        val cause = s.aiCause
    val rec = s.aiRecommendation
    val narrative = s.aiNarrative
    if (cause.isNullOrBlank() && rec.isNullOrBlank() && narrative.isNullOrBlank()) return
    VCard(background = VColors.sky.copy(alpha = 0.08f)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(VIcons.Sparkles, contentDescription = null, tint = VColors.sky, modifier = Modifier.size(14.dp))
            Text(
                appString(StringKeys.PEWS_AI_EXPLANATION),
                style = VTypography.label.copy(color = VColors.sky).copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
            )
        }
        if (!narrative.isNullOrBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(narrative, style = VTypography.body.copy(color = VColors.ink).copy(fontSize = 13.sp, lineHeight = 19.sp))
        }
        if (!cause.isNullOrBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(appString(StringKeys.PEWS_LIKELY_CAUSE), style = VTypography.caption.copy(color = VColors.sky).copy(fontWeight = FontWeight.SemiBold, fontSize = 11.sp))
            Text(cause, style = VTypography.body.copy(color = VColors.ink2).copy(fontSize = 13.sp, lineHeight = 19.sp))
        }
        if (!rec.isNullOrBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(appString(StringKeys.PEWS_SUGGESTED_ACTION), style = VTypography.caption.copy(color = VColors.sky).copy(fontWeight = FontWeight.SemiBold, fontSize = 11.sp))
            Text(rec, style = VTypography.body.copy(color = VColors.ink2).copy(fontSize = 13.sp, lineHeight = 19.sp))
        }
        s.aiProviderUsed?.let {
            Spacer(Modifier.height(8.dp))
            Text(appString(StringKeys.PEWS_GENERATED_BY, "provider" to it), style = VTypography.caption.copy(color = VColors.ink3).copy(fontSize = 10.sp))
        }
    }
}

@Composable
private fun InterventionCard(
    iv: PewsInterventionDto,
    isUpdating: Boolean,
    parentDrafts: Map<String, ParentDraftDto>,
    draftLoadingIds: Set<String>,
    onStart: (String) -> Unit,
    onMarkDone: (String, String) -> Unit,
    onDismiss: (String) -> Unit,
    onGenerateDraft: (String, String) -> Unit,
    onSendParentMessage: (String) -> Unit,
    onClearDraft: (String) -> Unit,
) {
        val statusTone = when (iv.status) {
        "done" -> VBadgeTone.Success
        "dismissed" -> VBadgeTone.Neutral
        "in_progress" -> VBadgeTone.Accent
        else -> VBadgeTone.Warning
    }
    VCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(iv.actionType.replace('_', ' '), style = VTypography.bodySmall.copy(fontWeight = FontWeight.SemiBold).copy(color = VColors.ink), modifier = Modifier.weight(1f))
            VBadge(text = iv.status.replace('_', ' '), tone = statusTone)
        }

        // Urgency + escalation indicators
        Row(
            Modifier.fillMaxWidth().padding(top = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            iv.urgency?.let { urg ->
                val urgTone = when (urg) {
                    "high" -> VBadgeTone.Danger
                    "medium" -> VBadgeTone.Warning
                    else -> VBadgeTone.Neutral
                }
                VBadge(text = urg.replaceFirstChar { it.uppercase() }, tone = urgTone)
            }
            if (iv.escalationLevel > 0) {
                val escLabel = when (iv.escalationLevel) {
                    2 -> appString(StringKeys.PEWS_ESCALATED)
                    else -> appString(StringKeys.PEWS_REMINDED)
                }
                val escTone = if (iv.escalationLevel >= 2) VBadgeTone.Danger else VBadgeTone.Warning
                VBadge(text = escLabel, tone = escTone)
            }
            iv.causeFamily?.let { cf ->
                VBadge(text = cf, tone = VBadgeTone.Neutral)
            }
        }

        // SLA countdown
        iv.slaDays?.let { sla ->
            Spacer(Modifier.height(6.dp))
            val slaText = if (iv.followUpDate != null) {
                appString(StringKeys.PEWS_SLA_FOLLOWUP, "days" to sla, "date" to iv.followUpDate)
            } else {
                appString(StringKeys.PEWS_SLA_DAYS, "days" to sla)
            }
            Text(
                slaText,
                style = VTypography.caption.copy(color = VColors.ink3).copy(fontSize = 11.sp),
            )
        }

        val notes = iv.notes
        if (!notes.isNullOrBlank()) {
            Spacer(Modifier.height(6.dp))
            Text(notes, style = VTypography.caption.copy(color = VColors.ink2).copy(fontSize = 12.sp, lineHeight = 17.sp))
        }

        // Plan steps from plan_json
        iv.planJson?.let { planJson ->
            val steps = parsePlanSteps(planJson)
            if (steps.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    appString(StringKeys.PEWS_PLAN),
                    style = VTypography.label.copy(color = VColors.ink3).copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                )
                Spacer(Modifier.height(4.dp))
                steps.forEachIndexed { i, step ->
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 2.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            "${i + 1}.",
                            style = VTypography.caption.copy(color = VColors.ink3).copy(fontSize = 12.sp),
                        )
                        Text(
                            step,
                            style = VTypography.caption.copy(color = VColors.ink2).copy(fontSize = 12.sp, lineHeight = 17.sp),
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }

        // Parent draft — prefer API-generated from parentDrafts, fall back to pre-generated from CaseFile
        val draftBody = parentDrafts[iv.id]?.body ?: iv.parentDraftBody
        val draftLang = parentDrafts[iv.id]?.language ?: iv.parentDraftLang
        val isParentAction = iv.actionType.contains("parent") || iv.actionType.contains("message") || iv.actionType.contains("call") || iv.actionType.contains("visit")
        val hasDraft = !draftBody.isNullOrBlank()

        if (hasDraft && draftBody != null) {
            Spacer(Modifier.height(8.dp))
            Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(VColors.sky.copy(alpha = 0.1f)).padding(8.dp)) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(VIcons.Sparkles, contentDescription = null, tint = VColors.sky, modifier = Modifier.size(12.dp))
                        Spacer(Modifier.size(4.dp))
                        Text(
                            appString(StringKeys.PEWS_PARENT_MESSAGE, "lang" to (draftLang?.uppercase() ?: "EN")),
                            style = VTypography.label.copy(color = VColors.sky).copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                            modifier = Modifier.weight(1f),
                        )
                        if (parentDrafts[iv.id] != null) {
                            VButton("✕", { onClearDraft(iv.id) }, variant = VButtonVariant.Ghost, size = VButtonSize.Sm)
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(draftBody, style = VTypography.body.copy(color = VColors.ink).copy(fontSize = 12.sp, lineHeight = 17.sp))
                }
            }
        }

        Spacer(Modifier.height(4.dp))
        Text(appString(StringKeys.PEWS_OPENED, "date" to iv.openedAt), style = VTypography.caption.copy(color = VColors.ink3).copy(fontSize = 11.sp))

        val open = iv.status == "open" || iv.status == "in_progress"
        val outcome = iv.outcome
        val isDraftLoading = iv.id in draftLoadingIds
        if (open) {
            Spacer(Modifier.height(10.dp))
            if (iv.status == "open") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    VButton(
                        text = appString(StringKeys.PEWS_START),
                        onClick = { onStart(iv.id) },
                        variant = VButtonVariant.Primary,
                        size = VButtonSize.Sm,
                        enabled = !isUpdating,
                    )
                    VButton(
                        text = appString(StringKeys.PEWS_DISMISS),
                        onClick = { onDismiss(iv.id) },
                        variant = VButtonVariant.Ghost,
                        size = VButtonSize.Sm,
                        enabled = !isUpdating,
                    )
                }
            } else {
                // In-progress: show who initiated it
                val initiatorLabel = iv.initiatedByName?.let { name ->
                    val role = iv.initiatedByRole?.let { r ->
                        if (r in listOf("school_admin", "admin")) appString(StringKeys.PEWS_ADMIN) else appString(StringKeys.PEWS_TEACHER)
                    } ?: ""
                    appString(StringKeys.PEWS_INITIATED_BY, "name" to name, "role" to role)
                }
                if (initiatorLabel != null) {
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(VIcons.Check, contentDescription = null, tint = VColors.success, modifier = Modifier.size(13.dp))
                        Text(initiatorLabel, style = VTypography.caption.copy(color = VColors.ink2).copy(fontSize = 11.sp))
                    }
                }
                // action-type-specific
                if (isParentAction) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (hasDraft) {
                            VButton(
                                text = appString(StringKeys.PEWS_SEND_TO_PARENT),
                                onClick = { onSendParentMessage(iv.id) },
                                variant = VButtonVariant.Primary,
                                size = VButtonSize.Sm,
                                enabled = !isUpdating,
                            )
                        } else {
                            var draftLang by remember { mutableStateOf("en") }
                            var langDropdownOpen by remember { mutableStateOf(false) }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                VButton(
                                    text = appString(StringKeys.PEWS_DRAFT_PARENT_MSG),
                                    onClick = { onGenerateDraft(iv.id, draftLang) },
                                    variant = VButtonVariant.Secondary,
                                    size = VButtonSize.Sm,
                                    enabled = !isDraftLoading,
                                )
                                Box {
                                    VButton(
                                        text = draftLang.uppercase(),
                                        onClick = { langDropdownOpen = true },
                                        variant = VButtonVariant.Ghost,
                                        size = VButtonSize.Sm,
                                    )
                                    DropdownMenu(
                                        expanded = langDropdownOpen,
                                        onDismissRequest = { langDropdownOpen = false },
                                        containerColor = VColors.surfaceCard,
                                    ) {
                                        listOf("en" to "English", "hi" to "हिन्दी", "mr" to "मराठी", "ta" to "தமிழ்", "te" to "తెలుగు", "bn" to "বাংলা").forEach { (code, label) ->
                                            DropdownMenuItem(
                                                text = { Text(label, style = VTypography.body.copy(color = VColors.ink)) },
                                                onClick = {
                                                    draftLang = code
                                                    langDropdownOpen = false
                                                },
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        VButton(
                            text = appString(StringKeys.PEWS_DISMISS),
                            onClick = { onDismiss(iv.id) },
                            variant = VButtonVariant.Ghost,
                            size = VButtonSize.Sm,
                            enabled = !isUpdating,
                        )
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        VButton(
                            text = appString(StringKeys.PEWS_MARK_IMPROVED),
                            onClick = { onMarkDone(iv.id, "improved") },
                            variant = VButtonVariant.Primary,
                            size = VButtonSize.Sm,
                            enabled = !isUpdating,
                        )
                        VButton(
                            text = appString(StringKeys.PEWS_NO_CHANGE),
                            onClick = { onMarkDone(iv.id, "unchanged") },
                            variant = VButtonVariant.Secondary,
                            size = VButtonSize.Sm,
                            enabled = !isUpdating,
                        )
                        VButton(
                            text = appString(StringKeys.PEWS_DISMISS),
                            onClick = { onDismiss(iv.id) },
                            variant = VButtonVariant.Ghost,
                            size = VButtonSize.Sm,
                            enabled = !isUpdating,
                        )
                    }
                }
            }
        } else if (!outcome.isNullOrBlank()) {
            Spacer(Modifier.height(6.dp))
            Text(appString(StringKeys.PEWS_OUTCOME, "outcome" to outcome), style = VTypography.caption.copy(color = VColors.ink2).copy(fontSize = 12.sp))
        }
    }
}

/** Parse plan_json to extract step descriptions. */
private fun parsePlanSteps(planJson: String): List<String> {
    return runCatching {
        val json = Json { ignoreUnknownKeys = true }
        val obj = json.parseToJsonElement(planJson)
        val steps = obj.jsonObject["steps"]?.jsonArray
            ?: obj.jsonObject["plan"]?.jsonArray
            ?: return emptyList()
        steps.mapNotNull { step ->
            when (step) {
                is JsonObject -> step["description"]?.jsonPrimitive?.contentOrNull
                    ?: step["action"]?.jsonPrimitive?.contentOrNull
                    ?: step["text"]?.jsonPrimitive?.contentOrNull
                else -> step.jsonPrimitive.contentOrNull
            }
        }
    }.getOrDefault(emptyList())
}

@Composable
private fun HistoryCard(history: List<PewsStudentDto>) {
        VCard {
        Text(appString(StringKeys.PEWS_HISTORY), style = VTypography.label.copy(color = VColors.ink3).copy(fontWeight = FontWeight.Bold, fontSize = 11.sp))
        Spacer(Modifier.height(8.dp))
        history.take(8).forEach { h ->
            Row(
                Modifier.fillMaxWidth().padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(h.runDate, style = VTypography.caption.copy(color = VColors.ink3).copy(fontSize = 12.sp), modifier = Modifier.weight(1f))
                val tone = when (h.riskLevel) {
                    "high" -> VColors.error
                    "medium" -> VColors.gold
                    else -> VColors.success
                }
                Text(
                    "${h.riskLevel} · ${h.riskScore}",
                    style = VTypography.caption.copy(color = tone).copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold),
                )
            }
        }
    }
}
