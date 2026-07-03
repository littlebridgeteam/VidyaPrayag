#!/usr/bin/env bash
# ──────────────────────────────────────────────────────────────────────────────
# E2E Branding Flow Test — ADB-driven automation
#
# Prerequisites:
#   - Android device connected via ADB (adb devices shows a device)
#   - Debug APK installed: ./gradlew :composeApp:assembleDevDebug
#   - Server running locally or on Render (configured in local.properties devBaseUrl)
#
# Usage:
#   chmod +x scripts/test-branding-e2e.sh
#   ./scripts/test-branding-e2e.sh
#
# What it does:
#   1. Launches the app
#   2. Waits for splash → login screen
#   3. Captures screenshot of splash (verify branding colors/logo)
#   4. Navigates to branding settings (admin must be logged in already)
#   5. Tests color picker interaction
#   6. Tests asset upload button visibility
#   7. Captures final screenshot
#   8. Dumps logcat for branding-related logs
# ──────────────────────────────────────────────────────────────────────────────

set -euo pipefail

PKG="com.littlebridge.enrollplus"
SCREENSHOT_DIR="/tmp/branding-e2e-screenshots"
LOGCAT_FILE="/tmp/branding-e2e-logcat.txt"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

log()  { echo -e "${GREEN}[E2E]${NC} $1"; }
warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }
fail() { echo -e "${RED}[FAIL]${NC} $1"; exit 1; }

# ── Pre-flight checks ────────────────────────────────────────────────────────

log "Pre-flight checks..."

if ! command -v adb &> /dev/null; then
    fail "adb not found in PATH. Install Android Platform Tools."
fi

DEVICES=$(adb devices | grep -v "List of devices" | grep "device$" | wc -l | tr -d ' ')
if [ "$DEVICES" -eq 0 ]; then
    fail "No Android device connected. Run 'adb devices' to verify."
fi

if [ "$DEVICES" -gt 1 ]; then
    warn "Multiple devices found. Using first device."
    SERIAL=$(adb devices | grep "device$" | head -1 | awk '{print $1}')
    ADB="adb -s $SERIAL"
else
    SERIAL=""
    ADB="adb"
fi

log "Using device: ${SERIAL:-default}"

# Clean up previous run
rm -rf "$SCREENSHOT_DIR"
mkdir -p "$SCREENSHOT_DIR"
rm -f "$LOGCAT_FILE"

# ── Step 1: Clear logcat & launch app ────────────────────────────────────────

log "Clearing logcat..."
$ADB logcat -c

log "Launching app..."
$ADB shell am force-stop "$PKG"
sleep 1
$ADB shell am start -n "$PKG/com.littlebridge.enrollplus.MainActivity" 2>/dev/null || \
    $ADB shell monkey -p "$PKG" -c android.intent.category.LAUNCHER 1

log "Waiting for app to initialize (5s)..."
sleep 5

# ── Step 2: Capture splash screen ────────────────────────────────────────────

log "Capturing splash screen..."
$ADB exec-out screencap -p > "$SCREENSHOT_DIR/01-splash.png"
log "Saved: $SCREENSHOT_DIR/01-splash.png"

# ── Step 3: Check if branding loaded from cache ──────────────────────────────

log "Checking logcat for branding cache load..."
$ADB logcat -d | grep -i "branding\|BrandingTheme\|loadCached" > "$LOGCAT_FILE" 2>/dev/null || true

BRANDING_LOGS=$(wc -l < "$LOGCAT_FILE" | tr -d ' ')
if [ "$BRANDING_LOGS" -gt 0 ]; then
    log "Found $BRANDING_LOGS branding-related log lines"
else
    warn "No branding logs found (may be normal if no cached branding)"
fi

# ── Step 4: Wait for login screen and capture ────────────────────────────────

log "Waiting for login screen (3s)..."
sleep 3
$ADB exec-out screencap -p > "$SCREENSHOT_DIR/02-login.png"
log "Saved: $SCREENSHOT_DIR/02-login.png"

# ── Step 5: Check if already authenticated (skip login) ──────────────────────

CURRENT_FOCUS=$($ADB shell dumpsys window | grep "mCurrentFocus" | head -1)
log "Current focus: $CURRENT_FOCUS"

# ── Step 6: Try navigating to branding settings ──────────────────────────────
# This assumes the user is already logged in as admin.
# If not logged in, the script will capture the login screen for manual inspection.

log "Attempting to navigate to branding settings..."

# Get screen dimensions
SCREEN_SIZE=$($ADB shell wm size | grep "Physical size" | awk '{print $3}')
SCREEN_W=$(echo "$SCREEN_SIZE" | cut -d 'x' -f1)
SCREEN_H=$(echo "$SCREEN_SIZE" | cut -d 'x' -f2)
log "Screen size: ${SCREEN_W}x${SCREEN_H}"

# Tap center-top for settings gear (approximate)
CENTER_X=$((SCREEN_W / 2))
log "Tapping settings area..."
$ADB shell input tap $CENTER_X 100
sleep 2
$ADB exec-out screencap -p > "$SCREENSHOT_DIR/03-after-settings-tap.png"

# Try to find and tap "Branding Kit" text via uiautomator
log "Looking for 'Branding Kit' text in UI..."
UI_DUMP=$($ADB shell uiautomator dump /dev/tty 2>/dev/null)

if echo "$UI_DUMP" | grep -q "Branding Kit"; then
    log "Found 'Branding Kit' in UI dump"

    # Extract bounds of the Branding Kit element
    BRANDING_BOUNDS=$(echo "$UI_DUMP" | grep -o 'text="Branding Kit"[^>]*bounds="\[[0-9,]*\]\[[0-9,]*\]"' | grep -o 'bounds="\[[0-9,]*\]\[[0-9,]*\]"' | head -1)

    if [ -n "$BRANDING_BOUNDS" ]; then
        # Parse coordinates
        COORDS=$(echo "$BRANDING_BOUNDS" | grep -o '[0-9]*,[0-9]*' | head -1)
        TAP_X=$(echo "$COORDS" | cut -d ',' -f1)
        TAP_Y=$(echo "$COORDS" | cut -d ',' -f2)
        log "Tapping Branding Kit at ($TAP_X, $TAP_Y)..."
        $ADB shell input tap $TAP_X $TAP_Y
        sleep 3
        $ADB exec-out screencap -p > "$SCREENSHOT_DIR/04-branding-settings.png"
        log "Saved: $SCREENSHOT_DIR/04-branding-settings.png"
    else
        warn "Could not extract coordinates for 'Branding Kit'"
    fi
else
    warn "'Branding Kit' text not found in UI. User may not be logged in as admin."
    warn "Log in as admin first, then re-run this script."
    $ADB exec-out screencap -p > "$SCREENSHOT_DIR/04-not-logged-in.png"
fi

# ── Step 7: Verify branding settings screen elements ─────────────────────────

log "Checking branding settings screen elements..."
UI_DUMP2=$($ADB shell uiautomator dump /dev/tty 2>/dev/null)

for ELEMENT in "Brand Colors" "Brand Assets" "Logo" "Custom Subdomain" "Save Colors" "Reset to Defaults"; do
    if echo "$UI_DUMP2" | grep -q "$ELEMENT"; then
        log "  ✓ Found: $ELEMENT"
    else
        warn "  ✗ Not found: $ELEMENT"
    fi
done

# ── Step 8: Test color picker interaction ────────────────────────────────────

log "Testing color picker — tapping a preset color..."
if echo "$UI_DUMP2" | grep -q "Primary Color"; then
    # Tap on "Primary Color" section
    PC_BOUNDS=$(echo "$UI_DUMP2" | grep -o 'text="Primary Color"[^>]*bounds="\[[0-9,]*\]\[[0-9,]*\]"' | grep -o 'bounds="\[[0-9,]*\]\[[0-9,]*\]"' | head -1)
    if [ -n "$PC_BOUNDS" ]; then
        COORDS=$(echo "$PC_BOUNDS" | grep -o '[0-9]*,[0-9]*' | head -1)
        TAP_X=$(echo "$COORDS" | cut -d ',' -f1)
        TAP_Y=$(echo "$COORDS" | cut -d ',' -f2)
        $ADB shell input tap $TAP_X $TAP_Y
        sleep 1
        log "Tapped Primary Color section at ($TAP_X, $TAP_Y)"
    fi
fi

# ── Step 9: Capture final screenshot + full logcat ───────────────────────────

log "Capturing final screenshot..."
$ADB exec-out screencap -p > "$SCREENSHOT_DIR/05-final.png"

log "Dumping full logcat for branding analysis..."
$ADB logcat -d | grep -i "branding\|BrandingTheme\|BrandingColorMapper\|uploadAsset\|deleteAsset\|loadCached\|setCachedBranding" > "$LOGCAT_FILE" 2>/dev/null || true

log "Logcat saved: $LOGCAT_FILE ($(wc -l < "$LOGCAT_FILE" | tr -d ' ') lines)"

# ── Step 10: Summary ─────────────────────────────────────────────────────────

echo ""
echo "═══════════════════════════════════════════════════════════════"
echo "  E2E Branding Test Summary"
echo "═══════════════════════════════════════════════════════════════"
echo ""
echo "  Screenshots saved to: $SCREENSHOT_DIR"
ls -la "$SCREENSHOT_DIR" 2>/dev/null | grep ".png" | awk '{print "    " $NF}'
echo ""
echo "  Logcat file: $LOGCAT_FILE"
echo "  Branding log lines: $(wc -l < "$LOGCAT_FILE" | tr -d ' ')"
echo ""
echo "  To view screenshots:"
echo "    open $SCREENSHOT_DIR"
echo ""
echo "  To inspect branding logs:"
echo "    cat $LOGCAT_FILE"
echo ""

log "E2E test complete. Review screenshots and logs for branding verification."
