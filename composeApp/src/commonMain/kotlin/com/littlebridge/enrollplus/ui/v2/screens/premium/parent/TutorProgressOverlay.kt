package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.core.state.SelectedChildHolder
import com.littlebridge.enrollplus.feature.tutor.domain.model.ProgressCardDto
import com.littlebridge.enrollplus.feature.tutor.domain.model.TopicProgressDto
import com.littlebridge.enrollplus.feature.tutor.presentation.ParentProgressViewModel
import com.littlebridge.enrollplus.feature.tutor.presentation.TutorChatViewModel
import com.littlebridge.enrollplus.ui.v2.components.misc.VShimmerBoxPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VStateHostPremium
import com.littlebridge.enrollplus.ui.v2.components.navigation.VFilterChip
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TutorProgressOverlay(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ParentProgressViewModel = koinViewModel(),
    chatViewModel: TutorChatViewModel = koinViewModel(),
    selectedChildHolder: SelectedChildHolder = koinInject(),
) {
    val state by viewModel.state.collectAsStateV2()
    val chatState by chatViewModel.state.collectAsStateV2()
    val childId by selectedChildHolder.selectedChildId.collectAsStateV2()

    LaunchedEffect(childId) {
        childId?.let { viewModel.loadProgress() }
    }

    ParentOverlayScaffold(
        title = "Tutor Progress",
        onBack = onBack,
        modifier = modifier,
    ) {
        // Subject picker
        if (chatState.subjects.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                chatState.subjects.forEach { subject ->
                    VFilterChip(
                        label = subject.subjectName,
                        active = state.subjectId == subject.subjectId,
                        onClick = { viewModel.updateSubject(subject.subjectId) },
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        VStateHostPremium(
            loading = state.isLoading,
            error = state.error,
            isEmpty = state.progressCard == null && !state.isLoading,
            modifier = Modifier.fillMaxSize(),
            emptyTitle = if (state.subjectId.isBlank()) "Select a subject" else "No progress data",
            emptyIcon = Icons.Filled.School,
            onRetry = { viewModel.loadProgress() },
            skeleton = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    VShimmerBoxPremium(height = 120.dp, shape = VShapes.Xl)
                    repeat(3) { VShimmerBoxPremium(height = 80.dp, shape = VShapes.Lg) }
                }
            },
        ) {
            val card = state.progressCard ?: return@VStateHostPremium

            // Stats summary
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(VShapes.Xl)
                    .background(VColors.SurfaceContainerLowest)
                    .padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                ProgressStat(
                    label = "Doubts Resolved",
                    value = card.totalDoubtsResolved.toString(),
                    modifier = Modifier.weight(1f),
                )
                ProgressStat(
                    label = "Sessions",
                    value = card.totalSessions.toString(),
                    modifier = Modifier.weight(1f),
                )
                ProgressStat(
                    label = "Answers",
                    value = card.totalAnswersGiven.toString(),
                    modifier = Modifier.weight(1f),
                )
            }

            if (card.safetyFlags > 0) {
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(VShapes.Lg)
                        .background(VColors.WarmOrange.copy(alpha = 0.12f))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        Modifier.size(8.dp).clip(CircleShape).background(VColors.WarmOrange),
                    )
                    Text(
                        text = "${card.safetyFlags} safety flag${if (card.safetyFlags > 1) "s" else ""}",
                        style = VTypography.BodyMedium.copy(color = VColors.WarmOrange),
                    )
                }
            }

            // Topic breakdown
            if (card.topics.isNotEmpty()) {
                Spacer(Modifier.height(20.dp))
                Text(
                    text = "Topic Mastery",
                    style = VTypography.SectionHeader.copy(color = VColors.OnSurface),
                )
                Spacer(Modifier.height(12.dp))
                card.topics.forEach { topic ->
                    TopicRow(topic = topic)
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun ProgressStat(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = value,
            style = VTypography.QuickStatValue.copy(color = VColors.OnSurface),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            style = VTypography.QuickStatLabel.copy(color = VColors.OnSurfaceVariant),
        )
    }
}

@Composable
private fun TopicRow(topic: TopicProgressDto) {
    val masteryPercent = (topic.currentMastery * 100).toInt()
    val masteryColor = when {
        masteryPercent >= 80 -> VColors.Primary
        masteryPercent >= 50 -> VColors.WarmOrange
        else -> VColors.Error
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.Lg)
            .background(VColors.SurfaceContainerLow)
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = topic.topicId,
                style = VTypography.BodyLarge.copy(color = VColors.OnSurface),
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(Icons.Filled.TrendingUp, contentDescription = null, tint = masteryColor, modifier = Modifier.size(14.dp))
                Text(
                    text = "$masteryPercent%",
                    style = VTypography.QuickStatValue.copy(color = masteryColor),
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(VShapes.Full)
                .background(VColors.SurfaceContainerHigh),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(masteryPercent / 100f)
                    .height(6.dp)
                    .clip(VShapes.Full)
                    .background(masteryColor),
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = "${topic.correct}/${topic.attempts} correct",
            style = VTypography.ThreadTime.copy(color = VColors.OnSurfaceVariant),
        )
    }
}
