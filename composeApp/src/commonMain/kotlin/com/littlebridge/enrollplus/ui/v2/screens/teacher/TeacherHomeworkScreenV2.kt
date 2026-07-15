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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.feature.teacher.domain.model.HomeworkSubmissionStatus
import com.littlebridge.enrollplus.feature.teacher.presentation.HomeworkBoardRow
import com.littlebridge.enrollplus.feature.teacher.presentation.HomeworkMode
import com.littlebridge.enrollplus.feature.teacher.presentation.HomeworkSummary
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherHomeworkViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonSize
import com.littlebridge.enrollplus.ui.v2.components.VButtonTone
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VDatePicker
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VInput
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.platform.rememberShareHelper
import org.koin.compose.viewmodel.koinViewModel
import com.littlebridge.enrollplus.ui.v2.screens.teacher.TeacherKit.TeacherSpinner

/**
 * TeacherHomeworkScreenV2 — the scoped homework lifecycle (Doc 08 §6–§8), rebuilt on the cream
 * token base (TEACHER_PORTAL_REDESIGN.md §4-§7). Reached PRE-SCOPED with a pre-authorized
 * [assignmentId]. Two faces:
 *   • LIST  — this class's active homework (each with a live turned-in ratio) + an assign composer
 *             (title / description / due date(+time) / allow-late).
 *   • BOARD — one homework's roster-joined submissions board (every enrolled student, incl. NOT
 *             SUBMITTED), grant extension (whole-class or one student), per-row review, and close.
 */
@Composable
fun TeacherHomeworkScreenV2(
    assignmentId: String,
    scopeLabel: String,
    modifier: Modifier = Modifier,
    tool: UpdateTool = UpdateTool.Homework,
    onToolChange: (UpdateTool) -> Unit = {},
    onChangeClass: () -> Unit = {},
    viewModel: TeacherHomeworkViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()

    LaunchedEffect(assignmentId) {
        if (assignmentId.isNotBlank() && state.assignmentId != assignmentId) viewModel.load(assignmentId)
    }

    Box(modifier.fillMaxSize().background(VColors.cream)) {
        when (state.mode) {
            HomeworkMode.List -> HomeworkListMode(viewModel, scopeLabel, tool, onToolChange, onChangeClass)
            HomeworkMode.Board -> HomeworkBoardMode(viewModel, scopeLabel, tool, onToolChange, onChangeClass)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// LIST mode
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun HomeworkListMode(
    viewModel: TeacherHomeworkViewModel,
    scopeLabel: String,
    tool: UpdateTool,
    onToolChange: (UpdateTool) -> Unit,
    onChangeClass: () -> Unit,
) {
    val state by viewModel.state.collectAsStateV2()

    LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = TeacherDockClearance),
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
                    VButton(
                        text = if (state.isComposerOpen) appString(StringKeys.COMMON_BUTTON_CLOSE) else appString(StringKeys.TC_ASSIGN_HOMEWORK),
                        onClick = { if (state.isComposerOpen) viewModel.closeComposer() else viewModel.openComposer() },
                        full = true,
                        variant = if (state.isComposerOpen) VButtonVariant.Ghost else VButtonVariant.Primary,
                        tone = VButtonTone.Mint,
                        leading = { Icon(if (state.isComposerOpen) VIcons.Close else VIcons.Plus, contentDescription = null, modifier = Modifier.size(15.dp)) },
                    )
                }
            }
        }

        if (state.isComposerOpen) item { HomeworkComposer(viewModel) }

        when {
            state.isLoading && state.items.isEmpty() -> item { Box(Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) { TeacherSpinner() } }
            state.error != null && state.items.isEmpty() -> item {
                VtCard { Column {
                    Text(appString(StringKeys.TC_COULDNT_LOAD_HOMEWORK), style = VTypography.caption, color = VColors.ink)
                    Spacer(Modifier.height(8.dp))
                    VButton(appString(StringKeys.COMMON_BUTTON_RETRY), onClick = { viewModel.retry() }, tone = VButtonTone.Mint, size = VButtonSize.Sm)
                } }
            }
            state.items.isEmpty() -> item {
                VtEmptyCard(
                    title = appString(StringKeys.TC_NO_ACTIVE_HOMEWORK),
                    subtext = appString(StringKeys.TC_ASSIGN_FIRST_HOMEWORK),
                    icon = VIcons.FileText,
                    tint = VColors.mint,
                )
            }
            else -> items(state.items, key = { it.id }) { hw -> HomeworkRow(hw) { viewModel.openBoard(hw.id) } }
        }
    }
}

@Composable
private fun HomeworkComposer(viewModel: TeacherHomeworkViewModel) {
    val state by viewModel.state.collectAsStateV2()
    VtCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(appString(StringKeys.TC_NEW_HOMEWORK), style = VTypography.caption, color = VColors.ink)
            VInput(value = state.composerTitle, onValueChange = viewModel::setComposerTitle, label = appString(StringKeys.TC_TITLE), placeholder = appString(StringKeys.TC_TITLE_PH))
            VInput(value = state.composerDescription, onValueChange = viewModel::setComposerDescription, label = appString(StringKeys.TC_DETAILS_OPTIONAL), placeholder = appString(StringKeys.TC_INSTRUCTIONS_PH), singleLine = false)
            VDatePicker(value = state.composerDueDate, onValueChange = viewModel::setComposerDueDate, label = appString(StringKeys.TC_DUE_DATE))
            // Allow-late toggle
            val late = state.composerAllowLate
            Row(
                Modifier.fillMaxWidth().clip(VShapes.md).background(VColors.creamDeep).border(1.dp, VColors.line, VShapes.md)
                    .clickable { viewModel.setComposerAllowLate(!late) }.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier.size(24.dp).clip(VShapes.sm).background(if (late) VColors.mint else VColors.surfaceCard).border(1.dp, if (late) VColors.mint else VColors.line, VShapes.sm),
                    contentAlignment = Alignment.Center,
                ) { if (late) Icon(VIcons.Check, contentDescription = null, tint = VColors.white, modifier = Modifier.size(16.dp)) }
                Spacer(Modifier.width(12.dp))
                Text(appString(StringKeys.TC_ALLOW_LATE), style = VTypography.caption, color = VColors.ink)
            }
            if (state.composerError != null) Text(state.composerError ?: "", style = VTypography.caption, color = VColors.coral)
            VButton(appString(StringKeys.TC_ASSIGN_HOMEWORK), onClick = { viewModel.assign() }, full = true, tone = VButtonTone.Mint, loading = state.isAssigning, enabled = state.canAssign)
        }
    }
}

@Composable
private fun HomeworkRow(hw: HomeworkSummary, onOpen: () -> Unit) {
    val pct = (hw.turnedInRatio * 100).toInt()
    VtCard(onClick = onOpen) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            VtIconDisc(VIcons.FileText, tint = VColors.mint, bg = VColors.mint.copy(alpha = 0.12f), size = 44.dp, glyph = 22.dp)
            Column(Modifier.weight(1f)) {
                Text(hw.title, style = VTypography.caption, color = VColors.ink, maxLines = 1)
                Spacer(Modifier.height(6.dp))
                androidx.compose.foundation.Canvas(Modifier.fillMaxWidth().height(8.dp)) {
                    drawRoundRect(color = VColors.creamDeep, size = size, cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx()))
                    val w = size.width * hw.turnedInRatio.coerceIn(0f, 1f)
                    drawRoundRect(color = VColors.mint, size = size.copy(width = w), cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx()))
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    appString(StringKeys.TC_DUE_PAST_TURNED_IN, "date" to prettyDateShort(hw.dueDate), "pastDue" to if (hw.isPastDue) " · ${appString(StringKeys.TC_PAST_DUE)}" else "", "turnedIn" to hw.turnedInCount.toString(), "total" to hw.totalCount.toString()),
                    style = VTypography.caption,
                    color = if (hw.isPastDue) VColors.gold else VColors.ink3,
                )
            }
            Text("$pct%", style = VTypography.label, color = VColors.mint)
            Icon(VIcons.ChevronRight, contentDescription = null, tint = VColors.ink3, modifier = Modifier.size(20.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// BOARD mode
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun HomeworkBoardMode(
    viewModel: TeacherHomeworkViewModel,
    scopeLabel: String,
    tool: UpdateTool,
    onToolChange: (UpdateTool) -> Unit,
    onChangeClass: () -> Unit,
) {
    val state by viewModel.state.collectAsStateV2()
    val board = state.board
    var closeConfirm by remember { mutableStateOf(false) }

    LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = TeacherDockClearance),
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(32.dp).clip(VShapes.full).background(VColors.creamDeep)
                                .clickable { viewModel.closeBoard() },
                            contentAlignment = Alignment.Center,
                        ) { Icon(VIcons.ArrowLeft, contentDescription = appString(StringKeys.COMMON_BUTTON_BACK), tint = VColors.ink, modifier = Modifier.size(16.dp)) }
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(board?.title ?: appString(StringKeys.TC_SUBMISSIONS), style = VTypography.caption, color = VColors.ink, maxLines = 1)
                            Text(appString(StringKeys.TC_DUE_LABEL, "date" to prettyDateShort(board?.dueDate)), style = VTypography.caption, color = VColors.ink3)
                        }
                    }
                    if (board != null) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            VtCompactMetric(board.submittedCount.toString(), appString(StringKeys.TC_SUBMITTED), VColors.success, Modifier.weight(1f))
                            VtCompactMetric(board.lateCount.toString(), appString(StringKeys.ATT_LATE), VColors.gold, Modifier.weight(1f))
                            VtCompactMetric(board.gradedCount.toString(), appString(StringKeys.TC_GRADED), VColors.violet, Modifier.weight(1f))
                            VtCompactMetric(board.notSubmittedCount.toString(), appString(StringKeys.TC_PENDING), VColors.coral, Modifier.weight(1f))
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            VButton(appString(StringKeys.TC_EXTEND_FOR_CLASS), onClick = { viewModel.openExtension(null) }, modifier = Modifier.weight(1f), variant = VButtonVariant.Secondary, tone = VButtonTone.Sky, size = VButtonSize.Md, leading = { Icon(VIcons.Clock, contentDescription = null, modifier = Modifier.size(15.dp)) })
                            VButton(appString(StringKeys.COMMON_BUTTON_CLOSE), onClick = { closeConfirm = true }, modifier = Modifier.weight(1f), variant = VButtonVariant.Ghost, size = VButtonSize.Md)
                        }
                    }
                }
            }
        }

        when {
            state.isBoardLoading && board == null -> item { Box(Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) { TeacherSpinner() } }
            board == null -> item { VtCard { Text(appString(StringKeys.TC_COULDNT_LOAD_BOARD), style = VTypography.caption, color = VColors.ink2) } }
            else -> items(board.rows, key = { it.studentId }) { row ->
                BoardStudentRow(row, updating = state.updatingStudentId == row.studentId, onReview = { st -> viewModel.reviewSubmission(row.studentId, st) }, onExtend = { viewModel.openExtension(row.studentId, row.name) })
            }
        }
    }

    if (state.isExtensionOpen) ExtensionSheet(viewModel)
    if (closeConfirm) {
        TeacherConfirmSheet(
            title = appString(StringKeys.TC_CLOSE_HOMEWORK_Q),
            body = appString(StringKeys.TC_CLOSE_HOMEWORK_DESC),
            confirmLabel = appString(StringKeys.TC_CLOSE_IT),
            destructive = true,
            onConfirm = { closeConfirm = false; viewModel.closeHomework() },
            onDismiss = { closeConfirm = false },
        )
    }
}

@Composable
private fun BoardStudentRow(row: HomeworkBoardRow, updating: Boolean, onReview: (String) -> Unit, onExtend: () -> Unit) {
    val (tint, label) = when (row.status) {
        HomeworkSubmissionStatus.SUBMITTED -> VColors.success to appString(StringKeys.TC_SUBMITTED)
        HomeworkSubmissionStatus.LATE -> VColors.gold to appString(StringKeys.ATT_LATE)
        HomeworkSubmissionStatus.GRADED -> VColors.violet to appString(StringKeys.TC_GRADED)
        else -> VColors.coral to appString(StringKeys.TC_NOT_SUBMITTED)
    }
    VtCard {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(Modifier.weight(1f)) {
                    Text(row.name, style = VTypography.caption, color = VColors.ink, maxLines = 1)
                    Text(
                        buildString {
                            append(if (row.rollNo != null) appString(StringKeys.TC_ROLL_NO, "no" to row.rollNo.toString()) else row.studentCode)
                            if (row.hasExtension && !row.extendedTo.isNullOrBlank()) append(" · ${appString(StringKeys.TC_EXTENDED_TO, "date" to prettyDateShort(row.extendedTo))}")
                        },
                        style = VTypography.caption,
                        color = VColors.ink3,
                    )
                }
                if (updating) TeacherSpinner(16.dp) else VtPill(label.uppercase(), bg = tint.copy(alpha = 0.14f), fg = tint)
            }
        // Review actions only when there's something turned in.
        if (row.status == HomeworkSubmissionStatus.SUBMITTED || row.status == HomeworkSubmissionStatus.LATE) {
            Spacer(Modifier.height(8.dp))

            // Parent-written answer / notes.
            if (row.submissionText.isNotBlank()) {
                Column(
                    Modifier.fillMaxWidth().clip(VShapes.sm).background(VColors.creamDeep).padding(10.dp),
                ) {
                    Text("Answer / Notes", style = VTypography.caption, color = VColors.ink)
                    Spacer(Modifier.height(4.dp))
                    Text(row.submissionText, style = VTypography.body, color = VColors.ink2)
                }
                Spacer(Modifier.height(8.dp))
            }

            // Photo attachments.
            if (row.attachments.isNotEmpty()) {
                Text("Attachments", style = VTypography.caption, color = VColors.ink)
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    val shareHelper = rememberShareHelper()
                    row.attachments.forEach { att ->
                        AttachmentChip(att.filename.ifBlank { "Attachment" }, onClick = { shareHelper.shareText(att.url, "Homework attachment") })
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                VButton(appString(StringKeys.TC_MARK_GRADED), onClick = { onReview(HomeworkSubmissionStatus.GRADED) }, modifier = Modifier.weight(1f), variant = VButtonVariant.Secondary, tone = VButtonTone.Mint, size = VButtonSize.Sm)
                VButton(appString(StringKeys.TC_EXTEND), onClick = onExtend, modifier = Modifier.weight(1f), variant = VButtonVariant.Ghost, size = VButtonSize.Sm)
            }
        } else if (row.status == HomeworkSubmissionStatus.NOT_SUBMITTED) {
            Spacer(Modifier.height(8.dp))
            VButton(appString(StringKeys.TC_GRANT_EXTENSION), onClick = onExtend, full = true, variant = VButtonVariant.Ghost, size = VButtonSize.Sm, leading = { Icon(VIcons.Clock, contentDescription = null, modifier = Modifier.size(14.dp)) })
        }
    }
}
}

@Composable
private fun AttachmentChip(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.clip(VShapes.sm).background(VColors.surfaceCard).border(1.dp, VColors.line, VShapes.sm).clickable { onClick() }.padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(VIcons.Upload, contentDescription = null, tint = VColors.ink2, modifier = Modifier.size(14.dp))
        Text(label, style = VTypography.caption, color = VColors.ink, maxLines = 1)
    }
}

@Composable
private fun ExtensionSheet(viewModel: TeacherHomeworkViewModel) {
    val state by viewModel.state.collectAsStateV2()
    Box(
        Modifier.fillMaxSize().background(VColors.ink.copy(alpha = 0.42f))
            .clickable { viewModel.closeExtension() },
        contentAlignment = Alignment.Center,
    ) {
        // Block tap propagation to scrim.
        Box(
            Modifier
                .padding(24.dp)
                .clickable(
                    onClick = {},
                )
                .clip(VShapes.xl)
                .background(VColors.surfaceCard)
                .border(1.dp, VColors.line, VShapes.xl)
                .padding(20.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    if (state.extensionStudentId == null) appString(StringKeys.TC_EXTEND_WHOLE_CLASS) else appString(StringKeys.TC_EXTEND_FOR, "name" to (state.extensionStudentName ?: "")),
                    style = VTypography.h3,
                    color = VColors.ink,
                )
                VDatePicker(value = state.extensionDate, onValueChange = viewModel::setExtensionDate, label = appString(StringKeys.TC_NEW_DUE_DATE))
                VInput(value = state.extensionReason, onValueChange = viewModel::setExtensionReason, label = appString(StringKeys.TC_REASON_OPTIONAL), placeholder = appString(StringKeys.TC_WHY_EXTENSION))
                if (state.extensionError != null) Text(state.extensionError ?: "", style = VTypography.caption, color = VColors.coral)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    VButton(appString(StringKeys.COMMON_BUTTON_CANCEL), onClick = { viewModel.closeExtension() }, modifier = Modifier.weight(1f), variant = VButtonVariant.Ghost, size = VButtonSize.Md)
                    VButton(appString(StringKeys.TC_GRANT), onClick = { viewModel.grantExtension() }, modifier = Modifier.weight(1f), tone = VButtonTone.Sky, size = VButtonSize.Md, loading = state.isGrantingExtension)
                }
            }
        }
    }
}
