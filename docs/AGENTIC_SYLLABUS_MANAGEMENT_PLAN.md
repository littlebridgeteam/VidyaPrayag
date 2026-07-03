# Agentic Syllabus Management & AI Assignment System — Master Plan

> **Feature:** AI-powered syllabus lifecycle (upload → auto-divide into topics/subtopics → daily check-in → pace monitoring → parent summary) + Agentic assignment generator (topic-complete → AI quiz → student submission → ranked results)
> **Status:** PLANNING → BUILD READY
> **Last updated:** 2026-07-03 (v1)
> **Target:** $100M Series-A grade school operating system

---

## 0. Executive Summary

This plan delivers **two deeply interconnected AI-agentic subsystems** that transform syllabus management from a manual checkbox exercise into an intelligent, self-monitoring system.

### The Two Subsystems

1. **Agentic Syllabus Lifecycle** — Teachers upload syllabus as image or text; AI divides it into chapters → topics → subtopics. After each class, a non-mandatory popup asks "what did you teach today?" The system evaluates progress against the academic calendar, warns teachers/admins if behind schedule, uses AI reconfirmation before sending alarms. If teacher doesn't populate, AI fills with estimated daily percentages (clearly labeled). Daily parent summaries appear on the home tab. Teachers can edit and delete entries.

2. **Agentic Assignment Generation** — When a topic is marked complete, teacher can generate an assignment (quiz, MCQs, fill-in-the-blanks, true/false) with one tap. AI creates questions + answers with difficulty adjusted ±10% for class level. Children answer from parent's phone. Results are ranked, sent to parents, visible in admin's class section. AI uses curated internet sources (NCERT, CBSE, Kaggle, HuggingFace, GitHub) in the system prompt.

### Design Philosophy

- Be **role-aware** (Parent, Teacher, School Admin) with clean separation
- Follow **SOLID + MVVM + Clean Architecture** (per `DEVELOPMENT_STANDARDS.md`)
- Be **offline-resilient** (Room cache for reads, outbox for writes)
- Be **accessible to 25-60 year old parents** (large touch targets, clear language, minimal steps)
- **Maximize reuse** — existing screens, components, APIs are extended, not duplicated
- **Integrate invisibly** — every meaningful action emits events, feeds notifications, updates cross-surface views
- **Respect AI cost constraints** — use free-tier providers, cache aggressively, batch where possible
- **Never block the teacher** — all AI features degrade gracefully; manual paths always exist

---

## 1. Deep System Audit

### 1.1 Existing Syllabus Infrastructure

| Layer | File | Status |
|---|---|---|
| **DB** | `Tables.kt:1059` — `CurriculumUnitsTable` | ✅ Typed template: chapter→topic via `parentId` self-FK, `classId` + `subjectId` typed scope |
| **DB** | `Tables.kt:1079` — `SyllabusProgressTable` | ✅ Per-section coverage: `unitId`, `section`, `assignmentId`, `isCovered`, `coveredOn`, `coveredBy` |
| **DB** | `Tables.kt:1030` — `SyllabusUnitsTable` (legacy) | ✅ RETAINED — parent/admin readers still use it. Migration to typed tables is part of this plan. |
| **DB** | `Tables.kt:1752` — `AcademicYearsTable` | ✅ Academic year with `startDate`, `endDate`, `status` |
| **DB** | `Tables.kt:1194` — `TeacherPeriodsTable` | ✅ Weekly recurring periods — basis for counting class sessions |
| **DB** | `Tables.kt:1110` — `HomeworkTable` | ✅ Homework with `assignmentId`, `curriculumUnitId` (nullable), `status` lifecycle |
| **DB** | `Tables.kt:1136` — `HomeworkAttachmentsTable` | ✅ File/image attachments — reuse pattern for syllabus source images |
| **DB** | `Tables.kt:1159` — `HomeworkSubmissionsTable` | ✅ Student submissions with `status`, `submittedAt`, `marks` |
| **Server** | `TeacherSyllabusRouting.kt` | ✅ Full CRUD: load, create units, rename, toggle coverage. Scoped via `requireOwnedAssignment`. |
| **Server** | `ParentAcademicsRouting.kt:358` | ✅ Parent read — but reads from legacy `SyllabusUnitsTable`, needs migration |
| **Server** | `SchoolAnalyticsRouting.kt` | ✅ Admin coverage analytics — reads from legacy table |
| **Server** | `TeacherHomeworkRouting.kt` | ✅ Homework lifecycle: assign, extend, review, close |
| **Server** | `AiService.kt:134` | ✅ AI gateway: lane-based routing, PII guardrails, caching, circuit breaker |
| **Server** | `LlmClient.kt:293` | ✅ OpenAI-compatible client: `complete()` + `completeWithVision()` |
| **Server** | `KeyVault.kt` | ✅ 9 AI providers with free-tier configs |
| **Server** | `Notify.kt` + `NotifyRecipients.kt` | ✅ Notification spine + recipient resolvers |
| **Server** | `AcademicCalendarCore.kt` | ✅ Calendar events, academic year binding |
| **Shared** | `TeacherSyllabusViewModel.kt` | ✅ VM: load, toggle (optimistic), edit mode, add/rename |
| **Shared** | `ParentDashboardViewModel.kt` | ✅ VM: `coveredToday` computed from syllabus, `schoolDayEnded` |
| **App** | `TeacherSyllabusScreenV2.kt` | ✅ Syllabus list with toggle, edit, add, progress ring |
| **App** | `TeacherCheckInPopup.kt` | ✅ Visual pattern for syllabus check-in popup |
| **App** | `ParentCoveredDetailOverlay.kt` | ✅ Bottom sheet: covered topics + syllabus progress |
| **App** | `TeacherHomeworkScreenV2.kt` | ✅ Visual pattern for quiz results screen |

### 1.2 Gap Analysis

| Gap | Impact | Priority |
|---|---|---|
| No AI syllabus parsing (image/text → topics) | Teachers manually type every chapter/topic | 🔴 Critical |
| No subtopic support (3-level hierarchy) | `parentId` self-FK exists but UI/API only handle 2 levels | 🔴 Critical |
| No syllabus source storage | No record of what was uploaded or AI-parsed | 🟡 High |
| No daily class log ("what was taught") | No structured record of daily teaching activity | 🔴 Critical |
| No AI daily summary for parents | Parents see covered topics but no human-readable summary | 🟡 High |
| No pace calculation or monitoring | System can't tell if syllabus is behind/ahead of schedule | 🔴 Critical |
| No pace alerts with AI reconfirmation | No warning before syllabus falls behind | 🔴 Critical |
| No teacher popup suppression prefs | No way to suppress daily check-in popup | 🟡 High |
| No quiz question bank | Assignments are free-text homework, no structured Q&A | 🔴 Critical |
| No AI quiz generation | Teacher manually creates every question | 🔴 Critical |
| No quiz submission (student via parent app) | Children can't answer structured quizzes | 🔴 Critical |
| No ranked quiz results | Teacher can't see class-wide quiz performance | 🟡 High |
| No DELETE endpoint for syllabus units | Teachers can rename but not delete | 🟡 High |
| No partial coverage (percentage) | Coverage is boolean — no 50% progress on a topic | 🟡 High |
| Parent syllabus reads from legacy table | Disconnected from typed `curriculum_units` teacher writes to | 🔴 Critical |
| No AI estimated daily percentage | When teacher doesn't log, parent sees nothing | 🟡 High |
| No "Generate Assignment" from completed topic | No bridge between syllabus completion and homework | 🔴 Critical |
| No admin pace alert view | Admin has no consolidated view of syllabus pace | 🟡 High |

### 1.3 Reuse Map

| Component | File | Reuse For |
|---|---|---|
| `CurriculumUnitsTable` | `Tables.kt:1059` | Syllabus template — extend UI/API to 3 levels via existing `parentId` |
| `SyllabusProgressTable` | `Tables.kt:1079` | Coverage tracking — add `coveragePercent` column |
| `HomeworkTable` | `Tables.kt:1110` | Quiz storage — add `isQuiz` + `quizMetaJson` |
| `HomeworkSubmissionsTable` | `Tables.kt:1159` | Quiz submissions — add `score` + `rank` |
| `HomeworkAttachmentsTable` pattern | `Tables.kt:1136` | Syllabus source image upload — same Supabase Storage pattern |
| `AiService.complete()` | `AiService.kt:134` | All AI calls — parse, summary, pace, quiz |
| `LlmClient.completeWithVision()` | `LlmClient.kt:293` | Image-based syllabus OCR + parsing |
| `Notify.toUsers()` + `NotifyRecipients` | `Notify.kt` | Pace alerts, quiz notifications |
| `TeacherSyllabusRouting.kt` | — | Extend with DELETE, parse, daily-log, popup-prefs |
| `TeacherSyllabusViewModel.kt` | — | Extend with upload, delete, subtopic, check-in |
| `TeacherSyllabusScreenV2.kt` | — | Extend with upload, subtopic, delete, generate quiz |
| `TeacherCheckInPopup.kt` | — | Visual pattern for `SyllabusCheckInPopup` |
| `ParentCoveredDetailOverlay.kt` | — | Extend with AI summary + estimation label |
| `ParentDashboardViewModel.kt` | — | Extend `coveredToday` with summary + `isAiEstimated` |
| `AcademicYearsTable` | `Tables.kt:1752` | Pace plan: total classes from academic year + periods |
| `TeacherPeriodsTable` | `Tables.kt:1194` | Pace plan: count weekly periods per subject |
| `VTheme`, `VButton`, `VCard`, `VInput`, `VIcons` | `ui/v2/components/` | All UI primitives |

---

## 2. UX Analysis — All Three Portals

### 2.1 Teacher

#### 2.1.1 Syllabus Upload — Image/Text → AI Parse

```
Teacher opens Syllabus tab → taps "Upload Syllabus"
  ↓
Choose input method:
  ┌──────────────────────────────────────────┐
  │  UPLOAD SYLLABUS                          │
  │                                           │
  │  ┌───────────┐    ┌───────────┐          │
  │  │ 📷 Image   │    │ 📝 Text    │          │
  │  │ Upload     │    │ Paste text │          │
  │  └───────────┘    └───────────┘          │
  │  Class: Grade 7   Subject: Mathematics    │
  └──────────────────────────────────────────┘
  ↓
A: Image → camera/gallery → Supabase Storage → AI vision parse
B: Text → paste syllabus text → AI text parse
  ↓
AI returns structured hierarchy:
  ┌──────────────────────────────────────────┐
  │  PARSED SYLLABUS — Review & Confirm       │
  │                                           │
  │  📁 Chapter 1: Integers                   │
  │    📄 Topic 1.1: Addition of Integers     │
  │      📄 Subtopic: Properties of Addition  │
  │    📄 Topic 1.2: Subtraction of Integers  │
  │  📁 Chapter 2: Fractions                  │
  │    📄 Topic 2.1: Types of Fractions       │
  │    📄 Topic 2.2: Operations on Fractions  │
  │      📄 Subtopic: Addition & Subtraction  │
  │      📄 Subtopic: Multiplication & Div    │
  │                                           │
  │  [Edit] [Add More] [Confirm & Save]       │
  └──────────────────────────────────────────┘
  ↓
[Confirm & Save] → bulk insert into curriculum_units (3-level)
  → Create syllabus_pace_plan (AI estimates total classes needed)
```

#### 2.1.2 Daily Check-In Popup — "What did you teach today?"

```
After a class period ends (from teacher_periods + current time):
  ┌──────────────────────────────────────────┐
  │         📚 What did you teach today?      │
  │         Grade 7-A · Mathematics           │
  │                                           │
  │    This is not mandatory.                 │
  │                                           │
  │  Topics covered today:                    │
  │  ☑ Topic 1.1: Addition of Integers        │
  │  ☑ Topic 1.2: Subtraction of Integers     │
  │  ☐ Topic 2.1: Types of Fractions          │
  │                                           │
  │  Coverage: [████████░░░░░░] 60%           │
  │                                           │
  │  Summary (optional):                      │
  │  ┌─────────────────────────────────────┐  │
  │  │ Taught addition and subtraction     │  │
  │  │ with real-world examples.           │  │
  │  └─────────────────────────────────────┘  │
  │                                           │
  │  [Save]  [Not today]  [Don't show this    │
  │                       week]               │
  │  ⚙ Never show (Settings)                 │
  └──────────────────────────────────────────┘
```

Key behaviors:
- **Trigger**: After end time of a teacher's period, if no `daily_class_log` exists for today + this assignment
- **"Not today"**: Dismisses for today only
- **"Don't show this week"**: Sets `suppress_mode = 'week'`, `suppressed_until = today + 7 days`
- **"Never show"**: Sets `suppress_mode = 'permanent'` — re-enable from Settings
- **"This is not mandatory"**: Always visible in muted text
- **Visual pattern**: Matches `TeacherCheckInPopup` — scrim + scale-in card + VTheme

#### 2.1.3 Syllabus Screen — Extended with Subtopics + Delete + Generate Quiz

```
Teacher opens Syllabus tab:
  ┌──────────────────────────────────────────┐
  │  SYLLABUS                          [Edit] │
  │  ╭─ 60% covered ─────────────────────╮   │
  │  │ 3 of 5 units covered              │   │
  │  ╰───────────────────────────────────╯   │
  │  [📤 Upload Syllabus]                     │
  │                                           │
  │  ✅ Chapter 1: Integers                   │
  │    ✅ Topic 1.1: Addition of Integers     │
  │      ✅ Subtopic: Properties of Addition  │
  │    ✅ Topic 1.2: Subtraction of Integers  │
  │  ⬜ Chapter 2: Fractions                  │
  │    ⬜ Topic 2.1: Types of Fractions       │
  │    ⬜ Topic 2.2: Operations on Fractions  │
  │      ⬜ Subtopic: Addition & Subtraction  │
  │      ⬜ Subtopic: Multiplication & Div    │
  │                                           │
  │  ── Edit Mode (when Edit toggled) ──      │
  │  [Add Chapter] [🗑 Delete] [Generate Quiz]│
  └──────────────────────────────────────────┘
```

In Edit mode:
- **Delete** button on each unit (with confirm dialog)
- **Generate Quiz** button on covered topics → opens quiz generation sheet
- **Add Chapter/Topic/Subtopic** — existing add flow, extended for 3-level depth

#### 2.1.4 Generate Quiz Flow

```
Teacher taps "Generate Quiz" on a covered topic:
  ┌──────────────────────────────────────────┐
  │  GENERATE ASSIGNMENT                      │
  │                                           │
  │  From: Topic 1.1: Addition of Integers    │
  │  Class: Grade 7-A                         │
  │                                           │
  │  Question types:                          │
  │  ☑ MCQ (Multiple Choice)                  │
  │  ☑ Fill in the Blanks                     │
  │  ☑ True / False                           │
  │                                           │
  │  Number of questions: [10]                │
  │  Difficulty: [Class Level ± 0%]           │
  │    [-10% Easier] [0% Standard] [+10% Hard]│
  │                                           │
  │  Due date: [Tomorrow]                     │
  │                                           │
  │  [Generate with AI]                       │
  └──────────────────────────────────────────┘
  ↓
AI generates quiz → teacher reviews:
  ┌──────────────────────────────────────────┐
  │  QUIZ PREVIEW — Review before publishing  │
  │                                           │
  │  Q1 (MCQ): What is (-5) + 3?              │
  │    A) -2  B) 2  C) -8  D) 8               │
  │    Correct: A) -2                         │
  │    Explanation: When adding integers      │
  │    with different signs, subtract         │
  │    smaller from larger.                   │
  │                                           │
  │  Q2 (Fill): (-7) - (-3) = ___             │
  │    Correct: -4                            │
  │                                           │
  │  Q3 (T/F): The sum of two negative        │
  │  integers is always positive.             │
  │    Correct: False                         │
  │                                           │
  │  [Edit Questions] [Regenerate] [Publish]  │
  └──────────────────────────────────────────┘
  ↓
[Publish] → creates homework row (isQuiz=true) + quiz_questions rows
  → notifies class parents: "New quiz: Addition of Integers"
```

#### 2.1.5 Teacher Quiz Results View

```
Teacher opens Homework tab → taps quiz assignment:
  ┌──────────────────────────────────────────┐
  │  QUIZ RESULTS — Addition of Integers      │
  │  Grade 7-A · 32 students · 28 submitted   │
  │                                           │
  │  RANK  STUDENT        SCORE   %           │
  │  1     Rahul Sharma    10/10   100%  🏆   │
  │  2     Priya Patel      9/10    90%       │
  │  3     Amit Kumar       9/10    90%       │
  │  4     Sneha Gupta      8/10    80%       │
  │  ...                                     │
  │  28    John Doe         3/10    30%       │
  │  ⬜ 4 not submitted                      │
  │                                           │
  │  CLASS AVERAGE: 72%                       │
  │                                           │
  │  QUESTION BREAKDOWN:                      │
  │  Q1: 26/28 correct (93%)  ✅ Easy         │
  │  Q2: 14/28 correct (50%)  ⚠ Review        │
  │  Q3: 22/28 correct (79%)  ✅ OK           │
  │                                           │
  │  [Send Results to Parents]                │
  └──────────────────────────────────────────┘
```

#### 2.1.6 Teacher Settings — Popup Suppression

```
Teacher opens Profile → Settings:
  ┌──────────────────────────────────────────┐
  │  SETTINGS                                 │
  │                                           │
  │  Syllabus Check-in Reminders              │
  │  ┌─────────────────────────────────────┐  │
  │  │ ○ Show after every class            │  │
  │  │ ○ Don't show this week              │  │
  │  │ ● Never show (disabled)             │  │
  │  └─────────────────────────────────────┘  │
  │  "You can re-enable anytime from Settings"│
  └──────────────────────────────────────────┘
```

### 2.2 Parent

#### 2.2.1 Daily Summary on Home Tab

```
Parent opens Home tab → sees "Today's Learning" card:
  ┌──────────────────────────────────────────┐
  │  TODAY'S LEARNING                         │
  │  Monday, July 3                            │
  │                                           │
  │  📚 Mathematics                            │
  │  "Addition and Subtraction of Integers    │
  │  with real-world examples. Students       │
  │  practiced 10 problems."                  │
  │  Coverage: 60% of syllabus                │
  │                                           │
  │  📗 Science                                │
  │  "Photosynthesis process and its          │
  │  importance in the food chain."           │
  │  Coverage: 45% of syllabus                │
  │  ℹ AI Estimated                           │
  │                                           │
  │  [View Full Syllabus]                     │
  └──────────────────────────────────────────┘
```

Key behaviors:
- **Teacher-populated**: When `daily_class_log` exists with `source = 'teacher'`, show teacher's summary text. No estimation label.
- **AI-estimated**: When no teacher log exists, show AI-estimated summary from `syllabus_pace_plan`. Display "ℹ AI Estimated" badge in muted text.
- **AI recalculation**: Only recalculated if teacher pace deviates >20% from plan (flag in `pace_plan`). Otherwise pre-calculated value is served.
- **Tap "View Full Syllabus"**: Opens existing `ParentCoveredDetailOverlay` with per-subject progress.

#### 2.2.2 Quiz Answering Flow

```
Parent receives notification: "New quiz: Addition of Integers"
  ↓
Parent opens Academics tab → "Pending Quizzes" section
  ↓
Tap quiz → quiz screen:
  ┌──────────────────────────────────────────┐
  │  QUIZ — Addition of Integers              │
  │  Grade 7-A · Due: Tomorrow                │
  │                                           │
  │  Q1/10: What is (-5) + 3?                 │
  │                                           │
  │  ○ A) -2                                  │
  │  ○ B) 2                                   │
  │  ● C) -8                                  │
  │  ○ D) 8                                   │
  │                                           │
  │  [Previous]              [Next →]         │
  │                                           │
  │  Progress: [█░░░░░░░░░] 1/10              │
  └──────────────────────────────────────────┘
  ↓
After all questions answered → [Submit]
  ↓
  ┌──────────────────────────────────────────┐
  │  QUIZ SUBMITTED!                          │
  │                                           │
  │  Score: 8/10 (80%)                        │
  │  Rank: 4th in class                       │
  │                                           │
  │  Q1: ✅ Correct  Q2: ❌ Incorrect         │
  │  Q3: ✅ Correct  Q4: ✅ Correct           │
  │  ...                                      │
  │                                           │
  │  [Review Answers]  [Done]                 │
  └──────────────────────────────────────────┘
```

Question types rendered:
- **MCQ**: Radio button list (A/B/C/D)
- **Fill in the Blanks**: Text input field
- **True/False**: Two-button toggle

#### 2.2.3 Quiz Result Notification

```
Parent receives push: "Quiz results: Rahul scored 8/10 (Rank 4th)"
  ↓
Tap → opens quiz result screen (same as post-submit view)
```

### 2.3 School Admin

#### 2.3.1 Syllabus Pace Alerts

```
Admin opens Records tab → "Syllabus Pace" section:
  ┌──────────────────────────────────────────┐
  │  SYLLABUS PACE ALERTS                     │
  │                                           │
  │  ⚠ BEHIND SCHEDULE                        │
  │  Grade 7-A · Mathematics · John D.        │
  │  Expected: 50%  Actual: 30%               │
  │  AI Confirmed: Yes                        │
  │  [View Syllabus] [Contact Teacher]        │
  │                                           │
  │  ⚠ CRITICAL                               │
  │  Grade 8-B · Science · Mary S.            │
  │  Expected: 60%  Actual: 20%               │
  │  AI Confirmed: Yes                        │
  │  [View Syllabus] [Contact Teacher]        │
  │                                           │
  │  ✅ ON TRACK                              │
  │  Grade 6-A · English · Bob T.             │
  │  Expected: 45%  Actual: 48%               │
  └──────────────────────────────────────────┘
```

Key behaviors:
- **Alert levels**: `BEHIND` (actual < expected - 15%), `CRITICAL` (actual < expected - 30%), `AHEAD` (actual > expected + 15%)
- **AI Confirmed badge**: Shows that AI reconfirmation passed before alert was sent. Prevents false alarms.
- **[Contact Teacher]**: Deep links to messaging with teacher pre-selected
- **[View Syllabus]**: Deep links to class syllabus coverage view

#### 2.3.2 Class Syllabus Coverage View

```
Admin taps a class → sees syllabus coverage:
  ┌──────────────────────────────────────────┐
  │  SYLLABUS COVERAGE — Grade 7-A            │
  │                                           │
  │  Subject      Coverage   Pace   Status    │
  │  Maths        60%        50%    ⚠ Behind  │
  │  Science      45%        45%    ✅ On Track│
  │  English      80%        50%    ✅ Ahead   │
  │  Social St.   30%        40%    ⚠ Behind  │
  │                                           │
  │  Overall: 54% (Expected: 46%)             │
  └──────────────────────────────────────────┘
```

### 2.4 Edge Cases

| Scenario | Handling |
|---|---|
| AI parse returns empty/unparseable syllabus | UI shows "Couldn't parse syllabus. Please enter manually." → falls back to manual entry |
| Teacher uploads duplicate syllabus | Server checks if `curriculum_units` already exist for this assignment → warns before overwrite |
| Teacher doesn't populate daily log for a week | AI estimation continues from pace_plan. No notification spam — weekly digest only. |
| AI pace alert fires but teacher is actually ahead | AI reconfirmation catches this — second LLM pass validates before sending alarm |
| Quiz has 0 questions after AI generation | Server rejects with error → teacher can regenerate or create manually |
| Student submits quiz after due date | Marked as "late" in submission, still scored but flagged in results |
| Parent has multiple children in different classes | Each child sees their own class's quizzes. No cross-class leakage. |
| Teacher deletes a unit that has progress records | Soft-delete unit. Progress records retained for audit. Parent view hides deleted. |
| AI provider is down during syllabus parse | Circuit breaker falls back to next provider. If all fail, manual entry fallback. |
| School has no academic year configured | Pace plan uses default 180 school days. Admin can configure later. |
| Coverage percentage set to 100% via daily log | Auto-marks all selected topics as `isCovered = true` in `syllabus_progress` |
| Teacher generates quiz for topic with subtopics | Quiz covers parent topic + all subtopics. Teacher can deselect specific subtopics. |
| Multiple teachers teach same subject to different sections | Each has own `assignmentId` → own syllabus, own pace plan, own daily logs |

---

## 3. Database Schema

### 3.1 New Table: `syllabus_sources`

Stores the raw syllabus upload (image URL or text) and the AI-parsed JSON result.

```sql
CREATE TABLE syllabus_sources (
    id              UUID PRIMARY KEY,
    school_id       UUID NOT NULL,
    assignment_id   UUID NOT NULL,               -- FK teacher_subject_assignments.id
    source_type     VARCHAR(8) NOT NULL,         -- IMAGE | TEXT
    source_url      TEXT,                        -- Supabase Storage URL (for IMAGE)
    raw_text        TEXT,                        -- pasted text (for TEXT) or OCR extracted
    parsed_json     TEXT NOT NULL DEFAULT '{}',  -- AI-returned structured hierarchy
    ai_provider     VARCHAR(32),                 -- which provider parsed it
    created_at      TIMESTAMP NOT NULL,
    updated_at      TIMESTAMP NOT NULL
);
CREATE INDEX idx_ss_assignment ON syllabus_sources(assignment_id);
CREATE INDEX idx_ss_school ON syllabus_sources(school_id);
```

### 3.2 New Table: `daily_class_log`

Structured record of what was taught in each class period. Source is either `teacher` (from popup) or `ai` (estimated).

```sql
CREATE TABLE daily_class_log (
    id              UUID PRIMARY KEY,
    school_id       UUID NOT NULL,
    assignment_id   UUID NOT NULL,               -- FK teacher_subject_assignments.id
    date            DATE NOT NULL,
    topic_ids       TEXT NOT NULL DEFAULT '[]',  -- JSON array of curriculum_units.id
    summary_text    TEXT DEFAULT '',
    coverage_pct    INTEGER NOT NULL DEFAULT 0,  -- 0-100
    source          VARCHAR(8) NOT NULL,         -- TEACHER | AI
    is_ai_estimated BOOLEAN NOT NULL DEFAULT false,
    created_at      TIMESTAMP NOT NULL,
    updated_at      TIMESTAMP NOT NULL
);
CREATE UNIQUE INDEX idx_dcl_assignment_date ON daily_class_log(assignment_id, date);
CREATE INDEX idx_dcl_school_date ON daily_class_log(school_id, date);
```

### 3.3 New Table: `syllabus_pace_plan`

AI-estimated pace plan per assignment. Calculated from total topics + total classes (from academic year + teacher_periods).

```sql
CREATE TABLE syllabus_pace_plan (
    id                      UUID PRIMARY KEY,
    school_id               UUID NOT NULL,
    assignment_id           UUID NOT NULL,       -- FK teacher_subject_assignments.id
    academic_year_id        UUID,                -- FK academic_years.id (nullable)
    total_topics            INTEGER NOT NULL DEFAULT 0,
    total_classes_expected  INTEGER NOT NULL DEFAULT 0,
    classes_elapsed         INTEGER NOT NULL DEFAULT 0,
    expected_coverage_pct   INTEGER NOT NULL DEFAULT 0,
    actual_coverage_pct     INTEGER NOT NULL DEFAULT 0,
    ai_estimate_json        TEXT DEFAULT '{}',   -- per-class breakdown, AI reasoning
    needs_recalc            BOOLEAN NOT NULL DEFAULT false,  -- true if teacher pace deviates >20%
    last_recalc_at          TIMESTAMP,
    created_at              TIMESTAMP NOT NULL,
    updated_at              TIMESTAMP NOT NULL
);
CREATE UNIQUE INDEX idx_spp_assignment ON syllabus_pace_plan(assignment_id);
CREATE INDEX idx_spp_school ON syllabus_pace_plan(school_id);
```

### 3.4 New Table: `syllabus_popup_prefs`

Teacher's suppression preferences for the daily check-in popup.

```sql
CREATE TABLE syllabus_popup_prefs (
    id              UUID PRIMARY KEY,
    teacher_id      UUID NOT NULL,               -- FK app_users.id
    assignment_id   UUID,                        -- FK teacher_subject_assignments.id (nullable = global)
    suppress_mode   VARCHAR(12) NOT NULL DEFAULT 'off',  -- off | week | permanent
    suppressed_until DATE,                       -- computed from mode + current date
    created_at      TIMESTAMP NOT NULL,
    updated_at      TIMESTAMP NOT NULL
);
CREATE UNIQUE INDEX idx_spp_teacher_assignment ON syllabus_popup_prefs(teacher_id, assignment_id);
```

### 3.5 New Table: `syllabus_pace_alerts`

Pace deviation alerts with AI reconfirmation. Created only after AI second-pass validates.

```sql
CREATE TABLE syllabus_pace_alerts (
    id              UUID PRIMARY KEY,
    school_id       UUID NOT NULL,
    assignment_id   UUID NOT NULL,               -- FK teacher_subject_assignments.id
    alert_level     VARCHAR(12) NOT NULL,        -- BEHIND | CRITICAL | AHEAD
    expected_pct    INTEGER NOT NULL,
    actual_pct      INTEGER NOT NULL,
    ai_confirmed    BOOLEAN NOT NULL DEFAULT false,
    ai_reconfirm_json TEXT DEFAULT '{}',         -- AI reasoning for confirmation
    notified_roles  TEXT NOT NULL DEFAULT '[]',  -- JSON array: ["teacher","admin","parents"]
    created_at      TIMESTAMP NOT NULL,
    resolved_at     TIMESTAMP                    -- null = active, set when pace recovers
);
CREATE INDEX idx_spa_school_active ON syllabus_pace_alerts(school_id)
    WHERE resolved_at IS NULL;
CREATE INDEX idx_spa_assignment ON syllabus_pace_alerts(assignment_id);
```

### 3.6 New Table: `quiz_questions`

Structured question bank for AI-generated quizzes. Linked to `homework` table.

```sql
CREATE TABLE quiz_questions (
    id              UUID PRIMARY KEY,
    homework_id     UUID NOT NULL,               -- FK homework.id ON DELETE CASCADE
    question_type   VARCHAR(12) NOT NULL,        -- MCQ | FILL_BLANK | TRUE_FALSE
    question_text   TEXT NOT NULL,
    options_json    TEXT DEFAULT '[]',           -- JSON array for MCQ: ["A) -2","B) 2",...]
    correct_answer  TEXT NOT NULL,               -- "A" for MCQ, "-4" for FILL, "true"/"false" for T/F
    explanation     TEXT DEFAULT '',
    difficulty_offset INTEGER NOT NULL DEFAULT 0,-- -10 to +10
    position        INTEGER NOT NULL DEFAULT 0,  -- display order
    created_at      TIMESTAMP NOT NULL
);
CREATE INDEX idx_qq_homework ON quiz_questions(homework_id, position);
```

### 3.7 New Table: `quiz_answers`

Per-question student answers linked to homework submissions.

```sql
CREATE TABLE quiz_answers (
    id              UUID PRIMARY KEY,
    submission_id   UUID NOT NULL,               -- FK homework_submissions.id ON DELETE CASCADE
    question_id     UUID NOT NULL,               -- FK quiz_questions.id ON DELETE CASCADE
    answer_text     TEXT NOT NULL,
    is_correct      BOOLEAN NOT NULL DEFAULT false,
    created_at      TIMESTAMP NOT NULL
);
CREATE INDEX idx_qa_submission ON quiz_answers(submission_id);
CREATE INDEX idx_qa_question ON quiz_answers(question_id);
```

### 3.8 Existing Tables — Column Additions

```sql
-- Add coverage_percent to syllabus_progress (partial coverage)
ALTER TABLE syllabus_progress
    ADD COLUMN IF NOT EXISTS coverage_percent INTEGER NOT NULL DEFAULT 0;

-- Add depth to curriculum_units (0=chapter, 1=topic, 2=subtopic)
ALTER TABLE curriculum_units
    ADD COLUMN IF NOT EXISTS depth INTEGER NOT NULL DEFAULT 0;

-- Add is_quiz + quiz_meta_json to homework
ALTER TABLE homework
    ADD COLUMN IF NOT EXISTS is_quiz BOOLEAN NOT NULL DEFAULT false,
    ADD COLUMN IF NOT EXISTS quiz_meta_json TEXT DEFAULT '{}';

-- Add score + rank to homework_submissions
ALTER TABLE homework_submissions
    ADD COLUMN IF NOT EXISTS score INTEGER,
    ADD COLUMN IF NOT EXISTS rank INTEGER;
```

### 3.9 Existing Tables — No Changes Needed

| Table | Why No Change |
|---|---|
| `CurriculumUnitsTable` | Already has `parentId` self-FK for hierarchy. `depth` column is additive. |
| `SyllabusProgressTable` | `coveragePercent` is additive. Existing `isCovered` boolean stays for back-compat. |
| `HomeworkTable` | `isQuiz` + `quizMetaJson` are additive. Existing lifecycle unchanged. |
| `HomeworkSubmissionsTable` | `score` + `rank` are additive. Existing `marks` stays for non-quiz homework. |
| `AcademicYearsTable` | Read-only — pace plan references it. |
| `TeacherPeriodsTable` | Read-only — pace plan counts weekly periods from it. |
| `TeacherSubjectAssignmentsTable` | Read-only — scoping via `requireOwnedAssignment`. |
| `Notify` / `NotifyRecipients` | No schema changes — uses existing notification infrastructure. |

### 3.10 Migration Strategy

- **Migration 110**: `syllabus_sources` + `daily_class_log` + `syllabus_pace_plan` + `syllabus_popup_prefs` + `syllabus_pace_alerts` (all additive) + ALTER TABLE additions to `syllabus_progress`, `curriculum_units`
- **Migration 111**: `quiz_questions` + `quiz_answers` + ALTER TABLE additions to `homework`, `homework_submissions`
- No destructive changes — all new tables + additive columns
- Backfill `curriculum_units.depth`: chapters (parentId = null) → depth=0, topics (parentId = chapter) → depth=1, existing subtopics (parentId = topic) → depth=2
- Backfill `syllabus_progress.coverage_percent`: where `isCovered = true` → set to 100

---

## 4. API Design

### 4.1 Syllabus AI Parse Endpoints (Teacher)

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/v1/teacher/syllabus/parse` | Upload image/text → AI parse → return structured hierarchy (preview, not saved) |
| `POST` | `/api/v1/teacher/syllabus/parse/confirm` | Confirm parsed hierarchy → bulk insert into curriculum_units + create pace plan |
| `DELETE` | `/api/v1/teacher/syllabus/units/{id}` | Soft-delete a syllabus unit (is_active=false) + cascade progress |

### 4.2 Daily Class Log Endpoints (Teacher)

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/v1/teacher/syllabus/daily-log` | Teacher check-in: topics covered, summary, coverage % |
| `GET` | `/api/v1/teacher/syllabus/daily-log?assignmentId=&date=` | Read daily log for a specific date |
| `GET` | `/api/v1/teacher/syllabus/daily-log/should-show?assignmentId=` | Check if popup should show (checks suppression prefs + existing log) |

### 4.3 Popup Preferences Endpoints (Teacher)

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/v1/teacher/syllabus/popup-prefs` | Set suppression mode (off/week/permanent) |
| `GET` | `/api/v1/teacher/syllabus/popup-prefs?assignmentId=` | Get current suppression prefs |

### 4.4 Coverage Extension (Teacher)

| Method | Path | Description |
|---|---|---|
| `PATCH` | `/api/v1/teacher/syllabus/progress` | Extended: now accepts `coverage_percent` (0-100) in addition to `is_covered` |

### 4.5 Pace Monitoring Endpoints (Admin)

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/v1/school/syllabus-pace/alerts` | List active pace alerts (filter by level, class, subject) |
| `GET` | `/api/v1/school/syllabus-pace/coverage?classId=&section=` | Per-subject coverage + pace status for a class |
| `POST` | `/api/v1/school/syllabus-pace/recalculate` | Manually trigger pace recalculation for all assignments |

### 4.6 Parent Daily Summary + Syllabus Migration (Parent)

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/v1/parent/child/{id}/daily-summary?date=` | Per-subject: summary text, is_ai_estimated, coverage_pct |
| `GET` | `/api/v1/parent/child/{id}/syllabus` | MIGRATED: now reads from `curriculum_units` + `syllabus_progress` (typed tables) |

### 4.7 Quiz Generation Endpoints (Teacher)

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/v1/teacher/syllabus/generate-quiz` | AI generate quiz from topic IDs → returns preview (questions + answers) |
| `POST` | `/api/v1/teacher/syllabus/quiz/{homeworkId}/publish` | Teacher confirms quiz → creates homework + quiz_questions → notifies parents |
| `GET` | `/api/v1/teacher/homework/{id}/quiz-results` | Ranked submissions with per-question correctness breakdown |

### 4.8 Quiz Answering Endpoints (Parent)

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/v1/parent/child/{id}/quiz/{homeworkId}` | Fetch quiz questions (no correct answers) |
| `POST` | `/api/v1/parent/child/{id}/quiz/{homeworkId}/submit` | Submit answers → auto-score → create quiz_answers + update submission |
| `GET` | `/api/v1/parent/child/{id}/quiz/{homeworkId}/result` | Student's score + ranking + per-question correctness |

### 4.9 Key DTOs

```kotlin
// ── Syllabus AI Parse ──
@Serializable
data class SyllabusParseRequest(
    @SerialName("assignment_id") val assignmentId: String,
    @SerialName("source_type") val sourceType: String,  // IMAGE | TEXT
    @SerialName("source_url") val sourceUrl: String? = null,
    @SerialName("raw_text") val rawText: String? = null,
    @SerialName("class_level") val classLevel: String,
    val subject: String,
)

@Serializable
data class SyllabusParseResponse(
    val chapters: List<ParsedChapterDto>,
    @SerialName("ai_provider") val aiProvider: String,
)

@Serializable
data class ParsedChapterDto(
    val title: String,
    val topics: List<ParsedTopicDto>,
)

@Serializable
data class ParsedTopicDto(
    val title: String,
    val subtopics: List<ParsedSubtopicDto> = emptyList(),
)

@Serializable
data class ParsedSubtopicDto(
    val title: String,
)

@Serializable
data class SyllabusParseConfirmRequest(
    @SerialName("assignment_id") val assignmentId: String,
    val chapters: List<ParsedChapterDto>,
    @SerialName("source_id") val sourceId: String? = null,
)

// ── Daily Class Log ──
@Serializable
data class DailyClassLogDto(
    val id: String,
    @SerialName("assignment_id") val assignmentId: String,
    val date: String,
    @SerialName("topic_ids") val topicIds: List<String>,
    @SerialName("summary_text") val summaryText: String,
    @SerialName("coverage_pct") val coveragePct: Int,
    val source: String,  // TEACHER | AI
    @SerialName("is_ai_estimated") val isAiEstimated: Boolean,
)

@Serializable
data class CreateDailyLogRequest(
    @SerialName("assignment_id") val assignmentId: String,
    val date: String,
    @SerialName("topic_ids") val topicIds: List<String>,
    @SerialName("summary_text") val summaryText: String = "",
    @SerialName("coverage_pct") val coveragePct: Int,
)

@Serializable
data class ShouldShowPopupDto(
    @SerialName("should_show") val shouldShow: Boolean,
    val reason: String,  // "no_log" | "suppressed_week" | "suppressed_permanent" | "already_logged"
)

// ── Popup Preferences ──
@Serializable
data class PopupPrefsDto(
    @SerialName("suppress_mode") val suppressMode: String,  // off | week | permanent
    @SerialName("suppressed_until") val suppressedUntil: String? = null,
)

@Serializable
data class SetPopupPrefsRequest(
    @SerialName("assignment_id") val assignmentId: String? = null,
    @SerialName("suppress_mode") val suppressMode: String,
)

// ── Pace Monitoring ──
@Serializable
data class PaceAlertDto(
    val id: String,
    @SerialName("assignment_id") val assignmentId: String,
    @SerialName("class_name") val className: String,
    val section: String,
    val subject: String,
    @SerialName("teacher_name") val teacherName: String,
    @SerialName("alert_level") val alertLevel: String,  // BEHIND | CRITICAL | AHEAD
    @SerialName("expected_pct") val expectedPct: Int,
    @SerialName("actual_pct") val actualPct: Int,
    @SerialName("ai_confirmed") val aiConfirmed: Boolean,
    @SerialName("created_at") val createdAt: String,
)

@Serializable
data class ClassCoverageDto(
    @SerialName("class_id") val classId: String,
    @SerialName("class_name") val className: String,
    val section: String,
    val subjects: List<SubjectCoverageDto>,
)

@Serializable
data class SubjectCoverageDto(
    val subject: String,
    @SerialName("coverage_pct") val coveragePct: Int,
    @SerialName("expected_pct") val expectedPct: Int,
    val status: String,  // BEHIND | ON_TRACK | AHEAD
    @SerialName("teacher_name") val teacherName: String,
)

// ── Parent Daily Summary ──
@Serializable
data class ParentDailySummaryDto(
    val date: String,
    val subjects: List<ParentDailySubjectDto>,
)

@Serializable
data class ParentDailySubjectDto(
    val subject: String,
    @SerialName("summary_text") val summaryText: String,
    @SerialName("is_ai_estimated") val isAiEstimated: Boolean,
    @SerialName("coverage_pct") val coveragePct: Int,
)

// ── Quiz Generation ──
@Serializable
data class GenerateQuizRequest(
    @SerialName("assignment_id") val assignmentId: String,
    @SerialName("topic_ids") val topicIds: List<String>,
    @SerialName("question_types") val questionTypes: List<String>,  // MCQ | FILL_BLANK | TRUE_FALSE
    @SerialName("question_count") val questionCount: Int,
    @SerialName("difficulty_offset") val difficultyOffset: Int,  // -10 to +10
    @SerialName("due_date") val dueDate: String,
)

@Serializable
data class QuizPreviewDto(
    val questions: List<QuizQuestionDto>,
    @SerialName("ai_provider") val aiProvider: String,
)

@Serializable
data class QuizQuestionDto(
    val id: String? = null,  // null in preview, set after publish
    @SerialName("question_type") val questionType: String,  // MCQ | FILL_BLANK | TRUE_FALSE
    @SerialName("question_text") val questionText: String,
    val options: List<String> = emptyList(),  // for MCQ
    @SerialName("correct_answer") val correctAnswer: String,
    val explanation: String = "",
    val position: Int,
)

@Serializable
data class PublishQuizRequest(
    @SerialName("homework_id") val homeworkId: String,
    val questions: List<QuizQuestionDto>,
)

// ── Quiz Results ──
@Serializable
data class QuizResultsDto(
    @SerialName("homework_id") val homeworkId: String,
    val title: String,
    @SerialName("class_name") val className: String,
    val section: String,
    @SerialName("total_students") val totalStudents: Int,
    @SerialName("submitted_count") val submittedCount: Int,
    @SerialName("class_average_pct") val classAveragePct: Int,
    val rankings: List<QuizRankingDto>,
    @SerialName("question_breakdown") val questionBreakdown: List<QuestionBreakdownDto>,
)

@Serializable
data class QuizRankingDto(
    val rank: Int,
    @SerialName("student_name") val studentName: String,
    val score: Int,
    val total: Int,
    val pct: Int,
    val late: Boolean = false,
)

@Serializable
data class QuestionBreakdownDto(
    val position: Int,
    @SerialName("question_text") val questionText: String,
    @SerialName("correct_count") val correctCount: Int,
    @SerialName("total_count") val totalCount: Int,
    val status: String,  // EASY | OK | REVIEW
)

// ── Parent Quiz ──
@Serializable
data class ParentQuizDto(
    @SerialName("homework_id") val homeworkId: String,
    val title: String,
    @SerialName("due_date") val dueDate: String,
    val questions: List<ParentQuizQuestionDto>,
)

@Serializable
data class ParentQuizQuestionDto(
    val id: String,
    @SerialName("question_type") val questionType: String,
    @SerialName("question_text") val questionText: String,
    val options: List<String> = emptyList(),
    val position: Int,
)

@Serializable
data class QuizSubmitRequest(
    val answers: List<QuizAnswerDto>,
)

@Serializable
data class QuizAnswerDto(
    @SerialName("question_id") val questionId: String,
    @SerialName("answer_text") val answerText: String,
)

@Serializable
data class QuizResultDto(
    val score: Int,
    val total: Int,
    val pct: Int,
    val rank: Int,
    @SerialName("per_question") val perQuestion: List<QuizResultQuestionDto>,
)

@Serializable
data class QuizResultQuestionDto(
    @SerialName("question_id") val questionId: String,
    @SerialName("question_text") val questionText: String,
    @SerialName("your_answer") val yourAnswer: String,
    @SerialName("correct_answer") val correctAnswer: String,
    @SerialName("is_correct") val isCorrect: Boolean,
    val explanation: String = "",
)
```

---

## 5. End-to-End Architecture Flow

### 5.1 Syllabus Upload → AI Parse → Save → Pace Plan

```
Teacher opens Syllabus tab → taps "Upload Syllabus"
  ↓
Choose: Image (camera/gallery) or Text (paste)
  ↓
POST /api/v1/teacher/syllabus/parse
  → Server uploads image to Supabase Storage (if IMAGE)
  → Server calls SyllabusAiService.parseSyllabusImage() or .parseSyllabusText()
    → AiService.complete() with REASON lane
    → For IMAGE: LlmClient.completeWithVision() with Gemini (vision-capable)
    → For TEXT: LlmClient.complete() with Groq (highest free throughput)
    → System prompt includes curated reference URLs (NCERT, CBSE, Kaggle, etc.)
  → AI returns structured JSON: {chapters: [{title, topics: [{title, subtopics}]}]}
  → Server saves to syllabus_sources (raw + parsed_json)
  → Returns SyllabusParseResponse to client
  ↓
Teacher reviews parsed hierarchy → can edit/rename/delete/add
  ↓
[Confirm & Save]
  → POST /api/v1/teacher/syllabus/parse/confirm
  → Server bulk-inserts into curriculum_units (3-level: depth 0/1/2 via parentId)
  → Server calls SyllabusAiService.estimatePacePlan()
    → Counts total topics
    → Counts weekly periods for this subject from TeacherPeriodsTable
    → Calculates total_classes_expected = weekly_periods × weeks_in_academic_year
    → AI estimates per-class coverage % → stores in syllabus_pace_plan
  → Returns success
  ↓
Teacher sees populated syllabus tree in TeacherSyllabusScreenV2
```

### 5.2 Daily Check-In Flow

```
Teacher's class period ends (endTime < now, from teacher_periods)
  ↓
Client checks: GET /api/v1/teacher/syllabus/daily-log/should-show?assignmentId=X
  → Server checks:
    1. Does daily_class_log exist for today + assignmentId? → if yes, return should_show=false
    2. Check syllabus_popup_prefs for this teacher + assignmentId
       → suppress_mode = 'permanent' → return should_show=false
       → suppress_mode = 'week' + suppressed_until >= today → return should_show=false
    3. Otherwise → return should_show=true, reason="no_log"
  ↓
If should_show=true → SyllabusCheckInPopup appears (scrim + scale-in)
  ↓
Teacher selects topics, sets coverage %, writes summary
  ↓
[Save] → POST /api/v1/teacher/syllabus/daily-log
  → Server creates daily_class_log (source=TEACHER, is_ai_estimated=false)
  → Server updates syllabus_progress for selected topics:
    → coverage_percent = entered value
    → isCovered = true if coverage_percent >= 100
    → coveredOn = today, coveredBy = teacherId
  → Server updates syllabus_pace_plan.actual_coverage_pct
  → Server checks: does actual deviate >20% from expected?
    → If yes: set needs_recalc=true (AI recalc on next scheduled run)
  ↓
[Not today] → popup dismissed, no record created
[Don't show this week] → POST /api/v1/teacher/syllabus/popup-prefs (mode=week)
[Never show] → POST /api/v1/teacher/syllabus/popup-prefs (mode=permanent)
```

### 5.3 Pace Monitoring + AI Reconfirmation + Alert Flow

```
Scheduled job runs daily (post-school-hours, e.g., 16:00):
  ↓
For each active assignment with a syllabus_pace_plan:
  1. Calculate classes_elapsed from academic_year start_date to today
     (minus holidays from calendar_events where type=HOLIDAY)
  2. Calculate expected_coverage_pct = (classes_elapsed / total_classes_expected) × 100
  3. Calculate actual_coverage_pct from syllabus_progress
     (avg coverage_percent across all units for this assignment+section)
  4. Update syllabus_pace_plan with new values
  ↓
  Check deviation:
    actual < expected - 15% → potential BEHIND alert
    actual < expected - 30% → potential CRITICAL alert
    actual > expected + 15% → potential AHEAD alert
  ↓
  If potential alert AND no existing active alert for this assignment:
    → Call SyllabusAiService.reconfirmAlert()
      → AiService.complete() with REASON lane
      → Input: alert data, pace plan, recent daily logs, topic list
      → AI evaluates: "Is this a real concern or a data artifact?"
      → Returns: {confirmed: bool, reasoning: string}
    ↓
    If AI confirmed:
      → Create syllabus_pace_alerts row (ai_confirmed=true)
      → Notify via Notify.toUsers():
        → Teacher: "Your {subject} syllabus for {class} is behind schedule ({actual}% vs expected {expected}%)"
        → Class Teacher (if different): same message
        → School Admin: "{teacher_name}'s {subject} for {class} is behind schedule"
        → Parents (weekly digest only, not per-check): "Syllabus pace update for {class}"
      → notified_roles = ["teacher", "admin"] (parents only in weekly digest)
  ↓
  If actual >= expected (pace recovered):
    → Resolve existing alert: set resolved_at = now
    → Notify teacher: "Your {subject} syllabus is back on track"
```

### 5.4 Parent Daily Summary Flow

```
Parent opens Home tab
  ↓
GET /api/v1/parent/child/{id}/daily-summary?date=today
  → Server resolves child's class + section via requireOwnedChild
  → For each subject (TSA) in child's class:
    1. Check daily_class_log for today + assignmentId
       → If exists (source=TEACHER): use summary_text, is_ai_estimated=false
       → If not exists:
         → Check syllabus_pace_plan for pre-calculated AI estimate
         → If needs_recalc=true: trigger async AI recalculation (BATCH lane)
         → Use current ai_estimate_json → extract summary for today
         → is_ai_estimated=true
    2. Get coverage_pct from syllabus_progress (avg for this subject)
  → Returns ParentDailySummaryDto with per-subject summaries
  ↓
Client renders "Today's Learning" card on ParentHomeScreenV2
  → Teacher-populated subjects: show summary text, no badge
  → AI-estimated subjects: show summary text + "ℹ AI Estimated" badge
  → Tap any subject → opens ParentCoveredDetailOverlay (existing, extended)
```

### 5.5 Quiz Generation → Publish → Student Answer → Results Flow

```
Teacher marks topic as covered → taps "Generate Quiz"
  ↓
POST /api/v1/teacher/syllabus/generate-quiz
  → Server calls SyllabusAiService.generateQuiz()
    → AiService.complete() with REASON lane (Groq provider)
    → System prompt includes topic titles, class level, difficulty offset
    → Curated reference URLs for question quality
    → AI returns structured JSON: [{type, question, options, correct, explanation}]
  → Returns QuizPreviewDto to client (questions + correct answers)
  ↓
Teacher reviews → can edit questions, regenerate, or publish
  ↓
[Publish] → POST /api/v1/teacher/syllabus/quiz/{homeworkId}/publish
  → Server creates homework row (isQuiz=true, status=ASSIGNED)
  → Server inserts quiz_questions rows
  → Server notifies class parents via Notify.toUsers() + NotifyRecipients.parentsOfClass()
    → "New quiz: {topic title} for {class}. Due: {date}"
  ↓
Parent sees notification → opens Academics tab → "Pending Quizzes"
  ↓
GET /api/v1/parent/child/{id}/quiz/{homeworkId}
  → Server fetches quiz_questions (without correct_answer)
  → Returns ParentQuizDto
  ↓
Parent/child answers all questions → [Submit]
  ↓
POST /api/v1/parent/child/{id}/quiz/{homeworkId}/submit
  → Server auto-scores: compare each answer to correct_answer
  → Create homework_submissions row (status=submitted, score=N, marks=N)
  → Create quiz_answers rows (one per question, is_correct computed)
  → Calculate rank: count submissions with higher score + 1
  → Update homework_submissions.rank for all submissions in this homework
  → Returns QuizResultDto (score, rank, per-question correctness)
  ↓
Client shows result screen (score, rank, per-question review)
  ↓
Teacher opens Homework tab → taps quiz
  ↓
GET /api/v1/teacher/homework/{id}/quiz-results
  → Server fetches all submissions for this homework
  → Ranks by score (descending)
  → Calculates per-question correct/total counts
  → Returns QuizResultsDto (rankings + question breakdown)
  ↓
Teacher views ranked results → [Send Results to Parents]
  → Server notifies all parents: "Quiz results available: {title}"
```

---

## 6. Implementation Phases

### Phase 0: Database Migrations (MUST BE FIRST)

**Goal:** Create all new tables + additive columns. Everything else depends on this.

**Files to create/modify:**

| File | Action | Description |
|---|---|---|
| `docs/db/migration_110_syllabus_agentic.sql` | CREATE | `syllabus_sources` + `daily_class_log` + `syllabus_pace_plan` + `syllabus_popup_prefs` + `syllabus_pace_alerts` + ALTER TABLE `syllabus_progress` (add `coverage_percent`) + ALTER TABLE `curriculum_units` (add `depth`) + backfill statements |
| `docs/db/migration_111_quiz.sql` | CREATE | `quiz_questions` + `quiz_answers` + ALTER TABLE `homework` (add `is_quiz`, `quiz_meta_json`) + ALTER TABLE `homework_submissions` (add `score`, `rank`) |
| `server/.../db/Tables.kt` | EDIT | Add `SyllabusSourcesTable`, `DailyClassLogTable`, `SyllabusPacePlanTable`, `SyllabusPopupPrefsTable`, `SyllabusPaceAlertsTable`, `QuizQuestionsTable`, `QuizAnswersTable` objects. Add `coveragePercent` to `SyllabusProgressTable`. Add `depth` to `CurriculumUnitsTable`. Add `isQuiz` + `quizMetaJson` to `HomeworkTable`. Add `score` + `rank` to `HomeworkSubmissionsTable`. |

**Why first:** All server routing and shared layer code depends on the table objects existing in `Tables.kt`.

### Phase 1: Backend — Syllabus AI Service + Routing Extensions

**Goal:** AI parsing, daily log, popup prefs, DELETE endpoint, pace monitoring, parent daily summary, syllabus read migration.

**Files to create/modify:**

| File | Action | Description |
|---|---|---|
| `server/.../feature/ai/SyllabusAiService.kt` | CREATE | `parseSyllabusImage()`, `parseSyllabusText()`, `estimatePacePlan()`, `generateDailySummary()`, `reconfirmAlert()`, `generateQuiz()` — all via `AiService.complete()` / `LlmClient.completeWithVision()` |
| `server/.../feature/teacher/TeacherSyllabusRouting.kt` | EDIT | Add: `POST /syllabus/parse`, `POST /syllabus/parse/confirm`, `DELETE /syllabus/units/{id}`, `POST /syllabus/daily-log`, `GET /syllabus/daily-log`, `GET /syllabus/daily-log/should-show`, `POST /syllabus/popup-prefs`, `GET /syllabus/popup-prefs`. Extend `PATCH /syllabus/progress` to accept `coverage_percent`. |
| `server/.../feature/ai/SyllabusPaceService.kt` | CREATE | Scheduled pace monitoring: calculate expected vs actual, AI reconfirmation, create alerts, send notifications. `recalculateAll()`, `checkAndAlert()`. |
| `server/.../feature/school/SyllabusPaceRouting.kt` | CREATE | `GET /syllabus-pace/alerts`, `GET /syllabus-pace/coverage`, `POST /syllabus-pace/recalculate` |
| `server/.../feature/parent/ParentAcademicsRouting.kt` | EDIT | Add `GET /child/{id}/daily-summary`. Migrate `GET /child/{id}/syllabus` from `SyllabusUnitsTable` to `CurriculumUnitsTable` + `SyllabusProgressTable`. |
| `server/.../Application.kt` or routing setup | EDIT | Wire `SyllabusPaceRouting` + register scheduled job for `SyllabusPaceService` |

**Server-side validation rules:**
- Parse: `sourceType` must be IMAGE or TEXT. IMAGE requires `sourceUrl`. TEXT requires `rawText`.
- Parse confirm: `chapters` must be non-empty. Each chapter must have at least one topic.
- Daily log: `coveragePct` must be 0-100. `topicIds` must belong to this assignment's curriculum_units.
- Delete unit: soft-delete only (set `isActive=false`). Cascade: hide progress records for deleted units in parent/admin views.
- Popup prefs: `suppressMode` must be off/week/permanent. `suppressedUntil` computed server-side.
- Pace recalculate: only school admin can trigger manually.
- Daily summary: parent must own child (`requireOwnedChild`).

**AI system prompt strategy:**
- Syllabus parse: Include class level, subject, curated reference URLs. Ask for JSON output with chapters → topics → subtopics structure.
- Daily summary: Include topic titles, class level. Ask for 2-3 sentence parent-friendly summary.
- Pace reconfirmation: Include alert data, pace plan, recent logs. Ask "Is this a real concern?" with reasoning.
- Quiz generation: Include topic titles, class level, difficulty offset, question types. Ask for structured JSON with question, options, correct answer, explanation.

### Phase 2: Backend — Quiz Generation + Submission + Results

**Goal:** AI quiz generation, publish, student submission via parent app, ranked results.

**Files to create/modify:**

| File | Action | Description |
|---|---|---|
| `server/.../feature/teacher/TeacherQuizRouting.kt` | CREATE | `POST /syllabus/generate-quiz`, `POST /syllabus/quiz/{homeworkId}/publish`, `GET /homework/{id}/quiz-results` |
| `server/.../feature/parent/ParentQuizRouting.kt` | CREATE | `GET /child/{id}/quiz/{homeworkId}`, `POST /child/{id}/quiz/{homeworkId}/submit`, `GET /child/{id}/quiz/{homeworkId}/result` |
| `server/.../Application.kt` or routing setup | EDIT | Wire `TeacherQuizRouting` + `ParentQuizRouting` |

**Server-side validation rules:**
- Generate quiz: `topicIds` must be covered (`isCovered=true` or `coveragePercent >= 100`). `questionCount` 1-20. `difficultyOffset` -10 to +10.
- Publish quiz: `questions` must be non-empty. Each question must have `correctAnswer`.
- Submit quiz: all questions must be answered. Parent must own child. Child must be in the class assigned to this homework.
- Auto-scoring: MCQ → exact match (case-insensitive). FILL_BLANK → normalized string match (trim, lowercase). TRUE_FALSE → "true"/"false" match.
- Rank calculation: recompute all submissions' rank after each new submission.

### Phase 3: Shared Layer (KMP Models + API + Repository)

**Goal:** Domain models, API interfaces, repository implementations for all new endpoints.

**Files to create/modify:**

| File | Action | Description |
|---|---|---|
| `shared/.../feature/teacher/domain/model/SyllabusAiModels.kt` | CREATE | Parse request/response, daily log, popup prefs DTOs |
| `shared/.../feature/teacher/data/remote/SyllabusAiApi.kt` | CREATE | Ktor API client for parse, daily-log, popup-prefs, delete |
| `shared/.../feature/teacher/domain/repository/SyllabusAiRepository.kt` | CREATE | Repository interface |
| `shared/.../feature/teacher/data/repository/SyllabusAiRepositoryImpl.kt` | CREATE | Repository impl |
| `shared/.../feature/teacher/domain/model/QuizModels.kt` | CREATE | Quiz generation, preview, results, submission DTOs |
| `shared/.../feature/teacher/data/remote/QuizApi.kt` | CREATE | Ktor API client for quiz endpoints |
| `shared/.../feature/teacher/domain/repository/QuizRepository.kt` | CREATE | Repository interface |
| `shared/.../feature/teacher/data/repository/QuizRepositoryImpl.kt` | CREATE | Repository impl |
| `shared/.../feature/admin/domain/model/SyllabusPaceModels.kt` | CREATE | Pace alert, coverage DTOs |
| `shared/.../feature/admin/data/remote/SyllabusPaceApi.kt` | CREATE | Ktor API client for pace endpoints |
| `shared/.../feature/admin/domain/repository/SyllabusPaceRepository.kt` | CREATE | Repository interface |
| `shared/.../feature/admin/data/repository/SyllabusPaceRepositoryImpl.kt` | CREATE | Repository impl |
| `shared/.../feature/parent/domain/model/ParentDailySummaryModels.kt` | CREATE | Daily summary, parent quiz DTOs |
| `shared/.../feature/parent/data/remote/ParentDailySummaryApi.kt` | CREATE | Ktor API client |
| `shared/.../feature/parent/domain/repository/ParentDailySummaryRepository.kt` | CREATE | Repository interface |
| `shared/.../feature/parent/data/repository/ParentDailySummaryRepositoryImpl.kt` | CREATE | Repository impl |
| `shared/.../di/Koin.kt` | EDIT | Register all new VMs, repos, APIs |

### Phase 4: App UI — Teacher Syllabus Extensions

**Goal:** Upload, subtopic display, delete, daily check-in popup, generate quiz button.

**Files to create/modify:**

| File | Action | Description |
|---|---|---|
| `shared/.../feature/teacher/presentation/TeacherSyllabusViewModel.kt` | EDIT | Add: upload state (parse preview, confirm), delete unit, subtopic depth rendering, daily check-in state, popup should-show check |
| `shared/.../feature/teacher/presentation/SyllabusCheckInViewModel.kt` | CREATE | VM for daily check-in popup: topics list, coverage slider, summary text, save, suppress |
| `shared/.../feature/teacher/presentation/QuizGenerationViewModel.kt` | CREATE | VM for quiz generation: topic selection, question types, count, difficulty, generate, preview, publish |
| `composeApp/.../screens/teacher/TeacherSyllabusScreenV2.kt` | EDIT | Add: upload button, 3-level indent for subtopics, delete in edit mode, "Generate Quiz" button on covered topics |
| `composeApp/.../screens/teacher/SyllabusUploadSheet.kt` | CREATE | Image/text upload bottom sheet |
| `composeApp/.../screens/teacher/SyllabusParsePreviewScreen.kt` | CREATE | Parsed hierarchy review + edit + confirm |
| `composeApp/.../screens/teacher/SyllabusCheckInPopup.kt` | CREATE | Daily check-in popup (scrim + scale-in, matches TeacherCheckInPopup pattern) |
| `composeApp/.../screens/teacher/QuizGenerationSheet.kt` | CREATE | Quiz generation form (topic, types, count, difficulty) |
| `composeApp/.../screens/teacher/QuizPreviewScreen.kt` | CREATE | Quiz preview with edit/regenerate/publish |
| `composeApp/.../screens/teacher/TeacherPortalV2.kt` | EDIT | Add overlays for upload, parse preview, check-in popup, quiz generation, quiz preview |
| `composeApp/.../screens/teacher/TeacherProfileScreenV2.kt` | EDIT | Add "Syllabus Check-in Reminders" setting |

### Phase 5: App UI — Parent Daily Summary + Quiz

**Goal:** Daily summary card on home tab, quiz answering screen, quiz result screen.

**Files to create/modify:**

| File | Action | Description |
|---|---|---|
| `shared/.../feature/parent/presentation/ParentDashboardViewModel.kt` | EDIT | Extend `coveredToday` with `summaryText` + `isAiEstimated` per subject. Fetch daily summary. |
| `shared/.../feature/parent/presentation/ParentQuizViewModel.kt` | CREATE | VM: fetch quiz, track answers, submit, show result |
| `composeApp/.../screens/parent/ParentHomeScreenV2.kt` | EDIT | Add "Today's Learning" card with per-subject summary + AI Estimated badge |
| `composeApp/.../screens/parent/ParentCoveredDetailOverlay.kt` | EDIT | Show summary text per subject + estimation label |
| `composeApp/.../screens/parent/ParentQuizScreen.kt` | CREATE | Quiz answering UI (MCQ radio, fill-blank text, true/false toggle) |
| `composeApp/.../screens/parent/ParentQuizResultScreen.kt` | CREATE | Score, rank, per-question review |
| `composeApp/.../screens/parent/ParentAcademicsScreenV2.kt` | EDIT | Add "Pending Quizzes" section linking to quiz screen |
| `composeApp/.../screens/parent/ParentPortalV2.kt` | EDIT | Add overlays for quiz screen + result screen |

### Phase 6: App UI — Teacher Quiz Results + Admin Pace Alerts

**Goal:** Teacher sees ranked quiz results. Admin sees pace alerts + coverage view.

**Files to create/modify:**

| File | Action | Description |
|---|---|---|
| `shared/.../feature/teacher/presentation/QuizResultsViewModel.kt` | CREATE | VM: fetch ranked results, question breakdown, send results to parents |
| `shared/.../feature/admin/presentation/SyllabusPaceViewModel.kt` | CREATE | VM: fetch alerts, coverage view, recalculate |
| `composeApp/.../screens/teacher/TeacherQuizResultsScreen.kt` | CREATE | Ranked list + question breakdown + send results button |
| `composeApp/.../screens/school/SyllabusPaceAlertsScreen.kt` | CREATE | Active alerts list with AI confirmed badge + contact teacher |
| `composeApp/.../screens/school/SyllabusCoverageScreen.kt` | CREATE | Per-class coverage table (subject, coverage, pace, status) |
| `composeApp/.../screens/teacher/TeacherPortalV2.kt` | EDIT | Add quiz results overlay |
| `composeApp/.../screens/school/SchoolRecordsScreenV2.kt` | EDIT | Add "Syllabus Pace" section linking to alerts + coverage |
| `composeApp/.../screens/school/SchoolPortalV2.kt` | EDIT | Add overlays for pace alerts + coverage |

### Phase 7: Notifications + Offline Support + Testing

**Goal:** Wire all notifications, add offline cache for daily summary + quiz, comprehensive tests.

**Files to create/modify:**

| File | Action | Description |
|---|---|---|
| `server/.../feature/ai/SyllabusPaceService.kt` | EDIT | Wire `Notify.toUsers()` for pace alerts (teacher + admin + parents weekly digest) |
| `server/.../feature/teacher/TeacherQuizRouting.kt` | EDIT | Wire `Notify.toUsers()` + `NotifyRecipients.parentsOfClass()` on quiz publish + results |
| `server/.../feature/teacher/TeacherSyllabusRouting.kt` | EDIT | Wire `Notify.toUsers()` for daily log updates (parent daily summary refresh signal) |
| `shared/.../data/local/DailySummaryCacheDao.kt` | CREATE | Room DAO for daily summary cache |
| `shared/.../data/local/AppDatabase.kt` | EDIT | Add DailySummaryCacheEntity, bump version |
| `shared/.../data/local/Migration_4_5.kt` | CREATE | Room migration for daily summary cache |
| `shared/.../feature/parent/data/repository/ParentDailySummaryRepositoryImpl.kt` | EDIT | Cache-then-network for daily summary reads |
| `server/src/test/kotlin/.../SyllabusParseTest.kt` | CREATE | Server tests: parse image, parse text, confirm, error handling |
| `server/src/test/kotlin/.../DailyClassLogTest.kt` | CREATE | Server tests: create log, should-show, popup prefs, coverage update |
| `server/src/test/kotlin/.../SyllabusPaceTest.kt` | CREATE | Server tests: pace calculation, alert creation, AI reconfirmation, recovery |
| `server/src/test/kotlin/.../QuizGenerationTest.kt` | CREATE | Server tests: generate, publish, submit, auto-score, rank, results |
| `server/src/test/kotlin/.../ParentDailySummaryTest.kt` | CREATE | Server tests: teacher-populated vs AI-estimated, child ownership |
| `server/src/test/kotlin/.../SyllabusDeleteTest.kt` | CREATE | Server tests: soft-delete, cascade, parent view hides deleted |
| `shared/src/commonTest/kotlin/.../SyllabusAiRepositoryTest.kt` | CREATE | Repository tests |
| `shared/src/commonTest/kotlin/.../QuizRepositoryTest.kt` | CREATE | Repository tests |

---

## 7. Server Architecture Details

### 7.1 SyllabusAiService — Parse + Pace + Quiz

```kotlin
object SyllabusAiService {

    suspend fun parseSyllabusImage(
        imageUrl: String,
        classLevel: String,
        subject: String,
    ): SyllabusParseResult = {
        val systemPrompt = """
            You are a syllabus parser for Indian school education.
            Given an image of a syllabus, extract the complete hierarchy:
            chapters → topics → subtopics.
            Return JSON: {"chapters":[{"title":"...","topics":[{"title":"...","subtopics":[{"title":"..."}]}]}]}
            Reference sources for structure quality:
            - NCERT: https://www.ncert.nic.in/syllabus.php
            - CBSE: https://www.cbse.gov.in/curriculum.html
            Subject: $subject, Class: $classLevel
        """.trimIndent()

        val messages = listOf(VisionLlmMessage(
            role = "user",
            content = listOf(
                VisionContentPart.TextPart(systemPrompt),
                VisionContentPart.ImagePart(VisionContentPart.ImageUrl(imageUrl)),
            ),
        ))

        val result = AiService.complete(
            lane = AiLane.REASON,
            messages = messages,
            feature = "syllabus_parse",
            noTraining = false,  // image may contain non-PII syllabus text
            useVision = true,
        )
        // Parse JSON from result.content → SyllabusParseResult
    }

    suspend fun parseSyllabusText(
        rawText: String,
        classLevel: String,
        subject: String,
    ): SyllabusParseResult = {
        // Same prompt but text-only, no vision
        val result = AiService.complete(
            lane = AiLane.REASON,
            messages = listOf(LlmMessage("user", "$systemPrompt\n\nSyllabus text:\n$rawText")),
            feature = "syllabus_parse",
            noTraining = true,  // text-only, safe for all providers
        )
    }

    suspend fun estimatePacePlan(
        totalTopics: Int,
        weeklyPeriods: Int,
        academicYearWeeks: Int,
        classLevel: String,
    ): PacePlanEstimate = {
        val totalClasses = weeklyPeriods * academicYearWeeks
        val prompt = """
            Estimate a pace plan for $totalTopics topics over $totalClasses classes.
            Return JSON: {"per_class_pct": 2.5, "estimated_completion_week": 15, "reasoning": "..."}
        """.trimIndent()

        val result = AiService.complete(
            lane = AiLane.BATCH,  // not real-time
            messages = listOf(LlmMessage("user", prompt)),
            feature = "syllabus_pace",
            noTraining = true,
        )
    }

    suspend fun generateDailySummary(
        topicTitles: List<String>,
        classLevel: String,
        subject: String,
    ): String = {
        val prompt = """
            Write a 2-3 sentence parent-friendly summary of what was taught today.
            Topics: ${topicTitles.joinToString(", ")}
            Subject: $subject, Class: $classLevel
            Keep it simple, no jargon. Parents are 25-60 years old.
        """.trimIndent()

        val result = AiService.complete(
            lane = AiLane.FAST_CHAT,
            messages = listOf(LlmMessage("user", prompt)),
            feature = "syllabus_summary",
            noTraining = true,
        )
        result.content
    }

    suspend fun reconfirmAlert(
        alertData: PaceAlertInput,
    ): AlertReconfirmation = {
        val prompt = """
            A pace alert was triggered for a class syllabus.
            Alert level: ${alertData.alertLevel}
            Expected coverage: ${alertData.expectedPct}%
            Actual coverage: ${alertData.actualPct}%
            Recent daily logs: ${alertData.recentLogs}
            Total topics: ${alertData.totalTopics}, Classes elapsed: ${alertData.classesElapsed}
            Is this a real concern or a data artifact (e.g., teacher just hasn't logged)?
            Return JSON: {"confirmed": true/false, "reasoning": "..."}
        """.trimIndent()

        val result = AiService.complete(
            lane = AiLane.REASON,
            messages = listOf(LlmMessage("user", prompt)),
            feature = "syllabus_pace_reconfirm",
            noTraining = true,
        )
        // Parse → AlertReconfirmation
    }

    suspend fun generateQuiz(
        topicTitles: List<String>,
        classLevel: String,
        subject: String,
        questionTypes: List<String>,
        questionCount: Int,
        difficultyOffset: Int,
    ): List<QuizQuestionJson> = {
        val prompt = """
            Generate $questionCount quiz questions for $subject, Class $classLevel.
            Topics: ${topicTitles.joinToString(", ")}
            Question types: ${questionTypes.joinToString(", ")}
            Difficulty offset: $difficultyOffset% (negative = easier, positive = harder)
            Return JSON array: [{"type":"MCQ","question":"...","options":["A) ...","B) ...","C) ...","D) ..."],"correct":"A","explanation":"..."}]
            Reference sources:
            - Khan Academy: https://github.com/Khan/khan-exercises
            - OpenStax: https://github.com/openstax
        """.trimIndent()

        val result = AiService.complete(
            lane = AiLane.REASON,
            messages = listOf(LlmMessage("user", prompt)),
            feature = "syllabus_quiz",
            noTraining = true,
        )
        // Parse JSON array → List<QuizQuestionJson>
    }
}
```

### 7.2 SyllabusPaceService — Scheduled Monitoring

```kotlin
object SyllabusPaceService {

    suspend fun recalculateAll() = dbQuery {
        val activePlans = SyllabusPacePlanTable.selectAll().toList()

        for (plan in activePlans) {
            val assignmentId = plan[SyllabusPacePlanTable.assignmentId]
            val schoolId = plan[SyllabusPacePlanTable.schoolId]

            // 1. Count classes elapsed from academic year start to today
            val academicYear = AcademicYearsTable.selectAll()
                .where { AcademicYearsTable.id eq plan[SyllabusPacePlanTable.academicYearId] }
                .singleOrNull()
            val startDate = academicYear?.let { LocalDate.parse(it[AcademicYearsTable.startDate]) }
                ?: LocalDate.now().minusDays(90)  // fallback

            val holidays = CalendarEventsTable.selectAll().where {
                (CalendarEventsTable.schoolId eq schoolId) and
                (CalendarEventsTable.eventType eq "HOLIDAY") and
                (CalendarEventsTable.startDate greaterEq startDate.toString())
            }.map { LocalDate.parse(it[CalendarEventsTable.startDate]) }.toSet()

            val classesElapsed = countSchoolDays(startDate, LocalDate.now(), holidays)

            // 2. Calculate expected coverage
            val totalClasses = plan[SyllabusPacePlanTable.totalClassesExpected]
            val expectedPct = if (totalClasses > 0) {
                (classesElapsed * 100 / totalClasses).coerceIn(0, 100)
            } else 0

            // 3. Calculate actual coverage from syllabus_progress
            val progressRows = SyllabusProgressTable.selectAll().where {
                (SyllabusProgressTable.assignmentId eq assignmentId)
            }.toList()
            val actualPct = if (progressRows.isNotEmpty()) {
                progressRows.map { it[SyllabusProgressTable.coveragePercent] }.average().toInt()
            } else 0

            // 4. Update pace plan
            SyllabusPacePlanTable.update({
                SyllabusPacePlanTable.id eq plan[SyllabusPacePlanTable.id]
            }) {
                it[classesElapsed] = classesElapsed
                it[expectedCoveragePct] = expectedPct
                it[actualCoveragePct] = actualPct
                it[lastRecalcAt] = Instant.now()
            }

            // 5. Check deviation + AI reconfirm + alert
            checkAndAlert(schoolId, assignmentId, expectedPct, actualPct)
        }
    }

    private suspend fun checkAndAlert(
        schoolId: UUID,
        assignmentId: UUID,
        expectedPct: Int,
        actualPct: Int,
    ) {
        val deviation = expectedPct - actualPct
        val alertLevel = when {
            deviation >= 30 -> "CRITICAL"
            deviation >= 15 -> "BEHIND"
            actualPct - expectedPct >= 15 -> "AHEAD"
            else -> return  // on track, no alert
        }

        // Check existing active alert
        val existing = SyllabusPaceAlertsTable.selectAll().where {
            (SyllabusPaceAlertsTable.assignmentId eq assignmentId) and
            (SyllabusPaceAlertsTable.resolvedAt.isNull())
        }.singleOrNull()
        if (existing != null) return  // already alerted

        // AI reconfirmation
        val reconfirm = SyllabusAiService.reconfirmAlert(
            PaceAlertInput(alertLevel, expectedPct, actualPct, /* ... */)
        )
        if (!reconfirm.confirmed) return  // AI says it's a data artifact

        // Create alert
        SyllabusPaceAlertsTable.insert {
            it[id] = UUID.randomUUID()
            it[SyllabusPaceAlertsTable.schoolId] = schoolId
            it[SyllabusPaceAlertsTable.assignmentId] = assignmentId
            it[SyllabusPaceAlertsTable.alertLevel] = alertLevel
            it[SyllabusPaceAlertsTable.expectedPct] = expectedPct
            it[SyllabusPaceAlertsTable.actualPct] = actualPct
            it[SyllabusPaceAlertsTable.aiConfirmed] = true
            it[SyllabusPaceAlertsTable.aiReconfirmJson] = reconfirm.reasoning
            it[SyllabusPaceAlertsTable.notifiedRoles] = """["teacher","admin"]"""
            it[createdAt] = Instant.now()
        }

        // Notify teacher + admin
        val teacherId = getTeacherIdForAssignment(assignmentId)
        Notify.toUser(teacherId, "Syllabus pace alert: $alertLevel", "...")
        // Notify admin...
    }
}
```

### 7.3 Quiz Auto-Scoring Logic

```kotlin
fun scoreQuizAnswer(
    questionType: String,
    studentAnswer: String,
    correctAnswer: String,
): Boolean {
    return when (questionType) {
        "MCQ" -> studentAnswer.trim().equals(correctAnswer.trim(), ignoreCase = true)
        "FILL_BLANK" -> {
            val normalized = { s: String -> s.trim().lowercase().replace(Regex("\\s+"), " ") }
            normalized(studentAnswer) == normalized(correctAnswer)
        }
        "TRUE_FALSE" -> studentAnswer.trim().lowercase() == correctAnswer.trim().lowercase()
        else -> false
    }
}

suspend fun submitQuiz(
    schoolId: UUID,
    homeworkId: UUID,
    studentId: String,
    answers: List<QuizAnswerDto>,
): QuizResultDto = dbQuery {
    // Fetch all questions for this quiz
    val questions = QuizQuestionsTable.selectAll().where {
        QuizQuestionsTable.homeworkId eq homeworkId
    }.sortedBy { it[QuizQuestionsTable.position] }.toList()

    // Score each answer
    var score = 0
    val perQuestion = mutableListOf<QuizResultQuestionDto>()
    for (q in questions) {
        val studentAns = answers.find { it.questionId == q[QuizQuestionsTable.id].value.toString() }
        val isCorrect = studentAns != null && scoreQuizAnswer(
            q[QuizQuestionsTable.questionType],
            studentAns.answerText,
            q[QuizQuestionsTable.correctAnswer],
        )
        if (isCorrect) score++

        // Insert quiz_answer
        QuizAnswersTable.insert {
            it[id] = UUID.randomUUID()
            it[QuizAnswersTable.submissionId] = submissionId
            it[QuizAnswersTable.questionId] = q[QuizQuestionsTable.id].value
            it[QuizAnswersTable.answerText] = studentAns?.answerText ?: ""
            it[QuizAnswersTable.isCorrect] = isCorrect
            it[createdAt] = Instant.now()
        }
    }

    // Create/update homework_submission
    HomeworkSubmissionsTable.insert { ... }

    // Recalculate ranks for all submissions
    val allSubmissions = HomeworkSubmissionsTable.selectAll().where {
        HomeworkSubmissionsTable.homeworkId eq homeworkId
    }.orderBy(HomeworkSubmissionsTable.score to SortOrder.DESC).toList()

    allSubmissions.forEachIndexed { index, sub ->
        HomeworkSubmissionsTable.update({
            HomeworkSubmissionsTable.id eq sub[HomeworkSubmissionsTable.id]
        }) {
            it[rank] = index + 1
        }
    }

    val rank = allSubmissions.indexOfFirst { it[HomeworkSubmissionsTable.studentId] == studentId } + 1
    val total = questions.size

    QuizResultDto(score = score, total = total, pct = (score * 100 / total), rank = rank, perQuestion = perQuestion)
}
```

### 7.4 Parent Syllabus Read Migration

```kotlin
// MIGRATED: reads from curriculum_units + syllabus_progress instead of legacy syllabus_units
suspend fun parentSyllabusTyped(
    schoolId: UUID,
    childId: UUID,
): ParentSyllabusData = dbQuery {
    val child = requireOwnedChild(schoolId, childId)
    val classId = child.classId
    val section = child.section

    // Get all TSAs for this class+section
    val assignments = TeacherSubjectAssignmentsTable.selectAll().where {
        (TeacherSubjectAssignmentsTable.classId eq classId) and
        (TeacherSubjectAssignmentsTable.section eq section) and
        (TeacherSubjectAssignmentsTable.isActive eq true)
    }.toList()

    val subjects = assignments.map { tsa ->
        val assignmentId = tsa[TeacherSubjectAssignmentsTable.id].value
        val subjectName = getSubjectName(tsa[TeacherSubjectAssignmentsTable.subjectId])

        // Fetch curriculum_units for this assignment
        val units = CurriculumUnitsTable.selectAll().where {
            (CurriculumUnitsTable.schoolId eq schoolId) and
            (CurriculumUnitsTable.assignmentId eq assignmentId) and
            (CurriculumUnitsTable.isActive eq true)
        }.orderBy(CurriculumUnitsTable.position).toList()

        // Build 3-level hierarchy
        val chapters = units.filter { it[CurriculumUnitsTable.depth] == 0 }.map { ch ->
            val topics = units.filter {
                it[CurriculumUnitsTable.parentId] == ch[CurriculumUnitsTable.id].value &&
                it[CurriculumUnitsTable.depth] == 1
            }.map { tp ->
                val subtopics = units.filter {
                    it[CurriculumUnitsTable.parentId] == tp[CurriculumUnitsTable.id].value &&
                    it[CurriculumUnitsTable.depth] == 2
                }
                // Get progress for this topic
                val progress = SyllabusProgressTable.selectAll().where {
                    (SyllabusProgressTable.unitId eq tp[CurriculumUnitsTable.id].value) and
                    (SyllabusProgressTable.section eq section)
                }.singleOrNull()

                ParentSyllabusUnitDto(
                    id = tp[CurriculumUnitsTable.id].value.toString(),
                    title = tp[CurriculumUnitsTable.title],
                    isCovered = progress?.get(SyllabusProgressTable.isCovered) ?: false,
                    coveragePercent = progress?.get(SyllabusProgressTable.coveragePercent) ?: 0,
                    coveredOn = progress?.get(SyllabusProgressTable.coveredOn)?.toString(),
                    subtopics = subtopics.map { st -> ... },
                )
            }
            ParentSyllabusUnitDto(
                id = ch[CurriculumUnitsTable.id].value.toString(),
                title = ch[CurriculumUnitsTable.title],
                topics = topics,
            )
        }

        ParentSyllabusSubjectDto(subject = subjectName, chapters = chapters)
    }

    ParentSyllabusData(subjects = subjects)
}
```

---

## 8. Security & Validation

| Concern | Mitigation |
|---|---|
| **Only assignment owner can parse/edit syllabus** | Server checks `requireOwnedAssignment` for all teacher syllabus endpoints |
| **Only admin can view pace alerts** | Server checks `role == "school_admin"` in SchoolContext for pace endpoints |
| **Only admin can trigger pace recalculation** | Server checks admin role for `POST /syllabus-pace/recalculate` |
| **Parent can only see own child's data** | Server validates via `requireOwnedChild` for all parent endpoints |
| **Quiz submission scoped to child's class** | Server verifies child is enrolled in the class assigned to the homework |
| **Quiz questions don't leak correct answers** | `GET /parent/child/{id}/quiz/{homeworkId}` returns questions WITHOUT `correctAnswer` field |
| **AI parse image size limit** | 10MB max, server-side validation on upload |
| **AI parse content validation** | Server validates file type (image/jpeg, image/png) for image uploads |
| **Coverage percentage bounds** | Server validates 0-100 range on all coverage inputs |
| **Topic IDs belong to assignment** | Server validates `topicIds` in daily log and quiz generation are owned by the assignment |
| **XSS in summary text** | Server sanitizes all text inputs (summary, quiz questions, explanations) |
| **SQL injection** | Exposed ORM parameterized queries (existing pattern) |
| **AI cost abuse** | Rate limiting via `AiService` (existing circuit breaker + rate limiter). Cache key includes assignmentId + classLevel + subject for parse. |
| **Quiz answer gaming** | Correct answers never sent to client before submission. Score computed server-side only. |
| **Deleted units visible to parents** | Parent syllabus read filters `isActive = true` on `curriculum_units` |

---

## 9. Testing Strategy

### 9.1 Server Tests

| Test | Description |
|---|---|
| `SyllabusParseTest` | Parse image (mock vision), parse text, confirm, error handling (empty, invalid) |
| `SyllabusDeleteTest` | Soft-delete unit, cascade progress, parent view hides deleted |
| `DailyClassLogTest` | Create log, should-show (no log, suppressed, already logged), popup prefs, coverage update |
| `SyllabusPaceTest` | Pace calculation, alert creation (BEHIND/CRITICAL/AHEAD), AI reconfirmation (confirmed/rejected), recovery (resolve alert) |
| `QuizGenerationTest` | Generate quiz (mock AI), publish, error (0 questions, invalid topics) |
| `QuizSubmissionTest` | Submit answers, auto-score (MCQ/FILL/TRUE_FALSE), rank calculation, late submission |
| `QuizResultsTest` | Ranked results, question breakdown, class average, not-submitted count |
| `ParentDailySummaryTest` | Teacher-populated vs AI-estimated, child ownership, multiple subjects |
| `ParentSyllabusMigrationTest` | Typed table read (3-level hierarchy), progress join, isActive filter |
| `PopupPrefsTest` | Set off/week/permanent, suppressed_until computation, re-enable |

### 9.2 Shared/Repository Tests

| Test | Description |
|---|---|
| `SyllabusAiRepositoryTest` | API calls map to correct endpoints, NetworkResult handling |
| `QuizRepositoryTest` | Generate, publish, submit, result API calls |
| `SyllabusPaceRepositoryTest` | Alerts, coverage API calls |
| `ParentDailySummaryRepositoryTest` | Daily summary fetch, cache-then-network |

### 9.3 Build Verification

```bash
# JVM compile + tests
./gradlew :shared:compileKotlinJvm :shared:jvmTest

# Android compile
./gradlew :shared:compileDevDebugKotlinAndroid :composeApp:compileDevDebugKotlinAndroid

# Server tests
./gradlew :server:test
```

---

## 10. SOLID + MVVM Compliance Checklist

| Principle | How This Feature Complies |
|---|---|
| **S**ingle Responsibility | `SyllabusAiService` handles AI calls only. `SyllabusPaceService` handles pace monitoring only. `TeacherQuizRouting` handles quiz endpoints only. `ParentQuizRouting` handles parent quiz endpoints only. |
| **O**pen/Closed | New tables and endpoints are additive — existing `TeacherSyllabusRouting` is extended, not rewritten. `HomeworkTable` gets `isQuiz` flag, existing homework flow unchanged. |
| **L**iskov Substitution | Repository impls match interfaces exactly. `SyllabusAiRepositoryImpl` implements `SyllabusAiRepository` contract. |
| **I**nterface Segregation | Separate repository interfaces per feature: `SyllabusAiRepository`, `QuizRepository`, `SyllabusPaceRepository`, `ParentDailySummaryRepository` |
| **D**ependency Inversion | VMs depend on repository interfaces, not impls. Koin wires impls. `SyllabusCheckInViewModel` depends on `SyllabusAiRepository`, not `SyllabusAiRepositoryImpl`. |
| **MVVM** | View (Composable) → ViewModel (StateFlow) → Repository → API → Server |
| **Clean Architecture** | Domain models in `shared/commonMain/feature/*/domain/model/`, data layer in `data/`, presentation in `presentation/` |

---

## 11. Notification Triggers

| Event | Recipient | Channel | Message |
|---|---|---|---|
| Syllabus parsed & confirmed | Teacher | In-app | "Syllabus uploaded and parsed successfully for {subject}" |
| Daily check-in saved | Parents of class | Push (optional) | "Today's {subject} summary available for {child}" |
| Pace alert: BEHIND | Teacher | Push + in-app | "Your {subject} syllabus for {class} is behind schedule ({actual}% vs expected {expected}%)" |
| Pace alert: BEHIND | School Admin | Push + in-app | "{teacher}'s {subject} for {class} is behind schedule" |
| Pace alert: CRITICAL | Teacher | Push + in-app | "URGENT: {subject} syllabus for {class} is critically behind ({actual}% vs expected {expected}%)" |
| Pace alert: CRITICAL | School Admin | Push + in-app | "CRITICAL: {teacher}'s {subject} for {class} is critically behind" |
| Pace alert: AHEAD | Teacher | In-app | "Your {subject} syllabus for {class} is ahead of schedule. Great progress!" |
| Pace recovered | Teacher | In-app | "Your {subject} syllabus is back on track" |
| Pace weekly digest | Parents of class | Push (weekly) | "Weekly syllabus update: {class} progress report" |
| Quiz published | Parents of class | Push + in-app | "New quiz: {topic title} for {class}. Due: {date}" |
| Quiz submitted | Teacher | In-app | "{student} submitted quiz: {title} — Score: {score}/{total}" |
| Quiz results available | Parents of class | Push + in-app | "Quiz results available: {title}. Check your child's score." |
| Quiz results sent to parents | Parents | Push + in-app | "{child} scored {score}/{total} (Rank {rank}) in {title}" |
| Syllabus unit deleted | (No notification) | — | Silent — teacher action, no cross-user impact |

---

## 12. Future Enhancements (Non-Breaking)

| Enhancement | Description |
|---|---|
| **AI syllabus-to-quiz auto-pipeline** | When topic is marked complete, auto-generate quiz in background and notify teacher "Quiz ready for review" |
| **Adaptive quiz difficulty** | AI adjusts question difficulty based on student's past quiz performance |
| **Syllabus import from other schools** | Share syllabus templates across schools in same trust/board |
| **AI-powered remedial content** | For topics where quiz performance is low, AI suggests remedial videos/links |
| **Parent syllabus feedback** | Parents can rate daily summary helpfulness → AI adjusts summary style |
| **Multi-language syllabus summaries** | AI generates summaries in parent's preferred language (Hindi, Tamil, etc.) |
| **Syllabus version history** | Track all changes to syllabus with diff view across academic year |
| **AI pace optimization suggestions** | AI suggests which topics to prioritize or compress to catch up |
| **Cross-subject correlation** | AI detects if math pace is affecting physics (dependency mapping) |
| **Gamified quiz leaderboard** | Monthly leaderboard with badges for top performers |
| **Quiz question bank reuse** | Save AI-generated questions to a reusable bank for future quizzes |
| **Syllabus compliance reporting** | Board-mandated syllabus completion reports auto-generated for admin |
| **Voice-based daily check-in** | Teacher speaks summary → AI transcribes and structures |
| **AI tutor integration** | Quiz wrong answers trigger AI tutor session for that topic |

---

## 13. God-Mode Agent Prompt

> Paste the prompt below into an AI agent (Claude/GLM/GPT) to execute this plan end-to-end.
> The agent follows a strict loop: BUILD → TEST → REVIEW → FIX → COMMIT per phase.

---

```
You are the Agentic Syllabus Management & AI Assignment System Executor. You are implementing a production-grade feature for a Kotlin Multiplatform school management app (VidyaPrayag/EnrollPlus) targeting a $100M Series A.

## YOUR MISSION
Implement the complete Agentic Syllabus Management & AI Assignment System as defined in docs/AGENTIC_SYLLABUS_MANAGEMENT_PLAN.md. You will work phase by phase, building server → shared → app, testing at each step.

## CODEBASE ARCHITECTURE (MEMORIZE THIS)
- **Server:** Ktor + Exposed ORM + PostgreSQL (Supabase). Routing files in `server/src/main/kotlin/com/littlebridge/enrollplus/feature/`. Tables in `server/.../db/Tables.kt`. Pattern: each feature has a `*Routing.kt` file with route handlers + DTOs.
- **Shared (KMP):** `shared/src/commonMain/kotlin/com/littlebridge/enrollplus/feature/`. Structure: `domain/model/` (DTOs), `data/remote/` (Ktor API), `domain/repository/` (interfaces), `data/repository/` (impls), `presentation/` (ViewModels). DI via Koin in `shared/.../di/Koin.kt`.
- **App (Compose):** `composeApp/src/commonMain/kotlin/com/littlebridge/enrollplus/ui/v2/screens/`. Portals: `school/` (admin), `teacher/`, `parent/`. Each portal has a `*PortalV2.kt` with tab + overlay routing. UI primitives: `VCard`, `VButton`, `VBadge`, `VAvatar`, `VStateHost`, `VTheme`, `VIcons` in `ui/v2/components/`.
- **DB Migrations:** `docs/db/migration_NNN_*.sql` (sequential numbering). Latest is migration_109. This plan uses 110 + 111.
- **Build:** `./gradlew :shared:compileKotlinJvm :shared:jvmTest` (JVM), `./gradlew :shared:compileDevDebugKotlinAndroid :composeApp:compileDevDebugKotlinAndroid` (Android). Set `JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.11.10-hotspot`.

## EXISTING INFRASTRUCTURE (REUSE, DON'T DUPLICATE)
- `CurriculumUnitsTable` (Tables.kt:1059) — typed syllabus template with `parentId` self-FK for hierarchy. Has `classId`, `subjectId`, `assignmentId`, `title`, `position`, `isActive`.
- `SyllabusProgressTable` (Tables.kt:1079) — per-section coverage: `unitId`, `section`, `assignmentId`, `isCovered`, `coveredOn`, `coveredBy`.
- `SyllabusUnitsTable` (Tables.kt:1030) — LEGACY. Parent reads still use this. Migration to typed tables is part of Phase 1.
- `HomeworkTable` (Tables.kt:1110) — homework with `assignmentId`, `curriculumUnitId`, `status` lifecycle.
- `HomeworkSubmissionsTable` (Tables.kt:1159) — student submissions with `status`, `submittedAt`, `marks`.
- `HomeworkAttachmentsTable` (Tables.kt:1136) — file attachments pattern for Supabase Storage.
- `AcademicYearsTable` (Tables.kt:1752) — academic year with `startDate`, `endDate`, `status`.
- `TeacherPeriodsTable` (Tables.kt:1194) — weekly recurring periods. Used for pace plan: count weekly periods per subject.
- `TeacherSubjectAssignmentsTable` (Tables.kt:302) — teacher↔class↔subject mapping with `isClassTeacher`.
- `AiService.kt:134` — AI gateway: `complete()` with lane-based routing (FAST_CHAT, CLASSIFY, REASON, BATCH), PII guardrails, caching, circuit breaker, rate limiter.
- `LlmClient.kt:293` — OpenAI-compatible client: `complete()` for text, `completeWithVision()` for image+text.
- `KeyVault.kt` — 9 AI providers: Cerebras, Groq, Groq_Fast, SambaNova, Mistral, OpenRouter, Gemini, NVIDIA_Reason, NVIDIA_Fast.
- `Notify.kt` — `Notify.toUsers()`, `Notify.toUser()` with preferences + rate limiting.
- `NotifyRecipients.kt` — `parentsOfStudent()`, `parentsOfClass()` — recipient resolvers.
- `TeacherSyllabusRouting.kt` — existing CRUD: load, create, rename, toggle coverage. Scoped via `requireOwnedAssignment`.
- `TeacherSyllabusViewModel.kt` — existing VM: load, toggle (optimistic), edit mode, add/rename.
- `TeacherSyllabusScreenV2.kt` — existing syllabus list with toggle, edit, add, progress ring.
- `TeacherCheckInPopup.kt` — visual pattern for daily check-in popup (scrim + scale-in card).
- `ParentHomeScreenV2.kt` — parent dashboard with `coveredToday` card.
- `ParentCoveredDetailOverlay.kt` — bottom sheet: covered topics + syllabus progress.
- `ParentDashboardViewModel.kt` — `coveredToday` computed from syllabus, `schoolDayEnded` flag.
- `ParentAcademicsRouting.kt:358` — `GET /parent/child/{id}/syllabus` (currently reads legacy table).
- `TeacherHomeworkRouting.kt` — homework lifecycle: assign, extend, review, close.
- `TeacherHomeworkScreenV2.kt` — homework board + composer (visual pattern for quiz results).
- `VTheme`, `VButton`, `VCard`, `VInput`, `VIcons`, `VBadge` — UI primitives in `ui/v2/components/`.
- `NetworkResult` — API result handling pattern.
- `PreferenceRepository` — token management + prefs.
- Koin DI in `Koin.kt` — register all new VMs, repos, APIs.
- AppDatabase version: 4 (entities: SchoolEntity, OutboxOperationEntity, AnnouncementEntity, TeacherDayCacheEntity). Next version: 5.

## EXECUTION RULES
1. **One phase at a time.** Complete Phase 0 (migrations), then Phase 1 (server syllabus), then Phase 2 (server quiz), then Phase 3 (shared), etc.
2. **BUILD → TEST → FIX loop.** After each file change:
   - Run `./gradlew :shared:compileKotlinJvm` to verify compilation.
   - Run `./gradlew :shared:jvmTest` for shared tests.
   - Run `./gradlew :server:test` for server tests.
   - Fix ALL errors before moving to the next file.
3. **Follow existing patterns EXACTLY.** Look at how `TeacherSyllabusRouting.kt` structures routes + DTOs — mirror that. Look at how `TeacherSyllabusViewModel.kt` structures state — mirror that. Look at how `TeacherCheckInPopup.kt` builds the popup UI — mirror that.
4. **No comments unless asked.** No documentation files unless asked. No helper scripts.
5. **Minimal edits.** Prefer editing existing files over creating new ones when possible. Use `edit` and `multi_edit` tools.
6. **SerialName on all DTO fields.** Every DTO must use `@SerialName("snake_case")` for wire format consistency.
7. **Koin registration.** Every new ViewModel, Repository, API must be registered in `Koin.kt`.
8. **Migration numbering.** This plan uses migration_110 (syllabus) + migration_111 (quiz).
9. **Test before claiming done.** Run the build commands. Paste the output. If it fails, fix it.

## PHASE EXECUTION ORDER

### Phase 0: Database Migrations (MUST BE FIRST)
1. Create `docs/db/migration_110_syllabus_agentic.sql` — 5 new tables + ALTER TABLE additions + backfill
2. Create `docs/db/migration_111_quiz.sql` — 2 new tables + ALTER TABLE additions
3. Edit `server/.../db/Tables.kt` — add all new table objects + new columns on existing tables
4. BUILD: `./gradlew :server:compileKotlin`

### Phase 1: Backend — Syllabus AI Service + Routing
1. Create `server/.../feature/ai/SyllabusAiService.kt` — parseSyllabusImage, parseSyllabusText, estimatePacePlan, generateDailySummary, reconfirmAlert, generateQuiz
2. Edit `server/.../feature/teacher/TeacherSyllabusRouting.kt` — add parse, parse/confirm, DELETE, daily-log, should-show, popup-prefs endpoints + extend progress
3. Create `server/.../feature/ai/SyllabusPaceService.kt` — recalculateAll, checkAndAlert
4. Create `server/.../feature/school/SyllabusPaceRouting.kt` — alerts, coverage, recalculate
5. Edit `server/.../feature/parent/ParentAcademicsRouting.kt` — add daily-summary + migrate syllabus read to typed tables
6. Wire routing in Application.kt
7. BUILD + TEST: `./gradlew :server:compileKotlin :server:test`

### Phase 2: Backend — Quiz Generation + Submission
1. Create `server/.../feature/teacher/TeacherQuizRouting.kt` — generate, publish, results
2. Create `server/.../feature/parent/ParentQuizRouting.kt` — fetch, submit, result
3. Wire routing
4. BUILD + TEST

### Phase 3: Shared Layer (KMP)
1. Create all DTOs in `shared/.../feature/*/domain/model/`
2. Create API clients in `shared/.../feature/*/data/remote/`
3. Create repository interfaces in `shared/.../feature/*/domain/repository/`
4. Create repository impls in `shared/.../feature/*/data/repository/`
5. Edit `Koin.kt` — register all new deps
6. BUILD: `./gradlew :shared:compileKotlinJvm :shared:jvmTest`

### Phase 4: App UI — Teacher Syllabus Extensions
1. Edit `TeacherSyllabusViewModel.kt` — add upload, delete, subtopic, check-in state
2. Create `SyllabusCheckInViewModel.kt` + `QuizGenerationViewModel.kt`
3. Edit `TeacherSyllabusScreenV2.kt` — upload button, 3-level indent, delete, generate quiz
4. Create `SyllabusUploadSheet.kt`, `SyllabusParsePreviewScreen.kt`, `SyllabusCheckInPopup.kt`, `QuizGenerationSheet.kt`, `QuizPreviewScreen.kt`
5. Edit `TeacherPortalV2.kt` — add overlays
6. Edit `TeacherProfileScreenV2.kt` — add popup suppression setting
7. BUILD: `./gradlew :composeApp:compileDevDebugKotlinAndroid`

### Phase 5: App UI — Parent Daily Summary + Quiz
1. Edit `ParentDashboardViewModel.kt` — extend coveredToday with summary + isAiEstimated
2. Create `ParentQuizViewModel.kt`
3. Edit `ParentHomeScreenV2.kt` — "Today's Learning" card
4. Edit `ParentCoveredDetailOverlay.kt` — summary text + estimation label
5. Create `ParentQuizScreen.kt`, `ParentQuizResultScreen.kt`
6. Edit `ParentAcademicsScreenV2.kt` — "Pending Quizzes" section
7. Edit `ParentPortalV2.kt` — add overlays
8. BUILD

### Phase 6: App UI — Teacher Quiz Results + Admin Pace
1. Create `QuizResultsViewModel.kt` + `SyllabusPaceViewModel.kt`
2. Create `TeacherQuizResultsScreen.kt`, `SyllabusPaceAlertsScreen.kt`, `SyllabusCoverageScreen.kt`
3. Edit `TeacherPortalV2.kt`, `SchoolRecordsScreenV2.kt`, `SchoolPortalV2.kt`
4. BUILD

### Phase 7: Notifications + Offline + Testing
1. Wire all `Notify.toUsers()` calls in server routing
2. Create `DailySummaryCacheDao.kt` + entity + migration (AppDatabase v5)
3. Edit `ParentDailySummaryRepositoryImpl.kt` — cache-then-network
4. Create all server + shared tests
5. Final BUILD + TEST all modules

## ANTI-PATTERNS (DO NOT DO THESE)
- ❌ Do NOT create new UI components when VCard/VButton/VBadge/VStateHost exist
- ❌ Do NOT create new table columns on existing tables without ALTER TABLE IF NOT EXISTS
- ❌ Do NOT skip @SerialName on DTO fields
- ❌ Do NOT hardcode teacher names, class names, or school IDs
- ❌ Do NOT send correct answers to the client before quiz submission
- ❌ Do NOT add comments or documentation to code files
- ❌ Do NOT skip Koin registration for new dependencies
- ❌ Do NOT skip server-side validation (coverage bounds, topic ownership, quiz question validation)
- ❌ Do NOT break existing endpoints — all changes are additive
- ❌ Do NOT create .md files other than the migration SQL
- ❌ Do NOT skip the AI reconfirmation step before sending pace alerts
- ❌ Do NOT send pace alert notifications to parents in real-time (weekly digest only)
- ❌ Do NOT block the teacher if AI is down — always provide manual fallback
- ❌ Do NOT forget to filter isActive=true when reading curriculum_units for parents

## START
Begin with Phase 0, step 1. Read the existing `Tables.kt` to understand the exact pattern for defining tables. Read `TeacherSyllabusRouting.kt` to understand the routing pattern. Then create the migration SQL files.
```

---

## 14. Summary

This plan delivers a complete, production-grade **Agentic Syllabus Management & AI Assignment System** that transforms syllabus management from manual data entry into an intelligent, self-monitoring, AI-powered ecosystem:

### Agentic Syllabus Lifecycle
- **AI-powered syllabus parsing** — image (vision LLM) or text → structured 3-level hierarchy (chapter → topic → subtopic) with teacher review
- **Daily check-in popup** — non-mandatory, post-class, with topic selection + coverage % + summary text. Suppression prefs (off/week/permanent).
- **AI estimated daily summary** — when teacher doesn't log, AI fills with estimated percentages, clearly labeled "AI Estimated" for parents
- **Pace monitoring with AI reconfirmation** — scheduled job calculates expected vs actual coverage. AI second-pass validates before sending alerts. Prevents false alarms.
- **Parent daily summary** — per-subject summary on home tab. Teacher-populated when available, AI-estimated otherwise. Integrates with existing `ParentCoveredDetailOverlay`.
- **Syllabus DELETE** — soft-delete with cascade. Parent/admin views hide deleted units.
- **Partial coverage** — `coverage_percent` (0-100) replaces boolean-only. 50% progress on a topic is now trackable.
- **Parent syllabus read migration** — moves from legacy `SyllabusUnitsTable` to typed `CurriculumUnitsTable` + `SyllabusProgressTable` with 3-level hierarchy.

### Agentic Assignment Generation
- **One-tap quiz generation** — from any covered topic, teacher generates MCQ + fill-blank + true/false questions via AI
- **Difficulty adjustment** — ±10% offset for class level. AI uses curated reference sources (NCERT, CBSE, Khan Academy, OpenStax) in system prompt.
- **Teacher review before publish** — full preview with edit/regenerate/publish. No unreviewed AI content reaches students.
- **Student quiz via parent app** — children answer from parent's phone. MCQ radio, fill-blank text, true/false toggle.
- **Auto-scoring + ranking** — server-side scoring with normalized matching. Real-time rank recalculation. Per-question correctness breakdown.
- **Results to parents** — teacher taps "Send Results" → all parents notified with child's score + rank.

### Cross-Cutting
- **7 new tables** — `syllabus_sources`, `daily_class_log`, `syllabus_pace_plan`, `syllabus_popup_prefs`, `syllabus_pace_alerts`, `quiz_questions`, `quiz_answers`
- **4 existing tables extended** — `curriculum_units` (depth), `syllabus_progress` (coverage_percent), `homework` (is_quiz, quiz_meta_json), `homework_submissions` (score, rank)
- **Covers all 3 portals** — teacher (upload, check-in, quiz gen, results), parent (daily summary, quiz answering, results), admin (pace alerts, coverage view)
- **AI cost-conscious** — BATCH lane for pace estimation, FAST_CHAT for summaries, REASON for parsing/quiz. Caching on parse results. Circuit breaker for provider failover.
- **Never blocks the teacher** — all AI features degrade gracefully. Manual syllabus entry always available. Parse failure → manual fallback. AI down → estimation from last known pace plan.
- **Follows all architectural standards** — SOLID, MVVM, Clean Architecture, Koin DI, offline-first, @SerialName on all DTOs
- **8 implementation phases** — Phase 0 (migrations) through Phase 7 (notifications + offline + testing)
