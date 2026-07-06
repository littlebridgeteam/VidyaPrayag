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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.domain.util.UiState
import com.littlebridge.enrollplus.feature.teacher.domain.model.AssessmentDto
import com.littlebridge.enrollplus.feature.teacher.domain.model.AttendanceStudentDto
import com.littlebridge.enrollplus.feature.teacher.domain.model.HomeworkItemDto
import com.littlebridge.enrollplus.feature.teacher.domain.model.LessonPlanDto
import com.littlebridge.enrollplus.feature.teacher.domain.model.SyllabusNodeDto
import com.littlebridge.enrollplus.feature.teacher.domain.model.TeacherClassSummaryDto
import com.littlebridge.enrollplus.presentation.TeacherViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes


private enum class UpdateTool(val label: String, val icon: ImageVector) {
    Attendance("Attendance", TICheck),
    Marks("Marks", TIAward),
    Syllabus("Syllabus", TIBook),
    Homework("Homework", TIEdit),
    Lesson("Lesson", TICalendar),
}


@Composable
fun TeacherUpdateTab(viewModel: TeacherViewModel) {
    val classesState by viewModel.classesState.collectAsState()
    val classes = (classesState as? UiState.Success)?.data?.data?.classes ?: emptyList()

    var selectedClass by rememberSaveable { mutableStateOf<TeacherClassSummaryDto?>(null) }
    var selectedTool by rememberSaveable { mutableStateOf(UpdateTool.Attendance) }

    LaunchedEffect(selectedClass) {
        selectedClass?.let {
            viewModel.selectClass(it.assignmentId, it)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (selectedClass == null) {
            ScopeSelector(
                classes = classes,
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
                    UpdateTool.Attendance -> AttendanceToolContent(viewModel, selectedClass!!)
                    UpdateTool.Marks -> MarksToolContent(viewModel, selectedClass!!)
                    UpdateTool.Syllabus -> SyllabusToolContent(viewModel)
                    UpdateTool.Homework -> HomeworkToolContent(viewModel)
                    UpdateTool.Lesson -> LessonToolContent(viewModel)
                }
            }
        }
    }
}

@Composable
private fun ScopeSelector(
    classes: List<TeacherClassSummaryDto>,
    onClassSelected: (TeacherClassSummaryDto) -> Unit,
) {
    Column(modifier = Modifier.padding(24.dp)) {
        Text(
            text = "Select a class to continue",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = VColors.ink3,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        if (classes.isEmpty()) {
            Text(
                text = "No classes available",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = VColors.ink3,
                modifier = Modifier.padding(vertical = 16.dp),
            )
        } else {
            classes.forEach { cls ->
                ScopeItem(cls) { onClassSelected(cls) }
            }
        }
    }
}

@Composable
private fun ScopeItem(scope: TeacherClassSummaryDto, onClick: () -> Unit) {
    val classLabel = "${scope.className}${if (scope.section.isNotBlank()) "-${scope.section}" else ""}"
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
                    text = classLabel,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = VColors.ink2,
                )
            }
            Column {
                Text(
                    text = classLabel,
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
private fun AttendanceToolContent(viewModel: TeacherViewModel, scope: TeacherClassSummaryDto) {
    val attendanceState by viewModel.attendanceState.collectAsState()
    val attData = (attendanceState as? UiState.Success)?.data?.data
    val students = attData?.students ?: emptyList()
    val classLabel = "${scope.className}${if (scope.section.isNotBlank()) "-${scope.section}" else ""}"

    var statuses by remember(students) {
        mutableStateOf(students.associate { it.studentId to it.status })
    }

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
                    text = "$classLabel · ${scope.subject}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = VColors.ink,
                )
                Text(
                    text = attData?.date ?: "",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = VColors.ink3,
                )
            }
        }
        if (attData?.isHoliday == true) {
            Text(
                text = "Holiday: ${attData.holidayName ?: "No classes"}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = VColors.ink3,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            )
        } else if (students.isEmpty()) {
            Text(
                text = when (attendanceState) {
                    is UiState.Loading -> "Loading…"
                    is UiState.Error -> (attendanceState as UiState.Error).message
                    else -> "No students enrolled"
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = VColors.ink3,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            )
        } else {
            Row(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                QuickPillButton("Mark all present") {
                    statuses = students.associate { it.studentId to "present" }
                }
                QuickPillButton("Mark all absent") {
                    statuses = students.associate { it.studentId to "absent" }
                }
            }
            students.forEach { student ->
                val currentStatus = statuses[student.studentId] ?: "present"
                AttendanceRow(
                    student = student,
                    currentStatus = currentStatus,
                    onStatusChange = { newStatus ->
                        statuses = statuses.toMutableMap().apply { this[student.studentId] = newStatus }
                    },
                )
            }
            val present = statuses.count { it.value == "present" }
            val absent = statuses.count { it.value == "absent" }
            val late = statuses.count { it.value == "late" }
            Row(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SummaryChip("$present Present", VColors.successSoft, VColors.success, Modifier.weight(1f))
                SummaryChip("$absent Absent", VColors.errorSoft, VColors.error, Modifier.weight(1f))
                SummaryChip("$late Late", VColors.goldSoft, VColors.gold, Modifier.weight(1f))
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun AttendanceRow(
    student: AttendanceStudentDto,
    currentStatus: String,
    onStatusChange: (String) -> Unit,
) {
    val initials = student.name.take(2).uppercase()
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
                    text = initials,
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
                    text = if (student.rollNo.isNotBlank()) "Roll #${student.rollNo}" else "",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = VColors.ink3,
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            AttToggleButton("P", currentStatus == "present", VColors.successSoft, VColors.success) {
                onStatusChange("present")
            }
            AttToggleButton("A", currentStatus == "absent", VColors.coralSoft, VColors.coral) {
                onStatusChange("absent")
            }
            AttToggleButton("L", currentStatus == "late", VColors.goldSoft, VColors.gold) {
                onStatusChange("late")
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
private fun MarksToolContent(viewModel: TeacherViewModel, scope: TeacherClassSummaryDto) {
    val assessmentsState by viewModel.assessmentsState.collectAsState()
    val assessments = (assessmentsState as? UiState.Success)?.data?.data?.assessments ?: emptyList()
    val classLabel = "${scope.className}${if (scope.section.isNotBlank()) "-${scope.section}" else ""}"

    var selectedAssessment by remember { mutableStateOf<AssessmentDto?>(null) }
    val activeAssessment = selectedAssessment ?: assessments.firstOrNull()

    Column {
        if (assessments.isEmpty()) {
            Text(
                text = when (assessmentsState) {
                    is UiState.Loading -> "Loading…"
                    is UiState.Error -> (assessmentsState as UiState.Error).message
                    else -> "No assessments created for this class"
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = VColors.ink3,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            )
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                assessments.take(4).forEach { assessment ->
                    AssessmentChip(
                        name = assessment.name,
                        meta = "${assessment.maxMarks} marks",
                        isActive = activeAssessment?.id == assessment.id,
                        onClick = { selectedAssessment = assessment },
                    )
                }
            }
            Text(
                text = "$classLabel · ${scope.subject} — ${activeAssessment?.name ?: ""} (${activeAssessment?.enteredCount ?: 0}/${activeAssessment?.rosterCount ?: 0} entered)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = VColors.ink2,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            )
            Text(
                text = "Status: ${activeAssessment?.status ?: ""}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = VColors.ink3,
                modifier = Modifier.padding(horizontal = 24.dp),
            )
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun AssessmentChip(name: String, meta: String, isActive: Boolean, onClick: () -> Unit = {}) {
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
            ) { onClick() }
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
private fun SyllabusToolContent(viewModel: TeacherViewModel) {
    val syllabusState by viewModel.syllabusState.collectAsState()
    val syllabusData = (syllabusState as? UiState.Success)?.data?.data
    val units = syllabusData?.units ?: emptyList()
    val coveredCount = syllabusData?.coveredCount ?: 0
    val totalCount = syllabusData?.totalCount ?: 0
    val progress = if (totalCount > 0) coveredCount.toFloat() / totalCount else 0f

    Column(modifier = Modifier.padding(24.dp)) {
        if (syllabusData == null) {
            Text(
                text = when (syllabusState) {
                    is UiState.Loading -> "Loading…"
                    is UiState.Error -> (syllabusState as UiState.Error).message
                    else -> "No syllabus data"
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = VColors.ink3,
                modifier = Modifier.padding(vertical = 16.dp),
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(VColors.surfaceTint, VShapes.full),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(6.dp)
                        .background(VColors.mint, VShapes.full),
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Coverage", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = VColors.ink3)
                Text("$coveredCount of $totalCount topics · ${(progress * 100).toInt()}%", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = VColors.ink3)
            }
            Spacer(Modifier.height(16.dp))
            units.filter { it.depth == 0 }.forEach { chapter ->
                val chapterTopics = units.filter { it.parentId == chapter.id }
                val chapterCovered = chapterTopics.count { it.isCovered }
                val chapterTotal = chapterTopics.size
                val allDone = chapterTotal > 0 && chapterCovered == chapterTotal
                SyllabusUnit(
                    chapter.title,
                    if (chapterTotal > 0) "$chapterCovered/$chapterTotal ${if (allDone) "✓" else ""}" else (if (chapter.isCovered) "✓" else "—"),
                    if (allDone) VColors.successSoft else if (chapterCovered > 0) VColors.goldSoft else VColors.surfaceTint,
                    if (allDone) VColors.success else if (chapterCovered > 0) VColors.gold else VColors.ink3,
                    allDone,
                )
            }
        }
    }
}

@Composable
private fun SyllabusUnit(name: String, badgeText: String, badgeBg: Color, badgeFg: Color, allDone: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .shadow(1.dp, VShapes.md)
            .background(VColors.white, VShapes.md)
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
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
private fun HomeworkToolContent(viewModel: TeacherViewModel) {
    val homeworkState by viewModel.homeworkState.collectAsState()
    val homeworkItems = (homeworkState as? UiState.Success)?.data?.data?.items ?: emptyList()

    Column(modifier = Modifier.padding(24.dp)) {
        if (homeworkItems.isEmpty()) {
            Text(
                text = when (homeworkState) {
                    is UiState.Loading -> "Loading…"
                    is UiState.Error -> (homeworkState as UiState.Error).message
                    else -> "No homework assigned for this class"
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = VColors.ink3,
                modifier = Modifier.padding(vertical = 16.dp),
            )
        } else {
            homeworkItems.forEach { hw ->
                val badgeText: String
                val badgeBg: Color
                val badgeFg: Color
                when {
                    hw.isPastDue && hw.gradedCount == hw.totalCount && hw.totalCount > 0 -> {
                        badgeText = "Graded"; badgeBg = VColors.successSoft; badgeFg = VColors.success
                    }
                    hw.isPastDue -> {
                        badgeText = "Past due"; badgeBg = VColors.errorSoft; badgeFg = VColors.error
                    }
                    else -> {
                        badgeText = "Active"; badgeBg = VColors.goldSoft; badgeFg = VColors.gold
                    }
                }
                HomeworkItem(
                    title = hw.title,
                    badgeText = badgeText,
                    badgeBg = badgeBg,
                    badgeFg = badgeFg,
                    due = "Due: ${hw.dueDate}${if (hw.dueTime != null) " ${hw.dueTime}" else ""}",
                    "${hw.submittedCount} submitted",
                    "${hw.notSubmittedCount} not submitted",
                    "${hw.gradedCount} graded",
                )
            }
        }
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
private fun LessonToolContent(viewModel: TeacherViewModel) {
    val lessonPlansState by viewModel.lessonPlansState.collectAsState()
    val lessons = (lessonPlansState as? UiState.Success)?.data?.data ?: emptyList()

    Column(modifier = Modifier.padding(24.dp)) {
        if (lessons.isEmpty()) {
            Text(
                text = when (lessonPlansState) {
                    is UiState.Loading -> "Loading…"
                    is UiState.Error -> (lessonPlansState as UiState.Error).message
                    else -> "No lesson plans for this class"
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = VColors.ink3,
                modifier = Modifier.padding(vertical = 16.dp),
            )
        } else {
            lessons.forEach { lesson ->
                val faded = lesson.status == "completed"
                LessonItem(
                    date = lesson.plannedDate ?: "",
                    topic = lesson.title,
                    objectives = lesson.objectives.joinToString("; "),
                    faded = faded,
                )
            }
        }
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
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = VColors.violet.copy(alpha = alpha),
        )
        Text(
            text = topic,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = VColors.ink.copy(alpha = alpha),
            modifier = Modifier.padding(top = 4.dp),
        )
        Text(
            text = objectives,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = VColors.ink2.copy(alpha = alpha),
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}
