package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.core.state.SelectedChildHolder
import com.littlebridge.enrollplus.feature.idcard.domain.model.IdCardDto
import com.littlebridge.enrollplus.feature.idcard.presentation.IdCardViewModel
import com.littlebridge.enrollplus.ui.v2.components.misc.VShimmerBoxPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VStateHostPremium
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DigitalIdCardOverlay(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: IdCardViewModel = koinViewModel(),
    selectedChildHolder: SelectedChildHolder = koinInject(),
) {
    val state by viewModel.state.collectAsStateV2()
    val childId by selectedChildHolder.selectedChildId.collectAsStateV2()

    LaunchedEffect(childId) {
        childId?.let { viewModel.loadChildIdCard(it) }
    }

    ParentOverlayScaffold(
        title = "Digital ID Card",
        onBack = onBack,
        modifier = modifier,
    ) {
        VStateHostPremium(
            loading = state.isLoading,
            error = state.error,
            isEmpty = state.currentCard == null && !state.isLoading,
            modifier = Modifier.fillMaxSize(),
            emptyTitle = "No ID card issued",
            emptyIcon = Icons.Filled.Badge,
            onRetry = { childId?.let { viewModel.loadChildIdCard(it) } },
            skeleton = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    VShimmerBoxPremium(height = 220.dp, shape = VShapes.Xl)
                }
            },
        ) {
            val card = state.currentCard ?: return@VStateHostPremium
            IdCardDisplay(card = card)
        }
    }
}

@Composable
private fun IdCardDisplay(card: IdCardDto) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(VShapes.Xl)
                .background(VColors.SurfaceContainerLowest)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(VColors.PrimaryContainer),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Filled.Badge, contentDescription = null, tint = VColors.Primary, modifier = Modifier.size(16.dp))
                    }
                    Text(
                        text = "Student ID",
                        style = VTypography.UpdateTitle.copy(color = VColors.OnSurface),
                    )
                }
                val statusColor = if (card.status == "ready") VColors.Primary else VColors.WarmOrange
                Box(
                    modifier = Modifier
                        .clip(VShapes.Full)
                        .background(statusColor.copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = card.status,
                        style = VTypography.ThreadTime.copy(color = statusColor),
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Name
            Text(
                text = card.personName,
                style = VTypography.SectionHeader.copy(color = VColors.OnSurface),
            )

            Spacer(Modifier.height(8.dp))
            Text(
                text = "ID: ${card.personId}",
                style = VTypography.ThreadPreview.copy(color = VColors.OnSurfaceVariant),
            )

            if (!card.validTill.isNullOrBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Valid till: ${card.validTill}",
                    style = VTypography.ThreadTime.copy(color = VColors.Outline),
                )
            }

            Spacer(Modifier.height(24.dp))

            // QR placeholder
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(VShapes.Lg)
                    .background(VColors.SurfaceContainerHigh),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.QrCode, contentDescription = "QR Code", tint = VColors.OnSurfaceVariant, modifier = Modifier.size(64.dp))
            }

            Spacer(Modifier.height(12.dp))
            Text(
                text = card.qrCodeData,
                style = VTypography.ThreadTime.copy(color = VColors.Outline),
            )
        }
    }
}
