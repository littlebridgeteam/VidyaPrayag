package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.admin.presentation.ResultsState
import com.littlebridge.enrollplus.feature.admin.presentation.ResultsViewModel
import com.littlebridge.enrollplus.feature.admin.presentation.StudentResult
import com.littlebridge.enrollplus.ui.v2.components.VAvatar
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VBadge
import com.littlebridge.enrollplus.ui.v2.components.VBadgeTone
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VTag
import com.littlebridge.enrollplus.ui.v2.components.VPullRefresh
import com.littlebridge.enrollplus.ui.v2.screens.VSectionHeader
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.SkeletonDashboard
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.theme.staggeredItemEntrance
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel

/**
 * ResultsPublishScreenV2 — exam results overlay (Admin → publish & review).
 *
 * Wired to [ResultsViewModel] (`GET /api/v1/school/results?test=&class=&subject=`).
 *
 * Layout:
 *   • 3 horizontally scrolling VTag rows: Tests · Classes · Subjects (single-select)
 *   • Class summary VCard: classAverage + averageTrend + 3 small chips
 *     (Exceeding / Meeting / Below).
 *   • Student results list — each VCard shows avatar, name, attendance/score data,
 *     status VBadge (Exceeding=Success, Meeting=Arctic, Below=Danger), trend caption.
 *
 * Three states via [VStateHost] (LAW 3).
 */
@Composable
fun ResultsPublishScreenV2(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ResultsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    Column(modifier.fillMaxSize().statusBarsPadding()
        .imePadding()
        .navigationBarsPadding()) {
        VBackHeader(title = appString(StringKeys.SCH_RESULTS), onBack = onBack, pinRouteId = "overlay_report_publish")
        VPullRefresh(isRefreshing = state.isLoading && state.students.isNotEmpty(), onRefresh = { viewModel.loadResults() }) {
            ResultsContent(
                state = state,
                onSelectTest = viewModel::selectTest,
                onSelectClass = viewModel::selectClass,
                onSelectSubject = viewModel::selectSubject,
                onRetry = { viewModel.loadResults() },
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun ResultsContent(
    state: ResultsState,
    onSelectTest: (String) -> Unit,
    onSelectClass: (String) -> Unit,
    onSelectSubject: (String) -> Unit,
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
        if (state.availableTests.isNotEmpty()) {
            FilterRow(label = appString(StringKeys.SCH_TESTS), items = state.availableTests, selected = state.selectedTest, onSelect = onSelectTest)
        }
        if (state.availableClasses.isNotEmpty()) {
            FilterRow(label = appString(StringKeys.SCH_CLASSES), items = state.availableClasses, selected = state.selectedClass, onSelect = onSelectClass)
        }
        if (state.availableSubjects.isNotEmpty()) {
            FilterRow(label = appString(StringKeys.SCH_SUBJECTS), items = state.availableSubjects, selected = state.selectedSubject, onSelect = onSelectSubject)
        }

        VStateHost(
            loading = state.isLoading,
            error = state.errorMessage,
            isEmpty = state.students.isEmpty(),
            emptyTitle = appString(StringKeys.SCH_NO_RESULTS_YET),
            emptyBody = appString(StringKeys.SCH_NO_RESULTS_DESC),
            emptyIcon = VIcons.ClipboardList,
            onRetry = onRetry,
            skeleton = { SkeletonDashboard() },
        ) {
            // Class summary
            VCard {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(Modifier.weight(1f)) {
                        Text(appString(StringKeys.SCH_CLASS_AVERAGE), style = VTypography.label.copy(color = VColors.ink3))
                        Spacer(Modifier.height(4.dp))
                        Text(state.classAverage, style = VTypography.body.copy(fontWeight = FontWeight.SemiBold, fontSize = 22.sp).copy(color = VColors.ink))
                    }
                    VBadge(text = state.averageTrend, tone = VBadgeTone.Arctic)
                }
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(Modifier.weight(1f)) { MiniCount(appString(StringKeys.SCH_EXCEEDING), state.exceedingCount.toString(), VBadgeTone.Success) }
                    Box(Modifier.weight(1f)) { MiniCount(appString(StringKeys.SCH_MEETING), state.meetingCount.toString(), VBadgeTone.Arctic) }
                    Box(Modifier.weight(1f)) { MiniCount(appString(StringKeys.SCH_BELOW), state.belowCount.toString(), VBadgeTone.Danger) }
                }
            }

            VSectionHeader(title = appString(StringKeys.SCH_STUDENTS_HEADER))
            state.students.forEachIndexed { i, s -> StudentResultCard(s, modifier = Modifier.staggeredItemEntrance(i, state.students.isNotEmpty())) }
        }
    }
}

@Composable
private fun FilterRow(
    label: String,
    items: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, style = VTypography.label.copy(color = VColors.ink3))
        Row(
            Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items.forEach { item ->
                VTag(text = item, active = item == selected, onClick = { onSelect(item) })
            }
        }
    }
}

@Composable
private fun MiniCount(label: String, value: String, tone: VBadgeTone) {
        Column {
        Text(label, style = VTypography.label.copy(color = VColors.ink3))
        Spacer(Modifier.height(4.dp))
        VBadge(text = value, tone = tone)
    }
}

@Composable
private fun StudentResultCard(s: StudentResult, modifier: Modifier = Modifier) {
        val statusTone = when (s.status.lowercase()) {
        "exceeding" -> VBadgeTone.Success
        "meeting" -> VBadgeTone.Arctic
        "below" -> VBadgeTone.Danger
        else -> VBadgeTone.Neutral
    }
    VCard(modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            VAvatar(name = s.name.ifBlank { "?" }, src = s.imageUrl.ifBlank { null }, size = 40.dp)
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        s.name,
                        style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold).copy(color = VColors.ink),
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    VBadge(text = s.status, tone = statusTone)
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    appString(StringKeys.SCH_SCORE_ATTENDANCE, "score" to s.score, "attendance" to s.attendance),
                    style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold).copy(color = VColors.ink2),
                )
                if (s.trend.isNotBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(s.trend, style = VTypography.caption.copy(color = VColors.ink3))
                }
            }
        }
    }
}
