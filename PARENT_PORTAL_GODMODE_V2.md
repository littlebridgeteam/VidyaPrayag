# ═══════════════════════════════════════════════════════════════════════════
# GOD MODE V2 — PARENT PORTAL PREMIUM REBUILD — COMPLETE PROMPT
# ═══════════════════════════════════════════════════════════════════════════
#
# Read this ENTIRE document before writing a single line of code.
#
# ═══════════════════════════════════════════════════════════════════════════
# 0. THE BRIEF
# ═══════════════════════════════════════════════════════════════════════════
#
# Nuke every parent portal screen. Build from scratch. Zero reference to the
# old code — it's broken, distorted, crashy, and looks like AI slop.
#
# The current screens and their CONCEPTS are wrong. Don't just swap styling —
# rethink the information architecture, the layout patterns, the navigation.
# The old UI had gamification (XP, levels, house badges), 10-card grids,
# journey progress rings — none of that belongs in a parent portal. Parents
# want INFORMATION, not ENTERTAINMENT. Start from a clean slate.
#
# The goal: a parent portal that feels like it was designed by Google's M3
# team and engineered by someone who actually cares. Not try-hard. Not
# gradient soup. Not glassmorphism on everything. Just clean, confident,
# premium UI that an Indian parent with a first-time smartphone can use
# instinctively.
#
# ZERO OLD UI ELEMENTS SHOULD BE RENDERED AFTER REBUILD.
# No old composable, no old layout pattern, no old card style, no old
# gradient, no old gamification widget — NOTHING from the old UI survives.
# If any old element is still rendering after the rebuild, the rebuild
# failed. Delete and redo.
#
# ═══════════════════════════════════════════════════════════════════════════
# 1. THE USER — WHO IS HOLDING THIS PHONE?
# ═══════════════════════════════════════════════════════════════════════════
#
#   Indian parents. Age 25–55+. Mother or father or grandparent.
#   Low-to-medium digital literacy. WhatsApp is their mental model for
#   every app. They know how to chat, send photos, and scroll. Everything
#   else is unfamiliar.
#
#   Their phone is often shared between family members. Connectivity is
#   patchy — 4G drops, WiFi is slow. They may prefer Hindi or a regional
#   language. They are busy. They don't have time to "explore the app."
#   They open it, glance, and close it. If they can't find what they need
#   in 5 seconds, they call the school instead.
#
#   They care about: Is my child in school today? How are their marks?
#   Is there a fee due? Did the teacher send a message? Is the bus coming?
#
#   They do NOT care about: Gamification, XP, levels, journey progress
#   rings, house badges, "player card aesthetics." That is designer
#   masturbation. The parent wants INFORMATION, not ENTERTAINMENT.
#
#   DESIGN CALIBRATION:
#   • WhatsApp mental model — chat-like patterns, feed-like layouts
#   • Comfortably readable text. NOT oversized "accessibility large" —
#     just clear, readable, comfortable. Think 14-16sp body, 18-22sp
#     headers. Don't make text huge — that wastes screen space and
#     forces more scrolling. Don't make it tiny either. Find the
#     sweet spot that a 50-year-old with reading glasses can scan
#     without squinting, but that doesn't feel like a children's app.
#   • Icon + label ALWAYS paired. Never icon-only.
#   • Plain language. "Fee Due" not "Outstanding Ledger Balance."
#   • Generous tap targets. 48dp minimum.
#   • Forgiving flows. If they make a mistake, they can go back.
#   • Offline tolerance. Show what you have. Queue actions. Don't crash.
#
# ═══════════════════════════════════════════════════════════════════════════
# 2. DESIGN LANGUAGE — MATERIAL 3 EXPRESSIVE (2025)
# ═══════════════════════════════════════════════════════════════════════════
#
#   SURFACE SYSTEM:
#   • Base is sophisticated grey (VColors.Surface) — like Apple's Settings
#   • Cards lift via M3 tonal elevation: SurfaceContainerLowest > Low >
#     SurfaceContainer > High > Highest
#   • Depth WITHOUT shadows. Shadows only on FABs/floating elements.
#
#   COLOR:
#   • One primary accent per section. VColors.Primary for main action.
#     VColors.Tertiary for secondary. VColors.WarmOrange for warnings.
#   • No rainbow. Grey base makes accent colors POP.
#   • Color is functional, not decorative.
#
#   SHAPE:
#   • VShapes.Xl (24dp) for cards. VShapes.Lg (16dp) for inner elements.
#     VShapes.Full for pills/badges. Consistent.
#   • Shape morphs on press: Xl → TwoXl. Subtle.
#
#   MOTION:
#   • 200–400ms range. pressScale (0.97–0.98) on every tappable card.
#   • Staggered entrance: 0–300ms cascade. AnimatedContent for tab switches.
#   • Motion has meaning. Guides the eye. Confirms action.
#
#   TYPOGRAPHY:
#   • VTypography tokens. Never hardcode fontSize, fontWeight, or sp.
#   • Hierarchy: GreetingTitle > SectionHeader > UpdateTitle > UpdateText
#     > NavLabel. Clear, obvious, consistent.
#   • Font sizes COMFORTABLE, NOT LARGE. Use VTypography tokens as-is.
#     Don't override to larger sizes — they're already sized for
#     comfortable readability. Oversized text wastes screen space and
#     causes layout overflow.
#
# ═══════════════════════════════════════════════════════════════════════════
# 3. WHAT "PREMIUM" MEANS HERE (AND WHAT IT DOESN'T)
# ═══════════════════════════════════════════════════════════════════════════
#
# PREMIUM IS:
#   ✓ Grey base with tonal surface hierarchy — muted, confident, expensive
#   ✓ One accent color per section — pops against the grey
#   ✓ Generous whitespace — 20–24dp horizontal, 16–24dp between sections
#   ✓ Clear information hierarchy — eye flows from hero → section → card
#   ✓ State completeness — loading skeletons, error cards, empty states
#   ✓ Subtle motion — pressScale, shapeMorph, staggered entrance
#   ✓ Consistent shape language — Xl for cards, Lg for inner, Full for pills
#   ✓ Every piece of data from a real API — zero hardcoded data
#   ✓ Every button wired — no empty onClick = { }
#   ✓ 140dp bottom padding on scrollable content — dock clearance
#   ✓ ALL feature buttons/options visible and accessible — no hidden
#     actions, no overflow menus for primary features, no "scroll to find"
#     a critical button. If a parent needs to pay fees, the Pay button
#     must be obvious and reachable without hunting.
#
# PREMIUM IS NOT:
#   ✗ Gradients on every card
#   ✗ Glassmorphism everywhere
#   ✗ Drop shadows on every card
#   ✗ Gamification (XP, levels, house badges) for parents
#   ✗ "Journey progress rings" — parents want marks, not RPG stats
#   ✗ Radial glow effects on cards
#   ✗ "LIVE" pulsing dots on hero cards
#   ✗ Hardcoded fake data
#   ✗ AI slop — flat list of identical cards, no visual hierarchy
#   ✗ Animation for animation's sake
#   ✗ Oversized fonts that waste screen space
#   ✗ Hidden features behind scroll — all key actions visible and reachable
#   ✗ The current UI concept — it's fundamentally wrong, start over
#
# ═══════════════════════════════════════════════════════════════════════════
# 4. NAVIGATION SYSTEM — PROPER AND RELIABLE
# ═══════════════════════════════════════════════════════════════════════════
#
# Navigation must be bulletproof. The current portal has broken navigation —
# taps that don't respond, overlays that don't close, back buttons that go
# to the wrong place. Fix ALL of this.
#
#   1. BOTTOM DOCK (5 tabs): Home | Academics | Fees | Conversations | Profile
#      - Each tab: icon + label (NEVER icon-only)
#      - Active tab: Primary tint + indicator bar
#      - Tap → switches content with 200ms crossfade
#      - Unread badge on Conversations if unread > 0
#      - Dock always visible, always tappable, never obscured
#
#   2. OVERLAYS (full-screen, slide from right):
#      - Open: 300ms slide-in from right (EaseEmphasized)
#      - Close: 300ms slide-out to right
#      - Back button (top-left, 40dp circle, ArrowBack icon)
#      - Back press closes overlay → returns to spawning tab
#      - Only ONE overlay open at a time
#      - Overlay renders ABOVE dock — dock hidden during overlay
#
#   3. BACK NAVIGATION HIERARCHY:
#      Overlay open → close overlay → spawning tab
#      Non-home tab → switch to Home tab
#      Home tab → exit app (or background)
#      NEVER: back from overlay → different overlay
#      NEVER: back from tab → exits app (unless Home tab)
#      NEVER: back button unresponsive or goes to wrong place
#
#   4. TAB INTERNAL NAVIGATION:
#      - Sub-tabs (Academics): chips at top, content swaps below
#      - Segments (Conversations): Messages | Announcements toggle
#      - Deep-linked sub-tab: LaunchedEffect sets selectedTab from deep link
#      - Back from sub-tab: goes to parent tab (not Home)
#
#   5. CONVERSATIONS INTERNAL NAVIGATION:
#      - Inbox → tap thread → conversation view (replaces inbox, NOT overlay)
#      - Conversation view: back press → returns to inbox
#      - Compose bar: always visible at bottom of conversation view
#      - NEVER: conversation opens as overlay (it's inline tab content)
#
#   6. DEEP LINK NAVIGATION:
#      - ParseDeepLink → DeepLinkTarget → Shell sets tab + overlay
#      - Deep link to /parent/messages → Conversations tab (NOT overlay)
#      - Deep link to /parent/academics/marks → Academics tab, Marks sub-tab
#      - Deep link survives config change (rememberSaveable)
#
# ═══════════════════════════════════════════════════════════════════════════
# 5. COMPOSE LAYOUT SAFETY — ZERO CRASH TOLERANCE
# ═══════════════════════════════════════════════════════════════════════════
#
# The #1 cause of the previous crash:
#   "Vertically scrollable component was measured with an infinity
#    maximum height constraints"
#
# RULE 1: NEVER nest verticalScroll inside verticalScroll.
# RULE 2: NEVER put LazyColumn inside Column(Modifier.verticalScroll()).
# RULE 3: NEVER use fillMaxSize() on a scrollable container's child.
# RULE 4: NEVER use fillMaxHeight() inside a verticalScroll.
# RULE 5: ALWAYS use weight(1f) on the content area in the shell.
# RULE 6: Header + scrollable list → use LazyColumn with header as item().
# RULE 7: VPullRefreshPremium must NOT add verticalScroll if content scrolls.
# RULE 8: ParentOverlayScaffold: weight(1f) on content, NOT fillMaxSize + scroll.
# RULE 9: AnimatedContent content handles own scrolling. No external wrap.
# RULE 10: Test on 360x640dp. If scroll inside scroll, restructure.
# RULE 11: ALL feature buttons VISIBLE and ACCESSIBLE. No clipping, no hidden
#   behind dock, no non-obvious scrolling to find critical buttons.
# RULE 12: No fixed heights on content areas that might grow. Use weight + scroll.
#
# ═══════════════════════════════════════════════════════════════════════════
# 6. TOKENS — NEVER HARDCODE
# ═══════════════════════════════════════════════════════════════════════════
#
# COLORS — VColors.* (M3 tonal palette, @Composable get()):
#   Primary, OnPrimary, PrimaryContainer, OnPrimaryContainer
#   Secondary, OnSecondary, SecondaryContainer, OnSecondaryContainer
#   Tertiary, OnTertiary, TertiaryContainer, OnTertiaryContainer
#   Error, OnError, ErrorContainer, OnErrorContainer
#   Surface, SurfaceDim, SurfaceBright
#   SurfaceContainerLowest, SurfaceContainerLow, SurfaceContainer
#   SurfaceContainerHigh, SurfaceContainerHighest
#   OnSurface, OnSurfaceVariant, Outline, OutlineVariant
#   WarmOrange, WarmOrangeContainer, LiveCyan
#   PrimaryMid, PrimaryDeep, TertiaryDeep, Scrim
#
# SHAPES — VShapes.*:
#   Xs=4dp, Sm=8dp, Md=12dp, Lg=16dp, Xl=24dp, TwoXl=28dp, Full=999dp
#   Also: XsDp, SmDp, MdDp, LgDp, XlDp, TwoXlDp for animateDpAsState
#
# TYPOGRAPHY — VTypography.*:
#   GreetingTitle, GreetingEyebrow, SectionHeader, SectionLink
#   UpdateTitle, UpdateText, NavLabel
#   StatValue, HwTitle, HwSub, HwStatus
#   ScheduleHour, ScheduleAmPm, ScheduleSubject, ScheduleTeacher, ScheduleStatus
#   SyllabusName, SyllabusPct
#
# MOTION — VMotion.*:
#   DurShort1, DurShort2, DurMedium1, DurMedium2, DurLong1
#   EaseEmphasized, EaseStandard
#
# NEVER: Color(0x...), Color.White, Color.Black, hardcoded dp for radii,
# hardcoded fontSize/sp, FontWeight.Bold without VTypography reference.
# NEVER: Override VTypography token sizes to be larger — they're already
# sized for comfortable readability.
#
# ═══════════════════════════════════════════════════════════════════════════
# 7. COMPONENTS — BUILDING BLOCKS
# ═══════════════════════════════════════════════════════════════════════════
#
# All in composeApp/.../ui/v2/components/. Use them. Don't reinvent.
#
# STATE: SkeletonCard, ErrorStateCard, EmptyStateCard
# CONTENT: VHeroCard, VFeesHeroCard, VProfileHeroCard, VQuickStatCard,
#   VUpdateCard, VFilterChip, VTopTabsPremium, VPrimaryButton,
#   VSecondaryButton, VTextInput, VSectionHeader, VGreetingTitle
# FEEDBACK: VConfirmDialogPremium, VSnackbarPremium, VDatePickerPremium,
#   VTimePickerPremium, VLanguagePickerPremium, VThemePickerPremium
# UTILITY: VAvatarPremium, VBrandLogoPremium, VPullRefreshPremium,
#   VChartsPremium, VShimmerPremium
# MODIFIERS: pressScale, shapeMorph, VStaggeredItem, radialGlow (sparingly)
#
# ═══════════════════════════════════════════════════════════════════════════
# 8. STATE MANAGEMENT — 4 STATES, EVERY SCREEN
# ═══════════════════════════════════════════════════════════════════════════
#
#   1. LOADING — SkeletonCard with shimmer. NOT "Loading..." text.
#   2. ERROR — ErrorStateCard with icon + message + "Retry" button.
#   3. EMPTY — EmptyStateCard with icon + friendly message + action button.
#   4. LOADED — The actual content. Polished. Complete.
#
# State priority: Loading > Error > Empty > Loaded.
# If loading AND have cached data: show cached + refresh indicator.
# If error AND have cached data: show cached + error banner.
#
# ═══════════════════════════════════════════════════════════════════════════
# 9. DATA FLOW — ZERO HARDCODED DATA
# ═══════════════════════════════════════════════════════════════════════════
#
# UI → ViewModel → Repository → API → Server → Database
#
# 1. NO inline fake data. 2. NO placeholder strings as real data.
# 3. NO mock models in composables. 4. Build missing API endpoints.
# 5. Create missing ViewModels. 6. Create missing Repositories.
# 7. Every onClick wired or has // TODO(reason). 8. Every form hits real endpoint.
#
# Use koinViewModel(), collectAsStateV2(), StateFlow data classes.
#
# ═══════════════════════════════════════════════════════════════════════════
# 10. DEEP LINK ARCHITECTURE
# ═══════════════════════════════════════════════════════════════════════════
#
#   /parent/home                    → Tab: Home
#   /parent/academics               → Tab: Academics
#   /parent/academics/attendance    → Tab: Academics, sub-tab: Attendance
#   /parent/academics/marks         → Tab: Academics, sub-tab: Marks
#   /parent/academics/syllabus      → Tab: Academics, sub-tab: Syllabus
#   /parent/academics/homework      → Tab: Academics, sub-tab: Homework
#   /parent/academics/quizzes       → Tab: Academics, sub-tab: Quizzes
#   /parent/academics/report        → Tab: Academics, sub-tab: Report
#   /parent/fees                    → Tab: Fees
#   /parent/conversations           → Tab: Conversations, segment: Messages
#   /parent/conversations/announcements → Tab: Conversations, segment: Announcements
#   /parent/profile                 → Tab: Profile
#   /parent/notifications           → Tab: Home, Overlay: Notifications
#   /parent/calendar                → Tab: Home, Overlay: Calendar
#   /parent/transport               → Tab: Home, Overlay: Transport
#   /parent/leave                   → Tab: Home, Overlay: Leave
#   /parent/scholarships            → Tab: Home, Overlay: Scholarships
#   /parent/health                  → Tab: Home, Overlay: Health
#   /parent/pulse                   → Tab: Home, Overlay: Pulse
#   /parent/tutor                   → Tab: Home, Overlay: TutorChat
#   /parent/tutor-progress          → Tab: Home, Overlay: TutorProgress
#   /parent/id-card                 → Tab: Home, Overlay: DigitalIdCard
#   /parent/library                 → Tab: Home, Overlay: Library
#   /parent/events                  → Tab: Home, Overlay: EventRegistration
#   /parent/link-child              → Tab: Profile, Overlay: LinkChild
#   /parent/account-settings        → Tab: Profile, Overlay: AccountSettings
#   /parent/messages/{threadId}     → Tab: Conversations, open thread
#   /parent/discovery               → Tab: Home, Overlay: Discovery
#   /parent/school-detail/{id}      → Tab: Home, Overlay: SchoolDetail
#
# Back from overlay → spawning tab. Back from non-home tab → Home. Back from Home → exit.
# /parent/messages → Conversations tab (NOT separate overlay).
#
# ═══════════════════════════════════════════════════════════════════════════
# 11. ANTI-SLOP RULES
# ═══════════════════════════════════════════════════════════════════════════
#
#   SLOP 1: Flat list of identical cards, no hierarchy.
#   SLOP 2: Every card has different gradient background.
#   SLOP 3: "LIVE" pulsing dots, radial glows, glassmorphism on cards.
#   SLOP 4: Gamification elements (XP, levels, house badges) for parents.
#   SLOP 5: Hardcoded data inline in composables.
#   SLOP 6: Empty onClick = { } handlers.
#   SLOP 7: "Loading..." text instead of skeleton shimmer.
#   SLOP 8: Random spacing values (7dp, 13dp, 22dp). Use 20-24dp/16-24dp/8-12dp.
#   SLOP 9: Content overflow or clipping on small screens.
#   SLOP 10: Looks like ChatGPT generated it in 10 seconds.
#   SLOP 11: ANY old UI element still rendering. Zero old elements survive.
#   SLOP 12: Feature buttons hidden or hard to reach. All primary actions visible.
#   SLOP 13: Oversized fonts wasting screen space. Use VTypography tokens as-is.
#
# ═══════════════════════════════════════════════════════════════════════════
# 12. CRITICAL RULES — ZERO TOLERANCE
# ═══════════════════════════════════════════════════════════════════════════
#
# 1.  NEVER hardcode Color(0x...) — use VColors.*
# 2.  NEVER hardcode dp for corner radii — use VShapes.*
# 3.  NEVER hardcode fontSize/fontWeight — use VTypography.*
# 4.  NEVER use Color.White or Color.Black
# 5.  NEVER nest verticalScroll inside verticalScroll — crash
# 6.  NEVER put LazyColumn inside Column(verticalScroll) — crash
# 7.  NEVER use fillMaxSize() on children inside verticalScroll — crash
# 8.  EVERY card → pressScale + shapeMorph
# 9.  EVERY screen → 4 states (loading, error, empty, loaded)
# 10. EVERY scrollable → 140dp bottom padding
# 11. EVERY onClick → wired or // TODO(reason)
# 12. EVERY piece of data → from ViewModel → API → backend
# 13. EVERY form → validation + submitting + success + error states
# 14. AutoMirrored icons for directional icons
# 15. Compile after EACH screen — fix errors immediately
# 16. Premium grey base (VColors.Surface) — never white-on-white
# 17. No AI slop — distinct visual identity, clear info hierarchy
# 18. No gamification for parents — information, not XP
# 19. No content overflow — all buttons/options visible on 360x640dp
# 20. No fixed heights on growing content — use weight + scroll
# 21. ALL feature buttons visible and accessible — no hidden actions
# 22. Fonts comfortable, NOT oversized — use VTypography tokens as-is
# 23. ZERO old UI elements rendered after rebuild — complete nuke
# 24. Navigation bulletproof — every tap responds, every back works
#
# ═══════════════════════════════════════════════════════════════════════════
# 13. NUKE LIST — FILES TO DELETE BEFORE REBUILD
# ═══════════════════════════════════════════════════════════════════════════
#
# All 29 files in composeApp/.../screens/premium/parent/ — DELETE ALL.
# ZERO old UI elements survive.
#
# SHELL & SCAFFOLD: ParentPortalShell.kt, ParentOverlayScaffold.kt
# TABS: ParentHomeScreen, ParentAcademicsScreen, ParentFeesScreen,
#   ParentConversationsScreen, ParentProfileScreen
# OVERLAYS: ParentNotificationsScreen, ParentCalendarScreen,
#   ParentScholarshipScreen, ParentAccountSettingsScreen, ParentLeaveScreen,
#   ParentMessagesScreen (REDUNDANT), ParentDiscoveryScreen,
#   ParentSchoolDetailScreen, ParentHealthScreen, ParentPulseScreen,
#   ParentTransportScreen, ParentTutorChatScreen, ParentTutorProgressScreen,
#   ParentDigitalIdCardScreen, ParentLibraryScreen, ParentEventsScreen
# SUB-SCREENS: ParentAnnouncementsScreen, ParentComposeMessageScreen,
#   ParentDailySummaryScreen, ParentHomeworkScreen, ParentQuizzesScreen,
#   ParentQuizDetailScreen, ParentReportCardScreen, ParentLeaderboardScreen
#
# VIEW MODELS (REUSE — DO NOT DELETE):
#   ParentDashboardViewModel, ParentHomeViewModel, ParentAcademicsViewModel,
#   ParentProfileViewModel, ParentMessageViewModel, ParentAnnouncementViewModel,
#   ParentLeaveViewModel, ParentPulseViewModel, ParentHealthViewModel,
#   ParentLibraryViewModel, ParentEventRegistrationViewModel,
#   ParentProgressViewModel, ParentNudgeViewModel
#
# ViewModels may need updates but DO NOT delete them. Only nuke UI.
#
# ═══════════════════════════════════════════════════════════════════════════
# 14. SCREEN INVENTORY — 22 NEW FILES
# ═══════════════════════════════════════════════════════════════════════════
#
# 1 Shell + 1 Scaffold + 5 Tabs + 14 Overlays + 1 Gate = 22 files
# (Down from 29 — removed redundant Messages overlay, merged sub-screens,
#   removed Leaderboard)
#
# ═══════════════════════════════════════════════════════════════════════════
# 15. SCREEN SPECS
# ═══════════════════════════════════════════════════════════════════════════
#
# ─── 15.1 PARENT PORTAL SHELL ───
# Type: Shell | Deep links: All /parent/*
# Layout: Column(fillMaxSize) { TopBar(fillMaxWidth) + Box(weight(1f)) {
#   AnimatedContent(tab) } + BottomDock(fillMaxWidth) }
# Overlays slide from right (300ms). Back: overlay→tab, tab→home, home→exit.
# CRITICAL: weight(1f) on content Box — bounded height prevents crash.
#
# ─── 15.2 PARENT OVERLAY SCAFFOLD ───
# Type: Scaffold | Layout: Column(fillMaxSize) { BackHeader(fillMaxWidth) +
#   Column(weight(1f) + verticalScroll) { content() + Spacer(140dp) } }
# CRITICAL FIX: weight(1f) NOT fillMaxSize on content area.
# Content inside must NOT have own verticalScroll or fillMaxSize.
#
# ─── 15.3 HOME TAB ───
# Type: Tab (scrollable) | ViewModel: ParentHomeViewModel (EXISTS)
# Deep links: /parent/home
# Layout (single verticalScroll, forEach — NO LazyColumn):
#   1. Greeting + child switcher (dropdown if multiple children)
#   2. Hero card: avatar + name + class + 3 quick stats (attendance, marks, fees)
#   3. Today's schedule: period cards (time, subject, teacher, status)
#   4. Quick actions grid (2-up): Fees, Messages, Attendance, Transport,
#      Health, Library, Scholarships, ID Card, Events, Calendar
#      ALL cards visible — no clipping on 360x640dp
#   5. Recent updates feed (max 5 items)
#   140dp bottom padding
# States: skeleton hero + cards | error + retry | empty + link child | loaded
#
# ─── 15.4 ACADEMICS TAB ───
# Type: Tab (sub-tabs) | ViewModel: ParentAcademicsViewModel (EXISTS)
# Deep links: /parent/academics, /parent/academics/{subtab}
# Layout: Column(fillMaxSize) { Row(horizontalScroll) { 7 filter chips } +
#   Column(weight(1f) + verticalScroll) { AnimatedContent(subtab) } }
# Sub-tabs: Overview, Attendance, Marks, Syllabus, Homework, Quizzes, Report
#   Overview: performance ring + subject breakdown bars
#   Attendance: summary ring + breakdown bars + monthly calendar grid
#   Marks: subject filter chips (Row+horizontalScroll) + assessment cards
#   Syllabus: coverage card + expandable subject cards
#   Homework: date chips + homework cards with status badges
#   Quizzes: quiz cards with status badges, tap → detail
#   Report: term report card with subject bars + grades + teacher remarks
# All content uses forEach inside parent scroll. No LazyColumn.
# 140dp bottom padding.
#
# ─── 15.5 FEES TAB ───
# Type: Tab (scrollable) | ViewModel: ParentHomeViewModel or dedicated
# Deep links: /parent/fees
# Layout (single verticalScroll):
#   1. Balance hero card: outstanding amount (large), "Pay Now" button
#      if amount > 0, "All fees paid" badge if 0, due date if applicable
#   2. Fee structure breakdown: fee items with Paid/Due/Overdue badges
#   3. Payment history: date, amount, receipt number, download button
#   4. Fee announcements: title, date, preview
#   140dp bottom padding
# States: skeleton | error+retry | empty | loaded | payment processing/success/error
# "Pay Now" button ALWAYS visible and accessible — no hunting.
#
# ─── 15.6 CONVERSATIONS TAB ───
# Type: Tab (segmented) | ViewModels: ParentMessageViewModel +
#   ParentAnnouncementViewModel (BOTH EXIST)
# Deep links: /parent/conversations, /parent/messages/{threadId}
# Layout: Column(fillMaxSize) { SegmentRow(fillMaxWidth) {
#   Messages | Announcements } + Box(weight(1f)) { AnimatedContent(segment) } }
# Messages: LazyColumn(fillMaxSize, contentPadding bottom 140dp) — thread cards
#   Tap thread → conversation view (inline, NOT overlay). Back → inbox.
#   Compose bar at bottom of conversation. WhatsApp mental model.
# Announcements: LazyColumn — announcement cards. Tap → detail.
# CRITICAL: LazyColumn as root inside Box(weight(1f)). No verticalScroll wrap.
#
# ─── 15.7 PROFILE TAB ───
# Type: Tab (scrollable) | ViewModel: ParentProfileViewModel (EXISTS)
# Deep links: /parent/profile
# Layout (single verticalScroll):
#   1. Child identity card: avatar + name + class + roll + school
#      4 stat cards (2x2): attendance, avg marks, quizzes, badges
#   2. Achievements: horizontal scroll of badge cards (Row+horizontalScroll)
#   3. Account actions: list rows (Account Settings, Link Another Child,
#      Discover Schools, Language, Theme, Logout)
#   140dp bottom padding
# No gamification — stats are practical (attendance, marks), not RPG.
#
# ─── 15.8 UNLINKED PARENT GATE ───
# Type: Gate (non-scrollable) | Layout: Column(fillMaxSize, center) {
#   icon + "Link Your Child" + explanation + "Link Child" button +
#   "Discover Schools" button }
# No scroll needed — content is short. fillMaxSize safe here.
#
# ─── 15.9 OVERLAYS (14 screens, brief specs) ───
# All use ParentOverlayScaffold. Content uses forEach (NOT LazyColumn)
# unless list is >20 items (then LazyColumn as root, no verticalScroll wrap).
#
# 15.9.1 NOTIFICATIONS: filter chips (All|Unread) + notification cards.
#   Tap → mark read + deep link. VM: shared NotificationsViewModel.
# 15.9.2 CALENDAR: month header + 7-col grid + events list below.
# 15.9.3 SCHOLARSHIPS: scheme cards + status badges + "Apply" button +
#   application form. States: applying, success, error.
# 15.9.4 ACCOUNT SETTINGS: profile form + notification toggles +
#   language picker + theme picker + change password. VM: ParentProfileViewModel.
# 15.9.5 LEAVE: child selector + date pickers + type + reason +
#   "Apply" button + leave history. VM: ParentLeaveViewModel.
# 15.9.6 DISCOVERY: search bar + school cards. Tap → school detail.
# 15.9.7 SCHOOL DETAIL: school header + stats + "Link Child" button.
# 15.9.8 HEALTH: health profile + emergency contact + pulse gauge.
#   VM: ParentHealthViewModel.
# 15.9.9 PULSE: score gauge + risk factor bars + trend chart +
#   recommendations. VM: ParentPulseViewModel.
# 15.9.10 TRANSPORT: bus status + boarding status + ETA + route stops +
#   driver info. States: loading, empty, offline.
# 15.9.11 TUTOR CHAT: chat bubbles + subject selector + suggestion chips +
#   compose bar. LazyColumn for messages (weight(1f)) + compose bar below.
# 15.9.12 TUTOR PROGRESS: summary card + subject mastery + heatmap.
#   VM: ParentProgressViewModel.
# 15.9.13 DIGITAL ID CARD: ID card display + QR code + "Download" button.
# 15.9.14 LIBRARY: 3 sub-tabs (Borrowed, Catalog, Fines). Sub-tab Row
#   (fixed) + content (weight(1f) + scroll). VM: ParentLibraryViewModel.
# 15.9.15 EVENTS: event list + tap → detail + "Register" button +
#   PTM slot booking. VM: ParentEventRegistrationViewModel.
#
# ═══════════════════════════════════════════════════════════════════════════
# 16. EXECUTION PLAN — BUILD ORDER
# ═══════════════════════════════════════════════════════════════════════════
#
# Phase 1: Foundation
#   1.1 Delete all 29 old parent screen files
#   1.2 Build ParentOverlayScaffold (weight(1f) pattern — crash fix)
#   1.3 Build ParentPortalShell (weight(1f) content Box)
#   1.4 Build UnlinkedParentGate
#   1.5 Compile — verify shell renders with placeholder tabs
#
# Phase 2: Core Tabs
#   2.1 ParentHomeScreen
#   2.2 ParentAcademicsScreen
#   2.3 ParentFeesScreen
#   2.4 ParentConversationsScreen
#   2.5 ParentProfileScreen
#   2.6 Compile — verify all 5 tabs, no crashes
#
# Phase 3: Primary Overlays
#   3.1 NotificationsOverlay
#   3.2 LeaveOverlay
#   3.3 HealthOverlay (includes Pulse)
#   3.4 TransportOverlay
#   3.5 TutorChatOverlay
#   3.6 Compile — verify overlays open/close
#
# Phase 4: Secondary Overlays
#   4.1 CalendarOverlay
#   4.2 ScholarshipsOverlay
#   4.3 AccountSettingsOverlay
#   4.4 DiscoveryOverlay + SchoolDetailOverlay
#   4.5 TutorProgressOverlay
#   4.6 DigitalIdCardOverlay
#   4.7 LibraryOverlay
#   4.8 EventsOverlay
#   4.9 Compile — verify all overlays
#
# Phase 5: Deep Link Wiring
#   5.1 Update NavGraphV2.parseDeepLink() with complete parent deep link map
#   5.2 Wire all deep links to shell tab + overlay routing
#   5.3 Compile — verify build
#
# Phase 6: Polish
#   6.1 Verify 4 states on every screen
#   6.2 Verify pressScale + shapeMorph on every card
#   6.3 Verify 140dp bottom padding on every scrollable
#   6.4 Verify zero hardcoded data (grep for listOf(, hardcoded strings)
#   6.5 Verify no nested scrolls (grep for verticalScroll inside verticalScroll)
#   6.6 Verify no LazyColumn inside Column(verticalScroll)
#   6.7 Verify ALL feature buttons visible on 360x640dp
#   6.8 Verify ZERO old UI elements rendering
#   6.9 Verify navigation: every tap responds, every back works
#   6.10 Final compile
#   6.11 Commit and push
#
# ═══════════════════════════════════════════════════════════════════════════
# 17. VERIFICATION CHECKLIST (after EACH screen)
# ═══════════════════════════════════════════════════════════════════════════
#
#   [ ] VColors.* (no Color(0x...), no Color.White/Black)
#   [ ] VShapes.* (no hardcoded dp for radii)
#   [ ] VTypography.* (no hardcoded fontSize/fontWeight)
#   [ ] VMotion.* for animation durations
#   [ ] Every card has pressScale + shapeMorph
#   [ ] Loading: SkeletonCard with shimmer
#   [ ] Error: ErrorStateCard with retry
#   [ ] Empty: EmptyStateCard with friendly message
#   [ ] Loaded: complete, polished
#   [ ] 140dp bottom padding on scrollable content
#   [ ] No nested verticalScroll
#   [ ] No LazyColumn inside Column(verticalScroll)
#   [ ] No fillMaxSize/fillMaxHeight inside verticalScroll
#   [ ] No hardcoded data (all from ViewModel)
#   [ ] Every onClick wired or has // TODO(reason)
#   [ ] ALL feature buttons visible and accessible on 360x640dp
#   [ ] Fonts comfortable, NOT oversized (VTypography tokens as-is)
#   [ ] Navigation works: tap responds, back goes to right place
#   [ ] ZERO old UI elements in this screen
#   [ ] Compiles without errors
#   [ ] Would a parent feel premium using this?
#
# If ANY answer is "no" or "maybe" — fix before moving to next screen.
#
# ═══════════════════════════════════════════════════════════════════════════
# 18. WORKFLOW
# ═══════════════════════════════════════════════════════════════════════════
#
# One screen per iteration. One masterpiece at a time.
#
# STEP 1: Read the screen spec (Section 15).
# STEP 2: Check if ViewModel exists. If not, create it.
# STEP 3: Check if API endpoint exists. If not, create it.
# STEP 4: Write the screen. Use tokens. Use components. Use modifiers.
# STEP 5: Verify layout safety (no nested scrolls, no infinite height).
# STEP 6: Verify all buttons visible and accessible.
# STEP 7: Compile. Fix errors.
# STEP 8: Run verification checklist (Section 17).
# STEP 9: Ask: "Would a parent feel premium using this?" If no, fix it.
# STEP 10: Mark complete. Move to next.
#
# Do NOT batch multiple screens. One screen. Done right. Move on.
#
# ═══════════════════════════════════════════════════════════════════════════
# 19. KNOWN ISSUES IN EXISTING CODE TO FIX
# ═══════════════════════════════════════════════════════════════════════════
#
# ISSUE 1: ParentOverlayScaffold crash
#   Root cause: Column(fillMaxSize + verticalScroll) — infinite height.
#   Fix: weight(1f) on content area, NOT fillMaxSize.
#
# ISSUE 2: ParentAcademicsScreen LazyRow inside verticalScroll
#   Fix: Use Row + horizontalScroll for chip rows. forEach for lists.
#
# ISSUE 3: VPullRefreshPremium wrapping verticalScroll
#   Fix: Verify implementation. If it has verticalScroll internally,
#   content inside must NOT have verticalScroll.
#
# ISSUE 4: Gamification elements on parent screens
#   Fix: Remove ALL gamification. Replace with practical info.
#
# ISSUE 5: Redundant Messages overlay (Changelog #12)
#   Fix: Remove Messages overlay. Deep link → Conversations tab.
#
# ISSUE 6: 10-card feature grid is information overload
#   Fix: Reduce to 6-8 most important. Use visual hierarchy.
#
# ISSUE 7: Oversized fonts wasting screen space
#   Fix: Use VTypography tokens as-is. Don't override to larger sizes.
#
# ISSUE 8: Hidden feature buttons
#   Fix: ALL primary actions visible in first viewport or clearly labeled
#   section. No hunting. No scrolling through 5 sections to find Pay button.
#
# ISSUE 9: Broken navigation
#   Fix: Every tap responds. Every back goes to right place. No dead-ends.
#   No overlays that won't close. No back buttons that go to wrong screen.
#
# ISSUE 10: Old UI concepts surviving
#   Fix: ZERO old UI elements render after rebuild. If any old composable,
#   old card style, old gradient, old gamification widget is still visible,
#   the rebuild failed. Delete and redo.
#
# ═══════════════════════════════════════════════════════════════════════════
# END OF PROMPT — GO BUILD IT
# ═══════════════════════════════════════════════════════════════════════════
