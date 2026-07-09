# Parent Portal Rebuild — Phase-Wise Plan

> **Audit Date:** July 6, 2026
> **Source Documents:** JSON style spec, ENROLLPLUS_SCREEN_ARCHITECTURE_FINAL.md (Part C), ENROLLPLUS_RESTRUCTURE_CHANGELOG_AND_BACKEND_MAPPING.md
> **Current State:** Home Tab + 16 Overlays built with style mismatches. 4 tabs missing. State management system missing. 12+ components missing.

---

## Audit Summary

### What Exists (Built)
- `ParentPortalShell.kt` — shell with standard bottom nav, tab switching, overlay host
- `ParentHomeTab.kt` — home tab with 7 sections (header, hero, learning, insights, schedule, quick access, announcements)
- `ParentOverlays.kt` — 16 overlay composables
- `ParentOverlayScaffold.kt` — overlay scaffold with slide animations
- `ParentRoute.kt` — tabs/overlays enum + deep link parser (21 paths)
- `VBackHeader.kt` — back header component
- Token files: `VColors.kt`, `VTypography.kt`, `VShapes.kt`, `VMotion.kt`
- `ParentViewModel.kt` — 25+ state flows, all repository methods wired

### What's Missing
- **4 entire tabs:** Academics, Fees, Conversations, Profile
- **2 shell items:** Unlinked Gate, ParentDock (floating glass dock)
- **3 home tab FOOs:** Child Switcher Dropdown, Feature Card Grid (with live metrics), School-Day Timeline
- **5 major overlay structural mismatches:** Messages, Transport, Health, Digital ID, AI Tutor
- **Entire state management system:** VStateHost, Skeletons, VShimmer, VPullRefresh, VEmptyState, VSnackbar
- **12+ components:** VAvatar, VProgressRing, VCharts, VBadge, VActionCard, VConfirmDialog, VDatePicker, VTimePicker, VThemePicker, FilterChip, VComingSoon, VStatusDot
- **2 functional bugs:** Initial child data never loads, conversation overlay routes to wrong screen

### Visual Mismatches Found (Home Tab + Overlays)
- Portal Header: 5 mismatches (padding, school name, icon sizes)
- Hero Card: 3 mismatches (decorative circles, arrow icon, tag content)
- Quick Access: 5 data mismatches (badges, sub-labels)
- VBackHeader: 8 mismatches (height, bg, shadow, padding, back button size/shape, icon size, title style)
- Notifications Overlay: 3 mismatches (icon container size/color, icon size, body text color)
- Scholarships Overlay: 3 mismatches (title weight, amount field missing, status badge wrong)
- Events Overlay: 3 mismatches (date display, title weight, status badge)
- Library Overlay: 2 mismatches (book icon, fines card missing)
- Transport Overlay: MAJOR structural mismatch (entire layout different)
- Health Overlay: MAJOR structural mismatch (missing pulse ring, risk factors, AI recommendation)
- Digital ID Overlay: MAJOR structural mismatch (entire card design different)
- AI Tutor Overlay: MAJOR structural mismatch (not a chat UI)

---

## State Management Specification

Every screen in the parent portal must implement a 4-phase state system. No screen is allowed to show a blank state, a lone centered spinner, or hardcoded data.

### 1. Loading State

**Rules:**
- Show a skeleton matching the real content layout (same item count, card sizes, spacing)
- Shimmer animation (left-to-right gradient sweep, 1.5s loop)
- Never a blank screen or a lone centered spinner
- Skeleton structure must mirror the actual content — if the real screen shows 5 cards, the skeleton shows 5 card-shaped blocks
- Skeleton must use the same padding, spacing, and corner radius as the real content

**Implementation:**
```
VStateHost(
    state = UiState.Loading,
    skeleton = { SkeletonDashboard() },  // matches real layout
    error = { ... },
    empty = { ... },
    content = { ... }
)
```

**Skeleton Inventory:**
| Skeleton | Used By |
|----------|---------|
| `SkeletonDashboard` | Home tab, Profile tab |
| `SkeletonList` / `SkeletonListRow` | Notifications, Messages, Scholarships, Events, Library, Leave, Conversations |
| `SkeletonFee` | Fees tab |
| `SkeletonAnnouncements` | Announcements feed (Conversations tab) |
| `SkeletonCalendar` | Calendar overlay, Academics Attendance |
| `SkeletonProfile` | Profile tab, Account Settings |

**Shimmer Animation:**
- Direction: left-to-right
- Duration: 1.5s loop
- Gradient: transparent → surfaceTint (20% opacity) → transparent
- Easing: LinearEasing
- Applied to: all skeleton blocks (cards, text lines, icon circles)

### 2. Content State

**Rules:**
- Real data rendered from ViewModel state — zero hardcoded values
- Pull-to-refresh enabled on all scrollable screens
- Staggered entrance animation for list items (0–300ms cascade)
- Each item animates in with a slight vertical translation (8dp → 0) + fade (0 → 1)
- Stagger delay: 50ms per item, max 300ms total
- Animation easing: `VMotion.easeEmphasized`

**Implementation:**
```
LaunchedEffect(data) {
    // Trigger staggered entrance
    visibleItemCount = 0
    data.forEachIndexed { index, _ ->
        delay(50)
        visibleItemCount = index + 1
    }
}
```

**Pull-to-Refresh:**
- Brand-tinted indicator (violet)
- Threshold: 80dp pull
- On refresh: re-call the screen's primary API
- Indicator color: `VColors.violet`
- Background: `VColors.cream`

### 3. Empty State

**Rules:**
- Plain-language message explaining why it's empty
- Action button ONLY if the user can do something about it
- No illustrations, no sad-face drawings — just text + button
- Friendly tone, not clinical
- Centered vertically in content area

**Examples:**
| Screen | Empty Message | Action Button |
|--------|--------------|---------------|
| Fees | "No fees due" | None (good news!) |
| Notifications | "No notifications yet" | None |
| Messages | "No conversations yet" | None |
| Scholarships | "No scholarships available right now" | None |
| Events | "No upcoming events" | None |
| Library (issued) | "No books issued" | "Browse Catalog" button |
| Unlinked gate | "Link your child to get started" | "Link Your Child" button |
| Leave history | "No leave requests" | "Apply for Leave" button |
| Transport | "Child not assigned to transport" | None |
| Health | "No health records on file" | None |
| Tutor progress | "No tutor sessions yet" | "Ask AI Tutor" button |
| Conversations | "No conversations yet" | None |
| Academics (any sub-tab) | "No [data type] yet" | None |
| Profile badges | "No badges earned yet" | None |

**Implementation:**
```
VEmptyState(
    title = "No fees due",
    body = null,  // omit body if title is self-explanatory
    icon = Icons.Rounded.CheckCircle,
    actionLabel = null,  // null = no button
    onAction = null
)
```

### 4. Error State

**Rules:**
- Plain-language message: "Couldn't load [thing]. Check your connection and try again."
- "Retry" button that re-calls the API
- If offline: show cached data with a "You're offline" banner at top
- No technical jargon ("404", "Network timeout", "Exception")
- Error message includes the specific thing that failed

**Examples:**
| Screen | Error Message |
|--------|--------------|
| Home | "Couldn't load the dashboard. Check your connection and try again." |
| Attendance | "Couldn't load attendance. Check your connection and try again." |
| Fees | "Couldn't load fee details. Check your connection and try again." |
| Transport | "Couldn't track the bus. Check your connection and try again." |
| Health | "Couldn't load health records. Check your connection and try again." |

**Implementation:**
```
VStateHost(
    state = UiState.Error("Couldn't load..."),
    onRetry = { viewModel.loadX() },
    ...
)
```

**Offline Banner:**
- Shown at top of screen when `NetworkResult.ConnectionError` is the last result
- Amber background (`VColors.goldSoft`)
- Text: "You're offline. Showing last saved data."
- Auto-dismissed when connection restored
- Does NOT block interaction — user can still scroll cached data

### State Transition Rules

```
Loading ──(API success)──→ Content
Loading ──(API error)────→ Error
Loading ──(API empty)────→ Empty
Error ──(tap Retry)──────→ Loading
Empty ──(tap Action)─────→ [action target]
Content ──(pull refresh)─→ Loading (with content still visible)
Content ──(API error)────→ Content (stale) + Error banner
```

**Crossfade:** All state transitions use 300ms crossfade with `VMotion.easeEmphasized`.

---

## Phase 1: Foundation — State Management + Core Components

> **Goal:** Build the state management system and all shared components that every screen depends on.
> **Duration estimate:** 1 session
> **Files created:** 8–10 new files
> **Files modified:** 0

### 1.1 VStateHost Component
- **File:** `ui/components/VStateHost.kt`
- 4-phase state manager: Loading → Error → Empty → Content
- 300ms crossfade between states
- Parameters: `state: UiState<T>`, `skeleton: @Composable`, `error: @Composable (String, () -> Unit)`, `empty: @Composable`, `content: @Composable (T)`
- Loading state shows skeleton composable
- Error state shows message + retry button
- Empty state shows VEmptyState
- Content state shows actual data

### 1.2 VShimmer + Skeletons
- **File:** `ui/components/VShimmer.kt`
- Shimmer brush animation: left-to-right gradient sweep, 1.5s loop
- `Modifier.shimmer()` extension
- **File:** `ui/components/skeletons/Skeletons.kt`
- `SkeletonDashboard()` — hero card block + 3 card blocks + timeline block + grid blocks
- `SkeletonList(rows: Int)` — N rows of card-shaped blocks with text lines
- `SkeletonListRow()` — single row: icon circle + 2 text lines
- `SkeletonFee()` — hero card block + 3 list rows
- `SkeletonAnnouncements()` — 3 announcement card blocks
- `SkeletonCalendar()` — calendar grid block
- `SkeletonProfile()` — avatar circle + stat grid blocks

### 1.3 VEmptyState Component
- **File:** `ui/components/VEmptyState.kt`
- Centered: icon in circle → title → optional body → optional action button
- No illustrations, no sad-face drawings
- Plain language text
- Action button only if user can do something

### 1.4 VSnackbar Component
- **File:** `ui/components/VSnackbar.kt`
- Bottom-anchored transient bar: icon + message + optional action
- Tones: Success (green check), Error (red alert), Warning (yellow), Info (blue)
- Auto-dismiss after 3–4s
- Slide-up + fade animation
- Does not block interaction

### 1.5 VPullRefresh Component
- **File:** `ui/components/VPullRefresh.kt`
- Wraps scrollable content
- Brand-tinted indicator (violet)
- Pull threshold: 80dp
- On refresh: calls `onRefresh` callback

### 1.6 VAvatar Component
- **File:** `ui/components/VAvatar.kt`
- Circular avatar with initials fallback
- Optional image URL (Coil)
- Sizes: sm (24dp), md (32dp), lg (48dp), xl (72dp)
- Background: `VColors.violetSoft` when showing initials
- Text: initials in `VColors.violet`

### 1.7 VProgressRing Component
- **File:** `ui/components/VProgressRing.kt`
- Circular progress with optional center label
- Parameters: `progress: Float` (0–1), `size: Dp`, `strokeWidth: Dp`, `color: Color`, `centerLabel: String?`
- Animated progress on first appearance (300ms)

### 1.8 VBadge Component
- **File:** `ui/components/VBadge.kt`
- Pill badge with tones: Accent, Neutral, Success, Warning, Danger, Arctic
- Tones map to color pairs (bg + text)
- Sizes: sm (9sp text), md (11sp text)

### 1.9 VActionCard Component
- **File:** `ui/components/VActionCard.kt`
- Tappable card: icon in rounded square + title + subtitle + chevron
- onClick callback
- White background, `VShapes.md` corners, subtle shadow

### 1.10 VConfirmDialog Component
- **File:** `ui/components/VConfirmDialog.kt`
- Dialog: icon in danger circle → title → message → confirm (Destructive) + cancel (Ghost)
- Scrim dismisses (cancel only)
- Used for: logout, unlink child, delete actions

### 1.11 VCharts Component (Minimal)
- **File:** `ui/components/VCharts.kt`
- `VBarChart(data: List<Pair<String, Float>>)` — simple bar chart
- `VLineChart(data: List<Float>)` — simple line chart
- `VDonutChart(segments: List<Pair<Float, Color>>)` — donut chart
- Canvas-based, no external chart library

### 1.12 Remaining Utility Components
- **VStatusDot:** Colored dot indicator (success/coral/gold/sky/violet)
- **FilterChip:** Rounded pill, active/inactive state, horizontal scroll
- **VComingSoon:** "PREVIEW" badge + title + description + "Notify me" pill
- **VDatePicker:** Dialog with calendar grid, month nav, day cells
- **VTimePicker:** Dialog with clock face, AM/PM toggle

### 1.13 Verification
- All components compile
- Each component has a preview composable (for dev testing)
- No external dependencies beyond what's already in `build.gradle.kts`

---

## Phase 2: Shell + Home Tab Fixes

> **Goal:** Fix the portal shell (add unlinked gate, ParentDock) and fix all visual mismatches in the Home Tab.
> **Duration estimate:** 1 session
> **Files created:** 1–2 new files
> **Files modified:** `ParentPortalShell.kt`, `ParentHomeTab.kt`, `VBackHeader.kt`

### 2.1 Fix VBackHeader (affects all overlays)
- Height: 48dp → 41dp
- Background: transparent → white
- Add bottom shadow (shadow-bottom)
- Padding: 16/4 → 8/24 (horizontal/vertical)
- Back button size: 40dp → 27dp
- Back button shape: full circle → 10dp radius
- Back icon size: 22dp → 15dp
- Title style: 15sp SemiBold → 16sp ExtraBold
- Body horizontal padding: 20dp → 24dp

### 2.2 Build Unlinked Gate Screen
- **File:** `ui/screens/parent/ParentUnlinkedGate.kt`
- Full screen gate shown when `isUnlinked == true`
- "Link your child" heading + explanation text
- "Link Child" button (Primary) → LinkChild overlay
- "Discover Schools" button (Secondary) → Discovery overlay
- Simple language, large text, no jargon

### 2.3 Fix Portal Header
- Bottom padding: 8dp → 16dp
- School label: "School" → actual school name from dashboard data
- Chevron icon size: 14dp → 12dp
- Bell button size: 38dp → 32dp
- Bell icon size: 18dp → 15dp

### 2.4 Fix Hero Card
- Remove decorative circles (violet + coral)
- Remove arrow icon next to name
- Fix tag content: use "Red House", "94% Attendance" from dashboard data (not computed progress/present)

### 2.5 Fix Quick Access Grid
- Academics tile: add grade badge (from dashboard data)
- Health tile: sub-label "Last check Jan 8" (from health data)
- Transport tile: sub-label "On route · 3:45 PM" (from transport live data)
- Scholarships tile: add count badge
- Library tile: add issued books count badge
- Each tile should show a live metric, not a static description

### 2.6 Fix Child Switcher Dropdown
- Add avatars to dropdown items
- Show name + class in each item
- Checkmark on current child
- Proper DropdownMenu (not custom Row)

### 2.7 Fix Bottom Nav → ParentDock
- Replace standard `ParentBottomNav` with floating glass dock
- Dock floats above content with 16dp margin from bottom
- Glassmorphism: semi-transparent white background + blur
- Active item: violet lozenge behind icon + label
- Inactive item: ink3 icon + label
- 5 items: Home, Academics, Fees, Conversations, Profile

### 2.8 Apply VStateHost to Home Tab
- Replace `HomeLoadingState` with `SkeletonDashboard()` inside `VStateHost`
- Replace `HomeErrorState` with `VEmptyState` + retry button inside `VStateHost`
- Add `VPullRefresh` wrapper to home scroll content
- Add staggered entrance animation to home sections

### 2.9 Verification
- Build compiles
- Home tab shows skeleton during loading (not spinner)
- Home tab shows error with retry (not blank)
- Pull-to-refresh works on home tab
- All JSON style values match for Portal Header, Hero Card, Quick Access, Bottom Nav
- Unlinked gate shows when `isUnlinked == true`

---

## Phase 3: Overlay Fixes (Structural + Style)

> **Goal:** Fix all 5 major structural mismatches and all style mismatches in overlays.
> **Duration estimate:** 1–2 sessions
> **Files modified:** `ParentOverlays.kt`, `ParentOverlayScaffold.kt`

### 3.1 Fix Notifications Overlay
- Icon container: 34dp → 29dp
- Icon colors: all violet → varied (sky/coral/mint/violet based on notification type)
- Icon size: 15dp → 13dp
- Body text color: ink2 → ink3

### 3.2 Rebuild Transport Overlay (MAJOR)
- Add "On Route" status card (large, centered)
- Add large ETA display: "3:45 PM" (28sp, ExtraBold, violet)
- Add route dots row (School → Home with dots/line)
- Add "Boarded" confirmation banner (green background)
- Add route details card: bus number, driver name, driver phone, route info
- Use `transportLiveState` for live data + `transportRouteState` for route details
- Add `VPullRefresh` for live updates

### 3.3 Rebuild Health Overlay (MAJOR)
- Add Pulse Score ring (102dp, score "85" 48sp in center, colored ring)
- Add Risk Factors card (list of factors with severity badges)
- Add AI Recommendation card (violetSoft background, AI icon, recommendation text)
- Keep existing Health Profile card (blood group, allergies, last checkup, emergency contact)
- Keep existing immunizations + incidents sections
- Wire `pulseState` for pulse score data

### 3.4 Rebuild Digital ID Card Overlay (MAJOR)
- White card background (not violet)
- School name at top (ink3, 12sp, Bold)
- Avatar centered (64dp)
- Student name (18sp, ExtraBold, ink)
- Class + roll number below name
- QR code centered (102dp) — generate from student ID
- Validity text at bottom
- "Download" button (Primary, full width)
- Remove "EnrollPlus" wordmark

### 3.5 Rebuild AI Tutor Overlay (MAJOR)
- Replace form-based UI with chat interface
- Chat bubbles: tutor (white, left-aligned), user (violet, right-aligned)
- Chat input bar at bottom: text field + send button
- Quick suggestion chips above input bar (horizontal scroll)
- Subject selector as dropdown (not list)
- Typing indicator (3 dots animation) when waiting for response
- "View Progress" button in header trailing slot
- WhatsApp mental model — large text, clear send button

### 3.6 Fix Scholarships Overlay
- Title weight: Bold → ExtraBold (800)
- Add amount field: "₹25,000" (16sp, 800, violet) below title
- Replace category badge with status badge: "Applied" / "Not Eligible" / "Eligible"
- Status badge colors: Applied=mint, Not Eligible=coral, Eligible=violet

### 3.7 Fix Events Overlay
- Date display: 12sp ink3 with icon → 10sp 700 violet (text only, no icon)
- Title weight: Bold → ExtraBold (800)
- Replace type label with status badge: "Registration Open" / "Registered" / "Book Slot" / "Coming Soon"
- Status badge colors: Registration Open=violet, Registered=mint, Book Slot=gold, Coming Soon=ink3

### 3.8 Fix Library Overlay
- Book icon: 34dp violetSoft School icon → 29dp skySoft Book icon
- Add "Fines" card section: outstanding fine amount + reason (from `libraryIssuedState` data with `fineAmount > 0`)

### 3.9 Apply VStateHost to All Overlays
- Replace `OvLoading()` with appropriate skeleton inside `VStateHost`
- Replace `OvError()` with error message + retry inside `VStateHost`
- Add empty states with plain language messages
- Add `VPullRefresh` where applicable

### 3.10 Verification
- Build compiles
- All overlay layouts match JSON spec
- VStateHost shows skeleton → content transition on all overlays
- Pull-to-refresh works on overlays with live data (Transport, Notifications)

---

## Phase 4: Academics Tab

> **Goal:** Build the complete Academics tab with 7 sub-tabs.
> **Duration estimate:** 1–2 sessions
> **Files created:** 1 new file
> **Files modified:** `ParentPortalShell.kt` (wire tab)

### 4.1 Academics Tab Shell
- **File:** `ui/screens/parent/ParentAcademicsTab.kt`
- Top: action cards row — "Apply for Leave" (→ Leave overlay) + "Health Records" (→ Health overlay)
- Below: `VTopTabs` with 7 tabs: Overview · Attendance · Marks · Syllabus · Quizzes · Homework · Report
- Content swaps below based on selected tab
- 140dp bottom padding for dock clearance
- `VPullRefresh` wraps entire screen

### 4.2 Overview Sub-tab
- Journey progress card (level ring, XP bar, streak count)
- Attendance summary (VProgressRing, %)
- Marks summary (avg, trend arrow)
- Syllabus coverage (VProgressBar, %)
- Recent achievements list

### 4.3 Attendance Sub-tab
- Monthly calendar with color-coded days (green=present, red=absent, yellow=late)
- Summary card: present days, absent days, late days, attendance %
- 6-month trend chart (VLineChart)
- Tap calendar day → shows detail for that day
- Swipe to change month

### 4.4 Marks Sub-tab
- Assessment list: name, subject, date, marks scored, max marks, grade badge
- Subject filter chips (FilterChip component)
- Trend chart for selected subject (VLineChart)
- Tap assessment → detail view

### 4.5 Syllabus Sub-tab
- Subject-wise syllabus coverage cards
- Each: subject name, coverage % (VProgressBar), unit breakdown (completed/total)
- Expandable to show unit list with status
- Tap card → expand unit list
- Tap unit → sub-topic detail

### 4.6 Quizzes Sub-tab
- Quiz list: title, subject, date, score, status badge (Completed/Pending/Upcoming)
- Tap completed quiz → quiz result detail
- Tap leaderboard → class ranking view

### 4.7 Homework Sub-tab
- Date selector + homework list for selected date
- Each: subject, title, description, due date, submission status (Submitted/Pending/Late)
- Tap → homework detail

### 4.8 Report Sub-tab
- Report card list (term-wise): term name, issue date, overall grade, download button
- Tap → AI report card preview (insights, strengths, improvement areas)
- "Download" → PDF download

### 4.9 State Management
- Each sub-tab uses `VStateHost` with appropriate skeleton
- Lazy-loads data on tab selection
- Pull-to-refresh per tab
- Empty states per sub-tab: "No [data type] yet"
- Error states: "Couldn't load [thing]. Check your connection and try again."

### 4.10 Data Wiring
- `attendanceState` → Attendance sub-tab
- `marksState` → Marks sub-tab
- `syllabusState` / `syllabusV2State` → Syllabus sub-tab
- `quizListState` → Quizzes sub-tab
- `dailySummaryState` → Homework sub-tab
- Report data → Report sub-tab (may need new VM method if not yet wired)

### 4.11 Verification
- Build compiles
- All 7 sub-tabs render with correct data
- VStateHost shows skeleton → content on each tab
- Pull-to-refresh works
- Tab switching lazy-loads data
- Empty/error states show correctly

---

## Phase 5: Fees Tab

> **Goal:** Build the complete Fees tab.
> **Duration estimate:** 1 session
> **Files created:** 1 new file
> **Files modified:** `ParentPortalShell.kt` (wire tab)

### 5.1 Fees Tab Layout
- **File:** `ui/screens/parent/ParentFeesTab.kt`
- Navy-gradient balance hero card:
  - Outstanding amount in large text (28sp, ExtraBold)
  - "Pay Now" button if payment gateway available
  - Due date below amount
- Fee announcements feed:
  - School-wide fee notices
  - Each: title, date, description preview
  - Tap → detail
- Payment history list:
  - Date, amount, receipt number, download button
  - Tap → receipt download
- 140dp bottom padding

### 5.2 State Management
- `VStateHost` with `SkeletonFee` for loading
- Empty: "No fee records yet"
- Error: "Couldn't load fee details. Check your connection and try again."
- `VPullRefresh` wraps entire screen
- Payment processing state: button shows spinner
- Payment success: VSnackbar (Success tone)
- Payment error: VSnackbar (Error tone)

### 5.3 Data Wiring
- `feesState` → balance, payment history, fee structure
- Fee announcements → may need new VM method or reuse `announcementsState` filtered by category
- Payment: call `POST /api/v1/parent/fees/pay` (already exists per memory)

### 5.4 Verification
- Build compiles
- Balance hero shows correct amount
- Payment history loads
- "Pay Now" triggers payment flow
- Empty/error states show correctly

---

## Phase 6: Conversations Tab

> **Goal:** Build the complete Conversations tab with Messages + Announcements segments.
> **Duration estimate:** 1–2 sessions
> **Files created:** 1 new file
- **Files modified:** `ParentPortalShell.kt` (wire tab, fix conversation routing bug)

### 6.1 Conversations Tab Shell
- **File:** `ui/screens/parent/ParentConversationsTab.kt`
- Segmented control: Messages · Announcements
- Content swaps below
- 140dp bottom padding
- Unread badge on Messages segment

### 6.2 Messages Segment
- Inbox of conversation threads (WhatsApp-style)
- Each: avatar (teacher/admin), name, last message preview, timestamp, unread badge
- Tap → conversation view (message bubbles + compose bar)
- "Compose new" → thread picker (select teacher/admin to message)
- Back → inbox (BackHandler peels back from conversation to inbox)

### 6.3 Conversation View
- Message bubbles: incoming (white, left), outgoing (violet, right)
- Compose bar at bottom: text field + send button
- Auto-scroll to latest message
- Sending state: bubble shows "Sending..." label
- Offline: "Will send when online" indicator on queued message

### 6.4 Announcements Segment
- Announcement feed (vertical list of cards)
- Each: title, category badge, date, description preview, school name
- Tap → announcement detail (full text)
- Pull-to-refresh

### 6.5 State Management
- Messages: `VStateHost` with `SkeletonList` for loading
- Messages empty: "No conversations yet"
- Messages error: "Couldn't load messages. Check your connection and try again."
- Announcements: `VStateHost` with `SkeletonAnnouncements` for loading
- Announcements empty: "No announcements yet"
- Announcements error: "Couldn't load announcements. Check your connection and try again."
- Sending state: compose bar shows spinner
- Offline: queued indicator on message

### 6.6 Data Wiring
- `threadsState` → Messages inbox
- `threadMessagesState` → conversation view
- `sendMessage()` → compose bar
- `loadThreadMessages()` → on thread tap
- `markThreadRead()` → on thread open
- `loadUnreadCount()` → after send/mark read
- `announcementsState` → Announcements segment

### 6.7 Fix Conversation Routing Bug
- `ParentPortalShell.kt`: clicking a conversation thread should open Conversations tab (not TutorChat overlay)
- Deep link `/parent/messages` → switch to Conversations tab (not overlay)

### 6.8 Verification
- Build compiles
- Messages inbox loads with correct threads
- Conversation view shows correct messages
- Send message works
- Announcements feed loads
- Back navigation works (conversation → inbox → tab)
- Deep link `/parent/messages` → Conversations tab

---

## Phase 7: Profile Tab

> **Goal:** Build the complete Profile tab with gamified player card + account options.
> **Duration estimate:** 1 session
- **Files created:** 1 new file
- **Files modified:** `ParentPortalShell.kt` (wire tab)

### 7.1 Profile Tab Layout
- **File:** `ui/screens/parent/ParentProfileTab.kt`
- Gamified player card (full-width):
  - House identity (house name + color)
  - Child avatar (large, 72dp)
  - Level + XP bar
  - Stats grid (2-up): Attendance %, Average Marks, XP Points, Quizzes Completed
  - Badges/achievements horizontal scroll
- Account options panel (below card or swipe-down reveal):
  - "Account Settings" → AccountSettings overlay
  - "Link Another Child" → LinkChild overlay
  - "Discover Schools" → Discovery overlay
  - "Logout" → VConfirmDialog

### 7.2 Stats Grid
- 2-up grid of stat cards
- Each: large number + label + trend arrow
- Tap stat → navigates to relevant tab (e.g., attendance → Academics Attendance tab)

### 7.3 Badges/Achievements
- Horizontal scroll of circular badge icons with labels
- Locked badges shown dimmed
- Tap unlocked badge → achievement detail

### 7.4 State Management
- `VStateHost` with `SkeletonProfile` for loading
- Empty badges: "No badges earned yet"
- Error: "Couldn't load profile. Check your connection and try again."

### 7.5 Data Wiring
- `dashboardState` → child name, class, avatar, level, XP, house
- `attendanceState` → attendance % for stats grid
- `marksState` → average marks for stats grid
- `quizListState` → quizzes completed count
- Badges/achievements → may need new VM method or dashboard data field

### 7.6 Verification
- Build compiles
- Player card renders with correct data
- Stats grid shows correct values
- Badges scroll horizontally
- Account options navigate to correct overlays
- Logout shows VConfirmDialog

---

## Phase 8: Functional Bug Fixes + Deep Link Completion

> **Goal:** Fix all functional bugs and complete deep link routing.
> **Duration estimate:** 0.5 session
- **Files modified:** `ParentViewModel.kt`, `ParentPortalShell.kt`, `ParentRoute.kt`

### 8.1 Fix Initial Child Data Loading (CRITICAL)
- **File:** `shared/.../presentation/ParentViewModel.kt`
- In `loadDashboard()`, after `selectIfUnset(firstChildId)`, call `loadChildData()`
- This ensures attendance, fees, timetable, daily summary load after first dashboard fetch
- Current bug: `selectIfUnset()` only sets the ID, doesn't trigger child data loading

### 8.2 Fix Conversation Overlay Routing
- **File:** `ParentPortalShell.kt`
- Conversation thread click → switch to Conversations tab (not TutorChat overlay)
- Remove any `ParentOverlay.TutorChat` usage for conversations

### 8.3 Fix Child Switch Data Reload
- When switching children via `selectChild()`, also reload open overlay data
- If Health overlay is open and child switches, reload health for new child
- Implementation: observe `selectedChildId` in overlay composables and reload on change

### 8.4 Complete Deep Link Paths
- **File:** `ParentRoute.kt`
- Add missing paths:
  - `/parent/messages` → switch to Conversations tab (not overlay)
  - `/parent/report-card` → switch to Academics tab, Report sub-tab
- Verify all 28 architecture spec paths work

### 8.5 Fix Transport Route Data Usage
- Transport overlay should use both `transportLiveState` (live location) AND `transportRouteState` (route details)
- Currently only `transportLiveState` is read; route details (driver, phone, stops) never displayed

### 8.6 Verification
- Login → dashboard loads → child data loads automatically (no manual child switch needed)
- Conversation thread click → Conversations tab (not TutorChat)
- Child switch → all open overlays reload for new child
- All 28 deep link paths resolve correctly
- Transport overlay shows both live data AND route details

---

## Phase 9: Final Polish + Verification

> **Goal:** Final pass to ensure everything compiles, all states work, no hardcoded data, all JSON styles match.
> **Duration estimate:** 0.5 session

### 9.1 Full Build Verification
- Run `:composeApp:compileDevDebugKotlinAndroid`
- Fix any compilation errors
- Run `:shared:compileKotlinJvm` (verify VM changes)

### 9.2 State Management Audit
- Every screen uses `VStateHost`
- Loading → skeleton (not spinner)
- Error → message + retry (not blank)
- Empty → plain language + optional action
- Content → real data from ViewModel
- Pull-to-refresh on all scrollable screens
- Staggered entrance on all list screens

### 9.3 Visual Style Audit
- Re-compare all JSON style values against code
- Verify VBackHeader matches JSON (height, bg, shadow, padding, back button, title)
- Verify Portal Header matches JSON (padding, school name, icon sizes)
- Verify Hero Card matches JSON (no decorative circles, no arrow, correct tags)
- Verify all overlays match JSON structural specs

### 9.4 Data Flow Audit
- Zero hardcoded data — every value comes from ViewModel
- Every ViewModel state flow is consumed by at least one UI component
- Every overlay load method is called on overlay open
- Child switch reloads all relevant data

### 9.5 Navigation Audit
- Every tab switch works
- Every overlay opens and closes
- Back button always goes to the right place
- No dead-ends
- Deep links resolve correctly

### 9.6 Accessibility Audit
- All nav items have icon + text label (never icon-only)
- 48dp minimum tap targets
- Large text (comfortable, not oversized — 14-16sp body, 18-22sp headers)
- Plain language (no jargon)
- Error messages in plain language

---

## Execution Order Summary

| Phase | Goal | Est. Sessions | Dependencies |
|-------|------|---------------|--------------|
| 1 | Foundation: State Management + Components | 1 | None |
| 2 | Shell + Home Tab Fixes | 1 | Phase 1 |
| 3 | Overlay Fixes (Structural + Style) | 1–2 | Phase 1 |
| 4 | Academics Tab | 1–2 | Phase 1 |
| 5 | Fees Tab | 1 | Phase 1 |
| 6 | Conversations Tab | 1–2 | Phase 1 |
| 7 | Profile Tab | 1 | Phase 1 |
| 8 | Bug Fixes + Deep Links | 0.5 | Phases 2–7 |
| 9 | Final Polish + Verification | 0.5 | Phase 8 |

**Total estimated sessions:** 8–10

**Critical path:** Phase 1 → Phase 2 → Phase 8 → Phase 9
**Parallelizable:** Phases 3, 4, 5, 6, 7 can be done in any order after Phase 1

---

## File Inventory

### New Files to Create (Phase 1)
1. `ui/components/VStateHost.kt`
2. `ui/components/VShimmer.kt`
3. `ui/components/skeletons/Skeletons.kt`
4. `ui/components/VEmptyState.kt`
5. `ui/components/VSnackbar.kt`
6. `ui/components/VPullRefresh.kt`
7. `ui/components/VAvatar.kt`
8. `ui/components/VProgressRing.kt`
9. `ui/components/VBadge.kt`
10. `ui/components/VActionCard.kt`
11. `ui/components/VConfirmDialog.kt`
12. `ui/components/VCharts.kt`
13. `ui/components/VStatusDot.kt`
14. `ui/components/FilterChip.kt`
15. `ui/components/VComingSoon.kt`
16. `ui/components/VDatePicker.kt`
17. `ui/components/VTimePicker.kt`

### New Files to Create (Phases 2–7)
18. `ui/screens/parent/ParentUnlinkedGate.kt` (Phase 2)
19. `ui/screens/parent/ParentAcademicsTab.kt` (Phase 4)
20. `ui/screens/parent/ParentFeesTab.kt` (Phase 5)
21. `ui/screens/parent/ParentConversationsTab.kt` (Phase 6)
22. `ui/screens/parent/ParentProfileTab.kt` (Phase 7)

### Files to Modify
- `ui/components/VBackHeader.kt` (Phase 2 — fix dimensions/style)
- `ui/screens/parent/ParentPortalShell.kt` (Phases 2, 4, 5, 6, 7 — wire tabs, fix routing)
- `ui/screens/parent/ParentHomeTab.kt` (Phase 2 — fix styles, apply VStateHost)
- `ui/screens/parent/ParentOverlays.kt` (Phase 3 — rebuild 5 overlays, fix styles)
- `ui/screens/parent/ParentOverlayScaffold.kt` (Phase 3 — body padding fix)
- `ui/screens/parent/ParentRoute.kt` (Phase 8 — deep link paths)
- `shared/.../presentation/ParentViewModel.kt` (Phase 8 — fix initial load bug)

---

*End of PARENT_PORTAL_REBUILD_PLAN.md*
