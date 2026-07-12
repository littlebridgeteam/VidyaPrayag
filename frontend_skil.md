# Frontend Design Skill

> Comprehensive, actionable reference for frontend UI/UX design — synthesizing design philosophy, reasoning engines, style systems, motion discipline, accessibility, performance, and pre-delivery verification.

---

## 1. Design Philosophy

Approach every design as the design lead at a small studio known for giving every client a visual identity that could not be mistaken for anyone else's. Make deliberate, opinionated choices about palette, typography, and layout specific to the brief. Take one real aesthetic risk you can justify.

### Ground It in the Subject

- If the brief doesn't pin down the product, pin it yourself: name one concrete subject, its audience, and the page's single job. State your choice.
- The subject's own world — materials, instruments, artifacts, vernacular — is where distinctive choices come from.
- Build with real content throughout. Never use lorem ipsum.

### Core Principles

- **The hero is a thesis.** Open with the most characteristic thing in the subject's world. A big number + small label + gradient accent is the template answer — only use if truly the best option.
- **Typography carries personality.** Pair display and body faces deliberately. Make type treatment memorable, not neutral.
- **Structure is information.** Numbering, eyebrows, dividers should encode something true about content. Numbered markers (01/02/03) only if content is actually a sequence.
- **Leverage motion deliberately.** An orchestrated moment lands harder than scattered effects. Extra animation contributes to AI-generated feeling.
- **Match complexity to vision.** Maximalist needs elaborate execution; minimal needs precision.

### AI Default Clusters to Avoid

1. Warm cream (~#F4F1EA) + serif display + terracotta accent
2. Near-black + acid-green or vermilion accent
3. Broadsheet + hairline rules + zero radius + dense columns

These are defaults, not choices. Don't spend freedom on these.

---

## 2. Design Process

### Two-Pass Method

**Pass 1 — Brainstorm:** Color (4–6 hex), Type (2+ roles), Layout (prose + ASCII wireframes), Signature (one memorable element).

**Pass 2 — Critique:** Review against brief. If any part reads like generic default — revise. Say what changed and why. Only after confirming uniqueness, start coding.

### Restraint

- Spend boldness in one place. Keep everything else quiet.
- Not taking a risk can be a risk itself.
- Quality floor without announcing it: responsive, keyboard focus, reduced motion.
- Critique as you build. Take screenshots.
- Chanel's advice: remove one accessory before leaving.
- Keep notes about what you've tried.

### CSS Warning

Type-based (`.section`) and element-based (`.cta`) selectors can cancel each other out, especially with paddings/margins between sections.

---

## 3. Writing in Design

Words are design material, not decoration.

- Write from the user's side. "Notifications," not "webhook config."
- Active voice. "Save changes," not "Submit." Action name consistent: "Publish" → "Published."
- Specific > clever. Plain terms, not selling.
- Failure = direction, not mood. Errors don't apologize, never vague. Empty screen = invitation to act.
- Conversational register: plain verbs, sentence case, no filler.
- One job per element. Nothing does double duty.

---

## 4. Design Dials

Three optional 1–10 dials biasing design generation.

**Variance:** 1–3 Centered/Minimal | 4–7 Balanced/Modern | 8–10 Bold/Asymmetric

**Motion:** 1–3 Subtle | 4–7 Standard | 8–10 Complex

**Density:** 1–3 Spacious (md:24px) | 4–7 Standard (md:16px) | 8–10 Dense (md:8px)

---

## 5. Style Catalog

| Style | Best For | Perf | A11y |
|-------|----------|------|------|
| Minimalism & Swiss | Enterprise, dashboards, docs | Excellent | WCAG AAA |
| Neumorphism | Health/wellness, meditation | Good | Low contrast |
| Glassmorphism | Modern SaaS, overlays | Good | Ensure 4.5:1 |
| Brutalism | Portfolios, artistic, editorial | Excellent | WCAG AAA |
| 3D & Hyperrealism | Gaming, product showcase | Poor | Not accessible |
| Vibrant & Block-based | Startups, agencies, youth | Good | Ensure WCAG |
| Motion-Driven | Portfolios, storytelling | Good | Reduced-motion |
| Micro-interactions | Mobile apps, touchscreen | Excellent | Good |
| Inclusive Design | Public services, healthcare | Excellent | WCAG AAA |
| Neubrutalism | Gen Z, startups | Excellent | WCAG AAA |
| Bento Box Grid | Dashboards, product pages | Excellent | WCAG AA |
| E-Ink / Paper | Reading apps, journals | Excellent | WCAG AAA |
| Spatial UI (VisionOS) | Spatial computing, VR/AR | Moderate | Contrast risks |

Each style includes: primary colors (hex), effects specs, framework ratings, complexity, implementation checklist, CSS variables, AI prompt keywords.

---

## 6. Color Systems

### Semantic Roles

Primary, On Primary, Secondary, Accent/CTA, Background, Foreground, Card, Muted, Muted Foreground, Border, Destructive, Ring — each as `--color-[role]` CSS variable.

### Selected Palettes

| Product | Primary | Accent | Background |
|---------|---------|--------|------------|
| SaaS | #2563EB | #EA580C | #F8FAFC |
| E-commerce | #059669 | #EA580C | #ECFDF5 |
| Luxury | #1C1917 | #A16207 | #FAFAF9 |
| Financial | #0F172A | #22C55E | #020617 |
| Healthcare | #0891B2 | #059669 | #ECFEFF |
| Education | #4F46E5 | #EA580C | #EEF2FF |
| Gaming | #7C3AED | #F43F5E | #0F0F23 |
| Fintech | #F59E0B | #8B5CF6 | #0F172A |
| AI/Chatbot | #7C3AED | #0891B2 | #FAF5FF |
| Government | #0F172A | #0369A1 | #F8FAFC |

### WCAG Contrast

- Normal text: min 4.5:1 | Large text (18pt+): min 3:1 | AAA: 7:1 normal
- Never convey info by color alone — pair with icons/text/patterns

---

## 7. Typography Pairings

| Pairing | Heading | Body | Best For |
|---------|---------|------|----------|
| Classic Elegant | Playfair Display | Inter | Luxury, fashion, editorial |
| Modern Professional | Poppins | Open Sans | SaaS, corporate, startups |
| Tech Startup | Space Grotesk | DM Sans | Tech, AI, dev tools |
| Minimal Swiss | Inter | Inter | Dashboards, admin, docs |
| Playful Creative | Fredoka | Nunito | Children's, educational |
| Bold Statement | Bebas Neue | Source Sans 3 | Marketing, events |
| Wellness Calm | Lora | Raleway | Health, meditation |
| Developer Mono | JetBrains Mono | IBM Plex Sans | Dev tools, documentation |
| Geometric Modern | Outfit | Work Sans | General, portfolios |
| Luxury Serif | Cormorant | Montserrat | Fashion, luxury e-commerce |
| Friendly SaaS | Plus Jakarta Sans | Plus Jakarta Sans | SaaS, B2B, productivity |
| Dashboard Data | Fira Code | Fira Sans | Analytics, data viz |

### Rules

Line height 1.5–1.75 | Line length 65–75ch | Modular scale (12,14,16,18,24,32) | `font-display: swap` | Min 16px body on mobile | Support system text scaling

---

## 8. Motion & Animation

### By Category

**Hover:** Subtle 150–200ms (< 2px move) | Standard 200–300ms (scale 1.02) | Complex 300–500ms (magnetic, max 1-2 elements)

**Scroll Reveal:** Subtle 300–400ms (y:12) | Standard 400–600ms (stagger 0.08, max 8) | Complex scrub pin (max 1-2 sections)

**Stagger:** Subtle 250–350ms (0.03 stagger) | Standard 300–450ms (back.out, bento center) | Complex 400–700ms (SplitText, headlines < 8 words)

**Page Transition:** Subtle 200–300ms fade | Standard 400–600ms overlay wipe | Complex 500–800ms Flip (max 1 pair)

**Loading:** Subtle 1200–1600ms shimmer (kill on mount) | Standard 800–1200ms dots (skip if < 300ms)

### Rules

150–300ms micro-interactions | Never > 500ms UI | `ease-out` enter, `ease-in` exit | `transform`+`opacity` only | Always `prefers-reduced-motion` | Max 1-2 animated per view | Infinite = loaders only | `will-change: transform`, remove after

---

## 9. UX Guidelines (99 Rules)

### Navigation
Smooth scroll (High) | Sticky nav padding (Medium) | Active state (Medium) | Back button predictable (High) | Deep linking (Medium) | Breadcrumbs for 3+ levels (Low)

### Animation
Max 1-2 per view (High) | 150-300ms timing (Medium) | `prefers-reduced-motion` (High) | Loading states (High) | Tap not hover for primary (High) | Infinite = loaders only (Medium) | `transform`/`opacity` only (Medium) | `ease-out`/`ease-in` not `linear` (Low)

### Layout
Z-index scale system (High) | Test overflow-hidden (Medium) | Safe areas for fixed (Medium) | Reserve space for async (High) | `dvh` not `100vh` (Medium) | Max-width 65-75ch (Medium)

### Touch
44x44px targets (High) | 8px gap (Medium) | No gesture conflicts (Medium) | `touch-action: manipulation` (Medium) | Haptic for confirmations (Low)

### Interaction
Visible focus rings (High) | Hover feedback (Medium) | Active/pressed state (Medium) | Disabled = opacity + cursor (Medium) | Loading buttons (High) | Error near problem (High) | Success feedback (Medium) | Confirm destructive (High)

### Accessibility
4.5:1 contrast (High) | No color-only (High) | Alt text (High) | Sequential headings (Medium) | `aria-label` for icon buttons (High) | Tab order = visual (High) | Semantic HTML (Medium) | `<label>` with `for` (High) | `aria-live`/`role=alert` (High) | Skip links (Medium) | Motion sensitivity (High)

### Performance
WebP + sizes (High) | `loading='lazy'` (Medium) | Code split (Medium) | `font-display: swap` (Medium) | `async`/`defer` (Medium) | Inline critical CSS (Medium)

### Forms
Visible labels (High) | Error below input (Medium) | Validate on blur (Medium) | Correct input types (Medium) | Required indicators (Medium) | Password toggle (Medium) | Submit feedback (High) | `inputmode` (Medium)

### Responsive
Mobile-first (Medium) | Test 320–1440 (Medium) | Touch targets on mobile (High) | 16px min body (High) | Viewport meta (High) | No horizontal scroll (High) | `max-width: 100%` images (Medium) | Table scroll/card (Medium)

### Feedback
Spinner/skeleton > 300ms (High) | Empty state + action (Medium) | Error recovery (Medium) | Progress indicators (Medium) | Toast 3-5s auto-dismiss (Medium) | Success confirmation (Medium)

### Content & Specialized
Truncate + expand (Medium) | Locale dates (Low) | Number formatting (Low) | Realistic samples (Low) | Skip onboarding (Medium) | Search autocomplete (Medium) | No results + suggestions (Medium) | Bulk actions (Low) | AI disclaimer (High) | AI streaming (Medium) | AI feedback loop (Low)

---

## 10. UI Reasoning Engine (161 Product Types)

Maps product types → recommended pattern, style priority, color mood, typography mood, key effects, decision rules, anti-patterns.

### Process

1. Detect product category from query
2. Find reasoning rules (exact → partial → keyword)
3. Extract style priority
4. Multi-domain search (product, style, color, typography, landing)
5. Select best matches via priority keywords
6. Build recommendation

### Selected Mappings

| Product | Pattern | Style Priority | Anti-Patterns |
|---------|---------|---------------|---------------|
| SaaS | Hero + Features + CTA | Glassmorphism + Flat | Excessive animation, dark default |
| E-commerce | Feature-Rich Showcase | Vibrant & Block | Flat without depth, text-heavy |
| Luxury | Feature-Rich Showcase | Liquid Glass + Glass | Playful colors |
| Financial | Data-Dense Dashboard | Dark Mode + Data-Dense | Light default, slow rendering |
| Healthcare | Social Proof | Neumorphism + Accessible | Neon, motion-heavy, AI gradients |
| Education | Feature-Rich Showcase | Claymorphism + Micro-int. | Dark modes, complex jargon |
| Agency | Storytelling-Driven | Brutalism + Motion | Corporate minimalism |
| Gaming | Feature-Rich Showcase | 3D + Retro-Futurism | Minimalist, static |
| Government | Minimal & Direct | Accessible + Minimalism | Ornate, low contrast, AI gradients |
| Fintech | Trust & Authority | Minimalism + Accessible | Playful, unclear fees, AI gradients |
| AI/Chatbot | Interactive Demo + Minimal | AI-Native + Minimalism | Heavy chrome, slow response |
| Productivity | Interactive Demo + Features | Flat + Micro-interactions | Complex onboarding, slow perf |

### Decision Rules (JSON per type)

```json
{"if_ux_focused":"prioritize-minimalism","if_data_heavy":"add-glassmorphism","must_have":"case-studies","if_luxury":"switch-to-liquid-glass","if_conversion_focused":"add-urgency-colors"}
```

### Full Coverage (161 types)

SaaS, Micro SaaS, E-commerce, Luxury, B2B, Financial, Analytics, Healthcare, Educational, Creative Agency, Portfolio, Gaming, Government, Fintech, Social Media, Productivity, Design System, AI/Chatbot, NFT/Web3, Creator Economy, Remote Work, Mental Health, Pet Tech, IoT, EV, Subscription Box, Podcast, Dating, Credentials, Knowledge Base, Hyperlocal, Beauty, Luxury Brand, Restaurant, Fitness, Real Estate, Travel, Hotel, Wedding, Legal, Insurance, Banking, E-learning, Non-profit, Music, Video, Job Board, Marketplace, Logistics, Agriculture, Construction, Automotive, Photography, Coworking, Home Services, Childcare, Senior Care, Medical, Pharmacy, Dental, Veterinary, Florist, Bakery, Brewery, Airline, News, Magazine, Freelancer, Marketing, Event, Membership, Newsletter, Digital Products, Church, Sports, Museum, Theater, Language Learning, Coding Bootcamp, Cybersecurity, Developer Tool, Biotech, Space Tech, Architecture, Quantum, Biohacking, Drone, Generative Art, Spatial Computing, Climate Tech, Personal Finance, Chat, Notes, Habit, Food Delivery, Ride Hailing, Recipe, Meditation, Weather, Diary, CRM, Inventory, Flashcard, Booking, Invoice, Grocery, Timer, Parenting, Scanner, Calendar, Password Manager, Expense Split, Voice Recorder, Bookmark, Translator, Calculator, Alarm, File Manager, Email, Puzzle, Trivia, Card/Board, Idle Clicker, Word Game, Arcade, Photo Editor, Video Editor, Drawing, Music Creation, Meme Maker, AI Photo, Link-in-Bio, Wardrobe, Plant Care, Book Tracker, Couple, Family Calendar, Mood Tracker, Gift Wishlist, Running GPS, Yoga, Sleep, Calorie, Period, Medication, Water, Fasting, Anonymous Community, Local Events, Study Together, Coding Challenge, Kids Learning, Music Learning, Parking, Transit, Road Trip, VPN, Emergency SOS, Wallpaper, White Noise, Home Decoration.

---

## 11. Landing Page Patterns (34)

| # | Pattern | CTA Placement |
|---|---------|---------------|
| 1 | Hero + Features + CTA | Hero sticky + bottom |
| 2 | Hero + Testimonials + CTA | Hero + post-testimonials |
| 3 | Product Demo + Features | Video center + right |
| 4 | Minimal Single Column | Center, large |
| 5 | Funnel (3-Step) | Each step + final |
| 6 | Comparison Table | Right + below |
| 7 | Lead Magnet + Form | Submit button |
| 8 | Pricing Page | Each card + sticky |
| 9 | Video-First Hero | Overlay + bottom |
| 10 | Scroll Storytelling | End of chapters + final |
| 11 | AI Personalization | Context-aware |
| 12 | Waitlist/Coming Soon | Email above fold + sticky |
| 13 | Comparison Focus | After table |
| 14 | Pricing-Focused | Each card + sticky |
| 15 | App Store Style | Download throughout |
| 16 | FAQ/Documentation | Search + contact |
| 17 | Immersive/Interactive | After interaction + skip |
| 18 | Event/Conference | Register sticky + speakers |
| 19 | Review/Ratings | After reviews + alongside |
| 20 | Community/Forum | Join + after members |
| 21 | Before-After | After reveal + bottom |
| 22 | Marketplace/Directory | Hero search + navbar |
| 23 | Newsletter/Content | Hero inline + sticky |
| 24 | Webinar Registration | Hero right + bottom |
| 25 | Enterprise Gateway | Contact Sales + Login |
| 26 | Portfolio Grid | Card hover + footer |
| 27 | Horizontal Scroll Journey | Floating sticky or end |
| 28 | Bento Grid Showcase | FAB or bottom |
| 29 | 3D Configurator | Inside + sticky |
| 30 | AI-Driven Dynamic | Input + 'Try it' |
| 31 | Feature-Rich Showcase | Hero + features + bottom |
| 32 | Hero-Centric | Hero dominant + sticky |
| 33 | Trust & Authority | Contact Sales / Quote |
| 34 | Real-Time/Operations | Nav + after metrics |

### Conversion Tips

7:1+ CTA contrast | Sticky nav CTA | Social proof before CTA (3-5 testimonials) | Forms ≤3 fields | Annual discount 20-30% | Progress indicators | Scarcity for waitlists

---

## 12. Data Visualization

| Data Type | Best Chart | When NOT to Use | A11y |
|-----------|-----------|-----------------|------|
| Trend Over Time | Line | < 4 pts, > 6 series | AA |
| Compare Categories | Bar | > 15 categories | AAA |
| Part-to-Whole | Pie/Donut | > 5, precise values, a11y-first | C |
| Correlation | Scatter/Bubble | Categorical, < 20 pts | B |
| Intensity | Heat Map | < 20 cells, exact values | B |
| Geographic | Choropleth | Different region sizes | B |
| Funnel | Funnel/Sankey | Non-sequential, < 3 stages | AA |
| Performance vs Target | Gauge | No target, multiple KPIs | AA |
| Forecast | Line + Confidence | No baseline, low confidence | AA |
| Anomaly | Line + Highlights | Predefined, real-time no pause | AA |

### Rules

Differentiate by line style not color alone | Value labels on bars | Pie max 6 slices + stacked bar fallback | Scatter opacity 0.6–0.8 | Heat map numeric legend + pattern overlay | Funnel conversion % as text | Gauge: numerical value + % always visible | < 1000 pts SVG, ≥ 1000 Canvas, > 10000 aggregate

---

## 13. React Performance

**Async (Critical):** `Promise.all` for independent ops | Start promises early, await late | `<Suspense>` for async components | Move `await` into branches

**Bundle (Critical):** Direct source imports not barrel | `next/dynamic` for heavy components | Load analytics after hydration | Dynamic import on feature enable | Preload on hover/focus

**Server:** `cache()` for dedup | LRU for cross-request | Pass only needed fields | Component composition for parallel fetch | `after()` for non-critical

**Client/Rerender:** `useSWR` for data | Read state on-demand in callbacks | `memo()` for expensive | Primitive deps | `setState(curr => ...)` | `useState(() => init())` | `startTransition` for non-urgent

---

## 14. Mobile App Guidelines

**Accessibility:** `accessibilityLabel` on icon buttons (Critical) | Visible label + a11y label on inputs (Critical) | `accessibilityRole` on interactive (High) | `accessibilityLiveRegion` for updates (Medium) | Hide decorative icons (Medium)

**Touch:** Min 44x44pt + `hitSlop` (Critical) | 8dp gap (Medium) | No gesture conflicts (High)

**Navigation:** `goBack()` + preserve state (Critical) | Max 5 tabs (Medium) | Modal close affordance (High) | Preserve screen state (Medium)

**Forms:** Validate onBlur (Medium) | `keyboardType` + `returnKeyType` (Medium) | `onSubmitEditing` → next field (Low) | Password toggle (Medium)

**Performance:** `FlatList` for > 50 items (High) | Proper image resize + cache (Medium) | Debounce scroll/search (Medium)

**Animation:** 150–300ms native easing (Medium) | Check `reduceMotionEnabled` (Critical) | Loops for loaders only (Medium)

**Typography:** Min 14–16pt + Dynamic Type (High) | `allowFontScaling` (High)

---

## 15. Anti-Patterns

### Forbidden

- Emojis as icons — use SVG (Heroicons/Lucide)
- Missing `cursor:pointer`
- Layout-shifting hovers
- Low contrast text (< 4.5:1)
- Instant state changes (no transition)
- Invisible focus states
- AI purple/pink gradients (overused tell)
- Lorem ipsum
- Color-only indicators
- Generic templates

### AI Tells

- Warm cream + serif + terracotta (default 1)
- Near-black + acid-green (default 2)
- Broadsheet + hairline + zero radius (default 3)
- Numbered markers when not sequential
- Big number + small label + gradient hero
- Same fonts on every project
- Scattered decorative animations
- Excessive motion

---

## 16. Pre-Delivery Checklist

- [ ] No emojis as icons (use SVG)
- [ ] Consistent icon set (Heroicons/Lucide)
- [ ] `cursor-pointer` on all clickables
- [ ] Hover transitions 150-300ms
- [ ] Text contrast 4.5:1 minimum
- [ ] Visible focus states
- [ ] `prefers-reduced-motion` respected
- [ ] Responsive: 375, 768, 1024, 1440
- [ ] No content behind fixed navbars
- [ ] No horizontal scroll on mobile
- [ ] Realistic content (no lorem ipsum)
- [ ] Touch targets min 44x44px
- [ ] Visible form labels (not placeholder-only)
- [ ] Error messages announced (`aria-live`)
- [ ] Semantic HTML (`<nav>`, `<main>`, `<article>`)
- [ ] Descriptive alt text
- [ ] Sequential heading hierarchy
- [ ] Loading states > 300ms
- [ ] Empty states with action
- [ ] Success/error feedback
- [ ] Confirmation for destructive actions

---

## 17. Design System Generation Workflow

1. **Identify product type** — search products
2. **Get reasoning rules** — find matching UI reasoning
3. **Extract style priority** — from reasoning
4. **Apply variance dial** — bias style selection
5. **Multi-domain search** — product, style, color, typography, landing
6. **Select best matches** — via priority keywords
7. **Apply motion dial** — pull matching motion snippet
8. **Apply density dial** — override spacing scale
9. **Build recommendation** — combine all domains
10. **Format output** — ASCII box or markdown
11. **Persist** — MASTER.md + page overrides

### Domain Auto-Detection

- **color**: palette, hex, token
- **chart**: graph, visualization, trend
- **landing**: cta, conversion, hero
- **product**: saas, ecommerce, fintech, etc.
- **style**: minimalism, glassmorphism, brutalism
- **ux**: usability, accessibility, wcag, touch
- **typography**: font pairing, heading, body
- **icons**: icon, lucide, heroicons
- **gsap**: scrolltrigger, stagger, parallax
- **react**: suspense, memo, rerender, bundle

### Supported Stacks

React, Next.js, Vue, Svelte, Astro, SwiftUI, React Native, Flutter, Nuxt.js, HTML+Tailwind, shadcn, Jetpack Compose, Three.js, Angular, Laravel, JavaFX, WPF, WinUI, Avalonia, Uno, UWP

---

## 18. Master + Override Pattern

### Architecture

```
design-system/[project-slug]/
├── MASTER.md           ← Global rules
└── pages/
    ├── dashboard.md    ← Page-specific overrides
    └── checkout.md     ← Only deviations from Master
```

### Logic

1. Check `pages/[page-name].md` — if exists, overrides Master
2. If not, strictly follow Master

### MASTER.md Contains

- Color palette table (role, hex, CSS variable)
- Typography (heading, body, mood, Google Fonts, CSS import)
- Spacing variables (overridden by density dial)
- Shadow depths (sm, md, lg, xl)
- Component specs (buttons, cards, inputs, modals)
- Style guidelines + page pattern
- Motion (if dial set)
- Anti-patterns + forbidden patterns
- Pre-delivery checklist

### Page Override Contains

- Layout overrides (max-width, grid, sections)
- Spacing overrides
- Typography overrides
- Color overrides
- Component overrides
- Page-specific components
- Recommendations from UX guidelines

### Page Type Detection

Dashboard, Checkout, Settings, Landing, Auth, Pricing, Blog, Product Detail, Search Results, Empty State — detected from page name + query context, with style/UX/landing searches for intelligent overrides.

---

## 19. Mobile Design System Deep Dives

Detailed design system specifications for mobile-first styles, including full token systems, component styling, iconography, mobile UX strategy, accessibility, and implementation guidance.

### Bauhaus (Mobile)

**Philosophy:** "Form follows function" with pure geometric beauty and primary color theory. Tactile constructivism — the screen is a canvas of physically stacked blocks. Poster-like, bold, unapologetically graphic.

**Tokens:**
- Background: `#F0F0F0` (off-white canvas) | Foreground: `#121212` (stark black)
- Primary Red: `#D02020` | Primary Blue: `#1040C0` | Primary Yellow: `#F0C020`
- Border: `#121212` (always black) | Muted: `#E0E0E0`
- Font: Outfit (400, 500, 700, 900)
- Radius: Binary — `0px` for blocks/inputs/cards, `9999px` for buttons/avatars
- Borders: `2px` standard, `3px` major containers
- Shadows: Hard offset — small `2px 2px 0 0 black`, buttons `4px 4px 0 0 black`, FAB `5px 5px 0 0 black`
- Touch feedback: `active:translate-x-[2px] active:translate-y-[2px] active:shadow-none`

**Typography:**
- Display: text-4xl → text-5xl (massive, 30-40% screen width)
- Headlines: font-black (900) uppercase, tracking-tighter
- Body: text-base (16px min), font-medium (500)
- Buttons: text-lg, font-bold (700) uppercase, tracking-wide

**Components:**
- Buttons: h-12/h-14 (48-56px), full-width, border-2, hard shadow, mechanical press
- Cards: border-2 border-black, solid color blocking, no gradients
- Inputs: border-2, sharp corners, clear focus state

**Anti-Patterns:** No gradients | No soft shadows | No rounded corners on blocks | No subtle grays

---

### Kinetic Brutalism (Mobile)

**Philosophy:** Relentless motion and aggressive scale. Numbers tower over labels. Scrolling is a performance. Hard edges, sharp borders, instant color flips.

**Tokens:**
- Background: `#09090B` (rich black) | Foreground: `#FAFAFA` (off-white)
- Muted: `#27272A` (dark gray) | Muted Foreground: `#A1A1AA` (zinc 400)
- Accent: `#DFE104` (acid yellow) | Accent Foreground: `#000000`
- Border: `#3F3F46` | Radius: `0px` | Border width: `2px`
- Font: Space Grotesk or Inter

**Signature Elements:**
- Infinite marquees (Reanimated, no easing, 5s loop, hard clip)
- Aggressive typography: ALWAYS uppercase, tight tracking
- Massive numerical elements (60–120pt) as background textures
- Tactile color inversions: press → flood with accent instantly
- Scroll-driven scaling: Interpolate scale/opacity
- Haptic Medium on every press

**Typography:**
- Display: 60–120pt uppercase, letterSpacing -1, lineHeight 0.9–1.1x
- Body: 18–20pt | Labels: 12pt uppercase letterSpacing +2
- Font scale: `windowWidth / 375 * size` for responsiveness

**Anti-Patterns:** No shadows | No gradients | No rounded corners | No soft transitions | No hover states (mobile-only)

---

### Cyberpunk (Mobile)

**Philosophy:** Hacker aesthetic with neon-on-dark, glitch effects, and terminal vibes. High-contrast neon green/magenta on deep black.

**Tokens:**
- Background: `#020203` | Elevated: `#050506` | Surface: `#0a0a0c`
- Foreground: `#EDEDEF` (high contrast) | Muted: `#8A8F98`
- Accent: `#5E6AD2` (electric indigo)
- Surface overlay: `rgba(255,255,255,0.05)`
- Font: JetBrains Mono for data, system sans for UI

**Iconography:** Lucide-react-native, `strokeWidth={1.5}`, icons wrapped in bordered square/circle with subtle glow shadow

**Mobile UX:**
- Native Driver for all opacity/transform animations
- Loading: "Deciphering..." text animation or rotating circuit SVG
- Gestures: PanResponder for swipe-to-action like sliding hardware panels

**Accessibility:** High contrast for outdoor legibility | hitSlop min 44x44dp | accessibilityLabel for icon buttons ("Decrypt" not "Enter")

---

### Sticker Bomb / Neo-Pop (Mobile)

**Philosophy:** Unapologetic visibility. Reject subtle elevation. Thick black borders, solid offset shadows, tactile mechanical tap. Sticker layering with slight rotations. Pop Art color scheme.

**Tokens:**
- Background: `#FFFDF5` (cream) | Ink: `#000000` (pure black for all text/borders/shadows)
- Accent: `#FF6B6B` (hot red) | Secondary: `#FFD93D` (vivid yellow) | Muted: `#C4B5FD` (soft violet)
- Font: Space Grotesk — weights 700 (bold) and 900 (black) only. No "Regular" or "Light"
- Borders: Default 4px, secondary 2px | Radius: 0 (sharp) default, 999 (pill) for badges only
- Shadows: Solid black offsets, blur radius always 0 — small 4x4, medium 8x8

**Components:**
- Buttons: height 56, borderWidth 4, Pressable with translateX/translateY 4 on press
- Cards: borderWidth 4, slight rotation (-1deg / 2deg) for "scattered sticker" look
- Inputs: height 64, borderWidth 4, focus → yellow background (no soft glow)
- Badges: borderWidth 2, absolute positioned, rotated

**Anti-Patterns:** No linear gradients | No shadow radius (blur always 0) | No subtle grays | No soft easing (Spring or Linear only) | No standard borderRadius (0 or 999 only)

---

### Sketch / Hand-Drawn (Mobile)

**Philosophy:** Authentic imperfection and human touch. Organic, playful irregularity evoking a sketchbook. Lowers "fear of interaction" by appearing unfinished and approachable.

**Tokens:**
- Background: `#FDFBF7` (warm paper) | Foreground: `#2D2D2D` (soft pencil black)
- Muted: `#E5E0D8` (old paper) | Accent: `#FF4D4D` (red correction marker)
- Border: `#2D2D2D` (pencil lead) | Secondary: `#2D5DA1` (blue ballpoint)
- Font: Kalam-Bold (headings, felt-tip marker), PatrickHand-Regular (body, human legible)

**Styling:**
- Wobbly borders: unique radius per corner (e.g., 15, 25, 20, 10)
- borderWidth: 2 minimum, 3 for primary actions
- Hard offset shadows via "Shadow View" behind component (offset 4x4)
- Paper texture: repeating pattern or subtle radial-gradient SVG overlay
- Cards: slight rotation, "tape" decoration (semi-transparent View at top)
- Buttons: "Post-it" yellow (#FFF9C4) for primary CTAs, translateX/Y 4 on press

**Best For:** Prototyping, creative brands, education/kids, gamified UI, narrative puzzles

---

## 20. Icon Catalog

### Icon Library: Phosphor Icons

Recommended icon library with categories, names, keywords, and usage patterns.

| Category | Icons | Usage |
|----------|-------|-------|
| **Navigation** | List, ArrowLeft, ArrowRight, CaretDown, CaretUp, House, X, ArrowSquareOut | Menu toggle, back/forward, dropdowns, home, close, external links |
| **Action** | Plus, Minus, Trash, PencilSimple, FloppyDisk, DownloadSimple, UploadSimple, Copy, Share, MagnifyingGlass, Funnel, Gear | Add/remove/delete/edit/save/download/upload/copy/share/search/filter/settings |
| **Status** | Check, CheckCircle, XCircle, Warning, WarningCircle, Info, CircleNotch, Clock | Success/error/warning/info/loading/pending states |
| **Communication** | Envelope, ChatCircle, Phone, PaperPlaneTilt, Bell | Email/chat/call/send/notifications |
| **User** | User, Users, UserPlus, SignIn, SignOut | Profile/team/invite/login/logout |
| **Media** | Image, Video, Play, Pause, SpeakerHigh, Microphone, Camera | Photos/video/playback/audio/recording |
| **Commerce** | ShoppingCart, ShoppingBag, CreditCard, CurrencyDollar | Cart/bag/payment/pricing |

### Icon Rules

- **Style:** Outline (regular weight) as default
- **Size:** 20px standard (`size={20}`)
- **Never use emojis as icons** — always SVG
- **Consistency:** Use one icon library throughout (Phosphor or Lucide or Heroicons)
- **Accessibility:** `aria-label` or `accessibilityLabel` on all icon-only buttons
- **Decorative icons:** Mark as `accessible={false}` / `importantForAccessibility="no"`

---

## 21. Google Fonts Catalog

A comprehensive index of 1700+ Google Fonts with metadata for font selection.

### Font Metadata Fields

Each font entry includes:
- **Family** — Font family name
- **Category** — Sans Serif, Serif, Display, Handwriting, Monospace
- **Stroke** — Sans Serif, Serif, Monospace
- **Classifications** — Display, Headline, Body, etc.
- **Keywords** — Descriptive tags (clean, modern, elegant, geometric, etc.)
- **Styles** — Available weights (e.g., 400 | 500 | 600 | 700 | 400i)
- **Variable Axes** — Variable font axes (wght, slnt, wdth, etc.)
- **Subsets** — latin, latin-ext, cyrillic, greek, vietnamese, etc.
- **Designers** — Type designer/foundry
- **Popularity Rank** — Global usage ranking
- **Trending Rank** — Current trending position
- **Is Noto** — Whether part of Google's Noto family
- **Google Fonts URL** — Direct spec link

### Font Categories

| Category | Characteristics | Best For |
|----------|----------------|----------|
| Sans Serif | Clean, modern, minimal, geometric, humanist, grotesque | UI body text, dashboards, SaaS, professional |
| Serif | Elegant, traditional, classic, refined, editorial, transitional | Luxury, editorial, publishing, literary |
| Display | Bold, decorative, headline, attention-grabbing, creative | Hero headlines, marketing, posters, large text |
| Handwriting | Personal, casual, friendly, warm, script, organic | Personal branding, creative, education, kids |
| Monospace | Code, developer, technical, precise, functional | Code blocks, data, developer tools, terminals |

### Font Selection Guidance

- **Variable fonts:** Prefer variable fonts for responsive weight control (single file, multiple weights)
- **Subsets:** Ensure font covers required scripts (latin, latin-ext, cyrillic, etc.)
- **Popularity:** Higher rank = more widely used/tested. Balance uniqueness with reliability.
- **Trending:** Check trending rank for emerging fonts that feel current.
- **Noto family:** Google's universal font family — excellent multilingual coverage.
- **Performance:** Load only needed weights and subsets. Use `font-display: swap`.

---

## 22. Stack-Specific Guidelines

The engine includes dedicated CSV files for 18+ framework/stack combinations, providing stack-specific component patterns, styling approaches, and best practices.

### Available Stack Guides

| Stack | Key Topics |
|-------|-----------|
| **React** | Component patterns, hooks, state management, rendering optimization |
| **Next.js** | App Router, RSC, Suspense boundaries, streaming, metadata |
| **React Native** | Platform components, gestures, animations (Reanimated), platform-specific styling |
| **Flutter** | Widget composition, Material/Cupertino, animations, theming |
| **SwiftUI** | Declarative syntax, modifiers, navigation, animations |
| **Jetpack Compose** | Composable functions, modifiers, state hoisting, Material 3 |
| **Svelte** | Reactive statements, stores, transitions, actions |
| **Astro** | Islands architecture, content collections, partial hydration |
| **Angular** | Components, directives, pipes, RxJS, dependency injection |
| **Nuxt.js** | File-based routing, auto-imports, server routes, Nitro |
| **Nuxt UI** | Component library, theming, dark mode, form handling |
| **HTML + Tailwind** | Utility classes, responsive design, dark mode, JIT |
| **shadcn/ui** | Radix primitives, CSS variables, variants, compound components |
| **Three.js** | Scene setup, geometries, materials, lighting, animation loop |
| **Laravel** | Blade templates, Livewire, Alpine.js, Tailwind integration |
| **JavaFX** | FXML, CSS styling, properties, bindings, layouts |
| **Avalonia** | XAML, MVVM, DataGrid, styling, cross-platform .NET |
| **Uno** | XAML, C# markup, platform extensions, code-behind |

### How Stack Guides Work

When a stack is detected from the query or explicitly specified, the engine:
1. Loads the stack-specific CSV
2. Searches for relevant patterns matching the query
3. Returns stack-specific code examples, component patterns, and best practices
4. Integrates these with the general design system recommendation

### Stack Detection

Stack is detected from query keywords:
- "react component" → react.csv
- "next.js app router" → nextjs.csv
- "react native animation" → react-native.csv
- "flutter widget" → flutter.csv
- "swiftui view" → swiftui.csv
- "compose modifier" → jetpack-compose.csv
- "svelte store" → svelte.csv
- "astro island" → astro.csv
- "angular component" → angular.csv
- "nuxt page" → nuxtjs.csv
- "tailwind class" → html-tailwind.csv
- "shadcn dialog" → shadcn.csv
- "three.js scene" → threejs.csv
- "laravel blade" → laravel.csv
- "javafx fxml" → javafx.csv
- "avalonia xaml" → avalonia.csv
- "uno platform" → uno.csv
