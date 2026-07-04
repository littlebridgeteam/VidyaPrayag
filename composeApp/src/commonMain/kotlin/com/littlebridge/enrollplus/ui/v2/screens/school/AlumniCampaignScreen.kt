package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.alumni.presentation.AlumniViewModel
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.screens.VSectionHeader
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.theme.VTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AlumniCampaignScreen(
    campaignId: String,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: AlumniViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()

    LaunchedEffect(campaignId) {
        viewModel.loadCampaignDetail(campaignId)
    }

    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
    ) {
        VBackHeader(title = "Campaign Detail", onBack = onBack)

        val c = state.selectedCampaign
        VStateHost(
            loading = state.isCampaignLoading,
            error = state.error,
            isEmpty = c == null,
            emptyTitle = "Campaign not found",
            onRetry = { viewModel.loadCampaignDetail(campaignId) },
        ) {
            val data = c!!
            Column(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                VCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(data.title, style = VTheme.type.h2, color = VTheme.colors.ink)
                        data.description?.let {
                            Text(it, style = VTheme.type.body, color = VTheme.colors.ink3)
                        }
                        data.cause?.let { Text("Cause: $it", style = VTheme.type.caption, color = VTheme.colors.ink3) }
                        Text("Status: ${data.status}", style = VTheme.type.caption, color = VTheme.colors.ink3)
                        Text("Period: ${data.startDate}${data.endDate?.let { e -> " → $e" }}", style = VTheme.type.caption, color = VTheme.colors.ink3)
                        data.targetBatchYear?.let { Text("Target Batch: $it", style = VTheme.type.caption, color = VTheme.colors.ink3) }
                    }
                }

                VSectionHeader("Progress")
                VCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        val progress = if (data.targetAmount > 0) {
                            (data.amountRaised / data.targetAmount * 100).toInt()
                        } else 0
                        Text(
                            "₹${data.amountRaised.toInt()} / ₹${data.targetAmount.toInt()} ($progress%)",
                            style = VTheme.type.body,
                            fontWeight = FontWeight.SemiBold,
                            color = VTheme.colors.ink,
                        )
                        Text("${data.donorCount} donors", style = VTheme.type.caption, color = VTheme.colors.ink3)
                    }
                }

                VSectionHeader("Donations")
                val d = state.campaignDonations
                VStateHost(
                    loading = false,
                    error = null,
                    isEmpty = d.isNullOrEmpty(),
                    emptyTitle = "No donations yet for this campaign",
                ) {
                    d!!.forEach { donation ->
                        VCard(modifier = Modifier.fillMaxWidth()) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(donation.alumniName, style = VTheme.type.body, fontWeight = FontWeight.SemiBold, color = VTheme.colors.ink)
                                Text("₹${donation.amount.toInt()}", style = VTheme.type.body, color = VTheme.colors.ink)
                                Text("Date: ${donation.donationDate}", style = VTheme.type.caption, color = VTheme.colors.ink3)
                                donation.paymentMode?.let { Text("Mode: $it", style = VTheme.type.caption, color = VTheme.colors.ink3) }
                                if (donation.is80gEligible) {
                                    Text("80G • Receipt: ${donation.receiptNumber ?: "Pending"}", style = VTheme.type.caption, color = VTheme.colors.accent)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
