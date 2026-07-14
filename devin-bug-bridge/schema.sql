-- D1 schema for the Devin Bug Bridge queue

CREATE TABLE IF NOT EXISTS bug_queue (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  slack_channel_id TEXT NOT NULL,
  slack_thread_ts TEXT,
  slack_message_ts TEXT NOT NULL,
  slack_user TEXT,
  text TEXT,
  files TEXT, -- JSON array of Slack file objects
  status TEXT NOT NULL DEFAULT 'pending', -- pending, processing, done, failed
  batch_id TEXT,
  devin_session_id TEXT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_status_created
  ON bug_queue (status, created_at);

CREATE INDEX IF NOT EXISTS idx_batch
  ON bug_queue (batch_id);
