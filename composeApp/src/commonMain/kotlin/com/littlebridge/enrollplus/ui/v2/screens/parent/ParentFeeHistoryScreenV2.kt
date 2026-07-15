package com.littlebridge.enrollplus.ui.v2.screens.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.feature.parent.presentation.FeeViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.screens.parent.VBackHeader
import org.koin.compose.viewmodel.koinViewModel
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.screens.teacher.TeacherKit.TeacherSpinner

/**
 * ParentFeeHistoryScreenV2 — premium fee history overlay for the Fees tab.
 *
 * Shows paid fees from [FeeState.outstandingFees] marked as paid, plus an empty state
 * when no payment history is available. Future work can add a dedicated history endpoint.
 */
@Composable
fun ParentFeeHistoryScreenV2(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FeeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    val totalCollected = state.totalCollected

    Column(
        modifier
            .fillMaxSize()
            .background(VColors.cream),
    ) {
        VBackHeader(title = "Fee History", onBack = onBack)

        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Summary hero
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(VShapes.lg)
                    .background(VColors.success)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    "Total Collected",
                    style = VTypography.body,
                    color = VColors.white.copy(alpha = 0.85f),
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    totalCollected,
                    style = VTypography.h2.copy(fontSize = 32.sp),
                    color = VColors.white,
                )
            }

            if (state.isLoading) {
                Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    androidx.compose.material3.TeacherSpinner(36.dp)
                }
            } else {
                EmptyHistoryCard()
            }
        }
    }
}

@Composable
private fun EmptyHistoryCard() {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.lg)
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier.size(56.dp).clip(CircleShape).background(VColors.creamDeep),
            contentAlignment = Alignment.Center,
        ) {
            Icon(VIcons.Wallet, contentDescription = null, tint = VColors.ink3, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.height(12.dp))
        Text(
            "No payment history",
            style = VTypography.body.copy(fontWeight = FontWeight.SemiBold),
            color = VColors.ink,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Once you pay fees, the receipts will show up here.",
            style = VTypography.caption,
            color = VColors.ink2,
        )
    }
}
