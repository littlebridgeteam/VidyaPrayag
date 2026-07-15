package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.admin.domain.model.FeeClassOptionDto
import com.littlebridge.enrollplus.feature.admin.domain.model.FeeLateFeeTierDto
import com.littlebridge.enrollplus.feature.admin.domain.model.FeeStructureDto
import com.littlebridge.enrollplus.feature.admin.domain.model.FeeStudentDto
import com.littlebridge.enrollplus.feature.admin.domain.model.FeeTeacherOptionDto
import com.littlebridge.enrollplus.feature.admin.domain.model.SalaryRecordDto
import com.littlebridge.enrollplus.feature.admin.presentation.FeeSalaryTab
import com.littlebridge.enrollplus.feature.admin.presentation.FeeSalaryViewModel
import com.littlebridge.enrollplus.feature.admin.presentation.FeeSubTab
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VBottomSheet
import com.littlebridge.enrollplus.ui.v2.components.VBottomSheetHeader
import com.littlebridge.enrollplus.ui.v2.components.VBadge
import com.littlebridge.enrollplus.ui.v2.components.VBadgeTone
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VConfirmDialog
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VInput
import com.littlebridge.enrollplus.ui.v2.components.VTopTabs
import com.littlebridge.enrollplus.ui.v2.screens.SkeletonList
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.util.todayIso
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FeeSalaryManagementScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FeeSalaryViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        VBackHeader(title = "Fee & Salary Management", onBack = onBack)

        VTopTabs(
            tabs = listOf("Fees", "Salary"),
            selected = if (state.activeTab == FeeSalaryTab.FEES) "Fees" else "Salary",
            onSelect = {
                viewModel.setTab(if (it == "Fees") FeeSalaryTab.FEES else FeeSalaryTab.SALARY)
            },
        )

        when (state.activeTab) {
            FeeSalaryTab.FEES -> FeesTab(state = state, viewModel = viewModel)
            FeeSalaryTab.SALARY -> SalaryTab(state = state, viewModel = viewModel)
        }
    }
}

// ── Fees Tab ──────────────────────────────────────────────────────────────────

@Composable
private fun FeesTab(
    state: com.littlebridge.enrollplus.feature.admin.presentation.FeeSalaryState,
    viewModel: FeeSalaryViewModel,
) {
    VTopTabs(
        tabs = listOf("Structure", "Payments", "Reminders", "Late Fees"),
        selected = when (state.activeFeeSubTab) {
            FeeSubTab.STRUCTURE -> "Structure"
            FeeSubTab.PAYMENT_TRACKING -> "Payments"
            FeeSubTab.REMINDER_SETTINGS -> "Reminders"
            FeeSubTab.LATE_FEE_TIERS -> "Late Fees"
        },
        onSelect = {
            viewModel.setFeeSubTab(
                when (it) {
                    "Structure" -> FeeSubTab.STRUCTURE
                    "Payments" -> FeeSubTab.PAYMENT_TRACKING
                    "Reminders" -> FeeSubTab.REMINDER_SETTINGS
                    else -> FeeSubTab.LATE_FEE_TIERS
                }
            )
        },
    )

    when (state.activeFeeSubTab) {
        FeeSubTab.STRUCTURE -> FeeStructureSubTab(state = state, viewModel = viewModel)
        FeeSubTab.PAYMENT_TRACKING -> PaymentTrackingSubTab(state = state, viewModel = viewModel)
        FeeSubTab.REMINDER_SETTINGS -> ReminderSettingsSubTab(state = state, viewModel = viewModel)
        FeeSubTab.LATE_FEE_TIERS -> LateFeeTiersSubTab(state = state, viewModel = viewModel)
    }
}

// ── Fee Structure Sub-Tab ─────────────────────────────────────────────────────

@Composable
private fun FeeStructureSubTab(
    state: com.littlebridge.enrollplus.feature.admin.presentation.FeeSalaryState,
    viewModel: FeeSalaryViewModel,
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showDeleteId by remember { mutableStateOf<String?>(null) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        VButton(
            text = "Add Fee Structure",
            onClick = { showAddDialog = true },
            leading = { Icon(VIcons.Plus, contentDescription = null) },
            full = true,
        )

        VStateHost(
            loading = state.isLoading,
            error = state.errorMessage,
            isEmpty = state.structures.isEmpty() && !state.isLoading,
            emptyTitle = "No Fee Structures",
            emptyBody = "Add a fee structure to start collecting fees from students.",
            emptyIcon = VIcons.Wallet,
            onRetry = { viewModel.loadFeeStructures() },
            skeleton = { SkeletonList(rows = 4) },
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                state.structures.forEach { struct ->
                    FeeStructureCard(
                        struct = struct,
                        onDelete = { showDeleteId = struct.id },
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddFeeStructureSheet(
            onDismiss = { showAddDialog = false },
            onCreate = { title, amount, desc, classId ->
                viewModel.createFeeStructure(title, amount, desc, classId)
                showAddDialog = false
            },
            classes = state.classes,
        )
    }

    showDeleteId?.let { id ->
        VConfirmDialog(
            visible = true,
            title = "Delete Fee Structure",
            message = "Are you sure you want to delete this fee structure?",
            confirmLabel = "Delete",
            onConfirm = { viewModel.deleteFeeStructure(id); showDeleteId = null },
            onDismiss = { showDeleteId = null },
            icon = VIcons.AlertTriangle,
        )
    }
}

@Composable
private fun FeeStructureCard(
    struct: FeeStructureDto,
    onDelete: () -> Unit,
) {
    VCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(struct.title, style = VTypography.body, fontWeight = FontWeight.SemiBold, color = VColors.ink, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                struct.description?.let {
                    Text(it, style = VTypography.caption, color = VColors.ink2, maxLines = 2, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                }
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "₹${"%,.0f".format(struct.amount)}",
                        style = VTypography.body,
                        fontWeight = FontWeight.Bold,
                        color = VColors.violet,
                    )
                    VBadge(
                        text = struct.frequency,
                        tone = VBadgeTone.Neutral,
                    )
                    if (!struct.isActive) {
                        VBadge(text = "Inactive", tone = VBadgeTone.Warning)
                    }
                }
            }
            Icon(
                VIcons.Close,
                contentDescription = "Delete",
                tint = VColors.error,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { onDelete() },
            )
        }
    }
}

@Composable
private fun AddFeeStructureSheet(
    onDismiss: () -> Unit,
    onCreate: (title: String, amount: Double, description: String?, classId: String?) -> Unit,
    classes: List<FeeClassOptionDto> = emptyList(),
) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedClassId by remember { mutableStateOf<String?>(null) }
    var classDropdownOpen by remember { mutableStateOf(false) }

    VBottomSheet(visible = true, onDismiss = onDismiss) {
        VBottomSheetHeader(title = "Add Fee Structure")
        Spacer(Modifier.height(16.dp))
        VInput(
            value = title,
            onValueChange = { title = it },
            label = "Title",
            placeholder = "e.g. Tuition Fee",
        )
        VInput(
            value = amount,
            onValueChange = { amount = it.filter { it.isDigit() || it == '.' } },
            label = "Amount (₹)",
            placeholder = "e.g. 5000",
            keyboardType = KeyboardType.Decimal,
        )
        VInput(
            value = description,
            onValueChange = { description = it },
            label = "Description (optional)",
            placeholder = "e.g. Monthly tuition for all classes",
        )
        Text("Class (optional)", style = VTypography.caption, color = VColors.ink2)
        VCard {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { classDropdownOpen = !classDropdownOpen }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = classes.firstOrNull { it.id == selectedClassId }?.name ?: "All classes",
                        style = VTypography.body,
                        color = VColors.ink,
                    )
                    Icon(
                        if (classDropdownOpen) VIcons.ChevronUp else VIcons.ChevronDown,
                        contentDescription = null,
                        tint = VColors.ink2,
                    )
                }
                if (classDropdownOpen) {
                    Spacer(Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedClassId = null
                                classDropdownOpen = false
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "All classes",
                            style = VTypography.body,
                            color = VColors.ink,
                            modifier = Modifier.weight(1f),
                        )
                        if (selectedClassId == null) {
                            Icon(VIcons.Check, contentDescription = null, tint = VColors.violet)
                        }
                    }
                    classes.forEach { cls ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedClassId = cls.id
                                    classDropdownOpen = false
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = cls.name,
                                style = VTypography.body,
                                color = VColors.ink,
                                modifier = Modifier.weight(1f),
                            )
                            if (cls.id == selectedClassId) {
                                Icon(VIcons.Check, contentDescription = null, tint = VColors.violet)
                            }
                        }
                    }
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            VButton(
                text = "Cancel",
                onClick = onDismiss,
                variant = VButtonVariant.Ghost,
                modifier = Modifier.weight(1f),
            )
            VButton(
                text = "Create",
                onClick = {
                    val amt = amount.toDoubleOrNull() ?: 0.0
                    if (title.isNotBlank() && amt > 0) {
                        onCreate(title.trim(), amt, description.ifBlank { null }, selectedClassId)
                    }
                },
                enabled = title.isNotBlank() && (amount.toDoubleOrNull() ?: 0.0) > 0,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

// ── Payment Tracking Sub-Tab ──────────────────────────────────────────────────

@Composable
private fun PaymentTrackingSubTab(
    state: com.littlebridge.enrollplus.feature.admin.presentation.FeeSalaryState,
    viewModel: FeeSalaryViewModel,
) {
    var showGenerateDialog by remember { mutableStateOf(false) }
    var showMarkPaidId by remember { mutableStateOf<String?>(null) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text("Total Due", style = VTypography.caption, color = VColors.ink2)
                Text("₹${"%,.0f".format(state.totalDue)}", style = VTypography.h3, fontWeight = FontWeight.Bold, color = VColors.error)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Total Paid", style = VTypography.caption, color = VColors.ink2)
                Text("₹${"%,.0f".format(state.totalPaid)}", style = VTypography.h3, fontWeight = FontWeight.Bold, color = VColors.success)
            }
        }

        VButton(
            text = "Generate Monthly Fees",
            onClick = { showGenerateDialog = true },
            leading = { Icon(VIcons.Plus, contentDescription = null) },
            full = true,
        )

        VStateHost(
            loading = state.isLoading,
            error = state.errorMessage,
            isEmpty = state.feeStudents.isEmpty() && !state.isLoading,
            emptyTitle = "No Fee Records",
            emptyBody = "Generate monthly fees for students to see payment tracking here.",
            emptyIcon = VIcons.Wallet,
            onRetry = { viewModel.loadFeeStudents() },
            skeleton = { SkeletonList(rows = 4) },
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                state.feeStudents.forEach { student ->
                    FeeStudentCard(
                        student = student,
                        onMarkPaid = { showMarkPaidId = student.childId },
                    )
                }
            }
        }
    }

    if (showGenerateDialog) {
        GenerateFeesSheet(
            currentMonth = state.selectedMonth,
            onDismiss = { showGenerateDialog = false },
            onGenerate = { month ->
                viewModel.generateFees(month)
                showGenerateDialog = false
            },
        )
    }

    showMarkPaidId?.let { childId ->
        VConfirmDialog(
            visible = true,
            title = "Mark Fees Paid",
            message = "Mark all due fees for this student as paid for ${state.selectedMonth}?",
            confirmLabel = "Mark Paid",
            onConfirm = {
                viewModel.markFeesPaid(childId, listOf(state.selectedMonth))
                showMarkPaidId = null
            },
            onDismiss = { showMarkPaidId = null },
            icon = VIcons.Check,
        )
    }
}

@Composable
private fun FeeStudentCard(
    student: FeeStudentDto,
    onMarkPaid: () -> Unit,
) {
    VCard {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(student.childName, style = VTypography.body, fontWeight = FontWeight.SemiBold, color = VColors.ink, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                    student.className?.let {
                        Text(it, style = VTypography.caption, color = VColors.ink2, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                    }
                }
                val statusDisplay = when (student.status) {
                    "PAID" -> "Paid"
                    "DUE" -> "Due"
                    "OVERDUE" -> "Overdue"
                    "PARTIAL" -> "Partial"
                    "NO_FEES" -> "No Fees"
                    else -> student.status.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() }
                }
                VBadge(
                    text = statusDisplay,
                    tone = when (student.status) {
                        "PAID" -> VBadgeTone.Success
                        "DUE" -> VBadgeTone.Warning
                        "OVERDUE" -> VBadgeTone.Danger
                        "PARTIAL", "NO_FEES" -> VBadgeTone.Neutral
                        else -> VBadgeTone.Neutral
                    },
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Total: ₹${"%,.0f".format(student.totalAmount)}", style = VTypography.caption, color = VColors.ink2)
                Text("Paid: ₹${"%,.0f".format(student.paidAmount)}", style = VTypography.caption, color = VColors.success)
                Text("Due: ₹${"%,.0f".format(student.dueAmount)}", style = VTypography.caption, color = VColors.error)
            }
            if (student.dueAmount > 0) {
                Spacer(Modifier.height(8.dp))
                VButton(
                    text = "Mark Paid",
                    onClick = onMarkPaid,
                    size = com.littlebridge.enrollplus.ui.v2.components.VButtonSize.Sm,
                    variant = VButtonVariant.Primary,
                )
            }
        }
    }
}

@Composable
private fun GenerateFeesSheet(
    currentMonth: String,
    onDismiss: () -> Unit,
    onGenerate: (String) -> Unit,
) {
    var month by remember { mutableStateOf(currentMonth) }

    VBottomSheet(visible = true, onDismiss = onDismiss) {
        VBottomSheetHeader(title = "Generate Monthly Fees")
        Spacer(Modifier.height(16.dp))
        Text(
            "This will create fee records for all active students based on your fee structures.",
            style = VTypography.caption,
            color = VColors.ink2,
        )
        VInput(
            value = month,
            onValueChange = { month = it },
            label = "Month (YYYY-MM)",
            placeholder = "2026-07",
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            VButton(
                text = "Cancel",
                onClick = onDismiss,
                variant = VButtonVariant.Ghost,
                modifier = Modifier.weight(1f),
            )
            VButton(
                text = "Generate",
                onClick = { if (month.isNotBlank()) onGenerate(month) },
                enabled = month.isNotBlank(),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

// ── Reminder Settings Sub-Tab ─────────────────────────────────────────────────

@Composable
private fun ReminderSettingsSubTab(
    state: com.littlebridge.enrollplus.feature.admin.presentation.FeeSalaryState,
    viewModel: FeeSalaryViewModel,
) {
    val config = state.reminderConfig
    var reminderDay by remember(config) { mutableStateOf(config?.reminderDay?.toString() ?: "5") }
    var isActive by remember(config) { mutableStateOf(config?.isActive ?: true) }
    val dayValid = reminderDay.toIntOrNull()?.let { it in 1..28 } ?: false
    val showError = reminderDay.isNotBlank() && !dayValid

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Fee Reminder Settings", style = VTypography.h3, fontWeight = FontWeight.Bold)
        Text(
            "Set the day of each month when fee reminder notifications are sent to parents.",
            style = VTypography.caption,
            color = VColors.ink2,
        )

        VCard {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                VInput(
                    value = reminderDay,
                    onValueChange = { reminderDay = it.filter { c -> c.isDigit() }.take(2) },
                    label = "Reminder Day (1-28)",
                    placeholder = "5",
                    keyboardType = KeyboardType.Number,
                    isError = showError,
                    errorText = if (showError) "Reminder day must be between 1 and 28" else null,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Active", style = VTypography.body, color = VColors.ink)
                    androidx.compose.material3.Switch(
                        checked = isActive,
                        onCheckedChange = { isActive = it },
                    )
                }
                VButton(
                    text = "Save",
                    onClick = {
                        val day = reminderDay.toIntOrNull()
                        if (day != null && day in 1..28) {
                            viewModel.updateReminderConfig(day, isActive)
                        }
                    },
                    full = true,
                    enabled = dayValid,
                )
            }
        }

        state.actionMessage?.let {
            Text(it, style = VTypography.caption, color = VColors.success)
        }
    }
}

// ── Salary Tab ────────────────────────────────────────────────────────────────

@Composable
private fun SalaryTab(
    state: com.littlebridge.enrollplus.feature.admin.presentation.FeeSalaryState,
    viewModel: FeeSalaryViewModel,
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showMarkPaidId by remember { mutableStateOf<String?>(null) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        VButton(
            text = "Add Salary Record",
            onClick = { showAddDialog = true },
            leading = { Icon(VIcons.Plus, contentDescription = null) },
            full = true,
        )

        VStateHost(
            loading = state.isLoading,
            error = state.errorMessage,
            isEmpty = state.salaryRecords.isEmpty() && !state.isLoading,
            emptyTitle = "No Salary Records",
            emptyBody = "Add a salary record for a teacher to get started.",
            emptyIcon = VIcons.Wallet,
            onRetry = { viewModel.loadSalaryRecords() },
            skeleton = { SkeletonList(rows = 4) },
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                state.salaryRecords.forEach { record ->
                    SalaryRecordCard(
                        record = record,
                        onMarkPaid = { showMarkPaidId = record.id },
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddSalarySheet(
            onDismiss = { showAddDialog = false },
            onCreate = { teacherId, month, base, allowances, deductions ->
                viewModel.setSalary(
                    com.littlebridge.enrollplus.feature.admin.domain.model.SetSalaryRequest(
                        teacherId = teacherId,
                        month = month,
                        baseSalary = base,
                        allowances = allowances,
                        deductions = deductions,
                    )
                )
                showAddDialog = false
            },
            teachers = state.teachers,
        )
    }

    showMarkPaidId?.let { id ->
        VConfirmDialog(
            visible = true,
            title = "Mark Salary Paid",
            message = "Mark this salary record as paid?",
            confirmLabel = "Mark Paid",
            onConfirm = { viewModel.markSalaryPaid(id); showMarkPaidId = null },
            onDismiss = { showMarkPaidId = null },
            icon = VIcons.Check,
        )
    }
}

@Composable
private fun SalaryRecordCard(
    record: SalaryRecordDto,
    onMarkPaid: () -> Unit,
) {
    VCard {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(record.teacherName.ifBlank { "Teacher" }, style = VTypography.body, fontWeight = FontWeight.SemiBold, color = VColors.ink, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                    Text(record.month, style = VTypography.caption, color = VColors.ink2)
                }
                val salaryStatusDisplay = when (record.status) {
                    "PAID" -> "Paid"
                    "UNPAID" -> "Unpaid"
                    "PENDING" -> "Pending"
                    else -> record.status.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() }
                }
                VBadge(
                    text = salaryStatusDisplay,
                    tone = if (record.status == "PAID") VBadgeTone.Success else VBadgeTone.Warning,
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text("Base: ₹${"%,.0f".format(record.baseSalary)}", style = VTypography.caption, color = VColors.ink2)
                    Text("Allowances: ₹${"%,.0f".format(record.allowances)}", style = VTypography.caption, color = VColors.ink2)
                    Text("Deductions: ₹${"%,.0f".format(record.deductions)}", style = VTypography.caption, color = VColors.ink2)
                }
                Text(
                    "Net: ₹${"%,.0f".format(record.netAmount)}",
                    style = VTypography.body,
                    fontWeight = FontWeight.Bold,
                    color = VColors.violet,
                )
            }
            if (record.status == "UNPAID") {
                Spacer(Modifier.height(8.dp))
                VButton(
                    text = "Mark Paid",
                    onClick = onMarkPaid,
                    size = com.littlebridge.enrollplus.ui.v2.components.VButtonSize.Sm,
                )
            }
        }
    }
}

@Composable
private fun AddSalarySheet(
    onDismiss: () -> Unit,
    onCreate: (teacherId: String, month: String, base: Double, allowances: Double, deductions: Double) -> Unit,
    teachers: List<FeeTeacherOptionDto> = emptyList(),
) {
    var selectedTeacherId by remember { mutableStateOf<String?>(null) }
    var teacherDropdownOpen by remember { mutableStateOf(false) }
    var month by remember { mutableStateOf(todayIso().substring(0, 7)) }
    var base by remember { mutableStateOf("") }
    var allowances by remember { mutableStateOf("") }
    var deductions by remember { mutableStateOf("") }

    VBottomSheet(visible = true, onDismiss = onDismiss) {
        VBottomSheetHeader(title = "Add Salary Record")
        Spacer(Modifier.height(16.dp))
        Text("Teacher", style = VTypography.caption, color = VColors.ink2)
        VCard {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { teacherDropdownOpen = !teacherDropdownOpen }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = teachers.firstOrNull { it.id == selectedTeacherId }?.name ?: "Select teacher",
                        style = VTypography.body,
                        color = VColors.ink,
                    )
                    Icon(
                        if (teacherDropdownOpen) VIcons.ChevronUp else VIcons.ChevronDown,
                        contentDescription = null,
                        tint = VColors.ink2,
                    )
                }
                if (teacherDropdownOpen) {
                    Spacer(Modifier.height(4.dp))
                    teachers.forEach { teacher ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedTeacherId = teacher.id
                                    teacherDropdownOpen = false
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = teacher.name,
                                style = VTypography.body,
                                color = VColors.ink,
                                modifier = Modifier.weight(1f),
                            )
                            if (teacher.id == selectedTeacherId) {
                                Icon(VIcons.Check, contentDescription = null, tint = VColors.violet)
                            }
                        }
                    }
                }
            }
        }
        VInput(
            value = month,
            onValueChange = { month = it },
            label = "Month (YYYY-MM)",
            placeholder = "2026-07",
        )
        VInput(
            value = base,
            onValueChange = { base = it.filter { c -> c.isDigit() || c == '.' } },
            label = "Base Salary (₹)",
            placeholder = "30000",
            keyboardType = KeyboardType.Decimal,
        )
        VInput(
            value = allowances,
            onValueChange = { allowances = it.filter { c -> c.isDigit() || c == '.' } },
            label = "Allowances (₹)",
            placeholder = "0",
            keyboardType = KeyboardType.Decimal,
        )
        VInput(
            value = deductions,
            onValueChange = { deductions = it.filter { c -> c.isDigit() || c == '.' } },
            label = "Deductions (₹)",
            placeholder = "0",
            keyboardType = KeyboardType.Decimal,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            VButton(
                text = "Cancel",
                onClick = onDismiss,
                variant = VButtonVariant.Ghost,
                modifier = Modifier.weight(1f),
            )
            VButton(
                text = "Save",
                onClick = {
                    val b = base.toDoubleOrNull() ?: 0.0
                    val al = allowances.toDoubleOrNull() ?: 0.0
                    val de = deductions.toDoubleOrNull() ?: 0.0
                    if (selectedTeacherId != null && month.isNotBlank() && b > 0) {
                        onCreate(selectedTeacherId!!, month, b, al, de)
                    }
                },
                enabled = selectedTeacherId != null && month.isNotBlank() && (base.toDoubleOrNull() ?: 0.0) > 0,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

// ── Late Fee Tiers Sub-Tab ────────────────────────────────────────────────────

@Composable
private fun LateFeeTiersSubTab(
    state: com.littlebridge.enrollplus.feature.admin.presentation.FeeSalaryState,
    viewModel: FeeSalaryViewModel,
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showDeleteId by remember { mutableStateOf<String?>(null) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Late Fee Tiers", style = VTypography.h3, fontWeight = FontWeight.Bold)
            VButton(
                text = "Add Tier",
                onClick = { showAddDialog = true },
                variant = VButtonVariant.Primary,
            )
        }

        state.actionMessage?.let { msg ->
            VCard {
                Text(msg, style = VTypography.body, color = VColors.violet)
            }
        }

        if (state.isLoading && state.lateFeeTiers.isEmpty()) {
            SkeletonList(rows = 3)
            return@Column
        }

        if (state.lateFeeTiers.isEmpty()) {
            VCard {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        VIcons.Wallet,
                        contentDescription = null,
                        tint = VColors.ink2,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("No late fee tiers configured", style = VTypography.body, color = VColors.ink2)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Add tiers to automatically charge late fees on overdue payments",
                        style = VTypography.caption,
                        color = VColors.ink2,
                    )
                }
            }
        } else {
            state.lateFeeTiers.forEach { tier ->
                LateFeeTierCard(
                    tier = tier,
                    onDelete = { showDeleteId = tier.id },
                )
            }
        }
    }

    if (showAddDialog) {
        AddLateFeeTierSheet(
            onDismiss = { showAddDialog = false },
            onCreate = { days, amount ->
                viewModel.createLateFeeTier(days, amount)
                showAddDialog = false
            },
        )
    }

    showDeleteId?.let { id ->
        VConfirmDialog(
            visible = true,
            title = "Delete Late Fee Tier",
            message = "Are you sure you want to delete this late fee tier?",
            confirmLabel = "Delete",
            onConfirm = { viewModel.deleteLateFeeTier(id); showDeleteId = null },
            onDismiss = { showDeleteId = null },
        )
    }
}

@Composable
private fun LateFeeTierCard(
    tier: FeeLateFeeTierDto,
    onDelete: () -> Unit,
) {
    VCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "After ${tier.daysAfterDue} days",
                    style = VTypography.body,
                    fontWeight = FontWeight.SemiBold,
                    color = VColors.ink,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Late fee: ₹${tier.amount}",
                    style = VTypography.caption,
                    color = VColors.ink2,
                )
            }
            if (tier.isActive) {
                VBadge(text = "Active", tone = VBadgeTone.Success)
            } else {
                VBadge(text = "Inactive", tone = VBadgeTone.Neutral)
            }
            Spacer(Modifier.width(8.dp))
            Icon(
                VIcons.Close,
                contentDescription = "Delete",
                tint = VColors.error,
                modifier = Modifier
                    .padding(4.dp)
                    .clickable { onDelete() },
            )
        }
    }
}

@Composable
private fun AddLateFeeTierSheet(
    onDismiss: () -> Unit,
    onCreate: (daysAfterDue: Int, amount: Double) -> Unit,
) {
    var days by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

    VBottomSheet(visible = true, onDismiss = onDismiss) {
        VBottomSheetHeader(title = "Add Late Fee Tier")
        Spacer(Modifier.height(16.dp))
        VInput(
            value = days,
            onValueChange = { days = it.filter { c -> c.isDigit() } },
            label = "Days After Due Date",
            placeholder = "e.g. 7",
            keyboardType = KeyboardType.Number,
        )
        VInput(
            value = amount,
            onValueChange = { amount = it.filter { c -> c.isDigit() || c == '.' } },
            label = "Late Fee Amount (₹)",
            placeholder = "e.g. 100",
            keyboardType = KeyboardType.Decimal,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            VButton(
                text = "Cancel",
                onClick = onDismiss,
                variant = VButtonVariant.Ghost,
                modifier = Modifier.weight(1f),
            )
            VButton(
                text = "Create",
                onClick = {
                    val d = days.toIntOrNull() ?: 0
                    val a = amount.toDoubleOrNull() ?: 0.0
                    if (d > 0 && a > 0) {
                        onCreate(d, a)
                    }
                },
                enabled = (days.toIntOrNull() ?: 0) > 0 && (amount.toDoubleOrNull() ?: 0.0) > 0,
                modifier = Modifier.weight(1f),
            )
        }
    }
}
