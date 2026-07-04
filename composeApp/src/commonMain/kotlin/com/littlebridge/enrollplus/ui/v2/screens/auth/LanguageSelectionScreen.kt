/*
 * File: LanguageSelectionScreen.kt
 * Module: ui.v2.screens.auth
 *
 * First-launch language selection screen.
 * Shown before login when no language preference is set.
 *
 * Spec ref: MULTI_LANGUAGE_SPEC.md §12.2
 */
package com.littlebridge.enrollplus.ui.v2.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.core.locale.LocaleManager
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VLanguagePicker
import com.littlebridge.enrollplus.ui.v2.components.VScreenScaffold
import com.littlebridge.enrollplus.ui.v2.locale.LocalLocale
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.ui.v2.theme.VTheme
import com.littlebridge.enrollplus.ui.v2.theme.colored
import org.koin.compose.koinInject

@Composable
fun LanguageSelectionScreen(
    onLanguageSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val localeManager = koinInject<LocaleManager>()
    val currentLocale by localeManager.currentLocale.collectAsState()
    val colors = VTheme.colors

    VScreenScaffold(
        modifier = modifier,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = appString(StringKeys.LANGUAGE_TITLE),
                style = VTheme.type.h1.colored(colors.ink),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = appString(StringKeys.LANGUAGE_SELECT),
                style = VTheme.type.body.colored(colors.ink3),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(32.dp))

            VLanguagePicker(
                currentLang = currentLocale,
                onSelect = { lang ->
                    localeManager.setLocale(lang)
                },
            )

            Spacer(modifier = Modifier.height(32.dp))

            VButton(
                text = appString(StringKeys.COMMON_BUTTON_CONTINUE),
                onClick = { onLanguageSelected(currentLocale) },
                variant = VButtonVariant.Primary,
                full = true,
            )
        }
    }
}
