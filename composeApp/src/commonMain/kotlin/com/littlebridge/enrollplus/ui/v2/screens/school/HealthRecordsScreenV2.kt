package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.health.domain.model.HealthIncidentDto
import com.littlebridge.enrollplus.feature.health.domain.model.HealthProfileDto
import com.littlebridge.enrollplus.feature.health.domain.model.ImmunizationDto
import com.littlebridge.enrollplus.feature.health.domain.model.UpsertHealthProfileRequest
import com.littlebridge.enrollplus.feature.health.domain.model.AddImmunizationRequest
import com.littlebridge.enrollplus.feature.health.domain.model.LogIncidentRequest
import com.littlebridge.enrollplus.feature.health.presentation.AdminHealthViewModel
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VBadge
import com.littlebridge.enrollplus.ui.v2.components.VBadgeTone
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonSize
import com.littlebridge.enrollplus.ui.v2.components.VButtonTone
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VInput
import com.littlebridge.enrollplus.ui.v2.components.VTopTabs
import com.littlebridge.enrollplus.ui.v2.screens.VSectionHeader
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.theme.VTheme
import com.littlebridge.enrollplus.ui.v2.theme.colored
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.locale.appString
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HealthRecordsScreenV2(
    studentId: String,
    studentName: String,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: AdminHealthViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    LaunchedEffect(studentId) { viewModel.load(studentId) }

    Column(
        modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .navigationBarsPadding(),
    ) {
        VBackHeader(title = appString(StringKeys.HLTH_TITLE, "name" to studentName), onBack = onBack)

        VStateHost(
            loading = state.isLoading,
            error = state.error,
            isEmpty = false,
            onRetry = { viewModel.load(studentId) },
            modifier = Modifier.fillMaxSize(),
        ) {
            HealthRecordsContent(
                state = state,
                studentId = studentId,
                onSaveProfile = { req -> viewModel.saveProfile(studentId, req) },
                onAddImmunization = { req -> viewModel.addImmunization(req) },
                onLogIncident = { req -> viewModel.logIncident(req) },
                onMarkNotified = { id -> viewModel.markNotified(id) },
                onClearMessages = viewModel::clearMessages,
            )
        }
    }
}

@Composable
private fun HealthRecordsContent(
    state: com.littlebridge.enrollplus.feature.health.presentation.AdminHealthState,
    studentId: String,
    onSaveProfile: (UpsertHealthProfileRequest) -> Unit,
    onAddImmunization: (AddImmunizationRequest) -> Unit,
    onLogIncident: (LogIncidentRequest) -> Unit,
    onMarkNotified: (String) -> Unit,
    onClearMessages: () -> Unit,
) {
    val c = VTheme.colors
    val tabKeys = listOf(StringKeys.HLTH_TAB_PROFILE, StringKeys.HLTH_TAB_IMMUNIZATIONS, StringKeys.HLTH_TAB_INCIDENTS)
    var tabIdx by remember { mutableStateOf(0) }
    val tabLabels = tabKeys.map { appString(it) }

    Column(Modifier.fillMaxSize()) {
        VTopTabs(tabs = tabLabels, selected = tabLabels[tabIdx], onSelect = { label -> tabIdx = tabLabels.indexOf(label) })

        if (state.infoMessage != null || state.saveError != null) {
            val msg = state.infoMessage ?: state.saveError
            val isError = state.saveError != null
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isError) c.danger.copy(alpha = 0.1f) else c.teal.copy(alpha = 0.1f))
                    .padding(12.dp),
            ) {
                Text(
                    msg ?: "",
                    style = VTheme.type.caption.colored(if (isError) c.dangerInk else c.tealDeep),
                )
            }
            LaunchedEffect(msg) {
                if (msg != null) {
                    kotlinx.coroutines.delay(2500)
                    onClearMessages()
                }
            }
        }

        when (tabIdx) {
            0 -> ProfileTab(
                profile = state.profile,
                isSaving = state.isSaving,
                onSave = onSaveProfile,
            )
            1 -> ImmunizationsTab(
                immunizations = state.immunizations,
                studentId = studentId,
                isSaving = state.isSaving,
                onAdd = onAddImmunization,
            )
            2 -> IncidentsTab(
                incidents = state.incidents,
                studentId = studentId,
                isSaving = state.isSaving,
                onLog = onLogIncident,
                onMarkNotified = onMarkNotified,
            )
        }
    }
}

// ── Profile tab ──────────────────────────────────────────────────

@Composable
private fun ProfileTab(
    profile: HealthProfileDto?,
    isSaving: Boolean,
    onSave: (UpsertHealthProfileRequest) -> Unit,
) {
    val c = VTheme.colors
    var bloodGroup by remember(profile?.id) { mutableStateOf(profile?.bloodGroup.orEmpty()) }
    var heightCm by remember(profile?.id) { mutableStateOf(profile?.heightCm?.toString().orEmpty()) }
    var weightKg by remember(profile?.id) { mutableStateOf(profile?.weightKg?.toString().orEmpty()) }
    var allergies by remember(profile?.id) { mutableStateOf(profile?.allergies ?: "[]") }
    var chronicConditions by remember(profile?.id) { mutableStateOf(profile?.chronicConditions ?: "[]") }
    var medications by remember(profile?.id) { mutableStateOf(profile?.medications ?: "[]") }
    var emergencyContactName by remember(profile?.id) { mutableStateOf(profile?.emergencyContactName.orEmpty()) }
    var emergencyContactPhone by remember(profile?.id) { mutableStateOf(profile?.emergencyContactPhone.orEmpty()) }
    var doctorName by remember(profile?.id) { mutableStateOf(profile?.doctorName.orEmpty()) }
    var doctorPhone by remember(profile?.id) { mutableStateOf(profile?.doctorPhone.orEmpty()) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        VSectionHeader(appString(StringKeys.HLTH_BASIC_INFO))
        VCard {
            Column(
                Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                VInput(value = bloodGroup, onValueChange = { bloodGroup = it }, label = appString(StringKeys.HLTH_BLOOD_GROUP), placeholder = "e.g. O+")
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(Modifier.weight(1f)) {
                        VInput(value = heightCm, onValueChange = { heightCm = it }, label = appString(StringKeys.HLTH_HEIGHT), placeholder = "e.g. 140")
                    }
                    Box(Modifier.weight(1f)) {
                        VInput(value = weightKg, onValueChange = { weightKg = it }, label = appString(StringKeys.HLTH_WEIGHT), placeholder = "e.g. 35")
                    }
                }
            }
        }

        VSectionHeader(appString(StringKeys.HLTH_MEDICAL_INFO))
        VCard {
            Column(
                Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                VInput(value = allergies, onValueChange = { allergies = it }, label = appString(StringKeys.HLTH_ALLERGIES), placeholder = "[\"Peanuts\"]")
                VInput(value = chronicConditions, onValueChange = { chronicConditions = it }, label = appString(StringKeys.HLTH_CHRONIC_CONDITIONS), placeholder = "[\"Asthma\"]")
                VInput(value = medications, onValueChange = { medications = it }, label = appString(StringKeys.HLTH_MEDICATIONS), placeholder = "[\"Inhaler\"]")
            }
        }

        VSectionHeader(appString(StringKeys.HLTH_EMERGENCY_CONTACT))
        VCard {
            Column(
                Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                VInput(value = emergencyContactName, onValueChange = { emergencyContactName = it }, label = appString(StringKeys.HLTH_CONTACT_NAME), placeholder = "e.g. Raj Patel")
                VInput(value = emergencyContactPhone, onValueChange = { emergencyContactPhone = it }, label = appString(StringKeys.HLTH_CONTACT_PHONE), placeholder = "e.g. 9876543210")
            }
        }

        VSectionHeader(appString(StringKeys.HLTH_DOCTOR_INFO))
        VCard {
            Column(
                Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                VInput(value = doctorName, onValueChange = { doctorName = it }, label = appString(StringKeys.HLTH_DOCTOR_NAME), placeholder = "e.g. Dr. Sharma")
                VInput(value = doctorPhone, onValueChange = { doctorPhone = it }, label = appString(StringKeys.HLTH_DOCTOR_PHONE), placeholder = "e.g. 9876543210")
            }
        }

        Spacer(Modifier.height(4.dp))
        VButton(
            text = appString(StringKeys.HLTH_SAVE_PROFILE),
            onClick = {
                onSave(
                    UpsertHealthProfileRequest(
                        bloodGroup = bloodGroup.trim().ifBlank { null },
                        heightCm = heightCm.trim().toDoubleOrNull(),
                        weightKg = weightKg.trim().toDoubleOrNull(),
                        allergies = allergies.trim().ifBlank { null },
                        chronicConditions = chronicConditions.trim().ifBlank { null },
                        medications = medications.trim().ifBlank { null },
                        emergencyContactName = emergencyContactName.trim().ifBlank { null },
                        emergencyContactPhone = emergencyContactPhone.trim().ifBlank { null },
                        doctorName = doctorName.trim().ifBlank { null },
                        doctorPhone = doctorPhone.trim().ifBlank { null },
                    ),
                )
            },
            full = true,
            loading = isSaving,
            tone = VButtonTone.Lavender,
        )
        Spacer(Modifier.height(24.dp))
    }
}

// ── Immunizations tab ────────────────────────────────────────────

@Composable
private fun ImmunizationsTab(
    immunizations: List<ImmunizationDto>,
    studentId: String,
    isSaving: Boolean,
    onAdd: (AddImmunizationRequest) -> Unit,
) {
    val c = VTheme.colors
    var showAdd by remember { mutableStateOf(false) }
    var vaccineName by remember { mutableStateOf("") }
    var doseNumber by remember { mutableStateOf("1") }
    var dateAdministered by remember { mutableStateOf("") }
    var nextDueDate by remember { mutableStateOf("") }
    var administeredBy by remember { mutableStateOf("") }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        VSectionHeader(appString(StringKeys.HLTH_IMMUNIZATION_RECORDS)) {
            VButton(
                text = appString(StringKeys.HLTH_ADD),
                onClick = { showAdd = !showAdd },
                size = VButtonSize.Sm,
                variant = VButtonVariant.Secondary,
                tone = VButtonTone.Lavender,
            )
        }

        if (showAdd) {
            VCard {
                Column(
                    Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    VInput(value = vaccineName, onValueChange = { vaccineName = it }, label = appString(StringKeys.HLTH_VACCINE_NAME), placeholder = "e.g. MMR")
                    VInput(value = doseNumber, onValueChange = { doseNumber = it }, label = appString(StringKeys.HLTH_DOSE_NUMBER), placeholder = "1")
                    VInput(value = dateAdministered, onValueChange = { dateAdministered = it }, label = appString(StringKeys.HLTH_DATE_ADMINISTERED), placeholder = "YYYY-MM-DD")
                    VInput(value = nextDueDate, onValueChange = { nextDueDate = it }, label = appString(StringKeys.HLTH_NEXT_DUE), placeholder = "YYYY-MM-DD")
                    VInput(value = administeredBy, onValueChange = { administeredBy = it }, label = appString(StringKeys.HLTH_ADMINISTERED_BY), placeholder = "e.g. Dr. Sharma")
                    VButton(
                        text = appString(StringKeys.HLTH_SAVE_RECORD),
                        onClick = {
                            onAdd(
                                AddImmunizationRequest(
                                    studentId = studentId,
                                    vaccineName = vaccineName.trim(),
                                    doseNumber = doseNumber.trim().toIntOrNull() ?: 1,
                                    dateAdministered = dateAdministered.trim(),
                                    nextDueDate = nextDueDate.trim().ifBlank { null },
                                    administeredBy = administeredBy.trim().ifBlank { null },
                                ),
                            )
                            showAdd = false
                            vaccineName = ""
                            doseNumber = "1"
                            dateAdministered = ""
                            nextDueDate = ""
                            administeredBy = ""
                        },
                        full = true,
                        loading = isSaving,
                        enabled = vaccineName.isNotBlank() && dateAdministered.isNotBlank(),
                        tone = VButtonTone.Lavender,
                    )
                }
            }
        }

        if (immunizations.isEmpty()) {
            VEmptyHealthState(message = appString(StringKeys.HLTH_NO_IMMUNIZATIONS))
        } else {
            immunizations.forEach { imm ->
                VCard {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(imm.vaccineName, style = VTheme.type.bodyStrong.colored(c.ink))
                            Text(appString(StringKeys.HLTH_DOSE, "number" to imm.doseNumber.toString(), "date" to imm.dateAdministered), style = VTheme.type.caption.colored(c.ink2))
                            if (!imm.administeredBy.isNullOrBlank()) {
                                Text(appString(StringKeys.HLTH_BY, "name" to imm.administeredBy), style = VTheme.type.caption.colored(c.ink3))
                            }
                            if (!imm.nextDueDate.isNullOrBlank()) {
                                Text(appString(StringKeys.HLTH_NEXT_DUE_LABEL, "date" to imm.nextDueDate), style = VTheme.type.caption.colored(c.tealDeep))
                            }
                        }
                        Box(
                            Modifier.size(36.dp).clip(CircleShape).background(c.teal.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(VIcons.Heart, contentDescription = null, tint = c.tealDeep, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

// ── Incidents tab ────────────────────────────────────────────────

@Composable
private fun IncidentsTab(
    incidents: List<HealthIncidentDto>,
    studentId: String,
    isSaving: Boolean,
    onLog: (LogIncidentRequest) -> Unit,
    onMarkNotified: (String) -> Unit,
) {
    val c = VTheme.colors
    var showAdd by remember { mutableStateOf(false) }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var treatment by remember { mutableStateOf("") }
    var medicationGiven by remember { mutableStateOf("") }
    var severity by remember { mutableStateOf("minor") }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        VSectionHeader(appString(StringKeys.HLTH_HEALTH_INCIDENTS)) {
            VButton(
                text = appString(StringKeys.HLTH_LOG),
                onClick = { showAdd = !showAdd },
                size = VButtonSize.Sm,
                variant = VButtonVariant.Secondary,
                tone = VButtonTone.Rose,
            )
        }

        if (showAdd) {
            VCard {
                Column(
                    Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    VInput(value = date, onValueChange = { date = it }, label = appString(StringKeys.HLTH_DATE), placeholder = "YYYY-MM-DD")
                    VInput(value = time, onValueChange = { time = it }, label = appString(StringKeys.HLTH_TIME), placeholder = "e.g. 14:30")
                    VInput(value = description, onValueChange = { description = it }, label = appString(StringKeys.HLTH_DESCRIPTION), placeholder = "What happened?")
                    VInput(value = treatment, onValueChange = { treatment = it }, label = appString(StringKeys.HLTH_TREATMENT), placeholder = "e.g. Rest + cold compress")
                    VInput(value = medicationGiven, onValueChange = { medicationGiven = it }, label = appString(StringKeys.HLTH_MEDICATION_GIVEN), placeholder = "e.g. Paracetamol 250mg")
                    VInput(value = severity, onValueChange = { severity = it }, label = appString(StringKeys.HLTH_SEVERITY), placeholder = "minor, moderate, or major")
                    VButton(
                        text = appString(StringKeys.HLTH_LOG_INCIDENT),
                        onClick = {
                            onLog(
                                LogIncidentRequest(
                                    studentId = studentId,
                                    date = date.trim(),
                                    time = time.trim().ifBlank { null },
                                    description = description.trim(),
                                    treatment = treatment.trim().ifBlank { null },
                                    medicationGiven = medicationGiven.trim().ifBlank { null },
                                    severity = severity.trim().ifBlank { "minor" },
                                ),
                            )
                            showAdd = false
                            date = ""
                            time = ""
                            description = ""
                            treatment = ""
                            medicationGiven = ""
                            severity = "minor"
                        },
                        full = true,
                        loading = isSaving,
                        enabled = date.isNotBlank() && description.isNotBlank(),
                        tone = VButtonTone.Rose,
                    )
                }
            }
        }

        if (incidents.isEmpty()) {
            VEmptyHealthState(message = appString(StringKeys.HLTH_NO_INCIDENTS))
        } else {
            incidents.forEach { inc ->
                VCard {
                    Column(
                        Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(inc.date, style = VTheme.type.bodyStrong.colored(c.ink))
                            val pair = when (inc.severity) {
                                "major" -> appString(StringKeys.HLTH_SEVERITY_MAJOR) to VBadgeTone.Danger
                                "moderate" -> appString(StringKeys.HLTH_SEVERITY_MODERATE) to VBadgeTone.Warning
                                else -> appString(StringKeys.HLTH_SEVERITY_MINOR) to VBadgeTone.Success
                            }
                            VBadge(text = pair.first, tone = pair.second)
                        }
                        Text(inc.description, style = VTheme.type.body.colored(c.ink))
                        if (!inc.treatment.isNullOrBlank()) {
                            Text(appString(StringKeys.HLTH_TREATMENT_LABEL, "treatment" to inc.treatment), style = VTheme.type.caption.colored(c.ink2))
                        }
                        if (!inc.medicationGiven.isNullOrBlank()) {
                            Text(appString(StringKeys.HLTH_MEDICATION_LABEL, "medication" to inc.medicationGiven), style = VTheme.type.caption.colored(c.ink2))
                        }
                        if (!inc.time.isNullOrBlank()) {
                            Text(appString(StringKeys.HLTH_TIME_LABEL, "time" to inc.time), style = VTheme.type.caption.colored(c.ink3))
                        }
                        Row(
                            Modifier.fillMaxWidth().padding(top = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            if (inc.parentNotified) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(VIcons.Check, contentDescription = null, tint = c.tealDeep, modifier = Modifier.size(14.dp))
                                    Text(appString(StringKeys.HLTH_PARENT_NOTIFIED), style = VTheme.type.caption.colored(c.tealDeep))
                                }
                            } else {
                                VButton(
                                    text = appString(StringKeys.HLTH_MARK_NOTIFIED),
                                    onClick = { onMarkNotified(inc.id) },
                                    size = VButtonSize.Sm,
                                    variant = VButtonVariant.Ghost,
                                    tone = VButtonTone.Teal,
                                )
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun VEmptyHealthState(message: String) {
    val c = VTheme.colors
    Box(
        Modifier.fillMaxWidth().padding(vertical = 32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(message, style = VTheme.type.caption.colored(c.ink3))
    }
}
