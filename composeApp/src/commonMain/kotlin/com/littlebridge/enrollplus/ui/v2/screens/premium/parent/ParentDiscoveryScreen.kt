package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.components.buttons.VPrimaryButton
import com.littlebridge.enrollplus.ui.v2.components.form.VSearchField
import com.littlebridge.enrollplus.ui.v2.tokens.PremiumTheme
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

@Composable
fun ParentDiscoveryScreen(
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
    onOpenSchool: (String) -> Unit = {},
) = PremiumTheme(isDark = false) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VColors.Surface)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
    ) {
        Spacer(Modifier.height(20.dp))
        Text("Discover Schools", style = VTypography.GreetingTitle.copy(color = VColors.OnSurface))
        Spacer(Modifier.height(8.dp))
        Text("Find the right school for your child.", style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
        Spacer(Modifier.height(20.dp))
        VSearchField(value = "", onValueChange = {}, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(20.dp))
        repeat(3) { i ->
            DiscoveryCard(
                name = listOf("Saraswati Vidya Mandir", "Delhi Public School", "St. Xavier's")[i],
                board = listOf("CBSE", "ICSE", "CBSE")[i],
                city = listOf("Lucknow", "Delhi", "Mumbai")[i],
                rating = listOf(4.8, 4.6, 4.9)[i].toString(),
            )
            Spacer(Modifier.height(12.dp))
        }
        Spacer(Modifier.height(20.dp))
        VPrimaryButton(text = "Exit", onClick = onExit, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun DiscoveryCard(name: String, board: String, city: String, rating: String) {
    Row(
        Modifier.fillMaxWidth().clip(VShapes.Lg).background(VColors.SurfaceContainerLow).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(Modifier.size(48.dp).clip(VShapes.Lg).background(VColors.PrimaryContainer), contentAlignment = Alignment.Center) {
            Text(name.first().toString(), style = VTypography.SectionHeader.copy(color = VColors.OnPrimaryContainer))
        }
        Column(Modifier.weight(1f)) {
            Text(name, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold))
            Text("$board · $city", style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant))
        }
        Text("★ $rating", style = VTypography.NavLabel.copy(color = VColors.WarmOrange, fontWeight = FontWeight.SemiBold))
    }
}
