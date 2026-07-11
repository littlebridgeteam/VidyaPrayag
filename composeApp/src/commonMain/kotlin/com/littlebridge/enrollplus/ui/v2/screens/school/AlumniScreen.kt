package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.alumni.domain.model.*
import com.littlebridge.enrollplus.feature.alumni.presentation.AlumniScreenState
import com.littlebridge.enrollplus.feature.alumni.presentation.AlumniViewModel
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VBottomSheet
import com.littlebridge.enrollplus.ui.v2.components.VBottomSheetHeader
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonSize
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VInput
import com.littlebridge.enrollplus.ui.v2.components.VTopTabs
import com.littlebridge.enrollplus.ui.v2.screens.VSectionHeader
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.SkeletonList
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.theme.staggeredItemEntrance
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

private enum class AlumniTab {
    Directory, Pending, Campaigns, Donations, Mentorship, Analytics;

    @Composable
    fun label(): String = when (this) {
        Directory  -> appString(StringKeys.ALM_TAB_DIRECTORY)
        Pending    -> appString(StringKeys.ALM_TAB_PENDING)
        Campaigns  -> appString(StringKeys.ALM_TAB_CAMPAIGNS)
        Donations  -> appString(StringKeys.ALM_TAB_DONATIONS)
        Mentorship -> appString(StringKeys.ALM_TAB_MENTORSHIP)
        Analytics  -> appString(StringKeys.ALM_TAB_ANALYTICS)
    }
}

@Composable
fun AlumniScreen(
    onBack: () -> Unit = {},
    onOpenAlumni: (String) -> Unit = {},
    onOpenCampaign: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: AlumniViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    var subTab by remember { mutableStateOf(AlumniTab.Directory) }

    LaunchedEffect(Unit) {
        viewModel.loadAlumni()
        viewModel.loadCampaigns()
        viewModel.loadAnalytics()
        viewModel.loadPendingVerifications()
    }

    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
    ) {
        VBackHeader(title = appString(StringKeys.ALM_TITLE), onBack = onBack)

        val subTabLabels = AlumniTab.entries.map { it.label() }
        VTopTabs(
            tabs = subTabLabels,
            selected = subTabLabels[subTab.ordinal],
            onSelect = { label ->
                subTab = AlumniTab.entries[subTabLabels.indexOf(label)]
                when (subTab) {
                    AlumniTab.Directory -> viewModel.loadAlumni()
                    AlumniTab.Pending -> viewModel.loadPendingVerifications()
                    AlumniTab.Campaigns -> viewModel.loadCampaigns()
                    AlumniTab.Donations -> viewModel.loadDonations()
                    AlumniTab.Mentorship -> {
                        viewModel.loadMentorships()
                        viewModel.loadMentorshipRequests()
                    }
                    AlumniTab.Analytics -> viewModel.loadAnalytics()
                }
            },
        )

        when (subTab) {
            AlumniTab.Directory -> AlumniDirectoryTab(
                state = state,
                onOpenAlumni = onOpenAlumni,
                onRetry = { viewModel.loadAlumni() },
                onAddAlumni = { viewModel.createAlumni(it) },
                onBulkImport = { viewModel.bulkImport(it) },
            )
            AlumniTab.Pending -> AlumniPendingTab(
                state = state,
                onApprove = { id -> viewModel.verifyAlumni(id, "approve") },
                onDecline = { id -> viewModel.verifyAlumni(id, "decline") },
                onRetry = { viewModel.loadPendingVerifications() },
            )
            AlumniTab.Campaigns -> AlumniCampaignsTab(
                state = state,
                onOpenCampaign = onOpenCampaign,
                onRetry = { viewModel.loadCampaigns() },
            )
            AlumniTab.Donations -> AlumniDonationsTab(
                state = state,
                onRetry = { viewModel.loadDonations() },
            )
            AlumniTab.Mentorship -> AlumniMentorshipTab(
                state = state,
                onRetry = {
                    viewModel.loadMentorships()
                    viewModel.loadMentorshipRequests()
                },
            )
            AlumniTab.Analytics -> AlumniAnalyticsTab(
                state = state,
                onRetry = { viewModel.loadAnalytics() },
            )
        }
    }
}

@Composable
private fun AlumniDirectoryTab(
    state: AlumniScreenState,
    onOpenAlumni: (String) -> Unit,
    onRetry: () -> Unit,
    onAddAlumni: (CreateAlumniRequest) -> Unit,
    onBulkImport: (List<CreateAlumniRequest>) -> Unit,
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            VButton(
                text = appString(StringKeys.ALM_ADD_ALUMNI),
                onClick = { showAddDialog = true },
                variant = VButtonVariant.Primary,
                size = VButtonSize.Sm,
                leading = { Icon(VIcons.Users, contentDescription = null, modifier = Modifier.size(14.dp)) },
            )
            VButton(
                text = appString(StringKeys.ALM_BULK_IMPORT),
                onClick = { showImportDialog = true },
                variant = VButtonVariant.Secondary,
                size = VButtonSize.Sm,
                leading = { Icon(VIcons.Upload, contentDescription = null, modifier = Modifier.size(14.dp)) },
            )
        }

        Spacer(Modifier.height(12.dp))

        state.infoMessage?.let {
            VCard(modifier = Modifier.fillMaxWidth()) {
                Text(it, style = VTypography.body, color = VColors.violet)
            }
            Spacer(Modifier.height(8.dp))
        }
    }

    VStateHost(
        loading = state.isLoading,
        error = state.error,
        isEmpty = state.alumni.isEmpty(),
        emptyTitle = appString(StringKeys.ALM_NO_ALUMNI),
        emptyBody = appString(StringKeys.ALM_NO_ALUMNI_BODY),
        onRetry = onRetry,
        skeleton = { SkeletonList(rows = 5, withAvatar = true) },
    ) {
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            state.alumni.forEachIndexed { index, alumni ->
                AlumniRowCard(alumni = alumni, onClick = { onOpenAlumni(alumni.id) }, modifier = Modifier.staggeredItemEntrance(index, state.alumni.isNotEmpty()))
            }
        }
    }

    if (showAddDialog) {
        AddAlumniSheet(
            loading = state.isLoading,
            onDismiss = { showAddDialog = false },
            onSubmit = { request ->
                onAddAlumni(request)
                showAddDialog = false
            },
        )
    }

    if (showImportDialog) {
        BulkImportSheet(
            loading = state.isLoading,
            onDismiss = { showImportDialog = false },
            onSubmit = { rows ->
                onBulkImport(rows)
                showImportDialog = false
            },
        )
    }
}

@Composable
private fun AddAlumniSheet(
    loading: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (CreateAlumniRequest) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var graduationYear by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var profession by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf("") }

    val canSubmit = name.isNotBlank() && graduationYear.toIntOrNull() != null

    VBottomSheet(
        visible = true,
        onDismiss = onDismiss,
    ) {
        VBottomSheetHeader(title = appString(StringKeys.ALM_ADD_ALUMNI))
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {

                VInput(
                    value = name,
                    onValueChange = { name = it },
                    label = appString(StringKeys.ALM_FULL_NAME_REQ),
                    placeholder = appString(StringKeys.ALM_NAME_PH),
                )
                VInput(
                    value = graduationYear,
                    onValueChange = { graduationYear = it.filter { ch -> ch.isDigit() }.take(4) },
                    label = appString(StringKeys.ALM_GRAD_YEAR_REQ),
                    placeholder = appString(StringKeys.ALM_GRAD_YEAR_PH),
                    keyboardType = KeyboardType.Number,
                )
                VInput(
                    value = studentId,
                    onValueChange = { studentId = it },
                    label = appString(StringKeys.ALM_STUDENT_ID_OPT),
                    placeholder = appString(StringKeys.ALM_STUDENT_ID_PH),
                )
                VInput(
                    value = email,
                    onValueChange = { email = it },
                    label = appString(StringKeys.ALM_EMAIL),
                    placeholder = appString(StringKeys.ALM_EMAIL_PH),
                    keyboardType = KeyboardType.Email,
                )
                VInput(
                    value = phone,
                    onValueChange = { phone = it },
                    label = appString(StringKeys.ALM_PHONE),
                    placeholder = appString(StringKeys.ALM_PHONE_PH),
                    keyboardType = KeyboardType.Phone,
                )
                VInput(
                    value = profession,
                    onValueChange = { profession = it },
                    label = appString(StringKeys.ALM_PROFESSION),
                    placeholder = appString(StringKeys.ALM_PROFESSION_PH),
                )
                VInput(
                    value = company,
                    onValueChange = { company = it },
                    label = appString(StringKeys.ALM_COMPANY),
                    placeholder = appString(StringKeys.ALM_COMPANY_PH),
                )
                VInput(
                    value = city,
                    onValueChange = { city = it },
                    label = appString(StringKeys.ALM_CITY),
                    placeholder = appString(StringKeys.ALM_CITY_PH),
                )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                VButton(
                    text = appString(StringKeys.COMMON_BUTTON_CANCEL),
                    onClick = onDismiss,
                    variant = VButtonVariant.Ghost,
                    size = VButtonSize.Sm,
                    modifier = Modifier.weight(1f),
                )
                VButton(
                    text = appString(StringKeys.ALM_ADD),
                    onClick = {
                        onSubmit(
                            CreateAlumniRequest(
                                studentId = studentId.ifBlank { null },
                                name = name.trim(),
                                graduationYear = graduationYear.toInt(),
                                email = email.ifBlank { null },
                                phone = phone.ifBlank { null },
                                currentProfession = profession.ifBlank { null },
                                company = company.ifBlank { null },
                                city = city.ifBlank { null },
                            ),
                        )
                    },
                    variant = VButtonVariant.Primary,
                    size = VButtonSize.Sm,
                    modifier = Modifier.weight(1f),
                    enabled = canSubmit,
                    loading = loading,
                )
            }
        }
    }
}

@Composable
private fun BulkImportSheet(
    loading: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (List<CreateAlumniRequest>) -> Unit,
) {
    var csvText by remember { mutableStateOf("") }

    val rows = parseAlumniCsv(csvText)
    val canSubmit = rows.isNotEmpty()

    VBottomSheet(
        visible = true,
        onDismiss = onDismiss,
    ) {
        VBottomSheetHeader(title = appString(StringKeys.ALM_BULK_IMPORT_TITLE))
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                appString(StringKeys.ALM_BULK_IMPORT_INSTR),
                style = VTypography.caption,
                color = VColors.ink3,
            )

            OutlinedTextField(
                value = csvText,
                onValueChange = { csvText = it },
                modifier = Modifier.fillMaxWidth().height(160.dp),
                placeholder = { Text(appString(StringKeys.ALM_CSV_PH)) },
                textStyle = VTypography.body,
            )

            if (rows.isNotEmpty()) {
                Text(appString(StringKeys.ALM_ROWS_READY, "count" to rows.size), style = VTypography.caption, color = VColors.violet)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                VButton(
                    text = appString(StringKeys.COMMON_BUTTON_CANCEL),
                    onClick = onDismiss,
                    variant = VButtonVariant.Ghost,
                    size = VButtonSize.Sm,
                    modifier = Modifier.weight(1f),
                )
                VButton(
                    text = if (rows.isNotEmpty()) appString(StringKeys.ALM_IMPORT_WITH_COUNT, "count" to rows.size) else appString(StringKeys.ALM_IMPORT),
                    onClick = { onSubmit(rows) },
                    variant = VButtonVariant.Primary,
                    size = VButtonSize.Sm,
                    modifier = Modifier.weight(1f),
                    enabled = canSubmit,
                    loading = loading,
                )
            }
        }
    }
}

private fun parseAlumniCsv(text: String): List<CreateAlumniRequest> {
    return text.lineSequence()
        .map { it.trim() }
        .filter { it.isNotBlank() && !it.startsWith("#") }
        .mapNotNull { line ->
            val parts = line.split(",").map { it.trim() }
            if (parts.size < 2) return@mapNotNull null
            val name = parts[0]
            val year = parts[1].toIntOrNull() ?: return@mapNotNull null
            if (name.isBlank()) return@mapNotNull null
            CreateAlumniRequest(
                name = name,
                graduationYear = year,
                email = parts.getOrNull(2)?.ifBlank { null },
                phone = parts.getOrNull(3)?.ifBlank { null },
                currentProfession = parts.getOrNull(4)?.ifBlank { null },
                company = parts.getOrNull(5)?.ifBlank { null },
                city = parts.getOrNull(6)?.ifBlank { null },
            )
        }
        .toList()
}

@Composable
private fun AlumniPendingTab(
    state: AlumniScreenState,
    onApprove: (String) -> Unit,
    onDecline: (String) -> Unit,
    onRetry: () -> Unit,
) {
    VStateHost(
        loading = state.isLoading,
        error = state.error,
        isEmpty = state.pendingVerifications.isEmpty(),
        emptyTitle = appString(StringKeys.ALM_NO_PENDING),
        emptyBody = appString(StringKeys.ALM_NO_PENDING_BODY),
        onRetry = onRetry,
        skeleton = { SkeletonList(rows = 4, withAvatar = true) },
    ) {
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            state.pendingVerifications.forEachIndexed { index, alumni ->
                VCard(modifier = Modifier.fillMaxWidth().staggeredItemEntrance(index, state.pendingVerifications.isNotEmpty())) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(alumni.name, style = VTypography.body, fontWeight = FontWeight.SemiBold, color = VColors.ink)
                        Text(appString(StringKeys.ALM_BATCH, "year" to alumni.graduationYear), style = VTypography.caption, color = VColors.ink3)
                        alumni.email?.let { Text(it, style = VTypography.caption, color = VColors.ink3) }
                        alumni.phone?.let { Text(it, style = VTypography.caption, color = VColors.ink3) }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                            VChipButton(text = appString(StringKeys.ALM_APPROVE), onClick = { onApprove(alumni.id) })
                            VChipButton(text = appString(StringKeys.ALM_DECLINE), onClick = { onDecline(alumni.id) }, isDestructive = true)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AlumniCampaignsTab(
    state: AlumniScreenState,
    onOpenCampaign: (String) -> Unit,
    onRetry: () -> Unit,
) {
    VStateHost(
        loading = state.isLoading,
        error = state.error,
        isEmpty = state.campaigns.isEmpty(),
        emptyTitle = appString(StringKeys.ALM_NO_CAMPAIGNS),
        emptyBody = appString(StringKeys.ALM_NO_CAMPAIGNS_BODY),
        onRetry = onRetry,
        skeleton = { SkeletonList(rows = 4) },
    ) {
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            state.campaigns.forEachIndexed { index, campaign ->
                VCard(
                    modifier = Modifier.fillMaxWidth().staggeredItemEntrance(index, state.campaigns.isNotEmpty()),
                    onClick = { onOpenCampaign(campaign.id) },
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(campaign.title, style = VTypography.body, fontWeight = FontWeight.SemiBold, color = VColors.ink)
                        campaign.description?.let {
                            Text(it, style = VTypography.caption, color = VColors.ink3, maxLines = 2)
                        }
                        val progress = if (campaign.targetAmount > 0) {
                            (campaign.amountRaised / campaign.targetAmount * 100).toInt()
                        } else 0
                        Text(
                            appString(StringKeys.ALM_CAMPAIGN_PROGRESS, "raised" to campaign.amountRaised.toInt(), "target" to campaign.targetAmount.toInt(), "pct" to progress, "donors" to campaign.donorCount),
                            style = VTypography.caption,
                            color = VColors.ink3,
                        )
                        Text(appString(StringKeys.ALM_STATUS, "status" to campaign.status), style = VTypography.caption, color = VColors.ink3)
                    }
                }
            }
        }
    }
}

@Composable
private fun AlumniDonationsTab(
    state: AlumniScreenState,
    onRetry: () -> Unit,
) {
    VStateHost(
        loading = state.isLoading,
        error = state.error,
        isEmpty = state.donations.isEmpty(),
        emptyTitle = appString(StringKeys.ALM_NO_DONATIONS),
        emptyBody = appString(StringKeys.ALM_NO_DONATIONS_BODY),
        onRetry = onRetry,
        skeleton = { SkeletonList(rows = 5) },
    ) {
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            state.donations.forEachIndexed { index, donation ->
                VCard(modifier = Modifier.fillMaxWidth().staggeredItemEntrance(index, state.donations.isNotEmpty())) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(donation.alumniName, style = VTypography.body, fontWeight = FontWeight.SemiBold, color = VColors.ink)
                        Text("₹${donation.amount.toInt()}", style = VTypography.body, color = VColors.ink)
                        donation.campaignTitle?.let { Text(appString(StringKeys.ALM_CAMPAIGN_LABEL, "title" to it), style = VTypography.caption, color = VColors.ink3) }
                        Text(appString(StringKeys.ALM_DATE, "date" to donation.donationDate), style = VTypography.caption, color = VColors.ink3)
                        if (donation.is80gEligible) {
                            Text(appString(StringKeys.ALM_80G_ELIGIBLE, "receipt" to (donation.receiptNumber ?: appString(StringKeys.ALM_RECEIPT_PENDING))), style = VTypography.caption, color = VColors.ink3)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AlumniAnalyticsTab(
    state: AlumniScreenState,
    onRetry: () -> Unit,
) {
    val analytics = state.analytics
    VStateHost(
        loading = state.isLoading,
        error = state.error,
        isEmpty = analytics == null,
        emptyTitle = appString(StringKeys.ALM_NO_ANALYTICS),
        onRetry = onRetry,
        skeleton = { SkeletonList(rows = 6) },
    ) {
        val a = analytics!!
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            VSectionHeader(appString(StringKeys.ALM_OVERVIEW))
            AnalyticsStatCard(label = appString(StringKeys.ALM_TOTAL_ALUMNI), value = a.totalAlumni.toString())
            AnalyticsStatCard(label = appString(StringKeys.ALM_ACTIVE_90), value = a.activeAlumni.toString())
            AnalyticsStatCard(label = appString(StringKeys.ALM_PENDING_VERIFICATIONS), value = a.pendingVerifications.toString())
            AnalyticsStatCard(label = appString(StringKeys.ALM_ENGAGEMENT_RATE), value = "${(a.engagementRate * 100).toInt() / 100.0}%")
            AnalyticsStatCard(label = appString(StringKeys.ALM_TOTAL_DONATIONS), value = "₹${a.totalDonations.toInt()}")
            AnalyticsStatCard(label = appString(StringKeys.ALM_ACTIVE_CAMPAIGNS), value = a.activeCampaigns.toString())
            AnalyticsStatCard(label = appString(StringKeys.ALM_ACTIVE_MENTORSHIPS), value = a.activeMentorships.toString())

            if (a.byGraduationYear.isNotEmpty()) {
                VSectionHeader(appString(StringKeys.ALM_BY_GRAD_YEAR))
                a.byGraduationYear.forEach { (year, count) ->
                    AnalyticsStatCard(label = year, value = count.toString())
                }
            }

            if (a.byProfession.isNotEmpty()) {
                VSectionHeader(appString(StringKeys.ALM_BY_PROFESSION))
                a.byProfession.forEach { (profession, count) ->
                    AnalyticsStatCard(label = profession, value = count.toString())
                }
            }

            if (a.byCity.isNotEmpty()) {
                VSectionHeader(appString(StringKeys.ALM_BY_CITY))
                a.byCity.forEach { (city, count) ->
                    AnalyticsStatCard(label = city, value = count.toString())
                }
            }
        }
    }
}

@Composable
private fun AlumniMentorshipTab(
    state: AlumniScreenState,
    onRetry: () -> Unit,
) {
        Column(
        Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Active mentorships
        VSectionHeader(appString(StringKeys.ALM_ACTIVE_MENTORSHIPS))
        VStateHost(
            loading = state.isLoading,
            error = state.error,
            isEmpty = state.mentorships.isEmpty(),
            emptyTitle = appString(StringKeys.ALM_NO_MENTORSHIPS),
            emptyBody = appString(StringKeys.ALM_NO_MENTORSHIPS_BODY),
            onRetry = onRetry,
            skeleton = { SkeletonList(rows = 3) },
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                state.mentorships.forEachIndexed { index, m ->
                    VCard(modifier = Modifier.fillMaxWidth().staggeredItemEntrance(index, state.mentorships.isNotEmpty())) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(m.alumniName, style = VTypography.body, fontWeight = FontWeight.SemiBold, color = VColors.ink)
                            Text(appString(StringKeys.ALM_MENTORING, "name" to m.studentName), style = VTypography.caption, color = VColors.ink3)
                            Text(appString(StringKeys.ALM_STATUS, "status" to m.status), style = VTypography.caption, color = VColors.ink3)
                            Text(appString(StringKeys.ALM_STARTED, "date" to m.startDate), style = VTypography.caption, color = VColors.ink3)
                            if (m.sessionCount > 0) {
                                Text(appString(StringKeys.ALM_SESSIONS, "count" to m.sessionCount), style = VTypography.caption, color = VColors.ink3)
                            }
                            m.notes?.let { Text(appString(StringKeys.ALM_NOTES, "notes" to it), style = VTypography.caption, color = VColors.ink3) }
                        }
                    }
                }
            }
        }

        // Pending requests
        VSectionHeader(appString(StringKeys.ALM_MENTORSHIP_REQUESTS))
        VStateHost(
            loading = state.isLoading,
            error = state.error,
            isEmpty = state.mentorshipRequests.isEmpty(),
            emptyTitle = appString(StringKeys.ALM_NO_MENTOR_REQUESTS),
            emptyBody = appString(StringKeys.ALM_NO_MENTOR_REQUESTS_BODY),
            onRetry = onRetry,
            skeleton = { SkeletonList(rows = 3) },
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                state.mentorshipRequests.forEachIndexed { index, r ->
                    VCard(modifier = Modifier.fillMaxWidth().staggeredItemEntrance(index, state.mentorshipRequests.isNotEmpty())) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(r.alumniName, style = VTypography.body, fontWeight = FontWeight.SemiBold, color = VColors.ink)
                            Text(appString(StringKeys.ALM_FROM, "name" to r.studentName), style = VTypography.caption, color = VColors.ink3)
                            Text(appString(StringKeys.ALM_REQUESTED_BY, "name" to r.requestedByName), style = VTypography.caption, color = VColors.ink3)
                            r.expertiseArea?.let { Text(appString(StringKeys.ALM_EXPERTISE, "area" to it), style = VTypography.caption, color = VColors.ink3) }
                            r.message?.let { Text(appString(StringKeys.ALM_MESSAGE, "msg" to it), style = VTypography.caption, color = VColors.ink3) }
                            Text(appString(StringKeys.ALM_STATUS, "status" to r.status), style = VTypography.caption, color = VColors.ink3)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AlumniRowCard(alumni: Alumni, onClick: () -> Unit, modifier: Modifier = Modifier) {
    VCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(alumni.name, style = VTypography.body, fontWeight = FontWeight.SemiBold, color = VColors.ink)
                if (alumni.isFeatured) {
                    Text("★", style = VTypography.caption, color = VColors.violet)
                }
            }
            Text(appString(StringKeys.ALM_BATCH, "year" to alumni.graduationYear), style = VTypography.caption, color = VColors.ink3)
            alumni.currentProfession?.let {
                Text("$it${alumni.company?.let { c -> " @ $c" }}", style = VTypography.caption, color = VColors.ink3)
            }
            alumni.city?.let { Text(it, style = VTypography.caption, color = VColors.ink3) }
            if (alumni.isMentor) {
                Text(alumni.mentorExpertise?.let { e -> appString(StringKeys.ALM_MENTOR_EXPERTISE, "area" to e) } ?: appString(StringKeys.ALM_MENTOR), style = VTypography.caption, color = VColors.violet)
            }
        }
    }
}

@Composable
private fun AnalyticsStatCard(label: String, value: String) {
    VCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(label, style = VTypography.body, color = VColors.ink3)
            Text(value, style = VTypography.body, fontWeight = FontWeight.SemiBold, color = VColors.ink)
        }
    }
}

@Composable
private fun VChipButton(text: String, onClick: () -> Unit, isDestructive: Boolean = false) {
        VCard(
        onClick = onClick,
        padding = 8.dp,
    ) {
        Text(
            text,
            style = VTypography.caption,
            fontWeight = FontWeight.Medium,
            color = if (isDestructive) VColors.error else VColors.violet,
        )
    }
}
