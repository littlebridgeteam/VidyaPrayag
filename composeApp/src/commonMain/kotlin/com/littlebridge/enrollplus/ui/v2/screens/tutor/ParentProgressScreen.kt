package com.littlebridge.enrollplus.ui.v2.screens.tutor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.tutor.domain.model.ProgressCardDto
import com.littlebridge.enrollplus.feature.tutor.domain.model.TopicProgressDto
import com.littlebridge.enrollplus.feature.tutor.presentation.ParentProgressViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VEmptyState
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.screens.VErrorState
import com.littlebridge.enrollplus.ui.v2.screens.VLoadingState
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.screens.parent.PremiumOverlayHeader
import com.littlebridge.enrollplus.ui.v2.theme.VTheme
import com.littlebridge.enrollplus.ui.v2.theme.colored
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.locale.appString
import org.koin.compose.viewmodel.koinViewModel

/**
 * ParentProgressScreen — mastery deltas, safety transparency, doubt stats.
 *
 * Parents see their child's progress across topics: current mastery,
 * doubts resolved, answers given (should be 0 — Socratic), and
 * safety flags if any.
 *
 * Spec: AI_TUTOR_2.0_AGENTIC_REDESIGN.md §10 (Parent progress card)
 */
@Composable
fun ParentProgressScreen(
    onBack: () -> Unit = {},
    subjectId: String = "",
    modifier: Modifier = Modifier,
    viewModel: ParentProgressViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    val c = VTheme.colors

    androidx.compose.runtime.LaunchedEffect(subjectId) {
        if (subjectId.isNotBlank()) viewModel.updateSubject(subjectId)
    }

    Box(
        modifier
            .fillMaxSize()
            .background(VColors.cream)
    ) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            PremiumOverlayHeader(title = appString(StringKeys.TUT_PROGRESS_TITLE), onBack = onBack)

            when {
                state.isLoading -> VLoadingState()
                state.error != null -> VErrorState(
                    message = state.error ?: "",
                    onRetry = { viewModel.loadProgress() },
                )
                state.progressCard == null -> VEmptyState(
                    title = appString(StringKeys.TUT_NO_PROGRESS),
                    body = appString(StringKeys.TUT_NO_PROGRESS_DESC),
                    icon = VIcons.BookOpen,
                )
                else -> state.progressCard?.let { ProgressContent(it) } ?: VEmptyState(
                    title = appString(StringKeys.TUT_NO_PROGRESS),
                    body = appString(StringKeys.TUT_NO_PROGRESS_DESC),
                    icon = VIcons.BookOpen,
                )
            }
        }
    }
}

@Composable
private fun ProgressContent(card: ProgressCardDto) {
    val c = VTheme.colors
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            VCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                ) {
                    StatBlock(appString(StringKeys.TUT_DOUBTS_RESOLVED), card.totalDoubtsResolved.toString())
                    StatBlock(appString(StringKeys.TUT_ANSWERS_GIVEN), card.totalAnswersGiven.toString())
                    StatBlock(appString(StringKeys.TUT_SESSIONS), card.totalSessions.toString())
                }
            }
        }

        if (card.safetyFlags > 0) {
            item {
                VCard(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(c.warmOrange.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("!", color = c.warmOrange, fontWeight = FontWeight.Bold)
                        }
                        Column(modifier = Modifier.padding(start = 12.dp)) {
                            Text(
                                appString(StringKeys.TUT_SAFETY_FLAGS).replace("{count}", card.safetyFlags.toString()),
                                style = VTheme.type.body.colored(c.ink),
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                appString(StringKeys.TUT_SAFETY_NOTIFIED),
                                style = VTheme.type.caption.colored(c.ink3),
                            )
                        }
                    }
                }
            }
        }

        if (card.topics.isNotEmpty()) {
            item {
                Text(
                    appString(StringKeys.TUT_TOPIC_MASTERY).replace("{count}", card.topics.size.toString()),
                    style = VTheme.type.h3.colored(c.ink),
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                )
            }
            items(card.topics) { topic ->
                TopicProgressCard(topic)
            }
        }
    }
}

@Composable
private fun StatBlock(label: String, value: String) {
    val c = VTheme.colors
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = VTheme.type.h2.colored(c.ink),
            fontWeight = FontWeight.Bold,
        )
        Text(
            label,
            style = VTheme.type.caption.colored(c.ink3),
        )
    }
}

@Composable
private fun TopicProgressCard(topic: TopicProgressDto) {
    val c = VTheme.colors
    val masteryColor = when {
        topic.currentMastery >= 75 -> c.teal
        topic.currentMastery >= 50 -> c.accent
        else -> c.warmOrange
    }
    VCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    appString(StringKeys.TUT_TOPIC_LABEL).replace("{topic}", topic.topicId.take(8)),
                    style = VTheme.type.body.colored(c.ink),
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    "${topic.currentMastery.toInt()}%",
                    style = VTheme.type.h3.colored(masteryColor),
                    fontWeight = FontWeight.Bold,
                )
            }
            LinearProgressIndicator(
                progress = { (topic.currentMastery / 100f).toFloat() },
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)),
                color = masteryColor,
                trackColor = c.cream,
            )
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    appString(StringKeys.TUT_ATTEMPTS).replace("{count}", topic.attempts.toString()),
                    style = VTheme.type.caption.colored(c.ink3),
                )
                Text(
                    appString(StringKeys.TUT_CORRECT_COUNT).replace("{count}", topic.correct.toString()),
                    style = VTheme.type.caption.colored(c.ink3),
                )
                Text(
                    appString(StringKeys.TUT_SOURCE_LABEL).replace("{source}", topic.source),
                    style = VTheme.type.caption.colored(c.ink3),
                )
            }
        }
    }
}
