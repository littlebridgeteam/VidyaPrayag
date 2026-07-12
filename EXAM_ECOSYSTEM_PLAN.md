# Examination & Unit Test Ecosystem — End-to-End Plan

> **The Agentic OS for School Management** — Full exam lifecycle from timetable upload to marks publication.

---

## Table of Contents

1. [Executive Summary](#1-executive-summary)
2. [What Already Exists](#2-what-already-exists)
3. [What's Missing](#3-whats-missing)
4. [Architecture: The Full Exam Lifecycle](#4-architecture-the-full-exam-lifecycle)
5. [Database Changes Required](#5-database-changes-required)
6. [Backend API: New Endpoints](#6-backend-api-new-endpoints)
7. [Backend: Scheduled Jobs](#7-backend-scheduled-jobs)
8. [Shared Module: Models & API Client](#8-shared-module-models--api-client)
9. [Teacher Portal UI](#9-teacher-portal-ui)
10. [Parent Portal UI](#10-parent-portal-ui)
11. [Admin Portal UI](#11-admin-portal-ui)
12. [Notification & Modal System](#12-notification--modal-system)
13. [AI OCR Pipeline](#13-ai-ocr-pipeline)
14. [Implementation Phases](#14-implementation-phases)
15. [File Inventory: Existing vs New](#15-file-inventory-existing-vs-new)

---

## 1. Executive Summary

The codebase has a **solid assessment/marks foundation** — the `AssessmentsTable` + `AssessmentMarksTable` schema, teacher Gradebook CRUD (create, list, enter marks, save, publish, unpublish), parent marks read API, AI vision OCR (for timetable import), academic calendar with EXAM event type, and a notification system with deep links.

What's missing is the **connective tissue** that turns these isolated pieces into a flow:

```
Teacher uploads exam timetable photo
  → AI OCR extracts exams
  → Bulk-create calendar EXAM events + assessments
  → Evening before each exam: notification to parents with syllabus
  → Parent sees exam detail modal (syllabus, or "request syllabus" button)
  → Exam happens
  → Teacher enters marks per subject
  → Teacher publishes → parent sees marks
```

This plan covers every gap: database tables, API endpoints, scheduled jobs, UI screens, modals, and AI OCR.

---

## 2. What Already Exists

### 2.1 Database Tables

| Table | Location | Status |
|-------|----------|--------|
| `assessments` | `Tables.kt:955-1002` | ✅ Full lifecycle (draft→scheduled→marks_pending→published→archived), typed scope (assignmentId, classId, subjectId), type (scheduled/surprise/assignment/project/exam), maxMarks, passMarks, examDate, calendarEventId, topicId |
| `assessment_marks` | `Tables.kt:1009-1040` | ✅ Per-student marks with studentRef (FK students.id), isAbsent, remark, enteredBy, enteredAt |
| `calendar_events` | `Tables.kt` | ✅ Type EXAM supported, audience (ALL_SCHOOL/GRADES/CLASSES/SECTIONS), class_ids, notify flags |
| `exam_results` | `Tables.kt:911-929` | ⚠️ DEPRECATED legacy table, still read by admin portal |
| `syllabus_units` | `Tables.kt:1054-1066` | ✅ Legacy syllabus units (chapter/topic, covered flag) |
| `curriculum_units` | `Tables.kt` | ✅ Typed syllabus template (class+subject scoped, position, depth) |
| `syllabus_progress` | `Tables.kt` | ✅ Per-section coverage tracking |
| `teacher_subject_assignments` | `Tables.kt` | ✅ Maps teachers to class+section+subject |
| `school_announcements` | `Tables.kt` | ✅ Announcement/notice system |

### 2.2 Backend API Endpoints

| Endpoint | File | What It Does |
|----------|------|-------------|
| `GET /api/v1/teacher/assessments?assignmentId=&status=` | `TeacherGradebookRouting.kt:416` | List assessments for a class+subject |
| `POST /api/v1/teacher/assessments` | `TeacherGradebookRouting.kt:452` | Create assessment (name, type, maxMarks, passMarks, examDate, calendarEventId) |
| `GET /api/v1/teacher/assessments/{id}/marks` | `TeacherGradebookRouting.kt:572` | Load roster + existing marks |
| `PUT /api/v1/teacher/assessments/{id}/marks` | `TeacherGradebookRouting.kt:633` | Save marks (NO publish, NO notify) |
| `POST /api/v1/teacher/assessments/{id}/publish` | `TeacherGradebookRouting.kt:776` | Publish → sets isPublished=true → notifies parents via `Notify.toUsers()` |
| `POST /api/v1/teacher/assessments/{id}/unpublish` | `TeacherGradebookRouting.kt:837` | Retract marks |
| `GET /api/v1/teacher/assessments/history?assignmentId=` | `TeacherGradebookRouting.kt:311` | Trends + score distribution |
| `GET /api/v1/parent/child/{id}/marks` | `ParentAcademicsRouting.kt:518` | Parent reads published marks for their child |
| `POST /api/v1/school/timetable/import-ocr` | `TimetableImportRouting.kt:126` | AI OCR for **class timetable** (weekly periods) — NOT exam timetable |
| `POST /api/v1/school/timetable/import-text` | `TimetableImportRouting.kt:185` | AI text parse for timetable |
| `POST /tutor/ingest/photo` | `IngestRouting.kt:39` | AI OCR for tutor problem photos |
| Calendar CRUD | Various | Create/list/publish calendar events including EXAM type |

### 2.3 AI / OCR Infrastructure

| Component | File | Capability |
|-----------|------|-----------|
| `AiService.completeWithVision()` | `AiService.kt:636-773` | Vision-capable LLM call (Gemini + OpenRouter), circuit breaker, rate limiter, guardrails |
| `OcrService` | `OcrService.kt:34-113` | Photo → text extraction for tutor problems |
| Timetable OCR | `TimetableImportRouting.kt:103-181` | AI vision extracts weekly schedule from image, parses to time slots |
| `completeWithVision` prompt pattern | `TimetableImportRouting.kt:103-110` | System prompt → structured output → regex parse → slots |

### 2.4 Notification System

| Component | File | Capability |
|-----------|------|-----------|
| `Notify.toUsers()` | `Notify.kt` | Send push notification to user IDs with category, title, body, deepLink, refType, refId |
| `NotifyRecipients.parentsOfClass()` | `Notify.kt` | Resolve all parent user IDs for a class |
| `NotificationScheduler` | `NotificationScheduler.kt` | Scheduled notification dispatch (hourly check) |
| Deep link system | `DeepLinkTarget` in shared | Typed deep links for portal navigation |

### 2.5 Teacher Portal UI

| Screen | File | What Exists |
|--------|------|-------------|
| `TeacherMarksScreenV2.kt` | composeApp | Assessment list, create assessment dialog, marks grid, save, publish |
| `TeacherGradebookViewModel.kt` | shared | State management for assessments + marks |
| `TeacherPortalV2.kt` | composeApp | 5-tab shell (HOME, UPDATE, CLASSES, TIMETABLE, PROFILE), overlay system |
| `TeacherAnnouncementDetailScreen.kt` | composeApp | Full-screen announcement detail with deep link support |
| `AcademicCalendarScreenV2.kt` | composeApp | Calendar view with EXAM event type support |

### 2.6 Parent Portal UI

| Screen | File | What Exists |
|--------|------|-------------|
| `ParentAcademicsScreenV2.kt` | composeApp | Tabs: Overview, Attendance, Marks, Syllabus, Quizzes, Homework, Timetable, Report |
| `ParentResultsFeesCards.kt` | composeApp | Results card with sparkline trend, latest mark, delta vs previous |
| `ParentAcademicsViewModel.kt` | shared | Loads marks via `getChildMarks()`, child switcher |
| `TrackProgressViewModel.kt` | shared | Academic competencies, emotional intelligence, badges |

### 2.7 Report Card System

| Component | File | Capability |
|-----------|------|-----------|
| `ReportRollupService` | `ReportRollupService.kt` | Deterministic fact bundle from assessment marks + attendance + PEWS |
| `ReportAssemblyService` | `ReportAssemblyService.kt` | Orchestrates rollup → triage → narrator → draft → review → publish |
| `NarratorService` | narrator/ | AI-generated narrative report card text |

---

## 3. What's Missing

### 3.1 Exam Timetable Upload (OCR) — ❌ MISSING

The existing `TimetableImportRouting` handles **class weekly timetables** (periods → subjects), NOT **exam timetables** (dates → subjects + time slots). Need:

- New endpoint `POST /api/v1/teacher/exam-timetable/import-ocr` — AI vision extracts exam schedule from a printed exam timetable photo
- New endpoint `POST /api/v1/teacher/exam-timetable/import-text` — text-based parsing fallback
- AI prompt that extracts: date, subject, time, max marks, exam name from a tabular exam schedule
- Parse output into structured `ExamTimetableSlotDto` list

### 3.2 Exam Timetable → Calendar + Assessments Bulk Creation — ❌ MISSING

No endpoint to bulk-create calendar EXAM events + draft assessments from an imported timetable. Need:

- `POST /api/v1/teacher/exam-timetable/publish` — takes parsed slots + class scope, creates:
  - One `calendar_events` row (type=EXAM) per exam date
  - One `assessments` row (status=draft/scheduled) per subject exam
  - Links assessment.calendarEventId to the calendar event

### 3.3 Exam Syllabus Mapping — ❌ MISSING

No way to attach "chapters/topics to study" to an exam. `AssessmentsTable.topicId` is a single topic, not a list. Need:

- New table `exam_syllabus_mapping` (assessment_id, curriculum_unit_id) — many-to-many
- API to add/remove syllabus units for an exam
- Parent API to read "what to study" for an upcoming exam

### 3.4 Exam Reminder Job (Evening Before) — ❌ MISSING

No scheduled job that runs in the evening, finds tomorrow's exams, and sends notifications. Need:

- `ExamReminderJob` — runs daily at 6 PM IST
- Finds assessments where examDate = tomorrow + status in (scheduled, draft)
- Sends notification to parents: "Tomorrow: [Subject] exam — [chapters/topics]"
- Deep links to exam detail modal
- If no syllabus mapped → notification includes "Request Syllabus" CTA

### 3.5 Exam Detail Modal (All Portals) — ❌ MISSING

No exam-specific modal that shows: exam name, subject, date, time, max marks, syllabus chapters, "request syllabus" button. Need:

- Parent: `ExamDetailModal.kt` — shows exam info + syllabus + request button → opens messaging to subject/class teacher
- Teacher: `ExamDetailSheet.kt` — shows exam info + edit syllabus mapping + enter marks CTA
- Admin: `ExamDetailAdminSheet.kt` — shows exam info + edit + publish calendar event

### 3.6 "Request Syllabus" Flow — ❌ MISSING

No endpoint or messaging flow for a parent to request syllabus from a teacher. Need:

- `POST /api/v1/parent/exam/{assessmentId}/request-syllabus` — creates a message thread to the subject teacher (or class teacher fallback) with a pre-filled "Please share syllabus for [exam]" message
- Resolve subject teacher via `teacher_subject_assignments` (class+section+subject)
- Fallback to class teacher if no subject teacher assigned

### 3.7 Notice/Announcement Modal System — ⚠️ PARTIAL

`TeacherAnnouncementDetailScreen` exists but:
- No parent-side announcement detail modal
- No admin-side announcement detail modal
- Announcements are not linked to exams
- No "attach syllabus notice" flow during timetable upload

### 3.8 Parent Exam Calendar View — ❌ MISSING

Parent sees marks in the Marks tab, but there's no "upcoming exams" view showing:
- Exam dates on a calendar/timeline
- Multiple exams per day
- Syllabus status per exam
- Countdown to each exam

### 3.9 Exam Timetable as a First-Class Object — ❌ MISSING

Currently assessments are created one-by-one. There's no "exam timetable" as a collection. Need:

- `exam_timetables` table — groups assessments into a named set (e.g., "Mid Term 2026")
- `exam_timetable_entries` table — links timetable to individual assessments with date/time/subject

---

## 4. Architecture: The Full Exam Lifecycle

```
┌─────────────────────────────────────────────────────────────────────┐
│                        EXAM LIFECYCLE FLOW                          │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  1. UPLOAD          Teacher uploads exam timetable photo            │
│     │               → AI OCR extracts: date, subject, time, name    │
│     │               → Teacher reviews + edits extracted slots       │
│     │               → Teacher optionally attaches syllabus notice   │
│     ↓                                                               │
│  2. PUBLISH         Teacher publishes timetable                     │
│     │               → Bulk-create calendar EXAM events              │
│     │               → Bulk-create draft assessments per subject     │
│     │               → Send notice/announcement to parents           │
│     │               → Academic calendar populated for the class     │
│     ↓                                                               │
│  3. SYLLABUS        Teacher maps syllabus units to each exam        │
│     │               → exam_syllabus_mapping rows created            │
│     │               → Parent can view "what to study"               │
│     ↓                                                               │
│  4. REMINDER        ExamReminderJob runs at 6 PM IST daily          │
│     │               → Finds exams scheduled for tomorrow            │
│     │               → Sends push notification to parents            │
│     │               → Notification includes: subject, chapters      │
│     │               → If no syllabus: "Request Syllabus" CTA        │
│     │               → Deep links to ExamDetailModal                 │
│     ↓                                                               │
│  5. EXAM DAY        Student takes exam                               │
│     │               → Calendar shows EXAM event                     │
│     │               → Teacher portal shows "Enter Marks" CTA        │
│     ↓                                                               │
│  6. MARKS ENTRY     Teacher opens Gradebook → assessment            │
│     │               → Enters marks per student                      │
│     │               → Saves (status → marks_pending)                │
│     │               → Marks NOT visible to parents yet              │
│     ↓                                                               │
│  7. PUBLISH         Teacher clicks "Publish"                        │
│     │               → Status → published, isPublished = true        │
│     │               → Push notification to all parents              │
│     │               → Deep link to marks view                       │
│     │               → Report card drafts marked stale               │
│     ↓                                                               │
│  8. PARENT VIEW     Parent opens notification → marks view          │
│                     → Sees score, max marks, grade, trend           │
│                     → Sparkline shows progress across exams         │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 5. Database Changes Required

### 5.1 New Table: `exam_timetables`

```sql
CREATE TABLE IF NOT EXISTS exam_timetables (
    id              UUID        PRIMARY KEY DEFAULT uuid_generate_v4(),
    school_id       UUID        NOT NULL,
    teacher_id      UUID        NOT NULL,                    -- FK app_users.id (creator)
    class_name      TEXT        NOT NULL,                    -- "Class 8"
    section         VARCHAR(8)  NOT NULL DEFAULT 'A',
    academic_year_id UUID       NULL,
    name            TEXT        NOT NULL,                    -- "Mid Term 2026", "Unit Test 1"
    term            VARCHAR(32) NULL,                        -- "Term 1", "Term 2"
    status          VARCHAR(16) NOT NULL DEFAULT 'draft',    -- draft | published | archived
    source_image_url TEXT       NULL,                        -- R2 URL if uploaded from photo
    ai_used         BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX ix_exam_tt_school ON exam_timetables (school_id, class_name, section);
CREATE INDEX ix_exam_tt_status ON exam_timetables (school_id, status);
```

### 5.2 New Table: `exam_timetable_entries`

```sql
CREATE TABLE IF NOT EXISTS exam_timetable_entries (
    id              UUID        PRIMARY KEY DEFAULT uuid_generate_v4(),
    timetable_id    UUID        NOT NULL,                    -- FK exam_timetables.id
    assessment_id   UUID        NULL,                        -- FK assessments.id (created on publish)
    calendar_event_id UUID      NULL,                        -- FK calendar_events.id
    school_id       UUID        NOT NULL,
    exam_date       DATE        NOT NULL,
    start_time      TIME        NULL,                        -- 09:00
    end_time        TIME        NULL,                        -- 12:00
    subject         TEXT        NOT NULL,
    exam_name       TEXT        NOT NULL,                    -- "Maths Unit Test"
    max_marks       INTEGER     NOT NULL DEFAULT 100,
    room            VARCHAR(64) NULL,
    sort_order      INTEGER     NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX ix_exam_tte_tt ON exam_timetable_entries (timetable_id);
CREATE INDEX ix_exam_tte_date ON exam_timetable_entries (school_id, exam_date);
CREATE INDEX ix_exam_tte_assessment ON exam_timetable_entries (assessment_id);
```

### 5.3 New Table: `exam_syllabus_mapping`

```sql
CREATE TABLE IF NOT EXISTS exam_syllabus_mapping (
    id              UUID        PRIMARY KEY DEFAULT uuid_generate_v4(),
    assessment_id   UUID        NOT NULL,                    -- FK assessments.id
    curriculum_unit_id UUID     NOT NULL,                    -- FK curriculum_units.id
    school_id       UUID        NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE(assessment_id, curriculum_unit_id)
);
CREATE INDEX ix_esm_assessment ON exam_syllabus_mapping (assessment_id);
CREATE INDEX ix_esm_unit ON exam_syllabus_mapping (curriculum_unit_id);
```

### 5.4 Exposed Table Objects (Kotlin)

Add to `server/src/main/kotlin/.../db/Tables.kt`:

- `ExamTimetablesTable : UUIDTable("exam_timetables")`
- `ExamTimetableEntriesTable : UUIDTable("exam_timetable_entries")`
- `ExamSyllabusMappingTable : UUIDTable("exam_syllabus_mapping")`

---

## 6. Backend API: New Endpoints

### 6.1 Exam Timetable Import (OCR)

```
POST /api/v1/teacher/exam-timetable/import-ocr
```
- **Auth**: Teacher (JWT)
- **Body**: `{ image: base64, mimeType: "image/jpeg", className: "Class 8", section: "A" }`
- **Process**: 
  1. Size/mime validation (same as TimetableImportRouting)
  2. `AiService.completeWithVision()` with exam-timetable-specific prompt
  3. AI prompt extracts: date, subject, start_time, end_time, exam_name, max_marks
  4. Parse AI output → `List<ExamTimetableSlotDto>`
- **Returns**: `{ slots: [...], rawText: "...", aiUsed: true }`

**AI System Prompt** (new):
```
You are an exam timetable OCR assistant. Extract the exam schedule from the provided image.
For each exam, output exactly one line in the format:
YYYY-MM-DD | HH:MM-HH:MM | Subject | Exam Name | Max Marks
Rules:
- Date must be YYYY-MM-DD
- Time in 24-hour format (e.g. 09:00-12:00). If no time, use 00:00-00:00
- Subject is the subject name (e.g. "Mathematics", "English")
- Exam Name is the test name (e.g. "Unit Test 1", "Mid Term")
- Max Marks is a number (e.g. 80, 100). If not visible, use 100
- Output ONLY the exam lines, no preamble
- If the image is not an exam timetable, output: ERROR_NOT_AN_EXAM_TIMETABLE
```

### 6.2 Exam Timetable Import (Text)

```
POST /api/v1/teacher/exam-timetable/import-text
```
- Same as OCR but takes raw text input (for typed/pasted timetables)

### 6.3 Exam Timetable Create + Publish

```
POST /api/v1/teacher/exam-timetable
```
- **Body**: `{ className, section, name, term, slots: [...] }`
- **Process**:
  1. Create `exam_timetables` row (status=draft)
  2. Create `exam_timetable_entries` rows for each slot
  3. Optionally store source image URL in R2
- **Returns**: `{ timetableId, entries: [...] }`

```
POST /api/v1/teacher/exam-timetable/{id}/publish
```
- **Process**:
  1. For each entry:
     a. Create `calendar_events` row (type=EXAM, startDate=endDate=exam_date, audience=CLASSES, classIds=[classId])
     b. Create `assessments` row (type=exam, status=scheduled if future date, examDate=date, maxMarks, calendarEventId)
     c. Link `exam_timetable_entries.assessmentId` + `calendarEventId`
  2. Set timetable status=published
  3. Send announcement/notice to parents: "Exam timetable published: [name]"
  4. Notify parents via `Notify.toUsers()` with deep link to exam timetable view
- **Returns**: `{ published: N, parentsNotified: M }`

### 6.4 Exam Timetable List + Detail

```
GET /api/v1/teacher/exam-timetable?className=&section=
GET /api/v1/teacher/exam-timetable/{id}
GET /api/v1/parent/child/{childId}/exam-timetable
```

### 6.5 Exam Syllabus Mapping

```
GET /api/v1/teacher/assessments/{id}/syllabus
PUT /api/v1/teacher/assessments/{id}/syllabus
  Body: { unitIds: [uuid, ...] }
GET /api/v1/parent/exam/{assessmentId}/syllabus
```

### 6.6 Request Syllabus (Parent → Teacher)

```
POST /api/v1/parent/exam/{assessmentId}/request-syllabus
```
- **Process**:
  1. Resolve assessment → class+section+subject
  2. Find subject teacher via `teacher_subject_assignments` (class+section+subject, active)
  3. If no subject teacher → find class teacher (isClassTeacher=true)
  4. Create or reuse a message thread between parent and teacher
  5. Post pre-filled message: "Please share the syllabus for [exam name] ([subject]) on [date]."
  6. Notify teacher via `Notify.toUsers()`
- **Returns**: `{ threadId, teacherId, teacherName }`

### 6.7 Parent Exam Calendar

```
GET /api/v1/parent/child/{childId}/exams
```
- **Returns**: Upcoming + past exams for the child's class, grouped by date
- Each item: `{ assessmentId, examName, subject, examDate, startTime, endTime, maxMarks, hasSyllabus, syllabusUnits: [...] }`
- Multiple exams per day supported (list, not single)

### 6.8 Exam Timetable Notice (Announcement Link)

```
POST /api/v1/teacher/exam-timetable/{id}/notice
```
- **Body**: `{ title, description, category: "exam" }`
- **Process**: Create `school_announcements` row linked to the timetable
- Parents see it in notifications + can open in announcement detail modal

---

## 7. Backend: Scheduled Jobs

### 7.1 ExamReminderJob (NEW)

**File**: `server/src/main/kotlin/.../feature/exam/ExamReminderJob.kt`

```
Schedule: Daily at 6:00 PM IST (12:30 UTC)
```

**Logic**:
1. Query `assessments` where `examDate = tomorrow` AND `status IN (scheduled, draft)` AND `isActive = true`
2. For each assessment:
   a. Resolve class+section → enrolled students → parent user IDs
   b. Query `exam_syllabus_mapping` → joined with `curriculum_units` → list of chapters/topics
   c. Build notification:
      - **With syllabus**: "Tomorrow: [Subject] exam. Topics: [ch1, ch2, ch3]. Good luck!"
      - **Without syllabus**: "Tomorrow: [Subject] exam. Syllabus not yet shared. Tap to request."
   d. Send via `Notify.toUsers()` with:
      - `category = "exam_reminder"`
      - `deepLink = "/parent/exam/{assessmentId}"`
      - `refType = "assessment"`, `refId = assessmentId`
3. Log to audit table

**Registration**: Add to `Application.kt` alongside other scheduled jobs.

### 7.2 ExamTimetablePublishJob (NEW, optional)

If a teacher schedules a timetable for future publish (e.g., publish at midnight), a job can handle deferred publish. This is optional — the teacher can also publish manually.

---

## 8. Shared Module: Models & API Client

### 8.1 New Domain Models

**File**: `shared/src/commonMain/.../feature/exam/domain/model/ExamModels.kt`

```kotlin
// Exam timetable
data class ExamTimetableDto(id, name, className, section, term, status, entries: List<ExamTimetableEntryDto>)
data class ExamTimetableEntryDto(id, examDate, startTime, endTime, subject, examName, maxMarks, room, assessmentId?)
data class ExamTimetableSlotDto(examDate, startTime, endTime, subject, examName, maxMarks)  // pre-publish
data class CreateExamTimetableRequest(className, section, name, term, slots: List<ExamTimetableSlotDto>)
data class ExamTimetableImportResponse(slots: List<ExamTimetableSlotDto>, rawText, aiUsed)

// Exam syllabus
data class ExamSyllabusDto(assessmentId, units: List<ExamSyllabusUnitDto>)
data class ExamSyllabusUnitDto(id, title, subject, isCovered)
data class UpdateExamSyllabusRequest(unitIds: List<String>)

// Parent exam view
data class ParentExamDto(assessmentId, examName, subject, examDate, startTime, endTime, maxMarks, hasSyllabus, syllabusUnits: List<String>)
data class ParentExamCalendarDto(exams: List<ParentExamDto>)

// Request syllabus
data class RequestSyllabusResponse(threadId, teacherId, teacherName)
```

### 8.2 New API Interface

**File**: `shared/src/commonMain/.../feature/exam/data/api/ExamApi.kt`

```kotlin
interface ExamApi {
    suspend fun importTimetableOcr(image: String, mimeType: String, className: String, section: String): NetworkResult<ExamTimetableImportResponse>
    suspend fun importTimetableText(text: String, className: String, section: String): NetworkResult<ExamTimetableImportResponse>
    suspend fun createTimetable(request: CreateExamTimetableRequest): NetworkResult<ExamTimetableDto>
    suspend fun publishTimetable(id: String): NetworkResult<PublishResult>
    suspend fun getTimetables(className: String, section: String): NetworkResult<List<ExamTimetableDto>>
    suspend fun getTimetable(id: String): NetworkResult<ExamTimetableDto>
    suspend fun getChildExams(childId: String): NetworkResult<ParentExamCalendarDto>
    suspend fun getExamSyllabus(assessmentId: String): NetworkResult<ExamSyllabusDto>
    suspend fun updateExamSyllabus(assessmentId: String, unitIds: List<String>): NetworkResult<Unit>
    suspend fun requestSyllabus(assessmentId: String): NetworkResult<RequestSyllabusResponse>
}
```

### 8.3 Repository + Koin Registration

- `ExamRepository` / `ExamRepositoryImpl` in shared
- Register in `Koin.kt` commonModule

---

## 9. Teacher Portal UI

### 9.1 Exam Timetable Upload Screen (NEW)

**File**: `composeApp/src/commonMain/.../ui/v2/screens/teacher/ExamTimetableUploadScreen.kt`

**Flow**:
1. **Pick class + section** (from teacher's assignments)
2. **Upload photo** → camera or gallery (reuse `MediaPicker` platform)
3. **AI processes** → shows extracted slots in editable list
4. **Edit slots** → teacher can fix dates/times/subjects/marks
5. **Name the timetable** → "Mid Term 2026" etc.
6. **Optional: Attach syllabus notice** → checkbox "Send notice to parents with syllabus request"
7. **Publish** → creates calendar events + assessments + sends notification

**UI Components**:
- Photo upload card with preview
- Extracted slots list (editable rows: date picker, time picker, subject text, marks number)
- "Add slot" / "Remove slot" buttons
- Publish button with confirmation dialog
- Loading state during AI OCR

### 9.2 Exam Syllabus Mapping Screen (NEW)

**File**: `composeApp/src/commonMain/.../ui/v2/screens/teacher/ExamSyllabusMappingScreen.kt`

**Flow**:
1. Teacher selects an assessment/exam
2. Shows curriculum units for the subject (from `CurriculumUnitsTable`)
3. Multi-select checkboxes for chapters/topics to include
4. Save → creates `exam_syllabus_mapping` rows

### 9.3 Exam Detail Sheet (NEW)

**File**: `composeApp/src/commonMain/.../ui/v2/screens/teacher/ExamDetailSheet.kt`

**Shows**:
- Exam name, subject, date, time, max marks
- Linked syllabus units (with "Edit" button → mapping screen)
- Marks entry status (entered k/n)
- "Enter Marks" CTA → navigates to existing Gradebook marks grid
- "Publish Marks" button (if marks_pending)

### 9.4 Exam Timetable List (NEW)

**File**: `composeApp/src/commonMain/.../ui/v2/screens/teacher/ExamTimetableListScreen.kt`

- List of timetables for teacher's classes
- Each row: name, class, term, status, entry count
- Tap → detail view with all entries

### 9.5 Integration into Teacher Portal

Add to `TeacherPortalV2.kt`:
- New overlay: `TeacherOverlay.ExamTimetableUpload`
- New overlay: `TeacherOverlay.ExamSyllabusMapping`
- New overlay: `TeacherOverlay.ExamDetail`
- Deep link: `exam/{assessmentId}` → opens ExamDetailSheet
- Deep link: `exam-timetable/upload` → opens upload screen
- HOME tab CTA: "Upload Exam Timetable" button (seasonal/contextual)

---

## 10. Parent Portal UI

### 10.1 Exam Detail Modal (NEW)

**File**: `composeApp/src/commonMain/.../ui/v2/screens/parent/ExamDetailModal.kt`

**Shows**:
- Exam name, subject, date, time, max marks
- **Syllabus section**:
  - If syllabus mapped: list of chapters/topics to study
  - If no syllabus: "Syllabus not yet shared" + **"Request Syllabus" button**
- **Request Syllabus button**:
  - Tapping → calls `POST /api/v1/parent/exam/{id}/request-syllabus`
  - Shows confirmation: "Request sent to [Teacher Name]"
  - Opens message thread to teacher
- If marks published: shows score, grade, trend
- If marks not published: "Results pending"

**Entry points**:
- From notification deep link `/parent/exam/{assessmentId}`
- From exam calendar view
- From academics Marks tab → tap an exam

### 10.2 Exam Calendar View (NEW)

**File**: `composeApp/src/commonMain/.../ui/v2/screens/parent/ExamCalendarView.kt`

**Shows**:
- Horizontal date timeline (next 2 weeks)
- Dots/badges on dates with exams
- Tap a date → list of exams that day (multiple supported)
- Each exam card: subject, time, max marks, syllabus status indicator
- Tap exam card → opens ExamDetailModal

**Integration**: Add as a new tab or section in `ParentAcademicsScreenV2` (replace or augment the Marks tab with "Exams" tab showing calendar + marks).

### 10.3 Announcement Detail Modal (NEW)

**File**: `composeApp/src/commonMain/.../ui/v2/screens/parent/AnnouncementDetailModal.kt`

- Reuses `SchoolAnnouncementsViewModel`
- Shows announcement title, date, category, description
- If linked to an exam: shows "View Exam Details" button
- Accessible from notifications list, exam timetable publish notice, etc.

### 10.4 Integration into Parent Portal

Add to `ParentPortalV2.kt`:
- New overlay: `ParentOverlay.ExamDetail`
- New overlay: `ParentOverlay.AnnouncementDetail`
- Deep link: `/parent/exam/{assessmentId}` → opens ExamDetailModal
- Deep link: `/parent/announcement/{id}` → opens AnnouncementDetailModal
- Academics tab: add "Exams" sub-tab between "Marks" and "Syllabus"

---

## 11. Admin Portal UI

### 11.1 Exam Timetable Overview (NEW)

**File**: `composeApp/src/commonMain/.../ui/v2/screens/admin/AdminExamTimetableScreen.kt`

- List all exam timetables across school
- Filter by class, term, status
- View entries, override publish status
- Create timetable on behalf of a teacher (admin privilege)

### 11.2 Exam Analytics (NEW)

- Class-wise performance summary
- Subject-wise average across exams
- Exam-wise pass/fail rate
- Trend across terms

---

## 12. Notification & Modal System

### 12.1 Notification Types

| Event | Category | Recipients | Deep Link | Body |
|-------|----------|-----------|-----------|------|
| Timetable published | `exam_timetable` | Parents of class | `/parent/exams` | "Exam timetable published: [name]" |
| Exam reminder (evening before) | `exam_reminder` | Parents of class | `/parent/exam/{id}` | "Tomorrow: [subject] exam. Topics: [chapters]" or "Tomorrow: [subject] exam. Tap to request syllabus." |
| Marks published | `marks` | Parents of class | `/parent/academics/marks` | "Marks for [exam] ([subject]) published." (ALREADY EXISTS) |
| Syllabus request | `syllabus_request` | Subject/class teacher | `/teacher/messages/{threadId}` | "[Parent] requested syllabus for [exam]" |
| Syllabus shared | `syllabus_shared` | Parents of class | `/parent/exam/{id}` | "Syllabus updated for [exam] ([subject])" |

### 12.2 Modal System (All Portals)

Every notification that opens a modal must:
1. Parse the deep link
2. Navigate to the correct overlay/screen
3. Load the referenced entity by ID
4. Show the modal with full details
5. Support back/close

**Existing modals that work**:
- `TeacherAnnouncementDetailScreen` — teacher announcement detail (deep link works)

**New modals needed**:
- `ExamDetailModal` (parent) — exam info + syllabus + request button
- `ExamDetailSheet` (teacher) — exam info + syllabus editor + marks CTA
- `AnnouncementDetailModal` (parent) — announcement detail (mirror of teacher's)
- `AnnouncementDetailSheet` (admin) — announcement detail (admin view)

### 12.3 Notice/Announcement Openable Everywhere

**Requirement**: All notices/announcements must be openable in a modal at all portals (parent, teacher, admin).

**Implementation**:
- `AnnouncementDetailModal` is a shared composable that takes an `announcementId`
- Each portal renders it as an overlay
- Deep link `/announcements/{id}` works in all three portals
- The modal fetches the announcement from `SchoolAnnouncementsViewModel` (already shared)
- If announcement is linked to an exam (via `refType`/`refId`), show "View Exam" button

---

## 13. AI OCR Pipeline

### 13.1 Exam Timetable OCR Flow

```
Teacher uploads photo
    │
    ▼
POST /api/v1/teacher/exam-timetable/import-ocr
    │
    ├─ Validate: max 10MB, mime whitelist (jpeg/png/webp/gif)
    │
    ├─ AiService.completeWithVision(
    │     feature = "exam_timetable_ocr",
    │     systemPrompt = EXAM_TIMETABLE_OCR_PROMPT,
    │     userText = "Extract the exam timetable from this image.",
    │     imageBase64 = req.image,
    │     imageMimeType = req.mimeType,
    │     schoolId = ctx.schoolId,
    │     temperature = 0.2,
    │     maxTokens = 4096
    │  )
    │
    ├─ Parse AI output → List<ExamTimetableSlotDto>
    │     Format: "YYYY-MM-DD | HH:MM-HH:MM | Subject | Exam Name | Max Marks"
    │     Parser: regex per line, split by "|"
    │
    ├─ If AI fails → return error (no fallback regex for exam timetables — too varied)
    │
    └─ Return { slots, rawText, aiUsed: true }
```

### 13.2 Reuse Existing Infrastructure

- `AiService.completeWithVision()` — already has circuit breaker, rate limiter, guardrails, failover (Gemini → OpenRouter)
- `fetchImageAsBase64()` — already has SSRF protection (SEC-011)
- Image upload pattern — same as `TimetableImportRouting` and `IngestRouting`
- R2 storage — for storing the source image (optional, for audit)

### 13.3 AI Prompt Engineering

The exam timetable OCR prompt must handle:
- Tabular layouts (date columns, subject columns)
- Multiple exams per day
- Varying formats (Indian schools use diverse layouts)
- Hindi/English mixed timetables
- Missing max marks (default to 100)
- Missing time slots (default to 00:00-00:00)

---

## 14. Implementation Phases

### Phase 1: Database + Backend Core (3-4 days)

1. Create migration SQL: `exam_timetables`, `exam_timetable_entries`, `exam_syllabus_mapping`
2. Add Exposed table objects to `Tables.kt`
3. Create `ExamTimetableRouting.kt` — import-ocr, import-text, create, publish, list, detail
4. Create `ExamSyllabusRouting.kt` — get, update, parent read
5. Create `ExamReminderJob.kt` — scheduled at 6 PM IST
6. Register routes in `Application.kt`
7. Register job in `Application.kt`
8. Seed test data

### Phase 2: Shared Module (2 days)

1. Create `ExamModels.kt` — all DTOs
2. Create `ExamApi.kt` — interface
3. Create `ExamApiImpl.kt` — Ktor implementation
4. Create `ExamRepository.kt` + `ExamRepositoryImpl.kt`
5. Create `ExamViewModel.kt` (teacher) + `ParentExamViewModel.kt`
6. Register in `Koin.kt`

### Phase 3: Teacher Portal UI (3-4 days)

1. `ExamTimetableUploadScreen.kt` — photo upload + AI extraction + edit + publish
2. `ExamTimetableListScreen.kt` — list of timetables
3. `ExamSyllabusMappingScreen.kt` — map curriculum units to exam
4. `ExamDetailSheet.kt` — exam info + marks CTA
5. Integrate into `TeacherPortalV2.kt` — overlays, deep links, HOME CTA
6. Wire up `ExamViewModel`

### Phase 4: Parent Portal UI (3-4 days)

1. `ExamDetailModal.kt` — exam info + syllabus + request button
2. `ExamCalendarView.kt` — timeline of upcoming exams
3. `AnnouncementDetailModal.kt` — shared announcement modal
4. Integrate into `ParentPortalV2.kt` — overlays, deep links
5. Add "Exams" tab to `ParentAcademicsScreenV2`
6. Wire up `ParentExamViewModel`

### Phase 5: Notification + Modal Polish (2 days)

1. Verify all notification deep links open the correct modal
2. Test exam reminder job end-to-end
3. Test "request syllabus" → message thread flow
4. Test announcement modal in all three portals
5. Test multiple exams per day in calendar view

### Phase 6: Admin Portal + Analytics (2 days, optional)

1. `AdminExamTimetableScreen.kt` — school-wide view
2. Exam analytics dashboard
3. Bulk publish/retract

### Phase 7: Testing + Seed (1-2 days)

1. Seed: create exam timetable for test school
2. Test: OCR → publish → reminder → marks → publish → parent view
3. Test: request syllabus flow
4. Test: announcement modal in all portals
5. Regression: existing gradebook still works

---

## 15. File Inventory: Existing vs New

### Existing Files (Modified)

| File | Change |
|------|--------|
| `server/.../db/Tables.kt` | Add 3 new table objects |
| `server/.../Application.kt` | Register exam routes + ExamReminderJob |
| `shared/.../di/Koin.kt` | Register ExamApi, ExamRepository, ViewModels |
| `composeApp/.../TeacherPortalV2.kt` | Add exam overlays + deep links + HOME CTA |
| `composeApp/.../ParentPortalV2.kt` | Add exam overlay + deep links |
| `composeApp/.../ParentAcademicsScreenV2.kt` | Add "Exams" tab |
| `server/.../feature/teacher/TeacherGradebookRouting.kt` | No change (already works) |
| `server/.../feature/parent/ParentAcademicsRouting.kt` | No change (marks read already works) |

### New Files — Backend

| File | Purpose |
|------|---------|
| `server/.../feature/exam/ExamTimetableRouting.kt` | Import OCR/text, create, publish, list, detail |
| `server/.../feature/exam/ExamSyllabusRouting.kt` | Get/update syllabus mapping, parent read |
| `server/.../feature/exam/ExamReminderJob.kt` | Daily 6 PM IST reminder job |
| `server/.../feature/exam/ExamRequestSyllabusRouting.kt` | Parent → teacher syllabus request |
| `server/.../feature/exam/ExamTimetableParser.kt` | Parse AI output → structured slots |
| `database/migrations/setup_exam_ecosystem_schema.sql` | 3 new tables |

### New Files — Shared

| File | Purpose |
|------|---------|
| `shared/.../feature/exam/domain/model/ExamModels.kt` | All DTOs |
| `shared/.../feature/exam/data/api/ExamApi.kt` | API interface |
| `shared/.../feature/exam/data/api/ExamApiImpl.kt` | Ktor implementation |
| `shared/.../feature/exam/data/repository/ExamRepository.kt` | Repository interface |
| `shared/.../feature/exam/data/repository/ExamRepositoryImpl.kt` | Implementation |
| `shared/.../feature/exam/presentation/ExamViewModel.kt` | Teacher VM |
| `shared/.../feature/exam/presentation/ParentExamViewModel.kt` | Parent VM |

### New Files — Compose App (Teacher)

| File | Purpose |
|------|---------|
| `composeApp/.../ui/v2/screens/teacher/ExamTimetableUploadScreen.kt` | Upload + OCR + edit + publish |
| `composeApp/.../ui/v2/screens/teacher/ExamTimetableListScreen.kt` | List timetables |
| `composeApp/.../ui/v2/screens/teacher/ExamSyllabusMappingScreen.kt` | Map syllabus to exam |
| `composeApp/.../ui/v2/screens/teacher/ExamDetailSheet.kt` | Exam info + marks CTA |

### New Files — Compose App (Parent)

| File | Purpose |
|------|---------|
| `composeApp/.../ui/v2/screens/parent/ExamDetailModal.kt` | Exam info + syllabus + request button |
| `composeApp/.../ui/v2/screens/parent/ExamCalendarView.kt` | Timeline of upcoming exams |
| `composeApp/.../ui/v2/screens/parent/AnnouncementDetailModal.kt` | Shared announcement modal |

### New Files — Compose App (Admin, optional Phase 6)

| File | Purpose |
|------|---------|
| `composeApp/.../ui/v2/screens/admin/AdminExamTimetableScreen.kt` | School-wide exam view |

---

## Summary: What Works vs What Needs Building

### ✅ Already Working (No Changes Needed)

- Teacher creates individual assessments (tests/exams) with name, type, max marks, pass marks, exam date
- Teacher enters marks per student in a grid
- Teacher saves marks (status → marks_pending, NOT visible to parents)
- Teacher publishes marks → push notification to parents → parents see marks
- Parent views published marks with trend sparkline
- Assessment lifecycle: draft → scheduled → marks_pending → published → archived
- AI vision OCR infrastructure (Gemini + OpenRouter, circuit breaker, rate limiter)
- Notification system with deep links
- Academic calendar with EXAM event type
- Report card system reads from assessment marks

### 🔧 Needs Building

- **Exam timetable upload (OCR)** — new endpoint + AI prompt + parser
- **Bulk exam creation from timetable** — new endpoint that creates calendar events + assessments
- **Exam syllabus mapping** — new table + API to attach chapters/topics to an exam
- **Evening exam reminder job** — new scheduled job at 6 PM IST
- **Parent exam detail modal** — new screen with syllabus + "request syllabus" button
- **Parent exam calendar view** — new screen showing upcoming exams timeline
- **Request syllabus flow** — new endpoint that creates a message thread to the teacher
- **Announcement detail modal (parent)** — mirror of existing teacher announcement detail
- **Teacher exam timetable upload screen** — new UI with photo upload + AI extraction + edit
- **Teacher exam syllabus mapping screen** — new UI for mapping curriculum units to exams
- **Exam timetable as first-class object** — new table grouping assessments into a named set

### ⚠️ Partially Working (Minor Extensions)

- Announcement/notice system exists for teachers but needs parent + admin modals
- Notification deep links work but need new paths for exam detail + announcement detail
- Parent academics screen has Marks tab but no Exams/calendar tab
