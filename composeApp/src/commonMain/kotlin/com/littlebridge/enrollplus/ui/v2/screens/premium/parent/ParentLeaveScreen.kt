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
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
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
import com.littlebridge.enrollplus.ui.v2.components.carousel.VStaggeredItem
import com.littlebridge.enrollplus.ui.v2.components.form.VTextInput
import com.littlebridge.enrollplus.ui.v2.components.typography.VSectionHeader
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
import com.littlebridge.enrollplus.ui.v2.modifiers.shapeMorph
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VMotion
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

@Composable
fun ParentLeaveScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedLeaveType by remember { mutableStateOf(0) }
    val leaveTypes = listOf("Sick", "Casual", "Other")

    var fromDate by remember { mutableStateOf("") }
    var toDate by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }

    ParentOverlayScaffold(title = "Apply for Leave", onBack = onBack, modifier = modifier) {
        // Leave application form card
        VStaggeredItem(delayMs = 0) {
            Column(
                Modifier.fillMaxWidth().clip(VShapes.Xl).background(VColors.SurfaceContainerLowest).padding(24.dp),
            ) {
                Text("Leave Application", style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.Bold))
                Spacer(Modifier.height(16.dp))

                VTextInput(value = fromDate, onValueChange = { fromDate = it }, label = "From Date", placeholder = "Select date", authStyle = false, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(16.dp))
                VTextInput(value = toDate, onValueChange = { toDate = it }, label = "To Date", placeholder = "Select date", authStyle = false, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(16.dp))

                // Leave type selector
                Text("Leave Type", style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant, fontWeight = FontWeight.SemiBold))
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    leaveTypes.forEachIndexed { index, type ->
                        val isActive = selectedLeaveType == index
                        val chipInteraction = remember { MutableInteractionSource() }
                        Box(
                            Modifier
                                .clip(VShapes.Full)
                                .background(if (isActive) VColors.Primary else VColors.SurfaceContainerHigh)
                                .pressScale(chipInteraction, pressedScale = 0.94f)
                                .clickable(
                                    interactionSource = chipInteraction,
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

                VTextInput(value = reason, onValueChange = { reason = it }, label = "Reason", placeholder = "Enter reason…", authStyle = false, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(24.dp))
                VPrimaryButton(text = "Submit Application", onClick = { /* TODO: submit leave application */ }, modifier = Modifier.fillMaxWidth())
            }
        }
        Spacer(Modifier.height(20.dp))

        // Leave History
        VStaggeredItem(delayMs = 80) {
            VSectionHeader("Leave History")
        }
        Spacer(Modifier.height(12.dp))
        VStaggeredItem(delayMs = 120) {
            LeaveHistoryRow("Sick Leave", "Feb 20-21, 2026 · 2 days", "Approved")
        }
        Spacer(Modifier.height(8.dp))
        VStaggeredItem(delayMs = 160) {
            LeaveHistoryRow("Casual Leave", "Jan 15, 2026 · 1 day", "Approved")
        }
        Spacer(Modifier.height(8.dp))
        VStaggeredItem(delayMs = 200) {
            LeaveHistoryRow("Sick Leave", "Dec 10-12, 2025 · 3 days", "Rejected")
        }
    }
}

@Composable
private fun LeaveHistoryRow(type: String, dates: String, status: String) {
    val isApproved = status == "Approved"
    val statusColor = if (isApproved) VColors.Tertiary else VColors.Error
    val interaction = remember { MutableInteractionSource() }
    Row(
        Modifier.fillMaxWidth().clip(VShapes.Lg).background(VColors.SurfaceContainerLowest)
            .pressScale(interaction, pressedScale = 0.98f)
            .shapeMorph(interaction, VShapes.LgDp, VShapes.XlDp, VMotion.DurShort2)
            .clickable(interactionSource = interaction, indication = null) { /* TODO: view leave detail */ }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(type, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.Bold))
            Text(dates, style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant))
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            if (isApproved) {
                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = statusColor, modifier = Modifier.size(14.dp))
            }
            Text(status, style = VTypography.NavLabel.copy(color = statusColor, fontWeight = FontWeight.Bold))
        }
    }
}
