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
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.screens.parent.VBackHeader
import org.koin.compose.viewmodel.koinViewModel
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.screens.teacher.TeacherKit.TeacherSpinner
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.locale.appString

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
            .background(VColors.cream),
    ) {
        VBackHeader(title = appString(StringKeys.PFP_PAY_FEES), onBack = onBack)

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
                    .clip(VShapes.lg)
                    .background(VColors.violet)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    appString(StringKeys.PFP_OUTSTANDING),
                    style = VTypography.body,
                    color = VColors.white.copy(alpha = 0.85f),
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    outstanding,
                    style = VTypography.h2.copy(fontSize = 32.sp),
                    color = VColors.white,
                )
                if (state.overdueCount > 0) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        appString(StringKeys.PFP_OVERDUE_HEADS, "count" to state.overdueCount.toString()),
                        style = VTypography.caption,
                        color = VColors.gold,
                    )
                }
            }

            // Payment method placeholder card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(VShapes.lg)
                    .background(VColors.surfaceCard)
                    .padding(20.dp),
            ) {
                Text(
                    appString(StringKeys.PFP_PAYMENT_METHOD),
                    style = VTypography.body.copy(fontWeight = FontWeight.SemiBold),
                    color = VColors.ink,
                )
                Spacer(Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(
                        Modifier.size(40.dp).clip(CircleShape).background(VColors.violetSoft),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Filled.Payment,
                            contentDescription = null,
                            tint = VColors.violet,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    Column {
                        Text(
                            appString(StringKeys.PFP_ONLINE_PAYMENT),
                            style = VTypography.body.copy(fontWeight = FontWeight.SemiBold),
                            color = VColors.ink,
                        )
                        Text(
                            appString(StringKeys.PFP_SECURE_GATEWAY),
                            style = VTypography.caption,
                            color = VColors.ink2,
                        )
                    }
                }
            }

            if (state.isLoading) {
                Box(Modifier.fillMaxWidth().height(56.dp), contentAlignment = Alignment.Center) {
                    TeacherSpinner(28.dp)
                }
            }

            Spacer(Modifier.weight(1f))

            // Pay button
            VButton(
                text = if (hasOutstanding) appString(StringKeys.PFP_PAY_AMOUNT, "amount" to outstanding) else appString(StringKeys.PFP_NO_FEES_DUE),
                onClick = onPay,
                full = true,
                enabled = hasOutstanding,
                loading = state.isLoading,
            )
        }
    }
}
