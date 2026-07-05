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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.admin.presentation.StaffViewModel
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.cards.HeroStatPremium
import com.littlebridge.enrollplus.ui.v2.components.cards.VGradientHeroPremium
import com.littlebridge.enrollplus.ui.v2.components.cards.VListTilePremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VStateHostPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VShimmerListPremium
import com.littlebridge.enrollplus.ui.v2.components.overlay.VConfirmDialogPremium
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StaffProfilePremium(
    staffId: String,
    onBack: () -> Unit = {},
    onRemoved: () -> Unit = onBack,
    modifier: Modifier = Modifier,
    viewModel: StaffViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    val staff = state.staff.find { it.id == staffId }

    var showDelete by remember { mutableStateOf(false) }

    VStateHostPremium(
        loading = state.isLoading && staff == null,
        error = state.error,
        isEmpty = staff == null && !state.isLoading,
        emptyTitle = "Staff member not found",
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
        if (staff == null) return@VStateHostPremium

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
            VBackHeader(title = staff.fullName, onBack = onBack)

            VGradientHeroPremium(
                title = staff.fullName,
                subtitle = staff.role.ifBlank { "Staff" },
                stats = listOf(
                    HeroStatPremium(staff.role.ifBlank { "—" }, "Role"),
                    HeroStatPremium(staff.department ?: "—", "Department"),
                ),
                onClick = {},
                modifier = Modifier.padding(horizontal = 20.dp),
            )

            Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Details", style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
                VListTilePremium(title = "Phone", subtitle = staff.phone ?: "Not provided", onClick = {}, leadingIcon = VIcons.Phone)
                VListTilePremium(title = "Email", subtitle = staff.email ?: "Not provided", onClick = {}, leadingIcon = VIcons.Mail)

                VListTilePremium(
                    title = "Remove Staff",
                    subtitle = "Soft-delete this staff record",
                    onClick = { showDelete = true },
                    leadingIcon = VIcons.Close,
                )
            }
        }
    }

    VConfirmDialogPremium(
        visible = showDelete,
        title = "Remove Staff",
        message = "Are you sure you want to remove ${staff?.fullName ?: "this staff member"}?",
        confirmLabel = "Remove",
        onConfirm = { viewModel.removeStaff(staffId); showDelete = false; onRemoved() },
        onDismiss = { showDelete = false },
        isDestructive = true,
    )
}
