package com.littlebridge.enrollplus.ui.v2.components.misc

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes

/**
 * Phone frame — 412×892 phone mock with island, screen, status bar.
 *
 * HTML: .phone
 *   width: 412px; height: 892px; border-radius: 56px;
 *   background: linear-gradient(145deg, #1a1a24, #0D0B14); padding: 12px;
 *   box-shadow: 0 0 0 3px #252330, 0 0 0 4px #1a1a24, 0 60px 140px rgba(0,0,0,0.6),
 *               0 0 180px rgba(103,80,246,0.06);
 *
 *   .screen { border-radius: 44px; background: var(--surface); }
 *   .island { width: 120px; height: 36px; background: #000; border-radius: 22px; top: 12px; }
 */
@Composable
fun VPhoneFrame(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .width(412.dp)
            .height(892.dp)
            .clip(RoundedCornerShape(56.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(VColors.PhoneFrameBgStart, VColors.PhoneFrameBgEnd),
                ),
            )
            .padding(12.dp),
    ) {
        // Screen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .clip(RoundedCornerShape(44.dp))
                .background(VColors.Surface),
        ) {
            content()
            // Dynamic island
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 12.dp)
                    .width(120.dp)
                    .height(36.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(androidx.compose.ui.graphics.Color.Black),
            )
        }
    }
}
