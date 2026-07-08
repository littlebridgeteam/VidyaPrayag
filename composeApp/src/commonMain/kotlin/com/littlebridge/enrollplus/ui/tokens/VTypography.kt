package com.littlebridge.enrollplus.ui.tokens

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object VTypography {
    // Headlines
    val h1 = TextStyle(
        fontSize = 44.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-1.8).sp,
        lineHeight = 44.sp,
    )
    val h2 = TextStyle(
        fontSize = 24.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-0.5).sp,
        lineHeight = 29.sp,
    )
    val h3 = TextStyle(
        fontSize = 22.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-0.5).sp,
        lineHeight = 26.sp,
    )

    // Body
    val body = TextStyle(
        fontSize = 15.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 23.sp,
    )
    val bodySmall = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 21.sp,
    )

    // Labels
    val label = TextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.1.sp,
        lineHeight = 18.sp,
    )

    // Caption
    val caption = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 16.sp,
    )

    // Accent label (slide labels)
    val accentLabel = TextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.3.sp,
        lineHeight = 18.sp,
    )

    // Wordmark
    val wordmark = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-0.4).sp,
        lineHeight = 20.sp,
    )

    // Splash name
    val splashName = TextStyle(
        fontSize = 42.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-1.8).sp,
        lineHeight = 42.sp,
    )

    // Slide counter
    val slideCounter = TextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.5.sp,
        lineHeight = 16.sp,
    )

    // OTP box
    val otpBox = TextStyle(
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 26.sp,
    )
}
