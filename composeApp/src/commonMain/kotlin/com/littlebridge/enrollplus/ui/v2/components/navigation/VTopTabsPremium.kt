package com.littlebridge.enrollplus.ui.v2.components.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

@Composable
fun VTopTabsPremium(
    tabs: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        tabs.forEach { tab ->
            val active = tab == selected
            val interaction = remember { MutableInteractionSource() }
            val bg by animateColorAsState(
                targetValue = if (active) VColors.OnSurface else VColors.SurfaceContainer,
                animationSpec = tween(220),
                label = "tabBg",
            )
            val fg by animateColorAsState(
                targetValue = if (active) VColors.Surface else VColors.OnSurfaceVariant,
                animationSpec = tween(220),
                label = "tabFg",
            )
            Text(
                text = tab,
                style = VTypography.Chip.copy(color = fg),
                modifier = Modifier
                    .clip(VShapes.Full)
                    .background(bg)
                    .pressScale(interaction, pressedScale = 0.93f)
                    .clickable(interactionSource = interaction, indication = null) { onSelect(tab) }
                    .padding(horizontal = 18.dp, vertical = 10.dp),
            )
        }
    }
}
