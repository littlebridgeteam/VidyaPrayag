#!/usr/bin/env bash
set -euo pipefail

# VidyaPrayag Database Restore Script
# Usage: ./scripts/restore.sh --file FILE --target URL [--verify]
#
# Restores a custom-format pg_dump backup to the target database.
# --file: path to the .dump backup file (required)
# --target: target database URL (required)
# --verify: run post-restore integrity checks

BACKUP_FILE=""
TARGET_URL=""
VERIFY=false

while [[ $# -gt 0 ]]; do
  case "$1" in
    --file) BACKUP_FILE="$2"; shift 2 ;;
    --target) TARGET_URL="$2"; shift 2 ;;
    --verify) VERIFY=true; shift ;;
    *) echo "Unknown option: $1"; exit 1 ;;
  esac
done

if [[ -z "$BACKUP_FILE" ]]; then
  echo "ERROR: --file is required."
  exit 1
fi

if [[ -z "$TARGET_URL" ]]; then
  echo "ERROR: --target is required."
  exit 1
fi

if [[ ! -f "$BACKUP_FILE" ]]; then
  echo "ERROR: Backup file not found: ${BACKUP_FILE}"
  exit 1
fi

echo "[$(date -u +%Y-%m-%dT%H:%M:%SZ)] Starting restore..."
echo "  Source: ${BACKUP_FILE}"
echo "  Target: ${TARGET_URL}"

# Validate the backup file first
if ! pg_restore --validate "$BACKUP_FILE" 2>/dev/null; then
  echo "[$(date -u +%Y-%m-%dT%H:%M:%SZ)] ERROR: Backup file validation failed — aborting restore."
  exit 2
fi
echo "[$(date -u +%Y-%m-%dT%H:%M:%SZ)] Backup file validated."

# Drop and recreate the target database
echo "[$(date -u +%Y-%m-%dT%H:%M:%SZ)] Dropping and recreating target database..."
dropdb --if-exists "$TARGET_URL"
createdb "$TARGET_URL"

# Restore
echo "[$(date -u +%Y-%m-%dT%H:%M:%SZ)] Restoring from backup..."
pg_restore --dbname="$TARGET_URL" --format=custom --clean --if-exists --no-owner --no-privileges "$BACKUP_FILE"

echo "[$(date -u +%Y-%m-%dT%H:%M:%SZ)] Restore complete."

# Verification
if [[ "$VERIFY" == "true" ]]; then
  echo "[$(date -u +%Y-%m-%dT%H:%M:%SZ)] Running verification checks..."

  TABLE_COUNT=$(psql "$TARGET_URL" -t -c "SELECT count(*) FROM information_schema.tables WHERE table_schema='public';" | tr -d '[:space:]')
  echo "  Table count: ${TABLE_COUNT}"

  if [[ "$TABLE_COUNT" -lt 50 ]]; then
    echo "[$(date -u +%Y-%m-%dT%H:%M:%SZ)] WARNING: Table count (${TABLE_COUNT}) seems low — verify schema completeness."
  fi

  USER_COUNT=$(psql "$TARGET_URL" -t -c "SELECT count(*) FROM app_users;" 2>/dev/null | tr -d '[:space:]' || echo "0")
  echo "  app_users rows: ${USER_COUNT}"

  SCHOOL_COUNT=$(psql "$TARGET_URL" -t -c "SELECT count(*) FROM schools;" 2>/dev/null | tr -d '[:space:]' || echo "0")
  echo "  schools rows: ${SCHOOL_COUNT}"

  FLYWAY_CHECK=$(psql "$TARGET_URL" -t -c "SELECT count(*) FROM flyway_schema_history;" 2>/dev/null | tr -d '[:space:]' || echo "0")
  echo "  flyway_schema_history rows: ${FLYWAY_CHECK}"

  if [[ "$FLYWAY_CHECK" -eq 0 ]]; then
    echo "[$(date -u +%Y-%m-%dT%H:%M:%SZ)] WARNING: flyway_schema_history is empty — migrations may need to be re-run."
  fi

  echo "[$(date -u +%Y-%m-%dT%H:%M:%SZ)] Verification complete."
fi

echo "[$(date -u +%Y-%m-%dT%H:%M:%SZ)] Restore process complete."
