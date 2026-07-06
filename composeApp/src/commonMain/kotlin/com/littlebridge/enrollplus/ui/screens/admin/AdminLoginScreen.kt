package com.littlebridge.enrollplus.ui.screens.admin

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.auth.presentation.AuthViewModel
import com.littlebridge.enrollplus.getPlatform
import com.littlebridge.enrollplus.ui.components.VBackHeader
import com.littlebridge.enrollplus.ui.components.VButton
import com.littlebridge.enrollplus.ui.components.VDividerWithText
import com.littlebridge.enrollplus.ui.components.VInput
import com.littlebridge.enrollplus.ui.components.VSSOButton
import com.littlebridge.enrollplus.ui.components.AppleIcon
import com.littlebridge.enrollplus.ui.components.GoogleIcon
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography
import kotlinx.coroutines.launch

@Composable
fun AdminLoginScreen(
    viewModel: AuthViewModel,
    onBack: () -> Unit,
    onNavigateToSignup: () -> Unit,
    onAuthSuccess: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
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
            .background(VColors.cream)
            .statusBarsPadding(),
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
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Staff Sign In", // TODO: i18n
                    style = VTypography.h2,
                    color = VColors.ink,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Access your school management console", // TODO: i18n
                    style = VTypography.bodySmall,
                    color = VColors.ink2,
                    textAlign = TextAlign.Center,
                )
            }

            // SSO — Google on Android, Apple on iOS
            val isAndroid = getPlatform().name.startsWith("Android")
            if (isAndroid) {
                VSSOButton(
                    text = "Google",
                    icon = GoogleIcon,
                    iconTint = null,
                    onClick = { scope.launch { snackbarHostState.showSnackbar("Coming Soon") } },
                )
            } else {
                VSSOButton(
                    text = "Apple",
                    icon = AppleIcon,
                    onClick = { scope.launch { snackbarHostState.showSnackbar("Coming Soon") } },
                )
            }

            VDividerWithText("or sign in with credentials")

            // Form
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                VInput(
                    label = "Email Address", // TODO: i18n
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "principal@school.edu.in",
                    keyboardType = KeyboardType.Email,
                )
                VInput(
                    label = "Password", // TODO: i18n
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Enter your password",
                    keyboardType = KeyboardType.Password,
                    visualTransformation = PasswordVisualTransformation(),
                )
            }

            // Forgot password
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                Text(
                    text = "Forgot password?", // TODO: i18n
                    style = VTypography.caption.copy(fontWeight = FontWeight.Bold),
                    color = VColors.violet,
                    modifier = Modifier.clickable { /* TODO: Forgot password flow */ },
                )
            }

            if (state.error != null) {
                Text(
                    text = state.error!!,
                    style = VTypography.caption,
                    color = VColors.error,
                )
            }

            VButton(
                text = "Sign In",
                onClick = { viewModel.loginStaff(email, password) },
                enabled = email.isNotBlank() && password.isNotBlank(),
                loading = state.isLoading,
                icon = Icons.AutoMirrored.Filled.ArrowForward,
            )

            // Footer
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 8.dp),
            ) {
                Text(
                    text = "Don't have an account?", // TODO: i18n
                    style = VTypography.caption,
                    color = VColors.ink3,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
                VButton(
                    text = "Register your school",
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
