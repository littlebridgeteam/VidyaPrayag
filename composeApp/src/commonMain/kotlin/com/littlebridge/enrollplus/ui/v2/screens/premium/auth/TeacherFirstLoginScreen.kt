package com.littlebridge.enrollplus.ui.v2.screens.premium.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.feature.auth.domain.repository.AuthRepository
import com.littlebridge.enrollplus.ui.components.VBackHeader
import com.littlebridge.enrollplus.ui.components.VButton
import com.littlebridge.enrollplus.ui.components.VInput
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

/**
 * Teacher first login — one-time "set new password" gate.
 * Uses the cream-based design language matching the landing/login screens.
 * Reuses AuthRepository.changePassword. On success, onDone advances to portal.
 */
@Composable
fun TeacherFirstLoginScreen(
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
    teacherName: String? = null,
    authRepository: AuthRepository = koinInject(),
) {
    val scope = rememberCoroutineScope()
    var current by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var submitting by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VColors.cream)
            .statusBarsPadding(),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            VBackHeader(onBack = onDone)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 32.dp)
                    .padding(top = 24.dp, bottom = 48.dp)
                    .imePadding()
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                // Header
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = if (teacherName.isNullOrBlank()) "Welcome" else "Welcome, $teacherName",
                        style = VTypography.caption,
                        color = VColors.violet,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Set New Password",
                        style = VTypography.h2,
                        color = VColors.ink,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "For security, please set a new password before continuing. Your temporary password will no longer work after this.",
                        style = VTypography.bodySmall,
                        color = VColors.ink2,
                        textAlign = TextAlign.Center,
                    )
                }

                // Form
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    VInput(
                        label = "Current Temporary Password",
                        value = current,
                        onValueChange = { current = it; error = null },
                        placeholder = "Enter temp password",
                        keyboardType = KeyboardType.Password,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    VInput(
                        label = "New Password",
                        value = newPassword,
                        onValueChange = { newPassword = it; error = null },
                        placeholder = "Minimum 8 characters",
                        keyboardType = KeyboardType.Password,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    VInput(
                        label = "Confirm Password",
                        value = confirm,
                        onValueChange = { confirm = it; error = null },
                        placeholder = "Re-enter new password",
                        keyboardType = KeyboardType.Password,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                if (error != null) {
                    Text(
                        text = error!!,
                        style = VTypography.caption,
                        color = VColors.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                VButton(
                    text = "Update & Continue",
                    onClick = {
                        error = when {
                            newPassword.length < 8 -> "Password must be at least 8 characters"
                            newPassword != confirm -> "Passwords do not match"
                            else -> null
                        }
                        if (error == null && !submitting) {
                            submitting = true
                            scope.launch {
                                when (val r = authRepository.changePassword(current.ifBlank { null }, newPassword)) {
                                    is NetworkResult.Success -> { submitting = false; onDone() }
                                    is NetworkResult.Error -> { submitting = false; error = r.message }
                                    is NetworkResult.ConnectionError -> { submitting = false; error = "Connection error. Please try again." }
                                }
                            }
                        }
                    },
                    enabled = !submitting,
                    loading = submitting,
                    icon = Icons.AutoMirrored.Filled.ArrowForward,
                )
            }
        }
    }
}
