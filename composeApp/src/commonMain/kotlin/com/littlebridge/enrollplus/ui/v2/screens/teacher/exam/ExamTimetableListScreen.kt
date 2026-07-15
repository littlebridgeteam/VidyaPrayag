package com.littlebridge.enrollplus.ui.v2.screens.teacher.exam

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.exam.domain.model.ExamTimetable
import com.littlebridge.enrollplus.feature.exam.presentation.ExamTimetablesViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VBadge
import com.littlebridge.enrollplus.ui.v2.components.VBadgeTone
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonSize
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VPullRefresh
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.screens.teacher.TeacherDockClearance
import org.koin.compose.viewmodel.koinViewModel

/**
 * ExamTimetableListScreen — full-screen overlay showing all exam timetables
 * for the school. Teacher can tap a draft to review/publish, or tap "New"
 * to upload a new timetable.
 */
@Composable
fun ExamTimetableListScreen(
    onBack: () -> Unit = {},
    onNew: () -> Unit = {},
    onOpenTimetable: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ExamTimetablesViewModel = koinViewModel(),
) {
    val listState by viewModel.listState.collectAsStateV2()

    LaunchedEffect(Unit) {
        viewModel.loadTimetables()
    }

    Column(modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()) {
        VBackHeader(title = "Exam Timetables", onBack = onBack)

        VPullRefresh(
            isRefreshing = listState.isLoading && listState.timetables.isNotEmpty(),
            onRefresh = { viewModel.loadTimetables() },
        ) {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp).padding(top = 16.dp, bottom = TeacherDockClearance),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                VButton(
                    text = "New Exam Timetable",
                    onClick = onNew,
                    variant = VButtonVariant.Primary,
                    size = VButtonSize.Md,
                    full = true,
                )

                VStateHost(
                    loading = listState.isLoading,
                    error = listState.error,
                    isEmpty = listState.timetables.isEmpty(),
                    emptyTitle = "No exam timetables yet",
                    emptyBody = "Upload a timetable image or paste text to get started",
                    emptyIcon = VIcons.Calendar,
                    emptyActionLabel = "New Exam Timetable",
                    onEmptyAction = onNew,
                    onRetry = { viewModel.loadTimetables() },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        listState.timetables.forEach { tt ->
                            ExamTimetableCard(
                                timetable = tt,
                                onClick = { onOpenTimetable(tt.id) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExamTimetableCard(
    timetable: ExamTimetable,
    onClick: () -> Unit,
) {
    VCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = timetable.name,
                    style = VTypography.h2.copy(fontWeight = FontWeight.Bold),
                    color = VColors.ink,
                )
                VBadge(
                    text = timetable.status,
                    tone = if (timetable.status == "published") VBadgeTone.Success else VBadgeTone.Warning,
                )
            }
            Text(
                text = "${timetable.className} - ${timetable.section}",
                style = VTypography.body,
                color = VColors.ink2,
            )
            timetable.term?.let { term ->
                Text(
                    text = term,
                    style = VTypography.caption,
                    color = VColors.ink3,
                )
            }
            Text(
                text = "${timetable.entries.size} exams",
                style = VTypography.caption,
                color = VColors.ink3,
            )
        }
    }
}
