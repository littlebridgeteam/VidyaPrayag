package com.littlebridge.enrollplus.ui.v2.screens.parent

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.feature.parent.domain.model.SkillTestQuestionDto
import com.littlebridge.enrollplus.feature.parent.presentation.SkillTestState
import com.littlebridge.enrollplus.feature.parent.presentation.SkillTestViewModel
import com.littlebridge.enrollplus.ui.components.VButton
import com.littlebridge.enrollplus.ui.components.VButtonVariant
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

// ═══════════════════════════════════════════════════════════════════════════════
// SkillTestCard — shown in the Academics Overview tab.
// SEPARATE from the teacher-generated QuizzesTab. This card handles the
// AI-generated weekly MCQ skill test with instant per-question evaluation.
//
// States:
//   1. Loading eligibility
//   2. Not eligible (cooldown) — shows best score + countdown
//   3. Eligible — "Start Test" button
//   4. No questions — waiting for weekly generation
//   5. In progress — question + options + instant feedback
//   6. Completed — final score + badge earned + retake info
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun SkillTestCard(
    childId: String?,
    viewModel: SkillTestViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    // Load eligibility when childId changes
    LaunchedEffect(childId) {
        if (childId != null) {
            viewModel.loadEligibility(childId)
        }
    }

    SkillTestCardContent(
        state = state,
        onStartTest = { viewModel.startTest(childId) },
        onSubmitAnswer = { qId, answer -> viewModel.submitAnswer(qId, answer) },
        onNext = { viewModel.nextQuestion() },
        onPrevious = { viewModel.previousQuestion() },
        onReset = { viewModel.resetTest() },
        onRetryEligibility = { viewModel.loadEligibility(childId) },
    )
}

@Composable
private fun SkillTestCardContent(
    state: SkillTestState,
    onStartTest: () -> Unit,
    onSubmitAnswer: (String, String) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onReset: () -> Unit,
    onRetryEligibility: () -> Unit,
) {
    Column(
        Modifier.fillMaxWidth()
            .background(VColors.surfaceCard, VShapes.lg)
            .border(1.dp, VColors.line, VShapes.lg)
            .padding(20.dp),
    ) {
        // ── Header ──
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                Modifier.size(40.dp).clip(VShapes.sm).background(VColors.violetSoft),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.Psychology, contentDescription = null, tint = VColors.violet, modifier = Modifier.size(22.dp))
            }
            Column(Modifier.weight(1f)) {
                Text("Skill Test", style = VTypography.h3.copy(fontSize = 16.sp), color = VColors.ink, fontWeight = FontWeight.Bold)
                Text("AI-generated weekly assessment", style = VTypography.caption, color = VColors.ink3)
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── State routing ──
        when {
            // Starting test
            state.isStartingTest -> {
                Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = VColors.violet, strokeWidth = 2.dp, modifier = Modifier.size(28.dp))
                        Spacer(Modifier.height(12.dp))
                        Text("Generating your test...", style = VTypography.caption, color = VColors.ink3)
                    }
                }
            }

            // Test in progress
            state.attemptId != null && !state.isCompleted -> {
                SkillTestInProgress(
                    state = state,
                    onSubmitAnswer = onSubmitAnswer,
                    onNext = onNext,
                    onPrevious = onPrevious,
                )
            }

            // Test completed
            state.isCompleted -> {
                SkillTestCompleted(state = state, onReset = onReset)
            }

            // Loading eligibility
            state.isLoadingEligibility -> {
                Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = VColors.violet, strokeWidth = 2.dp, modifier = Modifier.size(28.dp))
                }
            }

            // Eligibility error
            state.eligibilityError != null -> {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(state.eligibilityError ?: "", style = VTypography.caption, color = VColors.error, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(8.dp))
                    VButton("Retry", onClick = onRetryEligibility, variant = VButtonVariant.Secondary)
                }
            }

            // Start error
            state.startError != null -> {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(state.startError ?: "", style = VTypography.caption, color = VColors.error, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(8.dp))
                    VButton("Retry", onClick = onStartTest, variant = VButtonVariant.Secondary)
                }
            }

            // Not eligible — grade not set
            !state.eligible && state.gradeLevel == null && state.bestScore == null -> {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        state.eligibilityReason.ifBlank { "Please update your child's class in the profile to access skill tests." },
                        style = VTypography.body.copy(fontSize = 13.sp),
                        color = VColors.ink3,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            // Not eligible — cooldown
            !state.eligible && state.bestScore != null -> {
                SkillTestCooldown(state = state)
            }

            // Eligible — ready to start
            state.eligible && state.hasQuestions -> {
                SkillTestReady(state = state, onStartTest = onStartTest)
            }

            // No questions yet — backend is generating (or failed); let the parent retry
            !state.hasQuestions -> {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    val message = state.gradeLevel?.let {
                        "Questions for $it are being generated in the background. Please check back in a minute."
                    } ?: "Questions are being generated in the background. Please check back in a minute."
                    Text(
                        message,
                        style = VTypography.body.copy(fontSize = 13.sp),
                        color = VColors.ink3,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(12.dp))
                    VButton(
                        text = "Check again",
                        onClick = onRetryEligibility,
                        variant = VButtonVariant.Secondary,
                    )
                }
            }

            // Default — eligible with no prior score
            else -> {
                SkillTestReady(state = state, onStartTest = onStartTest)
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// SUB-STATES
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun SkillTestReady(state: SkillTestState, onStartTest: () -> Unit) {
    // Show grade level
    state.gradeLevel?.let { grade ->
        Text(
            "$grade Skill Test",
            style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold),
            color = VColors.ink3,
        )
        Spacer(Modifier.height(8.dp))
    }

    // Show best score if exists
    state.bestScore?.let { bs ->
        if (bs.attemptsCount > 0) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text("Best Score", style = VTypography.caption, color = VColors.ink3)
                    Text("${bs.bestScore}%", style = VTypography.h2.copy(fontSize = 22.sp), color = VColors.violet, fontWeight = FontWeight.ExtraBold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Attempts", style = VTypography.caption, color = VColors.ink3)
                    Text("${bs.attemptsCount}", style = VTypography.h3.copy(fontSize = 16.sp), color = VColors.ink, fontWeight = FontWeight.Bold)
                }
                if (bs.badgeEarned) {
                    Spacer(Modifier.size(8.dp))
                    Box(
                        Modifier.size(36.dp).clip(CircleShape).background(VColors.goldSoft),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Filled.EmojiEvents, contentDescription = "Badge earned", tint = VColors.gold, modifier = Modifier.size(20.dp))
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }

    VButton(
        text = "Start Skill Test",
        onClick = onStartTest,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun SkillTestCooldown(state: SkillTestState) {
    val bs = state.bestScore ?: return
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(44.dp).clip(CircleShape).background(VColors.violetSoft),
            contentAlignment = Alignment.Center,
        ) {
            Text("${bs.bestScore}%", style = VTypography.h3.copy(fontSize = 14.sp), color = VColors.violet, fontWeight = FontWeight.ExtraBold)
        }
        Column(Modifier.weight(1f)) {
            Text("Best Score: ${bs.bestScore}%", style = VTypography.body.copy(fontSize = 14.sp), color = VColors.ink, fontWeight = FontWeight.SemiBold)
            Text(
                state.eligibilityReason.ifBlank { "Retake available soon" },
                style = VTypography.caption,
                color = VColors.ink3,
            )
        }
        if (bs.badgeEarned) {
            Icon(Icons.Filled.EmojiEvents, contentDescription = "Badge earned", tint = VColors.gold, modifier = Modifier.size(24.dp))
        }
    }
    Spacer(Modifier.height(12.dp))
    LinearProgressIndicator(
        progress = { 1f },
        modifier = Modifier.fillMaxWidth().height(4.dp).clip(VShapes.sm),
        color = VColors.violetSoft,
        trackColor = VColors.lineSoft,
    )
}

@Composable
private fun SkillTestInProgress(
    state: SkillTestState,
    onSubmitAnswer: (String, String) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
) {
    val question = state.currentQuestion
    if (question == null) {
        Text("No question available", style = VTypography.body, color = VColors.ink3)
        return
    }

    // Progress bar
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            "Question ${state.currentQuestionIndex + 1} of ${state.totalQuestions}",
            style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold),
            color = VColors.ink2,
        )
        Text(
            "${state.correctCount} correct",
            style = VTypography.caption.copy(fontWeight = FontWeight.Bold),
            color = VColors.success,
        )
    }
    Spacer(Modifier.height(8.dp))
    LinearProgressIndicator(
        progress = { state.progressPct },
        modifier = Modifier.fillMaxWidth().height(4.dp).clip(VShapes.sm),
        color = VColors.violet,
        trackColor = VColors.lineSoft,
    )
    Spacer(Modifier.height(20.dp))

    // Question
    AnimatedContent(
        targetState = state.currentQuestionIndex,
        transitionSpec = {
            val forward = targetState > initialState
            val dur = 250
            slideInHorizontally(tween(dur), initialOffsetX = { if (forward) it / 3 else -it / 3 }) + fadeIn(tween(dur)) togetherWith
                slideOutHorizontally(tween(dur), targetOffsetX = { if (forward) -it / 3 else it / 3 }) + fadeOut(tween(dur))
        },
        label = "skill-test-question",
    ) { _ ->
        Column {
            // Subject tag
            SkillTestSubjectTag(question.subject)
            Spacer(Modifier.height(8.dp))

            // Question text
            Text(
                question.questionText,
                style = VTypography.body.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
                color = VColors.ink,
            )
            Spacer(Modifier.height(16.dp))

            // Options
            val isAnswered = question.id in state.answeredQuestions
            val lastResult = state.lastAnswerResult
            val showFeedback = isAnswered && lastResult != null && lastResult.questionId == question.id
            val selectedAnswer = state.lastSelectedAnswer

            question.options.forEach { option ->
                val letter = option.substringBefore(")").trim()
                val isCorrectOption = showFeedback && lastResult?.correctAnswer?.equals(letter, ignoreCase = true) == true
                val isUserWrongChoice = showFeedback && lastResult?.isCorrect == false &&
                    selectedAnswer?.equals(letter, ignoreCase = true) == true

                SkillTestOptionRow(
                    option = option,
                    isCorrect = isCorrectOption,
                    isWrong = isUserWrongChoice,
                    showCorrect = showFeedback && isCorrectOption,
                    enabled = !isAnswered && !state.isSubmittingAnswer,
                    onClick = { onSubmitAnswer(question.id, letter) },
                )
            }

            // Instant feedback
            if (showFeedback && lastResult != null) {
                Spacer(Modifier.height(12.dp))
                SkillTestFeedbackBox(lastResult.isCorrect, lastResult.correctAnswer, lastResult.explanation)
            }
        }
    }

    // Submitting indicator
    if (state.isSubmittingAnswer) {
        Spacer(Modifier.height(12.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircularProgressIndicator(color = VColors.violet, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
            Spacer(Modifier.size(8.dp))
            Text("Evaluating...", style = VTypography.caption, color = VColors.ink3)
        }
    }

    // Answer error
    state.answerError?.let {
        Spacer(Modifier.height(8.dp))
        Text(it, style = VTypography.caption, color = VColors.error)
    }

    // Navigation (only show after answering, before last question)
    val isAnswered = question.id in state.answeredQuestions
    if (isAnswered && !state.isSubmittingAnswer && state.currentQuestionIndex < state.totalQuestions - 1) {
        Spacer(Modifier.height(12.dp))
        VButton("Next Question", onClick = onNext, modifier = Modifier.fillMaxWidth())
    }
    if (state.currentQuestionIndex > 0 && !isAnswered) {
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            VButton("Back", onClick = onPrevious, variant = VButtonVariant.Ghost)
        }
    }
}

@Composable
private fun SkillTestCompleted(state: SkillTestState, onReset: () -> Unit) {
    val score = state.finalScore ?: 0
    val passed = score >= 60
    val badge = state.badgeEarned == true

    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Score circle
        Box(
            Modifier.size(72.dp).clip(CircleShape).background(
                if (passed) VColors.successSoft else VColors.violetSoft
            ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "$score%",
                style = VTypography.h2.copy(fontSize = 22.sp),
                color = if (passed) VColors.success else VColors.violet,
                fontWeight = FontWeight.ExtraBold,
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            if (passed) "Great job!" else "Keep practicing!",
            style = VTypography.h3.copy(fontSize = 16.sp),
            color = VColors.ink,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "${state.correctCount} out of ${state.totalQuestions} correct",
            style = VTypography.body.copy(fontSize = 14.sp),
            color = VColors.ink2,
        )

        // Badge earned
        if (badge) {
            Spacer(Modifier.height(16.dp))
            Row(
                Modifier.fillMaxWidth().clip(VShapes.sm).background(VColors.goldSoft).padding(12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Filled.EmojiEvents, contentDescription = null, tint = VColors.gold, modifier = Modifier.size(20.dp))
                Spacer(Modifier.size(8.dp))
                Text("Badge Earned: First Pass!", style = VTypography.body.copy(fontSize = 14.sp, fontWeight = FontWeight.Bold), color = VColors.gold)
            }
        }

        Spacer(Modifier.height(20.dp))

        // Best score info
        state.bestScore?.let { bs ->
            Text(
                "Best Score: ${bs.bestScore}% · Attempts: ${bs.attemptsCount}",
                style = VTypography.caption,
                color = VColors.ink3,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Next retake in 7 days",
                style = VTypography.caption,
                color = VColors.ink3,
            )
        }

        Spacer(Modifier.height(16.dp))
        VButton("Done", onClick = onReset, modifier = Modifier.fillMaxWidth(), variant = VButtonVariant.Secondary)
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// HELPER COMPOSABLES
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun SkillTestSubjectTag(subject: String) {
    Box(
        Modifier.clip(VShapes.sm).background(VColors.violetSoft).padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Text(subject, style = VTypography.caption.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold), color = VColors.violet)
    }
}

@Composable
private fun SkillTestOptionRow(
    option: String,
    isCorrect: Boolean,
    isWrong: Boolean,
    showCorrect: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val bg = when {
        showCorrect -> VColors.success.copy(alpha = 0.08f)
        isWrong -> VColors.error.copy(alpha = 0.08f)
        else -> VColors.creamDeep
    }
    val border = when {
        showCorrect -> VColors.success.copy(alpha = 0.3f)
        isWrong -> VColors.error.copy(alpha = 0.3f)
        else -> VColors.line
    }

    Row(
        Modifier.fillMaxWidth().padding(vertical = 4.dp)
            .clip(VShapes.sm)
            .background(bg)
            .border(1.dp, border, VShapes.sm)
            .clickable(enabled = enabled, interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            Modifier.size(22.dp).clip(CircleShape)
                .background(if (showCorrect) VColors.success else if (isWrong) VColors.error else VColors.surfaceCard)
                .border(1.dp, if (showCorrect) VColors.success else if (isWrong) VColors.error else VColors.line, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (showCorrect) {
                Icon(Icons.Filled.Check, contentDescription = null, tint = VColors.white, modifier = Modifier.size(14.dp))
            } else if (isWrong) {
                Icon(Icons.Filled.Close, contentDescription = null, tint = VColors.white, modifier = Modifier.size(14.dp))
            }
        }
        Text(option, style = VTypography.body.copy(fontSize = 14.sp), color = VColors.ink)
    }
}

@Composable
private fun SkillTestFeedbackBox(isCorrect: Boolean, correctAnswer: String, explanation: String) {
    Column(
        Modifier.fillMaxWidth().clip(VShapes.sm)
            .background(if (isCorrect) VColors.success.copy(alpha = 0.06f) else VColors.error.copy(alpha = 0.06f))
            .border(1.dp, if (isCorrect) VColors.success.copy(alpha = 0.2f) else VColors.error.copy(alpha = 0.2f), VShapes.sm)
            .padding(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(
                if (isCorrect) Icons.Filled.Check else Icons.Filled.Close,
                contentDescription = null,
                tint = if (isCorrect) VColors.success else VColors.error,
                modifier = Modifier.size(16.dp),
            )
            Text(
                if (isCorrect) "Correct!" else "Incorrect",
                style = VTypography.body.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold),
                color = if (isCorrect) VColors.success else VColors.error,
            )
        }
        if (!isCorrect && correctAnswer.isNotBlank()) {
            Spacer(Modifier.height(4.dp))
            Text("Correct answer: $correctAnswer", style = VTypography.caption.copy(fontSize = 12.sp), color = VColors.success, fontWeight = FontWeight.SemiBold)
        }
        if (explanation.isNotBlank()) {
            Spacer(Modifier.height(4.dp))
            Text(explanation, style = VTypography.caption.copy(fontSize = 12.sp), color = VColors.ink2)
        }
    }
}
