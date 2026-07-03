# EnRoll+ Ecosystem Map

> Living document. Every feature build appends an entry here so the NEXT feature
> inherits awareness of what exists, what events it emits, and what surfaces it
> touches. **Append only — never rewrite.**

---

## Feature Index

### 1. Notification Spine (RA-41/42/46/50)
- **Module**: `feature.notifications` (server) + `feature.notification` (shared client)
- **Tables**: `notifications`, `notification_preferences`, `device_tokens`
- **Events emitted**: `notification.created` (implicit via `Notify.toUsers`)
- **Key APIs**: `GET /api/v1/notifications`, `GET /api/v1/notifications/summary`, `PATCH /api/v1/notifications/{id}/read`, `POST /api/v1/notifications/read-all`
- **Surfaces**: Parent bell + inbox, Teacher bell + overlay, Admin web bell + dropdown, Admin dashboard activity feed
- **Deep link support**: `deep_link` column on `notifications` table; `refType` + `refId` for entity linking
- **Cross-feature**: Used by attendance, marks, homework, announcements, leave, fees, link-child, PEWS, report-card, alumni
- **Gaps**: NotificationDto does NOT return `deep_link`, `ref_type`, `ref_id` to clients — clients cannot deep-link from in-app notification taps. Activity feed items are not clickable.

### 2. DevTools (Super Admin)
- **Module**: `feature.devtools` (server) + `website/src/app/admin/dev-tools` (web)
- **Key APIs**: `POST /api/v1/admin/dev/send-notification`, `POST /api/v1/admin/dev/trigger-pulse`, `POST /api/v1/admin/dev/trigger-pews`, `GET/PUT /api/v1/admin/dev/otp-providers`
- **Surfaces**: Admin web portal → Dev Tools page (super_admin only)
- **Cross-feature**: Send-notification uses `Notify.toUser` with `deep_link`; AI Token Monitor (`/api/v1/admin/ai/*`)
- **Gaps**: No server log viewing endpoint or UI. Backend uses `println` + SLF4J but no structured log table or streaming API.

### 3. PEWS (Predictive Early Warning System)
- **Module**: `feature.pews` (server + shared)
- **Tables**: `pews_snapshots`, `pews_interventions`, `pews_config`
- **Events**: PEWS audit logs via `AuditLogger` → writes to `notifications` table (category=`pews_audit`)
- **Surfaces**: Admin web (`/admin/early-warning`), Admin app (SchoolPortalV2 → PewsCohort/PewsStudentDetail overlays), Parent app (ParentPewsScreenV2)
- **Cross-feature**: Uses `Notify.toUsers` for parent messaging; interventions tracked with `refType`/`refId`

### 4. AI Report Card 2.0
- **Module**: `feature.reportcard` (server) + admin web + teacher app
- **Tables**: `report_card_drafts`, `report_card_term_config`
- **Surfaces**: Admin web (`/admin/report-card`), Teacher app (ReportReview/ReportDraftEditor overlays)
- **Deep links**: `/teacher/report-review?className=8&section=A&term=Term 1` — parsed in `NavGraphV2.parseDeepLink`

### 5. AI Tutor 2.0
- **Module**: `feature.tutor` (server) + teacher/admin app
- **Tables**: `tutor_heatmap`, `tutor_efficacy`
- **Surfaces**: Admin web (`/admin/tutor`), Teacher app (Heatmap overlay)
- **Deep links**: `/tutor` — role-aware routing in `parseDeepLink`

### 6. Announcements
- **Module**: `feature.school` (server) — `SchoolAnnouncementsRouting`
- **Tables**: `announcements`
- **Surfaces**: Admin web (`/admin/announcements`), Parent app (announcements feed), Admin app (SchoolCommsScreenV2)
- **Cross-feature**: Triggers `Notify.toUsers` for broadcast; parent synth bridge in notification list (`ann_*` ids)
- **Gaps**: Announcement notifications in parent app are not clickable to a detail view

### 7. Messaging System
- **Module**: `feature.admin.presentation.MessagesViewModel` (shared) + messaging routes (server)
- **Tables**: `message_threads`, `message_messages`
- **Surfaces**: Admin app (Messages overlay), Teacher app (Messages overlay), Parent app (Messages overlay)
- **Gaps**: No deep link from notification to specific chat thread

### 8. Leave Requests
- **Module**: `feature.school` (server) — leave request routes
- **Tables**: `leave_requests`
- **Surfaces**: Admin web (`/admin/leave`), Admin app (LeaveRequests overlay), Teacher/Parent app (leave apply/status)
- **Cross-feature**: Triggers `Notify.toUsers` on apply/decide
- **Gaps**: Leave notification does not deep-link to the specific leave request

### 9. Fees
- **Module**: `feature.school` (server) — fee routes
- **Tables**: `fee_records`
- **Surfaces**: Admin web (`/admin/fees`), Parent app (fees screen)
- **Cross-feature**: Parent synth bridge in notification list (`fee_*` ids); triggers `Notify.toUsers` on status change
- **Gaps**: Fee notifications not clickable to fee detail

### 10. Alumni Management
- **Module**: `feature.alumni` (server + shared)
- **Tables**: `alumni`, `alumni_campaigns`, `alumni_donations`, `alumni_mentorships`, `alumni_mentorship_requests`
- **Surfaces**: Admin web (`/admin/alumni`), Admin app (Alumni/AlumniDetail/AlumniCampaign overlays)
- **Deep links**: `/alumni/directory/{id}` — parsed in `parseDeepLink`

### 11. Offline Mode & Sync
- **Module**: `shared/feature/offline` + `shared/feature/sync`
- **Tables**: Room entities (`SchoolEntity`, `OutboxOperationEntity`, `AnnouncementEntity`, `TeacherDayCacheEntity`)
- **Surfaces**: All mobile portals (offline read cache + write outbox)
- **Cross-feature**: SyncEngine drains outbox; telemetry via `oldestPendingAgeMs`

### 12. Academic Calendar
- **Module**: `feature.school` (server) — calendar routes
- **Tables**: `academic_calendar`, `holidays`, `academic_years`
- **Surfaces**: Admin web calendar, Admin app (AcademicCalendarPlatform overlay)
- **Deep links**: `/calendar` — generic routing

### 13. Library
- **Module**: `feature.library` (server)
- **Tables**: `library_books`, `library_loans`, `library_audit_log`
- **Surfaces**: Admin/Teacher app (Library overlay)
- **Deep links**: `/library` — role-aware routing

### 14. Transport
- **Module**: transport routes (server)
- **Surfaces**: Admin app (TransportManagement overlay), Teacher app (TransportAttendance overlay), Parent app (transport overlay)
- **Deep links**: `/transport` — role-aware routing

### 15. School Day Configuration & Timetable
- **Module**: `feature.school` (server) — day-config + timetable routes
- **Tables**: `school_day_configs`, `teacher_periods`
- **Surfaces**: Admin web (`/admin/academics`), Admin app (ClassesSubjects/ClassDetail overlays)

---

## Event Registry

| Event | Emitted By | Mechanism | Subscribers |
|---|---|---|---|
| `notification.created` | `Notify.toUsers` / `Notify.toUser` | DB insert + FCM push | Bell count, activity feed |
| `pews.audit` | `AuditLogger.log` | DB insert to `notifications` (category=`pews_audit`) | Admin oversight |
| `announcement.posted` | Announcement routing | `Notify.toUsers` call | Parent/teacher notification |
| `attendance.marked` | Attendance routing | `Notify.toUsers` call | Parent notification |
| `marks.published` | Marks routing | `Notify.toUsers` call | Parent notification |
| `homework.assigned` | Homework routing | `Notify.toUsers` call | Parent notification |
| `leave.applied` | Leave routing | `Notify.toUsers` call | Admin/teacher notification |
| `leave.decided` | Leave routing | `Notify.toUsers` call | Applicant notification |
| `fee.status_changed` | Fee routing | `Notify.toUsers` call | Parent notification |
| `link_child.decided` | Link-child routing | `Notify.toUsers` call | Parent notification |

---

## Surface Registry

| Surface | Platform | Role | Notifiable |
|---|---|---|---|
| Parent Portal (app) | Compose MP | Parent | Yes — bell + inbox |
| Teacher Portal (app) | Compose MP | Teacher | Yes — bell + overlay |
| School Portal (app) | Compose MP | SchoolAdmin/SuperAdmin | Yes — bell + overlay |
| Admin Web Portal | Next.js | SchoolAdmin/SuperAdmin | Yes — bell dropdown |
| Activity Feed (web) | Next.js | SchoolAdmin | Merged server-side (notifications + leave + announcements) |

---

## Deep Link Registry

| Path Pattern | Target | Status |
|---|---|---|
| `/parent/{tab}/{overlay}` | ParentPortalV2 tab + overlay | Implemented |
| `/teacher/{screen}?{params}` | TeacherPortalV2 overlay | Implemented (transport, report-review, tutor, events) |
| `/school/{screen}?{params}` | SchoolPortalV2 overlay | Implemented (basic) |
| `/alumni/{screen}/{id}` | SchoolPortalV2 Alumni overlay | Implemented |
| `/announcements` | Generic | Stub — no detail screen |
| `/calendar` | Generic | Stub |
| `/transport` | Role-aware | Implemented |
| `/report-card` | Role-aware | Implemented |
| `/tutor` | Role-aware | Implemented |
| `/library` | Role-aware | Implemented |
| `/events` | Role-aware | Implemented |
