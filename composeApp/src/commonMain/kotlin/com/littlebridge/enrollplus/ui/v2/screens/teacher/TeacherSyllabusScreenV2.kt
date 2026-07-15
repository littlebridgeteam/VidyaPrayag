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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.feature.teacher.domain.model.QuizDto
import com.littlebridge.enrollplus.feature.teacher.domain.model.QuizQuestionDto
import com.littlebridge.enrollplus.feature.teacher.domain.model.SylAutoFillChapter
import com.littlebridge.enrollplus.feature.teacher.presentation.SyllabusUnit
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherSyllabusViewModel
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
import androidx.compose.ui.text.TextStyle
import com.littlebridge.enrollplus.util.formatDecimal
import org.koin.compose.viewmodel.koinViewModel


/**
 * TeacherSyllabusScreenV2 — the scoped syllabus tracker (Doc 08 §2). Reached PRE-SCOPED with a
 * pre-authorized [assignmentId]. The core gesture is a SINGLE TAP on a unit row → optimistic
 * coverage toggle (no form, no save). Hierarchy (chapter ▸ topic ▸ subtopic) comes pre-flattened
 * from the server. An "Edit" toggle reveals the deliberate add/rename/delete affordances.
 *
 * Agentic features: AI syllabus parse (paste text → preview → confirm), daily class log popup,
 * quiz generation per unit, quiz list with publish.
 */
@Composable
fun TeacherSyllabusScreenV2(
    assignmentId: String,
    scopeLabel: String,
    modifier: Modifier = Modifier,
    tool: UpdateTool = UpdateTool.Syllabus,
    onToolChange: (UpdateTool) -> Unit = {},
    onChangeClass: () -> Unit = {},
    viewModel: TeacherSyllabusViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()

    LaunchedEffect(assignmentId) {
        if (assignmentId.isNotBlank() && state.assignmentId != assignmentId) {
            viewModel.load(assignmentId)
            viewModel.loadQuizzes()
            viewModel.loadPaceWarning()
        }
    }

    Box(modifier.fillMaxSize().background(VColors.cream)) {
        when {
            state.isLoading && state.units.isEmpty() -> TeacherCenterState { TeacherSpinner() }
            state.error != null && state.units.isEmpty() -> TeacherCenterState {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(appString(StringKeys.TC_COULDNT_LOAD_SYLLABUS), style = VTypography.h3.copy(color = VColors.ink))
                    Spacer(Modifier.height(12.dp))
                    VButton(appString(StringKeys.COMMON_BUTTON_RETRY), onClick = { viewModel.retry() }, tone = VButtonTone.Lavender)
                }
            }
            else -> SyllabusBody(viewModel, scopeLabel, tool, onToolChange, onChangeClass)
        }

        // ── Parse syllabus bottom sheet ──
        if (state.showParsePreview) ParseSyllabusSheet(viewModel)

        // ── Auto-fill preview sheet ──
        if (state.showAutoFillPreview) AutoFillPreviewSheet(viewModel)

        // ── Daily log popup ──
        if (state.showDailyLogPopup) DailyLogPopup(viewModel)

        // ── Quiz generation sheet ──
        if (state.showQuizSheet) QuizSheet(viewModel)

        // ── Quiz preview sheet (after AI generation, before publishing) ──
        if (state.showQuizPreview) QuizPreviewSheet(viewModel)

        // ── Quiz leaderboard sheet ──
        if (state.showLeaderboard) QuizLeaderboardSheet(viewModel)
    }
}

@Composable
private fun SyllabusBody(
    viewModel: TeacherSyllabusViewModel,
    scopeLabel: String,
    tool: UpdateTool,
    onToolChange: (UpdateTool) -> Unit,
    onChangeClass: () -> Unit,
) {
    val state by viewModel.state.collectAsStateV2()
    val pct = (state.progress * 100).toInt()

    LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // ── Scrollable scoped chrome ──
        item {
            ScopedToolHeader(
                tool = tool,
                scopeLabel = scopeLabel,
                onToolChange = onToolChange,
                onChangeClass = onChangeClass,
            )
        }

        // ── Header: compact progress + edit toggle ──
        item {
            VtCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            VtEyebrow(appString(StringKeys.TC_SYLLABUS), dot = VColors.success)
                        }
                        Box(
                            Modifier.size(34.dp).clip(CircleShape).background(if (state.isEditing) VColors.violet.copy(alpha = 0.14f) else VColors.surfaceTint)
                                .clickable { viewModel.toggleEditing() },
                            contentAlignment = Alignment.Center,
                        ) { Icon(VIcons.Edit3, contentDescription = appString(StringKeys.TC_EDIT), tint = if (state.isEditing) VColors.violetInk else VColors.ink2, modifier = Modifier.size(16.dp)) }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("$pct%", style = VTypography.h2, color = VColors.success)
                        Column(Modifier.weight(1f)) {
                            androidx.compose.foundation.Canvas(Modifier.fillMaxWidth().height(8.dp)) {
                                drawRoundRect(color = VColors.surfaceTint, size = size, cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx()))
                                val w = size.width * state.progress.coerceIn(0f, 1f)
                                drawRoundRect(color = VColors.success, size = size.copy(width = w), cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx()))
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                appString(StringKeys.TC_N_OF_N_UNITS_COVERED, "covered" to state.coveredCount.toString(), "total" to state.totalCount.toString()),
                                style = VTypography.caption,
                                color = VColors.ink2,
                            )
                        }
                    }
                }
            }
        }

        // ── Pace warning banner (if off-track) ──
        val pw = state.paceWarning
        if (pw != null && pw.level != "ON_TRACK") {
            item { PaceWarningBanner(pw) }
        }

        // ── Draft approval bar (if draft units exist) ──
        if (state.hasDraftUnits || state.draftUnits.isNotEmpty()) {
            item { DraftApprovalBar(viewModel) }
        }

        // ── Agentic action buttons (always visible) ──
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                VButton(
                    appString(StringKeys.TC_AUTO_FILL),
                    onClick = { viewModel.autoFill() },
                    modifier = Modifier.weight(1f),
                    variant = VButtonVariant.Secondary,
                    tone = VButtonTone.Lavender,
                    size = VButtonSize.Md,
                    loading = state.isAutoFilling,
                    leading = { Icon(VIcons.Sparkles, contentDescription = null, modifier = Modifier.size(16.dp)) },
                )
                VButton(
                    appString(StringKeys.TC_DAILY_LOG),
                    onClick = { viewModel.openDailyLogPopup() },
                    modifier = Modifier.weight(1f),
                    variant = VButtonVariant.Secondary,
                    tone = VButtonTone.Teal,
                    size = VButtonSize.Md,
                    leading = { Icon(VIcons.ClipboardList, contentDescription = null, modifier = Modifier.size(16.dp)) },
                )
                VButton(
                    appString(StringKeys.TC_QUIZ),
                    onClick = { viewModel.openQuizSheetFromButton() },
                    modifier = Modifier.weight(1f),
                    variant = VButtonVariant.Secondary,
                    tone = VButtonTone.Lavender,
                    size = VButtonSize.Md,
                    leading = { Icon(VIcons.GraduationCap, contentDescription = null, modifier = Modifier.size(16.dp)) },
                )
            }
        }

        // ── Edit mode: add chapter button ──
        if (state.isEditing) {
            item {
                if (state.addingUnderParentId == null) {
                    VButton(
                        appString(StringKeys.TC_ADD_A_CHAPTER),
                        onClick = { viewModel.openAdd(null) },
                        full = true,
                        variant = VButtonVariant.Secondary,
                        tone = VButtonTone.Teal,
                        size = VButtonSize.Md,
                        leading = { Icon(VIcons.Plus, contentDescription = null, modifier = Modifier.size(15.dp)) },
                    )
                } else {
                    AddUnitComposer(viewModel)
                }
            }
        }

        // ── Empty state with 3 clear options ──
        if (state.units.isEmpty() && state.autoFillError == null) {
            item { EmptyStateOptions(viewModel) }
        } else if (state.units.isEmpty() && state.autoFillError != null) {
            item {
                VtCard {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        VtIconDisc(VIcons.AlertCircle, tint = VColors.error, bg = VColors.error.copy(alpha = 0.14f), size = 48.dp, glyph = 24.dp)
                        Spacer(Modifier.height(10.dp))
                        Text(appString(StringKeys.TC_NO_NCERT_REFERENCE_FOUND), style = VTypography.bodySmall, color = VColors.ink, fontWeight = FontWeight.SemiBold)
                        Text(state.autoFillError ?: "", style = VTypography.caption, color = VColors.ink3)
                        Spacer(Modifier.height(12.dp))
                        EmptyStateOptions(viewModel)
                    }
                }
            }
        } else {
            // ── Syllabus unit rows (3-level hierarchy) ──
            items(state.units, key = { it.id }) { u ->
                SyllabusRow(
                    u,
                    isUpdating = state.updatingUnitId == u.id,
                    editing = state.isEditing,
                    isDraft = u.approvalStatus == "DRAFT",
                    onToggle = { viewModel.toggleUnit(u.id) },
                    onAddTopic = { viewModel.openAdd(u.id) },
                    onDelete = { viewModel.deleteUnit(u.id) },
                )
            }
        }

        // ── Quiz list section ──
        if (state.quizzes.isNotEmpty()) {
            item {
                Spacer(Modifier.height(6.dp))
                VtEyebrow(appString(StringKeys.TC_QUIZZES), dot = VColors.violet)
                Spacer(Modifier.height(8.dp))
            }
            items(state.quizzes, key = { it.id }) { q ->
                QuizRow(q, onPublish = { viewModel.publishQuiz(q.id) }, onLeaderboard = { viewModel.loadLeaderboard(q.id) })
            }
        }
    }
}

@Composable
private fun AddUnitComposer(viewModel: TeacherSyllabusViewModel) {
    val state by viewModel.state.collectAsStateV2()
    val isChapter = state.addingUnderParentId.isNullOrBlank()
    VtCard {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(if (isChapter) appString(StringKeys.TC_NEW_CHAPTER) else appString(StringKeys.TC_NEW_TOPIC), style = VTypography.body.copy(color = VColors.ink))
            VInput(value = state.addTitle, onValueChange = viewModel::setAddTitle, placeholder = if (isChapter) appString(StringKeys.TC_CHAPTER_TITLE) else appString(StringKeys.TC_TOPIC_TITLE))
            if (state.addError != null) Text(state.addError ?: "", style = VTypography.caption.copy(color = VColors.error))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                VButton(appString(StringKeys.COMMON_BUTTON_CANCEL), onClick = { viewModel.closeAdd() }, modifier = Modifier.weight(1f), variant = VButtonVariant.Ghost, size = VButtonSize.Md)
                VButton(appString(StringKeys.TC_ADD), onClick = { viewModel.submitAdd() }, modifier = Modifier.weight(1f), tone = VButtonTone.Teal, size = VButtonSize.Md, loading = state.isAdding)
            }
        }
    }
}

@Composable
private fun SyllabusRow(
    u: SyllabusUnit,
    isUpdating: Boolean,
    editing: Boolean,
    isDraft: Boolean = false,
    onToggle: () -> Unit,
    onAddTopic: () -> Unit,
    onDelete: () -> Unit,
) {
    val indent = (u.depth.coerceIn(0, 3) * 16).dp
    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = indent)
            .clip(RoundedCornerShape(16.dp))
            .background(if (u.isCovered) VColors.mint.copy(alpha = 0.08f) else VColors.white)
            .border(1.dp, if (u.isCovered) VColors.mint.copy(alpha = 0.35f) else VColors.line, RoundedCornerShape(16.dp))
            .clickable(enabled = !isUpdating) { onToggle() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Coverage check disc.
        Box(
            Modifier.size(28.dp).clip(CircleShape).background(if (u.isCovered) VColors.success else VColors.surfaceTint).border(1.dp, if (u.isCovered) VColors.success else VColors.line, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (isUpdating) TeacherSpinner(16.dp)
            else if (u.isCovered) Icon(VIcons.Check, contentDescription = null, tint = androidx.compose.ui.graphics.Color.White, modifier = Modifier.size(15.dp))
        }
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    u.title,
                    style = if (u.isChapter) VTypography.body else VTypography.bodySmall,
                    color = VColors.ink,
                    fontWeight = if (u.isChapter) FontWeight.Bold else FontWeight.Medium,
                )
                if (isDraft) {
                    VtPill(appString(StringKeys.TC_DRAFT), bg = VColors.violet.copy(alpha = 0.14f), fg = VColors.violetInk)
                }
            }
            if (u.isCovered && !u.coveredOn.isNullOrBlank()) {
                Text(
                    appString(StringKeys.TC_COVERED_DATE, "date" to prettyDateShort(u.coveredOn)),
                    style = VTypography.caption,
                    color = VColors.success,
                )
            }
        }
        // Add topic button (edit mode, chapters only)
        if (editing && u.isChapter) {
            Box(
                Modifier.size(32.dp).clip(CircleShape).background(VColors.surfaceTint)
                    .clickable { onAddTopic() },
                contentAlignment = Alignment.Center,
            ) { Icon(VIcons.Plus, contentDescription = appString(StringKeys.TC_ADD_TOPIC), tint = VColors.ink2, modifier = Modifier.size(16.dp)) }
        }
        // Delete button (edit mode)
        if (editing) {
            Box(
                Modifier.size(32.dp).clip(CircleShape).background(VColors.error.copy(alpha = 0.1f))
                    .clickable { onDelete() },
                contentAlignment = Alignment.Center,
            ) { Icon(VIcons.Minus, contentDescription = appString(StringKeys.COMMON_BUTTON_DELETE), tint = VColors.error, modifier = Modifier.size(16.dp)) }
        }
    }
}

// ── Parse Syllabus bottom sheet ──────────────────────────────────────────────

@Composable
private fun ParseSyllabusSheet(viewModel: TeacherSyllabusViewModel) {
    val state by viewModel.state.collectAsStateV2()

    Box(
        Modifier
            .fillMaxSize()
            .background(VColors.ink.copy(alpha = 0.4f))
            .clickable { viewModel.closeParseSheet() },
    ) {
        VtCard(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .heightIn(min = 200.dp, max = 520.dp)
                .padding(bottom = TeacherDockClearance),
            padding = 20.dp,
        ) {
            // Block tap propagation to scrim.
            Column(
                Modifier.fillMaxWidth().clickable(
                onClick = {},
                ),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(VIcons.Sparkles, contentDescription = null, tint = VColors.violetInk, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(appString(StringKeys.TC_PARSE_SYLLABUS), style = VTypography.h3.copy(color = VColors.ink))
                    Spacer(Modifier.weight(1f))
                    Box(
                        Modifier.size(28.dp).clip(CircleShape).background(VColors.surfaceTint)
                            .clickable { viewModel.closeParseSheet() },
                        contentAlignment = Alignment.Center,
                    ) { Icon(VIcons.Close, contentDescription = appString(StringKeys.COMMON_BUTTON_CLOSE), tint = VColors.ink2, modifier = Modifier.size(16.dp)) }
                }

                if (state.parsedUnits.isEmpty()) {
                    Text(appString(StringKeys.TC_PASTE_SYLLABUS_HINT), style = VTypography.body.copy(color = VColors.ink2))
                    VInput(
                        value = state.parseRawText,
                        onValueChange = viewModel::setParseRawText,
                        placeholder = appString(StringKeys.TC_PASTE_SYLLABUS_PH),
                        singleLine = false,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp, max = 200.dp),
                    )
                    if (state.parseError != null) {
                        Text(state.parseError ?: "", style = VTypography.caption.copy(color = VColors.error))
                    }
                    VButton(
                        appString(StringKeys.TC_PARSE_WITH_AI),
                        onClick = { viewModel.parseSyllabus() },
                        full = true,
                        tone = VButtonTone.Lavender,
                        loading = state.isParsing,
                        leading = { Icon(VIcons.Sparkles, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    )
                } else {
                    Text(appString(StringKeys.TC_PREVIEW_N_UNITS_FOUND, "count" to state.parsedUnits.size.toString()), style = VTypography.body.copy(color = VColors.ink))
                    LazyColumn(
                        Modifier.fillMaxWidth().heightIn(max = 240.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        items(state.parsedUnits) { pu ->
                            val indent = (pu.depth * 16).dp
                            Row(Modifier.fillMaxWidth().padding(start = indent), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(if (pu.depth == 0) "•" else "–", style = VTypography.body.copy(color = VColors.ink3))
                                Text(pu.title, style = VTypography.body.copy(color = VColors.ink))
                            }
                        }
                    }
                    if (state.parseError != null) {
                        Text(state.parseError ?: "", style = VTypography.caption.copy(color = VColors.error))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        VButton(appString(StringKeys.COMMON_BUTTON_CANCEL), onClick = { viewModel.closeParseSheet() }, modifier = Modifier.weight(1f), variant = VButtonVariant.Ghost, size = VButtonSize.Md)
                        VButton(appString(StringKeys.TC_CONFIRM_AND_CREATE), onClick = { viewModel.confirmParsedSyllabus() }, modifier = Modifier.weight(1f), tone = VButtonTone.Lavender, size = VButtonSize.Md, loading = state.isParsing)
                    }
                }
            }
        }
    }
}

// ── Daily Log popup ──────────────────────────────────────────────────────────

@Composable
private fun DailyLogPopup(viewModel: TeacherSyllabusViewModel) {
    val state by viewModel.state.collectAsStateV2()
    val expandedChapters = remember { mutableStateMapOf<String, Boolean>() }
    val expandedTopics = remember { mutableStateMapOf<String, Boolean>() }

    Box(
        Modifier
            .fillMaxSize()
            .background(VColors.ink.copy(alpha = 0.4f))
            .clickable { viewModel.dismissDailyLogPopup() },
    ) {
        VtCard(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .heightIn(min = 200.dp, max = 620.dp)
                .padding(bottom = TeacherDockClearance),
            padding = 20.dp,
        ) {
            // Block tap propagation from sheet content to the scrim dismiss layer.
            Column(
                Modifier.fillMaxWidth().clickable(
                onClick = {},
                ),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(VIcons.ClipboardList, contentDescription = null, tint = VColors.success, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(appString(StringKeys.TC_DAILY_CLASS_LOG), style = VTypography.bodySmall, color = VColors.ink, fontWeight = FontWeight.Bold)
                        Text("${state.dailyLogClassName} · ${state.dailyLogSubject}", style = VTypography.caption, color = VColors.ink2)
                    }
                    Spacer(Modifier.weight(1f))
                    Box(
                        Modifier.size(32.dp).clip(CircleShape).background(VColors.surfaceTint)
                            .clickable { viewModel.closeDailyLogPopup() },
                        contentAlignment = Alignment.Center,
                    ) { Icon(VIcons.Close, contentDescription = appString(StringKeys.COMMON_BUTTON_CLOSE), tint = VColors.ink2, modifier = Modifier.size(17.dp)) }
                }

                val selectedCount = state.dailyLogSelectedTopicIds.size
                Text(
                    if (selectedCount == 0) appString(StringKeys.TC_SELECT_TOPICS_COVERED_TODAY) else appString(StringKeys.TC_N_TOPICS_SELECTED, "count" to selectedCount.toString()),
                    style = VTypography.label,
                    color = VColors.ink2,
                )

                if (state.units.isNotEmpty()) {
                    LazyColumn(
                        Modifier.fillMaxWidth().weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        state.chapters.forEach { chapter ->
                            val chapterTopics = state.units.filter { it.parentId == chapter.id }
                            val isExpanded = expandedChapters[chapter.id] ?: false
                            item(key = "ch-${chapter.id}") {
                                DailyLogChapterRow(
                                    title = chapter.title,
                                    expanded = isExpanded,
                                    onToggle = { expandedChapters[chapter.id] = !isExpanded },
                                )
                            }
                            if (isExpanded) {
                                chapterTopics.forEach { topic ->
                                    val topicSubtopics = state.units.filter { it.parentId == topic.id }
                                    val topicExpanded = expandedTopics[topic.id] ?: false
                                    val topicSelected = topic.id in state.dailyLogSelectedTopicIds
                                    item(key = "tp-${topic.id}") {
                                        DailyLogTopicRow(
                                            title = topic.title,
                                            selected = topicSelected,
                                            hasSubtopics = topicSubtopics.isNotEmpty(),
                                            expanded = topicExpanded,
                                            onToggleSelect = { viewModel.toggleDailyLogTopic(topic.id) },
                                            onToggleExpand = { expandedTopics[topic.id] = !topicExpanded },
                                        )
                                    }
                                    if (topicExpanded && topicSubtopics.isNotEmpty()) {
                                        topicSubtopics.forEach { subtopic ->
                                            val subSelected = subtopic.id in state.dailyLogSelectedTopicIds
                                            item(key = "st-${subtopic.id}") {
                                                DailyLogSubtopicRow(
                                                    title = subtopic.title,
                                                    selected = subSelected,
                                                    onToggle = { viewModel.toggleDailyLogTopic(subtopic.id) },
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Coverage slider
                Text(appString(StringKeys.TC_COVERAGE_N_PCT, "pct" to state.dailyLogCoveragePct.toString()), style = VTypography.label, color = VColors.ink2)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(36.dp).clip(CircleShape).background(VColors.surfaceTint)
                            .clickable { viewModel.setDailyLogCoveragePct(state.dailyLogCoveragePct - 10) },
                        contentAlignment = Alignment.Center,
                    ) { Icon(VIcons.Minus, contentDescription = null, tint = VColors.ink2, modifier = Modifier.size(18.dp)) }
                    Text("${state.dailyLogCoveragePct}%", style = VTypography.bodySmall, color = VColors.ink, fontWeight = FontWeight.Bold)
                    Box(
                        Modifier.size(36.dp).clip(CircleShape).background(VColors.surfaceTint)
                            .clickable { viewModel.setDailyLogCoveragePct(state.dailyLogCoveragePct + 10) },
                        contentAlignment = Alignment.Center,
                    ) { Icon(VIcons.Plus, contentDescription = null, tint = VColors.ink2, modifier = Modifier.size(18.dp)) }
                }

                // Summary text
                VInput(
                    value = state.dailyLogSummary,
                    onValueChange = viewModel::setDailyLogSummary,
                    placeholder = appString(StringKeys.TC_WHAT_TAUGHT_TODAY_OPTIONAL),
                    singleLine = false,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp, max = 120.dp),
                )

                if (state.dailyLogError != null) {
                    Text(state.dailyLogError ?: "", style = VTypography.caption, color = VColors.error)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    VButton(appString(StringKeys.TC_SKIP), onClick = { viewModel.dismissDailyLogPopup() }, modifier = Modifier.weight(1f), variant = VButtonVariant.Ghost, size = VButtonSize.Md)
                    VButton(appString(StringKeys.TC_SAVE_LOG), onClick = { viewModel.saveDailyLog() }, modifier = Modifier.weight(1f), tone = VButtonTone.Teal, size = VButtonSize.Md, loading = state.isSavingDailyLog)
                }
            }
        }
    }
}

@Composable
private fun DailyLogChapterRow(title: String, expanded: Boolean, onToggle: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(VColors.surfaceTint)
            .clickable { onToggle() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            if (expanded) VIcons.ChevronDown else VIcons.ChevronRight,
            contentDescription = null,
            tint = VColors.ink2,
            modifier = Modifier.size(18.dp),
        )
        Text(title, style = VTypography.bodySmall, color = VColors.ink, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun DailyLogTopicRow(
    title: String,
    selected: Boolean,
    hasSubtopics: Boolean,
    expanded: Boolean,
    onToggleSelect: () -> Unit,
    onToggleExpand: () -> Unit,
) {
    Row(
        Modifier.fillMaxWidth().padding(start = 28.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) VColors.mint.copy(alpha = 0.08f) else androidx.compose.ui.graphics.Color.Transparent)
            .clickable { onToggleSelect() }
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            Modifier.size(22.dp).clip(CircleShape)
                .background(if (selected) VColors.success else VColors.surfaceTint)
                .border(1.dp, if (selected) VColors.success else VColors.line, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (selected) Icon(VIcons.Check, contentDescription = null, tint = androidx.compose.ui.graphics.Color.White, modifier = Modifier.size(13.dp))
        }
        Text(
            title,
            style = VTypography.caption,
            color = VColors.ink,
            modifier = Modifier.weight(1f),
        )
        if (hasSubtopics) {
            Box(
                Modifier.size(28.dp).clip(CircleShape)
                    .clickable { onToggleExpand() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    if (expanded) VIcons.ChevronDown else VIcons.ChevronRight,
                    contentDescription = null,
                    tint = VColors.ink3,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

@Composable
private fun DailyLogSubtopicRow(title: String, selected: Boolean, onToggle: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(start = 56.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) VColors.mint.copy(alpha = 0.06f) else androidx.compose.ui.graphics.Color.Transparent)
            .clickable { onToggle() }
            .padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            Modifier.size(18.dp).clip(CircleShape)
                .background(if (selected) VColors.success else VColors.surfaceTint)
                .border(1.dp, if (selected) VColors.success else VColors.line, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (selected) Icon(VIcons.Check, contentDescription = null, tint = androidx.compose.ui.graphics.Color.White, modifier = Modifier.size(11.dp))
        }
        Text(title, style = VTypography.caption, color = VColors.ink2)
    }
}

// ── Quiz generation sheet ────────────────────────────────────────────────────

@Composable
private fun QuizSheet(viewModel: TeacherSyllabusViewModel) {
    val state by viewModel.state.collectAsStateV2()

    Box(
        Modifier
            .fillMaxSize()
            .background(VColors.ink.copy(alpha = 0.4f))
            .clickable { viewModel.closeQuizSheet() },
    ) {
        VtCard(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .heightIn(min = 200.dp, max = 620.dp)
                .padding(bottom = TeacherDockClearance),
            padding = 20.dp,
        ) {
            // Block tap propagation to scrim.
            Column(
                Modifier.fillMaxWidth().clickable(
                onClick = {},
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(VIcons.Sparkles, contentDescription = null, tint = VColors.violetInk, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(appString(StringKeys.TC_GENERATE_QUIZ), style = VTypography.bodySmall, color = VColors.ink, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.weight(1f))
                    Box(
                        Modifier.size(32.dp).clip(CircleShape).background(VColors.surfaceTint)
                            .clickable { viewModel.closeQuizSheet() },
                        contentAlignment = Alignment.Center,
                    ) { Icon(VIcons.Close, contentDescription = appString(StringKeys.COMMON_BUTTON_CLOSE), tint = VColors.ink2, modifier = Modifier.size(17.dp)) }
                }

                // ── Unit selection (multiple) ──────────────────────────────
                Text(appString(StringKeys.TC_SELECT_UNITS), style = VTypography.label, color = VColors.ink2)
                val allUnits = state.units
                LazyColumn(
                    Modifier.fillMaxWidth().heightIn(max = 160.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    items(allUnits, key = { it.id }) { u ->
                        val isSelected = u.id in state.quizSelectedUnitIds
                        Row(
                            Modifier.fillMaxWidth()
                                .clip(VShapes.md)
                                .background(if (isSelected) VColors.violet.copy(alpha = 0.08f) else Color.Transparent)
                                .clickable { viewModel.toggleQuizUnit(u.id) }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Box(
                                Modifier.size(20.dp).clip(VShapes.sm)
                                    .background(if (isSelected) VColors.violetInk else VColors.surfaceTint)
                                    .border(1.dp, if (isSelected) VColors.violetInk else VColors.line, VShapes.sm),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (isSelected) Icon(VIcons.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                            Text(
                                u.title,
                                style = VTypography.bodySmall,
                                color = if (isSelected) VColors.ink else VColors.ink2,
                                maxLines = 1,
                            )
                        }
                    }
                }

                // ── Question types ─────────────────────────────────────────
                Text(appString(StringKeys.TC_QUESTION_TYPES), style = VTypography.label, color = VColors.ink2)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf("MCQ" to appString(StringKeys.TC_MCQ), "FILL_BLANK" to appString(StringKeys.TC_FILL_UPS), "TRUE_FALSE" to appString(StringKeys.TC_TRUE_FALSE), "MATCH" to appString(StringKeys.TC_MATCH)).forEach { (type, label) ->
                        val selected = type in state.quizQuestionTypes
                        Box(
                            Modifier
                                .weight(1f)
                                .clip(VShapes.md)
                                .background(if (selected) VColors.violet.copy(alpha = 0.14f) else VColors.surfaceTint)
                                .border(1.dp, if (selected) VColors.violetInk else VColors.line, VShapes.md)
                                .clickable { viewModel.toggleQuizQuestionType(type) }
                                .heightIn(min = 42.dp)
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                label,
                                style = VTypography.caption,
                                color = if (selected) VColors.violetInk else VColors.ink2,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            )
                        }
                    }
                }

                // ── Number of questions ────────────────────────────────────
                Text(appString(StringKeys.TC_NUMBER_OF_QUESTIONS_N, "count" to state.quizNumQuestions.toString()), style = VTypography.label, color = VColors.ink2)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(36.dp).clip(CircleShape).background(VColors.surfaceTint)
                            .clickable { viewModel.setQuizNumQuestions(state.quizNumQuestions - 1) },
                        contentAlignment = Alignment.Center,
                    ) { Icon(VIcons.Minus, contentDescription = null, tint = VColors.ink2, modifier = Modifier.size(18.dp)) }
                    Text("${state.quizNumQuestions}", style = VTypography.bodySmall, color = VColors.ink, fontWeight = FontWeight.Bold)
                    Box(
                        Modifier.size(36.dp).clip(CircleShape).background(VColors.surfaceTint)
                            .clickable { viewModel.setQuizNumQuestions(state.quizNumQuestions + 1) },
                        contentAlignment = Alignment.Center,
                    ) { Icon(VIcons.Plus, contentDescription = null, tint = VColors.ink2, modifier = Modifier.size(18.dp)) }
                }

                // ── Difficulty ─────────────────────────────────────────────
                Text(appString(StringKeys.TC_DIFFICULTY), style = VTypography.label, color = VColors.ink2)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf("EASY" to appString(StringKeys.TC_EASY), "MEDIUM" to appString(StringKeys.TC_MEDIUM), "HARD" to appString(StringKeys.TC_HARD)).forEach { (diff, label) ->
                        val selected = state.quizDifficulty == diff
                        Box(
                            Modifier
                                .weight(1f)
                                .clip(VShapes.md)
                                .background(if (selected) VColors.violet.copy(alpha = 0.14f) else VColors.surfaceTint)
                                .border(1.dp, if (selected) VColors.violetInk else VColors.line, VShapes.md)
                                .clickable { viewModel.setQuizDifficulty(diff) }
                                .heightIn(min = 42.dp)
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                label,
                                style = VTypography.bodySmall,
                                color = if (selected) VColors.violetInk else VColors.ink2,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            )
                        }
                    }
                }

                if (state.quizError != null) {
                    Text(state.quizError ?: "", style = VTypography.caption, color = VColors.error)
                }

                VButton(
                    appString(StringKeys.TC_GENERATE_QUIZ),
                    onClick = { viewModel.generateQuiz() },
                    full = true,
                    tone = VButtonTone.Lavender,
                    loading = state.isGeneratingQuiz,
                    leading = { Icon(VIcons.Sparkles, contentDescription = null, modifier = Modifier.size(16.dp)) },
                )
            }
        }
    }
}

// ── Quiz row (in the quiz list section) ──────────────────────────────────────

@Composable
private fun QuizRow(q: QuizDto, onPublish: () -> Unit, onLeaderboard: () -> Unit = {}) {
    VtCard(padding = 14.dp) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            VtIconDisc(VIcons.GraduationCap, tint = VColors.violetInk, bg = VColors.violet.copy(alpha = 0.14f), size = 36.dp, glyph = 18.dp)
            Column(Modifier.weight(1f)) {
                Text(q.title.ifBlank { appString(StringKeys.TC_QUIZ) }, style = VTypography.body.copy(color = VColors.ink))
                Text(appString(StringKeys.TC_N_QUESTIONS_STATUS, "count" to q.questions.size.toString(), "status" to q.status), style = VTypography.caption.copy(color = VColors.ink2))
            }
            if (q.status == "DRAFT") {
                VButton(appString(StringKeys.TC_PUBLISH), onClick = onPublish, size = VButtonSize.Sm, tone = VButtonTone.Lavender, variant = VButtonVariant.Secondary)
            } else {
                VtPill(appString(StringKeys.TC_PUBLISHED), bg = VColors.mint.copy(alpha = 0.14f), fg = VColors.success)
                VButton(appString(StringKeys.TC_RESULTS), onClick = onLeaderboard, size = VButtonSize.Sm, tone = VButtonTone.Sky, variant = VButtonVariant.Secondary)
            }
        }
    }
}

// ── Empty state with 3 clear options ─────────────────────────────────────────

@Composable
private fun EmptyStateOptions(viewModel: TeacherSyllabusViewModel) {
    val state by viewModel.state.collectAsStateV2()
    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
        Text(appString(StringKeys.TC_NO_UNITS_YET), style = VTypography.h3.copy(color = VColors.ink))
        Text(appString(StringKeys.TC_CHOOSE_HOW_TO_BUILD_SYLLABUS), style = VTypography.caption.copy(color = VColors.ink3))
        Spacer(Modifier.height(4.dp))
        // Option 1: Auto-fill from NCERT
        VtCard(padding = 16.dp, onClick = { viewModel.autoFill() }) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                VtIconDisc(VIcons.Sparkles, tint = VColors.violetInk, bg = VColors.violet.copy(alpha = 0.14f), size = 40.dp, glyph = 20.dp)
                Column(Modifier.weight(1f)) {
                    Text(appString(StringKeys.TC_AUTO_FILL_FROM_NCERT), style = VTypography.body.copy(color = VColors.ink))
                    Text(appString(StringKeys.TC_FETCH_STANDARD_NCERT_SYLLABUS), style = VTypography.caption.copy(color = VColors.ink3))
                }
            }
        }
        // Option 2: Paste text for AI parse
        VtCard(padding = 16.dp, onClick = { viewModel.openParseSheet() }) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                VtIconDisc(VIcons.ClipboardList, tint = VColors.success, bg = VColors.mint.copy(alpha = 0.14f), size = 40.dp, glyph = 20.dp)
                Column(Modifier.weight(1f)) {
                    Text(appString(StringKeys.TC_PASTE_SYLLABUS_TEXT), style = VTypography.body.copy(color = VColors.ink))
                    Text(appString(StringKeys.TC_AI_EXTRACT_CHAPTERS_TOPICS), style = VTypography.caption.copy(color = VColors.ink3))
                }
            }
        }
        // Option 3: Add manually
        VtCard(padding = 16.dp, onClick = { viewModel.toggleEditing(); viewModel.openAdd(null) }) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                VtIconDisc(VIcons.Plus, tint = VColors.ink2, bg = VColors.surfaceTint, size = 40.dp, glyph = 20.dp)
                Column(Modifier.weight(1f)) {
                    Text(appString(StringKeys.TC_ADD_MANUALLY), style = VTypography.body.copy(color = VColors.ink))
                    Text(appString(StringKeys.TC_CREATE_CHAPTERS_TOPICS_ONE_BY_ONE), style = VTypography.caption.copy(color = VColors.ink3))
                }
            }
        }
        if (state.isAutoFilling) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                TeacherSpinner(16.dp)
                Text(appString(StringKeys.TC_FETCHING_NCERT_REFERENCE), style = VTypography.caption.copy(color = VColors.ink2))
            }
        }
    }
}

// ── Pace warning banner ──────────────────────────────────────────────────────

@Composable
private fun PaceWarningBanner(warning: com.littlebridge.enrollplus.feature.teacher.domain.model.SylPaceWarning) {
    val isBehind = warning.level == "BEHIND" || warning.level == "CRITICAL"
    val bg = if (isBehind) VColors.error.copy(alpha = 0.08f) else VColors.violet.copy(alpha = 0.08f)
    val fg = if (isBehind) VColors.error else VColors.violetInk
    val icon = if (isBehind) VIcons.AlertCircle else VIcons.Sparkles
    val label = when (warning.level) {
        "CRITICAL" -> appString(StringKeys.TC_CRITICALLY_BEHIND)
        "BEHIND" -> appString(StringKeys.TC_BEHIND_SCHEDULE)
        "AHEAD" -> appString(StringKeys.TC_AHEAD_OF_SCHEDULE)
        else -> appString(StringKeys.TC_PACE_UPDATE)
    }
    VtCard(padding = 14.dp) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(Modifier.size(32.dp).clip(CircleShape).background(bg), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = fg, modifier = Modifier.size(16.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(label, style = VTypography.body.copy(color = fg))
                Text(
                    appString(StringKeys.TC_PACE_EXPECTED_ACTUAL, "expected" to warning.expectedPct.toString(), "actual" to warning.actualPct.toString(), "delta" to warning.deviationPct.toString()),
                    style = VTypography.caption.copy(color = VColors.ink2),
                )
                if (warning.message.isNotBlank()) {
                    Text(warning.message, style = VTypography.caption.copy(color = VColors.ink3))
                }
                Spacer(Modifier.height(4.dp))
                val metricsText = buildString {
                    if (warning.classesElapsed > 0) {
                        append(appString(StringKeys.TC_N_CLASSES_DONE, "count" to warning.classesElapsed.toString()))
                        if (warning.classesRemaining > 0) append(" · " + appString(StringKeys.TC_N_LEFT, "count" to warning.classesRemaining.toString()))
                    }
                    if (warning.weeklyPeriods > 0) {
                        append(" · " + appString(StringKeys.TC_N_PER_WEEK, "count" to warning.weeklyPeriods.toString()))
                    }
                    if (warning.holidayDaysCounted > 0) {
                        append(" · " + appString(StringKeys.TC_N_HOLIDAYS, "count" to warning.holidayDaysCounted.toString()))
                    }
                }
                if (metricsText.isNotBlank()) {
                    Text(metricsText, style = VTypography.caption.copy(color = VColors.ink3))
                }
                if (warning.estimatedCompletionDate.isNotBlank()) {
                    Text(appString(StringKeys.TC_EST_COMPLETION_DATE, "date" to warning.estimatedCompletionDate), style = VTypography.caption.copy(color = VColors.ink3))
                }
                if (warning.avgCoveragePerClass > 0) {
                    Text(appString(StringKeys.TC_AVG_N_PCT_PER_CLASS, "pct" to formatDecimal(warning.avgCoveragePerClass, 1)), style = VTypography.caption.copy(color = VColors.ink3))
                }
            }
        }
    }
}

// ── Draft approval bar ───────────────────────────────────────────────────────

@Composable
private fun DraftApprovalBar(viewModel: TeacherSyllabusViewModel) {
    val state by viewModel.state.collectAsStateV2()
    val draftCount = state.draftUnits.size
    VtCard(padding = 14.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.size(28.dp).clip(CircleShape).background(VColors.violet.copy(alpha = 0.14f)), contentAlignment = Alignment.Center) {
                    Icon(VIcons.ShieldCheck, contentDescription = null, tint = VColors.violetInk, modifier = Modifier.size(14.dp))
                }
                Text(appString(StringKeys.TC_N_DRAFT_UNITS_PENDING_APPROVAL, "count" to draftCount.toString()), style = VTypography.body.copy(color = VColors.violetInk))
            }
            Text(appString(StringKeys.TC_DRAFT_UNITS_NOT_VISIBLE_TO_PARENTS), style = VTypography.caption.copy(color = VColors.ink3))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                VButton(appString(StringKeys.TC_REJECT_ALL), onClick = { viewModel.rejectAllDrafts() }, modifier = Modifier.weight(1f), variant = VButtonVariant.Ghost, size = VButtonSize.Sm, loading = state.isApproving)
                VButton(appString(StringKeys.TC_APPROVE_ALL), onClick = { viewModel.approveAllDrafts() }, modifier = Modifier.weight(1f), tone = VButtonTone.Lavender, size = VButtonSize.Sm, loading = state.isApproving)
            }
            if (state.approveError != null) {
                Text(state.approveError ?: "", style = VTypography.caption.copy(color = VColors.error))
            }
        }
    }
}

// ── Auto-fill preview sheet ──────────────────────────────────────────────────

@Composable
private fun AutoFillPreviewSheet(viewModel: TeacherSyllabusViewModel) {
    val state by viewModel.state.collectAsStateV2()

    Box(
        Modifier
            .fillMaxSize()
            .background(VColors.ink.copy(alpha = 0.4f))
            .clickable { viewModel.dismissAutoFillPreview() },
    ) {
        VtCard(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .heightIn(min = 200.dp, max = 600.dp)
                .padding(bottom = TeacherDockClearance),
            padding = 20.dp,
        ) {
            // Block tap propagation to scrim.
            Column(
                Modifier.fillMaxWidth().clickable(
                onClick = {},
                ),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(VIcons.Sparkles, contentDescription = null, tint = VColors.violetInk, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(appString(StringKeys.TC_NCERT_AUTO_FILL), style = VTypography.h3.copy(color = VColors.ink))
                        if (state.autoFillSource.isNotBlank()) {
                            Text(state.autoFillSource, style = VTypography.caption.copy(color = VColors.ink2))
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    Box(
                        Modifier.size(28.dp).clip(CircleShape).background(VColors.surfaceTint)
                            .clickable { viewModel.dismissAutoFillPreview() },
                        contentAlignment = Alignment.Center,
                    ) { Icon(VIcons.Close, contentDescription = appString(StringKeys.COMMON_BUTTON_CLOSE), tint = VColors.ink2, modifier = Modifier.size(16.dp)) }
                }

                val totalChapters = state.autoFillChapters.size
                val totalTopics = state.autoFillChapters.sumOf { ch -> ch.topics.size }
                val totalSubtopics = state.autoFillChapters.sumOf { ch -> ch.topics.sumOf { t -> t.subtopics.size } }
                val totalUnits = totalChapters + totalTopics + totalSubtopics
                val subtopicText = if (totalSubtopics > 0) ", " + appString(StringKeys.TC_N_SUBTOPICS, "count" to totalSubtopics.toString()) else ""
                Text(
                    appString(StringKeys.TC_AUTO_FILL_PREVIEW, "chapters" to totalChapters.toString(), "topics" to totalTopics.toString(), "subtopics" to subtopicText, "units" to totalUnits.toString()),
                    style = VTypography.body.copy(color = VColors.ink2),
                )

                LazyColumn(
                    Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    items(state.autoFillChapters) { ch ->
                        AutoFillChapterRow(ch)
                    }
                }

                if (state.autoFillError != null) {
                    Text(state.autoFillError ?: "", style = VTypography.caption.copy(color = VColors.error))
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    VButton(appString(StringKeys.COMMON_BUTTON_CANCEL), onClick = { viewModel.dismissAutoFillPreview() }, modifier = Modifier.weight(1f), variant = VButtonVariant.Ghost, size = VButtonSize.Md)
                    VButton(appString(StringKeys.TC_CREATE_AS_DRAFT), onClick = { viewModel.confirmAutoFill() }, modifier = Modifier.weight(1f), tone = VButtonTone.Lavender, size = VButtonSize.Md, loading = state.isAutoFilling)
                }
            }
        }
    }
}

@Composable
private fun AutoFillChapterRow(ch: SylAutoFillChapter) {
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(VColors.surfaceTint).padding(12.dp)) {
        Text(ch.title, style = VTypography.body.copy(color = VColors.ink))
        if (ch.topics.isNotEmpty()) {
            Spacer(Modifier.height(6.dp))
            ch.topics.forEach { t ->
                Row(Modifier.fillMaxWidth().padding(start = 12.dp, top = 2.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("–", style = VTypography.body.copy(color = VColors.ink3))
                    Text(t.title, style = VTypography.body.copy(color = VColors.ink2))
                }
                if (t.subtopics.isNotEmpty()) {
                    t.subtopics.forEach { st ->
                        Row(Modifier.fillMaxWidth().padding(start = 28.dp, top = 1.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("·", style = VTypography.body.copy(color = VColors.ink3))
                            Text(st.title, style = VTypography.body.copy(color = VColors.ink3))
                        }
                    }
                }
            }
        }
    }
}

// ── Quiz preview sheet (after AI generation, before publishing) ──────────────

@Composable
private fun QuizPreviewSheet(viewModel: TeacherSyllabusViewModel) {
    val state by viewModel.state.collectAsStateV2()
    val quiz = state.generatedQuiz ?: return

    Box(
        Modifier
            .fillMaxSize()
            .background(VColors.ink.copy(alpha = 0.4f))
            .clickable { viewModel.closeQuizPreview() },
    ) {
        VtCard(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .heightIn(min = 200.dp, max = 700.dp)
                .padding(bottom = TeacherDockClearance),
            padding = 20.dp,
        ) {
            // Block tap propagation to scrim; keep content scrollable.
            Column(
                Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).clickable(
                onClick = {},
                ),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                // Header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(VIcons.GraduationCap, contentDescription = null, tint = VColors.violetInk, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(appString(StringKeys.TC_QUIZ_PREVIEW), style = VTypography.h3.copy(color = VColors.ink))
                    Spacer(Modifier.weight(1f))
                    Box(
                        Modifier.size(28.dp).clip(CircleShape).background(VColors.surfaceTint)
                            .clickable { viewModel.closeQuizPreview() },
                        contentAlignment = Alignment.Center,
                    ) { Icon(VIcons.Close, contentDescription = appString(StringKeys.COMMON_BUTTON_CLOSE), tint = VColors.ink2, modifier = Modifier.size(16.dp)) }
                }

                Text(quiz.title, style = VTypography.body.copy(color = VColors.ink2))
                Text(appString(StringKeys.TC_N_QUESTIONS_STATUS, "count" to quiz.questions.size.toString(), "status" to quiz.status), style = VTypography.caption.copy(color = VColors.ink3))

                // Questions list
                quiz.questions.forEachIndexed { idx, q ->
                    val isEditing = state.editingQuestionId == q.id
                    if (isEditing) {
                        EditableQuestionCard(
                            question = q,
                            isLoading = state.isPublishingQuiz,
                            onSave = { question, options, correctAnswer, explanation, qType ->
                                viewModel.updateGeneratedQuestion(quiz.id, q.id, question, options, correctAnswer, explanation, qType)
                            },
                            onCancel = { viewModel.cancelEditingQuestion() },
                        )
                    } else {
                        QuestionPreviewCard(
                            index = idx + 1,
                            question = q,
                            onEdit = { viewModel.startEditingQuestion(q.id) },
                        )
                    }
                }

                // Add Question button + form
                if (state.showAddQuestion) {
                    AddQuestionCard(
                        isLoading = state.isPublishingQuiz,
                        onSave = { question, options, correctAnswer, explanation, qType ->
                            viewModel.addQuestion(quiz.id, question, options, correctAnswer, explanation, qType)
                        },
                        onCancel = { viewModel.cancelAddQuestion() },
                    )
                } else {
                    VButton(
                        appString(StringKeys.TC_ADD_QUESTION),
                        onClick = { viewModel.openAddQuestion() },
                        variant = VButtonVariant.Ghost,
                        tone = VButtonTone.Teal,
                        size = VButtonSize.Md,
                        full = true,
                        leading = { Icon(VIcons.Plus, contentDescription = null, modifier = Modifier.size(14.dp)) },
                    )
                }

                if (state.quizPreviewError != null) {
                    Text(state.quizPreviewError ?: "", style = VTypography.caption.copy(color = VColors.error))
                }

                // Action buttons
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    VButton(
                        appString(StringKeys.TC_REGENERATE_ALL),
                        onClick = { viewModel.regenerateQuizQuestions() },
                        modifier = Modifier.weight(1f),
                        variant = VButtonVariant.Secondary,
                        tone = VButtonTone.Teal,
                        size = VButtonSize.Md,
                        loading = state.isRegenerating,
                        leading = { Icon(VIcons.Sparkles, contentDescription = null, modifier = Modifier.size(14.dp)) },
                    )
                    VButton(
                        appString(StringKeys.TC_PUBLISH_QUIZ),
                        onClick = { viewModel.publishGeneratedQuiz() },
                        modifier = Modifier.weight(1f),
                        tone = VButtonTone.Lavender,
                        size = VButtonSize.Md,
                        loading = state.isPublishingQuiz,
                    )
                }
            }
        }
    }
}

@Composable
private fun QuestionPreviewCard(
    index: Int,
    question: QuizQuestionDto,
    onEdit: () -> Unit,
) {

    Column(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(VColors.surfaceTint)
            .border(1.dp, VColors.line, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Q$index", style = VTypography.body.copy(color = VColors.violetInk))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(question.question, style = VTypography.body.copy(color = VColors.ink))
                if (question.options.isNotEmpty()) {
                    question.options.forEachIndexed { i, opt ->
                        val isCorrect = opt.startsWith(question.correctAnswer, ignoreCase = true) ||
                            question.correctIndex == i
                        Text(
                            opt,
                            style = VTypography.body.copy(color = if (isCorrect) VColors.success else VColors.ink2).copy(
                                fontSize = 12.sp,
                                fontWeight = if (isCorrect) FontWeight.Bold else FontWeight.Normal,
                            ),
                        )
                    }
                } else if (question.correctAnswer.isNotBlank()) {
                    Text(appString(StringKeys.TC_ANSWER_COLON, "answer" to question.correctAnswer), style = VTypography.body.copy(color = VColors.success))
                }
                if (!question.explanation.isNullOrBlank()) {
                    Text(appString(StringKeys.TC_EXPLANATION_COLON, "explanation" to question.explanation), style = VTypography.caption.copy(color = VColors.ink3))
                }
                Text(question.questionType, style = VTypography.caption.copy(color = VColors.ink3))
            }
            Box(
                Modifier.size(26.dp).clip(CircleShape).background(VColors.violet.copy(alpha = 0.1f))
                    .clickable { onEdit() },
                contentAlignment = Alignment.Center,
            ) { Icon(VIcons.Edit3, contentDescription = appString(StringKeys.TC_EDIT), tint = VColors.violetInk, modifier = Modifier.size(13.dp)) }
        }
    }
}

// ── Add question card (manual question creation) ─────────────────────────────

@Composable
private fun AddQuestionCard(
    isLoading: Boolean,
    onSave: (question: String, options: List<String>, correctAnswer: String, explanation: String?, questionType: String) -> Unit,
    onCancel: () -> Unit,
) {
    var questionText by remember { mutableStateOf("") }
    var optionsText by remember { mutableStateOf("") }
    var correctAnswer by remember { mutableStateOf("") }
    var explanation by remember { mutableStateOf("") }
    var questionType by remember { mutableStateOf("MCQ") }

    Column(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(VColors.violet.copy(alpha = 0.06f))
            .border(1.dp, VColors.violetInk.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(appString(StringKeys.TC_ADD_NEW_QUESTION), style = VTypography.body.copy(color = VColors.violetInk))

        // Question type selector
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("MCQ" to appString(StringKeys.TC_MCQ), "FILL_BLANK" to appString(StringKeys.TC_FILL_UPS), "TRUE_FALSE" to appString(StringKeys.TC_TRUE_FALSE)).forEach { (type, label) ->
                val selected = questionType == type
                Box(
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selected) VColors.violet.copy(alpha = 0.14f) else VColors.surfaceTint)
                        .border(1.dp, if (selected) VColors.violetInk else VColors.line, RoundedCornerShape(8.dp))
                        .clickable {
                            questionType = type
                            if (type == "TRUE_FALSE") {
                                correctAnswer = "true"
                                optionsText = ""
                            } else if (type == "FILL_BLANK") {
                                optionsText = ""
                            }
                        }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(label, style = VTypography.caption.copy(color = if (selected) VColors.violetInk else VColors.ink2).copy(fontSize = 11.sp))
                }
            }
        }

        VInput(
            value = questionText,
            onValueChange = { questionText = it },
            placeholder = appString(StringKeys.TC_QUESTION_TEXT),
            singleLine = false,
            modifier = Modifier.fillMaxWidth().heightIn(min = 60.dp, max = 120.dp),
        )

        if (questionType == "MCQ") {
            Text(appString(StringKeys.TC_OPTIONS_ONE_PER_LINE), style = VTypography.caption.copy(color = VColors.ink2))
            VInput(
                value = optionsText,
                onValueChange = { optionsText = it },
                placeholder = appString(StringKeys.TC_OPTIONS_PH),
                singleLine = false,
                modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp, max = 140.dp),
            )
        }

        if (questionType == "TRUE_FALSE") {
            Text(appString(StringKeys.TC_CORRECT_ANSWER), style = VTypography.caption.copy(color = VColors.ink2))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("true" to appString(StringKeys.TC_TRUE), "false" to appString(StringKeys.TC_FALSE)).forEach { (value, label) ->
                    val selected = correctAnswer.equals(value, ignoreCase = true)
                    Box(
                        Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selected) VColors.violet.copy(alpha = 0.14f) else VColors.surfaceTint)
                            .border(1.dp, if (selected) VColors.violetInk else VColors.line, RoundedCornerShape(8.dp))
                            .clickable { correctAnswer = value }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(label, style = VTypography.body.copy(color = if (selected) VColors.violetInk else VColors.ink2).copy(fontSize = 13.sp))
                    }
                }
            }
        } else {
            VInput(
                value = correctAnswer,
                onValueChange = { correctAnswer = it },
                placeholder = if (questionType == "FILL_BLANK") appString(StringKeys.TC_CORRECT_ANSWER_TEXT) else appString(StringKeys.TC_CORRECT_ANSWER_EG_AB),
            )
        }

        VInput(
            value = explanation,
            onValueChange = { explanation = it },
            placeholder = appString(StringKeys.TC_EXPLANATION_OPTIONAL),
            singleLine = false,
            modifier = Modifier.fillMaxWidth().heightIn(min = 40.dp, max = 80.dp),
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            VButton(appString(StringKeys.COMMON_BUTTON_CANCEL), onClick = onCancel, modifier = Modifier.weight(1f), variant = VButtonVariant.Ghost, size = VButtonSize.Md)
            VButton(
                appString(StringKeys.TC_ADD),
                onClick = {
                    val opts = if (questionType == "MCQ") optionsText.split("\n").map { it.trim() }.filter { it.isNotEmpty() } else emptyList()
                    onSave(questionText, opts, correctAnswer, explanation.ifBlank { null }, questionType)
                },
                modifier = Modifier.weight(1f),
                tone = VButtonTone.Lavender,
                size = VButtonSize.Md,
                loading = isLoading,
            )
        }
    }
}

@Composable
private fun EditableQuestionCard(
    question: QuizQuestionDto,
    isLoading: Boolean,
    onSave: (question: String, options: List<String>, correctAnswer: String, explanation: String?, questionType: String) -> Unit,
    onCancel: () -> Unit,
) {
    var questionText by remember { mutableStateOf(question.question) }
    var optionsText by remember { mutableStateOf(question.options.joinToString("\n")) }
    var correctAnswer by remember { mutableStateOf(question.correctAnswer) }
    var explanation by remember { mutableStateOf(question.explanation ?: "") }
    var questionType by remember { mutableStateOf(question.questionType) }

    Column(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(VColors.violet.copy(alpha = 0.06f))
            .border(1.dp, VColors.violetInk.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(appString(StringKeys.TC_EDITING_QUESTION), style = VTypography.body.copy(color = VColors.violetInk))

        VInput(
            value = questionText,
            onValueChange = { questionText = it },
            placeholder = appString(StringKeys.TC_QUESTION_TEXT),
            singleLine = false,
            modifier = Modifier.fillMaxWidth().heightIn(min = 60.dp, max = 120.dp),
        )

        if (questionType == "MCQ") {
            Text(appString(StringKeys.TC_OPTIONS_ONE_PER_LINE), style = VTypography.caption.copy(color = VColors.ink2))
            VInput(
                value = optionsText,
                onValueChange = { optionsText = it },
                placeholder = appString(StringKeys.TC_OPTIONS_PH),
                singleLine = false,
                modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp, max = 140.dp),
            )
        }

        if (questionType == "TRUE_FALSE") {
            Text(appString(StringKeys.TC_CORRECT_ANSWER), style = VTypography.caption.copy(color = VColors.ink2))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("true" to appString(StringKeys.TC_TRUE), "false" to appString(StringKeys.TC_FALSE)).forEach { (value, label) ->
                    val selected = correctAnswer.equals(value, ignoreCase = true)
                    Box(
                        Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selected) VColors.violet.copy(alpha = 0.14f) else VColors.surfaceTint)
                            .border(1.dp, if (selected) VColors.violetInk else VColors.line, RoundedCornerShape(8.dp))
                            .clickable { correctAnswer = value }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(label, style = VTypography.body.copy(color = if (selected) VColors.violetInk else VColors.ink2).copy(fontSize = 13.sp))
                    }
                }
            }
        } else {
            VInput(
                value = correctAnswer,
                onValueChange = { correctAnswer = it },
                placeholder = if (questionType == "FILL_BLANK") appString(StringKeys.TC_CORRECT_ANSWER_TEXT) else appString(StringKeys.TC_CORRECT_ANSWER_EG_AB),
            )
        }

        VInput(
            value = explanation,
            onValueChange = { explanation = it },
            placeholder = appString(StringKeys.TC_EXPLANATION_OPTIONAL),
            singleLine = false,
            modifier = Modifier.fillMaxWidth().heightIn(min = 40.dp, max = 80.dp),
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("MCQ", "FILL_BLANK", "TRUE_FALSE").forEach { type ->
                val selected = questionType == type
                Box(
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selected) VColors.violet.copy(alpha = 0.14f) else VColors.surfaceTint)
                        .border(1.dp, if (selected) VColors.violetInk else VColors.line, RoundedCornerShape(8.dp))
                        .clickable { questionType = type }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        type.replace("_", " "),
                        style = VTypography.body.copy(color = if (selected) VColors.violetInk else VColors.ink2).copy(fontSize = 10.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal),
                    )
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            VButton(appString(StringKeys.COMMON_BUTTON_CANCEL), onClick = onCancel, modifier = Modifier.weight(1f), variant = VButtonVariant.Ghost, size = VButtonSize.Md)
            VButton(
                appString(StringKeys.COMMON_BUTTON_SAVE),
                onClick = {
                    val opts = if (questionType == "MCQ") optionsText.split("\n").map { it.trim() }.filter { it.isNotEmpty() } else emptyList()
                    onSave(questionText, opts, correctAnswer, explanation.ifBlank { null }, questionType)
                },
                modifier = Modifier.weight(1f),
                tone = VButtonTone.Lavender,
                size = VButtonSize.Md,
                loading = isLoading,
            )
        }
    }
}

// ── Quiz leaderboard sheet ───────────────────────────────────────────────────

@Composable
private fun QuizLeaderboardSheet(viewModel: TeacherSyllabusViewModel) {
    val state by viewModel.state.collectAsStateV2()

    Box(
        Modifier
            .fillMaxSize()
            .background(VColors.ink.copy(alpha = 0.4f))
            .clickable { viewModel.closeLeaderboard() },
    ) {
        VtCard(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .heightIn(min = 200.dp, max = 650.dp)
                .padding(bottom = TeacherDockClearance),
            padding = 20.dp,
        ) {
            // Block tap propagation to scrim; keep content scrollable.
            Column(
                Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).clickable(
                onClick = {},
                ),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                // Header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(VIcons.GraduationCap, contentDescription = null, tint = VColors.violetInk, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(appString(StringKeys.TC_QUIZ_LEADERBOARD), style = VTypography.h3.copy(color = VColors.ink))
                    Spacer(Modifier.weight(1f))
                    Box(
                        Modifier.size(28.dp).clip(CircleShape).background(VColors.surfaceTint)
                            .clickable { viewModel.closeLeaderboard() },
                        contentAlignment = Alignment.Center,
                    ) { Icon(VIcons.Close, contentDescription = appString(StringKeys.COMMON_BUTTON_CLOSE), tint = VColors.ink2, modifier = Modifier.size(16.dp)) }
                }

                val lb = state.leaderboard
                if (state.leaderboardLoading) {
                    Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text(appString(StringKeys.TC_LOADING_LEADERBOARD), style = VTypography.body.copy(color = VColors.ink2))
                    }
                } else if (state.leaderboardError != null) {
                    Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text(state.leaderboardError ?: "", style = VTypography.body.copy(color = VColors.error))
                    }
                } else if (lb != null) {
                    // Quiz info
                    Text(lb.quizTitle.ifBlank { appString(StringKeys.TC_QUIZ) }, style = VTypography.body.copy(color = VColors.ink))
                    if (lb.subject.isNotBlank()) {
                        Text(lb.subject, style = VTypography.caption.copy(color = VColors.ink2))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(appString(StringKeys.TC_N_ATTEMPTED, "count" to lb.totalParticipants.toString()), style = VTypography.caption.copy(color = VColors.ink2))
                        Text(appString(StringKeys.TC_N_ENROLLED, "count" to lb.totalStudents.toString()), style = VTypography.caption.copy(color = VColors.ink2))
                    }

                    if (lb.entries.isEmpty()) {
                        Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                            Text(appString(StringKeys.TC_NO_ATTEMPTS_YET), style = VTypography.body.copy(color = VColors.ink2))
                        }
                    } else {
                        // Column headers
                        Row(
                            Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("#", style = VTypography.caption.copy(color = VColors.ink3), modifier = Modifier.width(28.dp))
                            Text(appString(StringKeys.TC_STUDENT), style = VTypography.caption.copy(color = VColors.ink3), modifier = Modifier.weight(1f))
                            Text(appString(StringKeys.TC_SCORE), style = VTypography.caption.copy(color = VColors.ink3))
                            Text("%", style = VTypography.caption.copy(color = VColors.ink3))
                        }

                        lb.entries.forEach { entry ->
                            Row(
                                Modifier.fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(VColors.surfaceTint.copy(alpha = 0.5f))
                                    .padding(horizontal = 8.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    "${entry.rank}",
                                    style = VTypography.body.copy(color = 
                                        if (entry.rank <= 3) VColors.violetInk else VColors.ink
                                    ).copy(fontSize = 13.sp, fontWeight = FontWeight.Bold),
                                    modifier = Modifier.width(28.dp),
                                )
                                Text(
                                    entry.studentName.ifBlank { appString(StringKeys.TC_STUDENT) },
                                    style = VTypography.body.copy(color = VColors.ink),
                                    modifier = Modifier.weight(1f),
                                )
                                Text(
                                    "${entry.score}/${entry.totalMarks}",
                                    style = VTypography.body.copy(color = VColors.ink),
                                )
                                Text(
                                    "${entry.percentage}%",
                                    style = VTypography.body.copy(color = 
                                        if (entry.percentage >= 50) VColors.success else VColors.error
                                    ).copy(fontSize = 13.sp, fontWeight = FontWeight.Bold),
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
                VButton(appString(StringKeys.COMMON_BUTTON_CLOSE), onClick = { viewModel.closeLeaderboard() }, full = true, variant = VButtonVariant.Secondary, tone = VButtonTone.Navy, size = VButtonSize.Md)
            }
        }
    }
}
