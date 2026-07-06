package com.littlebridge.enrollplus.ui.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.screens.admin.components.AdminColors
import com.littlebridge.enrollplus.ui.screens.admin.components.AdminTypography

// ═══════════════════════════════════════════════════════════════
// StatusBar — 317×30dp, space-between, padding:12/32/4
// "9:41" + "●●● 5G ▮"
// ═══════════════════════════════════════════════════════════════

@Composable
fun StatusBar(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(30.dp)
            .padding(start = 32.dp, end = 32.dp, top = 12.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "9:41",
            color = AdminColors.inkPrimary,
            style = AdminTypography.statusBar
        )
        Text(
            text = "●●● 5G ▮",
            color = AdminColors.inkPrimary,
            style = AdminTypography.statusBarIcons
        )
    }
}
