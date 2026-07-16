package com.littlebridge.enrollplus.ui.v2.screens.tutor

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.feature.tutor.domain.model.SubjectItemDto
import com.littlebridge.enrollplus.feature.tutor.presentation.ChatMessage
import com.littlebridge.enrollplus.feature.tutor.presentation.TutorChatViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.components.VEmptyState
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.theme.VTheme
import com.littlebridge.enrollplus.ui.v2.theme.colored
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.locale.appString
import org.koin.compose.viewmodel.koinViewModel

/**
 * TutorChatScreen — Claude-style AI tutor chat.
 *
 * Full-width message rows (not bubbles), subtle avatar indicators,
 * integrated input bar with inline send button, streaming text with
 * blinking cursor, compact subject chip above input.
 */
@Composable
fun TutorChatScreen(
    onBack: () -> Unit = {},
    subjectId: String = "",
    modifier: Modifier = Modifier,
    viewModel: TutorChatViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    val listState = rememberLazyListState()

    LaunchedEffect(subjectId) {
        if (subjectId.isNotBlank()) {
            viewModel.updateSubject(subjectId)
        } else {
            viewModel.loadSubjects()
        }
    }

    LaunchedEffect(state.conversationHistory.size) {
        if (state.conversationHistory.isNotEmpty()) {
            listState.animateScrollToItem(state.conversationHistory.lastIndex)
        }
    }

    LaunchedEffect(state.streamingText) {
        if (state.isStreaming && state.conversationHistory.isNotEmpty()) {
            listState.animateScrollToItem(state.conversationHistory.lastIndex)
        }
    }

    Column(
        modifier
            .fillMaxSize()
            .background(VColors.cream)
            .statusBarsPadding()
            .imePadding()
    ) {
        // ── Header ──
        VBackHeader(
            title = appString(StringKeys.TUT_AI_TUTOR),
            onBack = onBack,
            action = {
                if (state.conversationHistory.isNotEmpty()) {
                    Text(
                        appString(StringKeys.TUT_CLEAR),
                        style = VTypography.caption.copy(color = VColors.violet),
                        modifier = Modifier.clickable { viewModel.clearConversation() },
                    )
                }
            },
        )

        // ── Chat area ──
        Box(
            Modifier.weight(1f).fillMaxWidth(),
        ) {
            if (state.error != null) {
                VEmptyState(
                    title = appString(StringKeys.TUT_ERROR),
                    body = state.error ?: "",
                    icon = VIcons.AlertTriangle,
                    modifier = Modifier.fillMaxSize(),
                )
            } else if (state.conversationHistory.isEmpty() && !state.isLoading) {
                TutorWelcomeState(
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        top = 8.dp,
                        bottom = 16.dp,
                    ),
                ) {
                    itemsIndexed(state.conversationHistory) { idx, msg ->
                        val isLastTutor = msg.role == "tutor" &&
                            idx == state.conversationHistory.lastIndex &&
                            state.isStreaming
                        ChatRow(
                            msg = msg,
                            displayText = if (isLastTutor) state.streamingText else msg.text,
                            isStreaming = isLastTutor,
                        )
                    }
                    if (state.isLoading) {
                        item { TypingIndicator() }
                    }
                }
            }
        }

        // ── Input bar ──
        TutorInputBar(
            question = state.question,
            onQuestionChange = viewModel::updateQuestion,
            onSend = viewModel::askDoubt,
            canSend = state.question.isNotBlank() && !state.isLoading && !state.isStreaming,
            subjects = state.subjects,
            selectedSubjectId = state.subjectId,
            isLoadingSubjects = state.isLoadingSubjects,
            onSubjectSelect = viewModel::updateSubject,
        )
    }
}

@Composable
private fun ChatRow(
    msg: ChatMessage,
    displayText: String = msg.text,
    isStreaming: Boolean = false,
) {
    val c = VTheme.colors
    val isUser = msg.role == "user"

    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(vertical = 10.dp),
    ) {
        // ── Avatar + role label ──
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (!isUser) {
                Box(
                    Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(VColors.violetSoft),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        VIcons.GraduationCap,
                        contentDescription = null,
                        tint = VColors.violet,
                        modifier = Modifier.size(16.dp),
                    )
                }
                Spacer(Modifier.width(6.dp))
            }
            Text(
                if (isUser) "You" else "Tutor",
                style = VTypography.caption.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = if (isUser) c.ink3 else VColors.violet,
                ),
            )
            if (isUser) {
                Spacer(Modifier.width(6.dp))
                Box(
                    Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(c.accentTint),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        VIcons.User,
                        contentDescription = null,
                        tint = c.ink3,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }

        Spacer(Modifier.height(6.dp))

        // ── Message body ──
        val text = displayText.ifBlank { if (isStreaming) "" else msg.text }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        ) {
            Column(
                Modifier
                    .fillMaxWidth(0.88f)
                    .clip(
                        RoundedCornerShape(
                            topStart = 4.dp,
                            topEnd = 12.dp,
                            bottomStart = if (isUser) 12.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 12.dp,
                        )
                    )
                    .background(if (isUser) c.accentTint else c.card)
                    .border(0.5.dp, if (isUser) c.border1 else c.hairline, RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                // Streaming cursor
                if (isStreaming && text.isNotEmpty()) {
                    StreamingText(text = text)
                } else if (isStreaming) {
                    StreamingDots()
                } else {
                    Text(
                        text,
                        style = VTheme.type.body.colored(if (isUser) c.ink else c.ink),
                    )
                }

                // Safety flag warning banner
                if (!isStreaming && msg.safetyFlag != null) {
                    Spacer(Modifier.height(4.dp))
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(VColors.coral.copy(alpha = 0.08f))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(
                            VIcons.AlertTriangle,
                            contentDescription = null,
                            tint = VColors.coral,
                            modifier = Modifier.size(14.dp),
                        )
                        Text(
                            "Safety flag: ${msg.safetyFlag}. Teacher has been notified.",
                            style = VTypography.caption.copy(
                                color = VColors.coral,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp,
                            ),
                        )
                    }
                }

                if (!isStreaming && msg.nextPrompt != null) {
                    Spacer(Modifier.height(2.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Box(
                            Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(VColors.violet),
                        )
                        Text(
                            msg.nextPrompt ?: "",
                            style = VTheme.type.caption.colored(VColors.violet),
                            fontWeight = FontWeight.Medium,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        )
                    }
                }

                if (!isStreaming && msg.isPractice && msg.practiceQuestions != null) {
                    Spacer(Modifier.height(4.dp))
                    Row(
                        Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(VColors.mintSoft)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(
                            VIcons.CheckCircle,
                            contentDescription = null,
                            tint = VColors.mint,
                            modifier = Modifier.size(14.dp),
                        )
                        Text(
                            appString(StringKeys.TUT_PRACTICE_READY),
                            style = VTypography.caption.copy(
                                color = VColors.mint,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                            ),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StreamingText(text: String) {
    val c = VTheme.colors
    val transition = rememberInfiniteTransition(label = "cursor")
    val alpha by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "cursor_alpha",
    )
    Row(verticalAlignment = Alignment.Bottom) {
        Text(text, style = VTheme.type.body.colored(c.ink))
        Text(
            "▍",
            style = VTheme.type.body.colored(VColors.violet.copy(alpha = alpha)),
        )
    }
}

@Composable
private fun StreamingDots() {
    val transition = rememberInfiniteTransition(label = "dots")
    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "dots_alpha",
    )
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(3) {
            Box(
                Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(VColors.violet.copy(alpha = alpha)),
            )
        }
    }
}

@Composable
private fun TypingIndicator() {
    val c = VTheme.colors
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(VColors.violetSoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                VIcons.GraduationCap,
                contentDescription = null,
                tint = VColors.violet,
                modifier = Modifier.size(16.dp),
            )
        }
        Text("Tutor is thinking", style = VTypography.caption.colored(c.ink3))
        StreamingDots()
    }
}

@Composable
private fun TutorWelcomeState(modifier: Modifier = Modifier) {
    val c = VTheme.colors
    Column(
        modifier
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(VColors.violetSoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                VIcons.GraduationCap,
                contentDescription = null,
                tint = VColors.violet,
                modifier = Modifier.size(32.dp),
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            appString(StringKeys.TUT_ASK_QUESTION),
            style = VTypography.h3.copy(
                fontSize = 20.sp,
                lineHeight = 26.sp,
            ).colored(c.ink),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            appString(StringKeys.TUT_ASK_QUESTION_DESC),
            style = VTypography.body.colored(c.ink3),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
    }
}

@Composable
private fun TutorInputBar(
    question: String,
    onQuestionChange: (String) -> Unit,
    onSend: () -> Unit,
    canSend: Boolean,
    subjects: List<SubjectItemDto>,
    selectedSubjectId: String,
    isLoadingSubjects: Boolean,
    onSubjectSelect: (String) -> Unit,
) {
    val c = VTheme.colors

    Column(
        Modifier
            .fillMaxWidth()
            .background(c.card)
            .border(0.5.dp, c.hairline)
            .padding(horizontal = 16.dp)
            .padding(top = 10.dp, bottom = 8.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // ── Subject chip row ──
        SubjectChip(
            subjects = subjects,
            selectedSubjectId = selectedSubjectId,
            isLoading = isLoadingSubjects,
            onSelect = onSubjectSelect,
        )

        // ── Input row with inline send button ──
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            androidx.compose.material3.TextField(
                value = question,
                onValueChange = onQuestionChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        appString(StringKeys.TUT_TYPE_DOUBT),
                        style = VTheme.type.body.colored(c.placeholder),
                    )
                },
                shape = RoundedCornerShape(20.dp),
                colors = androidx.compose.material3.TextFieldDefaults.colors(
                    focusedContainerColor = c.cream,
                    unfocusedContainerColor = c.cream,
                    focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    cursorColor = VColors.violet,
                ),
                textStyle = VTheme.type.body.colored(c.ink),
                maxLines = 4,
            )

            // ── Send button ──
            Box(
                Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (canSend) VColors.violet else c.border1)
                    .clickable(enabled = canSend, onClick = onSend),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    VIcons.Send,
                    contentDescription = null,
                    tint = if (canSend) VColors.white else c.ink3,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Composable
private fun SubjectChip(
    subjects: List<SubjectItemDto>,
    selectedSubjectId: String,
    isLoading: Boolean,
    onSelect: (String) -> Unit,
) {
    val c = VTheme.colors
    var dropdownOpen by remember { mutableStateOf(false) }

    val selected = subjects.find { it.subjectId == selectedSubjectId }
    val label = when {
        isLoading -> appString(StringKeys.TUT_LOADING_SUBJECTS)
        selectedSubjectId.isEmpty() -> appString(StringKeys.TUT_GENERAL)
        selected != null -> selected.subjectName
        else -> appString(StringKeys.TUT_GENERAL)
    }

    Box {
        Row(
            Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(VColors.violetSoft)
                .clickable { dropdownOpen = true }
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(
                VIcons.Bookmark,
                contentDescription = null,
                tint = VColors.violet,
                modifier = Modifier.size(14.dp),
            )
            Text(
                label,
                style = VTypography.caption.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = VColors.violet,
                    fontSize = 12.sp,
                ),
            )
            Icon(
                VIcons.ChevronDown,
                contentDescription = null,
                tint = VColors.violet,
                modifier = Modifier.size(14.dp),
            )
        }

        DropdownMenu(
            expanded = dropdownOpen,
            onDismissRequest = { dropdownOpen = false },
            containerColor = c.card,
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        appString(StringKeys.TUT_GENERAL),
                        style = VTheme.type.body.colored(c.ink),
                    )
                },
                onClick = {
                    onSelect("")
                    dropdownOpen = false
                },
            )
            subjects.forEach { subject ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(
                                subject.subjectName,
                                style = VTheme.type.body.colored(c.ink),
                            )
                            Text(
                                subject.subjectCode,
                                style = VTheme.type.caption.colored(c.ink3),
                            )
                        }
                    },
                    onClick = {
                        onSelect(subject.subjectId)
                        dropdownOpen = false
                    },
                )
            }
        }
    }
}
