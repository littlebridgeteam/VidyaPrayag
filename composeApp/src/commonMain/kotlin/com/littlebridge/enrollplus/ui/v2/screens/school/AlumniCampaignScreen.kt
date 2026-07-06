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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.core.prefs.PreferenceRepository
import com.littlebridge.enrollplus.feature.alumni.domain.model.AlumniDonation
import com.littlebridge.enrollplus.feature.alumni.domain.model.AlumniDonationCampaign
import com.littlebridge.enrollplus.feature.alumni.domain.repository.AlumniRepository
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.screens.VSectionHeader
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.v2.theme.VTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun AlumniCampaignScreen(
    campaignId: String,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    repository: AlumniRepository = koinInject(),
    prefs: PreferenceRepository = koinInject(),
) {
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var campaign by remember { mutableStateOf<AlumniDonationCampaign?>(null) }
    var donations by remember { mutableStateOf<List<AlumniDonation>?>(null) }
    val scope = rememberCoroutineScope()
    val notSignedInError = appString(StringKeys.COMMON_ERROR_UNAUTHORIZED)
    val campaignNotFoundError = appString(StringKeys.SCH_CAMPAIGN_NOT_FOUND)

    LaunchedEffect(campaignId) {
        scope.launch {
            val token = prefs.getUserToken().first()
            if (token.isNullOrBlank()) { error = notSignedInError; isLoading = false; return@launch }
            val campaignResult = repository.getCampaign(token, campaignId)
            val donationsResult = repository.listDonations(token, campaignId)
            campaign = (campaignResult as? NetworkResult.Success)?.data?.data
            donations = (donationsResult as? NetworkResult.Success)?.data?.data ?: emptyList()
            isLoading = false
            if (campaign == null && error == null) {
                error = (campaignResult as? NetworkResult.Error)?.message ?: campaignNotFoundError
            }
        }
    }

    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
    ) {
        VBackHeader(title = appString(StringKeys.SCH_CAMPAIGN_DETAIL), onBack = onBack)

        val c = campaign
        VStateHost(
            loading = isLoading,
            error = error,
            isEmpty = c == null,
            emptyTitle = appString(StringKeys.SCH_CAMPAIGN_NOT_FOUND),
            onRetry = { isLoading = true; error = null },
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
                        data.cause?.let { Text(appString(StringKeys.SCH_CAUSE_COLON, "cause" to it), style = VTheme.type.caption, color = VTheme.colors.ink3) }
                        Text(appString(StringKeys.SCH_STATUS_COLON, "status" to data.status), style = VTheme.type.caption, color = VTheme.colors.ink3)
                        Text(appString(StringKeys.SCH_PERIOD_COLON, "start" to data.startDate, "end" to (data.endDate ?: "")), style = VTheme.type.caption, color = VTheme.colors.ink3)
                        data.targetBatchYear?.let { Text(appString(StringKeys.SCH_TARGET_BATCH_COLON, "batch" to it), style = VTheme.type.caption, color = VTheme.colors.ink3) }
                    }
                }

                VSectionHeader(appString(StringKeys.SCH_PROGRESS))
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
                        Text(appString(StringKeys.SCH_N_DONORS, "count" to data.donorCount.toString()), style = VTheme.type.caption, color = VTheme.colors.ink3)
                    }
                }

                VSectionHeader(appString(StringKeys.SCH_DONATIONS))
                val d = donations
                VStateHost(
                    loading = false,
                    error = null,
                    isEmpty = d.isNullOrEmpty(),
                    emptyTitle = appString(StringKeys.SCH_NO_DONATIONS_CAMPAIGN),
                ) {
                    d!!.forEach { donation ->
                        VCard(modifier = Modifier.fillMaxWidth()) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(donation.alumniName, style = VTheme.type.body, fontWeight = FontWeight.SemiBold, color = VTheme.colors.ink)
                                Text("₹${donation.amount.toInt()}", style = VTheme.type.body, color = VTheme.colors.ink)
                                Text(appString(StringKeys.SCH_DATE_COLON, "date" to donation.donationDate), style = VTheme.type.caption, color = VTheme.colors.ink3)
                                donation.paymentMode?.let { Text(appString(StringKeys.SCH_MODE_COLON, "mode" to it), style = VTheme.type.caption, color = VTheme.colors.ink3) }
                                if (donation.is80gEligible) {
                                    Text(appString(StringKeys.SCH_80G_RECEIPT, "receipt" to (donation.receiptNumber ?: appString(StringKeys.SCH_PENDING))), style = VTheme.type.caption, color = VTheme.colors.accent)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
