package com.littlebridge.enrollplus.ui.screens.teacher

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
// Custom icons from TeacherIcons.kt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.domain.util.UiState
import com.littlebridge.enrollplus.feature.teacher.domain.model.CalendarOverlayDto
import com.littlebridge.enrollplus.feature.teacher.domain.model.ResolvedDayDto
import com.littlebridge.enrollplus.feature.teacher.domain.model.ResolvedPeriodDto
import com.littlebridge.enrollplus.feature.teacher.domain.model.TeacherClassSummaryDto
import com.littlebridge.enrollplus.feature.teacher.domain.model.TeacherObligationsDto
import com.littlebridge.enrollplus.presentation.TeacherViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.util.nowMinutesOfDay

@Composable
fun TeacherHomeTab(
    viewModel: TeacherViewModel,
    onNavigateTab: (TeacherTab) -> Unit = {},
) {
    val dayState by viewModel.dayState.collectAsState()
    val obligationsState by viewModel.obligationsState.collectAsState()
    val classesState by viewModel.classesState.collectAsState()
    val profileState by viewModel.profileState.collectAsState()

    val dayData = (dayState as? UiState.Success)?.data?.data
    val obligations = (obligationsState as? UiState.Success)?.data?.data
    val classes = (classesState as? UiState.Success)?.data?.data?.classes ?: emptyList()
    val teacherName = (profileState as? UiState.Success)?.data?.data?.name ?: "Teacher"
    val firstName = teacherName.substringBefore(" ")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        GreetingSection(
            firstName = firstName,
            dayData = dayData,
            obligations = obligations,
        )
        NowTeachingCard(
            dayData = dayData,
            onClick = { onNavigateTab(TeacherTab.Classes) },
        )
        TodayScheduleSection(
            dayData = dayData,
            onViewWeek = { onNavigateTab(TeacherTab.Timetable) },
        )
        QuickActionsSection(
            onAction = { onNavigateTab(TeacherTab.Update) },
        )
        MyClassesSection(
            classes = classes,
            onViewAll = { onNavigateTab(TeacherTab.Classes) },
        )
        UpcomingEventsSection(
            calendar = dayData?.calendar ?: emptyList(),
            dateStr = dayData?.date,
        )
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun GreetingSection(
    firstName: String,
    dayData: ResolvedDayDto?,
    obligations: TeacherObligationsDto?,
) {
    val inClassNow = dayData?.nowIndex != null
    val classesToday = dayData?.periods?.size ?: 0
    val pendingTasks = obligations?.items?.size ?: 0
    val totalStudents = obligations?.classesTodayTotal ?: 0

    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .background(if (inClassNow) VColors.success else VColors.ink3, VShapes.full),
            )
            Text(
                text = if (inClassNow) "IN CLASS NOW" else "NOT IN CLASS",
                style = VTypography.accentLabel,
                color = if (inClassNow) VColors.success else VColors.ink3,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = buildAnnotatedString {
                append("${greetingForHour(nowMinutesOfDay() / 60)},\n")
                withStyle(SpanStyle(fontWeight = FontWeight.Normal, color = VColors.ink2)) {
                    append(firstName)
                }
            },
            style = VTypography.h2.copy(fontSize = 31.sp, letterSpacing = (-1).sp, lineHeight = 33.sp),
            color = VColors.ink,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "$classesToday classes today · $pendingTasks pending tasks · $totalStudents students",
            style = VTypography.body,
            color = VColors.ink2,
        )
    }
}

@Composable
private fun NowTeachingCard(
    dayData: ResolvedDayDto?,
    onClick: () -> Unit,
) {
    val nowIndex = dayData?.nowIndex
    val currentPeriod = if (nowIndex != null && nowIndex < dayData.periods.size) {
        dayData.periods[nowIndex]
    } else null

    if (currentPeriod == null) return

    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .padding(bottom = 16.dp)
            .shadow(1.dp, VShapes.md)
            .background(VColors.white, VShapes.md)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onClick() },
    ) {
        // Top section
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(VColors.success, VShapes.full),
                    )
                    Text(
                        text = "NOW TEACHING",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        color = VColors.success,
                    )
                }
                Text(
                    text = buildAnnotatedString {
                        append(currentPeriod.startTime + " ")
                        withStyle(SpanStyle(color = VColors.ink3, fontWeight = FontWeight.Medium)) {
                            append("— " + currentPeriod.endTime)
                        }
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = VColors.ink2,
                )
            }
            Spacer(Modifier.height(14.dp))
            Text(
                text = "${currentPeriod.className}${if (currentPeriod.section.isNotBlank()) "-${currentPeriod.section}" else ""} · ${currentPeriod.subject}",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp,
                color = VColors.ink,
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = currentPeriod.room.ifBlank { "No room assigned" },
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = VColors.ink3,
            )
        }
        // Stats row — 3 stats with dividers matching prototype
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(VColors.surfaceTint)
                .padding(horizontal = 20.dp, vertical = 14.dp),
        ) {
            NowStat(
                if (currentPeriod.attendanceMarked) "✓" else "!",
                if (currentPeriod.attendanceMarked) "Marked" else "Unmarked",
                if (currentPeriod.attendanceMarked) VColors.ink else VColors.coral,
                Modifier.weight(1f),
            )
            Box(modifier = Modifier.width(1.dp).height(32.dp).background(VColors.lineSoft))
            NowStat(
                if (currentPeriod.lessonPlanStatus == "completed") "✓" else "—",
                "Lesson",
                if (currentPeriod.lessonPlanStatus == "completed") VColors.success else VColors.ink3,
                Modifier.weight(1f).padding(horizontal = 16.dp),
            )
            Box(modifier = Modifier.width(1.dp).height(32.dp).background(VColors.lineSoft))
            NowStat(
                if (currentPeriod.status == "SCHEDULED") "●" else "○",
                "Status",
                if (currentPeriod.status == "SCHEDULED") VColors.ink else VColors.ink3,
                Modifier.weight(1f).padding(start = 16.dp),
            )
        }
        // CTA row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Text(
                    text = if (currentPeriod.attendanceMarked) "View class" else "Take attendance",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = VColors.violet,
                )
                Icon(
                    imageVector = TIArrowRight,
                    contentDescription = null,
                    tint = VColors.violet,
                    modifier = Modifier.size(14.dp),
                )
            }
        }
    }
}

@Composable
private fun NowStat(num: String, label: String, numColor: Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = num,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.4).sp,
            color = numColor,
        )
        Text(
            text = label.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.3.sp,
            color = VColors.ink3,
            modifier = Modifier.padding(top = 3.dp),
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    linkText: String? = null,
    onLinkClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.3).sp,
            color = VColors.ink,
        )
        if (linkText != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { onLinkClick() },
            ) {
                Text(
                    text = linkText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = VColors.violet,
                )
                Icon(
                    imageVector = TIArrowRight,
                    contentDescription = null,
                    tint = VColors.violet,
                    modifier = Modifier.size(13.dp),
                )
            }
        }
    }
}

@Composable
private fun TodayScheduleSection(
    dayData: ResolvedDayDto?,
    onViewWeek: () -> Unit,
) {
    val periods = dayData?.periods ?: emptyList()
    val nowIdx = dayData?.nowIndex
    val nextIdx = dayData?.nextIndex

    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        SectionHeader("Today's Schedule", "View week", onViewWeek)
        if (periods.isEmpty()) {
            Text(
                text = if (dayData?.isHoliday == true) "Holiday: ${dayData.holidayName ?: "No classes"}" else "No periods scheduled",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = VColors.ink3,
                modifier = Modifier.padding(vertical = 16.dp),
            )
        } else {
            periods.forEachIndexed { index, period ->
                val status = when {
                    index == nowIdx -> ScheduleStatus.Now
                    index == nextIdx -> ScheduleStatus.Next
                    nowIdx != null && index < nowIdx -> ScheduleStatus.Done
                    else -> ScheduleStatus.Upcoming
                }
                val classLabel = "${period.className}${if (period.section.isNotBlank()) "-${period.section}" else ""} · ${period.subject}"
                val timeLabel = "${period.startTime} — ${period.endTime}"
                ScheduleItem(timeLabel, classLabel, period.room, status)
            }
        }
    }
}

private enum class ScheduleStatus { Done, Now, Next, Upcoming }

@Composable
private fun ScheduleItem(
    time: String,
    className: String,
    room: String,
    status: ScheduleStatus,
) {
    val isCurrent = status == ScheduleStatus.Now
    val isDone = status == ScheduleStatus.Done
    val bgColor = if (isCurrent) VColors.violetSoft else VColors.white
    val timeColor = if (isCurrent) VColors.violet else VColors.ink3
    val timeWeight = if (isCurrent) FontWeight.Bold else FontWeight.SemiBold
    val classColor = if (isDone) VColors.ink3 else VColors.ink
    val badgeBg = when (status) {
        ScheduleStatus.Done -> VColors.surfaceTint
        ScheduleStatus.Now -> VColors.violet
        ScheduleStatus.Next -> VColors.surfaceTint
        ScheduleStatus.Upcoming -> Color.Transparent
    }
    val badgeFg = when (status) {
        ScheduleStatus.Done -> VColors.ink3
        ScheduleStatus.Now -> VColors.white
        ScheduleStatus.Next -> VColors.ink2
        ScheduleStatus.Upcoming -> VColors.ink2
    }
    val badgeText = when (status) {
        ScheduleStatus.Done -> "Done"
        ScheduleStatus.Now -> "Now"
        ScheduleStatus.Next -> "Next"
        ScheduleStatus.Upcoming -> ""
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .shadow(1.dp, VShapes.md)
            .background(bgColor, VShapes.md)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(
            text = time,
            fontSize = 13.sp,
            fontWeight = timeWeight,
            color = timeColor,
            modifier = Modifier.width(72.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = className,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.2).sp,
                color = classColor,
            )
            Text(
                text = room,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = VColors.ink3,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
        if (badgeText.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .background(badgeBg, VShapes.full)
                    .padding(horizontal = 10.dp, vertical = 3.dp),
            ) {
                Text(
                    text = badgeText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    color = badgeFg,
                )
            }
        }
    }
}

@Composable
private fun QuickActionsSection(onAction: () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        SectionHeader("Quick Actions")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            QuickActionItem("Attendance", TICheck, onAction, Modifier.weight(1f))
            QuickActionItem("Homework", TIEdit, onAction, Modifier.weight(1f))
            QuickActionItem("Marks", TIAward, onAction, Modifier.weight(1f))
            QuickActionItem("Syllabus", TIBook, onAction, Modifier.weight(1f))
        }
    }
}

@Composable
private fun QuickActionItem(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .shadow(1.dp, VShapes.md)
            .background(VColors.white, VShapes.md)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onClick() }
            .padding(vertical = 16.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(VColors.surfaceTint, VShapes.full),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = VColors.ink2,
                modifier = Modifier.size(19.dp),
            )
        }
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = VColors.ink2,
        )
    }
}

@Composable
private fun MyClassesSection(
    classes: List<TeacherClassSummaryDto>,
    onViewAll: () -> Unit,
) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        SectionHeader("My Classes", "View all", onViewAll)
        if (classes.isEmpty()) {
            Text(
                text = "No classes assigned",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = VColors.ink3,
                modifier = Modifier.padding(vertical = 16.dp),
            )
        } else {
            classes.take(5).forEach { cls ->
                val classId = "${cls.className}${if (cls.section.isNotBlank()) "-${cls.section}" else ""}"
                val meta = "${cls.studentCount} students"
                val atRisk = cls.atRiskCount
                val attendanceMarked = cls.todayAttendanceMarked
                val badgeText: String
                val badgeBg: Color
                val badgeFg: Color
                if (atRisk > 0) {
                    badgeText = "$atRisk at risk"
                    badgeBg = VColors.coralSoft
                    badgeFg = VColors.coral
                } else if (!attendanceMarked) {
                    badgeText = "Attendance due"
                    badgeBg = VColors.coralSoft
                    badgeFg = VColors.coral
                } else {
                    badgeText = "All caught up"
                    badgeBg = VColors.successSoft
                    badgeFg = VColors.success
                }
                ClassRow(classId, cls.subject, meta, badgeText, badgeBg, badgeFg)
            }
        }
    }
}

@Composable
private fun ClassRow(
    id: String,
    subject: String,
    students: String,
    badgeText: String,
    badgeBg: Color,
    badgeFg: Color,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .shadow(1.dp, VShapes.md)
            .background(VColors.white, VShapes.md)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {}
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(
            text = id,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.2).sp,
            color = VColors.ink,
            modifier = Modifier.width(64.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = subject,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.2).sp,
                color = VColors.ink,
            )
            Text(
                text = students,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = VColors.ink3,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
        Box(
            modifier = Modifier
                .background(badgeBg, VShapes.full)
                .padding(horizontal = 10.dp, vertical = 3.dp),
        ) {
            Text(
                text = badgeText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                color = badgeFg,
            )
        }
    }
}

@Composable
private fun UpcomingEventsSection(calendar: List<CalendarOverlayDto>, dateStr: String?) {
    val dayNum = dateStr?.substringAfterLast("-")?.toIntOrNull()?.toString() ?: "--"
    val monthMap = listOf("JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC")
    val monthNum = dateStr?.substringAfter("-")?.substringBefore("-")?.toIntOrNull()
    val monthLabel = if (monthNum != null && monthNum in 1..12) monthMap[monthNum - 1] else "---"

    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        SectionHeader("Upcoming Events")
        if (calendar.isEmpty()) {
            Text(
                text = "No upcoming events",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = VColors.ink3,
                modifier = Modifier.padding(vertical = 16.dp),
            )
        } else {
            calendar.take(5).forEach { event ->
                EventRow(
                    day = dayNum,
                    month = monthLabel,
                    title = event.title,
                    time = event.type.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                )
            }
        }
    }
}

@Composable
private fun EventRow(day: String, month: String, title: String, time: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .shadow(1.dp, VShapes.md)
            .background(VColors.white, VShapes.md)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {}
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Column(
            modifier = Modifier
                .size(46.dp)
                .background(VColors.surfaceTint, VShapes.sm),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = day,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp,
                color = VColors.ink,
            )
            Text(
                text = month,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = VColors.ink3,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.2).sp,
                color = VColors.ink,
            )
            Text(
                text = time,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = VColors.ink3,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}
