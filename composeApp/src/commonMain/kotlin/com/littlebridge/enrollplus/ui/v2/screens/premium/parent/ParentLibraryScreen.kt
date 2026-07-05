package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

@Composable
fun ParentLibraryScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ParentOverlayScaffold(title = "Library", onBack = onBack, modifier = modifier) {
        Text("Books borrowed by your child.", style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
        Spacer(Modifier.height(20.dp))
        LibraryBookRow("Mathematics Textbook", "Borrowed: Jan 10", "Due: Feb 10")
        Spacer(Modifier.height(8.dp))
        LibraryBookRow("Science Explorer", "Borrowed: Jan 15", "Due: Feb 15")
        Spacer(Modifier.height(8.dp))
        LibraryBookRow("English Literature", "Borrowed: Jan 20", "Due: Feb 20")
    }
}

@Composable
private fun LibraryBookRow(title: String, borrowed: String, due: String) {
    Row(
        Modifier.fillMaxWidth().clip(VShapes.Lg).background(VColors.SurfaceContainerLow).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            Modifier.size(44.dp).clip(VShapes.Lg).background(VColors.TertiaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.MenuBook, contentDescription = null, tint = VColors.OnTertiaryContainer, modifier = Modifier.size(22.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(title, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold))
            Text(borrowed, style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant))
            Text(due, style = VTypography.NavLabel.copy(color = VColors.WarmOrange))
        }
    }
}
