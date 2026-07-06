package com.littlebridge.enrollplus.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.littlebridge.enrollplus.ui.screens.admin.components.AdminColors
import com.littlebridge.enrollplus.ui.screens.admin.components.AdminShapes

// ═══════════════════════════════════════════════════════════════
// PhoneFrame — 331×716dp black phone with island + home bar
// padding:8, bg:black, radius:50, multi-layer shadow
// ═══════════════════════════════════════════════════════════════

@Composable
fun PhoneFrame(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .width(331.dp)
            .height(716.dp)
            .background(
                AdminColors.phoneBlack,
                RoundedCornerShape(50.dp)
            )
            .padding(8.dp)
    ) {
        // Dynamic Island — 85×24, top center
        Box(
            modifier = Modifier
                .width(85.dp)
                .height(24.dp)
                .align(Alignment.TopCenter)
                .background(AdminColors.phoneBlack, RoundedCornerShape(20.dp))
        )

        // Home bar — 102×3, bottom center
        Box(
            modifier = Modifier
                .width(102.dp)
                .height(3.dp)
                .align(Alignment.BottomCenter)
                .background(AdminColors.homeBarColor, RoundedCornerShape(4.dp))
        )

        content()
    }
}

// ═══════════════════════════════════════════════════════════════
// ScreenArea — 317×703dp, bg:rgb(251,248,244), radius:42
// Contains: StatusBar + PortalHeader + TabContent + BottomNav + Overlay
// ═══════════════════════════════════════════════════════════════

@Composable
fun ScreenArea(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .width(317.dp)
            .height(703.dp)
            .background(AdminColors.surfaceBase, RoundedCornerShape(42.dp))
    ) {
        content()
    }
}
