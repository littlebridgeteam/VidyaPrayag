-- V9: Exam timetable, syllabus mapping, and reminder log tables

-- Exam timetables (container for a set of exam entries)
CREATE TABLE IF NOT EXISTS exam_timetables (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id       UUID NOT NULL,
    teacher_id      UUID NOT NULL,
    class_name      TEXT NOT NULL,
    section         VARCHAR(8) NOT NULL DEFAULT 'A',
    academic_year_id UUID,
    name            TEXT NOT NULL,
    term            VARCHAR(32),
    status          VARCHAR(16) NOT NULL DEFAULT 'draft',
    source_image_url TEXT,
    ai_used         BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now()
);

-- Individual exam slots within a timetable
CREATE TABLE IF NOT EXISTS exam_timetable_entries (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    timetable_id    UUID NOT NULL REFERENCES exam_timetables(id) ON DELETE CASCADE,
    assessment_id   UUID,
    calendar_event_id UUID,
    school_id       UUID NOT NULL,
    exam_date       DATE NOT NULL,
    start_time      TIME,
    end_time        TIME,
    subject         TEXT NOT NULL,
    exam_name       TEXT NOT NULL,
    max_marks       INTEGER NOT NULL DEFAULT 100,
    room            VARCHAR(64),
    sort_order      INTEGER NOT NULL DEFAULT 0,
    created_at      TIMESTAMP NOT NULL DEFAULT now()
);

-- Many-to-many: which curriculum units to study for a given assessment
CREATE TABLE IF NOT EXISTS exam_syllabus_mapping (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    assessment_id       UUID NOT NULL,
    curriculum_unit_id  UUID NOT NULL,
    school_id           UUID NOT NULL,
    created_at          TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT ux_exam_syllabus_mapping UNIQUE (assessment_id, curriculum_unit_id)
);

-- Prevents duplicate evening-before exam reminders
CREATE TABLE IF NOT EXISTS exam_reminder_log (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    assessment_id   UUID NOT NULL,
    school_id       UUID NOT NULL,
    reminded_at     TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT ux_exam_reminder_log UNIQUE (assessment_id)
);

-- Indexes for common queries
CREATE INDEX IF NOT EXISTS idx_exam_timetables_school ON exam_timetables(school_id);
CREATE INDEX IF NOT EXISTS idx_exam_timetables_teacher ON exam_timetables(teacher_id);
CREATE INDEX IF NOT EXISTS idx_exam_timetable_entries_timetable ON exam_timetable_entries(timetable_id);
CREATE INDEX IF NOT EXISTS idx_exam_timetable_entries_school ON exam_timetable_entries(school_id);
CREATE INDEX IF NOT EXISTS idx_exam_syllabus_mapping_assessment ON exam_syllabus_mapping(assessment_id);
CREATE INDEX IF NOT EXISTS idx_exam_syllabus_mapping_school ON exam_syllabus_mapping(school_id);
CREATE INDEX IF NOT EXISTS idx_exam_reminder_log_assessment ON exam_reminder_log(assessment_id);
CREATE INDEX IF NOT EXISTS idx_exam_reminder_log_school ON exam_reminder_log(school_id);
