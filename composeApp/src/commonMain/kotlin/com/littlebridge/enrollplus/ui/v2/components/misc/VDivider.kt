package com.littlebridge.enrollplus.ui.v2.components.misc

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

/**
 * Divider — centered label with hairline lines on both sides.
 *
 * HTML: .divider
 *   display: flex; align-items: center; gap: 16px; margin: 24px 0;
 *   ::before, ::after { content: ''; flex: 1; height: 1px; background: var(--outline-variant); }
 *   span { font-size: 12px; font-weight: 600; text-transform: uppercase; letter-spacing: 0.06em; }
 */
@Composable
fun VDivider(
    label: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(0.5.dp))
                .background(VColors.OutlineVariant),
        )
        Text(
            text = label,
            style = VTypography.DividerLabel.copy(color = VColors.OnSurfaceVariant),
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(0.5.dp))
                .background(VColors.OutlineVariant),
        )
    }
}
