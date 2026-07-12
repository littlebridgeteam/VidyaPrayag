# People Tab — Full UI/UX Implementation Spec

**Source prototype:** `preview/people-tab-prototype.html`  
**Target files:** `StudentRosterScreenV2.kt` (students), staff roster screen (staff), teacher roster screen (teachers)  
**Date:** July 2026

---

## Table of Contents

1. [Screen-Level Layout](#1-screen-level-layout)
2. [Sub-Tab Pills](#2-sub-tab-pills)
3. [Students Tab — Screen Elements](#3-students-tab--screen-elements)
4. [Staff Tab — Screen Elements](#4-staff-tab--screen-elements)
5. [Teachers Tab — Screen Elements](#5-teachers-tab--screen-elements)
6. [Student Card — Full Spec](#6-student-card--full-spec)
7. [Staff Card — Full Spec](#7-staff-card--full-spec)
8. [Teacher Card — Full Spec](#8-teacher-card--full-spec)
9. [Shared UI Components](#9-shared-ui-components)
10. [Data Model Changes](#10-data-model-changes)
11. [Backend API Changes](#11-backend-api-changes)
12. [Color Token Reference](#12-color-token-reference)
13. [Typography Reference](#13-typography-reference)
14. [Spacing & Sizing](#14-spacing--sizing)
15. [Animation Spec](#15-animation-spec)
16. [Interaction & State](#16-interaction--state)
17. [Empty States](#17-empty-states)
18. [Implementation Checklist](#18-implementation-checklist)
19. [File References](#19-file-references)

---

## 1. Screen-Level Layout

The People tab is a full screen inside the phone frame (390×844px in prototype). Vertical stack, no horizontal scroll.

### Screen Structure (top → bottom)

```
┌─────────────────────────────────┐
│ Status Bar (system)             │  ← Not in app scope
├─────────────────────────────────┤
│ PEOPLE Header                   │  ← Eyebrow + Title
├─────────────────────────────────┤
│ Sub-Tab Pills                   │  ← Teachers | Students | Staff
├─────────────────────────────────┤
│ [Tab-specific content]          │
│  ┌ Link Banner (students only)  │
│  ┌ Sub-Tab Header (hidden)      │
│  ┌ Search Bar                   │
│  ┌ Filter Chips Row             │
│  ┌ Active Filter Chips          │
│  ┌ Sort + Bulk Row (hidden)     │
│  ┌ Scrollable Cards Container   │
│  ┌ Load More (teachers only)    │
├─────────────────────────────────┤
│ Bulk Action Bar (overlay)       │  ← Shows when bulk mode active
├─────────────────────────────────┤
│ Bottom Nav (5 tabs)             │  ← Home | People | Records | Comms | Settings
└─────────────────────────────────┘
```

### People Header

| Element | Spec | Compose |
|---------|------|---------|
| Eyebrow dot | 5px circle, `--violet` bg | `Box(5.dp, CircleShape, bg=VColors.violet)` |
| Eyebrow text | "PEOPLE", 11px bold, violet, letter-spacing 0.3px | `VLabel("PEOPLE", color=VColors.violet)` |
| Title | "People **Directory**", 22px extrabold ink, letter-spacing -0.5px. "Directory" is `--fw-r` (regular weight) + `--ink-2` color | `Text("People ", VTypography.h3.copy(FontWeight.ExtraBold)) + Text("Directory", VTypography.h3.copy(FontWeight.Normal, VColors.ink2))` |
| Padding | `4px 24px 2px` | `Modifier.padding(start=24.dp, end=24.dp, top=4.dp, bottom=2.dp)` |

---

## 2. Sub-Tab Pills

Horizontal row of 3 pills: **Teachers**, **Students**, **Staff**.

| State | Style | Compose |
|-------|-------|---------|
| Active | Gradient bg `linear-gradient(135deg,#7B61E5,#5B41D5)`, white text, bold, scale 1.0, shadow `0 2px 10px -2px rgba(91,65,213,.4)` | `VTopTabs(tabs=["Teachers","Students","Staff"], selected=activeTab, onSelect=...)` or custom |
| Inactive | Transparent bg, `--ink-3` text, semibold, scale 0.98 | Same component handles inactive state |
| Padding | `8px 16px` per pill | — |
| Font | 13px semibold | `VTypography.caption.copy(FontWeight.SemiBold)` |
| Radius | `--r-full` (9999px) | `RoundedCornerShape(50)` |
| Row padding | `2px 24px 4px` | — |
| Gap | 6px between pills | `Arrangement.spacedBy(6.dp)` |
| Overflow | Horizontal scroll, no scrollbar | `Row(horizontalScroll)` |

**Compose mapping:** Use `VTopTabs` from `VNavigation.kt` or custom pill row.

---

## 3. Students Tab — Screen Elements

### 3.1 Link Requests Banner (Students Only)

Shows when there are pending parent→child link requests. Tappable → navigates to link approval screen.

| Element | Spec |
|---------|------|
| Container | `--violet-soft` bg, `--r-md` (14px) radius, `12px 16px` padding, `1px` transparent border → violet on hover |
| Left section | Icon box (34px, `--r-sm` radius, gradient bg `#7B61E5→#5B41D5`, 16px white checkmark icon) + text column |
| Banner text | "3 pending link requests", 13px bold, `--violet` |
| Banner sub | "Tap to review parent→child approvals", 11px medium, `--violet-ink` at 70% opacity |
| Right | Chevron right icon, 16px, `--violet` stroke |
| Margin | `4px 24px 8px` |

**Compose:** `VActionCard(title="N pending link requests", subtitle="Tap to review parent→child approvals", icon=VIcons.Check, onClick=...)` with custom violet-soft styling.

**Data source:** Count of `parent_child_links` with `status = "pending"` or `status = "needs_review"` for the school. Already exists via `LinkRequestsViewModel`.

### 3.2 Sub-Tab Header (Hidden in Prototype)

The `.subtab-header` is `display:none` in the prototype — the Add/Import buttons are NOT visible. They exist in HTML but are hidden. The Add button is in the sub-tab pills row instead (via `.subtab-add-btn`).

**Note:** In the app, the Add Student / Import buttons should be placed in the header area. The prototype hides this row but the buttons are referenced via `data-dialog` attributes.

### 3.3 Search Bar

| Element | Spec |
|---------|------|
| Container | White bg, `--r-md` radius, `--shadow-1`, `1.5px` transparent border → violet on focus + `0 0 0 3px violet-soft` ring |
| Padding | `9px 14px` |
| Icon | Magnifier, 18px, `--ink-3` stroke |
| Input | 15px, `--ink` text, `--ink-3` placeholder |
| Placeholder | "Search students…" |
| Row padding | `2px 24px 4px` |

**Compose:** `VInput(value=search, onValueChange=..., placeholder="Search students…", leadingIcon=VIcons.Search)`

**Search matches:** name, roll number, class, section, admission number (studentCode), parent name

### 3.4 Filter Chips Row

Two filter chips, horizontally scrollable:

| Filter | Label (default) | Options |
|--------|----------------|---------|
| Class | "All Classes" | Class 6, 7, 8, 9, 10 |
| Section | "All Sections" | Section A, B, C |

**Chip style:**
- Default: `1.5px` `--line` border, white bg, 12px semibold `--ink-2`, `7px 14px` padding, `--r-full` radius
- Active: gradient bg `#7B61E5→#5B41D5`, white text, transparent border, shadow
- Hover: border → `--violet`
- Dropdown: white bg, `--r-md` radius, `--shadow-3`, `6px` padding, min-width 160px, max-height 240px, scrollable
- Options: 13px medium, `10px 12px` padding, `--r-sm` radius, selected = violet bold + filled radio circle

**Active filter chips:** Show below filter row as removable pills (violet-soft bg, violet text, 11px semibold, `4px 10px` padding, X icon). "Clear" link in coral.

**Compose:** Custom filter chip row with dropdown. Not a standard VComponent — needs custom implementation.

### 3.5 Sort + Bulk Row (Hidden in Prototype)

The `.sort-bulk-row` is `display:none` in the prototype — sort and bulk are merged into the filter row. However, the functionality exists:

**Sort options (students):**
- Name (A-Z) — default
- Roll Number
- Class

**Bulk toggle:** Checkbox icon + "Bulk" text. When active: gradient bg, white text.

**Note:** In the prototype, sort is accessed via a sort button in the filter row (right-aligned, `--surface-tint` bg). Bulk toggle is hidden but functional via state.

### 3.6 Cards Container

| Property | Value |
|----------|-------|
| Padding | `0 24px 16px` |
| Layout | Vertical column |
| Gap between cards | 10px |
| Scroll | Vertical, no scrollbar, momentum scroll |

### 3.7 Bulk Action Bar (Students)

Floating bar at bottom when bulk mode is active:

| Element | Spec |
|---------|------|
| Position | Absolute, `bottom: 90px`, `left: 16px`, `right: 16px` |
| Bg | `linear-gradient(135deg,#2A2520,#1A1614)` (dark) |
| Radius | `--r-lg` (18px) |
| Padding | `12px 16px` |
| Shadow | `0 8px 28px -6px rgba(0,0,0,.4), 0 2px 8px rgba(0,0,0,.2)` |
| Animation | `translateY(200%)` → `translateY(0)`, 300ms ease |
| Count text | "N selected", 13px bold, white |
| Actions | Graduate (gradient btn), Transfer (gradient btn), Cancel (transparent, white@60%) |

**Compose:** Custom `BottomBar` overlay with `AnimatedVisibility(enter=slideInVertically)`. Use `VButton(Variant.Primary, Size.Sm)` for actions.

---

## 4. Staff Tab — Screen Elements

### 4.1 Search Bar

Same as students but placeholder: "Search staff…"

**Search matches:** name, role, department, employee ID

### 4.2 Filter Chips

| Filter | Label (default) | Options |
|--------|----------------|---------|
| Department | "All Departments" | Administration, Finance, Transport, Maintenance, Library, Security |
| Role | "All Roles" | Accountant, Librarian, Driver, Receptionist, Custodian, Security Guard |

### 4.3 Sort Options (Staff)

- Name (A-Z) — default
- Department

### 4.4 No Bulk Mode

Staff tab has **no bulk toggle** in the prototype. No bulk bar, no selection checkboxes.

### 4.5 No Load More

Staff tab has no "Load More" button (all staff shown).

---

## 5. Teachers Tab — Screen Elements

### 5.1 Search Bar

Placeholder: "Search teachers…"

**Search matches:** name, role, subjects, grades

### 5.2 Filter Chips

| Filter | Label (default) | Options |
|--------|----------------|---------|
| Subject | "All Subjects" | Mathematics, Science, English, Social Studies, Hindi, Computer Science |
| Grade | "All Grades" | Grade 6, 7, 8, 9, 10 |

### 5.3 Sort Options (Teachers)

- Name (A-Z) — default
- Students (High→Low)
- Last Active

### 5.4 Bulk Mode (Teachers)

Teachers have bulk mode with bulk bar actions:
- **Assign Classes** (gradient btn)
- **Deactivate** (destructive btn)
- **Cancel** (transparent)

### 5.5 Load More

Teachers tab has a "Load More" button at the bottom of the cards container:

| Element | Spec |
|---------|------|
| Button | Full width, `12px` padding, `--r-md` radius, `1.5px` `--line` border, 14px semibold `--ink-2` |
| Hover | Border → violet, text → violet |
| Padding (container) | `8px 24px 16px` |

---

## 6. Student Card — Full Spec

### Card Container

| Property | CSS Value | Compose |
|----------|-----------|---------|
| Background | `--white` (#FFF) | `VColors.surfaceCard` |
| Border radius | `--r-md` (14px) | `RoundedCornerShape(14.dp)` |
| Padding | 16px | `VCard(padding=16.dp)` |
| Shadow (resting) | `--shadow-1`: `0 1px 2px rgba(26,22,20,.04), 0 1px 3px rgba(26,22,20,.06)` | `VCard(elevated=false)` default shadow |
| Shadow (hover) | `--shadow-2`: `0 2px 8px -2px rgba(26,22,20,.08), 0 1px 3px rgba(26,22,20,.04)` | — |
| Hover transform | `translateY(-1px)` | — |
| Selected (bulk) | `--violet-soft` bg, `1px` `--violet` border | `bg=VColors.violetSoft, border=VColors.violet` |
| Cursor | pointer | `VCard(onClick=onOpen)` |
| Transition | `250ms cubic-bezier(0.2,0,0,1)` | — |

### 6.1 Identity Header

**Layout:** `Row(alignItems=Top, spacedBy=12.dp)`

| Element | Size | Style | Data |
|---------|------|-------|------|
| Avatar | 48dp circle | Gradient bg by color tag (5 variants), white initials, `0 2px 8px -2px` colored shadow, inset top highlight | `student.fullName`, `student.profilePhotoUrl` |
| Name | 15px bold ink, letter-spacing -0.2px, single-line ellipsis | `Text(VTypography.bodySmall.copy(FontWeight.Bold, VColors.ink), maxLines=1, overflow=Ellipsis)` | `student.fullName` |
| Class-Section badge | Pill, 10px bold, violet-soft bg, violet text, `3px 10px` padding, letter-spacing 0.3px | `Box(RoundedCornerShape(50), bg=VColors.violetSoft, padding=3-10.dp)` + `Text(VTypography.label.copy(FontWeight.Bold, VColors.violet))` | `"Class ${student.className}-${student.section}"` |
| Roll # | 11px medium ink-3 | `Text(VTypography.caption, VColors.ink3)` | `"Roll #${student.rollNumber}"` |
| Admission # | 11px medium ink-3 | `Text(VTypography.caption, VColors.ink3)` | `"Admission #${student.studentCode}"` |
| Overflow ⋮ | 32dp, `--r-sm` radius, transparent bg → `--surface-tint` on hover, 18px `--ink-3` icon (3 dots) | `Box(32.dp, RoundedCornerShape(10.dp), clickable)` + `Icon(VIcons.More, 18.dp, VColors.ink3)` | — |

**Avatar gradient variants (exact hex):**

| Color Tag | Gradient | Shadow Color |
|-----------|----------|-------------|
| violet (default) | `#7B61E5 → #5B41D5` | `rgba(91,65,213,.35)` |
| coral | `#FF5C85 → #F82B60` | `rgba(248,43,96,.3)` |
| gold | `#FFD040 → #FCB400` | `rgba(252,180,0,.3)` |
| mint | `#4EE6A0 → #2DCE89` | `rgba(45,206,137,.3)` |
| sky | `#42CCFF → #18BFFF` | `rgba(24,191,255,.3)` |

All avatars have: `inset 0 1px 0 rgba(255,255,255,.15)` top highlight (except gold: `.2` opacity).

**Name row layout:** `Row(SpaceBetween, gap=8.dp)` → Name (weight=1f, ellipsis) + Class badge (flex-shrink=0)

**Roll row layout:** `Row(gap=12.dp, marginTop=4.dp)` → Roll # + Admission #

**Overflow menu items:**

| Item | Icon | Style |
|------|------|-------|
| Link to Parent | Person icon | Normal |
| Edit | Pencil icon | Normal |
| Promote | Transfer icon | Normal |
| Remove | Trash icon | **Danger** (coral text, coral-soft hover bg) |

**Overflow menu style:** White bg, `--r-md` radius, `--shadow-3`, `4px` padding, min-width 150px, absolute positioned below button, right-aligned.

### 6.2 Parent Info

**Layout:** `Row(gap=16.dp, marginTop=10.dp)`

| Item | Icon | Data | Style |
|------|------|------|-------|
| Parent name | Person (13px, `--ink-3` stroke, 2px width, no fill) | `student.parentName` or "—" | 11px medium `--ink-2` |
| Parent phone (masked) | Phone (13px, same icon style) | `student.parentPhone` masked → `+91 XXXXXXX234` | 11px medium `--ink-2` |

**Masking function:** `maskPhone(phone: String): String` — keep `+` prefix, replace all but last 4 digits with `X`.

### 6.3 Hairline Divider

- 1px height, `--line-soft` (#F0EAE0) bg
- Margin: 12px top and bottom
- **Compose:** `VDivider(modifier=Modifier.padding(vertical=12.dp))`

### 6.4 Priority Alerts

**Layout:** `Row(gap=14.dp, wrap)` — each alert is `Row(gap=5.dp)` with dot + text

| Alert | Dot Color | Hex | Halo Ring | Condition |
|-------|-----------|-----|-----------|-----------|
| Healthy | Green | `--mint` (#2DCE89) | `0 0 0 3px rgba(45,206,137,.18)` | active AND attendance ≥ 75% AND not new admission |
| Homework Due | Yellow | `--gold` (#FCB400) | `0 0 0 3px rgba(252,180,0,.18)` | homeworkPercent < 80% |
| Fees Pending | Orange | `#FF8800` | `0 0 0 3px rgba(255,136,0,.18)` | feesPending = true |
| Low Attendance | Red | `--coral` (#F82B60) | `0 0 0 3px rgba(248,43,96,.18)` | attendance 0.1%–74.9% |
| Parent Meeting | Blue | `--sky` (#18BFFF) | `0 0 0 3px rgba(24,191,255,.18)` | parentMeetingScheduled = true |
| New Admission | Violet | `--violet` (#5B41D5) | `0 0 0 3px rgba(91,65,213,.18)` | isNewAdmission = true |
| Inactive | Gray | `--ink-3` (#8A8078) | none | status ≠ "active" |

**Dot spec:** 8px circle, flex-shrink=0. Halo is box-shadow ring (3px spread at 18% opacity).

**Text spec:** 11px semibold, `--ink-2` (#5C544E)

**Compose:** `VStatusDot(color, size=8.dp, ring=true)` + `Text(VTypography.caption.copy(FontWeight.SemiBold, VColors.ink2))`

### 6.5 Micro Visualizations (Students Only)

**Layout:** `Row(gap=12.dp, marginTop=12.dp)` → two `Column(weight=1f)` side-by-side

Each metric:

```
┌─────────────────────┐
│ LABEL        VALUE  │  ← SpaceBetween row
│ ████████░░░░░░░░░░  │  ← Progress bar (6dp height)
└─────────────────────┘
```

| Metric | Label (uppercase 10px bold ink-3, letter-spacing 0.4px) | Value (13px bold ink) | Bar Color Logic | Data |
|--------|---------------------------------------------------------|----------------------|-----------------|------|
| Attendance | "ATTENDANCE" | `${attendance}%` or "—" | ≥85% → green, ≥75% → yellow, >0% → red, 0% → violet | `student.attendancePercent` |
| Homework | "HOMEWORK" | `${homework}%` or "—" | ≥80% → green, ≥50% → yellow, <50% → red | `student.homeworkPercent` |

**Bar spec:**
- Track: 6dp height, `--r-full` radius, `--surface-tint` (#F8F4EF) bg
- Fill: Same height, same radius, gradient bg by color, `width: N%`, 600ms width transition

**Bar gradient fills (exact):**

| Color | Gradient |
|-------|----------|
| Green | `linear-gradient(90deg, #4EE6A0, #2DCE89)` |
| Yellow | `linear-gradient(90deg, #FFD040, #FCB400)` |
| Orange | `linear-gradient(90deg, #FFAA00, #FF8800)` |
| Red | `linear-gradient(90deg, #FF5C85, #F82B60)` |
| Violet | `linear-gradient(90deg, #7B61E5, #5B41D5)` |
| Sky | `linear-gradient(90deg, #42CCFF, #18BFFF)` |

**Compose:** `VProgressBar(value=Float, tone=VBadgeTone, height=6.dp)` — maps green→Success, yellow→Warning, red→Danger, violet→Arctic.

### 6.6 Today Summary

**Container:** `--surface-tint` (#F8F4EF) bg, `--r-sm` (10px) radius, `10px 12px` padding, `marginTop=12dp`

**Header:** `Row(SpaceBetween)` → "TODAY" (11px bold uppercase ink-2, letter-spacing 0.4px) + chevron-right icon (14px, `--ink-3` stroke)

**Items:** `Column(gap=4.dp)` — each item is `Row(gap=6.dp)`:

| Element | Spec |
|---------|------|
| Dot | 5px circle, flex-shrink=0, color by type |
| Text | 12px medium `--ink-2` |

**Today item dot colors:**

| Color | Hex | Use Case |
|-------|-----|----------|
| Green | `--mint` (#2DCE89) | Present, All Homework Done, Checked In |
| Yellow | `--gold` (#FCB400) | Homework Due, Assignments Pending, Books Overdue |
| Red | `--coral` (#F82B60) | Absent, Not Checked In |
| Sky | `--sky` (#18BFFF) | Bus Route, Afternoon Route |

**⚠️ Bug in prototype:** `.se-today-item .dot.red` CSS class is missing. Only green, yellow, sky are defined. **Fix:** Add `.se-today-item .dot.red{background:var(--coral)}` to CSS.

**Data source:** `student.todayItems` — `List<TodayItemDto>` where `TodayItemDto(color: String, text: String)`

### 6.7 Quick Actions

**Layout:** `Row(gap=6.dp, marginTop=12.dp)` → 3 equal-width tiles

| Action | Icon (17px, `--violet` stroke, 2px width, no fill) | Label (10px semibold `--ink-2`) |
|--------|-----------------------------------------------------|--------------------------------|
| Profile | Person silhouette | "Profile" |
| Call | Phone handset | "Call" |
| Message | Chat bubble | "Message" |

**Tile spec:** `flex:1`, `Column(align=Center, gap=4.dp)`, `9px 4px` padding, `--r-sm` (10px) radius, `--surface-tint` bg, no border. Hover: bg → `--violet-soft`. 150ms transition.

**Compose:** Custom `QuickActionButton` composable — `Box(Modifier.weight(1f).clip(RoundedCornerShape(10.dp)).background(VColors.cream).clickable(onClick))` → `Column(align=Center, gap=4.dp)` → `Icon(17.dp, VColors.violet)` + `Text(10.dp, FontWeight.SemiBold, VColors.ink2)`

**Click behavior:** `event.stopPropagation()` — prevents card click. Each action needs its own callback: `onOpen()`, `onCallParent()`, `onMessage()`.

---

## 7. Staff Card — Full Spec

Uses the **same `.student-enriched` card container** as students. Same border radius, padding, shadow, hover, selected state.

### 7.1 Identity Header

Same layout as student card with these differences:

| Element | Student Card | Staff Card |
|---------|-------------|------------|
| Badge text | `"Class 8-A"` | `"Finance"` (department name) |
| Badge bg | `--violet-soft` | `--mint-soft` (#DCF5E8) |
| Badge text color | `--violet` | `--success` (#2D7A4A) |
| Subtitle line 1 | `"Roll #21"` | `"Accountant"` (role) |
| Subtitle line 2 | `"Admission #24105"` | `"EMP-001"` (employee ID) |

**Overflow menu items:**

| Item | Icon |
|------|------|
| Edit | Pencil |
| Assign Shift | Person + clock |
| Remove (danger) | Trash |

### 7.2 Contact Info

**Layout:** `Row(gap=16.dp, marginTop=10.dp)` — **three** items (vs two for students):

| Item | Icon (13px, `--ink-3` stroke) | Data | Notes |
|------|------|------|-------|
| Phone | Phone handset | `staff.phone` | **Not masked** (full phone shown) |
| Email | Envelope | `staff.email` or "—" | — |
| Shift | Clock | `staff.shift` | e.g. "9:00 AM – 5:00 PM" |

### 7.3 Priority Alerts

Same visual style as student alerts. Staff-specific alerts:

| Alert | Dot Color | Condition |
|-------|-----------|-----------|
| On Duty | Green | status == "active" AND checked in today |
| On Route | Green | Driver currently on bus route |
| Off Duty | Red | status != "active" OR not checked in |
| Since {year} | Violet | Tenure badge — always shown, `staff.joinedYear` |

### 7.4 No Micro Visualizations

Staff cards do **not** have the micro-viz progress bar section. The card goes directly from priority alerts → today summary.

### 7.5 Today Summary

Same visual structure as student card. Staff-specific items:

| Example Items | Dot Color |
|---------------|-----------|
| "Checked in 9:02 AM" | Green |
| "Fee Collection — 12 receipts" | Sky |
| "3 Books Overdue Return" | Yellow |
| "Bus B12 — Morning route done" | Green |
| "Afternoon route at 2:00 PM" | Sky |
| "Not checked in" | Red |
| "Leave request pending" | Yellow |
| "2 Pending Visitor Logs" | Yellow |

### 7.6 Quick Actions

Same as student card: Profile, Call, Message (3 tiles, identical styling).

### 7.7 No Bulk Mode

Staff cards have **no bulk selection mode**. No checkbox, no selected state, no bulk bar.

---

## 8. Teacher Card — Full Spec

The teacher card uses a **completely different design** — a "Bento Grid" style, not the `.student-enriched` style.

### Card Container

| Property | CSS Value | Compose |
|----------|-----------|---------|
| Background | `--white` | `VColors.surfaceCard` |
| Border radius | `--r-lg` (18px) | `RoundedCornerShape(18.dp)` |
| Border | `1px` `--line-soft` | `BorderStroke(1.dp, VColors.lineSoft)` |
| Shadow (resting) | `--shadow-1` | `VCard(elevated=false)` |
| Shadow (hover) | `0 8px 28px -6px rgba(91,65,213,.12), 0 2px 8px rgba(26,22,20,.06)` | — |
| Hover | Shadow + `translateY(-2px)` + border → `--violet-soft` | — |
| Selected (bulk) | `--violet-soft` bg, `--violet` border, accent strip height → 5px | — |
| Position | Relative, overflow hidden | — |

### 8.1 Top Accent Strip

- Absolute positioned, top:0, left:0, right:0, height: 3px (4px on hover, 5px when selected)
- Background: gradient from the teacher's first subject (e.g. Mathematics = `#7B61E5→#5B41D5`)
- 200ms height transition

### 8.2 Bento Header

**Layout:** `Row(align=Center, gap=10.dp, padding=14-14-12)`

| Element | Spec |
|---------|------|
| Avatar | 38dp circle (smaller than student/staff 48dp), 13px initials, same gradient variants |
| Name | 14px bold ink, same ellipsis |
| Crown badge | 16px gold crown icon, shown if `classTeacher == true`, `drop-shadow(0 1px 2px rgba(252,180,0,.3))` |
| Role · Experience | 11px medium ink-3, e.g. "Senior Teacher · 12 yrs" |
| Availability pill | Right-aligned, compact pill with dot + label |

**Availability pill variants:**

| State | Class | Bg Gradient | Text Color | Dot Color | Dot Animation |
|-------|-------|-------------|------------|-----------|---------------|
| Teaching | `avail-teaching` | `#DCF5E8→#B8EBCC` | `--success` (#2D7A4A) | `--success` | `pulseDot 2s infinite` (ring pulse) |
| On Break | `avail-break` | `#FFF4D1→#FFEAB8` | `--warning` (#B07500) | `--warning` | none |
| In Meeting | `avail-meeting` | `#E0F0FF→#C8E4FF` | `--sky` (#18BFFF) | `--sky` | none |
| On Leave | `avail-leave` | `#FFE4EC→#FFD0DA` | `--coral` (#F82B60) | `--coral` | none |

**Pill spec:** `4px 10px 4px 8px` padding, `--r-full` radius, 10.5px bold, letter-spacing 0.3px. Dot: 7px circle.

### 8.3 Bento Grid

2-column grid with 1px `--line-soft` dividers between cells. Each cell: white bg, `12px 14px` padding, `Column(gap=4.dp)`. Hover: bg → `--surface-tint`.

**Cell layout (6 cells total):**

```
┌────────────┬────────────┐
│ Classes    │ Students   │
├────────────┼────────────┤
│ Attendance │ Workload   │
├────────────┴────────────┤
│ Subjects & Grades (full)│
├─────────────────────────┤
│ Schedule + Actions (full)│
└─────────────────────────┘
```

### 8.4 Bento Cells — Detail

**Classes cell:**
- Label: "CLASSES" (9px bold uppercase ink-3, letter-spacing 0.5px, with menu icon)
- Stat row: Icon box (24px, `--r-sm` 7px radius, `--violet-soft` bg, `--violet` icon) + value column
- Value: 22px extrabold ink, letter-spacing -0.5px
- Sub: "active" (11px medium ink-3)

**Students cell:**
- Label: "STUDENTS"
- Icon box: `--sky-soft` bg, `--sky` icon
- Value: same style, sub: "enrolled"

**Attendance cell:**
- Label: "ATTENDANCE"
- Icon box bg/icon color: ≥95% → mint-soft/success, ≥85% → gold-soft/warning, <85% → coral-soft/coral
- Value: `${attendance}%` or "—", sub: "avg rate"

**Workload cell:**
- Label: "WORKLOAD"
- Bar: 6dp height, `--r-full` radius, `--surface-tint` track
- Fill: gradient by threshold: ≥80% → `#FF5C85→#F82B60`, ≥60% → `#FFD040→#FCB400`, >0% → `#4EE6A0→#2DCE89`, 0% → `--line`
- Row below: "Weekly capacity" (11px medium ink-3) + `${workload}%` (11px bold, same threshold color)

**Subjects & Grades cell (full width):**
- Label: "SUBJECTS & GRADES"
- Subject pills: Each subject has a custom icon + soft bg + ink color (see subject icon map below)
- Grade pills: `--surface-tint` bg, `--ink-2` text, `1px` `--line-soft` border, "Gr 8" format

**Subject icon/color map:**

| Subject | Icon | Gradient | Soft Bg | Ink Color |
|---------|------|----------|---------|-----------|
| Mathematics | Grid (4 squares) | `#7B61E5→#5B41D5` | `#EEE8FB` | `#5B41D5` |
| Science | Flask | `#4EE6A0→#2DCE89` | `#DCF5E8` | `#2D7A4A` |
| English | "E" letterform | `#42CCFF→#18BFFF` | `#E0F6FF` | `#0B7AB8` |
| Social Studies | Globe | `#FF5C85→#F82B60` | `#FFE4EC` | `#D11A4A` |
| Hindi | Lines | `#FFD040→#FCB400` | `#FFF4D1` | `#B07500` |
| Computer Science | Monitor | `#A78BFA→#7C3AED` | `#EDE9FE` | `#5B21B6` |

**Subject pill spec:** `3px 8px 3px 6px` padding, `--r-full` radius, 10px semibold, letter-spacing 0.2px, icon 11px. Hover: `scale(1.03)`.

**Grade pill spec:** `3px 8px` padding, `--r-full` radius, 10px bold, letter-spacing 0.3px.

**Schedule + Actions cell (full width):**
- Layout: `Row(SpaceBetween, align=Center)`
- Left: Calendar icon (13px) + schedule text (11px semibold ink-2), e.g. "3 classes today"
- Right: "Profile" button (gradient pill, 13px icon + 12px text) + overflow ⋮ menu

**Teacher overflow menu items:**

| Item | Icon |
|------|------|
| Assign Classes | Menu lines |
| Edit Details | Pencil |
| Reset Password | Lock |
| Deactivate (danger) | X |

**Profile button spec:** `7px 16px` padding, `--r-full` radius, 12px semibold, gradient bg `#7B61E5→#5B41D5`, white text, shadow `0 2px 10px -2px rgba(91,65,213,.35)`. Hover: darker gradient + `translateY(-1px)`.

---

## 9. Shared UI Components

### 9.1 Overflow Menu

Used by all three card types.

| Property | Spec |
|----------|------|
| Trigger button | 32×32dp, `--r-sm` radius, transparent bg → `--surface-tint` on hover, 18px `--ink-3` icon (3 vertical dots, 2.5px stroke) |
| Menu | White bg, `--r-md` radius, `--shadow-3`, `4px` padding, min-width 150px, absolute below trigger, right-aligned, `4px` margin-top |
| Item | `Row(gap=10.dp, padding=10-12.dp)`, `--r-sm` radius, 13px medium ink, icon 14px. Hover: `--surface-tint` bg. Danger: coral text, coral-soft hover bg. |
| Open/close | Toggle on trigger click, close on outside click |

**Compose:** `Box` + `DropdownMenu(expanded, onDismissRequest)` + `DropdownMenuItem` per item.

### 9.2 Bulk Checkbox

Used in student and teacher cards when bulk mode is active.

| Property | Spec |
|----------|------|
| Size | 22dp circle |
| Border | 2px `--line` (unselected) → 2px `--violet` (selected) |
| Selected bg | `--violet` |
| Checkmark | 12px, white, 3px stroke, opacity 0→1 on select |
| Animation | 200ms ease |

### 9.3 Bulk Action Bar

Used for students and teachers (not staff).

| Property | Spec |
|----------|------|
| Position | Overlay above bottom nav |
| Bg | `linear-gradient(135deg, #2A2520, #1A1614)` (dark warm) |
| Radius | `--r-lg` (18px) |
| Padding | `12px 16px` |
| Shadow | `0 8px 28px -6px rgba(0,0,0,.4), 0 2px 8px rgba(0,0,0,.2)` |
| Animation | Slide up from below, 300ms ease |
| Left | "N selected" (13px bold white) |
| Right | Action buttons (7px 14px, 12px font) |

**Bulk bar actions by tab:**

| Tab | Actions |
|-----|---------|
| Teachers | Assign Classes (gradient), Deactivate (destructive), Cancel (transparent) |
| Students | Graduate (gradient), Transfer (gradient), Cancel (transparent) |
| Staff | (none — no bulk mode) |

### 9.4 Add Dialogs

Three dialogs in the prototype:

**Add Teacher:**
- Fields: Full Name, Email or Phone, Initial Password (with hint: "Required when using email. OTP is sent automatically for phone.")
- Actions: Add Teacher (ghost btn), Cancel (outline btn)

**Add Student:**
- Fields: Full Name, Class, Section, Roll Number, Parent Phone (optional)
- Actions: Add Student (primary btn), Cancel (outline btn)

**Add Staff:**
- Fields: Full Name, Role, Department (optional), Phone (optional), Email (optional)
- Actions: Add Staff (primary btn), Cancel (outline btn)

**Import Students (CSV):**
- Hint: "Paste CSV with headers: full_name, class_name, section, roll_number"
- Field: Textarea (6 rows, monospace 13px) with pre-filled sample
- Actions: Import (primary btn), Cancel (outline btn)

**Dialog style:**
- Overlay: semi-transparent dark
- Card: white bg, `--r-lg` radius, top handle bar
- Title: bold, centered
- Fields: label (dialog-label) + input (dialog-input) with focus ring
- Actions: full-width buttons, stacked

**Compose:** `VConfirmDialog` pattern or custom dialog with `VInput` fields.

### 9.5 Empty State

Shown when no results match search/filters.

| Element | Spec |
|---------|------|
| Container | Centered, padding |
| Icon | Circle bg + smiley face SVG |
| Title | "No students found" / "No staff found" / "No teachers found" |
| Body | "Try adjusting your search or filters" |

**Compose:** `VEmptyState(title=..., body=..., icon=VIcons.Search)`

---

## 10. Data Model Changes

### 10.1 StudentDto — New Fields

Current `StudentDto` in `shared/.../feature/school/domain/model/StudentModels.kt`:

```kotlin
data class StudentDto(
    val id: String,
    val studentCode: String,        // ← already exists (used as Admission #)
    val fullName: String,
    val className: String,
    val section: String,
    val rollNumber: String,
    val parentPhone: String? = null,
    val profilePhotoUrl: String? = null,
    val attendancePercent: Float = 0f,
    val teacherCount: Int = 0,
    val parentCount: Int = 0,
    val isNewAdmission: Boolean = false,
    val status: String = "active"
)
```

**Fields to ADD:**

```kotlin
    val parentName: String? = null,              // Parent's full name for display
    val homeworkPercent: Float = 0f,             // Homework completion % (0-100)
    val feesPending: Boolean = false,            // Whether fees are overdue
    val parentMeetingScheduled: Boolean = false,  // Whether a parent meeting is upcoming
    val todayItems: List<TodayItemDto> = emptyList()  // Today's summary items
```

**New DTO:**

```kotlin
@Serializable
data class TodayItemDto(
    val color: String,   // "green" | "yellow" | "red" | "sky"
    val text: String     // e.g. "Present", "Math Homework Due"
)
```

**Alert derivation logic (client-side):**

```kotlin
val healthy = student.status == "active" && student.attendancePercent >= 75f && !student.isNewAdmission
val lowAttendance = student.attendancePercent in 0.1f..74.9f
val homeworkDue = student.homeworkPercent < 80f
val feesPending = student.feesPending
val parentMeeting = student.parentMeetingScheduled
val newAdmission = student.isNewAdmission
val inactive = student.status != "active"
```

### 10.2 StaffDto — New Fields

Current `StaffDto` in `shared/.../feature/school/domain/model/StaffModels.kt`:

```kotlin
data class StaffDto(
    val id: String,
    val fullName: String,
    val role: String,
    val department: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val photoUrl: String? = null
)
```

**Fields to ADD:**

```kotlin
    val employeeId: String? = null,              // e.g. "EMP-001"
    val shift: String? = null,                   // e.g. "9:00 AM – 5:00 PM"
    val status: String = "active",               // "active" | "inactive"
    val joinedYear: String? = null,              // e.g. "2019" (for tenure badge)
    val todayItems: List<TodayItemDto> = emptyList()  // Same DTO as student
```

### 10.3 Teacher Data — Already Exists

The prototype's teacher data uses fields that largely map to the existing `TeacherProfileDto`:

| Prototype Field | Existing DTO Field | Status |
|----------------|-------------------|--------|
| name | `name` | ✅ |
| role | `role` | ✅ |
| classTeacher | (not in DTO) | ❌ NEW — `isClassTeacher: Boolean` |
| grades | `assignments[].section` (partial) | ⚠️ Needs `grades: List<String>` |
| subjects | `assignments[].subject` | ✅ (via assignments list) |
| classes | `classCount` | ✅ |
| students | `studentCount` | ✅ |
| attendance | `attendancePercent` | ✅ |
| rating | (not in DTO) | ❌ NEW — `rating: Float` |
| experience | `experienceYears` | ✅ (as Int, needs formatting) |
| lastActive | (not in DTO) | ❌ NEW — `lastActive: String` |
| color | (derived from name hash) | ✅ (VAvatar handles) |
| availability | (not in DTO) | ❌ NEW — `availability: String` ("teaching"|"break"|"meeting"|"leave") |
| workload | (not in DTO) | ❌ NEW — `workload: Int` (0-100) |
| schedule | (not in DTO) | ❌ NEW — `schedule: String` (e.g. "3 classes today") |

**Teacher list response needs:** `isClassTeacher`, `grades`, `rating`, `lastActive`, `availability`, `workload`, `schedule` — these may need a new `TeacherListItemDto` or extending the existing list response.

---

## 11. Backend API Changes

### 11.1 Student List Endpoint (`GET /api/v1/school/students`)

**New fields to return per student:**

| Field | Source Table | Query |
|-------|-------------|-------|
| `parent_name` | `parent_child_links` (approved) → `app_users.name` | JOIN on `student_code`, status='approved' |
| `homework_percent` | `homework_submissions` (aggregate) | `COUNT(submitted) / COUNT(total) * 100` per student |
| `fees_pending` | `fees` table | `EXISTS WHERE status != 'paid' AND due_date < NOW()` |
| `parent_meeting_scheduled` | `calendar_events` or `parent_meetings` | `EXISTS WHERE student_id = ? AND date >= NOW()` |
| `today_items` | Multiple sources | Composite query: attendance (today), homework due (today), bus assignment, library overdue |

**Recommended approach:** Add a server-side aggregation query that joins these tables for the list response. For `today_items`, build the array server-side based on today's data.

**Alternative:** Create a separate `GET /api/v1/school/students/{id}/card` endpoint for lazy-loading enrichment data per card. Less initial payload but N+1 queries.

### 11.2 Staff List Endpoint (`GET /api/v1/school/staff`)

**New fields to return per staff:**

| Field | Source | Notes |
|-------|--------|-------|
| `employee_id` | `staff.employee_id` (may need column) | Check if column exists in `staff` table |
| `shift` | `staff.shift` (may need column) or `staff_shifts` table | New table or column |
| `status` | `staff.is_active` | Map boolean → "active"/"inactive" |
| `joined_year` | `staff.created_at` | Extract year from timestamp |
| `today_items` | Staff check-in table + task summaries | May need new `staff_check_ins` table |

**New tables likely needed:**
- `staff_check_ins` — track daily check-in/check-out times
- `staff_shifts` — shift definitions per staff member

### 11.3 Teacher List Endpoint

**New fields to return per teacher:**

| Field | Source | Notes |
|-------|--------|-------|
| `is_class_teacher` | `teacher_subject_assignments.is_class_teacher` | Already exists in DB |
| `grades` | `teacher_subject_assignments` aggregated | `DISTINCT class_name` from assignments |
| `rating` | May need new `teacher_ratings` table or derive from feedback | New |
| `last_active` | `app_users.last_login_at` | Check if column exists |
| `availability` | Derived from current schedule/attendance | Real-time or cached |
| `workload` | Calculated from assignments + timetable | Server-side computation |
| `schedule` | From timetable for today | e.g. "3 classes today" |

### 11.4 Link Requests Count

**Existing:** `GET /api/v1/school/link-requests?status=pending` already returns pending requests. The banner just needs the count. Could add a `count` field to the response or use a lightweight `GET /api/v1/school/link-requests/count` endpoint.

---

## 12. Color Token Reference

### CSS Variables → VColors Mapping

| CSS Variable | Hex Value | VColors Token | Notes |
|-------------|-----------|---------------|-------|
| `--cream` | #FBF8F4 | `VColors.cream` | Screen bg |
| `--cream-deep` | #F5F0E8 | `VColors.creamDeep` | — |
| `--white` | #FFF | `VColors.surfaceCard` | Card bg |
| `--surface` | #FBF8F4 | `VColors.surface` | — |
| `--surface-tint` | #F8F4EF | `VColors.surfaceTint` | Today section bg, viz bar track, action tile bg |
| `--surface-warm` | #FFF6EE | `VColors.surfaceWarm` | — |
| `--ink` | #1A1614 | `VColors.ink` | Primary text |
| `--ink-2` | #5C544E | `VColors.ink2` | Secondary text |
| `--ink-3` | #8A8078 | `VColors.ink3` | Tertiary text, labels |
| `--line` | #E8E0D6 | `VColors.line` | Borders |
| `--line-soft` | #F0EAE0 | `VColors.lineSoft` | Hairline divider |
| `--violet` | #5B41D5 | `VColors.violet` | Primary accent |
| `--violet-hover` | #4A30C4 | `VColors.violetHover` | — |
| `--violet-soft` | #EEE8FB | `VColors.violetSoft` | Class badge bg, action hover |
| `--violet-ink` | #16006E | `VColors.violetInk` | Banner sub text |
| `--coral` | #F82B60 | `VColors.coral` | Red alerts, danger |
| `--coral-soft` | #FFE4EC | `VColors.coralSoft` | Danger hover bg |
| `--gold` | #FCB400 | `VColors.gold` | Yellow alerts |
| `--gold-soft` | #FFF4D1 | `VColors.goldSoft` | — |
| `--sky` | #18BFFF | `VColors.sky` | Blue alerts |
| `--sky-soft` | #E0F6FF | `VColors.skySoft` | — |
| `--mint` | #2DCE89 | `VColors.mint` | Green alerts |
| `--mint-soft` | #DCF5E8 | `VColors.mintSoft` | Dept badge bg (staff) |
| `--success` | #2D7A4A | `VColors.success` | Dept badge text (staff) |
| `--error` | #BA1A1A | `VColors.error` | — |
| `--error-soft` | #FFDAD6 | `VColors.errorSoft` | — |
| `--warning` | #B07500 | `VColors.warning` | — |
| `--warning-soft` | #FFF4D1 | `VColors.warningSoft` | — |
| `#FF8800` | #FF8800 | **NO TOKEN** | ⚠️ Add `VColors.orange` for orange alert dots |

### Shadow Tokens

| Token | Value |
|-------|-------|
| `--shadow-1` | `0 1px 2px rgba(26,22,20,.04), 0 1px 3px rgba(26,22,20,.06)` |
| `--shadow-2` | `0 2px 8px -2px rgba(26,22,20,.08), 0 1px 3px rgba(26,22,20,.04)` |
| `--shadow-3` | `0 8px 24px -6px rgba(26,22,20,.10), 0 2px 8px rgba(26,22,20,.04)` |

---

## 13. Typography Reference

| Size | Weight | CSS Vars | Usage | Compose |
|------|--------|----------|-------|---------|
| 22px | 800 (extrabold) | `--fw-eb` | Page title | `VTypography.h3.copy(FontWeight.ExtraBold)` |
| 22px | 400 (regular) | `--fw-r` | Page title light part | `VTypography.h3.copy(FontWeight.Normal)` |
| 15px | 700 (bold) | `--fw-b` | Card name | `VTypography.bodySmall.copy(FontWeight.Bold)` |
| 15px | 600 (semibold) | `--fw-s` | Card name (alt) | `VTypography.bodySmall.copy(FontWeight.SemiBold)` |
| 15px | 500 (medium) | `--fw-m` | Search input | `VTypography.body` |
| 14px | 600 (semibold) | `--fw-s` | Teacher name | `VTypography.bodySmall.copy(FontWeight.SemiBold)` |
| 13px | 700 (bold) | `--fw-b` | Metric values, bulk count | `VTypography.bodySmall.copy(FontWeight.Bold)` |
| 13px | 600 (semibold) | `--fw-s` | Sort btn, filter chips, overflow items | `VTypography.caption.copy(FontWeight.SemiBold)` |
| 13px | 500 (medium) | `--fw-m` | Sort options, filter options | `VTypography.caption` |
| 12px | 600 (semibold) | `--fw-s` | Today items, schedule | `VTypography.caption.copy(FontWeight.SemiBold)` |
| 12px | 500 (medium) | `--fw-m` | Card subtitle | `VTypography.caption` |
| 11px | 600 (semibold) | `--fw-s` | Alert labels, parent info | `VTypography.caption.copy(FontWeight.SemiBold)` |
| 11px | 500 (medium) | `--fw-m` | Roll/Adm, contact info | `VTypography.caption.copy(VColors.ink3)` |
| 11px | 700 (bold) | `--fw-b` | Today header, eyebrow | `VTypography.label` |
| 10.5px | 700 (bold) | `--fw-b` | Availability pill | `VTypography.label.copy(FontWeight.Bold)` |
| 10px | 700 (bold) | `--fw-b` | Class badge, metric labels, action labels | `VTypography.label` |
| 10px | 600 (semibold) | `--fw-s` | Subject pills, grade pills | `VTypography.label.copy(FontWeight.SemiBold)` |
| 9px | 700 (bold) | `--fw-b` | Bento cell labels | `VTypography.label.copy(fontSize=9.sp)` |

---

## 14. Spacing & Sizing

### Spacing Tokens

| Token | Value | Usage |
|-------|-------|-------|
| `--s-xs` | 4px | Small gaps, eyebrow padding |
| `--s-sm` | 8px | Card gaps, filter row gaps |
| `--s-md` | 16px | Card padding, standard gap |
| `--s-lg` | 24px | Horizontal screen padding |
| `--s-xl` | 32px | Large gaps |

### Radius Tokens

| Token | Value | Usage |
|-------|-------|-------|
| `--r-sm` | 10px | Action tiles, overflow btn, bento stat icons |
| `--r-md` | 14px | Student/staff cards, search input, filter dropdowns |
| `--r-lg` | 18px | Teacher cards, bulk bar, dialog card |
| `--r-xl` | 24px | (unused in people tab) |
| `--r-full` | 9999px | Pills, badges, avatars, progress bars |

### Card Container Spacing

| Property | Value |
|----------|-------|
| Cards container horizontal padding | 24px (`--s-lg`) |
| Cards container bottom padding | 16px (`--s-md`) |
| Gap between cards | 10px |

---

## 15. Animation Spec

### Card Entrance

| Property | Value |
|----------|-------|
| Animation | `fadeIn` (opacity 0→1 + translateY 8px→0) |
| Duration | 300ms |
| Easing | `cubic-bezier(0.2, 0, 0, 1)` |
| Stagger | 50ms per card, up to 7 cards |
| Selector | `.student-enriched:nth-child(N)` and `.teacher-card:nth-child(N)` |

### Progress Bars

| Property | Value |
|----------|-------|
| Property | `width` |
| Duration | 600ms |
| Easing | `cubic-bezier(0.2, 0, 0, 1)` |

### Card Hover

| Property | Value |
|----------|-------|
| Duration | 250ms |
| Easing | `cubic-bezier(0.2, 0, 0, 1)` |
| Transform | `translateY(-1px)` (student/staff), `translateY(-2px)` (teacher) |
| Shadow | `--shadow-1` → `--shadow-2` (student/staff), custom (teacher) |

### Availability Dot Pulse (Teachers)

| Property | Value |
|----------|-------|
| Animation | `pulseDot` — `box-shadow: 0 0 0 0 rgba(45,206,137,.5)` → `0 0 0 5px rgba(45,206,137,0)` |
| Duration | 2s infinite |
| Only on | `avail-teaching` state |

### Bulk Bar Slide

| Property | Value |
|----------|-------|
| Transform | `translateY(200%)` → `translateY(0)` |
| Duration | 300ms |
| Easing | `cubic-bezier(0.2, 0, 0, 1)` |

### Quick Action Press

| Property | Value |
|----------|-------|
| Bg transition | `--surface-tint` → `--violet-soft` |
| Duration | 150ms |

### Accent Strip (Teachers)

| Property | Value |
|----------|-------|
| Height transition | 3px → 4px (hover), 3px → 5px (selected) |
| Duration | 200ms |

---

## 16. Interaction & State

### 16.1 Card Click

- Entire card is tappable → opens profile screen
- Student: `VCard(onClick = onOpen)`
- Staff: `VCard(onClick = onOpen)`
- Teacher: `VCard(onClick = onOpen)`

### 16.2 Quick Action Buttons

- Each button stops propagation (prevents card click)
- Student actions: Profile → `onOpen()`, Call → `onCallParent(phone)`, Message → `onMessage(id)`
- Staff actions: Profile → `onOpen()`, Call → `onCall(phone)`, Message → `onMessage(id)`
- Teacher actions: Profile → `onOpen()` (gradient button, not tile style)

### 16.3 Overflow Menu

- Opens on ⋮ tap
- Dismisses on outside click or item tap
- Student items: Link to Parent, Edit, Promote, Remove (danger)
- Staff items: Edit, Assign Shift, Remove (danger)
- Teacher items: Assign Classes, Edit Details, Reset Password, Deactivate (danger)
- Remove/Deactivate triggers `VConfirmDialog` before action (RA-21 pattern)

### 16.4 Bulk Mode (Students & Teachers Only)

| State | Behavior |
|-------|----------|
| Enter bulk | Checkbox appears at card top-left, quick actions hidden, overflow hidden |
| Card tap in bulk | Toggles selection (not navigation) |
| Selected card | `--violet-soft` bg + `--violet` border |
| Bulk bar | Slides up from bottom showing count + actions |
| Exit bulk | Clear selection, hide bulk bar, restore normal card |

### 16.5 Search

| Tab | Search Matches |
|-----|---------------|
| Students | name, roll, class, section, admission #, parent name |
| Staff | name, role, department, employee ID |
| Teachers | name, role, subjects, grades |

### 16.6 Filter Logic

| Tab | Filters |
|-----|---------|
| Students | Class (multi-select), Section (multi-select) |
| Staff | Department (multi-select), Role (multi-select) |
| Teachers | Subject (multi-select), Grade (multi-select) |

### 16.7 Sort Logic

| Tab | Sort Options |
|-----|-------------|
| Students | Name (A-Z), Roll Number, Class |
| Staff | Name (A-Z), Department |
| Teachers | Name (A-Z), Students (High→Low), Last Active |

---

## 17. Empty States

Shown when filtered list is empty.

| Tab | Title | Body |
|-----|-------|------|
| Students | "No students found" | "Try adjusting your search or filters" |
| Staff | "No staff found" | "Try adjusting your search or filters" |
| Teachers | "No teachers found" | "Try adjusting your search or filters" |

**Visual:** Centered icon-in-circle + title + body.

**Compose:** `VEmptyState(title=..., body=..., icon=VIcons.Search)`

---

## 18. Implementation Checklist

### Phase 1: Data Layer

- [ ] Add `TodayItemDto` to `StudentModels.kt`
- [ ] Add `parentName`, `homeworkPercent`, `feesPending`, `parentMeetingScheduled`, `todayItems` to `StudentDto`
- [ ] Add `employeeId`, `shift`, `status`, `joinedYear`, `todayItems` to `StaffDto`
- [ ] Add `isClassTeacher`, `grades`, `rating`, `lastActive`, `availability`, `workload`, `schedule` to teacher list DTO
- [ ] Add `VColors.orange` token (or decide to use `coral` for orange alerts)
- [ ] Update server student list endpoint to return new fields (joins + aggregation)
- [ ] Update server staff list endpoint to return new fields
- [ ] Update server teacher list endpoint to return new fields
- [ ] Add `staff_check_ins` table (if not exists) for today items
- [ ] Add link requests count endpoint or field

### Phase 2: UI Components

- [ ] Create `PriorityAlert(dotColor, label)` composable
- [ ] Create `QuickActionButton(icon, label, onClick)` composable
- [ ] Create `TodayItem(dotColor, text)` composable
- [ ] Create `TodaySummary(items: List<TodayItemDto>)` composable
- [ ] Create `maskPhone(phone: String): String` utility function
- [ ] Create `FilterChip(label, options, selected, onSelect)` composable (if not exists)
- [ ] Create `BulkActionBar(count, actions, onCancel)` composable
- [ ] Create `LinkRequestsBanner(count, onClick)` composable

### Phase 3: Student Card

- [ ] Rewrite `StudentCard` in `StudentRosterScreenV2.kt` with 6-section layout
- [ ] Wire `onCallParent` and `onMessage` callbacks (new params)
- [ ] Update `StudentRosterScreenV2` call site to pass new callbacks
- [ ] Add homework % to micro-viz section
- [ ] Add today summary section
- [ ] Add priority alerts with derived conditions
- [ ] Add parent info row with masked phone
- [ ] Add admission number to identity header
- [ ] Test with students having various alert combinations

### Phase 4: Staff Card

- [ ] Identify staff roster screen file
- [ ] Rewrite staff card with 5-section layout (no micro-viz)
- [ ] Wire `onCall` and `onMessage` callbacks
- [ ] Add employee ID, shift, contact info row
- [ ] Add priority alerts (On Duty, On Route, Off Duty, Since year)
- [ ] Add today summary section
- [ ] Test with staff having various statuses

### Phase 5: Teacher Card

- [ ] Identify teacher roster screen file
- [ ] Rewrite teacher card with bento grid layout
- [ ] Add top accent strip with subject gradient
- [ ] Add availability pill (4 states with pulse animation)
- [ ] Add bento cells: Classes, Students, Attendance, Workload
- [ ] Add subjects & grades cell with subject icon map
- [ ] Add schedule + profile button + overflow cell
- [ ] Add crown badge for class teachers
- [ ] Wire bulk mode for teachers
- [ ] Test with teachers having various availability states

### Phase 6: Screen-Level

- [ ] Add People header (eyebrow + title)
- [ ] Add sub-tab pills (Teachers, Students, Staff)
- [ ] Add search bar per tab
- [ ] Add filter chips per tab
- [ ] Add sort dropdown per tab
- [ ] Add link requests banner (students tab)
- [ ] Add bulk toggle + bulk bar (students & teachers tabs)
- [ ] Add load more button (teachers tab)
- [ ] Add empty states for all tabs
- [ ] Add add/import dialogs

### Phase 7: Polish

- [ ] Verify staggered entrance animation works for all card types
- [ ] Verify bulk mode selection works for student + teacher cards
- [ ] Verify search includes all new fields
- [ ] Verify filter dropdowns work with multi-select
- [ ] Verify overflow menu items match prototype per card type
- [ ] Verify `VConfirmDialog` gates all destructive actions
- [ ] Verify availability dot pulse animation (teachers)
- [ ] Verify accent strip height transitions (teachers)
- [ ] Verify progress bar fill animations (students + teachers)
- [ ] Fix prototype bug: add `.se-today-item .dot.red` CSS class
- [ ] Test on small screen (320dp) for overflow/wrapping
- [ ] Verify dark/midnight theme compatibility

---

## 19. File References

| File | Path | Role |
|------|------|------|
| Prototype | `preview/people-tab-prototype.html` | Visual reference (full HTML+CSS+JS) |
| Student screen | `composeApp/.../ui/v2/screens/school/StudentRosterScreenV2.kt` | Student card implementation |
| Student models | `shared/.../feature/school/domain/model/StudentModels.kt` | StudentDto + related DTOs |
| Staff models | `shared/.../feature/school/domain/model/StaffModels.kt` | StaffDto + related DTOs |
| Staff repo | `shared/.../feature/admin/domain/repository/StaffRepository.kt` | Staff data access interface |
| Staff API | `shared/.../feature/admin/data/remote/StaffApi.kt` | Staff HTTP endpoints |
| Staff VM | `shared/.../feature/admin/presentation/StaffViewModel.kt` | Staff state management |
| Teacher models | `shared/.../feature/school/domain/model/StudentModels.kt` (lines 136-178) | TeacherProfileDto |
| VColors | `composeApp/.../ui/tokens/VColors.kt` | Color tokens |
| VAtoms | `composeApp/.../ui/v2/components/VAtoms.kt` | VDivider, VStatusDot, VLabel |
| VProgress | `composeApp/.../ui/v2/components/VProgress.kt` | VProgressBar, VProgressRing |
| VAvatar | `composeApp/.../ui/v2/components/VAvatar.kt` | Avatar component |
| VCard | `composeApp/.../ui/v2/components/VCard.kt` | Card container |
| VBadge | `composeApp/.../ui/v2/components/VBadge.kt` | VBadgeTone enum |
| VButton | `composeApp/.../ui/v2/components/VButton.kt` | Button component |
| VIcons | `composeApp/.../ui/v2/components/VIcons.kt` | Icon set |
| VNavigation | `composeApp/.../ui/v2/components/VNavigation.kt` | VTopTabs, VBottomNav2 |
| VStructure | `composeApp/.../ui/v2/components/VStructure.kt` | VScreenScaffold, VEmptyState, VConfirmDialog |
| VInput | `composeApp/.../ui/v2/components/VInput.kt` | Search/input field |
| Link requests VM | `shared/.../feature/admin/presentation/LinkRequestsViewModel.kt` | Link approval queue |
| Parent link routing | `server/.../feature/parent/ParentLinkRouting.kt` | Parent→child link API |
| DB tables | `server/.../db/Tables.kt` | All Exposed table definitions |
