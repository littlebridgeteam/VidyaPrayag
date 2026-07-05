# PARENT PORTAL PREMIUM REBUILD — GOD MODE PROMPT (PART 1 OF 2)

## CONTEXT & AUDIT FINDINGS

You are working on the VidyaPrayag / Enroll+ Compose Multiplatform app. The Parent Portal has 5 bottom-nav tabs (Home, Academics, Fees, Conversations, Profile) and 20+ overlay screens, all routed through `ParentPortalShell.kt`. The backend server has 35+ routing files with real database-backed endpoints. The premium UI layer uses VColors, VShapes, VTypography, VMotion tokens, pressScale/shapeMorph modifiers, SkeletonCard/ErrorStateCard/EmptyStateCard states, VStaggeredItem entrances, and VPullRefreshPremium.

Despite previous rebuilds, a thorough code audit has revealed persistent issues that MUST be fixed in this final pass.

---

## ZERO TOLERANCE RULES

### RULE 1: ZERO HARDCODED DATA
No inline data collections, no mock lists, no dummy strings, no placeholder models, no hardcoded stats/badges/messages/subjects/suggestions. ALL data must flow:
```
Composable → ViewModel.state.collectAsStateV2() → Repository → ParentApi (Ktor) → Server Route → Database
```

### RULE 2: NO EMPTY onClick HANDLERS
Every `onClick = {}`, `clickable(...) { }`, `VPrimaryButton(onClick = {})` must be replaced with a real action — either a ViewModel call, a navigation callback, or a deep link trigger. If the action doesn't exist yet, create it.

### RULE 3: EVERY NOTIFICATION TAP MUST NAVIGATE
Every notification card must call `onDeepLink(notif.deepLink)` or equivalent to route the user to the corresponding screen. The deep link infrastructure exists in `NavGraphV2.kt` and `ParentPortalShell.kt` — it just needs to be connected.

### RULE 4: EVERY FORM MUST BE WIRED
Every text input, send button, apply button, register button, and submit button must call a ViewModel function that hits a real API endpoint. No form is decorative.

### RULE 5: EVERY CARD MUST BE TAPPABLE
Every card (event, scholarship, book, fee, quiz, leave, calendar event, announcement, thread) must navigate to a detail screen or trigger an action via a callback. No card is display-only if it has a natural detail/action target.

### RULE 6: PREMIUM UI STANDARDS
- All cards: `pressScale` + `shapeMorph` tactile feedback
- All scrollable screens: 140dp bottom padding
- All data-driven screens: VPullRefreshPremium
- All content entrances: VStaggeredItem with staggered delays
- All screens: loading (SkeletonCard) / error (ErrorStateCard) / empty (EmptyStateCard) / loaded states
- All directional icons: AutoMirrored variants
- All colors/typography/shapes: VColors / VTypography / VShapes tokens only
- All overlays: ParentOverlayScaffold
- No content overflow or clipping on 360×640dp screen

---

## AUDIT RESULTS — HARDCODED DATA TO ELIMINATE

### CRITICAL: ParentProfileScreen.kt
**Problem:** Hardcoded `BadgeData` list with 7 fake badges ("Math Champ", "Bookworm", "Quick Solver", "Perfect Score", "Science Whiz", "100 Days", "Art Master") with fake earned dates, fake progress values, and fake descriptions. Hardcoded `StatData` list with fake stats ("18 Quizzes Done", "↑ 420 this week", "↑ 2% this month", "↑ 5% this term"). Hardcoded XP calculation (`displayLevel * 300`, `(displayLevel + 1) * 500`).

**Fix:** Wire to `TrackProgressRouting.kt` backend which provides badges, competencies, and play indicators. Create or use a `ParentTrackProgressViewModel` that calls the track progress API. Replace all hardcoded badges and stats with data from the ViewModel state. If the API doesn't return some of these fields, extend the server route to provide them — do NOT fabricate data on the client.

### CRITICAL: ParentTutorChatScreen.kt
**Problem:** Entirely hardcoded. No ViewModel. Hardcoded chat messages (`mutableStateListOf(ChatMessage(...))` with 3 fake messages). Hardcoded subjects list (`listOf("Math", "Science", "English", "Social")`). Hardcoded suggestions list (`listOf("Explain fractions", "Practice algebra", ...)`) . Hardcoded Plan cards (3 fake weekly plans with fake progress). Hardcoded Practice cards (3 fake practice items). Send button only adds to local mutableStateListOf — no API call.

**Fix:** Wire to `AiRouting.kt` backend. Create a `ParentTutorViewModel` with:
- `sendMessage(text: String, subject: String)` → POST to AI chat endpoint
- `loadPlan(subject: String)` → GET AI study plan
- `loadPractice(subject: String)` → GET practice items
- State flow with messages list, plan list, practice list, loading/error states
- Subjects should come from the child's enrolled subjects via the academics API, not hardcoded

### CRITICAL: ParentNotificationsScreen.kt
**Problem:** Notification cards render data from `NotificationsViewModel` but have NO onClick handler. Tapping a notification does nothing. The notification data model includes a `deepLink` field but it is never used.

**Fix:** Add `onDeepLink: (String) -> Unit` parameter to `ParentNotificationsScreen`. Wire each `NotificationCard` to call `onDeepLink(notif.deepLink)` on tap. In `ParentPortalShell.kt`, pass the deep link handler that triggers the existing `LaunchedEffect(deepLinkTarget)` routing. Also add `pressScale` + `shapeMorph` to notification cards. Add mark-as-read functionality via ViewModel call on tap.

---

## AUDIT RESULTS — EMPTY onClick HANDLERS TO WIRE

| Screen | Line | Element | Required Action |
|--------|------|---------|----------------|
| ParentThreadDetailScreen.kt | 70 | "Send" button | Wire to `ParentMessageViewModel.sendMessage()` → POST /api/v1/parent/messages |
| ParentComposeMessageScreen.kt | 28 | "Send Message" button | Wire to `ParentMessageViewModel.sendMessage()` with recipient + subject |
| ParentComposeMessageScreen.kt | 26 | Message text input | Bind to ViewModel state, not empty `onValueChange = {}` |
| ParentScholarshipScreen.kt | 99 | "Apply Now" button | Wire to scholarship apply API (POST /api/v1/parent/scholarships/apply) |
| ParentScholarshipScreen.kt | 88 | Scholarship card click | Navigate to scholarship detail or expand with full info |
| ParentQuizzesScreen.kt | 51 | "Start Quiz" button | Wire to quiz start API, navigate to quiz detail/taking screen |
| ParentQuizDetailScreen.kt | 41 | "Start Quiz" button | Wire to quiz start API, begin quiz flow |
| ParentEventsScreen.kt | 74 | "Register" button | Wire to event registration API (POST /api/v1/parent/events/{id}/register) |
| ParentEventsScreen.kt | 62 | Event card click | Navigate to event detail or open registration overlay |
| ParentLibraryScreen.kt | 62 | Book card click | Navigate to book detail or open borrow action |
| ParentLeaveScreen.kt | 179 | Leave history card click | Navigate to leave detail or expand with full info |
| ParentCalendarScreen.kt | 89 | Calendar event click | Navigate to event detail or open event overlay |
| ParentFeesScreen.kt | 139 | Fee card click | Navigate to fee detail or open payment flow |
| ParentSchoolDetailScreen.kt | 167 | "Request Admission Info" button | Wire to school inquiry API or open compose message |
| ParentSchoolDetailScreen.kt | 169 | "Save to Favorites" button | Wire to favorites API or local persistence |
| ParentProfileScreen.kt | 162 | Profile hero card click | Navigate to edit profile or account settings |
| ParentHomeScreen.kt | 349 | "Dismiss" alert action | Wire to `ParentDashboardViewModel.dismissAlert(id)` |
| ParentHomeScreen.kt | 448 | Schedule card click | Navigate to timetable or class detail |
| ParentHomeScreen.kt | 638 | Hero stat card click | Navigate to corresponding detail (Pulse, attendance, alerts) |
| ParentConversationsScreen.kt | 199 | Announcement card click | Navigate to announcement detail or open full announcement |
| ParentTutorChatScreen.kt | 270 | Practice card click | Navigate to practice/quiz flow |

---

## BACKEND ROUTES THAT EXIST BUT ARE NOT FULLY WIRED IN FRONTEND

| Server Route File | Endpoints | Frontend Gap |
|-------------------|-----------|--------------|
| `AiRouting.kt` | AI chat, study plan, practice | ParentTutorChatScreen has no ViewModel, no API calls |
| `TrackProgressRouting.kt` | Badges, competencies, play indicators | ParentProfileScreen badges/stats are hardcoded |
| `EventRegistrationRouting.kt` | List events, register, cancel, slots | ParentEventsScreen "Register" button is empty onClick |
| `ScholarshipRouting.kt` | List, apply, applications, renew | ParentScholarshipScreen "Apply Now" is empty onClick |
| `LibraryRouting.kt` | List books, search, borrow, return | ParentLibraryScreen card click is empty |
| `ParentMessagesRouting.kt` | Threads, recipients, send, edit, delete | ParentComposeMessageScreen and ParentThreadDetailScreen send buttons are empty |
| `NotificationPreferencesRouting.kt` | Get/set notification preferences | No notification preferences screen in parent portal |
| `IdCardRouting.kt` | Digital ID card data | Verify ParentDigitalIdCardScreen is fully wired |
| `HealthRouting.kt` | Health records | Verify ParentHealthScreen is fully wired |
| `AcademicCalendarRouting.kt` | Calendar events | ParentCalendarScreen event click is empty |

---

## FRONTEND SCREENS THAT NEED BACKEND WIRING VERIFICATION

Every screen below must be verified to ensure it pulls ALL data from a ViewModel backed by a real API. If any screen has hardcoded data or empty handlers, fix it.

### Bottom-Nav Tabs (5)
1. **ParentHomeScreen.kt** — Wired to `ParentDashboardViewModel` ✓, but fix empty onClick handlers (Dismiss, schedule card, stat card)
2. **ParentAcademicsScreen.kt** — Wired to `ParentAcademicsViewModel` ✓, verify all 7 sub-tabs (Overview, Attendance, Marks, Syllabus, Homework, Quizzes, Report) pull data from ViewModel
3. **ParentFeesScreen.kt** — Wired to `FeeViewModel` ✓, but fee card onClick is empty — wire to payment flow or fee detail
4. **ParentConversationsScreen.kt** — Wired to `ParentMessageViewModel` + `ParentAnnouncementViewModel` ✓, but announcement card onClick is empty
5. **ParentProfileScreen.kt** — Wired to `ParentProfileViewModel` ✓, but badges and stats are hardcoded — wire to TrackProgress API

### Overlays (20+)
6. **ParentNotificationsScreen.kt** — Wired to `NotificationsViewModel` ✓, but NO onClick on notification cards — add deepLink navigation
7. **ParentCalendarScreen.kt** — Verify ViewModel wiring, fix empty event click
8. **ParentScholarshipScreen.kt** — Verify ViewModel wiring, fix empty "Apply Now" and card click
9. **ParentLeaveScreen.kt** — Wired to `ParentLeaveViewModel` ✓, fix empty history card click
10. **ParentTutorChatScreen.kt** — NO ViewModel — create `ParentTutorViewModel`, wire to AiRouting
11. **ParentTutorProgressScreen.kt** — Verify ViewModel wiring
12. **ParentLibraryScreen.kt** — Verify ViewModel wiring, fix empty book card click
13. **ParentEventsScreen.kt** — Verify ViewModel wiring, fix empty "Register" and card click
14. **ParentTransportScreen.kt** — Verify ViewModel wiring to TransportRouting
15. **ParentHealthScreen.kt** — Verify ViewModel wiring to HealthRouting
16. **ParentPulseScreen.kt** — Wired to `ParentPulseViewModel` ✓, verify all data is from ViewModel
17. **ParentDigitalIdCardScreen.kt** — Verify ViewModel wiring to IdCardRouting
18. **ParentDiscoveryScreen.kt** — Verify ViewModel wiring, fix any hardcoded school data
19. **ParentSchoolDetailScreen.kt** — Verify ViewModel wiring, fix empty action buttons
20. **ParentLinkChildScreen.kt** — Verify ViewModel wiring to ParentLinkRouting
21. **ParentAccountSettingsScreen.kt** — Verify ViewModel wiring
22. **ParentComposeMessageScreen.kt** — NO ViewModel wiring — wire to ParentMessageViewModel
23. **ParentThreadDetailScreen.kt** — Verify ViewModel wiring, fix empty Send button
24. **ParentQuizzesScreen.kt** — Verify ViewModel wiring, fix empty "Start Quiz"
25. **ParentQuizDetailScreen.kt** — Verify ViewModel wiring, fix empty "Start Quiz"
26. **ParentReportCardScreen.kt** — Verify ViewModel wiring
27. **ParentTimetableScreen.kt** — Verify ViewModel wiring
28. **ParentSyllabusScreen.kt** — Verify ViewModel wiring
29. **ParentHomeworkScreen.kt** — Verify ViewModel wiring
30. **ParentDailySummaryScreen.kt** — Verify ViewModel wiring
31. **ParentAnnouncementsScreen.kt** — Verify ViewModel wiring

---

## SHARED LAYER — ViewModels AND APIs

### Existing ViewModels (shared/src/commonMain/.../feature/parent/presentation/)
- `ParentDashboardViewModel.kt` — dashboard data ✓
- `ParentAcademicsViewModel.kt` — academics data ✓
- `ParentAnnouncementViewModel.kt` — announcements ✓
- `ParentHomeViewModel.kt` — home data ✓
- `ParentLeaveViewModel.kt` — leave requests ✓
- `ParentMessageViewModel.kt` — messaging ✓
- `ParentProfileViewModel.kt` — profile data ✓
- `ParentPulseViewModel.kt` — pulse data ✓

### Missing ViewModels TO CREATE
- `ParentTutorViewModel.kt` — AI tutor chat, plan, practice (wire to AiRouting)
- `ParentTrackProgressViewModel.kt` — badges, stats, competencies (wire to TrackProgressRouting)
- `ParentEventsViewModel.kt` — event list, registration (wire to EventRegistrationRouting)
- `ParentScholarshipViewModel.kt` — scholarship list, apply, applications (wire to ScholarshipRouting)
- `ParentLibraryViewModel.kt` — book list, search, borrow, return (wire to LibraryRouting)
- `ParentTransportViewModel.kt` — live location, route (wire to TransportRouting)
- `ParentHealthViewModel.kt` — health records (wire to HealthRouting)
- `ParentDigitalIdViewModel.kt` — ID card data (wire to IdCardRouting)
- `ParentCalendarViewModel.kt` — calendar events (wire to AcademicCalendarRouting)
- `ParentDiscoveryViewModel.kt` — school discovery (verify if exists)
- `ParentNotificationPreferencesViewModel.kt` — notification preferences (wire to NotificationPreferencesRouting)

### Existing API (shared/src/commonMain/.../feature/parent/data/remote/)
- `ParentApi.kt` — verify all endpoints are covered

### Existing Repository (shared/src/commonMain/.../feature/parent/data/repository/)
- `ParentRepositoryImpl.kt` — verify all methods are implemented
- `ParentRepository.kt` — verify all interfaces are declared

---

## DEEP LINK MAP — EVERY PATH MUST WORK

The `parseDeepLink()` function in `NavGraphV2.kt` already maps these paths. Verify each one navigates correctly:

| Deep Link Path | Tab | Overlay | Notes |
|----------------|-----|---------|-------|
| /parent/home | Home | — | ✓ |
| /parent/academics | Academics | — | ✓ |
| /parent/fees | Fees | — | ✓ |
| /parent/conversations | Conversations | — | ✓ |
| /parent/profile | Profile | — | ✓ |
| /parent/notifications | Home | notifications | Verify opens notifications overlay |
| /parent/calendar | Home | calendar | Verify opens calendar overlay |
| /parent/scholarships | Home | scholarships | Verify opens scholarships overlay |
| /parent/leave | Home | leave | Verify opens leave overlay |
| /parent/messages/{threadId} | — | messages | Verify opens thread detail with threadId |
| /parent/transport | Home | transport | Verify opens transport overlay |
| /parent/library | Home | library | Verify opens library overlay |
| /parent/events | Home | events | Verify opens events overlay |
| /parent/health | Home | health | Verify opens health overlay |
| /parent/pulse | Home | pulse | Verify opens pulse overlay |
| /parent/id-card | Home | id-card | Verify opens digital ID overlay |
| /parent/tutor | Academics | tutor | Verify opens tutor chat |
| /parent/tutor-progress | Academics | tutor-progress | Verify opens tutor progress |
| /parent/report-card | Academics | report-card | Verify opens report card |
| /parent/timetable | Academics | timetable | Verify opens timetable |
| /parent/account-settings | Profile | account-settings | Verify opens account settings |
| /parent/link-child | Profile | link-child | Verify opens link child |
| /parent/announcements | Conversations | announcements | Verify opens announcements |
| /parent/academics/marks | Academics | marks | Verify sub-tab navigation |
| /parent/academics/attendance | Academics | attendance | Verify sub-tab navigation |
| /parent/academics/homework | Academics | homework | Verify sub-tab navigation |
| /parent/academics/quizzes | Academics | quizzes | Verify sub-tab navigation |
| /parent/academics/syllabus | Academics | syllabus | Verify sub-tab navigation |

---

## NOTIFICATION DEEP LINK WIRING — CRITICAL PATH

The FCM service (`VidyaPrayagFirebaseMessagingService.kt`) receives push payloads with `deepLink`, `refType`, `refId` fields. `MainActivity.kt` extracts these and passes them to `App()` → `NavGraphV2()` → `parseDeepLink()` → `AuthedFlow()` → `ParentPortalShell()`.

**The gap:** `ParentNotificationsScreen.kt` renders notification cards but does NOT call the deep link handler when a card is tapped. The in-app notification list is display-only.

**Required fix:**
1. Add `onDeepLink: (String) -> Unit` parameter to `ParentNotificationsScreen`
2. In `ParentPortalShell.kt`, pass `onDeepLink = { path -> /* trigger deep link routing */ }` to the notifications overlay
3. In `NotificationCard`, add `clickable` with `pressScale` + `shapeMorph` that calls `onDeepLink(notif.deepLink)`
4. Also call `viewModel.markAsRead(notif.id)` on tap
5. Verify the deep link path from the server notification payload matches the `parseDeepLink()` expected format

---

## EXECUTION METHODOLOGY

For EACH screen:
1. Read the screen file completely
2. Identify ALL hardcoded data (lists, strings, stats, messages, etc.)
3. Identify ALL empty onClick handlers
4. Identify the corresponding ViewModel and API endpoint
5. If ViewModel doesn't exist — create it in `shared/src/commonMain/.../feature/parent/presentation/`
6. If API method doesn't exist — add it to `ParentApi.kt`
7. If repository method doesn't exist — add it to `ParentRepository.kt` and `ParentRepositoryImpl.kt`
8. If server endpoint doesn't exist — create it in the appropriate routing file
9. Replace hardcoded data with ViewModel state
10. Replace empty onClick with real actions
11. Ensure loading/error/empty/loaded states
12. Ensure VPullRefreshPremium on data-driven screens
13. Ensure VStaggeredItem entrances
14. Ensure 140dp bottom padding on scrollable screens
15. Ensure pressScale + shapeMorph on all cards
16. Compile and verify zero warnings

**SEE PART 2 FOR THE SCREEN-BY-SCREEN EXECUTION CHECKLIST AND VERIFICATION RITUAL.**
