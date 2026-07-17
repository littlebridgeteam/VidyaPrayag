---
description: Create brand-new Figma screens inspired by existing designs — never edit or replace, always create fresh frames using figwright MCP
---

# Figma Intuitive Design Workflow

Create **brand-new** Figma screens using figwright MCP tools. This workflow never edits or replaces existing frames — it creates entirely new screens alongside them, inspired by the user's design instructions.

Use this when the user wants to redesign a screen from scratch with a new design language, or create a new screen that doesn't exist yet.

---

## Prerequisites

1. **Figma plugin connected** — call `mcp1_ping` to verify. If no plugin, ask the user to open Figma and start the figwright plugin.
2. **Identify the target page** — call `mcp1_get_pages` to list pages. Navigate with `mcp1_navigate_to_page`:
   - 05 → Authentication, 06 → Parent, 07 → Teacher, 08 → Admin, 09 → Discovery, etc.
3. **Find existing screens for reference** — use `mcp1_search_nodes` to find related frames. These are **reference only** — never edit them.

---

## Step 1 — Understand the Design Brief

The user provides a design specification. Extract:
- **Screen name** and purpose
- **Design language** (Material 3 Expressive, etc.)
- **Color palette** (primary, surface, accents)
- **Typography scale** (headline, title, body, label)
- **Layout structure** (sections in scroll order)
- **Component patterns** (cards, carousels, segmented controls, etc.)
- **Interaction patterns** (animations, progressive disclosure, etc.)
- **Reference inspirations** (Google Wallet, Linear, CRED, etc.)

---

## Step 2 — Find Placement Coordinates

1. Search for existing screens on the target page: `mcp1_search_nodes` by name pattern.
2. Read the rightmost frame's x position + width using `mcp1_get_nodes_info`.
3. Place the new screen at `x = (rightmost_x + rightmost_width + 100)` to avoid overlap.
4. Standard Android frame size: **411 × variable height** (height depends on content).

---

## Step 3 — Build the Screen Section by Section

Create the new frame and build each section sequentially. Use `mcp1_create_frame` for the root, then build sections inside it.

### Build Order (top to bottom):
1. **Root frame** — `mcp1_create_frame` with the screen name, set fills to surface color, auto-layout VERTICAL, padding 0, itemSpacing 0.
2. **Hero section** — gradient background frame, large title, subtitle, floating illustration, stats badge.
3. **Statistics** — radial chart or animated stat cards.
4. **Primary CTA** — full-width gradient card with icon + title + subtitle + arrow.
5. **Navigation** — segmented control with animated indicator.
6. **Content sections** — recent templates carousel, card type selection, fields grouped sections, theme picker, background style thumbnails.
7. **Live Preview** — dominant section with real card preview.
8. **AI Suggestions** — recommendation cards.
9. **Floating CTA** — bottom action button.
10. **Empty state** (if applicable) — illustration + title + CTA.

### For each section:
- `mcp1_create_frame` — container with auto-layout
- `mcp1_set_auto_layout` — configure padding, spacing, alignment
- `mcp1_set_fills` — set background colors or gradients
- `mcp1_set_corner_radius` — rounded corners
- `mcp1_set_effects` — shadows for elevation
- `mcp1_create_text` — text nodes with exact typography
- `mcp1_set_text_properties` — font family, size, weight, line height
- `mcp1_create_ellipse` — circles for charts, avatars, status dots
- `mcp1_create_rectangle` — shapes, dividers, preview thumbnails
- `mcp1_set_layout_props` — sizing (HUG/FILL/FIXED), min/max bounds

### Color Values (RGB 0–1 for figwright):
Convert hex to RGB: `r = hex_r/255`, `g = hex_g/255`, `b = hex_b/255`

### Typography:
- Font family: "Plus Jakarta Sans" (primary), "DM Mono" (data)
- Material 3 type scale: Headline Large (32/Bold), Headline Medium (28/Bold), Title Large (22/ExtraBold), Title Medium (16/SemiBold), Body Medium (14/Regular), Label (12/Bold)

---

## Step 4 — Apply Premium Polish

After building all sections:
1. **Shadows** — `mcp1_set_effects` with DROP_SHADOW for elevated cards (dy=8, blur=24, alpha=0.08-0.12)
2. **Gradients** — `mcp1_set_fills` with GRADIENT_LINEAR for hero, CTA, and preview frames
3. **Corner radii** — large rounded corners (20-28dp for cards, 16dp for inner elements, 999 for pills)
4. **Spacing** — consistent 8dp grid (8, 16, 24, 32dp)
5. **No hard borders** — use elevation and subtle fills instead of strokes where possible

---

## Step 5 — Validate

1. `mcp1_get_screenshot` — export the new frame
2. Visually compare against the design brief
3. Check: all sections present, colors match spec, typography hierarchy correct, spacing consistent
4. Fix mismatches (max 3 iterations)

---

## Step 6 — Name and Document

- Name the frame: `{ScreenName}_{Theme}` (e.g. `IDCardStudio_Light`)
- The new frame is a **standalone design** — it does not replace or modify any existing frame
- Inform the user of the new frame's node ID and position

---

## Tips

- **Never edit existing frames** — this workflow only creates new content
- **Build incrementally** — create the root frame first, then add sections one at a time
- **Use auto-layout everywhere** — every container should use VERTICAL or HORIZONTAL auto-layout
- **Batch when possible** — use `mcp1_batch` for coordinated multi-element creation
- **Progressive disclosure** — design for sections that reveal progressively, not everything at once
- **Large touch targets** — minimum 48dp height for interactive elements
- **Material 3 Expressive** — playful shapes, larger components, expressive typography, rich gradients
