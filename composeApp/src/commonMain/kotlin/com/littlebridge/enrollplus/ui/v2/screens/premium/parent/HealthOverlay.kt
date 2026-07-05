package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.core.state.SelectedChildHolder
import com.littlebridge.enrollplus.feature.health.domain.model.HealthProfileDto
import com.littlebridge.enrollplus.feature.health.domain.model.ImmunizationDto
import com.littlebridge.enrollplus.feature.health.presentation.ParentHealthViewModel
import com.littlebridge.enrollplus.ui.v2.components.misc.VShimmerBoxPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VStateHostPremium
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HealthOverlay(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ParentHealthViewModel = koinViewModel(),
    selectedChildHolder: SelectedChildHolder = koinInject(),
) {
    val state by viewModel.state.collectAsStateV2()
    val childId by selectedChildHolder.selectedChildId.collectAsStateV2()

    LaunchedEffect(childId) {
        childId?.let { viewModel.load(it) }
    }

    ParentOverlayScaffold(
        title = "Health",
        onBack = onBack,
        modifier = modifier,
    ) {
        VStateHostPremium(
            loading = state.isLoading,
            error = state.error,
            isEmpty = state.data == null && !state.isLoading,
            modifier = Modifier.fillMaxSize(),
            emptyTitle = "No health records",
            emptyIcon = Icons.Filled.HealthAndSafety,
            onRetry = { childId?.let { viewModel.load(it) } },
            skeleton = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    VShimmerBoxPremium(height = 160.dp, shape = VShapes.Xl)
                    VShimmerBoxPremium(height = 100.dp, shape = VShapes.Lg)
                    VShimmerBoxPremium(height = 100.dp, shape = VShapes.Lg)
                }
            },
        ) {
            val data = state.data ?: return@VStateHostPremium

            // 1. Health profile card
            data.profile?.let { profile ->
                HealthProfileCard(profile = profile)
                Spacer(Modifier.height(20.dp))
            }

            // 2. Emergency contact
            data.profile?.let { profile ->
                if (!profile.emergencyContactName.isNullOrBlank() || !profile.emergencyContactPhone.isNullOrBlank()) {
                    EmergencyContactCard(profile = profile)
                    Spacer(Modifier.height(20.dp))
                }
            }

            // 3. Immunizations
            if (data.immunizations.isNotEmpty()) {
                Text(
                    text = "Immunizations",
                    style = VTypography.SectionHeader.copy(color = VColors.OnSurface),
                )
                Spacer(Modifier.height(12.dp))
                data.immunizations.forEach { imm ->
                    ImmunizationCard(immunization = imm)
                    Spacer(Modifier.height(8.dp))
                }
            }

            // 4. Incidents
            if (data.incidents.isNotEmpty()) {
                Spacer(Modifier.height(20.dp))
                Text(
                    text = "Medical Incidents",
                    style = VTypography.SectionHeader.copy(color = VColors.OnSurface),
                )
                Spacer(Modifier.height(12.dp))
                data.incidents.forEach { incident ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(VShapes.Lg)
                            .background(VColors.SurfaceContainerLow)
                            .padding(16.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(Icons.Filled.Healing, contentDescription = null, tint = VColors.WarmOrange, modifier = Modifier.size(18.dp))
                            Text(
                                text = incident.date,
                                style = VTypography.ThreadTime.copy(color = VColors.Outline),
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = incident.description,
                            style = VTypography.BodyMedium.copy(color = VColors.OnSurfaceVariant),
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun HealthProfileCard(profile: HealthProfileDto) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.Xl)
            .background(VColors.SurfaceContainerLowest)
            .padding(20.dp),
    ) {
        Text(
            text = "Health Profile",
            style = VTypography.UpdateTitle.copy(color = VColors.OnSurface),
        )
        Spacer(Modifier.height(16.dp))

        // Stats grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            HealthStat(
                icon = Icons.Filled.Bloodtype,
                label = "Blood Group",
                value = profile.bloodGroup ?: "—",
                modifier = Modifier.weight(1f),
            )
            HealthStat(
                icon = Icons.Filled.Height,
                label = "Height",
                value = profile.heightCm?.let { "${it.toInt()} cm" } ?: "—",
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            HealthStat(
                icon = Icons.Filled.MonitorWeight,
                label = "Weight",
                value = profile.weightKg?.let { "${it.toInt()} kg" } ?: "—",
                modifier = Modifier.weight(1f),
            )
            HealthStat(
                icon = Icons.Filled.LocalHospital,
                label = "Doctor",
                value = profile.doctorName ?: "—",
                modifier = Modifier.weight(1f),
            )
        }

        // Allergies
        if (profile.allergies.isNotBlank() && profile.allergies != "[]") {
            Spacer(Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Box(
                    Modifier.size(8.dp).clip(CircleShape).background(VColors.Error),
                )
                Text(
                    text = "Allergies: ${profile.allergies}",
                    style = VTypography.BodyMedium.copy(color = VColors.OnSurfaceVariant),
                )
            }
        }

        // Chronic conditions
        if (profile.chronicConditions.isNotBlank() && profile.chronicConditions != "[]") {
            Spacer(Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Box(
                    Modifier.size(8.dp).clip(CircleShape).background(VColors.WarmOrange),
                )
                Text(
                    text = "Chronic: ${profile.chronicConditions}",
                    style = VTypography.BodyMedium.copy(color = VColors.OnSurfaceVariant),
                )
            }
        }

        // Medications
        if (profile.medications.isNotBlank() && profile.medications != "[]") {
            Spacer(Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Box(
                    Modifier.size(8.dp).clip(CircleShape).background(VColors.Primary),
                )
                Text(
                    text = "Medications: ${profile.medications}",
                    style = VTypography.BodyMedium.copy(color = VColors.OnSurfaceVariant),
                )
            }
        }
    }
}

@Composable
private fun HealthStat(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(VShapes.Lg)
            .background(VColors.SurfaceContainerLow)
            .padding(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(icon, contentDescription = null, tint = VColors.OnSurfaceVariant, modifier = Modifier.size(16.dp))
            Text(
                text = label,
                style = VTypography.QuickStatLabel.copy(color = VColors.OnSurfaceVariant),
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = value,
            style = VTypography.QuickStatValue.copy(color = VColors.OnSurface),
        )
    }
}

@Composable
private fun EmergencyContactCard(profile: HealthProfileDto) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.Xl)
            .background(VColors.ErrorContainer.copy(alpha = 0.3f))
            .padding(20.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(Icons.Filled.Emergency, contentDescription = null, tint = VColors.Error, modifier = Modifier.size(20.dp))
            Text(
                text = "Emergency Contact",
                style = VTypography.UpdateTitle.copy(color = VColors.OnSurface),
            )
        }
        Spacer(Modifier.height(12.dp))
        if (!profile.emergencyContactName.isNullOrBlank()) {
            val name = profile.emergencyContactName!!
            Text(
                text = name,
                style = VTypography.BodyLarge.copy(color = VColors.OnSurface),
            )
        }
        if (!profile.emergencyContactPhone.isNullOrBlank()) {
            val phone = profile.emergencyContactPhone!!
            Spacer(Modifier.height(4.dp))
            Text(
                text = phone,
                style = VTypography.BodyMedium.copy(color = VColors.OnSurfaceVariant),
            )
        }
        if (!profile.doctorPhone.isNullOrBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Doctor: ${profile.doctorPhone}",
                style = VTypography.ThreadPreview.copy(color = VColors.OnSurfaceVariant),
            )
        }
    }
}

@Composable
private fun ImmunizationCard(immunization: ImmunizationDto) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.Lg)
            .background(VColors.SurfaceContainerLow)
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = immunization.vaccineName,
                style = VTypography.UpdateTitle.copy(color = VColors.OnSurface),
            )
            Box(
                modifier = Modifier
                    .clip(VShapes.Full)
                    .background(VColors.PrimaryContainer)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Text(
                    text = "Dose ${immunization.doseNumber}",
                    style = VTypography.ThreadTime.copy(color = VColors.OnPrimaryContainer),
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Administered: ${immunization.dateAdministered}",
            style = VTypography.ThreadPreview.copy(color = VColors.OnSurfaceVariant),
        )
        if (!immunization.nextDueDate.isNullOrBlank()) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Next due: ${immunization.nextDueDate}",
                style = VTypography.ThreadPreview.copy(color = VColors.WarmOrange),
            )
        }
        if (!immunization.administeredBy.isNullOrBlank()) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = "By: ${immunization.administeredBy}",
                style = VTypography.ThreadTime.copy(color = VColors.Outline),
            )
        }
    }
}
