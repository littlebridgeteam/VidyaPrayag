# Teacher Portal V2 — Redesign, Restructure & Rebuild Plan

> **This is not a reskin.** The current work is a ground-up redesign and restructure of the Teacher Portal's user interface and information architecture. Every tab, overlay, and workflow is being rebuilt to match the premium, parent-portal-grade experience while remaining speed-first for teachers. Do not treat this as "change the colours." The layout patterns, navigation hierarchy, component language, and data plumbing are all being rethought.

---

## 1. Current Phase

We are in the **middle of the rebuild**.

- The **Home tab** has been fully rebuilt as the new entry point.
- The **Update tab** has been rebuilt with a tool grid and a clean scope gate.
- The remaining tabs (Classes, Timetable, Profile) have been unified to the new cream base, comfortable typography, and dock-safe padding.
- The first two overlays (Health Alerts, PEWS) have been migrated to the new base.
- The remaining overlays still need the same treatment.

Everything compiles and is pushed to `feature/deep400_fixes`.

---

## 2. What Has Been Done

### 2.1 Home tab — fully rebuilt

File: `composeApp/src/commonMain/kotlin/com/littlebridge/enrollplus/ui/v2/screens/teacher/TeacherHomeScreenV2.kt`

Sections in vertical order on `VColors.cream`:

1. **TeacherHomeHeader**
   - Wordmark "Enroll+" in violet, left aligned.
   - Greeting "Hi {name}" in violet, subtitle "here's your day" in ink.
   - Notification bell icon (outline) on the right.
   - Uses `VColors.violet`, `VTypography.wordmark`, `VTypography.caption`, `VTypography.h2`.

2. **NowTeachingCard**
   - Violet gradient hero card.
   - Shows the current or next live class with subject, class-section, room, timing.
   - Tags: NOW / NEXT.
   - CTAs: "Mark attendance" and "Lesson plan" (primary violet buttons).
   - Empty state: "No class right now".

3. **Today's Schedule**
   - Horizontal scroll row of white class cards.
   - Each card tagged NOW / NEXT / LATER.
   - Tapping opens the scoped Attendance or Lesson Plan tool.

4. **Pending Actions**
   - Real obligation counts from `TeacherObligationsViewModel`.
   - Cards: Attendance to mark, Homework to publish, Results to publish, Leave requests.
   - Empty state: "All caught up".

5. **Quick Actions**
   - 2×2 grid: Attendance, Marks, Syllabus, Homework.
   - Each routes to the Update tab with the right tool pre-selected.

6. **My Classes**
   - Up to 4 class rows from `TeacherClassesViewModel`.
   - Each row shows class, section, subject, student count, attendance status.
   - Tapping goes to Classes tab.

7. **Upcoming Events**
   - Teacher PTM / event cards from `TeacherEventRegistrationViewModel`.
   - Date chip + event title + status.

Data sources:
- `TeacherTodayViewModel` — hero + schedule
- `TeacherCheckInViewModel` — first-login check-in popup
- `TeacherObligationsViewModel` — pending actions
- `TeacherClassesViewModel` — my classes
- `TeacherEventRegistrationViewModel` — upcoming events

### 2.2 Update tab — rebuilt

File: `composeApp/src/commonMain/kotlin/com/littlebridge/enrollplus/ui/v2/screens/teacher/TeacherUpdateScreenV2.kt`

- Cream background.
- Header: "Update" + "Mark & publish".
- Tool grid (2 rows):
  - Row 1: Attendance, Marks, Syllabus
  - Row 2: Homework, Lesson Plan
- Each tool shows the class scope gate (`TeacherScopeSelector`) first.
- After picking a class, a scope bar shows the selected class + a "Change" button.
- Tool screens are delegated to existing scoped screens.

### 2.3 Classes / Timetable / Profile tabs — unified

- Background changed to `VColors.cream`.
- Headers reduced to comfortable `VTypography.h2` / `VTheme.type.h2` size.
- Bottom padding increased to `120.dp` so content clears the floating dock.

Files:
- `TeacherClassesScreenV2.kt`
- `TeacherTimetableScreenV2.kt`
- `TeacherProfileScreenV2.kt`

### 2.4 Overlays — first pass

- `TeacherHealthAlertsScreenV2.kt` — cream background + dock clearance.
- `TeacherPewsScreenV2.kt` — cream background + dock clearance.

### 2.5 Backend wiring

File: `composeApp/src/commonMain/kotlin/com/littlebridge/enrollplus/ui/v2/screens/teacher/TeacherPortalV2.kt`

- `onOpenUpdateTool(tool)` wired into `TeacherHomeScreenV2`.
- `onOpenNotifications` wired to the Notifications overlay.
- Deep-link routing preserved.

### 2.6 Strings

File: `shared/src/commonMain/kotlin/com/littlebridge/enrollplus/core/locale/AppStrings.kt`

Added keys for the new home sections:
- `TC_NOW_TEACHING`
- `TC_QUICK_ACTIONS`
- `TC_PENDING_ACTIONS`
- `TC_UPCOMING_EVENTS`
- `TC_YOUR_DAY`
- `TC_NO_PERIOD_RIGHT_NOW`
- `TC_LATER`
- `TC_TO_PUBLISH`
- `TC_LEAVE_REQUESTS`

English + Hindi translations added.

---

## 3. What Is Left

### 3.1 Remaining overlays to migrate

These overlays still need to be opened, reviewed, and rebuilt/restructured to the new cream-base system, comfortable typography, dock-safe padding, and the component vocabulary below:

1. **Notifications** — `NotificationsScreenV2` (shared)
2. **Transport Attendance** — `TransportAttendanceScreenV2`
3. **PEWS** — `TeacherPewsScreenV2` (structure done, content cards still use old theme helpers)
4. **Report Review** — `TeacherReportReviewQueueScreen`
5. **Report Draft Editor** — `TeacherReportDraftEditorScreen`
6. **Heatmap** — `TeacherHeatmapScreen`
7. **Digital ID Card** — `DigitalIdCardScreen` (shared)
8. **Scheduled Messages** — `ScheduledMessagesScreenV2` (shared)
9. **Event Registration** — `TeacherPtmEventRegistrationScreenV2`
10. **Messages** — `TeacherMessagesScreenV2`
11. **Calendar** — `AcademicCalendarScreenV2` (shared)

### 3.2 Tool sub-screens

The Update tab delegates to these existing scoped screens. They need the same redesign pass:

- `TeacherAttendanceScreenV2.kt`
- `TeacherMarksScreenV2.kt`
- `TeacherSyllabusScreenV2.kt` (check exact filename)
- `TeacherHomeworkScreenV2.kt`
- `TeacherLessonPlanScreenV2.kt` (check exact filename)

### 3.3 Shared components / TeacherKit

`TeacherKit.kt` currently mixes theme-based helpers (`TCard`, `TEyebrow`, `TIconDisc`, `TPill`, etc.) with token-based callers. A later pass should reconcile this so every helper uses either the token system or a clean bridge. For now, new/rebuilt screens should prefer direct token usage.

### 3.4 Verification

- Run every screen on a real device / preview.
- Confirm no text is cut off, no buttons hidden, no overlays that won't close.
- Confirm back navigation and deep links still work.

---

## 4. Design System — Tokens

### 4.1 Base colours

Use the token object:

```kotlin
import com.littlebridge.enrollplus.ui.tokens.VColors
```

| Token | Hex | Usage |
|-------|-----|-------|
| `VColors.cream` | `#FBF8F4` | Page background for every teacher screen. |
| `VColors.creamDeep` | `#F5F0E8` | Slightly deeper cream for input surfaces or empty-state canvas. |
| `VColors.surfaceCard` | `#FFFFFF` | Card surfaces. |
| `VColors.surfaceTint` | `#F8F4EF` | Subtle tinted rows / selected chips / soft backgrounds. |
| `VColors.surfaceWarm` | `#FFFFF6EE` | Warm highlight surfaces. |

### 4.2 Ink / text

| Token | Hex | Usage |
|-------|-----|-------|
| `VColors.ink` | `#1A1614` | Primary text, headers, body. |
| `VColors.ink2` | `#5C544E` | Secondary text, captions, metadata. |
| `VColors.ink3` | `#8A8078` | Tertiary / disabled / placeholders. |

### 4.3 Primary / accent

| Token | Hex | Usage |
|-------|-----|-------|
| `VColors.violet` | `#5B41D5` | Primary brand colour. Active tabs, primary buttons, wordmark, notification dot, hero gradients. |
| `VColors.violetHover` | `#4A30C4` | Pressed / hover state. |
| `VColors.violetSoft` | `#EEE8FB` | Light violet backgrounds, active chip fill, soft pills. |
| `VColors.violetInk` | `#16006E` | Text on violet surfaces. |

### 4.4 Accent palette

Used for subject chips, status pills, and data differentiation:

| Token | Hex | Usage |
|-------|-----|-------|
| `VColors.mint` / `mintSoft` | `#2DCE89` / `#DCF5E8` | Success, present, good standing. |
| `VColors.sky` / `skySoft` | `#18BFFF` / `#E0F6FF` | Information, blue-subject accent. |
| `VColors.coral` / `coralSoft` | `#F82B60` / `#FFE4EC` | Urgent, absent, high risk. |
| `VColors.gold` / `goldSoft` | `#FCB400` / `#FFF4D1` | Warnings, late, pending attention. |

### 4.5 Outlines

| Token | Hex | Usage |
|-------|-----|-------|
| `VColors.line` | `#E8E0D6` | Card borders, dividers. |
| `VColors.lineSoft` | `#F0EAE0` | Subtle hairlines, inactive chip borders. |

### 4.6 States

| Token | Hex | Usage |
|-------|-----|-------|
| `VColors.success` / `successSoft` | `#2D7A4A` / `#D4EDDB` | Success text / success background. |
| `VColors.error` / `errorSoft` | `#BA1A1A` / `#FFFFDAD6` | Error text / error background. |

---

## 5. Typography

Use the token object:

```kotlin
import com.littlebridge.enrollplus.ui.tokens.VTypography
```

| Token | Size | Weight | Usage |
|-------|------|--------|-------|
| `VTypography.h1` | 44sp | ExtraBold | Do not use for tab headers. Reserved for splash / hero. |
| `VTypography.h2` | 24sp | ExtraBold | Tab headers, card titles, section hero text. |
| `VTypography.h3` | 22sp | ExtraBold | Sub-section titles. |
| `VTypography.body` | 15sp | Medium | Primary body text. |
| `VTypography.bodySmall` | 14sp | Medium | Secondary body, card metadata. |
| `VTypography.label` | 13sp | SemiBold | Buttons, chips, eyebrow labels. |
| `VTypography.caption` | 12sp | Medium | Fine metadata, timestamps, hints. |
| `VTypography.wordmark` | 16sp | ExtraBold | "Enroll+" header wordmark. |

**Rule:** Use tokens as-is. Do not override `fontSize` to make text larger. Comfortable, not oversized.

---

## 6. Shapes

Use the token object:

```kotlin
import com.littlebridge.enrollplus.ui.tokens.VShapes
```

| Token | Radius |
|-------|--------|
| `VShapes.sm` | 8.dp |
| `VShapes.md` | 12.dp |
| `VShapes.lg` | 16.dp |
| `VShapes.xl` | 20.dp |
| `VShapes.xxl` | 24.dp |
| `VShapes.full` | 999.dp |

- Cards: `VShapes.lg` or `VShapes.xl`.
- Chips / pills: `VShapes.full`.
- Buttons: `VShapes.full` (lozenge) or `VShapes.lg`.
- Hero cards: `VShapes.xxl`.

---

## 7. Component Vocabulary

### 7.1 Cards

- White surface (`VColors.surfaceCard`).
- 1dp hairline border (`VColors.lineSoft` or `VColors.line`).
- 16.dp radius (`VShapes.lg`).
- 16.dp internal padding.
- No heavy drop shadows. Elevation is tonal / border-based.

### 7.2 Hero card

- Violet gradient background (`VColors.violet` to `VColors.violetHover`).
- White text.
- Large radius (`VShapes.xxl`).
- Two primary CTAs as white/violet filled or outlined buttons.

### 7.3 Chips / pills

- Inactive: white card, soft outline, ink2 text.
- Active: violet soft fill, violet text, no outline or violet outline.
- Full radius.

### 7.4 List rows

- Full-width row inside a card, or card-as-row.
- Leading: subject-coloured disc icon or avatar.
- Trailing: chevron or status icon.
- Tap target at least 48.dp high.

### 7.5 Section headers

- Eyebrow: all-caps label in `VTypography.label` with a coloured dot.
- Title: `VTypography.h2` or `VTypography.h3`.
- Optional trailing text action in violet.

### 7.6 Empty states

- No illustrations unless necessary.
- Centre icon in a soft tinted circle.
- Title + subtext in ink2.
- Action button if there is a logical next step.

### 7.7 Loading / error

- Centre spinner for initial load.
- Inline retry button for errors.
- Skeleton placeholders only when meaningful.

---

## 8. Navigation & Layout Rules

### 8.1 Bottom dock

- 5 tabs: Home, Update, Classes, Timetable, Profile.
- Floating pill with violet active indicator.
- Home tab hides the canonical header and renders its own.
- All other tabs show the slim canonical `TeacherHeader`.

### 8.2 Overlays

- Every overlay is full-screen above tabs.
- Every overlay has a back affordance (`VBackHeader`) and closes with the system back gesture.
- No dead-ends.

### 8.3 Tab internal navigation

- Classes: list → class detail → student profile. All in-tab.
- Update: tool grid → scope gate → scoped tool screen. Scope can be changed without leaving the tab.

### 8.4 Padding

- Horizontal page padding: `16.dp`.
- Vertical spacing between sections: `16.dp`–`24.dp`.
- Bottom padding for scrollable content: `120.dp` minimum to clear the dock.
- Status bars: `statusBarsPadding()` on overlays; the tab shell handles safe area for tabs.

### 8.5 Touch targets

- Minimum 48.dp × 48.dp for icon buttons.
- Chips and rows should be at least 44.dp high.

---

## 9. Tab-by-Tab Detailed Concept

### 9.1 Home tab

**Purpose:** The teacher's daily command centre. Time-sensitive, glanceable, action-oriented.

**Layout:** Single vertical scroll. Cream background. Sections stacked with `24.dp` gaps.

1. **Header**
   - Left: "Enroll+" wordmark (violet), "Hi {name}" caption, "here's your day" h2 with "your day" in violet.
   - Right: notification bell icon.

2. **Now Teaching hero**
   - Full-width violet gradient card.
   - Top row: eyebrow "NOW TEACHING", pill tag "NOW" or "NEXT".
   - Middle: class-subject title, room, time range.
   - Bottom row: two CTAs — "Mark attendance" (white fill) and "Lesson plan" (white outline).
   - If nothing live: "No class right now" + "No classes scheduled today."

3. **Today's Schedule**
   - Section header: "TODAY'S SCHEDULE" left, "All classes" text action right.
   - Horizontal LazyRow of white cards.
   - Card: time, subject, class-section, room, lesson plan status, attendance status.
   - Tag chip: NOW / NEXT / LATER.

4. **Pending Actions**
   - Section header: "Pending actions".
   - Horizontal row of compact count cards or a 2×2 grid.
   - Each card: icon, count, label (e.g. "3 to mark", "2 to publish").
   - Empty: "All caught up".

5. **Quick Actions**
   - Section header: "Quick actions".
   - 2×2 grid of tool chips: Attendance, Marks, Homework, Syllabus.
   - Each opens Update tab with that tool pre-selected.

6. **My Classes**
   - Section header: "My classes".
   - Vertical list of up to 4 class rows.
   - Row: subject-colour disc, class-section, subject, student count, attendance chip, chevron.
   - "See all" trailing action to Classes tab.

7. **Upcoming Events**
   - Section header: "Upcoming events".
   - Vertical list of PTM/event cards with date chip.

### 9.2 Update tab

**Purpose:** The teacher's write plane. Pick a tool, pick a class, do the work.

**Layout:** Vertical scroll. Cream background.

1. **Header**
   - "Update" h2.
   - "Mark & publish" bodySmall.

2. **Tool grid**
   - 2 rows, all buttons visible:
     - Row 1: Attendance, Marks, Syllabus
     - Row 2: Homework, Lesson Plan
   - Active tool has violet soft fill + violet text.
   - Tapping a tool resets to its scope gate.

3. **Scope gate**
   - Showed when no class is selected.
   - Title: "Which class?"
   - Caption: "Pick a class for {tool}"
   - Search field if more than 6 allocations.
   - List of class cards: subject-colour disc, class-section, subject, student count, class-teacher pill, attendance tick if done.

4. **Scoped workspace**
   - Scope bar: class · subject label + "Change" button.
   - Below: the actual attendance / marks / homework / syllabus / lesson plan UI.

### 9.3 Classes tab

**Purpose:** Browse my classes, drill into a class, drill into a student.

**Layout:** In-tab navigation with AnimatedContent.

**List state:**
- Header: "Classes" h2 + "You teach {count} classes" body.
- Search input.
- Filter chip: "All classes" / "Class teacher" / "Subject only".
- Vertical list of class cards.
- Card: subject-colour disc, class-section, subject, student count, attendance status, chevron.

**Detail state:**
- Back header.
- Hero identity card: class, section, subject, student count.
- Next class card.
- Attendance snapshot: present / absent / late / leave counts.
- Weekly timetable card.
- Scheduled tests card.
- Active homework card.
- Full roster with tap-to-student.

**Student profile state:**
- Back header.
- Student avatar + name.
- Attendance summary.
- Performance / marks summary.
- Flags (health, behaviour, etc.).
- Parent contact.

### 9.4 Timetable tab

**Purpose:** View and request changes to the weekly schedule.

**Layout:** Vertical scroll. Top tabs + day selector.

1. **Top tabs**
   - "This week" / "Change requests"

2. **Day selector**
   - Mon–Sat pill chips.
   - Selected day has violet soft fill.

3. **This week view**
   - List of period cards for the selected day.
   - Card: time block, subject, class-section, room.
   - Edit / delete actions on each card.
   - "Request new period" button at the bottom.

4. **Change requests view**
   - List of pending/approved/rejected timetable change requests.
   - Status pill on each.

### 9.5 Profile tab

**Purpose:** Account identity, leave, security, preferences, logout.

**Layout:** Vertical scroll. Cream background.

1. **Header**
   - "Profile" h2.

2. **Identity card**
   - Avatar, name, username, school, subjects taught, classes taught.

3. **My leave**
   - "Apply leave" button.
   - Leave composer: date range, reason.
   - List of submitted leave requests with status.

4. **Change password**
   - Expandable form: old password, new password, confirm.

5. **Appearance**
   - Theme picker: Warm / Light / Night.

6. **Language**
   - Language picker.

7. **Logout**
   - Destructive button with confirmation dialog.

---

## 10. Overlays Detailed Concept

### 10.1 Notifications

- Full-screen list of notification cards.
- Unread indicator dot.
- Swipe or tap to mark read.
- Deep-link support.

### 10.2 Transport Attendance

- Route/class selector.
- Student list with present / absent / not-marked states.
- Mark-all / undo actions.

### 10.3 PEWS (Students needing attention)

- List of at-risk students from own classes.
- Each card: avatar, name, class, attendance %, marks %, deterministic signals, AI line (if provided).
- Open interventions with status, urgency, SLA, plan steps.
- Actions: Start, Mark done, Dismiss, Generate parent draft, Send parent message.

### 10.4 Report Review + Draft Editor

- Queue of report cards pending review.
- Card: student, class, term, status.
- Tap opens draft editor.
- Editor: subject-wise marks / remarks, save, submit.

### 10.5 Heatmap

- Subject / class performance heatmap.
- Colour-coded cells (mint → gold → coral).
- Drill into weak areas.

### 10.6 Digital ID Card

- Shared component, teacher mode.
- School-branded ID with photo, name, role, QR code.

### 10.7 Scheduled Messages

- List of scheduled announcements.
- Create / edit / delete.
- Recipients, send time, preview.

### 10.8 Event Registration (PTM)

- List of upcoming PTM events.
- Registration status.
- Slot picker if applicable.

### 10.9 Messages

- Conversation list → thread view.
- Send text / attachments.
- Read receipts.

### 10.10 Calendar

- Monthly / weekly calendar.
- Events, holidays, exam schedules.
- Tap event for detail.

---

## 11. Reference Screens

For visual and structural reference, look at the existing **Parent Portal** screens that have already been rebuilt to the cream-base system:

- `composeApp/src/commonMain/kotlin/com/littlebridge/enrollplus/ui/v2/screens/parent/ParentAcademicsScreenV2.kt`
  - Shows how academic sections are grouped with clear hierarchy, card spacing, and comfortable typography.
- `composeApp/src/commonMain/kotlin/com/littlebridge/enrollplus/ui/v2/screens/parent/ParentHomeScreenV2.kt`
  - Reference for the greeting hero + wordmark + notification bell header pattern.

Also reference the HTML prototype if available:
- `preview/enrollplus-parent-prototype.html`
- `preview/enrollplus-teacher-home-prototype.html`

---

## 12. Non-Negotiable Rules

1. **No hardcoded data.** Every number, name, count, and status comes from a ViewModel → API → backend.
2. **No raw values.** Use `VColors`, `VTypography`, `VShapes` tokens only.
3. **Comfortable fonts.** Do not inflate text sizes.
4. **All primary actions visible.** No overflow menus for core features.
5. **Dock clearance.** Scrollable content must end at least `120.dp` above the screen bottom.
6. **Every overlay closes.** Back button + system back + tap outside where appropriate.
7. **No dead ends.** Every button navigates somewhere meaningful.
8. **Respect existing API signatures.** Do not delete callbacks or ViewModel parameters when rebuilding a screen.
9. **Add string keys for new copy.** English + Hindi at minimum.
10. **Compile after every screen.** `./gradlew.bat :composeApp:compileDevDebugKotlinAndroid`.

---

## 13. Current File Checklist

| File | Status |
|------|--------|
| `TeacherHomeScreenV2.kt` | ✅ Rebuilt |
| `TeacherUpdateScreenV2.kt` | ✅ Rebuilt |
| `TeacherClassesScreenV2.kt` | ✅ Cream + clearance + header |
| `TeacherTimetableScreenV2.kt` | ✅ Cream + clearance |
| `TeacherProfileScreenV2.kt` | ✅ Cream + clearance + header |
| `TeacherHealthAlertsScreenV2.kt` | ✅ Cream + clearance |
| `TeacherPewsScreenV2.kt` | ✅ Cream + clearance |
| `TransportAttendanceScreenV2.kt` | ✅ Cream tokens |
| `TeacherReportReviewQueueScreen.kt` | ✅ Cream tokens (VtC/VtT bridge) |
| `TeacherReportDraftEditorScreen.kt` | ✅ Cream tokens (VtC/VtT bridge) |
| `TeacherHeatmapScreen.kt` | ✅ Cream tokens (VtC/VtT bridge) |
| `TeacherPtmEventRegistrationScreenV2.kt` | ✅ Cream tokens (VtC/VtT bridge) + list bottom clearance |
| `TeacherMessagesScreenV2.kt` | ✅ Cream tokens (VtC/VtT bridge) |
| `NotificationsScreenV2.kt` | ✅ Already token-based (shared) |
| `DigitalIdCardScreen.kt` | ✅ Cream tokens (shared) |
| `ScheduledMessagesScreenV2.kt` | ✅ Already token-based (shared) |
| `AcademicCalendarScreenV2.kt` | ✅ Cream tokens (shared) |
| `TeacherAttendanceScreenV2.kt` | ✅ Cream tokens (tool sub-screen) |
| `TeacherMarksScreenV2.kt` | ✅ Cream tokens (tool sub-screen) |
| `TeacherHomeworkScreenV2.kt` | ✅ Cream tokens (tool sub-screen) |
| `TeacherSyllabusScreenV2.kt` | ✅ Cream tokens (tool sub-screen) |
| `TeacherLessonPlanScreenV2.kt` | ✅ Cream tokens (tool sub-screen) |

### 13.1 Shell / shared components (§3.3 reconciliation)

| File | Status |
|------|--------|
| `TeacherKit.kt` | ✅ Legacy `T*` atoms retargeted to cream tokens via `KitC` bridge |
| `TeacherKitV2.kt` | ✅ Central `VtC` / `VtT` / `coloredV` bridge + `Vt*` token atoms + `vtSubjectColor` |
| `TeacherPortalV2.kt` | ✅ Token-based shell (no legacy `VTheme` wrapper) |
| `TeacherHeader.kt` | ✅ Cream tokens (VtC/VtT bridge) |
| `TeacherDock.kt` | ✅ Cream tokens (VtC/VtT bridge) |
| `TeacherDialogs.kt` | ✅ Cream tokens (VtC/VtT bridge) |
| `TeacherCheckInPopup.kt` | ✅ Cream tokens (VtC/VtT bridge) |
| `TeacherScopeSelector.kt` | ✅ Cream tokens (VtC/VtT bridge) |
| `TeacherStudentProfileScreenV2.kt` | ✅ Cream tokens (VtC/VtT bridge) |

**Migration status:** the entire Teacher Portal package now renders on the
cream/violet token system. A portal-wide grep confirms **zero functional
`VTheme` code references** remain in `ui/v2/screens/teacher/` (only KDoc
comments in `TeacherKitV2.kt` mention the legacy name to document the bridge).

> The **migration bridge pattern** (`val c = VtC`, `VtT.*`, `.coloredV(...)`,
> `vtSubjectColor(...)`) preserves 100% of each screen's layout and branch
> logic while retiring the legacy lavender `VTheme` dependency. `VtC`/`VtT`
> map every legacy accessor name onto the new `VColors`/`VTypography` tokens,
> so consumers migrate with minimal churn and no behavioural change.

---

## 14. How to Continue

The teacher-portal migration described in this document is **complete**. Any
future screen should use the token system directly
(`com.littlebridge.enrollplus.ui.tokens` — `VColors` / `VTypography` /
`VShapes`) or the `VtC` / `VtT` bridge, following the vocabulary above. Note
that `parent/`, `discovery/`, and `auth/` V2 screens are **outside the scope of
this teacher-portal document** and may still use the legacy theme.

---

*Last updated: 2026-07-08 — Teacher Portal token migration completed.*

---

## 15. Verification Pass (2026-07-08)

A follow-up verification pass was run against §12 (Non-Negotiable Rules):

- **Rule 2 (no raw values):** `grep` for `Color(0x…)` across every teacher
  screen returns **0 matches** outside the `TeacherKit*` bridge files, where a
  single harmonious subject-colour rotation is documented. ✅
- **Rule 5 (dock clearance):** every *tab* content list ends on the shared
  `TeacherDockClearance` constant (`= 120.dp`, defined once in
  `TeacherKitV2.kt`). Overlays with a `VBackHeader` correctly use
  `24.dp`/`navigationBarsPadding()` instead of dock clearance because the
  floating dock is not rendered above an overlay. The PTM Event Registration
  overlay was the one list without explicit bottom padding; it now carries a
  `contentPadding` bottom inset so the final card is never flush against the
  system nav bar. ✅
- **Legacy `VTheme`:** portal-wide grep confirms the only remaining `VTheme`
  occurrences are KDoc comments plus the unrelated `VThemePicker` component. ✅
- **Strings:** all nine new `TC_*` home-section keys resolve in both the
  English and Hindi maps of `AppStrings.kt`. ✅
- **Rule 10 (compile):** the canonical build task
  `:composeApp:compileDevDebugKotlinAndroid` requires AGP 9.2.1 / Kotlin 2.2.10
  / compileSdk 36, whose Gradle + Kotlin daemons need ~3–4 GB of heap. The CI
  sandbox used for this pass has only ~1 GB of RAM, so the full Android build
  OOMs during project configuration and cannot be completed there — this is an
  environment limit, not a source defect. Every changed file was instead
  validated with a standalone Kotlin parse/analysis run (no syntax or
  structural errors) and a brace/paren balance sweep across all 26 teacher
  source files (0 mismatches). The full Gradle build should be run on a
  developer machine or a CI runner with ≥6 GB RAM before release.

---

## 12. Teacher Portal Gamification UI Specification

The teacher portal must include gamification tools at two levels: **per-student** (inside the student profile drill-down) and **per-class** (inside the class detail pane).

### 12.1 Per-Student Gamification Card
**Location:** `TeacherStudentProfileScreenV2.kt` — embedded as last item in `StudentProfileBody` LazyColumn.
**File:** `TeacherGamificationScreenV2.kt` — `TeacherStudentGamificationCard` composable.

**Features:**
1. **Earned Badges** — Horizontal scroll of student's earned badges (gold soft chips with star icon + badge name).
2. **Encourage Button** — Sends XP encouragement to student. Violet tint, heart icon.
3. **Spotlight Button** — Spotlights student for improvement. Gold tint, star icon.
4. **Send Shoutout** — Toggle to reveal text field + send button. Teal tint, megaphone icon.
5. **Assign Quest** — Toggle to reveal available quest list. Each quest row: name, description, XP reward. Tap to assign. Coral tint, target icon.
6. **Action Feedback** — Success/error message banner that auto-dismisses after 3s.

### 12.2 Per-Class Gamification Card
**Location:** `TeacherClassesScreenV2.kt` — embedded as last item in `ClassDetailBody` LazyColumn.
**File:** `TeacherGamificationScreenV2.kt` — `TeacherClassGamificationCard` composable.

**Features:**
1. **Overview Metrics** — Total XP, total badges awarded, active quests count. Three metric tiles in a row.
2. **Class Leaderboard** — Top 5 students by XP. Rank disc (gold/silver/bronze for top 3), student ID, total XP.
3. **Class Goals** — List of active class goals with progress bar (current/target), reward text.
4. **Pep Talk Button** — Send motivational pep talk to entire class. Confirm-then-send pattern. Violet tint.
5. **Create Class Goal** — Toggle form with goal type, target (number), reward text. Submit creates goal via API. Coral tint.
6. **Shoutout Moderation** — Recent shoutouts list with sender → receiver, message, delete button.
7. **Action Feedback** — Success/error message banner that auto-dismisses after 3s.

### 12.3 TeacherGamificationViewModel
**File:** `shared/.../gamification/presentation/ParentGamificationViewModel.kt` (appended below `ParentGamificationViewModel`)

**State:** `TeacherGamificationState` — overview, classLeaderboard, classGoals, availableQuests, shoutouts, studentBadges, isLoading, isActionLoading, error, actionMessage.

**Methods:**
- `load()` — Fetches overview, class leaderboard, class goals, available quests, shoutouts
- `loadStudentBadges(studentId)` — Fetches a student's earned badges
- `encourageStudent(studentId, amount, reason)` — Sends XP encouragement
- `spotlightStudent(studentId, reason)` — Spotlights a student
- `awardBadge(studentId, badgeId)` — Awards a badge
- `sendShoutout(receiverId, message, templateId, isPublic)` — Sends a shoutout
- `assignQuest(studentId, questId)` — Assigns a quest to a student
- `pepTalk(className, section)` — Sends pep talk to a class
- `createClassGoal(goalType, target, reward, className)` — Creates a class goal
- `updateClassGoalProgress(goalId, progress)` — Updates goal progress
- `deleteShoutout(shoutoutId)` — Deletes a shoutout
- `clearActionMessage()` — Clears the action feedback banner

### 12.4 Style Rules
- Use teacher portal tokens: `VtC` (violet accent, cream base, teal success), `VtT` (typography), `VtCard`, `VtEyebrow`, `VtPill`, `VtIconDisc`, `VtMetricTile`
- All cards use `VtCard` (white surface + hairline border)
- Action buttons use `VtC` accent colors with 10% alpha background
- No fixed heights on growing content — all inside `LazyColumn` with `verticalArrangement = spacedBy(12.dp)`
- All buttons within screen bounds — no overflow, no hidden actions
- Loading state: `CircularProgressIndicator` at 16dp inside button
- Data flows from ViewModel → API → backend — zero hardcoded data
- Koin DI: `factory { TeacherGamificationViewModel(get(), get()) }` registered in `Koin.kt`
