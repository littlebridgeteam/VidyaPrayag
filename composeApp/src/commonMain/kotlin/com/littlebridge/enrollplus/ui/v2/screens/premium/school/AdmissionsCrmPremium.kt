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
import com.littlebridge.enrollplus.feature.admin.presentation.AdmissionCRMViewModel
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
fun AdmissionsCrmPremium(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: AdmissionCRMViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    val isLoading by viewModel.isLoading.collectAsStateV2()
    val errorMessage by viewModel.errorMessage.collectAsStateV2()

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .navigationBarsPadding(),
    ) {
        VBackHeader(title = "Admissions CRM", onBack = onBack)

        VStateHostPremium(
            loading = isLoading,
            error = errorMessage,
            isEmpty = state.recentEnquiries.isEmpty() && !isLoading,
            emptyTitle = "No enquiries yet",
            skeleton = {
                Column(
                    Modifier.fillMaxSize().padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    VShimmerListPremium(itemCount = 5)
                }
            },
        ) {
            Column(
                Modifier.verticalScroll(rememberScrollState()).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                VStatCardPremium(value = "${state.totalEnquiries}", label = "Total Enquiries", onClick = {}, icon = VIcons.ClipboardList)
                VStatCardPremium(value = "${state.newEnquiries}", label = "New", onClick = {}, icon = VIcons.Sparkles)
                VStatCardPremium(value = "${state.followUps}", label = "Follow-ups", onClick = {}, icon = VIcons.Clock)
                VStatCardPremium(value = state.efficiencyLabel, label = "Efficiency", onClick = {}, icon = VIcons.TrendingUp)

                Text("Recent Enquiries", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                state.recentEnquiries.forEach { enq ->
                    VListTilePremium(
                        title = enq.studentName,
                        subtitle = "${enq.className} - Parent: ${enq.parentName}",
                        onClick = {},
                        leadingIcon = VIcons.Users,
                        trailingText = enq.status,
                    )
                }
            }
        }
    }
}
