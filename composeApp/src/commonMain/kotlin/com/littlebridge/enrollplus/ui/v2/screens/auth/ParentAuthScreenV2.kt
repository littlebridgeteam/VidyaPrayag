package com.littlebridge.enrollplus.ui.v2.screens.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.auth.domain.model.AuthFlow
import com.littlebridge.enrollplus.feature.auth.presentation.AuthStep
import com.littlebridge.enrollplus.feature.auth.presentation.AuthViewModel
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonSize
import com.littlebridge.enrollplus.ui.v2.components.VButtonTone
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VInput
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.theme.VTheme
import com.littlebridge.enrollplus.ui.v2.theme.colored
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.locale.appString
import org.koin.compose.viewmodel.koinViewModel

/**
 * ParentAuthScreenV2 — the parent-scoped sign-in / sign-up (PHASE 4).
 *
 * The parent-only auth surface, locked to the **PARENT** role with the OTP (phone) flow
 * only. There is zero admin UI here (LAW 3: no role leakage) — no portal selector, no password
 * field, no email/credential affordance. Copy speaks only to families.
 *
 * Wiring: binds to the real [AuthViewModel]. On mount we pin the role to PARENT. The phone→OTP flow
 * is exactly the existing repository flow (`onContinue()` triggers `sendOtp`, `onSubmit()` verifies).
 * On `isAuthSuccessful` we call [onAuthSuccess]; the host (NavGraphV2) then routes on child-link
 * state (PHASE 6).
 */
@Composable
fun ParentAuthScreenV2(
    onAuthSuccess: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()

    // Clear any state left over from a prior session (the AuthViewModel is reused across a
    // logout → landing → login round-trip), then pin the role to PARENT for this scoped screen.
    LaunchedEffect(Unit) {
        viewModel.reset()
        viewModel.onRoleChanged("PARENT")
    }
    LaunchedEffect(state.isAuthSuccessful) { if (state.isAuthSuccessful) onAuthSuccess() }

    AuthScaffoldV2(
        title = appString(StringKeys.AUTH_PARENT_WELCOME),
        subtitle = appString(StringKeys.AUTH_PARENT_SUBTITLE),
        error = state.error,
        onBack = onBack,
        modifier = modifier,
    ) {
        val c = VTheme.colors
        when (state.step) {
            AuthStep.Identifier -> {
                VInput(
                    value = state.identifier,
                    onValueChange = viewModel::onIdentifierChanged,
                    label = appString(StringKeys.AUTH_MOBILE_NUMBER),
                    placeholder = "+91 98XXX XXXXX",
                    leadingIcon = VIcons.Phone,
                    keyboardType = KeyboardType.Phone,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            AuthStep.SignupDetails -> {
                // New family: we still collect a display name (phone signup asks for name with OTP).
                VInput(
                    value = state.name,
                    onValueChange = viewModel::onNameChanged,
                    label = appString(StringKeys.AUTH_YOUR_NAME),
                    placeholder = appString(StringKeys.AUTH_FULL_NAME_PH),
                    leadingIcon = VIcons.User,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            AuthStep.Otp -> {
                // RA-S02: the name field is for NEW families only. A returning parent
                // (AuthFlow.LOGIN_PHONE) already has a name on file, so asking for it at the
                // OTP step is wrong — show it ONLY when the resolved flow is SIGNUP_PHONE.
                if (state.flow == AuthFlow.SIGNUP_PHONE) {
                    VInput(
                        value = state.name,
                        onValueChange = viewModel::onNameChanged,
                        label = appString(StringKeys.AUTH_YOUR_NAME),
                        placeholder = appString(StringKeys.AUTH_FULL_NAME_PH),
                        leadingIcon = VIcons.User,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(16.dp))
                }
                VInput(
                    value = state.otp,
                    onValueChange = viewModel::onOtpChanged,
                    label = appString(StringKeys.AUTH_OTP),
                    placeholder = appString(StringKeys.AUTH_OTP_CODE_PH),
                    leadingIcon = VIcons.ShieldCheck,
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    appString(StringKeys.AUTH_OTP_SENT_TO).replace("{phone}", state.identifier.ifBlank { appString(StringKeys.AUTH_YOUR_PHONE) }),
                    style = VTheme.type.caption.colored(c.ink3),
                )
            }
            // Parents never see the password step — but keep the branch exhaustive & safe.
            AuthStep.LoginPassword -> {
                VInput(
                    value = state.otp,
                    onValueChange = viewModel::onOtpChanged,
                    label = appString(StringKeys.AUTH_OTP),
                    placeholder = appString(StringKeys.AUTH_OTP_CODE_PH),
                    leadingIcon = VIcons.ShieldCheck,
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        val ctaLabel = when (state.step) {
            AuthStep.Identifier -> appString(StringKeys.AUTH_SEND_OTP)
            AuthStep.Otp -> appString(StringKeys.AUTH_VERIFY_CONTINUE)
            else -> appString(StringKeys.COMMON_BUTTON_CONTINUE)
        }
        VButton(
            text = ctaLabel,
            onClick = { if (state.step == AuthStep.Identifier) viewModel.onContinue() else viewModel.onSubmit() },
            full = true,
            size = VButtonSize.Lg,
            tone = VButtonTone.Teal,
            loading = state.isLoading,
            trailing = { Icon(VIcons.ArrowRight, contentDescription = null, modifier = Modifier.size(16.dp)) },
        )

        if (state.step != AuthStep.Identifier) {
            Spacer(Modifier.height(8.dp))
            AuthBackLink(onClick = viewModel::goBack, modifier = Modifier.align(androidx.compose.ui.Alignment.CenterHorizontally))
        }
    }
}
