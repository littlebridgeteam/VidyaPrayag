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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
enum class UpdateTool(
    val labelKey: String,
    val icon: ImageVector,
    val accent: Color,
    val soft: Color,
) {
    Attendance(StringKeys.TEACHER_ATTENDANCE, VIcons.ListChecks, VColors.violet, VColors.violetSoft),
    Marks(StringKeys.TC_MARKS, VIcons.GraduationCap, VColors.mint, VColors.mintSoft),
    Homework(StringKeys.TEACHER_HOMEWORK, VIcons.FileText, VColors.gold, VColors.goldSoft),
    Syllabus(StringKeys.TEACHER_SYLLABUS, VIcons.BookOpen, VColors.sky, VColors.skySoft),
    LessonPlan(StringKeys.TC_LESSON, VIcons.ClipboardList, VColors.coral, VColors.coralSoft),
}

/**
 * TeacherUpdateScreenV2 — the UPDATE tab, rebuilt from scratch on the premium
 * cream/violet token system.
 *
 * Structure (top → bottom, all fixed-height chrome except the workspace):
 *   1. [TeacherPremiumHeader]  — the shared portal header ("let's update").
 *   2. Tool rail               — a horizontal pill rail (Attendance · Marks ·
 *                                Homework · Syllabus · Lesson Plan). The active
 *                                tool wears a violet gradient; the rest are clean
 *                                white pills with the tool's accent glyph.
 *   3. Workspace (weight 1f)   — either the scope gate (no class chosen) showing a
 *                                premium "pick a class" intro + [TeacherScopeSelector],
 *                                or the scoped tool with a sticky scope bar on top.
 *
 * CRASH-SAFETY: the body is NEVER wrapped in a verticalScroll. The scope gate and
 * every scoped tool sub-screen host their own scrollable LazyColumn; nesting them
 * inside a Column(verticalScroll) hands the inner list an infinite max-height and
 * crashes at measure time. Header + rail stay fixed; the workspace fills the
 * remaining bounded height via weight(1f).
 *
 * [initialAssignmentId]/[initialScopeLabel]/[initialTool] let a Home CTA jump
 * straight into a pre-scoped tool. Signature is otherwise PRESERVED.
 */
@Composable
fun TeacherUpdateScreenV2(
    modifier: Modifier = Modifier,
    initialAssignmentId: String? = null,
    initialScopeLabel: String = "",
    initialTool: UpdateTool = UpdateTool.Attendance,
    teacherName: String = "",
    unreadCount: Int = 0,
    onOpenNotifications: () -> Unit = {},
    classesViewModel: TeacherClassesViewModel = koinViewModel(),
) {
    val classesState by classesViewModel.state.collectAsStateV2()

    var tool by rememberSaveable { mutableStateOf(initialTool) }
    var pickedAssignment by rememberSaveable { mutableStateOf(initialAssignmentId) }
    var pickedLabel by rememberSaveable { mutableStateOf(initialScopeLabel) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VColors.cream)
            .padding(top = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // 1 — shared premium header (fixed chrome).
        TeacherPremiumHeader(
            teacherName = teacherName,
            lead = appString(StringKeys.TC_LETS),
            accent = appString(StringKeys.TC_UPDATE_ACCENT),
            unreadCount = unreadCount,
            onOpenNotifications = onOpenNotifications,
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        // 2 — tool rail.
        ToolRail(
            selected = tool,
            onSelect = {
                tool = it
                pickedAssignment = null
                pickedLabel = ""
            },
        )

        // 3 — workspace (bounded height; owns its own scroll).
        Box(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
        ) {
            val asg = pickedAssignment
            if (asg == null) {
                ScopeGate(
                    tool = tool,
                    classes = classesState.classes,
                    onPick = { cls ->
                        pickedAssignment = cls.assignmentId
                        pickedLabel = scopeLabelFor(cls)
                    },
                )
            } else {
                Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    ScopeBar(
                        tool = tool,
                        label = pickedLabel,
                        onChange = { pickedAssignment = null; pickedLabel = "" },
                    )
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

// ─────────────────────────────────────────────────────────────────────────────
// Tool rail — horizontal pill selector. Active = violet gradient; rest = white.
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ToolRail(
    selected: UpdateTool,
    onSelect: (UpdateTool) -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        UpdateTool.entries.forEach { t ->
            ToolPill(tool = t, active = t == selected, onClick = { onSelect(t) })
        }
    }
}

@Composable
private fun ToolPill(
    tool: UpdateTool,
    active: Boolean,
    onClick: () -> Unit,
) {
    val ix = remember { MutableInteractionSource() }
    val base = Modifier
        .clip(VShapes.full)
        .clickable(interactionSource = ix, indication = null) { onClick() }
    if (active) {
        Row(
            base
                .background(Brush.horizontalGradient(listOf(VColors.violet, VColors.violetHover)))
                .padding(horizontal = 16.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(tool.icon, contentDescription = null, tint = VColors.white, modifier = Modifier.size(17.dp))
            Text(
                appString(tool.labelKey),
                style = VTypography.label.copy(color = VColors.white, fontWeight = FontWeight.Bold),
                maxLines = 1,
            )
        }
    } else {
        Row(
            base
                .background(VColors.surfaceCard)
                .border(1.dp, VColors.line, VShapes.full)
                .padding(horizontal = 16.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(tool.icon, contentDescription = null, tint = tool.accent, modifier = Modifier.size(17.dp))
            Text(
                appString(tool.labelKey),
                style = VTypography.label.copy(color = VColors.ink2, fontWeight = FontWeight.SemiBold),
                maxLines = 1,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Scope gate — a premium "pick a class" intro + the shared scope selector.
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ScopeGate(
    tool: UpdateTool,
    classes: List<TeacherClassSummaryDto>,
    onPick: (TeacherClassSummaryDto) -> Unit,
) {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ToolIntroCard(tool)
        // The selector owns the remaining bounded height + its own scroll.
        Box(Modifier.fillMaxWidth().weight(1f)) {
            TeacherScopeSelector(
                classes = classes,
                onPick = onPick,
                title = appString(StringKeys.TC_WHICH_CLASS),
                caption = appString(
                    StringKeys.TC_PICK_CLASS_FOR,
                    "tool" to appString(tool.labelKey).lowercase(),
                ),
            )
        }
    }
}

/** A soft accent hero explaining the active write plane. */
@Composable
private fun ToolIntroCard(tool: UpdateTool) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(VShapes.xl)
            .background(tool.soft)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            Modifier.size(48.dp).clip(VShapes.lg).background(tool.accent.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(tool.icon, contentDescription = null, tint = tool.accent, modifier = Modifier.size(24.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(
                appString(tool.labelKey),
                style = VTypography.h3.copy(fontSize = 18.sp, color = VColors.ink),
            )
            Text(
                toolBlurb(tool),
                style = VTypography.caption.copy(color = VColors.ink2),
            )
        }
    }
}

@Composable
private fun toolBlurb(tool: UpdateTool): String = when (tool) {
    UpdateTool.Attendance -> appString(StringKeys.TC_UPDATE_BLURB_ATTENDANCE)
    UpdateTool.Marks -> appString(StringKeys.TC_UPDATE_BLURB_MARKS)
    UpdateTool.Homework -> appString(StringKeys.TC_UPDATE_BLURB_HOMEWORK)
    UpdateTool.Syllabus -> appString(StringKeys.TC_UPDATE_BLURB_SYLLABUS)
    UpdateTool.LessonPlan -> appString(StringKeys.TC_UPDATE_BLURB_LESSON)
}

// ─────────────────────────────────────────────────────────────────────────────
// Scope bar — sticky context strip once a class is chosen.
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ScopeBar(tool: UpdateTool, label: String, onChange: () -> Unit) {
    val ix = remember { MutableInteractionSource() }
    Row(
        Modifier
            .fillMaxWidth()
            .clip(VShapes.xl)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.xl)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            Modifier.size(38.dp).clip(VShapes.md).background(tool.soft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(tool.icon, contentDescription = null, tint = tool.accent, modifier = Modifier.size(19.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(
                appString(tool.labelKey),
                style = VTypography.caption.copy(fontWeight = FontWeight.Bold, color = tool.accent),
                maxLines = 1,
            )
            Text(
                label,
                style = VTypography.bodySmall.copy(fontWeight = FontWeight.SemiBold, color = VColors.ink),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Row(
            Modifier
                .clip(VShapes.full)
                .background(VColors.violetSoft)
                .clickable(interactionSource = ix, indication = null) { onChange() }
                .padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(VIcons.ArrowLeft, contentDescription = null, tint = VColors.violet, modifier = Modifier.size(14.dp))
            Text(
                appString(StringKeys.TC_CHANGE),
                style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold, color = VColors.violet),
            )
        }
    }
}

private fun scopeLabelFor(cls: TeacherClassSummaryDto): String {
    val classLabel = if (cls.section.isBlank()) cls.className else "${cls.className}-${cls.section}"
    return "$classLabel · ${cls.subject}"
}
