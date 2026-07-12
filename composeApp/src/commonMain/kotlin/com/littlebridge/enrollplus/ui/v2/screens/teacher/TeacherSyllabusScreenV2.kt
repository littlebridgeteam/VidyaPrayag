package com.littlebridge.enrollplus.ui.v2.screens.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import com.littlebridge.enrollplus.ui.tokens.VTypography
import androidx.compose.ui.text.TextStyle
import com.littlebridge.enrollplus.util.formatDecimal
import org.koin.compose.viewmodel.koinViewModel

// ── Cream/violet token bridge for the syllabus tracker ───────────────────────
// Maps the legacy VTheme accessor names used across this screen onto the new
// cream/violet token system (VColors/VTypography). This keeps every layout and
// state branch below unchanged while retiring the lavender VTheme dependency.
private object SylColors {
    val background get() = VColors.cream
    val card get() = VColors.white
    val cream get() = VColors.surfaceTint      // subtle chip / disc fill
    val hairline get() = VColors.line
    val ink get() = VColors.ink
    val ink2 get() = VColors.ink2
    val ink3 get() = VColors.ink3
    val navyDeep get() = VColors.ink           // primary heading ink
    val accent get() = VColors.violet
    val accentDeep get() = VColors.violetInk
    val teal get() = VColors.mint              // coverage / positive
    val tealDeep get() = VColors.success
    val danger get() = VColors.error
    val dangerInk get() = VColors.error
}

private object SylType {
    val h3: TextStyle get() = VTypography.h3
    val body: TextStyle get() = VTypography.body
    val bodyStrong: TextStyle get() = VTypography.body
    val caption: TextStyle get() = VTypography.caption
}

private fun TextStyle.colored(color: Color): TextStyle = copy(color = color)

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
    viewModel: TeacherSyllabusViewModel = koinViewModel(),
) {
    val c = SylColors
    val state by viewModel.state.collectAsStateV2()

    LaunchedEffect(assignmentId) {
        if (assignmentId.isNotBlank() && state.assignmentId != assignmentId) {
            viewModel.load(assignmentId)
            viewModel.loadQuizzes()
            viewModel.loadPaceWarning()
        }
    }

    Box(modifier.fillMaxSize().background(c.background)) {
        when {
            state.isLoading && state.units.isEmpty() -> TeacherCenterState { TeacherSpinner() }
            state.error != null && state.units.isEmpty() -> TeacherCenterState {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(appString(StringKeys.TC_COULDNT_LOAD_SYLLABUS), style = SylType.h3.colored(c.ink))
                    Spacer(Modifier.height(12.dp))
                    VButton(appString(StringKeys.COMMON_BUTTON_RETRY), onClick = { viewModel.retry() }, tone = VButtonTone.Lavender)
                }
            }
            else -> SyllabusBody(viewModel, scopeLabel)
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
private fun SyllabusBody(viewModel: TeacherSyllabusViewModel, scopeLabel: String) {
    val c = SylColors
    val state by viewModel.state.collectAsStateV2()
    val pct = (state.progress * 100).toInt()

    LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 14.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // ── Header card with progress ring + edit toggle ──
        item {
            VtCard(padding = 16.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TRing(percent = pct, modifier = Modifier.size(72.dp), accent = c.tealDeep, label = "$pct%", labelSize = 16.sp)
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        VtEyebrow(appString(StringKeys.TC_SYLLABUS), dot = c.tealDeep)
                        Spacer(Modifier.height(4.dp))
                        Text(scopeLabel.ifBlank { "${state.className}-${state.section} · ${state.subject}" }, style = SylType.bodyStrong.colored(c.navyDeep).copy(fontSize = 15.sp, fontWeight = FontWeight.ExtraBold))
                        Text(appString(StringKeys.TC_N_OF_N_UNITS_COVERED, "covered" to state.coveredCount.toString(), "total" to state.totalCount.toString()), style = SylType.caption.colored(c.ink2).copy(fontSize = 12.sp))
                    }
                    val ix = remember { MutableInteractionSource() }
                    Box(
                        Modifier.size(34.dp).clip(CircleShape).background(if (state.isEditing) c.accent.copy(alpha = 0.14f) else c.cream)
                            .clickable(interactionSource = ix, indication = null) { viewModel.toggleEditing() },
                        contentAlignment = Alignment.Center,
                    ) { Icon(VIcons.Edit3, contentDescription = appString(StringKeys.TC_EDIT), tint = if (state.isEditing) c.accentDeep else c.ink2, modifier = Modifier.size(16.dp)) }
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
                    size = VButtonSize.Sm,
                    loading = state.isAutoFilling,
                    leading = { Icon(VIcons.Sparkles, contentDescription = null, modifier = Modifier.size(14.dp)) },
                )
                VButton(
                    appString(StringKeys.TC_DAILY_LOG),
                    onClick = { viewModel.openDailyLogPopup() },
                    modifier = Modifier.weight(1f),
                    variant = VButtonVariant.Secondary,
                    tone = VButtonTone.Teal,
                    size = VButtonSize.Sm,
                    leading = { Icon(VIcons.ClipboardList, contentDescription = null, modifier = Modifier.size(14.dp)) },
                )
                VButton(
                    appString(StringKeys.TC_QUIZ),
                    onClick = { viewModel.openQuizSheetFromButton() },
                    modifier = Modifier.weight(1f),
                    variant = VButtonVariant.Secondary,
                    tone = VButtonTone.Lavender,
                    size = VButtonSize.Sm,
                    leading = { Icon(VIcons.GraduationCap, contentDescription = null, modifier = Modifier.size(14.dp)) },
                )
            }
        }

        // ── Edit mode: add chapter button ──
        if (state.isEditing) {
            item {
                if (state.addingUnderParentId == null) {
                    VButton(appString(StringKeys.TC_ADD_A_CHAPTER), onClick = { viewModel.openAdd(null) }, full = true, variant = VButtonVariant.Secondary, tone = VButtonTone.Teal, size = VButtonSize.Md, leading = { Icon(VIcons.Plus, contentDescription = null, modifier = Modifier.size(15.dp)) })
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
                VtCard { Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    VtIconDisc(VIcons.AlertCircle, tint = c.dangerInk, bg = c.danger.copy(alpha = 0.14f), size = 48.dp, glyph = 24.dp)
                    Spacer(Modifier.height(10.dp))
                    Text(appString(StringKeys.TC_NO_NCERT_REFERENCE_FOUND), style = SylType.h3.colored(c.ink))
                    Text(state.autoFillError ?: "", style = SylType.caption.colored(c.ink3).copy(fontSize = 12.sp))
                    Spacer(Modifier.height(12.dp))
                    EmptyStateOptions(viewModel)
                } }
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
                VtEyebrow(appString(StringKeys.TC_QUIZZES), dot = c.accent)
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
    val c = SylColors
    val state by viewModel.state.collectAsStateV2()
    val isChapter = state.addingUnderParentId.isNullOrBlank()
    VtCard(padding = 16.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(if (isChapter) appString(StringKeys.TC_NEW_CHAPTER) else appString(StringKeys.TC_NEW_TOPIC), style = SylType.bodyStrong.colored(c.navyDeep).copy(fontWeight = FontWeight.ExtraBold))
            VInput(value = state.addTitle, onValueChange = viewModel::setAddTitle, placeholder = if (isChapter) appString(StringKeys.TC_CHAPTER_TITLE) else appString(StringKeys.TC_TOPIC_TITLE))
            if (state.addError != null) Text(state.addError ?: "", style = SylType.caption.colored(c.dangerInk).copy(fontSize = 12.sp))
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
    val c = SylColors
    val indent = (u.depth.coerceIn(0, 3) * 16).dp
    val ix = remember { MutableInteractionSource() }
    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = indent)
            .clip(RoundedCornerShape(16.dp))
            .background(if (u.isCovered) c.teal.copy(alpha = 0.08f) else c.card)
            .border(1.dp, if (u.isCovered) c.teal.copy(alpha = 0.35f) else c.hairline, RoundedCornerShape(16.dp))
            .clickable(interactionSource = ix, indication = null, enabled = !isUpdating) { onToggle() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // Coverage check disc.
        Box(
            Modifier.size(26.dp).clip(CircleShape).background(if (u.isCovered) c.tealDeep else c.cream).border(1.dp, if (u.isCovered) c.tealDeep else c.hairline, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (isUpdating) TeacherSpinner(14.dp)
            else if (u.isCovered) Icon(VIcons.Check, contentDescription = null, tint = androidx.compose.ui.graphics.Color.White, modifier = Modifier.size(14.dp))
        }
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    u.title,
                    style = (if (u.isChapter) SylType.bodyStrong else SylType.body).colored(c.ink).copy(fontSize = if (u.isChapter) 14.5.sp else 13.5.sp, fontWeight = if (u.isChapter) FontWeight.ExtraBold else FontWeight.Medium),
                )
                if (isDraft) {
                    VtPill(appString(StringKeys.TC_DRAFT), bg = c.accent.copy(alpha = 0.14f), fg = c.accentDeep)
                }
            }
            if (u.isCovered && !u.coveredOn.isNullOrBlank()) {
                Text(appString(StringKeys.TC_COVERED_DATE, "date" to prettyDateShort(u.coveredOn)), style = SylType.caption.colored(c.tealDeep).copy(fontSize = 10.5.sp))
            }
        }
        // Add topic button (edit mode, chapters only)
        if (editing && u.isChapter) {
            Box(
                Modifier.size(28.dp).clip(CircleShape).background(c.cream)
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onAddTopic() },
                contentAlignment = Alignment.Center,
            ) { Icon(VIcons.Plus, contentDescription = appString(StringKeys.TC_ADD_TOPIC), tint = c.ink2, modifier = Modifier.size(14.dp)) }
        }
        // Delete button (edit mode)
        if (editing) {
            val ixDel = remember { MutableInteractionSource() }
            Box(
                Modifier.size(28.dp).clip(CircleShape).background(c.danger.copy(alpha = 0.1f))
                    .clickable(interactionSource = ixDel, indication = null) { onDelete() },
                contentAlignment = Alignment.Center,
            ) { Icon(VIcons.Minus, contentDescription = appString(StringKeys.COMMON_BUTTON_DELETE), tint = c.dangerInk, modifier = Modifier.size(14.dp)) }
        }
    }
}

// ── Parse Syllabus bottom sheet ──────────────────────────────────────────────

@Composable
private fun ParseSyllabusSheet(viewModel: TeacherSyllabusViewModel) {
    val c = SylColors
    val state by viewModel.state.collectAsStateV2()

    Box(
        Modifier
            .fillMaxSize()
            .background(c.ink.copy(alpha = 0.4f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { viewModel.closeParseSheet() },
    ) {
        VtCard(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .heightIn(min = 200.dp, max = 520.dp)
                .padding(bottom = 0.dp),
            padding = 20.dp,
        ) {
            Column(
                Modifier.fillMaxWidth().clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) {},
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(VIcons.Sparkles, contentDescription = null, tint = c.accentDeep, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(appString(StringKeys.TC_PARSE_SYLLABUS), style = SylType.h3.colored(c.navyDeep).copy(fontSize = 17.sp))
                    Spacer(Modifier.weight(1f))
                    Box(
                        Modifier.size(28.dp).clip(CircleShape).background(c.cream)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) { viewModel.closeParseSheet() },
                        contentAlignment = Alignment.Center,
                    ) { Icon(VIcons.Close, contentDescription = appString(StringKeys.COMMON_BUTTON_CLOSE), tint = c.ink2, modifier = Modifier.size(16.dp)) }
                }

                if (state.parsedUnits.isEmpty()) {
                    Text(appString(StringKeys.TC_PASTE_SYLLABUS_HINT), style = SylType.body.colored(c.ink2).copy(fontSize = 13.sp))
                    VInput(
                        value = state.parseRawText,
                        onValueChange = viewModel::setParseRawText,
                        placeholder = appString(StringKeys.TC_PASTE_SYLLABUS_PH),
                        singleLine = false,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp, max = 200.dp),
                    )
                    if (state.parseError != null) {
                        Text(state.parseError ?: "", style = SylType.caption.colored(c.dangerInk).copy(fontSize = 12.sp))
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
                    Text(appString(StringKeys.TC_PREVIEW_N_UNITS_FOUND, "count" to state.parsedUnits.size.toString()), style = SylType.bodyStrong.colored(c.navyDeep).copy(fontWeight = FontWeight.ExtraBold, fontSize = 14.sp))
                    LazyColumn(
                        Modifier.fillMaxWidth().heightIn(max = 240.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        items(state.parsedUnits) { pu ->
                            val indent = (pu.depth * 16).dp
                            Row(Modifier.fillMaxWidth().padding(start = indent), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(if (pu.depth == 0) "•" else "–", style = SylType.body.colored(c.ink3).copy(fontSize = 13.sp))
                                Text(pu.title, style = SylType.body.colored(c.ink).copy(fontSize = 13.sp, fontWeight = if (pu.depth == 0) FontWeight.Bold else FontWeight.Normal))
                            }
                        }
                    }
                    if (state.parseError != null) {
                        Text(state.parseError ?: "", style = SylType.caption.colored(c.dangerInk).copy(fontSize = 12.sp))
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
    val c = SylColors
    val state by viewModel.state.collectAsStateV2()
    val expandedChapters = remember { mutableStateMapOf<String, Boolean>() }
    val expandedTopics = remember { mutableStateMapOf<String, Boolean>() }

    Box(
        Modifier
            .fillMaxSize()
            .background(c.ink.copy(alpha = 0.4f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { viewModel.dismissDailyLogPopup() },
    ) {
        VtCard(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .heightIn(min = 200.dp, max = 620.dp),
            padding = 20.dp,
        ) {
            Column(
                Modifier.fillMaxWidth().clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) {},
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(VIcons.ClipboardList, contentDescription = null, tint = c.tealDeep, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(appString(StringKeys.TC_DAILY_CLASS_LOG), style = SylType.h3.colored(c.navyDeep).copy(fontSize = 17.sp))
                        Text("${state.dailyLogClassName} · ${state.dailyLogSubject}", style = SylType.caption.colored(c.ink2).copy(fontSize = 12.sp))
                    }
                    Spacer(Modifier.weight(1f))
                    Box(
                        Modifier.size(28.dp).clip(CircleShape).background(c.cream)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) { viewModel.closeDailyLogPopup() },
                        contentAlignment = Alignment.Center,
                    ) { Icon(VIcons.Close, contentDescription = appString(StringKeys.COMMON_BUTTON_CLOSE), tint = c.ink2, modifier = Modifier.size(16.dp)) }
                }

                val selectedCount = state.dailyLogSelectedTopicIds.size
                Text(
                    if (selectedCount == 0) appString(StringKeys.TC_SELECT_TOPICS_COVERED_TODAY) else appString(StringKeys.TC_N_TOPICS_SELECTED, "count" to selectedCount.toString()),
                    style = SylType.bodyStrong.colored(c.ink2).copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
                )

                if (state.units.isNotEmpty()) {
                    LazyColumn(
                        Modifier.fillMaxWidth().weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
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
                Text(appString(StringKeys.TC_COVERAGE_N_PCT, "pct" to state.dailyLogCoveragePct.toString()), style = SylType.bodyStrong.colored(c.ink2).copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    val ixMinus = remember { MutableInteractionSource() }
                    val ixPlus = remember { MutableInteractionSource() }
                    Box(
                        Modifier.size(32.dp).clip(CircleShape).background(c.cream)
                            .clickable(interactionSource = ixMinus, indication = null) { viewModel.setDailyLogCoveragePct(state.dailyLogCoveragePct - 10) },
                        contentAlignment = Alignment.Center,
                    ) { Icon(VIcons.Minus, contentDescription = null, tint = c.ink2, modifier = Modifier.size(16.dp)) }
                    Text("${state.dailyLogCoveragePct}%", style = SylType.bodyStrong.colored(c.navyDeep).copy(fontSize = 16.sp, fontWeight = FontWeight.ExtraBold))
                    Box(
                        Modifier.size(32.dp).clip(CircleShape).background(c.cream)
                            .clickable(interactionSource = ixPlus, indication = null) { viewModel.setDailyLogCoveragePct(state.dailyLogCoveragePct + 10) },
                        contentAlignment = Alignment.Center,
                    ) { Icon(VIcons.Plus, contentDescription = null, tint = c.ink2, modifier = Modifier.size(16.dp)) }
                }

                // Summary text
                VInput(
                    value = state.dailyLogSummary,
                    onValueChange = viewModel::setDailyLogSummary,
                    placeholder = appString(StringKeys.TC_WHAT_TAUGHT_TODAY_OPTIONAL),
                    singleLine = false,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 60.dp, max = 100.dp),
                )

                if (state.dailyLogError != null) {
                    Text(state.dailyLogError ?: "", style = SylType.caption.colored(c.dangerInk).copy(fontSize = 12.sp))
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
    val c = SylColors
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(c.cream)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onToggle() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            if (expanded) VIcons.ChevronDown else VIcons.ChevronRight,
            contentDescription = null,
            tint = c.ink2,
            modifier = Modifier.size(16.dp),
        )
        Text(title, style = SylType.bodyStrong.colored(c.ink).copy(fontSize = 13.sp, fontWeight = FontWeight.Bold))
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
    val c = SylColors
    Row(
        Modifier.fillMaxWidth().padding(start = 28.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) c.teal.copy(alpha = 0.08f) else androidx.compose.ui.graphics.Color.Transparent)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            Modifier.size(18.dp).clip(CircleShape)
                .background(if (selected) c.tealDeep else c.cream)
                .border(1.dp, if (selected) c.tealDeep else c.hairline, CircleShape)
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onToggleSelect() },
            contentAlignment = Alignment.Center,
        ) {
            if (selected) Icon(VIcons.Check, contentDescription = null, tint = androidx.compose.ui.graphics.Color.White, modifier = Modifier.size(11.dp))
        }
        Text(
            title,
            style = SylType.body.colored(c.ink).copy(fontSize = 12.sp),
            modifier = Modifier.weight(1f).clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onToggleSelect() },
        )
        if (hasSubtopics) {
            Box(
                Modifier.size(20.dp).clip(CircleShape)
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onToggleExpand() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    if (expanded) VIcons.ChevronDown else VIcons.ChevronRight,
                    contentDescription = null,
                    tint = c.ink3,
                    modifier = Modifier.size(14.dp),
                )
            }
        }
    }
}

@Composable
private fun DailyLogSubtopicRow(title: String, selected: Boolean, onToggle: () -> Unit) {
    val c = SylColors
    Row(
        Modifier.fillMaxWidth().padding(start = 56.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(if (selected) c.teal.copy(alpha = 0.06f) else androidx.compose.ui.graphics.Color.Transparent)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onToggle() }
            .padding(horizontal = 8.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            Modifier.size(14.dp).clip(CircleShape)
                .background(if (selected) c.tealDeep else c.cream)
                .border(1.dp, if (selected) c.tealDeep else c.hairline, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (selected) Icon(VIcons.Check, contentDescription = null, tint = androidx.compose.ui.graphics.Color.White, modifier = Modifier.size(9.dp))
        }
        Text(title, style = SylType.body.colored(c.ink2).copy(fontSize = 11.sp))
    }
}

// ── Quiz generation sheet ────────────────────────────────────────────────────

@Composable
private fun QuizSheet(viewModel: TeacherSyllabusViewModel) {
    val c = SylColors
    val state by viewModel.state.collectAsStateV2()

    Box(
        Modifier
            .fillMaxSize()
            .background(c.ink.copy(alpha = 0.4f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { viewModel.closeQuizSheet() },
    ) {
        VtCard(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .heightIn(min = 200.dp, max = 600.dp),
            padding = 20.dp,
        ) {
            Column(
                Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) {},
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(VIcons.Sparkles, contentDescription = null, tint = c.accentDeep, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(appString(StringKeys.TC_GENERATE_QUIZ), style = SylType.h3.colored(c.navyDeep).copy(fontSize = 17.sp))
                    Spacer(Modifier.weight(1f))
                    Box(
                        Modifier.size(28.dp).clip(CircleShape).background(c.cream)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) { viewModel.closeQuizSheet() },
                        contentAlignment = Alignment.Center,
                    ) { Icon(VIcons.Close, contentDescription = appString(StringKeys.COMMON_BUTTON_CLOSE), tint = c.ink2, modifier = Modifier.size(16.dp)) }
                }

                // ── Unit selection (multiple) ──────────────────────────────
                Text(appString(StringKeys.TC_SELECT_UNITS), style = SylType.bodyStrong.colored(c.ink2).copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold))
                val allUnits = state.units
                Column(
                    Modifier.fillMaxWidth().heightIn(max = 150.dp).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    allUnits.forEach { u ->
                        val isSelected = u.id in state.quizSelectedUnitIds
                        val ixUnit = remember { MutableInteractionSource() }
                        Row(
                            Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) c.accent.copy(alpha = 0.08f) else Color.Transparent)
                                .clickable(interactionSource = ixUnit, indication = null) { viewModel.toggleQuizUnit(u.id) }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Box(
                                Modifier.size(16.dp).clip(RoundedCornerShape(4.dp))
                                    .background(if (isSelected) c.accentDeep else c.cream)
                                    .border(1.dp, if (isSelected) c.accentDeep else c.hairline, RoundedCornerShape(4.dp)),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (isSelected) Icon(VIcons.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                            }
                            Text(
                                u.title,
                                style = SylType.body.colored(if (isSelected) c.navyDeep else c.ink2).copy(fontSize = 12.sp),
                                maxLines = 1,
                            )
                        }
                    }
                }

                // ── Question types ─────────────────────────────────────────
                Text(appString(StringKeys.TC_QUESTION_TYPES), style = SylType.bodyStrong.colored(c.ink2).copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("MCQ" to appString(StringKeys.TC_MCQ), "FILL_BLANK" to appString(StringKeys.TC_FILL_UPS), "TRUE_FALSE" to appString(StringKeys.TC_TRUE_FALSE), "MATCH" to appString(StringKeys.TC_MATCH)).forEach { (type, label) ->
                        val selected = type in state.quizQuestionTypes
                        val ixType = remember { MutableInteractionSource() }
                        Box(
                            Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selected) c.accent.copy(alpha = 0.14f) else c.cream)
                                .border(1.dp, if (selected) c.accentDeep else c.hairline, RoundedCornerShape(10.dp))
                                .clickable(interactionSource = ixType, indication = null) { viewModel.toggleQuizQuestionType(type) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                label,
                                style = SylType.body.colored(if (selected) c.accentDeep else c.ink2).copy(fontSize = 11.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal),
                            )
                        }
                    }
                }

                // ── Number of questions ────────────────────────────────────
                Text(appString(StringKeys.TC_NUMBER_OF_QUESTIONS_N, "count" to state.quizNumQuestions.toString()), style = SylType.bodyStrong.colored(c.ink2).copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    val ixMinus = remember { MutableInteractionSource() }
                    val ixPlus = remember { MutableInteractionSource() }
                    Box(
                        Modifier.size(32.dp).clip(CircleShape).background(c.cream)
                            .clickable(interactionSource = ixMinus, indication = null) { viewModel.setQuizNumQuestions(state.quizNumQuestions - 1) },
                        contentAlignment = Alignment.Center,
                    ) { Icon(VIcons.Minus, contentDescription = null, tint = c.ink2, modifier = Modifier.size(16.dp)) }
                    Text("${state.quizNumQuestions}", style = SylType.bodyStrong.colored(c.navyDeep).copy(fontSize = 16.sp, fontWeight = FontWeight.ExtraBold))
                    Box(
                        Modifier.size(32.dp).clip(CircleShape).background(c.cream)
                            .clickable(interactionSource = ixPlus, indication = null) { viewModel.setQuizNumQuestions(state.quizNumQuestions + 1) },
                        contentAlignment = Alignment.Center,
                    ) { Icon(VIcons.Plus, contentDescription = null, tint = c.ink2, modifier = Modifier.size(16.dp)) }
                }

                // ── Difficulty ─────────────────────────────────────────────
                Text(appString(StringKeys.TC_DIFFICULTY), style = SylType.bodyStrong.colored(c.ink2).copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("EASY" to appString(StringKeys.TC_EASY), "MEDIUM" to appString(StringKeys.TC_MEDIUM), "HARD" to appString(StringKeys.TC_HARD)).forEach { (diff, label) ->
                        val selected = state.quizDifficulty == diff
                        val ixDiff = remember { MutableInteractionSource() }
                        Box(
                            Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selected) c.accent.copy(alpha = 0.14f) else c.cream)
                                .border(1.dp, if (selected) c.accentDeep else c.hairline, RoundedCornerShape(10.dp))
                                .clickable(interactionSource = ixDiff, indication = null) { viewModel.setQuizDifficulty(diff) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                label,
                                style = SylType.body.colored(if (selected) c.accentDeep else c.ink2).copy(fontSize = 13.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal),
                            )
                        }
                    }
                }

                if (state.quizError != null) {
                    Text(state.quizError ?: "", style = SylType.caption.colored(c.dangerInk).copy(fontSize = 12.sp))
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
    val c = SylColors
    VtCard(padding = 14.dp) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            VtIconDisc(VIcons.GraduationCap, tint = c.accentDeep, bg = c.accent.copy(alpha = 0.14f), size = 36.dp, glyph = 18.dp)
            Column(Modifier.weight(1f)) {
                Text(q.title.ifBlank { appString(StringKeys.TC_QUIZ) }, style = SylType.bodyStrong.colored(c.ink).copy(fontSize = 14.sp, fontWeight = FontWeight.Bold))
                Text(appString(StringKeys.TC_N_QUESTIONS_STATUS, "count" to q.questions.size.toString(), "status" to q.status), style = SylType.caption.colored(c.ink2).copy(fontSize = 11.sp))
            }
            if (q.status == "DRAFT") {
                VButton(appString(StringKeys.TC_PUBLISH), onClick = onPublish, size = VButtonSize.Sm, tone = VButtonTone.Lavender, variant = VButtonVariant.Secondary)
            } else {
                VtPill(appString(StringKeys.TC_PUBLISHED), bg = c.teal.copy(alpha = 0.14f), fg = c.tealDeep)
                VButton(appString(StringKeys.TC_RESULTS), onClick = onLeaderboard, size = VButtonSize.Sm, tone = VButtonTone.Sky, variant = VButtonVariant.Secondary)
            }
        }
    }
}

// ── Empty state with 3 clear options ─────────────────────────────────────────

@Composable
private fun EmptyStateOptions(viewModel: TeacherSyllabusViewModel) {
    val c = SylColors
    val state by viewModel.state.collectAsStateV2()
    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
        Text(appString(StringKeys.TC_NO_UNITS_YET), style = SylType.h3.colored(c.ink).copy(fontSize = 16.sp))
        Text(appString(StringKeys.TC_CHOOSE_HOW_TO_BUILD_SYLLABUS), style = SylType.caption.colored(c.ink3).copy(fontSize = 12.sp))
        Spacer(Modifier.height(4.dp))
        // Option 1: Auto-fill from NCERT
        VtCard(padding = 16.dp, onClick = { viewModel.autoFill() }) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                VtIconDisc(VIcons.Sparkles, tint = c.accentDeep, bg = c.accent.copy(alpha = 0.14f), size = 40.dp, glyph = 20.dp)
                Column(Modifier.weight(1f)) {
                    Text(appString(StringKeys.TC_AUTO_FILL_FROM_NCERT), style = SylType.bodyStrong.colored(c.ink).copy(fontSize = 14.sp, fontWeight = FontWeight.ExtraBold))
                    Text(appString(StringKeys.TC_FETCH_STANDARD_NCERT_SYLLABUS), style = SylType.caption.colored(c.ink3).copy(fontSize = 11.sp))
                }
            }
        }
        // Option 2: Paste text for AI parse
        VtCard(padding = 16.dp, onClick = { viewModel.openParseSheet() }) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                VtIconDisc(VIcons.ClipboardList, tint = c.tealDeep, bg = c.teal.copy(alpha = 0.14f), size = 40.dp, glyph = 20.dp)
                Column(Modifier.weight(1f)) {
                    Text(appString(StringKeys.TC_PASTE_SYLLABUS_TEXT), style = SylType.bodyStrong.colored(c.ink).copy(fontSize = 14.sp, fontWeight = FontWeight.ExtraBold))
                    Text(appString(StringKeys.TC_AI_EXTRACT_CHAPTERS_TOPICS), style = SylType.caption.colored(c.ink3).copy(fontSize = 11.sp))
                }
            }
        }
        // Option 3: Add manually
        VtCard(padding = 16.dp, onClick = { viewModel.toggleEditing(); viewModel.openAdd(null) }) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                VtIconDisc(VIcons.Plus, tint = c.ink2, bg = c.cream, size = 40.dp, glyph = 20.dp)
                Column(Modifier.weight(1f)) {
                    Text(appString(StringKeys.TC_ADD_MANUALLY), style = SylType.bodyStrong.colored(c.ink).copy(fontSize = 14.sp, fontWeight = FontWeight.ExtraBold))
                    Text(appString(StringKeys.TC_CREATE_CHAPTERS_TOPICS_ONE_BY_ONE), style = SylType.caption.colored(c.ink3).copy(fontSize = 11.sp))
                }
            }
        }
        if (state.isAutoFilling) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                TeacherSpinner(16.dp)
                Text(appString(StringKeys.TC_FETCHING_NCERT_REFERENCE), style = SylType.caption.colored(c.ink2).copy(fontSize = 12.sp))
            }
        }
    }
}

// ── Pace warning banner ──────────────────────────────────────────────────────

@Composable
private fun PaceWarningBanner(warning: com.littlebridge.enrollplus.feature.teacher.domain.model.SylPaceWarning) {
    val c = SylColors
    val isBehind = warning.level == "BEHIND" || warning.level == "CRITICAL"
    val bg = if (isBehind) c.danger.copy(alpha = 0.08f) else c.accent.copy(alpha = 0.08f)
    val fg = if (isBehind) c.dangerInk else c.accentDeep
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
                Text(label, style = SylType.bodyStrong.colored(fg).copy(fontSize = 13.sp, fontWeight = FontWeight.ExtraBold))
                Text(
                    appString(StringKeys.TC_PACE_EXPECTED_ACTUAL, "expected" to warning.expectedPct.toString(), "actual" to warning.actualPct.toString(), "delta" to warning.deviationPct.toString()),
                    style = SylType.caption.colored(c.ink2).copy(fontSize = 11.sp),
                )
                if (warning.message.isNotBlank()) {
                    Text(warning.message, style = SylType.caption.colored(c.ink3).copy(fontSize = 11.sp))
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
                    Text(metricsText, style = SylType.caption.colored(c.ink3).copy(fontSize = 10.sp))
                }
                if (warning.estimatedCompletionDate.isNotBlank()) {
                    Text(appString(StringKeys.TC_EST_COMPLETION_DATE, "date" to warning.estimatedCompletionDate), style = SylType.caption.colored(c.ink3).copy(fontSize = 10.sp))
                }
                if (warning.avgCoveragePerClass > 0) {
                    Text(appString(StringKeys.TC_AVG_N_PCT_PER_CLASS, "pct" to formatDecimal(warning.avgCoveragePerClass, 1)), style = SylType.caption.colored(c.ink3).copy(fontSize = 10.sp))
                }
            }
        }
    }
}

// ── Draft approval bar ───────────────────────────────────────────────────────

@Composable
private fun DraftApprovalBar(viewModel: TeacherSyllabusViewModel) {
    val c = SylColors
    val state by viewModel.state.collectAsStateV2()
    val draftCount = state.draftUnits.size
    VtCard(padding = 14.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.size(28.dp).clip(CircleShape).background(c.accent.copy(alpha = 0.14f)), contentAlignment = Alignment.Center) {
                    Icon(VIcons.ShieldCheck, contentDescription = null, tint = c.accentDeep, modifier = Modifier.size(14.dp))
                }
                Text(appString(StringKeys.TC_N_DRAFT_UNITS_PENDING_APPROVAL, "count" to draftCount.toString()), style = SylType.bodyStrong.colored(c.accentDeep).copy(fontSize = 13.sp, fontWeight = FontWeight.ExtraBold))
            }
            Text(appString(StringKeys.TC_DRAFT_UNITS_NOT_VISIBLE_TO_PARENTS), style = SylType.caption.colored(c.ink3).copy(fontSize = 11.sp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                VButton(appString(StringKeys.TC_REJECT_ALL), onClick = { viewModel.rejectAllDrafts() }, modifier = Modifier.weight(1f), variant = VButtonVariant.Ghost, size = VButtonSize.Sm, loading = state.isApproving)
                VButton(appString(StringKeys.TC_APPROVE_ALL), onClick = { viewModel.approveAllDrafts() }, modifier = Modifier.weight(1f), tone = VButtonTone.Lavender, size = VButtonSize.Sm, loading = state.isApproving)
            }
            if (state.approveError != null) {
                Text(state.approveError ?: "", style = SylType.caption.colored(c.dangerInk).copy(fontSize = 11.sp))
            }
        }
    }
}

// ── Auto-fill preview sheet ──────────────────────────────────────────────────

@Composable
private fun AutoFillPreviewSheet(viewModel: TeacherSyllabusViewModel) {
    val c = SylColors
    val state by viewModel.state.collectAsStateV2()

    Box(
        Modifier
            .fillMaxSize()
            .background(c.ink.copy(alpha = 0.4f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { viewModel.dismissAutoFillPreview() },
    ) {
        VtCard(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .heightIn(min = 200.dp, max = 600.dp)
                .padding(bottom = 0.dp),
            padding = 20.dp,
        ) {
            Column(
                Modifier.fillMaxWidth().clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) {},
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(VIcons.Sparkles, contentDescription = null, tint = c.accentDeep, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(appString(StringKeys.TC_NCERT_AUTO_FILL), style = SylType.h3.colored(c.navyDeep).copy(fontSize = 17.sp))
                        if (state.autoFillSource.isNotBlank()) {
                            Text(state.autoFillSource, style = SylType.caption.colored(c.ink2).copy(fontSize = 12.sp))
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    Box(
                        Modifier.size(28.dp).clip(CircleShape).background(c.cream)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) { viewModel.dismissAutoFillPreview() },
                        contentAlignment = Alignment.Center,
                    ) { Icon(VIcons.Close, contentDescription = appString(StringKeys.COMMON_BUTTON_CLOSE), tint = c.ink2, modifier = Modifier.size(16.dp)) }
                }

                val totalChapters = state.autoFillChapters.size
                val totalTopics = state.autoFillChapters.sumOf { ch -> ch.topics.size }
                val totalSubtopics = state.autoFillChapters.sumOf { ch -> ch.topics.sumOf { t -> t.subtopics.size } }
                val totalUnits = totalChapters + totalTopics + totalSubtopics
                val subtopicText = if (totalSubtopics > 0) ", " + appString(StringKeys.TC_N_SUBTOPICS, "count" to totalSubtopics.toString()) else ""
                Text(
                    appString(StringKeys.TC_AUTO_FILL_PREVIEW, "chapters" to totalChapters.toString(), "topics" to totalTopics.toString(), "subtopics" to subtopicText, "units" to totalUnits.toString()),
                    style = SylType.body.colored(c.ink2).copy(fontSize = 13.sp),
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
                    Text(state.autoFillError ?: "", style = SylType.caption.colored(c.dangerInk).copy(fontSize = 12.sp))
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
    val c = SylColors
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(c.cream).padding(12.dp)) {
        Text(ch.title, style = SylType.bodyStrong.colored(c.ink).copy(fontSize = 14.sp, fontWeight = FontWeight.ExtraBold))
        if (ch.topics.isNotEmpty()) {
            Spacer(Modifier.height(6.dp))
            ch.topics.forEach { t ->
                Row(Modifier.fillMaxWidth().padding(start = 12.dp, top = 2.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("–", style = SylType.body.colored(c.ink3).copy(fontSize = 12.sp))
                    Text(t.title, style = SylType.body.colored(c.ink2).copy(fontSize = 12.sp))
                }
                if (t.subtopics.isNotEmpty()) {
                    t.subtopics.forEach { st ->
                        Row(Modifier.fillMaxWidth().padding(start = 28.dp, top = 1.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("·", style = SylType.body.colored(c.ink3).copy(fontSize = 11.sp))
                            Text(st.title, style = SylType.body.colored(c.ink3).copy(fontSize = 11.sp))
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
    val c = SylColors
    val state by viewModel.state.collectAsStateV2()
    val quiz = state.generatedQuiz ?: return

    Box(
        Modifier
            .fillMaxSize()
            .background(c.ink.copy(alpha = 0.4f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { viewModel.closeQuizPreview() },
    ) {
        VtCard(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .heightIn(min = 200.dp, max = 700.dp),
            padding = 20.dp,
        ) {
            Column(
                Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) {},
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                // Header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(VIcons.GraduationCap, contentDescription = null, tint = c.accentDeep, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(appString(StringKeys.TC_QUIZ_PREVIEW), style = SylType.h3.colored(c.navyDeep).copy(fontSize = 17.sp))
                    Spacer(Modifier.weight(1f))
                    Box(
                        Modifier.size(28.dp).clip(CircleShape).background(c.cream)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) { viewModel.closeQuizPreview() },
                        contentAlignment = Alignment.Center,
                    ) { Icon(VIcons.Close, contentDescription = appString(StringKeys.COMMON_BUTTON_CLOSE), tint = c.ink2, modifier = Modifier.size(16.dp)) }
                }

                Text(quiz.title, style = SylType.bodyStrong.colored(c.ink2).copy(fontSize = 13.sp))
                Text(appString(StringKeys.TC_N_QUESTIONS_STATUS, "count" to quiz.questions.size.toString(), "status" to quiz.status), style = SylType.caption.colored(c.ink3).copy(fontSize = 11.sp))

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
                    Text(state.quizPreviewError ?: "", style = SylType.caption.colored(c.dangerInk).copy(fontSize = 12.sp))
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
    val c = SylColors
    val ixEdit = remember { MutableInteractionSource() }

    Column(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(c.cream)
            .border(1.dp, c.hairline, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Q$index", style = SylType.bodyStrong.colored(c.accentDeep).copy(fontSize = 13.sp, fontWeight = FontWeight.Bold))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(question.question, style = SylType.body.colored(c.ink).copy(fontSize = 13.sp))
                if (question.options.isNotEmpty()) {
                    question.options.forEachIndexed { i, opt ->
                        val isCorrect = opt.startsWith(question.correctAnswer, ignoreCase = true) ||
                            question.correctIndex == i
                        Text(
                            opt,
                            style = SylType.body.colored(if (isCorrect) c.tealDeep else c.ink2).copy(
                                fontSize = 12.sp,
                                fontWeight = if (isCorrect) FontWeight.Bold else FontWeight.Normal,
                            ),
                        )
                    }
                } else if (question.correctAnswer.isNotBlank()) {
                    Text(appString(StringKeys.TC_ANSWER_COLON, "answer" to question.correctAnswer), style = SylType.body.colored(c.tealDeep).copy(fontSize = 12.sp, fontWeight = FontWeight.Bold))
                }
                if (!question.explanation.isNullOrBlank()) {
                    Text(appString(StringKeys.TC_EXPLANATION_COLON, "explanation" to question.explanation), style = SylType.caption.colored(c.ink3).copy(fontSize = 11.sp))
                }
                Text(question.questionType, style = SylType.caption.colored(c.ink3).copy(fontSize = 10.sp))
            }
            Box(
                Modifier.size(26.dp).clip(CircleShape).background(c.accent.copy(alpha = 0.1f))
                    .clickable(interactionSource = ixEdit, indication = null) { onEdit() },
                contentAlignment = Alignment.Center,
            ) { Icon(VIcons.Edit3, contentDescription = appString(StringKeys.TC_EDIT), tint = c.accentDeep, modifier = Modifier.size(13.dp)) }
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
    val c = SylColors
    var questionText by remember { mutableStateOf("") }
    var optionsText by remember { mutableStateOf("") }
    var correctAnswer by remember { mutableStateOf("") }
    var explanation by remember { mutableStateOf("") }
    var questionType by remember { mutableStateOf("MCQ") }

    Column(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(c.accent.copy(alpha = 0.06f))
            .border(1.dp, c.accentDeep.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(appString(StringKeys.TC_ADD_NEW_QUESTION), style = SylType.bodyStrong.colored(c.accentDeep).copy(fontSize = 13.sp, fontWeight = FontWeight.Bold))

        // Question type selector
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("MCQ" to appString(StringKeys.TC_MCQ), "FILL_BLANK" to appString(StringKeys.TC_FILL_UPS), "TRUE_FALSE" to appString(StringKeys.TC_TRUE_FALSE)).forEach { (type, label) ->
                val selected = questionType == type
                val ixType = remember { MutableInteractionSource() }
                Box(
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selected) c.accent.copy(alpha = 0.14f) else c.cream)
                        .border(1.dp, if (selected) c.accentDeep else c.hairline, RoundedCornerShape(8.dp))
                        .clickable(interactionSource = ixType, indication = null) {
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
                    Text(label, style = SylType.caption.colored(if (selected) c.accentDeep else c.ink2).copy(fontSize = 11.sp))
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
            Text(appString(StringKeys.TC_OPTIONS_ONE_PER_LINE), style = SylType.caption.colored(c.ink2).copy(fontSize = 11.sp))
            VInput(
                value = optionsText,
                onValueChange = { optionsText = it },
                placeholder = appString(StringKeys.TC_OPTIONS_PH),
                singleLine = false,
                modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp, max = 140.dp),
            )
        }

        if (questionType == "TRUE_FALSE") {
            Text(appString(StringKeys.TC_CORRECT_ANSWER), style = SylType.caption.colored(c.ink2).copy(fontSize = 11.sp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("true" to appString(StringKeys.TC_TRUE), "false" to appString(StringKeys.TC_FALSE)).forEach { (value, label) ->
                    val selected = correctAnswer.equals(value, ignoreCase = true)
                    val ixTF = remember { MutableInteractionSource() }
                    Box(
                        Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selected) c.accent.copy(alpha = 0.14f) else c.cream)
                            .border(1.dp, if (selected) c.accentDeep else c.hairline, RoundedCornerShape(8.dp))
                            .clickable(interactionSource = ixTF, indication = null) { correctAnswer = value }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(label, style = SylType.body.colored(if (selected) c.accentDeep else c.ink2).copy(fontSize = 13.sp))
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
    val c = SylColors
    var questionText by remember { mutableStateOf(question.question) }
    var optionsText by remember { mutableStateOf(question.options.joinToString("\n")) }
    var correctAnswer by remember { mutableStateOf(question.correctAnswer) }
    var explanation by remember { mutableStateOf(question.explanation ?: "") }
    var questionType by remember { mutableStateOf(question.questionType) }

    Column(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(c.accent.copy(alpha = 0.06f))
            .border(1.dp, c.accentDeep.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(appString(StringKeys.TC_EDITING_QUESTION), style = SylType.bodyStrong.colored(c.accentDeep).copy(fontSize = 13.sp, fontWeight = FontWeight.Bold))

        VInput(
            value = questionText,
            onValueChange = { questionText = it },
            placeholder = appString(StringKeys.TC_QUESTION_TEXT),
            singleLine = false,
            modifier = Modifier.fillMaxWidth().heightIn(min = 60.dp, max = 120.dp),
        )

        if (questionType == "MCQ") {
            Text(appString(StringKeys.TC_OPTIONS_ONE_PER_LINE), style = SylType.caption.colored(c.ink2).copy(fontSize = 11.sp))
            VInput(
                value = optionsText,
                onValueChange = { optionsText = it },
                placeholder = appString(StringKeys.TC_OPTIONS_PH),
                singleLine = false,
                modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp, max = 140.dp),
            )
        }

        if (questionType == "TRUE_FALSE") {
            Text(appString(StringKeys.TC_CORRECT_ANSWER), style = SylType.caption.colored(c.ink2).copy(fontSize = 11.sp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("true" to appString(StringKeys.TC_TRUE), "false" to appString(StringKeys.TC_FALSE)).forEach { (value, label) ->
                    val selected = correctAnswer.equals(value, ignoreCase = true)
                    val ixTF = remember { MutableInteractionSource() }
                    Box(
                        Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selected) c.accent.copy(alpha = 0.14f) else c.cream)
                            .border(1.dp, if (selected) c.accentDeep else c.hairline, RoundedCornerShape(8.dp))
                            .clickable(interactionSource = ixTF, indication = null) { correctAnswer = value }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(label, style = SylType.body.colored(if (selected) c.accentDeep else c.ink2).copy(fontSize = 13.sp))
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
                val ixType = remember { MutableInteractionSource() }
                Box(
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selected) c.accent.copy(alpha = 0.14f) else c.cream)
                        .border(1.dp, if (selected) c.accentDeep else c.hairline, RoundedCornerShape(8.dp))
                        .clickable(interactionSource = ixType, indication = null) { questionType = type }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        type.replace("_", " "),
                        style = SylType.body.colored(if (selected) c.accentDeep else c.ink2).copy(fontSize = 10.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal),
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
    val c = SylColors
    val state by viewModel.state.collectAsStateV2()

    Box(
        Modifier
            .fillMaxSize()
            .background(c.ink.copy(alpha = 0.4f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { viewModel.closeLeaderboard() },
    ) {
        VtCard(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .heightIn(min = 200.dp, max = 650.dp),
            padding = 20.dp,
        ) {
            Column(
                Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) {},
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                // Header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(VIcons.GraduationCap, contentDescription = null, tint = c.accentDeep, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(appString(StringKeys.TC_QUIZ_LEADERBOARD), style = SylType.h3.colored(c.navyDeep).copy(fontSize = 17.sp))
                    Spacer(Modifier.weight(1f))
                    Box(
                        Modifier.size(28.dp).clip(CircleShape).background(c.cream)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) { viewModel.closeLeaderboard() },
                        contentAlignment = Alignment.Center,
                    ) { Icon(VIcons.Close, contentDescription = appString(StringKeys.COMMON_BUTTON_CLOSE), tint = c.ink2, modifier = Modifier.size(16.dp)) }
                }

                val lb = state.leaderboard
                if (state.leaderboardLoading) {
                    Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text(appString(StringKeys.TC_LOADING_LEADERBOARD), style = SylType.body.colored(c.ink2))
                    }
                } else if (state.leaderboardError != null) {
                    Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text(state.leaderboardError ?: "", style = SylType.body.colored(c.dangerInk))
                    }
                } else if (lb != null) {
                    // Quiz info
                    Text(lb.quizTitle.ifBlank { appString(StringKeys.TC_QUIZ) }, style = SylType.bodyStrong.colored(c.ink).copy(fontSize = 15.sp))
                    if (lb.subject.isNotBlank()) {
                        Text(lb.subject, style = SylType.caption.colored(c.ink2).copy(fontSize = 12.sp))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(appString(StringKeys.TC_N_ATTEMPTED, "count" to lb.totalParticipants.toString()), style = SylType.caption.colored(c.ink2).copy(fontSize = 12.sp))
                        Text(appString(StringKeys.TC_N_ENROLLED, "count" to lb.totalStudents.toString()), style = SylType.caption.colored(c.ink2).copy(fontSize = 12.sp))
                    }

                    if (lb.entries.isEmpty()) {
                        Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                            Text(appString(StringKeys.TC_NO_ATTEMPTS_YET), style = SylType.body.colored(c.ink2))
                        }
                    } else {
                        // Column headers
                        Row(
                            Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("#", style = SylType.caption.colored(c.ink3).copy(fontSize = 11.sp, fontWeight = FontWeight.Bold), modifier = Modifier.width(28.dp))
                            Text(appString(StringKeys.TC_STUDENT), style = SylType.caption.colored(c.ink3).copy(fontSize = 11.sp, fontWeight = FontWeight.Bold), modifier = Modifier.weight(1f))
                            Text(appString(StringKeys.TC_SCORE), style = SylType.caption.colored(c.ink3).copy(fontSize = 11.sp, fontWeight = FontWeight.Bold))
                            Text("%", style = SylType.caption.colored(c.ink3).copy(fontSize = 11.sp, fontWeight = FontWeight.Bold))
                        }

                        lb.entries.forEach { entry ->
                            Row(
                                Modifier.fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(c.cream.copy(alpha = 0.5f))
                                    .padding(horizontal = 8.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    "${entry.rank}",
                                    style = SylType.bodyStrong.colored(
                                        if (entry.rank <= 3) c.accentDeep else c.ink
                                    ).copy(fontSize = 13.sp, fontWeight = FontWeight.Bold),
                                    modifier = Modifier.width(28.dp),
                                )
                                Text(
                                    entry.studentName.ifBlank { appString(StringKeys.TC_STUDENT) },
                                    style = SylType.body.colored(c.ink).copy(fontSize = 13.sp),
                                    modifier = Modifier.weight(1f),
                                )
                                Text(
                                    "${entry.score}/${entry.totalMarks}",
                                    style = SylType.body.colored(c.ink).copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
                                )
                                Text(
                                    "${entry.percentage}%",
                                    style = SylType.body.colored(
                                        if (entry.percentage >= 50) c.tealDeep else c.dangerInk
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
