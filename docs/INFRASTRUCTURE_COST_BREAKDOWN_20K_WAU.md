# Infrastructure & API Cost Breakdown — 20K Weekly Active Users

> **Verified July 10, 2026** — All pricing confirmed against official provider documentation.
> Target scale: ~15,000 students, ~4,000-6,000 DAU, ~200-500 peak concurrent users.

---

## 1. Executive Summary

| Scenario | Monthly Cost |
|----------|-------------|
| **With GitHub Student Offer (first year)** | **$638/month** |
| **Without Student Offer** | **$657/month** |

### Cost Distribution (No Student Offer)

| Service | Monthly Cost | % of Total |
|---------|-------------|-----------|
| AI (Groq + free providers) | $350 | 53% |
| WhatsApp (Meta Cloud API) | $211 | 32% |
| Server (DigitalOcean) | $48 | 7% |
| SMS (Fast2SMS + MSG91) | $27 | 4% |
| Database (Neon) | $19 | 3% |
| Media Storage (Cloudflare R2) | $1.80 | 0.3% |
| **Total** | **$657** | **100%** |

**AI + WhatsApp = 85% of total cost.** Server, database, media, and SMS combined are only 15%.

---

## 2. Chosen Infrastructure Stack

| Component | Provider | Plan | Why |
|-----------|----------|------|-----|
| **Server** | DigitalOcean | Premium Intel, 8GB RAM, 4 vCPU, 160GB SSD, 5TB transfer ($48/mo) | Handles 200-500 concurrent users + 18 background jobs |
| **Database** | Neon | Pro ($19/mo after student offer) | Serverless Postgres, autoscaling, branching |
| **Media Storage** | Cloudflare R2 | Pay-as-you-go | $0 egress (killer feature), 10GB free tier |
| **AI/LLM** | Groq (primary) | Developer tier (pay-as-you-go) | Cheapest GPT-OSS 120B, fastest inference |
| **AI/LLM (backup)** | Cerebras, OpenRouter, Gemini, Mistral | Free tiers | Multi-provider failover, free capacity |
| **WhatsApp** | Meta Cloud API | Per-message pricing | Direct Meta integration, India rates |
| **SMS** | Fast2SMS (primary), MSG91 (fallback) | Pay-per-use | Cheapest in India (₹0.11-0.20/SMS) |
| **Push Notifications** | Firebase Cloud Messaging | Free | Unlimited push notifications |
| **OTP** | Firebase Phone Auth (primary), SMS (fallback) | Free → SMS | Firebase Auth free, SMS only for failures |
| **Website** | Vercel | Free | Next.js hosting |
| **CI/CD** | GitHub Actions | Free (public repo) | 2,000 minutes/month free |

### GitHub Student Pack Benefits

| Service | Benefit | Duration | Savings |
|---------|---------|----------|---------|
| DigitalOcean | $200 platform credit | ~4 months free on $48 droplet | $192 |
| Neon | 1 year free database | 12 months | $228 |
| Cloudflare R2 | 10GB free (always available) | Permanent | $0.15/mo |

---

## 3. AI Cost — $350/month

### 3.1 Usage Pattern

| Metric | Value |
|--------|-------|
| Students | 15,000 |
| Students using AI tutor daily (20%) | 3,000 |
| Doubts per student per day | 10 |
| Total doubts/day | ~70,000 (scaled from 7K baseline) |
| LLM calls per doubt (agent loop, avg) | ~2-3 |
| Total LLM calls/day | ~140,000-210,000 |
| Tokens per doubt (avg) | ~7,500 |
| Total tokens/day | ~525M |

### 3.2 Medium Tier Strategy — Triage Routing

| Doubt Type | Lane | Model | % of Doubts | Cost |
|-----------|------|-------|------------|------|
| **Simple** (definitions, recall, single-step) | FAST | Llama 3.1 8B (Groq) | 70% | $0 (free tier) + paid overflow |
| **Complex** (proofs, multi-step, conceptual) | REASON | GPT-OSS 120B (Groq) | 30% | Free tier first + paid overflow |

### 3.3 Optimizations Applied

| Optimization | Effect |
|-------------|--------|
| **Triage classifier** | Routes 70% to cheap FAST lane, 30% to expensive REASON lane |
| **70% cache hit rate** | 70% of doubts are similar → cached response, no LLM call |
| **maxSteps = 3** (reduced from 6) | 2 LLM calls avg per doubt instead of 3 |
| **Groq Batch API for scheduled jobs** | 50% off for PEWS, DailySummary, ReportCard, PulseWeekly |
| **Multi-provider free tier utilization** | Cerebras (1M TPD), Groq free (200K TPD), OpenRouter, Gemini, Mistral |

### 3.4 Groq Free Tier Limits (Verified from Official Docs)

| Model | RPM | RPD | TPM | TPD | Price (in/out $/1M) |
|-------|-----|-----|-----|-----|-------------------|
| Llama 3.1 8B | 30 | 14,400 | 6,000 | 500,000 | $0.05 / $0.08 |
| Llama 3.3 70B | 30 | 1,000 | 12,000 | 100,000 | $0.59 / $0.79 |
| GPT-OSS 120B | 30 | 1,000 | 8,000 | 200,000 | $0.15 / $0.60 |
| GPT-OSS 20B | 30 | 1,000 | 8,000 | 200,000 | $0.075 / $0.30 |

### 3.5 Groq Developer Tier (Paid) — 10x Limits + 25% Discount

| Model | Dev RPD | Dev TPD | Dev Price (in/out $/1M) |
|-------|---------|---------|------------------------|
| Llama 3.1 8B | 500,000 | 5,000,000 | $0.0375 / $0.06 |
| GPT-OSS 120B | 10,000 | 2,000,000 | $0.1125 / $0.45 |
| GPT-OSS 20B | 10,000 | 2,000,000 | $0.056 / $0.225 |

### 3.6 Groq Batch API — 50% Off

| Model | Batch Price (in/out $/1M) | Window |
|-------|--------------------------|--------|
| GPT-OSS 120B | $0.075 / $0.30 | 24h processing |
| GPT-OSS 20B | $0.0375 / $0.15 | 24h processing |

- Does NOT count against rate limits
- Perfect for scheduled jobs: PEWS, DailySummary, ReportCardJob, PulseWeeklyJob

### 3.7 All PII-Safe Free Provider Capacities

| Provider | Model | Free TPD | Notes |
|----------|-------|---------|-------|
| Groq (REASON) | GPT-OSS 120B | 200,000 | 1,000 RPD, 8K TPM |
| Groq (FAST) | Llama 3.1 8B | 500,000 | 14,400 RPD, 6K TPM |
| Cerebras | GPT-OSS 120B | 1,000,000 | 30 RPM, 60-100K TPM, 3,000 tok/s |
| OpenRouter (free) | Nemotron 550B | ~250,000 | 50 RPD |
| OpenRouter ($10 credit) | Nemotron 550B | ~5,000,000 | 1,000 RPD (free models, wallet preserved) |
| NVIDIA | MiniMax M2.7 | ~5,000,000 | 1,000 RPD, 5K TPM |
| **Total free PII-safe** | | **~7,000,000 tokens/day** | |

### 3.8 Non-PII Free Providers (for Agentic OS non-student tasks)

| Provider | Model | Free Capacity | Used For |
|----------|-------|--------------|----------|
| Gemini | 2.5 Flash | 1,500 RPD, ~7.5M tokens/day | F2 lead scoring, F14 inventory, F17 pace, F18 reputation, F22 scholarship, F25 exam tracker |
| Mistral | Small 3 | ~1B tokens/month = ~33M/day | F4 exam papers, batch non-PII |

### 3.9 Other Provider Pricing Comparison (GPT-OSS 120B)

| Provider | Input $/1M | Output $/1M | Free Tier? | PII-Safe? | Speed |
|----------|-----------|------------|-----------|-----------|-------|
| **Groq (Dev tier, 25% off)** | $0.1125 | $0.45 | Yes (free tier first) | Yes | 500 tok/s |
| **Groq (on-demand)** | $0.15 | $0.60 | Yes (free tier first) | Yes | 500 tok/s |
| **Groq (Batch, 50% off)** | $0.075 | $0.30 | No (paid only) | Yes | 24h window |
| Together AI | $0.15 | $0.60 | No free tier | Yes | ~150 tok/s |
| Cerebras | $0.25 | $0.69 | Yes (1M TPD free) | Yes | 3,000 tok/s |
| DeepSeek | $0.14 | $0.28 | 5M one-time | No | ~60 tok/s |

**Groq is the cheapest and fastest for GPT-OSS 120B.**

### 3.10 AI Cost Breakdown

| Component | Daily Cost | Monthly Cost |
|-----------|-----------|-------------|
| Simple doubts (Llama 8B, Groq Dev) | $2.73 | $82 |
| Complex doubts (GPT-OSS 120B, Groq Dev) | $7.45 | $224 |
| Scheduled jobs (Batch API, 50% off) | $0.86 | $26 |
| CLASSIFY + FAST_CHAT overflow | $0.50 | $14 |
| Free tier absorbed | $0 | $0 |
| **Total AI** | **$11.54** | **~$350** |

---

## 4. WhatsApp Cost — $211/month (Optimized)

### 4.1 Meta WhatsApp Cloud API Pricing (India, Verified July 2026)

| Category | Per-Message Rate (India) | Free? |
|----------|------------------------|-------|
| Service (user-initiated, 24h window) | $0 | Always free |
| Utility (inside 24h window) | $0 | Free |
| Utility (outside 24h window) | $0.0014 (₹0.115) | Paid |
| Authentication (OTP) | $0.0014 (₹0.115) | Paid |
| Marketing (promotions, fee reminders) | $0.010 (₹0.8631) | Paid |

### 4.2 Usage Pattern (20K WAU, ~15,000 Students)

| Message Type | Volume Calculation | Messages/month | Category | Cost |
|-------------|-------------------|---------------|----------|------|
| Attendance (absent only) | 15,000 × 12% absent × 22 school days | 39,600 | Utility | $55 |
| Fee reminders | 15,000 × 30% don't pay after push × 1/month | 4,500 | Marketing | $45 |
| Admin manual reminders | 15,000 × 10% when admin clicks remind | 1,500 | Marketing | $15 |
| Marks/homework (push-first, 30% WhatsApp fallback) | 5,000/day × 30% × 30 days | 45,000 | Utility | $63 |
| Event announcements (push-first, 30% fallback) | 300/day × 30% × 30 days | 2,700 | Marketing | $27 |
| OTP via WhatsApp | 150/day × 30 days | 4,500 | Auth | $6 |
| Service messages (parent-initiated) | ~3,000/day × 30 days | 90,000 | Service | $0 |
| **Total** | | **~187,800** | | **~$211** |

### 4.3 Optimization Strategy

| Strategy | Effect |
|----------|--------|
| Push notifications (Firebase) first for marks/homework | Only 30% who don't open app get WhatsApp |
| Push notifications first for event announcements | Only 30% get WhatsApp |
| Attendance alerts only for absent students | 12% absent rate, not 100% |
| Fee reminders: push first → WhatsApp after non-payment | ~1 WhatsApp/month per non-paying student |
| Admin manual reminders | 10% of students when admin clicks |
| Service messages (parent replies) | Always free |

### 4.4 Meta Graph APIs (Free)

| API | Cost | Usage |
|-----|------|-------|
| Facebook Graph API | $0 | Auto-post to school Facebook pages |
| Instagram Graph API | $0 | Auto-post to school Instagram accounts |
| Meta Marketing API | $0 (API only, ad spend separate) | Run FB/Instagram ads if needed |

---

## 5. SMS Cost — $27/month

### 5.1 Provider Pricing (India)

| Provider | Per SMS Cost | Best For |
|----------|-------------|---------|
| Fast2SMS (primary) | ₹0.11-0.20 (~$0.0013-0.0024) | Bulk, OTP |
| MSG91 (fallback) | ₹0.15 (~$0.0018) | OTP, delivery guarantee |
| Twilio (international) | ₹0.45 (~$0.0054) | International OTP only |

### 5.2 Usage Pattern

| Message Type | Volume/month | Cost |
|-------------|-------------|------|
| OTP (login/signup) via SMS | ~15,000 | $27 |
| Firebase Phone Auth (free, reduces SMS) | Absorbs ~80% of OTP attempts | $0 |
| MSG91 fallback (SMS delivery failure) | ~1,500 | Included above |
| **Total SMS** | **~15,000** | **$27** |

### 5.3 Optimization

- Firebase Phone Auth handles 80% of OTP verification for free
- SMS only sent when Firebase OTP fails or is unavailable
- MSG91 as fallback if Fast2SMS delivery fails

---

## 6. Server Cost — $48/month

### 6.1 DigitalOcean Droplet

| Spec | Value |
|------|-------|
| RAM | 8GB |
| vCPU | 4 |
| SSD | 160GB |
| Transfer | 5TB/month |
| Price | $48/month |

### 6.2 Why $48 Tier (Not $24)

| Droplet | RAM | vCPU | Price | 20K WAU? |
|---------|-----|------|-------|---------|
| $24 tier | 4GB | 2 | $24 | Risky — CPU spikes during peak study hours |
| $48 tier | 8GB | 4 | $48 | **Comfortable — handles 200-500 concurrent + 18 jobs** |
| $96 tier | 16GB | 8 | $96 | Overkill unless splitting API + jobs |

### 6.3 What Runs on This Server

- Ktor backend (API server)
- 18 scheduled background jobs (5 continuous, 6 hourly, 7 daily, 2 weekly, 1 monthly)
- AI HTTP clients (Groq, Cerebras, OpenRouter, etc.)
- WebSocket connections for real-time updates
- AI agent loops (JSON parsing, tool execution, grounding)

---

## 7. Database Cost — $19/month

### 7.1 Neon Pro Plan

| Feature | Value |
|---------|-------|
| Storage | 10GB |
| Compute | 0.25 CU (autoscaling) |
| Branches | Unlimited |
| Price | $19/month |
| Student offer | 1 year free |

### 7.2 Why Neon (Not Supabase)

| Factor | Neon | Supabase |
|--------|------|---------|
| Student offer | 1 year free | No student offer |
| Serverless autoscaling | Yes | No |
| DB branching | Yes | No |
| Price after offer | $19/mo (10GB) | $25/mo (8GB) |
| Postgres compatible | Yes | Yes |

---

## 8. Media Storage Cost — $1.80/month

### 8.1 Cloudflare R2 Pricing

| Resource | Free Tier | After Free |
|----------|----------|-----------|
| Storage | First 10GB free | $0.015/GB/month |
| Egress | **Unlimited free** | $0 |
| Write operations (Class A) | 1M/month free | $4.50/million |
| Read operations (Class B) | 10M/month free | $0.36/million |

### 8.2 Storage Estimate (20K WAU)

| Content | Size |
|---------|------|
| PEWS health snapshots | ~60GB |
| Homework images | ~30GB |
| Report card PDFs | ~15GB |
| Event photos | ~15GB |
| Brand assets/logos | ~1GB |
| Library book covers | ~5GB |
| AI knowledge chunks | ~4GB |
| **Total** | **~130GB** |

| Calculation | Value |
|------------|-------|
| Total storage | 130GB |
| Free tier | 10GB |
| Paid storage | 120GB × $0.015 | 
| **Monthly cost** | **$1.80** |

**R2's $0 egress is the killer feature.** No matter how many images/PDFs parents and students download, egress costs nothing. This would cost $50-100+/month on AWS S3 or Google Cloud Storage.

---

## 9. Complete Cost Summary

### 9.1 With GitHub Student Offer (First Year)

| Service | Monthly Cost |
|---------|-------------|
| AI (Groq + free providers) | $350 |
| WhatsApp (Meta Cloud API, optimized) | $211 |
| SMS (Fast2SMS + MSG91, OTP only) | $27 |
| Server (DigitalOcean — $200 credit applies) | $0 → $48 (after credit) |
| Database (Neon — 1 year free) | $0 |
| Media (Cloudflare R2, 130GB) | $1.80 |
| Firebase (push, auth) | $0 |
| Vercel (website) | $0 |
| GitHub Actions (CI/CD) | $0 |
| **Total (first 4 months, credit active)** | **$590** |
| **Total (months 5-12, DB still free)** | **$638** |
| **Total (after 12 months, no student offer)** | **$657** |

### 9.2 Without Student Offer

| Service | Monthly Cost |
|---------|-------------|
| AI (Groq + free providers) | $350 |
| WhatsApp (Meta Cloud API, optimized) | $211 |
| SMS (Fast2SMS + MSG91, OTP only) | $27 |
| Server (DigitalOcean, 8GB/4vCPU) | $48 |
| Database (Neon Pro) | $19 |
| Media (Cloudflare R2, 130GB) | $1.80 |
| Firebase (push, auth) | $0 |
| Vercel (website) | $0 |
| GitHub Actions (CI/CD) | $0 |
| **Total** | **$657/month** |

### 9.3 Cost Per Student

| Metric | Value |
|--------|-------|
| Students | 15,000 |
| Monthly total (no student offer) | $657 |
| **Cost per student per month** | **$0.044 (4.4 paise)** |
| **Cost per student per year** | **$0.52 (52 paise)** |

---

## 10. Cost Reduction Levers

If you need to bring cost down further, here are the options ranked by impact:

| Lever | Savings | Trade-off |
|-------|---------|-----------|
| Increase AI cache hit to 80% (from 70%) | -$100/mo on AI | More stale responses for edge-case doubts |
| Route 50% of complex doubts to GPT-OSS 20B | -$60/mo on AI | Slightly lower quality on borderline complex doubts |
| Reduce WhatsApp fallback to 20% (from 30%) | -$70/mo on WhatsApp | More parents miss notifications |
| Use $24 DO droplet (4GB/2vCPU) | -$24/mo on server | CPU spikes during peak, risky for 20K WAU |
| Route all OTP via Firebase Auth (no SMS) | -$27/mo on SMS | Users without phone verification fallback |
| **Maximum savings (all levers)** | **~$281/mo** | **$657 → $376/mo** |

---

## 11. Scaling Path

| Milestone | Students | Monthly Cost | Key Changes |
|-----------|---------|-------------|-------------|
| Current | < 1,000 | ~$10-15 | Free tiers only, $24 DO droplet |
| Initial launch | 7,000 | ~$518 | $24 DO droplet, Neon free (student), AI $350, WhatsApp $97 |
| Growth | 15,000 (20K WAU) | ~$657 | $48 DO droplet, Neon Pro, AI $350, WhatsApp $211 |
| Scale | 30,000 (40K WAU) | ~$1,100 | $96 DO droplet or 2× $48, Neon Scale, AI ~$600, WhatsApp ~$400 |
| Enterprise | 50,000+ | ~$1,800+ | Dedicated GPU for AI, multi-region, Redis cluster |

---

## 12. Code Updates Required

| File | Change | Priority |
|------|--------|----------|
| `KeyVault.kt` | Fix all RPM/RPD/TPM/TPD values to match real Groq free tier limits | **Critical** |
| `RateLimiter.kt` | Add TPD (tokens per day) tracking — currently missing | **Critical** |
| `KeyVault.kt` | Add Llama 3.1 8B as separate FAST provider (14,400 RPD, 500K TPD) | High |
| `KeyVault.kt` | Add Groq Batch API support for scheduled jobs (50% off) | High |
| `TutorAgentService.kt` | Add triage routing (simple → FAST_CHAT, complex → REASON) | High |
| `TutorAgentService.kt` | Reduce maxSteps from 6 to 3 | Medium |
| Notification service | Push-first logic (Firebase → WhatsApp fallback) | Medium |
| Notification service | Attendance WhatsApp only for absent students | Medium |

---

## 13. Provider Rate Limits Reference

### Groq Free Tier (Per Organization, Not Per API Key)

| Model | RPM | RPD | TPM | TPD |
|-------|-----|-----|-----|-----|
| Llama 3.1 8B | 30 | 14,400 | 6,000 | 500,000 |
| Llama 3.3 70B | 30 | 1,000 | 12,000 | 100,000 |
| GPT-OSS 120B | 30 | 1,000 | 8,000 | 200,000 |
| GPT-OSS 20B | 30 | 1,000 | 8,000 | 200,000 |
| Llama 4 Scout 17B | 30 | 1,000 | 30,000 | 500,000 |
| Kimi K2 | 60 | 1,000 | 10,000 | 300,000 |
| Qwen 3 32B | 60 | 1,000 | 6,000 | 500,000 |

### Groq Developer Tier (10x Free + 25% Discount)

| Model | Dev RPM | Dev RPD | Dev TPM | Dev TPD |
|-------|---------|---------|---------|---------|
| Llama 3.1 8B | 300 | 500,000 | 60,000 | 5,000,000 |
| GPT-OSS 120B | 300 | 10,000 | 80,000 | 2,000,000 |
| GPT-OSS 20B | 300 | 10,000 | 80,000 | 2,000,000 |

### Cerebras Free Tier

| Limit | Value |
|-------|-------|
| TPD | 1,000,000 (all models share) |
| RPM | 30 |
| TPM | 60,000-100,000 |
| Context | 8,192 tokens (free tier cap) |

### OpenRouter

| Tier | RPD | RPM | Notes |
|------|-----|-----|-------|
| Free (no credits) | 50 | 20 | Free models only |
| Paid ($10+ credits) | 1,000 | 20 | Free models, wallet preserved |

### WhatsApp Cloud API

| Category | India Rate | Free Tier |
|----------|-----------|-----------|
| Service | $0 | Always free |
| Utility (in window) | $0 | Free |
| Utility (out window) | $0.0014 | Paid |
| Authentication | $0.0014 | Paid |
| Marketing | $0.010 | Paid |
| Free service conversations | 1,000/month per WABA | |

---

*Document last updated: July 10, 2026*
*All pricing verified against official provider documentation on the same date.*
