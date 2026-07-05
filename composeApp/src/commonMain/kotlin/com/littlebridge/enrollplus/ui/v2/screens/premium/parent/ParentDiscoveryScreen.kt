package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.components.buttons.VSecondaryButton
import com.littlebridge.enrollplus.ui.v2.tokens.PremiumTheme
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

/**
 * ParentDiscoveryScreen — school discovery screen for unauthenticated parents.
 *
 * Phase 1 placeholder — full implementation in later phase.
 */
@Composable
fun ParentDiscoveryScreen(
    onExit: () -> Unit,
    onOpenSchool: (String) -> Unit,
    modifier: Modifier = Modifier,
) = PremiumTheme(isDark = false) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VColors.Surface)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(60.dp))

        Box(
            modifier = Modifier.size(56.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Filled.Search,
                contentDescription = null,
                tint = VColors.Primary,
                modifier = Modifier.size(28.dp),
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Find your child's school",
            style = VTypography.SectionHeader.copy(color = VColors.OnSurface),
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Search by school name or location to get started.",
            style = VTypography.BodyLarge.copy(color = VColors.OnSurfaceVariant),
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(32.dp))

        VSecondaryButton(
            text = "Back",
            onClick = onExit,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
