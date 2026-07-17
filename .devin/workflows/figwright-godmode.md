---
description: Full-stack Figma-to-code feature delivery via Figwright MCP (READ-ONLY Figma access). Study Figma designs, preserve existing UI/UX, implement changes DB→Backend→API→VM→UI, then reverse-verify UI→DB with a 5-iteration convergence loop. Enforces button visibility, partial-scroll patterns, and workflow completeness.
---

# Figwright God Mode — Figma-Driven Full-Stack Feature Delivery

> **MANDATORY WORKFLOW when a Figma design is provided via the Figwright MCP.**
> Extends AGENT.md §3 (Full-Stack Feature Completion Graph) with Figma design ingestion,
> existing-system preservation, and professional UX enforcement.
> Every feature delivered through this workflow MUST pass all phases or loop back.

> **CRITICAL: Figma access is READ-ONLY.**
> NEVER call write tools on Figma (no create_frame, set_fills, set_text, create_component,
> move_nodes, delete_nodes, etc.). Only use READ tools:
> - mcp0_ping, mcp0_get_metadata, mcp0_get_pages, mcp0_navigate_to_page
> - mcp0_get_selection, mcp0_get_design_context, mcp0_get_node, mcp0_get_nodes_info
> - mcp0_get_screenshot, mcp0_get_styles, mcp0_get_variable_defs
> - mcp0_get_fonts, mcp0_get_local_components, mcp0_get_component_api
> - mcp0_get_annotations, mcp0_get_reactions, mcp0_get_viewport
> - mcp0_scan_text_nodes, mcp0_scan_nodes_by_types, mcp0_search_nodes
> - mcp0_token_map, mcp0_component_map, mcp0_icon_map, mcp0_scan_components
> - mcp0_design_diff, mcp0_save_screenshots, mcp0_save_image_fills
> - mcp0_analyze_project
> All code changes happen in the Kotlin codebase, NOT in Figma.

---

## Phase 1: EXISTING SYSTEM ANALYSIS

> **GOAL**: Understand the current system before changing anything.
> We preserve existing UI/UX unless the Figma design explicitly changes it.

### Step 1.1 — Trace Current Implementation (all 7 layers)
```
[CUR-1] UI Screen
        → Search composeApp/.../ui/v2/screens/{role}/ for the existing screen
        → Read the FULL composable
        → Record: current layout, current buttons, current state handling, current scroll pattern

[CUR-2] ViewModel
        → Search shared/.../feature/{feature}/presentation/ for the VM
        → Read the FULL ViewModel
        → Record: current state class, current functions, current API calls

[CUR-3] API Client
        → Search shared/.../feature/{feature}/data/remote/ for the API
        → Read the FULL API client
        → Record: current endpoints, current method signatures

[CUR-4] Backend (Router + Service + Dao + Models)
        → Search server/.../feature/{feature}/ for all four files
        → Read ALL four
        → Record: current routes, current DB queries, current DTOs

[CUR-5] Database
        → Search database/migrations/ for the feature's SQL
        → Read DatabaseFactory.kt for table registration
        → Record: current table structure, columns, indexes

[CUR-6] Navigation
        → Search NavGraphV2.kt for the feature's route
        → Record: current route string, deep-link, entry point

[CUR-7] DI
        → Search Koin.kt for the feature's registrations
        → Record: what's registered (API, Repo, VM)
```

### Step 1.2 — Current System Summary
```markdown
## Current System: [Feature Name]

### Files
| Layer | File | Status |
|-------|------|--------|
| UI | {path} | Exists — needs {changes} |
| VM | {path} | Exists — needs {changes} |
| API | {path} | Exists — needs {changes} |
| Backend | {path} | Exists — needs {changes} |
| DB | {path} | Exists — needs {changes} |
| Nav | {path} | Exists — needs {changes} |
| DI | {path} | Exists — needs {changes} |

### What Works (DO NOT BREAK)
- {list of working features that must be preserved}

### What Changes (from Figma study)
- {list of changes needed, mapped to layers}

### What's New
- {list of entirely new components/endpoints/tables}
```

---

## Phase 2: PLANNING (Figma-Driven)

> Merge the Figma Design Study (Phase 0) with the Current System Analysis (Phase 1)
> to produce a precise change plan. No code is written in this phase.

### Step 2.1 — Change Impact Matrix
```
[PLAN-1] For each visual/data change in the Figma study:
         → Does it need a DB schema change? (new column, new table, alter type)
         → Does it need a backend route change? (new endpoint, modified DTO, new query)
         → Does it need an API client change? (new method, modified model)
         → Does it need a ViewModel change? (new state, new function, modified logic)
         → Does it need a UI change? (new composable, modified layout, new button)
         → Does it need a navigation change? (new route, modified deep-link)
         → Does it need a DI change? (new registration)

[PLAN-2] Build dependency graph
         DB → Backend → API → VM → UI → Nav → DI
         Mark each change as NEW / MODIFIED / DELETED

[PLAN-3] Map user workflow from Figma
         For EACH screen in the Figma:
         → Entry point → Each screen → Each button → Each action → Success/Error/Empty
         → Which buttons are in the fixed header? (always visible)
         → Which buttons are in the fixed footer? (always visible)
         → Which buttons are in the scroll content? (scroll with content)
         → Which buttons need confirmation dialogs?
         → What is the back/cancel flow?
         → What is the loading state? Empty state? Error state? Success state?

[PLAN-4] Identify scroll behavior from Figma
         → Fixed header + scrolling content below?
         → Fixed footer (action bar) + scrolling content above?
         → BOTH fixed header AND fixed footer with scroll in middle?
         → Tabs that stay fixed while content below scrolls?
         → Full screen scroll (no fixed elements)?
         → Record the EXACT scroll pattern for each screen
```

### Step 2.2 — Execution Plan Output
```markdown
## Execution Plan: [Feature Name]

### Change Summary
| # | Change | Layers | Type | Priority |
|---|--------|--------|------|----------|
| 1 | Add filter chips to header | UI | Modified | High |
| 2 | Add new filter API endpoint | DB, Backend, API, VM | New | High |
| 3 | Convert full scroll to fixed-header+scroll | UI | Modified | Critical |

### Scroll Pattern per Screen
| Screen | Header | Middle | Footer | Pattern |
|--------|--------|--------|--------|---------|
| ListScreen | Fixed (title+filters) | Scrollable list | Fixed (action bar) | Column { Header; LazyColumn(weight 1f); Footer } |
| FormScreen | Fixed (title+back) | Scrollable form | Fixed (Submit+Cancel) | Column { Header; Column(weight 1f, scroll); Footer } |

### Button Inventory per Screen
| Screen | Button | Location | Scroll Behavior | Action | Wired to | Confirmation? |
|--------|--------|----------|-----------------|--------|-----------|---------------|
| ListScreen | "Add New" | FAB | Fixed | Navigate to Form | navController.navigate() | No |
| ListScreen | "Delete" | List item | Scrolls | Delete item | vm.deleteItem(id) | Yes (dialog) |
| FormScreen | "Save" | Footer | Fixed | Submit form | vm.saveItem() | No |
| FormScreen | "Cancel" | Footer | Fixed | Go back | navController.popBackStack() | Yes (if dirty) |
| FormScreen | "Back" | Header | Fixed | Go back | navController.popBackStack() | No |
```

---

## When to Use

- User provides a Figma file connected via the Figwright MCP
- User asks to pick screens from Figma and implement changes
- User wants to optimize/simplify existing UI without breaking current functionality
- User wants design-to-code with full backend/database adjustments

## Prerequisites

- Figwright MCP server is connected and Figma plugin is active (verify with mcp0_ping)
- AGENT.md has been read end-to-end
- Existing system is functional (we are optimizing, not rebuilding)

---

## Phase 0: FIGMA DESIGN INGESTION (Read-Only)

> **GOAL**: Extract the proposed design from Figma with 100% fidelity.
> Do NOT guess — use the MCP read tools to get the actual design.

### Step 0.1 — Connect & Verify
```
[FIG-1] Call mcp0_ping → verify Figwright MCP + Figma plugin are connected
        If not connected → STOP, tell user to connect Figma

[FIG-2] Call mcp0_get_metadata → record fileName, currentPage, all pages
        Identify which page contains the target screens

[FIG-3] Call mcp0_navigate_to_page {pageId} → switch to the target page
        Confirm page name matches what user described
```

### Step 0.2 — Screen Selection & Design Extraction
```
[FIG-4] Get the target nodes:
        → If user has selected in Figma: call mcp0_get_selection
        → If user gave a Figma URL: use the nodeId from it
        → Otherwise: call mcp0_get_pages, navigate, then scan

[FIG-5] Call mcp0_get_design_context {nodeId, detail: "full"}
        This returns the full node tree with:
        - Geometry (x, y, width, height)
        - Fills (exact RGBA), strokes, effects (shadows, blurs)
        - Text content + typography (font family, size, weight, line height, letter spacing)
        - Auto-layout config (mode, padding, itemSpacing, alignment, sizing)
        - Corner radius
        - Component instances + properties
        - Design token / variable bindings (resolved to names)
        - Deduped component subtrees (with textOverrides + propertyOverrides)
        Record ALL of this. This is the source of truth for the UI.

[FIG-6] Call mcp0_get_screenshot {nodeIds: [screenId]}
        Save the visual baseline. Take screenshots of sub-states if available
        (loading, empty, error — if they exist as separate frames in Figma).

[FIG-7] Call mcp0_save_screenshots {nodeIds, outDir: "docs/figma-exports/"}
        Persist PNGs to disk for reference during implementation.
```

### Step 0.3 — Token & Component Mapping
```
[FIG-8]  Call mcp0_get_variable_defs → get all Figma variables (colors, spacing, etc.)
         Call mcp0_get_styles → get paint/text/effect/grid shared styles
         Record Figma token names and values.

[FIG-9]  Call mcp0_token_map {rootDir: project root}
         Auto-maps Figma variables → project design tokens (CSS custom properties / Tailwind).
         Record which Figma tokens map to existing code tokens and which are new.

[FIG-10] Call mcp0_scan_components {rootDir: project root}
         Scans the Kotlin/Compose codebase for existing UI components.
         Returns component names + props.

[FIG-11] Call mcp0_component_map {nodeId: screenId, rootDir: project root}
         Maps Figma component instances → existing code components.
         Record: which Figma components have code equivalents (reuse) vs need new code.

[FIG-12] Call mcp0_icon_map {nodeId: screenId, rootDir: project root}
         Maps Figma icon nodes → existing .svg files in the project.
         Record: which icons can be reused vs need fresh export.

[FIG-13] Call mcp0_get_fonts → get all fonts used on the page
         Cross-reference with available fonts in the Compose app.
```

### Step 0.4 — Deep Node Inspection (when needed)
```
[FIG-14] For any complex component that get_design_context truncated:
         Call mcp0_get_node {nodeId} → full recursive subtree at max fidelity
         Use for: individual components, complex auto-layout frames, text nodes with rich formatting

[FIG-15] Call mcp0_get_component_api {nodeId} for any component instance
         Returns the full property API (variant/boolean/text/instance-swap props)
         Record: what properties each instance exposes (for setting in code)

[FIG-16] Call mcp0_get_annotations {nodeId} for any annotated nodes
         Dev Mode annotations may contain implementation notes from designers.

[FIG-17] Call mcp0_get_reactions {nodeId} for interactive elements
         Prototype reactions reveal navigation flows and interaction patterns.
         Record: click → navigate to which screen? hover → what happens?
         This tells you the INTENDED navigation graph from the designer's perspective.

[FIG-18] Call mcp0_scan_text_nodes {root: screenId}
         Get every TEXT node with its characters, fontSize, fontName.
         Use this to build the complete string inventory for the screen.

[FIG-19] Call mcp0_scan_nodes_by_types {root: screenId, types: ["FRAME", "COMPONENT"]}
         Get the structural skeleton of the screen for layout analysis.
```

### Step 0.5 — Design Diff (if modifying existing screen)
```
[FIG-20] If the screen already exists in the app:
         Call mcp0_design_diff {nodeId: screenId, rootDir: project root}
         First call → creates baseline, returns status "baseline-created"
         Subsequent calls → returns diff with per-node, per-property changes
         Record exactly what CHANGED: added/removed/changed nodes, fills, layout, text, tokens
```

### Step 0.6 — Design Study Output
Produce this structured summary before writing ANY code:

```markdown
## Figma Design Study: [Screen Name]

### Screen Metadata
- Figma nodeId: {id}
- Page: {pageName}
- Dimensions: {width}x{height}

### Layout Structure
- Root: {Frame/Component, auto-layout mode, sizing}
- Sections: {list of major sections with their layout}
- Scroll behavior: {whole screen scroll | fixed header + scroll middle + fixed footer | tab pager}
- Fixed header height: {px or "none"}
- Fixed footer height: {px or "none"}

### Color Palette (from Figma → project tokens)
| Figma Token | Figma Value | Project Token | Status |
|-------------|-------------|---------------|--------|
| Primary | #6266F0 | VColors.Primary | Match |
| Card BG | #FFFFFF | VColors.Surface | Match |
| New Accent | #FF6B6B | (none) | NEW — create token |

### Typography
| Element | Font | Size | Weight | Line Height | Project Token | Status |
|---------|------|------|--------|-------------|---------------|--------|
| Title | Inter | 24 | Bold | 32 | VTypography.h5 | Match |
| Body | Inter | 16 | Regular | 24 | VTypography.body1 | Match |

### Components Identified
| Figma Component | Code Equivalent | Action |
|----------------|-----------------|--------|
| Card/Item | VCard | Reuse |
| FilterChip | (none) | Create new |

### Icons
| Figma Icon | SVG File | Action |
|-----------|----------|--------|
| search | icons/search.svg | Reuse |
| filter-new | (none) | Export from Figma screenshot |

### Buttons & Actions (COMPLETE INVENTORY)
| Button | Label | Position | Scroll Behavior | Action | Confirmation? |
|--------|-------|----------|-----------------|--------|---------------|
| Submit | "Save" | Fixed footer | Always visible | Save form | No |
| Back | "←" | Fixed header | Always visible | Navigate back | No |
| Delete | "Delete" | In-list item | Scrolls with list | Delete item | Yes (dialog) |
| Cancel | "Cancel" | Fixed footer | Always visible | Go back | Yes (if dirty) |

### Navigation Flows (from mcp0_get_reactions)
| From | Trigger | To | Transition |
|------|---------|-----|-----------|
| ListScreen | ON_CLICK item | DetailScreen | Push |
| DetailScreen | ON_CLICK "Edit" | FormScreen | Push |
| FormScreen | ON_CLICK "Save" | ListScreen | Pop |

### Changes from Current Implementation (if modifying)
1. {what changed} — {why} — {which layers affected}
2. ...

### New API/Backend Needs
- New endpoint: {method} {path} — {purpose}
- Modified endpoint: {method} {path} — {what changed}
- New DB column: {table}.{column} — {type} — {purpose}
```

---

## Phase 3: FORWARD PASS — Database → Backend → API → ViewModel → UI

> Implement changes in dependency order. Each layer depends on the one below it.
> Follow AGENT.md §3 Phase 2 for the standard forward pass, with Figma-specific additions.

### Step 3.1 — Database Layer
```
[DB-1] Create/modify SQL migration
       → NEW table/column: database/migrations/setup_{feature}.sql or alter_{feature}_{change}.sql
       → Match the Figma data model: every field in the design needs a DB column
       → Add indexes for any new filter/sort fields shown in the Figma
       → Reference existing migration patterns in database/migrations/

[DB-2] Register/modify in DatabaseFactory
       → Add new tables to schema creation
       → Add ALTER TABLE for modifications
       → VERIFY: migration runs without errors
```

### Step 3.2 — Backend Layer
```
[BACKEND-1] Create/modify DTOs ({Feature}Models.kt)
       → Match Figma field names exactly (use @SerialName for JSON key mapping)
       → Every field shown in the Figma MUST have a corresponding DTO field
       → Every field the UI needs to display MUST be in the response DTO

[BACKEND-2] Create/modify DAO ({Feature}Dao.kt)
       → CRUD operations for new table/column
       → Filter/sort queries matching the Figma's filter/sort UI
       → VERIFY: every query returns all fields the UI needs

[BACKEND-3] Create/modify Service ({Feature}Service.kt)
       → Business logic for new/modified operations
       → Authorization checks (role-based access matching the portal)
       → Validation matching Figma form constraints (required fields, max lengths)

[BACKEND-4] Create/modify Router ({Feature}Router.kt)
       → Add/modify Ktor routes for every new/changed operation
       → JWT auth guard, role check, error handling
       → Mount in Application.kt if new router
       → VERIFY: every API method in the plan has a matching route

[BACKEND-5] Test endpoint
       → Verify route responds correctly
       → Verify response JSON matches the DTO structure
```

### Step 3.3 — API Client Layer
```
[API-1] Create/modify shared domain models
       → Match backend response DTOs exactly
       → @Serializable with @SerialName matching backend JSON keys
       → Every field the UI displays MUST exist in the model

[API-2] Create/modify API client ({Feature}Api.kt)
       → Add/modify methods for every new/changed endpoint
       → Use safeApiCall wrapper
       → Match URL paths to backend routes EXACTLY
       → VERIFY: every VM function has a corresponding API method

[API-3] Create/modify repository (if needed)
       → Wrap API calls, handle caching/offline if applicable
```

### Step 3.4 — DI Layer
```
[DI-1] Register in Koin (Koin.kt)
       → Register any new API, Repository, ViewModel
       → VERIFY: every class used in UI is registered
       → VERIFY: no "No bean found" runtime crashes
```

### Step 3.5 — ViewModel Layer
```
[VM-1] Create/modify ViewModel ({Feature}ViewModel.kt)
       → StateFlow for UI state: Loading, Success, Error, Empty
       → Expose functions for EVERY user action identified in the Figma button inventory
       → Handle loading/error/empty states explicitly
       → Re-fetch data after mutations (create/update/delete)
       → VERIFY: every button in the Figma has a corresponding VM function
       → VERIFY: every Figma prototype reaction (from mcp0_get_reactions) has a VM handler
```

### Step 3.6 — UI Layer (FIGMA FIDELITY IMPLEMENTATION)
```
[UI-1] Create/modify Composable screen ({Feature}Screen.kt)
       → Match the Figma design EXACTLY:
         * Layout structure (Figma frames → Column/Row/Box)
         * Colors (Figma fills → VColors tokens or exact RGBA from get_design_context)
         * Typography (Figma text → VTypography tokens or exact font/size/weight)
         * Spacing (Figma padding/itemSpacing → exact dp values)
         * Corner radius (Figma values → exact dp)
         * Shadows/effects (Figma drop shadows → Modifier.shadow with exact params)
         * Icons (mapped from Figma via icon_map → existing SVGs or new assets)
       → Use existing VTheme/VColors/VTypography tokens where they match
       → Only use raw values when no token matches (and note it for future tokenization)
       → Preserve existing UI/UX for parts NOT changed by the Figma design

[UI-2] Implement scroll behavior per the Figma design
       *** CRITICAL: Match the Figma's scroll pattern exactly ***

       PATTERN A — Fixed Header + Scroll Middle + Fixed Footer:
       Column(Modifier.fillMaxSize()) {
           // FIXED HEADER — statusBarsPadding, does NOT scroll
           HeaderBar(Modifier.statusBarsPadding())

           // SCROLLABLE MIDDLE — weight(1f) MANDATORY, NOT fillMaxSize
           Column(
               Modifier
                   .weight(1f)
                   .verticalScroll(rememberScrollState())
           ) {
               // content
           }

           // FIXED FOOTER — navigationBarsPadding + imePadding, does NOT scroll
           FooterBar(Modifier.navigationBarsPadding().imePadding())
       }

       PATTERN B — Fixed Header + LazyColumn + Fixed Footer:
       Column(Modifier.fillMaxSize()) {
           HeaderBar(Modifier.statusBarsPadding())
           LazyColumn(
               Modifier.weight(1f),
               contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp)
           ) { /* items */ }
           FooterBar(Modifier.navigationBarsPadding().imePadding())
       }

       PATTERN C — Full Screen Scroll:
       Column(
           Modifier
               .fillMaxSize()
               .statusBarsPadding()
               .navigationBarsPadding()
               .verticalScroll(rememberScrollState())
               .imePadding()
       ) { /* content */ }

       PATTERN D — Fixed Header + Tabs + Scroll Content:
       Column(Modifier.fillMaxSize()) {
           HeaderBar(Modifier.statusBarsPadding())  // fixed title
           TabRow(Modifier)                          // fixed tabs
           LazyColumn(Modifier.weight(1f)) { /* content scrolls */ }
       }

       PATTERN E — Scaffold (auto insets):
       Scaffold(
           topBar = { HeaderBar() },
           bottomBar = { FooterBar() }
       ) { padding ->
           Content(Modifier.padding(padding).verticalScroll(rememberScrollState()))
       }

       *** SCROLL RULES (NON-NEGOTIABLE) ***
       → weight(1f) is MANDATORY for the scroll section between fixed header/footer
       → NEVER use fillMaxSize on the scroll middle (it overlaps the footer)
       → NEVER wrap the whole screen (including header+footer) in verticalScroll
       → Fixed footer MUST have navigationBarsPadding() + imePadding()
       → Fixed header MUST have statusBarsPadding()
       → Submit/Cancel in fixed footer MUST be visible at ALL scroll positions
       → Back button in fixed header MUST be visible at ALL scroll positions
       → Last item in scroll section MUST be fully visible when scrolled to end
         → add contentPadding(bottom = 16.dp) on LazyColumn, or Spacer at end of Column
       → First item in scroll section MUST be fully visible when scrolled to top
         → add contentPadding(top = 8.dp) on LazyColumn
       → When keyboard opens: fixed footer moves up (imePadding) OR scroll section adjusts
       → On 360x640dp: verify scroll section has at least 200dp visible height
       → No double scrollbar — only the middle section scrolls

[UI-3] Wire UI to ViewModel
       → Collect state with collectAsState()
       → EVERY button onClick → ViewModel function call (NO DEAD BUTTONS)
       → Show loading indicator (CircularProgressIndicator/skeleton) during API calls
       → Show error message + retry button on failure
       → Show empty state ("No data" + action button) when list is empty
       → Show success confirmation (snackbar/toast) + navigation after action
       → Refresh data after mutations

[UI-4] Add/modify navigation route
       → Add to NavGraphV2.kt or portal internal navigation
       → Add deep-link parsing if applicable
       → VERIFY: route string matches navigation calls EXACTLY

[UI-5] Add/modify entry point
       → Add button/menu item/tab in the portal that navigates to this screen
       → VERIFY: button is visible, accessible, properly sized (≥48dp touch target)
       → VERIFY: button is NOT hidden behind bottom nav, FAB, or system UI
       → VERIFY: button has adequate padding (≥16dp from screen edges)
```

---

## Phase 4: REVERSE VERIFICATION PASS — UI → Database

> **THIS IS THE MOST CRITICAL PHASE.**
> Trace the complete path from every user action to the database and back.
> If ANY link is broken, fix it before declaring the feature complete.

### REV-1: Figma Design Fidelity Check
```
[REV-FIG-1] Compare implemented UI to Figma screenshot
           → Compare the Figma screenshot (from FIG-6) against the implemented screen
           → Layout structure matches?
           → Colors match (Figma value vs code value)?
           → Typography matches (font, size, weight)?
           → Spacing matches (padding, gaps)?
           → Corner radius matches?
           → Shadows/effects match?
           → Icons match?
           → Text content matches (from scan_text_nodes inventory)?
           → Any mismatch → fix before proceeding

[REV-FIG-2] Verify scroll behavior matches Figma
           → Implemented scroll pattern matches what the Figma shows?
           → Fixed header stays fixed when scrolling? (not scrolling away)
           → Fixed footer stays fixed when scrolling? (not scrolling away)
           → Middle section scrolls correctly?
           → No double scrollbars?
           → Tabs (if any) stay fixed when content scrolls?

[REV-FIG-3] Verify all Figma components are implemented
           → Cross-check every component from the design study against the code
           → No missing components
           → No extra components not in the Figma (unless preserving existing functionality)
```

### REV-2: Button Accessibility & Visibility Check
```
[REV-2a] Button visibility — ALL buttons
        → Is EVERY button VISIBLE on screen? (not clipped, not off-screen)
        → Is EVERY button REACHABLE by scroll? (not hidden behind fixed elements)
        → Is EVERY button TAPPABLE? (touch target ≥ 48dp × 48dp)
        → Is there enough PADDING? (≥16dp from screen edges, ≥12dp between buttons)
        → Does the screen fit on a small phone (360×640 dp)?
        → Test with fontScale = 1.3 (accessibility scale)

[REV-2b] NOT SHADOWED / NOT CUT OFF CHECKS
        → No button hidden behind bottom navigation bar
        → No button cut by system navigation bar / gesture area
        → No button overlapped by FAB
        → No button hidden by keyboard (IME) — imePadding() applied
        → No button covered by bottom sheet / dialog overlay (partial shadowing)
        → No button shadowed by sticky/fixed header or footer
        → No button cut by screen rounded corners / display cutout
        → No button hidden behind system UI overlay (cutout, notch)
        → All buttons have adequate z-index (not painted under another composable)
        → All buttons within safe content area
          (statusBarsPadding + navigationBarsPadding + imePadding applied)
        → Buttons in LazyColumn: contentPadding set so last item's button is fully visible
        → Buttons in bottom bar: navigationBarsPadding applied to bar container
        → Buttons in top bar: statusBarsPadding applied to top bar container
        → Content has bottom padding to avoid FAB overlap (FAB size + 16dp margin)
        → Content has bottom padding equal to bottom nav bar height

[REV-2c] PARTIAL SCROLL / FIXED HEADER + FIXED FOOTER CHECKS
        → If only MIDDLE section scrolls (header fixed, footer fixed):
          → Scrollable middle uses Modifier.weight(1f)? (NOT fillMaxSize — that overlaps footer)
          → Last item fully visible when scrolled to end?
            (contentPadding bottom or Spacer at end)
          → First item fully visible when scrolled to top?
            (contentPadding top)
          → Buttons inside scroll section reachable by scrolling?
            (not permanently trapped behind fixed footer)
          → Buttons in fixed footer ALWAYS visible regardless of scroll position?
          → Buttons in fixed header ALWAYS visible regardless of scroll position?
          → Fixed footer has navigationBarsPadding()? (not cut by system gesture bar)
          → Fixed header has statusBarsPadding()? (not cut by notch/status bar)
          → When keyboard opens: fixed footer moves up (imePadding) OR scroll adjusts?
          → No double scrollbar? (only middle scrolls, not whole screen)
          → On 360×640dp: scroll section has ≥200dp visible height after header+footer?
          → Filter/tab bar in header: scrolling content does NOT move the tabs?
          → If header height is dynamic: onSizeChanged captures it for contentPadding?
          → If footer height is dynamic: onSizeChanged captures it for contentPadding?
```

### REV-3: Button Wiring Check (Full Chain Trace)
```
[REV-3a] Trace EVERY button's onClick:
        → Button onClick → ViewModel function? (NO EMPTY onClick)
        → ViewModel function → API call? (NO MISSING API CALL)
        → API call → backend endpoint? (NO ORPHAN ENDPOINT)
        → Backend endpoint → database query? (NO MISSING DAO METHOD)
        → Response: DB → backend → API → ViewModel → UI state update? (NO DROPPED RESPONSE)

[REV-3b] Cross-check against Figma button inventory:
        → Every button in the Figma design study → has an onClick handler?
        → Every button in the Figma design study → has a VM function?
        → Every Figma prototype reaction (from get_reactions) → has a navigation handler?
        → NO DEAD BUTTONS. NO ORPHAN ENDPOINTS. NO DISCONNECTED NAVIGATION.
```

### REV-4: Workflow Completeness Check
```
[REV-4a] Can the user ENTER this feature? (entry point exists and works)
[REV-4b] Can the user go BACK from every screen? (back button, back gesture)
[REV-4c] Can the user CANCEL an in-progress action? (cancel/dismiss button)
[REV-4d] After SUCCESS, can the user return to where they started?
[REV-4e] After ERROR, can the user RETRY? (retry button, not just error text)
[REV-4f] Is there a CONFIRMATION dialog for destructive actions? (delete, reset, submit)
[REV-4g] Are there BOTH "Submit/Save" AND "Cancel/Back" on every form?
[REV-4h] Are there BOTH "Next" AND "Previous" on every multi-step flow?
[REV-4i] Does every list screen have pull-to-refresh OR a refresh button?
[REV-4j] Does every detail screen have a Back button in the top bar?
```

### REV-5: State Management Check
```
[REV-5a] Loading state: spinner/skeleton shown during API call? (NEVER blank screen)
[REV-5b] Error state: user-friendly message + retry button? (NEVER silent failure)
[REV-5c] Empty state: "No {items} found" + action button (e.g., "Add first {item}")?
[REV-5d] Success state: confirmation toast/snackbar + navigation to next logical screen?
[REV-5e] Does the screen REFRESH data after create/update/delete? (no stale data)
[REV-5f] Is data re-fetched on screen entry? (not just on first compose)
```

### REV-6: Data Flow Integrity Check
```
[REV-6a] UI shows data from ViewModel state (not direct API calls in Composable)
[REV-6b] ViewModel calls API → backend → DB (no layer skipped)
[REV-6c] Response: DB → backend → API → ViewModel → UI (no layer dropped)
[REV-6d] No direct DB access from UI
[REV-6e] No hardcoded data in UI that should come from API
[REV-6f] DTO fields match between backend response and shared model (@SerialName)
[REV-6g] API URL paths match backend route paths EXACTLY
```

### REV-7: Navigation Integrity Check
```
[REV-7a] Every navigate("route") call targets a route that EXISTS in NavGraphV2
[REV-7b] Every screen is reachable from at least one entry point
[REV-7c] Deep-link path (if any) is parsed correctly
[REV-7d] Back navigation doesn't return to auth/splash screens
[REV-7e] Role-based access: correct role sees correct screens
[REV-7f] Figma prototype reactions (from get_reactions) → all implemented as navigation?
```

### REV-8: DI Registration Check
```
[REV-8a] Every ViewModel used in UI is registered in Koin
[REV-8b] Every API used by ViewModel is registered in Koin
[REV-8c] Every repository used by API/VM is registered in Koin
[REV-8d] No runtime "No bean found" crashes
```

### REV-9: Build Verification
```
[REV-9a] Code compiles without errors
[REV-9b] No unresolved references
[REV-9c] No missing imports
[REV-9d] Run: .\gradlew.bat :composeApp:assembleDebug (or relevant target)
[REV-9e] Run: .\gradlew.bat :server:compileKotlin
[REV-9f] Run: .\gradlew.bat :shared:compileKotlinJvm
```

---

## Phase 5: MULTI-ITERATION VERIFICATION LOOP (Max 5)

> Run REV-1 through REV-9. If ANY check fails, fix and re-run.
> If all checks pass in a single iteration, the feature is COMPLETE.

```
ITERATION 1:
  → Run REV-1 (Figma fidelity) through REV-9 (build)
  → If ALL pass → FEATURE COMPLETE ✓
  → If ANY fail → record failures, fix, proceed to Iteration 2

ITERATION 2:
  → Re-run only the FAILED checks from Iteration 1
  → If ALL pass → FEATURE COMPLETE ✓
  → If ANY fail → record failures, fix, proceed to Iteration 3

ITERATION 3:
  → Re-run only the FAILED checks from Iteration 2
  → If ALL pass → FEATURE COMPLETE ✓
  → If ANY fail → record failures, fix, proceed to Iteration 4

ITERATION 4:
  → Re-run only the FAILED checks from Iteration 3
  → If ALL pass → FEATURE COMPLETE ✓
  → If ANY fail → record failures, fix, proceed to Iteration 5

ITERATION 5 (FINAL):
  → Re-run only the FAILED checks from Iteration 4
  → If ALL pass → FEATURE COMPLETE ✓
  → If ANY fail → STOP, report specific failures to user with escalation notes

*** AFTER 5 ITERATIONS EXHAUSTED ***
  → Do NOT attempt further fixes
  → Produce a detailed report of what's still broken
  → List exact files and line numbers of failures
  → Suggest manual next steps for the user
```

---

## Phase 6: CONVERGENCE REPORT

> The deliverable. This is what the user and next session read.

```markdown
## Figwright Feature Delivery Report: [Feature Name]

### Figma Source
- File: {fileName}
- Page: {pageName}
- Screen nodeIds: {list}
- Screenshots: docs/figma-exports/{files}

### Changes Implemented
| # | Change | Layers | Status |
|---|--------|--------|--------|
| 1 | {description} | DB, Backend, API, VM, UI | ✅ Complete |
| 2 | {description} | UI only | ✅ Complete |
| 3 | {description} | DB, Backend | ✅ Complete |

### Reverse Verification Results
| Check | Iteration 1 | Iteration 2 | Final |
|-------|-------------|-------------|-------|
| REV-1: Figma fidelity | ❌ color mismatch | ✅ fixed | ✅ PASS |
| REV-2: Button visibility | ✅ PASS | — | ✅ PASS |
| REV-3: Button wiring | ❌ dead button | ❌ missing API | ✅ PASS |
| REV-4: Workflow completeness | ✅ PASS | — | ✅ PASS |
| REV-5: State management | ✅ PASS | — | ✅ PASS |
| REV-6: Data flow integrity | ✅ PASS | — | ✅ PASS |
| REV-7: Navigation integrity | ✅ PASS | — | ✅ PASS |
| REV-8: DI registration | ✅ PASS | — | ✅ PASS |
| REV-9: Build verification | ❌ compile error | ✅ fixed | ✅ PASS |

### Button Inventory Verification
| Screen | Button | Visible? | Tappable? | Wired? | Scroll Behavior | Status |
|--------|--------|----------|-----------|--------|-----------------|--------|
| ListScreen | "Add New" FAB | ✅ | ✅ (56dp) | ✅ vm.addNew() | Fixed | ✅ |
| ListScreen | "Delete" | ✅ | ✅ (48dp) | ✅ vm.delete(id) | Scrolls | ✅ |
| FormScreen | "Save" | ✅ | ✅ (48dp) | ✅ vm.save() | Fixed footer | ✅ |
| FormScreen | "Cancel" | ✅ | ✅ (48dp) | ✅ popBackStack() | Fixed footer | ✅ |
| FormScreen | "Back" | ✅ | ✅ (48dp) | ✅ popBackStack() | Fixed header | ✅ |

### Scroll Pattern Verification
| Screen | Pattern | Header Fixed? | Footer Fixed? | Middle Scrolls? | Last Item Visible? | IME Safe? |
|--------|---------|---------------|---------------|-----------------|-------------------|-----------|
| ListScreen | A (Header+Lazy+Footer) | ✅ | ✅ | ✅ weight(1f) | ✅ contentPadding | ✅ imePadding |
| FormScreen | A (Header+Scroll+Footer) | ✅ | ✅ | ✅ weight(1f) | ✅ Spacer | ✅ imePadding |

### Build Status
| Target | Status |
|--------|--------|
| :server:compileKotlin | ✅ |
| :shared:compileKotlinJvm | ✅ |
| :composeApp:compileDevDebugKotlinAndroid | ✅ |

### Escalation Notes (if any)
For any ❌ that wasn't resolved in 5 iterations:
- Issue: {description}
- Files: {list}
- Failure reasons per iteration: {list}
- Suggested next steps: {what to try}

### Feature Status: ✅ COMPLETE / ❌ INCOMPLETE
```

---

## EXECUTION RULES (NON-NEGOTIABLE)

1. **READ-ONLY Figma** — NEVER call Figma write tools. Only read/extract/export.
2. **Read AGENT.md first** — this workflow extends it, doesn't replace it.
3. **Preserve existing UI/UX** — unless the Figma design explicitly changes it.
4. **Figma is the source of truth for UI** — match colors, typography, spacing, layout EXACTLY.
5. **No dead buttons** — every button must have onClick → VM → API → backend → DB.
6. **No orphan endpoints** — every backend route must have a UI button that triggers it.
7. **No invisible buttons** — every button must be visible, tappable, within safe area, not shadowed.
8. **Partial scroll is a first-class concern** — match the Figma's scroll pattern exactly.
9. **weight(1f) is mandatory** for scroll sections between fixed header/footer.
10. **Every form has Submit + Cancel/Back** — no one-way workflows.
11. **Every destructive action has a confirmation dialog** — no accidental deletes.
12. **Every screen has loading + error + empty + success states** — no blank screens.
13. **Reverse verification is mandatory** — forward pass alone is NEVER enough.
14. **5 iterations max** — after 5, escalate with detailed report.
15. **Convergence report is the deliverable** — it's what the next session reads.
