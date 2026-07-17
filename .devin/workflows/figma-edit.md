---
description: Edit Figma UIs using figwright MCP tools — read, modify, validate, and sync Figma frames to match the codebase design system
---

# Figma Edit Workflow

Edit Figma frames using figwright MCP tools, guided by the codebase as source of truth.
All design values (colors, typography, spacing, radii, elevation) come from the Kotlin source — never eyeball.

Reference: `FIGMA_BOARD_SPEC.md` for the full design system spec, page layout, and naming conventions.

---

## Prerequisites

1. **Figma plugin connected** — call `mcp1_ping` to verify. If no plugin connected, ask the user to open the Figma file and start the figwright plugin.
2. **Session file** — check `.figwright/screenshot-session.json` for the current portal, device info, and existing Figma node IDs.
3. **Design system tokens** — read `FIGMA_BOARD_SPEC.md` for exact hex values, font specs, spacing, radii, elevation. Source files:
   - Colors: `composeApp/.../ui/v2/theme/VColors.kt`
   - Typography: `composeApp/.../ui/v2/theme/VType.kt`
   - Spacing/Radii: `composeApp/.../ui/v2/theme/VDimens.kt`
   - Components: `composeApp/.../ui/v2/components/` (VButton, VCard, VInput, etc.)

---

## Step 1 — Identify the Target

Determine WHAT to edit. The user may provide:
- A Figma URL (paste it — figwright tools accept URLs as nodeId)
- A frame name (e.g. "Admin — Home Dashboard")
- A screen description ("the parent home screen loading state")
- A screenshot for visual reference

If the user gives a Figma URL, extract the node ID from it. Otherwise:
1. Call `mcp1_get_metadata` to see the current file + page.
2. Call `mcp1_get_pages` to list all pages.
3. Navigate to the correct page with `mcp1_navigate_to_page`:
   - 05 → Authentication, 06 → Parent, 07 → Teacher, 08 → Admin, 09 → Discovery, etc.
4. Search for the frame: `mcp1_search_nodes` by name, or `mcp1_get_selection` if the user has it selected in Figma.

---

## Step 2 — Read the Current Figma State

Before editing, understand what exists:

1. **Get design context** — `mcp1_get_design_context` with the node ID (or selection). Use `detail: "full"` for complete styling data. This returns the node tree with fills, typography, auto-layout, text content, and resolved design tokens.
2. **Get a screenshot** — `mcp1_get_screenshot` with the node ID to see the current visual state. Compare with the user's request or reference screenshot.
3. **Get node detail** (if needed) — `mcp1_get_node` for a single node's full recursive subtree at max fidelity. Use for deep inspection of a specific component or element.

Record what needs to change:
- Colors that don't match `VColors` hex values
- Typography that doesn't match `VType` specs
- Spacing/padding that doesn't match `VDimens` (4/8/16/24/32dp)
- Corner radii that don't match (Cards=16, Inputs=12, Buttons=10, Sheets=32, Pills=999)
- Missing or incorrect shadows (navy-tinted `#26234D`, 3 tiers)
- Text content that doesn't match the screen's actual copy
- Layout structure that doesn't match the Compose composable

---

## Step 3 — Find the Corresponding Code

The codebase is the source of truth. Read the actual Composable to extract exact values:

1. Search for the screen file:
   - Parent: `composeApp/.../ui/v2/screens/parent/`
   - Teacher: `composeApp/.../ui/v2/screens/teacher/`
   - Admin: `composeApp/.../ui/v2/screens/school/`
   - Auth: `composeApp/.../ui/v2/screens/auth/`
   - Discovery: `composeApp/.../ui/v2/screens/discovery/`
   - Shared components: `composeApp/.../ui/v2/components/`
2. Read the `.kt` file(s) — extract exact colors, dimensions, padding, fonts, icons, layout structure, text strings.
3. If the edit involves a design system component (VButton, VCard, VInput, etc.), read the component file too for its exact specs.

---

## Step 4 — Make the Edits in Figma

Use figwright MCP tools to modify the Figma frame. Common operations:

### Colors / Fills
- `mcp1_set_fills` — set SOLID or gradient fills with exact RGB (0–1) values from `VColors`
- `mcp1_bind_variable_to_paint` — bind a Figma COLOR variable to a fill/stroke (preferred if variables exist)
- `mcp1_apply_style_to_node` — apply a shared paint style (field: "fill" / "stroke")

### Typography
- `mcp1_set_text` — replace text content of a TEXT node
- `mcp1_set_text_properties` — set font family, size, weight, line-height, letter-spacing, text case, truncation
- `mcp1_set_text_range` — style character ranges within a single TEXT node (inline rich text)
- `mcp1_apply_style_to_node` — apply a shared text style (field: "text")
- Font family: "Plus Jakarta Sans" (primary), "DM Mono" (data), "Inter" (legacy splash/landing only)

### Layout / Spacing
- `mcp1_set_auto_layout` — configure auto-layout (HORIZONTAL/VERTICAL/GRID) with exact padding, itemSpacing, alignment
- `mcp1_set_layout_props` — set sizing (HUG/FILL/FIXED), min/max bounds, layout positioning
- `mcp1_resize_nodes` — resize frames/elements to exact dimensions
- `mcp1_set_position` — position absolutely-placed elements
- `mcp1_set_constraints` — set resize constraints (MIN/CENTER/MAX/STRETCH/SCALE)

### Corner Radii
- `mcp1_set_corner_radius` — uniform or per-corner (topLeft, topRight, bottomRight, bottomLeft)

### Effects / Shadows
- `mcp1_set_effects` — set DROP_SHADOW / INNER_SHADOW with navy-tinted color `#26234D` (RGBA: r=0.149, g=0.137, b=0.302, a=varies)
  - Card: dy=2, spread=4, alpha=0.06
  - Raised: dy=8, spread=24, alpha=0.09
  - Modal: dy=16, spread=40, alpha=0.15
  - Dark theme: suppress shadows (set visible=false)

### Strokes / Borders
- `mcp1_set_strokes` — SOLID or gradient strokes with weight, alignment (INSIDE/OUTSIDE/CENTER), dash patterns
- Hairline color: `rgba(38,35,77,0.06)` → r=0.149, g=0.137, b=0.302, a=0.06

### Visibility / Opacity
- `mcp1_set_visible` — show/hide nodes
- `mcp1_set_opacity` — set layer opacity (0–1)

### Structure
- `mcp1_create_frame` — create new containers (use for adding new sections)
- `mcp1_create_text` — create new text nodes
- `mcp1_create_rectangle` — create shapes/dividers
- `mcp1_create_ellipse` — create circles (avatars, status dots)
- `mcp1_import_svg` — import SVG icons as editable vectors
- `mcp1_import_image` — import raster images (PNG/JPG)
- `mcp1_clone_node` — duplicate a node with its full subtree
- `mcp1_move_nodes` — translate by (dx, dy)
- `mcp1_reparent_nodes` — move nodes into a different parent
- `mcp1_reorder_nodes` — reorder within parent (z-order)
- `mcp1_delete_nodes` — permanently remove nodes
- `mcp1_group_nodes` / `mcp1_ungroup_nodes` — group/ungroup

### Components
- `mcp1_create_component` — convert a node into a reusable component
- `mcp1_create_instance` — instantiate a component
- `mcp1_swap_component` — swap an instance's main component
- `mcp1_detach_instance` — detach an instance into a plain frame
- `mcp1_combine_as_variants` — combine components into a variant set
- `mcp1_set_instance_properties` — set variant/boolean/text/instance-swap properties

### Variables / Design Tokens
- `mcp1_create_variable_collection` / `mcp1_create_variable` / `mcp1_set_variable_value` — create and set design token variables
- `mcp1_bind_variable_to_node` — bind a variable to a scalar field (width, height, spacing, radius)
- `mcp1_bind_variable_to_paint` — bind a COLOR variable to a fill/stroke
- `mcp1_set_variable_code_syntax` — declare the code-side token name (e.g. `--color-primary`)

### Batch Operations
- `mcp1_batch` — apply multiple invertible write ops atomically (all-or-nothing with rollback). Use for coordinated multi-node edits.
- `mcp1_batch_rename_nodes` — rename many nodes at once
- `mcp1_multi_edit` (via edit tool) — for code files

---

## Step 5 — Validate the Edit

1. **Export the edited frame** — `mcp1_get_screenshot` or `mcp1_save_screenshots` to see the result.
2. **Read the exported image** — visually compare against:
   - The user's request / reference screenshot
   - The actual app screenshot (if available in `/tmp/vp_screenshots/`)
   - The codebase values from Step 3
3. **Check for common mismatches**:
   - Colors match `VColors` hex exactly
   - Text font/size/weight matches `VType`
   - Spacing matches `VDimens` (4/8/16/24/32dp)
   - Corner radii match (Cards=16, Inputs=12, Buttons=10, Sheets=32, Pills=999)
   - Shadow is navy-tinted `#26234D` with correct tier
   - Divider is 0.5dp with `hairline` color
   - Frame width = full device width (412dp for LM-F100); 440dp is content max-width only
4. **If mismatched** → fix and re-validate. Max 3 fix iterations.
5. **If still mismatched after 3 iterations** → flag for manual review with specific mismatch details.

---

## Step 6 — Update Session Tracking

Update `.figwright/screenshot-session.json` with the edit results:
- Set `verified: true` for the screen/state if validation passed
- Update `lastUpdated` timestamp
- Add any new node IDs created during the edit

---

## Edit Modes

### From Screenshot (App → Figma)
User provides or captures a screenshot from the running app. Build/edit the Figma frame to match it exactly, using the codebase for exact values. Follow `FIGMA_BOARD_SPEC.md` Steps 1–5.

### From Code (Code → Figma)
User describes a code change (e.g. "the button color changed to teal-deep"). Read the updated `.kt` file, find the corresponding Figma node, and update it to match.

### From Design (Figma → Code)
User edits in Figma and wants the code updated. Use `mcp1_get_design_context` to read the Figma state, then update the corresponding `.kt` file(s). This is the reverse flow — ensure code matches Figma, not the other way around.

### From Spec (Spec → Figma)
User provides a spec or description (e.g. "add a loading skeleton state to the parent home screen"). Read the codebase for the screen structure, build the new state frame in Figma following the design system.

---

## Tips

- **Always use exact values** — never eyeball colors, spacing, or typography. Read the source files.
- **Prefer variables/styles** — if Figma variables or shared styles exist for a token, bind to them instead of hardcoding values. Use `mcp1_get_variable_defs` and `mcp1_get_styles` to check.
- **Auto Layout is mandatory** — every container should use auto-layout matching the Compose Column/Row structure. Never free-position elements unless the Compose code uses absolute positioning.
- **Name frames correctly** — use `{ScreenName}_{State}_{Theme}` naming (e.g. `ParentHomeScreenV2_Success_Light`).
- **Place on correct page** — Parent→06, Teacher→07, Admin→08, Auth→05, Discovery→09.
- **Use `mcp1_batch` for coordinated edits** — when changing multiple related properties atomically (e.g. updating a card's fill + radius + shadow together).
- **Check component instances** — if a node is an instance, use `mcp1_set_instance_properties` to change variant props rather than editing the instance's children directly.
- **Dark theme** — shadows suppressed, backgrounds dark (`#050505`), cards `#0E0E10`, ink colors inverted.
