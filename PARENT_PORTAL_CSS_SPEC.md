# Parent Portal — CSS Style Spec (Extracted from HTML Prototype)

> **Source:** `preview/enrollplus-parent-prototype.html`
> **Purpose:** Exact CSS values for all 4 missing tabs + bottom nav + overlays, to match when building Kotlin Compose equivalents.
> **Token mapping:** CSS `var(--X)` → `VColors.X` / `VShapes.X` / `VTypography.X`

## CSS Variable → Compose Token Map

| CSS Variable | Value | Compose Token |
|-------------|-------|---------------|
| `--cream` | `#FBF8F4` | `VColors.cream` |
| `--cream-deep` | `#F5F0E8` | `VColors.creamDeep` |
| `--white` | `#FFF` | `VColors.white` |
| `--surface` | `#FBF8F4` | `VColors.surface` |
| `--surface-tint` | `#F8F4EF` | `VColors.surfaceTint` |
| `--surface-warm` | `#FFF6EE` | `VColors.surfaceWarm` |
| `--ink` | `#1A1614` | `VColors.ink` |
| `--ink-2` | `#5C544E` | `VColors.ink2` |
| `--ink-3` | `#8A8078` | `VColors.ink3` |
| `--line` | `#E8E0D6` | `VColors.line` |
| `--line-soft` | `#F0EAE0` | `VColors.lineSoft` |
| `--violet` | `#5B41D5` | `VColors.violet` |
| `--violet-hover` | `#4A30C4` | `VColors.violetHover` |
| `--violet-soft` | `#EEE8FB` | `VColors.violetSoft` |
| `--coral` | `#F82B60` | `VColors.coral` |
| `--coral-soft` | `#FFE4EC` | `VColors.coralSoft` |
| `--gold` | `#FCB400` | `VColors.gold` |
| `--gold-soft` | `#FFF4D1` | `VColors.goldSoft` |
| `--sky` | `#18BFFF` | `VColors.sky` |
| `--sky-soft` | `#E0F6FF` | `VColors.skySoft` |
| `--mint` | `#2DCE89` | `VColors.mint` |
| `--mint-soft` | `#DCF5E8` | `VColors.mintSoft` |
| `--success` | `#2D7A4A` | `VColors.success` |
| `--success-soft` | `#D4EDDB` | `VColors.successSoft` |
| `--error` | `#BA1A1A` | `VColors.error` |
| `--error-soft` | `#FFDAD6` | `VColors.errorSoft` |
| `--warning` | `#B07500` | `VColors.warning` |
| `--r-sm` | `10px` | `VShapes.sm` (10.dp) |
| `--r-md` | `14px` | `VShapes.md` (14.dp) |
| `--r-lg` | `18px` | `VShapes.lg` (18.dp) |
| `--r-xl` | `24px` | `VShapes.xl` (24.dp) |
| `--r-full` | `9999px` | `VShapes.full` (CircleShape) |
| `--s-xs` | `4px` | 4.dp |
| `--s-sm` | `8px` | 8.dp |
| `--s-md` | `16px` | 16.dp |
| `--s-lg` | `24px` | 24.dp |
| `--s-xl` | `32px` | 32.dp |
| `--font` | `Inter` | `VTypography` font family |
| `--fw-r` | `400` | `FontWeight.Normal` |
| `--fw-m` | `500` | `FontWeight.Medium` |
| `--fw-s` | `600` | `FontWeight.SemiBold` |
| `--fw-b` | `700` | `FontWeight.Bold` |
| `--fw-eb` | `800` | `FontWeight.ExtraBold` |
| `--dur` | `250ms` | `VMotion.dur` (250) |
| `--ease` | `cubic-bezier(0.2,0,0)` | `VMotion.ease` |
| `--shadow-1` | `0 1px 2px rgba(26,22,20,.04), 0 1px 3px rgba(26,22,20,.06)` | `VShadows.shadow1` |
| `--shadow-2` | `0 2px 8px -2px rgba(26,22,20,.08), 0 1px 3px rgba(26,22,20,.04)` | `VShadows.shadow2` |
| `--shadow-3` | `0 8px 24px -6px rgba(26,22,20,.1), 0 2px 8px rgba(26,22,20,.04)` | `VShadows.shadow3` |

---

## 1. Academics Tab

### 1.1 Action Cards Row (`.ac-actions` + `.ac-action`)

| Property | CSS Value | Compose Equivalent |
|----------|-----------|-------------------|
| Row layout | `display:flex; gap:8px; margin:0 24px 16px` | `Row(horizontalArrangement = spacedBy(8.dp), modifier = padding(horizontal=24.dp, bottom=16.dp))` |
| Card flex | `flex:1` | `Modifier.weight(1f)` |
| Card padding | `12px 14px` | `padding(horizontal=14.dp, vertical=12.dp)` |
| Card bg | `var(--white)` | `VColors.white` |
| Card radius | `var(--r-md)` = `14px` | `VShapes.md` (14.dp) |
| Card shadow | `var(--shadow-1)` | `VShadows.shadow1` |
| Card arrangement | `flex; align:center; gap:10px` | `Row(verticalAlignment=CenterVertically, horizontalArrangement=spacedBy(10.dp))` |

### 1.2 Action Card Icon (`.ac-action-icon`)

| Property | CSS Value | Compose Equivalent |
|----------|-----------|-------------------|
| Size | `30x30px` | `30.dp` box |
| Radius | `var(--r-sm)` = `10px` | `VShapes.sm` (10.dp) |
| Icon size | `15x15px` | `15.dp` |
| Icon stroke width | `2` | `2.dp` stroke |
| Leave icon bg | `var(--gold-soft)` | `VColors.goldSoft` |
| Leave icon stroke | `var(--warning)` | `VColors.warning` |
| Health icon bg | `var(--coral-soft)` | `VColors.coralSoft` |
| Health icon stroke | `var(--coral)` | `VColors.coral` |

### 1.3 Action Card Label (`.ac-action-label`)

| Property | CSS Value | Compose Equivalent |
|----------|-----------|-------------------|
| Font size | `12px` | `12.sp` |
| Font weight | `700` | `FontWeight.Bold` |
| Color | `var(--ink)` | `VColors.ink` |
| Letter spacing | `-.2px` | `-0.2.sp` |
| Line height | `1.3` | `1.3` (lineHeightMultiple) |

### 1.4 Sub-tabs Bar (`.ac-subtabs` + `.ac-subtab`)

| Property | CSS Value | Compose Equivalent |
|----------|-----------|-------------------|
| Bar layout | `flex; gap:0; margin:0 24px 16px` | `Row(padding(horizontal=24.dp, bottom=16.dp))` |
| Bar bg | `var(--surface-tint)` | `VColors.surfaceTint` |
| Bar radius | `var(--r-md)` = `14px` | `VShapes.md` |
| Bar padding | `3px` | `3.dp` |
| Bar overflow | `overflow-x:auto` | `horizontalScroll(rememberScrollState())` |
| Tab padding | `7px 14px` | `padding(horizontal=14.dp, vertical=7.dp)` |
| Tab radius | `var(--r-sm)` = `10px` | `VShapes.sm` |
| Tab font size | `12px` | `12.sp` |
| Tab font weight (inactive) | `600` | `FontWeight.SemiBold` |
| Tab color (inactive) | `var(--ink-3)` | `VColors.ink3` |
| Tab bg (inactive) | `transparent` | `Color.Transparent` |
| Tab font weight (active) | `800` | `FontWeight.ExtraBold` |
| Tab color (active) | `var(--ink)` | `VColors.ink` |
| Tab bg (active) | `var(--white)` | `VColors.white` |
| Tab shadow (active) | `var(--shadow-1)` | `VShadows.shadow1` |
| Tab whitespace | `nowrap` | no wrap (default in Row) |

### 1.5 Content Card (`.ac-card`)

| Property | CSS Value | Compose Equivalent |
|----------|-----------|-------------------|
| Bg | `var(--white)` | `VColors.white` |
| Radius | `var(--r-md)` = `14px` | `VShapes.md` |
| Shadow | `var(--shadow-1)` | `VShadows.shadow1` |
| Padding | `16px` | `16.dp` |
| Margin | `0 24px 8px` | `padding(horizontal=24.dp, bottom=8.dp)` |

### 1.6 Card Title (`.ac-card-title`)

| Property | CSS Value | Compose Equivalent |
|----------|-----------|-------------------|
| Font size | `13px` | `13.sp` |
| Font weight | `800` | `FontWeight.ExtraBold` |
| Color | `var(--ink)` | `VColors.ink` |
| Letter spacing | `-.2px` | `-0.2.sp` |
| Margin bottom | `12px` | `12.dp` |

### 1.7 Stat Row (`.ac-stat-row`)

| Property | CSS Value | Compose Equivalent |
|----------|-----------|-------------------|
| Layout | `flex; justify:space-between; align:center` | `Row(SpaceBetween, CenterVertically)` |
| Padding | `8px 0` | `padding(vertical=8.dp)` |
| Border (not last) | `1px solid var(--line-soft)` | `border(bottom=1.dp, color=VColors.lineSoft)` |
| Label font | `13px 600 var(--ink-2)` | `13.sp SemiBold VColors.ink2` |
| Value font | `14px 800 var(--ink) -0.2px` | `14.sp ExtraBold VColors.ink -0.2.sp` |
| Badge font | `10px 700` | `10.sp Bold` |
| Badge padding | `2px 8px` | `padding(horizontal=8.dp, vertical=2.dp)` |
| Badge radius | `var(--r-full)` | `VShapes.full` |
| Badge up | `bg:mint-soft, color:success` | `VColors.mintSoft / VColors.success` |
| Badge down | `bg:coral-soft, color:coral` | `VColors.coralSoft / VColors.coral` |
| Badge neutral | `bg:surface-tint, color:ink-3` | `VColors.surfaceTint / VColors.ink3` |

### 1.8 Progress Bar (`.ac-progress`)

| Property | CSS Value | Compose Equivalent |
|----------|-----------|-------------------|
| Margin | `8px 0` | `padding(vertical=8.dp)` |
| Head layout | `flex; justify:space-between` | `Row(SpaceBetween)` |
| Head margin bottom | `6px` | `6.dp` |
| Label font | `12px 600 var(--ink-2)` | `12.sp SemiBold VColors.ink2` |
| Pct font | `12px 800 var(--ink)` | `12.sp ExtraBold VColors.ink` |
| Bar height | `6px` | `6.dp` |
| Bar bg | `var(--surface-tint)` | `VColors.surfaceTint` |
| Bar radius | `var(--r-full)` | `VShapes.full` |
| Fill height | `100%` | `fillMaxHeight()` |
| Fill radius | `var(--r-full)` | `VShapes.full` |
| Fill animation | `transition:width 600ms var(--ease)` | `animateFloatAsState(600ms, VMotion.ease)` |
| Fill violet | `var(--violet)` | `VColors.violet` |
| Fill mint | `var(--mint)` | `VColors.mint` |
| Fill gold | `var(--gold)` | `VColors.gold` |
| Fill coral | `var(--coral)` | `VColors.coral` |

### 1.9 Marks Item (`.ac-marks-item`)

| Property | CSS Value | Compose Equivalent |
|----------|-----------|-------------------|
| Layout | `flex; align:center; gap:12px; padding:10px 0` | `Row(CenterVertically, spacedBy(12.dp), padding(vertical=10.dp))` |
| Border (not last) | `1px solid var(--line-soft)` | `border(bottom=1.dp, VColors.lineSoft)` |
| Subject font | `13px 700 var(--ink) -0.2px flex:1` | `13.sp Bold VColors.ink -0.2.sp, weight(1f)` |
| Date font | `11px 500 var(--ink-3)` | `11.sp Medium VColors.ink3` |
| Score font | `14px 800 var(--ink) tabular-nums` | `14.sp ExtraBold VColors.ink, FontFeatureTabularNums` |
| Grade font | `10px 800` | `10.sp ExtraBold` |
| Grade padding | `2px 8px` | `padding(horizontal=8.dp, vertical=2.dp)` |
| Grade radius | `var(--r-full)` | `VShapes.full` |
| Grade A | `bg:mint-soft, color:success` | `VColors.mintSoft / VColors.success` |
| Grade B | `bg:sky-soft, color:sky` | `VColors.skySoft / VColors.sky` |
| Grade C | `bg:gold-soft, color:warning` | `VColors.goldSoft / VColors.warning` |

### 1.10 Attendance Calendar (`.ac-cal`)

| Property | CSS Value | Compose Equivalent |
|----------|-----------|-------------------|
| Layout | `grid 7 cols; gap:3px; margin:8px 0` | `LazyVerticalGrid(7 cols, spacing=3.dp, padding(vertical=8.dp))` |
| Day cell | `aspect-ratio:1; radius:10px; flex center` | `Box(aspectRatio=1f, shape=10.dp, contentAlignment=Center)` |
| Day font | `10px 600 var(--ink-3)` | `10.sp SemiBold VColors.ink3` |
| Present | `bg:mint-soft, color:success` | `VColors.mintSoft / VColors.success` |
| Absent | `bg:coral-soft, color:coral` | `VColors.coralSoft / VColors.coral` |
| Late | `bg:gold-soft, color:warning` | `VColors.goldSoft / VColors.warning` |
| Empty | `bg:surface-tint` | `VColors.surfaceTint` |
| Header | `9px 700 var(--ink-3) uppercase` | `9.sp Bold VColors.ink3, uppercase` |

### 1.11 Homework Item (`.ac-hw-item`)

| Property | CSS Value | Compose Equivalent |
|----------|-----------|-------------------|
| Layout | `flex; align:flex-start; gap:12px; padding:10px 0` | `Row(Top, spacedBy(12.dp), padding(vertical=10.dp))` |
| Border (not last) | `1px solid var(--line-soft)` | `border(bottom=1.dp, VColors.lineSoft)` |
| Status dot | `8x8px; radius:50%` | `8.dp Box(CircleShape)` |
| Dot done | `bg:mint` | `VColors.mint` |
| Dot pending | `bg:gold` | `VColors.gold` |
| Dot late | `bg:coral` | `VColors.coral` |
| Subject font | `13px 700 var(--ink) -0.2px` | `13.sp Bold VColors.ink -0.2.sp` |
| Title font | `12px 500 var(--ink-2); margin-top:2px` | `12.sp Medium VColors.ink2, padding(top=2.dp)` |
| Due font | `10px 600 var(--ink-3); margin-top:3px` | `10.sp SemiBold VColors.ink3, padding(top=3.dp)` |
| Badge font | `9px 800` | `9.sp ExtraBold` |
| Badge padding | `2px 7px` | `padding(horizontal=7.dp, vertical=2.dp)` |
| Badge radius | `var(--r-full)` | `VShapes.full` |
| Badge done | `bg:mint-soft, color:success` | `VColors.mintSoft / VColors.success` |
| Badge pending | `bg:gold-soft, color:warning` | `VColors.goldSoft / VColors.warning` |
| Badge late | `bg:coral-soft, color:coral` | `VColors.coralSoft / VColors.coral` |

### 1.12 Academics Tab HTML Structure Summary

```
[Action Cards Row]
  [Leave Action Card] [Health Action Card]
[Sub-tabs Bar]
  Overview | Attendance | Marks | Syllabus | Homework | Quizzes | Report
[Sub-tab Content]
  Overview:
    [Card: Performance Summary] — 4 stat rows with badges
    [Card: Syllabus Coverage] — 4 progress bars (violet/mint/gold/coral)
  Attendance:
    [Card: January 2026] — 7-col calendar grid
    [Card: Summary] — 4 stat rows (present/absent/late/rate)
  Marks:
    [Card: Recent Assessments] — 5 marks items with grade badges
  Syllabus:
    [Card: Subject Coverage] — 5 progress bars
  Homework:
    [Card: Today's Homework] — 3 hw items with status dots + badges
  Quizzes:
    [Card: Recent Quizzes] — 4 quiz items (completed + upcoming)
  Report:
    [Card: Report Cards] — 3 marks items with grade badges
    [Card: AI Insights] — strengths + improvement areas text
```

---

## 2. Fees Tab

### 2.1 Balance Hero Card (`.fees-hero`)

| Property | CSS Value | Compose Equivalent |
|----------|-----------|-------------------|
| Margin | `8px 24px 16px` | `padding(horizontal=24.dp, top=8.dp, bottom=16.dp)` |
| Padding | `24px 20px` | `padding(horizontal=20.dp, vertical=24.dp)` |
| Bg | `var(--white)` | `VColors.white` |
| Radius | `var(--r-lg)` = `18px` | `VShapes.lg` (18.dp) |
| Shadow | `var(--shadow-1)` | `VShadows.shadow1` |
| Decorative circle | `::before 120x120px, coralSoft, opacity:.4, top:-30px right:-30px` | `Box(120.dp, CircleShape, VColors.coralSoft.copy(alpha=0.4f), offset top=-30 right=-30)` |

### 2.2 Hero Label (`.fees-hero-label`)

| Property | CSS Value | Compose Equivalent |
|----------|-----------|-------------------|
| Font size | `11px` | `11.sp` |
| Font weight | `700` | `FontWeight.Bold` |
| Color | `var(--ink-3)` | `VColors.ink3` |
| Text transform | `uppercase` | `uppercase` |
| Letter spacing | `1px` | `1.sp` |

### 2.3 Hero Amount (`.fees-hero-amount`)

| Property | CSS Value | Compose Equivalent |
|----------|-----------|-------------------|
| Font size | `32px` | `32.sp` |
| Font weight | `800` | `FontWeight.ExtraBold` |
| Color | `var(--ink)` | `VColors.ink` |
| Letter spacing | `-1px` | `-1.sp` |
| Margin top | `6px` | `6.dp` |

### 2.4 Hero Due (`.fees-hero-due`)

| Property | CSS Value | Compose Equivalent |
|----------|-----------|-------------------|
| Font size | `12px` | `12.sp` |
| Font weight | `500` | `FontWeight.Medium` |
| Color | `var(--ink-3)` | `VColors.ink3` |
| Margin top | `4px` | `4.dp` |

### 2.5 Pay Now Button (`.fees-hero-btn`)

| Property | CSS Value | Compose Equivalent |
|----------|-----------|-------------------|
| Margin top | `16px` | `padding(top=16.dp)` |
| Padding | `12px 20px` | `padding(horizontal=20.dp, vertical=12.dp)` |
| Bg | `var(--violet)` | `VColors.violet` |
| Text color | `#fff` | `VColors.white` |
| Radius | `var(--r-md)` = `14px` | `VShapes.md` |
| Font size | `14px` | `14.sp` |
| Font weight | `700` | `FontWeight.Bold` |
| Shadow | `0 4px 12px -2px rgba(91,65,213,.3)` | custom violet shadow |
| Icon size | `16x16px` | `16.dp` |
| Layout | `flex; align:center; gap:8px` | `Row(CenterVertically, spacedBy(8.dp))` |
| Hover bg | `var(--violet-hover)` | `VColors.violetHover` |

### 2.6 Fee Announcements (reuses `.ph-ann` from Home tab)

| Property | CSS Value | Compose Equivalent |
|----------|-----------|-------------------|
| Layout | `flex; gap:12px; padding:14px 16px` | `Row(spacedBy(12.dp), padding(horizontal=16.dp, vertical=14.dp))` |
| Bg | `var(--white)` | `VColors.white` |
| Radius | `var(--r-md)` = `14px` | `VShapes.md` |
| Shadow | `var(--shadow-1)` | `VShadows.shadow1` |
| Left bar | `::before 3px, var(--sky)` | `border(start=3.dp, VColors.sky)` |
| Icon container | `34x34px, radius:10px, bg:sky-soft` | `34.dp Box(10.dp, VColors.skySoft)` |
| Icon size | `15x15px, stroke:sky` | `15.dp, VColors.sky` |
| Title font | `13px 700 var(--ink) -0.2px` | `13.sp Bold VColors.ink -0.2.sp` |
| Body font | `12px 500 var(--ink-2); line-height:1.4; 2-line clamp` | `12.sp Medium VColors.ink2, maxLines=2` |
| Time font | `10px 600 var(--ink-3); margin-top:4px` | `10.sp SemiBold VColors.ink3, padding(top=4.dp)` |

### 2.7 Payment History Item (`.fees-pay-item`)

| Property | CSS Value | Compose Equivalent |
|----------|-----------|-------------------|
| Layout | `flex; align:center; gap:12px; padding:12px 0` | `Row(CenterVertically, spacedBy(12.dp), padding(vertical=12.dp))` |
| Border (not last) | `1px solid var(--line-soft)` | `border(bottom=1.dp, VColors.lineSoft)` |
| Icon container | `34x34px, radius:10px, bg:mint-soft` | `34.dp Box(10.dp, VColors.mintSoft)` |
| Icon size | `15x15px, stroke:success` | `15.dp, VColors.success` |
| Title font | `13px 700 var(--ink) -0.2px` | `13.sp Bold VColors.ink -0.2.sp` |
| Date font | `11px 500 var(--ink-3); margin-top:1px` | `11.sp Medium VColors.ink3, padding(top=1.dp)` |
| Amount font | `14px 800 var(--ink) tabular-nums` | `14.sp ExtraBold VColors.ink, tabular nums` |
| Receipt font | `10px 600 var(--violet); margin-top:2px` | `10.sp SemiBold VColors.violet, padding(top=2.dp)` |

### 2.8 Fees Tab HTML Structure Summary

```
[Balance Hero Card]
  [Label: "Outstanding Balance"]
  [Amount: "₹2,500" — 32sp ExtraBold]
  [Due: "Due by March 15, 2026 · Term 3"]
  [Pay Now Button — violet, 14sp Bold, arrow icon]
[Section: Fee Announcements]
  [Announcement Card — sky left bar, megaphone icon]
[Section: Payment History]
  [Card with payment items]
    [Pay Item 1: Term 2 — ₹12,500 — Download Receipt]
    [Pay Item 2: Term 1 — ₹12,500 — Download Receipt]
    [Pay Item 3: Annual — ₹3,000 — Download Receipt]
```

---

## 3. Conversations Tab

### 3.1 Segment Control (`.conv-segments` + `.conv-seg`)

| Property | CSS Value | Compose Equivalent |
|----------|-----------|-------------------|
| Bar layout | `flex; gap:0; margin:8px 24px 16px` | `Row(padding(horizontal=24.dp, top=8.dp, bottom=16.dp))` |
| Bar bg | `var(--surface-tint)` | `VColors.surfaceTint` |
| Bar radius | `var(--r-md)` = `14px` | `VShapes.md` |
| Bar padding | `3px` | `3.dp` |
| Segment flex | `flex:1` | `Modifier.weight(1f)` |
| Segment padding | `8px` | `8.dp` |
| Segment radius | `var(--r-sm)` = `10px` | `VShapes.sm` |
| Segment font | `12px 600 var(--ink-3)` | `12.sp SemiBold VColors.ink3` |
| Segment text align | `center` | `TextAlign.Center` |
| Active bg | `var(--white)` | `VColors.white` |
| Active color | `var(--ink)` | `VColors.ink` |
| Active weight | `800` | `FontWeight.ExtraBold` |
| Active shadow | `var(--shadow-1)` | `VShadows.shadow1` |

### 3.2 Thread Item (`.conv-thread`)

| Property | CSS Value | Compose Equivalent |
|----------|-----------|-------------------|
| Layout | `flex; align:center; gap:12px; padding:12px 16px` | `Row(CenterVertically, spacedBy(12.dp), padding(horizontal=16.dp, vertical=12.dp))` |
| Bg | `var(--white)` | `VColors.white` |
| Radius | `var(--r-md)` = `14px` | `VShapes.md` |
| Shadow | `var(--shadow-1)` | `VShadows.shadow1` |
| Margin | `0 24px 6px` | `padding(horizontal=24.dp, bottom=6.dp)` |

### 3.3 Thread Avatar (`.conv-avatar`)

| Property | CSS Value | Compose Equivalent |
|----------|-----------|-------------------|
| Size | `42x42px` | `42.dp` |
| Radius | `var(--r-full)` | `CircleShape` |
| Font size | `14px` | `14.sp` |
| Font weight | `800` | `FontWeight.ExtraBold` |
| Violet variant | `bg:violet-soft, color:violet` | `VColors.violetSoft / VColors.violet` |
| Sky variant | `bg:sky-soft, color:sky` | `VColors.skySoft / VColors.sky` |
| Coral variant | `bg:coral-soft, color:coral` | `VColors.coralSoft / VColors.coral` |
| Gold variant | `bg:gold-soft, color:warning` | `VColors.goldSoft / VColors.warning` |

### 3.4 Thread Info (`.conv-name`, `.conv-preview`)

| Property | CSS Value | Compose Equivalent |
|----------|-----------|-------------------|
| Name font | `13px 700 var(--ink) -0.2px` | `13.sp Bold VColors.ink -0.2.sp` |
| Preview font | `12px 500 var(--ink-3); margin-top:2px` | `12.sp Medium VColors.ink3, padding(top=2.dp)` |
| Preview overflow | `ellipsis; nowrap` | `maxLines=1, TextOverflow.Ellipsis` |

### 3.5 Thread Meta (`.conv-meta`, `.conv-time`, `.conv-unread`)

| Property | CSS Value | Compose Equivalent |
|----------|-----------|-------------------|
| Meta layout | `flex column; align:flex-end; gap:4px` | `Column(horizontalAlignment=End, spacedBy(4.dp))` |
| Time font | `10px 600 var(--ink-3)` | `10.sp SemiBold VColors.ink3` |
| Unread min-width | `18px` | `minWidth=18.dp` |
| Unread height | `18px` | `18.dp` |
| Unread radius | `var(--r-full)` | `CircleShape` |
| Unread bg | `var(--violet)` | `VColors.violet` |
| Unread text | `#fff 10px 800` | `VColors.white 10.sp ExtraBold` |
| Unread padding | `0 5px` | `padding(horizontal=5.dp)` |

### 3.6 Announcement Item (`.conv-ann-item`)

| Property | CSS Value | Compose Equivalent |
|----------|-----------|-------------------|
| Layout | `flex; gap:12px; padding:14px 16px` | `Row(spacedBy(12.dp), padding(horizontal=16.dp, vertical=14.dp))` |
| Bg | `var(--white)` | `VColors.white` |
| Radius | `var(--r-md)` = `14px` | `VShapes.md` |
| Shadow | `var(--shadow-1)` | `VShadows.shadow1` |
| Margin | `0 24px 8px` | `padding(horizontal=24.dp, bottom=8.dp)` |
| Left bar | `::before 3px, var(--sky)` | `border(start=3.dp, VColors.sky)` |
| Category font | `9px 800; padding:2px 7px; radius:full` | `9.sp ExtraBold, padding(h=7.dp,v=2.dp), CircleShape` |
| Category colors | `bg:sky-soft, color:sky` | `VColors.skySoft / VColors.sky` |
| Title font | `13px 700 var(--ink) -0.2px` | `13.sp Bold VColors.ink -0.2.sp` |
| Body font | `12px 500 var(--ink-2); line-height:1.4; 2-line clamp` | `12.sp Medium VColors.ink2, maxLines=2` |
| Time font | `10px 600 var(--ink-3); margin-top:5px` | `10.sp SemiBold VColors.ink3, padding(top=5.dp)` |

### 3.7 Conversations Tab HTML Structure Summary

```
[Segment Control]
  Messages | Announcements
[Messages Segment]
  [Thread: PS avatar (violet) — Priya Sharma (Math) — "Aarav did well..." — 11:30 — unread:1]
  [Thread: MI avatar (sky) — Meera Iyer (English) — "Please ensure..." — 10:15 — unread:1]
  [Thread: AD avatar (gold) — Anita Desai (Social) — "Thank you..." — Yesterday]
  [Thread: OF avatar (coral) — School Office — "Fee reminder..." — 3d]
[Announcements Segment]
  [Ann: Events — Sports Day Registration — 2h ago]
  [Ann: PTM — PTM Scheduled for Jan 22 — 5h ago]
  [Ann: Fees — Term 3 Fee Payment Due — 3d ago]
  [Ann: Holiday — Republic Day Holiday — 1w ago]
```

---

## 4. Profile Tab

### 4.1 Hero Card (`.prof-hero`)

| Property | CSS Value | Compose Equivalent |
|----------|-----------|-------------------|
| Margin | `8px 24px 16px` | `padding(horizontal=24.dp, top=8.dp, bottom=16.dp)` |
| Padding | `24px 20px` | `padding(horizontal=20.dp, vertical=24.dp)` |
| Bg | `var(--white)` | `VColors.white` |
| Radius | `var(--r-lg)` = `18px` | `VShapes.lg` (18.dp) |
| Shadow | `var(--shadow-1)` | `VShadows.shadow1` |
| Text align | `center` | `horizontalAlignment=CenterHorizontally` |
| Decorative circle 1 | `::before 120x120px, violetSoft, opacity:.4, top:-40 right:-20` | `Box(120.dp, CircleShape, VColors.violetSoft.copy(alpha=0.4f))` |
| Decorative circle 2 | `::after 80x80px, coralSoft, opacity:.3, bottom:-30 left:-20` | `Box(80.dp, CircleShape, VColors.coralSoft.copy(alpha=0.3f))` |

### 4.2 Hero Avatar (`.prof-hero-avatar`)

| Property | CSS Value | Compose Equivalent |
|----------|-----------|-------------------|
| Size | `64x64px` | `64.dp` |
| Radius | `var(--r-full)` | `CircleShape` |
| Bg | `var(--violet-soft)` | `VColors.violetSoft` |
| Font size | `22px` | `22.sp` |
| Font weight | `800` | `FontWeight.ExtraBold` |
| Color | `var(--violet)` | `VColors.violet` |
| Margin | `0 auto 12px` | `padding(bottom=12.dp)` |
| Outline | `3px solid var(--white)` | `border(3.dp, VColors.white)` |
| Ring shadow | `0 0 0 3px var(--violet-soft)` | `3.dp ring VColors.violetSoft` |

### 4.3 Hero Text (`.prof-hero-name`, `.prof-hero-class`, `.prof-hero-house`)

| Property | CSS Value | Compose Equivalent |
|----------|-----------|-------------------|
| Name font | `18px 800 var(--ink) -0.4px` | `18.sp ExtraBold VColors.ink -0.4.sp` |
| Class font | `13px 500 var(--ink-3); margin-top:2px` | `13.sp Medium VColors.ink3, padding(top=2.dp)` |
| House layout | `inline-flex; gap:4px; margin-top:8px` | `Row(spacedBy(4.dp), padding(top=8.dp))` |
| House font | `10px 700; padding:3px 10px; radius:full` | `10.sp Bold, padding(h=10.dp,v=3.dp), CircleShape` |
| House colors | `bg:coral-soft, color:coral` | `VColors.coralSoft / VColors.coral` |
| House letter spacing | `.3px` | `0.3.sp` |

### 4.4 Stats Grid (`.prof-stats` + `.prof-stat`)

| Property | CSS Value | Compose Equivalent |
|----------|-----------|-------------------|
| Grid | `grid 2 cols; gap:8px; margin:0 24px 16px` | `LazyVerticalGrid(2 cols, spacing=8.dp, padding(h=24.dp,b=16.dp))` or `Column { Row, Row }` |
| Stat padding | `14px` | `14.dp` |
| Stat bg | `var(--white)` | `VColors.white` |
| Stat radius | `var(--r-md)` = `14px` | `VShapes.md` |
| Stat shadow | `var(--shadow-1)` | `VShadows.shadow1` |
| Number font | `20px 800 var(--ink) -0.3px` | `20.sp ExtraBold VColors.ink -0.3.sp` |
| Label font | `10px 600 var(--ink-3); margin-top:4px; uppercase; letter-spacing:.5px` | `10.sp SemiBold VColors.ink3, padding(top=4.dp), uppercase, 0.5.sp` |
| Trend font | `10px 700; margin-top:2px` | `10.sp Bold, padding(top=2.dp)` |
| Trend up | `color:var(--success)` | `VColors.success` |
| Trend down | `color:var(--coral)` | `VColors.coral` |

### 4.5 Account Row (`.prof-row`)

| Property | CSS Value | Compose Equivalent |
|----------|-----------|-------------------|
| Layout | `flex; align:center; gap:12px; padding:14px 16px` | `Row(CenterVertically, spacedBy(12.dp), padding(h=16.dp,v=14.dp))` |
| Bg | `var(--white)` | `VColors.white` |
| Radius | `var(--r-md)` = `14px` | `VShapes.md` |
| Shadow | `var(--shadow-1)` | `VShadows.shadow1` |
| Margin bottom | `6px` | `padding(bottom=6.dp)` |
| Icon container | `32x32px, radius:10px` | `32.dp Box(10.dp)` |
| Icon size | `16x16px, stroke-width:2` | `16.dp, 2.dp stroke` |
| Settings icon | `bg:surface-tint, stroke:ink-2` | `VColors.surfaceTint / VColors.ink2` |
| Link icon | `bg:violet-soft, stroke:violet` | `VColors.violetSoft / VColors.violet` |
| Discover icon | `bg:sky-soft, stroke:sky` | `VColors.skySoft / VColors.sky` |
| Logout icon | `bg:coral-soft, stroke:coral` | `VColors.coralSoft / VColors.coral` |
| Label font | `13px 700 var(--ink) -0.2px; flex:1` | `13.sp Bold VColors.ink -0.2.sp, weight(1f)` |
| Chevron | `16x16px, stroke:ink-3, stroke-width:2` | `16.dp, VColors.ink3, 2.dp` |

### 4.6 Logout Button (`.prof-logout`)

| Property | CSS Value | Compose Equivalent |
|----------|-----------|-------------------|
| Margin | `0 24px 16px` | `padding(horizontal=24.dp, bottom=16.dp)` |
| Padding | `14px` | `14.dp` |
| Bg | `var(--coral-soft)` | `VColors.coralSoft` |
| Radius | `var(--r-md)` = `14px` | `VShapes.md` |
| Shadow | `var(--shadow-1)` | `VShadows.shadow1` |
| Font | `14px 800 var(--coral)` | `14.sp ExtraBold VColors.coral` |
| Text align | `center` | `TextAlign.Center` |

### 4.7 Profile Tab HTML Structure Summary

```
[Hero Card — centered]
  [Decorative circles: violet (top-right), coral (bottom-left)]
  [Avatar: 64dp, initials "AS", violetSoft bg, white outline + violetSoft ring]
  [Name: "Aarav Sharma" — 18sp ExtraBold]
  [Class: "Class 7-B · Roll 14 · Delhi Public School" — 13sp Medium]
  [House badge: "Red House" — coralSoft bg, 10sp Bold]
[Stats Grid — 2x2]
  [Stat: 94% — Attendance — ↑ 2% this term (success)]
  [Stat: 87.3 — Avg Marks — ↑ 5.1 points (success)]
  [Stat: 1,240 — XP Points — ↑ 180 this week (success)]
  [Stat: 14 — Quizzes Done — 2 pending]
[Section: Account]
  [Row: Settings icon (surfaceTint) — Account Settings — chevron]
  [Row: Link icon (violetSoft) — Link Another Child — chevron]
  [Row: Discover icon (skySoft) — Discover Schools — chevron]
[Logout Button — coralSoft bg, 14sp ExtraBold coral, centered]
```

---

## 5. Bottom Nav (`.bottom-nav` + `.nav-item`)

### 5.1 Nav Bar

| Property | CSS Value | Compose Equivalent |
|----------|-----------|-------------------|
| Layout | `flex; align:center; justify:space-around; padding:10px 4px 16px` | `Row(SpaceAround, CenterVertically, padding(h=4.dp,v_top=10.dp,v_bottom=16.dp))` |
| Bg | `var(--white)` | `VColors.white` |
| Shadow | `0 -1px 0 var(--line-soft)` | `border(top=1.dp, VColors.lineSoft)` |
| Top divider | `::before 1px, gradient(transparent, line, transparent), left:24px right:24px` | `Box(1.dp, horizontalGradient(Transparent, VColors.line, Transparent), padding(h=24.dp))` |

### 5.2 Nav Item

| Property | CSS Value | Compose Equivalent |
|----------|-----------|-------------------|
| Layout | `flex column; align:center; gap:3px; padding:6px 12px` | `Column(CenterHorizontally, spacedBy(3.dp), padding(h=12.dp,v=6.dp))` |
| Radius | `var(--r-md)` = `14px` | `VShapes.md` |
| Icon container | `30x30px, radius:10px` | `30.dp Box(10.dp)` |
| Icon size | `20x20px, stroke-width:2` | `20.dp, 2.dp stroke` |
| Icon color (inactive) | `var(--ink-3)` | `VColors.ink3` |
| Label font | `10px 600 var(--ink-3) -0.2px` | `10.sp SemiBold VColors.ink3` |
| Active icon bg | `var(--violet-soft)` | `VColors.violetSoft` |
| Active icon radius | `var(--r-md)` = `14px` | `VShapes.md` |
| Active icon color | `var(--violet)` | `VColors.violet` |
| Active label color | `var(--violet)` | `VColors.violet` |
| Active label weight | `800` | `FontWeight.ExtraBold` |
| Badge min-width | `16px` | `minWidth=16.dp` |
| Badge height | `16px` | `16.dp` |
| Badge radius | `var(--r-full)` | `CircleShape` |
| Badge bg | `var(--coral)` | `VColors.coral` |
| Badge text | `#fff 9px 800` | `VColors.white 9.sp ExtraBold` |
| Badge padding | `0 4px` | `padding(horizontal=4.dp)` |
| Badge border | `2px solid var(--white)` | `border(2.dp, VColors.white)` |
| Badge position | `top:2px right:6px` | `offset(top=2.dp, right=6.dp)` |
| Press animation | `transform:scale(.95)` | `scale(0.95f)` on press |

### 5.3 Nav Items (5 tabs)

| Tab | Icon | Label | Badge |
|-----|------|-------|-------|
| Home | `icon-home` | "Home" | — |
| Academics | `icon-book` | "Academics" | — |
| Fees | `icon-rupee` | "Fees" | — |
| Conversations | `icon-chat` | "Chats" | `2` (coral) |
| Profile | `icon-user` | "Profile" | — |

---

## 6. Overlay System (shared across all overlays)

### 6.1 Overlay Container (`.overlay`)

| Property | CSS Value | Compose Equivalent |
|----------|-----------|-------------------|
| Position | `absolute; inset:0` | `fillMaxSize()` |
| Bg | `var(--cream)` | `VColors.cream` |
| Z-index | `100` | above tab content |
| Transform (hidden) | `translateX(100%)` | offset animation: full width right |
| Transform (active) | `translateX(0)` | offset: 0 |
| Transition | `transform 300ms var(--ease)` | `300ms VMotion.ease` slide |
| Layout | `flex column; overflow:hidden` | `Column` |

### 6.2 Overlay Header (`.overlay-header`)

| Property | CSS Value | Compose Equivalent |
|----------|-----------|-------------------|
| Layout | `flex; align:center; gap:12px; padding:8px 24px` | `Row(CenterVertically, spacedBy(12.dp), padding(h=24.dp,v=8.dp))` |
| Bg | `var(--white)` | `VColors.white` |
| Shadow | `0 1px 0 var(--line-soft)` | `border(bottom=1.dp, VColors.lineSoft)` |

### 6.3 Overlay Back Button (`.overlay-back`)

| Property | CSS Value | Compose Equivalent |
|----------|-----------|-------------------|
| Size | `32x32px` | `32.dp` |
| Radius | `var(--r-sm)` = `10px` | `VShapes.sm` (10.dp) |
| Bg | `var(--surface-tint)` | `VColors.surfaceTint` |
| Icon size | `18x18px` | `18.dp` |
| Icon stroke | `var(--ink); stroke-width:2` | `VColors.ink, 2.dp` |

### 6.4 Overlay Title (`.overlay-title`)

| Property | CSS Value | Compose Equivalent |
|----------|-----------|-------------------|
| Font size | `16px` | `16.sp` |
| Font weight | `800` | `FontWeight.ExtraBold` |
| Color | `var(--ink)` | `VColors.ink` |
| Letter spacing | `-.3px` | `-0.3.sp` |

### 6.5 Overlay Body (`.overlay-body`)

| Property | CSS Value | Compose Equivalent |
|----------|-----------|-------------------|
| Layout | `flex:1; overflow-y:auto` | `verticalScroll(rememberScrollState()).weight(1f)` |
| Padding bottom | `var(--s-lg)` = `24px` | `padding(bottom=24.dp)` |
| Scrollbar | `hidden` | no scrollbar (default in Compose) |

### 6.6 Overlay Card (`.ov-card`)

| Property | CSS Value | Compose Equivalent |
|----------|-----------|-------------------|
| Bg | `var(--white)` | `VColors.white` |
| Radius | `var(--r-md)` = `14px` | `VShapes.md` |
| Shadow | `var(--shadow-1)` | `VShadows.shadow1` |
| Padding | `16px` | `16.dp` |
| Margin | `8px 24px` | `padding(horizontal=24.dp, vertical=8.dp)` |
| Title font | `13px 800 var(--ink) -0.2px; margin-bottom:12px` | `13.sp ExtraBold VColors.ink -0.2.sp, padding(bottom=12.dp)` |
| Row layout | `flex; justify:space-between; padding:8px 0` | `Row(SpaceBetween, padding(vertical=8.dp))` |
| Row border | `1px solid var(--line-soft) (not last)` | `border(bottom=1.dp, VColors.lineSoft)` |
| Label font | `13px 600 var(--ink-2)` | `13.sp SemiBold VColors.ink2` |
| Value font | `13px 700 var(--ink)` | `13.sp Bold VColors.ink` |

---

## 7. Overlay-Specific Components

### 7.1 Transport Tracking (`.ov-track`)

| Property | CSS Value | Compose Equivalent |
|----------|-----------|-------------------|
| Bg | `var(--white)` | `VColors.white` |
| Radius | `var(--r-md)` = `14px` | `VShapes.md` |
| Shadow | `var(--shadow-1)` | `VShadows.shadow1` |
| Padding | `20px` | `20.dp` |
| Margin | `8px 24px` | `padding(horizontal=24.dp, vertical=8.dp)` |
| Text align | `center` | `CenterHorizontally` |
| Status font | `12px 700 var(--success) uppercase; letter-spacing:1px` | `12.sp Bold VColors.success uppercase 1.sp` |
| ETA font | `28px 800 var(--ink) -0.5px; margin-top:6px` | `28.sp ExtraBold VColors.ink -0.5.sp, padding(top=6.dp)` |
| Sub font | `12px 500 var(--ink-3); margin-top:4px` | `12.sp Medium VColors.ink3, padding(top=4.dp)` |
| Route layout | `flex; justify:center; gap:8px; margin-top:16px` | `Row(SpaceEvenly, spacedBy(8.dp), padding(top=16.dp))` |
| Route font | `12px 600 var(--ink-2)` | `12.sp SemiBold VColors.ink2` |
| Dot | `8x8px; radius:50%; bg:success` | `8.dp CircleShape VColors.success` |
| Line | `40x2px; bg:success; opacity:.4` | `40.dp x 2.dp, VColors.success.copy(alpha=0.4f)` |

### 7.2 Boarded Banner (`.ov-boarded`)

| Property | CSS Value | Compose Equivalent |
|----------|-----------|-------------------|
| Bg | `var(--mint-soft)` | `VColors.mintSoft` |
| Radius | `var(--r-md)` = `14px` | `VShapes.md` |
| Padding | `12px 16px` | `padding(horizontal=16.dp, vertical=12.dp)` |
| Margin | `8px 24px` | `padding(horizontal=24.dp, vertical=8.dp)` |
| Layout | `flex; align:center; gap:10px` | `Row(CenterVertically, spacedBy(10.dp))` |
| Icon container | `28x28px; radius:50%; bg:success` | `28.dp CircleShape VColors.success` |
| Icon size | `14x14px; stroke:#fff; stroke-width:3` | `14.dp, VColors.white, 3.dp` |
| Text font | `12px 700 var(--success)` | `12.sp Bold VColors.success` |
| Time font | `11px 500 var(--ink-3); margin-top:1px` | `11.sp Medium VColors.ink3, padding(top=1.dp)` |

### 7.3 Pulse Score (`.ov-pulse`)

| Property | CSS Value | Compose Equivalent |
|----------|-----------|-------------------|
| Bg | `var(--white)` | `VColors.white` |
| Radius | `var(--r-md)` = `14px` | `VShapes.md` |
| Shadow | `var(--shadow-1)` | `VShadows.shadow1` |
| Padding | `24px` | `24.dp` |
| Margin | `8px 24px` | `padding(horizontal=24.dp, vertical=8.dp)` |
| Text align | `center` | `CenterHorizontally` |
| Score font | `48px 800 var(--success) -1px; line-height:1` | `48.sp ExtraBold VColors.success -1.sp` |
| Label font | `12px 700 var(--ink-3) uppercase; letter-spacing:1px; margin-top:6px` | `12.sp Bold VColors.ink3 uppercase 1.sp, padding(top=6.dp)` |
| Ring size | `120x120px; radius:50%; border:6px mint-soft` | `120.dp CircleShape, 6.dp border VColors.mintSoft` |
| Ring margin | `16px auto` | `padding(vertical=16.dp)` |
| Ring fill | `::before border:6px success; clip-path:polygon(0 0,100% 0,100% 85%,0 85%)` | `VProgressRing(progress=0.85f, color=VColors.success)` |

### 7.4 Chat Bubble (`.ov-bubble`)

| Property | CSS Value | Compose Equivalent |
|----------|-----------|-------------------|
| Max width | `80%` | `fillMaxWidth(0.8f)` |
| Padding | `10px 14px` | `padding(horizontal=14.dp, vertical=10.dp)` |
| Radius | `var(--r-md)` = `14px` | `VShapes.md` |
| Font | `13px 500; line-height:1.5` | `13.sp Medium, lineHeight=1.5` |
| Tutor bubble | `bg:white; shadow-1; align-self:flex-start; bottom-left-radius:4px; color:ink` | `VColors.white, VShadows.shadow1, start align, bottomStart=4.dp, VColors.ink` |
| User bubble | `bg:violet; color:#fff; align-self:flex-end; bottom-right-radius:4px` | `VColors.violet, VColors.white, end align, bottomEnd=4.dp` |

### 7.5 Chat Chips (`.ov-chip`)

| Property | CSS Value | Compose Equivalent |
|----------|-----------|-------------------|
| Font | `11px 600` | `11.sp SemiBold` |
| Padding | `6px 12px` | `padding(horizontal=12.dp, vertical=6.dp)` |
| Radius | `var(--r-full)` | `CircleShape` |
| Bg | `var(--surface-tint)` | `VColors.surfaceTint` |
| Color | `var(--ink-2)` | `VColors.ink2` |
| Hover bg | `var(--violet-soft)` | `VColors.violetSoft` |
| Hover color | `var(--violet)` | `VColors.violet` |

### 7.6 Chat Input Bar (`.ov-chat-input`)

| Property | CSS Value | Compose Equivalent |
|----------|-----------|-------------------|
| Layout | `flex; gap:8px; padding:8px 24px` | `Row(spacedBy(8.dp), padding(h=24.dp,v=8.dp))` |
| Bg | `var(--white)` | `VColors.white` |
| Shadow | `0 -1px 0 var(--line-soft)` | `border(top=1.dp, VColors.lineSoft)` |
| Input padding | `10px 14px` | `padding(horizontal=14.dp, vertical=10.dp)` |
| Input border | `1px solid var(--line)` | `border(1.dp, VColors.line)` |
| Input radius | `var(--r-md)` = `14px` | `VShapes.md` |
| Input font | `13px` | `13.sp` |
| Input focus border | `var(--violet)` | `VColors.violet` |
| Send button | `38x38px; radius:14px; bg:violet` | `38.dp, VShapes.md, VColors.violet` |
| Send icon | `16x16px; stroke:#fff; stroke-width:2` | `16.dp, VColors.white, 2.dp` |

### 7.7 Scholarship Item (`.ov-sch-item`)

| Property | CSS Value | Compose Equivalent |
|----------|-----------|-------------------|
| Bg | `var(--white)` | `VColors.white` |
| Radius | `var(--r-md)` = `14px` | `VShapes.md` |
| Shadow | `var(--shadow-1)` | `VShadows.shadow1` |
| Padding | `16px` | `16.dp` |
| Margin | `0 24px 8px` | `padding(horizontal=24.dp, bottom=8.dp)` |
| Name font | `14px 800 var(--ink) -0.2px` | `14.sp ExtraBold VColors.ink -0.2.sp` |
| Amount font | `16px 800 var(--violet); margin-top:4px` | `16.sp ExtraBold VColors.violet, padding(top=4.dp)` |
| Meta font | `11px 500 var(--ink-3); margin-top:4px` | `11.sp Medium VColors.ink3, padding(top=4.dp)` |
| Status font | `10px 800; padding:3px 10px; radius:full; margin-top:8px` | `10.sp ExtraBold, padding(h=10.dp,v=3.dp), CircleShape, padding(top=8.dp)` |
| Status eligible | `bg:mint-soft, color:success` | `VColors.mintSoft / VColors.success` |
| Status applied | `bg:sky-soft, color:sky` | `VColors.skySoft / VColors.sky` |
| Status not | `bg:surface-tint, color:ink-3` | `VColors.surfaceTint / VColors.ink3` |

### 7.8 Digital ID Card (`.ov-id`)

| Property | CSS Value | Compose Equivalent |
|----------|-----------|-------------------|
| Bg | `var(--white)` | `VColors.white` |
| Radius | `var(--r-lg)` = `18px` | `VShapes.lg` (18.dp) |
| Shadow | `var(--shadow-2)` | `VShadows.shadow2` |
| Padding | `24px` | `24.dp` |
| Margin | `16px 24px` | `padding(horizontal=24.dp, vertical=16.dp)` |
| Text align | `center` | `CenterHorizontally` |
| Decorative circle | `::before 100x100px, violetSoft, opacity:.4, top:-30 right:-30` | `Box(100.dp, CircleShape, VColors.violetSoft.copy(alpha=0.4f))` |
| School font | `12px 700 var(--violet) uppercase; letter-spacing:1px` | `12.sp Bold VColors.violet uppercase 1.sp` |
| Avatar | `72x72px; radius:full; bg:violetSoft; 24sp 800 violet; margin:16px auto 12px; outline:3px white; shadow:0 0 0 3px violetSoft` | `72.dp CircleShape VColors.violetSoft 24.sp ExtraBold, padding(v=16.dp,b=12.dp), border(3.dp,white), ring(3.dp,violetSoft)` |
| Name font | `18px 800 var(--ink) -0.3px` | `18.sp ExtraBold VColors.ink -0.3.sp` |
| Class font | `13px 500 var(--ink-3); margin-top:2px` | `13.sp Medium VColors.ink3, padding(top=2.dp)` |
| QR code | `120x120px; bg:surface-tint; radius:14px; margin:20px auto 12px` | `120.dp Box(14.dp, VColors.surfaceTint), padding(v=20.dp,b=12.dp)` |
| Valid font | `11px 600 var(--ink-3)` | `11.sp SemiBold VColors.ink3` |

### 7.9 Library Item (`.ov-lib-item`)

| Property | CSS Value | Compose Equivalent |
|----------|-----------|-------------------|
| Layout | `flex; align:center; gap:12px; padding:12px 0` | `Row(CenterVertically, spacedBy(12.dp), padding(vertical=12.dp))` |
| Border (not last) | `1px solid var(--line-soft)` | `border(bottom=1.dp, VColors.lineSoft)` |
| Icon container | `34x34px; radius:10px; bg:sky-soft` | `34.dp Box(10.dp, VColors.skySoft)` |
| Icon size | `15x15px; stroke:sky` | `15.dp, VColors.sky` |
| Title font | `13px 700 var(--ink) -0.2px` | `13.sp Bold VColors.ink -0.2.sp` |
| Due font | `11px 500 var(--ink-3); margin-top:1px` | `11.sp Medium VColors.ink3, padding(top=1.dp)` |
| Badge font | `9px 800; padding:2px 7px; radius:full` | `9.sp ExtraBold, padding(h=7.dp,v=2.dp), CircleShape` |
| Badge ok | `bg:mint-soft, color:success` | `VColors.mintSoft / VColors.success` |
| Badge due | `bg:coral-soft, color:coral` | `VColors.coralSoft / VColors.coral` |

### 7.10 Event Item (`.ov-evt-item`)

| Property | CSS Value | Compose Equivalent |
|----------|-----------|-------------------|
| Bg | `var(--white)` | `VColors.white` |
| Radius | `var(--r-md)` = `14px` | `VShapes.md` |
| Shadow | `var(--shadow-1)` | `VShadows.shadow1` |
| Padding | `16px` | `16.dp` |
| Margin | `0 24px 8px` | `padding(horizontal=24.dp, bottom=8.dp)` |
| Left bar | `::before 3px, var(--violet)` | `border(start=3.dp, VColors.violet)` |
| Date font | `10px 700 var(--violet) uppercase; letter-spacing:.5px` | `10.sp Bold VColors.violet uppercase 0.5.sp` |
| Name font | `14px 800 var(--ink) -0.2px; margin-top:4px` | `14.sp ExtraBold VColors.ink -0.2.sp, padding(top=4.dp)` |
| Desc font | `12px 500 var(--ink-2); margin-top:4px; line-height:1.4` | `12.sp Medium VColors.ink2, padding(top=4.dp)` |
| Status font | `10px 800; padding:3px 10px; radius:full; margin-top:8px` | `10.sp ExtraBold, padding(h=10.dp,v=3.dp), CircleShape, padding(top=8.dp)` |
| Status open | `bg:violet-soft, color:violet` | `VColors.violetSoft / VColors.violet` |
| Status registered | `bg:mint-soft, color:success` | `VColors.mintSoft / VColors.success` |
| Status closed | `bg:surface-tint, color:ink-3` | `VColors.surfaceTint / VColors.ink3` |

### 7.11 Notification Item (`.ov-notif`)

| Property | CSS Value | Compose Equivalent |
|----------|-----------|-------------------|
| Layout | `flex; gap:12px; padding:14px 16px` | `Row(spacedBy(12.dp), padding(h=16.dp,v=14.dp))` |
| Bg | `var(--white)` | `VColors.white` |
| Radius | `var(--r-md)` = `14px` | `VShapes.md` |
| Shadow | `var(--shadow-1)` | `VShadows.shadow1` |
| Margin | `0 24px 6px` | `padding(horizontal=24.dp, bottom=6.dp)` |
| Unread left bar | `border-left:3px solid var(--violet)` | `border(start=3.dp, VColors.violet)` |
| Icon container | `34x34px; radius:10px` | `34.dp Box(10.dp)` |
| Icon size | `15x15px; stroke-width:2` | `15.dp, 2.dp stroke` |
| Bell icon | `bg:violet-soft, stroke:violet` | `VColors.violetSoft / VColors.violet` |
| Fee icon | `bg:coral-soft, stroke:coral` | `VColors.coralSoft / VColors.coral` |
| Msg icon | `bg:sky-soft, stroke:sky` | `VColors.skySoft / VColors.sky` |
| Evt icon | `bg:mint-soft, stroke:success` | `VColors.mintSoft / VColors.success` |
| Title font | `13px 700 var(--ink) -0.2px` | `13.sp Bold VColors.ink -0.2.sp` |
| Body font | `12px 500 var(--ink-3); margin-top:2px; line-height:1.4` | `12.sp Medium VColors.ink3, padding(top=2.dp)` |
| Time font | `10px 600 var(--ink-3); margin-top:3px` | `10.sp SemiBold VColors.ink3, padding(top=3.dp)` |

---

*End of PARENT_PORTAL_CSS_SPEC.md*
