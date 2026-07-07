---
name: enrollplus-craft
description: Anti-slop Compose Multiplatform skill for EnrollPlus — three-portal education app (Admin, Teacher, Parent). Dial-driven design, token-locked output, motion discipline, pre-flight verification. Apply to any EnrollPlus UI screen generation task.
---

# EnrollPlus Craft Skill — Anti-Slop Compose Multiplatform

## 0. BRIEF INFERENCE (Read the Room Before Anything Else)

Before writing a single line of Compose code, infer what the user actually wants.

### 0.A Read these signals first
1. **Portal** — Admin (command center, dense), Teacher (speed tool, medium), Parent (information feed, spacious)
2. **Screen type** — list, detail, form, dashboard, overlay, gate, onboarding
3. **Data source** — which ViewModel, which API, which backend endpoints
4. **User state** — authenticated, first-launch, offline, error, empty
5. **Quiet constraints** — one-handed use (Teacher), shared family phone (Parent), 200-2000 students (Admin)

### 0.B Output a one-line "Design Read" before generating
Before any code, state:
**"Reading this as: <portal> <screen type> for <persona>, with <density> density, <motion> motion, leaning toward <layout pattern>."**

Example reads:
- *"Reading this as: Admin Home dashboard for school principals, with cockpit density, restrained motion, leaning toward hero → urgent → important → context hierarchy."*
- *"Reading this as: Parent Fees list for low-literacy parents, with airy density, minimal motion, leaning toward WhatsApp-style feed with clear due amounts."*
- *"Reading this as: Teacher Attendance screen for time-poor teachers, with balanced density, feedback-only motion, leaning toward one-tap grid with confirm snackbar."*

### 0.C If the brief is ambiguous, ask one question
Ask exactly **one** clarifying question — never a multi-question dump. Only when the design read genuinely diverges. If you can confidently infer, declare the read and proceed.

### 0.D Anti-Default Discipline
Do NOT default to: flat card grids, equal-weight stat cards, generic empty states with illustrations, gradient backgrounds, gamification widgets, hardcoded sample data, oversized text, icon-only buttons. These are AI slop. Reach past them based on the design read.

---

## 1. THE THREE DIALS (Core Configuration)

After the design read, set three dials. Every layout, motion, and density decision is gated by these.

### Portal Defaults

| Portal | DESIGN_VARIANCE | MOTION_INTENSITY | VISUAL_DENSITY | Rationale |
|---|---|---|---|---|
| **Admin** | 4 | 5 | 8 | Commanding, organized, data-rich. Low variance = predictable layout. High density = cockpit. |
| **Teacher** | 5 | 4 | 6 | Speed-first, balanced. Low motion = no delay. Medium density = scannable. |
| **Parent** | 6 | 3 | 4 | Warm, spacious, simple. Low motion = information first. Low density = breathing room. |

### How the Dials Drive Output

- **DESIGN_VARIANCE > 6**: Asymmetric layouts allowed. Split screens, offset content, varied section shapes.
- **DESIGN_VARIANCE <= 5**: Predictable, organized layouts. Aligned grids, consistent section structure.
- **MOTION_INTENSITY > 4**: Entry transitions on screen load, scroll-reveal on key sections, press physics on interactive elements.
- **MOTION_INTENSITY <= 3**: Motion confirms action only (snackbar, dialog, press scale). No decorative animation.
- **VISUAL_DENSITY > 7**: Monospace for numbers, tight spacing, border-based separation (not shadows), minimal decorative whitespace.
- **VISUAL_DENSITY <= 5**: Generous spacing, comfortable touch targets, whitespace as design element.

---

## 2. TOKEN LOCK (Mandatory — Zero Raw Values)

### 2.A Color Lock
ALL colors come from `VColors`. NEVER hardcode hex values in composables.

Available tokens (defined in `ui/tokens/VColors.kt`):
- Warm Cream Base: `cream`, `creamDeep`, `white`
- Warm Surfaces: `surface`, `surfaceCard`, `surfaceElevated`, `surfaceTint`, `surfaceWarm`
- On-surface: `ink`, `ink2`, `ink3`, `line`, `lineSoft`
- Primary: `violet`, `violetHover`, `violetSoft`, `violetInk`
- Accents: `coral`/`coralSoft`, `gold`/`goldSoft`, `sky`/`skySoft`, `mint`/`mintSoft`
- States: `success`/`successSoft`, `error`/`errorSoft`

### 2.B Color Consistency Lock
- **One accent per screen.** If a screen uses `coral` for urgency, all urgent elements use `coral`. Do not mix `coral` and `error` for the same semantic meaning.
- **Semantic color mapping** (use consistently across all portals):
  - Fees/urgent/due → `coral` / `coralSoft`
  - Success/present/paid → `success` / `successSoft` or `mint` / `mintSoft`
  - Warning/late/pending → `gold` / `goldSoft`
  - Info/notice → `sky` / `skySoft`
  - Primary action/CTA → `violet` / `violetSoft`
  - Error/absent/failed → `error` / `errorSoft`
- **No pure black.** `ink` (`#1A1614`) is the darkest text color. Never use `Color.Black`.
- **Surface hierarchy**: `surface` (base) → `surfaceCard` (cards) → `surfaceElevated` (dialogs/overlays).

### 2.C Shape Lock
ALL shapes come from `VShapes`. NEVER hardcode `RoundedCornerShape(N.dp)`.

Available tokens (defined in `ui/tokens/VShapes.kt`):
- `sm` = 10.dp — badges, chips, small inputs
- `md` = 14.dp — buttons, medium cards
- `lg` = 18.dp — cards, list items
- `xl` = 24.dp — large cards, bottom sheets
- `xxl` = 32.dp — hero cards, overlay containers
- `full` = 50% — pills, avatars, FABs

**Shape Consistency Rule**: Pick the right shape for the component type and NEVER mix:
- Badges/chips → `VShapes.sm` or `VShapes.full`
- Buttons → `VShapes.md`
- Cards → `VShapes.lg`
- Overlays/sheets → `VShapes.xl` or `VShapes.xxl`
- Avatars/FABs → `VShapes.full`

### 2.D Typography Lock
ALL text styles come from `VTypography`. NEVER hardcode `fontSize`, `fontWeight`, `lineHeight`.

Available tokens (defined in `ui/tokens/VTypography.kt`):
- `h1` = 44sp ExtraBold — splash, hero numbers
- `h2` = 24sp ExtraBold — screen titles
- `h3` = 22sp ExtraBold — section headers
- `body` = 15sp Medium — primary body text
- `bodySmall` = 14sp Medium — secondary body text
- `label` = 13sp SemiBold — labels, badges, metadata
- `caption` = 12sp Medium — timestamps, helper text
- `accentLabel` = 13sp Bold — accent text
- `wordmark` = 16sp ExtraBold — brand name

**Typography discipline**: Use tokens AS-IS. Do NOT override font sizes. Hierarchy through weight + color, not raw scale. `ink` for primary text, `ink2` for secondary, `ink3` for tertiary.

### 2.E Motion Lock
ALL animations use `VMotion` durations and easings. NEVER hardcode `tween(duration)` or raw easing curves.

Available tokens (defined in `ui/tokens/VMotion.kt`):
- `ease` = CubicBezierEasing(0.2f, 0f, 0f, 1f)
- `durFast` = 150ms — press feedback, toggle
- `durDefault` = 250ms — standard transitions
- `durSlow` = 400ms — screen transitions, dialog
- `durSlower` = 700ms — staggered reveals
- Helpers: `tweenFast()`, `tweenDefault()`, `tweenSlow()`, `tweenSlower()`

**Motion rules**: Use VMotion helpers. Spring physics for interactive: `spring(dampingRatio = 0.7f, stiffness = 300f)`. No `LinearEasing`. No raw `tween(300)`. Press feedback: `scale(0.97f)` with `tweenFast()`. Screen entry: `fadeIn() + slideInVertically()` with `tweenSlow()`.

---

## 3. DESIGN ENGINEERING DIRECTIVES (Bias Correction)

### 3.A Layout Diversification
- **ANTI-GRID BIAS**: The generic "3-4 identical cards in a grid" is banned for Admin. Admin Home is a hierarchy: hero → urgent → important → context. NOT a flat grid.
- **Section-Layout-Repetition Ban**: Once you use a layout pattern for a section, it can appear at most ONCE on a screen. Vary: full-width hero, 2-up grid, horizontal scroll, list, split, carousel.
- **Bento cell count**: N items → N cells. No empty cells. No filler cells. No "blank tile to make the grid even."
- **Eyebrow restraint**: Max 1 uppercase tracking label per 3 sections. Do NOT put `ATTENDANCE` / `FEES` / `EVENTS` labels above every section. Use sparingly for hierarchy, not decoration.
- **Meta-Label Ban**: No "SECTION 01", "SECTION 04", "ITEM 05" labels. They look cheap.

### 3.B Materiality & Depth
- **TONAL SURFACES, NOT SHADOWS**: Depth from surface color hierarchy, not `Modifier.shadow()`.
  - Admin: `surface` base → `surfaceCard` cards → `surfaceElevated` overlays
  - Parent/Teacher: `cream` base → `surfaceCard` cards → `surfaceElevated` overlays
- **When shadow is used** (rare, overlays/FABs only): tint to background hue. `Modifier.shadow(4.dp, VShapes.lg, clip = true)` — never raw black shadows.
- **Border-based separation**: `Modifier.border(1.dp, VColors.line, VShapes.lg)` for subtle card borders. Prefer over shadows for list items.
- **No glassmorphism** on cards. `Modifier.blur()` is for overlays only, never scrolling content.

### 3.C Interactive States (4 States Every Screen)
1. **Loading**: Skeletal loaders matching final layout shape. `Modifier.background(VColors.lineSoft)` placeholder boxes with subtle shimmer. NO `CircularProgressIndicator` as primary loading.
2. **Empty**: Actionable, not decorative. "No teachers yet" + "Add Teacher" button. NOT an illustration. Show the path to populate.
3. **Error**: Clear, inline for forms. Snackbar for transient. "Couldn't load. Pull to retry." with retry button. NOT "Something went wrong."
4. **Content**: Data-driven. Every value from ViewModel → API → backend. ZERO hardcoded data.

### 3.D Tactile Feedback
- Every clickable element: `scale(0.97f)` with `VMotion.tweenFast()` on press.
- Buttons: `scale(0.98f)`. Cards: `scale(0.99f)`.
- Snackbar after every save/delete/update with checkmark + "Done".

### 3.E Touch Target Discipline
- **48dp minimum** Teacher (one-handed use), **44dp minimum** Parent (accessibility), **40dp minimum** Admin (dense dashboard).
- Icon + label ALWAYS paired. Never icon-only unless universally understood (back arrow, close X, search).

---

## 4. MOTION DISCIPLINE

### 4.A Motion Must Be Motivated
Before adding any animation, ask: "What does this communicate?" Valid: hierarchy, storytelling, feedback, state transition. Invalid: "it looked cool", "the prototype had it." If you cannot articulate the reason in one sentence, drop the animation.

### 4.B Motion Claimed = Motion Shown
If `MOTION_INTENSITY > 3`, the screen MUST have working motion (entry transition, scroll-reveal, press physics). A static screen claiming `MOTION_INTENSITY: 5` is broken. If you can't ship working motion, drop the dial to 2.

### 4.C Permitted Motion Patterns (Compose)
- **Screen entry**: `fadeIn(VMotion.tweenSlow()) + slideInVertically(initialOffsetY = { it / 20 })`
- **List item reveal**: `AnimatedVisibility(enter = fadeIn() + slideInVertically())` with staggered delays (`index * 50ms`)
- **Press feedback**: `graphicsLayer { scaleX = if (pressed) 0.97f else 1f; scaleY = if (pressed) 0.97f else 1f }`
- **Overlay enter**: `slideInVertically(initialOffsetY = { it }) + fadeIn()` with `VMotion.tweenSlow()`
- **Snackbar**: `slideInVertically(initialOffsetY = { it })` with `VMotion.tweenDefault()`
- **Tab switch**: `Crossfade(targetState = tab, animationSpec = VMotion.tweenDefault())`
- **Number count-up**: `AnimatedContent` with `VMotion.tweenSlow()` for metric values

### 4.D Forbidden Motion Patterns
- `LinearEasing` — always use `VMotion.ease`
- Raw `tween(300)` — always use `VMotion.tweenDefault()` etc.
- Animations on `width`/`height` — animate `scale`/`alpha`/`translation` instead
- Infinite shimmer on more than 1 element per screen
- Bounce/overshoot on every element — reserve for celebration moments only
- Animation that delays information access (Teacher/Parent — speed first)

---

## 5. PORTAL-SPECIFIC DIRECTIVES

### 5.A Admin Portal (VARIANCE: 4, MOTION: 5, DENSITY: 8)

**Persona**: Indian school principals, admin staff managing 200-2000 students.

**Premium for Admin IS**:
- PREMIUM GREY BASE — `VColors.surface` is the foundation. NOT white.
- TONAL SURFACES — surface hierarchy creates depth without shadows.
- INFORMATION HIERARCHY — hero metrics → pulse gauge → quick actions → activity. NOT a flat grid.
- Destructive actions always confirm with a dialog.
- 200-400ms animations (`VMotion.durSlow` to `durSlower`).
- Every drill-down path works. Every metric taps to detail.

**Premium for Admin is NOT**:
- Gradients — admin is a professional, not a consumer
- Glassmorphism — decorative, not functional
- Same radius on everything — lazy
- Drop shadows on every card — Material 2
- Beautiful empty states with illustrations — admins want actionable empties
- Hardcoded fake data — every metric flows from ViewModel → API → backend
- Flat lists of identical cards — no information hierarchy = slop

**Layout pattern**: Hero (greeting + date + bell) → Insights carousel → Pulse gauge → Quick actions → KPI grid → Activity feed → Context sections (events, birthdays, spotlight)

**Monospace for numbers**: At DENSITY 8, numeric metrics use `FontWeight.Black` with `VTypography.h2` for hero numbers to align visually.

**Status badge colors**:
- Present/active → `mintSoft` bg + `success` text
- Absent/inactive → `errorSoft` bg + `error` text
- Pending/late → `goldSoft` bg + `gold` text (dark text: `ink`)
- Notice/info → `skySoft` bg + `sky` text (dark text: `ink`)

### 5.B Teacher Portal (VARIANCE: 5, MOTION: 4, DENSITY: 6)

**Persona**: Classroom teacher, age 22-55, medium digital literacy, time-poor, one-handed use between classes.

**Speed-first design**:
- 1-2 taps to core actions (attendance, homework, grading)
- Big tap targets (48dp minimum)
- Minimal decorative motion — motion confirms action, doesn't delay it
- Clear "done" confirmation — snackbar with checkmark after every save
- No gesture-only discoverability — every action has a visible button
- Offline tolerance — show what you have, queue writes, don't crash
- Forgiving flows — mistakes can be undone

**Layout pattern**: Today's schedule → Quick actions → Pending tasks → Recent activity. Scannable in 5 seconds.

### 5.C Parent Portal (VARIANCE: 6, MOTION: 3, DENSITY: 4)

**Persona**: Mother/father/guardian, age 25-55+, low-to-medium digital literacy, WhatsApp-literate, shared family phone, patchy connectivity.

**Simplicity-first design**:
- 1-2 taps to see child's status
- WhatsApp mental model: feed-like home, chat-style messages
- Plain language: "Fee Due ₹2,500" not "Outstanding Ledger Balance"
- Big tap targets (48dp minimum), generous spacing
- Icon + label ALWAYS paired
- No gamification — XP, levels, badges, progress rings = banned
- No complex data tables — "87.3% average" not a spreadsheet
- Animation that delays information = banned

**Layout pattern**: Child status card → Today's summary → Messages → Fees → Events. Feed-like, scannable, calm.

---

## 6. COMPOSE LAYOUT SAFETY (Anti-Crash Rules)

1. **No fixed heights on growing content.** Use `weight()` + `verticalScroll()` or `LazyColumn`. Never `Modifier.height(200.dp)` on dynamic lists.
2. **No `fillMaxSize()` inside scrollable containers.** Use `fillMaxWidth()` or `wrapContentHeight()`.
3. **LazyColumn/LazyRow keys**: Always provide `key = { item -> item.id }` to prevent recomposition bugs.
4. **No negative padding.** Use `offset` if you need overlap.
5. **No circular recomposition.** Don't update `State` in `LaunchedEffect` that reads the same `State`.
6. **No `remember` without key for derived values.** Use `remember(key1, key2) { ... }` when inputs change.
7. **No unbounded `LazyColumn`.** Always provide `contentPadding` or constrain height.
8. **Every screen has a Scaffold.** Never raw `Column` as screen root. Use `Scaffold(topBar = ..., bottomBar = ..., snackbarHost = ...)`.
9. **Every overlay has a scrim.** `Scrim(color = VColors.ink.copy(alpha = 0.5f), onClick = onDismiss)`.
10. **No `drawBehind` with unsized canvas.** Always provide bounds.
11. **No `InfiniteSizeConstraints` crash**: Test overlays with `Dialog` or `BottomSheet`, not `Popup` with manual sizing.
12. **No `derivedStateOf` without read.** Wrap in `remember { derivedStateOf { } }` and read it.

---

## 7. DATA FLOW DISCIPLINE (Zero Hardcoded)

### 7.A The Chain
```
Screen (Composable) → ViewModel (StateFlow<UiState>) → Repository → API (Ktor) → Backend → Database
```

### 7.B Rules
- **ZERO hardcoded data in composables.** No `val sampleStudents = listOf("Rahul", "Priya")`. No mock metrics.
- Every visible value comes from a ViewModel `StateFlow`.
- ViewModel exposes `UiState` sealed interface: `Loading`, `Error(message)`, `Empty`, `Success(data)`.
- Screen observes `viewModel.state.collectAsStateWithLifecycle()`.
- Loading → skeleton. Error → retry. Empty → action. Success → data.
- If backend isn't ready, ViewModel returns `Error("Not yet available")` — NOT fake data.

### 7.C Content Authenticity
- **No generic names.** "John Doe", "Sarah Chan" = banned. Use realistic Indian names: "Arjun Sharma", "Priya Iyer", "Mohammed Khan", "Ananya Reddy".
- **No fake-perfect numbers.** `99.99%`, `50%`, `1234567` = banned. Use organic data: `87.3%`, `₹2,500`, `42 students`.
- **No filler verbs in UI copy.** "Elevate", "Seamless", "Unleash", "Next-Gen", "Revolutionize" = banned. Plain language: "Mark attendance", "Pay fees", "Send message".
- **No startup-slop brand names.** "Acme", "Nexus", "SmartFlow" = banned. Use real school names: "Delhi Public School", "Kendriya Vidyalaya", "St. Mary's Convent".

---

## 8. AI TELLS (Forbidden Patterns in Compose)

### 8.A Visual Tells
- **No neon glows.** `Modifier.shadow()` with high elevation = banned. Use tonal surfaces.
- **No pure black.** `Color.Black` = banned. Use `VColors.ink`.
- **No oversaturated backgrounds.** Full-screen `violet` or `coral` backgrounds = banned. Use `surface`/`cream` base.
- **No gradient backgrounds on cards.** `Brush.linearGradient()` on card backgrounds = banned. Solid `VColors.surfaceCard` only.
- **No glassmorphism on cards.** `Modifier.blur()` on scrolling content = banned. Overlays only.

### 8.B Layout Tells
- **No 3-column equal feature cards.** The generic "three identical cards horizontally" = banned. Use asymmetric grid, 2-up, or list.
- **No flat card grids for Admin.** Admin Home is a hierarchy, not a grid.
- **No cards-inside-cards-inside-cards.** Max 1 level of nesting. Card → content. Not Card → Card → content.
- **No giant rounded wrapper sections around everything.** Don't wrap an entire screen section in a `VShapes.xxl` card for decoration.
- **No overcompartmentalized dashboard framing.** Don't put every metric in its own bordered box. Group related metrics.

### 8.C Typography Tells
- **No oversized H1s.** `VTypography.h1` (44sp) is for splash/hero numbers ONLY. Screen titles use `h2` (24sp).
- **No all-caps section headers.** Use `VTypography.h3` with normal case. Eyebrows (uppercase labels) max 1 per 3 sections.
- **No gradient text.** `Brush.linearGradient()` on `Text` = banned.
- **No font size overrides.** Use `VTypography` tokens as-is. Don't do `style = VTypography.body.copy(fontSize = 18.sp)`.

### 8.D Content Tells
- **No generic names.** Use realistic Indian names.
- **No fake-perfect numbers.** Use organic, messy data.
- **No filler verbs.** "Elevate", "Seamless", "Unleash", "Next-Gen" = banned.
- **No fake brand names.** "Acme", "Nexus", "SmartFlow" = banned.
- **No emoji in code or UI text.** Replace with proper icons or clean text.

### 8.E Compose-Specific Tells
- **No `MaterialTheme` color overrides.** Use `VColors` directly. Don't create custom `ColorScheme`.
- **No raw `dp` values for common spacing.** Use standard multiples: 4.dp, 8.dp, 12.dp, 16.dp, 20.dp, 24.dp.
- **No `Modifier.padding(all = 16.dp)` for asymmetric layouts.** Use explicit `padding(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 12.dp)`.
- **No `Column` with 10+ children and no `Spacer`.** Use `Spacer(Modifier.height(12.dp))` between elements.
- **No `Row` with 5+ children that doesn't scroll.** Use `LazyRow` or wrap to `Column`.

---

## 9. OUTPUT ENFORCEMENT (No Truncation, No Placeholders)

### 9.A Banned Output Patterns
**In code blocks**: `// ...`, `// rest of code`, `// implement here`, `// TODO`, `/* ... */`, `// similar to above`, `// continue pattern`, bare `...`

**In prose**: "Let me know if you want me to continue", "for brevity", "the rest follows the same pattern", "similarly for the remaining", "and so on", "I'll leave that as an exercise"

**Structural shortcuts**: Skeleton when full implementation was requested, showing first and last while skipping middle, replacing repeated logic with one example + description, describing what code should do instead of writing it.

### 9.B Handling Long Outputs
- Do NOT compress remaining sections to squeeze them in.
- Do NOT skip ahead to a conclusion.
- Write at full quality up to a clean breakpoint (end of a composable function, end of a file).
- End with: `[PAUSED — X of Y complete. Send "continue" to resume from: next file/function name]`
- On "continue", pick up exactly where stopped. No recap, no repetition.

### 9.C Code Quality
- Every composable compiles. No missing imports. No unresolved references.
- Every composable is immediately runnable — no "wire this up later."
- Every ViewModel method called in UI exists in the actual ViewModel file.
- Every API endpoint referenced exists in the actual API file.
- Every navigation route referenced exists in the NavGraph.

---

## 10. PRE-FLIGHT CHECK (Run Before Outputting Any Screen)

**THIS IS NOT OPTIONAL. Run every box. If any box fails, the output is not done.**

- [ ] **Design Read declared** (one-liner stating portal, screen type, persona, density, motion, layout pattern)?
- [ ] **Dial values** match portal defaults (Admin 4/5/8, Teacher 5/4/6, Parent 6/3/4)?
- [ ] **ZERO raw hex colors** — all colors from `VColors`?
- [ ] **ZERO raw `RoundedCornerShape`** — all shapes from `VShapes`?
- [ ] **ZERO raw `fontSize`/`fontWeight`** — all text from `VTypography`?
- [ ] **ZERO raw `tween()` durations** — all motion from `VMotion`?
- [ ] **Color Consistency Lock** — one accent per screen, semantic mapping followed?
- [ ] **Shape Consistency Lock** — correct `VShapes` per component type?
- [ ] **4 states implemented** — Loading (skeleton), Empty (actionable), Error (retry), Content (data-driven)?
- [ ] **ZERO hardcoded data** — all values from ViewModel StateFlow?
- [ ] **Touch targets** meet minimum (48dp Teacher/Parent, 40dp Admin)?
- [ ] **Icon + label paired** — no icon-only buttons (except back/close/search)?
- [ ] **Press feedback** on every clickable element (`scale(0.97f)` + `VMotion.tweenFast()`)?
- [ ] **No AI tells** — no gradients on cards, no glassmorphism, no flat grids for Admin, no oversized text?
- [ ] **No filler verbs** in UI copy — "Elevate", "Seamless", "Unleash" = absent?
- [ ] **No generic names** — realistic Indian names used?
- [ ] **No emoji** in code or UI text?
- [ ] **Scaffold used** as screen root — not raw `Column`?
- [ ] **No fixed heights** on growing content — `weight()` + `scroll()` used?
- [ ] **LazyColumn/LazyRow keys** provided — `key = { item -> item.id }`?
- [ ] **Motion motivated** — every animation can be justified in one sentence?
- [ ] **No truncated output** — no `// ...`, no `// TODO`, no "rest follows same pattern"?
- [ ] **Every import present** — code compiles as-is?
- [ ] **Every ViewModel method exists** — no calls to non-existent methods?
- [ ] **Every navigation route exists** — no references to undefined routes?

---

## 11. REFERENCE VOCABULARY (Pattern Names to Know)

### Layout Patterns
- **Hero → Context Cascade**: Admin Home pattern. Hero metrics → pulse → quick actions → activity → context. Eye flows top to bottom, urgency decreases.
- **Feed List**: Parent Home pattern. Vertical scroll of cards, each card = one update. WhatsApp-style.
- **Quick Action Grid**: Teacher Home pattern. Horizontal scroll of circular icon buttons for 1-tap actions.
- **Split Detail**: Master-detail pattern. List on left (or top), detail on right (or overlay).
- **Tab + Overlay**: 5 bottom tabs, each tab has its own scrollable content. Overlays slide up from bottom for detail/form screens.

### Card Patterns
- **Hero Metric Card**: Large number + label + trend arrow + tap-to-detail. Uses `VColors.surfaceCard` + `VShapes.lg`.
- **Status Badge**: Small pill with soft bg + accent text. `VShapes.full` + `VColors.*Soft` bg.
- **List Item Card**: Avatar + name + subtitle + chevron. `VColors.surfaceCard` + `VShapes.lg` + border.
- **Action Card**: Icon + label + description + tap action. `VColors.surfaceCard` + `VShapes.lg`.
- **Empty State Card**: Icon + message + action button. `VColors.surfaceTint` + `VShapes.xl`.

### Motion Patterns
- **Staggered Reveal**: List items appear with cascading delay (`index * 50ms`). Use for first screen load only.
- **Press Scale**: Element scales to 0.97f on press, springs back on release. Every clickable.
- **Snackbar Confirm**: Slides up from bottom after save/delete. Auto-dismisses after 3s.
- **Overlay Slide-Up**: Bottom sheet slides from bottom with scrim fade-in.
- **Crossfade Tab**: Content crossfades when switching tabs. No slide.

### State Patterns
- **Skeleton Shimmer**: Gray placeholder boxes with left-to-right shimmer sweep. Matches final layout shape.
- **Error + Retry**: Error message + retry button. Centered. `VColors.errorSoft` background.
- **Empty + Action**: Message + primary action button. "No data yet" + "Add first" CTA.
- **Content**: Real data from ViewModel. Every value observable.

---

## 12. EXECUTION PROTOCOL

When generating any EnrollPlus UI screen, follow this exact sequence:

1. **[DESIGN READ]** Declare the one-line design read (Section 0.B).
2. **[DIAL CHECK]** Confirm dial values match portal defaults.
3. **[TOKEN CHECK]** Verify all colors/shapes/typography/motion will use VColors/VShapes/VTypography/VMotion.
4. **[STATE PLAN]** Plan all 4 states (Loading, Empty, Error, Content) before writing the content state.
5. **[DATA CHECK]** Identify which ViewModel and StateFlow feeds this screen. Confirm methods exist.
6. **[LAYOUT PLAN]** Sketch the section hierarchy. Verify no layout repetition. Verify bento cell count.
7. **[MOTION PLAN]** List every animation and its motivation. Drop any that can't be justified.
8. **[BUILD]** Write the composable. Full implementation. No placeholders.
9. **[PRE-FLIGHT]** Run the Section 10 checklist. Fix any failures.
10. **[OUTPUT]** Deliver complete, compilable code.
