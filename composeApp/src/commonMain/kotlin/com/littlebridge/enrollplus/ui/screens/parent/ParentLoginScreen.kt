package com.littlebridge.enrollplus.ui.screens.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.auth.presentation.AuthViewModel
import com.littlebridge.enrollplus.ui.components.VBackHeader
import com.littlebridge.enrollplus.ui.components.VButton
import com.littlebridge.enrollplus.ui.components.VDividerWithText
import com.littlebridge.enrollplus.ui.components.VInput
import com.littlebridge.enrollplus.ui.components.VOTPInput
import com.littlebridge.enrollplus.ui.components.VSSOButton
import com.littlebridge.enrollplus.ui.components.AppleIcon
import com.littlebridge.enrollplus.ui.components.GoogleIcon
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography
import kotlinx.coroutines.launch

@Composable
fun ParentLoginScreen(
    viewModel: AuthViewModel,
    onBack: () -> Unit,
    onNavigateToSignup: () -> Unit,
    onAuthSuccess: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    var phone by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Reset stale state when this screen enters composition
    LaunchedEffect(Unit) {
        viewModel.resetAll()
    }

    if (state.authResponse != null) {
        LaunchedEffect(state.authResponse) {
            onAuthSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VColors.cream),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
        ) {
        VBackHeader(onBack = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp)
                .padding(top = 24.dp, bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            // Header
            Column {
                Text(
                    text = "Parent Sign In", // TODO: i18n
                    style = VTypography.h2,
                    color = VColors.ink,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Enter your phone number to receive a verification code", // TODO: i18n
                    style = VTypography.bodySmall,
                    color = VColors.ink2,
                )
            }

            // SSO
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                VSSOButton(
                    text = "Google",
                    icon = GoogleIcon,
                    onClick = { scope.launch { snackbarHostState.showSnackbar("Coming Soon") } },
                    modifier = Modifier.weight(1f),
                )
                VSSOButton(
                    text = "Apple",
                    icon = AppleIcon,
                    onClick = { scope.launch { snackbarHostState.showSnackbar("Coming Soon") } },
                    modifier = Modifier.weight(1f),
                )
            }

            VDividerWithText("or use your phone number")

            // Phone section or OTP section
            if (!state.otpSent) {
                Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                    VInput(
                        label = "Phone Number", // TODO: i18n
                        value = phone,
                        onValueChange = { if (it.length <= 10 && it.all { c -> c.isDigit() }) phone = it },
                        placeholder = "98765 43210",
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone,
                        prefix = "+91",
                    )
                    if (state.error != null) {
                        Text(
                            text = state.error!!,
                            style = VTypography.caption,
                            color = VColors.error,
                        )
                    }
                    VButton(
                        text = "Send OTP",
                        onClick = { viewModel.sendOtpForLogin(phone) },
                        enabled = phone.length == 10,
                        loading = state.isLoading,
                        icon = Icons.AutoMirrored.Filled.ArrowForward,
                    )
                }
            } else {
                // OTP section
                Column(
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    var otp by remember { mutableStateOf("") }

                    Text(
                        text = "Enter the 6-digit code sent to\n+91 ${phone.take(5)} ${phone.drop(5)}",
                        style = VTypography.bodySmall,
                        color = VColors.ink2,
                        textAlign = TextAlign.Center,
                    )

                    VOTPInput(
                        value = otp,
                        onValueChange = { otp = it },
                        isError = state.error != null,
                    )

                    if (state.error != null) {
                        Text(
                            text = state.error!!,
                            style = VTypography.caption,
                            color = VColors.error,
                            textAlign = TextAlign.Center,
                        )
                    }

                    // Resend row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        if (state.resendCountdown > 0) {
                            Text(
                                text = "Resend in 0:${state.resendCountdown.toString().padStart(2, '0')}",
                                style = VTypography.caption,
                                color = VColors.ink3,
                            )
                        } else {
                            Text(
                                text = "Didn't receive it?",
                                style = VTypography.caption,
                                color = VColors.ink3,
                            )
                            Text(
                                text = "Resend OTP",
                                style = VTypography.caption.copy(fontWeight = FontWeight.Bold),
                                color = VColors.violet,
                                modifier = Modifier.clickable { viewModel.resendOtp() },
                            )
                        }
                    }

                    VButton(
                        text = "Verify & Continue",
                        onClick = { viewModel.verifyAndLoginParent(otp) },
                        enabled = otp.length == 6,
                        loading = state.isLoading,
                        icon = Icons.AutoMirrored.Filled.ArrowForward,
                    )
                }
            }

            // Footer
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 8.dp),
            ) {
                Text(
                    text = "New to Enroll+?", // TODO: i18n
                    style = VTypography.caption,
                    color = VColors.ink3,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
                VButton(
                    text = "Create parent account",
                    onClick = onNavigateToSignup,
                    variant = com.littlebridge.enrollplus.ui.components.VButtonVariant.Outline,
                )
            }
        }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        ) { snackbarData ->
            Snackbar(
                snackbarData = snackbarData,
                containerColor = VColors.ink,
                contentColor = VColors.white,
            )
        }
    }
}
