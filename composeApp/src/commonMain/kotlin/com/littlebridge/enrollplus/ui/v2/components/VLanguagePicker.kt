/*
 * File: VLanguagePicker.kt
 * Module: ui.v2.components
 *
 * Language selection component — shows a grid/list of supported languages
 * with native names. Highlights the currently selected language.
 *
 * Spec ref: MULTI_LANGUAGE_SPEC.md §12.1
 */
package com.littlebridge.enrollplus.ui.v2.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.i18n.domain.model.SUPPORTED_LANGUAGES
import com.littlebridge.enrollplus.feature.i18n.domain.model.LanguagePreference
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.theme.VTheme
import com.littlebridge.enrollplus.ui.v2.theme.colored

@Composable
fun VLanguagePicker(
    currentLang: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = VTheme.colors

    Column(modifier = modifier.fillMaxWidth()) {
        SUPPORTED_LANGUAGES.forEach { lang ->
            VLanguageRow(
                lang = lang,
                isSelected = lang.code == currentLang,
                accentColor = colors.accentDeep,
                onClick = { onSelect(lang.code) },
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun VLanguageRow(
    lang: LanguagePreference,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
) {
    val colors = VTheme.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) accentColor.copy(alpha = 0.08f) else colors.card)
            .border(
                width = if (isSelected) 1.5.dp else 0.5.dp,
                color = if (isSelected) accentColor else colors.hairline,
                shape = RoundedCornerShape(12.dp),
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Language code badge
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (isSelected) accentColor.copy(alpha = 0.12f) else colors.background),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = lang.code.uppercase(),
                style = VTheme.type.caption.colored(if (isSelected) accentColor else colors.ink3),
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = lang.nativeName,
                style = VTheme.type.body.colored(if (isSelected) accentColor else colors.ink),
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            )
            Text(
                text = lang.englishName,
                style = VTheme.type.caption.colored(colors.ink3),
            )
        }

        if (isSelected) {
            Icon(
                imageVector = VIcons.Check,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}
