package com.littlebridge.enrollplus.ui.screens.admin

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.auth.presentation.AuthViewModel
import com.littlebridge.enrollplus.ui.components.VBackHeader
import com.littlebridge.enrollplus.ui.components.VButton
import com.littlebridge.enrollplus.ui.components.VInput
import com.littlebridge.enrollplus.ui.components.VProgressBar
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.tokens.VMotion

private enum class AdminSignupStep { SchoolDetails, AdminDetails, Password, Success }

@Composable
fun AdminSignupScreen(
    viewModel: AuthViewModel,
    onBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onAuthSuccess: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    var step by remember { mutableStateOf(AdminSignupStep.SchoolDetails) }

    // Form fields
    var schoolName by remember { mutableStateOf("") }
    var board by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var schoolType by remember { mutableStateOf("") }
    var adminName by remember { mutableStateOf("") }
    var adminRole by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    if (state.authResponse != null && step != AdminSignupStep.Success) {
        LaunchedEffect(state.authResponse) {
            step = AdminSignupStep.Success
        }
    }

    val progress = when (step) {
        AdminSignupStep.SchoolDetails -> 0.33f
        AdminSignupStep.AdminDetails -> 0.66f
        AdminSignupStep.Password -> 1f
        AdminSignupStep.Success -> 1f
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VColors.cream),
    ) {
        VBackHeader(onBack = {
            when (step) {
                AdminSignupStep.AdminDetails -> step = AdminSignupStep.SchoolDetails
                AdminSignupStep.Password -> step = AdminSignupStep.AdminDetails
                AdminSignupStep.SchoolDetails -> onBack()
                AdminSignupStep.Success -> onAuthSuccess()
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
                    text = "Register Your School", // TODO: i18n
                    style = VTypography.h2,
                    color = VColors.ink,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Set up your management portal in minutes", // TODO: i18n
                    style = VTypography.bodySmall,
                    color = VColors.ink2,
                )
            }

            // Progress bar
            VProgressBar(progress = progress)

            // Step content
            when (step) {
                AdminSignupStep.SchoolDetails -> {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        VInput(
                            label = "School Name", // TODO: i18n
                            value = schoolName,
                            onValueChange = { schoolName = it },
                            placeholder = "Delhi Public School",
                        )
                        VInput(
                            label = "Board / Affiliation", // TODO: i18n
                            value = board,
                            onValueChange = { board = it },
                            placeholder = "CBSE",
                        )
                        VInput(
                            label = "City", // TODO: i18n
                            value = city,
                            onValueChange = { city = it },
                            placeholder = "New Delhi",
                        )
                        VInput(
                            label = "School Type", // TODO: i18n
                            value = schoolType,
                            onValueChange = { schoolType = it },
                            placeholder = "Full School (Nursery – 12)",
                        )
                        VButton(
                            text = "Continue",
                            onClick = { step = AdminSignupStep.AdminDetails },
                            enabled = schoolName.isNotBlank() && board.isNotBlank(),
                            icon = Icons.AutoMirrored.Filled.ArrowForward,
                        )
                    }
                }

                AdminSignupStep.AdminDetails -> {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        VInput(
                            label = "Your Full Name", // TODO: i18n
                            value = adminName,
                            onValueChange = { adminName = it },
                            placeholder = "Dr. Rajesh Sharma",
                        )
                        VInput(
                            label = "Your Role", // TODO: i18n
                            value = adminRole,
                            onValueChange = { adminRole = it },
                            placeholder = "Principal",
                        )
                        VInput(
                            label = "Email Address", // TODO: i18n
                            value = email,
                            onValueChange = { email = it },
                            placeholder = "principal@dps.edu.in",
                            keyboardType = KeyboardType.Email,
                        )
                        VInput(
                            label = "Phone Number", // TODO: i18n
                            value = phone,
                            onValueChange = { if (it.length <= 10 && it.all { c -> c.isDigit() }) phone = it },
                            placeholder = "98765 43210",
                            keyboardType = KeyboardType.Phone,
                            prefix = "+91",
                        )
                        VButton(
                            text = "Continue",
                            onClick = { step = AdminSignupStep.Password },
                            enabled = adminName.isNotBlank() && email.isNotBlank(),
                            icon = Icons.AutoMirrored.Filled.ArrowForward,
                        )
                    }
                }

                AdminSignupStep.Password -> {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        VInput(
                            label = "Create Password", // TODO: i18n
                            value = password,
                            onValueChange = { password = it },
                            placeholder = "Min. 8 characters",
                            keyboardType = KeyboardType.Password,
                            visualTransformation = PasswordVisualTransformation(),
                        )
                        VInput(
                            label = "Confirm Password", // TODO: i18n
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            placeholder = "Re-enter password",
                            keyboardType = KeyboardType.Password,
                            visualTransformation = PasswordVisualTransformation(),
                        )
                        // Info card
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(VColors.surfaceWarm, com.littlebridge.enrollplus.ui.tokens.VShapes.md)
                                .padding(16.dp),
                        ) {
                            Column {
                                Text(
                                    text = "Password requirements", // TODO: i18n
                                    style = VTypography.caption.copy(fontWeight = FontWeight.Bold),
                                    color = VColors.ink,
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "• At least 8 characters\n• One uppercase letter (A–Z)\n• One number (0–9)\n• One special character (!@#$%)",
                                    style = VTypography.caption,
                                    color = VColors.ink2,
                                )
                            }
                        }
                        if (state.error != null) {
                            Text(
                                text = state.error!!,
                                style = VTypography.caption,
                                color = VColors.error,
                            )
                        }
                        VButton(
                            text = "Create Account",
                            onClick = {
                                viewModel.registerSchool(
                                    adminName = adminName,
                                    email = email,
                                    password = password,
                                    schoolName = schoolName,
                                    board = board,
                                    schoolType = schoolType,
                                    city = city,
                                    state = null,
                                    contactPhone = phone,
                                )
                            },
                            enabled = password.length >= 8 && password == confirmPassword,
                            loading = state.isLoading,
                            icon = Icons.AutoMirrored.Filled.ArrowForward,
                        )
                    }
                }

                AdminSignupStep.Success -> {
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
                            text = "School Registered", // TODO: i18n
                            style = VTypography.h3,
                            color = VColors.ink,
                        )
                        Text(
                            text = "Welcome to Enroll+ — your school management portal is ready to set up.", // TODO: i18n
                            style = VTypography.body,
                            color = VColors.ink2,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(16.dp))
                        VButton(
                            text = "Start Onboarding",
                            onClick = onAuthSuccess,
                            icon = Icons.AutoMirrored.Filled.ArrowForward,
                        )
                    }
                }
            }

            // Footer
            if (step != AdminSignupStep.Success) {
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
