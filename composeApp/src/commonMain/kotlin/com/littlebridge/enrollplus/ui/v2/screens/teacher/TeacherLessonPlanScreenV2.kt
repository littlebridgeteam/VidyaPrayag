package com.littlebridge.enrollplus.ui.v2.screens.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.feature.teacher.domain.model.HomeworkItemDto
import com.littlebridge.enrollplus.feature.teacher.domain.model.SyllabusNodeDto
import com.littlebridge.enrollplus.feature.teacher.presentation.LessonPlanMode
import com.littlebridge.enrollplus.feature.teacher.presentation.LessonPlanSummary
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherLessonPlanViewModel
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonSize
import com.littlebridge.enrollplus.ui.v2.components.VButtonTone
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VInput
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel
import com.littlebridge.enrollplus.ui.v2.screens.teacher.TeacherSpinner

/**
 * TeacherLessonPlanScreenV2 — the scoped lesson plan surface (LESSON_PLANNING_SPEC P1-20).
 * Reached PRE-SCOPED with a pre-authorized [assignmentId]. Four modes:
 *   • LIST     — this class's lesson plans + create button + calendar/templates shortcuts
 *   • EDITOR   — create/edit: title, objectives, activities, resources, assessment, duration, date
 *   • CALENDAR — month grid with per-day lesson plans
 *   • TEMPLATES — list own+shared templates; instantiate or save new
 */
@Composable
fun TeacherLessonPlanScreenV2(
    assignmentId: String,
    scopeLabel: String,
    modifier: Modifier = Modifier,
    tool: UpdateTool = UpdateTool.LessonPlan,
    onToolChange: (UpdateTool) -> Unit = {},
    onChangeClass: () -> Unit = {},
    viewModel: TeacherLessonPlanViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()

    LaunchedEffect(assignmentId) {
        if (assignmentId.isNotBlank() && state.assignmentId != assignmentId) {
            viewModel.load(assignmentId, scopeLabel)
        }
    }

    Box(modifier.fillMaxSize().background(VColors.cream)) {
        when (state.mode) {
            LessonPlanMode.List -> LessonPlanListMode(viewModel, scopeLabel, tool, onToolChange, onChangeClass)
            LessonPlanMode.Editor -> LessonPlanEditorMode(viewModel, scopeLabel, tool, onToolChange, onChangeClass)
            LessonPlanMode.Calendar -> LessonPlanCalendarMode(viewModel, scopeLabel, tool, onToolChange, onChangeClass)
            LessonPlanMode.Templates -> LessonPlanTemplatesMode(viewModel, scopeLabel, tool, onToolChange, onChangeClass)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// LIST mode
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun LessonPlanListMode(
    viewModel: TeacherLessonPlanViewModel,
    scopeLabel: String,
    tool: UpdateTool,
    onToolChange: (UpdateTool) -> Unit,
    onChangeClass: () -> Unit,
) {
    val state by viewModel.state.collectAsStateV2()

    LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            ScopedToolHeader(
                tool = tool,
                scopeLabel = scopeLabel,
                onToolChange = onToolChange,
                onChangeClass = onChangeClass,
            )
        }

        item {
            VtCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        VButton(
                            text = appString(StringKeys.TC_NEW_PLAN),
                            onClick = { viewModel.openNewEditor() },
                            modifier = Modifier.weight(1f),
                            variant = VButtonVariant.Primary,
                            tone = VButtonTone.Lavender,
                            size = VButtonSize.Md,
                            leading = { Icon(VIcons.Plus, contentDescription = null, modifier = Modifier.size(15.dp)) },
                        )
                        VButton(
                            text = appString(StringKeys.TC_CALENDAR),
                            onClick = { viewModel.openCalendar() },
                            modifier = Modifier.weight(1f),
                            variant = VButtonVariant.Secondary,
                            tone = VButtonTone.Lavender,
                            size = VButtonSize.Md,
                            leading = { Icon(VIcons.Calendar, contentDescription = null, modifier = Modifier.size(15.dp)) },
                        )
                        VButton(
                            text = appString(StringKeys.TC_TEMPLATES),
                            onClick = { viewModel.openTemplates() },
                            modifier = Modifier.weight(1f),
                            variant = VButtonVariant.Secondary,
                            tone = VButtonTone.Lavender,
                            size = VButtonSize.Md,
                            leading = { Icon(VIcons.ClipboardList, contentDescription = null, modifier = Modifier.size(15.dp)) },
                        )
                    }
                }
            }
        }

        // Status filter chips
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                LessonStatusChip(appString(StringKeys.TC_ALL), state.statusFilter == null) { viewModel.setStatusFilter(null) }
                LessonStatusChip(appString(StringKeys.TC_PLANNED), state.statusFilter == "planned") { viewModel.setStatusFilter("planned") }
                LessonStatusChip(appString(StringKeys.TC_COMPLETED), state.statusFilter == "completed") { viewModel.setStatusFilter("completed") }
                LessonStatusChip(appString(StringKeys.TC_SKIPPED), state.statusFilter == "skipped") { viewModel.setStatusFilter("skipped") }
            }
        }

        // Post-complete quiz suggestion banner
        if (state.showQuizSuggestion) {
            item { QuizSuggestionBanner(viewModel, state.completedPlanTitle) }
        }

        when {
            state.isLoading && state.items.isEmpty() -> item {
                Box(Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) { TeacherSpinner() }
            }
            state.error != null && state.items.isEmpty() -> item {
                VtCard {
                    Column {
                        Text(appString(StringKeys.TC_COULDNT_LOAD_LESSON_PLANS), style = VTypography.caption, color = VColors.ink)
                        Spacer(Modifier.height(8.dp))
                        VButton(appString(StringKeys.COMMON_BUTTON_RETRY), onClick = { viewModel.retry() }, tone = VButtonTone.Lavender, size = VButtonSize.Sm)
                    }
                }
            }
            state.items.isEmpty() -> item {
                VtCard {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        VtIconDisc(VIcons.ClipboardList, tint = VColors.violet, bg = VColors.violet.copy(alpha = 0.14f), size = 48.dp, glyph = 24.dp)
                        Spacer(Modifier.height(10.dp))
                        Text(appString(StringKeys.TC_NO_LESSON_PLANS_YET), style = VTypography.caption, color = VColors.ink)
                        Text(appString(StringKeys.TC_CREATE_FIRST_LESSON_PLAN), style = VTypography.caption, color = VColors.ink3)
                    }
                }
            }
            else -> items(state.items, key = { it.id }) { plan ->
                LessonPlanRow(plan) { viewModel.openExistingEditor(plan.id) }
            }
        }
    }
}

@Composable
private fun LessonStatusChip(label: String, active: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .clip(VShapes.full)
            .background(if (active) VColors.violet.copy(alpha = 0.14f) else VColors.surfaceTint)
            .border(1.dp, if (active) VColors.violet.copy(alpha = 0.5f) else VColors.line, VShapes.full)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 7.dp),
    ) {
        Text(
            label,
            style = VTypography.caption,
            color = if (active) VColors.violetInk else VColors.ink3,
        )
    }
}

@Composable
private fun LessonPlanRow(plan: LessonPlanSummary, onClick: () -> Unit) {
    val statusColor = when (plan.status) {
        "completed" -> VColors.success
        "skipped" -> VColors.ink3
        else -> VColors.violet
    }
    val statusIcon = when (plan.status) {
        "completed" -> VIcons.Check
        "skipped" -> VIcons.Close
        else -> VIcons.ClipboardList
    }
    Row(
        Modifier
            .fillMaxWidth()
            .clip(VShapes.lg)
            .background(VColors.white)
            .border(1.dp, VColors.line, VShapes.lg)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        VtIconDisc(statusIcon, tint = statusColor, bg = statusColor.copy(alpha = 0.12f), size = 44.dp, glyph = 22.dp)
        Column(Modifier.weight(1f)) {
            Text(
                plan.title,
                style = VTypography.caption,
                color = VColors.ink,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(4.dp))
            val dateText = plan.plannedDate?.let { prettyDateShort(it) } ?: appString(StringKeys.TC_NO_DATE)
            val unitText = plan.unitTitle?.let { " · $it" } ?: ""
            Text(
                "$dateText · ${plan.durationMinutes} min$unitText",
                style = VTypography.caption,
                color = VColors.ink2,
            )
        }
        VtPill(plan.status.uppercase(), bg = statusColor.copy(alpha = 0.12f), fg = statusColor)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// EDITOR mode
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun LessonPlanEditorMode(
    viewModel: TeacherLessonPlanViewModel,
    scopeLabel: String,
    tool: UpdateTool,
    onToolChange: (UpdateTool) -> Unit,
    onChangeClass: () -> Unit,
) {
    val state by viewModel.state.collectAsStateV2()
    val e = state.editor

    var objectiveText by remember { mutableStateOf("") }
    var resourceText by remember { mutableStateOf("") }
    var activityText by remember { mutableStateOf("") }
    var activityDuration by remember { mutableStateOf("15") }

    LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 14.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Scrollable scoped chrome
        item {
            ScopedToolHeader(
                tool = tool,
                scopeLabel = scopeLabel,
                onToolChange = onToolChange,
                onChangeClass = onChangeClass,
            )
        }

        // Header
        item {
            VtCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                        Modifier.size(34.dp).clip(CircleShape).background(VColors.surfaceTint)
                            .clickable { viewModel.closeEditor() },
                        contentAlignment = Alignment.Center,
                    ) { Icon(VIcons.ArrowLeft, contentDescription = appString(StringKeys.COMMON_BUTTON_BACK), tint = VColors.ink2, modifier = Modifier.size(18.dp)) }
                    Spacer(Modifier.width(12.dp))
                    VtEyebrow(if (e.isNew) appString(StringKeys.TC_NEW_LESSON_PLAN) else appString(StringKeys.TC_EDIT_LESSON_PLAN), dot = VColors.violet)
                }
            }
        }

        // Title
        item {
            VtCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(appString(StringKeys.TC_TITLE), style = VTypography.label.copy(color = VColors.ink2))
                    VInput(value = e.title, onValueChange = viewModel::setEditorTitle, placeholder = appString(StringKeys.TC_LESSON_TITLE))
                }
            }
        }

        // Planned date + duration
        item {
            VtCard {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(Modifier.weight(1f)) {
                        Text(appString(StringKeys.TC_PLANNED_DATE), style = VTypography.label.copy(color = VColors.ink2))
                        Spacer(Modifier.height(6.dp))
                        VInput(value = e.plannedDate, onValueChange = viewModel::setEditorDate, placeholder = "YYYY-MM-DD")
                    }
                    Column(Modifier.width(100.dp)) {
                        Text(appString(StringKeys.TC_MINUTES), style = VTypography.label.copy(color = VColors.ink2))
                        Spacer(Modifier.height(6.dp))
                        VInput(value = e.durationMinutes.toString(), onValueChange = { v -> v.toIntOrNull()?.let { viewModel.setEditorDuration(it) } }, placeholder = "45")
                    }
                }
            }
        }

        // Curriculum unit picker
        item {
            VtCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(appString(StringKeys.TC_CURRICULUM_UNIT_OPTIONAL), style = VTypography.label.copy(color = VColors.ink2))
                    if (state.syllabusUnits.isEmpty()) {
                        Text(appString(StringKeys.TC_NO_SYLLABUS_UNITS), style = VTypography.caption.copy(color = VColors.ink3))
                    } else {
                        UnitPicker(
                            units = state.syllabusUnits,
                            selectedId = e.curriculumUnitId,
                            onPick = viewModel::setEditorUnit,
                        )
                    }
                }
            }
        }

        // Homework link picker
        item {
            VtCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(appString(StringKeys.TC_LINK_HOMEWORK_OPTIONAL), style = VTypography.label.copy(color = VColors.ink2))
                    if (state.homeworkOptions.isEmpty()) {
                        Text(appString(StringKeys.TC_NO_ACTIVE_HOMEWORK_CLASS), style = VTypography.caption.copy(color = VColors.ink3))
                    } else {
                        HomeworkPicker(
                            homework = state.homeworkOptions,
                            selectedId = e.homeworkId,
                            onPick = viewModel::setEditorHomework,
                        )
                    }
                }
            }
        }

        // Quiz attach — show existing quizzes for this assignment
        item {
            VtCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(VIcons.GraduationCap, contentDescription = null, tint = VColors.violet, modifier = Modifier.size(16.dp))
                        Text(appString(StringKeys.TC_QUIZ), style = VTypography.label.copy(color = VColors.ink2))
                    }
                    if (state.existingQuizzes.isEmpty()) {
                        Text(appString(StringKeys.TC_NO_QUIZZES_CREATED_YET), style = VTypography.caption.copy(color = VColors.ink3))
                    } else {
                        state.existingQuizzes.forEach { quiz ->
                            Row(
                                Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(VColors.surfaceTint).padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Box(
                                    Modifier.size(28.dp).clip(CircleShape).background(VColors.violet.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center,
                                ) { Icon(VIcons.GraduationCap, contentDescription = null, tint = VColors.violetInk, modifier = Modifier.size(14.dp)) }
                                Column(Modifier.weight(1f)) {
                                    Text(quiz.title.ifBlank { "Untitled Quiz" }, style = VTypography.body.copy(color = VColors.ink))
                                    Text("${quiz.questions.size} questions · ${quiz.status}", style = VTypography.caption.copy(color = VColors.ink3))
                                }
                                VtPill(quiz.status.uppercase(), bg = VColors.violet.copy(alpha = 0.12f), fg = VColors.violetInk)
                            }
                        }
                    }
                }
            }
        }

        // Objectives
        item {
            VtCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(appString(StringKeys.TC_OBJECTIVES), style = VTypography.label.copy(color = VColors.ink2))
                    e.objectives.forEachIndexed { i, obj ->
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(Modifier.size(6.dp).clip(CircleShape).background(VColors.violet))
                            Text(obj, style = VTypography.body.copy(color = VColors.ink), modifier = Modifier.weight(1f))
                                                    Box(
                                Modifier.size(24.dp).clip(CircleShape).background(VColors.surfaceTint)
                                    .clickable { viewModel.removeObjective(i) },
                                contentAlignment = Alignment.Center,
                            ) { Icon(VIcons.Close, contentDescription = appString(StringKeys.TC_REMOVE), tint = VColors.ink3, modifier = Modifier.size(12.dp)) }
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        VInput(
                            value = objectiveText,
                            onValueChange = { objectiveText = it },
                            placeholder = appString(StringKeys.TC_ADD_OBJECTIVE),
                            modifier = Modifier.weight(1f),
                        )
                        VButton(
                            text = appString(StringKeys.TC_ADD),
                            onClick = { viewModel.addObjective(objectiveText); objectiveText = "" },
                            size = VButtonSize.Sm,
                            variant = VButtonVariant.Secondary,
                            tone = VButtonTone.Lavender,
                        )
                    }
                }
            }
        }

        // Activities
        item {
            VtCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(appString(StringKeys.TC_ACTIVITIES), style = VTypography.label.copy(color = VColors.ink2))
                    e.activities.forEachIndexed { i, act ->
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(Modifier.size(6.dp).clip(CircleShape).background(VColors.success))
                            Text("${act.activity} (${act.durationMin} min)", style = VTypography.body.copy(color = VColors.ink), modifier = Modifier.weight(1f))
                                                    Box(
                                Modifier.size(24.dp).clip(CircleShape).background(VColors.surfaceTint)
                                    .clickable { viewModel.removeActivity(i) },
                                contentAlignment = Alignment.Center,
                            ) { Icon(VIcons.Close, contentDescription = appString(StringKeys.TC_REMOVE), tint = VColors.ink3, modifier = Modifier.size(12.dp)) }
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        VInput(
                            value = activityText,
                            onValueChange = { activityText = it },
                            placeholder = appString(StringKeys.TC_ADD_ACTIVITY),
                            modifier = Modifier.weight(1f),
                        )
                        VInput(
                            value = activityDuration,
                            onValueChange = { activityDuration = it },
                            placeholder = appString(StringKeys.TC_MIN),
                            modifier = Modifier.width(60.dp),
                        )
                        VButton(
                            text = appString(StringKeys.TC_ADD),
                            onClick = {
                                val dur = activityDuration.toIntOrNull() ?: 15
                                viewModel.addActivity(activityText, dur)
                                activityText = ""
                                activityDuration = "15"
                            },
                            size = VButtonSize.Sm,
                            variant = VButtonVariant.Secondary,
                            tone = VButtonTone.Teal,
                        )
                    }
                }
            }
        }

        // Resources
        item {
            VtCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(appString(StringKeys.TC_RESOURCES), style = VTypography.label.copy(color = VColors.ink2))
                    e.resources.forEachIndexed { i, res ->
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(Modifier.size(6.dp).clip(CircleShape).background(VColors.success))
                            Text(res, style = VTypography.body.copy(color = VColors.ink), modifier = Modifier.weight(1f))
                                                    Box(
                                Modifier.size(24.dp).clip(CircleShape).background(VColors.surfaceTint)
                                    .clickable { viewModel.removeResource(i) },
                                contentAlignment = Alignment.Center,
                            ) { Icon(VIcons.Close, contentDescription = appString(StringKeys.TC_REMOVE), tint = VColors.ink3, modifier = Modifier.size(12.dp)) }
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        VInput(
                            value = resourceText,
                            onValueChange = { resourceText = it },
                            placeholder = appString(StringKeys.TC_ADD_RESOURCE),
                            modifier = Modifier.weight(1f),
                        )
                        VButton(
                            text = appString(StringKeys.TC_ADD),
                            onClick = { viewModel.addResource(resourceText); resourceText = "" },
                            size = VButtonSize.Sm,
                            variant = VButtonVariant.Secondary,
                            tone = VButtonTone.Teal,
                        )
                    }
                }
            }
        }

        // Assessment method
        item {
            VtCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(appString(StringKeys.TC_ASSESSMENT_METHOD), style = VTypography.label.copy(color = VColors.ink2))
                    VInput(value = e.assessmentMethod, onValueChange = viewModel::setEditorAssessment, placeholder = appString(StringKeys.TC_HOW_ASSESS_OPTIONAL))
                }
            }
        }

        // Error
        if (e.error != null) {
            item {
                Text(e.error ?: "", style = VTypography.caption.copy(color = VColors.error))
            }
        }

        // Actions
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                VButton(
                    text = if (e.isNew) appString(StringKeys.TC_CREATE_PLAN) else appString(StringKeys.TC_SAVE_CHANGES),
                    onClick = { viewModel.savePlan() },
                    full = true,
                    variant = VButtonVariant.Primary,
                    tone = VButtonTone.Lavender,
                    size = VButtonSize.Md,
                    loading = e.isSaving,
                )
                if (!e.isNew) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        VButton(
                            text = appString(StringKeys.TC_COMPLETE),
                            onClick = { viewModel.completePlan() },
                            modifier = Modifier.weight(1f),
                            variant = VButtonVariant.Secondary,
                            tone = VButtonTone.Teal,
                            size = VButtonSize.Md,
                            leading = { Icon(VIcons.Check, contentDescription = null, modifier = Modifier.size(15.dp)) },
                            enabled = !e.isCompleted && !e.isSkipped,
                        )
                        VButton(
                            text = appString(StringKeys.TC_SKIP),
                            onClick = { viewModel.skipPlan() },
                            modifier = Modifier.weight(1f),
                            variant = VButtonVariant.Secondary,
                            tone = VButtonTone.Lavender,
                            size = VButtonSize.Md,
                            enabled = !e.isCompleted && !e.isSkipped,
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        VButton(
                            text = appString(StringKeys.TC_SAVE_AS_TEMPLATE),
                            onClick = { viewModel.openSaveTemplateDialog() },
                            modifier = Modifier.weight(1f),
                            variant = VButtonVariant.Ghost,
                            size = VButtonSize.Sm,
                        )
                        VButton(
                            text = appString(StringKeys.COMMON_BUTTON_DELETE),
                            onClick = { viewModel.deletePlan() },
                            modifier = Modifier.weight(1f),
                            variant = VButtonVariant.Ghost,
                            size = VButtonSize.Sm,
                        )
                    }
                }
            }
        }
    }

    // Save template dialog
    if (state.showSaveTemplateDialog) {
        SaveTemplateDialog(viewModel)
    }
}

@Composable
private fun SaveTemplateDialog(viewModel: TeacherLessonPlanViewModel) {
    val state by viewModel.state.collectAsStateV2()
    VtCard {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(appString(StringKeys.TC_SAVE_AS_TEMPLATE), style = VTypography.h3.copy(color = VColors.ink))
            VInput(value = state.templateTitle, onValueChange = viewModel::setTemplateTitle, placeholder = appString(StringKeys.TC_TEMPLATE_TITLE))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                    Modifier.size(24.dp).clip(CircleShape)
                        .background(if (state.templateIsShared) VColors.violet.copy(alpha = 0.14f) else VColors.surfaceTint)
                        .border(1.dp, if (state.templateIsShared) VColors.violet else VColors.line, CircleShape)
                        .clickable { viewModel.setTemplateShared(!state.templateIsShared) },
                    contentAlignment = Alignment.Center,
                ) {
                    if (state.templateIsShared) Icon(VIcons.Check, contentDescription = null, tint = VColors.violetInk, modifier = Modifier.size(14.dp))
                }
                Text(appString(StringKeys.TC_SHARE_WITH_TEACHERS), style = VTypography.body.copy(color = VColors.ink))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                VButton(appString(StringKeys.COMMON_BUTTON_CANCEL), onClick = { viewModel.closeSaveTemplateDialog() }, modifier = Modifier.weight(1f), variant = VButtonVariant.Ghost, size = VButtonSize.Md)
                VButton(appString(StringKeys.COMMON_BUTTON_SAVE), onClick = { viewModel.saveTemplate() }, modifier = Modifier.weight(1f), tone = VButtonTone.Lavender, size = VButtonSize.Md, loading = state.isSavingTemplate)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// CALENDAR mode
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun LessonPlanCalendarMode(
    viewModel: TeacherLessonPlanViewModel,
    scopeLabel: String,
    tool: UpdateTool,
    onToolChange: (UpdateTool) -> Unit,
    onChangeClass: () -> Unit,
) {
    val state by viewModel.state.collectAsStateV2()
    val cal = state.calendar

    LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 14.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            ScopedToolHeader(
                tool = tool,
                scopeLabel = scopeLabel,
                onToolChange = onToolChange,
                onChangeClass = onChangeClass,
            )
        }

        item {
            VtCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                        Modifier.size(34.dp).clip(CircleShape).background(VColors.surfaceTint)
                            .clickable { viewModel.closeCalendar() },
                        contentAlignment = Alignment.Center,
                    ) { Icon(VIcons.ArrowLeft, contentDescription = appString(StringKeys.COMMON_BUTTON_BACK), tint = VColors.ink2, modifier = Modifier.size(18.dp)) }
                    Spacer(Modifier.width(12.dp))
                    VtEyebrow(appString(StringKeys.TC_CALENDAR), dot = VColors.violet)
                    Spacer(Modifier.width(8.dp))
                    Text(cal.month.ifBlank { "—" }, style = VTypography.h3.copy(color = VColors.ink))
                }
            }
        }

        if (state.isCalendarLoading && cal.days.isEmpty()) {
            item { Box(Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) { TeacherSpinner() } }
        } else if (cal.days.isEmpty()) {
            item {
                VtCard {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        VtIconDisc(VIcons.Calendar, tint = VColors.violet, bg = VColors.violet.copy(alpha = 0.14f), size = 48.dp, glyph = 24.dp)
                        Spacer(Modifier.height(10.dp))
                        Text(appString(StringKeys.TC_NO_PLANS_THIS_MONTH), style = VTypography.h3.copy(color = VColors.ink))
                    }
                }
            }
        } else {
            items(cal.days, key = { it.date }) { day ->
                VtCard {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            prettyDateShort(day.date),
                            style = VTypography.body.copy(color = VColors.ink),
                        )
                        day.plans.forEach { plan ->
                            val statusColor = when (plan.status) {
                                "completed" -> VColors.success
                                "skipped" -> VColors.ink3
                                else -> VColors.violet
                            }
                            Row(
                                Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                                    .background(statusColor.copy(alpha = 0.08f))
                                    .clickable { viewModel.openExistingEditor(plan.id) }
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Box(Modifier.size(6.dp).clip(CircleShape).background(statusColor))
                                Text(plan.title, style = VTypography.body.copy(color = VColors.ink), modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(plan.status, style = VTypography.caption.copy(color = statusColor))
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// TEMPLATES mode
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun LessonPlanTemplatesMode(
    viewModel: TeacherLessonPlanViewModel,
    scopeLabel: String,
    tool: UpdateTool,
    onToolChange: (UpdateTool) -> Unit,
    onChangeClass: () -> Unit,
) {
    val state by viewModel.state.collectAsStateV2()

    LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 14.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            ScopedToolHeader(
                tool = tool,
                scopeLabel = scopeLabel,
                onToolChange = onToolChange,
                onChangeClass = onChangeClass,
            )
        }

        item {
            VtCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                        Modifier.size(34.dp).clip(CircleShape).background(VColors.surfaceTint)
                            .clickable { viewModel.closeTemplates() },
                        contentAlignment = Alignment.Center,
                    ) { Icon(VIcons.ArrowLeft, contentDescription = appString(StringKeys.COMMON_BUTTON_BACK), tint = VColors.ink2, modifier = Modifier.size(18.dp)) }
                    Spacer(Modifier.width(12.dp))
                    VtEyebrow(appString(StringKeys.TC_TEMPLATES), dot = VColors.violet)
                }
            }
        }

        if (state.isTemplatesLoading && state.templates.isEmpty()) {
            item { Box(Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) { TeacherSpinner() } }
        } else if (state.templatesError != null && state.templates.isEmpty()) {
            item {
                VtCard {
                    Column {
                        Text(appString(StringKeys.TC_COULDNT_LOAD_TEMPLATES), style = VTypography.body.copy(color = VColors.ink))
                        Spacer(Modifier.height(8.dp))
                        VButton(appString(StringKeys.COMMON_BUTTON_RETRY), onClick = { viewModel.loadTemplates() }, tone = VButtonTone.Lavender, size = VButtonSize.Sm)
                    }
                }
            }
        } else if (state.templates.isEmpty()) {
            item {
                VtCard {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        VtIconDisc(VIcons.ClipboardList, tint = VColors.violet, bg = VColors.violet.copy(alpha = 0.14f), size = 48.dp, glyph = 24.dp)
                        Spacer(Modifier.height(10.dp))
                        Text(appString(StringKeys.TC_NO_TEMPLATES_YET), style = VTypography.h3.copy(color = VColors.ink))
                        Text(appString(StringKeys.TC_SAVE_LESSON_AS_TEMPLATE), style = VTypography.caption.copy(color = VColors.ink3))
                    }
                }
            }
        } else {
            items(state.templates, key = { it.id }) { tpl ->
                TemplateRow(
                    template = tpl,
                    onInstantiate = { viewModel.openInstantiateDialog(tpl.id) },
                    onDelete = { viewModel.deleteTemplate(tpl.id) },
                )
            }
        }
    }

    // Instantiate dialog
    if (state.showInstantiateDialog) {
        InstantiateDialog(viewModel)
    }
}

@Composable
private fun TemplateRow(
    template: com.littlebridge.enrollplus.feature.teacher.domain.model.LessonTemplateDto,
    onInstantiate: () -> Unit,
    onDelete: () -> Unit,
) {
    VtCard {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                VtIconDisc(VIcons.ClipboardList, tint = VColors.violet, bg = VColors.violet.copy(alpha = 0.12f), size = 40.dp, glyph = 20.dp)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(template.title, style = VTypography.body.copy(color = VColors.ink), maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text("${template.objectives.size} objectives · ${template.durationMinutes} min", style = VTypography.caption.copy(color = VColors.ink2))
                }
                if (template.isShared) {
                    VtPill(appString(StringKeys.TC_SHARED), bg = VColors.mint.copy(alpha = 0.12f), fg = VColors.success)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                VButton(
                    text = appString(StringKeys.TC_USE_TEMPLATE),
                    onClick = onInstantiate,
                    modifier = Modifier.weight(1f),
                    variant = VButtonVariant.Primary,
                    tone = VButtonTone.Lavender,
                    size = VButtonSize.Sm,
                )
                VButton(
                    text = appString(StringKeys.COMMON_BUTTON_DELETE),
                    onClick = onDelete,
                    modifier = Modifier.weight(1f),
                    variant = VButtonVariant.Ghost,
                    size = VButtonSize.Sm,
                )
            }
        }
    }
}

@Composable
private fun InstantiateDialog(viewModel: TeacherLessonPlanViewModel) {
    val state by viewModel.state.collectAsStateV2()
    VtCard {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(appString(StringKeys.TC_INSTANTIATE_FROM_TEMPLATE), style = VTypography.h3.copy(color = VColors.ink))
            Text(appString(StringKeys.TC_PICK_DATE_FOR_LESSON), style = VTypography.body.copy(color = VColors.ink2))
            VInput(value = state.instantiateDate, onValueChange = viewModel::setInstantiateDate, placeholder = "YYYY-MM-DD")
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                VButton(appString(StringKeys.COMMON_BUTTON_CANCEL), onClick = { viewModel.closeInstantiateDialog() }, modifier = Modifier.weight(1f), variant = VButtonVariant.Ghost, size = VButtonSize.Md)
                VButton(appString(StringKeys.COMMON_BUTTON_CREATE), onClick = { viewModel.instantiateFromTemplate() }, modifier = Modifier.weight(1f), tone = VButtonTone.Lavender, size = VButtonSize.Md, loading = state.isInstantiating)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Pickers for curriculum unit + homework link
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun UnitPicker(
    units: List<SyllabusNodeDto>,
    selectedId: String?,
    onPick: (String?) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        PickerRow(label = appString(StringKeys.TC_NO_UNIT_LINKED), selected = selectedId == null, onClick = { onPick(null) })
        units.forEach { unit ->
            val indent = (unit.depth.coerceIn(0, 3) * 12).dp
            PickerRow(
                label = unit.title,
                selected = selectedId == unit.id,
                onClick = { onPick(unit.id) },
                modifier = Modifier.padding(start = indent),
            )
        }
    }
}

@Composable
private fun HomeworkPicker(
    homework: List<HomeworkItemDto>,
    selectedId: String?,
    onPick: (String?) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        PickerRow(label = appString(StringKeys.TC_NO_HOMEWORK_LINKED), selected = selectedId == null, onClick = { onPick(null) })
        homework.forEach { hw ->
            PickerRow(
                label = "${hw.title} (due ${hw.dueDate})",
                selected = selectedId == hw.id,
                onClick = { onPick(hw.id) },
            )
        }
    }
}

@Composable
private fun PickerRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) VColors.violet.copy(alpha = 0.10f) else VColors.surfaceTint)
            .border(1.dp, if (selected) VColors.violet.copy(alpha = 0.4f) else VColors.line, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            Modifier.size(20.dp).clip(CircleShape)
                .background(if (selected) VColors.violet else androidx.compose.ui.graphics.Color.Transparent)
                .border(1.dp, if (selected) VColors.violet else VColors.line, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (selected) Icon(VIcons.Check, contentDescription = null, tint = androidx.compose.ui.graphics.Color.White, modifier = Modifier.size(12.dp))
        }
        Text(
            label,
            style = VTypography.body.copy(color = if (selected) VColors.violetInk else VColors.ink),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Post-complete quiz suggestion banner
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun QuizSuggestionBanner(
    viewModel: TeacherLessonPlanViewModel,
    completedPlanTitle: String,
) {
    VtCard {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    Modifier.size(36.dp).clip(CircleShape).background(VColors.violet.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center,
                ) { Icon(VIcons.GraduationCap, contentDescription = null, tint = VColors.violetInk, modifier = Modifier.size(18.dp)) }
                Column(Modifier.weight(1f)) {
                    Text(
                        appString(StringKeys.TC_LESSON_COMPLETED),
                        style = VTypography.body.copy(color = VColors.ink),
                    )
                    Text(
                        appString(StringKeys.TC_CREATE_QUIZ_TO_ASSESS, "title" to completedPlanTitle),
                        style = VTypography.caption.copy(color = VColors.ink2),
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                VButton(
                    text = appString(StringKeys.TC_NOT_NOW),
                    onClick = { viewModel.dismissQuizSuggestion() },
                    modifier = Modifier.weight(1f),
                    variant = VButtonVariant.Ghost,
                    size = VButtonSize.Sm,
                )
                VButton(
                    text = appString(StringKeys.TC_CREATE_QUIZ),
                    onClick = { viewModel.dismissQuizSuggestion() },
                    modifier = Modifier.weight(1f),
                    variant = VButtonVariant.Secondary,
                    tone = VButtonTone.Lavender,
                    size = VButtonSize.Sm,
                    leading = { Icon(VIcons.GraduationCap, contentDescription = null, modifier = Modifier.size(14.dp)) },
                )
            }
        }
    }
}
