#!/usr/bin/env bash
set -euo pipefail

# VidyaPrayag Database Backup Script
# Usage: ./scripts/backup.sh [--full] [--s3-bucket BUCKET] [--db-url URL]
#
# Creates a custom-format pg_dump backup with timestamp.
# --full: marks as weekly full backup (longer retention)
# --s3-bucket: uploads backup to S3 after local save
# --db-url: override DATABASE_URL for the backup

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKUP_DIR="${BACKUP_DIR:-/backups}"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_TYPE="daily"
S3_BUCKET=""
DB_URL="${DATABASE_URL:-}"

while [[ $# -gt 0 ]]; do
  case "$1" in
    --full) BACKUP_TYPE="full"; shift ;;
    --s3-bucket) S3_BUCKET="$2"; shift 2 ;;
    --db-url) DB_URL="$2"; shift 2 ;;
    *) echo "Unknown option: $1"; exit 1 ;;
  esac
done

if [[ -z "$DB_URL" ]]; then
  echo "ERROR: DATABASE_URL is not set. Pass --db-url or set DATABASE_URL env var."
  exit 1
fi

mkdir -p "$BACKUP_DIR"

BACKUP_FILE="${BACKUP_DIR}/backup-${BACKUP_TYPE}-${TIMESTAMP}.dump"

echo "[$(date -u +%Y-%m-%dT%H:%M:%SZ)] Starting ${BACKUP_TYPE} backup..."
echo "  Target: ${BACKUP_FILE}"

pg_dump "$DB_URL" --format=custom --no-owner --no-privileges --file="$BACKUP_FILE"

FILE_SIZE=$(du -h "$BACKUP_FILE" | cut -f1)
echo "[$(date -u +%Y-%m-%dT%H:%M:%SZ)] Backup complete: ${BACKUP_FILE} (${FILE_SIZE})"

# Validate the backup
if ! pg_restore --validate "$BACKUP_FILE" 2>/dev/null; then
  echo "[$(date -u +%Y-%m-%dT%H:%M:%SZ)] WARNING: Backup validation failed!"
  exit 2
fi
echo "[$(date -u +%Y-%m-%dT%H:%M:%SZ)] Backup validated successfully."

# Upload to S3 if requested
if [[ -n "$S3_BUCKET" ]]; then
  echo "[$(date -u +%Y-%m-%dT%H:%M:%SZ)] Uploading to S3: s3://${S3_BUCKET}/$(basename "$BACKUP_FILE")"
  if command -v aws &>/dev/null; then
    aws s3 cp "$BACKUP_FILE" "s3://${S3_BUCKET}/$(basename "$BACKUP_FILE")" --storage-class STANDARD_IA
    echo "[$(date -u +%Y-%m-%dT%H:%M:%SZ)] S3 upload complete."
  else
    echo "[$(date -u +%Y-%m-%dT%H:%M:%SZ)] WARNING: aws CLI not found — skipping S3 upload."
  fi
fi

# Retention policy
if [[ "$BACKUP_TYPE" == "daily" ]]; then
  find "$BACKUP_DIR" -name "backup-daily-*.dump" -mtime +7 -delete
  echo "[$(date -u +%Y-%m-%dT%H:%M:%SZ)] Cleaned daily backups older than 7 days."
elif [[ "$BACKUP_TYPE" == "full" ]]; then
  find "$BACKUP_DIR" -name "backup-full-*.dump" -mtime +28 -delete
  echo "[$(date -u +%Y-%m-%dT%H:%M:%SZ)] Cleaned full backups older than 28 days."
fi

echo "[$(date -u +%Y-%m-%dT%H:%M:%SZ)] Backup process complete."
