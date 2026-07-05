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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.ui.v2.components.buttons.VPrimaryButton
import com.littlebridge.enrollplus.ui.v2.components.form.VSearchField
import com.littlebridge.enrollplus.ui.v2.components.navigation.VFilterChip
import com.littlebridge.enrollplus.ui.v2.tokens.PremiumTheme
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

internal data class SchoolData(
    val id: String,
    val name: String,
    val logo: String,
    val address: String,
    val board: String,
    val rating: Float,
    val students: Int,
    val teachers: Int,
    val acres: Float,
    val fees: String,
    val established: String,
    val desc: String,
    val facilities: List<String>,
    val admissions: String,
)

internal val discoverySchools = listOf(
    SchoolData(
        id = "dps",
        name = "Delhi Public School",
        logo = "DPS",
        address = "Sector 14, Dwarka · New Delhi",
        board = "CBSE",
        rating = 4.8f,
        students = 2400,
        teachers = 120,
        acres = 8.5f,
        fees = "₹1.2L/yr",
        established = "1996",
        desc = "A premier CBSE institution with state-of-the-art facilities, smart classrooms, and a 1:20 teacher-student ratio. Known for consistent board results and national-level sports achievements.",
        facilities = listOf("Smart Classrooms", "Science Labs", "Sports Complex", "Library", "Robotics Lab", "Auditorium"),
        admissions = "Online applications open from Jan 15 – Mar 31. Entrance test for Class 9+. Documents required: Birth Certificate, Previous Report Card, Transfer Certificate.",
    ),
    SchoolData(
        id = "ms",
        name = "Modern School",
        logo = "MS",
        address = "Barakhamba Road · New Delhi",
        board = "CBSE",
        rating = 4.6f,
        students = 1800,
        teachers = 95,
        acres = 6.2f,
        fees = "₹98K/yr",
        established = "1920",
        desc = "One of Delhi's oldest and most prestigious schools. Heritage campus with modern infrastructure. Strong focus on holistic education, arts, and leadership development.",
        facilities = listOf("Heritage Campus", "Olympic Pool", "Tennis Courts", "Digital Library", "Music Academy", "Drama Studio"),
        admissions = "Applications open Feb 1 – Apr 15. Interaction-based admission for primary, written test for senior classes. Priority to siblings and alumni.",
    ),
    SchoolData(
        id = "si",
        name = "Sunrise International",
        logo = "SI",
        address = "Vasant Kunj · New Delhi",
        board = "IB",
        rating = 4.5f,
        students = 1200,
        teachers = 78,
        acres = 12f,
        fees = "₹2.1L/yr",
        established = "2004",
        desc = "International Baccalaureate school with global curriculum standards. Multi-cultural environment with exchange programs. 12-acre green campus with eco-friendly infrastructure.",
        facilities = listOf("IB Curriculum", "Exchange Programs", "Eco Campus", "Innovation Hub", "Language Lab", "Cafeteria"),
        admissions = "Rolling admissions. Assessment includes student portfolio review and parent interaction. International transfer students welcome.",
    ),
    SchoolData(
        id = "ha",
        name = "Heritage Academy",
        logo = "HA",
        address = "Rohini · New Delhi",
        board = "CBSE",
        rating = 4.3f,
        students = 950,
        teachers = 62,
        acres = 4.8f,
        fees = "₹72K/yr",
        established = "2008",
        desc = "Community-focused school with emphasis on values education and experiential learning. Small class sizes ensure personalized attention. Strong parent-teacher partnership model.",
        facilities = listOf("Experiential Labs", "Art Studio", "Community Garden", "Sports Field", "Counseling Center", "Smart Boards"),
        admissions = "Applications open Mar 1 – May 31. First-come basis with interaction session. Need-based scholarships available for meritorious students.",
    ),
)

@Composable
fun ParentDiscoveryScreen(
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
    onOpenSchool: (String) -> Unit = {},
) = PremiumTheme(isDark = false) {
    var selectedBoard by remember { mutableStateOf(0) }
    val boards = listOf("All Boards", "CBSE", "ICSE", "IB", "State Board")
    val schools = discoverySchools

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VColors.Surface)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState()),
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
                        onClick = onExit,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = VColors.OnSurface, modifier = Modifier.size(20.dp))
            }
            Text("Discover Schools", style = VTypography.SectionHeader.copy(color = VColors.OnSurface, fontWeight = FontWeight.Bold))
        }

        // Search field
        Column(Modifier.padding(horizontal = 20.dp)) {
            VSearchField(value = "", onValueChange = { /* TODO: bind search query */ }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(16.dp))

            // Board filters
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                boards.forEachIndexed { index, board ->
                    VFilterChip(
                        label = board,
                        active = selectedBoard == index,
                        onClick = { selectedBoard = index },
                        activeBg = VColors.Primary,
                        activeFg = VColors.OnPrimary,
                        inactiveBg = VColors.SurfaceContainer,
                        inactiveFg = VColors.OnSurfaceVariant,
                    )
                }
            }
        }
        Spacer(Modifier.height(20.dp))

        // School cards
        Column(Modifier.padding(horizontal = 20.dp)) {
            schools.forEach { school ->
                SchoolCardFull(school, onClick = { onOpenSchool(school.id) })
                Spacer(Modifier.height(16.dp))
            }
        }
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun SchoolCardFull(school: SchoolData, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    Column(
        Modifier
            .fillMaxWidth()
            .clip(VShapes.Xl)
            .background(VColors.SurfaceContainerLowest)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
    ) {
        // Header with rating + logo
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
                Modifier.size(40.dp).clip(VShapes.Md).background(VColors.SurfaceContainerLowest),
                contentAlignment = Alignment.Center,
            ) {
                Text(school.logo, style = VTypography.NavLabel.copy(color = VColors.OnSurface, fontWeight = FontWeight.Bold))
            }
        }
        // Body
        Column(Modifier.padding(16.dp)) {
            Text(school.name, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.Bold))
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(Icons.Filled.LocationOn, contentDescription = null, tint = VColors.OnSurfaceVariant, modifier = Modifier.size(14.dp))
                Text(school.address, style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant))
            }
            Spacer(Modifier.height(12.dp))
            // Tags
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SchoolTag(school.board, VColors.PrimaryContainer, VColors.OnPrimaryContainer)
                SchoolTag("Co-ed", VColors.SurfaceContainerHigh, VColors.OnSurfaceVariant)
                SchoolTag("K-12", VColors.SurfaceContainerHigh, VColors.OnSurfaceVariant)
            }
            Spacer(Modifier.height(16.dp))
            // Stats
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                SchoolStat("${school.students}", "Students")
                SchoolStat("${school.teachers}", "Teachers")
                SchoolStat("${school.acres}", "Acres")
            }
            Spacer(Modifier.height(16.dp))
            VPrimaryButton(text = "View Details & Apply", onClick = onClick, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun SchoolTag(text: String, bg: androidx.compose.ui.graphics.Color, fg: androidx.compose.ui.graphics.Color) {
    Box(Modifier.clip(VShapes.Full).background(bg).padding(horizontal = 12.dp, vertical = 4.dp)) {
        Text(text, style = VTypography.NavLabel.copy(color = fg, fontWeight = FontWeight.SemiBold, fontSize = 11.sp))
    }
}

@Composable
private fun SchoolStat(value: String, label: String) {
    Column {
        Text(value, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.Bold, fontSize = 16.sp))
        Text(label, style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant, fontSize = 11.sp))
    }
}
