package com.littlebridge.enrollplus.ui.v2.screens.premium.school

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.admin.presentation.SchoolRecordsViewModel
import com.littlebridge.enrollplus.feature.admin.presentation.PaceAlertsViewModel
import com.littlebridge.enrollplus.feature.admin.presentation.SyllabusCoverageViewModel
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.cards.VListTilePremium
import com.littlebridge.enrollplus.ui.v2.components.cards.VStatCardPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VEmptyStatePremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VStateHostPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VShimmerListPremium
import com.littlebridge.enrollplus.ui.v2.components.navigation.VTopTabsPremium
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

private enum class RecordsTabPremium { Coverage, Pace, Attendance, Marks, Fee, Documents }

@Composable
fun SchoolRecordsPremium(
    initialTab: String = "Coverage",
    viewModel: SyllabusCoverageViewModel = koinViewModel(),
    recordsViewModel: SchoolRecordsViewModel = koinViewModel(),
    paceViewModel: PaceAlertsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    val recordsState by recordsViewModel.state.collectAsStateV2()
    val paceState by paceViewModel.state.collectAsStateV2()

    var tab by remember { mutableStateOf(RecordsTabPremium.entries.find { it.name == initialTab } ?: RecordsTabPremium.Coverage) }

    LaunchedEffect(tab) {
        when (tab) {
            RecordsTabPremium.Attendance -> recordsViewModel.ensureAttendance()
            RecordsTabPremium.Marks -> recordsViewModel.ensureMarks()
            RecordsTabPremium.Fee -> recordsViewModel.ensureFees()
            else -> Unit
        }
    }

    val tabLabels = listOf("Coverage", "Pace", "Attendance", "Marks", "Fee", "Documents")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .statusBarsPadding()
            .imePadding()
            .navigationBarsPadding()
            .padding(top = 24.dp, bottom = 140.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Records", style = VTypography.GreetingTitle.copy(color = VColors.OnSurface))
        VTopTabsPremium(tabs = tabLabels, selected = tabLabels[tab.ordinal], onSelect = { label -> tab = RecordsTabPremium.entries[tabLabels.indexOf(label)] })

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            when (tab) {
                RecordsTabPremium.Coverage -> {
                    VStateHostPremium(
                        loading = state.isLoading,
                        error = state.errorMessage,
                        isEmpty = state.departmentProgress.isEmpty() && state.alerts.isEmpty() && state.milestones.isEmpty(),
                        emptyTitle = "No coverage data",
                        skeleton = { VShimmerListPremium(itemCount = 4) },
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            VStatCardPremium(
                                value = "${state.overallPercentage}%",
                                label = "Overall Coverage",
                                onClick = {},
                                trend = state.overallTrend.takeIf { it.isNotBlank() },
                            )
                            state.departmentProgress.forEach { d ->
                                VListTilePremium(
                                    title = d.name,
                                    subtitle = "${(d.progress * 100).roundToInt()}% covered",
                                    onClick = {},
                                    trailingText = if (d.isDelayed) "Delayed" else "On track",
                                )
                            }
                            state.alerts.forEach { a ->
                                VListTilePremium(
                                    title = "${a.subject} - ${a.className}",
                                    subtitle = a.instructor.ifBlank { "No instructor" },
                                    onClick = {},
                                    leadingIcon = VIcons.AlertCircle,
                                    trailingText = "${a.delayPercentage}% behind",
                                )
                            }
                        }
                    }
                }
                RecordsTabPremium.Pace -> {
                    VStateHostPremium(
                        loading = paceState.isLoading,
                        error = paceState.errorMessage,
                        isEmpty = paceState.snapshots.isEmpty() && paceState.alerts.isEmpty(),
                        emptyTitle = "No pace data",
                        skeleton = { VShimmerListPremium(itemCount = 4) },
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            paceState.alerts.forEach { alert ->
                                VListTilePremium(
                                    title = "${alert.subject} - ${alert.className}-${alert.section}",
                                    subtitle = alert.message.ifBlank { alert.teacherName },
                                    onClick = { paceViewModel.resolveAlert(alert.id) },
                                    leadingIcon = VIcons.AlertCircle,
                                    trailingText = alert.level,
                                )
                            }
                            paceState.snapshots.forEach { snap ->
                                VListTilePremium(
                                    title = "${snap.subject} - ${snap.className}-${snap.section}",
                                    subtitle = "${snap.coveredTopics}/${snap.totalTopics} topics - ${snap.actualPct}%",
                                    onClick = {},
                                    trailingText = "Expected ${snap.expectedPct}%",
                                )
                            }
                        }
                    }
                }
                RecordsTabPremium.Attendance -> {
                    val ui = recordsState.attendance
                    val data = ui.data
                    VStateHostPremium(
                        loading = ui.isLoading,
                        error = ui.error,
                        isEmpty = ui.loaded && (data == null || data.total == 0),
                        emptyTitle = "No attendance data",
                        skeleton = { VShimmerListPremium(itemCount = 3) },
                    ) {
                        if (data == null) return@VStateHostPremium
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            VStatCardPremium(
                                value = "${data.rate}%",
                                label = "Present Rate",
                                onClick = {},
                                trend = "${data.present}/${data.total}",
                            )
                            data.byClass.forEach { row ->
                                VListTilePremium(
                                    title = row.grade,
                                    subtitle = "${row.present + row.late}/${row.total} present - ${row.rate}%",
                                    onClick = {},
                                )
                            }
                        }
                    }
                }
                RecordsTabPremium.Marks -> {
                    val ui = recordsState.marks
                    val data = ui.data
                    VStateHostPremium(
                        loading = ui.isLoading,
                        error = ui.error,
                        isEmpty = ui.loaded && (data == null || data.assessments.isEmpty()),
                        emptyTitle = "No assessments",
                        skeleton = { VShimmerListPremium(itemCount = 3) },
                    ) {
                        if (data == null) return@VStateHostPremium
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            VStatCardPremium(
                                value = "${data.overallAveragePct}%",
                                label = "Overall Average",
                                onClick = {},
                                trend = "${data.assessmentCount} assessments",
                            )
                            data.assessments.forEach { a ->
                                VListTilePremium(
                                    title = "${a.subject} - ${a.assessmentName}",
                                    subtitle = "${a.className} - Avg ${a.average}/${a.maxMarks}",
                                    onClick = {},
                                    trailingText = if (a.isPublished) "Published" else "Draft",
                                )
                            }
                        }
                    }
                }
                RecordsTabPremium.Fee -> {
                    val ui = recordsState.fees
                    val data = ui.data
                    VStateHostPremium(
                        loading = ui.isLoading,
                        error = ui.error,
                        isEmpty = ui.loaded && data != null && (data.paidCount + data.dueCount + data.overdueCount) == 0,
                        emptyTitle = "No fee data",
                        skeleton = { VShimmerListPremium(itemCount = 3) },
                    ) {
                        if (data == null) return@VStateHostPremium
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            VStatCardPremium(
                                value = "${data.currency} ${formatMoney(data.paidTotal)}",
                                label = "Paid (${data.paidCount})",
                                onClick = {},
                                trend = "Due: ${data.currency} ${formatMoney(data.dueTotal)}",
                                trendUp = true,
                            )
                            data.recent.forEach { f ->
                                VListTilePremium(
                                    title = f.title,
                                    subtitle = f.dueDate?.let { "Due: $it" } ?: f.category,
                                    onClick = {},
                                    trailingText = f.status,
                                )
                            }
                        }
                    }
                }
                RecordsTabPremium.Documents -> {
                    VEmptyStatePremium(
                        title = "Document Library",
                        body = "Media uploads happen via Announcements and the Academic Calendar.",
                        icon = VIcons.FileText,
                    )
                }
            }
        }
    }
}

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
