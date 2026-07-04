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
import com.littlebridge.enrollplus.feature.alumni.presentation.AlumniViewModel
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VTopTabs
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.v2.screens.VSectionHeader
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.theme.VTheme
import com.littlebridge.enrollplus.ui.v2.theme.colored
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AlumniDetailScreen(
    alumniId: String,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: AlumniViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    val profileLabel = appString(StringKeys.SCH_PROFILE)
    var subTab by remember { mutableStateOf(profileLabel) }

    LaunchedEffect(alumniId) {
        viewModel.loadAlumniDetail(alumniId)
        viewModel.loadAlumniDonations(alumniId)
    }

    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
    ) {
        VBackHeader(title = appString(StringKeys.SCH_ALUMNI_DETAIL), onBack = onBack)

        val a = state.selectedAlumni
        VStateHost(
            loading = state.isDetailLoading,
            error = state.error,
            isEmpty = a == null,
            emptyTitle = appString(StringKeys.SCH_ALUMNI_NOT_FOUND),
            onRetry = { viewModel.loadAlumniDetail(alumniId) },
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
                                Text(data.name, style = VTheme.type.h2, color = VTheme.colors.ink)
                                Text(appString(StringKeys.SCH_BATCH_YEAR, "year" to data.graduationYear), style = VTheme.type.body, color = VTheme.colors.ink3)
                                if (data.isFeatured) Text(appString(StringKeys.SCH_FEATURED), style = VTheme.type.caption, color = VTheme.colors.accent)
                                if (data.verificationStatus != "approved") {
                                    Text(appString(StringKeys.SCH_STATUS_COLON, "status" to data.verificationStatus), style = VTheme.type.caption, color = VTheme.colors.danger)
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
                                        Text(career.jobTitle, style = VTheme.type.body, fontWeight = FontWeight.SemiBold, color = VTheme.colors.ink)
                                        Text(career.company, style = VTheme.type.body, color = VTheme.colors.ink3)
                                        career.industry?.let { Text(it, style = VTheme.type.caption, color = VTheme.colors.ink3) }
                                        val dateRange = buildString {
                                            career.startDate?.let { append(it) }
                                            append(" — ")
                                            if (career.isCurrent) append(appString(StringKeys.SCH_PRESENT)) else career.endDate?.let { append(it) }
                                        }
                                        Text(dateRange, style = VTheme.type.caption, color = VTheme.colors.ink3)
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
                        val donations = state.selectedAlumniDonations
                        VStateHost(
                            loading = state.areDonationsLoading,
                            error = null,
                            isEmpty = donations.isNullOrEmpty(),
                            emptyTitle = appString(StringKeys.SCH_NO_DONATIONS_RECORDED),
                        ) {
                            donations.forEach { donation ->
                                VCard(modifier = Modifier.fillMaxWidth()) {
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text("₹${donation.amount.toInt()}", style = VTheme.type.body, fontWeight = FontWeight.SemiBold, color = VTheme.colors.ink)
                                        Text(appString(StringKeys.SCH_DATE_COLON, "date" to donation.donationDate), style = VTheme.type.caption, color = VTheme.colors.ink3)
                                        donation.campaignTitle?.let { Text(appString(StringKeys.SCH_CAMPAIGN_COLON, "title" to it), style = VTheme.type.caption, color = VTheme.colors.ink3) }
                                        donation.paymentMode?.let { Text(appString(StringKeys.SCH_MODE_COLON, "mode" to it), style = VTheme.type.caption, color = VTheme.colors.ink3) }
                                        if (donation.is80gEligible) {
                                            Text(appString(StringKeys.SCH_80G_ELIGIBLE_RECEIPT, "receipt" to (donation.receiptNumber ?: appString(StringKeys.SCH_PENDING))), style = VTheme.type.caption, color = VTheme.colors.accent)
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
            Text(label, style = VTheme.type.caption, color = VTheme.colors.ink3)
            Text(value, style = VTheme.type.body, color = VTheme.colors.ink)
        }
    }
}
