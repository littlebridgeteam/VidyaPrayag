package com.littlebridge.enrollplus.ui.v2.screens.premium.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.auth.presentation.AuthStep
import com.littlebridge.enrollplus.feature.auth.presentation.AuthViewModel
import com.littlebridge.enrollplus.ui.v2.components.buttons.VPrimaryButton
import com.littlebridge.enrollplus.ui.v2.components.buttons.VSecondaryButton
import com.littlebridge.enrollplus.ui.v2.components.form.VTextInput
import com.littlebridge.enrollplus.ui.v2.components.navigation.VFilterChip
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

/**
 * Premium admin auth — email/password sign-in for school staff.
 *
 * M3 Expressive design using premium components:
 * - VTextInput for form fields with focus glow
 * - VPrimaryButton for CTA with shape-morph on press
 * - VFilterChip for board selection in registration form
 * - AuthScaffoldPremium for the shared chrome
 *
 * Handles both SCHOOL_ADMIN and TEACHER roles (resolved server-side from JWT).
 * Includes school self-registration form for new schools.
 *
 * Reuses the existing AuthViewModel — same data flow, new UI layer.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminAuthScreen(
    onAuthSuccess: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.reset()
        viewModel.onRoleChanged("ADMIN")
    }
    LaunchedEffect(state.isAuthSuccessful) { if (state.isAuthSuccessful) onAuthSuccess() }

    AuthScaffoldPremium(
        title = "Staff Login",
        subtitle = "School administration & teaching staff",
        error = state.error,
        onBack = onBack,
        modifier = modifier,
    ) {
        when (state.step) {
            AuthStep.Identifier -> {
                VTextInput(
                    value = state.identifier,
                    onValueChange = viewModel::onIdentifierChanged,
                    label = "Email or Staff ID",
                    placeholder = "office@svm.edu.in  ·  SVM001.T07",
                    keyboardType = KeyboardType.Email,
                    authStyle = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            AuthStep.LoginPassword -> {
                VTextInput(
                    value = state.password,
                    onValueChange = viewModel::onPasswordChanged,
                    label = "Password",
                    placeholder = "Enter your password",
                    isPassword = true,
                    passwordVisible = passwordVisible,
                    authStyle = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(8.dp))
                val forgotInteraction = remember { MutableInteractionSource() }
                Text(
                    "Forgot password?",
                    style = VTypography.SectionLink.copy(color = VColors.Primary),
                    modifier = Modifier
                        .align(Alignment.End)
                        .pressScale(forgotInteraction, pressedScale = 0.95f)
                        .clickable(interactionSource = forgotInteraction, indication = null) {
                            // TODO: forgot password flow
                        },
                )
            }
            AuthStep.SignupDetails -> {
                if (state.isRegisterSchool) {
                    // School registration form
                    VTextInput(
                        value = state.name,
                        onValueChange = viewModel::onNameChanged,
                        label = "Your Name",
                        placeholder = "Dr. Anita Verma",
                        keyboardType = KeyboardType.Text,
                        authStyle = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(12.dp))
                    VTextInput(
                        value = state.identifier,
                        onValueChange = viewModel::onIdentifierChanged,
                        label = "Work Email",
                        placeholder = "office@svm.edu.in",
                        keyboardType = KeyboardType.Email,
                        authStyle = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(12.dp))
                    VTextInput(
                        value = state.schoolName,
                        onValueChange = viewModel::onSchoolNameChanged,
                        label = "School Name",
                        placeholder = "Saraswati Vidya Mandir",
                        keyboardType = KeyboardType.Text,
                        authStyle = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Board",
                        style = VTypography.FormLabelAuth.copy(color = VColors.OnSurfaceVariant),
                        modifier = Modifier.padding(start = 4.dp, bottom = 6.dp),
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        listOf("CBSE", "ICSE", "UP State", "Other").forEach { b ->
                            VFilterChip(
                                label = b,
                                active = state.board == b,
                                onClick = { viewModel.onBoardChanged(b) },
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    VTextInput(
                        value = state.city,
                        onValueChange = viewModel::onCityChanged,
                        label = "City (Optional)",
                        placeholder = "Lucknow",
                        keyboardType = KeyboardType.Text,
                        authStyle = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(12.dp))
                    VTextInput(
                        value = state.password,
                        onValueChange = viewModel::onPasswordChanged,
                        label = "Create Password",
                        placeholder = "Minimum 8 characters",
                        isPassword = true,
                        passwordVisible = passwordVisible,
                        authStyle = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                } else {
                    // No account notice
                    Text(
                        "No staff account?",
                        style = VTypography.UpdateTitle.copy(color = VColors.OnSurface),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Contact your school administrator for credentials, or register your school with us.",
                        style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            AuthStep.Otp -> {
                VTextInput(
                    value = state.password,
                    onValueChange = viewModel::onPasswordChanged,
                    label = "Password",
                    placeholder = "Enter your password",
                    isPassword = true,
                    authStyle = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        when (state.step) {
            AuthStep.SignupDetails -> {
                if (state.isRegisterSchool) {
                    VPrimaryButton(
                        text = "Register School",
                        onClick = viewModel::registerSchool,
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
                    Spacer(Modifier.height(8.dp))
                    AuthBackLinkPremium(
                        onClick = viewModel::cancelRegisterSchool,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    )
                } else {
                    VPrimaryButton(
                        text = "Onboard Your School",
                        onClick = viewModel::startRegisterSchool,
                        enabled = !state.isLoading,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(8.dp))
                    AuthBackLinkPremium(
                        onClick = viewModel::goBack,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    )
                }
            }
            else -> {
                val ctaLabel = if (state.step == AuthStep.Identifier) "Continue" else "Sign In"
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

        // School registration prompt on Identifier step
        if (state.step is AuthStep.Identifier) {
            Spacer(Modifier.height(20.dp))
            val regInteraction = remember { MutableInteractionSource() }
            Text(
                "Haven't registered? Onboard with us now",
                style = VTypography.SectionLink.copy(color = VColors.Primary),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .pressScale(regInteraction, pressedScale = 0.95f)
                    .clickable(interactionSource = regInteraction, indication = null) {
                        viewModel.startRegisterSchoolDirect()
                    },
            )
        }
    }
}
