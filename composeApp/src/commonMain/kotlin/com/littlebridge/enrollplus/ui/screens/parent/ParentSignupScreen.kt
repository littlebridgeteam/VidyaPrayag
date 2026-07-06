package com.littlebridge.enrollplus.ui.screens.parent

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.auth.presentation.AuthViewModel
import com.littlebridge.enrollplus.ui.components.VBackHeader
import com.littlebridge.enrollplus.ui.components.VButton
import com.littlebridge.enrollplus.ui.components.VDividerWithText
import com.littlebridge.enrollplus.ui.components.VInput
import com.littlebridge.enrollplus.ui.components.VOTPInput
import com.littlebridge.enrollplus.ui.components.VProgressBar
import com.littlebridge.enrollplus.ui.components.VSSOButton
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.tokens.VMotion

private enum class ParentSignupStep { Form, Otp, Success }

@Composable
fun ParentSignupScreen(
    viewModel: AuthViewModel,
    onBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onAuthSuccess: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    var step by remember { mutableStateOf(ParentSignupStep.Form) }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }

    if (state.authResponse != null && step != ParentSignupStep.Success) {
        LaunchedEffect(state.authResponse) {
            step = ParentSignupStep.Success
        }
    }

    val progress = when (step) {
        ParentSignupStep.Form -> 0.5f
        ParentSignupStep.Otp -> 1f
        ParentSignupStep.Success -> 1f
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VColors.cream),
    ) {
        VBackHeader(onBack = {
            if (step == ParentSignupStep.Otp) {
                step = ParentSignupStep.Form
                viewModel.resetOtpState()
            } else if (step == ParentSignupStep.Form) {
                onBack()
            } else {
                onAuthSuccess()
            }
        })

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp)
                .padding(top = 16.dp, bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            // Header
            Column {
                Text(
                    text = "Parent Sign Up", // TODO: i18n
                    style = VTypography.h2,
                    color = VColors.ink,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Create your account to get started", // TODO: i18n
                    style = VTypography.bodySmall,
                    color = VColors.ink2,
                )
            }

            // SSO (only on form step)
            if (step == ParentSignupStep.Form) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    VSSOButton(
                        text = "Google",
                        icon = Icons.Default.Check,
                        onClick = { /* TODO: Google SSO */ },
                        modifier = Modifier.weight(1f),
                    )
                    VSSOButton(
                        text = "Apple",
                        icon = Icons.Default.Check,
                        onClick = { /* TODO: Apple SSO */ },
                        modifier = Modifier.weight(1f),
                    )
                }

                VDividerWithText("or use your phone number")
            }

            // Progress bar
            VProgressBar(progress = progress)

            // Step content
            when (step) {
                ParentSignupStep.Form -> {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        VInput(
                            label = "Full Name", // TODO: i18n
                            value = name,
                            onValueChange = { name = it },
                            placeholder = "Priya Sharma",
                        )
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
                            text = "Continue",
                            onClick = { viewModel.sendOtpForSignup(phone) },
                            enabled = name.isNotBlank() && phone.length == 10,
                            loading = state.isLoading,
                            icon = Icons.AutoMirrored.Filled.ArrowForward,
                        )
                    }
                }

                ParentSignupStep.Otp -> {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = "Verify your phone number\nEnter the 6-digit code sent to\n+91 ${phone.take(5)} ${phone.drop(5)}",
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
                            text = "Verify & Create",
                            onClick = { viewModel.verifyAndSignupParent(name, otp) },
                            enabled = otp.length == 6,
                            loading = state.isLoading,
                            icon = Icons.AutoMirrored.Filled.ArrowForward,
                        )
                    }
                }

                ParentSignupStep.Success -> {
                    val popScale = animateFloatAsState(
                        targetValue = 1f,
                        animationSpec = tween(VMotion.durDefault, easing = VMotion.ease),
                        label = "pop",
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .scale(popScale.value)
                                .background(VColors.successSoft, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = VColors.success,
                                modifier = Modifier.size(36.dp),
                            )
                        }
                        Text(
                            text = "Account Created", // TODO: i18n
                            style = VTypography.h3,
                            color = VColors.ink,
                        )
                        Text(
                            text = "Your account is ready. You can link your child's school after signing in.", // TODO: i18n
                            style = VTypography.body,
                            color = VColors.ink2,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(16.dp))
                        VButton(
                            text = "Continue",
                            onClick = onAuthSuccess,
                            icon = Icons.AutoMirrored.Filled.ArrowForward,
                        )
                    }
                }
            }

            // Footer
            if (step != ParentSignupStep.Success) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 8.dp),
                ) {
                    Text(
                        text = "Already have an account?", // TODO: i18n
                        style = VTypography.caption,
                        color = VColors.ink3,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )
                    VButton(
                        text = "Sign in",
                        onClick = onNavigateToLogin,
                        variant = com.littlebridge.enrollplus.ui.components.VButtonVariant.Outline,
                    )
                }
            }
        }
    }
}
