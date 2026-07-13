# Vidya Prayag — Comprehensive UI/UX Redesign Specification

> **Document type:** Design audit + redesign specification  
> **Status:** For engineering & design review  
> **Date:** June 2026  
> **Scope:** Full `ui/v2/` design system, all screens, all components, navigation, theming, motion, and UX patterns

---

## Table of Contents

1. [Executive Summary](#1-executive-summary)
2. [Existing Product Analysis](#2-existing-product-analysis)
3. [Screen-by-Screen Audit](#3-screen-by-screen-audit)
4. [Component Audit](#4-component-audit)
5. [Design Debt Report](#5-design-debt-report)
6. [Premium Design Vision](#6-premium-design-vision)
7. [Screen Redesign Recommendations](#7-screen-redesign-recommendations)
8. [Component Design Guidelines](#8-component-design-guidelines)
9. [Motion & Animation Guidelines](#9-motion--animation-guidelines)
10. [UX Recommendations](#10-ux-recommendations)
11. [Design System Specification](#11-design-system-specification)
12. [Prioritized Implementation Roadmap](#12-prioritized-implementation-roadmap)

---

## 1. Executive Summary

Vidya Prayag is a Kotlin Multiplatform (KMP) school management application targeting Android, iOS, Web (Wasm/JS), and Desktop. The UI layer is built entirely with Compose Multiplatform, following a Clean Architecture + MVVM pattern. The design system — codified as the `VTheme` token family (`VColors`, `VType`, `VDimens`, `VMotion`, `VElevation`) — is a mature, fully token-driven system translated from a React/Tailwind reference (`primitives.tsx`).

The app serves three distinct user roles — **School Admin**, **Teacher**, and **Parent** — each with its own portal, tab structure, and deep navigation tree. A state-driven navigation graph (`NavGraphV2`) routes users through an unauthenticated funnel (landing → auth → discovery/onboarding) and a post-login gate (child-link / onboarding / first-login) before handing control to the role-specific portal.

### Current Strengths

- **Token-driven design system** with zero hardcoded colors in production screens — all colors flow through `VTheme.colors`
- **Three-tier elevation system** (`VElevation`) with navy-tinted multi-layer shadows drawn in pure Canvas (platform-identical render)
- **Comprehensive component library** (23 component files) covering buttons, cards, inputs, navigation, charts, progress, avatars, badges, date/time pickers, snackbar, shimmer, and more
- **Consistent state management** via `VStateHost` (Loading → Error → Empty → Content) with skeleton crossfade
- **Spring-based motion system** (`VMotion`) with portal-aware transitions (forwardSlide, modalRise, quietFade)
- **Multi-theme support** (Light, Dark, Warm, High Contrast) with runtime school branding injection
- **Accessibility features**: font scaling, WCAG contrast utilities, high-contrast palette, system bar adaptation

### Key Opportunities

- **Visual hierarchy inconsistency** across portals — admin home is a 14-section mega-scroll while teacher/parent homes are more focused
- **Component proliferation** — multiple bottom nav implementations (`VBottomNav`, `VBottomNav2`, `VBottomNav4`) and dual design vocabularies (`VTheme` vs `Enroll` tokens)
- **Missing motion in many screens** — the motion system exists but many screens use plain `Column`/`verticalScroll` without staggered entrance
- **No bottom sheet component** — overlays are full-screen pushes; the design would benefit from a proper bottom sheet primitive
- **Limited form components** — no multi-line text area, no select/dropdown, no switch/toggle, no slider
- **Chart limitations** — no line chart, no grouped bar chart, no tooltip/interaction
- **QR code generator is a placeholder** — uses a simplified encoder that produces non-scannable codes for data > 14 bytes

---

## 2. Existing Product Analysis

### 2.1 Architecture Overview

```
┌─────────────────────────────────────────────────────────┐
│                    composeApp (UI)                       │
│  ┌─────────────┐  ┌──────────────┐  ┌────────────────┐  │
│  │  theme/ (11) │  │ components/  │  │  screens/ (118)│  │
│  │  VColors     │  │  (23 files)  │  │  auth/ (10)    │  │
│  │  VType       │  │  VButton     │  │  parent/ (32)  │  │
│  │  VDimens     │  │  VCard       │  │  school/ (49)  │  │
│  │  VMotion     │  │  VInput      │  │  teacher/ (25) │  │
│  │  VElevation  │  │  VNavigation │  │  discovery/ (3)│  │
│  │  VTheme      │  │  VCharts     │  │  notifications/│  │
│  │  EnrollTokens│  │  ...         │  │  + Shared.kt   │  │
│  └─────────────┘  └──────────────┘  └────────────────┘  │
│                    navigation/                           │
│                    NavGraphV2.kt                         │
├─────────────────────────────────────────────────────────┤
│                    shared (Domain/Data)                  │
│  feature/ (17 verticals) · di/Koin.kt · core/ · util/   │
├─────────────────────────────────────────────────────────┤
│                    server (Ktor Backend)                 │
│  Application.kt · Tables.kt · Routes · core/            │
└─────────────────────────────────────────────────────────┘
```

### 2.2 Navigation Flow

```
App.kt
  └─ SplashScreen
       ├─ valid session → NavGraphV2 (AuthedFlow)
       │    ├─ SchoolAdmin → SchoolPortalV2 (5 tabs: Home · People · Records · Comms · Settings)
       │    ├─ Teacher → TeacherPortalV2 (4 tabs: Home · Update · Classes · Profile)
       │    ├─ Parent → ParentPortalV2 (5 tabs: Home · Academics · Fees · Conversations · Profile)
       │    └─ Gate: Onboarding / FirstLogin / LinkChild
       │
       └─ no session → NavGraphV2 (UnauthFlow)
            └─ CommonLandingScreenV3
                 ├─ "I'm a Parent" → ParentAuthScreenV2 (OTP)
                 ├─ "School / Admin" → AdminAuthScreenV2 (credentials)
                 ├─ Discovery → DiscoveryScreenV2 → ParentLinkChildScreenV2
                 └─ Legal → LegalInfoScreenV2
```

**Navigation characteristics:**
- State-driven (no Jetpack `NavHost`) — small enum state machines own routing
- `AnimatedContent` with `VMotion` transitions between states
- Deep-link parsing from notification paths (`/parent/home/messages`, `/teacher/report-review?className=8&section=A`)
- Back-press handling via `BackHandler` — collapses funnel toward root, never exits to splash
- Each portal manages its own tab state + overlay stack (full-screen pushes above tab content)

### 2.3 Theme System

**File:** `composeApp/.../ui/v2/theme/` (11 files)

| File | Purpose |
|------|---------|
| `VColors.kt` | Authoritative color tokens: brand families (teal, navy, lavender/accent), ink scale (ink/ink2/ink3), surfaces (background, card, cream), semantic (success/warning/danger), dark mode variants, WCAG contrast utilities, high-contrast palette |
| `VType.kt` | Typography scale: Plus Jakarta Sans (UI) + DM Mono (data/numbers). Styles: h1 (32/800), h2 (22/700), h3 (17/700), h4 (14/600), body (14/400), bodyStrong (14/600), caption (12/500), label (11/700 uppercase 0.10em), labelStrong, inputLabel (12/600), dataLg (22/500 tnum), data (15/400 tnum), dataSm (13/400 tnum). Font scaling support. |
| `VDimens.kt` | Spacing scale (base-4): xs=4, sm=8, md=12, lg=16, xl=20, xxl=24, xxxl=32. Screen padding=16. Radii: radiusSm=6, radiusInput=12, radiusCard=16, radiusSheet=24, radiusXl=20, radiusPill=999. Max content width=440dp. Shape helpers. |
| `VMotion.kt` | Spring configs: soft (dampingRatio=0.8, stiffness=medium), sheet (0.85, low), card (0.75, mediumLow), snappy (0.6, medium). Entrance: fadeUp (300ms + 8dp slide). Screen transitions: forwardSlide (horizontal 30dp + fade), modalRise (vertical 24dp + fade), quietFade (300ms crossfade). |
| `VElevation.kt` | 3-tier navy-tinted shadow system: Card (2dp dy, 4dp spread, 6% alpha), Raised (8dp dy, 24dp spread, 9% alpha), Modal (16dp dy, 40dp spread, 15% alpha). Multi-layer Canvas drawBehind with quadratic falloff. Suppressed in dark mode. |
| `VTheme.kt` | Theme provider composable wrapping content in CompositionLocals for colors, typography, dimensions. Bridges to Material3 ColorScheme. Exposes `VTheme` object for access. |
| `VThemeDef.kt` | Data class bundling VColors with metadata (id, displayName, description, icon). VThemeMode enum: System, Light, Dark, Custom. |
| `VThemeRegistry.kt` | Registry of all themes: light, dark, warm, high_contrast. Supports dynamic registration of school-branded themes. resolveSystem(), resolve(), resolveInclusive(). |
| `BrandingColorMapper.kt` | Maps school branding hex colors to VColors token overrides (accent + teal family), preserving ink, surfaces, and semantic colors. |
| `EnrollTokens.kt` | Semantic token bridge for teacher portal — maps "EnrollTheme/indigo" vocabulary onto existing VTheme tokens. Provides Enroll.colors, Enroll.type, Enroll.shape, Enroll.space. No new colors introduced. |
| `VStatusBarAdapter.kt` | expect/actual for platform status bar adaptation (Android: enableEdgeToEdge; iOS/web/desktop: no-op). |

**Color Palette Summary (Light theme):**

| Token Family | Key Tokens | Hex Values |
|-------------|-----------|------------|
| **Teal (brand)** | teal, tealDeep | `#2DD4BF`, `#0D9488` |
| **Navy (brand)** | navy, navyDeep | `#26234D`, `#1A1838` |
| **Accent (lavender)** | accent, accentDeep, accentSoft, accentTint | `#6C5CE0`, `#544AB8`, `#8B7EE8`, `#F4F3FA` |
| **Ink** | ink, ink2, ink3 | `#1A1838`, `#5C5870`, `#9B96B0` |
| **Surfaces** | background, card, cream | `#F8F7FC`, `#FFFFFF`, `#F0EFF5` |
| **Borders** | hairline, border1, border2 | `#E8E6F0`, `#D8D5E5`, `#C5C2D5` |
| **Semantic** | success, warning, danger (+ Inks) | `#10B981`, `#F59E0B`, `#EF4444` |
| **Shadow** | shadowTint | `#26234D` (navy-tinted) |

### 2.4 Portal Structures

#### Parent Portal (5 tabs)
- **Home** — Aurora-washed hero with child identity + journey ring, school-day timeline, live feature cards (attendance, fees, academics, messages, transport, tutor, scholarships, ID card, library, events, PEWS nudge)
- **Academics** — Attendance calendar, report card, marks, syllabus progress
- **Fees** — Outstanding summary, payment history, fee notices
- **Conversations** — Message threads with teachers/admins (WhatsApp-style)
- **Profile** — Child profile card (collectible), settings, theme picker, logout

#### School Admin Portal (5 tabs)
- **Home** — 14-section command center: greeting, smart insights carousel, school pulse gauge, KPI grid, campus health analytics, fee collection bars, parent engagement leaderboard, communication center, event dashboard, teacher spotlight, student achievements, birthdays, live activity feed, analytics entry cards
- **People** — Student roster, teacher assignments, staff directory, link requests, admissions CRM
- **Records** — Classes/subjects, class performance, teacher performance, daily attendance, academic calendar, results publish, PEWS cohort/student detail, health records, alumni, scholarships, ID cards
- **Comms** — Messages, scheduled messages, school comms, leave requests, event registration
- **Settings** — School profile, branding kit, transport management, school day config, academic year management, analytics dashboard

#### Teacher Portal (4 tabs)
- **Home** — Greeting hero with check-in ring, attendance summary (swipe-expand), today's schedule, reminders/obligations, PEWS alerts, health alerts, digital ID card, report review queue, scheduled messages, events
- **Update** — Write plane: attendance, marks, syllabus, homework (with class/section/subject scope gate)
- **Classes** — Class list → composite class detail → scoped student profile drill-down
- **Profile** — Identity, leave apply/status, password change, theme switch, logout

---

## 3. Screen-by-Screen Audit

### 3.1 Authentication Screens

#### CommonLandingScreenV3 (1,295 lines)
- **Purpose:** Dual-audience landing page for parents and school administrators
- **Layout:** Vertical scroll with animated hero, ecosystem domain cards (school/parent), school-day timeline, trust metrics, featured institutions, role-entry CTAs, legal footer
- **Visual hierarchy:** Strong — animated brand logo, gradient hero, domain cards with icons + metrics, timeline with time markers
- **Interactions:** "I'm a Parent" → OTP auth; "School / Administration" → credential auth; featured school tap → parent auth; footer links → legal
- **Strengths:** Rich, content-filled landing that conveys premium quality; dual-audience handling; animated ecosystem sections
- **Weaknesses:** 1,295 lines in a single file — difficult to maintain; no lazy loading for featured institutions; animation density may impact low-end devices
- **Opportunities:** Split into smaller composables; add lazy column for featured schools; consider a simplified variant for returning users

#### AdminAuthScreenV2 (17,588 bytes)
- **Purpose:** Credential-based login for school administrators
- **Layout:** Auth scaffold with email/password inputs, login button, forgot password link
- **Strengths:** Uses VInput with custom focus treatment; VButton with loading state
- **Weaknesses:** Standard form — no visual distinction from parent auth beyond copy
- **Opportunities:** Add school branding preview; consider biometric auth for returning admins

#### ParentAuthScreenV2 (6,798 bytes)
- **Purpose:** OTP-based phone authentication for parents
- **Layout:** Phone number input → OTP verification flow
- **Strengths:** Clean, focused flow; VInput with phone keyboard type
- **Opportunities:** Add phone country code picker; auto-fill OTP from SMS

#### SchoolOnboardingScreenV2 (57,094 bytes)
- **Purpose:** Multi-step school onboarding wizard for new admins
- **Layout:** Step-by-step wizard with progress indicator
- **Strengths:** Server-driven step resume; comprehensive data collection
- **Weaknesses:** Very large file; potentially overwhelming for new users
- **Opportunities:** Break into step-specific composables; add skip-for-now on optional steps

#### ParentLinkChildScreenV2 (22,514 bytes)
- **Purpose:** 3-step child linking wizard (parent name → school search → child details)
- **Strengths:** Auto-correction on class/section input; school search with live results
- **Opportunities:** Add QR code scanning for instant school linking

#### TeacherFirstLoginScreenV2 (7,670 bytes)
- **Purpose:** First-login password change gate for teachers
- **Strengths:** Enforced security gate; clean form
- **Opportunities:** Add password strength indicator; biometric enrollment

#### SplashScreenV2 (9,539 bytes)
- **Purpose:** App entry point with brand logo animation
- **Strengths:** VBrandLogo glass cube; animated entrance

#### LegalInfoScreenV2 (15,155 bytes)
- **Purpose:** Privacy policy, terms of service, help desk
- **Strengths:** Honest, minimal copy; support email link

### 3.2 Parent Screens

#### ParentHomeScreenV2 (840 lines)
- **Purpose:** Parent dashboard — the flagship screen
- **Layout:** Aurora-washed hero (child identity + journey ring), school-day timeline rail, live feature cards carousel
- **Design law:** "NEVER COLLAPSE TO WHITE SPACE" — every card renders a rich state even with sparse data
- **Strengths:** Rich visual hierarchy; live clock refresh; PEWS nudge integration; permission rationale handling; aurora wash background
- **Weaknesses:** 840 lines; some cards duplicate functionality available in tab destinations
- **Opportunities:** Extract hero, timeline, and card sections into separate composables; add staggered entrance animation

#### ParentAcademicsScreenV2 (26,712 bytes)
- **Purpose:** Academic progress — attendance, marks, syllabus, report card
- **Strengths:** VProgressRing for attendance; VBars for marks trend; VDonut for subject breakdown

#### ParentFeesScreenV2 (9,688 bytes)
- **Purpose:** Fee management — outstanding, history, notices
- **Strengths:** VProgressBar for collection; clean summary cards

#### ParentMessagesScreenV2 (29,708 bytes)
- **Purpose:** WhatsApp-style messaging with teachers/admins
- **Strengths:** Conversation list + thread view; dock hiding on conversation open
- **Opportunities:** Add typing indicator; message search; voice messages

#### ParentProfileScreenV2 (11,242 bytes) / ParentProfileCardScreenV2 (47,501 bytes)
- **Purpose:** Profile + collectible child profile card
- **Strengths:** Collectible card concept is unique and premium; rich child data display

#### ParentAttendanceCard (25,587 bytes) / ParentAttendanceCalendar (11,806 bytes)
- **Purpose:** Attendance visualization with calendar grid
- **Strengths:** Calendar grid with color-coded days; monthly summary

#### ParentScheduleCard (29,959 bytes)
- **Purpose:** Today's timetable display
- **Strengths:** Timeline rail with period cards

#### Other Parent Screens
- **ParentLeaveScreenV2** — Leave application form with VDatePicker
- **ParentHealthScreenV2** — Health records and alerts
- **ParentLibraryScreenV2** — Book catalog, issued/returned tracking
- **ParentPewsScreenV2** — Early warning system parent view
- **ParentPulseScreen** — Engagement/pulse metrics
- **ParentEventRegistrationScreenV2** — Event registration flow
- **BusTrackingScreenV2** — Live bus tracking
- **DigitalIdCardScreen** — Digital ID with QR code
- **ScholarshipWorkflowScreenV2** / **ScholarshipsScreenV2** — Scholarship discovery and application
- **ParentUnlinkedScreenV2** — Onboarding for unlinked parents
- **ParentConversationsScreenV2** — Conversation list
- **ParentActivityScreenV2** — Activity feed

### 3.3 School Admin Screens

#### SchoolHomeScreenV2 (1,748 lines)
- **Purpose:** Admin command center — the most complex screen in the app
- **Layout:** 14 sections in a single vertical scroll: greeting header, smart insights carousel, school pulse gauge, KPI grid, campus health, fee collection, parent engagement, communication center, event dashboard, teacher spotlight, student achievements, birthdays, live activity feed, analytics entry
- **Strengths:** Data-driven from real API; skeleton loading via VStateHost; gradient accents; animated gauges
- **Weaknesses:** 1,748 lines — extreme file size; 14 sections in one scroll is overwhelming; no tab/section navigation within the home; some sections may be empty for small schools
- **Opportunities:** Split into modular composables; add a customizable widget grid; consider collapsible sections; implement lazy loading

#### SchoolPortalV2 (593 lines)
- **Purpose:** 5-tab admin shell with 30+ overlay routes
- **Strengths:** Comprehensive overlay system; deep-link routing; badge counts from real data

#### SchoolPeopleScreenV2 (57,260 bytes)
- **Purpose:** Student/teacher/staff directory
- **Strengths:** Search, filter, role-based views

#### ClassesSubjectsScreenV2 (119,534 bytes)
- **Purpose:** Class and subject management — the largest file in the codebase
- **Weaknesses:** Extreme file size; likely contains multiple screens' worth of logic
- **Opportunities:** Critical need to decompose into separate files

#### SchoolRecordsScreenV2 (24,782 bytes)
- **Purpose:** Records hub — attendance, performance, calendar, results, PEWS, health, alumni, scholarships, ID cards

#### SchoolSettingsScreenV2 (20,947 bytes)
- **Purpose:** Settings hub — profile, branding, transport, school day, academic year, analytics

#### Other Notable Admin Screens
- **StudentProfileScreenV2** (31,103 bytes) — Rich student detail with academic/attendance/health tabs
- **TeacherProfileScreenV2** (26,412 bytes) — Teacher detail with assignments, performance
- **PewsCohortScreenV2** (27,193 bytes) / **PewsStudentDetailScreenV2** (27,115 bytes) — Early warning system
- **BrandingSettingsScreen** (22,937 bytes) — School branding customization
- **AcademicCalendarPlatformScreenV2** (26,015 bytes) — Premium calendar platform
- **AdminEventRegistrationScreenV2** (26,173 bytes) — Event management
- **ScholarshipManagementScreenV2** (37,858 bytes) — Scholarship administration
- **SchoolLibraryScreen** (90,285 bytes) — Library management
- **HealthRecordsScreenV2** (24,418 bytes) — Health records management
- **AlumniScreen** (27,922 bytes) / **AlumniDetailScreen** (10,975 bytes) — Alumni management
- **TransportManagementScreenV2** (22,312 bytes) — Transport management
- **AnalyticsDashboardScreenV2** (9,429 bytes) — Analytics overview
- **LinkRequestsScreenV2** (9,727 bytes) — Parent-child link approval queue
- **MessagesScreenV2** (27,364 bytes) — Admin messaging
- **ScheduledMessagesScreenV2** (9,061 bytes) — Scheduled message management

### 3.4 Teacher Screens

#### TeacherHomeScreenV2 (740 lines)
- **Purpose:** Teacher dashboard with check-in, attendance, schedule, reminders
- **Layout:** Greeting hero with check-in ring, swipe-expand attendance summary, schedule card, reminders card, action cards (PEWS, health, ID, report review, messages, events)
- **Strengths:** Swipe-to-expand card pattern; biometric check-in; live clock refresh; obligations-driven reminders
- **Opportunities:** Staggered entrance; extract sub-composables

#### TeacherClassesScreenV2 (28,227 bytes)
- **Purpose:** Class list → class detail → student profile drill-down
- **Strengths:** Self-contained navigation within tab; composite detail view

#### TeacherMarksScreenV2 (22,387 bytes)
- **Purpose:** Marks entry with scope gate
- **Strengths:** Scope selector (class/section/subject); grid entry

#### TeacherHomeworkScreenV2 (19,617 bytes)
- **Purpose:** Homework assignment creation and tracking

#### TeacherLessonPlanScreenV2 (43,120 bytes)
- **Purpose:** Lesson planning with syllabus linkage
- **Weaknesses:** Large file; complex form

#### TeacherTimetableScreenV2 (18,825 bytes)
- **Purpose:** Weekly timetable view

#### TeacherPewsScreenV2 (21,769 bytes)
- **Purpose:** Early warning system for teachers

#### TeacherMessagesScreenV2 (21,024 bytes)
- **Purpose:** Teacher messaging

#### TeacherProfileScreenV2 (24,600 bytes)
- **Purpose:** Teacher profile, leave, settings

#### Other Teacher Screens
- **TeacherAttendanceScreenV2** — Attendance marking
- **TeacherSyllabusScreenV2** — Syllabus tracking
- **TeacherUpdateScreenV2** — Update hub (attendance/marks/syllabus/homework)
- **TeacherScopeSelector** — Class/section/subject picker
- **TeacherCheckInPopup** — Biometric check-in popup
- **TeacherDialogs** — Reusable teacher dialogs
- **TeacherKit** — Teacher utility components
- **TeacherHealthAlertsScreenV2** — Health alert management
- **TeacherReportDraftEditorScreen** — AI report card draft editing
- **TeacherReportReviewQueueScreen** — Report review queue
- **TeacherStudentProfileScreenV2** — Student profile (teacher view)
- **TeacherPtmEventRegistrationScreenV2** — PTM event registration
- **TransportAttendanceScreenV2** — Transport attendance

### 3.5 Discovery Screens

#### DiscoveryScreenV2 (39,760 bytes)
- **Purpose:** School marketplace/discovery for new parents
- **Strengths:** Rich school cards; search and filter

#### AcademicCalendarScreenV2 (12,848 bytes)
- **Purpose:** Shared academic calendar view
- **Strengths:** Month grid with event indicators; event list

### 3.6 Notifications Screen
- **NotificationsScreenV2** — Shared notification list used across all portals

---

## 4. Component Audit

### 4.1 Component Inventory (23 files)

| Component | File | Lines | Variants | Status |
|-----------|------|-------|----------|--------|
| **VButton** | VButton.kt | 359 | 4 variants × 8 tones × 3 sizes | ✅ Mature |
| **VCard** | VCard.kt | 216 | VCard + VActionCard | ✅ Mature |
| **VInput** | VInput.kt | 134 | Single-line text/password | ⚠️ No multiline |
| **VNavigation** | VNavigation.kt | 944 | VTopTabs, VBottomNav, VBottomNav2, VBottomNav4, VBackHeader | ⚠️ Proliferation |
| **VStructure** | VStructure.kt | 252 | VScreenScaffold, VEmptyState, VComingSoon, VConfirmDialog | ✅ Mature |
| **VAtoms** | VAtoms.kt | 93 | VDivider, VLabel, VStatusDot | ✅ Mature |
| **VBadge** | VBadge.kt | 139 | VBadge (6 tones) + VTag | ✅ Mature |
| **VAvatar** | VAvatar.kt | 160 | Circular with image/initials/shimmer | ✅ Mature |
| **VShimmer** | VShimmer.kt | 86 | ShimmerBox | ✅ Mature |
| **VProgress** | VProgress.kt | 109 | VProgressBar + VProgressRing | ✅ Mature |
| **VCharts** | VCharts.kt | 244 | VDonut, VSparkline, VBars, VLegendDot | ✅ Good |
| **VIcons** | VIcons.kt | 458 | 40+ icons (Material + hand-authored) | ✅ Mature |
| **VSnackbar** | VSnackbar.kt | 100 | 4 tones (Success/Error/Info/Warning) | ✅ Good |
| **VPullRefresh** | VPullRefresh.kt | 58 | PullToRefreshBox wrapper | ✅ Good |
| **VDatePicker** | VDatePicker.kt | 234 | Calendar dialog | ✅ Good |
| **VTimePicker** | VTimePicker.kt | 138 | Dropdown hour:minute | ✅ Good |
| **VScheduleToggle** | VScheduleToggle.kt | 133 | Publish now/schedule later | ✅ Good |
| **VThemePicker** | VThemePicker.kt | 150 | System + registered themes | ✅ Good |
| **VLogo** | VLogo.kt | 115 | Bridge mark (Canvas) | ✅ Mature |
| **VBrandLogo** | VBrandLogo.kt | 100 | Glass cube logo plate | ✅ Mature |
| **QrCodeImage** | QrCodeImage.kt | 138 | Canvas QR rendering | ⚠️ Placeholder encoder |
| **SectionHeader** | SectionHeader.kt | 76 | String-based (Enroll tokens) | ⚠️ Dual vocabulary |
| **EnrollCard** | EnrollCard.kt | 107 | Flat border card (Enroll tokens) | ⚠️ Dual vocabulary |

### 4.2 Shared Screen Infrastructure

| Helper | File | Purpose |
|--------|------|---------|
| **VStateHost** | Shared.kt | Loading/Error/Empty/Content phase host with crossfade |
| **VLoadingState** | Shared.kt | Centered CircularProgressIndicator |
| **VErrorState** | Shared.kt | Error message + retry button via VEmptyState |
| **VSectionHeader** | Shared.kt | ALL-CAPS section header with composable action slot |
| **VPortalHeader** | Shared.kt | Avatar + name + subtitle greeting bar |
| **SkeletonList** | Skeletons.kt | Avatar + 2-line shimmer rows |
| **SkeletonDashboard** | Skeletons.kt | Greeting + hero + 2-up grid + list shimmer |
| **SkeletonProfile** | Skeletons.kt | Centered avatar + name + detail rows shimmer |
| **SkeletonCalendar** | Skeletons.kt | Month header + chip strip + event rows shimmer |
| **SkeletonFee** | Skeletons.kt | Summary hero + breakdown cards + rows shimmer |
| **SkeletonAnnouncements** | Skeletons.kt | Filter chips + announcement cards shimmer |

### 4.3 Component Analysis

#### VButton — Universal Action Button
- **Variants:** Primary (filled), Secondary (outlined), Ghost (text-only), Destructive (red filled)
- **Tones:** Navy, Teal, Sky, Peach, Lavender, Sand, Rose, Mint (8 color tones)
- **Sizes:** Sm (36dp), Md (44dp), Lg (52dp)
- **States:** Idle → Loading (spinner) → Success (checkmark animation)
- **Premium detail:** Diagonal light sweep animation on press for filled primary buttons
- **Design debt:** No icon-only variant; no trailing icon support; no badge support

#### VCard — Universal Elevated Surface
- **Properties:** Hairline border + soft navy-tinted shadow (VElevation.Card); suppressed in dark mode
- **Variants:** VCard (elevated) + VActionCard (icon + title + subtitle + arrow row)
- **Design debt:** No swipe-to-dismiss; no long-press menu; no drag-and-drop

#### VInput — Text Field
- **Built on:** BasicTextField (not Material3 TextField) for full control
- **Focus treatment:** Border → tealDeep, background → card, 4dp teal glow ring
- **Features:** Leading icon, trailing composable, password masking with visibility toggle
- **Design debt:** No multiline/textarea; no character count; no error state with message; no prefix/suffix

#### VNavigation — Tab & Bottom Navigation
- **VTopTabs:** Horizontally scrollable pill-style tab bar with colored bg + scale animation
- **VBottomNav2:** Sticky bottom nav with animated pill indicator (spring physics), haptic feedback, badge counts
- **VBottomNav4:** Variant with pill background and animated transitions
- **VBackHeader:** Top app bar with circular back button + optional trailing action
- **Design debt:** THREE bottom nav implementations (VBottomNav, VBottomNav2, VBottomNav4) — needs consolidation; no top app bar with search; no collapsible header

#### VCharts — Pure Canvas Charts
- **VDonut:** Animated multi-segment ring with center slot; 800ms sweep animation
- **VSparkline:** Filled area + line micro chart with end dot; 1100ms reveal animation
- **VBars:** Vertical bars with last bar highlighted; 600ms grow animation
- **VLegendDot:** Color swatch + label + optional value
- **Design debt:** No line chart with axes; no grouped/stacked bars; no tooltip/interaction; no pie chart; no area chart with gradient

#### VStructure — Screen Infrastructure
- **VScreenScaffold:** Root layout — max 440dp width, portal background, optional top/bottom bars, auto-pads for floating bottom nav (112dp + nav insets)
- **VEmptyState:** Centered icon-in-circle + title + body + optional action
- **VComingSoon:** "PREVIEW" badge card for not-yet-shipped features
- **VConfirmDialog:** Universal confirmation gate for destructive actions (RA-21)
- **Design debt:** No bottom sheet; no full-screen modal; no side drawer; no segmented control

---

## 5. Design Debt Report

### 5.1 Critical Debt

| # | Issue | Impact | Files Affected |
|---|-------|--------|---------------|
| D-01 | **Bottom nav proliferation** — 3 implementations (VBottomNav, VBottomNav2, VBottomNav4) | Maintenance burden, inconsistent behavior | VNavigation.kt, SchoolPortalV2.kt, ParentPortalV2.kt |
| D-02 | **Dual design vocabulary** — `VTheme` vs `Enroll` tokens | Confusion about which to use; SectionHeader vs VSectionHeader; EnrollCard vs VCard | EnrollTokens.kt, SectionHeader.kt, EnrollCard.kt |
| D-03 | **Mega-files** — ClassesSubjectsScreenV2 (119KB), SchoolLibraryScreen (90KB), SchoolHomeScreenV2 (1,748 lines) | Unmaintainable, slow IDE, merge conflicts | 5+ files |
| D-04 | **QR code placeholder** — simplified encoder produces non-scannable codes for data > 14 bytes | Digital ID card feature broken for real use | QrCodeImage.kt |
| D-05 | **No bottom sheet component** — all overlays are full-screen pushes | Missed UX pattern for quick actions, filters, selections | All portals |

### 5.2 Moderate Debt

| # | Issue | Impact |
|---|-------|--------|
| D-06 | **Missing form components** — no textarea, no select/dropdown, no switch/toggle, no slider, no checkbox | Forms use raw Compose or workarounds |
| D-07 | **No staggered entrance animation** in most screens — VMotion.fadeUp exists but screens use plain Column/verticalScroll | Screens feel static despite motion system existing |
| D-08 | **VLoadingState uses CircularProgressIndicator** — not a VShimmer skeleton by default | Inconsistent with skeleton-first design intent |
| D-09 | **No search component** — screens implement search inline with VInput + manual filtering | No shared search UX pattern |
| D-10 | **No tooltip/coach mark system** — no first-time user guidance | Discovery of features relies on visual hierarchy alone |
| D-11 | **No skeleton for many screens** — only 6 skeleton types exist for 118+ screens | Loading states inconsistent |
| D-12 | **HorizontalDivider used in SchoolHomeScreenV2** — imports `material3.HorizontalDivider` despite VDivider being the standard | Design system violation |
| D-13 | **Hardcoded dp values** in many screens — spacing uses raw `16.dp`, `14.dp` instead of VTheme.dimens | Token bypass reduces consistency |
| D-14 | **No haptic feedback system** — only VBottomNav2 uses haptics; no shared haptic utility | Inconsistent tactile feedback |

### 5.3 Minor Debt

| # | Issue | Impact |
|---|-------|--------|
| D-15 | **VIcons.Users** falls back to Person (no group icon) | Visual ambiguity for "users" vs "user" |
| D-16 | **VIcons.IdCard** falls back to Person | No distinct ID card icon |
| D-17 | **No dark mode screenshots/verification** — dark mode tokens exist but visual QA is manual | Potential contrast issues |
| D-18 | **No animation for tab content transitions** — tab switches swap content instantly | Missed opportunity for premium feel |
| D-19 | **No empty illustration system** — VEmptyState uses icon-in-circle, no illustrated empty states | Less delightful empty states |
| D-20 | **CommonLandingScreenV2 still exists** alongside V3 | Dead code confusion |

---

## 6. Premium Design Vision

### 6.1 Design Principles

1. **Timeless Elegance** — Clean surfaces, generous whitespace, restrained color usage. The design should feel as fresh in 5 years as it does today. Reference: Apple, Linear, Stripe.

2. **Confident Restraint** — One dominant brand accent per portal. Color is meaning, not decoration. Teal for admin, lavender for parent, warm violet for teacher.

3. **Tactile Precision** — Every interaction has a physical response. Press scales, spring physics, haptic confirmation. The UI feels like it has weight and substance.

4. **Layered Depth** — Navy-tinted shadows create hierarchy without harshness. Dark mode is a first-class citizen, not an inversion. Depth is subtle but perceptible.

5. **Data as Design** — Numbers are beautiful. DM Mono with tabular figures. Charts are pure Canvas craft. Data visualization is a feature, not an afterthought.

6. **Motion with Purpose** — Animations guide attention, confirm actions, and smooth transitions. Never decorative. Spring physics over linear easing. 200–400ms is the sweet spot.

7. **Accessible by Default** — Font scaling, high contrast, WCAG-compliant color pairs. Accessibility is not a feature — it's a baseline.

### 6.2 Target Quality Bar

The redesign should position Vidya Prayag at the intersection of:
- **Linear's** minimalism and motion craft
- **Stripe's** data visualization and form design
- **Apple's** material precision and tactile feedback
- **Notion's** information density with breathing room
- **Airbnb's** storytelling and visual hierarchy

### 6.3 Portal Identity

| Portal | Primary Accent | Background Tone | Personality |
|--------|---------------|-----------------|-------------|
| **Parent** | Lavender (`#6C5CE0`) | Soft lavender-white (`#F8F7FC`) | Warm, personal, child-centric |
| **Teacher** | Violet accent (via Enroll bridge) | Lavender canvas | Professional, efficient, focused |
| **Admin** | Teal (`#0D9488`) | Cool portal white | Authoritative, analytical, commanding |

---

## 7. Screen Redesign Recommendations

### 7.1 Priority Screen Redesigns

#### P0: SchoolHomeScreenV2 — Decompose & Modularize
**Current:** 1,748 lines, 14 sections in one scroll  
**Target:** Modular widget system with customizable layout

**Recommendations:**
- Split into 14 independent composables, each in its own file
- Implement a `DashboardWidget` interface with `@Composable render()` — enables future drag-to-reorder
- Add a "Customize Dashboard" mode (like iOS widget editing)
- Default layout: 6 primary widgets visible above the fold; rest in a "More" section
- Add staggered entrance animation (VMotion.fadeUp with 50ms per-widget delay)
- Replace `HorizontalDivider` with `VDivider`
- Add pull-to-refresh via `VPullRefresh`

#### P0: CommonLandingScreenV3 — Performance & Structure
**Current:** 1,295 lines, single file  
**Target:** Split into 8–10 composables with lazy loading

**Recommendations:**
- Extract: HeroSection, EcosystemDomains, SchoolDayTimeline, TrustMetrics, FeaturedInstitutions, RoleEntryCTAs, LegalFooter
- Use `LazyColumn` for the scrollable content (currently `verticalScroll`)
- Add `key()` for each section to preserve scroll position on recomposition
- Reduce animation density on low-end devices (check `LocalAccessibilityManager`)

#### P1: ParentHomeScreenV2 — Refine & Animate
**Current:** 840 lines, rich but monolithic  
**Target:** Modular sections with staggered entrance

**Recommendations:**
- Extract: AuroraHero, JourneyRing, TimelineRail, FeatureCardCarousel, PewNudgeCard
- Add staggered entrance: hero (0ms) → timeline (100ms) → cards (200ms, 50ms stagger)
- Add swipe-to-refresh on the whole dashboard
- Implement a "child switcher" if parent has multiple children (horizontal pager)

#### P1: TeacherHomeScreenV2 — Polish & Expand
**Current:** 740 lines, good structure  
**Target:** Enhanced with staggered entrance and improved card patterns

**Recommendations:**
- Extract: GreetingHeroCard, AttendanceSummaryCard, ScheduleCard, RemindersCard
- Add staggered entrance animation
- Improve swipe-to-expand card with a proper `SwipeableCard` component
- Add a "quick actions" row (mark attendance, assign homework, send message)

#### P2: ClassesSubjectsScreenV2 — Critical Decomposition
**Current:** 119,534 bytes — largest file in codebase  
**Target:** Split into 8–12 focused files

**Recommendations:**
- Extract class list, class detail, subject management, section management, assignment management into separate files
- Consider a master-detail navigation pattern instead of deep scroll

#### P2: SchoolLibraryScreen — Decomposition
**Current:** 90,285 bytes  
**Target:** Split into 6–8 focused files (catalog, issued, reservations, search, book detail, etc.)

### 7.2 Cross-Screen Patterns to Introduce

| Pattern | Description | Priority |
|---------|-------------|----------|
| **Staggered List Entrance** | Items enter with 50ms stagger using VMotion.fadeUp | P0 |
| **Tab Content Transition** | Crossfade (200ms) when switching tabs | P1 |
| **Pull-to-Refresh Everywhere** | All list/dashboard screens wrap content in VPullRefresh | P1 |
| **Smart Empty States** | VEmptyState with contextual illustrations and CTAs | P1 |
| **Inline Search** | Shared VSearchBar component with debounce + clear | P1 |
| **Filter Bottom Sheet** | VBottomSheet with VTag chips for filtering lists | P2 |
| **Swipe Actions** | Swipe-to-delete/archive on list items | P2 |
| **Contextual Toolbars** | Top bar transforms on item selection (bulk actions) | P2 |

---

## 8. Component Design Guidelines

### 8.1 New Components Needed

#### VBottomSheet (P0)
```
VBottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    peekHeight: Dp = 0.dp,
    dragHandle: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
)
```
- Modal bottom sheet with scrim, drag-to-dismiss, spring physics
- Uses VElevationLevel.Modal shadow
- Scrim: `ink.copy(alpha = 0.4f)` with fade-in/out
- Drag handle: 36dp × 4dp pill in `border2` color

#### VSearchBar (P0)
```
VSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search",
    onClear: (() -> Unit)? = null,
)
```
- Built on VInput with Search icon as leading
- Clear (X) button as trailing when text is non-empty
- Subtle card background instead of cream

#### VSelect / VDropdown (P1)
```
VSelect(
    options: List<VSelectOption>,
    selected: String?,
    onSelect: (String) -> Unit,
    label: String?,
    placeholder: String = "Select",
)
```
- Read-only field that opens a VBottomSheet with options
- Supports single and multi-select modes

#### VSwitch (P1)
```
VSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String?,
)
```
- Toggle switch with teal accent when on
- Label beside switch

#### VTextArea (P1)
```
VTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    label: String?,
    placeholder: String?,
    maxLength: Int? = null,
    rows: Int = 3,
)
```
- Multi-line VInput variant
- Optional character count

#### VSegmentedControl (P2)
```
VSegmentedControl(
    segments: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
)
```
- Pill-style segmented control with animated selection indicator
- Uses spring physics for indicator movement

#### VSlider (P2)
```
VSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    range: ClosedFloatingPointRange<Float> = 0f..1f,
    label: String?,
)
```

#### VCheckbox (P2)
```
VCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String?,
)
```

#### VTooltip / VCoachMark (P3)
- First-time user guidance overlay
- Points to UI elements with description text
- Dismissable with tap

### 8.2 Existing Component Improvements

| Component | Improvement | Priority |
|-----------|------------|----------|
| **VButton** | Add icon-only variant; add trailing icon; add badge support | P1 |
| **VInput** | Add error state with message; add character count; add prefix/suffix | P1 |
| **VCard** | Add swipe-to-dismiss variant; add long-press menu | P2 |
| **VNavigation** | Consolidate to single VBottomNav; add search-capable top bar | P0 |
| **VCharts** | Add line chart with axes; add tooltip; add grouped bars | P2 |
| **VEmptyState** | Add illustrated variant with custom Lottie/Canvas animation | P2 |
| **VConfirmDialog** | Add variant with text input (e.g., "Type DELETE to confirm") | P2 |
| **QrCodeImage** | Replace placeholder encoder with real QR library (qrcode-kotlin) | P0 |
| **VAvatar** | Add square variant; add group/stacked variant | P2 |
| **VBadge** | Add dot-only variant (notification indicator) | P3 |

### 8.3 Component Consolidation

**Action:** Consolidate the dual vocabulary (`VTheme` vs `Enroll` tokens)

**Recommendation:**
- **Deprecate** `EnrollTokens.kt`, `SectionHeader.kt`, `EnrollCard.kt`
- **Migrate** all `Enroll.*` references to `VTheme.*` equivalents
- **Enhance** `VSectionHeader` to accept string-based action (matching SectionHeader's contract)
- **Add** flat card variant to `VCard` (matching EnrollCard's contract)
- **Timeline:** P1 — after component improvements, before screen redesigns

**Action:** Consolidate bottom navigation

**Recommendation:**
- **Keep** `VBottomNav2` as the canonical implementation
- **Deprecate** `VBottomNav` and `VBottomNav4`
- **Migrate** `SchoolPortalV2` from `VBottomNav` to `VBottomNav2`
- **Timeline:** P0 — immediate

---

## 9. Motion & Animation Guidelines

### 9.1 Motion Principles

1. **Spring over Tween** — Prefer spring physics for interactive elements; use tween only for one-shot entrances
2. **Subtle Momentum** — Animations should feel like they have mass. Damping ratio 0.6–0.85 for most interactions
3. **Directional Intent** — Forward navigation slides horizontally; modal content rises vertically; tab switches crossfade
4. **Never Block** — Animations run on the render thread; never delay content availability
5. **Respect Reduce Motion** — Check `LocalAccessibilityManager` and disable non-essential animations

### 9.2 Motion Token Usage

| Token | Use Case | Spec |
|-------|---------|------|
| `VMotion.springSoft` | Card press, gentle expansions | dampingRatio=0.8, stiffness=Spring.StiffnessMedium |
| `VMotion.springSheet` | Bottom sheet drag | dampingRatio=0.85, stiffness=Spring.StiffnessLow |
| `VMotion.springCard` | Card swipe, dismiss | dampingRatio=0.75, stiffness=Spring.StiffnessMediumLow |
| `VMotion.springSnappy` | Tab indicator, toggles | dampingRatio=0.6, stiffness=Spring.StiffnessMedium |
| `VMotion.fadeUp` | Item entrance | 300ms tween + 8dp Y slide |
| `VMotion.forwardSlide` | Forward navigation | 300ms horizontal 30dp slide + fade |
| `VMotion.modalRise` | Modal/sheet entrance | 300ms vertical 24dp slide + fade |
| `VMotion.quietFade` | Theme switch, resolving | 300ms crossfade |

### 9.3 Screen-Level Motion

| Pattern | Implementation | Priority |
|---------|---------------|----------|
| **Staggered List Entrance** | `itemsIndexed` with `VMotion.fadeUp` delayed by `index * 50ms` | P0 |
| **Dashboard Widget Entrance** | Each widget enters with 100ms stagger | P0 |
| **Tab Content Crossfade** | `AnimatedContent` with `quietFade()` on tab change | P1 |
| **Pull-to-Refresh Completion** | Content scale 0.98 → 1.0 spring on refresh complete | P1 |
| **Card Press Feedback** | `pressScale(0.98f)` on all clickable cards | P1 |
| **Screen Transition** | `forwardSlide()` for push, reverse for pop | Already implemented |
| **Theme Switch** | 300ms crossfade (already in NavGraphV2) | Already implemented |
| **Loading → Content** | 300ms crossfade via VStateHost | Already implemented |

### 9.4 Micro-Interactions

| Element | Interaction | Animation |
|---------|------------|-----------|
| **VButton** | Press | Scale 0.96 + light sweep (filled primary) |
| **VButton** | Loading → Success | Spinner → Checkmark pop (spring) |
| **VBottomNav** | Tab switch | Pill indicator spring + haptic |
| **VInput** | Focus | Border color tween + glow ring fade-in |
| **VAvatar** | Image load | Shimmer → crossfade to photo (300ms) |
| **VBadge** | Appear | Scale 0.8 → 1.0 spring |
| **VEmptyState** | Appear | Fade + 8dp slide up |
| **VConfirmDialog** | Appear | Scale 0.95 → 1.0 + fade |
| **VSnackbar** | Appear | Slide up from bottom + fade |
| **VProgressRing** | Value change | 700ms tween sweep |
| **VDonut** | First render | 800ms tween segment sweep |
| **VSparkline** | First render | 1100ms tween line reveal |
| **VBars** | First render | 600ms tween grow |

---

## 10. UX Recommendations

### 10.1 Navigation UX

| Recommendation | Rationale | Priority |
|---------------|-----------|----------|
| **Add tab content crossfade** | Currently tab switches are instant — feels jarring | P0 |
| **Add back gesture support** | Ensure swipe-back works on Android | P1 |
| **Add breadcrumb for deep navigation** | Admin portal has 30+ overlay routes — users get lost | P1 |
| **Add "recently visited" shortcuts** | Quick access to frequently used screens | P2 |
| **Add global search** | Search across students, classes, messages, settings | P2 |

### 10.2 Form UX

| Recommendation | Rationale | Priority |
|---------------|-----------|----------|
| **Inline validation** | Validate on blur, show error below field | P0 |
| **Smart defaults** | Pre-fill class/section from teacher's scope | P1 |
| **Auto-save drafts** | Save form state on navigation away | P1 |
| **Progressive disclosure** | Show optional fields in collapsible "more" section | P2 |
| **Bulk actions** | Select multiple items → apply action (attendance, marks) | P2 |

### 10.3 Content UX

| Recommendation | Rationale | Priority |
|---------------|-----------|----------|
| **Smart empty states** | Contextual CTAs in empty states (e.g., "Add your first class") | P0 |
| **Skeleton-first loading** | Every screen should have a skeleton, not a spinner | P0 |
| **Offline indicators** | Show banner when network is unavailable | P1 |
| **Pull-to-refresh everywhere** | All list/dashboard screens | P1 |
| **Infinite scroll for long lists** | Roster, alumni, library — paginate instead of loading all | P1 |
| **Sticky section headers** | In long lists, keep current section label visible | P2 |

### 10.4 Accessibility

| Recommendation | Rationale | Priority |
|---------------|-----------|----------|
| **Screen reader labels** | All interactive elements need contentDescription | P0 |
| **Min touch target 44dp** | Audit all clickable elements for minimum size | P0 |
| **Focus indicators** | Keyboard navigation support (especially web/desktop) | P1 |
| **Reduce motion respect** | Disable non-essential animations when system setting is on | P1 |
| **High contrast mode** | Verify all screens with high_contrast theme | P1 |
| **Dynamic type** | Verify all text scales with font scaling | P1 |

### 10.5 Information Architecture

| Recommendation | Rationale | Priority |
|---------------|-----------|----------|
| **Admin home: customizable widgets** | 14 sections is too many — let users choose | P2 |
| **Teacher: consolidate update tab** | Attendance/marks/syllabus/homework in one tab is cluttered | P2 |
| **Parent: add "quick actions" on home** | One-tap access to most common actions | P2 |
| **Settings: organize by category** | Group settings into categories with icons | P2 |

---

## 11. Design System Specification

### 11.1 Color System

#### Primary Brand Colors

| Token | Light | Dark | Usage |
|-------|-------|------|-------|
| `teal` | `#2DD4BF` | `#14B8A6` | Brand accent (admin) |
| `tealDeep` | `#0D9488` | `#0F766E` | Interactive elements, focus rings |
| `navy` | `#26234D` | `#C5C2D5` | Primary text, dark surfaces |
| `navyDeep` | `#1A1838` | `#E8E6F0` | Headlines, emphasis |
| `accent` | `#6C5CE0` | `#A78BFA` | Portal accent (parent/teacher) |
| `accentDeep` | `#544AB8` | `#8B7EE8` | Interactive accent |
| `accentSoft` | `#8B7EE8` | `#7C6FE0` | Secondary accent |
| `accentTint` | `#F4F3FA` | `#2A2645` | Accent backgrounds, selected states |

#### Ink Scale

| Token | Light | Dark | Usage |
|-------|-------|------|-------|
| `ink` | `#1A1838` | `#F0EFF5` | Primary text |
| `ink2` | `#5C5870` | `#B0ABB8` | Secondary text, labels |
| `ink3` | `#9B96B0` | `#7C7890` | Tertiary text, placeholders |

#### Surfaces

| Token | Light | Dark | Usage |
|-------|-------|------|-------|
| `background` | `#F8F7FC` | `#0F0E1A` | App canvas |
| `card` | `#FFFFFF` | `#1A1830` | Card fill |
| `cream` | `#F0EFF5` | `#252338` | Inactive areas, input backgrounds |

#### Borders

| Token | Light | Dark | Usage |
|-------|-------|------|-------|
| `hairline` | `#E8E6F0` | `#353350` | 0.5dp dividers |
| `border1` | `#D8D5E5` | `#3D3A52` | 1dp card borders |
| `border2` | `#C5C2D5` | `#4A4760` | Strong borders |

#### Semantic

| Token | Light | Dark | Usage |
|-------|-------|------|-------|
| `success` / `successInk` | `#D1FAE5` / `#065F46` | `#065F46` / `#D1FAE5` | Success states |
| `warning` / `warningInk` | `#FEF3C7` / `#92400E` | `#92400E` / `#FEF3C7` | Warning states |
| `danger` / `dangerInk` | `#FEE2E2` / `#991B1B` | `#991B1B` / `#FEE2E2` | Error/destructive states |

#### Shadow

| Token | Value | Usage |
|-------|-------|-------|
| `shadowTint` | `#26234D` (navy) | All elevation shadows |

### 11.2 Typography Scale

| Style | Font | Size | Weight | Line Height | Letter Spacing | Usage |
|-------|------|------|--------|-------------|----------------|-------|
| `h1` | Plus Jakarta Sans | 32 | 800 | 1.15 | -0.02em | Hero headlines |
| `h2` | Plus Jakarta Sans | 22 | 700 | 1.2 | -0.01em | Screen titles |
| `h3` | Plus Jakarta Sans | 17 | 700 | 1.25 | 0 | Section titles |
| `h4` | Plus Jakarta Sans | 14 | 600 | 1.3 | 0 | Card titles |
| `body` | Plus Jakarta Sans | 14 | 400 | 1.5 | 0 | Body text |
| `bodyStrong` | Plus Jakarta Sans | 14 | 600 | 1.4 | 0 | Emphasized body |
| `caption` | Plus Jakarta Sans | 12 | 500 | 1.4 | 0 | Captions, subtitles |
| `label` | Plus Jakarta Sans | 11 | 700 | 1.2 | 0.10em UPPER | Section labels |
| `labelStrong` | Plus Jakarta Sans | 11 | 700 | 1.2 | 0.10em UPPER | Strong labels |
| `inputLabel` | Plus Jakarta Sans | 12 | 600 | 1.3 | 0 | Input labels |
| `dataLg` | DM Mono | 22 | 500 | 1.2 | 0 tnum | Large stats |
| `data` | DM Mono | 15 | 400 | 1.3 | 0 tnum | Data values |
| `dataSm` | DM Mono | 13 | 400 | 1.3 | 0 tnum | Small data |

### 11.3 Spacing Scale (Base-4)

| Token | Value | Usage |
|-------|-------|-------|
| `xs` | 4dp | Tight gaps, icon insets |
| `sm` | 8dp | Small gaps, chip padding |
| `md` | 12dp | Medium gaps, card spacing |
| `lg` | 16dp | Screen padding, card content |
| `xl` | 20dp | Section spacing |
| `xxl` | 24dp | Large section breaks |
| `xxxl` | 32dp | Hero spacing |
| `screenPadding` | 16dp | Standard screen horizontal padding |

### 11.4 Border Radii

| Token | Value | Usage |
|-------|-------|-------|
| `radiusSm` | 6dp | Chips, small elements |
| `radiusInput` | 12dp | Input fields, date picker |
| `radiusCard` | 16dp | Cards, dialogs |
| `radiusSheet` | 24dp | Bottom sheets (top corners) |
| `radiusXl` | 20dp | FABs, large elements |
| `radiusPill` | 999dp | Pills, avatars, progress bars |

### 11.5 Elevation System

| Level | DY | Spread | Alpha | Steps | Usage |
|-------|-----|--------|-------|-------|-------|
| `Card` | 2dp | 4dp | 6% | 4 | Resting cards |
| `Raised` | 8dp | 24dp | 9% | 8 | Sheets, popovers, active rows |
| `Modal` | 16dp | 40dp | 15% | 12 | Modals, bottom sheets, dialogs |

**Rules:**
- Shadows are navy-tinted (`shadowTint`), never pure black
- Shadows are suppressed in dark mode (`isNight == true`)
- Multi-layer Canvas drawBehind with quadratic falloff
- Apply BEFORE clip/background

### 11.6 Motion System

| Token | Type | Config | Usage |
|-------|------|--------|-------|
| `springSoft` | Spring | D=0.8, S=Medium | Card press, gentle |
| `springSheet` | Spring | D=0.85, S=Low | Sheet drag |
| `springCard` | Spring | D=0.75, S=MediumLow | Card swipe |
| `springSnappy` | Spring | D=0.6, S=Medium | Tab indicator |
| `fadeUp` | Tween | 300ms + 8dp Y | Item entrance |
| `forwardSlide` | Tween | 300ms + 30dp X + fade | Forward nav |
| `modalRise` | Tween | 300ms + 24dp Y + fade | Modal entrance |
| `quietFade` | Tween | 300ms crossfade | Theme switch |

### 11.7 Component API Reference (Target State)

#### Buttons
```
VButton(text, onClick, variant, tone, size, full, enabled, loading, icon, trailingIcon)
VIconButton(icon, onClick, tone, size, enabled, badge)
```

#### Surfaces
```
VCard(modifier, padding, shape, background, border, elevated, onClick, content)
VActionCard(title, subtitle, icon, onClick, modifier)
EnrollCard → deprecated, use VCard with flat=true
VBottomSheet(visible, onDismiss, peekHeight, dragHandle, content)
```

#### Inputs
```
VInput(value, onValueChange, label, hint, placeholder, leadingIcon, keyboardType, isPassword, singleLine, enabled, trailing, error, maxLength)
VTextArea(value, onValueChange, label, placeholder, maxLength, rows)
VSelect(options, selected, onSelect, label, placeholder, multiSelect)
VSwitch(checked, onCheckedChange, label)
VCheckbox(checked, onCheckedChange, label)
VSlider(value, onValueChange, range, label)
VDatePicker(value, onValueChange, label, placeholder, enabled, isError)
VTimePicker(hour, minute, onHourChange, onMinuteChange, label, enabled)
VScheduleToggle(selection, onSelectionChange)
VSearchBar(value, onValueChange, placeholder, onClear)
```

#### Navigation
```
VBottomNav2(items, selected, onSelect, activeColor)  // canonical
VTopTabs(tabs, selected, onSelect, activeColor)
VBackHeader(title, onBack, action)
VSegmentedControl(segments, selected, onSelect)
```

#### Display
```
VBadge(text, tone, leadingIcon)
VTag(text, selected, onSelect, icon)
VAvatar(name, photoUrl, size, ring)
VLogo(size, withWord, tone)
VBrandLogo(size, cornerRadius)
VProgressBar(value, tone, height)
VProgressRing(value, size, strokeWidth, tone, label)
VDonut(data, size, thickness, center)
VSparkline(values, width, height, color)
VBars(data, height)
VLegendDot(color, label, value)
```

#### Feedback
```
VSnackbar(message, visible, onDismiss, tone, actionLabel, onAction)
VConfirmDialog(visible, title, message, confirmLabel, onConfirm, onDismiss, cancelLabel, icon)
VEmptyState(title, icon, body, action)
VComingSoon(title, description, preview, onNotifyMe)
```

#### Infrastructure
```
VScreenScaffold(topBar, bottomBar, content)
VStateHost(loading, error, isEmpty, emptyTitle, emptyBody, emptyIcon, onRetry, skeleton, content)
VPullRefresh(isRefreshing, onRefresh, content)
VThemePicker(currentMode, currentCustomId, onSelect)
ShimmerBox(modifier, width, height, shape)
```

#### Atoms
```
VDivider(modifier, color)
VLabel(text, color)
VStatusDot(color, size, ring)
```

---

## 12. Prioritized Implementation Roadmap

### Phase 1: Foundation & Debt Clearance (Weeks 1–3)

| Task | Priority | Effort | Impact |
|------|----------|--------|--------|
| Consolidate bottom nav to VBottomNav2 | P0 | S | High |
| Replace QR code placeholder with real library | P0 | S | Critical |
| Replace HorizontalDivider with VDivider in SchoolHomeScreenV2 | P0 | XS | Medium |
| Deprecate EnrollTokens/SectionHeader/EnrollCard — migrate to VTheme | P1 | M | High |
| Add VBottomSheet component | P0 | M | High |
| Add VSearchBar component | P0 | S | High |
| Add staggered entrance animation to key screens | P0 | M | High |
| Add smart empty states with contextual CTAs | P0 | M | High |
| Audit all screens for hardcoded dp values → replace with VTheme.dimens | P1 | M | Medium |
| Remove CommonLandingScreenV2 (dead code) | P1 | XS | Low |

### Phase 2: Component Enhancement (Weeks 4–6)

| Task | Priority | Effort | Impact |
|------|----------|--------|--------|
| Add VSelect/VDropdown component | P1 | M | High |
| Add VSwitch component | P1 | S | Medium |
| Add VTextArea component | P1 | S | Medium |
| Add VSegmentedControl component | P2 | S | Medium |
| Add VIconButton variant to VButton | P1 | S | Medium |
| Add error state to VInput | P1 | S | High |
| Add tab content crossfade to all portals | P1 | S | High |
| Add pull-to-refresh to all list/dashboard screens | P1 | M | High |
| Add VCheckbox component | P2 | S | Medium |
| Add VSlider component | P2 | S | Low |
| Add illustrated empty states | P2 | M | Medium |
| Add line chart with axes to VCharts | P2 | M | Medium |
| Add tooltip/coach mark system | P3 | M | Low |

### Phase 3: Screen Decomposition (Weeks 7–12)

| Task | Priority | Effort | Impact |
|------|----------|--------|--------|
| Decompose SchoolHomeScreenV2 (1,748 lines → 14 composables) | P0 | L | Critical |
| Decompose CommonLandingScreenV3 (1,295 lines → 8 composables) | P0 | L | High |
| Decompose ClassesSubjectsScreenV2 (119KB → 8–12 files) | P1 | L | High |
| Decompose SchoolLibraryScreen (90KB → 6–8 files) | P1 | L | Medium |
| Decompose ParentHomeScreenV2 (840 lines → 5 composables) | P1 | M | High |
| Decompose TeacherHomeScreenV2 (740 lines → 4 composables) | P1 | M | Medium |
| Add skeletons for all remaining screens | P1 | L | High |
| Add inline search to roster/people screens | P1 | M | Medium |
| Add filter bottom sheet to list screens | P2 | M | Medium |

### Phase 4: UX Polish & Accessibility (Weeks 13–16)

| Task | Priority | Effort | Impact |
|------|----------|--------|--------|
| Screen reader labels audit | P0 | M | Critical |
| Min touch target audit (44dp) | P0 | M | Critical |
| Reduce motion respect | P1 | S | High |
| High contrast mode verification | P1 | M | High |
| Dynamic type verification | P1 | M | High |
| Focus indicators for web/desktop | P1 | M | High |
| Offline indicators | P1 | S | Medium |
| Infinite scroll for long lists | P1 | M | Medium |
| Sticky section headers in long lists | P2 | S | Medium |
| Bulk actions for admin screens | P2 | L | Medium |
| Global search | P2 | L | High |
| Customizable admin dashboard widgets | P2 | L | Medium |

### Phase 5: Motion & Delight (Weeks 17–20)

| Task | Priority | Effort | Impact |
|------|----------|--------|--------|
| Staggered entrance on all list screens | P0 | M | High |
| Card press feedback (pressScale) on all clickable cards | P1 | M | Medium |
| Swipe-to-dismiss on list items | P2 | M | Medium |
| Tab content transition polish | P1 | S | Medium |
| Micro-interaction audit (haptic feedback) | P2 | M | Medium |
| First-time user coach marks | P3 | M | Low |
| Animated illustrations for empty states | P3 | L | Low |

### Timeline Summary

```
Weeks 1-3:   Phase 1 — Foundation & Debt Clearance
Weeks 4-6:   Phase 2 — Component Enhancement
Weeks 7-12:  Phase 3 — Screen Decomposition
Weeks 13-16: Phase 4 — UX Polish & Accessibility
Weeks 17-20: Phase 5 — Motion & Delight
```

### Effort Legend
- **XS** = < 1 hour
- **S** = 1–4 hours
- **M** = 1–2 days
- **L** = 3–5 days

---

## Appendix A: File Inventory

### Theme Files (11)
```
composeApp/.../ui/v2/theme/
├── BrandingColorMapper.kt
├── EnrollTokens.kt
├── VColors.kt
├── VDimens.kt
├── VElevation.kt
├── VMotion.kt
├── VStatusBarAdapter.kt
├── VTheme.kt
├── VThemeDef.kt
├── VThemeRegistry.kt
└── VType.kt
```

### Component Files (23)
```
composeApp/.../ui/v2/components/
├── EnrollCard.kt
├── QrCodeImage.kt
├── SectionHeader.kt
├── VAtoms.kt
├── VAvatar.kt
├── VBadge.kt
├── VBrandLogo.kt
├── VButton.kt
├── VCard.kt
├── VCharts.kt
├── VDatePicker.kt
├── VIcons.kt
├── VInput.kt
├── VLogo.kt
├── VNavigation.kt
├── VProgress.kt
├── VPullRefresh.kt
├── VScheduleToggle.kt
├── VShimmer.kt
├── VSnackbar.kt
├── VStructure.kt
├── VThemePicker.kt
└── VTimePicker.kt
```

### Screen Files (118+)
```
composeApp/.../ui/v2/screens/
├── Shared.kt
├── Skeletons.kt
├── auth/ (10 files)
├── discovery/ (3 files)
├── notifications/
├── parent/ (32 files)
├── school/ (49 files)
└── teacher/ (25 files)
```

### Navigation
```
composeApp/.../ui/v2/navigation/
└── NavGraphV2.kt (567 lines)
```

---

## Appendix B: Design Rules (Existing)

These rules are extracted from code comments and should be preserved in the redesign:

| Rule | Description |
|------|-------------|
| **RULE-1** | Never introduce a new color — all colors flow through VTheme.colors tokens |
| **RULE-2** | No jank, no layout shift — animations must not cause recomposition loops |
| **RULE-3** | Reuse shared components instead of re-deriving at every callsite |
| **RULE-5** | CMP-safe — all code must compile on all Compose Multiplatform targets |
| **LAW 3** | Every screen has Loading · Error · Empty states via VStateHost |
| **LAW 4** | Back-press can never return to splash, landing, or an auth screen |
| **RA-21** | Every destructive action must route through VConfirmDialog |
| **Design Law** | NEVER COLLAPSE TO WHITE SPACE — every card renders a rich state even with sparse data |
| **No Floating Toasts** | Presentation flourishes are surfaced inside cards/bell, never as transient overlays |

---

*End of specification document.*
