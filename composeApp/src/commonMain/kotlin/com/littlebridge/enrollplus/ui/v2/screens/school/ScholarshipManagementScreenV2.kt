package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.feature.scholarship.domain.model.ScholarshipScheme
import com.littlebridge.enrollplus.feature.scholarship.domain.model.UpdateSchemeRequest
import com.littlebridge.enrollplus.feature.scholarship.presentation.ScholarshipScreenState
import com.littlebridge.enrollplus.feature.scholarship.presentation.ScholarshipViewModel
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VBadge
import com.littlebridge.enrollplus.ui.v2.components.VBadgeTone
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonSize
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VDatePicker
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VInput
import com.littlebridge.enrollplus.ui.v2.screens.VSectionHeader
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.SkeletonList
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.theme.staggeredItemEntrance
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.locale.appString
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ScholarshipManagementScreenV2(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ScholarshipViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    var showSchemeForm by remember { mutableStateOf(false) }
    var showApplications by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) } // 0=schemes, 1=applications, 2=renewals
    var editingScheme by remember { mutableStateOf<ScholarshipScheme?>(null) }
    var deleteScheme by remember { mutableStateOf<ScholarshipScheme?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadSchemes()
        viewModel.loadApplications()
        viewModel.loadRenewals()
    }

    LaunchedEffect(state.infoMessage) {
        if (state.infoMessage != null) {
            showSchemeForm = false
            editingScheme = null
            viewModel.clearMessages()
        }
    }

    Column(
        modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        VBackHeader(title = appString(StringKeys.SCH_MGMT_TITLE), onBack = onBack, pinRouteId = "overlay_scholarships")

        // Tab selector
        ScholarshipTabRow(selectedTab = selectedTab, onTabSelected = { selectedTab = it })

        // Create button is always visible on Schemes tab (even when empty)
        if (selectedTab == 0 && !state.isLoading && state.error == null) {
            VButton(
                text = appString(StringKeys.SCH_CREATE_NEW),
                onClick = { showSchemeForm = true },
                variant = VButtonVariant.Primary,
                size = VButtonSize.Md,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }

        VStateHost(
            loading = state.isLoading,
            error = state.error,
            isEmpty = when (selectedTab) {
                0 -> state.schemes.isEmpty()
                1 -> state.applications.isEmpty()
                else -> state.renewals.isEmpty()
            },
            emptyTitle = when (selectedTab) {
                0 -> appString(StringKeys.SCH_NO_SCHEMES)
                1 -> appString(StringKeys.SCH_NO_APPLICATIONS)
                else -> appString(StringKeys.SCH_NO_RENEWALS)
            },
            emptyBody = when (selectedTab) {
                0 -> appString(StringKeys.SCH_NO_SCHEMES_BODY)
                1 -> appString(StringKeys.SCH_NO_APPLICATIONS_BODY)
                else -> appString(StringKeys.SCH_NO_RENEWALS_BODY)
            },
            onRetry = { viewModel.loadSchemes() },
            modifier = Modifier.fillMaxSize(),
            skeleton = { SkeletonList(rows = 5) },
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                when (selectedTab) {
                    0 -> {
                        itemsIndexed(state.schemes, key = { _, it -> it.id }) { index, scheme ->
                            ScholarshipSchemeCard(
                                scheme = scheme,
                                onEdit = { editingScheme = scheme },
                                onDelete = { deleteScheme = scheme },
                                modifier = Modifier.staggeredItemEntrance(index, state.schemes.isNotEmpty()),
                            )
                        }
                    }
                    1 -> {
                        item {
                            VSectionHeader(title = appString(StringKeys.SCH_APPLICATIONS).replace("{count}", state.applications.size.toString()))
                        }
                        itemsIndexed(state.applications, key = { _, it -> it.id }) { index, app ->
                            ScholarshipApplicationReviewCard(
                                application = app,
                                onApprove = { remarks, amount ->
                                    viewModel.approveApplication(
                                        app.id,
                                        com.littlebridge.enrollplus.feature.scholarship.domain.model.ApproveApplicationRequest(
                                            remarks = remarks,
                                            disbursementAmount = amount,
                                        )
                                    )
                                },
                                onReject = { remarks ->
                                    viewModel.rejectApplication(
                                        app.id,
                                        com.littlebridge.enrollplus.feature.scholarship.domain.model.RejectApplicationRequest(remarks)
                                    )
                                },
                                onDisburse = { amount, reference ->
                                    viewModel.disburse(
                                        app.id,
                                        com.littlebridge.enrollplus.feature.scholarship.domain.model.DisburseRequest(amount, reference)
                                    )
                                },
                                modifier = Modifier.staggeredItemEntrance(index, state.applications.isNotEmpty()),
                            )
                        }
                    }
                    2 -> {
                        item {
                            VSectionHeader(title = appString(StringKeys.SCH_RENEWALS).replace("{count}", state.renewals.size.toString()))
                        }
                        itemsIndexed(state.renewals, key = { _, it -> it.id }) { index, renewal ->
                            ScholarshipRenewalCard(
                                renewal = renewal,
                                onApprove = { remarks ->
                                    viewModel.approveRenewal(
                                        renewal.id,
                                        com.littlebridge.enrollplus.feature.scholarship.domain.model.ApproveRenewalRequest(remarks)
                                    )
                                },
                                onReject = { remarks ->
                                    viewModel.rejectRenewal(
                                        renewal.id,
                                        com.littlebridge.enrollplus.feature.scholarship.domain.model.RejectApplicationRequest(remarks)
                                    )
                                },
                                modifier = Modifier.staggeredItemEntrance(index, state.renewals.isNotEmpty()),
                            )
                        }
                    }
                }
            }
        }
    }

    // Create scheme form overlay
    AnimatedVisibility(
        visible = showSchemeForm,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
    ) {
        ScholarshipSchemeForm(
            onCreate = { request ->
                viewModel.createScheme(request)
            },
            onDismiss = { showSchemeForm = false },
        )
    }

    // Edit scheme form overlay
    AnimatedVisibility(
        visible = editingScheme != null,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
    ) {
        editingScheme?.let { scheme ->
            ScholarshipSchemeForm(
                existingScheme = scheme,
                onUpdate = { request ->
                    viewModel.updateScheme(scheme.id, request)
                },
                onDismiss = { editingScheme = null },
            )
        }
    }

    // Delete confirmation dialog
    if (deleteScheme != null) {
        DeleteConfirmationDialog(
            title = appString(StringKeys.SCH_DELETE_TITLE),
            message = appString(StringKeys.SCH_DELETE_MSG).replace("{title}", deleteScheme?.title ?: ""),
            onConfirm = {
                deleteScheme?.let { viewModel.deleteScheme(it.id) }
                deleteScheme = null
            },
            onDismiss = { deleteScheme = null },
        )
    }
}

@Composable
private fun ScholarshipTabRow(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        listOf(appString(StringKeys.SCH_SCHEMES), appString(StringKeys.SCH_TAB_APPLICATIONS), appString(StringKeys.SCH_TAB_RENEWALS)).forEachIndexed { index, label ->
            val isSelected = selectedTab == index
            Box(
                Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) VColors.violet else VColors.cream)
                    .clickable { onTabSelected(index) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    label,
                    style = VTypography.label,
                    color = if (isSelected) Color.White else VColors.ink3,
                )
            }
        }
    }
}

@Composable
private fun ScholarshipSchemeCard(
    scheme: ScholarshipScheme,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val typeTone = when (scheme.scholarshipType) {
        "full_waiver" -> VBadgeTone.Success
        "partial_waiver" -> VBadgeTone.Warning
        else -> VBadgeTone.Accent
    }
    VCard(modifier) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            VBadge(text = scheme.scholarshipType.replace("_", " "), tone = typeTone)
            if (scheme.isRenewable) {
                VBadge(text = appString(StringKeys.SCH_RENEWABLE), tone = VBadgeTone.Neutral)
            }
            if (!scheme.isActive) {
                VBadge(text = appString(StringKeys.SCH_INACTIVE), tone = VBadgeTone.Danger)
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(scheme.title, style = VTypography.h3, color = VColors.ink)
        if (scheme.description.isNotBlank()) {
            Spacer(Modifier.height(4.dp))
            Text(scheme.description, style = VTypography.body, color = VColors.ink2)
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(appString(StringKeys.SCH_AWARD), style = VTypography.label, color = VColors.ink3)
                Text(scheme.amount, style = VTypography.body.copy(fontWeight = FontWeight.SemiBold, fontSize = 22.sp), color = VColors.ink)
            }
            if (scheme.eligibilityCriteria.isNotBlank()) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(appString(StringKeys.SCH_ELIGIBILITY), style = VTypography.label, color = VColors.ink3)
                    Text(scheme.eligibilityCriteria, style = VTypography.caption, color = VColors.ink2)
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(Modifier.weight(1f)) {
                VButton(
                    text = appString(StringKeys.SCH_EDIT),
                    onClick = onEdit,
                    variant = VButtonVariant.Secondary,
                    size = VButtonSize.Sm,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Box(Modifier.weight(1f)) {
                VButton(
                    text = appString(StringKeys.SCH_DEACTIVATE),
                    onClick = onDelete,
                    variant = VButtonVariant.Destructive,
                    size = VButtonSize.Sm,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun ScholarshipApplicationReviewCard(
    application: com.littlebridge.enrollplus.feature.scholarship.domain.model.ScholarshipApplication,
    onApprove: (String, Double?) -> Unit,
    onReject: (String) -> Unit,
    onDisburse: (Double, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showActions by remember { mutableStateOf(false) }
    var remarks by remember { mutableStateOf("") }
    var disbursementAmount by remember { mutableStateOf("") }
    var disbursementReference by remember { mutableStateOf("") }

    val statusTone = when (application.status) {
        "PENDING" -> VBadgeTone.Warning
        "APPROVED" -> VBadgeTone.Success
        "REJECTED" -> VBadgeTone.Danger
        "DISBURSED" -> VBadgeTone.Accent
        else -> VBadgeTone.Neutral
    }

    VCard(modifier) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    application.scholarshipTitle ?: application.institution,
                    style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold), color = VColors.ink,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    application.studentName ?: appString(StringKeys.SCH_STUDENT),
                    style = VTypography.caption, color = VColors.ink3,
                )
            }
            VBadge(text = application.status, tone = statusTone)
        }

        val appText = application.parentApplicationText
        if (appText?.isNotBlank() == true) {
            Spacer(Modifier.height(8.dp))
            Text(appText, style = VTypography.caption, color = VColors.ink2)
        }

        if (application.documentUrls.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(appString(StringKeys.SCH_DOCUMENTS).replace("{count}", application.documentUrls.size.toString()), style = VTypography.label, color = VColors.ink3)
        }

        if (application.status == "PENDING") {
            Spacer(Modifier.height(12.dp))
            if (!showActions) {
                VButton(
                    text = appString(StringKeys.SCH_REVIEW),
                    onClick = { showActions = true },
                    variant = VButtonVariant.Secondary,
                    size = VButtonSize.Sm,
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                VInput(
                    value = remarks,
                    onValueChange = { remarks = it },
                    label = appString(StringKeys.SCH_REMARKS),
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(8.dp))
                VInput(
                    value = disbursementAmount,
                    onValueChange = { disbursementAmount = it.filter { it.isDigit() || it == '.' } },
                    label = appString(StringKeys.SCH_DISBURSEMENT_AMT),
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(Modifier.weight(1f)) {
                        VButton(
                            text = appString(StringKeys.SCH_APPROVE),
                            onClick = {
                                val amount = disbursementAmount.toDoubleOrNull()
                                onApprove(remarks, amount)
                                showActions = false
                            },
                            variant = VButtonVariant.Primary,
                            size = VButtonSize.Sm,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    Box(Modifier.weight(1f)) {
                        VButton(
                            text = appString(StringKeys.SCH_REJECT),
                            onClick = {
                                onReject(remarks)
                                showActions = false
                            },
                            variant = VButtonVariant.Secondary,
                            size = VButtonSize.Sm,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }

        if (application.status == "APPROVED") {
            Spacer(Modifier.height(12.dp))
            VInput(
                value = disbursementReference,
                onValueChange = { disbursementReference = it },
                label = appString(StringKeys.SCH_DISBURSEMENT_REF),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
            VButton(
                text = appString(StringKeys.SCH_RECORD_DISBURSEMENT),
                onClick = {
                    val amount = disbursementAmount.toDoubleOrNull() ?: application.disbursementAmount ?: 0.0
                    if (disbursementReference.isNotBlank()) {
                        onDisburse(amount, disbursementReference)
                    }
                },
                variant = VButtonVariant.Primary,
                size = VButtonSize.Sm,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (application.status == "DISBURSED" && application.disbursementAmount != null) {
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(appString(StringKeys.SCH_DISBURSED).replace("{amount}", application.disbursementAmount.toString()), style = VTypography.caption, color = VColors.ink3)
                Text(appString(StringKeys.SCH_REF).replace("{ref}", application.disbursementReference ?: "—"), style = VTypography.caption, color = VColors.ink3)
            }
        }
    }
}

@Composable
private fun ScholarshipRenewalCard(
    renewal: com.littlebridge.enrollplus.feature.scholarship.domain.model.ScholarshipRenewal,
    onApprove: (String) -> Unit,
    onReject: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var remarks by remember { mutableStateOf("") }
    val statusTone = when (renewal.status) {
        "pending" -> VBadgeTone.Warning
        "approved" -> VBadgeTone.Success
        "rejected" -> VBadgeTone.Danger
        else -> VBadgeTone.Neutral
    }

    VCard(modifier) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    renewal.scholarshipTitle ?: appString(StringKeys.SCH_RENEWAL_FOR),
                    style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold), color = VColors.ink,
                )
                Spacer(Modifier.height(2.dp))
                Text(appString(StringKeys.SCH_RENEWAL_FOR), style = VTypography.caption, color = VColors.ink3)
            }
            VBadge(text = renewal.status, tone = statusTone)
        }

        if (renewal.status == "pending") {
            Spacer(Modifier.height(12.dp))
            VInput(
                value = remarks,
                onValueChange = { remarks = it },
                label = appString(StringKeys.SCH_REMARKS),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.weight(1f)) {
                    VButton(
                        text = appString(StringKeys.SCH_APPROVE),
                        onClick = { onApprove(remarks) },
                        variant = VButtonVariant.Primary,
                        size = VButtonSize.Sm,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                Box(Modifier.weight(1f)) {
                    VButton(
                        text = appString(StringKeys.SCH_REJECT),
                        onClick = { onReject(remarks) },
                        variant = VButtonVariant.Secondary,
                        size = VButtonSize.Sm,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
private fun ScholarshipSchemeForm(
    existingScheme: ScholarshipScheme? = null,
    onCreate: (com.littlebridge.enrollplus.feature.scholarship.domain.model.CreateSchemeRequest) -> Unit = {},
    onUpdate: (UpdateSchemeRequest) -> Unit = {},
    onDismiss: () -> Unit,
) {
    val isEdit = existingScheme != null
    var title by remember { mutableStateOf(existingScheme?.title ?: "") }
    var description by remember { mutableStateOf(existingScheme?.description ?: "") }
    var amount by remember { mutableStateOf(existingScheme?.amount ?: "") }
    var numericAmount by remember { mutableStateOf(existingScheme?.numericAmount?.toString() ?: "") }
    var scholarshipType by remember { mutableStateOf(existingScheme?.scholarshipType ?: "fixed") }
    var waiverPercentage by remember { mutableStateOf(existingScheme?.waiverPercentage?.toString() ?: "") }
    var eligibilityCriteria by remember { mutableStateOf(existingScheme?.eligibilityCriteria ?: "") }
    var category by remember { mutableStateOf(existingScheme?.category ?: "Merit Based") }
    var startDate by remember { mutableStateOf(existingScheme?.startDate ?: "") }
    var endDate by remember { mutableStateOf(existingScheme?.endDate ?: "") }
    var isRenewable by remember { mutableStateOf(existingScheme?.isRenewable ?: false) }
    var renewalPeriodMonths by remember { mutableStateOf(existingScheme?.renewalPeriodMonths?.toString() ?: "12") }
    var typeDropdownOpen by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val typeOptions = listOf("fixed" to appString(StringKeys.SCH_FIXED), "full_waiver" to appString(StringKeys.SCH_FULL_WAIVER), "partial_waiver" to appString(StringKeys.SCH_PARTIAL_WAIVER))
    val categoryOptions = listOf("Merit Based", "Need Based", "Sports", "Cultural", "Special")

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center,
    ) {
        VCard(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .clickable(enabled = false) {},
            padding = 16.dp,
        ) {
            Text(
                if (isEdit) appString(StringKeys.SCH_EDIT_SCHEME) else appString(StringKeys.SCH_CREATE_SCHEME),
                style = VTypography.h3, color = VColors.ink,
            )
            Spacer(Modifier.height(16.dp))

            Column(Modifier.verticalScroll(scrollState)) {
                VInput(value = title, onValueChange = { title = it }, label = appString(StringKeys.SCH_TITLE_LABEL), modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                VInput(value = description, onValueChange = { description = it }, label = appString(StringKeys.SCH_DESCRIPTION), modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                VInput(value = amount, onValueChange = { amount = it }, label = appString(StringKeys.SCH_DISPLAY_AMOUNT), modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                VInput(value = numericAmount, onValueChange = { numericAmount = it.filter { it.isDigit() || it == '.' } }, label = appString(StringKeys.SCH_NUMERIC_AMOUNT), modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))

                // Scholarship type dropdown
                Text(appString(StringKeys.SCH_TYPE), style = VTypography.label, color = VColors.ink3)
                Spacer(Modifier.height(4.dp))
                Box {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(VColors.cream)
                            .clickable { typeDropdownOpen = !typeDropdownOpen }
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                    ) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                typeOptions.firstOrNull { it.first == scholarshipType }?.second ?: scholarshipType,
                                style = VTypography.body, color = VColors.ink,
                            )
                            Icon(
                                imageVector = if (typeDropdownOpen) VIcons.ChevronUp else VIcons.ChevronDown,
                                contentDescription = null,
                                tint = VColors.ink3,
                            )
                        }
                    }
                    if (typeDropdownOpen) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(top = 44.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                                .padding(vertical = 4.dp),
                        ) {
                            typeOptions.forEach { (value, label) ->
                                val isSelected = scholarshipType == value
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            scholarshipType = value
                                            typeDropdownOpen = false
                                        }
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(label, style = VTypography.body, color = if (isSelected) VColors.violet else VColors.ink)
                                    if (isSelected) {
                                        Icon(imageVector = VIcons.Check, contentDescription = null, tint = VColors.violet)
                                    }
                                }
                            }
                        }
                    }
                }

                if (scholarshipType == "partial_waiver") {
                    Spacer(Modifier.height(8.dp))
                    VInput(value = waiverPercentage, onValueChange = { waiverPercentage = it.filter { it.isDigit() || it == '.' } }, label = appString(StringKeys.SCH_WAIVER_PCT), modifier = Modifier.fillMaxWidth())
                }

                Spacer(Modifier.height(8.dp))
                VInput(value = eligibilityCriteria, onValueChange = { eligibilityCriteria = it }, label = appString(StringKeys.SCH_ELIGIBILITY_CRIT), modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))

                // Category dropdown
                Text(appString(StringKeys.SCH_CATEGORY), style = VTypography.label, color = VColors.ink3)
                Spacer(Modifier.height(4.dp))
                Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    categoryOptions.forEach { cat ->
                        val isSelected = category == cat
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) VColors.violet else VColors.cream)
                                .clickable { category = cat }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                        ) {
                            Text(cat, style = VTypography.label, color = if (isSelected) Color.White else VColors.ink3)
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
                VDatePicker(
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = appString(StringKeys.SCH_MGMT_START_DATE),
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(8.dp))
                VDatePicker(
                    value = endDate,
                    onValueChange = { endDate = it },
                    label = appString(StringKeys.SCH_MGMT_END_DATE),
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(8.dp))

                // Renewable toggle
                Row(
                    Modifier.fillMaxWidth().clickable { isRenewable = !isRenewable },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(if (isRenewable) VColors.violet else VColors.cream),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (isRenewable) {
                            Text("✓", style = VTypography.label, color = Color.White)
                        }
                    }
                    Text(appString(StringKeys.SCH_RENEWABLE_LABEL), style = VTypography.body, color = VColors.ink)
                }

                if (isRenewable) {
                    Spacer(Modifier.height(8.dp))
                    VInput(value = renewalPeriodMonths, onValueChange = { renewalPeriodMonths = it.filter { ch -> ch.isDigit() } }, label = appString(StringKeys.SCH_RENEWAL_PERIOD), modifier = Modifier.fillMaxWidth())
                }

                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(Modifier.weight(1f)) {
                        VButton(
                            text = appString(StringKeys.COMMON_BUTTON_CANCEL),
                            onClick = onDismiss,
                            variant = VButtonVariant.Secondary,
                            size = VButtonSize.Md,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    Box(Modifier.weight(1f)) {
                        VButton(
                            text = if (isEdit) appString(StringKeys.SCH_UPDATE) else appString(StringKeys.SCH_CREATE),
                            onClick = {
                                if (title.isNotBlank()) {
                                    if (isEdit) {
                                        onUpdate(
                                            UpdateSchemeRequest(
                                                title = title,
                                                description = description,
                                                amount = amount,
                                                numericAmount = numericAmount.toDoubleOrNull(),
                                                scholarshipType = scholarshipType,
                                                waiverPercentage = waiverPercentage.toFloatOrNull(),
                                                eligibilityCriteria = eligibilityCriteria,
                                                category = category,
                                                startDate = startDate.ifBlank { null },
                                                endDate = endDate.ifBlank { null },
                                                isRenewable = isRenewable,
                                                renewalPeriodMonths = renewalPeriodMonths.toIntOrNull(),
                                            )
                                        )
                                    } else {
                                        onCreate(
                                            com.littlebridge.enrollplus.feature.scholarship.domain.model.CreateSchemeRequest(
                                                title = title,
                                                description = description,
                                                amount = amount,
                                                numericAmount = numericAmount.toDoubleOrNull(),
                                                scholarshipType = scholarshipType,
                                                waiverPercentage = waiverPercentage.toFloatOrNull(),
                                                eligibilityCriteria = eligibilityCriteria,
                                                category = category,
                                                startDate = startDate.ifBlank { null },
                                                endDate = endDate.ifBlank { null },
                                                isRenewable = isRenewable,
                                                renewalPeriodMonths = renewalPeriodMonths.toIntOrNull(),
                                            )
                                        )
                                    }
                                }
                            },
                            variant = VButtonVariant.Primary,
                            size = VButtonSize.Md,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DeleteConfirmationDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center,
    ) {
        VCard(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .clickable(enabled = false) {},
            padding = 20.dp,
        ) {
            Text(title, style = VTypography.h3, color = VColors.ink)
            Spacer(Modifier.height(8.dp))
            Text(message, style = VTypography.body, color = VColors.ink2)
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.weight(1f)) {
                    VButton(
                        text = appString(StringKeys.COMMON_BUTTON_CANCEL),
                        onClick = onDismiss,
                        variant = VButtonVariant.Secondary,
                        size = VButtonSize.Md,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                Box(Modifier.weight(1f)) {
                    VButton(
                        text = appString(StringKeys.SCH_DEACTIVATE),
                        onClick = onConfirm,
                        variant = VButtonVariant.Destructive,
                        size = VButtonSize.Md,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}
