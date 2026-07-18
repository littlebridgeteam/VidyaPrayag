package com.littlebridge.enrollplus.ui.v2.screens.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.feature.admin.presentation.RegistrationOnboardingViewModel
import com.littlebridge.enrollplus.feature.admin.presentation.RegistrationOnboardingViewModel.FlowStep
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VInput
import com.littlebridge.enrollplus.ui.v2.components.VMaterialDatePicker
import com.littlebridge.enrollplus.ui.v2.components.VMaterialTimePicker
import com.littlebridge.enrollplus.ui.v2.components.VSheetPicker
import com.littlebridge.enrollplus.ui.v2.theme.VTheme
import com.littlebridge.enrollplus.ui.v2.theme.colored
import com.littlebridge.enrollplus.ui.v2.theme.shapeCard
import com.littlebridge.enrollplus.ui.v2.theme.shapePill
import com.littlebridge.enrollplus.ui.components.VBackHeader as LegacyBackHeader
import com.littlebridge.enrollplus.ui.components.VButton as LegacyButton
import com.littlebridge.enrollplus.ui.components.VButtonVariant as LegacyButtonVariant
import org.koin.compose.viewmodel.koinViewModel

private data class FieldError(val field: String, val message: String)

private fun validateName(name: String): String? {
    if (name.isBlank()) return "Name is required"
    if (name.trim().length < 2) return "Name must be at least 2 characters"
    if (!name.trim().all { it.isLetter() || it == ' ' || it == '.' || it == '-' }) return "Name must contain only letters"
    return null
}

private fun validateEmail(email: String): String? {
    if (email.isBlank()) return "Email is required"
    val trimmed = email.trim().lowercase()
    if (!trimmed.contains("@")) return "Email must contain @ symbol"
    val parts = trimmed.split("@")
    if (parts.size != 2) return "Enter a valid email"
    val local = parts[0]
    val domain = parts[1]
    if (local.isEmpty()) return "Enter a valid email"
    if (!domain.contains(".") || domain.startsWith(".") || domain.endsWith(".")) return "Enter a valid email address"
    val domainParts = domain.split(".")
    if (domainParts.any { it.isEmpty() }) return "Enter a valid email address"
    if (domainParts.last().length < 2) return "Enter a valid email address"
    return null
}

private fun validatePhone(phone: String): String? {
    if (phone.isBlank()) return "Phone number is required"
    if (phone.length != 10) return "Phone must be exactly 10 digits"
    if (!phone.all { it.isDigit() }) return "Phone must contain only digits"
    if (phone[0] == '0') return "Phone cannot start with 0"
    return null
}

private fun validatePassword(password: String): String? {
    if (password.isBlank()) return "Password is required"
    if (password.length < 8) return "Must be at least 8 characters"
    if (!password.any { it.isUpperCase() }) return "Must contain an uppercase letter"
    if (!password.any { it.isDigit() }) return "Must contain a number"
    if (!password.any { !it.isLetterOrDigit() }) return "Must contain a special character"
    return null
}

private fun validateConfirmPassword(password: String, confirm: String): String? {
    if (confirm.isBlank()) return "Please confirm your password"
    if (password != confirm) return "Passwords do not match"
    return null
}

private fun validateSchoolName(name: String): String? {
    if (name.isBlank()) return "School name is required"
    if (name.trim().length < 3) return "School name must be at least 3 characters"
    return null
}

private fun validatePrincipalName(name: String): String? {
    if (name.isBlank()) return "Principal name is required"
    if (!name.trim().all { it.isLetter() || it == ' ' || it == '.' || it == '-' }) return "Name must contain only letters"
    return null
}

private fun validatePrincipalPhone(phone: String): String? {
    if (phone.isBlank()) return "Principal phone is required"
    if (phone.length != 10) return "Phone must be exactly 10 digits"
    if (!phone.all { it.isDigit() }) return "Phone must contain only digits"
    return null
}

@Composable
private fun VChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val c = VTheme.colors
    val bg = if (selected) c.accent else c.cream
    val fg = if (selected) c.card else c.ink2
    val border = if (selected) c.accent else c.hairline

    Box(
        modifier = Modifier
            .clip(VTheme.dimens.shapePill)
            .background(bg)
            .border(1.dp, border, VTheme.dimens.shapePill)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Text(
            text = text,
            style = VTheme.type.body.copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold).colored(fg),
        )
    }
}

@Composable
private fun VChipGroup(
    options: List<String>,
    selected: String?,
    onSelect: (String) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        options.forEach { opt ->
            VChip(text = opt, selected = selected == opt, onClick = { onSelect(opt) })
        }
    }
}

@Composable
private fun VSectionLabel(text: String) {
    Text(
        text = text,
        style = VTheme.type.caption.copy(fontWeight = FontWeight.SemiBold, letterSpacing = 1.1.sp).colored(VTheme.colors.ink3),
    )
}

@Composable
private fun StepHeader(
    title: String,
    subtitle: String,
    currentStep: Int,
    totalSteps: Int = 4,
) {
    val c = VTheme.colors
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(top = 16.dp, bottom = 24.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(bottom = 16.dp)) {
            repeat(totalSteps) { index ->
                val isCompleted = index < currentStep
                val isCurrent = index == currentStep
                val color = when {
                    isCompleted -> c.accent
                    isCurrent -> c.accent
                    else -> c.hairline
                }
                val width = if (isCurrent) 24.dp else 8.dp
                Box(
                    modifier = Modifier.height(4.dp).width(width).clip(VTheme.dimens.shapePill).background(color),
                )
            }
        }
        Text(
            text = title,
            style = VTheme.type.h2.copy(fontWeight = FontWeight.ExtraBold).colored(c.ink),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(6.dp))
            Text(
            text = subtitle,
            style = VTheme.type.body.colored(c.ink2),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun BackHeader(onBack: () -> Unit) {
    LegacyBackHeader(onBack = onBack)
}

@Composable
private fun SignInFooter(onSignIn: () -> Unit) {
    val c = VTheme.colors
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(top = 8.dp),
    ) {
        Text(
            text = "Already have an account?",
            style = VTheme.type.caption.colored(c.ink3),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
        LegacyButton(
            text = "Sign in",
            onClick = onSignIn,
            variant = LegacyButtonVariant.Outline,
        )
    }
}

@Composable
private fun PasswordStrengthBar(password: String) {
    val c = VTheme.colors
    val strength = remember(password) {
        var score = 0
        if (password.length >= 8) score++
        if (password.any { it.isUpperCase() }) score++
        if (password.any { it.isDigit() }) score++
        if (password.any { !it.isLetterOrDigit() }) score++
        score
    }
    val color = when (strength) {
        0 -> c.hairline
        1 -> c.danger
        2 -> c.warning
        3 -> c.teal
        4 -> c.successInk
        else -> c.hairline
    }
    val label = when (strength) {
        1 -> "Weak"
        2 -> "Fair"
        3 -> "Good"
        4 -> "Strong"
        else -> ""
    }
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
        repeat(4) { index ->
            Box(modifier = Modifier.weight(1f).height(3.dp).clip(VTheme.dimens.shapePill).background(if (index < strength) color else c.hairline))
        }
        if (label.isNotBlank()) {
            Spacer(Modifier.width(8.dp))
            Text(text = label, style = VTheme.type.caption.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold).colored(color))
        }
    }
}

@Composable
private fun PasswordRequirement(text: String, met: Boolean) {
    val c = VTheme.colors
    val color = if (met) c.successInk else c.ink3
    val icon = if (met) "✓" else "○"
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = icon, style = VTheme.type.body.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold).colored(color), modifier = Modifier.size(16.dp), textAlign = TextAlign.Center)
        Text(text = text, style = VTheme.type.caption.copy(fontSize = 13.sp).colored(color))
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

    val stepOrder = listOf(FlowStep.One, FlowStep.Two, FlowStep.Three, FlowStep.Four, FlowStep.Success)
    var previousStepIndex by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize().background(VTheme.colors.background).statusBarsPadding()) {
        AnimatedContent(
            targetState = state.step,
            modifier = Modifier.fillMaxWidth().weight(1f),
            transitionSpec = {
                val oldIdx = stepOrder.indexOf(previousStepIndex.let { stepOrder.getOrElse(it) { state.step } })
                val newIdx = stepOrder.indexOf(targetState)
                val dir = if (newIdx >= oldIdx) 1 else -1
                previousStepIndex = newIdx

                (slideInHorizontally(tween(350)) { it * dir } + fadeIn(tween(250)))
                    .togetherWith(slideOutHorizontally(tween(350)) { -it * dir } + fadeOut(tween(200)))
            },
            label = "step-transition",
        ) { step ->
            when (step) {
                FlowStep.One -> StepOneBasicDetails(viewModel, onNavigateToLogin)
                FlowStep.Two -> StepTwoCreatePassword(viewModel, onNavigateToLogin)
                FlowStep.Three -> StepThreeSchoolIdentity(viewModel)
                FlowStep.Four -> StepFourAcademicYear(viewModel)
                FlowStep.Success -> SuccessScreen(viewModel, onOnboardingComplete)
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// Step 1: Basic Details
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun StepOneBasicDetails(
    viewModel: RegistrationOnboardingViewModel,
    onSignIn: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val c = VTheme.colors
    var validationErrors by remember { mutableStateOf<List<FieldError>>(emptyList()) }
    fun getError(field: String): String? = validationErrors.find { it.field == field }?.message

    fun validateAndSubmit() {
        val errors = mutableListOf<FieldError>()
        validateName(state.adminName)?.let { errors.add(FieldError("name", it)) }
        if (state.adminRole.isBlank()) errors.add(FieldError("role", "Select your role"))
        validateEmail(state.email)?.let { errors.add(FieldError("email", it)) }
        validatePhone(state.contactPhone)?.let { errors.add(FieldError("phone", it)) }
        if (errors.isNotEmpty()) { validationErrors = errors; return }
        viewModel.submitBasicDetails {}
    }

    Column(modifier = Modifier.fillMaxSize()) {
        BackHeader(onBack = onSignIn)
        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 32.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            StepHeader(title = "Basic Details", subtitle = "Tell us about yourself and your school", currentStep = 0)
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                VInput(
                    label = "Your Full Name", value = state.adminName,
                    onValueChange = { viewModel.update { s -> s.copy(adminName = it) }; validationErrors = validationErrors.filter { it.field != "name" } },
                    placeholder = "Dr. Rajesh Sharma",
                    isError = getError("name") != null, errorText = getError("name"),
                )
                VSheetPicker(
                    label = "Your Role", value = state.adminRole,
                    options = listOf("Principal", "Vice Principal", "Administrator", "Director", "Manager"),
                    onSelect = { viewModel.update { s -> s.copy(adminRole = it) }; validationErrors = validationErrors.filter { it.field != "role" } },
                    placeholder = "Select your role", searchable = true,
                )
                if (getError("role") != null) Text(text = getError("role")!!, style = VTheme.type.caption.colored(c.dangerInk))
                VInput(
                    label = "Email Address", value = state.email,
                    onValueChange = { viewModel.update { s -> s.copy(email = it) }; validationErrors = validationErrors.filter { it.field != "email" } },
                    placeholder = "principal@dps.edu.in", keyboardType = KeyboardType.Email,
                    isError = getError("email") != null, errorText = getError("email"),
                )
                VInput(
                    label = "Phone Number", value = state.contactPhone,
                    onValueChange = { if (it.length <= 10 && it.all { c -> c.isDigit() }) { viewModel.update { s -> s.copy(contactPhone = it) }; validationErrors = validationErrors.filter { it.field != "phone" } } },
                    placeholder = "98765 43210", keyboardType = KeyboardType.Phone,
                    isError = getError("phone") != null, errorText = getError("phone"),
                )
                if (state.error != null) Text(text = state.error!!, style = VTheme.type.caption.colored(c.dangerInk))
                LegacyButton(
                    text = "Continue", onClick = { validateAndSubmit() },
                    enabled = state.adminName.isNotBlank() && state.email.isNotBlank() && state.contactPhone.isNotBlank(),
                    loading = state.isLoading,
                    icon = Icons.AutoMirrored.Filled.ArrowForward,
                )
                SignInFooter(onSignIn = onSignIn)
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// Step 2: Create Password
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun StepTwoCreatePassword(
    viewModel: RegistrationOnboardingViewModel,
    onSignIn: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val c = VTheme.colors
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var validationErrors by remember { mutableStateOf<List<FieldError>>(emptyList()) }
    fun getError(field: String): String? = validationErrors.find { it.field == field }?.message

    fun validateAndSubmit() {
        val errors = mutableListOf<FieldError>()
        validatePassword(state.password)?.let { errors.add(FieldError("password", it)) }
        validateConfirmPassword(state.password, state.confirmPassword)?.let { errors.add(FieldError("confirm", it)) }
        if (errors.isNotEmpty()) { validationErrors = errors; return }
        viewModel.createAccount {}
    }

    Column(modifier = Modifier.fillMaxSize()) {
        BackHeader(onBack = { viewModel.goBack() })
        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 32.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            StepHeader(title = "Create Password", subtitle = "Set a strong password for your account", currentStep = 1)
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                VInput(
                    label = "Create Password", value = state.password,
                    onValueChange = { viewModel.update { s -> s.copy(password = it) }; validationErrors = validationErrors.filter { it.field != "password" } },
                    placeholder = "Min. 8 characters", keyboardType = KeyboardType.Password,
                    isPassword = true, passwordVisible = passwordVisible,
                    isError = getError("password") != null, errorText = getError("password"),
                    trailing = { Icon(VIcons.Eye, contentDescription = "Toggle password", tint = c.ink3, modifier = Modifier.size(18.dp).clickable { passwordVisible = !passwordVisible }) },
                )
                if (state.password.isNotBlank()) PasswordStrengthBar(state.password)
                VInput(
                    label = "Confirm Password", value = state.confirmPassword,
                    onValueChange = { viewModel.update { s -> s.copy(confirmPassword = it) }; validationErrors = validationErrors.filter { it.field != "confirm" } },
                    placeholder = "Re-enter password", keyboardType = KeyboardType.Password,
                    isPassword = true, passwordVisible = confirmPasswordVisible,
                    isError = getError("confirm") != null || (state.confirmPassword.isNotBlank() && state.password != state.confirmPassword),
                    errorText = getError("confirm") ?: if (state.confirmPassword.isNotBlank() && state.password != state.confirmPassword) "Passwords do not match" else null,
                    trailing = { Icon(VIcons.Eye, contentDescription = "Toggle password", tint = c.ink3, modifier = Modifier.size(18.dp).clickable { confirmPasswordVisible = !confirmPasswordVisible }) },
                )
                Column(
                    modifier = Modifier.fillMaxWidth().clip(VTheme.dimens.shapeCard).background(c.accentTint).border(1.dp, c.accent.copy(alpha = 0.15f), VTheme.dimens.shapeCard).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(text = "Password requirements", style = VTheme.type.caption.copy(fontWeight = FontWeight.Bold).colored(c.ink))
                    PasswordRequirement("At least 8 characters", state.password.length >= 8)
                    PasswordRequirement("One uppercase letter (A–Z)", state.password.any { it.isUpperCase() })
                    PasswordRequirement("One number (0–9)", state.password.any { it.isDigit() })
                    PasswordRequirement("One special character (!@#\$%)", state.password.any { !it.isLetterOrDigit() })
                }
                if (state.error != null) Text(text = state.error!!, style = VTheme.type.caption.colored(c.dangerInk))
                LegacyButton(
                    text = "Create Account", onClick = { validateAndSubmit() },
                    enabled = state.password.length >= 8 && state.password == state.confirmPassword,
                    loading = state.isLoading,
                    icon = Icons.AutoMirrored.Filled.ArrowForward,
                )
                SignInFooter(onSignIn = onSignIn)
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// Step 3: School Identity
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun StepThreeSchoolIdentity(
    viewModel: RegistrationOnboardingViewModel,
) {
    val state by viewModel.state.collectAsState()
    val c = VTheme.colors
    var validationErrors by remember { mutableStateOf<List<FieldError>>(emptyList()) }
    fun getError(field: String): String? = validationErrors.find { it.field == field }?.message

    fun validateAndSubmit() {
        val errors = mutableListOf<FieldError>()
        validateSchoolName(state.schoolName)?.let { errors.add(FieldError("schoolName", it)) }
        if (state.board.isBlank()) errors.add(FieldError("board", "Select a board"))
        if (state.schoolType.isBlank()) errors.add(FieldError("schoolType", "Select school type"))
        if (state.principalName.isNotBlank()) validatePrincipalName(state.principalName)?.let { errors.add(FieldError("principalName", it)) }
        if (state.principalPhone.isNotBlank()) validatePrincipalPhone(state.principalPhone)?.let { errors.add(FieldError("principalPhone", it)) }
        if (errors.isNotEmpty()) { validationErrors = errors; return }
        viewModel.submitSchoolIdentity {}
    }

    Column(modifier = Modifier.fillMaxSize()) {
        BackHeader(onBack = { viewModel.goBack() })
        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 32.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            StepHeader(title = "School Identity", subtitle = "Tell us about your school", currentStep = 2)
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                VInput(
                    label = "Full legal name", value = state.schoolName,
                    onValueChange = { viewModel.update { s -> s.copy(schoolName = it) }; validationErrors = validationErrors.filter { it.field != "schoolName" } },
                    placeholder = "Delhi Public School",
                    isError = getError("schoolName") != null, errorText = getError("schoolName"),
                )
                VInput(label = "Short name", value = state.shortName, onValueChange = { viewModel.update { s -> s.copy(shortName = it) } }, placeholder = "DPS")
                VInput(label = "Affiliation number", value = state.affiliationNumber, onValueChange = { viewModel.update { s -> s.copy(affiliationNumber = it) } }, placeholder = "1234567")
                VSectionLabel("BOARD")
                VChipGroup(
                    options = listOf("CBSE", "ICSE", "UP State", "Other"), selected = state.board,
                    onSelect = { viewModel.update { s -> s.copy(board = it) }; validationErrors = validationErrors.filter { it.field != "board" } },
                )
                if (getError("board") != null) Text(text = getError("board")!!, style = VTheme.type.caption.colored(c.dangerInk))
                VSectionLabel("SCHOOL TYPE")
                VChipGroup(options = listOf("Government", "Private Aided", "Private Unaided", "Central"), selected = state.schoolType, onSelect = { viewModel.update { s -> s.copy(schoolType = it) }; validationErrors = validationErrors.filter { it.field != "schoolType" } })
                if (getError("schoolType") != null) Text(text = getError("schoolType")!!, style = VTheme.type.caption.colored(c.dangerInk))
                VInput(
                    label = "Principal's name", value = state.principalName,
                    onValueChange = { viewModel.update { s -> s.copy(principalName = it) }; validationErrors = validationErrors.filter { it.field != "principalName" } },
                    placeholder = "Dr. Rajesh Sharma",
                    isError = getError("principalName") != null, errorText = getError("principalName"),
                )
                VInput(
                    label = "Principal's mobile", value = state.principalPhone,
                    onValueChange = { if (it.length <= 10 && it.all { c -> c.isDigit() }) { viewModel.update { s -> s.copy(principalPhone = it) }; validationErrors = validationErrors.filter { it.field != "principalPhone" } } },
                    placeholder = "98765 43210", keyboardType = KeyboardType.Phone,
                    isError = getError("principalPhone") != null, errorText = getError("principalPhone"),
                )
                VSheetPicker(
                    label = "City", value = state.city,
                    options = listOf("New Delhi", "Mumbai", "Bangalore", "Chennai", "Kolkata", "Hyderabad", "Pune", "Ahmedabad", "Jaipur", "Lucknow", "Kanpur", "Varanasi", "Meerut", "Noida", "Ghaziabad", "Gurugram"),
                    onSelect = { viewModel.update { s -> s.copy(city = it) } }, placeholder = "Select city", searchable = true,
                )
                if (state.error != null) Text(text = state.error!!, style = VTheme.type.caption.colored(c.dangerInk))
            }
            LegacyButton(
                text = "Continue", onClick = { validateAndSubmit() },
                enabled = state.schoolName.isNotBlank() && state.board.isNotBlank(),
                loading = state.isLoading,
                icon = Icons.AutoMirrored.Filled.ArrowForward,
            )
            LegacyButton(
                text = "Back", onClick = { viewModel.goBack() },
                variant = LegacyButtonVariant.Outline,
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// Step 4: Academic Year
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun StepFourAcademicYear(
    viewModel: RegistrationOnboardingViewModel,
) {
    val state by viewModel.state.collectAsState()
    val c = VTheme.colors
    var validationErrors by remember { mutableStateOf<List<FieldError>>(emptyList()) }
    fun getError(field: String): String? = validationErrors.find { it.field == field }?.message

    fun validateAndSubmit() {
        val errors = mutableListOf<FieldError>()
        if (state.academicYearLabel.isBlank()) errors.add(FieldError("year", "Select an academic year"))
        if (state.yearStartDate.isBlank()) errors.add(FieldError("startDate", "Select start date"))
        if (state.yearEndDate.isBlank()) errors.add(FieldError("endDate", "Select end date"))
        if (state.yearStartDate.isNotBlank() && state.yearEndDate.isNotBlank() && state.yearStartDate >= state.yearEndDate) {
            errors.add(FieldError("endDate", "End date must be after start date"))
        }
        if (state.workingDays.isBlank()) errors.add(FieldError("workingDays", "Select working days"))
        if (state.periodsPerDay.isBlank()) errors.add(FieldError("periods", "Select periods per day"))
        val startH = state.schoolStartTime.substringBefore(":").toIntOrNull() ?: 0
        val startM = state.schoolStartTime.substringAfter(":").take(2).ifBlank { "0" }.toIntOrNull() ?: 0
        val endH = state.schoolEndTime.substringBefore(":").toIntOrNull() ?: 0
        val endM = state.schoolEndTime.substringAfter(":").take(2).ifBlank { "0" }.toIntOrNull() ?: 0
        if (startH > endH || (startH == endH && startM >= endM)) {
            errors.add(FieldError("endTime", "End time must be after start time"))
        }
        if (errors.isNotEmpty()) { validationErrors = errors; return }
        viewModel.submitAcademicYear {}
    }

    Column(modifier = Modifier.fillMaxSize()) {
        BackHeader(onBack = { viewModel.goBack() })
        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 32.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            StepHeader(title = "Academic Year", subtitle = "Set up your academic calendar", currentStep = 3)
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                VSectionLabel("CURRENT ACADEMIC YEAR")
                VChipGroup(
                    options = listOf("2025-26", "2026-27"), selected = state.academicYearLabel,
                    onSelect = { viewModel.update { s -> s.copy(academicYearLabel = it) }; validationErrors = validationErrors.filter { it.field != "year" } },
                )
                if (getError("year") != null) Text(text = getError("year")!!, style = VTheme.type.caption.colored(c.dangerInk))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        VMaterialDatePicker(value = state.yearStartDate, onValueChange = { viewModel.update { s -> s.copy(yearStartDate = it) }; validationErrors = validationErrors.filter { it.field != "startDate" } }, label = "Year starts", placeholder = "Select date")
                        if (getError("startDate") != null) Text(text = getError("startDate")!!, style = VTheme.type.caption.colored(c.dangerInk), modifier = Modifier.padding(top = 4.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        VMaterialDatePicker(value = state.yearEndDate, onValueChange = { viewModel.update { s -> s.copy(yearEndDate = it) }; validationErrors = validationErrors.filter { it.field != "endDate" } }, label = "Year ends", placeholder = "Select date")
                        if (getError("endDate") != null) Text(text = getError("endDate")!!, style = VTheme.type.caption.colored(c.dangerInk), modifier = Modifier.padding(top = 4.dp))
                    }
                }
                VSectionLabel("WORKING DAYS")
                VChipGroup(options = listOf("Mon-Fri", "Mon-Sat"), selected = state.workingDays, onSelect = { viewModel.update { s -> s.copy(workingDays = it) }; validationErrors = validationErrors.filter { it.field != "workingDays" } })
                if (getError("workingDays") != null) Text(text = getError("workingDays")!!, style = VTheme.type.caption.colored(c.dangerInk))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        val startHour = state.schoolStartTime.substringBefore(":").toIntOrNull() ?: 8
                        val startMinute = state.schoolStartTime.substringAfter(":").take(2).ifBlank { "00" }
                        VMaterialTimePicker(
                            hour = startHour, minute = startMinute,
                            onHourChange = { h -> viewModel.update { s -> val m = s.schoolStartTime.substringAfter(":").take(2).ifBlank { "00" }; s.copy(schoolStartTime = "${h.toString().padStart(2, '0')}:$m") } },
                            onMinuteChange = { m -> viewModel.update { s -> val h = s.schoolStartTime.substringBefore(":").padStart(2, '0').ifBlank { "08" }; s.copy(schoolStartTime = "$h:$m") } },
                            label = "Start time",
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        val endHour = state.schoolEndTime.substringBefore(":").toIntOrNull() ?: 14
                        val endMinute = state.schoolEndTime.substringAfter(":").take(2).ifBlank { "00" }
                        VMaterialTimePicker(
                            hour = endHour, minute = endMinute,
                            onHourChange = { h -> viewModel.update { s -> val m = s.schoolEndTime.substringAfter(":").take(2).ifBlank { "00" }; s.copy(schoolEndTime = "${h.toString().padStart(2, '0')}:$m") } },
                            onMinuteChange = { m -> viewModel.update { s -> val h = s.schoolEndTime.substringBefore(":").padStart(2, '0').ifBlank { "14" }; s.copy(schoolEndTime = "$h:$m") } },
                            label = "End time",
                        )
                        if (getError("endTime") != null) Text(text = getError("endTime")!!, style = VTheme.type.caption.colored(c.dangerInk), modifier = Modifier.padding(top = 4.dp))
                    }
                }
                VSheetPicker(
                    label = "Periods per day", value = state.periodsPerDay,
                    options = listOf("4", "5", "6", "7", "8", "9", "10", "11", "12"),
                    onSelect = { viewModel.update { s -> s.copy(periodsPerDay = it) }; validationErrors = validationErrors.filter { it.field != "periods" } }, placeholder = "Select periods",
                )
                if (getError("periods") != null) Text(text = getError("periods")!!, style = VTheme.type.caption.colored(c.dangerInk))
                if (state.error != null) Text(text = state.error!!, style = VTheme.type.caption.colored(c.dangerInk))
            }
            Spacer(Modifier.height(8.dp))
            LegacyButton(
                text = "Register School", onClick = { validateAndSubmit() },
                enabled = state.academicYearLabel.isNotBlank(),
                loading = state.isLoading,
                icon = Icons.AutoMirrored.Filled.ArrowForward,
            )
            LegacyButton(
                text = "Back", onClick = { viewModel.goBack() },
                variant = LegacyButtonVariant.Outline,
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// Success screen
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun SuccessScreen(
    viewModel: RegistrationOnboardingViewModel,
    onComplete: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val c = VTheme.colors

    val ringScale = remember { Animatable(0f) }
    val iconScale = remember { Animatable(0f) }
    val titleAlpha = remember { Animatable(0f) }
    val subtitleAlpha = remember { Animatable(0f) }
    val buttonAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        ringScale.animateTo(1f, tween(500, easing = FastOutSlowInEasing))
        iconScale.animateTo(1f, tween(350, easing = FastOutSlowInEasing))
        titleAlpha.animateTo(1f, tween(400))
        subtitleAlpha.animateTo(1f, tween(400))
        buttonAlpha.animateTo(1f, tween(400))
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .scale(ringScale.value)
                        .background(c.successInk.copy(alpha = 0.08f), CircleShape),
                )
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .scale(iconScale.value)
                        .background(c.successInk, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(VIcons.CheckCircle, contentDescription = null, tint = c.card, modifier = Modifier.size(36.dp))
                }
            }

            Text(
                text = "School Registered!",
                style = VTheme.type.h2.copy(fontSize = 24.sp, fontWeight = FontWeight.ExtraBold).colored(c.ink),
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(titleAlpha.value),
            )
            Text(
                text = "Welcome to Enroll+!\nYour school management portal is ready.",
                style = VTheme.type.body.copy(fontSize = 15.sp, lineHeight = 24.sp).colored(c.ink3),
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(subtitleAlpha.value),
            )
            Spacer(Modifier.height(8.dp))
            LegacyButton(
                text = "Continue to Home",
                onClick = { viewModel.completeOnboarding(onComplete) },
                loading = state.isLoading,
                icon = Icons.AutoMirrored.Filled.ArrowForward,
                modifier = Modifier.alpha(buttonAlpha.value).fillMaxWidth(),
            )
            if (state.error != null) Text(text = state.error!!, style = VTheme.type.caption.colored(c.dangerInk))
        }
    }
}
