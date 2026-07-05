package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.ui.v2.components.buttons.VPrimaryButton
import com.littlebridge.enrollplus.ui.v2.components.buttons.VSecondaryButton
import com.littlebridge.enrollplus.ui.v2.components.typography.VSectionHeader
import com.littlebridge.enrollplus.ui.v2.tokens.PremiumTheme
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

@Composable
fun ParentSchoolDetailScreen(
    schoolId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) = PremiumTheme(isDark = false) {
    val school = remember(schoolId) { discoverySchools.firstOrNull { it.id == schoolId } }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VColors.Surface)
            .statusBarsPadding(),
    ) {
        // Header with back button
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                Modifier.size(40.dp).clip(VShapes.Full).background(VColors.SurfaceContainerLow)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onBack,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = VColors.OnSurface, modifier = Modifier.size(20.dp))
            }
            Text(
                school?.name ?: "School Details",
                style = VTypography.SectionHeader.copy(color = VColors.OnSurface, fontWeight = FontWeight.Bold),
            )
        }

        if (school == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("School not found", style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
            }
            return@PremiumTheme
        }

        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        ) {
            // Header image area with rating + logo
            Row(
                Modifier.fillMaxWidth().background(VColors.PrimaryContainer).padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Filled.Star, contentDescription = null, tint = VColors.WarmOrange, modifier = Modifier.size(16.dp))
                    Text(school.rating.toString(), style = VTypography.NavLabel.copy(color = VColors.OnPrimaryContainer, fontWeight = FontWeight.Bold))
                }
                Box(
                    Modifier.size(48.dp).clip(VShapes.Md).background(VColors.SurfaceContainerLowest),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(school.logo, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.Bold))
                }
            }

            // Body
            Column(Modifier.padding(20.dp)) {
                Text(school.name, style = VTypography.SectionHeader.copy(color = VColors.OnSurface, fontWeight = FontWeight.Black, fontSize = 20.sp))
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Filled.LocationOn, contentDescription = null, tint = VColors.OnSurfaceVariant, modifier = Modifier.size(14.dp))
                    Text(school.address, style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant, fontWeight = FontWeight.Medium))
                }
                Spacer(Modifier.height(16.dp))

                // Tags
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SchoolDetailTag(school.board, VColors.PrimaryContainer, VColors.OnPrimaryContainer)
                    SchoolDetailTag("Co-ed", VColors.SurfaceContainerHigh, VColors.OnSurfaceVariant)
                    SchoolDetailTag("K-12", VColors.SurfaceContainerHigh, VColors.OnSurfaceVariant)
                }
                Spacer(Modifier.height(20.dp))

                // Description
                Text(school.desc, style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant, lineHeight = 22.sp))
                Spacer(Modifier.height(24.dp))

                // Stats grid (4 stats including fees)
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    SchoolDetailStat("${school.students}", "Students")
                    SchoolDetailStat("${school.teachers}", "Teachers")
                    SchoolDetailStat("${school.acres}", "Acres")
                    SchoolDetailStat(school.fees, "Fees/yr")
                }
                Spacer(Modifier.height(24.dp))

                // Facilities
                VSectionHeader("Facilities")
                Spacer(Modifier.height(12.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    school.facilities.forEach { facility ->
                        SchoolDetailTag(facility, VColors.SurfaceContainerLow, VColors.OnSurfaceVariant)
                    }
                }
                Spacer(Modifier.height(24.dp))

                // Admissions
                VSectionHeader("Admissions")
                Spacer(Modifier.height(12.dp))
                Text(school.admissions, style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant, lineHeight = 22.sp))
                Spacer(Modifier.height(16.dp))

                // Established
                Column {
                    Text("ESTABLISHED", style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant, fontWeight = FontWeight.SemiBold, fontSize = 11.sp))
                    Text(school.established, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.Bold, fontSize = 16.sp))
                }
                Spacer(Modifier.height(24.dp))

                // Action buttons
                VPrimaryButton(text = "Request Admission Info", onClick = { /* TODO: request admission info */ }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
                VSecondaryButton(text = "Save to Favorites", onClick = { /* TODO: save school to favorites */ }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun SchoolDetailTag(text: String, bg: androidx.compose.ui.graphics.Color, fg: androidx.compose.ui.graphics.Color) {
    Box(Modifier.clip(VShapes.Full).background(bg).padding(horizontal = 14.dp, vertical = 8.dp)) {
        Text(text, style = VTypography.NavLabel.copy(color = fg, fontWeight = FontWeight.SemiBold, fontSize = 12.sp))
    }
}

@Composable
private fun SchoolDetailStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.Bold, fontSize = 16.sp))
        Text(label, style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant, fontSize = 11.sp))
    }
}
