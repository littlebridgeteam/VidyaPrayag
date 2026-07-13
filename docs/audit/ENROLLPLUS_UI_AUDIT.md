# EnrollPlus UI v2 — Comprehensive Audit

**Date:** 2025-01-18  
**Scope:** `composeApp/src/commonMain/kotlin/com/littlebridge/enrollplus/ui/v2/`  
**Total files audited:** ~120 Kotlin files (20 components, ~100 screen files)

---

## 1. Directory Structure

```
ui/v2/
├── components/          # 20 reusable UI primitives
│   ├── VButton.kt        # Button with variant/tone/size + loading phase animation
│   ├── VCard.kt          # Elevated surface + VActionCard
│   ├── VInput.kt         # BasicTextField-based input with focus treatment
│   ├── VNavigation.kt    # VTopTabs (pill bar) + VBottomNav2 (floating dock)
│   ├── VStructure.kt     # VScreenScaffold, VEmptyState, VComingSoon, VConfirmDialog
│   ├── VAtoms.kt         # VDivider, VLabel, VStatusDot
│   ├── VAvatar.kt        # Circular avatar with crossfade image→initials
│   ├── VBadge.kt         # Pill status chip (6 tones)
│   ├── VCharts.kt        # VDonut + VBars (pure Canvas)
│   ├── VProgress.kt      # VProgressBar + VProgressRing (animated)
│   ├── VShimmer.kt       # ShimmerBox loading placeholder
│   ├── VDatePicker.kt    # Calendar dialog date picker
│   ├── VPullRefresh.kt   # Material3 PullToRefreshBox wrapper
│   ├── VIcons.kt         # Curated icon set (634 lines)
│   └── ... (VLogo, VBrandLogo, VThemePicker, QrCodeImage, etc.)
├── theme/                # VTheme, VThemeDef, VMotion
├── navigation/
│   └── NavGraphV2.kt     # 635 lines — role-based nav, deep links, onboarding gate
├── screens/
│   ├── Shared.kt         # VStateHost, VLoadingState, VErrorState, VEmptyState
│   ├── Skeletons.kt      # 7 skeleton composables
│   ├── auth/             # 3 screens (onboarding gate flows)
│   ├── parent/           # ~30 files (portal + 25+ screens/cards)
│   ├── school/           # ~40 files (portal + 35+ screens/cards)
│   ├── teacher/          # ~15 files (portal + 10+ screens)
│   ├── discovery/        # ~5 files (marketplace, calendar, SRI preview)
│   ├── notifications/    # 1 file (shared notifications inbox)
│   ├── library/          # ~3 files (shared library UIX components)
│   ├── tutor/            # ~4 files (tutor chat, heatmap, practice, progress)
│   └── student/          # 1 file (student library view)
└── locale/               # appString() + LocalLocale
```

**Screen count:** ~77 `*ScreenV2.kt` files + ~23 helper/card files = **~100 total screen-layer files**.

---

## 2. Theme System

### Design Token Architecture

The app has **two parallel color systems** — a critical inconsistency:

| System | Location | Usage |
|--------|----------|-------|
| **VTheme** (v2) | `ui/v2/theme/VTheme.kt` | 180 references across 35 files |
| **VColors** (v1 tokens) | `ui/tokens/VColors.kt` | 2,225 references across 97 files |

**VTheme** provides `VTheme.colors`, `VTheme.type`, `VTheme.dimens` — the intended v2 design system. It supports light/dark/midnight themes via `VThemeDef` with `isNight` flag.

**VColors** is a static object with hardcoded color vals (`cream`, `violet`, `mint`, `coral`, `gold`, `ink`, `ink2`, `ink3`, `surfaceCard`, `line`, `white`, `error`, etc.). It does **not** respond to theme changes.

### Finding: Theme Inconsistency (CRITICAL)

The design system memory states: *"Never hardcode colors — always use VTheme."* However:

- **97 of ~100 screen files** import and use `VColors.*` directly
- Only **35 files** use `VTheme.colors` 
- The `VColors` object is a **light-only static palette** — dark/midnight themes cannot work when screens reference `VColors.cream` or `VColors.ink` directly
- The `AcademicCalendarScreenV2` uses a `VtC`/`VtT` bridge (a separate legacy adapter) instead of either system

### Typography

`VTypography` (static object) is used universally — `h1`, `h2`, `body`, `bodyStrong`, `caption`, `label`, `inputLabel`. This is consistent but also **not theme-aware**.

### Motion

`VMotion` provides reusable transition specs. `VTheme` also exposes motion tokens. Usage is sporadic — many screens define inline `tween()` durations rather than using shared motion tokens.

---

## 3. Reusable Components

### Component Inventory & Compliance

| Component | Adoption | Notes |
|-----------|----------|-------|
| **VButton** | High | Used everywhere. Has variant/tone/size system + loading phase animation. **Issue:** auth screens mix v1 `ui.components.VButton` with v2 `VButton` |
| **VCard** | High | Universal surface. `onClick` variant for clickable cards. |
| **VInput** | High | BasicTextField-based. Focus treatment with teal glow ring. |
| **VScreenScaffold** | **LOW** | Only 3 files (the 3 portal shells). Most screens use raw `Column`/`Box` |
| **VStateHost** | **GOOD** | 60 files, 181 matches. Proper loading/error/empty/content gating |
| **VConfirmDialog** | Moderate | 17 files, 41 matches. RA-21 directive says "every destructive action" — not fully enforced |
| **VTopTabs** | Moderate | Used in academics, conversations, admin sub-tabs |
| **VBottomNav2** | Moderate | Portal docks use bespoke variants (ParentDock, TeacherDock, VCreamBottomNav) |
| **VAvatar** | High | Crossfade image→initials, deterministic pastel bg |
| **VBadge** | High | 6 tones, used for status chips everywhere |
| **VProgressBar** | Moderate | Used in progress cards |
| **VProgressRing** | Moderate | Used in dashboard/academic cards |
| **VDatePicker** | Low | Only used where date input needed |
| **VPullRefresh** | Moderate | Used in home/notifications screens |
| **ShimmerBox** | High | Used in all 7 skeleton composables |
| **VIcons** | High | Curated icon set, used everywhere |

### Legacy Component Imports (FINDING)

4 files import from `ui.components` (v1) instead of `ui.v2.components`:
- `SchoolOnboardingScreenV2.kt` — 5 imports (VButton, VInput, VProgressBar, etc.)
- `TeacherFirstLoginScreenV2.kt` — 3 imports (VButton, VInput, VBackHeader)
- `ParentLinkChildScreenV2.kt` — 2 imports (VBackHeader, VProgressBarSegments)
- `ParentAcademicsScreenV2.kt` — 3 imports

These should be migrated to v2 equivalents for consistency.

---

## 4. Navigation Flows

### NavGraphV2 Architecture

`NavGraphV2.kt` (635 lines) is the single entry point:

1. **EntryRole enum** — `Parent`, `SchoolAdmin`, `SuperAdmin`, `Teacher`, `Alumni`, `Unknown` with `from(role: String?)` factory
2. **DeepLinkTarget sealed class** — `ParentTab`, `SchoolScreen`, `TeacherScreen`, `Messages`, `Generic` with role-specific path parsing
3. **Theme resolution** — user preference + school branding → `VThemeDef`, applied via `AnimatedContent` with fade transition
4. **AuthedFlow gate** — enum state machine: `Resolving → Onboarding/FirstLogin → Portal`
5. **RolePortal dispatcher** — routes to `ParentPortalV2`, `SchoolPortalV2`, `TeacherPortalV2`

### Back Behavior

All three portals implement `BackHandler`:
- **Parent:** Overlay → tabs; no tab-level back (stays on current tab)
- **School:** Overlay → tabs; `StudentProfile`/`TeacherProfile` → return-to-origin overlay; `ClassDetail` → `ClassesSubjects`
- **Teacher:** Overlay → tabs; non-home tab → home (familiar app behavior)

### Transition Specs

- NavGraphV2: `fadeIn(300) + fadeOut(200)` for theme switches
- AuthedFlow: `AnimatedContent` with enum state machine
- ParentLinkChild: `slideInHorizontally + fadeIn` step transitions (280ms)
- ParentUnlinked: Custom vertical/horizontal slide transitions per step direction

### Deep Link Routing

Each portal has a `LaunchedEffect(deepLinkTarget)` that maps `DeepLinkTarget` variants to tab + overlay combinations. The mapping is exhaustive but verbose — the parent portal alone has ~40 `when` branches for overlay routing.

---

## 5. Auth Screens

### 5.1 ParentLinkChildScreenV2 (473 lines)

**Purpose:** 3-step wizard for parents to link their child.

| Step | Content | Validation |
|------|---------|------------|
| 1 | Full name + language tags (English/हिन्दी) | None (always enabled) |
| 2 | School search → `GET /api/v1/parent/schools/search` | Must select a school |
| 3 | Child name + class + section + roll no + phone | `step3Valid` from VM |

**Wiring:** `LinkChildViewModel` (Koin) — real backend calls.  
**State handling:** `linkPending` (awaiting admin approval), `linkNeedsReview` (phone mismatch), `linkError`.  
**Animations:** `AnimatedContent` with horizontal slide + fade between steps (280ms).  
**Issues:**
- Uses legacy `VBackHeader` and `VProgressBarSegments` from `ui.components`
- Background hardcoded to `VColors.cream` (not theme-aware)
- `contentDescription = null` on 4 icons

### 5.2 SchoolOnboardingScreenV2 (982 lines)

**Purpose:** 6-step school onboarding wizard for new school admins.

| Step | Title | Content |
|------|-------|---------|
| 1 | Identity | Legal name, short name, affiliation, board, type, principal name + mobile |
| 2 | Branding | Logo upload, color pickers |
| 3 | Classes | Class builder with sections |
| 4 | Subjects | Subject list with codes + types |
| 5 | Teachers | Teacher provisioning (name + email) |
| 6 | Review | Launch confirmation |

**Wiring:** 5 ViewModels — `InstitutionalBasicOBViewModel`, `BrandingInfoOBViewModel`, `AcademicInfoOBViewModel`, `LaunchInfoOBViewModel`, `TeacherProvisioningOBViewModel`.  
**Resume:** `resumeStep` parameter allows jumping to a specific step.  
**Issues:**
- 5 legacy v1 component imports (`VButton`, `VInput`, `VProgressBar`, `VProgressBarSegments`, `FilterChip`)
- Heavy use of `VColors.*` directly (72 matches)
- `contentDescription = null` on 4 icons
- Pre-seeded with 2 classes + 6 subjects (reasonable defaults)

### 5.3 TeacherFirstLoginScreenV2 (197 lines)

**Purpose:** Password change on first login for teachers.

**Fields:** Current temp password, new password, confirm password.  
**Validation:** Min 8 chars, match check (client-side `validate()` function).  
**Wiring:** `AuthRepository.changePassword()` via `koinInject()`.  
**Issues:**
- 3 legacy v1 imports (`VButton`, `VInput`, `VBackHeader`)
- All colors hardcoded via `VColors.*`
- `teacherName` parameter defaults to `"Mr. Vikram"` (should be null/empty)
- "Need Help" button has empty `onClick = {}` (no-op)
- `contentDescription = null` on 3 icons
- No password strength indicator

---

## 6. Admin/School Screens

### SchoolPortalV2 (726 lines)

**5-tab shell:** Home · People · Records · Comms · Settings  
**Bottom nav:** `VCreamBottomNav` (bespoke, not shared `VBottomNav2`)  
**Overlays:** 40 distinct overlay screens (largest portal)

#### Tab Screens

| Tab | Screen | Key Features |
|-----|--------|-------------|
| Home | `SchoolHomeScreenV2` | Command palette, pinned screens, live metrics, PEWS cards, calendar, analytics |
| People | `SchoolPeopleScreenV2` | 3 sub-tabs (Teachers/Students/Staff), link request queue, graduation bulk action |
| Records | `SchoolRecordsScreenV2` | Fee records, attendance, academic records |
| Comms | `SchoolCommsScreenV2` | Messages, PTM scheduling, scheduled messages, delivery log, announcements |
| Settings | `SchoolSettingsScreenV2` | Profile edit, academic year, transport, scholarships, branding, ID cards, library, classes/subjects, alumni, cohort analytics |

#### Overlay Screens (40)

Notable overlays:
- **AcademicCalendarPlatformScreenV2** — premium VP-CAL calendar platform
- **UnifiedCreateEventScreenV2** — 3-step event/announcement creator
- **PewsCohortScreenV2 / PewsStudentDetailScreenV2** — Predictive Early Warning System
- **StudentProfileScreenV2** — student drill-down with attendance/marks/leave/fees
- **TeacherProfileScreenV2** — teacher detail with assignment management
- **ClassesSubjectsScreenV2** — consolidated class/subject/bell schedule/timetable
- **ClassDetailScreenV2** — composite class detail with student/teacher drill-down
- **AdmissionsCrmScreenV2** — admissions CRM
- **AlumniScreen / AlumniDetailScreen / AlumniCampaignScreen** — alumni management
- **TransportManagementScreenV2** — routes, vehicles, assignments
- **ScholarshipManagementScreenV2** — schemes, applications, renewals
- **SchoolBrandingScreenV2** — colors, logo, subdomain
- **IdCardScreen** — templates, generation, PDF export
- **SchoolLibraryScreen** — catalog, issues, returns, fines
- **AnalyticsDashboardScreenV2** — cohort analytics
- **LinkRequestsScreenV2** — parent→child link approval queue
- **LeaveRequestsScreenV2** — leave request management
- **MessagesScreenV2** — threaded messaging
- **ScheduledMessagesScreenV2** — scheduled message composer
- **DailyAttendanceScreenV2** — daily attendance
- **ClassPerformanceScreenV2 / TeacherPerformanceScreenV2** — performance analytics
- **ResultsPublishScreenV2** — results publishing
- **SchedulePtmScreenV2** — PTM scheduling
- **HealthRecordsScreenV2** — student health records
- **EditSchoolProfileScreenV2** — institutional profile editor
- **AcademicYearManagementScreenV2** — academic year management
- **AdminEventRegistrationScreenV2** — event registration
- **StaffProfileScreenV2** — non-teaching staff profile
- **StudentRosterScreenV2** — student roster
- **DeliveryLog** — `VComingSoon` placeholder (not yet shipped)

**State management:** `VStateHost` used in 35+ school screens (good adoption).  
**Destructive actions:** `VConfirmDialog` used in 12 school screens (moderate).  
**Issues:**
- `VColors.*` used 81 times in `SchoolHomeScreenV2` alone
- `VScreenScaffold` only used at portal level, not individual screens
- `BackHandler` has nested `when` for profile return routing (complex but functional)

---

## 7. Teacher Screens

### TeacherPortalV2 (364 lines)

**5-tab shell:** Home · Update · Classes · Timetable · Profile  
**Bottom nav:** `TeacherDock` (bespoke, not shared `VBottomNav2`)  
**Overlays:** 12 distinct overlay screens

#### Tab Screens

| Tab | Screen | Key Features |
|-----|--------|-------------|
| Home | `TeacherHomeScreenV2` | Greeting, fingerprint check-in popup, attendance summary, today's schedule, assignments/tests/reminders, swipe cards |
| Update | `TeacherUpdateScreenV2` | Write plane: Attendance · Marks · Syllabus · Homework with class/section/subject scope gate |
| Classes | `TeacherClassesScreenV2` | Class list → composite class detail → student profile drill-down |
| Timetable | `TeacherTimetableScreenV2` | Weekly timetable view |
| Profile | `TeacherProfileScreenV2` | Identity, leave apply/status, password, theme switch, logout |

#### Update Tab Architecture

The Update tab is pre-scoped from Home CTAs via:
- `updateAssignmentId` — pre-selected assignment
- `updateScopeLabel` — display label for scope
- `updateInitialTool` — `UpdateTool.Attendance/Marks/LessonPlan`
- `updateScopeNonce` — forces re-read of initial values

#### Overlay Screens (12)

- **NotificationsScreenV2** — shared inbox
- **TeacherHealthAlertsScreenV2** — health alerts
- **TransportAttendanceScreenV2** — transport attendance
- **TeacherPewsScreenV2** — students needing attention
- **TeacherReportReviewQueueScreen** — AI report card review
- **TeacherReportDraftEditorScreen** — report card draft editor
- **TeacherHeatmapScreen** — tutor heatmap
- **DigitalIdCardScreen** — shared digital ID (isTeacher=true)
- **ScheduledMessagesScreenV2** — shared scheduled messages
- **TeacherPtmEventRegistrationScreenV2** — PTM event registration
- **TeacherMessagesScreenV2** — threaded messaging
- **AcademicCalendarScreenV2** — shared calendar

**Issues:**
- `VColors.*` used 65 times in `TeacherHomeScreenV2` alone
- `VScreenScaffold` only used at portal level
- `contentDescription = null` on 8+ icons in home screen
- `TeacherKitV2.kt` uses `VTheme.colors` (1 match) — rare correct usage

---

## 8. Parent Screens

### ParentPortalV2 (450 lines)

**5-tab shell:** Home · Academics · Fees · Conversations · Profile  
**Bottom nav:** `ParentDock` (bespoke floating glass dock, not shared `VBottomNav2`)  
**Overlays:** 18 distinct overlay screens  
**Special:** Unlinked-parent gate — shows `ParentUnlinkedScreenV2` when no child linked

#### Unlinked-Parent Flow

`ParentUnlinkedScreenV2` (706 lines) — 3-step first-run experience:
1. **Carousel** — 5-slide `HorizontalPager` with Notion-style artifact cards (AI Learning, Safety, Conversations, Academics, Link Child)
2. **Marketplace** — embedded `DiscoveryScreenV2` for school discovery
3. **LinkChild** — `ParentLinkChildScreenV2` wizard

#### Tab Screens

| Tab | Screen | Key Features |
|-----|--------|-------------|
| Home | `ParentHomeScreenV2` (1412 lines) | Portal header with child carousel, daily summary, schedule, announcements, quick actions, transport, tutor, scholarships, ID card, library, events |
| Academics | `ParentAcademicsScreenV2` | Multi-tab: Marks, Attendance, Homework, Quizzes, Syllabus, Report Card |
| Fees | `ParentFeesScreenV2` | Fee summary, payment, history |
| Conversations | `ParentConversationsScreenV2` | Messaging + Announcements segments |
| Profile | `ParentProfileCardScreenV2` | Flagship collectible player card with swipe-down account options |

#### Overlay Screens (18)

- **NotificationsScreenV2** — shared inbox
- **AcademicCalendarScreenV2** — shared calendar (parent qualifier)
- **ScholarshipWorkflowScreenV2** — scholarship application workflow
- **ParentProfileScreenV2** — account settings
- **ParentLinkChildScreenV2** — child linking wizard
- **ParentLeaveScreenV2** — leave application
- **ParentMessagesScreenV2** — parent↔teacher/admin messaging
- **DiscoveryScreenV2** — school marketplace (authenticated)
- **ParentHealthScreenV2** — child health records
- **ParentPulseScreen** — parent pulse
- **BusTrackingScreenV2** — live bus tracking
- **TutorChatScreen** — AI tutor chat
- **ParentProgressScreen** — tutor progress
- **DigitalIdCardScreen** — digital ID card
- **ParentLibraryScreenV2** — library
- **ParentEventRegistrationScreenV2** — event registration
- **ParentFeePaymentScreenV2** — fee payment
- **ParentFeeHistoryScreenV2** — fee history

**Dock hiding:** The floating dock hides when a conversation thread or compose-new is open (WhatsApp pattern).  
**Issues:**
- `ParentHomeScreenV2` is 1412 lines — should be decomposed
- `VColors.*` used 109 times in home screen alone
- `contentDescription = null` on 8 icons in home screen
- `ParentUnlinkedScreenV2` hardcodes English strings ("Welcome to Enroll+", "The school experience, reimagined.", "Swipe to explore...") — not localized

---

## 9. Shared/Discovery Screens

### 9.1 DiscoveryScreenV2 (876 lines)

**Purpose:** School marketplace — list, profile, compare views.  
**Wiring:** `SchoolDiscoveryViewModel` → `GET /api/v1/parent/schools/discover`.  
**State management:** `VStateHost` for loading/error/empty.  
**Features:**
- Client-side search filter (name + location substring)
- Filter chips: All, CBSE, English, Co-ed
- Sort: name, rating
- Compare mode (up to 3 schools)
- School profile drill-down
- Embedded mode for unlinked-parent landing
- "Already linked? Link your child" CTA (conditional)

**Issues:**
- SRI pills use hardcoded colors (`SriInk = 0xFF0A3A76`, `SriBg = 0xFFC8DEFF`) — intentional per design spec, but bypasses theme
- Server-side filtering not yet wired (Phase D)
- Board/type/fee range fields show as "Coming Soon" (backend doesn't send)

### 9.2 AcademicCalendarScreenV2 (294 lines)

**Purpose:** Month grid + upcoming events list.  
**Wiring:** `AcademicCalendarViewModel` → `GET /api/v1/school/calendar`.  
**State management:** `VStateHost` with `SkeletonCalendar`.  
**Issues:**
- Uses `VtC`/`VtT` bridge instead of `VTheme` or `VColors` — a third color system
- `coloredV()` extension instead of `colored()` — inconsistent with rest of app
- No event type color coding (backend doesn't categorize)

### 9.3 NotificationsScreenV2 (526 lines)

**Purpose:** Shared notification inbox across all portals.  
**Wiring:** `NotificationsViewModel` → `GET /api/v1/parent/notifications`.  
**Features:**
- Navy→indigo gradient hero with radial teal blob
- All/Unread filter pills
- Per-item cards with category badge, time, title, body, unread dot, chevron
- Staggered fade-up entrance animation
- Mark all read, clear all, mark individual read
- Deep link routing from notification tap
- Pull-to-refresh

**Issues:**
- 27 `VColors.*` usages — not theme-aware
- 6 `contentDescription = null` on icons

---

## 10. Forms, Lists, Dialogs, Bottom Sheets

### Forms

- **VInput** is the universal form field — `BasicTextField` with focus treatment
- **VDatePicker** for all date inputs — read-only field opening calendar dialog
- **ParentLinkChildScreenV2** — 5-field form (name, class, section, roll, phone) with inline validation
- **SchoolOnboardingScreenV2** — 6-step form wizard with 5 ViewModels
- **TeacherFirstLoginScreenV2** — 3-field password form with client-side validation
- **No form abstraction** — each screen builds forms from raw `VInput` + `Column`/`Row`

### Lists

- **LazyColumn** used in roster/message/notification lists
- **LazyRow** used in child carousel, quick actions, featured schools
- **HorizontalPager** used in unlinked-parent carousel
- **VStateHost** gates list screens with skeleton loading
- No shared list item component — each screen composes its own row layouts

### Dialogs

- **VConfirmDialog** — the only dialog component (17 files, 41 matches)
- Used for: delete student, delete teacher, delete staff, approve/reject link, approve/reject leave, logout, remove assignment, etc.
- **RA-21 directive:** "every destructive action must route through VConfirmDialog" — **not fully enforced** (many `onClick` deletions don't use it)

### Bottom Sheets

- **No bottom sheet component exists** in the v2 component library
- Modal-style overlays use full-screen `when` blocks in portal shells
- No `ModalBottomSheet` or equivalent from Material3

---

## 11. Accessibility, Animations, Platform Behavior

### Accessibility

**CRITICAL:** 309 `contentDescription = null` across 85 files.

- Icons universally set `contentDescription = null` — **screen reader users get no icon labels**
- Text-based content is readable by TalkBack/VoiceOver
- No `semantics` modifiers used anywhere for custom accessibility
- No focus ordering specified
- No minimum touch target enforcement (some clickable areas may be < 48dp)
- No dynamic font scaling support (VTypography is static)

### Animations

| Animation | Where Used | Spec |
|-----------|-----------|------|
| `AnimatedContent` | NavGraphV2, AuthedFlow, ParentLinkChild, ParentUnlinked | Fade/slide transitions |
| `animateFloatAsState` | FeatureSlideCard, VProgressBar, VProgressRing | Scale/alpha/progress |
| `animateDpAsState` | SlideIndicator | Width animation |
| `rememberInfiniteTransition` | ShimmerBox | Shimmer sweep (1200ms) |
| `tween()` | Most transitions | 200-700ms durations |
| `spring()` | VBottomNav2, FeatureSlideCard | Damping ratio 0.8 |
| `BackHandler` | All 3 portals | Predictive back / edge-swipe |

### Platform Behavior

- **Status bar:** `VStatusBarAdapter(def.colors.isNight)` in NavGraphV2 — adapts status bar icons to theme
- **IME padding:** `imePadding()` used in auth screens and input-heavy screens
- **Navigation bars padding:** `navigationBarsPadding()` used in auth/overlay screens
- **Safe areas:** `statusBarsPadding()` used in most full-screen overlays
- **Predictive back:** `BackHandler` with `ExperimentalComposeUiApi` opt-in — Android 14+ gesture support
- **No iOS-specific handling** — Compose Multiplatform renders identically on iOS

---

## 12. Summary of Findings

### Critical Issues

1. **Theme inconsistency (CRITICAL):** 97/100 screen files use `VColors.*` (static, light-only) instead of `VTheme.colors` (theme-aware). Dark/midnight themes cannot work properly. This is the single largest architectural debt.

2. **Accessibility deficit (CRITICAL):** 309 `contentDescription = null` across 85 files. No `semantics` modifiers. No screen reader support for icons. No dynamic font scaling.

3. **No bottom sheet component:** The app needs modal bottom sheets for quick actions, filters, and selections. Currently everything is full-screen overlays or dialogs.

### Moderate Issues

4. **Legacy v1 component imports:** 4 auth/academics files import from `ui.components` (v1) instead of `ui.v2.components`.

5. **VScreenScaffold underutilized:** Only 3 portal shells use it. Individual screens use raw `Column`/`Box` — no consistent max-width constraint or portal background.

6. **VConfirmDialog not universally enforced:** RA-21 directive says every destructive action must use it, but adoption is only 17 files.

7. **Three color systems:** `VTheme.colors` (v2), `VColors` (v1 static), and `VtC`/`VtT` (teacher bridge) — should consolidate to one.

8. **Hardcoded strings in unlinked-parent flow:** `ParentUnlinkedScreenV2` has English-only strings not going through `appString()`.

9. **Large files:** `ParentHomeScreenV2` (1412 lines), `SchoolOnboardingScreenV2` (982 lines), `DiscoveryScreenV2` (876 lines) — should be decomposed.

### Positive Findings

10. **VStateHost adoption is excellent** — 60 files use it for consistent loading/error/empty/content state handling.

11. **Skeleton loading** — 7 skeleton composables with `ShimmerBox` provide smooth loading experiences.

12. **Deep link system** is comprehensive — typed `DeepLinkTarget` sealed classes with role-specific routing.

13. **Back handling** is well-implemented across all portals with `BackHandler` for predictive back.

14. **Component library** is well-designed — VButton, VCard, VInput, VBadge, VAvatar, VProgress, VCharts are solid primitives.

15. **Real backend wiring** — all screens use Koin-injected ViewModels with real API calls. No MockV2 in production paths.

16. **Pull-to-refresh** is consistently implemented via `VPullRefresh` wrapper.

17. **Notifications** are well-designed with staggered animations, deep link routing, and mark-all/clear-all actions.

---

## 13. Recommendations

| Priority | Issue | Recommendation |
|----------|-------|---------------|
| P0 | Theme inconsistency | Migrate all `VColors.*` references to `VTheme.colors` — or make `VColors` theme-aware |
| P0 | Accessibility | Add meaningful `contentDescription` to all icons; add `semantics` for custom controls |
| P1 | Legacy imports | Migrate 4 auth files from `ui.components` to `ui.v2.components` |
| P1 | VScreenScaffold | Adopt in individual screens, not just portals |
| P1 | Bottom sheets | Add a `VBottomSheet` component to the v2 library |
| P1 | VConfirmDialog | Audit all destructive actions and route through VConfirmDialog |
| P2 | Color systems | Consolidate `VtC`/`VtT` bridge into `VTheme` |
| P2 | Hardcoded strings | Route all strings through `appString()` in `ParentUnlinkedScreenV2` |
| P2 | Large files | Decompose `ParentHomeScreenV2`, `SchoolOnboardingScreenV2`, `DiscoveryScreenV2` |
| P2 | Dynamic font scaling | Make `VTypography` respond to `fontScale` from `VTheme` |
| P3 | Form abstraction | Consider a `VForm` or `VFormField` wrapper for consistent form layouts |
| P3 | List item component | Consider a `VListItem` for consistent row layouts |
| P3 | Motion tokens | Centralize all `tween()` durations into `VMotion` |
