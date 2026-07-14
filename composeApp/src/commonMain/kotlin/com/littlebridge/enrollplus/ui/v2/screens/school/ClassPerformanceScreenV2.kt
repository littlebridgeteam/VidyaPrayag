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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.feature.admin.presentation.ClassPerformanceState
import com.littlebridge.enrollplus.feature.admin.presentation.ClassPerformanceViewModel
import com.littlebridge.enrollplus.feature.admin.presentation.GradeDistribution
import com.littlebridge.enrollplus.feature.admin.presentation.ProgressMonitoringItem
import com.littlebridge.enrollplus.feature.admin.presentation.SubjectMatrixItem
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VBadge
import com.littlebridge.enrollplus.ui.v2.components.VBadgeTone
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VProgressBar
import com.littlebridge.enrollplus.ui.v2.screens.VSectionHeader
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.SkeletonDashboard
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

/**
 * ClassPerformanceScreenV2 — class-level analytics overlay.
 *
 * Wired to [ClassPerformanceViewModel] (`GET /api/v1/school/analytics/class`).
 *
 * Layout:
 *   • 3-tile KPI row: avg proficiency / active students / median grade
 *   • Grade distribution VCard with VProgressBar rows (one per grade)
 *   • Subject matrix VCard (subject + percentage + trend VBadge)
 *   • Risk summary VCard (critical/moderate counts + target reach VBadge)
 *   • Top performer VCard
 *   • Recent progress monitoring rows (student initials avatar + status VBadge)
 *
 * Three states via [VStateHost] (LAW 3).
 */
@Composable
fun ClassPerformanceScreenV2(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ClassPerformanceViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    LaunchedEffect(Unit) { viewModel.load() }
    Column(modifier.fillMaxSize().statusBarsPadding()
        .imePadding()
        .navigationBarsPadding()) {
        VBackHeader(title = appString(StringKeys.SCH_CLASS_PERFORMANCE), onBack = onBack)
        ClassPerformanceContent(
            state = state,
            onRetry = { viewModel.load() },
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun ClassPerformanceContent(
    state: ClassPerformanceState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
        Column(
        modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        VStateHost(
            loading = state.isLoading,
            error = state.errorMessage,
            isEmpty = state.gradeDistribution.isEmpty() &&
                state.subjectMatrix.isEmpty() &&
                state.recentProgress.isEmpty() &&
                state.avgProficiency.isBlank(),
            emptyTitle = appString(StringKeys.SCH_NO_DATA_YET),
            emptyBody = appString(StringKeys.SCH_CLASS_PERFORMANCE_DESC),
            emptyIcon = VIcons.TrendingUp,
            onRetry = onRetry,
            skeleton = { SkeletonDashboard() },
        ) {
            // KPI row
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.weight(1f)) { Kpi(label = appString(StringKeys.SCH_AVG_PROFICIENCY), value = state.avgProficiency.ifBlank { "—" }) }
                Box(Modifier.weight(1f)) { Kpi(label = appString(StringKeys.SCH_ACTIVE_STUDENTS), value = state.activeStudents.toString()) }
                Box(Modifier.weight(1f)) { Kpi(label = appString(StringKeys.SCH_MEDIAN_GRADE), value = state.medianGrade.ifBlank { "—" }) }
            }

            // Grade distribution
            if (state.gradeDistribution.isNotEmpty()) {
                VSectionHeader(title = appString(StringKeys.SCH_GRADE_DISTRIBUTION))
                VCard {
                    state.gradeDistribution.forEachIndexed { i, g ->
                        if (i > 0) Spacer(Modifier.height(10.dp))
                        GradeRow(g)
                    }
                }
            }

            // Subject matrix
            if (state.subjectMatrix.isNotEmpty()) {
                VSectionHeader(title = appString(StringKeys.SCH_SUBJECT_MATRIX))
                VCard {
                    state.subjectMatrix.forEachIndexed { i, s ->
                        if (i > 0) Box(Modifier.fillMaxWidth().height(1.dp).background(VColors.line))
                        SubjectRow(s)
                    }
                }
            }

            // Risk summary
            VSectionHeader(title = appString(StringKeys.SCH_EARLY_WARNING_HEADER))
            VCard {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(Modifier.weight(1f)) { RiskTile(label = appString(StringKeys.SCH_CRITICAL), value = state.criticalRiskCount.toString(), tone = VBadgeTone.Danger) }
                    Box(Modifier.weight(1f)) { RiskTile(label = appString(StringKeys.SCH_MODERATE), value = state.moderateRiskCount.toString(), tone = VBadgeTone.Warning) }
                    Box(Modifier.weight(1f)) { RiskTile(label = appString(StringKeys.SCH_ON_TARGET), value = "${state.proficiencyTargetReach}%", tone = VBadgeTone.Success) }
                }
            }

            // Top performer
            if (state.topPerformerName.isNotBlank()) {
                VSectionHeader(title = appString(StringKeys.SCH_TOP_PERFORMER))
                VCard {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        VBadge(text = appString(StringKeys.SCH_STAR_1ST), tone = VBadgeTone.Warning)
                        Column(Modifier.weight(1f)) {
                            Text(state.topPerformerName, style = VTypography.bodySmall.copy(fontWeight = FontWeight.SemiBold).copy(color = VColors.ink), maxLines = 1, overflow = TextOverflow.Ellipsis)
                            if (state.topPerformerDetails.isNotBlank()) {
                                Spacer(Modifier.height(2.dp))
                                Text(state.topPerformerDetails, style = VTypography.caption.copy(color = VColors.ink3), maxLines = 2, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                }
            }

            // Recent progress monitoring
            if (state.recentProgress.isNotEmpty()) {
                VSectionHeader(title = appString(StringKeys.SCH_PROGRESS_MONITORING))
                state.recentProgress.forEach { p -> ProgressRow(p) }
            }
        }
    }
}

@Composable
private fun Kpi(label: String, value: String) {
        VCard {
        Text(label, style = VTypography.label.copy(color = VColors.ink3), maxLines = 1, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.height(4.dp))
        Text(value, style = VTypography.body.copy(fontWeight = FontWeight.SemiBold, fontSize = 22.sp).copy(color = VColors.ink), maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun RiskTile(label: String, value: String, tone: VBadgeTone) {
        Column {
        Text(label, style = VTypography.label.copy(color = VColors.ink3), maxLines = 1, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.height(4.dp))
        VBadge(text = value, tone = tone)
    }
}

@Composable
private fun GradeRow(g: GradeDistribution) {
        Column {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(g.grade, style = VTypography.bodySmall.copy(fontWeight = FontWeight.SemiBold).copy(color = VColors.ink))
            Text("${g.percentage}%", style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold).copy(color = VColors.ink2))
        }
        Spacer(Modifier.height(6.dp))
        VProgressBar(value = (g.value * 100f).coerceIn(0f, 100f))
    }
}

@Composable
private fun SubjectRow(s: SubjectMatrixItem) {
        val (trendText, trendTone) = when (s.trend.lowercase()) {
        "up" -> appString(StringKeys.SCH_TREND_UP) to VBadgeTone.Success
        "down" -> appString(StringKeys.SCH_TREND_DOWN) to VBadgeTone.Danger
        else -> appString(StringKeys.SCH_TREND_FLAT) to VBadgeTone.Neutral
    }
    Row(
        Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(s.name, style = VTypography.bodySmall.copy(fontWeight = FontWeight.SemiBold).copy(color = VColors.ink), modifier = Modifier.weight(1f))
        Text("${s.percentage}%", style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold).copy(color = VColors.ink2))
        VBadge(text = trendText, tone = trendTone)
    }
}

@Composable
private fun ProgressRow(p: ProgressMonitoringItem) {
        val statusTone = when (p.status.uppercase()) {
        "EXCELLING" -> VBadgeTone.Success
        "PEWS ALERT" -> VBadgeTone.Danger
        "CONSISTENT" -> VBadgeTone.Arctic
        else -> VBadgeTone.Neutral
    }
    VCard {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(VColors.surfaceCard)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                Text(p.initials.take(2).uppercase(), style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold).copy(color = VColors.ink))
            }
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        p.name,
                        style = VTypography.bodySmall.copy(fontWeight = FontWeight.SemiBold).copy(color = VColors.ink),
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    VBadge(text = p.status, tone = statusTone)
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    appString(StringKeys.SCH_PROGRESS_SCORES, "math" to p.math, "science" to p.science, "literature" to p.literature),
                    style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold).copy(color = VColors.ink2),
                )
                Spacer(Modifier.height(2.dp))
                Text(appString(StringKeys.SCH_PROGRESS_ATTENDANCE, "attendance" to p.attendance), style = VTypography.caption.copy(color = VColors.ink3))
            }
        }
    }
}
