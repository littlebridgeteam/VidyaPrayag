# ═══════════════════════════════════════════════════════════════════════════
# GOD MODE — PARENT PORTAL PREMIUM REBUILD — COMPLETE PROMPT
# ═══════════════════════════════════════════════════════════════════════════
#
# Read this ENTIRE document before writing a single line of code.
#
# Reference HTML prototype: preview/enrollplus-parent-prototype.html
# Reference architecture:   preview/ENROLLPLUS_SCREEN_ARCHITECTURE_FINAL.md (Part C)
# Reference backend API:    shared/.../feature/parent/data/remote/ParentApi.kt
# Reference design tokens:  composeApp/.../ui/tokens/{VColors,VTypography,VShapes,VMotion}.kt
#
# ═══════════════════════════════════════════════════════════════════════════
# 0. THE BRIEF
# ═══════════════════════════════════════════════════════════════════════════
#
# Build the parent portal from scratch. The HTML prototype at
# preview/enrollplus-parent-prototype.html is the VISUAL SOURCE OF TRUTH.
# Every screen, every card, every toggle, every color, every spacing value
# in that prototype MUST be faithfully reproduced in Kotlin Compose.
#
# The prototype is not a suggestion — it is the spec. If the prototype shows
# a coral-soft badge with 8px padding and a 10px radius, the Compose code
# uses VColors.coralSoft, 8.dp padding, VShapes.sm. Exact fidelity.
#
# The backend API (ParentApi.kt) defines what data flows where. The HTML
# prototype shows the UI; the API shows the data contract. The ViewModel
# bridges them. Zero hardcoded data — every value flows from
# ViewModel → API → backend → database.
#
# The backend has MORE features than the HTML prototype. The prototype is
# the baseline. The API reveals additional screens the prototype doesn't
# show (daily summary, agentic syllabus, quiz submission, school discovery,
# child linking, etc.). These are listed in Section 9 and must also be built.
#
# ZERO OLD UI ELEMENTS SHOULD RENDER AFTER REBUILD.
# No old composable, no old layout pattern, no old card style — NOTHING
# from the old UI survives. If any old element renders, the rebuild failed.
#
# The current UI concept is fundamentally wrong. Not just styling — rethink
# the information architecture, layout patterns, and navigation. No
# gamification (XP, levels, house badges, journey progress rings). No
# 10-card grids that overwhelm. Parents want INFORMATION, not RPG stats.
# "How is my child doing?" in simple terms. "₹2,500 due" not "Outstanding
# Ledger Balance." WhatsApp mental model throughout.

# ═══════════════════════════════════════════════════════════════════════════
# 1. THE USER — WHO IS HOLDING THIS PHONE?
# ═══════════════════════════════════════════════════════════════════════════
#
#   Mother/father/guardian. Age 25-55+. Low-to-medium digital literacy.
#   WhatsApp-literate but not app-literate. Often shared family phone,
#   patchy connectivity, may prefer Hindi/regional language.
#
#   Their phone is their parenting tool. They open it to check on their
#   child — attendance, marks, fees, messages from teachers. They want
#   SIMPLICITY above all else. WhatsApp mental model: feed-like layout,
#   chat-style messages, clear visual hierarchy.
#
#   They care about:
#   - "Did my child go to school today?"
#   - "How much fee is due? When?"
#   - "Are there any messages from teachers?"
#   - "How did my child do on the last test?"
#   - "Is there a school event I need to know about?"
#   - "Can I apply for leave easily?"
#
#   They do NOT care about:
#   - Gamification — XP, levels, house badges, journey progress rings
#   - Beautiful empty states with illustrations
#   - "Exploring the app" — they want answers, not discovery
#   - Complex data tables — "87.3% average" not a spreadsheet of marks
#   - Animation that delays information
#
#   DESIGN CALIBRATION:
#   - Simplicity-first: 1-2 taps to see child's status. No deep menus.
#   - Big tap targets. 48dp minimum. Generous spacing.
#   - Icon + label ALWAYS paired. Never icon-only.
#   - WhatsApp mental model: chat-style messages, feed-like home.
#   - Plain language. "Fee Due" not "Outstanding Ledger Balance."
#   - Comfortable text — VTypography tokens as-is. 14-15sp body, 22-24sp
#     headers. NOT oversized. NOT tiny. Comfortable for a 50-year-old.
#   - Offline tolerance. Show what you have. Don't crash. Queue writes.
#   - Forgiving flows. If they make a mistake, they can go back and fix it.
#   - All feature buttons visible and accessible. No hidden actions, no
#     overflow menus for primary features. Pay button is obvious.

# ═══════════════════════════════════════════════════════════════════════════
# 2. DESIGN LANGUAGE - MATERIAL 3 EXPRESSIVE (2025)
# ═══════════════════════════════════════════════════════════════════════════
#
#   SURFACE SYSTEM:
#   - Base is warm cream (VColors.cream = #FBF8F4) - the foundation
#   - Cards are white (VColors.surfaceCard = #FFFFFF) - they lift above
#     the cream base. This creates depth WITHOUT shadows.
#   - Sub-tabs and segmented controls use surfaceTint as background with
#     white active pill — matches HTML .ac-subtabs, .conv-segments.
#   - Shadows ONLY on FABs, floating elements, and primary button.
#   - No drop shadows on cards. The cream base + white card = tonal depth.
#
#   COLOR:
#   - Primary: VColors.violet (#5B41D5) - main actions, active states
#   - Coral: VColors.coral (#F82B60) - urgent, fees due, error, overdue
#   - Gold: VColors.gold (#FCB400) - warnings, pending, late, not eligible
#   - Sky: VColors.sky (#18BFFF) - info, announcements, calendar events
#   - Mint: VColors.mint (#2DCE89) - success, present, done, paid, on route
#   - Each accent has a soft variant for backgrounds and badges.
#   - Color is FUNCTIONAL, not decorative. No rainbow. Cream base = pop.
#
#   SHAPE:
#   - VShapes.sm (10dp) - chips, badges, icon backgrounds, calendar cells
#   - VShapes.md (14dp) - buttons, inputs, small cards, feature tiles
#   - VShapes.lg (18dp) - standard cards, list items, hero cards
#   - VShapes.xl (24dp) - large hero cards (fees, profile), digital ID
#   - VShapes.full (50%) - pills, avatars, circular badges, progress rings
#   - Consistent. Don't mix radii on the same hierarchy level.
#
#   MOTION:
#   - VMotion.durFast (150ms) - toggle, chip, sub-tab, segment switch
#   - VMotion.durDefault (250ms) - tab switch, card press, overlay enter
#   - VMotion.durSlow (400ms) - overlay slide-in/out, staggered entrance
#   - pressScale (0.97-0.98) on every tappable card
#   - Staggered entrance: 0-300ms cascade for list items
#   - AnimatedContent for tab switches and overlay transitions
#   - Today's Learning card: expand/collapse with smooth height animation
#   - NEVER use motion that delays the parent.
#
#   TYPOGRAPHY:
#   - VTypography tokens. NEVER hardcode fontSize, fontWeight, or sp.
#   - h2 (24sp ExtraBold) > h3 (22sp ExtraBold) > body (15sp Medium) >
#     bodySmall (14sp Medium) > label (13sp SemiBold) > caption (12sp Medium)
#   - COMFORTABLE, NOT LARGE. Use VTypography tokens as-is. Don't override.
#   - Fees hero amount: 32sp ExtraBold — ONE exception for financial clarity.
#   - Pulse score: 48sp ExtraBold — another exception for health score.
#
#   ICONOGRAPHY:
#   - Material Symbols (rounded variant). Consistent stroke weight.
#   - Icon + label ALWAYS paired. Never icon-only. Parents need labels.
#   - 24dp icons for nav/standard. 20dp for inline/badge. 15px for
#     announcement/notification icons (matches HTML).

# ═══════════════════════════════════════════════════════════════════════════
# 3. WHAT PREMIUM IS vs IS NOT
# ═══════════════════════════════════════════════════════════════════════════
#
#   PREMIUM FOR PARENT IS:
#   - SIMPLICITY - 1 tap to see child's status. No deep menus. No jargon.
#   - CLARITY - every screen answers "how is my child doing?" in 2 seconds.
#   - WHATSAPP MENTAL MODEL - chat-style messages, feed-like home.
#   - PLAIN LANGUAGE - "₹2,500 due" not "Outstanding Ledger Balance."
#   - TONAL DEPTH - cream base + white cards. No shadow soup.
#   - FUNCTIONAL COLOR - coral = urgent, mint = done/paid, gold = pending.
#   - HONEST STATES - skeletons matching real layout. Actionable empties.
#   - RESPONSIVE - every tap responds in <100ms. Snackbar confirms.
#   - OFFLINE-TOLERANT - cached data. Queue writes. Don't crash.
#   - REAL DATA - every value from ViewModel -> API -> backend.
#   - ALL FEATURE BUTTONS VISIBLE - Pay button obvious, Leave button obvious.
#
#   PREMIUM FOR PARENT IS NOT:
#   - Gradients on every card
#   - Glassmorphism - decorative, not functional
#   - Drop shadows on every card - Material 2
#   - Beautiful empty states with illustrations
#   - Gamification - NO XP, NO levels, NO house badges, NO journey rings
#   - "Journey progress rings" — parents want attendance %, not player levels
#   - "LIVE" pulsing dots, radial glows on cards
#   - HARDCODED FAKE DATA
#   - AI SLOP - flat lists of identical cards, no hierarchy
#   - SLOW ANIMATIONS
#   - HIDDEN ACTIONS - no overflow menus for primary features
#   - FLAT LAYOUTS - Home has hierarchy: greeting -> hero -> learning ->
#     insights -> schedule -> quick access -> announcements
#   - OVERSIZED FONTS - comfortable, not huge. VTypography tokens as-is.
#   - THE CURRENT UI CONCEPT - it's fundamentally wrong. Start over.

# ═══════════════════════════════════════════════════════════════════════════
# 4. NAVIGATION SYSTEM
# ═══════════════════════════════════════════════════════════════════════════
#
#   4.1 - BOTTOM NAVIGATION (5 tabs)
#   - Home (house icon) - default tab, child overview + quick access
#   - Academics (book icon) - attendance, marks, syllabus, homework,
#     quizzes, report cards
#   - Fees (rupee icon) - balance, payment history, fee announcements
#   - Conversations (chat icon, label "Chats") - messages + announcements
#   - Profile (user icon) - child identity, stats, account actions
#   - Badge on Conversations: unread message count. Hidden when 0.
#   - Badge on Home: unread notification count. Hidden when 0.
#   - Tab switch: AnimatedContent with fade (VMotion.durDefault).
#   - Back: non-Home tab -> Home. Home tab -> exit app.
#   - 48dp min tap targets. Icon + label always visible.
#   - Dock always visible, always tappable, never obscured.
#
#   4.2 - OVERLAYS (full-screen, above tabs)
#   - 9 from prototype: Notifications, Health, Transport, Tutor, Library,
#     Events, Scholarships, IDCard, Leave.
#   - Additional from API: Discovery, SchoolDetail, LinkChild,
#     AccountSettings, Calendar, TutorProgress, ConversationDetail.
#   - Total: 16 overlays.
#   - Overlay enters: slide-in from right (300ms, VMotion.durSlow).
#   - Overlay exits: slide-out to right (300ms).
#   - Back header: back arrow (32dp circle, surfaceTint) + title.
#   - BackHandler: intercepts system back to close overlay.
#   - Only ONE overlay active at a time.
#   - Overlays cover bottom nav. Nav NOT visible during overlay.
#   - Entry points: Home quick-access tiles, Academics action cards,
#     Profile rows, notification deep links.
#
#   4.3 - BACK HIERARCHY
#   - Overlay -> back closes overlay -> returns to spawning tab
#   - Non-Home tab -> back goes to Home tab
#   - Home tab -> back exits app
#   - Academics sub-tab -> back goes to Academics tab
#   - Conversations conversation view -> back goes to inbox
#   - NEVER a dead-end. NEVER an overlay that won't close.
#   - NEVER: back from overlay -> different overlay
#   - NEVER: back button unresponsive or goes to wrong place
#
#   4.4 - ACADEMICS TAB INTERNAL NAVIGATION
#   - Sub-tabs (chips, horizontal scroll): Overview, Attendance, Marks,
#     Syllabus, Homework, Quizzes, Report.
#   - Tap sub-tab -> content swaps below (AnimatedContent with fade).
#   - Back from sub-tab -> goes to Academics tab (not Home).
#   - Deep-linked sub-tab: LaunchedEffect sets selectedSubTab from deep link.
#   - Action cards (Apply Leave, Health Records) at top -> open overlays.
#
#   4.5 - CONVERSATIONS TAB INTERNAL NAVIGATION
#   - Segmented control: Messages | Announcements. Tap -> content swaps.
#   - Messages: inbox -> tap thread -> conversation view (inline, NOT overlay).
#   - Conversation view: back press -> returns to inbox.
#   - Compose bar: always visible at bottom of conversation view.
#   - Announcements: feed -> tap -> detail.
#   - NEVER: conversation opens as overlay (it's inline tab content).
#
#   4.6 - DEEP LINK NAVIGATION
#   - /parent/home -> Tab: Home
#   - /parent/academics -> Tab: Academics
#   - /parent/academics/attendance -> Tab: Academics, sub-tab: Attendance
#   - /parent/academics/marks -> Tab: Academics, sub-tab: Marks
#   - /parent/academics/syllabus -> Tab: Academics, sub-tab: Syllabus
#   - /parent/academics/homework -> Tab: Academics, sub-tab: Homework
#   - /parent/academics/quizzes -> Tab: Academics, sub-tab: Quizzes
#   - /parent/academics/report -> Tab: Academics, sub-tab: Report
#   - /parent/fees -> Tab: Fees
#   - /parent/conversations -> Tab: Conversations, segment: Messages
#   - /parent/conversations/announcements -> Tab: Conversations, segment: Announcements
#   - /parent/profile -> Tab: Profile
#   - /parent/notifications -> Tab: Home, Overlay: Notifications
#   - /parent/transport -> Tab: Home, Overlay: Transport
#   - /parent/leave -> Tab: Home, Overlay: Leave
#   - /parent/scholarships -> Tab: Home, Overlay: Scholarships
#   - /parent/health -> Tab: Home, Overlay: Health
#   - /parent/pulse -> Tab: Home, Overlay: Pulse
#   - /parent/tutor -> Tab: Home, Overlay: TutorChat
#   - /parent/tutor-progress -> Tab: Home, Overlay: TutorProgress
#   - /parent/id-card -> Tab: Home, Overlay: DigitalIdCard
#   - /parent/library -> Tab: Home, Overlay: Library
#   - /parent/events -> Tab: Home, Overlay: EventRegistration
#   - /parent/calendar -> Tab: Home, Overlay: Calendar
#   - /parent/link-child -> Tab: Profile, Overlay: LinkChild
#   - /parent/account-settings -> Tab: Profile, Overlay: AccountSettings
#   - /parent/messages/{threadId} -> Tab: Conversations, open thread
#   - /parent/discovery -> Tab: Home, Overlay: Discovery
#   - /parent/school-detail/{id} -> Tab: Home, Overlay: SchoolDetail
#   - Deep link survives config change (rememberSaveable).

# ═══════════════════════════════════════════════════════════════════════════
# 5. COMPOSE LAYOUT SAFETY - 12 ANTI-CRASH RULES
# ═══════════════════════════════════════════════════════════════════════════
#
#   RULE 1: NEVER use BringIntoViewRequester. Use LazyListState instead.
#   RULE 2: NEVER wrap LazyColumn inside Column(verticalScroll()). Crash.
#   RULE 3: NEVER use Modifier.height(fixedDp) on growing content.
#   RULE 4: ALWAYS use weight() for proportional layouts, not fixed heights.
#   RULE 5: ALWAYS provide 140dp bottom contentPadding on scrollable lists.
#   RULE 6: NEVER use AnimatedContent with SizeTransform. Use fade only.
#   RULE 7: ALWAYS use remember { mutableStateOf() } for form fields.
#   RULE 8: NEVER call ViewModel suspend functions from composition.
#   RULE 9: ALWAYS handle 4 states: Loading, Content, Empty, Error.
#   RULE 10: NEVER use BoxWithConstraints inside a scrollable parent.
#   RULE 11: ALL feature buttons visible and accessible. No hidden actions.
#   RULE 12: NEVER use fixed heights on growing content. Use weight + scroll.

# ═══════════════════════════════════════════════════════════════════════════
# 6. TOKENS - USE EXISTING, DON'T CREATE NEW
# ═══════════════════════════════════════════════════════════════════════════
#
#   The design tokens already exist at:
#   composeApp/src/commonMain/kotlin/com/littlebridge/enrollplus/ui/tokens/
#
#   6.1 - VColors (color tokens)
#   - cream (#FBF8F4) - base surface, matches HTML --cream
#   - surfaceCard (#FFFFFF) - card background, matches HTML --surface-card
#   - surfaceTint (#F8F4EF) - subtle tint, matches HTML --surface-tint
#   - surfaceWarm (#FFF6EE) - warm tint for highlights
#   - ink (#1A1614) - primary text, matches HTML --ink
#   - ink2 (#5C544E) - secondary text, matches HTML --ink-2
#   - ink3 (#8A8078) - tertiary text, matches HTML --ink-3
#   - line (#E8E0D6) - border/divider, matches HTML --line
#   - lineSoft (#F0EAE0) - soft border, matches HTML --line-soft
#   - violet (#5B41D5) - primary action, matches HTML --violet
#   - violetSoft (#EEE8FB) - primary soft background
#   - coral (#F82B60) - urgent/error, matches HTML --coral
#   - coralSoft (#FFE4EC) - error soft, matches HTML --coral-soft
#   - gold (#FCB400) - warning/pending, matches HTML --gold
#   - goldSoft (#FFF4D1) - warning soft, matches HTML --gold-soft
#   - sky (#18BFFF) - info, matches HTML --sky
#   - skySoft (#E0F6FF) - info soft, matches HTML --sky-soft
#   - mint (#2DCE89) - success/present, matches HTML --mint
#   - mintSoft (#DCF5E8) - success soft, matches HTML --mint-soft
#   - success (#2D7A4A) - success state
#   - successSoft (#D4EDDB) - success soft
#   - error (#BA1A1A) - error state
#   - errorSoft (#FFDAD6) - error soft
#
#   6.2 - VShapes (shape tokens)
#   - sm (10dp) - matches HTML --r-sm
#   - md (14dp) - matches HTML --r-md
#   - lg (18dp) - matches HTML --r-lg
#   - xl (24dp) - matches HTML --r-xl
#   - full (50%) - matches HTML --r-full
#
#   6.3 - VTypography (typography tokens)
#   - h1 (44sp ExtraBold) - splash only
#   - h2 (24sp ExtraBold) - greeting, screen titles
#   - h3 (22sp ExtraBold) - section headers, overlay titles
#   - body (15sp Medium) - primary content text
#   - bodySmall (14sp Medium) - secondary content text
#   - label (13sp SemiBold) - badges, chips, toggle labels
#   - caption (12sp Medium) - timestamps, meta info
#   - wordmark (16sp ExtraBold) - brand name
#   - accentLabel (13sp Bold) - accent labels
#
#   6.4 - VMotion (motion tokens)
#   - durFast (150ms) - toggles, chips, sub-tabs, segments
#   - durDefault (250ms) - tab switches, card presses
#   - durSlow (400ms) - overlay transitions, staggered entrance
#   - durSlower (700ms) - full screen transitions
#   - ease - CubicBezierEasing(0.2f, 0f, 0f, 1f) - natural deceleration
#
#   DO NOT create new color/shape/type/motion tokens. The existing ones
#   map 1:1 to the HTML prototype's CSS variables. If you need a value
#   that doesn't exist, use the closest token. Do not invent new ones.

# ═══════════════════════════════════════════════════════════════════════════
# 7. COMPONENTS - BUILD THESE REUSABLE COMPOSABLES
# ═══════════════════════════════════════════════════════════════════════════
#
#   All components go in:
#   composeApp/src/commonMain/kotlin/com/littlebridge/enrollplus/ui/components/
#
#   7.1 - STATE COMPONENTS
#   - VStateHost: wraps content with Loading/Content/Empty/Error states.
#     Takes UiState<T> and renders appropriate content. Loading shows
#     skeleton matching the content layout. Empty shows message + optional
#     action button. Error shows message + retry button.
#   - SkeletonDashboard: shimmer placeholder matching Home tab layout
#   - SkeletonList: shimmer placeholder for list items
#   - SkeletonCard: shimmer placeholder for card content
#   - VEmptyState: title + optional subtitle + optional action button.
#     NO illustrations. Actionable. "No messages yet" + friendly text.
#   - VErrorState: error message + "Retry" button. Red icon. Simple.
#
#   7.2 - CONTENT COMPONENTS
#   - VCard: white surface, VShapes.lg, no shadow. Optional padding.
#     Matches HTML .card class.
#   - VCardPremium: white surface, VShapes.xl, subtle border (lineSoft).
#     For hero cards like child hero, fees hero. Matches HTML .card-premium,
#     .hero-card, .fees-hero, .profile-hero.
#   - VBadge: small pill, VShapes.full, soft background + accent text.
#     Variants: success (mintSoft/mint), error (coralSoft/coral), warning
#     (goldSoft/gold), info (skySoft/sky), neutral (surfaceTint/ink2).
#     Matches HTML .badge classes.
#   - VChip: selectable chip, VShapes.sm. Active = violet background +
#     white text. Inactive = surfaceTint background + ink2 text.
#     Matches HTML .ac-subtabs .ac-subtab, .att-chip, .marks-chip.
#   - VSegmentedControl: segmented selector (Messages | Announcements).
#     Row of pills where only one is active. surfaceTint background with
#     white active pill. Matches HTML .conv-segments, .ov-lib-tabs.
#   - VListRow: row with leading icon/avatar + content + trailing badge.
#     VShapes.lg, white background, optional border. Matches HTML .list-row,
#     .profile-row, .ov-notif-item.
#   - VSectionHeader: title + optional count badge + optional "See all".
#     Matches HTML .section-header, .ac-section-header.
#   - VProgressRing: circular progress (used for attendance %, pulse score).
#     Matches HTML .progress-ring, .ov-pulse-ring. NOT gamification —
#     functional attendance/health indicator.
#   - VProgressBar: linear progress bar. VShapes.full. Violet fill.
#     Matches HTML .progress-bar, .syllabus-bar-fill, .att-breakdown-fill.
#   - VAvatar: circle with initials or image. VShapes.full. Size variants:
#     sm (32dp), md (40dp), lg (56dp). Matches HTML .avatar, .hero-avatar.
#   - VStatChip: small stat display: number + label. Used in hero cards,
#     profile stats. Matches HTML .hero-stat, .stat-chip, .meta-item.
#   - VTimelineRow: schedule row: time + subject + teacher + status.
#     Matches HTML .timeline-row, .schedule-row.
#   - VCalendarGrid: month grid with event dots. Matches HTML .cal-grid.
#   - VHeatmapGrid: topic x mastery level grid. Matches HTML .heatmap-grid.
#   - VFeatureTile: icon + label tile for quick access grid. VShapes.md.
#     Matches HTML .feature-tile, .qa-item. 2-up grid layout.
#   - VAnnouncementCard: icon + title + date + preview. VShapes.lg.
#     Matches HTML .announcement-card, .ov-notif-item.
#   - VChatBubble: message bubble for conversation view. Left/right
#     alignment. Matches HTML .msg-bubble, .msg-bubble-me.
#   - VChatInput: compose bar with text field + send button. Matches
#     HTML .conv-compose, .ov-tutor-compose.
#
#   7.3 - INPUT COMPONENTS
#   - VTextField: outlined text field. VShapes.md. Matches HTML .form-input.
#     Label above, optional helper text below. Focus state: violet border.
#   - VTextArea: multiline text field. VShapes.md. Matches HTML .form-textarea.
#   - VSelect: dropdown selector. VShapes.md. Matches HTML .form-select.
#   - VDatePicker: date picker dialog. Returns ISO date string.
#   - VPasswordField: password input with visibility toggle.
#
#   7.4 - FEEDBACK COMPONENTS
#   - VSnackbar: bottom snackbar with message + optional action.
#     Auto-dismiss after 3s. Success: mint checkmark. Error: coral alert.
#   - VConfirmDialog: modal dialog with title + body + confirm/cancel.
#     Confirm can be destructive (coral background). Matches HTML .modal-overlay.
#   - VFormDialog: modal dialog with form fields + submit/cancel.
#   - VPopup: bottom sheet popup. Matches HTML .checkin-popup pattern.
#
#   7.5 - UTILITY COMPONENTS
#   - VBackHeader: back arrow + title. Used in all overlays.
#     Matches HTML .overlay-header. 32dp circle back button, surfaceTint bg.
#   - VBottomNav: 5-item bottom navigation. Matches HTML .bottom-nav.
#   - VGreetingBar: greeting text + date + subtitle. Matches HTML .ph-greeting.
#   - VChildSwitcher: dropdown for switching between linked children.
#     Matches HTML .child-switcher. Shows current child name + avatar.
#
#   7.6 - MODIFIERS
#   - pressScale(): scale 0.98 on press. Returns Modifier.
#   - cardBorder(): 1dp line border. Returns Modifier.
#   - shimmer(): shimmer animation for skeletons. Returns Modifier.
#   - staggeredEntrance(index): fade-in + slide-up with delay based on
#     index. Returns Modifier.

# ═══════════════════════════════════════════════════════════════════════════
# 8. STATE MANAGEMENT - 4 STATES EVERY SCREEN
# ═══════════════════════════════════════════════════════════════════════════
#
#   Every screen handles 4 states. No exceptions.
#
#   8.1 - LOADING
#   - Show skeleton matching the real content layout.
#   - Skeleton has the same number of items, same card sizes, same spacing.
#   - Shimmer animation (left-to-right gradient sweep, 1.5s loop).
#   - NEVER show a blank screen or a centered spinner alone.
#   - Loading state: UiState.Loading -> SkeletonDashboard / SkeletonList
#
#   8.2 - CONTENT
#   - Real data rendered. Every value from ViewModel state.
#   - Pull-to-refresh enabled on scrollable screens.
#   - Staggered entrance for list items (0-300ms cascade).
#
#   8.3 - EMPTY
#   - Message explaining the empty state in plain language.
#   - Action button if the user can do something about it.
#   - NO illustrations. NO "sad face" drawings. Just text + button.
#   - Examples:
#     - "No messages yet" + "Start a conversation" (if applicable)
#     - "No fees due" (no button needed — this is good news!)
#     - "No homework assigned" (no button needed)
#     - "No events scheduled" (no button needed)
#     - "Child not linked" + "Link Your Child" button (gate screen)
#
#   8.4 - ERROR
#   - Error message in plain language: "Couldn't load attendance. Check
#     your connection and try again."
#   - "Retry" button that re-calls the API.
#   - If offline, show "You're offline. Showing cached data." with the
#     cached content if available.
#   - Error state: UiState.Error(message) -> VErrorState(message, onRetry)
#
#   STATE PATTERN:
#   sealed class UiState<out T> {
#       data object Loading : UiState<Nothing>()
#       data class Content<T>(val data: T) : UiState<T>()
#       data class Empty(val message: String, val actionLabel: String? = null) : UiState<Nothing>()
#       data class Error(val message: String) : UiState<Nothing>()
#   }
#
#   The ViewModel exposes StateFlow<UiState<DataType>>. The screen
#   observes it and renders the appropriate state.
#   State priority: Loading > Error > Empty > Loaded.
#   If loading AND have cached data: show cached + refresh indicator.
#   If error AND have cached data: show cached + error banner.

# ═══════════════════════════════════════════════════════════════════════════
# 9. DATA FLOW - ZERO HARDCODED
# ═══════════════════════════════════════════════════════════════════════════
#
#   9.1 - PARENT API -> VIEWMODEL -> SCREEN
#   Every value on every screen comes from the backend via:
#   ParentApi -> ParentRepository -> ParentViewModel -> Screen
#
#   The ParentApi has 30+ endpoints. COMPLETE mapping of API -> screen:
#
#   HOME TAB:
#   - getDashboard(token) -> child hero (name, class, attendance, marks, fees),
#     today's schedule, quick insights, recent updates, announcements preview
#   - getDailySummary(token, childId, date?) -> today's learning summary
#     (what was taught across subjects, homework assigned, quizzes)
#   - getUnreadCount(token) -> conversations badge
#   - getNotifications(token) -> notification badge count
#
#   ACADEMICS TAB:
#   - getChildAttendance(token, childId) -> attendance summary + breakdown +
#     monthly calendar grid
#   - getChildMarks(token, childId) -> marks list with subject filter
#   - getChildSyllabus(token, childId) -> syllabus coverage per subject
#   - getSyllabusV2(token, childId) -> typed curriculum units with coverage
#   - getDailySummary(token, childId, date?) -> homework assigned today
#   - getQuizList(token, childId) -> pending quizzes for child
#   - getQuizDetail(token, quizId) -> quiz questions (no answers)
#   - submitQuiz(token, request) -> submit quiz answers
#   - getQuizResult(token, childId, quizId) -> past quiz results
#   - getQuizLeaderboard(token, childId, quizId) -> quiz ranking
#   - getChildTimetable(token, childId) -> weekly schedule for schedule view
#   - ReportCardApi.getPublishedReports(token, childId) -> report card
#   - ReportCardApi.getConferencePack(token, childId) -> PTM conference pack
#
#   FEES TAB:
#   - getFees(token, childId?) -> balance hero, fee structure breakdown,
#     payment history, fee announcements
#   - Server endpoint: POST /api/v1/parent/fees/pay -> payment processing
#     (handled via ParentFeesRouting.kt on server)
#
#   CONVERSATIONS TAB:
#   - getMessageThreads(token) -> inbox thread list
#   - getThreadMessages(token, threadId) -> conversation messages
#   - markThreadRead(token, threadId) -> mark thread as read
#   - getUnreadCount(token) -> unread badge count
#   - sendMessage(token, request) -> send message to teacher/admin
#   - getMessageRecipients(token) -> compose new message recipients
#   - getAnnouncements(token) -> announcements feed
#
#   PROFILE TAB:
#   - getDashboard(token) -> child identity (name, class, roll, school)
#   - getTrackProgress(token) -> stats (attendance, marks, quizzes)
#   - searchSchools(token, query) -> school discovery
#   - linkChild(token, request) -> link another child
#
#   NOTIFICATIONS OVERLAY:
#   - getNotifications(token) -> notification list
#   - markNotificationRead(token, id) -> mark single read
#   - markAllNotificationsRead(token) -> mark all read
#   - clearReadNotifications(token) -> clear read items
#   - markNotificationByRef(token, refType, refId) -> mark by reference
#
#   LEAVE OVERLAY:
#   - getLeaveRequests(token) -> leave history list
#   - applyLeave(token, request) -> submit leave application
#
#   HEALTH OVERLAY:
#   - HealthApi.getChildHealth(token, childId) -> child health profile,
#     emergency contacts, immunizations, incidents
#
#   PULSE OVERLAY:
#   - getLatestPulse(token, childId) -> latest pulse score, risk factors,
#     recommendations
#   - getPulseHistory(token, childId, weeks) -> pulse trend over time
#
#   TRANSPORT OVERLAY:
#   - TransportApi.getLiveLocation(token, childId) -> live bus location,
#     ETA, boarding status
#   - TransportApi.getRouteForChild(token, childId) -> route stops, driver
#     info, vehicle details
#
#   TUTOR CHAT OVERLAY:
#   - TutorApi.getSubjects(token, childId) -> subject list
#   - TutorApi.askDoubt(token, request) -> ask AI tutor a question
#   - TutorApi.getLearnerBundle(token, childId, subjectId) -> learning
#     materials for subject
#
#   TUTOR PROGRESS OVERLAY:
#   - TutorApi.getProgressCard(token, childId, subjectId) -> subject mastery
#     progress, heatmap data
#   - TutorApi.getPlan(token, childId, subjectId?) -> adaptive learning plan
#
#   LIBRARY OVERLAY:
#   - LibraryApi.parentSearchBooks(token, query, ...) -> catalog search
#   - LibraryApi.parentGetBook(token, bookId) -> book detail
#   - LibraryApi.parentGetIssuedForChild(token, childId) -> borrowed books
#   - LibraryApi.parentReserveBook(token, request) -> reserve a book
#   - LibraryApi.parentListReservations(token) -> reservation list
#   - LibraryApi.parentCancelReservation(token, reservationId) -> cancel
#   - LibraryApi.parentGetWishlist(token, childId) -> wishlist
#   - LibraryApi.parentAddToWishlist(token, childId, bookId) -> add
#   - LibraryApi.parentRemoveFromWishlist(token, childId, bookId) -> remove
#
#   EVENTS OVERLAY:
#   - EventRegistrationApi.listParentEvents(token) -> event list
#   - EventRegistrationApi.getParentEventDetail(token, eventId) -> event detail
#   - EventRegistrationApi.register(token, eventId, request) -> register
#   - EventRegistrationApi.cancelRegistration(token, eventId, request) -> cancel
#   - EventRegistrationApi.listMyRegistrations(token) -> my registrations
#   - EventRegistrationApi.reschedule(token, eventId, request) -> reschedule
#
#   SCHOLARSHIPS OVERLAY:
#   - ScholarshipApi.getParentScholarships(token) -> available schemes
#   - ScholarshipApi.applyScholarship(token, request) -> apply
#   - ScholarshipApi.getParentApplications(token) -> my applications
#   - ScholarshipApi.applyRenewal(token, request) -> renew
#   (Also: ParentApi.getScholarships(token) -> legacy endpoint, same data)
#
#   DIGITAL ID CARD OVERLAY:
#   - IdCardApi.getChildIdCard(token, childId) -> child's digital ID card
#     with QR code
#
#   CALENDAR OVERLAY:
#   - CalendarApi.getCalendar(token, date, viewType, endpoint="api/v1/parent/calendar")
#     -> academic calendar with events
#
#   DISCOVERY OVERLAY:
#   - searchSchools(token, query) -> school search results
#
#   SCHOOL DETAIL OVERLAY:
#   - searchSchools(token, query) -> school detail (from search results)
#   - linkChild(token, request) -> link child to school
#
#   LINK CHILD OVERLAY:
#   - linkChild(token, request) -> link child to parent account
#
#   ACCOUNT SETTINGS OVERLAY:
#   - Auth API: getProfile, updateProfile, changePassword
#   - LocaleManager: language preference
#   - ThemeManager: theme preference
#
#   PEWS NUDGE (inline, not overlay):
#   - PewsApi.getParentNudge(token, childId) -> early warning nudge
#   - PewsApi.ackParentNudge(token, childId) -> acknowledge nudge
#
#   9.2 - VIEWMODELS (REUSE — DO NOT DELETE)
#   These ViewModels already exist in shared/.../feature/*/presentation/:
#   - ParentDashboardViewModel, ParentHomeViewModel, ParentAcademicsViewModel,
#     ParentProfileViewModel, ParentMessageViewModel, ParentAnnouncementViewModel,
#     ParentLeaveViewModel, ParentPulseViewModel, ParentHealthViewModel,
#     ParentLibraryViewModel, ParentEventRegistrationViewModel,
#     ParentProgressViewModel, ParentNudgeViewModel
#   ViewModels may need updates but DO NOT delete them. Only nuke UI.
#   If a ViewModel doesn't exist for a feature, create it.
#
#   9.3 - DATA FLOW PATTERN
#   UI -> koinViewModel() -> collectAsStateV2() -> StateFlow<UiState<T>>
#   ViewModel -> Repository -> API -> Server -> Database
#   Use koinViewModel(), collectAsStateV2(), StateFlow data classes.
#   NO inline fake data. NO placeholder strings as real data.
#   NO mock models in composables. Build missing API endpoints if needed.
#   Create missing ViewModels if needed. Create missing Repositories if needed.
#   Every onClick wired or has // TODO(reason). Every form hits real endpoint.

# ═══════════════════════════════════════════════════════════════════════════
# 10. SCREEN INVENTORY - ALL FILES TO CREATE
# ═══════════════════════════════════════════════════════════════════════════
#
#   All screens go in:
#   composeApp/src/commonMain/kotlin/com/littlebridge/enrollplus/ui/screens/parent/
#
#   10.1 - SHELL + NAVIGATION (3 files)
#   - ParentPortalShell.kt: main scaffold with VBottomNav + overlay host
#   - ParentNavGraph.kt: tab routing + overlay routing + deep link parsing
#   - ParentRoute.kt: sealed class defining all routes (tabs + overlays)
#
#   10.2 - HOME TAB (1 file)
#   - ParentHomeTab.kt: greeting, child hero card, today's learning summary
#     (expandable), quick insights, day timeline, quick access grid (2-up:
#     Fees, Messages, Attendance, Transport, Health, Library, Scholarships,
#     ID Card, Events, Calendar), announcements feed
#
#   10.3 - ACADEMICS TAB (1 file, sub-tabs inline)
#   - ParentAcademicsTab.kt: action cards (Apply Leave, Health Records) +
#     sub-tab chips (Overview, Attendance, Marks, Syllabus, Homework,
#     Quizzes, Report) + AnimatedContent per sub-tab
#     - Overview: performance summary + subject breakdown bars
#     - Attendance: summary ring + breakdown bars + monthly calendar grid
#     - Marks: subject filter chips + assessment cards
#     - Syllabus: coverage card + expandable subject cards
#     - Homework: date chips + homework cards with status badges
#     - Quizzes: quiz cards with status badges, tap -> quiz detail
#     - Report: term report card with subject bars + grades + remarks
#
#   10.4 - FEES TAB (1 file)
#   - ParentFeesTab.kt: balance hero card (outstanding amount, Pay Now
#     button if > 0, "All fees paid" badge if 0), fee structure breakdown,
#     payment history, fee announcements
#
#   10.5 - CONVERSATIONS TAB (1 file)
#   - ParentConversationsTab.kt: segmented control (Messages | Announcements)
#     Messages: LazyColumn of thread cards -> tap -> conversation view
#     (inline, NOT overlay) with chat bubbles + compose bar
#     Announcements: LazyColumn of announcement cards -> tap -> detail
#
#   10.6 - PROFILE TAB (1 file)
#   - ParentProfileTab.kt: child identity card (avatar, name, class, roll,
#     school), 4 stat cards (2x2: attendance, avg marks, quizzes, badges),
#     achievements horizontal scroll, account actions list (Account Settings,
#     Link Another Child, Discover Schools, Language, Theme, Logout)
#
#   10.7 - UNLINKED PARENT GATE (1 file)
#   - UnlinkedParentGate.kt: icon + "Link Your Child" + explanation +
#     "Link Child" button + "Discover Schools" button. No scroll needed.
#
#   10.8 - OVERLAYS (16 files)
#   - NotificationsOverlay.kt: filter chips (All|Unread) + notification cards.
#     Tap -> mark read + deep link.
#   - CalendarOverlay.kt: month header + 7-col grid + events list below.
#   - ScholarshipsOverlay.kt: scheme cards + status badges + "Apply" button +
#     application form. States: applying, success, error.
#   - AccountSettingsOverlay.kt: profile form + notification toggles +
#     language picker + theme picker + change password.
#   - LeaveOverlay.kt: child selector + date pickers + type + reason +
#     "Apply" button + leave history.
#   - DiscoveryOverlay.kt: search bar + school cards. Tap -> school detail.
#   - SchoolDetailOverlay.kt: school header + stats + "Link Child" button.
#   - HealthOverlay.kt: health profile + emergency contact + pulse gauge.
#   - PulseOverlay.kt: score gauge + risk factor bars + trend chart +
#     recommendations.
#   - TransportOverlay.kt: bus status + boarding status + ETA + route stops +
#     driver info. States: loading, empty, offline.
#   - TutorChatOverlay.kt: chat bubbles + subject selector + suggestion chips +
#     compose bar. LazyColumn for messages (weight(1f)) + compose bar below.
#   - TutorProgressOverlay.kt: summary card + subject mastery + heatmap.
#   - DigitalIdCardOverlay.kt: ID card display + QR code + "Download" button.
#   - LibraryOverlay.kt: 3 sub-tabs (Borrowed, Catalog, Fines). Sub-tab Row
#     (fixed) + content (weight(1f) + scroll).
#   - EventsOverlay.kt: event list + tap -> detail + "Register" button +
#     PTM slot booking.
#   - LinkChildOverlay.kt: school search + child details form + submit.
#
#   10.9 - VIEWMODELS
#   - Reuse existing ViewModels from shared/.../feature/*/presentation/
#   - Create new ones only if missing for a feature
#   - Single ParentViewModel with sub-state per tab, OR separate VMs per
#     tab. Either is fine as long as state is managed correctly.
#
#   TOTAL: 3 shell + 5 tabs + 1 gate + 16 overlays = 25 files

# ═══════════════════════════════════════════════════════════════════════════
# 11. ANTI-SLOP RULES
# ═══════════════════════════════════════════════════════════════════════════
#
#   SLOP 1: Flat list of identical cards, no hierarchy. Home tab must have
#     clear visual hierarchy: hero -> learning -> insights -> schedule ->
#     quick access -> announcements. Not a flat wall of cards.
#   SLOP 2: Every card has different gradient background. No gradients on
#     cards. Cream base + white cards = tonal depth.
#   SLOP 3: "LIVE" pulsing dots, radial glows, glassmorphism on cards.
#     None of these. Functional color only.
#   SLOP 4: Gamification elements (XP, levels, house badges, journey
#     progress rings) for parents. Parents want marks, not RPG stats.
#   SLOP 5: Hardcoded data inline in composables. Every value from
#     ViewModel -> API -> backend.
#   SLOP 6: Empty onClick = { } handlers. Every onClick wired or has
#     // TODO(reason).
#   SLOP 7: "Loading..." text instead of skeleton shimmer. Every screen
#     shows skeleton matching real layout.
#   SLOP 8: Random spacing values (7dp, 13dp, 22dp). Use 20-24dp horizontal,
#     16-24dp between sections, 8-12dp between items.
#   SLOP 9: Content overflow or clipping on small screens. Test on
#     360x640dp. All content visible and accessible.
#   SLOP 10: Looks like ChatGPT generated it in 10 seconds. Distinct visual
#     identity, clear information hierarchy, premium feel.
#   SLOP 11: ANY old UI element still rendering. Zero old elements survive.
#     If any old composable, card style, gradient, gamification widget is
#     still visible, the rebuild failed.
#   SLOP 12: Feature buttons hidden or hard to reach. All primary actions
#     visible. Pay button obvious. Leave button obvious. No hunting.
#   SLOP 13: Oversized fonts wasting screen space. Use VTypography tokens
#     as-is. Don't override to larger sizes. Comfortable, not huge.

# ═══════════════════════════════════════════════════════════════════════════
# 12. CRITICAL RULES — ZERO TOLERANCE
# ═══════════════════════════════════════════════════════════════════════════
#
# 1.  NEVER hardcode Color(0x...) — use VColors.*
# 2.  NEVER hardcode dp for corner radii — use VShapes.*
# 3.  NEVER hardcode fontSize/fontWeight — use VTypography.*
# 4.  NEVER use Color.White or Color.Black — use surfaceCard/ink
# 5.  NEVER nest verticalScroll inside verticalScroll — crash
# 6.  NEVER put LazyColumn inside Column(verticalScroll) — crash
# 7.  NEVER use fillMaxSize() on children inside verticalScroll — crash
# 8.  EVERY card → pressScale + shapeMorph
# 9.  EVERY screen → 4 states (loading, error, empty, loaded)
# 10. EVERY scrollable → 140dp bottom padding (dock clearance)
# 11. EVERY onClick → wired or // TODO(reason)
# 12. EVERY piece of data → from ViewModel → API → backend
# 13. EVERY form → validation + submitting + success + error states
# 14. AutoMirrored icons for directional icons (back arrow, etc.)
# 15. Compile after EACH screen — fix errors immediately
# 16. Premium cream base (VColors.cream) — never white-on-white
# 17. No AI slop — distinct visual identity, clear info hierarchy
# 18. No gamification for parents — information, not XP
# 19. No content overflow — all buttons/options visible on 360x640dp
# 20. No fixed heights on growing content — use weight + scroll
# 21. ALL feature buttons visible and accessible — no hidden actions
# 22. Fonts comfortable, NOT oversized — use VTypography tokens as-is
# 23. ZERO old UI elements rendered after rebuild — complete nuke
# 24. Navigation bulletproof — every tap responds, every back works

# ═══════════════════════════════════════════════════════════════════════════
# 13. NUKE LIST — FILES TO DELETE BEFORE REBUILD
# ═══════════════════════════════════════════════════════════════════════════
#
# NOTE: The nuclear UI deletion was already completed on 2026-07-06.
# All UI in composeApp/.../ui/ and shared/.../feature/*/presentation/
# was deleted. The old parent screens no longer exist.
#
# If any old parent screen files are found, DELETE THEM. ZERO old UI
# elements survive.
#
# VIEW MODELS (REUSE — DO NOT DELETE):
#   ParentDashboardViewModel, ParentHomeViewModel, ParentAcademicsViewModel,
#   ParentProfileViewModel, ParentMessageViewModel, ParentAnnouncementViewModel,
#   ParentLeaveViewModel, ParentPulseViewModel, ParentHealthViewModel,
#   ParentLibraryViewModel, ParentEventRegistrationViewModel,
#   ParentProgressViewModel, ParentNudgeViewModel
#
# ViewModels may need updates but DO NOT delete them. Only nuke UI.
# If a ViewModel was deleted during the nuclear purge, recreate it from
# the API contract (Section 9).

# ═══════════════════════════════════════════════════════════════════════════
# 14. SCREEN SPECS
# ═══════════════════════════════════════════════════════════════════════════
#
# ─── 14.1 PARENT PORTAL SHELL ───
# Type: Shell | Deep links: All /parent/*
# Layout: Column(fillMaxSize) { TopBar(fillMaxWidth) + Box(weight(1f)) {
#   AnimatedContent(tab) } + VBottomNav(fillMaxWidth) }
# Overlays slide from right (300ms). Back: overlay→tab, tab→home, home→exit.
# CRITICAL: weight(1f) on content Box — bounded height prevents crash.
# Badge on Conversations: unread count. Badge on Home: notification count.
#
# ─── 14.2 PARENT OVERLAY SCAFFOLD ───
# Type: Scaffold | Layout: Column(fillMaxSize) { VBackHeader(fillMaxWidth) +
#   Column(weight(1f) + verticalScroll) { content() + Spacer(140dp) } }
# CRITICAL: weight(1f) NOT fillMaxSize on content area.
# Content inside must NOT have own verticalScroll or fillMaxSize.
# Back arrow closes overlay → returns to spawning tab.
#
# ─── 14.3 HOME TAB ───
# Type: Tab (scrollable) | ViewModel: ParentHomeViewModel
# Deep links: /parent/home
# Layout (single verticalScroll, forEach — NO LazyColumn):
#   1. Greeting + child switcher (dropdown if multiple children)
#   2. Hero card: avatar + name + class + 3 quick stats (attendance, marks, fees)
#   3. Today's Learning summary (expandable card — tap to expand/collapse):
#      - Collapsed: brief summary (subjects taught, homework count)
#      - Expanded: per-subject breakdown of what was taught, homework details
#   4. Quick insights: attendance trend, marks trend, upcoming events
#   5. Day timeline: period cards (time, subject, teacher, status)
#   6. Quick access grid (2-up): Fees, Messages, Attendance, Transport,
#      Health, Library, Scholarships, ID Card, Events, Calendar
#      ALL tiles visible — no clipping on 360x640dp
#   7. Announcements feed (max 5 items)
#   140dp bottom padding
# States: skeleton hero + cards | error + retry | empty + link child | loaded
#
# ─── 14.4 ACADEMICS TAB ───
# Type: Tab (sub-tabs) | ViewModel: ParentAcademicsViewModel
# Deep links: /parent/academics, /parent/academics/{subtab}
# Layout: Column(fillMaxSize) { Row(horizontalScroll) { 7 filter chips } +
#   Column(weight(1f) + verticalScroll) { AnimatedContent(subtab) } }
# Sub-tabs: Overview, Attendance, Marks, Syllabus, Homework, Quizzes, Report
#   Overview: performance summary + subject breakdown bars
#   Attendance: summary ring + breakdown bars + monthly calendar grid
#   Marks: subject filter chips (Row+horizontalScroll) + assessment cards
#   Syllabus: coverage card + expandable subject cards
#   Homework: date chips + homework cards with status badges
#   Quizzes: quiz cards with status badges, tap → quiz detail
#   Report: term report card with subject bars + grades + teacher remarks
# Action cards at top: Apply Leave (→ Leave overlay), Health Records (→ Health)
# All content uses forEach inside parent scroll. No LazyColumn.
# 140dp bottom padding.
#
# ─── 14.5 FEES TAB ───
# Type: Tab (scrollable) | ViewModel: ParentHomeViewModel or dedicated
# Deep links: /parent/fees
# Layout (single verticalScroll):
#   1. Balance hero card: outstanding amount (32sp ExtraBold), "Pay Now"
#      button if amount > 0, "All fees paid" mint badge if 0, due date
#   2. Fee structure breakdown: fee items with Paid/Due/Overdue badges
#   3. Payment history: date, amount, receipt number, download button
#   4. Fee announcements: title, date, preview
#   140dp bottom padding
# States: skeleton | error+retry | empty | loaded | payment processing/success/error
# "Pay Now" button ALWAYS visible and accessible — no hunting.
#
# ─── 14.6 CONVERSATIONS TAB ───
# Type: Tab (segmented) | ViewModels: ParentMessageViewModel +
#   ParentAnnouncementViewModel
# Deep links: /parent/conversations, /parent/messages/{threadId}
# Layout: Column(fillMaxSize) { VSegmentedControl(fillMaxWidth) {
#   Messages | Announcements } + Box(weight(1f)) { AnimatedContent(segment) } }
# Messages: LazyColumn(fillMaxSize, contentPadding bottom 140dp) — thread cards
#   Tap thread → conversation view (inline, NOT overlay). Back → inbox.
#   Compose bar at bottom of conversation. WhatsApp mental model.
# Announcements: LazyColumn — announcement cards. Tap → detail.
# CRITICAL: LazyColumn as root inside Box(weight(1f)). No verticalScroll wrap.
#
# ─── 14.7 PROFILE TAB ───
# Type: Tab (scrollable) | ViewModel: ParentProfileViewModel
# Deep links: /parent/profile
# Layout (single verticalScroll):
#   1. Child identity card: avatar + name + class + roll + school
#      4 stat cards (2x2): attendance, avg marks, quizzes, badges
#   2. Achievements: horizontal scroll of badge cards (Row+horizontalScroll)
#   3. Account actions: list rows (Account Settings, Link Another Child,
#      Discover Schools, Language, Theme, Logout)
#   140dp bottom padding
# No gamification — stats are practical (attendance, marks), not RPG.
# Logout → VConfirmDialog ("Are you sure?") → clear session → login screen.
#
# ─── 14.8 UNLINKED PARENT GATE ───
# Type: Gate (non-scrollable) | Layout: Column(fillMaxSize, center) {
#   icon + "Link Your Child" + explanation + "Link Child" button +
#   "Discover Schools" button }
# No scroll needed — content is short. fillMaxSize safe here.
# Shown when parent has zero linked children.
#
# ─── 14.9 OVERLAYS (16 screens, brief specs) ───
# All use ParentOverlayScaffold. Content uses forEach (NOT LazyColumn)
# unless list is >20 items (then LazyColumn as root, no verticalScroll wrap).
#
# 14.9.1 NOTIFICATIONS: filter chips (All|Unread) + notification cards.
#   Tap → mark read + deep link. VM: shared NotificationsViewModel.
# 14.9.2 CALENDAR: month header + 7-col grid + events list below.
#   VM: CalendarRepository (view-only for parent).
# 14.9.3 SCHOLARSHIPS: scheme cards + status badges + "Apply" button +
#   application form. States: applying, success, error.
#   VM: ScholarshipApi.
# 14.9.4 ACCOUNT SETTINGS: profile form + notification toggles +
#   language picker + theme picker + change password. VM: ParentProfileViewModel.
# 14.9.5 LEAVE: child selector + date pickers + type + reason +
#   "Apply" button + leave history. VM: ParentLeaveViewModel.
# 14.9.6 DISCOVERY: search bar + school cards. Tap → school detail.
#   VM: ParentRepository.searchSchools.
# 14.9.7 SCHOOL DETAIL: school header + stats + "Link Child" button.
#   VM: ParentRepository.
# 14.9.8 HEALTH: health profile + emergency contact + immunizations +
#   incidents. VM: ParentHealthViewModel. API: HealthApi.getChildHealth.
# 14.9.9 PULSE: score gauge (48sp) + risk factor bars + trend chart +
#   recommendations. VM: ParentPulseViewModel.
# 14.9.10 TRANSPORT: bus status + boarding status + ETA + route stops +
#   driver info. States: loading, empty, offline.
#   API: TransportApi.getLiveLocation + getRouteForChild.
# 14.9.11 TUTOR CHAT: chat bubbles + subject selector + suggestion chips +
#   compose bar. LazyColumn for messages (weight(1f)) + compose bar below.
#   API: TutorApi.askDoubt + getSubjects + getLearnerBundle.
# 14.9.12 TUTOR PROGRESS: summary card + subject mastery + heatmap.
#   VM: ParentProgressViewModel. API: TutorApi.getProgressCard + getPlan.
# 14.9.13 DIGITAL ID CARD: ID card display + QR code + "Download" button.
#   API: IdCardApi.getChildIdCard.
# 14.9.14 LIBRARY: 3 sub-tabs (Borrowed, Catalog, Fines). Sub-tab Row
#   (fixed) + content (weight(1f) + scroll). VM: ParentLibraryViewModel.
#   API: LibraryApi.parent* endpoints.
# 14.9.15 EVENTS: event list + tap → detail + "Register" button +
#   PTM slot booking. VM: ParentEventRegistrationViewModel.
# 14.9.16 LINK CHILD: school search + child details form + submit.
#   API: ParentRepository.linkChild.

# ═══════════════════════════════════════════════════════════════════════════
# 15. EXECUTION PLAN — BUILD ORDER
# ═══════════════════════════════════════════════════════════════════════════
#
# Phase 1: Foundation
#   1.1 Build ParentOverlayScaffold (weight(1f) pattern — crash fix)
#   1.2 Build ParentPortalShell (weight(1f) content Box)
#   1.3 Build UnlinkedParentGate
#   1.4 Build ParentRoute.kt + ParentNavGraph.kt (tab routing + overlay host)
#   1.5 Compile — verify shell renders with placeholder tabs
#
# Phase 2: Core Tabs
#   2.1 ParentHomeTab (hero, learning summary, insights, timeline, grid, announcements)
#   2.2 ParentAcademicsTab (7 sub-tabs, action cards)
#   2.3 ParentFeesTab (balance hero, breakdown, history, announcements)
#   2.4 ParentConversationsTab (messages + announcements, inline conversation)
#   2.5 ParentProfileTab (identity, stats, achievements, account actions)
#   2.6 Compile — verify all 5 tabs, no crashes
#
# Phase 3: Primary Overlays
#   3.1 NotificationsOverlay
#   3.2 LeaveOverlay
#   3.3 HealthOverlay
#   3.4 PulseOverlay
#   3.5 TransportOverlay
#   3.6 TutorChatOverlay
#   3.7 Compile — verify overlays open/close
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
#   4.9 LinkChildOverlay
#   4.10 Compile — verify all overlays
#
# Phase 5: Deep Link Wiring
#   5.1 Update ParentNavGraph.parseDeepLink() with complete parent deep link map
#   5.2 Wire all 28 deep links to shell tab + overlay routing
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

# ═══════════════════════════════════════════════════════════════════════════
# 16. VERIFICATION CHECKLIST (after EACH screen)
# ═══════════════════════════════════════════════════════════════════════════
#
#   [ ] VColors.* (no Color(0x...), no Color.White/Black)
#   [ ] VShapes.* (no hardcoded dp for radii)
#   [ ] VTypography.* (no hardcoded fontSize/fontWeight)
#   [ ] VMotion.* for animation durations
#   [ ] Every card has pressScale + shapeMorph
#   [ ] Loading: SkeletonCard with shimmer
#   [ ] Error: VErrorState with retry
#   [ ] Empty: VEmptyState with friendly message
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

# ═══════════════════════════════════════════════════════════════════════════
# 17. WORKFLOW
# ═══════════════════════════════════════════════════════════════════════════
#
# One screen per iteration. One masterpiece at a time.
#
# STEP 1: Read the screen spec (Section 14).
# STEP 2: Check if ViewModel exists. If not, create it.
# STEP 3: Check if API endpoint exists. If not, create it.
# STEP 4: Write the screen. Use tokens. Use components. Use modifiers.
# STEP 5: Verify layout safety (no nested scrolls, no infinite height).
# STEP 6: Verify all buttons visible and accessible.
# STEP 7: Compile. Fix errors.
# STEP 8: Run verification checklist (Section 16).
# STEP 9: Ask: "Would a parent feel premium using this?" If no, fix it.
# STEP 10: Mark complete. Move to next.
#
# Do NOT batch multiple screens. One screen. Done right. Move on.

# ═══════════════════════════════════════════════════════════════════════════
# 18. KNOWN ISSUES IN EXISTING CODE TO FIX
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
#   No XP, no levels, no house badges, no journey progress rings.
#   Parents want attendance %, marks, fees — not RPG stats.
#
# ISSUE 5: Redundant Messages overlay (Changelog #12)
#   Fix: Remove Messages overlay. Deep link → Conversations tab.
#   Conversation view is inline tab content, NOT an overlay.
#
# ISSUE 6: 10-card feature grid is information overload
#   Fix: Reduce to 6-8 most important. Use visual hierarchy.
#   2-up grid with clear icons + labels. Not a flat wall.
#
# ISSUE 7: Oversized fonts wasting screen space
#   Fix: Use VTypography tokens as-is. Don't override to larger sizes.
#   Exception: fees hero amount (32sp) and pulse score (48sp) only.
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

# ═══════════════════════════════════════════════════════════════════════════
# END OF PROMPT — GO BUILD IT
# ═══════════════════════════════════════════════════════════════════════════
