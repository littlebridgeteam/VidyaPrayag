# ═══════════════════════════════════════════════════════════════════════════
# GOD MODE PROMPT — ADMIN/SCHOOL PORTAL PREMIUM REBUILD
# ═══════════════════════════════════════════════════════════════════════════
#
# DESIGN CONCEPT INSPIRATION:
#   Absolute best of latest Material UI by Google.
#   Must give the feel of a top $100M startup of 2026.
#
# ┌─────────────────────────────────────────────────────────────────────────┐
# │  YOU ARE THE GOD OF ADMIN UX.                                          │
# │                                                                         │
# │  You don't just "write Compose code." You DESIGN command centers.       │
# │  Every screen you ship is a masterpiece — the kind of screen that       │
# │  makes a principal stop and think: "This app actually runs my           │
# │  school better than I could before."                                    │
# │                                                                         │
# │  You are NOT an AI code generator. You are a craftsman who happens      │
# │  to use AI tools. The difference? A craftsman CARES about every data    │
# │  point, every drill-down path, every empty state, every confirmation    │
# │  dialog. A code generator just dumps composables and moves on.          │
# │                                                                         │
# │  This is a $100M valuation startup. Our admins are Indian school        │
# │  principals and admin staff who manage 200-2000 students. The UI        │
# │  must be:                                                               │
# │  • COMMANDING — a principal should see school health in 5 seconds       │
# │  • DENSE-BUT-ORGANIZED — data-rich views that don't overwhelm           │
# │  • ALIVE — every interaction has a response (scale, morph, confirm)     │
# │  • HONEST — no fake data, no placeholder metrics, no broken drill-downs │
# │  • FAST — actions complete in 1-2 taps, animations are 200-400ms        │
# │  • CONFIDENT — destructive actions always confirm, never accidental     │
# └─────────────────────────────────────────────────────────────────────────┘
#
# ───────────────────────────────────────────────────────────────────────────
# THE DESIGN BIBLE — WHAT "PREMIUM" MEANS FOR ADMIN
# ───────────────────────────────────────────────────────────────────────────
#
# The admin portal is NOT the parent portal. The persona is different.
# A parent needs warmth and simplicity. A principal needs COMMAND and CLARITY.
#
# Premium for Admin is NOT:
#   ✗ Gradients everywhere — the admin is a professional, not a consumer
#   ✗ Glassmorphism on everything — decorative, not functional
#   ✗ Rounded corners on everything at the same radius — lazy
#   ✗ Drop shadows on every card — Material 2, we're past that
#   ✗ "Beautiful" empty states with illustrations — admins want actionable
#     empty states ("No teachers yet — Add your first teacher" with a button)
#   ✗ HARDCODED FAKE DATA — no dummy KPIs, no mock enrollment numbers, no
#     placeholder teacher lists. Every metric flows from ViewModel → API →
#     backend → database. No exceptions. A principal making decisions on
#     fake data is a liability, not a feature.
#   ✗ AI SLOP — flat lists of identical cards with no information hierarchy.
#     A principal's Home is NOT a grid of equal-weight stat cards. It's a
#     command center: hero metrics → pulse gauge → quick actions → activity.
#     If it looks like a generic admin dashboard template, it IS slop.
#     Burn it down and rebuild with intention.
#   ✗ FLAT LAYOUTS — the admin portal has INFORMATION HIERARCHY. Not every
#     card is equal. The school pulse score is more important than the
#     birthday list. The pending approvals are more urgent than the teacher
#     spotlight. Design the eye flow: hero → urgent → important → context.
#
# Premium for Admin IS:
#   ✓ PREMIUM GREY BASE — VColors.Surface is the foundation. NOT white.
#     Cards use SurfaceContainerLowest to lift above the base. The grey
#     base creates a sophisticated, muted, confident feel — like a high-end
#     analytics dashboard, not a cheap white-page admin panel.
#   ✓ TONAL SURFACES — M3 surface container elevation creates depth
#     without shadows. SurfaceContainerLowest > SurfaceContainerLow >
#     SurfaceContainer > SurfaceContainerHigh. Use this hierarchy.
#   ✓ INFORMATION HIERARCHY — the Home tab is NOT a flat grid. It's:
#     1. Greeting hero (school name, date, admin name, notification bell)
#     2. Insights carousel (swipeable, 3-4 key metric cards with trends)
#     3. Pulse gauge (school health score ring + sub-metric bars)
#     4. Quick actions row (horizontal scroll of circular icon buttons)
#     5. 2-up KPI grid (class avg, teacher performance)
#     6. Comms center mini (latest 3 announcements + "View all")
#     7. Upcoming events (next 3)
#     8. Teacher spotlight card
#     9. Recent achievements
#     10. Today's birthdays
#     11. Activity feed (10 recent staff actions)
#     This is a COMMAND CENTER, not a card grid.
#   ✓ PURPOSEFUL COLOR — VColors.Primary for ONE primary action per section.
#     VColors.Tertiary for secondary. VColors.WarmOrange for warnings.
#     VColors.Error for destructive actions (always with confirmation).
#     Don't rainbow-blast. One accent per section.
#   ✓ SHAPE LANGUAGE — VShapes.Xl (24dp) for cards, VShapes.Lg (16dp) for
#     inner elements, VShapes.Full for pills/badges. Consistent.
#   ✓ MOTION WITH MEANING — pressScale on every tappable card (0.97-0.98).
#     shapeMorph on press (Xl → TwoXl). VStaggeredItem for entrance (0-300ms
#     cascade). AnimatedContent for sub-tab switches. NOT animation for
#     animation's sake.
#   ✓ TYPOGRAPHY HIERARCHY — VTypography.GreetingTitle for screen titles.
#     SectionHeader for section titles. UpdateTitle for card titles.
#     UpdateText for body. NavLabel for captions. StatValue for metrics.
#     Never hardcode fontSize.
#   ✓ WHITESPACE — 20-24dp horizontal padding is the standard. 16-24dp
#     between sections. 8-12dp between items in a list. Don't cram.
#   ✓ STATE COMPLETENESS — every screen has 4 states:
#     1. LOADING — skeleton cards (VShimmerCardPremium / VShimmerListPremium)
#     2. ERROR — ErrorStateCard with icon + message + "Retry" button
#     3. EMPTY — EmptyStateCard with icon + actionable message + button
#     4. LOADED — the actual content, polished and complete
#   ✓ CONFIRMATION ON DESTRUCTIVE — deactivate teacher, graduate students,
#     delete class, reject request, logout → ALWAYS show VConfirmDialog.
#     Never let a principal accidentally delete data.
#   ✓ DRILL-DOWN EVERYWHERE — every metric card → detail overlay. Every
#     class name → class detail. Every student name → student profile.
#     Every teacher name → teacher profile. A principal should never
#     see a data point and wonder "what's behind this?"
#
# ───────────────────────────────────────────────────────────────────────────
# THE SELF-REVIEW RITUAL — MANDATORY AFTER EVERY SCREEN
# ───────────────────────────────────────────────────────────────────────────
#
# After completing EACH screen, ask these questions OUT LOUD (write answers
# as comments at the bottom of the file before finalizing):
#
#   ┌─ SELF-REVIEW: [Screen Name] ────────────────────────────────────────┐
#   │                                                                       │
#   │ 1. DOES THIS LOOK LIKE A COMMAND CENTER?                              │
#   │    Would a principal feel in control? Or does it look generic?        │
#   │                                                                       │
#   │ 2. Is there ONE clear primary action on this screen?                  │
#   │    A principal should know what to do within 3 seconds.               │
#   │                                                                       │
#   │ 3. Does every tappable element respond to touch?                      │
#   │    pressScale + shapeMorph. No bare clickables.                       │
#   │                                                                       │
#   │ 4. Are the 4 states (loading/error/empty/loaded) all implemented?     │
#   │                                                                       │
#   │ 5. Is the spacing consistent? (20-24dp H, 16-24dp sections, 8-12dp)   │
#   │                                                                       │
#   │ 6. Are all colors from VColors? Shapes from VShapes? Text from        │
#   │    VTypography? No hardcoded values?                                  │
#   │                                                                       │
#   │ 7. Does the screen have 140dp bottom padding?                         │
#   │                                                                       │
#   │ 8. Is there staggered entrance animation?                             │
#   │                                                                       │
#   │ 9. Are there any empty onClick = { } handlers?                        │
#   │    Every clickable must be wired or have a TODO(comment).             │
#   │                                                                       │
#   │ 10. DOES A PRINCIPAL UNDERSTAND THIS SCREEN INSTANTLY?                │
#   │     Clear labels. Obvious drill-downs. No jargon.                     │
#   │                                                                       │
#   │ 11. IS EVERY BUTTON FULLY VISIBLE ON 360x640dp?                       │
#   │     No clipping. No hidden behind bottom nav. Use scroll/weight/pad.  │
#   │                                                                       │
#   │ 12. IS THERE ZERO HARDCODED DATA?                                     │
#   │     Every metric from ViewModel → API → backend. No inline fake data. │
#   │                                                                       │
#   │ 13. ARE DESTRUCTIVE ACTIONS CONFIRMED?                                │
#   │     Deactivate, delete, reject, graduate, logout → VConfirmDialog.    │
#   │                                                                       │
#   │ 14. DOES EVERY METRIC HAVE A DRILL-DOWN?                              │
#   │     Tap enrollment → Student Roster. Tap attendance → DailyAttendance.│
#   │     Tap fee → Fee sub-tab. Tap pulse → Analytics.                     │
#   │                                                                       │
#   │ 15. IS THE INFORMATION HIERARCHY CORRECT?                             │
#   │     Most important at top. Urgent highlighted. Context at bottom.     │
#   │     If principal scrolls past birthdays to see pending approvals,     │
#   │     you failed the hierarchy. Fix it.                                 │
#   │                                                                       │
#   │ If ANY answer is "no" or "maybe," DO NOT move to the next screen.     │
#   │ Fix it. Recompile. Re-review. Only proceed when ALL 15 answers are    │
#   │ a confident "YES."                                                    │
#   └───────────────────────────────────────────────────────────────────────┘
#
# ───────────────────────────────────────────────────────────────────────────
# ITERATION WORKFLOW
# ───────────────────────────────────────────────────────────────────────────
#
# Each iteration = ONE screen or ONE tightly-related group.
#
# STEP 1: Read the architecture spec (Part A in ENROLLPLUS_SCREEN_ARCHITECTURE_FINAL.md)
# STEP 2: Read the current implementation in ui/v2/screens/premium/school/
# STEP 3: Identify gaps from the analysis below
# STEP 4: Write code using tokens, components, modifiers
# STEP 5: Compile — fix errors immediately
# STEP 6: Run SELF-REVIEW RITUAL (all 15 questions)
# STEP 7: If any "no" → fix → recompile → re-review
# STEP 8: Mark complete. Move to next.
#
# One screen. One masterpiece. One perfect screen > ten half-baked ones.
#
# ───────────────────────────────────────────────────────────────────────────
# BEFORE YOU START — READ THESE
# ───────────────────────────────────────────────────────────────────────────
#
# 1. This entire file
# 2. ENROLLPLUS_SCREEN_ARCHITECTURE_FINAL.md — Part A (A-0 through A-6.38)
# 3. ENROLLPLUS_RESTRUCTURE_CHANGELOG_AND_BACKEND_MAPPING.md — all 22 entries
# 4. VColors.kt, VShapes.kt, VTypography.kt, VMotion.kt — know your tokens
# 5. ui/v2/components/ — know your building blocks (30+ components)
# 6. ui/v2/screens/premium/school/ — 28+ existing admin screens
# 7. website/src/components/admin/ — web reference for data patterns ONLY,
#    NOT for UI. Compose UI follows the premium token system.
#
# ───────────────────────────────────────────────────────────────────────────
# TOKENS — NEVER HARDCODE
# ───────────────────────────────────────────────────────────────────────────
#
# COLORS — VColors.* (@Composable get()):
#   Primary/OnPrimary/PrimaryContainer/OnPrimaryContainer
#   Secondary/OnSecondary/SecondaryContainer/OnSecondaryContainer
#   Tertiary/OnTertiary/TertiaryContainer/OnTertiaryContainer
#   Error/OnError/ErrorContainer/OnErrorContainer
#   Surface/SurfaceDim/SurfaceBright
#   SurfaceContainerLowest/Low/Container/High/Highest
#   OnSurface/OnSurfaceVariant/Outline/OutlineVariant
#   WarmOrange/WarmOrangeContainer/LiveCyan/GlassWhite20
#   PrimaryMid/PrimaryDeep/TertiaryDeep/Scrim
#
# SHAPES — VShapes.*: Xs=4, Sm=8, Md=12, Lg=16, Xl=24, TwoXl=28, Full=999
#   Also XsDp..TwoXlDp for animateDpAsState targets
#
# TYPOGRAPHY — VTypography.*:
#   GreetingTitle, GreetingEyebrow, SectionHeader, SectionLink
#   FeatureTitle, FeatureSubtitle, FeatureAmount, FeatureBadge
#   UpdateTitle, UpdateText, NavLabel
#   StatValue, HwTitle, HwSub, HwStatus
#   ScheduleHour/AmPm/Subject/Teacher/Status, SyllabusName/Pct
#
# MOTION — VMotion.*: DurShort1/Short2/Medium1/Medium2/Long1
#
# MODIFIERS:
#   pressScale(interactionSource, pressedScale = 0.97f)
#   shapeMorph(interactionSource, fromShape, toShape, duration)
#   radialGlow(offsetX, offsetY, radius, color)
#   VStaggeredItem(delayMs)
#
# COMPONENTS:
#   VShimmerCardPremium/VShimmerListPremium — skeletons
#   VStateHostPremium — 4-phase state manager
#   VEmptyStatePremium — empty state
#   VPullRefreshPremium — pull to refresh
#   VChartsPremium — charts (line, bar, heatmap, donut)
#   VGradientHeroPremium/HeroStatPremium — hero cards
#   VStatCardPremium — stat card (value, label, trend, icon, onClick)
#   VListTilePremium — list item (title, subtitle, icon, trailing)
#   VTopTabsPremium — sub-tab navigation
#   VBottomNav/NavItem — bottom navigation
#   VScreenScaffoldPremium — screen scaffold
#   VBackHeader — overlay header
#   VConfirmDialogPremium — destructive action confirmation
#   VSnackbarPremium — snackbar feedback
#   VSearchField — search input
#   VTextInput — form input
#   VPrimaryButton/VSecondaryButton — buttons
#   VDatePickerPremium/VTimePickerPremium — pickers
#   VLanguagePickerPremium/VThemePickerPremium — settings pickers
#   VAvatarPremium/VBrandLogoPremium — branding
#   SchoolOverlayScaffold — shared overlay scaffold
#   VFilterChip — filter chip
#   VIcons.* — icon set
#
# ───────────────────────────────────────────────────────────────────────────
# CRITICAL RULES — ZERO TOLERANCE
# ───────────────────────────────────────────────────────────────────────────
#
# 1. NEVER hardcode Color(0x...) — always VColors.*
# 2. NEVER hardcode dp for corner radii — always VShapes.*
# 3. NEVER hardcode FontWeight/fontSize — always VTypography.*
# 4. NEVER use indication = null without pressScale + shapeMorph
# 5. EVERY card → pressScale + shapeMorph (0.97-0.98, Xl→TwoXl)
# 6. EVERY screen → 4 states (loading, error, empty, loaded)
# 7. EVERY scrollable → 140dp bottom padding
# 8. EVERY overlay → SchoolOverlayScaffold
# 9. EVERY form → validation + submitting + success + error states
# 10. NEVER leave onClick = { } empty — wire or TODO(comment)
# 11. AutoMirrored icons for directional icons (ArrowBack, etc.)
# 12. Compile after EACH screen
# 13. Run SELF-REVIEW RITUAL after EACH screen — all 15 must be "YES"
# 14. NEVER use Color.White or Color.Black — use VColors tokens
# 15. NEVER use FontWeight.Bold without VTypography reference
# 16. NEVER let content overflow — test on 360x640dp. Use scroll/weight/pad.
# 17. NEVER use fixed height for growing content — use weight + scroll
# 18. ALWAYS test on 360x640dp mentally
# 19. ZERO HARDCODED DATA — every metric from ViewModel → API → backend
# 20. ALL FEATURES WIRED TO BACKEND — no "TODO: wire to API" left behind
# 21. FULL RESTRUCTURE ALLOWED — delete and rewrite any screen from scratch
# 22. PREMIUM GREY BASE — VColors.Surface, NOT white
# 23. NO AI SLOP — distinct visual identity, clear info hierarchy
# 24. DESTRUCTIVE ACTIONS ALWAYS CONFIRM — VConfirmDialogPremium
# 25. EVERY METRIC HAS A DRILL-DOWN — tap any KPI → detail overlay
# 26. SUPERADMIN ROLE GATING — "All Schools" card + "Dev Tools" if SuperAdmin
#
# ═══════════════════════════════════════════════════════════════════════════
# GAP ANALYSIS — STRUCTURAL GAPS (Missing Screens & Broken Wiring)
# ═══════════════════════════════════════════════════════════════════════════

## A. HOME TAB GAPS

### A-1. Missing Insights Carousel (HorizontalPager)
**Status:** CRITICAL
**Spec (A-1):** Swipeable carousel of 3-4 metric cards (students, attendance %, fee collection, pending approvals). Each: large number, label, trend arrow, comparison text. Page indicator dots. Tap → drill-down.
**Current:** Flat 2-up grid of VStatCardPremium. No carousel, no swipe, no trends.
**Fix:** Replace with HorizontalPager of full-width insight cards. Each: VTypography.StatValue metric, label, trend arrow (up/down icon), "vs last week: +X%". Page dots below. Tap → overlay.

### A-2. Missing Pulse Gauge Widget
**Status:** CRITICAL
**Spec (A-1):** VProgressRing centered, score 0-100, 4 sub-metric bars (academics, attendance, engagement, fees). Tap → Analytics.
**Current:** VGradientHeroPremium with just the score number. No ring, no sub-metrics.
**Fix:** Build `PulseGaugeWidget`: VProgressRing (score) + 4 VProgressBar sub-metrics with labels. Tap → onOpenAnalytics.

### A-3. Missing Quick Actions Row
**Status:** CRITICAL
**Spec (A-1):** Horizontal scroll: circular icon buttons — "Add Announcement", "Create Event", "Schedule PTM", "Add Teacher", "Add Student", "Analytics".
**Current:** Not present.
**Fix:** Build `QuickActionRow`: horizontal scroll of 48-56dp circles (VColors.PrimaryContainer bg, VColors.OnPrimaryContainer icon). Labels below (VTypography.NavLabel). Each → overlay/tab.

### A-4. Missing Comms Center Mini
**Status:** CRITICAL
**Spec (A-1):** Latest 3 announcements + "View all" → Comms tab.
**Current:** Single VStatCardPremium with unread count only.
**Fix:** Add comms section: "Communications" header + "View all" link. 3 announcement mini-cards (title, category badge, date). Data from SchoolAnnouncementsViewModel.

### A-5. Missing Upcoming Events
**Status:** CRITICAL
**Spec (A-1):** Next 3 events with date, title, category. Tap → Calendar.
**Current:** VStatCardPremium with count only. No event list.
**Fix:** Add events section: 3 event cards (date badge, title, category dot). Tap → Calendar overlay. Data from AcademicCalendarPlatformViewModel.

### A-6. Missing Teacher Spotlight + Achievements + Birthdays
**Status:** GAP
**Spec (A-1):** Teacher spotlight card, recent achievements list, today's birthdays.
**Current:** None present.
**Fix:** Add all three as conditional sections (only show if data exists). Spotlight: avatar, name, achievement reason. Tap → TeacherProfile. Achievements: icon, title, student/class, date. Birthdays: avatar, name, class/role.

### A-7. Missing Activity Feed
**Status:** CRITICAL
**Spec (A-1):** 10 recent staff actions. Each: avatar, action text, timestamp. "View all" → Analytics.
**Current:** Not present.
**Fix:** Add activity feed at bottom. Data from SchoolDashboardViewModel. Max 10 items. "View all" → AnalyticsDashboard. Empty: "No recent activity".

### A-8. Missing Onboarding Nudge
**Status:** GAP
**Spec (A-1):** If profile incomplete → nudge card with completion % + "Resume".
**Current:** Not present.
**Fix:** Conditional card at top. Data from InstitutionalProfileViewModel. "Finish setting up — X% complete" + "Resume" → EditProfile. VColors.PrimaryContainer bg.

### A-9. Flat Layout — No Information Hierarchy
**Status:** CRITICAL
**Current:** Home is a flat vertical list of VStatCardPremium — "Analytics", "At-Risk", "Transport", "Reports", "Events" — all same visual weight. AI slop.
**Fix:** Rebuild with full architecture spec layout: hero → insights carousel → pulse gauge → quick actions → KPI grid → comms mini → events → spotlight → achievements → birthdays → activity feed. Use varied card types. NOT a flat list.

### A-10. Missing Pull-to-Refresh
**Fix:** Wrap Home content in VPullRefreshPremium. On refresh → reload all ViewModels.

### A-11. Missing Staggered Entrance
**Fix:** Wrap each section in VStaggeredItem with incremental delays (0ms, 50ms, 100ms... 300ms).

### A-12. Notification Bell Not Properly Wired
**Current:** Bell icon in hero trailing goes to onExit (→ settings), not notifications.
**Fix:** Wire bell icon tap → onOpenNotifications. Hero card onClick can remain separate.

## B. PEOPLE TAB GAPS

### B-1. Missing Add Teacher Dialog
**Status:** CRITICAL
**Spec (A-2):** "Add Teacher" button → dialog: Name, Employee ID, Initial password. Validation. Submitting: spinner.
**Current:** No "Add Teacher" button or dialog.
**Fix:** Add VPrimaryButton at top of Teachers sub-tab. Dialog with VTextInput fields. Validation: name + employee ID required. Success → snackbar + refresh.

### B-2. Missing Add Student Dialog
**Status:** CRITICAL
**Spec (A-2):** "Add Student" → dialog: Name, Class (dropdown), Section (dropdown), Roll number, Parent phone. Validation: phone format, required fields.
**Current:** Not present.
**Fix:** Add button + dialog. Class/Section as dropdowns from ClassesSubjectsViewModel. Phone validation (10 digits). Success → snackbar + refresh.

### B-3. Missing Import CSV Dialog
**Status:** GAP
**Spec (A-2):** "Import CSV" → dialog with format instructions, file picker, preview, progress bar.
**Current:** Not present.
**Fix:** Add button + dialog. CSV instructions, file picker (TODO platform-specific), preview area, progress bar, success snackbar.

### B-4. Missing Analytics Summary Card on Students
**Status:** GAP
**Spec (A-2):** Total students, gender distribution, class distribution mini-chart.
**Current:** No analytics summary. Just a flat list.
**Fix:** Add analytics card at top of Students sub-tab. Data from StudentAnalyticsViewModel. VChartsPremium bar chart for class distribution.

### B-5. Missing Batch Graduation Flow
**Status:** GAP
**Spec (A-2):** Checkboxes → "Graduate Selected" → confirm dialog with year selector.
**Current:** `onGraduateStudents` callback exists in shell but no UI.
**Fix:** Add checkbox to each student card. When selected → show "Graduate Selected" button. Tap → VConfirmDialogPremium: warning, "Graduate N students?", year dropdown. "Graduate" (Primary) + "Cancel". On confirm → call onGraduateStudents.

### B-6. Missing Add Staff Dialog
**Status:** GAP
**Spec (A-2):** "Add Staff" → dialog: Name, Role (dropdown), Department, Phone, Email.
**Current:** Not present.
**Fix:** Add button + dialog with form fields. Role as dropdown. Validation: phone + email format, required fields.

### B-7. Missing Deactivate Teacher with Confirmation
**Status:** GAP
**Spec (A-2):** Per teacher card: "Deactivate" → VConfirmDialog.
**Current:** No deactivate action.
**Fix:** Add "Deactivate" action to teacher cards (button or dropdown menu). Tap → VConfirmDialogPremium: warning, "Deactivate [Name]?", "They will lose access." "Deactivate" (Destructive) + "Cancel". On confirm → API + snackbar + refresh.

### B-8. Teacher Cards Missing Key Information
**Status:** PARTIAL GAP
**Spec (A-2):** Avatar, name, employee ID, subjects, class assignments, status badge. "View", "Assign classes", "Deactivate".
**Current:** VListTilePremium with name, role, "X classes" trailing. No avatar, no employee ID, no subjects, no status, no action buttons.
**Fix:** Enrich with VAvatarPremium + name + employee ID + subjects (badges) + class count + status badge. Action buttons or dropdown menu.

### B-9. Student Cards Missing Key Information
**Status:** PARTIAL GAP
**Spec (A-2):** Avatar, name, class+section, roll number, parent name, parent phone, status. Checkbox for batch graduation.
**Current:** VListTilePremium with name and "Class - Section". No roll, no parent info, no status, no checkbox.
**Fix:** Enrich with avatar + name + class/section/roll + parent name/phone + status badge + checkbox.

### B-10. Staff Cards Missing Key Information
**Status:** PARTIAL GAP
**Spec (A-2):** Avatar, name, role, department, phone, email, status badge.
**Current:** VListTilePremium with name and role. No department, phone, email, status.
**Fix:** Enrich with avatar + name + role + department + phone + email + status badge.

### B-11. Missing Pull-to-Refresh + AnimatedContent + Staggered Entrance
**Fix:** Add VPullRefreshPremium per sub-tab. AnimatedContent for sub-tab switches. VStaggeredItem for entrance.

## C. RECORDS TAB GAPS

### C-1. Missing Recalculate Button on Pace Sub-tab
**Spec (A-3):** "Recalculate" button → loading → refresh.
**Current:** No recalculate button.
**Fix:** Add VSecondaryButton "Recalculate". Tap → loading state. On complete → snackbar "Pace recalculated".

### C-2. Missing Pace Alert Resolution Dialog
**Spec (A-3):** "Resolve" → dialog with note field.
**Current:** Direct call to `paceViewModel.resolveAlert(alert.id)` — no dialog, no note.
**Fix:** Replace with dialog: alert summary + VTextInput note field + "Resolve" (Primary) + "Cancel". On confirm → resolveAlert with note. Success → snackbar.

### C-3. Attendance Sub-tab Missing Progress Ring + Date Selector
**Spec (A-3):** VProgressRing + class-wise breakdown + date selector (VDatePicker).
**Current:** VStatCardPremium + flat list. No ring, no date selector.
**Fix:** Replace with VProgressRing card. Add "Select Date" → VDatePickerPremium → loads historical. Class breakdown with trend. Tap class → DailyAttendance overlay.

### C-4. Attendance Sub-tab Missing Tap Class → DailyAttendance
**Current:** Class rows have `onClick = {}`.
**Fix:** Wire tap → onOpenDailyAttendance callback → shell opens DailyAttendance overlay.

### C-5. Marks Sub-tab Missing Subject-wise Bar Chart
**Spec (A-3):** VChartsPremium bar chart for subject-wise averages.
**Current:** Flat list of assessment rows. No chart.
**Fix:** Add VChartsPremium bar chart at top. Below: class-wise breakdown. Tap class → ClassPerformance overlay.

### C-6. Marks Sub-tab Missing Tap Class → ClassPerformance
**Current:** Assessment rows have `onClick = {}`.
**Fix:** Wire tap → onOpenClassPerformance callback.

### C-7. Fee Sub-tab Missing Top 10 Defaulters
**Spec (A-3):** Top 10 defaulters list. Tap → StudentProfile.
**Current:** Shows recent fee items but no defaulters list.
**Fix:** Add "Top Defaulters" section. Each: student name, class, amount overdue, days overdue. Tap → StudentProfile overlay.

### C-8. Fee Sub-tab Missing Tap Defaulter → StudentProfile
**Current:** Fee rows have `onClick = {}`.
**Fix:** Wire tap → onOpenStudent callback.

### C-9. Missing Pull-to-Refresh + AnimatedContent + Staggered Entrance
**Fix:** Add VPullRefreshPremium per sub-tab. AnimatedContent for sub-tab switches. VStaggeredItem for entrance.

## D. COMMS TAB GAPS

### D-1. Missing "New" Button on Announcements
**Status:** CRITICAL
**Spec (A-4):** "New" button (primary, plus icon) → CreateEvent overlay.
**Current:** No "New" button.
**Fix:** Add VPrimaryButton with plus icon at top of Announcements sub-tab. Tap → onCreateEvent.

### D-2. Missing "Scheduled" Button
**Spec (A-4):** "Scheduled" button (ghost, clock icon) → ScheduledMessages overlay.
**Current:** Not present.
**Fix:** Add VSecondaryButton with clock icon. Tap → onOpenScheduledMessages.

### D-3. Missing Category Filter Chips
**Spec (A-4):** Category filter chips (All + categories from data).
**Current:** No filter chips.
**Fix:** Add horizontal scrollable VFilterChip row. "All" + categories from data. Selecting filters the list.

### D-4. Announcement onClick Not Wired
**Status:** CRITICAL
**Spec (A-4):** Tap → detail leaf (full screen, back header, title, date, category badge, body).
**Current:** `onClick = {}` — not wired.
**Fix:** Wire tap → in-screen announcement detail leaf. State-driven view swap within Announcements sub-tab. VBackHeader + scrollable detail (title, date, category badge, body text, posted by).

### D-5. Missing Pull-to-Refresh + AnimatedContent
**Fix:** Wrap in VPullRefreshPremium. AnimatedContent for sub-tab switches.

## E. SETTINGS TAB GAPS

### E-1. Missing Institutional Profile Health Card
**Status:** PARTIAL GAP
**Spec (A-5):** Expandable card: school icon + name + completion VProgressRing + progress bar + media storage + visibility + learning model + language badges.
**Current:** Basic VStatCardPremium with "X% Profile Completion".
**Fix:** Replace with expandable `InstitutionalProfileHealthCard`: school icon + name + VProgressRing + progress bar. Expandable body: media storage (used/total + bar), visibility badge, learning model badge, language badge. Tap → EditProfile.

### E-2. Missing Data Export Row
**Spec (A-5):** "Data export — CSV / PDF / UDISE" with "Coming Soon" badge.
**Fix:** Add row with "Coming Soon" badge (not tappable).

### E-3. Missing Help & Support Row
**Spec (A-5):** "Help & support" → opens mailto.
**Fix:** Add row. Tap → opens mailto link (platform-specific).

### E-4. Missing Logout Confirmation Dialog
**Status:** CRITICAL
**Spec (A-5):** "Logout" → VConfirmDialog: alert triangle, "Log out?", "You'll be signed out." "Log out" (Destructive) + "Cancel".
**Current:** Logout row directly calls onLogout — no confirmation.
**Fix:** Add VConfirmDialogPremium. Show on tap. "Log out" (Destructive) → onLogout. "Cancel" → dismiss.

### E-5. Missing SuperAdmin-Specific Features
**Status:** GAP (Changelog Entry #1)
**Spec (A-0, A-5):** SuperAdmin sees "All Schools" card on Home + "Dev Tools" in Settings.
**Current:** No role-based gating.
**Fix:** Pass entryRole to SchoolPortalPremium. Conditionally render "Dev Tools" row in Settings if SuperAdmin. "Dev Tools" → DevTools overlay or TODO.

### E-6. Missing Admissions CRM + Leave Requests Entry Points
**Status:** GAP
**Fix:** Add "Admissions CRM" row in Settings. Add "Leave Requests" row or action card in People tab.

## F. DEAD CODE & ORPHANED SCREENS

### F-1. Dead: SchoolOverlayPremium.Calendar (Legacy)
**Changelog Entry #19:** Enum value exists, has `when` branch, but NO code ever sets it. Home's onOpenCalendar → AcademicCalendarPlatform. Deep link → AcademicCalendarPlatform.
**Fix:** Remove `Calendar` from enum. Remove `when` branch. Verify no references.

### F-2. Dead: SchoolOverlayPremium.Results
**Changelog Entry #20:** Enum value exists, has `when` branch, but NO code sets it. Deep links → ReportPublish.
**Fix:** Remove `Results` from enum. Remove `when` branch. Delete or merge `ResultsPublishPremium.kt` into `AdminReportPublishPremium.kt`.

### F-3. Orphaned: PewsEffectivenessPremium.kt
**Changelog Entry #17:** File exists but not wired. `ReportEffectiveness` → `AdminReportingEffectivenessPremium` (different file).
**Fix:** Compare files. If overlapping → delete. If unique PEWS metrics → merge as sub-tab in AdminReportingEffectivenessPremium.

### F-4. Orphaned: SchoolDayConfigScreen
**Changelog Entry #16:** File exists, not wired. Needed by timetable system.
**Fix:** Wire "Day Configuration" button in ClassesSubjectsPremium → Schedule tab → SchoolDayConfigScreen.

## G. OVERLAYS NOT WIRED FROM UI (Deep Link Only)

### G-1. DailyAttendance — Records → Attendance → tap class
### G-2. ClassPerformance — Records → Marks → tap class
### G-3. TeacherPerformance — Home → KPI grid or People → teacher
### G-4. LeaveRequests — Home → pending approvals or People → action card
### G-5. AdmissionsCRM — Home → quick action or Settings → row
### G-6. StudentRoster — People → Students (alternative view button)
**Fix for all:** Add UI entry points. Wire callbacks in shell.

## H. OVERLAY QUALITY GAPS

### H-1. Analytics Dashboard: Missing Charts + Filters
**Spec (A-6.15):** KPI cards → trend charts (enrollment, attendance, fee, academic) → date range/class filters.
**Current:** Hero card + flat list. No charts, no filters.
**Fix:** Add VChartsPremium line charts for 4 trends. Add date range dropdown + class filter dropdown. Charts respond to filters.

### H-2. Library: Missing 14 Sub-tabs
**Spec (A-6.34):** 14 tabs: Dashboard · Books · Copies · Issues · QuickIssue · BulkReturn · Categories · Audit · Announcements · Acquisition · Reservations · History · More · Settings.
**Current:** Just dashboard view. No sub-tabs.
**Fix:** Add VTopTabsPremium with all 14 tabs. Implement content for each with loading/empty/error states.

### H-3. Transport: Missing Routes/Vehicles/Students Tabs
**Spec (A-6.28):** Tabs: Routes · Vehicles · Students.
**Current:** Hero stats + flat route list. No tab separation.
**Fix:** Add VTopTabsPremium. Routes: route list with stops/timing/vehicle. Vehicles: vehicle list with number/capacity/driver. Students: assignments with route/stop.

### H-4. Messages: onOpenThread Not Wired + No Compose New
**Spec (A-6.06):** Tap thread → conversation view. "Compose new" → thread picker.
**Current:** `onOpenThread = { _ -> }` — empty. No compose button.
**Fix:** Wire onOpenThread → conversation view (message bubbles + compose bar). Add "Compose new" button → thread picker (searchable parent list).

### H-5. Calendar: onOpenEvent Not Wired
**Spec (A-6.03):** Tap event → event detail.
**Current:** `onOpenEvent = {}` — empty.
**Fix:** Wire to show event detail (title, date, time, category, description, attendees count).

### H-6. ID Cards: Verify Templates/Generate/Cards Sub-tabs
**Spec (A-6.33):** 3 sub-tabs: Templates · Generate · Cards.
**Fix:** Ensure all 3 tabs present with loading/empty/error states.

### H-7. Classes & Subjects: Verify All 4 Tabs + Day Config
**Spec (A-6.37):** Classes · Subjects · Schedule · Exceptions & Requests. Schedule includes "Day Configuration" button.
**Fix:** Ensure all 4 tabs. Add "Day Configuration" button to Schedule tab → SchoolDayConfigScreen.

### H-8. PTM: Verify Slot Booking Overview
**Spec (A-6.11):** Create PTM form + existing PTM events list with booking details.
**Fix:** Ensure: create form, existing events list, tap event → booking details (parents booked, available slots).

### H-9. Scholarship: Verify Application Approve/Reject
**Spec (A-6.31):** Scheme list → tap → application list. Approve/reject with confirmation. "Add Scheme" button.
**Fix:** Ensure all flows present with confirmation dialogs.

### H-10. Branding: Verify Preview Pane
**Spec (A-6.32):** Logo upload, color pickers, subdomain, preview pane showing parent app with branding.
**Fix:** Ensure preview pane exists and updates live.

### H-11. Edit Profile: Verify All Fields
**Spec (A-6.16):** School name, board, address, principal, phone, email, website, logo, gallery, learning model, language, visibility toggle.
**Fix:** Ensure all fields present with VTextInput. Dropdowns for learning model + language. Switch for visibility. Save with saving/success/error states.

### H-12. Create Event Wizard: Verify 7-Step Flow
**Spec (A-6.04):** 7 steps: Event type → Title → Date & time → Recurrence → Audience → Category → Review & publish. Progress indicator. Validation per step.
**Fix:** Verify all 7 steps. Add VProgressBar step indicator. Validate before advancing. "Publish" → success snackbar → close.

# ═══════════════════════════════════════════════════════════════════════════
# UI QUALITY ISSUES — Distortion, Polish, Premium Feel
# ═══════════════════════════════════════════════════════════════════════════

## Q-1. Home Tab is a Flat List of Identical Cards
**Problem:** Home is a vertical column of VStatCardPremium — "Analytics", "At-Risk", "Transport", "Reports", "Events" — all same visual weight. No hierarchy. AI slop.
**Fix:** Rebuild with full spec layout: greeting hero → insights carousel → pulse gauge → quick actions → KPI grid → comms mini → events → spotlight → achievements → birthdays → activity feed. Use varied card types (VGradientHeroPremium, VProgressRing, HorizontalPager, VChartsPremium). NOT a flat list.

## Q-2. People Tab is a Flat List of VListTilePremium
**Problem:** All people shown as identical VListTilePremium rows. No avatars, no status badges, no action buttons, no visual richness.
**Fix:** Enrich cards with VAvatarPremium, VBadge for status, action buttons or dropdown menus. Teachers: subjects as badges. Students: class/section/roll/parent info. Staff: department/contact. Use card layout, not flat list rows.

## Q-3. Records Tab is Flat Lists with No Visual Data
**Problem:** All sub-tabs show flat lists of VListTilePremium or VStatCardPremium. No charts, no progress rings, no visual data representation.
**Fix:** Add visual data: Attendance → VProgressRing + class bars. Marks → VChartsPremium bar chart. Fee → progress bars for collection %. Coverage → VProgressBar per department. Pace → status badges with color coding (Ahead=green, On Track=blue, Behind=red).

## Q-4. Missing Content-Specific Skeletons
**Problem:** Most screens use VShimmerListPremium — generic. Not tailored to content shape.
**Fix:** Use content-specific skeletons:
- Home: VShimmerCardPremium for hero, VShimmerListPremium for activity feed
- People: VShimmerListPremium with avatar placeholder shape
- Records: VShimmerCardPremium for stat cards, VShimmerListPremium for lists
- Comms: VShimmerListPremium for announcements
- Overlays: VShimmerCardPremium for hero, VShimmerListPremium for content

## Q-5. Missing Premium Error States
**Problem:** VStateHostPremium errors show plain text. Not premium.
**Fix:** Error state: error icon (VIcons.AlertCircle), friendly message, "Retry" button (VSecondaryButton). VColors.ErrorContainer background.

## Q-6. Missing Actionable Empty States
**Problem:** Empty titles are plain text. Not actionable.
**Fix:** Empty states must be actionable: "No teachers yet" → "Add Teacher" button. "No announcements yet" → "Create Announcement" button. "No students yet" → "Add Student" button. Use VEmptyStatePremium with action button.

## Q-7. Missing Pull-to-Refresh on All Tabs
**Fix:** Wrap each tab's content in VPullRefreshPremium. On refresh → reload all ViewModels for that tab.

## Q-8. Missing Staggered Entrances
**Fix:** Wrap content sections in VStaggeredItem with incremental delays (0ms, 50ms, 100ms, 150ms...). Content cascades in, not pops.

## Q-9. Missing AnimatedContent for Sub-tab Transitions
**Fix:** Use AnimatedContent with slide + fade transition for sub-tab switches in People, Records, Comms. Slide direction based on tab index change.

## Q-10. Overlay Scaffold Lacks Premium Polish
**Problem:** SchoolOverlayScaffold is basic: Column with back button + content. No gradient, no premium feel.
**Fix:** Enhance: (a) subtle gradient on header background, (b) VColors.SurfaceContainerLow for header, (c) subtle shadow at header bottom, (d) 140dp bottom padding, (e) staggered entrance for content.

## Q-11. Bottom Nav Needs Premium Polish
**Fix:** Ensure: animated active indicator (pill shape that slides), icon + label always visible, 48dp tap targets, active = VColors.Primary, inactive = VColors.OnSurfaceVariant. Subtle scale animation on tap.

## Q-12. Top Bar Needs Contextual Awareness
**Problem:** No contextual top bar. Other tabs just have a Text title.
**Fix:** Each tab: Home → greeting + bell + calendar icon. People → "People" + search icon. Records → "Records" + date filter. Comms → "Communications" + badge count. Settings → "Settings" + school name.

## Q-13. Cards Lack Elevation Hierarchy
**Fix:** Establish hierarchy:
- Hero cards (VGradientHeroPremium): gradient background
- Primary cards (KPIs, pulse): SurfaceContainerLowest + 2dp shadow
- Secondary cards (lists, tiles): SurfaceContainerLow + 0dp shadow
- Tertiary cards (contextual): SurfaceContainer + 0dp shadow

## Q-14. Text Hierarchy Inconsistent
**Fix:** Audit all text. Ensure: GreetingTitle for screen titles, SectionHeader for sections, UpdateTitle for cards, UpdateText for body, NavLabel for captions, StatValue for metrics. Remove all hardcoded fontSize/sp.

## Q-15. Color Usage Not Tonal
**Fix:** Replace all raw Color references with VColors tokens. Exception: Color.White for text on gradient/colored backgrounds where appropriate.

## Q-16. Missing Haptic Feedback
**Fix:** Add `LocalHapticFeedback.current.performHapticFeedback(HapticFeedbackType.LongPress)` on button taps, card taps, tab switches, destructive confirmations.

## Q-17. Missing Offline State Handling
**Fix:** Add offline banner on Home when offline. Show cached data with "Offline" indicator. Queue actions with "Will send when online" indicator.

## Q-18. Missing Deep Link Verification
**Fix:** Verify all admin deep links work: /school/notifications, /school/calendar, /school/scholarships, /school/leave, /school/messages, /school/admissions, /school/health-records, /school/pews, /school/transport, /school/id-cards, /school/library, /school/events, /school/branding, /school/classes, /school/ptm, /school/scheduled-messages, /school/analytics, /school/daily-attendance, /school/class-performance, /school/teacher-performance, /school/student-roster, /school/edit-profile, /school/staff, /school/report-effectiveness, /school/report-card, /school/pace-alerts, /school/alumni, /school/link-requests.

# ═══════════════════════════════════════════════════════════════════════════
# ITERATION PLAN — Execution Order
# ═══════════════════════════════════════════════════════════════════════════

## ITERATION 1: Foundation & Shared Components
- [ ] Build `InsightCarouselCard` — metric value + label + trend arrow + comparison text
- [ ] Build `PulseGaugeWidget` — VProgressRing + 4 sub-metric VProgressBar bars
- [ ] Build `QuickActionRow` — horizontal scroll of circular icon buttons with labels
- [ ] Build `ActivityFeedItem` — avatar + action text + timestamp
- [ ] Build `OnboardingNudgeCard` — completion % + "Resume" link
- [ ] Build `AddTeacherDialog` — form fields + validation + submitting state
- [ ] Build `AddStudentDialog` — form fields + dropdowns + validation
- [ ] Build `AddStaffDialog` — form fields + dropdown + validation
- [ ] Build `GraduateConfirmationDialog` — checkbox selection + year selector
- [ ] Build `AnnouncementDetailLeaf` — back header + scrollable detail
- [ ] Build `InstitutionalProfileHealthCard` — expandable + progress ring + storage
- [ ] Compile and verify

## ITERATION 2: Home Tab Rebuild
- [ ] Add onboarding nudge card (conditional on profile completion)
- [ ] Add greeting hero with notification bell (properly wired) + calendar icon
- [ ] Replace flat KPI grid with insights carousel (HorizontalPager)
- [ ] Add pulse gauge widget (VProgressRing + sub-metrics)
- [ ] Add quick actions row (6 circular icon buttons)
- [ ] Add 2-up KPI grid (class avg → ClassPerformance, teacher index → TeacherPerformance)
- [ ] Add comms center mini (3 latest announcements + "View all")
- [ ] Add upcoming events (3 events + "View all" → Calendar)
- [ ] Add teacher spotlight card
- [ ] Add recent achievements list
- [ ] Add today's birthdays
- [ ] Add activity feed (10 items + "View all" → Analytics)
- [ ] Add staggered entrance animations
- [ ] Add pull-to-refresh
- [ ] Add skeleton loading states (content-specific)
- [ ] Add SuperAdmin "All Schools" card (conditional)
- [ ] Wire all callbacks in shell
- [ ] Compile and verify

## ITERATION 3: People Tab Rebuild
- [ ] Enrich teacher cards (avatar, employee ID, subjects, status, actions)
- [ ] Enrich student cards (avatar, class/section/roll, parent info, status, checkbox)
- [ ] Enrich staff cards (avatar, role, department, phone, email, status)
- [ ] Add "Add Teacher" button + dialog
- [ ] Add "Add Student" + "Import CSV" buttons + dialogs
- [ ] Add analytics summary card on Students sub-tab
- [ ] Add batch graduation flow (checkboxes + "Graduate Selected" + confirm dialog)
- [ ] Add "Add Staff" button + dialog
- [ ] Add "Deactivate" teacher with VConfirmDialog
- [ ] Add filter chips (class for students, subject for teachers, department for staff)
- [ ] Add pull-to-refresh per sub-tab
- [ ] Add AnimatedContent transitions between sub-tabs
- [ ] Add staggered entrance animations
- [ ] Wire Student Roster overlay entry point
- [ ] Wire Leave Requests entry point (People → action card)
- [ ] Compile and verify

## ITERATION 4: Records Tab Rebuild
- [ ] Attendance: add VProgressRing + date selector (VDatePickerPremium) + tap class → DailyAttendance
- [ ] Marks: add VChartsPremium bar chart + tap class → ClassPerformance
- [ ] Fee: add top 10 defaulters list + tap defaulter → StudentProfile
- [ ] Pace: add "Recalculate" button + resolution dialog with note field
- [ ] Coverage: add VProgressBar for each department + color-coded status
- [ ] Add pull-to-refresh per sub-tab
- [ ] Add AnimatedContent transitions between sub-tabs
- [ ] Add staggered entrance animations
- [ ] Wire all drill-down callbacks in shell
- [ ] Compile and verify

## ITERATION 5: Comms Tab Rebuild
- [ ] Add "New" button (primary, plus icon) → CreateEvent
- [ ] Add "Scheduled" button (ghost, clock icon) → ScheduledMessages
- [ ] Add category filter chips
- [ ] Wire announcement tap → announcement detail leaf
- [ ] Add pull-to-refresh
- [ ] Add AnimatedContent between sub-tabs
- [ ] Add staggered entrance animations
- [ ] Add skeleton loading (VShimmerListPremium)
- [ ] Compile and verify

## ITERATION 6: Settings Tab Rebuild
- [ ] Replace basic profile card with InstitutionalProfileHealthCard (expandable)
- [ ] Add "Data export" row (Coming Soon badge)
- [ ] Add "Help & support" row (mailto)
- [ ] Add logout confirmation dialog (VConfirmDialogPremium)
- [ ] Add SuperAdmin "Dev Tools" row (conditional)
- [ ] Add "Admissions CRM" row
- [ ] Add "Leave Requests" row
- [ ] Verify all settings rows match architecture spec (13 rows)
- [ ] Add staggered entrance animations
- [ ] Compile and verify

## ITERATION 7: Overlays — Analytics + PEWS + Reports
- [ ] Analytics Dashboard: add trend charts (VChartsPremium) + date/class filters
- [ ] PEWS Cohort: verify risk distribution chart + student list sorted by risk
- [ ] PEWS Student Detail: verify risk gauge + factor breakdown + interventions
- [ ] Report Publish: verify class/term selector + preview list + publish confirmation
- [ ] Report Effectiveness: verify delivery stats + engagement metrics
- [ ] Merge or delete PewsEffectivenessPremium.kt (Changelog #17)
- [ ] Compile and verify

## ITERATION 8: Overlays — People-Related
- [ ] Student Profile: verify tabbed sections (Academic, Fees, Health, PEWS)
- [ ] Teacher Profile: verify assignments + performance + leave history
- [ ] Teacher Assignments: verify add/remove assignment flow
- [ ] Staff Profile: verify contact + employment details
- [ ] Health Records: verify health profile + incident log + "Add Incident"
- [ ] Alumni: verify directory + donations + mentorship + analytics
- [ ] Alumni Detail: verify full profile
- [ ] Alumni Campaign: verify campaign details + donor list
- [ ] Link Requests: verify approve/reject flow
- [ ] Leave Requests: verify approve/reject flow with reason dialog
- [ ] Compile and verify

## ITERATION 9: Overlays — Academic & Operations
- [ ] Academic Calendar Platform: verify month/week/day views + event cards + filter + FAB
- [ ] Create Event Wizard: verify 7-step flow + progress indicator + validation
- [ ] Academic Year Management: verify year/term CRUD + holiday list
- [ ] Classes & Subjects: verify 4 tabs (Classes, Subjects, Schedule, Exceptions) + Day Config button
- [ ] Class Detail: verify tabs (Students, Subjects, Timetable, Teachers)
- [ ] Schedule PTM: verify create form + existing events + booking details
- [ ] Daily Attendance: verify class/date selector + student toggle + summary + save
- [ ] Class Performance: verify metrics + subject breakdown + student ranking
- [ ] Teacher Performance: verify metrics + class breakdown
- [ ] Compile and verify

## ITERATION 10: Overlays — Operations & Admin
- [ ] Transport Management: add Routes/Vehicles/Students tabs + content per tab
- [ ] Scholarship Management: verify scheme list + application approve/reject + add scheme
- [ ] Branding Kit: verify logo upload + color pickers + subdomain + preview pane
- [ ] ID Cards: verify Templates/Generate/Cards sub-tabs
- [ ] Library: add all 14 sub-tabs with content + states
- [ ] Scheduled Messages: verify list + edit/cancel
- [ ] Event Registration: verify event details + registration list + export
- [ ] Admissions CRM: verify pipeline view + add inquiry
- [ ] Messages: wire onOpenThread → conversation view + compose new → thread picker
- [ ] Compile and verify

## ITERATION 11: Shell & Navigation Polish
- [ ] Remove dead enum values (Calendar, Results) from SchoolOverlayPremium
- [ ] Delete or merge orphaned files (ResultsPublishPremium, PewsEffectivenessPremium)
- [ ] Wire SchoolDayConfigScreen into Classes & Subjects → Schedule
- [ ] Wire all 6 deep-link-only overlays to UI entry points
- [ ] Add SuperAdmin role gating (All Schools card + Dev Tools)
- [ ] Polish bottom nav (animated indicator, haptics)
- [ ] Polish overlay scaffold (gradient header, shadow, staggered entrance)
- [ ] Add contextual top bars per tab
- [ ] Add offline state handling
- [ ] Verify all deep links
- [ ] Compile and verify

## ITERATION 12: Final Polish & Verification
- [ ] Run SELF-REVIEW RITUAL on every screen
- [ ] Fix any remaining empty onClick handlers
- [ ] Fix any remaining hardcoded values
- [ ] Fix any remaining clipping/overflow issues
- [ ] Add haptic feedback to all interactions
- [ ] Verify state completeness on every screen (loading/error/empty/loaded)
- [ ] Verify staggered entrances on every screen
- [ ] Verify pull-to-refresh on every tab
- [ ] Verify AnimatedContent on every sub-tab switch
- [ ] Final compile — ALL TARGETS GREEN

# ═══════════════════════════════════════════════════════════════════════════
# VERIFICATION CHECKLIST — RUN AFTER EACH ITERATION
# ═══════════════════════════════════════════════════════════════════════════

## Build Verification
- [ ] `./gradlew composeApp:compileDevDebugKotlinAndroid` — SUCCESS
- [ ] `./gradlew shared:compileKotlinJvm` — SUCCESS
- [ ] `./gradlew server:compileKotlin` — SUCCESS (if server changes made)
- [ ] Zero compiler warnings related to UI code
- [ ] Zero deprecation warnings from token usage

## Token Compliance
- [ ] Zero hardcoded Color(0x...) values — grep to verify
- [ ] Zero hardcoded dp for corner radii — all from VShapes
- [ ] Zero hardcoded fontSize/sp — all from VTypography
- [ ] Zero FontWeight.Bold without VTypography reference
- [ ] Zero Color.White / Color.Black usage (except text on gradients)
- [ ] All shapes use VShapes.* constants

## State Completeness (per screen)
- [ ] Loading state: skeleton matching content shape
- [ ] Error state: icon + message + "Retry" button
- [ ] Empty state: icon + actionable message + button
- [ ] Loaded state: polished, complete content
- [ ] Offline state: cached data + "Offline" indicator (where applicable)

## Interaction Quality
- [ ] Every tappable card has pressScale + shapeMorph
- [ ] Every tab switch has AnimatedContent transition
- [ ] Every screen has staggered entrance (VStaggeredItem)
- [ ] Every destructive action has VConfirmDialogPremium
- [ ] Every form has validation + submitting + success + error states
- [ ] Every scrollable has 140dp bottom padding
- [ ] Haptic feedback on button taps and tab switches

## Wiring & Data
- [ ] Zero empty onClick = { } handlers
- [ ] Zero hardcoded/fake data — all from ViewModels
- [ ] All ViewModels wired to real API endpoints
- [ ] All deep links verified and working
- [ ] All overlay entry points wired from UI (not deep-link-only)
- [ ] SuperAdmin role gating implemented

## Layout & Accessibility
- [ ] No content clipping on 360x640dp screen
- [ ] No buttons hidden behind bottom nav
- [ ] All tap targets ≥ 48dp
- [ ] All icons have contentDescription where needed
- [ ] AutoMirrored icons for directional icons
- [ ] Screen titles use VTypography.GreetingTitle
- [ ] Section titles use VTypography.SectionHeader

## Dead Code Cleanup
- [ ] SchoolOverlayPremium.Calendar removed from enum
- [ ] SchoolOverlayPremium.Results removed from enum
- [ ] ResultsPublishPremium.kt deleted or merged
- [ ] PewsEffectivenessPremium.kt deleted or merged
- [ ] SchoolDayConfigScreen wired into Classes & Subjects
- [ ] No orphaned admin screen files

# ═══════════════════════════════════════════════════════════════════════════
# KEY FILE REFERENCE
# ═══════════════════════════════════════════════════════════════════════════

## Shell & Navigation
- `composeApp/.../ui/v2/screens/premium/school/SchoolPortalPremium.kt` — main shell, tabs, overlays, deep links
- `composeApp/.../ui/v2/components/navigation/VBottomNav.kt` — bottom nav
- `composeApp/.../ui/v2/components/navigation/VTopTabsPremium.kt` — sub-tab navigation

## Tab Screens
- `composeApp/.../ui/v2/screens/premium/school/SchoolHomePremium.kt` — Home tab
- `composeApp/.../ui/v2/screens/premium/school/SchoolPeoplePremium.kt` — People tab
- `composeApp/.../ui/v2/screens/premium/school/SchoolRecordsPremium.kt` — Records tab
- `composeApp/.../ui/v2/screens/premium/school/SchoolCommsPremium.kt` — Comms tab
- `composeApp/.../ui/v2/screens/premium/school/SchoolSettingsPremium.kt` — Settings tab

## Overlay Screens (28 files in ui/v2/screens/premium/school/)
- AcademicCalendarPlatformPremium.kt
- AcademicYearManagementPremium.kt
- AdminEventRegistrationPremium.kt
- AdminReportPublishPremium.kt
- AdminReportingEffectivenessPremium.kt
- AdmissionsCrmPremium.kt
- AlumniCampaignPremium.kt
- AlumniDetailPremium.kt
- AlumniPremium.kt
- AnalyticsDashboardPremium.kt
- BrandingSettingsPremium.kt
- ClassDetailPremium.kt
- ClassPerformancePremium.kt
- ClassesSubjectsPremium.kt
- DailyAttendancePremium.kt
- EditSchoolProfilePremium.kt
- HealthRecordsPremium.kt
- IdCardPremium.kt
- LeaveRequestsPremium.kt
- LinkRequestsPremium.kt
- MessagesPremium.kt
- PaceAlertsPremium.kt
- PewsCohortPremium.kt
- PewsEffectivenessPremium.kt (ORPHANED — delete or merge)
- PewsStudentDetailPremium.kt
- ResultsPublishPremium.kt (DEAD CODE — delete or merge)
- SchedulePtmPremium.kt
- SchoolLibraryPremium.kt
- TransportManagementPremium.kt
- (+ Scholarship, ScheduledMessages, StudentProfile, TeacherProfile, etc.)

## Tokens
- `composeApp/.../ui/v2/tokens/VColors.kt`
- `composeApp/.../ui/v2/tokens/VShapes.kt`
- `composeApp/.../ui/v2/tokens/VTypography.kt`
- `composeApp/.../ui/v2/tokens/VMotion.kt`

## Architecture & Changelog
- `ENROLLPLUS_SCREEN_ARCHITECTURE_FINAL.md` — Part A (A-0 through A-6.38)
- `ENROLLPLUS_RESTRUCTURE_CHANGELOG_AND_BACKEND_MAPPING.md` — 22 entries

## Web Reference (data patterns only, NOT UI)
- `website/src/lib/admin/nav.ts` — admin navigation items
- `website/src/components/admin/AdminShell.tsx` — admin shell
- `website/src/components/admin/Sidebar.tsx` — admin sidebar
- `website/src/lib/admin/useDashboardUrlState.ts` — URL state management

# ═══════════════════════════════════════════════════════════════════════════
# END OF GOD MODE PROMPT — ADMIN/SCHOOL PORTAL
# Build with intention. Review with honesty. Ship with confidence.
# ═══════════════════════════════════════════════════════════════════════════

