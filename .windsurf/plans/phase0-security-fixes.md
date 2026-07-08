# Phase 0 Security Blocker Fixes — Implementation Plan

## Overview
Implement all 12 Phase 0 security fixes from GOD_MODE_AUDIT_v2.md in topological order (Layer 0 → 1 → 2 → 3).

## Topological Order

### Layer 0 — Foundation (shared isProduction + standalone config fixes)

**FS-008: Remove seed-credentials from repo**
- Add `seed-credentials-*.md` to `.gitignore`
- `git rm --cached seed-credentials-2026-06-07.md` (keep file locally, remove from tracking)

**SEC-044: JWT dev fallback secret**
- Create `core/RuntimeEnvironment.kt` — shared `isProduction` determination (APP_ENV → DATABASE_URL fallback, fail-closed if ambiguous)
- Remove `DEV_SECRET_FALLBACK` constant from `JwtConfig.kt`
- Production: require `JWT_SECRET` env var, throw if missing
- Dev: generate ephemeral secret via `SecureRandom`, log warning

**SEC-019: AI encryption key fail-closed**
- Modify `EncryptionService.kt` to use `RuntimeEnvironment.isProduction`
- Production + no `AI_ENCRYPTION_KEY` → throw on startup
- Production + existing `plain:` prefixed keys in DB → throw on startup
- Dev: allow plaintext, log prominent warning

**AUTH-015: CORS fail-closed in prod**
- Modify `Application.kt` CORS config to use `RuntimeEnvironment.isProduction`
- Production + no `CORS_ALLOWED_ORIGINS` → empty allowlist (reject all cross-origin), log warning
- Dev: `anyHost()` with warning log

### Layer 1 — Shared Utilities + Standalone Fixes

**SEC-011: SSRF in fetchImageAsBase64**
- Create `core/UrlValidator.kt` — block internal IPs (127.x, 10.x, 172.16-31.x, 192.168.x, 169.254.x, ::1, fc00::/7), require HTTP(S) schemes, HTTPS-only in prod
- Apply to `fetchImageAsBase64` in `TeacherSyllabusRouting.kt`
- Lateral: verify `LibraryCoverService.validateStorageUrl` (already has partial SSRF prevention)

**SEC-012: File size validation (streaming)**
- Create `core/FileUploadValidator.kt` with `readBytesWithLimit(input, maxBytes)` that reads in chunks and throws if exceeding limit
- Apply to: `AlumniRouting.kt`, `MediaRouting.kt`, `LibraryRouting.kt`, `MessageAttachmentUpload.kt`

**SEC-013: MIME type validation (magic bytes)**
- Add `validateMagicBytes(bytes, claimedContentType)` to `FileUploadValidator.kt`
- Check file magic bytes against claimed MIME type (JPEG, PNG, GIF, WebP, MP4, WebM, MOV, PDF, DOCX, etc.)
- Apply to all upload endpoints

**WEB-026: CSP headers in Next.js**
- Add `headers()` function to `next.config.mjs` with:
  - Content-Security-Policy (script-src, style-src, img-src, connect-src, font-src, frame-ancestors)
  - X-Frame-Options: DENY
  - X-Content-Type-Options: nosniff
  - Referrer-Policy: strict-origin-when-cross-origin
  - Permissions-Policy

### Layer 2 — Persistent Rate Limiting

**AUTH-025: DB-backed rate limiter**
- Create migration `migration_114_rate_limits.sql` — table `rate_limit_hits(id, bucket_key, created_at)`
- Create `core/PersistentRateLimiter.kt` — DB-backed sliding window, same API surface as LoginThrottle
- Replace `LoginThrottle` in-memory implementation with DB-backed
- Replace `LibraryRouting` in-memory rate limiter with DB-backed

**SEC-015/016: Rate limiting on AI endpoints**
- Apply `PersistentRateLimiter` to tutor and reportcard routing entry points
- Per-user rate limit: e.g. 20 requests/minute for AI endpoints

### Layer 3 — Complex Multi-Component Fixes

**WEB-011: React Error Boundary**
- Create `components/ErrorBoundary.tsx` — class component with fallback UI
- Wrap children in root `layout.tsx`

**AUTH-021/022: JWT httpOnly cookies**
- Server: Set `access_token` and `refresh_token` as httpOnly, Secure, SameSite cookies on login/refresh
- Server: Clear cookies on logout
- Server: Read JWT from cookie as fallback when Authorization header absent
- Server: CORS `allowCredentials = true`
- Website: Add `credentials: "include"` to all fetch calls
- Website: Remove localStorage token storage from `session.tsx` and `auth.ts`
- Website: Session state via non-httpOnly `session_active` indicator cookie + `/auth/me` check
- Website: Update `client.ts` to not send Authorization header (cookie sent automatically)

## Build Verification
After each layer:
- Server: `./gradlew :server:compileKotlin` (JVM)
- Website: `npm run build` in website/
- All existing tests must pass

## Fix Log
After all 12 fixes converge, append detailed fix log to `GOD_MODE_AUDIT_v2.md`.
