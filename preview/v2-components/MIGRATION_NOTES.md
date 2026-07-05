# M3 Expressive Component Library — Migration Notes

## Overview

This document describes what changed from the existing `composeApp/.../ui/v2/theme/` system to the new `preview/v2-components/` M3 Expressive component library.

All new files live in `preview/v2-components/` — they are **reference source files**, not yet wired into the app's build. When ready to integrate, move them into `composeApp/src/commonMain/kotlin/com/littlebridge/enrollplus/ui/v2/`.

---

## 1. Color System

### Old: `theme/VColors.kt`
- Data class `VColors` with semantic properties: `teal`, `tealDeep`, `navy`, `accent`, `accentSoft`, `ink`, `ink2`, `ink3`, `card`, `cream`, `background`, `border1`, `hairline`, `success`, `warning`, `danger`, etc.
- Three palettes: `LightVColors`, `NightVColors`, `HighContrastVColors`
- Accessed via `VTheme.colors.tealDeep` (CompositionLocal)
- Colors: teal `#3CB9A9`, navy `#26234D`, accent `#6C5CE0`, lavender, sand, rose, mint tones

### New: `tokens/VColors.kt`
- Object `VColors` with M3 Expressive color constants (plain `val` properties, no data class)
- Single light palette (dark palette to be added later)
- Accessed directly: `VColors.Primary`, `VColors.Tertiary`, etc.
- Colors match HTML `:root` exactly: primary `#6750F6`, tertiary `#00BFA0`, secondary `#625B71`, surface `#FEF9FF`, etc.
- Adds surface container hierarchy: `SurfaceContainerLowest/Low/High/Highest`
- Adds gradient stop colors: `PrimaryMid`, `PrimaryDeep`, `TertiaryDeep`, etc.
- Adds glassmorphism approximations: `GlassWhite15`, `GlassWhite12`, `GlassWhite20`
- Adds radial glow colors: `HeroGlowTopRight`, `HeroGlowBottomLeft`, etc.

### Mapping Table

| Old (VColors)         | New (VColors)              | Hex (new)  |
|-----------------------|----------------------------|------------|
| `accent`              | `Primary`                  | `#6750F6`  |
| `accentDeep`          | `PrimaryMid`               | `#544AB8`  |
| `teal`                | `Tertiary`                 | `#00BFA0`  |
| `tealDeep`            | `TertiaryDeep`             | `#00897B`  |
| `navy`                | `Secondary`                | `#625B71`  |
| `ink`                 | `OnSurface`                | `#1D1B20`  |
| `ink2`                | `OnSurfaceVariant`         | `#49454F`  |
| `ink3`                | `Outline`                  | `#7A757F`  |
| `card`                | `SurfaceContainerLowest`   | `#FFFFFF`  |
| `cream`               | `SurfaceContainerLow`      | `#F8F4FB`  |
| `background`          | `Surface`                  | `#FEF9FF`  |
| `border1`             | `OutlineVariant`           | `#CAC4CF`  |
| `hairline`            | `OutlineVariant`           | `#CAC4CF`  |
| `success`             | `Tertiary`                 | `#00BFA0`  |
| `danger`              | `Error`                    | `#BA1A1A`  |
| `dangerInk`           | `OnError`                  | `#FFFFFF`  |
| `placeholder`         | `Outline`                  | `#7A757F`  |

---

## 2. Typography

### Old: `theme/VType.kt`
- Font family: **Plus Jakarta Sans** (UI) + **DM Mono** (data)
- Styles: `h1`, `h2`, `h3`, `h4`, `body`, `bodyStrong`, `caption`, `label`, `inputLabel`, `dataXl`, `dataLg`, `dataMd`
- Data class `VTypography` with `scaleBy(fontScale)` for accessibility
- Accessed via `VTheme.type.h1` (CompositionLocal)

### New: `tokens/VTypography.kt`
- Font family: **Inter** (matching HTML `font-family: 'Inter', system-ui, -apple-system, sans-serif`)
- Currently uses `FontFamily.SansSerif` as fallback until Inter font resources are bundled
- ~80+ named text styles extracted from HTML CSS — each maps to a specific HTML class
- Object `VTypography` with plain `val` properties (no data class, no `scaleBy`)
- Accessed directly: `VTypography.GreetingTitle`, `VTypography.SectionHeader`, etc.

### Key Style Mapping

| Old (VType)     | New (VTypography)          | Size/Weight     |
|-----------------|----------------------------|-----------------|
| `h1`            | `GreetingTitle`            | 34sp / 800      |
| `h2`            | `SectionHeader`            | 24sp / 800      |
| `h3`            | `HeroName`                 | 22sp / 800      |
| `h4`            | `FeatureTitle`             | 18sp / 800      |
| `body`          | `FormInput`                | 15sp / 500      |
| `bodyStrong`    | `UpdateTitle`              | 16sp / 700      |
| `caption`       | `UpdateText`               | 14sp / 500      |
| `label`         | `FormLabelPortal`          | 13sp / 600      |
| `inputLabel`    | `FormLabelAuth`            | 12sp / 600      |
| `dataXl`        | `FeesAmount`               | 40sp / 900      |
| `dataLg`        | `HeroStatValue`            | 26sp / 900      |
| `dataMd`        | `QuickStatValue`           | 22sp / 900      |

### New styles with no old equivalent
`Eyebrow`, `LivePill`, `NavLabel`, `NavBadge`, `ScheduleHour`, `ScheduleAmPm`, `ScheduleStatus`, `Chip`, `SubTab`, `ThreadName`, `ThreadPreview`, `ThreadBadge`, `BadgeName`, `BadgeDesc`, `BadgeEarnedTag`, `SchoolTag`, `SchoolRating`, `RoleTileName`, `RoleTileDesc`, `TrustBadge`, `LoginHeroTitle`, `OnboardTitle`, `OnboardDesc`, `DividerLabel`, `ChatText`, `ChatTime`, `LinkedBadge`, `LandingHeadline`, `LandingSub`, `BrandText`, `VersionTag`, `RolesTitle`, `LandingTerms`, and many more.

---

## 3. Shapes

### Old: `theme/VDimens.kt`
- `radiusSm` = 6dp, `radiusMd` = 10dp, `radiusInput` = 12dp, `radiusLg` = 14dp, `radiusCard` = 16dp, `radiusXl` = 20dp, `radiusSheet` = 32dp, `radiusPill` = 999dp
- Shape helpers: `shapeSm`, `shapeMd`, `shapeInput`, `shapeLg`, `shapeCard`, `shapeXl`, `shapePill`

### New: `tokens/VShapes.kt`
- `Xs` = 4dp, `Sm` = 8dp, `Md` = 12dp, `Lg` = 16dp, `Xl` = 24dp, `TwoXl` = 28dp, `Full` = 999dp (CircleShape)
- Both `RoundedCornerShape` values and `Dp` values (for `animateDpAsState` targets)
- Phone frame radii: `PhoneRadius` = 56dp, `ScreenRadius` = 44dp, `IslandRadius` = 22dp

### Mapping

| Old (VDimens)    | New (VShapes)  | dp  |
|------------------|----------------|-----|
| `radiusSm`       | `Xs`           | 4   |
| `radiusMd`       | `Sm`           | 8   |
| `radiusInput`    | `Md`           | 12  |
| `radiusLg`       | `Lg`           | 16  |
| `radiusCard`     | `Lg`           | 16  |
| `radiusXl`       | `Xl`           | 24  |
| `radiusSheet`    | `TwoXl`        | 28  |
| `radiusPill`     | `Full`         | 999 |

**Note**: Old `radiusMd` = 10dp is gone. The new system jumps from `Sm` (8dp) to `Md` (12dp). Old `radiusXl` = 20dp is also gone — closest is `Xl` = 24dp.

---

## 4. Motion

### Old: `theme/VMotion.kt`
- Spring-based animation specs (converted from framer-motion)
- `softSpring`, `sheetSpring`, `cardSpring`, `snappySpring`
- Modifiers: `pressScale`, `cardPressScale`, `staggeredItemEntrance`, `shakeOnError`
- Screen transitions: `fadeUp`, `forwardSlide`, `modalRise`, `quietFade`

### New: `tokens/VMotion.kt`
- CSS-derived duration constants: `DurShort2` (150ms), `DurShort3` (200ms), `DurMedium1` (250ms), `DurMedium2` (300ms), `DurLong1` (400ms), `DurLong2` (500ms)
- Easing: `EaseEmphasized` = `FastOutSlowInEasing` (cubic-bezier(0.2,0,0,1))
- Keyframe-derived constants: `SlideUpFromY`, `SpringInScaleFrom`, `LivePulseDuration`, `LiveBlinkDuration`, `LiBounceDuration`, `FloatGlowDuration1/2`
- Staggered delay arrays: `StaggeredDelays` (0,30,60,100,150,200,250,300ms), `AuthAnimDelays` (100,200,300,400,500,600ms)
- Helper composables: `rememberLivePulse()`, `rememberLiveBlink()`

### New: `modifiers/` (3 files)
- **`VShapeMorphModifier.kt`**: Animates corner radius on press (e.g. `ShapeFull` → `ShapeMd` on `:active`). This is a new interaction pattern not in the old system.
- **`VGlowModifier.kt`**: Draws radial gradient circles at specified positions (reproduces CSS `::before`/`::after` pseudo-elements). Not in old system.
- **`VPressScaleModifier.kt`**: Scales composable on press using `graphicsLayer`. Similar to old `pressScale` but uses spring animation and works with any scale value (0.9, 0.93, 0.95, 0.96, 0.97, 0.98, 0.92).

---

## 5. Component Structure

### Old: `components/` (flat directory)
~20 component files in a flat directory:
- `VButton.kt`, `VCard.kt`, `VInput.kt`, `VNavigation.kt`, `VAvatar.kt`, `VBadge.kt`, `VBrandLogo.kt`, `VCharts.kt`, `VDatePicker.kt`, `VIcons.kt`, `VLanguagePicker.kt`, `VLogo.kt`, `VProgress.kt`, `VPullRefresh.kt`, `VScheduleToggle.kt`, `VShimmer.kt`, `VSnackbar.kt`, `VStructure.kt`, `VThemePicker.kt`, `VTimePicker.kt`, etc.

### New: `components/` (categorized subdirectories)
~30 component files in 8 subdirectories:
- `buttons/` — VPrimaryButton, VSecondaryButton, VLandingButton, VTextButton, VSocialButton, VIconButton, VFAB
- `cards/` — VHeroCard, VProfileHeroCard, VFeesHeroCard, VBadgeCard, VSchoolCardFull, VSchoolOptionCard, VQuickStatCard, VStatTile, VUpdateCard, VRoleTile, VTrustBadge, VChildLinkCard, VSurfaceCard
- `navigation/` — VBottomNav, VTopAppBar, VBackHeader, VSegmentedToggle, VOnboardingDots, VFilterChip
- `carousel/` — VScrollSnapCarousel, VStaggeredColumn
- `form/` — VTextInput, VSearchField
- `progress/` — VProgressBar, VProgressRing, VPulseDot, VShimmer
- `overlay/` — VFullScreenOverlay, VDialog
- `typography/` — VGreetingEyebrow, VGreetingTitle, VSectionHeader, VGradientText
- `misc/` — VDivider, VStatusBar, VPhoneFrame

### Component Mapping

| Old Component          | New Replacement(s)                              |
|------------------------|-------------------------------------------------|
| `VButton`              | `VPrimaryButton`, `VSecondaryButton`, `VLandingButton`, `VTextButton`, `VSocialButton` |
| `VCard` / `VActionCard`| `VSurfaceCard`, `VQuickStatCard`, `VStatTile`, `VUpdateCard`, `VRoleTile` |
| `VInput`               | `VTextInput`, `VSearchField`                     |
| `VNavigation`          | `VBottomNav`, `VTopAppBar`, `VBackHeader`        |
| `VBadge`               | `VBadgeCard`, `VTrustBadge`                      |
| `VProgress`            | `VProgressBar`, `VProgressRing`                  |
| `VShimmer`             | `VShimmer` (new version with liBounce animation) |
| `VStructure`           | `VPhoneFrame`, `VFullScreenOverlay`, `VDialog`   |
| `VScheduleToggle`      | `VSegmentedToggle`                               |
| `VAvatar`              | (Inline in VHeroCard, VProfileHeroCard, VUpdateCard) |
| `VBrandLogo` / `VLogo` | (Not yet migrated — add as needed)               |
| `VCharts`              | (Not yet migrated — add as needed)               |
| `VDatePicker`          | (Not yet migrated — add as needed)               |
| `VTimePicker`          | (Not yet migrated — add as needed)               |
| `VIcons`               | (Not yet migrated — use Material Icons or custom)|
| `VLanguagePicker`      | (Not yet migrated)                               |
| `VThemePicker`         | (Not yet migrated)                               |
| `VPullRefresh`         | (Not yet migrated)                               |
| `VSnackbar`            | (Not yet migrated)                               |

---

## 6. Theme Provider

### Old: `theme/VTheme.kt`
- `VTheme` composable wraps content with 4 CompositionLocals: `LocalVColors`, `LocalVType`, `LocalVDimens`, `LocalVThemeDef`
- `VTheme` accessor object: `VTheme.colors`, `VTheme.type`, `VTheme.dimens`, `VTheme.themeDef`
- Material 3 ColorScheme bridge via `materialColorSchemeFor()`
- Font scale support via `LocalFontScale`

### New: No theme provider needed
- All tokens are plain `object` singletons accessed directly: `VColors.Primary`, `VShapes.Xl`, `VTypography.GreetingTitle`, `VMotion.DurShort2`
- No CompositionLocal wrapping required
- No Material 3 ColorScheme bridge (components use explicit colors)
- **Trade-off**: No runtime theme switching. When dark mode is needed, either:
  1. Add a `VColorsDark` object and swap via CompositionLocal, or
  2. Convert `VColors` to a data class with light/dark instances (like the old system)

---

## 7. Animation Patterns

### Shape Morph (NEW)
The HTML prototypes use `border-radius` transitions on `:active` — buttons morph from pill to rounded-rect on press. This is reproduced via `Modifier.shapeMorph()` which animates `RoundedCornerShape` corner radius using `animateDpAsState`.

### Radial Glow (NEW)
The HTML uses `::before` / `::after` pseudo-elements with `radial-gradient(circle, color, transparent 50%)` for decorative glows on hero cards. This is reproduced via `Modifier.radialGlow()` which draws a radial gradient circle at a specified offset using `drawBehind`.

### Conic Gradient Ring (NEW)
Badges use `conic-gradient(from 0deg, primary, tertiary, primary)` as a ring border. Compose doesn't have conic gradients natively — approximated with `Brush.sweepGradient()` which produces a similar effect.

### Glassmorphism (APPROXIMATION)
The HTML uses `backdrop-filter: blur(12px)` with semi-transparent backgrounds. Compose Multiplatform doesn't support backdrop blur on all platforms — approximated with semi-transparent color overlays (`Color.White.copy(alpha = 0.15f)`). On Android 12+ you could use `RenderEffect.createBlurEffect` for real blur.

### Staggered Entrance (ENHANCED)
The old system had `staggeredItemEntrance` modifier. The new system provides `VStaggeredItem` composable with explicit delay control and two animation variants: `slideUp` (translateY 24→0) and `springIn` (scale 0.9→1 + translateY 16→0).

---

## 8. File Inventory

```
preview/v2-components/
├── MIGRATION_NOTES.md                    (this file)
├── tokens/
│   ├── VColors.kt                        (123 lines — M3 Expressive colors)
│   ├── VShapes.kt                        (46 lines — corner radii + phone frame)
│   ├── VMotion.kt                        (120 lines — durations, easing, infinite animations)
│   └── VTypography.kt                    (560+ lines — ~80 text styles)
├── modifiers/
│   ├── VShapeMorphModifier.kt            (48 lines — animated corner radius on press)
│   ├── VGlowModifier.kt                  (62 lines — radial gradient overlay)
│   └── VPressScaleModifier.kt            (68 lines — scale transform on press)
└── components/
    ├── buttons/                          (7 files)
    │   ├── VPrimaryButton.kt
    │   ├── VSecondaryButton.kt
    │   ├── VLandingButton.kt
    │   ├── VTextButton.kt
    │   ├── VSocialButton.kt
    │   ├── VIconButton.kt
    │   └── VFAB.kt
    ├── cards/                            (13 files)
    │   ├── VHeroCard.kt
    │   ├── VProfileHeroCard.kt
    │   ├── VFeesHeroCard.kt
    │   ├── VBadgeCard.kt
    │   ├── VSchoolCardFull.kt
    │   ├── VSchoolOptionCard.kt
    │   ├── VQuickStatCard.kt
    │   ├── VStatTile.kt
    │   ├── VUpdateCard.kt
    │   ├── VRoleTile.kt
    │   ├── VTrustBadge.kt
    │   ├── VChildLinkCard.kt
    │   └── VSurfaceCard.kt
    ├── navigation/                       (6 files)
    │   ├── VBottomNav.kt
    │   ├── VTopAppBar.kt
    │   ├── VBackHeader.kt
    │   ├── VSegmentedToggle.kt
    │   ├── VOnboardingDots.kt
    │   └── VFilterChip.kt
    ├── carousel/                         (2 files)
    │   ├── VScrollSnapCarousel.kt
    │   └── VStaggeredColumn.kt
    ├── form/                             (2 files)
    │   ├── VTextInput.kt
    │   └── VSearchField.kt
    ├── progress/                         (4 files)
    │   ├── VProgressBar.kt
    │   ├── VProgressRing.kt
    │   ├── VPulseDot.kt
    │   └── VShimmer.kt
    ├── overlay/                          (2 files)
    │   ├── VFullScreenOverlay.kt
    │   └── VDialog.kt
    ├── typography/                       (4 files)
    │   ├── VGreetingEyebrow.kt
    │   ├── VGreetingTitle.kt
    │   ├── VSectionHeader.kt
    │   └── VGradientText.kt
    └── misc/                             (3 files)
        ├── VDivider.kt
        ├── VStatusBar.kt
        └── VPhoneFrame.kt
```

**Total**: 47 files (4 tokens + 3 modifiers + 30 components + 1 migration doc + 9 directory READMEs implied)

---

## 9. Integration Steps (When Ready)

1. **Bundle Inter font**: Add Inter `.ttf` files to `composeApp/src/commonMain/composeResources/font/` and update `VTypography.Inter` to use `FontFamily(Font(Res.font.inter_regular), ...)`.

2. **Move files**: Copy `tokens/`, `modifiers/`, and `components/` directories from `preview/v2-components/` into `composeApp/src/commonMain/kotlin/com/littlebridge/enrollplus/ui/v2/`.

3. **Resolve package names**: Update all `package` declarations from `com.littlebridge.enrollplus.ui.v2.tokens` etc. to match the actual target package structure.

4. **Add icon dependencies**: The components reference icons via `(@Composable () -> Unit)?` lambda parameters. Wire up actual icons from `VIcons.kt` or Material Icons.

5. **Dark mode**: Create `VColorsDark` object or convert `VColors` to a data class with light/dark instances. Add a CompositionLocal provider similar to the old `VTheme`.

6. **Remove old components**: Once all screens are migrated to the new components, remove the old `theme/VColors.kt`, `theme/VType.kt`, `theme/VDimens.kt`, `theme/VMotion.kt`, and old component files.

7. **Update imports**: Search-and-replace all `VTheme.colors.accent` → `VColors.Primary`, `VTheme.type.h1` → `VTypography.GreetingTitle`, etc.
