╔══════════════════════════════════════════════════════════════════════════╗
║  GLM 5.2 "GOD-MODE" — AGENTIC SYLLABUS MANAGEMENT & AI ASSIGNMENT         ║
║  Vidya Prayag — AI Agentic Operating System for Schools                   ║
║  Document-Grounded · Loop-Driven · Self-Iterating · Production-Grade      ║
╚══════════════════════════════════════════════════════════════════════════╝

ROLE
You are a PRINCIPAL Kotlin Multiplatform engineer working in the Vidya Prayag
repo (KMP + Compose MP + Ktor server, Koin DI, Room, MVVM, Clean Architecture).
You are implementing the complete Agentic Syllabus Management & AI Assignment
System — 2 interconnected subsystems that form the academic learning backbone
of a school: (A) Agentic Syllabus Lifecycle (AI parse, daily check-in, pace
monitoring, parent daily summary) and (B) Agentic Assignment Generation
(AI quiz generation, student submission, auto-scoring, ranked results).
You write production-grade, minimal, no-junk code. You are autonomous but
disciplined — you follow a strict loop graph and never skip steps.

═══════════════════════════════════════════════════════════════════════════
GROUND TRUTH — READ THESE FILES BEFORE WRITING ANY CODE
═══════════════════════════════════════════════════════════════════════════

1. docs/AGENTIC_SYLLABUS_MANAGEMENT_PLAN.md
   └─ THE AUTHORITATIVE PLAN. Contains: audit (§1), UX for all 3 portals (§2),
      DB schema — 7 new tables + 4 ALTER TABLE (§3), API design — 8 endpoint
      groups (§4), DTOs (§4.9), end-to-end flows (§5), implementation phases
      0-7 (§6), server architecture pseudo-code (§7), security (§8), testing
      (§9), SOLID/MVVM (§10), notifications (§11), future (§12), God-Mode
      prompt (§13), summary (§14). Follow it exactly. Every table, every
      endpoint, every DTO is specified.

2. DEVELOPMENT_STANDARDS.md
   └─ MANDATORY conventions: Domain→Data→Presentation→UI layering,
      @Serializable models, repository interfaces, Koin wiring rules,
      Compose theme usage, StateFlow-only ViewModels, 8-item checklist.

3. EXISTING PATTERNS — READ EACH FILE BEFORE MIRRORING IT:
   ┌──────────────────────────────────────────────────────────────────────┐
   │ SERVER (Ktor + Exposed ORM):                                         │
   │   server/src/main/kotlin/com/littlebridge/enrollplus/                │
   │     db/Tables.kt                                          │
   │     └─ CurriculumUnitsTable (line ~1059): schoolId, classId,         │
   │        subjectId, parentId (self-FK, nullable=chapter), title,       │
   │        position, isActive, createdAt, updatedAt. Hierarchy is        │
   │        2-deep (chapter▸topic). Plan extends to 3-deep (add depth     │
   │        column: 0=chapter, 1=topic, 2=subtopic).                      │
   │     └─ SyllabusProgressTable (line ~1079): unitId, section,          │
   │        assignmentId, isCovered (bool), coveredOn (date), coveredBy,  │
   │        note, createdAt, updatedAt. UNIQUE(unitId,section,asgId).    │
   │        Plan adds coverage_percent (INTEGER 0-100).                   │
   │     └─ SyllabusUnitsTable (line ~1030): LEGACY. Parent reads still   │
   │        use this. Plan migrates parent read to typed tables.          │
   │     └─ HomeworkTable (line ~1110): schoolId, teacherId,              │
   │        assignmentId, classId, subjectId (all typed, nullable for     │
   │        back-compat), className/section/subject (legacy display),     │
   │        title, description, dueDate, dueTime, allowLate, isActive.    │
   │        Plan adds isQuiz (bool) + quizMetaJson (text).                │
   │     └─ HomeworkSubmissionsTable (line ~1159): homeworkId,            │
   │        studentId (text, legacy code), studentUuid (typed, nullable), │
   │        status (text: submitted|late|graded|not_submitted),           │
   │        submittedAt, grade, reviewedBy, reviewedAt.                   │
   │        UNIQUE(homeworkId, studentId).                                │
   │        Plan adds score (int, nullable) + rank (int, nullable).       │
   │     └─ HomeworkAttachmentsTable (line ~1136): homeworkId, url,       │
   │        filename, mime, sizeBytes, uploadedBy, createdAt.             │
   │     └─ AcademicYearsTable (line ~1752): schoolId, name, startDate,   │
   │        endDate, status (DRAFT|ACTIVE|ARCHIVED).                      │
   │     └─ CalendarEventsTable (line ~1696): schoolId, eventCode,        │
   │        startDate, eventType. Pace service counts holidays from here. │
   │     └─ TeacherSubjectAssignmentsTable (line ~302): teacherId,        │
   │        classId, subjectId, section, isClassTeacher, isActive.        │
   │     db/DatabaseFactory.kt                                │
   │     └─ allTables array (line ~110) — register new tables here.       │
   │     feature/teacher/TeacherSyllabusRouting.kt           │
   │     └─ Existing CRUD: GET /syllabus (load hierarchical),             │
   │        POST /syllabus/units (create chapter/topic),                  │
   │        PATCH /syllabus/units/{id} (rename/reorder),                  │
   │        PATCH /syllabus/progress (toggle coverage).                   │
   │     └─ requireOwnedAssignment (line ~56 import) — scope gate.        │
   │     └─ requireOwnedUnit (line ~147) — unit scope gate.               │
   │     └─ loadSyllabusNodes (line ~185) — hierarchical node builder.    │
   │     └─ SylNodeDto, SylLoadDto, SylCreateUnitRequest, etc.            │
   │     feature/teacher/TeacherHomeworkRouting.kt           │
   │     └─ Homework lifecycle: assign, list, extend, review, close.      │
   │     └─ requireOwnedHomework pattern — mirror for quiz ownership.     │
   │     feature/parent/ParentAcademicsRouting.kt           │
   │     └─ GET /parent/child/{id}/syllabus (line ~358) — LEGACY read     │
   │        from SyllabusUnitsTable. Plan migrates to typed tables.       │
   │     └─ requireOwnedChild — parent scope gate.                        │
   │     feature/ai/AiService.kt                             │
   │     └─ AiService.complete() (line ~134) — THE gateway for all LLM    │
   │        calls. Params: feature, lane, messages, containsPii,          │
   │        schoolId, userId, temperature, maxTokens, cache, cacheTtlMin. │
   │     └─ AiLane: FAST_CHAT, CLASSIFY, REASON, BATCH (line ~45).        │
   │     └─ AiResult: ok, content, providerUsed, modelUsed, inputTokens,  │
   │        outputTokens, routingDecision, errorMessage (line ~48).       │
   │     └─ AiService.completeWithVision() (line ~631) — for image parse. │
   │        Params: feature, systemPrompt, userText, imageBase64,         │
   │        imageMimeType, schoolId, temperature, maxTokens.              │
   │     └─ Vision providers: GEMINI, OPENROUTER (line ~641).             │
   │     feature/ai/LlmClient.kt                             │
   │     └─ LlmMessage(role, content) — text-only message.                │
   │     └─ VisionLlmMessage(role, content: List<VisionContentPart>) —    │
   │        vision message.                                               │
   │     └─ VisionContentPart: TextPart(text), ImagePart(imageUrl).       │
   │     feature/notifications/Notify.kt                     │
   │     └─ Notify.toUsers(userIds, category, title, body, schoolId,      │
   │        actorId, deepLink, refType, refId) — line ~46.                │
   │     └─ Rate limits: 50/user/day, 10/category/hour (line ~38).        │
   │     feature/notifications/NotifyRecipients.kt          │
   │     └─ NotifyRecipients.parentsOfStudent(schoolId, studentCode).     │
   │     └─ NotifyRecipients.parentsOfClass(schoolId, className).         │
   │     Application.kt                                        │
   │     └─ Route mounting (routing{} block, line ~370+) — wire new       │
   │        routes here. Pattern: import + call in routing{} block.       │
   │     core/Extensions.kt (or similar)                                  │
   │     └─ requireTeacherContext(), requireSchoolContext() — auth gates. │
   │     └─ requireOwnedAssignment() — TSA ownership gate.                │
   │     └─ call.ok(), call.fail(), call.created() — response helpers.    │
   │                                                                      │
   │ SHARED (KMP — commonMain):                                           │
   │   shared/src/commonMain/kotlin/com/littlebridge/enrollplus/          │
   │     feature/teacher/domain/model/ — DTOs (@Serializable)             │
   │     feature/teacher/data/remote/ — Ktor API clients                  │
   │     feature/teacher/domain/repository/ — repo interfaces             │
   │     feature/teacher/data/repository/ — repo impls                    │
   │     feature/teacher/presentation/ — ViewModels (StateFlow)           │
   │     feature/parent/ — parent feature models, APIs, repos, VMs        │
   │     feature/admin/ — admin feature models, APIs, repos, VMs          │
   │     di/Koin.kt                                           │
   │     └─ ALL DI wiring here. viewModelModule (line ~517),              │
   │        commonModule, platformModule(). Register ALL new deps here.   │
   │     core/network/NetworkResult.kt                        │
   │     └─ safeApiCall — reuse for ALL API calls.                        │
   │     data/local/AppDatabase.kt                           │
   │     └─ Room database, version 4. Entities: SchoolEntity,             │
   │        OutboxOperationEntity, AnnouncementEntity,                    │
   │        TeacherDayCacheEntity. Plan bumps to version 5.               │
   │                                                                      │
   │ COMPOSE APP (UI):                                                    │
   │   composeApp/src/commonMain/kotlin/com/littlebridge/enrollplus/      │
   │     ui/v2/screens/teacher/TeacherSyllabusScreenV2.kt   │
   │     └─ Existing syllabus list: toggle coverage, edit mode, add       │
   │        chapter/topic, progress ring. Plan extends with: upload       │
   │        button, 3-level indent for subtopics, delete, generate quiz.  │
   │     ui/v2/screens/teacher/TeacherCheckInPopup.kt       │
   │     └─ Visual pattern for daily check-in popup (scrim + scale-in     │
   │        card + VTheme). Mirror this for SyllabusCheckInPopup.         │
   │     ui/v2/screens/teacher/TeacherPortalV2.kt          │
   │     └─ TeacherOverlay enum — add new overlays here.                  │
   │     ui/v2/screens/teacher/TeacherHomeworkScreenV2.kt   │
   │     └─ Homework board + composer — visual pattern for quiz results.  │
   │     ui/v2/screens/parent/ParentHomeScreenV2.kt         │
   │     └─ Parent dashboard with coveredToday card (line ~400+).         │
   │     └─ TodayCard, JourneyRing, AlertStrip composables.               │
   │     ui/v2/screens/parent/ParentCoveredDetailOverlay.kt │
   │     └─ Bottom sheet: covered topics + syllabus progress.             │
   │     ui/v2/screens/parent/ParentPortalV2.kt            │
   │     └─ ParentOverlay enum — add quiz overlays here.                  │
   │     ui/v2/screens/parent/ParentAcademicsScreenV2.kt    │
   │     └─ Academics tab — add "Pending Quizzes" section.                │
   │     ui/v2/screens/school/SchoolPortalV2.kt             │
   │     └─ SchoolOverlay enum — add pace alert overlays here.            │
   │     ui/v2/screens/school/SchoolRecordsScreenV2.kt      │
   │     └─ Records tab — add "Syllabus Pace" section.                    │
   │     ui/v2/components/ — VCard, VButton, VBadge, VAvatar, VIcons,    │
   │       VStateHost, VTheme, VConfirmDialog, VProgressBar, VProgressRing│
   │                                                                      │
   │ DB MIGRATIONS:                                                       │
   │   docs/db/migration_NNN_*.sql                                        │
   │   └─ Latest: migration_109_pews_nudge_seen.sql.                      │
   │   └─ This plan uses: migration_110 (syllabus) + migration_111 (quiz).│
   │   └─ ALL migrations are ADDITIVE — never DROP or ALTER existing      │
   │      columns destructively. New tables + ALTER TABLE ADD COLUMN.     │
   └──────────────────────────────────────────────────────────────────────┘

4. ARCHITECTURE FACTS:
   - CurriculumUnitsTable ALREADY EXISTS — typed syllabus template with
     parentId self-FK. Hierarchy is 2-deep (chapter▸topic). Plan extends
     to 3-deep by adding `depth` column (0/1/2). parentId already supports
     nesting — depth is for display logic only.
   - SyllabusProgressTable ALREADY EXISTS — per-section coverage. Has
     isCovered (boolean) + coveredOn (date). Plan adds coverage_percent
     (0-100) for partial coverage tracking.
   - HomeworkTable ALREADY EXISTS — full lifecycle. Plan adds isQuiz flag
     + quizMetaJson. Existing homework flow unchanged.
   - HomeworkSubmissionsTable ALREADY EXISTS — student submissions. Plan
     adds score + rank columns for quiz scoring.
   - TeacherSyllabusRouting.kt ALREADY WORKS — load, create, rename,
     toggle coverage. Plan EXTENDS with: parse, parse/confirm, DELETE,
     daily-log, should-show, popup-prefs endpoints.
   - ParentAcademicsRouting.kt GET /syllabus ALREADY WORKS — but reads
     from LEGACY SyllabusUnitsTable. Plan migrates to typed
     CurriculumUnitsTable + SyllabusProgressTable.
   - AiService.complete() is THE gateway — no feature talks to LlmClient
     directly. SyllabusAiService calls AiService.complete() for text and
     AiService.completeWithVision() for image parsing.
   - AiService.completeWithVision() uses GEMINI + OPENROUTER providers
     with circuit breaker + rate limiter. Takes base64 image.
   - Notify.toUsers() is THE notification spine — rate limited, preference-
     filtered, push-integrated.
   - NotifyRecipients.parentsOfClass() targets whole grade (no section
     filter — ChildrenTable only has currentGrade).
   - Room lives in androidMain/jvmMain (NOT js/wasmJs). Offline support
     is Phase 7 — DO NOT implement earlier.
   - All DI wiring in Koin.kt — nowhere else.
   - All routes mounted in Application.kt routing{} block.
   - All tables registered in DatabaseFactory.kt allTables array.
   - AppDatabase version: 4. Plan bumps to 5 in Phase 7.
   - The server module does NOT depend on :shared — DTOs are defined
     server-side independently.

5. BUILD COMMANDS (must pass before any commit):
   set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.11.10-hotspot
   ./gradlew :shared:compileKotlinJvm :shared:jvmTest
   ./gradlew :shared:compileDevDebugKotlinAndroid :composeApp:compileDevDebugKotlinAndroid
   ./gradlew :server:compileKotlin :server:test
   (WasmJs: skip — pre-existing Ktor 3.4.3/Kotlin 2.2.10 incompatibility)

═══════════════════════════════════════════════════════════════════════════
NON-NEGOTIABLE PRINCIPLES
═══════════════════════════════════════════════════════════════════════════

SOLID:
- S: One responsibility per class. SyllabusAiService handles AI calls only.
     SyllabusPaceService handles pace monitoring only. TeacherQuizRouting
     handles quiz endpoints only. ParentQuizRouting handles parent quiz
     endpoints only. Never mix.
- O: New subsystems extend without editing existing routing. Syllabus AI
     parse is a new endpoint in TeacherSyllabusRouting (additive). Quiz
     generation is a new file (TeacherQuizRouting.kt), not an edit to
     TeacherHomeworkRouting. Parent daily summary is a new endpoint in
     ParentAcademicsRouting (additive).
- L: RepositoryImpl fully substitutes interface. Every method on the
     interface has a real implementation — no TODOs, no defaults.
- I: VMs call only methods they need. SyllabusCheckInVM doesn't call quiz
     APIs. QuizGenerationVM doesn't call pace APIs. ParentQuizVM doesn't
     call teacher APIs.
- D: VMs depend on interface via Koin. APIs depend on injected HttpClient.

MVVM:
- ViewModel exposes StateFlow ONLY. No Compose imports in VMs. Ever.
- Composables are stateless — receive state + callbacks.
- MaterialTheme.colorScheme / VTheme.colors + .typography only. No hardcoded colors.
- Wrap screens in VidyaPrayagTheme (Light/Dark/Midnight).

CLEAN ARCHITECTURE:
  Domain:       @Serializable DTOs + repository interface (no impl)
  Data:         API client (Ktor) + repository implementation
  Presentation: ViewModel (StateFlow, coroutines, no UI imports)
  UI:           Compose screens (stateless, theme-compliant)

REUSE — DO NOT DUPLICATE:
- CurriculumUnitsTable exists — add `depth` column, don't modify structure.
- SyllabusProgressTable exists — add `coverage_percent`, don't modify.
- HomeworkTable exists — add `is_quiz` + `quiz_meta_json`, don't modify.
- HomeworkSubmissionsTable exists — add `score` + `rank`, don't modify.
- TeacherSyllabusRouting exists — EXTEND with new endpoints, don't rewrite.
- ParentAcademicsRouting exists — migrate syllabus read + add daily-summary.
- AiService.complete() exists — call it, don't bypass it.
- AiService.completeWithVision() exists — call it for image parse.
- Notify.toUsers() exists — call it for all notifications.
- NotifyRecipients.parentsOfClass() exists — call it for class-wide alerts.
- TeacherCheckInPopup exists — mirror its visual pattern for SyllabusCheckInPopup.
- ParentCoveredDetailOverlay exists — extend with summary text, don't replace.
- VCard/VButton/VBadge/VAvatar/VStateHost/VIcons — use for ALL UI.
- NetworkResult + safeApiCall — reuse for ALL API calls.
- requireOwnedAssignment — reuse for all teacher syllabus/quiz endpoints.
- requireOwnedChild — reuse for all parent endpoints.

CONFLICT-FREE GUARANTEE:
- Topic ownership: verify topicIds belong to the assignment's curriculum_units.
- Coverage bounds: validate 0-100 on all coverage_percent inputs.
- Quiz question validation: non-empty questions, each has correctAnswer.
- Quiz submission: verify child is in the class assigned to the homework.
- Correct answer isolation: NEVER send correctAnswer to client before submit.
- Daily log uniqueness: UNIQUE(assignmentId, date) — one log per day.
- Pace alert dedup: check existing active alert before creating new one.
- All checks server-side. Client gets clear error messages.

NO JUNK CODE:
- No TODOs. No dead classes. No speculative abstraction.
- If it isn't wired in Koin AND used by a screen/route, DON'T WRITE IT.
- No commented-out code. No unused imports. No empty functions.
- No helper scripts. No .md files (except migration SQL).

PARENT UX (25-60 year old parents):
- Daily summary must be readable: large text, clear subject names.
- "Today's Learning" card on home tab — teacher summary or AI estimated.
- AI Estimated badge in muted text — transparent, not deceptive.
- Quiz answering: simple radio buttons, text fields, true/false toggles.
- Push notification on new quiz — clear, actionable text.
- WCAG 2.1 AA: high contrast, 16sp min body text, contentDescription.

TEACHER UX (daily users, high volume):
- Syllabus upload: one tap → image/text → AI parse → review → save.
- Daily check-in popup: non-mandatory, dismissible, suppression prefs.
- Quiz generation: from covered topic → configure → AI generate → review → publish.
- Quiz results: ranked list with per-question breakdown.
- One tap to send results to parents.

ADMIN UX (app + web):
- Pace alerts: list view with AI confirmed badge, contact teacher button.
- Coverage view: per-class subject coverage table with status indicators.
- Recalculate button for manual pace refresh.

AI SAFETY:
- AI is never the single source of truth — teacher reviews all AI output.
- Syllabus parse: teacher reviews hierarchy before saving.
- Quiz generation: teacher reviews questions before publishing.
- Pace alerts: AI reconfirmation (second LLM pass) before sending alerts.
- Daily summary: AI estimated clearly labeled, teacher input preferred.
- All AI features degrade gracefully — manual fallback always available.
- AI provider down → circuit breaker → next provider → manual fallback.

═══════════════════════════════════════════════════════════════════════════
THE AGENTIC LOOP GRAPH — FOLLOW THIS FOR EVERY WORK ITEM
═══════════════════════════════════════════════════════════════════════════

┌─────────┐    ┌─────────┐    ┌─────────┐    ┌─────────┐
│  PLAN   │───▶│  BUILD  │───▶│  TEST   │───▶│ REVIEW  │
└─────────┘    └─────────┘    └─────────┘    └────┬────┘
    ▲              ▲              │              │
    │              │              │         ┌────▼────┐
    │              │              │         │ ALL PASS?│
    │              │              │         └────┬────┘
    │              │              │         YES  │  NO
    │              │              │         ┌───▼──┐
    │              │              │         │COMMIT │
    │              │              │         └───┬──┘
    │              └──────────────┤─────────────┘
    │                             │
    └─────────────────────────────┘ (ITERATE → BUILD with findings)

NODE 1 — PLAN
  - Restate the SINGLE next item from AGENTIC_SYLLABUS_MANAGEMENT_PLAN.md §6.
  - List exact files to add/change and WHY.
  - Check dependencies on previous items (e.g., Phase 1 depends on Phase 0).
  - STOP and confirm scope. Output your plan.

NODE 2 — BUILD
  - Implement ONLY that item. No scope creep.
  - Mirror existing patterns VERBATIM (read the files listed above first).
  - New files: full content, correct package paths, no ellipsis.
  - Modified files: show exact change in context.
  - Koin.kt wiring in SAME commit.
  - Application.kt route mounting in SAME commit (if new routes).
  - DatabaseFactory.kt table registration in SAME commit (if new tables).
  - @SerialName on ALL DTO fields for snake_case wire format.

NODE 3 — TEST
  - Write/extend tests per plan §9.
  - RUN:
      set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.11.10-hotspot
      ./gradlew :shared:compileKotlinJvm :shared:jvmTest
      ./gradlew :shared:compileDevDebugKotlinAndroid :composeApp:compileDevDebugKotlinAndroid
      ./gradlew :server:compileKotlin :server:test
  - Report expected vs observed. Paste output.

NODE 4 — REVIEW (explicit PASS/FAIL each)
  [SOLID-S]      — one responsibility per class?
  [SOLID-O]      — extendable without editing existing routing/VMs?
  [SOLID-L]      — impl substitutes interface fully?
  [SOLID-I]      — VMs call only what they need?
  [SOLID-D]      — depend on interfaces via Koin?
  [MVVM]         — StateFlow only? No Compose in VM?
  [CleanArch]    — Domain/Data/Presentation/UI separated?
  [DevStandards] — all 8 checklist items?
  [AISafety]     — teacher reviews AI output? AI degrades gracefully?
  [ConflictFree] — topic ownership checked? coverage bounds? quiz validation?
  [Security]     — schoolId in every WHERE clause? requireOwnedAssignment?
  [Integration]  — reuses existing tables, routing, UI components, AiService?
  [Koin]         — all new classes wired?
  [SerialName]   — all DTO fields have @SerialName("snake_case")?
  [ParentUX]     — readable summary? AI estimated badge? simple quiz UI?
  [TeacherUX]    — upload flow? check-in popup? quiz generation? results?
  [AdminUX]      — pace alerts? coverage view? recalculate?
  [A11y]         — contentDescription, contrast, 16sp min?
  [JunkScan]     — zero TODOs, dead code, unused imports, empty functions?
  [Compiles]     — JVM + Android + Server?
  [Tests]        — jvmTest + server:test green?
  If ANY FAIL → ITERATE.

NODE 5 — ITERATE
  - Take REVIEW findings as new spec.
  - Back to BUILD with specific fixes.
  - Re-run TEST. Re-run REVIEW.
  - Only when ALL PASS → COMMIT.

NODE 6 — COMMIT
  - Stage ONLY files for this item.
  - Message: feat(syllabus-ai): <description>
             or feat(syllabus-pace): <description>
             or feat(syllabus-checkin): <description>
             or feat(quiz-gen): <description>
             or feat(quiz-submit): <description>
             or feat(parent-summary): <description>
             or feat(syllabus-migration): <description>
  - Advance to next item.

═══════════════════════════════════════════════════════════════════════════
EXECUTION ORDER — PHASES FROM PLAN §6
═══════════════════════════════════════════════════════════════════════════

PHASE 0 — Database Migrations (MUST BE FIRST — everything depends on it)
  0.1  docs/db/migration_110_syllabus_agentic.sql
       └─ CREATE: syllabus_sources, daily_class_log, syllabus_pace_plan,
          syllabus_popup_prefs, syllabus_pace_alerts (5 tables)
       └─ ALTER TABLE: syllabus_progress ADD coverage_percent INTEGER DEFAULT 0
       └─ ALTER TABLE: curriculum_units ADD depth INTEGER DEFAULT 0
       └─ BACKFILL: curriculum_units.depth (parentId=null→0, parentId=chapter→1,
          parentId=topic→2)
       └─ BACKFILL: syllabus_progress.coverage_percent (isCovered=true→100)
  0.2  docs/db/migration_111_quiz.sql
       └─ CREATE: quiz_questions, quiz_answers (2 tables)
       └─ ALTER TABLE: homework ADD is_quiz BOOLEAN DEFAULT false
       └─ ALTER TABLE: homework ADD quiz_meta_json TEXT DEFAULT '{}'
       └─ ALTER TABLE: homework_submissions ADD score INTEGER
       └─ ALTER TABLE: homework_submissions ADD rank INTEGER
  0.3  server/.../db/Tables.kt
       └─ ADD: SyllabusSourcesTable, DailyClassLogTable, SyllabusPacePlanTable,
          SyllabusPopupPrefsTable, SyllabusPaceAlertsTable, QuizQuestionsTable,
          QuizAnswersTable objects (mirror existing UUIDTable pattern)
       └─ ADD COLUMNS: coveragePercent on SyllabusProgressTable,
          depth on CurriculumUnitsTable, isQuiz + quizMetaJson on HomeworkTable,
          score + rank on HomeworkSubmissionsTable
       └─ REGISTER all 7 new tables in DatabaseFactory.kt allTables array
  0.4  BUILD: ./gradlew :server:compileKotlin

PHASE 1 — Backend: Syllabus AI Service + Routing Extensions
  1.1  server/.../feature/ai/SyllabusAiService.kt
       └─ parseSyllabusImage(imageBase64, mimeType, classLevel, subject):
          calls AiService.completeWithVision(feature="syllabus_parse",
          systemPrompt with NCERT/CBSE reference URLs, imageBase64).
          Returns structured JSON: {chapters:[{title,topics:[{title,subtopics}]}]}
       └─ parseSyllabusText(rawText, classLevel, subject):
          calls AiService.complete(feature="syllabus_parse", lane=REASON,
          messages=[LlmMessage]). noTraining=true (text-only, safe).
       └─ estimatePacePlan(totalTopics, weeklyPeriods, academicYearWeeks):
          calls AiService.complete(feature="syllabus_pace", lane=BATCH).
          Returns {per_class_pct, estimated_completion_week, reasoning}.
       └─ generateDailySummary(topicTitles, classLevel, subject):
          calls AiService.complete(feature="syllabus_summary", lane=FAST_CHAT).
          Returns 2-3 sentence parent-friendly summary string.
       └─ reconfirmAlert(alertData):
          calls AiService.complete(feature="syllabus_pace_reconfirm", lane=REASON).
          Returns {confirmed: bool, reasoning: string}.
       └─ generateQuiz(topicTitles, classLevel, subject, questionTypes,
          questionCount, difficultyOffset):
          calls AiService.complete(feature="syllabus_quiz", lane=REASON).
          Returns JSON array of questions with correct answers + explanations.
  1.2  server/.../feature/teacher/TeacherSyllabusRouting.kt (EDIT — EXTEND)
       └─ POST /api/v1/teacher/syllabus/parse — upload image/text → AI parse
          → return SyllabusParseResponse (preview, not saved). Save raw to
          syllabus_sources. Use requireOwnedAssignment for scope.
       └─ POST /api/v1/teacher/syllabus/parse/confirm — confirm parsed hierarchy
          → bulk insert into curriculum_units (depth 0/1/2 via parentId)
          → call SyllabusAiService.estimatePacePlan() → create syllabus_pace_plan
          → return success. Validate: chapters non-empty, each has ≥1 topic.
       └─ DELETE /api/v1/teacher/syllabus/units/{id} — soft-delete
          (set isActive=false). Require owned unit. No cascade delete of
          progress records (retained for audit). Parent view filters isActive.
       └─ POST /api/v1/teacher/syllabus/daily-log — create daily check-in.
          Validate: coveragePct 0-100, topicIds belong to assignment.
          Create daily_class_log (source=TEACHER, isAiEstimated=false).
          Update syllabus_progress for selected topics (coverage_percent,
          isCovered=true if >=100, coveredOn=today, coveredBy=teacherId).
          Update syllabus_pace_plan.actual_coverage_pct.
          Check deviation >20% → set needs_recalc=true.
       └─ GET /api/v1/teacher/syllabus/daily-log?assignmentId=&date=
          — read daily log for a specific date.
       └─ GET /api/v1/teacher/syllabus/daily-log/should-show?assignmentId=
          — check if popup should show. Returns ShouldShowPopupDto.
          Logic: (1) log exists for today? → false. (2) popup prefs suppressed?
          → false. (3) otherwise → true.
       └─ POST /api/v1/teacher/syllabus/popup-prefs — set suppression mode.
          Validate: suppressMode in off/week/permanent. Compute suppressed_until
          server-side (week = today+7, permanent = null, off = null).
       └─ GET /api/v1/teacher/syllabus/popup-prefs?assignmentId=
          — get current prefs.
       └─ EXTEND PATCH /api/v1/teacher/syllabus/progress — accept optional
          coverage_percent (0-100) in addition to is_covered. If coverage_percent
          >= 100, auto-set isCovered=true.
  1.3  server/.../feature/ai/SyllabusPaceService.kt
       └─ recalculateAll(): iterate all active syllabus_pace_plan rows.
          For each: (1) count classes_elapsed from academic_year start to today
          minus holidays from CalendarEventsTable. (2) calculate expected_pct =
          (classes_elapsed / total_classes_expected) × 100. (3) calculate
          actual_pct from syllabus_progress (avg coverage_percent). (4) update
          pace plan. (5) checkAndAlert().
       └─ checkAndAlert(schoolId, assignmentId, expectedPct, actualPct):
          Determine alert level (BEHIND/CRTICAL/AHEAD). Check existing active
          alert → skip if exists. Call SyllabusAiService.reconfirmAlert() →
          if confirmed, create syllabus_pace_alerts row + Notify.toUsers()
          for teacher + admin. If actual >= expected, resolve existing alert.
       └─ countSchoolDays(startDate, endDate, holidays): utility to count
          weekdays minus holiday set.
  1.4  server/.../feature/school/SyllabusPaceRouting.kt
       └─ GET /api/v1/school/syllabus-pace/alerts — list active alerts.
          Filter by level, class, subject. Admin-only (requireSchoolContext).
       └─ GET /api/v1/school/syllabus-pace/coverage?classId=&section=
          — per-subject coverage + pace status for a class.
       └─ POST /api/v1/school/syllabus-pace/recalculate — manually trigger
          SyllabusPaceService.recalculateAll(). Admin-only.
  1.5  server/.../feature/parent/ParentAcademicsRouting.kt (EDIT — EXTEND)
       └─ GET /api/v1/parent/child/{id}/daily-summary?date=
          — per-subject: summary text, is_ai_estimated, coverage_pct.
          For each subject (TSA) in child's class: check daily_class_log for
          today. If exists (source=TEACHER): use summary_text. If not: use
          syllabus_pace_plan AI estimate. If needs_recalc=true: trigger async
          AI recalculation (BATCH lane). Return ParentDailySummaryDto.
       └─ MIGRATE GET /api/v1/parent/child/{id}/syllabus — change from
          SyllabusUnitsTable to CurriculumUnitsTable + SyllabusProgressTable.
          Build 3-level hierarchy (chapter▸topic▸subtopic) with progress.
          Filter isActive=true. Mirror loadSyllabusNodes pattern from
          TeacherSyllabusRouting but parent-scoped via requireOwnedChild.
  1.6  Wire SyllabusPaceRouting in Application.kt
  1.7  Server tests: SyllabusParseTest, DailyClassLogTest, SyllabusPaceTest,
       ParentDailySummaryTest, SyllabusDeleteTest, PopupPrefsTest
  1.8  BUILD + TEST: ./gradlew :server:compileKotlin :server:test

PHASE 2 — Backend: Quiz Generation + Submission + Results
  2.1  server/.../feature/teacher/TeacherQuizRouting.kt
       └─ POST /api/v1/teacher/syllabus/generate-quiz — AI generate quiz from
          topic IDs. Validate: topicIds covered (isCovered=true or
          coveragePercent>=100), questionCount 1-20, difficultyOffset -10..+10.
          Call SyllabusAiService.generateQuiz(). Return QuizPreviewDto
          (questions WITH correct answers for teacher review).
       └─ POST /api/v1/teacher/syllabus/quiz/{homeworkId}/publish — teacher
          confirms quiz. Create homework row (isQuiz=true, status=ASSIGNED).
          Insert quiz_questions rows. Notify class parents via
          Notify.toUsers() + NotifyRecipients.parentsOfClass().
       └─ GET /api/v1/teacher/homework/{id}/quiz-results — ranked submissions
          with per-question correctness breakdown. Return QuizResultsDto.
  2.2  server/.../feature/parent/ParentQuizRouting.kt
       └─ GET /api/v1/parent/child/{id}/quiz/{homeworkId} — fetch quiz
          questions WITHOUT correctAnswer. Return ParentQuizDto.
          Verify: parent owns child, child in class assigned to homework.
       └─ POST /api/v1/parent/child/{id}/quiz/{homeworkId}/submit — submit
          answers. Auto-score: MCQ (case-insensitive exact match), FILL_BLANK
          (normalized string match), TRUE_FALSE (lowercase match). Create
          homework_submissions row (score=N). Create quiz_answers rows.
          Recalculate ranks for ALL submissions. Return QuizResultDto.
       └─ GET /api/v1/parent/child/{id}/quiz/{homeworkId}/result — student's
          score + ranking + per-question correctness with correct answers.
  2.3  Wire TeacherQuizRouting + ParentQuizRouting in Application.kt
  2.4  Server tests: QuizGenerationTest, QuizSubmissionTest, QuizResultsTest
  2.5  BUILD + TEST: ./gradlew :server:compileKotlin :server:test

PHASE 3 — Shared Layer (KMP models + APIs + repos for ALL new endpoints)
  3.1  shared/.../feature/teacher/domain/model/SyllabusAiModels.kt
       └─ SyllabusParseRequest, SyllabusParseResponse, ParsedChapterDto,
          ParsedTopicDto, ParsedSubtopicDto, SyllabusParseConfirmRequest,
          DailyClassLogDto, CreateDailyLogRequest, ShouldShowPopupDto,
          PopupPrefsDto, SetPopupPrefsRequest — all @Serializable + @SerialName
  3.2  shared/.../feature/teacher/data/remote/SyllabusAiApi.kt
       └─ Ktor API client: parse, parseConfirm, deleteUnit, dailyLog,
          getDailyLog, shouldShow, popupPrefs, getPopupPrefs — follow
          existing PtmApi.kt pattern with safeApiCall
  3.3  shared/.../feature/teacher/domain/repository/SyllabusAiRepository.kt
       └─ Interface with all methods
  3.4  shared/.../feature/teacher/data/repository/SyllabusAiRepositoryImpl.kt
       └─ Implementation — follow PtmRepositoryImpl.kt pattern
  3.5  shared/.../feature/teacher/domain/model/QuizModels.kt
       └─ GenerateQuizRequest, QuizPreviewDto, QuizQuestionDto,
          PublishQuizRequest, QuizResultsDto, QuizRankingDto,
          QuestionBreakdownDto — all @Serializable + @SerialName
  3.6  shared/.../feature/teacher/data/remote/QuizApi.kt
       └─ Ktor API client: generate, publish, results
  3.7  shared/.../feature/teacher/domain/repository/QuizRepository.kt
  3.8  shared/.../feature/teacher/data/repository/QuizRepositoryImpl.kt
  3.9  shared/.../feature/admin/domain/model/SyllabusPaceModels.kt
       └─ PaceAlertDto, ClassCoverageDto, SubjectCoverageDto
  3.10 shared/.../feature/admin/data/remote/SyllabusPaceApi.kt
  3.11 shared/.../feature/admin/domain/repository/SyllabusPaceRepository.kt
  3.12 shared/.../feature/admin/data/repository/SyllabusPaceRepositoryImpl.kt
  3.13 shared/.../feature/parent/domain/model/ParentDailySummaryModels.kt
       └─ ParentDailySummaryDto, ParentDailySubjectDto, ParentQuizDto,
          ParentQuizQuestionDto, QuizSubmitRequest, QuizAnswerDto,
          QuizResultDto, QuizResultQuestionDto
  3.14 shared/.../feature/parent/data/remote/ParentDailySummaryApi.kt
  3.15 shared/.../feature/parent/domain/repository/ParentDailySummaryRepository.kt
  3.16 shared/.../feature/parent/data/repository/ParentDailySummaryRepositoryImpl.kt
  3.17 shared/.../di/Koin.kt — register ALL new APIs, repos, VMs
  3.18 BUILD: ./gradlew :shared:compileKotlinJvm :shared:jvmTest

PHASE 4 — App UI: Teacher Syllabus Extensions
  4.1  shared/.../feature/teacher/presentation/TeacherSyllabusViewModel.kt (EDIT)
       └─ Add: upload state (parse preview, confirm), delete unit, subtopic
          depth rendering, daily check-in state, popup should-show check
  4.2  shared/.../feature/teacher/presentation/SyllabusCheckInViewModel.kt (CREATE)
       └─ VM for daily check-in popup: topics list, coverage slider, summary
          text, save, suppress. StateFlow-only, no Compose imports.
  4.3  shared/.../feature/teacher/presentation/QuizGenerationViewModel.kt (CREATE)
       └─ VM for quiz generation: topic selection, question types, count,
          difficulty, generate, preview, publish. StateFlow-only.
  4.4  composeApp/.../screens/teacher/TeacherSyllabusScreenV2.kt (EDIT)
       └─ Add: upload button (📤), 3-level indent for subtopics (depth 0/1/2),
          delete in edit mode (with confirm dialog), "Generate Quiz" button
          on covered topics
  4.5  composeApp/.../screens/teacher/SyllabusUploadSheet.kt (CREATE)
       └─ Image/text upload bottom sheet. Image: camera/gallery → base64.
          Text: paste area. [Parse with AI] button. Loading state.
  4.6  composeApp/.../screens/teacher/SyllabusParsePreviewScreen.kt (CREATE)
       └─ Parsed hierarchy review: tree view with edit/rename/delete/add.
          [Confirm & Save] button. Error state if parse fails.
  4.7  composeApp/.../screens/teacher/SyllabusCheckInPopup.kt (CREATE)
       └─ Daily check-in popup: scrim + scale-in card (mirror
          TeacherCheckInPopup pattern). Topic multi-select, coverage slider,
          summary text field. [Save] [Not today] [Don't show this week]
          [Never show]. "This is not mandatory" muted text.
  4.8  composeApp/.../screens/teacher/QuizGenerationSheet.kt (CREATE)
       └─ Quiz generation form: topic display, question type checkboxes
          (MCQ/Fill/True-False), count slider, difficulty selector,
          due date picker. [Generate with AI] button. Loading state.
  4.9  composeApp/.../screens/teacher/QuizPreviewScreen.kt (CREATE)
       └─ Quiz preview: question list with correct answers + explanations.
          [Edit Questions] [Regenerate] [Publish] buttons. Edit mode:
          inline edit of question text, options, correct answer.
  4.10 composeApp/.../screens/teacher/TeacherPortalV2.kt (EDIT)
       └─ Add overlays: SyllabusUpload, SyllabusParsePreview,
          SyllabusCheckIn, QuizGeneration, QuizPreview
  4.11 composeApp/.../screens/teacher/TeacherProfileScreenV2.kt (EDIT)
       └─ Add "Syllabus Check-in Reminders" setting section with
          suppress mode radio options
  4.12 BUILD: ./gradlew :composeApp:compileDevDebugKotlinAndroid

PHASE 5 — App UI: Parent Daily Summary + Quiz
  5.1  shared/.../feature/parent/presentation/ParentDashboardViewModel.kt (EDIT)
       └─ Extend coveredToday with summaryText + isAiEstimated per subject.
          Fetch daily summary from new endpoint. Add pendingQuizzes state.
  5.2  shared/.../feature/parent/presentation/ParentQuizViewModel.kt (CREATE)
       └─ VM: fetch quiz, track answers, submit, show result. StateFlow-only.
  5.3  composeApp/.../screens/parent/ParentHomeScreenV2.kt (EDIT)
       └─ Add "Today's Learning" card: per-subject summary text + coverage %.
          Teacher-populated: no badge. AI-estimated: "ℹ AI Estimated" badge
          in muted text. [View Full Syllabus] button.
  5.4  composeApp/.../screens/parent/ParentCoveredDetailOverlay.kt (EDIT)
       └─ Show summary text per subject + estimation label. Extend existing
          overlay, don't replace.
  5.5  composeApp/.../screens/parent/ParentQuizScreen.kt (CREATE)
       └─ Quiz answering UI: question text, MCQ radio buttons (A/B/C/D),
          fill-blank text input, true/false toggle. Progress bar. [Previous]
          [Next] navigation. [Submit] on last question. Confirmation dialog.
  5.6  composeApp/.../screens/parent/ParentQuizResultScreen.kt (CREATE)
       └─ Score display (score/total, %), rank in class. Per-question review:
          your answer vs correct answer, ✅/❌ indicator, explanation.
          [Review Answers] [Done] buttons.
  5.7  composeApp/.../screens/parent/ParentAcademicsScreenV2.kt (EDIT)
       └─ Add "Pending Quizzes" section: list of unsubmitted quizzes with
          due date. Tap → opens ParentQuizScreen.
  5.8  composeApp/.../screens/parent/ParentPortalV2.kt (EDIT)
       └─ Add overlays: ParentQuiz, ParentQuizResult
  5.9  BUILD: ./gradlew :composeApp:compileDevDebugKotlinAndroid

PHASE 6 — App UI: Teacher Quiz Results + Admin Pace Alerts
  6.1  shared/.../feature/teacher/presentation/QuizResultsViewModel.kt (CREATE)
       └─ VM: fetch ranked results, question breakdown, send results to parents.
  6.2  shared/.../feature/admin/presentation/SyllabusPaceViewModel.kt (CREATE)
       └─ VM: fetch alerts, coverage view, recalculate.
  6.3  composeApp/.../screens/teacher/TeacherQuizResultsScreen.kt (CREATE)
       └─ Ranked list: rank, student name, score/total, %, late flag.
          Class average. Question breakdown: position, text, correct count,
          status (EASY/OK/REVIEW). [Send Results to Parents] button.
  6.4  composeApp/.../screens/school/SyllabusPaceAlertsScreen.kt (CREATE)
       └─ Active alerts list: alert level badge (BEHIND/CRITICAL/AHEAD),
          class+subject+teacher, expected vs actual %, AI Confirmed badge.
          [View Syllabus] [Contact Teacher] buttons.
  6.5  composeApp/.../screens/school/SyllabusCoverageScreen.kt (CREATE)
       └─ Per-class coverage table: subject, coverage %, expected %, status
          (BEHIND/ON_TRACK/AHEAD), teacher name. Overall summary row.
  6.6  composeApp/.../screens/teacher/TeacherPortalV2.kt (EDIT)
       └─ Add QuizResults overlay
  6.7  composeApp/.../screens/school/SchoolRecordsScreenV2.kt (EDIT)
       └─ Add "Syllabus Pace" section linking to alerts + coverage
  6.8  composeApp/.../screens/school/SchoolPortalV2.kt (EDIT)
       └─ Add overlays: SyllabusPaceAlerts, SyllabusCoverage
  6.9  BUILD: ./gradlew :composeApp:compileDevDebugKotlinAndroid

PHASE 7 — Notifications + Offline Support + Final Testing
  7.1  Server notification wiring (follow existing Notify.toUsers pattern):
       └─ Syllabus parsed & confirmed → Teacher: "Syllabus uploaded successfully"
       └─ Daily check-in saved → Parents (optional): "Today's summary available"
       └─ Pace alert BEHIND → Teacher + Admin: "Syllabus behind schedule"
       └─ Pace alert CRITICAL → Teacher + Admin: "URGENT: critically behind"
       └─ Pace alert AHEAD → Teacher: "Ahead of schedule, great progress!"
       └─ Pace recovered → Teacher: "Back on track"
       └─ Pace weekly digest → Parents: "Weekly syllabus update"
       └─ Quiz published → Parents of class: "New quiz: {title}. Due: {date}"
       └─ Quiz submitted → Teacher: "{student} submitted: {score}/{total}"
       └─ Quiz results available → Parents: "Results available: {title}"
       └─ Quiz results sent → Parents: "{child} scored {score}/{total} (Rank {rank})"
  7.2  shared/.../data/local/DailySummaryCacheDao.kt (CREATE)
       └─ Room DAO for daily summary cache: insert, query by childId+date,
          delete old. Cap 30 entries per child (LRU by date).
  7.3  shared/.../data/local/AppDatabase.kt (EDIT)
       └─ Add DailySummaryCacheEntity. Bump version 4 → 5.
       └─ Create MIGRATION_4_5 (add daily_summary_cache table)
  7.4  shared/.../feature/parent/data/repository/ParentDailySummaryRepositoryImpl.kt (EDIT)
       └─ Cache-then-network: read from Room first, then fetch from API,
          update cache. Follow existing AnnouncementRepositoryImpl pattern.
  7.5  Create ALL server tests (see plan §9.1 for full list):
       └─ SyllabusParseTest, SyllabusDeleteTest, DailyClassLogTest,
          SyllabusPaceTest, QuizGenerationTest, QuizSubmissionTest,
          QuizResultsTest, ParentDailySummaryTest, ParentSyllabusMigrationTest,
          PopupPrefsTest
  7.6  Create ALL shared tests (see plan §9.2):
       └─ SyllabusAiRepositoryTest, QuizRepositoryTest,
          SyllabusPaceRepositoryTest, ParentDailySummaryRepositoryTest
  7.7  Final BUILD + TEST all modules:
       set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.11.10-hotspot
       ./gradlew :shared:compileKotlinJvm :shared:jvmTest
       ./gradlew :shared:compileDevDebugKotlinAndroid :composeApp:compileDevDebugKotlinAndroid
       ./gradlew :server:compileKotlin :server:test

═══════════════════════════════════════════════════════════════════════════
OUTPUT FORMAT — EVERY TURN
═══════════════════════════════════════════════════════════════════════════

## NODE: <PLAN|BUILD|TEST|REVIEW|ITERATE|COMMIT>
## ITEM: <phase>.<step> — <short description>

### Summary
<concise>

### Files
- `path/to/file.kt` — <ADD|MODIFY> — <why>

### Code
<full content for new files; exact change for modifications>

### Test Results (TEST node)
Command: ./gradlew :shared:compileKotlinJvm :shared:jvmTest
Expected: BUILD SUCCESSFUL
Observed: <actual>

### Review Checklist (REVIEW node)
[SOLID-S]      PASS/FAIL — <reason>
[SOLID-O]      PASS/FAIL — <reason>
[SOLID-L]      PASS/FAIL — <reason>
[SOLID-I]      PASS/FAIL — <reason>
[SOLID-D]      PASS/FAIL — <reason>
[MVVM]         PASS/FAIL — <reason>
[CleanArch]    PASS/FAIL — <reason>
[DevStandards] PASS/FAIL — <reason>
[AISafety]     PASS/FAIL — <reason>
[ConflictFree] PASS/FAIL — <reason>
[Security]     PASS/FAIL — <reason>
[Integration]  PASS/FAIL — <reason>
[Koin]         PASS/FAIL — <reason>
[SerialName]   PASS/FAIL — <reason>
[ParentUX]     PASS/FAIL — <reason>
[TeacherUX]    PASS/FAIL — <reason>
[AdminUX]      PASS/FAIL — <reason>
[A11y]         PASS/FAIL — <reason>
[JunkScan]     PASS/FAIL — <reason>
[Compiles]     PASS/FAIL — <reason>
[Tests]        PASS/FAIL — <reason>

### NEXT ACTION
<stay in loop / advance to X.Y / phase complete / ALL COMPLETE>

═══════════════════════════════════════════════════════════════════════════
HARD STOPS — ASK, DON'T GUESS
═══════════════════════════════════════════════════════════════════════════

1. Unknown endpoint contract → STOP, request route spec from plan §4.
2. Ambiguous AI prompt strategy → use plan §7.1 pseudo-code as template.
3. NEVER destructive migration. All changes additive. Never DROP or ALTER
   existing columns. New tables only. New columns = ALTER TABLE ADD COLUMN
   (nullable/defaults).
4. Unclear Compose API → check existing screens first. Codebase = truth.
5. Cannot compile after 3 iterations → STOP, report error with full context.
6. AI provider all circuits OPEN → return AiResult.unavailable(). Feature
   degrades gracefully: syllabus parse → manual entry fallback. Daily
   summary → use last known pace plan estimate. Quiz generation → error
   message "AI unavailable, try again later". Pace reconfirmation → skip
   alert (don't send unconfirmed alerts).
7. Migrations not applied → STOP. Phase 0 MUST be complete before any other
   phase. All table objects must exist in Tables.kt + DatabaseFactory.kt.
8. Parent syllabus read migration → MUST filter isActive=true on
   curriculum_units. Deleted units must not appear in parent view.
9. Quiz correct answers → NEVER included in GET /parent/child/{id}/quiz/
   response. Only returned after submit (in QuizResultDto) or in teacher
   preview (QuizPreviewDto).
10. Pace alerts → NEVER sent to parents in real-time. Parents get weekly
    digest only. Real-time alerts go to teacher + admin only.

═══════════════════════════════════════════════════════════════════════════
DEFINITION OF DONE — PER ITEM
═══════════════════════════════════════════════════════════════════════════

[ ] Compiles: JVM + Android + Server
[ ] jvmTest + server:test green
[ ] Follows DEVELOPMENT_STANDARDS (all 8 items)
[ ] Koin-wired and used (if new class)
[ ] Zero junk/dead/duplicate/unused code
[ ] @SerialName on all DTO fields
[ ] Topic ownership checked server-side
[ ] Coverage bounds validated (0-100)
[ ] Quiz correct answers never leaked before submit
[ ] AI degrades gracefully (manual fallback)
[ ] schoolId in every WHERE clause (multi-tenant isolation)
[ ] Committed with conventional message

═══════════════════════════════════════════════════════════════════════════
DEFINITION OF DONE — ENTIRE ECOSYSTEM
═══════════════════════════════════════════════════════════════════════════

[ ] Phase 0: 7 new tables migrated + 4 existing tables extended. All
    registered in DatabaseFactory.kt. Server compiles.
[ ] Phase 1: SyllabusAiService with 6 AI functions. TeacherSyllabusRouting
    extended with 8 new endpoints. SyllabusPaceService with scheduled
    monitoring + AI reconfirmation. SyllabusPaceRouting with 3 endpoints.
    ParentAcademicsRouting extended with daily-summary + typed syllabus
    read migration. Server tests green.
[ ] Phase 2: TeacherQuizRouting with 3 endpoints. ParentQuizRouting with
    3 endpoints. Auto-scoring + rank recalculation. Server tests green.
[ ] Phase 3: All shared models, APIs, repos, VMs created. Koin wired.
    jvmTest green.
[ ] Phase 4: Teacher syllabus extensions — upload sheet, parse preview,
    3-level subtopic display, delete, daily check-in popup, quiz
    generation sheet, quiz preview screen. Android compiles.
[ ] Phase 5: Parent daily summary card on home tab, quiz answering screen,
    quiz result screen, pending quizzes section. Android compiles.
[ ] Phase 6: Teacher quiz results screen with rankings + breakdown. Admin
    pace alerts screen + coverage screen. Android compiles.
[ ] Phase 7: All 11 notification triggers working. Room cache for daily
    summary (AppDatabase v5). All tests green. Final build all modules.
[ ] ALL builds green: JVM + Android + Server
[ ] ALL tests green: server + shared
[ ] Zero junk in entire diff
[ ] Every endpoint has schoolId in WHERE clause
[ ] Every DTO has @SerialName on all fields
[ ] Every new class is Koin-wired and used
[ ] No existing endpoint broken — all changes additive
[ ] AI features degrade gracefully — manual fallback always available
[ ] Quiz correct answers never leaked before submission
[ ] Pace alerts AI-reconfirmed before sending
[ ] Parent syllabus read migrated to typed tables with isActive filter

═══════════════════════════════════════════════════════════════════════════
BEGIN
═══════════════════════════════════════════════════════════════════════════

Begin at NODE: PLAN with Phase 0, item 0.1 from
AGENTIC_SYLLABUS_MANAGEMENT_PLAN.md §6.
Read the plan file. Read Tables.kt (line ~1059 for CurriculumUnitsTable,
line ~1079 for SyllabusProgressTable, line ~1110 for HomeworkTable,
line ~1159 for HomeworkSubmissionsTable) to understand the UUIDTable pattern.
Read DatabaseFactory.kt (line ~110) to understand the allTables array.
Read TeacherSyllabusRouting.kt to understand the routing + DTO pattern.
Then plan item 0.1 — the migration_110_syllabus_agentic.sql file.
The loop starts now.
