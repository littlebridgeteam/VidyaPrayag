package com.littlebridge.enrollplus.ui.v2.screens.teacher

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.feature.teacher.domain.model.TeacherClassSummaryDto
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherClassesViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import org.koin.compose.viewmodel.koinViewModel

/** The five scoped tools the Update tab fronts. */
enum class UpdateTool(val labelKey: String, val icon: ImageVector) {
    Attendance(StringKeys.TEACHER_ATTENDANCE, VIcons.ListChecks),
    Marks(StringKeys.TC_MARKS, VIcons.GraduationCap),
    Syllabus(StringKeys.TEACHER_SYLLABUS, VIcons.BookOpen),
    Homework(StringKeys.TEACHER_HOMEWORK, VIcons.FileText),
    LessonPlan(StringKeys.TC_LESSON, VIcons.ClipboardList),
}

/**
 * TeacherUpdateScreenV2 — the UPDATE tab. A tool grid chooses the active write plane
 * (Attendance · Marks · Syllabus · Homework · Lesson Plan); each tool first asks the teacher
 * to pick one of their allocations (the scope gate → assignmentId), then hands off to the
 * tool's scoped sub-screen. A back affordance returns to the scope gate so the teacher can
 * switch class without leaving the tab.
 *
 * [initialAssignmentId]/[initialScopeLabel]/[initialTool] let a Home deep-link jump straight
 * into a pre-scoped tool.
 */
@Composable
fun TeacherUpdateScreenV2(
    modifier: Modifier = Modifier,
    initialAssignmentId: String? = null,
    initialScopeLabel: String = "",
    initialTool: UpdateTool = UpdateTool.Attendance,
    classesViewModel: TeacherClassesViewModel = koinViewModel(),
) {
    val classesState by classesViewModel.state.collectAsStateV2()

    var tool by rememberSaveable { mutableStateOf(initialTool) }
    var pickedAssignment by rememberSaveable { mutableStateOf(initialAssignmentId) }
    var pickedLabel by rememberSaveable { mutableStateOf(initialScopeLabel) }

    // IMPORTANT: this screen must NOT wrap its body in a verticalScroll. The scope
    // gate ([TeacherScopeSelector]) and every scoped tool sub-screen (Attendance,
    // Marks, Syllabus, Homework, Lesson Plan) each host their own vertically
    // scrollable LazyColumn. Nesting them inside a Column(verticalScroll) hands the
    // inner LazyColumn an infinite max-height constraint and crashes at measure time
    // (IllegalStateException: "Vertically scrollable component was measured with an
    // infinity maximum height constraints"). Instead the header + tool grid stay
    // fixed at the top, and the body fills the remaining bounded height with weight(1f).
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VColors.cream)
            .padding(top = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        UpdateHeader()
        ToolGrid(
            selected = tool,
            onSelect = {
                tool = it
                pickedAssignment = null
                pickedLabel = ""
            },
        )

        Box(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
        ) {
            val asg = pickedAssignment
            if (asg == null) {
                ScopeGate(tool = tool, classes = classesState.classes) { cls ->
                    pickedAssignment = cls.assignmentId
                    pickedLabel = scopeLabelFor(cls)
                }
            } else {
                Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    ScopeBar(label = pickedLabel, onChange = { pickedAssignment = null; pickedLabel = "" })
                    // The scoped tool screen owns the remaining bounded height and its
                    // own LazyColumn/scroll — hence weight(1f) here, never verticalScroll.
                    Box(Modifier.fillMaxWidth().weight(1f)) {
                        AnimatedContent(
                            targetState = tool,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "updateTool",
                        ) { active ->
                            when (active) {
                                UpdateTool.Attendance -> TeacherAttendanceScreenV2(asg, pickedLabel)
                                UpdateTool.Marks -> TeacherMarksScreenV2(asg, pickedLabel)
                                UpdateTool.Syllabus -> TeacherSyllabusScreenV2(asg, pickedLabel)
                                UpdateTool.Homework -> TeacherHomeworkScreenV2(asg, pickedLabel)
                                UpdateTool.LessonPlan -> TeacherLessonPlanScreenV2(asg, pickedLabel)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UpdateHeader() {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = "Update",
            style = VTypography.h2,
            color = VColors.ink,
        )
        Text(
            text = "Mark & publish",
            style = VTypography.bodySmall,
            color = VColors.ink2,
        )
    }
}

@Composable
private fun ToolGrid(
    selected: UpdateTool,
    onSelect: (UpdateTool) -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            UpdateTool.entries.take(3).forEach { t ->
                ToolChip(
                    tool = t,
                    active = t == selected,
                    modifier = Modifier.weight(1f),
                    onClick = { onSelect(t) },
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)) {
            UpdateTool.entries.drop(3).forEach { t ->
                ToolChip(
                    tool = t,
                    active = t == selected,
                    modifier = Modifier.weight(1f),
                    onClick = { onSelect(t) },
                )
            }
        }
    }
}

@Composable
private fun ToolChip(
    tool: UpdateTool,
    active: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val ix = remember { MutableInteractionSource() }
    Column(
        modifier = modifier
            .clip(VShapes.lg)
            .background(if (active) VColors.violetSoft else VColors.surfaceCard)
            .border(
                1.dp,
                if (active) VColors.violet.copy(alpha = 0.5f) else VColors.outlineSoft,
                VShapes.lg,
            )
            .clickable(interactionSource = ix, indication = null) { onClick() }
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(
            imageVector = tool.icon,
            contentDescription = appString(tool.labelKey),
            tint = if (active) VColors.violet else VColors.ink2,
            modifier = Modifier.size(22.dp),
        )
        Text(
            text = appString(tool.labelKey),
            style = VTypography.label,
            color = if (active) VColors.violet else VColors.ink2,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
    }
}

@Composable
private fun ScopeGate(
    tool: UpdateTool,
    classes: List<TeacherClassSummaryDto>,
    onPick: (TeacherClassSummaryDto) -> Unit,
) {
    TeacherScopeSelector(
        classes = classes,
        onPick = onPick,
        title = appString(StringKeys.TC_WHICH_CLASS),
        caption = appString(StringKeys.TC_PICK_CLASS_FOR, "tool" to appString(tool.labelKey).lowercase()),
    )
}

@Composable
private fun ScopeBar(label: String, onChange: () -> Unit) {
    val ix = remember { MutableInteractionSource() }
    Row(
        Modifier
            .fillMaxWidth()
            .clip(VShapes.xl)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.outlineSoft, VShapes.xl)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(VIcons.School, contentDescription = null, tint = VColors.violet, modifier = Modifier.size(18.dp))
        Text(
            text = label,
            style = VTypography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = VColors.ink,
            modifier = Modifier.weight(1f),
            maxLines = 1,
        )
        Row(
            Modifier
                .clip(VShapes.full)
                .background(VColors.violetSoft)
                .clickable(interactionSource = ix, indication = null) { onChange() }
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(VIcons.ArrowLeft, contentDescription = null, tint = VColors.violet, modifier = Modifier.size(14.dp))
            Text(
                text = appString(StringKeys.TC_CHANGE),
                style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold),
                color = VColors.violet,
            )
        }
    }
}

private fun scopeLabelFor(cls: TeacherClassSummaryDto): String {
    val classLabel = if (cls.section.isBlank()) cls.className else "${cls.className}-${cls.section}"
    return "$classLabel · ${cls.subject}"
}
