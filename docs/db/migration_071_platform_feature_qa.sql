-- =====================================================================
-- migration_071_platform_feature_qa.sql
-- Feature & QA Management Platform (spec §3)
--
-- 15 new platform-level tables (no school_id): 11 registry + 4 auto-discovery.
-- All use UUID PKs (gen_random_uuid), timestamps, JSONB stored as text/jsonb.
--
-- Run in Supabase SQL Editor before deploying the matching server code.
-- AUTO_CREATE_TABLES is OFF in prod; validateSchema() gates boot on these.
-- =====================================================================

-- 3.1 platform_features (Feature Registry)
CREATE TABLE IF NOT EXISTS platform_features (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    feature_id      VARCHAR(128) NOT NULL UNIQUE,
    name            VARCHAR(200) NOT NULL,
    description     TEXT,
    business_goal   TEXT,
    product_area    VARCHAR(64),
    category        VARCHAR(64),
    module          VARCHAR(64),
    parent_id       UUID REFERENCES platform_features(id) ON DELETE SET NULL,
    status          VARCHAR(32) NOT NULL DEFAULT 'planned',
    completion_pct  INTEGER NOT NULL DEFAULT 0,
    priority        VARCHAR(16) NOT NULL DEFAULT 'medium',
    severity        VARCHAR(16),
    business_impact VARCHAR(64),
    tech_complexity VARCHAR(64),
    risk_level      VARCHAR(32),
    dependencies    TEXT NOT NULL DEFAULT '[]',
    blockers        TEXT,
    estimated_effort VARCHAR(4),
    owner_id        UUID REFERENCES app_users(id) ON DELETE SET NULL,
    team            VARCHAR(64),
    sprint          VARCHAR(64),
    version_intro   VARCHAR(32),
    target_release  VARCHAR(64),
    release_status  VARCHAR(32),
    tags            TEXT NOT NULL DEFAULT '[]',
    metadata        TEXT NOT NULL DEFAULT '{}',
    legacy_imported BOOLEAN NOT NULL DEFAULT FALSE,
    is_archived     BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by      UUID REFERENCES app_users(id) ON DELETE SET NULL,
    updated_by      UUID REFERENCES app_users(id) ON DELETE SET NULL
);
CREATE INDEX IF NOT EXISTS ix_pf_status       ON platform_features(status);
CREATE INDEX IF NOT EXISTS ix_pf_priority     ON platform_features(priority);
CREATE INDEX IF NOT EXISTS ix_pf_product_area ON platform_features(product_area);
CREATE INDEX IF NOT EXISTS ix_pf_owner        ON platform_features(owner_id);
CREATE INDEX IF NOT EXISTS ix_pf_parent       ON platform_features(parent_id);
CREATE INDEX IF NOT EXISTS ix_pf_archived     ON platform_features(is_archived) WHERE is_archived = FALSE;

-- 3.2 platform_feature_flows
CREATE TABLE IF NOT EXISTS platform_feature_flows (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    feature_id      UUID NOT NULL REFERENCES platform_features(id) ON DELETE CASCADE,
    flow_name       VARCHAR(200) NOT NULL,
    flow_description TEXT,
    flow_steps      TEXT NOT NULL DEFAULT '[]',
    entry_points    TEXT NOT NULL DEFAULT '[]',
    exit_points     TEXT NOT NULL DEFAULT '[]',
    deep_links      TEXT NOT NULL DEFAULT '[]',
    edge_cases      TEXT NOT NULL DEFAULT '[]',
    sort_order      INTEGER NOT NULL DEFAULT 0,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS ix_pff_feature ON platform_feature_flows(feature_id);

-- 3.3 platform_screens (Screen Registry)
CREATE TABLE IF NOT EXISTS platform_screens (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    screen_id       VARCHAR(128) NOT NULL UNIQUE,
    name            VARCHAR(200) NOT NULL,
    route           TEXT,
    module          VARCHAR(64),
    purpose         TEXT,
    screenshot_url  TEXT,
    permissions     TEXT NOT NULL DEFAULT '[]',
    user_actions    TEXT NOT NULL DEFAULT '[]',
    connected_screens TEXT NOT NULL DEFAULT '[]',
    empty_state     TEXT,
    loading_state   TEXT,
    error_state     TEXT,
    feature_id      UUID REFERENCES platform_features(id) ON DELETE SET NULL,
    sort_order      INTEGER NOT NULL DEFAULT 0,
    metadata        TEXT NOT NULL DEFAULT '{}',
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS ix_ps_module  ON platform_screens(module);
CREATE INDEX IF NOT EXISTS ix_ps_feature ON platform_screens(feature_id);

-- 3.4 platform_feature_apis (API Mapping)
CREATE TABLE IF NOT EXISTS platform_feature_apis (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    feature_id      UUID NOT NULL REFERENCES platform_features(id) ON DELETE CASCADE,
    endpoint        TEXT NOT NULL,
    method          VARCHAR(8) NOT NULL,
    description     TEXT,
    db_entities     TEXT NOT NULL DEFAULT '[]',
    caching         VARCHAR(64),
    feature_flag    VARCHAR(64),
    analytics_events TEXT NOT NULL DEFAULT '[]',
    notifications   TEXT NOT NULL DEFAULT '[]',
    is_documented   BOOLEAN NOT NULL DEFAULT FALSE,
    sort_order      INTEGER NOT NULL DEFAULT 0,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS ix_pfa_feature ON platform_feature_apis(feature_id);

-- 3.5 platform_test_cases
CREATE TABLE IF NOT EXISTS platform_test_cases (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    case_id         VARCHAR(128) NOT NULL UNIQUE,
    feature_id      UUID NOT NULL REFERENCES platform_features(id) ON DELETE CASCADE,
    screen_id       UUID REFERENCES platform_screens(id) ON DELETE SET NULL,
    api_id          UUID REFERENCES platform_feature_apis(id) ON DELETE SET NULL,
    title           VARCHAR(200) NOT NULL,
    description     TEXT,
    preconditions   TEXT,
    test_steps      TEXT NOT NULL DEFAULT '[]',
    expected_result TEXT,
    priority        VARCHAR(16) NOT NULL DEFAULT 'medium',
    test_type       VARCHAR(32) NOT NULL DEFAULT 'functional',
    status          VARCHAR(16) NOT NULL DEFAULT 'not_run',
    assigned_to     UUID REFERENCES app_users(id) ON DELETE SET NULL,
    build_version   VARCHAR(64),
    environment     VARCHAR(16),
    devices         TEXT NOT NULL DEFAULT '[]',
    os_versions     TEXT NOT NULL DEFAULT '[]',
    platform        VARCHAR(16) NOT NULL DEFAULT 'all',
    last_tested_at  TIMESTAMP,
    last_tested_by  UUID REFERENCES app_users(id) ON DELETE SET NULL,
    failure_reason  TEXT,
    metadata        TEXT NOT NULL DEFAULT '{}',
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by      UUID REFERENCES app_users(id) ON DELETE SET NULL
);
CREATE INDEX IF NOT EXISTS ix_ptc_feature   ON platform_test_cases(feature_id);
CREATE INDEX IF NOT EXISTS ix_ptc_status    ON platform_test_cases(status);
CREATE INDEX IF NOT EXISTS ix_ptc_priority  ON platform_test_cases(priority);
CREATE INDEX IF NOT EXISTS ix_ptc_assigned  ON platform_test_cases(assigned_to);

-- 3.6 platform_test_attachments
CREATE TABLE IF NOT EXISTS platform_test_attachments (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    test_case_id    UUID REFERENCES platform_test_cases(id) ON DELETE CASCADE,
    bug_id          UUID REFERENCES platform_bugs(id) ON DELETE CASCADE,
    file_name       VARCHAR(255) NOT NULL,
    file_url        TEXT NOT NULL,
    file_type       VARCHAR(32) NOT NULL,
    mime_type       VARCHAR(128),
    file_size_bytes BIGINT,
    uploaded_by     UUID REFERENCES app_users(id) ON DELETE SET NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS ix_pta_test_case ON platform_test_attachments(test_case_id);
CREATE INDEX IF NOT EXISTS ix_pta_bug       ON platform_test_attachments(bug_id);

-- 3.7 platform_bugs
CREATE TABLE IF NOT EXISTS platform_bugs (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    bug_id          VARCHAR(16) NOT NULL UNIQUE,
    title           VARCHAR(200) NOT NULL,
    description     TEXT,
    feature_id      UUID REFERENCES platform_features(id) ON DELETE SET NULL,
    screen_id       UUID REFERENCES platform_screens(id) ON DELETE SET NULL,
    api_id          UUID REFERENCES platform_feature_apis(id) ON DELETE SET NULL,
    test_case_id    UUID REFERENCES platform_test_cases(id) ON DELETE SET NULL,
    status          VARCHAR(32) NOT NULL DEFAULT 'reported',
    priority        VARCHAR(16) NOT NULL DEFAULT 'medium',
    severity        VARCHAR(16),
    reproducibility VARCHAR(32),
    environment     VARCHAR(16),
    build_version   VARCHAR(64),
    platform        VARCHAR(16),
    device          VARCHAR(128),
    os_version      VARCHAR(64),
    steps_to_reproduce TEXT NOT NULL DEFAULT '[]',
    expected_result TEXT,
    actual_result   TEXT,
    reported_by     UUID REFERENCES app_users(id) ON DELETE SET NULL,
    assigned_to     UUID REFERENCES app_users(id) ON DELETE SET NULL,
    triaged_by      UUID REFERENCES app_users(id) ON DELETE SET NULL,
    fixed_by        UUID REFERENCES app_users(id) ON DELETE SET NULL,
    verified_by     UUID REFERENCES app_users(id) ON DELETE SET NULL,
    sla_due_at      TIMESTAMP,
    resolved_at     TIMESTAMP,
    closed_at       TIMESTAMP,
    tags            TEXT NOT NULL DEFAULT '[]',
    metadata        TEXT NOT NULL DEFAULT '{}',
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS ix_pb_status    ON platform_bugs(status);
CREATE INDEX IF NOT EXISTS ix_pb_priority  ON platform_bugs(priority);
CREATE INDEX IF NOT EXISTS ix_pb_severity  ON platform_bugs(severity);
CREATE INDEX IF NOT EXISTS ix_pb_feature   ON platform_bugs(feature_id);
CREATE INDEX IF NOT EXISTS ix_pb_assigned  ON platform_bugs(assigned_to);

-- 3.8 platform_bug_comments
CREATE TABLE IF NOT EXISTS platform_bug_comments (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    bug_id          UUID NOT NULL REFERENCES platform_bugs(id) ON DELETE CASCADE,
    author_id       UUID REFERENCES app_users(id) ON DELETE SET NULL,
    body            TEXT NOT NULL,
    mentions        TEXT NOT NULL DEFAULT '[]',
    is_internal     BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS ix_pbc_bug ON platform_bug_comments(bug_id);

-- 3.9 platform_bug_activity
CREATE TABLE IF NOT EXISTS platform_bug_activity (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    bug_id          UUID NOT NULL REFERENCES platform_bugs(id) ON DELETE CASCADE,
    actor_id        UUID REFERENCES app_users(id) ON DELETE SET NULL,
    action          VARCHAR(64) NOT NULL,
    field           VARCHAR(64),
    old_value       TEXT,
    new_value       TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS ix_pba_bug ON platform_bug_activity(bug_id);

-- 3.10 platform_audit_log
CREATE TABLE IF NOT EXISTS platform_audit_log (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    actor_id        UUID REFERENCES app_users(id) ON DELETE SET NULL,
    action          VARCHAR(64) NOT NULL,
    entity_type     VARCHAR(32) NOT NULL,
    entity_id       UUID,
    old_snapshot    TEXT,
    new_snapshot    TEXT,
    ip_address      TEXT,
    user_agent      TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS ix_pal_actor       ON platform_audit_log(actor_id);
CREATE INDEX IF NOT EXISTS ix_pal_entity_type ON platform_audit_log(entity_type);
CREATE INDEX IF NOT EXISTS ix_pal_action      ON platform_audit_log(action);
CREATE INDEX IF NOT EXISTS ix_pal_created     ON platform_audit_log(created_at);

-- 3.11 platform_notifications
CREATE TABLE IF NOT EXISTS platform_notifications (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL REFERENCES app_users(id) ON DELETE CASCADE,
    category        VARCHAR(32) NOT NULL DEFAULT 'general',
    title           TEXT NOT NULL,
    body            TEXT NOT NULL DEFAULT '',
    entity_type     VARCHAR(32),
    entity_id       UUID,
    deep_link       TEXT,
    is_read         BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS ix_pn_user_unread ON platform_notifications(user_id, is_read);

-- =====================================================================
-- Auto-Discovery tables (spec §19.1)
-- =====================================================================

-- 19.1a platform_discovered_screens
CREATE TABLE IF NOT EXISTS platform_discovered_screens (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    screen_id       VARCHAR(128) NOT NULL,
    name            TEXT NOT NULL,
    module          VARCHAR(64) NOT NULL,
    file_path       TEXT NOT NULL,
    portal          VARCHAR(32),
    overlay_enum    VARCHAR(64),
    deep_link_path  TEXT,
    is_mapped       BOOLEAN NOT NULL DEFAULT FALSE,
    mapped_screen_id UUID REFERENCES platform_screens(id) ON DELETE SET NULL,
    discovered_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    last_seen_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    file_modified_at TIMESTAMP,
    UNIQUE(screen_id)
);

-- 19.1b platform_discovered_apis
CREATE TABLE IF NOT EXISTS platform_discovered_apis (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    method          VARCHAR(8) NOT NULL,
    path            TEXT NOT NULL,
    file_path       TEXT NOT NULL,
    feature_package VARCHAR(64),
    description     TEXT,
    is_mapped       BOOLEAN NOT NULL DEFAULT FALSE,
    mapped_api_id   UUID REFERENCES platform_feature_apis(id) ON DELETE SET NULL,
    is_alive        BOOLEAN,
    last_checked_at TIMESTAMP,
    response_ms     INTEGER,
    status_code     INTEGER,
    discovered_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    last_seen_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(method, path)
);

-- 19.1c platform_feature_files
CREATE TABLE IF NOT EXISTS platform_feature_files (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    feature_id      UUID NOT NULL REFERENCES platform_features(id) ON DELETE CASCADE,
    file_path       TEXT NOT NULL,
    file_type       VARCHAR(16) NOT NULL,
    last_modified_at TIMESTAMP,
    last_commit_sha VARCHAR(40),
    last_commit_msg TEXT,
    last_commit_author VARCHAR(128),
    UNIQUE(feature_id, file_path)
);

-- 19.1d platform_api_health_checks
CREATE TABLE IF NOT EXISTS platform_api_health_checks (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    discovered_api_id UUID NOT NULL REFERENCES platform_discovered_apis(id) ON DELETE CASCADE,
    checked_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    status_code     INTEGER,
    response_ms     INTEGER,
    is_alive        BOOLEAN,
    error_message   TEXT
);
CREATE INDEX IF NOT EXISTS ix_ahc_api ON platform_api_health_checks(discovered_api_id);
