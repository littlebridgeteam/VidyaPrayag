package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.feature.admin.domain.model.AttendanceSummaryDto
import com.littlebridge.enrollplus.feature.admin.domain.model.FeeLedgerDto
import com.littlebridge.enrollplus.feature.admin.domain.model.MarksSummaryDto
import com.littlebridge.enrollplus.feature.admin.presentation.SchoolRecordsState
import com.littlebridge.enrollplus.feature.admin.presentation.SchoolRecordsViewModel
import com.littlebridge.enrollplus.feature.admin.presentation.SyllabusCoverageState
import com.littlebridge.enrollplus.feature.admin.presentation.SyllabusCoverageViewModel
import com.littlebridge.enrollplus.feature.admin.presentation.PaceAlertsViewModel
import com.littlebridge.enrollplus.feature.admin.presentation.PaceAlertsState
import com.littlebridge.enrollplus.ui.v2.components.VBadge
import com.littlebridge.enrollplus.ui.v2.components.VBadgeTone
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonSize
import com.littlebridge.enrollplus.ui.v2.components.VButtonTone
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VComingSoon
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VProgressBar
import com.littlebridge.enrollplus.ui.v2.components.VTopTabs
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VMotion
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

private enum class RecordsTab {
    Coverage, Pace, Attendance, Marks, Fee, Documents;

    @Composable
    fun label(): String = when (this) {
        Coverage   -> appString(StringKeys.REC_TAB_COVERAGE)
        Pace       -> appString(StringKeys.REC_TAB_PACE)
        Attendance -> appString(StringKeys.REC_TAB_ATTENDANCE)
        Marks      -> appString(StringKeys.REC_TAB_MARKS)
        Fee        -> appString(StringKeys.REC_TAB_FEE)
        Documents  -> appString(StringKeys.REC_TAB_DOCUMENTS)
    }
}

/**
 * SchoolRecordsScreenV2 — `Admin.tsx → Records`, wired to the real
 * [SyllabusCoverageViewModel] (`AnalyticsApi` → `GET /api/v1/syllabus-coverage`).
 *
 * The **Coverage** tab renders live department progress, lagging alerts and academic
 * milestones from the analytics endpoint ([SyllabusCoverageViewModel]).
 *
 * RA-52: **Attendance**, **Marks** and **Fee** now read real school-wide rollups from
 * [SchoolRecordsViewModel] (`GET /api/v1/school/{attendance/summary,marks/summary,fees/ledger}`)
 * instead of `VComingSoon` placeholders. Each rollup loads lazily on first view of its tab
 * and carries its own loading / error+retry / empty state ([VStateHost], LAW: three states).
 * Only **Documents** stays `VComingSoon` (no media-storage backend yet — LAW 6, no fabrication).
 * No MockV2 in production.
 */
@Composable
fun SchoolRecordsScreenV2(
    modifier: Modifier = Modifier,
    viewModel: SyllabusCoverageViewModel = koinViewModel(),
    recordsViewModel: SchoolRecordsViewModel = koinViewModel(),
    paceViewModel: PaceAlertsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    val recordsState by recordsViewModel.state.collectAsStateV2()
    val paceState by paceViewModel.state.collectAsStateV2()
    SchoolRecordsContent(
        state = state,
        records = recordsState,
        pace = paceState,
        onRetry = viewModel::load,
        onRetryPace = paceViewModel::load,
        onResolveAlert = paceViewModel::resolveAlert,
        onRecalculatePace = paceViewModel::recalculate,
        onTabSelected = { tab ->
            when (tab) {
                RecordsTab.Attendance.name -> recordsViewModel.ensureAttendance()
                RecordsTab.Marks.name -> recordsViewModel.ensureMarks()
                RecordsTab.Fee.name -> recordsViewModel.ensureFees()
            }
        },
        onRetryAttendance = recordsViewModel::loadAttendance,
        onRetryMarks = recordsViewModel::loadMarks,
        onRetryFees = recordsViewModel::loadFees,
        modifier = modifier,
    )
}

@Composable
private fun SchoolRecordsContent(
    state: SyllabusCoverageState,
    records: SchoolRecordsState,
    pace: PaceAlertsState,
    onRetry: () -> Unit,
    onRetryPace: () -> Unit,
    onResolveAlert: (String) -> Unit,
    onRecalculatePace: () -> Unit,
    onTabSelected: (String) -> Unit,
    onRetryAttendance: () -> Unit,
    onRetryMarks: () -> Unit,
    onRetryFees: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var tab by remember { mutableStateOf(RecordsTab.Coverage) }

    LaunchedEffect(tab) { onTabSelected(tab.name) }

    // Stagger entrance
    val headerAlpha = remember { Animatable(0f) }
    val headerOffset = remember { Animatable(20f) }
    LaunchedEffect(Unit) {
        headerAlpha.snapTo(0f); headerOffset.snapTo(20f)
        launch {
            delay(100)
            headerAlpha.animateTo(1f, tween(VMotion.durSlower, easing = VMotion.ease))
            headerOffset.animateTo(0f, tween(VMotion.durSlower, easing = VMotion.ease))
        }
    }

    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .statusBarsPadding()
            .imePadding()
            .navigationBarsPadding()
            .padding(top = 16.dp, bottom = 140.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Premium header
        Column(
            modifier = Modifier
                .graphicsLayer(translationY = headerOffset.value)
                .alpha(headerAlpha.value),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(7.dp),
            ) {
                Box(Modifier.size(5.dp).clip(CircleShape).background(VColors.violet))
                Text(appString(StringKeys.REC_TITLE), style = VTypography.accentLabel, color = VColors.violet)
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold, color = VColors.ink)) {
                        append("Records")
                    }
                    withStyle(SpanStyle(fontWeight = FontWeight.Normal, color = VColors.ink2)) {
                        append(" & Analytics")
                    }
                },
                style = VTypography.h2,
            )
        }

        val tabLabels = RecordsTab.entries.map { it.label() }
        VTopTabs(
            tabs = tabLabels,
            selected = tabLabels[tab.ordinal],
            onSelect = { label -> tab = RecordsTab.entries[tabLabels.indexOf(label)] },
        )
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            when (tab) {
                RecordsTab.Coverage -> CoverageTab(state = state, onRetry = onRetry)
                RecordsTab.Pace -> PaceTab(state = pace, onRetry = onRetryPace, onResolve = onResolveAlert, onRecalculate = onRecalculatePace)
                RecordsTab.Attendance -> AttendanceTab(ui = records.attendance, onRetry = onRetryAttendance)
                RecordsTab.Marks -> MarksTab(ui = records.marks, onRetry = onRetryMarks)
                RecordsTab.Fee -> FeeTab(ui = records.fees, onRetry = onRetryFees)
                RecordsTab.Documents -> VComingSoon(
                    title = appString(StringKeys.REC_DOC_LIBRARY_TITLE),
                    description = appString(StringKeys.REC_DOC_LIBRARY_DESC),
                )
            }
        }
    }
}

@Composable
private fun CoverageTab(state: SyllabusCoverageState, onRetry: () -> Unit) {
    VStateHost(
        loading = state.isLoading,
        error = state.errorMessage,
        isEmpty = state.departmentProgress.isEmpty() && state.alerts.isEmpty() && state.milestones.isEmpty(),
        emptyTitle = appString(StringKeys.REC_NO_COVERAGE),
        emptyBody = appString(StringKeys.REC_NO_COVERAGE_BODY),
        emptyIcon = VIcons.BookOpen,
        onRetry = onRetry,
        skeleton = { com.littlebridge.enrollplus.ui.v2.screens.SkeletonList(rows = 5, withAvatar = false) },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // ── Overall ───────────────────────────────────────────────────────
            RecordsCreamCard {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(appString(StringKeys.REC_OVERALL_COVERAGE), style = VTypography.label, color = VColors.ink3)
                    if (state.overallTrend.isNotBlank()) {
                        VBadge(text = state.overallTrend, tone = VBadgeTone.Arctic)
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text("${state.overallPercentage}%", style = VTypography.h2.copy(fontWeight = FontWeight.ExtraBold), color = VColors.ink)
                Spacer(Modifier.height(8.dp))
                VProgressBar(
                    value = state.overallPercentage.toFloat(),
                    tone = if (state.overallPercentage < 70) VBadgeTone.Warning else VBadgeTone.Arctic,
                )
            }

            // ── By department ─────────────────────────────────────────────────
            if (state.departmentProgress.isNotEmpty()) {
                RecordsCreamCard {
                    Text(appString(StringKeys.REC_BY_DEPARTMENT), style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold), color = VColors.ink)
                    Spacer(Modifier.height(12.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        state.departmentProgress.forEach { d ->
                            Column {
                                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(d.name, style = VTypography.bodySmall, color = VColors.ink)
                                    Text("${(d.progress * 100).roundToInt()}%", style = VTypography.caption, color = VColors.ink2)
                                }
                                Spacer(Modifier.height(4.dp))
                                VProgressBar(
                                    value = d.progress * 100f,
                                    tone = if (d.isDelayed) VBadgeTone.Danger else VBadgeTone.Arctic,
                                )
                                if (d.trend.isNotBlank()) {
                                    Text(d.trend, style = VTypography.caption, color = if (d.isDelayed) VColors.coral else VColors.ink3)
                                }
                            }
                        }
                    }
                }
            }

            // ── Lagging alerts ────────────────────────────────────────────────
            if (state.alerts.isNotEmpty()) {
                Column {
                    Text(appString(StringKeys.REC_LAGGING_CLASSES), style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold), color = VColors.ink, modifier = Modifier.padding(bottom = 8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        state.alerts.forEach { a ->
                            RecordsCreamCard {
                                Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Icon(VIcons.AlertCircle, contentDescription = null, tint = if (a.isCritical) VColors.coral else VColors.gold, modifier = Modifier.size(18.dp).padding(top = 2.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text("${a.subject} • ${a.className}", style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold), color = VColors.ink)
                                        if (a.instructor.isNotBlank()) {
                                            Text(a.instructor, style = VTypography.caption, color = VColors.ink3)
                                        }
                                    }
                                    VBadge(
                                        text = appString(StringKeys.REC_BEHIND, "pct" to a.delayPercentage),
                                        tone = if (a.isCritical) VBadgeTone.Danger else VBadgeTone.Warning,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ── Academic milestones ───────────────────────────────────────────
            if (state.milestones.isNotEmpty()) {
                Column {
                    Text(appString(StringKeys.REC_MILESTONES), style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold), color = VColors.ink, modifier = Modifier.padding(bottom = 8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        state.milestones.forEach { m ->
                            RecordsCreamCard {
                                Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(m.month, style = VTypography.caption, color = VColors.ink3)
                                        Text(m.day, style = VTypography.h2.copy(fontWeight = FontWeight.ExtraBold), color = VColors.ink)
                                    }
                                    Column(Modifier.weight(1f)) {
                                        Text(m.title, style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold), color = VColors.ink)
                                        if (m.description.isNotBlank()) {
                                            Text(m.description, style = VTypography.caption, color = VColors.ink3)
                                        }
                                    }
                                    if (m.isVerified) {
                                        Icon(VIcons.Check, contentDescription = appString(StringKeys.REC_VERIFIED), tint = VColors.success, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ───────────────────────────── RA-52: Attendance ─────────────────────────────

@Composable
private fun AttendanceTab(
    ui: com.littlebridge.enrollplus.feature.admin.presentation.AttendanceSummaryUi,
    onRetry: () -> Unit,
) {
    val data: AttendanceSummaryDto? = ui.data
    VStateHost(
        loading = ui.isLoading,
        error = ui.error,
        isEmpty = ui.loaded && (data == null || data.total == 0),
        emptyTitle = appString(StringKeys.REC_NO_ATTENDANCE),
        emptyBody = appString(StringKeys.REC_NO_ATTENDANCE_BODY),
        emptyIcon = VIcons.Calendar,
        onRetry = onRetry,
        skeleton = { com.littlebridge.enrollplus.ui.v2.screens.SkeletonList(rows = 4, withAvatar = false) },
    ) {
        if (data == null) return@VStateHost
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            RecordsCreamCard {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(appString(StringKeys.REC_LATEST_REGISTER) + (data.latestDate?.let { " • $it" } ?: ""), style = VTypography.label, color = VColors.ink3)
                    VBadge(text = appString(StringKeys.REC_PRESENT_PCT, "pct" to data.rate), tone = if (data.rate < 75) VBadgeTone.Warning else VBadgeTone.Success)
                }
                Spacer(Modifier.height(8.dp))
                VProgressBar(
                    value = data.rate.toFloat(),
                    tone = if (data.rate < 75) VBadgeTone.Warning else VBadgeTone.Success,
                )
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    StatCell(label = appString(StringKeys.REC_PRESENT), value = data.present.toString(), tint = VColors.success)
                    StatCell(label = appString(StringKeys.REC_ABSENT), value = data.absent.toString(), tint = VColors.coral)
                    StatCell(label = appString(StringKeys.REC_LATE), value = data.late.toString(), tint = VColors.gold)
                    StatCell(label = appString(StringKeys.REC_TOTAL), value = data.total.toString(), tint = VColors.ink)
                }
            }

            if (data.byClass.isNotEmpty()) {
                RecordsCreamCard {
                    Text(appString(StringKeys.REC_BY_CLASS), style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold), color = VColors.ink)
                    Spacer(Modifier.height(12.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        data.byClass.forEach { row ->
                            Column {
                                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(row.grade, style = VTypography.bodySmall, color = VColors.ink)
                                    Text("${row.present + row.late}/${row.total} • ${row.rate}%", style = VTypography.caption, color = VColors.ink2)
                                }
                                Spacer(Modifier.height(4.dp))
                                VProgressBar(
                                    value = row.rate.toFloat(),
                                    tone = if (row.rate < 75) VBadgeTone.Warning else VBadgeTone.Success,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────── RA-52: Marks ────────────────────────────────

@Composable
private fun MarksTab(
    ui: com.littlebridge.enrollplus.feature.admin.presentation.MarksSummaryUi,
    onRetry: () -> Unit,
) {
    val data: MarksSummaryDto? = ui.data
    VStateHost(
        loading = ui.isLoading,
        error = ui.error,
        isEmpty = ui.loaded && (data == null || data.assessments.isEmpty()),
        emptyTitle = appString(StringKeys.REC_NO_ASSESSMENTS),
        emptyBody = appString(StringKeys.REC_NO_ASSESSMENTS_BODY),
        emptyIcon = VIcons.BookOpen,
        onRetry = onRetry,
        skeleton = { com.littlebridge.enrollplus.ui.v2.screens.SkeletonList(rows = 4, withAvatar = false) },
    ) {
        if (data == null) return@VStateHost
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            RecordsCreamCard {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(appString(StringKeys.REC_OVERALL_AVG), style = VTypography.label, color = VColors.ink3)
                    VBadge(text = appString(StringKeys.REC_ASSESSMENT_COUNT, "count" to data.assessmentCount, "s" to if (data.assessmentCount == 1) "" else "s"), tone = VBadgeTone.Arctic)
                }
                Spacer(Modifier.height(8.dp))
                Text("${data.overallAveragePct}%", style = VTypography.h2.copy(fontWeight = FontWeight.ExtraBold), color = VColors.ink)
                Spacer(Modifier.height(8.dp))
                VProgressBar(
                    value = data.overallAveragePct.toFloat(),
                    tone = if (data.overallAveragePct < 40) VBadgeTone.Danger else if (data.overallAveragePct < 60) VBadgeTone.Warning else VBadgeTone.Success,
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                data.assessments.forEach { a ->
                    val pct = if (a.maxMarks > 0) ((a.average / a.maxMarks) * 100).roundToInt() else 0
                    RecordsCreamCard {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(Modifier.weight(1f)) {
                                Text("${a.subject} • ${a.assessmentName}", style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold), color = VColors.ink)
                                Text("${a.className}${a.examDate?.let { " • $it" } ?: ""}", style = VTypography.caption, color = VColors.ink3)
                            }
                            VBadge(
                                text = if (a.isPublished) appString(StringKeys.REC_PUBLISHED) else appString(StringKeys.REC_DRAFT),
                                tone = if (a.isPublished) VBadgeTone.Success else VBadgeTone.Neutral,
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(appString(StringKeys.REC_AVG, "avg" to a.average, "max" to a.maxMarks), style = VTypography.caption, color = VColors.ink2)
                            Text(if (a.gradedCount > 0) appString(StringKeys.REC_GRADED, "pct" to pct, "count" to a.gradedCount) else appString(StringKeys.REC_NOT_GRADED), style = VTypography.caption, color = VColors.ink3)
                        }
                        Spacer(Modifier.height(4.dp))
                        VProgressBar(
                            value = pct.toFloat(),
                            tone = if (pct < 40) VBadgeTone.Danger else if (pct < 60) VBadgeTone.Warning else VBadgeTone.Success,
                        )
                    }
                }
            }
        }
    }
}

// ──────────────────────────────── RA-52: Fee ─────────────────────────────────

@Composable
private fun FeeTab(
    ui: com.littlebridge.enrollplus.feature.admin.presentation.FeeLedgerUi,
    onRetry: () -> Unit,
) {
    val data: FeeLedgerDto? = ui.data
    val hasAny = data != null && (data.paidCount + data.dueCount + data.overdueCount) > 0
    VStateHost(
        loading = ui.isLoading,
        error = ui.error,
        isEmpty = ui.loaded && !hasAny,
        emptyTitle = appString(StringKeys.REC_NO_FEES),
        emptyBody = appString(StringKeys.REC_NO_FEES_BODY),
        emptyIcon = VIcons.Wallet,
        onRetry = onRetry,
        skeleton = { com.littlebridge.enrollplus.ui.v2.screens.SkeletonList(rows = 4, withAvatar = false) },
    ) {
        if (data == null) return@VStateHost
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            RecordsCreamCard {
                Text(appString(StringKeys.REC_LEDGER, "currency" to data.currency), style = VTypography.label, color = VColors.ink3)
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    StatCell(label = appString(StringKeys.REC_PAID), value = formatMoney(data.paidTotal), sub = "${data.paidCount}", tint = VColors.success)
                    StatCell(label = appString(StringKeys.REC_DUE), value = formatMoney(data.dueTotal), sub = "${data.dueCount}", tint = VColors.gold)
                    StatCell(label = appString(StringKeys.REC_OVERDUE), value = formatMoney(data.overdueTotal), sub = "${data.overdueCount}", tint = VColors.coral)
                }
            }

            if (data.recent.isNotEmpty()) {
                Column {
                    Text(appString(StringKeys.REC_RECENT), style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold), color = VColors.ink, modifier = Modifier.padding(bottom = 8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        data.recent.forEach { f ->
                            RecordsCreamCard {
                                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.SpaceBetween) {
                                    Column(Modifier.weight(1f)) {
                                        Text(f.title, style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold), color = VColors.ink)
                                        Text(f.dueDate?.let { appString(StringKeys.REC_DUE_DATE, "category" to f.category, "date" to it) } ?: f.category, style = VTypography.caption, color = VColors.ink3)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("${f.currency} ${formatMoney(f.amount)}", style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold), color = VColors.ink)
                                        Spacer(Modifier.height(4.dp))
                                        VBadge(
                                            text = f.status,
                                            tone = when (f.status.uppercase()) {
                                                "PAID" -> VBadgeTone.Success
                                                "OVERDUE" -> VBadgeTone.Danger
                                                else -> VBadgeTone.Warning
                                            },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/** A compact label-over-value stat cell used by the Attendance & Fee rollups. */
@Composable
private fun StatCell(label: String, value: String, tint: Color, sub: String? = null) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = VTypography.h3.copy(fontWeight = FontWeight.ExtraBold), color = tint)
        Text(label, style = VTypography.caption, color = VColors.ink3)
        if (sub != null) {
            Text(sub, style = VTypography.caption, color = VColors.ink2)
        }
    }
}

/** Group a Double into a plain integer-rupees string (no locale dep in commonMain). */
private fun formatMoney(value: Double): String {
    val whole = value.toLong()
    val s = whole.toString()
    val sb = StringBuilder()
    val len = s.length
    for (i in 0 until len) {
        if (i > 0 && (len - i) % 3 == 0) sb.append(',')
        sb.append(s[i])
    }
    return sb.toString()
}

// ── Premium shared primitive ──────────────────────────────────────────────────

@Composable
private fun RecordsCreamCard(
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.lg)
            .padding(16.dp),
    ) { content() }
}

// ── Pace tab (admin pace monitoring) ─────────────────────────────────────────

@Composable
private fun PaceTab(
    state: PaceAlertsState,
    onRetry: () -> Unit,
    onResolve: (String) -> Unit,
    onRecalculate: () -> Unit,
) {
    VStateHost(
        loading = state.isLoading,
        error = state.errorMessage,
        isEmpty = state.snapshots.isEmpty() && state.alerts.isEmpty(),
        emptyTitle = appString(StringKeys.REC_NO_PACE),
        emptyBody = appString(StringKeys.REC_NO_PACE_BODY),
        emptyIcon = VIcons.BookOpen,
        onRetry = onRetry,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // ── Recalculate button ──
            VButton(
                appString(StringKeys.REC_RECALCULATE),
                onClick = onRecalculate,
                variant = VButtonVariant.Secondary,
                tone = VButtonTone.Lavender,
                loading = state.isRecalculating,
                leading = { Icon(VIcons.Sparkles, contentDescription = null, modifier = Modifier.size(16.dp)) },
            )

            // ── Active alerts ──
            if (state.alerts.isNotEmpty()) {
                Column {
                    Text(appString(StringKeys.REC_ACTIVE_ALERTS), style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold), color = VColors.ink, modifier = Modifier.padding(bottom = 8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        state.alerts.forEach { alert ->
                            RecordsCreamCard {
                                Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Icon(
                                        VIcons.AlertCircle,
                                        contentDescription = null,
                                        tint = when (alert.level) {
                                            "CRITICAL" -> VColors.coral
                                            "BEHIND" -> VColors.gold
                                            else -> VColors.violet
                                        },
                                        modifier = Modifier.size(18.dp).padding(top = 2.dp),
                                    )
                                    Column(Modifier.weight(1f)) {
                                        Text("${alert.subject} • ${alert.className}-${alert.section}", style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold), color = VColors.ink)
                                        if (alert.teacherName.isNotBlank()) {
                                            Text(alert.teacherName, style = VTypography.caption, color = VColors.ink3)
                                        }
                                        if (alert.message.isNotBlank()) {
                                            Text(alert.message, style = VTypography.caption, color = VColors.ink3)
                                        }
                                        if (alert.aiReconfirmed) {
                                            Text(appString(StringKeys.REC_AI_RECONFIRMED), style = VTypography.caption.copy(fontSize = 10.sp), color = VColors.violet)
                                        }
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        VBadge(
                                            text = alert.level,
                                            tone = when (alert.level) {
                                                "CRITICAL" -> VBadgeTone.Danger
                                                "BEHIND" -> VBadgeTone.Warning
                                                else -> VBadgeTone.Accent
                                            },
                                        )
                                        Spacer(Modifier.height(8.dp))
                                        VButton(
                                            appString(StringKeys.REC_RESOLVE),
                                            onClick = { onResolve(alert.id) },
                                            size = VButtonSize.Sm,
                                            variant = VButtonVariant.Secondary,
                                            tone = VButtonTone.Lavender,
                                            loading = state.resolvingAlertId == alert.id,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ── Pace snapshots ──
            if (state.snapshots.isNotEmpty()) {
                Column {
                    Text(appString(StringKeys.REC_PACE_SNAPSHOTS), style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold), color = VColors.ink, modifier = Modifier.padding(bottom = 8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        state.snapshots.forEach { snap ->
                            RecordsCreamCard {
                                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Column(Modifier.weight(1f)) {
                                        Text("${snap.subject} • ${snap.className}-${snap.section}", style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold), color = VColors.ink)
                                        if (snap.teacherName.isNotBlank()) {
                                            Text(snap.teacherName, style = VTypography.caption, color = VColors.ink3)
                                        }
                                        Text(appString(StringKeys.REC_TOPICS_COVERED, "covered" to snap.coveredTopics, "total" to snap.totalTopics), style = VTypography.caption, color = VColors.ink3)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("${snap.actualPct}%", style = VTypography.bodySmall.copy(fontWeight = FontWeight.ExtraBold), color = VColors.ink)
                                        Text(appString(StringKeys.REC_EXPECTED, "pct" to snap.expectedPct), style = VTypography.caption.copy(fontSize = 10.sp), color = VColors.ink3)
                                    }
                                }
                                Spacer(Modifier.height(8.dp))
                                VProgressBar(
                                    value = snap.actualPct.toFloat(),
                                    tone = when (snap.status) {
                                        "CRITICAL" -> VBadgeTone.Danger
                                        "BEHIND" -> VBadgeTone.Warning
                                        "AHEAD" -> VBadgeTone.Success
                                        else -> VBadgeTone.Arctic
                                    },
                                )
                                Spacer(Modifier.height(4.dp))
                                VBadge(
                                    text = snap.status.replace('_', ' '),
                                    tone = when (snap.status) {
                                        "CRITICAL" -> VBadgeTone.Danger
                                        "BEHIND" -> VBadgeTone.Warning
                                        "AHEAD" -> VBadgeTone.Success
                                        else -> VBadgeTone.Arctic
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
