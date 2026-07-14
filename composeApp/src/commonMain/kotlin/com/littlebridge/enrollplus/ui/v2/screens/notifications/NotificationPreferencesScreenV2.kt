package com.littlebridge.enrollplus.ui.v2.screens.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.notification.domain.model.NotificationPreferenceDto
import com.littlebridge.enrollplus.feature.notification.presentation.NotificationPreferencesState
import com.littlebridge.enrollplus.feature.notification.presentation.NotificationPreferencesViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VPullRefresh
import com.littlebridge.enrollplus.ui.v2.screens.SkeletonList
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NotificationPreferencesScreenV2(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: NotificationPreferencesViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()

    Column(
        modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .navigationBarsPadding(),
    ) {
        VBackHeader(title = "Notification Preferences", onBack = onBack)

        VPullRefresh(
            isRefreshing = state.isLoading && state.preferences.isNotEmpty(),
            onRefresh = { viewModel.load() },
        ) {
            NotificationPreferencesContent(
                state = state,
                onToggle = { pref, enabled ->
                    viewModel.updatePreference(
                        category = pref.category,
                        enabled = enabled,
                        pushEnabled = pref.pushEnabled,
                        inAppEnabled = pref.inAppEnabled,
                        emailEnabled = pref.emailEnabled,
                        smsEnabled = pref.smsEnabled,
                        sound = pref.sound,
                    )
                },
                onRetry = { viewModel.load() },
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun NotificationPreferencesContent(
    state: NotificationPreferencesState,
    onToggle: (NotificationPreferenceDto, Boolean) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        VStateHost(
            loading = state.isLoading,
            error = state.error,
            isEmpty = state.preferences.isEmpty(),
            emptyTitle = "No Preferences Found",
            emptyBody = "Notification preferences will appear here once configured.",
            onRetry = onRetry,
            skeleton = { SkeletonList(rows = 4) },
        ) {
            state.preferences.forEach { pref ->
                PreferenceRow(
                    preference = pref,
                    onToggle = { enabled -> onToggle(pref, enabled) },
                )
            }

            if (state.saveSuccess) {
                Text(
                    "Preference saved",
                    style = VTypography.caption,
                    color = VColors.success,
                )
            }
        }
    }
}

@Composable
private fun PreferenceRow(
    preference: NotificationPreferenceDto,
    onToggle: (Boolean) -> Unit,
) {
    VCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = preference.category.replace("_", " ").replaceFirstChar { it.uppercase() },
                    style = VTypography.body.copy(fontWeight = FontWeight.SemiBold, color = VColors.ink),
                )
                Spacer(Modifier.height(4.dp))
                val channels = buildList {
                    if (preference.pushEnabled == true) add("Push")
                    if (preference.inAppEnabled == true) add("In-app")
                    if (preference.emailEnabled == true) add("Email")
                    if (preference.smsEnabled == true) add("SMS")
                }
                if (channels.isNotEmpty()) {
                    Text(
                        text = channels.joinToString(" · "),
                        style = VTypography.caption.copy(color = VColors.ink3),
                    )
                }
            }
            Switch(
                checked = preference.enabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = VColors.surface,
                    checkedTrackColor = VColors.violet,
                    uncheckedThumbColor = VColors.ink3,
                    uncheckedTrackColor = VColors.ink.copy(alpha = 0.1f),
                ),
            )
        }
    }
}
