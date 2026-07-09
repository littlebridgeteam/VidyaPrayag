# ═══════════════════════════════════════════════════════════════════════════
# GOD MODE — TEACHER PORTAL PREMIUM REBUILD — COMPLETE PROMPT
# ═══════════════════════════════════════════════════════════════════════════
#
# Read this ENTIRE document before writing a single line of code.
#
# Reference HTML prototype: preview/enrollplus-teacher-prototype.html
# Reference architecture:   preview/ENROLLPLUS_SCREEN_ARCHITECTURE_FINAL.md (Part B)
# Reference backend API:    shared/.../feature/teacher/data/remote/TeacherApi.kt
# Reference design tokens:  composeApp/.../ui/tokens/{VColors,VTypography,VShapes,VMotion}.kt
#
# ═══════════════════════════════════════════════════════════════════════════
# 0. THE BRIEF
# ═══════════════════════════════════════════════════════════════════════════
#
# Build the teacher portal from scratch. The HTML prototype at
# preview/enrollplus-teacher-prototype.html is the VISUAL SOURCE OF TRUTH.
# Every screen, every card, every toggle, every color, every spacing value
# in that prototype MUST be faithfully reproduced in Kotlin Compose.
#
# The prototype is not a suggestion — it is the spec. If the prototype shows
# a coral-soft badge with 8px padding and a 10px radius, the Compose code
# uses VColors.coralSoft, 8.dp padding, VShapes.sm. Exact fidelity.
#
# The backend API (TeacherApi.kt) defines what data flows where. The HTML
# prototype shows the UI; the API shows the data contract. The ViewModel
# bridges them. Zero hardcoded data — every value flows from
# ViewModel → API → backend → database.
#
# The backend has MORE features than the HTML prototype. The prototype is
# the baseline. The API reveals additional screens the prototype doesn't
# show (quiz system, AI syllabus parse, daily logs, lesson templates, etc.).
# These are listed in Section 9 and must also be built.
#
# ZERO OLD UI ELEMENTS SHOULD RENDER AFTER REBUILD.
# No old composable, no old layout pattern, no old card style — NOTHING
# from the old UI survives. If any old element renders, the rebuild failed.

# ═══════════════════════════════════════════════════════════════════════════
# 1. THE USER — WHO IS HOLDING THIS PHONE?
# ═══════════════════════════════════════════════════════════════════════════
#
#   Classroom teacher. Age 22-55. Medium digital literacy, time-poor,
#   using the app between/during classes. One-handed use, short attention
#   windows, frequent interruptions.
#
#   Their phone is their work tool. They open it between periods - 5 minutes
#   to mark attendance, 10 minutes to enter marks, 2 minutes to check
#   homework submissions. They need SPEED above all else.
#
#   They care about:
#   - "What class do I have next?"
#   - "Did I mark attendance for 7-B today?"
#   - "How many homework submissions are pending?"
#   - "Is there a student leave request I need to approve?"
#   - "Can I quickly enter marks for yesterday's quiz?"
#
#   They do NOT care about:
#   - Beautiful empty states with illustrations
#   - Animations that slow them down
#   - "Exploring the app" - they want to get in, do the thing, get out
#   - Gamification, badges, progress rings for themselves
#
#   DESIGN CALIBRATION:
#   - Speed-first: 1-2 taps to core actions (attendance, homework, grading)
#   - Big tap targets. 48dp minimum. One-handed use.
#   - Minimal decorative motion. Motion confirms action, doesn't delay it.
#   - Clear "done" confirmation - snackbar with checkmark after every save.
#   - No gesture-only discoverability. Every action has a visible button.
#   - Icon + label ALWAYS paired. Never icon-only.
#   - Comfortable text - VTypography tokens as-is. 14-15sp body, 22-24sp
#     headers. NOT oversized. NOT tiny. Comfortable for a 50-year-old with
#     reading glasses but dense enough for a 25-year-old who wants speed.
#   - Offline tolerance. Show what you have. Queue writes. Don't crash.
#   - Forgiving flows. If they make a mistake, they can go back and fix it.

# ═══════════════════════════════════════════════════════════════════════════
# 2. DESIGN LANGUAGE - MATERIAL 3 EXPRESSIVE (2025)
# ═══════════════════════════════════════════════════════════════════════════
#
#   SURFACE SYSTEM:
#   - Base is warm cream (VColors.cream = #FBF8F4) - the foundation
#   - Cards are white (VColors.surfaceCard = #FFFFFF) - they lift above
#     the cream base. This creates depth WITHOUT shadows.
#   - Elevated/tinted surfaces use VColors.surfaceTint (#F8F4EF) or
#     VColors.surfaceWarm (#FFF6EE) for subtle warmth differentiation.
#   - Shadows ONLY on FABs, floating elements, and primary button (VButton
#     already handles this with violet-tinted shadow).
#   - No drop shadows on cards. The cream base + white card = tonal depth.
#
#   COLOR:
#   - Primary: VColors.violet (#5B41D5) - main actions, active states
#   - Coral: VColors.coral (#F82B60) - urgent, destructive, error badges
#   - Gold: VColors.gold (#FCB400) - warnings, pending, syllabus progress
#   - Sky: VColors.sky (#18BFFF) - info, calendar events
#   - Mint: VColors.mint (#2DCE89) - success, present, done
#   - Each accent has a soft variant (coralSoft, goldSoft, skySoft, mintSoft)
#     for backgrounds and badges.
#   - Color is FUNCTIONAL, not decorative. A coral badge means "urgent."
#     A mint badge means "done." Don't use color randomly.
#   - No rainbow. The cream base makes accent colors POP.
#
#   SHAPE:
#   - VShapes.sm (10dp) - small chips, badges, toggle buttons
#   - VShapes.md (14dp) - buttons, inputs, small cards
#   - VShapes.lg (18dp) - standard cards, list items
#   - VShapes.xl (24dp) - large cards, hero sections
#   - VShapes.full (50%) - pills, avatars, circular badges
#   - Consistent. Don't mix radii on the same hierarchy level.
#
#   MOTION:
#   - VMotion.durFast (150ms) - toggle, chip select, small state changes
#   - VMotion.durDefault (250ms) - tab switch, overlay enter/exit, card press
#   - VMotion.durSlow (400ms) - screen transitions, staggered entrance
#   - pressScale (0.97-0.98) on every tappable card - confirms the tap
#   - Staggered entrance: 0-300ms cascade for list items
#   - AnimatedContent for tab switches and overlay transitions
#   - Motion has MEANING. It guides the eye and confirms action.
#   - NEVER use motion that delays the user. A teacher marking attendance
#     should not wait 400ms for a row animation to finish.
#
#   TYPOGRAPHY:
#   - VTypography tokens. NEVER hardcode fontSize, fontWeight, or sp.
#   - Hierarchy: h2 (24sp ExtraBold) > h3 (22sp ExtraBold) > body (15sp
#     Medium) > bodySmall (14sp Medium) > label (13sp SemiBold) > caption
#     (12sp Medium)
#   - Use h2 for greeting, h3 for section headers, body for content,
#     bodySmall for secondary text, label for badges/chips, caption for
#     timestamps/meta.
#   - Font sizes COMFORTABLE, NOT LARGE. Use VTypography tokens as-is.
#     Don't override to larger sizes. Oversized text wastes screen space
#     and forces more scrolling - unacceptable for a speed-first tool.
#
#   ICONOGRAPHY:
#   - Material Symbols (rounded variant). Consistent stroke weight.
#   - Icon + label ALWAYS paired. Never icon-only navigation.
#   - 24dp icons for standard use. 20dp for inline/badge icons.
#   - Icon tint follows context: VColors.ink2 for default, VColors.violet
#     for active, VColors.mint for success, VColors.coral for error.

# ═══════════════════════════════════════════════════════════════════════════
# 3. WHAT PREMIUM IS vs IS NOT
# ═══════════════════════════════════════════════════════════════════════════
#
#   PREMIUM FOR TEACHER IS:
#   - SPEED - 1 tap to attendance, 2 taps to marks entry. No deep menus.
#   - CLARITY - every screen answers "what do I do here?" in 2 seconds.
#   - DENSE-BUT-ORGANIZED - show real information, not empty whitespace.
#     A teacher with 5 classes and 90 students needs to see data, not
#     padding. But organized - grouped, labeled, scannable.
#   - TONAL DEPTH - cream base + white cards. No shadow soup.
#   - FUNCTIONAL COLOR - coral = urgent, mint = done, gold = pending,
#     violet = primary action. Color communicates status instantly.
#   - HONEST STATES - loading skeletons that match the real layout.
#     Empty states that say "No homework assigned yet" with a "Create"
#     button, not a sad illustration.
#   - RESPONSIVE - every tap responds in <100ms. Save shows snackbar
#     immediately. No silent successes, no silent failures.
#   - OFFLINE-TOLERANT - attendance saved offline syncs when online.
#     Show what you have. Queue what you can't send.
#   - REAL DATA - every number, every name, every count flows from
#     ViewModel -> API -> backend. Zero hardcoded values.
#
#   PREMIUM FOR TEACHER IS NOT:
#   - Gradients - teacher is a professional, not a consumer
#   - Glassmorphism - decorative, not functional
#   - Drop shadows on every card - Material 2, we're past that
#   - Beautiful empty states with illustrations - teachers want
#     actionable empty states ("No homework yet" + "Create" button)
#   - Gamification for the teacher themselves - no XP, no levels, no
#     progress rings on the teacher's own profile
#   - HARDCODED FAKE DATA - every value flows from the backend
#   - AI SLOP - flat lists of identical cards with no information hierarchy
#   - SLOW ANIMATIONS - a teacher marking attendance cannot wait for
#     elaborate transitions between students
#   - HIDDEN ACTIONS - no overflow menus for primary features. Every
#     critical action is visible and accessible. No "scroll to find" a
#     save button. No gesture-only discoverability.
#   - FLAT LAYOUTS - the Home tab has information hierarchy:
#     greeting -> now teaching -> schedule -> obligations -> quick actions ->
#     classes -> events. Not a flat grid of identical cards.

# ═══════════════════════════════════════════════════════════════════════════
# 4. NAVIGATION SYSTEM
# ═══════════════════════════════════════════════════════════════════════════
#
#   4.1 - BOTTOM NAVIGATION (5 tabs)
#   - Home (house icon) - default tab, today's overview
#   - Update (edit icon) - attendance, marks, syllabus, homework, lesson
#   - Classes (users icon) - class list -> student drill-down
#   - Timetable (calendar icon) - weekly schedule + change requests
#   - Profile (user icon) - settings, leave, overlays entry
#   - Badge on Update tab: outstanding obligations count (from
#     getObligations API). Hidden when count is 0.
#   - Badge on Home tab: unread notification count (from getUnreadCount
#     or notification feed). Hidden when 0.
#   - Tab switch: AnimatedContent with fade (VMotion.durDefault).
#   - Back press: if on non-Home tab -> go Home. If on Home -> exit app.
#   - 48dp min tap targets. Icon + label always visible.
#
#   4.2 - OVERLAYS (full-screen, above tabs)
#   - 12+ overlays: Notifications, HealthAlerts, Transport, PEWS,
#     ReportReview, ReportDraft, Heatmap, IdCard, ScheduledMessages,
#     EventRegistration, Messages, Calendar, StudentProfile, LeaveForm,
#     ChangePassword.
#   - Overlay enters: slide-in from right (VMotion.durSlow) or fade-in.
#   - Overlay exits: slide-out to right or fade-out.
#   - Back header: back arrow (left) + overlay title (center-left).
#     Back arrow -> closes overlay, returns to previous tab.
#   - BackHandler: intercepts system back to close overlay, not exit app.
#   - Only ONE overlay active at a time. Opening a new overlay closes
#     the previous one.
#   - Overlays are full-screen - they cover the bottom nav. The bottom
#     nav is NOT visible during an overlay.
#   - Entry points: Home tab quick-access cards, Profile tab rows,
#     notification deep links, Home tab check-in popup.
#
#   4.3 - BACK HIERARCHY
#   - Overlay open -> back closes overlay -> returns to tab
#   - Tab active (non-Home) -> back goes to Home tab
#   - Home tab -> back exits app
#   - Update tab with tool active -> back goes to scope selector
#   - Update tab scope selector -> back goes to Home tab
#   - Classes tab student profile -> back goes to class roster
#   - Classes tab class roster -> back goes to class list
#   - Messages overlay conversation view -> back goes to thread list
#   - NEVER a dead-end. NEVER an overlay that won't close. NEVER a back
#     press that exits the app when the user expected to go back.
#
#   4.4 - UPDATE TAB INTERNAL NAVIGATION
#   - Scope selector (class picker) -> select class -> tool selector appears
#   - Tool selector (segmented): Attendance | Marks | Syllabus | Homework |
#     Lesson Plan. Tap segment -> tool content swaps below.
#   - Back from tool content -> returns to scope selector (NOT to tab).
#   - Change class -> back to scope selector.
#   - Deep link from Home quick action -> Update tab with class + tool
#     pre-selected (skips scope selector if class is known).
#
#   4.5 - MESSAGES OVERLAY INTERNAL NAVIGATION
#   - Thread list -> tap thread -> conversation view (messages + compose)
#   - Back from conversation -> thread list
#   - Compose bar at bottom of conversation -> send message
#   - Unread badge on thread item -> markThreadRead on open
#
#   4.6 - DEEP LINK NAVIGATION
#   - Deep links from notifications route to specific overlays:
#     /teacher/attendance -> Update tab + Attendance tool + class pre-selected
#     /teacher/homework -> Update tab + Homework tool
#     /teacher/marks -> Update tab + Marks tool
#     /teacher/pews -> PEWS overlay
#     /teacher/messages -> Messages overlay
#     /teacher/events -> EventRegistration overlay
#     /teacher/transport -> Transport overlay
#     /teacher/health -> HealthAlerts overlay
#     /teacher/calendar -> Calendar overlay
#     /teacher/reports -> ReportReview overlay
#     /teacher/id-card -> IdCard overlay
#   - Deep link parsing: App.kt receives deepLink string, TeacherNavGraph
#     parses it and routes to the correct tab/overlay.
#   - Deep link consumed: onDeepLinkConsumed() called after routing.

# ═══════════════════════════════════════════════════════════════════════════
# 5. COMPOSE LAYOUT SAFETY - 12 ANTI-CRASH RULES
# ═══════════════════════════════════════════════════════════════════════════
#
#   These rules are NON-NEGOTIABLE. Violating any one causes crashes or
#   layout corruption. The previous codebase crashed repeatedly because
#   these were ignored.
#
#   RULE 1: NEVER use BringIntoViewRequester. It caused infinite layout
#   passes and crashed the app on focus changes (see Known Issues #1).
#   Use Modifier.scrollable() + manual scroll-to-item via
#   LazyListState.animateScrollToItem() instead.
#
#   RULE 2: NEVER wrap a LazyColumn inside a Column that has
#   verticalScroll(). LazyColumn manages its own scrolling. Wrapping it
#   in a scrollable parent causes "Vertically scrollable component was
#   measured with an infinity maximum height constraints" crash.
#   Use LazyColumn with item() + item(span = true) for headers/footers
#   instead of Column + LazyColumn.
#
#   RULE 3: NEVER use Modifier.height(fixedDp) on content that grows.
#   Cards, lists, text blocks - all use heightIn(min = ...) or
#   wrapContentHeight(). Fixed heights clip content on font scale,
#   long text, or unexpected data.
#
#   RULE 4: ALWAYS use weight() inside Column for proportional layouts,
#   not fixed heights. weight(1f) fills remaining space. weight(0.3f)
#   takes 30%. Never use height(0.dp).weight(1f) - just use weight(1f).
#
#   RULE 5: ALWAYS provide a contentPadding to LazyColumn/LazyRow when
#   the list extends under a bottom nav or FAB. 80dp bottom padding
#   minimum when bottom nav is visible. 140dp when FAB is present.
#
#   RULE 6: NEVER use AnimatedContent with SizeTransform that changes
#   the parent's measured size during animation. This causes
#   "Looking up a composition within a disposed composition" crashes.
#   Use fade transitions only, or slide transitions with fixed bounds.
#
#   RULE 7: ALWAYS use remember { mutableStateOf() } for form fields.
#   Never use plain var in a @Composable. State survives recomposition
#   only through remember.
#
#   RULE 8: NEVER call ViewModel functions directly from composition.
#   Use LaunchedEffect for initial loads. Use
#   rememberCoroutineScope().launch for user-triggered actions.
#   Calling suspend functions from composition crashes.
#
#   RULE 9: ALWAYS handle the 4 states: Loading, Content, Empty, Error.
#   Every screen. No exceptions. A screen that only handles Content
#   will crash or show blank when the API is slow or fails.
#
#   RULE 10: NEVER use BoxWithConstraints inside a scrollable parent.
#   It measures with unbounded constraints and crashes. Use
#   BoxWithConstraints only at the screen root, outside scroll.
#
#   RULE 11: ALL feature buttons must be visible and accessible. No
#   hidden actions, no overflow menus for primary features, no
#   "scroll to find" a critical button. The Save button in attendance
#   is always visible at the bottom. The Create button in homework is
#   always visible at the top. Every primary action has a visible,
#   tappable button.
#
#   RULE 12: NEVER use fixed heights on growing content. Use weight +
#   scroll for flexible layouts. A student list with 40 students must
#   scroll, not clip. A form with 5 fields must fit on screen, not
#   require a fixed 600dp column.

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
#   - coralSoft (#FFE4EC) - error soft background, matches HTML --coral-soft
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
#   - durFast (150ms) - toggles, chips
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
#     NO illustrations. Actionable. "No homework assigned yet" + "Create" button.
#   - VErrorState: error message + "Retry" button. Red icon. Simple.
#
#   7.2 - CONTENT COMPONENTS
#   - VCard: white surface, VShapes.lg, no shadow. Optional padding.
#     Matches HTML .card class.
#   - VCardPremium: white surface, VShapes.xl, subtle border (lineSoft).
#     For hero cards like "Now Teaching." Matches HTML .card-premium.
#   - VBadge: small pill, VShapes.full, soft background + accent text.
#     Variants: success (mintSoft/mint), error (coralSoft/coral), warning
#     (goldSoft/gold), info (skySoft/sky), neutral (surfaceTint/ink2).
#     Matches HTML .badge classes.
#   - VChip: selectable chip, VShapes.sm. Active = violet background +
#     white text. Inactive = surfaceTint background + ink2 text.
#     Matches HTML .assessment-chip, .route-chip, .tool-seg.
#   - VToggleGroup: segmented selector (like iOS segmented control).
#     Row of VChips where only one is active at a time. Used for Update
#     tab tool selector, Timetable sub-tabs, attendance P/A/L toggle.
#     Matches HTML .tool-segments, .att-toggle-group, .sub-tabs.
#   - VListRow: row with leading icon/avatar + content + trailing badge.
#     VShapes.lg, white background, optional border. Matches HTML .list-row.
#   - VSectionHeader: title + optional count badge + optional "See all".
#     Matches HTML .section-header.
#   - VProgressRing: circular progress (used for syllabus coverage %).
#     Matches HTML .progress-ring. NOT gamification - functional coverage.
#   - VProgressBar: linear progress bar. VShapes.full. Violet fill.
#     Matches HTML .progress-bar.
#   - VAvatar: circle with initials or image. VShapes.full. Size variants:
#     sm (32dp), md (40dp), lg (56dp). Matches HTML .avatar.
#   - VStatChip: small stat display: number + label. Used in class cards,
#     student profiles. Matches HTML .stat-chip, .meta-item.
#   - VTimelineRow: schedule row: time + class/subject + room + status.
#     Matches HTML .schedule-row.
#   - VCalendarGrid: month grid with event dots. Matches HTML .cal-grid.
#   - VHeatmapGrid: topic x mastery level grid. Matches HTML .heatmap-grid.
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
#     Auto-dismiss after 3s. Success variant: mint checkmark. Error
#     variant: coral alert icon. Matches HTML snackbar pattern.
#   - VConfirmDialog: modal dialog with title + body + confirm/cancel.
#     Confirm can be destructive (coral background). Matches HTML .modal-overlay.
#   - VFormDialog: modal dialog with form fields + submit/cancel.
#     Matches HTML .modal-card with form content.
#   - VPopup: bottom sheet popup. Matches HTML .checkin-popup.
#     Used for check-in reminder.
#
#   7.5 - UTILITY COMPONENTS
#   - VBackHeader: back arrow + title. Used in all overlays.
#     Matches HTML .overlay-header.
#   - VBottomNav: 5-item bottom navigation. Matches HTML .bottom-nav.
#   - VGreetingBar: greeting text + date + subtitle. Matches HTML .ph-greeting.
#   - VQuickAction: icon + label in a card. VShapes.lg. Matches HTML .qa-item.
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
#     - "No homework assigned yet" + "Create Homework" button
#     - "No classes scheduled for this day" (no button needed)
#     - "No change requests submitted" (no button needed)
#     - "You're all caught up!" (obligations all zero)
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

# ═══════════════════════════════════════════════════════════════════════════
# 9. DATA FLOW - ZERO HARDCODED
# ═══════════════════════════════════════════════════════════════════════════
#
#   9.1 - TEACHER API -> VIEWMODEL -> SCREEN
#   Every value on every screen comes from the backend via:
#   TeacherApi -> TeacherRepository -> TeacherViewModel -> Screen
#
#   The TeacherApi has 60+ endpoints. COMPLETE mapping of API -> screen:
#
#   HOME TAB:
#   - getDay(token, date?) -> greeting, schedule, "in class now" status
#   - getObligations(token) -> pending task counts
#   - getCheckInStatus(token, date?) -> check-in popup trigger
#   - listClassesV2(token) -> class cards (ID, subject, student count)
#   - getUnreadCount(token) -> notification badge
#
#   UPDATE TAB - ATTENDANCE:
#   - loadAttendance(token, assignmentId, date?) -> student roster
#   - saveAttendance(token, AttendanceSaveRequest) -> save confirmation
#
#   UPDATE TAB - MARKS:
#   - listAssessments(token, assignmentId, status?) -> assessment chips
#   - createAssessmentV2(token, request) -> create dialog
#   - getAssessmentMarks(token, assessmentId) -> marks entry roster
#   - saveAssessmentMarks(token, assessmentId, request) -> save (NO publish)
#   - publishAssessment(token, assessmentId) -> explicit publish
#   - unpublishAssessment(token, assessmentId) -> retract
#   - getAssessmentHistory(token, assignmentId) -> trends (FOO)
#
#   UPDATE TAB - SYLLABUS:
#   - loadSyllabus(token, assignmentId) -> units with progress
#   - createSyllabusUnit / updateSyllabusUnit / deleteSyllabusUnit
#   - toggleSyllabusProgress(token, request) -> one-tap toggle
#   - parseSyllabus(token, request) -> AI parse (FOO - dialog)
#   - confirmParsedSyllabus(token, request) -> bulk insert
#   - autoFillSyllabus(token, request) -> NCERT lookup (FOO - dialog)
#   - confirmAutoFillSyllabus / approveSyllabus / rejectSyllabus
#   - getPaceWarning(token, assignmentId) -> inline banner
#   - createDailyLog / listDailyLogs / shouldShowDailyLogPopup
#   - setPopupPrefs -> dismiss popup
#   - generateQuiz / publishQuiz / listQuizzes / getQuizResults
#   - getQuizLeaderboard / updateQuizQuestion / addQuizQuestion
#   - regenerateQuiz (FOO - quiz management overlay/dialog)
#
#   UPDATE TAB - HOMEWORK:
#   - listHomework(token, assignmentId) -> homework list
#   - assignHomework(token, request) -> create dialog
#   - getHomeworkBoard(token, homeworkId, assignmentId) -> submissions
#   - grantHomeworkExtension(token, homeworkId, request) -> extend
#   - reviewHomeworkSubmission(token, homeworkId, studentId, request)
#   - closeHomework(token, homeworkId, assignmentId)
#
#   UPDATE TAB - LESSON PLAN:
#   - listLessonPlans(token, assignmentId, filters...) -> plan list
#   - getLessonPlan / createLessonPlan / updateLessonPlan
#   - deleteLessonPlan / completeLessonPlan / skipLessonPlan
#   - getLessonCalendar(token, assignmentId, month) -> calendar view
#   - listLessonTemplates / saveLessonTemplate / deleteLessonTemplate
#   - instantiateLessonFromTemplate
#
#   CLASSES TAB:
#   - listClassesV2(token) -> class card list
#   - getClassDetailV2(token, assignmentId) -> class detail + roster
#   - getStudentProfileV2(token, studentId) -> scoped student profile
#
#   TIMETABLE TAB:
#   - getWeek(token, date?) -> Mon-Sat resolved schedule
#   - getTimetableChangeRequests(token) -> change request list
#   - submitTimetableChangeRequest(token, request) -> submit
#
#   PROFILE TAB:
#   - getProfile(token) -> teacher identity
#   - getMyLeave / applyMyLeave -> self leave management
#   - getLeaveRequests / decideLeaveRequest -> student leave approval
#
#   MESSAGES OVERLAY:
#   - getMessageThreads / getThreadMessages / markThreadRead
#   - sendMessage / broadcastToClass
#
#   9.2 - CROSS-FEATURE APIs (shared across portals)
#
#   HEALTH ALERTS: HealthApi.getHealthAlerts(token)
#     (GET /api/v1/teacher/health/alerts)
#
#   TRANSPORT: TransportApi (admin-scoped endpoints with route validation)
#
#   PEWS (Teacher):
#   - PewsApi.getTeacherStudents(token) -> at-risk students
#   - PewsApi.getTeacherInterventions(token, status?) -> interventions
#   - PewsApi.updateTeacherIntervention(token, id, request)
#   - PewsApi.generateParentDraft(token, id, lang) -> AI draft
#   - PewsApi.sendParentMessage(token, id) -> send to parent
#
#   REPORT CARDS: ReportCardApi (batch generation, job status, draft review)
#
#   EVENT REGISTRATION (Teacher PTM):
#   - EventRegistrationApi.getTeacherPtmEvents(token)
#   - EventRegistrationApi.getTeacherPtmDetail(token, eventId)
#   - EventRegistrationApi.getTeacherPtmSlots(token, eventId)
#   - EventRegistrationApi.checkinParent(token, eventId, registrationId)
#
#   ID CARD: IdCardApi.getTeacherIdCard(token)
#     (GET /api/v1/teacher/id-card)
#
#   SCHEDULED MESSAGES: ScheduledMessageApi (CRUD)
#
#   CALENDAR: CalendarRepository (view-only for teacher)

# ═══════════════════════════════════════════════════════════════════════════
# 10. SCREEN INVENTORY - ALL FILES TO CREATE
# ═══════════════════════════════════════════════════════════════════════════
#
#   All screens go in:
#   composeApp/src/commonMain/kotlin/com/littlebridge/enrollplus/ui/screens/teacher/
#
#   10.1 - SHELL + NAVIGATION (3 files)
#   - TeacherPortalShell.kt: main scaffold with VBottomNav + overlay host
#   - TeacherNavGraph.kt: tab routing + overlay routing + deep link parsing
#   - TeacherRoute.kt: sealed class defining all routes (tabs + overlays)
#
#   10.2 - HOME TAB (2 files)
#   - TeacherHomeTab.kt: greeting, schedule, obligations, quick actions,
#     class cards, events mini, check-in popup
#   - TeacherHomeViewModel.kt: loads day, obligations, check-in, classes
#
#   10.3 - UPDATE TAB (7 files)
#   - TeacherUpdateTab.kt: scope selector + tool segmented switch + content
#   - TeacherUpdateViewModel.kt: shared VM for all tools
#   - AttendanceTool.kt: student roster with P/A/L toggle, save button
#   - MarksTool.kt: assessment selector, marks entry, save/publish
#   - SyllabusTool.kt: unit list, topic toggle, progress, AI parse,
#     NCERT auto-fill, pace warning, daily log, quiz management
#   - HomeworkTool.kt: homework list, create dialog, submissions board,
#     extension, review, close
#   - LessonPlanTool.kt: plan list, create/edit dialog, calendar, templates
#
#   10.4 - CLASSES TAB (3 files)
#   - TeacherClassesTab.kt: class card list -> class detail -> student profile
#   - TeacherClassesViewModel.kt: loads classes, class detail, student profile
#   - StudentProfileOverlay.kt: student drill-down
#
#   10.5 - TIMETABLE TAB (2 files)
#   - TeacherTimetableTab.kt: week view + change requests sub-tab
#   - TeacherTimetableViewModel.kt: loads week, change requests, submits
#
#   10.6 - PROFILE TAB (2 files)
#   - TeacherProfileTab.kt: identity card, leave form, leave history,
#     change password, overlay entry rows, logout
#   - TeacherProfileViewModel.kt: loads profile, submits leave, password
#
#   10.7 - OVERLAYS (15 files)
#   - NotificationsOverlay.kt
#   - HealthAlertsOverlay.kt
#   - TransportOverlay.kt
#   - PewsOverlay.kt
#   - ReportReviewOverlay.kt
#   - ReportDraftEditorOverlay.kt
#   - HeatmapOverlay.kt
#   - IdCardOverlay.kt
#   - ScheduledMessagesOverlay.kt
#   - EventRegistrationOverlay.kt
#   - MessagesOverlay.kt
#   - CalendarOverlay.kt
#   - LeaveFormOverlay.kt
#   - ChangePasswordOverlay.kt
#   - StudentLeaveRequestsOverlay.kt (FOO - API supports, HTML doesn't show)
#
#   10.8 - DIALOGS (4 files, can be inline in parent screen)
#   - ChangeRequestDialog.kt
#   - HomeworkCreateDialog.kt
#   - LessonPlanCreateDialog.kt
#   - LogoutConfirmDialog.kt
#
#   10.9 - VIEWMODELS
#   - Single TeacherViewModel with sub-state per tab, OR separate VMs per
#     tab. Either is fine as long as state is managed correctly.
#
#   TOTAL: ~38 new files + shared components in ui/components/

# ═══════════════════════════════════════════════════════════════════════════
# 11. SCREEN SPECS - DETAILED LAYOUT FOR EACH SCREEN
# ═══════════════════════════════════════════════════════════════════════════
#
#   HTML CSS spacing tokens map to dp: --s-xs=4dp, --s-sm=8dp,
#   --s-md=16dp, --s-lg=24dp, --s-xl=32dp
#
#   11.1 - TEACHER PORTAL SHELL (B-0)
#   HTML ref: .shell, .phone, .bottom-nav
#   - Full screen, cream background (VColors.cream)
#   - Bottom: VBottomNav, 5 items, 64dp height, icon(24dp)+label(caption)
#   - Active tab: violet icon + violet text. Inactive: ink3.
#   - Badge on Update: obligations count. Badge on Home: unread count.
#   - Content: AnimatedContent switching between 5 tabs (fade, 250ms)
#   - Overlay layer: AnimatedContent above tabs, full screen
#   - Content padding: 64dp bottom (clears bottom nav)
#
#   11.2 - HOME TAB (B-1)
#   HTML ref: #tab-home, .ph-greeting, .ph-schedule, .ph-obligations,
#     .ph-quick-actions, .ph-class-cards
#   - LazyColumn, scrollable
#   - Greeting bar: "In class now" coralSoft badge (if active) +
#     "Good morning, Priya" (h2, ink) + "5 classes, 7 pending, 90 students"
#     (bodySmall, ink2). Padding: 24dp H, 16dp V.
#   - Today's schedule: LazyRow of period cards. Each: time (caption, ink3)
#     + class name (body, ink) + subject (bodySmall, ink2) + room (caption)
#     + status badge (Upcoming=goldSoft, In Progress=coralSoft, Done=mintSoft)
#     Card: white, VShapes.lg, 16dp padding.
#   - Obligations summary: VCard with total count badge (coral if >0, mint=0).
#     Breakdown rows: "Unmarked attendance: N", "Ungraded homework: N",
#     "Pending syllabus: N". Tap row -> Update tab with tool pre-selected.
#   - Quick actions: 2x2 grid of VQuickAction. Attendance, Homework, Marks,
#     Syllabus. Each -> Update tab with tool pre-selected.
#   - Class cards: vertical list. Each: class ID (caption) + subject (body)
#     + student count + pending badge + ungraded badge. Tap -> Classes tab.
#   - Events mini: small card, next event. Tap -> Calendar overlay.
#   - Check-in popup: VPopup bottom sheet. "Mark attendance for 7-B now?"
#     + "Mark" (Primary) + "Later" (Ghost). Shows if active class + not checked in.
#   - Bottom padding: 80dp.
#
#   11.3 - UPDATE TAB (B-2)
#   HTML ref: #tab-update, .scope-selector, .tool-segments, .tool-content
#   - If no class: scope selector (list of assigned classes). Tap to select.
#   - Once selected: class header (name + subject + student count) +
#     tool segmented switch (5 segments: Attendance, Marks, Syllabus,
#     Homework, Lesson). Active=violet bg+white text. Inactive=surfaceTint+ink2.
#   - Below: tool content swaps via AnimatedContent.
#   - Back button: returns to scope selector.
#
#   11.3a - ATTENDANCE TOOL
#   HTML ref: #tool-attendance, .att-toggle-group, .att-row
#   - Date selector (VDatePicker). Back-date window from API.
#   - Holiday/cancelled banner (goldSoft) if applicable.
#   - "Last marked by {name}" if alreadyMarked (caption, ink3).
#   - Student rows: roll no + name + P/A/L toggle (3 buttons).
#     Present=mint, Absent=coral, Late=gold. Active=filled, inactive=outline.
#     Leave-defaulted: "on approved leave" badge (skySoft).
#   - Quick: "Mark all present" + "Mark all absent" (Ghost buttons).
#   - Summary bar: present(N) + absent(N) + late(N). Sticky bottom.
#   - Save: VButton Primary, full width, sticky. "Save Attendance" ->
#     saveAttendance -> snackbar "Attendance marked". Loading spinner on save.
#
#   11.3b - MARKS TOOL
#   HTML ref: #tool-marks, .assessment-chip, .marks-row
#   - Assessment chips: horizontal scroll. Filter: All/Draft/Pending/Published.
#     Tap chip -> load marks for that assessment.
#   - "Create Assessment" (Ghost, top-right). Dialog: name, max marks, date.
#   - Student rows: roll no + name + mark input (numeric, max from assessment).
#     Auto-grade calculation.
#   - "Save Marks" -> saveAssessmentMarks (NO publish). Snackbar "Marks saved".
#   - "Publish" -> confirm dialog -> publishAssessment. Snackbar "Published".
#   - "Unpublish" (if published) -> unpublishAssessment.
#   - "View Trends" -> assessment history (FOO).
#
#   11.3c - SYLLABUS TOOL
#   HTML ref: #tool-syllabus, .syllabus-unit, .syllabus-topic
#   - Progress bar: overall coverage %. VProgressBar, violet.
#   - Pace warning banner (if behind): goldSoft + "Behind pace by N topics".
#   - Unit list: expandable. Unit header: name + completion % + expand icon.
#     Sub-topic: name + checkbox. Tap -> toggleSyllabusProgress.
#   - "Add Unit" -> dialog: name, parent (optional) -> createSyllabusUnit.
#   - Per-unit: rename, reorder, delete (long-press or menu).
#   - "Parse from Image/Text" -> dialog -> parseSyllabus -> preview -> confirm.
#   - "Auto-fill from NCERT" -> dialog -> autoFillSyllabus -> preview -> confirm.
#   - Approve/Reject (DRAFT units): approveSyllabus / rejectSyllabus.
#   - "Add Daily Log" -> dialog: date, topics, homework, notes.
#   - Daily log popup: if shouldShowDailyLogPopup -> popup -> create or dismiss.
#   - "Generate Quiz" per unit -> generateQuiz -> preview dialog -> edit ->
#     publish. Quiz list -> listQuizzes. Results -> getQuizResults + leaderboard.
#
#   11.3d - HOMEWORK TOOL
#   HTML ref: #tool-homework, .hw-item
#   - Homework list: title + due date + submission count badge (N/M).
#     Status: Active=mintSoft, Closed=ink3. Card: white, VShapes.lg.
#   - "Create Homework" (Primary, top). Dialog: title, description, due date.
#   - Tap homework -> submissions board: roster-joined. Each student:
#     Submitted=mint, Not Submitted=coral, Late=gold. Grade inline.
#   - "Extend Deadline" -> dialog: student selector + new date.
#   - "Close Homework" -> confirm -> closeHomework.
#
#   11.3e - LESSON PLAN TOOL
#   HTML ref: #tool-lesson, .lesson-item
#   - Plan list: date + topic + objectives preview + status badge.
#     Planned=skySoft, Completed=mintSoft, Skipped=ink3.
#   - "Create Lesson Plan" (Primary, top). Dialog: date, topic, objectives,
#     resources. -> createLessonPlan.
#   - Tap plan -> edit dialog. Update or delete (with confirmation).
#   - "Complete" / "Skip" buttons per plan.
#   - "Calendar" button -> month grid (getLessonCalendar).
#   - "Templates" button -> template list. Save/instantiate.
#
#   11.4 - CLASSES TAB (B-3)
#   HTML ref: #tab-classes, .class-card
#   - Scrollable list of class cards. Each: class name + section + subject
#     + student count + pending badge + ungraded badge + at-risk badge.
#     Card: white, VShapes.lg. Tap -> class detail (animated drill-down).
#   - Class detail: header + next period + weekly timetable mini +
#     attendance summary + assessment schedule + active homework +
#     student roster. Each student: name + roll + attendance % + latest
#     mark + flags. Tap student -> StudentProfileOverlay.
#   - Student Profile: header (avatar, name, roll, class) + academic
#     summary (attendance %, avg marks, syllabus coverage) + recent marks
#     + attendance history + homework status.
#   - Pull-to-refresh on class list and class detail.
#
#   11.5 - TIMETABLE TAB (B-4)
#   HTML ref: #tab-timetable, .sub-tabs, .day-btn, .period-row
#   - Sub-tabs: This Week | Change Requests. VToggleGroup.
#   - This Week: day selector (Mon-Sat, current highlighted). Period cards
#     for selected day: period no + time + class + subject + room. Tap ->
#     change request dialog (NOT direct edit).
#   - Change Requests: list of submitted requests. Each: type + target
#     period + reason + status badge (Pending=goldSoft, Approved=mintSoft,
#     Rejected=coralSoft) + date.
#   - Change Request dialog: type (Swap/Move/Cancel/Change room) + reason.
#     "Submit" -> submitTimetableChangeRequest. "Cancel" -> dismiss.
#
#   11.6 - PROFILE TAB (B-5)
#   HTML ref: #tab-profile, .profile-header, .profile-row
#   - Identity card: avatar + name + employee ID + department + subjects.
#     VCardPremium, VShapes.xl.
#   - Profile rows (VListRow, tappable):
#     Notifications, Messages, Calendar, Health Alerts, Transport, PEWS,
#     Report Review, Learning Heatmap, Digital ID Card, Scheduled Messages,
#     Event Registration, Student Leave Requests, Apply Leave,
#     Change Password, Logout.
#   - Each row: icon + label + trailing chevron.
#   - Bottom padding: 80dp.
#
#   11.7 - OVERLAY SPECS (B-6)
#   Each overlay: VBackHeader + content. Full screen. cream bg.
#
#   NOTIFICATIONS (B-6.01): List of teacher notifications. icon + title +
#     body + timestamp + read/unread. Tap -> deep link. "Mark all read" button.
#   HEALTH ALERTS (B-6.02): Student health alerts. avatar + name + class +
#     alert type + severity badge + instructions. "Acknowledge" button.
#   TRANSPORT (B-6.03): Route selector (chips). Student list per route.
#     Boarding status toggle. Stop-wise grouping. "Save" button.
#   PEWS (B-6.04): At-risk students. name + class + risk level badge
#     (High=coral, Moderate=gold, Low=sky) + primary factor. Tap ->
#     detail. Interventions: update, generate parent draft, send message.
#   REPORT REVIEW (B-6.05): Queue of report drafts. student name + class +
#     status + date. "Review" -> ReportDraftEditor.
#   REPORT DRAFT EDITOR (B-6.06): Student header + editable sections
#     (academic, behavior, comments). "Approve"/"Request Changes".
#     Auto-save indicator.
#   HEATMAP (B-6.07): Student selector + subject-wise mastery grid
#     (topics x mastery: red/yellow/green). Tap cell -> topic detail.
#   ID CARD (B-6.08): Teacher ID card display (photo, name, employee ID,
#     department, school, valid dates). QR code. "Download" button.
#   SCHEDULED MESSAGES (B-6.09): Scheduled message list. CRUD operations.
#   EVENT REGISTRATION (B-6.10): PTM events. Event details + slot booking
#     list (parent name, student, booked time). "Start Session" per slot.
#     "Check-in" button.
#   MESSAGES (B-6.11): Thread list (parent avatar, name, last message,
#     timestamp, unread badge). Tap -> conversation view. Compose bar.
#   CALENDAR (B-6.12): Month grid + event list. View-only for teacher.
#   LEAVE FORM: From date + to date + leave type (Casual/Sick/Earned) +
#     reason. "Apply" -> applyMyLeave. Leave history below.
#   CHANGE PASSWORD: Current + new + confirm. "Change" -> API.
#   STUDENT LEAVE REQUESTS (FOO): Student leave approval queue.
#     Approve/Reject buttons. -> decideLeaveRequest.

# ═══════════════════════════════════════════════════════════════════════════
# 12. ANTI-SLOP RULES - 13 SIGNALS THAT YOUR CODE IS SLOP
# ═══════════════════════════════════════════════════════════════════════════
#
#   If ANY of these signals appear in your code, STOP and fix immediately:
#
#   SIGNAL 1: Hardcoded data. Any value that should come from the API but
#     is instead a string literal or magic number. "Priya" as teacher name,
#     "7-B" as class name, "32" as student count. ALL of these must come
#     from ViewModel state.
#
#   SIGNAL 2: Flat card grids. A 3x3 or 4x4 grid of identical cards with
#     no information hierarchy. The Home tab is NOT a grid. It's a
#     hierarchical scroll: greeting -> schedule -> obligations -> actions.
#
#   SIGNAL 3: Missing states. A screen that only handles Content. No
#     Loading skeleton, no Empty state, no Error state. 4 states. Always.
#
#   SIGNAL 4: LazyColumn inside verticalScroll. This crashes. Use
#     LazyColumn with item() for all content including headers.
#
#   SIGNAL 5: Fixed height on growing content. Modifier.height(200.dp) on
#     a card that contains a list. Use heightIn(min = 200.dp) or
#     wrapContentHeight() instead.
#
#   SIGNAL 6: Drop shadows on cards. VCard has NO shadow. Depth comes
#     from cream base + white card surface. Only VButton and FABs have shadows.
#
#   SIGNAL 7: Gradient backgrounds. No gradients. Anywhere. The base is
#     solid cream. Cards are solid white. Badges are solid soft colors.
#
#   SIGNAL 8: Oversized text. body = 15sp, not 18sp. h2 = 24sp, not 32sp.
#     Use VTypography tokens as-is. Don't override sizes.
#
#   SIGNAL 9: Icon-only navigation. Bottom nav items MUST have icon +
#     label. Profile rows MUST have icon + label. Never icon-only.
#
#   SIGNAL 10: Silent success. User saves attendance and nothing happens.
#     ALWAYS show a snackbar after every successful action.
#
#   SIGNAL 11: Old UI elements rendering. If any old composable, old card
#     style, old gradient, old gamification widget still renders after
#     rebuild, the rebuild FAILED. Zero old elements.
#
#   SIGNAL 12: Hidden primary actions. A Save button that's only visible
#     after scrolling. A Create button buried in a menu. Every primary
#     action is visible and accessible without scrolling to find it.
#
#   SIGNAL 13: Slow animations. Any animation over 400ms on a core flow.
#     Attendance toggle should be 150ms. Tab switch 250ms. Overlay 400ms
#     max. A teacher cannot wait for elaborate transitions.

# ═══════════════════════════════════════════════════════════════════════════
# 13. CRITICAL RULES - 20 ZERO-TOLERANCE RULES
# ═══════════════════════════════════════════════════════════════════════════
#
#   RULE 1: ZERO hardcoded data. Every value from ViewModel -> API.
#   RULE 2: ZERO gradients. Solid colors only.
#   RULE 3: ZERO drop shadows on cards. Tonal depth only.
#   RULE 4: ZERO icon-only navigation. Icon + label always.
#   RULE 5: ZERO missing states. 4 states on every screen.
#   RULE 6: ZERO LazyColumn inside verticalScroll. Crashes.
#   RULE 7: ZERO fixed heights on growing content. Crashes/clips.
#   RULE 8: ZERO BringIntoViewRequester. Crashes (Known Issue #1).
#   RULE 9: ZERO oversized text. VTypography tokens as-is.
#   RULE 10: ZERO silent successes. Snackbar after every action.
#   RULE 11: ZERO hidden primary actions. All visible and accessible.
#   RULE 12: ZERO old UI elements rendering. Rebuild failed if any appear.
#   RULE 13: ZERO animations over 400ms on core flows.
#   RULE 14: ZERO dead-end navigation. Every back goes somewhere correct.
#   RULE 15: ZERO overlays that won't close. BackHandler on every overlay.
#   RULE 16: ZERO hardcoded colors. Use VColors tokens.
#   RULE 17: ZERO hardcoded shapes. Use VShapes tokens.
#   RULE 18: ZERO hardcoded font sizes. Use VTypography tokens.
#   RULE 19: ZERO hardcoded durations. Use VMotion tokens.
#   RULE 20: ZERO calls to ViewModel suspend functions from composition.
#     Use LaunchedEffect or rememberCoroutineScope.

# ═══════════════════════════════════════════════════════════════════════════
# 14. EXECUTION PLAN - 6 PHASES
# ═══════════════════════════════════════════════════════════════════════════
#
#   PHASE 1: SCAFFOLD + SHELL (compile-clean)
#   - Create TeacherRoute.kt sealed class (all routes)
#   - Create TeacherPortalShell.kt (VBottomNav + tab host + overlay host)
#   - Create TeacherNavGraph.kt (routing logic, deep link parsing)
#   - Create placeholder tab screens (5 tabs, each shows tab name)
#   - Create placeholder overlay screens (15 overlays, each shows name)
#   - Wire into App.kt post-auth flow
#   - Register ViewModels in Koin.kt
#   - COMPILE. Fix all errors. Zero warnings.
#
#   PHASE 2: SHARED COMPONENTS
#   - Build all components from Section 7:
#     VStateHost, SkeletonDashboard, SkeletonList, VEmptyState, VErrorState,
#     VCard, VCardPremium, VBadge, VChip, VToggleGroup, VListRow,
#     VSectionHeader, VProgressRing, VProgressBar, VAvatar, VStatChip,
#     VTimelineRow, VCalendarGrid, VHeatmapGrid, VTextField, VTextArea,
#     VSelect, VDatePicker, VPasswordField, VSnackbar, VConfirmDialog,
#     VFormDialog, VPopup, VBackHeader, VBottomNav, VGreetingBar,
#     VQuickAction
#   - Build modifiers: pressScale, cardBorder, shimmer, staggeredEntrance
#   - COMPILE. Verify each component renders in preview.
#
#   PHASE 3: HOME TAB + UPDATE TAB (core teacher workflows)
#   - Build TeacherHomeTab.kt (greeting, schedule, obligations, quick
#     actions, class cards, events mini, check-in popup)
#   - Build TeacherHomeViewModel.kt (getDay, getObligations, getCheckInStatus,
#     listClassesV2, getUnreadCount)
#   - Build TeacherUpdateTab.kt (scope selector + tool switch)
#   - Build TeacherUpdateViewModel.kt (shared state)
#   - Build AttendanceTool.kt (roster, P/A/L toggle, save)
#   - Build MarksTool.kt (assessment chips, marks entry, save/publish)
#   - Build SyllabusTool.kt (units, topics, progress, AI parse, quiz)
#   - Build HomeworkTool.kt (list, create, submissions, extend, close)
#   - Build LessonPlanTool.kt (list, create/edit, calendar, templates)
#   - COMPILE. Verify data flows from ViewModel to screen.
#
#   PHASE 4: CLASSES + TIMETABLE + PROFILE
#   - Build TeacherClassesTab.kt (class list -> detail -> student profile)
#   - Build TeacherClassesViewModel.kt
#   - Build StudentProfileOverlay.kt
#   - Build TeacherTimetableTab.kt (week view + change requests)
#   - Build TeacherTimetableViewModel.kt
#   - Build TeacherProfileTab.kt (identity, leave, password, rows, logout)
#   - Build TeacherProfileViewModel.kt
#   - Build ChangeRequestDialog.kt, HomeworkCreateDialog.kt,
#     LessonPlanCreateDialog.kt, LogoutConfirmDialog.kt
#   - COMPILE. Verify all tabs functional.
#
#   PHASE 5: OVERLAYS
#   - Build all 15 overlays from Section 11.7
#   - Wire overlay entry points (Home tab cards, Profile tab rows,
#     notification deep links)
#   - Build MessagesOverlay.kt with thread list + conversation view
#   - Build PewsOverlay.kt with interventions + parent draft
#   - Build EventRegistrationOverlay.kt with PTM slots + check-in
#   - COMPILE. Verify every overlay opens and closes correctly.
#
#   PHASE 6: POLISH + VERIFY
#   - Staggered entrance animations on all lists
#   - Pull-to-refresh on scrollable screens
#   - Offline state handling (cached data + queue writes)
#   - Deep link routing end-to-end test
#   - Run verification checklist (Section 15) on every screen
#   - Fix any anti-slop violations (Section 12)
#   - Final compile. Zero warnings. Zero hardcoded data.
#   - Hand off for user testing.

# ═══════════════════════════════════════════════════════════════════════════
# 15. VERIFICATION CHECKLIST - 15 ITEMS PER SCREEN
# ═══════════════════════════════════════════════════════════════════════════
#
#   For EVERY screen, verify ALL 15 items:
#
#   [ ] 1. Loading state: skeleton matching real content layout
#   [ ] 2. Content state: real data from ViewModel
#   [ ] 3. Empty state: message + optional action button, no illustrations
#   [ ] 4. Error state: message + retry button
#   [ ] 5. Zero hardcoded data (all values from API)
#   [ ] 6. Zero hardcoded colors (all from VColors)
#   [ ] 7. Zero hardcoded shapes (all from VShapes)
#   [ ] 8. Zero hardcoded font sizes (all from VTypography)
#   [ ] 9. No LazyColumn inside verticalScroll
#   [ ] 10. No fixed heights on growing content
#   [ ] 11. All primary actions visible and accessible
#   [ ] 12. Back navigation works (no dead-ends)
#   [ ] 13. Snackbar after every successful action
#   [ ] 14. 48dp minimum tap targets
#   [ ] 15. No old UI elements rendering

# ═══════════════════════════════════════════════════════════════════════════
# 16. WORKFLOW - ONE SCREEN AT A TIME
# ═══════════════════════════════════════════════════════════════════════════
#
#   Build ONE screen at a time. Complete it fully before moving to the next.
#
#   "Complete" means:
#   1. All 4 states implemented (Loading, Content, Empty, Error)
#   2. All data flows from ViewModel -> API
#   3. All interactions work (tap, save, snackbar)
#   4. Back navigation works
#   5. Compiles without warnings
#   6. Passes all 15 verification checklist items
#
#   Order of execution:
#   Phase 1: Shell -> compile
#   Phase 2: Components -> compile
#   Phase 3: Home -> Update (Attendance -> Marks -> Syllabus -> Homework ->
#     Lesson) -> compile
#   Phase 4: Classes -> Timetable -> Profile -> compile
#   Phase 5: Overlays (one at a time) -> compile
#   Phase 6: Polish -> verify
#
#   NEVER build 2 screens simultaneously. NEVER skip the compile step.
#   NEVER move to the next screen until the current one passes all 15
#   checklist items.

# ═══════════════════════════════════════════════════════════════════════════
# 17. KNOWN ISSUES - 5 ISSUES WITH FIXES
# ═══════════════════════════════════════════════════════════════════════════
#
#   ISSUE 1: BringIntoViewRequester crash
#   SYMPTOM: App crashes when focusing a text field inside a scrollable
#     column. Infinite layout passes.
#   ROOT CAUSE: BringIntoViewRequester triggers recursive layout
#     calculations that exceed Compose's layout pass limit.
#   FIX: NEVER use BringIntoViewRequester. Use LazyColumn with
#     animateScrollToItem() or Modifier.scrollable() with manual scroll
#     control. See Rule 1 in Section 5.
#
#   ISSUE 2: LazyColumn inside verticalScroll crash
#   SYMPTOM: "Vertically scrollable component was measured with an
#     infinity maximum height constraints" crash.
#   ROOT CAUSE: LazyColumn needs bounded height to virtualize items.
#     Wrapping it in a scrollable Column gives it infinite height.
#   FIX: Use LazyColumn with item() for ALL content including headers,
#     footers, and non-list sections. See Rule 2 in Section 5.
#
#   ISSUE 3: AnimatedContent SizeTransform crash
#   SYMPTOM: "Looking up a composition within a disposed composition" crash
#     during screen transitions.
#   ROOT CAUSE: SizeTransform changes parent's measured size during
#     animation, causing composition disposal mid-animation.
#   FIX: Use fade transitions only, or slide with fixed bounds. Never
#     SizeTransform that changes parent size. See Rule 6 in Section 5.
#
#   ISSUE 4: Marks save auto-publishing
#   SYMPTOM: Saving marks immediately publishes them to parents without
#     teacher confirmation.
#   ROOT CAUSE: Old code combined save + publish in one call.
#   FIX: saveAssessmentMarks ONLY saves. Publish is a separate explicit
#     action via publishAssessment. See B-MK-1 fix in architecture doc.
#
#   ISSUE 5: Student profile 403 for non-teaching teachers
#   SYMPTOM: Teacher gets 403 when viewing a student profile.
#   ROOT CAUSE: getStudentProfileV2 checks if the teacher teaches that
#     student. If not, access is denied.
#   FIX: This is CORRECT behavior. Show a graceful "You don't teach this
#     student" message instead of a generic error. Handle 403 specifically
#     in the ViewModel.

# ═══════════════════════════════════════════════════════════════════════════
# 18. KOIN DI REGISTRATION
# ═══════════════════════════════════════════════════════════════════════════
#
#   Register all teacher ViewModels in Koin.kt viewModelModule:
#
#   viewModel { TeacherHomeViewModel(get(), get()) }
#   viewModel { TeacherUpdateViewModel(get(), get()) }
#   viewModel { TeacherClassesViewModel(get(), get()) }
#   viewModel { TeacherTimetableViewModel(get(), get()) }
#   viewModel { TeacherProfileViewModel(get(), get()) }
#
#   The TeacherRepository is already registered:
#   single<TeacherRepository> { TeacherRepositoryImpl(get()) }
#
#   Cross-feature APIs already registered:
#   - HealthApi, PewsApi, TransportApi, IdCardApi, EventRegistrationApi,
#     ScheduledMessageApi, ReportCardApi
#
#   Inject these into ViewModels as needed:
#   - TeacherHomeViewModel(get<TeacherRepository>(), get<PewsApi>())
#   - etc.
#
#   DO NOT re-register APIs that already exist in Koin. Reuse them.

# ═══════════════════════════════════════════════════════════════════════════
# 19. SUMMARY
# ═══════════════════════════════════════════════════════════════════════════
#
#   This document defines the COMPLETE teacher portal rebuild:
#   - 5 tabs (Home, Update, Classes, Timetable, Profile)
#   - 15 overlays (Notifications, Health, Transport, PEWS, Reports, Heatmap,
#     IdCard, ScheduledMessages, Events, Messages, Calendar, Leave, Password,
#     StudentLeave, StudentProfile)
#   - 4 dialogs (ChangeRequest, HomeworkCreate, LessonPlanCreate, Logout)
#   - ~38 new files + shared components
#   - 60+ API endpoints mapped to screens
#   - 12 anti-crash layout rules
#   - 13 anti-slop signals
#   - 20 zero-tolerance critical rules
#   - 15-item verification checklist per screen
#   - 6-phase execution plan
#   - 5 known issues with fixes
#
#   The HTML prototype is the VISUAL SOURCE OF TRUTH.
#   The TeacherApi is the DATA SOURCE OF TRUTH.
#   The VColors/VTypography/VShapes/VMotion tokens are the STYLE SOURCE OF TRUTH.
#
#   Build it. One screen at a time. Compile after each. Zero hardcoded data.
#   Zero old UI. Zero crashes. Speed-first. Premium.
#
#   ═══════════════════════════════════════════════════════════════════════════
#   END OF DOCUMENT
#   ═══════════════════════════════════════════════════════════════════════════
