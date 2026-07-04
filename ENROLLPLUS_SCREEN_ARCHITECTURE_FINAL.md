# Enroll+ — Complete Screen Architecture (Final, Post-Restructuring)

> **Iterations run:** 8. Iteration 8 = God-Mode Audit Pass. Found and fixed: 2 unreachable dead overlay enum values (SchoolOverlay.Calendar, SchoolOverlay.Results), 1 orphaned screen not in D-6 (ParentPewsScreenV2.kt), 6 "future" entry labels that are actually LIVE from Home tab. Website section removed — this document covers the Compose Multiplatform application only.

> **Source of truth:** Compose Multiplatform codebase at `composeApp/src/commonMain/kotlin/com/littlebridge/enrollplus/ui/v2/`. Navigation graph: `NavGraphV2.kt`. Portals: `SchoolPortalV2.kt`, `TeacherPortalV2.kt`, `ParentPortalV2.kt`.

> **Figma:** No Figma file link or MCP connection was provided. All descriptions are derived from live codebase inspection.

---

## Design System Components (build these in Figma first)

| Component | Purpose |
|---|---|
| `VScreenScaffold` | Phone-frame root: centers max-width column, background, topBar + content + floating bottomBar. Adaptive: 440dp phone, 560dp tablet, 720dp desktop. |
| `VTopTabs` | Row of text tabs with animated underline indicator. All sub-tab navigation. |
| `VBottomNav` | 5-or-fewer tab bar pinned to bottom. Icon + label, active lozenge. |
| `ParentDock` | Bespoke floating glass dock for parent portal only. |
| `VBackHeader` | Top app bar: circular back button + centered title + trailing slot. |
| `VCard` | Surface card with rounded corners, optional onClick. |
| `VButton` | Variants: Primary, Secondary, Ghost, Destructive. Sizes: Sm, Md, Lg. |
| `VInput` | Text input with label, error, helper text. |
| `VBadge` | Pill badge. Tones: Accent, Neutral, Success, Warning, Danger, Arctic. |
| `VAvatar` | Circular avatar with initials fallback + optional image URL. |
| `VProgressRing` | Circular progress with optional center label. |
| `VProgressBar` | Linear progress bar. |
| `VEmptyState` | Centered icon-in-circle + title + body + optional action. |
| `VComingSoon` | "PREVIEW" card for unshipped features with "Notify me" affordance. |
| `VConfirmDialog` | Universal destructive-action confirmation: icon + title + message + confirm (Destructive) + cancel (Ghost). |
| `VSnackbar` | Bottom-anchored transient message: icon + text + optional action. Tones: Success, Error, Info, Warning. |
| `VStateHost` | 4-phase state manager: Loading (skeleton) → Error → Empty → Content. 300ms crossfade. |
| `VPullRefresh` | Pull-to-refresh wrapper with brand-tinted indicator. |
| `VDatePicker` | Calendar date picker dialog. |
| `VTimePicker` | Time picker dialog. |
| `VThemePicker` | Theme mode selector: Light / Dark / High Contrast / Custom. |
| `VShimmer` | Shimmer animation for loading placeholders. |
| `VCharts` | Chart components (bar, line, donut). |
| `VActionCard` | Tappable card with icon + title + subtitle + chevron. |
| `VStatusDot` | Colored status indicator dot. |
| `FilterChip` | Rounded pill filter chip with active/inactive state. |
| `CoachMarkOverlay` | First-time tooltip overlay pointing at a target element. |

### Skeleton Loaders

| Skeleton | Used By |
|---|---|
| `SkeletonList` / `SkeletonListRow` | All list-based screens (people, records, announcements, messages) |
| `SkeletonDashboard` | Home screens (school, teacher, parent) |
| `SkeletonProfile` | Profile screens |
| `SkeletonCalendar` | Calendar screens |
| `SkeletonFee` | Fee screens |
| `SkeletonAnnouncements` | Announcement feeds |

### Theme Tokens

| Token | Admin/Parent | Teacher |
|---|---|---|
| Primary accent | Lavender `#7C6FE8` / Navy | Deep Indigo `#2D1FA3` |
| Background | `#FCF8FF` (canvas white) | `#F8F7FF` (warm white) |
| Card surface | `#FFFFFF` | `#FFFFFF` |
| Dark mode | Full support via `VThemeRegistry` | Full support |

---

# PART A — ADMIN PORTAL

> **User:** School Principal, admin staff, accountant. Age 30–60. Medium-high literacy, desktop-leaning. Dense-but-organized info, clear labels, confirm on destructive actions.

## A-0 — Admin Portal Shell

- **Type:** Full screen (shell)
- **Entry point(s):** Post-auth `AuthedFlow` → `SchoolPortalV2`. Deep link from notifications.
- **Primary user:** Principal/admin staff. Medium-high literacy. Needs clear labels, not icon-only.
- **User goal:** 5-tab admin command center.
- **Layout:** `VScreenScaffold` with header (school name + logo + notification bell with badge + calendar icon) and `VBottomNav`: Home, People, Records, Comms, Settings. Content swaps between tabs. Overlays render as full-screen `AnimatedContent` above tabs.
- **Components:** `VScreenScaffold`, `VBottomNav`, `VBackHeader`, `VNavItem`, `AnimatedContent`, `BackHandler`
- **Interactions:** Tap tab → switch with crossfade. Notification bell → Notifications overlay. Calendar icon → Calendar overlay. Back → exits overlay to tabs, or goes Home if on non-home tab.
- **States:** Default, Overlay active, Loading (initial tab fetch).
- **Data:** School name, logo, unread notification count, comms badge count.
- **Consolidation note:** SuperAdmin shares this portal — no separate SuperAdmin UI. See FILE 2 #1.
- **Accessibility:** All nav items have icon + text label. 48dp min tap targets.

---

## A-1 — Home Tab

### Admin → Home

- **Type:** Tab (full screen, scrollable)
- **Entry point:** Default tab. Tap "Home" in bottom nav.
- **Primary user:** Principal. Scans, doesn't read. Wants at-a-glance school health.
- **User goal:** School overview — enrollment, attendance, pending tasks, events, activity.
- **Layout:** Scrollable column. Greeting bar (school name + date) → swipeable insights carousel (3-4 metric cards: students, attendance %, fee collection, pending approvals) → pulse gauge widget (school health score ring + sub-metric bars) → quick actions row (horizontal scroll: Add Announcement, Create Event, Schedule PTM, Add Teacher, Add Student, Analytics) → 2-up KPI grid (class avg, teacher performance) → comms center mini (latest 3 announcements, "View all" → Comms tab) → upcoming events (next 3) → teacher spotlight card → recent achievements list → today's birthdays → activity feed (10 recent staff actions). 140dp bottom padding.
- **Components:** `VGreetingBar`, `VCard`, `VBadge`, `VProgressRing`, `VProgressBar`, `VAvatar`, `VActionCard`, `VSectionHeader`, `HorizontalPager`, staggered entrance
- **Interactions:** Swipe carousel. Tap card → overlay/tab. Pull-to-refresh reloads all widgets. Tap event → Calendar overlay. Tap "View all" comms → Comms tab.
- **States:** Default, Loading (`SkeletonDashboard`), Empty ("Welcome! Start by adding classes and students"), Error (retry), Offline (cached + banner).
- **Data:** Student count, attendance %, fee collection %, pending approvals, health score, class avg, teacher index, announcements, events, spotlight, achievements, birthdays, activity log.

### Admin → Home → Insights Carousel Card (FOO)

- **Type:** Expandable card (swipeable carousel/pager)
- **Entry:** Top of Home tab.
- **User goal:** Show one key metric at a time in premium swipeable format.
- **Layout:** Full-width card: large number, label, trend arrow, comparison text ("vs last week"). 3-4 cards in `HorizontalPager`. Page indicator dots.
- **Components:** `VCard`, `VBadge`, `HorizontalPager`
- **Interactions:** Swipe left/right. Tap → drill-down overlay.
- **States:** Default, Loading (shimmer), Error (inline retry).
- **Data:** Metric value, label, trend direction, comparison period.

### Admin → Home → Pulse Gauge Widget (FOO)

- **Type:** Card with embedded gauge
- **Entry:** Below insights carousel.
- **Layout:** `VProgressRing` centered in card, score in center, label below, 4 sub-metric bars (academics, attendance, engagement, fees).
- **Interactions:** Tap → AnalyticsDashboard overlay.
- **States:** Default, Loading (shimmer ring), Error.
- **Data:** Overall score 0-100, sub-metric scores.

### Admin → Home → Quick Actions Row (FOO)

- **Type:** Horizontal scroll row of circular icon buttons
- **Entry:** Below pulse gauge.
- **Layout:** Horizontal scroll: circular icon buttons with labels — "Add Announcement", "Create Event", "Schedule PTM", "Add Teacher", "Add Student", "Analytics".
- **Interactions:** Tap → corresponding overlay/tab.
- **States:** Default only.
- **Data:** Action icons + labels.

### Admin → Home → Activity Feed (FOO)

- **Type:** Scrollable list section
- **Entry:** Bottom of Home tab.
- **Layout:** Vertical list of activity items: avatar, action text ("posted announcement 'Holiday Notice'"), timestamp. 10 items max, "View all" → AnalyticsDashboard.
- **Interactions:** Tap item → relevant detail.
- **States:** Default, Empty ("No recent activity"), Loading (skeleton).
- **Data:** Staff avatar, name, action, timestamp.

---

## A-2 — People Tab

### Admin → People

- **Type:** Tab (full screen, scrollable)
- **Entry:** Tap "People" in bottom nav.
- **Primary user:** Admin staff. Comfortable with tabbed data views.
- **User goal:** Manage teachers, students, non-teaching staff, alumni.
- **Layout:** "People" title → `VActionCard` "Child link requests" (pending count badge) → `VTopTabs`: Teachers · Students · Non-teaching staff · Alumni. Content swaps below. 140dp bottom padding.
- **Components:** `VTopTabs`, `VActionCard`, `VBadge`, `VCard`, `VAvatar`, `VButton`, `VEmptyState`, `VStateHost`
- **Interactions:** Tap sub-tab → switch. Tap "Child link requests" → LinkRequests overlay. Tap person row → profile overlay. Tap "Add" → dialog.
- **States:** Default, Loading (`SkeletonList`), Empty per sub-tab, Error.
- **Data:** Pending link count, person lists with name/role/avatar/status.

### Admin → People → Teachers Sub-tab

- **Type:** Sub-tab
- **Entry:** Default sub-tab on People.
- **Layout:** "Add Teacher" button → scrollable teacher cards. Each: avatar, name, employee ID, subjects, class assignments, status badge. "Load more" if paginated. Per card: "View" (→ TeacherProfile overlay), "Assign classes" (→ TeacherAssignments overlay), "Deactivate" (→ VConfirmDialog).
- **Components:** `VCard`, `VAvatar`, `VBadge`, `VButton`, `VConfirmDialog`, `VStateHost`, `SkeletonList`
- **Interactions:** Tap card → TeacherProfile. "Assign classes" → TeacherAssignments. "Deactivate" → confirm dialog. "Add Teacher" → AddTeacherDialog. Pull-to-refresh.
- **States:** Default, Loading, Empty ("No teachers added yet"), Error, Success snackbar.
- **Data:** Name, employee ID, subjects, assignments, status.

### Admin → People → Teachers → Add Teacher Dialog (FOO)

- **Type:** Modal (dialog)
- **Entry:** Tap "Add Teacher" button.
- **Layout:** `Dialog` + `VCard`. Fields: Name (VInput), Employee ID (VInput), Initial password (VInput, optional). "Add" (Primary) + "Cancel" (Ghost). Inline validation. Submitting: button spinner.
- **Interactions:** "Add" → validates → submits → closes + snackbar. "Cancel"/scrim → dismisses.
- **States:** Default, Validation error, Submitting, Success snackbar, Error snackbar.
- **Data:** Form fields.

### Admin → People → Students Sub-tab

- **Type:** Sub-tab
- **Entry:** Tap "Students" in People sub-tabs.
- **Layout:** "Add Student" + "Import CSV" buttons → analytics summary card (total, gender, class distribution mini-chart) → scrollable student cards. Each: avatar, name, class+section, roll number, parent name, parent phone, status. Checkbox for batch graduation. "Graduate Selected" appears when checkboxes selected. Search bar for filtering.
- **Components:** `VCard`, `VAvatar`, `VBadge`, `VButton`, `VInput`, `VCharts`, `VStateHost`, `SkeletonList`, `VConfirmDialog`
- **Interactions:** Tap card → StudentProfile. "Add Student" → dialog. "Import CSV" → dialog. Checkboxes → "Graduate Selected" → confirm dialog with year selector. Search filters. Pull-to-refresh.
- **States:** Default, Loading, Empty, Error, Search empty, Graduating.
- **Data:** Name, class/section, roll number, parent info, status, analytics summary.

### Admin → People → Students → Add Student Dialog (FOO)

- **Type:** Modal (dialog)
- **Entry:** Tap "Add Student" button.
- **Layout:** `Dialog` + `VCard`. Fields: Name, Class (dropdown), Section (dropdown), Roll number, Parent phone. "Add" + "Cancel". Validation: phone format, required fields.
- **States:** Default, Validation error, Submitting, Success, Error.
- **Data:** Form fields.

### Admin → People → Students → Import CSV Dialog (FOO)

- **Type:** Modal (dialog)
- **Entry:** Tap "Import CSV" button.
- **Layout:** `Dialog` + `VCard`. CSV format instructions (columns: name, class, section, roll_number, parent_phone). File picker button. Preview of first 5 rows. "Import" (Primary) + "Cancel". Progress bar during import.
- **Interactions:** "Choose file" → picker. Preview shows. "Import" → progress → snackbar.
- **States:** Default, File selected (preview), Importing (progress), Success ("Imported N students"), Error.
- **Data:** CSV instructions, preview, progress.

### Admin → People → Students → Graduate Confirmation Dialog (FOO)

- **Type:** Modal (dialog)
- **Entry:** Select checkboxes → tap "Graduate Selected".
- **Layout:** `VConfirmDialog` variant: warning icon, "Graduate N students?", explanation, academic year dropdown. "Graduate" (Primary) + "Cancel".
- **States:** Default, Graduating (loading), Success, Error.
- **Data:** Student count, year options.

### Admin → People → Non-teaching Staff Sub-tab

- **Type:** Sub-tab
- **Entry:** Tap "Non-teaching staff" in People sub-tabs.
- **Layout:** "Add Staff" button → search bar → scrollable staff cards. Each: avatar, name, role, department, phone, email, status badge. Tap → StaffProfile overlay.
- **Components:** `VCard`, `VAvatar`, `VBadge`, `VButton`, `VInput`, `VStateHost`, `SkeletonList`
- **Interactions:** Tap card → StaffProfile. "Add Staff" → dialog. Search filters. Pull-to-refresh.
- **States:** Default, Loading, Empty, Error, Search empty.
- **Data:** Name, role, department, phone, email, status.

### Admin → People → Non-teaching Staff → Add Staff Dialog (FOO)

- **Type:** Modal (dialog)
- **Entry:** Tap "Add Staff" button.
- **Layout:** `Dialog` + `VCard`. Fields: Name, Role (dropdown), Department, Phone, Email. "Add" + "Cancel".
- **States:** Default, Validation error, Submitting, Success, Error.
- **Data:** Form fields.

### Admin → People → Alumni Sub-tab

- **Type:** Sub-tab
- **Entry:** Tap "Alumni" in People sub-tabs.
- **Layout:** Single `VActionCard`: "Alumni Management" — "View alumni directory, donations, mentorship, and analytics". Tap → Alumni overlay.
- **Interactions:** Tap → Alumni overlay.
- **States:** Default only.
- **Data:** Card title + subtitle.

---

## A-3 — Records Tab

### Admin → Records

- **Type:** Tab (full screen, scrollable)
- **Entry:** Tap "Records" in bottom nav.
- **Primary user:** Principal/admin. Data-dense views OK.
- **User goal:** Monitor syllabus coverage, pace, attendance, marks, fees, documents school-wide.
- **Layout:** "Records" title → `VTopTabs`: Coverage · Pace · Attendance · Marks · Fee · Documents. Content swaps below. 140dp bottom padding.
- **Components:** `VTopTabs`, `VStateHost`, `VCard`, `VProgressBar`, `VBadge`, `VCharts`, `VEmptyState`, `SkeletonList`
- **Interactions:** Tap sub-tab → lazy-loads data. Pull-to-refresh per tab.
- **States:** Default, Loading (skeleton per tab), Empty per tab, Error per tab.
- **Data:** Varies by sub-tab.

### Admin → Records → Coverage Sub-tab

- **Type:** Sub-tab
- **Layout:** Overall coverage card (VProgressBar, school-wide %) → department breakdown cards (name, %, bar) → milestone list (completed/upcoming units) → alerts list (classes behind, "Resolve" button).
- **Interactions:** "Resolve" → alert resolution dialog. Tap department → drill-down (future).
- **States:** Default, Loading (`SkeletonList(rows=5)`), Empty ("No coverage data yet"), Error.
- **Data:** Overall %, department %, milestones, alerts.

### Admin → Records → Pace Sub-tab

- **Type:** Sub-tab
- **Layout:** "Recalculate" button → pace alert cards. Each: class, subject, status badge (Ahead/On Track/Behind), deviation %, "Resolve" button. Resolved alerts dimmed.
- **Interactions:** "Recalculate" → loading state. "Resolve" → resolution dialog with note field.
- **States:** Default, Loading, Empty ("No pace alerts. All classes on track."), Error, Recalculating.
- **Data:** Class, subject, pace status, deviation %, notes.

### Admin → Records → Attendance Sub-tab

- **Type:** Sub-tab
- **Layout:** Overall attendance card (VProgressRing) → class-wise breakdown (name, %, bar, trend) → date selector.
- **Interactions:** Tap date → `VDatePicker` → loads historical. Tap class → DailyAttendance overlay.
- **States:** Default, Loading, Empty, Error.
- **Data:** Overall %, per-class %, date, trend.

### Admin → Records → Marks Sub-tab

- **Type:** Sub-tab
- **Layout:** Overall average card → class-wise breakdown (name, avg, count, trend) → subject-wise average bar chart.
- **Interactions:** Tap class → ClassPerformance overlay.
- **States:** Default, Loading, Empty, Error.
- **Data:** Overall avg, per-class avg, assessment count, subject averages.

### Admin → Records → Fee Sub-tab

- **Type:** Sub-tab
- **Layout:** Total collection card (amount + %) → class-wise breakdown (collected, pending, %, bar) → top 10 defaulters list.
- **Interactions:** Tap defaulter → StudentProfile overlay.
- **States:** Default, Loading, Empty, Error.
- **Data:** Total collected, pending, per-class amounts, defaulter names.

### Admin → Records → Documents Sub-tab

- **Type:** Sub-tab
- **Layout:** `VEmptyState`: "Document library — Circulars, timetables and holiday lists are uploaded via Announcements and the Academic Calendar."
- **States:** Empty only (placeholder for future).
- **Data:** None.

---

## A-4 — Comms Tab

### Admin → Comms

- **Type:** Tab (full screen, scrollable)
- **Entry:** Tap "Comms" in bottom nav. Badge shows unread count.
- **Primary user:** Admin staff. Manages school-to-parent communication.
- **User goal:** Manage announcements, messages, PTM scheduling, notification center.
- **Layout:** "Communications" title → `VTopTabs`: Announcements · Messages · PTM · Notifications. Content swaps below. 140dp bottom padding. `VPullRefresh` wraps the whole screen.
- **Components:** `VTopTabs`, `VPullRefresh`, `VCard`, `VBadge`, `VButton`, `VStateHost`, `SkeletonAnnouncements`, `FilterChip`
- **Interactions:** Tap sub-tab → switch. Tap announcement → detail leaf. Tap "New" → CreateEvent overlay. Tap "Scheduled" → ScheduledMessages overlay.
- **States:** Default, Loading, Empty, Error, Offline (cached).
- **Data:** Announcements list, messages/PTM entry cards, notification center link.

### Admin → Comms → Announcements Sub-tab

- **Type:** Sub-tab
- **Layout:** "Announcements" header + "Scheduled" button (ghost, clock icon) + "New" button (primary, plus icon). Below: category filter chips (All + categories from data). Below: staggered list of announcement cards. Each: title, category badge (or "Calendar Only" warning badge), date, description preview. Tap → `AnnouncementDetailV2` leaf.
- **Interactions:** Tap card → detail leaf (full screen, back header, title, date, category badge, body). "New" → CreateEvent overlay. "Scheduled" → ScheduledMessages overlay. Filter chips filter list. Pull-to-refresh.
- **States:** Default, Loading (`SkeletonAnnouncements`), Empty ("No announcements yet. Posts you publish to parents and staff will appear here."), Error.
- **Data:** Title, category, date, description, isCalendarOnly flag.

### Admin → Comms → Messages Sub-tab

- **Type:** Sub-tab
- **Layout:** `CommsEntryCard`: icon (Chat), "Parent messages", "Open two-way parent ↔ school message threads." Tap → Messages overlay.
- **Interactions:** Tap → Messages overlay.
- **States:** Default only.
- **Data:** Card metadata.

### Admin → Comms → PTM Sub-tab

- **Type:** Sub-tab
- **Layout:** `CommsEntryCard`: icon (Calendar), "Parent–Teacher meetings", "Schedule PTMs and track slot bookings." Tap → SchedulePTM overlay.
- **Interactions:** Tap → SchedulePTM overlay.
- **States:** Default only.
- **Data:** Card metadata.

### Admin → Comms → Notifications Sub-tab

- **Type:** Sub-tab
- **Layout:** `CommsEntryCard`: icon (Bell), "Notification center", "View delivery logs & send push notifications." Tap → Notifications overlay.
- **Interactions:** Tap → Notifications overlay.
- **States:** Default only.
- **Data:** Card metadata.

### Admin → Comms → Announcement Detail Leaf (FOO)

- **Type:** Full screen leaf (in-screen, not overlay)
- **Entry:** Tap announcement card in Announcements sub-tab.
- **Layout:** `VBackHeader` ("Announcement") → scrollable: title (h2), date + "Posted by School Administration", category badge, body text (line height 22.4sp).
- **Interactions:** Back → returns to announcements list.
- **States:** Default, Unavailable ("Announcement unavailable" if not found).
- **Data:** Title, date, category, description.

---

## A-5 — Settings Tab

### Admin → Settings

- **Type:** Tab (full screen, scrollable)
- **Entry:** Tap "Settings" in bottom nav.
- **Primary user:** Principal/admin. Needs clear setting labels with descriptions.
- **User goal:** Configure school profile, academic year, classes, transport, scholarships, branding, ID cards, library, fees, notifications, theme, logout.
- **Layout:** "Settings" title → `VStateHost` (loading/error/content) → institutional profile health card (school name, completion %, progress ring, storage usage bar, visibility badge, learning model, language) → settings rows list → theme picker card. 140dp bottom padding.
- **Components:** `VStateHost`, `VCard`, `VProgressRing`, `VProgressBar`, `VBadge`, `VThemePicker`, `VConfirmDialog`, `VButton`, `VIcons`
- **Interactions:** Tap profile card → EditProfile overlay. Tap any setting row → corresponding overlay. Tap "Logout" → `VConfirmDialog`. Tap theme option → switches theme immediately.
- **States:** Default, Loading (profile fetch), Error (retry).
- **Data:** School name, completion %, storage used/total, visibility, learning model, language, theme mode.

### Admin → Settings → Institutional Profile Health Card (FOO)

- **Type:** Expandable card
- **Layout:** Teal-tinted header: school icon + school name + next-step guidance + chevron. Badges: "Public/Private profile", "Tour live" if active. Body: `VProgressRing` (completion %) + progress bar + "Profile completion" label. Below: media storage card (cream background, used/total, progress bar). Below: learning model + language badges.
- **Interactions:** Tap card → EditProfile overlay.
- **States:** Default, Loading (skeleton).
- **Data:** School name, completion %, storage, visibility, tour status, learning model, language.

### Admin → Settings → Settings Rows (FOO)

- **Type:** Scrollable list of tappable rows
- **Layout:** Each row: icon in rounded square + title + subtitle + "Coming soon" badge OR chevron. Rows:
  1. Academic year — "Manage term dates & holidays" → AcademicYear overlay
  2. Classes & subjects — "Classes, subjects, bell schedule & timetable" → ClassesSubjects overlay
  3. Teacher management — "Add, view & remove teachers" → People tab (Teachers sub-tab)
  4. Transport Management — "Routes, vehicles & student assignments" → TransportManagement overlay
  5. Scholarship Management — "Schemes, applications & renewals" → ScholarshipManagement overlay
  6. Branding Kit — "Logo, colors & custom subdomain" → BrandingKit overlay
  7. ID Cards — "Templates, generation & PDF export" → IdCards overlay
  8. Library Management — "Catalog, issues, returns & fines" → Library overlay
  9. Fee structure — "Edit heads & amounts for next cycle" → (future fee config)
  10. Notifications — "Channels & quiet hours" → Notifications overlay
  11. Data export — "CSV / PDF / UDISE" — Coming Soon badge
  12. Help & support — "Email support@enrollplus..." → opens mailto
  13. Logout — "Sign out of the admin console" → VConfirmDialog
- **Interactions:** Tap row → overlay or action. "Coming soon" rows not tappable.
- **States:** Default only.
- **Data:** Row metadata.

### Admin → Settings → Logout Confirmation Dialog (FOO)

- **Type:** Modal (dialog)
- **Entry:** Tap "Logout" row.
- **Layout:** `VConfirmDialog`: alert triangle icon, "Log out?", "You'll be signed out of the admin console and need to sign in again." "Log out" (Destructive) + "Cancel" (Ghost).
- **Interactions:** "Log out" → onLogout callback. "Cancel"/scrim → dismisses.
- **States:** Default only.
- **Data:** None.

### Admin → Settings → Theme Picker (FOO)

- **Type:** Expandable card with theme options
- **Layout:** `VCard` containing `VThemePicker`: options for Light, Dark, High Contrast, Custom theme. Current selection highlighted. Custom theme shows saved theme name.
- **Interactions:** Tap option → switches theme immediately (300ms crossfade).
- **States:** Default only.
- **Data:** Current theme mode, custom theme ID.

---

## A-6 — Admin Overlays (30 full-screen overlays)

All overlays render via `AnimatedContent` above the tab content. Each has a `VBackHeader` with back button. Back press returns to tabs.

### A-6.01 — Notifications Overlay

- **Type:** Full screen overlay
- **Entry:** Notification bell in header. "Notifications" sub-tab in Comms. "Notifications" settings row.
- **Layout:** `VBackHeader` ("Notifications") → filter tabs (All / Unread) → scrollable notification list. Each: icon, title, body preview, timestamp, read/unread indicator. Tap → marks read + navigates to deep-linked screen.
- **Components:** `VBackHeader`, `VCard`, `VBadge`, `VStateHost`, `SkeletonList`, `VSnackbar`
- **Interactions:** Tap notification → mark read + deep link. Swipe to dismiss (future). Pull-to-refresh.
- **States:** Default, Loading, Empty ("No notifications"), Error, Offline.
- **Data:** Notification title, body, timestamp, read status, deep link target.
- **Consolidation note:** Same `NotificationsScreenV2` composable used by all 3 portals with role-specific data. Intentionally shared — different permission scopes. See FILE 2 #2.

### A-6.02 — Calendar Overlay (Legacy) ⚠️ UNREACHABLE

- **Type:** Full screen overlay
- **Entry:** NONE — **DEAD CODE.** `SchoolOverlay.Calendar` exists in the enum and has a `when` branch that renders `AcademicCalendarScreenV2`, but NO code ever sets `overlay = SchoolOverlay.Calendar`. The header calendar icon calls `onOpenCalendar` which is wired to `SchoolOverlay.AcademicCalendarPlatform` (A-6.03). Deep link `/school/calendar` also maps to `AcademicCalendarPlatform`. This enum value is a dead remnant.
- **Resolution:** Remove `SchoolOverlay.Calendar` from enum + remove its `when` branch. See FILE 2 #21.
- **Layout:** `VBackHeader` ("Calendar") → month view calendar with event dots. Tap date → shows events for that date. Tap event → detail.
- **States:** Default, Loading, Empty ("No events this month"), Error.
- **Data:** Events list with dates.
- **Consolidation note:** Legacy calendar. Superseded by AcademicCalendarPlatform overlay. See FILE 2 #3.

### A-6.03 — Academic Calendar Platform Overlay

- **Type:** Full screen overlay
- **Entry:** Home tab "Create Event" quick action. Comms "New" button.
- **Layout:** Premium calendar platform: month/week/day views. Event cards with category colors. Filter by category. "Create Event" FAB.
- **Components:** `VBackHeader`, `VCard`, `VBadge`, `VCharts` (mini), `VButton`, `VStateHost`
- **Interactions:** Swipe between months. Tap day → day detail. Tap event → event detail. "Create Event" → CreateEvent wizard.
- **States:** Default, Loading, Empty, Error.
- **Data:** Events with dates, categories, descriptions.

### A-6.04 — Create Event Wizard Overlay

- **Type:** Full screen overlay (7-step wizard)
- **Entry:** "Create Event" FAB in AcademicCalendarPlatform. "New" button in Comms Announcements.
- **Layout:** `VBackHeader` ("New Event") → 7-step wizard with progress indicator. Steps: Event type → Title & description → Date & time → Recurrence → Target audience → Category → Review & publish. Each step: form fields, "Next"/"Back" buttons. Final step: "Publish" button.
- **Components:** `VBackHeader`, `VInput`, `VButton`, `VDatePicker`, `VTimePicker`, `VBadge`, `VProgressBar` (step indicator), `VSnackbar`
- **Interactions:** "Next" → validates → advances step. "Back" → previous step. "Publish" → creates event + announcement → success snackbar → closes.
- **States:** Default, Validation error per step, Publishing (loading), Success, Error.
- **Data:** Event type, title, description, date, time, recurrence, audience, category.

### A-6.05 — Academic Year Management Overlay

- **Type:** Full screen overlay
- **Entry:** Settings → "Academic year" row.
- **Layout:** `VBackHeader` ("Academic Year") → list of academic years. Current year highlighted. Term cards within each year (term name, start/end dates, holiday list). "Add Year" button. "Add Term" button per year.
- **Interactions:** Tap year → expand/collapse terms. "Add Year" → dialog. "Add Term" → dialog. Tap term → edit dialog.
- **States:** Default, Loading, Empty ("No academic years configured"), Error.
- **Data:** Year name, terms, start/end dates, holidays.

### A-6.06 — Messages Overlay

- **Type:** Full screen overlay
- **Entry:** Comms → Messages sub-tab. Header messages icon. Deep link from notification.
- **Layout:** `VBackHeader` ("Messages") → inbox list of conversation threads. Each: parent avatar, parent name, last message preview, timestamp, unread badge. Tap → conversation view (messages list + compose bar). "Compose new" button → thread picker.
- **Components:** `VBackHeader`, `VCard`, `VAvatar`, `VBadge`, `VInput` (compose), `VButton`, `VStateHost`, `SkeletonList`
- **Interactions:** Tap thread → conversation. Type + send. Long-press message → context menu (delete, forward — future). Back → inbox. Pull-to-refresh.
- **States:** Default, Loading, Empty ("No conversations yet"), Error, Sending (compose loading), Offline (queued indicator).
- **Data:** Thread list, messages, parent info, unread counts.
- **Consolidation note:** Admin uses `MessagesScreenV2` (different from parent/teacher variants). See FILE 2 #4.

### A-6.07 — Leave Requests Overlay

- **Type:** Full screen overlay
- **Entry:** People tab (future direct link). Settings (future).
- **Layout:** `VBackHeader` ("Leave Requests") → filter tabs (Pending / Approved / Rejected) → list of leave request cards. Each: student avatar, name, class, leave dates, reason, status badge. "Approve"/"Reject" buttons for pending.
- **Interactions:** Tap "Approve" → updates status + snackbar. Tap "Reject" → reject reason dialog → updates + snackbar. Filter tabs switch list.
- **States:** Default, Loading, Empty ("No leave requests"), Error.
- **Data:** Student name, class, dates, reason, status.

### A-6.08 — Link Requests Overlay

- **Type:** Full screen overlay
- **Entry:** People tab → "Child link requests" card.
- **Layout:** `VBackHeader` ("Link Requests") → list of parent-child linking requests. Each: parent name, phone, child name, class, request date, status. "Approve"/"Reject" buttons.
- **Interactions:** "Approve" → links parent to child + snackbar. "Reject" → reason dialog + snackbar.
- **States:** Default, Loading, Empty ("No pending requests"), Error.
- **Data:** Parent name, phone, child name, class, date, status.

### A-6.09 — Admissions CRM Overlay

- **Type:** Full screen overlay
- **Entry:** Home tab (future widget). Settings (future).
- **Layout:** `VBackHeader` ("Admissions") → pipeline view: inquiry → application → assessment → enrolled. Cards in each stage. Drag-to-move (future). Add inquiry button.
- **States:** Default, Loading, Empty ("No admissions inquiries yet"), Error.
- **Data:** Applicant name, stage, contact, class applied for.

### A-6.10 — Results / ResultsPublish Overlay ⚠️ UNREACHABLE

- **Type:** Full screen overlay
- **Entry:** NONE — **DEAD CODE.** `SchoolOverlay.Results` exists in the enum and has a `when` branch that renders `ResultsPublishScreenV2`, but NO code ever sets `overlay = SchoolOverlay.Results`. Deep links for `/school/report-card` map to `SchoolOverlay.ReportPublish` (A-6.29) instead. This enum value is a dead remnant.
- **Resolution:** Remove `SchoolOverlay.Results` from enum + remove its `when` branch. Either delete `ResultsPublishScreenV2.kt` or merge its functionality into `AdminReportPublishScreen` (A-6.29). See FILE 2 #22.
- **Layout:** `VBackHeader` ("Publish Results") → class selector → assessment selector → results table (student name, marks, grade). "Publish" button → confirmation → sends results to parents.
- **States:** Default, Loading, Empty, Error, Publishing.
- **Data:** Class, assessment, student marks, grades.

### A-6.11 — Schedule PTM Overlay

- **Type:** Full screen overlay
- **Entry:** Comms → PTM sub-tab. Home quick action.
- **Layout:** `VBackHeader` ("Schedule PTM") → create PTM event form (date, time slots, class, subject) → slot booking overview. Existing PTM events list below.
- **Interactions:** Create PTM → form → save. Tap existing PTM → booking details (parents who booked, available slots).
- **States:** Default, Loading, Empty, Error, Saving.
- **Data:** PTM date, slots, class, bookings.

### A-6.12 — Daily Attendance Overlay

- **Type:** Full screen overlay
- **Entry:** Records → Attendance sub-tab → class row.
- **Layout:** `VBackHeader` ("Daily Attendance") → class selector → date selector → student list with present/absent/late toggle per student. Summary bar at top (present count, absent count, %). "Save" button.
- **States:** Default, Loading, Empty, Error, Saving.
- **Data:** Student names, attendance status, date, class.

### A-6.13 — Class Performance Overlay

- **Type:** Full screen overlay
- **Entry:** Records → Marks sub-tab → class row.
- **Layout:** `VBackHeader` ("Class Performance") → class name header → performance metrics (avg marks, attendance %, syllabus coverage) → subject-wise breakdown chart → student ranking list.
- **States:** Default, Loading, Empty, Error.
- **Data:** Class avg, subject averages, student rankings.

### A-6.14 — Teacher Performance Overlay

- **Type:** Full screen overlay
- **Entry:** People → teacher card (future). Home tab (future).
- **Layout:** `VBackHeader` ("Teacher Performance") → teacher name + avatar → metrics (classes taught, syllabus coverage, avg class performance, attendance marking rate) → class-wise breakdown.
- **States:** Default, Loading, Empty, Error.
- **Data:** Teacher metrics, class breakdown.

### A-6.15 — Analytics Dashboard Overlay

- **Type:** Full screen overlay
- **Entry:** Home → pulse gauge. Home → activity feed "View all". Home → quick actions "Analytics".
- **Layout:** `VBackHeader` ("Analytics") → KPI cards row → charts (enrollment trend, attendance trend, fee collection trend, academic performance trend) → filter by date range/class.
- **States:** Default, Loading, Empty, Error.
- **Data:** KPIs, trend charts, filters.

### A-6.16 — Edit School Profile Overlay

- **Type:** Full screen overlay
- **Entry:** Settings → institutional profile health card.
- **Layout:** `VBackHeader` ("Edit Profile") → form: school name, board, address, principal name, contact phone, contact email, website, logo upload, gallery upload, learning model, primary language, visibility toggle. "Save" button.
- **States:** Default, Loading (profile fetch), Saving, Success, Error.
- **Data:** All school profile fields.

### A-6.17 — Student Roster Overlay

- **Type:** Full screen overlay
- **Entry:** People → Students sub-tab (alternative view).
- **Layout:** `VBackHeader` ("Student Roster") → class/section filter → searchable student table (name, roll, parent phone, status). Export button. Bulk select + actions.
- **States:** Default, Loading, Empty, Error.
- **Data:** Student list, class filters.

### A-6.18 — Student Profile Overlay

- **Type:** Full screen overlay
- **Entry:** People → student card. Records → Fee → defaulter. Deep link from notification.
- **Layout:** `VBackHeader` (student name) → student header card (avatar, name, class, roll, parent info) → tabbed sections: Academic (marks, attendance, syllabus), Fees (balance, history), Health (records), PEWS (risk indicators). Edit button.
- **States:** Default, Loading, Error.
- **Data:** Student profile, academic records, fee status, health, PEWS.

### A-6.19 — PEWS Cohort Overlay

- **Type:** Full screen overlay
- **Entry:** Home tab (future widget). Records (future).
- **Layout:** `VBackHeader` ("Early Warning System") → cohort overview (at-risk count, trend) → risk distribution chart → student list sorted by risk score. Each: avatar, name, risk level badge (High/Medium/Low), primary risk factor. Tap → PewsStudentDetail.
- **States:** Default, Loading, Empty ("No students at risk"), Error, Feature-disabled (KillSwitchGuard).
- **Data:** Risk scores, risk factors, student list.
- **Accessibility:** PEWS label expanded as "Predictive Early Warning System" on first view.

### A-6.20 — PEWS Student Detail Overlay

- **Type:** Full screen overlay
- **Entry:** PEWS Cohort → student card.
- **Layout:** `VBackHeader` (student name) → risk score gauge → risk factor breakdown (attendance, academics, engagement, behavior) → historical trend chart → recommended interventions list → "Log intervention" button.
- **States:** Default, Loading, Error.
- **Data:** Risk score, factor breakdown, trend, interventions.

### A-6.21 — Teacher Profile Overlay

- **Type:** Full screen overlay
- **Entry:** People → teacher card.
- **Layout:** `VBackHeader` (teacher name) → teacher header (avatar, name, employee ID, subjects) → class assignments list → performance summary → leave history. "Edit" button. "Assign Classes" button.
- **States:** Default, Loading, Error.
- **Data:** Teacher profile, assignments, performance, leave.

### A-6.22 — Teacher Assignments Overlay

- **Type:** Full screen overlay
- **Entry:** People → teacher card → "Assign classes".
- **Layout:** `VBackHeader` ("Assign Classes") → teacher name → class+subject assignment list with add/remove. "Add Assignment" → class+subject picker dialog.
- **States:** Default, Loading, Saving, Error.
- **Data:** Teacher name, current assignments, available classes/subjects.

### A-6.23 — Staff Profile Overlay

- **Type:** Full screen overlay
- **Entry:** People → Non-teaching staff → staff card.
- **Layout:** `VBackHeader` (staff name) → staff header (avatar, name, role, department) → contact info → employment details. "Edit" button.
- **States:** Default, Loading, Error.
- **Data:** Staff profile, contact, employment.

### A-6.24 — Health Records Overlay

- **Type:** Full screen overlay
- **Entry:** Settings (future). Student Profile → Health tab.
- **Layout:** `VBackHeader` ("Health Records") → student selector → health profile (blood group, allergies, conditions, immunizations, emergency contact) → incident log (date, type, treatment, notes). "Add Incident" button.
- **States:** Default, Loading, Empty, Error.
- **Data:** Health profile, immunizations, incidents.

### A-6.25 — Alumni Overlay

- **Type:** Full screen overlay
- **Entry:** People → Alumni sub-tab.
- **Layout:** `VBackHeader` ("Alumni") → alumni directory (graduation year filter, search) → alumni cards (name, graduation year, current occupation, contact, photo). "Add Alumnus" button. Sub-sections: Donations, Mentorship, Analytics.
- **States:** Default, Loading, Empty, Error.
- **Data:** Alumni profiles, donation history, mentorship connections.

### A-6.26 — Alumni Detail Overlay

- **Type:** Full screen overlay
- **Entry:** Alumni overlay → alumni card.
- **Layout:** `VBackHeader` (alumni name) → profile header → contact info → career history → donation history → mentorship connections → event attendance.
- **States:** Default, Loading, Error.
- **Data:** Full alumni profile.

### A-6.27 — Alumni Campaign Overlay

- **Type:** Full screen overlay
- **Entry:** Alumni overlay → campaign card.
- **Layout:** `VBackHeader` ("Campaign") → campaign details (title, goal, raised, progress bar) → donor list → "Share" button.
- **States:** Default, Loading, Error.
- **Data:** Campaign info, donations.

### A-6.28 — Transport Management Overlay

- **Type:** Full screen overlay
- **Entry:** Settings → "Transport Management" row.
- **Layout:** `VBackHeader` ("Transport") → tabs: Routes · Vehicles · Students. Route list (name, stops, timing, assigned vehicle). Vehicle list (number, capacity, driver). Student assignments (student, route, stop).
- **States:** Default, Loading, Empty, Error.
- **Data:** Routes, vehicles, drivers, student assignments.

### A-6.29 — Report Publish Overlay

- **Type:** Full screen overlay
- **Entry:** Home tab → "Publish Reports" card. Deep link `/school/report-card`.
- **Layout:** `VBackHeader` ("Publish Reports") → class selector → term selector → report card preview list → "Publish All" button → confirmation.
- **States:** Default, Loading, Empty, Error, Publishing.
- **Data:** Class, term, report cards.

### A-6.30 — Report Effectiveness Overlay

- **Type:** Full screen overlay
- **Entry:** Home tab → "Report Effectiveness" card. Deep link `/school/report-effectiveness`.
- **Layout:** `VBackHeader` ("Report Effectiveness") → report delivery stats (sent, opened, acknowledged) → parent engagement metrics → class-wise breakdown.
- **States:** Default, Loading, Empty, Error.
- **Data:** Delivery stats, engagement metrics.

### A-6.31 — Scholarship Management Overlay

- **Type:** Full screen overlay
- **Entry:** Settings → "Scholarship Management" row.
- **Layout:** `VBackHeader` ("Scholarships") → scheme list (name, eligibility, amount, deadline, applications count). Tap scheme → application list. "Add Scheme" button. Approve/reject applications.
- **States:** Default, Loading, Empty, Error.
- **Data:** Schemes, applications, amounts, deadlines.

### A-6.32 — Branding Kit Overlay

- **Type:** Full screen overlay
- **Entry:** Settings → "Branding Kit" row.
- **Layout:** `VBackHeader` ("Branding") → logo upload, color picker (primary, secondary, accent), subdomain configuration, preview pane (shows how parent app looks with branding). "Save" button.
- **States:** Default, Loading, Saving, Success, Error.
- **Data:** Logo, colors, subdomain, preview.

### A-6.33 — ID Cards Overlay

- **Type:** Full screen overlay
- **Entry:** Settings → "ID Cards" row.
- **Layout:** `VBackHeader` ("ID Cards") → 3 sub-tabs: Templates · Generate · Cards. Templates: template gallery with design options. Generate: select class/template → generate PDF. Cards: individual student ID card preview + print.
- **States:** Default, Loading, Empty, Generating (progress), Error.
- **Data:** Templates, student data, generated cards.

### A-6.34 — Library Overlay (School)

- **Type:** Full screen overlay
- **Entry:** Settings → "Library Management" row.
- **Layout:** `VBackHeader` ("Library") → 14 sub-tabs via `VTopTabs`: Dashboard · Books · Copies · Issues · QuickIssue · BulkReturn · Categories · Audit · Announcements · Acquisition · Reservations · History · More · Settings. Dashboard: stats (total books, issued, available, overdue). Books: searchable catalog. Issues: active issue list. QuickIssue: scan/search book + student → issue. BulkReturn: multi-select returns. Each tab has its own content + states.
- **Components:** `VTopTabs`, `VCard`, `VBadge`, `VInput`, `VButton`, `VStateHost`, `VCharts`, `QrCodeImage`, `SkeletonList`
- **Interactions:** Tab switch. Search. Issue/return flows. QR code scan (future). Add book dialog. Category management.
- **States:** Per tab: Default, Loading, Empty, Error.
- **Data:** Book catalog, copies, issues, returns, fines, categories, audit log, reservations, acquisition requests.
- **Consolidation note:** School library has 14 tabs vs student library's 9 tabs — different permission scopes, intentionally separate. See FILE 2 #5.

### A-6.35 — Scheduled Messages Overlay

- **Type:** Full screen overlay
- **Entry:** Comms → Announcements → "Scheduled" button.
- **Layout:** `VBackHeader` ("Scheduled Messages") → list of scheduled announcements. Each: title, scheduled date/time, target audience, status (Pending/Sent/Failed). Edit/Cancel buttons.
- **States:** Default, Loading, Empty ("No scheduled messages"), Error.
- **Data:** Scheduled message details, status.

### A-6.36 — Event Registration Overlay (Admin)

- **Type:** Full screen overlay
- **Entry:** Calendar → event with registration. Home (future).
- **Layout:** `VBackHeader` ("Event Registration") → event details → registration list (parent name, student name, status). Export list button. Capacity indicator.
- **States:** Default, Loading, Empty, Error.
- **Data:** Event details, registrations, capacity.

### A-6.37 — Classes & Subjects Overlay

- **Type:** Full screen overlay
- **Entry:** Settings → "Classes & subjects" row.
- **Layout:** `VBackHeader` ("Classes & Subjects") → `VTopTabs`: Classes · Subjects · Schedule · Exceptions & Requests. 
  - Classes: class cards (code, name, sections), create/edit/delete dialogs.
  - Subjects: class selector → subject list, create/edit/delete dialogs.
  - Schedule: timetable grid (weekday × period), create/edit/delete period dialogs, bulk create, teacher inline creation.
  - Exceptions & Requests: exception list (date-specific schedule changes), change request list (approve/reject).
- **Components:** `VTopTabs`, `VCard`, `VInput`, `VButton`, `VConfirmDialog`, `VDatePicker`, `VTimePicker`, `VBadge`, `VStateHost`
- **Interactions:** CRUD operations on classes, subjects, periods. Approve/reject change requests. 10+ dialog/sheet composables for create/edit operations.
- **States:** Per tab: Default, Loading, Empty, Error, Saving, Success.
- **Data:** Classes, sections, subjects, timetable periods, exceptions, change requests.

### A-6.38 — Class Detail Overlay

- **Type:** Full screen overlay
- **Entry:** Classes & Subjects → class card. People → student → class info.
- **Layout:** `VBackHeader` (class name) → class header (name, code, section, student count) → tabs: Students · Subjects · Timetable · Teachers. Student list, subject list, weekly timetable, assigned teachers.
- **States:** Default, Loading, Error.
- **Data:** Class info, students, subjects, timetable, teachers.

---

*End of Part A — Admin Portal*

---

# PART B — TEACHER PORTAL

> **User:** Classroom teacher. Age 22–55. Medium literacy, time-poor, using app between/during classes. One-handed use, short attention windows, frequent interruptions.
> **Design calibration:** Speed-first: 1–2 taps to core actions (attendance, homework, grading). Big tap targets. Minimal decorative motion. Clear "done" confirmation. No gesture-only discoverability.

## B-0 — Teacher Portal Shell

- **Type:** Full screen (shell)
- **Entry point(s):** Post-auth `AuthedFlow` → `TeacherPortalV2`. Deep link from notification.
- **Primary user:** Teacher. Speed-first, one-handed, short attention windows.
- **User goal:** 5-tab teacher command center for daily classroom operations.
- **Layout:** `VScreenScaffold` with header (teacher name + notification bell) and `VBottomNav`: Home, Update, Classes, Timetable, Profile. Content swaps between tabs. Overlays render as full-screen `AnimatedContent` above tabs.
- **Components:** `VScreenScaffold`, `VBottomNav`, `VBackHeader`, `VNavItem`, `AnimatedContent`, `BackHandler`
- **Interactions:** Tap tab → switch with crossfade. Notification bell → Notifications overlay. Back → exits overlay to tabs, or goes Home if on non-home tab.
- **States:** Default, Overlay active, Loading.
- **Data:** Teacher name, unread notification count, update tab badge (outstanding obligations count).
- **Accessibility:** All nav items have icon + text label. 48dp min tap targets.

---

## B-1 — Home Tab

### Teacher → Home

- **Type:** Tab (full screen, scrollable)
- **Entry:** Default tab. Tap "Home" in bottom nav.
- **Primary user:** Teacher. Wants to see today's schedule, pending tasks, quick actions. Scans in 5 seconds between classes.
- **User goal:** Today's overview — schedule, obligations, quick access to attendance/homework/marks.
- **Layout:** Scrollable column. Greeting bar (teacher name + date) → today's class timeline (horizontal scroll of period cards: class name, subject, time, room) → obligations summary card (outstanding count with badge: unmarked attendance, ungraded homework, pending syllabus updates) → quick action shortcuts (Attendance, Homework, Marks, Syllabus — each → Update tab with tool pre-selected) → class cards list (assigned classes with student count, latest homework, recent marks) → notification bell → upcoming events mini. 140dp bottom padding.
- **Components:** `VGreetingBar`, `VCard`, `VBadge`, `VProgressRing`, `VActionCard`, `VSectionHeader`, staggered entrance
- **Interactions:** Tap period card → Classes tab. Tap quick action → Update tab with tool. Tap class card → Classes tab. Tap obligation → Update tab. Pull-to-refresh.
- **States:** Default, Loading (`SkeletonDashboard`), Empty ("Welcome! Your schedule and tasks will appear here."), Error, Offline.
- **Data:** Today's periods, obligation counts, class assignments, recent activity.

### Teacher → Home → Today's Class Timeline (FOO)

- **Type:** Horizontal scroll card row
- **Layout:** Horizontal scroll of period cards. Each: class name, subject, time range, room number, status badge (Upcoming/In Progress/Done). Current period highlighted.
- **Interactions:** Tap card → Classes tab (filtered to that class). Swipe to scroll.
- **States:** Default, Empty ("No classes today"), Loading.
- **Data:** Period time, class, subject, room, status.

### Teacher → Home → Obligations Summary Card (FOO)

- **Type:** Card with expandable detail
- **Layout:** Card with badge count (total outstanding). Below: breakdown rows — "Unmarked attendance: N classes", "Ungraded homework: N submissions", "Pending syllabus: N units". Each row tappable.
- **Interactions:** Tap row → Update tab with tool pre-selected. Tap card → Update tab.
- **States:** Default, All-clear ("You're all caught up! 🎉").
- **Data:** Outstanding counts per category.

### Teacher → Home → Check-in Popup (FOO)

- **Type:** Modal (popup)
- **Entry:** Appears when teacher has an active class period and hasn't marked attendance.
- **Layout:** Bottom sheet popup: "Mark attendance for Class 7-B now?" with "Mark" (Primary) + "Later" (Ghost) buttons.
- **Interactions:** "Mark" → Update tab → Attendance tool pre-selected. "Later" → dismisses (reminds after 10 min).
- **States:** Default only.
- **Data:** Class name, period info.

---

## B-2 — Update Tab

### Teacher → Update

- **Type:** Tab (full screen)
- **Entry:** Tap "Update" in bottom nav. Badge shows outstanding count. Deep link from Home quick actions.
- **Primary user:** Teacher. Speed-first — needs to pick class, pick tool, do action, confirm done.
- **User goal:** Mark attendance, enter marks, update syllabus, assign homework, create lesson plans.
- **Layout:** Scope gate → tool selector → tool screen. If no class selected: `TeacherScopeSelector` (class allocation picker — dropdown of assigned classes). Once class selected: segmented switch (`UpdateTool` enum) with 5 tools: Attendance, Marks, Syllabus, Homework, Lesson Plan. Content swaps below based on selected tool. Back button returns to scope selector.
- **Components:** `TeacherScopeSelector`, segmented switch (custom), `VBackHeader`, `VStateHost`, `VButton`, `VCard`, `VBadge`
- **Interactions:** Select class → enables tools. Tap tool segment → switches tool screen. Change class → back to scope selector. Each tool has its own sub-screen.
- **States:** Default (scope selector), Tool active, Loading, Error.
- **Data:** Assigned classes, selected class, selected tool.

### Teacher → Update → Attendance Tool

- **Type:** Sub-screen (within Update tab)
- **Entry:** Tap "Attendance" segment. Deep link from Home → check-in popup.
- **Layout:** Class header (class name, student count, date) → student list with present/absent/late toggle per student. Quick actions: "Mark all present", "Mark all absent". Summary bar (present/absent/late counts). "Save" button.
- **Components:** `VCard`, `VButton`, `VBadge`, `VStateHost`, `VSnackbar`, `VDatePicker`
- **Interactions:** Tap toggle per student. "Mark all present" → sets all. "Save" → saves + success snackbar "Attendance marked ✓". Date selector → view/edit past dates.
- **States:** Default, Loading (student list), Saving, Success, Error, Offline (queued save).
- **Data:** Student names, roll numbers, attendance status, date.

### Teacher → Update → Marks Tool

- **Type:** Sub-screen (within Update tab)
- **Entry:** Tap "Marks" segment.
- **Layout:** Assessment selector (existing assessments for class) or "Create new assessment" → student list with mark input per student. Grade auto-calculation. "Save" button.
- **Components:** `VCard`, `VInput`, `VButton`, `VBadge`, `VStateHost`, `VSnackbar`
- **Interactions:** Select assessment → loads marks. Enter marks per student. "Save" → saves + snackbar. "Create new" → assessment creation dialog (name, max marks, date).
- **States:** Default, Loading, Saving, Success, Error, No assessments ("Create an assessment first").
- **Data:** Assessments, student marks, grades.

### Teacher → Update → Syllabus Tool

- **Type:** Sub-screen (within Update tab)
- **Entry:** Tap "Syllabus" segment.
- **Layout:** Syllabus unit list (unit name, completion status, sub-topics). Tap unit → sub-topic list with completion checkboxes. "Mark complete" per sub-topic. Progress bar at top (overall coverage %).
- **Components:** `VCard`, `VProgressBar`, `VButton`, `VBadge`, `VStateHost`, `VSnackbar`
- **Interactions:** Tap unit → expand sub-topics. Toggle sub-topic completion. Progress updates live. 6 dialog/sheet composables for unit/topic management.
- **States:** Default, Loading, Empty ("No syllabus defined for this class"), Saving, Error.
- **Data:** Units, sub-topics, completion status, coverage %.

### Teacher → Update → Homework Tool

- **Type:** Sub-screen (within Update tab)
- **Entry:** Tap "Homework" segment.
- **Layout:** Homework list (assigned homework with title, due date, submission count). "Create Homework" button → creation dialog (title, description, due date, subject). Tap homework → submission list (student name, submitted/not, grade). Grade submissions inline.
- **Components:** `VCard`, `VInput`, `VButton`, `VBadge`, `VDatePicker`, `VStateHost`, `VSnackbar`
- **Interactions:** "Create" → dialog → save. Tap homework → submissions. Grade inline. 1 dialog composable for homework creation.
- **States:** Default, Loading, Empty ("No homework assigned yet"), Saving, Error.
- **Data:** Homework list, submissions, grades.

### Teacher → Update → Lesson Plan Tool

- **Type:** Sub-screen (within Update tab)
- **Entry:** Tap "Lesson" segment.
- **Layout:** Lesson plan list (date, topic, objectives, resources). "Create Lesson Plan" button → creation dialog (date, topic, objectives, resources, duration). Tap plan → edit. 2 dialog composables for create/edit.
- **Components:** `VCard`, `VInput`, `VButton`, `VDatePicker`, `VStateHost`, `VSnackbar`
- **Interactions:** "Create" → dialog → save. Tap plan → edit dialog. Delete with confirmation.
- **States:** Default, Loading, Empty ("No lesson plans yet"), Saving, Error.
- **Data:** Lesson plans, topics, objectives.

---

## B-3 — Classes Tab

### Teacher → Classes

- **Type:** Tab (full screen, scrollable)
- **Entry:** Tap "Classes" in bottom nav.
- **Primary user:** Teacher. Wants to see assigned classes and drill into student details.
- **User goal:** View assigned classes, class rosters, student profiles, homework status.
- **Layout:** Scrollable list of class cards. Each: class name, section, student count, subject, latest homework summary, recent marks summary. Tap class → animated drill-down to class detail (student roster). Tap student → TeacherStudentProfileScreenV2.
- **Components:** `VCard`, `VAvatar`, `VBadge`, `VProgressBar`, `VStateHost`, `SkeletonList`
- **Interactions:** Tap class card → expand to student list (animated). Tap student → student profile sub-screen. Back → class list. Pull-to-refresh.
- **States:** Default, Loading, Empty ("No classes assigned"), Error.
- **Data:** Class names, student counts, homework/marks summaries, student rosters.

### Teacher → Classes → Student Profile Sub-screen (FOO)

- **Type:** Full screen drill-down (animated transition, not overlay)
- **Entry:** Tap student in class roster.
- **Layout:** Student header (avatar, name, roll, class) → academic summary (attendance %, avg marks, syllabus coverage) → recent marks list → attendance history → homework submission status.
- **States:** Default, Loading, Error.
- **Data:** Student profile, academic records.

---

## B-4 — Timetable Tab

### Teacher → Timetable

- **Type:** Tab (full screen)
- **Entry:** Tap "Timetable" in bottom nav.
- **Primary user:** Teacher. Needs weekly schedule at a glance + ability to request changes.
- **User goal:** View weekly timetable and submit change requests.
- **Layout:** `VTopTabs`: This Week · Change Requests. Content swaps below. 140dp bottom padding.
- **Components:** `VTopTabs`, `VCard`, `VBadge`, `VButton`, `VStateHost`, `SkeletonCalendar`
- **Interactions:** Tap sub-tab → switch. Tap period → edit dialog. "Request new period" → creation dialog.
- **States:** Default, Loading, Empty, Error.

### Teacher → Timetable → This Week Sub-tab

- **Type:** Sub-tab
- **Layout:** Day selector (Mon–Sun, current day highlighted) → period cards for selected day. Each: period number, time, class, subject, room. Edit/delete actions per period (opens change request dialog, not direct edit — teacher can't modify timetable directly).
- **Interactions:** Tap day → shows periods. Tap period → change request dialog (request modification with reason). "Request new period" → creation dialog. Empty state if no periods.
- **States:** Default, Loading, Empty ("No periods scheduled for this day"), Error.
- **Data:** Day, periods, class, subject, time, room.

### Teacher → Timetable → Change Requests Sub-tab

- **Type:** Sub-tab
- **Layout:** List of submitted change requests. Each: request type (New/Modify/Delete), target period, reason, status badge (Pending/Approved/Rejected), date submitted.
- **Interactions:** Tap request → detail view. No edit (can only cancel pending requests).
- **States:** Default, Loading, Empty ("No change requests submitted"), Error.
- **Data:** Request type, period, reason, status, date.

### Teacher → Timetable → Change Request Dialog (FOO)

- **Type:** Modal (dialog)
- **Entry:** Tap period → edit. Tap "Request new period".
- **Layout:** `Dialog` with form: request type (New/Modify/Delete), day, period number, class, subject, reason text field. "Submit" (Primary) + "Cancel" (Ghost).
- **States:** Default, Validation error, Submitting, Success, Error.
- **Data:** Form fields, period info.

---

## B-5 — Profile Tab

### Teacher → Profile

- **Type:** Tab (full screen, scrollable)
- **Entry:** Tap "Profile" in bottom nav.
- **Primary user:** Teacher. Needs personal info, leave management, settings.
- **User goal:** View profile, apply for leave, change password, switch theme, logout.
- **Layout:** Scrollable column. Identity card (avatar, name, employee ID, department, subjects) → leave application form (from date, to date, reason, type — Casual/Sick/Earned, "Apply" button) → leave status list (recent applications with status badges) → change password form (current, new, confirm) → theme switch (Light/Dark/System) → logout button → `VConfirmDialog`. 140dp bottom padding.
- **Components:** `VCard`, `VAvatar`, `VBadge`, `VInput`, `VButton`, `VDatePicker`, `VConfirmDialog`, `VThemePicker`, `VStateHost`
- **Interactions:** "Apply" leave → validates → submits → snackbar. Change password → validates → submits → snackbar. Theme switch → immediate. Logout → confirm dialog.
- **States:** Default, Loading (profile), Saving (leave/password), Success, Error.
- **Data:** Teacher profile, leave applications, leave balance.

### Teacher → Profile → Leave Application Form (FOO)

- **Type:** Inline form card
- **Layout:** `VCard` with fields: From date (VDatePicker), To date (VDatePicker), Leave type (dropdown: Casual/Sick/Earned), Reason (VInput multiline). "Apply" button (Primary).
- **Interactions:** Select dates → calculates days. "Apply" → validates → submits → snackbar.
- **States:** Default, Validation error, Submitting, Success, Error.
- **Data:** Date range, leave type, reason.

### Teacher → Profile → Leave Status List (FOO)

- **Type:** Scrollable list section
- **Layout:** List of leave applications. Each: date range, type, reason preview, status badge (Pending/Approved/Rejected), days count.
- **States:** Default, Empty ("No leave applications yet").
- **Data:** Applications, status, dates.

### Teacher → Profile → Change Password Form (FOO)

- **Type:** Inline form card
- **Layout:** `VCard` with fields: Current password (VInput, obscured), New password (VInput, obscured), Confirm password (VInput, obscured). "Change" button (Primary). Validation: min length, match.
- **States:** Default, Validation error, Submitting, Success, Error.
- **Data:** Password fields.

### Teacher → Profile → Logout Confirmation (FOO)

- **Type:** Modal (dialog)
- **Layout:** `VConfirmDialog`: "Log out?", "You'll need to sign in again." "Log out" (Destructive) + "Cancel".
- **States:** Default only.

---

## B-6 — Teacher Overlays (12 full-screen overlays)

### B-6.01 — Notifications Overlay

- **Type:** Full screen overlay
- **Entry:** Notification bell in header.
- **Layout:** Same as Admin A-6.01 but teacher-scoped notifications.
- **States:** Default, Loading, Empty, Error, Offline.
- **Data:** Teacher notifications, read status, deep links.
- **Consolidation note:** Shared `NotificationsScreenV2` composable. See FILE 2 #2.

### B-6.02 — Health Alerts Overlay

- **Type:** Full screen overlay
- **Entry:** Home tab → Health Alerts card. Notification deep link.
- **Layout:** `VBackHeader` ("Health Alerts") → list of student health alerts. Each: student avatar, name, class, alert type (allergy/condition/medication), severity badge, instructions. "Acknowledge" button.
- **States:** Default, Loading, Empty ("No health alerts"), Error.
- **Data:** Student health alerts, severity, instructions.

### B-6.03 — Transport Attendance Overlay

- **Type:** Full screen overlay
- **Entry:** Notification deep link. Home (future).
- **Layout:** `VBackHeader` ("Transport Attendance") → route selector → student list (assigned to route) with boarding status toggle (Boarded/Not Boarded). Stop-wise grouping. "Save" button.
- **States:** Default, Loading, Empty ("No students on this route"), Saving, Error.
- **Data:** Route, stops, students, boarding status.
- **Accessibility:** Route validation — only teachers assigned to a route can mark attendance. See FILE 2 #6.

### B-6.04 — PEWS Overlay (Teacher)

- **Type:** Full screen overlay
- **Entry:** Home (future). Notification deep link.
- **Layout:** `VBackHeader` ("Early Warning") → at-risk students in teacher's classes. Each: student name, class, risk level badge, primary factor. Tap → student detail (limited to teacher's subject scope).
- **States:** Default, Loading, Empty, Error, Feature-disabled (KillSwitchGuard).
- **Data:** At-risk students, risk levels, factors.
- **Consolidation note:** Teacher-specific PEWS view — different data scope from admin. See FILE 2 #7.

### B-6.05 — Report Review Queue Overlay

- **Type:** Full screen overlay
- **Entry:** Home tab → "Review Reports" card. Notification deep link.
- **Layout:** `VBackHeader` ("Report Review") → queue of report card drafts pending teacher review. Each: student name, class, draft status, submitted date. "Review" → opens ReportDraftEditor.
- **States:** Default, Loading, Empty ("No reports to review"), Error.
- **Data:** Draft list, student info, status.

### B-6.06 — Report Draft Editor Overlay

- **Type:** Full screen overlay
- **Entry:** Report Review Queue → "Review".
- **Layout:** `VBackHeader` ("Edit Report") → student header → editable report card sections (academic performance, behavior, comments). "Approve"/"Request Changes" buttons. Auto-save indicator.
- **States:** Default, Loading, Saving, Approved, Error.
- **Data:** Report draft, student info, editable sections.

### B-6.07 — Heatmap Overlay

- **Type:** Full screen overlay
- **Entry:** Home tab → "Learning Heatmap" card. Deep link `/teacher/tutor`.
- **Layout:** `VBackHeader` ("Learning Heatmap") → student selector → subject-wise mastery heatmap grid (topics × mastery level: red/yellow/green). Tap cell → topic detail.
- **States:** Default, Loading, Empty, Error.
- **Data:** Topics, mastery levels, student.

### B-6.08 — Digital ID Card Overlay

- **Type:** Full screen overlay
- **Entry:** Profile tab (future). Home (future).
- **Layout:** `VBackHeader` ("Digital ID") → teacher ID card display (photo, name, employee ID, department, school name, valid dates). QR code for verification. "Download" button.
- **States:** Default, Loading, Error.
- **Data:** Teacher ID info, QR code.
- **Consolidation note:** Shared `DigitalIdCardScreen` — same composable for parent and teacher. See FILE 2 #8.

### B-6.09 — Scheduled Messages Overlay

- **Type:** Full screen overlay
- **Entry:** Home (future). Messages (future).
- **Layout:** Same as Admin A-6.35 but teacher-scoped.
- **States:** Default, Loading, Empty, Error.
- **Data:** Teacher's scheduled messages.
- **Consolidation note:** Shared `ScheduledMessagesScreenV2`. See FILE 2 #9.

### B-6.10 — Event Registration Overlay (Teacher)

- **Type:** Full screen overlay
- **Entry:** Calendar → PTM event. Notification deep link.
- **Layout:** `VBackHeader` ("PTM Event") → event details → slot booking list (parent name, student name, booked time). Manage slots. "Start Session" button per slot.
- **States:** Default, Loading, Empty, Error.
- **Data:** PTM event, slots, bookings.

### B-6.11 — Messages Overlay (Teacher)

- **Type:** Full screen overlay
- **Entry:** Notification deep link. Home (future).
- **Layout:** `VBackHeader` ("Messages") → inbox of parent-teacher conversation threads. Each: parent avatar, name, last message, timestamp, unread badge. Tap → conversation view. Compose bar.
- **States:** Default, Loading, Empty, Error, Sending, Offline.
- **Data:** Thread list, messages, parent info.
- **Consolidation note:** Uses `TeacherMessagesScreenV2` — separate from admin/parent variants. See FILE 2 #4.

### B-6.12 — Calendar Overlay

- **Type:** Full screen overlay
- **Entry:** Calendar icon in header (if present). Notification deep link.
- **Layout:** Same as Admin A-6.02 — shared `AcademicCalendarScreenV2` (view-only for teacher).
- **States:** Default, Loading, Empty, Error.
- **Data:** Calendar events.
- **Consolidation note:** Shared legacy calendar. See FILE 2 #3.

---

*End of Part B — Teacher Portal*

---

# PART C — PARENT PORTAL

> **User:** Mother/father/guardian. Age 25–55+. Low–medium digital literacy. WhatsApp-literate but not app-literate. Often shared family phone, patchy connectivity, may prefer Hindi/regional language.
> **Design calibration:** WhatsApp-mental-model navigation. Large text. Icon + label always paired (never icon-only). Offline-tolerant states. Minimal jargon ("Fee Due" not "Outstanding Ledger Balance"). Biometric/simple auth over password. Generous tap targets. Simple, forgiving flows.

## C-0 — Parent Portal Shell

- **Type:** Full screen (shell)
- **Entry point(s):** Post-auth `AuthedFlow` → `ParentPortalV2`. Deep link from notification.
- **Primary user:** Parent/guardian. Low-medium literacy. WhatsApp mental model. Needs icon+label, large text, simple navigation.
- **User goal:** 5-tab parent command center for child monitoring and school communication.
- **Layout:** `VScreenScaffold` with header (child name + school name + notification bell) and `ParentDock` (bespoke floating glass dock): Home, Academics, Fees, Conversations, Profile. Content swaps between tabs. Overlays render as full-screen `AnimatedContent` above tabs. If parent has no linked children → `ParentUnlinkedScreenV2` gate.
- **Components:** `VScreenScaffold`, `ParentDock`, `VBackHeader`, `AnimatedContent`, `BackHandler`, `ParentUnlinkedScreenV2`
- **Interactions:** Tap dock item → switch tab with crossfade. Notification bell → Notifications overlay. Back → exits overlay to tabs, or goes Home if on non-home tab.
- **States:** Default, Overlay active, Loading, Unlinked (no children — gate screen).
- **Data:** Child name, school name, unread notification count.
- **Accessibility:** All dock items have icon + text label. 48dp min tap targets. Never icon-only.

### Parent → Unlinked Gate Screen

- **Type:** Full screen gate
- **Entry:** Parent portal opens when parent has 0 linked children.
- **Layout:** Friendly illustration + "Link your child" heading + explanation text + "Link Child" button (Primary) + "Discover Schools" button (Secondary).
- **Interactions:** "Link Child" → LinkChild overlay. "Discover Schools" → Discovery overlay.
- **States:** Default only.
- **Data:** None.
- **Accessibility:** Simple language. No jargon. Large text.

---

## C-1 — Home Tab

### Parent → Home

- **Type:** Tab (full screen, scrollable)
- **Entry:** Default tab. Tap "Home" in dock.
- **Primary user:** Parent. Wants to see child's status at a glance — attendance, fees, messages, today's schedule. WhatsApp-mental-model — expects a feed-like layout.
- **User goal:** At-a-glance child overview + quick access to fees, messages, health, transport, tutor.
- **Layout:** Scrollable column. Child switcher header (dropdown: child name + class + avatar — if multiple children) → hero card (child identity card with journey progress ring, level, XP, house badge) → school-day timeline (today's periods with status) → feature card grid (2-up): Fees (balance + "Pay" button), Academics (latest marks), Messages (unread count), Pulse (health score), Transport (bus status), Tutor (AI tutor status), Scholarships (available), ID Card, Library, Events. Each card tappable → overlay. 140dp bottom padding for dock clearance.
- **Components:** `ParentHeader` (child switcher dropdown), `VCard`, `VAvatar`, `VBadge`, `VProgressRing`, `VProgressBar`, `VActionCard`, `VSectionHeader`, staggered entrance, `ParentCoveredCard`
- **Interactions:** Tap child switcher → dropdown menu of children. Tap feature card → corresponding overlay. Pull-to-refresh. Tap hero card → Profile tab.
- **States:** Default, Loading (`SkeletonDashboard`), Empty (first-time — "Welcome! Link your child to get started"), Error, Offline (cached + banner).
- **Data:** Child name, class, avatar, journey progress, today's periods, fee balance, unread messages, pulse score, bus status, tutor status, scholarship availability.

### Parent → Home → Child Switcher Dropdown (FOO)

- **Type:** Dropdown menu
- **Entry:** Tap child name in header.
- **Layout:** `DropdownMenu` with list of linked children. Each: avatar, name, class. Current child highlighted with checkmark.
- **Interactions:** Tap child → switches all home data to selected child. Closes dropdown.
- **States:** Default, Single child (no dropdown — static header).
- **Data:** Children list.

### Parent → Home → Hero Card (FOO)

- **Type:** Card with embedded progress visualization
- **Layout:** Full-width card. Left: child avatar (large). Right: child name, class, house badge. Bottom: journey progress ring (level, XP bar). Gamified "player card" aesthetic.
- **Interactions:** Tap card → Profile tab.
- **States:** Default, Loading (skeleton).
- **Data:** Child name, class, avatar, level, XP, house.

### Parent → Home → School-Day Timeline (FOO)

- **Type:** Vertical timeline list
- **Layout:** Today's periods as timeline items. Each: time, subject, teacher name, status icon (upcoming/done). Current period highlighted.
- **Interactions:** Tap period → no action (display only for parent).
- **States:** Default, Empty ("No school today"), Loading.
- **Data:** Period times, subjects, teachers, status.

### Parent → Home → Feature Card Grid (FOO)

- **Type:** 2-up card grid
- **Layout:** Grid of tappable cards, each with icon + label + key metric:
  1. Fees — balance amount + "Pay" button → Fees tab
  2. Academics — latest mark → Academics tab
  3. Messages — unread count → Conversations tab
  4. Pulse — health score → Pulse overlay
  5. Transport — bus status ("On route"/"Arrived") → Transport overlay
  6. Tutor — AI tutor status → TutorChat overlay
  7. Scholarships — count available → Scholarships overlay
  8. ID Card — "View" → DigitalIdCard overlay
  9. Library — borrowed books count → Library overlay
  10. Events — upcoming count → EventRegistration overlay
- **Interactions:** Tap card → tab or overlay. "Pay" button on Fees card → fee payment flow.
- **States:** Default, per-card empty states.
- **Data:** Varies per card (see above).
- **Accessibility:** Every card has icon + text label. No icon-only cards. Amounts shown in plain language ("₹500 due" not "Outstanding: 500.00").

---

## C-2 — Academics Tab

### Parent → Academics

- **Type:** Tab (full screen, scrollable)
- **Entry:** Tap "Academics" in dock. Deep link from notification.
- **Primary user:** Parent. Wants to see child's academic progress in simple terms. "How is my child doing?" — not data tables.
- **User goal:** Monitor child's attendance, marks, syllabus, quizzes, homework, report card.
- **Layout:** Scrollable column. Top: action cards row — "Apply for Leave" (→ Leave overlay) + "Health Records" (→ Health overlay). Below: `VTopTabs`: Overview · Attendance · Marks · Syllabus · Quizzes · Homework · Report. Content swaps below. 140dp bottom padding.
- **Components:** `VTopTabs`, `VActionCard`, `VCard`, `VProgressRing`, `VProgressBar`, `VBadge`, `VCharts`, `VStateHost`, `SkeletonList`, `VPullRefresh`
- **Interactions:** Tap sub-tab → lazy-loads data. Tap action card → overlay. Pull-to-refresh. Deep link support to specific tab.
- **States:** Default, Loading (skeleton per tab), Empty per tab, Error per tab, Offline.
- **Data:** Varies by sub-tab.

### Parent → Academics → Overview Sub-tab

- **Type:** Sub-tab
- **Layout:** Journey progress card (level ring, XP bar, streak count) → attendance summary (VProgressRing, %) → marks summary (avg, trend) → syllabus coverage (VProgressBar, %) → recent achievements list.
- **States:** Default, Loading, Empty ("No academic data yet"), Error.
- **Data:** Level, XP, attendance %, marks avg, coverage %, achievements.

### Parent → Academics → Attendance Sub-tab

- **Type:** Sub-tab
- **Layout:** `ParentAttendanceCalendar` (monthly calendar with color-coded days: green=present, red=absent, yellow=late) → `ParentAttendanceCard` (summary: present days, absent days, late days, attendance %) → trend chart (last 6 months).
- **Components:** `ParentAttendanceCalendar`, `ParentAttendanceCard`, `VProgressRing`, `VCharts`, `VStateHost`
- **Interactions:** Tap calendar day → shows detail for that day. Swipe to change month.
- **States:** Default, Loading, Empty ("No attendance records yet"), Error.
- **Data:** Daily attendance status, monthly summary, trend.

### Parent → Academics → Marks Sub-tab

- **Type:** Sub-tab
- **Layout:** Assessment list. Each: assessment name, subject, date, marks scored, max marks, grade badge. Subject filter chips. Trend chart for selected subject.
- **Interactions:** Tap assessment → detail. Tap filter chip → filters by subject.
- **States:** Default, Loading, Empty ("No marks recorded yet"), Error.
- **Data:** Assessment name, subject, marks, grade, date.

### Parent → Academics → Syllabus Sub-tab

- **Type:** Sub-tab
- **Layout:** Subject-wise syllabus coverage cards. Each: subject name, coverage % (VProgressBar), unit breakdown (completed/total units). Expandable to show unit list with status.
- **Interactions:** Tap card → expand unit list. Tap unit → sub-topic detail.
- **States:** Default, Loading, Empty ("No syllabus data yet"), Error.
- **Data:** Subjects, coverage %, units, sub-topics, status.

### Parent → Academics → Quizzes Sub-tab

- **Type:** Sub-tab
- **Layout:** Quiz list. Each: quiz title, subject, date, score, status badge (Completed/Pending/Upcoming). Tap completed quiz → quiz result detail → leaderboard.
- **Interactions:** Tap quiz → result detail. Tap leaderboard → class ranking view.
- **States:** Default, Loading, Empty ("No quizzes yet"), Error.
- **Data:** Quiz title, subject, score, status, leaderboard.

### Parent → Academics → Homework Sub-tab

- **Type:** Sub-tab
- **Layout:** `DailySummaryTab` — date selector + homework list for selected date. Each: subject, title, description, due date, submission status (Submitted/Pending/Late). Tap → homework detail.
- **Interactions:** Tap date → loads homework. Tap homework → detail view.
- **States:** Default, Loading, Empty ("No homework for this date"), Error.
- **Data:** Homework title, subject, due date, status, description.

### Parent → Academics → Report Sub-tab

- **Type:** Sub-tab
- **Layout:** `ParentReportScreen` — report card list (term-wise). Each: term name, issue date, overall grade, download button. Tap → `AiReportCardPreview` (AI-enhanced report card with insights, strengths, improvement areas).
- **Components:** `ParentReportScreen`, `AiReportCardPreview`, `VCard`, `VBadge`, `VButton`, `VStateHost`
- **Interactions:** Tap report → AI report card preview. "Download" → PDF download.
- **States:** Default, Loading, Empty ("No report cards published yet"), Error.
- **Data:** Term, grades, AI insights, strengths, improvement areas.

---

## C-3 — Fees Tab

### Parent → Fees

- **Type:** Tab (full screen, scrollable)
- **Entry:** Tap "Fees" in dock. Deep link from notification.
- **Primary user:** Parent. Needs to see what's due and pay easily. Plain language amounts. "₹500 due" not "Outstanding Ledger Balance."
- **User goal:** View fee balance, payment history, fee announcements, and make payments.
- **Layout:** Scrollable column. Navy-gradient balance hero card (outstanding amount in large text, "Pay Now" button if payment gateway available, due date) → fee announcements feed (school-wide fee notices) → payment history list (date, amount, receipt number, download). 140dp bottom padding.
- **Components:** `VCard` (gradient variant), `VButton`, `VBadge`, `VSectionHeader`, `VStateHost`, `SkeletonFee`, `VSnackbar`
- **Interactions:** "Pay Now" → payment gateway flow (external). Tap announcement → detail. Tap payment → receipt download. Pull-to-refresh.
- **States:** Default, Loading (`SkeletonFee`), Empty ("No fee records yet"), Error, Offline, Payment processing (loading), Payment success (snackbar), Payment error (snackbar).
- **Data:** Outstanding amount, due date, fee structure, announcements, payment history, receipts.
- **Accessibility:** Amounts in plain language. Large text for balance. "Pay Now" button is prominent. No financial jargon.

---

## C-4 — Conversations Tab

### Parent → Conversations

- **Type:** Tab (full screen)
- **Entry:** Tap "Conversations" in dock. Deep link from notification.
- **Primary user:** Parent. WhatsApp-mental-model — expects chat-like interface. Unread badge on dock item.
- **User goal:** Message teachers/school and view school announcements.
- **Layout:** Segmented control (`ConversationsSegment` enum): Messages · Announcements. Content swaps below. 140dp bottom padding.
- **Components:** Segmented control (custom), `VCard`, `VAvatar`, `VBadge`, `VInput`, `VButton`, `VStateHost`, `SkeletonList`, `VPullRefresh`
- **Interactions:** Tap segment → switch. Tap thread → conversation view. Type + send. Back → inbox. Pull-to-refresh.
- **States:** Default, Loading, Empty, Error, Sending, Offline (queued indicator).

### Parent → Conversations → Messages Segment

- **Type:** Segment (sub-tab equivalent)
- **Layout:** Inbox of conversation threads (WhatsApp-style). Each: avatar (teacher/admin), name, last message preview, timestamp, unread badge. Tap → conversation view (message bubbles + compose bar at bottom). "Compose new" → thread picker (select teacher/admin to message).
- **Interactions:** Tap thread → conversation. Type + send. Long-press message → context menu (future). Back → inbox. `BackHandler` peels back from compose/thread to inbox.
- **States:** Default, Loading, Empty ("No conversations yet"), Error, Sending (compose loading), Offline (queued with "Will send when online" indicator).
- **Data:** Thread list, messages, sender info, unread counts.

### Parent → Conversations → Announcements Segment

- **Type:** Segment (sub-tab equivalent)
- **Layout:** `ParentActivityScreenV2` — announcement feed (vertical list of announcement cards). Each: title, category badge, date, description preview, school name. Tap → announcement detail (full text).
- **Interactions:** Tap card → detail view. Pull-to-refresh. Filter by category (future).
- **States:** Default, Loading (`SkeletonAnnouncements`), Empty ("No announcements yet"), Error, Offline (cached).
- **Data:** Announcement title, category, date, description, school name.

---

## C-5 — Profile Tab

### Parent → Profile

- **Type:** Tab (full screen, scrollable)
- **Entry:** Tap "Profile" in dock.
- **Primary user:** Parent. Gamified "player card" aesthetic — collectible feel. Swipe-down reveals account options.
- **User goal:** View child's gamified profile, access account settings, link another child, discover schools, logout.
- **Layout:** `ParentProfileCardScreenV2` — collectible player card (house identity, stats grid: attendance, marks, XP, badges/achievements). Swipe-down gesture reveals account options panel: "Account Settings" (→ Profile overlay), "Link Another Child" (→ LinkChild overlay), "Discover Schools" (→ Discovery overlay), "Logout" (→ VConfirmDialog). 140dp bottom padding.
- **Components:** `ParentProfileCardScreenV2`, `ParentCoveredCard`, `ParentCoveredDetailOverlay`, `VAvatar`, `VBadge`, `VProgressRing`, `VButton`, `VConfirmDialog`
- **Interactions:** Swipe down on card → reveals account options. Tap option → overlay or action. Tap stat → detail. Tap badge → achievement detail.
- **States:** Default, Loading, Error.
- **Data:** Child stats, house, level, XP, badges, achievements.

### Parent → Profile → Account Options Reveal (FOO)

- **Type:** Swipe-down panel (reveal on gesture)
- **Layout:** Panel slides down from bottom of player card. Contains: "Account Settings" row, "Link Another Child" row, "Discover Schools" row, "Logout" row. Each with icon + label.
- **Interactions:** Tap row → overlay or action. Swipe up → hides panel.
- **States:** Hidden, Revealed.
- **Data:** None (static menu).

### Parent → Profile → Stats Grid (FOO)

- **Type:** Grid of stat cards
- **Layout:** 2-up grid of stat cards: Attendance %, Average Marks, XP Points, Quizzes Completed. Each with large number + label + trend arrow.
- **Interactions:** Tap stat → detail view (e.g., attendance → Academics Attendance tab).
- **States:** Default, Loading.
- **Data:** Stat values, trends.

### Parent → Profile → Badges/Achievements (FOO)

- **Type:** Horizontal scroll of badge cards
- **Layout:** Horizontal scroll of circular badge icons with labels. Locked badges shown dimmed. Tap unlocked badge → achievement detail.
- **Interactions:** Tap badge → detail. Swipe to scroll.
- **States:** Default, Empty ("No badges earned yet").
- **Data:** Badge name, icon, earned/locked status, description.

---

## C-6 — Parent Overlays (16 full-screen overlays)

### C-6.01 — Notifications Overlay

- **Type:** Full screen overlay
- **Entry:** Notification bell in header. Deep link from push notification.
- **Layout:** Same as Admin A-6.01 but parent-scoped.
- **States:** Default, Loading, Empty, Error, Offline.
- **Data:** Parent notifications, read status, deep links.
- **Consolidation note:** Shared `NotificationsScreenV2`. See FILE 2 #2.

### C-6.02 — Calendar Overlay

- **Type:** Full screen overlay
- **Entry:** Home feature card (future). Deep link.
- **Layout:** Shared `AcademicCalendarScreenV2` (view-only for parent).
- **States:** Default, Loading, Empty, Error.
- **Data:** Calendar events.
- **Consolidation note:** Shared legacy calendar. See FILE 2 #3.

### C-6.03 — Scholarships Overlay

- **Type:** Full screen overlay
- **Entry:** Home → Scholarships feature card.
- **Layout:** `ScholarshipWorkflowScreenV2` — available scholarship schemes list. Each: name, eligibility, amount, deadline, status (Eligible/Not Eligible/Applied). "Apply" button → application form (multi-step). Applied status tracking.
- **States:** Default, Loading, Empty ("No scholarships available"), Error, Applying (loading), Success, Error.
- **Data:** Schemes, eligibility, amounts, deadlines, application status.
- **Consolidation note:** Replaces orphaned `ScholarshipsScreenV2.kt`. See FILE 2 #10.

### C-6.04 — Profile / Account Settings Overlay

- **Type:** Full screen overlay
- **Entry:** Profile tab → "Account Settings" in account options reveal.
- **Layout:** `ParentProfileScreenV2` — parent profile form (name, phone, email, address, alternate contact) + child list with unlink option + notification preferences + language preference + theme switch + change password + logout.
- **States:** Default, Loading, Saving, Success, Error.
- **Data:** Parent profile, children, preferences.
- **Consolidation note:** Renamed from "Profile" to "Account Settings" to disambiguate from Profile tab (player card). See FILE 2 #11.

### C-6.05 — Leave Overlay

- **Type:** Full screen overlay
- **Entry:** Academics → "Apply for Leave" action card. Home (future).
- **Layout:** `ParentLeaveScreenV2` — leave application form (child selector if multiple, from date, to date, reason, type — Sick/Casual/Other, supporting document upload optional). Leave history list below.
- **States:** Default, Loading, Submitting, Success, Error.
- **Data:** Children, leave history, form fields.

### C-6.06 — Messages Overlay

- **Type:** Full screen overlay
- **Entry:** Deep link from notification. Home (future).
- **Layout:** `ParentMessagesScreenV2` — same WhatsApp-style inbox as Conversations Messages segment but as full overlay.
- **States:** Default, Loading, Empty, Error, Sending, Offline.
- **Data:** Thread list, messages.
- **Consolidation note:** Separate from Conversations tab — overlay is for deep-link entry only. See FILE 2 #12.

### C-6.07 — Link Child Overlay

- **Type:** Full screen overlay
- **Entry:** Unlinked gate screen. Profile → "Link Another Child". Unauth flow.
- **Layout:** `ParentLinkChildScreenV2` — multi-step wizard: Step 1 (school selection — search/discover) → Step 2 (child details — name, class, roll number) → Step 3 (verification — OTP sent to school admin or parent phone) → Step 4 (success — child linked). Progress indicator at top.
- **States:** Default, Step N, Validation error, Submitting, Success, Error.
- **Data:** School list, child details, OTP.
- **Consolidation note:** Shared `ParentLinkChildScreenV2` — used in both unauth flow and parent overlay. See FILE 2 #13.

### C-6.08 — Discovery Overlay

- **Type:** Full screen overlay
- **Entry:** Unlinked gate → "Discover Schools". Profile → "Discover Schools". Unauth flow.
- **Layout:** `DiscoveryScreenV2` — school discovery: search bar + location filter + school cards (name, board, address, rating, cover image). Tap school → school profile detail with "Link Child" button.
- **States:** Default, Loading, Empty ("No schools found"), Error, Search empty.
- **Data:** School list, profiles, ratings.
- **Consolidation note:** Shared `DiscoveryScreenV2` — used in both unauth flow and parent overlay. See FILE 2 #13.

### C-6.09 — Health Overlay

- **Type:** Full screen overlay
- **Entry:** Academics → "Health Records" action card. Home → Pulse feature card.
- **Layout:** `ParentHealthScreenV2` — child health profile (blood group, allergies, conditions, immunizations, emergency contact) + `ParentPulseScreen` (Pulse score gauge, risk factors, recommendations, trend chart).
- **States:** Default, Loading, Empty ("No health records"), Error.
- **Data:** Health profile, pulse score, factors, recommendations.

### C-6.10 — Pulse Overlay

- **Type:** Full screen overlay
- **Entry:** Home → Pulse feature card. Health overlay (integrated).
- **Layout:** `ParentPulseScreen` — pulse score gauge (VProgressRing) → risk factor breakdown (attendance, academics, engagement) → trend chart → AI recommendations → "View Details" per factor.
- **States:** Default, Loading, Empty ("No pulse data yet"), Error, Feature-disabled (KillSwitchGuard).
- **Data:** Pulse score, factors, trend, recommendations.

### C-6.11 — Transport Overlay

- **Type:** Full screen overlay
- **Entry:** Home → Transport feature card. Deep link from notification.
- **Layout:** `BusTrackingScreenV2` — live bus tracking map (route, current location, ETA) + child boarding status (Boarded/Not Boarded with timestamp) + route details (stops, timings, driver info).
- **States:** Default, Loading, Empty ("Child not assigned to transport"), Error, Offline (last known location), Live (real-time updates).
- **Data:** Bus location, route, ETA, boarding status, driver info.

### C-6.12 — Tutor Chat Overlay

- **Type:** Full screen overlay
- **Entry:** Home → Tutor feature card. Deep link.
- **Layout:** `TutorChatScreen` — chat interface with AI tutor. Message bubbles (user right, tutor left). Quick suggestion chips. Subject selector. "View Progress" button → TutorProgress overlay. Typing indicator.
- **States:** Default, Loading, Sending, Error, Offline (queued messages).
- **Data:** Chat messages, subject, tutor responses.
- **Accessibility:** Simple chat UI — WhatsApp mental model. Large text. Clear send button.

### C-6.13 — Tutor Progress Overlay

- **Type:** Full screen overlay
- **Entry:** Tutor Chat → "View Progress". Deep link.
- **Layout:** `ParentProgressScreen` — tutor progress dashboard: subjects covered, mastery levels (heatmap), sessions count, time spent, improvement trend. Per-subject breakdown cards.
- **States:** Default, Loading, Empty ("No tutor sessions yet"), Error.
- **Data:** Subjects, mastery, sessions, time, trend.

### C-6.14 — Digital ID Card Overlay

- **Type:** Full screen overlay
- **Entry:** Home → ID Card feature card. Profile (future).
- **Layout:** `DigitalIdCardScreen` — student ID card display (photo, name, class, roll, school name, valid dates). QR code for verification. "Download" button.
- **States:** Default, Loading, Error.
- **Data:** Student ID info, QR code.
- **Consolidation note:** Shared `DigitalIdCardScreen` — same composable for parent and teacher. See FILE 2 #8.

### C-6.15 — Library Overlay (Parent)

- **Type:** Full screen overlay
- **Entry:** Home → Library feature card.
- **Layout:** `ParentLibraryScreenV2` — browse catalog (search + category filter) + borrowed books list (title, due date, return button) + reservation list + fine status. 9 sub-tabs: Dashboard · Browse · Borrowed · Reservations · History · Fines · Categories · Search · Settings.
- **States:** Default, Loading, Empty, Error.
- **Data:** Catalog, borrowed books, reservations, fines.
- **Consolidation note:** Separate from school library (14 tabs) — different permission scope. See FILE 2 #5.

### C-6.16 — Event Registration Overlay (Parent)

- **Type:** Full screen overlay
- **Entry:** Home → Events feature card. Deep link from notification.
- **Layout:** `ParentEventRegistrationScreenV2` — event list (upcoming events with registration status) → tap event → event detail + "Register" button → registration confirmation. PTM slot booking.
- **States:** Default, Loading, Empty ("No upcoming events"), Error, Registered (success state).
- **Data:** Events, registration status, PTM slots.

---

*End of Part C — Parent Portal*

---

# PART D — SHARED / COMMON SCREENS

> **User:** All of the above (Admin, Teacher, Parent). Design to the lowest-literacy common denominator.
> **Design calibration:** Login/OTP/onboarding must be the simplest, most forgiving flow in the entire app — it's every user's first impression. Large text, clear instructions, error recovery, no jargon.

## D-1 — Splash Screen

### Shared → Splash

- **Type:** Full screen (transient)
- **Entry point(s):** App launch. `SplashScreenV2` in `App.kt`.
- **Primary user:** All users. First impression.
- **User goal:** Show branded splash while app initializes (session check, theme load).
- **Layout:** Full-screen brand color background. Center: `VBrandLogo` (animated logo with subtle scale-in). Below: app name "Enroll+" in brand font. Bottom: subtle loading spinner. Duration: 1.5–2s, then transitions based on session validity.
- **Components:** `VBrandLogo`, `VProgressRing` (indeterminate), `AnimatedContent`
- **Interactions:** None (auto-transitions).
- **States:** Default (showing splash), Transitioning (fading to auth or portal).
- **Data:** None.

---

## D-2 — Unauthenticated Flow (Pre-Login Funnel)

### Shared → UnauthFlow

- **Type:** Full screen (state machine funnel)
- **Entry point(s):** No valid session after splash. `NavGraphV2` → `UnauthFlow`.
- **Primary user:** All first-time users. Lowest literacy common denominator.
- **User goal:** Guide user to the correct auth path (parent or admin) and into the app.
- **Layout:** `AnimatedContent` with 7 states: Landing → ParentAuth → AdminAuth → Discovery → ParentLinkChild → SchoolOnboarding → Legal. `BackHandler` navigates back in funnel.
- **Components:** `AnimatedContent`, `BackHandler`, `VButton`, `VInput`, `VCard`
- **Interactions:** Back press → previous funnel step (except from Landing → exits app).
- **States:** Landing, ParentAuth, AdminAuth, Discovery, ParentLinkChild, SchoolOnboarding, Legal.
- **Data:** None (state-driven).

### Shared → Landing Screen

- **Type:** Full screen
- **Entry:** Default UnauthFlow state.
- **Primary user:** All users. First impression — must be inviting and simple.
- **User goal:** Choose entry path: Parent or School Admin.
- **Layout:** `CommonLandingScreenV3` — full-screen gradient background. Center: brand logo + app name. Below: two large buttons: "I'm a Parent" (Primary, large) and "School Admin / Teacher" (Secondary). Below: "Discover Schools" link (ghost). Bottom: "Terms & Privacy" link.
- **Components:** `VBrandLogo`, `VButton`, `VCard`, `VIcons`
- **Interactions:** "I'm a Parent" → ParentAuth. "School Admin / Teacher" → AdminAuth. "Discover Schools" → Discovery. "Terms & Privacy" → Legal.
- **States:** Default only.
- **Data:** None.
- **Accessibility:** Large buttons, clear labels. No jargon. "I'm a Parent" not "Guardian Portal Access."
- **Consolidation note:** Replaces orphaned `CommonLandingScreenV2.kt`. See FILE 2 #14.

### Shared → Parent Auth Screen

- **Type:** Full screen
- **Entry:** Landing → "I'm a Parent".
- **Primary user:** Parent. Low literacy. May be first-time smartphone user. WhatsApp-mental-model.
- **User goal:** Sign in as parent — phone + OTP, simple and forgiving.
- **Layout:** `ParentAuthScreenV2` — `VBackHeader` ("Sign In") → phone number input (large, with country code +91 prefix) → "Send OTP" button (Primary, full width) → OTP input (6-digit, large boxes with auto-advance) → "Verify" button → "Resend OTP" link (countdown timer). Error states: invalid phone, invalid OTP, network error. Success → AuthedFlow.
- **Components:** `VBackHeader`, `VInput` (phone), `VInput` (OTP — custom large digit boxes), `VButton`, `VSnackbar`, `VBadge`
- **Interactions:** Enter phone → "Send OTP" → OTP screen → enter OTP → "Verify" → auth. "Resend OTP" after 30s countdown. Back → Landing.
- **States:** Default (phone entry), OTP sent (OTP entry), Verifying (loading), Error invalid phone, Error invalid OTP, Error network, Success (transitions), Resend countdown.
- **Data:** Phone number, OTP code.
- **Accessibility:** Large input fields. Clear error messages in plain language ("This phone number isn't registered" not "404: user not found"). 48dp+ tap targets. Auto-advance OTP boxes.

### Shared → Admin Auth Screen

- **Type:** Full screen
- **Entry:** Landing → "School Admin / Teacher".
- **Primary user:** Admin/teacher. Medium-high literacy. May use email or phone.
- **User goal:** Sign in as school admin or teacher.
- **Layout:** `AdminAuthScreenV2` — `VBackHeader` ("Admin Sign In") → tabbed input: Phone tab / Email tab → credentials input → "Sign In" button (Primary) → OTP or password step (depending on school config). Error states. Success → AuthedFlow.
- **Components:** `VBackHeader`, `VTopTabs` (Phone/Email), `VInput`, `VButton`, `VSnackbar`
- **Interactions:** Select tab → enter credentials → "Sign In" → verify → auth. Back → Landing.
- **States:** Default, Verifying, Error invalid credentials, Error network, Success.
- **Data:** Phone/email, password/OTP.

### Shared → Discovery Screen (Unauth)

- **Type:** Full screen
- **Entry:** Landing → "Discover Schools". Unlinked parent gate.
- **Layout:** Same as C-6.08 Discovery overlay — `DiscoveryScreenV2`. Search + school cards.
- **States:** Default, Loading, Empty, Error, Search empty.
- **Consolidation note:** Shared `DiscoveryScreenV2` — used in unauth flow, unlinked gate, and parent overlay. See FILE 2 #13.

### Shared → Parent Link Child Screen (Unauth)

- **Type:** Full screen
- **Entry:** UnauthFlow → ParentLinkChild state. Post-auth parent unlinked gate.
- **Layout:** Same as C-6.07 Link Child overlay — `ParentLinkChildScreenV2`. Multi-step wizard.
- **States:** Step N, Validation error, Submitting, Success, Error.
- **Consolidation note:** Shared across unauth and authed flows. See FILE 2 #13.

### Shared → School Onboarding Screen

- **Type:** Full screen (multi-step wizard)
- **Entry:** AuthedFlow → `AuthedRoute.SchoolOnboarding` (when `OnboardingGate.Onboarding`). UnauthFlow → SchoolOnboarding state.
- **Primary user:** School admin. First-time setup. Medium-high literacy.
- **User goal:** Complete school onboarding — profile setup, class creation, teacher invitation, branding.
- **Layout:** `SchoolOnboardingScreenV2` — multi-step wizard with progress indicator. Steps: School details (name, board, address, logo) → Academic year setup → Class & section creation → Teacher invitation → Branding (colors, subdomain) → Review & finish. Each step: form fields, "Next"/"Back" buttons. `OnboardingGateViewModel` determines current step.
- **Components:** `VBackHeader`, `VInput`, `VButton`, `VCard`, `VProgressBar` (step indicator), `VDatePicker`, `VThemePicker`, `VBrandLogo`, `VSnackbar`, `VAvatar` (logo upload)
- **Interactions:** "Next" → validates → advances. "Back" → previous step. "Finish" → completes onboarding → portal. Skip optional steps. Logo upload → file picker.
- **States:** Default (step N), Validation error, Saving, Success, Error, Skipped step.
- **Data:** School profile, academic year, classes, teacher invites, branding.
- **Accessibility:** Clear step indicator. "Skip for now" on optional steps. Progress saved automatically.

### Shared → Teacher First Login Screen

- **Type:** Full screen
- **Entry:** AuthedFlow → `AuthedRoute.TeacherFirstLogin` (when `profileCompleted == false`).
- **Primary user:** Teacher. First login. Needs to complete profile.
- **User goal:** Complete teacher profile on first login.
- **Layout:** `TeacherFirstLoginScreenV2` — welcome header → profile form: full name, subject(s), department, phone, profile photo upload, change password (from temp password). "Complete" button → portal.
- **Components:** `VBackHeader`, `VInput`, `VButton`, `VAvatar` (photo upload), `VSnackbar`
- **Interactions:** Fill form → "Complete" → validates → saves → portal. Photo upload → file picker.
- **States:** Default, Validation error, Saving, Success, Error.
- **Data:** Teacher profile fields.

### Shared → Legal Info Screen

- **Type:** Full screen
- **Entry:** Landing → "Terms & Privacy". Settings (future).
- **Layout:** `LegalInfoScreenV2` — `VBackHeader` ("Legal") → list of legal documents: Terms of Service, Privacy Policy, Cookie Policy. Tap → document viewer (full text, scrollable). Each document has title, last updated date, and body content.
- **Components:** `VBackHeader`, `VCard`, `VSectionHeader`, `VButton`
- **Interactions:** Tap document → full text view. Back → list.
- **States:** Default (list), Document view (full text).
- **Data:** Document titles, dates, body content.
- **Consolidation note:** Replaces orphaned `CommonLandingScreenV2.kt` which had a stale reference to LegalInfoScreenV2. See FILE 2 #14.

---

## D-3 — Shared Overlay Screens (Cross-Portal)

These screens are used by 2+ portals with role-specific data scoping. They are NOT duplicates — they share the same composable but receive different data based on the caller's role.

### Shared → Notifications Screen

- **Type:** Full screen overlay (shared composable)
- **Composable:** `NotificationsScreenV2`
- **Used by:** Admin (A-6.01), Teacher (B-6.01), Parent (C-6.01)
- **Layout:** `VBackHeader` ("Notifications") → filter tabs (All / Unread) → notification list. Each: icon, title, body, timestamp, read/unread indicator. Tap → mark read + deep link navigate.
- **States:** Default, Loading, Empty, Error, Offline.
- **Data:** Role-scoped notifications.
- **Consolidation note:** Intentionally shared — different permission scopes, same UI pattern. See FILE 2 #2.

### Shared → Academic Calendar Screen (Legacy)

- **Type:** Full screen overlay (shared composable)
- **Composable:** `AcademicCalendarScreenV2`
- **Used by:** Admin (A-6.02), Teacher (B-6.12), Parent (C-6.02)
- **Layout:** Month view calendar with event dots. Tap date → events list. Tap event → detail.
- **States:** Default, Loading, Empty, Error.
- **Data:** Role-scoped calendar events.
- **Consolidation note:** Legacy — superseded by AcademicCalendarPlatform for admin. See FILE 2 #3.

### Shared → Digital ID Card Screen

- **Type:** Full screen overlay (shared composable)
- **Composable:** `DigitalIdCardScreen`
- **Used by:** Teacher (B-6.08), Parent (C-6.14)
- **Layout:** ID card display (photo, name, ID, school, valid dates) + QR code + "Download" button.
- **States:** Default, Loading, Error.
- **Data:** Role-specific ID info (teacher vs student).
- **Consolidation note:** Intentionally shared — same visual pattern, different data. See FILE 2 #8.

### Shared → Scheduled Messages Screen

- **Type:** Full screen overlay (shared composable)
- **Composable:** `ScheduledMessagesScreenV2`
- **Used by:** Admin (A-6.35), Teacher (B-6.09)
- **Layout:** Scheduled message list. Each: title, date/time, audience, status. Edit/Cancel.
- **States:** Default, Loading, Empty, Error.
- **Data:** Role-scoped scheduled messages.
- **Consolidation note:** Intentionally shared. See FILE 2 #9.

### Shared → Messages Screen (3 Variants)

- **Type:** Full screen overlay (3 separate composables)
- **Composables:** `MessagesScreenV2` (admin), `TeacherMessagesScreenV2` (teacher), `ParentMessagesScreenV2` (parent)
- **Used by:** Admin (A-6.06), Teacher (B-6.11), Parent (C-6.06 + Conversations tab)
- **Layout:** All follow WhatsApp-style inbox + conversation pattern. Admin messages school↔parent. Teacher messages teacher↔parent. Parent messages parent↔teacher/admin.
- **States:** Default, Loading, Empty, Error, Sending, Offline.
- **Consolidation note:** 3 separate composables with same UI pattern — candidate for unification into one parameterized composable. See FILE 2 #4.

### Shared → Event Registration Screen (2 Variants)

- **Type:** Full screen overlay (2 separate composables)
- **Composables:** Admin event registration, `ParentEventRegistrationScreenV2`
- **Used by:** Admin (A-6.36), Teacher (B-6.10), Parent (C-6.16)
- **Layout:** Event list → event detail → registration. Admin/teacher view = manage registrations. Parent view = register for events.
- **States:** Default, Loading, Empty, Error, Registered.
- **Consolidation note:** Admin/teacher share a composable; parent has separate. See FILE 2 #15.

### Shared → Discovery Screen

- **Type:** Full screen (shared composable)
- **Composable:** `DiscoveryScreenV2`
- **Used by:** UnauthFlow, Parent unlinked gate (C-0), Parent overlay (C-6.08)
- **Layout:** School search + location filter + school cards. Tap → school profile.
- **States:** Default, Loading, Empty, Error, Search empty.
- **Consolidation note:** Shared across 3 entry points. See FILE 2 #13.

### Shared → Parent Link Child Screen

- **Type:** Full screen (shared composable)
- **Composable:** `ParentLinkChildScreenV2`
- **Used by:** UnauthFlow, Parent unlinked gate (C-0), Parent overlay (C-6.07)
- **Layout:** Multi-step wizard: school → child details → verification → success.
- **States:** Step N, Validation error, Submitting, Success, Error.
- **Consolidation note:** Shared across 3 entry points. See FILE 2 #13.

---

## D-4 — Shared FOO Components (Cross-Portal)

These are FOOs (Further-Opening Options) that appear across multiple portals with the same pattern.

### Shared → VConfirmDialog (Destructive Action Confirmation)

- **Type:** Modal (dialog)
- **Used by:** All portals — logout, delete, deactivate, graduate, remove.
- **Layout:** `Dialog` + `VCard`: icon in danger circle → title → message → "Confirm" (Destructive) + "Cancel" (Ghost). Scrim dismisses.
- **States:** Visible, Hidden.
- **Data:** Title, message, confirm/cancel labels, icon.
- **Accessibility:** Clear danger styling. No ambiguous button labels.

### Shared → VSnackbar (Transient Feedback)

- **Type:** Toast/snackbar
- **Used by:** All portals — success/error feedback after actions.
- **Layout:** Bottom-anchored bar: icon + message + optional action label. Slide-up + fade animation. Auto-dismiss after 3–4s.
- **Tones:** Success (green check), Error (red alert), Warning (yellow triangle), Info (blue book).
- **States:** Visible, Hidden (animating out).
- **Data:** Message, tone, action label.
- **Accessibility:** Large text. Clear icon. Not blocking — user can continue interacting.

### Shared → VEmptyState (Empty/No-Data Placeholder)

- **Type:** Inline content replacement
- **Used by:** All portals — every list-based screen's empty state.
- **Layout:** Centered: icon in circle → title → body text → optional action button.
- **States:** Visible (replaces content area).
- **Data:** Title, body, icon, action.
- **Accessibility:** Plain language. Friendly tone. "No announcements yet" not "Empty dataset."

### Shared → VComingSoon (Preview Placeholder)

- **Type:** Inline card
- **Used by:** All portals — unshipped features.
- **Layout:** `VCard`: "● PREVIEW" badge → title → description → optional preview slot → "Notify me when ready" pill.
- **States:** Default.
- **Data:** Title, description.

### Shared → VStateHost (Loading/Error/Empty/Content Manager)

- **Type:** Content state manager (not visible itself — wraps content)
- **Used by:** All portals — every data-driven screen.
- **Layout:** 4-phase crossfade: Loading (skeleton) → Error (message + retry) → Empty (VEmptyState) → Content. 300ms transition.
- **States:** Loading, Error, Empty, Content.
- **Data:** State flag, error message, retry callback.

### Shared → VPullRefresh (Pull-to-Refresh)

- **Type:** Gesture wrapper
- **Used by:** All portals — all scrollable list screens.
- **Layout:** Wraps scrollable content. Brand-tinted indicator appears on pull-down gesture.
- **States:** Idle, Refreshing (indicator visible), Complete.
- **Data:** isRefreshing flag, onRefresh callback.

### Shared → VDatePicker (Calendar Date Picker)

- **Type:** Modal (dialog)
- **Used by:** All portals — date selection in forms, attendance, leave, events.
- **Layout:** `Dialog` with calendar grid: month header + prev/next arrows → weekday labels → day cells (selectable, current day highlighted). "OK" + "Cancel" buttons.
- **States:** Default, Selected.
- **Data:** Selected date, min/max date range.

### Shared → VTimePicker (Time Picker)

- **Type:** Modal (dialog)
- **Used by:** All portals — time selection in event creation, timetable, PTM scheduling.
- **Layout:** `Dialog` with clock face: hour/minute selection. AM/PM toggle. "OK" + "Cancel".
- **States:** Default, Selected.
- **Data:** Selected time.

### Shared → VThemePicker (Theme Mode Selector)

- **Type:** Inline card with options
- **Used by:** Admin Settings, Teacher Profile, Parent Account Settings.
- **Layout:** `VCard` with options: Light, Dark, High Contrast, Custom. Current selection highlighted with checkmark.
- **Interactions:** Tap option → switches theme immediately (300ms crossfade).
- **States:** Default.
- **Data:** Current theme mode, custom theme ID.

### Shared → FilterChip (Category Filter)

- **Type:** Pill chip (tappable)
- **Used by:** All portals — announcement categories, subject filters, status filters.
- **Layout:** Rounded pill with label. Active state: filled background. Inactive: outlined. Horizontal scroll row.
- **Interactions:** Tap → toggles active/inactive → filters list.
- **States:** Active, Inactive.
- **Data:** Label, active state.

---

## D-5 — Deep Link Routing Map

All deep links are parsed by `NavGraphV2.parseDeepLink()` into `DeepLinkTarget` sealed classes. The routing map:

| Path Pattern | Role | Target |
|---|---|---|
| `/parent/messages` | Parent | ParentOverlay.Messages |
| `/parent/calendar` | Parent | ParentOverlay.Calendar |
| `/parent/fees` | Parent | ParentTab.Fees |
| `/parent/leave` | Parent | ParentOverlay.Leave |
| `/parent/scholarships` | Parent | ParentOverlay.Scholarships |
| `/parent/transport` | Parent | ParentOverlay.Transport |
| `/parent/report-card` | Parent | ParentTab.Academics (Report sub-tab) |
| `/parent/tutor` | Parent | ParentOverlay.TutorChat |
| `/parent/health` | Parent | ParentOverlay.Health |
| `/parent/pulse` | Parent | ParentOverlay.Pulse |
| `/parent/library` | Parent | ParentOverlay.Library |
| `/parent/events` | Parent | ParentOverlay.EventRegistration |
| `/parent/notifications` | Parent | ParentOverlay.Notifications |
| `/teacher/messages` | Teacher | TeacherOverlay.Messages |
| `/teacher/calendar` | Teacher | TeacherOverlay.Calendar |
| `/teacher/notifications` | Teacher | TeacherOverlay.Notifications |
| `/teacher/transport` | Teacher | TeacherOverlay.TransportAttendance |
| `/teacher/pews` | Teacher | TeacherOverlay.Pews |
| `/teacher/timetable` | Teacher | TeacherScreen.Timetable |
| `/teacher/report-review` | Teacher | TeacherOverlay.ReportReview |
| `/school/messages` | SchoolAdmin | SchoolOverlay.Messages |
| `/school/calendar` | SchoolAdmin | SchoolOverlay.Calendar |
| `/school/notifications` | SchoolAdmin | SchoolOverlay.Notifications |
| `/school/people` | SchoolAdmin | SchoolScreen.People |
| `/school/records` | SchoolAdmin | SchoolScreen.Records |
| `/school/pews` | SchoolAdmin | SchoolOverlay.PewsCohort |
| `/school/transport` | SchoolAdmin | SchoolOverlay.TransportManagement |
| `/school/scholarships` | SchoolAdmin | SchoolOverlay.ScholarshipManagement |
| `/school/library` | SchoolAdmin | SchoolOverlay.Library |
| `/school/events` | SchoolAdmin | SchoolOverlay.EventRegistration |
| `/alumni/*` | Alumni | AlumniScreen.* (via Parent portal fallback) |
| `/*` (unknown) | Any | Generic fallback (current portal home) |

---

## D-6 — Orphaned / Dead-End Screen Inventory

These screens exist in the codebase but are NOT wired into any navigation path:

| Screen File | Status | Resolution |
|---|---|---|
| `CommonLandingScreenV2.kt` | Superseded by `CommonLandingScreenV3.kt` | Remove file. See FILE 2 #14. |
| `ScholarshipsScreenV2.kt` (parent) | Not referenced by any overlay enum | Wire into ParentOverlay.Scholarships or remove. See FILE 2 #10. |
| `SchoolDayConfigScreenV2.kt` | Not referenced by any overlay or settings row | Wire into Settings → Academic Year or Classes & Subjects. See FILE 2 #18. |
| `PewsEffectivenessScreenV2.kt` | Not referenced by any overlay | Delete or merge into `AdminReportingEffectivenessScreen` (already wired). See FILE 2 #19. |
| `ParentPewsScreenV2.kt` | Not referenced by any overlay enum — only referenced within itself | Delete. `ParentOverlay.Pulse` maps to `ParentPulseScreen` (different file). See FILE 2 #23. |
| `ResultsPublishScreenV2.kt` | `SchoolOverlay.Results` enum exists but NO code ever sets it — dead `when` branch | Delete or merge into `AdminReportPublishScreen` (A-6.29). See FILE 2 #22. |
| `SchoolOverlay.Calendar` (enum value) | `SchoolOverlay.Calendar` enum exists but NO code ever sets it — dead `when` branch | Remove enum value + `when` branch. See FILE 2 #21. |
| `PewsPreview.kt` | Preview/demo file | Keep for dev preview only. No production wiring needed. |
| `SriPreview.kt` | Preview/demo file (discovery) | Keep for dev preview only. No production wiring needed. |
| `AiReportCardPreview.kt` | Referenced by ParentAcademics + ParentReport | Not orphaned — wired correctly. |
| `TutorPlanScreen.kt` | Not referenced by any overlay | Wire into TutorChat or TutorProgress. See FILE 2 #20. |
| `TutorPracticeScreen.kt` | Not referenced by any overlay | Wire into TutorChat or TutorProgress. See FILE 2 #20. |

---

*End of Part D — Shared/Common Screens*

---

## Iteration Summary

| Iteration | Scope | Result |
|---|---|---|
| 0 | Ground truth crawl — all screen files, nav graph, components | ✅ Complete |
| 1 | Full inventory sweep — every Screen → Tab → Sub-tab → FOO → State | ✅ Complete |
| 2 | Duplicate & redundancy matrix — cross-referenced all items | ✅ Complete (15 duplications found) |
| 3 | Consolidation & IA redesign — resolved all duplications | ✅ Complete (20 restructure entries) |
| 4 | Premium Figma-ready spec writing — all screens documented | ✅ Complete (FILE 1) |
| 5 | Adversarial validation — checked Iteration 1 inventory against FILE 1 | ❌ Found 3 missing items (TutorPlan, TutorPractice, SchoolDayConfig) |
| 6 | Fix pass — added missing screens to FILE 1 + FILE 2 entries #18-20 | ✅ Complete |
| 7 | Final validation — re-checked all items | ✅ Clean — zero missing items |

**Total iterations run:** 7. **Stopped because:** final validation pass came back clean.

---

*End of FILE 1 — ENROLLPLUS_SCREEN_ARCHITECTURE_FINAL.md*
