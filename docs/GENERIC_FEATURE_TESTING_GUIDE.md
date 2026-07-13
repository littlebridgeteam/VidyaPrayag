# Generic Feature Testing Guide

A repeatable checklist for testing any feature in Vidya Prayag end-to-end.
Use this for every new feature or bug fix to ensure consistent verification.

---

## 0. Prerequisites

| Item | Requirement |
|------|-------------|
| Server | Running locally on `http://localhost:8080` (or `http://192.168.x.x:8080` for device tests) |
| Device | Android device connected via ADB (`adb devices` should list it) |
| Build | DevDebug APK installed (`./gradlew :composeApp:assembleDevDebug` then install) |
| Test Credentials | School admin: `a1@gmail.com` / `12345678` · Parent phone: `9535248581` |
| DB | Migrations applied, Supabase reachable |

---

## 1. Server Compilation

```bash
cd /path/to/Vidya\ Prayag
./gradlew :server:compileKotlin
```

Must exit with `BUILD SUCCESSFUL`. Fix any compile errors before proceeding.

---

## 2. Start / Restart Server

```bash
# Kill existing server if running
lsof -ti :8080 | xargs kill

# Start fresh
./gradlew :server:run
```

Wait for `Responding at http://0.0.0.0:8080` in logs.

---

## 3. API-Level Tests (Fastest — Do This First)

### 3.1 Login & Get Token

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"identifier":"a1@gmail.com","role":"school_admin","password":"12345678"}' \
  | python3 -c "import sys,json; print(json.load(sys.stdin)['data']['token'])")
echo "Token: ${TOKEN:0:20}..."
```

### 3.2 Test Feature Endpoints

For each endpoint in the feature:

```bash
# GET (read)
curl -s http://localhost:8080/api/v1/<endpoint> \
  -H "Authorization: Bearer $TOKEN" | python3 -m json.tool

# POST/PATCH/PUT (write)
curl -s -X PATCH http://localhost:8080/api/v1/<endpoint> \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"field":"value"}' | python3 -m json.tool
```

### 3.3 Verify Cross-Table Sync

If the feature writes to one table but another table should reflect the change
(e.g. branding → school profile), verify both reads after mutation:

```bash
# After updating feature A
curl -s http://localhost:8080/api/v1/feature-a -H "Authorization: Bearer $TOKEN" | python3 -m json.tool
curl -s http://localhost:8080/api/v1/feature-b -H "Authorization: Bearer $TOKEN" | python3 -m json.tool
```

### 3.4 Reset State

After testing, restore the original state so subsequent tests are not affected.

---

## 4. Server Integration Tests

```bash
./gradlew :server:test --tests "com.littlebridge.enrollplus.feature.<feature>.*"
```

Run feature-specific tests only for speed. Run full suite before releases:

```bash
./gradlew :server:test
```

---

## 5. Compose UI Tests (Instrumented)

```bash
# Ensure device is connected
adb devices

# Run UI tests
./gradlew :composeApp:connectedDevDebugAndroidTest \
  --tests "com.littlebridge.enrollplus.ui.v2.screens.<feature>.*"
```

---

## 6. E2E Device Test (ADB Automation)

### 6.1 Connect Device

```bash
adb connect 192.168.x.x:5555   # or USB
adb devices
```

### 6.2 Automated Login via ADB

```bash
# Launch app
adb shell am start -n com.littlebridge.enrollplus.dev.debug/com.littlebridge.enrollplus.MainActivity

# Wait for splash
sleep 3

# Dump UI to find elements
adb shell uiautomator dump /sdcard/ui.xml && adb shell cat /sdcard/ui.xml

# Tap email field, type credentials
adb shell input tap <x> <y>          # email field coordinates from UI dump
adb shell input text "a1@gmail.com"
adb shell input tap <x> <y>          # password field
adb shell input text "12345678"
adb shell input tap <x> <y>          # login button
sleep 3
```

### 6.3 Navigate to Feature Screen

Use `uiautomator dump` to find element coordinates, then tap through navigation:

```bash
adb shell uiautomator dump /sdcard/ui.xml && adb shell cat /sdcard/ui.xml
# Find the target menu item / button bounds, extract center coordinates
adb shell input tap <x> <y>
```

### 6.4 Verify UI Elements

```bash
adb shell uiautomator dump /sdcard/ui.xml
adb shell cat /sdcard/ui.xml | grep -o 'text="[^"]*"'
```

Check that expected text labels, images, and colors are present.

### 6.5 Verify Visual Changes

If the feature changes visual elements (logo, colors, theme):

```bash
# Take screenshot before
adb shell screencap -p /sdcard/before.png && adb pull /sdcard/before.png

# Perform the action via ADB taps

# Take screenshot after
adb shell screencap -p /sdcard/after.png && adb pull /sdcard/after.png

# Compare manually or via image diff
```

---

## 7. E2E Script Template

Create a script at `scripts/test-<feature>-e2e.sh`:

```bash
#!/bin/bash
set -euo pipefail

DEVICE="${1:-}"
if [ -z "$DEVICE" ]; then
  echo "Usage: $0 <device_ip:port>"
  exit 1
fi

adb connect "$DEVICE"
adb shell am start -n com.littlebridge.enrollplus.dev.debug/com.littlebridge.enrollplus.MainActivity
sleep 3

# Helper: tap element by text
tap_by_text() {
  local text="$1"
  adb shell uiautomator dump /sdcard/ui.xml >/dev/null 2>&1
  local bounds=$(adb shell cat /sdcard/ui.xml | \
    grep -o "text=\"$text\"[^>]*bounds=\"\[[0-9,]*\]\[[0-9,]*\]\"" | \
    grep -o '\[[0-9,]*\]\[[0-9,]*\]')
  # Parse bounds and compute center
  local coords=$(echo "$bounds" | grep -o '[0-9]*' | head -4)
  local x1=$(echo "$coords" | sed -n 1p) y1=$(echo "$coords" | sed -n 2p)
  local x2=$(echo "$coords" | sed -n 3p) y2=$(echo "$coords" | sed -n 4p)
  local cx=$(( (x1 + x2) / 2 )) cy=$(( (y1 + y2) / 2 ))
  adb shell input tap $cx $cy
}

# Login
tap_by_text "Email"
adb shell input text "a1@gmail.com"
# ... rest of flow

echo "E2E test complete"
```

---

## 8. Checklist Summary

- [ ] Server compiles (`./gradlew :server:compileKotlin`)
- [ ] Server starts and responds on :8080
- [ ] API tests pass (login, CRUD, cross-table sync)
- [ ] Server integration tests pass (`./gradlew :server:test --tests "..."`)
- [ ] Compose UI tests pass (`./gradlew :composeApp:connectedDevDebugAndroidTest`)
- [ ] E2E device test passes (login, navigate, verify UI)
- [ ] Visual changes verified via screenshot comparison
- [ ] Test state restored to defaults

---

## 9. Tips for Speed

- **API tests first**: They are 10x faster than device tests. Always verify API behavior before device testing.
- **Use `python3 -m json.tool`** to pretty-print curl responses for quick visual verification.
- **Filter tests**: Use `--tests` flag to run only relevant tests instead of the full suite.
- **Keep server running**: Don't restart between API tests unless code changes require it.
- **ADB UI dumps**: Use `uiautomator dump` + `grep` to find elements quickly instead of guessing coordinates.
