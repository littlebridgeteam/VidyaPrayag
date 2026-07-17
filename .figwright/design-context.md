# Design Context: Admin Dashboard — Saraswati Vidya Mandir

**Generated from image description.** Exact dimensions below are best-estimate for a 412px-wide Android phone frame. Where possible, verify pixel values against the source asset before production use.

---

## 1. Screen Metadata

- Image file: `chat-uploaded-image`
- Screen name: Admin Home Dashboard
- Device type: phone
- Screen dimensions: 412px × 915px (estimated, content scrolls)
- Theme: Light
- Background color: `#F8FAFC`
- Safe area / status bar: visible at top, height ~24px, background matches page (`#F8FAFC`) or transparent
- Bottom navigation bar: not visible in frame

---

## 2. Color Palette

| Token Name | Hex | RGB | Opacity | Usage |
|---|---|---|---|---|
| Page Background | `#F8FAFC` | 248,250,252 | 100% | Root scroll container |
| Surface | `#FFFFFF` | 255,255,255 | 100% | Cards, search bar, stat cards |
| Primary Ink | `#1E293B` | 30,41,59 | 100% | Headings, primary numbers, primary text |
| Secondary Ink | `#64748B` | 100,116,139 | 100% | Subtitles, descriptions, hint text |
| Muted Ink | `#94A3B8` | 148,163,184 | 100% | Timestamps, tertiary labels |
| Primary Purple | `#7C3AED` | 124,58,237 | 100% | Primary accents, active icons, graph lines |
| Primary Blue | `#3B82F6` | 59,130,246 | 100% | Secondary accents, gradient end |
| Gradient Hero Start | `#7C3AED` | 124,58,237 | 100% | Hero card top-left |
| Gradient Hero End | `#3B82F6` | 59,130,246 | 100% | Hero card bottom-right |
| AI Banner Start | `#8B5CF6` | 139,92,246 | 100% | AI insight banner |
| AI Banner End | `#6366F1` | 99,102,241 | 100% | AI insight banner |
| Success Green | `#22C55E` | 34,197,94 | 100% | Attendance icon/ring |
| Success Green Light | `#DCFCE7` | 220,252,231 | 100% | Attendance icon background tint |
| Urgent Red | `#EF4444` | 239,68,68 | 100% | Notification badges, urgent icons |
| Urgent Red Light | `#FEE2E2` | 254,226,226 | 100% | Urgent card tint / badge background |
| Purple Light | `#EDE9FE` | 237,233,254 | 100% | Stat icon backgrounds, subtle tints |
| Star Yellow | `#FBBF24` | 251,191,36 | 100% | Star icon in school health |
| Hairline | `#E2E8F0` | 226,232,240 | 100% | Dividers, borders |

### Gradients

- **Hero Card Gradient**
  - Type: linear
  - Direction: 135° (top-left to bottom-right)
  - Stops: `[(0.0, #7C3AED), (1.0, #3B82F6)]`

- **AI Insight Banner**
  - Type: linear
  - Direction: 135°
  - Stops: `[(0.0, #8B5CF6), (1.0, #6366F1)]`

---

## 3. Typography

Assumed font family: **Plus Jakarta Sans** for headings and body, **DM Mono** for numeric data.

| ID | Text Content | Font Family | Size (px) | Weight | Line Height | Letter Spacing | Color | Align | Max Lines | Truncation |
|---|---|---|---|---|---|---|---|---|---|---|
| T1 | "Good morning, Admin" | Plus Jakarta Sans | 22 | SemiBold | 28 | -0.3 | #1E293B | left | 1 | none |
| T2 | "Saraswati Vidya Mandir" | Plus Jakarta Sans | 14 | Medium | 20 | 0 | #1E293B | left | 1 | none |
| T3 | "Search students, events, announcements..." | Plus Jakarta Sans | 14 | Regular | 20 | 0 | #94A3B8 | left | 1 | none |
| T4 | "Here's what's happening today ✨" | Plus Jakarta Sans | 16 | SemiBold | 22 | 0 | #FFFFFF | left | 2 | none |
| T5 | "Stay ahead, make an impact." | Plus Jakarta Sans | 13 | Regular | 18 | 0 | #E0E7FF | left | 2 | none |
| T6 | "Thu, 17 Jul 2026" | Plus Jakarta Sans | 12 | Regular | 16 | 0 | #FFFFFF | left | 1 | none |
| T7 | "28°C • Sunny" | Plus Jakarta Sans | 12 | Regular | 16 | 0 | #FFFFFF | left | 1 | none |
| T8 | "School Health 82%" | Plus Jakarta Sans | 13 | SemiBold | 18 | 0 | #FFFFFF | left | 1 | none |
| T9 | "Priority Today 🚨" | Plus Jakarta Sans | 16 | SemiBold | 22 | 0 | #1E293B | left | 1 | none |
| T10 | "Review 2 pending notifications" | Plus Jakarta Sans | 14 | SemiBold | 20 | 0 | #EF4444 | left | 1 | none |
| T11 | "Urgent updates from class 8-B & 5 parents" | Plus Jakarta Sans | 12 | Regular | 16 | 0 | #64748B | left | 2 | none |
| T12 | "Review Now →" | Plus Jakarta Sans | 13 | SemiBold | 18 | 0 | #FFFFFF | left | 1 | none |
| T13 | "Announce" | Plus Jakarta Sans | 11 | Medium | 14 | 0 | #64748B | center | 1 | none |
| T14 | "Add Event" | Plus Jakarta Sans | 11 | Medium | 14 | 0 | #64748B | center | 1 | none |
| T15 | "Reports" | Plus Jakarta Sans | 11 | Medium | 14 | 0 | #64748B | center | 1 | none |
| T16 | "Calendar" | Plus Jakarta Sans | 11 | Medium | 14 | 0 | #64748B | center | 1 | none |
| T17 | "More" | Plus Jakarta Sans | 11 | Medium | 14 | 0 | #64748B | center | 1 | none |
| T18 | "248" | Plus Jakarta Sans | 28 | Bold | 34 | -0.5 | #1E293B | left | 1 | none |
| T19 | "+12" | Plus Jakarta Sans | 11 | SemiBold | 14 | 0 | #22C55E | left | 1 | none |
| T20 | "Students" | Plus Jakarta Sans | 12 | Regular | 16 | 0 | #64748B | left | 1 | none |
| T21 | "This Week" | Plus Jakarta Sans | 11 | Regular | 14 | 0 | #94A3B8 | left | 1 | none |
| T22 | "18" | Plus Jakarta Sans | 28 | Bold | 34 | -0.5 | #1E293B | left | 1 | none |
| T23 | "+3" | Plus Jakarta Sans | 11 | SemiBold | 14 | 0 | #22C55E | left | 1 | none |
| T24 | "Teachers" | Plus Jakarta Sans | 12 | Regular | 16 | 0 | #64748B | left | 1 | none |
| T25 | "94%" | Plus Jakarta Sans | 24 | Bold | 30 | -0.3 | #1E293B | left | 1 | none |
| T26 | "+4%" | Plus Jakarta Sans | 11 | SemiBold | 14 | 0 | #22C55E | left | 1 | none |
| T27 | "Attendance" | Plus Jakarta Sans | 12 | Regular | 16 | 0 | #64748B | left | 1 | none |
| T28 | "₹4.2L" | Plus Jakarta Sans | 24 | Bold | 30 | -0.3 | #1E293B | left | 1 | none |
| T29 | "78% of total" | Plus Jakarta Sans | 12 | Regular | 16 | 0 | #64748B | left | 1 | none |
| T30 | "Fees Collected" | Plus Jakarta Sans | 12 | Regular | 16 | 0 | #64748B | left | 1 | none |
| T31 | "AI Insight" | Plus Jakarta Sans | 11 | SemiBold | 14 | 0.05em | #FFFFFF | left | 1 | none |
| T32 | "Class 8-B fees collection is 15% behind average. A reminder to parents might help." | Plus Jakarta Sans | 13 | Regular | 18 | 0 | #FFFFFF | left | 2 | none |
| T33 | "Send Reminder" | Plus Jakarta Sans | 12 | SemiBold | 16 | 0 | #7C3AED | left | 1 | none |
| T34 | "Upcoming Events" | Plus Jakarta Sans | 16 | SemiBold | 22 | 0 | #1E293B | left | 1 | none |
| T35 | "View all" | Plus Jakarta Sans | 12 | SemiBold | 16 | 0 | #7C3AED | right | 1 | none |
| T36 | "3d Annual Sports Day 20 Jul 2026" | Plus Jakarta Sans | 13 | Medium | 18 | 0 | #1E293B | left | 1 | none |
| T37 | "7d Independence Day Holiday 15 Aug 2026" | Plus Jakarta Sans | 13 | Medium | 18 | 0 | #1E293B | left | 1 | none |
| T38 | "Recent Activity" | Plus Jakarta Sans | 16 | SemiBold | 22 | 0 | #1E293B | left | 1 | none |
| T39 | "Attendance marked for Class 8-B 2h ago" | Plus Jakarta Sans | 13 | Medium | 18 | 0 | #1E293B | left | 1 | none |
| T40 | "New announcement posted 5h ago" | Plus Jakarta Sans | 13 | Medium | 18 | 0 | #1E293B | left | 1 | none |

### Text Case / Styling Notes

- T1: sentence case, includes waving hand emoji (`👋`)
- T2: title case, accompanied by downward chevron icon (dropdown)
- T4: sentence case, includes sparkle emoji (`✨`)
- T8: title case + percentage
- T9: title case, includes siren/urgent emoji (`🚨`)
- T12: title case, includes right arrow (`→`)
- T31: uppercase label (all caps with 0.05em letter-spacing)
- All body text left-aligned; "View all" links right-aligned within their headers
- No inline bold/color spans observed except emojis

---

## 4. Layout Tree

```
Root Container (Screen Scroll)
├─ Type: Column (vertical scroll)
├─ Width: 412px (fill)
├─ Height: wrap content (scrollable)
├─ Padding: 0px (top), 0px (bottom), 0px (left), 0px (right)
├─ Background: #F8FAFC
├─ Gap between children: 0px
│
├─ Section: Status Bar
│  ├─ Type: StatusBar (system)
│  ├─ Height: 24px
│  ├─ Background: transparent / #F8FAFC
│
├─ Section: Header
│  ├─ Type: Column (vertical)
│  ├─ Width: 412px (fill)
│  ├─ Padding: 16px (left), 16px (right), 16px (top), 8px (bottom)
│  ├─ Background: transparent (page shows through)
│  ├─ Gap between children: 8px
│  │
│  ├─ Row: Top Bar
│  │  ├─ Type: Row (horizontal)
│  │  ├─ Width: 380px (fill)
│  │  ├─ Height: wrap
│  │  ├─ Padding: 0px
│  │  ├─ Background: transparent
│  │  ├─ Children alignment: space-between
│  │  ├─ Gap: 12px
│  │  │
│  │  ├─ Column: Greeting Block
│  │  │  ├─ Type: Column (vertical)
│  │  │  ├─ Width: wrap
│  │  │  ├─ Height: wrap
│  │  │  ├─ Gap: 2px
│  │  │  ├─ Text T1: "Good morning, Admin 👋"
│  │  │  └─ Row: Institution Selector
│  │  │     ├─ Text T2: "Saraswati Vidya Mandir"
│  │  │     └─ Icon: chevron-down (12×12, #1E293B, trailing)
│  │  │
│  │  └─ Row: Right Actions
│  │     ├─ Type: Row (horizontal)
│  │     ├─ Gap: 12px
│  │     ├─ Element: Profile Avatar
│  │     │  ├─ Type: Circle image
│  │     │  ├─ Size: 40px × 40px
│  │     │  ├─ Corner radius: 999px
│  │     │  ├─ Border: 2px solid #FFFFFF
│  │     │  └─ Image: male user photo (placeholder if unavailable)
│  │     │
│  │     └─ Element: Notification Bell
│  │        ├─ Type: Icon button
│  │        ├─ Size: 40px × 40px (touch target)
│  │        ├─ Background: #FFFFFF
│  │        ├─ Corner radius: 999px
│  │        ├─ Icon: bell (20×20, #1E293B)
│  │        └─ Badge: red circle 18×18, number "2" in white 10px SemiBold, top-right offset -2px
│  │
│  └─ Row: Search Bar
│     ├─ Type: Row (horizontal)
│     ├─ Width: 380px (fill)
│     ├─ Height: 48px
│     ├─ Padding: 12px (left), 12px (right), 0px (top), 0px (bottom)
│     ├─ Background: #FFFFFF
│     ├─ Corner radius: 12px
│     ├─ Border: none
│     ├─ Shadow: drop-shadow dy=1 blur=3 color=#1E293B 4%
│     ├─ Children alignment: space-between
│     │
│     ├─ Text T3: "Search students, events, announcements..."
│     └─ Icon: sliders/filter (20×20, #94A3B8)
│
├─ Section: Hero Card
│  ├─ Type: Column (vertical)
│  ├─ Width: 380px (fill minus 16px each side)
│  ├─ Height: 200px (estimated)
│  ├─ Margin: 0px (left), 0px (right), 16px (top), 0px (bottom)
│  ├─ Padding: 20px (all sides)
│  ├─ Background: gradient (#7C3AED → #3B82F6, 135°)
│  ├─ Corner radius: 24px
│  ├─ Shadow: drop-shadow dy=8 blur=24 color=#7C3AED 15%
│  ├─ Overflow: hidden (abstract 3D shapes positioned absolutely in background)
│  ├─ Gap between children: 8px
│  │
│  ├─ Text T4: "Here's what's happening today ✨"
│  ├─ Text T5: "Stay ahead, make an impact."
│  ├─ Row: Date + Weather
│  │  ├─ Type: Row (horizontal)
│  │  ├─ Width: wrap
│  │  ├─ Gap: 16px
│  │  ├─ Text T6: "Thu, 17 Jul 2026"
│  │  └─ Text T7: "28°C • Sunny"
│  │
│  ├─ Row: School Health
│  │  ├─ Type: Row (horizontal)
│  │  ├─ Width: wrap
│  │  ├─ Gap: 6px
│  │  ├─ Icon: heart (16×16, #FFFFFF)
│  │  ├─ Text T8: "School Health 82%"
│  │  └─ Icon: star (16×16, #FBBF24)
│  │
│  └─ Decorative Layer (absolute)
│     ├─ Abstract translucent 3D shapes (right side)
│     ├─ Small line graph
│     └─ Small star accents
│
├─ Section: Priority Card
│  ├─ Type: Row (horizontal)
│  ├─ Width: 380px (fill)
│  ├─ Height: 140px (estimated)
│  ├─ Margin: 16px (left), 16px (right), 16px (top), 0px (bottom)
│  ├─ Padding: 16px (all sides)
│  ├─ Background: #FFFFFF
│  ├─ Corner radius: 20px
│  ├─ Border: none
│  ├─ Shadow: drop-shadow dy=2 blur=8 color=#1E293B 6%
│  ├─ Children alignment: space-between
│  ├─ Gap: 12px
│  │
│  ├─ Column: Text Content
│  │  ├─ Type: Column (vertical)
│  │  ├─ Width: 220px
│  │  ├─ Height: wrap
│  │  ├─ Gap: 4px
│  │  ├─ Text T9: "Priority Today 🚨"
│  │  ├─ Text T10: "Review 2 pending notifications"
│  │  ├─ Text T11: "Urgent updates from class 8-B & 5 parents"
│  │  └─ Button: "Review Now →"
│  │     ├─ Width: wrap, Height: 36px
│  │     ├─ Padding: 8px (left), 12px (right), 8px (top), 8px (bottom)
│  │     ├─ Background: #EF4444
│  │     ├─ Corner radius: 999px (pill)
│  │     ├─ Text T12: "Review Now →" (#FFFFFF)
│  │
│  └─ Visual: Bell with Badge + Avatars
│     ├─ Type: Decorative graphic / icon group
│     ├─ Width: 120px, Height: 120px
│     ├─ Bell icon: ~80×80, translucent red tint
│     ├─ Red badge with "2" overlapping bell
│     └─ 3 small circular avatars (~28×28 each) stacked/dotted around bell
│
├─ Section: Quick Actions
│  ├─ Type: Row (horizontal, scrollable)
│  ├─ Width: 412px (fill)
│  ├─ Height: 80px
│  ├─ Padding: 0px (top), 0px (bottom), 0px (left), 16px (right)
│  ├─ Background: transparent
│  ├─ Gap between children: 16px
│  │
│  ├─ QuickAction (×5)
│  │  ├─ Type: Column (vertical, centered)
│  │  ├─ Width: 64px
│  │  ├─ Height: wrap
│  │  ├─ Gap: 8px
│  │  ├─ Circle Icon Container
│  │  │  ├─ Size: 52px × 52px
│  │  │  ├─ Background: #FFFFFF
│  │  │  ├─ Corner radius: 999px
│  │  │  ├─ Shadow: drop-shadow dy=1 blur=4 color=#1E293B 6%
│  │  │  └─ Icon: 24×24 (color per action)
│  │  └─ Text T13-T17 (labels)
│  │
│  ├─ Icons:
│  │  ├─ Announce: megaphone (#7C3AED)
│  │  ├─ Add Event: calendar-plus (#7C3AED)
│  │  ├─ Reports: document-chart (#7C3AED)
│  │  ├─ Calendar: calendar (#7C3AED)
│  │  └─ More: more-horizontal / three-dots (#64748B)
│
├─ Section: Stats Grid
│  ├─ Type: Grid (2 columns)
│  ├─ Width: 380px (fill)
│  ├─ Margin: 16px (left), 16px (right), 16px (top), 0px (bottom)
│  ├─ Column gap: 12px
│  ├─ Row gap: 12px
│  │
│  ├─ Card: Students
│  │  ├─ Type: Column (vertical)
│  │  ├─ Width: 184px
│  │  ├─ Padding: 16px
│  │  ├─ Background: #FFFFFF
│  │  ├─ Corner radius: 20px
│  │  ├─ Shadow: drop-shadow dy=2 blur=8 color=#1E293B 5%
│  │  ├─ Gap: 12px
│  │  │
│  │  ├─ Row: Icon + Growth
│  │  │  ├─ Circle background: 40×40, #EDE9FE
│  │  │  ├─ Icon: two people / users (#7C3AED, 20×20)
│  │  │  └─ Row: arrow-up-right + Text T19 "+12" (#22C55E)
│  │  │
│  │  ├─ Text T18: "248"
│  │  ├─ Text T20: "Students"
│  │  ├─ Row: timeframe + dropdown
│  │  │  ├─ Text T21: "This Week"
│  │  │  └─ Icon: chevron-down (10×10, #94A3B8)
│  │  └─ Sparkline graph (purple #7C3AED, 2px stroke, no fill)
│  │
│  ├─ Card: Teachers
│  │  ├─ Same structure as Students card
│  │  ├─ Circle background: #EDE9FE
│  │  ├─ Icon: graduation-cap (#7C3AED, 20×20)
│  │  ├─ Text T22: "18"
│  │  ├─ Text T23: "+3"
│  │  ├─ Text T24: "Teachers"
│  │  ├─ Sparkline graph (purple #7C3AED)
│  │
│  ├─ Card: Attendance
│  │  ├─ Type: Row (horizontal)
│  │  ├─ Width: 184px
│  │  ├─ Padding: 16px
│  │  ├─ Background: #FFFFFF
│  │  ├─ Corner radius: 20px
│  │  ├─ Shadow: drop-shadow dy=2 blur=8 color=#1E293B 5%
│  │  ├─ Gap: 12px
│  │  │
│  │  ├─ Column: Text
│  │  │  ├─ Icon container: 40×40, #DCFCE7, icon: user-check (#22C55E, 20×20)
│  │  │  ├─ Text T25: "94%"
│  │  │  ├─ Row: arrow-up-right + "+4%" (#22C55E)
│  │  │  └─ Text T27: "Attendance"
│  │  └─ Progress Ring
│  │     ├─ Size: 56×56
│  │     ├─ Stroke: 6px
│  │     ├─ Track: #E2E8F0
│  │     ├─ Fill: #22C55E at 94%
│  │     └─ Cap: round
│  │
│  └─ Card: Fees Collected
│     ├─ Type: Row (horizontal)
│     ├─ Width: 184px
│     ├─ Padding: 16px
│     ├─ Background: #FFFFFF
│     ├─ Corner radius: 20px
│     ├─ Shadow: drop-shadow dy=2 blur=8 color=#1E293B 5%
│     ├─ Gap: 12px
│     │
│     ├─ Column: Text
│     │  ├─ Icon container: 40×40, #EDE9FE, icon: wallet (#7C3AED, 20×20)
│     │  ├─ Text T28: "₹4.2L"
│     │  ├─ Text T29: "78% of total"
│     │  └─ Text T30: "Fees Collected"
│     └─ Progress Ring
│        ├─ Size: 56×56
│        ├─ Stroke: 6px
│        ├─ Track: #E2E8F0
│        ├─ Fill: #7C3AED at 78%
│        └─ Cap: round
│
├─ Section: AI Insight Banner
│  ├─ Type: Row (horizontal)
│  ├─ Width: 380px (fill)
│  ├─ Height: wrap
│  ├─ Margin: 16px (left), 16px (right), 16px (top), 0px (bottom)
│  ├─ Padding: 16px (all sides)
│  ├─ Background: gradient (#8B5CF6 → #6366F1, 135°)
│  ├─ Corner radius: 20px
│  ├─ Shadow: drop-shadow dy=4 blur=12 color=#8B5CF6 12%
│  ├─ Children alignment: space-between
│  ├─ Gap: 12px
│  │
│  ├─ Column: Content
│  │  ├─ Type: Column (vertical)
│  │  ├─ Width: 260px
│  │  ├─ Gap: 4px
│  │  ├─ Badge: "AI Insight" (T31)
│  │  ├─ Text T32: "Class 8-B fees collection is 15% behind average. A reminder to parents might help."
│  │  └─ Button: "Send Reminder"
│  │     ├─ Width: wrap, Height: 32px
│  │     ├─ Padding: 8px (left), 12px (right), 8px (top), 8px (bottom)
│  │     ├─ Background: #FFFFFF
│  │     ├─ Corner radius: 999px
│  │     └─ Text T33: "Send Reminder" (#7C3AED)
│  │
│  └─ Visual: Friendly robot icon
│     ├─ Size: 64×64
│     ├─ Position: right side
│     └─ Style: flat/3D illustration
│
├─ Section: Events & Activity
│  ├─ Type: Row (horizontal)
│  ├─ Width: 380px (fill)
│  ├─ Margin: 16px (left), 16px (right), 16px (top), 0px (bottom)
│  ├─ Gap: 12px
│  │
│  ├─ Card: Upcoming Events
│  │  ├─ Type: Column (vertical)
│  │  ├─ Width: 184px
│  │  ├─ Padding: 16px
│  │  ├─ Background: #FFFFFF
│  │  ├─ Corner radius: 20px
│  │  ├─ Shadow: drop-shadow dy=2 blur=8 color=#1E293B 5%
│  │  ├─ Gap: 12px
│  │  │
│  │  ├─ Row: Header
│  │  │  ├─ Children alignment: space-between
│  │  │  ├─ Text T34: "Upcoming Events"
│  │  │  └─ Text T35: "View all"
│  │  │
│  │  ├─ Event Row 1
│  │  │  ├─ Type: Row (horizontal)
│  │  │  ├─ Gap: 12px
│  │  │  ├─ Badge: "3d" (pill, #EDE9FE bg, #7C3AED text, 10px SemiBold)
│  │  │  └─ Column
│  │  │     ├─ Text T36: "Annual Sports Day"
│  │  │     └─ Text: "20 Jul 2026" (12px #94A3B8)
│  │  │
│  │  └─ Event Row 2
│  │     ├─ Same as Event Row 1
│  │     ├─ Badge: "7d"
│  │     ├─ Text T37: "Independence Day Holiday"
│  │     └─ Text: "15 Aug 2026" (12px #94A3B8)
│  │
│  └─ Card: Recent Activity
│     ├─ Type: Column (vertical)
│     ├─ Width: 184px
│     ├─ Padding: 16px
│     ├─ Background: #FFFFFF
│     ├─ Corner radius: 20px
│     ├─ Shadow: drop-shadow dy=2 blur=8 color=#1E293B 5%
│     ├─ Gap: 12px
│     │
│     ├─ Row: Header
│     │  ├─ Children alignment: space-between
│     │  ├─ Text T38: "Recent Activity"
│     │  └─ Text T35: "View all"
│     │
│     ├─ Activity Row 1
│     │  ├─ Type: Row (horizontal)
│     │  ├─ Gap: 10px
│     │  ├─ Dot: 8×8 circle, #7C3AED
│     │  └─ Column
│     │     ├─ Text T39: "Attendance marked for Class 8-B"
│     │     └─ Text: "2h ago" (12px #94A3B8)
│     │
│     └─ Activity Row 2
│        ├─ Same as Activity Row 1
│        ├─ Text T40: "New announcement posted"
│        └─ Text: "5h ago" (12px #94A3B8)
│
└─ Bottom Spacer
   ├─ Height: 24px
   └─ Background: transparent
```

---

## 5. Icons & Images

| ID | Type | Description | Size | Color | Likely Source |
|---|---|---|---|---|---|
| I1 | Icon | Downward-pointing chevron for institution dropdown | 12×12 | #1E293B | Lucide chevron-down |
| I2 | Image | Circular profile photo of male user | 40×40 | n/a | photo URL |
| I3 | Icon | Bell with small clapper | 20×20 | #1E293B | Lucide bell |
| I4 | Icon | Filter / sliders with horizontal lines | 20×20 | #94A3B8 | Lucide sliders-horizontal |
| I5 | Icon | Heart with two lobes | 16×16 | #FFFFFF | Lucide heart |
| I6 | Icon | Five-pointed star | 16×16 | #FBBF24 | Lucide star |
| I7 | Icon | Right-pointing arrow `→` as button suffix | 16×16 | #FFFFFF | custom or Lucide arrow-right |
| I8 | Icon | Megaphone / bullhorn | 24×24 | #7C3AED | Lucide megaphone |
| I9 | Icon | Calendar with small plus badge | 24×24 | #7C3AED | Lucide calendar-plus |
| I10 | Icon | Document with chart / bar graph | 24×24 | #7C3AED | Lucide file-bar-chart |
| I11 | Icon | Calendar page | 24×24 | #7C3AED | Lucide calendar |
| I12 | Icon | Three horizontal dots | 24×24 | #64748B | Lucide more-horizontal |
| I13 | Icon | Two overlapping person silhouettes | 20×20 | #7C3AED | Lucide users |
| I14 | Icon | Upward-right arrow for growth | 10×10 | #22C55E | Lucide trending-up |
| I15 | Icon | Graduation cap with tassel | 20×20 | #7C3AED | Lucide graduation-cap |
| I16 | Icon | Person silhouette with checkmark | 20×20 | #22C55E | Lucide user-check |
| I17 | Icon | Wallet / billfold | 20×20 | #7C3AED | Lucide wallet |
| I18 | Image | Friendly robot / AI mascot | 64×64 | multi-color | illustration asset |
| I19 | Icon | Bell (large, translucent, background decoration) | 80×80 | #EF4444 20% | Lucide bell |

---

## 6. Spacing System

- Base unit: 4px
- Observed values: 4, 8, 12, 16, 20, 24
- Screen horizontal margin: 16px left/right
- Section vertical gap: 16px between major sections
- Card internal padding: 16px or 20px
- Card internal gap: 8px–12px
- Quick action item gap: 16px
- Grid column/row gap: 12px
- Icon-to-text gap: 6px–8px
- Header row gap: 12px

---

## 7. Corner Radii

| Element Type | Radius | Notes |
|---|---|---|
| Page cards | 20px | Standard stat, events, priority cards |
| Hero card | 24px | Larger, more prominent |
| Search bar | 12px | Input-shaped |
| Buttons / pills | 999px | Fully rounded |
| Avatars | 999px | Circular |
| Quick-action circles | 999px | Icon containers |
| Notification badge | 999px | Red pill with number |

---

## 8. Shadows & Elevation

| Level | Elements | dy | blur | spread | color | opacity |
|---|---|---|---|---|---|---|
| None | Page background, header | 0 | 0 | 0 | — | 0% |
| Subtle | Search bar | 1px | 3px | 0px | #1E293B | 4% |
| Card | Stat cards, events, activity, priority | 2px | 8px | 0px | #1E293B | 5-6% |
| Elevated | Hero card | 8px | 24px | 0px | #7C3AED | 15% |
| AI Banner | AI insight | 4px | 12px | 0px | #8B5CF6 | 12% |

---

## 9. Borders & Dividers

| Element | Width | Color | Style | Sides |
|---|---|---|---|---|
| Profile avatar border | 2px | #FFFFFF | solid | all |
| No major visible dividers | — | — | — | — |

---

## 10. Interaction States

| Element | State | Background | Text Color | Border | Shadow |
|---|---|---|---|---|---|
| Primary button | Normal | #EF4444 | #FFFFFF | none | none |
| Primary button | Pressed | #DC2626 | #FFFFFF | none | none |
| Text link "View all" | Normal | transparent | #7C3AED | none | none |
| Quick action circle | Normal | #FFFFFF | — | none | dy=1 blur=4 |
| Quick action circle | Pressed | #F1F5F9 | — | none | dy=1 blur=4 |
| Search bar | Normal | #FFFFFF | #94A3B8 hint | none | dy=1 blur=3 |
| Search bar | Focused | #FFFFFF | #1E293B | 1.5px #7C3AED | dy=2 blur=6 |

---

## 11. Animations / Motion

- Progress rings: animated sweep from 0 to target value over 700ms ease-out
- Stat counters: count-up animation on load (optional)
- Hero decorative shapes: subtle floating/breathing loop (optional)
- Sparklines: draw-in left-to-right on scroll-into-view
- Quick action icons: scale 0.95 on press with 100ms spring

---

## 12. Z-Order / Layering

1. Page background (#F8FAFC)
2. Scroll content column
3. Cards and surfaces
4. Decorative 3D graphics on hero card
5. Floating notification badge (above bell icon)
6. AI robot illustration (above banner)
7. Top notification badge (highest small element)

---

## 13. Accessibility Notes

- Minimum touch targets: quick-action circles 52×52, notification bell 40×40, buttons 44+px height
- Color contrast: white text on purple gradient passes WCAG AA
- Red-on-white urgent text (#EF4444 on #FFFFFF) is 4.5:1+ for 14px text
- Icon-only quick actions are paired with text labels
- "View all" links use visible color (#7C3AED) to indicate tappability

---

## 14. Component Mapping (Vidya Prayag V2)

| Visual Element | V2 Component | Props / Notes |
|---|---|---|
| Page scaffold | `VScreenScaffold` | topBar omitted, scrollable content |
| Greeting header | `VTopAppBar` custom | title + subtitle + actions |
| Search bar | `VInput` | placeholder, leadingIcon=search, trailing=filter icon |
| Hero card | `VCard` | custom gradient fill, padding=20dp, radius=24dp |
| Priority card | `VCard` | elevated, padding=16dp |
| Pill buttons | `VButton` | variant=Primary, tone=Danger, size=Sm, radius=999dp |
| Quick actions | `VCard` circular 52dp | icon + label stacked |
| Stat cards | `VCard` | padding=16dp, radius=20dp |
| Progress rings | `VProgressRing` | size=56dp, stroke=6dp, tone=Success/Arctic |
| AI banner | `VCard` | gradient fill, custom robot image |
| Event/activity cards | `VCard` | padding=16dp, radius=20dp |
| Badge chips | `VBadge` | tone=Arctic for "3d"/"7d", tone=Danger for urgent notifications |

---

## Implementation Notes

- Use `VidyaPrayagTheme` wrapper for the screen.
- All text uses `VTheme.type` or `Plus Jakarta Sans` equivalents.
- Replace hardcoded colors with `VTheme.colors` tokens where possible; this design uses a purple/blue accent rather than the default teal, so consider extending the theme or using local `Color` values for this admin dashboard variant.
- The hero and AI insight gradients are defined as direct hex values; bind them to Figma or Compose gradients for maintainability.
- Progress rings can use `VProgressRing` with `tone=Success` (green) or `tone=Arctic` (purple) depending on mapping.
