# Vidya Prayag — Product Design Specification

## Version 2

> **Document type:** Product Design Specification  
> **Audience:** Product designers, UX designers, design systems engineers  
> **Purpose:** Enable a designer to recreate the entire application in Figma without reference to existing code  
> **Design leadership perspective:** Apple, Stripe, Linear, Airbnb  
> **Date:** July 2026

---

## Table of Contents

**Volume I — Design Foundation**
1. Design Philosophy
2. Brand Identity & Visual Language
3. Color System
4. Typography System
5. Spacing & Grid System
6. Motion Design Language
7. Iconography

**Volume II — Component Specifications**
8. Component Design Specifications

**Volume III — Screen Specifications**
9. Unauthenticated Flow
10. Onboarding Flow
11. Parent Portal
12. School Admin Portal
13. Teacher Portal

**Volume IV — Cross-Cutting Concerns**
14. Interaction Design Patterns
15. Accessibility Design Guidelines
16. Inspiration & References
17. Dark Mode Design Guidelines

---

# VOLUME I — DESIGN FOUNDATION

---

## 1. Design Philosophy

### 1.1 The Product

Vidya Prayag is a school management platform that connects three distinct human beings — a parent, a teacher, and a school administrator — around the life of a child. The product is not an ERP. It is not a portal. It is a **relationship engine** that makes the daily rhythm of a child's education visible, actionable, and beautiful for everyone involved.

### 1.2 The Emotional Contract

Every screen must answer: **how should this make the person feel?**

| Role | Core Emotion | Design Implication |
|------|-------------|-------------------|
| **Parent** | Reassurance, pride, connection | Warm tones, child-centric, gentle motion, celebratory micro-moments |
| **Teacher** | Competence, clarity, efficiency | Focused layouts, fast access, minimal decoration, data-forward |
| **Admin** | Authority, control, insight | Structured grids, analytical density, confident color, commanding hierarchy |

### 1.3 Seven Design Principles

**P1 — Timeless Elegance:** Restraint over trends. Generous whitespace, limited color vocabulary. Reference: Apple HIG — "clarity, deference, depth."

**P2 — Confident Restraint:** One dominant accent per portal. Color is meaning, not decoration. A screen with five accent colors has lost its way.

**P3 — Tactile Precision:** Every interactive element responds to touch with physical sensation — scale, spring, haptic. The UI has weight.

**P4 — Layered Depth:** Navy-tinted shadows, never black. Three elevation tiers. Dark mode is a different atmosphere, not an inversion.

**P5 — Data as Design:** Numbers are beautiful. Tabular figures, monospace data fonts, canvas charts with animated reveals. A percentage is a visual artifact.

**P6 — Motion with Purpose:** Animation guides attention, confirms actions, smooths transitions. Never decorative. Spring physics. 200–400ms sweet spot.

**P7 — Accessible by Default:** WCAG AA contrast. 44pt minimum touch targets. Reduce Motion respected. Font scaling never breaks layout.

### 1.4 The Quality Bar

| Tradition | What We Take | Reference |
|-----------|-------------|-----------|
| **Apple** | Material precision, tactile feedback, typography hierarchy, spatial depth | iOS Settings, Apple Health, Apple Card |
| **Stripe** | Data visualization, form design, dashboard craft, micro-interactions | Stripe Dashboard, Stripe Atlas |
| **Linear** | Minimalism, motion craft, keyboard-first, dark mode excellence | Linear app, Linear marketing |
| **Airbnb** | Storytelling, visual hierarchy, photography integration, trust-building | Airbnb host dashboard, Airbnb landing |

### 1.5 Anti-Patterns We Reject

- The ERP aesthetic — dense tables, gray borders, no emotion
- The Material default — bright ripples, flat surfaces, generic shadows
- The dashboard dump — 20 widgets with no hierarchy
- The form maze — 15 fields with no progressive disclosure
- The toast flood — transient messages for every action
- The empty void — "No data" with no guidance

---

## 2. Brand Identity & Visual Language

### 2.1 The Mark

The logo is a **bridge** — a "Setu" (Sanskrit for bridge). Two grounded pillars, an arc spanning them, three cables connecting arc to deck. A navy dot at the apex represents the child at the center.

**Geometry (56-unit viewBox):**
- Outer plate: rounded square 52×52, corner radius 14, accent at 12% opacity
- Arc: quadratic bezier (12,32)→(28,12)→(44,32)
- Deck: line (10,40)→(46,40)
- Cables: verticals at x=18, 28, 38, 78% opacity
- Pillar caps: circles at (12,32) and (44,32), radius 2.6
- Center node: navy circle at (28,22), radius 2.4

**Presentations:**
1. **Glass cube** — frosted white-on-teal plate, bridge in white + navy center. Splash/auth. 160pt, 28pt radius, 16% plate opacity.
2. **Bare mark** — bridge geometry alone, configurable stroke. Inline use.
3. **With wordmark** — mark + "Vidya" + accent "S" + "etu", ExtraBold, -0.02em.

**Rationale:** The bridge metaphor is culturally resonant (Setu = connection). It communicates bridging school and home without being literal. Simple enough at 16pt, distinctive at 160pt.

### 2.2 Brand Voice

| Attribute | We Are | We Are Not |
|-----------|--------|-----------|
| Tone | Warm, confident, clear | Corporate, clinical, playful |
| Language | Plain, human, specific | Jargon, generic, abstract |
| Errors | "Couldn't load attendance. Tap to retry." | "Error 404: Resource not found." |
| Empty | "No messages yet. When your child's teacher sends a message, it'll appear here." | "No data available." |
| Success | "Attendance marked for 28 students." | "Success!" |

### 2.3 Visual Language Summary

| Element | Characteristic | Inspiration |
|---------|---------------|-------------|
| Surfaces | White cards on tinted canvas, hairline borders, navy-tinted shadows | Linear, Notion |
| Typography | Plus Jakarta Sans (warm) + DM Mono (data) | Stripe, Vercel |
| Color | Teal + navy + lavender. Semantic for data only. | Apple Health, Linear |
| Motion | Spring physics, staggered entrances, directional transitions | iOS, Linear |
| Iconography | Material core + custom vectors, 24pt, 2pt stroke | Lucide, Phosphor |
| Data viz | Pure canvas, animated reveals, tabular figures | Stripe, Apple Health |

---

## 3. Color System

### 3.1 Color Philosophy

Color serves three purposes in priority order:
1. **Identity** — which portal? (teal=admin, lavender=parent/teacher)
2. **Meaning** — what does data tell me? (green=good, amber=caution, red=urgent)
3. **Hierarchy** — what should I look at first? (darker ink = more important)

Color is never decoration. More than two accent colors visible simultaneously = failed design review.

### 3.2 Portal Color Identities

**Parent — Lavender (`#6C5CE0`):** Care without sentimentality. Warm enough to feel personal, cool enough to feel trustworthy. Pairs with navy. Screenshots well for sharing with family.

**Teacher — Violet (shared lavender):** Same family as parent, reinforcing connection. More restrained application — function over decoration.

**Admin — Teal (`#0D9488`):** Authority without coldness. More human than corporate blue, more serious than green. Pairs with navy for "command center" feeling.

**Usage rules across all portals:**
- Accent on: active tabs, primary CTAs, progress rings, selected states, notification badges
- Semantic colors on: DATA ONLY (attendance, fees, grades). A "Submit" button is never green.
- Navy ink on: all primary text, headlines

### 3.3 Color Tokens — Light Theme

#### Brand Family

| Token | Hex | Usage |
|-------|-----|-------|
| Teal | `#2DD4BF` | Admin accent, active states |
| Teal Deep | `#0D9488` | Interactive, focus rings, pressed |
| Navy | `#26234D` | Primary text, dark surfaces, shadow tint |
| Navy Deep | `#1A1838` | Headlines, max emphasis |
| Accent | `#6C5CE0` | Parent/teacher accent |
| Accent Deep | `#544AB8` | Interactive accent, gradient start |
| Accent Soft | `#8B7EE8` | Secondary accent, gradient end |
| Accent Tint | `#F4F3FA` | Selected backgrounds, chip fills |

#### Ink Scale

| Token | Hex | Usage |
|-------|-----|-------|
| Ink | `#1A1838` | Primary text — headlines, body, names |
| Ink 2 | `#5C5870` | Secondary — labels, captions, metadata |
| Ink 3 | `#9B96B0` | Tertiary — placeholders, disabled, hints |

**Rationale:** Ink is navy-based, not gray. Navy-tinted text on white feels considered; gray text feels clinical.

#### Surfaces

| Token | Hex | Usage |
|-------|-----|-------|
| Background | `#F8F7FC` | App canvas (barely-perceptible lavender tint) |
| Card | `#FFFFFF` | Card/sheet/dialog fill |
| Cream | `#F0EFF5` | Input backgrounds, inactive areas |

**Rationale:** Background isn't pure white — it's lavender-tinted so white cards appear to float. Three-tier surface system creates spatial hierarchy without relying solely on shadows.

#### Borders

| Token | Hex | Usage |
|-------|-----|-------|
| Hairline | `#E8E6F0` | 0.5pt dividers |
| Border 1 | `#D8D5E5` | 1pt card/input borders |
| Border 2 | `#C5C2D5` | Strong borders, drag handles |

#### Semantic

| Token | Soft | Ink | Usage |
|-------|------|-----|-------|
| Success | `#D1FAE5` | `#065F46` | Present, approved, completed |
| Warning | `#FEF3C7` | `#92400E` | Late, pending, caution |
| Danger | `#FEE2E2` | `#991B1B` | Absent, rejected, overdue |
| Info | `#DBEAFE` | `#1E40AF` | Informational |

**Usage rule:** Semantic colors on DATA ONLY, never on chrome.

#### Shadow

Navy-tinted (`#26234D`), never pure black. Navy shadows feel warm and intentional.

### 3.4 Dark Theme

Dark mode is a **different atmosphere** — the same room at dusk.

| Token | Light | Dark | Rationale |
|-------|-------|------|-----------|
| Background | `#F8F7FC` | `#0F0E1A` | Deep navy-black, not pure black |
| Card | `#FFFFFF` | `#1A1830` | Elevated surface |
| Cream | `#F0EFF5` | `#252338` | Recessed surface |
| Ink | `#1A1838` | `#F0EFF5` | Inverted |
| Teal | `#2DD4BF` | `#14B8A6` | Deeper for contrast |
| Accent | `#6C5CE0` | `#A78BFA` | Lighter for visibility |
| Shadows | Navy-tinted | Suppressed | Depth via surface color |

### 3.5 School Branding Override

Schools can override the accent family. Preserved: ink scale, surfaces, semantic colors, shadow tint. Changed: active tab, CTA, gauges, selected states, gradient heroes.

### 3.6 Contrast Standards

| Pairing | Light | Dark | Standard |
|---------|-------|------|----------|
| Ink on Card | 15.8:1 | 14.2:1 | AAA |
| Ink 2 on Card | 7.2:1 | 5.8:1 | AA+ |
| Teal Deep on White | 4.8:1 | 4.5:1 | AA |
| White on Teal Deep | 4.8:1 | 4.5:1 | AA |

---

## 4. Typography System

### 4.1 Type Philosophy

Two typefaces create a "voice vs. data" distinction:

- **Plus Jakarta Sans** — the voice. Humanist, warm, clear. Confident but not cold.
- **DM Mono** — the data. Monospaced, tabular figures. Mechanical precision for numbers.

### 4.2 Type Scale

#### Headlines

| Style | Font | Size | Weight | LH | Tracking | Usage |
|-------|------|------|--------|-----|---------|-------|
| Display | Jakarta | 32 | 800 | 1.15 | -0.02em | Hero, splash |
| H2 | Jakarta | 22 | 700 | 1.20 | -0.01em | Screen/modal titles |
| H3 | Jakarta | 17 | 700 | 1.25 | 0 | Section titles, card headers |
| H4 | Jakarta | 14 | 600 | 1.30 | 0 | Card titles, row titles |

#### Body

| Style | Font | Size | Weight | LH | Usage |
|-------|------|------|--------|-----|-------|
| Body | Jakarta | 14 | 400 | 1.50 | Body text, descriptions, messages |
| Body Strong | Jakarta | 14 | 600 | 1.40 | Emphasized body, active labels |
| Caption | Jakarta | 12 | 500 | 1.40 | Subtitles, metadata, timestamps |

#### Labels

| Style | Font | Size | Weight | Tracking | Usage |
|-------|------|------|--------|---------|-------|
| Label | Jakarta | 11 | 700 | 0.10em UPPER | Section labels, field labels |
| Input Label | Jakarta | 12 | 600 | 0 | Form field labels |

#### Data

| Style | Font | Size | Weight | Tracking | Usage |
|-------|------|------|--------|---------|-------|
| Data Large | DM Mono | 22 | 500 | 0 tnum | Large stats, KPI numbers |
| Data | DM Mono | 15 | 400 | 0 tnum | Row values, table cells |
| Data Small | DM Mono | 13 | 400 | 0 tnum | Inline numbers, compact |

### 4.3 Font Scaling

| Factor | Impact |
|--------|--------|
| 0.85× | Small preference |
| 1.0× | Default |
| 1.15× | Large — layout adapts |
| 1.3× | XL (accessibility) — single-column fallback |

**Rule:** Layout must never break at 1.3×. Text must never truncate at 1.15×.

---

## 5. Spacing & Grid System

### 5.1 Philosophy

Base-4 system. Every value is a multiple of 4pt. Single-column layout, max width 440pt, centered.

### 5.2 Spacing Scale

| Token | Value | Usage |
|-------|-------|-------|
| xs | 4pt | Icon-to-label, chip internal |
| sm | 8pt | Between related items, card sections |
| md | 12pt | Between cards, input spacing, row padding |
| lg | 16pt | Screen padding, card content, section spacing |
| xl | 20pt | Large section breaks |
| xxl | 24pt | Major breaks, modal padding |
| xxxl | 32pt | Hero spacing, splash margin |

### 5.3 Screen Grid

```
┌───────────────────────────────────┐
│        (status bar)               │
│  ┌─────────────────────────┐      │
│  │  MAX 440pt, PAD 16pt   │      │
│  │  Single column, centered│      │
│  └─────────────────────────┘      │
│  ┌─────────────────────────┐      │
│  │  BOTTOM NAV (112pt)     │      │
│  └─────────────────────────┘      │
│        (home indicator)           │
└───────────────────────────────────┘
```

### 5.4 Card Anatomy

- Width: screen - 32pt | Radius: 16pt | Border: 1pt hairline
- Content padding: 16pt all sides
- Shadow: navy-tinted, 2pt dy, 4pt spread, 6% alpha (light only)

### 5.5 List Row Anatomy

- 12pt vertical padding, 16pt horizontal
- Avatar 40pt + title (H4) + subtitle (Caption) + trailing value
- Divider: 0.5pt, left-inset 56pt (aligns with text)

### 5.6 Bottom Nav Anatomy

- 64pt content + 48pt inset = 112pt total
- Icons 24pt, labels 10pt/600
- Active: accent pill (40×32, radius 12) + accent color
- Inactive: ink3
- Badge: 16pt danger circle, top-right of icon

---

## 6. Motion Design Language

### 6.1 Philosophy

Motion serves: **orientation** (where am I?), **confirmation** (did it work?), **hierarchy** (what's first?), **delight** (does it feel good?). Never decorative.

### 6.2 Principles

**M1 — Physical Plausibility:** Spring physics, not linear. Things have mass and momentum.

**M2 — Directional Consistency:** Forward = L→R. Back = R→L. Modals rise. Dismissals fall. Theme = crossfade.

**M3 — Staggered Revelation:** Items enter in sequence, 50ms stagger. Guides the eye.

**M4 — Duration Discipline:**
- 100–200ms: Micro-interactions
- 200–300ms: Tab switch, focus
- 300–400ms: Screen transitions, modals
- 400–700ms: Data reveals (charts, gauges)
- Never exceed 800ms

**M5 — Reduce Motion:** Disable staggered entrances, decorative animations. Slides → crossfades. Keep functional motion only.

### 6.3 Spring Specs

| Spring | Damping | Stiffness | Usage |
|--------|---------|-----------|-------|
| Soft | 0.80 | Medium | Card press, expansions |
| Sheet | 0.85 | Low | Bottom sheet drag |
| Card | 0.75 | Medium-Low | Swipe, dismiss |
| Snappy | 0.60 | Medium | Tab indicator, toggle |

### 6.4 Transition Catalog

**Screen Transitions:**

| Transition | Duration | Pattern | Usage |
|-----------|---------|---------|-------|
| Forward Slide | 300ms | New R→L (30pt+fade), old L→R | Push nav |
| Backward Slide | 300ms | Reverse of forward | Pop nav |
| Modal Rise | 300ms | Bottom→top (24pt+fade) | Modal/sheet enter |
| Modal Fall | 250ms | Top→bottom | Modal dismiss |
| Quiet Fade | 300ms | Crossfade | Theme switch, tab content |

**Entrance Animations:**

| Animation | Duration | Movement | Stagger |
|-----------|---------|---------|---------|
| Fade Up | 300ms | 8pt Y + alpha | 50ms/item |
| Fade In | 200ms | Alpha only | None |
| Scale In | 250ms | 0.92→1.0 + alpha | None |
| Slide In | 300ms | 16pt X + alpha | None |

**Data Reveals:**

| Animation | Duration | Description |
|-----------|---------|-------------|
| Gauge Sweep | 700ms | Arc clockwise from 12 o'clock |
| Donut Reveal | 800ms | Segments sequential, 100ms stagger |
| Bar Grow | 600ms | Bars grow from bottom |
| Sparkline Draw | 1100ms | Line draws L→R + end dot pulse |
| Count-Up | 400ms | Number 0→target |

**Micro-Interactions:**

| Element | Trigger | Animation | Duration |
|---------|---------|-----------|----------|
| Button (Primary) | Press | Scale 0.96 + light sweep | 100ms |
| Button (Success) | Done | Spinner → checkmark pop | 400ms |
| Card | Press | Scale 0.98 | 100ms |
| Input | Focus | Border → tealDeep + 4pt glow | 200ms |
| Tab (Bottom) | Tap | Pill slides + haptic | 300ms Snappy |
| Avatar | Load | Shimmer → crossfade | 300ms |
| Badge | Appears | Scale 0.8→1.0 | 250ms |
| Snackbar | Appears | Slide up 16pt + fade | 300ms |
| Dialog | Appears | Scale 0.95→1.0 + scrim | 250ms |
| Bottom Sheet | Appears | Slide up + scrim | 300ms |
| Shimmer | Loading | Gradient sweep L→R | 1200ms loop |

---

## 7. Iconography

### 7.1 Philosophy

Icons are functional, not decorative. Material core + custom vectors. 24pt default, 2pt stroke, outlined (resting) → filled (active).

### 7.2 Categories

**Navigation:** Home, Book Open, Wallet, Message Circle, User, Users, File Text, Settings, Edit, Arrow Left/Right, Chevron Right/Down, X, Check, Plus

**Data:** Calendar, Clock, Trending Up/Down, Bar Chart, Pie Chart, Award, Graduation Cap, Book, Clipboard, Hash

**Status:** Check Circle, Alert Circle, Alert Triangle, Info, Bell, Shield, Heart, Bus, Map Pin

**Action:** Search, Filter, More Horizontal/Vertical, Download, Upload, Share, Printer, Qr Code, Camera, Image, Phone, Mail, Send, Paperclip

---

# VOLUME II — COMPONENT SPECIFICATIONS

---

## 8. Component Design Specifications

### 8.1 Button

**Emotional Goal:** The moment of commitment. Confident, inviting, satisfying.

**Variants:**

| Variant | Fill | Border | Text | Usage |
|---------|------|--------|------|-------|
| Primary | Portal accent | None | White 14/600 | Dominant action (one per screen) |
| Secondary | Transparent | 1pt accent deep | Accent deep | Alternative action |
| Ghost | Transparent | None | Ink2 | Tertiary (Skip, Cancel) |
| Destructive | `#DC2626` | None | White | Irreversible (requires confirm dialog) |

**Sizes:** Sm 36pt | Md 44pt | Lg 52pt. Radius: 12pt. Padding: 20pt horizontal.

**States:**
- Press: Scale 0.96 + light sweep (100ms), spring settle (200ms)
- Loading: Text → spinner, taps ignored
- Success: Spinner → checkmark pop (400ms), fade back after 1.5s
- Disabled: 40% opacity

**Tones (Primary only):** Navy, Teal, Lavender, Sky, Peach, Sand, Rose, Mint

**Inspiration:** Apple (press depth), Stripe (loading→success), Linear (ghost restraint)

**Accessibility:** Min 44pt target, 2pt focus ring, screen reader announces label + state

---

### 8.2 Card

**Emotional Goal:** Containers of meaning — physical, layered, intentional.

| Variant | Shadow | Border | Usage |
|---------|--------|--------|-------|
| Elevated | Navy 6% alpha | 1pt hairline | Default content container |
| Flat | None | 1pt border1 | Dense scannable layouts |
| Action | Elevated | 1pt hairline | Icon + title + subtitle + chevron |
| Tinted | None | 1pt semantic | Alerts, birthdays, achievements |

Radius: 16pt. Padding: 16pt. Press (clickable): Scale 0.98, Soft spring.

**Inspiration:** Linear (hairline + shadow), Notion (white on tinted), Apple (physical weight)

---

### 8.3 Input

**Emotional Goal:** A conversation. Attentive, acknowledging, validating without judgment.

| State | Fill | Border | Special |
|-------|------|--------|---------|
| Resting | Cream | 1pt border1 | Placeholder in ink3 |
| Focus | White (lifts) | 1.5pt teal deep | 4pt teal glow ring (15% alpha) |
| Error | Danger soft | 1.5pt danger ink | Error message 12pt below |
| Disabled | Cream | Hairline | ink3 text, no interaction |

Radius: 12pt. Label: 12pt/600 ink2, above input. Text: 14pt/400 ink. Leading icon: 20pt ink2.

**Inspiration:** Stripe (focus glow, cream→white), Linear (label-above, error), Apple (clear button)

---

### 8.4 Bottom Navigation

**Emotional Goal:** The compass — stable, reliable, instantly responsive.

**Container:** Fixed bottom, white, 1pt hairline top, 64pt + 48pt = 112pt, raised shadow.

**Items:** 4–5, evenly distributed. Icon 24pt (outlined→filled active), label 10pt/600. Inactive: ink3. Active: accent + accent tint pill (40×32, radius 12). Pill slides Snappy spring + haptic.

**Badge:** 16pt danger circle, top-right, scale in 0.8→1.0.

**Inspiration:** Apple (floating tab bar), Linear (spring physics), Airbnb (icon+label)

---

### 8.5 Top Tabs

Pill-style, scrollable. Inactive: transparent, ink3, scale 0.98. Active: accent tint, accent deep, scale 1.0. 200ms Snappy crossfade+scale. 8pt between tabs.

---

### 8.6 Avatar

Circle, 32–80pt. Image → crossfade from initials (300ms). Initials: first letters of first two words, 14pt/600 white. Pastel background by name hash. Optional 2pt accent ring.

**Inspiration:** Airbnb (pastel + initials), Linear (crossfade), Apple (contact card)

---

### 8.7 Badge

Pill, 22pt, 4pt/10pt padding, 11pt/600. 6 tones: Arctic/Accent/Success/Warning/Danger/Neutral. Soft tint fill + matching ink. Scale in 0.8→1.0.

---

### 8.8 Progress Bar

6pt track, cream, 3pt radius. Animated fill 600ms. Tone-based color. No label.

---

### 8.9 Progress Ring

Canvas arc, 48–128pt. Stroke 6–16pt. Clockwise sweep 700ms. Optional center label (Data Large). Round cap.

**Inspiration:** Apple Health (ring metaphor), Apple Watch (activity rings), Stripe (gauge)

---

### 8.10 Charts

**Donut:** 80–160pt, 12–24pt thick. Segments sweep sequentially (800ms, 100ms stagger). 2pt gap. Optional center slot.

**Bars:** 80–120pt. Grow from bottom (600ms). Last bar highlighted accent.

**Sparkline:** 24–40pt. Line draws L→R (1100ms) + end dot pulse. Area: accent 15% alpha gradient.

**Inspiration:** Stripe (donut in cards), Apple Health (sparkline), Linear (minimal charts)

---

### 8.11 Snackbar

Bottom-anchored, 16pt from bottom. Navy deep fill (light mode). 14pt white text, 20pt semantic icon, optional action. 12pt radius. Auto-dismiss 4s (6s with action). Slide up + fade. Swipe to dismiss.

---

### 8.12 Date Picker

Read-only field → calendar dialog. Friendly display ("13 Jun 2026"). Modal Rise (300ms). Month grid, selected = accent deep circle, today = accent ring. "Done" button.

**Inspiration:** Apple (iOS calendar picker), Google Calendar (month nav)

---

### 8.13 Time Picker

Hour:minute dropdowns, 12h display. Hours 00–23, minutes 15-min increments. Clock icon leading.

---

### 8.14 Bottom Sheet (New)

Bottom-anchored, full width. White, 24pt top radius. Modal shadow. 36×4pt drag handle. Scrim: ink 40%. Drag to dismiss (<30% → dismiss). Modal Rise enter, Modal Fall exit.

**Inspiration:** Apple (iOS sheet physics), Linear (scrim + sheet)

---

### 8.15 Search Bar (New)

Full width, 44pt. White, 1pt hairline, 12pt radius. Search icon leading. Clear (X) trailing when text. Focus: border → teal deep + glow.

**Inspiration:** Linear (command palette), Apple (iOS search), Airbnb (search-first)

---

### 8.16 Empty State

Centered. 48pt icon in 80pt cream circle. H3 title, Body description (max 2 lines), optional Secondary button. Fade Up on appear.

**Inspiration:** Airbnb (illustrated + CTA), Linear (minimal), Stripe (guidance)

---

### 8.17 Skeleton / Shimmer

Rounded rect matching real content silhouette. Cream + animated gradient sweep (1200ms loop). Corners/height match real element. Layout mirrors real content — same spacing, proportions, count.

**Inspiration:** Facebook (original skeleton), Linear (shimmer), Stripe (layout-matching)

---

### 8.18 Confirm Dialog

Scrim: ink 50%. White card, 16pt radius, 24pt padding, max 320pt. Scale 0.95→1.0 + fade (250ms). Optional icon in 56pt circle. H3 title, Body message. Destructive/Primary + Ghost cancel, stacked.

**Inspiration:** Apple (iOS confirmation), Linear (dialog animation)

---

### 8.19 Pull to Refresh

32pt spinner, teal deep. 64pt threshold. Drag: opacity + scale 0.5→1.0. Completion: content scales 0.98→1.0 (Soft spring, 300ms).

---

### 8.20 Theme Picker

Vertical list of theme cards. 56pt rows, 14pt radius. Icon (40pt circle) + label + caption + check when active. Active: accent tint. Inactive: cream. Tap: 300ms crossfade.

---

### 8.21 Logo / Brand Mark

**Glass Cube:** 56–160pt, white 16% alpha plate, 28pt radius, bridge in white + navy center. For dark heroes.

**Bare Mark:** 24–56pt, configurable stroke, navy center. Inline use.

**With Wordmark:** Mark + "Vidya" + accent "S" + "etu", ExtraBold, -0.02em.

---

### 8.22 QR Code

200pt default. Ink on white. 4-module quiet zone. 12pt container radius. Scale in 0.95→1.0.

---

### 8.23 Schedule Toggle

Two radio rows ("Publish now" / "Schedule for later"). 20pt circle radio, teal deep when selected. Date + time picker expand in cream container (Soft spring, 300ms).

---

# VOLUME III — SCREEN SPECIFICATIONS

---

## 9. Unauthenticated Flow

### 9.1 Splash Screen

**Emotional Goal:** First impression. Calm, premium, confident — a deep breath before a conversation.

**Visual Hierarchy:** Brand mark (glass cube) → wordmark → teal gradient background.

**Layout:**
```
┌───────────────────────────────┐
│                               │
│        ┌──────────┐           │
│        │  BRIDGE  │           │
│        │   MARK   │           │
│        │ (glass)  │           │
│        └──────────┘           │
│         VidyaSetu             │
│                               │
└───────────────────────────────┘
 Background: Teal → Teal Deep vertical gradient
 Logo: 160pt centered | Wordmark: 32pt, white ExtraBold
```

**Color:** Background teal gradient. Logo: white strokes, navy center, 16% alpha plate.

**Motion:**
```
[0ms]    Background fades in (400ms)
[200ms]  Logo scales in 0.8→1.0 (600ms, Soft)
[500ms]  Bridge strokes draw (800ms)
[800ms]  Wordmark fades in (400ms)
[2500ms] Crossfade to next screen (500ms)
```

**Interaction:** None. Passive. 2.5–3s, then auto-transition.

**Accessibility:** Reduce Motion: skip all, show immediately, hold 1s. Screen reader: "Vidya Prayag."

**Rationale:** Establishes brand in 3 seconds. Teal = brand color. Glass cube = premium. Sequential animation = brand "assembling itself."

**Inspiration:** Apple launch splash, Linear loading, Airbnb brand-first entry.

---

### 9.2 Landing Screen

**Emotional Goal:** Welcome and orient. Two audiences — parents and admins — must each feel this product was built for them.

**Visual Hierarchy:**
1. Hero — animated logo + headline + dual CTAs
2. Ecosystem domains — what the product does (per audience)
3. School day timeline — a day in the life
4. Trust metrics — social proof
5. Featured institutions — schools on platform
6. Role entry CTAs — conversion moment
7. Legal footer

**Layout:**
```
┌─────────────────────────────────────────────┐
│  HERO SECTION                               │
│  • Bridge mark (56pt)                      │
│  • "Where school meets home" (Display)     │
│  • Subtitle (Body, ink2)                   │
│  [I'm a Parent] [School / Admin]           │
├─────────────────────────────────────────────┤
│  ECOSYSTEM DOMAINS (horizontal scroll)      │
│  [Icon+Title+Sub+Metrics] × 4              │
├─────────────────────────────────────────────┤
│  SCHOOL DAY TIMELINE                        │
│  08:00 ● School opens                      │
│  09:15 ● Attendance synced                 │
│  11:30 ● Assessment completed              │
│  02:00 ● Parent update sent                │
│  04:30 ● Analytics generated               │
├─────────────────────────────────────────────┤
│  TRUST METRICS                              │
│  24,000+    12,000+    99.9%               │
│  Daily       Parent      Workflow           │
│  interactions connections reliability       │
├─────────────────────────────────────────────┤
│  FEATURED INSTITUTIONS (horizontal scroll)  │
├─────────────────────────────────────────────┤
│  ROLE ENTRY CTAs                            │
│  ┌──────────────────────────────────────┐   │
│  │ I'm a Parent →                      │   │
│  │ OTP login, child linking            │   │
│  └──────────────────────────────────────┘   │
│  ┌──────────────────────────────────────┐   │
│  │ School / Administration →           │   │
│  │ Dashboard, analytics, CRM           │   │
│  └──────────────────────────────────────┘   │
├─────────────────────────────────────────────┤
│  Privacy Policy · Terms · Help             │
└─────────────────────────────────────────────┘
```

**Color:** Background app bg. Hero on white card. Domain cards: white, icon in accent tint circle. Timeline: accent rail + dots, Data Small time, Body text. Trust: Data Large accent deep. CTAs: Primary (Lavender for parent, Teal for admin).

**Typography:** Hero headline Display 32/800. Domain title H4. Timeline time Data Small. Trust value Data Large. CTA title H4 white, subtitle Caption white 80%.

**Motion:**
```
[0ms]    Hero → Fade Up (300ms)
[100ms]  Logo → Scale In (400ms)
[300ms]  Headline → Fade Up
[400ms]  CTAs → Fade Up (100ms stagger)
[600ms]  Ecosystem → Fade Up
[700ms]  Domain cards → Fade Up (80ms stagger)
[1000ms] Timeline → Fade Up
[1100ms] Timeline items → Fade Up (100ms stagger)
[1500ms] Trust → Fade Up
[1600ms] Trust numbers → Count-Up (400ms)
[1800ms] Featured → Fade Up
[2000ms] Role CTAs → Fade Up
```

**Interaction:** "I'm a Parent" → Forward Slide to Parent Auth. "School/Admin" → Forward Slide to Admin Auth. Featured school tap → Parent Auth (pre-filled). Legal → Modal Rise.

**Rationale:** The landing is a **story**, not a form. Emotion (hero) → capability (domains) → value (timeline+metrics) → trust (featured) → choice (CTAs). Dual-audience: domains and timeline adapt per context.

**Inspiration:** Airbnb landing (storytelling, trust), Linear marketing (minimalism, motion), Stripe Atlas (dual-audience), Apple (hierarchy).

---

### 9.3 Parent Auth (Phone + OTP)

**Emotional Goal:** Begin the relationship. Simple, fast, safe. Phone → OTP. No passwords.

**Layout:**
```
┌─────────────────────────────────────────────┐
│  ◀ Back                                     │
│  [Bridge Mark 56pt]  VidyaSetu              │
│  Welcome to Vidya Prayag                   │
│  Enter your phone number to get started    │
│  ┌─────────────────────────────────────┐    │
│  │  📱 Phone Number                    │    │
│  │  +91 | 98765 43210                 │    │
│  └─────────────────────────────────────┘    │
│  We'll send a 6-digit code to verify       │
│  ┌─────────────────────────────────────┐    │
│  │           Send Code                 │    │
│  └─────────────────────────────────────┘    │
│  By continuing, you agree to Terms · Privacy│
└─────────────────────────────────────────────┘
```

**Color:** Primary button Lavender, Lg, full width. Legal links in accent deep.

**Motion:** Staggered Fade Up — mark (100ms), headline (300ms), input (500ms), button (600ms), helper (700ms).

**Interaction:** Numeric keyboard, auto-format. "Send Code" → loading → OTP screen (Forward Slide). Error: input error + snackbar.

**Rationale:** Phone-first removes password friction. Single field = effortless. Brand mark provides continuity.

**Inspiration:** Stripe (single-field form), Apple (phone auth), Linear (minimal auth).

---

### 9.4 OTP Verification

**Emotional Goal:** Confirm identity. Focused, quick. Six digits, auto-fill, clear feedback.

**Layout:**
```
┌─────────────────────────────────────────────┐
│  ◀ Back                                     │
│  Enter the code sent to +91 98765 43210    │
│  ┌──┐ ┌──┐ ┌──┐  ┌──┐ ┌──┐ ┌──┐          │
│  └──┘ └──┘ └──┘  └──┘ └──┘ └──┘          │
│  Resend code in 0:30                       │
│  ┌─────────────────────────────────────┐    │
│  │            Verify                   │    │
│  └─────────────────────────────────────┘    │
└─────────────────────────────────────────────┘
```

**Color:** Boxes 48pt, 12pt radius. Cream→white+teal (active). Danger (error). Entered digit H2 22/700 ink.

**Motion:** Boxes Scale In (300ms, 50ms stagger). First box auto-focuses.

**Interaction:** Auto-advance, backspace returns. Auto-submit on 6 digits. Auto-fill from SMS. Error: all boxes shake (Card spring) + danger + snackbar. Resend after 30s countdown.

**Rationale:** Six large boxes > single input. Auto-advance + auto-submit = minimal taps. Shake = universal "no."

**Inspiration:** Apple (OTP auto-fill), Stripe (verification), Linear (minimal OTP).

---

### 9.5 Admin Auth (Email + Password)

**Emotional Goal:** Professional entry. Serious, capable. This is a tool for running a school.

**Layout:**
```
┌─────────────────────────────────────────────┐
│  ◀ Back                                     │
│  [Bridge Mark 56pt]  VidyaSetu              │
│  School Administration                     │
│  Sign in to your dashboard                 │
│  ┌─────────────────────────────────────┐    │
│  │  ✉ Email Address                   │    │
│  └─────────────────────────────────────┘    │
│  ┌─────────────────────────────────────┐    │
│  │  🔒 Password              👁        │    │
│  └─────────────────────────────────────┘    │
│  Forgot password?                          │
│  ┌─────────────────────────────────────┐    │
│  │            Sign In                  │    │
│  └─────────────────────────────────────┘    │
└─────────────────────────────────────────────┘
```

**Color:** Primary button Teal tone. "Forgot password?" accent deep, right-aligned.

**Interaction:** Email validation on blur. Password visibility toggle. "Sign In" → loading → portal. "Forgot password?" → Modal Rise to reset.

**Rationale:** Admins expect credential login. Email+password is familiar and efficient. Teal button signals "admin lane."

**Inspiration:** Stripe (email+password), Linear (admin auth), Apple (password field).

---

### 9.6 Legal Info Screen

**Emotional Goal:** Transparency and trust. Honest, accessible — not a wall of legalese.

**Layout:** Back header → "Privacy & Terms" title → Top Tabs (Privacy/Terms/Help) → scrollable content → support email.

**Rationale:** Tabbed structure for findability. "Last updated" date for freshness. Support email = human touch.

---

## 10. Onboarding Flow

### 10.1 School Onboarding (Admin)

**Emotional Goal:** Set up with confidence. Guided, not overwhelmed. Each step = progress.

**Steps (5):**
1. **School Identity** — Name, type, board, established year
2. **Contact & Location** — Address, phone, email, city, state
3. **Academic Structure** — Classes, sections pattern, academic year dates
4. **Admin Profile** — Name, role, phone (pre-filled)
5. **Review & Confirm** — Summary with edit links

**Layout:**
```
┌─────────────────────────────────────────────┐
│  ◀ Back          Step 2 of 5               │
│  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ │
│  School Details                            │
│  Tell us about your school                 │
│  [Input: School Name]                      │
│  [Select: School Type]                     │
│  [Select: Board Affiliation]               │
│  ┌─────────────────────────────────────┐    │
│  │  Continue                           │    │
│  └─────────────────────────────────────┘    │
│  Skip for now                              │
└─────────────────────────────────────────────┘
```

**Color:** Progress bar teal on cream, animated (300ms Snappy). Continue: Primary Teal. Skip: Ghost.

**Motion:** Forward Slide per step. Progress bar animates. Fields Fade Up with 80ms stagger.

**Interaction:** Continue validates + advances. Skip on optional steps. Review step has "Edit" links per section. Final submit: loading → success checkmark → portal.

**Rationale:** Wizard breaks 20-field form into 4–5 field chunks. Progress bar = sense of advancement. Skip reduces commitment anxiety. Review = completion before irreversible submit.

**Inspiration:** Stripe Atlas (wizard, progress), Linear (setup), Apple (setup assistant).

---

### 10.2 Parent Link Child (3-Step Wizard)

**Emotional Goal:** Connect to your child. The most emotionally charged moment — careful, hopeful, clear.

**Steps:**
1. **Your Details** — Parent name, preferred language
2. **Find School** — Live search with results
3. **Child Details** — Name, roll/admission number, class (auto-peels section), section

**Layout (Step 2 — School Search):**
```
┌─────────────────────────────────────────────┐
│  ◀ Back        Step 2 of 3                  │
│  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ │
│  Find Your School                          │
│  Search for your child's school            │
│  ┌─────────────────────────────────────┐    │
│  │  🔍 Search school name...           │    │
│  └─────────────────────────────────────┘    │
│  ┌─────────────────────────────────────┐    │
│  │  🏫 Delhi Public School             │    │
│  │  New Delhi · CBSE                  │    │
│  └─────────────────────────────────────┘    │
│  ┌─────────────────────────────────────┐    │
│  │  🏫 Delhi Public School             │    │
│  │  Gurugram · CBSE                   │    │
│  └─────────────────────────────────────┘    │
│  Can't find your school? Browse all →     │
└─────────────────────────────────────────────┘
```

**Color:** Search bar white. School cards elevated. Selected: accent tint + accent border. "Browse all" accent deep.

**Interaction:** Debounced search (300ms). School tap → selected state (Soft spring) → Continue appears. Submit (Step 3): loading → "Request sent! The school will review your link request." → pending state.

**Rationale:** 3-step wizard makes complex process manageable. Live search = magical. Auto-peeling class/section = small intelligence. "Request sent" sets expectations.

**Inspiration:** Airbnb (search-first), Stripe (multi-step forms), Apple (setup wizard).

---

### 10.3 Teacher First Login

**Emotional Goal:** Secure the account. A necessary security step, not a chore.

**Layout:** "Set Your Password" → new password + confirm + requirements checklist (8+ chars, 1 number, 1 special) → real-time green checkmarks → Continue → portal.

---

## 11. Parent Portal

### 11.1 Parent Home

**Emotional Goal:** Reassurance and pride. Open the app → see your child immediately. Like opening a window into their school day. Warm, personal, complete.

**Visual Hierarchy:**
1. Aurora Hero — child avatar, name, class, attendance ring, live greeting
2. School Day Timeline — today's schedule as visual rail
3. Feature Cards — attendance, fees, academics, messages, transport (horizontal scroll)
4. PEWS Nudge — early warning card (when applicable)
5. Upcoming Events

**Layout:**
```
┌─────────────────────────────────────────────┐
│  ┌─────────────────────────────────────┐    │
│  │  AURORA HERO (lavender wash)        │    │
│  │  [Avatar 56pt] Good morning,       │    │
│  │                 Aarav, Class 8-A   │    │
│  │                 Delhi Public School│    │
│  │  [Ring: 94.2%]  [Grade: A+]       │    │
│  └─────────────────────────────────────┘    │
│  TODAY'S JOURNEY                           │
│  ┌─────────────────────────────────────┐    │
│  │  ● 08:00  School started           │    │
│  │  ● 09:15  Attendance marked        │    │
│  │  ● 11:30  Maths class              │    │
│  │  ○ 03:30  School ends              │    │
│  └─────────────────────────────────────┘    │
│  QUICK ACCESS                              │
│  [💰Fees ₹2,500] [📊Acad 94%] [💬Msgs 3]  │  ← Horizontal scroll
│  [🚌Bus Live] [📚Library] [🎯Tutor]      │
│  ┌─────────────────────────────────────┐    │
│  │  ⚠ PEWS Alert                       │    │
│  │  Aarav's attendance has dropped     │    │
│  │  below 75%. Contact Teacher →       │    │
│  └─────────────────────────────────────┘    │
│  ┌─────────────────────────────────────┐    │
│  │  📅 Upcoming Events                 │    │
│  │  PTM - 15 Jun · Sports Day - 22 Jun│    │
│  └─────────────────────────────────────┘    │
│  [Home][Acad][Fees][Msgs][Prof]           │
└─────────────────────────────────────────────┘
```

**Color:** Aurora hero: lavender 8% alpha wash on white. Ring: accent deep. Timeline: filled dots (accent) past, outlined (ink3) future. Feature cards: icon in accent tint circle, value in Data accent deep. PEWS: warning soft tinted card.

**Typography:** Greeting H3 17/700. Child name H4 14/600. School Caption 12/500. Ring value Data Large 22 DM Mono. Timeline time Data Small 13. Feature value Data 15 accent deep.

**Motion:**
```
[0ms]    Tab content → Quiet Fade (300ms)
[0ms]    Aurora hero → Fade Up
[100ms]  Avatar → Scale In (400ms, Soft)
[200ms]  Ring → Fade Up
[400ms]  Ring sweep → Gauge Sweep (700ms)
[400ms]  Ring value → Count-Up (400ms)
[300ms]  Timeline → Fade Up
[400ms]  Timeline items → Fade Up (80ms stagger)
[600ms]  Feature cards → Fade Up (60ms stagger)
[800ms]  PEWS → Fade Up (if present)
[900ms]  Events → Fade Up
```

**Interaction:** Avatar tap → child profile (Modal Rise). Ring tap → Academics tab. Feature card tap → respective screen. PEWS "Contact Teacher" → Messages (pre-addressed). Pull to refresh. Live clock: greeting updates every minute.

**Design Law:** NEVER COLLAPSE TO WHITE SPACE. Every card renders rich state even with sparse data. Empty timeline shows schedule structure. Screen is always full and composed.

**Rationale:** The emotional center. Aurora hero = premium warmth. Child's face + ring answers "is my child okay?" in <1s. Timeline = "what's happening today?" Feature cards = "what can I do?" PEWS = "we noticed something."

**Inspiration:** Apple Health (summary, ring), Airbnb (storytelling), Linear (card hierarchy), Stripe (data-forward).

---

### 11.2 Parent Academics

**Emotional Goal:** Understand progress. Celebrate the journey. Attendance, marks, syllabus — as achievements, not judgments.

**Visual Hierarchy:**
1. Attendance Calendar — monthly grid, color-coded days
2. Subject Performance — donut chart + breakdown
3. Marks Trend — sparkline + latest results
4. Syllabus Coverage — per-subject progress bars
5. Report Card — latest report with download

**Layout:**
```
┌─────────────────────────────────────────────┐
│  [Home][●Acad][Fees][Msgs][Prof]           │
│  ATTENDANCE                                │
│  ┌─────────────────────────────────────┐    │
│  │  JUNE 2026                    ◀ ▶  │    │
│  │  M T W T F S S                     │    │
│  │  ● ● ● ● ● ─ ─                     │    │
│  │  ● ● ○ ● ● ─ ─                     │    │
│  │  ● ● ✕ ● ● ─ ─                     │    │
│  │  94.2% present · 1 late · 1 absent│    │
│  └─────────────────────────────────────┘    │
│  PERFORMANCE                               │
│  ┌─────────────────────────────────────┐    │
│  │  [DONUT 88%]  Maths 92%            │    │
│  │               Science 88%          │    │
│  │               English 85%          │    │
│  │               Hindi 90%            │    │
│  └─────────────────────────────────────┘    │
│  MARKS TREND                               │
│  ┌─────────────────────────────────────┐    │
│  │  [SPARKLINE]                        │    │
│  │  T1: 87% · T2: 91% · ↑              │    │
│  └─────────────────────────────────────┘    │
│  SYLLABUS COVERAGE                         │
│  Maths    ━━━━━━━━━━━━░ 78%               │
│  Science  ━━━━━━━━━━━━━ 95%               │
│  English  ━━━━━━━━━━░░░ 65%               │
│  REPORT CARD                               │
│  [📄 Term 2 · A+ · View →]               │
└─────────────────────────────────────────────┘
```

**Color:** Calendar: present=success, late=warning, absent=danger, future=cream. Donut: distinct color per subject. Sparkline: accent line + 15% alpha area. Syllabus: Arctic progress bars.

**Motion:** Donut Reveal (800ms). Sparkline Draw (1100ms). Bars animated width (600ms). Staggered Fade Up per section.

**Interaction:** Calendar month nav (◀▶, 300ms crossfade). Day tap → day detail (Modal Rise). Donut segment tap → subject marks. Sparkline tap → marks history. Report card tap → full view. Pull to refresh.

**Rationale:** Multiple lenses on "how is my child doing?" — attendance, performance, trend, coverage, formal assessment. Visual variety prevents data fatigue. Color-coded calendar is instantly scannable.

**Inspiration:** Apple Health (calendar, chart variety), Stripe (data viz), Linear (clean data).

---

### 11.3 Parent Fees

**Emotional Goal:** Clarity and control. Fees are a stress point. Clear, calm, actionable. "You owe this much, due by this date, pay here."

**Visual Hierarchy:**
1. Outstanding Summary — hero card, amount due + due date + pay button
2. Payment History — list of past payments with status badges
3. Fee Notices — announcements about fee changes

**Layout:**
```
┌─────────────────────────────────────────────┐
│  [Home][Acad][●Fees][Msgs][Prof]           │
│  ┌─────────────────────────────────────┐    │
│  │  OUTSTANDING FEES (warning tint)    │    │
│  │  ₹ 12,500                          │    │
│  │  Due by 30 June 2026               │    │
│  │  [Pay Now]                         │    │
│  └─────────────────────────────────────┘    │
│  PAYMENT HISTORY                           │
│  ₹15,000 · 15 May · Term 1      ✓ Paid   │
│  ₹8,000  · 10 Jan · Transport   ✓ Paid   │
│  ₹5,000  · 05 Sep · Lab         ✓ Paid   │
│  FEE NOTICES                               │
│  [📋 Fee Structure 2026-27 · Read →]      │
└─────────────────────────────────────────────┘
```

**Color:** Outstanding: warning soft tint, warning ink amount, warning tone button. Payments: Data amount, Caption date, Success badge. Notices: Action Card.

**Motion:** Amount Count-Up (400ms). Payment rows Fade Up (50ms stagger).

**Interaction:** "Pay Now" → Modal Rise to payment gateway. Payment tap → receipt (Modal Rise). Notice tap → detail (Forward Slide). Pull to refresh.

**Rationale:** Single clear "you owe this" hero, then supporting detail. Warning color communicates urgency without alarm. Payment history provides reassurance. The layout answers: what do I owe, when, and can I see proof of past payments?

**Inspiration:** Stripe (payment clarity), Apple Card (statement design), Linear (clean financial UI).

---

### 11.4 Parent Messages

**Emotional Goal:** Connection. Talk to the right teacher. WhatsApp-style familiarity with school-appropriate formality.

**Visual Hierarchy:**
1. Conversation List — teacher/admin names, last message, timestamp, unread badge
2. Thread View — message bubbles, input bar

**Layout (List):**
```
┌─────────────────────────────────────────────┐
│  [Home][Acad][Fees][●Msgs][Prof]           │
│  Messages                                  │
│  ┌─────────────────────────────────────┐    │
│  │ [Avatar] Mrs. Priya (Class Teacher)│    │
│  │          Aarav did well in...  2:30│    │
│  │                          [3]       │    │  ← Unread badge
│  ├─────────────────────────────────────┤    │
│  │ [Avatar] School Office              │    │
│  │          PTM scheduled for...  Yesterday│ │
│  ├─────────────────────────────────────┤    │
│  │ [Avatar] Mr. Rajesh (Maths)        │    │
│  │          Homework submitted ✓  Mon│    │
│  └─────────────────────────────────────┘    │
└─────────────────────────────────────────────┘
```

**Layout (Thread):**
```
┌─────────────────────────────────────────────┐
│  ◀ [Avatar] Mrs. Priya                    │
│     Class Teacher · 8-A                   │
│  ┌─────────────────────────────────────┐    │
│  │  [Teacher bubble] Aarav did well   │    │  ← Left aligned, cream
│  │  in today's test. 92%!             │    │
│  │                          2:30 PM   │    │
│  │                                     │    │
│  │  [Parent bubble] That's wonderful! │    │  ← Right aligned, accent tint
│  │  Thank you so much.       2:32 PM  │    │
│  └─────────────────────────────────────┘    │
│  ┌─────────────────────────────────────┐    │
│  │  [📎] Type a message...    [Send]  │    │
│  └─────────────────────────────────────┘    │
└─────────────────────────────────────────────┘
```

**Color:** Teacher bubbles: cream fill, ink text. Parent bubbles: accent tint fill, ink text. Unread badge: danger circle. Input bar: white, hairline top border. Send button: accent deep.

**Typography:** Sender name H4 14/600. Last message Body 14/400 ink2. Timestamp Caption 12/500 ink3. Message text Body 14/400. Time Caption 11/400 ink3.

**Motion:** Thread enter: Forward Slide. Messages Fade Up from bottom (200ms). New message: Slide In from bottom.

**Interaction:** Conversation tap → thread (Forward Slide). Back → list (Backward Slide). Send → message appears + sends. Attachment (📎) → file picker. Scroll to bottom on new message. Unread badge clears on open.

**Rationale:** WhatsApp-style familiarity reduces learning curve. Left/right bubble alignment is universally understood. Class teacher flagged first in list — most important contact. Input bar always visible, never hidden.

**Inspiration:** WhatsApp (bubble layout, familiarity), Apple Messages (thread design), Linear (comment thread aesthetics).

---

### 11.5 Parent Profile

**Emotional Goal:** Ownership and personalization. "This is my space." Child profile card, settings, theme, logout.

**Visual Hierarchy:**
1. Child Profile Card — collectible card with child photo, name, class, school, QR code
2. Settings List — theme, notifications, language, privacy
3. Logout

**Layout:**
```
┌─────────────────────────────────────────────┐
│  [Home][Acad][Fees][Msgs][●Prof]           │
│  ┌─────────────────────────────────────┐    │
│  │  COLLECTIBLE PROFILE CARD           │    │
│  │  [Avatar 80pt]                     │    │
│  │  Aarav Sharma                      │    │
│  │  Class 8-A · Delhi Public School   │    │
│  │  [QR Code 120pt]                   │    │
│  │  Student ID: DPS-2024-1024         │    │
│  └─────────────────────────────────────┘    │
│  SETTINGS                                  │
│  [🎨 Appearance        →]                 │
│  [🔔 Notifications     →]                 │
│  [🌐 Language          →]                 │
│  [🔒 Privacy           →]                 │
│  [❓ Help & Support    →]                 │
│  ┌─────────────────────────────────────┐    │
│  │  Log Out                            │    │  ← Ghost, Destructive
│  └─────────────────────────────────────┘    │
└─────────────────────────────────────────────┘
```

**Color:** Profile card: gradient hero (accent deep → accent), white text, QR on white sub-card. Settings: Action Card rows. Logout: Ghost variant.

**Motion:** Profile card Scale In (400ms, Soft). Settings rows Fade Up (50ms stagger).

**Interaction:** Profile card tap → full-screen card view (Modal Rise). Theme → Theme Picker (Modal Rise). Logout → Confirm Dialog.

**Rationale:** The collectible card concept makes the child's identity feel precious — like a trading card or ID badge. It's unique and premium. Settings are simple and scannable. Logout is present but not prominent — it's a destructive action that requires confirmation.

**Inspiration:** Apple Wallet (card design), Linear (settings list), Airbnb (profile card).

---

## 12. School Admin Portal

### 12.1 Admin Home

**Emotional Goal:** Command and control. The admin should feel like they're looking at the bridge of a ship — everything visible, everything actionable, everything under control.

**Visual Hierarchy:**
1. Greeting Header — admin name, school, session, last updated
2. Smart Insights — actionable, data-driven carousel
3. School Pulse — flagship gauge 0–100 + category breakdown
4. KPI Grid — key metrics with trend deltas
5. Campus Health — attendance trend chart
6. Fee Collection — bars + summary
7. Parent Engagement — leaderboard
8. Communication Center — actionable cards
9. Event Dashboard — upcoming + recent
10. Teacher Spotlight — top performer
11. Student Achievements — carousel
12. Birthdays — celebration widget
13. Live Activity — timeline feed
14. Analytics Entry — risk monitor cards

**Layout (Top Sections):**
```
┌─────────────────────────────────────────────┐
│  ┌─────────────────────────────────────┐    │
│  │  GREETING HEADER                    │    │
│  │  Good morning, Rajesh              │    │
│  │  Delhi Public School · 2026-27     │    │
│  │  Updated 2 min ago · [Refresh]     │    │
│  └─────────────────────────────────────┘    │
│  SMART INSIGHTS                            │
│  [Insight 1] [Insight 2] [Insight 3]      │  ← Horizontal scroll
│  ┌─────────────────────────────────────┐    │
│  │  SCHOOL PULSE                       │    │
│  │  ┌──────────┐  Attendance  92%     │    │
│  │  │  GAUGE   │  Performance 85%    │    │
│  │  │   87.3   │  Engagement  78%    │    │
│  │  │  /100    │  Fee Collection 94% │    │
│  │  └──────────┘                      │    │
│  └─────────────────────────────────────┘    │
│  ┌──────────┐ ┌──────────┐               │
│  │ 1,240    │ │ 94.2%    │               │  ← KPI grid (2-up)
│  │ Students │ │ Attendance│              │
│  │ ↑ 12     │ │ ↑ 1.2%   │               │  ← Trend delta
│  └──────────┘ └──────────┘               │
│  ┌──────────┐ ┌──────────┐               │
│  │ ₹4.2L    │ │ 87       │               │
│  │ Collected│ │ Teachers │               │
│  │ ↑ 8%     │ │ → 3 new  │               │
│  └──────────┘ └──────────┘               │
│  CAMPUS HEALTH                             │
│  ┌─────────────────────────────────────┐    │
│  │  [ATTENDANCE TREND CHART]           │    │
│  │  Last 7 days · 92% avg             │    │
│  └─────────────────────────────────────┘    │
│  FEE COLLECTION                            │
│  ┌─────────────────────────────────────┐    │
│  │  [BAR CHART] Term 1 · Term 2 · Term 3│   │
│  │  94% collected · ₹4.2L / ₹4.5L     │    │
│  └─────────────────────────────────────┘    │
│  [Home][People][Records][Comms][Settings] │
└─────────────────────────────────────────────┘
```

**Color:** Greeting: H3 ink, school name Caption ink2. Pulse gauge: teal fill, category bars in semantic colors. KPI cards: Data Large ink, Label ink2, trend delta in success/danger. Charts: accent/semantic colors. Insights: accent tint cards.

**Typography:** Greeting H3 17/700. KPI value Data Large 22 DM Mono. KPI label Label 11/700 UPPER. Trend delta Caption 12/500. Gauge value Data Large 22.

**Motion:**
```
[0ms]    Tab content → Quiet Fade (300ms)
[100ms]  Greeting → Fade Up
[200ms]  Insights → Fade Up
[300ms]  Pulse gauge → Fade Up
[500ms]  Gauge sweep → Gauge Sweep (700ms)
[500ms]  Gauge value → Count-Up (400ms)
[400ms]  KPI grid → Fade Up (80ms stagger)
[600ms]  KPI values → Count-Up (400ms)
[500ms]  Campus health → Fade Up
[700ms]  Attendance chart → Sparkline Draw (1100ms)
[600ms]  Fee collection → Fade Up
[800ms]  Fee bars → Bar Grow (600ms)
```

**Interaction:** Pull to refresh (all dashboard data). Insight tap → relevant screen. Gauge tap → analytics detail. KPI tap → detail screen. Chart tap → full chart view. Each section is an Action Card when tappable.

**Rationale:** The admin home is a **command center**. 14 sections provide comprehensive oversight. The School Pulse gauge is the flagship — a single number that summarizes school health. KPI grid provides the "at a glance" metrics. Charts provide trend context. The density is intentional — admins need data, not whitespace. But hierarchy prevents overwhelm: gauge > KPIs > charts > lists.

**Design Note:** Each of the 14 sections should be an independent composable widget, enabling future customization (drag-to-reorder, hide/show widgets).

**Inspiration:** Stripe Dashboard (command center, KPI grid, charts), Apple Health (summary gauges), Linear (analytics density), Airbnb (host dashboard storytelling).

---

### 12.2 Admin People

**Emotional Goal:** Know your people. Students, teachers, staff — searchable, filterable, actionable.

**Visual Hierarchy:**
1. Search bar + filter chips
2. Tabbed views: Students | Teachers | Staff
3. List rows with avatar, name, class/role, status
4. FAB or header action: Add new

**Layout:**
```
┌─────────────────────────────────────────────┐
│  [●Home][People][Records][Comms][Settings] │
│  People                                    │
│  [🔍 Search students, teachers...]        │
│  [Students] [Teachers] [Staff]             │  ← Top Tabs
│  [All] [Class 8] [Class 9] [Active]       │  ← Filter chips
│  ┌─────────────────────────────────────┐    │
│  │ [Avatar] Aarav Sharma    Class 8-A │    │
│  │           Roll 1024      ● Active  │    │
│  ├─────────────────────────────────────┤    │
│  │ [Avatar] Priya Patel     Class 8-A │    │
│  │           Roll 1025      ● Active  │    │
│  ├─────────────────────────────────────┤    │
│  │ [Avatar] Rohan Kumar     Class 8-B │    │
│  │           Roll 1026      ○ Inactive│    │
│  └─────────────────────────────────────┘    │
│  Showing 1,240 of 1,240                   │
│  [+ Add Student]                           │
└─────────────────────────────────────────────┘
```

**Color:** Search bar white. Filter chips: inactive=cream, active=accent tint+accent deep. Status dot: success (active), ink3 (inactive). "Add" button: Primary Teal, floating.

**Motion:** List rows Fade Up (50ms stagger). Tab switch: Quiet Fade (300ms). Chip select: Snappy spring scale.

**Interaction:** Search: debounced (300ms), filters list live. Tab switch: changes data source. Filter chip: multi-select, toggles filter. Row tap → student/teacher profile (Forward Slide). "Add" → onboarding form (Modal Rise).

**Inspiration:** Linear (list + filter pattern), Stripe (search + tab), Apple (Contacts list).

---

### 12.3 Admin Records

**Emotional Goal:** The archive. Everything the school has recorded, organized and accessible. Not a dumping ground — a curated library.

**Visual Hierarchy:** Grid of action cards, each representing a record category:
- Classes & Subjects
- Class Performance
- Teacher Performance
- Daily Attendance
- Academic Calendar
- Results Publishing
- PEWS (Early Warning)
- Health Records
- Alumni
- Scholarships
- ID Cards

**Layout:**
```
┌─────────────────────────────────────────────┐
│  [Home][●Records][Comms][Settings]         │
│  Records                                   │
│  ┌──────────┐ ┌──────────┐               │
│  │ 📊 Class  │ │ 👥 Teacher│              │  ← 2-up grid
│  │ Performance│ │ Performance│            │
│  └──────────┘ └──────────┘               │
│  ┌──────────┐ ┌──────────┐               │
│  │ 📅 Academic│ │ 📝 Results│             │
│  │ Calendar  │ │ Publish  │              │
│  └──────────┘ └──────────┘               │
│  ┌──────────┐ ┌──────────┐               │
│  │ ⚠ PEWS    │ │ 🏥 Health │             │
│  │ Early Warn│ │ Records  │              │
│  └──────────┘ └──────────┘               │
│  ┌──────────┐ ┌──────────┐               │
│  │ 🎓 Alumni │ │ 🏅 Schol- │             │
│  │           │ │ arships  │              │
│  └──────────┘ └──────────┘               │
│  ┌──────────┐                            │
│  │ 🪪 ID     │                            │
│  │ Cards    │                            │
│  └──────────┘                            │
└─────────────────────────────────────────────┘
```

**Color:** Action Cards with category icons in accent tint circles. 2-up grid. Each card: icon (24pt in 40pt circle) + title (H4) + subtitle (Caption).

**Motion:** Cards Fade Up (80ms stagger). Tap: scale 0.98 + Forward Slide.

**Interaction:** Card tap → respective record screen (Forward Slide). Each record screen has its own search/filter/list pattern.

**Rationale:** A grid of categorized entry points is more scannable than a single long list. The 2-up grid fills the 440pt width perfectly (each card ~200pt). Icons provide instant recognition. The subtitle clarifies what's inside.

**Inspiration:** Apple Settings (grid of categories), Linear (card grid), Stripe (dashboard widgets).

---

### 12.4 Admin Comms

**Emotional Goal:** Reach your audience. Messages, announcements, events — the communication hub.

**Visual Hierarchy:**
1. Messages — conversation list (parents, teachers)
2. Scheduled Messages — queued for future delivery
3. Announcements — school-wide broadcasts
4. Leave Requests — parent-submitted leave applications
5. Event Registration — PTM, sports day, etc.

**Layout:** Tabbed or list-based, similar to People screen. Each category is an Action Card or list.

---

### 12.5 Admin Settings

**Emotional Goal:** Configure with confidence. School profile, branding, transport, calendar — all the knobs and dials.

**Visual Hierarchy:** List of settings categories, each an Action Card:
- School Profile (name, address, logo)
- Branding Kit (colors, themes)
- Transport Management (routes, buses, drivers)
- School Day Config (timings, periods)
- Academic Year Management (sessions, terms)
- Analytics Dashboard (insights, trends)

**Color:** Action Cards with settings icons. Each tap → Forward Slide to detail screen.

**Interaction:** Branding → live preview of color changes (Quiet Fade on theme switch). Academic Year → calendar-based term editor.

**Inspiration:** Apple Settings (category list), Stripe (account settings), Linear (workspace settings).

---

## 13. Teacher Portal

### 13.1 Teacher Home

**Emotional Goal:** Ready for the day. The teacher opens the app and immediately knows: what's my schedule, what needs my attention, have I checked in. Efficient, focused, data-forward.

**Visual Hierarchy:**
1. Greeting Hero — name, check-in ring, today's class count
2. Attendance Summary — swipe-expand card with today's classes
3. Today's Schedule — period-by-period timeline
4. Reminders — obligations (pending attendance, marks, homework)
5. Action Cards — PEWS alerts, health alerts, ID card, report review, messages, events

**Layout:**
```
┌─────────────────────────────────────────────┐
│  ┌─────────────────────────────────────┐    │
│  │  GREETING HERO                      │    │
│  │  Good morning, Mrs. Priya          │    │
│  │  [Check-in Ring: Not checked in]   │    │
│  │  Tap to check in →                  │    │
│  └─────────────────────────────────────┘    │
│  ┌─────────────────────────────────────┐    │
│  │  ATTENDANCE TODAY (swipe-expand)   │    │
│  │  Face 0: [Ring 3/5] 60% done      │    │
│  │  Face 1: Class 8-A ✓  Class 8-B ✓ │    │
│  │         Class 9-A ○  Class 9-B ○ │    │
│  └─────────────────────────────────────┘    │
│  TODAY'S SCHEDULE                          │
│  ┌─────────────────────────────────────┐    │
│  │  ● 08:00  Class 8-A · Maths        │    │
│  │  ● 09:00  Class 8-B · Maths        │    │
│  │  ● 10:00  Class 9-A · Maths        │    │
│  │  ○ 11:00  Free period              │    │
│  │  ○ 12:00  Class 9-B · Maths        │    │
│  └─────────────────────────────────────┘    │
│  WHAT NEEDS ME                             │
│  ┌─────────────────────────────────────┐    │
│  │  ⚠ 3 classes pending attendance    │    │
│  │  📝 2 homework assignments to grade │    │
│  │  📊 1 mark entry pending            │    │
│  └─────────────────────────────────────┘    │
│  [⚠ PEWS] [🏥 Health] [🪪 ID] [📊 Reports]│
│  [💬 Messages] [📅 Events]                │
│  [●Home][Update][Classes][Profile]        │
└─────────────────────────────────────────────┘
```

**Color:** Greeting: H3 ink. Check-in ring: accent deep (not checked in) → success (checked in). Schedule: filled dots past, outlined future. Obligations: warning tint for pending. Action cards: standard Action Card pattern.

**Typography:** Greeting H3 17/700. Ring value Data Large 22. Schedule time Data Small 13. Schedule class H4 14/600. Obligation count Data 15.

**Motion:**
```
[0ms]    Tab content → Quiet Fade (300ms)
[100ms]  Greeting hero → Fade Up
[200ms]  Check-in ring → Fade Up
[400ms]  Ring sweep → Gauge Sweep (700ms)
[300ms]  Attendance card → Fade Up
[400ms]  Schedule → Fade Up
[500ms]  Schedule items → Fade Up (80ms stagger)
[600ms]  Reminders → Fade Up
[700ms]  Action cards → Fade Up (60ms stagger)
```

**Interaction:** Check-in ring tap → biometric check-in (fingerprint/face). Attendance card swipe → expands to face 1 (class list). Schedule item tap → attendance marking for that class. Obligation tap → respective action (attendance/marks/homework). Action card tap → respective screen. Pull to refresh. Live clock refresh every 60s.

**Rationale:** The teacher home is **action-oriented**. The check-in ring is the first thing — "are you here?" The attendance summary is the primary task — "mark your classes." The schedule provides context. Reminders ensure nothing falls through. The swipe-expand card is a signature interaction — face 0 shows the summary, face 1 shows the detail, without leaving the screen.

**Inspiration:** Apple Health (ring metaphor for check-in), Linear (action-oriented dashboard), Stripe (task-focused layout), Airbnb (card-based actions).

---

### 13.2 Teacher Update

**Emotional Goal:** Get it done. The update tab is the teacher's workspace — attendance, marks, syllabus, homework. Fast, focused, no decoration. Pure efficiency.

**Visual Hierarchy:**
1. Scope Selector — class + section + subject (the gate)
2. Action Cards — Attendance, Marks, Syllabus, Homework

**Layout:**
```
┌─────────────────────────────────────────────┐
│  [Home][●Update][Classes][Profile]         │
│  ┌─────────────────────────────────────┐    │
│  │  SCOPE                              │    │
│  │  Class 8-A ▼  Section A ▼  Maths ▼ │    │
│  └─────────────────────────────────────┘    │
│  ┌──────────┐ ┌──────────┐               │
│  │ ✅ Attend-│ │ 📊 Marks │               │
│  │  ance    │ │          │              │
│  │ 28/30    │ │ 2 pending│              │
│  └──────────┘ └──────────┘               │
│  ┌──────────┐ ┌──────────┐               │
│  │ 📖 Syllabus│ │ 📝 Home- │             │
│  │  78%     │ │ work     │              │
│  │  covered │ │ 2 active │              │
│  └──────────┘ └──────────┘               │
└─────────────────────────────────────────────┘
```

**Color:** Scope selector: cream background, accent deep text, chevron icons. Action cards: Elevated, icon in accent tint circle, title H4, status Caption with semantic color.

**Interaction:** Scope selector: dropdown/bottom sheet for each dimension. Changing scope reloads all action cards. Action card tap → respective marking screen (Forward Slide).

**Rationale:** The scope selector is the gate — it ensures the teacher only sees classes they're assigned to. The 2×2 grid of action cards covers the four core teaching tasks. Each card shows current status, creating urgency and clarity.

**Inspiration:** Linear (scope selector pattern), Stripe (action grid), Apple (clean form design).

---

### 13.3 Teacher Classes

**Emotional Goal:** Know your students. A class list that drills down to individual students — their attendance, marks, and profile.

**Visual Hierarchy:**
1. Class List — assigned classes with student count
2. Class Detail — student roster with quick stats
3. Student Profile — attendance, marks, health, contact

**Layout (Class List):**
```
┌─────────────────────────────────────────────┐
│  [Home][Update][●Classes][Profile]         │
│  My Classes                                │
│  ┌─────────────────────────────────────┐    │
│  │  Class 8-A · Maths                  │    │
│  │  30 students · Class Teacher        │    │
│  │  Attendance: 94% · Marks: 88%      │    │
│  └─────────────────────────────────────┘    │
│  ┌─────────────────────────────────────┐    │
│  │  Class 8-B · Maths                  │    │
│  │  28 students                        │    │
│  │  Attendance: 91% · Marks: 85%      │    │
│  └─────────────────────────────────────┘    │
│  ┌─────────────────────────────────────┐    │
│  │  Class 9-A · Maths                  │    │
│  │  32 students                        │    │
│  └─────────────────────────────────────┘    │
└─────────────────────────────────────────────┘
```

**Color:** Class cards: Elevated. Class name H4. Student count Caption. Stats in Data Small with semantic colors.

**Interaction:** Class tap → class detail (Forward Slide) with student roster. Student tap → student profile (Forward Slide). Back → previous level (Backward Slide).

**Inspiration:** Apple (list → detail navigation), Linear (class card design), Stripe (roster layout).

---

### 13.4 Teacher Profile

**Emotional Goal:** My identity and settings. Who I am, what I teach, how I configure my experience.

**Visual Hierarchy:**
1. Identity Card — photo, name, subjects, classes
2. Leave — apply for leave, view status
3. Settings — theme, password, logout

**Layout:**
```
┌─────────────────────────────────────────────┐
│  [Home][Update][Classes][●Profile]         │
│  ┌─────────────────────────────────────┐    │
│  │  [Avatar 80pt]                     │    │
│  │  Mrs. Priya Sharma                 │    │
│  │  Maths · Class 8-A, 8-B, 9-A, 9-B │    │
│  │  Delhi Public School               │    │
│  └─────────────────────────────────────┘    │
│  LEAVE                                     │
│  [📅 Apply for Leave →]                   │
│  [📋 My Leave History →]                  │
│  SETTINGS                                  │
│  [🎨 Appearance →]                       │
│  [🔒 Change Password →]                   │
│  [❓ Help & Support →]                    │
│  [Log Out]                                │
└─────────────────────────────────────────────┘
```

**Color:** Identity card: gradient hero (accent deep → accent). Leave: Action Cards. Settings: Action Cards. Logout: Ghost.

**Interaction:** Leave apply → form with VDatePicker + reason VTextArea. Leave history → list with status badges. Logout → Confirm Dialog.

**Inspiration:** Apple (settings list), Linear (profile card), Airbnb (host profile).

---

# VOLUME IV — CROSS-CUTTING CONCERNS

---

## 14. Interaction Design Patterns

### 14.1 Navigation Patterns

| Pattern | Description | Usage |
|---------|-------------|-------|
| **Tab + Overlay** | Bottom nav tabs + full-screen overlays pushed above tab content | All portals |
| **Master-Detail** | List → detail drill-down within a tab | Teacher Classes, Admin People |
| **Wizard** | Multi-step forward flow with progress indicator | Onboarding, Link Child |
| **Modal Rise** | Bottom-up modal for focused tasks | Date picker, bottom sheet, confirm |
| **Scrim + Dialog** | Centered dialog over dimmed background | Confirmations, alerts |
| **Pull to Refresh** | Drag down to reload data | All list/dashboard screens |

### 14.2 Form Patterns

| Pattern | Description | Usage |
|---------|-------------|-------|
| **Single Field** | One input + button. Minimal friction. | Parent auth (phone) |
| **Progressive Disclosure** | Show optional fields in expandable "more" section | School onboarding |
| **Inline Validation** | Validate on blur, show error below field | All forms |
| **Smart Defaults** | Pre-fill from context (teacher scope, parent phone) | Marks, attendance |
| **Auto-correction** | Fix common input errors silently | Class/section auto-peeling |

### 14.3 Data Patterns

| Pattern | Description | Usage |
|---------|-------------|-------|
| **Skeleton First** | Show layout-matching skeleton, not spinner | All data screens |
| **State Host** | Loading → Error → Empty → Content with crossfade | All data screens |
| **Pull to Refresh** | User-initiated data reload | All lists/dashboards |
| **Staggered Entrance** | Items appear in sequence, 50ms stagger | All lists |
| **Count-Up** | Numbers animate from 0 to target | KPIs, stats |
| **Chart Reveal** | Charts animate on first render | Donut, bars, sparkline, gauge |

### 14.4 Feedback Patterns

| Pattern | Description | Usage |
|---------|-------------|-------|
| **Snackbar** | Bottom-anchored transient message, 4s auto-dismiss | Action confirmations |
| **Confirm Dialog** | Destructive action gate with clear consequence | Delete, logout, reject |
| **Button Success** | Loading → checkmark pop → fade back to text | Submit, save, approve |
| **Badge Appear** | Scale 0.8→1.0 spring when badge appears | Notification counts |
| **Haptic** | Light impact on tab switch, medium on confirm | Bottom nav, confirm |

### 14.5 Empty State Patterns

Every empty state must include:
1. **Icon** — relevant to the content type (message bubble, calendar, document)
2. **Title** — what's empty ("No messages yet")
3. **Body** — why and what to expect ("When your child's teacher sends a message, it'll appear here")
4. **Action** (when applicable) — CTA to create/resolve ("Link your child to get started")

**Rationale:** Empty states are design moments, not error states. They guide the user toward the next action and set expectations for what the screen will contain once populated.

---

## 15. Accessibility Design Guidelines

### 15.1 Visual Accessibility

| Guideline | Standard | Implementation |
|-----------|----------|---------------|
| **Color contrast** | WCAG AA (4.5:1 normal, 3:1 large) | All text/background pairs verified |
| **Not color alone** | Never use color as sole indicator | Badges include icon + text; status dots have labels |
| **Focus indicator** | 2pt ring, 4pt offset, teal deep | All interactive elements |
| **Min touch target** | 44pt | All buttons, cards, list rows |
| **Font scaling** | Support 0.85×–1.3× | Layout never breaks at 1.3× |
| **High contrast** | Dedicated high-contrast theme | Available in Theme Picker |

### 15.2 Motor Accessibility

| Guideline | Implementation |
|-----------|---------------|
| **Large tap targets** | 44pt minimum, 52pt for primary actions |
| **No swipe-only actions** | All swipe actions have button alternatives |
| **Drag to dismiss** | Bottom sheets also closeable via tap scrim or back button |
| **No time-limited interactions** | OTP entry has no countdown timeout (only resend cooldown) |

### 15.3 Cognitive Accessibility

| Guideline | Implementation |
|-----------|---------------|
| **Clear labels** | All buttons and links use plain language |
| **Consistent navigation** | Bottom nav in same position across all portals |
| **Error prevention** | Confirmation dialogs for destructive actions |
| **Error recovery** | Error states include retry buttons with clear messaging |
| **Progressive disclosure** | Complex forms broken into steps with progress indicators |

### 15.4 Motion Accessibility

| Guideline | Implementation |
|-----------|---------------|
| **Reduce Motion** | Disable staggered entrances, decorative animations, slide transitions → crossfades |
| **No auto-playing video** | No video content in-app |
| **No flashing** | No animations flash more than 3 times per second |
| **Respect system setting** | Check platform Reduce Motion preference |

---

## 16. Inspiration & References

### 16.1 Per-Screen Inspiration Matrix

| Screen | Primary Inspiration | Secondary | What We Take |
|--------|-------------------|-----------|-------------|
| Splash | Apple launch splash | Linear loading | Sequential brand reveal, premium timing |
| Landing | Airbnb landing | Stripe Atlas, Linear marketing | Storytelling, dual-audience, trust-building |
| Parent Auth | Stripe forms | Apple phone auth | Single-field simplicity, focus treatment |
| OTP | Apple OTP | Stripe verification | Auto-fill, box design, shake error |
| Admin Auth | Linear admin auth | Stripe login | Credential form, password toggle |
| School Onboarding | Stripe Atlas | Apple setup assistant | Wizard pattern, progress bar, skip |
| Parent Link Child | Airbnb search | Stripe multi-step | Live search, wizard, auto-correction |
| Parent Home | Apple Health | Airbnb dashboard | Ring metaphor, aurora hero, timeline |
| Parent Academics | Apple Health | Stripe dashboard | Calendar grid, chart variety, data viz |
| Parent Fees | Stripe payments | Apple Card | Clear outstanding hero, payment history |
| Parent Messages | WhatsApp | Apple Messages | Bubble layout, familiarity, input bar |
| Parent Profile | Apple Wallet | Linear settings | Collectible card, settings list |
| Admin Home | Stripe Dashboard | Apple Health, Linear | Command center, gauge, KPI grid, charts |
| Admin People | Linear list | Stripe search, Apple Contacts | Search + filter + tab pattern |
| Admin Records | Apple Settings | Linear card grid | Category grid, icon recognition |
| Admin Settings | Apple Settings | Stripe account, Linear workspace | Category list, branding preview |
| Teacher Home | Apple Health | Linear, Stripe | Check-in ring, swipe-expand, action cards |
| Teacher Update | Linear scope | Stripe action grid | Scope selector, 2×2 action grid |
| Teacher Classes | Apple list-detail | Linear class cards | Drill-down navigation, roster |
| Teacher Profile | Apple settings | Linear profile | Identity card, leave, settings |

### 16.2 Design System References

| Element | Primary Reference | What We Take |
|---------|------------------|-------------|
| Color tokens | Apple Health, Linear | Semantic separation, navy-tinted ink |
| Typography | Stripe, Vercel | Humanist sans + mono data, tabular figures |
| Spacing | Apple HIG, Material 3 | Base-4 system, consistent rhythm |
| Elevation | Linear, Notion | Hairline border + soft shadow, navy tint |
| Motion | iOS, Linear | Spring physics, staggered revelation |
| Components | Apple, Stripe, Linear | Button press depth, card weight, input focus |
| Charts | Stripe, Apple Health | Canvas charts, animated reveals, minimal decoration |
| Empty states | Airbnb, Linear | Illustrated + CTA, guidance over apology |
| Skeletons | Facebook, Stripe | Layout-matching, shimmer aesthetic |

---

## 17. Dark Mode Design Guidelines

### 17.1 Philosophy

Dark mode is a **different atmosphere** — the same room at dusk. Surfaces deepen, shadows disappear, accents gain luminosity. It is not an inversion.

### 17.2 Key Principles

1. **No pure black** — Use navy-black (`#0F0E1A`) for background. Pure black is harsh on OLED.
2. **Depth via surface color** — Card (`#1A1830`) is lighter than background (`#0F0E1A`). Cream (`#252338`) is darker than card. Three tiers create spatial hierarchy.
3. **Shadows suppressed** — In dark mode, shadows are invisible. Depth is communicated entirely through surface color differential.
4. **Accents gain luminosity** — Teal deepens (`#14B8A6`), accent lightens (`#A78BFA`) for visibility on dark surfaces.
5. **Borders become more visible** — Hairline (`#353350`), border1 (`#3D3A52`) are more prominent than in light mode because shadows are absent.
6. **Text inverts** — Ink becomes near-white (`#F0EFF5`), ink2 becomes lavender-gray (`#B0ABB8`), ink3 becomes muted (`#7C7890`).
7. **Semantic colors preserved** — Success/warning/danger soft fills darken, inks lighten, maintaining the same semantic meaning.

### 17.3 Per-Element Dark Mode Treatment

| Element | Light | Dark | Notes |
|---------|-------|------|-------|
| Card | White + shadow | `#1A1830`, no shadow | Depth from color, not shadow |
| Input (resting) | Cream fill | `#252338` fill | Darker than card |
| Input (focus) | White + teal border | `#1A1830` + teal border | Lighter than cream |
| Snackbar | Navy deep fill | Card fill + ink text | Inverted contrast |
| Bottom nav | White + raised shadow | Card, no shadow | Border-only separation |
| Shimmer | Cream + white gradient | `#252338` + `#1A1830` gradient | Darker shimmer |
| Aurora hero | Lavender 8% wash | Accent 4% wash | Reduced opacity for dark |

### 17.4 Transitions

- Theme switch: 300ms Quiet Fade (crossfade). No slide, no scale.
- All elements crossfade simultaneously — no staggered transition.
- The user should perceive the room "dimming" or "brightening," not elements changing individually.

---

*End of Product Design Specification — Version 2.*
