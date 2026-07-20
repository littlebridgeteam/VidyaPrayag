# Bug Audit & Fix Loop — Individual Per-Bug Trace System

> **Usage**: Paste this prompt into a conversation. Replace `{BUG_CSV}` with the path to the bug CSV file (e.g., `docs/bug_reports_v1.0.0.csv`). Replace `{BUG_NUMBERS}` with the specific bug numbers to audit (e.g., `12, 13, 14, 15, 16`).

---

## Task

You are a senior debugging assistant for the VidyaPrayag / EnrollPlus project (Kotlin Compose Multiplatform app + Ktor backend). You are auditing and fixing bugs from the bug report CSV using a structured loop system.

**Core Rules:**
1. **4-5 bugs per batch** — no more, no less
2. **Each bug audited individually** — one bug at a time, never bulk
3. **3-4 audit rounds per bug** — each round is a separate pass
4. **Bidirectional trace** — Frontend → ViewModel → Backend API, then reverse: Backend API → ViewModel → Frontend
5. **Button visibility check** — every button must be visible within screen, no padding/height issues
6. **After audit report** — for each finding, run 3-4 separate fix loops individually

---

## Phase 1: Bug Selection

### Step 1: Read Bug CSV
1. Read `{BUG_CSV}` file
2. Filter to only `{BUG_NUMBERS}` bugs
3. For each selected bug, extract: BugNumber, Title, Component, Severity, BugType, StepsToReproduce, ActualResult, ExpectedResult, Status, Role

### Step 2: Build Bug Summary Table

| Bug # | Title | Severity | BugType | Component | Status |
|---|---|---|---|---|---|

Output this table before starting any audit.

---

## Phase 2: Per-Bug Audit (3-4 Rounds, Individually)

**CRITICAL: Process ONE bug at a time. Complete all rounds for Bug N before starting Bug N+1. Never process multiple bugs in a single round.**

For EACH bug (1 through 4-5), run these rounds sequentially:

### Round 1: Forward Trace — Frontend → ViewModel → Backend API

**Goal**: Trace from the UI button/screen down to the server route.

#### 1A. Frontend (UI Screen)
1. **Find the screen file**: Search `composeApp/src/commonMain/kotlin/com/littlebridge/enrollplus/ui/v2/screens/` for the screen mentioned in the bug's Component field
2. **Read the screen file**: Full read, identify the relevant composable section
3. **Button visibility check**:
   - [ ] Button/entry point EXISTS in the composable
   - [ ] Button is VISIBLE (not hidden behind conditional, not in overflow menu, not gated by a flag that's always false)
   - [ ] Button is TAPPABLE (has `onClick` callback wired)
   - [ ] Button fits within screen bounds — no `width()` that exceeds screen, no `padding` that pushes it off-screen
   - [ ] No `height()` constraint that clips the button
   - [ ] No parent `Box` with `fillMaxSize()` that overlays the button
   - [ ] Button label is readable (not empty, not truncated to nothing)
4. **Layout/padding check**:
   - [ ] No horizontal overflow — `Row` children use `weight()` or `wrapContentWidth()`
   - [ ] No vertical overflow — content uses `verticalScroll` or `LazyColumn`
   - [ ] No fixed `height()` on growing content
   - [ ] Padding sums don't exceed screen width (horizontal padding + content width ≤ screen width)
   - [ ] `VCard` doesn't double-pad (VCard auto-applies `VTheme.dimens.md`)
   - [ ] `VButton` uses `full = true` not `Modifier.fillMaxWidth()` for full-width
5. **Record findings**: List any issues found with file path and line numbers

#### 1B. ViewModel
1. **Find the ViewModel**: From the screen's `koinViewModel()` call, trace to the VM class in `shared/src/commonMain/kotlin/com/littlebridge/enrollplus/feature/`
2. **Read the ViewModel file**: Full read
3. **State check**:
   - [ ] State data class has `isLoading`, `error`, and content fields
   - [ ] `isLoading` is set to `true` before API call and `false` after (in `finally` block or after both success and error paths)
   - [ ] Error state is set on API failure (not silently swallowed)
   - [ ] No stuck loading — if API fails, `isLoading` resets to `false`
4. **Function check**:
   - [ ] The function called by the screen's button exists in the VM
   - [ ] VM function calls repository method (not API directly)
   - [ ] try-catch wraps the API call
5. **Record findings**: List any issues with file path and line numbers

#### 1C. Backend API
1. **Find the server route**: Search `server/src/main/kotlin/` for the endpoint path
2. **Read the route file**: Full read of the relevant routing file
3. **Route check**:
   - [ ] Route exists and is registered in `Application.kt` or routing file
   - [ ] Route is behind JWT auth + role check
   - [ ] Route handler does real work (not TODO)
   - [ ] Input validation present (required fields, format checks)
   - [ ] Response DTO matches what the client expects
4. **Record findings**: List any issues with file path and line numbers

#### Round 1 Output
```
BUG #{number} — Round 1: Forward Trace
  Frontend:
    Screen: {file path}
    Button visible: ✅/❌ {details}
    Layout/padding: ✅/❌ {details}
    Findings: {list}
  ViewModel:
    VM: {file path}
    State management: ✅/❌ {details}
    Function wiring: ✅/❌ {details}
    Findings: {list}
  Backend API:
    Route: {file path}
    Route registered: ✅/❌
    Auth guard: ✅/❌
    Validation: ✅/❌
    Findings: {list}
```

---

### Round 2: Reverse Trace — Backend API → ViewModel → Frontend

**Goal**: Trace from the server response back up to the UI render. Catch deserialization mismatches, missing observers, stale state.

#### 2A. Backend Response
1. **Read the server route's response**: What JSON structure does the server return?
2. **DTO check**:
   - [ ] Server response DTO field names match shared model `@SerialName` values
   - [ ] Field types match (String vs Int, nullable vs non-null)
   - [ ] No extra/missing fields that would cause deserialization errors
3. **Record findings**

#### 2B. Shared Layer (Models → API → Repository)
1. **Find the shared model**: In `shared/.../feature/{feature}/domain/model/`
2. **Find the API client**: In `shared/.../feature/{feature}/data/remote/`
3. **Find the repository**: In `shared/.../feature/{feature}/data/repository/`
4. **Chain check**:
   - [ ] API client method exists with correct endpoint path + HTTP method
   - [ ] API uses `safeApiCall` for error handling
   - [ ] Repository interface declares the method
   - [ ] Repository impl delegates to API client
   - [ ] Koin registers API + Repository
5. **Record findings**

#### 2C. ViewModel → UI Observer
1. **VM StateFlow check**:
   - [ ] VM exposes `StateFlow<UiState>` (or similar)
   - [ ] VM updates state after repo call returns
   - [ ] State update includes the data from the API response
2. **UI observer check**:
   - [ ] Screen collects/observes the VM's StateFlow
   - [ ] UI re-renders on state change
   - [ ] Loading state shows skeleton/spinner
   - [ ] Error state shows error message + retry
   - [ ] Empty state shows actionable empty state
   - [ ] Content state renders actual data from VM
3. **Record findings**

#### Round 2 Output
```
BUG #{number} — Round 2: Reverse Trace
  Backend Response:
    DTO match: ✅/❌ {details}
    Findings: {list}
  Shared Layer:
    Model: {file path}
    API: {file path}
    Repository: {file path}
    Chain intact: ✅/❌ {details}
    Findings: {list}
  VM → UI:
    StateFlow observed: ✅/❌
    4 states handled: ✅/❌
    Findings: {list}
```

---

### Round 3: Edge Cases & State Sync

**Goal**: Check for edge cases, state sync issues, and race conditions.

1. **State sync after mutation**:
   - [ ] After a create/update/delete, does the list/data refresh?
   - [ ] Is `refresh()` called (silent, no skeleton) or `load()` (with skeleton)?
   - [ ] Does the refresh key increment actually trigger VM reload?
2. **Error recovery**:
   - [ ] If API returns 4xx, UI shows error (not crash)
   - [ ] If API returns 5xx, UI shows error (not crash)
   - [ ] If network timeout, UI shows error (not infinite loading)
   - [ ] `isLoading` resets on ALL error paths
3. **Concurrent access**:
   - [ ] Rapid double-tap on button — does it fire twice? (should debounce or disable during loading)
   - [ ] Screen rotation / recomposition — state preserved?
4. **Button visibility edge cases**:
   - [ ] Button visible on narrow screen (320dp)?
   - [ ] Button visible when keyboard is open?
   - [ ] Button visible when content is long (scrolled to bottom)?
   - [ ] Button not clipped by parent `Box` or `Card` with fixed height?
5. **Record findings**

#### Round 3 Output
```
BUG #{number} — Round 3: Edge Cases & State Sync
  State sync: ✅/❌ {details}
  Error recovery: ✅/❌ {details}
  Concurrent access: ✅/❌ {details}
  Button edge cases: ✅/❌ {details}
  Findings: {list}
```

---

### Round 4: Cross-Component & Consistency (Optional, for P1/P0 bugs only)

**Goal**: Check if the bug pattern exists elsewhere in the codebase.

1. **Grep for similar patterns**: If the bug is "tab overlap", grep for all `VTopTabs` usage. If "missing header", grep for all overlay screens without `VBackHeader`.
2. **Consistency check**:
   - [ ] Same component used consistently across screens?
   - [ ] Same validation pattern applied to similar fields?
   - [ ] Same loading/error pattern used?
3. **Record findings**: List all other screens/components that may have the same bug

#### Round 4 Output
```
BUG #{number} — Round 4: Cross-Component
  Similar patterns found: {list of files}
  Consistency issues: {list}
  Findings: {list}
```

---

## Phase 3: Audit Report (After All Bugs Audited)

After completing all rounds for ALL 4-5 bugs, produce a consolidated audit report.

### Findings Summary Table

| Bug # | Round | Direction | Layer | Finding | Severity | File(s) | Line(s) |
|---|---|---|---|---|---|---|---|

### Per-Bug Verdict

| Bug # | Title | Rounds Run | Total Findings | Status |
|---|---|---|---|---|
| {n} | {title} | {3 or 4} | {count} | ✅ CONFIRMED / ❌ NOT REPRODUCIBLE / ⚠️ PARTIAL |

### Button Visibility Report

| Bug # | Screen | Button | Visible | Tappable | Fits Screen | Padding OK | Height OK |
|---|---|---|---|---|---|---|---|

---

## Phase 4: Per-Finding Fix Loops (3-4 Loops, Individually)

**CRITICAL: Process ONE finding at a time. Complete all loops for Finding N before starting Finding N+1. Never bulk-fix multiple findings in a single loop.**

For EACH finding from the audit report, run 3-4 fix loops sequentially:

### Loop 1: Root Cause Fix

1. **Read the target file(s)** identified in the finding
2. **Identify root cause**: Is this a frontend issue, ViewModel issue, backend issue, or cross-layer?
3. **Apply fix** using `edit` or `multi_edit`
4. **Verify**:
   - Read modified file back
   - Check syntax (imports at top, no broken brackets)
   - Grep for expected pattern
5. **Record**: 
   ```
   FINDING #{n} — Loop 1: Root Cause Fix
     Root cause: {description}
     Fix applied: {description}
     File(s) modified: {list}
     Verification: ✅ PASS / ❌ FAIL {reason}
   ```

**Pass** → ✅ FIXED (1 loop) → next finding
**Fail** → Loop 2

### Loop 2: Corrected Fix

1. **Analyze failure**: What went wrong in Loop 1?
2. **Re-read file** for fresh context (file may have changed)
3. **Apply corrected fix** (fix the mistake from Loop 1)
4. **Verify**
5. **Record**:
   ```
   FINDING #{n} — Loop 2: Corrected Fix
     Failure analysis: {what went wrong in L1}
     Corrected fix: {description}
     Verification: ✅ PASS / ❌ FAIL {reason}
   ```

**Pass** → ✅ FIXED (2 loops) → next finding
**Fail** → Loop 3

### Loop 3: Alternative Approach

1. **Is the approach fundamentally wrong?** Try a different file/method/pattern
2. **Grep codebase** for similar working patterns — use as reference
3. **Apply alternative fix**
4. **Verify**
5. **Record**:
   ```
   FINDING #{n} — Loop 3: Alternative Approach
     Why previous approach failed: {reason}
     New approach: {description}
     Reference pattern: {file that works correctly}
     Verification: ✅ PASS / ❌ FAIL {reason}
   ```

**Pass** → ✅ FIXED (3 loops) → next finding
**Fail** → Loop 4

### Loop 4: Minimal Safe Fix + Escalate

1. **Scope reduce**: Fix only the most critical aspect of the finding
2. **Add TODO comment**: `// TODO: {full finding description} — needs complete fix`
3. **Apply minimal change**
4. **Verify**
5. **Record**:
   ```
   FINDING #{n} — Loop 4: Minimal Safe Fix
     Minimal fix: {description}
     TODO added: {yes/no}
     Verification: ✅ PASS / ❌ FAIL
   ```

**Pass** → ⚠️ PARTIAL (4 loops) → next finding
**Fail** → Escalate:
   ```
   ESCALATION: Finding #{n}
   Loops: 4
   Failure chain:
     L1: {reason}
     L2: {reason}
     L3: {reason}
     L4: {reason}
   Root blocker: {analysis}
   Files: {list}
   Suggested approach: {what to try next}
   ```
   → Mark ❌ BLOCKED → next finding

---

## Phase 5: Convergence Report

After all findings have been processed through their fix loops, produce the final report.

### Fix Resolution Table

| Finding # | Bug # | Finding | Severity | Loops Used | Status | File(s) Modified |
|---|---|---|---|---|---|---|

### Statistics

- Total bugs audited: {n}
- Total findings: {n}
- Fixed (1 loop): {n} ({percent})
- Fixed (2-3 loops): {n} ({percent})
- Partial (4 loops): {n} ({percent})
- Blocked (4 loops, escalated): {n} ({percent})
- Total fix loops run: {n}
- Average loops per finding: {n}

### Button Visibility Summary

| Screen | Button | Issue Found | Fix Applied | Status |
|---|---|---|---|---|

### Escalation Notes

{Full escalation notes for each ❌ BLOCKED finding}

### Next Session Handoff

{List of BLOCKED + PARTIAL findings with file paths and suggested approaches}

---

## Execution Rules

1. **ONE bug at a time in audit** — never process multiple bugs in a single round
2. **ONE finding at a time in fix** — never bulk-fix multiple findings in a single loop
3. **3-4 audit rounds per bug** — each round is a separate pass with a specific goal
4. **3-4 fix loops per finding** — each loop is a separate attempt with verification
5. **Bidirectional trace mandatory** — Round 1 goes down (UI→VM→API), Round 2 goes up (API→VM→UI)
6. **Button visibility is a first-class check** — not an afterthought, checked in every round
7. **No padding/height issues ignored** — every layout issue is a finding
8. **Record everything** — every round and loop outputs a structured record
9. **Audit report before fixes** — all bugs must be audited before any fixing starts
10. **Convergence report is the deliverable** — next session reads it

---

## Anti-Recurring Mistakes (Enforce on Every Fix)

### Full-Chain Wiring
Every fix must be wired across the entire stack. A UI-only fix is incomplete. After fixing, trace: Frontend → ViewModel → Backend API and reverse.

### Button Visibility
Every feature MUST have a visible, tappable button. Check:
- Not hidden behind a conditional that's always false
- Not clipped by parent `Box`/`Card` with fixed height
- Not pushed off-screen by excessive padding
- Not in an overflow menu that requires extra taps
- Visible on narrow screens (320dp)
- Visible when keyboard is open

### Layout & Padding
- `fillMaxWidth()` or `weight()` for flexible elements
- `maxLines = 1` + `TextOverflow.Ellipsis` for long text
- `wrapContentHeight()` for growing content, NOT fixed `height()`
- Row children: `Modifier.weight(1f)` on flexible items
- No double padding (VCard auto-applies `VTheme.dimens.md`)
- `VButton(full = true)` for full-width, NOT `Modifier.fillMaxWidth()`

### Token Discipline
Use ONLY existing tokens:
- **VTheme.colors**: `ink`, `ink2`, `ink3`, `surface`, `coral`, `coralSoft`, `cream`, etc.
- **VTheme.type**: `h2`, `h3`, `body`, `caption`, `label`
- **FORBIDDEN**: `textPrimary`, `textSecondary`, `textTertiary`, `titleLarge`, `titleMedium`, `bodyMedium`

### Component Discipline
- **VCard**: No double padding
- **VButton**: `full = true` for full-width
- **VButtonVariant**: `Primary`, `Secondary`, `Ghost`, `Destructive`
- **VButtonSize**: `Sm`, `Md`, `Lg`
- **VStateHost**: For loading/error/empty/content states
- **VBackHeader**: For overlay screens
- **VPullRefresh**: For list content

### State Management
- `isLoading = true` before API call, `false` in `finally` or after both success+error
- Error state set on failure (not silently swallowed)
- After mutation, call `refresh()` (silent) not `load()` (skeleton flash)
- Debounce or disable button during loading to prevent double-submit

### Import Discipline
- All imports at file top
- Never use fully-qualified names in function bodies
