# Multi-Language Support — Technical Specification

> **Document status:** Implementation-ready blueprint
> **Last updated:** 2026-06-27
> **Prerequisites:** None
> **Source:** `DIFFERENTIATING_FEATURES.md` §8.1
> **Template:** `_SPEC_TEMPLATE.md` v1 (25 mandatory + 6 optional sections)

---

## 1. Feature Overview

### What

Full multi-language support for the app UI and content: 10 Indian languages (English, Hindi, Bengali, Tamil, Telugu, Marathi, Gujarati, Kannada, Malayalam, Punjabi) with per-user language preference, server-side content translation, and locale-aware formatting.

### Why — Product Rationale

India has 22 official languages and hundreds of dialects. Most school ERP apps are English-only, excluding non-English-speaking parents (grandparents, rural families). Multi-language support is critical for adoption across Tier 2/3/4 cities and rural areas where parents may be more comfortable in their regional language.

This is a **differentiating feature** (Priority P1, Phase 2, effort L, "High" value per `DIFFERENTIATING_FEATURES.md`). It dramatically expands the addressable market by making the app accessible to non-English-speaking parents.

### What Stands Out (Competitive Moat)

From `DIFFERENTIATING_FEATURES.md` §8.1:
> "Multi-Language — 10 Indian languages, per-user preference, server-side content translation. Data readiness: languagePref field exists."

No major school ERP supports 10 Indian languages with AI-powered content translation. Most offer English only or 2-3 languages.

### Goals

- App UI translated into 10 languages (Compose Multiplatform string resources)
- Per-user language preference (stored in `app_users.languagePref` — field already exists)
- Server-side pre-translated notification templates (`ServerStrings`) — no AI, same approach as client `AppStrings`
- Locale-aware date, number, and currency formatting
- Language switcher in settings (no app restart required)
- **Language tracking** — admin dashboard shows language distribution per school, per-user language history
- **Super Admin language dashboard** — website page showing all users' current language preferences grouped by role (FR-017)
- **Super Admin ServerStrings manager** — website page to view and update server-level notification translations (FR-018)

### Non-goals

- [ ] Voice-based language switching
- [ ] Auto-detection of user's language from phone settings (manual selection only)
- [ ] **AI-powered content translation** (initial release uses pre-translated string maps only — `AppStrings` + `ServerStrings`. Dynamic content like announcements stays English. AI translation is F-14, future enhancement — see §25)
- [ ] Translation of user-generated content (posts, messages)
- [ ] OCR translation of uploaded documents
- [ ] RTL layout for existing languages (only LTR for initial 10 languages)
- [ ] Language-specific fonts (system fonts used on Android/iOS/JVM; pre-check required for Wasm/JS — see §10.13)
- [ ] Full grammatical gender support (gender-neutral phrasing used instead; targeted `_m`/`_f` variants only for strings where neutral phrasing is impossible — see §10.7)
- [ ] Cross-script search / transliteration (same-script `ILIKE` matching only — see §10.15, F-11)
- [ ] CLDR locale-aware collation (Unicode code-point sort for initial release — see NFR-10, F-12)

### Dependencies

- `AppUsersTable.languagePref` — existing field (VARCHAR, current DB default 'hi'; **migration required** to ALTER default to 'en', see §6.9)
- `SchoolsTable.medium` — existing field (VARCHAR(32), NOT NULL). Stores human-readable medium of instruction (e.g., "English", "Hindi"). **Not** a language code — does not map to `languagePref` codes (en, hi, etc.). Informational only; does not override user `languagePref`.
- `WHATSAPP_INTEGRATION_SPEC.md` — WhatsApp templates support multi-language
- `Notify.kt` — notification dispatch (modified for ServerStrings template lookup)
- `AI_REPORT_CARD_SPEC.md` — report card translation (existing integration, separate from this spec)
- **`website/` — Next.js admin dashboard** (modified for Super Admin language dashboard + ServerStrings manager, FR-017/FR-018)
- **`website/src/lib/admin/nav.ts`** — admin sidebar navigation (add 2 `superAdminOnly` nav items)
- **`website/src/lib/admin/session.ts`** — admin auth session (provides `session.role` for Super Admin check)

### Related Modules

- `shared/.../core/locale/` — new locale management module
- `server/.../feature/i18n/` — new internationalization module
- `server/.../feature/notifications/` — notification dispatch (modified)
- **`website/src/app/admin/language-dashboard/`** — new Super Admin language dashboard page (FR-017)
- **`website/src/app/admin/server-strings/`** — new Super Admin ServerStrings manager page (FR-018)
- **`website/src/components/admin/`** — new `LanguageDashboard.tsx` + `ServerStringsManager.tsx` components
- `composeApp/.../ui/v2/screens/school/SchoolSettingsScreenV2.kt` — admin settings (modified)
- `composeApp/.../ui/v2/screens/teacher/TeacherProfileScreenV2.kt` — teacher profile (modified)
- `composeApp/.../ui/v2/screens/parent/ParentProfileCardScreenV2.kt` — parent profile (modified)
- `composeApp/.../ui/v2/screens/**/*.kt` — all screens (modified for string externalization)

---

## 2. Current System Assessment

### Existing Code

- `AppUsersTable` has `languagePref` field (VARCHAR, default 'hi')
- `SchoolsTable` has `medium` field (VARCHAR(32), NOT NULL) — stores human-readable medium of instruction (e.g., "English", "Hindi"). Not a language code.
- WhatsApp templates support multi-language (from `WHATSAPP_INTEGRATION_SPEC.md`)
- No UI translations exist — all strings are hardcoded in English
- `DIFFERENTIATING_FEATURES.md` §8.1: Multi-Language, effort L, data readiness: "languagePref field exists"

### Existing Database

- `AppUsersTable.languagePref` — VARCHAR, default 'hi'. Already exists, not used. **Note:** DB column default is 'hi', not 'en' — migration required to align with English-first design (see §6.9).
- `SchoolsTable.medium` — VARCHAR(32), NOT NULL. Stores human-readable medium of instruction (e.g., "English", "Hindi"). **Not** a language code — distinct from `languagePref` codes (en, hi, etc.). Informational only; does not override user `languagePref`.

### Existing APIs

- No language-related API endpoints exist
- `PATCH /api/v1/user/profile` — could be extended, but dedicated endpoint preferred

### Existing UI

- All UI strings hardcoded in English
- No string resource files
- No locale management
- Settings screen exists but no language switcher

### Existing Services

- `Notify.kt` — notification dispatch (no translation)
- `NotificationService.kt` — FCM push dispatch (no translation)
- No content translation service

### Existing Documentation

- `DIFFERENTIATING_FEATURES.md` §8.1 — Multi-Language
- `WHATSAPP_INTEGRATION_SPEC.md` — WhatsApp multi-language templates
- `AI_INFRASTRUCTURE_SPEC.md` — AI service (for translation)
- `AI_REPORT_CARD_SPEC.md` — Report card translation (existing integration)

### Technical Debt

| # | Gap | Details |
|---|---|---|
| TD-1 | All UI strings hardcoded | No string resource files — strings embedded in Compose code |
| TD-2 | No locale management | No `LocaleManager`, no `CompositionLocal` for locale |
| TD-3 | No content translation | No `ContentTranslationService` — all content English only |
| TD-4 | No locale-aware formatting | Dates, numbers, currency all in English/US format |
| TD-5 | `languagePref` field unused | Field exists in DB but never read or written |
| TD-6 | No language switcher UI | No settings UI for language selection |

### Gaps

| # | Gap | Impact | Severity |
|---|---|---|---|
| G1 | English-only UI | Excludes non-English-speaking parents | **High** |
| G2 | No content translation | Announcements, notifications in English only | **High** |
| G3 | No locale-aware formatting | Dates/numbers in US format, not Indian | **Medium** |
| G4 | `languagePref` field unused | Existing infrastructure wasted | **Low** |
| G5 | No language switcher | Users cannot change language | **High** |

---

## 3. Functional Requirements

### FR-001
| Field | Value |
|---|---|
| **Title** | UI String Externalization |
| **Description** | All UI strings externalized to string resources for 10 languages. |
| **Priority** | Critical |
| **User Roles** | System |
| **Acceptance notes** | 10 string resource files (one per language). All hardcoded strings replaced with resource references. |

### FR-002
| Field | Value |
|---|---|
| **Title** | Instant Language Switch |
| **Description** | User selects language in settings → instant switch (no restart). |
| **Priority** | Critical |
| **User Roles** | Parent, Teacher, School Admin |
| **Acceptance notes** | `LocaleManager.setLocale()` updates `StateFlow` → triggers recomposition. No app restart. |

### FR-003
| Field | Value |
|---|---|
| **Title** | Server-Side Notification Translation |
| **Description** | Server-side notification translation: notification titles and bodies translated to user's languagePref via pre-translated `ServerStrings` templates. Dynamic content (announcements) stays English in initial release — AI translation is F-14 (future). |
| **Priority** | High |
| **User Roles** | System |
| **Acceptance notes** | `ServerStrings.fill(key, lang, params)` resolves pre-translated templates with placeholder substitution. Falls back to English if template or language missing. No AI calls in initial release. |

### FR-004
| Field | Value |
|---|---|
| **Title** | Locale-Aware Date Formatting |
| **Description** | Locale-aware date formatting (e.g., "१५ जुलाई" in Hindi). |
| **Priority** | Medium |
| **User Roles** | System |
| **Acceptance notes** | `DateFormatter` expect/actual class per platform. Uses ICU/locale-aware formatting. |

### FR-005
| Field | Value |
|---|---|
| **Title** | Locale-Aware Number/Currency Formatting |
| **Description** | Locale-aware number/currency formatting (₹1,00,000 in Indian format). |
| **Priority** | Medium |
| **User Roles** | System |
| **Acceptance notes** | `CurrencyFormatter` expect/actual class. Indian numbering system (lakhs, crores). |

### FR-006
| Field | Value |
|---|---|
| **Title** | Language Preference Sync |
| **Description** | Language preference synced to server (`app_users.languagePref`). |
| **Priority** | High |
| **User Roles** | Parent, Teacher, School Admin |
| **Acceptance notes** | `PATCH /api/v1/user/language-pref` updates `app_users.languagePref`. Persisted locally in DataStore. |

### FR-007
| Field | Value |
|---|---|
| **Title** | WhatsApp in Preferred Language |
| **Description** | WhatsApp notifications sent in user's preferred language. |
| **Priority** | High |
| **User Roles** | System |
| **Acceptance notes** | Uses pre-approved Meta templates per language. Template selected based on `languagePref`. |

### FR-008
| Field | Value |
|---|---|
| **Title** | English Fallback |
| **Description** | Fallback to English if translation unavailable or string resource missing. |
| **Priority** | Critical |
| **User Roles** | System |
| **Acceptance notes** | If `ServerStrings` template or `AppStrings` key is missing for a language, fall back to English. Never show blank or error. |

### FR-009
| Field | Value |
|---|---|
| **Title** | Pluralized UI Strings |
| **Description** | Strings with counts (notifications, attendance days, announcements) use ICU MessageFormat plural patterns for correct singular/plural forms in all 10 languages. |
| **Priority** | High |
| **User Roles** | System |
| **Acceptance notes** | `appPlural(key, count)` resolves ICU `{count, plural, ...}` patterns per locale. All 10 languages use CLDR `one`/`other` categories. See §10.6. |

### FR-010
| Field | Value |
|---|---|
| **Title** | Gender-Neutral Translation |
| **Description** | UI strings and pre-translated notification templates use gender-neutral phrasing to avoid grammatical gender complexity across Indian languages. |
| **Priority** | Medium |
| **User Roles** | System |
| **Acceptance notes** | Translators use neutral nouns (बच्चा not बालक/बालिका). Rare gendered variants use `_m`/`_f` key suffix. See §10.7. |

### FR-011
| Field | Value |
|---|---|
| **Title** | First-Launch Language Selection |
| **Description** | On first app launch (no `languagePref` in DataStore), present a language selection screen before the login screen. User picks from 10 languages with native names. Selection persists to DataStore and syncs to server on login. |
| **Priority** | High |
| **User Roles** | Parent, Teacher, School Admin |
| **Acceptance notes** | Shown only when `LocaleManager.currentLocale` has no stored value. User can change later in Settings. Default selection = English. See §10.14. |

### FR-012
| Field | Value |
|---|---|
| **Title** | Locale-Aware Number Formatting |
| **Description** | General numbers (percentages, counts, scores) are formatted using locale-aware digits. "85%" in Hindi renders as "८५%". |
| **Priority** | Medium |
| **User Roles** | System |
| **Acceptance notes** | `NumberFormatter(locale)` formats integers, decimals, and percentages with locale-appropriate digits. Extends `CurrencyFormatter` pattern. See §10.8. |

### FR-013
| Field | Value |
|---|---|
| **Title** | Non-English Search Support |
| **Description** | In-app search (school search, student search) accepts non-English input. Users can search using Hindi, Bengali, Tamil, etc. Server performs case-insensitive `ILIKE` matching — no transliteration or cross-script matching in initial release. |
| **Priority** | Low |
| **User Roles** | Parent, Teacher, School Admin |
| **Acceptance notes** | Search queries in Indic scripts match stored data in the same script. Cross-script matching (Hindi query → English data) is a non-goal. See §10.15. |

### FR-014
| Field | Value |
|---|---|
| **Title** | Translation Quality Review Process |
| **Description** | AI-translated string resources undergo a human review workflow before release. Reviewer approves or rejects each string; rejected strings are re-translated or manually corrected. |
| **Priority** | High |
| **User Roles** | Dev Team |
| **Acceptance notes** | Review spreadsheet per language. Reviewer = bilingual team member or contracted translator. Approval gates release. See §10.16. |

### FR-016
| Field | Value |
|---|---|
| **Title** | Language Tracking & Analytics |
| **Description** | School Admin can view language distribution across their school's users. System tracks language preference changes per user (history table). Super Admin can view platform-wide language adoption. |
| **Priority** | High |
| **User Roles** | School Admin, Super Admin |
| **Acceptance notes** | `GET /api/v1/school/language-distribution` returns aggregate counts + percentages. `language_pref_history` table records every change (user, old lang, new lang, timestamp). `GET /api/v1/school/users-language-pref` returns per-user language list. See §9.3, §20. |

### FR-015
| Field | Value |
|---|---|
| **Title** | String Resource Completeness Check |
| **Description** | Automated CI check verifies all 10 language maps in `AppStrings` have the same set of keys as the English canonical map. Missing keys fail the build. |
| **Priority** | High |
| **User Roles** | System |
| **Acceptance notes** | Unit test `AppStringsKeyParityTest` compares key sets. Missing keys in any language → build failure. See §10.17. |

### FR-017
| Field | Value |
|---|---|
| **Title** | Super Admin Language Dashboard (Website) |
| **Description** | Super Admin can view all app users' current language preferences on the website admin dashboard, grouped by role (School Admin, Teacher, Parent) in 3 collapsible dropdowns. Each dropdown shows user name, phone, school name, current language, and last changed timestamp. |
| **Priority** | Medium |
| **User Roles** | Super Admin |
| **Acceptance notes** | `GET /api/admin/users-by-language` returns all users grouped by role with their `languagePref`. Website page at `/admin/language-dashboard` renders 3 collapsible sections. Super Admin only — School Admin and other roles see access denied. See §9.3, §20. |

### FR-018
| Field | Value |
|---|---|
| **Title** | ServerStrings Translation Manager (Website) |
| **Description** | Super Admin can view and update all server-level notification template translations (`ServerStrings`) from the website. Page shows a table of all keys × all 10 languages with inline editing. DB-backed overrides layer on top of compiled Kotlin defaults. |
| **Priority** | Medium |
| **User Roles** | Super Admin |
| **Acceptance notes** | `GET /api/admin/server-strings` returns all keys × languages (compiled defaults + DB overrides). `PATCH /api/admin/server-strings/{key}` updates a single translation in `server_string_overrides` table. Website page at `/admin/server-strings` renders editable table. Changes take effect immediately (in-memory cache invalidated on update). Super Admin only. See §6.2, §8.3, §9.3. |

### Non-Functional Requirements

| ID | Requirement |
|---|---|
| NFR-1 | Language switch triggers recomposition in < 500ms |
| NFR-2 | String resource files < 500KB per language |
| NFR-3 | All 10 languages render correctly on Android, iOS, JVM, web. **Pre-implementation font rendering check required for Wasm/JS** — see §10.13. |
| NFR-4 | No app restart required for language switch |
| NFR-5 | Screen readers (TalkBack/VoiceOver) correctly read Indic script strings. Accessibility labels (`contentDescription`) use `appString()` — no hardcoded English labels. |
| NFR-6 | All timestamps displayed in IST (Asia/Kolkata). India has one timezone — no per-user timezone configuration needed. Server stores UTC, client converts to IST for display. |
| NFR-7 | Emoji and Unicode characters in translated strings and user-generated content render correctly on all platforms. No character stripping or encoding issues. |
| NFR-8 | Lists containing non-English names (students, schools) sort using locale-aware collation where feasible. Initial release: Unicode code-point sort (acceptable for Indic scripts). Future: CLDR collation. |
| NFR-9 | `ServerStrings` template resolution + placeholder fill completes in < 1ms (in-memory map lookup, no I/O, no AI) |

---

## 4. User Stories

### Parent
- [ ] Select my preferred language from 10 options in settings
- [ ] **Select my language on first app launch before login**
- [ ] See the entire app UI in my selected language instantly
- [ ] Receive notifications (push + WhatsApp) in my language via pre-translated templates
- [ ] See dates formatted in my locale (e.g., "१५ जुलाई" in Hindi)
- [ ] See currency in Indian format (₹1,00,000)
- [ ] **See percentages and scores in my locale digits (८५% not 85%)**
- [ ] **Search for schools using my language's script**
- [ ] Switch back to English anytime

### Teacher
- [ ] Select my preferred language from 10 options
- [ ] See the entire app UI in my selected language
- [ ] Create announcements in English (dynamic content stays English until AI translation F-14 is implemented)

### School Admin
- [ ] Select my preferred language from 10 options
- [ ] See the entire app UI in my selected language
- [ ] View school's medium of instruction
- [ ] **View language distribution across my school's users** (FR-016)
- [ ] **View per-user language preferences** (FR-016)

### Super Admin
- [ ] **View all app users' current language preferences on the website, grouped by role (School Admin, Teacher, Parent) in 3 collapsible dropdowns** (FR-017)
- [ ] **View and update server-level notification template translations (ServerStrings) from the website** (FR-018)
- [ ] **View platform-wide language adoption statistics** (FR-016)

### System
- [ ] Translate notification titles + bodies to recipient's language via `ServerStrings` templates
- [ ] **Track every language preference change** in `language_pref_history` table
- [ ] Fall back to English if template or string resource missing
- [ ] Sync language preference to server

---

## 5. Business Rules

### BR-001
**Rule:** Default language is English.
**Enforcement:** `app_users.languagePref` defaults to 'en' (requires migration from current DB default 'hi'; see §6.9). `LocaleManager` initializes to 'en' if no preference. `ReportAssemblyService.resolveLanguagePref()` falls back to 'en' when user row is not found.

### BR-002
**Rule:** Language switch is instant — no app restart.
**Enforcement:** `LocaleManager.setLocale()` updates `StateFlow` → `CompositionLocal` → recomposition.

### BR-003
**Rule:** Pre-translated templates do not translate proper nouns.
**Enforcement:** `ServerStrings` templates use `{student_name}`, `{school_name}` placeholders. Placeholder values are inserted as-is at runtime — not translated. The template structure around the placeholder is pre-translated.

### BR-004
**Rule:** Fallback to English on missing translation.
**Enforcement:** If `ServerStrings` template or `AppStrings` key is missing for a language, fall back to English. `ServerStrings.get(key, lang)` falls back to `templates["en"]?.get(key)`. `AppStrings.get(key, locale)` falls back to English map. Never show blank or error.

### BR-005
**Rule:** Language preference changes are tracked.
**Enforcement:** Every `PATCH /api/v1/user/language-pref` inserts a row into `language_pref_history` table (user_id, old_lang, new_lang, changed_at). `LanguagePreferenceChanged` event emitted. See §6.2, §20.

### BR-006
**Rule:** WhatsApp templates are pre-approved per language.
**Enforcement:** Meta requires pre-approved templates. Each template has language-specific versions. Template selected based on `languagePref`.

### BR-007
**Rule:** Locale-aware formatting uses Indian numbering system.
**Enforcement:** `CurrencyFormatter` uses Indian numbering (lakhs, crores) for all languages. ₹1,00,000 not $100,000.

### BR-008
**Rule:** `languagePref` synced to server on change.
**Enforcement:** `LocaleManager.setLocale()` calls `PATCH /api/v1/user/language-pref` in background with a **2-second debounce** (rapid switches only sync the final selection). Offline changes queued via DataStore flag `language_pref_pending_sync` — on network restore, a coroutine retry loop flushes the pending preference. See §10.9 for implementation details.

### BR-009
**Rule:** ~~All AI translation calls must pass `containsPii = true`.~~ **Deferred to F-14.** No AI translation in initial release. `ServerStrings` templates are pre-translated and reviewed — no PII sent to AI at runtime. Placeholder values (student names, amounts) are inserted into pre-translated templates locally on the server — no external API call.

### BR-010
**Rule:** Server-side language resolution uses DB lookup for notifications.
**Enforcement:**
- **Notifications:** `Notify.kt` calls `UserLanguageResolver.resolve(userId)` which reads `AppUsersTable.languagePref` (with 10-min Caffeine cache, evicted on preference update). See §9.3 Mechanism A.
- **Content fetches (announcements):** Dynamic content stays English in initial release. `Accept-Language` header mechanism (§9.3 Mechanism B) is deferred to F-14.
- **Cache invalidation:** `PATCH /api/v1/user/language-pref` calls `UserLanguageResolver.evict(userId)` after DB update.
- **Fallback:** Both mechanisms fall back to `'en'` if language is missing, blank, or not in the supported set.

### BR-011
**Rule:** Super Admin language dashboard and ServerStrings manager are Super Admin only.
**Enforcement:** `GET /api/admin/users-by-language`, `GET /api/admin/server-strings`, `PATCH /api/admin/server-strings/{key}`, `DELETE /api/admin/server-strings/{key}` all require JWT auth with `role = 'super_admin'`. Website pages at `/admin/language-dashboard` and `/admin/server-strings` check `session.role === 'super_admin'` — non-super-admin roles see access denied. Nav items are `superAdminOnly: true` in `ADMIN_NAV`.

### BR-012
**Rule:** ServerStrings DB overrides take priority over compiled Kotlin defaults.
**Enforcement:** `ServerStrings.get(key, lang)` checks in-memory override cache (loaded from `server_string_overrides` at startup) first, then falls back to compiled Kotlin map, then English. `PATCH /api/admin/server-strings/{key}` upserts to DB + updates in-memory cache immediately. `DELETE` removes DB override + reverts to compiled default. No server restart needed.

## 6. Database Design

### 6.1 Entity Relationship Summary

Two new tables: `language_pref_history` (tracks every language preference change) and `server_string_overrides` (DB-backed overrides for `ServerStrings` translations, editable by Super Admin). Uses existing `app_users.languagePref` field for current language. `translation_cache` table is deferred to F-14 (AI translation).

### 6.2 New Tables

#### `language_pref_history` table (tracks language preference changes)

```sql
CREATE TABLE language_pref_history (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID NOT NULL,                       -- FK app_users.id
    school_id   UUID,                                 -- denormalized for school-scoped queries
    old_lang    VARCHAR(8),                           -- previous language (NULL on first set)
    new_lang    VARCHAR(8) NOT NULL,                  -- new language
    changed_at  TIMESTAMP NOT NULL DEFAULT now(),
    source      VARCHAR(16) NOT NULL DEFAULT 'app'    -- 'app' | 'first_launch' | 'migration' | 'admin_reset'
);

CREATE INDEX idx_language_pref_history_user ON language_pref_history(user_id, changed_at DESC);
CREATE INDEX idx_language_pref_history_school ON language_pref_history(school_id, changed_at DESC);
```

**Purpose:** Every time a user's `languagePref` changes, a row is inserted. This provides:
- Per-user language change history (audit trail)
- School-wide language switch rate analytics (`switch_rate_7d` = count of rows in last 7 days for a school)
- Platform-wide language adoption trends over time
- Debugging: "when did user X switch to Hindi?"

**`source` field values:**
- `app` — user changed language via in-app language switcher
- `first_launch` — user selected language on first-launch language selection screen (FR-011)
- `migration` — bulk migration (e.g., §6.12 removed language reset, or migration_072 default change)
- `admin_reset` — Super Admin manually reset a user's language (e.g., support case)

#### `server_string_overrides` table (DB-backed ServerStrings overrides — FR-018)

```sql
CREATE TABLE server_string_overrides (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    string_key   VARCHAR(128) NOT NULL,          -- e.g., "notification.fee_reminder.title"
    lang         VARCHAR(8) NOT NULL,             -- en, hi, bn, ta, te, mr, gu, kn, ml, pa
    value        TEXT NOT NULL,                   -- overridden translation text
    updated_by   UUID,                            -- FK app_users.id (Super Admin who made the change)
    updated_at   TIMESTAMP NOT NULL DEFAULT now(),
    UNIQUE(string_key, lang)
);
```

**Purpose:** Allows Super Admin to update server-level notification template translations (`ServerStrings`) from the website without a server redeploy. The compiled Kotlin `ServerStrings` object serves as the default/fallback. At runtime, `ServerStrings.get(key, lang)` checks DB overrides first, then falls back to the compiled Kotlin map.

**Resolution order:**
1. `server_string_overrides` table (DB) — if row exists for `(string_key, lang)`, use `value`
2. Compiled Kotlin `ServerStrings` map (in-memory) — fallback
3. English compiled map — final fallback (BR-004)

**Cache:** `ServerStrings` maintains an in-memory `ConcurrentHashMap<String, ConcurrentHashMap<String, String>>` loaded at startup from `server_string_overrides`. On `PATCH /api/admin/server-strings/{key}`, the cache entry is updated immediately (no restart needed).

#### `translation_cache` table (deferred to F-14 — AI Content Translation)

> **Not implemented in initial release.** No AI translation, no cache needed. `ServerStrings` templates are in-memory and don't require DB caching. This table is documented here for reference and will be created when F-14 is implemented.

```sql
-- FUTURE: Create when AI content translation (F-14) is implemented
CREATE TABLE translation_cache (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_id       UUID,                           -- tenant scope (nullable for platform-wide content)
    content_hash    VARCHAR(64) NOT NULL,          -- SHA-256 hash of original content
    target_lang     VARCHAR(8) NOT NULL,            -- "hi", "bn", etc.
    translated_text TEXT NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    expires_at      TIMESTAMP NOT NULL,
    UNIQUE(school_id, content_hash, target_lang)
);
```

### 6.3 Modified Tables

`app_users.languagePref` — ALTER COLUMN DEFAULT from 'hi' to 'en'. See §6.9 for migration. Existing rows with default 'hi' (not explicitly set by user) must also be updated to 'en' — safe because the multi-language feature is not yet implemented and no user has explicitly chosen a language.

### 6.4 Indexes

- `language_pref_history(user_id, changed_at DESC)` — per-user history queries
- `language_pref_history(school_id, changed_at DESC)` — school-wide analytics (switch rate, trends)
- ~~`translation_cache(school_id, content_hash, target_lang)`~~ — deferred to F-14
- ~~`translation_cache(expires_at)`~~ — deferred to F-14
- ~~`translation_cache(school_id)`~~ — deferred to F-14

### 6.5 Constraints

- `language_pref_history.user_id` — NOT NULL, UUID
- `language_pref_history.new_lang` — NOT NULL, VARCHAR(8)
- `language_pref_history.old_lang` — nullable (NULL on first set)
- `language_pref_history.school_id` — nullable (NULL for users not yet linked to a school)
- `language_pref_history.source` — NOT NULL, VARCHAR(16), default `'app'`
- ~~`translation_cache` constraints~~ — deferred to F-14

### 6.6 Foreign Keys

`language_pref_history.user_id` → `app_users.id` (soft FK — not enforced with a CONSTRAINT to allow insertion even if user is being deleted, but application code should validate user exists).
`language_pref_history.school_id` → `schools.id` (soft FK — nullable, denormalized for query efficiency).

~~`translation_cache.school_id` → `schools.id`~~ — deferred to F-14.

### 6.7 Soft Delete Strategy

N/A — `language_pref_history` is append-only. Rows are never deleted (audit trail). Old history rows can be archived after 1 year if table grows too large.

~~translation cache soft delete~~ — deferred to F-14.

### 6.8 Audit Fields

- `language_pref_history.changed_at` — when the change occurred
- `language_pref_history.old_lang` — previous language (NULL on first set)
- `language_pref_history.new_lang` — new language
- `language_pref_history.source` — what triggered the change

~~translation_cache audit fields~~ — deferred to F-14.

### 6.9 Migration Notes

Migration: `docs/db/migration_071_language_pref_history.sql`
- CREATE `language_pref_history` table + indexes
- No data migration needed (new table, starts empty)

Migration: `docs/db/migration_072_language_pref_default.sql`
- ALTER `app_users.languagePref` column DEFAULT from 'hi' to 'en'
- UPDATE existing rows: set `language_pref = 'en'` WHERE `language_pref = 'hi'` (safe — multi-language feature not yet implemented, no user has explicitly chosen a language; all 'hi' values are from the column default)
- INSERT into `language_pref_history` for each updated row: `(user_id, school_id, old_lang='hi', new_lang='en', source='migration')`
- Update Exposed table definition in `Tables.kt`: change `.default("hi")` to `.default("en")`
- Seed data (`scripts/seed-2026-06-07.sql`, `scripts/seed-expansion-2026-06-09.sql`) already uses 'en' explicitly — no seed changes needed

~~Migration: `docs/db/migration_074_translation_cache.sql`~~ — deferred to F-14.

Migration: `docs/db/migration_073_server_string_overrides.sql`
- CREATE `server_string_overrides` table + `UNIQUE(string_key, lang)` constraint
- No data migration needed (new table, starts empty — compiled defaults are used until Super Admin creates overrides)

### 6.10 Exposed Mappings

```kotlin
object LanguagePrefHistoryTable : UUIDTable("language_pref_history", "id") {
    val userId    = uuid("user_id")
    val schoolId  = uuid("school_id").nullable()  // denormalized for school-scoped queries
    val oldLang   = varchar("old_lang", 8).nullable()  // NULL on first set
    val newLang   = varchar("new_lang", 8)
    val changedAt = timestamp("changed_at")
    val source    = varchar("source", 16).default("app")  // app | first_launch | migration | admin_reset

    init {
        index("idx_lang_pref_history_user", false, userId, changedAt)
        index("idx_lang_pref_history_school", false, schoolId, changedAt)
    }
}
```

Register in `DatabaseFactory.allTables`.

```kotlin
object ServerStringOverridesTable : UUIDTable("server_string_overrides", "id") {
    val stringKey = varchar("string_key", 128)
    val lang      = varchar("lang", 8)
    val value     = text("value")
    val updatedBy = uuid("updated_by").nullable()  // FK app_users.id (Super Admin)
    val updatedAt = timestamp("updated_at")

    init {
        uniqueIndex("uq_string_key_lang", stringKey, lang)
    }
}
```

Register in `DatabaseFactory.allTables`.

~~`TranslationCacheTable`~~ — deferred to F-14.

### 6.11 Seed Data

N/A — `ServerStrings` templates are code-compiled (Kotlin `object`). `language_pref_history` starts empty. `server_string_overrides` starts empty (no overrides at launch — compiled defaults are used). No seed data needed.

### 6.12 Removed Language Strategy

If a language is removed from `SUPPORTED_LANGUAGES` (e.g., a language is deprecated or support is dropped), users with `languagePref` set to that language must be reset to English.

**Server-side migration:**

```sql
-- migration_0NN_removed_lang_reset.sql
-- When a language code is removed from SUPPORTED_LANGUAGES,
-- reset all users with that language to 'en'.

BEGIN;
UPDATE app_users
    SET language_pref = 'en'
    WHERE language_pref = '{removed_lang}';
COMMIT;
```

**Client-side handling:**

```kotlin
// LocaleManager.kt — on app launch, validate stored languagePref
fun initLocale() {
    val stored = preferenceRepository.getLanguagePref() // from DataStore
    if (stored != null && stored !in SUPPORTED_LANGUAGES) {
        // Stored language no longer supported — reset to English
        currentLocale.value = "en"
        preferenceRepository.setLanguagePref("en")
        // Sync reset to server
        syncToServer("en")
    } else {
        currentLocale.value = stored ?: "en"
    }
}
```

**Key points:**
- The `SUPPORTED_LANGUAGES` list is the single source of truth. Both client and server validate against it.
- Server migration is one-way: removed language → `'en'`. No data loss — the user can re-select a supported language in Settings.
- Client validates on every app launch. If the stored DataStore value is not in `SUPPORTED_LANGUAGES`, it resets to `'en'` and syncs.
- `AppStrings.get(key, locale)` already falls back to English if `locale` is not in `maps` — so even without the migration, the UI would render in English. The migration ensures the DB is consistent.
- `UserLanguageResolver.resolve(userId)` also falls back to `'en'` if the DB value is not in `SUPPORTED_LANGUAGES` — so notifications would still work in English.
- **No forced UI prompt** — the user simply sees English. They can change to another supported language in Settings.

---

## 7. State Machines

### Language Preference State Machine

**Note:** The state machine models the **client-side** `LocaleManager` state (DataStore + StateFlow). The DB default is `'hi'` (pre-migration) or `'en'` (post-migration 072). The app default is always `'en'` (BR-001) — `LocaleManager` initializes to `'en'` if DataStore has no stored value, regardless of DB default.

```
                    ┌──────────────────────────────────────────────────────┐
                    │                                                      │
                    ▼                                                      │
  ┌──── en (default) ◀──────────────────────────────────────────────────┐  │
  │         │                                                            │  │
  │   user_selects(lang)                                                 │  │
  │         │                                                            │  │
  │         ▼                                                            │  │
  │    selected(lang)                                                    │  │
  │     │         │                                                      │  │
  │     │ online   │ offline                                             │  │
  │     │ + sync   │                                                     │  │
  │     │ ok       ▼                                                     │  │
  │     │     pending_sync(lang)                                         │  │
  │     │         │         │                                            │  │
  │     │         │ online  │ sync fails                                 │  │
  │     │         │ + sync  │ (HTTP 4xx/5xx)                             │  │
  │     │         │ ok      ▼                                            │  │
  │     │         │     sync_failed(lang)                                │  │
  │     │         │         │                                            │  │
  │     │         │         │ network restored                           │  │
  │     │         │         │ (retries sync)                             │  │
  │     ▼         ▼         │                                            │  │
  │  synced(lang) ◀────────┘                                            │  │
  │     │                                                               │  │
  │     │ user_changes(new_lang)                                        │  │
  │     │ (cancels previous sync job,                                   │  │
  │     │  starts new 2s debounce)                                      │  │
  │     ▼                                                               │  │
  │  selected(new_lang) ─────────────────────────────────────────────────┘  │
  │                                                                         │
  └─────────────────────────────────────────────────────────────────────────┘
```

**Concrete state values:**

| State | `currentLocale` (StateFlow) | `language_pref_pending_sync` (DataStore) | Meaning |
|---|---|---|---|
| `en` (initial) | `"en"` | `false` | No user preference set. App default. Shown on first launch before `LanguageSelectionScreen` (FR-011). |
| `selected(lang)` | `lang` | `false` | User selected a language. 2s debounce timer running. Not yet synced to server. |
| `pending_sync(lang)` | `lang` | `true` | User selected language while offline. Sync will be attempted when network restores. |
| `sync_failed(lang)` | `lang` | `true` | Sync attempted but failed (HTTP 4xx/5xx). Distinct from `pending_sync` — the device was online but the server rejected/errored. Retry on next connectivity change. |
| `synced(lang)` | `lang` | `false` | Server confirmed update (`PATCH` returned 200 + `updatedAt` timestamp). Local preference = server preference. |

**State transitions:**

| Current State | Event | Next State | Guard / Condition |
|---|---|---|---|
| `en` | User selects language | `selected(lang)` | Valid language code (en/hi/bn/ta/te/mr/gu/kn/ml/pa) |
| `selected(lang)` | 2s debounce expires + online + PATCH succeeds | `synced(lang)` | HTTP 200, `updatedAt` received |
| `selected(lang)` | 2s debounce expires + offline | `pending_sync(lang)` | `networkMonitor.isOnline() == false` |
| `selected(lang)` | 2s debounce expires + online + PATCH fails | `sync_failed(lang)` | HTTP 4xx/5xx or network timeout |
| `pending_sync(lang)` | Network restored + PATCH succeeds | `synced(lang)` | `networkMonitor.observe()` emits `true` |
| `pending_sync(lang)` | Network restored + PATCH fails | `sync_failed(lang)` | HTTP 4xx/5xx |
| `sync_failed(lang)` | Network restored (any change) + PATCH succeeds | `synced(lang)` | Retry on next connectivity event |
| `synced(lang)` | User changes language | `selected(new_lang)` | Cancels any pending sync job, starts new 2s debounce |
| Any state | User changes language | `selected(new_lang)` | `syncJob?.cancel()` — always cancels in-flight debounce. Previous sync state is abandoned. |

### Content Translation State Machine (deferred to F-14)

> **Not implemented in initial release.** `ServerStrings` handles all server-side translation via pre-translated templates. Dynamic content stays English. The state machine below is the planned design for when AI translation is added (F-14).

```
original_content ──ai_translates──> translated ──cached──> cached
  │                                    │
  │──already_cached──> cached          │──ai_fails──> fallback_english
  │                                    │──ai_timeout──> fallback_english
  └──target_is_english──> no_translation
```

| Current State | Event | Next State | Guard / Condition |
|---|---|---|---|
| `original_content` | Target lang is English | `no_translation` | `targetLang == 'en'` |
| `original_content` | Check cache | `cached` | Cache hit (content_hash + target_lang) |
| `original_content` | AI translates | `translated` | AI service available |
| `translated` | Cache result | `cached` | Store in translation_cache |
| `original_content` | AI fails | `fallback_english` | AI error or empty response |
| `original_content` | AI timeout (> 3s) | `fallback_english` | Timeout |
| `fallback_english` | None | (terminal) | Use original English content |

---

## 8. Backend Architecture

### 8.1 Component Overview

`ContentTranslationService` (AI-powered translation) is **deferred to F-14**. Initial release uses `ServerStrings` (pre-translated templates) for server-side notification translation. `Notify.kt` is modified to resolve recipient language and use `ServerStrings` templates. A new `language_pref_history` table tracks every language preference change. A new `server_string_overrides` table allows Super Admin to update `ServerStrings` translations from the website without a server redeploy. New API endpoints sync language preference, provide language analytics, and expose Super Admin endpoints for language dashboard + ServerStrings management.

### 8.2 Design Principles

1. **English is default and fallback** — always works, even if a translation is missing
2. **Pre-translated templates, not AI** — `ServerStrings` handles all server-generated messages. No AI calls, no external API dependencies, no latency, no cost. AI translation is F-14 (future).
3. **Translate on server, not client** — client handles UI string resources (`AppStrings`), server handles notification templates (`ServerStrings`)
4. **No proper noun translation** — `ServerStrings` templates use `{student_name}` placeholders. Placeholder values inserted as-is at runtime.
5. **Offline-first language pref** — preference stored locally, synced when online
6. **Track every change** — `language_pref_history` table records every language preference change with old/new language, timestamp, and source

### 8.3 Core Types

#### ContentTranslationService (deferred to F-14 — AI Content Translation)

> **Not implemented in initial release.** The code below is the planned design for when AI translation is added (F-14). Initial release uses `ServerStrings` only.

```kotlin
// FUTURE: server/.../feature/i18n/ContentTranslationService.kt — implement with F-14
object ContentTranslationService {

    private val translationCacheRepository = TranslationCacheRepository

    suspend fun translate(
        content: String,
        targetLang: String,
        schoolId: UUID? = null,
    ): String {
        if (targetLang == "en") return content
        // Check cache (school-scoped to prevent cross-tenant cache sharing)
        val cached = translationCacheRepository.get(content.hash(), targetLang, schoolId)
        if (cached != null) return cached
        // AI translate — AiService is a Kotlin object (singleton), called directly
        val result = AiService.complete(
            feature = "translation",
            lane = AiLane.FAST_CHAT,
            messages = listOf(
                LlmMessage(
                    role = "system",
                    content = "Translate the following text to $targetLang. " +
                        "Maintain tone and context. " +
                        "Do not translate proper nouns (school names, person names). " +
                        "Use gender-neutral phrasing where possible (e.g., \"child\" instead of \"son/daughter\"). " +
                        "Keep it natural and concise.",
                ),
                LlmMessage(role = "user", content = content),
            ),
            containsPii = true,  // notifications/announcements carry PII
            schoolId = schoolId,
            temperature = 0.3,   // lower temperature for faithful translation
            maxTokens = 2048,
        )
        // AiResult.ok → success; .content holds translated text
        // Fallback to original content on any failure
        val translated = if (result.ok) result.content!! else content
        // Cache result (only if translation succeeded — school-scoped)
        if (result.ok) {
            translationCacheRepository.put(content.hash(), targetLang, translated, schoolId, ttlHours = 24)
        }
        return translated
    }

    suspend fun translateNotification(
        title: String,
        body: String,
        userLang: String,
        schoolId: UUID? = null,
    ): Pair<String, String> {
        // Pure function: takes String inputs, returns Pair<String, String>.
        // Does NOT mutate a Notification entity or DTO.
        // Caller (Notify.kt) uses the returned pair to construct the notification.
        if (userLang == "en") return title to body
        val translatedTitle = translate(title, userLang, schoolId)
        val translatedBody = translate(body, userLang, schoolId)
        return translatedTitle to translatedBody
    }
}
```

#### ServerStrings — Server-Side Pre-Translated Templates

Many server-generated messages are **templated** — the title/body follows a known pattern with only variable placeholders (student name, date, amount). These do NOT need AI translation. The server maintains pre-translated string maps, same approach as the client's `AppStrings`.

```kotlin
// server/.../feature/i18n/ServerStrings.kt

object ServerStrings {

    // Compiled Kotlin defaults — shipped with server code
    private val templates = mapOf(
        "en" to mapOf(
            "notification.fee_reminder.title" to "Fee Reminder",
            "notification.fee_reminder.body" to "Fee of ₹{amount} is due for {student_name} on {due_date}.",
            "notification.attendance_alert.title" to "Attendance Alert",
            "notification.attendance_alert.body" to "{student_name} was absent on {date}.",
            "notification.announcement.title" to "New Announcement",
            "notification.announcement.body" to "{school_name} published a new announcement.",
            "notification.link_approved.title" to "Child Link Approved",
            "notification.link_approved.body" to "Your request to link with {student_name} has been approved.",
            "notification.link_rejected.title" to "Child Link Request Declined",
            "notification.link_rejected.body" to "Your request to link with {student_name} has been declined.",
            "notification.exam_reminder.title" to "Exam Reminder",
            "notification.exam_reminder.body" to "{exam_name} for {student_name} starts on {date}.",
        ),
        "hi" to mapOf(
            "notification.fee_reminder.title" to "फीस रिमाइंडर",
            "notification.fee_reminder.body" to "{student_name} के लिए ₹{amount} की फीस {due_date} को देय है।",
            "notification.attendance_alert.title" to "उपस्थिति अलर्ट",
            "notification.attendance_alert.body" to "{student_name} {date} को अनुपस्थित थे।",
            "notification.announcement.title" to "नई घोषणा",
            "notification.announcement.body" to "{school_name} ने एक नई घोषणा प्रकाशित की है।",
            "notification.link_approved.title" to "बाल लिंक स्वीकृत",
            "notification.link_approved.body" to "{student_name} के साथ लिंक करने का आपका अनुरोध स्वीकृत हो गया है।",
            "notification.link_rejected.title" to "बाल लिंक अनुरोध अस्वीकृत",
            "notification.link_rejected.body" to "{student_name} के साथ लिंक करने का आपका अनुरोध अस्वीकृत हो गया है।",
            "notification.exam_reminder.title" to "परीक्षा रिमाइंडर",
            "notification.exam_reminder.body" to "{student_name} की {exam_name} {date} से शुरू होगी।",
        ),
        // ... bn, ta, te, mr, gu, kn, ml, pa — same keys, translated values
    )

    // DB-backed overrides — loaded at startup from server_string_overrides table
    // Updated at runtime via ServerStringOverrideRepository (no restart needed)
    private val overrides: ConcurrentHashMap<String, ConcurrentHashMap<String, String>> = ConcurrentHashMap()

    /**
     * Set a DB override for (key, lang). Called by ServerStringOverrideRepository.
     */
    fun setOverride(key: String, lang: String, value: String) {
        overrides.computeIfAbsent(key) { ConcurrentHashMap() }[lang] = value
    }

    /**
     * Remove a DB override for (key, lang). Reverts to compiled default.
     * Called by ServerStringOverrideRepository.
     */
    fun removeOverride(key: String, lang: String) {
        overrides[key]?.remove(lang)
    }

    /**
     * Resolve a template key for a given language.
     * Resolution order: DB override → compiled default → English compiled → key itself.
     */
    fun get(key: String, lang: String): String {
        return overrides[key]?.get(lang)
            ?: templates[lang]?.get(key)
            ?: templates["en"]?.get(key)
            ?: key  // key itself as last resort (indicates a bug)
    }

    /**
     * Resolve a template key and fill in placeholders.
     * Placeholders use {name} syntax: "Fee of ₹{amount} is due for {student_name}"
     *
     * Usage:
     *   ServerStrings.fill("notification.fee_reminder.body", "hi",
     *       mapOf("amount" to "5000", "student_name" to "आर्य", "due_date" to "15 जुलाई"))
     *   → "आर्य के लिए ₹5000 की फीस 15 जुलाई को देय है।"
     */
    fun fill(key: String, lang: String, params: Map<String, String>): String {
        val template = get(key, lang)
        var result = template
        params.forEach { (k, v) ->
            result = result.replace("{$k}", v)
        }
        return result
    }

    /**
     * Check if a notification type has a pre-translated template.
     * Used by Notify.kt to decide: template lookup vs AI translation.
     */
    fun hasTemplate(notificationType: String, part: String = "body"): Boolean {
        val key = "notification.${notificationType}.${part}"
        return templates["en"]?.containsKey(key) == true
    }
}
```

**Notification translation flow in `Notify.kt` (initial release — no AI):**

```kotlin
// Notify.kt — modified notification dispatch

suspend fun sendNotification(
    userId: UUID,
    notificationType: String,  // "fee_reminder", "attendance_alert", etc.
    titleParams: Map<String, String>,
    bodyParams: Map<String, String>,
    dynamicBody: String? = null,  // if content is dynamic (not templated) — stays English in initial release
    schoolId: UUID? = null,
) {
    val userLang = UserLanguageResolver.resolve(userId)

    // 1. Title — always templated, use ServerStrings
    val title = ServerStrings.fill("notification.${notificationType}.title", userLang, titleParams)

    // 2. Body — templated or dynamic?
    val body = if (dynamicBody != null) {
        // Dynamic content (e.g., custom announcement text) — stays English in initial release
        // AI translation of dynamic content is F-14 (future)
        dynamicBody
    } else {
        // Templated body → use ServerStrings
        ServerStrings.fill("notification.${notificationType}.body", userLang, bodyParams)
    }

    // 3. Send via FCM
    NotificationService.send(userId, title, body)
}
```

**When to use `ServerStrings` vs English (initial release):**

| Content Type | Source | Translation Method | Example |
|---|---|---|---|
| Notification titles | `ServerStrings` | Pre-translated template | "Fee Reminder" → "फीस रिमाइंडर" |
| Notification bodies (templated) | `ServerStrings` | Pre-translated template + placeholder fill | "Fee of ₹{amount} due for {student_name}" |
| Announcement content | DB (user-authored) | **English only** (AI translation deferred to F-14) | Custom text written by school admin |
| Notification bodies (dynamic) | DB (user-authored) | **English only** (AI translation deferred to F-14) | Custom message from teacher to parent |
| WhatsApp templates | Meta (pre-approved) | Template ID lookup | Already handled by WhatsApp gateway |
| Error messages | `ServerStrings` | Pre-translated template | "Invalid language code" |
| System messages | `ServerStrings` | Pre-translated template | "Scheduled maintenance" |

**Key points:**
- `ServerStrings` follows the same Kotlin `object` singleton pattern as `ContentTranslationService`, `Notify`, `AiService`.
- Same key naming convention as client `AppStrings`: `{domain}.{type}.{variant}` (e.g., `notification.fee_reminder.body`).
- Placeholder syntax: `{variable_name}` — filled at runtime via `ServerStrings.fill()`. Variable values themselves may be locale-formatted (e.g., dates via `DateFormatter`, currency via `NumberFormatter`).
- Falls back to English if a language map or key is missing — same fallback chain as client `AppStrings.get()`.
- **No AI cost** for templated notifications. Dynamic content stays English in initial release. AI translation is F-14 (future).
- **Translation quality:** `ServerStrings` entries go through the same human review process as client strings (§10.16). They are reviewed before release, not AI-generated at runtime.
- **Adding new templates:** Add the key + 10 translations to `ServerStrings.kt` → deploy server. No app release needed (server-only change).
- **DB overrides (FR-018):** Super Admin can update any `ServerStrings` translation from the website via `PATCH /api/admin/server-strings/{key}`. Overrides are stored in `server_string_overrides` table and loaded into an in-memory `ConcurrentHashMap` at startup. `ServerStrings.get(key, lang)` checks the override cache first, then falls back to the compiled Kotlin map, then English. No server restart needed — cache is updated on every PATCH/DELETE.

**Server singleton pattern:** The server module does **not** use Koin. All cross-cutting services are Kotlin `object` singletons — `Notify`, `AiService`, `GuardrailService`, `NotifyRecipients` all follow this pattern. `ServerStrings` and `UserLanguageResolver` follow the same pattern. ~~`ContentTranslationService` and `TranslationCacheRepository`~~ are deferred to F-14. Dependencies are created directly as private fields, not injected. This matches `Notify.kt:34-35` which creates `DeviceTokenRepository()` and `NotificationService(...)` directly.

**Key API notes:**
- `ServerStrings` is a Kotlin `object` (singleton) — call `ServerStrings.get(key, lang)` or `ServerStrings.fill(key, lang, params)` directly. No injection. Checks `server_string_overrides` in-memory cache first (loaded at startup from DB), then compiled Kotlin map, then English fallback.
- `ServerStringOverrideRepository` is a Kotlin `object` (singleton) — CRUD for `server_string_overrides` table. Updates `ServerStrings` in-memory override cache on upsert/delete. See §8.4.
- `UserLanguageResolver` is a Kotlin `object` (singleton) — call `UserLanguageResolver.resolve(userId)` directly.
- ~~`ContentTranslationService`~~ — deferred to F-14.
- ~~`TranslationCacheRepository`~~ — deferred to F-14.
- ~~`AiService.complete()` for translation~~ — deferred to F-14.

### 8.4 Repositories

~~`TranslationCacheRepository`~~ — deferred to F-14. No cache table in initial release.

**`LanguagePrefHistoryRepository`** (new — tracks language changes):

```kotlin
object LanguagePrefHistoryRepository {

    suspend fun record(
        userId: UUID,
        schoolId: UUID?,
        oldLang: String?,
        newLang: String,
        source: String = "app",
    ) {
        dbQuery {
            LanguagePrefHistoryTable.insert {
                it[LanguagePrefHistoryTable.userId] = userId
                it[LanguagePrefHistoryTable.schoolId] = schoolId
                it[LanguagePrefHistoryTable.oldLang] = oldLang
                it[LanguagePrefHistoryTable.newLang] = newLang
                it[LanguagePrefHistoryTable.changedAt] = DateTime.nowUTC()
                it[LanguagePrefHistoryTable.source] = source
            }
        }
    }

    suspend fun getUserHistory(userId: UUID, limit: Int = 50): List<LanguagePrefHistoryEntry> {
        return dbQuery {
            LanguagePrefHistoryTable
                .select { LanguagePrefHistoryTable.userId eq userId }
                .orderBy(LanguagePrefHistoryTable.changedAt, SortOrder.DESC)
                .limit(limit)
                .map { it.toLanguagePrefHistoryEntry() }
        }
    }

    suspend fun getSchoolSwitchCount(schoolId: UUID, days: Int = 7): Int {
        val cutoff = DateTime.nowUTC().minusDays(days)
        return dbQuery {
            LanguagePrefHistoryTable
                .select {
                    (LanguagePrefHistoryTable.schoolId eq schoolId) and
                    (LanguagePrefHistoryTable.changedAt greaterEq cutoff) and
                    (LanguagePrefHistoryTable.source neq "migration")
                }
                .count().toInt()
        }
    }
}
```

Called from `PATCH /api/v1/user/language-pref` endpoint after updating `app_users.languagePref`.

**`ServerStringOverrideRepository`** (new — CRUD for ServerStrings DB overrides, FR-018):

```kotlin
object ServerStringOverrideRepository {

    /**
     * Load all overrides into ServerStrings in-memory cache at startup.
     * Called once during DatabaseFactory initialization.
     */
    suspend fun loadAllIntoCache() {
        dbQuery {
            ServerStringOverridesTable.selectAll().forEach { row ->
                ServerStrings.setOverride(
                    row[ServerStringOverridesTable.stringKey],
                    row[ServerStringOverridesTable.lang],
                    row[ServerStringOverridesTable.value],
                )
            }
        }
    }

    suspend fun upsert(key: String, lang: String, value: String, updatedBy: UUID) {
        dbQuery {
            ServerStringOverridesTable.upsert(
                ServerStringOverridesTable.stringKey to key,
                ServerStringOverridesTable.lang to lang,
            ) {
                it[ServerStringOverridesTable.value] = value
                it[ServerStringOverridesTable.updatedBy] = updatedBy
                it[ServerStringOverridesTable.updatedAt] = DateTime.nowUTC()
            }
        }
        ServerStrings.setOverride(key, lang, value)
    }

    suspend fun delete(key: String, lang: String): Boolean {
        val deleted = dbQuery {
            ServerStringOverridesTable.deleteWhere {
                (ServerStringOverridesTable.stringKey eq key) and
                (ServerStringOverridesTable.lang eq lang)
            } > 0
        }
        if (deleted) {
            ServerStrings.removeOverride(key, lang)
        }
        return deleted
    }

    suspend fun getAll(): List<ServerStringOverrideEntry> {
        return dbQuery {
            ServerStringOverridesTable.selectAll()
                .map { it.toServerStringOverrideEntry() }
        }
    }
}
```

Called from `I18nAnalyticsRouting` Super Admin endpoints (`PATCH`/`DELETE /api/admin/server-strings/{key}`). `loadAllIntoCache()` is called once at server startup from `DatabaseFactory.init()`.

~~`TranslationCacheRepository` (deferred to F-14):~~

```kotlin
// FUTURE: server/.../feature/i18n/TranslationCacheRepository.kt — implement with F-14
// object TranslationCacheRepository {
//     suspend fun get(contentHash, targetLang, schoolId): String? = ...
//     suspend fun put(contentHash, targetLang, translatedText, schoolId, ttlHours) = ...
//     suspend fun invalidateByContentHash(contentHash, schoolId) = ...
//     suspend fun invalidateBySchool(schoolId) = ...
// }
```

**Pattern:** `object` singleton — same as `ServerStrings`, `Notify`, `UserLanguageResolver`. No Koin injection. Called directly.

~~**Content update invalidation:**~~ Deferred to F-14. No translation cache in initial release — `ServerStrings` is static and doesn't need invalidation.

### 8.5 Mappers

N/A — `language_pref_history` and `server_string_overrides` store and retrieve structured data directly. `ServerStrings` is in-memory with override cache loaded from DB.

### 8.6 Permission Checks

- Language preference: user can only update own preference
- Translation: system-internal, no user-facing permission
- **Super Admin only endpoints:** `GET /api/admin/users-by-language` (FR-017), `GET /api/admin/server-strings`, `PATCH /api/admin/server-strings/{key}`, `DELETE /api/admin/server-strings/{key}` (FR-018) — all require `role = 'super_admin'` in JWT. Non-super-admin roles receive 403 Forbidden. See BR-011.

### 8.7 Background Jobs

~~**Translation Cache Cleanup**~~ — deferred to F-14. No cache table in initial release.

**Language Pref History Archival** (optional, low priority):
1. Query `language_pref_history` where `changed_at < now() - 1 year`
2. Archive to cold storage or delete (audit trail retention policy)
3. Return count archived

### 8.8 Domain Events

- `LanguagePreferenceChanged` — emitted when user changes language preference. Payload: `userId, oldLang, newLang, source`. Also triggers `LanguagePrefHistoryRepository.record()`.
- `ServerStringOverrideUpdated` — emitted when Super Admin updates a ServerStrings translation. Payload: `key, lang, oldValue, newValue, updatedBy`. Triggers `ServerStringOverrideRepository` upsert + in-memory cache update.
- `ServerStringOverrideDeleted` — emitted when Super Admin removes an override. Payload: `key, lang, deletedBy`. Triggers in-memory cache revert to compiled default.
- ~~`ContentTranslated`~~ — deferred to F-14
- ~~`TranslationFailed`~~ — deferred to F-14
- ~~`TranslationCacheHit`~~ — deferred to F-14
- ~~`ContentUpdated`~~ — deferred to F-14 (no cache to invalidate)

### 8.9 Caching

- ~~**Translation cache:**~~ deferred to F-14.
- **User language preference (notifications):** in-memory Caffeine cache in `UserLanguageResolver` (Kotlin `object` singleton), keyed by `userId`, 10-minute TTL. Evicted on `PATCH /api/v1/user/language-pref`. See §9.3 Mechanism A.
- ~~**User language preference (content fetches):**~~ deferred to F-14 (no `Accept-Language` content translation in initial release).
- **ServerStrings overrides:** in-memory `ConcurrentHashMap<String, ConcurrentHashMap<String, String>>` in `ServerStrings` (Kotlin `object` singleton), keyed by `string_key` → `lang` → `value`. Loaded at startup from `server_string_overrides` table via `ServerStringOverrideRepository.loadAllIntoCache()`. Updated immediately on `PATCH`/`DELETE` — no restart needed. See §8.4, BR-012.
- **String resources (client):** loaded once per language, cached in memory (`AppStrings` object, app lifetime).
- **String templates (server):** `ServerStrings` object — loaded once at server startup, in-memory for process lifetime. No TTL (static, code-compiled). Updated on server deploy.

### 8.10 Transactions

- Language preference update: single transaction (update `app_users.languagePref` + insert `language_pref_history` row)
- ServerString override upsert: single transaction (upsert `server_string_overrides` row) + in-memory cache update after commit
- ServerString override delete: single transaction (delete `server_string_overrides` row) + in-memory cache revert after commit
- ~~Translation cache store~~ — deferred to F-14
- Notification translation: no transaction (`ServerStrings` is in-memory lookup, no DB write)

### 8.11 Rate Limiting

- ~~AI translation~~ — deferred to F-14
- Language preference update: standard API rate limiting
- No rate limiting needed for `ServerStrings` (in-memory lookup, no external calls)

### 8.12 Configuration

- `MULTI_LANGUAGE_ENABLED` — default `true`; enable/disable feature
- ~~`TRANSLATION_CACHE_TTL_HOURS`~~ — deferred to F-14
- ~~`AI_TRANSLATION_RATE_LIMIT_PER_SCHOOL_PER_HOUR`~~ — deferred to F-14
- `STRING_EXTERNALIZATION_ENABLED` — default `true`; rollback flag for client string externalization (§10.3)
- `SUPPORTED_LANGUAGES` — hardcoded list: `en, hi, bn, ta, te, mr, gu, kn, ml, pa` (single source of truth, shared by client and server)

---

## 9. API Contracts

### 9.1 Language Preference

```
GET /api/v1/user/language-pref
  Auth: JWT (requireAuth)
  → 200: { "language": "hi", "updated_at": "2026-07-04T01:06:00Z" }
  → 401: Unauthorized

PATCH /api/v1/user/language-pref
  Auth: JWT (requireAuth)
  Body: { "language": "hi" }
  → 200: { "language": "hi", "updated_at": "2026-07-04T01:06:00Z" }
  → 400: Invalid language code
  → 401: Unauthorized
```

**Login response includes `languagePref`:** The existing `AuthTokenResponse` (returned by `POST /auth/login`) is modified to include `languagePref` so the client gets the user's language on login without a separate request. This is the primary mechanism for initial language sync on login.

```kotlin
// server/.../feature/auth/AuthRouting.kt — modified AuthTokenResponse
@Serializable
data class AuthTokenResponse(
    val token: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("user_id") val userId: String,
    val name: String,
    val role: String,
    @SerialName("profile_completed") val profileCompleted: Boolean,
    @SerialName("must_change_password") val mustChangePassword: Boolean = false,
    @SerialName("language_pref") val languagePref: String = "en",  // NEW — from AppUsersTable.languagePref
)
```

**When each endpoint is used:**
- **Login (`POST /auth/login`):** Client receives `languagePref` in `AuthTokenResponse` → stores in DataStore via `LocaleManager`. This is the primary path — no extra network call needed.
- **GET `/api/v1/user/language-pref`:** Used on app launch when the user has a valid session but DataStore is empty (e.g., app reinstall, cleared cache). The client calls this to re-sync the stored preference.
- **PATCH `/api/v1/user/language-pref`:** Used when the user changes language via the in-app language switcher. Updates the DB, evicts `UserLanguageResolver` cache, and the client updates DataStore locally.

No other new endpoints — notification translation happens server-side via `ServerStrings` templates. See §9.3 for how the server determines the user's language for notification translation.

### 9.2 DTO Models

All `@Serializable`, wrapped in `ApiResponse<T>` pattern.

```kotlin
@Serializable data class LanguagePrefRequest(
    val language: String,  // en | hi | bn | ta | te | mr | gu | kn | ml | pa
)

@Serializable data class LanguagePrefResponse(
    val language: String,
    @SerialName("updated_at") val updatedAt: String,  // ISO 8601 timestamp — confirms server received and stored the update
)
```

**Note:** `AuthTokenResponse` (in `AuthRouting.kt`) is also modified to include `languagePref: String` — see §9.1 for the full DTO. This is not a new DTO; it's an additive field on the existing login response.

### 9.3 Server-Side Language Resolution

The server determines the user's language for content translation via two distinct mechanisms, depending on the content type:

#### Mechanism A: Notifications (push, WhatsApp, in-app) — DB lookup per recipient

Notifications are sent by the server to recipients without a client request. The server reads the recipient's `languagePref` directly from `AppUsersTable`.

```kotlin
// server/.../feature/i18n/UserLanguageResolver.kt

/**
 * Resolves a user's language preference for server-side translation.
 * Reads from AppUsersTable.languagePref with an in-memory cache (10-minute TTL)
 * to avoid a DB hit on every notification send.
 *
 * Cache: Caffeine cache keyed by userId (UUID), value is language code (String).
 * Server singleton pattern (object) — same as Notify, AiService, GuardrailService.
 * The server module does not use Koin.
 * Invalidated on PATCH /api/v1/user/language-pref (the endpoint evicts the
 * user's entry from the cache after updating the DB).
 */
object UserLanguageResolver {
    // Caffeine cache: userId -> language code, 10-minute TTL
    private val cache = Caffeine.newBuilder()
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .maximumSize(10_000)
        .build<UUID, String>()

    /**
     * Resolve user's language preference. Returns 'en' as fallback.
     * Checks cache first, then DB, then falls back to 'en'.
     */
    suspend fun resolve(userId: UUID): String {
        cache.getIfPresent(userId)?.let { return it }
        val lang = dbQuery {
            AppUsersTable.select(AppUsersTable.languagePref)
                .where { AppUsersTable.id eq userId }
                .singleOrNull()
                ?.get(AppUsersTable.languagePref)
        } ?: "en"
        val resolved = lang?.takeIf { it.isNotBlank() } ?: "en"
        cache.put(userId, resolved)
        return resolved
    }

    /** Evict a user's cached language after they update their preference. */
    fun evict(userId: UUID) {
        cache.invalidate(userId)
    }
}
```

**Pattern:** Kotlin `object` singleton — same as `Notify`, `ServerStrings`, `GuardrailService`. The server module does not use Koin. Called directly as `UserLanguageResolver.resolve(userId)`.

**Cache invalidation:** `PATCH /api/v1/user/language-pref` calls `UserLanguageResolver.evict(userId)` after updating the DB row, so the next notification send picks up the new language immediately.

#### Mechanism B: Content fetches (announcements, etc.) — Accept-Language header (deferred to F-14)

> **Not implemented in initial release.** Dynamic content (announcements) stays English. The `Accept-Language` header mechanism will be implemented when AI content translation (F-14) is added. The design below is for reference.

For client-initiated content fetches (e.g., `GET /api/v1/school/announcements`), the client would send an `Accept-Language` HTTP header with the user's `languagePref` value. The server would read this header to determine the target language for AI translation.

```kotlin
// FUTURE: Client side (shared/.../core/network/buildRefreshClient.kt)
// Add Accept-Language header to all API requests — implement with F-14
// install(DefaultRequestHeaders) {
//     Accept-Language { localeManager.currentLocale.value }
// }

// FUTURE: Server side (server/.../Application.kt or a helper)
// fun ApplicationCall.acceptedLanguage(): String {
//     val header = request.headers["Accept-Language"]
//     val supported = setOf("en", "hi", "bn", "ta", "te", "mr", "gu", "kn", "ml", "pa")
//     return header?.takeIf { it in supported } ?: "en"
// }
```

~~**Why two mechanisms?**~~
- ~~Notifications are server-initiated (no client request) — the server must look up the recipient's language from the DB.~~
- ~~Content fetches are client-initiated — the client already knows the user's language and can send it via header, avoiding a DB lookup per request.~~

~~**Why not `?lang=` query param?**~~ Deferred to F-14.

~~**Endpoints that translate content via Accept-Language:**~~ Deferred to F-14.

**Endpoints that do NOT use Accept-Language (initial release):**
- All endpoints — no content translation in initial release. Dynamic content stays English.
- `PATCH /api/v1/user/language-pref` — writes preference
- Auth endpoints — no translatable content
- Analytics/data endpoints — return structured data

#### Analytics & Tracking Endpoints

```
GET /api/v1/school/language-distribution
  Auth: JWT (requireAuth, School Admin)
  → 200: {
    "distribution": [
      { "language": "en", "count": 450, "percentage": 0.65 },
      { "language": "hi", "count": 120, "percentage": 0.17 },
      { "language": "ta", "count": 68, "percentage": 0.10 },
      ...
    ],
    "total_users": 690,
    "most_popular": "en",
    "switch_rate_7d": 12
  }
  → 401: Unauthorized
  → 403: Forbidden (non-School Admin)

GET /api/v1/school/users-language-pref
  Auth: JWT (requireAuth, School Admin)
  → 200: {
    "users": [
      { "user_id": "uuid", "name": "Rajesh Kumar", "language": "hi", "last_changed": "2026-07-01T10:30:00Z" },
      { "user_id": "uuid", "name": "Priya Sharma", "language": "en", "last_changed": null },
      ...
    ],
    "total": 690
  }
  → 401: Unauthorized
  → 403: Forbidden (non-School Admin)

GET /api/v1/user/language-history
  Auth: JWT (requireAuth, own profile only)
  → 200: {
    "history": [
      { "old_lang": null, "new_lang": "en", "changed_at": "2026-06-15T08:00:00Z", "source": "first_launch" },
      { "old_lang": "en", "new_lang": "hi", "changed_at": "2026-07-01T10:30:00Z", "source": "app" },
      ...
    ]
  }
  → 401: Unauthorized

GET /api/admin/language-adoption
  Auth: JWT (requireAuth, Super Admin only)
  → 200: {
    "platform_distribution": [
      { "language": "en", "count": 12500, "percentage": 0.72 },
      { "language": "hi", "count": 3200, "percentage": 0.18 },
      ...
    ],
    "total_users": 17400,
    "switch_rate_7d": 145,
    "by_school": [
      { "school_id": "uuid", "school_name": "Delhi Public School", "most_popular": "hi", "total_users": 690 },
      ...
    ]
  }
  → 401: Unauthorized
  → 403: Forbidden (non-Super Admin)
```

~~`GET /api/admin/translation-cache/stats`~~ — deferred to F-14.

```
GET /api/admin/users-by-language
  Auth: JWT (requireAuth, Super Admin only)
  Query: ?role=school_admin|teacher|parent  (optional — filter by role; omit for all)
  → 200: {
    "groups": {
      "school_admin": [
        { "user_id": "uuid", "name": "Rajesh Kumar", "phone": "+91...", "school_name": "Delhi Public School", "language": "hi", "last_changed": "2026-07-01T10:30:00Z" },
        ...
      ],
      "teacher": [
        { "user_id": "uuid", "name": "Priya Sharma", "phone": "+91...", "school_name": "Delhi Public School", "language": "en", "last_changed": null },
        ...
      ],
      "parent": [
        { "user_id": "uuid", "name": "Amit Verma", "phone": "+91...", "school_name": "Delhi Public School", "language": "ta", "last_changed": "2026-06-20T14:00:00Z" },
        ...
      ]
    },
    "counts": { "school_admin": 45, "teacher": 320, "parent": 17035 },
    "total": 17400
  }
  → 401: Unauthorized
  → 403: Forbidden (non-Super Admin)

GET /api/admin/server-strings
  Auth: JWT (requireAuth, Super Admin only)
  → 200: {
    "strings": [
      {
        "key": "notification.fee_reminder.title",
        "translations": {
          "en": { "value": "Fee Reminder", "is_override": false, "updated_by": null, "updated_at": null },
          "hi": { "value": "फीस रिमाइंडर", "is_override": true, "updated_by": "uuid", "updated_at": "2026-07-03T12:00:00Z" },
          "bn": { "value": "ফি রিমাইন্ডার", "is_override": false, "updated_by": null, "updated_at": null },
          ...
        }
      },
      ...
    ],
    "total_keys": 24,
    "languages": ["en", "hi", "bn", "ta", "te", "mr", "gu", "kn", "ml", "pa"]
  }
  → 401: Unauthorized
  → 403: Forbidden (non-Super Admin)

PATCH /api/admin/server-strings/{key}
  Auth: JWT (requireAuth, Super Admin only)
  Body: { "lang": "hi", "value": "फीस रिमाइंडर — अपडेटेड" }
  → 200: { "key": "notification.fee_reminder.title", "lang": "hi", "value": "फीस रिमाइंडर — अपडेटेड", "updated_at": "2026-07-03T12:05:00Z" }
  → 400: Invalid key or language code
  → 401: Unauthorized
  → 403: Forbidden (non-Super Admin)

DELETE /api/admin/server-strings/{key}
  Auth: JWT (requireAuth, Super Admin only)
  Query: ?lang=hi
  → 200: { "deleted": true, "key": "notification.fee_reminder.title", "lang": "hi" }
  → 404: Override not found
  → 401: Unauthorized
  → 403: Forbidden (non-Super Admin)
```

**Notes:**
- Language distribution endpoint is School Admin only (scoped to the admin's `school_id` from JWT). Queries `app_users.languagePref` grouped by language.
- Users language pref endpoint is School Admin only — returns per-user language for the admin's school. Useful for admin dashboard and support.
- Language history endpoint is user-scoped — users can view their own language change history.
- Platform language adoption endpoint is Super Admin only — platform-wide language statistics.
- `switch_rate_7d` = count of `language_pref_history` rows in the last 7 days for this school (excluding `source='migration'`).
- `GET /api/admin/users-by-language` (FR-017) — Super Admin only. Returns all app users grouped by role with their `languagePref`, school name, and last changed timestamp. `last_changed` is the most recent `language_pref_history.changed_at` for that user (or `null` if never changed). Supports optional `?role=` filter.
- `GET /api/admin/server-strings` (FR-018) — Super Admin only. Returns all ServerStrings keys × all 10 languages. Each translation indicates whether it's a compiled default (`is_override: false`) or a DB override (`is_override: true`). The website renders this as an editable table.
- `PATCH /api/admin/server-strings/{key}` (FR-018) — Super Admin only. Upserts a DB override for `(string_key, lang)`. Updates the in-memory `ServerStrings` override cache immediately — next notification uses the new value. `updated_by` set from JWT.
- `DELETE /api/admin/server-strings/{key}?lang=hi` (FR-018) — Super Admin only. Removes a DB override, reverting to the compiled Kotlin default. Returns 404 if no override exists for that key+lang.
- These endpoints return structured JSON — no translation needed.

---

## 10. Frontend Architecture

### 10.1 Screens

No generic `SettingsScreen.kt` exists. Each role has its own settings/profile screen — the language switcher is added to each role's existing screen, next to the existing `VThemePicker` (theme switcher).

| Screen | Platform | Role | Description |
|---|---|---|---|
| `SchoolSettingsScreenV2.kt` (modified) | Compose | School Admin | Add language switcher below existing `VThemePicker` in the "Settings" tab. File: `composeApp/.../ui/v2/screens/school/SchoolSettingsScreenV2.kt` |
| `TeacherProfileScreenV2.kt` (modified) | Compose | Teacher | Add language switcher below existing `VThemePicker` in the "Profile" tab. File: `composeApp/.../ui/v2/screens/teacher/TeacherProfileScreenV2.kt` |
| `ParentProfileCardScreenV2.kt` (modified) | Compose | Parent | Add language switcher in the account-options reveal (swipe-down section with logout / link child / discover schools). File: `composeApp/.../ui/v2/screens/parent/ParentProfileCardScreenV2.kt` |

**Rationale:** Each portal already has a `VThemePicker` for theme switching (Warm/Light/Night). The language switcher follows the same pattern — a `VLanguagePicker` composable placed adjacent to `VThemePicker` in each role's settings/profile screen. This avoids creating a new shared screen and respects the existing per-portal IA.

### 10.2 Navigation

- **Admin:** Settings tab (5th bottom nav tab) → scroll to Language section → language selection dialog
- **Teacher:** Profile tab (4th bottom nav tab) → scroll to Language section → language selection dialog
- **Parent:** Profile tab (5th bottom nav tab) → swipe down account options → Language section → language selection dialog

### 10.3 UX Flows

#### User: Change Language
1. User opens their role's Settings/Profile tab
2. Scrolls to the Language section (below the Appearance/Theme section)
3. Taps "Language"
4. Sees list of 10 languages with native names (e.g., "हिन्दी", "বাংলা", "தமிழ்")
5. Selects language
6. UI instantly switches to selected language (StateFlow recomposition via `LocalLocale`)
7. Preference synced to server in background (`PATCH /api/v1/user/language-pref`)

### 10.4 State Management

```kotlin
class LocaleManager {
    val currentLocale: StateFlow<String>  // "en", "hi", etc.

    fun setLocale(lang: String) {
        // 1. Update StateFlow (triggers recomposition)
        // 2. Persist to DataStore
        // 3. Debounce 2s, then sync to server: PATCH /api/v1/user/language-pref
        //    If offline, set language_pref_pending_sync=true in DataStore.
        //    On network restore, retry sync. See §10.9 for full implementation.
    }
}

// Compose provider
@Composable
fun AppRoot() {
    val locale by localeManager.currentLocale.collectAsState()
    CompositionLocalProvider(LocalLocale provides locale) {
        VidyaPrayagTheme { AppContent() }
    }
}
```

### 10.5 String Resources

**Approach: Kotlin string maps (not Android XML resources)**

Compose Multiplatform (CMP) does have a resources API (`org.jetbrains.compose.resources`), and the codebase already uses it for drawables and fonts in `composeApp/src/commonMain/composeResources/`. However, CMP's `stringResource(Res.string.key)` resolves based on the **system locale**, not an in-app user preference. For an in-app language switcher with instant switching (no restart, no system locale change), we use **Kotlin string maps** — a `Map<String, String>` per language, resolved at runtime via `LocaleManager.currentLocale`.

This approach:
- Works on all CMP targets (Android, iOS, JVM, Web/Wasm, JS) with no platform-specific code
- Integrates directly with `LocaleManager` + `CompositionLocalProvider(LocalLocale)` — language switch is instant via StateFlow recomposition
- No dependency on system locale or platform resource frameworks
- No XML parsing, no generated accessor compilation issues
- Fallback to English if a key is missing in the selected language
- **Compile-time resolution:** String maps are Kotlin code (`object AppStrings`), compiled into the binary. No runtime file parsing, no XML loading, no I/O. All strings are available immediately on app launch.

**Note:** The existing `composeApp/src/commonMain/composeResources/values/strings.xml` file (landing page strings) remains as-is for CMP's system-locale-based resolution. The multi-language feature uses a separate string map system. If desired, the landing page strings can be migrated to the new system in a follow-up.

**Note on `strings_en.xml`:** This spec previously referenced `shared/src/commonMain/resources/strings/strings_en.xml` as the string resource location. That reference is **obsolete** — string resources are Kotlin code in `AppStrings.kt`, not XML files. The XML format, parsing strategy, and `stringResource()` function signature are not applicable. The custom `appString(key: String)` composable function (defined in §10.5 `LocalLocale.kt`) takes a `String` key (not an `Int` resource ID) and resolves from the in-memory `AppStrings` map. This is distinct from CMP's built-in `stringResource(@StringRes Int)` which resolves from XML resources by system locale.

#### String map structure

```kotlin
// shared/src/commonMain/kotlin/com/littlebridge/enrollplus/core/locale/AppStrings.kt

/**
 * All UI string keys. Add keys here as strings are externalized from screens.
 * Keys are stable identifiers — never change a key's name after release.
 */
object StringKeys {
    // ── Naming convention: {screen}.{widget}.{variant} ──
    //   screen  = screen or feature area (auth, dashboard, settings, parent_dashboard, teacher_attendance, etc.)
    //   widget  = widget or section identifier (title, subtitle, label, hint, button_save, error_empty, etc.)
    //   variant = optional disambiguator (desc, short, long, plural, etc.)
    //
    // Examples:
    //   auth.login.title           → "Log In"
    //   auth.login.button_submit    → "Submit"
    //   parent_dashboard.attendance.label → "Attendance"
    //   settings.language.title     → "Language"
    //   settings.language.desc      → "Choose your preferred language"
    //   common.button_save          → "Save"
    //   common.button_cancel        → "Cancel"
    //   common.error_generic        → "Something went wrong"
    //
    // Rules:
    //   1. Keys are snake_case, dot-separated, all lowercase.
    //   2. Use `common.` prefix for strings shared across screens (buttons, labels, errors).
    //   3. Use the screen directory name as the first segment (e.g., `parent_dashboard`, not `parent`).
    //   4. Never change a key after release — keys are stable identifiers.
    //   5. For pluralized strings, append `_plural` suffix (see §10.6).
    //   6. For gendered variants (if needed), append `_m` / `_f` suffix (see §10.7).

    // Common (shared across screens)
    const val COMMON_BUTTON_SAVE     = "common.button_save"
    const val COMMON_BUTTON_CANCEL   = "common.button_cancel"
    const val COMMON_ERROR_GENERIC   = "common.error_generic"

    // Auth
    const val AUTH_LOGIN_TITLE       = "auth.login.title"
    const val AUTH_LOGIN_SUBTITLE    = "auth.login.subtitle"
    const val AUTH_OTP_PROMPT        = "auth.otp.prompt"

    // Dashboard
    const val DASHBOARD_TITLE        = "dashboard.title"
    const val DASHBOARD_GREETING     = "dashboard.greeting"

    // Settings
    const val SETTINGS_LANGUAGE_TITLE = "settings.language.title"
    const val SETTINGS_LANGUAGE_DESC  = "settings.language.desc"

    // ... add all keys during Phase 1 externalization (~1000 keys total)
}

/**
 * String maps per language. Each map is a complete set of translations.
 * English is the canonical source — all keys must exist in the English map.
 * Other languages may omit keys; missing keys fall back to English.
 */
object AppStrings {
    private val en: Map<String, String> = mapOf(
        StringKeys.COMMON_BUTTON_SAVE      to "Save",
        StringKeys.COMMON_BUTTON_CANCEL    to "Cancel",
        StringKeys.COMMON_ERROR_GENERIC    to "Something went wrong",
        StringKeys.AUTH_LOGIN_TITLE        to "Log In",
        StringKeys.AUTH_LOGIN_SUBTITLE     to "Enter your credentials to continue",
        StringKeys.AUTH_OTP_PROMPT         to "We've sent a 6-digit code to {phone}",
        StringKeys.DASHBOARD_TITLE         to "Dashboard",
        StringKeys.DASHBOARD_GREETING      to "Welcome back",
        StringKeys.SETTINGS_LANGUAGE_TITLE to "Language",
        StringKeys.SETTINGS_LANGUAGE_DESC  to "Choose your preferred language",
    )

    private val hi: Map<String, String> = mapOf(
        StringKeys.COMMON_BUTTON_SAVE      to "सहेजें",
        StringKeys.COMMON_BUTTON_CANCEL    to "रद्द करें",
        StringKeys.COMMON_ERROR_GENERIC    to "कुछ गलत हुआ",
        StringKeys.AUTH_LOGIN_TITLE        to "लॉग इन",
        StringKeys.AUTH_LOGIN_SUBTITLE     to "जारी रखने के लिए अपनी प्रमाणिकी दर्ज करें",
        StringKeys.AUTH_OTP_PROMPT         to "हमने {phone} पर 6-अंकों का कोड भेजा है",
        StringKeys.DASHBOARD_TITLE         to "डैशबोर्ड",
        StringKeys.DASHBOARD_GREETING      to "वापसी पर स्वागत है",
        StringKeys.SETTINGS_LANGUAGE_TITLE to "भाषा",
        StringKeys.SETTINGS_LANGUAGE_DESC  to "अपनी पसंदीदा भाषा चुनें",
    )

    // ... bn, ta, te, mr, gu, kn, ml, pa maps

    private val maps: Map<String, Map<String, String>> = mapOf(
        "en" to en,
        "hi" to hi,
        // ... all supported languages
    )

    /**
     * Resolve a string key for the given locale.
     * Falls back to English if the key is missing in the selected language.
     * Falls back to the key itself if missing from English (indicates a bug).
     */
    fun get(key: String, locale: String): String {
        val langMap = maps[locale] ?: maps["en"]!!
        return langMap[key] ?: en[key] ?: key
    }

    /**
     * Resolve a pluralized string using ICU MessageFormat.
     * The string value must be an ICU plural pattern, e.g.:
     *   "{count, plural, =0 {No notifications} =1 {1 notification} other {# notifications}}"
     * Falls back to English if key missing in selected language.
     * See §10.6 for pluralization details.
     */
    fun getPlural(key: String, locale: String, count: Int): String {
        val langMap = maps[locale] ?: maps["en"]!!
        val pattern = langMap[key] ?: en[key] ?: key
        return icuFormat(pattern, locale, "count" to count)
    }

    /** Check if a translation exists for the given key and locale (for testing). */
    fun hasTranslation(key: String, locale: String): Boolean {
        return (maps[locale]?.containsKey(key) == true)
    }
}
```

#### Composable accessor

```kotlin
// shared/src/commonMain/kotlin/com/littlebridge/enrollplus/core/locale/LocalLocale.kt

val LocalLocale = compositionLocalOf<String> { "en" }

/**
 * Returns the localized string for the given key, using the current locale
 * from CompositionLocalProvider(LocalLocale). Falls back to English.
 *
 * Usage in screens:
 *   val title = appString(StringKeys.AUTH_LOGIN_TITLE)
 *   Text(title)
 *
 * For parameterized strings (named placeholders):
 *   val prompt = appString(StringKeys.AUTH_OTP_PROMPT, "phone" to phoneNumber)
 */
@Composable
fun appString(key: String, vararg args: Pair<String, Any?>): String {
    val locale = LocalLocale.current
    val template = AppStrings.get(key, locale)
    return if (args.isEmpty()) template else icuFormat(template, locale, *args)
}

/**
 * Returns a pluralized localized string using ICU MessageFormat.
 *
 * Usage:
 *   val msg = appPlural(StringKeys.NOTIFICATIONS_COUNT, unreadCount)
 *   Text(msg)  // "3 notifications" / "३ सूचनाएँ"
 *
 * The string value must be an ICU plural pattern. See §10.6.
 */
@Composable
fun appPlural(key: String, count: Int): String {
    val locale = LocalLocale.current
    return AppStrings.getPlural(key, locale, count)
}
```

#### Why not CMP's built-in stringResource(Res.string.key)?

The codebase already imports `org.jetbrains.compose.resources.stringResource` (see `CommonLandingScreenV2.kt:86`) and has `composeApp/src/commonMain/composeResources/values/strings.xml`. CMP's resources API supports locale-qualified directories (`composeResources/values-hi/strings.xml`, `composeResources/values-bn/strings.xml`, etc.) and auto-generates `Res.string.*` accessors. However:

1. **System locale binding:** CMP's `stringResource()` resolves based on the **system locale**, not an in-app user preference. Changing the system locale requires platform-specific code (Android: `AppCompatDelegate.setApplicationLocales()`, iOS: `UserDefaults`, JVM: `Locale.setDefault()`, Web: no equivalent). This defeats the "instant switch, no restart" requirement (BR-002).
2. **No CompositionLocal override:** CMP does not provide a `CompositionLocal` to override the resource locale at the composition level. There is no equivalent to `CompositionLocalProvider(LocalLocale provides "hi")` for CMP's resource system.
3. **Cross-platform inconsistency:** Web (Wasm/JS) has no system locale API. An in-app language switcher cannot rely on system locale changes.

The Kotlin string maps approach avoids all three issues: the `LocaleManager` StateFlow drives `LocalLocale`, which drives `appString()`, which reads from `AppStrings.get(key, locale)`. Language switch is instant — no system locale change, no restart, no platform-specific code.

### 10.6 Pluralization (ICU MessageFormat)

Indian languages have diverse pluralization rules. Strings with counts (notifications, announcements, attendance days, fees) must use ICU MessageFormat plural patterns, not hardcoded singular/plural.

#### ICU plural categories by language

| Language | CLDR plural categories | Example |
|---|---|---|
| English (en) | one, other | "1 notification" / "3 notifications" |
| Hindi (hi) | one, other | "1 सूचना" / "3 सूचनाएँ" |
| Bengali (bn) | one, other | "1 বিজ্ঞপ্তি" / "3 বিজ্ঞপ্তি" |
| Tamil (ta) | one, other | "1 அறிவிப்பு" / "3 அறிவிப்புகள்" |
| Telugu (te) | one, other | "1 నోటిఫికేషన్" / "3 నోటిఫికేషన్‌లు" |
| Marathi (mr) | one, other | "1 सूचना" / "3 सूचना" |
| Gujarati (gu) | one, other | "1 સૂચના" / "3 સૂચનાઓ" |
| Kannada (kn) | one, other | "1 ಅಧಿಸೂಚನೆ" / "3 ಅಧಿಸೂಚನೆಗಳು" |
| Malayalam (ml) | one, other | "1 അറിയിപ്പ്" / "3 അറിയിപ്പുകൾ" |
| Punjabi (pa) | one, other | "1 ਨੋਟੀਫਿਕੇਸ਼ਨ" / "3 ਨੋਟੀਫਿਕੇਸ਼ਨ" |

All 10 supported languages use CLDR `one`/`other` plural categories. No language in the initial set requires `zero`, `two`, `few`, or `many` categories, but the ICU MessageFormat supports them if needed for future languages (e.g., Arabic would use `zero`, `one`, `two`, `few`, `many`, `other`).

#### String map entries for pluralized strings

Pluralized strings use ICU MessageFormat patterns as their value. Keys use the `_plural` suffix convention.

```kotlin
object StringKeys {
    // ... other keys ...

    // Pluralized strings (suffix: _plural)
    const val NOTIFICATIONS_COUNT_PLURAL    = "common.notifications_count_plural"
    const val ATTENDANCE_DAYS_PRESENT_PLURAL = "parent_dashboard.attendance.days_present_plural"
    const val ANNOUNCEMENTS_UNREAD_PLURAL   = "parent_dashboard.announcements.unread_plural"
}

object AppStrings {
    private val en: Map<String, String> = mapOf(
        // ... other strings ...
        StringKeys.NOTIFICATIONS_COUNT_PLURAL to
            "{count, plural, =0 {No notifications} =1 {1 notification} other {# notifications}}",
        StringKeys.ATTENDANCE_DAYS_PRESENT_PLURAL to
            "{count, plural, =1 {1 day present} other {# days present}}",
        StringKeys.ANNOUNCEMENTS_UNREAD_PLURAL to
            "{count, plural, =0 {No unread announcements} =1 {1 unread announcement} other {# unread announcements}}",
    )

    private val hi: Map<String, String> = mapOf(
        // ... other strings ...
        StringKeys.NOTIFICATIONS_COUNT_PLURAL to
            "{count, plural, =0 {कोई सूचना नहीं} =1 {1 सूचना} other {# सूचनाएँ}}",
        StringKeys.ATTENDANCE_DAYS_PRESENT_PLURAL to
            "{count, plural, =1 {1 दिन उपस्थित} other {# दिन उपस्थित}}",
        StringKeys.ANNOUNCEMENTS_UNREAD_PLURAL to
            "{count, plural, =0 {कोई अपठित घोषणा नहीं} =1 {1 अपठित घोषणा} other {# अपठित घोषणाएँ}}",
    )
    // ... other languages follow same ICU pattern
}
```

#### Usage in screens

```kotlin
// Singular/plural resolved automatically based on count + locale
val notifMsg = appPlural(StringKeys.NOTIFICATIONS_COUNT_PLURAL, unreadCount)
Text(notifMsg)  // en: "3 notifications" / hi: "3 सूचनाएँ" / ta: "3 அறிவிப்புகள்"
```

#### ICU MessageFormat implementation

The `icuFormat()` function used by `AppStrings.getPlural()` and `appString()` wraps a Kotlin Multiplatform ICU MessageFormat implementation. Options:

1. **`kotlinx-datetime` + manual ICU parsing** — lightweight, no external dependency. Parse `{count, plural, ...}` patterns manually (the only pattern type we need for the initial 10 languages).
2. **ICU4J/JNI on Android, JS ICU on Web** — platform-specific, heavier.
3. **Pure Kotlin ICU MessageFormat parser** — small library, ~200 LOC for `plural` + `select` + simple `{name}` substitution.

**Recommended:** Option 3 (pure Kotlin parser). The plural patterns we need are simple (`one`/`other` only for initial 10 languages). A minimal parser handling `{name}` substitution and `{count, plural, ...}` patterns is sufficient and avoids platform-specific dependencies.

```kotlin
// shared/.../core/locale/IcuFormatter.kt

/**
 * Minimal ICU MessageFormat implementation for Kotlin Multiplatform.
 * Supports: {name} placeholder substitution and {name, plural, ...} patterns.
 * Sufficient for the initial 10 languages (all use one/other categories).
 */
expect fun icuFormat(pattern: String, locale: String, vararg args: Pair<String, Any?>): String
```

Actual implementations:
- `commonMain`: Pure Kotlin parser (~200 LOC)
- Platform-specific overrides only if a native ICU library is preferred

### 10.7 Gender-Neutral Translation Strategy

Several Indian languages (Hindi, Bengali, Marathi, Gujarati, Punjabi) have grammatical gender. For example, in Hindi, "Your child has 85% attendance" changes based on the child's gender (बालक vs बालिका). Managing gendered variants for every UI string would double the string map size and require passing gender context to every composable.

**Design decision: Gender-neutral phrasing (Option A)**

The translation strategy for UI strings and AI-translated content is to use **gender-neutral phrasing** wherever possible. This avoids the complexity of gendered string variants while remaining natural in all 10 languages.

#### Guidelines for translators (UI string maps)

1. **Use gender-neutral constructions** when referring to students/children:
   - ❌ "आपका बालक 85% उपस्थिति पर है" (gendered: बालक = male child)
   - ✅ "आपके बच्चे की उपस्थिति 85% है" (gender-neutral: बच्चा = child, neutral)

2. **Use neutral nouns instead of gendered ones:**
   - Hindi: बच्चा (child, neutral) instead of बालक/बालिका (boy/girl)
   - Bengali: সন্তান (child, neutral) instead of ছেলে/মেয়ে (boy/girl)
   - Marathi: बाळ (child, neutral) instead of मुलगा/मुलगी (boy/girl)

3. **Avoid gendered adjectives** — rephrase to use neutral verbs or nouns:
   - ❌ "वह प्रथम आया" (he came first — gendered verb आया)
   - ✅ "वह प्रथम स्थान पर रहा" (neutral construction)

4. **For strings where gender-neutral phrasing is impossible** (rare), use the `_m` / `_f` key suffix convention:
   ```kotlin
   const val REPORT_CARD_RANK_M = "report_card.rank.m"  // "He ranked 1st"
   const val REPORT_CARD_RANK_F = "report_card.rank.f"  // "She ranked 1st"
   ```
   The composable would select the key based on the student's gender:
   ```kotlin
   val rankKey = if (student.gender == "F") StringKeys.REPORT_CARD_RANK_F else StringKeys.REPORT_CARD_RANK_M
   val rankStr = appString(rankKey, "rank" to student.rank)
   ```
   **This is a last resort** — prefer gender-neutral phrasing.

#### AI translation prompt (server-side content)

The AI translation prompt for announcements and notifications includes a gender-neutral instruction:

```
Translate the following text to {{target_language}}. Maintain tone and context.
Do not translate proper nouns (school names, person names). Keep it natural and concise.
Use gender-neutral phrasing where possible (e.g., "child" instead of "son/daughter").
```

This ensures AI-translated announcements and notifications avoid unnecessary gender marking.

#### Known limitation

Gender-neutral phrasing may occasionally sound slightly less natural in highly gendered languages (Hindi, Punjabi). This is an accepted trade-off for the initial release. If user feedback indicates gendered phrasing is important for specific strings, targeted `_m`/`_f` variants can be added incrementally.

### 10.8 Locale-Aware Formatting

```kotlin
expect class DateFormatter(locale: String) {
    fun format(date: LocalDate): String  // "15 July" / "१५ जुलाई"
    fun formatLong(date: LocalDate): String  // "Monday, 15 July 2026"
}

expect class CurrencyFormatter(locale: String) {
    fun format(amount: Double): String  // "₹1,00,000" (Indian numbering)
}

expect class NumberFormatter(locale: String) {
    fun formatInt(value: Int): String            // 85 → "८५" in Hindi
    fun formatPercent(value: Double): String     // 85.0 → "८५%" in Hindi
    fun formatDecimal(value: Double, places: Int = 2): String  // 3.14 → "३.१४" in Hindi
}
```

**Time zone handling:** All timestamps are stored as UTC in the database. For display, the client converts UTC → IST (Asia/Kolkata, UTC+5:30). India has a single timezone, so no per-user timezone configuration is needed. `DateFormatter` assumes IST for display. Server-side notification timestamps are also converted to IST in push payloads.

**Number formatting:** `NumberFormatter` uses locale-aware digits (Devanagari digits in Hindi, Bengali digits in Bengali, etc.) for integers, percentages, and decimals. Currency formatting remains in `CurrencyFormatter` (Indian numbering system: lakhs/crores). English locale uses Western digits.

### 10.9 Offline Support

- Language preference cached locally in DataStore
- Offline language change queued for sync (see below)
- String maps compiled into app (no network needed for UI) — `AppStrings` object is Kotlin code, bundled in the binary

#### Offline queue implementation

The offline queue uses a **DataStore flag + coroutine retry loop** (not WorkManager — WorkManager is Android-only and this is a KMP shared module targeting Android, iOS, JVM, and Web).

```kotlin
// shared/.../core/locale/LocaleManager.kt

class LocaleManager(
    private val preferenceRepository: PreferenceRepository,
    private val languageRepository: LanguageRepository,
    private val networkMonitor: NetworkMonitor,  // expect/actual — observes connectivity
) {
    val currentLocale: StateFlow<String>

    fun setLocale(lang: String) {
        // 1. Update StateFlow (instant recomposition)
        currentLocale.value = lang
        // 2. Persist to DataStore (local truth)
        preferenceRepository.setLanguagePref(lang)
        // 3. Debounce 2s, then attempt server sync
        syncJob?.cancel()
        syncJob = scope.launch {
            delay(2000)  // 2-second debounce
            syncToServer(lang)
        }
    }

    private suspend fun syncToServer(lang: String) {
        val online = networkMonitor.isOnline()
        if (!online) {
            // Queue: set DataStore flag for pending sync
            preferenceRepository.setLanguagePrefPendingSync(true)
            return
        }
        val result = languageRepository.updateLanguagePref(/* token */, lang)
        if (result.isFailure) {
            preferenceRepository.setLanguagePrefPendingSync(true)
        } else {
            preferenceRepository.setLanguagePrefPendingSync(false)
        }
    }

    init {
        // On network restore, flush pending sync
        scope.launch {
            networkMonitor.observe().collect { online ->
                if (online && preferenceRepository.getLanguagePrefPendingSync()) {
                    syncToServer(currentLocale.value)
                }
            }
        }
    }
}
```

**Key design decisions:**
- **DataStore flag** (`language_pref_pending_sync: Boolean`) — persists across app restarts. Set to `true` when sync fails or offline. Set to `false` on successful sync.
- **Coroutine retry loop** — `networkMonitor.observe()` is a `Flow<Boolean>` that emits on connectivity change. When online + pending flag is set, the sync is retried.
- **2-second debounce** — `syncJob?.cancel()` + `delay(2000)` ensures rapid language switches only sync the final selection.
- **No WorkManager** — WorkManager is Android-only. The KMP shared module uses `NetworkMonitor` (expect/actual) + coroutines instead. On Android, `NetworkMonitor` can wrap `ConnectivityManager`; on iOS, `NWPathMonitor`; on JVM/Web, a simple ping or always-online assumption.
- **No exponential backoff** — language pref sync is a single PATCH call. If it fails, the pending flag remains set and the next connectivity change retries. Simple and sufficient.

### 10.10 Loading States

- **Language switch:** instant (no loading state). StateFlow recomposition updates all `appString()` calls in < 500ms. No spinner, no skeleton.
- **Content translation (announcements, notifications):** transparent fallback. The server translates content before returning it in the API response (via `Accept-Language` header) or before sending a push notification. The client never shows a loading indicator for translation — it simply receives the translated content in the response.

**What the user sees:**

| Scenario | What happens | UX |
|---|---|---|
| **Cached translation** | Server finds translation in cache, returns translated content in API response | User sees Hindi content immediately. No loading state. |
| **On-demand translation (cache miss)** | Server calls AI, translates, caches, returns translated content in same API response | User sees a normal network loading state (existing `ShimmerBox` / spinner) while the API call completes. The response contains translated content. No separate "translating" indicator. |
| **Translation fails** | Server returns English content (fallback). `ContentTranslationService.translate()` returns original content on AI failure. | User sees English content. No error message shown — silent fallback (BR-004). |
| **Batch job in progress** (announcement just published) | Server hasn't pre-cached translations yet. On-demand translation triggers on fetch. | Same as on-demand translation — normal API loading state, translated content in response. |

**Key principle:** Translation is server-side and transparent. The client does not know whether content was translated or is original English — it just renders what the API returns. There is no "English first, then swap to Hindi" behavior. The API response is either translated or English (on fallback), determined server-side before the response is sent.

### 10.11 Error Handling (UI)

- Language sync failure: "Language preference saved locally. Will sync when online."
- Translation failure: Show English content (silent fallback)

### 10.12 Component Integration Guidelines

| Rule | Description |
|---|---|
| **R1** | All UI strings use `appString(StringKeys.KEY)` — no hardcoded strings |
| **R2** | Language switcher shows native language names (हिन्दी, not Hindi) |
| **R3** | Locale-aware date formatting via `DateFormatter` |
| **R4** | Locale-aware currency formatting via `CurrencyFormatter` (Indian numbering) |
| **R5** | `CompositionLocalProvider(LocalLocale)` wraps entire app |
| **R6** | Language preference persisted in DataStore before server sync |
| **R7** | Fallback to English if string key missing for selected language (`AppStrings.get()` handles this automatically) |
| **R8** | No app restart required for language switch |
| **R9** | RTL support ready (but not enabled for initial 10 LTR languages) |
| **R10** | All screens use `appString()` instead of hardcoded strings |
| **R11** | Strings with counts use `appPlural(StringKeys.KEY_PLURAL, count)` — ICU MessageFormat plural patterns, not hardcoded singular/plural (see §10.6) |
| **R12** | UI strings use gender-neutral phrasing; `_m`/`_f` suffix only when neutral phrasing is impossible (see §10.7) |
| **R13** | Accessibility labels (`contentDescription`) use `appString()` — no hardcoded English labels for screen readers (NFR-7) |
| **R14** | Numbers (percentages, scores, counts) use `NumberFormatter` for locale-aware digits (FR-012) |
| **R15** | Timestamps displayed in IST — server stores UTC, client converts for display (NFR-8) |
| **R16** | Emoji and Unicode in translated strings preserved — no character stripping (NFR-9) |
| **R17** | Non-English search uses server-side `ILIKE` matching — no cross-script transliteration (FR-013) |

### 10.13 Wasm/JS Font Rendering Pre-Check

**Problem:** The non-goal states "Language-specific fonts (system fonts used)." This works on Android, iOS, and JVM — all ship with Indic script fonts (Devanagari, Tamil, Bengali, Telugu, Gujarati, Kannada, Malayalam, Punjabi). However, **Wasm/JS in browsers depends on the user's OS-installed fonts**. Most desktop OSes (Windows, macOS, mainstream Linux) include Indic fonts, but:
- Minimal Linux environments (Docker containers, headless CI) may lack them
- Some older browsers or embedded webviews may not have Indic font fallbacks
- ChromeOS has limited Indic font coverage

**Pre-implementation check (Phase 1, before externalizing strings):**

1. Render sample text in all 10 scripts on Wasm/JS target in Chrome, Firefox, Safari
2. Verify Devanagari (हिन्दी), Tamil (தமிழ்), Bengali (বাংলা), Punjabi (ਪੰਜਾਬੀ) render correctly
3. If any script shows tofu boxes (□□□) or missing glyphs:
   - Bundle a minimal Indic font for web targets only
   - Recommended: **Noto Sans Devanagari** (~200KB woff2) covers Hindi, Marathi, Punjabi (Gurmukhi uses Noto Sans Gurmukhi ~150KB)
   - Place in `composeApp/src/commonMain/composeResources/font/` and load via `FontFamily` in `VTheme` for Wasm/JS only
   - Use `expect/actual` to conditionally apply bundled font on Wasm/JS, system font on other targets

4. If all scripts render correctly on all target browsers → no bundled font needed, system fonts confirmed sufficient

**Expected outcome:** Most modern browsers on Windows/macOS render Indic scripts natively. The pre-check confirms this. If issues are found, the fix is a conditional font bundle (~350KB for Devanagari + Gurmukhi woff2) loaded only on Wasm/JS — acceptable size impact.

```kotlin
// shared/.../core/locale/IndicFontProvider.kt (expect/actual)
// commonMain:
expect fun indicFontFamily(): FontFamily?  // null = use system font

// wasmJsMain / jsMain:
actual fun indicFontFamily(): FontFamily = FontFamily(
    Font(Res.font.noto_sans_devanagari, weight = FontWeight.Normal)
)

// androidMain / iosMain / jvmMain:
actual fun indicFontFamily(): FontFamily? = null  // system fonts are sufficient
```

### 10.14 First-Launch Language Selection (FR-011)

On first app launch, when `LocaleManager` has no stored `languagePref` in DataStore, a language selection screen is shown **before the login screen**.

```kotlin
// composeApp/.../ui/v2/screens/onboarding/LanguageSelectionScreen.kt
@Composable
fun LanguageSelectionScreen(
    onLanguageSelected: (String) -> Unit,
) {
    val languages = SUPPORTED_LANGUAGES  // en, hi, bn, ta, te, mr, gu, kn, ml, pa
    // Display each language with its native name:
    //   English    → "English"
    //   Hindi      → "हिन्दी"
    //   Bengali    → "বাংলা"
    //   Tamil      → "தமிழ்"
    //   ...
    // Default selection = English (highlighted)
    // On select: localeManager.setLocale(lang) → onLanguageSelected(lang)
}
```

**Navigation flow:**
```
App Launch
  → Check DataStore for languagePref
    → If exists: proceed to login screen
    → If missing: show LanguageSelectionScreen
      → User selects language → setLocale() → proceed to login screen
```

**Key behaviors:**
- Shown only once (DataStore has no `languagePref` on first launch)
- Default selection = English (BR-001)
- Selection persists to DataStore immediately via `LocaleManager.setLocale()`
- Server sync happens after login (when JWT is available) via `PATCH /api/v1/user/language-pref`
- User can change language later in Settings (FR-002)
- The screen uses native language names (हिन्दी, not Hindi) — same list as the in-app language switcher

### 10.15 Non-English Search Support (FR-013)

In-app search (school search, student search, announcement search) accepts non-English input. The server performs case-insensitive `ILIKE` matching against stored data.

**What works:**
- Searching "दिल्ली" matches stored "दिल्ली" (same-script matching)
- Searching "123" matches "123" (numeric matching, locale-independent)

**What does NOT work (non-goal):**
- Cross-script matching: searching "Delhi" does NOT match "दिल्ली" (no transliteration)
- Transliterated search: searching "Dilli" does NOT match "दिल्ली" (no phonetic matching)

**Implementation:** No server changes needed — existing `ILIKE` queries already support Unicode. The search input field uses the app's current locale keyboard. No special handling required beyond ensuring the search `TextField` accepts Unicode input (it does by default in Compose).

**Future enhancement:** Cross-script search via transliteration (e.g., `Aksharamukha` library) — see §25 Future Enhancements.

### 10.16 Translation Quality Review Process (FR-014)

AI-translated string resources undergo a human review workflow before release. The process ensures translation quality across all 10 languages.

**Workflow:**

1. **AI Translation (Phase 2):** `AiService.complete()` translates each English string to the target language. Output is a CSV/spreadsheet per language with columns: `key`, `english_text`, `ai_translation`, `status` (default: `pending`).

2. **Human Review:** A bilingual team member or contracted translator reviews each string:
   - **Approve:** `status = approved` — string is ready for release
   - **Reject + Correct:** `status = corrected` — reviewer manually corrects the translation
   - **Reject + Re-translate:** `status = rejected` — string sent back for AI re-translation with adjusted prompt

3. **Approval Gate:** A language map is only included in the release build when **all keys** in that language have `status = approved` or `status = corrected`. The `AppStringsKeyParityTest` (§10.17) enforces this.

4. **Spot-check (post-release):** Dev Team samples 5% of strings per language in the running app to verify rendering and context.

**Reviewer roles:**
- **Hindi, Marathi, Gujarati, Punjabi:** Devanagari/Gurmukhi script — one reviewer can cover multiple languages if proficient
- **Bengali, Assamese (future):** Bengali script
- **Tamil, Telugu, Kannada, Malayalam:** Dravidian scripts — one reviewer per script family
- **English:** Canonical source — no review needed (Dev Team authors)

**Tools:**
- Review spreadsheet (Google Sheets / Excel) — one tab per language
- AI translation script (`scripts/_gen_translations.py`) generates the initial spreadsheet
- Reviewer fills `status` and `corrected_text` columns
- Build script (`scripts/import_translations.py`) reads approved/corrected strings and generates Kotlin map entries for `AppStrings.kt`

**User feedback vs review process:**
- **Pre-release review (FR-014, this section):** Human reviewers check AI-translated strings **before** they ship. This is the primary quality gate. No user involvement.
- **Post-release user feedback (F-7, future enhancement):** In-app mechanism for users to report poor translations (e.g., "Report translation issue" button). This is **not** in scope for the initial release. Until F-7 is implemented, translation issues are detected via:
  - Dev Team spot-checks (step 4 above)
  - Support tickets / user complaints (existing channels)
  - ~~`TranslationFailed` and `ContentTranslated` event logs~~ — deferred to F-14 (no AI translation in initial release)
- **Re-translation trigger:** If a translation quality issue is reported (via any channel), the fix process is: add the key to the review spreadsheet → reviewer corrects → `import_translations.py` regenerates the map → next app release. ~~For dynamic content (announcements), the content editor can edit the source English content, which triggers `ContentUpdated` → cache invalidation → re-translation on next fetch.~~ Dynamic content re-translation deferred to F-14.

### 10.17 String Resource Completeness Check (FR-015)

An automated unit test verifies that all 10 language maps in `AppStrings` have the same set of keys as the English canonical map. Missing keys in any language fail the build.

```kotlin
// shared/src/commonTest/kotlin/com/littlebridge/enrollplus/core/locale/AppStringsKeyParityTest.kt

class AppStringsKeyParityTest {

    private val supportedLangs = listOf("en", "hi", "bn", "ta", "te", "mr", "gu", "kn", "ml", "pa")
    private val canonicalLang = "en"

    @Test
    fun "all language maps have same keys as English canonical"() {
        val englishKeys = AppStrings.getKeys(canonicalLang)
        for (lang in supportedLangs) {
            if (lang == canonicalLang) continue
            val langKeys = AppStrings.getKeys(lang)
            val missing = englishKeys - langKeys
            val extra = langKeys - englishKeys
            assertTrue(missing.isEmpty(),
                "Language '$lang' is missing ${missing.size} keys: ${missing.take(10)}")
            assertTrue(extra.isEmpty(),
                "Language '$lang' has ${extra.size} extra keys not in English: ${extra.take(10)}")
        }
    }

    @Test
    fun "all pluralized keys use ICU plural pattern"() {
        val pluralKeys = AppStrings.getKeys(canonicalLang)
            .filter { it.endsWith("_plural") }
        for (key in pluralKeys) {
            val value = AppStrings.get(key, canonicalLang)
            assertTrue(value.contains("{count, plural,"),
                "Key '$key' must contain ICU plural pattern, got: $value")
        }
    }
}
```

**Note:** `AppStrings.getKeys(lang)` is a test-only helper that returns the key set for a given language. Added to `AppStrings` for testing purposes:
```kotlin
// In AppStrings object
internal fun getKeys(locale: String): Set<String> =
    (maps[locale] ?: maps["en"]!!).keys
```

This test runs in `commonTest` — it executes on all KMP targets (Android, iOS, JVM, Web) in CI. A missing key in any language → build failure → translator must fill the gap before merge.

---

## 11. Shared Module Changes (KMP)

### 11.1 DTOs

All DTOs defined in section 9.2, placed in `shared/.../i18n/domain/model/LanguageModels.kt`.

### 11.2 Domain Models

```kotlin
data class LanguagePreference(
    val code: String,       // en, hi, bn, etc.
    val nativeName: String, // हिन्दी, বাংলা, etc.
    val englishName: String, // Hindi, Bengali, etc.
    val script: String,     // Devanagari, Bengali, etc.
)

val SUPPORTED_LANGUAGES = listOf(
    LanguagePreference("en", "English", "English", "Latin"),
    LanguagePreference("hi", "हिन्दी", "Hindi", "Devanagari"),
    LanguagePreference("bn", "বাংলা", "Bengali", "Bengali"),
    LanguagePreference("ta", "தமிழ்", "Tamil", "Tamil"),
    LanguagePreference("te", "తెలుగు", "Telugu", "Telugu"),
    LanguagePreference("mr", "मराठी", "Marathi", "Devanagari"),
    LanguagePreference("gu", "ગુજરાતી", "Gujarati", "Gujarati"),
    LanguagePreference("kn", "ಕನ್ನಡ", "Kannada", "Kannada"),
    LanguagePreference("ml", "മലയാളം", "Malayalam", "Malayalam"),
    LanguagePreference("pa", "ਪੰਜਾਬੀ", "Punjabi", "Gurmukhi"),
)
```

### 11.3 Repository Interfaces

```kotlin
interface LanguageRepository {
    suspend fun getLanguagePref(token: String): NetworkResult<String>
    suspend fun updateLanguagePref(token: String, language: String): NetworkResult<Unit>
}
```

### 11.4 UseCases

```kotlin
class GetLanguagePrefUseCase(
    private val preferenceRepository: PreferenceRepository,
) {
    operator fun invoke(): StateFlow<String> = preferenceRepository.getLanguagePref()
}

class UpdateLanguagePrefUseCase(
    private val languageRepository: LanguageRepository,
    private val preferenceRepository: PreferenceRepository,
) {
    suspend operator fun invoke(language: String): NetworkResult<Unit> {
        require(language in SUPPORTED_LANGUAGES.map { it.code }) { "Invalid language code: $language" }
        preferenceRepository.setLanguagePref(language) // local persistence (DataStore)
        return languageRepository.updateLanguagePref(/* token */, language) // server sync
    }
}
```

**No `TranslateContentUseCase`** — content translation is server-side only (§8.2 Design Principle 3: "Translate on server, not client"). The client never calls AI for translation. UI strings are resolved locally via `appString(StringKeys.KEY)` from `AppStrings` Kotlin string maps (§10.5). Server-translated content (announcements, notifications) arrives pre-translated via API responses and push payloads.

### 11.5 Validation

**Language code validation:**
- **Where enforced:** Server-side (`UserRouting.kt` PATCH endpoint) + client-side (`LocaleManager.setLocale()` checks against `SUPPORTED_LANGUAGES` set).
- **Rule:** Language code must be one of: `en`, `hi`, `bn`, `ta`, `te`, `mr`, `gu`, `kn`, `ml`, `pa`.
- **Server error:** HTTP 400 `Invalid language code` with `ApiResponse` error envelope.
- **Client behavior:** `LocaleManager` silently ignores invalid codes (defensive — should never happen since UI picker only shows valid options).

**Content length for translation:**
- **Where enforced:** Server-side, in `ContentTranslationService.translate()`.
- **Rule:** Content is **truncated to 5000 characters** before sending to AI. This is not a validation rejection — it's a silent truncation.
- **Rationale:** AI translation has token limits. 5000 chars ≈ 1250 tokens (English) ≈ 800 tokens (Indic scripts). Well within `maxTokens = 2048` budget.
- **Behavior:** `ContentTranslationService.translate()` calls `content.take(5000)` before the AI call. The full content remains in the DB unchanged. The client receives the translated (truncated) content + a `truncated: Boolean` field in the announcement response body. See EC-17.
- **No client-side validation needed:** The client does not enforce the 5000-char limit. Truncation is a server-side concern. The client renders whatever the server returns.
- **EC-17 vs §11.5 clarification:** §11.5 previously said "max 5000 characters" which implied rejection. This is corrected: the server **truncates** (accepts, shortens), not **rejects**. EC-17 is the authoritative specification.

### 11.6 Serialization

Standard Kotlinx serialization. Language codes serialized as lowercase strings.

### 11.7 Network APIs

Ktor `@Resource` route definitions added to `LanguageApi.kt`:
- PATCH `/api/v1/user/language-pref`

### 11.8 Database Models (Local Cache)

- Language preference stored in DataStore (key-value)
- String resources bundled in app (no local DB)

---

## 12. Permissions Matrix

| Action | Super Admin | School Admin | Teacher | Parent |
|---|---|---|---|---|
| View own language preference | ✅ | ✅ | ✅ | ✅ |
| Update own language preference | ✅ | ✅ | ✅ | ✅ |
| View school medium of instruction | ✅ | ✅ | ✅ | ✅ |
| View supported languages list | ✅ | ✅ | ✅ | ✅ |
| View language distribution | ✅ | ❌ | ❌ | ✅ |
| View per-user language preferences | ✅ | ❌ | ❌ | ❌ |
| View own language history | ✅ | ✅ | ✅ | ❌ |
| View platform language adoption | ❌ | ❌ | ❌ | ✅ |
| ~~View translation cache stats~~ | ~~❌~~ | ~~❌~~ | ~~❌~~ | ~~✅~~ — deferred to F-14 |
| View all users by language (grouped by role) | ❌ | ❌ | ❌ | ✅ |
| View ServerStrings translations | ❌ | ❌ | ❌ | ✅ |
| Update ServerStrings translations | ❌ | ❌ | ❌ | ✅ |

---

## 13. Notifications

### Notification Translation Integration

In `Notify.kt`, before sending notification:
1. Resolve recipient's language via `UserLanguageResolver.resolve(userId)` — checks in-memory Caffeine cache (10-min TTL) first, then falls back to DB query on `AppUsersTable.languagePref`, then falls back to `'en'`. See §9.3 Mechanism A.
2. If not English, resolve notification title + body via `ServerStrings.fill(key, lang, params)` — pre-translated templates with placeholder substitution.
3. If notification type has no template (dynamic content), body stays English in initial release.
4. Send notification with resolved title + body.

```kotlin
// Notify.kt — integration point (Notify is an object singleton)
// Add inside Notify.toUsers() or similar, per recipient:
val userLang = UserLanguageResolver.resolve(recipientUserId)
if (userLang != "en" && ServerStrings.hasTemplate(notificationType, "body")) {
    val title = ServerStrings.fill("notification.${notificationType}.title", userLang, titleParams)
    val body = ServerStrings.fill("notification.${notificationType}.body", userLang, bodyParams)
    sendNotification(recipientUserId, title, body)
} else {
    // English or no template available — send as-is
    sendNotification(recipientUserId, title, body)
}
```

### Notification Channels by Language

| Channel | Translation | Fallback |
|---|---|---|
| FCM Push | `ServerStrings` template in user's language | English if template or language missing |
| WhatsApp | Pre-approved template in user's language | English template if not available |
| In-App | `ServerStrings` template in user's language | English if template or language missing |

### WhatsApp Multi-Language Templates

From `WHATSAPP_INTEGRATION_SPEC.md`:
- Each WhatsApp template has language-specific versions
- Template selected based on `languagePref`
- Meta requires pre-approval for each language version
- If template not available in user's language, English template used

---

## 14. Background Jobs

~~**Translation Cache Cleanup Job**~~ — deferred to F-14. No cache table in initial release.

~~**Batch Translation Job**~~ — deferred to F-14. No AI translation in initial release.

**Language Pref History Archival** (optional, low priority):

| Property | Value |
|---|---|
| **Name** | `LanguagePrefHistoryArchivalJob` |
| **Schedule** | Monthly |
| **Duration** | < 1 minute |
| **Retry** | None |

#### Job Flow

1. Query `language_pref_history` where `changed_at < now() - 1 year`
2. Archive to cold storage or delete (based on retention policy)
3. Return count archived
4. Log summary

~~#### Synchronous vs Asynchronous Decision~~ — deferred to F-14.

~~#### Job Flow~~ — deferred to F-14.

---

## 15. Integrations

### Internal Integrations

| System | Integration Point | Direction | Protocol | Error Handling |
|---|---|---|---|---|
| `AppUsersTable.languagePref` | User language preference | Read | Direct DB | Default 'en' if not set (after migration; current DB default is 'hi'). `ReportAssemblyService.resolveLanguagePref()` also falls back to 'en' when user row is not found. |
| `SchoolsTable.medium` | School medium of instruction (informational) | Read | Direct DB | Stores human-readable values (e.g., "English", "Hindi") — **not** language codes. Does not override `languagePref`. **Existing conflation bug:** `ReportRollupService.kt:77` uses `medium` as a language code fallback (`?: "en"`). Must not be repeated in multi-language implementation. See Appendix D § School-Level Settings. |
| `Notify.kt` | Notification translation | Call | Direct call (`UserLanguageResolver.resolve()` + `ServerStrings.fill()`) | Fallback to English |
| `NotificationService.kt` | FCM push with translated content | Call | Direct call | Existing error handling |
| WhatsApp gateway | Language-specific templates | Outbound | HTTP API | English fallback |
| `ServerStrings` (Kotlin `object` singleton) | Pre-translated notification templates | Call | Direct call (`ServerStrings.fill(key, lang, params)`) | Falls back to English if key/lang missing |
| `UserLanguageResolver` (Kotlin `object` singleton) | User language lookup for notifications | Call | Direct call (`UserLanguageResolver.resolve(userId)`) | Fallback to `'en'` if user not found or language blank |
| `LanguagePrefHistoryRepository` (Kotlin `object` singleton) | Language change tracking | Write | Direct DB | Insert fails silently (audit trail, non-blocking) |
| `LanguagePrefHistoryTable` | Language change history | Read/Write | Direct DB | Append-only, no soft delete |
| ~~`AiService`~~ | ~~Content translation~~ | ~~Call~~ | ~~Direct call~~ | ~~Deferred to F-14~~ |
| ~~`ContentTranslationService`~~ | ~~Translation orchestration~~ | ~~Call~~ | ~~Direct call~~ | ~~Deferred to F-14~~ |
| ~~`TranslationCacheRepository`~~ | ~~Translation cache CRUD~~ | ~~Call~~ | ~~Direct call~~ | ~~Deferred to F-14~~ |
| ~~`TranslationCacheTable`~~ | ~~Translation caching~~ | ~~Read/Write~~ | ~~Direct DB~~ | ~~Deferred to F-14~~ |
| `PewsCaseFilesTable.parentDraftLang` | PEWS parent draft language | Read/Write | Direct DB | **Independent from `languagePref`.** See §15 PEWS Integration below. |

#### PEWS Integration: `parent_draft_lang` vs `languagePref`

`PewsCaseFilesTable.parentDraftLang` (VARCHAR(8), nullable) is **independent** from `app_users.languagePref`. They serve different purposes:

| Field | Purpose | Scope | Set by | Values |
|---|---|---|---|---|
| `app_users.languagePref` | User's UI language preference | Per user (global) | User (via language switcher or first-launch) | en, hi, bn, ta, te, mr, gu, kn, ml, pa |
| `PewsCaseFilesTable.parentDraftLang` | Language of a specific PEWS parent draft message | Per case file (per intervention) | Teacher/School Admin (via `?lang=` query param on draft-message endpoint) | en, hi, mr, ta, te, bn (subset — see `ParentDraftService` LANGUAGES list) |

**Key distinctions:**
- `parentDraftLang` records what language a specific intervention draft message was written in. A teacher may choose to write a parent message in Hindi even if their own UI is in English.
- `languagePref` is the user's global UI language. It affects all screens, notifications, and content fetches.
- `parentDraftLang` is set via `POST /api/v1/school/pews/interventions/{id}/draft-message?lang=hi` — the teacher explicitly selects the draft language. It defaults to `"en"`.
- The PEWS parent draft language list (`en, hi, mr, ta, te, bn`) is a **subset** of `SUPPORTED_LANGUAGES` — not all 10 languages are available for PEWS drafts. This is an existing limitation in `ParentDraftService`, not a multi-language spec concern.
- **No sync:** Changing `languagePref` does NOT change `parentDraftLang` on existing case files. They are decoupled.
- **Future enhancement:** `ParentDraftService.generateDraft()` could default the `language` parameter to the parent's `languagePref` (resolved via `UserLanguageResolver.resolve(parentUserId)`) instead of `"en"`. This is not in scope for the initial multi-language release but is a natural integration point.

### External Integrations

| System | Purpose | Direction | Protocol | Authentication | Error Handling |
|---|---|---|---|---|---|
| AI Service (LLM) | Content translation | Outbound | HTTP API | Bearer token | Fallback to English |
| WhatsApp Gateway | Language-specific templates | Outbound | HTTP API | Bearer token (existing) | English template fallback |

### Integration Patterns

- **AI translation:** `AiService.complete(feature = "translation", lane = AiLane.FAST_CHAT, messages = listOf(LlmMessage(...)), containsPii = true, schoolId = ...)` → returns `AiResult`. If `result.ok`, use `result.content`; else fallback to original English content. `AiService` is a Kotlin `object` — call directly, no injection. See §8.3 for full code example.
- **WhatsApp templates:** Template ID selected based on `languagePref`. Pre-approved by Meta per language.
- **Notification translation:** Hybrid flow in `Notify.kt` — `ServerStrings.fill()` for templated messages (fee reminders, attendance alerts, link approved/rejected, etc.), `ContentTranslationService.translate()` for dynamic content (custom announcement text, custom teacher messages). `Notify` resolves recipient language via `UserLanguageResolver.resolve(userId)` (also an `object` singleton). See §8.3 ServerStrings for hybrid flow code and §13 for integration code.

---

## 16. Security

### Authentication

- Language preference API: JWT auth via `requireAuth()`
- Translation: system-internal, no user auth

### Authorization

- User can only update own language preference
- No cross-user preference access
- **Super Admin only:** `GET /api/admin/users-by-language` (FR-017), `GET /api/admin/server-strings`, `PATCH /api/admin/server-strings/{key}`, `DELETE /api/admin/server-strings/{key}` (FR-018). See BR-011, §8.6.

### Data Protection

- Language preference — non-sensitive user configuration
- ~~Translated content — same sensitivity as original content~~ — deferred to F-14
- ~~AI translation — content sent to LLM API, same PII as original notification/announcement~~ — deferred to F-14
- **ServerStrings overrides** — template text only, no PII. Placeholders filled at runtime in memory.

### Input Validation

- Language code: one of en, hi, bn, ta, te, mr, gu, kn, ml, pa
- ~~Content for translation: non-empty, max 5000 characters~~ — deferred to F-14
- **ServerStrings override key:** must match an existing key in compiled `ServerStrings` templates (validated against `templates["en"]` keys). Invalid keys return 400 Bad Request.
- **ServerStrings override lang:** must be one of the 10 supported language codes. Invalid lang returns 400 Bad Request.
- **ServerStrings override value:** non-empty, max 2000 characters. Empty values return 400 Bad Request.

### Rate Limiting

- ~~AI translation: 1000 per school per hour~~ — deferred to F-14
- Language preference update: standard API rate limiting
- ServerStrings override update/delete: standard API rate limiting (Super Admin only, low volume)
- Translation cache: no rate limiting (internal)

### Audit Logging

- Language preference change: user ID, old language, new language, timestamp
- ~~Translation success: content hash, target language, cache hit/miss~~ — deferred to F-14
- ~~Translation failure: content hash, target language, error~~ — deferred to F-14
- **ServerString override update (FR-018):** Super Admin ID, string key, language, old value, new value, timestamp
- **ServerString override delete (FR-018):** Super Admin ID, string key, language, timestamp
- **Users-by-language access (FR-017):** Super Admin ID, timestamp (audit log of who viewed platform-wide language data)

**Note on PATCH API response:** The `PATCH /api/v1/user/language-pref` response returns only the **new** language and `updated_at` timestamp (see §9.1). The **old** language is NOT returned in the API response — it is recorded in the server-side audit log only. The `LanguagePreferenceChanged` domain event (§8.8) carries `userId, oldLang, newLang` in its payload for internal consumers. The API response is designed for the client to confirm the update, not for audit — audit is a server-side concern logged to the application log and `LanguagePreferenceChanged` event stream.

### PII Handling

- ~~Notification/announcement content sent to AI for translation~~ — deferred to F-14. No AI calls in initial release.
- ~~**Mandatory:** All `AiService.complete()` calls from `ContentTranslationService` must pass `containsPii = true` (BR-009).~~ Deferred to F-14.
- ~~`GuardrailService` performs a defensive `looksLikePii()` content scan~~ — deferred to F-14.
- Language preference (`languagePref`) — non-sensitive user configuration, not PII.
- ~~Translation cache (`translation_cache` table) stores translated content~~ — deferred to F-14.
- **ServerStrings overrides** (`server_string_overrides` table) — contains notification template text only (no PII). Templates use placeholders like `{student_name}` — the actual PII values are inserted at runtime in memory and never stored in the overrides table.
- **Users-by-language endpoint** (FR-017) — returns user name, phone, school name, language preference. This is Super Admin only (BR-011). Phone numbers are already visible to Super Admin via existing admin endpoints. No additional PII exposure beyond existing admin capabilities.

### Multi-tenant Isolation

- ~~`translation_cache` — **school-scoped** via `school_id` column~~ — deferred to F-14.
- `app_users.languagePref` — user-scoped
- ~~`ContentTranslationService.translate()` accepts `schoolId` parameter~~ — deferred to F-14.
- ~~Cross-school cache sharing is prevented at the database level~~ — deferred to F-14.
- **`server_string_overrides`** — platform-wide (not school-scoped). Super Admin manages translations for all schools. No `school_id` column — overrides apply globally to all notifications.
- **`GET /api/admin/users-by-language`** — returns users from ALL schools (Super Admin only, platform-wide view). No school_id filtering needed — Super Admin has global access.

---

## 17. Performance & Scalability

### Expected Scale

- 10 languages × 1000 strings = 10,000 string resources total
- ~~500 translations per day per school (announcements + notifications)~~ — deferred to F-14
- ~~Translation cache: ~500 entries per day per school, 24-hour TTL~~ — deferred to F-14
- **ServerStrings:** ~24 keys × 10 languages = 240 template entries (compiled) + up to 240 DB overrides (worst case)
- **Users-by-language:** ~17,400 users (current platform scale). `GET /api/admin/users-by-language` returns all users — consider pagination for >50,000 users in future.

### Query Optimization

- ~~Translation cache: `UNIQUE(school_id, content_hash, target_lang)`~~ — deferred to F-14
- User language pref: `app_users.languagePref` — O(1) lookup
- String resources: in-memory map, O(1) lookup
- **ServerStrings overrides:** in-memory `ConcurrentHashMap`, O(1) lookup. DB `UNIQUE(string_key, lang)` index for upsert/delete.
- **Users-by-language:** `app_users` table scan grouped by role. Indexed on `role` + `languagePref`. For >50,000 users, add `WHERE role = ?` filter via `?role=` query param.

### Indexing Strategy

- ~~`translation_cache(school_id, content_hash, target_lang)`~~ — deferred to F-14
- ~~`translation_cache(expires_at)`~~ — deferred to F-14
- ~~`translation_cache(school_id)`~~ — deferred to F-14
- `language_pref_history(user_id, changed_at DESC)` — for user history queries
- `language_pref_history(school_id, changed_at DESC)` — for school switch rate queries
- `server_string_overrides(string_key, lang)` — UNIQUE index for upsert/delete

### Caching Strategy

- ~~Translation cache: 24-hour TTL~~ — deferred to F-14
- User language preference (notifications): Caffeine cache in `UserLanguageResolver`, 10-minute TTL, evicted on preference update. See §9.3 Mechanism A.
- ~~User language preference (content fetches): no server-side cache~~ — deferred to F-14.
- String resources: loaded once per language, cached in memory for app lifetime
- **ServerStrings overrides:** in-memory `ConcurrentHashMap`, loaded at startup, updated on PATCH/DELETE. No TTL — overrides persist until explicitly deleted.

### Pagination

N/A — translation operates on individual content items.

**`GET /api/admin/users-by-language`** — returns all users grouped by role. No pagination in initial release (~17,400 users). If user count exceeds 50,000, add cursor-based pagination with `?cursor=` + `?limit=` params. For now, the `?role=` filter allows fetching one role at a time to reduce response size.

### Connection Pooling

Uses existing HikariCP connection pool. No additional pooling needed.

### Async Processing

- ~~AI translation: async (non-blocking, fallback to English on timeout)~~ — deferred to F-14
- Notification translation: async (fire-and-forget with fallback)
- Language preference sync: async (background, offline queue)
- **ServerStrings override update:** synchronous (DB upsert + in-memory cache update, < 10ms)
- **Users-by-language query:** synchronous (DB query, ~100ms for 17K users)

### Scalability Concerns

- ~~AI translation volume: 500 translations/day/school × 100 schools = 50,000/day. Cache hit rate > 80% reduces AI calls to ~10,000/day. Feasible.~~ — deferred to F-14
- String resource size: 10 languages × ~50KB = 500KB total. Acceptable for mobile app.
- ~~Translation cache growth: 500 entries/day/school × 100 schools = 50,000 entries/day. 24-hour TTL keeps table small.~~ — deferred to F-14
- **ServerStrings overrides:** max 240 rows (24 keys × 10 languages). Negligible DB/memory impact.
- **Users-by-language:** ~17,400 users. Single DB query with GROUP BY role. Acceptable. Add pagination if > 50,000 users.

---

## 18. Edge Cases

| # | Scenario | Expected Behavior |
|---|---|---|
| EC-1 | User selects language not in supported list | Return 400 "Invalid language code." |
| EC-2 | ~~AI translation service unavailable~~ | ~~Fallback to English. Log error.~~ Deferred to F-14 |
| EC-3 | ~~AI translation timeout (> 3 seconds)~~ | ~~Fallback to English. Log timeout.~~ Deferred to F-14 |
| EC-4 | ~~AI returns empty translation~~ | ~~Fallback to English. Log error.~~ Deferred to F-14 |
| EC-5 | String resource missing for selected language | Fallback to English string. Log missing key. |
| EC-6 | User has no `languagePref` set | Default to 'en'. |
| EC-7 | ~~Translation cache miss~~ | ~~Call AI, cache result, return translation.~~ Deferred to F-14 |
| EC-8 | ~~Translation cache expired~~ | ~~Call AI, update cache, return translation.~~ Deferred to F-14 |
| EC-9 | WhatsApp template not available in user's language | Use English template. Log missing template. |
| EC-10 | User changes language while offline | Save locally, sync when online. |
| EC-11 | ~~Content contains proper nouns (school name, person name)~~ | ~~AI prompt excludes proper nouns from translation.~~ Deferred to F-14 |
| EC-12 | ~~Content is already in target language~~ | ~~AI detects and returns as-is. No error.~~ Deferred to F-14 |
| EC-13 | ~~Content is mixed language (English + Hindi)~~ | ~~AI translates English parts to target language.~~ Deferred to F-14 |
| EC-14 | User switches language rapidly | Each switch triggers recomposition. Last selection wins. Server synced with **2-second debounce** — only the final language is sent to `PATCH /api/v1/user/language-pref`. |
| EC-15 | ~~Translation cache table grows large~~ | ~~Hourly cleanup job deletes expired entries (24-hour TTL).~~ Deferred to F-14 |
| EC-16 | App launched for first time (no language pref) | Default to English. **First-launch language selection screen shown before login** (FR-011). User selects language → persists to DataStore → syncs to server on login. See §10.14. |
| EC-17 | ~~Content exceeds 5000 characters~~ | ~~Truncate to 5000 characters for AI translation.~~ Deferred to F-14 |
| EC-18 | ~~Multiple users with same content + language~~ | ~~Cache hit — same translation served.~~ Deferred to F-14 |
| EC-19 | User's `languagePref` is set to a language removed from `SUPPORTED_LANGUAGES` | See §6.12 Removed Language Strategy. |
| EC-20 | Non-Super Admin attempts to access `/admin/language-dashboard` or `/admin/server-strings` | Website shows "Access Denied" card. Server returns 403 Forbidden for API calls. See BR-011. |
| EC-21 | Super Admin updates ServerStrings override with invalid key (not in compiled templates) | Server returns 400 Bad Request: "Invalid string key." Key must exist in `templates["en"]`. |
| EC-22 | Super Admin updates ServerStrings override with invalid language code | Server returns 400 Bad Request: "Invalid language code." Must be one of 10 supported languages. |
| EC-23 | Super Admin deletes a ServerStrings override that doesn't exist | Server returns 404 Not Found: "No override found for (key, lang)." |
| EC-24 | Server restarts after overrides were created | Overrides are persisted in `server_string_overrides` table. `ServerStringOverrideRepository.loadAllIntoCache()` reloads them at startup. No data loss. |
| EC-25 | `GET /api/admin/users-by-language` returns user with no `languagePref` | `languagePref` field shows `'en'` (DB default post-migration 072). `last_changed` shows `null` if no history row exists. |
| EC-26 | Super Admin updates override for a key that has no compiled default in that language | Override is stored and served. If the compiled map doesn't have the key for that language, the override IS the only translation. Works correctly — override-first resolution. |

---

## 19. Error Handling

### Error Response Format

Standard `ApiResponse` error format for language preference API. Internal errors logged.

### Error Codes

| Code | HTTP Status | Description | User Message |
|---|---|---|---|
| `INVALID_LANGUAGE_CODE` | 400 | Language code not in supported list | "Please select a valid language." |
| ~~`TRANSLATION_FAILED`~~ | ~~500~~ | ~~AI translation error~~ | ~~Deferred to F-14~~ |
| `INVALID_STRING_KEY` | 400 | ServerStrings override key not in compiled templates | "Invalid string key." (FR-018) |
| `OVERRIDE_NOT_FOUND` | 404 | No DB override found for (key, lang) on DELETE | "No override found for this key and language." (FR-018) |
| `FORBIDDEN_SUPER_ADMIN_ONLY` | 403 | Non-Super Admin accessing Super Admin endpoint | "Super Admin access required." (FR-017/FR-018) |

### Error Handling Strategy

- ~~**AI translation errors:** Fallback to English. Log error. Never block notification delivery.~~ — deferred to F-14
- **WhatsApp template missing:** Use English template. Log missing template.
- **String resource missing:** Fallback to English string. Log missing key.
- **Language pref sync failure:** Save locally, sync when online.
- ~~**Cache errors:** Bypass cache, call AI directly. Log error.~~ — deferred to F-14
- **ServerStrings override errors:** Return 400 for invalid key/lang, 404 for missing override on DELETE. In-memory cache is updated only after successful DB operation.
- **Super Admin auth failure:** Return 403 Forbidden. Log unauthorized access attempt.

### Retry Strategy

- ~~AI translation: no retry (fallback to English)~~ — deferred to F-14
- Language pref sync: retried when online (offline queue)
- WhatsApp: existing retry logic (3 retries)
- ServerStrings override update: no retry (synchronous, returns error to UI immediately)
- Users-by-language query: no retry (synchronous, returns error to UI immediately)

### Fallback Behavior

- ~~AI unavailable: English content~~ — deferred to F-14
- WhatsApp template missing: English template
- String resource missing: English string
- ~~Cache unavailable: call AI directly (no cache)~~ — deferred to F-14
- Language pref sync failure: local preference used, synced later
- **ServerStrings override deleted:** reverts to compiled Kotlin default, then English
- **ServerStrings override cache miss (key not in overrides map):** falls back to compiled Kotlin default, then English

---

## 20. Analytics & Reporting

### Analytics Dashboard Data

| Metric | Source | Derivation |
|---|---|---|
| Language distribution (per school) | `app_users.languagePref` | Group by language, count, percentage |
| Per-user language preference | `app_users.languagePref` | Direct lookup per user |
| Language switch rate (7d) | `language_pref_history` | Count of rows in last 7 days (excluding `source='migration'`) |
| Language change history (per user) | `language_pref_history` | All rows for a user, ordered by `changed_at DESC` |
| Platform-wide language adoption | `app_users.languagePref` | Group by language across all schools |
| Most popular language (per school) | `app_users.languagePref` | `GROUP BY language ORDER BY count DESC LIMIT 1` |
| ~~Translation volume~~ | ~~`translation_cache` count~~ | ~~Deferred to F-14~~ |
| ~~Translation cache hit rate~~ | ~~Cache hits / total requests~~ | ~~Deferred to F-14~~ |
| ~~AI translation calls~~ | ~~`ContentTranslationService` logs~~ | ~~Deferred to F-14~~ |
| ~~AI translation failures~~ | ~~`ContentTranslationService` logs~~ | ~~Deferred to F-14~~ |
| WhatsApp template usage | WhatsApp gateway logs | Count per language |
| Language switch rate | `language_pref_history` | Count per day |

### Export Capabilities

- Language distribution export (CSV) — user ID, name, language, last changed
- Per-user language history export (CSV) — user ID, old lang, new lang, timestamp, source
- ~~Translation cache stats export (CSV)~~ — deferred to F-14

### API Endpoints

| Endpoint | Auth | Description |
|---|---|---|
| `GET /api/v1/school/language-distribution` | School Admin | Language distribution for admin's school, switch rate, most popular. See §9.3 Analytics & Tracking Endpoints. |
| `GET /api/v1/school/users-language-pref` | School Admin | Per-user language preferences for admin's school. See §9.3. |
| `GET /api/v1/user/language-history` | User (own) | Own language change history. See §9.3. |
| `GET /api/admin/language-adoption` | Super Admin | Platform-wide language adoption, per-school breakdown. See §9.3. |
| ~~`GET /api/admin/translation-cache/stats`~~ | ~~Super Admin~~ | ~~Deferred to F-14~~ |
| `GET /api/admin/users-by-language` | Super Admin | All users grouped by role with language pref. See §9.3. (FR-017) |
| `GET /api/admin/server-strings` | Super Admin | All ServerStrings keys × languages. See §9.3. (FR-018) |
| `PATCH /api/admin/server-strings/{key}` | Super Admin | Update a single ServerStrings translation. See §9.3. (FR-018) |
| `DELETE /api/admin/server-strings/{key}` | Super Admin | Remove a DB override, revert to compiled default. See §9.3. (FR-018) |

### Report Types

| Report | Format | Frequency | Recipient |
|---|---|---|---|
| Language adoption | JSON (API) | On-demand | School Admin |
| Language distribution | JSON (API) | On-demand | School Admin |
| Per-user language preferences | JSON (API) | On-demand | School Admin |
| Platform language adoption | JSON (API) | On-demand | Super Admin |
| ~~Translation metrics~~ | ~~JSON (API)~~ | ~~On-demand~~ | ~~Deferred to F-14~~ |
| ~~AI translation audit~~ | ~~JSON (API)~~ | ~~Weekly~~ | ~~Deferred to F-14~~ |
| Users by language (grouped by role) | JSON (API) | On-demand | Super Admin |
| ServerStrings translations | JSON (API) | On-demand | Super Admin |

---

## 21. Testing Strategy

### Unit Tests

- ~~`ContentTranslationService.translate()`~~ — deferred to F-14
- ~~`ContentTranslationService.translateNotification()`~~ — deferred to F-14
- `UserLanguageResolver.resolve()` — cache hit, cache miss (DB lookup), fallback to 'en' when user not found, cache eviction after `PATCH /api/v1/user/language-pref`
- `LocaleManager.setLocale()` — updates StateFlow, persists to DataStore, syncs to server
- `DateFormatter` — locale-aware date formatting for all 10 languages
- `CurrencyFormatter` — Indian numbering system (₹1,00,000)
- Language code validation — valid codes accepted, invalid rejected
- ~~Translation cache — store, retrieve, expire, cleanup~~ — deferred to F-14
- ~~`TranslationCacheRepository.invalidateByContentHash()`~~ — deferred to F-14
- `AppStrings.getPlural()` — ICU MessageFormat plural resolution for `one`/`other` categories across all 10 languages (e.g., count=0, 1, 3 for `NOTIFICATIONS_COUNT_PLURAL`)
- `icuFormat()` — `{name}` placeholder substitution, `{count, plural, ...}` pattern parsing, fallback to `other` category, fallback to English pattern if key missing
- `appString()` — named placeholder substitution (e.g., `{phone}` in `AUTH_OTP_PROMPT`)
- String key naming — all keys in `StringKeys` follow `{screen}.{widget}.{variant}` convention (automated check)
- `NumberFormatter` — locale-aware digit conversion for integers, percentages, decimals across all 10 languages
- `AppStringsKeyParityTest` — all 10 language maps have same keys as English canonical (FR-015)
- `AppStrings.get()` with `STRING_EXTERNALIZATION_ENABLED=false` — returns English fallback for all keys (rollback flag)
- `LocaleManager` first-launch — no stored `languagePref` → triggers `LanguageSelectionScreen` flow (FR-011)
- `LocaleManager.initLocale()` — stored language not in `SUPPORTED_LANGUAGES` → resets to `'en'` + syncs (§6.12, EC-19)
- `UserLanguageResolver.resolve()` — DB value not in `SUPPORTED_LANGUAGES` → falls back to `'en'` (§6.12)
- `ServerStrings.get(key, lang)` — returns translated value for known key+lang, falls back to English if lang missing, falls back to key if key missing
- `ServerStrings.fill(key, lang, params)` — placeholder substitution works for all `{variable_name}` patterns, handles missing params gracefully (leaves placeholder unfilled)
- `ServerStrings.hasTemplate(type, part)` — returns `true` for known notification types, `false` for unknown
- `LanguagePrefHistoryRepository.record()` — inserts row with correct old/new lang, source
- `ServerStrings.get(key, lang)` — DB override takes priority over compiled default, falls back to English
- `ServerStrings.fill(key, lang, params)` — placeholder substitution with override values
- `ServerStringOverrideRepository` — upsert, delete, getAll, getByKey
- `I18nAnalyticsRouting` — `GET /api/admin/users-by-language` returns users grouped by role with correct language prefs
- `I18nAnalyticsRouting` — `GET /api/admin/server-strings` returns all keys × languages with `is_override` flag
- `I18nAnalyticsRouting` — `PATCH /api/admin/server-strings/{key}` upserts override + updates in-memory cache
- `I18nAnalyticsRouting` — `DELETE /api/admin/server-strings/{key}?lang=` removes override + reverts to compiled default, timestamp
- `LanguagePrefHistoryRepository.getUserHistory()` — returns rows ordered by changed_at DESC, limited
- `LanguagePrefHistoryRepository.getSchoolSwitchCount()` — counts non-migration changes in last N days

### Integration Tests

- Full notification flow: create notification → fetch user lang → resolve via `ServerStrings` → send
- Language preference sync: change locally → sync to server → verify `app_users.languagePref` updated + `language_pref_history` row inserted
- WhatsApp template selection: user with `languagePref = 'hi'` → Hindi template selected
- ~~Cache lifecycle~~ — deferred to F-14
- ~~Content update invalidation~~ — deferred to F-14
- **Analytics endpoints**: `GET /api/v1/school/language-distribution` (School Admin) + `GET /api/v1/school/users-language-pref` (School Admin) + `GET /api/v1/user/language-history` (User) + `GET /api/admin/language-adoption` (Super Admin) — verify auth + response schema
- ~~Accept-Language header translation~~ — deferred to F-14

### E2E Tests

- User changes language in settings → UI switches instantly → all screens show translated strings
- ~~Announcement published in English → user with `languagePref = 'hi'` sees Hindi translation~~ — deferred to F-14
- Notification sent to user with `languagePref = 'bn'` → receives Bengali translation via `ServerStrings` template (via `UserLanguageResolver.resolve()`)
- User changes language via `PATCH /api/v1/user/language-pref` → next notification uses new language (cache evicted) + `language_pref_history` row inserted
- **First app launch (no DataStore)** → language selection screen shown → user selects Hindi → proceeds to login → UI in Hindi (FR-011)
- ~~Content > 5000 chars~~ — deferred to F-14
- **Non-English search** → user types "दिल्ली" in school search → matching schools with "दिल्ली" in name returned (FR-013)
- **Rollback flag** → set `STRING_EXTERNALIZATION_ENABLED=false` → all screens display English (rollback verified)
- **Language tracking** → School Admin views `GET /api/v1/school/language-distribution` → sees correct counts + percentages for their school
- **Per-user language list** → School Admin views `GET /api/v1/school/users-language-pref` → sees each user's current language
- **Language history** → User views `GET /api/v1/user/language-history` → sees their own change history with timestamps

### Performance Tests

- Language switch recomposition: < 500ms
- String resource loading: < 100ms per language
- `ServerStrings.fill()` template resolution: < 1ms (in-memory map lookup + placeholder fill)
- ~~AI translation: < 3 seconds per content~~ — deferred to F-14
- ~~Translation cache lookup: < 10ms~~ — deferred to F-14

### Test Data

- 10 sample strings in all 10 languages
- 5 sample notifications with templated content (fee reminder, attendance alert, link approved, etc.)
- ~~Mock AI service~~ — not needed in initial release (no AI)
- 1 user with `languagePref = 'ur'` (Urdu — not in `SUPPORTED_LANGUAGES`) — for removed language reset test (§6.12)
- Mock WhatsApp gateway
- 3 users with different `languagePref` values in same school — for language distribution endpoint test
- `language_pref_history` rows for 3 users — for switch rate + history endpoint tests

### Test Environment

- Test database with `language_pref_history` table
- ~~Mock AI service for translation~~ — not needed in initial release
- Mock WhatsApp gateway
- Test JWT tokens for parent, teacher, admin roles
- All 10 string resource files for testing

---

## 22. Acceptance Criteria

- [ ] App UI available in 10 languages (English, Hindi, Bengali, Tamil, Telugu, Marathi, Gujarati, Kannada, Malayalam, Punjabi)
- [ ] Language switch in settings works instantly (no app restart)
- [ ] Server-side notification translation via `ServerStrings` pre-translated templates (no AI)
- [ ] Locale-aware date formatting (e.g., "१५ जुलाई" in Hindi)
- [ ] Locale-aware number/currency formatting (₹1,00,000 in Indian format)
- [ ] WhatsApp notifications sent in user's preferred language
- [ ] Fallback to English if `ServerStrings` template or `AppStrings` key missing
- [ ] Language preference synced to server (`app_users.languagePref`)
- [ ] **Login response (`AuthTokenResponse`) includes `languagePref` field** — client initializes `LocaleManager` from login response
- [ ] **`GET /api/v1/user/language-pref` returns current language preference** — used on app launch when DataStore is empty
- [ ] All UI strings externalized to `AppStrings` Kotlin string maps
- [ ] **String keys follow `{screen}.{widget}.{variant}` naming convention** — no collisions across 118 screen files
- [ ] **Pluralized strings use `appPlural()` with ICU MessageFormat** — correct singular/plural in all 10 languages (FR-009)
- [ ] **UI strings use gender-neutral phrasing** — no unnecessary gendered variants (FR-010)
- [ ] Language switcher shows native language names (हिन्दी, not Hindi)
- [ ] ~~Translation cache reduces AI calls (> 80% hit rate)~~ — deferred to F-14
- [ ] ~~AI translation does not translate proper nouns~~ — deferred to F-14 (templates use placeholders, no AI)
- [ ] Offline language change queued for sync via DataStore `language_pref_pending_sync` flag + `NetworkMonitor` retry (BR-008, §10.9)
- [ ] **Rapid language switching syncs with 2-second debounce** — only final selection sent to server (EC-14)
- [ ] **`PATCH` and `GET /api/v1/user/language-pref` return `updated_at` timestamp** — client confirms server sync
- [ ] **Server resolves user language for notifications via `UserLanguageResolver` (DB + Caffeine cache)** (BR-010) — verified by unit test
- [ ] ~~Client sends `Accept-Language` header on all API requests~~ — deferred to F-14
- [ ] **`PATCH /api/v1/user/language-pref` evicts `UserLanguageResolver` cache** — next notification uses new language
- [ ] **`PATCH /api/v1/user/language-pref` inserts row into `language_pref_history`** — old lang, new lang, source, timestamp recorded
- [ ] **First-launch language selection screen shown before login** (FR-011) — only when DataStore has no stored `languagePref`
- [ ] **Locale-aware number formatting** — percentages and scores use locale digits (८५% in Hindi) (FR-012)
- [ ] **Non-English search accepted** — Indic script input matches same-script stored data (FR-013)
- [ ] **Translation quality review process** — all pre-translated strings human-reviewed before release (FR-014)
- [ ] **String resource key parity** — `AppStringsKeyParityTest` passes: all 10 language maps have same keys (FR-015)
- [ ] **Accessibility labels use `appString()`** — no hardcoded English `contentDescription` (NFR-7)
- [ ] **Timestamps displayed in IST** — server stores UTC, client converts for display (NFR-8)
- [ ] **`GET /api/v1/school/language-distribution` returns language distribution** — School Admin only, correct counts + percentages
- [ ] **`GET /api/v1/school/users-language-pref` returns per-user language list** — School Admin only
- [ ] **`GET /api/v1/user/language-history` returns user's own change history** — with timestamps + source
- [ ] **`GET /api/admin/language-adoption` returns platform-wide language stats** — Super Admin only
- [ ] **`GET /api/admin/users-by-language` returns all users grouped by role** — 3 groups (school_admin, teacher, parent) with user name, phone, school name, language, last_changed. Super Admin only (FR-017)
- [ ] **Website `/admin/language-dashboard` renders 3 collapsible dropdowns** — School Admin, Teacher, Parent sections. Each expandable to show user list with language prefs. Super Admin only (FR-017)
- [ ] **`GET /api/admin/server-strings` returns all keys × all 10 languages** — each translation indicates `is_override` (DB override vs compiled default). Super Admin only (FR-018)
- [ ] **`PATCH /api/admin/server-strings/{key}` upserts DB override** — in-memory `ServerStrings` cache updated immediately, next notification uses new value. Super Admin only (FR-018)
- [ ] **`DELETE /api/admin/server-strings/{key}?lang=` removes DB override** — reverts to compiled Kotlin default. 404 if no override exists. Super Admin only (FR-018)
- [ ] **Website `/admin/server-strings` renders editable translation table** — all keys × languages, inline editing, overrides visually distinguished from defaults. Super Admin only (FR-018)
- [ ] **`server_string_overrides` table created with `UNIQUE(string_key, lang)`** — Exposed table registered in `DatabaseFactory` (FR-018)
- [ ] **`STRING_EXTERNALIZATION_ENABLED` flag works** — when `false`, all `appString()` calls return English (rollback)
- [ ] **Removed language resets to English** — user with unsupported `languagePref` sees English on next launch (§6.12, EC-19)
- [ ] **`SchoolsTable.medium` does NOT default `languagePref`** — user at Hindi-medium school defaults to `'en'` unless they explicitly choose Hindi
- [ ] **`PewsCaseFilesTable.parentDraftLang` is independent from `languagePref`** — changing UI language does not change existing PEWS draft languages
- [ ] **PATCH audit log records old + new language** — server-side `language_pref_history` row, not in API response (§16 Audit Logging)
- [ ] **Templated notifications use `ServerStrings` (no AI call)** — fee reminder, attendance alert, link approved/rejected, exam reminder sent in user's language via pre-translated templates (§8.3 ServerStrings)
- [ ] ~~Dynamic notification content uses AI~~ — deferred to F-14. Dynamic content stays English in initial release.

---

## 23. Implementation Roadmap

| Phase | Duration | Tasks |
|---|---|---|
| 1 | 3 days | Externalize all UI strings to `AppStrings` Kotlin string maps (`StringKeys` + English map). **Wasm/JS font rendering pre-check** (§10.13). Add `STRING_EXTERNALIZATION_ENABLED` flag. Incremental PRs (5-10 screens each). |
| 2 | 5 days | Translate string maps to 9 languages (AI-assisted + human review — §10.16) — add `hi`, `bn`, `ta`, `te`, `mr`, `gu`, `kn`, `ml`, `pa` maps to `AppStrings`. `AppStringsKeyParityTest` (§10.17) gates merge. |
| 3 | 2 days | `LocaleManager` + `CompositionLocal` setup. First-launch `LanguageSelectionScreen` (§10.14). |
| 4 | 2 days | Locale-aware formatters (`DateFormatter`, `CurrencyFormatter`, `NumberFormatter`) — expect/actual. IST timezone handling (NFR-6). |
| 5 | 2 days | `ServerStrings.kt` (server-side pre-translated notification templates, all 10 languages) + `LanguagePrefHistoryRepository` + `LanguagePrefHistoryTable` |
| 6 | 1 day | `Notify.kt` integration — `ServerStrings.fill()` for templated messages. Dynamic content stays English (no AI). Uses `UserLanguageResolver.resolve()`. |
| 7 | 2 days | Client UI (language switcher in settings, native language names, first-launch language selection screen) |
| 8 | 1 day | Analytics & tracking endpoints (`GET /api/v1/school/language-distribution`, `GET /api/v1/school/users-language-pref`, `GET /api/v1/user/language-history`, `GET /api/admin/language-adoption`) |
| 8a | 1 day | Super Admin endpoints: `GET /api/admin/users-by-language` (FR-017), `GET /api/admin/server-strings` + `PATCH` + `DELETE` (FR-018). `ServerStringOverridesTable` + migration. `ServerStrings` override cache loading at startup. |
| 8b | 1 day | Website pages: `/admin/language-dashboard` (3 collapsible role dropdowns), `/admin/server-strings` (editable translation table). Nav items added to `ADMIN_NAV`. |
| 9 | 2 days | Tests: unit, integration, E2E, `AppStringsKeyParityTest` |
| ~~F-14~~ | ~~TBD~~ | ~~AI content translation: `ContentTranslationService` + `TranslationCacheRepository` + `translation_cache` table + `Accept-Language` header + batch translation job~~ |

### Pre-Implementation Checklist

- [ ] Verify `app_users.languagePref` field exists and is accessible
- [ ] ~~Verify `AiService` API for translation~~ — not needed in initial release (no AI)
- [ ] Verify WhatsApp templates have language-specific versions
- [ ] Verify CMP `stringResource()` uses system locale (confirmed — not suitable for in-app switcher; using Kotlin string maps instead, see §10.5)
- [ ] Verify ICU/locale-aware formatting available on all platforms (Android, iOS, JVM, web)
- [ ] **Wasm/JS Indic script font rendering check** — render sample text in all 10 scripts on Chrome/Firefox/Safari. If missing glyphs, bundle Noto Sans fonts for web targets (see §10.13)

---

## 24. File-Level Impact Analysis

### Server

| File | Change Type | Description |
|---|---|---|
| `server/.../db/Tables.kt` | Add + Modify | Add `LanguagePrefHistoryTable` + `ServerStringOverridesTable`. Modify `AppUsersTable.languagePref` default from `"hi"` to `"en"` (migration 072). ~~`TranslationCacheTable`~~ deferred to F-14. |
| `server/.../db/DatabaseFactory.kt` | Modify | Register `LanguagePrefHistoryTable` + `ServerStringOverridesTable`. ~~`TranslationCacheTable`~~ deferred to F-14. |
| ~~`server/.../feature/i18n/ContentTranslationService.kt`~~ | ~~**New**~~ | ~~AI content translation service~~ — deferred to F-14. |
| `server/.../feature/i18n/ServerStrings.kt` | **New** | Server-side pre-translated string templates for notifications, system messages, error messages. Same pattern as client `AppStrings` — Kotlin `object` with `Map<String, Map<String, String>>`. Placeholder fill via `{variable_name}` syntax. See §8.3 ServerStrings. |
| `server/.../feature/i18n/UserLanguageResolver.kt` | **New** | Resolves user language for notifications — Caffeine cache (10-min TTL) + DB lookup on `AppUsersTable.languagePref`. Kotlin `object` singleton. See §9.3 Mechanism A. |
| `server/.../feature/i18n/LanguagePrefHistoryRepository.kt` | **New** | Records + queries language preference changes. `record()`, `getUserHistory()`, `getSchoolSwitchCount()`. Kotlin `object` singleton. See §8.4. |
| `server/.../feature/i18n/ServerStringOverridesTable.kt` | **New** | Exposed table definition for `server_string_overrides` — `string_key`, `lang`, `value`, `updated_by`, `updated_at`. `UNIQUE(string_key, lang)`. See §6.2. (FR-018) |
| `server/.../feature/i18n/ServerStringOverrideRepository.kt` | **New** | CRUD for `server_string_overrides` — `upsert(key, lang, value, updatedBy)`, `delete(key, lang)`, `getAll()`, `getByKey(key)`. Kotlin `object` singleton. Updates `ServerStrings` in-memory override cache on upsert/delete. See §8.3. (FR-018) |
| ~~`server/.../feature/i18n/TranslationCacheRepository.kt`~~ | ~~**New**~~ | ~~Translation cache CRUD~~ — deferred to F-14. |
| `server/.../feature/notifications/Notify.kt` | Modify | Notification translation via `ServerStrings.fill()` for templated messages. Dynamic content stays English (no AI). Uses `UserLanguageResolver.resolve()` for recipient language. See §8.3 ServerStrings flow. |
| `server/.../feature/auth/AuthRouting.kt` | Modify | Add `languagePref` field to `AuthTokenResponse` — populated from `AppUsersTable.languagePref` on login. See §9.1. |
| `server/.../feature/user/UserRouting.kt` | Modify | Add `GET /api/v1/user/language-pref` + `PATCH /api/v1/user/language-pref` — PATCH calls `UserLanguageResolver.evict(userId)` + `LanguagePrefHistoryRepository.record()` after DB update. See §9.1. |
| ~~`server/.../Application.kt`~~ | ~~Modify~~ | ~~`acceptedLanguage()` helper~~ — deferred to F-14. |
| `server/.../feature/i18n/I18nAnalyticsRouting.kt` | **New** | `GET /api/v1/school/language-distribution` (School Admin) + `GET /api/v1/school/users-language-pref` (School Admin) + `GET /api/v1/user/language-history` (User) + `GET /api/admin/language-adoption` (Super Admin) + `GET /api/admin/users-by-language` (Super Admin, FR-017) + `GET /api/admin/server-strings` (Super Admin, FR-018) + `PATCH /api/admin/server-strings/{key}` (Super Admin, FR-018) + `DELETE /api/admin/server-strings/{key}` (Super Admin, FR-018). See §9.3 Analytics & Tracking Endpoints. |
| `docs/db/migration_071_language_pref_history.sql` | **New** | DDL: `language_pref_history` table + indexes |
| `docs/db/migration_073_server_string_overrides.sql` | **New** | DDL: `server_string_overrides` table + `UNIQUE(string_key, lang)` constraint. See §6.2. (FR-018) |
| `docs/db/migration_072_language_pref_default.sql` | **New** | DDL: ALTER `app_users.language_pref` default from 'hi' to 'en' + data migration + insert history rows |
| ~~`docs/db/migration_074_translation_cache.sql`~~ | ~~**New**~~ | ~~DDL: `translation_cache` table~~ — deferred to F-14. |
| `docs/db/migration_0NN_removed_lang_reset.sql` | **Conditional** | When a language is removed from `SUPPORTED_LANGUAGES`: `UPDATE app_users SET language_pref = 'en' WHERE language_pref = '{removed_lang}'` + insert history rows. See §6.12. |

### Shared (KMP)

| File | Change Type | Description |
|---|---|---|
| `shared/.../core/locale/AppStrings.kt` | **New** | `StringKeys` object (all string keys, `{screen}.{widget}.{variant}` naming) + `AppStrings` object (string maps per language, `get(key, locale)` + `getPlural(key, locale, count)` with English fallback). Contains all 10 language maps with ICU MessageFormat plural patterns. See §10.5, §10.6. |
| `shared/.../core/locale/LocalLocale.kt` | **New** | `LocalLocale` CompositionLocal + `appString()` + `appPlural()` composable accessors. See §10.5, §10.6. |
| `shared/.../core/locale/IcuFormatter.kt` | **New** + expect/actual | Minimal ICU MessageFormat parser — `{name}` substitution + `{count, plural, ...}` patterns. Pure Kotlin in commonMain. See §10.6. |
| `shared/.../core/locale/LocaleManager.kt` | **New** | Locale state management (StateFlow, DataStore persistence, 2s debounce server sync, offline queue via `language_pref_pending_sync` flag). See §10.4, §10.9. |
| `shared/.../core/locale/NetworkMonitor.kt` | **New** + expect/actual | Connectivity observer — `Flow<Boolean>` for online/offline state. Android: `ConnectivityManager`, iOS: `NWPathMonitor`, JVM/Web: always-online or ping. Used by `LocaleManager` offline queue. See §10.9. |
| `shared/.../core/locale/DateFormatter.kt` | **New** + expect/actual | Locale-aware date formatting |
| `shared/.../core/locale/CurrencyFormatter.kt` | **New** + expect/actual | Currency formatting (Indian numbering) |
| `shared/.../core/locale/NumberFormatter.kt` | **New** + expect/actual | Locale-aware number formatting (integers, percentages, decimals with locale digits). See §10.8, FR-012. |
| `shared/.../core/locale/IndicFontProvider.kt` | **New** + expect/actual | Conditional Indic font bundle for Wasm/JS — returns `null` on Android/iOS/JVM (system fonts), `FontFamily(Noto Sans)` on Wasm/JS if pre-check fails. See §10.13. |
| `shared/.../i18n/domain/model/LanguageModels.kt` | **New** | DTOs, domain models, `SUPPORTED_LANGUAGES` list |
| `shared/.../i18n/domain/repository/LanguageRepository.kt` | **New** | Repository interface |
| `shared/.../i18n/data/remote/LanguageApi.kt` | **New** | HTTP API definitions |
| ~~`shared/.../core/network/buildRefreshClient.kt`~~ | ~~Modify~~ | ~~Add `Accept-Language` header~~ — deferred to F-14. |
| `shared/src/commonTest/.../core/locale/AppStringsKeyParityTest.kt` | **New** | Unit test: all 10 language maps have same keys as English canonical. Missing keys → build failure. See §10.17, FR-015. |

### Client (Compose)

| File | Change Type | Description |
|---|---|---|
| `composeApp/.../ui/v2/screens/school/SchoolSettingsScreenV2.kt` | Modify | Add `VLanguagePicker` below existing `VThemePicker` in the Settings tab. Inject `LocaleManager` via `koinInject()`. |
| `composeApp/.../ui/v2/screens/teacher/TeacherProfileScreenV2.kt` | Modify | Add `VLanguagePicker` below existing `VThemePicker` in the Profile tab. Inject `LocaleManager` via `koinInject()`. |
| `composeApp/.../ui/v2/screens/parent/ParentProfileCardScreenV2.kt` | Modify | Add `VLanguagePicker` in the account-options reveal section (alongside logout / link child / discover schools). Inject `LocaleManager` via `koinInject()`. |
| `composeApp/.../ui/v2/components/VLanguagePicker.kt` | **New** | Reusable language switcher composable — displays current language + opens selection dialog with 10 languages (native names). Follows `VThemePicker` pattern. |
| `composeApp/.../ui/v2/screens/onboarding/LanguageSelectionScreen.kt` | **New** | First-launch language selection screen — shown before login when DataStore has no `languagePref`. See §10.14, FR-011. |
| `composeApp/.../ui/v2/screens/**/*.kt` | Modify | Replace all hardcoded strings with `appString(StringKeys.KEY)` |
| `composeApp/.../App.kt` | Modify | Wrap `NavGraphV2` in `CompositionLocalProvider(LocalLocale provides locale)` — see §10.4. Add first-launch language selection navigation check. See §10.14. |

### Website (Next.js)

| File | Change Type | Description |
|---|---|---|
| `website/src/app/admin/language-dashboard/page.tsx` | **New** | Super Admin language dashboard page — 3 collapsible dropdowns (School Admin, Teacher, Parent). Each section expands to show user list with name, phone, school name, current language, last changed timestamp. Fetches `GET /api/admin/users-by-language`. Super Admin only — checks `session.role === 'super_admin'`. See FR-017. |
| `website/src/app/admin/server-strings/page.tsx` | **New** | Super Admin ServerStrings translation manager — editable table of all keys × 10 languages. Inline editing saves via `PATCH /api/admin/server-strings/{key}`. Delete button removes override via `DELETE`. Overrides visually distinguished from compiled defaults (badge/color). Fetches `GET /api/admin/server-strings`. Super Admin only. See FR-018. |
| `website/src/lib/admin/nav.ts` | Modify | Add 2 nav items: `{ href: '/admin/language-dashboard', label: 'Language Dashboard', icon: IconGlobe, superAdminOnly: true }` and `{ href: '/admin/server-strings', label: 'Server Strings', icon: IconTranslate, superAdminOnly: true }`. |
| `website/src/lib/admin/client.ts` | Modify | Add API client methods: `getUsersByLanguage()`, `getServerStrings()`, `patchServerString(key, lang, value)`, `deleteServerString(key, lang)`. |
| `website/src/lib/admin/types.ts` | Modify | Add TypeScript types: `UsersByLanguageResponse`, `ServerStringsResponse`, `ServerStringEntry`, `ServerStringPatchBody`. |
| `website/src/components/admin/LanguageDashboard.tsx` | **New** | Client component rendering 3 collapsible sections (School Admin, Teacher, Parent). Each section has a header with count + expand/collapse toggle. Expanded view shows table: name, phone, school, language badge, last changed. Search/filter within each section. See FR-017. |
| `website/src/components/admin/ServerStringsManager.tsx` | **New** | Client component rendering editable translation table. Rows = string keys, columns = 10 languages. Inline edit on cell click. Save button per row or auto-save on blur. Override cells show badge. Revert button per override cell. See FR-018. |

---

## 25. Future Enhancements

| # | Enhancement | Priority | Effort | Notes |
|---|---|---|---|---|
| F-1 | Urdu/Arabic support with RTL | Medium | M | RTL layout, Urdu string resources |
| F-2 | Auto-detect language from phone settings | Low | S | Read system locale on first launch |
| F-3 | Translation of user-generated content | Medium | L | Translate posts, messages |
| F-4 | OCR translation of uploaded documents | Low | L | AI-powered OCR + translation |
| F-5 | Language-specific fonts | Low | M | Custom fonts for better rendering |
| F-6 | Voice-based language selection | Low | S | Voice command to change language |
| F-7 | Translation quality feedback | Medium | S | Users rate translation quality |
| F-8 | Bulk announcement translation | Low | S | Pre-translate announcements for all languages on publish |
| F-9 | Language-specific content moderation | Medium | M | AI moderation in user's language |
| F-10 | Dialect support | Low | L | Beyond standard languages to regional dialects |
| F-11 | Cross-script search (transliteration) | Low | M | Search "Delhi" → match "दिल्ली" via transliteration (Aksharamukha or similar). See §10.15. |
| F-12 | CLDR locale-aware collation | Low | M | Replace Unicode code-point sort with CLDR collation for proper Indic script ordering (NFR-10). |
| F-13 | Remote string config (server-hosted UI strings) | Medium | L | Server hosts all UI string resources (`AppStrings` content). Client fetches at app launch via `GET /api/v1/i18n/strings?lang=hi` and caches locally. Enables hot-updates of translations without app release. See §25.1 below. |
| F-14 | AI Content Translation | High | L | AI-powered translation of dynamic content (announcements, custom notification bodies). `ContentTranslationService` + `TranslationCacheRepository` + `translation_cache` table + `Accept-Language` header mechanism (§9.3 Mechanism B) + batch translation job + cache cleanup job. Replaces "English only" for dynamic content with AI-translated content. See §25.2 below. |

### 25.1 Remote String Config (F-13 — Future Enhancement)

**Problem:** In the initial release, UI strings are compiled into the app binary (`AppStrings.kt`). Fixing a translation error requires an app release (Play Store / App Store review cycle). This is slow for translation fixes.

**Solution:** Server hosts the canonical string resources. Client fetches them at app launch and caches locally. Translation fixes ship via server deploy (minutes), not app release (days).

**Architecture:**

```
Server (source of truth)                          Client (cached consumer)
┌──────────────────────────────┐                 ┌──────────────────────────────┐
│ ServerStrings.kt             │                 │ AppStrings.kt (compiled      │
│  └─ UI strings (all langs)   │                 │  fallback — shipped in app)  │
│  └─ Notification templates   │                 │                              │
│  └─ Error messages           │                 │ RemoteStrings.kt (fetched    │
│                              │                 │  from server, cached locally)│
│ GET /api/v1/i18n/strings     │ ──JSON──────→  │  └─ DataStore cache          │
│   ?lang=hi&version=v3        │                 │  └─ Falls back to AppStrings │
│                              │                 │      if fetch fails          │
│ POST /api/v1/i18n/strings    │                 │                              │
│   (admin edits translations) │                 │ appString(key) →             │
│                              │                 │   RemoteStrings.get(key)     │
│ translation_strings table    │                 │     ?: AppStrings.get(key)   │
│  (key, lang, value, version) │                 │      ?: key                  │
└──────────────────────────────┘                 └──────────────────────────────┘
```

**API:**

```
GET /api/v1/i18n/strings?lang=hi&version=v3
  Auth: JWT (requireAuth) or anonymous (pre-login strings needed)
  → 200: {
      "version": 3,
      "lang": "hi",
      "strings": {
        "auth.login.title": "लॉगिन",
        "auth.login.subtitle": "अपना फ़ोन नंबर दर्ज करें",
        "parent.dashboard.greeting": "नमस्ते, {name}",
        ...
      }
    }
  → 304: Not Modified (if client's version matches server's latest)
```

**Client flow:**

```kotlin
// shared/.../core/locale/RemoteStrings.kt

object RemoteStrings {
    private var remoteStrings: Map<String, String>? = null  // key → translated value
    private var version: Int = 0

    suspend fun fetch(lang: String, currentVersion: Int): Boolean {
        return try {
            val response = httpClient.get("/api/v1/i18n/strings") {
                parameter("lang", lang)
                parameter("version", currentVersion)
            }
            if (response.status == HttpStatusCode.NotModified) {
                // Server version unchanged, keep cached strings
                return false
            }
            val data = response.body<RemoteStringsResponse>()
            remoteStrings = data.strings
            version = data.version
            // Persist to DataStore for offline use
            preferenceRepository.setRemoteStrings(data.strings, data.version)
            true
        } catch (e: Exception) {
            // Fetch failed — use cached strings from DataStore, or fall back to compiled AppStrings
            val cached = preferenceRepository.getRemoteStrings()
            remoteStrings = cached?.strings
            version = cached?.version ?: 0
            false
        }
    }

    fun get(key: String): String? = remoteStrings?.get(key)
}

// Modified appString() — checks remote first, falls back to compiled
@Composable
fun appString(key: String): String {
    val locale = LocalLocale.current
    return RemoteStrings.get(key)
        ?: AppStrings.get(key, locale)  // compiled fallback
        ?: key  // bug indicator
}
```

**Server-side storage:**

```sql
-- migration_0NN_translation_strings.sql
CREATE TABLE IF NOT EXISTS translation_strings (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    key         VARCHAR(128) NOT NULL,      -- e.g., "auth.login.title"
    lang        VARCHAR(8)  NOT NULL,       -- en, hi, bn, ...
    value       TEXT        NOT NULL,       -- translated text
    version     INT         NOT NULL DEFAULT 1,
    updated_at  TIMESTAMP   NOT NULL DEFAULT now(),
    UNIQUE(key, lang)
);

CREATE INDEX idx_translation_strings_lang_version
    ON translation_strings(lang, version);
```

**Admin UI:** School Admin (or Super Admin) can edit translations via a dashboard:
- `GET /api/v1/i18n/strings?lang=hi` — view all strings for a language
- `PATCH /api/v1/i18n/strings/{key}` — update a single string (bumps version)
- `POST /api/v1/i18n/strings/publish` — publish all pending changes (bumps global version)

**Key design decisions:**
- **Compiled `AppStrings` stays as fallback.** If the server is unreachable or the fetch fails, the app uses the strings compiled into the binary. This ensures the app always works offline.
- **Version-based caching.** Client sends `?version=v3` — server returns `304 Not Modified` if no changes since that version. Minimizes bandwidth.
- **DataStore persistence.** Fetched strings are persisted to DataStore so they survive app restarts. On next launch, client checks version → fetches only if newer.
- **No per-school customization (initial).** String resources are platform-wide, not school-specific. School-specific strings (school name, etc.) are already dynamic data, not string resources.
- **Phase 2+ timeline.** This is a future enhancement. Initial release uses compiled `AppStrings` only. Remote string config is additive — it layers on top without changing the existing `appString()` API contract.
- **Security:** Translation edits are Super Admin only (platform-level, not school-scoped). String resources are not multi-tenant — all schools share the same UI translations.

### 25.2 AI Content Translation (F-14 — Future Enhancement)

**Problem:** In the initial release, dynamic content (announcements, custom notification bodies, custom teacher messages) stays in English. Users with `languagePref != 'en'` see English for all dynamic content. Only templated notifications are translated (via `ServerStrings`).

**Solution:** AI-powered translation of dynamic content at runtime. `ContentTranslationService` calls `AiService.complete()` with a translation prompt, caches results in `translation_cache` table (24h TTL, school-scoped), and falls back to English on failure.

**What F-14 adds:**

| Component | Purpose |
|---|---|
| `ContentTranslationService.kt` | AI translation orchestration — calls `AiService.complete()` with translation prompt, checks cache first, falls back to English on failure |
| `TranslationCacheRepository.kt` | Cache CRUD — `get()`, `put()`, `invalidateByContentHash()`, `invalidateBySchool()` |
| `translation_cache` table | DB cache — `(school_id, content_hash, target_lang)` → `translated_text`, 24h TTL |
| `Accept-Language` header (§9.3 Mechanism B) | Client sends `Accept-Language` header on content fetches; server translates dynamic content via `ContentTranslationService` |
| Batch translation job | Async post-publish: translate announcement to all 9 non-English languages, store in cache |
| Cache cleanup job | Hourly: delete expired `translation_cache` rows |
| `GET /api/admin/translation-cache/stats` | Super Admin analytics: cache entry count, hit rate, by-language breakdown |

**What F-14 does NOT change:**
- `ServerStrings` stays for templated notifications (pre-translated, no AI, no cost)
- `AppStrings` stays for UI strings (compiled, no AI)
- `language_pref_history` stays for tracking (already implemented in initial release)
- All initial-release APIs, endpoints, and tracking mechanisms remain unchanged

**Hybrid flow after F-14:**
1. Notification title → `ServerStrings.fill()` (pre-translated, no AI)
2. Notification body (templated) → `ServerStrings.fill()` (pre-translated, no AI)
3. Notification body (dynamic) → `ContentTranslationService.translate()` (AI + cache)
4. Announcement content → `ContentTranslationService.translate()` (AI + cache, via `Accept-Language` header)

**AI translation prompt** (from Appendix C):
- System: "Translate to {target_language}. Maintain tone. Don't translate proper nouns. Gender-neutral phrasing."
- User: content to translate
- `containsPii = true` — restricts to no-training providers
- `temperature = 0.3` — faithful translation
- `AiLane.FAST_CHAT` — low-latency lane

**Dependencies:**
- `AiService` already exists (multi-provider LLM gateway with circuit breaker)
- `translation_cache` table DDL: `migration_074_translation_cache.sql` (documented in §6, Appendix E)
- `Accept-Language` header: modify `buildRefreshClient.kt` (client) + add `acceptedLanguage()` helper (server)

**Priority:** High — this is the #1 future enhancement for multi-language. Dynamic content in English is the main gap in the initial release.

---

## Appendix A: Sequence Diagrams

### A.1 User Changes Language

```
User (app)       LocaleManager       DataStore       Server
  │                  │                  │              │
  │  Select "हिन्दी"   │                  │              │
  │  ──────────────> │                  │              │
  │                  │──update StateFlow│              │
  │  ←──recomposition│                  │              │
  │                  │──persist "hi"──→ │              │
  │                  │                  │              │
  │                  │──PATCH /language-pref─────────→│
  │                  │←──200 OK───────────────────────│
  │                  │                  │              │
  │  UI now in Hindi │                  │              │
  │                  │                  │              │
```

### A.2 Notification Translation Flow (Initial Release — No AI)

```
Notify.kt    UserLanguageResolver    ServerStrings    NotificationService
  │                    │                    │                  │
  │  send notification │                    │                  │
  │  ────────────────> │                    │                  │
  │                    │──fetch user lang──│                  │
  │                    │←──"hi"───────────│                  │
  │                    │                    │                  │
  │                    │  [if lang == 'en'] │                  │
  │                    │──send as-is─────────────────────────────→│
  │                    │                    │                  │
  │                    │  [if lang != 'en'] │                  │
  │                    │  [if hasTemplate]  │                  │
  │                    │──ServerStrings.fill(key, "hi", params)→│
  │                    │←──translated title + body────────────│
  │                    │──send translated────────────────────────→│
  │                    │                    │                  │
  │                    │  [if !hasTemplate] │                  │
  │                    │──send English (dynamic content)────────→│
  │                    │                    │                  │
```

~~A.3 Announcement Translation Flow~~ — deferred to F-14.

---

## Appendix B: Domain Model / ER Diagram

```
┌──────────────────────────────────────────────────────────────────────┐
│                          app_users                                     │
│  id (PK)                                                              │
│  phone, role, isPhoneVerified                                         │
│  languagePref (existing: VARCHAR, default 'hi' → migrate to 'en')     │
│  ...                                                                  │
└──────────────────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────────────────┐
│                    language_pref_history (new)                         │
│  id (PK)                                                              │
│  user_id (FK app_users)                                               │
│  school_id (denormalized, nullable)                                   │
│  old_lang (nullable — NULL on first set)                              │
│  new_lang (NOT NULL)                                                  │
│  changed_at (NOT NULL)                                                │
│  source ('app' | 'first_launch' | 'migration' | 'admin_reset')        │
│  Indexes: (user_id, changed_at DESC), (school_id, changed_at DESC)   │
└──────────────────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────────────────┐
│                    server_string_overrides (new)                       │
│  id (PK)                                                              │
│  string_key (VARCHAR 128)                                             │
│  lang (VARCHAR 8)                                                     │
│  value (TEXT)                                                         │
│  updated_by (FK app_users, nullable — Super Admin)                    │
│  updated_at (NOT NULL)                                                │
│  UNIQUE: (string_key, lang)                                           │
└──────────────────────────────────────────────────────────────────────┘

~~translation_cache~~ — deferred to F-14

String Maps (in-app, Kotlin code, not in DB):
┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐
│ AppStrings.en    │  │ AppStrings.hi    │  │ AppStrings.pa    │
│ Map<String,String>│  │ Map<String,String>│  │ Map<String,String>│
│ (English)        │  │ (Hindi)          │  │ (Punjabi)        │
└──────────────────┘  └──────────────────┘  └──────────────────┘
        ...10 language maps total...
        Resolved via appString(key) + LocalLocale

┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐
│ ServerStrings.en │  │ ServerStrings.hi │  │ ServerStrings.pa │
│ Map<String,String>│  │ Map<String,String>│  │ Map<String,String>│
│ (notification    │  │ (notification    │  │ (notification    │
│  templates)      │  │  templates)      │  │  templates)      │
└──────────────────┘  └──────────────────┘  └──────────────────┘
        ...10 language maps total...
        Resolved via ServerStrings.fill(key, lang, params)
```

---

## Appendix C: Event Flow

### Domain Events

| Event | Emitter | Consumers | Payload | Side Effects |
|---|---|---|---|---|
| `LanguagePreferenceChanged` | `LocaleManager.setLocale()` | Server sync, `LanguagePrefHistoryRepository` | `userId, oldLang, newLang, source` | `app_users.languagePref` updated, `language_pref_history` row inserted |
| `ServerStringOverrideUpdated` | `I18nAnalyticsRouting` (Super Admin) | `ServerStringOverrideRepository`, `ServerStrings` cache | `key, lang, oldValue, newValue, updatedBy` | `server_string_overrides` row upserted, in-memory cache updated |
| `ServerStringOverrideDeleted` | `I18nAnalyticsRouting` (Super Admin) | `ServerStringOverrideRepository`, `ServerStrings` cache | `key, lang, deletedBy` | `server_string_overrides` row deleted, in-memory cache reverted to compiled default |
| ~~`ContentTranslated`~~ | ~~`ContentTranslationService.translate()`~~ | ~~None (logged)~~ | ~~`contentHash, targetLang, cacheHit`~~ | ~~Deferred to F-14~~ |
| ~~`TranslationFailed`~~ | ~~`ContentTranslationService.translate()`~~ | ~~None (logged)~~ | ~~`contentHash, targetLang, error`~~ | ~~Deferred to F-14~~ |
| ~~`TranslationCacheHit`~~ | ~~`ContentTranslationService.translate()`~~ | ~~None (logged)~~ | ~~`contentHash, targetLang`~~ | ~~Deferred to F-14~~ |
| ~~`TranslationCacheExpired`~~ | ~~`TranslationCacheCleanupJob`~~ | ~~None (logged)~~ | ~~`count`~~ | ~~Deferred to F-14~~ |

### Event Delivery Guarantees

- Events emitted synchronously within service methods
- All events logged for audit
- No external consumers — events are internal audit trail

### AI Translation Prompt (deferred to F-14)

> **Not used in initial release.** Documented here for reference. `ServerStrings` handles all server-side translation via pre-translated templates — no AI calls.

Passed as `List<LlmMessage>` to `AiService.complete()`. The system message includes the target language and proper-noun instruction; the user message contains the content to translate.

```
LlmMessage(role = "system", content = """
    Translate the following text to {{target_language}}. Maintain tone and context.
    Do not translate proper nouns (school names, person names). Keep it natural and concise.
    Use gender-neutral phrasing where possible (e.g., "child" instead of "son/daughter").
""".trimIndent())

LlmMessage(role = "user", content = {{content}})
```

**API call parameters:**
- `feature = "translation"` — usage logging tag
- `lane = AiLane.FAST_CHAT` — low-latency lane for short content
- `containsPii = true` — restricts to no-training providers (notifications carry PII)
- `temperature = 0.3` — lower temperature for faithful translation
- `maxTokens = 2048` — sufficient for announcement/notification translation
- `schoolId` — scopes L1 cache and usage logging

### Supported Languages

| Code | Language | Script |
|---|---|---|
| en | English | Latin |
| hi | Hindi | Devanagari |
| bn | Bengali | Bengali |
| ta | Tamil | Tamil |
| te | Telugu | Telugu |
| mr | Marathi | Devanagari |
| gu | Gujarati | Gujarati |
| kn | Kannada | Kannada |
| ml | Malayalam | Malayalam |
| pa | Punjabi | Gurmukhi |

### Translation Pipeline

| Content Type | Translation Method (Initial Release) | Translation Method (After F-14) |
|---|---|---|
| UI strings | Pre-translated string resources (`AppStrings`) | Same (F-13 may add remote config) |
| Notification titles + templated bodies | Pre-translated templates (`ServerStrings`) | Same |
| Notification bodies (dynamic) | **English only** (no AI) | AI via `ContentTranslationService` + cache |
| Announcements | **English only** (no AI) | AI via `ContentTranslationService` + cache + `Accept-Language` header |
| Report cards | AI translation (from `AI_REPORT_CARD_SPEC.md`) | Same |
| WhatsApp templates | Pre-approved Meta templates per language | Same |

---

## Appendix D: Configuration

### Environment Variables

| Variable | Default | Description |
|---|---|---|
| `MULTI_LANGUAGE_ENABLED` | `true` | Enable/disable multi-language feature |
| ~~`TRANSLATION_CACHE_TTL_HOURS`~~ | ~~`24`~~ | ~~Deferred to F-14~~ |
| ~~`TRANSLATION_AI_TIMEOUT_MS`~~ | ~~`3000`~~ | ~~Deferred to F-14~~ |
| ~~`TRANSLATION_CACHE_CLEANUP_CRON`~~ | ~~`0 0 * * * *`~~ | ~~Deferred to F-14~~ |
| `SUPPORTED_LANGUAGES` | `en,hi,bn,ta,te,mr,gu,kn,ml,pa` | Comma-separated supported language codes |
| ~~`TRANSLATION_RATE_LIMIT_PER_HOUR`~~ | ~~`1000`~~ | ~~Deferred to F-14~~ |

### Feature Flags

| Flag | Default | Description |
|---|---|---|
| `MULTI_LANGUAGE_ENABLED` | `true` | Enable/disable multi-language feature |
| ~~`AI_TRANSLATION_ENABLED`~~ | ~~`true`~~ | ~~Deferred to F-14~~ |
| ~~`TRANSLATION_CACHE_ENABLED`~~ | ~~`true`~~ | ~~Deferred to F-14~~ |
| `WHATSAPP_MULTI_LANGUAGE_ENABLED` | `true` | Enable/disable WhatsApp multi-language templates |
| `STRING_EXTERNALIZATION_ENABLED` | `true` | **Migration rollback flag.** When `false`, screens use hardcoded English strings instead of `appString()`. Used during Phase 1 migration to roll back individual screens if string externalization breaks. See Appendix E §String Resource Migration Rollback. |

### School-Level Settings

- `SchoolsTable.medium` — existing field (VARCHAR(32), NOT NULL). Stores human-readable medium of instruction (e.g., "English", "Hindi"). **Not** a language code — does not map to `languagePref` codes (en, hi, etc.). Informational only; does not override user `languagePref`.
  - **Not used for defaults:** `medium` is **not** used to initialize or default a user's `languagePref`. A user at a "Hindi-medium" school still defaults to `'en'` (BR-001) unless they explicitly select Hindi via the language switcher or first-launch selection screen (FR-011). The rationale: the app UI language is the user's personal preference, not the school's instructional medium. A Hindi-medium school may have English-speaking parents and vice versa.
  - **Existing conflation bug:** `ReportRollupService.kt:77` reads `SchoolsTable.medium` and falls back to `"en"` as if it were a language code (`it[SchoolsTable.medium] ?: "en"`). Since `medium` stores values like `"English"` (not `"en"`), this fallback is semantically incorrect. The multi-language implementation must not repeat this pattern. If a school-level language code is needed, derive it via a mapping (e.g., `"English" → "en"`, `"Hindi" → "hi"`) or use `SchoolPhilosophyTable.primaryLanguage` instead. See §15 Internal Integrations.

---

## Appendix E: Migration & Rollback

### Migration: `migration_071_language_pref_history.sql`

```sql
-- Migration 071: Language Preference History Table
-- Tracks every language preference change for analytics and audit

BEGIN;

CREATE TABLE IF NOT EXISTS language_pref_history (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID NOT NULL,
    school_id   UUID,
    old_lang    VARCHAR(8),
    new_lang    VARCHAR(8) NOT NULL,
    changed_at  TIMESTAMP NOT NULL DEFAULT now(),
    source      VARCHAR(16) NOT NULL DEFAULT 'app'
);

CREATE INDEX IF NOT EXISTS idx_lang_pref_history_user
    ON language_pref_history (user_id, changed_at DESC);

CREATE INDEX IF NOT EXISTS idx_lang_pref_history_school
    ON language_pref_history (school_id, changed_at DESC);

COMMIT;
```

### Migration: `migration_072_language_pref_default.sql`

```sql
-- Migration 072: Fix language_pref column default
-- Changes app_users.language_pref default from 'hi' to 'en'
-- to align with English-first design (BR-001, EC-6, EC-16).
-- Safe: multi-language feature not yet implemented; all existing
-- 'hi' values are from the column default, not user choice.

BEGIN;

-- 1. Change column default
ALTER TABLE app_users
    ALTER COLUMN language_pref SET DEFAULT 'en';

-- 2. Migrate existing rows with default 'hi' to 'en'
--    (No user has explicitly chosen a language — feature not yet implemented)
UPDATE app_users SET language_pref = 'en' WHERE language_pref = 'hi';

-- 3. Insert history rows for migrated users
INSERT INTO language_pref_history (user_id, school_id, old_lang, new_lang, source)
SELECT id, school_id, 'hi', 'en', 'migration'
FROM app_users
WHERE language_pref = 'en';

COMMIT;
```

### Rollback: `migration_072_rollback.sql`

```sql
BEGIN;
-- Restore original column default (data migration is one-way;
-- 'en' is the correct value for all existing users)
ALTER TABLE app_users
    ALTER COLUMN language_pref SET DEFAULT 'hi';
COMMIT;
```

### Migration: `migration_073_server_string_overrides.sql`

```sql
-- Migration 073: Server String Overrides Table
-- Allows Super Admin to update ServerStrings translations from the website
-- without a server redeploy. DB overrides take priority over compiled Kotlin defaults.

BEGIN;

CREATE TABLE IF NOT EXISTS server_string_overrides (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    string_key   VARCHAR(128) NOT NULL,
    lang         VARCHAR(8) NOT NULL,
    value        TEXT NOT NULL,
    updated_by   UUID,
    updated_at   TIMESTAMP NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_string_key_lang
    ON server_string_overrides (string_key, lang);

COMMIT;
```

### String Resource Migration Rollback

Phase 1 externalizes ~1000 hardcoded strings across 118 screen files to `AppStrings` Kotlin string maps. If externalization breaks a screen (missing key, wrong string, layout overflow), the `STRING_EXTERNALIZATION_ENABLED` feature flag provides a rollback mechanism.

**How it works:**

```kotlin
// shared/.../core/locale/AppStrings.kt

object AppStrings {
    private val externalizationEnabled: Boolean =
        AppConfig.getBoolean("STRING_EXTERNALIZATION_ENABLED", default = true)

    fun get(key: String, locale: String): String {
        if (!externalizationEnabled) {
            // Return the hardcoded English fallback for this key
            // Each key maps to its original English string
            return englishFallback[key] ?: key
        }
        return (maps[locale] ?: maps["en"]!!)[key] ?: (maps["en"]!!)[key] ?: key
    }
}
```

**Rollback levels:**

| Level | Scope | Action |
|---|---|---|
| **Per-screen** | Single screen broken | Add `@StringExternalization("screen_name")` annotation to screen. If screen is in `DISABLED_SCREENS` set, `appString()` returns English fallback for that screen's keys. Other screens unaffected. |
| **Global** | Multiple screens broken | Set `STRING_EXTERNALIZATION_ENABLED=false` in env config. All `appString()` calls return English. App functions in English-only mode (same as pre-migration). |
| **Full revert** | Catastrophic failure | Revert the git commit that introduced `AppStrings`. All screens return to hardcoded strings. No DB migration needed — `app_users.languagePref` column is harmless if unused. |

**Migration strategy (Phase 1):**
1. Externalize screens incrementally (5-10 per PR, not all 118 at once)
2. Each PR adds keys to `StringKeys` + English map only
3. `AppStringsKeyParityTest` (§10.17) runs in CI — catches missing keys before merge
4. If a screen breaks in production: add screen to `DISABLED_SCREENS` set → hotfix → re-enable
5. After all 118 screens externalized + tested: remove `STRING_EXTERNALIZATION_ENABLED` flag

**Note:** This flag is temporary — removed after Phase 1 is complete and all screens are verified. It is not a permanent feature flag.

### Migration Validation

- Verify `translation_cache` table created with correct columns
- Verify `UNIQUE(school_id, content_hash, target_lang)` constraint created
- Verify `idx_translation_cache_expires` index created
- Verify `idx_translation_cache_school` index created
- Run `SELECT count(*) FROM translation_cache` — should be 0 (new feature)
- Verify `app_users.languagePref` column default is now 'en': `SELECT column_default FROM information_schema.columns WHERE table_name = 'app_users' AND column_name = 'language_pref'` — should return `'en'::character varying`
- Verify no rows have `language_pref = 'hi'` (unless explicitly set by a user after multi-language feature is implemented): `SELECT count(*) FROM app_users WHERE language_pref = 'hi'` — should be 0 immediately after migration

---

## Appendix F: Observability

### Structured Logging

| Log Level | Event | Context Fields |
|---|---|---|
| INFO | Language preference changed | `userId, oldLang, newLang, source` |
| ~~INFO~~ | ~~Content translated~~ | ~~`contentHash, targetLang, cacheHit, durationMs`~~ — deferred to F-14 |
| ~~INFO~~ | ~~Translation cache hit~~ | ~~`contentHash, targetLang`~~ — deferred to F-14 |
| ~~INFO~~ | ~~Translation cache cleanup~~ | ~~`entriesDeleted, durationMs`~~ — deferred to F-14 |
| ~~WARN~~ | ~~Translation fallback to English~~ | ~~`contentHash, targetLang, reason`~~ — deferred to F-14 |
| WARN | String resource missing | `key, lang, fallbackToEnglish` |
| WARN | WhatsApp template missing for language | `templateId, lang, fallbackToEnglish` |
| ~~ERROR~~ | ~~AI translation service unavailable~~ | ~~`error, stackTrace`~~ — deferred to F-14 |
| ERROR | Language pref sync failed | `userId, error` |

### Metrics

| Metric | Type | Labels | Description |
|---|---|---|---|
| `language_preference_distribution` | Gauge | `language` | User count per language |
| ~~`translations_total`~~ | ~~Counter~~ | ~~`school_id, target_lang, source (cache/ai)`~~ | ~~Deferred to F-14~~ |
| ~~`translation_cache_hit_rate`~~ | ~~Gauge~~ | ~~—~~ | ~~Deferred to F-14~~ |
| ~~`translation_ai_duration`~~ | ~~Histogram~~ | ~~—~~ | ~~Deferred to F-14~~ |
| ~~`translation_failures_total`~~ | ~~Counter~~ | ~~`reason (timeout/error/empty)`~~ | ~~Deferred to F-14~~ |
| `language_switches_total` | Counter | `old_lang, new_lang` | Language switches per day |
| `whatsapp_templates_by_language` | Counter | `template_id, lang` | WhatsApp template usage by language |

### Health Checks

| Check | Endpoint | Description |
|---|---|---|
| Multi-language enabled | `/health/i18n` | Verify feature flag enabled |
| ~~AI translation service~~ | ~~`/health/ai-translation`~~ | ~~Deferred to F-14~~ |

### Alerts

| Alert | Condition | Severity | Notification |
|---|---|---|---|
| ~~AI translation failure rate high~~ | ~~`translation_failures_total` > 10%~~ | ~~Warning~~ | ~~Deferred to F-14~~ |
| ~~Translation cache hit rate low~~ | ~~Cache hit rate < 50%~~ | ~~Warning~~ | ~~Deferred to F-14~~ |
| ~~AI translation latency~~ | ~~`translation_ai_duration` > 5 seconds~~ | ~~Warning~~ | ~~Deferred to F-14~~ |
| Language pref sync failures | Sync failure rate > 5% | Warning | Email to dev team |

### Dashboards

| Dashboard | Panels | Audience |
|---|---|---|
| Language Adoption | User distribution by language, switch rate, most popular | Product Team |
| ~~Translation Metrics~~ | ~~Volume, cache hit rate, AI latency, failure rate~~ — deferred to F-14 | ~~Dev Team~~ |
| WhatsApp by Language | Template usage by language, missing templates | Dev Team |
| ~~AI Translation Audit~~ | ~~Sample translations, quality metrics~~ — deferred to F-14 | ~~Dev Team~~ |

### Risk Analysis

| Risk | Likelihood | Impact | Mitigation |
|---|---|---|---|
| ~~AI translation quality poor~~ | ~~Medium~~ | ~~Medium~~ | ~~Deferred to F-14. Initial release uses pre-translated templates only.~~ |
| ~~AI service unavailable~~ | ~~Medium~~ | ~~Low~~ | ~~Deferred to F-14. No AI in initial release.~~ |
| String resource translation errors | Low | Medium | Human review of all 10 language files before release. |
| Rendering issues with Indic scripts | Medium | Medium | **Pre-implementation check required for Wasm/JS target.** Android, iOS, JVM have system Indic fonts (Devanagari, Tamil, Bengali, etc.). Wasm/JS in browsers depends on OS-installed fonts — Linux server environments and some browsers may lack Indic script support. If Wasm/JS rendering fails, bundle a minimal Indic font (e.g., Noto Sans Devanagari ~200KB) for web targets only. See §10.13. |
| WhatsApp template approval delays | Medium | Low | Start Meta approval process early. English fallback. |
| Dynamic content not translated (English only) | High | Low | **Known limitation of initial release.** Templated notifications are translated via `ServerStrings`. Dynamic content (announcements) stays English. AI translation is F-14. Documented in user stories, acceptance criteria, and §25.2. |
| ~~Translation cache stale~~ | ~~Low~~ | ~~Low~~ | ~~Deferred to F-14. No translation cache in initial release.~~ |
| App size increase from string resources | Low | Low | 10 languages × ~50KB = 500KB. Acceptable. |
