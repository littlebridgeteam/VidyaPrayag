package com.littlebridge.enrollplus.ui.v2.components.typography

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

/**
 * Greeting title — 34sp, weight 800, with gradient text span for the accent part.
 *
 * HTML: .greeting-title
 *   font-size: 34px; font-weight: 800; line-height: 1.05; letter-spacing: -0.035em;
 *   .greeting-title em {
 *     font-weight: 900; letter-spacing: -0.045em;
 *     background: linear-gradient(135deg, var(--primary), var(--tertiary));
 *     -webkit-background-clip: text; -webkit-text-fill-color: transparent;
 *   }
 *
 * @param plainText  The non-accent portion (e.g. "One platform.")
 * @param accentText The gradient-clipped portion (e.g. "Every stakeholder.")
 */
@Composable
fun VGreetingTitle(
    plainText: String,
    accentText: String? = null,
    modifier: Modifier = Modifier,
) {
    val annotated = buildAnnotatedString {
        append(plainText)
        if (accentText != null) {
            append("\n")
            withStyle(
                SpanStyle(
                    fontWeight = FontWeight.Black,
                    letterSpacing = VTypography.GreetingTitleAccent.letterSpacing,
                    brush = androidx.compose.ui.graphics.Brush.linearGradient(
                        colors = listOf(VColors.Primary, VColors.Tertiary),
                    ),
                ),
            ) {
                append(accentText)
            }
        }
    }
    Text(
        text = annotated,
        style = VTypography.GreetingTitle.copy(color = VColors.OnSurface),
        modifier = modifier,
    )
}
