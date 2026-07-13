package com.littlebridge.enrollplus.ui.v2.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography

/**
 * VOfflineBanner — a slim dismissible banner shown when [isOffline] is true.
 * Indicates the user is viewing cached/stale data because the server is unreachable.
 */
@Composable
fun VOfflineBanner(
    isOffline: Boolean,
    modifier: Modifier = Modifier,
) {
    if (!isOffline) return
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(VColors.goldSoft)
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = VIcons.AlertCircle,
            contentDescription = null,
            tint = VColors.gold,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = "Offline — showing cached data",
            style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold),
            color = VColors.ink,
        )
    }
}

/**
 * VBackOnlineBanner — a brief green confirmation shown when the app transitions
 * from offline→online. Auto-dismissed by the caller after ~2.5s.
 */
@Composable
fun VBackOnlineBanner(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(VColors.mintSoft)
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = VIcons.CheckCircle,
            contentDescription = null,
            tint = VColors.mint,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = "Back online — data refreshed",
            style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold),
            color = VColors.success,
        )
    }
}

/**
 * VStaleChip — a compact inline indicator shown within a tab's content area
 * when that tab's data is stale (served from cache). Unlike [VOfflineBanner]
 * which is portal-level, this is per-tab and non-intrusive.
 */
@Composable
fun VStaleChip(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(VColors.goldSoft)
            .padding(horizontal = 8.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            imageVector = VIcons.AlertCircle,
            contentDescription = null,
            tint = VColors.gold,
            modifier = Modifier.size(12.dp),
        )
        Text(
            text = "Cached",
            style = VTypography.caption.copy(fontWeight = FontWeight.Medium),
            color = VColors.ink,
        )
    }
}
