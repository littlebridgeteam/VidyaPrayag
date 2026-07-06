package com.littlebridge.enrollplus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.ui.tokens.VColors

enum class VAvatarSize(val dp: Int, val fontSize: Int) {
    Sm(24, 9),
    Md(32, 11),
    Lg(48, 16),
    Xl(72, 22),
}

@Composable
fun VAvatar(
    name: String,
    modifier: Modifier = Modifier,
    size: VAvatarSize = VAvatarSize.Md,
    imageUrl: String? = null,
) {
    val initials = name.take(2).uppercase()

    if (imageUrl != null) {
        // Coil image loading would go here — for now using initials fallback
        // coil3 async image can be added when AsyncImage import is available
    }

    Box(
        modifier = modifier
            .size(size.dp.dp)
            .background(VColors.violetSoft, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initials,
            fontSize = size.fontSize.sp,
            fontWeight = FontWeight.ExtraBold,
            color = VColors.violet,
        )
    }
}
