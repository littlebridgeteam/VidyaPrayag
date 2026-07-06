package com.littlebridge.enrollplus.ui.screens.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.components.VButton
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography

@Composable
fun UnlinkedParentGate(
    onLinkChild: () -> Unit,
    onDiscoverSchools: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VColors.cream)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(VColors.violetSoft, VShapes.full),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.Link,
                contentDescription = null,
                tint = VColors.violet,
                modifier = Modifier.size(36.dp),
            )
        }
        Spacer(Modifier.height(20.dp))
        Text(
            text = "Link Your Child",
            style = VTypography.h2,
            color = VColors.ink,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Link your child's school account to see attendance, marks, fees, and messages.",
            style = VTypography.body,
            color = VColors.ink2,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(32.dp))
        VButton(
            text = "Link Child",
            onClick = onLinkChild,
            icon = Icons.Rounded.Link,
        )
        Spacer(Modifier.height(12.dp))
        VButton(
            text = "Discover Schools",
            onClick = onDiscoverSchools,
            variant = com.littlebridge.enrollplus.ui.components.VButtonVariant.Outline,
            icon = Icons.Rounded.Search,
        )
    }
}
