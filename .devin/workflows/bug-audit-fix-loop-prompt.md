---
description: Audit 4-5 bugs individually in 3-4 bidirectional rounds (Frontend→VM→API and reverse), then fix each finding in 3-4 separate loops. Checks button visibility, padding, and height on every pass.
---

# /bug-audit-fix-loop

## Usage

```
/bug-audit-fix-loop {BUG_CSV} {BUG_NUMBERS}
```

**Example:**
```
/bug-audit-fix-loop docs/bug_reports_v1.0.0.csv 12,13,14,15,16
```

## What It Does

1. **Selects 4-5 bugs** from the specified CSV file
2. **Audits each bug individually** (never bulk) through 3-4 rounds:
   - **Round 1**: Forward trace — Frontend → ViewModel → Backend API
   - **Round 2**: Reverse trace — Backend API → ViewModel → Frontend
   - **Round 3**: Edge cases, state sync, button visibility on narrow screens
   - **Round 4** (P0/P1 only): Cross-component consistency check
3. **Produces an audit report** with all findings
4. **Fixes each finding individually** (never bulk) through 3-4 loops:
   - **Loop 1**: Root cause fix
   - **Loop 2**: Corrected fix (if L1 fails)
   - **Loop 3**: Alternative approach (if L2 fails)
   - **Loop 4**: Minimal safe fix + escalate (if L3 fails)
5. **Produces a convergence report** with resolution statistics

## Key Checks Performed

- Button visible within screen bounds (no clipping, no overflow)
- No padding/height issues that push buttons off-screen
- `isLoading` resets on all error paths (no stuck loading)
- DTO field names match between server and client
- StateFlow observed by UI, all 4 states handled
- State sync after mutations (refresh, not full reload)
- No double padding on VCard
- VButton uses `full = true` not `fillMaxWidth()`

## Full Spec

See: `.devin/workflows/bug-audit-fix-loop.md`
