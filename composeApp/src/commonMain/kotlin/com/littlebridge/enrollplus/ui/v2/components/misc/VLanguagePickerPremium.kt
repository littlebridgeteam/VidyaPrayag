package com.littlebridge.enrollplus.ui.v2.components.misc

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.i18n.domain.model.SUPPORTED_LANGUAGES
import com.littlebridge.enrollplus.feature.i18n.domain.model.LanguagePreference
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

/**
 * Premium language picker — list of supported languages with native names.
 * Uses M3 Expressive tokens: primary-container highlight, rounded-lg cards, press-scale.
 */
@Composable
fun VLanguagePickerPremium(
    currentLang: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SUPPORTED_LANGUAGES.forEach { lang ->
            VLanguageRowPremium(
                lang = lang,
                isSelected = lang.code == currentLang,
                onClick = { onSelect(lang.code) },
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun VLanguageRowPremium(
    lang: LanguagePreference,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.Lg)
            .background(if (isSelected) VColors.PrimaryContainer else VColors.SurfaceContainerLow)
            .border(
                width = if (isSelected) 1.5.dp else 0.dp,
                color = if (isSelected) VColors.Primary else VColors.OutlineVariant,
                shape = VShapes.Lg,
            )
            .pressScale(interaction, pressedScale = 0.98f)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (isSelected) VColors.Primary.copy(alpha = 0.12f) else VColors.SurfaceContainer),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = lang.code.uppercase(),
                style = VTypography.NavLabel.copy(
                    color = if (isSelected) VColors.OnPrimaryContainer else VColors.OnSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                ),
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = lang.nativeName,
                style = VTypography.FormInput.copy(
                    color = if (isSelected) VColors.OnPrimaryContainer else VColors.OnSurface,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                ),
            )
            Text(
                text = lang.englishName,
                style = VTypography.UpdateTime.copy(color = VColors.OnSurfaceVariant),
            )
        }

        if (isSelected) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = VColors.Primary,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}
