package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.core.state.SelectedChildHolder
import com.littlebridge.enrollplus.feature.tutor.domain.model.SubjectItemDto
import com.littlebridge.enrollplus.feature.tutor.presentation.ChatMessage
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
fun TutorChatOverlay(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TutorChatViewModel = koinViewModel(),
    selectedChildHolder: SelectedChildHolder = koinInject(),
) {
    val state by viewModel.state.collectAsStateV2()
    val childId by selectedChildHolder.selectedChildId.collectAsStateV2()

    LaunchedEffect(childId) {
        viewModel.loadSubjects()
    }

    // TutorChat needs a non-scrolling Column: header (subject picker) + messages (weight) + compose bar
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VColors.Surface),
    ) {
        // Back header (inline, not ParentOverlayScaffold because we need weight for messages)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(VColors.Surface)
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = VColors.OnSurface,
                )
            }
            Text(
                text = "AI Tutor",
                style = VTypography.SectionHeader.copy(color = VColors.OnSurface),
            )
        }

        // Subject picker
        if (state.subjects.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                VFilterChip(
                    label = "All",
                    active = state.subjectId.isEmpty(),
                    onClick = { viewModel.updateSubject("") },
                )
                state.subjects.forEach { subject ->
                    VFilterChip(
                        label = subject.subjectName,
                        active = state.subjectId == subject.subjectId,
                        onClick = { viewModel.updateSubject(subject.subjectId) },
                    )
                }
            }
        } else if (state.isLoadingSubjects) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                repeat(3) {
                    VShimmerBoxPremium(
                        height = 32.dp,
                        shape = VShapes.Full,
                        modifier = Modifier.width(80.dp),
                    )
                }
            }
        }

        // Messages area
        if (state.conversationHistory.isEmpty() && !state.isLoading) {
            // Empty state — suggestion chips
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(VColors.PrimaryContainer),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Filled.School, contentDescription = null, tint = VColors.Primary, modifier = Modifier.size(28.dp))
                    }
                    Text(
                        text = "Ask me anything about your child's subjects",
                        style = VTypography.BodyLarge.copy(color = VColors.OnSurfaceVariant),
                    )
                    SuggestionChips(
                        onSuggestion = { viewModel.updateQuestion(it); viewModel.askDoubt() },
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 20.dp, end = 20.dp, top = 16.dp, bottom = 16.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(state.conversationHistory) { msg ->
                    TutorMessageBubble(message = msg)
                }
                if (state.isLoading) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(VShapes.Lg)
                                    .background(VColors.SurfaceContainerHigh)
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                            ) {
                                Text(
                                    text = "Thinking...",
                                    style = VTypography.BodyMedium.copy(color = VColors.OnSurfaceVariant),
                                )
                            }
                        }
                    }
                }
            }
        }

        // Error
        if (state.error != null) {
            val errMsg = state.error!!
            Text(
                text = errMsg,
                style = VTypography.ThreadPreview.copy(color = VColors.Error),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
            )
        }

        // Compose bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(VColors.SurfaceContainerLow)
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .imePadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                value = state.question,
                onValueChange = { viewModel.updateQuestion(it) },
                placeholder = { Text("Ask a doubt...", style = VTypography.BodyMedium.copy(color = VColors.OnSurfaceVariant)) },
                modifier = Modifier.weight(1f),
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                ),
                shape = VShapes.Full,
            )
            IconButton(
                onClick = { viewModel.askDoubt() },
                enabled = state.question.isNotBlank() && !state.isLoading,
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = if (state.question.isNotBlank() && !state.isLoading) VColors.Primary else VColors.Outline,
                )
            }
        }
    }
}

@Composable
private fun SuggestionChips(onSuggestion: (String) -> Unit) {
    val suggestions = listOf(
        "Explain photosynthesis",
        "Help with fractions",
        "Tips for exam prep",
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        suggestions.forEach { text ->
            Row(
                modifier = Modifier
                    .clip(VShapes.Full)
                    .background(VColors.SurfaceContainerLow)
                    .clickable { onSuggestion(text) }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(Icons.Filled.Lightbulb, contentDescription = null, tint = VColors.OnSurfaceVariant, modifier = Modifier.size(14.dp))
                Text(
                    text = text,
                    style = VTypography.BodyMedium.copy(color = VColors.OnSurface),
                )
            }
        }
    }
}

@Composable
private fun TutorMessageBubble(message: ChatMessage) {
    val isUser = message.role == "user"
    val bg = if (isUser) VColors.Primary else VColors.SurfaceContainerHigh
    val fg = if (isUser) VColors.OnPrimary else VColors.OnSurface
    val alignment = if (isUser) Alignment.End else Alignment.Start

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment,
    ) {
        Box(
            modifier = Modifier
                .width(280.dp)
                .clip(VShapes.Lg)
                .background(bg)
                .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            Column {
                Text(
                    text = message.text,
                    style = VTypography.BodyMedium.copy(color = fg),
                )
                if (message.isPractice && message.practiceQuestions != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Practice questions available",
                        style = VTypography.ThreadTime.copy(color = if (isUser) VColors.OnPrimary.copy(alpha = 0.7f) else VColors.Primary),
                    )
                }
                if (!message.nextPrompt.isNullOrBlank()) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Next: ${message.nextPrompt}",
                        style = VTypography.ThreadTime.copy(color = if (isUser) VColors.OnPrimary.copy(alpha = 0.7f) else VColors.OnSurfaceVariant),
                    )
                }
            }
        }
    }
}
