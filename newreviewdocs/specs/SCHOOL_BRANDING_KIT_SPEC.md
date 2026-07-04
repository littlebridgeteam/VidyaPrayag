# School Branding Kit — Technical Specification

> **Document status:** Partially implemented — see §0 for status
> **Last updated:** 2026-07-03
> **Prerequisites:** None
> **Source:** `DIFFERENTIATING_FEATURES.md` §7.2
> **Template:** `_SPEC_TEMPLATE.md` v1 (25 mandatory + 6 optional sections)

---

## 0. Implementation Status Audit

> **Audited:** 2026-07-03 on branch `feature/school-branding-kit`
> **Method:** Full codebase search across `server/`, `shared/`, and `composeApp/` modules.

### 0.1 What Exists (Implemented)

| # | Component | File | Status | Notes |
|---|---|---|---|---|
| E-1 | **DB Table** | `server/.../db/Tables.kt:2986-3005` | ✅ Complete | `SchoolBrandingTable` — all columns match spec §6.10 |
| E-2 | **DB Migration** | `docs/db/migration_101_school_branding.sql` | ✅ Complete | DDL + subdomain index, matches spec Appendix E |
| E-3 | **DatabaseFactory registration** | `server/.../db/DatabaseFactory.kt:275` | ✅ Complete | `SchoolBrandingTable` registered in `allTables` |
| E-4 | **Server BrandingService** | `server/.../feature/branding/BrandingService.kt` (348 lines) | ✅ Complete | CRUD, subdomain management, reset, default fallback, hex/subdomain validation. DTOs defined inline. |
| E-5 | **Server BrandingRouting** | `server/.../feature/branding/BrandingRouting.kt` (137 lines) | ✅ Complete | Admin endpoints (GET/PATCH/POST reset/subdomain) + public endpoints (GET branding/{schoolId}, GET subdomain/{subdomain}). **Missing: upload endpoint.** |
| E-6 | **Server Application.kt wiring** | `server/.../Application.kt:63,495` | ✅ Complete | `brandingRouting()` imported and mounted |
| E-7 | **Shared DTOs** | `shared/.../feature/branding/domain/model/BrandingModels.kt` (62 lines) | ✅ Complete | `SchoolBranding`, `UpdateBrandingRequest`, `SubdomainRequest`, `SubdomainResponse`, `SubdomainCheckResponse`, `RemoveSubdomainResponse`, `SubdomainResolution` |
| E-8 | **Shared Repository Interface** | `shared/.../feature/branding/domain/repository/BrandingRepository.kt` (17 lines) | ✅ Complete | All 8 methods matching API endpoints |
| E-9 | **Shared Repository Impl** | `shared/.../feature/branding/data/repository/BrandingRepositoryImpl.kt` (22 lines) | ✅ Complete | Delegates to `BrandingApi` |
| E-10 | **Shared BrandingApi (Ktor client)** | `shared/.../feature/branding/data/remote/BrandingApi.kt` (74 lines) | ✅ Complete | All admin + public endpoints. **Missing: upload method.** |
| E-11 | **Shared BrandingViewModel** | `shared/.../feature/branding/presentation/BrandingViewModel.kt` (164 lines) | ✅ Complete | `loadBranding`, `updateBranding`, `resetBranding`, `checkSubdomain`, `updateSubdomain`, `removeSubdomain`. State: `BrandingState` with isLoading, branding, subdomainAvailable, error, infoMessage. |
| E-12 | **Shared BrandingThemeManager** | `shared/.../feature/branding/presentation/BrandingThemeManager.kt` (57 lines) | ✅ Complete | App-lifecycle singleton. Fetches branding after login, holds `StateFlow<SchoolBranding?>`. Silent fallback to default on error. `clear()` on logout. |
| E-13 | **Compose BrandingColorMapper** | `composeApp/.../ui/v2/theme/BrandingColorMapper.kt` (88 lines) | ✅ Complete | Maps hex → `VColors` token overrides (`accent`, `accentSoft`, `accentDeep`, `accentTint`, `teal`, `tealDeep`). Derives tint for light/dark. Returns base unchanged if not customized. |
| E-14 | **Compose NavGraphV2 integration** | `composeApp/.../ui/v2/navigation/NavGraphV2.kt:76-91` | ✅ Complete | Injects `BrandingThemeManager`, applies `BrandingColorMapper` to theme def, fetches branding on auth, clears on logout. 300ms crossfade on theme/branding switch. |
| E-15 | **Compose BrandingSettingsScreen** | `composeApp/.../ui/v2/screens/school/BrandingSettingsScreen.kt` (566 lines) | ✅ Complete | Color pickers (preset swatches + hex input), live preview card (mock header + buttons + swatches), subdomain management (input + check + assign + remove), reset to defaults with confirm dialog. Uses VTheme, VCard, VButton, VInput, VBadge, VStateHost. |
| E-16 | **Compose SchoolSettingsScreenV2** | `composeApp/.../ui/v2/screens/school/SchoolSettingsScreenV2.kt:194` | ✅ Complete | "Branding Kit" settings row with icon + description |
| E-17 | **Compose SchoolPortalV2 routing** | `composeApp/.../ui/v2/screens/school/SchoolPortalV2.kt:71,423-426,581` | ✅ Complete | `SchoolOverlay.BrandingKit` → shows `BrandingSettingsScreen` |
| E-18 | **Koin DI registration** | `shared/.../di/Koin.kt:436-449,653-654` | ✅ Complete | `BrandingApi` (single), `BrandingRepository` (single), `BrandingThemeManager` (single), `BrandingViewModel` (factory) |
| E-19 | **Onboarding integration** | `shared/.../feature/admin/presentation/BrandingInfoOBViewModel.kt` | ✅ Partial | Onboarding-time branding info collection exists. Uses `MediaApi` for logo upload during onboarding. Some fields remain local-only (not yet persisted to `school_branding` server-side). |

### 0.2 What's Missing (Not Implemented)

| # | Gap | Spec Section | Impact | Priority |
|---|---|---|---|---|
| M-1 | **Asset Upload API (FR-001)** | §9.1, §8.3 | Admin cannot upload logo/favicon/app-icon/splash via branding settings. No `POST /api/v1/school/branding/upload` endpoint, no `uploadAsset()` in `BrandingService`, no upload method in `BrandingApi`/`BrandingRepository`. | **High** |
| M-2 | **Branded Login Screen (FR-004)** | §10.1, §10.3 | Login/auth screens (`CommonLandingScreenV3.kt`, `SplashScreenV2.kt`) don't show school logo/name. No branding fetch before login. | **High** |
| M-3 | **Branded SplashScreen (FR section 10.1)** | §10.1, §10.8 | `SplashScreenV2.kt` uses hardcoded `VBrandLogo`. No school splash image support. | **Medium** |
| M-4 | **Branded Communications (FR-005)** | §15, §10.10 R10 | No email template branding, no report card header branding, no ID card branding. Server has no integration between `school_branding` and email/report/idcard templates. | **Medium** |
| M-5 | **Use Cases (Clean Architecture)** | §11.4 | No use case classes (`GetBrandingUseCase`, `UpdateBrandingUseCase`, etc.). ViewModel calls repository directly. Deviates from project convention. | **Low** |
| M-6 | **DataStore Caching** | §11.8, §8.9 NFR-6 | No branding caching in DataStore. `BrandingThemeManager` holds in-memory only; re-fetches every app launch. Spec requires session persistence. | **Medium** |
| M-7 | **Subdomain Routing (Web)** | §10.8, FR-006 | No web app subdomain middleware. No code resolves school from subdomain on web platform. | **Low** |
| M-8 | **Server-side Caching** | §8.9 | No server-side branding cache (10-min TTL). Every request hits DB directly. | **Low** |
| M-9 | **Rate Limiting** | §8.11 | No rate limiting on branding update (10/school/hr), asset upload (20/school/hr), or subdomain check (30/school/hr). | **Low** |
| M-10 | **Audit Logging** | §16 | No audit logging for branding updates, subdomain assignments/removals, or asset uploads. | **Low** |
| M-11 | **Env Config** | Appendix D | No `BRANDING_*` env vars in `.env.example` or `EnvConfig`. Defaults are hardcoded in `BrandingService`. | **Low** |
| M-12 | **Health Checks** | Appendix F | No `/health/branding` endpoint. | **Low** |
| M-13 | **Analytics** | §20 | No branding adoption metrics or subdomain registry API for super admin. | **Low** |
| M-14 | **Tests** | §21 | No unit tests, integration tests, or E2E tests for branding. | **Medium** |
| M-15 | **Dynamic App Icon (Android)** | §10.7 | No Android adaptive icon support with school logo. | **Low** |
| M-16 | **Login Background URL** | §6.2, §9.3 | Field exists in DB/model/service but no UI to set it and no login screen uses it. | **Low** |
| M-17 | **Logo Dark URL** | §6.2, §9.3 | Field exists in DB/model/service but no UI to set it and no dark mode logo fallback logic. | **Low** |
| M-18 | **Branding Preview on Login Screen** | §10.6 R2 | The `BrandingSettingsScreen` has a live preview card, but the actual login screen doesn't use school branding. | **Medium** |

### 0.3 Summary

- **Backend (server):** ~90% complete. CRUD, subdomain, reset, public endpoints all working. Missing: asset upload endpoint, server-side caching, rate limiting, audit logging.
- **Shared (KMP):** ~85% complete. Models, repository, API client, ViewModel, ThemeManager all working. Missing: use cases, DataStore caching, upload API method.
- **Client (Compose):** ~70% complete. BrandingSettingsScreen with color picker + preview + subdomain management is fully built. Dynamic theming via `BrandingColorMapper` + `NavGraphV2` integration is working. Missing: branded login/splash screens, asset upload UI, web subdomain routing.
- **Cross-cutting:** Missing tests, env config, health checks, analytics, audit logging.

### 0.4 Production-Grade Gaps (Research-Based)

> **Researched:** 2026-07-03 — benchmarked against Studeia, Classe365, TalentLMS, Cube, Authaz, Developex SaaS guide, and other white-label school/SaaS platforms.

The current spec covers basic branding (logo, 3 colors, subdomain). For a production-grade school management app, the following must-have capabilities are **not in the spec at all**. These are organized by category.

#### A. Communication Branding (Entirely Missing)

| # | Feature | Description | Why It Matters |
|---|---|---|---|
| PG-A1 | **Custom Email Sender** | Per-school SMTP/SendGrid/Resend config. Emails sent from `noreply@schooldomain.com` instead of `noreply@vidyaprayag.com`. `TenantEmailConfig` table. | Parents trust emails from their school's domain. Deliverability improves (SPF/DKIM match). |
| PG-A2 | **Branded Email Templates** | Email header/footer with school logo + colors. 40+ template types (fee reminder, attendance alert, exam result, holiday notice, etc.) all carry school branding. | Every communication reinforces school identity. Currently all emails use Vidya Prayag branding. |
| PG-A3 | **Custom Reply-To Address** | School admin sets reply-to email. Parent replies go to school, not platform. | Operational necessity — parents should reach their school, not the platform team. |
| PG-A4 | **SMS Sender ID** | Per-school alphanumeric sender ID (e.g., `DPSRKP` instead of `VIDYAPR`). DLT-compliant registration for India. | SMS is primary communication channel in Indian schools. Sender ID = brand recognition. |
| PG-A5 | **WhatsApp Template Branding** | WhatsApp Business API templates with school name in header. School-specific template approval flow. | WhatsApp is the #1 messaging channel for Indian parents. Templates must carry school identity. |
| PG-A6 | **Push Notification Branding** | Notification title prefixed with school short name. Optional custom notification icon per school. | Parents with multiple school apps can distinguish notifications. |
| PG-A7 | **Email Unsubscribe Management** | Per-school unsubscribe links, digest preferences, frequency caps. CAN-SPAM/GDPR compliance. | Legal compliance. Currently no unsubscribe infrastructure. |

#### B. Document Branding (Entirely Missing)

| # | Feature | Description | Why It Matters |
|---|---|---|---|
| PG-B1 | **Report Card Headers** | Report card PDF header with school logo, name, address, colors. Footer with school contact + website. | Report cards are official school documents. Must carry school identity, not platform branding. |
| PG-B2 | **Fee Receipt/Invoice Branding** | Fee receipts, invoices, payment confirmations with school logo + header + colors. | Financial documents must look official and school-branded for parent trust. |
| PG-B3 | **Transfer Certificate Branding** | TC header with school logo, name, affiliation body. Official seal placement. | TC is a legal document. Must carry school's official letterhead. |
| PG-B4 | **Exam Hall Ticket Branding** | Hall tickets with school header, exam details, student photo. | Exam documents must be school-branded for authenticity. |
| PG-B5 | **ID Card Branding** | ID card templates use school logo, colors, design. Front/back config per school. | ID cards are physical brand assets students carry daily. |
| PG-B6 | **Certificate Branding** | Achievement/character certificates with school letterhead, logo, seal. | Certificates represent school authority. Must be branded. |
| PG-B7 | **Newsletter/Notice Branding** | Digital newsletters, notice board PDFs with school header + colors. | Regular communications should reinforce school identity. |
| PG-B8 | **Admission Form Branding** | Online + printable admission forms with school branding. | First impression for prospective parents. |

#### C. Typography & Font Customization (Marked as Non-Goal — Should Reconsider)

| # | Feature | Description | Why It Matters |
|---|---|---|---|
| PG-C1 | **Custom Font Upload** | Admin uploads WOFF2/WOFF/TTF font files. Stored in Supabase Storage. Applied to headings + body text. | Schools have brand fonts (e.g., DPS uses specific typeface). System fonts feel generic. |
| PG-C2 | **Google Fonts Integration** | Admin selects from Google Fonts library. No upload needed. Font URL stored in branding config. | Zero-friction font customization. Google Fonts covers 99% of school brand fonts. |
| PG-C3 | **Font Weight Control** | Separate heading + body font weight selection (100-900 scale). | Fine-grained typography control for brand consistency. |
| PG-C4 | **Font Size Scale** | Adjustable base font size (small/medium/large) or custom rem scale. | Schools may prefer larger text for parent readability. |

#### D. Advanced Theming (Partially Missing)

| # | Feature | Description | Why It Matters |
|---|---|---|---|
| PG-D1 | **Dark Mode Color Palette** | Separate dark mode colors per school (not auto-derived). Admin sets dark-mode primary/secondary/accent. | Auto-derived dark colors often look wrong. Schools need deliberate dark palette. |
| PG-D2 | **Auto-Generated Palette** | Admin picks ONE primary color. System generates full palette (tints, shades, accents, semantic colors) using color theory. | Reduces admin effort. Guarantees harmonious palette. Cube.do does this excellently. |
| PG-D3 | **Accessibility/Contrast Checking** | WCAG AA contrast ratio validation (4.5:1 text, 3:1 large text). Warn admin if colors fail. Suggest accessible alternatives. | Legal compliance (ADA/Section 508). Prevents unreadable color combinations. |
| PG-D4 | **Color Blindness Simulation** | Preview branding in deuteranopia, protanopia, tritanopia modes. | 8% of men have color vision deficiency. Ensure branding is distinguishable. |
| PG-D5 | **Border Radius / Shape Control** | Configurable corner radius (none/small/medium/large/pill) for buttons + cards. | Some school brands are sharp/angular, others are soft/rounded. |
| PG-D6 | **Preset Theme Templates** | 5-10 curated branding presets (Minimal, Bold, Soft, Dark-first, Brand-forward). Admin picks a preset then customizes. | Reduces setup time. Gives non-designers a good starting point. |
| PG-D7 | **Seasonal/Event Branding** | Schedule branding changes for festivals, events, holidays (e.g., Diwali theme for 2 weeks). Auto-revert to default. | Indian schools celebrate many festivals. Seasonal branding builds engagement. |
| PG-D8 | **Background Image / Pattern** | Optional login screen background image or pattern overlay. Admin uploads or picks from presets. | Login screen is the first impression. Background image adds personality. |

#### E. Mobile App Branding (Partially Missing)

| # | Feature | Description | Why It Matters |
|---|---|---|---|
| PG-E1 | **Dynamic App Icon (Android)** | Adaptive icon with school logo as foreground. `<activity-alias>` mechanism for icon switching. | Home screen icon = daily brand impression. Currently shows Vidya Prayag icon for all schools. |
| PG-E2 | **Branded Push Notification Icon** | Custom notification small icon per school (monochrome silhouette of school logo). | Notification shade shows school icon, not generic app icon. |
| PG-E3 | **App Store Listing Customization** | Per-school Play Store listing (screenshots, description, feature graphic) for white-label builds. | White-label schools need their own store presence. |
| PG-E4 | **Deep Linking per School** | School-specific deep link domain (e.g., `dpsrkpuram.app.link`) for sharing content within the app. | Branded links when parents share attendance/results via WhatsApp. |
| PG-E5 | **Branded Onboarding Flow** | First-launch onboarding uses school colors + logo + welcome message from principal. | First impression for new parents. Currently generic Vidya Prayag onboarding. |

#### F. Web / Domain Branding (Partially Missing)

| # | Feature | Description | Why It Matters |
|---|---|---|---|
| PG-F1 | **Custom Domain (Not Just Subdomain)** | School can use their own domain (e.g., `app.dpsrkpuram.com`) with CNAME + automatic TLS via Let's Encrypt. | Premium schools demand their own domain. Subdomain feels like a shared platform. |
| PG-F2 | **Automatic TLS/SSL** | On-demand TLS certificate provisioning for custom domains/subdomains. Zero-config HTTPS. | Security + SEO. Browsers flag HTTP as insecure. |
| PG-F3 | **Custom Favicon per School** | Browser tab favicon uses school logo, not Vidya Prayag logo. | Brand consistency in browser tabs. Small but noticed. |
| PG-F4 | **SEO Meta Tags per School** | Per-school `<title>`, `<meta description>`, Open Graph tags for social sharing. | When parents share the web app link, it should show school name + logo, not Vidya Prayag. |
| PG-F5 | **Custom Error Pages** | School-branded 404, 500, maintenance pages. | Generic error pages break brand immersion. |
| PG-F6 | **PWA Manifest per School** | Per-school `manifest.json` with school name, icon, theme color, display mode. | Installable PWA shows school branding on home screen, not Vidya Prayag. |

#### G. Brand Asset Management (Missing)

| # | Feature | Description | Why It Matters |
|---|---|---|---|
| PG-G1 | **Asset Versioning** | Keep previous logo/icon versions when updating. Allow rollback to previous asset. | Admin uploads wrong logo → needs to revert quickly. |
| PG-G2 | **Auto Image Optimization** | Server auto-generates multiple sizes (512px, 256px, 128px, 64px) from uploaded logo. WebP conversion for smaller payload. | Performance: serve right size to right device. Bandwidth savings in India. |
| PG-G3 | **Brand Asset Library** | Store multiple assets per school (primary logo, horizontal logo, monogram, mascot, banner). Use different assets in different contexts. | Schools have multiple brand assets. One logo URL is insufficient. |
| PG-G4 | **Asset Validation & Preview** | Validate image dimensions, file size, format before upload. Show preview at actual size in context (header, login, report card). | Prevents broken/ugly branding from wrong image dimensions. |

#### H. Branding Governance (Missing)

| # | Feature | Description | Why It Matters |
|---|---|---|---|
| PG-H1 | **Branding Change History** | Full audit trail of branding changes (who, when, what changed, old value, new value). | Accountability. Essential for multi-admin schools. |
| PG-H2 | **Branding Approval Workflow** | For schools with multiple admins: branding changes require approval from principal/super-admin before going live. | Prevents rogue admin from changing school branding inappropriately. |
| PG-H3 | **Branding Rollback** | One-click revert to previous branding configuration. | Quick recovery from bad branding changes. |
| PG-H4 | **"Powered by" Toggle** | Show/hide "Powered by Vidya Prayag" footer. Hidden on Enterprise/premium tier. | Monetization lever. Premium schools pay to remove platform branding. |
| PG-H5 | **Branding Lock** | Super-admin can lock branding for a school (prevent changes by school admin). | For managed/standardized school groups (e.g., DPS society controls all branch branding). |
| PG-H6 | **Branding Schedule** | Schedule branding changes in advance (e.g., set Diwali theme for Oct 20-Nov 5). Auto-applies and auto-reverts. | Admin doesn't need to manually change and revert branding for events. |

#### I. Multi-Campus Branding (Missing)

| # | Feature | Description | Why It Matters |
|---|---|---|---|
| PG-I1 | **Per-Campus Branding Overrides** | School groups (e.g., DPS Society) can set shared base branding with per-campus accent overrides. | Large school networks need both consistency and individuality. |
| PG-I2 | **Campus Selector with Branding** | Login screen shows campus selector (if multi-campus), each campus shows its accent color. | Parents with children in different campuses need visual distinction. |

#### J. Accessibility & Compliance (Missing)

| # | Feature | Description | Why It Matters |
|---|---|---|---|
| PG-J1 | **WCAG AA Contrast Validation** | Server-side + client-side validation that text/background combinations meet 4.5:1 (normal) or 3:1 (large) contrast ratio. Block or warn on failing combinations. | Legal compliance. India's RPWD Act 2016 + global accessibility standards. |
| PG-J2 | **Accessible Color Suggestions** | When admin picks a color that fails contrast, suggest the nearest accessible alternative. | Helps non-designer admins make accessible choices. |
| PG-J3 | **Minimum Font Size Enforcement** | Prevent admin from setting font sizes below readability threshold (12px body, 16px heading minimum). | Prevents unreadable UI from bad admin choices. |

#### K. Performance & Infrastructure (Missing)

| # | Feature | Description | Why It Matters |
|---|---|---|---|
| PG-K1 | **Branding CDN Delivery** | Brand assets served via CDN (Cloudflare/Supabase CDN). Not directly from DB or storage bucket. | Fast logo loading worldwide. Critical for web app. |
| PG-K2 | **Responsive Logo Variants** | Auto-generate logo variants for different screen densities (1x, 2x, 3x) and contexts (header, login, report card). | Crisp logo on all devices. No blurry or oversized images. |
| PG-K3 | **Branding Prefetch** | App prefetches branding on splash screen (before login screen renders). No flash of default branding. | NFR-2 says < 200ms. Prefetch eliminates visible flash entirely. |
| PG-K4 | **Brand Asset Compression** | Auto-compress uploaded images (PNG → WebP for photos, optimized PNG for logos with transparency). | India mobile bandwidth is limited. Smaller assets = faster load. |

#### L. Analytics & Insights (Missing)

| # | Feature | Description | Why It Matters |
|---|---|---|---|
| PG-L1 | **Branding Adoption Dashboard** | Super-admin dashboard: % schools with custom branding, popular colors, asset upload trends, subdomain usage. | Platform-level insights for product team. |
| PG-L2 | **Branding Impact Metrics** | Correlate branding adoption with parent engagement (app opens, notification response rate). | Justify branding feature investment. |
| PG-L3 | **Subdomain Registry** | Super-admin view of all custom subdomains + domains. Search, filter, manage. | Platform governance. Prevent subdomain squatting. |

### 0.5 Recommended Priority for Production

| Priority | Items | Rationale |
|---|---|---|
| **P0 — Ship Now** | PG-A2 (branded email templates), PG-B1 (report card headers), PG-B2 (fee receipts), PG-D3 (contrast checking), PG-K3 (branding prefetch) | Core communications + documents must be school-branded. Accessibility is legal. |
| **P1 — Next Sprint** | PG-A1 (custom email sender), PG-A4 (SMS sender ID), PG-A5 (WhatsApp branding), PG-B3-B8 (document branding), PG-D1 (dark mode palette), PG-D2 (auto-palette), PG-G2 (image optimization) | Complete the branding surface area across all touchpoints. |
| **P2 — Quarter 2** | PG-C1-C4 (custom fonts), PG-D5-D8 (advanced theming), PG-E1-E5 (mobile branding), PG-F1-F6 (web/domain), PG-G1/G3/G4 (asset management) | Deep customization for premium schools. |
| **P3 — Quarter 3** | PG-H1-H6 (governance), PG-I1-I2 (multi-campus), PG-J1-J3 (accessibility), PG-K1/K2/K4 (perf), PG-L1-L3 (analytics) | Enterprise features + governance + optimization. |

---

## 1. Feature Overview

### What

Per-school branding customization: logo, colors, fonts, custom app icon, login screen, email templates, and report card headers. Enables white-label experience where each school's app reflects their brand identity.

### Why — Product Rationale

Schools want their app to reflect their brand identity — logo, colors, name. A white-labeled app increases school's sense of ownership and trust with parents. Without branding, all schools see the same Vidya Prayag branding, which feels generic.

This is a **differentiating feature** (Priority P2, Phase 2, effort M, "Low" value per `DIFFERENTIATING_FEATURES.md`). It's lower priority than core features but important for school retention and premium positioning.

### What Stands Out (Competitive Moat)

From `DIFFERENTIATING_FEATURES.md` §7.2:
> "School Branding Kit — logo, colors, fonts, custom app icon, login screen, email templates, report card headers. White-label experience."

Most school ERPs offer some branding (logo upload), but few offer full white-label with dynamic theming, custom subdomains, and branded communications.

### Goals

- School admin uploads logo, sets primary/secondary colors
- App UI adapts: app icon, splash screen, login screen, header bar, buttons
- Email templates, report cards, newsletters use school branding
- ID cards, certificates use school branding
- Custom subdomain (school.vidyaprayag.com) for web app
- Branding preview before applying

### Non-goals

- [ ] Custom fonts (system fonts used initially)
- [ ] Per-user themes (branding is per-school, not per-user)
- [ ] Dark mode logo variants (future enhancement)
- [ ] Custom domain (e.g., app.dpsrkpuram.com) — only subdomain
- [ ] Multi-language branding (branding is language-agnostic)
- [ ] Branded push notification icons

### Dependencies

- `SchoolsTable` — existing `name`, `logoUrl`, `board`, `mediumOfInstruction` fields
- Supabase Storage — for brand asset uploads (logo, favicon, app icon, splash)
- `VColors` / `VTheme` — existing theming system (modified for dynamic override)
- Email template system — for branded emails
- Report card generator — for branded report cards

### Related Modules

- `server/.../feature/branding/` — new branding module
- `shared/.../core/branding/` — new client branding manager
- `composeApp/.../ui/v2/theme/` — modified for dynamic theming
- `composeApp/.../ui/v2/screens/auth/` — modified login screen
- `composeApp/.../ui/v2/screens/admin/` — new branding settings screen

---

## 2. Current System Assessment

### Existing Code

- `SchoolsTable` has `name`, `logoUrl`, `board`, `mediumOfInstruction`
- `brand-assets/` directory has EnrollPlus branding (platform-level, not per-school)
- No per-school color/font customization
- `DIFFERENTIATING_FEATURES.md` §7.2: School Branding Kit, effort M

### Existing Database

- `SchoolsTable` — has `name`, `logoUrl` (existing but unused for dynamic theming)
- No `school_branding` table exists

### Existing APIs

- No branding API endpoints exist
- School info available via existing school management APIs

### Existing UI

- `VColors` / `VTheme` — hardcoded Vidya Prayag colors
- Login screen — generic Vidya Prayag branding
- No dynamic theming support

### Existing Services

- No branding service exists
- Email templates use Vidya Prayag branding
- Report cards use Vidya Prayag header

### Existing Documentation

- `DIFFERENTIATING_FEATURES.md` §7.2 — School Branding Kit

### Technical Debt

| # | Gap | Details |
|---|---|---|
| TD-1 | No per-school branding table | No `school_branding` table for colors, assets |
| TD-2 | Hardcoded theme | `VColors` hardcoded — no dynamic override |
| TD-3 | Generic login screen | No school-specific login screen |
| TD-4 | No asset upload | No branding asset upload to Supabase Storage |
| TD-5 | No subdomain routing | Web app doesn't resolve school from subdomain |
| TD-6 | Generic email/report templates | No school branding in communications |

### Gaps

| # | Gap | Impact | Severity |
|---|---|---|---|
| G1 | No dynamic theming | All schools look the same | **Medium** |
| G2 | Generic login screen | No school identity at login | **Medium** |
| G3 | No branded communications | Emails/reports look generic | **Medium** |
| G4 | No subdomain routing | Schools can't have custom URL | **Low** |
| G5 | No branding management UI | Admin can't customize branding | **Medium** |

---

## 3. Functional Requirements

### FR-001
| Field | Value |
|---|---|
| **Title** | Brand Asset Upload |
| **Description** | Admin uploads: logo (PNG/SVG), favicon, app icon, splash screen image. |
| **Priority** | High |
| **User Roles** | School Admin |
| **Acceptance notes** | Assets uploaded to Supabase Storage. URLs stored in `school_branding`. Logo: max 1MB, PNG/SVG. App icon: 512x512px. Splash: 1080x1920px. |

### FR-002
| Field | Value |
|---|---|
| **Title** | Color Customization |
| **Description** | Admin sets: primary color, secondary color, accent color (hex). |
| **Priority** | High |
| **User Roles** | School Admin |
| **Acceptance notes** | Hex color codes (e.g., #2563EB). Applied to VColors tokens. Preview before applying. |

### FR-003
| Field | Value |
|---|---|
| **Title** | Dynamic Theming |
| **Description** | App UI dynamically uses school colors (VColors tokens overridden per school). |
| **Priority** | Critical |
| **User Roles** | System |
| **Acceptance notes** | `BrandingManager.applyBranding()` overrides VColors. MaterialTheme uses custom color scheme. |

### FR-004
| Field | Value |
|---|---|
| **Title** | Branded Login Screen |
| **Description** | Login screen shows school logo + name. |
| **Priority** | High |
| **User Roles** | Parent, Teacher, School Admin |
| **Acceptance notes** | School logo displayed above login form. School name shown. Fallback to Vidya Prayag logo if not customized. |

### FR-005
| Field | Value |
|---|---|
| **Title** | Branded Communications |
| **Description** | Email templates, report cards, newsletters, ID cards use school branding. |
| **Priority** | Medium |
| **User Roles** | System |
| **Acceptance notes** | Email headers use school logo + colors. Report cards use school header. ID cards use school logo. |

### FR-006
| Field | Value |
|---|---|
| **Title** | Custom Subdomain |
| **Description** | Custom subdomain mapping (school.vidyaprayag.com). |
| **Priority** | Low |
| **User Roles** | School Admin |
| **Acceptance notes** | Admin sets subdomain (e.g., "dpsrkpuram"). Web app resolves school from subdomain. Subdomain unique across platform. |

### FR-007
| Field | Value |
|---|---|
| **Title** | Branding Preview |
| **Description** | Branding preview before applying. |
| **Priority** | Medium |
| **User Roles** | School Admin |
| **Acceptance notes** | Admin sees preview of login screen, header, buttons with selected colors/logo before saving. |

### FR-008
| Field | Value |
|---|---|
| **Title** | Default Fallback |
| **Description** | Default fallback to Vidya Prayag branding if school hasn't customized. |
| **Priority** | Critical |
| **User Roles** | System |
| **Acceptance notes** | `is_customized = false` → use default Vidya Prayag colors/logo. No blank or broken UI. |

### Non-Functional Requirements

| ID | Requirement |
|---|---|
| NFR-1 | Branding API responds in < 500ms |
| NFR-2 | Branding applied to UI in < 200ms (no visible flash) |
| NFR-3 | Logo image max 1MB, WebP/PNG/SVG |
| NFR-4 | App icon 512x512px, PNG |
| NFR-5 | Splash screen 1080x1920px, PNG/WebP |
| NFR-6 | Branding cached client-side for app session |
| NFR-7 | Subdomain resolution in < 100ms |

---

## 4. User Stories

### School Admin
- [ ] Upload school logo, favicon, app icon, splash screen
- [ ] Set primary, secondary, and accent colors
- [ ] Preview branding before applying
- [ ] Set custom subdomain for web app
- [ ] See branding applied across app (login, header, buttons, emails)

### Parent
- [ ] See school logo and name on login screen
- [ ] See school colors throughout the app
- [ ] Receive branded emails and report cards
- [ ] Access app via school's custom subdomain (web)

### Teacher
- [ ] See school branding throughout the app
- [ ] Receive branded communications

### System
- [ ] Fetch branding on login and apply to UI
- [ ] Use default Vidya Prayag branding if school hasn't customized
- [ ] Resolve school from subdomain for web app
- [ ] Apply school branding to email templates, report cards, ID cards

---

## 5. Business Rules

### BR-001
**Rule:** Branding is per-school, not per-user.
**Enforcement:** `school_branding.school_id` — one branding config per school. All users in school see same branding.

### BR-002
**Rule:** Default fallback to Vidya Prayag branding.
**Enforcement:** If `school_branding` row doesn't exist or `is_customized = false`, use default colors (#2563EB, #1E40AF, #3B82F6) and default logo.

### BR-003
**Rule:** Subdomain must be unique across platform.
**Enforcement:** `school_branding.custom_subdomain` — checked for uniqueness before assignment. Return 409 if already taken.

### BR-004
**Rule:** Branding applied at app launch, not per-screen.
**Enforcement:** `BrandingManager.applyBranding()` called once after login. VColors tokens overridden globally. All screens use overridden colors.

### BR-005
**Rule:** Branding preview before applying.
**Enforcement:** Admin sees live preview in branding settings screen. Changes saved only on "Apply" button click. Preview is client-side only.

### BR-006
**Rule:** Branding assets stored in Supabase Storage.
**Enforcement:** Logos, icons, splash screens uploaded to Supabase Storage bucket `school-branding`. URLs stored in `school_branding` table.

### BR-007
**Rule:** Subdomain format: lowercase alphanumeric + hyphens.
**Enforcement:** Regex validation: `^[a-z0-9][a-z0-9-]{2,30}[a-z0-9]$`. Min 4, max 32 characters. No leading/trailing hyphens.

### BR-008
**Rule:** Branding cached for app session.
**Enforcement:** Branding fetched once after login, cached in memory for app session. Re-fetched on app restart. No real-time branding updates.

---

## 6. Database Design

### 6.1 Entity Relationship Summary

One new table: `school_branding` (1:1 with `schools`). Stores brand assets (URLs), colors, and subdomain per school.

### 6.2 New Tables

#### `school_branding` table

```sql
CREATE TABLE school_branding (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id       UUID NOT NULL UNIQUE,
    logo_url        TEXT,                          -- Supabase Storage URL
    logo_dark_url   TEXT,                          -- dark mode logo variant
    favicon_url     TEXT,
    app_icon_url    TEXT,                          -- for home screen icon
    splash_screen_url TEXT,
    primary_color   VARCHAR(8) DEFAULT '#2563EB',  -- hex
    secondary_color VARCHAR(8) DEFAULT '#1E40AF',
    accent_color    VARCHAR(8) DEFAULT '#3B82F6',
    custom_subdomain TEXT,                         -- "dpsrkpuram"
    login_background_url TEXT,
    is_customized   BOOLEAN NOT NULL DEFAULT false,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now()
);
```

### 6.3 Modified Tables

N/A — no existing tables modified. `SchoolsTable.logoUrl` remains but is superseded by `school_branding.logo_url`.

### 6.4 Indexes

- `school_branding(school_id)` — UNIQUE, for school lookup
- `school_branding(custom_subdomain)` — for subdomain resolution (unique constraint via application logic)

### 6.5 Constraints

- `school_branding.school_id` — NOT NULL, UNIQUE
- `school_branding.primary_color` — VARCHAR(8), default '#2563EB'
- `school_branding.secondary_color` — VARCHAR(8), default '#1E40AF'
- `school_branding.accent_color` — VARCHAR(8), default '#3B82F6'
- `school_branding.is_customized` — NOT NULL, default false
- `school_branding.custom_subdomain` — nullable, unique across non-null values

### 6.6 Foreign Keys

- `school_branding.school_id` → `schools.id` (implicit)

### 6.7 Soft Delete Strategy

N/A — branding row is never deleted. If school wants to revert, set `is_customized = false` and reset colors to defaults.

### 6.8 Audit Fields

- `school_branding.created_at` — when branding row created
- `school_branding.updated_at` — when branding last updated

### 6.9 Migration Notes

Migration: `docs/db/migration_101_school_branding.sql`
- CREATE `school_branding` table
- No data migration (new feature)
- Existing `SchoolsTable.logoUrl` not migrated (superseded by `school_branding.logo_url`)

### 6.10 Exposed Mappings

```kotlin
object SchoolBrandingTable : UUIDTable("school_branding", "id") {
    val schoolId          = uuid("school_id").unique()
    val logoUrl           = text("logo_url").nullable()
    val logoDarkUrl       = text("logo_dark_url").nullable()
    val faviconUrl        = text("favicon_url").nullable()
    val appIconUrl        = text("app_icon_url").nullable()
    val splashScreenUrl   = text("splash_screen_url").nullable()
    val primaryColor      = varchar("primary_color", 8).default("#2563EB")
    val secondaryColor    = varchar("secondary_color", 8).default("#1E40AF")
    val accentColor       = varchar("accent_color", 8).default("#3B82F6")
    val customSubdomain   = text("custom_subdomain").nullable()
    val loginBackgroundUrl = text("login_background_url").nullable()
    val isCustomized      = bool("is_customized").default(false)
    val createdAt         = timestamp("created_at")
    val updatedAt         = timestamp("updated_at")
}
```

Register in `DatabaseFactory.allTables`.

### 6.11 Seed Data

N/A — branding created by school admin. Default row can be auto-created on first school setup.

---

## 7. State Machines

### Branding Customization State Machine

```
not_customized ──admin_sets_colors──> preview ──admin_applies──> customized
  │                                      │
  └──admin_uploads_logo──>               │──admin_cancels──> not_customized
     preview                             │
                                         └──admin_applies──> customized
```

| Current State | Event | Next State | Guard / Condition |
|---|---|---|---|
| `not_customized` | Admin sets colors/logo | `preview` | Admin in branding settings |
| `preview` | Admin clicks "Apply" | `customized` | `is_customized = true`, save to DB |
| `preview` | Admin clicks "Cancel" | `not_customized` | Discard changes |
| `customized` | Admin updates branding | `preview` | Admin in branding settings |
| `customized` | Admin resets to default | `not_customized` | `is_customized = false`, reset colors |

### Subdomain State Machine

```
no_subdomain ──admin_enters_subdomain──> checking ──available──> assigned
  │                                         │
  └──admin_removes_subdomain──>             │──taken──> no_subdomain (error)
  no_subdomain                              │
                                             └──invalid_format──> no_subdomain (error)
```

| Current State | Event | Next State | Guard / Condition |
|---|---|---|---|
| `no_subdomain` | Admin enters subdomain | `checking` | Non-empty input |
| `checking` | Subdomain available | `assigned` | Unique across platform |
| `checking` | Subdomain taken | `no_subdomain` | Return error "Subdomain already taken" |
| `checking` | Invalid format | `no_subdomain` | Return error "Invalid subdomain format" |
| `assigned` | Admin removes subdomain | `no_subdomain` | Set `custom_subdomain = null` |

### Branding Resolution State Machine (Client)

```
app_launch ──fetch_branding──> fetching ──success──> applied ──app_session──> applied
  │                               │
  └──no_school_id──>              │──failure──> default_branding
     default_branding             │
                                   └──not_customized──> default_branding
```

| Current State | Event | Next State | Guard / Condition |
|---|---|---|---|
| `app_launch` | User logged in, has school_id | `fetching` | JWT contains school_id |
| `app_launch` | No school_id (pre-login) | `default_branding` | Use Vidya Prayag defaults |
| `fetching` | Branding fetched, `is_customized = true` | `applied` | Apply custom colors/logo |
| `fetching` | Branding fetched, `is_customized = false` | `default_branding` | Use default colors/logo |
| `fetching` | Fetch failed | `default_branding` | Fallback to defaults |
| `applied` | App session continues | `applied` | Branding persists for session |

---

## 8. Backend Architecture

### 8.1 Component Overview

`BrandingService` handles branding CRUD, asset uploads, and subdomain management. Branding fetched by client after login and applied to UI theming. Public endpoints allow pre-login branding resolution (for login screen and subdomain routing).

### 8.2 Design Principles

1. **Default fallback** — always works with Vidya Prayag defaults, even if school hasn't customized
2. **Public read, admin write** — branding read is public (no auth), write is admin-only
3. **Cache for session** — branding fetched once per app session, not per screen
4. **Assets in Supabase Storage** — logos/icons stored as files, URLs in DB
5. **Subdomain unique** — custom subdomain unique across entire platform

### 8.3 Core Types

#### BrandingService

```kotlin
class BrandingService {
    suspend fun getBranding(schoolId: UUID): SchoolBrandingDto
    suspend fun updateBranding(schoolId: UUID, request: UpdateBrandingRequest): SchoolBrandingDto
    suspend fun uploadAsset(schoolId: UUID, assetType: String, file: ByteArray): String  // returns URL
    suspend fun checkSubdomainAvailable(subdomain: String): Boolean
    suspend fun resolveSubdomain(subdomain: String): SchoolBrandingDto
}
```

### 8.4 Repositories

- `BrandingRepository` — CRUD for `school_branding` table

### 8.5 Mappers

- `BrandingMapper` — maps `school_branding` rows to `SchoolBrandingDto`

### 8.6 Permission Checks

- `getBranding` — public (no auth), for login screen and subdomain resolution
- `updateBranding` — School Admin only
- `uploadAsset` — School Admin only
- `checkSubdomainAvailable` — School Admin only
- `resolveSubdomain` — public (no auth), for web app subdomain routing

### 8.7 Background Jobs

N/A — no background jobs. Branding is on-demand read/write.

### 8.8 Domain Events

- `BrandingUpdated` — emitted when admin updates branding (colors, logo, assets)
- `SubdomainAssigned` — emitted when admin sets custom subdomain
- `SubdomainRemoved` — emitted when admin removes custom subdomain
- `BrandingAssetUploaded` — emitted when admin uploads a brand asset

### 8.9 Caching

- Server-side: branding cached per school, 10-minute TTL
- Client-side: branding cached in memory for app session (no TTL — persists until app restart)
- Subdomain resolution: cached per subdomain, 1-hour TTL

### 8.10 Transactions

- Branding update: single transaction (update `school_branding` row)
- Asset upload: upload to Supabase Storage (no transaction), then update DB URL (single transaction)
- Subdomain assignment: single transaction (update `custom_subdomain`)

### 8.11 Rate Limiting

- Branding read: no rate limiting (public, cached)
- Branding update: 10 updates per school per hour
- Asset upload: 20 uploads per school per hour
- Subdomain check: 30 checks per school per hour

### 8.12 Configuration

- `BRANDING_ENABLED` — default `true`; enable/disable feature
- `BRANDING_CACHE_TTL_SECONDS` — default `600` (10 minutes)
- `BRANDING_MAX_LOGO_SIZE_KB` — default `1024` (1MB)
- `BRANDING_MAX_ICON_SIZE_KB` — default `512`
- `BRANDING_MAX_SPLASH_SIZE_KB` — default `2048` (2MB)
- `BRANDING_SUBDOMAIN_MIN_LENGTH` — default `4`
- `BRANDING_SUBDOMAIN_MAX_LENGTH` — default `32`
- `BRANDING_DEFAULT_PRIMARY_COLOR` — default `#2563EB`
- `BRANDING_DEFAULT_SECONDARY_COLOR` — default `#1E40AF`
- `BRANDING_DEFAULT_ACCENT_COLOR` — default `#3B82F6`

---

## 9. API Contracts

### 9.1 Admin Endpoints

```
GET /api/v1/school/branding
  → 200: SchoolBrandingDto

PATCH /api/v1/school/branding
  Body: { primary_color: "#2563EB", secondary_color: "#1E40AF", accent_color: "#3B82F6" }
  → 200: SchoolBrandingDto

POST /api/v1/school/branding/upload
  Body: multipart { asset_type: "logo", file: <binary> }
  → 200: { url: "https://supabase.url/logo.webp" }

POST /api/v1/school/branding/subdomain
  Body: { subdomain: "dpsrkpuram" }
  → 200: { subdomain: "dpsrkpuram" }
  → 409: Subdomain already taken
  → 400: Invalid subdomain format
```

### 9.2 Public Endpoints

```
GET /api/v1/branding/{schoolId}
  → 200: SchoolBrandingDto
  → 404: School not found

GET /api/v1/branding/subdomain/{subdomain}
  → 200: { schoolId: "uuid", schoolName: "DPS R.K. Puram", branding: SchoolBrandingDto }
  → 404: Subdomain not found
```

### 9.3 DTO Models

All `@Serializable`, wrapped in `ApiResponse<T>` pattern.

```kotlin
@Serializable data class SchoolBrandingDto(
    val schoolId: String,
    val schoolName: String,
    val logoUrl: String?,
    val logoDarkUrl: String?,
    val faviconUrl: String?,
    val appIconUrl: String?,
    val splashScreenUrl: String?,
    val primaryColor: String,    // hex: "#2563EB"
    val secondaryColor: String,  // hex: "#1E40AF"
    val accentColor: String,     // hex: "#3B82F6"
    val customSubdomain: String?,
    val loginBackgroundUrl: String?,
    val isCustomized: Boolean,
)

@Serializable data class UpdateBrandingRequest(
    val primaryColor: String? = null,
    val secondaryColor: String? = null,
    val accentColor: String? = null,
    val loginBackgroundUrl: String? = null,
)

@Serializable data class SubdomainRequest(
    val subdomain: String,
)

@Serializable data class SubdomainResponse(
    val subdomain: String,
)
```

---

## 10. Frontend Architecture

### 10.1 Screens

| Screen | Platform | Role | Description |
|---|---|---|---|
| `LoginScreen` (modified) | Compose | All | Show school logo + name, use school colors |
| `BrandingSettingsScreen` | Compose | School Admin | Branding management: upload assets, set colors, preview, subdomain |
| `SplashScreen` (modified) | Compose | All | Show school splash screen image |

### 10.2 Navigation

- Admin: Admin tab → Settings → Branding → Branding Settings
- Login: Pre-login → branding fetched → school-specific login screen

### 10.3 UX Flows

#### Admin: Configure Branding
1. Admin opens Branding Settings
2. Sees current branding (logo, colors, subdomain)
3. Uploads new logo → sees preview
4. Sets primary/secondary/accent colors → sees live preview
5. Clicks "Apply" → branding saved
6. Optionally sets subdomain → checks availability → saves

#### User: Login with School Branding
1. User opens app (or visits subdomain URL)
2. App fetches branding for school
3. Login screen shows school logo, name, and colors
4. User logs in → branding applied throughout app

### 10.4 State Management

```kotlin
data class BrandingState(
    val branding: SchoolBrandingDto?,
    val isLoading: Boolean,
    val error: String?,
)

data class BrandingSettingsState(
    val currentBranding: SchoolBrandingDto,
    val previewPrimaryColor: String,
    val previewSecondaryColor: String,
    val previewAccentColor: String,
    val previewLogoUrl: String?,
    val subdomainInput: String,
    val subdomainAvailable: Boolean?,
    val isSaving: Boolean,
    val error: String?,
)
```

### 10.5 Dynamic Theming

```kotlin
@Composable
fun VidyaPrayagTheme(
    branding: SchoolBrandingDto?,
    content: @Composable () -> Unit
) {
    val colorScheme = if (branding != null && branding.isCustomized) {
        buildCustomColorScheme(branding)
    } else {
        defaultColorScheme
    }
    MaterialTheme(colorScheme = colorScheme, content = content)
}
```

### 10.6 Branding Manager

```kotlin
class BrandingManager {
    fun applyBranding(branding: SchoolBrandingDto) {
        VColors.lightPrimary = Color(branding.primaryColor.hexToInt())
        VColors.lightSecondary = Color(branding.secondaryColor.hexToInt())
        // ... override all tokens
    }
}
```

### 10.7 App Icon (Android)

Android supports dynamic app icons via `<activity-alias>` in AndroidManifest. However, changing app icon dynamically requires user interaction on some launchers. Alternative: use adaptive icons with school logo as foreground.

### 10.8 Subdomain Routing (Web)

Web app resolves school from subdomain:
- `dpsrkpuram.vidyaprayag.com` → fetch branding for school with subdomain "dpsrkpuram"
- Show school-specific login screen

### 10.9 Offline Support

- Branding cached in memory for app session
- If branding fetch fails, use default Vidya Prayag branding
- No offline branding management (requires online to save)

### 10.10 Loading States

- Branding fetch: no loading state (use defaults until fetched, then apply)
- Asset upload: "Uploading logo..."
- Subdomain check: "Checking availability..."
- Save: "Saving branding..."

### 10.11 Error Handling (UI)

- Branding fetch failure: silent fallback to defaults
- Asset upload failure: "Failed to upload. Please try again."
- Subdomain taken: "Subdomain already taken. Try another."
- Subdomain invalid: "Invalid subdomain. Use lowercase letters, numbers, and hyphens."
- Save failure: "Failed to save branding. Please try again."

### 10.12 Component Integration Guidelines

| Rule | Description |
|---|---|
| **R1** | `VidyaPrayagTheme` wraps entire app with branding-aware color scheme |
| **R2** | Login screen shows school logo (or default) above form |
| **R3** | Color picker in branding settings with live preview |
| **R4** | Logo upload with image picker and crop/resize |
| **R5** | Subdomain input with real-time availability check |
| **R6** | Preview shows login screen, header, and button with selected branding |
| **R7** | "Apply" button saves branding; "Cancel" discards changes |
| **R8** | SplashScreen shows school splash image (or default) |
| **R9** | All UI components use VColors tokens (not hardcoded colors) |
| **R10** | Email/report templates use school branding (server-side rendering) |

---

## 11. Shared Module Changes (KMP)

### 11.1 DTOs

All DTOs defined in section 9.3, placed in `shared/.../branding/domain/model/BrandingModels.kt`.

### 11.2 Domain Models

```kotlin
data class SchoolBranding(
    val schoolId: String,
    val schoolName: String,
    val logoUrl: String?,
    val primaryColor: String,
    val secondaryColor: String,
    val accentColor: String,
    val customSubdomain: String?,
    val isCustomized: Boolean,
)

object DefaultBranding {
    val PRIMARY_COLOR = "#2563EB"
    val SECONDARY_COLOR = "#1E40AF"
    val ACCENT_COLOR = "#3B82F6"
}
```

### 11.3 Repository Interfaces

```kotlin
interface BrandingRepository {
    suspend fun getBranding(token: String): NetworkResult<SchoolBrandingDto>
    suspend fun updateBranding(token: String, request: UpdateBrandingRequest): NetworkResult<SchoolBrandingDto>
    suspend fun uploadAsset(token: String, assetType: String, file: ByteArray): NetworkResult<String>
    suspend fun checkSubdomain(token: String, subdomain: String): NetworkResult<Boolean>
    suspend fun getPublicBranding(schoolId: String): NetworkResult<SchoolBrandingDto>
    suspend fun resolveSubdomain(subdomain: String): NetworkResult<SubdomainResolutionDto>
}
```

### 11.4 UseCases

- `GetBrandingUseCase`
- `UpdateBrandingUseCase`
- `UploadBrandingAssetUseCase`
- `CheckSubdomainUseCase`
- `ResolveSubdomainUseCase`
- `ApplyBrandingUseCase` (client-side, applies to VColors)

### 11.5 Validation

- Colors: valid hex format `^#[0-9A-Fa-f]{6}$`
- Subdomain: `^[a-z0-9][a-z0-9-]{2,30}[a-z0-9]$`, min 4, max 32 chars
- Logo file: PNG/SVG/WebP, max 1MB
- App icon: PNG, 512x512px, max 512KB
- Splash screen: PNG/WebP, 1080x1920px, max 2MB

### 11.6 Serialization

Standard Kotlinx serialization. Colors serialized as hex strings.

### 11.7 Network APIs

Ktor `@Resource` route definitions in `BrandingApi.kt`:
- GET/PATCH `/api/v1/school/branding`
- POST `/api/v1/school/branding/upload`
- POST `/api/v1/school/branding/subdomain`
- GET `/api/v1/branding/{schoolId}` (public)
- GET `/api/v1/branding/subdomain/{subdomain}` (public)

### 11.8 Database Models (Local Cache)

- Branding cached in DataStore as JSON (for app session persistence)
- Cache key: `branding:{schoolId}`

---

## 12. Permissions Matrix

| Action | Super Admin | School Admin | Teacher | Parent |
|---|---|---|---|---|
| View school branding (public) | ✅ | ✅ | ✅ | ✅ |
| Update branding colors | ✅ | ✅ | ❌ | ❌ |
| Upload brand assets | ✅ | ✅ | ❌ | ❌ |
| Set custom subdomain | ✅ | ✅ | ❌ | ❌ |
| Reset branding to default | ✅ | ✅ | ❌ | ❌ |
| View all schools' branding | ✅ | ❌ | ❌ | ❌ |

---

## 13. Notifications

N/A — branding changes don't trigger notifications. Branding is silently applied on next app launch.

---

## 14. Background Jobs

N/A — no background jobs. Branding is on-demand read/write.

---

## 15. Integrations

### Internal Integrations

| System | Integration Point | Direction | Protocol | Error Handling |
|---|---|---|---|---|
| `SchoolsTable` | School name | Read | Direct DB | Use "Unknown School" if not found |
| Supabase Storage | Brand asset storage | Upload/Read | HTTP API | Log on failure |
| `VColors` / `VTheme` | Dynamic theming | Write (client) | Direct call | Fallback to defaults |
| Email template system | Branded emails | Read | Direct call | Use default branding |
| Report card generator | Branded report cards | Read | Direct call | Use default branding |
| ID card generator | Branded ID cards | Read | Direct call | Use default branding |

### External Integrations

| System | Purpose | Direction | Protocol | Authentication | Error Handling |
|---|---|---|---|---|---|
| Supabase Storage | Brand asset hosting | Outbound | HTTP API | Service key (existing) | Log on failure |

### Integration Patterns

- **Supabase Storage:** Admin uploads asset → Supabase URL returned → URL stored in `school_branding`
- **Dynamic theming:** `BrandingManager.applyBranding()` overrides `VColors` tokens after login
- **Email/report branding:** Server-side template rendering fetches `school_branding` for school-specific headers
- **Subdomain routing:** Web app middleware resolves subdomain → school_id → branding

---

## 16. Security

### Authentication

- Admin endpoints: JWT auth via `requireAuth()`, school admin role
- Public endpoints (`GET /branding/{schoolId}`, `GET /branding/subdomain/{subdomain}`): no auth

### Authorization

- School admin can only update branding for own school
- Super admin can update branding for any school
- Public read access for branding (needed for login screen)

### Data Protection

- Brand assets — public (logos, icons visible to all users)
- Colors — public (visible in UI)
- Subdomain — public (visible in URL)
- No PII in branding data

### Input Validation

- Colors: valid hex format `^#[0-9A-Fa-f]{6}$`
- Subdomain: `^[a-z0-9][a-z0-9-]{2,30}[a-z0-9]$`
- Logo: PNG/SVG/WebP, max 1MB
- App icon: PNG, 512x512px, max 512KB
- Splash: PNG/WebP, 1080x1920px, max 2MB

### Rate Limiting

- Branding read: no rate limiting (public, cached)
- Branding update: 10 per school per hour
- Asset upload: 20 per school per hour
- Subdomain check: 30 per school per hour

### Audit Logging

- Branding updated: admin ID, school ID, changes (colors, assets)
- Subdomain assigned/removed: admin ID, school ID, subdomain
- Asset uploaded: admin ID, school ID, asset type, URL

### PII Handling

- No PII in branding data
- School name is public information
- Brand assets are public (logos, icons)

### Multi-tenant Isolation

- `school_branding.school_id` — school-scoped
- Admin can only update own school's branding
- Public read access doesn't expose cross-school data (only requested school's branding)

---

## 17. Performance & Scalability

### Expected Scale

- 1 branding row per school
- 1-5 brand assets per school (logo, favicon, icon, splash, login background)
- Branding fetched once per app session per user

### Query Optimization

- `school_branding(school_id)` — UNIQUE index, O(1) lookup
- `school_branding(custom_subdomain)` — indexed for subdomain resolution

### Indexing Strategy

- `school_branding(school_id)` — UNIQUE, for school lookup
- `school_branding(custom_subdomain)` — for subdomain resolution

### Caching Strategy

- Server-side: branding cached per school, 10-minute TTL
- Client-side: branding cached in memory for app session
- Subdomain resolution: cached per subdomain, 1-hour TTL

### Pagination

N/A — single branding row per school.

### Connection Pooling

Uses existing HikariCP connection pool. No additional pooling needed.

### Async Processing

- Branding fetch: synchronous (with caching)
- Asset upload: async (Supabase Storage upload)
- Branding apply: synchronous (client-side VColors override)

### Scalability Concerns

- Branding fetch volume: 1 per user per app session. With 10,000 users, ~10,000 fetches/day. Cache hit rate > 90%.
- Asset storage: 5 assets × 100 schools = 500 files in Supabase Storage. Negligible.
- Subdomain resolution: 1 per web app visit. Cached for 1 hour.

---

## 18. Edge Cases

| # | Scenario | Expected Behavior |
|---|---|---|
| EC-1 | School has no branding row | Return default branding (`is_customized = false`). |
| EC-2 | Branding fetch fails | Use default Vidya Prayag branding. Silent fallback. |
| EC-3 | Logo URL broken/expired | Show default logo. Log error. |
| EC-4 | Invalid hex color in DB | Use default color. Log error. |
| EC-5 | Subdomain already taken | Return 409 "Subdomain already taken." |
| EC-6 | Subdomain invalid format | Return 400 "Invalid subdomain format." |
| EC-7 | Logo upload exceeds size limit | Return 400 "File too large. Max 1MB." |
| EC-8 | Logo upload invalid format | Return 400 "Invalid format. Use PNG, SVG, or WebP." |
| EC-9 | Admin resets branding to default | Set `is_customized = false`, reset colors to defaults. Keep assets. |
| EC-10 | Web app accessed via root domain (no subdomain) | Show default Vidya Prayag login. |
| EC-11 | Web app accessed via unknown subdomain | Show 404 "School not found." |
| EC-12 | School admin from school A tries to update school B branding | Return 403 "Access denied." |
| EC-13 | Branding applied mid-session | Not supported. Branding applied at app launch only. User must restart app. |
| EC-14 | Dark mode with no dark logo variant | Use light logo. Log warning. |
| EC-15 | App icon change on Android | May require launcher restart. Show "App icon may take a moment to update." |
| EC-16 | Subdomain removed by admin | Web app at that subdomain shows 404. |
| EC-17 | Multiple admins editing branding simultaneously | Last write wins. No conflict resolution. |
| EC-18 | Splash screen image not set | Use default Vidya Prayag splash. |

---

## 19. Error Handling

### Error Response Format

Standard `ApiResponse` error format.

### Error Codes

| Code | HTTP Status | Description | User Message |
|---|---|---|---|
| `BRANDING_NOT_FOUND` | 404 | School branding not found | (Internal — return defaults) |
| `SUBDOMAIN_TAKEN` | 409 | Subdomain already in use | "Subdomain already taken. Try another." |
| `INVALID_SUBDOMAIN` | 400 | Subdomain format invalid | "Invalid subdomain. Use lowercase letters, numbers, and hyphens (4-32 chars)." |
| `INVALID_COLOR` | 400 | Color not valid hex | "Invalid color. Use hex format (#RRGGBB)." |
| `FILE_TOO_LARGE` | 400 | Upload exceeds size limit | "File too large. Max {size}." |
| `INVALID_FILE_FORMAT` | 400 | Upload format not supported | "Invalid format. Use {formats}." |
| `SUBDOMAIN_NOT_FOUND` | 404 | Subdomain doesn't resolve to any school | "School not found for this subdomain." |

### Error Handling Strategy

- **Branding fetch failure:** Silent fallback to defaults. Log error.
- **Asset upload failure:** Return error to admin. Retry available.
- **Subdomain conflict:** Return 409. Admin chooses different subdomain.
- **Color validation:** Return 400. Admin corrects hex code.
- **File validation:** Return 400 with specific message. Admin corrects file.

### Retry Strategy

- Branding fetch: no retry (fallback to defaults)
- Asset upload: admin can retry upload
- Subdomain check: real-time (no retry needed)

### Fallback Behavior

- No branding row: default Vidya Prayag branding
- Branding fetch failure: default Vidya Prayag branding
- Logo URL broken: default Vidya Prayag logo
- Invalid color in DB: default color for that token
- Subdomain not found: 404 page (web)

---

## 20. Analytics & Reporting

### Analytics Dashboard Data

| Metric | Source | Derivation |
|---|---|---|
| Schools with custom branding | `school_branding.is_customized = true` | Count |
| Schools with custom subdomain | `school_branding.custom_subdomain IS NOT NULL` | Count |
| Most popular primary colors | `school_branding.primary_color` | Group by color, count |
| Branding update frequency | Audit logs | Count per school per month |

### Export Capabilities

N/A — branding data not exportable.

### Report Types

| Report | Format | Frequency | Recipient |
|---|---|---|---|
| Branding adoption | JSON (API) | On-demand | Super Admin |
| Subdomain registry | JSON (API) | On-demand | Super Admin |

---

## 21. Testing Strategy

### Unit Tests

- `BrandingService.getBranding()` — existing branding, no branding (defaults), fetch failure
- `BrandingService.updateBranding()` — color validation, partial updates
- `BrandingService.checkSubdomainAvailable()` — available, taken, invalid format
- `BrandingService.resolveSubdomain()` — valid subdomain, unknown subdomain
- `BrandingManager.applyBranding()` — VColors override, default fallback
- Color validation — valid hex, invalid hex
- Subdomain validation — valid, invalid, edge cases (hyphens, length)

### Integration Tests

- Full branding flow: admin sets colors → fetch branding → verify colors applied
- Asset upload: upload logo → verify URL stored → fetch branding → verify URL
- Subdomain: set subdomain → resolve via public API → verify school returned
- Default fallback: school with no branding → fetch → verify defaults returned

### E2E Tests

- Admin configures branding → user logs in → sees school logo and colors
- Web app accessed via subdomain → shows school-specific login
- Admin resets branding → user sees default Vidya Prayag branding on next launch

### Performance Tests

- Branding fetch: < 500ms (cached: < 50ms)
- Asset upload: < 3 seconds (1MB file)
- Subdomain resolution: < 100ms
- Branding apply (client): < 200ms (no visible flash)

### Test Data

- 3 schools: one with full branding, one with partial, one with no branding
- Sample logos (PNG, SVG, WebP)
- Sample subdomains (valid, invalid, taken)

### Test Environment

- Test database with `school_branding` table
- Mock Supabase Storage (returns URLs)
- Test JWT tokens for admin and parent roles

---

## 22. Acceptance Criteria

- [ ] Admin uploads logo, favicon, app icon, splash screen
- [ ] Admin sets primary/secondary/accent colors
- [ ] App UI dynamically uses school colors
- [ ] Login screen shows school logo + name
- [ ] Email templates, report cards use school branding
- [ ] Custom subdomain works for web app
- [ ] Branding preview available before applying
- [ ] Default fallback when not customized
- [ ] Branding cached for app session
- [ ] Subdomain uniqueness enforced
- [ ] Color validation (hex format)
- [ ] Asset size and format validation

---

## 23. Implementation Roadmap

| Phase | Duration | Tasks |
|---|---|---|
| 1 | 1 day | DB migration `migration_101_school_branding.sql`, Exposed table, register in `DatabaseFactory` |
| 2 | 2 days | `BrandingService` (CRUD, asset upload, subdomain management) |
| 3 | 2 days | Dynamic theming in `VColors`/`VTheme` — `BrandingManager` |
| 4 | 2 days | Login screen + splash screen branding |
| 5 | 2 days | Email/report card/newsletter branding integration |
| 6 | 2 days | Client UI: `BrandingSettingsScreen` (upload, color picker, preview, subdomain) |
| 7 | 1 day | Subdomain routing (web app middleware) |
| 8 | 1 day | Tests: unit, integration, E2E |

### Pre-Implementation Checklist

- [ ] Verify Supabase Storage bucket for brand assets
- [ ] Verify `VColors` token system supports dynamic override
- [ ] Verify Android adaptive icon support
- [ ] Verify web app subdomain routing middleware
- [ ] Verify email template system supports dynamic branding

---

## 24. File-Level Impact Analysis

### Server

| File | Change Type | Description |
|---|---|---|
| `server/.../db/Tables.kt` | Add | `SchoolBrandingTable` |
| `server/.../db/DatabaseFactory.kt` | Modify | Register `SchoolBrandingTable` in `allTables` |
| `server/.../feature/branding/BrandingService.kt` | **New** | Core branding service (CRUD, upload, subdomain) |
| `server/.../feature/branding/BrandingRepository.kt` | **New** | Branding repository |
| `server/.../feature/branding/BrandingRouting.kt` | **New** | API endpoints (admin + public) |
| `docs/db/migration_101_school_branding.sql` | **New** | DDL: `school_branding` table |

### Shared (KMP)

| File | Change Type | Description |
|---|---|---|
| `shared/.../branding/domain/model/BrandingModels.kt` | **New** | DTOs, domain models, `DefaultBranding` |
| `shared/.../branding/domain/repository/BrandingRepository.kt` | **New** | Repository interface |
| `shared/.../branding/data/remote/BrandingApi.kt` | **New** | HTTP API definitions |
| `shared/.../core/branding/BrandingManager.kt` | **New** | Client branding application (VColors override) |

### Client (Compose)

| File | Change Type | Description |
|---|---|---|
| `composeApp/.../ui/v2/theme/VTheme.kt` | Modify | Dynamic theming support (`VidyaPrayagTheme` with branding param) |
| `composeApp/.../ui/v2/theme/VColors.kt` | Modify | Support dynamic color override |
| `composeApp/.../ui/v2/screens/auth/LoginScreen.kt` | Modify | School logo + name display |
| `composeApp/.../ui/v2/screens/SplashScreen.kt` | Modify | School splash image |
| `composeApp/.../ui/v2/screens/admin/BrandingSettingsScreen.kt` | **New** | Branding management UI (upload, colors, preview, subdomain) |

---

## 25. Future Enhancements

| # | Enhancement | Priority | Effort | Notes |
|---|---|---|---|---|
| F-1 | Dark mode logo variants | Medium | S | `logo_dark_url` for dark mode |
| F-2 | Custom fonts | Medium | M | Per-school font selection |
| F-3 | Custom domain (not subdomain) | Low | L | e.g., app.dpsrkpuram.com |
| F-4 | Branded push notification icons | Low | S | Custom notification icon per school |
| F-5 | Branding templates | Low | S | Pre-made branding templates |
| F-6 | Multi-language branding | Low | M | Different logos for different languages |
| F-7 | Branded WhatsApp templates | Medium | M | School-specific WhatsApp template headers |
| F-8 | Branding A/B testing | Low | L | Test different color schemes |
| F-9 | Animated splash screens | Low | M | Lottie/animated splash per school |
| F-10 | Branded in-app notifications | Low | S | School branding in notification cards |

---

## Appendix A: Sequence Diagrams

### A.1 Branding Application Flow

```
User (app)       Server              Cache              BrandingManager
  │                  │                    │                    │
  │  POST /login     │                    │                    │
  │  ──────────────> │                    │                    │
  │  ←──JWT (school_id)                   │                    │
  │                  │                    │                    │
  │  GET /branding/{schoolId}             │                    │
  │  ──────────────> │                    │                    │
  │                  │──check cache──────→│                    │
  │                  │←──cache miss───────│                    │
  │                  │──query DB──────────────────────────→   │
  │                  │←──branding row──────────────────────   │
  │                  │──store in cache───→│                    │
  │  ←──200: SchoolBrandingDto            │                    │
  │                  │                    │                    │
  │  ──applyBranding(branding)──────────────────────────────→│
  │                  │                    │   VColors overridden
  │  ──render UI with school colors──────→│                    │
  │                  │                    │                    │
```

### A.2 Admin Updates Branding

```
Admin (app)       Server              Supabase Storage
  │                  │                    │
  │  PATCH /school/branding               │
  │  { primary_color: "#FF0000" }         │
  │  ──────────────> │                    │
  │                  │──validate hex      │
  │                  │──update DB         │
  │  ←──200: updated branding              │
  │                  │                    │
  │  POST /school/branding/upload         │
  │  { asset_type: "logo", file }         │
  │  ──────────────> │                    │
  │                  │──upload to Supabase──────────────────→│
  │                  │←──URL─────────────────────────────────│
  │                  │──update DB with URL                   │
  │  ←──200: { url }                       │
  │                  │                    │
```

---

## Appendix B: Domain Model / ER Diagram

```
┌──────────────────────────────────────────────────────────────────────┐
│                           schools (existing)                           │
│  id (PK)                                                              │
│  name, logoUrl, board, mediumOfInstruction                            │
└──────────────────────────┬───────────────────────────────────────────┘
                           │
                           │ 1:1
                           ▼
┌──────────────────────────────────────────────────────────────────────┐
│                      school_branding (new)                             │
│  id (PK)                                                              │
│  school_id (UNIQUE, FK → schools.id)                                  │
│  logo_url, logo_dark_url, favicon_url                                 │
│  app_icon_url, splash_screen_url, login_background_url                │
│  primary_color (#2563EB), secondary_color (#1E40AF), accent_color    │
│  custom_subdomain (nullable, unique)                                  │
│  is_customized (default false)                                        │
│  created_at, updated_at                                               │
│  INDEX: (school_id) UNIQUE                                            │
│  INDEX: (custom_subdomain)                                            │
└──────────────────────────────────────────────────────────────────────┘

Brand Assets (in Supabase Storage, not in DB):
┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐
│ logo.png          │  │ app_icon.png     │  │ splash.webp      │
│ (Supabase URL)    │  │ (Supabase URL)   │  │ (Supabase URL)   │
└──────────────────┘  └──────────────────┘  └──────────────────┘
```

---

## Appendix C: Event Flow

### Domain Events

| Event | Emitter | Consumers | Payload | Side Effects |
|---|---|---|---|---|
| `BrandingUpdated` | `BrandingService.updateBranding()` | None (logged) | `schoolId, changes` | Cache invalidated |
| `SubdomainAssigned` | `BrandingService.updateSubdomain()` | None (logged) | `schoolId, subdomain` | Web app can resolve subdomain |
| `SubdomainRemoved` | `BrandingService.removeSubdomain()` | None (logged) | `schoolId, oldSubdomain` | Web app 404 for old subdomain |
| `BrandingAssetUploaded` | `BrandingService.uploadAsset()` | None (logged) | `schoolId, assetType, url` | Asset URL stored in DB |

### Event Delivery Guarantees

- Events emitted synchronously within service methods
- All events logged for audit
- No external consumers — events are internal audit trail

### Branding Resolution on Login

After user login, JWT contains `school_id`. Client fetches branding:
```
GET /api/v1/branding/{schoolId}
```

Client applies branding to VColors:
```kotlin
class BrandingManager {
    fun applyBranding(branding: SchoolBrandingDto) {
        VColors.lightPrimary = Color(branding.primaryColor.hexToInt())
        VColors.lightSecondary = Color(branding.secondaryColor.hexToInt())
        // ... override all tokens
    }
}
```

---

## Appendix D: Configuration

### Environment Variables

| Variable | Default | Description |
|---|---|---|
| `BRANDING_ENABLED` | `true` | Enable/disable branding feature |
| `BRANDING_CACHE_TTL_SECONDS` | `600` | Server-side cache TTL (10 min) |
| `BRANDING_MAX_LOGO_SIZE_KB` | `1024` | Max logo file size (1MB) |
| `BRANDING_MAX_ICON_SIZE_KB` | `512` | Max app icon file size |
| `BRANDING_MAX_SPLASH_SIZE_KB` | `2048` | Max splash screen file size (2MB) |
| `BRANDING_SUBDOMAIN_MIN_LENGTH` | `4` | Min subdomain length |
| `BRANDING_SUBDOMAIN_MAX_LENGTH` | `32` | Max subdomain length |
| `BRANDING_DEFAULT_PRIMARY_COLOR` | `#2563EB` | Default primary color |
| `BRANDING_DEFAULT_SECONDARY_COLOR` | `#1E40AF` | Default secondary color |
| `BRANDING_DEFAULT_ACCENT_COLOR` | `#3B82F6` | Default accent color |

### Feature Flags

| Flag | Default | Description |
|---|---|---|
| `BRANDING_ENABLED` | `true` | Enable/disable branding feature |
| `BRANDING_SUBDOMAIN_ENABLED` | `true` | Enable/disable custom subdomains |
| `BRANDING_DYNAMIC_APP_ICON` | `false` | Enable/disable dynamic app icon (Android) |
| `BRANDING_EMAIL_INTEGRATION` | `true` | Enable/disable branded email templates |

### School-Level Settings

N/A — branding IS the school-level setting. No additional configuration needed.

---

## Appendix E: Migration & Rollback

### Migration: `migration_101_school_branding.sql`

```sql
-- Migration 101: School Branding Kit
-- Creates school_branding table for per-school branding customization

BEGIN;

CREATE TABLE IF NOT EXISTS school_branding (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id       UUID NOT NULL UNIQUE,
    logo_url        TEXT,
    logo_dark_url   TEXT,
    favicon_url     TEXT,
    app_icon_url    TEXT,
    splash_screen_url TEXT,
    primary_color   VARCHAR(8) DEFAULT '#2563EB',
    secondary_color VARCHAR(8) DEFAULT '#1E40AF',
    accent_color    VARCHAR(8) DEFAULT '#3B82F6',
    custom_subdomain TEXT,
    login_background_url TEXT,
    is_customized   BOOLEAN NOT NULL DEFAULT false,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_school_branding_subdomain
    ON school_branding (custom_subdomain)
    WHERE custom_subdomain IS NOT NULL;

COMMIT;
```

### Rollback: `migration_101_rollback.sql`

```sql
BEGIN;
DROP TABLE IF EXISTS school_branding;
COMMIT;
```

### Migration Validation

- Verify `school_branding` table created with correct columns
- Verify `school_id` UNIQUE constraint created
- Verify `custom_subdomain` index created
- Run `SELECT count(*) FROM school_branding` — should be 0 (new feature)
- Verify default colors match expected values

---

## Appendix F: Observability

### Structured Logging

| Log Level | Event | Context Fields |
|---|---|---|
| INFO | Branding fetched | `schoolId, isCustomized, cacheHit` |
| INFO | Branding updated | `schoolId, adminId, changes` |
| INFO | Asset uploaded | `schoolId, assetType, url, fileSize` |
| INFO | Subdomain assigned | `schoolId, subdomain` |
| INFO | Subdomain removed | `schoolId, oldSubdomain` |
| INFO | Subdomain resolved | `subdomain, schoolId` |
| WARN | Branding not found (using defaults) | `schoolId` |
| WARN | Logo URL broken | `schoolId, logoUrl` |
| WARN | Invalid color in DB | `schoolId, color, field` |
| ERROR | Asset upload failed | `schoolId, assetType, error` |
| ERROR | Branding fetch failed | `schoolId, error` |

### Metrics

| Metric | Type | Labels | Description |
|---|---|---|---|
| `branding_fetches_total` | Counter | `cache_hit` | Total branding fetches |
| `branding_cache_hit_rate` | Gauge | — | Cache hit percentage |
| `branding_updates_total` | Counter | `school_id` | Branding updates per school |
| `branding_asset_uploads_total` | Counter | `asset_type` | Asset uploads by type |
| `branding_subdomain_count` | Gauge | — | Total custom subdomains |
| `branding_customized_schools` | Gauge | — | Schools with custom branding |
| `branding_fetch_duration` | Histogram | — | Branding fetch latency |

### Health Checks

| Check | Endpoint | Description |
|---|---|---|
| Branding service | `/health/branding` | Verify branding service and DB accessible |
| Supabase Storage | `/health/storage` | Verify Supabase Storage accessible (existing) |

### Alerts

| Alert | Condition | Severity | Notification |
|---|---|---|---|
| Branding fetch failure rate high | Error rate > 5% | Warning | Email to dev team |
| Asset upload failure rate high | Upload error rate > 10% | Warning | Email to dev team |
| Subdomain resolution slow | Resolution time > 500ms | Warning | Email to dev team |

### Dashboards

| Dashboard | Panels | Audience |
|---|---|---|
| Branding Adoption | Customized schools, subdomain count, popular colors | Product Team |
| Branding Performance | Fetch duration, cache hit rate, error rate | Dev Team |
| Asset Uploads | Upload count by type, failure rate, storage usage | Dev Team |

### Risk Analysis

| Risk | Likelihood | Impact | Mitigation |
|---|---|---|---|
| Branding fetch failure | Low | Low | Default fallback. Silent. |
| Logo URL broken | Low | Low | Default logo fallback. |
| Subdomain conflict | Medium | Low | Uniqueness check before assignment. |
| Invalid color in DB | Very Low | Low | Validation on write. Default fallback on read. |
| Asset upload failure | Medium | Low | Admin can retry. Default assets used. |
| Dynamic app icon not supported | High | Low | Use adaptive icons. May require launcher restart. |
| Subdomain DNS propagation delay | Low | Low | Use wildcard DNS. CNAME resolves immediately. |
| Color contrast/accessibility issues | Medium | Medium | Preview before applying. Admin responsibility. |
