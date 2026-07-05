package com.littlebridge.enrollplus.ui.v2.screens.premium.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.feature.auth.domain.repository.AuthRepository
import com.littlebridge.enrollplus.ui.v2.components.buttons.VPrimaryButton
import com.littlebridge.enrollplus.ui.v2.components.buttons.VSecondaryButton
import com.littlebridge.enrollplus.ui.v2.components.form.VTextInput
import com.littlebridge.enrollplus.ui.v2.tokens.PremiumTheme
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

/**
 * Premium teacher first login — one-time "set new password" gate.
 * Reuses AuthRepository.changePassword. On success, onDone advances to portal.
 */
@Composable
fun TeacherFirstLoginScreen(
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
    teacherName: String? = null,
    authRepository: AuthRepository = koinInject(),
) = PremiumTheme(isDark = false) {
    val scope = rememberCoroutineScope()
    var current by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var submitting by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VColors.Surface)
            .statusBarsPadding()
            .imePadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(Modifier.height(48.dp))
        Text(
            if (teacherName.isNullOrBlank()) "Welcome" else "Welcome, $teacherName",
            style = VTypography.Eyebrow.copy(color = VColors.Primary),
        )
        Spacer(Modifier.height(4.dp))
        Text("Set New Password", style = VTypography.GreetingTitle.copy(color = VColors.OnSurface))
        Spacer(Modifier.height(8.dp))
        Text(
            "For security, please set a new password before continuing. Your temporary password will no longer work after this.",
            style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant),
        )

        Spacer(Modifier.height(32.dp))
        VTextInput(
            value = current,
            onValueChange = { current = it; error = null },
            label = "Current Temporary Password",
            placeholder = "Enter temp password",
            isPassword = true,
            authStyle = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(16.dp))
        VTextInput(
            value = newPassword,
            onValueChange = { newPassword = it; error = null },
            label = "New Password",
            placeholder = "Minimum 8 characters",
            isPassword = true,
            authStyle = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(16.dp))
        VTextInput(
            value = confirm,
            onValueChange = { confirm = it; error = null },
            label = "Confirm Password",
            placeholder = "Re-enter new password",
            isPassword = true,
            authStyle = true,
            modifier = Modifier.fillMaxWidth(),
        )

        if (error != null) {
            Spacer(Modifier.height(12.dp))
            Text(error!!, style = VTypography.UpdateText.copy(color = VColors.Error))
        }

        Spacer(Modifier.height(32.dp))
        VPrimaryButton(
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
            trailing = {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = VColors.OnPrimary, modifier = Modifier.size(18.dp))
            },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))
        VSecondaryButton(
            text = "Need Help?",
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(32.dp))
    }
}
