package com.littlebridge.enrollplus.ui.v2.screens.premium.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Favorite
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.components.misc.VLogoPremium
import com.littlebridge.enrollplus.ui.v2.components.typography.VGradientText
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
import com.littlebridge.enrollplus.ui.v2.modifiers.radialGlow
import com.littlebridge.enrollplus.ui.v2.modifiers.shapeMorph
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VMotion
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import kotlinx.coroutines.delay

@Composable
fun CommonLandingScreen(
    onParent: () -> Unit,
    onAdmin: () -> Unit,
    modifier: Modifier = Modifier,
    onLegal: (String) -> Unit = {},
) {
    var animStep by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        for (i in 1..5) {
            delay(100)
            animStep = i
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VColors.Surface)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding(),
    ) {
        // ── Hero ───────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(VColors.Primary, VColors.PrimaryMid, VColors.PrimaryDeep),
                    ),
                )
                .radialGlow(offsetX = 300.dp, offsetY = (-80).dp, radius = 260.dp, color = VColors.HeroGlowTopRight)
                .radialGlow(offsetX = (-60).dp, offsetY = 500.dp, radius = 220.dp, color = VColors.HeroGlowBottomLeft),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp)
                    .padding(top = 40.dp, bottom = 56.dp),
            ) {
                AnimatedVisibility(
                    visible = animStep >= 1,
                    enter = fadeIn(tween(VMotion.DurMedium2)) +
                        slideInVertically(tween(VMotion.DurLong2)) { it / 2 },
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        VLogoPremium(size = 36.dp)
                        Text(
                            text = "Enroll+",
                            style = VTypography.BrandText.copy(color = VColors.OnPrimary),
                        )
                    }
                }

                Spacer(Modifier.height(56.dp))

                AnimatedVisibility(
                    visible = animStep >= 2,
                    enter = fadeIn(tween(VMotion.DurMedium2)) +
                        slideInVertically(tween(VMotion.DurLong2)) { it / 2 },
                ) {
                    Column {
                        Text(
                            text = "The OS for",
                            style = VTypography.LandingHeadline.copy(color = VColors.OnPrimary.copy(alpha = 0.7f)),
                        )
                        VGradientText(
                            text = "your campus",
                            style = VTypography.LandingHeadlineAccent,
                            colors = listOf(VColors.LiveCyan, VColors.OnPrimary),
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                AnimatedVisibility(
                    visible = animStep >= 3,
                    enter = fadeIn(tween(VMotion.DurMedium2)) +
                        slideInVertically(tween(VMotion.DurLong2)) { it / 2 },
                ) {
                    Text(
                        text = "Attendance, fees, results, and conversations — connecting your office, teachers, and every parent in real time.",
                        style = VTypography.LandingSub.copy(color = VColors.OnPrimary.copy(alpha = 0.75f)),
                        modifier = Modifier.padding(end = 32.dp),
                    )
                }
            }
        }

        // ── Role selection ─────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
                .padding(top = 40.dp),
        ) {
            AnimatedVisibility(
                visible = animStep >= 4,
                enter = fadeIn(tween(VMotion.DurMedium2)) +
                    slideInVertically(tween(VMotion.DurLong2)) { it / 2 },
            ) {
                Column {
                    Text(
                        text = "Get started",
                        style = VTypography.RolesTitle.copy(color = VColors.OnSurface),
                    )
                    Spacer(Modifier.height(20.dp))

                    RoleCard(
                        title = "I'm a Parent",
                        subtitle = "Track your child's progress, fees, and school updates",
                        icon = Icons.Filled.Favorite,
                        iconTint = VColors.OnPrimary,
                        cardBg = listOf(VColors.Primary, VColors.PrimaryMid),
                        iconBg = VColors.GlassWhite15,
                        onClick = onParent,
                    )

                    Spacer(Modifier.height(14.dp))

                    RoleCard(
                        title = "School / Staff",
                        subtitle = "Admin dashboard, attendance, reports & communication",
                        icon = Icons.Filled.AdminPanelSettings,
                        iconTint = VColors.OnTertiary,
                        cardBg = listOf(VColors.TertiaryContainer, VColors.SurfaceContainerHigh),
                        iconBg = VColors.Tertiary.copy(alpha = 0.12f),
                        onClick = onAdmin,
                    )
                }
            }

            Spacer(Modifier.height(40.dp))

            // ── Footer ──────────────────────────────────────────────────────
            AnimatedVisibility(
                visible = animStep >= 5,
                enter = fadeIn(tween(VMotion.DurMedium2)),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        LegalLink("Privacy Policy") { onLegal("privacy") }
                        Text("  ·  ", style = VTypography.LandingTerms.copy(color = VColors.Outline))
                        LegalLink("Terms of Service") { onLegal("terms") }
                        Text("  ·  ", style = VTypography.LandingTerms.copy(color = VColors.Outline))
                        LegalLink("Help Desk") { onLegal("help") }
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "By continuing you agree to our Terms of Service and Privacy Policy.",
                        style = VTypography.LandingTerms.copy(color = VColors.OnSurfaceVariant),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun RoleCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    cardBg: List<Color>,
    iconBg: Color,
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.Xl)
            .shapeMorph(interaction, VShapes.XlDp, VShapes.LgDp, VMotion.DurMedium1)
            .background(Brush.linearGradient(cardBg))
            .pressScale(interaction, pressedScale = 0.97f)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(26.dp))
        }
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = title,
                style = VTypography.FeatureTitle.copy(color = VColors.OnSurface),
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = VTypography.FeatureSubtitle.copy(color = VColors.OnSurfaceVariant),
            )
        }
        Icon(
            Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = VColors.OnSurfaceVariant,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun LegalLink(text: String, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    Text(
        text = text,
        style = VTypography.LandingTerms.copy(color = VColors.Primary),
        modifier = Modifier
            .pressScale(interaction, pressedScale = 0.95f)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
    )
}
