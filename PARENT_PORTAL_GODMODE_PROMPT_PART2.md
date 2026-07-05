# PARENT PORTAL PREMIUM REBUILD — GOD MODE PROMPT (PART 2 OF 2)

## SCREEN-BY-SCREEN EXECUTION CHECKLIST

Process each screen in this order. Do NOT skip any screen. After completing each screen, run the self-review ritual before moving to the next.

---

### PHASE 1: FIX CRITICAL HARDCODED DATA (3 screens)

#### 1.1 — ParentProfileScreen.kt
- [ ] Create `ParentTrackProgressViewModel.kt` in `shared/.../feature/parent/presentation/`
- [ ] Add `getTrackProgress(childId)` method to `ParentApi.kt` → GET /api/v1/parent/track-progress
- [ ] Add `getTrackProgress(childId)` to `ParentRepository.kt` interface and `ParentRepositoryImpl.kt`
- [ ] Verify `TrackProgressRouting.kt` returns: badges list (name, description, icon category, earned, earnedDate, progress, progressText), stats list (value, label, trend), XP data (current, max, level)
- [ ] If server doesn't return all fields, extend `TrackProgressRouting.kt` to include them from database
- [ ] Replace hardcoded `BadgeData` list with `state.badges` from ViewModel
- [ ] Replace hardcoded `StatData` list with `state.stats` from ViewModel
- [ ] Replace hardcoded XP calculation with `state.xp` from ViewModel
- [ ] Replace `onClick = {}` on profile hero card with `onAccountSettings` callback
- [ ] Add `pressScale` + `shapeMorph` to BadgeCard
- [ ] Add `pressScale` + `shapeMorph` to StatCard
- [ ] Add `pressScale` + `shapeMorph` to AccountRow (already has it ✓)
- [ ] Ensure loading/error/empty/loaded states for badges and stats sections
- [ ] Add VPullRefreshPremium (already has it ✓)
- [ ] Compile

#### 1.2 — ParentTutorChatScreen.kt
- [ ] Create `ParentTutorViewModel.kt` in `shared/.../feature/parent/presentation/`
- [ ] Add to `ParentApi.kt`:
  - `POST /api/v1/parent/ai/chat` — send message, get AI response
  - `GET /api/v1/parent/ai/plan?subject={subject}` — get study plan
  - `GET /api/v1/parent/ai/practice?subject={subject}` — get practice items
  - `GET /api/v1/parent/academics/subjects` — get child's enrolled subjects
- [ ] Add repository methods for each API call
- [ ] Verify `AiRouting.kt` implements these endpoints — if not, add them
- [ ] ViewModel state: messages list, plan list, practice list, subjects list, suggestions (from AI or subjects), inputText, isLoading, isSending, error
- [ ] Replace hardcoded `messages` mutableStateListOf with `state.messages` from ViewModel
- [ ] Replace hardcoded `subjects` list with `state.subjects` from ViewModel
- [ ] Replace hardcoded `suggestions` list with `state.suggestions` from ViewModel
- [ ] Replace hardcoded PlanCard data with `state.plan` from ViewModel
- [ ] Replace hardcoded PracticeCard data with `state.practice` from ViewModel
- [ ] Wire send button to `viewModel.sendMessage(inputText, selectedSubject)`
- [ ] Wire PracticeCard click to `onOpenQuizDetail(practice.quizId)` or navigate to quiz
- [ ] Add loading state (SkeletonCard) for chat/plan/practice tabs
- [ ] Add error state (ErrorStateCard) for chat/plan/practice tabs
- [ ] Add empty state (EmptyStateCard) for plan/practice tabs
- [ ] Add `pressScale` + `shapeMorph` to PracticeCard (already has pressScale, add shapeMorph)
- [ ] Add `pressScale` + `shapeMorph` to PlanCard
- [ ] Add 140dp bottom padding to scrollable columns
- [ ] Compile

#### 1.3 — ParentNotificationsScreen.kt
- [ ] Add `onDeepLink: (String) -> Unit` parameter
- [ ] Add `onClick` parameter to `NotificationCard` composable
- [ ] Wire `NotificationCard` click to call `onDeepLink(notif.deepLink)` + `viewModel.markAsRead(notif.id)`
- [ ] Add `pressScale` + `shapeMorph` to `NotificationCard`
- [ ] Add `MutableInteractionSource` to `NotificationCard`
- [ ] Replace `StatusBox("Loading notifications...")` with proper `SkeletonCard` loading state
- [ ] Replace `StatusBox("No notifications")` with `EmptyStateCard`
- [ ] Add `ErrorStateCard` for error state
- [ ] Wrap content in `VStaggeredItem` for staggered entrance
- [ ] In `ParentPortalShell.kt`, pass `onDeepLink` to notifications overlay — wire to the same deep link routing mechanism used by FCM push taps
- [ ] Verify `NotificationsViewModel` has a `markAsRead(id)` method — if not, add it
- [ ] Verify server `NotificationRouting.kt` has a PATCH/POST mark-as-read endpoint — if not, add it
- [ ] Compile

---

### PHASE 2: WIRE EMPTY onClick HANDLERS (by screen)

#### 2.1 — ParentThreadDetailScreen.kt
- [ ] Verify ViewModel is wired (should use `ParentMessageViewModel`)
- [ ] Wire "Send" button to `viewModel.sendMessage(threadId, text)`
- [ ] Bind reply text input to ViewModel state (not empty `onValueChange = {}`)
- [ ] Add `pressScale` to send button
- [ ] Add loading/sending state for send button
- [ ] Add 140dp bottom padding
- [ ] Compile

#### 2.2 — ParentComposeMessageScreen.kt
- [ ] Wire to `ParentMessageViewModel` (or create dedicated compose ViewModel)
- [ ] Bind recipient selector to `viewModel.recipients` from GET /api/v1/parent/messages/recipients
- [ ] Bind message text input to ViewModel state
- [ ] Wire "Send Message" button to `viewModel.sendMessage(recipientId, subject, body)`
- [ ] Add form validation (recipient required, message required)
- [ ] Add submitting state on send button
- [ ] Add success callback (navigate back to conversations)
- [ ] Add error state
- [ ] Add 140dp bottom padding
- [ ] Compile

#### 2.3 — ParentScholarshipScreen.kt
- [ ] Create or verify `ParentScholarshipViewModel.kt`
- [ ] Wire to `ScholarshipRouting.kt` — GET /api/v1/parent/scholarships
- [ ] Wire "Apply Now" button to `viewModel.apply(scholarshipId)` → POST /api/v1/parent/scholarships/apply
- [ ] Wire scholarship card click to expand details or navigate to detail
- [ ] Add loading/error/empty/loaded states
- [ ] Add VPullRefreshPremium
- [ ] Add VStaggeredItem entrances
- [ ] Add 140dp bottom padding
- [ ] Add `pressScale` + `shapeMorph` to scholarship cards
- [ ] Add applying state on "Apply Now" button
- [ ] Compile

#### 2.4 — ParentQuizzesScreen.kt + ParentQuizDetailScreen.kt
- [ ] Verify `ParentAcademicsViewModel` provides quiz data
- [ ] Verify `ParentAcademicsRouting.kt` has quiz list + quiz start endpoints
- [ ] Wire "Start Quiz" button to `viewModel.startQuiz(quizId)` or navigate to quiz taking screen
- [ ] Wire quiz card click to navigate to `ParentQuizDetailScreen`
- [ ] Add loading/error/empty/loaded states
- [ ] Add `pressScale` + `shapeMorph` to quiz cards
- [ ] Add VStaggeredItem entrances
- [ ] Add 140dp bottom padding
- [ ] Compile

#### 2.5 — ParentEventsScreen.kt
- [ ] Create or verify `ParentEventsViewModel.kt`
- [ ] Wire to `EventRegistrationRouting.kt` — GET events, POST register, GET slots
- [ ] Wire "Register" button to `viewModel.register(eventId)` → POST /api/v1/parent/events/{id}/register
- [ ] Wire event card click to expand details or show registration overlay
- [ ] Add loading/error/empty/loaded states
- [ ] Add VPullRefreshPremium
- [ ] Add VStaggeredItem entrances
- [ ] Add 140dp bottom padding
- [ ] Add `pressScale` + `shapeMorph` to event cards
- [ ] Add registering state on "Register" button
- [ ] Compile

#### 2.6 — ParentLibraryScreen.kt
- [ ] Create or verify `ParentLibraryViewModel.kt`
- [ ] Wire to `LibraryRouting.kt` — GET books, search, POST borrow, POST return
- [ ] Wire book card click to navigate to book detail or open borrow action
- [ ] Add search functionality (search bar wired to ViewModel)
- [ ] Add loading/error/empty/loaded states
- [ ] Add VPullRefreshPremium
- [ ] Add VStaggeredItem entrances
- [ ] Add 140dp bottom padding
- [ ] Add `pressScale` + `shapeMorph` to book cards
- [ ] Compile

#### 2.7 — ParentLeaveScreen.kt
- [ ] Verify `ParentLeaveViewModel` is wired ✓
- [ ] Wire leave history card click to expand details or navigate to leave detail
- [ ] Verify leave form submission is wired to API
- [ ] Add loading/error/empty/loaded states for leave history
- [ ] Add VStaggeredItem entrances
- [ ] Add 140dp bottom padding
- [ ] Add `pressScale` + `shapeMorph` to leave history cards
- [ ] Compile

#### 2.8 — ParentCalendarScreen.kt
- [ ] Create or verify `ParentCalendarViewModel.kt`
- [ ] Wire to `AcademicCalendarRouting.kt` — GET calendar events
- [ ] Wire calendar event click to navigate to event detail or open event overlay
- [ ] Add loading/error/empty/loaded states
- [ ] Add VPullRefreshPremium
- [ ] Add VStaggeredItem entrances
- [ ] Add 140dp bottom padding
- [ ] Add `pressScale` + `shapeMorph` to event cards
- [ ] Compile

#### 2.9 — ParentFeesScreen.kt
- [ ] Verify `FeeViewModel` is wired ✓
- [ ] Wire fee card click to navigate to fee detail or open payment flow
- [ ] Wire `onPayClick` to actual payment API call (POST /api/v1/parent/fees/pay)
- [ ] Add loading/error/empty/loaded states (verify existing)
- [ ] Add VStaggeredItem entrances (verify existing)
- [ ] Add 140dp bottom padding (verify existing)
- [ ] Add `pressScale` + `shapeMorph` to fee cards
- [ ] Compile

#### 2.10 — ParentSchoolDetailScreen.kt
- [ ] Verify ViewModel wiring (should use discovery or school API)
- [ ] Wire "Request Admission Info" to open compose message or school inquiry API
- [ ] Wire "Save to Favorites" to favorites API or local persistence
- [ ] Add `pressScale` + `shapeMorph` to action buttons
- [ ] Compile

#### 2.11 — ParentHomeScreen.kt
- [ ] Wire "Dismiss" alert action to `viewModel.dismissAlert(alert.id)` — add method to `ParentDashboardViewModel` and server
- [ ] Wire schedule card click to `onOpenCalendar` or navigate to timetable
- [ ] Wire hero stat card click to appropriate navigation (Pulse, attendance detail, alerts)
- [ ] Compile

#### 2.12 — ParentConversationsScreen.kt
- [ ] Wire announcement card click to navigate to announcement detail or expand
- [ ] Compile

---

### PHASE 3: VERIFY ALL REMAINING SCREENS

For each remaining screen, verify:
- ViewModel is wired and data comes from API
- No hardcoded data
- No empty onClick handlers
- Loading/error/empty/loaded states exist
- VPullRefreshPremium on data-driven screens
- VStaggeredItem entrances
- 140dp bottom padding
- pressScale + shapeMorph on all cards
- AutoMirrored icons for directional icons

Screens to verify:
- [ ] ParentPulseScreen.kt
- [ ] ParentTransportScreen.kt
- [ ] ParentHealthScreen.kt
- [ ] ParentDigitalIdCardScreen.kt
- [ ] ParentDiscoveryScreen.kt
- [ ] ParentLinkChildScreen.kt
- [ ] ParentAccountSettingsScreen.kt
- [ ] ParentReportCardScreen.kt
- [ ] ParentTimetableScreen.kt
- [ ] ParentSyllabusScreen.kt (or ParentSyllabusV2Screen.kt)
- [ ] ParentHomeworkScreen.kt
- [ ] ParentDailySummaryScreen.kt
- [ ] ParentAnnouncementsScreen.kt
- [ ] ParentTutorProgressScreen.kt
- [ ] ParentAcademicsScreen.kt (all 7 sub-tabs: Overview, Attendance, Marks, Syllabus, Homework, Quizzes, Report)

---

### PHASE 4: DEEP LINK VERIFICATION

For each deep link path in the map (see Part 1), verify:
- [ ] `parseDeepLink()` correctly maps the path to a `DeepLinkTarget`
- [ ] `ParentPortalShell.kt` `LaunchedEffect(deepLinkTarget)` correctly sets tab + overlay
- [ ] The target screen actually renders when the overlay/tab is set
- [ ] Notification taps trigger the deep link path from the server payload
- [ ] FCM push taps (from `MainActivity.kt`) trigger the same routing

Test paths to verify:
- [ ] /parent/notifications → opens notifications overlay
- [ ] /parent/messages/{threadId} → opens thread detail with correct thread
- [ ] /parent/scholarships → opens scholarships overlay
- [ ] /parent/events → opens events overlay
- [ ] /parent/transport → opens transport overlay
- [ ] /parent/library → opens library overlay
- [ ] /parent/health → opens health overlay
- [ ] /parent/pulse → opens pulse overlay
- [ ] /parent/id-card → opens digital ID overlay
- [ ] /parent/tutor → opens tutor chat
- [ ] /parent/leave → opens leave overlay
- [ ] /parent/calendar → opens calendar overlay
- [ ] /parent/academics/marks → opens academics tab on marks sub-tab
- [ ] /parent/academics/attendance → opens academics tab on attendance sub-tab
- [ ] /parent/academics/quizzes → opens academics tab on quizzes sub-tab
- [ ] /parent/report-card → opens report card overlay
- [ ] /parent/timetable → opens timetable overlay
- [ ] /parent/account-settings → opens account settings overlay
- [ ] /parent/link-child → opens link child overlay

---

### PHASE 5: MISSING FEATURE SCREENS

If any backend endpoint exists but has NO frontend screen, create one:
- [ ] Notification Preferences — `NotificationPreferencesRouting.kt` exists but no parent screen. Create `ParentNotificationPreferencesScreen.kt` as an overlay, wire to ViewModel, add deep link /parent/notification-preferences
- [ ] Any other backend route without a frontend screen — identify and create

---

### PHASE 6: FINAL COMPILATION & VERIFICATION

- [ ] Run full build: `./gradlew :composeApp:compileDevDebugKotlinAndroid :shared:compileKotlinJvm :server:compileKotlin`
- [ ] Zero compilation errors
- [ ] Zero compilation warnings
- [ ] Grep for `onClick = {}` in parent screens — must return zero results
- [ ] Grep for `clickable.*\{ *\}` in parent screens — must return zero results (except decorative elements)
- [ ] Grep for `listOf(` in parent screens — verify each is UI-only (filter labels, weekday names) not data
- [ ] Grep for `mutableStateListOf` in parent screens — verify each is UI-only state (selected filter, expanded state) not data
- [ ] Grep for `hardcoded|mockData|fakeData|dummyData|sampleData` in parent screens — must return zero results
- [ ] Verify every screen has loading/error/empty/loaded states
- [ ] Verify every data-driven screen has VPullRefreshPremium
- [ ] Verify every scrollable screen has 140dp bottom padding
- [ ] Verify every card has pressScale + shapeMorph
- [ ] Verify every notification tap navigates via deep link

---

## SELF-REVIEW RITUAL (RUN AFTER EACH SCREEN)

After completing each screen, answer these questions. If ANY answer is "No", fix before moving on:

1. Does ALL data in this screen come from a ViewModel backed by a real API?
2. Are there ANY hardcoded lists, strings, stats, or mock data?
3. Does EVERY button/card with an onClick have a real action (not `{}`)?
4. Does the screen have loading (SkeletonCard), error (ErrorStateCard), empty (EmptyStateCard), and loaded states?
5. Does the screen have VPullRefreshPremium (if data-driven)?
6. Does the screen have VStaggeredItem entrances?
7. Does the screen have 140dp bottom padding (if scrollable)?
8. Do ALL cards have pressScale + shapeMorph?
9. Are ALL directional icons using AutoMirrored variants?
10. Are ALL colors/typography/shapes using VColors/VTypography/VShapes tokens?
11. Does the screen compile without warnings?
12. If this screen is reachable via deep link, does the deep link correctly open it?

---

## FILE PATHS REFERENCE

### Frontend Screens
```
composeApp/src/commonMain/kotlin/com/littlebridge/enrollplus/ui/v2/screens/premium/parent/
├── ParentPortalShell.kt              — Shell with tabs + overlays + deep link routing
├── ParentOverlayScaffold.kt          — Shared overlay scaffold
├── ParentSharedComponents.kt         — SkeletonCard, ErrorStateCard, EmptyStateCard, FeatureGridCard, ChildSwitcherDropdown, AttendanceCalendar, TypingIndicator
├── ParentHomeScreen.kt               — Tab 0: Home
├── ParentAcademicsScreen.kt          — Tab 1: Academics (7 sub-tabs)
├── ParentFeesScreen.kt               — Tab 2: Fees
├── ParentConversationsScreen.kt      — Tab 3: Conversations (Messages + Announcements)
├── ParentProfileScreen.kt            — Tab 4: Profile
├── ParentNotificationsScreen.kt      — Overlay: Notifications
├── ParentCalendarScreen.kt           — Overlay: Calendar
├── ParentScholarshipScreen.kt        — Overlay: Scholarships
├── ParentLeaveScreen.kt              — Overlay: Leave
├── ParentTutorChatScreen.kt          — Overlay: Tutor Chat
├── ParentTutorProgressScreen.kt      — Overlay: Tutor Progress
├── ParentLibraryScreen.kt            — Overlay: Library
├── ParentEventsScreen.kt             — Overlay: Events
├── ParentTransportScreen.kt          — Overlay: Transport
├── ParentHealthScreen.kt             — Overlay: Health
├── ParentPulseScreen.kt              — Overlay: Pulse
├── ParentDigitalIdCardScreen.kt      — Overlay: Digital ID Card
├── ParentDiscoveryScreen.kt          — Overlay: School Discovery
├── ParentSchoolDetailScreen.kt       — Overlay: School Detail
├── ParentLinkChildScreen.kt          — Overlay: Link Child
├── ParentAccountSettingsScreen.kt    — Overlay: Account Settings
├── ParentComposeMessageScreen.kt     — Overlay: Compose Message
├── ParentThreadDetailScreen.kt       — Overlay: Thread Detail
├── ParentQuizzesScreen.kt            — Overlay/Sub-tab: Quizzes
├── ParentQuizDetailScreen.kt         — Overlay: Quiz Detail
├── ParentReportCardScreen.kt         — Overlay: Report Card
├── ParentTimetableScreen.kt          — Overlay: Timetable
├── ParentSyllabusScreen.kt           — Overlay/Sub-tab: Syllabus
├── ParentHomeworkScreen.kt           — Overlay/Sub-tab: Homework
├── ParentDailySummaryScreen.kt       — Overlay: Daily Summary
└── ParentAnnouncementsScreen.kt      — Overlay: Announcements
```

### Shared ViewModels
```
shared/src/commonMain/kotlin/com/littlebridge/enrollplus/feature/parent/presentation/
├── ParentDashboardViewModel.kt       ✓ exists
├── ParentAcademicsViewModel.kt       ✓ exists
├── ParentAnnouncementViewModel.kt    ✓ exists
├── ParentHomeViewModel.kt            ✓ exists
├── ParentLeaveViewModel.kt           ✓ exists
├── ParentMessageViewModel.kt         ✓ exists
├── ParentProfileViewModel.kt         ✓ exists
├── ParentPulseViewModel.kt           ✓ exists
├── ParentTutorViewModel.kt           ✗ CREATE
├── ParentTrackProgressViewModel.kt   ✗ CREATE
├── ParentEventsViewModel.kt          ✗ CREATE (or verify)
├── ParentScholarshipViewModel.kt     ✗ CREATE (or verify)
├── ParentLibraryViewModel.kt         ✗ CREATE (or verify)
├── ParentTransportViewModel.kt       ✗ CREATE (or verify)
├── ParentHealthViewModel.kt          ✗ CREATE (or verify)
├── ParentDigitalIdViewModel.kt       ✗ CREATE (or verify)
├── ParentCalendarViewModel.kt        ✗ CREATE (or verify)
├── ParentDiscoveryViewModel.kt       ✗ CREATE (or verify)
└── ParentNotificationPreferencesViewModel.kt ✗ CREATE
```

### Shared API + Repository
```
shared/src/commonMain/kotlin/com/littlebridge/enrollplus/feature/parent/data/
├── remote/ParentApi.kt               — Add missing endpoint methods
├── repository/ParentRepositoryImpl.kt — Add missing implementations
└── domain/repository/ParentRepository.kt — Add missing interfaces
```

### Server Routes
```
server/src/main/kotlin/com/littlebridge/enrollplus/feature/
├── parent/ParentAcademicsRouting.kt     — attendance, marks, syllabus, quizzes
├── parent/ParentDashboardRouting.kt     — dashboard data
├── parent/ParentFeesRouting.kt          — fees + pay
├── parent/ParentLeaveRouting.kt         — leave requests
├── parent/ParentLinkRouting.kt          — link child
├── parent/TrackProgressRouting.kt       — badges, competencies, play indicators
├── user/ParentRouting.kt               — announcements, notifications, calendar
├── user/ParentMessagesRouting.kt        — messaging
├── ai/AiRouting.kt                      — AI tutor
├── scholarship/ScholarshipRouting.kt    — scholarships
├── transport/TransportRouting.kt        — transport
├── library/LibraryRouting.kt            — library
├── event/EventRegistrationRouting.kt    — events
├── health/HealthRouting.kt              — health
├── idcard/IdCardRouting.kt              — digital ID
├── pulse/PulseRouting.kt                — pulse
├── notification/api/NotificationRouting.kt — notifications
├── notifications/NotificationPreferencesRouting.kt — notification preferences
└── calendar/AcademicCalendarRouting.kt  — calendar events
```

### Navigation
```
composeApp/src/commonMain/kotlin/com/littlebridge/enrollplus/ui/v2/navigation/
└── NavGraphV2.kt    — DeepLinkTarget sealed class + parseDeepLink() function
```

### Android Notification Handling
```
composeApp/src/androidMain/kotlin/com/littlebridge/enrollplus/
├── notification/VidyaPrayagFirebaseMessagingService.kt — FCM receiver
├── notification/NotificationManagerHelper.kt           — System tray + PendingIntent
└── MainActivity.kt                                     — Extracts deepLink from intent
```

---

## FINAL OUTPUT CHECKLIST

When you are done, the following must be true:

- [ ] **ZERO** hardcoded data in any parent portal screen
- [ ] **ZERO** empty onClick handlers (`onClick = {}`)
- [ ] **EVERY** notification tap navigates to the correct screen via deep link
- [ ] **EVERY** form/button is wired to a real API endpoint via ViewModel
- [ ] **EVERY** card is tappable with pressScale + shapeMorph
- [ ] **EVERY** screen has loading/error/empty/loaded states
- [ ] **EVERY** data-driven screen has VPullRefreshPremium
- [ ] **EVERY** scrollable screen has 140dp bottom padding
- [ ] **EVERY** content entrance uses VStaggeredItem
- [ ] **ALL** colors/typography/shapes use design tokens
- [ ] **ALL** directional icons use AutoMirrored variants
- [ ] **ALL** deep link paths navigate correctly
- [ ] **ALL** backend endpoints have corresponding frontend screens
- [ ] **ALL** frontend screens have corresponding backend endpoints
- [ ] **BUILD** compiles with zero errors and zero warnings
- [ ] **NO** TODOs, FIXMEs, or placeholder comments in production code
