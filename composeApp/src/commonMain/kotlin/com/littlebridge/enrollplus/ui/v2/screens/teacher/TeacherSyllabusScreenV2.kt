package com.littlebridge.enrollplus.ui.v2.screens.teacher

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import com.littlebridge.enrollplus.feature.teacher.domain.model.QuizDto
import com.littlebridge.enrollplus.feature.teacher.domain.model.QuizQuestionDto
import com.littlebridge.enrollplus.feature.teacher.domain.model.SylAutoFillChapter
import com.littlebridge.enrollplus.feature.teacher.domain.model.SylParsedUnit
import com.littlebridge.enrollplus.feature.teacher.presentation.SyllabusUnit
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherSyllabusViewModel
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherSyllabusState
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonSize
import com.littlebridge.enrollplus.ui.v2.components.VButtonTone
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VInput
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.theme.VTheme
import com.littlebridge.enrollplus.ui.v2.theme.colored
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
    viewModel: TeacherSyllabusViewModel = koinViewModel(),
) {
    val c = VTheme.colors
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
                    Text("Couldn't load syllabus", style = VTheme.type.h3.colored(c.ink))
                    Spacer(Modifier.height(12.dp))
                    VButton("Retry", onClick = { viewModel.retry() }, tone = VButtonTone.Lavender)
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
    }
}

@Composable
private fun SyllabusBody(viewModel: TeacherSyllabusViewModel, scopeLabel: String) {
    val c = VTheme.colors
    val state by viewModel.state.collectAsStateV2()
    val pct = (state.progress * 100).toInt()

    LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 14.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // ── Header card with progress ring + edit toggle ──
        item {
            TCard(padding = 16.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TRing(percent = pct, modifier = Modifier.size(72.dp), accent = c.tealDeep, label = "$pct%", labelSize = 16.sp)
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        TEyebrow("SYLLABUS", dot = c.tealDeep)
                        Spacer(Modifier.height(4.dp))
                        Text(scopeLabel.ifBlank { "${state.className}-${state.section} · ${state.subject}" }, style = VTheme.type.bodyStrong.colored(c.navyDeep).copy(fontSize = 15.sp, fontWeight = FontWeight.ExtraBold))
                        Text("${state.coveredCount} of ${state.totalCount} units covered", style = VTheme.type.caption.colored(c.ink2).copy(fontSize = 12.sp))
                    }
                    val ix = remember { MutableInteractionSource() }
                    Box(
                        Modifier.size(34.dp).clip(CircleShape).background(if (state.isEditing) c.accent.copy(alpha = 0.14f) else c.cream)
                            .clickable(interactionSource = ix, indication = null) { viewModel.toggleEditing() },
                        contentAlignment = Alignment.Center,
                    ) { Icon(VIcons.Edit3, contentDescription = "Edit", tint = if (state.isEditing) c.accentDeep else c.ink2, modifier = Modifier.size(16.dp)) }
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
                    "Auto-fill",
                    onClick = { viewModel.autoFill() },
                    modifier = Modifier.weight(1f),
                    variant = VButtonVariant.Secondary,
                    tone = VButtonTone.Lavender,
                    size = VButtonSize.Sm,
                    loading = state.isAutoFilling,
                    leading = { Icon(VIcons.Sparkles, contentDescription = null, modifier = Modifier.size(14.dp)) },
                )
                VButton(
                    "Daily Log",
                    onClick = { viewModel.openDailyLogPopup() },
                    modifier = Modifier.weight(1f),
                    variant = VButtonVariant.Secondary,
                    tone = VButtonTone.Teal,
                    size = VButtonSize.Sm,
                    leading = { Icon(VIcons.ClipboardList, contentDescription = null, modifier = Modifier.size(14.dp)) },
                )
                VButton(
                    "Quiz",
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
                    VButton("Add a chapter", onClick = { viewModel.openAdd(null) }, full = true, variant = VButtonVariant.Secondary, tone = VButtonTone.Teal, size = VButtonSize.Md, leading = { Icon(VIcons.Plus, contentDescription = null, modifier = Modifier.size(15.dp)) })
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
                TCard { Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    TIconDisc(VIcons.AlertCircle, tint = c.dangerInk, bg = c.danger.copy(alpha = 0.14f), size = 48.dp, glyph = 24.dp)
                    Spacer(Modifier.height(10.dp))
                    Text("No NCERT reference found", style = VTheme.type.h3.colored(c.ink))
                    Text(state.autoFillError ?: "", style = VTheme.type.caption.colored(c.ink3).copy(fontSize = 12.sp))
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
                TEyebrow("QUIZZES", dot = c.accent)
                Spacer(Modifier.height(8.dp))
            }
            items(state.quizzes, key = { it.id }) { q ->
                QuizRow(q, onPublish = { viewModel.publishQuiz(q.id) })
            }
        }
    }
}

@Composable
private fun AddUnitComposer(viewModel: TeacherSyllabusViewModel) {
    val c = VTheme.colors
    val state by viewModel.state.collectAsStateV2()
    val isChapter = state.addingUnderParentId.isNullOrBlank()
    TCard(padding = 16.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(if (isChapter) "New chapter" else "New topic", style = VTheme.type.bodyStrong.colored(c.navyDeep).copy(fontWeight = FontWeight.ExtraBold))
            VInput(value = state.addTitle, onValueChange = viewModel::setAddTitle, placeholder = if (isChapter) "Chapter title" else "Topic title")
            if (state.addError != null) Text(state.addError ?: "", style = VTheme.type.caption.colored(c.dangerInk).copy(fontSize = 12.sp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                VButton("Cancel", onClick = { viewModel.closeAdd() }, modifier = Modifier.weight(1f), variant = VButtonVariant.Ghost, size = VButtonSize.Md)
                VButton("Add", onClick = { viewModel.submitAdd() }, modifier = Modifier.weight(1f), tone = VButtonTone.Teal, size = VButtonSize.Md, loading = state.isAdding)
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
    val c = VTheme.colors
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
                    style = (if (u.isChapter) VTheme.type.bodyStrong else VTheme.type.body).colored(c.ink).copy(fontSize = if (u.isChapter) 14.5.sp else 13.5.sp, fontWeight = if (u.isChapter) FontWeight.ExtraBold else FontWeight.Medium),
                )
                if (isDraft) {
                    TPill("DRAFT", bg = c.accent.copy(alpha = 0.14f), fg = c.accentDeep)
                }
            }
            if (u.isCovered && !u.coveredOn.isNullOrBlank()) {
                Text("Covered ${prettyDateShort(u.coveredOn)}", style = VTheme.type.caption.colored(c.tealDeep).copy(fontSize = 10.5.sp))
            }
        }
        // Add topic button (edit mode, chapters only)
        if (editing && u.isChapter) {
            Box(
                Modifier.size(28.dp).clip(CircleShape).background(c.cream)
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onAddTopic() },
                contentAlignment = Alignment.Center,
            ) { Icon(VIcons.Plus, contentDescription = "Add topic", tint = c.ink2, modifier = Modifier.size(14.dp)) }
        }
        // Delete button (edit mode)
        if (editing) {
            val ixDel = remember { MutableInteractionSource() }
            Box(
                Modifier.size(28.dp).clip(CircleShape).background(c.danger.copy(alpha = 0.1f))
                    .clickable(interactionSource = ixDel, indication = null) { onDelete() },
                contentAlignment = Alignment.Center,
            ) { Icon(VIcons.Minus, contentDescription = "Delete", tint = c.dangerInk, modifier = Modifier.size(14.dp)) }
        }
    }
}

// ── Parse Syllabus bottom sheet ──────────────────────────────────────────────

@Composable
private fun ParseSyllabusSheet(viewModel: TeacherSyllabusViewModel) {
    val c = VTheme.colors
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
        TCard(
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
                    Text("Parse Syllabus", style = VTheme.type.h3.colored(c.navyDeep).copy(fontSize = 17.sp))
                    Spacer(Modifier.weight(1f))
                    Box(
                        Modifier.size(28.dp).clip(CircleShape).background(c.cream)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) { viewModel.closeParseSheet() },
                        contentAlignment = Alignment.Center,
                    ) { Icon(VIcons.Close, contentDescription = "Close", tint = c.ink2, modifier = Modifier.size(16.dp)) }
                }

                if (state.parsedUnits.isEmpty()) {
                    Text("Paste your syllabus text below. AI will extract chapters and topics.", style = VTheme.type.body.colored(c.ink2).copy(fontSize = 13.sp))
                    VInput(
                        value = state.parseRawText,
                        onValueChange = viewModel::setParseRawText,
                        placeholder = "e.g. Chapter 1: Number Systems\n1.1 Real Numbers\n1.2 Irrational Numbers...",
                        singleLine = false,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp, max = 200.dp),
                    )
                    if (state.parseError != null) {
                        Text(state.parseError ?: "", style = VTheme.type.caption.colored(c.dangerInk).copy(fontSize = 12.sp))
                    }
                    VButton(
                        "Parse with AI",
                        onClick = { viewModel.parseSyllabus() },
                        full = true,
                        tone = VButtonTone.Lavender,
                        loading = state.isParsing,
                        leading = { Icon(VIcons.Sparkles, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    )
                } else {
                    Text("Preview (${state.parsedUnits.size} units found)", style = VTheme.type.bodyStrong.colored(c.navyDeep).copy(fontWeight = FontWeight.ExtraBold, fontSize = 14.sp))
                    LazyColumn(
                        Modifier.fillMaxWidth().heightIn(max = 240.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        items(state.parsedUnits) { pu ->
                            val indent = (pu.depth * 16).dp
                            Row(Modifier.fillMaxWidth().padding(start = indent), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(if (pu.depth == 0) "•" else "–", style = VTheme.type.body.colored(c.ink3).copy(fontSize = 13.sp))
                                Text(pu.title, style = VTheme.type.body.colored(c.ink).copy(fontSize = 13.sp, fontWeight = if (pu.depth == 0) FontWeight.Bold else FontWeight.Normal))
                            }
                        }
                    }
                    if (state.parseError != null) {
                        Text(state.parseError ?: "", style = VTheme.type.caption.colored(c.dangerInk).copy(fontSize = 12.sp))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        VButton("Cancel", onClick = { viewModel.closeParseSheet() }, modifier = Modifier.weight(1f), variant = VButtonVariant.Ghost, size = VButtonSize.Md)
                        VButton("Confirm & Create", onClick = { viewModel.confirmParsedSyllabus() }, modifier = Modifier.weight(1f), tone = VButtonTone.Lavender, size = VButtonSize.Md, loading = state.isParsing)
                    }
                }
            }
        }
    }
}

// ── Daily Log popup ──────────────────────────────────────────────────────────

@Composable
private fun DailyLogPopup(viewModel: TeacherSyllabusViewModel) {
    val c = VTheme.colors
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
        TCard(
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
                        Text("Daily Class Log", style = VTheme.type.h3.colored(c.navyDeep).copy(fontSize = 17.sp))
                        Text("${state.dailyLogClassName} · ${state.dailyLogSubject}", style = VTheme.type.caption.colored(c.ink2).copy(fontSize = 12.sp))
                    }
                    Spacer(Modifier.weight(1f))
                    Box(
                        Modifier.size(28.dp).clip(CircleShape).background(c.cream)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) { viewModel.closeDailyLogPopup() },
                        contentAlignment = Alignment.Center,
                    ) { Icon(VIcons.Close, contentDescription = "Close", tint = c.ink2, modifier = Modifier.size(16.dp)) }
                }

                val selectedCount = state.dailyLogSelectedTopicIds.size
                Text(
                    if (selectedCount == 0) "Select topics covered today" else "$selectedCount topic${if (selectedCount > 1) "s" else ""} selected",
                    style = VTheme.type.bodyStrong.colored(c.ink2).copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
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
                Text("Coverage: ${state.dailyLogCoveragePct}%", style = VTheme.type.bodyStrong.colored(c.ink2).copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    val ixMinus = remember { MutableInteractionSource() }
                    val ixPlus = remember { MutableInteractionSource() }
                    Box(
                        Modifier.size(32.dp).clip(CircleShape).background(c.cream)
                            .clickable(interactionSource = ixMinus, indication = null) { viewModel.setDailyLogCoveragePct(state.dailyLogCoveragePct - 10) },
                        contentAlignment = Alignment.Center,
                    ) { Icon(VIcons.Minus, contentDescription = null, tint = c.ink2, modifier = Modifier.size(16.dp)) }
                    Text("${state.dailyLogCoveragePct}%", style = VTheme.type.bodyStrong.colored(c.navyDeep).copy(fontSize = 16.sp, fontWeight = FontWeight.ExtraBold))
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
                    placeholder = "What was taught today? (optional)",
                    singleLine = false,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 60.dp, max = 100.dp),
                )

                if (state.dailyLogError != null) {
                    Text(state.dailyLogError ?: "", style = VTheme.type.caption.colored(c.dangerInk).copy(fontSize = 12.sp))
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    VButton("Skip", onClick = { viewModel.dismissDailyLogPopup() }, modifier = Modifier.weight(1f), variant = VButtonVariant.Ghost, size = VButtonSize.Md)
                    VButton("Save Log", onClick = { viewModel.saveDailyLog() }, modifier = Modifier.weight(1f), tone = VButtonTone.Teal, size = VButtonSize.Md, loading = state.isSavingDailyLog)
                }
            }
        }
    }
}

@Composable
private fun DailyLogChapterRow(title: String, expanded: Boolean, onToggle: () -> Unit) {
    val c = VTheme.colors
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
        Text(title, style = VTheme.type.bodyStrong.colored(c.ink).copy(fontSize = 13.sp, fontWeight = FontWeight.Bold))
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
    val c = VTheme.colors
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
            style = VTheme.type.body.colored(c.ink).copy(fontSize = 12.sp),
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
    val c = VTheme.colors
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
        Text(title, style = VTheme.type.body.colored(c.ink2).copy(fontSize = 11.sp))
    }
}

// ── Quiz generation sheet ────────────────────────────────────────────────────

@Composable
private fun QuizSheet(viewModel: TeacherSyllabusViewModel) {
    val c = VTheme.colors
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
        TCard(
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
                    Text("Generate Quiz", style = VTheme.type.h3.colored(c.navyDeep).copy(fontSize = 17.sp))
                    Spacer(Modifier.weight(1f))
                    Box(
                        Modifier.size(28.dp).clip(CircleShape).background(c.cream)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) { viewModel.closeQuizSheet() },
                        contentAlignment = Alignment.Center,
                    ) { Icon(VIcons.Close, contentDescription = "Close", tint = c.ink2, modifier = Modifier.size(16.dp)) }
                }

                // ── Unit selection (multiple) ──────────────────────────────
                Text("Select units", style = VTheme.type.bodyStrong.colored(c.ink2).copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold))
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
                                style = VTheme.type.body.colored(if (isSelected) c.navyDeep else c.ink2).copy(fontSize = 12.sp),
                                maxLines = 1,
                            )
                        }
                    }
                }

                // ── Question types ─────────────────────────────────────────
                Text("Question types", style = VTheme.type.bodyStrong.colored(c.ink2).copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("MCQ" to "MCQ", "FILL_BLANK" to "Fill-ups", "TRUE_FALSE" to "True/False", "MATCH" to "Match").forEach { (type, label) ->
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
                                style = VTheme.type.body.colored(if (selected) c.accentDeep else c.ink2).copy(fontSize = 11.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal),
                            )
                        }
                    }
                }

                // ── Number of questions ────────────────────────────────────
                Text("Number of questions: ${state.quizNumQuestions}", style = VTheme.type.bodyStrong.colored(c.ink2).copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    val ixMinus = remember { MutableInteractionSource() }
                    val ixPlus = remember { MutableInteractionSource() }
                    Box(
                        Modifier.size(32.dp).clip(CircleShape).background(c.cream)
                            .clickable(interactionSource = ixMinus, indication = null) { viewModel.setQuizNumQuestions(state.quizNumQuestions - 1) },
                        contentAlignment = Alignment.Center,
                    ) { Icon(VIcons.Minus, contentDescription = null, tint = c.ink2, modifier = Modifier.size(16.dp)) }
                    Text("${state.quizNumQuestions}", style = VTheme.type.bodyStrong.colored(c.navyDeep).copy(fontSize = 16.sp, fontWeight = FontWeight.ExtraBold))
                    Box(
                        Modifier.size(32.dp).clip(CircleShape).background(c.cream)
                            .clickable(interactionSource = ixPlus, indication = null) { viewModel.setQuizNumQuestions(state.quizNumQuestions + 1) },
                        contentAlignment = Alignment.Center,
                    ) { Icon(VIcons.Plus, contentDescription = null, tint = c.ink2, modifier = Modifier.size(16.dp)) }
                }

                // ── Difficulty ─────────────────────────────────────────────
                Text("Difficulty", style = VTheme.type.bodyStrong.colored(c.ink2).copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("EASY", "MEDIUM", "HARD").forEach { diff ->
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
                                diff.lowercase().replaceFirstChar { it.uppercase() },
                                style = VTheme.type.body.colored(if (selected) c.accentDeep else c.ink2).copy(fontSize = 13.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal),
                            )
                        }
                    }
                }

                if (state.quizError != null) {
                    Text(state.quizError ?: "", style = VTheme.type.caption.colored(c.dangerInk).copy(fontSize = 12.sp))
                }

                VButton(
                    "Generate Quiz",
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
private fun QuizRow(q: QuizDto, onPublish: () -> Unit) {
    val c = VTheme.colors
    TCard(padding = 14.dp) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            TIconDisc(VIcons.GraduationCap, tint = c.accentDeep, bg = c.accent.copy(alpha = 0.14f), size = 36.dp, glyph = 18.dp)
            Column(Modifier.weight(1f)) {
                Text(q.title.ifBlank { "Quiz" }, style = VTheme.type.bodyStrong.colored(c.ink).copy(fontSize = 14.sp, fontWeight = FontWeight.Bold))
                Text("${q.questions.size} questions · ${q.status}", style = VTheme.type.caption.colored(c.ink2).copy(fontSize = 11.sp))
            }
            if (q.status == "DRAFT") {
                VButton("Publish", onClick = onPublish, size = VButtonSize.Sm, tone = VButtonTone.Lavender, variant = VButtonVariant.Secondary)
            } else {
                TPill("Published", bg = c.teal.copy(alpha = 0.14f), fg = c.tealDeep)
            }
        }
    }
}

// ── Empty state with 3 clear options ─────────────────────────────────────────

@Composable
private fun EmptyStateOptions(viewModel: TeacherSyllabusViewModel) {
    val c = VTheme.colors
    val state by viewModel.state.collectAsStateV2()
    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
        Text("No units yet", style = VTheme.type.h3.colored(c.ink).copy(fontSize = 16.sp))
        Text("Choose how to build your syllabus:", style = VTheme.type.caption.colored(c.ink3).copy(fontSize = 12.sp))
        Spacer(Modifier.height(4.dp))
        // Option 1: Auto-fill from NCERT
        TCard(padding = 16.dp, onClick = { viewModel.autoFill() }) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TIconDisc(VIcons.Sparkles, tint = c.accentDeep, bg = c.accent.copy(alpha = 0.14f), size = 40.dp, glyph = 20.dp)
                Column(Modifier.weight(1f)) {
                    Text("Auto-fill from NCERT", style = VTheme.type.bodyStrong.colored(c.ink).copy(fontSize = 14.sp, fontWeight = FontWeight.ExtraBold))
                    Text("Fetch the standard CBSE/NCERT syllabus for this class & subject", style = VTheme.type.caption.colored(c.ink3).copy(fontSize = 11.sp))
                }
            }
        }
        // Option 2: Paste text for AI parse
        TCard(padding = 16.dp, onClick = { viewModel.openParseSheet() }) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TIconDisc(VIcons.ClipboardList, tint = c.tealDeep, bg = c.teal.copy(alpha = 0.14f), size = 40.dp, glyph = 20.dp)
                Column(Modifier.weight(1f)) {
                    Text("Paste syllabus text", style = VTheme.type.bodyStrong.colored(c.ink).copy(fontSize = 14.sp, fontWeight = FontWeight.ExtraBold))
                    Text("AI will extract chapters and topics from pasted text", style = VTheme.type.caption.colored(c.ink3).copy(fontSize = 11.sp))
                }
            }
        }
        // Option 3: Add manually
        TCard(padding = 16.dp, onClick = { viewModel.toggleEditing(); viewModel.openAdd(null) }) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TIconDisc(VIcons.Plus, tint = c.ink2, bg = c.cream, size = 40.dp, glyph = 20.dp)
                Column(Modifier.weight(1f)) {
                    Text("Add manually", style = VTheme.type.bodyStrong.colored(c.ink).copy(fontSize = 14.sp, fontWeight = FontWeight.ExtraBold))
                    Text("Create chapters and topics one by one", style = VTheme.type.caption.colored(c.ink3).copy(fontSize = 11.sp))
                }
            }
        }
        if (state.isAutoFilling) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                TeacherSpinner(16.dp)
                Text("Fetching NCERT reference…", style = VTheme.type.caption.colored(c.ink2).copy(fontSize = 12.sp))
            }
        }
    }
}

// ── Pace warning banner ──────────────────────────────────────────────────────

@Composable
private fun PaceWarningBanner(warning: com.littlebridge.enrollplus.feature.teacher.domain.model.SylPaceWarning) {
    val c = VTheme.colors
    val isBehind = warning.level == "BEHIND" || warning.level == "CRITICAL"
    val bg = if (isBehind) c.danger.copy(alpha = 0.08f) else c.accent.copy(alpha = 0.08f)
    val fg = if (isBehind) c.dangerInk else c.accentDeep
    val icon = if (isBehind) VIcons.AlertCircle else VIcons.Sparkles
    val label = when (warning.level) {
        "CRITICAL" -> "Critically behind"
        "BEHIND" -> "Behind schedule"
        "AHEAD" -> "Ahead of schedule"
        else -> "Pace update"
    }
    TCard(padding = 14.dp) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(Modifier.size(32.dp).clip(CircleShape).background(bg), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = fg, modifier = Modifier.size(16.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(label, style = VTheme.type.bodyStrong.colored(fg).copy(fontSize = 13.sp, fontWeight = FontWeight.ExtraBold))
                Text(
                    "Expected ${warning.expectedPct}% · Actual ${warning.actualPct}% · Δ ${warning.deviationPct}%",
                    style = VTheme.type.caption.colored(c.ink2).copy(fontSize = 11.sp),
                )
                if (warning.message.isNotBlank()) {
                    Text(warning.message, style = VTheme.type.caption.colored(c.ink3).copy(fontSize = 11.sp))
                }
                Spacer(Modifier.height(4.dp))
                val metricsText = buildString {
                    if (warning.classesElapsed > 0) {
                        append("${warning.classesElapsed} classes done")
                        if (warning.classesRemaining > 0) append(" · ${warning.classesRemaining} left")
                    }
                    if (warning.weeklyPeriods > 0) {
                        append(" · ${warning.weeklyPeriods}/week")
                    }
                    if (warning.holidayDaysCounted > 0) {
                        append(" · ${warning.holidayDaysCounted} holidays")
                    }
                }
                if (metricsText.isNotBlank()) {
                    Text(metricsText, style = VTheme.type.caption.colored(c.ink3).copy(fontSize = 10.sp))
                }
                if (warning.estimatedCompletionDate.isNotBlank()) {
                    Text("Est. completion: ${warning.estimatedCompletionDate}", style = VTheme.type.caption.colored(c.ink3).copy(fontSize = 10.sp))
                }
                if (warning.avgCoveragePerClass > 0) {
                    Text("Avg ${"%.1f".format(warning.avgCoveragePerClass)}%/class", style = VTheme.type.caption.colored(c.ink3).copy(fontSize = 10.sp))
                }
            }
        }
    }
}

// ── Draft approval bar ───────────────────────────────────────────────────────

@Composable
private fun DraftApprovalBar(viewModel: TeacherSyllabusViewModel) {
    val c = VTheme.colors
    val state by viewModel.state.collectAsStateV2()
    val draftCount = state.draftUnits.size
    TCard(padding = 14.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.size(28.dp).clip(CircleShape).background(c.accent.copy(alpha = 0.14f)), contentAlignment = Alignment.Center) {
                    Icon(VIcons.ShieldCheck, contentDescription = null, tint = c.accentDeep, modifier = Modifier.size(14.dp))
                }
                Text("$draftCount draft unit${if (draftCount != 1) "s" else ""} pending approval", style = VTheme.type.bodyStrong.colored(c.accentDeep).copy(fontSize = 13.sp, fontWeight = FontWeight.ExtraBold))
            }
            Text("Draft units are not visible to parents until approved.", style = VTheme.type.caption.colored(c.ink3).copy(fontSize = 11.sp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                VButton("Reject All", onClick = { viewModel.rejectAllDrafts() }, modifier = Modifier.weight(1f), variant = VButtonVariant.Ghost, size = VButtonSize.Sm, loading = state.isApproving)
                VButton("Approve All", onClick = { viewModel.approveAllDrafts() }, modifier = Modifier.weight(1f), tone = VButtonTone.Lavender, size = VButtonSize.Sm, loading = state.isApproving)
            }
            if (state.approveError != null) {
                Text(state.approveError ?: "", style = VTheme.type.caption.colored(c.dangerInk).copy(fontSize = 11.sp))
            }
        }
    }
}

// ── Auto-fill preview sheet ──────────────────────────────────────────────────

@Composable
private fun AutoFillPreviewSheet(viewModel: TeacherSyllabusViewModel) {
    val c = VTheme.colors
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
        TCard(
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
                        Text("NCERT Auto-fill", style = VTheme.type.h3.colored(c.navyDeep).copy(fontSize = 17.sp))
                        if (state.autoFillSource.isNotBlank()) {
                            Text(state.autoFillSource, style = VTheme.type.caption.colored(c.ink2).copy(fontSize = 12.sp))
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
                    ) { Icon(VIcons.Close, contentDescription = "Close", tint = c.ink2, modifier = Modifier.size(16.dp)) }
                }

                val totalChapters = state.autoFillChapters.size
                val totalTopics = state.autoFillChapters.sumOf { ch -> ch.topics.size }
                val totalSubtopics = state.autoFillChapters.sumOf { ch -> ch.topics.sumOf { t -> t.subtopics.size } }
                val totalUnits = totalChapters + totalTopics + totalSubtopics
                val subtopicText = if (totalSubtopics > 0) ", $totalSubtopics subtopics" else ""
                Text(
                    "Preview: $totalChapters chapters, $totalTopics topics$subtopicText — $totalUnits units will be created as DRAFT for your review.",
                    style = VTheme.type.body.colored(c.ink2).copy(fontSize = 13.sp),
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
                    Text(state.autoFillError ?: "", style = VTheme.type.caption.colored(c.dangerInk).copy(fontSize = 12.sp))
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    VButton("Cancel", onClick = { viewModel.dismissAutoFillPreview() }, modifier = Modifier.weight(1f), variant = VButtonVariant.Ghost, size = VButtonSize.Md)
                    VButton("Create as Draft", onClick = { viewModel.confirmAutoFill() }, modifier = Modifier.weight(1f), tone = VButtonTone.Lavender, size = VButtonSize.Md, loading = state.isAutoFilling)
                }
            }
        }
    }
}

@Composable
private fun AutoFillChapterRow(ch: SylAutoFillChapter) {
    val c = VTheme.colors
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(c.cream).padding(12.dp)) {
        Text(ch.title, style = VTheme.type.bodyStrong.colored(c.ink).copy(fontSize = 14.sp, fontWeight = FontWeight.ExtraBold))
        if (ch.topics.isNotEmpty()) {
            Spacer(Modifier.height(6.dp))
            ch.topics.forEach { t ->
                Row(Modifier.fillMaxWidth().padding(start = 12.dp, top = 2.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("–", style = VTheme.type.body.colored(c.ink3).copy(fontSize = 12.sp))
                    Text(t.title, style = VTheme.type.body.colored(c.ink2).copy(fontSize = 12.sp))
                }
                if (t.subtopics.isNotEmpty()) {
                    t.subtopics.forEach { st ->
                        Row(Modifier.fillMaxWidth().padding(start = 28.dp, top = 1.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("·", style = VTheme.type.body.colored(c.ink3).copy(fontSize = 11.sp))
                            Text(st.title, style = VTheme.type.body.colored(c.ink3).copy(fontSize = 11.sp))
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
    val c = VTheme.colors
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
        TCard(
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
                    Text("Quiz Preview", style = VTheme.type.h3.colored(c.navyDeep).copy(fontSize = 17.sp))
                    Spacer(Modifier.weight(1f))
                    Box(
                        Modifier.size(28.dp).clip(CircleShape).background(c.cream)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) { viewModel.closeQuizPreview() },
                        contentAlignment = Alignment.Center,
                    ) { Icon(VIcons.Close, contentDescription = "Close", tint = c.ink2, modifier = Modifier.size(16.dp)) }
                }

                Text(quiz.title, style = VTheme.type.bodyStrong.colored(c.ink2).copy(fontSize = 13.sp))
                Text("${quiz.questions.size} questions · ${quiz.status}", style = VTheme.type.caption.colored(c.ink3).copy(fontSize = 11.sp))

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

                if (state.quizPreviewError != null) {
                    Text(state.quizPreviewError ?: "", style = VTheme.type.caption.colored(c.dangerInk).copy(fontSize = 12.sp))
                }

                // Action buttons
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    VButton(
                        "Regenerate All",
                        onClick = { viewModel.regenerateQuizQuestions() },
                        modifier = Modifier.weight(1f),
                        variant = VButtonVariant.Secondary,
                        tone = VButtonTone.Teal,
                        size = VButtonSize.Md,
                        loading = state.isRegenerating,
                        leading = { Icon(VIcons.Sparkles, contentDescription = null, modifier = Modifier.size(14.dp)) },
                    )
                    VButton(
                        "Publish Quiz",
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
    val c = VTheme.colors
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
            Text("Q$index", style = VTheme.type.bodyStrong.colored(c.accentDeep).copy(fontSize = 13.sp, fontWeight = FontWeight.Bold))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(question.question, style = VTheme.type.body.colored(c.ink).copy(fontSize = 13.sp))
                if (question.options.isNotEmpty()) {
                    question.options.forEachIndexed { i, opt ->
                        val isCorrect = opt.startsWith(question.correctAnswer, ignoreCase = true) ||
                            question.correctIndex == i
                        Text(
                            opt,
                            style = VTheme.type.body.colored(if (isCorrect) c.tealDeep else c.ink2).copy(
                                fontSize = 12.sp,
                                fontWeight = if (isCorrect) FontWeight.Bold else FontWeight.Normal,
                            ),
                        )
                    }
                } else if (question.correctAnswer.isNotBlank()) {
                    Text("Answer: ${question.correctAnswer}", style = VTheme.type.body.colored(c.tealDeep).copy(fontSize = 12.sp, fontWeight = FontWeight.Bold))
                }
                if (!question.explanation.isNullOrBlank()) {
                    Text("Explanation: ${question.explanation}", style = VTheme.type.caption.colored(c.ink3).copy(fontSize = 11.sp))
                }
                Text(question.questionType, style = VTheme.type.caption.colored(c.ink3).copy(fontSize = 10.sp))
            }
            Box(
                Modifier.size(26.dp).clip(CircleShape).background(c.accent.copy(alpha = 0.1f))
                    .clickable(interactionSource = ixEdit, indication = null) { onEdit() },
                contentAlignment = Alignment.Center,
            ) { Icon(VIcons.Edit3, contentDescription = "Edit", tint = c.accentDeep, modifier = Modifier.size(13.dp)) }
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
    val c = VTheme.colors
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
        Text("Editing Question", style = VTheme.type.bodyStrong.colored(c.accentDeep).copy(fontSize = 13.sp, fontWeight = FontWeight.Bold))

        VInput(
            value = questionText,
            onValueChange = { questionText = it },
            placeholder = "Question text",
            singleLine = false,
            modifier = Modifier.fillMaxWidth().heightIn(min = 60.dp, max = 120.dp),
        )

        if (questionType == "MCQ") {
            Text("Options (one per line):", style = VTheme.type.caption.colored(c.ink2).copy(fontSize = 11.sp))
            VInput(
                value = optionsText,
                onValueChange = { optionsText = it },
                placeholder = "A) ...\nB) ...\nC) ...\nD) ...",
                singleLine = false,
                modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp, max = 140.dp),
            )
        }

        VInput(
            value = correctAnswer,
            onValueChange = { correctAnswer = it },
            placeholder = "Correct answer (e.g. A, B, true, false, or text)",
        )

        VInput(
            value = explanation,
            onValueChange = { explanation = it },
            placeholder = "Explanation (optional)",
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
                        style = VTheme.type.body.colored(if (selected) c.accentDeep else c.ink2).copy(fontSize = 10.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal),
                    )
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            VButton("Cancel", onClick = onCancel, modifier = Modifier.weight(1f), variant = VButtonVariant.Ghost, size = VButtonSize.Md)
            VButton(
                "Save",
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
