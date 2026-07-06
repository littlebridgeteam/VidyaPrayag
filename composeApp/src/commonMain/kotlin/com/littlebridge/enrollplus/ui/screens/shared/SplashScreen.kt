package com.littlebridge.enrollplus.ui.screens.shared

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.tokens.VMotion
import kotlinx.coroutines.delay

/**
 * Premium splash — Enroll+ wordmark with a measured, confident reveal.
 *
 * Sequence (total ~1600ms):
 *   0ms      → logo fade-in + scale 0.96→1 (600ms ease)
 *   400ms    → accent line draws from 0 to 48dp (800ms ease)
 *   1200ms   → hold
 *   1600ms   → onTimeout
 *
 * No bouncy springs, no shimmer. Just a clean, timed reveal that matches
 * the auth prototype's CSS keyframes exactly.
 */
@Composable
fun SplashScreen(
    onTimeout: () -> Unit,
) {
    val logoAlpha = remember { Animatable(0f) }
    val logoScale = remember { Animatable(0.96f) }
    val accentWidth = remember { Animatable(0f) }
    val accentAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Phase 1 — logo reveal (0–600ms)
        launch { logoAlpha.animateTo(1f, tween(600, easing = VMotion.ease)) }
        launch { logoScale.animateTo(1f, tween(600, easing = VMotion.ease)) }

        // Phase 2 — accent line draw (400ms delay, 800ms duration)
        delay(400)
        launch { accentWidth.animateTo(1f, tween(800, easing = VMotion.ease)) }
        launch { accentAlpha.animateTo(1f, tween(800, easing = VMotion.ease)) }

        // Phase 3 — hold then hand off
        delay(400)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VColors.cream)
            .clickable { onTimeout() },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = buildAnnotatedString {
                    append("Enroll")
                    withStyle(SpanStyle(color = VColors.violet)) { append("+") }
                },
                style = VTypography.splashName,
                color = VColors.ink,
                modifier = Modifier
                    .alpha(logoAlpha.value)
                    .scale(logoScale.value),
            )
            Spacer(Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .height(2.dp)
                    .width(48.dp * accentWidth.value)
                    .alpha(accentAlpha.value)
                    .background(VColors.violet, RoundedCornerShape(2.dp)),
            )
        }
    }
}
