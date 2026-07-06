package com.littlebridge.enrollplus.ui.v2.screens.premium.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.core.locale.LocaleManager
import com.littlebridge.enrollplus.ui.v2.components.buttons.VPrimaryButton
import com.littlebridge.enrollplus.ui.v2.components.misc.VLanguagePickerPremium
import com.littlebridge.enrollplus.ui.v2.tokens.PremiumTheme
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.koinInject

/**
 * Premium language selection — first-launch language picker.
 *
 * M3 Expressive design with VLanguagePickerPremium and VPrimaryButton.
 */
@Composable
fun LanguageSelectionScreen(
    onLanguageSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) = PremiumTheme(isDark = false) {
    val localeManager = koinInject<LocaleManager>()
    val currentLocale by localeManager.currentLocale.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VColors.Surface)
            .statusBarsPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Choose Your Language",
            style = VTypography.GreetingTitle.copy(color = VColors.OnSurface),
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Select your preferred language to continue",
            style = VTypography.LandingSub.copy(color = VColors.OnSurfaceVariant),
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(32.dp))
        VLanguagePickerPremium(
            currentLang = currentLocale,
            onSelect = { lang -> localeManager.setLocale(lang) },
        )
        Spacer(Modifier.height(32.dp))
        VPrimaryButton(
            text = "Continue",
            onClick = { onLanguageSelected(currentLocale) },
            modifier = Modifier.padding(horizontal = 24.dp),
        )
    }
}
