package com.littlebridge.enrollplus.ui.screens.admin.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape

// ═══════════════════════════════════════════════════════════════
// Admin Colors — EXACT from JSON computed styles
// ═══════════════════════════════════════════════════════════════

object AdminColors {
    // ── Surfaces ──
    val surfaceBase = Color(0xFFFBF8F4)     // rgb(251,248,244) — screen area
    val cardWhite = Color(0xFFFFFFFF)       // rgb(255,255,255) — all cards
    val pillBg = Color(0xFFF8F4EF)          // rgb(248,244,239) — subtab/filter containers
    val trackBg = Color(0xFFF5F0E8)         // rgb(245,240,232) — progress bar tracks

    // ── Text ──
    val inkPrimary = Color(0xFF1A1614)      // rgb(26,22,20)
    val inkSecondary = Color(0xFF8A8078)    // rgb(138,128,120)
    val inkTertiary = Color(0xFF5C544E)     // rgb(92,84,78)

    // ── Accent: Sienna (brand/active) ──
    val sienna = Color(0xFFB45309)          // rgb(180,83,9)
    val siennaBg = Color(0xFFFEF3C7)        // rgb(254,243,199)

    // ── Alert: Red ──
    val alertRed = Color(0xFFE76F51)        // rgb(231,111,81)
    val alertRedBg = Color(0xFFFCE8E2)      // rgb(252,232,226)

    // ── Good: Green ──
    val goodGreen = Color(0xFF2D7A4A)       // rgb(45,122,74)
    val goodGreenBg = Color(0xFFD4EDDB)     // rgb(212,237,219)

    // ── Amber ──
    val amber = Color(0xFFD4A017)           // rgb(212,160,23)

    // ── Sky Blue ──
    val skyBlue = Color(0xFF3B82A0)         // rgb(59,130,160)
    val skyBlueBg = Color(0xFFDBEEF5)       // rgb(219,238,245)

    // ── Purple ──
    val purpleBg = Color(0xFFF0E4ED)        // rgb(240,228,237)
    val purple = Color(0xFF8B5CF6)          // purple accent

    // ── Gold ──
    val goldBg = Color(0xFFFBF0D6)          // rgb(251,240,214)

    // ── Sidebar (dark) ──
    val sidebarBg = Color(0xFF131218)       // rgb(19,18,24)
    val sidebarText = Color(0xFF8B8895)     // rgb(139,136,149)
    val sidebarGroup = Color(0xFF56545F)    // rgb(86,84,95)
    val sidebarAdmin = Color(0xFF5A5764)    // rgb(90,87,100)

    // ── Overlay ──
    val headerLine = Color(0xFFF0EAE0)      // rgb(240,234,224) — header bottom border

    // ── Inbox dots ──
    val inboxDotUrgent = Color(0xFFE76F51)   // rgb(231,111,81)
    val inboxDotNew = Color(0xFFB45309)      // rgb(180,83,9)
    val inboxDotPending = Color(0xFFD4A017)  // rgb(212,160,23)

    // ── Inbox tags ──
    val inboxTagUrgentBg = Color(0xFFFCE8E2) // rgb(252,232,226)
    val inboxTagUrgentColor = Color(0xFFE76F51) // rgb(231,111,81)
    val inboxTagNewBg = Color(0xFFFEF3C7)    // rgb(254,243,199)
    val inboxTagNewColor = Color(0xFFB45309) // rgb(180,83,9)
    val inboxTagPendingBg = Color(0xFFFBF0D6) // rgb(251,240,214)
    val inboxTagPendingColor = Color(0xFFB07500) // rgb(176,117,00) — actually rgb(176,117,0)

    // ── Phone Frame ──
    val phoneBlack = Color(0xFF000000)
    val homeBarColor = Color(0x4D000000)    // rgba(0,0,0,0.3)
}

// ═══════════════════════════════════════════════════════════════
// Admin Typography — EXACT from JSON (Inter font family)
// ═══════════════════════════════════════════════════════════════

object AdminTypography {
    // Micro
    val ringSmall = TextStyle(fontSize = 8.sp, fontWeight = FontWeight.Bold)
    val badgeText = TextStyle(fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
    val microLabel = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Bold)
    val microLabelW600 = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.SemiBold)

    // Small text
    val metaText = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Medium)
    val sectionLabel = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold)
    val subtabActive = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
    val subtabInactive = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.SemiBold)

    // Medium text
    val alertTitle = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold)
    val metaSub = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium)
    val filterActive = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
    val filterInactive = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.SemiBold)

    // Body
    val barName = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    val rowTitle = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold)
    val statusBar = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    val personName = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
    val navItem = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium)

    // Headers
    val overlayTitle = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
    val profileName = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
    val headerName = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
    val avatarText = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
    val brandName = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)

    // Large numbers
    val ringNum = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Black)
    val pulseNum = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Black)
    val summaryVal = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Black)
    val heroBig = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Black)

    // Special
    val pulseNumWithLine = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Black, lineHeight = 22.sp)
    val heroTotal = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    val heroSub = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium)
    val heroSubStrong = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
    val heroLabel = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold)
    val pulseLabel = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
    val pulseTrend = TextStyle(fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
    val barLabel = TextStyle(fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
    val barPct = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold)
    val addBtn = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold)
    val personMeta = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Medium)
    val profileRole = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium)
    val profilePct = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold)
    val rowSub = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Medium)
    val greeting = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium)
    val summaryLabel = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold)
    val summarySub = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium)
    val sidebarAdminLabel = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium)
    val statusBarIcons = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.SemiBold)

    // ── Fee card ──
    val feeTitle = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
    val feeAmt = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Black)
    val feeAmtSm = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    val feeMeta = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium)
    val feeMetaStrong = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)

    // ── Section headers ──
    val sectionTitle = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold)
    val sectionLink = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.SemiBold)

    // ── Inbox items ──
    val inboxTitle = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold)
    val inboxMeta = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Medium)
    val inboxTag = TextStyle(fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)

    // ── Activity items ──
    val actTitle = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold)
    val actMeta = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Medium)
    val actTime = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.SemiBold)

    // ── Pulse sub ──
    val pulseSub = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Medium)
    val pulseUnit = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
}

// ═══════════════════════════════════════════════════════════════
// Admin Shapes — EXACT from JSON border-radius values
// ═══════════════════════════════════════════════════════════════

object AdminShapes {
    val card = RoundedCornerShape(14.dp)
    val hero = RoundedCornerShape(18.dp)
    val iconBg = RoundedCornerShape(10.dp)
    val pillOuter = RoundedCornerShape(14.dp)
    val pillInner = RoundedCornerShape(10.dp)
    val bottomNav = RoundedCornerShape(24.dp)
    val phone = RoundedCornerShape(50.dp)
    val screen = RoundedCornerShape(42.dp)
    val island = RoundedCornerShape(20.dp)
    val homeBar = RoundedCornerShape(4.dp)
    val navDot = RoundedCornerShape(50)
    val full = RoundedCornerShape(50)
    val miniBar = RoundedCornerShape(2.dp)
}

// ═══════════════════════════════════════════════════════════════
// Admin Elevation/Shadow tokens
// ═══════════════════════════════════════════════════════════════

object AdminElevation {
    const val card = 1
    const val cardAmbient = 2
    const val bottomNav = 3
    const val activeNavIcon = 4
    const val headerIcon = 2
}

// ═══════════════════════════════════════════════════════════════
// Enums
// ═══════════════════════════════════════════════════════════════

enum class AdminTab(val label: String) {
    HOME("Home"),
    PEOPLE("People"),
    RECORDS("Records"),
    COMMS("Comms"),
    SETTINGS("Settings")
}

enum class AvatarTint(val bg: Color, val color: Color) {
    SIENNA(AdminColors.siennaBg, AdminColors.sienna),
    SKY(AdminColors.skyBlueBg, AdminColors.skyBlue),
    GOLD(AdminColors.goldBg, AdminColors.amber),
    CORAL(AdminColors.alertRedBg, AdminColors.alertRed),
    MINT(AdminColors.goodGreenBg, AdminColors.goodGreen),
    PLUM(AdminColors.purpleBg, AdminColors.purple)
}

enum class SettingIconTint(val bg: Color) {
    CALENDAR(AdminColors.siennaBg),
    CLASSES(AdminColors.skyBlueBg),
    TRANSPORT(AdminColors.goodGreenBg),
    LIBRARY(AdminColors.skyBlueBg),
    ID_CARDS(AdminColors.goldBg),
    HEALTH(AdminColors.alertRedBg),
    LEAVE(AdminColors.siennaBg),
    SCHOLAR(AdminColors.purpleBg),
    BRANDING(AdminColors.alertRedBg),
    FEE(AdminColors.siennaBg),
    NOTIF(AdminColors.alertRedBg),
    EXPORT(AdminColors.pillBg),
    HELP(AdminColors.pillBg),
    PROFILE(AdminColors.siennaBg)
}

enum class BarFillType(val color: Color) {
    GOOD(AdminColors.goodGreen),
    MID(AdminColors.amber),
    LOW(AdminColors.alertRed)
}
