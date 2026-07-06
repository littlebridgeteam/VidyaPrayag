package com.littlebridge.enrollplus.ui.screens.teacher

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
// Custom icons from TeacherIcons.kt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes


private enum class UpdateTool(val label: String, val icon: ImageVector) {
    Attendance("Attendance", TICheck),
    Marks("Marks", TIAward),
    Syllabus("Syllabus", TIBook),
    Homework("Homework", TIEdit),
    Lesson("Lesson", TICalendar),
}

private data class ClassScope(
    val id: String,
    val name: String,
    val subject: String,
    val studentCount: Int,
)

@Composable
fun TeacherUpdateTab() {
    var selectedClass by rememberSaveable { mutableStateOf<ClassScope?>(null) }
    var selectedTool by rememberSaveable { mutableStateOf(UpdateTool.Attendance) }

    Column(modifier = Modifier.fillMaxSize()) {
        if (selectedClass == null) {
            ScopeSelector(
                onClassSelected = { selectedClass = it },
            )
        } else {
            // Tool segments bar
            ToolSegments(
                selectedTool = selectedTool,
                onToolSelected = { selectedTool = it },
            )
            // Tool content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
            ) {
                when (selectedTool) {
                    UpdateTool.Attendance -> AttendanceToolContent(selectedClass!!)
                    UpdateTool.Marks -> MarksToolContent(selectedClass!!)
                    UpdateTool.Syllabus -> SyllabusToolContent()
                    UpdateTool.Homework -> HomeworkToolContent()
                    UpdateTool.Lesson -> LessonToolContent()
                }
            }
        }
    }
}

@Composable
private fun ScopeSelector(onClassSelected: (ClassScope) -> Unit) {
    val classes = remember {
        listOf(
            ClassScope("7-B", "Class 7-B", "Mathematics", 32),
            ClassScope("8-A", "Class 8-A", "Mathematics", 28),
            ClassScope("9-C", "Class 9-C", "Algebra", 30),
        )
    }
    Column(modifier = Modifier.padding(24.dp)) {
        Text(
            text = "Select a class to continue",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = VColors.ink3,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        classes.forEach { cls ->
            ScopeItem(cls) { onClassSelected(cls) }
        }
    }
}

@Composable
private fun ScopeItem(scope: ClassScope, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .shadow(1.dp, VShapes.md)
            .background(VColors.white, VShapes.md)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(VColors.surfaceTint, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = scope.id,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = VColors.ink2,
                )
            }
            Column {
                Text(
                    text = scope.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = VColors.ink,
                )
                Text(
                    text = "${scope.subject} · ${scope.studentCount} students",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = VColors.ink3,
                )
            }
        }
        Text(
            text = "›",
            fontSize = 22.sp,
            color = VColors.ink3,
        )
    }
}

@Composable
private fun ToolSegments(
    selectedTool: UpdateTool,
    onToolSelected: (UpdateTool) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        UpdateTool.entries.forEach { tool ->
            val isSelected = tool == selectedTool
            val color = if (isSelected) VColors.violet else VColors.ink3
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onToolSelected(tool) }
                    .padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    imageVector = tool.icon,
                    contentDescription = tool.label,
                    tint = color,
                    modifier = Modifier.size(18.dp),
                )
                Text(
                    text = tool.label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = color,
                )
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .height(3.dp)
                        .background(
                            if (isSelected) VColors.violet else Color.Transparent,
                            VShapes.full,
                        ),
                )
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(VColors.lineSoft),
    )
}

@Composable
private fun AttendanceToolContent(scope: ClassScope) {
    val students = remember {
        listOf(
            StudentAtt("AK", "Aarav Kumar", "Roll #01", AttStatus.Present),
            StudentAtt("DS", "Diya Singh", "Roll #02", AttStatus.Present),
            StudentAtt("RP", "Rohan Patel", "Roll #03", AttStatus.Absent),
            StudentAtt("AN", "Ananya Nair", "Roll #04", AttStatus.Present),
            StudentAtt("VR", "Vihaan Reddy", "Roll #05", AttStatus.Late),
        )
    }
    var statuses by remember { mutableStateOf(students.associate { it to it.status }) }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = "Class ${scope.id} · ${scope.subject}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = VColors.ink,
                )
                Text(
                    text = "Monday, 15 July 2026",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = VColors.ink3,
                )
            }
        }
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            QuickPillButton("Mark all present") {}
            QuickPillButton("Mark all absent") {}
        }
        students.forEach { student ->
            AttendanceRow(
                student = student,
                currentStatus = statuses[student] ?: AttStatus.Present,
                onStatusChange = { newStatus ->
                    statuses = statuses.toMutableMap().apply { this[student] = newStatus }
                },
            )
        }
        // Summary
        val present = statuses.count { it.value == AttStatus.Present }
        val absent = statuses.count { it.value == AttStatus.Absent }
        val late = statuses.count { it.value == AttStatus.Late }
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SummaryChip("$present Present", VColors.successSoft, VColors.success, Modifier.weight(1f))
            SummaryChip("$absent Absent", VColors.errorSoft, VColors.error, Modifier.weight(1f))
            SummaryChip("$late Late", VColors.goldSoft, VColors.gold, Modifier.weight(1f))
        }
        Spacer(Modifier.height(24.dp))
    }
}

private enum class AttStatus { Present, Absent, Late }
private data class StudentAtt(val initials: String, val name: String, val roll: String, val status: AttStatus)

@Composable
private fun AttendanceRow(
    student: StudentAtt,
    currentStatus: AttStatus,
    onStatusChange: (AttStatus) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(VColors.surfaceTint, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = student.initials,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = VColors.ink2,
                )
            }
            Column {
                Text(
                    text = student.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = VColors.ink,
                )
                Text(
                    text = student.roll,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = VColors.ink3,
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            AttToggleButton("P", currentStatus == AttStatus.Present, VColors.successSoft, VColors.success) {
                onStatusChange(AttStatus.Present)
            }
            AttToggleButton("A", currentStatus == AttStatus.Absent, VColors.coralSoft, VColors.coral) {
                onStatusChange(AttStatus.Absent)
            }
            AttToggleButton("L", currentStatus == AttStatus.Late, VColors.goldSoft, VColors.gold) {
                onStatusChange(AttStatus.Late)
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(1.dp)
            .background(VColors.lineSoft),
    )
}

@Composable
private fun AttToggleButton(
    text: String,
    isActive: Boolean,
    activeBg: Color,
    activeFg: Color,
    onClick: () -> Unit,
) {
    val bg = if (isActive) activeBg else Color.Transparent
    val fg = if (isActive) activeFg else VColors.ink3
    val border = if (isActive) activeFg else VColors.line
    Box(
        modifier = Modifier
            .size(32.dp)
            .background(bg, VShapes.sm)
            .border(1.5.dp, border, VShapes.sm)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = fg,
        )
    }
}

@Composable
private fun QuickPillButton(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .border(1.5.dp, VColors.line, VShapes.full)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onClick() }
            .padding(horizontal = 16.dp, vertical = 7.dp),
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = VColors.ink2,
        )
    }
}

@Composable
private fun SummaryChip(text: String, bg: Color, fg: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(bg, VShapes.sm)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = fg,
        )
    }
}

@Composable
private fun MarksToolContent(scope: ClassScope) {
    val students = remember {
        listOf(
            StudentMark("AK", "Aarav Kumar", "Roll #01", "42", "B+", VColors.mint),
            StudentMark("DS", "Diya Singh", "Roll #02", "48", "A", VColors.mint),
            StudentMark("RP", "Rohan Patel", "Roll #03", "35", "C+", VColors.gold),
            StudentMark("AN", "Ananya Nair", "Roll #04", "45", "A-", VColors.mint),
            StudentMark("VR", "Vihaan Reddy", "Roll #05", "", "—", VColors.ink3),
        )
    }
    Column {
        // Assessment chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AssessmentChip("Unit Test 1", "50 marks", true)
            AssessmentChip("Quiz 3", "20 marks", false)
            AssessmentChip("Assignment 2", "30 marks", false)
        }
        students.forEach { student ->
            MarksRow(student)
        }
        Spacer(Modifier.height(24.dp))
    }
}

private data class StudentMark(
    val initials: String,
    val name: String,
    val roll: String,
    val marks: String,
    val grade: String,
    val gradeColor: Color,
)

@Composable
private fun AssessmentChip(name: String, meta: String, isActive: Boolean) {
    val bg = if (isActive) VColors.violetSoft else Color.Transparent
    val border = if (isActive) VColors.violet else VColors.line
    val nameColor = if (isActive) VColors.violet else VColors.ink
    Column(
        modifier = Modifier
            .background(bg, VShapes.md)
            .border(1.5.dp, border, VShapes.md)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {}
            .padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Text(
            text = name,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = nameColor,
        )
        Text(
            text = meta,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = VColors.ink3,
        )
    }
}

@Composable
private fun MarksRow(student: StudentMark) {
    var marksValue by remember { mutableStateOf(student.marks) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(VColors.surfaceTint, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = student.initials,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = VColors.ink2,
                )
            }
            Column {
                Text(
                    text = student.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = VColors.ink,
                )
                Text(
                    text = student.roll,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = VColors.ink3,
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            BasicTextField(
                value = marksValue,
                onValueChange = { marksValue = it.take(3) },
                textStyle = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = VColors.ink,
                ),
                cursorBrush = SolidColor(VColors.violet),
                modifier = Modifier
                    .width(40.dp)
                    .padding(vertical = 4.dp),
            ) { innerTextField ->
                Box(contentAlignment = Alignment.Center) {
                    if (marksValue.isEmpty()) {
                        Text(
                            text = "—",
                            fontSize = 15.sp,
                            color = VColors.ink3,
                        )
                    }
                    innerTextField()
                }
            }
            Text(
                text = "/50",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = VColors.ink3,
            )
            Text(
                text = student.grade,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = student.gradeColor,
            )
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(1.dp)
            .background(VColors.lineSoft),
    )
}

@Composable
private fun SyllabusToolContent() {
    Column(modifier = Modifier.padding(24.dp)) {
        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(VColors.surfaceTint, VShapes.full),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.62f)
                    .height(6.dp)
                    .background(VColors.violet, VShapes.full),
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("Coverage", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = VColors.ink2)
            Text("8 of 13 topics · 62%", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = VColors.ink2)
        }
        Spacer(Modifier.height(16.dp))
        SyllabusUnit("Chapter 1: Integers", "3/3 ✓", VColors.successSoft, VColors.success, true)
        SyllabusUnit("Chapter 2: Fractions", "2/4", VColors.goldSoft, VColors.gold, false)
        SyllabusUnit("Chapter 3: Decimals", "0/3", VColors.surfaceTint, VColors.ink3, false)
    }
}

@Composable
private fun SyllabusUnit(name: String, badgeText: String, badgeBg: Color, badgeFg: Color, allDone: Boolean) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = VColors.ink,
            )
            Box(
                modifier = Modifier
                    .background(badgeBg, VShapes.full)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Text(
                    text = badgeText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = badgeFg,
                )
            }
        }
    }
}

@Composable
private fun HomeworkToolContent() {
    Column(modifier = Modifier.padding(24.dp)) {
        HomeworkItem("Worksheet: Fractions Practice", "Due tomorrow", VColors.errorSoft, VColors.error, "Due: 16 Jul 2026", "3 submitted", "2 not submitted", "0 graded")
        HomeworkItem("Chapter 2 — Exercise 2.3", "Due in 3 days", VColors.goldSoft, VColors.gold, "Due: 18 Jul 2026", "1 submitted", "4 not submitted", "0 graded")
        HomeworkItem("Mental Math — Division", "Graded", VColors.successSoft, VColors.success, "Due: 12 Jul 2026", "5 submitted", "0 not submitted", "5 graded")
    }
}

@Composable
private fun HomeworkItem(
    title: String,
    badgeText: String,
    badgeBg: Color,
    badgeFg: Color,
    due: String,
    vararg meta: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .shadow(1.dp, VShapes.md)
            .background(VColors.white, VShapes.md)
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = VColors.ink,
            )
            Box(
                modifier = Modifier
                    .background(badgeBg, VShapes.full)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Text(
                    text = badgeText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = badgeFg,
                )
            }
        }
        Text(
            text = due,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = VColors.ink3,
            modifier = Modifier.padding(top = 6.dp),
        )
        Row(
            modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            meta.forEach { m ->
                Text(
                    text = m,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = VColors.ink3,
                )
            }
        }
    }
}

@Composable
private fun LessonToolContent() {
    Column(modifier = Modifier.padding(24.dp)) {
        LessonItem("Mon, 15 Jul 2026", "Fractions — Comparing and Ordering", "Students will learn to compare fractions with unlike denominators using LCM method")
        LessonItem("Wed, 17 Jul 2026", "Fractions — Addition & Subtraction", "Students will practice adding and subtracting mixed fractions with word problems")
        LessonItem("Fri, 19 Jul 2026", "Decimals — Introduction", "Introduce decimal notation, place value chart, and conversion from fractions")
        LessonItem("Mon, 22 Jul 2026", "Decimals — Operations", "Add, subtract, multiply and divide decimals up to 2 places", faded = true)
    }
}

@Composable
private fun LessonItem(date: String, topic: String, objectives: String, faded: Boolean = false) {
    val alpha = if (faded) 0.5f else 1f
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .shadow(1.dp, VShapes.md)
            .background(VColors.white, VShapes.md)
            .padding(16.dp),
    ) {
        Text(
            text = date,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = VColors.violet.copy(alpha = alpha),
        )
        Text(
            text = topic,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = VColors.ink.copy(alpha = alpha),
            modifier = Modifier.padding(top = 4.dp),
        )
        Text(
            text = objectives,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = VColors.ink2.copy(alpha = alpha),
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}
