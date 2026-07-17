---
description: Takes issues from feature-audit output and fixes them in a 5-attempt loop with dependency graph resolution, verification, and convergence tracking
---

# Feature Fix Workflow — 5-Attempt Loop with Dependency Graph

## When to Use
After running `/feature-audit` on a feature, feed the audit's Issue Table into this workflow. It systematically fixes every issue in dependency order, with up to 5 verification attempts per issue before escalating.

## Inputs
- **Issue Table** from the feature-audit output (the complete table with #, Round, Category, Severity, Issue, File(s), Fix)
- **Feature name** and **portal scope** from the audit

---

## Phase 1: Build the Fix Dependency Graph

Before fixing anything, sort issues into a dependency graph so that upstream fixes happen before downstream fixes.

### 1.1 Categorize by Layer
Assign each issue to its stack layer:

| Layer | Code | Examples |
|---|---|---|
| L0 — Database | DB | Missing tables, columns, migrations, indexes |
| L1 — Server | SRV | Missing routes, DTO mismatches, business logic, auth guards |
| L2 — Shared Models | MOD | Missing DTOs, @SerialName mismatches, wrong types |
| L3 — API Client | API | Missing API methods, wrong paths, missing safeApiCall |
| L4 — Repository | REP | Missing interface methods, missing impl, wrong delegation |
| L5 — ViewModel | VM | Missing VM, missing state, missing functions, Koin registration |
| L6 — UI Screen | UI | Missing screen, wrong tokens, missing states, overflow |
| L7 — Navigation | NAV | Missing overlay enum, missing rendering branch, missing callback |
| L8 — Entry Point | BTN | Missing source button, hidden button, no entry point |
| L9 — Optimization | OPT | Clutter reduction, plan optimization, dead code, consistency |

### 1.2 Build Dependency Edges
An issue in layer L depends on all issues in layers < L that affect the same feature sub-component.

Rules:
- L1 (Server route) depends on L0 (DB table) if the route queries that table
- L2 (Shared model) depends on L1 (Server DTO) — fields must match
- L3 (API client) depends on L2 (Shared model) — method signature uses model
- L4 (Repository) depends on L3 (API client) — delegates to API
- L5 (ViewModel) depends on L4 (Repository) — calls repo methods
- L6 (UI screen) depends on L5 (ViewModel) — observes StateFlow
- L7 (Navigation) depends on L6 (UI screen) — renders the screen
- L8 (Entry point) depends on L7 (Navigation) — triggers overlay
- L9 (Optimization) depends on ALL other layers being complete — optimize last

### 1.3 Topological Sort
Produce a fix order where no issue is fixed before its dependencies:

```
Fix Batch 1: All L0 issues (DB)
Fix Batch 2: All L1 issues (Server) — depends on L0
Fix Batch 3: All L2 issues (Shared Models) — depends on L1
Fix Batch 4: All L3 issues (API Client) — depends on L2
Fix Batch 5: All L4 issues (Repository) — depends on L3
Fix Batch 6: All L5 issues (ViewModel) — depends on L4
Fix Batch 7: All L6 issues (UI Screen) — depends on L5
Fix Batch 8: All L7 issues (Navigation) — depends on L6
Fix Batch 9: All L8 issues (Entry Point) — depends on L7
Fix Batch 10: All L9 issues (Optimization) — depends on ALL
```

Within each batch, fix Critical → High → Medium → Low.

### 1.4 Output: Fix Execution Plan
```
BATCH 1 (DB):
  1.1 [Critical] Create FeeStructuresTable in Tables.kt
  1.2 [Critical] Create SalaryRecordsTable in Tables.kt
  1.3 [High] Add migration SQL file

BATCH 2 (SERVER):
  2.1 [Critical] Add GET /api/v1/school/fees/structures route
  2.2 [Critical] Add POST /api/v1/school/fees/structures route
  ...

BATCH 3 (MODELS):
  3.1 [Critical] Create FeeStructureDto in FeeSalaryModels.kt
  ...
```

---

## Phase 2: 5-Attempt Fix Loop

For EACH issue in the Fix Execution Plan, run the following loop:

### Attempt 1: Implement Fix
1. **Read the target file(s)** — understand current code structure
2. **Apply the fix** — use `edit` or `multi_edit` tool
3. **Verify syntax** — read the modified file back, check for obvious errors
4. **Check imports** — ensure all new imports are added at file top
5. **Run verification** — attempt to compile or grep for the expected pattern

**If verification passes** → Mark issue ✅ FIXED, move to next issue

**If verification fails** → Record failure reason, proceed to Attempt 2

### Attempt 2: Correct Fix
1. **Analyze failure** — what went wrong? Wrong file? Wrong edit location? Missing dependency?
2. **Re-read the file** — get fresh context
3. **Apply corrected fix** — different approach or corrected edit
4. **Verify** — same checks as Attempt 1

**If verification passes** → Mark issue ✅ FIXED (2 attempts), move to next issue

**If verification fails** → Record failure reason, proceed to Attempt 3

### Attempt 3: Alternative Approach
1. **Re-analyze** — is the fix approach fundamentally wrong? Is there a different file or method?
2. **Search codebase** — grep for similar patterns that work correctly, use as reference
3. **Apply alternative fix** — different strategy entirely (e.g., different component, different pattern)
4. **Verify** — same checks

**If verification passes** → Mark issue ✅ FIXED (3 attempts), move to next issue

**If verification fails** → Record failure reason, proceed to Attempt 4

### Attempt 4: Minimal Safe Fix
1. **Scope reduce** — can we fix the most critical aspect only and defer the rest?
2. **Apply minimal fix** — smallest possible change that addresses the core issue
3. **Add TODO comment** — mark remaining work with `// TODO: {issue description} — needs full fix`
4. **Verify** — same checks

**If verification passes** → Mark issue ⚠️ PARTIAL (4 attempts), move to next issue

**If verification fails** → Record failure reason, proceed to Attempt 5

### Attempt 5: Escalation
1. **Stop fixing** — this issue cannot be resolved in the current session
2. **Document the blocker** — write a detailed escalation note:
   ```
   ESCALATION: Issue #{id} — {title}
   Attempts: 5
   Failure reasons:
     Attempt 1: {reason}
     Attempt 2: {reason}
     Attempt 3: {reason}
     Attempt 4: {reason}
     Attempt 5: {reason}
   Blocker: {what's preventing the fix}
   Suggested next steps: {what a human or different approach should try}
   Files involved: {list}
   Dependencies: {list any unfixed upstream issues that may be blocking this}
   ```
3. Mark issue ❌ BLOCKED
4. Move to next issue — do NOT stop the entire batch

---

## Phase 3: Batch Verification Gate

After completing each batch (all issues in one layer), run a **batch verification gate** before moving to the next batch:

### 3.1 Compile Check
- **Server**: `./gradlew :server:compileKotlin` — must pass
- **Shared**: `./gradlew :shared:compileKotlinJvm` — must pass
- **ComposeApp**: `./gradlew :composeApp:compileDevDebugKotlinAndroid` — must pass

If compile fails:
1. Read the error output
2. Fix the compile error (this gets its own mini 5-attempt loop)
3. Re-compile
4. Do NOT proceed to next batch until compile passes

### 3.2 Cross-Layer Consistency Check
Verify the batch's fixes are consistent with the layer above:
- After L1 (Server): Do route paths match what L2 (Models) will need?
- After L2 (Models): Do @SerialName values match server JSON keys?
- After L3 (API): Do API method signatures match L2 models?
- After L4 (Repo): Do repo methods match L3 API methods?
- After L5 (VM): Does VM call repo methods that exist?
- After L6 (UI): Does UI observe the correct StateFlow from VM?
- After L7 (Nav): Does overlay branch render the correct screen composable?
- After L8 (BTN): Does button callback trigger the correct overlay?

### 3.3 Gating Rule
**If batch verification fails → fix the failure before proceeding.** Do not carry broken state into the next batch.

---

## Phase 4: Full-Chain Integration Verification

After ALL batches are complete, run a full-chain integration test:

### 4.1 Chain Trace (per feature sub-component)
For each feature sub-component (e.g., "Fee Structure CRUD", "Salary Payment Tracking"):

```
[1] DB table exists? → ✅/❌
[2] Server route registered? → ✅/❌
[3] Server DTO matches shared model? → ✅/❌
[4] Shared model @Serializable? → ✅/❌
[5] API client method exists? → ✅/❌
[6] API uses safeApiCall? → ✅/❌
[7] Repo interface method exists? → ✅/❌
[8] Repo impl delegates to API? → ✅/❌
[9] Koin registers API + Repo? → ✅/❌
[10] VM state class exists? → ✅/❌
[11] VM function calls repo? → ✅/❌
[12] Koin registers VM? → ✅/❌
[13] UI screen composable exists? → ✅/❌
[14] UI uses correct tokens? → ✅/❌
[15] UI handles 4 states? → ✅/❌
[16] Overlay enum entry exists? → ✅/❌
[17] Overlay rendering branch exists? → ✅/❌
[18] Callback wired through portal chain? → ✅/❌
[19] Source button visible and tappable? → ✅/❌
[20] No overflow on any screen? → ✅/❌
```

Any ❌ = go back to the relevant batch and fix.

### 4.2 Build Verification
```
./gradlew :server:compileKotlin
./gradlew :shared:compileKotlinJvm
./gradlew :shared:jvmTest
./gradlew :composeApp:compileDevDebugKotlinAndroid
```
ALL must pass.

### 4.3 Dead Code Sweep
- Grep for any orphaned artifacts created during fixing that aren't referenced
- Remove unused imports added during fixes
- Remove any TODO comments from Attempt 4 that were resolved by later fixes

---

## Phase 5: Convergence Report

### Issue Resolution Summary
| # | Issue | Severity | Layer | Attempts | Status | Notes |
|---|---|---|---|---|---|---|
| 1 | {title} | Critical | L1 | 1 | ✅ FIXED | Clean fix |
| 2 | {title} | High | L6 | 3 | ✅ FIXED | Needed alternative approach |
| 3 | {title} | Medium | L7 | 5 | ❌ BLOCKED | Escalation note attached |
| ... | ... | ... | ... | ... | ... | ... |

### Statistics
- Total issues: {n}
- Fixed (1 attempt): {n} — {percent}
- Fixed (2-3 attempts): {n} — {percent}
- Partial (4 attempts): {n} — {percent}
- Blocked (5 attempts): {n} — {percent}
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
| Sub-Component | Chain Steps Passed | Chain Steps Failed | Status |
|---|---|---|---|
| {name} | 20/20 | 0 | ✅ |
| {name} | 18/20 | 2 | ⚠️ — see issues #{n}, #{n} |
| ... | ... | ... | ... |

### Escalation Notes
For each ❌ BLOCKED issue, include the full escalation note from Attempt 5.

### Next Session Handoff
If any issues are ❌ BLOCKED or ⚠️ PARTIAL:
- List them with file paths and suggested approaches
- Note any dependencies that were also blocked
- Provide the exact grep/read commands a next session should start with

---

## Execution Rules

1. **Never skip the dependency graph** — fixing L6 (UI) before L1 (Server) wastes time
2. **Never skip batch verification gates** — broken state compounds across layers
3. **5 attempts is a hard limit** — after 5, escalate and move on
4. **Each attempt must be different** — don't repeat the same fix that already failed
5. **Record every failure reason** — the escalation note is useless without failure details
6. **Compile after every batch** — don't let compile errors accumulate
7. **Full-chain trace is mandatory** — individual fixes passing ≠ feature working
8. **No partial credit** — a feature with 19/20 chain steps passed is still BROKEN
9. **Optimization (L9) is last** — never optimize before the feature works
10. **Dead code sweep is mandatory** — fixes create artifacts, clean them up
11. **TODO comments from Attempt 4 must be tracked** — they are technical debt
12. **The convergence report is the deliverable** — it's what the next session reads
