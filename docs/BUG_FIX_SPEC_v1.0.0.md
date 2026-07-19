# Bug Fix Spec — v1.0.0

**Branch:** `fix/bug-fixes-v1.0.0`  
**Base:** `development_v1.0.0` (`73cde248`)  
**Date:** 2026-07-19  

---

## Workflow

1. User reports a bug (description + steps to reproduce + expected vs actual)
2. I investigate root cause in codebase
3. I implement minimal fix (prefer upstream root cause over downstream workaround)
4. I verify the fix compiles / doesn't break existing flows
5. Bug is marked resolved in this spec

---

## Bug Tracker

| # | Severity | Component | Description | Status | Fix Commit |
|---|----------|-----------|-------------|--------|------------|
| 1 | P2 | Auth / School Onboarding | School name fields accept special characters & numbers — no character validation on Full Legal Name or Short Name | ✅ Fixed | — |
| 2 | P2 | UI — School Registration | Affiliation Number accepts alphabets & special characters without validation | ✅ Fixed | — |
| 3 | P1 | UI — School Registration & Edit Profile | "Other" board option shows no text input field — user cannot enter custom board name | ✅ Fixed | — |
| 6 | P1 | UI — School Dashboard | "Add Student" quick action opens Admissions CRM instead of Add Student dialog | ✅ Fixed | — |
| 4 | P2 | UI — School Registration | School Type chip "Private Unaided" text overflows layout — VChip had no width constraint or text overflow handling | ✅ Fixed | — |
| 5 | P0 | Auth / School Onboarding | Continue button causes infinite loading & logout on app reopen — session never persisted before Success screen; date picker for academic year dates not visible in bottom sheet | ✅ Fixed | — |
| 7 | P2 | UI — School Dashboard / Announcements | Announcement screen header missing — UnifiedCreateEventScreenV2 root Column missing statusBarsPadding, header drawn under status bar | ✅ Fixed | — |
| 8 | P2 | UI — School Dashboard / Reports | Report screen header missing — AdminReportPublishScreen root Column missing statusBarsPadding, header drawn under status bar | ✅ Fixed | — |
| 9 | P1 | Analytics | Incorrect analytics data for new school — analytics overview fell back to hardcoded CMS template trend (4.2%) instead of returning zeros when no attendance data exists | ✅ Fixed | — |
| 10 | P1 | UI — Teacher / Assignments | Teacher assignment screen overlapping — VStateHost with skeleton uses AnimatedContent (Box internally), but content lambda emitted 6 composables that stacked on top of each other instead of flowing vertically | ✅ Fixed | — |
| 11 | P1 | UI — Teacher / Edit Profile | Teacher Edit Profile not working — "Edit Details" dropdown menu item called onViewProfile() (same as Profile button), no edit endpoint/API/ViewModel method existed. Added PUT /api/v1/school/teachers/{id} server endpoint, UpdateTeacherRequest model, TeachersApi.updateTeacher, TeachersRepository.updateTeacher, TeacherProfileViewModel.updateTeacher/startEdit/cancelEdit, EditTeacherForm UI with name/email/phone/designation fields, and wired onEditTeacher callback through SchoolPortalV2 → SchoolPeopleScreenV2 → TeachersSubTab → TeacherCard | ✅ Fixed | — |

---

## Severity Levels

- **P0 — Critical:** App crash, data loss, auth failure, production blocker
- **P1 — High:** Feature broken, wrong data shown, UX dead-end
- **P2 — Medium:** Visual glitch, minor UX issue, non-breaking error
- **P3 — Low:** Cosmetic, polish, nice-to-have

## Fix Guidelines

- Minimal changes — single-line fixes when sufficient
- No over-engineering
- Add regression test if the bug is data/logic related
- Never delete or weaken existing tests without explicit direction
- Reference the bug number in commit message: `fix(#N): <description>`
