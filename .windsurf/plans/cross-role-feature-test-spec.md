# Cross-Role Feature Testing — Specification

## Overview

End-to-end functional testing of cross-role features using **two physical devices** via ADB. Each device logs in as a different role (Admin, Teacher, Parent). Actions on one device must propagate and be verifiable on the other.

Extends the existing [Bug Reporting Spec](../plans/bug-reporting-spec.md) — same Slack channels, bug format, ADB commands, severity guidelines.

**No code changes during Manual or Full Auto modes — QA only.**

---

## Device Setup

| Item | Requirement |
|---|---|
| Device 1 | ADB-connected, logged in as Admin or Teacher |
| Device 2 | ADB-connected, logged in as Parent (or complementary role) |
| Backend | Both devices point to same server instance |
| School | Both accounts in same school (same `school_id`) |
| Linked Child | Parent has approved child link to student in school |
| Teacher Assignment | Teacher has active `teacher_subject_assignment` for parent's child's class/section/subject |

### Multi-Device ADB

All ADB commands use `-s <serial>` prefix. Example: `adb -s <SERIAL_1> exec-out screencap -p > /tmp/d1_screenshot.png`

### Device Pairings

| Pairing | Device 1 | Device 2 | Test Modules |
|---|---|---|---|
| A | Admin | Parent | Announcements, Messages, Fees, Link-Child, PTM, Scholarship, Transport, Calendar |
| B | Teacher | Parent | Attendance, Homework, Marks, Leave, Messages, Syllabus |
| C | Admin | Teacher | Teacher Assignments, Announcements (teacher receipt), PTM, Calendar |

---

## Test Modules

### Module 1: Announcements

**Flow:** Admin creates announcement → `Notify.toUsers()` to parents + teachers → FCM push + in-app notification → Parent/Teacher sees it.

**Pairing A: Admin → Parent**

| ID | Test Case | Admin Action | Parent Verification | Pass Criteria |
|---|---|---|---|---|
| ANN-01 | ALL_SCHOOL announcement | Comms → Announcements → Create, audience=ALL_SCHOOL, publish | Notifications → Announcements | Parent sees title+body within 30s. Deep link works. |
| ANN-02 | CLASS-scoped announcement | Audience=CLASS, select parent's child's class | Notifications + Announcements | Targeted parent receives. Other class parents do NOT. |
| ANN-03 | STUDENT-scoped announcement | Audience=STUDENT, select child's student code | Notifications + Announcements | Only targeted parent receives. |
| ANN-04 | Edit announcement | Edit body of existing announcement | Refresh announcements | Updated content visible. |
| ANN-05 | Delete announcement | Delete announcement | Refresh announcements | No longer visible. |
| ANN-06 | Announcement with attachment | Create with image attachment | Open announcement detail | Image renders correctly. |

**Pairing C: Admin → Teacher**

| ID | Test Case | Admin Action | Teacher Verification | Pass Criteria |
|---|---|---|---|---|
| ANN-07 | Teacher receives announcement | Post ALL_SCHOOL announcement | Notifications → Announcements | Teacher receives with deep link `/teacher/announcements?id={id}`. |

---

### Module 2: Messages

**Flow:** Sender calls `sendInConversation()` → `Notify.toUsers()` to recipient → recipient sees message + notification.

**Pairing A: Admin → Parent**

| ID | Test Case | Admin Action | Parent Verification | Pass Criteria |
|---|---|---|---|---|
| MSG-01 | Admin sends message | Comms → Messages → select parent → type → send | Notifications → Messages → thread | Message appears within 30s. Sender name = admin. |
| MSG-02 | Parent replies | (Parent sends reply in thread) | Admin opens thread | Reply appears in admin's thread. |
| MSG-03 | Message with attachment | Admin sends image attachment | Parent opens conversation | Image renders. |
| MSG-04 | New conversation | Admin starts new conversation with parent | Parent checks message list | New conversation appears. |

**Pairing B: Teacher → Parent**

| ID | Test Case | Teacher Action | Parent Verification | Pass Criteria |
|---|---|---|---|---|
| MSG-05 | Teacher sends message | Messages → select parent → send | Parent checks messages | Message received. |
| MSG-06 | Parent sends to teacher | (Parent composes to teacher) | Teacher checks messages | Message received by teacher. |

---

### Module 3: Attendance

**Flow:** Teacher marks via `POST /teacher/attendance` → absent/late triggers `Notify.toUsers()` to parents (category `attendance`).

**Pairing B: Teacher → Parent**

| ID | Test Case | Teacher Action | Parent Verification | Pass Criteria |
|---|---|---|---|---|
| ATT-01 | Mark PRESENT | Mark child PRESENT → save | Academics → Attendance | Shows "Present". No notification sent. |
| ATT-02 | Mark ABSENT | Mark child ABSENT → save | Notifications + Attendance | Push notification received. Record shows "Absent". Deep link works. |
| ATT-03 | Mark LATE | Mark child LATE → save | Notifications + Attendance | Notification received. Shows "Late". |
| ATT-04 | Edit (absent→present) | Change ABSENT to PRESENT → save | Refresh attendance | Updated to "Present". No new notification. |
| ATT-05 | Approved leave pre-marked | Open attendance for approved-leave date | N/A (teacher device) | Student pre-marked "leave" (source "leave_auto"). |
| ATT-06 | Back-date blocked | Try date >7 days ago | N/A (teacher device) | Server rejects "BACK_DATE_BLOCKED". |
| ATT-07 | Future date blocked | Try tomorrow | N/A (teacher device) | Server rejects "FUTURE_DATE". |

---

### Module 4: Homework

**Flow:** Teacher creates homework → `NotifyRecipients.parentsOfClass()` → `Notify.toUsers()` to class parents (category `homework`).

**Pairing B: Teacher → Parent**

| ID | Test Case | Teacher Action | Parent Verification | Pass Criteria |
|---|---|---|---|---|
| HW-01 | Create homework | Homework → Create, select assignment, title, description, due date → save | Notifications → Academics → Homework | Notification received. Homework visible with correct details. Deep link works. |
| HW-02 | Homework with attachment | Create with image/file | Open homework detail | Attachment visible and downloadable. |
| HW-03 | Edit homework | Edit description/due date | Refresh homework list | Updated content visible. |
| HW-04 | Delete homework | Delete/deactivate | Refresh homework list | No longer visible. |

---

### Module 5: Marks / Results Publishing

**Flow (Teacher):** Save marks (no notify) → Publish → `Notify.toUsers()` to parents (category `marks`).
**Flow (Admin):** `POST /school/results` → `Notify.toUsers()` to parents.

**Pairing B: Teacher → Parent**

| ID | Test Case | Teacher Action | Parent Verification | Pass Criteria |
|---|---|---|---|---|
| MKS-01 | Save marks (draft) | Create assessment → enter marks → SAVE only | Notifications + Marks | NO notification. Marks NOT visible (isPublished=false). |
| MKS-02 | Publish marks | Publish the assessment | Notifications → Marks | Notification received. Marks visible with score, max marks. Deep link works. |
| MKS-03 | Unpublish | Unpublish assessment | Refresh marks | Marks no longer visible. No re-notification. |
| MKS-04 | Re-publish | Republish after unpublish | Check marks | Marks visible again. New notification sent. |
| MKS-05 | Absent (AB≠0) | Mark student AB → publish | Check marks | Shows "Absent", not "0". |
| MKS-06 | Marks over max | Enter marks > maxMarks | N/A (teacher device) | Server rejects "MARK_OVER_MAX". |

**Pairing A: Admin → Parent**

| ID | Test Case | Admin Action | Parent Verification | Pass Criteria |
|---|---|---|---|---|
| MKS-07 | Admin publishes results | Records → Marks → Publish Results, enter scores | Notifications + Marks | Parent notified. Results visible. |

---

### Module 6: Leave Requests

**Flow:** Parent applies → `Notify.toUsers()` to teachers + admins → approve/reject → `Notify.toUser()` to parent.

**Pairing B: Parent → Teacher**

| ID | Test Case | Parent Action | Teacher Verification | Pass Criteria |
|---|---|---|---|---|
| LVE-01 | Apply for leave | Leave Requests → apply for child, dates, reason | Notifications → Leave Requests | Teacher notified. Request visible, status "Pending". |
| LVE-02 | Teacher approves | (Teacher approves request) | Parent checks notifications | Parent notified "approved". Status updated. |
| LVE-03 | Teacher rejects | (Teacher rejects with reason) | Parent checks notifications | Parent notified "rejected" with reason. |

**Pairing A: Parent → Admin**

| ID | Test Case | Parent Action | Admin Verification | Pass Criteria |
|---|---|---|---|---|
| LVE-04 | Admin approves | Parent applies | Admin approves | Parent receives approval notification. |
| LVE-05 | Admin rejects | Parent applies | Admin rejects | Parent receives rejection with reason. |
| LVE-06 | Approved leave in attendance | (Precondition: approved leave for today) | Teacher opens attendance | Student pre-marked "leave" (source "leave_auto"). |

---

### Module 7: Parent-Child Link Approval

**Flow:** Parent submits link-child → `Notify.toUsers()` to admins → admin approves → `children` row created → `Notify.toUser()` to parent.

**Pairing A: Parent → Admin**

| ID | Test Case | Parent Action | Admin Verification | Pass Criteria |
|---|---|---|---|---|
| LNK-01 | Submit link request | Link Child wizard → search school → enter details → submit | Notifications → Link Requests | Admin notified. Request in PENDING tab with correct details. |
| LNK-02 | Admin approves | (Admin approves) | Parent dashboard | Parent notified "approved". Child appears on dashboard. |
| LNK-03 | Admin rejects | (Admin rejects) | Parent notifications | Parent notified "declined". Child NOT on dashboard. |
| LNK-04 | Phone mismatch → needs_review | Submit with different phone | Link Requests → NEEDS_REVIEW tab | Request in NEEDS_REVIEW tab, not PENDING. |

---

### Module 8: PTM / Event Registration

**Flow:** Admin schedules PTM → calendar event with `registrationEnabled=true` → parent registers → `Notify.toUser()` to parent + `Notify.toUsers()` to teachers.

**Pairing A: Admin → Parent**

| ID | Test Case | Admin Action | Parent Verification | Pass Criteria |
|---|---|---|---|---|
| PTM-01 | Schedule PTM | Comms → PTM → Schedule, enter details | Events/PTM screen | PTM visible. Notification received. |
| PTM-02 | Parent registers | (Parent registers for slot) | Admin PTM dashboard | Count increments. Parent gets confirmation notification. |
| PTM-03 | Parent cancels | (Parent cancels registration) | Admin PTM dashboard | Count decrements. |
| PTM-04 | Waitlist when full | maxAttendees=1, second parent tries | Second parent device | Gets "WAITLISTED" status + notification. |

**Pairing C: Admin → Teacher**

| ID | Test Case | Admin Action | Teacher Verification | Pass Criteria |
|---|---|---|---|---|
| PTM-05 | Teacher sees registrations | Parent registers | Teacher PTM screen | Teacher sees count + slot-wise bookings. |

---

### Module 9: Fee Management

**Flow:** Admin creates fee record → parent sees fee → `NotificationScheduler.checkFeeReminders()` sends reminders for DUE/OVERDUE.

**Pairing A: Admin → Parent**

| ID | Test Case | Admin Action | Parent Verification | Pass Criteria |
|---|---|---|---|---|
| FEE-01 | Create fee record | Fees → Create, amount, title, due date, child | Parent Fees screen | Fee visible, status "DUE". |
| FEE-02 | Fee overdue | (Date passes or server trigger) | Parent fees | Status "OVERDUE". |
| FEE-03 | Fee reminder | Reminder scheduler runs | Parent notifications | Notification "Fee Due Soon" or "Fee Overdue". Deep link works. |
| FEE-04 | Mark paid | Admin marks paid (or parent pays) | Parent fees | Status "PAID". |
| FEE-05 | Transport fee | Transport → Fees → create | Parent fees | Transport fee visible. |

---

### Module 10: Scholarship

**Flow:** Admin creates scheme → parent applies → admin reviews → approve/reject → `Notify.toUser()` to parent.

**Pairing A: Admin → Parent**

| ID | Test Case | Admin Action | Parent Verification | Pass Criteria |
|---|---|---|---|---|
| SCH-01 | Create scheme | Settings → Scholarship → Create | Parent scholarships | New scholarship visible. |
| SCH-02 | Parent applies | (Parent applies with child + docs) | Admin applications | Application "PENDING" with details. |
| SCH-03 | Admin approves | (Admin approves) | Parent notifications | Parent notified. Status "APPROVED". Fee waiver applied. |
| SCH-04 | Admin rejects | (Admin rejects with remarks) | Parent notifications | Parent notified with remarks. Status "REJECTED". |
| SCH-05 | Disburse | (Admin disburses amount + ref) | Parent application detail | Disbursement info visible. Status "DISBURSED". |
| SCH-06 | Renewal | (Parent applies for renewal) | Admin renewals | Renewal request "pending". |
| SCH-07 | Deactivate scheme | Admin deactivates | Parent scholarships | Scheme no longer visible. |

---

### Module 11: Transport

**Flow:** Admin creates route/vehicle/assignment → driver updates GPS → parent views live location.

**Pairing A: Admin → Parent**

| ID | Test Case | Admin Action | Parent Verification | Pass Criteria |
|---|---|---|---|---|
| TRP-01 | Create route + stops | Transport → Routes → Create | N/A | Route created. |
| TRP-02 | Create vehicle | Create vehicle, assign to route | N/A | Vehicle created. |
| TRP-03 | Assign student | Assign parent's child to route/stop/vehicle | Parent transport screen | Route name, stop, bus number visible. |
| TRP-04 | Live location | (Simulate GPS update) | Parent live location | Map shows bus location, next stop, ETA. |
| TRP-05 | Deactivate assignment | Admin deactivates | Parent transport | No assignment found. Empty state. |

---

### Module 12: Calendar Events

**Pairing A: Admin → Parent**

| ID | Test Case | Admin Action | Parent Verification | Pass Criteria |
|---|---|---|---|---|
| CAL-01 | Create holiday | Create HOLIDAY event → publish | Parent calendar | Holiday visible. Attendance blocked for that date. |
| CAL-02 | Create exam event | Create EXAM event | Parent calendar | Exam visible with date. |
| CAL-03 | Custom event with banner | Create custom event with banner image | Parent calendar → detail | Event visible, banner renders. |
| CAL-04 | Event with registration | Create event with registration + slots | Parent event | Register button visible, can register. |

**Pairing C: Admin → Teacher**

| ID | Test Case | Admin Action | Teacher Verification | Pass Criteria |
|---|---|---|---|---|
| CAL-05 | Teacher sees events | Publish calendar event | Teacher calendar | Event visible. |

---

### Module 13: Library (Admin → Parent/Student)

| ID | Test Case | Admin Action | Parent Verification | Pass Criteria |
|---|---|---|---|---|
| LIB-01 | Issue book | Library → issue book to student | Parent library → issued | Book shows in issued list with due date. |
| LIB-02 | Return book | Return the issued book | Parent library | Book removed from issued list. |
| LIB-03 | Renew book | Renew issued book | Parent library | Due date extended. Renewal count incremented. |
| LIB-04 | Reserve book | (Parent reserves unavailable book) | Admin reservations | Reservation appears "pending". |
| LIB-05 | Reservation fulfilled | Return + notify reserver | Parent notifications | Parent notified book available. |

---

### Module 14: Notification Preferences

| ID | Test Case | Device Action | Verification | Pass Criteria |
|---|---|---|---|---|
| NOT-01 | Disable category | Parent disables "homework" notifications in preferences | Teacher creates homework | Parent does NOT receive homework notification. In-app notification also suppressed. |
| NOT-02 | Rate limit (10/category/hour) | Trigger >10 notifications in same category within 1 hour | Parent notifications | After 10th, further notifications suppressed (rate limited). |
| NOT-03 | Rate limit (50/day total) | Trigger >50 notifications across categories in 24h | Parent notifications | After 50th, further notifications suppressed. |
| NOT-04 | Mark notification read | Parent taps notification | Notification list | Notification marked as read (isRead=true). |
| NOT-05 | Deep link navigation | Parent taps notification with deep link | App navigates | Correct screen opens matching deep link path. |

---

## QA Modes for Cross-Role Testing

### Mode 1: Manual Cross-Role
- User performs actions on Device 1, describes what to verify on Device 2
- Cascade captures screenshots on both devices via ADB
- Bug reports posted to `C0BH3721V0D` (Manual channel)
- Screenshots from BOTH devices attached when applicable

### Mode 2: Full Auto Cross-Role
- Cascade autonomously navigates Device 1 (sender role) via ADB
- Cascade then switches to Device 2 (receiver role) to verify propagation
- Cascade captures screenshots + UI hierarchy + logcat from both devices
- Bug reports posted to `C0BGTQB5Y4X` (Auto channel)
- Both device screenshots attached to bug reports

### Mode 3: Auto Fix Cross-Role
- Same as Full Auto but bugs logged to local `.md` file
- Fixed in batches of 5, rebuilt, reverified across both devices
- No Slack posting

---

## Cross-Role Bug Report Format

Extends the standard bug report with cross-role context:

```
🐛 *Bug Report — {Category}*  [{Severity}]
• *Bug ID:* BUG-{timestamp}-{seq}
• *Title:* {concise summary}
• *Severity:* {Critical/High/Medium/Low}
• *Category:* {Functional/UI/Non-Functional}
• *Subcategory:* {specific type}
• *Cross-Role Flow:* {Sender Role} → {Receiver Role} — {Feature}
• *Expected Behavior:* {what should happen on receiver device}
• *Actual Behavior:* {what actually happens}
• *Steps to Reproduce:*
  1. On Device 1 ({role}): {action}
  2. On Device 2 ({role}): {verification action}
  3. {observation}
• *Device 1:* {model, Android, role}
• *Device 2:* {model, Android, role}
• *App:* {version, build type}
• *Mode:* {Manual/Auto}
• *Time:* {ISO 8601 UTC}
📎 Screenshot D1 attached
📎 Screenshot D2 attached
```

---

## Test Execution Order

Recommended order for maximum coverage efficiency:

1. **Pairing A (Admin → Parent):** LNK → ANN → MSG → FEE → PTM → SCH → TRP → CAL → LIB → MKS-07
2. **Pairing B (Teacher → Parent):** ATT → HW → MKS → LVE → MSG
3. **Pairing C (Admin → Teacher):** ANN-07 → PTM-05 → CAL-05
4. **Cross-cutting:** NOT (notification preferences, rate limits, deep links)

---

## Summary

| Metric | Value |
|---|---|
| Total Test Modules | 14 |
| Total Test Cases | ~75 |
| Device Pairings | 3 (A: Admin↔Parent, B: Teacher↔Parent, C: Admin↔Teacher) |
| Cross-Role Flows | Announcements, Messages, Attendance, Homework, Marks, Leave, Link-Child, PTM, Fees, Scholarship, Transport, Calendar, Library, Notification Prefs |
| Bug Report Channels | Same as bug-reporting-spec (Manual: `C0BH3721V0D`, Auto: `C0BGTQB5Y4X`) |
| QA Modes | Manual, Full Auto, Auto Fix (extended for dual-device) |
