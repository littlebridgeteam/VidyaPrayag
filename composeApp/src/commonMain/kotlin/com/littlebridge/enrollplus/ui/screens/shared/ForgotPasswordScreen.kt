package com.littlebridge.enrollplus.ui.screens.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.components.VBackHeader
import com.littlebridge.enrollplus.ui.components.VButton
import com.littlebridge.enrollplus.ui.components.VInput
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography
import kotlinx.coroutines.launch

@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var email by remember { mutableStateOf("") }
    var sent by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VColors.cream)
            .statusBarsPadding(),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
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
                if (!sent) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "Forgot Password",
                            style = VTypography.h2,
                            color = VColors.ink,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "Enter your email address and we'll send you a link to reset your password",
                            style = VTypography.bodySmall,
                            color = VColors.ink2,
                            textAlign = TextAlign.Center,
                        )
                    }

                    VInput(
                        label = "Email Address",
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "you@school.edu.in",
                        keyboardType = KeyboardType.Email,
                    )

                    VButton(
                        text = "Send Reset Link",
                        onClick = {
                            sent = true
                            scope.launch { snackbarHostState.showSnackbar("Reset link sent to $email") }
                        },
                        enabled = email.isNotBlank() && email.contains("@"),
                        icon = Icons.AutoMirrored.Filled.ArrowForward,
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Spacer(Modifier.height(48.dp))
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .background(VColors.successSoft, androidx.compose.foundation.shape.CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "✓",
                                style = VTypography.h2.copy(fontWeight = FontWeight.Bold),
                                color = VColors.success,
                            )
                        }
                        Text(
                            text = "Check Your Email",
                            style = VTypography.h3,
                            color = VColors.ink,
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            text = "We've sent a password reset link to\n$email\n\nThe link will expire in 30 minutes.",
                            style = VTypography.bodySmall,
                            color = VColors.ink2,
                            textAlign = TextAlign.Center,
                        )
                        VButton(
                            text = "Back to Login",
                            onClick = onBack,
                            variant = com.littlebridge.enrollplus.ui.components.VButtonVariant.Outline,
                        )
                        Text(
                            text = "Didn't receive it? Check your spam folder or try again",
                            style = VTypography.caption,
                            color = VColors.ink3,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    sent = false
                                    email = ""
                                }
                                .padding(top = 8.dp),
                        )
                    }
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
