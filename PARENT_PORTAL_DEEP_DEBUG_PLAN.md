# Parent Portal Deep Debug Plan

## Issues Found & Fix Status

### Critical Issues (Round 1 Failures)

| ID | Screen | Check | Issue | File | Status |
|----|--------|-------|-------|------|--------|
| H-1 | Announcements | 8 | Raw `Color(0xFF6C8DF5)` not VColors token | ParentActivityScreenV2.kt:153 | FIXED |
| H-2 | Library | 9 | `Modifier.padding(12.dp)` inside VCard (skeleton) | ParentLibraryScreenV2.kt:199 | FIXED |
| H-3 | Library | 9 | `Modifier.padding(16.dp)` inside VCard (My Books) | ParentLibraryScreenV2.kt:320 | FIXED |
| H-4 | Library | 9 | `Modifier.padding(16.dp)` inside VCard (Reservations) | ParentLibraryScreenV2.kt:360 | FIXED |
| H-5 | Library | 9 | `Modifier.padding(12.dp)` inside VCard (Browse) | ParentLibraryScreenV2.kt:235 | FIXED |
| H-6 | PEWS | 8 | `.copy(fontSize=15.sp)` overrides typography tokens | ParentPewsScreenV2.kt:130 | FIXED |
| H-7 | PEWS | 8 | `.copy(fontSize=13.sp, lineHeight=19.sp)` overrides | ParentPewsScreenV2.kt:135 | FIXED |
| H-8 | PEWS | 8 | `.copy(fontSize=12.sp)` and `.copy(fontSize=14.sp)` | ParentPewsScreenV2.kt:147-148 | FIXED |
| H-9 | Fee History | 11 | Wrong TeacherSpinner import | ParentFeeHistoryScreenV2.kt:91 | FIXED |
| H-10 | Fee Payment | 10 | Raw Row+clickable not VButton | ParentFeePaymentScreenV2.kt:152 | FIXED |
| H-11 | Events | 10 | PremiumButton raw Row not VButton | ParentEventRegistrationScreenV2.kt | FIXED |
| H-12 | Events | 8 | PremiumInput raw OutlinedTextField | ParentEventRegistrationScreenV2.kt | FIXED |
| H-13 | Events | 8 | PremiumBadge raw Box not VBadge | ParentEventRegistrationScreenV2.kt | FIXED |
| H-14 | Events | 8 | SegmentChip raw Box not PortalTabChip | ParentEventRegistrationScreenV2.kt | FIXED |
| H-15 | Health | 9 | `padding(16.dp)` not `20.dp` horizontal | ParentHealthScreenV2.kt:93 | FIXED |
| H-16 | Announcements | 18 | Hardcoded strings | ParentActivityScreenV2.kt | FIXED |
| H-17 | PEWS | 18 | Hardcoded strings | ParentPewsScreenV2.kt | FIXED |
| H-18 | Fee Payment | 18 | Hardcoded strings | ParentFeePaymentScreenV2.kt | FIXED |
| H-19 | Fee History | 18 | Hardcoded strings | ParentFeeHistoryScreenV2.kt | FIXED |
| H-20 | Events | 18 | Many hardcoded strings | ParentEventRegistrationScreenV2.kt | FIXED |
| H-21 | Homework | 18 | Hardcoded strings | ParentHomeworkScreenV2.kt | FIXED |

### Warnings

| ID | Issue | File | Status |
|----|-------|------|--------|
| W-1 | Fee History: no backend endpoint, always empty | ParentFeeHistoryScreenV2.kt | PENDING (backend) |
| W-2 | Fee Payment: `onPay` empty, no payment integration | ParentFeePaymentScreenV2.kt | PENDING (backend) |
| W-3 | Pulse: VEmptyState not VStateHost | ParentPulseScreen.kt | PENDING |
| W-4 | Events: empty state no body/icon | ParentEventRegistrationScreenV2.kt | PENDING |
| W-5 | Events: MyRegistrations passes error=null | ParentEventRegistrationScreenV2.kt | PENDING |
| W-6 | Report: error state no retry button | ParentReportScreen.kt | FIXED |
| W-7 | PEWS: VStateHost strings hardcoded | ParentPewsScreenV2.kt | FIXED |
| W-8 | Library: uses PL_PARENT not real name | ParentLibraryScreenV2.kt | PENDING |
| W-9 | Fee Payment: "Razorpay" hardcoded | ParentFeePaymentScreenV2.kt | FIXED |
| W-10 | Homework: success message hardcoded | ParentHomeworkScreenV2.kt | FIXED |

## Per-Feature Status

| # | Feature | R1 | R2 | Status |
|---|---------|----|----|--------|
| 1 | Portal Shell | PASS | PASS | COMPLETE |
| 2 | Home Tab | FAIL: 8,18 | Blocked | IN PROGRESS (H-1 fixed, H-16 pending) |
| 3 | Academics Tab | PASS | PASS | COMPLETE |
| 4 | Fees Tab | PASS | PASS | COMPLETE |
| 5 | Conversations Tab | PASS | PASS | COMPLETE |
| 6 | Profile Tab | PASS | PASS | COMPLETE |
| 7 | Notifications Overlay | PASS | PASS | COMPLETE |
| 8 | Leave Overlay | PASS | PASS | COMPLETE |
| 9 | Messages Overlay | PASS | PASS | COMPLETE |
| 10 | Health Overlay | FAIL: 9 | Blocked | FIXED - needs re-verify |
| 11 | Pulse Overlay | PASS | PASS | COMPLETE |
| 12 | PEWS Overlay | FAIL: 8,18 | Blocked | PARTIAL (H-6,7,8 fixed, H-17 pending) |
| 13 | Library Overlay | FAIL: 9 | Blocked | FIXED - needs re-verify |
| 14 | Events Overlay | FAIL: 8,10 | Blocked | PARTIAL (H-11-14 fixed, H-20 pending) |
| 15 | Fee Payment Overlay | FAIL: 10,18 | Blocked | PARTIAL (H-10 fixed, H-18 pending) |
| 16 | Fee History Overlay | FAIL: 11,18 | Blocked | PARTIAL (H-9 fixed, H-19 pending) |
| 17 | Homework Overlay | FAIL: 18 | Blocked | PENDING (H-21) |
| 18 | Report Card Overlay | FAIL: 12 | Blocked | FIXED (W-6) - needs re-verify |
| 19 | Parent Profile Overlay | PASS | PASS | COMPLETE |
| 20 | LinkChild/Discovery | PASS | PASS | COMPLETE |
| 21 | Scholarships Overlay | PASS | PASS | COMPLETE |
| 22 | Calendar Overlay | PASS | PASS | COMPLETE |
| 23 | Transport Overlay | PASS | PASS | COMPLETE |
| 24 | Digital ID Overlay | PASS | PASS | COMPLETE |
| 25 | Tutor Chat/Progress | PASS | PASS | COMPLETE |

## Remaining Work

### Hardcoded Strings (H-16 through H-21)
These require adding StringKeys entries to the locale system and replacing hardcoded strings with `appString()` calls. Affected files:
- ParentActivityScreenV2.kt — "Announcements", "All caught up", "New announcements..."
- ParentPewsScreenV2.kt — "All good!", "There's no specific concern...", "All on track"
- ParentFeePaymentScreenV2.kt — "Pay Fees", "Outstanding Amount", "Payment Method", etc.
- ParentFeeHistoryScreenV2.kt — "Fee History", "Total Collected", "No payment history", etc.
- ParentEventRegistrationScreenV2.kt — "Events", "Event Detail", "Upcoming Events", etc.
- ParentHomeworkScreenV2.kt — "Homework", "No active homework", "Written answer / notes", etc.

### Warnings (W-1 through W-10)
Lower priority but should be addressed for completeness.

## Fixes Applied This Session

1. **H-1**: `ParentActivityScreenV2.kt:153` — Replaced `Color(0xFF6C8DF5)` with `VColors.sky`
2. **H-2/H-5**: `ParentLibraryScreenV2.kt` — Removed `.padding(12.dp)` from VCard in Browse tab (2 locations)
3. **H-3**: `ParentLibraryScreenV2.kt` — Removed `.padding(16.dp)` from VCard in My Books tab
4. **H-4**: `ParentLibraryScreenV2.kt` — Removed `.padding(16.dp)` from VCard in Reservations tab
5. **H-6/H-7/H-8**: `ParentPewsScreenV2.kt` — Removed all `.copy(fontSize=...)` typography overrides
6. **H-9**: `ParentFeeHistoryScreenV2.kt:91` — Fixed `androidx.compose.material3.TeacherSpinner` to `TeacherSpinner`
7. **H-10**: `ParentFeePaymentScreenV2.kt` — Replaced raw `Row+clickable` pay button with `VButton(full=true)`
8. **H-11**: `ParentEventRegistrationScreenV2.kt` — Replaced `PremiumButton` with `VButton` wrapper
9. **H-12**: `ParentEventRegistrationScreenV2.kt` — Replaced `PremiumInput` with `VInput` wrapper
10. **H-13**: `ParentEventRegistrationScreenV2.kt` — Replaced `PremiumBadge` with `VBadge` wrapper
11. **H-14**: `ParentEventRegistrationScreenV2.kt` — Replaced `SegmentChip` with `PortalTabChip` wrapper
12. **H-15**: `ParentHealthScreenV2.kt:93` — Changed `padding(16.dp)` to `padding(horizontal=20.dp).padding(top=16.dp, bottom=24.dp)`
13. **W-6**: `ParentReportScreen.kt` — Added retry button to error state
