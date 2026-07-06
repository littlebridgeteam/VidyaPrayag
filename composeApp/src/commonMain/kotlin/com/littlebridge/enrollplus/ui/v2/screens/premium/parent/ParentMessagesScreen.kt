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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.parent.presentation.ParentMessageViewModel
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ParentMessagesScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    initialThreadId: String? = null,
    viewModel: ParentMessageViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    ParentOverlayScaffold(title = "Messages", onBack = onBack, modifier = modifier) {
        if (state.loading) {
            StatusBox("Loading messages...")
        } else if (state.error != null) {
            StatusBox(state.error!!, isError = true)
        } else if (state.threads.isEmpty()) {
            StatusBox("No conversations yet")
        } else {
            state.threads.forEach { thread ->
                Row(
                    Modifier.fillMaxWidth().clip(VShapes.Lg).background(VColors.SurfaceContainerLow).padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(Modifier.size(44.dp).clip(CircleShape).background(VColors.PrimaryContainer), contentAlignment = Alignment.Center) {
                        Text(thread.senderName.firstOrNull()?.toString() ?: "?", style = VTypography.SectionHeader.copy(color = VColors.OnPrimaryContainer))
                    }
                    Column(Modifier.weight(1f)) {
                        Text(thread.senderName, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold))
                        Text(thread.lastMessage, style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant), maxLines = 1)
                    }
                    Text(thread.time, style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant))
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun StatusBox(msg: String, isError: Boolean = false) {
    Box(
        Modifier.fillMaxWidth().height(120.dp).clip(VShapes.Lg)
            .background(if (isError) VColors.ErrorContainer else VColors.SurfaceContainerLow),
        contentAlignment = Alignment.Center,
    ) {
        Text(msg, style = VTypography.UpdateText.copy(color = if (isError) VColors.OnErrorContainer else VColors.OnSurfaceVariant))
    }
}
