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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
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
            Box(
                Modifier.size(40.dp).clip(VShapes.Full).background(VColors.SurfaceContainerLow)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onBack,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = VColors.OnSurface, modifier = Modifier.size(20.dp))
            }
            Text("AI Tutor", style = VTypography.SectionHeader.copy(color = VColors.OnSurface, fontWeight = FontWeight.Bold))
        }

        // Chat messages — scrollable
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
        ) {
            ChatBubble("Hi Aarav! Ready to practice some math today? 🎯", "AI Tutor · 9:35 AM", isUser = false)
            Spacer(Modifier.height(12.dp))
            ChatBubble("Yes! Let's do algebra", "You · 9:36 AM", isUser = true)
            Spacer(Modifier.height(12.dp))
            ChatBubble("Great! Solve: 3x + 7 = 22. What is x?", "AI Tutor · 9:36 AM", isUser = false)
        }

        // Fixed input at bottom
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
            Box(
                Modifier.size(48.dp).clip(VShapes.Full).background(VColors.Primary),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = VColors.OnPrimary, modifier = Modifier.size(22.dp))
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
