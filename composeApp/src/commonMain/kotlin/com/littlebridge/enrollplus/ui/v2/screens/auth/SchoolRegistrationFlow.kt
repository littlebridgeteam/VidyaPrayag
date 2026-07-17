package com.littlebridge.enrollplus.ui.v2.screens.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.feature.admin.presentation.RegistrationOnboardingViewModel
import com.littlebridge.enrollplus.feature.admin.presentation.RegistrationOnboardingViewModel.FlowStep
import com.littlebridge.enrollplus.ui.components.VButton
import com.littlebridge.enrollplus.ui.components.VButtonVariant
import com.littlebridge.enrollplus.ui.components.VInput
import com.littlebridge.enrollplus.ui.components.VProgressBarSegments
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VMotion
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.components.VDatePicker
import com.littlebridge.enrollplus.ui.v2.components.VDropdown
import com.littlebridge.enrollplus.ui.v2.components.VTimePicker
import org.koin.compose.viewmodel.koinViewModel

// ── Chip selector (matches Figma pill chips: active=#5B41D5, inactive=#F8F6EF) ──

@Composable
private fun VChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val bg = if (selected) VColors.violet else VColors.surfaceTint
    val fg = if (selected) VColors.white else VColors.ink2
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(
            text = text,
            style = VTypography.body.copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
            color = fg,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun VChipGroup(
    options: List<String>,
    selected: String?,
    onSelect: (String) -> Unit,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        options.forEach { opt ->
            VChip(
                text = opt,
                selected = selected == opt,
                onClick = { onSelect(opt) },
            )
        }
    }
}

// ── Section label (matches Figma "BOARD" / "SCHOOL TYPE" labels) ──

@Composable
private fun VSectionLabel(text: String) {
    Text(
        text = text,
        style = VTypography.caption.copy(
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.1.sp,
        ),
        color = VColors.ink2,
    )
}

// ── Onboarding header (ONBOARDING label + progress segments + title row) ──

@Composable
private fun OnboardingHeader(
    title: String,
    stepLabel: String,
    currentStep: Int, // 0-indexed for VProgressBarSegments
    totalSteps: Int = 4,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "ONBOARDING",
            style = VTypography.h2.copy(fontSize = 20.sp, fontWeight = FontWeight.Medium),
            color = VColors.ink2,
        )
        VProgressBarSegments(total = totalSteps, current = currentStep)
        Spacer(Modifier.height(0.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                style = VTypography.h2.copy(fontSize = 24.sp, fontWeight = FontWeight.ExtraBold),
                color = VColors.ink,
            )
            Text(
                text = stepLabel,
                style = VTypography.caption.copy(fontSize = 12.sp, fontWeight = FontWeight.Medium),
                color = VColors.ink2,
            )
        }
    }
}

// ── Footer (Already have an account? + Sign in) ──

@Composable
private fun SignInFooter(onSignIn: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(top = 8.dp),
    ) {
        Text(
            text = "Already have an account?",
            style = VTypography.caption,
            color = VColors.ink3,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
        VButton(
            text = "Sign in",
            onClick = onSignIn,
            variant = VButtonVariant.Outline,
        )
    }
}

// ── Bottom bar (divider + button row, used by Steps 3-4) ──

@Composable
private fun VBottomBar(
    showBack: Boolean = false,
    backText: String = "Back",
    onBack: () -> Unit = {},
    continueText: String = "Continue",
    onContinue: () -> Unit,
    continueEnabled: Boolean = true,
    continueLoading: Boolean = false,
) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(VColors.lineSoft),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (showBack) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 52.dp)
                        .border(1.5.dp, VColors.violet, VShapes.md)
                        .clickable { onBack() }
                        .padding(vertical = 15.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = backText,
                        style = VTypography.body.copy(fontWeight = FontWeight.SemiBold),
                        color = VColors.violet,
                    )
                }
            }
            VButton(
                text = continueText,
                onClick = onContinue,
                enabled = continueEnabled,
                loading = continueLoading,
                icon = Icons.AutoMirrored.Filled.ArrowForward,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// Main screen
// ════════════════════════════════════════════════════════════════════════════

@Composable
fun SchoolRegistrationFlow(
    onNavigateToLogin: () -> Unit,
    onOnboardingComplete: () -> Unit,
    viewModel: RegistrationOnboardingViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VColors.cream)
            .statusBarsPadding(),
    ) {
        AnimatedContent(
            targetState = state.step,
            transitionSpec = {
                if (initialState.ordinal < targetState.ordinal) {
                    (slideInHorizontally(VMotion.tweenSlow()) { it / 3 } + fadeIn(VMotion.tweenSlow())) togetherWith
                        (slideOutHorizontally(VMotion.tweenSlow()) { -it / 3 } + fadeOut(VMotion.tweenFast()))
                } else {
                    (slideInHorizontally(VMotion.tweenSlow()) { -it / 3 } + fadeIn(VMotion.tweenSlow())) togetherWith
                        (slideOutHorizontally(VMotion.tweenSlow()) { it / 3 } + fadeOut(VMotion.tweenFast()))
                }
            },
            label = "reg-step",
        ) { step ->
            when (step) {
                FlowStep.One -> StepOneBasicDetails(
                    viewModel = viewModel,
                    onSignIn = onNavigateToLogin,
                )
                FlowStep.Two -> StepTwoCreatePassword(
                    viewModel = viewModel,
                    onSignIn = onNavigateToLogin,
                )
                FlowStep.Three -> StepThreeSchoolIdentity(
                    viewModel = viewModel,
                )
                FlowStep.Four -> StepFourAcademicYear(
                    viewModel = viewModel,
                )
                FlowStep.Success -> SuccessScreen(
                    viewModel = viewModel,
                    onComplete = onOnboardingComplete,
                )
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// Step 1: Basic Details (pre-auth) — admin name, role, email, phone
// Layout: fixed back header + scroll content (header + form + footer)
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun ColumnScope.StepOneBasicDetails(
    viewModel: RegistrationOnboardingViewModel,
    onSignIn: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    // Back header
    SimpleBackHeader(onBack = onSignIn)

    Column(
        modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = 32.dp)
            .padding(top = 24.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        OnboardingHeader(
            title = "Basic Details",
            stepLabel = "Step 1 of 4",
            currentStep = 0,
        )

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            VInput(
                label = "Your Full Name",
                value = state.adminName,
                onValueChange = { viewModel.update { s -> s.copy(adminName = it) } },
                placeholder = "Dr. Rajesh Sharma",
            )
            VDropdown(
                label = "Your Role",
                value = state.adminRole,
                options = listOf("Principal", "Vice Principal", "Administrator", "Director", "Manager"),
                onSelect = { viewModel.update { s -> s.copy(adminRole = it) } },
                placeholder = "Select your role",
            )
            VInput(
                label = "Email Address",
                value = state.email,
                onValueChange = { viewModel.update { s -> s.copy(email = it) } },
                placeholder = "principal@dps.edu.in",
                keyboardType = KeyboardType.Email,
            )
            VInput(
                label = "Phone Number",
                value = state.contactPhone,
                onValueChange = { if (it.length <= 10 && it.all { c -> c.isDigit() }) viewModel.update { s -> s.copy(contactPhone = it) } },
                placeholder = "98765 43210",
                keyboardType = KeyboardType.Phone,
                prefix = "+91",
            )
            if (state.error != null) {
                Text(
                    text = state.error!!,
                    style = VTypography.caption,
                    color = VColors.error,
                )
            }
        }

        SignInFooter(onSignIn = onSignIn)
    }

    VBottomBar(
        continueText = "Continue",
        onContinue = { viewModel.submitBasicDetails {} },
        continueEnabled = state.adminName.isNotBlank() && state.email.isNotBlank() && state.contactPhone.isNotBlank(),
        continueLoading = state.isLoading,
    )
}

// ════════════════════════════════════════════════════════════════════════════
// Step 2: Create Password (pre-auth) — password, confirm, requirements
// Layout: fixed back header + scroll content (header + form + footer)
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun ColumnScope.StepTwoCreatePassword(
    viewModel: RegistrationOnboardingViewModel,
    onSignIn: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    SimpleBackHeader(onBack = { viewModel.goBack() })

    Column(
        modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = 32.dp)
            .padding(top = 24.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        OnboardingHeader(
            title = "Create Password",
            stepLabel = "Step 2 of 4",
            currentStep = 1,
        )

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            VInput(
                label = "Create Password",
                value = state.password,
                onValueChange = { viewModel.update { s -> s.copy(password = it) } },
                placeholder = "Min. 8 characters",
                keyboardType = KeyboardType.Password,
                visualTransformation = PasswordVisualTransformation(),
            )
            VInput(
                label = "Confirm Password",
                value = state.confirmPassword,
                onValueChange = { viewModel.update { s -> s.copy(confirmPassword = it) } },
                placeholder = "Re-enter password",
                keyboardType = KeyboardType.Password,
                visualTransformation = PasswordVisualTransformation(),
                isError = state.confirmPassword.isNotBlank() && state.password != state.confirmPassword,
                errorMessage = if (state.confirmPassword.isNotBlank() && state.password != state.confirmPassword) "Passwords do not match" else null,
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(VColors.surfaceWarm, VShapes.md)
                    .padding(16.dp),
            ) {
                Column {
                    Text(
                        text = "Password requirements",
                        style = VTypography.caption.copy(fontWeight = FontWeight.Bold),
                        color = VColors.ink,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "• At least 8 characters\n• One uppercase letter (A–Z)\n• One number (0–9)\n• One special character (!@#$%)",
                        style = VTypography.caption,
                        color = VColors.ink3,
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
        }

        SignInFooter(onSignIn = onSignIn)
    }

    VBottomBar(
        continueText = "Create Account",
        onContinue = { viewModel.createAccount {} },
        continueEnabled = state.password.length >= 8 && state.password == state.confirmPassword,
        continueLoading = state.isLoading,
    )
}

// ════════════════════════════════════════════════════════════════════════════
// Step 3: School Identity (post-auth) — school name, short name, affiliation,
// board chips, type chips, principal, city
// Layout: fixed back header + fixed header section + scroll content + fixed bottom bar
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun ColumnScope.StepThreeSchoolIdentity(
    viewModel: RegistrationOnboardingViewModel,
) {
    val state by viewModel.state.collectAsState()

    // Fixed back header
    SimpleBackHeader(onBack = { viewModel.goBack() })

    // Fixed header section
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
    ) {
        OnboardingHeader(
            title = "School identity",
            stepLabel = "Step 3 of 4",
            currentStep = 2,
        )
    }

    // Scrollable content
    Column(
        modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        VInput(
            label = "Full legal name",
            value = state.schoolName,
            onValueChange = { viewModel.update { s -> s.copy(schoolName = it) } },
            placeholder = "Delhi Public School",
        )
        VInput(
            label = "Short name",
            value = state.shortName,
            onValueChange = { viewModel.update { s -> s.copy(shortName = it) } },
            placeholder = "DPS",
        )
        VInput(
            label = "Affiliation number",
            value = state.affiliationNumber,
            onValueChange = { viewModel.update { s -> s.copy(affiliationNumber = it) } },
            placeholder = "1234567",
        )
        VSectionLabel("BOARD")
        VChipGroup(
            options = listOf("CBSE", "ICSE", "UP State", "Other"),
            selected = state.board,
            onSelect = { viewModel.update { s -> s.copy(board = it) } },
        )
        VSectionLabel("SCHOOL TYPE")
        VChipGroup(
            options = listOf("Government", "Private Aided", "Private Unaided", "Central"),
            selected = state.schoolType,
            onSelect = { viewModel.update { s -> s.copy(schoolType = it) } },
        )
        VInput(
            label = "Principal's name",
            value = state.principalName,
            onValueChange = { viewModel.update { s -> s.copy(principalName = it) } },
            placeholder = "Dr. Rajesh Sharma",
        )
        VInput(
            label = "Principal's mobile",
            value = state.principalPhone,
            onValueChange = { if (it.length <= 10 && it.all { c -> c.isDigit() }) viewModel.update { s -> s.copy(principalPhone = it) } },
            placeholder = "98765 43210",
            keyboardType = KeyboardType.Phone,
            prefix = "+91",
        )
        VDropdown(
            label = "City",
            value = state.city,
            options = listOf("New Delhi", "Mumbai", "Bangalore", "Chennai", "Kolkata", "Hyderabad", "Pune", "Ahmedabad", "Jaipur", "Lucknow", "Kanpur", "Varanasi", "Meerut", "Noida", "Ghaziabad", "Gurugram"),
            onSelect = { viewModel.update { s -> s.copy(city = it) } },
            placeholder = "Select city",
        )
        if (state.error != null) {
            Text(
                text = state.error!!,
                style = VTypography.caption,
                color = VColors.error,
            )
        }
        Spacer(Modifier.height(8.dp))
    }

    // Fixed bottom bar with Back + Continue
    VBottomBar(
        showBack = true,
        onBack = { viewModel.goBack() },
        continueText = "Continue",
        onContinue = { viewModel.submitSchoolIdentity {} },
        continueEnabled = state.schoolName.isNotBlank() && state.board.isNotBlank(),
        continueLoading = state.isLoading,
    )
}

// ════════════════════════════════════════════════════════════════════════════
// Step 4: Academic Year (post-auth) — year chips, dates, working days, times,
// periods
// Layout: fixed back header + fixed header section + scroll content + fixed bottom bar
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun ColumnScope.StepFourAcademicYear(
    viewModel: RegistrationOnboardingViewModel,
) {
    val state by viewModel.state.collectAsState()

    // Fixed back header
    SimpleBackHeader(onBack = { viewModel.goBack() })

    // Fixed header section
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
    ) {
        OnboardingHeader(
            title = "Academic year",
            stepLabel = "Step 4 of 4",
            currentStep = 3,
        )
    }

    // Scrollable content
    Column(
        modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        VSectionLabel("CURRENT ACADEMIC YEAR")
        VChipGroup(
            options = listOf("2025-26", "2026-27"),
            selected = state.academicYearLabel,
            onSelect = { viewModel.update { s -> s.copy(academicYearLabel = it) } },
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            VDatePicker(
                value = state.yearStartDate,
                onValueChange = { newStart ->
                    viewModel.update { s ->
                        // Auto-compute year end = start + 1 year (same month/day)
                        val endIso = run {
                            val parts = newStart.split("-")
                            if (parts.size == 3) {
                                val y = (parts[0].toIntOrNull() ?: 2025) + 1
                                val m = parts[1]
                                val d = parts[2]
                                "$y-$m-$d"
                            } else {
                                s.yearEndDate
                            }
                        }
                        s.copy(yearStartDate = newStart, yearEndDate = endIso)
                    }
                },
                label = "Year starts",
                placeholder = "Select date",
                modifier = Modifier.weight(1f),
            )
            VDatePicker(
                value = state.yearEndDate,
                onValueChange = { viewModel.update { s -> s.copy(yearEndDate = it) } },
                label = "Year ends",
                placeholder = "Select date",
                modifier = Modifier.weight(1f),
            )
        }
        VSectionLabel("WORKING DAYS")
        VChipGroup(
            options = listOf("Mon-Fri", "Mon-Sat"),
            selected = state.workingDays,
            onSelect = { viewModel.update { s -> s.copy(workingDays = it) } },
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val startHour = state.schoolStartTime.substringBefore(":").toIntOrNull() ?: 8
            val startMinute = state.schoolStartTime.substringAfter(":").take(2).ifBlank { "00" }
            VTimePicker(
                hour = startHour,
                minute = startMinute,
                onHourChange = { h ->
                    viewModel.update { s ->
                        val m = s.schoolStartTime.substringAfter(":").take(2).ifBlank { "00" }
                        s.copy(schoolStartTime = "${h.toString().padStart(2, '0')}:$m")
                    }
                },
                onMinuteChange = { m ->
                    viewModel.update { s ->
                        val h = s.schoolStartTime.substringBefore(":").padStart(2, '0').ifBlank { "08" }
                        s.copy(schoolStartTime = "$h:$m")
                    }
                },
                label = "Start time",
                modifier = Modifier.weight(1f),
            )
            val endHour = state.schoolEndTime.substringBefore(":").toIntOrNull() ?: 14
            val endMinute = state.schoolEndTime.substringAfter(":").take(2).ifBlank { "00" }
            VTimePicker(
                hour = endHour,
                minute = endMinute,
                onHourChange = { h ->
                    viewModel.update { s ->
                        val m = s.schoolEndTime.substringAfter(":").take(2).ifBlank { "00" }
                        s.copy(schoolEndTime = "${h.toString().padStart(2, '0')}:$m")
                    }
                },
                onMinuteChange = { m ->
                    viewModel.update { s ->
                        val h = s.schoolEndTime.substringBefore(":").padStart(2, '0').ifBlank { "14" }
                        s.copy(schoolEndTime = "$h:$m")
                    }
                },
                label = "End time",
                modifier = Modifier.weight(1f),
            )
        }
        VDropdown(
            label = "Periods per day",
            value = state.periodsPerDay,
            options = listOf("4", "5", "6", "7", "8", "9", "10", "11", "12"),
            onSelect = { viewModel.update { s -> s.copy(periodsPerDay = it) } },
            placeholder = "Select periods",
        )
        if (state.error != null) {
            Text(
                text = state.error!!,
                style = VTypography.caption,
                color = VColors.error,
            )
        }
        Spacer(Modifier.height(8.dp))
    }

    // Fixed bottom bar with Back + Continue
    VBottomBar(
        showBack = true,
        onBack = { viewModel.goBack() },
        continueText = "Continue",
        onContinue = { viewModel.submitAcademicYear {} },
        continueEnabled = state.academicYearLabel.isNotBlank(),
        continueLoading = state.isLoading,
    )
}

// ════════════════════════════════════════════════════════════════════════════
// Success screen — centered check circle + title + description + button
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun ColumnScope.SuccessScreen(
    viewModel: RegistrationOnboardingViewModel,
    onComplete: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    // Back header (no back arrow, just spacer for status bar)
    Box(modifier = Modifier.height(48.dp))

    Column(
        modifier = Modifier
            .weight(1f)
            .imePadding()
            .navigationBarsPadding()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        val popScale = remember { Animatable(0f) }
        LaunchedEffect(Unit) {
            popScale.animateTo(1f, tween(VMotion.durDefault, easing = VMotion.ease))
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .scale(popScale.value)
                    .background(VColors.successSoft, RoundedCornerShape(36.dp)),
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
                text = "School Registered",
                style = VTypography.h3.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                color = VColors.ink,
            )
            Text(
                text = "Welcome to Enroll+, your school management portal is ready to set up.",
                style = VTypography.body.copy(fontSize = 14.sp),
                color = VColors.ink3,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(16.dp))
            VButton(
                text = "Start Onboarding",
                onClick = {
                    viewModel.completeOnboarding(onComplete)
                },
                loading = state.isLoading,
                icon = Icons.AutoMirrored.Filled.ArrowForward,
            )
            if (state.error != null) {
                Text(
                    text = state.error!!,
                    style = VTypography.caption,
                    color = VColors.error,
                )
            }
        }
    }
}

// ── Simple back header (48px, transparent, back arrow) ──

@Composable
private fun SimpleBackHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(VColors.surfaceTint, VShapes.md)
                .clickable { onBack() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = VColors.ink,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}
