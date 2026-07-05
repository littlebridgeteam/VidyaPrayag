package com.littlebridge.enrollplus.ui.v2.tokens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import vidyaprayag.composeapp.generated.resources.Res
import vidyaprayag.composeapp.generated.resources.inter_black
import vidyaprayag.composeapp.generated.resources.inter_bold
import vidyaprayag.composeapp.generated.resources.inter_extrabold
import vidyaprayag.composeapp.generated.resources.inter_medium
import vidyaprayag.composeapp.generated.resources.inter_regular
import vidyaprayag.composeapp.generated.resources.inter_semibold

/**
 * M3 Expressive typography tokens — Inter font family.
 * All sizes/weights/letter-spacing lifted verbatim from HTML CSS.
 *
 * The HTML uses `font-family: 'Inter', system-ui, -apple-system, sans-serif`.
 * Inter is bundled as .ttf under composeResources/font/ and loaded via
 * [LocalInterFont]. All properties are @Composable get() so they resolve
 * the correct FontFamily at runtime.
 */
object VTypography {

    // ── Font family ───────────────────────────────────────────────────────────
    val Inter: FontFamily @Composable get() = LocalInterFont.current

    // ── Greeting ──────────────────────────────────────────────────────────────
    // .greeting-eyebrow: 13px / 600 / 0.01em / primary
    val Eyebrow : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.01.em,
    )

    // .greeting-title: 34px / 800 / -0.035em / line-height 1.05
    val GreetingTitle : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 34.sp, fontWeight = FontWeight.ExtraBold,
        lineHeight = 35.7.sp, letterSpacing = (-0.035).em,
    )

    // .greeting-title em: 34px / 900 / -0.045em (gradient text)
    val GreetingTitleAccent : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 34.sp, fontWeight = FontWeight.Black,
        lineHeight = 35.7.sp, letterSpacing = (-0.045).em,
    )

    // ── Landing ───────────────────────────────────────────────────────────────
    // .landing-headline: 36px / 800 / -0.035em / line-height 1.05
    val LandingHeadline : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold,
        lineHeight = 37.8.sp, letterSpacing = (-0.035).em,
    )

    // .landing-headline em: 36px / 900 / -0.045em
    val LandingHeadlineAccent : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 36.sp, fontWeight = FontWeight.Black,
        lineHeight = 37.8.sp, letterSpacing = (-0.045).em,
    )

    // .landing-sub: 15px / 500 / on-surface-variant / line-height 1.55
    val LandingSub : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 15.sp, fontWeight = FontWeight.Medium,
        lineHeight = 23.25.sp,
    )

    // .landing-brand-text: 20px / 900 / -0.03em
    val BrandText : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 20.sp, fontWeight = FontWeight.Black,
        letterSpacing = (-0.03).em,
    )

    // .landing-version: 11px / 700
    val VersionTag : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 11.sp, fontWeight = FontWeight.Bold,
    )

    // .landing-roles-title: 16px / 800 / -0.02em
    val RolesTitle : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-0.02).em,
    )

    // ── Section header ────────────────────────────────────────────────────────
    // .section-header h2: 24px / 800 / -0.03em
    val SectionHeader : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-0.03).em,
    )

    // .section-header .link: 14px / 600 / primary
    val SectionLink : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
    )

    // ── Top bar ───────────────────────────────────────────────────────────────
    // .top-bar-title: 20px / 800 / -0.03em
    val TopBarTitle : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-0.03).em,
    )

    // .overlay-title: same as top-bar-title
    val OverlayTitle: TextStyle @Composable get() = TopBarTitle

    // ── Status bar ────────────────────────────────────────────────────────────
    // .status-time: 15px / 700 / -0.01em
    val StatusTime : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 15.sp, fontWeight = FontWeight.Bold,
        letterSpacing = (-0.01).em,
    )

    // ── Hero card ─────────────────────────────────────────────────────────────
    // .hero-student-info h3: 22px / 800 / -0.03em
    val HeroName : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-0.03).em,
    )

    // .hero-student-info p: 14px / 500 / opacity 0.7
    val HeroSubtitle : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 14.sp, fontWeight = FontWeight.Medium,
    )

    // .hero-stat-value: 26px / 900 / -0.04em
    val HeroStatValue : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 26.sp, fontWeight = FontWeight.Black,
        letterSpacing = (-0.04).em,
    )

    // .hero-stat-label: 10px / 600 / uppercase / 0.06em
    val HeroStatLabel : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 10.sp, fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.06.em,
    )

    // .hero-live-pill: 12px / 700 / 0.03em / uppercase
    val LivePill : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 12.sp, fontWeight = FontWeight.Bold,
        letterSpacing = 0.03.em,
    )

    // .lhc-title: 24px / 800 / -0.03em / line-height 1.2
    val LandingHeroTitle : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold,
        lineHeight = 28.8.sp, letterSpacing = (-0.03).em,
    )

    // .lhc-desc: 14px / 500 / opacity 0.75 / line-height 1.5
    val LandingHeroDesc : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 14.sp, fontWeight = FontWeight.Medium,
        lineHeight = 21.sp,
    )

    // .lhc-feature: 12px / 600
    val LandingHeroFeature : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
    )

    // ── Landing stats ─────────────────────────────────────────────────────────
    // .ls-val: 24px / 900 / -0.04em
    val LandingStatValue : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 24.sp, fontWeight = FontWeight.Black,
        letterSpacing = (-0.04).em,
    )

    // .ls-label: 10px / 600 / uppercase / 0.06em
    val LandingStatLabel : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 10.sp, fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.06.em,
    )

    // ── Feature carousel ──────────────────────────────────────────────────────
    // .fc-title: 18px / 800 / -0.025em
    val FeatureTitle : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-0.025).em,
    )

    // .fc-subtitle: 14px / 500 / line-height 1.4
    val FeatureSubtitle : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 14.sp, fontWeight = FontWeight.Medium,
        lineHeight = 19.6.sp,
    )

    // .fc-amount: 30px / 900 / -0.04em
    val FeatureAmount : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 30.sp, fontWeight = FontWeight.Black,
        letterSpacing = (-0.04).em,
    )

    // .fc-badge: 13px / 700
    val FeatureBadge : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 13.sp, fontWeight = FontWeight.Bold,
    )

    // ── Chips ─────────────────────────────────────────────────────────────────
    // .chip: 14px / 600 / -0.005em
    val Chip : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
        letterSpacing = (-0.005).em,
    )

    // .sub-tab: 13px / 600
    val SubTab : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
    )

    // .discover-filter: 13px / 600
    val DiscoverFilter: TextStyle @Composable get() = SubTab

    // ── Navigation ────────────────────────────────────────────────────────────
    // .nav-label: 12px / 600
    val NavLabel : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
    )

    // .nav-label active: 12px / 700
    val NavLabelActive : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 12.sp, fontWeight = FontWeight.Bold,
    )

    // .nav-badge: 10px / 800
    val NavBadge : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold,
    )

    // ── Schedule ──────────────────────────────────────────────────────────────
    // .sc-hr: 22px / 900 / -0.04em
    val ScheduleHour : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 22.sp, fontWeight = FontWeight.Black,
        letterSpacing = (-0.04).em,
    )

    // .sc-ampm: 11px / 600 / uppercase / 0.04em
    val ScheduleAmPm : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.04.em,
    )

    // .sc-subject: 16px / 800 / -0.02em
    val ScheduleSubject : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-0.02).em,
    )

    // .sc-teacher: 13px / 500 / opacity 0.65
    val ScheduleTeacher : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 13.sp, fontWeight = FontWeight.Medium,
    )

    // .sc-status: 11px / 800 / uppercase / 0.04em
    val ScheduleStatus : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold,
        letterSpacing = 0.04.em,
    )

    // .sp-label: 12px / 600
    val ScheduleProgressLabel : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
    )

    // ── Updates ───────────────────────────────────────────────────────────────
    // .update-source: 12px / 700
    val UpdateSource : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 12.sp, fontWeight = FontWeight.Bold,
    )

    // .update-source .us-time: 12px / 500
    val UpdateTime : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 12.sp, fontWeight = FontWeight.Medium,
    )

    // .update-title: 16px / 700 / -0.015em / line-height 1.3
    val UpdateTitle : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 16.sp, fontWeight = FontWeight.Bold,
        lineHeight = 20.8.sp, letterSpacing = (-0.015).em,
    )

    // .update-text: 14px / 500 / line-height 1.5
    val UpdateText : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 14.sp, fontWeight = FontWeight.Medium,
        lineHeight = 21.sp,
    )

    // .update-action-btn: 13px / 600
    val UpdateAction : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
    )

    // ── Live update banner ────────────────────────────────────────────────────
    // .lu-title: 14px / 700
    val LiveUpdateTitle : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 14.sp, fontWeight = FontWeight.Bold,
    )

    // .lu-sub: 12px / 500
    val LiveUpdateSub : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 12.sp, fontWeight = FontWeight.Medium,
    )

    // ── Quick stats ───────────────────────────────────────────────────────────
    // .qs-value: 22px / 900 / -0.03em
    val QuickStatValue : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 22.sp, fontWeight = FontWeight.Black,
        letterSpacing = (-0.03).em,
    )

    // .qs-label: 11px / 600 / uppercase / 0.04em
    val QuickStatLabel : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.04.em,
    )

    // ── Stat tile (profile) ───────────────────────────────────────────────────
    // .stat-value: 28px / 900 / -0.04em
    val StatValue : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 28.sp, fontWeight = FontWeight.Black,
        letterSpacing = (-0.04).em,
    )

    // .stat-label: 12px / 600 / uppercase / 0.04em
    val StatLabel : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.04.em,
    )

    // .stat-trend: 11px / 600
    val StatTrend : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
    )

    // ── Progress cards ────────────────────────────────────────────────────────
    // .pc-title: 16px / 800 / -0.02em
    val ProgressCardTitle : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-0.02).em,
    )

    // .pc-ring-inner: 22px / 900
    val ProgressRingValue : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 22.sp, fontWeight = FontWeight.Black,
    )

    // .pc-metric-label: 13px / 600
    val MetricLabel : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
    )

    // .pc-metric-value: 12px / 600
    val MetricValue : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
    )

    // ── Syllabus ──────────────────────────────────────────────────────────────
    // .syllabus-name: 14px / 700
    val SyllabusName : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 14.sp, fontWeight = FontWeight.Bold,
    )

    // .syllabus-pct: 13px / 700
    val SyllabusPct : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 13.sp, fontWeight = FontWeight.Bold,
    )

    // ── Marks ─────────────────────────────────────────────────────────────────
    // .mark-name: 15px / 700
    val MarkName : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 15.sp, fontWeight = FontWeight.Bold,
    )

    // .mark-date: 12px / 500
    val MarkDate : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 12.sp, fontWeight = FontWeight.Medium,
    )

    // .mark-score-val: 22px / 900 / -0.03em
    val MarkScoreVal : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 22.sp, fontWeight = FontWeight.Black,
        letterSpacing = (-0.03).em,
    )

    // .mark-score-max: 12px / 500
    val MarkScoreMax : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 12.sp, fontWeight = FontWeight.Medium,
    )

    // ── Homework ──────────────────────────────────────────────────────────────
    // .hw-title: 15px / 700
    val HwTitle : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 15.sp, fontWeight = FontWeight.Bold,
    )

    // .hw-sub: 12px / 500
    val HwSub : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 12.sp, fontWeight = FontWeight.Medium,
    )

    // .hw-status: 11px / 700 / uppercase / 0.04em
    val HwStatus : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 11.sp, fontWeight = FontWeight.Bold,
        letterSpacing = 0.04.em,
    )

    // ── Fees ──────────────────────────────────────────────────────────────────
    // .fees-hero-label: 13px / 600 / opacity 0.7
    val FeesHeroLabel : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
    )

    // .fees-hero-amount: 40px / 900 / -0.04em
    val FeesAmount : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 40.sp, fontWeight = FontWeight.Black,
        letterSpacing = (-0.04).em,
    )

    // .fees-hero-due: 14px / 500 / opacity 0.8
    val FeesDue : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 14.sp, fontWeight = FontWeight.Medium,
    )

    // .pay-title: 15px / 700
    val PayTitle : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 15.sp, fontWeight = FontWeight.Bold,
    )

    // .pay-date: 12px / 500
    val PayDate : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 12.sp, fontWeight = FontWeight.Medium,
    )

    // .pay-amount: 18px / 900 / -0.03em
    val PayAmount : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 18.sp, fontWeight = FontWeight.Black,
        letterSpacing = (-0.03).em,
    )

    // ── Conversations ─────────────────────────────────────────────────────────
    // .seg-btn: 14px / 600
    val SegBtn : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
    )

    // .thread-name: 15px / 700
    val ThreadName : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 15.sp, fontWeight = FontWeight.Bold,
    )

    // .thread-preview: 13px / 500
    val ThreadPreview : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 13.sp, fontWeight = FontWeight.Medium,
    )

    // .thread-time: 11px / 500
    val ThreadTime : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 11.sp, fontWeight = FontWeight.Medium,
    )

    // .thread-badge: 11px / 800
    val ThreadBadge : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold,
    )

    // ── Profile hero ──────────────────────────────────────────────────────────
    // .ph-info h3: 22px / 800 / -0.03em
    val ProfileName : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-0.03).em,
    )

    // .ph-info p: 14px / 500 / opacity 0.7
    val ProfileMeta : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 14.sp, fontWeight = FontWeight.Medium,
    )

    // .ph-badge: 11px / 700
    val ProfileBadge : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 11.sp, fontWeight = FontWeight.Bold,
    )

    // .ph-level-text: 14px / 700
    val ProfileLevelText : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 14.sp, fontWeight = FontWeight.Bold,
    )

    // .ph-xp: 12px / 600 / opacity 0.7
    val ProfileXp : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
    )

    // ── Badges ────────────────────────────────────────────────────────────────
    // .badge-name: 14px / 800 / -0.02em
    val BadgeName : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-0.02).em,
    )

    // .badge-desc: 11px / 500 / line-height 1.4
    val BadgeDesc : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 11.sp, fontWeight = FontWeight.Medium,
        lineHeight = 15.4.sp,
    )

    // .badge-earned-tag: 10px / 800 / uppercase / 0.06em
    val BadgeEarnedTag : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold,
        letterSpacing = 0.06.em,
    )

    // .badge-progress-text: 10px / 700 / uppercase / 0.04em
    val BadgeProgressText : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 10.sp, fontWeight = FontWeight.Bold,
        letterSpacing = 0.04.em,
    )

    // ── School cards ──────────────────────────────────────────────────────────
    // .scf-name: 17px / 800 / -0.02em
    val SchoolName : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-0.02).em,
    )

    // .scf-addr: 13px / 500
    val SchoolAddr : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 13.sp, fontWeight = FontWeight.Medium,
    )

    // .scf-tag: 11px / 700
    val SchoolTag : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 11.sp, fontWeight = FontWeight.Bold,
    )

    // .scf-stat-val: 18px / 900 / -0.03em
    val SchoolStatVal : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 18.sp, fontWeight = FontWeight.Black,
        letterSpacing = (-0.03).em,
    )

    // .scf-stat-label: 10px / 600 / uppercase / 0.04em
    val SchoolStatLabel : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 10.sp, fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.04.em,
    )

    // .scf-action: 14px / 700
    val SchoolAction : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 14.sp, fontWeight = FontWeight.Bold,
    )

    // .scf-rating: 13px / 800
    val SchoolRating : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold,
    )

    // .school-option-name: 15px / 800
    val SchoolOptionName : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold,
    )

    // .school-option-meta: 12px / 500
    val SchoolOptionMeta : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 12.sp, fontWeight = FontWeight.Medium,
    )

    // .school-option-logo: 16px / 900
    val SchoolOptionLogo : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 16.sp, fontWeight = FontWeight.Black,
    )

    // ── Account rows ──────────────────────────────────────────────────────────
    // .ar-label: 15px / 600
    val AccountLabel : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 15.sp, fontWeight = FontWeight.SemiBold,
    )

    // ── Role tiles ────────────────────────────────────────────────────────────
    // .role-tile-name: 17px / 800 / -0.02em
    val RoleTileName : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-0.02).em,
    )

    // .role-tile-desc: 13px / 500 / line-height 1.4
    val RoleTileDesc : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 13.sp, fontWeight = FontWeight.Medium,
        lineHeight = 18.2.sp,
    )

    // ── Trust badges ──────────────────────────────────────────────────────────
    // .trust-badge: 12px / 600
    val TrustBadge : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
    )

    // ── Login ─────────────────────────────────────────────────────────────────
    // .login-hero-title: 26px / 900 / -0.04em / white
    val LoginHeroTitle : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 26.sp, fontWeight = FontWeight.Black,
        letterSpacing = (-0.04).em,
    )

    // .login-hero-sub: 14px / 500 / rgba(255,255,255,0.7)
    val LoginHeroSub : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 14.sp, fontWeight = FontWeight.Medium,
    )

    // .login-hero-badge: 11px / 700 / uppercase / 0.06em
    val LoginHeroBadge : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 11.sp, fontWeight = FontWeight.Bold,
        letterSpacing = 0.06.em,
    )

    // .login-footer-text: 14px / 500
    val LoginFooter : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 14.sp, fontWeight = FontWeight.Medium,
    )

    // ── Onboarding ────────────────────────────────────────────────────────────
    // .onboard-title: 26px / 900 / -0.04em
    val OnboardTitle : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 26.sp, fontWeight = FontWeight.Black,
        letterSpacing = (-0.04).em,
    )

    // .onboard-desc: 15px / 500 / line-height 1.6
    val OnboardDesc : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 15.sp, fontWeight = FontWeight.Medium,
        lineHeight = 24.sp,
    )

    // ── Buttons ───────────────────────────────────────────────────────────────
    // .btn-primary / .btn-secondary: 15px / 700
    val ButtonPrimary : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 15.sp, fontWeight = FontWeight.Bold,
    )

    // .btn-text: 14px / 600
    val ButtonText : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
    )

    // .social-btn: 14px / 600
    val SocialButton : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
    )

    // .signup-type-btn: 14px / 700
    val SignupTypeBtn : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 14.sp, fontWeight = FontWeight.Bold,
    )

    // ── Form ──────────────────────────────────────────────────────────────────
    // .form-label (parent-portal): 13px / 600
    val FormLabelPortal : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
    )

    // .form-label (auth-flow): 12px / 600 / uppercase / 0.04em
    val FormLabelAuth : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.04.em,
    )

    // .form-input: 15px / 500
    val FormInput : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 15.sp, fontWeight = FontWeight.Medium,
    )

    // .search-field input: 16px / 500 (parent-portal) / 15px / 500 (auth-flow)
    val SearchInput : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 16.sp, fontWeight = FontWeight.Medium,
    )

    val SearchInputAuth : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 15.sp, fontWeight = FontWeight.Medium,
    )

    // ── Divider ───────────────────────────────────────────────────────────────
    // .divider span: 12px / 600 / uppercase / 0.06em
    val DividerLabel : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.06.em,
    )

    // ── Chat ──────────────────────────────────────────────────────────────────
    // .chat-text: 14px / 500 / line-height 1.5
    val ChatText : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 14.sp, fontWeight = FontWeight.Medium,
        lineHeight = 21.sp,
    )

    // .chat-time: 11px / 500
    val ChatTime : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 11.sp, fontWeight = FontWeight.Medium,
    )

    // ── Child link ────────────────────────────────────────────────────────────
    // .child-name: 17px / 800
    val ChildName : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold,
    )

    // .child-meta: 13px / 500
    val ChildMeta : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 13.sp, fontWeight = FontWeight.Medium,
    )

    // .linked-badge: 12px / 700
    val LinkedBadge : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 12.sp, fontWeight = FontWeight.Bold,
    )

    // ── Action cards (academics) ──────────────────────────────────────────────
    // .ac-title: 14px / 800
    val ActionCardTitle : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold,
    )

    // .ac-sub: 11px / 500
    val ActionCardSub : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 11.sp, fontWeight = FontWeight.Medium,
    )

    // ── Generic body text ─────────────────────────────────────────────────────
    // Used for various 14px / 500 / on-surface-variant / line-height 1.6
    val BodyMedium : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 14.sp, fontWeight = FontWeight.Medium,
        lineHeight = 22.4.sp,
    )

    // 15px / 500 / on-surface-variant / line-height 1.5 (page descriptions)
    val BodyLarge : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 15.sp, fontWeight = FontWeight.Medium,
        lineHeight = 22.5.sp,
    )

    // ── Landing terms ─────────────────────────────────────────────────────────
    // .landing-terms: 12px / 500 / line-height 1.5
    val LandingTerms : TextStyle @Composable get() = TextStyle(
        fontFamily = Inter, fontSize = 12.sp, fontWeight = FontWeight.Medium,
        lineHeight = 18.sp,
    )
}

// ── CompositionLocal for Inter font family ──────────────────────────────────
// Default is FontFamily.SansSerif so previews/tests work without a provider.
// PremiumTheme provides the actual Inter font loaded from composeResources.
val LocalInterFont: ProvidableCompositionLocal<FontFamily> =
    staticCompositionLocalOf { FontFamily.SansSerif }

/**
 * Loads the Inter font family from bundled .ttf resources.
 * Call from a @Composable context (e.g. inside PremiumTheme).
 */
@Composable
fun interFontFamily(): FontFamily = FontFamily(
    Font(Res.font.inter_regular, FontWeight.Normal),
    Font(Res.font.inter_medium, FontWeight.Medium),
    Font(Res.font.inter_semibold, FontWeight.SemiBold),
    Font(Res.font.inter_bold, FontWeight.Bold),
    Font(Res.font.inter_extrabold, FontWeight.ExtraBold),
    Font(Res.font.inter_black, FontWeight.Black),
)
