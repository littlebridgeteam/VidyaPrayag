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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.core.prefs.PreferenceRepository
import com.littlebridge.enrollplus.feature.alumni.domain.model.Alumni
import com.littlebridge.enrollplus.feature.alumni.domain.repository.AlumniRepository
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VTopTabs
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.v2.screens.VSectionHeader
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import org.koin.compose.koinInject

@Composable
fun AlumniDetailScreen(
    alumniId: String,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    repository: AlumniRepository = koinInject(),
    prefs: PreferenceRepository = koinInject(),
) {
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var alumni by remember { mutableStateOf<Alumni?>(null) }
    val profileLabel = appString(StringKeys.SCH_PROFILE)
    var subTab by remember { mutableStateOf(profileLabel) }
    val scope = rememberCoroutineScope()
    val notSignedInError = appString(StringKeys.COMMON_ERROR_UNAUTHORIZED)
    val networkError = appString(StringKeys.COMMON_ERROR_NETWORK)

    LaunchedEffect(alumniId) {
        scope.launch {
            val token = prefs.getUserToken().first()
            if (token.isNullOrBlank()) { error = notSignedInError; isLoading = false; return@launch }
            when (val result = repository.getAlumni(token, alumniId)) {
                is NetworkResult.Success -> { alumni = result.data.data; isLoading = false }
                is NetworkResult.Error -> { error = result.message; isLoading = false }
                is NetworkResult.ConnectionError -> { error = networkError; isLoading = false }
            }
        }
    }

    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
    ) {
        VBackHeader(title = appString(StringKeys.SCH_ALUMNI_DETAIL), onBack = onBack)

        val a = alumni
        VStateHost(
            loading = isLoading,
            error = error,
            isEmpty = a == null,
            emptyTitle = appString(StringKeys.SCH_ALUMNI_NOT_FOUND),
            onRetry = { isLoading = true; error = null },
        ) {
            val data = a!!
            VTopTabs(
                tabs = listOf(appString(StringKeys.SCH_PROFILE), appString(StringKeys.SCH_CAREER), appString(StringKeys.SCH_DONATIONS)),
                selected = subTab,
                onSelect = { subTab = it },
            )

            when (subTab) {
                appString(StringKeys.SCH_PROFILE) -> {
                    Column(
                        Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        VCard(modifier = Modifier.fillMaxWidth()) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(data.name, style = VTypography.h2, color = VColors.ink)
                                Text(appString(StringKeys.SCH_BATCH_YEAR, "year" to data.graduationYear), style = VTypography.body, color = VColors.ink3)
                                if (data.isFeatured) Text(appString(StringKeys.SCH_FEATURED), style = VTypography.caption, color = VColors.violet)
                                if (data.verificationStatus != "approved") {
                                    Text(appString(StringKeys.SCH_STATUS_COLON, "status" to data.verificationStatus), style = VTypography.caption, color = VColors.error)
                                }
                            }
                        }

                        VSectionHeader(appString(StringKeys.SCH_CONTACT))
                        data.email?.let { DetailRow(appString(StringKeys.SCH_EMAIL), it) }
                        data.phone?.let { DetailRow(appString(StringKeys.SCH_PHONE), it) }
                        data.city?.let { DetailRow(appString(StringKeys.SCH_CITY), it) }
                        data.linkedinUrl?.let { DetailRow(appString(StringKeys.SCH_LINKEDIN), it) }

                        VSectionHeader(appString(StringKeys.SCH_PROFESSIONAL))
                        data.currentProfession?.let { DetailRow(appString(StringKeys.SCH_PROFESSION), it) }
                        data.company?.let { DetailRow(appString(StringKeys.SCH_COMPANY), it) }
                        data.skills?.let { DetailRow(appString(StringKeys.SCH_SKILLS), it) }
                        data.achievements?.let { DetailRow(appString(StringKeys.SCH_ACHIEVEMENTS), it) }

                        if (data.isMentor) {
                            VSectionHeader(appString(StringKeys.SCH_MENTORSHIP))
                            DetailRow(appString(StringKeys.SCH_MENTOR), appString(StringKeys.COMMON_YES))
                            data.mentorExpertise?.let { DetailRow(appString(StringKeys.SCH_EXPERTISE), it) }
                        }

                        VSectionHeader(appString(StringKeys.SCH_PRIVACY))
                        DetailRow(appString(StringKeys.SCH_VISIBILITY), data.visibilityLevel)
                        DetailRow(appString(StringKeys.SCH_SHOW_PHONE), if (data.showPhone) appString(StringKeys.COMMON_YES) else appString(StringKeys.COMMON_NO))
                        DetailRow(appString(StringKeys.SCH_SHOW_EMAIL), if (data.showEmail) appString(StringKeys.COMMON_YES) else appString(StringKeys.COMMON_NO))
                        DetailRow(appString(StringKeys.SCH_PROFILE_COMPLETENESS), "${data.profileCompleteness}%")
                    }
                }
                appString(StringKeys.SCH_CAREER) -> {
                    Column(
                        Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        if (data.careerHistory.isEmpty()) {
                            VStateHost(loading = false, error = null, isEmpty = true, emptyTitle = appString(StringKeys.SCH_NO_CAREER_HISTORY)) {}
                        } else {
                            data.careerHistory.forEach { career ->
                                VCard(modifier = Modifier.fillMaxWidth()) {
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(career.jobTitle, style = VTypography.body, fontWeight = FontWeight.SemiBold, color = VColors.ink)
                                        Text(career.company, style = VTypography.body, color = VColors.ink3)
                                        career.industry?.let { Text(it, style = VTypography.caption, color = VColors.ink3) }
                                        val dateRange = buildString {
                                            career.startDate?.let { append(it) }
                                            append(" — ")
                                            if (career.isCurrent) append(appString(StringKeys.SCH_PRESENT)) else career.endDate?.let { append(it) }
                                        }
                                        Text(dateRange, style = VTypography.caption, color = VColors.ink3)
                                    }
                                }
                            }
                        }
                    }
                }
                appString(StringKeys.SCH_DONATIONS) -> {
                    Column(
                        Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        var donations by remember { mutableStateOf<List<com.littlebridge.enrollplus.feature.alumni.domain.model.AlumniDonation>?>(null) }
                        var donationsLoading by remember { mutableStateOf(true) }
                        LaunchedEffect(alumniId) {
                            scope.launch {
                                val token = prefs.getUserToken().first()
                                if (token.isNullOrBlank()) { donationsLoading = false; return@launch }
                                when (val result = repository.getAlumniDonations(token, alumniId)) {
                                    is NetworkResult.Success -> { donations = result.data.data ?: emptyList(); donationsLoading = false }
                                    is NetworkResult.Error -> { donationsLoading = false }
                                    is NetworkResult.ConnectionError -> { donationsLoading = false }
                                }
                            }
                        }
                        VStateHost(
                            loading = donationsLoading,
                            error = null,
                            isEmpty = donations.isNullOrEmpty(),
                            emptyTitle = appString(StringKeys.SCH_NO_DONATIONS_RECORDED),
                        ) {
                            donations!!.forEach { donation ->
                                VCard(modifier = Modifier.fillMaxWidth()) {
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text("₹${donation.amount.toInt()}", style = VTypography.body, fontWeight = FontWeight.SemiBold, color = VColors.ink)
                                        Text(appString(StringKeys.SCH_DATE_COLON, "date" to donation.donationDate), style = VTypography.caption, color = VColors.ink3)
                                        donation.campaignTitle?.let { Text(appString(StringKeys.SCH_CAMPAIGN_COLON, "title" to it), style = VTypography.caption, color = VColors.ink3) }
                                        donation.paymentMode?.let { Text(appString(StringKeys.SCH_MODE_COLON, "mode" to it), style = VTypography.caption, color = VColors.ink3) }
                                        if (donation.is80gEligible) {
                                            Text(appString(StringKeys.SCH_80G_ELIGIBLE_RECEIPT, "receipt" to (donation.receiptNumber ?: appString(StringKeys.SCH_PENDING))), style = VTypography.caption, color = VColors.violet)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    VCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Text(label, style = VTypography.caption, color = VColors.ink3)
            Text(value, style = VTypography.body, color = VColors.ink)
        }
    }
}
