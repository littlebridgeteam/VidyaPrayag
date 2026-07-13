# UI Overhaul Prompt — Absolute Pixel-Perfect Match to Premium Reference

> **Goal:** Make the Compose Multiplatform UI **100% identical** to the premium reference designs in `preview/parent-portal.html` and `preview/index.html`. Every screen, every component, every animation, every spacing value.

> **Design language:** M3 Expressive, Inter font, $100M startup 2026 look.

> **Reference files:**
> - `preview/index.html` — Auth flow: Landing, Login Parent, Login Staff, Signup, Onboarding 1-3, School Select, Child Link, Done
> - `preview/parent-portal.html` — Parent portal: Home, Academics, Fees, Conversations, Profile tabs + 10 overlays + FAB menu + bottom nav

---

## PART 1: Design Token Audit (PASS — No Changes Needed)

The design tokens are **already correctly implemented** and match the reference CSS `:root` variables exactly:

| Token Category | Reference CSS | Compose Token File | Status |
|---|---|---|---|
| Colors (40+ tokens) | `:root` CSS variables | `VColorPalette.kt` → `VColors.kt` | ✅ Exact match |
| Typography (60+ styles) | CSS font-size/weight/letter-spacing | `VTypography.kt` | ✅ Exact match |
| Shapes (7 tokens) | `--shape-xs` through `--shape-full` | `VShapes.kt` | ✅ Exact match |
| Motion (durations, easings, keyframes) | `--dur-*`, `--ease-*`, `@keyframes` | `VMotion.kt` | ✅ Exact match |
| Font family | `Inter` with `opsz` 32 | `LocalInterFont` via bundled .ttf | ✅ Exact match |

**No token-level changes are required.** All discrepancies are at the **component** and **screen layout** level.

---

## PART 2: Screen-by-Screen Discrepancy Analysis

### 2.1 — Splash Screen

**Reference:** No splash screen in HTML reference (web starts at landing).
**Compose:** `SplashScreen.kt` — logo scale/alpha animation, gradient bg, progress indicator.

**Verdict:** Splash is a native app pattern. Keep as-is. No reference to match against.

---

### 2.2 — Landing Screen (`CommonLandingScreen.kt` vs `index.html` Landing)

**Reference structure (index.html lines 93-230):**
1. Brand bar: 44dp icon (gradient bg, shadow) + "Enroll+" text (20px/900) + version tag
2. Greeting section: eyebrow (13px/600/primary) with **pulsing teal dot** + headline (36px/800 with gradient `<em>`) + sub text (15px/500)
3. Hero card: gradient bg (140deg primary→#544AB8→#3D35A0), radial glows (top-right CDBDFF, bottom-left 00BFA0), live pill with pulsing dot, title (24px/800), desc (14px/500), feature pills (glassmorphism)
4. 3 stat cards in grid: surface-container-lowest bg, 24px/900 values, 10px/600 uppercase labels
5. Role tiles: "Parent" (primary-container gradient) and "Staff" (tertiary-container gradient), 20dp padding, 17px/800 name, 13px/500 desc, chevron icon
6. Trust badges row: "500+ Schools", "50K+ Parents", "4.8★ Rating" — 12px/600 pills
7. Footer: legal links

**Compose current state:**
- Has brand bar, greeting, hero card, role selection cards, footer
- **Discrepancies:**
  - ❌ Missing pulsing teal dot in eyebrow (landing-eyebrow-dot)
  - ❌ Missing 3 stat cards grid (ls-card layout)
  - ❌ Missing trust badges row
  - ❌ Hero card may not have exact feature pills with glassmorphism
  - ❌ Role tiles may not have exact gradient backgrounds (primary-container→surface-container-lowest, tertiary-container→surface-container-lowest)
  - ❌ Version tag may be missing
  - ⚠️ Verify radial glow positions match exactly

**Required changes:**
1. Add pulsing teal dot before eyebrow text (use `rememberLivePulse()`)
2. Add 3-column stat cards grid below hero card
3. Add trust badges row below role tiles
4. Ensure role tiles have correct gradient backgrounds and press-scale + shape-morph interactions
5. Add version tag in brand bar

---

### 2.3 — Login Parent Screen (vs `index.html` page-login-parent)

**Reference structure (index.html lines 420-465):**
1. Status bar with light icons (on gradient bg)
2. Login hero: gradient bg (primary→primaryDeep), back button, title "Parent Login" (26px/900), sub text (14px/500/white-70%), badge pill
3. Login body: form group (email), form group (password), forgot password link, primary button with arrow icon
4. Divider: "Or continue with" — line + text + line
5. Social row: Google + Apple buttons (surface-container-lowest bg, 1.5px outline-variant border)
6. Login footer: "New parent? Create account" link

**Compose current state:** `ParentAuthScreen.kt` uses `AuthScaffoldPremium.kt`
- **Discrepancies:**
  - ⚠️ Verify login hero gradient matches exactly (140deg primary→#544AB8→#3D35A0)
  - ⚠️ Verify form inputs match CSS: 16px/20px padding, shape-lg radius, 1.5px outline-variant border, focus state with primary border + 3px primary-container shadow
  - ⚠️ Verify social buttons match: surface-container-lowest bg, 1.5px outline-variant border, 14px/600 text
  - ⚠️ Verify divider styling: 1px outline-variant lines, 12px/600 uppercase text
  - ❌ Verify animated entrance (anim-1, anim-2, anim-3) staggered delays match

---

### 2.4 — Login Staff Screen (vs `index.html` page-login-staff)

**Reference structure (index.html lines 467-507):**
- Same as parent login but:
  - Title: "Staff Login"
  - Sub: "Teachers & administrators sign in here."
  - Badge: "For Teachers & Admins" with graduation cap icon
  - Button color: **tertiary** bg with on-tertiary text (NOT primary)
  - Social: "School SSO" + "Google Workspace" (NOT Google + Apple)

**Compose current state:** `AdminAuthScreen.kt`
- **Discrepancies:**
  - ❌ Staff login button should use **tertiary** color, not primary
  - ❌ Social buttons should be "School SSO" + "Google Workspace", not Google + Apple
  - ❌ Badge should have graduation cap icon
  - ⚠️ Verify hero gradient matches

---

### 2.5 — Signup Screen (vs `index.html` page-signup) — **MISSING**

**Reference structure (index.html lines 509-559):**
1. Top bar with back button + "Create Account" title
2. Description text: "Join VidyaSetu as a parent..."
3. Signup type toggle: "Parent" (active) | "Teacher / Admin" — pill buttons
4. Form: Full Name, Email, Phone, Password
5. Primary button: "Create Account"
6. Divider: "Or sign up with"
7. Social row: Google + Apple
8. Footer: "Already have an account? Login"

**Compose:** **NO dedicated signup screen exists.** This is a **critical missing screen**.

**Required:** Build `SignupScreen.kt` matching the reference exactly.

---

### 2.6 — Onboarding Screens 1-3 (vs `index.html` pages onboarding, onboarding2, onboarding3) — **MISSING**

**Reference structure (index.html lines 561-637):**
Each onboarding screen has:
1. Full-height illustration area with gradient bg and centered icon circle
   - OB1: primary gradient, checkmark icon
   - OB2: tertiary gradient, pulse/health icon
   - OB3: warm orange gradient, smiley icon
2. Onboard body: title (26px/900), description (15px/500), dot indicators (3 dots, active = primary), actions row (Skip text button + Next primary button)

**Compose:** **NO onboarding screens exist.** These are **critical missing screens**.

**Required:** Build 3 onboarding screens with:
- Exact gradient backgrounds per screen
- Icon circle (120dp, white icon, 56dp size)
- Slide-up entrance animations with staggered delays
- Dot indicators with active state
- Skip + Next buttons

---

### 2.7 — School Select Screen (vs `index.html` page-schoolselect) — **LIKELY MISSING**

**Reference structure (index.html lines 639-683):**
1. Top bar: back button + "Select School" title
2. Description text
3. Search field
4. School option list: each has logo (gradient bg), name (15px/800), meta (12px/500), check icon for selected
5. Continue button at bottom

**Compose:** May exist as part of onboarding or auth flow. Need to verify or build.

---

### 2.8 — Child Link Screen (vs `index.html` page-childlink)

**Reference structure (index.html lines 685-723):**
1. Top bar: back + "Link Your Child"
2. Description text
3. Form: Child's Full Name, Class/Grade, Roll Number/Student ID, Date of Birth
4. Linked child card: tertiary border + tertiary-container bg, avatar, name, meta, "Linked" badge
5. Primary button: "Link Child & Continue"
6. Secondary button: "Add Another Child Later"

**Compose:** `ParentLinkChildScreen.kt` exists. Need to verify it matches the reference exactly.

---

### 2.9 — Done Screen (vs `index.html` page-done) — **MISSING**

**Reference structure (index.html lines 725-745):**
1. Centered layout
2. Success circle: 120dp, tertiary gradient, white checkmark (56dp)
3. Title: "You're all set!" (28px/900)
4. Description: success message
5. Primary button: "Enter Parent Portal"

**Compose:** **NO done screen exists.** Need to build or add to auth flow.

---

### 2.10 — Parent Portal Shell & Top Bar

**Reference (parent-portal.html lines 840-874):**
1. Phone frame with island + status bar
2. Top bar: menu icon (44dp), "VidyaSetu" title (20px/800), search icon, bell icon with red badge dot
3. Search field: surface-container-high bg, shape-full radius, search icon + input + avatar (36dp gradient)

**Compose:** `ParentPortalShell.kt` has `ParentTopBar` and `ParentSearchField`
- **Discrepancies:**
  - ⚠️ Verify top bar icon buttons are 44dp with shape-full radius
  - ⚠️ Verify bell icon has red badge dot (10dp, error bg, 2px surface border)
  - ⚠️ Verify search field has surface-container-high bg and shape-full radius
  - ⚠️ Verify search avatar is 36dp with gradient bg (primary→primaryFixedDim)
  - ❌ Menu icon in reference is hamburger, verify Compose uses same

---

### 2.11 — Parent Home Tab

**Reference (parent-portal.html lines 856-1044):**

**Greeting:**
- Eyebrow: "Wednesday, 5 March" (13px/600/primary)
- Title: "Hi Priya, here's *Aarav's* day" (34px/800, em = gradient text 34px/900)

**Hero Card:**
- Gradient: 140deg primary→#544AB8→#3D35A0
- Radial glow top-right: 280dp CDBDFF 25% opacity
- Radial glow bottom-left: 240dp 00BFA0 12% opacity
- Top row: live pill (glassmorphism, pulsing #00F5C4 dot) + icon button (glassmorphism)
- Student row: 64dp avatar (glassmorphism), name (22px/800), class info (14px/500/70%)
- Stats row: 3 stats (26px/900 value, 10px/600 uppercase label)
- Press interaction: border-radius morphs from 28dp to 24dp

**Live Update Banner:**
- surface-container-lowest bg, shape-xl radius
- Clock icon in circle (40dp, surface-container-low bg)
- Title (14px/700) + sub (12px/500/on-surface-variant)
- Arrow icon
- Press: scale 0.98 + shape morph

**Filter Chips:**
- Horizontal scroll, chips: active = on-surface bg / surface text, inactive = surface-container bg / on-surface-variant text
- 14px/600, shape-full radius, press scale 0.93

**Priority Carousel:**
- Section header: "Priority" + "See all" link
- 4 feature cards in horizontal scroll:
  - Each card: 220dp width, shape-2xl radius, colored bg per type
  - fc-fees: primary-container bg, fc-attend: tertiary-container, fc-hw: warmOrangeContainer, fc-msg: secondary-container
  - Icon (44dp circle), title (18px/800), subtitle (14px/500), amount (30px/900), badge (13px/700 pill)
  - Press: scale 0.97 + shape morph

**Quick Stats:**
- 3 cards in row: surface-container-lowest bg, shape-xl radius
- Icon (40dp circle), value (24px/900), label (10px/600 uppercase)
- Press: scale 0.95 + shape morph

**Today's Schedule:**
- Progress bar: 4dp height, shape-full, gradient fill (primary→tertiary)
- Schedule cards: surface-container-lowest bg, shape-xl radius
  - Time (18px/800 hr + 12px/600 am/pm), divider, subject (15px/700), teacher (13px/500)
  - Live badge: pulsing dot + "Live" text
  - Next badge: primary bg pill

**School Updates:**
- Update items: avatar (40dp circle), source (13px/700), time (12px/500), title (15px/800), text (13px/500)
- Action buttons: primary (on-surface bg) + secondary (surface-container-low bg)

**Compose current (`ParentHomeScreen.kt`):**
- Has greeting, hero card, live update, filter chips, priority carousel, quick stats, schedule, school updates
- **Discrepancies:**
  - ❌ Greeting title format: reference shows "Hi Priya, here's *Aarav's* day" — Compose may not render the gradient accent on child's name correctly
  - ❌ Hero card: Compose uses VHeroCard component which may not match exact radial glow positions, glassmorphism effects, live pill styling
  - ❌ Live update banner: Compose uses TertiaryContainer bg instead of surface-container-lowest with clock icon
  - ❌ Feature cards: Compose uses FeatureCardData/FeatureCard which may not match exact 220dp width, colored backgrounds, icon styling
  - ❌ Quick stats: Compose uses VQuickStatCard which may not match exact layout
  - ❌ Schedule cards: need to verify live/next badge styling matches reference
  - ❌ School updates: Compose uses VUpdateCard which may not match exact avatar/source/title/text layout
  - ⚠️ Filter chips: verify active state uses on-surface/surface (NOT primary/on-primary)

---

### 2.12 — Parent Academics Tab

**Reference (parent-portal.html lines 1047-1171):**

**Action Cards:**
- 2 cards in row: surface-container-lowest bg, shape-xl radius
- Leave card: calendar icon, "Apply Leave", "2 days used this term"
- Health card: pulse icon, "Health", "Pulse score: 87"
- Press: scale 0.97 + shape morph

**Sub-tabs:**
- Active: on-surface bg / surface text, Inactive: surface-container bg / on-surface-variant text
- 13px/600, shape-full radius

**Overview sub-tab:**
- Progress card: surface-container-lowest bg, shape-xl radius
  - Progress ring (120dp, 10dp stroke, primary color, center text 28px/900)
  - Title + description text
- Subject breakdown: syllabus-item with name + percentage + bar (4dp height, shape-full)

**Attendance sub-tab:**
- Progress ring card (94%)
- Monthly breakdown: days present (tertiary), absent (error), late (warmOrange)

**Marks sub-tab:**
- Mark cards: surface-container-lowest bg, shape-xl radius
  - Subject name (15px/700) + date (12px/500)
  - Score: value (24px/900) + max (14px/500/on-surface-variant)

**Syllabus sub-tab:**
- Syllabus items with colored progress bars (primary, tertiary, warmOrange)

**Homework sub-tab:**
- HW cards: pending (warmOrange accent) vs done (tertiary accent)
  - Icon (40dp circle), title (15px/700), sub (13px/500), status badge

**Report sub-tab:**
- Report card with subject metrics (bar + percentage + grade)
- Teacher remarks card

**Compose current (`ParentAcademicsScreen.kt`):**
- Has action cards, sub-tabs, 6 sub-tab contents
- **Discrepancies:**
  - ❌ Action card subtitles don't match reference ("Submit a leave request" vs "2 days used this term")
  - ❌ Sub-tabs use VFilterChip instead of reference's sub-tab styling (active = on-surface/surface, NOT primary)
  - ❌ Progress ring: Compose uses custom drawBehind, reference has specific pc-ring styling with conic-gradient-like effect
  - ❌ Subject breakdown bars: verify syllabus-bar/syllabus-fill styling matches
  - ❌ Mark cards: Compose may not match exact mark-card layout
  - ❌ Homework cards: Compose may not match hw-card with pending/done styling
  - ❌ Report card: Compose may not match pc-metrics with pc-metric-bar layout

---

### 2.13 — Parent Fees Tab

**Reference (parent-portal.html lines 1174-1214):**

**Fees Hero:**
- Gradient: 140deg primary→#544AB8→#3D35A0
- Radial glow top-right
- Label (14px/500/white-70%), amount (40px/900/white), due date (14px/500/white-70%)
- Pay button: white bg, primary text, shape-full, 16px/700

**Fee Announcements:**
- Update items with "View Invoice" + "Download" action buttons

**Payment History:**
- Payment items: surface-container-lowest bg, shape-xl radius
  - Icon (44dp, tertiary-container bg, tertiary icon)
  - Title (15px/700), date (12px/500)
  - Amount (18px/900)
  - Press: scale 0.98 + shape morph

**Compose current (`ParentFeesScreen.kt`):**
- Has VFeesHeroCard, collection progress, announcements, payment history
- **Discrepancies:**
  - ❌ Fees hero: verify VFeesHeroCard matches exact gradient, radial glow, pay button styling
  - ❌ Compose adds "Collection Progress" card that is **NOT in the reference** — remove it
  - ❌ Payment items: Compose uses placeholder PaymentItem composable — verify it matches reference layout (44dp icon, 15px/700 title, 18px/900 amount)
  - ❌ Fee announcements: verify VUpdateCard matches reference update-item layout

---

### 2.14 — Parent Conversations Tab

**Reference (parent-portal.html lines 1217-1246):**

**Top bar:** Back button + "Conversations" title + compose/edit icon

**Segment selector:**
- 2 buttons: "Messages" (active) | "Announcements"
- Active: primary bg / on-primary text, Inactive: surface-container bg / on-surface-variant text
- 14px/600, shape-full radius, press scale 0.95

**Messages list:**
- Thread items: 16dp vertical padding, 20dp horizontal, border-bottom
  - Avatar (48dp circle, primary-container bg or teal or amber variants)
  - Name (15px/700), preview (13px/500, ellipsis)
  - Time (11px/500), unread badge (20dp, error bg, white text)

**Announcements:**
- Update items with colored avatars per type

**Compose current (`ParentConversationsScreen.kt`):**
- Has top bar, segment selector, messages list, announcements
- **Discrepancies:**
  - ❌ Segment selector uses VFilterChip — should use seg-btn styling (active = primary/on-primary, NOT on-surface/surface)
  - ❌ Thread rows: Compose uses ThreadRow composable — verify avatar is 48dp with color variants (primary-container, tertiary-container, amber)
  - ❌ Thread rows: verify unread badge is 20dp min-width, error bg, white text, 11px/800
  - ❌ Top bar: reference has back button + title + edit icon, Compose has title + search + compose icons
  - ❌ Announcements: verify VUpdateCard matches reference update-item layout with colored avatars

---

### 2.15 — Parent Profile Tab — **MAJOR DISCREPANCIES**

**Reference (parent-portal.html lines 1248-1367):**

**Profile Hero Card:**
- Gradient: 140deg primary→#544AB8→#3D35A0
- Radial glow top-right
- Avatar (64dp, glassmorphism), name (22px/800), class info (14px/500/70%), badge pill
- Level section: "Level 12 — Scholar" (14px/700) + XP text (12px/600/70%)
- XP bar: 8dp height, shape-full, white-15% bg, gradient fill (#00F5C4→#00BFA0)

**Stats Grid (2x2):**
- 4 stat cards: surface-container-lowest bg, shape-xl radius
  - Value (28px/900), label (12px/600 uppercase), trend (11px/600, tertiary color for up)
  - Press: scale 0.96 + shape morph

**Badges Row (horizontal scroll):**
- Badge cards (168dp width): earned vs locked styling
  - Earned: gradient bg (primary-container→surface-container-lowest), conic-gradient ring icon
  - Locked: surface-container-low bg, grayed icon
  - Badge name (14px/800), desc (11px/500), earned tag OR progress bar
  - Press: scale 0.96 + shape morph

**Account Options:**
- 4 rows: Account Settings, Link Another Child, Discover Schools, Logout (error color)
- Each: 40dp icon (surface-container-low bg), label (15px/600), chevron
- Press: scale 0.98 + shape morph

**Compose current (`ParentProfileScreen.kt`):**
- Has VProfileHeroCard, stat tiles, linked children, account settings, preferences, support, logout
- **CRITICAL Discrepancies:**
  - ❌ Profile hero shows PARENT info — reference shows CHILD info (Aarav Sharma, Class 8-B)
  - ❌ Stats grid: Compose has 3 tiles (Children, Attendance, Badges) — reference has 4 tiles (Attendance, Avg Marks, XP Points, Quizzes Done) in 2x2 grid
  - ❌ **Badges row is completely MISSING** — need to build horizontal scroll of badge cards with earned/locked states
  - ❌ Compose adds "Linked Children" section — NOT in reference profile tab
  - ❌ Compose adds "Preferences" and "Support" sections — NOT in reference
  - ❌ Account options: Compose has different rows (Personal Info, Security, Privacy, Notifications, Theme, Language, Help, Terms) — reference has only 4 (Account Settings, Link Another Child, Discover Schools, Logout)
  - ❌ XP bar: verify gradient fill matches (#00F5C4→#00BFA0)
  - ❌ Stat card layout: 28px/900 value, 12px/600 uppercase label, 11px/600 trend — Compose may use different sizes

---

### 2.16 — Bottom Navigation

**Reference (parent-portal.html lines 1380-1387):**
- 5 items: Home, Academics, Fees, Chats, Profile
- Active: primary color icon + label
- Fees has badge (1)
- Nav bar: surface bg, shadow

**Compose current:**
- 5 items: Home, Academics, Fees, Messages, Profile
- **Discrepancies:**
  - ❌ Label "Messages" should be "Chats"
  - ⚠️ Verify active state uses primary color
  - ⚠️ Verify badge styling matches (nav-badge: 18dp, error bg, white text)

---

### 2.17 — FAB Menu

**Reference (parent-portal.html lines 1372-1378):**
- FAB button: 56dp, primary bg, white + icon, shadow
- Expandable menu items: AI Tutor, Message Teacher, Apply Leave
- Menu items: surface-container-lowest bg, shape-xl, shadow, staggered entrance

**Compose current:**
- Has FabMenu with same 3 items
- **Discrepancies:**
  - ⚠️ Verify FAB is 56dp with primary bg and shadow
  - ⚠️ Verify menu items have staggered entrance animation
  - ⚠️ Verify menu item styling matches (icon + text, surface-container-lowest bg)

---

### 2.18 — Overlays

**Reference overlays (parent-portal.html lines 1389-1577):**
1. **Notifications** — update-item list with colored avatars
2. **Pulse Score** — progress ring (120dp) + metric bars
3. **Transport Tracking** — live update banner + route timeline + bus details
4. **AI Tutor** — chat bubbles (left/right) + chat input + send button
5. **Apply Leave** — form (dates, type tabs, reason textarea) + leave history
6. **Health Records** — health profile card + pulse score card
7. **Account Settings** — form (name, phone, email) + save button
8. **Link Another Child** — centered card with emoji + button
9. **Discover Schools** — search + filter chips + school cards with gradient headers
10. **School Detail** — gradient header + info + stats + facilities + admissions + actions

**Compose overlays:**
- Has: Notifications, Calendar, Scholarships, Leave, Messages, LinkChild, Discovery, Health, Pulse, Transport, TutorChat, TutorProgress, DigitalIdCard, Library, EventRegistration
- **Discrepancies:**
  - ❌ Overlay transition: reference uses translateX(100%→0) with emphasized easing — Compose should match
  - ❌ Overlay header: verify 20px/800 title, back button 44dp
  - ⚠️ Notifications: verify update-item layout matches
  - ⚠️ Pulse: verify progress ring + metric bars match
  - ⚠️ Transport: verify timeline with colored dots matches
  - ⚠️ AI Tutor: verify chat bubble styling (left: surface-container-low, right: primary), chat input (surface-container-high, shape-full), send button (44dp, primary)
  - ⚠️ Leave: verify form styling matches
  - ⚠️ Health: verify profile card layout matches
  - ⚠️ Account Settings: verify form matches
  - ⚠️ Discovery: verify school cards with gradient headers, rating, logo, tags, stats, action button match
  - ❌ School Detail overlay: **MISSING** in Compose — need to build with gradient header, info, stats, facilities, admissions, action buttons

---

## PART 3: Missing Screens Summary

| Screen | Reference | Compose Status | Priority |
|---|---|---|---|
| Signup | `index.html` page-signup | **MISSING** | Critical |
| Onboarding 1 | `index.html` page-onboarding | **MISSING** | Critical |
| Onboarding 2 | `index.html` page-onboarding2 | **MISSING** | Critical |
| Onboarding 3 | `index.html` page-onboarding3 | **MISSING** | Critical |
| School Select | `index.html` page-schoolselect | **LIKELY MISSING** | Critical |
| Done Screen | `index.html` page-done | **MISSING** | High |
| School Detail Overlay | `parent-portal.html` overlay-school-detail | **MISSING** | Medium |

---

## PART 4: Component-Level Discrepancies

### 4.1 — Hero Card
**Reference CSS:**
- `background: linear-gradient(140deg, var(--primary) 0%, #544AB8 50%, #3D35A0 100%)`
- `::before` radial gradient: 280px circle, rgba(205,189,255,0.25), top-right
- `::after` radial gradient: 240px circle, rgba(0,191,160,0.12), bottom-left
- `border-radius: var(--shape-2xl)` → morphs to `var(--shape-xl)` on press
- Live pill: `rgba(255,255,255,0.15)` bg, `backdrop-filter: blur(12px)`, pulsing dot
- Avatar: `rgba(255,255,255,0.2)` bg, 2px border `rgba(255,255,255,0.25)`

**Action:** Verify `VHeroCard.kt` implements all these exact effects. The `radialGlow` modifier must match the `::before` and `::after` positions and colors.

### 4.2 — Feature Cards (Priority Carousel)
**Reference CSS:**
- Card width: ~220dp (implicit from carousel)
- `border-radius: var(--shape-2xl)` → morphs to `var(--shape-xl)` on press
- Colored bg per type: primary-container, tertiary-container, warmOrangeContainer, secondary-container
- Icon: 44dp circle, colored bg
- Title: 18px/800/-0.025em
- Amount: 30px/900/-0.04em
- Badge: 13px/700, shape-full pill

**Action:** Verify `FeatureCard` composable matches all these specs.

### 4.3 — Schedule Cards
**Reference CSS:**
- `surface-container-lowest` bg, `shape-xl` radius
- Time: 18px/800 (hr) + 12px/600 (am/pm)
- Vertical divider: 1px, surface-container
- Subject: 15px/700, Teacher: 13px/500
- Live badge: pulsing dot (8dp, #00F5C4, animated ring) + "Live" text
- Next badge: primary bg pill

**Action:** Verify `ScheduleCard` composable matches.

### 4.4 — Progress Ring
**Reference CSS:**
- 120dp outer, 100dp inner
- 10dp stroke
- Conic gradient: primary 0% → primary 100% (or tertiary for attendance)
- Center text: 28px/900

**Action:** Verify Compose progress ring implementation matches. The reference uses `conic-gradient` which in Compose requires `drawArc` with `SweepGradient`.

### 4.5 — Badge Cards
**Reference CSS:**
- 168dp width, shape-xl radius
- Earned: gradient bg (primary-container→surface-container-lowest), conic-gradient ring (primary→tertiary→primary)
- Locked: surface-container-low bg, grayed icon
- Name: 14px/800, Desc: 11px/500, Earned tag: 10px/800 uppercase tertiary
- Progress: 5dp bar, shape-full

**Action:** Build badge card composable — **currently missing**.

### 4.6 — School Cards (Discovery)
**Reference CSS:**
- `shape-2xl` radius, overflow hidden
- Header: 140dp height, gradient bg per school, dark overlay gradient, rating pill, logo
- Body: name (17px/800), address (13px/500), tags (11px/700 pills), stats row, action button
- Press: scale 0.98 + shape morph

**Action:** Verify school card composable matches.

### 4.7 — Chat Bubbles (AI Tutor)
**Reference CSS:**
- Left bubble: surface-container-low bg, on-surface text, border-bottom-left-radius: shape-sm
- Right bubble: primary bg, on-primary text, border-bottom-right-radius: shape-sm
- Text: 14px/500, line-height 1.5, max-width 80%
- Time: 11px/500/outline

**Action:** Verify chat bubble composables match.

---

## PART 5: Interaction & Animation Discrepancies

### 5.1 — Press Interactions
**Reference pattern (universal):**
- `:active { transform: scale(0.9X); border-radius: <larger shape>; }`
- Duration: `var(--dur-short-2)` (150ms)
- Easing: `var(--ease-emphasized)` or `var(--ease-standard)`

**Compose:** Uses `pressScale` + `shapeMorph` modifiers.
**Action:** Verify ALL interactive elements have both press-scale AND shape-morph. The reference applies both simultaneously on every tappable element.

### 5.2 — Page Transitions
**Reference:**
- Enter: `translateX(100%→0)` + `opacity(0→1)`, 500ms, emphasized easing
- Exit: `translateX(0→-30%)` + `opacity(1→0)`, 500ms

**Compose:** Uses `AnimatedContent` with `fadeIn/fadeOut`.
**Discrepancy:** ❌ Compose uses only fade — reference uses **slide + fade**. Update to `slideInHorizontally + fadeIn` / `slideOutHorizontally + fadeOut`.

### 5.3 — Overlay Transitions
**Reference:**
- `translateX(100%→0)`, 300ms, emphasized easing

**Compose:** May use fade only.
**Action:** Update to `slideInHorizontally(initialOffsetX = { it })` with 300ms.

### 5.4 — Staggered Entrance
**Reference:** Elements animate in with staggered delays (0, 30, 60, 100, 150, 200, 250, 300ms).
**Compose:** Uses `VStaggeredItem` with delays.
**Action:** Verify staggered delays are applied to ALL sections on home tab and other screens.

### 5.5 — Live Pulse Animation
**Reference:** `@keyframes livePulse` — box-shadow ring 4px→10px, 2s infinite.
**Compose:** `rememberLivePulse()` returns ringScale + ringAlpha.
**Action:** Verify live pulse is used on: hero card live dot, schedule live badge, landing eyebrow dot.

### 5.6 — Auth Animation
**Reference:** `anim-1` through `anim-3` with staggered delays (100, 200, 300ms).
- `slideUp` keyframe: translateY(24→0) + opacity(0→1), 600ms, emphasized

**Compose:** Verify auth screens use staggered slide-up entrance.

---

## PART 6: Implementation Priority Order

### Phase 1: Missing Screens (Critical)
1. Build `SignupScreen.kt`
2. Build `OnboardingScreen.kt` (reusable for 3 screens with data params)
3. Build `SchoolSelectScreen.kt`
4. Build `DoneScreen.kt`
5. Wire all into `NavGraphV2.kt` navigation flow

### Phase 2: Profile Tab Overhaul (Critical)
1. Change profile hero to show CHILD info (name, class, badge)
2. Replace 3 stat tiles with 4-tile 2x2 grid (Attendance, Avg Marks, XP Points, Quizzes Done)
3. Build badge cards horizontal scroll (earned + locked states)
4. Replace account options with 4 reference rows
5. Remove "Linked Children", "Preferences", "Support" sections

### Phase 3: Home Tab Polish (High)
1. Fix greeting to show gradient accent on child's name
2. Fix live update banner to match reference (surface-container-lowest, clock icon)
3. Fix feature cards: exact 220dp width, colored backgrounds, icon/title/subtitle/amount/badge layout
4. Fix quick stats: 3 cards with icon/value/label
5. Fix schedule cards: live/next badge styling
6. Fix school updates: update-item layout with action buttons

### Phase 4: Academics Tab Polish (High)
1. Fix action card subtitles to match reference
2. Fix sub-tabs to use reference styling (on-surface/surface active, NOT primary)
3. Fix progress ring to match reference (conic gradient, 120dp, 10dp stroke)
4. Fix subject breakdown bars
5. Fix mark cards layout
6. Fix homework cards with pending/done styling
7. Fix report card with metric bars

### Phase 5: Fees Tab Polish (Medium)
1. Remove "Collection Progress" card (not in reference)
2. Fix fees hero to match exact reference styling
3. Fix payment items to match reference layout

### Phase 6: Conversations Tab Polish (Medium)
1. Fix segment selector to use primary/on-primary active state
2. Fix thread rows: 48dp avatars with color variants, unread badges
3. Fix top bar to match reference (back + title + edit icon)

### Phase 7: Overlay Polish (Medium)
1. Build School Detail overlay
2. Fix overlay transitions to use slide-in
3. Verify all overlay content matches reference

### Phase 8: Navigation & Shell Polish (Low)
1. Fix bottom nav label "Messages" → "Chats"
2. Fix page transitions to use slide + fade
3. Verify FAB menu styling and animations
4. Verify top bar and search field styling

### Phase 9: Auth Screen Polish (Low)
1. Fix staff login button to use tertiary color
2. Fix staff social buttons to "School SSO" + "Google Workspace"
3. Verify all auth form styling matches reference
4. Add staggered entrance animations

---

## PART 7: Key CSS-to-Compose Mapping Reference

| CSS Property | Compose Equivalent |
|---|---|
| `linear-gradient(140deg, ...)` | `Brush.linearGradient(colors = listOf(...))` |
| `radial-gradient(circle, color 0%, transparent 50%)` | `radialGlow()` modifier or `Brush.radialGradient` |
| `backdrop-filter: blur(12px)` | Use `GlassWhite15`/`GlassWhite12` color (approximation) |
| `rgba(255,255,255,0.15)` | `VColors.GlassWhite15` |
| `border-radius: var(--shape-2xl)` | `clip(VShapes.TwoXl)` |
| `:active { transform: scale(0.97) }` | `pressScale(interactionSource, 0.97f)` |
| `:active { border-radius: var(--shape-xl) }` | `shapeMorph(interactionSource, TwoXlDp, XlDp, DurShort2)` |
| `transition: all var(--dur-short-2)` | `tween(VMotion.DurShort2)` |
| `@keyframes livePulse` | `rememberLivePulse()` |
| `@keyframes slideUp` | `slideInVertically(initialOffsetY = { 24.dp.toPx().toInt() }) + fadeIn()` |
| `text-transform: uppercase` | `TextStyle(textTransform = TextTransform.Uppercase)` |
| `letter-spacing: -0.03em` | `letterSpacing = (-0.03).em` |
| `overflow-y: auto; scrollbar-width: none` | `verticalScroll(rememberScrollState())` |
| `flex: 1` | `Modifier.weight(1f)` |
| `gap: 12px` | `Arrangement.spacedBy(12.dp)` |

---

## PART 8: Verification Checklist

For each screen, verify against the reference HTML:

- [ ] Background color matches exactly
- [ ] All gradient backgrounds match angle + stops
- [ ] All radial glow positions and colors match
- [ ] All text sizes, weights, letter-spacings match
- [ ] All shapes/border-radius match
- [ ] All padding/margins match
- [ ] All icon sizes match
- [ ] All press interactions have scale + shape morph
- [ ] All entrance animations have correct staggered delays
- [ ] All overlay transitions use slide-in
- [ ] All page transitions use slide + fade
- [ ] All live pulse animations are present
- [ ] All glassmorphism effects use correct opacity values
- [ ] All shadow effects match
- [ ] All border widths and colors match
- [ ] All badge/pill styling matches
- [ ] All divider styling matches
- [ ] All form input styling matches (border, focus state, placeholder)
- [ ] All button styling matches (radius, press interaction, text style)
- [ ] All scroll behavior is smooth with no scrollbar

---

## EXECUTION INSTRUCTIONS

1. **Start with Phase 1** (missing screens) — these are the most critical gaps
2. **Then Phase 2** (profile tab) — this has the most discrepancies
3. **Then Phase 3-4** (home + academics) — these are the most visible tabs
4. **Then Phase 5-8** — polish remaining tabs and overlays
5. **After each phase**, build and verify the app compiles
6. **After all phases**, do a visual side-by-side comparison with the reference HTML files opened in a browser

**Every single element must match the reference pixel-for-pixel. No approximations. No "close enough". The target is 100% visual identity with the premium reference design.**
