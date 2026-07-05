package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.components.navigation.VBackHeader
import com.littlebridge.enrollplus.ui.v2.tokens.PremiumTheme
import com.littlebridge.enrollplus.ui.v2.tokens.VColors

/**
 * ParentOverlayScaffold — full-screen overlay scaffold with back header + scrollable content.
 *
 * Layout safety (Rule 5): the content area uses [weight][androidx.compose.foundation.layout.weight]
 * so the header is always visible and the scroll area fills the remaining space — no
 * `fillMaxSize` inside the scroll, no nested verticalScroll.
 */
@Composable
fun ParentOverlayScaffold(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    trailingIcon: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) = PremiumTheme(isDark = false) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VColors.Surface)
            .statusBarsPadding(),
    ) {
        VBackHeader(
            title = title,
            onBack = onBack,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = trailingIcon,
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
            ) {
                content()
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}
