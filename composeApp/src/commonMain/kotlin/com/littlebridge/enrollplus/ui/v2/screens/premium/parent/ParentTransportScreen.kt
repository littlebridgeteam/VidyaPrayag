package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

@Composable
fun ParentTransportScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    childId: String = "",
) {
    ParentOverlayScaffold(title = "Transport Tracking", onBack = onBack, modifier = modifier) {
        Row(
            Modifier.fillMaxWidth().clip(VShapes.Lg).background(VColors.PrimaryContainer).padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(Modifier.size(48.dp).clip(CircleShape).background(VColors.Primary), contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.LocalTaxi, contentDescription = null, tint = VColors.OnPrimary, modifier = Modifier.size(24.dp))
            }
            Column(Modifier.weight(1f)) {
                Text("Bus Route 12", style = VTypography.UpdateTitle.copy(color = VColors.OnPrimaryContainer, fontWeight = FontWeight.SemiBold))
                Text("Driver: Ramesh Kumar · +91 98XXX XXXXX", style = VTypography.NavLabel.copy(color = VColors.OnPrimaryContainer.copy(alpha = 0.7f)))
            }
        }
        Spacer(Modifier.height(16.dp))
        Text("Live tracking will appear here when the bus is on route.", style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
    }
}
