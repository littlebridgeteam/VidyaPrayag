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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.components.buttons.VPrimaryButton
import com.littlebridge.enrollplus.ui.v2.components.buttons.VSecondaryButton
import com.littlebridge.enrollplus.ui.v2.tokens.PremiumTheme
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

@Composable
fun ParentUnlinkedScreen(
    onLinked: () -> Unit,
    modifier: Modifier = Modifier,
) = PremiumTheme(isDark = false) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VColors.Surface)
            .statusBarsPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            Modifier.size(80.dp).clip(CircleShape).background(VColors.PrimaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.ChildCare, contentDescription = null, tint = VColors.OnPrimaryContainer, modifier = Modifier.size(40.dp))
        }
        Spacer(Modifier.height(24.dp))
        Text("No Child Linked Yet", style = VTypography.GreetingTitle.copy(color = VColors.OnSurface), textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text("Link your child to start tracking their progress, attendance, and school updates.", style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant), textAlign = TextAlign.Center)
        Spacer(Modifier.height(32.dp))
        VPrimaryButton(text = "Link Your Child", onClick = onLinked, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        VSecondaryButton(text = "Explore Schools", onClick = {}, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(32.dp))
    }
}
