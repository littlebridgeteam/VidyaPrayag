package com.littlebridge.enrollplus.ui.v2.screens.tutor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.tutor.domain.model.PracticeQuestionDto
import com.littlebridge.enrollplus.feature.tutor.presentation.TutorPracticeViewModel
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonSize
import com.littlebridge.enrollplus.ui.v2.components.VButtonTone
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VEmptyState
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.theme.VTheme
import com.littlebridge.enrollplus.ui.v2.theme.colored
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.locale.appString
import org.koin.compose.viewmodel.koinViewModel
import com.littlebridge.enrollplus.ui.v2.screens.teacher.TeacherKit.TeacherSpinner

/**
 * TutorPracticeScreen — targeted practice + auto-grade feedback.
 *
 * Displays MCQ or free-response practice questions. On submit,
 * the answer is auto-graded server-side and the child sees
 * correct/incorrect + explanation + new mastery level.
 *
 * Spec: AI_TUTOR_2.0_AGENTIC_REDESIGN.md §6.4 (Tier 3 Act — Practice)
 */
@Composable
fun TutorPracticeScreen(
    onBack: () -> Unit = {},
    subjectId: String = "",
    question: PracticeQuestionDto? = null,
    modifier: Modifier = Modifier,
    viewModel: TutorPracticeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    val c = VTheme.colors

    androidx.compose.runtime.LaunchedEffect(subjectId, question) {
        if (subjectId.isNotBlank()) viewModel.updateSubject(subjectId)
        if (question != null) viewModel.setQuestion(question)
    }

    Box(
        modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(c.background)
    ) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            VBackHeader(title = appString(StringKeys.TUT_PRACTICE), onBack = onBack)

            val q = state.currentQuestion
            when {
                state.isGrading -> {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            TeacherSpinner(36.dp)
                            Text(
                                appString(StringKeys.TUT_GRADING),
                                style = VTheme.type.caption.colored(c.ink3),
                                modifier = Modifier.padding(top = 12.dp),
                            )
                        }
                    }
                }
                q == null -> {
                    VEmptyState(
                        title = appString(StringKeys.TUT_NO_QUESTION),
                        body = appString(StringKeys.TUT_NO_QUESTION_DESC),
                        icon = VIcons.BookOpen,
                    )
                }
                state.gradeResult != null -> {
                    val result = state.gradeResult
                    if (result != null) {
                        GradeResultContent(
                            result = result,
                            onNext = viewModel::nextQuestion,
                        )
                    }
                }
                else -> {
                    QuestionContent(
                        question = q,
                        selectedAnswer = state.selectedAnswer,
                        onSelect = viewModel::selectAnswer,
                        onSubmit = viewModel::submitAnswer,
                    )
                }
            }
        }
    }
}

@Composable
private fun QuestionContent(
    question: PracticeQuestionDto,
    selectedAnswer: String,
    onSelect: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    val c = VTheme.colors
    Column(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        VCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(c.accent.copy(alpha = 0.12f))
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                    ) {
                        Text(
                            question.difficulty.uppercase(),
                            style = VTheme.type.caption.colored(c.accent),
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                Text(
                    question.stem,
                    style = VTheme.type.h3.colored(c.ink),
                )
            }
        }

        val opts = question.options
        if (opts != null) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                opts.forEach { option ->
                    val isSelected = option == selectedAnswer
                    VCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(option) },
                    ) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                Modifier
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) c.accent else c.cream),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (isSelected) {
                                    Text("✓", color = c.card, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text(
                                option,
                                style = VTheme.type.body.colored(c.ink),
                            )
                        }
                    }
                }
            }
        } else {
            androidx.compose.material3.OutlinedTextField(
                value = selectedAnswer,
                onValueChange = onSelect,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(appString(StringKeys.TUT_TYPE_ANSWER)) },
                shape = RoundedCornerShape(12.dp),
            )
        }

        VButton(
            text = appString(StringKeys.TUT_SUBMIT_ANSWER),
            modifier = Modifier.fillMaxWidth(),
            size = VButtonSize.Md,
            variant = VButtonVariant.Primary,
            tone = VButtonTone.Lavender,
            enabled = selectedAnswer.isNotBlank(),
            onClick = onSubmit,
        )
    }
}

@Composable
private fun GradeResultContent(
    result: com.littlebridge.enrollplus.feature.tutor.domain.model.PracticeGradeDto,
    onNext: () -> Unit,
) {
    val c = VTheme.colors
    Column(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        VCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    if (result.correct) appString(StringKeys.TUT_CORRECT) else appString(StringKeys.TUT_NOT_QUITE),
                    style = VTheme.type.h2.colored(if (result.correct) c.teal else c.warmOrange),
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    appString(StringKeys.TUT_SCORE_PCT).replace("{pct}", result.gradePct.toString()),
                    style = VTheme.type.body.colored(c.ink),
                )
            }
        }

        VCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    appString(StringKeys.TUT_FEEDBACK),
                    style = VTheme.type.caption.colored(c.ink3),
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    result.feedback,
                    style = VTheme.type.body.colored(c.ink2),
                )
            }
        }

        VButton(
            text = appString(StringKeys.TUT_NEXT_QUESTION),
            modifier = Modifier.fillMaxWidth(),
            size = VButtonSize.Md,
            variant = VButtonVariant.Primary,
            tone = VButtonTone.Lavender,
            onClick = onNext,
        )
    }
}
