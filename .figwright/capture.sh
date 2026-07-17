#!/bin/bash
# capture.sh — Wait for UI to stabilize, then take a screenshot
# Usage: ./capture.sh <output_path> [max_wait_seconds]
#
# Polls the device for UI stability by comparing consecutive screenshots.
# Only captures once the screen hasn't changed for 2 consecutive polls,
# meaning shimmer/loading animations have stopped and the real UI is rendered.

set -euo pipefail

OUTPUT_PATH="${1:?Usage: capture.sh <output_path> [max_wait_seconds]}"
MAX_WAIT="${2:-15}"
TEMP_DIR="/sdcard/.capture_tmp"

# Ensure temp dir exists
adb shell "mkdir -p $TEMP_DIR" 2>/dev/null || true

echo "[capture] Waiting for UI to stabilize (max ${MAX_WAIT}s)..."

PREVIOUS_HASH=""
STABLE_COUNT=0
ELAPSED=0
POLL_INTERVAL=2  # seconds between polls

while [ "$ELAPSED" -lt "$MAX_WAIT" ]; do
  # Take a temporary screenshot
  adb shell screencap -p "$TEMP_DIR/poll.png" 2>/dev/null

  # Get MD5 hash of the screenshot
  CURRENT_HASH=$(adb shell "md5sum $TEMP_DIR/poll.png" 2>/dev/null | awk '{print $1}')

  if [ -n "$PREVIOUS_HASH" ] && [ "$CURRENT_HASH" = "$PREVIOUS_HASH" ]; then
    STABLE_COUNT=$((STABLE_COUNT + 1))
    echo "[capture] UI stable ($STABLE_COUNT/2)..."

    if [ "$STABLE_COUNT" -ge 2 ]; then
      echo "[capture] UI is stable after ${ELAPSED}s. Capturing final screenshot."
      adb shell screencap -p /sdcard/screen.png
      adb pull /sdcard/screen.png "$OUTPUT_PATH"
      adb shell "rm -rf $TEMP_DIR" 2>/dev/null || true
      echo "[capture] Saved to: $OUTPUT_PATH"
      exit 0
    fi
  else
    STABLE_COUNT=0
    if [ -n "$PREVIOUS_HASH" ]; then
      echo "[capture] UI still changing (shimmer/loading)... retrying"
    else
      echo "[capture] Initial screenshot taken, waiting for stability..."
    fi
  fi

  PREVIOUS_HASH="$CURRENT_HASH"
  sleep "$POLL_INTERVAL"
  ELAPSED=$((ELAPSED + POLL_INTERVAL))
done

# Fallback: if we timed out, take the best screenshot we can
echo "[capture] Max wait reached (${MAX_WAIT}s). Capturing anyway (may have shimmer)."
adb shell screencap -p /sdcard/screen.png
adb pull /sdcard/screen.png "$OUTPUT_PATH"
adb shell "rm -rf $TEMP_DIR" 2>/dev/null || true
echo "[capture] Saved to: $OUTPUT_PATH (WARNING: may contain loading state)"
