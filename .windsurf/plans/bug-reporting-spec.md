# QA Bug Reporting — Specification

## Overview

Cascade acts as a senior QA tester (20+ YOE) + senior Product QA expert, testing the VidyaPrayag Android app via ADB over TCP on a physical device. Reports bugs to Slack with mandatory screenshots. **No code changes or fixes are made** — pure QA reporting only.

## Slack Channels

| Channel ID | Mode | Bug Type |
|---|---|---|
| `C0BH3721V0D` | Manual | All bug types (functional, UI, non-functional) |
| `C0BGTQB5Y4X` | Full Auto | All bug types (UI, functional, non-functional) |
| `C0BHAHSC76G` | — | Reserved for APK uploads only (not used for bug reports) |

## Slack Bot Credentials

- **Bot Token:** `<SLACK_BOT_TOKEN — set via env var SLACK_BOT_TOKEN, never commit>`
- **API:** `chat.postMessage` for text reports, `files.getUploadURLExternal` + `files.completeUploadExternal` for screenshots

## Modes

### Mode 1: Manual
- User tests the app manually on the connected device
- User describes the issue to Cascade
- Cascade takes a screenshot via ADB (`adb shell screencap`)
- Cascade generates a professional bug report from the user's context + its own analysis
- Cascade posts the report to `C0BH3721V0D` (all bug types)
- Screenshot is **mandatory** — attached to every report

### Mode 2: Full Auto God Mode
- Cascade autonomously navigates the app via ADB
- Cascade identifies bugs by analyzing screenshots, UI hierarchy (`adb shell uiautomator dump`), and logcat output
- Cascade tests across all screens, user roles, and edge cases
- Cascade reports all bugs to `C0BGTQB5Y4X`
- Screenshot is **mandatory** — attached to every report
- No user intervention required

### Mode 3: Auto Fix Mode
- Cascade autonomously navigates the app via ADB
- Cascade identifies bugs by analyzing screenshots, UI hierarchy (`adb shell uiautomator dump`), and logcat output
- Bug reports are logged to a local `.md` file (same format as Slack reports, no Slack posting)
- **Batch workflow:** discover 5 bugs → log to `.md` → fix all 5 → build APK → reverify → move on to next 5
- Screenshots captured and referenced in the `.md` file
- No user intervention required until all batches are complete

## Bug Categories

### Functional Bugs
- App crash / ANR
- API error / request failed
- Data not loading / incorrect data
- Feature not working as expected
- Authentication / login issues
- Notification not received
- Navigation broken
- Form submission failures

### UI Bugs
- Layout broken / overlapping elements
- Text overflow / truncation
- Color / theme issues
- Animation glitches
- Responsive / screen size issues
- Missing icons / broken images
- Inconsistent spacing / alignment
- Accessibility issues (contrast, touch target size)

### Non-Functional Bugs
- Performance issues (slow loading, jank)
- Battery drain
- High memory usage
- Network inefficiency
- Security concerns (exposed data, insecure storage)

## Bug Report Format

Every report includes:

| Field | Required | Description |
|---|---|---|
| **Bug ID** | ✅ | Auto-generated: `BUG-{timestamp}-{sequence}` |
| **Title** | ✅ | Concise summary (max 100 chars) |
| **Severity** | ✅ | `Critical` / `High` / `Medium` / `Low` |
| **Category** | ✅ | `Functional` / `UI` / `Non-Functional` |
| **Subcategory** | ✅ | Specific type (e.g. "Layout overlap", "App crash") |
| **Expected Behavior** | ✅ | What should happen |
| **Actual Behavior** | ✅ | What actually happens |
| **Steps to Reproduce** | ✅ | Numbered, reproducible steps |
| **Screen** | ✅ | Current screen/route |
| **Screenshot** | ✅ | MANDATORY — captured via ADB |
| **Device Info** | ✅ | Model, Android version, resolution |
| **App Info** | ✅ | Version, flavor, build type |
| **Timestamp** | ✅ | ISO 8601 UTC |
| **Mode** | ✅ | `Manual` or `Auto` |

## Slack Message Format

```
🐛 *Bug Report — {Category}*  [{Severity}]
• *Bug ID:* BUG-20260714-154522-001
• *Title:* Login button overlaps with forgot password link
• *Severity:* Medium
• *Category:* UI — Layout overlap
• *Expected:* Login button and "Forgot Password?" link should have clear vertical spacing
• *Actual:* Login button bottom edge touches the "Forgot Password?" link with no gap
• *Steps to Reproduce:*
  1. Open app
  2. Navigate to login screen
  3. Observe button and link spacing
• *Screen:* auth/login
• *Device:* Pixel 7, Android 14 (1080x2400)
• *App:* v1.1 staging/debug
• *Mode:* Manual
• *Time:* 2026-07-14T15:45:22Z
📎 Screenshot attached
```

## ADB Commands

| Purpose | Command |
|---|---|
| Screenshot | `adb exec-out screencap -p > /tmp/bug_screenshot.png` |
| UI Hierarchy | `adb shell uiautomator dump /sdcard/ui.xml && adb pull /sdcard/ui.xml /tmp/ui.xml` |
| Logcat (filtered) | `adb logcat -d -t 100 *:E` |
| Current activity | `adb shell dumpsys activity activities \| grep mResumedActivity` |
| Device info | `adb shell getprop ro.product.model` |
| Android version | `adb shell getprop ro.build.version.release` |
| Screen resolution | `adb shell wm size` |
| Tap | `adb shell input tap {x} {y}` |
| Swipe | `adb shell input swipe {x1} {y1} {x2} {y2} {duration_ms}` |
| Input text | `adb shell input text "{text}"` |
| Key events | `adb shell input keyevent { keycode}` |
| Go back | `adb shell input keyevent 4` |
| Go home | `adb shell input keyevent 3` |

## QA Methodology (Full Auto Mode)

### Test Coverage
1. **Onboarding flow** — first launch, permissions, school selection
2. **Auth flows** — login, logout, OTP, password reset
3. **Parent portal** — dashboard, child details, attendance, fees, homework, messages
4. **Teacher portal** — classes, attendance marking, homework, day/week view
5. **Admin portal** — dashboard, link requests, announcements, analytics
6. **Navigation** — bottom nav, back stack, deep links
7. **Error states** — network errors, empty states, loading states
8. **Edge cases** — empty input, special characters, long text, rapid taps
9. **Theme** — light/dark/midnight mode switching
10. **Accessibility** — contrast, touch targets, text size

### Severity Guidelines
- **Critical:** App crash, data loss, security breach, login impossible
- **High:** Core feature broken, incorrect data shown, navigation dead-end
- **Medium:** UI issue affecting usability, non-critical feature partially broken
- **Low:** Cosmetic issue, minor inconsistency, enhancement suggestion

## Rules

1. **Screenshot is MANDATORY** for every bug report — no exceptions
2. **Manual/Auto modes** → no code changes, QA only
3. **Auto Fix mode** → code changes allowed, bugs fixed in batches of 5, rebuild and reverify required
3. **Manual mode** → user describes issue, Cascade reports to `C0BH3721V0D` (all bug types)
4. **Auto mode** → Cascade reports everything to `C0BGTQB5Y4X`
5. **Auto Fix mode** → Cascade logs bugs to local `.md` file, fixes them in batches of 5, rebuilds, and reverifies
6. Reports must be professional, detailed, and immediately actionable by a developer
7. Every report must have Expected vs Actual behavior + Steps to Reproduce
8. Cascade thinks like a senior QA engineer with product expertise — observes edge cases, UX issues, and non-functional concerns that a junior tester would miss
