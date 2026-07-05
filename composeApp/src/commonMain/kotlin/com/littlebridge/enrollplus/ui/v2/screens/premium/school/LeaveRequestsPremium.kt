package com.littlebridge.enrollplus.ui.v2.screens.premium.school

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.admin.presentation.LeaveRequestsViewModel
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.cards.VListTilePremium
import com.littlebridge.enrollplus.ui.v2.components.cards.VStatCardPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VStateHostPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VShimmerListPremium
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LeaveRequestsPremium(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: LeaveRequestsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .navigationBarsPadding(),
    ) {
        VBackHeader(title = "Leave Requests", onBack = onBack)

        VStateHostPremium(
            loading = state.isLoading,
            error = state.errorMessage,
            isEmpty = state.requests.isEmpty() && !state.isLoading,
            emptyTitle = "No leave requests",
            skeleton = {
                Column(
                    Modifier.fillMaxSize().padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    VShimmerListPremium(itemCount = 4)
                }
            },
        ) {
            Column(
                Modifier.verticalScroll(rememberScrollState()).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                VStatCardPremium(
                    value = "${state.approvalRate}%",
                    label = "Approval Rate",
                    onClick = {},
                    icon = VIcons.Check,
                )

                Text("Requests", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                state.requests.forEach { req ->
                    VListTilePremium(
                        title = req.requesterName,
                        subtitle = "${req.dateRange} - ${req.reason}",
                        onClick = {},
                        leadingIcon = VIcons.Calendar,
                        trailingText = req.status,
                    )
                }
            }
        }
    }
}
