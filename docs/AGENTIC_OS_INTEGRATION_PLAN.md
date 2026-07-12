# Enroll+ Agentic OS — Integration & Expansion Plan

> Built from a full codebase audit of 35 server modules + 21 shared client modules.
> Every proposed feature is mapped: **expand existing** vs **build from scratch**.
> No feature works in isolation — every connection is documented.

---

## Part 1: Existing Feature Inventory (What We Already Have)

### 1.1 Server Modules (35 directories under `feature/`)

| Module | Key Files | What It Does Today |
|--------|-----------|-------------------|
| **admissions** | `AdmissionRouting.kt` | Enquiry CRUD, summary, efficiency metric (converted/(converted+follow_ups)) |
| **ai** | `AiService.kt`, `SyllabusAiService.kt`, `SyllabusPaceService.kt`, `DailySummaryAutoJob.kt` | Multi-provider LLM gateway (4 lanes: FAST_CHAT/CLASSIFY/REASON/BATCH), syllabus parsing (image+text), pace monitoring with AI reconfirmation, quiz generation, auto daily class log |
| **alumni** | `AlumniRouting.kt` | Alumni records |
| **announcements** | `AnnouncementRouting.kt` | School-wide announcements CRUD |
| **auth** | `AuthRouting.kt`, `OtpService.kt`, `OtpAdminRouting.kt` | JWT auth, OTP generation/delivery/verification with rate limiting |
| **branding** | `BrandingRouting.kt` | School branding (logo, colors, name) |
| **calendar** | `AcademicCalendarRouting.kt`, `AcademicYearRouting.kt` | Academic calendar events, year management |
| **config** | `AppStatusRouting.kt`, `VersionRouting.kt` | App status, version info |
| **content** | `LandingRouting.kt`, `SupportRouting.kt` | Landing page, support content |
| **devtools** | `DevToolsRouting.kt` | Dev tools (log viewer, AI usage) |
| **event** | `EventRegistrationRouting.kt` | Event registration |
| **fee** | `FeeService.kt` | Fee records, scholarship waiver application (fixed/full_waiver/partial_waiver) |
| **gateway** | `GatewayRouting.kt` | SMS gateway integration |
| **health** | `HealthRouting.kt` | Student health records, incidents |
| **i18n** | `I18nRouting.kt`, `ServerStrings.kt`, `UserLanguageResolver.kt` | Internationalization, multi-language support |
| **idcard** | `IdCardRouting.kt` | ID card generation |
| **library** | `LibraryRouting.kt` | Library books, borrowing, returns |
| **notification** | `NotificationRouting.kt`, `NotificationService.kt`, `DeviceTokenRepository.kt`, `FirebaseAdminInitializer.kt` | FCM push notification dispatch, device token management |
| **notifications** | `Notify.kt`, `NotificationsRouting.kt`, `NotificationScheduler.kt`, `NotificationPreferencesRouting.kt` | Notification spine (Notify.toUser/toUsers), rate limiting, deep links, preferences, scheduling |
| **onboarding** | `OnboardingRouting.kt` | School onboarding wizard |
| **organization** | `OrganizationRouting.kt` | Multi-branch/org management |
| **parent** | `ParentAcademicsRouting.kt`, `ParentDashboardRouting.kt`, `ParentFeesRouting.kt`, `ParentLeaveRouting.kt`, `ParentLinkRouting.kt`, `TrackProgressRouting.kt` | Parent dashboard, academics, fees, leave, child linking, progress tracking |
| **pews** | `PewsRouting.kt`, `PewsSnapshotService.kt`, `PewsReasoningService.kt`, `PewsInterventionService.kt`, `PewsDailyJob.kt`, `triage/`, `act/`, `learn/`, `caseworker/` | **Full PEWS pipeline**: deterministic risk scoring (attendance+marks+leave+homework+fees+health+transport+mastery) → AI triage → AI narrative → auto intervention (open task + notify owner) → learn (outcome tracking) |
| **pulse** | `ParentPulseService.kt`, `PulseRouting.kt`, `PulseWeeklyJob.kt` | Weekly parent pulse: aggregates attendance, marks, homework, announcements, messages, events into summary with AI narrative |
| **reportcard** | `assemble/`, `core/`, `data/`, `ecosystem/`, `learn/`, `narrator/`, `queue/`, `rollup/`, `triage/` | **Full AI Report Card 2.0**: Rollup (deterministic fact bundle) → Triage → Narrator (tool-using agent with grounding guard) → Assembly (draft→review→publish state machine) → Learn (effectiveness) |
| **scheduling** | `MessageDispatchScheduler.kt`, `ScheduledMessageRouting.kt` | Scheduled message dispatch |
| **scholarship** | `ScholarshipService.kt`, `ScholarshipRouting.kt` | Full scholarship workflow: scheme CRUD, application, approval/rejection, disbursement, renewal, fee integration via FeeService |
| **school** | `AdminDashboardRouting.kt`, `SchoolAnalyticsRouting.kt`, `SchoolIntelligenceRouting.kt`, `SchoolProfileRouting.kt`, `SchoolStudentsRouting.kt`, `SchoolClassesRouting.kt`, `SchoolTimetableRouting.kt`, `SchoolDayConfigRouting.kt`, `TeacherAssignmentRouting.kt`, `TeacherProvisioningRouting.kt`, `TimetableChangeRequestRouting.kt`, `TimetableImportRouting.kt`, `LeaveRequestsRouting.kt`, `MessagesRouting.kt`, `PtmRouting.kt`, `ResultsRouting.kt`, `SchoolLessonPlanRouting.kt`, `NonTeachingStaffRouting.kt`, `PeriodExceptionRouting.kt`, `SyllabusPaceRouting.kt`, `StudentAggregationService.kt` | **Admin command center**: intelligence dashboard (attendance timeline, early warning, academic health, activity feed), timetable, classes, students, teachers, leave, messages, PTM, results, lesson plans, non-teaching staff, syllabus pace admin |
| **teacher** | `TeacherAttendanceRouting.kt`, `TeacherClassesRouting.kt`, `TeacherDayRouting.kt`, `TeacherGradebookRouting.kt`, `TeacherHomeworkRouting.kt`, `TeacherLeaveRouting.kt`, `TeacherLessonPlanRouting.kt`, `TeacherMessagesRouting.kt`, `TeacherQuizRouting.kt`, `TeacherSelfLeaveRouting.kt`, `TeacherStudentRouting.kt`, `TeacherSyllabusRouting.kt` | **Full teacher portal**: attendance (typed, assignment-scoped), classes, day schedule, gradebook (assessments + marks), homework (assign + submissions board + extend + close), leave, lesson plans, messages, AI quiz generation, syllabus (hierarchical units + one-tap coverage toggle) |
| **transport** | Routes, stops, vehicles, assignments, attendance, GPS tracking, fees | Full transport module with live location, pickup/drop, parent view |
| **tutor** | `core/` (TutorModule, TutorModuleRegistry, TutorRouter, TutorConstants, TutorKillSwitch), `sense/`, `triage/`, `agent/`, `act/`, `learn/`, `ingest/`, `heatmap/`, `parent/`, `admin/`, `rag/` | **Full AI Tutor 2.0**: 5-tier pipeline (Sense→Triage→Agent→Act→Learn) + Ingest (OCR+Voice) + Teacher Heatmap + Parent Progress + Admin Efficacy + RAG. Modular registry pattern (plug-and-play) |
| **user** | `ParentRouting.kt`, `UserDetailsRouting.kt` | Parent scholarships, announcements, user details |
| **media** | `MediaRouting.kt` | Media uploads |

### 1.2 Key Existing AI/Agentic Capabilities

| Capability | Location | What It Does |
|-----------|----------|-------------|
| **AiService** | `feature/ai/AiService.kt` | Single choke point for ALL LLM calls. 4 lanes, multi-provider (Groq, Gemini, Cerebras, NVIDIA, OpenRouter, Mistral, SambaNova), PII guardrail, L1 cache, circuit breakers, jitter, usage logging |
| **PEWS Pipeline** | `feature/pews/` | Daily job: deterministic snapshot → AI triage → AI reasoning (narrative+cause+recommendation) → auto intervention (open task + notify) → learn (outcome tracking) |
| **Tutor 2.0** | `feature/tutor/` | 10 modules: Sense (learner bundle), Triage (classify+cache), Agent (tool-using LLM), Act (render+grade+notify), Learn (efficacy), Ingest (OCR+Voice), Heatmap, ParentProgress, AdminEfficacy, RAG |
| **Report Card 2.0** | `feature/reportcard/` | Rollup (fact bundle) → Triage → Narrator (agent with 6 tools + grounding guard) → Assembly (batch job, draft→review→publish) → Learn (effectiveness) |
| **Syllabus AI** | `feature/ai/SyllabusAiService.kt` | parseSyllabusImage (vision), parseSyllabusText, estimatePacePlan, generateDailySummary, reconfirmAlert, generateQuiz |
| **Syllabus Pace** | `feature/ai/SyllabusPaceService.kt` | Computes expected vs actual coverage, creates deviation alerts with AI reconfirmation, pace snapshots |
| **Parent Pulse** | `feature/pulse/ParentPulseService.kt` | Weekly aggregation: attendance, marks, homework, announcements, messages, events → AI narrative summary |
| **Notify Spine** | `feature/notifications/Notify.kt` | Single write-path for all notifications. Multi-recipient, rate-limited, deep-linked, FCM-pushed |
| **Daily Summary Auto Job** | `feature/ai/DailySummaryAutoJob.kt` | Auto-generates AI class summaries for teachers who didn't log manually |
| **Scholarship Workflow** | `feature/scholarship/ScholarshipService.kt` | Full workflow with fee integration (auto-apply waiver on approval) |
| **Kill Switches** | `feature/pews/core/KillSwitchConfig.kt` | Per-module feature flags for granular AI on/off |
| **Module Registry Pattern** | `TutorModuleRegistry.kt`, `ReportCardModuleRegistry.kt` | Plug-and-play module registration (SOLID: O/C) |

### 1.3 Shared Client Modules (21 directories)

admin, alumni, auth, branding, content, event, health, i18n, idcard, library, notification, parent, pews, reportcard, scheduling, scholarship, school, schools, teacher, transport, tutor

Each has `data/` (API + repository impl), `domain/` (models + repository interface), and previously had `presentation/` (ViewModels — deleted for UI rebuild).

---

## Part 2: Feature Classification — Expand vs Build From Scratch

### Category A: EXPAND Existing Modules (17 features)

These features have significant existing code to build upon. The expansion plan details what to add vs what to reuse.

#### F1. Auto-Attendance Reconciliation Agent
**Expands:** `teacher/TeacherAttendanceRouting.kt` + `transport/` + `school/LeaveRequestsRouting.kt` + `pews/` + `notifications/Notify.kt`

**Already exists:**
- Teacher marks attendance via `TeacherAttendanceRouting` (typed, assignment-scoped)
- Leave requests in `LeaveRequestsTable` (student + teacher)
- Transport GPS tracking with pickup/drop marking
- PEWS already uses attendance as a risk signal
- `Notify.toUser()` for parent notifications

**What to ADD:**
- `AttendanceReconciliationAgent.kt` — listens to `attendance.absent` event
- `AttendanceCrossVerifier.kt` — SQL joins: leave_requests + transport_gps + sibling attendance + calendar
- `AttendanceEscalationChain.kt` — timed escalation (WhatsApp → SMS → teacher → PEWS → admin)
- `AttendancePatternDetector.kt` — weekly SQL window functions for truancy patterns
- New table: `attendance_reconciliation_log`
- WhatsApp Business API integration (new `WhatsAppService.kt`)
- Event emission: `attendance.absent` → Orchestrator event bus

**Integration points:**
- Calls `Notify.toUser()` (existing) for push notifications
- Calls `PewsSnapshotService.addFactor()` (existing pattern) for PEWS flagging
- Reads `TransportAttendance` (existing) for bus GPS verification
- Reads `LeaveRequestsTable` (existing) for authorized leave check
- Reads `AcademicCalendarTable` (existing) for holiday check
- Feeds into F5 (engagement score), F3 (compliance), F7 (fee hold)

---

#### F2. Admission Lead-to-Enrollment Pipeline
**Expands:** `admissions/AdmissionRouting.kt`

**Already exists:**
- `AdmissionEnquiriesTable` with CRUD endpoints
- Enquiry summary with efficiency metric
- Status workflow (new → follow_up → converted)
- Pagination

**What to ADD:**
- `AdmissionLeadAgent.kt` — multi-channel capture (webhook endpoints)
- `LeadScoringService.kt` — AI scoring via `AiService.complete(CLASSIFY lane)`
- `LeadNurtureService.kt` — timed nurture sequence via Orchestrator
- `VisitScheduler.kt` — Google Calendar API integration
- `PostEnrollmentOnboarding.kt` — cascade trigger for account creation + class + transport + fees
- New tables: `admission_leads` (extends enquiries), `lead_interactions`, `lead_scores_history`
- WhatsApp Business webhook endpoint

**Integration points:**
- Enrollment confirmed → emits `enrollment.confirmed` event → triggers F6 (transport), F7 (fee schedule), F10 (first UPI), F15 (circular list), F16 (birthday), F3 (RTE quota)
- F19 (withdrawal) → opens seat → updates waitlist in F2
- F22 (scholarship) → checks eligibility during application stage
- F18 (reputation) → high conversion → auto-request reviews
- Uses `BrandingRouting` (existing) for branded emails

---

#### F4. AI Exam Paper Generator
**Expands:** `teacher/TeacherQuizRouting.kt` + `ai/SyllabusAiService.kt`

**Already exists:**
- `TeacherQuizRouting` — AI quiz generation from syllabus units (MCQ, FILL_BLANK, TRUE_FALSE, MATCH)
- `SyllabusAiService.generateQuiz()` — LLM-powered question generation
- `CurriculumUnitsTable` — hierarchical syllabus data
- Quiz results tracking with rankings

**What to ADD:**
- `ExamPaperGeneratorService.kt` — blueprint generation (topic coverage, difficulty, Bloom's)
- Extend `SyllabusAiService` with `generateExamPaper()` method
- `QuestionBankService.kt` — accumulate + tag + reuse questions
- `ExamVariationService.kt` — 4 versions with auto-variation
- `ExamModerationService.kt` — post-exam performance analysis
- New tables: `exam_papers`, `question_bank`, `exam_moderation`

**Integration points:**
- Pulls from `CurriculumUnitsTable` (existing) + `NcertReferenceData` (existing)
- Moderation data → feeds F17 (pace tracker) for teaching gap detection
- Weak topics → feeds Tutor 2.0 (existing) for auto-practice generation
- Results → feeds F11 (report card comments) + PEWS (existing)
- Question quality → feeds F23 (staff performance)

---

#### F5. Parent Engagement Scorer
**Expands:** `pulse/ParentPulseService.kt` + `notifications/`

**Already exists:**
- `ParentPulseService` — weekly aggregation of attendance, marks, homework, announcements, messages, events
- `NotificationsTable` — notification read tracking
- `MessageThreadsTable` — message response data
- `FeeRecordsTable` — payment history
- `LeaveRequestsTable` — leave proactiveness
- `PulseWeeklyJob` — scheduled weekly job pattern

**What to ADD:**
- `EngagementScoringService.kt` — 11-signal weighted scoring (0-100)
- `EngagementCorrelationService.kt` — cross-reference with student performance
- `EngagementCampaignService.kt` — targeted re-engagement campaigns
- Weekly digest generation (extends PulseWeeklyJob pattern)
- New tables: `parent_engagement_scores`, `engagement_campaigns`

**Integration points:**
- F1 (attendance) → unverified absences reduce score
- F7 (fees) → payment promptness is a signal
- F12 (PTM) → attendance is a signal
- F15 (circulars) → response rate is a signal
- F21 (queries) → message response time is a signal
- Champions (80-100) → F18 (reputation) auto-requests reviews
- Correlates with PEWS (existing) for student risk
- Tutor (existing) engagement is a signal

---

#### F6. Transport Route Optimizer
**Expands:** `transport/` (full module exists)

**Already exists:**
- `TransportRoute` with stops, sequence, estimated times
- `TransportVehicle` with capacity, driver info
- `TransportAssignment` linking students to routes
- `TransportTracking` with GPS (lat, lng, speed, heading)
- `RouteProgress` with next stop + ETA
- `TransportAttendance` with pickup/drop status
- Parent API: `getLiveLocation()`, `getRouteForChild()`
- Driver API: `updateLocation()`, `markPickup()`, `markDrop()`

**What to ADD:**
- `RouteOptimizationService.kt` — Google OR-Tools CP-SAT solver
- `DynamicRouter.kt` — auto-assign on new admission
- `GpsVarianceTracker.kt` — planned vs actual variance
- `RouteCostAnalyzer.kt` — per-route cost analysis
- `TransportAutoNotify.kt` — boarding/arrival/delay notifications
- Google Maps Geocoding + Distance Matrix API integration
- New tables: `transport_routes_optimized`, `gps_variance_log`

**Integration points:**
- F1 (attendance) → GPS cross-verification for absent students
- F2 (admissions) → new enrollment triggers route assignment
- F3 (compliance) → vehicle fitness + driver verification
- F7 (fees) → transport fee included in schedule
- F24 (emergency) → transport emergency → mass broadcast
- F5 (engagement) → transport update views feed score

---

#### F7. Smart Fee Recovery Agent
**Expands:** `fee/FeeService.kt` + `parent/ParentFeesRouting.kt`

**Already exists:**
- `FeeRecordsTable` with status (DUE/OVERDUE/PAID)
- `FeeService.applyScholarship()` — waiver application
- `ParentFeesRouting` — parent fee view
- `POST /api/v1/parent/fees/pay` endpoint (added recently)

**What to ADD:**
- `FeeRecoveryAgent.kt` — daily overdue detection + AI risk prediction
- `FeeEscalationService.kt` — timed escalation chain
- `FeeRecoveryAnalytics.kt` — recovery metrics
- WhatsApp + SMS + email templates for fee reminders
- PDF generation for formal notices (PDFBox)
- New table: `fee_recovery_actions`

**Integration points:**
- F1 (attendance) → authorized absence → fee hold
- F2 (admissions) → enrollment → fee schedule generation
- F5 (engagement) → payment promptness is a signal
- F10 (UPI) → payment link in reminder
- F3 (compliance) → collection rate for audit
- F19 (withdrawal) → fee clearance check
- F22 (scholarship) → disbursement → fee adjustment
- F13 (payroll) → fee collection funds payroll

---

#### F8. Homework Auto-Checker
**Expands:** `teacher/TeacherHomeworkRouting.kt`

**Already exists:**
- Full homework lifecycle: assign, submissions board (roster-joined), extend, grade, close
- `HomeworkSubmissionsTable` with submission tracking
- `HomeworkTable` with assignments, due dates, allow_late
- Teacher can mark submissions as reviewed/graded
- Parent notification on homework assignment (via Notify)

**What to ADD:**
- `HomeworkAutoCheckerService.kt` — AI grading (objective auto, subjective suggest)
- `HomeworkComplianceTracker.kt` — daily missing submission detection
- `HomeworkComplianceDigest.kt` — weekly per-student + per-class digest
- Google Vision OCR for photo submissions
- New tables: `homework_submissions` (extend with AI fields), `homework_compliance`

**Integration points:**
- F5 (engagement) → homework acknowledgment is a signal
- F17 (pace) → compliance feeds teaching gap detection
- F4 (exam) → weak topics from homework feed blueprint
- PEWS (existing) → 3 misses → PEWS signal
- F11 (report card) → homework scores feed comments
- F23 (staff review) → grading turnaround time
- Tutor 2.0 (existing) → weak students → auto-practice

---

#### F9. Auto-Timetable Generator
**Expands:** `school/SchoolTimetableRouting.kt` + `TimetableChangeRequestRouting.kt` + `TimetableImportRouting.kt`

**Already exists:**
- `SchoolTimetableRouting` — timetable CRUD
- `TimetableChangeRequestRouting` — change request workflow
- `TimetableImportRouting` — import functionality
- `TeacherSubjectAssignmentsTable` — teacher assignments
- `TeacherPeriodsTable` — period definitions
- `SchoolDayConfigRouting` — day configuration
- `PeriodExceptionRouting` — period exceptions

**What to ADD:**
- `TimetableOptimizationService.kt` — Google OR-Tools CP-SAT solver
- `SubstitutionFinder.kt` — auto-find substitute on teacher absence
- Constraint configuration UI (load balance, no back-to-back heavy, lab doubles)
- New tables: `timetable_generations`, `substitution_proposals`

**Integration points:**
- F1 (attendance) → timetable tells which teacher marks attendance
- F17 (pace) → period allocation vs syllabus coverage
- F23 (staff review) → teacher load balance data
- F12 (PTM) → teacher free slots for scheduling
- Calendar (existing) → sync generated timetable
- Teacher Portal (existing) → teachers see schedule
- Parent Portal (existing) → parents see child's schedule

---

#### F11. Auto-Report Card Comment Generator
**Expands:** `reportcard/narrator/NarratorService.kt`

**Already exists:**
- `NarratorService` — AI narration with tool-using agent + grounding guard
- `ReportFactBundle` — deterministic fact bundle (attendance, marks, co-scholastic)
- `ReportGroundingGuard` — verifies every number against facts
- `ReportAssemblyService` — batch orchestration (draft → review → publish)
- Multi-language support (existing i18n)
- `ReportLearnService` — effectiveness tracking

**What to ADD:**
- Extend `NarratorService` with `generateCommentOnly()` method (lighter than full report)
- Pull additional signals: F8 (homework compliance), F5 (parent engagement), Tutor heatmap
- `CommentTemplateService.kt` — per-school comment style customization
- Auto-translation via i18n (existing `UserLanguageResolver`)

**Integration points:**
- F8 (homework) → compliance data feeds comments
- F5 (engagement) → engagement context in comments
- F4 (exam) → exam results feed comments
- PEWS (existing) → risk context
- Tutor (existing) → heatmap data
- F17 (pace) → syllabus coverage context
- i18n (existing) → multi-language translation

---

#### F12. PTM Auto-Scheduler
**Expands:** `school/PtmRouting.kt`

**Already exists:**
- PTM CRUD (create, get, complete)
- `PtmClassProgressTable` — per-class met/total tracking
- `PtmEventsTable` — PTM event management
- Calendar event creation integration
- Metrics tracking (check-ins, invites, read receipts)

**What to ADD:**
- `PtmSchedulerService.kt` — AI slot allocation
- `PtmPreferenceForm.kt` — parent preference collection
- `PtmAutoBooking.kt` — Google Calendar + Meet integration
- `PtmBriefGenerator.kt` — AI per-student talking points
- New tables: `ptm_schedules`, `ptm_slots`, `ptm_preferences`

**Integration points:**
- F1 (attendance) → high unverified absences → priority slots
- F5 (engagement) → engagement score → slot priority
- F9 (timetable) → teacher free slots
- F11 (report card) → pre-meeting brief from comments
- F8 (homework) → homework data for discussion
- PEWS (existing) → flagged students get longer slots

---

#### F15. Auto-Circular & Consent Tracker
**Expands:** `announcements/AnnouncementRouting.kt` + `notifications/Notify.kt`

**Already exists:**
- `AnnouncementRouting` — announcement CRUD
- `AnnouncementsTable` — school-scoped announcements
- `Notify.toUsers()` — multi-recipient notification with deep links
- `NotificationScheduler` — scheduled dispatch
- Parent announcement reading via `ParentRouting`

**What to ADD:**
- `CircularService.kt` — extends announcements with consent tracking
- `ConsentTracker.kt` — digital consent (acknowledge/decline)
- `ConsentReminderJob.kt` — auto-reminders (48h, 72h escalation)
- Live dashboard data endpoint
- New tables: `circulars` (extends announcements), `circular_consents`

**Integration points:**
- F1 (attendance) → absent students get extended deadline
- F2 (admissions) → new parents auto-added to circular list
- F5 (engagement) → response rate is a signal
- F24 (emergency) → emergency circulars via F24 broadcast
- F21 (queries) → circular-related queries auto-routed

---

#### F17. Auto-Syllabus Pace Catch-Up Planner
**Expands:** `ai/SyllabusPaceService.kt` + `school/SyllabusPaceRouting.kt`

**Already exists:**
- `SyllabusPaceService` — computes expected vs actual coverage
- Pace snapshots with deviation levels (ON_TRACK/BEHIND/CRITICAL/AHEAD)
- `SyllabusPaceAlertsTable` — alerts with AI reconfirmation
- `SyllabusPaceRouting` — admin endpoints (snapshots, alerts, coverage, recalculate)
- `SyllabusAiService.reconfirmAlert()` — AI second-pass validation
- `SyllabusAiService.estimatePacePlan()` — pace estimation

**What to ADD:**
- `PaceCatchUpPlanner.kt` — AI reallocation suggestions
- `PaceProjectionService.kt` — "at current pace, 78% covered before exam"
- Integration with F9 (timetable) for period reallocation
- Auto-adjust lesson plan recommendations

**Integration points:**
- F4 (exam) → moderation data → teaching gap detection
- F8 (homework) → compliance → pace context
- F9 (timetable) → period allocation vs coverage
- Tutor (existing) → weak topics for practice
- Syllabus (existing) → units/topics data
- Calendar (existing) → remaining working days

---

#### F19. Auto-Withdrawal & TC Workflow
**Expands:** `idcard/IdCardRouting.kt` + `school/SchoolStudentsRouting.kt` + `library/` + `fee/`

**Already exists:**
- `IdCardRouting` — ID card generation
- `SchoolStudentsRouting` — student management
- `LibraryRouting` — book borrowing/returns
- `FeeService` — fee records
- `StudentsTable` — student records

**What to ADD:**
- `WithdrawalService.kt` — withdrawal request + clearance checklist
- `TcGenerator.kt` — PDF TC generation (branded template)
- `ClearanceOrchestrator.kt` — parallel department clearance
- Digital signature for principal
- New tables: `withdrawal_requests`, `clearance_checklist`

**Integration points:**
- F2 (admissions) → seat opens → waitlist update
- F7 (fees) → fee clearance check
- F3 (compliance) → TC compliance (issued within 7 days)
- F6 (transport) → route removal
- F10 (UPI) → fee settlement
- Library (existing) → book return check
- ID Card (existing) → card return check

---

#### F21. Auto-Parent Query Router
**Expands:** `school/MessagesRouting.kt` + `ai/AiService.kt` + `tutor/rag/`

**Already exists:**
- `MessagesRouting` — messaging between parents and school
- `MessageThreadsTable` — conversation threads
- `AiService` — classification lane (CLASSIFY)
- `RagModule` — RAG for knowledge retrieval (existing in tutor)
- `MessagingCore.kt` — messaging core logic

**What to ADD:**
- `QueryClassificationService.kt` — AI classifies (fee/homework/health/general)
- `FaqRagService.kt` — FAQ retrieval using existing RAG pattern
- `QueryRoutingService.kt` — route to right person
- `QueryAnalyticsService.kt` — response time + resolution tracking
- New tables: `parent_queries`, `query_routing_log`

**Integration points:**
- F1 (attendance) → absence replies → attendance reconciliation
- F5 (engagement) → response time is a signal
- F7 (fees) → fee queries → fee info auto-response
- F15 (circulars) → circular queries auto-routed
- Messages (existing) → conversation tracking
- Notifications (existing) → notify routed person

---

#### F22. Auto-Grant & Scholarship Finder
**Expands:** `scholarship/ScholarshipService.kt`

**Already exists:**
- `ScholarshipService` — full workflow (scheme CRUD, application, approval, disbursement, renewal)
- `FeeService.applyScholarship()` — auto-apply waiver on fee records
- `ScholarshipsTable` — scheme management
- Notification on status changes (existing)
- Gamification elements (existing)

**What to ADD:**
- `ExternalScholarshipScraper.kt` — Playwright + Cheerio for gov/private databases
- `ScholarshipMatchService.kt` — AI matching students to external schemes
- `AutoFillService.kt` — pre-fill application forms from student data
- New tables: `external_scholarships`, `scholarship_matches`

**Integration points:**
- F2 (admissions) → lead stage checks eligibility
- F7 (fees) → disbursement → fee adjustment (existing FeeService)
- F3 (compliance) → RTE reimbursement claims
- F5 (engagement) → engagement boost
- Scholarships (existing) → extends current workflow

---

#### F24. Auto-Emergency Broadcast
**Expands:** `notifications/Notify.kt` + `announcements/`

**Already exists:**
- `Notify.toUsers()` — multi-recipient notification with FCM
- `NotificationService` — FCM push with multi-device fan-out
- `NotificationsTable` — notification storage
- `AnnouncementsTable` — announcement storage
- Deep link support (existing)
- Rate limiting (existing)

**What to ADD:**
- `EmergencyBroadcastService.kt` — one-tap multi-channel dispatch
- `EmergencyBroadcastRouting.kt` — admin endpoint
- Simultaneous: push + WhatsApp + SMS + email + website banner
- Delivery tracking + auto-retry
- New table: `emergency_broadcasts`

**Integration points:**
- F1 (attendance) → attendance context for emergencies
- F6 (transport) → transport emergency → mass broadcast
- F15 (circulars) → emergency circular integration
- F20 (CCTV) → security emergency → broadcast
- F2 (admissions) → parent contact list
- Notifications (existing) → FCM dispatch

---

#### F25. Auto-Competitive Exam Tracker
**Expands:** `tutor/` + `ai/SyllabusAiService.kt`

**Already exists:**
- Tutor 2.0 — full adaptive learning pipeline with practice questions
- `SyllabusAiService` — quiz generation, pace estimation
- `TutorMasteryTable` — per-topic mastery tracking
- `TutorSessionRepository` — session history
- Adaptive plans via `TutorPlanViewModel`

**What to ADD:**
- `CompetitiveExamService.kt` — exam registration + date tracking
- `ExamPrepPlanner.kt` — AI parallel study plan
- `SyllabusOverlapService.kt` — map school syllabus vs exam syllabus
- `ExamResultTracker.kt` — post-exam result recording
- New tables: `competitive_exams`, `exam_prep_plans`, `exam_results`

**Integration points:**
- F4 (exam) → gap topics from exam data
- F8 (homework) → homework load adjustment during prep
- F17 (pace) → syllabus overlap analysis
- F11 (report card) → achievement in comments
- Tutor (existing) → practice modules
- F5 (engagement) → engagement signal
- F23 (staff review) → teacher credit for results

---

### Category B: BUILD From Scratch (8 features)

These features have no significant existing code to build upon. They pull data FROM existing modules but require new modules entirely.

#### F3. Compliance & Audit Agent
**New module:** `feature/compliance/`
**Pulls data from:** `school/` (teacher quals, student count), `health/` (health records), `library/` (book count), `transport/` (vehicle fitness), `fee/` (fee collection), `pews/` (attendance rate)

**New tables:** `compliance_items`, `compliance_certificates`
**New tech:** PDFBox (report generation), board-specific JSON config templates
**Pattern:** Follows `PewsDailyJob` pattern (daily check + alert)

#### F10. UPI Auto-Fee Collection
**New module:** `feature/payment/`
**Connects to:** `fee/FeeService.kt` (fee records), `parent/ParentFeesRouting.kt` (parent fee view)

**New table:** `upi_payment_links`
**New tech:** Razorpay/Cashfree UPI Collect + Intent API, Ktor webhook endpoint
**Pattern:** Webhook handler like existing `GatewayRouting`

#### F13. Auto-Staff Payroll
**New module:** `feature/payroll/`
**Pulls data from:** `teacher/TeacherAttendanceRouting.kt` (teacher attendance), `school/LeaveRequestsRouting.kt` (leave), `school/NonTeachingStaffRouting.kt` (non-teaching staff), `fee/FeeService.kt` (fee collection funds)

**New tables:** `payroll_runs`, `payslips`
**New tech:** PDFBox (payslip PDF), Orchestrator for auto-dispatch
**Pattern:** Monthly job like `PulseWeeklyJob`

#### F14. Smart Inventory & Procurement Agent
**New module:** `feature/inventory/`
**Minimal existing overlap** — only connects to F3 (compliance for infrastructure count) and F2 (new student demand)

**New tables:** `inventory_items`, `inventory_transactions`, `purchase_orders`
**New tech:** `AiService.complete(REASON lane)` for consumption prediction, vendor email/webhook
**Pattern:** Follows module registry pattern

#### F16. Auto-Birthday & Event Wisher
**New module:** `feature/birthday/`
**Pulls data from:** `students/` (birthdays), `branding/` (branded card)

**New table:** `birthday_log`
**New tech:** WhatsApp Business API, PDFBox (digital card)
**Pattern:** Daily job like `DailySummaryAutoJob`

#### F18. Auto-School Reputation Monitor
**New module:** `feature/reputation/`
**Minimal existing overlap** — connects to F5 (Champions → request reviews) and F2 (rating → lead nurturing)

**New tables:** `reputation_reviews`, `reputation_sentiment_log`
**New tech:** Playwright + Cheerio (web scraping), `AiService.complete(CLASSIFY lane)` for sentiment
**Pattern:** Daily job

#### F20. Auto-CCTV Incident Detector
**New module:** `feature/cctv/`
**Minimal existing overlap** — connects to PEWS (repeated incidents) and F24 (security emergency)

**New tables:** `cctv_incidents`, `cctv_alerts`
**New tech:** Google Vision API / YOLOv8 (self-hosted), existing CCTV RTSP feeds
**Pattern:** Continuous processing service

#### F23. Auto-Staff Performance Reviewer
**New module:** `feature/staffreview/`
**Pulls data from:** `teacher/` (attendance, gradebook, syllabus), `pews/` (student correlation), `tutor/` (heatmap), `ai/SyllabusPaceService.kt` (pace), F8 (grading turnaround), F21 (feedback sentiment)

**New table:** `staff_performance_reviews`
**New tech:** `AiService.complete(REASON lane)` for review generation
**Pattern:** Quarterly job + on-demand

---

## Part 3: User-Wise Data Flow

### 3.1 Admin Data Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                         ADMIN DAILY FLOW                         │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  MORNING (auto, admin sees only exceptions)                      │
│  ┌──────────────────────────────────────────────────────┐       │
│  │ F1 Attendance Agent runs automatically:               │       │
│  │   Teacher marks absent → F1 cross-verifies            │       │
│  │   → 80% auto-resolved (leave/bus/sibling)             │       │
│  │   → 20% unverified → escalation chain                 │       │
│  │   → Only T+2hr escalations reach admin                │       │
│  └──────────────────────────────────────────────────────┘       │
│  ┌──────────────────────────────────────────────────────┐       │
│  │ F6 Transport Auto-Notify runs:                        │       │
│  │   Bus delays >10min → parents auto-notified           │       │
│  │   → Only breakdowns reach admin                        │       │
│  └──────────────────────────────────────────────────────┘       │
│  ┌──────────────────────────────────────────────────────┐       │
│  │ F7 Fee Recovery runs daily:                           │       │
│  │   Overdue → AI predicts risk → auto-reminders         │       │
│  │   → Only Day-14+ defaulters on admin call list        │       │
│  └──────────────────────────────────────────────────────┘       │
│  ┌──────────────────────────────────────────────────────┐       │
│  │ F21 Query Router runs:                                │       │
│  │   70% auto-answered → 30% routed to right person      │       │
│  │   → Admin sees only fee-related queries               │       │
│  └──────────────────────────────────────────────────────┘       │
│                                                                  │
│  MIDDAY (admin actions)                                          │
│  ┌──────────────────────────────────────────────────────┐       │
│  │ F9 Timetable: teacher absent → auto-substitute found  │       │
│  │   → Admin 1-tap approve → auto-notify affected classes│       │
│  └──────────────────────────────────────────────────────┘       │
│  ┌──────────────────────────────────────────────────────┐       │
│  │ F15 Circular: admin posts → auto multi-channel send   │       │
│  │   → Auto-track consents → only non-responders flagged  │       │
│  └──────────────────────────────────────────────────────┘       │
│  ┌──────────────────────────────────────────────────────┐       │
│  │ F2 Admissions: leads auto-scored + nurtured            │       │
│  │   → Only priority leads (80+) reach admin for visit    │       │
│  └──────────────────────────────────────────────────────┘       │
│                                                                  │
│  EVENING (auto-digest)                                           │
│  ┌──────────────────────────────────────────────────────┐       │
│  │ Auto-Daily Digest (4 PM):                              │       │
│  │   AI compiles: attendance %, fee collection,           │       │
│  │   incidents, pending tasks, compliance warnings        │       │
│  │   → Delivered via push + WhatsApp                      │       │
│  └──────────────────────────────────────────────────────┘       │
│                                                                  │
│  WEEKLY (auto)                                                   │
│  ┌──────────────────────────────────────────────────────┐       │
│  │ F5 Engagement scores updated → at-risk parents flagged │       │
│  │ F14 Inventory prediction → auto-PO for low stock       │       │
│  │ F3 Compliance check → expiring certificates alerted    │       │
│  │ F23 Staff performance data compiled                    │       │
│  └──────────────────────────────────────────────────────┘       │
│                                                                  │
│  MONTHLY (auto)                                                  │
│  ┌──────────────────────────────────────────────────────┐       │
│  │ F13 Payroll auto-calculated → payslips auto-sent      │       │
│  │ F6 Route efficiency report → auto-suggests changes    │       │
│  │ F3 Compliance audit PDF → always inspection-ready     │       │
│  │ F18 Reputation report → sentiment trend               │       │
│  └──────────────────────────────────────────────────────┘       │
│                                                                  │
│  QUARTERLY/ANNUAL (auto)                                         │
│  ┌──────────────────────────────────────────────────────┐       │
│  │ F9 Timetable auto-generated → admin reviews           │       │
│  │ F12 PTM auto-scheduled → admin confirms               │       │
│  │ F23 Staff performance reviews auto-drafted             │       │
│  │ F3 Annual compliance audit → one-click export         │       │
│  └──────────────────────────────────────────────────────┘       │
│                                                                  │
│  DATA SOURCES:                                                   │
│  ├── SchoolIntelligenceRouting (existing) → command center     │
│  ├── F1 reconciliation_log → attendance exceptions             │
│  ├── F7 fee_recovery_actions → defaulter list                  │
│  ├── F3 compliance_items → audit status                         │
│  ├── F5 engagement_scores → parent engagement                  │
│  ├── F2 admission_leads → funnel analytics                     │
│  ├── F14 inventory_items → stock alerts                         │
│  ├── F6 gps_variance_log → route efficiency                    │
│  ├── F13 payroll_runs → salary summary                          │
│  └── F18 reputation_sentiment_log → online sentiment            │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 3.2 Teacher Data Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                        TEACHER DAILY FLOW                        │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  MORNING                                                         │
│  ┌──────────────────────────────────────────────────────┐       │
│  │ TeacherDayRouting (existing): today's schedule        │       │
│  │   + F9 auto-substitute notifications if timetable     │       │
│  │     changed due to another teacher's absence           │       │
│  └──────────────────────────────────────────────────────┘       │
│  ┌──────────────────────────────────────────────────────┐       │
│  │ TeacherAttendanceRouting (existing): mark attendance  │       │
│  │   → F1 auto-reconciles → teacher only sees            │       │
│  │     unverified absences at T+45min                    │       │
│  │   → F1 auto-creates leave from parent WhatsApp reply  │       │
│  └──────────────────────────────────────────────────────┘       │
│  ┌──────────────────────────────────────────────────────┐       │
│  │ F8 Homework check: AI already graded objective        │       │
│  │   → Teacher reviews AI-suggested marks for subjective │       │
│  │   → F8 auto-notified parents of missing submissions   │       │
│  └──────────────────────────────────────────────────────┘       │
│                                                                  │
│  DURING CLASS                                                    │
│  ┌──────────────────────────────────────────────────────┐       │
│  │ TeacherSyllabusRouting (existing): one-tap coverage   │       │
│  │   → F17 auto-calculates pace → "3 units behind"       │       │
│  │   → F17 suggests: "skip Unit 4 review, focus Unit 7"  │       │
│  └──────────────────────────────────────────────────────┘       │
│  ┌──────────────────────────────────────────────────────┐       │
│  │ TeacherQuizRouting (existing): AI quiz from units     │       │
│  │   → F4 extends: full exam paper with blueprint        │       │
│  │   → 4 auto-variations for anti-cheating               │       │
│  └──────────────────────────────────────────────────────┘       │
│  ┌──────────────────────────────────────────────────────┐       │
│  │ Tutor Heatmap (existing): real-time class heatmap     │       │
│  │   → Shows which students need help now                │       │
│  └──────────────────────────────────────────────────────┘       │
│                                                                  │
│  END OF DAY                                                      │
│  ┌──────────────────────────────────────────────────────┐       │
│  │ TeacherGradebookRouting (existing): enter marks       │       │
│  │   → F4 auto-extracts from photo (OMR/OCR)             │       │
│  │   → Auto-populates gradebook                          │       │
│  └──────────────────────────────────────────────────────┘       │
│  ┌──────────────────────────────────────────────────────┐       │
│  │ F21 Query Router: only queries needing teacher input  │       │
│  │   → AI handled 70% automatically                      │       │
│  └──────────────────────────────────────────────────────┘       │
│  ┌──────────────────────────────────────────────────────┐       │
│  │ DailySummaryAutoJob (existing): AI auto-logs class    │       │
│  │   summary if teacher didn't write one                 │       │
│  └──────────────────────────────────────────────────────┘       │
│                                                                  │
│  WEEKLY                                                         │
│  ┌──────────────────────────────────────────────────────┐       │
│  │ F4 Exam Paper: AI generates full paper from syllabus  │       │
│  │   → Teacher reviews blueprint → AI generates questions│       │
│  │   → 4 variations + answer keys auto-generated         │       │
│  └──────────────────────────────────────────────────────┘       │
│  ┌──────────────────────────────────────────────────────┐       │
│  │ F5 Engagement digest: "3 at-risk parents need         │       │
│  │   outreach" → one-tap call/WhatsApp                   │       │
│  └──────────────────────────────────────────────────────┘       │
│  ┌──────────────────────────────────────────────────────┐       │
│  │ F8 Compliance digest: per-student submission rate     │       │
│  └──────────────────────────────────────────────────────┘       │
│                                                                  │
│  QUARTERLY                                                      │
│  ┌──────────────────────────────────────────────────────┐       │
│  │ F11 Report Card: NarratorService (existing) generates │       │
│  │   comments → teacher reviews → admin publishes         │       │
│  │   → F11 adds homework + engagement + pace context     │       │
│  └──────────────────────────────────────────────────────┘       │
│  ┌──────────────────────────────────────────────────────┐       │
│  │ F12 PTM: AI generates per-student brief               │       │
│  │   → Strengths, gaps, action items from all modules    │       │
│  └──────────────────────────────────────────────────────┘       │
│  ┌──────────────────────────────────────────────────────┐       │
│  │ F23 Staff Review: data-driven performance draft       │       │
│  │   → Principal reviews → shares with teacher           │       │
│  └──────────────────────────────────────────────────────┘       │
│                                                                  │
│  DATA SOURCES:                                                   │
│  ├── TeacherDayRouting (existing) → schedule                    │
│  ├── TeacherAttendanceRouting (existing) → attendance          │
│  ├── TeacherSyllabusRouting (existing) → coverage              │
│  ├── TeacherGradebookRouting (existing) → marks                │
│  ├── TeacherHomeworkRouting (existing) → homework              │
│  ├── TeacherQuizRouting (existing) → quizzes                   │
│  ├── Tutor Heatmap (existing) → class performance              │
│  ├── F1 reconciliation_log → unverified absences              │
│  ├── F4 exam_moderation → question performance                 │
│  ├── F5 engagement_scores → parent outreach list              │
│  ├── F8 homework_compliance → submission rates                 │
│  ├── F17 pace snapshots → syllabus pace                        │
│  ├── F21 query_routing_log → parent queries                    │
│  └── F23 staff_performance_reviews → self-review               │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 3.3 Parent Data Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                         PARENT DAILY FLOW                        │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  MORNING                                                         │
│  ┌──────────────────────────────────────────────────────┐       │
│  │ F6 Transport Auto-Notify (auto):                      │       │
│  │   "Bus arriving in 10 min" → "Boarded at 7:14"        │       │
│  │   → Zero manual action needed                         │       │
│  └──────────────────────────────────────────────────────┘       │
│  ┌──────────────────────────────────────────────────────┐       │
│  │ F1 Attendance Alert (conditional):                    │       │
│  │   If child absent → auto-WhatsApp "confirm absence"   │       │
│  │   → Parent replies "sick" → AI auto-creates leave     │       │
│  │   → If child present → no notification (silence)      │       │
│  └──────────────────────────────────────────────────────┘       │
│                                                                  │
│  DURING DAY                                                      │
│  ┌──────────────────────────────────────────────────────┐       │
│  │ F8 Homework Auto-Push (auto):                         │       │
│  │   "Maths homework due tomorrow" → auto-delivered      │       │
│  │   → If not submitted by deadline → auto-reminder      │       │
│  └──────────────────────────────────────────────────────┘       │
│  ┌──────────────────────────────────────────────────────┐       │
│  │ F15 Circular (auto):                                  │       │
│  │   School circular → WhatsApp + app + email            │       │
│  │   → Parent taps "Acknowledge" → auto-tracked          │       │
│  │   → 48h no response → auto-reminder                   │       │
│  └──────────────────────────────────────────────────────┘       │
│  ┌──────────────────────────────────────────────────────┐       │
│  │ F21 Query Router: parent asks in app                  │       │
│  │   → AI auto-answers 70% from FAQ                      │       │
│  │   → Complex queries routed to right person            │       │
│  └──────────────────────────────────────────────────────┘       │
│                                                                  │
│  EVENING                                                         │
│  ┌──────────────────────────────────────────────────────┐       │
│  │ F6 Transport Drop (auto):                             │       │
│  │   "Dropped at stop at 3:42 PM" → auto-pushed          │       │
│  └──────────────────────────────────────────────────────┘       │
│  ┌──────────────────────────────────────────────────────┐       │
│  │ F10 UPI Fee Link (conditional, before due date):      │       │
│  │   "Fee due in 7 days" → UPI deep link → one-tap pay   │       │
│  │   → Auto-receipt → auto-ledger update                 │       │
│  └──────────────────────────────────────────────────────┘       │
│  ┌──────────────────────────────────────────────────────┐       │
│  │ ParentPulseService (existing, weekly):                │       │
│  │   "This week: attendance 94%, Maths quiz 8/10,        │       │
│  │    all homework submitted" → AI narrative             │       │
│  │   → F5 adds engagement context                        │       │
│  └──────────────────────────────────────────────────────┘       │
│                                                                  │
│  WEEKLY                                                         │
│  ┌──────────────────────────────────────────────────────┐       │
│  │ F4 Test Result Push (auto):                           │       │
│  │   Score + class average + AI insight → instant push   │       │
│  └──────────────────────────────────────────────────────┘       │
│  ┌──────────────────────────────────────────────────────┐       │
│  │ F16 Birthday (conditional):                           │       │
│  │   If child's birthday → auto WhatsApp + digital card  │       │
│  └──────────────────────────────────────────────────────┘       │
│                                                                  │
│  MONTHLY/QUARTERLY                                              │
│  ┌──────────────────────────────────────────────────────┐       │
│  │ F11 Report Card (existing + enhanced):                │       │
│  │   AI comments → multi-language → plain language       │       │
│  │   → "Aarav shows strong analytical skills..."          │       │
│  └──────────────────────────────────────────────────────┘       │
│  ┌──────────────────────────────────────────────────────┐       │
│  │ F12 PTM (auto-scheduled):                             │       │
│  │   Preference form → AI allocates slot → auto-confirm  │       │
│  │   → Day-before reminder → Meet link included          │       │
│  └──────────────────────────────────────────────────────┘       │
│  ┌──────────────────────────────────────────────────────┐       │
│  │ F22 Scholarship (auto-matched):                       │       │
│  │   "Your child is eligible for [Scheme] — ₹25,000"     │       │
│  │   → Pre-filled form → parent just confirms            │       │
│  └──────────────────────────────────────────────────────┘       │
│  ┌──────────────────────────────────────────────────────┐       │
│  │ F25 Competitive Exam (opt-in):                        │       │
│  │   Study plan + progress + gap topics → weekly update  │       │
│  └──────────────────────────────────────────────────────┘       │
│                                                                  │
│  DATA SOURCES:                                                   │
│  ├── ParentDashboardRouting (existing) → dashboard              │
│  ├── ParentAcademicsRouting (existing) → academics              │
│  ├── ParentFeesRouting (existing) → fees                        │
│  ├── ParentPulseService (existing) → weekly summary             │
│  ├── TutorApi (existing) → progress card + efficacy             │
│  ├── TransportApi (existing) → live location + route            │
│  ├── F1 attendance alerts → absence confirmation                │
│  ├── F6 transport notifications → boarding/arrival              │
│  ├── F8 homework push → assignments + compliance                │
│  ├── F10 UPI links → fee payment                                │
│  ├── F15 circulars → consent tracking                           │
│  ├── F16 birthday → wishes                                      │
│  ├── F22 scholarship matches → eligibility                      │
│  └── F25 exam prep → study plan + progress                      │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## Part 4: Integration Architecture — How Features Connect

### 4.1 The Agent Orchestrator (New Core)

The Orchestrator sits between all features. It's the nervous system that makes the OS "agentic."

**Location:** `server/.../feature/orchestrator/`

**Pattern:** Follows existing `TutorModuleRegistry` + `PewsDailyJob` patterns.

```
Existing Module emits event → Orchestrator Event Bus
  → Condition Engine evaluates rules
  → Action Dispatcher sends via WhatsApp/SMS/Email/Push/UPI/PDF
  → Audit Logger records every decision
  → Outcome Tracker waits for response
  → Learning Loop adjusts thresholds monthly
```

### 4.2 Event Chain Examples

**Chain 1: Student Absent → Full Reconciliation**
```
TeacherAttendanceRouting marks absent
  → event: attendance.absent
  → F1 Agent: cross-verify (leave? bus? sibling? calendar?)
  → If unverified:
    → T+5min: WhatsApp parent (via WhatsAppService)
    → T+25min: SMS parent (via existing OtpDeliveryProvider)
    → T+45min: Notify class teacher (via Notify.toUser)
    → T+60min: PEWS flag (via PewsSnapshotService.addFactor)
    → T+2hr: Escalate to admin (via Notify.toUser + dashboard)
  → Parent replies "sick":
    → F21 Query Router parses reply
    → F1 auto-creates leave (LeaveRequestsTable)
    → Updates attendance to "Authorized Absence"
    → Cancels all pending escalations
    → Logs outcome in agent_audit_log
  → If 3 unverified absences in 2 weeks:
    → F1 PatternDetector creates PEWS signal
    → F12 PTM Scheduler gives priority slot
    → F5 Engagement Scorer reduces parent score
```

**Chain 2: New Admission → Full Onboarding Cascade**
```
F2 Admission Pipeline: lead scored 85 → nurtured → visited → applied → approved
  → event: enrollment.confirmed
  → F6 Transport: geocode address → find nearest stop → assign route
  → F7 Fee Recovery: generate fee schedule → create fee records
  → F10 UPI: send first fee UPI link
  → F15 Circular: add parent to circular distribution list
  → F16 Birthday: add child birthday to daily job
  → F3 Compliance: update RTE quota tracking
  → F22 Scholarship: check eligibility → notify if match
  → Onboarding (existing): trigger onboarding wizard
  → Branding (existing): welcome email with school branding
```

**Chain 3: Exam Completed → Academic Improvement Loop**
```
TeacherGradebookRouting publishes marks
  → event: test.completed
  → F4 Exam Moderation: AI analyzes per-question performance
    → "Q7 too hard — 85% scored 0"
    → "Topic X: class avg 40% → teaching gap"
  → F17 Pace Tracker: adjusts pace recommendation
    → "Reallocate 2 periods to Topic X"
  → Tutor 2.0 (existing): auto-generates practice for weak topics
  → F11 Report Card: includes exam performance in comments
  → PEWS (existing): consistent low scores → risk flag
  → F23 Staff Review: question quality + moderation feeds teacher eval
  → F5 Engagement: test result push to parent → engagement signal
```

**Chain 4: Fee Overdue → Recovery Cascade**
```
FeeService daily job: fee overdue detected
  → event: fee.overdue
  → F7 Smart Recovery: AI predicts payment likelihood
    → Low risk: WhatsApp reminder (friendly)
    → Medium risk: WhatsApp + SMS + email
    → High risk: all channels + admin call list
  → F10 UPI: include payment link in reminder
  → F5 Engagement: payment promptness updates score
  → If parent responds "will pay on 15th":
    → F7 pauses reminders → resumes on 16th if unpaid
  → If partial payment:
    → F10 auto-adjusts balance → F7 continues for remaining
  → Day 21: F7 auto-drafts formal notice (PDF, branded)
  → Day 30: F7 escalates to principal
  → F3 Compliance: collection rate updates audit
```

### 4.3 Cross-Feature Data Dependencies

```
F1 ──→ F5 (absence reduces engagement score)
F1 ──→ F3 (attendance rate for compliance)
F1 ──→ F7 (authorized absence → fee hold)
F1 ──→ F12 (priority PTM for high absences)
F1 ──→ F15 (extended circular deadline for absent)
F1 ──→ F23 (teacher marking timeliness)
F1 ──→ PEWS (unverified absence → risk factor)

F2 ──→ F6 (enrollment → route assignment)
F2 ──→ F7 (enrollment → fee schedule)
F2 ──→ F10 (enrollment → first UPI link)
F2 ──→ F15 (enrollment → circular list)
F2 ──→ F16 (enrollment → birthday list)
F2 ──→ F3 (enrollment → RTE quota)
F2 ──→ F22 (lead → scholarship check)

F4 ──→ F17 (moderation → teaching gaps)
F4 ──→ Tutor (weak topics → practice)
F4 ──→ F11 (results → comments)
F4 ──→ F23 (question quality → teacher eval)
F4 ──→ PEWS (low scores → risk)
F4 ──→ F25 (gap topics → exam prep)

F5 ──→ F18 (Champions → request reviews)
F5 ←── F1 (absences → score)
F5 ←── F7 (payment → score)
F5 ←── F12 (PTM → score)
F5 ←── F15 (circulars → score)
F5 ←── F21 (messages → score)
F5 ←── Tutor (engagement → score)

F6 ──→ F1 (GPS → attendance verification)
F6 ──→ F3 (vehicle fitness → compliance)
F6 ──→ F7 (transport fee → fee schedule)
F6 ──→ F24 (emergency → broadcast)

F7 ──→ F5 (payment → engagement)
F7 ──→ F3 (collection rate → compliance)
F7 ──→ F13 (collection → payroll funding)
F7 ←── F1 (absence → fee hold)
F7 ←── F2 (enrollment → fee schedule)
F7 ←── F10 (UPI → payment)
F7 ←── F19 (withdrawal → clearance)
F7 ←── F22 (scholarship → adjustment)

F8 ──→ F5 (homework → engagement)
F8 ──→ F17 (compliance → pace)
F8 ──→ F4 (weak topics → exam blueprint)
F8 ──→ PEWS (misses → risk)
F8 ──→ F11 (scores → comments)
F8 ──→ F23 (turnaround → teacher eval)
F8 ──→ Tutor (weak → practice)
F8 ──→ F25 (load adjustment → exam prep)

F9 ──→ F1 (timetable → attendance)
F9 ──→ F17 (periods → pace)
F9 ──→ F23 (load → teacher eval)
F9 ──→ F12 (free slots → PTM)

F10 ─→ F5 (payment → engagement)
F10 ─→ F3 (collection → compliance)
F10 ←── F2 (enrollment → first link)
F10 ←── F7 (recovery → payment link)
F10 ←── F19 (withdrawal → settlement)

F11 ←── F4 (results → comments)
F11 ←── F8 (homework → comments)
F11 ←── F5 (engagement → context)
F11 ←── F17 (pace → context)
F11 ──→ F25 (achievement → exam tracker)

F12 ←── F1 (absences → priority)
F12 ←── F5 (engagement → priority)
F12 ←── F9 (free slots → scheduling)
F12 ←── F11 (brief → pre-meeting)
F12 ←── PEWS (flagged → longer slots)

F13 ←── F9 (substitute → payroll)
F13 ←── F7 (collection → funding)
F13 ──→ F3 (quals → compliance)
F13 ──→ F23 (salary → performance context)

F14 ──→ F3 (infrastructure → compliance)
F14 ←── F2 (new students → demand)

F15 ←── F1 (absent → extended deadline)
F15 ←── F2 (new parents → list)
F15 ──→ F5 (response → engagement)
F15 ──→ F21 (queries → routing)

F17 ←── F4 (moderation → gaps)
F17 ←── F8 (compliance → pace)
F17 ←── F9 (periods → coverage)
F17 ──→ Tutor (weak → practice)
F17 ──→ F11 (pace → comments)
F17 ──→ F23 (pace → teacher eval)
F17 ──→ F25 (overlap → exam prep)

F18 ←── F5 (Champions → reviews)
F18 ──→ F2 (rating → lead nurturing)

F19 ──→ F2 (seat opens → waitlist)
F19 ──→ F3 (TC → compliance)
F19 ──→ F6 (route removal)
F19 ←── F7 (fee clearance)
F19 ←── F10 (settlement)
F19 ←── Library (book return)
F19 ←── ID Card (card return)

F20 ──→ PEWS (incidents → risk)
F20 ──→ F24 (security → broadcast)
F20 ──→ F23 (discipline → review)
F20 ──→ F3 (safety → compliance)

F21 ──→ F1 (replies → attendance)
F21 ──→ F5 (response time → engagement)
F21 ←── F15 (circular → queries)

F22 ──→ F7 (disbursement → fee adjustment)
F22 ──→ F3 (RTE → reimbursement)
F22 ←── F2 (lead → eligibility)

F23 ←── F1 (attendance marking)
F23 ←── F4 (question quality)
F23 ←── F8 (grading turnaround)
F23 ←── F9 (load balance)
F23 ←── F17 (pace)
F23 ←── F21 (feedback sentiment)
F23 ←── F13 (payroll context)
F23 ←── F20 (discipline)
F23 ←── F25 (exam results)
F23 ──→ F3 (training → compliance)

F24 ←── F1 (attendance context)
F24 ←── F6 (transport emergency)
F24 ←── F15 (circular)
F24 ←── F20 (CCTV security)

F25 ←── F4 (gap topics)
F25 ←── F8 (load adjustment)
F25 ←── F17 (syllabus overlap)
F25 ──→ F11 (achievement → comments)
F25 ──→ F5 (engagement → signal)
F25 ──→ F23 (results → teacher credit)
```

---

## Part 5: Tech Stack Summary

### Existing (Reuse)

| Tech | Location | Used By |
|------|----------|---------|
| Kotlin + Ktor 3.4.3 | `server/` | All backend |
| Exposed ORM | `server/` | All DB operations |
| PostgreSQL | prod | All data |
| Compose Multiplatform | `composeApp/` + `shared/` | All clients |
| Next.js 14 | `website/` | Admin web |
| AiService (multi-provider LLM) | `feature/ai/AiService.kt` | All AI features |
| FCM (Firebase Cloud Messaging) | `feature/notification/` | Push notifications |
| Notify spine | `feature/notifications/Notify.kt` | All notifications |
| OTP gateway (MSG91/Twilio) | `feature/auth/OtpService.kt` | SMS |
| i18n (ServerStrings + UserLanguageResolver) | `feature/i18n/` | Multi-language |
| Kill switches | `feature/pews/core/KillSwitchConfig.kt` | Feature flags |
| Module Registry pattern | `TutorModuleRegistry` | Plug-and-play modules |
| Daily/Weekly Job pattern | `PewsDailyJob`, `PulseWeeklyJob` | Scheduled tasks |
| Supabase Storage | existing | File storage |

### New (Add)

| Tech | Used By | Priority |
|------|---------|---------|
| WhatsApp Business Cloud API | F1, F2, F7, F10, F12, F15, F16, F21, F24 | P0 |
| Razorpay/Cashfree (UPI) | F10, F7 | P0 |
| Apache PDFBox | F3, F4, F10, F11, F13, F19 | P0 |
| Google Maps Geocoding + Distance Matrix | F6 | P1 |
| Google OR-Tools (CP-SAT) | F6, F9 | P1 |
| Google Calendar API | F2, F9, F12 | P1 |
| Google Vision API (OCR) | F8 | P2 |
| Google Vision API (Video) | F20 | P3 |
| YOLOv8 (self-hosted) | F20 (alternative) | P3 |
| Playwright + Cheerio | F18, F22 | P2 |
| Agent Orchestrator (new) | All 25 features | P0 |

---

## Part 6: Build Priority & Sequence

### Phase 1 — Foundation (P0, weeks 1-3)
1. **Agent Orchestrator** — core engine (event bus, condition engine, action dispatcher, audit)
2. **WhatsApp Business API** — `WhatsAppService.kt` (used by 9 features)
3. **F1 Auto-Attendance** — expand `TeacherAttendanceRouting` + `transport` + `leave` + `pews`
4. **F7 Smart Fee Recovery** — expand `FeeService` + `ParentFeesRouting`
5. **F10 UPI Fee Collection** — new `feature/payment/` + Razorpay
6. **F2 Admission Pipeline** — expand `AdmissionRouting`

### Phase 2 — High Impact (P1, weeks 4-7)
7. **F5 Parent Engagement** — expand `ParentPulseService`
8. **F8 Homework Auto-Checker** — expand `TeacherHomeworkRouting`
9. **F9 Auto-Timetable** — expand `SchoolTimetableRouting` + OR-Tools
10. **F15 Auto-Circular** — expand `AnnouncementRouting`
11. **F3 Compliance Agent** — new `feature/compliance/`
12. **F6 Transport Optimizer** — expand `transport/` + OR-Tools + Maps
13. **F12 PTM Scheduler** — expand `PtmRouting` + Calendar API
14. **F11 Report Card Comments** — expand `NarratorService`
15. **F13 Auto-Payroll** — new `feature/payroll/`

### Phase 3 — Differentiators (P2, weeks 8-11)
16. **F4 Exam Paper Generator** — expand `TeacherQuizRouting` + `SyllabusAiService`
17. **F17 Syllabus Pace Planner** — expand `SyllabusPaceService`
18. **F14 Inventory Agent** — new `feature/inventory/`
19. **F19 Withdrawal/TC** — expand `IdCardRouting` + `SchoolStudentsRouting`
20. **F21 Parent Query Router** — expand `MessagesRouting` + AI
21. **F23 Staff Performance** — new `feature/staffreview/`
22. **F25 Competitive Exam** — expand `tutor/` + `SyllabusAiService`

### Phase 4 — Advanced (P3, weeks 12-14)
23. **F16 Birthday Wisher** — new `feature/birthday/`
24. **F18 Reputation Monitor** — new `feature/reputation/`
25. **F20 CCTV Incident Detector** — new `feature/cctv/`
26. **F22 Scholarship Finder** — expand `ScholarshipService`
27. **F24 Emergency Broadcast** — expand `Notify.kt`

---

## Part 7: New Tables Summary (~35)

| Source | Tables |
|--------|--------|
| Orchestrator | `agent_events`, `agent_audit_log` |
| F1 | `attendance_reconciliation_log` |
| F2 | `admission_leads`, `lead_interactions`, `lead_scores_history` |
| F3 | `compliance_items`, `compliance_certificates` |
| F4 | `exam_papers`, `question_bank`, `exam_moderation` |
| F5 | `parent_engagement_scores`, `engagement_campaigns` |
| F6 | `transport_routes_optimized`, `gps_variance_log` |
| F7 | `fee_recovery_actions` |
| F8 | `homework_compliance` (extends existing submissions) |
| F9 | `timetable_generations`, `substitution_proposals` |
| F10 | `upi_payment_links` |
| F12 | `ptm_schedules`, `ptm_slots`, `ptm_preferences` |
| F13 | `payroll_runs`, `payslips` |
| F14 | `inventory_items`, `inventory_transactions`, `purchase_orders` |
| F15 | `circulars`, `circular_consents` |
| F16 | `birthday_log` |
| F18 | `reputation_reviews`, `reputation_sentiment_log` |
| F19 | `withdrawal_requests`, `clearance_checklist` |
| F20 | `cctv_incidents`, `cctv_alerts` |
| F21 | `parent_queries`, `query_routing_log` |
| F22 | `external_scholarships`, `scholarship_matches` |
| F23 | `staff_performance_reviews` |
| F24 | `emergency_broadcasts` |
| F25 | `competitive_exams`, `exam_prep_plans`, `exam_results` |

---

## Summary

- **17 features EXPAND existing modules** — building on attendance, admissions, quiz, pulse, transport, fee, homework, timetable, reportcard, PTM, announcements, syllabus pace, students, messages, scholarship, notifications, and tutor modules
- **8 features BUILD from scratch** — compliance, UPI payments, payroll, inventory, birthday wisher, reputation monitor, CCTV detector, staff performance reviewer
- **Every feature connects to 5-10 others** through the Agent Orchestrator event bus
- **5 key data flow chains** (Safety, Revenue, Academic, Compliance, Admission-to-Graduation) create self-reinforcing loops
- **~35 new tables** + **9 new external integrations**
- **4-phase build sequence** over ~14 weeks
- **The result:** A true agentic OS where features don't work in isolation — they form an intelligent, self-correcting system that acts autonomously and learns from outcomes.
