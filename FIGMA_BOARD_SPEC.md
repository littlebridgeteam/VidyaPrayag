# Vidya Prayag — Figma Board Spec

> **One workflow:** Screenshot from ADB(physical runnign phone) → Code → Build in Figma → Validate → Archive.
> The codebase is the source of truth. Every value comes from source code.

---

## Figma File Pages

| # | Page | Purpose |
|---|------|---------|
| 00 | Cover | App name, version, reference device info |
| 01 | Design System | Colors, typography, spacing, radii, elevation, motion |
| 02 | Foundations | Brand assets, logos, fonts, icon library |
| 03 | Components | Reusable UI components as Figma components/variants |
| 04 | Icons | Full icon set |
| 05 | Authentication | Splash, Landing, Login, Signup, Onboarding, Link Child |
| 06 | Parent Portal | All parent screens |
| 07 | Teacher Portal | All teacher screens |
| 08 | Admin Portal | All admin screens |
| 09 | Discovery | Discovery + Academic Calendar |
| 10 | Tutor | Tutor screens |
| 11 | Library | Library components |
| 12 | Notifications | Notifications + preferences |
| 13 | Common | Skeletons, dialogs, bottom sheets, empty/error states |

---

## Design System (Page 01)

### Colors — Light Theme
Source: `composeApp/.../ui/tokens/VColors.kt`

**Brand:** Teal `#3CB9A9`, Teal-Deep `#006A60`, Navy `#26234D`, Navy-Deep `#1A1838`, Lavender `#FCF8FF`, Lavender-Light `#EAE6FA`, Cream `#F5F5F3`, Warm-Orange `#9E421A`

**Accent (Violet):** Primary `#6C5CE0`, Soft `#8B7EE8`, Deep `#544AB8`, Tint `#F4F3FA`

**Ink:** Primary `#1A2422`, Secondary `#3D4947`, Tertiary `#6D7A77`, Placeholder `#BCC9C6`

**Surfaces:** Background `#FCF8FF`, Card `#FFFFFF`, Border-1 `rgba(8,8,8,0.06)`, Border-2 `rgba(8,8,8,0.10)`, Hairline `rgba(38,35,77,0.06)`, Shadow-Tint `#26234D`

**Semantic:** Success `#A8E6CF` / Ink `#1F7A4D`, Warning `#FFD4A3` / Ink `#B3651A`, Danger `#FFADA8` / Ink `#B3261E`

### Colors — Dark Theme
Bg `#050505`, Card `#0E0E10`, Tinted `#141416`, Accent `#8B7EE8`, Ink `#F4F4F6`, Ink2 `#B9BCC4`, Ink3 `#7A7E89`. Shadows suppressed.

### Typography
Source: `composeApp/.../ui/v2/theme/VType.kt`. Fonts in `composeResources/font/`

| Style | Font | Size | Weight | Line | Tracking |
|------|------|------|--------|------|----------|
| H1-Display | Plus Jakarta Sans | 32 | 800 | 35.2 | -0.02em |
| H2-Heading | Plus Jakarta Sans | 22 | 700 | 26.4 | -0.01em |
| H3-Subheading | Plus Jakarta Sans | 17 | 700 | 22 | -0.3sp |
| H4-LabelStrong | Plus Jakarta Sans | 14 | 600 | 20 | 0 |
| Body | Plus Jakarta Sans | 14 | 400 | 21 | 0 |
| Body-Strong | Plus Jakarta Sans | 14 | 600 | 21 | 0 |
| Caption | Plus Jakarta Sans | 12 | 500 | 17 | 0 |
| Label | Plus Jakarta Sans | 11 | 600 | 14 | 0.08em UPPER |
| Label-Strong | Plus Jakarta Sans | 11 | 700 | 14 | 0.10em UPPER |
| Input-Label | Plus Jakarta Sans | 12 | 600 | 16 | 0 |
| Data | DM Mono | 15 | 400 | 21 | tnum |
| Data-Sm | DM Mono | 13 | 400 | 17 | tnum |
| Data-Lg | DM Mono | 22 | 500 | 26 | tnum |

Legacy font: Inter (splash/landing only).

### Spacing (Base-4)
Source: `composeApp/.../ui/v2/theme/VDimens.kt`. XS=4, SM=8, MD=16, LG=24, XL=32, 2XL=48, 3XL=64, ScreenPadding=16 (all dp).

### Border Radii
SM=6, MD=10, Input=12, LG=14, Card=16, XL=20, Sheet=32, Pill=999 (all dp).

### Elevation
Navy-tinted (`#26234D`), 3 tiers: Card (dy=2, spread=4, alpha=0.06), Raised (dy=8, spread=24, alpha=0.09), Modal (dy=16, spread=40, alpha=0.15). Suppressed in Dark theme.

### Motion
Springs: Soft(240,22), Sheet(220,28), Card(260,30), Snappy(300,20). Transitions: Forward-Slide (280ms, ±30px H), Modal-Rise (280ms, +30px V), Quiet-Fade (280ms).

---

## Screen Capture → Figma Workflow

### Folders

```
/tmp/vp_screenshots/     ← fresh captures land here (one PNG per screen-state-theme)
/tmp/vp_completedSS/     ← validated screenshots archived here (same filename kept)
```

### Screenshot Naming Convention

Every screenshot gets a **unique, traceable name** at capture time, following the same pattern as the Figma frame:

```
{ScreenName}_{State}_{Theme}.png

Examples:
ParentHomeScreenV2_Success_Light.png
ParentHomeScreenV2_Loading_Light.png
TeacherMarksScreenV2_Empty_Light.png
SchoolPeopleScreenV2_Error_Dark.png
```

This name is the **canonical key** — it is used for:
1. The ADB screenshot filename on disk
2. The Figma frame name (Step 3)
3. The Figma export filename (Step 4 validation)
4. The archived filename in `/tmp/vp_completedSS/` (Step 5)

Keeping the same name across all steps ensures the original screenshot and the Figma export can always be paired for validation, even after archival.

### Modes

**Auto mode** — AI navigates the device via ADB, captures each screen, builds Figma, validates, and moves to the next screen automatically.

**Manual mode** — User navigates the device. When user says "capture" (or "screenshot" / "take it"),AI captures the screenshot and run the workflow, AI runs the workflow.

---

### Step-by-Step Process

#### Step 1 — Capture Screenshot

Determine the `{ScreenName}_{State}_{Theme}` key **before** capturing. If the screen is not yet identified, use a temporary placeholder name and rename after Step 2 identification.

```bash
# Set the capture key (example)
CAPTURE_KEY="ParentHomeScreenV2_Success_Light"

# Capture from device with the unique name
adb shell screencap -p /sdcard/vp_capture.png
adb pull /sdcard/vp_capture.png "/tmp/vp_screenshots/${CAPTURE_KEY}.png"

# Clean up device-side temp
adb shell rm -f /sdcard/vp_capture.png
```

**Do NOT** wipe `/tmp/vp_screenshots/` between captures — each screenshot has a unique name and multiple may coexist during a session. Only remove a file when it is archived in Step 5.

#### Step 2 — Find the Screen Code

1. Read the screenshot image to identify the screen visually.
2. Search the codebase for the matching Composable:
   - Parent screens: `composeApp/.../ui/v2/screens/parent/`
   - Teacher screens: `composeApp/.../ui/v2/screens/teacher/`
   - Admin screens: `composeApp/.../ui/v2/screens/school/`
   - Auth screens: `composeApp/.../ui/v2/screens/auth/`
   - Shared components: `composeApp/.../ui/v2/components/`
3. Read the relevant `.kt` file(s) to extract exact values: colors, dimensions, padding, fonts, icons, layout structure.

#### Step 3 — Build in Figma

Using figwright MCP tools, create a Figma frame that matches the screenshot exactly:

1. Create a frame sized to the device's dp viewport (NOT fixed 440×960 — measure actual device).
2. Apply the portal background color from `VColors`.
3. Build the UI top-to-bottom using Auto Layout with exact `VDimens` spacing/radii values.
4. Use exact `VColors` hex values for every fill — no eyeballing.
5. Use exact `VType` typography values (font family, size, weight, line-height, tracking).
6. Import icons as SVG vectors matching the Compose `ImageVector` paths.
7. Place the frame on the correct Figma page (Parent→06, Teacher→07, Admin→08, etc.).

#### Step 4 — Validate

1. Export the Figma frame as PNG via `mcp1_save_screenshots` to `/tmp/vp_screenshots/` using the **same `{ScreenName}_{State}_{Theme}` name** with a `_figma` suffix to distinguish it:
   - Original screenshot: `/tmp/vp_screenshots/{ScreenName}_{State}_{Theme}.png`
   - Figma export: `/tmp/vp_screenshots/{ScreenName}_{State}_{Theme}_figma.png`
2. Read both images.
3. Compare visually — check:
   - Colors match exactly
   - Text font/size/weight matches
   - Spacing and padding match
   - Corner radii match
   - Icon sizes and positions match
   - Shadow direction and tint match
4. If mismatched → fix the Figma frame and re-validate. Max 3 fix iterations.
5. If still mismatched after 3 iterations → flag for manual review.

#### Step 5 — Archive Screenshot

Archive **both** the original screenshot and the Figma export (if validation passed) so the pair can be re-validated later if needed.

```bash
CAPTURE_KEY="ParentHomeScreenV2_Success_Light"

# Move original screenshot to completed folder (keeps same name)
mv "/tmp/vp_screenshots/${CAPTURE_KEY}.png" "/tmp/vp_completedSS/${CAPTURE_KEY}.png"

# Optionally archive the Figma export alongside it for future reference
mv "/tmp/vp_screenshots/${CAPTURE_KEY}_figma.png" "/tmp/vp_completedSS/${CAPTURE_KEY}_figma.png"
```

#### Step 6 — Next Screen

**Auto mode:** AI navigates to the next screen via ADB:
```bash
adb shell input tap {x} {y}        # tap a UI element
adb shell input swipe {x1} {y1} {x2} {y2} {duration_ms}  # swipe
adb shell input keyevent 4         # back
```
Wait 500ms for animations to settle, then repeat from Step 1.

**Manual mode:** Wait for user to say "capture" again.

---

### Frame Naming

The Figma frame uses the **same `{ScreenName}_{State}_{Theme}` key** as the screenshot (see [Screenshot Naming Convention](#screenshot-naming-convention) above). This 1:1 mapping is what makes post-Figma validation possible.

**States:** `Success`, `Loading`, `Empty`, `Error`, `Offline`, `Dialog`, `Keyboard`, `ScrolledTop`, `ScrolledBottom`

**Themes:** `Light`, `Dark`, `Midnight`, `Warm`, `HighContrast`

---

### Portal Tab Configuration

| Portal | Tabs | Active Color | Dock File |
|--------|------|-------------|-----------|
| Parent | Home, Academics, Fees, Conversations, Profile | accentDeep `#544AB8` | `ParentDock.kt` |
| Teacher | Home, Classes, Messages, Profile | accentDeep `#544AB8` | `TeacherDock.kt` |
| Admin | Home, People, Records, Comms, Settings | tealDeep `#006A60` | `VBottomNav2` |

---

### Common Mismatch Sources

| Symptom | Fix |
|---------|-----|
| Text size wrong | Upload actual `.ttf` files from `composeResources/font/` to Figma as shared fonts |
| Colors slightly off | Use exact `VColors` hex — never eyeball |
| Spacing wrong | Use Auto Layout with `VDimens` spacing values (4/8/16/24/32dp) |
| Shadow wrong | Navy-tinted `#26234D`, match `VElevation` values |
| Corner radius wrong | Cards=16, Inputs=12, Buttons=10, Sheets=32, Pills=999 |
| Divider too thick | 0.5dp with `hairline` color |
| Frame too narrow | Frame = full device width; 440dp is content max-width only |
