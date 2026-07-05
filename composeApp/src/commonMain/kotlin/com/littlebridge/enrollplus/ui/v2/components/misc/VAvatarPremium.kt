package com.littlebridge.enrollplus.ui.v2.components.misc

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

@Composable
fun VAvatarPremium(
    name: String,
    modifier: Modifier = Modifier,
    size: Int = 48,
    photoUrl: String? = null,
    glassBorder: Boolean = false,
) {
    val initials = name.take(2).uppercase()
    val sz = size.dp
    Box(
        modifier = modifier
            .size(sz)
            .clip(VShapes.Xl)
            .background(Brush.linearGradient(listOf(VColors.Primary, VColors.PrimaryFixedDim))),
        contentAlignment = Alignment.Center,
    ) {
        if (photoUrl != null) {
            val painter = rememberAsyncImagePainter(photoUrl)
            val state by painter.state.collectAsState()
            if (state is AsyncImagePainter.State.Success) {
                Image(painter = painter, contentDescription = name, contentScale = ContentScale.Crop, modifier = Modifier.size(sz))
            } else {
                Text(text = initials, style = VTypography.HeroStatValue.copy(color = VColors.OnPrimary))
            }
        } else {
            Text(text = initials, style = VTypography.HeroStatValue.copy(color = VColors.OnPrimary))
        }
    }
}
