package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.littlebridge.enrollplus.ui.v2.components.form.VTextInput
import com.littlebridge.enrollplus.ui.v2.components.navigation.VFilterChip
import com.littlebridge.enrollplus.ui.v2.components.navigation.VTopTabsPremium
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
import com.littlebridge.enrollplus.ui.v2.tokens.PremiumTheme
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

@Composable
fun ParentTutorChatScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) = PremiumTheme(isDark = false) {
    var inputText by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf("Chat") }
    var selectedSubject by remember { mutableStateOf("Math") }
    val tabs = listOf("Chat", "Plan", "Practice")
    val subjects = listOf("Math", "Science", "English", "Social")
    val suggestions = listOf("Explain fractions", "Practice algebra", "Quiz me on geometry", "Help with homework")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VColors.Surface)
            .statusBarsPadding(),
    ) {
        // Header with back button
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            val backInteraction = remember { MutableInteractionSource() }
            Box(
                Modifier.size(40.dp).clip(VShapes.Full).background(VColors.SurfaceContainerLow)
                    .pressScale(backInteraction, pressedScale = 0.9f)
                    .clickable(interactionSource = backInteraction, indication = null, onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = VColors.OnSurface, modifier = Modifier.size(20.dp))
            }
            Text("AI Tutor", style = VTypography.SectionHeader.copy(color = VColors.OnSurface, fontWeight = FontWeight.Bold))
        }

        // VTopTabs (Chat / Plan / Practice)
        VTopTabsPremium(
            tabs = tabs,
            selected = selectedTab,
            onSelect = { selectedTab = it },
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        Spacer(Modifier.height(12.dp))

        // Subject selector
        LazyRow(
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(subjects) { subject ->
                VFilterChip(
                    label = subject,
                    active = subject == selectedSubject,
                    onClick = { selectedSubject = subject },
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        when (selectedTab) {
            "Chat" -> {
                // Chat tab
                Column(
                    Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
                ) {
                    ChatBubble("Hi! I'm your AI tutor. Ready to practice $selectedSubject today? 🎯", "AI Tutor · 9:35 AM", isUser = false)
                    Spacer(Modifier.height(12.dp))
                    ChatBubble("Yes! Let's start", "You · 9:36 AM", isUser = true)
                    Spacer(Modifier.height(12.dp))
                    ChatBubble("Great! Let's begin with a quick question.", "AI Tutor · 9:36 AM", isUser = false)
                    Spacer(Modifier.height(12.dp))
                    TypingIndicator()
                }

                // Suggestion chips
                LazyRow(
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(suggestions) { suggestion ->
                        VFilterChip(
                            label = suggestion,
                            active = false,
                            onClick = { inputText = suggestion },
                            inactiveBg = VColors.SurfaceContainerLow,
                            inactiveFg = VColors.OnSurfaceVariant,
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))
            }
            "Plan" -> {
                // Plan tab
                Column(
                    Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
                ) {
                    PlanCard("Week 1", "Algebra Basics", "Linear equations & inequalities", 0.8f)
                    Spacer(Modifier.height(10.dp))
                    PlanCard("Week 2", "Geometry", "Triangles, circles & area", 0.5f)
                    Spacer(Modifier.height(10.dp))
                    PlanCard("Week 3", "Statistics", "Mean, median, mode & graphs", 0.2f)
                }
            }
            "Practice" -> {
                // Practice tab
                Column(
                    Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
                ) {
                    PracticeCard("Quick Quiz", "10 questions · 15 min", Icons.Filled.Quiz)
                    Spacer(Modifier.height(10.dp))
                    PracticeCard("Flashcards", "20 cards · $selectedSubject", Icons.Filled.School)
                    Spacer(Modifier.height(10.dp))
                    PracticeCard("Timed Challenge", "5 questions · 5 min", Icons.Filled.Quiz)
                }
            }
        }

        // Fixed input at bottom (only on Chat tab)
        if (selectedTab == "Chat") {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                VTextInput(
                    value = inputText,
                    onValueChange = { inputText = it },
                    label = "",
                    placeholder = "Type your answer…",
                    authStyle = false,
                    modifier = Modifier.weight(1f),
                )
                val sendInteraction = remember { MutableInteractionSource() }
                Box(
                    Modifier.size(48.dp).clip(VShapes.Full).background(VColors.Primary)
                        .pressScale(sendInteraction, pressedScale = 0.9f)
                        .clickable(interactionSource = sendInteraction, indication = null) { /* TODO: send message */ },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = VColors.OnPrimary, modifier = Modifier.size(22.dp))
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(text: String, timestamp: String, isUser: Boolean) {
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
    ) {
        Box(
            Modifier
                .widthIn(max = 280.dp)
                .clip(VShapes.Lg)
                .background(if (isUser) VColors.Primary else VColors.SurfaceContainerLow)
                .padding(12.dp),
        ) {
            Text(
                text,
                style = VTypography.UpdateText.copy(
                    color = if (isUser) VColors.OnPrimary else VColors.OnSurface,
                ),
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            timestamp,
            style = VTypography.NavLabel.copy(color = VColors.Outline, fontSize = 11.sp),
        )
    }
}

@Composable
private fun PlanCard(week: String, title: String, description: String, progress: Float) {
    Column(
        Modifier.fillMaxWidth().clip(VShapes.Xl).background(VColors.SurfaceContainerLowest).padding(16.dp),
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(week, style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant, fontWeight = FontWeight.SemiBold))
                Text(title, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.Bold))
                Text(description, style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant))
            }
            Text("${(progress * 100).toInt()}%", style = VTypography.SectionLink.copy(color = VColors.Primary, fontWeight = FontWeight.Bold))
        }
        Spacer(Modifier.height(10.dp))
        Box(Modifier.fillMaxWidth().height(5.dp).clip(VShapes.Full).background(VColors.SurfaceContainerHigh)) {
            Box(Modifier.fillMaxWidth(progress).height(5.dp).clip(VShapes.Full).background(VColors.Primary))
        }
    }
}

@Composable
private fun PracticeCard(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    val interaction = remember { MutableInteractionSource() }
    Row(
        Modifier.fillMaxWidth().clip(VShapes.Xl).background(VColors.SurfaceContainerLowest)
            .pressScale(interaction, pressedScale = 0.98f)
            .clickable(interactionSource = interaction, indication = null) { /* TODO: start practice */ }
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            Modifier.size(44.dp).clip(VShapes.Md).background(VColors.PrimaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = VColors.OnPrimaryContainer, modifier = Modifier.size(22.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(title, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.Bold))
            Text(subtitle, style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant))
        }
    }
}
