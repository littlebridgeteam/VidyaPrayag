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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.littlebridge.enrollplus.feature.admin.domain.model.SchoolClassDto
import com.littlebridge.enrollplus.feature.admin.domain.model.StudentDto
import com.littlebridge.enrollplus.feature.admin.domain.model.TimetablePeriodDto
import com.littlebridge.enrollplus.feature.admin.presentation.ClassPerformanceState
import com.littlebridge.enrollplus.feature.admin.presentation.ClassPerformanceViewModel
import com.littlebridge.enrollplus.feature.admin.presentation.ClassesSubjectsViewModel
import com.littlebridge.enrollplus.feature.admin.presentation.StudentRosterState
import com.littlebridge.enrollplus.feature.admin.presentation.StudentRosterViewModel
import com.littlebridge.enrollplus.ui.v2.components.VAvatar
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VBadge
import com.littlebridge.enrollplus.ui.v2.components.VBadgeTone
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VEmptyState
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VProgressBar
import com.littlebridge.enrollplus.ui.v2.components.VTopTabs
import com.littlebridge.enrollplus.ui.v2.screens.VSectionHeader
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.SkeletonList
import com.littlebridge.enrollplus.ui.v2.screens.SkeletonDashboard
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.theme.staggeredItemEntrance
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.locale.appString
import org.koin.compose.viewmodel.koinViewModel

private val DETAIL_TABS = listOf("Students", "Teachers", "Timetable", "Analytics")
private val WEEKDAY_NAMES = listOf("", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

@Composable
private fun detailTabs(): List<String> = listOf(
    appString(StringKeys.CD_STUDENTS),
    appString(StringKeys.CD_TEACHERS),
    appString(StringKeys.CD_TIMETABLE),
    appString(StringKeys.CD_ANALYTICS),
)

/**
 * ClassDetailScreenV2 — drill-down view for a single class.
 *
 * Sub-tabs: Students · Teachers · Timetable · Analytics
 * - Students: filtered roster from [StudentRosterViewModel], click → student profile
 * - Teachers: derived from timetable periods (unique teacherId/teacherName), click → teacher profile
 * - Timetable: weekly grid filtered to this class from [ClassesSubjectsViewModel]
 * - Analytics: class-level performance from [ClassPerformanceViewModel]
 */
@Composable
fun ClassDetailScreenV2(
    classId: String,
    className: String,
    onBack: () -> Unit = {},
    onOpenStudent: (String) -> Unit = {},
    onOpenTeacher: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    classesViewModel: ClassesSubjectsViewModel = koinViewModel(),
    studentRosterViewModel: StudentRosterViewModel = koinViewModel(),
    classPerformanceViewModel: ClassPerformanceViewModel = koinViewModel(),
) {
    val classesState by classesViewModel.state.collectAsStateV2()
    val rosterState by studentRosterViewModel.state.collectAsStateV2()
    val performanceState by classPerformanceViewModel.state.collectAsStateV2()
    var activeTabIdx by remember { mutableStateOf(0) }

    // Load full timetable (not class-filtered) so the shared ClassesSubjectsViewModel
    // state isn't corrupted for ClassesSubjectsScreenV2. We filter client-side.
    // Also load class-specific analytics.
    LaunchedEffect(className) {
        if (classesState.timetable == null) {
            classesViewModel.loadTimetable(null)
        }
        classPerformanceViewModel.load(className)
    }

    Column(
        modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .navigationBarsPadding(),
    ) {
        VBackHeader(title = className, onBack = onBack)
        val tabsList = detailTabs()
        VTopTabs(tabs = tabsList, selected = tabsList[activeTabIdx], onSelect = { label -> activeTabIdx = tabsList.indexOf(label) })

        when (activeTabIdx) {
            0 -> ClassStudentsSubTab(
                className = className,
                state = rosterState,
                onOpenStudent = onOpenStudent,
                onRetry = { studentRosterViewModel.load() },
            )
            1 -> ClassTeachersSubTab(
                className = className,
                classesState = classesState,
                onOpenTeacher = onOpenTeacher,
            )
            2 -> ClassTimetableSubTab(
                className = className,
                classesState = classesState,
            )
            3 -> ClassAnalyticsSubTab(
                state = performanceState,
                onRetry = { classPerformanceViewModel.load(className) },
            )
        }
    }
}

// ── Students Sub-Tab ──────────────────────────────────────────────────────────

@Composable
private fun ClassStudentsSubTab(
    className: String,
    state: StudentRosterState,
    onOpenStudent: (String) -> Unit,
    onRetry: () -> Unit,
) {
    val students = state.students.filter { it.className.equals(className, ignoreCase = true) }

    VStateHost(
        loading = state.isLoading,
        error = state.error,
        isEmpty = students.isEmpty() && !state.isLoading,
        emptyTitle = appString(StringKeys.CD_NO_STUDENTS),
        emptyBody = appString(StringKeys.CD_NO_STUDENTS_BODY).replace("{className}", className),
        emptyIcon = VIcons.Users,
        onRetry = onRetry,
        skeleton = { SkeletonList(rows = 6, withAvatar = true) },
    ) {
        LazyColumn(
            Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 12.dp, bottom = 80.dp),
        ) {
            item {
                VSectionHeader(appString(StringKeys.CD_STUDENTS_COUNT).replace("{count}", students.size.toString()))
            }
            itemsIndexed(students, key = { _, it -> it.id }) { index, student ->
                StudentRowCard(
                    student = student,
                    onClick = { onOpenStudent(student.id) },
                    modifier = Modifier.staggeredItemEntrance(index, students.isNotEmpty()),
                )
            }
        }
    }
}

@Composable
private fun StudentRowCard(
    student: StudentDto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    VCard(modifier.fillMaxWidth().clickable { onClick() }) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            VAvatar(name = student.fullName, src = student.profilePhotoUrl, size = 44.dp)
            Column(Modifier.weight(1f)) {
                Text(student.fullName, style = VTypography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = VColors.ink, maxLines = 1)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (student.section.isNotBlank()) VBadge(text = appString(StringKeys.CD_SEC).replace("{section}", student.section), tone = VBadgeTone.Accent)
                    Text(appString(StringKeys.CD_ROLL).replace("{number}", student.rollNumber), style = VTypography.caption, color = VColors.ink3)
                }
                if (student.attendancePercent > 0f) {
                    Text(appString(StringKeys.CD_ATTENDANCE).replace("{percent}", student.attendancePercent.toInt().toString()), style = VTypography.label, color = VColors.ink3)
                }
            }
            Box(
                Modifier.size(28.dp).clip(CircleShape)
                    .background(VColors.violet.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center,
            ) {
                Text("›", color = VColors.violet, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ── Teachers Sub-Tab ──────────────────────────────────────────────────────────

@Composable
private fun ClassTeachersSubTab(
    className: String,
    classesState: com.littlebridge.enrollplus.feature.admin.presentation.ClassesSubjectsState,
    onOpenTeacher: (String) -> Unit,
) {
    // Extract unique teachers from timetable periods for this class
    val tt = classesState.timetable
    val classTeachers = remember(tt, className) {
        if (tt == null) return@remember emptyList()
        tt.weekdays.flatMap { it.periods }
            .filter { it.className.equals(className, ignoreCase = true) && it.teacherId.isNotBlank() }
            .distinctBy { it.teacherId }
            .map { period ->
                TeacherInfo(
                    id = period.teacherId,
                    name = period.teacherName,
                    subject = period.subject,
                )
            }
    }

    if (tt == null && classesState.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(appString(StringKeys.CD_LOADING_TIMETABLE), style = VTypography.body, color = VColors.ink2)
        }
    } else if (classTeachers.isEmpty()) {
        VEmptyState(
            title = appString(StringKeys.CD_NO_TEACHERS),
            icon = VIcons.Users,
            body = appString(StringKeys.CD_NO_TEACHERS_BODY),
        )
    } else {
        LazyColumn(
            Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 12.dp, bottom = 80.dp),
        ) {
            item {
                VSectionHeader(appString(StringKeys.CD_TEACHERS_COUNT).replace("{count}", classTeachers.size.toString()))
            }
            itemsIndexed(classTeachers, key = { _, it -> it.id }) { index, teacher ->
                TeacherRowCard(
                    teacher = teacher,
                    onClick = { onOpenTeacher(teacher.id) },
                    modifier = Modifier.staggeredItemEntrance(index, classTeachers.isNotEmpty()),
                )
            }
        }
    }
}

private data class TeacherInfo(
    val id: String,
    val name: String,
    val subject: String,
)

@Composable
private fun TeacherRowCard(
    teacher: TeacherInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    VCard(modifier.fillMaxWidth().clickable { onClick() }) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            VAvatar(name = teacher.name, size = 44.dp)
            Column(Modifier.weight(1f)) {
                Text(teacher.name, style = VTypography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = VColors.ink, maxLines = 1)
                if (teacher.subject.isNotBlank()) {
                    Text(teacher.subject, style = VTypography.caption, color = VColors.ink2)
                }
            }
            Box(
                Modifier.size(28.dp).clip(CircleShape)
                    .background(VColors.violet.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center,
            ) {
                Text("›", color = VColors.violet, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ── Timetable Sub-Tab ─────────────────────────────────────────────────────────

@Composable
private fun ClassTimetableSubTab(
    className: String,
    classesState: com.littlebridge.enrollplus.feature.admin.presentation.ClassesSubjectsState,
) {
    val tt = classesState.timetable
    var selectedDay by remember { mutableStateOf(1) }

    if (tt == null && classesState.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(appString(StringKeys.CD_LOADING_TIMETABLE), style = VTypography.body, color = VColors.ink2)
        }
        return
    }

    if (tt == null || tt.weekdays.isEmpty()) {
        VEmptyState(
            title = appString(StringKeys.CD_NO_TIMETABLE),
            icon = VIcons.Calendar,
            body = appString(StringKeys.CD_NO_TIMETABLE_BODY),
        )
        return
    }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        VSectionHeader(appString(StringKeys.CD_WEEKLY_TIMETABLE).replace("{className}", className))

        // Day selector
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            (1..7).forEach { day ->
                VBadge(
                    text = WEEKDAY_NAMES[day],
                    tone = if (selectedDay == day) VBadgeTone.Arctic else VBadgeTone.Neutral,
                    modifier = Modifier.clickable { selectedDay = day },
                )
            }
        }

        val dayData = tt.weekdays.find { it.weekday == selectedDay }
        val classPeriods = dayData?.periods?.filter {
            it.className.equals(className, ignoreCase = true)
        } ?: emptyList()

        if (classPeriods.isEmpty()) {
            VEmptyState(
                title = appString(StringKeys.CD_NO_PERIODS).replace("{day}", WEEKDAY_NAMES[selectedDay]),
                icon = VIcons.Calendar,
                body = appString(StringKeys.CD_NO_PERIODS_BODY).replace("{className}", className),
            )
        } else {
            classPeriods.forEach { period ->
                ClassPeriodRow(period = period)
            }
        }

        Spacer(Modifier.height(80.dp))
    }
}

@Composable
private fun ClassPeriodRow(period: TimetablePeriodDto) {
    VCard(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    "${period.startTime} – ${period.endTime}",
                    style = VTypography.body,
                    fontWeight = FontWeight.Medium,
                    color = VColors.ink,
                )
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (period.section.isNotBlank()) VBadge(text = period.section, tone = VBadgeTone.Accent)
                    Text(period.subject, style = VTypography.caption, color = VColors.ink2)
                    if (period.teacherName.isNotBlank()) {
                        Text("• ${period.teacherName}", style = VTypography.caption, color = VColors.ink3)
                    }
                }
            }
            if (period.room.isNotBlank()) {
                VBadge(text = appString(StringKeys.CD_ROOM).replace("{room}", period.room), tone = VBadgeTone.Success)
            }
        }
    }
}

// ── Analytics Sub-Tab ─────────────────────────────────────────────────────────

@Composable
private fun ClassAnalyticsSubTab(
    state: ClassPerformanceState,
    onRetry: () -> Unit,
) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Spacer(Modifier.height(4.dp))
        VStateHost(
            loading = state.isLoading,
            error = state.errorMessage,
            isEmpty = state.gradeDistribution.isEmpty() &&
                state.subjectMatrix.isEmpty() &&
                state.recentProgress.isEmpty() &&
                state.avgProficiency.isBlank(),
            emptyTitle = appString(StringKeys.CD_NO_ANALYTICS),
            emptyBody = appString(StringKeys.CD_NO_ANALYTICS_BODY),
            emptyIcon = VIcons.TrendingUp,
            onRetry = onRetry,
            skeleton = { SkeletonDashboard() },
        ) {
            // KPI row
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.weight(1f)) { AnalyticsKpi(label = appString(StringKeys.CD_AVG_PROFICIENCY), value = state.avgProficiency.ifBlank { "—" }) }
                Box(Modifier.weight(1f)) { AnalyticsKpi(label = appString(StringKeys.CD_ACTIVE_STUDENTS), value = state.activeStudents.toString()) }
                Box(Modifier.weight(1f)) { AnalyticsKpi(label = appString(StringKeys.CD_MEDIAN_GRADE), value = state.medianGrade.ifBlank { "—" }) }
            }

            // Grade distribution
            if (state.gradeDistribution.isNotEmpty()) {
                VSectionHeader(title = appString(StringKeys.CD_GRADE_DIST))
                VCard {
                    state.gradeDistribution.forEachIndexed { i, g ->
                        if (i > 0) Spacer(Modifier.height(10.dp))
                        AnalyticsGradeRow(g)
                    }
                }
            }

            // Subject matrix
            if (state.subjectMatrix.isNotEmpty()) {
                VSectionHeader(title = appString(StringKeys.CD_SUBJECT_MATRIX))
                VCard {
                    state.subjectMatrix.forEachIndexed { i, s ->
                        if (i > 0) Box(Modifier.fillMaxWidth().height(1.dp).background(VColors.line))
                        AnalyticsSubjectRow(s)
                    }
                }
            }

            // Risk summary
            VSectionHeader(title = appString(StringKeys.CD_EARLY_WARNING))
            VCard {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(Modifier.weight(1f)) { AnalyticsRiskTile(label = appString(StringKeys.CD_CRITICAL), value = state.criticalRiskCount.toString(), tone = VBadgeTone.Danger) }
                    Box(Modifier.weight(1f)) { AnalyticsRiskTile(label = appString(StringKeys.CD_MODERATE), value = state.moderateRiskCount.toString(), tone = VBadgeTone.Warning) }
                    Box(Modifier.weight(1f)) { AnalyticsRiskTile(label = appString(StringKeys.CD_ON_TARGET), value = "${state.proficiencyTargetReach}%", tone = VBadgeTone.Success) }
                }
            }

            // Top performer
            if (state.topPerformerName.isNotBlank()) {
                VSectionHeader(title = appString(StringKeys.CD_TOP_PERFORMER))
                VCard {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        VBadge(text = "★ 1ST", tone = VBadgeTone.Warning)
                        Column(Modifier.weight(1f)) {
                            Text(state.topPerformerName, style = VTypography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = VColors.ink)
                            if (state.topPerformerDetails.isNotBlank()) {
                                Spacer(Modifier.height(2.dp))
                                Text(state.topPerformerDetails, style = VTypography.caption, color = VColors.ink3)
                            }
                        }
                    }
                }
            }

            // Recent progress monitoring
            if (state.recentProgress.isNotEmpty()) {
                VSectionHeader(title = appString(StringKeys.CD_PROGRESS_MONITORING))
                state.recentProgress.forEach { p -> AnalyticsProgressRow(p) }
            }
        }
        Spacer(Modifier.height(80.dp))
    }
}

@Composable
private fun AnalyticsKpi(label: String, value: String) {
    VCard {
        Text(label, style = VTypography.label, color = VColors.ink3)
        Spacer(Modifier.height(4.dp))
        Text(value, style = VTypography.body.copy(fontWeight = FontWeight.SemiBold, fontSize = 22.sp), color = VColors.ink)
    }
}

@Composable
private fun AnalyticsRiskTile(label: String, value: String, tone: VBadgeTone) {
    Column {
        Text(label, style = VTypography.label, color = VColors.ink3)
        Spacer(Modifier.height(4.dp))
        VBadge(text = value, tone = tone)
    }
}

@Composable
private fun AnalyticsGradeRow(g: com.littlebridge.enrollplus.feature.admin.presentation.GradeDistribution) {
    Column {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(g.grade, style = VTypography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = VColors.ink)
            Text("${g.percentage}%", style = VTypography.bodySmall, color = VColors.ink2)
        }
        Spacer(Modifier.height(6.dp))
        VProgressBar(value = (g.value * 100f).coerceIn(0f, 100f))
    }
}

@Composable
private fun AnalyticsSubjectRow(s: com.littlebridge.enrollplus.feature.admin.presentation.SubjectMatrixItem) {
    val (trendText, trendTone) = when (s.trend.lowercase()) {
        "up" -> appString(StringKeys.CD_TREND_UP) to VBadgeTone.Success
        "down" -> appString(StringKeys.CD_TREND_DOWN) to VBadgeTone.Danger
        else -> appString(StringKeys.CD_TREND_FLAT) to VBadgeTone.Neutral
    }
    Row(
        Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(s.name, style = VTypography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = VColors.ink, modifier = Modifier.weight(1f))
        Text("${s.percentage}%", style = VTypography.bodySmall, color = VColors.ink2)
        VBadge(text = trendText, tone = trendTone)
    }
}

@Composable
private fun AnalyticsProgressRow(p: com.littlebridge.enrollplus.feature.admin.presentation.ProgressMonitoringItem) {
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
                    .background(VColors.cream)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                Text(p.initials.take(2).uppercase(), style = VTypography.bodySmall, color = VColors.ink)
            }
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        p.name,
                        style = VTypography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = VColors.ink,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    VBadge(text = p.status, tone = statusTone)
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    "${appString(StringKeys.CD_MATH)} ${p.math} · ${appString(StringKeys.CD_SCI)} ${p.science} · ${appString(StringKeys.CD_LIT)} ${p.literature}",
                    style = VTypography.bodySmall, color = VColors.ink2,
                )
                Spacer(Modifier.height(2.dp))
                Text(appString(StringKeys.CD_ATTENDANCE_LABEL).replace("{percent}", p.attendance), style = VTypography.caption, color = VColors.ink3)
            }
        }
    }
}
