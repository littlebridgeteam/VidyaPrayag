package com.littlebridge.enrollplus.ui.v2.screens.parent

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentAttendanceData
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentDailySummaryData
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentMarksData
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentQuizDetailData
import com.littlebridge.enrollplus.feature.parent.domain.model.DashboardChildSummary
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentQuizDto
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentSyllabusData
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentSyllabusV2Data
import com.littlebridge.enrollplus.feature.parent.domain.model.QuizLeaderboardData
import com.littlebridge.enrollplus.feature.parent.presentation.AcademicCompetency
import com.littlebridge.enrollplus.feature.parent.presentation.AchievementBadge
import com.littlebridge.enrollplus.feature.parent.presentation.ParentAcademicsState
import com.littlebridge.enrollplus.feature.parent.presentation.ParentAcademicsViewModel
import com.littlebridge.enrollplus.feature.parent.presentation.TrackProgressState
import com.littlebridge.enrollplus.feature.parent.presentation.TrackProgressViewModel
import com.littlebridge.enrollplus.feature.teacher.domain.model.QuizSubmitResponse
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VProgressBar
import com.littlebridge.enrollplus.ui.v2.theme.VMotion
import com.littlebridge.enrollplus.ui.v2.locale.appString
import org.koin.compose.viewmodel.koinViewModel
import com.littlebridge.enrollplus.ui.v2.theme.VTheme
import androidx.compose.foundation.shape.RoundedCornerShape

// ═══════════════════════════════════════════════════════════════════════════════
// SCREEN ENTRY POINT
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun ParentAcademicsScreenV2(
    modifier: Modifier = Modifier,
    parentName: String = "",
    children: List<DashboardChildSummary> = emptyList(),
    selectedChild: DashboardChildSummary? = null,
    onSelectChild: (String) -> Unit = {},
    onOpenLeave: () -> Unit = {},
    onOpenHealth: () -> Unit = {},
    onOpenNotifications: () -> Unit = {},
    initialTab: String? = null,
    onTabConsumed: () -> Unit = {},
    initialReportDraftId: String? = null,
    onReportDraftIdConsumed: () -> Unit = {},
    unreadNotificationsCount: Int = 0,
    viewModel: TrackProgressViewModel = koinViewModel(),
    academicsViewModel: ParentAcademicsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val academics by academicsViewModel.state.collectAsState()

    ParentAcademicsContent(
        state = state,
        academics = academics,
        parentName = parentName,
        children = children,
        selectedChild = selectedChild,
        onSelectChild = {
            onSelectChild(it)
            academicsViewModel.selectChild(it)
        },
        onLoadAttendance = { academicsViewModel.loadAttendance() },
        onLoadMarks = { academicsViewModel.loadMarks() },
        onLoadSyllabus = { academicsViewModel.loadSyllabus() },
        onLoadSyllabusV2 = { academicsViewModel.loadSyllabusV2() },
        onLoadDailySummary = { academicsViewModel.loadDailySummary() },
        onLoadQuizzes = { academicsViewModel.loadQuizzes() },
        onOpenQuiz = { academicsViewModel.loadQuizDetail(it) },
        onViewQuizResult = { academicsViewModel.loadQuizResult(it) },
        onSubmitQuiz = { id, ans, txt -> academicsViewModel.submitQuiz(id, ans, txt) },
        onClearQuizResult = { academicsViewModel.clearQuizResult() },
        onLoadLeaderboard = { academicsViewModel.loadLeaderboard(it) },
        onOpenLeave = onOpenLeave,
        onOpenHealth = onOpenHealth,
        onOpenNotifications = onOpenNotifications,
        unreadNotificationsCount = unreadNotificationsCount,
        initialTab = initialTab,
        onTabConsumed = onTabConsumed,
        initialReportDraftId = initialReportDraftId,
        onReportDraftIdConsumed = onReportDraftIdConsumed,
        modifier = modifier,
    )
}

// ═══════════════════════════════════════════════════════════════════════════════
// CONTENT SHELL — matches onboarding's structural hierarchy
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun ParentAcademicsContent(
    state: TrackProgressState,
    academics: ParentAcademicsState,
    parentName: String,
    children: List<DashboardChildSummary>,
    selectedChild: DashboardChildSummary?,
    onLoadAttendance: () -> Unit,
    onLoadMarks: () -> Unit,
    onLoadSyllabus: () -> Unit,
    onLoadSyllabusV2: () -> Unit,
    onLoadDailySummary: () -> Unit,
    onLoadQuizzes: () -> Unit,
    onOpenQuiz: (String) -> Unit,
    onViewQuizResult: (String) -> Unit,
    onSubmitQuiz: (String, List<Pair<String, Int>>, Map<String, String>) -> Unit,
    onClearQuizResult: () -> Unit,
    onLoadLeaderboard: (String) -> Unit,
    onSelectChild: (String) -> Unit = {},
    onOpenLeave: () -> Unit = {},
    onOpenHealth: () -> Unit = {},
    onOpenNotifications: () -> Unit = {},
    unreadNotificationsCount: Int = 0,
    initialTab: String? = null,
    onTabConsumed: () -> Unit = {},
    initialReportDraftId: String? = null,
    onReportDraftIdConsumed: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val staticTabs = listOf("Overview", "Attendance", "Marks", "Syllabus", "Quizzes", "Homework")
    var tab by remember { mutableStateOf("Overview") }
    val showReport = tab == "Report" || initialReportDraftId != null
    val visibleTabs = remember(staticTabs, showReport) {
        staticTabs + if (showReport) listOf("Report") else emptyList()
    }

    LaunchedEffect(initialTab) {
        if (initialTab != null) {
            tab = initialTab
            onTabConsumed()
        }
    }

    LaunchedEffect(initialReportDraftId) {
        if (initialReportDraftId != null && tab != "Report") {
            tab = "Report"
        }
    }

    LaunchedEffect(tab, academics.selectedChildId) {
        when (tab) {
            "Attendance" -> if (academics.attendance == null && !academics.attendanceLoading) onLoadAttendance()
            "Marks" -> if (academics.marks == null && !academics.marksLoading) onLoadMarks()
            "Syllabus" -> {
                if (academics.syllabus == null && !academics.syllabusLoading) onLoadSyllabus()
                if (academics.syllabusV2 == null && !academics.syllabusV2Loading) onLoadSyllabusV2()
            }
            "Quizzes" -> if (academics.quizzes.isEmpty() && !academics.quizzesLoading) onLoadQuizzes()
            "Homework" -> if (academics.dailySummary == null && !academics.dailySummaryLoading) onLoadDailySummary()
        }
    }

    Column(
        modifier
            .fillMaxSize()
            .background(VTheme.colors.cream)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 100.dp),
    ) {
        // ── Shared portal header (same as Home) ──
        PortalTopHeader(
            parentName = parentName,
            childName = selectedChild?.name?.ifBlank { null } ?: "Your Child",
            children = children,
            selectedChild = selectedChild,
            onSelectChild = onSelectChild,
            onOpenNotifications = onOpenNotifications,
            unreadNotificationsCount = unreadNotificationsCount,
        )

        // ── Tab chips row (white selected pill like screenshot) ──
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(visibleTabs) { label ->
                PortalTabChip(
                    label = label,
                    selected = tab == label,
                    onClick = { tab = label },
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Animated content (subtle slide like onboarding: it/4, not full it) ──
        AnimatedContent(
            targetState = tab,
            transitionSpec = {
                val forward = visibleTabs.indexOf(targetState) > visibleTabs.indexOf(initialState)
                val dur = 280
                val enter = slideInHorizontally(
                    animationSpec = tween(dur),
                    initialOffsetX = { if (forward) it / 4 else -it / 4 },
                ) + fadeIn(tween(dur))
                val exit = slideOutHorizontally(
                    animationSpec = tween(dur),
                    targetOffsetX = { if (forward) -it / 4 else it / 4 },
                ) + fadeOut(tween(dur))
                enter togetherWith exit
            },
            label = "academics-tab",
        ) { currentTab ->
            Column(
                Modifier.padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                when (currentTab) {
                    "Overview" -> OverviewTab(
                        state = state,
                        academics = academics,
                        onOpenLeave = onOpenLeave,
                        onOpenHealth = onOpenHealth,
                        onOpenReport = {
                            val childId = academics.selectedChildId
                            if (childId != null) {
                                tab = "Report"
                            }
                        },
                        onOpenSyllabus = { tab = "Syllabus" },
                    )
                    "Attendance" -> AttendanceTab(academics, onLoadAttendance)
                    "Marks" -> MarksTab(academics, onLoadMarks)
                    "Syllabus" -> SyllabusTab(academics, onLoadSyllabus, onLoadSyllabusV2)
                    "Quizzes" -> QuizzesTab(
                        academics = academics,
                        onRetry = onLoadQuizzes,
                        onOpenQuiz = onOpenQuiz,
                        onViewQuizResult = onViewQuizResult,
                        onSubmitQuiz = onSubmitQuiz,
                        onBackToList = onClearQuizResult,
                        onLoadLeaderboard = onLoadLeaderboard,
                    )
                    "Homework" -> HomeworkTab(academics, onLoadDailySummary)
                    "Report" -> {
                        val childId = academics.selectedChildId
                        if (childId != null) {
                            ParentReportScreen(
                                childId = childId,
                                onBack = { tab = "Overview" },
                                initialDraftId = initialReportDraftId,
                                onDraftIdConsumed = onReportDraftIdConsumed,
                            )
                        }
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// OVERVIEW — Premium academic snapshot
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun OverviewTab(
    state: TrackProgressState,
    academics: ParentAcademicsState,
    onOpenLeave: () -> Unit,
    onOpenHealth: () -> Unit,
    onOpenReport: () -> Unit,
    onOpenSyllabus: () -> Unit,
) {
    if (state.isLoading) {
        LoadingState()
        return
    }
    state.error?.let {
        ErrorState(message = it)
        return
    }

    val hasData = state.academicCompetencies.isNotEmpty() ||
        state.emotionalIntelligence.isNotEmpty() ||
        state.emotionalDescription.isNotBlank() ||
        state.currentLevel > 0

    if (!hasData) {
        EmptyState("No Progress Yet", "Academic progress data will appear here once available.")
        return
    }

    val attendanceRate = academics.attendance?.attendanceRate
    val averageScore = academics.marks?.results?.mapNotNull { m ->
        m.marks?.let { if (m.maxMarks > 0) (it / m.maxMarks * 100).toInt() else null }
    }?.average()?.toInt()
    val syllabusProgress = academics.syllabusV2?.subjects?.map { it.progress }?.average()?.toInt()
        ?: academics.syllabus?.subjects?.map { it.progress }?.average()?.toInt()

    // ── Academic Snapshot Card (premium identity + progress + stats in one) ──
    AcademicSnapshotCard(
        childName = academics.selectedChild?.name?.ifBlank { null } ?: "Your Child",
        progressPct = (state.overallProgress * 100f).toInt(),
        level = state.currentLevel,
        journey = state.journeyDescription,
        attendanceRate = attendanceRate,
        averageScore = averageScore,
        syllabusProgress = syllabusProgress,
    )

    // ── Academic competencies ──
    if (state.academicCompetencies.isNotEmpty()) {
        SectionLabel("Academic Competencies")
        val palette = subjectPalette
        state.academicCompetencies.forEachIndexed { idx, comp ->
            val tone = palette[idx % palette.size]
            CompetencyCard(comp, tone)
        }
    }

    // ── Emotional intelligence ──
    if (state.emotionalIntelligence.isNotEmpty() || state.emotionalDescription.isNotBlank()) {
        SectionLabel("Emotional Intelligence")
        EmotionalIntelligenceCard(
            description = state.emotionalDescription,
            metrics = state.emotionalIntelligence,
        )
    }

    // ── Achievement badges ──
    if (state.badges.isNotEmpty()) {
        SectionLabel("Achievements")
        BadgesRow(state.badges)
    }

    // ── Quick actions (2-column grid, not stacked list) ──
    SectionLabel("Quick Actions")
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        QuickActionTile(
            icon = Icons.Filled.Description,
            iconColor = VTheme.colors.violet,
            title = "Report Card",
            onClick = onOpenReport,
            modifier = Modifier.weight(1f),
        )
        QuickActionTile(
            icon = Icons.Filled.CalendarMonth,
            iconColor = VTheme.colors.coral,
            title = "Apply Leave",
            onClick = onOpenLeave,
            modifier = Modifier.weight(1f),
        )
    }
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        QuickActionTile(
            icon = Icons.Filled.Favorite,
            iconColor = VTheme.colors.error,
            title = "Health Records",
            onClick = onOpenHealth,
            modifier = Modifier.weight(1f),
        )
        QuickActionTile(
            icon = Icons.Filled.School,
            iconColor = VTheme.colors.gold,
            title = "Syllabus",
            onClick = onOpenSyllabus,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun AcademicSnapshotCard(
    childName: String,
    progressPct: Int,
    level: Int,
    journey: String,
    attendanceRate: Int?,
    averageScore: Int?,
    syllabusProgress: Int?,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(VTheme.colors.surfaceCard, RoundedCornerShape(18.dp))
            .border(1.dp, VTheme.colors.line, RoundedCornerShape(18.dp))
            .padding(20.dp),
    ) {
        // ── Identity row ──
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            SimpleAvatar(name = childName, size = 44.dp)
            Column(Modifier.weight(1f)) {
                Text(
                    childName,
                    style = VTheme.type.h3.copy(fontSize = 16.sp),
                    color = VTheme.colors.ink,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(4.dp))
                if (level > 0) {
                    MiniBadge(text = "Level $level", color = VTheme.colors.violet, bg = VTheme.colors.violetSoft)
                } else {
                    Text("Academic Overview", style = VTheme.type.caption, color = VTheme.colors.ink3)
                }
            }
            Text(
                "$progressPct%",
                style = VTheme.type.h2.copy(fontSize = 24.sp),
                color = VTheme.colors.violet,
                fontWeight = FontWeight.ExtraBold,
            )
        }

        // ── Progress bar ──
        Spacer(Modifier.height(14.dp))
        VProgressBar(value = progressPct, height = 6.dp)

        // ── Journey description ──
        if (journey.isNotBlank()) {
            Spacer(Modifier.height(12.dp))
            Text(
                journey,
                style = VTheme.type.body.copy(fontSize = 13.sp),
                color = VTheme.colors.ink2,
            )
        }

        // ── Inline mini-stats (divider + 3-column row) ──
        Spacer(Modifier.height(16.dp))
        HorizontalDivider(color = VTheme.colors.lineSoft)
        Spacer(Modifier.height(14.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            InlineStat(
                value = attendanceRate?.let { "$it%" } ?: "—",
                label = "Attendance",
                modifier = Modifier.weight(1f),
            )
            InlineStat(
                value = averageScore?.let { "$it%" } ?: "—",
                label = "Avg Score",
                modifier = Modifier.weight(1f),
            )
            InlineStat(
                value = syllabusProgress?.let { "$it%" } ?: "—",
                label = "Syllabus",
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun InlineStat(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            value,
            style = VTheme.type.h3.copy(fontSize = 16.sp),
            color = VTheme.colors.ink,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(2.dp))
        Text(label, style = VTheme.type.caption.copy(fontSize = 10.sp), color = VTheme.colors.ink3)
    }
}

@Composable
private fun QuickActionTile(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .clip(RoundedCornerShape(18.dp))
            .background(VTheme.colors.surfaceCard)
            .border(1.dp, VTheme.colors.line, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(iconColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = "", tint = iconColor, modifier = Modifier.size(18.dp))
        }
        Text(title, style = VTheme.type.caption.copy(fontWeight = FontWeight.SemiBold), color = VTheme.colors.ink)
    }
}

@Composable
private fun CompetencyCard(comp: AcademicCompetency, tone: Color) {
    CreamCard {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(tone.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    comp.title.take(2).uppercase(),
                    style = VTheme.type.label.copy(fontWeight = FontWeight.ExtraBold, fontSize = 11.sp),
                    color = tone,
                )
            }
            Column(Modifier.weight(1f)) {
                Text(comp.title, style = VTheme.type.body, color = VTheme.colors.ink, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(6.dp))
                TintedBar((comp.progress * 100f), tone)
            }
            Text(
                "${(comp.progress * 100f).toInt()}%",
                style = VTheme.type.body.copy(fontWeight = FontWeight.Bold),
                color = tone,
            )
        }
    }
}

@Composable
private fun EmotionalIntelligenceCard(
    description: String,
    metrics: Map<String, Float>,
) {
    CreamCard {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(Icons.Filled.Spa, contentDescription = "", tint = VTheme.colors.violet, modifier = Modifier.size(18.dp))
            Text("Emotional Intelligence", style = VTheme.type.label, color = VTheme.colors.ink, fontWeight = FontWeight.Bold)
        }
        if (description.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(description, style = VTheme.type.body, color = VTheme.colors.ink2)
        }
        if (metrics.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            metrics.entries.forEachIndexed { idx, (trait, score) ->
                val tone = subjectPalette[(idx + 1) % subjectPalette.size]
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(trait, style = VTheme.type.caption, color = VTheme.colors.ink2)
                    Text("${(score * 100f).toInt()}%", style = VTheme.type.caption.copy(fontWeight = FontWeight.Bold), color = tone)
                }
                TintedBar(score * 100f, tone, height = 6.dp)
            }
        }
    }
}

@Composable
private fun BadgesRow(badges: List<AchievementBadge>) {
    LazyRow(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(badges) { badge ->
            BadgeChip(badge)
        }
    }
}

@Composable
private fun BadgeChip(badge: AchievementBadge) {
    val alpha = if (badge.isLocked) 0.3f else 1f
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.width(72.dp),
    ) {
        Box(
            Modifier.size(56.dp).clip(CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            val colors = badge.gradientColors.mapNotNull { runCatching { it.hexToColor() }.getOrNull() }
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        if (colors.isNotEmpty()) Brush.linearGradient(colors) else Brush.linearGradient(listOf(VTheme.colors.violet, VTheme.colors.violetSoft))
                    ),
            )
            Icon(
                if (badge.isLocked) Icons.Filled.School else Icons.Filled.Insights,
                contentDescription = "",
                tint = Color.White.copy(alpha = alpha),
                modifier = Modifier.size(24.dp),
            )
        }
        Text(
            badge.title,
            style = VTheme.type.caption.copy(fontSize = 10.sp),
            color = VTheme.colors.ink2.copy(alpha = alpha),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// ATTENDANCE TAB
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun AttendanceTab(academics: ParentAcademicsState, onRetry: () -> Unit) {
    if (academics.attendanceLoading) {
        LoadingState()
        return
    }
    academics.attendanceError?.let {
        ErrorState(message = it, onRetry = onRetry)
        return
    }

    val data = academics.attendance
    val hasData = data != null && data.totalDays > 0

    if (hasData && data != null) {
        CreamCard {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f)) {
                    Text("This Term", style = VTheme.type.label, color = VTheme.colors.ink3)
                    Text("Attendance Rate", style = VTheme.type.body.copy(fontWeight = FontWeight.Bold), color = VTheme.colors.ink, modifier = Modifier.padding(top = 2.dp))
                }
                Text(
                    "${data.attendanceRate}%",
                    style = VTheme.type.h3.copy(fontWeight = FontWeight.ExtraBold),
                    color = if (data.attendanceRate >= 75) VTheme.colors.success else VTheme.colors.gold,
                )
            }
            Spacer(Modifier.height(8.dp))
            VProgressBar(value = data.attendanceRate, height = 8.dp)
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AttendanceStat("Present", data.presentDays, VTheme.colors.success, Modifier.weight(1f))
                AttendanceStat("Late", data.lateDays, VTheme.colors.gold, Modifier.weight(1f))
                AttendanceStat("Absent", data.absentDays, VTheme.colors.error, Modifier.weight(1f))
            }
        }
    } else {
        CreamCard {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Box(
                    Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(VTheme.colors.success.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.CalendarMonth, contentDescription = "", tint = VTheme.colors.success, modifier = Modifier.size(20.dp))
                }
                Column(Modifier.weight(1f)) {
                    Text("No Attendance Records", style = VTheme.type.body, color = VTheme.colors.ink, fontWeight = FontWeight.SemiBold)
                    Text("Attendance data will appear here once available.", style = VTheme.type.caption, color = VTheme.colors.ink3)
                }
            }
        }
    }

    ParentAttendanceCalendar(records = data?.records ?: emptyList())
}

@Composable
private fun AttendanceStat(label: String, count: Int, accent: Color, modifier: Modifier = Modifier) {
    Column(
        modifier
            .clip(RoundedCornerShape(14.dp))
            .background(accent.copy(alpha = 0.10f))
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("$count", style = VTheme.type.h3.copy(fontSize = 18.sp), color = accent, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(2.dp))
        Text(label, style = VTheme.type.caption.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold), color = VTheme.colors.ink2)
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// MARKS TAB
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun MarksTab(academics: ParentAcademicsState, onRetry: () -> Unit) {
    if (academics.marksLoading) {
        LoadingState()
        return
    }
    academics.marksError?.let {
        ErrorState(message = it, onRetry = onRetry)
        return
    }

    val data = academics.marks
    if (data == null || data.results.isEmpty()) {
        EmptyState("No Marks Yet", "Exam results will appear here once published.")
        return
    }

    data.results.forEachIndexed { idx, m ->
        val marksValue = m.marks
        val pct = if (marksValue != null && m.maxMarks > 0) (marksValue / m.maxMarks * 100.0) else null
        val gradeColor = when {
            pct == null -> VTheme.colors.ink3
            pct >= 75 -> VTheme.colors.success
            pct >= 40 -> VTheme.colors.gold
            else -> VTheme.colors.error
        }
        val subjectColor = subjectPalette[idx % subjectPalette.size]

        CreamCard {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(subjectColor.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        m.subject.take(2).uppercase(),
                        style = VTheme.type.label.copy(fontWeight = FontWeight.ExtraBold, fontSize = 12.sp),
                        color = subjectColor,
                    )
                }
                Column(Modifier.weight(1f)) {
                    Text(m.examName, style = VTheme.type.body, color = VTheme.colors.ink, fontWeight = FontWeight.SemiBold)
                    Text(m.subject, style = VTheme.type.caption, color = VTheme.colors.ink3)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        if (marksValue != null) "${marksValue.toInt()} / ${m.maxMarks}" else "—",
                        style = VTheme.type.body.copy(fontWeight = FontWeight.Bold),
                        color = gradeColor,
                    )
                    if (pct != null) {
                        MiniBadge(
                            text = "${pct.toInt()}%",
                            color = gradeColor,
                            bg = gradeColor.copy(alpha = 0.12f),
                        )
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// SYLLABUS TAB
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun SyllabusTab(academics: ParentAcademicsState, onRetry: () -> Unit, onRetryV2: () -> Unit) {
    val v2Data = academics.syllabusV2
    val legacyData = academics.syllabus
    val isLoading = academics.syllabusV2Loading || academics.syllabusLoading
    val error = academics.syllabusV2Error ?: academics.syllabusError

    if (isLoading) {
        LoadingState()
        return
    }
    error?.let {
        ErrorState(message = it, onRetry = onRetryV2)
        return
    }

    val isEmpty = (v2Data == null || v2Data.subjects.isEmpty()) &&
        (legacyData == null || legacyData.subjects.isEmpty())

    if (isEmpty) {
        EmptyState("No Syllabus Data", "Syllabus progress will appear here once available.")
        return
    }

    val palette = subjectPalette

    if (v2Data != null && v2Data.subjects.isNotEmpty()) {
        v2Data.subjects.forEachIndexed { idx, subj ->
            val tone = palette[idx % palette.size]
            val displayProgress = if (subj.isAiEstimated && subj.progress == 0) subj.estimatedPct else subj.progress
            var expanded by remember { mutableStateOf(idx == 0) }
            SyllabusSubjectCard(
                subjectName = subj.subject,
                progress = displayProgress,
                tone = tone,
                isAiEstimated = subj.isAiEstimated,
                estimatedPct = subj.estimatedPct,
                expanded = expanded,
                onToggle = { expanded = !expanded },
                units = subj.units.filter { it.depth <= 1 }.map { SyllabusUnitItem(it.title, it.isCovered, it.coveredOn, it.isAiEstimated) },
            )
        }
    } else if (legacyData != null) {
        legacyData.subjects.forEachIndexed { idx, subj ->
            val tone = palette[idx % palette.size]
            var expanded by remember { mutableStateOf(idx == 0) }
            SyllabusSubjectCard(
                subjectName = subj.subject,
                progress = subj.progress,
                tone = tone,
                isAiEstimated = false,
                estimatedPct = 0,
                expanded = expanded,
                onToggle = { expanded = !expanded },
                units = subj.units.map { SyllabusUnitItem(it.title, it.isCovered, it.coveredOn, false) },
            )
        }
    }
}

private data class SyllabusUnitItem(
    val title: String,
    val isCovered: Boolean,
    val coveredOn: String?,
    val isAiEstimated: Boolean,
)

@Composable
private fun SyllabusSubjectCard(
    subjectName: String,
    progress: Int,
    tone: Color,
    isAiEstimated: Boolean,
    estimatedPct: Int,
    expanded: Boolean,
    onToggle: () -> Unit,
    units: List<SyllabusUnitItem>,
) {
    CreamCard {
        Row(
            Modifier.fillMaxWidth()
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onToggle() },
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(tone.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    subjectName.take(2).uppercase(),
                    style = VTheme.type.label.copy(fontWeight = FontWeight.ExtraBold, fontSize = 11.sp),
                    color = tone,
                )
            }
            Text(subjectName, style = VTheme.type.body, color = VTheme.colors.ink, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            if (isAiEstimated) {
                MiniBadge(text = "AI", color = VTheme.colors.violet, bg = VTheme.colors.violetSoft)
                Spacer(Modifier.width(4.dp))
            }
            Text("$progress%", style = VTheme.type.body.copy(fontWeight = FontWeight.Bold), color = tone)
            Icon(
                if (expanded) Icons.Filled.ExpandMore else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = if (expanded) "Collapse" else "Expand",
                tint = VTheme.colors.ink3,
                modifier = Modifier.size(18.dp),
            )
        }
        Spacer(Modifier.height(10.dp))
        TintedBar(progress.toFloat(), tone, height = 8.dp)
        if (isAiEstimated && estimatedPct > 0) {
            Spacer(Modifier.height(4.dp))
            Text(
                "Teacher hasn't updated progress. Estimated based on scheduled classes.",
                style = VTheme.type.caption.copy(fontSize = 10.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                color = VTheme.colors.ink3,
            )
        }
        if (expanded && units.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = VTheme.colors.lineSoft)
            Spacer(Modifier.height(8.dp))
            units.forEach { u ->
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        u.title,
                        style = VTheme.type.caption,
                        color = if (u.isCovered) VTheme.colors.ink else VTheme.colors.ink2,
                        modifier = Modifier.weight(1f),
                    )
                    if (u.isCovered) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            if (u.isAiEstimated) {
                                Text("EST", style = VTheme.type.caption.copy(fontWeight = FontWeight.SemiBold, fontSize = 10.sp), color = VTheme.colors.violet)
                            } else {
                                Icon(Icons.Filled.Check, contentDescription = "", tint = VTheme.colors.success, modifier = Modifier.size(13.dp))
                                Text(u.coveredOn ?: "Covered", style = VTheme.type.caption.copy(fontWeight = FontWeight.SemiBold), color = VTheme.colors.success)
                            }
                        }
                    } else {
                        Text("Pending", style = VTheme.type.caption.copy(fontWeight = FontWeight.SemiBold), color = VTheme.colors.gold)
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// QUIZZES TAB
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun QuizzesTab(
    academics: ParentAcademicsState,
    onRetry: () -> Unit,
    onOpenQuiz: (String) -> Unit,
    onViewQuizResult: (String) -> Unit,
    onSubmitQuiz: (String, List<Pair<String, Int>>, Map<String, String>) -> Unit,
    onBackToList: () -> Unit,
    onLoadLeaderboard: (String) -> Unit,
) {
    if (academics.quizDetail != null) {
        QuizDetailCard(academics, onSubmitQuiz, onBackToList)
        return
    }
    if (academics.quizResult != null) {
        QuizResultCard(academics, onBackToList, onLoadLeaderboard)
        return
    }

    if (academics.quizzesLoading) {
        LoadingState()
        return
    }
    academics.quizzesError?.let {
        ErrorState(message = it, onRetry = onRetry)
        return
    }

    if (academics.quizzes.isEmpty()) {
        EmptyState("No Quizzes", "Quizzes will appear here when assigned by the teacher.")
        return
    }

    academics.quizzes.forEach { quiz ->
        CreamCard(
            modifier = Modifier.clickable {
                when (quiz.status) {
                    "PUBLISHED" -> onOpenQuiz(quiz.id)
                    "SUBMITTED" -> onViewQuizResult(quiz.id)
                }
            },
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(VTheme.colors.violet.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.Quiz, contentDescription = "", tint = VTheme.colors.violet, modifier = Modifier.size(20.dp))
                }
                Column(Modifier.weight(1f)) {
                    Text(quiz.title.ifBlank { "Quiz" }, style = VTheme.type.body, color = VTheme.colors.ink, fontWeight = FontWeight.SemiBold)
                    Text(
                        "${quiz.subject} · ${quiz.numQuestions} questions",
                        style = VTheme.type.caption,
                        color = VTheme.colors.ink3,
                    )
                }
                when (quiz.status) {
                    "PUBLISHED" -> {
                        MiniBadge(text = "Start", color = VTheme.colors.violet, bg = VTheme.colors.violetSoft)
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Open", tint = VTheme.colors.violet, modifier = Modifier.size(16.dp))
                    }
                    "SUBMITTED" -> {
                        MiniBadge(text = "Result", color = VTheme.colors.success, bg = VTheme.colors.successSoft)
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "View", tint = VTheme.colors.violet, modifier = Modifier.size(16.dp))
                    }
                    else -> MiniBadge(text = "Pending", color = VTheme.colors.ink3, bg = VTheme.colors.surfaceTint)
                }
            }
        }
    }
}

@Composable
private fun QuizDetailCard(
    academics: ParentAcademicsState,
    onSubmit: (String, List<Pair<String, Int>>, Map<String, String>) -> Unit,
    onBack: () -> Unit,
) {
    val detail = academics.quizDetail ?: return
    val answers = remember { mutableStateMapOf<String, Int>() }
    val textAnswers = remember { mutableStateMapOf<String, String>() }

    CreamCard {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                Modifier.size(32.dp).clip(CircleShape).background(VTheme.colors.creamDeep)
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onBack() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = VTheme.colors.ink2, modifier = Modifier.size(18.dp))
            }
            Icon(Icons.Filled.Quiz, contentDescription = "", tint = VTheme.colors.violet, modifier = Modifier.size(20.dp))
            Text(detail.title.ifBlank { "Quiz" }, style = VTheme.type.body, color = VTheme.colors.ink, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            Text(detail.subject, style = VTheme.type.caption, color = VTheme.colors.ink3)
        }
        Spacer(Modifier.height(16.dp))

        Column(
            Modifier.fillMaxWidth().heightIn(max = 420.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            detail.questions.forEachIndexed { qIdx, q ->
                Column {
                    val typeLabel = when (q.questionType) {
                        "TRUE_FALSE" -> " (True/False)"
                        "FILL_BLANK" -> " (Fill in the blank)"
                        "MATCH" -> " (Match)"
                        else -> ""
                    }
                    Text("${qIdx + 1}. ${q.question}$typeLabel", style = VTheme.type.body.copy(fontSize = 14.sp, fontWeight = FontWeight.SemiBold), color = VTheme.colors.ink)
                    Spacer(Modifier.height(8.dp))

                    when (q.questionType) {
                        "FILL_BLANK" -> {
                            OutlinedTextField(
                                value = textAnswers[q.id] ?: "",
                                onValueChange = { textAnswers[q.id] = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Type your answer...", style = VTheme.type.body.copy(fontSize = 13.sp), color = VTheme.colors.ink3) },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = VTheme.colors.violet,
                                    unfocusedBorderColor = VTheme.colors.line,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = VTheme.colors.creamDeep,
                                ),
                            )
                        }
                        else -> {
                            val options = if (q.questionType == "TRUE_FALSE" && q.options.isEmpty()) listOf("True", "False") else q.options
                            options.forEachIndexed { optIdx, opt ->
                                val selected = answers[q.id] == optIdx
                                Row(
                                    Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (selected) VTheme.colors.violetSoft else VTheme.colors.creamDeep)
                                        .border(1.dp, if (selected) VTheme.colors.violet else VTheme.colors.line, RoundedCornerShape(10.dp))
                                        .clickable {
                                            answers[q.id] = optIdx
                                            if (q.questionType == "TRUE_FALSE") textAnswers[q.id] = opt
                                        }
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Box(
                                        Modifier.size(20.dp).clip(CircleShape)
                                            .background(if (selected) VTheme.colors.violet else VTheme.colors.surfaceCard)
                                            .border(1.dp, if (selected) VTheme.colors.violet else VTheme.colors.line, CircleShape),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        if (selected) Icon(Icons.Filled.Check, contentDescription = "", tint = VTheme.colors.white, modifier = Modifier.size(12.dp))
                                    }
                                    Text(opt, style = VTheme.type.body.copy(fontSize = 13.sp), color = VTheme.colors.ink)
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        val quizErr = academics.quizSubmitError
        if (quizErr != null) {
            Text(quizErr, style = VTheme.type.caption, color = VTheme.colors.error)
            Spacer(Modifier.height(8.dp))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            VButton("Back", onClick = onBack, modifier = Modifier.weight(1f), variant = VButtonVariant.Ghost)
            VButton("Clear", onClick = { answers.clear(); textAnswers.clear() }, modifier = Modifier.weight(1f), variant = VButtonVariant.Ghost)
            VButton(
                "Submit",
                onClick = {
                    val ans = answers.entries.map { it.key to it.value }
                    val txt = textAnswers.toMap()
                    if (ans.isNotEmpty() || txt.isNotEmpty()) onSubmit(detail.id, ans, txt)
                },
                modifier = Modifier.weight(1f),
                loading = academics.isSubmittingQuiz,
            )
        }
    }
}

@Composable
private fun QuizResultCard(
    academics: ParentAcademicsState,
    onBack: () -> Unit,
    onLoadLeaderboard: (String) -> Unit,
) {
    val result = academics.quizResult?.data ?: return

    LaunchedEffect(result.quizId) { onLoadLeaderboard(result.quizId) }

    CreamCard {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Box(
                Modifier.size(64.dp).clip(CircleShape).background(VTheme.colors.violetSoft),
                contentAlignment = Alignment.Center,
            ) {
                Text("${result.percentage}%", style = VTheme.type.h3.copy(fontSize = 20.sp), color = VTheme.colors.violet, fontWeight = FontWeight.ExtraBold)
            }
            Spacer(Modifier.height(12.dp))
            Text("Score: ${result.score} / ${result.totalMarks}", style = VTheme.type.body, color = VTheme.colors.ink, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(16.dp))

            Column(
                Modifier.fillMaxWidth().heightIn(max = 350.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                result.questionResults.forEachIndexed { idx, qr ->
                    Column(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                            .background(if (qr.correct) VTheme.colors.success.copy(alpha = 0.06f) else VTheme.colors.error.copy(alpha = 0.06f))
                            .border(1.dp, if (qr.correct) VTheme.colors.success.copy(alpha = 0.2f) else VTheme.colors.error.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                            .padding(10.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(if (qr.correct) Icons.Filled.Check else Icons.Filled.Close, contentDescription = "", tint = if (qr.correct) VTheme.colors.success else VTheme.colors.error, modifier = Modifier.size(16.dp))
                            Text("${idx + 1}. ${qr.question}", style = VTheme.type.body.copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold), color = VTheme.colors.ink)
                        }
                        Spacer(Modifier.height(6.dp))
                        if (qr.selectedAnswer.isNotBlank()) {
                            Text("Your answer: ${qr.selectedAnswer}", style = VTheme.type.caption.copy(fontSize = 12.sp), color = if (qr.correct) VTheme.colors.success else VTheme.colors.error)
                        }
                        if (!qr.correct && qr.correctAnswer.isNotBlank()) {
                            Text("Correct: ${qr.correctAnswer}", style = VTheme.type.caption.copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold), color = VTheme.colors.success)
                        }
                        val expl = qr.explanation
                        if (!expl.isNullOrBlank()) {
                            Spacer(Modifier.height(2.dp))
                            Text(expl, style = VTheme.type.caption.copy(fontSize = 11.sp), color = VTheme.colors.ink2)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            if (academics.leaderboardLoading) {
                Text("Loading leaderboard...", style = VTheme.type.caption, color = VTheme.colors.ink3)
            } else if (academics.leaderboardError != null) {
                Text(academics.leaderboardError!!, style = VTheme.type.caption, color = VTheme.colors.ink3)
            } else {
                val lb = academics.leaderboard
                if (lb != null && lb.entries.isNotEmpty()) {
                    Text("Leaderboard", style = VTheme.type.body, color = VTheme.colors.ink, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(4.dp))
                    Text("${lb.totalParticipants} participants", style = VTheme.type.caption, color = VTheme.colors.ink3)
                    Spacer(Modifier.height(10.dp))
                    Column(
                        Modifier.fillMaxWidth().heightIn(max = 300.dp).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        lb.entries.forEach { entry ->
                            Row(
                                Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (entry.isCurrentStudent) VTheme.colors.violetSoft else Color.Transparent)
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text("#${entry.rank}", style = VTheme.type.body.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold), color = VTheme.colors.violet)
                                Text(
                                    entry.studentName + if (entry.isCurrentStudent) " (You)" else "",
                                    style = VTheme.type.body.copy(fontSize = 13.sp),
                                    color = VTheme.colors.ink,
                                    modifier = Modifier.weight(1f),
                                )
                                Text("${entry.score}/${entry.totalMarks}", style = VTheme.type.body.copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold), color = VTheme.colors.ink)
                                Text("${entry.percentage}%", style = VTheme.type.body.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold), color = VTheme.colors.success)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            VButton("Back to Quizzes", onClick = onBack, variant = VButtonVariant.Secondary)
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// HOMEWORK TAB
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun HomeworkTab(academics: ParentAcademicsState, onRetry: () -> Unit) {
    if (academics.dailySummaryLoading) {
        LoadingState()
        return
    }
    academics.dailySummaryError?.let {
        ErrorState(message = it, onRetry = onRetry)
        return
    }

    val data = academics.dailySummary
    if (data == null || data.entries.isEmpty()) {
        EmptyState("No Homework Logs", "Daily homework summaries will appear here once available.")
        return
    }

    if (!data.aiSummary.isNullOrBlank()) {
        CreamCard {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    Modifier.size(36.dp).clip(CircleShape).background(VTheme.colors.violetSoft),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.Insights, contentDescription = "", tint = VTheme.colors.violet, modifier = Modifier.size(18.dp))
                }
                Column(Modifier.weight(1f)) {
                    Text("AI Summary", style = VTheme.type.body, color = VTheme.colors.ink, fontWeight = FontWeight.SemiBold)
                    Text(data.aiSummary!!, style = VTheme.type.caption, color = VTheme.colors.ink2)
                }
            }
        }
    }

    data.entries.forEach { entry ->
        CreamCard {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(entry.subject, style = VTheme.type.body, color = VTheme.colors.ink, fontWeight = FontWeight.SemiBold)
                    if (entry.summaryText.isNotBlank()) {
                        Text(entry.summaryText, style = VTheme.type.caption, color = VTheme.colors.ink3)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("${entry.coveragePct}%", style = VTheme.type.body.copy(fontWeight = FontWeight.Bold), color = VTheme.colors.violet)
                    if (entry.isAiEstimated) {
                        MiniBadge(text = "AI Est.", color = VTheme.colors.violet, bg = VTheme.colors.violetSoft)
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            VProgressBar(value = entry.coveragePct, height = 6.dp)
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// SHARED HELPERS — matching onboarding patterns exactly
// ═══════════════════════════════════════════════════════════════════════════════

private val subjectPalette = listOf(
    VTheme.colors.violet,
    VTheme.colors.coral,
    VTheme.colors.gold,
    VTheme.colors.success,
)

@Composable
private fun CreamCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier
            .fillMaxWidth()
            .background(VTheme.colors.surfaceCard, RoundedCornerShape(18.dp))
            .border(1.dp, VTheme.colors.line, RoundedCornerShape(18.dp))
            .padding(16.dp),
    ) {
        content()
    }
}

@Composable
private fun MiniBadge(text: String, color: Color, bg: Color) {
    Text(
        text = text,
        style = VTheme.type.caption.copy(fontWeight = FontWeight.Bold),
        color = color,
        modifier = Modifier.background(bg, RoundedCornerShape(50)).padding(horizontal = 8.dp, vertical = 3.dp),
    )
}

@Composable
private fun SimpleAvatar(name: String, size: androidx.compose.ui.unit.Dp, textColor: Color = VTheme.colors.violet, bg: Color = VTheme.colors.violetSoft) {
    val initials = name.split(" ").take(2).mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("")
    Box(
        modifier = Modifier.size(size).background(bg, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(initials, style = VTheme.type.label, color = textColor)
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, style = VTheme.type.label, color = VTheme.colors.ink2, fontWeight = FontWeight.Bold)
}

@Composable
private fun TintedBar(value: Float, fill: Color, height: androidx.compose.ui.unit.Dp = 7.dp) {
    val animated by animateFloatAsState(
        targetValue = value.coerceIn(0f, 100f) / 100f,
        animationSpec = tween(VMotion.durSlow, easing = VMotion.ease),
        label = "tinted-bar",
    )
    Box(
        Modifier.fillMaxWidth().height(height).clip(androidx.compose.foundation.shape.RoundedCornerShape(50)),
    ) {
        Box(Modifier.fillMaxWidth().height(height).clip(androidx.compose.foundation.shape.RoundedCornerShape(50)).background(VTheme.colors.line)) {
            Box(
                Modifier.fillMaxWidth(animated).height(height).clip(androidx.compose.foundation.shape.RoundedCornerShape(50)).background(fill),
            )
        }
    }
}

@Composable
private fun LoadingState() {
    Box(Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = VTheme.colors.violet, strokeWidth = 2.dp, modifier = Modifier.size(32.dp))
    }
}

@Composable
private fun ErrorState(message: String, onRetry: (() -> Unit)? = null) {
    CreamCard {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(VTheme.colors.error.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.Close, contentDescription = "", tint = VTheme.colors.error, modifier = Modifier.size(20.dp))
            }
            Column(Modifier.weight(1f)) {
                Text("Something went wrong", style = VTheme.type.body, color = VTheme.colors.ink, fontWeight = FontWeight.SemiBold)
                Text(message, style = VTheme.type.caption, color = VTheme.colors.ink3)
            }
            if (onRetry != null) {
                VButton("Retry", onClick = onRetry, variant = VButtonVariant.Secondary)
            }
        }
    }
}

@Composable
private fun EmptyState(title: String, body: String) {
    CreamCard {
        Column(
            Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(Icons.Filled.TaskAlt, contentDescription = "", tint = VTheme.colors.ink3, modifier = Modifier.size(32.dp))
            Text(title, style = VTheme.type.body, color = VTheme.colors.ink, fontWeight = FontWeight.SemiBold)
            Text(body, style = VTheme.type.caption, color = VTheme.colors.ink3, textAlign = TextAlign.Center)
        }
    }
}

private fun String.hexToColor(): Color {
    val hex = removePrefix("#")
    return Color(hex.toLong(16).or(0xFF000000).toInt())
}
