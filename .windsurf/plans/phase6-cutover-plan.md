# Phase 6 Cutover Plan

## Current State
- **Build: GREEN** (`BUILD SUCCESSFUL in 7s`)
- **Phase 4: COMPLETE** — 46 premium school files exist, all wired into `SchoolPortalPremium`
- **SchoolPortalPremium already wired** in `NavGraphV2.kt` line 786 (SchoolAdmin/SuperAdmin)
- **Auth: 10 premium screens exist** but not wired into NavGraphV2
- **Parent: 33 premium screens exist** but `ParentPortalShell` is simplified (tabs only, no overlays/deep-links)
- **Teacher: 30 premium screens exist** but `TeacherPortalShell` is simplified (tabs only, no overlays/deep-links)

## Critical Gaps Discovered

### Gap 1: Portal Shells Missing Deep-Link + Overlay Routing
- **V2 `ParentPortalV2`** (671 lines): 16 overlay states, deep-link routing, back handler, 16 overlay screens
- **Premium `ParentPortalShell`** (177 lines): 5 tabs only, no overlays, no deep links, bell = TODO
- **V2 `TeacherPortalV2`** (408 lines): 14 overlay states, deep-link routing, back handler, 14 overlay screens
- **Premium `TeacherPortalShell`** (148 lines): 4 tabs only, no overlays, no deep links

### Gap 2: Auth Screen Signature Mismatches
- `SchoolOnboardingScreen` (premium) — missing `resumeStep: String` param (V2 has it for partial onboarding resume)
- `LegalInfoScreen` (premium) — uses `LegalDocPremium` enum vs V2's `LegalDoc` enum
- `ParentDiscoveryScreen` (premium) — has `onExit: () -> Unit` only, V2 has `onOpenSchool`, `onExit?`, `embedded`, `onLinkChild?`
- `CommonLandingScreen` (premium) — `onLegal: (String) -> Unit` vs V2's `onLegal: (LegalDoc) -> Unit`

### Gap 3: SchoolPortalPremium Still Uses 2 V2 Screens
- `NotificationsScreenV2` — no premium school notifications screen exists
- `AcademicCalendarScreenV2` — no premium equivalent (only `AcademicCalendarPlatformPremium` which is different)

---

## Execution Plan (5 Batches)

### Batch 1: Auth Screens + Splash (Low Risk)
**Fix signature gaps:**
1. `SchoolOnboardingScreen.kt` (premium) — add `resumeStep: String = "BASIC"` param + step mapping logic (copy from V2 lines 117-143)
2. `LegalInfoScreen.kt` (premium) — change `initial: LegalDocPremium` to `initial: String = "Privacy"` + map internally
3. `ParentDiscoveryScreen.kt` (premium) — add `onOpenSchool: (String) -> Unit = {}` param

**Swap imports in `NavGraphV2.kt`:**
- `SplashScreenV2` → `SplashScreen` (premium)
- `CommonLandingScreenV3` → `CommonLandingScreen` (premium) — map `LegalDoc` → `String` at call site
- `ParentAuthScreenV2` → `ParentAuthScreen` (premium) — same signature
- `AdminAuthScreenV2` → `AdminAuthScreen` (premium) — same signature
- `LegalInfoScreenV2` → `LegalInfoScreen` (premium) — map `LegalDoc` → `String`
- `ParentLinkChildScreenV2` → `ParentLinkChildScreen` (premium) — same signature
- `SchoolOnboardingScreenV2` → `SchoolOnboardingScreen` (premium) — pass `resumeStep`
- `TeacherFirstLoginScreenV2` → `TeacherFirstLoginScreen` (premium) — same signature
- `LanguageSelectionScreen` (V2) → `LanguageSelectionScreen` (premium) — same signature
- `DiscoveryScreenV2` → `ParentDiscoveryScreen` (premium) — adapt call site

**Swap in `App.kt`:**
- `SplashScreenV2` → `SplashScreen` (premium)

**Compile check**

### Batch 2: Upgrade ParentPortalShell (Medium Risk)
**Add to `ParentPortalShell.kt`:**
1. `ParentOverlay` enum: None, Notifications, Calendar, Scholarships, Leave, Messages, LinkChild, Discovery, Health, Pulse, Transport, TutorChat, TutorProgress, DigitalIdCard, Library, EventRegistration
2. `deepLinkTarget: DeepLinkTarget? = null` parameter
3. Deep-link state: `localDeepLink`, `deepLinkThreadId`, `deepLinkAcademicsTab`, `deepLinkSegment`, `deepLinkReportDraftId`
4. `LaunchedEffect(deepLinkTarget, localDeepLink)` — copy routing logic from V2 (lines 101-165)
5. `VBackHandler` for overlay back + tab back
6. Overlay routing using premium parent screens:
   - `ParentNotificationsScreen`, `ParentCalendarScreen`, `ParentScholarshipScreen`, `ParentLeaveScreen`, `ParentMessagesScreen`, `ParentLinkChildScreen`, `ParentDiscoveryScreen`, `ParentHealthScreen`, `ParentPulseScreen`, `ParentTransportScreen`, `ParentTutorChatScreen`, `ParentTutorProgressScreen`, `ParentDigitalIdCardScreen`, `ParentLibraryScreen`, `ParentEventsScreen`
7. Wire bell click → `ParentOverlay.Notifications`
8. Wire `ParentProfileScreen(onLinkChild = { overlay = ParentOverlay.LinkChild })`

**Compile check**

### Batch 3: Upgrade TeacherPortalShell (Medium Risk)
**Add to `TeacherPortalShell.kt`:**
1. `TeacherOverlay` enum: None, Notifications, HealthAlerts, TransportAttendance, Pews, ReportReview, ReportDraftEditor, DigitalIdCard, ScheduledMessages, EventRegistration, Messages, Calendar, Library, Announcements
2. `deepLinkTarget: DeepLinkTarget? = null` parameter
3. Deep-link state: `localDeepLink`, `deepLinkThreadId`, `selectedRouteId`, `reportClassName`, `reportSection`, `reportTerm`, `reportDraftId`
4. `LaunchedEffect(deepLinkTarget, localDeepLink)` — copy routing logic from V2 (lines 86-143)
5. `VBackHandler` for overlay back + tab back
6. Overlay routing using premium teacher screens:
   - `TeacherNotificationsScreen`, `TeacherHealthAlertsScreen`, `TeacherTransportAttendanceScreen`, `TeacherPewsScreen`, `TeacherReportReviewScreen`, `TeacherReportDraftScreen`, `TeacherMessagesScreen`, `TeacherCalendarScreen`, `TeacherLibraryScreen`, `TeacherAnnouncementsScreen`
7. Wire bell click → `TeacherOverlay.Notifications`

**Compile check**

### Batch 4: NavGraphV2 Portal Cutover + SchoolPortalPremium V2 Deps
**Swap in `NavGraphV2.kt` `RolePortal()`:**
- `TeacherPortalV2(...)` → `TeacherPortalShell(...)` with `deepLinkTarget`
- `ParentPortalV2(...)` → `ParentPortalShell(...)` with `deepLinkTarget`

**Build 2 missing premium school screens:**
1. `SchoolNotificationsScreen.kt` — premium replacement for `NotificationsScreenV2` (reuse `NotificationsViewModel`, same `onBack`/`onDeepLink` API)
2. `SchoolAcademicCalendarScreen.kt` — premium replacement for `AcademicCalendarScreenV2` (reuse calendar ViewModel with `named("schoolCalendar")` qualifier)

**Update `SchoolPortalPremium.kt`:**
- Replace `NotificationsScreenV2` import → `SchoolNotificationsScreen`
- Replace `AcademicCalendarScreenV2` import → `SchoolAcademicCalendarScreen`

**Compile check**

### Batch 5: Cleanup
- Delete old V2 files that are fully replaced:
  - `auth/SplashScreenV2.kt`, `auth/CommonLandingScreenV3.kt`, `auth/ParentAuthScreenV2.kt`, `auth/AdminAuthScreenV2.kt`, `auth/LegalInfoScreenV2.kt`, `auth/ParentLinkChildScreenV2.kt`, `auth/SchoolOnboardingScreenV2.kt`, `auth/TeacherFirstLoginScreenV2.kt`
  - `parent/ParentPortalV2.kt`
  - `teacher/TeacherPortalV2.kt`
  - `discovery/DiscoveryScreenV2.kt` (if fully replaced)
  - `notifications/NotificationsScreenV2.kt` (if fully replaced)
  - `discovery/AcademicCalendarScreenV2.kt` (if fully replaced)
- Update `PREMIUM_REBUILD_PROGRESS.md` — mark all phases complete
- Final compile check

---

## Risk Assessment
- **Batch 1**: Low risk — same patterns, same ViewModels, just different UI layer
- **Batch 2**: Medium risk — largest single change (~400 lines added to ParentPortalShell)
- **Batch 3**: Medium risk — similar to Batch 2 but smaller (~300 lines added)
- **Batch 4**: Low risk — import swaps + 2 small new screens
- **Batch 5**: Low risk — deletion only after all compile checks pass

## Compile Command
```
.\gradlew :composeApp:compileDevDebugKotlinAndroid 2>&1 | Select-String -Pattern "^e:|BUILD SUC" | Select-Object -First 15
```
