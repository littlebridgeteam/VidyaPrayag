package com.littlebridge.enrollplus.ui.v2.screens.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.CircularProgressIndicator
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
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.screens.parent.PremiumOverlayHeader
import org.koin.compose.viewmodel.koinViewModel
import com.littlebridge.enrollplus.ui.v2.theme.VTheme
import androidx.compose.foundation.shape.RoundedCornerShape

/**
 * ParentFeePaymentScreenV2 — premium pay-now overlay for the Fees tab.
 *
 * Shows the current outstanding balance and a payment method placeholder.
 * The actual payment call is left to the caller via [onPay] (real backend integration
 * lives in [FeeViewModel] / server).
 */
@Composable
fun ParentFeePaymentScreenV2(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FeeViewModel = koinViewModel(),
    onPay: () -> Unit = {},
) {
    val state by viewModel.state.collectAsStateV2()
    val outstanding = state.outstandingFees
    val hasOutstanding = outstanding.trim() != "$0" && outstanding.trim() != "₹0" && outstanding.isNotBlank()

    Column(
        modifier
            .fillMaxSize()
            .background(VTheme.colors.cream),
    ) {
        PremiumOverlayHeader(title = "Pay Fees", onBack = onBack)

        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Outstanding balance hero
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(VTheme.colors.violet)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    "Outstanding Amount",
                    style = VTheme.type.body,
                    color = VTheme.colors.white.copy(alpha = 0.85f),
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    outstanding,
                    style = VTheme.type.h2.copy(fontSize = 32.sp),
                    color = VTheme.colors.white,
                )
                if (state.overdueCount > 0) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "${state.overdueCount} overdue fee head(s)",
                        style = VTheme.type.caption,
                        color = VTheme.colors.gold,
                    )
                }
            }

            // Payment method placeholder card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(VTheme.colors.surfaceCard)
                    .padding(20.dp),
            ) {
                Text(
                    "Payment Method",
                    style = VTheme.type.body.copy(fontWeight = FontWeight.SemiBold),
                    color = VTheme.colors.ink,
                )
                Spacer(Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(
                        Modifier.size(40.dp).clip(CircleShape).background(VTheme.colors.violetSoft),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Filled.Payment,
                            contentDescription = "",
                            tint = VTheme.colors.violet,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    Column {
                        Text(
                            "Online Payment",
                            style = VTheme.type.body.copy(fontWeight = FontWeight.SemiBold),
                            color = VTheme.colors.ink,
                        )
                        Text(
                            "Secure Razorpay gateway",
                            style = VTheme.type.caption,
                            color = VTheme.colors.ink2,
                        )
                    }
                }
            }

            if (state.isLoading) {
                Box(Modifier.fillMaxWidth().height(56.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = VTheme.colors.violet, modifier = Modifier.size(28.dp))
                }
            }

            Spacer(Modifier.weight(1f))

            // Pay button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50))
                    .background(if (hasOutstanding) VTheme.colors.violet else VTheme.colors.lineSoft)
                    .clickable(enabled = hasOutstanding, onClick = onPay)
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    if (hasOutstanding) "Pay $outstanding" else "No fees due",
                    style = VTheme.type.body.copy(fontWeight = FontWeight.SemiBold),
                    color = if (hasOutstanding) VTheme.colors.white else VTheme.colors.ink3,
                )
            }
        }
    }
}
