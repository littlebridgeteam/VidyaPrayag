package com.littlebridge.enrollplus.ui.v2.screens.premium.auth

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.components.misc.VBrandLogoPremium
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
import com.littlebridge.enrollplus.ui.v2.modifiers.radialGlow
import com.littlebridge.enrollplus.ui.v2.tokens.PremiumTheme
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VMotion
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import kotlinx.coroutines.launch

/**
 * Premium auth scaffold — shared chrome for role-scoped auth screens.
 *
 * M3 Expressive design:
 * - Gradient hero with radial glow + bridge logo
 * - Glass back button
 * - Title/subtitle with M3 Expressive typography
 * - Rounded form sheet overlapping hero
 * - Error display + secured footer
 *
 * Wraps content in PremiumTheme for dark mode support.
 */
@Composable
fun AuthScaffoldPremium(
    title: String,
    subtitle: String,
    error: String?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) = PremiumTheme(isDark = false) {
    val logoScale = remember { Animatable(0.85f) }
    val logoAlpha = remember { Animatable(0f) }
    val sheetY = remember { Animatable(40f) }
    val sheetAlpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        launch { logoScale.animateTo(1f, tween(VMotion.DurLong1)) }
        launch { logoAlpha.animateTo(1f, tween(VMotion.DurLong1)) }
        launch { sheetY.animateTo(0f, tween(VMotion.DurLong2)) }
        launch { sheetAlpha.animateTo(1f, tween(VMotion.DurMedium2)) }
    }

    Column(
        modifier
            .fillMaxSize()
            .background(VColors.Surface)
            .verticalScroll(rememberScrollState()),
    ) {
        // ── Gradient hero ──
        Box(
            Modifier
                .fillMaxWidth()
                .heightIn(min = 280.dp)
                .background(
                    Brush.linearGradient(
                        colors = listOf(VColors.Primary, VColors.PrimaryMid, VColors.PrimaryDeep),
                    ),
                )
                .radialGlow(offsetX = 280.dp, offsetY = (-60).dp, radius = 240.dp, color = VColors.HeroGlowTopRight),
            contentAlignment = Alignment.Center,
        ) {
            // Back button — glass style
            val backInteraction = remember { MutableInteractionSource() }
            Box(
                Modifier
                    .align(Alignment.TopStart)
                    .statusBarsPadding()
                    .padding(start = 16.dp, top = 8.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(VColors.GlassWhite15)
                    .pressScale(backInteraction, pressedScale = 0.9f)
                    .clickable(interactionSource = backInteraction, indication = null, onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = VColors.OnPrimary,
                    modifier = Modifier.size(20.dp),
                )
            }

            Column(
                Modifier
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                VBrandLogoPremium(
                    size = 120.dp,
                    modifier = Modifier.graphicsLayer {
                        scaleX = logoScale.value
                        scaleY = logoScale.value
                        alpha = logoAlpha.value
                    },
                )
                Spacer(Modifier.height(20.dp))
                Text(
                    title,
                    style = VTypography.LoginHeroTitle.copy(color = VColors.OnPrimary),
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    subtitle,
                    style = VTypography.LoginHeroSub.copy(color = VColors.OnPrimary.copy(alpha = 0.7f)),
                    textAlign = TextAlign.Center,
                )
            }
        }

        // ── Form sheet (overlaps hero) ──
        Column(
            Modifier
                .fillMaxWidth()
                .offset(y = (-16).dp)
                .graphicsLayer {
                    translationY = sheetY.value * density
                    alpha = sheetAlpha.value
                }
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(VColors.Surface)
                .imePadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp)
                .padding(top = 32.dp, bottom = 32.dp),
        ) {
            content()

            if (error != null) {
                Spacer(Modifier.height(12.dp))
                Text(
                    error,
                    style = VTypography.UpdateText.copy(color = VColors.Error),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
            }

            // Secured footer
            Spacer(Modifier.height(20.dp))
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(
                    Icons.Filled.Lock,
                    contentDescription = null,
                    tint = VColors.Outline,
                    modifier = Modifier.size(13.dp),
                )
                Text(
                    "Secured by Enroll+",
                    style = VTypography.NavLabel.copy(color = VColors.Outline),
                )
            }
        }
    }
}

/** "Back" text link for stepping back through the auth flow. */
@Composable
fun AuthBackLinkPremium(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val interaction = remember { MutableInteractionSource() }
    Text(
        "Back",
        style = VTypography.ButtonText.copy(color = VColors.OnSurfaceVariant),
        modifier = modifier
            .pressScale(interaction, pressedScale = 0.95f)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
    )
}
