package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
import com.littlebridge.enrollplus.ui.v2.tokens.PremiumTheme
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VMotion
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

/**
 * Shared overlay scaffold for parent portal full-screen overlays.
 * Provides back header + scrollable content area with premium tokens.
 */
@Composable
fun ParentOverlayScaffold(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) = PremiumTheme(isDark = false) {
    // Slide-in from right, matching HTML reference overlay transform: translateX(100%) → translateX(0)
    val slideAnim = remember { Animatable(1f) }
    LaunchedEffect(Unit) {
        slideAnim.animateTo(0f, tween(VMotion.DurMedium2, easing = VMotion.EaseEmphasized))
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VColors.Surface)
            .statusBarsPadding()
            .graphicsLayer { translationX = slideAnim.value * size.width }
            .verticalScroll(rememberScrollState()),
    ) {
        // Back header
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            val backInteraction = remember { MutableInteractionSource() }
            Box(
                Modifier.size(40.dp).clip(CircleShape).background(VColors.SurfaceContainerHigh)
                    .pressScale(backInteraction, pressedScale = 0.9f)
                    .clickable(interactionSource = backInteraction, indication = null, onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = VColors.OnSurface, modifier = Modifier.size(20.dp))
            }
            Text(title, style = VTypography.SectionHeader.copy(color = VColors.OnSurface, fontWeight = FontWeight.Bold))
        }

        // Content
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        ) {
            content()
            Spacer(Modifier.height(32.dp))
        }
    }
}
