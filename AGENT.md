# AGENT.md — Enroll+ Full-Stack Feature Delivery Protocol

> **MANDATORY READING FOR ALL AI AGENTS (Cascade, Devin, etc.)**
> Before writing ANY code for Enroll+, you MUST read this file end-to-end and follow
> the FULL-STACK FEATURE COMPLETION GRAPH (§3) for every feature, bugfix, or change.
> If you skip any node in the graph, you are INCOMPLETE and must loop back.

---

## 1. PROJECT OVERVIEW

**Enroll+ (VidyaPrayag)** is a Kotlin Multiplatform (KMP) school management platform.

### Tech Stack
- **Frontend**: Compose Multiplatform (Android, iOS, Web/Wasm, Desktop/JVM)
- **Shared Module**: Business logic, API clients, domain models, ViewModels (Koin DI)
- **Backend**: Ktor server (Kotlin)
- **Database**: PostgreSQL (HikariCP connection pool, Exposed ORM)
- **Navigation**: Role-driven state machine (`NavGraphV2`) — Parent, Teacher, SchoolAdmin, SuperAdmin
- **DI**: Koin (shared module + compose app)
- **Networking**: Ktor HttpClient with JWT auth, token refresh, `safeApiCall` wrapper
- **Image Loading**: Coil3 with Ktor fetcher

### Module Map
```
Database Layer     →  database/migrations/*.sql
                       server/src/main/kotlin/.../db/DatabaseFactory.kt

Backend Layer      →  server/src/main/kotlin/.../feature/{feature}/
                       ├── {Feature}Router.kt        (Ktor routes)
                       ├── {Feature}Service.kt       (business logic)
                       ├── {Feature}Dao.kt           (database access)
                       └── {Feature}Models.kt        (request/response DTOs)

API Client Layer   →  shared/src/commonMain/kotlin/.../feature/{feature}/data/remote/
                       └── {Feature}Api.kt           (HttpClient calls via safeApiCall)

Domain Layer       →  shared/src/commonMain/kotlin/.../feature/{feature}/domain/
                       ├── model/                    (domain models)
                       └── repository/               (repository interfaces)

Presentation Layer →  shared/src/commonMain/kotlin/.../feature/{feature}/presentation/
                       └── {Feature}ViewModel.kt     (state management)

DI Layer           →  shared/src/commonMain/kotlin/.../di/Koin.kt
                       (register API, repository, ViewModel)

UI Layer           →  composeApp/src/commonMain/kotlin/.../ui/v2/screens/{role}/
                       └── {Feature}Screen.kt        (Composable screens)

Navigation Layer   →  composeApp/src/commonMain/kotlin/.../ui/v2/navigation/NavGraphV2.kt
                       (role-driven routing, deep-link parsing)
```

### Roles
- **Parent** — views child data, fees, academics, communications
- **Teacher** — manages classes, homework, attendance, reports
- **SchoolAdmin** — manages school settings, students, staff, fees, analytics
- **SuperAdmin** — platform-wide administration

---

## 2. THE PROBLEM THIS FILE SOLVES

### Recurring Failure Patterns (DO NOT REPEAT THESE)

| # | Failure | Description |
|---|---------|-------------|
| F1 | **Ghost Backend** | Backend endpoint exists but no UI screen or button to trigger it |
| F2 | **Dead Button** | UI button exists but `onClick` is empty or not wired to ViewModel/API |
| F3 | **Invisible Button** | Button rendered but clipped/off-screen due to padding, height, or layout issues |
| F4 | **One-Way Workflow** | Forward button exists (Continue/Submit) but no Back/Cancel/Reset button |
| F5 | **Missing Loading State** | API call triggered but no loading indicator, error toast, or success feedback |
| F6 | **Orphan Screen** | Screen exists but no navigation route leads to it |
| F7 | **Stale Data** | Screen shows data but never refreshes after create/update/delete operations |
| F8 | **Half-Stack Feature** | Database table + backend route exist but no API client, no ViewModel, no UI |
| F9 | **Untyped Navigation** | Screen navigates to a route string that doesn't exist in NavGraphV2 |
| F10 | **No Empty State** | Screen shows blank when API returns empty list — no "No data" message |

---

## 3. FULL-STACK FEATURE COMPLETION GRAPH (GOD MODE)

> This is a **directed acyclic graph (DAG)**. Every node must be completed.
> You cannot skip nodes. You cannot mark a feature "done" until ALL nodes pass.
> After completing the forward pass (DB → UI), you MUST run the **reverse verification pass** (UI → DB).

### Phase 1: PLANNING (Before Writing Any Code)

```
[PLAN-1] Define the feature in ONE sentence
         → What does the user DO? What role? What happens?

[PLAN-2] Identify ALL affected layers
         → List every file you will create or modify across all 7 layers

[PLAN-3] Map the user workflow
         → Entry point → Action → Success state → Error state → Exit/back
         → Draw the screen flow: ScreenA → ScreenB → ScreenC
         → For EACH screen: what buttons? What loading? What empty state?

[PLAN-4] Identify the navigation path
         → How does the user REACH this feature from their portal home?
         → What deep-link path? (e.g., /parent/fees, /teacher/homework)
         → Is it a tab, an overlay, a standalone screen, or a nested flow?
```

### Phase 2: FORWARD PASS — Database → Backend → API → ViewModel → UI

```
[DB-1] Create SQL migration
       → database/migrations/setup_{feature}.sql
       → Include CREATE TABLE, indexes, constraints, seed data
       → Reference existing migration patterns

[DB-2] Register table in DatabaseFactory
       → server/src/main/kotlin/.../db/DatabaseFactory.kt
       → Add table to schema creation/migration list

[BACKEND-1] Create DTOs / Models
       → server/.../feature/{feature}/{Feature}Models.kt
       → Request and response serializable classes

[BACKEND-2] Create DAO
       → server/.../feature/{feature}/{Feature}Dao.kt
       → All CRUD operations using Exposed

[BACKEND-3] Create Service
       → server/.../feature/{feature}/{Feature}Service.kt
       → Business logic, validation, authorization checks

[BACKEND-4] Create Router
       → server/.../feature/{feature}/{Feature}Router.kt
       → Ktor routes: GET/POST/PUT/DELETE
       → JWT auth guard, role check, error handling
       → Mount in Application.kt

[BACKEND-5] Test endpoint manually
       → Verify route is registered, responds to requests
       → Check auth/role enforcement

[API-1] Create domain models (shared)
       → shared/.../feature/{feature}/domain/model/{Feature}Models.kt
       → Match backend response structure

[API-2] Create API client
       → shared/.../feature/{feature}/data/remote/{Feature}Api.kt
       → Use safeApiCall, proper URL construction, auth header
       → Match every backend endpoint

[API-3] Create repository (if needed)
       → shared/.../feature/{feature}/domain/repository/{Feature}Repository.kt
       → Wraps API calls, handles caching/offline

[DI-1] Register in Koin
       → shared/.../di/Koin.kt
       → Register API, repository, ViewModel
       → VERIFY: every class used in UI is registered

[VM-1] Create ViewModel
       → shared/.../feature/{feature}/presentation/{Feature}ViewModel.kt
       → StateFlow for UI state (loading, success, error, empty)
       → Expose functions for every user action
       → Handle loading/error/empty states explicitly

[UI-1] Create Composable screen
       → composeApp/.../ui/v2/screens/{role}/{Feature}Screen.kt
       → Follow existing VTheme design system
       → Use VColors, VSpacing, VTypography from ui/v2/theme/

[UI-2] Wire UI to ViewModel
       → Collect state with collectAsState()
       → Every button onClick → ViewModel function call
       → Show loading indicator during API calls
       → Show error message on failure
       → Show empty state when list is empty
       → Show success confirmation after action

[UI-3] Add navigation route
       → Add to NavGraphV2.kt or the appropriate portal's internal navigation
       → Add deep-link parsing if applicable
       → VERIFY: the route string matches what you navigate to

[UI-4] Add entry point button
       → Add button/menu item/tab in the portal that navigates to this screen
       → VERIFY: button is visible, accessible, properly sized
```

### Phase 3: REVERSE VERIFICATION PASS — UI → Database

> **THIS IS THE MOST CRITICAL PHASE.**
> You must trace the complete path from user action to database and back.
> If ANY link is broken, you must fix it before declaring the feature complete.

```
[REV-1] UI Accessibility & Visibility Check
        → Is the entry button VISIBLE on screen? (not clipped, not off-screen)
        → Is the button reachable by scroll? (not hidden behind fixed elements)
        → Is the button tappable? (adequate touch target ≥ 48dp)
        → Is there enough padding? (no edge-kissing, no overlap)
        → Does the screen fit on a small phone (360x640 dp)?
        → Does the screen handle landscape orientation?
        → Test with fontScale = 1.3 (accessibility scale)

        *** NOT SHADOWED / NOT CUT OFF CHECKS ***
        → Is the button BELOW the app's bottom navigation bar? (not hidden behind it)
        → Is the button ABOVE the system navigation bar? (not cut by gesture area)
        → Is the button not overlapped by any Floating Action Button (FAB)?
        → When the keyboard (IME) opens, is the button still visible? (use imePadding)
        → Is the button not covered by any bottom sheet / dialog / overlay?
        → Is the button not shadowed by any sticky/fixed header or footer?
        → Is the button not cut by screen rounded corners (edge display)?
        → Is the button not hidden behind any system UI overlay (cutout, notch)?
        → Does the button have adequate z-index? (not painted under another composable)
        → Is the button within the safe content area? (statusBarsPadding + navigationBarsPadding + imePadding applied)
        → If button is in a LazyColumn — is it visible when scrolled to it? (not clipped by viewport)
        → If button is in a bottom bar — does the bottom bar have navigationBarsPadding?
        → If button is in a top bar — does the top bar have statusBarsPadding?
        → If screen has a FAB — does content have bottom padding to avoid FAB overlap?
        → If screen has a bottom nav bar — does content have bottom padding equal to bar height?

        *** PARTIAL SCROLL / FIXED HEADER + FIXED FOOTER CHECKS ***
        → If only a MIDDLE section scrolls (header fixed on top, footer fixed on bottom):
          → Does the scrollable middle section have topPadding == fixedHeaderHeight?
          → Does the scrollable middle section have bottomPadding == fixedFooterHeight + navBarInset?
          → Can the LAST item in the scroll section be fully scrolled into view? (not hidden behind footer)
          → Can the FIRST item in the scroll section be fully scrolled into view? (not hidden behind header)
          → Are buttons inside the scroll section reachable by scrolling? (not permanently trapped behind fixed footer)
          → Are buttons in the fixed footer properly padded? (not overlapping with scroll content)
          → Are buttons in the fixed header properly padded? (not overlapping with scroll content)
          → Does the fixed footer have navigationBarsPadding? (not cut by system gesture bar)
          → Does the fixed header have statusBarsPadding? (not cut by notch/status bar)
          → When keyboard opens on a form inside the scroll section — does the fixed footer move up? (imePadding on footer OR footer is above IME)
          → Is the scroll section using Modifier.weight(1f) inside a Column? (so it fills space between header and footer)
          → Is the scroll section NOT using fillMaxSize? (which would ignore the fixed header/footer heights)
          → If header has tabs/filters — does scrolling the content NOT move the tabs? (tabs stay fixed)
          → If footer has Submit/Cancel — are they ALWAYS visible regardless of scroll position?
          → Is there NO double scrollbar? (only the middle section scrolls, not the whole screen)
          → Is the scroll section's contentPadding(top, bottom) set to header/footer height? (so first/last items aren't clipped)

[REV-2] Button Wiring Check
        → Trace EVERY button's onClick → does it call a ViewModel function?
        → Trace EVERY ViewModel function → does it call the API?
        → Trace EVERY API call → does it hit a real backend endpoint?
        → Trace EVERY backend endpoint → does it query the database?
        → NO DEAD BUTTONS. NO ORPHAN ENDPOINTS.

[REV-3] Workflow Completeness Check
        → Can the user ENTER this feature? (entry point exists and works)
        → Can the user go BACK from every screen? (back button, back gesture)
        → Can the user CANCEL an in-progress action? (cancel/dismiss button)
        → After SUCCESS, can the user return to where they started?
        → After ERROR, can the user RETRY?
        → Is there a CONFIRMATION step for destructive actions? (delete, submit)
        → Are there BOTH "Submit/Save" AND "Cancel/Back" on every form?

[REV-4] State Management Check
        → Loading state: spinner/skeleton shown during API call?
        → Error state: user-friendly message + retry button?
        → Empty state: "No data yet" message + call-to-action?
        → Success state: confirmation toast/snackbar + navigation?
        → Does the screen REFRESH data after create/update/delete?
        → Is stale data avoided? (re-fetch on screen entry, after mutations)

[REV-5] Data Flow Integrity Check
        → UI shows data from ViewModel state (not direct API calls in Composable)
        → ViewModel calls API which calls backend which calls DB
        → Response from DB → backend → API → ViewModel → UI
        → No layer is skipped. No direct DB access from UI.
        → No hardcoded data in UI that should come from API.

[REV-6] Navigation Integrity Check
        → Every navigate("route") call targets a route that EXISTS
        → Every screen is reachable from at least one entry point
        → Deep-link path (if any) is parsed correctly in NavGraphV2
        → Back navigation doesn't return to auth/splash screens
        → Role-based access: correct role sees correct screens

[REV-7] DI Registration Check
        → Every ViewModel used in UI is registered in Koin
        → Every API used by ViewModel is registered in Koin
        → Every repository used by API/VM is registered in Koin
        → No runtime "No bean found" crashes

[REV-8] Build Verification
        → Code compiles without errors
        → No unresolved references
        → No missing imports
        → Run: .\gradlew.bat :composeApp:assembleDebug (or relevant target)
```

### Phase 4: MULTI-ITERATION VERIFICATION LOOP

> Run this loop UP TO 5 TIMES. If any check fails, fix and re-run.
> If all checks pass in a single iteration, the feature is COMPLETE.

```
ITERATION LOOP (max 5):
  ┌─────────────────────────────────────────────────────────┐
  │  for i in 1..5:                                         │
  │    run REV-1 through REV-8                              │
  │    if ALL pass → FEATURE COMPLETE ✓                     │
  │    else → fix failures, continue to next iteration      │
  │                                                         │
  │  if 5 iterations exhausted → STOP and report            │
  │  the specific failing checks to the user                │
  └─────────────────────────────────────────────────────────┘
```

---

## 4. UI/UX MANDATES (NON-NEGOTIABLE)

### 4.1 Button Accessibility & Visibility Rules
- **Minimum touch target**: 48dp × 48dp
- **Padding**: minimum 16dp from screen edges, 12dp between buttons
- **Visibility**: button must be within the visible viewport — use `verticalScroll` or `LazyColumn` for long forms

*** NOT SHADOWED / NOT CUT OFF RULES ***
- **Bottom nav bar overlap**: if the screen has a bottom navigation bar (e.g., ParentPortalV2 tabs), ALL content above it MUST have `bottomPadding >= navBarHeight`. No button may be rendered behind the bottom nav bar.
- **System nav bar / gesture area**: apply `navigationBarsPadding()` to any bottom bar or bottom-pinned button so it is NOT cut off by the system gesture inset.
- **FAB overlap**: if a Floating Action Button is present, content MUST have `bottomPadding` that accounts for FAB size + margin. No button may be hidden behind the FAB.
- **Keyboard (IME) visibility**: any Submit/Save button in a form MUST use `imePadding()` so it stays visible when the keyboard opens. NEVER let a button be hidden by the IME.
- **Bottom sheet / dialog overlay**: when a bottom sheet is open, underlying buttons MUST be either disabled or fully covered by the scrim — no partial shadowing.
- **Sticky header/footer**: if a sticky header or footer is used, scrollable content MUST have matching top/bottom padding so buttons are not hidden behind the sticky element.
- **Screen rounded corners**: buttons near screen edges MUST respect display cutout insets (`displayCutoutPadding()` on Android) so they are not clipped by curved corners.
- **Notch / cutout**: apply `statusBarsPadding()` to top-bar buttons so they are NOT hidden behind a notch or punch-hole camera.
- **Z-index / layering**: ensure buttons are painted ABOVE background content. Use `Modifier.zIndex()` if needed. No button should be painted under another composable.
- **Safe content area**: the root container of every screen MUST apply `Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()` (or consume insets via `Scaffold`). Individual forms should additionally apply `imePadding()`.
- **LazyColumn / LazyRow clipping**: buttons inside lazy lists MUST have adequate `contentPadding` on the list so the last item's button is fully visible when scrolled to the end.
- **Bottom action bar**: if using a pinned bottom action bar (Submit/Cancel), it MUST have `navigationBarsPadding()` AND the scrollable content above it MUST have `bottomPadding >= actionBarHeight + navBarInset`.
- **Top bar**: top bar buttons MUST have `statusBarsPadding()` applied to the top bar container, not the button itself, to avoid double-padding.

*** PARTIAL SCROLL / FIXED HEADER + FIXED FOOTER RULES ***
- **Pattern**: when a screen has a fixed header (title bar, filter bar, tab row) AND a fixed footer (action bar, bottom nav) with only the middle section scrollable, use this structure:
  ```kotlin
  Column(Modifier.fillMaxSize()) {
      // FIXED HEADER — does NOT scroll
      FixedHeaderBar(Modifier)  // has statusBarsPadding()

      // SCROLLABLE MIDDLE — fills remaining space
      Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
          // content here
      }

      // FIXED FOOTER — does NOT scroll
      FixedActionBar(Modifier)  // has navigationBarsPadding()
  }
  ```
- **`weight(1f)` is MANDATORY**: the scrollable middle MUST use `Modifier.weight(1f)` so it fills exactly the space between the fixed header and footer — NOT `fillMaxSize` (which would overlap the footer).
- **Content padding**: the scrollable middle MUST have `contentPadding(top = headerHeight, bottom = footerHeight + navBarInset)` if using LazyColumn, OR `padding(top = ..., bottom = ...)` if using `verticalScroll` — so the first item is not hidden behind the header and the last item is not hidden behind the footer.
- **Footer always visible**: Submit/Cancel/Save buttons in the fixed footer MUST be visible at ALL scroll positions — they must NOT scroll away.
- **Header always visible**: title/back button/filter tabs in the fixed header MUST stay fixed at ALL scroll positions — they must NOT scroll away.
- **No nested scroll conflict**: only ONE scroll container should exist between header and footer. Do NOT wrap the entire screen (including header+footer) in a `verticalScroll` — that defeats the purpose of fixed sections.
- **Keyboard handling**: when keyboard opens, the fixed footer MUST either move above the keyboard (`imePadding()`) or the scroll section must expand to show the focused field. NEVER let the footer be hidden by the keyboard.
- **Small screen test**: on 360×640 dp, verify the scroll section still has enough visible height (at least 200dp) after header and footer are rendered. If header+footer consume too much, consider collapsible header or reduce footer height.
- **Filter/tab bar in header**: if the fixed header contains filter chips or tabs, scrolling the content MUST NOT move them. They must remain interactive at all times.
- **Sticky vs fixed**: a STICKY header scrolls with content then sticks to top — a FIXED header never scrolls at all. Decide which pattern you need and implement it correctly. Do NOT accidentally make a sticky header when you need a fixed one.

### 4.2 Workflow Completeness Rules
- **Every form screen MUST have**: Submit button + Back/Cancel button
- **Every multi-step flow MUST have**: Next button + Previous button + Cancel button
- **Every list screen MUST have**: Pull-to-refresh OR refresh button + empty state
- **Every detail screen MUST have**: Back button in top bar
- **Every destructive action MUST have**: Confirmation dialog with "Confirm" + "Cancel"
- **Every success state MUST have**: Visual feedback (snackbar/toast) + navigation to next logical screen
- **Every error state MUST have**: Error message + Retry button (not just a log)

### 4.3 Loading & State Rules
- **Loading**: show `CircularProgressIndicator` or skeleton — NEVER blank screen
- **Error**: show error text + retry button — NEVER silent failure
- **Empty**: show "No {items} found" + action button (e.g., "Add first {item}")
- **Success**: show snackbar/toast + auto-navigate or dismiss

### 4.4 Screen Layout Rules
- **Always wrap content in `verticalScroll`** or use `LazyColumn` — never assume screen height
- **Use `fillMaxSize`** for root container, then `verticalScroll` for content
- **Bottom action buttons**: use `BottomActionBar` pattern or pin to bottom with `Spacer(Modifier.weight(1f))`
- **Test on small screen**: mentally verify layout works at 360×640 dp
- **Safe area**: always apply `statusBarsPadding()` and `navigationBarsPadding()` where appropriate

*** LAYOUT EDGE-CASE RULES ***
- **Keyboard**: form screens MUST use `imePadding()` on the scroll container or the bottom bar so the Submit button is NEVER hidden by the keyboard
- **Bottom nav + scroll**: when screen has BOTH a bottom nav bar and scrollable content, content `bottomPadding` = `navBarHeight + navBarInset + 8dp` minimum
- **FAB + list**: when a FAB is present over a LazyColumn, add `contentPadding(bottom = 80dp)` to the list so the last item is not hidden behind the FAB
- **Scaffold insets**: prefer `Scaffold` with `contentWindowInsets` to automatically handle status bar, nav bar, and IME insets — but VERIFY the inner content padding is consumed correctly
- **Nested scroll**: if a horizontally scrollable row is inside a vertical scroll, ensure the horizontal row has adequate vertical padding so buttons are not clipped at the top/bottom of the viewport
- **Dialog/BottomSheet**: buttons inside dialogs MUST be fully visible — use `Dialog` with proper `usePlatformDefaultWidth = false` and `imePadding()` for forms inside dialogs
- **Tab row + content**: if a TabRow is used, content below it MUST start after the tab height — no button should be hidden behind the tab row when scrolling

*** PARTIAL SCROLL LAYOUT RULES ***
- **Fixed header + scroll + fixed footer**: use `Column { Header; LazyColumn(Modifier.weight(1f)); Footer }` — NEVER wrap the whole column in `verticalScroll`
- **Header height tracking**: if header height is dynamic (e.g., collapsible), use `onSizeChanged` to capture height and apply it as `contentPadding(top = ...)` to the scroll section
- **Footer height tracking**: if footer height is dynamic (e.g., multi-line button text), use `onSizeChanged` to capture height and apply it as `contentPadding(bottom = ...)` to the scroll section
- **Scroll-to-end visibility**: the LAST item in the scroll section (especially action buttons) MUST be fully visible when scrolled to the end — add `contentPadding(bottom = footerHeight + 16dp)` to ensure this
- **Scroll-to-top visibility**: the FIRST item in the scroll section MUST be fully visible when scrolled to the top — add `contentPadding(top = headerHeight + 8dp)` to ensure this
- **Overscroll / bounce**: on the scroll section, ensure `flingBehavior` is smooth and there is no jarring stop behind the fixed header/footer
- **Scaffold alternative**: prefer `Scaffold(topBar = { Header }, bottomBar = { Footer }) { padding -> Content(Modifier.padding(padding)) }` — this automatically handles insets and sizing. Use this when possible instead of manual Column+weight.
- **Collapsed header on scroll**: if implementing a collapsing header (header shrinks on scroll), use `nestedScroll` with `enterAlwaysScrollBehavior` — do NOT manually animate header height based on scroll offset (janky and error-prone)
- **Content under transparent header**: if the header is transparent/translucent, the scroll content MUST have top padding so text is not obscured behind the header on first scroll position

---

## 5. FILE LOCATION CHEAT SHEET

| Layer | Path Pattern | Example |
|-------|-------------|---------|
| SQL Migration | `database/migrations/setup_{feature}.sql` | `setup_fee_salary_management.sql` |
| Server Router | `server/src/main/kotlin/.../feature/{feature}/{Feature}Router.kt` | `TutorRouter.kt` |
| Server Service | `server/src/main/kotlin/.../feature/{feature}/{Feature}Service.kt` | `TutorService.kt` |
| Server DAO | `server/src/main/kotlin/.../feature/{feature}/{Feature}Dao.kt` | `TutorDao.kt` |
| Server Models | `server/src/main/kotlin/.../feature/{feature}/{Feature}Models.kt` | `TutorModels.kt` |
| Server Registration | `server/src/main/kotlin/.../Application.kt` | Mount `featureRouting()` |
| Shared API | `shared/src/commonMain/kotlin/.../feature/{feature}/data/remote/{Feature}Api.kt` | `TutorApi.kt` |
| Shared Domain | `shared/src/commonMain/kotlin/.../feature/{feature}/domain/model/` | `TutorModels.kt` |
| Shared VM | `shared/src/commonMain/kotlin/.../feature/{feature}/presentation/{Feature}ViewModel.kt` | `TutorViewModel.kt` |
| Shared DI | `shared/src/commonMain/kotlin/.../di/Koin.kt` | Register in `featureModule` |
| UI Screen | `composeApp/src/commonMain/kotlin/.../ui/v2/screens/{role}/{Feature}Screen.kt` | `TutorScreen.kt` |
| Navigation | `composeApp/src/commonMain/kotlin/.../ui/v2/navigation/NavGraphV2.kt` | Add route/deep-link |
| UI Components | `composeApp/src/commonMain/kotlin/.../ui/v2/components/` | Reusable components |
| UI Theme | `composeApp/src/commonMain/kotlin/.../ui/v2/theme/` | VColors, VTheme, VThemeDef |

---

## 6. CHECKLIST TEMPLATE (Copy for every feature)

```markdown
## Feature: [FEATURE NAME]

### Planning
- [ ] PLAN-1: Feature defined in one sentence
- [ ] PLAN-2: All affected layers identified
- [ ] PLAN-3: User workflow mapped (entry → action → success → error → exit)
- [ ] PLAN-4: Navigation path identified

### Forward Pass (DB → UI)
- [ ] DB-1: SQL migration created
- [ ] DB-2: Table registered in DatabaseFactory
- [ ] BACKEND-1: DTOs/models created
- [ ] BACKEND-2: DAO created
- [ ] BACKEND-3: Service created
- [ ] BACKEND-4: Router created + mounted in Application.kt
- [ ] BACKEND-5: Endpoint tested
- [ ] API-1: Domain models created (shared)
- [ ] API-2: API client created
- [ ] API-3: Repository created (if needed)
- [ ] DI-1: Registered in Koin
- [ ] VM-1: ViewModel created with all states
- [ ] UI-1: Composable screen created
- [ ] UI-2: UI wired to ViewModel (all buttons, all states)
- [ ] UI-3: Navigation route added
- [ ] UI-4: Entry point button added in portal

### Reverse Verification (UI → DB)
- [ ] REV-1: UI accessibility verified (visible, tappable, fits small screen)
- [ ] REV-2: Every button wired to ViewModel → API → backend → DB
- [ ] REV-3: Workflow complete (enter, back, cancel, retry, confirm)
- [ ] REV-4: All states handled (loading, error, empty, success)
- [ ] REV-5: Data flows through all layers correctly
- [ ] REV-6: Navigation routes exist and are reachable
- [ ] REV-7: DI registrations complete (no missing beans)
- [ ] REV-8: Build compiles without errors

### Iteration Loop
- [ ] Iteration 1: [PASS/FAIL — list failures]
- [ ] Iteration 2: [PASS/FAIL — list failures]
- [ ] Iteration 3: [PASS/FAIL — list failures]
- [ ] Iteration 4: [PASS/FAIL — list failures]
- [ ] Iteration 5: [PASS/FAIL — list failures]
- [ ] FEATURE COMPLETE ✓
```

---

## 7. ANTI-PATTERNS — DO NOT DO THESE

1. **DO NOT** create a backend endpoint without creating the UI to call it
2. **DO NOT** create a UI button without wiring its `onClick` to a ViewModel function
3. **DO NOT** create a screen without adding a navigation route to reach it
4. **DO NOT** create a form with only a Submit button — always add Back/Cancel
5. **DO NOT** skip loading/error/empty states — every API call needs all three
6. **DO NOT** hardcode data in Composables that should come from the API
7. **DO NOT** forget to register classes in Koin — runtime crashes are unacceptable
8. **DO NOT** create buttons with insufficient touch targets or off-screen positioning
9. **DO NOT** skip the reverse verification pass — the forward pass alone is NEVER enough
10. **DO NOT** declare a feature complete without running the iteration loop

---

## 8. QUICK REFERENCE — EXISTING PATTERNS

### API Client Pattern (see `TutorApi.kt`)
```kotlin
suspend fun getThings(token: String, id: String): NetworkResult<ThingsResponse> {
    return safeApiCall {
        client.get(getUrl("api/v1/feature/things/$id")) {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
    }
}
```

### ViewModel State Pattern
```kotlin
sealed class FeatureState {
    object Loading : FeatureState()
    data class Success(val data: ...) : FeatureState()
    data class Error(val message: String) : FeatureState()
    object Empty : FeatureState()
}
```

### Screen State Handling Pattern
```kotlin
when (val state = viewModel.state.collectAsState().value) {
    is Loading -> LoadingIndicator()
    is Success -> ContentList(state.data)
    is Error -> ErrorView(state.message, onRetry = { viewModel.retry() })
    is Empty -> EmptyState(onAdd = { viewModel.addAction() })
}
```

### Navigation Pattern (NavGraphV2)
```kotlin
// Add to DeepLinkTarget sealed class
data class FeatureScreen(override val role: EntryRole, val params: Map<String, String> = emptyMap()) : DeepLinkTarget()

// Add to parseDeepLink
"feature" -> DeepLinkTarget.SchoolScreen(currentRole, "feature")

// Add to AuthedFlow / portal navigation
```

---

## 9. ENFORCEMENT

This file is the **single source of truth** for feature delivery in Enroll+.
Any AI agent working on this codebase MUST:

1. Read this file before starting any feature work
2. Follow the Full-Stack Feature Completion Graph (§3) for every change
3. Complete the checklist (§6) and include it in the PR/commit description
4. Run the reverse verification pass (Phase 3) — no exceptions
5. Run the iteration loop (Phase 4) until all checks pass or 5 iterations are exhausted

**If you are an AI agent and you did not follow this protocol, your work is INCOMPLETE.**
