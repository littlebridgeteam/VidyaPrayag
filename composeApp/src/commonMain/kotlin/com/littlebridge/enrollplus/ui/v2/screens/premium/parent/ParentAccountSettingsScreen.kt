package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.parent.presentation.ParentProfileViewModel
import com.littlebridge.enrollplus.ui.v2.components.buttons.VPrimaryButton
import com.littlebridge.enrollplus.ui.v2.components.form.VTextInput
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.PremiumTheme
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ParentAccountSettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ParentProfileViewModel = koinViewModel(),
) = PremiumTheme(isDark = false) {
    val state by viewModel.state.collectAsStateV2()
    val profile = state.profile

    var name by remember(profile?.name) { mutableStateOf(profile?.name ?: "") }
    var phone by remember(profile?.phone) { mutableStateOf(profile?.phone ?: "") }
    var email by remember(profile?.email) { mutableStateOf(profile?.email ?: "") }

    ParentOverlayScaffold(
        title = "Account Settings",
        onBack = onBack,
        modifier = modifier,
    ) {
        Text(
            "Parent Profile",
            style = VTypography.UpdateTitle.copy(
                color = VColors.OnSurface,
                fontWeight = FontWeight.Bold,
            ),
        )
        Spacer(Modifier.height(16.dp))

        VTextInput(
            value = name,
            onValueChange = { name = it },
            label = "Name",
            modifier = Modifier.fillMaxWidth(),
            authStyle = false,
        )
        Spacer(Modifier.height(12.dp))

        VTextInput(
            value = phone,
            onValueChange = { phone = it },
            label = "Phone",
            modifier = Modifier.fillMaxWidth(),
            authStyle = false,
        )
        Spacer(Modifier.height(12.dp))

        VTextInput(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            modifier = Modifier.fillMaxWidth(),
            authStyle = false,
        )
        Spacer(Modifier.height(24.dp))

        VPrimaryButton(
            text = "Save Changes",
            onClick = { /* TODO: save profile changes via viewModel */ },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
