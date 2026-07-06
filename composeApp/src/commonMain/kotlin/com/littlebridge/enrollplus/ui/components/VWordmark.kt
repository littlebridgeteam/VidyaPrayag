package com.littlebridge.enrollplus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography

@Composable
fun VWordmark(
    modifier: Modifier = Modifier,
    size: Int = 40,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .size(size.dp)
                .clip(VShapes.lg)
                .background(VColors.violet),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "e+",
                style = VTypography.h3.copy(
                    fontSize = androidx.compose.ui.unit.TextUnit(size * 0.5f, androidx.compose.ui.unit.TextUnitType.Sp),
                    fontWeight = FontWeight.ExtraBold,
                ),
                color = VColors.white,
            )
        }
        Spacer(Modifier.padding(start = 10.dp))
        Text(
            text = "EnrollPlus",
            style = VTypography.wordmark.copy(
                fontSize = androidx.compose.ui.unit.TextUnit(size * 0.42f, androidx.compose.ui.unit.TextUnitType.Sp),
            ),
            color = VColors.ink,
        )
    }
}
