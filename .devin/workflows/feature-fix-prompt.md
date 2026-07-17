# Feature Fix Prompt — 5-Attempt Loop with Dependency Graph

> **Usage**: Paste this prompt after running the feature audit. Replace `{FEATURE}` and `{PORTAL}`. Feed the audit's Issue Table into this prompt.

---

## Task

You are a senior fix engineer for the VidyaPrayag / EnrollPlus project (Kotlin Compose Multiplatform app + Ktor backend). You have received the output of a comprehensive feature audit for **{FEATURE}** in the **{PORTAL}** portal. Your job is to fix every issue found, using a structured 5-attempt loop with dependency graph resolution.

## Input

The audit produced an Issue Table with columns: #, Round, Category, Severity, Issue, File(s), Fix. Use that table as your work queue.

---

## Phase 1: Build the Fix Dependency Graph

### Step 1: Categorize Each Issue by Layer

| Layer | Code | What it covers |
|---|---|---|
| L0 — Database | DB | Tables, columns, migrations, indexes |
| L1 — Server | SRV | Routes, DTOs, business logic, auth, validation |
| L2 — Shared Models | MOD | DTO classes, @SerialName, @Serializable |
| L3 — API Client | API | API interface methods, endpoint paths, safeApiCall |
| L4 — Repository | REP | Interface + impl, delegation to API |
| L5 — ViewModel | VM | State class, StateFlow, functions, Koin registration |
| L6 — UI Screen | UI | Composable, tokens, VCard/VButton/VStateHost, 4 states |
| L7 — Navigation | NAV | Overlay enum, rendering branch, callback chain |
| L8 — Entry Point | BTN | Source button visible and tappable |
| L9 — Optimization | OPT | Clutter reduction, consistency, dead code |

### Step 2: Build Dependency Edges

- L1 depends on L0 (route needs table)
- L2 depends on L1 (model matches server DTO)
- L3 depends on L2 (API method uses model)
- L4 depends on L3 (repo delegates to API)
- L5 depends on L4 (VM calls repo)
- L6 depends on L5 (UI observes VM state)
- L7 depends on L6 (nav renders screen)
- L8 depends on L7 (button triggers overlay)
- L9 depends on ALL (optimize last)

### Step 3: Topological Sort

Produce fix batches in layer order. Within each batch, sort by severity: Critical → High → Medium → Low.

Output:
```
BATCH 1 (L0-DB):
  1.1 [Critical] {issue title} → {file} → {fix description}
  1.2 [High] {issue title} → {file} → {fix description}

BATCH 2 (L1-SRV):
  2.1 [Critical] {issue title} → {file} → {fix description}
  ...
```

---

## Phase 2: 5-Attempt Fix Loop (Per Issue)

For EACH issue in the execution plan, run this loop:

### Attempt 1: Direct Fix
1. Read the target file(s)
2. Apply the fix using `edit` or `multi_edit`
3. Verify: read modified file back, check syntax, check imports at top
4. Verify: grep for expected pattern or attempt compile

**Pass** → ✅ FIXED (1 attempt) → next issue
**Fail** → record reason → Attempt 2

### Attempt 2: Corrected Fix
1. Analyze what went wrong
2. Re-read file for fresh context
3. Apply corrected edit (fix the mistake from Attempt 1)
4. Verify

**Pass** → ✅ FIXED (2 attempts) → next issue
**Fail** → record reason → Attempt 3

### Attempt 3: Alternative Approach
1. Is the approach fundamentally wrong? Try a different file/method/pattern
2. Grep codebase for similar working patterns — use as reference
3. Apply alternative fix
4. Verify

**Pass** → ✅ FIXED (3 attempts) → next issue
**Fail** → record reason → Attempt 4

### Attempt 4: Minimal Safe Fix
1. Scope reduce — fix only the most critical aspect
2. Add `// TODO: {full issue description} — needs complete fix`
3. Apply minimal change
4. Verify

**Pass** → ⚠️ PARTIAL (4 attempts) → next issue
**Fail** → record reason → Attempt 5

### Attempt 5: Escalate
1. STOP — do not attempt further fixes on this issue
2. Write escalation note:
   ```
   ESCALATION: Issue #{id} — {title}
   Attempts: 5
   Failure chain:
     A1: {reason}
     A2: {reason}
     A3: {reason}
     A4: {reason}
     A5: {reason}
   Root blocker: {analysis}
   Files: {list}
   Upstream deps: {any unfixed issues blocking this}
   Suggested approach: {what to try next}
   ```
3. Mark ❌ BLOCKED → next issue

---

## Phase 3: Batch Verification Gate (After Each Batch)

After fixing ALL issues in one batch (layer), BEFORE moving to the next batch:

### 3.1 Compile
```
./gradlew :server:compileKotlin
./gradlew :shared:compileKotlinJvm
./gradlew :composeApp:compileDevDebugKotlinAndroid
```

If compile fails → fix compile error (mini 5-attempt loop) → re-compile → DO NOT proceed until green.

### 3.2 Cross-Layer Consistency
- After L1: route paths match what L2 models will need?
- After L2: @SerialName values match server JSON keys?
- After L3: API method signatures match L2 models?
- After L4: repo methods match L3 API methods?
- After L5: VM calls repo methods that actually exist?
- After L6: UI observes correct StateFlow from VM?
- After L7: overlay branch renders correct screen?
- After L8: button callback triggers correct overlay?

### 3.3 Gate Rule
**Batch must pass verification before next batch starts.** No carrying broken state forward.

---

## Phase 4: Full-Chain Integration Verification (After All Batches)

### 4.1 Per Sub-Component Chain Trace
For each feature sub-component, verify all 20 chain links:

```
[1]  DB table exists?           ✅/❌
[2]  Server route registered?    ✅/❌
[3]  Server DTO matches shared?  ✅/❌
[4]  Shared model @Serializable? ✅/❌
[5]  API client method exists?   ✅/❌
[6]  API uses safeApiCall?       ✅/❌
[7]  Repo interface exists?      ✅/❌
[8]  Repo impl delegates?        ✅/❌
[9]  Koin: API + Repo registered?✅/❌
[10] VM state class exists?      ✅/❌
[11] VM function calls repo?     ✅/❌
[12] Koin: VM registered?        ✅/❌
[13] UI screen exists?           ✅/❌
[14] UI uses correct tokens?     ✅/❌
[15] UI handles 4 states?        ✅/❌
[16] Overlay enum entry?         ✅/❌
[17] Overlay rendering branch?   ✅/❌
[18] Callback wired?             ✅/❌
[19] Source button visible?      ✅/❌
[20] No screen overflow?         ✅/❌
```

Any ❌ → go back to relevant batch → fix → re-verify

### 4.2 Full Build
```
./gradlew :server:compileKotlin
./gradlew :shared:compileKotlinJvm
./gradlew :shared:jvmTest
./gradlew :composeApp:compileDevDebugKotlinAndroid
```
ALL must pass.

### 4.3 Dead Code Sweep
- Grep for orphaned artifacts from fixes
- Remove unused imports
- Remove resolved TODO comments from Attempt 4

---

## Phase 5: Convergence Report

### Issue Resolution Table
| # | Issue | Severity | Layer | Attempts | Status | Notes |
|---|---|---|---|---|---|---|

### Statistics
- Total issues: {n}
- Fixed (1 attempt): {n} ({percent})
- Fixed (2-3 attempts): {n} ({percent})
- Partial (4 attempts): {n} ({percent})
- Blocked (5 attempts): {n} ({percent})
- Total fix attempts: {n}
- Average attempts per issue: {n}

### Build Status
| Target | Status |
|---|---|
| :server:compileKotlin | ✅/❌ |
| :shared:compileKotlinJvm | ✅/❌ |
| :shared:jvmTest | ✅/❌ |
| :composeApp:compileDevDebugKotlinAndroid | ✅/❌ |

### Full-Chain Verification
| Sub-Component | Passed | Failed | Status |
|---|---|---|---|

### Escalation Notes
{Full escalation notes for each ❌ BLOCKED issue}

### Next Session Handoff
{List of BLOCKED + PARTIAL issues with file paths and suggested approaches}

---

## Anti-Recurring Mistakes (Enforce on Every Fix)

### Full-Chain Wiring
Every fix must be wired across the entire stack. A UI-only fix is incomplete. After fixing, trace all 10 chain links.

### Token Discipline
Use ONLY existing tokens:
- **VColors**: `ink`, `ink2`, `ink3`, `surface`, `coral`, `coralSoft`, `cream`, etc.
- **VTypography**: `h2`, `h3`, `body`, `caption`, `label`
- **FORBIDDEN**: `textPrimary`, `textSecondary`, `textTertiary`, `titleLarge`, `titleMedium`, `bodyMedium`

### Component Discipline
- **VCard**: No double padding (auto-applies `VTheme.dimens.md`)
- **VButton**: `full = true` for full-width, NOT `Modifier.fillMaxWidth()`
- **VButtonVariant**: `Primary`, `Secondary`, `Ghost`, `Destructive`
- **VButtonSize**: `Sm`, `Md`, `Lg`
- **VStateHost**: For loading/error/empty/content states
- **VBackHeader**: For overlay screens
- **VPullRefresh**: For list content
- **TeacherSpinner()**: For loading in teacher screens, NOT text-only

### Content Padding
- Overlay screens: `.padding(horizontal = 20.dp).padding(top = 16.dp, bottom = 24.dp)`
- NOT `.padding(16.dp)`

### No Hardcoded Data
Every displayed value flows from ViewModel → API → backend → database. Zero string literals for data.

### Overflow Prevention
- `fillMaxWidth()` or `weight()` for flexible elements
- `maxLines = 1` + `TextOverflow.Ellipsis` for long text
- `wrapContentHeight()` for growing content, NOT fixed `height()`
- Row children: `Modifier.weight(1f)` on flexible items

### Koin Registration
After creating any new API, Repository, or ViewModel:
- API → `single { ApiClass(get()) }` in commonModule
- Repository → `single { RepoClass(get()) }` in commonModule
- ViewModel → `factory { VMClass(get(), get()) }` in viewModelModule

### Import Discipline
- All imports at file top
- Never use fully-qualified names in function bodies
- `LaunchedEffect` — import at top, don't inline-qualify

---

## Execution Rules

1. **Dependency graph first** — never fix L6 before L1
2. **5 attempts max per issue** — escalate, don't loop forever
3. **Each attempt must differ** — don't repeat a failed approach
4. **Record every failure** — escalation notes need failure chain
5. **Compile after every batch** — no broken state carries forward
6. **Full-chain trace is mandatory** — individual fixes ≠ working feature
7. **No partial credit** — 19/20 chain steps = BROKEN
8. **Optimization is last** — L9 after everything works
9. **Dead code sweep is mandatory** — clean up after fixes
10. **Convergence report is the deliverable** — next session reads it
