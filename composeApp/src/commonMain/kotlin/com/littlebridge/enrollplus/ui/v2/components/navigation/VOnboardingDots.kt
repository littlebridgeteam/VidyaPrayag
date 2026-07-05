package com.littlebridge.enrollplus.ui.v2.components.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

/**
 * Onboarding dots — active dot stretches to 28dp wide, inactive 8dp.
 *
 * HTML: .ob-dot
 *   width: 8px; height: 8px; border-radius: var(--shape-full);
 *   background: var(--surface-container-high);
 *   .active { width: 28px; background: var(--primary); }
 */
@Composable
fun VOnboardingDots(
    count: Int,
    activeIndex: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(count) { index ->
            val isActive = index == activeIndex
            val bg = if (isActive) VColors.Primary else VColors.SurfaceContainerHigh
            val w = if (isActive) 28.dp else 8.dp

            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .width(w)
                    .height(8.dp)
                    .clip(VShapes.Full)
                    .background(bg),
            )
        }
    }
}
