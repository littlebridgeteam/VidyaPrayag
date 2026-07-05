package com.littlebridge.enrollplus.ui.v2.screens.premium.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.auth.domain.model.AuthFlow
import com.littlebridge.enrollplus.feature.auth.presentation.AuthStep
import com.littlebridge.enrollplus.feature.auth.presentation.AuthViewModel
import com.littlebridge.enrollplus.ui.v2.components.buttons.VPrimaryButton
import com.littlebridge.enrollplus.ui.v2.components.form.VTextInput
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

/**
 * Premium parent auth — phone + OTP sign-in flow.
 *
 * M3 Expressive design using premium components:
 * - VTextInput for form fields with focus glow
 * - VPrimaryButton for CTA with shape-morph on press
 * - AuthScaffoldPremium for the shared chrome
 *
 * Reuses the existing AuthViewModel — same data flow, new UI layer.
 */
@Composable
fun ParentAuthScreen(
    onAuthSuccess: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()

    LaunchedEffect(Unit) {
        viewModel.reset()
        viewModel.onRoleChanged("PARENT")
    }
    LaunchedEffect(state.isAuthSuccessful) { if (state.isAuthSuccessful) onAuthSuccess() }

    AuthScaffoldPremium(
        title = "Parent Welcome",
        subtitle = "Sign in with your phone number",
        error = state.error,
        onBack = onBack,
        modifier = modifier,
    ) {
        when (state.step) {
            AuthStep.Identifier -> {
                VTextInput(
                    value = state.identifier,
                    onValueChange = viewModel::onIdentifierChanged,
                    label = "Mobile Number",
                    placeholder = "+91 98XXX XXXXX",
                    keyboardType = KeyboardType.Phone,
                    authStyle = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            AuthStep.SignupDetails -> {
                VTextInput(
                    value = state.name,
                    onValueChange = viewModel::onNameChanged,
                    label = "Your Name",
                    placeholder = "Enter your full name",
                    keyboardType = KeyboardType.Text,
                    authStyle = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            AuthStep.Otp -> {
                if (state.flow == AuthFlow.SIGNUP_PHONE) {
                    VTextInput(
                        value = state.name,
                        onValueChange = viewModel::onNameChanged,
                        label = "Your Name",
                        placeholder = "Enter your full name",
                        keyboardType = KeyboardType.Text,
                        authStyle = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(16.dp))
                }
                VTextInput(
                    value = state.otp,
                    onValueChange = viewModel::onOtpChanged,
                    label = "OTP Code",
                    placeholder = "6-digit code",
                    keyboardType = KeyboardType.Number,
                    authStyle = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "OTP sent to ${state.identifier.ifBlank { "your phone" }}",
                    style = VTypography.UpdateTime.copy(color = VColors.OnSurfaceVariant),
                )
            }
            AuthStep.LoginPassword -> {
                VTextInput(
                    value = state.otp,
                    onValueChange = viewModel::onOtpChanged,
                    label = "OTP Code",
                    placeholder = "6-digit code",
                    keyboardType = KeyboardType.Number,
                    authStyle = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        val ctaLabel = when (state.step) {
            AuthStep.Identifier -> "Send OTP"
            AuthStep.Otp -> "Verify & Continue"
            else -> "Continue"
        }
        VPrimaryButton(
            text = ctaLabel,
            onClick = {
                if (state.step == AuthStep.Identifier) viewModel.onContinue()
                else viewModel.onSubmit()
            },
            enabled = !state.isLoading,
            trailing = {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = VColors.OnPrimary,
                    modifier = Modifier.height(18.dp),
                )
            },
            modifier = Modifier.fillMaxWidth(),
        )

        if (state.step != AuthStep.Identifier) {
            Spacer(Modifier.height(8.dp))
            AuthBackLinkPremium(
                onClick = viewModel::goBack,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }
    }
}
