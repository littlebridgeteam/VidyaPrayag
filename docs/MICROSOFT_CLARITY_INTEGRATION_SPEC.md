# Microsoft Clarity Integration Spec

**Project ID:** `xly829jv3t`  
**Platform:** Android (KMP — `composeApp` module, `androidMain` source set)  
**Date:** 2026-07-14

---

## Overview

Microsoft Clarity is a free analytics tool that provides session recordings, heatmaps, and user behavior insights. This spec documents step-by-step integration into the Vidya Prayag Android app.

Clarity is an **Android-only SDK** — it cannot be used on iOS, Web, or Desktop targets. All code changes are isolated to the `androidMain` source set and Android-specific Gradle configuration.

---

## Prerequisites

- **minSdk:** 24 (already satisfied — see `gradle/libs.versions.toml`)
- **Repository:** `mavenCentral()` is already declared in `settings.gradle.kts` → `dependencyResolutionManagement.repositories` — **no change needed**
- **Project ID:** `xly829jv3t`

---

## Step 1 — Add Clarity version to the version catalog

**File:** `gradle/libs.versions.toml`

Add to the `[versions]` section:

```toml
clarity = "3.8.2"
```

Add to the `[libraries]` section:

```toml
clarity-compose = { module = "com.microsoft.clarity:clarity-compose", version.ref = "clarity" }
```

> **Why `clarity-compose`?** The app is 100% Jetpack Compose (Compose Multiplatform). The `clarity-compose` artifact includes Compose-aware lifecycle hooks on top of the core SDK. Use the non-Compose variant (`com.microsoft.clarity:clarity`) only if you have legacy View-based screens — we don't.
>
> **Pinning:** `3.+` resolves to the latest 3.x release at build time. For reproducible CI builds, consider pinning to an explicit version (e.g. `3.0.3`). Check [Maven Central](https://repo1.maven.org/maven2/com/microsoft/clarity/clarity-compose/) for the latest version.

---

## Step 2 — Add the dependency to `composeApp/build.gradle.kts`

**File:** `composeApp/build.gradle.kts`

In the `androidMain.dependencies { }` block (around line 92), add:

```kotlin
androidMain.dependencies {
    // ... existing dependencies ...
    // Microsoft Clarity — session recordings & heatmaps (Android-only)
    implementation(libs.clarity.compose)
}
```

This ensures Clarity is only compiled into the Android APK, not into iOS/Web/Desktop artifacts.

---

## Step 3 — Initialize Clarity in `MainActivity.onCreate()`

**File:** `composeApp/src/androidMain/kotlin/com/littlebridge/enrollplus/MainActivity.kt`

Add the imports at the top of the file:

```kotlin
import com.microsoft.clarity.Clarity
import com.microsoft.clarity.ClarityConfig
import com.microsoft.clarity.models.LogLevel
```

In `onCreate()`, add the Clarity initialization **after** `super.onCreate()` and **before** `setContent {}`:

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // ... existing splash screen + edge-to-edge setup ...

    // Microsoft Clarity — session recordings & heatmaps
    val clarityConfig = ClarityConfig(
        projectId = "xly829jv3t",
        logLevel = LogLevel.None  // Use LogLevel.Verbose while testing
    )
    Clarity.initialize(applicationContext, clarityConfig)

    // ... existing deep-link extraction + setContent { App(...) } ...
}
```

### Placement detail

The initialization should go **after** the `splashScreen.setOnExitAnimationListener { ... }` block and **before** the `deepLink.value = extractDeepLink(intent)` line. This ensures:

1. The splash screen is configured first (no visual delay).
2. Clarity is initialized before any Compose content renders, so it captures the full session from frame one.
3. Deep-link processing and `setContent` happen after Clarity is ready.

---

## Step 4 — (Optional) Add custom user identification

If you want to correlate Clarity sessions with authenticated users, set a custom user ID after login. This can be done from the `MainViewModel` when the auth state resolves, or from any Android-specific interop.

**Option A — In `MainActivity`, observe auth state:**

```kotlin
// After Clarity.initialize(...)
// Optionally set user ID once authenticated
// This requires bridging from MainViewModel's authState to Android-specific code
```

**Option B — Via a Clarity helper in `androidMain`:**

Create `composeApp/src/androidMain/kotlin/com/littlebridge/enrollplus/ClarityHelper.kt`:

```kotlin
package com.littlebridge.enrollplus

import com.microsoft.clarity.Clarity

object ClarityHelper {
    fun setUserId(userId: String?) {
        if (userId != null) {
            Clarity.setUserCustomId(userId)
        } else {
            Clarity.clearUserCustomId()
        }
    }
}
```

Then call `ClarityHelper.setUserId(...)` from the Android side when the user logs in or out. This requires an `expect/actual` or platform-interface bridge from `commonMain` — add only if user-level session correlation is needed.

---

## Step 5 — (Optional) Add custom tags / events

Clarity supports custom tags for filtering sessions in the dashboard:

```kotlin
// Tag a session with the user's role
Clarity.addCustomTag("role", "admin")  // or "teacher", "parent"

// Tag a session with the active portal
Clarity.addCustomTag("portal", "school")
```

These can be called at any point after initialization. Consider adding role-based tags when `NavGraphV2` resolves the `EntryRole`.

---

## Step 6 — Verify the build

Run the Android debug build:

```bash
./gradlew :composeApp:assembleDevDebug
```

> **Note:** The project uses product flavors (`dev`, `staging`, `prod`). Use `assembleDevDebug` for local development.

If the build succeeds with no unresolved-reference errors for `Clarity` / `ClarityConfig`, the dependency is correctly wired.

---

## Step 7 — Run the app and verify in Clarity dashboard

1. Install the debug APK on a device or emulator.
2. Navigate through a few screens.
3. Go to [clarity.microsoft.com](https://clarity.microsoft.com) → Project `xly829jv3t`.
4. **Note:** It can take up to **2 hours** for data to appear.
5. While testing, set `logLevel = LogLevel.Verbose` in `ClarityConfig` and check `logcat` for Clarity initialization logs. Filter logcat by `Clarity` tag.

---

## Step 8 — (Recommended) Gate Clarity behind dev/staging only

To avoid collecting analytics from production until you're ready, use a `BuildConfig` flag:

**In `composeApp/build.gradle.kts`**, inside each flavor block:

```kotlin
create("dev") {
    dimension = "environment"
    buildConfigField("String", "AUTH_BASE_URL", "\"$devBaseUrl\"")
    buildConfigField("String", "SCHOOL_BASE_URL", "\"$devBaseUrl\"")
    buildConfigField("boolean", "CLARITY_ENABLED", "true")
}
create("staging") {
    dimension = "environment"
    buildConfigField("String", "AUTH_BASE_URL", "\"https://vidyaprayag-1.onrender.com\"")
    buildConfigField("String", "SCHOOL_BASE_URL", "\"https://vidyaprayag-1.onrender.com\"")
    buildConfigField("boolean", "CLARITY_ENABLED", "true")
}
create("prod") {
    dimension = "environment"
    buildConfigField("String", "AUTH_BASE_URL", "\"https://vidyaprayag-1.onrender.com\"")
    buildConfigField("String", "SCHOOL_BASE_URL", "\"https://vidyaprayag-1.onrender.com\"")
    buildConfigField("boolean", "CLARITY_ENABLED", "false")  // Flip to true when ready for prod
}
```

**In `MainActivity.onCreate()`**, wrap the initialization:

```kotlin
if (BuildConfig.CLARITY_ENABLED) {
    val clarityConfig = ClarityConfig(
        projectId = "xly829jv3t",
        logLevel = LogLevel.None
    )
    Clarity.initialize(applicationContext, clarityConfig)
}
```

---

## Files changed summary

| File | Change |
|---|---|
| `gradle/libs.versions.toml` | Add `clarity` version + `clarity-compose` library entry |
| `composeApp/build.gradle.kts` | Add `implementation(libs.clarity.compose)` in `androidMain.dependencies` |
| `composeApp/src/androidMain/.../MainActivity.kt` | Add imports + `Clarity.initialize()` call in `onCreate()` |
| `composeApp/build.gradle.kts` (optional) | Add `CLARITY_ENABLED` BuildConfig field per flavor |

---

## Notes & caveats

- **KMP isolation:** Clarity is Android-only. All code lives in `androidMain` — iOS/Web/Desktop targets are unaffected.
- **No iOS equivalent:** Microsoft does not provide a Clarity SDK for iOS. If iOS analytics are needed in the future, consider a web-based Clarity snippet embedded in a WKWebView, or an alternative like Mixpanel.
- **Privacy:** Review Clarity's [privacy policy](https://clarity.microsoft.com/privacy). If GDPR/consent applies, gate initialization behind a user consent flag.
- **Session data latency:** Up to 2 hours for data to appear in the dashboard after first run.
- **Min SDK:** Clarity requires API 21+; our `minSdk` is 24 — no conflict.
