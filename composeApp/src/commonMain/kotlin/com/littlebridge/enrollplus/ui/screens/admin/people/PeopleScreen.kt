package com.littlebridge.enrollplus.ui.screens.admin.people

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.domain.util.UiState
import com.littlebridge.enrollplus.presentation.admin.AdminPeopleViewModel
import com.littlebridge.enrollplus.ui.screens.admin.components.AdminColors
import com.littlebridge.enrollplus.ui.screens.admin.components.AdminTypography
import com.littlebridge.enrollplus.ui.screens.admin.components.Avatar
import com.littlebridge.enrollplus.ui.screens.admin.components.AvatarTint
import com.littlebridge.enrollplus.ui.screens.admin.components.CardSurface
import com.littlebridge.enrollplus.ui.screens.admin.components.IconBox
import com.littlebridge.enrollplus.ui.screens.admin.components.PillButton
import com.littlebridge.enrollplus.ui.screens.admin.components.AdminShapes
import com.littlebridge.enrollplus.ui.screens.admin.components.SubtabPill

// ═══════════════════════════════════════════════════════════════
// PeopleScreen
// ═══════════════════════════════════════════════════════════════

@Composable
fun PeopleScreen(
    modifier: Modifier = Modifier
) {
    var activeSubtab by remember { mutableIntStateOf(0) }
    val subtabs = listOf("Teachers", "Students", "Non-teaching", "Alumni")

    Column(modifier = modifier.fillMaxWidth()) {
        // Link card — 277×60, margin:0/24/16
        PeopleLinkCard(
            title = "Child Link Requests",
            sub = "3 parents waiting for approval",
            count = 3,
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
        )

        // Subtab pill
        SubtabPill(
            tabs = subtabs,
            activeIndex = activeSubtab,
            onTabSelect = { activeSubtab = it },
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
        )

        when (activeSubtab) {
            0 -> { // Teachers
                PeopleAddButton(
                    text = "Add Teacher",
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
                )
                val people = listOf(
                    PersonData("Priya Sharma", "EMP-001 · Mathematics · Classes 7-A, 7-B", "PS", AvatarTint.SIENNA),
                    PersonData("Meera Iyer", "EMP-002 · English · Classes 7-A, 6-B", "MI", AvatarTint.SKY),
                    PersonData("Anita Desai", "EMP-003 · Social Studies · Class 8-B", "AD", AvatarTint.GOLD),
                    PersonData("Rajesh Kumar", "EMP-004 · Science · Classes 6-A, 6-B, 7-A", "RK", AvatarTint.MINT),
                    PersonData("Sunita Nair", "EMP-005 · Hindi · Classes 8-A, 8-B · Inactive", "SN", AvatarTint.CORAL)
                )
                people.forEach { person ->
                    PersonCard(
                        name = person.name,
                        meta = person.meta,
                        avatarText = person.avatarText,
                        avatarTint = person.tint,
                        modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 8.dp)
                    )
                }
            }
            1 -> { // Students
                PeopleAddButton(
                    text = "Add Student",
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
                )
                val students = listOf(
                    PersonData("Aarav Sharma", "Class 7-A · Roll 11 · Parent: Priya Sharma", "AS", AvatarTint.SIENNA),
                    PersonData("Sneha Reddy", "Class 8-A · Roll 04 · Parent: Kavya Reddy", "SR", AvatarTint.SKY),
                    PersonData("Rohan Gupta", "Class 8-B · Roll 22 · Parent: Vikram Gupta", "RG", AvatarTint.CORAL),
                    PersonData("Ananya Tiwari", "Class 6-B · Roll 15 · Parent: Anil Tiwari", "AT", AvatarTint.GOLD),
                    PersonData("Karan Singh", "Class 7-A · Roll 19 · Parent: Manjit Singh", "KS", AvatarTint.MINT),
                    PersonData("Priya Desai", "Class 7-A · Roll 08 · Parent: Nilam Desai", "PD", AvatarTint.PLUM)
                )
                students.forEach { person ->
                    PersonCard(
                        name = person.name,
                        meta = person.meta,
                        avatarText = person.avatarText,
                        avatarTint = person.tint,
                        showAssign = false,
                        modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 8.dp)
                    )
                }
            }
            2 -> { // Non-teaching Staff
                PeopleAddButton(
                    text = "Add Staff",
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
                )
                val staff = listOf(
                    PersonData("Gopal Tiwari", "Accountant · Finance · +91 98765 43210", "GT", AvatarTint.SIENNA),
                    PersonData("Lakshmi Menon", "Librarian · Library · +91 98765 12345", "LM", AvatarTint.SKY),
                    PersonData("Harish Verma", "Transport In-charge · Operations · +91 98450 67890", "HV", AvatarTint.GOLD),
                    PersonData("Nisha Joshi", "Front Office · Administration · +91 98000 11111", "NJ", AvatarTint.MINT),
                    PersonData("Ramesh Babu", "Maintenance · Facilities · +91 97000 22222", "RB", AvatarTint.CORAL)
                )
                staff.forEach { person ->
                    PersonCard(
                        name = person.name,
                        meta = person.meta,
                        avatarText = person.avatarText,
                        avatarTint = person.tint,
                        showAssign = false,
                        modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 8.dp)
                    )
                }
            }
            3 -> { // Alumni
                AlumniLinkCard(
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
                )
            }
        }
    }
}

data class PersonData(
    val name: String,
    val meta: String,
    val avatarText: String,
    val tint: AvatarTint
)

// ═══════════════════════════════════════════════════════════════
// PeopleLinkCard — 277×60, gap:12, padding:14/16
// ═══════════════════════════════════════════════════════════════

@Composable
fun PeopleLinkCard(
    title: String,
    sub: String,
    count: Int,
    modifier: Modifier = Modifier
) {
    CardSurface(
        modifier = modifier,
        padding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        radius = 14
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon — 31×31, bg:rgb(254,243,199), radius:10
            IconBox(size = 31, bg = AdminColors.siennaBg, radius = 10) {
                Text(text = "🔗", fontSize = 12.sp)
            }

            // Body
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = AdminColors.inkPrimary,
                    style = AdminTypography.rowTitle
                )
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = sub,
                    color = AdminColors.inkSecondary,
                    style = AdminTypography.metaText
                )
            }

            // Badge — 19×19, bg:rgb(231,111,81), white, 11sp w800
            Box(
                modifier = Modifier
                    .size(19.dp)
                    .background(AdminColors.alertRed, RoundedCornerShape(50)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = count.toString(),
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// PeopleAddButton — 317×31, bg:rgb(254,243,199), 13sp w700, radius:14
// ═══════════════════════════════════════════════════════════════

@Composable
fun PeopleAddButton(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(31.dp)
            .background(AdminColors.siennaBg, AdminShapes.card)
            .clickable { },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = "+", color = AdminColors.sienna, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(
                text = text,
                color = AdminColors.sienna,
                style = AdminTypography.addBtn
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// PersonCard — 277×61, gap:12, padding:14/16
// ═══════════════════════════════════════════════════════════════

@Composable
fun PersonCard(
    name: String,
    meta: String,
    avatarText: String,
    avatarTint: AvatarTint,
    showAssign: Boolean = true,
    modifier: Modifier = Modifier
) {
    CardSurface(
        modifier = modifier,
        padding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        radius = 14
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Avatar — 42×42
            Avatar(
                text = avatarText,
                size = 42,
                bg = avatarTint.bg,
                color = avatarTint.color,
                fontSize = 14
            )

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    color = AdminColors.inkPrimary,
                    style = AdminTypography.personName
                )
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = meta,
                    color = AdminColors.inkSecondary,
                    style = AdminTypography.personMeta
                )
            }

            // Action buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                PillButton(
                    text = "View",
                    bg = AdminColors.pillBg,
                    color = AdminColors.inkTertiary,
                    fontSize = 10,
                    fontWeight = FontWeight.Bold,
                    padding = PaddingValues(horizontal = 10.dp, vertical = 5.dp)
                )
                if (showAssign) {
                    PillButton(
                        text = "Assign",
                        bg = AdminColors.skyBlueBg,
                        color = AdminColors.skyBlue,
                        fontSize = 10,
                        fontWeight = FontWeight.Bold,
                        padding = PaddingValues(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// AlumniLinkCard — link to alumni management
// ═══════════════════════════════════════════════════════════════

@Composable
fun AlumniLinkCard(
    modifier: Modifier = Modifier
) {
    CardSurface(
        modifier = modifier,
        padding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        radius = 14
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconBox(size = 31, bg = AdminColors.purpleBg, radius = 10) {
                Text(text = "🏆", fontSize = 12.sp)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Alumni Management",
                    color = AdminColors.inkPrimary,
                    style = AdminTypography.rowTitle
                )
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = "View alumni directory, donations, mentorship, and analytics",
                    color = AdminColors.inkSecondary,
                    style = AdminTypography.metaText
                )
            }
            Text(
                text = "›",
                color = AdminColors.inkSecondary,
                fontSize = 14.sp
            )
        }
    }
}
