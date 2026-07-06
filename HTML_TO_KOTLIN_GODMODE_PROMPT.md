# GOD MODE PROMPT — HTML Prototype to Kotlin Compose Multiplatform Conversion

> **Purpose:** Convert the HTML interactive prototype in `preview/` to 100% production-ready Kotlin Compose Multiplatform code in `composeApp/` and `shared/`.
> **Rule:** Zero visual drift. Zero structural drift. Zero omitted screens. Every HTML element becomes a Compose element.

> **Project structure — READ AND UNDERSTAND BEFORE WRITING ANY CODE:**
> - **UI screens** go in `composeApp/src/commonMain/kotlin/com/littlebridge/enrollplus/ui/screens/` — organized by portal (`shared/`, `admin/`, `teacher/`, `parent/`)
> - **UI components** go in `composeApp/src/commonMain/kotlin/com/littlebridge/enrollplus/ui/components/`
> - **UI tokens** go in `composeApp/src/commonMain/kotlin/com/littlebridge/enrollplus/ui/tokens/`
> - **ViewModels** go in `shared/src/commonMain/kotlin/com/littlebridge/enrollplus/feature/<feature>/presentation/`
> - **Repositories & APIs** go in `shared/src/commonMain/kotlin/com/littlebridge/enrollplus/feature/<feature>/data/`
> - **Domain models & use cases** go in `shared/src/commonMain/kotlin/com/littlebridge/enrollplus/feature/<feature>/domain/`
> - **DI registration** is in `shared/src/commonMain/kotlin/com/littlebridge/enrollplus/core/di/Koin.kt`
> - The old `ui/v2/` folder was DELETED. You are building fresh. Do NOT reference old code.
> - `App.kt` is currently a minimal shell. You will wire screens into it as you build them.

---

## 0. BRIEF

You are converting an HTML prototype into Kotlin Compose Multiplatform code. The HTML prototype is the **single source of truth** for layout, structure, hierarchy, spacing, and flow. The Kotlin output must render identically on device.

**Do NOT:**
- Improve, optimize, or "fix" the HTML layout. If the HTML has a 2-up grid, Kotlin gets a 2-up grid. Not a 3-up, not a list, not a "better" layout.
- Skip any element. If the HTML has a badge, the Kotlin has a badge. If the HTML has a chevron, the Kotlin has a chevron.
- Reinterpret the design. You are a translator, not a designer.
- Guess. If you can't find the mapping, stop and ask.

**Do:**
- Read every `data-*` attribute. These are your Compose instructions.
- Read every CSS class. These map to Composable names.
- Read the Kotlin signature comments at the top of each screen block. These are the exact function signatures to implement.
- Follow the existing project structure (packages, naming, file organization).

---

## 1. INPUT FILES

- `preview/enrollplus-auth-prototype.html` — Splash, landing page (carousel), parent/staff auth screens (login + signup flows)
- `preview/admin.html` — Admin portal (5 tabs + 30 overlays) *(not yet built)*
- `preview/teacher.html` — Teacher portal (5 tabs + 12 overlays) *(not yet built)*
- `preview/parent.html` — Parent portal (5 tabs + 16 overlays) *(not yet built)*
- `preview/shared.html` — Splash, landing, auth, onboarding, legal *(not yet built)*
- `preview/styles.css` — Design tokens (CSS variables = Compose tokens) *(not yet built)*
- `preview/nav.js` — Navigation logic (tab switching, overlay stack, back handling) *(not yet built)*

Reference docs (READ THESE FIRST — they define the screen architecture, backend mappings, and data flow):
- `preview/ENROLLPLUS_SCREEN_ARCHITECTURE_FINAL.md` — Complete screen inventory with states, data sources, components, interactions, and consolidation notes for ALL portals (admin, teacher, parent, shared). 1897 lines. This is the authoritative screen architecture document.
- `preview/ENROLLPLUS_RESTRUCTURE_CHANGELOG_AND_BACKEND_MAPPING.md` — 22 restructure entries mapping every UI consolidation to its backend API endpoints. Covers SuperAdmin gating, shared notifications, calendar unification, messages unification, and more. Use this to determine which backend endpoints feed each screen.

> **CRITICAL:** These reference docs are NOT optional reading. They define which ViewModel feeds each screen, which API endpoints exist, and how data flows. If you skip them, you will produce hardcoded slop with no backend connection.

---

## 2. OUTPUT STRUCTURE

### 2.1 File Organization

Mirror the HTML screen sections into Kotlin files:

```
composeApp/src/commonMain/kotlin/com/littlebridge/enrollplus/ui/
├── tokens/
│   ├── VColors.kt
│   ├── VShapes.kt
│   ├── VTypography.kt
│   └── VMotion.kt
├── components/
│   ├── VCard.kt
│   ├── VButton.kt
│   ├── VInput.kt
│   ├── VBadge.kt
│   ├── VAvatar.kt
│   ├── VBackHeader.kt
│   ├── VBottomNav.kt
│   ├── VScreenScaffold.kt
│   ├── VTopTabs.kt
│   ├── VActionCard.kt
│   ├── VProgressRing.kt
│   ├── VProgressBar.kt
│   ├── VEmptyState.kt
│   ├── VComingSoon.kt
│   ├── VConfirmDialog.kt
│   ├── VSnackbar.kt
│   ├── VStateHost.kt
│   ├── VPullRefresh.kt
│   ├── VDatePicker.kt
│   ├── VTimePicker.kt
│   ├── VThemePicker.kt
│   ├── VShimmer.kt
│   ├── VCharts.kt
│   ├── VStatusDot.kt
│   ├── FilterChip.kt
│   └── CoachMarkOverlay.kt
├── navigation/
│   ├── NavGraph.kt
│   ├── DeepLinkParser.kt
│   └── BackHandler.kt
├── screens/
│   ├── admin/
│   │   ├── AdminPortalShell.kt
│   │   ├── AdminHomeScreen.kt
│   │   ├── AdminPeopleScreen.kt
│   │   ├── AdminRecordsScreen.kt
│   │   ├── AdminCommsScreen.kt
│   │   ├── AdminSettingsScreen.kt
│   │   └── overlays/
│   │       ├── NotificationsOverlay.kt
│   │       ├── AcademicCalendarPlatformOverlay.kt
│   │       ├── CreateEventWizardOverlay.kt
│   │       └── ... (30 overlay files)
│   ├── teacher/
│   │   ├── TeacherPortalShell.kt
│   │   ├── TeacherHomeScreen.kt
│   │   ├── TeacherUpdateScreen.kt
│   │   ├── TeacherClassesScreen.kt
│   │   ├── TeacherTimetableScreen.kt
│   │   ├── TeacherProfileScreen.kt
│   │   └── overlays/
│   │       └── ... (12 overlay files)
│   ├── parent/
│   │   ├── ParentPortalShell.kt
│   │   ├── ParentHomeScreen.kt
│   │   ├── ParentAcademicsScreen.kt
│   │   ├── ParentFeesScreen.kt
│   │   ├── ParentConversationsScreen.kt
│   │   ├── ParentProfileScreen.kt
│   │   └── overlays/
│   │       └── ... (16 overlay files)
│   └── shared/
│       ├── SplashScreen.kt
│       ├── LandingScreen.kt
│       ├── ParentAuthScreen.kt
│       ├── AdminAuthScreen.kt
│       ├── SchoolOnboardingScreen.kt
│       ├── TeacherFirstLoginScreen.kt
│       ├── LegalInfoScreen.kt
│       ├── DiscoveryScreen.kt
│       ├── ParentLinkChildScreen.kt
│       └── UnlinkedGateScreen.kt
└── theme/
    └── PremiumTheme.kt
```

### 2.2 File Naming

- HTML `data-screen="AdminHome"` → `AdminHomeScreen.kt`
- HTML `data-overlay="Notifications"` → `NotificationsOverlay.kt`
- HTML `data-composable="VActionCard"` → `VActionCard.kt`
- One composable per file (except small helpers in same file)

---

## 3. ELEMENT MAPPING RULES

### 3.1 Layout Primitives

| HTML | CSS | Kotlin Compose |
|---|---|---|
| `<div class="column">` | `flex-direction: column` | `Column { }` |
| `<div class="row">` | `flex-direction: row` | `Row { }` |
| `<div class="box">` | `position: relative` | `Box { }` |
| `<div class="lazy-column">` | `overflow-y: auto` | `LazyColumn { }` |
| `<div class="lazy-row">` | `overflow-x: auto` | `LazyRow { }` |
| `<div class="spacer">` | `height: Npx` | `Spacer(modifier = Modifier.height(N.dp))` |
| `<div class="weight-1">` | `flex: 1` | `Modifier.weight(1f)` |
| `<div class="fill-width">` | `width: 100%` | `Modifier.fillMaxWidth()` |
| `<div class="fill-height">` | `height: 100%` | `Modifier.fillMaxHeight()` |
| `<div class="scrollable">` | `overflow: scroll` | `Modifier.verticalScroll(rememberScrollState())` |

### 3.2 Component Primitives

| HTML Class | Kotlin Composable |
|---|---|
| `.vscreenscaffold` | `VScreenScaffold { }` |
| `.vbackheader` | `VBackHeader(title = "...") { }` |
| `.vbottomnav` | `VBottomNav(items = ..., active = ...) { }` |
| `.vtopabs` | `VTopTabs(tabs = ..., selected = ...) { }` |
| `.vcard` | `VCard { }` |
| `.vbutton--primary` | `VButton(variant = VButtonVariant.Primary) { }` |
| `.vbutton--secondary` | `VButton(variant = VButtonVariant.Secondary) { }` |
| `.vbutton--ghost` | `VButton(variant = VButtonVariant.Ghost) { }` |
| `.vbutton--destructive` | `VButton(variant = VButtonVariant.Destructive) { }` |
| `.vinput` | `VInput(label = "...", value = ..., onValueChange = ...)` |
| `.vbadge` | `VBadge(tone = VBadgeTone.Accent) { }` |
| `.vavatar` | `VAvatar(name = "...", imageUrl = ...)` |
| `.vactioncard` | `VActionCard(icon = ..., title = ..., subtitle = ..., onClick = ...)` |
| `.vprogressring` | `VProgressRing(progress = ..., label = ...)` |
| `.vprogressbar` | `VProgressBar(progress = ...)` |
| `.vemptystate` | `VEmptyState(icon = ..., title = ..., body = ..., action = ...)` |
| `.vcomingsoon` | `VComingSoon(title = ..., description = ...)` |
| `.vconfirmdialog` | `VConfirmDialog(title = ..., message = ..., onConfirm = ..., onCancel = ...)` |
| `.vsnackbar` | `VSnackbar(message = ..., tone = ..., action = ...)` |
| `.vstatehost` | `VStateHost(state = ..., loading = ..., error = ..., empty = ...) { content }` |
| `.vpullrefresh` | `VPullRefresh(isRefreshing = ..., onRefresh = ...) { }` |
| `.vshimmer` | `VShimmer { }` |
| `.vstatusdot` | `VStatusDot(color = ...)` |
| `.filterchip` | `FilterChip(label = ..., active = ..., onClick = ...)` |

### 3.3 Badge Tones

| HTML Class | Kotlin Enum |
|---|---|
| `.vbadge--accent` | `VBadgeTone.Accent` |
| `.vbadge--neutral` | `VBadgeTone.Neutral` |
| `.vbadge--success` | `VBadgeTone.Success` |
| `.vbadge--warning` | `VBadgeTone.Warning` |
| `.vbadge--danger` | `VBadgeTone.Danger` |

### 3.4 Text Styles

| HTML Class | Kotlin Token |
|---|---|
| `.text-h1` | `VTypography.h1` |
| `.text-h2` | `VTypography.h2` |
| `.text-h3` | `VTypography.h3` |
| `.text-body` | `VTypography.body` |
| `.text-body-small` | `VTypography.bodySmall` |
| `.text-label` | `VTypography.label` |
| `.text-caption` | `VTypography.caption` |

### 3.5 Shape Tokens

| CSS Variable | Kotlin Token |
|---|---|
| `--shape-sm` (8px) | `VShapes.sm` |
| `--shape-md` (12px) | `VShapes.md` |
| `--shape-lg` (16px) | `VShapes.lg` |
| `--shape-xl` (24px) | `VShapes.xl` |
| `--shape-full` (9999px) | `VShapes.full` |

### 3.6 Spacing Tokens

| CSS Variable | Kotlin Token |
|---|---|
| `--space-xs` (4px) | `4.dp` |
| `--space-sm` (8px) | `8.dp` |
| `--space-md` (16px) | `16.dp` |
| `--space-lg` (24px) | `24.dp` |
| `--space-xl` (32px) | `32.dp` |
| `--space-xxl` (48px) | `48.dp` |

**Rule:** Never use raw `dp` values that don't exist in the token system. If the HTML has `padding: 16px`, use `16.dp` (which maps to `--space-md`). If it has `padding: 14px`, round to the nearest token (16.dp) OR ask.

---

## 4. DATA-* ATTRIBUTE PROTOCOL

### 4.1 `data-screen`
Defines the screen identity. Maps to the Kotlin file name and composable function name.

```html
<div data-screen="AdminHome">
```
→ Create file `AdminHomeScreen.kt` with `@Composable fun AdminHomeScreen() { }`

### 4.2 `data-overlay`
Defines an overlay screen. Maps to an overlay composable.

```html
<div data-overlay="Notifications">
```
→ Create file `NotificationsOverlay.kt` with `@Composable fun NotificationsOverlay() { }`

### 4.3 `data-composable`
Defines which Compose composable this element maps to.

```html
<div data-composable="VActionCard">
```
→ Use `VActionCard(...)` in Kotlin

### 4.4 `data-foo`
Defines a "Further-Opening Option" — a sub-component or nested pattern.

```html
<div data-composable="VCard" data-foo="InsightsCarousel">
```
→ Extract as a separate composable: `@Composable fun InsightsCarousel() { }` used inside the parent screen.

### 4.5 `data-state`
Defines a state variant for the screen. The Kotlin must handle all listed states.

```html
<div data-screen="AdminHome" data-state="loading">
<div data-screen="AdminHome" data-state="empty">
<div data-screen="AdminHome" data-state="error">
<div data-screen="AdminHome" data-state="content">
```
→ The composable must use `VStateHost` with all 4 states implemented.

### 4.6 `data-nav`
Defines navigation behavior.

```html
<button data-nav="tab:People">People</button>
<button data-nav="overlay:Notifications">Notifications</button>
<button data-nav="back">Back</button>
```
→ `tab:X` → `onTabChange(SchoolTab.X)` in Kotlin
→ `overlay:X` → `onNavigate(SchoolOverlay.X)` in Kotlin
→ `back` → `onBack()` or `BackHandler { }` in Kotlin

---

## 5. TRANSLATION WORKFLOW (PER SCREEN)

For each screen in the HTML prototype, follow these steps **in order**:

### Step 1: Read the Header Comment
Every screen block starts with an HTML comment containing the Kotlin signature:
```html
<!--
  @Composable
  fun AdminHomeScreen(
    viewModel: AdminHomeViewModel = koinViewModel(),
    onNavigate: (SchoolOverlay) -> Unit,
    onTabChange: (SchoolTab) -> Unit
  )
-->
```
→ Write the exact function signature. Do not add, remove, or reorder parameters.

### Step 2: Identify the Root Layout
Read the first child element. Is it a `vscreenscaffold`? A `vbackheader` + `column`? This determines the root Compose structure.

### Step 3: Map the Tree Top-Down
Walk the HTML DOM tree depth-first. For each element:
1. Read `data-composable` → determine which Compose composable to use
2. Read CSS classes → determine modifiers, variants, styles
3. Read inline styles → determine spacing, padding, alignment
4. Read text content → determine string parameters
5. Read `data-nav` → determine click handlers
6. Read `data-state` → determine state branches

### Step 4: Extract Sub-Composables
Any element with `data-foo` becomes its own composable function. Extract it into the same file (if small) or a separate file (if large/reused).

### Step 5: Implement States
If the screen has `data-state` variants, implement all of them using `VStateHost`:
- `loading` → skeleton composables (shimmer)
- `error` → error message + retry button
- `empty` → `VEmptyState` with appropriate text
- `content` → the main layout from Step 3

### Step 6: Wire Navigation
Map every `data-nav` attribute to its Kotlin handler:
- Tab switches → call `onTabChange()` 
- Overlay opens → call `onNavigate()`
- Back → `BackHandler { onBack() }`

### Step 7: Wire Data
Use the architecture doc to determine which ViewModel provides data for this screen. The HTML shows placeholder data — replace with ViewModel state:
- `koinViewModel()` to get ViewModel
- `collectAsStateV2()` to observe state
- Map HTML placeholder text to ViewModel state fields

### Step 8: Verify
Compare the Kotlin composable tree against the HTML DOM tree:
- Every HTML child → has a Kotlin child composable?
- Every HTML class → has a matching Kotlin modifier/composable?
- Every HTML `data-nav` → has a matching Kotlin click handler?
- Every HTML `data-state` → has a matching Kotlin state branch?
- Every HTML text → has a matching Kotlin text parameter?

If ANY check fails, fix before moving to the next screen.

---

## 6. CSS TO COMPOSE MAPPING

### 6.1 Padding & Margin

```css
padding: 16px;         → Modifier.padding(16.dp)
padding: 8px 16px;     → Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
padding: 4px 8px 12px 16px; → Modifier.padding(start = 16.dp, top = 4.dp, end = 8.dp, bottom = 12.dp)
margin: 16px;          → Modifier.padding(16.dp)  (Compose has no margin — use padding)
margin-bottom: 8px;    → Modifier.padding(bottom = 8.dp)
```

### 6.2 Alignment

```css
align-items: center;   → verticalAlignment = Alignment.CenterVertically (in Column)
                       → horizontalAlignment = Alignment.CenterHorizontally (in Row)
justify-content: center; → Arrangement.Center (in Column/Row)
justify-content: space-between; → Arrangement.SpaceBetween
justify-content: space-evenly;  → Arrangement.SpaceEvenly
```

### 6.3 Size

```css
width: 200px;          → Modifier.width(200.dp)
height: 48dp;          → Modifier.height(48.dp)
min-height: 48dp;      → Modifier.heightIn(min = 48.dp)
max-width: 440px;      → Modifier.widthIn(max = 440.dp)
```

### 6.4 Colors

```css
background: var(--surface);     → color = VColors.surface
color: var(--on-surface);       → color = VColors.onSurface
border: 1px solid var(--outline); → border = BorderStroke(1.dp, VColors.outline)
```

**Rule:** Never hardcode hex colors in Kotlin. Always use VColors tokens. If the HTML uses a CSS variable, find the matching VColors token. If no token exists, create one.

### 6.5 Typography

```css
font-size: 15px;       → fontSize = VTypography.body.fontSize
font-weight: 600;      → fontWeight = FontWeight.SemiBold
line-height: 22px;     → lineHeight = 22.sp
letter-spacing: 0.1px; → letterSpacing = 0.1.sp
```

**Rule:** Prefer VTypography token styles over manual fontSize/fontWeight. Only override if the HTML explicitly deviates.

### 6.6 Border Radius

```css
border-radius: 12px;   → shape = RoundedCornerShape(12.dp) or VShapes.md
border-radius: 50%;    → shape = CircleShape or VShapes.full
```

### 6.7 Shadows / Elevation

```css
box-shadow: 0 2px 8px rgba(0,0,0,0.08); → elevation = 2.dp (approximate)
```

Map shadow intensity to Compose elevation:
- `0 1px 2px` → `1.dp`
- `0 2px 8px` → `2.dp`
- `0 4px 16px` → `4.dp`
- `0 8px 32px` → `8.dp`

---

## 7. NAVIGATION TRANSLATION

### 7.1 Portal Shells

Each portal HTML has a JS navigation system (tab switching, overlay stack, back handling). Translate to:

```kotlin
@Composable
fun AdminPortalShell() {
    var currentTab by remember { mutableStateOf(SchoolTab.Home) }
    var currentOverlay by remember { mutableStateOf<SchoolOverlay?>(null) }
    
    BackHandler(enabled = currentOverlay != null) {
        currentOverlay = null
    }
    
    BackHandler(enabled = currentOverlay == null && currentTab != SchoolTab.Home) {
        currentTab = SchoolTab.Home
    }
    
    VScreenScaffold(
        topBar = { /* header */ },
        bottomBar = { VBottomNav(active = currentTab, onTabChange = { currentTab = it }) }
    ) {
        AnimatedContent(targetState = currentTab) { tab ->
            when (tab) {
                SchoolTab.Home -> AdminHomeScreen(onNavigate = { currentOverlay = it })
                SchoolTab.People -> AdminPeopleScreen(...)
                // ...
            }
        }
        
        AnimatedContent(targetState = currentOverlay) { overlay ->
            if (overlay != null) {
                when (overlay) {
                    SchoolOverlay.Notifications -> NotificationsOverlay(onBack = { currentOverlay = null })
                    // ...
                }
            }
        }
    }
}
```

### 7.2 Tab Enums

Translate HTML tab names to Kotlin enums:

```kotlin
enum class SchoolTab { Home, People, Records, Comms, Settings }
enum class TeacherTab { Home, Update, Classes, Timetable, Profile }
enum class ParentTab { Home, Academics, Fees, Conversations, Profile }
```

### 7.3 Overlay Enums

Translate HTML overlay names to Kotlin sealed classes or enums:

```kotlin
enum class SchoolOverlay {
    Notifications, AcademicCalendarPlatform, CreateEvent, AcademicYear,
    Messages, LeaveRequests, LinkRequests, Admissions, DailyAttendance,
    ClassPerformance, TeacherPerformance, Analytics, EditProfile,
    StudentRoster, StudentProfile, PewsCohort, PewsStudentDetail,
    TeacherProfile, TeacherAssignments, StaffProfile, HealthRecords,
    Alumni, AlumniDetail, AlumniCampaign, TransportManagement,
    ReportPublish, ReportEffectiveness, ScholarshipManagement,
    BrandingKit, IdCards, Library, ScheduledMessages, EventRegistration,
    ClassesSubjects, ClassDetail
}
```

### 7.4 Deep Links

Translate the `data-nav="deeplink:/path"` attributes to the deep link parser:

```kotlin
fun parseDeepLink(path: String): DeepLinkTarget = when {
    path.startsWith("/parent/messages") -> DeepLinkTarget.ParentTab(ParentTab.Conversations)
    path.startsWith("/parent/fees") -> DeepLinkTarget.ParentTab(ParentTab.Fees)
    path.startsWith("/school/notifications") -> DeepLinkTarget.SchoolOverlay(SchoolOverlay.Notifications)
    // ... full map from D-5 in architecture doc
    else -> DeepLinkTarget.Fallback
}
```

---

## 8. STATE TRANSLATION

### 8.1 Screen States

Every screen in the HTML has 4 state variants. Translate to:

```kotlin
when (val state = viewModel.uiState) {
    is UiState.Loading -> SkeletonDashboard()  // or appropriate skeleton
    is UiState.Error -> ErrorView(message = state.message, onRetry = viewModel::retry)
    is UiState.Empty -> VEmptyState(
        icon = Icons.Default.Inbox,
        title = "No data yet",
        body = "Data will appear here once available."
    )
    is UiState.Content -> {
        // The main layout from the HTML "content" state
    }
}
```

### 8.2 Interactive States

HTML elements with `data-state` that are NOT screen-level (e.g., button loading, dialog open) become Kotlin state variables:

```html
<button data-state="submitting">Saving...</button>
```
→
```kotlin
var isSubmitting by remember { mutableStateOf(false) }
VButton(onClick = { isSubmitting = true; viewModel.save() }) {
    if (isSubmitting) CircularProgressIndicator() else Text("Save")
}
```

---

## 9. COMPONENT TRANSLATION RULES

### 9.1 VCard

```html
<div class="vcard" data-composable="VCard">
  <div class="vcard-title">Title</div>
  <div class="vcard-body">Body content</div>
</div>
```
→
```kotlin
VCard {
    Column {
        Text("Title", style = VTypography.h3)
        Text("Body content", style = VTypography.body)
    }
}
```

### 9.2 VActionCard

```html
<div class="vactioncard" data-composable="VActionCard" data-nav="overlay:Analytics">
  <div class="vactioncard-icon">📊</div>
  <div class="vactioncard-content">
    <div class="vactioncard-title">Analytics</div>
    <div class="vactioncard-subtitle">View school performance</div>
  </div>
  <div class="vactioncard-chevron">›</div>
</div>
```
→
```kotlin
VActionCard(
    icon = Icons.Default.Analytics,
    title = "Analytics",
    subtitle = "View school performance",
    onClick = onNavigate(SchoolOverlay.Analytics)
)
```

### 9.3 VBackHeader

```html
<div class="vbackheader" data-composable="VBackHeader">
  <button data-nav="back">←</button>
  <span>Title</span>
  <div class="vbackheader-trailing"><!-- optional trailing --></div>
</div>
```
→
```kotlin
VBackHeader(
    title = "Title",
    onBack = onBack,
    trailing = { /* trailing content if present */ }
)
```

### 9.4 VBottomNav

```html
<div class="vbottomnav" data-composable="VBottomNav">
  <button class="vnavitem active" data-nav="tab:Home">
    <span class="vnavitem-icon">🏠</span>
    <span class="vnavitem-label">Home</span>
  </button>
  <button class="vnavitem" data-nav="tab:People">
    <span class="vnavitem-icon">👥</span>
    <span class="vnavitem-label">People</span>
  </button>
</div>
```
→
```kotlin
VBottomNav(
    items = listOf(
        VNavItem(icon = Icons.Default.Home, label = "Home", tab = SchoolTab.Home),
        VNavItem(icon = Icons.Default.People, label = "People", tab = SchoolTab.People),
    ),
    active = currentTab,
    onTabChange = onTabChange
)
```

### 9.5 VTopTabs

```html
<div class="vtoptabs" data-composable="VTopTabs">
  <button class="vtoptab active" data-nav="tab:Teachers">Teachers</button>
  <button class="vtoptab" data-nav="tab:Students">Students</button>
</div>
```
→
```kotlin
VTopTabs(
    tabs = listOf("Teachers", "Students", "Non-teaching staff", "Alumni"),
    selected = selectedSubTab,
    onTabChange = { selectedSubTab = it }
)
```

### 9.6 Dialogs

```html
<div class="vconfirmdialog" data-composable="VConfirmDialog">
  <div class="vconfirmdialog-icon">⚠️</div>
  <div class="vconfirmdialog-title">Log out?</div>
  <div class="vconfirmdialog-message">You'll be signed out.</div>
  <button class="vbutton--destructive">Log out</button>
  <button class="vbutton--ghost">Cancel</button>
</div>
```
→
```kotlin
if (showDialog) {
    VConfirmDialog(
        icon = Icons.Default.Warning,
        title = "Log out?",
        message = "You'll be signed out.",
        confirmLabel = "Log out",
        cancelLabel = "Cancel",
        onConfirm = { onLogout() },
        onCancel = { showDialog = false }
    )
}
```

---

## 10. ZERO-TOLERANCE CHECKLIST

Before marking any screen as "translated," verify ALL of the following:

### Structure
- [ ] Every HTML child element has a corresponding Kotlin composable
- [ ] Every HTML `data-composable` has a matching Kotlin composable call
- [ ] Every HTML `data-foo` has been extracted as a separate composable
- [ ] Every HTML `data-nav` has a matching Kotlin click handler
- [ ] Every HTML `data-state` variant has a matching Kotlin state branch
- [ ] The Kotlin composable tree depth matches the HTML DOM tree depth

### Styling
- [ ] No hardcoded hex colors — all colors use VColors tokens
- [ ] No hardcoded dp values outside the token system
- [ ] No hardcoded text sizes — all use VTypography tokens
- [ ] No hardcoded corner radii — all use VShapes tokens
- [ ] Padding/margin from CSS is correctly translated to Modifier.padding
- [ ] Alignment from CSS is correctly translated to Alignment/Arrangement

### Content
- [ ] Every HTML text node has a matching Kotlin Text() composable
- [ ] Every HTML icon has a matching Kotlin icon parameter
- [ ] Every HTML badge has a matching Kotlin VBadge with correct tone
- [ ] Every HTML avatar has a matching Kotlin VAvatar
- [ ] Every HTML button has a matching Kotlin VButton with correct variant

### Navigation
- [ ] Tab switches work (tap tab → content changes)
- [ ] Overlay opens (tap action → overlay appears)
- [ ] Back from overlay → returns to tabs
- [ ] Back from non-home tab → returns to Home
- [ ] Deep links parse to correct screen

### States
- [ ] Loading state shows skeleton
- [ ] Error state shows message + retry
- [ ] Empty state shows VEmptyState
- [ ] Content state shows main layout

### Data
- [ ] ViewModel is injected via koinViewModel()
- [ ] UI state is observed via collectAsStateV2()
- [ ] HTML placeholder data is replaced with ViewModel state
- [ ] Error handling wraps API calls

---

## 11. EXECUTION ORDER

Translate in this order — each phase must compile before moving to the next:

### Phase 1: Tokens & Theme
- VColors, VShapes, VTypography, VMotion
- PremiumTheme
- Compile: ✅

### Phase 2: Base Components
- VScreenScaffold, VBackHeader, VBottomNav, VTopTabs
- VCard, VButton, VInput, VBadge, VAvatar, VActionCard
- VProgressRing, VProgressBar, VEmptyState, VComingSoon
- VConfirmDialog, VSnackbar, VStateHost, VPullRefresh
- VShimmer, VStatusDot, FilterChip
- Compile: ✅

### Phase 3: Navigation
- Tab enums, Overlay enums
- Portal shells (Admin, Teacher, Parent)
- Deep link parser
- Compile: ✅

### Phase 4: Shared Screens (START HERE — Landing Page First)

#### 4.1 Landing Page — `LandingScreen.kt`

> **This is the FIRST screen to build. The HTML prototype is in `preview/enrollplus-auth-prototype.html`.**

**File location:** `composeApp/src/commonMain/kotlin/com/littlebridge/enrollplus/ui/screens/shared/LandingScreen.kt`

**CRITICAL — NO HARDCODED CONTENT:**
The HTML prototype contains lots of hardcoded content (headlines, subtitles, feature text, labels). This was for visual prototyping only. In the Kotlin Compose version, ALL text, data, and numbers MUST come from the backend via ViewModel → API → database. Do NOT copy the hardcoded strings from HTML into the Kotlin composables.

Specifically:
- Slide headlines, subtitles, and feature descriptions → fetch from backend config API (e.g., `GET /api/v1/config/landing-slides` or similar). If no endpoint exists yet, create a ViewModel that exposes these as state and wire it to the appropriate repository/use case. Do NOT inline strings.
- Slide count, order, and target navigation → driven by backend response, not hardcoded list of 2 slides.
- CTA button text and navigation target → derived from active slide data from the ViewModel.
- Secondary action link text and target → derived from active slide data.
- Terms & Privacy Policy links → use string resources, not inline strings.
- Any numbers, counts, or metrics → from backend only.

**What the HTML IS the source of truth for:**
- Visual layout (carousel structure, bottom-anchored content, top bar with wordmark + counter)
- Spacing, padding, typography sizes, colors, shapes
- Animation behavior (stagger fade-up on slide change, scroll-snap carousel)
- Component hierarchy (slide → top-bar + visual-mark + main + controls)
- Interaction patterns (swipe between slides, progress bar updates, CTA changes dynamically)

**100% SAME VISUAL OUTPUT:**
The Kotlin Compose version must render pixel-for-pixel identical to the HTML prototype. Same spacing, same font sizes, same colors, same animations, same carousel behavior. The only difference: content comes from the backend, not hardcoded.

**But while copying, do NOT create something broken:**
- The carousel must work (HorizontalPager with scroll-snap behavior)
- The stagger animation must work (Compose animation API)
- The progress bars must update on slide change
- The CTA must dynamically update text + navigation based on active slide
- The secondary link must dynamically update based on active slide
- All navigation must work (tap CTA → navigate to correct auth screen)

**Compose implementation notes:**
- Use `HorizontalPager` for the carousel (not LazyRow — pager gives proper snap behavior)
- Use `animateFloatAsState` or `Animatable` for the stagger fade-up animation on slide change
- Use `VColors` tokens for all colors — the HTML uses CSS variables that map directly to VColors
- Use `VTypography` tokens for all text styles
- Place the file in `composeApp/src/commonMain/kotlin/com/littlebridge/enrollplus/ui/screens/shared/LandingScreen.kt`
- Create a `LandingViewModel` in `shared/src/commonMain/kotlin/com/littlebridge/enrollplus/feature/auth/presentation/LandingViewModel.kt` that fetches landing slide config from backend
- Register the ViewModel in Koin

**Backend connection:**
- Check `ENROLLPLUS_SCREEN_ARCHITECTURE_FINAL.md` for the Landing screen spec (search for "Landing" or "Splash" in the shared/auth section)
- Check `ENROLLPLUS_RESTRUCTURE_CHANGELOG_AND_BACKEND_MAPPING.md` for any relevant backend mapping entries
- If no backend endpoint exists for landing slide config, create one: `GET /api/v1/config/landing-slides` returning slide data (label, headline, subtitle, icon, target screen, CTA text, secondary link text + target)
- The ViewModel must handle all 4 states: Loading (skeleton/shimmer), Error (retry), Empty (fallback to default slides), Content (render slides from backend)

#### 4.2 Remaining Shared Screens
- SplashScreen, ParentAuth, AdminAuth
- SchoolOnboarding, TeacherFirstLogin, LegalInfo
- Discovery, ParentLinkChild, UnlinkedGate
- Compile: ✅

### Phase 5: Admin Portal (5 tabs + 30 overlays)
- One tab at a time: Home → People → Records → Comms → Settings
- Then overlays: one at a time, in architecture doc order
- Compile after each screen: ✅

### Phase 6: Teacher Portal (5 tabs + 12 overlays)
- Home → Update → Classes → Timetable → Profile
- Then overlays
- Compile after each screen: ✅

### Phase 7: Parent Portal (5 tabs + 16 overlays)
- Home → Academics → Fees → Conversations → Profile
- Then overlays
- Compile after each screen: ✅

### Phase 8: Integration & Cutover
- Wire all portals into NavGraph
- Wire deep links
- Wire ViewModels
- Full build: ✅
- Side-by-side visual comparison with HTML prototype

---

## 12. ANTI-PATTERNS (DO NOT DO THESE)

1. **Do NOT use AndroidView.** Everything is Compose.
2. **Do NOT use XML layouts.** Everything is @Composable.
3. **Do NOT skip "minor" elements.** Badges, chevrons, status dots, helper text — all must appear.
4. **Do NOT merge screens.** If the HTML has 90 screens, Kotlin gets 90 screens.
5. **Do NOT reorder layout.** If HTML has title above body, Kotlin has title above body.
6. **Do NOT change spacing.** If HTML has 16px gap, Kotlin has 16.dp gap.
7. **Do NOT add elements not in HTML.** No extra buttons, no extra cards, no "improvements."
8. **Do NOT use hardcoded strings in production.** Use string resources. (During initial translation, hardcoded is OK for speed — mark with `// TODO: i18n`.)
9. **Do NOT ignore states.** Every screen must have all 4 states implemented.
10. **Do NOT guess composable parameters.** If unsure, check the component definition file.

---

## 13. WHEN TO STOP AND ASK

Stop and ask the user if:
- An HTML element has no `data-composable` and no recognizable CSS class
- A CSS property has no Compose equivalent (e.g., `backdrop-filter`)
- The HTML layout is ambiguous (e.g., unclear if it's a Row or Column)
- A screen references a ViewModel that doesn't exist yet
- A component needs a parameter that doesn't exist in the component definition
- Two HTML elements conflict (e.g., `data-composable="VCard"` but styled as a button)

**Never guess. Never skip. Never "improve." Translate faithfully.**

---

*End of GOD MODE PROMPT — HTML to Kotlin Conversion*
