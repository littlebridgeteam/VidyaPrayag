package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.components.buttons.VPrimaryButton
import com.littlebridge.enrollplus.ui.v2.components.form.VTextInput
import com.littlebridge.enrollplus.ui.v2.components.typography.VSectionHeader
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

@Composable
fun ParentLeaveScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedLeaveType by remember { mutableStateOf(0) }
    val leaveTypes = listOf("Sick", "Casual", "Other")

    ParentOverlayScaffold(title = "Apply for Leave", onBack = onBack, modifier = modifier) {
        // Leave application form card
        Column(
            Modifier.fillMaxWidth().clip(VShapes.Xl).background(VColors.SurfaceContainerLowest).padding(24.dp),
        ) {
            Text("Leave Application", style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.Bold))
            Spacer(Modifier.height(16.dp))

            VTextInput(value = "", onValueChange = {}, label = "From Date", placeholder = "Select date", authStyle = false, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(16.dp))
            VTextInput(value = "", onValueChange = {}, label = "To Date", placeholder = "Select date", authStyle = false, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(16.dp))

            // Leave type selector
            Text("Leave Type", style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant, fontWeight = FontWeight.SemiBold))
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                leaveTypes.forEachIndexed { index, type ->
                    val isActive = selectedLeaveType == index
                    Box(
                        Modifier
                            .clip(VShapes.Full)
                            .background(if (isActive) VColors.Primary else VColors.SurfaceContainerHigh)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) { selectedLeaveType = index }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                    ) {
                        Text(
                            type,
                            style = VTypography.NavLabel.copy(
                                color = if (isActive) VColors.OnPrimary else VColors.OnSurfaceVariant,
                                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                            ),
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))

            VTextInput(value = "", onValueChange = {}, label = "Reason", placeholder = "Enter reason…", authStyle = false, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(24.dp))
            VPrimaryButton(text = "Submit Application", onClick = {}, modifier = Modifier.fillMaxWidth())
        }
        Spacer(Modifier.height(20.dp))

        // Leave History
        VSectionHeader("Leave History")
        Spacer(Modifier.height(12.dp))
        Row(
            Modifier.fillMaxWidth().clip(VShapes.Lg).background(VColors.SurfaceContainerLowest).padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text("Sick Leave", style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.Bold))
                Text("Feb 20-21, 2026 · 2 days", style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant))
            }
            Text("Approved", style = VTypography.NavLabel.copy(color = VColors.Tertiary, fontWeight = FontWeight.Bold))
        }
    }
}
