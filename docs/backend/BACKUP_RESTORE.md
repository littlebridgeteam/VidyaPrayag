# Backup & Restore Strategy

> **RTO** (Recovery Time Objective): < 4 hours  
> **RPO** (Recovery Point Objective): < 24 hours

---

## 1. Backup Strategy

### 1.1 Self-Hosted Postgres

| Backup Type | Frequency | Format | Retention | Storage |
|-------------|-----------|--------|-----------|--------|
| Daily incremental | Daily 02:00 UTC | `pg_dump --format=custom` | 7 days | Local volume |
| Weekly full | Sunday 01:00 UTC | `pg_dump --format=custom` | 4 weeks | Local volume + S3 |
| Monthly archive | 1st of month | `pg_dump --format=custom` | 12 months | S3 Glacier |

#### Cron Setup

```bash
# Edit crontab for the postgres user
sudo crontab -e -u postgres

# Daily backup at 02:00 UTC
0 2 * * * /opt/vidyaprayag/scripts/backup.sh >> /var/log/vidyaprayag-backup.log 2>&1

# Weekly full backup (Sunday 01:00 UTC) — same script, different retention
0 1 * * 0 /opt/vidyaprayag/scripts/backup.sh --full >> /var/log/vidyaprayag-backup.log 2>&1
```

#### WAL Archiving (Point-in-Time Recovery)

Add to `postgresql.conf`:
```ini
archive_mode = on
archive_command = 'test ! -f /var/lib/postgresql/wal_archive/%f && cp %p /var/lib/postgresql/wal_archive/%f'
archive_timeout = '5min'
wal_level = replica
```

This enables point-in-time recovery (PITR) by replaying WAL segments up to a target timestamp.

### 1.2 Supabase (Managed)

Supabase provides automated daily backups on Pro plan and above.

**To verify backups are active:**
1. Navigate to Supabase Dashboard → Project Settings → Backups
2. Confirm "Daily Backups" is enabled
3. Check the last backup timestamp (should be < 24h old)

**Manual export (on-demand):**
```bash
# Using pg_dump against the Supabase connection string
pg_dump "$DATABASE_URL" --format=custom --file=backup-$(date +%Y%m%d).dump
```

**Supabase Dashboard manual export:**
1. Supabase Dashboard → Database → Backups
2. Click "Create Backup" for an on-demand snapshot

### 1.3 Docker Compose Volume Backup

When using `docker-compose.yml` (see GAP-014), the Postgres data volume persists across container restarts. To back up the volume:

```bash
# Back up the Postgres data volume
docker run --rm -v vidyaprayag_db_data:/data -v $(pwd):/backup \
  ubuntu tar czf /backup/db-volume-$(date +%Y%m%d).tar.gz /data

# Restore the volume
docker run --rm -v vidyaprayag_db_data:/data -v $(pwd):/backup \
  ubuntu tar xzf /backup/db-volume-YYYYMMDD.tar.gz -C /
```

---

## 2. Backup Verification

### 2.1 Automated Daily Restore Test

Run a daily restore test on a staging database to verify backup integrity:

```bash
# Restore to a staging database and run a smoke test
./scripts/restore.sh --file backup-YYYYMMDD.dump --target "postgresql://staging:5432/vidyaprayag_test" --verify
```

The `--verify` flag runs basic integrity checks:
- Table count matches expected set
- Row counts for critical tables (app_users, schools, students) > 0
- Flyway schema history table is intact

**Alerting:** If the restore test fails, send an alert (email/Slack webhook). Example:

```bash
if ! ./scripts/restore.sh --file "$LATEST_BACKUP" --target "$STAGING_URL" --verify; then
  curl -X POST "$ALERT_WEBHOOK" \
    -H "Content-Type: application/json" \
    -d '{"text":"BACKUP VERIFICATION FAILED for '"$LATEST_BACKUP"'"}'
fi
```

### 2.2 Manual Verification

```bash
# List backup contents without restoring
pg_restore --list backup-YYYYMMDD.dump

# Verify the dump file is not corrupted
pg_restore --validate backup-YYYYMMDD.dump
```

---

## 3. Restore Procedure

### 3.1 Full Database Restore

```bash
# 1. Stop the application server
docker-compose stop server

# 2. Drop and recreate the database
dropdb --if-exists "$DATABASE_URL"
createdb "$DATABASE_URL"

# 3. Restore from backup
pg_restore --dbname="$DATABASE_URL" --format=custom --clean --if-exists backup-YYYYMMDD.dump

# 4. Verify
psql "$DATABASE_URL" -c "SELECT count(*) FROM app_users;"
psql "$DATABASE_URL" -c "SELECT count(*) FROM flyway_schema_history;"

# 5. Restart the application
docker-compose start server
```

Or use the restore script:
```bash
./scripts/restore.sh --file backup-YYYYMMDD.dump --target "$DATABASE_URL"
```

### 3.2 Partial Restore (Single Table)

```bash
# Restore a single table from a custom-format dump
pg_restore --dbname="$DATABASE_URL" --table=students --format=custom backup-YYYYMMDD.dump
```

### 3.3 Point-in-Time Recovery (WAL Replay)

```bash
# 1. Restore the base backup
pg_restore --dbname="$DATABASE_URL" --format=custom base-backup.dump

# 2. Create a recovery configuration
cat > recovery.conf << 'EOF'
restore_command = 'cp /var/lib/postgresql/wal_archive/%f %p'
recovery_target_time = '2026-07-04 14:30:00 UTC'
recovery_target_action = 'promote'
EOF

# 3. Restart Postgres with recovery.conf in the data directory
# Postgres will replay WAL segments up to the target timestamp and then promote.
```

---

## 4. Disaster Recovery Runbook

### Scenario: Production database corrupted

1. **Assess** (5 min): Confirm corruption — check application logs, run `psql -c "SELECT 1"`.
2. **Stop writes** (5 min): Stop the application server to prevent further damage.
3. **Identify backup** (5 min): Find the most recent valid backup.
   ```bash
   ls -lt /backups/*.dump | head -5
   ```
4. **Restore** (30 min): Follow Section 3.1.
5. **Verify** (15 min): Run smoke tests — login, create test record, check critical endpoints.
6. **Resume** (5 min): Start the application server, monitor logs for 15 minutes.

**Total estimated time: 1 hour (well within 4-hour RTO).**

### Scenario: Accidental data deletion (single table)

1. **Identify** the deleted table and approximate time of deletion.
2. **Restore** the table from the latest backup (Section 3.2).
3. **Verify** row counts and referential integrity.

---

## 5. Backup Script Usage

```bash
# Daily backup (default)
./scripts/backup.sh

# Full backup with S3 upload
./scripts/backup.sh --full --s3-bucket my-backup-bucket

# Custom database URL
./scripts/backup.sh --db-url "postgresql://user:pass@host:5432/dbname"
```

## 6. Restore Script Usage

```bash
# Full restore
./scripts/restore.sh --file backup-20260704.dump --target "postgresql://user:pass@host:5432/dbname"

# Restore with verification
./scripts/restore.sh --file backup-20260704.dump --target "$DATABASE_URL" --verify
```

---

## 7. Critical Tables (Priority Restore Order)

If a partial restore is needed, prioritize in this order:

1. `app_users` — authentication, all users
2. `schools` — school registry
3. `students` — student records
4. `school_classes` — class structure
5. `attendance_records` — daily attendance
6. `fee_records` — financial data
7. `messages` / `message_threads` — communication history
8. `announcements` — school announcements
9. `exam_results` — academic results
10. All remaining tables
