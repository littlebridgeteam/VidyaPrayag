# External Services Inventory

> Complete list of third-party services required to run VidyaPrayag / Enroll+ in production.
> Derived from `.env.example`, codebase, and deployment configuration.

---

## Quick Summary

| # | Service | Purpose | Free Tier? | Required? |
|---|---------|---------|------------|-----------|
| 1 | Supabase | PostgreSQL + File Storage | Yes | **Yes** |
| 2 | Render | Backend hosting (Ktor) | Yes (cold starts) | **Yes** |
| 3 | Firebase (Primary) | Push notifications (FCM) | Yes | **Yes** |
| 4 | Firebase (OTPSender) | SMS gateway via Android phone | Yes | Optional |
| 5 | Fast2SMS | SMS OTP delivery (India) | No (~₹0.15-0.25/OTP) | Pick 1+ |
| 6 | MSG91 | SMS OTP delivery (India, fallback) | No (pay-per-use) | Pick 1+ |
| 7 | Twilio | International SMS + WhatsApp | No (pay-per-use) | Optional |
| 8 | WhatsApp Cloud API (Meta) | WhatsApp OTP delivery | Yes (1k convos/month) | Optional |
| 9 | SMTP Provider | Email OTP delivery | Yes | **Yes** |
| 10 | Cerebras Cloud | AI/LLM — fast lane (PEWS) | Yes | Pick 1+ |
| 11 | Groq | AI/LLM — reason/classify (PEWS) | Yes | Pick 1+ |
| 12 | SambaNova | AI/LLM — reasoning (PEWS) | Yes | Optional |
| 13 | Mistral | AI/LLM — batch (PEWS) | Yes | Optional |
| 14 | OpenRouter | AI/LLM — fallback (PEWS) | Yes | Optional |
| 15 | NVIDIA NIM | AI/LLM — reason + fast (PEWS) | Yes | Optional |
| 16 | GitHub Actions | CI/CD + Render keep-alive | Yes | **Yes** |
| 17 | Vercel / Netlify | Website hosting (Next.js) | Yes | **Yes** |

**Total: 17 services** — many are interchangeable fallbacks; you don't need all simultaneously.

---

## Minimum Viable Set (Go-Live)

These are non-negotiable for a production launch:

| # | Service | Why |
|---|---------|-----|
| 1 | **Supabase** | Database + media storage — the entire backend depends on this |
| 2 | **Render** | Hosts the Ktor API server |
| 3 | **Firebase (Primary)** | Push notifications to mobile app |
| 4 | **One SMS provider** | Fast2SMS or MSG91 — for phone OTP authentication |
| 5 | **One SMTP provider** | Gmail/Resend/Brevo — for email OTP authentication |
| 6 | **Vercel** | Hosts the marketing + onboarding website |
| 7 | **One AI provider** | Groq or Cerebras — for PEWS (optional at launch, but recommended) |

**Minimum: 5-7 services to be production-ready.**

---

## Detailed Breakdown

### 1. Supabase — Database + File Storage

- **Purpose:** PostgreSQL database hosting + Storage buckets for media uploads (logos, gallery, videos)
- **Env vars:**
  - `DATABASE_URL` — Postgres connection string (Session Pooler on port 5432)
  - `DATABASE_USER` / `DATABASE_PASSWORD` — optional if not encoded in URL
  - `SUPABASE_URL` — project base URL
  - `SUPABASE_SERVICE_KEY` — service role secret (NOT the anon key)
  - `SUPABASE_BUCKET` — storage bucket name (default: `school-media`)
- **Setup:** Supabase Dashboard → Project Settings → Database / API / Storage
- **Cost:** Free tier (500MB DB, 1GB storage); Pro plan $25/mo for higher limits
- **Notes:** If `DATABASE_URL` is empty, server falls back to local SQLite (dev only). If `SUPABASE_URL`/`SUPABASE_SERVICE_KEY` are missing, media upload returns 503 but server still boots.

---

### 2. Render — Backend Hosting

- **Purpose:** Hosts the Ktor API server
- **Evidence:** `keep-render-awake.yml` pings `vidyaprayag-api.onrender.com` every minute to prevent free-tier sleep
- **Cost:**
  - Free tier: spins down after 15 min inactivity (hence the keep-alive cron)
  - Starter plan: $7/mo — always-on, no cold starts
- **Notes:** Health check endpoint at `/api/v1/health`. Dockerfile is ready in repo root.

---

### 3. Firebase (Primary App) — Push Notifications

- **Purpose:** FCM (Firebase Cloud Messaging) for user-facing push notifications to the mobile app
- **Env vars:**
  - `FIREBASE_CREDENTIALS_JSON` — inline service-account JSON, OR
  - `FIREBASE_CREDENTIALS_FILE` — absolute path to service-account JSON, OR
  - `GOOGLE_APPLICATION_CREDENTIALS` — Google ADC convention
- **Client side:** `google-services.json` in `composeApp/` (Android)
- **Cost:** Free
- **Notes:** If no credentials resolve, push dispatch degrades to a no-op (never crashes boot).

---

### 4. Firebase (OTPSender App — separate project) — SMS Gateway

- **Purpose:** Self-hosted SMS gateway — backend pushes FCM data-message to a registered OTPSender Android phone, which sends the OTP SMS from its own SIM
- **Env vars:**
  - `OTP_SENDER_FIREBASE_CREDENTIALS_JSON` / `OTP_SENDER_FIREBASE_CREDENTIALS_FILE`
  - `OTP_GATEWAY_TOKEN` — shared secret for gateway ↔ backend API
  - `OTP_GATEWAY_ENABLED` — route phone OTPs through gateway (default: false)
  - `OTP_GATEWAY_LIVENESS_MINUTES` — device eligibility window (default: 5)
- **Cost:** Free (but requires a dedicated Android phone with a SIM, always on)
- **Notes:** This is a SEPARATE Firebase project from the primary app. No ADC fallback (would resolve to the wrong project). When `OTP_GATEWAY_TOKEN` is unset, the entire `/api/v1/gateway/*` route group is 404'd.

---

### 5. Fast2SMS — SMS OTP Delivery

- **Purpose:** India-based SMS provider for OTP delivery (primary cheap option)
- **Env vars:**
  - `FAST2SMS_API_KEY` — from https://www.fast2sms.com → Dev API → API Key
  - `FAST2SMS_ROUTE` — `otp` (no DLT template) or `dlt` (requires sender ID + DLT template)
  - `FAST2SMS_SENDER_ID` — for DLT route
  - `FAST2SMS_DLT_TEMPLATE_ID` — for DLT route
- **Cost:** ~₹0.15-0.25 per OTP
- **Notes:** OTP route is simplest — no DLT template approval needed.

---

### 6. MSG91 — SMS OTP Delivery (fallback)

- **Purpose:** India SMS provider with DLT-compliant flow API
- **Env vars:**
  - `MSG91_AUTH_KEY` — from https://control.msg91.com → API → Auth Keys
  - `MSG91_FLOW_ID` — from dashboard → Flow → your OTP template
  - `MSG91_OTP_VAR_NAME` — variable name in template (default: `OTP`)
  - `MSG91_SENDER_ID` — approved sender ID
- **Cost:** Pay-per-use
- **Notes:** Requires DLT template approval on MSG91 dashboard.

---

### 7. Twilio — International SMS + WhatsApp (fallback)

- **Purpose:** International SMS and WhatsApp sandbox for OTP delivery
- **Env vars:**
  - `TWILIO_ACCOUNT_SID` — from https://console.twilio.com
  - `TWILIO_AUTH_TOKEN`
  - `TWILIO_FROM` — e.g. `+15551234567` for SMS, `whatsapp:+14155238886` for WhatsApp
  - `TWILIO_CHANNEL` — `sms` or `whatsapp`
- **Cost:** Pay-per-use, more expensive than Indian providers
- **Notes:** Best for international users or as a fallback when Indian providers are down.

---

### 8. WhatsApp Cloud API (Meta) — WhatsApp OTP Delivery

- **Purpose:** Meta's official WhatsApp Business API for sending OTPs via WhatsApp
- **Env vars:**
  - `WHATSAPP_ACCESS_TOKEN` — long-lived system-user access token
  - `WHATSAPP_PHONE_NUMBER_ID` — from WhatsApp panel
  - `WHATSAPP_TEMPLATE_NAME` — approved authentication template (default: `vidyaprayag_otp`)
  - `WHATSAPP_TEMPLATE_LANG` — template language (default: `en`)
  - `WHATSAPP_API_VERSION` — API version (default: `v19.0`)
  - `WHATSAPP_INCLUDE_BUTTON` — include URL button in template (default: true)
- **Setup:** https://developers.facebook.com/docs/whatsapp/cloud-api/get-started
  1. Create app → add "WhatsApp" product
  2. Get long-lived system-user access token
  3. Note PHONE_NUMBER_ID from WhatsApp panel
  4. Submit authentication template with body `{{1}} is your verification code` + one URL button
- **Cost:** Free up to 1000 conversations/month, then pay-per-use
- **Notes:** Great for India where WhatsApp is ubiquitous. Free tier is generous.

---

### 9. SMTP Provider — Email OTP Delivery

- **Purpose:** Email-based OTP delivery (fallback or primary for email auth)
- **Env vars:**
  - `SMTP_HOST` — e.g. `smtp.gmail.com`, `smtp.resend.com`, `email-smtp.{region}.amazonaws.com`
  - `SMTP_PORT` — 465 (SSL) or 587 (STARTTLS)
  - `SMTP_USERNAME`
  - `SMTP_PASSWORD`
  - `SMTP_FROM` — display name + email (e.g. `VidyaPrayag <noreply@example.com>`)
  - `SMTP_USE_SSL` — true/false
  - `SMTP_USE_STARTTLS` — true/false
  - `SMTP_TIMEOUT_MS` — default 8000
- **Supported providers:**
  | Provider | Host | Port | TLS | Notes |
  |----------|------|------|-----|-------|
  | Gmail | smtp.gmail.com | 465 | SSL | Use App Password, not account password |
  | Resend | smtp.resend.com | 465 | SSL | API key as password |
  | AWS SES | email-smtp.{region}.amazonaws.com | 587 | STARTTLS | |
  | Brevo | smtp-relay.brevo.com | 587 | STARTTLS | |
  | Postmark | smtp.postmarkapp.com | 587 | STARTTLS | |
- **Cost:** Free tier available on most providers (Gmail: free, Resend: 3k/mo free, SES: 62k/mo free for EC2)

---

### 10-15. AI/LLM Providers — PEWS (Predictive Early Warning System)

All providers use OpenAI-compatible APIs. The backend has a circuit breaker that tries providers in order. Unset providers are automatically skipped. With zero keys set, PEWS still produces deterministic risk signals — only the AI explanation text is omitted.

**Privacy routing:** PII prompts are sent ONLY to no-training providers (Cerebras / Groq / OpenRouter / NVIDIA). Mistral / SambaNova are for non-PII batch/reasoning only.

| # | Provider | Lane | Training | Env Var | Get Key |
|---|----------|------|----------|---------|---------|
| 10 | **Cerebras** | FAST | No-training | `AI_CEREBRAS_API_KEY` | https://cloud.cerebras.ai |
| 11 | **Groq** | REASON/CLASSIFY | No-training | `AI_GROQ_API_KEY` | https://console.groq.com |
| 12 | **SambaNova** | REASON | Opt-in (non-PII only) | `AI_SAMBANOVA_API_KEY` | https://cloud.sambanova.ai |
| 13 | **Mistral** | BATCH | Opt-in (non-PII only) | `AI_MISTRAL_API_KEY` | https://console.mistral.ai |
| 14 | **OpenRouter** | REASON fallback | No-training | `AI_OPENROUTER_API_KEY` | https://openrouter.ai/keys |
| 15 | **NVIDIA NIM** | REASON + FAST | No-training | `AI_NVIDIA_REASON_API_KEY` | https://build.nvidia.com |

**Additional env vars:**
- `AI_ENCRYPTION_KEY` — AES-256-GCM key for encrypting provider secrets at rest (REQUIRED in production, generate with `openssl rand -hex 32`)
- `AI_CIRCUIT_FAILS_TO_OPEN` — consecutive failures before breaker trips (default: 5)
- `AI_CIRCUIT_COOLDOWN_SEC` — seconds to stay open before probe (default: 30)
- `PEWS_ENABLED` — master switch (default: true)
- `PEWS_RUN_HOUR_UTC` — daily recompute hour (default: 0)
- `PEWS_RUN_DAY_OF_WEEK` — for weekly frequency schools (default: MONDAY)

**Cost:** All have free tiers. You only need 1-2 to start; add more for redundancy.

---

### 16. GitHub Actions — CI/CD

- **Purpose:** CI pipeline (`ci.yml`) + Render keep-alive cron (`keep-render-awake.yml`)
- **Cost:** Free for public repos; 2000 min/mo free for private repos
- **Notes:** The keep-alive cron runs every minute, 24/7, pinging the Render health endpoint. This burns ~1440 GitHub Actions minutes/month.

---

### 17. Vercel / Netlify — Website Hosting

- **Purpose:** Hosts the Next.js 14 marketing + onboarding website
- **Config:** `NEXT_PUBLIC_API_BASE_URL` — points to the Ktor backend URL
- **Cost:** Free tier (Vercel: free for hobby, Netlify: 100GB bandwidth free)
- **Notes:** Website only calls public auth + JWT-authed onboarding routes — it never touches the DB directly.

---

## OTP Delivery Chain

The backend uses a chain-of-responsibility dispatcher. Providers are tried in order until one succeeds:

```
OTP_CHANNEL_ORDER=sms,whatsapp,email  (default)
```

```
Phone OTP → SMS provider chain:
  Fast2SMS → MSG91 → Twilio(SMS) → WhatsApp Cloud → Console fallback

Email OTP → SMTP chain:
  SMTP provider → Console fallback

Gateway mode (if OTP_GATEWAY_ENABLED=true):
  Phone OTP → OTPSender device (via FCM) → SMS from device SIM
```

Unset providers are automatically skipped (no network round-trip wasted). Console fallback prints OTP to stdout — **dev/CI only, must be false in production**.

---

## Cost Estimate (Minimum Viable Production)

| Service | Plan | Monthly Cost |
|---------|------|-------------|
| Supabase | Free | $0 |
| Render | Starter (always-on) | $7 |
| Firebase (primary) | Free | $0 |
| Fast2SMS | Pay-per-use (~₹0.20/OTP) | ~₹100-500 (depends on volume) |
| Gmail SMTP | Free | $0 |
| Vercel | Free | $0 |
| Groq (AI) | Free | $0 |
| GitHub Actions | Free | $0 |
| **Total** | | **~$7-15/mo + SMS usage** |

Scaling up: Supabase Pro ($25), Render Standard ($25), paid SMS volume, additional AI providers — expect $50-100/mo at moderate scale.

---

## Scheduled Jobs (Cron / Background)

All scheduled jobs are long-running coroutines launched at server startup. They use a check-and-run pattern: poll at a fixed interval, check if it's the target hour/day, run with a guard to prevent duplicate runs. Resilient to server restarts.

### Summary Table

| # | Job Name | Frequency | Target Time | What It Does |
|---|----------|-----------|-------------|--------------|
| 1 | NotificationScheduler | **Hourly** | Every hour | Fee reminders, calendar reminders, event registration reminders |
| 2 | MessageDispatchScheduler | **Every 1 minute** | Continuous | Dispatches scheduled announcements/broadcasts when due |
| 3 | PulseWeeklyJob | **Weekly** | Sunday 6 PM IST (12:30 PM UTC) | Generates Parent Pulse weekly summaries + sends push notifications |
| 4 | PewsDailyJob | **Daily** (or weekly per school) | Midnight UTC (5:30 AM IST), configurable | PEWS pipeline: Sense → Triage → Reason → Act → Learn |
| 5 | DailySummaryAutoJob | **Daily** | 11 AM UTC (4:30 PM IST) | AI-estimated daily class summaries for missing teacher logs |
| 6 | TransportJobScheduler — GPS | **Every 5 minutes** | Continuous | Checks for stale GPS pings on active vehicles, notifies admins |
| 7 | TransportJobScheduler — Attendance | **Daily** | 8 PM IST (2:30 PM UTC) | Finalizes missing transport attendance as "missed", notifies parents |
| 8 | LibraryJobScheduler — Overdue | **Daily** | 8 AM UTC | Notifies borrowers with overdue books |
| 9 | LibraryJobScheduler — Due Date | **Daily** | 8 AM UTC | Reminds borrowers of upcoming due dates |
| 10 | LibraryJobScheduler — Expiry+Purge | **Daily** | Midnight UTC | Expires stale reservations, deactivates expired announcements, purges old records |
| 11 | LibraryJobScheduler — Badges | **Daily** | 9 AM UTC | Evaluates and awards library badges to all borrowers |
| 12 | LibraryJobScheduler — Trending | **Hourly** | Every hour | Refreshes trending book counts (cache warm) |
| 13 | LibraryJobScheduler — Audit Retention | **Monthly** | 1st of month, midnight UTC | Purges audit logs >3yr, issues >5yr, announcements >6mo. Verifies hash chain first. |
| 14 | ReportCardJob — Worker | **Every 3 seconds** | Continuous | Polls for queued AI report card generation jobs and processes them |
| 15 | ReportCardJob — Scheduler | **Hourly** | Every hour | Checks for term-close windows, auto-enqueues draft generation |
| 16 | IdCardExpiryCheckJob | **Daily** | Every 24h from server start | Checks ID cards expiring within 30 days, logs for admin follow-up |
| 17 | FeatureFlagService | **Every 1 minute** | Continuous | Hot-reloads feature flags from DB (no restart needed) |
| 18 | GitHub Actions — Keep Render Awake | **Every 1 minute** | 24/7 cron | Pings Render health endpoint to prevent free-tier cold starts |

### By Frequency

**Every 1-5 minutes (continuous polling):**
- MessageDispatchScheduler — 1 min
- FeatureFlagService — 1 min
- GitHub Actions keep-alive — 1 min
- TransportJobScheduler GPS staleness — 5 min
- ReportCardJob worker — 3 sec

**Hourly:**
- NotificationScheduler (fee/calendar/event reminders)
- LibraryJobScheduler trending refresh
- ReportCardJob term-close scheduler
- PewsDailyJob (hourly check, fires once at target hour)
- PulseWeeklyJob (hourly check, fires once on Sunday at target hour)
- DailySummaryAutoJob (15-min check, fires once at target hour)

**Daily:**
- PewsDailyJob — midnight UTC (configurable)
- DailySummaryAutoJob — 11 AM UTC (4:30 PM IST)
- LibraryJobScheduler overdue + due-date — 8 AM UTC
- LibraryJobScheduler badges — 9 AM UTC
- LibraryJobScheduler expiry + purge — midnight UTC
- TransportJobScheduler attendance — 2:30 PM UTC (8 PM IST)
- IdCardExpiryCheckJob — every 24h from boot

**Weekly:**
- PulseWeeklyJob — Sunday 12:30 PM UTC (6 PM IST)
- PewsDailyJob (per-school weekly mode) — configurable day, default Monday

**Monthly:**
- LibraryJobScheduler audit retention — 1st of month at midnight UTC

### External Services Triggered by Scheduled Jobs

| Job | External Service Used | When |
|-----|----------------------|------|
| PewsDailyJob | AI/LLM providers (Cerebras/Groq/etc.) | Daily during Reason stage |
| DailySummaryAutoJob | AI/LLM providers (Cerebras/Groq/etc.) | Daily for AI summary generation |
| ReportCardJob | AI/LLM providers (Cerebras/Groq/etc.) | When jobs are dequeued (term-close or manual) |
| NotificationScheduler | Firebase (FCM) | When fee/calendar reminders are sent |
| PulseWeeklyJob | Firebase (FCM) | Sunday when pulse notifications are sent |
| TransportJobScheduler | Firebase (FCM) | When GPS staleness or missed attendance notifications are sent |
| LibraryJobScheduler | Firebase (FCM) | When overdue/due-date notifications are sent |
| MessageDispatchScheduler | Firebase (FCM) | When scheduled announcements/broadcasts are dispatched |
| GitHub Actions keep-alive | Render | Every minute (HTTP ping) |

---

## Setup Checklist

- [ ] Create Supabase project → get `DATABASE_URL`, `SUPABASE_URL`, `SUPABASE_SERVICE_KEY`
- [ ] Create storage bucket `school-media` (public) in Supabase
- [ ] Deploy backend to Render → set all env vars
- [ ] Create Firebase project → download service-account JSON → set `FIREBASE_CREDENTIALS_JSON`
- [ ] Add `google-services.json` to `composeApp/` for Android
- [ ] Sign up for Fast2SMS or MSG91 → get API key → set env vars
- [ ] Configure SMTP (Gmail App Password or Resend) → set env vars
- [ ] Generate `JWT_SECRET` — `openssl rand -hex 64`
- [ ] Generate `OTP_PEPPER` — `openssl rand -hex 32`
- [ ] Generate `AI_ENCRYPTION_KEY` — `openssl rand -hex 32`
- [ ] Set `OTP_ENABLE_CONSOLE_FALLBACK=false` in production
- [ ] Set `OTP_DEV_RETURN_CODE=false` in production
- [ ] Set `CORS_ALLOWED_ORIGINS` to your website + app origins
- [ ] Sign up for Groq or Cerebras → get API key → set `AI_GROQ_API_KEY`
- [ ] Deploy website to Vercel → set `NEXT_PUBLIC_API_BASE_URL`
- [ ] (Optional) Set up WhatsApp Cloud API for WhatsApp OTP
- [ ] (Optional) Set up OTPSender Firebase project for self-hosted SMS gateway
- [ ] (Optional) Add more AI providers for redundancy
