package com.littlebridge.enrollplus.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.ui.screens.admin.components.AdminColors
import com.littlebridge.enrollplus.ui.screens.admin.components.AdminTypography
import com.littlebridge.enrollplus.ui.screens.admin.components.AdminShapes
import com.littlebridge.enrollplus.ui.screens.admin.components.AdminTab

// ═══════════════════════════════════════════════════════════════
// AdminSidebar — 260dp dark column
// bg:rgb(19,18,24), padding:24/16
// ═══════════════════════════════════════════════════════════════

@Composable
fun AdminSidebar(
    activeTab: AdminTab,
    onTabSelect: (AdminTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(260.dp)
            .fillMaxHeight()
            .background(AdminColors.sidebarBg)
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        // Brand
        SidebarBrand()

        Spacer(modifier = Modifier.height(32.dp))

        // Nav groups
        SidebarNavGroup("Tabs")
        Spacer(modifier = Modifier.height(4.dp))

        AdminTab.entries.forEach { tab ->
            SidebarNavItem(
                label = tab.label,
                active = tab == activeTab,
                onClick = { onTabSelect(tab) }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        SidebarNavGroup("Overlays")
        Spacer(modifier = Modifier.height(4.dp))

        // Overlay items (static labels from prototype)
        val overlayItems = listOf(
            "Student Profile", "Classes & Subjects", "Transport",
            "ID Cards", "Library", "Leave Requests", "Class Detail"
        )
        overlayItems.forEach { label ->
            SidebarNavItem(
                label = label,
                active = false,
                onClick = {}
            )
        }
    }
}

@Composable
fun SidebarBrand() {
    Row(
        modifier = Modifier.padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "+",
            color = AdminColors.sienna,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = "Admin",
            color = AdminColors.sidebarAdmin,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun SidebarNavGroup(label: String) {
    Text(
        text = label,
        color = AdminColors.sidebarGroup,
        style = AdminTypography.sectionLabel,
        modifier = Modifier.padding(horizontal = 8.dp)
    )
}

@Composable
fun SidebarNavItem(
    label: String,
    active: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .height(39.dp)
            .width(227.dp)
            .background(
                if (active) AdminColors.sienna else Color.Transparent,
                RoundedCornerShape(50)
            )
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Nav dot
        Box(
            modifier = Modifier
                .size(5.dp)
                .clip(RoundedCornerShape(50))
                .background(if (active) Color.White else AdminColors.sidebarText)
        )
        Text(
            text = label,
            color = if (active) Color.White else AdminColors.sidebarText,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
