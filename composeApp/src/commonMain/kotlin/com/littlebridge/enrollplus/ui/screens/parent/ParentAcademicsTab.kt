package com.littlebridge.enrollplus.ui.screens.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.domain.util.UiState
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentAttendanceData
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentMarkDto
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentQuizDto
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentSyllabusSubjectDto
import com.littlebridge.enrollplus.presentation.ParentViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography

private enum class AcSubTab(val label: String) {
    Overview("Overview"), Attendance("Attendance"), Marks("Marks"),
    Syllabus("Syllabus"), Homework("Homework"), Quizzes("Quizzes"), Report("Report")
}

@Composable
fun ParentAcademicsTab(
    viewModel: ParentViewModel,
    onOverlayOpen: (ParentOverlay) -> Unit,
) {
    val attendanceState by viewModel.attendanceState.collectAsState()
    val marksState by viewModel.marksState.collectAsState()
    val syllabusState by viewModel.syllabusState.collectAsState()
    val quizListState by viewModel.quizListState.collectAsState()
    val dailySummaryState by viewModel.dailySummaryState.collectAsState()
    val trackProgressState by viewModel.trackProgressState.collectAsState()

    var subTab by rememberSaveable { mutableStateOf(AcSubTab.Overview) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(VColors.cream)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState()),
    ) {
        // Action cards
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AcActionCard(
                icon = Icons.Rounded.CalendarMonth,
                iconBg = VColors.goldSoft, iconTint = VColors.gold,
                label = "Apply for\nLeave",
                modifier = Modifier.weight(1f),
                onClick = { onOverlayOpen(ParentOverlay.Leave) },
            )
            AcActionCard(
                icon = Icons.Rounded.Favorite,
                iconBg = VColors.coralSoft, iconTint = VColors.coral,
                label = "Health\nRecords",
                modifier = Modifier.weight(1f),
                onClick = { onOverlayOpen(ParentOverlay.Health) },
            )
        }

        // Sub-tab selector — horizontally scrollable
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .horizontalScroll(rememberScrollState())
                .background(VColors.surfaceTint, VShapes.md)
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            AcSubTab.entries.forEach { st ->
                val isSelected = st == subTab
                Box(
                    modifier = Modifier
                        .background(
                            if (isSelected) VColors.white else Color.Transparent,
                            VShapes.sm,
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { subTab = st }
                        .padding(horizontal = 14.dp, vertical = 7.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = st.label,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                        color = if (isSelected) VColors.ink else VColors.ink3,
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        when (subTab) {
            AcSubTab.Overview -> AcOverviewTab(attendanceState, marksState, syllabusState, quizListState, trackProgressState)
            AcSubTab.Attendance -> AcAttendanceTab(attendanceState)
            AcSubTab.Marks -> AcMarksTab(marksState)
            AcSubTab.Syllabus -> AcSyllabusTab(syllabusState)
            AcSubTab.Homework -> AcHomeworkTab(dailySummaryState)
            AcSubTab.Quizzes -> AcQuizzesTab(quizListState)
            AcSubTab.Report -> AcReportTab(marksState)
        }

        Spacer(Modifier.height(100.dp))
    }
}

@Composable
private fun AcActionCard(icon: androidx.compose.ui.graphics.vector.ImageVector, iconBg: Color, iconTint: Color, label: String, modifier: Modifier, onClick: () -> Unit) {
    Row(
        modifier = modifier
            .background(VColors.white, VShapes.md)
            .shadow(1.dp, VShapes.md)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(modifier = Modifier.size(30.dp).background(iconBg, VShapes.sm), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(15.dp))
        }
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VColors.ink, lineHeight = 16.sp, letterSpacing = (-0.2).sp)
    }
}

@Composable
private fun AcCard(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .background(VColors.white, VShapes.md)
            .shadow(1.dp, VShapes.md)
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Text(title, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = VColors.ink, letterSpacing = (-0.2).sp)
        Spacer(Modifier.height(12.dp))
        content()
    }
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun StatRow(label: String, value: String, badge: String? = null, badgeBg: Color = VColors.surfaceTint, badgeColor: Color = VColors.ink3) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = VColors.ink2, modifier = Modifier.weight(1f))
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = VColors.ink)
        if (badge != null) {
            Spacer(Modifier.width(8.dp))
            Box(modifier = Modifier.background(badgeBg, VShapes.full).padding(horizontal = 8.dp, vertical = 2.dp)) {
                Text(badge, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = badgeColor)
            }
        }
    }
}

@Composable
private fun ProgressBar(label: String, pct: Int, color: Color) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = VColors.ink2)
            Text("$pct%", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = VColors.ink)
        }
        Spacer(Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(VColors.surfaceTint, VShapes.full),
        ) {
            Box(modifier = Modifier
                .fillMaxWidth(pct / 100f)
                .height(6.dp)
                .background(color, VShapes.full))
        }
    }
}

@Composable
private fun AcOverviewTab(
    attendanceState: UiState<ParentAttendanceData>,
    marksState: UiState<com.littlebridge.enrollplus.feature.parent.domain.model.ParentMarksData>,
    syllabusState: UiState<com.littlebridge.enrollplus.feature.parent.domain.model.ParentSyllabusData>,
    quizListState: UiState<com.littlebridge.enrollplus.feature.parent.domain.model.ParentQuizListData>,
    trackProgressState: UiState<com.littlebridge.enrollplus.feature.parent.domain.model.TrackProgressData>,
) {
    val attRate = (attendanceState as? UiState.Success)?.data?.attendanceRate ?: 0
    val marks = (marksState as? UiState.Success)?.data?.results ?: emptyList()
    val avgMarks = if (marks.isNotEmpty()) marks.mapNotNull { it.marks?.let { m -> m / it.maxMarks * 100 } }.average() else 0.0
    val quizzes = (quizListState as? UiState.Success)?.data?.quizzes ?: emptyList()
    val quizzesDone = quizzes.count { it.status.equals("COMPLETED", true) }
    val syllabusSubjects = (syllabusState as? UiState.Success)?.data?.subjects ?: emptyList()

    AcCard("Performance Summary") {
        StatRow("Attendance", "$attRate%", if (attRate > 0) "+${(attRate - 90).coerceAtLeast(0)}%" else "—",
            badgeBg = VColors.mintSoft, badgeColor = VColors.success)
        StatRow("Average Marks", String.format("%.1f", avgMarks), null)
        StatRow("Quizzes Completed", "$quizzesDone/${quizzes.size}", null)
        StatRow("Homework Submitted", "—", null)
    }
    AcCard("Syllabus Coverage") {
        val colors = listOf(VColors.violet, VColors.mint, VColors.gold, VColors.coral, VColors.sky)
        syllabusSubjects.forEachIndexed { idx, s ->
            ProgressBar(s.subject, s.progress, colors[idx % colors.size])
        }
        if (syllabusSubjects.isEmpty()) { Text("Loading syllabus...", style = VTypography.body, color = VColors.ink3) }
    }
}

@Composable
private fun AcAttendanceTab(attendanceState: UiState<ParentAttendanceData>) {
    when (val s = attendanceState) {
        is UiState.Loading -> AcCard("Loading...") { Text("Loading attendance...", style = VTypography.body, color = VColors.ink3) }
        is UiState.Error -> AcCard("Error") { Text(s.message, style = VTypography.body, color = VColors.coral) }
        is UiState.Success -> {
            val data = s.data
            AcCard("This Month") {
                // Calendar grid
                val days = data.records
                val cols = 7
                val rows = (days.size + cols - 1) / cols
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    val headers = listOf("M","T","W","T","F","S","S")
                    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        headers.forEach { h ->
                            Box(modifier = Modifier.size(28.dp), contentAlignment = Alignment.Center) {
                                Text(h, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = VColors.ink3)
                            }
                        }
                    }
                    days.chunked(cols).forEach { week ->
                        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            week.forEach { day ->
                                val (bg, fg) = when (day.status) {
                                    "present" -> VColors.mintSoft to VColors.success
                                    "absent" -> VColors.coralSoft to VColors.coral
                                    "late" -> VColors.goldSoft to VColors.gold
                                    else -> VColors.surfaceTint to VColors.ink3
                                }
                                Box(
                                    modifier = Modifier.size(28.dp).background(bg, VShapes.sm),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(day.date.takeLast(2), fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = fg)
                                }
                            }
                            repeat(cols - week.size) {
                                Box(modifier = Modifier.size(28.dp).background(VColors.surfaceTint, VShapes.sm))
                            }
                        }
                    }
                }
            }
            AcCard("Summary") {
                StatRow("Present", "${data.presentDays}", null)
                StatRow("Absent", "${data.absentDays}", null)
                StatRow("Late", "${data.lateDays}", null)
                StatRow("Attendance Rate", "${data.attendanceRate}%", null)
            }
        }
    }
}

@Composable
private fun AcMarksTab(marksState: UiState<com.littlebridge.enrollplus.feature.parent.domain.model.ParentMarksData>) {
    when (val s = marksState) {
        is UiState.Loading -> AcCard("Loading...") { Text("Loading marks...", style = VTypography.body, color = VColors.ink3) }
        is UiState.Error -> AcCard("Error") { Text(s.message, style = VTypography.body, color = VColors.coral) }
        is UiState.Success -> {
            AcCard("Recent Assessments") {
                s.data.results.forEach { mark ->
                    MarkItem(mark)
                }
                if (s.data.results.isEmpty()) { Text("No marks available", style = VTypography.body, color = VColors.ink3) }
            }
        }
    }
}

@Composable
private fun MarkItem(mark: ParentMarkDto) {
    val pct = mark.marks?.let { (it / mark.maxMarks * 100).toInt() } ?: 0
    val (grade, gradeBg, gradeColor) = when {
        pct >= 90 -> Triple("A+", VColors.mintSoft, VColors.success)
        pct >= 80 -> Triple("A", VColors.mintSoft, VColors.success)
        pct >= 70 -> Triple("B+", VColors.skySoft, VColors.sky)
        pct >= 60 -> Triple("B", VColors.skySoft, VColors.sky)
        pct >= 50 -> Triple("C+", VColors.goldSoft, VColors.gold)
        else -> Triple("C", VColors.coralSoft, VColors.coral)
    }
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(mark.subject, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VColors.ink, modifier = Modifier.weight(1f))
        Column(horizontalAlignment = Alignment.End) {
            Text("${mark.marks?.toInt() ?: 0}/${mark.maxMarks}", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = VColors.ink)
            Text(mark.examDate ?: "", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = VColors.ink3)
        }
        Spacer(Modifier.width(8.dp))
        Box(modifier = Modifier.background(gradeBg, VShapes.full).padding(horizontal = 8.dp, vertical = 2.dp)) {
            Text(grade, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = gradeColor)
        }
    }
}

@Composable
private fun AcSyllabusTab(syllabusState: UiState<com.littlebridge.enrollplus.feature.parent.domain.model.ParentSyllabusData>) {
    when (val s = syllabusState) {
        is UiState.Loading -> AcCard("Loading...") { Text("Loading syllabus...", style = VTypography.body, color = VColors.ink3) }
        is UiState.Error -> AcCard("Error") { Text(s.message, style = VTypography.body, color = VColors.coral) }
        is UiState.Success -> {
            AcCard("Subject Coverage") {
                val colors = listOf(VColors.violet, VColors.mint, VColors.gold, VColors.coral, VColors.sky)
                s.data.subjects.forEachIndexed { idx, subj ->
                    ProgressBar(subj.subject, subj.progress, colors[idx % colors.size])
                }
                if (s.data.subjects.isEmpty()) { Text("No syllabus data", style = VTypography.body, color = VColors.ink3) }
            }
        }
    }
}

@Composable
private fun AcHomeworkTab(dailySummaryState: UiState<com.littlebridge.enrollplus.feature.parent.domain.model.ParentDailySummaryData>) {
    when (val s = dailySummaryState) {
        is UiState.Loading -> AcCard("Loading...") { Text("Loading...", style = VTypography.body, color = VColors.ink3) }
        is UiState.Error -> AcCard("Error") { Text(s.message, style = VTypography.body, color = VColors.coral) }
        is UiState.Success -> {
            AcCard("Today's Classes") {
                s.data.entries.forEach { entry ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).background(VColors.mint, CircleShape))
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(entry.subject, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VColors.ink)
                            Text(entry.summaryText, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = VColors.ink2, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        }
                        Text("${entry.coveragePct}%", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = VColors.ink3)
                    }
                }
                if (s.data.entries.isEmpty()) { Text("No homework data", style = VTypography.body, color = VColors.ink3) }
            }
        }
    }
}

@Composable
private fun AcQuizzesTab(quizListState: UiState<com.littlebridge.enrollplus.feature.parent.domain.model.ParentQuizListData>) {
    when (val s = quizListState) {
        is UiState.Loading -> AcCard("Loading...") { Text("Loading quizzes...", style = VTypography.body, color = VColors.ink3) }
        is UiState.Error -> AcCard("Error") { Text(s.message, style = VTypography.body, color = VColors.coral) }
        is UiState.Success -> {
            AcCard("Recent Quizzes") {
                s.data.quizzes.forEach { quiz ->
                    val isDone = quiz.status.equals("COMPLETED", true)
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).background(if (isDone) VColors.mint else VColors.gold, CircleShape))
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(quiz.title.ifBlank { quiz.subject }, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VColors.ink)
                            Text(quiz.subject, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = VColors.ink3)
                        }
                        val (badgeText, badgeBg, badgeColor) = if (isDone) {
                            Triple("${quiz.totalMarks} marks", VColors.mintSoft, VColors.success)
                        } else {
                            Triple("Upcoming", VColors.goldSoft, VColors.gold)
                        }
                        Box(modifier = Modifier.background(badgeBg, VShapes.full).padding(horizontal = 7.dp, vertical = 2.dp)) {
                            Text(badgeText, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = badgeColor)
                        }
                    }
                }
                if (s.data.quizzes.isEmpty()) { Text("No quizzes available", style = VTypography.body, color = VColors.ink3) }
            }
        }
    }
}

@Composable
private fun AcReportTab(marksState: UiState<com.littlebridge.enrollplus.feature.parent.domain.model.ParentMarksData>) {
    when (val s = marksState) {
        is UiState.Loading -> AcCard("Loading...") { Text("Loading...", style = VTypography.body, color = VColors.ink3) }
        is UiState.Error -> AcCard("Error") { Text(s.message, style = VTypography.body, color = VColors.coral) }
        is UiState.Success -> {
            AcCard("Report Cards") {
                s.data.results.take(5).forEach { mark ->
                    MarkItem(mark)
                }
                if (s.data.results.isEmpty()) { Text("No reports available", style = VTypography.body, color = VColors.ink3) }
            }
        }
    }
}
