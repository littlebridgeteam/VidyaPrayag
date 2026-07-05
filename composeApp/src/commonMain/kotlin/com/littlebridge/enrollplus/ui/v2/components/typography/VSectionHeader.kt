package com.littlebridge.enrollplus.ui.v2.components.typography

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

/**
 * Section header — 24sp weight 800, optional "View all" link in primary.
 *
 * HTML: .section-header
 *   display: flex; justify-content: space-between; align-items: baseline;
 *   padding: 4px 24px 16px;
 *   h2 { font-size: 24px; font-weight: 800; letter-spacing: -0.03em; }
 *   .link { font-size: 14px; font-weight: 600; color: var(--primary); cursor: pointer; }
 */
@Composable
fun VSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    linkText: String? = null,
    onLinkClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        Text(
            text = title,
            style = VTypography.SectionHeader.copy(color = VColors.OnSurface),
        )
        if (linkText != null && onLinkClick != null) {
            val interaction = remember { MutableInteractionSource() }
            Text(
                text = linkText,
                style = VTypography.SectionLink.copy(color = VColors.Primary),
                modifier = Modifier
                    .clickable(interactionSource = interaction, indication = null, onClick = onLinkClick)
                    .padding(bottom = 2.dp),
            )
        }
    }
}
