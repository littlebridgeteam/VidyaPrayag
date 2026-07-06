package com.littlebridge.enrollplus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes

enum class VBadgeTone(val bg: Color, val fg: Color) {
    Accent(VColors.violetSoft, VColors.violet),
    Neutral(VColors.surfaceTint, VColors.ink2),
    Success(VColors.mintSoft, VColors.success),
    Warning(VColors.goldSoft, VColors.gold),
    Danger(VColors.coralSoft, VColors.coral),
    Arctic(VColors.skySoft, VColors.sky),
}

enum class VBadgeSize(val textSize: Int) { Sm(9), Md(11) }

@Composable
fun VBadge(
    text: String,
    modifier: Modifier = Modifier,
    tone: VBadgeTone = VBadgeTone.Accent,
    size: VBadgeSize = VBadgeSize.Md,
) {
    Box(
        modifier = modifier
            .background(tone.bg, VShapes.full)
            .padding(horizontal = 8.dp, vertical = 2.dp),
    ) {
        Text(
            text = text,
            fontSize = size.textSize.sp,
            fontWeight = FontWeight.ExtraBold,
            color = tone.fg,
            letterSpacing = 0.3.sp,
        )
    }
}
