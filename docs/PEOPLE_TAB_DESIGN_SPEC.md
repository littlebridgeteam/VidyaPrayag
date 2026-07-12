# People Tab — Production Design Specification

**Source of truth:** `preview/people-tab-prototype.html`
**Spec date:** July 2026
**Status:** Final — supersedes `PEOPLE_TAB_ENRICHED_CARD_SPEC.md`

---

## Table of Contents

1. [Executive Summary](#1-executive-summary)
2. [Information Architecture](#2-information-architecture)
3. [Screen Overview](#3-screen-overview)
4. [Component Inventory](#4-component-inventory)
5. [Card Specifications](#5-card-specifications)
6. [Visual Wireframes](#6-visual-wireframes)
7. [Design Tokens](#7-design-tokens)
8. [Interaction Specifications](#8-interaction-specifications)
9. [State Management](#9-state-management)
10. [Accessibility Requirements](#10-accessibility-requirements)
11. [Engineering Notes](#11-engineering-notes)
12. [QA Checklist](#12-qa-checklist)
13. [Future Scalability Notes](#13-future-scalability-notes)

---

## 1. Executive Summary

The People Tab is the school admin's directory hub — a single screen with three sub-tabs (Teachers, Students, Staff) providing searchable, filterable, sortable card-based views of every person in the school.

**Two card paradigms:**
- **Teachers** — "Bento Grid" card (WWDC-inspired, 2-column stat grid with subject-colored accent strip)
- **Students/Staff** — "Enriched Vertical" card (6-section vertical stack with progress bars and today summary)

**Design language:** Warm cream backgrounds, warm ink text, violet primary accent, semantic colors (mint/gold/coral/sky) for status. Mobile-first, 390×844px phone frame, bottom-sheet dialogs, floating bulk action bar, 5-tab bottom navigation.

**Key UX decisions:**

| Decision | Rationale |
|----------|-----------|
| Three sub-tabs | Different data shapes per role; mixing creates visual inconsistency |
| Bento grid for teachers | Richer quantitative data benefits from grid layout |
| Enriched vertical for students/staff | Linear scanning faster for identity + status data |
| Priority alerts with colored dots + halos | Instant visual triage without reading |
| Today summary as tinted sub-card | Separates live status from profile data |
| Quick actions as bottom tiles | Reduces taps for common actions |
| Bulk mode only for students & teachers | Staff count small; bulk ops are student/teacher-specific |
| Bottom-sheet dialogs | Mobile-native, thumb-reachable |
| Hidden subtab-header row | Add/Import buttons in HTML but `display:none` |

---

## 2. Information Architecture

```
People Tab (Screen)
├── People Header (eyebrow + title)
├── Sub-Tab Pills [Teachers | Students | Staff]
├── Tab Content (one active)
│   ├── Teachers Tab
│   │   ├── Search Bar
│   │   ├── Filter Chips [Subject | Grade]
│   │   ├── Active Filter Chips (conditional)
│   │   ├── Sort + Bulk Row (hidden CSS, functional JS)
│   │   ├── Scrollable Cards → Teacher Card × N (Bento Grid)
│   │   ├── Load More Button
│   │   └── Bulk Action Bar (overlay, conditional)
│   ├── Students Tab
│   │   ├── Link Requests Banner (conditional)
│   │   ├── Search Bar
│   │   ├── Filter Chips [Class | Section]
│   │   ├── Active Filter Chips (conditional)
│   │   ├── Sort + Bulk Row (hidden CSS, functional JS)
│   │   ├── Scrollable Cards → Student Card × N (Enriched Vertical)
│   │   └── Bulk Action Bar (overlay, conditional)
│   └── Staff Tab
│       ├── Search Bar
│       ├── Filter Chips [Department | Role]
│       ├── Active Filter Chips (conditional)
│       ├── Sort + Bulk Row (hidden CSS, functional JS)
│       └── Scrollable Cards → Staff Card × N (Enriched Vertical, no micro-viz)
├── Bottom Navigation [Home | People | Records | Comms | Settings]
└── Dialogs (overlay, conditional)
    ├── Add Teacher | Add Student | Add Staff | Import Students
```

**Hierarchy:** Page → Tab → List → Card → Detail (5 levels). Card is the primary interaction surface. Identity first, status second, metrics third, live data fourth, actions last.

---

## 3. Screen Overview

### 3.1 Phone Frame

| Property | Prototype | Compose |
|----------|-----------|---------|
| Frame | 390×844px, black bezel, 50px radius | `VScreenScaffold` max-440dp |
| Screen bg | `--cream` (#FBF8F4) | `VTheme.colors.background` |
| Dynamic island | 124×34px black pill | System (out of scope) |
| Home indicator | 134×5px bar | System (out of scope) |

### 3.2 People Header

| Element | Spec |
|---------|------|
| Padding | `4px 24px 2px` |
| Eyebrow | `Row(gap=6.dp)` — 5dp violet dot + "PEOPLE" (11px bold, violet, letter-spacing 0.3px) |
| Title | "People Directory" — 22px, "People" is 800 weight + `--ink`, "Directory" is 400 weight + `--ink-2`, letter-spacing -0.5px |

### 3.3 Sub-Tab Pills

| State | Bg | Text | Weight | Scale | Shadow |
|-------|----|------|--------|-------|--------|
| Active | `linear-gradient(135deg,#7B61E5,#5B41D5)` | #FFF | bold | 1.0 | `0 2px 10px -2px rgba(91,65,213,.4)` |
| Inactive | transparent | `--ink-3` | semibold | 0.98 | none |

Padding: `8px 16px` per pill. Font: 13px. Radius: full. Row padding: `2px 24px 4px`. Gap: 6px. Transition: 220ms ease. Horizontal scroll, hidden scrollbar.

### 3.4 Search Bar

White bg, `--r-md` radius, `--shadow-1`, `1.5px` transparent border → violet on focus + 3px violet-soft ring. Padding `9px 14px`. Icon: 18px magnifier, `--ink-3`. Input: 15px `--ink`, placeholder `--ink-3`. Row padding: `2px 24px 4px`.

### 3.5 Filter Chips

**Chip states:** Default (white bg, `1.5px --line` border, 12px semibold `--ink-2`), Active (gradient bg, white text, transparent border, shadow), Hover (border → violet). Padding `7px 14px`. Radius full. Gap 6px. Horizontal scroll.

**Dropdown:** White, `--r-md` radius, `--shadow-3`, 6px padding, min-width 160px, max-height 240px scrollable. Options: 13px medium, `10px 12px` padding, `--r-sm` radius, selected = violet bold + filled radio circle.

**Chip label logic:** 0 selected → "All {Type}", 1 → "1 {Type-singular}", 2+ → "N {Type-plural}"

**Active filter chips:** `--violet-soft` bg, `--violet` text, 11px semibold, `4px 10px` padding, X icon. Hover → coral. "Clear all" link in coral.

### 3.6 Sort + Bulk Row

`display:none` in CSS but functional via JS. Sort button: `--surface-tint` bg, 12px semibold, right-aligned. Sort menu: white, `--r-md`, `--shadow-3`, options 13px medium, selected = violet bold. Bulk toggle: checkbox icon + "Bulk", active = gradient bg.

### 3.7 Cards Container

Padding `0 24px 16px`. Vertical flex, 10px gap. Scroll via parent `.tab-scroll` (flex:1, overflow-y:auto, hidden scrollbar, padding-bottom:100px).

### 3.8 Load More (Teachers Only)

Full width, 12px padding, `--r-md` radius, `1.5px --line` border, 14px semibold `--ink-2`. Hover: border → violet, text → violet. Container padding: `8px 24px 16px`.

### 3.9 Bulk Action Bar

Absolute, `bottom:90px, left:16px, right:16px`. Dark bg `linear-gradient(135deg,#2A2520,#1A1614)`. `--r-lg` radius. `12px 16px` padding. Slide up animation 300ms. Count: "N selected" 13px bold white. Actions: 7px 14px padding, 12px font.

**Per-tab actions:** Teachers → Assign Classes (gradient) + Deactivate (destructive) + Cancel. Students → Graduate (gradient) + Transfer (gradient) + Cancel. Staff → none.

### 3.10 Bottom Navigation

5 items: Home, People (active), Records, Comms, Settings. Active: violet-soft bg icon, violet text/icon, extrabold label. Inactive: transparent, `--ink-3`, medium label. Icon: 22px, 32×32dp container. Label: 11px. Padding: `8px 4px 16px`. Top border: `0 -1px 0 --line-soft`.

### 3.11 Dialogs (Bottom Sheet)

Overlay: `rgba(26,22,20,.45)`, z-200, flex bottom-aligned. Card: white, `--r-xl` top radius, 24px padding, max-height 80% scrollable. Slide up 300ms. Handle: 40×4dp, `--line` bg. Title: 18px bold. Fields: 12px semibold label + 15px input with `1.5px --line` border → violet on focus. Actions: full-width stacked buttons.

### 3.12 Empty State

56dp icon circle (`--surface-tint` bg, 24px `--ink-3` smiley icon), 16px bold title, 13px medium `--ink-3` body (6px margin, 1.5 line-height). Padding: `32px 24px`, centered.

### 3.13 Link Requests Banner (Students Only)

`--violet-soft` bg, `--r-md` radius, `12px 16px` padding. Icon box: 34dp, gradient bg, 16px white checkmark. Title: "N pending link requests" 13px bold violet. Sub: "Tap to review parent→child approvals" 11px medium `--violet-ink`@70%. Arrow: 16px violet chevron. Press: scale 0.98, 150ms.

---

## 4. Component Inventory

### Screen-Level

| Component | Variants | States |
|-----------|----------|--------|
| People Header | — | — |
| Sub-Tab Pill | Active, Inactive | Hover, Focus |
| Search Input | — | Default, Focus, Typing |
| Filter Chip | Default, Active | Hover, Dropdown Open |
| Filter Dropdown | Open, Closed | — |
| Filter Option | Selected, Unselected | Hover |
| Active Filter Chip | — | Hover (→coral) |
| Sort Button | — | Hover, Menu Open |
| Sort Menu/Option | Selected, Unselected | Hover |
| Bulk Toggle | Active, Inactive | Hover |
| Load More Button | — | Hover, Pressed |
| Bulk Action Bar | Active, Hidden | — |
| Bottom Nav Item | Active, Inactive | Hover, Pressed |
| Dialog Overlay/Card | Active, Inactive | Sliding, Shown |
| Dialog Field | — | Default, Focus, Error |
| Empty State | — | — |
| Link Requests Banner | — | Hover, Pressed |

### Card-Level

| Component | Variants | Used In |
|-----------|----------|---------|
| Teacher Card | Default, Selected, Bulk | Teachers tab |
| Student Card | Default, Selected, Bulk | Students tab |
| Staff Card | Default | Staff tab |
| Avatar | 5 color variants | All cards |
| Badge (Class/Dept) | Violet, Mint | Student, Staff |
| Overflow Menu | Open, Closed | All cards |
| Priority Alert Dot | 7 colors | Student, Staff |
| Progress Bar | 6 colors | Student cards |
| Today Summary | — | Student, Staff |
| Quick Action Tile | — | Student, Staff |
| Bento Grid/Cell | Normal, Full-width | Teacher cards |
| Availability Pill | 4 states | Teacher cards |
| Crown Badge | — | Teacher cards |
| Subject/Grade Pill | 6 subject variants | Teacher cards |
| Workload Bar | 4 colors | Teacher cards |
| Profile Button | — | Teacher cards |
| Accent Strip | 6 subject colors | Teacher cards |
| Bulk Checkbox | Selected, Unselected | Student, Teacher |

---

## 5. Card Specifications

### 5.1 Teacher Card — Bento Grid

**Purpose:** Comprehensive at-a-glance view of teacher's status, workload, and assignments. Bento grid layout for quantitative metrics.

**Wireframe:**
```
┌─────────────────────────────────────────────┐
│ ▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔ │ ← Accent strip (3px, subject gradient)
│  ┌────┐  Priya Sharma 👑    [Teaching ●]    │ ← Header
│  │ AP │  Senior Teacher · 12 yrs           │
│  └────┘                                     │
│ ─────────────────────────────────────────── │
│  CLASSES          │  STUDENTS               │
│  ┌──┐ 5  active   │  ┌──┐ 142  enrolled    │
│ ─────────────────────────────────────────── │
│  ATTENDANCE       │  WORKLOAD               │
│  ┌──┐ 94% avg     │  ████████░░  72%       │
│ ─────────────────────────────────────────── │
│  SUBJECTS & GRADES                           │
│  📐 Mathematics  🧪 Science  Gr 8  Gr 9  Gr 10│
│ ─────────────────────────────────────────── │
│  📅 3 classes today        [Profile] [⋮]    │
└─────────────────────────────────────────────┘
```

**Container:** White bg, `--r-lg` (18px) radius, `1px --line-soft` border, `--shadow-1` → hover shadow `0 8px 28px -6px rgba(91,65,213,.12), 0 2px 8px rgba(26,22,20,.06)`, hover `translateY(-2px)`, border → `--violet-soft`. Selected: `--violet-soft` bg, `--violet` border. Relative, overflow hidden. 250ms transition.

**Accent strip:** Absolute top, 3px→4px(hover)→5px(selected), subject gradient bg, 200ms height transition.

**Header:** `Row(align=Center, gap=10.dp, padding=14-14-12)`. Avatar 38dp. Name 14px bold + crown (16px gold, if class teacher). Role·Experience 11px medium `--ink-3`. Availability pill (4 states, 10.5px bold, 7dp dot, teaching has pulse animation).

**Availability states:** Teaching (mint bg, success text, pulsing dot), Break (gold bg, warning text), Meeting (sky bg, sky text), Leave (coral bg, coral text).

**Bento grid:** CSS Grid `1fr 1fr`, 1px gap with `--line-soft` bg. Cells: white→`--surface-tint` on hover, `12px 14px` padding. Full-width cells span both columns.

**Bento cells:** Classes (violet icon box, value, "active"), Students (sky icon box, "enrolled"), Attendance (color-coded icon box by threshold, "avg rate"), Workload (6dp bar with gradient fill by threshold, "Weekly capacity" + percentage), Subjects & Grades (full-width, subject pills + grade pills), Schedule + Actions (full-width, calendar icon + text + profile button + overflow).

**Subject pill/color map:**

| Subject | Icon | Gradient | Soft Bg | Ink |
|---------|------|----------|---------|-----|
| Mathematics | Grid | `#7B61E5→#5B41D5` | `#EEE8FB` | `#5B41D5` |
| Science | Flask | `#4EE6A0→#2DCE89` | `#DCF5E8` | `#2D7A4A` |
| English | Letterform | `#42CCFF→#18BFFF` | `#E0F6FF` | `#0B7AB8` |
| Social Studies | Globe | `#FF5C85→#F82B60` | `#FFE4EC` | `#D11A4A` |
| Hindi | Lines | `#FFD040→#FCB400` | `#FFF4D1` | `#B07500` |
| Computer Science | Monitor | `#A78BFA→#7C3AED` | `#EDE9FE` | `#5B21B6` |

**Profile button:** `7px 16px` padding, full radius, 12px semibold, gradient bg, white text, shadow. Hover: darker gradient + `translateY(-1px)`.

**Overflow items:** Assign Classes, Edit Details, Reset Password, Deactivate (danger).

**Bulk mode:** Checkbox row above header. Profile button + overflow hidden. Card tap toggles selection.

---

### 5.2 Student Card — Enriched Vertical

**Purpose:** Identity, parent contact, priority alerts, academic metrics, today's status, quick actions — without requiring a tap into profile.

**Wireframe:**
```
┌─────────────────────────────────────────────┐
│  ┌────┐  Aarav Patel        [Class 8-A]  ⋮  │
│  │ AP │  Roll #21    Admission #24105       │
│  └────┘                                     │
│  👤 Rahul Patel   📱 +91 XXXXXXX234         │
│ ─────────────────────────────────────────── │
│  🟢 Healthy                                 │
│  ATTENDANCE  96%      HOMEWORK  92%         │
│  ██████████████░░░    ██████████████░░      │
│  ┌───────────────────────────────────────┐  │
│  │ TODAY                              →  │  │
│  │ • Present                              │  │
│  │ • Math Homework Due                    │  │
│  │ • Bus Route B12                        │  │
│  └───────────────────────────────────────┘  │
│  [👤 Profile]  [📱 Call]  [💬 Message]      │
└─────────────────────────────────────────────┘
```

**Container:** White bg, `--r-md` (14px) radius, 16px padding, `--shadow-1`→`--shadow-2` on hover, `translateY(-1px)`. Selected: `--violet-soft` bg, `--violet` border. 250ms transition.

**Section 1 — Identity Header:** `Row(align=Top, gap=12.dp)`. Avatar 48dp (5 color variants). Name 15px bold, ellipsis. Class badge: 10px bold, `--violet-soft` bg, `--violet` text, `3px 10px` padding. Roll # + Admission #: 11px medium `--ink-3`. Overflow ⋮: 32dp, hidden in bulk.

**Section 2 — Parent Info:** `Row(gap=16.dp, marginTop=10.dp)`. Parent name (person icon, 11px medium `--ink-2`). Parent phone masked (phone icon, `+91 XXXXXXX234` format).

**Section 3 — Divider:** 1px, `--line-soft`, 12px margin top/bottom.

**Section 4 — Priority Alerts:** `Row(gap=14.dp, wrap)`. 8dp dot + 11px semibold `--ink-2` label. Colors: green (healthy), yellow (homework due), orange (fees pending), red (low attendance), blue (parent meeting), violet (new admission), gray (inactive). Halo ring: `0 0 0 3px {color}@18%`.

**Section 5 — Micro Visualizations:** `Row(gap=12.dp, marginTop=12.dp)`, two `Column(weight=1f)`. Label: 10px bold uppercase `--ink-3`. Value: 13px bold `--ink`. Bar: 6dp, full radius, `--surface-tint` track, gradient fill by threshold, 600ms width transition.

**Bar color thresholds:** Attendance: ≥85% green, ≥75% yellow, >0% red, 0% violet. Homework: ≥80% green, ≥50% yellow, <50% red.

**Section 6 — Today Summary:** `--surface-tint` bg, `--r-sm` radius, `10px 12px` padding. Header: "TODAY" 11px bold uppercase + chevron. Items: 5dp dot + 12px medium `--ink-2` text. Colors: green, yellow, red, sky. **⚠️ Bug: `.dot.red` CSS missing in prototype.**

**Section 7 — Quick Actions:** `Row(gap=6.dp, marginTop=12.dp)`, 3 equal tiles. `--surface-tint` bg, `--r-sm` radius, `9px 4px` padding. Icon 17px violet. Label 10px semibold `--ink-2`. Hover: `--violet-soft` bg. `stopPropagation` on click. Hidden in bulk mode.

**Overflow items:** Link to Parent, Edit, Promote, Remove (danger).

---

### 5.3 Staff Card — Enriched Vertical (No Micro-Viz)

**Purpose:** Same as student card but for non-teaching staff. No micro-viz, department badge, 3 contact items, no bulk mode.

**Wireframe:**
```
┌─────────────────────────────────────────────┐
│  ┌────┐  Rajesh Kumar       [Finance]   ⋮  │
│  │ RK │  Accountant    EMP-001              │
│  └────┘                                     │
│  📱 +91 98765 43210  ✉ rajesh@school.edu   │
│  🕐 9:00 AM – 5:00 PM                       │
│ ─────────────────────────────────────────── │
│  🟢 On Duty   🟣 Since 2019                 │
│  ┌───────────────────────────────────────┐  │
│  │ TODAY                              →  │  │
│  │ • Checked in 9:02 AM                   │  │
│  │ • Fee Collection — 12 receipts         │  │
│  └───────────────────────────────────────┘  │
│  [👤 Profile]  [📱 Call]  [💬 Message]      │
└─────────────────────────────────────────────┘
```

**Differences from student:** Badge = department (mint-soft bg, success text). Subtitle = role + employee ID. Contact = 3 items (phone unmasked, email, shift). No micro-viz. No bulk mode. "Since {year}" always appended to alerts. Overflow: Edit, Assign Shift, Remove.

---

## 6. Visual Wireframes

### 6.1 Full Screen — Teachers Tab

```
┌───────────────────────────────────────────┐
│  9:41                    📶 📶 🔋       │
│  ● PEOPLE                                 │
│  People Directory                         │
│  [Teachers] [Students] [Staff]            │
│  ┌─────────────────────────────────────┐  │
│  │ 🔍  Search teachers…                │  │
│  └─────────────────────────────────────┘  │
│  [All Subjects ▾]  [All Grades ▾]        │
│  ┌─────────────────────────────────────┐  │
│  │ ▔▔▔ (accent)                        │  │
│  │  ┌──┐ Priya Sharma 👑  [Teaching ●] │  │
│  │  │PS│ Senior Teacher · 12 yrs       │  │
│  │ ─────────────────────────────────── │  │
│  │  CLASSES    │  STUDENTS             │  │
│  │  5 active   │  142 enrolled         │  │
│  │ ─────────────────────────────────── │  │
│  │  ATTENDANCE │  WORKLOAD             │  │
│  │  94% avg    │  ████████░░  72%     │  │
│  │ ─────────────────────────────────── │  │
│  │  📐 Math  🧪 Sci  Gr 8  Gr 9  Gr 10│  │
│  │ ─────────────────────────────────── │  │
│  │  📅 3 classes today  [Profile][⋮]  │  │
│  └─────────────────────────────────────┘  │
│           [Load More]                     │
│  🏠    👥    📄    💬    ⚙              │
│ Home  People Records Comms Settings       │
└───────────────────────────────────────────┘
```

### 6.2 Full Screen — Students Tab

```
┌───────────────────────────────────────────┐
│  ● PEOPLE                                 │
│  People Directory                         │
│  [Teachers] [Students] [Staff]            │
│  ┌─────────────────────────────────────┐  │
│  │ ✓  3 pending link requests       →  │  │
│  └─────────────────────────────────────┘  │
│  ┌─────────────────────────────────────┐  │
│  │ 🔍  Search students…                │  │
│  └─────────────────────────────────────┘  │
│  [All Classes ▾]  [All Sections ▾]       │
│  ┌─────────────────────────────────────┐  │
│  │  ┌──┐ Aarav Patel    [Class 8-A] ⋮ │  │
│  │  │AP│ Roll #21  Admission #24105    │  │
│  │  👤 Rahul Patel  📱 +91 XXXX234     │  │
│  │  🟢 Healthy                         │  │
│  │  ATT 96%  ████████░░  HW 92% ████░ │  │
│  │  ┌───────────────────────────────┐  │  │
│  │  │ TODAY →  • Present • HW Due   │  │  │
│  │  └───────────────────────────────┘  │  │
│  │  [Profile] [Call] [Message]        │  │
│  └─────────────────────────────────────┘  │
│  🏠    👥    📄    💬    ⚙              │
└───────────────────────────────────────────┘
```

### 6.3 Dialog Wireframe

```
┌───────────────────────────────────────────┐
│  ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░ │
│  ┌───────────────────────────────────┐    │
│  │           ─────── (handle)        │    │
│  │  Add Student                      │    │
│  │  Full Name                        │    │
│  │  ┌─────────────────────────────┐  │    │
│  │  │ e.g. Aarav Patel            │  │    │
│  │  └─────────────────────────────┘  │    │
│  │  Class | Section | Roll Number   │    │
│  │  Parent Phone (optional)          │    │
│  │  [    Add Student    ]            │    │
│  │  [      Cancel       ]            │    │
│  └───────────────────────────────────┘    │
└───────────────────────────────────────────┘
```

### 6.4 Bulk Mode Card

```
┌─────────────────────────────────────────────┐
│  ⬜  ┌────┐  Aarav Patel        [Class 8-A] │
│      │ AP │  Roll #21    Admission #24105   │
│      └────┘                                 │
│  (no ⋮, no quick actions in bulk mode)      │
└─────────────────────────────────────────────┘
```
Selected: entire card bg → `--violet-soft`, border → `--violet`.

---

## 7. Design Tokens

### 7.1 Colors

| Token | Hex | Usage |
|-------|-----|-------|
| `--cream` | #FBF8F4 | Screen bg |
| `--white` | #FFFFFF | Card bg, inputs |
| `--surface-tint` | #F8F4EF | Today bg, bar tracks, tile bg, cell hover |
| `--ink` | #1A1614 | Primary text |
| `--ink-2` | #5C544E | Secondary text |
| `--ink-3` | #8A8078 | Tertiary text, labels, icons |
| `--line` | #E8E0D6 | Borders |
| `--line-soft` | #F0EAE0 | Dividers, bento grid gaps |
| `--violet` | #5B41D5 | Primary accent |
| `--violet-soft` | #EEE8FB | Badge bg, selected bg, action hover |
| `--coral` | #F82B60 | Red alerts, danger |
| `--coral-soft` | #FFE4EC | Danger hover bg |
| `--gold` | #FCB400 | Yellow alerts, crown |
| `--gold-soft` | #FFF4D1 | Yellow soft bg |
| `--sky` | #18BFFF | Blue alerts |
| `--sky-soft` | #E0F6FF | Blue soft bg |
| `--mint` | #2DCE89 | Green alerts |
| `--mint-soft` | #DCF5E8 | Green soft bg, staff badge |
| `--success` | #2D7A4A | Staff badge text |
| `--warning` | #B07500 | Break availability text |
| `#FF8800` | #FF8800 | Orange alert (⚠️ no token — add `--orange`) |

### 7.2 Typography

Font: Inter (400/500/600/700/800).

| Size | Weight | Usage |
|------|--------|-------|
| 22px | 800/400 | Page title (mixed weight) |
| 18px | 700 | Dialog title |
| 16px | 700 | Empty state title |
| 15px | 700 | Student/staff card name |
| 15px | 500 | Search input |
| 14px | 700 | Teacher card name, load more |
| 13px | 700 | Metric values, bulk count |
| 13px | 600 | Filter chips, sort btn, overflow items |
| 12px | 600 | Today items, schedule, dialog labels |
| 11px | 700 | Eyebrow, today header, nav active |
| 11px | 600 | Alert labels, parent info |
| 11px | 500 | Roll/adm/role/empId, nav inactive |
| 10.5px | 700 | Availability pill |
| 10px | 700 | Badges, metric labels, action labels |
| 10px | 600 | Subject pills |
| 9px | 700 | Bento cell labels |

### 7.3 Radius

| Token | Value | Usage |
|-------|-------|-------|
| `--r-sm` | 10px | Action tiles, overflow btn, today section |
| `--r-md` | 14px | Student/staff cards, search, dropdowns |
| `--r-lg` | 18px | Teacher cards, bulk bar, dialog top |
| `--r-xl` | 24px | Dialog card top corners |
| `--r-full` | 9999px | Pills, badges, avatars, bars, buttons |

### 7.4 Shadows

| Token | Value |
|-------|-------|
| `--shadow-1` | `0 1px 2px rgba(26,22,20,.04), 0 1px 3px rgba(26,22,20,.06)` |
| `--shadow-2` | `0 2px 8px -2px rgba(26,22,20,.08), 0 1px 3px rgba(26,22,20,.04)` |
| `--shadow-3` | `0 8px 24px -6px rgba(26,22,20,.10), 0 2px 8px rgba(26,22,20,.04)` |
| Teacher hover | `0 8px 28px -6px rgba(91,65,213,.12), 0 2px 8px rgba(26,22,20,.06)` |
| Bulk bar | `0 8px 28px -6px rgba(0,0,0,.4), 0 2px 8px rgba(0,0,0,.2)` |

### 7.5 Spacing

| Token | Value |
|-------|-------|
| `--s-xs` | 4px |
| `--s-sm` | 8px |
| `--s-md` | 16px |
| `--s-lg` | 24px |
| `--s-xl` | 32px |

### 7.6 Animation

| Token | Value |
|-------|-------|
| `--dur` | 250ms |
| `--ease` | `cubic-bezier(0.2, 0, 0, 1)` |

### 7.7 Key Sizes

| Element | Size |
|---------|------|
| Student/staff avatar | 48dp |
| Teacher avatar | 38dp |
| Bento stat icon box | 24dp |
| Overflow button | 32dp |
| Nav icon container | 32dp |
| Empty state icon circle | 56dp |
| Link banner icon box | 34dp |
| Progress bar height | 6dp |
| Accent strip height | 3px (4px hover, 5px selected) |
| Card gap | 10px |
| Screen horizontal padding | 24px |
| Card padding | 16px |

---

## 8. Interaction Specifications

### 8.1 Sub-Tab Switching

**Trigger:** Tap sub-tab pill. **Effect:** Active pill → gradient bg, inactive → transparent. Tab content switches (`display:flex`/`none`). All per-tab state (search, filters, sort, bulk, selection) preserved. Transition: 220ms.

### 8.2 Search

**Trigger:** Keystroke in search input. **Effect:** List re-renders with filter. Empty results → empty state. Production: debounce 200-300ms. No clear button.

**Search fields:** Teachers → name, role, subjects, grades. Students → name, roll, class, section, admission#, parentName. Staff → name, role, department, employeeId.

### 8.3 Filter Dropdown

**Trigger:** Tap filter chip. **Effect:** All other dropdowns close. This dropdown opens. Option tap toggles selected state. Chip label updates dynamically ("All Subjects" → "1 Subject" → "3 Subjects"). Chip style: default ↔ active. List re-renders. **Close:** Click outside or tap chip again.

### 8.4 Active Filter Removal

**Trigger:** Tap removable pill. **Effect:** Filter removed from state, dropdown option unselected, chip label updated, list re-renders. Hover: bg → coral-soft, text → coral.

### 8.5 Clear All Filters

**Trigger:** Tap "Clear all" link. **Effect:** All filters for tab cleared, all chip labels reset, all options unselected, list re-renders.

### 8.6 Sort Dropdown

**Trigger:** Tap sort button. **Effect:** Menu opens. Option tap: previous unselected, clicked selected (violet bold), button label updates ("Sort: Name" → "Sort: Roll"), menu closes, list re-sorts. **Close:** Click outside.

### 8.7 Bulk Mode Toggle

**Trigger:** Tap "Bulk" button. **Effect:** On → cards re-render with checkboxes, quick actions/overflow hidden, card tap toggles selection. Off → selection cleared, cards normal, bulk bar hidden.

### 8.8 Card Selection (Bulk)

**Trigger:** Tap card in bulk mode (excluding buttons). **Effect:** Selection toggles. Selected → `--violet-soft` bg, `--violet` border, checkbox filled. Bulk bar count updates, shows if count > 0.

### 8.9 Bulk Cancel

**Trigger:** Tap "Cancel" in bulk bar. **Effect:** Bulk off, selection cleared, toggle deactivated, cards normal, bar hidden.

### 8.10 Overflow Menu

**Trigger:** Tap ⋮ button. `stopPropagation` prevents card click. All other menus close. This menu opens. Item tap: menu closes, action triggered. Danger items → confirmation dialog. **Close:** Click outside or tap item.

### 8.11 Card Click (Non-Bulk)

**Trigger:** Tap card (excluding overflow, buttons). **Effect:** Navigate to profile screen.

### 8.12 Quick Actions

**Trigger:** Tap tile. `stopPropagation`. Profile → opens profile. Call → initiates call. Message → opens messaging.

### 8.13 Link Banner

**Trigger:** Tap banner. **Effect:** Scale 0.98 (150ms), then navigate to link approval screen.

### 8.14 Dialog Open/Close

**Open:** Tap `data-dialog` trigger → overlay `display:flex`, card slides up 300ms. **Close:** Tap `data-dialog-close` button or tap overlay background → overlay hidden.

### 8.15 Load More

**Trigger:** Tap "Load More". **Effect:** Additional cards appended (prototype: no implementation. Production: fetch next page).

### 8.16 Bottom Navigation

**Trigger:** Tap nav item. **Effect:** Navigate to tab/screen. Active state moves.

---

## 9. State Management

### 9.1 State Structure

```
state = {
  activeTab: 'teachers',
  search: { teachers:'', students:'', staff:'' },
  filters: {
    teachers: { subject:[], grade:[] },
    students: { class:[], section:[] },
    staff: { department:[], role:[] }
  },
  sort: { teachers:'name', students:'name', staff:'name' },
  bulk: { teachers:false, students:false },
  selected: { teachers:Set(), students:Set() }
}
```

### 9.2 State Transitions

- **Tab switch:** Only `activeTab` changes. Per-tab state preserved.
- **Search:** `search[tab]` updates → list re-filters → empty state if no results.
- **Filter:** `filters[tab][type]` array updates (add/remove) → chip label/style updates → list re-filters.
- **Sort:** `sort[tab]` changes → button label updates → list re-sorts.
- **Bulk toggle:** `bulk[tab]` boolean → cards re-render → bar visibility depends on `selected[tab].size > 0`.
- **Card selection:** `selected[tab]` Set add/remove → card visual state + bar count update.

### 9.3 Loading States (Production)

| Scenario | Behavior |
|----------|----------|
| Initial load | Shimmer placeholders (ShimmerBox) matching card silhouette |
| Search/filter | Keep previous results, debounce |
| Load More | Inline spinner in button |
| Dialog submit | Button spinner, auto-disable (VButton loading) |

### 9.4 Error States (Production)

| Scenario | Behavior |
|----------|----------|
| API failure | Error state with retry (VStateHost) |
| Dialog submit fail | `.dialog-error` coral text |
| Search timeout | Stale results + warning banner |

### 9.5 Success States (Production)

| Scenario | Behavior |
|----------|----------|
| Dialog submit success | Dialog closes, toast, list refresh |
| Bulk action success | Bar hides, selection clears, toast |
| Link approved | Banner count decrements |

---

## 10. Accessibility Requirements

### 10.1 Keyboard Navigation (Production)

| Element | Key | Action |
|---------|-----|--------|
| Sub-tab pills | Tab/Shift+Tab | Focus between pills |
| Sub-tab pills | Enter/Space | Activate tab |
| Search input | Tab | Focus from previous element |
| Filter chip | Enter/Space | Open dropdown |
| Filter option | Arrow Up/Down | Navigate options |
| Filter option | Enter/Space | Toggle selection |
| Sort button | Enter/Space | Open sort menu |
| Card | Tab | Focus to next interactive element |
| Overflow button | Enter/Space | Open menu |
| Quick action tile | Enter/Space | Trigger action |
| Bulk toggle | Enter/Space | Toggle bulk mode |
| Dialog | Escape | Close dialog |
| Dialog inputs | Tab | Navigate between fields |

### 10.2 Screen Reader

| Element | ARIA |
|---------|------|
| Sub-tab pills | `role="tab"`, `aria-selected="true/false"` |
| Tab content | `role="tabpanel"` |
| Search input | `aria-label="Search {tab}"` |
| Filter chip | `aria-haspopup="true"`, `aria-expanded="true/false"` |
| Filter dropdown | `role="listbox"`, options `role="option"`, `aria-selected` |
| Sort menu | `role="menu"`, options `role="menuitemradio"` |
| Card | `role="button"`, `aria-label="{name}, {role/class}"` |
| Overflow menu | `aria-haspopup="menu"`, `aria-expanded` |
| Bulk checkbox | `role="checkbox"`, `aria-checked` |
| Dialog | `role="dialog"`, `aria-modal="true"`, `aria-labelledby` pointing to title |
| Empty state | `role="status"` |
| Link banner | `role="alert"` if count > 0 |
| Availability pill | `aria-label="{availability status}"` |
| Priority alert | `aria-label="{alert label}"` (dot is decorative) |
| Progress bar | `role="progressbar"`, `aria-valuenow`, `aria-valuemin=0`, `aria-valuemax=100` |

### 10.3 Color Contrast

| Text | Bg | Ratio | WCAG |
|------|----|----|------|
| `--ink` (#1A1614) on `--white` | 15.8:1 | AAA |
| `--ink-2` (#5C544E) on `--white` | 7.2:1 | AAA |
| `--ink-3` (#8A8078) on `--white` | 3.9:1 | AA (large only) |
| `--violet` (#5B41D5) on `--violet-soft` | 5.4:1 | AA |
| `--coral` (#F82B60) on `--coral-soft` | 3.2:1 | ⚠️ Below AA |
| White on `--violet` gradient | 4.6:1 | AA |
| `--ink-3` on `--surface-tint` | 3.5:1 | ⚠️ Below AA for small text |

**Action items:** `--ink-3` labels at 11px may need darkening for WCAG AA. Coral on coral-soft needs darkening for danger text.

### 10.4 Focus Indicators

All interactive elements must have visible focus indicators. Prototype uses border color change + box-shadow ring. Production: ensure `focus` state is visible in Compose (not just hover).

### 10.5 Touch Targets

| Element | Size | Meets 44dp? |
|---------|------|-------------|
| Sub-tab pill | ~36×32dp | ⚠️ Close |
| Filter chip | ~36×28dp | ⚠️ Close |
| Overflow button | 32×32dp | ⚠️ Close |
| Quick action tile | ~48×48dp | ✅ |
| Nav item | ~48×48dp | ✅ |
| Bulk checkbox | 22×22dp | ❌ Needs padding |
| Sort button | ~36×28dp | ⚠️ Close |

**Recommendation:** Increase touch targets to minimum 44×44dp in production.

### 10.6 Reduced Motion

Production must respect `prefers-reduced-motion`:
- Disable card entrance stagger animation
- Disable availability dot pulse
- Disable progress bar width animation (show final state)
- Disable bulk bar slide (show/hide instantly)
- Disable dialog slide (show/hide instantly)

---

## 11. Engineering Notes

### 11.1 Data Model Changes

#### New DB Tables (Phase 1 — implemented)

**`staff_shifts` (StaffShiftsTable):**
```sql
CREATE TABLE IF NOT EXISTS staff_shifts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id UUID NOT NULL REFERENCES schools(id),
    staff_id UUID NOT NULL REFERENCES non_teaching_staff(id),
    shift_name VARCHAR(64) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMPTZ DEFAULT now()
);
```

**`staff_check_ins` (StaffCheckInsTable):**
```sql
CREATE TABLE IF NOT EXISTS staff_check_ins (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id UUID NOT NULL REFERENCES schools(id),
    staff_id UUID NOT NULL REFERENCES non_teaching_staff(id),
    date DATE NOT NULL,
    check_in_at TIMESTAMPTZ NOT NULL,
    check_out_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT now()
);
```

**`teacher_ratings` (TeacherRatingsTable):**
```sql
CREATE TABLE IF NOT EXISTS teacher_ratings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id UUID NOT NULL REFERENCES schools(id),
    teacher_id UUID NOT NULL REFERENCES app_users(id),
    rating REAL NOT NULL CHECK (rating >= 0 AND rating <= 5),
    rated_by UUID REFERENCES app_users(id),
    created_at TIMESTAMPTZ DEFAULT now()
);
```

**New columns on `non_teaching_staff`:**
- `employee_id VARCHAR(32)` — nullable, unique per school
- `shift_id UUID` — nullable, FK to `staff_shifts.id`

#### StudentDto — new fields (Phase 2 — implemented)
```kotlin
val parentName: String? = null           // primary parent's display name
val homeworkPercent: Float = 0f          // submission ratio 0..100
val feesPending: Boolean = false         // any overdue/unpaid fee_records
val parentMeetingScheduled: Boolean = false  // upcoming PTM event
val todayItems: List<TodayItemDto> = emptyList()  // composite today indicators
```

#### StaffDto — new fields (Phase 3 — implemented)
```kotlin
val employeeId: String? = null           // from non_teaching_staff.employee_id
val shift: String? = null                // "Morning (09:00–17:00)" from staff_shifts
val status: String = "active"            // active|inactive from is_active
val joinedYear: String? = null           // year from created_at
val todayItems: List<TodayItemDto> = emptyList()  // check-in status
```

#### New DTO:
```kotlin
@Serializable
data class TodayItemDto(
    val color: String,  // "green"|"yellow"|"red"|"sky"
    val text: String
)
```

#### TeacherCardProfileDto — new fields (Phase 4 — implemented)
```kotlin
val isClassTeacher: Boolean = false      // from teacher_subject_assignments.is_class_teacher
val experience: String? = null           // "12 yrs" derived from app_users.created_at
val rating: Float? = null                // average from teacher_ratings
```

#### TeacherCardWorkloadDto — new fields
```kotlin
val workloadPercent: Int = 0             // totalClasses * 10, capped at 100
val schedule: String = ""                // "3 classes today" from teacher_periods
```

#### TeacherCardDto — new field
```kotlin
val availability: String = "break"       // "teaching"|"break"|"meeting"|"leave"
```

### 11.2 Backend API Changes (All Phases — implemented)

#### Student list (`GET /api/v1/school/students`)

**Pagination:** When `page` query param is present, returns `StudentListPaginatedResponse` with `students` + `pagination` (page, pageSize, totalRecords, hasNext). Default pageSize=10, max=100. When `page` is absent, returns legacy `StudentListResponse` (backward compatible).

**Enrichment** (via `StudentAggregationService`):
- `parent_name`: queries `parent_child_links` (approved, primary guardian first) → `app_users.fullName`
- `homework_percent`: counts `homework_submissions` (status != "not_submitted") / total active `homework` rows
- `fees_pending`: checks `fee_records` where status != "PAID" and `child_id` matches student
- `parent_meeting_scheduled`: checks `ptm_events` for any upcoming/today event
- `today_items`: composite query — attendance today (present/absent/late/leave), homework due today (pending count), library overdue books, transport assignment

#### Staff list (`GET /api/v1/school/staff`)

**Pagination:** Same pattern as students — `page` param triggers `StaffListPaginatedResponse`, default pageSize=10.

**Enrichment** (`enrichStaffForList`):
- `employee_id`: from `non_teaching_staff.employee_id` column
- `shift`: from `staff_shifts` table (active shift for this staff member)
- `status`: derived from `is_active` column
- `joined_year`: year extracted from `created_at`
- `today_items`: check-in status from `staff_check_ins` (checked in time, or "Not checked in")

#### Teacher list (`GET /api/v1/school/teachers`)

Already paginated. Default pageSize changed from 20 to 10. New enrichment added:
- `is_class_teacher`: from `teacher_subject_assignments.is_class_teacher` (any assignment)
- `experience`: years from `app_users.created_at` → "N yrs"
- `rating`: average from `teacher_ratings` table (null when no ratings)
- `workload_percent`: `totalClasses * 10`, capped at 100
- `schedule`: from `teacher_periods` where weekday = today → "N classes today" / "No classes today"
- `availability`: derived from `teacher_periods` (currently within start_time/end_time → "teaching"), `leave_requests` (approved + today in range → "leave"), else "break"

All batched queries (ratings, periods, leave) use OR-reduce on teacher IDs to avoid N+1.

#### Link requests count (`GET /api/v1/school/link-requests/count`)

New lightweight endpoint returning `LinkRequestCountDto`:
```json
{
  "pending": 5,
  "needs_review": 2
}
```
Uses `requireSchoolContext` (any authenticated school user). Queries `parent_child_links` count grouped by status.

### 11.3 Alert Derivation Logic (Client-Side)

```kotlin
// Student alerts
val healthy = status == "active" && attendancePercent >= 75f && !isNewAdmission
val lowAttendance = attendancePercent in 0.1f..74.9f
val homeworkDue = homeworkPercent < 80f
val feesPending = feesPending
val parentMeeting = parentMeetingScheduled
val newAdmission = isNewAdmission
val inactive = status != "active"

// Staff alerts
val onDuty = status == "active" && checkedInToday
val onRoute = role == "Driver" && currentlyOnRoute
val offDuty = status != "active" || !checkedInToday
// "Since {year}" always shown
```

### 11.4 Phone Masking

```kotlin
fun maskPhone(phone: String): String {
    // Keep + prefix, replace all but last 4 digits with X
    // "+91 98765 43210" → "+91 XXXXXXX43210" (prototype pre-masks in mock data)
}
```

### 11.5 Compose Component Mapping

| Prototype | Compose | Notes |
|-----------|---------|-------|
| Card container | `VCard(padding, onClick)` | — |
| Avatar | `VAvatar(name, photoUrl, size)` | Gradient variants need custom bg |
| Divider | `VDivider()` | — |
| Status dot | `VStatusDot(color, ring=true)` | — |
| Progress bar | `VProgressBar(value, tone, height=6.dp)` | — |
| Label | `VLabel(text)` | — |
| Badge | Custom `Box` + `Text` | Not VBadge (different style) |
| Overflow menu | `DropdownMenu` + `DropdownMenuItem` | Material3 |
| Empty state | `VEmptyState(title, body, icon)` | — |
| Dialog | `VConfirmDialog` or custom bottom sheet | — |
| Search input | `VInput(leadingIcon=Search)` | — |
| Sub-tab pills | `VTopTabs` or custom | — |
| Bottom nav | `VBottomNav2` | — |
| Shimmer | `ShimmerBox` | — |
| Button | `VButton(variant, size)` | — |

### 11.6 Performance Considerations

- **Debounce search** 200-300ms to avoid re-rendering on every keystroke
- **Virtualize card list** for large schools (LazyColumn in Compose)
- **Paginate** teacher list (Load More button implies pagination)
- **Cache enrichment data** — today_items change frequently but don't need real-time; cache for 30-60 seconds
- **Avoid N+1 queries** — aggregate today_items server-side in list response

---

## 12. QA Checklist

### Visual

- [ ] Teacher card: accent strip color matches first subject
- [ ] Teacher card: crown badge visible only for class teachers
- [ ] Teacher card: availability pill correct state (teaching/break/meeting/leave)
- [ ] Teacher card: teaching dot pulses
- [ ] Teacher card: bento grid 2-column layout with 1px dividers
- [ ] Teacher card: workload bar color matches threshold (green/yellow/red)
- [ ] Teacher card: subject pills have correct icon + color per subject
- [ ] Teacher card: grade pills show "Gr {n}" format
- [ ] Student card: avatar gradient matches color tag
- [ ] Student card: class badge shows "Class {n}-{section}" in violet-soft
- [ ] Student card: parent phone is masked (last 4 digits only)
- [ ] Student card: priority alert dots have correct color + halo ring
- [ ] Student card: progress bars animate width on render (600ms)
- [ ] Student card: progress bar color matches threshold
- [ ] Student card: today summary has tinted bg, correct dot colors
- [ ] Staff card: department badge in mint-soft/success (not violet)
- [ ] Staff card: phone NOT masked
- [ ] Staff card: "Since {year}" always present in alerts
- [ ] Staff card: no micro-viz section
- [ ] All cards: hover lifts card (translateY) and increases shadow
- [ ] All cards: name truncates with ellipsis for long names

### Functional

- [ ] Sub-tab switching preserves per-tab search/filters/sort/bulk state
- [ ] Search filters correctly across all specified fields per tab
- [ ] Filter dropdown opens/closes correctly
- [ ] Filter multi-select works (can select multiple values)
- [ ] Filter chip label updates dynamically ("All" → "1 Subject" → "3 Subjects")
- [ ] Active filter chips appear below filter row
- [ ] Removing active filter chip updates dropdown + chip label + list
- [ ] "Clear all" resets all filters for that tab
- [ ] Sort dropdown changes sort order correctly
- [ ] Sort button label updates on selection
- [ ] Bulk toggle shows/hides checkboxes, hides quick actions/overflow
- [ ] Card tap in bulk mode toggles selection (not navigation)
- [ ] Bulk bar shows correct count, only when count > 0
- [ ] Bulk bar actions match tab (teachers: assign/deactivate, students: graduate/transfer)
- [ ] Bulk cancel clears selection and exits bulk mode
- [ ] Overflow menu opens on ⋮ tap, closes on outside click
- [ ] Overflow menu danger items trigger confirmation dialog
- [ ] Quick action buttons don't trigger card click (stopPropagation)
- [ ] Link banner press feedback (scale 0.98)
- [ ] Dialogs open as bottom sheet, slide up animation
- [ ] Dialog close on cancel button or overlay tap
- [ ] Load More button visible only on teachers tab
- [ ] Empty state shows correct message per tab
- [ ] Staff tab has no bulk toggle, no bulk bar

### Accessibility

- [ ] All interactive elements have visible focus indicators
- [ ] Keyboard navigation works for tabs, search, filters, sort, cards
- [ ] Screen reader announces card identity (name + role/class)
- [ ] Screen reader announces alert labels
- [ ] Progress bars have ARIA progressbar role with values
- [ ] Dialog has role=dialog, aria-modal=true
- [ ] Color contrast meets WCAG AA for all text (check ink-3 and coral)
- [ ] Touch targets ≥ 44dp (check small buttons)
- [ ] Reduced motion preference respected

### Responsiveness

- [ ] Cards render correctly at 320dp width (small screen)
- [ ] Filter row scrolls horizontally without breaking layout
- [ ] Sub-tab pills scroll horizontally
- [ ] Long names truncate with ellipsis
- [ ] Many priority alerts wrap to second line
- [ ] Many subject pills wrap correctly
- [ ] Bento grid maintains 2-column layout at narrow widths

### Animations

- [ ] Card entrance: fadeIn + translateY, staggered 50ms per card (max 7)
- [ ] Progress bar: width animates 600ms on render
- [ ] Card hover: translateY + shadow transition 250ms
- [ ] Availability dot: pulse animation 2s infinite (teaching only)
- [ ] Bulk bar: slide up 300ms
- [ ] Dialog: slide up 300ms
- [ ] Accent strip: height transition 200ms on hover/selection
- [ ] Subject pill: scale 1.03 on hover
- [ ] Quick action tile: bg transition 150ms on hover

### Edge Cases

- [ ] Student with no parent → parent info shows "—"
- [ ] Staff with no email → email shows "—"
- [ ] Student with 0% attendance → bar shows violet, value "—"
- [ ] Teacher on leave → workload 0%, bar shows `--line` color
- [ ] Teacher with no subjects → subject pills area empty
- [ ] Empty search results → empty state
- [ ] All filters cleared → list returns to full
- [ ] Bulk mode with 0 selections → bulk bar hidden
- [ ] Long subject name (e.g. "Computer Science") → pill doesn't overflow
- [ ] Long department name → badge doesn't overflow
- [ ] Multiple alerts on one card → wrap to second line
- [ ] Today summary with 0 items → section still shows with header only

---

## 13. Future Scalability Notes

### Potential Enhancements

| Feature | Notes |
|---------|-------|
| Pull-to-refresh | Use `VPullRefresh` to refresh today_items and alert data |
| Card swipe actions | Swipe left for quick actions (call/message) on student/staff cards |
| Sticky filter bar | Make search + filters sticky at top during scroll |
| Infinite scroll | Replace Load More with infinite scroll for teachers |
| Saved filters | Let admins save common filter combinations |
| Column customization | Let admins choose which bento cells to show/hide on teacher cards |
| Batch assign | Bulk assign classes to multiple teachers at once |
| Export | Export filtered list as CSV |
| Real-time availability | WebSocket-based live availability for teachers |
| Photo upload | Avatar photo upload from card overflow menu |
| Staff check-in/out | Quick action tile for staff check-in (replaces call for drivers) |
| Student behavior | Re-add behavior emoji micro-viz (removed per user request) |

### Scalability Considerations

- **Large schools (1000+ students):** Must virtualize card list (LazyColumn), paginate API, debounce search
- **Multiple schools:** Filter by school_id (already in JWT), consider school switcher in header
- **Offline support:** Cache card data in Room for offline browsing; today_items will be stale but identity/alerts available
- **Dark/midnight theme:** All colors must have dark theme equivalents in `VTheme.colors`; verify contrast ratios
- **Internationalization:** Alert labels, today item text, dialog labels need i18n strings; subject names may need localization
- **Role-based visibility:** Teachers may see a subset of student card data (no fees, no parent phone); parents see even less

### Known Prototype Bugs

1. **`.se-today-item .dot.red` CSS missing** — only green, yellow, sky defined. Fix: add `.se-today-item .dot.red{background:var(--coral)}`
2. **Sub-tab header `display:none`** — Add/Import buttons exist in HTML but hidden. App must place these elsewhere.
3. **Sort + bulk row `display:none`** — Sort and bulk functionality works via JS but the row is hidden. App must expose sort and bulk controls.
4. **No loading/error states** — Prototype has shimmer keyframe defined but unused. No error handling.
5. **No dialog validation** — Dialog inputs have no validation; submit closes dialog regardless of input.
6. **Orange alert color `#FF8800` has no CSS token** — hardcoded in `.se-alert-dot.orange`. Should add `--orange` to `:root`.
7. **Staff sort-bulk row has no bulk toggle** — only sort dropdown, no bulk button (correct — staff has no bulk mode).

---

## File References

| File | Path | Role |
|------|------|------|
| Prototype | `preview/people-tab-prototype.html` | Source of truth (1542 lines) |
| Student screen | `composeApp/.../ui/v2/screens/school/StudentRosterScreenV2.kt` | Student card implementation |
| Student models | `shared/.../feature/school/domain/model/StudentModels.kt` | StudentDto |
| Staff models | `shared/.../feature/school/domain/model/StaffModels.kt` | StaffDto |
| Staff repo | `shared/.../feature/admin/domain/repository/StaffRepository.kt` | Staff data access |
| Staff API | `shared/.../feature/admin/data/remote/StaffApi.kt` | Staff HTTP endpoints |
| Staff VM | `shared/.../feature/admin/presentation/StaffViewModel.kt` | Staff state management |
| VColors | `composeApp/.../ui/v2/theme/VColors.kt` | Color tokens |
| VAtoms | `composeApp/.../ui/v2/components/VAtoms.kt` | VDivider, VStatusDot, VLabel |
| VProgress | `composeApp/.../ui/v2/components/VProgress.kt` | VProgressBar |
| VAvatar | `composeApp/.../ui/v2/components/VAvatar.kt` | Avatar component |
| VCard | `composeApp/.../ui/v2/components/VCard.kt` | Card container |
| VBadge | `composeApp/.../ui/v2/components/VBadge.kt` | VBadgeTone enum |
| VButton | `composeApp/.../ui/v2/components/VButton.kt` | Button component |
| VInput | `composeApp/.../ui/v2/components/VInput.kt` | Search/input field |
| VIcons | `composeApp/.../ui/v2/components/VIcons.kt` | Icon set |
| VNavigation | `composeApp/.../ui/v2/components/VNavigation.kt` | VTopTabs, VBottomNav2 |
| VStructure | `composeApp/.../ui/v2/components/VStructure.kt` | VScreenScaffold, VEmptyState, VConfirmDialog |
| VShimmer | `composeApp/.../ui/v2/components/VShimmer.kt` | ShimmerBox loading placeholder |
| Link requests VM | `shared/.../feature/admin/presentation/LinkRequestsViewModel.kt` | Link approval queue |
| DB tables | `server/.../db/Tables.kt` | All Exposed table definitions |
| Koin DI | `shared/.../di/Koin.kt` | All DI registration |
