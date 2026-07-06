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
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography

@Composable
fun TeacherHomeTab(
    onNavigateTab: (TeacherTab) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        GreetingSection()
        NowTeachingCard(onClick = { onNavigateTab(TeacherTab.Classes) })
        TodayScheduleSection(
            onViewWeek = { onNavigateTab(TeacherTab.Timetable) },
        )
        QuickActionsSection(
            onAction = { onNavigateTab(TeacherTab.Update) },
        )
        MyClassesSection(
            onViewAll = { onNavigateTab(TeacherTab.Classes) },
        )
        UpcomingEventsSection()
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun GreetingSection() {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .background(VColors.success, VShapes.full),
            )
            Text(
                text = "IN CLASS NOW",
                style = VTypography.accentLabel,
                color = VColors.success,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = buildAnnotatedString {
                append("Good morning,\n")
                withStyle(SpanStyle(fontWeight = FontWeight.Normal, color = VColors.ink2)) {
                    append("Priya")
                }
            },
            style = VTypography.h2.copy(fontSize = 31.sp, letterSpacing = (-1).sp, lineHeight = 33.sp),
            color = VColors.ink,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "5 classes today · 7 pending tasks · 90 students",
            style = VTypography.body,
            color = VColors.ink2,
        )
    }
}

@Composable
private fun NowTeachingCard(onClick: () -> Unit) {
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
                        append("9:00 ")
                        withStyle(SpanStyle(color = VColors.ink3, fontWeight = FontWeight.Medium)) {
                            append("— 9:45")
                        }
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = VColors.ink2,
                )
            }
            Spacer(Modifier.height(14.dp))
            Text(
                text = "8-A · Mathematics",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp,
                color = VColors.ink,
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = "Room 312",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = VColors.ink3,
            )
        }
        // Stats row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(VColors.surfaceTint)
                .padding(horizontal = 20.dp, vertical = 14.dp),
        ) {
            NowStat("28", "Present", VColors.ink)
            Spacer(Modifier.width(16.dp))
            NowStat("2", "Absent", VColors.coral)
            Spacer(Modifier.width(16.dp))
            NowStat("7", "Ungraded", VColors.gold)
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
                    text = "Take attendance",
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
private fun NowStat(num: String, label: String, numColor: Color) {
    Column {
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
private fun TodayScheduleSection(onViewWeek: () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        SectionHeader("Today's Schedule", "View week", onViewWeek)
        ScheduleItem("8:00 — 8:45", "7-B · Mathematics", "Room 204", ScheduleStatus.Done)
        ScheduleItem("9:00 — 9:45", "8-A · Mathematics", "Room 312", ScheduleStatus.Now)
        ScheduleItem("10:00 — 10:45", "9-C · Algebra", "Room 108", ScheduleStatus.Next)
        ScheduleItem("11:15 — 12:00", "7-B · Mathematics", "Room 204", ScheduleStatus.Upcoming)
        ScheduleItem("1:00 — 1:45", "8-A · Mathematics", "Room 312", ScheduleStatus.Upcoming)
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
private fun MyClassesSection(onViewAll: () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        SectionHeader("My Classes", "View all", onViewAll)
        ClassRow("7-B", "Mathematics", "32 students · 3 pending · 5 ungraded", "3 pending", VColors.coralSoft, VColors.coral)
        ClassRow("8-A", "Mathematics", "28 students · 1 pending · 7 ungraded", "1 pending", VColors.coralSoft, VColors.coral)
        ClassRow("9-C", "Algebra", "30 students · 2 pending", "All graded", VColors.successSoft, VColors.success)
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
private fun UpcomingEventsSection() {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        SectionHeader("Upcoming Events")
        EventRow("18", "JUL", "Parent-Teacher Meeting", "10:00 AM — 1:00 PM")
        EventRow("22", "JUL", "Unit Test — Class 8-A", "11:00 AM — 12:00 PM")
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
