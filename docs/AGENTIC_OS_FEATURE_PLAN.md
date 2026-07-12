# Enroll+ Agentic OS — Feature Plan & Architecture

> The first agentic OS for campus — moving schools from manual to automated.
> Automate ~80% of manual work for Admin, Teacher, and Parent.

## Architecture

```
ADMIN AGENTS → ─┐
TEACHER AGENTS →├── AGENT ORCHESTRATOR ──→ EXISTING DATA LAYER
PARENT AGENTS → ─┘  (Event Bus → Conditions → Actions → Audit)
```

**Tech Stack:** Kotlin/Ktor (existing) | PostgreSQL (existing) | Compose MP (existing) | Next.js (existing) | LlmClient (existing) | FCM (existing) | WhatsApp Business API (new) | Razorpay/Cashfree (new) | Google Maps + OR-Tools (new) | Google Vision (new) | Playwright (new) | PDFBox (new)

## Agent Orchestrator (Core Engine)

Central nervous system. Every agent registers, listens to events, dispatches actions, logs outcomes.

**New tables:** `agent_events`, `agent_audit_log`

**Key events:** `attendance.absent`, `fee.overdue`, `fee.paid`, `homework.missed`, `leave.approved`, `syllabus.covered`, `pews.flag_raised`, `test.completed`, `transport.delayed`, `admission.lead_created`, `teacher.absent`, `compliance.expiring`, `enrollment.confirmed`, `report_card.published`

**Condition types:** Rule-based (IF/THEN), AI-classified (LLM), Threshold, Pattern, Composite (AND/OR)

**Action types:** WHATSAPP_SEND, SMS_SEND, EMAIL_SEND, PUSH, UPI_LINK, PDF_GEN, CALENDAR_EVENT, WEBHOOK, PEWS_FLAG, CREATE_TASK, ESCALATE_HUMAN

**Learning loop:** Monthly job analyzes outcomes → adjusts thresholds (e.g., "WhatsApp response 68% vs SMS 22% → prefer WhatsApp")

**Admin dashboard:** `/admin/agents` — all agents, event queue, action log, outcome metrics, per-agent toggle

---

## Features (25)

### F1. Auto-Attendance Reconciliation Agent
**Replaces:** Admin manually calling parents of absent students.

**Flow:** Teacher marks absent → cross-verify (leave? bus GPS? sibling? calendar?) → classify (authorized/family/unverified) → escalation chain (WhatsApp T+5min → SMS T+25min → teacher T+45min → PEWS T+60min → admin T+2hr) → auto-reconcile if parent replies.

**Tech:** SQL cross-verification on existing tables | WhatsApp Business API | `LlmClient` for reply parsing | existing `PewsSnapshotService` | existing `NotificationService`

**New table:** `attendance_reconciliation_log`

**Connects to:** F5 (score), F3 (compliance), F7 (fee hold), F15 (circular deadline), F21 (reply parsing), F12 (priority PTM), F23 (teacher review), PEWS, Leave, Transport

### F2. Admission Lead-to-Enrollment Pipeline
**Replaces:** Manual lead tracking in spreadsheets.

**Flow:** Multi-channel capture → AI score (0-100) → tier-based nurture (WhatsApp/email sequence) → auto-schedule visit → post-visit application → enrollment triggers auto-onboarding (account, class, transport, fees, ID card).

**Tech:** Ktor webhooks | `LlmClient` scoring | WhatsApp templates | Google Calendar API | MJML email

**New tables:** `admission_leads`, `lead_interactions`, `lead_scores_history`

**Connects to:** F6 (transport), F7 (fee schedule), F10 (first UPI), F19 (waitlist), F22 (scholarship), F15 (circular), F16 (birthday), F18 (reviews), F3 (RTE), Onboarding, Branding

### F3. Compliance & Audit Agent
**Replaces:** Manual compliance tracking, stressful audit prep.

**Flow:** Board-specific matrix → daily auto-evidence from existing modules → continuous monitoring (30/7/0 day alerts) → one-click inspection-ready PDF → RTE quota tracker → staff compliance sub-agent.

**Tech:** SQL aggregation | Supabase Storage (existing) | PDFBox | JSON board templates | Orchestrator alerts

**New tables:** `compliance_items`, `compliance_certificates`

**Connects to:** F1 (attendance rate), F2 (RTE quota), F13 (staff quals), F14 (infrastructure), F19 (TC compliance), F23 (training), Health, Library, Transport

### F4. AI Exam Paper Generator
**Replaces:** Teachers spending 3+ hours per subject creating papers.

**Flow:** Teacher selects scope → AI blueprint (topic/difficulty/Bloom's) → question generation from syllabus + NCERT → 4 auto-variations (anti-cheating) → question bank accumulator → post-exam moderation report.

**Tech:** `LlmClient` + `NcertReferenceService` (existing) | PostgreSQL FTS | PDFBox

**New tables:** `exam_papers`, `question_bank`, `exam_moderation`

**Connects to:** F17 (teaching gaps), Tutor (weak topics), F11 (comments), F23 (teacher eval), PEWS, Syllabus, NCERT, Gradebook

### F5. Parent Engagement Scorer
**Replaces:** No visibility into parent engagement until problems surface.

**Flow:** 11 signals weighted (app opens 15%, notif read 15%, PTM 15%, messages 10%, fees 10%, homework 10%, leave 5%, events 5%, profile 5%, transport 5%, health 5%) → tier (Champion/Engaged/At-Risk/Disengaged) → auto-actions per tier → student correlation → weekly digest to teacher.

**Tech:** SQL aggregation across 11 tables | weighted formula | `LlmClient` for insights

**New tables:** `parent_engagement_scores`, `engagement_campaigns`

**Connects to:** F1 (absences), F7 (payment), F12 (PTM), F15 (circulars), F21 (messages), F18 (Champions→reviews), PEWS, Tutor, Notifications

### F6. Transport Route Optimizer
**Replaces:** Manual route planning, no dynamic adjustment.

**Flow:** Geocode addresses → OR-Tools optimizes (distance, capacity, time windows) → dynamic re-routing on new admission → GPS variance tracking → auto parent notifications (boarding/arrival/delay) → breakdown auto-response → cost analyzer.

**Tech:** Google Maps Geocoding + Distance Matrix | Google OR-Tools | existing GPS | Orchestrator

**New tables:** `transport_routes`, `transport_stops`, `gps_variance_log`

**Connects to:** F1 (GPS cross-verify), F2 (new student route), F3 (vehicle fitness), F7 (transport fee), F24 (emergency), F5 (engagement), Transport (existing)

### F7. Smart Fee Recovery Agent
**Replaces:** Admin manually chasing defaulters.

**Flow:** Daily overdue detection → AI predicts payment likelihood → risk-based escalation (WhatsApp Day 1 → SMS Day 3 → email Day 7 → admin call list Day 14 → formal notice Day 21 → principal Day 30) → smart adjustments (parent promises payment → pause, partial payment → adjust, high engagement → gentler tone).

**Tech:** `LlmClient` prediction | Orchestrator | PDFBox for notices | UPI links

**New table:** `fee_recovery_actions`

**Connects to:** F1 (fee hold during absence), F2 (fee schedule), F5 (payment promptness), F10 (UPI link), F3 (collection rate), F19 (clearance), F22 (scholarship adjustment), F13 (payroll funding), Fees (existing)

### F8. Homework Auto-Checker
**Replaces:** Teachers manually checking 400 answers per homework.

**Flow:** Student submits (photo/text) → AI classifies objective vs subjective → auto-grade objective → AI suggests marks+feedback for subjective → teacher reviews → daily: missing submission detection → auto-WhatsApp parent → 3 misses → PEWS flag → weekly compliance digest.

**Tech:** Google Vision OCR | `LlmClient` for subjective | Orchestrator for alerts

**New tables:** `homework_submissions`, `homework_compliance`

**Connects to:** F5 (engagement signal), F17 (teaching gaps), F4 (weak topics), PEWS, F11 (report comments), F23 (grading turnaround), Homework (existing), Tutor (existing)

### F9. Auto-Timetable Generator
**Replaces:** Admin spending 3 days creating timetables.

**Flow:** Inputs (teachers, subjects, rooms, availability) → OR-Tools CP-SAT solver (no double-books, load balance, no back-to-back heavy subjects, lunch fairness, lab doubles, part-time windows) → admin review/swap → one-click publish → dynamic substitution on teacher absence.

**Tech:** Google OR-Tools | existing `teacher_periods` | Orchestrator for publish

**New tables:** `timetable_generations`, `substitution_proposals`

**Connects to:** F1 (which teacher marks attendance), F17 (period vs coverage), F23 (load balance), F12 (teacher free slots), Calendar, Teacher Portal, Parent Portal

### F10. UPI Auto-Fee Collection
**Replaces:** Manual fee collection via cash/cheque.

**Flow:** T-7 days → auto UPI deep link (GPay/PhonePe) → parent taps → pays → webhook auto-reconciles → instant branded receipt → partial payment auto-adjusts → no payment → triggers F7.

**Tech:** Razorpay/Cashfree UPI Collect + Intent | Ktor webhook | PDFBox | Orchestrator

**New table:** `upi_payment_links`

**Connects to:** F2 (first fee), F7 (recovery), F5 (promptness signal), F3 (collection rate), F19 (clearance), Fees (existing)

### F11. Auto-Report Card Comment Generator
**Replaces:** Teachers writing 160 generic comments/year.

**Flow:** AI pulls (attendance, marks trend, homework F8, PEWS, tutor heatmap, engagement F5) → personalized comment → teacher reviews → multi-language translation.

**Tech:** `LlmClient` | existing report card module | i18n (existing)

**Connects to:** F8, F5, F4, PEWS, Tutor, Report Card, F17, i18n

### F12. PTM Auto-Scheduler
**Replaces:** Admin spending 2 days coordinating PTM.

**Flow:** Parent preference form → AI allocates (teacher load, PEWS priority, sibling grouping, engagement F5) → auto confirmations + Meet links → auto reschedule → pre-meeting AI brief per student.

**Tech:** Google Calendar + Meet API | `LlmClient` optimization | Orchestrator

**New tables:** `ptm_schedules`, `ptm_slots`, `ptm_preferences`

**Connects to:** F1 (priority absences), F5 (engagement→slot), F9 (teacher slots), F11 (brief), F8, PEWS, F17

### F13. Auto-Staff Payroll
**Replaces:** Excel salary calculation.

**Flow:** Monthly job: base + present days - leave + overtime + substitute bonuses (F9) → auto payslip PDF → WhatsApp + email → flag discrepancies.

**Tech:** SQL + PDFBox + Orchestrator

**New tables:** `payroll_runs`, `payslips`

**Connects to:** F9 (substitute data), F3 (compliance), F23 (performance), F7 (funding), Leave, Teacher Attendance

### F14. Smart Inventory & Procurement Agent
**Replaces:** Manual stock counting, reactive ordering.

**Flow:** Issue log deducts stock → AI predicts "run out in 12 days" → auto PO to vendor → track delivery → flag unusual consumption.

**Tech:** SQL + `LlmClient` prediction + vendor email/webhook

**New tables:** `inventory_items`, `inventory_transactions`, `purchase_orders`

**Connects to:** F3 (infrastructure), F2 (new student demand), F13 (budget), Library

### F15. Auto-Circular & Consent Tracker
**Replaces:** Paper circulars, manual consent collection.

**Flow:** Admin posts → multi-channel send → digital consent → auto-track → 48h reminder → 72h teacher alert → live dashboard → compliance report.

**Tech:** Orchestrator + WhatsApp + existing notification spine

**New tables:** `circulars`, `circular_consents`

**Connects to:** F1 (absent→extended deadline), F2 (new parents), F5 (response rate signal), F24 (emergency), F21 (queries), Announcements

### F16. Auto-Birthday & Event Wisher
**Replaces:** Nobody remembering birthdays.

**Flow:** Daily job → WhatsApp parent (branded) → notify teacher → digital card → staff celebration reminder.

**Tech:** SQL daily job + WhatsApp + PDFBox

**New table:** `birthday_log`

**Connects to:** F2 (enrollment adds), Branding, F5 (engagement boost)

### F17. Auto-Syllabus Pace Catch-Up Planner
**Replaces:** Teachers guessing if they're on track.

**Flow:** System knows (units, covered, working days, exam dates) → AI: "78% covered, 3 units behind" → suggests reallocation → adjusts lesson plans.

**Tech:** SQL + `LlmClient` + existing syllabus + pace

**Connects to:** F4 (moderation→gaps), F8 (compliance), F9 (periods), Tutor, Syllabus, Calendar

### F18. Auto-School Reputation Monitor
**Replaces:** No online reputation tracking.

**Flow:** Daily scrape (Google, JustDial, Facebook) → AI sentiment → flag negative → suggest response → trend correlation → monthly report → auto-request reviews from F5 Champions.

**Tech:** Playwright + Cheerio + `LlmClient` sentiment

**New tables:** `reputation_reviews`, `reputation_sentiment_log`

**Connects to:** F5 (Champions→reviews), F2 (rating→nurturing), Branding

### F19. Auto-Withdrawal & TC Workflow
**Replaces:** 7-10 day manual TC process.

**Flow:** Parent requests → auto-check (fees F7, library, ID) → clearance checklist per dept → all clear → auto TC (branded) → principal signature → deliver → update seat (F2 waitlist).

**Tech:** Orchestrator + PDFBox + digital signature

**New tables:** `withdrawal_requests`, `clearance_checklist`

**Connects to:** F2 (seat opens), F7 (fee clearance), F3 (TC compliance), F6 (route removal), F10 (settlement), Library, ID Card

### F20. Auto-CCTV Incident Detector
**Replaces:** Passive CCTV nobody watches.

**Flow:** AI video analysis → detects (fighting, loitering, unauthorized) → alert admin with clip → log incident → face match to student → repeated → PEWS.

**Tech:** Google Vision / YOLOv8 + existing CCTV RTSP

**New tables:** `cctv_incidents`, `cctv_alerts`

**Connects to:** PEWS, F24 (security broadcast), F23 (discipline record), F3 (safety compliance)

### F21. Auto-Parent Query Router
**Replaces:** Chaotic direct WhatsApp to teachers.

**Flow:** Parent asks → AI classifies (fee→admin, homework→teacher, health→nurse, general→FAQ auto-answer) → confidence <70% → class teacher → tracked → admin dashboard.

**Tech:** `LlmClient` + existing `RagModule` for FAQ

**New tables:** `parent_queries`, `query_routing_log`

**Connects to:** F1 (absence replies), F5 (response time signal), F7 (fee queries), F15 (circular queries), Messages, Notifications

### F22. Auto-Grant & Scholarship Finder
**Replaces:** Nobody proactively matching students to grants.

**Flow:** Profile students → scrape gov+private databases → auto-match → pre-fill forms → notify parents → track status.

**Tech:** Playwright + `LlmClient` + existing scholarship module

**New tables:** `external_scholarships`, `scholarship_matches`

**Connects to:** F2 (lead eligibility), F7 (disbursement→fee adjustment), F3 (RTE), F5 (engagement), Scholarships

### F23. Auto-Staff Performance Reviewer
**Replaces:** Subjective annual teacher evaluation.

**Flow:** Auto-compile (attendance rate, homework submission, score improvement, pace F17, feedback F21, PEWS correlation, grading turnaround F8) → AI review draft → principal reviews → share with teacher.

**Tech:** SQL + `LlmClient`

**New table:** `staff_performance_reviews`

**Connects to:** F1, F4, F8, F9, F17, F21, F13, F3, PEWS, Tutor

### F24. Auto-Emergency Broadcast
**Replaces:** 30+ minutes of manual calling.

**Flow:** Admin taps Emergency → select type/severity/classes → simultaneous (push + WhatsApp + SMS + email + website banner) → track delivery → auto-retry → audit trail.

**Tech:** Orchestrator multi-channel + existing notification spine

**New table:** `emergency_broadcasts`

**Connects to:** F1 (attendance context), F6 (transport emergency), F15 (circular), F20 (CCTV security), F2 (parent contacts), Notifications

### F25. Auto-Competitive Exam Tracker
**Replaces:** No systematic competitive exam tracking.

**Flow:** Student opts in → system knows (exam date, syllabus overlap) → parallel study plan → gap topics → Tutor modules → weekly progress → adjust homework load → post-exam result → achievement analytics.

**Tech:** `LlmClient` + Tutor 2.0 + existing syllabus

**New tables:** `competitive_exams`, `exam_prep_plans`, `exam_results`

**Connects to:** F4 (gap topics), F8 (load adjustment), F17 (overlap), F11 (achievement), Tutor, F5, F23

---

## Key Data Flow Chains

**Safety Loop:** F1→PEWS→F12→F11→F5→F1
**Revenue Loop:** F2→F7→F10→F7→F13→F23→F4→F2
**Academic Loop:** F17→F4→F8→Tutor→F4→F17→F11→F5
**Compliance Loop:** F3←F1←F14←F13←F6←F19→F3
**Admission-to-Graduation:** F2→F22→F10→F6→F15→F16→F1→F8→F4→F11→F25→F18→F2

---

## New Tables Summary (~35)

Orchestrator: `agent_events`, `agent_audit_log` | F1: `attendance_reconciliation_log` | F2: `admission_leads`, `lead_interactions`, `lead_scores_history` | F3: `compliance_items`, `compliance_certificates` | F4: `exam_papers`, `question_bank`, `exam_moderation` | F5: `parent_engagement_scores`, `engagement_campaigns` | F6: `transport_routes`, `transport_stops`, `gps_variance_log` | F7: `fee_recovery_actions` | F8: `homework_submissions`, `homework_compliance` | F9: `timetable_generations`, `substitution_proposals` | F10: `upi_payment_links` | F12: `ptm_schedules`, `ptm_slots`, `ptm_preferences` | F13: `payroll_runs`, `payslips` | F14: `inventory_items`, `inventory_transactions`, `purchase_orders` | F15: `circulars`, `circular_consents` | F16: `birthday_log` | F18: `reputation_reviews`, `reputation_sentiment_log` | F19: `withdrawal_requests`, `clearance_checklist` | F20: `cctv_incidents`, `cctv_alerts` | F21: `parent_queries`, `query_routing_log` | F22: `external_scholarships`, `scholarship_matches` | F23: `staff_performance_reviews` | F24: `emergency_broadcasts` | F25: `competitive_exams`, `exam_prep_plans`, `exam_results`

---

## New External Integrations

| Integration | Used By | Priority |
|-------------|---------|----------|
| WhatsApp Business Cloud API | F1,F2,F7,F10,F12,F15,F16,F21,F24 | P0 |
| Razorpay/Cashfree (UPI) | F10,F7 | P0 |
| Apache PDFBox | F3,F4,F10,F11,F13,F19 | P0 |
| Google Maps + Geocoding | F6 | P1 |
| Google OR-Tools | F6,F9 | P1 |
| Google Calendar API | F2,F9,F12 | P1 |
| Google Vision (OCR+video) | F8,F20 | P2 |
| Google Speech-to-Text | F12 | P2 |
| Playwright + Cheerio | F18,F22 | P2 |

---

## Build Priority & Sequence

### Phase 1 — Foundation (P0)
1. **Agent Orchestrator** — core engine, event bus, action dispatcher
2. **WhatsApp Business API integration** — used by 9 features
3. **F1 Auto-Attendance** — lowest effort, highest safety impact
4. **F7 Smart Fee Recovery** — direct revenue impact
5. **F10 UPI Auto-Fee Collection** — pairs with F7
6. **F2 Admission Pipeline** — revenue growth

### Phase 2 — High Impact (P1)
7. **F5 Parent Engagement Scorer** — data foundation for others
8. **F8 Homework Auto-Checker** — teacher time savings
9. **F9 Auto-Timetable** — admin time savings
10. **F15 Auto-Circular** — parent communication
11. **F3 Compliance Agent** — audit readiness
12. **F6 Transport Optimizer** — safety + efficiency
13. **F12 PTM Scheduler** — admin + parent convenience
14. **F11 Report Card Comments** — teacher time savings
15. **F13 Auto-Payroll** — admin time savings

### Phase 3 — Differentiators (P2)
16. **F4 Exam Paper Generator** — teacher productivity
17. **F17 Syllabus Pace Planner** — academic quality
18. **F14 Inventory Agent** — operational efficiency
19. **F19 Withdrawal/TC Workflow** — process automation
20. **F21 Parent Query Router** — communication efficiency
21. **F23 Staff Performance** — HR automation
22. **F25 Competitive Exam Tracker** — parent value

### Phase 4 — Advanced (P3)
23. **F16 Birthday Wisher** — emotional connection
24. **F18 Reputation Monitor** — brand management
25. **F20 CCTV Incident Detector** — safety automation
26. **F22 Scholarship Finder** — parent financial aid
27. **F24 Emergency Broadcast** — safety communication

---

## The "Zero-Touch School" Vision

**Admin sees only:** exceptions, decisions, strategy. Everything else autopilot.
**Teacher sees only:** teaching, reviewing AI content, human interventions. Everything else autopilot.
**Parent sees only:** what matters, what needs action, what's relevant. Everything else autopilot.
