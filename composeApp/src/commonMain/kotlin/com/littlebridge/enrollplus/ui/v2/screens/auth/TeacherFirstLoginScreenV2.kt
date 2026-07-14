package com.littlebridge.enrollplus.ui.v2.screens.auth

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.Alignment
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.feature.auth.domain.repository.AuthRepository
import com.littlebridge.enrollplus.ui.components.VBackHeader
import com.littlebridge.enrollplus.ui.components.VButton
import com.littlebridge.enrollplus.ui.components.VInput
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.util.AnalyticsTracker
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun TeacherFirstLoginScreenV2(
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
    teacherName: String? = "Mr. Vikram",
    authRepository: AuthRepository = koinInject(),
) {
    val scope = rememberCoroutineScope()
    var current by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var submitting by remember { mutableStateOf(false) }

    Column(
        modifier
            .fillMaxSize()
            .background(VColors.cream)
            .statusBarsPadding()
            .imePadding()
            .navigationBarsPadding(),
    ) {
        VBackHeader(onBack = onDone)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp)
                .padding(top = 24.dp, bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(VColors.violetSoft, VShapes.lg),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Filled.Lock,
                        contentDescription = null,
                        tint = VColors.violet,
                        modifier = Modifier.size(28.dp),
                    )
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    text = if (teacherName.isNullOrBlank()) appString(StringKeys.OB_WELCOME) else "${appString(StringKeys.OB_WELCOME)}, $teacherName",
                    style = VTypography.caption,
                    color = VColors.ink3,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = appString(StringKeys.AUTH_SET_NEW_PASSWORD),
                    style = VTypography.h2,
                    color = VColors.ink,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = appString(StringKeys.AUTH_FIRST_LOGIN_DESC),
                    style = VTypography.bodySmall,
                    color = VColors.ink2,
                    textAlign = TextAlign.Center,
                )
            }

            VInput(
                label = appString(StringKeys.AUTH_CURRENT_TEMP_PW),
                value = current,
                onValueChange = { current = it; error = null },
                placeholder = "••••••••",
                keyboardType = KeyboardType.Password,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
            )

            VInput(
                label = appString(StringKeys.AUTH_NEW_PASSWORD),
                value = newPassword,
                onValueChange = { newPassword = it; error = null },
                placeholder = appString(StringKeys.AUTH_PASSWORD_8_PH),
                keyboardType = KeyboardType.Password,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
            )

            VInput(
                label = appString(StringKeys.AUTH_CONFIRM_PASSWORD),
                value = confirm,
                onValueChange = { confirm = it; error = null },
                placeholder = appString(StringKeys.AUTH_REENTER_PH),
                keyboardType = KeyboardType.Password,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
            )

            if (error != null) {
                Text(
                    text = error ?: "",
                    style = VTypography.caption,
                    color = VColors.error,
                )
            }

            val pwTooShortMsg = appString(StringKeys.AUTH_PW_TOO_SHORT)
            val pwNoMatchMsg = appString(StringKeys.AUTH_PW_NO_MATCH)
            val connErrorMsg = appString(StringKeys.AUTH_CONN_ERROR)
            VButton(
                text = appString(StringKeys.AUTH_UPDATE_CONTINUE),
                onClick = {
                    error = validate(
                        current, newPassword, confirm,
                        pwTooShort = pwTooShortMsg,
                        pwNoMatch = pwNoMatchMsg,
                    )
                    if (error == null && !submitting) {
                        submitting = true
                        scope.launch {
                            when (val r = authRepository.changePassword(current.ifBlank { null }, newPassword)) {
                                is NetworkResult.Success -> {
                                    submitting = false
                                    AnalyticsTracker.event("vp_teacher_firstlogin_complete")
                                    AnalyticsTracker.event("vp_auth_change_password", mapOf("role" to "teacher"))
                                    onDone()
                                }
                                is NetworkResult.Error -> { submitting = false; error = r.message }
                                is NetworkResult.ConnectionError -> { submitting = false; error = connErrorMsg }
                            }
                        }
                    }
                },
                loading = submitting,
                icon = Icons.AutoMirrored.Filled.ArrowForward,
            )

            VButton(
                text = appString(StringKeys.AUTH_NEED_HELP),
                onClick = {},
                variant = com.littlebridge.enrollplus.ui.components.VButtonVariant.Ghost,
            )
        }
    }
}

private fun validate(current: String, newPassword: String, confirm: String, pwTooShort: String, pwNoMatch: String): String? = when {
    newPassword.length < 8 -> pwTooShort
    newPassword != confirm -> pwNoMatch
    else -> null
}
