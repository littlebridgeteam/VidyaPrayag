package com.littlebridge.enrollplus.ui.v2.components.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.rememberLivePulse

/**
 * Pulse dot — 8dp dot, animated ring expanding 4→10dp, 2s infinite.
 *
 * HTML: .hero-live-dot / .landing-eyebrow-dot
 *   width: 8px; height: 8px; border-radius: var(--shape-full);
 *   background: #00F5C4; box-shadow: 0 0 0 4px rgba(0,245,196,0.2);
 *   animation: livePulse 2s infinite;
 *   @keyframes livePulse { 0%,100% { box-shadow: 0 0 0 4px rgba(0,245,196,0.2); } 50% { box-shadow: 0 0 0 10px rgba(0,245,196,0.05); } }
 */
@Composable
fun VPulseDot(
    modifier: Modifier = Modifier,
    color: androidx.compose.ui.graphics.Color? = null,
) {
    val resolvedColor = color ?: VColors.LiveCyan
    val (ringScale, ringAlpha) = rememberLivePulse()
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        // Expanding ring
        Box(
            modifier = Modifier
                .size((ringScale * 2).dp)
                .clip(VShapes.Full)
                .background(resolvedColor.copy(alpha = ringAlpha)),
        )
        // Core dot
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(VShapes.Full)
                .background(resolvedColor),
        )
    }
}
