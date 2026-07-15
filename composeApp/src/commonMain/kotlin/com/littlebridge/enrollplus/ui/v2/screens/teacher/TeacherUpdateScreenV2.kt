package com.littlebridge.enrollplus.ui.v2.screens.teacher

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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
import com.littlebridge.enrollplus.ui.v2.screens.teacher.TeacherKit.TeacherSpinner

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
 * TeacherUpdateScreenV2 — the UPDATE tab, premium rebuild.
 *
 * A) SCOPE-GATE (no class chosen): one vertically-scrollable LazyColumn so the
 *    entire page glides under one finger. Clean greeting, compact tool rail,
 *    and a scannable class list.
 *
 * B) SCOPED-TOOL (class chosen): the chrome is deliberately compact so the tool
 *    itself gets maximum height. The active tool owns its own LazyColumn inside
 *    a weight(1f) box (bounded height — no crash).
 *
 * Layout safety: no nested LazyColumns, no fixed heights on growing content,
 * primary actions stay visible.
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
    onImportMarks: () -> Unit = {},
    classesViewModel: TeacherClassesViewModel = koinViewModel(),
) {
    val classesState by classesViewModel.state.collectAsStateV2()

    var tool by rememberSaveable { mutableStateOf(initialTool) }
    var pickedAssignment by rememberSaveable { mutableStateOf(initialAssignmentId) }
    var pickedLabel by rememberSaveable { mutableStateOf(initialScopeLabel) }

    val asg = pickedAssignment

    if (asg == null) {
        ScopeGateMode(
            tool = tool,
            onToolChange = { tool = it },
            onPickClass = { cls ->
                pickedAssignment = cls.assignmentId
                pickedLabel = scopeLabelFor(cls)
            },
            teacherName = teacherName,
            unreadCount = unreadCount,
            onOpenNotifications = onOpenNotifications,
            classesState = classesState,
            onRefresh = { classesViewModel.load() },
            modifier = modifier,
        )
    } else {
        ScopedToolMode(
            tool = tool,
            assignmentId = asg,
            scopeLabel = pickedLabel,
            onToolChange = { tool = it },
            onChangeClass = { pickedAssignment = null; pickedLabel = "" },
            onOpenMessages = onOpenMessages,
            onImportMarks = onImportMarks,
            modifier = modifier,
        )
    }
}

// ── SCOPE GATE: pick a class, then pick a tool ──────────────────────────────

@Composable
private fun ScopeGateMode(
    tool: UpdateTool,
    onToolChange: (UpdateTool) -> Unit,
    onPickClass: (TeacherClassSummaryDto) -> Unit,
    teacherName: String,
    unreadCount: Int,
    onOpenNotifications: () -> Unit,
    classesState: com.littlebridge.enrollplus.feature.teacher.presentation.TeacherClassesState,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isRefreshing by remember { mutableStateOf(false) }
    LaunchedEffect(classesState.isLoading) {
        if (!classesState.isLoading) isRefreshing = false
    }

    VPullRefresh(
        isRefreshing = isRefreshing,
        onRefresh = { isRefreshing = true; onRefresh() },
        modifier = modifier.fillMaxSize().background(VColors.cream),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            contentPadding = PaddingValues(top = 16.dp, bottom = TeacherDockClearance),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
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

            item {
                ToolRail(
                    selected = tool,
                    onSelect = onToolChange,
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
            }

            item {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        appString(StringKeys.TC_WHICH_CLASS),
                        style = VTypography.h3,
                        color = VColors.ink,
                    )
                    Text(
                        appString(StringKeys.TC_PICK_CLASS_FOR, "tool" to appString(tool.labelKey).lowercase()),
                        style = VTypography.caption,
                        color = VColors.ink2,
                    )
                }
            }

            scopeSelectorItems(
                classes = classesState.classes,
                onPick = onPickClass,
                horizontalPadding = 20.dp,
            )

            if (classesState.classes.isEmpty()) {
                item {
                    Box(Modifier.padding(horizontal = 20.dp)) {
                        when {
                            classesState.isLoading -> TeacherCenterState { TeacherSpinner() }
                            classesState.error != null -> TeacherCenterState {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        appString(StringKeys.TC_COULDNT_LOAD_CLASSES),
                                        style = VTypography.caption.copy(color = VColors.ink),
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(classesState.error ?: "", style = VTypography.caption, color = VColors.ink3)
                                    Spacer(Modifier.height(14.dp))
                                    VButton(
                                        appString(StringKeys.COMMON_BUTTON_TRY_AGAIN),
                                        onClick = onRefresh,
                                        size = VButtonSize.Sm,
                                        tone = VButtonTone.Lavender,
                                    )
                                }
                            }
                            else -> VtEmptyCard(
                                title = appString(StringKeys.TC_NO_ALLOCATIONS),
                                subtext = appString(StringKeys.TC_ASSIGNMENTS_WILL_APPEAR),
                                icon = VIcons.BookOpen,
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── SCOPED TOOL: compact chrome, maximum tool height ─────────────────────────

@Composable
private fun ScopedToolMode(
    tool: UpdateTool,
    assignmentId: String,
    scopeLabel: String,
    onToolChange: (UpdateTool) -> Unit,
    onChangeClass: () -> Unit,
    onOpenMessages: () -> Unit,
    onImportMarks: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VColors.cream)
            .statusBarsPadding(),
    ) {
        AnimatedContent(
            targetState = tool,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "updateTool",
        ) { active ->
            when (active) {
                UpdateTool.Attendance -> TeacherAttendanceScreenV2(
                    assignmentId,
                    scopeLabel,
                    tool = active,
                    onToolChange = onToolChange,
                    onChangeClass = onChangeClass,
                    onOpenMessages = onOpenMessages,
                )
                UpdateTool.Marks -> TeacherMarksScreenV2(
                    assignmentId,
                    scopeLabel,
                    tool = active,
                    onToolChange = onToolChange,
                    onChangeClass = onChangeClass,
                    onImportMarks = onImportMarks,
                )
                UpdateTool.Syllabus -> TeacherSyllabusScreenV2(
                    assignmentId,
                    scopeLabel,
                    tool = active,
                    onToolChange = onToolChange,
                    onChangeClass = onChangeClass,
                )
                UpdateTool.Homework -> TeacherHomeworkScreenV2(
                    assignmentId,
                    scopeLabel,
                    tool = active,
                    onToolChange = onToolChange,
                    onChangeClass = onChangeClass,
                )
                UpdateTool.LessonPlan -> TeacherLessonPlanScreenV2(
                    assignmentId,
                    scopeLabel,
                    tool = active,
                    onToolChange = onToolChange,
                    onChangeClass = onChangeClass,
                )
            }
        }
    }
}

// ── Tool rail ───────────────────────────────────────────────────────────────

@Composable
internal fun ToolRail(
    selected: UpdateTool,
    onSelect: (UpdateTool) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
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
    val base = Modifier
        .clip(VShapes.full)
        .clickable { onClick() }
    val bg = if (active) tool.accent else VColors.surfaceCard
    val fg = if (active) VColors.white else VColors.ink2
    val border = if (active) null else VColors.line

    Row(
        base
            .background(bg)
            .then(if (border != null) Modifier.border(1.dp, border, VShapes.full) else Modifier)
            .padding(horizontal = 14.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(tool.icon, contentDescription = null, tint = fg, modifier = Modifier.size(16.dp))
        Text(
            appString(tool.labelKey),
            style = VTypography.label,
            color = fg,
            maxLines = 1,
        )
    }
}

// ── Scope bar (compact context strip once a class is chosen) ─────────────────

@Composable
internal fun ScopeBar(tool: UpdateTool, label: String, onChange: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.lg)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            Modifier.size(32.dp).clip(VShapes.md).background(tool.soft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(tool.icon, contentDescription = null, tint = tool.accent, modifier = Modifier.size(17.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(
                appString(tool.labelKey),
                style = VTypography.caption.copy(color = tool.accent),
                maxLines = 1,
            )
            Text(
                label,
                style = VTypography.caption.copy(color = VColors.ink),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Row(
            Modifier
                .clip(VShapes.full)
                .background(VColors.violetSoft)
                .clickable { onChange() }
                .padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(VIcons.ArrowLeft, contentDescription = null, tint = VColors.violet, modifier = Modifier.size(13.dp))
            Text(
                appString(StringKeys.TC_CHANGE),
                style = VTypography.caption.copy(color = VColors.violet),
            )
        }
    }
}

/** Combined rail + scope bar rendered as the first item in a scoped tool's LazyColumn.
 * This makes the entire scoped screen scroll as one unit. */
@Composable
internal fun ScopedToolHeader(
    tool: UpdateTool,
    scopeLabel: String,
    onToolChange: (UpdateTool) -> Unit,
    onChangeClass: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ToolRail(selected = tool, onSelect = onToolChange)
        ScopeBar(tool = tool, label = scopeLabel, onChange = onChangeClass)
    }
}

private fun scopeLabelFor(cls: TeacherClassSummaryDto): String {
    val classLabel = if (cls.section.isBlank()) cls.className else "${cls.className}-${cls.section}"
    return "$classLabel · ${cls.subject}"
}
