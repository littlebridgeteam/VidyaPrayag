package com.littlebridge.enrollplus.ui.v2.tokens

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

/**
 * M3 Expressive motion tokens — from HTML :root and @keyframes.
 */
object VMotion {

    val EaseEmphasized: Easing = FastOutSlowInEasing
    val EaseStandard: Easing = FastOutSlowInEasing

    const val DurShort2 = 150
    const val DurShort3 = 200
    const val DurMedium1 = 250
    const val DurMedium2 = 300
    const val DurLong1 = 400
    const val DurLong2 = 500

    // slideUp: translateY(24→0) + opacity(0→1)
    const val SlideUpFromY = 24f
    const val SlideUpDuration = DurMedium2

    // springIn: scale(0.9→1) + translateY(16→0) + opacity(0→1)
    const val SpringInScaleFrom = 0.9f
    const val SpringInTranslateFrom = 16f
    const val SpringInDuration = DurLong2

    // Staggered entrance delays (ms) — from tab-page:nth-child rules
    val StaggeredDelays = intArrayOf(0, 30, 60, 100, 150, 200, 250, 300)

    // auth-flow anim-1 through anim-6
    val AuthAnimDelays = intArrayOf(100, 200, 300, 400, 500, 600)
    const val AuthAnimDuration = 600

    // livePulse: box-shadow ring 4dp→10dp, 2s infinite
    const val LivePulseDuration = 2000
    const val LivePulseRingStart = 4f
    const val LivePulseRingEnd = 10f
    const val LivePulseAlphaStart = 0.2f
    const val LivePulseAlphaEnd = 0.05f

    // liveBlink: opacity 1→0.3, 1.5s infinite
    const val LiveBlinkDuration = 1500

    // liBounce: scaleY(0.3→1) + radius change, 1s infinite
    const val LiBounceDuration = 1000
    const val LiBarDelayStep = 100

    // floatGlow: opacity 0.35→0.5, 6s/8s infinite
    const val FloatGlowDuration1 = 6000
    const val FloatGlowDuration2 = 8000
    const val FloatGlowDelay2 = 2000
    const val FloatGlowAlphaStart = 0.35f
    const val FloatGlowAlphaEnd = 0.5f

    // FAB menu stagger
    val FabMenuDelays = intArrayOf(0, 50, 100)

    // Page transition
    const val PageTransitionDuration = DurLong2
    const val PageExitOffset = 0.3f // translateX(-30%)
}

/**
 * Infinite pulse animation for live dots.
 * Returns [ringScale, ringAlpha] driven by a 2s infinite transition.
 */
@Composable
fun rememberLivePulse(): Pair<Float, Float> {
    val transition = rememberInfiniteTransition(label = "livePulse")
    val ringScale by transition.animateFloat(
        initialValue = VMotion.LivePulseRingStart,
        targetValue = VMotion.LivePulseRingEnd,
        animationSpec = infiniteRepeatable(
            tween(VMotion.LivePulseDuration / 2, easing = VMotion.EaseEmphasized),
            RepeatMode.Reverse,
        ),
        label = "ringScale",
    )
    val ringAlpha by transition.animateFloat(
        initialValue = VMotion.LivePulseAlphaStart,
        targetValue = VMotion.LivePulseAlphaEnd,
        animationSpec = infiniteRepeatable(
            tween(VMotion.LivePulseDuration / 2, easing = VMotion.EaseEmphasized),
            RepeatMode.Reverse,
        ),
        label = "ringAlpha",
    )
    return ringScale to ringAlpha
}

/**
 * Infinite blink animation for live status dots.
 * Returns opacity float 1.0→0.3→1.0 over 1.5s.
 */
@Composable
fun rememberLiveBlink(): Float {
    val transition = rememberInfiniteTransition(label = "liveBlink")
    val opacity by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            tween(VMotion.LiveBlinkDuration / 2, easing = VMotion.EaseEmphasized),
            RepeatMode.Reverse,
        ),
        label = "blinkOpacity",
    )
    return opacity
}
