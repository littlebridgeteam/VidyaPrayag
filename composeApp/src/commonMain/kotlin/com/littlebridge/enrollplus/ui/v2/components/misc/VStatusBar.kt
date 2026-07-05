package com.littlebridge.enrollplus.ui.v2.components.misc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

/**
 * Status bar — time + signal/wifi/battery SVG icons, 54dp height.
 *
 * HTML: .status-bar
 *   display: flex; justify-content: space-between; align-items: center;
 *   padding: 18px 32px 0; height: 54px;
 *   .status-time { font-size: 15px; font-weight: 700; letter-spacing: -0.01em; }
 *   .status-icons { display: flex; gap: 6px; align-items: center; }
 *   .status-icons svg { width: 18px; height: 18px; color: var(--on-surface); }
 *
 * Pass [light] = true for dark backgrounds (login hero, onboarding).
 */
@Composable
fun VStatusBar(
    modifier: Modifier = Modifier,
    time: String = "9:41",
    light: Boolean = false,
    signalIcon: (@Composable () -> Unit)? = null,
    wifiIcon: (@Composable () -> Unit)? = null,
    batteryIcon: (@Composable () -> Unit)? = null,
) {
    val fgColor = if (light) androidx.compose.ui.graphics.Color.White else VColors.OnSurface

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .padding(horizontal = 32.dp, vertical = 0.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = time,
            style = VTypography.StatusTime.copy(color = fgColor),
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (signalIcon != null) signalIcon()
            if (wifiIcon != null) wifiIcon()
            if (batteryIcon != null) batteryIcon()
        }
    }
}
