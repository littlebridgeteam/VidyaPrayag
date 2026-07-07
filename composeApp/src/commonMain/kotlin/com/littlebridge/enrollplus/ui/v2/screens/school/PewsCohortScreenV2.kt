/*
 * File: PewsCohortScreenV2.kt
 * Module: ui.v2.screens.school
 *
 * The REAL school-admin PEWS cohort screen (the live at-risk roster). This is
 * the screen behind the PewsPreview teaser — it shows actual deterministic
 * snapshots served by GET /api/v1/school/pews/cohort, with band counts, a band
 * filter, a manual recompute, and per-student rows that open the detail screen.
 *
 * Honesty (RA-S10 / LAW 6): every row is a real student snapshot. Risk levels,
 * scores and signals are deterministic; the optional AI line is shown only when
 * the server provides it. The screen never invents a student or a number.
 */
package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.feature.pews.domain.model.PewsCohortDto
import com.littlebridge.enrollplus.feature.pews.domain.model.PewsConfigDto
import com.littlebridge.enrollplus.feature.pews.domain.model.PewsEffectivenessDto
import com.littlebridge.enrollplus.feature.pews.domain.model.PewsEffectivenessTrendDto
import com.littlebridge.enrollplus.feature.pews.domain.model.PewsInterventionDto
import com.littlebridge.enrollplus.feature.pews.domain.model.PewsStudentDto
import com.littlebridge.enrollplus.feature.pews.domain.model.PewsTrendPointDto
import com.littlebridge.enrollplus.feature.pews.presentation.PewsCohortState
import com.littlebridge.enrollplus.feature.pews.presentation.PewsCohortViewModel
import com.littlebridge.enrollplus.ui.v2.components.VBadge
import com.littlebridge.enrollplus.ui.v2.components.VBadgeTone
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonSize
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

/**
 * PewsCohortScreenV2 — the admin's live at-risk roster.
 *
 * Wired to [PewsCohortViewModel] (`GET /api/v1/school/pews/cohort`, school-scoped).
 * Tapping a row opens that student's PEWS detail. Three states via [VStateHost].
 */
@Composable
fun PewsCohortScreenV2(
    onBack: () -> Unit = {},
    onOpenStudent: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: PewsCohortViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()

    Column(modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()) {
        VBackHeader(
            title = appString(StringKeys.SCH_EARLY_WARNING),
            onBack = onBack,
            action = {
                VButton(
                    text = if (state.isRunning) "…" else appString(StringKeys.SCH_RECOMPUTE),
                    onClick = viewModel::runNow,
                    variant = VButtonVariant.Secondary,
                    size = VButtonSize.Sm,
                    enabled = !state.isRunning && !state.isLoading,
                )
            },
        )
        PewsCohortContent(
            state = state,
            onRetry = viewModel::load,
            onSetMinLevel = viewModel::setMinLevel,
            onOpenStudent = onOpenStudent,
            onSaveConfig = viewModel::saveConfig,
            onPollJob = viewModel::pollJobStatus,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun PewsCohortContent(
    state: PewsCohortState,
    onRetry: () -> Unit,
    onSetMinLevel: (String) -> Unit,
    onOpenStudent: (String) -> Unit,
    onSaveConfig: (PewsConfigDto) -> Unit,
    onPollJob: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
        // The screen is only "empty" (full-screen empty state) when there is NOTHING
    // to manage at all — no cohort AND no config loaded. When the cohort has no
    // at-risk students but config/effectiveness exist, we must still render the
    // list so the admin can reach the Config card (incl. the "Share with parents"
    // toggle) and Effectiveness — otherwise a school with no current risk could
    // never enable parent sharing or change thresholds.
    val nothingToShow = state.isEmpty && state.config == null && state.effectiveness == null
    VStateHost(
        loading = state.isLoading,
        error = state.error,
        isEmpty = nothingToShow,
        emptyIcon = VIcons.ShieldCheck,
        emptyTitle = appString(StringKeys.SCH_NO_STUDENTS_ATTENTION),
        emptyBody = appString(StringKeys.SCH_NO_STUDENTS_ATTENTION_DESC),
        onRetry = onRetry,
        modifier = modifier,
    ) {
        val cohort = state.cohort
        val students = cohort?.students.orEmpty()
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (cohort != null) {
                item { RiskBandSummary(cohort) }
            }
            // Async job status indicator
            if (state.isRunning && state.jobStatus != null) {
                item { JobStatusCard(status = state.jobStatus ?: "", jobId = state.jobId, onPoll = onPollJob) }
            }
            // Cohort trend chart
            state.trend?.let { trend ->
                if (trend.points.size > 1) {
                    item { TrendCard(trend.points) }
                }
            }
            item {
                BandFilterRow(selected = state.minLevel, onSelect = onSetMinLevel)
            }
            if (cohort != null && !cohort.aiEnabled) {
                item { AiDisabledNote() }
            }
            if (students.isEmpty()) {
                // Inline "all on track" note so config/effectiveness stay reachable.
                item { AllOnTrackNote() }
            } else {
                items(students, key = { it.studentCode }) { s ->
                    PewsStudentRow(s, onClick = { onOpenStudent(s.studentCode) })
                }
            }
            // ── Effectiveness rollup (LEARN loop) — admin parity with the web portal
            state.effectiveness?.let { eff ->
                if (eff.total > 0) {
                    item { EffectivenessCard(eff) }
                }
            }
            // ── Config (thresholds, run frequency, AI + parent-share toggles) ──────
            state.config?.let { cfg ->
                item {
                    ConfigCard(
                        config = cfg,
                        isSaving = state.isSavingConfig,
                        onSave = onSaveConfig,
                    )
                }
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

/** Effectiveness — what the intervention loop is achieving (the LEARN stage). */
@Composable
private fun EffectivenessCard(eff: PewsEffectivenessDto) {
        val resolved = eff.done + eff.dismissed
    val outcomeTotal = eff.improved + eff.unchanged + eff.worsened
    val improvedPct = if (outcomeTotal > 0) (eff.improved * 100) / outcomeTotal else 0
    VCard {
        Text(
            appString(StringKeys.SCH_EFFECTIVENESS_HEADER),
            style = VTypography.label.copy(color = VColors.ink3).copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            appString(StringKeys.SCH_EFFECTIVENESS_LOOP_DESC),
            style = VTypography.caption.copy(color = VColors.ink3).copy(fontSize = 12.sp),
        )
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            EffStat(appString(StringKeys.SCH_OPEN), "${eff.open}", VColors.ink, Modifier.weight(1f))
            EffStat(appString(StringKeys.SCH_RESOLVED), "$resolved", VColors.ink, Modifier.weight(1f))
            EffStat(appString(StringKeys.SCH_IMPROVED), if (outcomeTotal > 0) "$improvedPct%" else "—", VColors.success, Modifier.weight(1f))
        }
        if (outcomeTotal > 0) {
            Spacer(Modifier.height(12.dp))
            OutcomeBar(appString(StringKeys.SCH_IMPROVED), eff.improved, eff.total, VColors.success)
            Spacer(Modifier.height(6.dp))
            OutcomeBar(appString(StringKeys.SCH_NO_CHANGE), eff.unchanged, eff.total, VColors.ink3.copy(alpha = 0.5f))
            Spacer(Modifier.height(6.dp))
            OutcomeBar(appString(StringKeys.SCH_WORSENED), eff.worsened, eff.total, VColors.error)
        }
    }
}

@Composable
private fun EffStat(
    label: String,
    value: String,
    fg: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
) {
        Column(
        modifier.clip(RoundedCornerShape(10.dp)).background(VColors.cream).padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(value, style = VTypography.body.copy(fontWeight = FontWeight.SemiBold, fontSize = 22.sp).copy(color = fg).copy(fontWeight = FontWeight.Bold, fontSize = 18.sp))
        Spacer(Modifier.height(2.dp))
        Text(label, style = VTypography.caption.copy(color = VColors.ink3).copy(fontSize = 11.sp))
    }
}

@Composable
private fun OutcomeBar(
    label: String,
    value: Int,
    total: Int,
    fill: androidx.compose.ui.graphics.Color,
) {
        val frac = if (total > 0) value.toFloat() / total.toFloat() else 0f
    Column(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = VTypography.caption.copy(color = VColors.ink2).copy(fontSize = 12.sp))
            Text("$value", style = VTypography.caption.copy(color = VColors.ink).copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold))
        }
        Spacer(Modifier.height(4.dp))
        Box(
            Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(999.dp)).background(VColors.cream),
        ) {
            Box(
                Modifier.fillMaxWidth(frac).height(8.dp).clip(RoundedCornerShape(999.dp)).background(fill),
            )
        }
    }
}

/** Config — thresholds, run frequency, and what gets shared (admin parity with web). */
@Composable
private fun ConfigCard(
    config: PewsConfigDto,
    isSaving: Boolean,
    onSave: (PewsConfigDto) -> Unit,
) {
        var draft by remember(config) { mutableStateOf(config) }
    val dirty = draft != config

    VCard {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    appString(StringKeys.SCH_CONFIGURATION),
                    style = VTypography.label.copy(color = VColors.ink3).copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    appString(StringKeys.SCH_CONFIGURATION_DESC),
                    style = VTypography.caption.copy(color = VColors.ink3).copy(fontSize = 12.sp),
                )
            }
            if (dirty) {
                VButton(
                    text = if (isSaving) "…" else appString(StringKeys.SCH_SAVE),
                    onClick = { onSave(draft) },
                    variant = VButtonVariant.Primary,
                    size = VButtonSize.Sm,
                    enabled = !isSaving,
                )
            }
        }
        Spacer(Modifier.height(12.dp))

        ConfigToggle(
            label = appString(StringKeys.SCH_RELATIVE_THRESHOLDS),
            hint = appString(StringKeys.SCH_RELATIVE_THRESHOLDS_HINT),
            checked = draft.useRelativeThresholds,
            onChange = { draft = draft.copy(useRelativeThresholds = it) },
        )
        ConfigToggle(
            label = appString(StringKeys.SCH_AI_NARRATIVE),
            hint = appString(StringKeys.SCH_AI_NARRATIVE_HINT),
            checked = draft.aiNarrativeEnabled,
            onChange = { draft = draft.copy(aiNarrativeEnabled = it) },
        )
        ConfigToggle(
            label = appString(StringKeys.SCH_SHARE_WITH_PARENTS),
            hint = appString(StringKeys.SCH_SHARE_WITH_PARENTS_HINT),
            checked = draft.parentShareEnabled,
            onChange = { draft = draft.copy(parentShareEnabled = it) },
        )
        // Run frequency pills
        Spacer(Modifier.height(10.dp))
        Text(
            appString(StringKeys.SCH_RUN_FREQUENCY),
            style = VTypography.body.copy(color = VColors.ink).copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
        )
        Spacer(Modifier.height(6.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FreqChip(appString(StringKeys.SCH_DAILY), "daily", draft.runFrequency, { draft = draft.copy(runFrequency = it) }, Modifier.weight(1f))
            FreqChip(appString(StringKeys.SCH_WEEKLY), "weekly", draft.runFrequency, { draft = draft.copy(runFrequency = it) }, Modifier.weight(1f))
        }
    }
}

@Composable
private fun ConfigToggle(
    label: String,
    hint: String,
    checked: Boolean,
    onChange: (Boolean) -> Unit,
) {
        Row(
        Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                label,
                style = VTypography.body.copy(color = VColors.ink).copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
            )
            Spacer(Modifier.height(2.dp))
            Text(
                hint,
                style = VTypography.caption.copy(color = VColors.ink3).copy(fontSize = 11.sp, lineHeight = 15.sp),
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = VColors.surfaceCard,
                checkedTrackColor = VColors.violet,
                uncheckedThumbColor = VColors.surfaceCard,
                uncheckedTrackColor = VColors.ink3.copy(alpha = 0.3f),
            ),
        )
    }
}

@Composable
private fun FreqChip(
    label: String,
    value: String,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
        val isSel = selected == value
    Box(
        modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (isSel) VColors.ink else VColors.cream)
            .clickable { onSelect(value) }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            style = VTypography.label.copy(color = if (isSel) VColors.surfaceCard else VColors.ink2)
                .copy(fontWeight = FontWeight.SemiBold, fontSize = 12.sp),
        )
    }
}

/** Real band counts — High / Watch (medium) / Watch (early). */
@Composable
private fun RiskBandSummary(cohort: PewsCohortDto) {
        VCard {
        Text(
            appString(StringKeys.SCH_RISK_BAND),
            style = VTypography.label.copy(color = VColors.ink3).copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
        )
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BandCount(appString(StringKeys.SCH_HIGH), cohort.high, VColors.error, VColors.error, Modifier.weight(1f))
            BandCount(appString(StringKeys.SCH_MEDIUM), cohort.medium, VColors.gold, VColors.gold, Modifier.weight(1f))
            BandCount(appString(StringKeys.SCH_WATCH), cohort.watch, VColors.success, VColors.success, Modifier.weight(1f))
        }
        cohort.runDate?.let {
            Spacer(Modifier.height(10.dp))
            Text(
                appString(StringKeys.SCH_AS_OF, "date" to it),
                style = VTypography.caption.copy(color = VColors.ink3).copy(fontSize = 11.sp),
            )
        }
    }
}

@Composable
private fun BandCount(
    label: String,
    count: Int,
    bg: androidx.compose.ui.graphics.Color,
    fg: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier.clip(RoundedCornerShape(10.dp)).background(bg).padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("$count", style = VTypography.body.copy(fontWeight = FontWeight.SemiBold, fontSize = 22.sp).copy(color = fg).copy(fontWeight = FontWeight.Bold))
        Spacer(Modifier.height(2.dp))
        Text(label, style = VTypography.caption.copy(color = fg).copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold))
    }
}

@Composable
private fun BandFilterRow(selected: String, onSelect: (String) -> Unit) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(appString(StringKeys.SCH_ALL), "watch", selected, onSelect, Modifier.weight(1f))
        FilterChip(appString(StringKeys.SCH_MEDIUM_PLUS), "medium", selected, onSelect, Modifier.weight(1f))
        FilterChip(appString(StringKeys.SCH_HIGH_ONLY), "high", selected, onSelect, Modifier.weight(1f))
    }
}

@Composable
private fun FilterChip(
    label: String,
    value: String,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
        val isSel = selected == value
    Box(
        modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (isSel) VColors.ink else VColors.cream)
            .clickable { onSelect(value) }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            style = VTypography.label.copy(color = if (isSel) VColors.surfaceCard else VColors.ink2)
                .copy(fontWeight = FontWeight.SemiBold, fontSize = 12.sp),
        )
    }
}

@Composable
private fun AiDisabledNote() {
        Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
            .background(VColors.cream).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(VIcons.AlertCircle, contentDescription = null, tint = VColors.ink3, modifier = Modifier.size(16.dp))
        Text(
            appString(StringKeys.SCH_AI_DISABLED_NOTE),
            style = VTypography.caption.copy(color = VColors.ink2).copy(fontSize = 12.sp, lineHeight = 17.sp),
        )
    }
}

/**
 * Inline "all on track" note shown in place of the student list when the cohort
 * is empty — so the Configuration card (incl. "Share with parents") and the
 * Effectiveness rollup below remain reachable instead of being hidden behind a
 * full-screen empty state.
 */
@Composable
private fun AllOnTrackNote() {
        Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
            .background(VColors.cream).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(VIcons.ShieldCheck, contentDescription = null, tint = VColors.violet, modifier = Modifier.size(18.dp))
        Text(
            appString(StringKeys.SCH_ALL_ON_TRACK_NOTE),
            style = VTypography.caption.copy(color = VColors.ink2).copy(fontSize = 12.sp, lineHeight = 17.sp),
        )
    }
}

/** Async job status indicator — shows queued/processing/completed/failed with auto-poll. */
@Composable
private fun JobStatusCard(status: String, jobId: String?, onPoll: (String) -> Unit) {
        val (label, tone) = when (status) {
        "queued" -> appString(StringKeys.SCH_QUEUED) to VColors.ink3
        "processing" -> appString(StringKeys.SCH_RUNNING) to VColors.gold
        "completed" -> appString(StringKeys.SCH_COMPLETE) to VColors.success
        "failed" -> appString(StringKeys.SCH_FAILED) to VColors.error
        else -> status to VColors.ink3
    }
    VCard {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                Modifier.size(10.dp).clip(CircleShape).background(tone),
            )
            Text(
                label,
                style = VTypography.body.copy(color = VColors.ink).copy(fontWeight = FontWeight.SemiBold, fontSize = 13.sp),
                modifier = Modifier.weight(1f),
            )
            if (jobId != null && (status == "queued" || status == "processing")) {
                VButton(
                    text = appString(StringKeys.SCH_REFRESH),
                    onClick = { onPoll(jobId) },
                    variant = VButtonVariant.Ghost,
                    size = VButtonSize.Sm,
                )
            }
        }
    }
}

/** Cohort risk distribution over time — a simple sparkline-style trend. */
@Composable
private fun TrendCard(points: List<PewsTrendPointDto>) {
        val maxTotal = points.maxOfOrNull { it.total } ?: 0
    VCard {
        Text(
            appString(StringKeys.SCH_RISK_TREND),
            style = VTypography.label.copy(color = VColors.ink3).copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            appString(StringKeys.SCH_RISK_TREND_DESC),
            style = VTypography.caption.copy(color = VColors.ink3).copy(fontSize = 12.sp),
        )
        Spacer(Modifier.height(12.dp))
        // Stacked bar chart — one bar per run date
        points.takeLast(15).forEach { p ->
            Row(
                Modifier.fillMaxWidth().padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    p.runDate.takeLast(5),
                    style = VTypography.caption.copy(color = VColors.ink3).copy(fontSize = 10.sp),
                    modifier = Modifier.weight(0.3f),
                )
                Box(
                    Modifier.weight(0.7f).height(16.dp).clip(RoundedCornerShape(4.dp)).background(VColors.cream),
                ) {
                    Row(Modifier.fillMaxSize()) {
                        val highFrac = if (maxTotal > 0) p.high.toFloat() / maxTotal else 0f
                        val medFrac = if (maxTotal > 0) p.medium.toFloat() / maxTotal else 0f
                        val watchFrac = if (maxTotal > 0) p.watch.toFloat() / maxTotal else 0f
                        Box(Modifier.fillMaxWidth(highFrac).fillMaxSize().background(VColors.error))
                        Box(Modifier.fillMaxWidth(medFrac).fillMaxSize().background(VColors.gold))
                        Box(Modifier.fillMaxWidth(watchFrac).fillMaxSize().background(VColors.success))
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TrendLegend(appString(StringKeys.SCH_HIGH), VColors.error)
            TrendLegend(appString(StringKeys.SCH_MEDIUM), VColors.gold)
            TrendLegend(appString(StringKeys.SCH_WATCH), VColors.success)
        }
    }
}

@Composable
private fun TrendLegend(label: String, color: androidx.compose.ui.graphics.Color) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(Modifier.size(8.dp).clip(RoundedCornerShape(2.dp)).background(color))
        Text(label, style = VTypography.caption.copy(color = VColors.ink3).copy(fontSize = 10.sp))
    }
}

@Composable
private fun PewsStudentRow(s: PewsStudentDto, onClick: () -> Unit) {
        val (tone, levelLabel) = when (s.riskLevel) {
        "high" -> VBadgeTone.Danger to appString(StringKeys.SCH_HIGH)
        "medium" -> VBadgeTone.Warning to appString(StringKeys.SCH_MEDIUM)
        else -> VBadgeTone.Success to appString(StringKeys.SCH_WATCH)
    }
    VCard(onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // initial avatar
            Box(
                Modifier.size(40.dp).clip(CircleShape).background(VColors.cream),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    s.name.firstOrNull()?.uppercase() ?: "?",
                    style = VTypography.bodySmall.copy(fontWeight = FontWeight.SemiBold).copy(color = VColors.ink2),
                )
            }
            Column(Modifier.weight(1f)) {
                Text(s.name, style = VTypography.bodySmall.copy(fontWeight = FontWeight.SemiBold).copy(color = VColors.ink), maxLines = 1)
                Spacer(Modifier.height(2.dp))
                Text(
                    appString(StringKeys.SCH_CLASS_SECTION_DASH, "className" to s.className, "section" to if (s.section.isNotBlank()) "-${s.section}" else ""),
                    style = VTypography.caption.copy(color = VColors.ink3).copy(fontSize = 12.sp),
                )
            }
            VBadge(text = levelLabel, tone = tone)
        }
        // signal chips (deterministic reasons)
        if (s.signals.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                s.signals.take(3).forEach { sig ->
                    SignalChip(sig.label)
                }
            }
        }
        // AI one-liner — only when present
        val aiLine = s.aiNarrative ?: s.aiCause
        if (!aiLine.isNullOrBlank()) {
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(VIcons.Sparkles, contentDescription = null, tint = VColors.sky, modifier = Modifier.size(13.dp))
                Text(
                    aiLine,
                    style = VTypography.caption.copy(color = VColors.ink2).copy(fontSize = 12.sp, lineHeight = 17.sp),
                    maxLines = 2,
                )
            }
        }
    }
}

@Composable
private fun SignalChip(label: String) {
        Box(
        Modifier.clip(RoundedCornerShape(8.dp)).background(VColors.cream).padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Text(label, style = VTypography.caption.copy(color = VColors.ink2).copy(fontSize = 11.sp))
    }
}
