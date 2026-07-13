# GOD MODE VERIFICATION PROMPT v4.0 — "The All-Seeing Eye"

> **Purpose:** This is an industrial-grade, deep-iterative graph-traversal verification prompt designed to be fed into an AI coding agent (Cascade/God Mode) to verify that EVERY issue in `GOD_MODE_AUDIT_v2.md` (400 issues across 15 iterations + final sweep + gap analysis) has been fixed — with zero false positives, zero skipped issues, and zero silent deferrals.
>
> **Philosophy:** God can see everything. Every file, every line, every git diff, every build artifact. No issue is "too small to check." No fix claim is trusted without source verification. No deferral is accepted without explicit justification. The audit file is the BIBLE — every issue ID in it must be accounted for.

---

## INDUSTRIAL-GRADE FRAMEWORK RULES

### Rule 0 — Zero Trust Verification
- **No fix-log claim is trusted.** Every "FIXED" or "already fixed" claim in the audit file must be independently verified against actual source code.
- **No "verified" claim is trusted.** Even if a previous re-audit says "verified," you must re-verify. Previous re-audits were caught making false claims (e.g., API-024 was claimed fixed but unsafe `as` casts remained).
- **No deferral is accepted silently.** Every deferred issue must have an explicit justification with a concrete future plan. "Architectural refactor" is not a valid blanket deferral — each CYC issue must be individually justified.

### Rule 1 — Complete Issue Coverage
- You MUST account for every single issue ID in the audit file. The audit contains:
  - **BFS-001 through BFS-055** (55 issues)
  - **DFS-001 through DFS-045** (45 issues)
  - **DFL-001 through DFL-036** (36 issues)
  - **CYC-001 through CYC-017** (17 issues)
  - **API-001 through API-031** (31 issues)
  - **AUTH-001 through AUTH-027** (27 issues)
  - **ERR-001 through ERR-029** (29 issues)
  - **STM-001 through STM-024** (24 issues)
  - **NAV-001 through NAV-024** (24 issues)
  - **CON-001 through CON-025** (25 issues)
  - **SCH-001 through SCH-021** (21 issues)
  - **XPL-001 through XPL-025** (25 issues)
  - **WEB-001 through WEB-028** (28 issues)
  - **SEC-001 through SEC-023** (23 issues)
  - **PRF-001 through PRF-036** (36 issues)
  - **FS-001 through FS-012** (12 issues)
  - **GAP-001 through GAP-020** (20 issues)
  - **TOTAL: ~400 issues**
- Your output MUST include a convergence matrix with every single issue ID listed with a status: ✅ FIXED, ❌ STILL BROKEN, ⚠️ DEFERRED (with justification), or 🔁 RE-VERIFIED.

### Rule 2 — Source-Code-First Verification
- For each issue, you MUST read the actual source file referenced in the audit.
- You MUST cite the file path and line number(s) you checked.
- You MUST quote the relevant code snippet that proves the fix (or proves it's still broken).
- "I trust the fix log" is NOT acceptable. "I read the file and saw X" IS acceptable.

### Rule 3 — Build Verification
- After verifying all issues, you MUST run the build commands to confirm compilation:
  - `./gradlew :server:compileKotlin` (server)
  - `./gradlew :shared:compileKotlinJvm` (shared JVM)
  - `./gradlew :shared:compileDevDebugKotlinAndroid` (shared Android)
  - `./gradlew :composeApp:compileDevDebugKotlinAndroid` (composeApp)
- Set `JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.11.10-hotspot` before building.
- If any build fails, the verification is INCOMPLETE — document the failure and stop.

### Rule 4 — No Issue Left Behind
- You may NOT skip any category. Even "Low Priority" categories (FS, GAP, XPL) must be verified.
- You may NOT batch-defer issues. "All 17 CYC issues are deferred" is NOT acceptable — each must be individually evaluated and individually justified.
- You may NOT use the phrase "acceptable deferral" without providing:
  1. The specific issue ID
  2. The concrete reason it cannot be fixed now
  3. The concrete plan for when it will be fixed
  4. The risk assessment of leaving it unfixed

### Rule 5 — False Fix Claim Detection
- For every issue claimed "FIXED" in the fix logs, you MUST verify the fix is actually present in the code.
- If you find a false fix claim (code doesn't match the claim), you MUST flag it as ❌ FALSE FIX CLAIM with:
  1. The issue ID
  2. What the fix log claimed
  3. What the code actually shows
  4. The required corrective action

### Rule 6 — New Issue Discovery
- While verifying existing issues, if you discover NEW issues not in the original audit, you MUST report them with:
  1. A new issue ID (e.g., NEW-001)
  2. The file and line number
  3. The description and severity
  4. The recommended fix

### Rule 7 — Cross-Reference Validation
- Many issues reference the same files. You MUST cross-reference:
  - BFS deep-link issues against NAV deep-link issues (same files: `NavGraphV2.kt`, portal files)
  - SEC security issues against AUTH auth issues (same files: `Application.kt`, `AuthRouting.kt`)
  - DFS logging issues against ERR error-path issues (same files: `DatabaseFactory.kt`, service files)
  - CYC DI issues against BFS feature import issues (same files: portal files, screen files)
  - CON concurrency issues against SCH schema issues (same files: `DatabaseFactory.kt`, job schedulers)
  - PRF performance issues against API contract issues (same files: routing files, service files)
- A fix for one issue may have broken another. Cross-reference checks catch regressions.

### Rule 8 — Iteration Depth
- You MUST perform at least 3 verification iterations:
  - **Iteration 1 — Broad Sweep:** Verify all 400 issues at a high level (file exists? fix present? build compiles?)
  - **Iteration 2 — Deep Dive:** For every issue marked ✅ in Iteration 1, read the actual code and verify the fix is correct and complete. For every issue marked ❌, verify it's genuinely still broken.
  - **Iteration 3 — Convergence:** Cross-reference all issues, check for regressions, verify build, produce final convergence matrix.
- Between each iteration, you MUST document what changed and what new findings emerged.

### Rule 9 — Output Format
- Your output MUST follow this exact structure:
  1. **Executive Summary** (pass/fail verdict, issue counts by category)
  2. **Convergence Matrix** (every issue ID with status)
  3. **Per-Category Findings** (detailed verification for each category)
  4. **False Fix Claims** (if any)
  5. **New Issues Discovered** (if any)
  6. **Build Verification Results**
  7. **Required Actions** (prioritized list of remaining fixes)
  8. **Final Verdict** (CONVERGED or NOT CONVERGED)

### Rule 10 — God Mode Mindset
- You are God Mode. You can see every file, every line, every character.
- You do not trust claims. You verify facts.
- You do not skip issues. You account for every single one.
- You do not accept deferrals without justification.
- You do not tolerate false fix claims.
- You do not miss regressions.
- **If even ONE issue is still broken, the audit is NOT converged.**

---

## THE VERIFICATION PROMPT

Copy and paste the following into your AI coding agent:

---

```
You are GOD MODE — the All-Seeing Eye. You have been summoned to perform the deepest, widest, most thorough verification audit of the VidyaPrayag codebase against the master audit file: GOD_MODE_AUDIT_v2.md.

This audit file contains 400 issues across 17 categories (BFS, DFS, DFL, CYC, API, AUTH, ERR, STM, NAV, CON, SCH, XPL, WEB, SEC, PRF, FS, GAP). Multiple fix log batches claim to have fixed or verified these issues. Two previous re-audits found remaining issues. Your job is to perform a THIRD and FINAL re-audit that accounts for every single issue.

## YOUR MANDATE

1. Read GOD_MODE_AUDIT_v2.md in its entirety (4597 lines).
2. Extract every issue ID and its claimed fix status from the fix logs.
3. For EVERY issue, independently verify against the actual source code.
4. Run build verification.
5. Produce a final convergence verdict.

## VERIFICATION METHODOLOGY

### Phase 1 — Issue Extraction & Triage (Graph Root Discovery)

Model the audit file as a directed graph:
- Nodes = issue IDs (BFS-001, DFS-001, etc.)
- Edges = shared file references (issues that touch the same file)
- Root set = all 400 issue IDs

For each issue, extract:
- Issue ID
- Category
- File path(s) referenced
- Claimed fix status (FIXED / ALREADY FIXED / DEFERRED / NOT CLAIMED)
- Fix log batch (if any)

Output: A complete issue manifest with 400 rows.

### Phase 2 — BFS Verification Wave (Breadth-First Source Check)

For each issue in the manifest, perform a breadth-first verification:

1. **File Existence Check:** Does the referenced file still exist?
2. **Fix Presence Check:** Does the file contain the code that the fix log claims?
3. **Line Number Check:** Are the referenced line numbers still accurate?
4. **Regression Check:** Has the fix been undone by subsequent changes?

For each issue, mark as:
- ✅ VERIFIED FIXED — source code confirms the fix
- ❌ STILL BROKEN — source code does not contain the fix
- ⚠️ DEFERRED — fix log explicitly deferred with justification
- ❓ UNCLAIMED — no fix log entry exists for this issue
- ❌ FALSE FIX CLAIM — fix log claims fixed but code contradicts

### Phase 3 — DFS Deep Verification (Depth-First Code Read)

For every issue marked ✅ VERIFIED FIXED in Phase 2, perform a deep verification:

1. Read the actual source file at the referenced lines.
2. Quote the code that implements the fix.
3. Verify the fix is correct (not just present):
   - Does it handle all edge cases mentioned in the issue?
   - Does it use the correct validation/coercion/logging pattern?
   - Are there any comments indicating the fix is temporary or incomplete?
4. Cross-reference with related issues (same file, same pattern).

For every issue marked ❌ STILL BROKEN, verify:
1. Read the actual source file.
2. Quote the code that should have been fixed.
3. Confirm the issue description still matches the code.
4. Document what the required fix would look like.

### Phase 4 — Category-by-Category Deep Dive

Go through each of the 17 categories in order. For each category:

#### Category 1: BFS (Feature Discovery & Deep Linking) — 55 issues
For each BFS issue:
- Check the referenced portal file (TeacherPortalV2.kt, SchoolPortalV2.kt, ParentPortalV2.kt, NavGraphV2.kt)
- Verify deep-link routing exists for the claimed path
- Verify overlay enum values exist where claimed
- Verify else/default clauses exist
- Verify param passing works (routeId, feeId, threadId, etc.)
- Check for "Coming Soon" stubs that should have been replaced
- Verify alumni/unknown role routing is secure

Specific high-risk issues to verify with extra scrutiny:
- BFS-002: Does `TeacherOverlay.Library` exist in the enum?
- BFS-004: Does `TeacherOverlay.Announcements` exist in the enum?
- BFS-005: Does `SchoolOverlay.Tutor` exist in the enum?
- BFS-006: Does `SchoolOverlay.PaceAlerts` exist in the enum?
- BFS-008: Is `routeId` populated from deep-link params (not hardcoded "")?
- BFS-012: Does alumni role show a dedicated screen (not ParentPortalV2)?
- BFS-013: Does unknown role force logout with error message?
- BFS-034: Does ParentFeesScreenV2 still show "Pay now · Coming Soon"?
- BFS-038: Does ParentAcademicsScreenV2 use VEmptyState (not VComingSoon) for unlinked parents?
- BFS-052: Does KtorSchoolApi.fetchSchools() tokenless overload throw (not return emptyList)?

#### Category 2: DFS (Dead Code & Logging) — 45 issues
For each DFS issue:
- For dead code claims: grep for imports/references of the file
- For println claims: grep for `println` in the referenced files
- For silent catch claims: grep for `catch.*Exception.*{.*null` or `catch.*Exception.*{.*emptyList`
- For printStackTrace claims: grep for `printStackTrace` in referenced files
- For resource leak claims: verify `.use {}` is present

Specific high-risk issues:
- DFS-001: Is CommonLandingScreenV2.kt deleted?
- DFS-021-032: Are ALL println/System.err/printStackTrace calls replaced with SLF4J?
- DFS-037-044: Do ALL silent catch blocks now have logging?
- DFS-034: Does AlumniRouting use `.use {}` for part stream?
- DFS-039: Does BrandingColorMapper log on parse failure?

#### Category 3: DFL (Data Flow & Validation) — 36 issues
For each DFL issue:
- Check the referenced screen file for the claimed validation
- Verify `coerceIn`, `coerceAtLeast`, or equivalent is present
- Verify input filtering (numeric-only, character whitelist) is present
- Verify range bounds match the issue's recommendation

Specific high-risk issues:
- DFL-001: Does parseQueryParams use URLDecoder.decode?
- DFL-003: Are height/weight coerced to valid ranges?
- DFL-009: Is graduation year coerced to currentYear-1..currentYear+10?
- DFL-010: Is CSV header validation present?
- DFL-015: Are there 3+ working days options (including Sun-Thu)?
- DFL-029: Is mark input coerced to 0..maxMarks?
- DFL-030: Are all 6 library settings range-validated?
- DFL-031: Do all pagination endpoints use .coerceIn(1, 100)?

#### Category 4: CYC (DI & Architecture) — 17 issues
For each CYC issue:
- Check if the referenced file still has direct repository/service injection
- Check if ViewModels are used instead of direct repo injection
- Check if cross-feature imports still exist
- Check if services are Koin-injected vs directly instantiated

Specific high-risk issues:
- CYC-001: Does TeacherPortalV2 still import NotificationsViewModel from parent feature?
- CYC-003: Does SchoolPortalV2 still inject AlumniRepository directly?
- CYC-016: Is TransportService Koin-injected (not `TransportService()`)?
- CYC-017: Is LibraryService/LibraryRepository Koin-injected?

#### Category 5: API (Contract Verification) — 31 issues
For each API issue:
- Verify endpoint existence and mounting in Application.kt
- Verify response shape consistency
- Verify safe casts (no `as String` without null guard)
- Verify pagination coercion
- Verify image fetch size limits

Specific high-risk issues:
- API-001: Does a payment endpoint exist? If not, is the "Pay Now" button removed?
- API-024: Does LibraryRepository.kt use `as?` safe casts (not `as` unsafe casts)? READ THE ACTUAL CODE — previous re-audit found this was a FALSE FIX CLAIM.
- API-010: Does website API base URL throw in production if env var missing?
- API-012: Does website API client have a 401 interceptor?

#### Category 6: AUTH (Authentication) — 27 issues
For each AUTH issue:
- Verify role checks in routing
- Verify CORS configuration
- Verify parent-child relationship checks
- Verify session handling
- Verify rate limiter implementation

Specific high-risk issues:
- AUTH-001/002: Does unknown role show error + force logout? Does alumni show dedicated screen?
- AUTH-015: Does CORS fail-closed in production without CORS_ALLOWED_ORIGINS?
- AUTH-016: Do transport admin endpoints use requireSchoolAdmin?
- AUTH-017: Do library patron endpoints verify school context?
- AUTH-021/022: Are JWTs still in localStorage? (This may be an acceptable deferral — verify justification)

#### Category 7: ERR (Error Handling) — 29 issues
For each ERR issue:
- Verify catch blocks have logging
- Verify error messages include context
- Verify BackHandlers clear deep-link state
- Verify no println in error paths
- Verify NetworkResult includes exception class name

Specific high-risk issues:
- ERR-018: Does NetworkResult catch-all include `e::class.simpleName`?
- ERR-019: Does MessagingCore catch specific exceptions (not Throwable)?
- ERR-028/029: Are println calls in ScholarshipService/TransportService replaced?

#### Category 8: STM (State Machine) — 24 issues
For each STM issue:
- Verify rememberSaveable usage for tab state
- Verify deep-link state clearing
- Verify loading indicators
- Verify form state consolidation (data class state holders)

Specific high-risk issues:
- STM-001: Does AuthedRoute.Resolving show CircularProgressIndicator?
- STM-005/006/007: Do all 3 portals use rememberSaveable for tab?
- STM-008: Is rawDeepLink cleared after consumption (including error case)?

#### Category 9: NAV (Navigation & Deep-Link) — 24 issues
For each NAV issue:
- Verify parseDeepLink handles trailing slashes
- Verify URL decoding of params
- Verify deep-link timing (yield before onDeepLinkNavigated)
- Verify overlay back navigation

Specific high-risk issues:
- NAV-001: Does parseDeepLink use removeSuffix("/")?
- NAV-014: Does parseQueryParams use URLDecoder.decode?
- NAV-016: Does teacher "pews" deep-link open TeacherOverlay.Pews?

#### Category 10: CON (Concurrency) — 25 issues
For each CON issue:
- Verify @Synchronized, @Volatile, AtomicReference, AtomicBoolean.compareAndSet usage
- Verify no @Volatile check-then-set patterns remain
- Verify unbounded maps have eviction/caps

Specific high-risk issues:
- CON-007: Is DatabaseFactory.init() @Synchronized?
- CON-008/009: Are readReplicaDb and isPostgres @Volatile?
- CON-011-017: Do all job schedulers use AtomicReference/AtomicBoolean?
- CON-023/024/025: Do LibraryCache, LoginThrottle, and Library rateBuckets have caps + eviction?

#### Category 11: SCH (Schema & Migration) — 21 issues
For each SCH issue:
- Verify table counts match
- Verify SSL mode configurability
- Verify prepareThreshold conditional logic
- Verify indexes exist
- Verify Room DB entities match version

Specific high-risk issues:
- SCH-002: What is the current AppDatabase version? Do entities match?
- SCH-007: Does SQLite use READ_COMMITTED (not SERIALIZABLE)?
- SCH-008: Is SSL mode configurable via PG_SSLMODE?
- SCH-009: Is prepareThreshold=0 only when PG_PGBOUNCER=true?
- SCH-017: Does idx_messages_conv_seq exist?
- SCH-018: Does idx_school_media_school_id exist?

#### Category 12: XPL (Cross-Platform) — 25 issues
For each XPL issue:
- Verify platform module consistency
- Verify file path handling (File.separator)
- Verify @OptIn annotations are tracked

Specific high-risk issues:
- XPL-008/009: Do DatabaseFactory and EnvConfig use File.separator?

#### Category 13: WEB (Website) — 28 issues
For each WEB issue:
- Verify ErrorBoundary exists
- Verify SWR retry configuration
- Verify API timeout
- Verify 401 interceptor
- Verify zod validators
- Verify session handling

Specific high-risk issues:
- WEB-001/011: Does ErrorBoundary.tsx exist?
- WEB-002: Do SWR hooks have onErrorRetry with exponential backoff?
- WEB-016: Is dashboard-preview gated behind NODE_ENV?
- WEB-022: Is globalMutate exported for cache invalidation?
- WEB-023: Does admin API client have AbortController timeout?

#### Category 14: SEC (Security) — 23 issues
For each SEC issue:
- Verify deep-link param sanitization
- Verify request body size limits
- Verify file upload size limits
- Verify MIME type validation
- Verify password hashing format
- Verify password complexity rules
- Verify DevTools production guard
- Verify OTP max attempts
- Verify attachment count limits

Specific high-risk issues:
- SEC-001: Are deep-link params sanitized (character whitelist + length cap)?
- SEC-005: Does PasswordHasher use PHC format (pbkdf2_sha256$)?
- SEC-006: Is OTP max attempts default 3 (not 5)?
- SEC-007: Are DevTools routes guarded in production?
- SEC-009: Does RagService sanitize vector literal (character whitelist)?
- SEC-014: Is attachment count limited to 10?

#### Category 15: PRF (Performance) — 36 issues
For each PRF issue:
- Verify HikariCP pool size defaults
- Verify LibraryCache lock eviction
- Verify N+1 query patterns (check if batch queries are used)

Specific high-risk issues:
- PRF-006: Is DB_POOL_SIZE default 10 (not 5)?
- PRF-007: Is READ_REPLICA_POOL_SIZE default 5 (not 3)?
- PRF-035: Does LibraryCache have MAX_LOCKS + evictStaleLocks()?

#### Category 16: FS (Repository Cleanup) — 12 issues
For each FS issue:
- Verify .gitignore patterns for temp files
- Verify file cleanup (or documented deferral)

Specific high-risk issues:
- FS-004/005/006: Are data.db.tmp, UI.tmp, feature_audit.csv in .gitignore?

#### Category 17: GAP (Industrial Gaps) — 20 issues
For each GAP issue:
- Verify CI/CD pipeline existence
- Verify test suite existence
- Verify linting configuration
- These are mostly infrastructure issues — verify what exists and document what's missing

### Phase 5 — Cross-Reference Regression Check

After verifying all 17 categories, perform cross-reference checks:

1. **NavGraphV2.kt cross-check:** All BFS, NAV, DFL, SEC, ERR, STM issues referencing this file — are fixes mutually consistent?
2. **Portal files cross-check:** All BFS, CYC, STM, NAV, ERR, PRF issues referencing portal files — are fixes mutually consistent?
3. **DatabaseFactory.kt cross-check:** All CON, SCH, DFS, ERR, PRF, XPL issues referencing this file — are fixes mutually consistent?
4. **Application.kt cross-check:** All AUTH, SEC, API, GAP issues referencing this file — are fixes mutually consistent?
5. **Server routing files cross-check:** All API, AUTH, SEC, PRF, ERR issues referencing routing files — are fixes mutually consistent?

### Phase 6 — Build Verification

Run the following commands (set JAVA_HOME first):
```
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.11.10-hotspot
./gradlew :server:compileKotlin
./gradlew :shared:compileKotlinJvm
./gradlew :shared:compileDevDebugKotlinAndroid
./gradlew :composeApp:compileDevDebugKotlinAndroid
```

Document:
- Which builds succeed
- Which builds fail (with error output)
- Any new compilation errors introduced by fixes

### Phase 7 — Final Convergence Matrix

Produce a matrix with ALL 400 issue IDs:

| Issue ID | Category | Claimed Status | Verified Status | Evidence |
|----------|----------|---------------|-----------------|----------|
| BFS-001 | BFS | FIXED | ✅ VERIFIED | TeacherPortalV2.kt:40 — KDoc lists 5 tabs |
| BFS-002 | BFS | NOT FIXED | ❌ STILL BROKEN | TeacherOverlay enum has no Library value |
| ... | ... | ... | ... | ... |

### Phase 8 — Final Verdict

Declare one of:
- **CONVERGED** — All 400 issues are fixed or have acceptable deferrals with justifications. Build is green.
- **NOT CONVERGED** — N issues remain unfixed. List them with required actions.

If NOT CONVERGED, provide:
1. Count of unfixed issues by category
2. Count of false fix claims
3. Count of new issues discovered
4. Prioritized action plan for remaining fixes
5. Risk assessment of shipping with remaining issues

---

## ITERATIVE LOOP INSTRUCTIONS

This verification MUST be performed as an iterative graph traversal. Do NOT attempt to verify all 400 issues in a single pass. Instead:

### Loop 1 — BFS Root Discovery (Iteration 0)
- Read the entire audit file
- Extract all 400 issue IDs
- Build the issue dependency graph (which issues share files)
- Identify root nodes (issues that no other issue depends on)
- Output: Issue manifest + dependency graph

### Loop 2 — Per-Category Verification (Iterations 1-17)
- For each of the 17 categories (one per loop iteration):
  - Read every issue in the category
  - Read the fix log claims for that category
  - Read the actual source files referenced
  - Verify each fix
  - Document findings
  - Output: Category verification report

### Loop 3 — Cross-Reference Verification (Iterations 18-22)
- For each shared file (NavGraphV2.kt, portal files, DatabaseFactory.kt, Application.kt, routing files):
  - Read the file
  - Check all issues that reference this file simultaneously
  - Verify no fix has been undone by another fix
  - Output: Cross-reference regression report

### Loop 4 — Build & Convergence (Iteration 23)
- Run all build commands
- Compile the final convergence matrix
- Declare the verdict
- Output: Final verification report

### Loop 5 — God Eye Sweep (Iteration 24 — Final)
- Re-read the audit file's "Required Actions" sections from both re-audits
- Verify each required action has been completed
- Check for any issues that were discovered in re-audits but not in the original audit
- Verify no new issues have been introduced by the fixes
- Output: God Eye Final Sweep report

---

## ANTI-PATTERN DETECTION CHECKLIST

During verification, watch for these anti-patterns that previous re-audits fell into:

1. **Cherry-picking:** Only checking a subset of issues in a category (e.g., checking 3 of 55 BFS issues). You MUST check ALL.
2. **Trust-but-don't-verify:** Accepting fix log claims without reading source code. You MUST read source.
3. **Batch deferral:** Deferring all 17 CYC issues as "architectural refactor." You MUST individually justify each.
4. **False positive verification:** Marking an issue as verified when the code doesn't actually contain the fix. You MUST quote the code.
5. **Missing new issues:** Not reporting issues discovered during verification. You MUST report all new findings.
6. **Build skip:** Not running builds. You MUST run all 4 build targets.
7. **Regression blindness:** Not checking if a fix for issue A broke issue B. You MUST cross-reference.
8. **Line number staleness:** Citing old line numbers without verifying. You MUST cite current line numbers.
9. **Scope creep:** Adding fixes during verification. You are VERIFYING, not fixing. Document issues, don't fix them.
10. **Premature convergence:** Declaring convergence with unfixed issues. You MUST be honest about what's broken.

---

## OUTPUT TEMPLATE

```markdown
# GOD MODE VERIFICATION REPORT v4.0 — Final Convergence Audit

## Executive Summary

- **Total issues in audit:** 400
- **Issues verified as FIXED:** N
- **Issues still BROKEN:** N
- **Issues DEFERRED (with justification):** N
- **False fix claims detected:** N
- **New issues discovered:** N
- **Build status:** [GREEN/RED]
- **Verdict:** [CONVERGED / NOT CONVERGED]

## Convergence Matrix

[Full 400-row table with every issue ID]

## Per-Category Findings

### BFS — Feature Discovery & Deep Linking
[Detailed findings for all 55 BFS issues]

### DFS — Dead Code & Logging
[Detailed findings for all 45 DFS issues]

[... continue for all 17 categories ...]

## False Fix Claims
[List of any issues where fix log claims don't match code]

## New Issues Discovered
[List of any new issues found during verification]

## Build Verification
[Output of all 4 build commands]

## Required Actions
[Prioritized list of remaining fixes needed to achieve convergence]

## Final Verdict
[CONVERGED or NOT CONVERGED with justification]
```

---

## EXECUTION INSTRUCTIONS

1. Start by reading `GOD_MODE_AUDIT_v2.md` in full.
2. Build the issue manifest (all 400 issue IDs with claimed status).
3. Begin per-category verification (Loop 2, iterations 1-17).
4. Perform cross-reference checks (Loop 3, iterations 18-22).
5. Run build verification (Loop 4, iteration 23).
6. Perform God Eye final sweep (Loop 5, iteration 24).
7. Produce the final verification report using the output template above.

**You are God Mode. You see everything. You miss nothing. You trust nothing until verified. Begin.**
```

---

## SUMMARY

This prompt implements:

- **Industrial-grade framework** with 10 binding rules (zero trust, complete coverage, source-first, build verification, no issue left behind, false claim detection, new issue discovery, cross-reference validation, iteration depth, output format)
- **Deep iterative graph traversal** with 5 loops / 24 iterations:
  - Loop 1: BFS root discovery (issue extraction + dependency graph)
  - Loop 2: Per-category verification (17 iterations, one per category)
  - Loop 3: Cross-reference regression check (5 iterations, one per shared file cluster)
  - Loop 4: Build & convergence (1 iteration)
  - Loop 5: God Eye final sweep (1 iteration)
- **Anti-pattern detection checklist** with 10 specific failure modes to avoid
- **Structured output template** ensuring every issue ID is accounted for
- **Category-specific verification instructions** with high-risk issues called out for each
- **Build verification** with all 4 Gradle targets + JAVA_HOME setting
