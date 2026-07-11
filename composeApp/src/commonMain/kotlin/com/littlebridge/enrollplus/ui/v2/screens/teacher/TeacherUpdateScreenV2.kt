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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonSize
import com.littlebridge.enrollplus.ui.v2.components.VButtonTone
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VPullRefresh
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
    onOpenMessages: () -> Unit = {},
    classesViewModel: TeacherClassesViewModel = koinViewModel(),
) {
    val classesState by classesViewModel.state.collectAsStateV2()

    var tool by rememberSaveable { mutableStateOf(initialTool) }
    var pickedAssignment by rememberSaveable { mutableStateOf(initialAssignmentId) }
    var pickedLabel by rememberSaveable { mutableStateOf(initialScopeLabel) }
    var dismissedSuggestions by remember { mutableStateOf(setOf<UpdateTool>()) }

    val asg = pickedAssignment

    if (asg == null) {
        // ── MODE A: scope gate — the WHOLE screen is a single scroll (like Classes).
        var isRefreshing by remember { mutableStateOf(false) }
        LaunchedEffect(classesState.isLoading) {
            if (!classesState.isLoading) isRefreshing = false
        }
        VPullRefresh(
            isRefreshing = isRefreshing,
            onRefresh = { isRefreshing = true; classesViewModel.load() },
            modifier = modifier.fillMaxSize().background(VColors.cream),
        ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
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

            // 5b — loading / error / empty states when no classes to show.
            if (classesState.classes.isEmpty()) {
                item {
                    Box(Modifier.padding(horizontal = 16.dp)) {
                        when {
                            classesState.isLoading -> TeacherCenterState { TeacherSpinner() }
                            classesState.error != null -> TeacherCenterState {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(appString(StringKeys.TC_COULDNT_LOAD_CLASSES), style = VtT.bodyStrong.coloredV(VtC.navyDeep))
                                    Spacer(Modifier.height(4.dp))
                                    Text(classesState.error ?: "", style = VtT.caption.coloredV(VtC.ink3))
                                    Spacer(Modifier.height(14.dp))
                                    VButton(appString(StringKeys.COMMON_BUTTON_TRY_AGAIN), onClick = classesViewModel::load, size = VButtonSize.Sm, tone = VButtonTone.Lavender)
                                }
                            }
                            else -> VtEmptyCard(
                                title = appString(StringKeys.TC_NO_ALLOCATIONS),
                                subtext = "Your class assignments will appear here once allocated.",
                                icon = VIcons.BookOpen,
                            )
                        }
                    }
                }
            }
        }
        }
    } else {
        // ── MODE B: scoped tool — fixed header + scope bar, bounded sub-screen.
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(VColors.cream)
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
                            UpdateTool.Attendance -> TeacherAttendanceScreenV2(asg, pickedLabel, onOpenMessages = onOpenMessages)
                            UpdateTool.Marks -> TeacherMarksScreenV2(asg, pickedLabel)
                            UpdateTool.Syllabus -> TeacherSyllabusScreenV2(asg, pickedLabel)
                            UpdateTool.Homework -> TeacherHomeworkScreenV2(asg, pickedLabel)
                            UpdateTool.LessonPlan -> TeacherLessonPlanScreenV2(asg, pickedLabel)
                        }
                    }
                }

                // Cross-tool switch suggestion (non-blocking toast)
                val suggestedTool = crossToolSuggestion(tool, dismissedSuggestions)
                if (suggestedTool != null) {
                    CrossToolSuggestionToast(
                        fromTool = tool,
                        toTool = suggestedTool,
                        onSwitch = {
                            tool = suggestedTool
                        },
                        onDismiss = {
                            dismissedSuggestions = dismissedSuggestions + suggestedTool
                        },
                    )
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
// Scope gate pieces — a premium "pick a class" intro + the shared scope rows.
// (The rows themselves are emitted by scopeSelectorItems into the tab's LazyColumn.)
// ─────────────────────────────────────────────────────────────────────────────

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

// ─────────────────────────────────────────────────────────────────────────────
// Cross-tool switch suggestions — non-blocking toast that suggests switching
// to a related tool after the teacher finishes with the current one.
// ─────────────────────────────────────────────────────────────────────────────

private fun crossToolSuggestion(
    current: UpdateTool,
    dismissed: Set<UpdateTool>,
): UpdateTool? {
    val next = when (current) {
        UpdateTool.Attendance -> UpdateTool.Marks
        UpdateTool.Marks -> UpdateTool.Homework
        UpdateTool.Homework -> UpdateTool.LessonPlan
        UpdateTool.LessonPlan -> UpdateTool.Syllabus
        UpdateTool.Syllabus -> UpdateTool.Attendance
    }
    return if (next in dismissed) null else next
}

private fun suggestionMessage(from: UpdateTool, to: UpdateTool): String = when (from) {
    UpdateTool.Attendance -> "Attendance done? Try updating ${to.labelKey.lowercase()} for this class."
    UpdateTool.Marks -> "Marks updated? Consider assigning ${to.labelKey.lowercase()} next."
    UpdateTool.Homework -> "Homework set? Plan a ${to.labelKey.lowercase()} to support it."
    UpdateTool.LessonPlan -> "Lesson planned? Track it in the ${to.labelKey.lowercase()}."
    UpdateTool.Syllabus -> "Syllabus updated? Mark ${to.labelKey.lowercase()} for today."
}

@Composable
private fun CrossToolSuggestionToast(
    fromTool: UpdateTool,
    toTool: UpdateTool,
    onSwitch: () -> Unit,
    onDismiss: () -> Unit,
) {
    val ixDismiss = remember { MutableInteractionSource() }
    val ixSwitch = remember { MutableInteractionSource() }
    Row(
        Modifier
            .fillMaxWidth()
            .clip(VShapes.lg)
            .background(VColors.violetSoft)
            .border(1.dp, VColors.violet.copy(alpha = 0.2f), VShapes.lg)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            Modifier.size(32.dp).clip(VShapes.md).background(toTool.accent.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(toTool.icon, contentDescription = null, tint = toTool.accent, modifier = Modifier.size(16.dp))
        }
        Text(
            suggestionMessage(fromTool, toTool),
            style = VTypography.caption.copy(fontSize = 12.sp, color = VColors.ink2, fontWeight = FontWeight.Medium),
            modifier = Modifier.weight(1f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Row(
            Modifier
                .clip(VShapes.full)
                .background(VColors.violet)
                .clickable(interactionSource = ixSwitch, indication = null) { onSwitch() }
                .padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                appString(toTool.labelKey),
                style = VTypography.label.copy(color = VColors.white, fontWeight = FontWeight.Bold, fontSize = 11.sp),
                maxLines = 1,
            )
            Icon(VIcons.ArrowRight, contentDescription = null, tint = VColors.white, modifier = Modifier.size(12.dp))
        }
        Box(
            Modifier.size(24.dp).clip(VShapes.full)
                .clickable(interactionSource = ixDismiss, indication = null) { onDismiss() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(VIcons.Close, contentDescription = null, tint = VColors.ink3, modifier = Modifier.size(14.dp))
        }
    }
}
