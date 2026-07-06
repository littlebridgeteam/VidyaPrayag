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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.health.presentation.AdminHealthViewModel
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.cards.VListTilePremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VStateHostPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VShimmerListPremium
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HealthRecordsPremium(
    studentId: String,
    studentName: String,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: AdminHealthViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    LaunchedEffect(studentId) { viewModel.load(studentId) }

    VStateHostPremium(
        loading = state.isLoading,
        error = state.error,
        isEmpty = state.profile == null && !state.isLoading,
        emptyTitle = "No health records",
        modifier = modifier.fillMaxSize(),
        skeleton = {
            Column(
                Modifier.fillMaxSize().statusBarsPadding().padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                VShimmerListPremium(itemCount = 4)
            }
        },
    ) {
        val profile = state.profile ?: return@VStateHostPremium

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .imePadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            VBackHeader(title = "$studentName - Health", onBack = onBack)

            Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Medical Profile", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                VListTilePremium(title = "Blood Group", subtitle = profile.bloodGroup ?: "Not specified", onClick = {}, leadingIcon = VIcons.ShieldCheck)
                VListTilePremium(title = "Allergies", subtitle = profile.allergies.ifBlank { "None recorded" }, onClick = {}, leadingIcon = VIcons.AlertCircle)
                VListTilePremium(title = "Conditions", subtitle = profile.chronicConditions.ifBlank { "None recorded" }, onClick = {}, leadingIcon = VIcons.AlertTriangle)

                Text("Immunizations", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                state.immunizations.forEach { imm ->
                    VListTilePremium(title = imm.vaccineName, subtitle = imm.dateAdministered, onClick = {}, leadingIcon = VIcons.Check)
                }

                Text("Incidents", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                state.incidents.forEach { inc ->
                    VListTilePremium(title = inc.description, subtitle = "${inc.date} - ${inc.severity}", onClick = {}, leadingIcon = VIcons.AlertCircle)
                }
            }
        }
    }
}
