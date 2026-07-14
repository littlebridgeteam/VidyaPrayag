# Premium V3 Screen Rebuild — Updated Master Prompt (v2)

> **Generated:** July 5, 2026  
> **Status:** Phases 0–3 COMPLETE, Phase 4 ~28% done, Phase 5 partially absorbed into Phases 2–3, Phase 6 NOT STARTED  
> **Two parallel sessions are active.** This prompt supersedes the original. It reflects the **actual file system state**, not the stale progress tracker.

---

## PROJECT: VidyaPrayag / Enroll+ — Premium V3 Screen Rebuild

### WHAT WE'RE BUILDING

A complete UI rebuild of the **VidyaSetu** (Enroll+) Compose Multiplatform app — a school-parent communication platform (attendance, fees, academics, messaging, library, transport, events). The app serves 3 roles: **Parent**, **Teacher/School Staff**, and **School Admin**.

The rebuild upgrades every screen from the old V2 design system to a **premium M3 Expressive** design language, matching the HTML prototypes in the `preview/` folder (`parent-portal.html`, `auth-flow.html`). The new design features gradient heroes, radial glows, staggered entrance animations, shape-morph on press, glass-morphism, and full dark mode support.

### TECH STACK (DO NOT CHANGE)

- **Compose Multiplatform** targeting Android, iOS, JVM, JS, WASM
- **Material 3** with custom theming (not default M3 colors)
- **Koin** DI with `koinViewModel()` for ViewModels
- **StateFlow** + `collectAsStateV2()` for state management
- **Hand-rolled navigation** via `AnimatedContent` state machine in `NavGraphV2.kt` (no NavHost/Compose Navigation)
- **Room** database in shared module for offline
- **Ktor** client for networking
- **Coil** for image loading
- **kotlinx-serialization** for JSON
- **No new dependencies allowed**

### ARCHITECTURE RULES

1. **Reuse existing ViewModels** — all ViewModels live in `shared/src/commonMain/kotlin/com/littlebridge/enrollplus/feature/{auth,parent,teacher,admin}/presentation/`. Never create fake data or mock ViewModels.
2. **`VColors` is `@Composable get()`** — it delegates to `LocalVColorPalette.current`. This means:
   - Cannot use `VColors.X` in function default parameters — use `Color.Unspecified` and resolve inside the `@Composable` body
   - Cannot use `VColors.X` inside `Canvas` `DrawScope` — resolve to a local `val` before the `Canvas` block
3. **All new screens** go in `composeApp/src/commonMain/kotlin/com/littlebridge/enrollplus/ui/v2/screens/premium/{auth,parent,teacher,school}/`
4. **All new components** go in `composeApp/src/commonMain/kotlin/com/littlebridge/enrollplus/ui/v2/components/{buttons,cards,form,navigation,overlay,progress,typography,misc,carousel}/`
5. **All screens must wrap in `PremiumTheme`** — `PremiumTheme(isDark = false) { ... }` or `true` for dark
6. **Every screen handles 4 states**: Loading, Error, Empty, Content — use `VStateHostPremium` component
7. **No hardcoded hex colors** in composables — use `VColors.X` tokens
8. **Use `VMotion`** for all animation durations and easings
9. **No Android-specific APIs** in commonMain
10. **Use `VScreenScaffoldPremium`** for standard screen scaffolding (status bar adapter + safe areas)
11. **Use `VEmptyStatePremium`** for empty states, `VComingSoonPremium` for unbuilt features
12. **Use `VShimmerPremium`** for loading skeletons
13. **Use `VConfirmDialogPremium`** for confirmation dialogs
14. **Use `VFullScreenOverlay`** for full-screen overlay navigation transitions

---

## WHAT'S BEEN BUILT — PHASE 0: FOUNDATION (COMPLETE)

### Token System (6 files — Dark Mode Ready)

| File | Purpose |
|------|---------|
| `tokens/VColorPalette.kt` | Data class with ~80 color properties. Two instances: `LightPalette` and `DarkPalette`. Backed by `CompositionLocal` (`LocalVColorPalette`). |
| `tokens/VColors.kt` | Object with `@Composable get()` properties delegating to `LocalVColorPalette.current`. All component files reference `VColors.Primary` etc. |
| `tokens/PremiumTheme.kt` | Composable that provides the correct `VColorPalette` + bridges to M3 `ColorScheme` for Material components. Usage: `PremiumTheme(isDark = false) { ScreenContent() }` |
| `tokens/VShapes.kt` | Shape tokens (Full, Lg, Xl, TwoXl, etc.) |
| `tokens/VMotion.kt` | Motion tokens (DurShort2, DurMedium2, DurLong2, EaseEmphasized, FabMenuDelays, forwardSlide, modalRise, quietFade) |
| `tokens/VTypography.kt` | Typography tokens (GreetingTitle, LandingHeadline, ButtonPrimary, FormLabelAuth, NavLabel, LoginHeroTitle, LoginHeroSub, etc.) |

### Modifiers (3 files)

| File | Purpose |
|------|---------|
| `modifiers/VGlowModifier.kt` | `Modifier.radialGlow(offsetX, offsetY, radius, color)` — radial gradient glow. `toPx()` calls must be inside `drawBehind` block. |
| `modifiers/VShapeMorphModifier.kt` | `Modifier.shapeMorph(interaction, shape1, shape2, duration)` — morphs corner radius on press. |
| `modifiers/VPressScaleModifier.kt` | `Modifier.pressScale(interaction, pressedScale)` — scales down on press. |

### Premium Components (65 files total — EXPANDED from original 38)

#### buttons/ (7 files)
`VPrimaryButton.kt`, `VSecondaryButton.kt`, `VLandingButton.kt`, `VSocialButton.kt`, `VIconButton.kt`, `VFAB.kt`, **`VTextButton.kt`** (NEW)

#### cards/ (17 files — EXPANDED from 8)
`VBadgeCard.kt`, `VRoleTile.kt`, `VTrustBadge.kt`, `VFeesHeroCard.kt`, `VUpdateCard.kt`, **`VBadgePremium.kt`** (NEW), **`VChildLinkCard.kt`** (NEW), **`VGradientHeroPremium.kt`** (NEW), **`VHeroCard.kt`** (NEW), **`VListTilePremium.kt`** (NEW), **`VProfileHeroCard.kt`** (NEW), **`VQuickStatCard.kt`** (NEW), **`VSchoolCardFull.kt`** (NEW), **`VSchoolOptionCard.kt`** (NEW), **`VStatCardPremium.kt`** (NEW), **`VStatTile.kt`** (NEW), **`VSurfaceCard.kt`** (NEW)

#### carousel/ (2 files)
`VScrollSnapCarousel.kt`, **`VStaggeredColumn.kt`** (NEW — staggered entrance animation container)

#### form/ (2 files)
`VTextInput.kt`, `VSearchField.kt`

#### misc/ (14 files — EXPANDED from 6)
`VBrandLogoPremium.kt`, `VChartsPremium.kt`, `VDivider.kt`, `VLanguagePickerPremium.kt`, `VPullRefreshPremium.kt`, `VThemePickerPremium.kt`, **`VAvatarPremium.kt`** (NEW), **`VComingSoonPremium.kt`** (NEW), **`VDataTablePremium.kt`** (NEW), **`VEmptyStatePremium.kt`** (NEW), **`VPhoneFrame.kt`** (NEW), **`VShimmerPremium.kt`** (NEW), **`VStateHostPremium.kt`** (NEW), **`VStatusBar.kt`** (NEW)

#### navigation/ (9 files — EXPANDED from 4)
`VBottomNav.kt`, `VTopAppBar.kt`, `VBackHeader.kt`, `VFilterChip.kt`, **`VFilterBarPremium.kt`** (NEW), **`VOnboardingDots.kt`** (NEW), **`VScreenScaffoldPremium.kt`** (NEW), **`VSegmentedToggle.kt`** (NEW), **`VTopTabsPremium.kt`** (NEW)

#### overlay/ (6 files — EXPANDED from 4)
`VDatePickerPremium.kt`, `VTimePickerPremium.kt`, `VSnackbarPremium.kt`, **`VConfirmDialogPremium.kt`** (NEW), **`VDialog.kt`** (NEW), **`VFullScreenOverlay.kt`** (NEW)

#### progress/ (4 files — EXPANDED from 3)
`VProgressBar.kt`, `VProgressRing.kt`, `VPulseDot.kt`, **`VShimmer.kt`** (NEW)

#### typography/ (4 files)
`VSectionHeader.kt`, `VGreetingTitle.kt`, `VGreetingEyebrow.kt`, `VGradientText.kt`

### Bug Fixes Applied to Components

- `VPrimaryButton` — added missing `background(VColors.Primary)` and disabled alpha
- `VSecondaryButton` — added missing `background(VColors.SurfaceContainerLow)` and disabled alpha
- `VSocialButton` — added missing `background(VColors.SurfaceContainerLow)`
- `VIconButton` — default param `iconColor` changed from `VColors.OnSurfaceVariant` to `Color.Unspecified`, resolved inside body
- `VPulseDot` — same pattern for `@Composable get()` default param
- `VGradientText` — same pattern
- `VProgressRing` — `drawArc` fixed to use `style = Stroke(width = ...)` instead of invalid `strokeWidth` param
- `VGlowModifier` — `toPx()` calls moved inside `drawBehind` block (DrawScope implements Density)
- `VChartsPremium` — `VColors` references inside `Canvas` resolved to local vals before `Canvas` block
- `VDivider` — removed broken private `background` extension function
- `VFAB` — added missing `Box` import
- `VBadgeCard` — added missing `fillMaxWidth` import
- `VScrollSnapCarousel` — added missing `Text` import

---

## WHAT'S BEEN BUILT — PHASE 1: AUTH & ONBOARDING (COMPLETE — 10/10 SCREENS)

All in `composeApp/.../ui/v2/screens/premium/auth/`:

1. **`SplashScreen.kt`** (4.5KB) — Full-bleed gradient hero (Primary→PrimaryDeep), radial glow accents, `VBrandLogoPremium` with spring-in animation, pulsing halo via `rememberInfiniteTransition`, "VidyaSetu" wordmark + tagline.

2. **`LanguageSelectionScreen.kt`** (2.8KB) — Centered layout, "Choose Your Language" title, `VLanguagePickerPremium` with native language names, `VPrimaryButton` continue CTA. Uses `LocaleManager` from Koin.

3. **`CommonLandingScreen.kt`** (14.2KB) — The single entry surface for both parent and staff. Gradient hero with radial glow, staggered entrance animations (6 steps, 80ms delay each via `AnimatedVisibility` + `slideInVertically`), `VLogoPremium` + brand text, gradient headline "Bridge between school & home", stats row (12K+ Students, 80+ Schools, 4.9 Rating) in glass cards, role selection tiles (`VRoleTile` — Parent with Favorite icon, Staff with AdminPanelSettings icon), trust badges (`VTrustBadge`), legal footer links (Privacy/Terms/Help).

4. **`AuthScaffoldPremium.kt`** (8.7KB) — Shared chrome for all auth screens. Gradient hero (Primary→PrimaryDeep) with `radialGlow`, glass back button (`GlassWhite15` bg + `pressScale`), `VBrandLogoPremium` with spring-in, title/subtitle with `VTypography.LoginHeroTitle/LoginHeroSub`, rounded form sheet overlapping hero (`RoundedCornerShape(topStart=28.dp, topEnd=28.dp)`), error display, secured footer. Also exports `AuthBackLinkPremium`.

5. **`ParentAuthScreen.kt`** (6.2KB) — Phone + OTP login. Reuses `AuthViewModel` via `koinViewModel()`. On mount: `viewModel.reset()` + `viewModel.onRoleChanged("PARENT")`. Steps: Identifier (phone input) → Otp (OTP input + optional name for SIGNUP_PHONE) → submit. `VTextInput` for fields, `VPrimaryButton` for CTA with ArrowForward trailing icon. `LaunchedEffect(state.isAuthSuccessful)` calls `onAuthSuccess()`.

6. **`AdminAuthScreen.kt`** (13.7KB) — Email/password login + school self-registration. Reuses `AuthViewModel`. On mount: `reset()` + `onRoleChanged("ADMIN")`. Steps: Identifier (email/staff ID) → LoginPassword (password + forgot password link) → SignupDetails (school registration form with name, email, school name, board chips via `VFilterChip`, city, password). "Onboard Your School" CTA and "Haven't registered?" prompt on Identifier step.

7. **`TeacherFirstLoginScreen.kt`** (6.7KB) — One-time "set new password" gate. Reuses `AuthRepository.changePassword()` via `koinInject()`. Three fields: current temp password, new password, confirm. Client-side validation (≥8 chars, match). On success → `onDone()`.

8. **`ParentLinkChildScreen.kt`** (13.3KB) — 3-step link-child wizard. Reuses `LinkChildViewModel` via `koinViewModel()`. Step 1: name + language chips. Step 2: school search via `VSearchField` + `SchoolMatch` result cards. Step 3: child name, class, section, roll number. Progress bars at top, `Crossfade` between steps, `VPrimaryButton` CTA changes per step.

9. **`SchoolOnboardingScreen.kt`** (13.1KB) — 4-step school registration wizard. Step 1: school details (name, board chips, school type chips, city). Step 2: admin details (name, email, phone). Step 3: classes selection (FlowRow of `VFilterChip`). Step 4: review + confirm. `AnimatedContent` with horizontal slide between steps, progress bars, `VPrimaryButton` CTA.

10. **`LegalInfoScreen.kt`** (13.4KB) — Privacy Policy / Terms of Service / Help Desk. Tabbed interface with pill-shaped tab selector. Privacy content: data collection, usage, what we never do, security, retention. Terms content: acceptable use, accounts, content, availability, changes. Help content: email support card (opens `mailto:` via `LocalUriHandler`), FAQ items. Uses `LegalDocPremium` enum.

**Compile status: BUILD SUCCESSFUL** (`composeApp:compileDevDebugKotlinAndroid`)

---

## WHAT'S BEEN BUILT — PHASE 2: PARENT PORTAL (COMPLETE — 33 FILES)

All in `composeApp/.../ui/v2/screens/premium/parent/`:

### Portal Shell
- **`ParentPortalShell.kt`** (7.1KB) — Bottom-nav shell with 5 tabs + overlay routing

### 5 Tab Screens
- **`ParentHomeScreen.kt`** (10KB) — Dashboard with child hero, alerts, featured schools
- **`ParentAcademicsScreen.kt`** (9.8KB) — Academics with tabs (Overview, Attendance, Marks, Syllabus, etc.)
- **`ParentFeesScreen.kt`** (5.8KB) — Fee balance hero, payment history, fee announcements
- **`ParentConversationsScreen.kt`** (6.7KB) — Messaging + announcements (segments: Messages, Announcements)
- **`ParentProfileScreen.kt`** (7.4KB) — Profile, linked children, settings (theme, language), logout

### Overlay Scaffold
- **`ParentOverlayScaffold.kt`** (3.5KB) — Shared overlay chrome for all parent full-screen overlays

### 27 Overlay/Sub Screens
- `ParentNotificationsScreen.kt` (3.1KB)
- `ParentCalendarScreen.kt` (3.9KB)
- `ParentScholarshipScreen.kt` (2.7KB)
- `ParentLeaveScreen.kt` (2.1KB)
- `ParentMessagesScreen.kt` (3.6KB)
- `ParentComposeMessageScreen.kt` (1.6KB)
- `ParentThreadDetailScreen.kt` (3.2KB)
- `ParentUnlinkedScreen.kt` (3.1KB)
- `ParentDiscoveryScreen.kt` (4.0KB)
- `ParentHealthScreen.kt` (3.2KB)
- `ParentPulseScreen.kt` (3.6KB)
- `ParentTransportScreen.kt` (2.5KB)
- `ParentTutorChatScreen.kt` (3.3KB)
- `ParentTutorProgressScreen.kt` (3.7KB)
- `ParentDigitalIdCardScreen.kt` (2.9KB)
- `ParentLibraryScreen.kt` (2.9KB)
- `ParentEventsScreen.kt` (2.9KB)
- `ParentReportCardScreen.kt` (3.3KB)
- `ParentTimetableScreen.kt` (2.6KB)
- `ParentHomeworkScreen.kt` (2.6KB)
- `ParentQuizzesScreen.kt` (2.6KB)
- `ParentQuizDetailScreen.kt` (2.2KB)
- `ParentSyllabusV2Screen.kt` (2.8KB)
- `ParentAnnouncementsScreen.kt` (3.0KB)
- `ParentDailySummaryScreen.kt` (2.7KB)
- `ParentLeaderboardScreen.kt` (3.2KB)

### ViewModels Used (in `shared/.../feature/parent/presentation/`)
`ParentDashboardViewModel.kt`, `ParentHomeViewModel.kt`, `ParentAcademicsViewModel.kt`, `TrackProgressViewModel.kt`, `FeeViewModel.kt`, `ParentMessageViewModel.kt`, `NotificationsViewModel.kt`, `ParentProfileViewModel.kt`, `LinkChildViewModel.kt`, `ParentAnnouncementViewModel.kt`, `ParentLeaveViewModel.kt`, `ParentPulseViewModel.kt`, `ScholarshipsViewModel.kt`

---

## WHAT'S BEEN BUILT — PHASE 3: TEACHER PORTAL (COMPLETE — 30 FILES)

All in `composeApp/.../ui/v2/screens/premium/teacher/`:

### Portal Shell
- **`TeacherPortalShell.kt`** (6.0KB) — Bottom-nav shell with tabs + overlay routing

### Tab Screens
- **`TeacherHomeScreen.kt`** (7.0KB) — Today's schedule, quick actions, pending tasks
- **`TeacherClassesScreen.kt`** (6.5KB) — Class list with subject, student count, quick actions
- **`TeacherAttendanceScreen.kt`** (4.6KB) — Class attendance marking
- **`TeacherMarksScreen.kt`** (3.7KB) — Grade entry
- **`TeacherHomeworkScreen.kt`** (4.5KB) — Homework creation/assignment
- **`TeacherSyllabusScreen.kt`** (4.3KB) — Syllabus coverage tracking
- **`TeacherTimetableScreen.kt`** (4.5KB) — Weekly timetable view
- **`TeacherMessagesScreen.kt`** (3.6KB) — Messaging
- **`TeacherProfileScreen.kt`** (7.8KB) — Profile, settings, logout

### Overlay Scaffold
- **`TeacherOverlayScaffold.kt`** (3.3KB) — Shared overlay chrome

### Additional Screens
- `TeacherAnnouncementsScreen.kt` (3.0KB)
- `TeacherCalendarScreen.kt` (3.7KB)
- `TeacherChangePasswordScreen.kt` (1.9KB)
- `TeacherHealthAlertsScreen.kt` (2.8KB)
- `TeacherLeaveScreen.kt` (4.9KB)
- `TeacherLessonPlanScreen.kt` (3.8KB)
- `TeacherLibraryScreen.kt` (2.9KB)
- `TeacherNotificationsScreen.kt` (2.8KB)
- `TeacherPewsScreen.kt` (2.9KB)
- `TeacherPtmScreen.kt` (2.9KB)
- `TeacherPtmRegistrationScreen.kt` (2.3KB)
- `TeacherReportCardScreen.kt` (2.9KB)
- `TeacherReportDraftScreen.kt` (2.9KB)
- `TeacherReportReviewScreen.kt` (3.0KB)
- `TeacherScopeSelectorScreen.kt` (2.4KB)
- `TeacherStudentProfileScreen.kt` (3.0KB)
- `TeacherTransportScreen.kt` (2.5KB)
- `TeacherTransportAttendanceScreen.kt` (2.9KB)
- `TeacherUpdateScreen.kt` (2.0KB)

### ViewModels Used (in `shared/.../feature/teacher/presentation/`)
`TeacherTodayViewModel.kt`, `TeacherClassesViewModel.kt`, `TeacherAttendanceViewModel.kt`, `TeacherGradebookViewModel.kt`, `TeacherHomeworkViewModel.kt`, `TeacherSyllabusViewModel.kt`, `TeacherTimetableViewModel.kt`, `TeacherMessageViewModel.kt`, `TeacherProfileViewModel.kt`, `TeacherProfileActionsViewModel.kt`, `TeacherLessonPlanViewModel.kt`, `TeacherLeaveViewModel.kt`, `TeacherStudentProfileViewModel.kt`, `TeacherCheckInViewModel.kt`, `TeacherObligationsViewModel.kt`

---

## WHAT'S BEEN BUILT — PHASE 4: ADMIN/SCHOOL PORTAL (PARTIAL — 14 of ~50 SCREENS)

All in `composeApp/.../ui/v2/screens/premium/school/`:

### Built (14 files)
- **`SchoolPortalPremium.kt`** (32.9KB) — Portal shell with tab navigation + overlay routing (LARGE — likely contains multiple sub-screens inline)
- **`SchoolHomePremium.kt`** (9.6KB) — Admin dashboard
- **`SchoolPeoplePremium.kt`** (8.9KB) — Staff/student management
- **`SchoolRecordsPremium.kt`** (12.7KB) — Academic records
- **`SchoolSettingsPremium.kt`** (6.7KB) — School settings
- **`SchoolCommsPremium.kt`** (4.6KB) — Communications/messaging
- **`HealthRecordsPremium.kt`** (4.2KB) — Health records
- **`LeaveRequestsPremium.kt`** (3.4KB) — Leave approval
- **`PewsCohortPremium.kt`** (5.1KB) — PEWS cohort view
- **`PewsStudentDetailPremium.kt`** (4.7KB) — PEWS student detail
- **`StaffProfilePremium.kt`** (4.7KB) — Staff profile
- **`StudentProfilePremium.kt`** (5.9KB) — Student profile
- **`StudentRosterPremium.kt`** (3.6KB) — Student roster
- **`TeacherAssignmentPremium.kt`** (4.6KB) — Teacher assignment management
- **`TeacherProfilePremium.kt`** (5.9KB) — Teacher profile

### REMAINING School Screens to Build (~36 screens)

These V2 screens in `composeApp/.../ui/v2/screens/school/` have NO premium equivalent yet:

1. `AcademicCalendarPlatformScreenV2.kt` (27KB) → `AcademicCalendarPremium.kt`
2. `AcademicYearManagementScreenV2.kt` (9.7KB) → `AcademicYearManagementPremium.kt`
3. `AdminEventRegistrationScreenV2.kt` (27KB) → `EventRegistrationPremium.kt`
4. `AdminReportPublishScreen.kt` (9.6KB) → `ReportPublishPremium.kt`
5. `AdminReportingEffectivenessScreen.kt` (8.3KB) → `ReportingEffectivenessPremium.kt`
6. `AdmissionsCrmScreenV2.kt` (10.6KB) → `AdmissionsCrmPremium.kt`
7. `AlumniCampaignScreen.kt` (6.2KB) → `AlumniCampaignPremium.kt`
8. `AlumniDetailScreen.kt` (10.5KB) → `AlumniDetailPremium.kt`
9. `AlumniScreen.kt` (31.2KB) → `AlumniPremium.kt`
10. `AnalyticsDashboardScreenV2.kt` (9.9KB) → `AnalyticsDashboardPremium.kt`
11. `BrandingSettingsScreen.kt` (30.3KB) → `BrandingSettingsPremium.kt`
12. `ClassDetailScreenV2.kt` (25.9KB) → `ClassDetailPremium.kt`
13. `ClassPerformanceScreenV2.kt` (12.1KB) → `ClassPerformancePremium.kt`
14. `ClassesSubjectsScreenV2.kt` (127.8KB) → `ClassesSubjectsPremium.kt` (HUGE — may need splitting)
15. `DailyAttendanceScreenV2.kt` (9.7KB) → `DailyAttendancePremium.kt`
16. `EditSchoolProfileScreenV2.kt` (15.3KB) → `EditSchoolProfilePremium.kt`
17. `IdCardScreen.kt` + `IdCardCardsTab.kt` + `IdCardGenerateTab.kt` + `IdCardTemplatesTab.kt` → `IdCardPremium.kt` (consolidate 4 files)
18. `LinkRequestsScreenV2.kt` (10KB) → `LinkRequestsPremium.kt`
19. `MessagesScreenV2.kt` (28.6KB) → `MessagesPremium.kt`
20. `PaceAlertsScreenV2.kt` (7.2KB) → `PaceAlertsPremium.kt`
21. `PewsEffectivenessScreenV2.kt` (9.4KB) → `PewsEffectivenessPremium.kt`
22. `PewsPreview.kt` (8.1KB) → `PewsPreviewPremium.kt`
23. `ResultsPublishScreenV2.kt` (9.1KB) → `ResultsPublishPremium.kt`
24. `SchedulePtmScreenV2.kt` (13.4KB) → `SchedulePtmPremium.kt`
25. `ScheduledMessagesScreenV2.kt` (9.1KB) → `ScheduledMessagesPremium.kt`
26. `ScholarshipManagementScreenV2.kt` (40.2KB) → `ScholarshipManagementPremium.kt`
27. `SchoolDayConfigScreenV2.kt` (21.6KB) → `SchoolDayConfigPremium.kt`
28. `SchoolLibraryScreen.kt` (92.4KB) → `SchoolLibraryPremium.kt` (HUGE — may need splitting)
29. `TeacherPerformanceScreenV2.kt` (9.9KB) → `TeacherPerformancePremium.kt`
30. `TransportManagementScreenV2.kt` (24.3KB) → `TransportManagementPremium.kt`
31. `TutorManagementScreenV2.kt` (4.1KB) → `TutorManagementPremium.kt`
32. `UnifiedCreateEventScreenV2.kt` (16.6KB) → `UnifiedCreateEventPremium.kt`

### ViewModels Available (in `shared/.../feature/admin/presentation/` — 36 ViewModels)
`AcademicCalendarPlatformViewModel.kt`, `AcademicCalendarViewModel.kt`, `AcademicInfoOBViewModel.kt`, `AcademicYearViewModel.kt`, `AdmissionCRMViewModel.kt`, `AnalyticsDashboardViewModel.kt`, `BrandingInfoOBViewModel.kt`, `ClassPerformanceViewModel.kt`, `ClassesSubjectsViewModel.kt`, `DailyAttendanceViewModel.kt`, `InstitutionalBasicOBViewModel.kt`, `InstitutionalProfileViewModel.kt`, `LaunchInfoOBViewModel.kt`, `LeaveRequestsViewModel.kt`, `LinkRequestsViewModel.kt`, `MessagesViewModel.kt`, `OnboardingGateViewModel.kt`, `PaceAlertsViewModel.kt`, `ResultsViewModel.kt`, `SchedulePTMViewModel.kt`, `SchoolAnnouncementsViewModel.kt`, `SchoolDashboardViewModel.kt`, `SchoolDayConfigViewModel.kt`, `SchoolProfileViewModel.kt`, `SchoolRecordsViewModel.kt`, `SchoolTeachersViewModel.kt`, `StaffViewModel.kt`, `StudentAnalyticsViewModel.kt`, `StudentProfileViewModel.kt`, `StudentRosterViewModel.kt`, `SyllabusCoverageViewModel.kt`, `TeacherAssignmentViewModel.kt`, `TeacherPerformanceViewModel.kt`, `TeacherProfileViewModel.kt`, `TeacherProvisioningOBViewModel.kt`, `UnifiedCreateEventViewModel.kt`

---

## PHASE 5: SHARED/CROSS-PORTAL SCREENS (ABSORBED INTO PHASES 2–3)

The original plan called for 13 shared/cross-portal screens. Most have been **absorbed** into the parent and teacher premium directories:

| Original Shared Screen | Built As | Location |
|------------------------|----------|----------|
| DiscoveryScreen | `ParentDiscoveryScreen.kt` | premium/parent/ |
| AcademicCalendarScreen | `ParentCalendarScreen.kt` + `TeacherCalendarScreen.kt` | premium/parent/ + premium/teacher/ |
| NotificationsScreen | `ParentNotificationsScreen.kt` + `TeacherNotificationsScreen.kt` | premium/parent/ + premium/teacher/ |
| DigitalIdCard | `ParentDigitalIdCardScreen.kt` | premium/parent/ |
| BusTracking | `ParentTransportScreen.kt` + `TeacherTransportScreen.kt` | premium/parent/ + premium/teacher/ |
| Library | `ParentLibraryScreen.kt` + `TeacherLibraryScreen.kt` | premium/parent/ + premium/teacher/ |
| TutorChat | `ParentTutorChatScreen.kt` | premium/parent/ |
| ParentProgress | `ParentTutorProgressScreen.kt` | premium/parent/ |
| ParentHealth | `ParentHealthScreen.kt` + `TeacherHealthAlertsScreen.kt` | premium/parent/ + premium/teacher/ |
| ParentPulse | `ParentPulseScreen.kt` | premium/parent/ |
| ParentLeave | `ParentLeaveScreen.kt` + `TeacherLeaveScreen.kt` | premium/parent/ + premium/teacher/ |
| EventRegistration | `ParentEventsScreen.kt` | premium/parent/ |
| ScholarshipWorkflow | `ParentScholarshipScreen.kt` | premium/parent/ |

**Phase 5 is effectively COMPLETE** — all 13 shared screens have been built as role-specific variants within their respective portals.

**REMAINING for Phase 5:** The admin versions of shared screens (Library, Transport, Calendar, Messages, Events) are part of Phase 4's remaining work.

---

## PHASE 6: CUTOVER (NOT STARTED)

### Current State
`NavGraphV2.kt` still imports and routes to **V2 screens**:
- `CommonLandingScreenV3` (V2 auth)
- `ParentAuthScreenV2`, `AdminAuthScreenV2` (V2 auth)
- `ParentLinkChildScreenV2`, `SchoolOnboardingScreenV2`, `TeacherFirstLoginScreenV2` (V2 auth)
- `LegalInfoScreenV2` (V2 auth)
- `DiscoveryScreenV2` (V2 discovery)
- `ParentPortalV2`, `TeacherPortalV2`, `SchoolPortalV2` (V2 portals)

### Cutover Steps (DO NOT START until Phase 4 is complete)
1. **Swap auth imports** in `NavGraphV2.kt` — replace V2 auth screen imports with premium auth screen imports
2. **Swap portal imports** — replace `ParentPortalV2` → `ParentPortalShell`, `TeacherPortalV2` → `TeacherPortalShell`, `SchoolPortalV2` → `SchoolPortalPremium`
3. **Swap discovery import** — replace `DiscoveryScreenV2` → `ParentDiscoveryScreen`
4. **Update all call sites** — match new function signatures (premium screens may have different parameter names)
5. **Remove dev preview entry point** if one exists
6. **Full build verification** — `.\gradlew :composeApp:compileDevDebugKotlinAndroid`
7. **Delete old V2 screen files** — only after build is green and manually verified
8. **Update `PREMIUM_REBUILD_PROGRESS.md`** — mark all phases complete

---

## KEY FILE LOCATIONS

```
Repo root: c:\Users\HP\Devin1\VidyaPrayag\

Progress tracker: c:\Users\HP\Devin1\VidyaPrayag\PREMIUM_REBUILD_PROGRESS.md
  ⚠️ NOTE: This tracker is STALE — it shows Phase 1 screens 7-10 as incomplete,
  but they ARE built. Use this prompt as the source of truth instead.

Premium tokens (6 files):
  composeApp/src/commonMain/kotlin/com/littlebridge/enrollplus/ui/v2/tokens/
    VColorPalette.kt, VColors.kt, PremiumTheme.kt, VShapes.kt, VMotion.kt, VTypography.kt

Premium modifiers (3 files):
  composeApp/src/commonMain/kotlin/com/littlebridge/enrollplus/ui/v2/modifiers/
    VGlowModifier.kt, VShapeMorphModifier.kt, VPressScaleModifier.kt

Premium components (65 files):
  composeApp/src/commonMain/kotlin/com/littlebridge/enrollplus/ui/v2/components/
    buttons/ (7), cards/ (17), carousel/ (2), form/ (2), misc/ (14),
    navigation/ (9), overlay/ (6), progress/ (4), typography/ (4)

Premium auth screens (COMPLETE — 10 files):
  composeApp/src/commonMain/kotlin/com/littlebridge/enrollplus/ui/v2/screens/premium/auth/
    SplashScreen.kt, LanguageSelectionScreen.kt, CommonLandingScreen.kt,
    AuthScaffoldPremium.kt, ParentAuthScreen.kt, AdminAuthScreen.kt,
    TeacherFirstLoginScreen.kt, ParentLinkChildScreen.kt, SchoolOnboardingScreen.kt,
    LegalInfoScreen.kt

Premium parent screens (COMPLETE — 33 files):
  composeApp/src/commonMain/kotlin/com/littlebridge/enrollplus/ui/v2/screens/premium/parent/
    ParentPortalShell.kt + 5 tab screens + ParentOverlayScaffold.kt + 26 overlay screens

Premium teacher screens (COMPLETE — 30 files):
  composeApp/src/commonMain/kotlin/com/littlebridge/enrollplus/ui/v2/screens/premium/teacher/
    TeacherPortalShell.kt + 9 tab screens + TeacherOverlayScaffold.kt + 19 overlay screens

Premium school screens (PARTIAL — 14 of ~50 files):
  composeApp/src/commonMain/kotlin/com/littlebridge/enrollplus/ui/v2/screens/premium/school/
    SchoolPortalPremium.kt + 13 screen files (36 remaining)

Existing V2 screens (reference, to be replaced):
  composeApp/src/commonMain/kotlin/com/littlebridge/enrollplus/ui/v2/screens/
    auth/ (10 files), parent/ (32 files), teacher/ (25 files), school/ (50 files),
    discovery/ (3 files), library/ (3 files), notifications/ (1 file),
    student/ (1 file), tutor/ (5 files)

Navigation:
  composeApp/src/commonMain/kotlin/com/littlebridge/enrollplus/ui/v2/navigation/NavGraphV2.kt
    (855 lines — still routes to V2 screens, NOT premium)

ViewModels (shared module):
  shared/src/commonMain/kotlin/com/littlebridge/enrollplus/feature/
    auth/presentation/        — AuthViewModel
    parent/presentation/      — 13 ViewModels
    teacher/presentation/     — 15 ViewModels
    admin/presentation/       — 36 ViewModels
    + feature/{alumni,branding,content,event,health,i18n,idcard,library,notification,pews,reportcard,scheduling,scholarship,school,schools,transport,tutor}/

Build command: .\gradlew :composeApp:compileDevDebugKotlinAndroid
```

---

## COMPILE VERIFICATION

After each batch of screens, run:
```powershell
.\gradlew :composeApp:compileDevDebugKotlinAndroid 2>&1 | Select-String -Pattern "^e:|BUILD SUC" | Select-Object -First 15
```

Fix all `^e:` errors before proceeding. The build must be green before moving to the next phase.

---

## IMMEDIATE NEXT STEPS (PRIORITY ORDER)

### 1. Complete Phase 4: Admin/School Portal (36 remaining screens)

**Strategy for large V2 screens (>20KB):**
- `ClassesSubjectsScreenV2.kt` (128KB) — Split into `ClassesListPremium.kt`, `ClassDetailPremium.kt`, `SubjectManagementPremium.kt`
- `SchoolLibraryScreen.kt` (92KB) — Split into `SchoolLibraryListPremium.kt`, `SchoolLibraryDetailPremium.kt`, `SchoolLibraryIssuePremium.kt`
- `AlumniScreen.kt` (31KB) — May keep as single `AlumniPremium.kt` with tabs
- `BrandingSettingsScreen.kt` (30KB) — May keep as single `BrandingSettingsPremium.kt` with sections
- `AdminEventRegistrationScreenV2.kt` (27KB) — Keep as single `EventRegistrationPremium.kt`
- `AcademicCalendarPlatformScreenV2.kt` (27KB) — Keep as single `AcademicCalendarPremium.kt`
- `ClassDetailScreenV2.kt` (26KB) — Keep as single `ClassDetailPremium.kt`
- `TransportManagementScreenV2.kt` (24KB) — Keep as single `TransportManagementPremium.kt`
- `SchoolDayConfigScreenV2.kt` (22KB) — Keep as single `SchoolDayConfigPremium.kt`
- `ScholarshipManagementScreenV2.kt` (40KB) — Keep as single `ScholarshipManagementPremium.kt`
- `MessagesScreenV2.kt` (29KB) — Keep as single `MessagesPremium.kt`

**ID Card consolidation:** Merge `IdCardScreen.kt` + `IdCardCardsTab.kt` + `IdCardGenerateTab.kt` + `IdCardTemplatesTab.kt` → single `IdCardPremium.kt` with tab sections.

**Build in batches of 5-8 screens, compile-check after each batch.**

### 2. Phase 6: Cutover

Once Phase 4 is complete:
1. Swap all imports in `NavGraphV2.kt`
2. Update call sites to match premium screen signatures
3. Full build verification
4. Delete old V2 screen files
5. Update progress tracker

### 3. Parallel Session Coordination

If two sessions are running:
- **Session A:** Build school screens 1-18 (alphabetical)
- **Session B:** Build school screens 19-36 (alphabetical)
- **Both:** Compile-check independently after each batch
- **Merge:** After both complete, do a joint compile-check before cutover

---

## PATTERNS TO FOLLOW (from completed screens)

### Standard Premium Screen Structure
```kotlin
@Composable
fun SomeScreenPremium(
    viewModel: SomeViewModel = koinViewModel(),
    onBack: () -> Unit,
    // ... navigation callbacks
) {
    val state by viewModel.state.collectAsStateV2()
    
    PremiumTheme(isDark = false) {
        VScreenScaffoldPremium(
            topBar = { VBackHeader(title = "Screen Title", onBack = onBack) },
        ) {
            VStateHostPremium(
                state = state,
                loadingContent = { VShimmerPremium() },
                errorContent = { VEmptyStatePremium(message = "...") },
                emptyContent = { VEmptyStatePremium(message = "...") },
            ) { data ->
                // Content
            }
        }
    }
}
```

### Portal Shell Pattern (from ParentPortalShell/TeacherPortalShell)
```kotlin
@Composable
fun SomePortalShell(
    onLogout: () -> Unit,
    deepLinkTarget: DeepLinkTarget? = null,
    modifier: Modifier = Modifier,
) {
    var activeTab by remember { mutableStateOf(0) }
    var overlay by remember { mutableStateOf<SomeOverlay?>(null) }
    
    // Deep link handling
    LaunchedEffect(deepLinkTarget) { ... }
    
    // Back handler for overlay
    VBackHandler(enabled = overlay != null) { overlay = null }
    
    Box(modifier.fillMaxSize()) {
        AnimatedContent(overlay) { current ->
            if (current == null) {
                // Tab content with VBottomNav
                Column {
                    AnimatedContent(activeTab) { tab ->
                        when (tab) {
                            0 -> HomeTab()
                            1 -> ClassesTab()
                            // ...
                        }
                    }
                    VBottomNav(
                        items = navItems,
                        activeIndex = activeTab,
                        onItemClick = { activeTab = it },
                    )
                }
            } else {
                // Overlay screen
                OverlayScaffold(onBack = { overlay = null }) {
                    when (current) {
                        // ...
                    }
                }
            }
        }
    }
}
```

### Key Gotchas (from experience)
- **`VColors.X` in default params:** Use `Color.Unspecified`, resolve in `@Composable` body
- **`VColors.X` in `Canvas`:** Resolve to `val color = VColors.X` before `Canvas` block
- **`toPx()` in modifiers:** Must be inside `drawBehind` block (DrawScope implements Density)
- **`collectAsStateV2()`:** Use this instead of `collectAsState()` — it's the app's custom wrapper
- **`VBackHandler`:** Use this instead of `BackHandler` — it's the app's custom wrapper
- **No `NavHost`:** Navigation is state-machine via `AnimatedContent` + enum routes
- **`koinViewModel()`:** Use `org.koin.compose.viewmodel.koinViewModel()` for ViewModels
- **`koinInject()`:** Use `org.koin.compose.koinInject()` for repositories/services
