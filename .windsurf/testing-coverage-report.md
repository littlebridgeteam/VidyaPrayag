# QA Testing Coverage Report

**App:** EnRoll Plus v1.1 dev/debug (branch: `development_v1.0.0`)
**Device:** LG LM-F100, Android 13 (1080x2460)
**Backend:** Local dev server `http://192.168.1.15:8080`
**Admin:** a1@gmail.com
**Date:** 2026-07-14
**Mode:** Auto Reporting → Slack `C0BGTQB5Y4X`

---

## 1. Feature-Wise Coverage

| # | Feature | Status | Bugs Found | Notes |
|---|---------|--------|------------|-------|
| 1 | Authentication / Login | ✅ Pass | 0 | Login flow works, token issued, role detected |
| 2 | Dashboard / Home | ✅ Pass | 1 | Announce quick-action navigates to wrong screen (BUG-006) |
| 3 | People Directory — Teachers | ❌ Fail | 1 | Teachers tab completely blank despite API returning data (BUG-001) |
| 4 | People Directory — Students | ⚠️ Partial | 2 | Slow load (39-69s), duplicate "Class" prefix in cards (BUG-002) |
| 5 | People Directory — Staff | 🔲 Not tested | — | Not yet navigated |
| 6 | Records — Coverage | ✅ Pass | 0 | Proper empty state displayed |
| 7 | Records — Pace | 🔲 Not tested | — | Not yet navigated |
| 8 | Records — Attendance | ✅ Pass | 0 | Shows 70% present, 7/3/0/10 stats correctly |
| 9 | Records — Marks | 🔲 Not tested | — | Not yet navigated |
| 10 | Comms — Announcements | ⚠️ Partial | 1 | Duplicate "Class Class 10" in announcement bodies (BUG-004) |
| 11 | Comms — Messages (list) | ✅ Pass | 0 | List loads, shows Gaurav conversation with correct time format |
| 12 | Comms — Messages (thread) | ❌ Fail | 1 | Empty thread — no message bubbles displayed (BUG-007) |
| 13 | Comms — PTM | ✅ Pass | 0 | Shows PTM list with dates and 0/0 met status |
| 14 | Comms — Notifications | ❌ Fail | 1 | Raw ISO timestamps shown instead of human-readable (BUG-005) |
| 15 | Settings — Classes & Subjects | ✅ Pass | 0 | Classes list with sections, subject counts, edit/delete actions |
| 16 | Settings — Transport Management | ✅ Pass | 0 | Proper empty states for Routes/Vehicles/Assignments |
| 17 | Settings — Scholarship Management | ✅ Pass | 0 | Scheme displayed with award, eligibility, edit/deactivate |
| 18 | Settings — Institutional Profile | ✅ Pass | 0 | School profile form with all fields populated |
| 19 | Student Detail | ⚠️ Partial | 1 | DANGER ZONE at top instead of bottom (BUG-008) |
| 20 | Create Event Wizard | ✅ Pass | 0 | 3-step wizard opens, event type selection works |
| 21 | Jump-to-Screen Search | ✅ Pass | 0 | All navigation options listed correctly |
| 22 | Notification Bell | ✅ Pass | 0 | Opens notifications overlay correctly |
| 23 | Pull-to-Refresh | 🔲 Not tested | — | Not explicitly tested |
| 24 | Academic Calendar | 🔲 Not tested | — | Not yet navigated |
| 25 | Health Records | 🔲 Not tested | — | Not yet navigated |
| 26 | Fee Management | 🔲 Not tested | — | Not yet navigated |
| 27 | Library | 🔲 Not tested | — | Not yet navigated |
| 28 | ID Card | 🔲 Not tested | — | Not yet navigated |
| 29 | Admissions | 🔲 Not tested | — | Not yet navigated |
| 30 | Alumni | 🔲 Not tested | — | Not yet navigated |
| 31 | Branding | 🔲 Not tested | — | Not yet navigated |
| 32 | Onboarding | ✅ Pass | 0 | Onboarding flow completed successfully |

---

## 2. Screen-Wise Coverage

| # | Screen | Route | Tested | Result | Screenshot |
|---|--------|-------|--------|--------|------------|
| 1 | Login | auth/login | ✅ | Pass | — |
| 2 | Onboarding | discovery/onboarding | ✅ | Pass | — |
| 3 | Admin Dashboard | admin/dashboard | ✅ | Pass (1 bug) | 66_dashboard.png |
| 4 | People Directory | admin/people | ✅ | Fail | 68_teachers_check.png |
| 5 | Teachers Sub-tab | admin/people/teachers | ✅ | Fail — blank | 68_teachers_check.png |
| 6 | Students Sub-tab | admin/people/students | ✅ | Partial — slow, dup prefix | 67_students_loaded.png |
| 7 | Staff Sub-tab | admin/people/staff | ❌ | Not tested | — |
| 8 | Student Detail | admin/people/student-detail | ✅ | Partial — Danger Zone | 82_student_detail.png |
| 9 | Records — Coverage | admin/records/coverage | ✅ | Pass | 78_records.png |
| 10 | Records — Attendance | admin/records/attendance | ✅ | Pass | 79_attendance.png |
| 11 | Records — Pace | admin/records/pace | ❌ | Not tested | — |
| 12 | Records — Marks | admin/records/marks | ❌ | Not tested | — |
| 13 | Comms Hub | admin/comms | ✅ | Pass | 70_comms.png |
| 14 | Announcements | admin/comms/announcements | ✅ | Partial — dup Class | 70_comms.png |
| 15 | Messages List | admin/comms/messages | ✅ | Pass | 76_messages.png |
| 16 | Message Thread | admin/comms/messages/thread | ✅ | Fail — empty | 77_message_thread.png |
| 17 | PTM | admin/comms/ptm | ✅ | Pass | 87_ptm.png |
| 18 | Notifications | admin/notifications | ✅ | Fail — raw ISO | 73_notifications.png |
| 19 | Settings — Classes & Subjects | admin/settings/classes | ✅ | Pass | 84_classes.png |
| 20 | Settings — Transport | admin/settings/transport | ✅ | Pass | 88_transport.png |
| 21 | Settings — Scholarship | admin/settings/scholarship | ✅ | Pass | 91_scholarship.png |
| 22 | Settings — Institutional Profile | admin/settings/profile | ✅ | Pass | 89_scholarship.png |
| 23 | Create Event Wizard | admin/event/create | ✅ | Pass | 85_create_event.png |
| 24 | Jump-to-Screen Search | overlay/search | ✅ | Pass | 86_jump_search.png |

---

## 3. Actionables-Wise Coverage

| # | Action | Screen | Tested | Result |
|---|--------|--------|--------|--------|
| 1 | Login with credentials | Login | ✅ | Pass — token issued, role detected |
| 2 | Navigate via bottom nav (Home/People/Records/Comms/Settings) | All | ✅ | Pass — all 5 tabs navigate correctly |
| 3 | Tap Announce quick action | Dashboard | ✅ | **FAIL** — opens Notifications instead of Announcements |
| 4 | Tap Add Event quick action | Dashboard | ✅ | Pass — opens Create Event wizard |
| 5 | Tap notification bell | Dashboard | ✅ | Pass — opens Notifications overlay |
| 6 | Tap "Jump to screen" search | Dashboard | ✅ | Pass — shows all navigation options |
| 7 | Switch People sub-tabs (Teachers/Students/Staff) | People | ⚠️ | Teachers blank, Students loads (slow), Staff not tested |
| 8 | Tap student card to view detail | People/Students | ✅ | Pass — opens detail (Danger Zone at top) |
| 9 | Switch Records sub-tabs (Coverage/Pace/Attendance/Marks) | Records | ⚠️ | Coverage + Attendance tested, Pace + Marks not tested |
| 10 | Switch Comms sub-tabs (Announcements/Messages/PTM/Notifications) | Comms | ✅ | Pass — all 4 tabs accessible |
| 11 | Tap message conversation to open thread | Comms/Messages | ✅ | **FAIL** — thread is empty |
| 12 | Tap "See all PTMs" | Comms/PTM | ❌ | Not tested |
| 13 | Tap "See all messages" | Comms/Messages | ❌ | Not tested |
| 14 | Tap "Mark all" in Notifications | Notifications | ❌ | Not tested |
| 15 | Tap "Clear" in Notifications | Notifications | ❌ | Not tested |
| 16 | Tap notification item to mark read | Notifications | ❌ | Not tested |
| 17 | Add Class in Settings | Settings/Classes | ❌ | Not tested |
| 18 | Edit Class in Settings | Settings/Classes | ❌ | Not tested |
| 19 | Delete Class in Settings | Settings/Classes | ❌ | Not tested |
| 20 | Add Route in Transport | Settings/Transport | ❌ | Not tested |
| 21 | Add Vehicle in Transport | Settings/Transport | ❌ | Not tested |
| 22 | Create Scholarship Scheme | Settings/Scholarship | ❌ | Not tested |
| 23 | Edit Scholarship Scheme | Settings/Scholarship | ❌ | Not tested |
| 24 | Deactivate Scholarship Scheme | Settings/Scholarship | ❌ | Not tested |
| 25 | Pull-to-refresh on any screen | Any | ❌ | Not tested |
| 26 | Search/filter in People tab | People | ❌ | Not tested |
| 27 | Search/filter in Records tab | Records | ❌ | Not tested |
| 28 | Add Teacher via People tab | People/Teachers | ❌ | Not tested |
| 29 | Add Student via People tab | People/Students | ❌ | Not tested |
| 30 | Add Staff via People tab | People/Staff | ❌ | Not tested |
| 31 | Graduate Students | People/Students | ❌ | Not tested |
| 32 | Import Students CSV | People/Students | ❌ | Not tested |
| 33 | Continue in Create Event wizard | Event/Create | ❌ | Not tested (only step 1 viewed) |
| 34 | Tap "Notification preferences" | Notifications | ❌ | Not tested |
| 35 | Pin conversation to home | Messages/Thread | ❌ | Not tested |
| 36 | Send new message | Messages/Thread | ❌ | Not tested |

---

## Summary

| Metric | Value |
|--------|-------|
| **Total Features** | 32 |
| **Features Tested** | 20 |
| **Features Passed** | 14 |
| **Features Failed** | 4 |
| **Features Partial** | 2 |
| **Features Not Tested** | 12 |
| **Total Screens** | 24 |
| **Screens Tested** | 20 |
| **Screens Passed** | 13 |
| **Screens Failed** | 4 |
| **Screens Not Tested** | 4 |
| **Total Actionables** | 36 |
| **Actionables Tested** | 11 |
| **Actionables Passed** | 9 |
| **Actionables Failed** | 2 |
| **Actionables Not Tested** | 25 |
| **Total Bugs Found** | 8 |
| **Bugs Reported to Slack** | 8 |

### Bugs Found

| Bug ID | Severity | Title |
|--------|----------|-------|
| BUG-001 | High | Teachers tab completely blank despite API returning data |
| BUG-002 | Medium | Duplicate "Class" prefix in student cards |
| BUG-003 | High | Students API takes 39-69 seconds to load |
| BUG-004 | Medium | Duplicate "Class" prefix in announcement descriptions |
| BUG-005 | Medium | Raw ISO timestamps in notifications |
| BUG-006 | High | Announce quick action opens Notifications instead of Announcements |
| BUG-007 | High | Message thread empty — no message bubbles |
| BUG-008 | Medium | DANGER ZONE at top of student detail instead of bottom |

### Next Steps
- Continue testing untested screens: Staff sub-tab, Records Pace/Marks, Academic Calendar, Health Records, Fee Management, Library, ID Card, Admissions, Alumni, Branding
- Continue testing untested actionables: add/edit/delete flows, search/filter, pull-to-refresh, notification actions, message sending
- Re-test any fixes when bugs are resolved
