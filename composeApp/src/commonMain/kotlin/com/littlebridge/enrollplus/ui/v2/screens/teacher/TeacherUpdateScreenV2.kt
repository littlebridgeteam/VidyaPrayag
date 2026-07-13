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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
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
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import org.koin.compose.viewmodel.koinViewModel
import com.littlebridge.enrollplus.ui.v2.theme.VTheme
import androidx.compose.foundation.shape.RoundedCornerShape

/** The five scoped tools the Update tab fronts. */
enum class UpdateTool(
    val labelKey: String,
    val icon: ImageVector,
    val accent: Color,
    val soft: Color,
) {
    Attendance(StringKeys.TEACHER_ATTENDANCE, VIcons.ListChecks, VTheme.colors.violet, VTheme.colors.violetSoft),
    Marks(StringKeys.TC_MARKS, VIcons.GraduationCap, VTheme.colors.mint, VTheme.colors.mintSoft),
    Homework(StringKeys.TEACHER_HOMEWORK, VIcons.FileText, VTheme.colors.gold, VTheme.colors.goldSoft),
    Syllabus(StringKeys.TEACHER_SYLLABUS, VIcons.BookOpen, VTheme.colors.sky, VTheme.colors.skySoft),
    LessonPlan(StringKeys.TC_LESSON, VIcons.ClipboardList, VTheme.colors.coral, VTheme.colors.coralSoft),
}

/**
 * TeacherUpdateScreenV2 — the UPDATE tab, rebuilt from scratch on the premium
 * cream/violet token system.
 *
 * TWO layout modes:
 *
 *  A) SCOPE-GATE mode (no class chosen yet) — the WHOLE screen scrolls as one
 *     [LazyColumn], exactly like the Classes tab: header → tool rail → tool intro
 *     → "which class" heading → the scope rows. There is no nested scroll here, so
 *     the entire page glides under one finger.
 *
 *  B) SCOPED-TOOL mode (a class is chosen) — the header + sticky scope bar stay as
 *     fixed chrome and the active tool sub-screen fills the remaining BOUNDED height
 *     via weight(1f). Each sub-screen (Attendance/Marks/…) owns its own LazyColumn.
 *
 * CRASH-SAFETY: a child LazyColumn is NEVER nested inside a Column(verticalScroll)
 * or another parent-owned scroll — that hands the inner list an infinite max-height
 * and crashes at measure time. In mode A the single LazyColumn owns the scroll and
 * the rows are emitted flat via [scopeSelectorItems] (no nested list). In mode B the
 * sub-screen lives in a weight(1f) box (bounded height), so its own LazyColumn is safe.
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

    val asg = pickedAssignment

    if (asg == null) {
        // ── MODE A: scope gate — the WHOLE screen is a single scroll (like Classes).
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(VTheme.colors.cream)
                .statusBarsPadding(),
            contentPadding = PaddingValues(top = 12.dp, bottom = TeacherDockClearance),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // 1 — shared premium header.
            item {
                TeacherPremiumHeader(
                    teacherName = teacherName,
                    lead = appString(StringKeys.TC_LETS),
                    accent = appString(StringKeys.TC_UPDATE_ACCENT),
                    unreadCount = unreadCount,
                    onOpenNotifications = onOpenNotifications,
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
            }

            // 2 — tool rail (horizontal scroll is fine inside a vertical LazyColumn).
            item {
                ToolRail(
                    selected = tool,
                    onSelect = {
                        tool = it
                        pickedAssignment = null
                        pickedLabel = ""
                    },
                )
            }

            // 3 — tool intro hero.
            item {
                Box(Modifier.padding(horizontal = 16.dp)) { ToolIntroCard(tool) }
            }

            // 4 — "which class" heading.
            item {
                Box(Modifier.padding(horizontal = 16.dp)) {
                    ScopeSelectorHeading(
                        title = appString(StringKeys.TC_WHICH_CLASS),
                        caption = appString(
                            StringKeys.TC_PICK_CLASS_FOR,
                            "tool" to appString(tool.labelKey).lowercase(),
                        ),
                    )
                }
            }

            // 5 — the scope rows, emitted flat into THIS list (no nested scroll).
            scopeSelectorItems(
                classes = classesState.classes,
                onPick = { cls ->
                    pickedAssignment = cls.assignmentId
                    pickedLabel = scopeLabelFor(cls)
                },
                horizontalPadding = 16.dp,
            )
        }
    } else {
        // ── MODE B: scoped tool — fixed header + scope bar, bounded sub-screen.
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(VTheme.colors.cream)
                .statusBarsPadding()
                .padding(top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TeacherPremiumHeader(
                teacherName = teacherName,
                lead = appString(StringKeys.TC_LETS),
                accent = appString(StringKeys.TC_UPDATE_ACCENT),
                unreadCount = unreadCount,
                onOpenNotifications = onOpenNotifications,
                modifier = Modifier.padding(horizontal = 20.dp),
            )

            ToolRail(
                selected = tool,
                onSelect = {
                    tool = it
                    pickedAssignment = null
                    pickedLabel = ""
                },
            )

            Column(
                Modifier.fillMaxWidth().weight(1f).padding(horizontal = 16.dp),
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
        .clip(RoundedCornerShape(50))
        .clickable(interactionSource = ix, indication = null) { onClick() }
    if (active) {
        Row(
            base
                .background(Brush.horizontalGradient(listOf(VTheme.colors.violet, VTheme.colors.violetHover)))
                .padding(horizontal = 16.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(tool.icon, contentDescription = "", tint = VTheme.colors.white, modifier = Modifier.size(17.dp))
            Text(
                appString(tool.labelKey),
                style = VTheme.type.label.copy(color = VTheme.colors.white, fontWeight = FontWeight.Bold),
                maxLines = 1,
            )
        }
    } else {
        Row(
            base
                .background(VTheme.colors.surfaceCard)
                .border(1.dp, VTheme.colors.line, RoundedCornerShape(50))
                .padding(horizontal = 16.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(tool.icon, contentDescription = "", tint = tool.accent, modifier = Modifier.size(17.dp))
            Text(
                appString(tool.labelKey),
                style = VTheme.type.label.copy(color = VTheme.colors.ink2, fontWeight = FontWeight.SemiBold),
                maxLines = 1,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Scope gate pieces — a premium "pick a class" intro + the shared scope rows.
// (The rows themselves are emitted by scopeSelectorItems into the tab's LazyColumn.)
// ─────────────────────────────────────────────────────────────────────────────

/** A soft accent hero explaining the active write plane. */
@Composable
private fun ToolIntroCard(tool: UpdateTool) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(tool.soft)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            Modifier.size(48.dp).clip(RoundedCornerShape(18.dp)).background(tool.accent.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(tool.icon, contentDescription = "", tint = tool.accent, modifier = Modifier.size(24.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(
                appString(tool.labelKey),
                style = VTheme.type.h3.copy(fontSize = 18.sp, color = VTheme.colors.ink),
            )
            Text(
                toolBlurb(tool),
                style = VTheme.type.caption.copy(color = VTheme.colors.ink2),
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
            .clip(RoundedCornerShape(24.dp))
            .background(VTheme.colors.surfaceCard)
            .border(1.dp, VTheme.colors.line, RoundedCornerShape(24.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            Modifier.size(38.dp).clip(RoundedCornerShape(14.dp)).background(tool.soft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(tool.icon, contentDescription = "", tint = tool.accent, modifier = Modifier.size(19.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(
                appString(tool.labelKey),
                style = VTheme.type.caption.copy(fontWeight = FontWeight.Bold, color = tool.accent),
                maxLines = 1,
            )
            Text(
                label,
                style = VTheme.type.bodySmall.copy(fontWeight = FontWeight.SemiBold, color = VTheme.colors.ink),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Row(
            Modifier
                .clip(RoundedCornerShape(50))
                .background(VTheme.colors.violetSoft)
                .clickable(interactionSource = ix, indication = null) { onChange() }
                .padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(VIcons.ArrowLeft, contentDescription = "", tint = VTheme.colors.violet, modifier = Modifier.size(14.dp))
            Text(
                appString(StringKeys.TC_CHANGE),
                style = VTheme.type.caption.copy(fontWeight = FontWeight.SemiBold, color = VTheme.colors.violet),
            )
        }
    }
}

private fun scopeLabelFor(cls: TeacherClassSummaryDto): String {
    val classLabel = if (cls.section.isBlank()) cls.className else "${cls.className}-${cls.section}"
    return "$classLabel · ${cls.subject}"
}
