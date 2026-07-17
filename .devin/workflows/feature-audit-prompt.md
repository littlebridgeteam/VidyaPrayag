# Feature Audit Prompt — 3-Round Iterative Bug Discovery

> **Usage**: Paste this prompt into a conversation. Replace `{FEATURE}` with the feature name (e.g., "Fee Management", "Parent Homework", "Teacher Attendance", "Library", "Exams"). Replace `{PORTAL}` with Admin, Teacher, Parent, or All.

---

## Task

You are a senior debugging assistant for the VidyaPrayag / EnrollPlus project (Kotlin Compose Multiplatform app + Ktor backend). You are performing a comprehensive 3-round audit of the **{FEATURE}** feature in the **{PORTAL}** portal.

## Pre-Audit: Plan Discovery

Before starting any round, you MUST:

1. **Search for ALL plan/spec files** related to `{FEATURE}`:
   - `.windsurf/plans/*.md`
   - Repo root: `*PLAN*.md`, `*GODMODE*.md`, `*SPEC*.md`, `*AUDIT*.md`, `*PROMPT*.md`
   - `docs/**/*.md`
   - Grep for the feature keyword across all `.md` files in the repo
2. **Read every matching plan file** and extract:
   - Planned screens and their file paths
   - Planned API endpoints (path, method, request/response)
   - Planned database tables
   - Planned DTOs/models and their fields
   - Planned navigation/overlay entries
   - Planned buttons/entry points
   - Planned implementation order
   - Planned verification checklists
3. **Build a Plan Summary Table** listing every planned artifact and its expected file location

---

## Round 1: Full-Chain Wiring Audit (Structural)

**Goal**: Verify every link in the chain EXISTS and is correctly wired. Pure structural check — no functionality testing yet.

### 1.1 Server Layer
For each planned API endpoint in this feature:
- [ ] **Route exists**: Grep `server/src/main/kotlin/` for the route path
- [ ] **Route registered**: Check `Application.kt` or routing file for the `routing { ... }` block
- [ ] **DTO matches shared model**: Compare server DTO field names/types vs shared `*Models.kt` — field-for-field, `@SerialName`, nullability
- [ ] **Business logic present**: Handler does real work (not `call.respondText("TODO")`)
- [ ] **Auth guard**: Route is behind JWT + role check
- [ ] **Input validation**: Numeric coercion, string length, required fields

### 1.2 Shared Layer
For each planned model/API/repo:
- [ ] **Model DTO exists**: In correct `*Models.kt` file
- [ ] **@Serializable + @SerialName**: All DTOs serializable, serial names match server JSON
- [ ] **API client method**: In correct `*Api.kt`, correct endpoint path + HTTP method
- [ ] **safeApiCall**: Error handling present
- [ ] **Repository interface**: In domain layer
- [ ] **Repository impl**: In data layer, delegates to API
- [ ] **Koin registration**: API + Repo in commonModule

### 1.3 ViewModel Layer
For each planned ViewModel:
- [ ] **VM class exists**: In correct `presentation/` package
- [ ] **State data class**: With loading/error/empty/content states
- [ ] **StateFlow exposed**: As `StateFlow<UiState>`
- [ ] **Functions call repo**: Not API directly
- [ ] **Koin registration**: `factory { VM(get(), get()) }` in viewModelModule
- [ ] **Error handling**: try-catch, error state on failure
- [ ] **Loading state**: `isLoading = true` before, `false` after

### 1.4 UI Layer
For each planned screen:
- [ ] **Screen composable exists**: In correct `ui/v2/screens/{portal}/` directory
- [ ] **Correct tokens**: VColors (ink, ink2, ink3, surface, coral), VTypography (h2, h3, body, caption), VShapes, VTheme.dimens
- [ ] **NO wrong tokens**: Check for nonexistent tokens: `textPrimary`, `textSecondary`, `titleLarge`, `titleMedium`, `bodyMedium`
- [ ] **VCard**: No double padding (VCard auto-applies VTheme.dimens.md)
- [ ] **VButton**: `full = true` for full-width, NOT `Modifier.fillMaxWidth()`
- [ ] **VButtonVariant**: `Primary`, `Secondary`, `Ghost`, `Destructive` — NOT `Filled`, `Outlined`, `Text`
- [ ] **VButtonSize**: `Sm`, `Md`, `Lg` — NOT `Small`, `Medium`, `Large`
- [ ] **VStateHost**: Used for loading/error/empty/content
- [ ] **VBackHeader**: Used for overlay screens
- [ ] **VPullRefresh**: Wraps list content if applicable
- [ ] **4 states**: loading, error, empty, content — all handled
- [ ] **No hardcoded data**: All values from ViewModel state

### 1.5 Navigation Layer
For each planned overlay/navigation:
- [ ] **Overlay enum entry**: In correct enum (TeacherOverlay, ParentOverlay, SchoolOverlay, AdminOverlay)
- [ ] **Overlay rendering branch**: `when` branch in portal scaffold
- [ ] **Callback wiring**: Portal → intermediate → target screen
- [ ] **Source button**: VISIBLE, TAPPABLE button on source screen
- [ ] **Button NOT hidden**: Not in overflow, not buried, not behind gesture
- [ ] **Deep link**: If planned, routing handles the path
- [ ] **Back navigation**: Goes to correct parent, no dead-ends

### 1.6 Plan-vs-Implementation Diff Table

| Planned Artifact | Plan Section | Expected File | Actual File | Status |
|---|---|---|---|---|
| (screen/route/model/etc.) | §X.Y | path | path or "NOT FOUND" | ✅/❌/⚠️ |

Flag every ❌ and ⚠️ as a bug.

---

## Round 2: Functional & UX Audit (Behavioral)

**Goal**: Verify what exists actually WORKS end-to-end. Each feature must function, not just render.

### 2.1 End-to-End Flow Verification
For each user-facing feature:
1. Trace: tap button → callback → VM function → repo method → API request → server route → DB → response → deserialize → repo returns → VM updates StateFlow → UI observes → re-renders
2. Identify broken steps:
   - Button exists, callback not wired → **MISSING CALLBACK**
   - Callback wired, VM function missing → **MISSING VM FUNCTION**
   - VM function exists, repo missing → **MISSING REPO METHOD**
   - Repo exists, API missing → **MISSING API METHOD**
   - API exists, route missing → **MISSING SERVER ROUTE**
   - Route exists, DTO mismatch → **DTO MISMATCH**
   - All exist, UI doesn't observe → **MISSING UI OBSERVER**
3. Error path: API 404/500/network fail → does UI show error or crash?

### 2.2 Frontend Button/Entry Point Audit
For each feature:
- [ ] Visible tappable button exists
- [ ] Button is accessible (not hidden/buried/gated)
- [ ] Button label is clear plain language
- [ ] Button triggers correct callback
- [ ] If NO button: **"MISSING ENTRY POINT — user cannot access this feature"**
  - Specify: screen, position, label, VButton variant/size

### 2.3 On-Screen Bounds Audit
For each screen:
- [ ] No horizontal overflow
- [ ] No vertical overflow (scroll/LazyColumn used)
- [ ] No fixed heights on growing content
- [ ] Row weight distribution for multi-child rows
- [ ] Text truncation (maxLines + Ellipsis)
- [ ] Padding sums ≤ screen width
- [ ] Button labels fit
- [ ] Card content fits
- If ANY overflow: **"OVERFLOW DETECTED — {element} exceeds screen bounds"**

### 2.4 State Management Audit
For each screen:
- [ ] Loading: `TeacherSpinner()` or `VStateHost(loading=true)` — NOT text-only
- [ ] Error: message + retry, `VColors.coral` for error text
- [ ] Empty: actionable empty state (not just "No data")
- [ ] Content: renders actual data from VM
- [ ] No stuck loading: error state set on failure
- [ ] Refresh: pull-to-refresh or button triggers reload

### 2.5 API Response Correctness
For each API call:
- [ ] Endpoint path matches server route
- [ ] HTTP method matches
- [ ] Request body matches server expectation
- [ ] Response body matches client deserialization
- [ ] Auth token passed in header
- [ ] Query params correct
- [ ] Pagination handled if server paginates

---

## Round 3: Plan Optimization & Clutter Reduction (Strategic)

**Goal**: Compare plan vs implementation. Suggest optimizations. The plan itself may be over-engineered.

### 3.1 Feature Completeness vs Plan
- [ ] All planned features implemented? List missing ones
- [ ] Extra features beyond plan? List and assess if needed
- [ ] Plan assumptions still valid given codebase evolution?

### 3.2 UI Clutter & Simplicity Audit
- [ ] Too many tabs/sub-tabs? Can they be consolidated?
- [ ] Too many cards? Can related cards merge?
- [ ] Redundant information shown in multiple places?
- [ ] Too many buttons? Can secondary actions go in expandable?
- [ ] Information hierarchy: most important at top?
- [ ] Cognitive load: can decisions be reduced?
- [ ] Tap depth: can common actions be 1-2 taps?

### 3.3 Plan Optimization Suggestions
- [ ] Can screens be merged?
- [ ] Can API calls be batched?
- [ ] Can DTOs be simplified (flatten nested, remove unused)?
- [ ] Can navigation be flattened?
- [ ] Can state management be consolidated?
- [ ] Is plan over-specified? Under-specified?

### 3.4 Consistency Audit
- [ ] Token consistency across all screens in feature
- [ ] Component consistency (same patterns use same components)
- [ ] Navigation consistency (back behavior)
- [ ] Error handling consistency
- [ ] Empty state consistency
- [ ] Loading consistency

### 3.5 Dead Code & Unused Artifacts
- [ ] Unused DTOs (defined, never referenced)
- [ ] Unused API methods (no repo calls them)
- [ ] Unused repo methods (no VM calls them)
- [ ] Unused VM functions (no UI calls them)
- [ ] Unused overlay entries (no button triggers them)
- [ ] Orphan screens (no overlay renders them)

---

## Output Format

### Executive Summary
- Feature: {FEATURE}
- Portal: {PORTAL}
- Plan files matched: {list with paths}
- Total issues: {count}
- Critical: {n} | High: {n} | Medium: {n} | Low: {n}
- Status: ✅ PASS / ⚠️ PARTIAL / ❌ FAIL

### Plan Summary Table
| Planned Artifact | Plan Section | Expected Location | Status |
|---|---|---|---|

### Round 1 Results: Full-Chain Wiring
For each chain link per feature/sub-feature:
| Chain Link | Status | File | Notes |
|---|---|---|---|
| Server route | ✅/❌ | path | notes |
| Shared models | ✅/❌ | path | notes |
| API client | ✅/❌ | path | notes |
| Repository | ✅/❌ | path | notes |
| ViewModel | ✅/❌ | path | notes |
| UI screen | ✅/❌ | path | notes |
| Overlay enum | ✅/❌ | path | notes |
| Overlay rendering | ✅/❌ | path | notes |
| Callback wiring | ✅/❌ | path | notes |
| Source button | ✅/❌ | path | notes |

### Round 2 Results: Functional & UX
For each user flow:
- Flow: {tap X} → {API call} → {response} → {UI update}
- Broken step: {which step}
- Fix: {what to change}

For each screen:
- Overflow: YES/NO (details if YES)
- States: loading ✅/❌, error ✅/❌, empty ✅/❌, content ✅/❌
- Entry point: YES/NO (where to add if NO)

### Round 3 Results: Plan Optimization
- Optimization suggestions (priority ordered)
- Clutter reduction opportunities
- Consistency issues
- Dead code found

### Complete Issue Table
| # | Round | Category | Severity | Issue | File(s) | Fix |
|---|---|---|---|---|---|---|

### Fix Priority Order
1. Critical wiring (R1 ❌)
2. Missing entry points (R2)
3. Broken API flows (R2)
4. Overflow/layout (R2)
5. State management (R2)
6. Plan optimizations (R3)
7. Dead code (R3)

---

## Anti-Recurring Mistakes (ENFORCE ON EVERY FIX)

### 1. Full-Chain Wiring
Every fix must be wired across the ENTIRE stack. A UI-only fix is incomplete. Verify and list ALL 10 chain links for every fix.

### 2. Frontend Button Availability
Every feature MUST have a visible, tappable button. A backend endpoint with no UI entry point is an UNFIXED bug. Flag: **"MISSING ENTRY POINT"**

### 3. On-Screen Bounds
All elements MUST stay within screen bounds. Check: fixed widths, long text, Row overflow, padding sums. Flag: **"OVERFLOW DETECTED"**

### 4. End-to-End Functionality
Feature must not only render — it must FUNCTION. Tap → API → response → UI update. Any broken step = BROKEN feature.

### 5. Token Discipline
Use ONLY existing tokens. Forbidden: `textPrimary`, `textSecondary`, `textTertiary`, `titleLarge`, `titleMedium`, `bodyMedium`. Use: `VColors.ink/ink2/ink3`, `VTypography.h2/h3/body/caption/label`.

### 6. Component Discipline
- VCard: no double padding
- VButton: `full = true` not `fillMaxWidth()`
- VButtonVariant: `Primary/Secondary/Ghost/Destructive`
- VButtonSize: `Sm/Md/Lg`
- VStateHost for 4 states
- VBackHeader for overlays
- VPullRefresh for lists

### 7. No Hardcoded Data
Every displayed value flows from ViewModel → API → backend → database. Zero string literals for data.

### 8. Plan Optimization Is Mandatory
Round 3 is NOT optional. The plan itself may be the root cause of bugs (over-engineering, wrong assumptions, missing edge cases).
