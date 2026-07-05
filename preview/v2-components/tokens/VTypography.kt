package com.littlebridge.enrollplus.ui.v2.tokens

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

/**
 * M3 Expressive typography tokens — Inter font family.
 * All sizes/weights/letter-spacing lifted verbatim from HTML CSS.
 *
 * The HTML uses `font-family: 'Inter', system-ui, -apple-system, sans-serif`.
 * In Compose Multiplatform we use FontFamily.SansSerif as the Inter fallback
 * until Inter is bundled as a resource.
 */
object VTypography {

    // ── Font family ───────────────────────────────────────────────────────────
    // When Inter font resources are added, replace with:
    //   FontFamily(Font(Res.font.inter_regular), Font(Res.font.inter_medium), ...)
    val Inter = FontFamily.SansSerif

    // ── Greeting ──────────────────────────────────────────────────────────────
    // .greeting-eyebrow: 13px / 600 / 0.01em / primary
    val Eyebrow = TextStyle(
        fontFamily = Inter, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.01.em,
    )

    // .greeting-title: 34px / 800 / -0.035em / line-height 1.05
    val GreetingTitle = TextStyle(
        fontFamily = Inter, fontSize = 34.sp, fontWeight = FontWeight.ExtraBold,
        lineHeight = 35.7.sp, letterSpacing = (-0.035).em,
    )

    // .greeting-title em: 34px / 900 / -0.045em (gradient text)
    val GreetingTitleAccent = TextStyle(
        fontFamily = Inter, fontSize = 34.sp, fontWeight = FontWeight.Black,
        lineHeight = 35.7.sp, letterSpacing = (-0.045).em,
    )

    // ── Landing ───────────────────────────────────────────────────────────────
    // .landing-headline: 36px / 800 / -0.035em / line-height 1.05
    val LandingHeadline = TextStyle(
        fontFamily = Inter, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold,
        lineHeight = 37.8.sp, letterSpacing = (-0.035).em,
    )

    // .landing-headline em: 36px / 900 / -0.045em
    val LandingHeadlineAccent = TextStyle(
        fontFamily = Inter, fontSize = 36.sp, fontWeight = FontWeight.Black,
        lineHeight = 37.8.sp, letterSpacing = (-0.045).em,
    )

    // .landing-sub: 15px / 500 / on-surface-variant / line-height 1.55
    val LandingSub = TextStyle(
        fontFamily = Inter, fontSize = 15.sp, fontWeight = FontWeight.Medium,
        lineHeight = 23.25.sp,
    )

    // .landing-brand-text: 20px / 900 / -0.03em
    val BrandText = TextStyle(
        fontFamily = Inter, fontSize = 20.sp, fontWeight = FontWeight.Black,
        letterSpacing = (-0.03).em,
    )

    // .landing-version: 11px / 700
    val VersionTag = TextStyle(
        fontFamily = Inter, fontSize = 11.sp, fontWeight = FontWeight.Bold,
    )

    // .landing-roles-title: 16px / 800 / -0.02em
    val RolesTitle = TextStyle(
        fontFamily = Inter, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-0.02).em,
    )

    // ── Section header ────────────────────────────────────────────────────────
    // .section-header h2: 24px / 800 / -0.03em
    val SectionHeader = TextStyle(
        fontFamily = Inter, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-0.03).em,
    )

    // .section-header .link: 14px / 600 / primary
    val SectionLink = TextStyle(
        fontFamily = Inter, fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
    )

    // ── Top bar ───────────────────────────────────────────────────────────────
    // .top-bar-title: 20px / 800 / -0.03em
    val TopBarTitle = TextStyle(
        fontFamily = Inter, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-0.03).em,
    )

    // .overlay-title: same as top-bar-title
    val OverlayTitle = TopBarTitle

    // ── Status bar ────────────────────────────────────────────────────────────
    // .status-time: 15px / 700 / -0.01em
    val StatusTime = TextStyle(
        fontFamily = Inter, fontSize = 15.sp, fontWeight = FontWeight.Bold,
        letterSpacing = (-0.01).em,
    )

    // ── Hero card ─────────────────────────────────────────────────────────────
    // .hero-student-info h3: 22px / 800 / -0.03em
    val HeroName = TextStyle(
        fontFamily = Inter, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-0.03).em,
    )

    // .hero-student-info p: 14px / 500 / opacity 0.7
    val HeroSubtitle = TextStyle(
        fontFamily = Inter, fontSize = 14.sp, fontWeight = FontWeight.Medium,
    )

    // .hero-stat-value: 26px / 900 / -0.04em
    val HeroStatValue = TextStyle(
        fontFamily = Inter, fontSize = 26.sp, fontWeight = FontWeight.Black,
        letterSpacing = (-0.04).em,
    )

    // .hero-stat-label: 10px / 600 / uppercase / 0.06em
    val HeroStatLabel = TextStyle(
        fontFamily = Inter, fontSize = 10.sp, fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.06.em,
    )

    // .hero-live-pill: 12px / 700 / 0.03em / uppercase
    val LivePill = TextStyle(
        fontFamily = Inter, fontSize = 12.sp, fontWeight = FontWeight.Bold,
        letterSpacing = 0.03.em,
    )

    // .lhc-title: 24px / 800 / -0.03em / line-height 1.2
    val LandingHeroTitle = TextStyle(
        fontFamily = Inter, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold,
        lineHeight = 28.8.sp, letterSpacing = (-0.03).em,
    )

    // .lhc-desc: 14px / 500 / opacity 0.75 / line-height 1.5
    val LandingHeroDesc = TextStyle(
        fontFamily = Inter, fontSize = 14.sp, fontWeight = FontWeight.Medium,
        lineHeight = 21.sp,
    )

    // .lhc-feature: 12px / 600
    val LandingHeroFeature = TextStyle(
        fontFamily = Inter, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
    )

    // ── Landing stats ─────────────────────────────────────────────────────────
    // .ls-val: 24px / 900 / -0.04em
    val LandingStatValue = TextStyle(
        fontFamily = Inter, fontSize = 24.sp, fontWeight = FontWeight.Black,
        letterSpacing = (-0.04).em,
    )

    // .ls-label: 10px / 600 / uppercase / 0.06em
    val LandingStatLabel = TextStyle(
        fontFamily = Inter, fontSize = 10.sp, fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.06.em,
    )

    // ── Feature carousel ──────────────────────────────────────────────────────
    // .fc-title: 18px / 800 / -0.025em
    val FeatureTitle = TextStyle(
        fontFamily = Inter, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-0.025).em,
    )

    // .fc-subtitle: 14px / 500 / line-height 1.4
    val FeatureSubtitle = TextStyle(
        fontFamily = Inter, fontSize = 14.sp, fontWeight = FontWeight.Medium,
        lineHeight = 19.6.sp,
    )

    // .fc-amount: 30px / 900 / -0.04em
    val FeatureAmount = TextStyle(
        fontFamily = Inter, fontSize = 30.sp, fontWeight = FontWeight.Black,
        letterSpacing = (-0.04).em,
    )

    // .fc-badge: 13px / 700
    val FeatureBadge = TextStyle(
        fontFamily = Inter, fontSize = 13.sp, fontWeight = FontWeight.Bold,
    )

    // ── Chips ─────────────────────────────────────────────────────────────────
    // .chip: 14px / 600 / -0.005em
    val Chip = TextStyle(
        fontFamily = Inter, fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
        letterSpacing = (-0.005).em,
    )

    // .sub-tab: 13px / 600
    val SubTab = TextStyle(
        fontFamily = Inter, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
    )

    // .discover-filter: 13px / 600
    val DiscoverFilter = SubTab

    // ── Navigation ────────────────────────────────────────────────────────────
    // .nav-label: 12px / 600
    val NavLabel = TextStyle(
        fontFamily = Inter, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
    )

    // .nav-label active: 12px / 700
    val NavLabelActive = TextStyle(
        fontFamily = Inter, fontSize = 12.sp, fontWeight = FontWeight.Bold,
    )

    // .nav-badge: 10px / 800
    val NavBadge = TextStyle(
        fontFamily = Inter, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold,
    )

    // ── Schedule ──────────────────────────────────────────────────────────────
    // .sc-hr: 22px / 900 / -0.04em
    val ScheduleHour = TextStyle(
        fontFamily = Inter, fontSize = 22.sp, fontWeight = FontWeight.Black,
        letterSpacing = (-0.04).em,
    )

    // .sc-ampm: 11px / 600 / uppercase / 0.04em
    val ScheduleAmPm = TextStyle(
        fontFamily = Inter, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.04.em,
    )

    // .sc-subject: 16px / 800 / -0.02em
    val ScheduleSubject = TextStyle(
        fontFamily = Inter, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-0.02).em,
    )

    // .sc-teacher: 13px / 500 / opacity 0.65
    val ScheduleTeacher = TextStyle(
        fontFamily = Inter, fontSize = 13.sp, fontWeight = FontWeight.Medium,
    )

    // .sc-status: 11px / 800 / uppercase / 0.04em
    val ScheduleStatus = TextStyle(
        fontFamily = Inter, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold,
        letterSpacing = 0.04.em,
    )

    // .sp-label: 12px / 600
    val ScheduleProgressLabel = TextStyle(
        fontFamily = Inter, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
    )

    // ── Updates ───────────────────────────────────────────────────────────────
    // .update-source: 12px / 700
    val UpdateSource = TextStyle(
        fontFamily = Inter, fontSize = 12.sp, fontWeight = FontWeight.Bold,
    )

    // .update-source .us-time: 12px / 500
    val UpdateTime = TextStyle(
        fontFamily = Inter, fontSize = 12.sp, fontWeight = FontWeight.Medium,
    )

    // .update-title: 16px / 700 / -0.015em / line-height 1.3
    val UpdateTitle = TextStyle(
        fontFamily = Inter, fontSize = 16.sp, fontWeight = FontWeight.Bold,
        lineHeight = 20.8.sp, letterSpacing = (-0.015).em,
    )

    // .update-text: 14px / 500 / line-height 1.5
    val UpdateText = TextStyle(
        fontFamily = Inter, fontSize = 14.sp, fontWeight = FontWeight.Medium,
        lineHeight = 21.sp,
    )

    // .update-action-btn: 13px / 600
    val UpdateAction = TextStyle(
        fontFamily = Inter, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
    )

    // ── Live update banner ────────────────────────────────────────────────────
    // .lu-title: 14px / 700
    val LiveUpdateTitle = TextStyle(
        fontFamily = Inter, fontSize = 14.sp, fontWeight = FontWeight.Bold,
    )

    // .lu-sub: 12px / 500
    val LiveUpdateSub = TextStyle(
        fontFamily = Inter, fontSize = 12.sp, fontWeight = FontWeight.Medium,
    )

    // ── Quick stats ───────────────────────────────────────────────────────────
    // .qs-value: 22px / 900 / -0.03em
    val QuickStatValue = TextStyle(
        fontFamily = Inter, fontSize = 22.sp, fontWeight = FontWeight.Black,
        letterSpacing = (-0.03).em,
    )

    // .qs-label: 11px / 600 / uppercase / 0.04em
    val QuickStatLabel = TextStyle(
        fontFamily = Inter, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.04.em,
    )

    // ── Stat tile (profile) ───────────────────────────────────────────────────
    // .stat-value: 28px / 900 / -0.04em
    val StatValue = TextStyle(
        fontFamily = Inter, fontSize = 28.sp, fontWeight = FontWeight.Black,
        letterSpacing = (-0.04).em,
    )

    // .stat-label: 12px / 600 / uppercase / 0.04em
    val StatLabel = TextStyle(
        fontFamily = Inter, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.04.em,
    )

    // .stat-trend: 11px / 600
    val StatTrend = TextStyle(
        fontFamily = Inter, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
    )

    // ── Progress cards ────────────────────────────────────────────────────────
    // .pc-title: 16px / 800 / -0.02em
    val ProgressCardTitle = TextStyle(
        fontFamily = Inter, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-0.02).em,
    )

    // .pc-ring-inner: 22px / 900
    val ProgressRingValue = TextStyle(
        fontFamily = Inter, fontSize = 22.sp, fontWeight = FontWeight.Black,
    )

    // .pc-metric-label: 13px / 600
    val MetricLabel = TextStyle(
        fontFamily = Inter, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
    )

    // .pc-metric-value: 12px / 600
    val MetricValue = TextStyle(
        fontFamily = Inter, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
    )

    // ── Syllabus ──────────────────────────────────────────────────────────────
    // .syllabus-name: 14px / 700
    val SyllabusName = TextStyle(
        fontFamily = Inter, fontSize = 14.sp, fontWeight = FontWeight.Bold,
    )

    // .syllabus-pct: 13px / 700
    val SyllabusPct = TextStyle(
        fontFamily = Inter, fontSize = 13.sp, fontWeight = FontWeight.Bold,
    )

    // ── Marks ─────────────────────────────────────────────────────────────────
    // .mark-name: 15px / 700
    val MarkName = TextStyle(
        fontFamily = Inter, fontSize = 15.sp, fontWeight = FontWeight.Bold,
    )

    // .mark-date: 12px / 500
    val MarkDate = TextStyle(
        fontFamily = Inter, fontSize = 12.sp, fontWeight = FontWeight.Medium,
    )

    // .mark-score-val: 22px / 900 / -0.03em
    val MarkScoreVal = TextStyle(
        fontFamily = Inter, fontSize = 22.sp, fontWeight = FontWeight.Black,
        letterSpacing = (-0.03).em,
    )

    // .mark-score-max: 12px / 500
    val MarkScoreMax = TextStyle(
        fontFamily = Inter, fontSize = 12.sp, fontWeight = FontWeight.Medium,
    )

    // ── Homework ──────────────────────────────────────────────────────────────
    // .hw-title: 15px / 700
    val HwTitle = TextStyle(
        fontFamily = Inter, fontSize = 15.sp, fontWeight = FontWeight.Bold,
    )

    // .hw-sub: 12px / 500
    val HwSub = TextStyle(
        fontFamily = Inter, fontSize = 12.sp, fontWeight = FontWeight.Medium,
    )

    // .hw-status: 11px / 700 / uppercase / 0.04em
    val HwStatus = TextStyle(
        fontFamily = Inter, fontSize = 11.sp, fontWeight = FontWeight.Bold,
        letterSpacing = 0.04.em,
    )

    // ── Fees ──────────────────────────────────────────────────────────────────
    // .fees-hero-label: 13px / 600 / opacity 0.7
    val FeesHeroLabel = TextStyle(
        fontFamily = Inter, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
    )

    // .fees-hero-amount: 40px / 900 / -0.04em
    val FeesAmount = TextStyle(
        fontFamily = Inter, fontSize = 40.sp, fontWeight = FontWeight.Black,
        letterSpacing = (-0.04).em,
    )

    // .fees-hero-due: 14px / 500 / opacity 0.8
    val FeesDue = TextStyle(
        fontFamily = Inter, fontSize = 14.sp, fontWeight = FontWeight.Medium,
    )

    // .pay-title: 15px / 700
    val PayTitle = TextStyle(
        fontFamily = Inter, fontSize = 15.sp, fontWeight = FontWeight.Bold,
    )

    // .pay-date: 12px / 500
    val PayDate = TextStyle(
        fontFamily = Inter, fontSize = 12.sp, fontWeight = FontWeight.Medium,
    )

    // .pay-amount: 18px / 900 / -0.03em
    val PayAmount = TextStyle(
        fontFamily = Inter, fontSize = 18.sp, fontWeight = FontWeight.Black,
        letterSpacing = (-0.03).em,
    )

    // ── Conversations ─────────────────────────────────────────────────────────
    // .seg-btn: 14px / 600
    val SegBtn = TextStyle(
        fontFamily = Inter, fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
    )

    // .thread-name: 15px / 700
    val ThreadName = TextStyle(
        fontFamily = Inter, fontSize = 15.sp, fontWeight = FontWeight.Bold,
    )

    // .thread-preview: 13px / 500
    val ThreadPreview = TextStyle(
        fontFamily = Inter, fontSize = 13.sp, fontWeight = FontWeight.Medium,
    )

    // .thread-time: 11px / 500
    val ThreadTime = TextStyle(
        fontFamily = Inter, fontSize = 11.sp, fontWeight = FontWeight.Medium,
    )

    // .thread-badge: 11px / 800
    val ThreadBadge = TextStyle(
        fontFamily = Inter, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold,
    )

    // ── Profile hero ──────────────────────────────────────────────────────────
    // .ph-info h3: 22px / 800 / -0.03em
    val ProfileName = TextStyle(
        fontFamily = Inter, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-0.03).em,
    )

    // .ph-info p: 14px / 500 / opacity 0.7
    val ProfileMeta = TextStyle(
        fontFamily = Inter, fontSize = 14.sp, fontWeight = FontWeight.Medium,
    )

    // .ph-badge: 11px / 700
    val ProfileBadge = TextStyle(
        fontFamily = Inter, fontSize = 11.sp, fontWeight = FontWeight.Bold,
    )

    // .ph-level-text: 14px / 700
    val ProfileLevelText = TextStyle(
        fontFamily = Inter, fontSize = 14.sp, fontWeight = FontWeight.Bold,
    )

    // .ph-xp: 12px / 600 / opacity 0.7
    val ProfileXp = TextStyle(
        fontFamily = Inter, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
    )

    // ── Badges ────────────────────────────────────────────────────────────────
    // .badge-name: 14px / 800 / -0.02em
    val BadgeName = TextStyle(
        fontFamily = Inter, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-0.02).em,
    )

    // .badge-desc: 11px / 500 / line-height 1.4
    val BadgeDesc = TextStyle(
        fontFamily = Inter, fontSize = 11.sp, fontWeight = FontWeight.Medium,
        lineHeight = 15.4.sp,
    )

    // .badge-earned-tag: 10px / 800 / uppercase / 0.06em
    val BadgeEarnedTag = TextStyle(
        fontFamily = Inter, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold,
        letterSpacing = 0.06.em,
    )

    // .badge-progress-text: 10px / 700 / uppercase / 0.04em
    val BadgeProgressText = TextStyle(
        fontFamily = Inter, fontSize = 10.sp, fontWeight = FontWeight.Bold,
        letterSpacing = 0.04.em,
    )

    // ── School cards ──────────────────────────────────────────────────────────
    // .scf-name: 17px / 800 / -0.02em
    val SchoolName = TextStyle(
        fontFamily = Inter, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-0.02).em,
    )

    // .scf-addr: 13px / 500
    val SchoolAddr = TextStyle(
        fontFamily = Inter, fontSize = 13.sp, fontWeight = FontWeight.Medium,
    )

    // .scf-tag: 11px / 700
    val SchoolTag = TextStyle(
        fontFamily = Inter, fontSize = 11.sp, fontWeight = FontWeight.Bold,
    )

    // .scf-stat-val: 18px / 900 / -0.03em
    val SchoolStatVal = TextStyle(
        fontFamily = Inter, fontSize = 18.sp, fontWeight = FontWeight.Black,
        letterSpacing = (-0.03).em,
    )

    // .scf-stat-label: 10px / 600 / uppercase / 0.04em
    val SchoolStatLabel = TextStyle(
        fontFamily = Inter, fontSize = 10.sp, fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.04.em,
    )

    // .scf-action: 14px / 700
    val SchoolAction = TextStyle(
        fontFamily = Inter, fontSize = 14.sp, fontWeight = FontWeight.Bold,
    )

    // .scf-rating: 13px / 800
    val SchoolRating = TextStyle(
        fontFamily = Inter, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold,
    )

    // .school-option-name: 15px / 800
    val SchoolOptionName = TextStyle(
        fontFamily = Inter, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold,
    )

    // .school-option-meta: 12px / 500
    val SchoolOptionMeta = TextStyle(
        fontFamily = Inter, fontSize = 12.sp, fontWeight = FontWeight.Medium,
    )

    // .school-option-logo: 16px / 900
    val SchoolOptionLogo = TextStyle(
        fontFamily = Inter, fontSize = 16.sp, fontWeight = FontWeight.Black,
    )

    // ── Account rows ──────────────────────────────────────────────────────────
    // .ar-label: 15px / 600
    val AccountLabel = TextStyle(
        fontFamily = Inter, fontSize = 15.sp, fontWeight = FontWeight.SemiBold,
    )

    // ── Role tiles ────────────────────────────────────────────────────────────
    // .role-tile-name: 17px / 800 / -0.02em
    val RoleTileName = TextStyle(
        fontFamily = Inter, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-0.02).em,
    )

    // .role-tile-desc: 13px / 500 / line-height 1.4
    val RoleTileDesc = TextStyle(
        fontFamily = Inter, fontSize = 13.sp, fontWeight = FontWeight.Medium,
        lineHeight = 18.2.sp,
    )

    // ── Trust badges ──────────────────────────────────────────────────────────
    // .trust-badge: 12px / 600
    val TrustBadge = TextStyle(
        fontFamily = Inter, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
    )

    // ── Login ─────────────────────────────────────────────────────────────────
    // .login-hero-title: 26px / 900 / -0.04em / white
    val LoginHeroTitle = TextStyle(
        fontFamily = Inter, fontSize = 26.sp, fontWeight = FontWeight.Black,
        letterSpacing = (-0.04).em,
    )

    // .login-hero-sub: 14px / 500 / rgba(255,255,255,0.7)
    val LoginHeroSub = TextStyle(
        fontFamily = Inter, fontSize = 14.sp, fontWeight = FontWeight.Medium,
    )

    // .login-hero-badge: 11px / 700 / uppercase / 0.06em
    val LoginHeroBadge = TextStyle(
        fontFamily = Inter, fontSize = 11.sp, fontWeight = FontWeight.Bold,
        letterSpacing = 0.06.em,
    )

    // .login-footer-text: 14px / 500
    val LoginFooter = TextStyle(
        fontFamily = Inter, fontSize = 14.sp, fontWeight = FontWeight.Medium,
    )

    // ── Onboarding ────────────────────────────────────────────────────────────
    // .onboard-title: 26px / 900 / -0.04em
    val OnboardTitle = TextStyle(
        fontFamily = Inter, fontSize = 26.sp, fontWeight = FontWeight.Black,
        letterSpacing = (-0.04).em,
    )

    // .onboard-desc: 15px / 500 / line-height 1.6
    val OnboardDesc = TextStyle(
        fontFamily = Inter, fontSize = 15.sp, fontWeight = FontWeight.Medium,
        lineHeight = 24.sp,
    )

    // ── Buttons ───────────────────────────────────────────────────────────────
    // .btn-primary / .btn-secondary: 15px / 700
    val ButtonPrimary = TextStyle(
        fontFamily = Inter, fontSize = 15.sp, fontWeight = FontWeight.Bold,
    )

    // .btn-text: 14px / 600
    val ButtonText = TextStyle(
        fontFamily = Inter, fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
    )

    // .social-btn: 14px / 600
    val SocialButton = TextStyle(
        fontFamily = Inter, fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
    )

    // .signup-type-btn: 14px / 700
    val SignupTypeBtn = TextStyle(
        fontFamily = Inter, fontSize = 14.sp, fontWeight = FontWeight.Bold,
    )

    // ── Form ──────────────────────────────────────────────────────────────────
    // .form-label (parent-portal): 13px / 600
    val FormLabelPortal = TextStyle(
        fontFamily = Inter, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
    )

    // .form-label (auth-flow): 12px / 600 / uppercase / 0.04em
    val FormLabelAuth = TextStyle(
        fontFamily = Inter, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.04.em,
    )

    // .form-input: 15px / 500
    val FormInput = TextStyle(
        fontFamily = Inter, fontSize = 15.sp, fontWeight = FontWeight.Medium,
    )

    // .search-field input: 16px / 500 (parent-portal) / 15px / 500 (auth-flow)
    val SearchInput = TextStyle(
        fontFamily = Inter, fontSize = 16.sp, fontWeight = FontWeight.Medium,
    )

    val SearchInputAuth = TextStyle(
        fontFamily = Inter, fontSize = 15.sp, fontWeight = FontWeight.Medium,
    )

    // ── Divider ───────────────────────────────────────────────────────────────
    // .divider span: 12px / 600 / uppercase / 0.06em
    val DividerLabel = TextStyle(
        fontFamily = Inter, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.06.em,
    )

    // ── Chat ──────────────────────────────────────────────────────────────────
    // .chat-text: 14px / 500 / line-height 1.5
    val ChatText = TextStyle(
        fontFamily = Inter, fontSize = 14.sp, fontWeight = FontWeight.Medium,
        lineHeight = 21.sp,
    )

    // .chat-time: 11px / 500
    val ChatTime = TextStyle(
        fontFamily = Inter, fontSize = 11.sp, fontWeight = FontWeight.Medium,
    )

    // ── Child link ────────────────────────────────────────────────────────────
    // .child-name: 17px / 800
    val ChildName = TextStyle(
        fontFamily = Inter, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold,
    )

    // .child-meta: 13px / 500
    val ChildMeta = TextStyle(
        fontFamily = Inter, fontSize = 13.sp, fontWeight = FontWeight.Medium,
    )

    // .linked-badge: 12px / 700
    val LinkedBadge = TextStyle(
        fontFamily = Inter, fontSize = 12.sp, fontWeight = FontWeight.Bold,
    )

    // ── Action cards (academics) ──────────────────────────────────────────────
    // .ac-title: 14px / 800
    val ActionCardTitle = TextStyle(
        fontFamily = Inter, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold,
    )

    // .ac-sub: 11px / 500
    val ActionCardSub = TextStyle(
        fontFamily = Inter, fontSize = 11.sp, fontWeight = FontWeight.Medium,
    )

    // ── Generic body text ─────────────────────────────────────────────────────
    // Used for various 14px / 500 / on-surface-variant / line-height 1.6
    val BodyMedium = TextStyle(
        fontFamily = Inter, fontSize = 14.sp, fontWeight = FontWeight.Medium,
        lineHeight = 22.4.sp,
    )

    // 15px / 500 / on-surface-variant / line-height 1.5 (page descriptions)
    val BodyLarge = TextStyle(
        fontFamily = Inter, fontSize = 15.sp, fontWeight = FontWeight.Medium,
        lineHeight = 22.5.sp,
    )

    // ── Landing terms ─────────────────────────────────────────────────────────
    // .landing-terms: 12px / 500 / line-height 1.5
    val LandingTerms = TextStyle(
        fontFamily = Inter, fontSize = 12.sp, fontWeight = FontWeight.Medium,
        lineHeight = 18.sp,
    )
}
