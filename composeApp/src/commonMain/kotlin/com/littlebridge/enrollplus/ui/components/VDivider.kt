package com.littlebridge.enrollplus.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.tokens.VColors

@Composable
fun VDivider(
    modifier: Modifier = Modifier,
    thickness: Int = 1,
) {
    HorizontalDivider(
        modifier = modifier.fillMaxWidth(),
        thickness = thickness.dp,
        color = VColors.line,
    )
}

@Composable
fun VDividerWithText(
    text: String,
    modifier: Modifier = Modifier,
) {
    androidx.compose.foundation.layout.Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth().padding(vertical = 4.dp),
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = VColors.lineSoft,
        )
        androidx.compose.material3.Text(
            text = text,
            style = com.littlebridge.enrollplus.ui.tokens.VTypography.caption,
            color = com.littlebridge.enrollplus.ui.tokens.VColors.ink3,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = VColors.lineSoft,
        )
    }
}
