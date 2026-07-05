# ═══════════════════════════════════════════════════════════════════════════
# GOD MODE PROMPT — PARENT PORTAL PREMIUM REBUILD
# ═══════════════════════════════════════════════════════════════════════════
#
# ┌─────────────────────────────────────────────────────────────────────────┐
# │  YOU ARE THE GOD OF UI/UX.                                             │
# │                                                                         │
# │  You don't just "write Compose code." You DESIGN experiences.           │
# │  Every screen you ship is a masterpiece — the kind of screen that       │
# │  makes a parent stop scrolling and think: "Wow, this feels premium."    │
# │                                                                         │
# │  You are NOT an AI code generator. You are a craftsman who happens      │
# │  to use AI tools. The difference? A craftsman CARES about every pixel,  │
# │  every animation curve, every shadow, every haptic, every empty state.  │
# │  A code generator just dumps composables and moves on.                  │
# │                                                                         │
# │  This is a $100M valuation startup. Our parents are Indian parents      │
# │  who may be using a smartphone for the first time. The UI must be:      │
# │  • INSTINCTIVE — a parent should never wonder "what do I tap?"          │
# │  • BEAUTIFUL — warm, confident, never clinical or cold                  │
# │  • ALIVE — every interaction has a response (scale, morph, glow)        │
# │  • HONEST — no fake data, no placeholder text, no broken buttons        │
# │  • FAST — animations are 200-400ms, never sluggish, never instant       │
# └─────────────────────────────────────────────────────────────────────────┘
#
# ───────────────────────────────────────────────────────────────────────────
# THE DESIGN BIBLE — WHAT "PREMIUM" ACTUALLY MEANS HERE
# ───────────────────────────────────────────────────────────────────────────
#
# Premium is NOT:
#   ✗ Gradients everywhere (that's Instagram 2018, not M3 2025)
#   ✗ Glassmorphism on everything (that's iOS 14, not craft)
#   ✗ Rounded corners on everything at the same radius (that's lazy)
#   ✗ Drop shadows on every card (that's Material 2, we're past that)
#   ✗ Animations on every element (that's distracting, not delightful)
#   ✗ "Beautiful" empty states with illustrations nobody looks at
#
# Premium IS:
#   ✓ TONAL SURFACES — M3 surface container elevation creates depth
#     without shadows. SurfaceContainerLowest > SurfaceContainerLow >
#     SurfaceContainer > SurfaceContainerHigh. Use this hierarchy.
#   ✓ PURPOSEFUL COLOR — VColors.Primary for ONE primary action per screen.
#     VColors.Tertiary for secondary. VColors.WarmOrange for warnings.
#     Don't rainbow-blast. One accent per screen section.
#   ✓ SHAPE LANGUAGE — VShapes.Xl (24dp) for cards, VShapes.Lg (16dp) for
#     inner elements, VShapes.Full for pills/badges. Consistent.
#   ✓ MOTION WITH MEANING — pressScale on every tappable card (0.97-0.98).
#     shapeMorph on press (Xl → TwoXl). VStaggeredItem for entrance (0-300ms
#     cascade). NOT animation for animation's sake.
#   ✓ TYPOGRAPHY HIERARCHY — VTypography.GreetingTitle for hero text.
#     SectionHeader for section titles. UpdateTitle for card titles.
#     UpdateText for body. NavLabel for captions. Never hardcode fontSize.
#   ✓ WHITESPACE — 20-24dp horizontal padding is the standard. 16-24dp
#     between sections. 8-12dp between items in a list. Don't cram.
#   ✓ STATE COMPLETENESS — every screen has 4 states:
#     1. LOADING — skeleton cards (SkeletonCard) with shimmer, NOT "Loading..."
#     2. ERROR — ErrorStateCard with icon + message + "Retry" button
#     3. EMPTY — EmptyStateCard with icon + friendly message + action button
#     4. LOADED — the actual content, polished and complete
#
# ───────────────────────────────────────────────────────────────────────────
# THE SELF-REVIEW RITUAL — MANDATORY AFTER EVERY SCREEN
# ───────────────────────────────────────────────────────────────────────────
#
# After completing EACH screen in EACH iteration, you MUST stop and ask
# yourself these questions OUT LOUD (write the answers as comments at the
# bottom of the file before finalizing):
#
#   ┌─ SELF-REVIEW: [Screen Name] ────────────────────────────────────────┐
#   │                                                                       │
#   │ 1. DOES THIS LOOK GOOD?                                               │
#   │    Be honest. Would you show this to a designer at Google and be      │
#   │    proud? Or would you be embarrassed? If embarrassed, FIX IT NOW.    │
#   │                                                                       │
#   │ 2. Is there ONE clear primary action on this screen?                  │
#   │    A parent should know what to do within 2 seconds.                  │
#   │    If there are 3 equally-weighted buttons, you failed.               │
#   │                                                                       │
#   │ 3. Does every tappable element respond to touch?                      │
#   │    pressScale + shapeMorph. No exceptions. No bare clickables.        │
#   │                                                                       │
#   │ 4. Are the 4 states (loading/error/empty/loaded) all implemented?     │
#   │    Not just the happy path. ALL FOUR.                                 │
#   │                                                                       │
#   │ 5. Is the spacing consistent?                                         │
#   │    20-24dp horizontal padding. 16-24dp between sections.              │
#   │    8-12dp between list items. No random 7dp or 13dp.                  │
#   │                                                                       │
#   │ 6. Are all colors from VColors? All shapes from VShapes?              │
#   │    All text from VTypography? No hardcoded values?                    │
#   │                                                                       │
#   │ 7. Does the screen have 140dp bottom padding?                         │
#   │    Bottom nav overlaps content without it.                            │
#   │                                                                       │
#   │ 8. Is there staggered entrance animation?                             │
#   │    VStaggeredItem with 0-300ms cascade. Content shouldn't pop in.     │
#   │                                                                       │
#   │ 9. Are there any empty onClick = { } handlers?                        │
#   │    Every clickable must be wired or have a TODO(comment) explaining   │
#   │    what it should do.                                                 │
#   │                                                                       │
#   │ 10. WOULD AN INDIAN PARENT WHO HAS NEVER USED A SMARTPHONE            │
#   │     UNDERSTAND THIS SCREEN?                                           │
#   │     If the answer is "maybe," you need to simplify.                   │
#   │     Clear labels in plain language. Obvious buttons. No jargon.       │
#   │                                                                       │
#   │ 11. IS EVERY BUTTON, OPTION, AND ACTION FULLY VISIBLE ON SCREEN?      │
#   │     No clipping. No overflow. No button hidden behind the bottom nav. │
#   │     Test mentally on a 360x640dp screen. If a "Submit" button is      │
#   │     at the very bottom and the bottom nav covers it, the parent       │
#   │     CANNOT submit. That's a broken screen. Fix it.                    │
#   │     Use weight(1f) + scroll, use 140dp bottom padding, use            │
#   │     fillMaxHeight with nested scroll. NEVER assume the screen is      │
#   │     tall enough. God sees every screen size — so should you.          │
#   │                                                                       │
#   │ If ANY answer is "no" or "maybe," DO NOT move to the next screen.     │
#   │ Fix it. Recompile. Re-review. Only proceed when ALL 11 answers are    │
#   │ a confident "YES."                                                    │
#   └───────────────────────────────────────────────────────────────────────┘
#
# ───────────────────────────────────────────────────────────────────────────
# ITERATION WORKFLOW — HOW TO WORK
# ───────────────────────────────────────────────────────────────────────────
#
# Each iteration = ONE screen or ONE tightly-related group of screens.
#
# STEP 1: Read the architecture spec for this screen (Part C in the doc).
# STEP 2: Read the current implementation.
# STEP 3: Identify what's missing/broken (check the gap analysis below).
# STEP 4: Write the code. Use tokens. Use components. Use modifiers.
# STEP 5: Compile. Fix errors.
# STEP 6: Run the SELF-REVIEW RITUAL (all 10 questions).
# STEP 7: If any answer is "no" → go back to STEP 4 and fix.
# STEP 8: Mark iteration complete. Move to next.
#
# Do NOT batch multiple screens in one iteration. One screen. One masterpiece.
# One perfect screen is worth more than ten half-baked ones.
#
# ───────────────────────────────────────────────────────────────────────────
# BEFORE YOU START — READ THESE
# ───────────────────────────────────────────────────────────────────────────
#
# 1. This entire file — understand every gap, every fix, every spec.
# 2. ENROLLPLUS_SCREEN_ARCHITECTURE_FINAL.md — Part C (C-0 through C-6.16)
# 3. ENROLLPLUS_RESTRUCTURE_CHANGELOG_AND_BACKEND_MAPPING.md — all entries
# 4. VColors.kt, VShapes.kt, VTypography.kt, VMotion.kt — know your tokens
# 5. ui/v2/components/ — know your building blocks (30+ components)
# 6. The HTML reference (parent-portal.html) is a ROUGH MOCK — NOT the target.
#    The architecture docs are the SOURCE OF TRUTH for features.
#    Your design quality must EXCEED the HTML, not match it.
#
# ───────────────────────────────────────────────────────────────────────────
# TOKENS — NEVER HARDCODE
# ───────────────────────────────────────────────────────────────────────────
#
# COLORS — VColors.* (M3 tonal palette, @Composable get()):
#   Primary / OnPrimary / PrimaryContainer / OnPrimaryContainer
#   Secondary / OnSecondary / SecondaryContainer / OnSecondaryContainer
#   Tertiary / OnTertiary / TertiaryContainer / OnTertiaryContainer
#   Error / OnError / ErrorContainer / OnErrorContainer
#   Surface / SurfaceDim / SurfaceBright
#   SurfaceContainerLowest / SurfaceContainerLow / SurfaceContainer /
#   SurfaceContainerHigh / SurfaceContainerHighest
#   OnSurface / OnSurfaceVariant / Outline / OutlineVariant
#   WarmOrange / WarmOrangeContainer / LiveCyan / GlassWhite20
#   PrimaryMid / PrimaryDeep / TertiaryDeep / Scrim
#
# SHAPES — VShapes.* (corner radii):
#   Xs=4dp, Sm=8dp, Md=12dp, Lg=16dp, Xl=24dp, TwoXl=28dp, Full=999dp
#   Also: XsDp, SmDp, MdDp, LgDp, XlDp, TwoXlDp for animateDpAsState targets
#
# TYPOGRAPHY — VTypography.* (text styles):
#   GreetingTitle, GreetingEyebrow, SectionHeader, SectionLink
#   FeatureTitle, FeatureSubtitle, FeatureAmount, FeatureBadge
#   UpdateTitle, UpdateText, NavLabel
#   StatValue, HwTitle, HwSub, HwStatus
#   ScheduleHour, ScheduleAmPm, ScheduleSubject, ScheduleTeacher, ScheduleStatus
#   SyllabusName, SyllabusPct
#
# MOTION — VMotion.* (animation specs):
#   DurShort1, DurShort2, DurMedium1, DurMedium2, DurLong1
#   Use with tween(), spring(), AnimatedContent transitionSpec
#
# MODIFIERS — tactile feedback:
#   pressScale(interactionSource, pressedScale = 0.97f) — scale down on press
#   shapeMorph(interactionSource, fromShape, toShape, duration) — morph shape on press
#   radialGlow(offsetX, offsetY, radius, color) — subtle glow effect
#   VStaggeredItem(delayMs) — staggered entrance animation
#
# COMPONENTS — premium building blocks:
#   SkeletonCard, ErrorStateCard, EmptyStateCard — state components
#   VPullRefreshPremium — pull to refresh wrapper
#   VChartsPremium — charts (line, bar, heatmap)
#   VShimmerPremium — shimmer effect for skeletons
#   VHeroCard, VFeesHeroCard, VProfileHeroCard — hero cards
#   VQuickStatCard, VUpdateCard — content cards
#   VFilterChip, VTopTabsPremium — navigation
#   VPrimaryButton, VSecondaryButton — buttons
#   VTextInput — form input
#   VSectionHeader, VGreetingTitle, VGreetingEyebrow — typography
#   VConfirmDialogPremium, VSnackbarPremium — feedback
#   VDatePickerPremium, VTimePickerPremium — pickers
#   VLanguagePickerPremium, VThemePickerPremium — settings pickers
#   VAvatarPremium, VBrandLogoPremium — branding
#   ParentOverlayScaffold — overlay scaffold (back nav + scroll + 140dp pad)
#
# ───────────────────────────────────────────────────────────────────────────
# CRITICAL RULES — ZERO TOLERANCE
# ───────────────────────────────────────────────────────────────────────────
#
# 1. NEVER hardcode Color(0x...) — always VColors.*
# 2. NEVER hardcode dp for corner radii — always VShapes.*
# 3. NEVER hardcode FontWeight/fontSize — always VTypography.*
# 4. NEVER use indication = null without pressScale + shapeMorph
# 5. EVERY card → pressScale + shapeMorph (0.97-0.98 scale, Xl→TwoXl morph)
# 6. EVERY screen → 4 states (loading skeleton, error card, empty card, loaded)
# 7. EVERY scrollable → 140dp bottom padding (dock clearance)
# 8. EVERY overlay → ParentOverlayScaffold (consistent back nav)
# 9. EVERY form → validation + submitting + success + error states
# 10. NEVER leave onClick = { } empty — wire or TODO(comment)
# 11. AutoMirrored icons for directional icons (ArrowBack, Message, etc.)
# 12. Compile after EACH screen — fix errors immediately
# 13. Run SELF-REVIEW RITUAL after EACH screen — all 10 must be "YES"
# 14. NEVER use Color.White or Color.Black — use VColors.OnSurface, VColors.Surface
# 15. NEVER use FontWeight.Bold without VTypography reference — use .copy(fontWeight = ...)
# 16. NEVER let content overflow or clip off-screen — every button, option, and
#     action MUST be fully visible and tappable within the viewport. No hidden
#     buttons behind the bottom nav. No cut-off cards. No content requiring the
#     parent to guess where a button is. If it doesn't fit, restructure the layout
#     — don't shrink it illegibly, don't clip it silently. Use scroll, use weight,
#     use Spacer, use fillMaxHeight carefully. TEST mentally on small screens (360dp).
# 17. NEVER use fixed height for content areas that might grow — use weight(1f)
#     + verticalScroll, or fillMaxHeight with nested scroll. Fixed heights cause
#     overflow on different screen sizes.
# 18. ALWAYS test layout mentally on smallest common device: 360x640dp.
#     If a button is at y=620dp and bottom nav starts at y=640dp, it's INVISIBLE.
#     The 140dp bottom padding exists for this reason — USE IT.
#
# ═══════════════════════════════════════════════════════════════════════════
# GAP ANALYSIS — WHAT'S BROKEN, MISSING, OR LOW QUALITY
# ═══════════════════════════════════════════════════════════════════════════

## SECTION 1: STRUCTURAL GAPS — Missing Screens & Broken Wiring

### 1.1 — Home Tab: Missing Feature Card Grid (10 cards)
**Status:** CRITICAL GAP
**Architecture spec (C-1):** Home must have a 2-up feature card grid with 10 cards:
1. Fees — balance + "Pay" → Fees tab
2. Academics — latest mark → Academics tab
3. Messages — unread count → Conversations tab
4. Pulse — health score → Pulse overlay
5. Transport — bus status → Transport overlay
6. Tutor — AI tutor status → TutorChat overlay
7. Scholarships — count available → Scholarships overlay
8. ID Card — "View" → DigitalIdCard overlay
9. Library — borrowed books count → Library overlay
10. Events — upcoming count → EventRegistration overlay

**Current:** Only 4 carousel cards (Fees, Attendance, Homework, Messages) + 3 quick stat cards.
**Fix:** Replace the 4-card carousel with the full 10-card 2-up grid. Each card: icon + label + key metric + tap → overlay/tab. Use `VQuickStatCard` or build a `FeatureGridCard` composable. Wire all 10 callbacks in `ParentPortalShell.kt`.

### 1.2 — Home Tab: Missing Child Switcher Dropdown
**Status:** CRITICAL GAP
**Architecture spec (C-1):** Child switcher header (dropdown: child name + class + avatar — if multiple children).
**Current:** No child switcher on Home. Shell has a search field with child initial avatar but no dropdown.
**Fix:** Add `ChildSwitcherDropdown` composable to Home tab. Shows current child name + class + avatar. Tapping opens dropdown menu of all linked children. Selecting switches all home data.

### 1.3 — Home Tab: Missing Hero Card with Journey Progress
**Status:** PARTIAL GAP
**Architecture spec (C-1):** Hero card with child identity card with journey progress ring, level, XP, house badge. Gamified "player card" aesthetic.
**Current:** VHeroCard with stats (attendance, status, pending) but no progress ring, no level/XP, no house badge.
**Fix:** Enhance VHeroCard or create `ParentHeroCard` with: child avatar (large), name, class, house badge, journey progress ring (level, XP bar). Tap → Profile tab.

### 1.4 — Home Tab: Missing School-Day Timeline
**Status:** PARTIAL GAP
**Architecture spec (C-1):** School-day timeline (today's periods with status). Current period highlighted.
**Current:** Has "Today's Schedule" with ScheduleCard list + progress bar. Close but needs: teacher name per period (has it), status icon (upcoming/done — has LIVE/NEXT/DONE badges). Mostly OK but needs polish.
**Fix:** Minor polish — ensure timeline visual matches premium standard. Add staggered entrance per card.

### 1.5 — Academics Tab: Missing Quizzes Sub-tab
**Status:** CRITICAL GAP
**Architecture spec (C-2):** 7 sub-tabs: Overview · Attendance · Marks · Syllabus · Quizzes · Homework · Report.
**Current:** 6 sub-tabs (Overview, Attendance, Marks, Syllabus, Homework, Report). Missing "Quizzes".
**Fix:** Add "Quizzes" sub-tab between Syllabus and Homework. Wire to `ParentQuizzesScreen` content (already exists as overlay screen, needs to be embedded as sub-tab content). Quiz list: title, subject, date, score, status badge. Tap completed quiz → quiz result detail → leaderboard.

### 1.6 — Academics Tab: Missing Attendance Calendar
**Status:** CRITICAL GAP
**Architecture spec (C-2):** `ParentAttendanceCalendar` (monthly calendar with color-coded days: green=present, red=absent, yellow=late) + summary card + trend chart.
**Current:** Just a progress ring card + breakdown bars. No calendar visualization, no trend chart.
**Fix:** Build `AttendanceCalendar` composable — monthly grid with color-coded days. Tap day → detail. Swipe to change month. Use `VDatePickerPremium` calendar grid as reference. Add 6-month trend chart using `VChartsPremium`.

### 1.7 — Academics Tab: Missing Syllabus Expandable Units
**Status:** PARTIAL GAP
**Architecture spec (C-2):** Subject-wise syllabus coverage cards. Expandable to show unit list with status. Tap unit → sub-topic detail.
**Current:** Flat list of syllabus rows with progress bars. No expand/collapse, no unit breakdown.
**Fix:** Make syllabus cards expandable. Tap card → expands to show unit list with completion status. Use animateContentSize for smooth expand/collapse.

### 1.8 — Academics Tab: Marks Sub-tab Missing Subject Filter + Trend Chart
**Status:** PARTIAL GAP
**Architecture spec (C-2):** Assessment list with subject filter chips + trend chart for selected subject.
**Current:** Flat list of mark rows. No filter chips, no trend chart.
**Fix:** Add horizontal scrollable subject filter chips at top. Add trend chart below list showing marks over time for selected subject.

### 1.9 — Academics Tab: Report Sub-tab Missing AI Report Card
**Status:** PARTIAL GAP
**Architecture spec (C-2):** `AiReportCardPreview` (AI-enhanced report card with insights, strengths, improvement areas). Download button.
**Current:** Basic term report card with subject bars + grades + teacher remarks. No AI insights, no download.
**Fix:** Add AI insights section: strengths, improvement areas, AI-generated summary. Add "Download" button. Use `VChartsPremium` for visual grade breakdown.

### 1.10 — Conversations Tab: Missing Compose New Message
**Status:** GAP
**Architecture spec (C-4):** "Compose new" → thread picker (select teacher/admin to message).
**Current:** Only thread list. No compose new button or thread picker.
**Fix:** Add compose FAB or compose button in Messages segment. Opens thread picker (list of teachers/admins). Select → opens compose view.

### 1.11 — Conversations Tab: Announcements Segment is Hardcoded
**Status:** GAP
**Architecture spec (C-4):** `ParentActivityScreenV2` — announcement feed from API.
**Current:** Two hardcoded VUpdateCard announcements (Principal + School Office).
**Fix:** Wire to announcements ViewModel/API. Show real announcement feed with loading/empty/error states. Each card: title, category badge, date, description preview, school name.

### 1.12 — Profile Tab: Missing Swipe-Down Account Options Reveal
**Status:** GAP
**Architecture spec (C-5):** Gamified "player card" aesthetic. Swipe-down reveals account options panel.
**Current:** Account options are always visible as flat rows below badges. No swipe gesture, no reveal animation.
**Fix:** Implement swipe-down gesture on player card that reveals account options panel with slide animation. Use `Modifier.draggable` or `SwipeableState`. Panel contains: Account Settings, Link Another Child, Discover Schools, Logout.

### 1.13 — Profile Tab: Missing Tap Stat → Detail Navigation
**Status:** GAP
**Architecture spec (C-5):** Tap stat → detail view (e.g., attendance → Academics Attendance tab).
**Current:** Stat cards have empty onClick = { }.
**Fix:** Wire stat card taps to navigate: Attendance → Academics tab (Attendance sub-tab), Avg Marks → Academics (Marks sub-tab), XP → Profile detail, Quizzes → Academics (Quizzes sub-tab).

### 1.14 — Profile Tab: Missing Badge Detail View
**Status:** GAP
**Architecture spec (C-5):** Tap unlocked badge → achievement detail.
**Current:** Badge cards have empty onClick = { }.
**Fix:** Tap earned badge → show achievement detail (badge icon, name, description, earned date, confetti animation). Use `VDialog` or full-screen overlay.

### 1.15 — Account Settings Overlay: Missing Fields
**Status:** GAP
**Architecture spec (C-6.04):** Parent profile form (name, phone, email, address, alternate contact) + child list with unlink option + notification preferences + language preference + theme switch + change password + logout.
**Current:** Only name, phone, email fields + Save button. Missing: address, alternate contact, child list with unlink, notification preferences, language preference, theme switch, change password, logout.
**Fix:** Add all missing sections. Use `VTextInput` for form fields. Add `VLanguagePickerPremium` for language. Add `VThemePickerPremium` for theme. Add child list with unlink option. Add notification preference toggles. Add change password section. Add logout button.

### 1.16 — Tutor Chat Overlay: Missing Plan + Practice Tabs
**Status:** CRITICAL GAP (Changelog Entry #18)
**Architecture spec (C-6.12):** `TutorChatScreen` with VTopTabs (Chat · Plan · Practice). Quick suggestion chips. Subject selector. "View Progress" button → TutorProgress overlay. Typing indicator.
**Current:** Single chat interface only. No Plan tab, no Practice tab, no quick suggestion chips, no subject selector, no typing indicator, no View Progress button.
**Fix:** Add VTopTabs (Chat · Plan · Practice). Embed TutorPlanScreen and TutorPracticeScreen as tab content (check if these exist as orphaned files — if so, wire them; if not, create them). Add quick suggestion chips above input. Add subject selector dropdown. Add typing indicator animation. Add "View Progress" button → TutorProgress overlay.

### 1.17 — Tutor Chat Overlay: Missing Typing Indicator + Send State
**Status:** GAP
**Architecture spec (C-6.12):** Typing indicator. Sending state.
**Current:** No typing indicator, no sending state, send button has no onClick.
**Fix:** Add typing indicator (three animated dots) when AI is "typing". Add sending state (disable input + show spinner). Wire send button to add message + simulate AI response (or real API).

### 1.18 — Pulse Overlay: Missing AI Recommendations + Trend Chart
**Status:** PARTIAL GAP
**Architecture spec (C-6.10):** Pulse score gauge → risk factor breakdown → trend chart → AI recommendations → "View Details" per factor.
**Current:** Score ring + 4 metric bars. No trend chart, no AI recommendations, no per-factor detail.
**Fix:** Add 6-month trend chart (VChartsPremium). Add AI recommendations card (text-based insights). Make each metric bar tappable → detail view.

### 1.19 — Transport Overlay: Missing Live Map + Boarding Status
**Status:** PARTIAL GAP
**Architecture spec (C-6.11):** Live bus tracking map + child boarding status (Boarded/Not Boarded with timestamp) + route details.
**Current:** Timeline stops + bus details. No map, no boarding status.
**Fix:** Add boarding status card (Boarded/Not Boarded + timestamp). Add placeholder map area (can be static image for now — mark with TODO for real map integration). Add real-time ETA updates indicator.

### 1.20 — Health Overlay: Missing Pulse Integration + Recommendations
**Status:** PARTIAL GAP
**Architecture spec (C-6.09):** Child health profile + `ParentPulseScreen` (Pulse score gauge, risk factors, recommendations, trend chart).
**Current:** Health profile rows + pulse score ring. Missing: risk factors, recommendations, trend chart.
**Fix:** Add risk factors section. Add AI recommendations card. Add trend chart. Link to full Pulse overlay for detailed view.

### 1.21 — Library Overlay: Missing 9 Sub-tabs
**Status:** CRITICAL GAP
**Architecture spec (C-6.15):** 9 sub-tabs: Dashboard · Browse · Borrowed · Reservations · History · Fines · Categories · Search · Settings.
**Current:** Need to verify current implementation.
**Fix:** Implement all 9 sub-tabs with proper content for each. Dashboard = overview stats. Browse = catalog search + filter. Borrowed = current loans with due dates. Reservations = pending reservations. History = past loans. Fines = outstanding/paid fines. Categories = browse by category. Search = full-text search. Settings = library preferences.

### 1.22 — Event Registration Overlay: Missing PTM Slot Booking
**Status:** PARTIAL GAP
**Architecture spec (C-6.16):** Event list → event detail + "Register" button → registration confirmation. PTM slot booking.
**Current:** Need to verify. Likely basic event list.
**Fix:** Add PTM slot booking flow: select date → select time slot → confirm booking. Add registration status badges. Add "Registered" success state.

### 1.23 — Leave Overlay: Missing Child Selector + Document Upload
**Status:** PARTIAL GAP
**Architecture spec (C-6.05):** Child selector if multiple, from date, to date, reason, type — Sick/Casual/Other, supporting document upload optional. Leave history list.
**Current:** Has form with dates, type, reason, submit. Missing: child selector (if multiple children), document upload.
**Fix:** Add child selector dropdown (if >1 child). Add document upload button (file picker — mark with TODO for platform-specific implementation). Wire submit to real API.

### 1.24 — Scholarships Overlay: Missing Application Form
**Status:** PARTIAL GAP
**Architecture spec (C-6.03):** Available scholarship schemes list. Each: name, eligibility, amount, deadline, status. "Apply" button → application form (multi-step). Applied status tracking.
**Current:** Need to verify. Likely basic list.
**Fix:** Add multi-step application form. Add eligibility check display. Add application status tracking (Eligible/Not Eligible/Applied/Under Review).

### 1.25 — Link Child Overlay: Missing Multi-step Wizard
**Status:** PARTIAL GAP
**Architecture spec (C-6.07):** Multi-step wizard: Step 1 (school selection) → Step 2 (child details) → Step 3 (verification — OTP) → Step 4 (success). Progress indicator at top.
**Current:** Need to verify current implementation.
**Fix:** Ensure 4-step wizard with progress indicator. Each step validates before advancing. Success state with confetti/checkmark animation.

### 1.26 — Digital ID Card Overlay: Missing QR Code + Download
**Status:** PARTIAL GAP
**Architecture spec (C-6.14):** Student ID card display (photo, name, class, roll, school name, valid dates). QR code for verification. "Download" button.
**Current:** Need to verify.
**Fix:** Ensure QR code is generated from student ID. Add "Download" button (saves to device — mark with TODO for platform implementation). Add flip animation (front: photo+info, back: QR code).

### 1.27 — Calendar Overlay: Missing Event Details
**Status:** PARTIAL GAP
**Architecture spec (C-6.02):** Shared `AcademicCalendarScreenV2` (view-only for parent).
**Current:** Need to verify.
**Fix:** Ensure calendar shows events with tap → event detail view. View-only (no create/edit for parents).

### 1.28 — Messages Overlay: Redundant with Conversations Tab
**Status:** ARCHITECTURE ISSUE (Changelog Entry #12)
**Architecture spec:** Remove `ParentOverlay.Messages` from enum. Deep link `/parent/messages` → switches to Conversations tab + Messages segment.
**Current:** Both `ParentOverlay.Messages` and Conversations tab exist.
**Fix:** Remove `ParentOverlay.Messages` from enum. Update deep link routing to switch to Conversations tab. Remove overlay rendering branch.

### 1.29 — Unlinked Gate Screen: Not Wired
**Status:** GAP
**Architecture spec (C-0):** If parent has no linked children → `ParentUnlinkedScreenV2` gate screen.
**Current:** `ParentUnlinkedScreen.kt` exists but may not be wired in shell. Home tab shows "No children linked yet" text.
**Fix:** Wire `ParentUnlinkedScreen` as gate screen when `state.children.isEmpty()`. Show "Link Child" (Primary) + "Discover Schools" (Secondary) buttons.

### 1.30 — Orphaned Screens Not Wired
**Status:** DEAD CODE
**Files that exist but are NOT wired into any overlay/tab:**
- `ParentQuizzesScreen.kt` — should be Academics Quizzes sub-tab
- `ParentQuizDetailScreen.kt` — should be quiz result detail (from Quizzes sub-tab)
- `ParentLeaderboardScreen.kt` — should be quiz leaderboard (from quiz detail)
- `ParentReportCardScreen.kt` — should be Report sub-tab detail (from Report sub-tab)
- `ParentTimetableScreen.kt` — should be "Full timetable" overlay (from Home schedule)
- `ParentSyllabusV2Screen.kt` — should be Syllabus sub-tab detail (from Syllabus sub-tab)
- `ParentDailySummaryScreen.kt` — should be Homework sub-tab detail
- `ParentHomeworkScreen.kt` — should be homework detail view
- `ParentAnnouncementsScreen.kt` — should be Announcements segment detail
- `ParentComposeMessageScreen.kt` — should be compose new message view
- `ParentThreadDetailScreen.kt` — should be conversation thread detail view

**Fix:** Wire all orphaned screens into appropriate navigation paths. Add overlay enum values as needed.

---

## SECTION 2: UI QUALITY ISSUES — Distortion, Polish, Premium Feel

### 2.1 — HTML-to-Compose Distortion
**Problem:** The HTML reference was a rough mock with hardcoded CSS. Translating it 1:1 to Compose caused:
- Incorrect spacing/padding ratios (HTML px ≠ dp)
- Missing elevation/shadow hierarchy
- No use of M3 tonal surface elevation (SurfaceContainerLowest vs SurfaceContainerLow vs SurfaceContainer)
- Flat backgrounds where gradients should be
- No glassmorphism on overlay headers
- No blur effects on FAB menus
**Fix:** Audit every screen for spacing, elevation, surface tonal hierarchy. Use M3 surface elevation tokens. Add subtle gradients on hero cards. Add glassmorphism on overlay headers and FAB menus.

### 2.2 — Missing Loading States (Skeletons)
**Problem:** Most screens show plain "Loading..." text. Not premium.
**Fix:** Use `VShimmerPremium` to build skeleton loading states for:
- Home: hero card skeleton, schedule skeleton, feature grid skeleton
- Academics: per-sub-tab skeleton
- Fees: hero card skeleton, list skeleton
- Conversations: thread list skeleton
- Profile: hero card skeleton, stats skeleton
- Every overlay: content skeleton

### 2.3 — Missing Error States
**Problem:** Error states show plain text in a box. Not premium.
**Fix:** Build `ErrorStateCard` composable with: error icon, friendly message, "Retry" button. Use VColors.ErrorContainer. Add subtle animation (shake on appear).

### 2.4 — Missing Empty States
**Problem:** Empty states show plain text in a box. Not premium.
**Fix:** Build `EmptyStateCard` composable with: illustration (vector), friendly message, action button. Use VColors.SurfaceContainerLow. Add subtle entrance animation.

### 2.5 — Missing Pull-to-Refresh
**Problem:** No pull-to-refresh on any screen.
**Fix:** Use `VPullRefreshPremium` wrapper on all scrollable tab content and overlay lists.

### 2.6 — Missing Staggered Entrances on Overlays
**Problem:** Overlays render all content at once. No staggered entrance.
**Fix:** Wrap overlay content sections in `VStaggeredItem` with incremental delays (0ms, 50ms, 100ms, 150ms...).

### 2.7 — FAB Menu Needs Glassmorphism
**Problem:** FAB menu items use plain `SurfaceContainerLowest` background. Looks flat.
**Fix:** Add glassmorphism: semi-transparent background + blur effect + subtle shadow. Use `GlassSurface` modifier or `Modifier.background(VColors.Scrim.copy(alpha = 0.05f))` + shadow.

### 2.8 — Bottom Nav Bar Needs Premium Polish
**Problem:** Standard `VBottomNav`. May not have active indicator animation, pill shape, or haptic feedback.
**Fix:** Ensure bottom nav has: animated active indicator (pill shape that slides between items), icon + label always visible, 48dp tap targets, active item color = VColors.Primary, inactive = VColors.OnSurfaceVariant. Add subtle scale animation on tap.

### 2.9 — Top Bar Needs Contextual Awareness
**Problem:** Top bar shows static title. Not contextual.
**Fix:** Top bar should: show child name + class on Home, show tab name on other tabs, show search icon (functional — opens search overlay), show notification bell with unread badge. Add subtle slide animation when switching tabs.

### 2.10 — Cards Lack Elevation Hierarchy
**Problem:** All cards use same background (SurfaceContainerLowest). No visual hierarchy.
**Fix:** Establish elevation hierarchy:
- Hero cards: gradient background + 8dp shadow
- Primary cards: SurfaceContainerLowest + 2dp shadow
- Secondary cards: SurfaceContainerLow + 0dp shadow
- Tertiary cards: SurfaceContainer + 0dp shadow

### 2.11 — Text Hierarchy Inconsistent
**Problem:** Text sizes and weights are inconsistent across screens. Some use VTypography tokens, others hardcode fontSize.
**Fix:** Audit all text usage. Ensure:
- Screen titles: VTypography.SectionHeader
- Card titles: VTypography.UpdateTitle
- Body text: VTypography.UpdateText
- Labels/captions: VTypography.NavLabel
- Stats: VTypography.StatValue
- Remove all hardcoded fontSize/sp values. Use VTypography tokens.

### 2.12 — Color Usage Not Tonal
**Problem:** Some screens use raw Color.White, Color.Black instead of VColors.OnSurface, VColors.Surface.
**Fix:** Replace all raw Color references with VColors tokens. Exception: Color.White for text on gradient/colored backgrounds where appropriate.

### 2.13 — Missing Haptic Feedback
**Problem:** No haptic feedback on any interaction.
**Fix:** Add `LocalHapticFeedback.current.performHapticFeedback(HapticFeedbackType.LongPress)` on button taps, card taps, tab switches. Subtle but adds premium feel.

### 2.14 — Missing Transition Animations Between Sub-tabs
**Problem:** Sub-tab content swaps instantly. No animation.
**Fix:** Use `AnimatedContent` with slide + fade transition for sub-tab switches in Academics, Conversations, Library.

### 2.15 — Overlay Slide Animation Needs Polish
**Problem:** `ParentOverlayScaffold` has basic slide-in from right. But no slide-out on back. And no scale fade for background.
**Fix:** Add slide-out animation on back press. Add subtle scale-down on background content when overlay is active. Use `AnimatedContent` with custom transition spec.

---

## SECTION 3: FUNCTIONAL GAPS — Missing Interactions & Data Flow

### 3.1 — Fee Payment Flow Not Wired
**Problem:** "Pay Now" button on Fees hero card has empty onClick.
**Fix:** Wire to payment gateway flow. Show payment processing state. Show success snackbar. Show error snackbar. Update outstanding balance after payment.

### 3.2 — Search Not Functional
**Problem:** Search field on Home has no search functionality.
**Fix:** Implement search: typing filters content (students, fees, events). Show search results overlay. Clear search on back.

### 3.3 — Notification Bell Not Showing Unread Count
**Problem:** Bell icon shows a dot but no count.
**Fix:** Show unread count badge on bell icon. Tap → Notifications overlay. Mark all as read on exit.

### 3.4 — Filter Chips on Home Not Functional
**Problem:** Filter chips (All, Academics, Fees, Attendance, Transport, Library) have empty onClick.
**Fix:** Wire filter chips to filter content on Home. "All" shows everything. "Fees" shows only fee-related cards. etc.

### 3.5 — FAB Menu Items Need Real Wiring
**Problem:** FAB menu items (AI Tutor, Message Teacher, Apply Leave) are wired to overlays. Good. But "Message Teacher" goes to generic Messages overlay, not compose new message.
**Fix:** "Message Teacher" → should open compose new message with teacher picker, not generic messages overlay.

### 3.6 — Deep Link Routing Incomplete
**Problem:** Need to verify all deep links from architecture doc are wired.
**Fix:** Verify deep links for: /parent/notifications, /parent/calendar, /parent/scholarships, /parent/leave, /parent/messages (→ tab switch), /parent/link-child, /parent/discovery, /parent/health, /parent/pulse, /parent/transport, /parent/tutor, /parent/tutor-progress, /parent/id-card, /parent/library, /parent/events, /parent/account-settings, /parent/school-detail/{id}.

### 3.7 — Offline State Not Handled
**Problem:** No offline state on any screen.
**Fix:** Add offline banner on Home when offline. Show cached data with "Offline" indicator. Queue actions (messages, leave applications) with "Will send when online" indicator.

---

## SECTION 4: ORPHANED SCREENS — Dead Code Cleanup

### 4.1 — Screens to Delete (confirmed orphaned, no unique functionality)
- Check `ParentPewsScreenV2.kt` — if exists, delete (Changelog Entry #21)
- Check `StudentLibraryScreen.kt` — if exists, delete or merge (Changelog Entry #22)

### 4.2 — Screens to Wire (have functionality, just not connected)
- `ParentQuizzesScreen.kt` → Academics Quizzes sub-tab
- `ParentQuizDetailScreen.kt` → from Quizzes sub-tab tap
- `ParentLeaderboardScreen.kt` → from Quiz Detail
- `ParentReportCardScreen.kt` → from Report sub-tab tap (AI Report Card)
- `ParentTimetableScreen.kt` → from Home "Full timetable" link
- `ParentSyllabusV2Screen.kt` → from Syllabus sub-tab tap
- `ParentDailySummaryScreen.kt` → from Homework sub-tab
- `ParentHomeworkScreen.kt` → from homework item tap
- `ParentAnnouncementsScreen.kt` → from Announcements segment tap
- `ParentComposeMessageScreen.kt` → from compose new message
- `ParentThreadDetailScreen.kt` → from thread tap in Conversations

---

## SECTION 5: ITERATION PLAN — Execution Order

### ITERATION 1: Foundation & Shared Components
- [ ] Build `SkeletonCard` composable (using VShimmerPremium)
- [ ] Build `ErrorStateCard` composable (icon + message + retry)
- [ ] Build `EmptyStateCard` composable (illustration + message + action)
- [ ] Build `FeatureGridCard` composable (icon + label + metric + tap)
- [ ] Build `ChildSwitcherDropdown` composable
- [ ] Build `AttendanceCalendar` composable (monthly grid, color-coded)
- [ ] Build `TypingIndicator` composable (3 animated dots)
- [ ] Build `GlassSurface` modifier (semi-transparent + blur + shadow)
- [ ] Compile and verify

### ITERATION 2: Home Tab Rebuild
- [ ] Add child switcher dropdown
- [ ] Enhance hero card with progress ring, level, XP, house badge
- [ ] Replace 4-card carousel with 10-card 2-up feature grid
- [ ] Polish school-day timeline with staggered entrance
- [ ] Wire all 10 feature card callbacks in shell
- [ ] Wire filter chips to filter content
- [ ] Add skeleton loading states
- [ ] Add pull-to-refresh
- [ ] Wire unlinked gate screen when no children
- [ ] Compile and verify

### ITERATION 3: Academics Tab Rebuild
- [ ] Add Quizzes sub-tab (7 total)
- [ ] Rebuild Overview with journey progress card + achievements
- [ ] Rebuild Attendance with calendar + trend chart
- [ ] Rebuild Marks with subject filter + trend chart
- [ ] Rebuild Syllabus with expandable unit list
- [ ] Rebuild Quizzes with quiz list + tap → detail → leaderboard
- [ ] Rebuild Homework with date selector + homework list
- [ ] Rebuild Report with AI insights + download
- [ ] Add AnimatedContent transitions between sub-tabs
- [ ] Add per-sub-tab loading/empty/error states
- [ ] Compile and verify

### ITERATION 4: Fees Tab Rebuild
- [ ] Enhance hero card with gradient + shadow
- [ ] Wire "Pay Now" to payment flow (processing/success/error states)
- [ ] Wire announcements to real data
- [ ] Wire payment history to real data
- [ ] Add receipt download
- [ ] Add skeleton loading
- [ ] Add pull-to-refresh
- [ ] Compile and verify

### ITERATION 5: Conversations Tab Rebuild
- [ ] Add compose new message → thread picker
- [ ] Wire announcements to real data (not hardcoded)
- [ ] Wire thread tap → thread detail view
- [ ] Add sending state with queued indicator
- [ ] Add skeleton loading
- [ ] Add pull-to-refresh
- [ ] Add AnimatedContent between segments
- [ ] Compile and verify

### ITERATION 6: Profile Tab Rebuild
- [ ] Implement swipe-down account options reveal
- [ ] Wire stat card taps to navigate
- [ ] Wire badge taps to achievement detail
- [ ] Polish gamified player card aesthetic
- [ ] Add skeleton loading
- [ ] Compile and verify

### ITERATION 7: Overlays — Tutor Chat + Progress
- [ ] Add VTopTabs (Chat · Plan · Practice)
- [ ] Wire Plan and Practice tab content
- [ ] Add quick suggestion chips
- [ ] Add subject selector
- [ ] Add typing indicator
- [ ] Add "View Progress" button → TutorProgress overlay
- [ ] Wire send button
- [ ] Enhance TutorProgress with mastery heatmap
- [ ] Compile and verify

### ITERATION 8: Overlays — Pulse + Health + Transport
- [ ] Enhance Pulse with trend chart + AI recommendations + per-factor detail
- [ ] Enhance Health with risk factors + recommendations + trend chart
- [ ] Enhance Transport with boarding status + map placeholder
- [ ] Add staggered entrances
- [ ] Add loading/empty/error states
- [ ] Compile and verify

### ITERATION 9: Overlays — Leave + Scholarships + Link Child
- [ ] Enhance Leave with child selector + document upload
- [ ] Enhance Scholarships with multi-step application form
- [ ] Verify Link Child 4-step wizard
- [ ] Add form validation + submitting states
- [ ] Compile and verify

### ITERATION 10: Overlays — Library + Events + ID Card + Calendar + Settings
- [ ] Build Library 9 sub-tabs
- [ ] Enhance Events with PTM slot booking
- [ ] Enhance ID Card with QR + download + flip animation
- [ ] Verify Calendar view-only with event details
- [ ] Enhance Account Settings with all missing fields
- [ ] Compile and verify

### ITERATION 11: Shell + Navigation + Deep Links
- [ ] Remove redundant ParentOverlay.Messages (Changelog #12)
- [ ] Wire all orphaned screens
- [ ] Verify all deep links
- [ ] Add offline state handling
- [ ] Polish FAB with glassmorphism
- [ ] Polish bottom nav with animated indicator
- [ ] Polish top bar with contextual awareness
- [ ] Add overlay slide-out animation
- [ ] Compile and verify

### ITERATION 12: Final Polish + Dead Code Cleanup
- [ ] Delete orphaned dead code files
- [ ] Audit all text for VTypography token usage
- [ ] Audit all colors for VColors token usage
- [ ] Audit all shapes for VShapes token usage
- [ ] Add haptic feedback to all interactions
- [ ] Verify 140dp bottom padding on all scrollable screens
- [ ] Final compile and verify
- [ ] Commit and push

---

## SECTION 6: FILE REFERENCE

### Key files to modify:
- `ParentPortalShell.kt` — shell, overlay routing, tab wiring, deep links
- `ParentHomeScreen.kt` — Home tab
- `ParentAcademicsScreen.kt` — Academics tab
- `ParentFeesScreen.kt` — Fees tab
- `ParentConversationsScreen.kt` — Conversations tab
- `ParentProfileScreen.kt` — Profile tab
- `ParentOverlayScaffold.kt` — shared overlay scaffold
- `ParentTutorChatScreen.kt` — Tutor Chat overlay
- `ParentPulseScreen.kt` — Pulse overlay
- `ParentHealthScreen.kt` — Health overlay
- `ParentTransportScreen.kt` — Transport overlay
- `ParentLeaveScreen.kt` — Leave overlay
- `ParentScholarshipScreen.kt` — Scholarships overlay
- `ParentLibraryScreen.kt` — Library overlay
- `ParentEventsScreen.kt` — Events overlay
- `ParentDigitalIdCardScreen.kt` — ID Card overlay
- `ParentCalendarScreen.kt` — Calendar overlay
- `ParentAccountSettingsScreen.kt` — Account Settings overlay
- `ParentSchoolDetailScreen.kt` — School Detail overlay
- `ParentDiscoveryScreen.kt` — Discovery overlay
- `ParentNotificationsScreen.kt` — Notifications overlay
- `ParentMessagesScreen.kt` — Messages overlay (to be removed)
- `ParentTutorProgressScreen.kt` — Tutor Progress overlay
- `ParentLinkChildScreen.kt` — Link Child overlay
- `ParentUnlinkedScreen.kt` — Unlinked gate screen

### Key tokens/components to use:
- `VColors.kt` — all color tokens
- `VShapes.kt` — all shape tokens
- `VTypography.kt` — all text style tokens
- `VMotion.kt` — all animation tokens
- `PremiumTheme.kt` — theme wrapper
- `VShimmerPremium.kt` — skeleton loading
- `VPullRefreshPremium.kt` — pull to refresh
- `VChartsPremium.kt` — charts
- `VEmptyStatePremium.kt` — empty state
- `VStateHostPremium.kt` — state host
- `VConfirmDialogPremium.kt` — confirm dialog
- `VSnackbarPremium.kt` — snackbar
- `VDatePickerPremium.kt` — date picker
- `VLanguagePickerPremium.kt` — language picker
- `VThemePickerPremium.kt` — theme picker
- `VAvatarPremium.kt` — avatar
- `VBrandLogoPremium.kt` — brand logo
- `pressScale` modifier — tactile press feedback
- `shapeMorph` modifier — shape morph on press
- `VStaggeredItem` — staggered entrance animation
- `radialGlow` modifier — radial glow effect

### Architecture docs (source of truth):
- `ENROLLPLUS_SCREEN_ARCHITECTURE_FINAL.md` — Part C (C-0 through C-6.16)
- `ENROLLPLUS_RESTRUCTURE_CHANGELOG_AND_BACKEND_MAPPING.md` — Entries #10-#13, #18, #21-#22

### HTML reference (rough mock only, NOT target quality):
- `preview/parent-portal.html` — use for layout structure reference only

---

## SECTION 7: VERIFICATION CHECKLIST

After each iteration, verify:
- [ ] `./gradlew.bat composeApp:compileDevDebugKotlinAndroid` — BUILD SUCCESSFUL
- [ ] No new compiler warnings
- [ ] No hardcoded colors (all VColors)
- [ ] No hardcoded shapes (all VShapes)
- [ ] No hardcoded text styles (all VTypography)
- [ ] Every card has pressScale + shapeMorph
- [ ] Every screen has loading/error/empty states
- [ ] Every scrollable screen has 140dp bottom padding
- [ ] Every overlay uses ParentOverlayScaffold
- [ ] Every form has validation + submitting state
- [ ] No empty onClick = { } (wire or TODO with comment)
- [ ] AutoMirrored icons used for directional icons
- [ ] Staggered entrances on all content sections
- [ ] Pull-to-refresh on all data-driven screens
- [ ] NO content overflow or clipping — all buttons/options visible on 360x640dp
- [ ] NO fixed heights on growing content — use weight + scroll
- [ ] 140dp bottom padding on all scrollable screens (buttons not hidden behind nav)

---

## SECTION 8: THE FINAL QUESTION

Before marking ANY iteration complete, after all checks pass,
after the self-review ritual's 11 questions are all "YES," ask yourself
ONE more question — the only question that ultimately matters:

    ┌─────────────────────────────────────────────────────────────────────┐
    │                                                                     │
    │  "If I were a parent opening this screen for the first time,        │
    │   would I feel like I'm using a premium product?"                   │
    │                                                                     │
    │  Not "is it functional?" — that's table stakes.                     │
    │  Not "does it compile?" — that's the minimum.                      │
    │  Not "does it match the spec?" — that's the baseline.               │
    │                                                                     │
    │  Would I feel PREMIUM?                                              │
    │  Would I feel CONFIDENT?                                            │
    │  Would I feel like this app RESPECTS my time and intelligence?      │
    │                                                                     │
    │  If the answer is anything less than "absolutely yes,"              │
    │  you are not done. Go back. Refine. Polish. Repeat.                 │
    │                                                                     │
    └─────────────────────────────────────────────────────────────────────┘

---

# END OF GOD MODE PROMPT
#
# You are the God of UI/UX. You are the craftsman. You are the engineer
# who ships masterpieces, not drafts. Every screen you touch becomes the
# best version of itself. Every parent who opens this app feels it —
# the warmth, the confidence, the premium quality.
#
# Now go. Build something worthy of a $100M valuation.
# One screen at a time. One masterpiece at a time.
#
# ═══════════════════════════════════════════════════════════════════════════
