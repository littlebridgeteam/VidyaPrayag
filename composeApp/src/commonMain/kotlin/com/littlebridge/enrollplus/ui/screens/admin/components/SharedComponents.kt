package com.littlebridge.enrollplus.ui.screens.admin.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ═══════════════════════════════════════════════════════════════
// CardSurface — white card with standard shadow
// rgba(26,22,20,0.04) 0px 1px 2px 0px, rgba(26,22,20,0.06) 0px 1px 3px 0px
// ═══════════════════════════════════════════════════════════════

@Composable
fun CardSurface(
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(0.dp),
    radius: Int = 14,
    bg: Color = AdminColors.cardWhite,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 1.dp,
                shape = RoundedCornerShape(radius.dp),
                ambientColor = Color(0x0A1A1614),
                spotColor = Color(0x0F1A1614)
            )
            .background(bg, RoundedCornerShape(radius.dp))
            .padding(padding)
    ) {
        content()
    }
}

// ═══════════════════════════════════════════════════════════════
// PillButton — generic pill-shaped button
// ═══════════════════════════════════════════════════════════════

@Composable
fun PillButton(
    text: String,
    bg: Color,
    color: Color,
    fontSize: Int,
    fontWeight: FontWeight,
    modifier: Modifier = Modifier,
    radius: Int = 9999,
    padding: PaddingValues = PaddingValues(horizontal = 10.dp, vertical = 5.dp),
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .background(bg, RoundedCornerShape(radius.dp))
            .clickable { onClick() }
            .padding(padding),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = color,
            fontSize = fontSize.sp,
            fontWeight = fontWeight
        )
    }
}

// ═══════════════════════════════════════════════════════════════
// MetaText — 11sp w500, color rgb(138,128,120)
// ═══════════════════════════════════════════════════════════════

@Composable
fun MetaText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = AdminTypography.metaText
) {
    Text(
        text = text,
        color = AdminColors.inkSecondary,
        style = style,
        modifier = modifier
    )
}

// ═══════════════════════════════════════════════════════════════
// TitleText — color rgb(26,22,20)
// ═══════════════════════════════════════════════════════════════

@Composable
fun TitleText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        color = AdminColors.inkPrimary,
        style = style,
        modifier = modifier
    )
}

// ═══════════════════════════════════════════════════════════════
// SectionLabel — 11sp w700, color rgb(138,128,120)
// ═══════════════════════════════════════════════════════════════

@Composable
fun SectionLabel(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        color = AdminColors.inkSecondary,
        style = AdminTypography.sectionLabel,
        modifier = modifier
    )
}

// ═══════════════════════════════════════════════════════════════
// Avatar — circular avatar with initials
// ═══════════════════════════════════════════════════════════════

@Composable
fun Avatar(
    text: String,
    size: Int,
    bg: Color,
    color: Color,
    fontSize: Int,
    fontWeight: FontWeight = FontWeight.ExtraBold,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(size.dp)
            .height(size.dp)
            .background(bg, RoundedCornerShape(50)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = color,
            fontSize = fontSize.sp,
            fontWeight = fontWeight
        )
    }
}

// ═══════════════════════════════════════════════════════════════
// SubtabPill — shared pill container for subtabs
// 277×30, gap:0, padding:3, bg:rgb(248,244,239), radius:14
// ═══════════════════════════════════════════════════════════════

@Composable
fun SubtabPill(
    tabs: List<String>,
    activeIndex: Int,
    onTabSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(AdminColors.pillBg, AdminShapes.pillOuter)
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        tabs.forEachIndexed { index, label ->
            SubtabPillItem(
                label = label,
                active = index == activeIndex,
                onClick = { onTabSelect(index) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SubtabPillItem(
    label: String,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(25.dp)
            .background(
                if (active) AdminColors.cardWhite else Color.Transparent,
                AdminShapes.pillInner
            )
            .then(
                if (active) Modifier.shadow(1.dp, AdminShapes.pillInner, ambientColor = Color(0x0A1A1614), spotColor = Color(0x0F1A1614))
                else Modifier
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (active) AdminColors.inkPrimary else AdminColors.inkSecondary,
            style = if (active) AdminTypography.subtabActive else AdminTypography.subtabInactive
        )
    }
}

// ═══════════════════════════════════════════════════════════════
// IconBox — colored icon container (used by QA buttons, alert icons, etc.)
// ═══════════════════════════════════════════════════════════════

@Composable
fun IconBox(
    size: Int,
    bg: Color,
    radius: Int = 10,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .width(size.dp)
            .height(size.dp)
            .background(bg, RoundedCornerShape(radius.dp)),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
