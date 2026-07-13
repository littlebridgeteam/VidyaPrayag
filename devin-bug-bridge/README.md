# Devin Bug Bridge

Cloudflare Worker that batches Slack bug reports and sends them to Devin (Kimi) on the same branch.

## What it does

- Receives bug reports from Slack `#bugs` channel
- Queues them in Cloudflare D1 (oldest first, FIFO)
- Every 2 minutes, picks up to 3 oldest pending bugs
- Downloads screenshots from Slack and uploads them to Supabase Storage
- Creates a single Devin API session with all 3 bugs
- Replies in each Slack thread: `Bug #N in batch-X — Devin is fixing it now`
- Devin fixes all bugs on branch `devin/bug-fixes` and opens a single PR

## Cost

- Cloudflare Workers: **Free** (100k requests/day)
- Cloudflare D1: **Free** (5M rows/month, 1GB storage)
- Supabase Storage: **Free** (500MB storage)
- Devin Pro: **$20/month** (already paid)
- **Total added cost: $0**

## Prerequisites

1. Node.js installed
2. Cloudflare account
3. Wrangler CLI logged in
4. Devin API token
5. Devin Organization ID
6. Slack Bot Token
7. Slack `#bugs` channel ID
8. Supabase project with public Storage bucket

## Setup Steps

### 1. Install Wrangler CLI

```bash
npm install -g wrangler
wrangler login
```

### 2. Create D1 Database

```bash
cd devin-bug-bridge
wrangler d1 create devin-bug-bridge-db
```

Copy the `database_id` from the output and paste it into `wrangler.toml`:

```toml
[[env.production.d1_databases]]
database_name = "devin-bug-bridge-db"
database_id = "YOUR_D1_DATABASE_ID"
```

### 3. Set up Supabase Storage

1. Go to [supabase.com](https://supabase.com) and open your project
2. Left sidebar → **Storage**
3. Click **New bucket**
4. Name: `devin-bug-screenshots`
5. Select **Public bucket** (so Devin can read screenshot URLs)
6. Click **Save**

**Security note:** A public bucket makes screenshots readable by anyone with the URL. That is fine for bug reports posted in a public Slack channel, but do not use this for sensitive data.

### 4. Get Supabase URL and Service Role Key

1. In Supabase, go to **Project Settings** (gear icon) → **API**
2. Copy the **URL** (looks like `https://xxxxxxxxxxxxxxxxxxxx.supabase.co`)
3. Copy the **service_role key** (starts with `eyJ...`) — NOT the anon key

The service role key is needed because the bridge uploads files server-side.

### 5. Set Secrets

```bash
wrangler secret put SLACK_BOT_TOKEN
wrangler secret put DEVIN_API_TOKEN
wrangler secret put DEVIN_ORG_ID
wrangler secret put SUPABASE_SERVICE_KEY
```

### 6. Update `wrangler.toml`

Set the non-secret variables:

```toml
SLACK_CHANNEL_BUGS = "CXXXXXXXXXX"  # your #bugs channel ID
BRANCH_NAME = "devin/bug-fixes"
MAX_BATCH_SIZE = "3"
SUPABASE_URL = "https://xxxxxxxxxxxxxxxxxxxx.supabase.co"
SUPABASE_BUCKET = "devin-bug-screenshots"
```

### 7. Apply Database Schema

```bash
wrangler d1 execute devin-bug-bridge-db --file=schema.sql
```

### 8. Deploy

```bash
npm run deploy
```

After deploy, Wrangler prints a URL like:

```
https://devin-bug-bridge.your-subdomain.workers.dev
```

### 9. Configure Slack Events API

1. Go to [api.slack.com/apps](https://api.slack.com/apps)
2. Select your Devin Bug Bot app (or create one)
3. Enable **Event Subscriptions**
4. Request URL: `https://devin-bug-bridge.your-subdomain.workers.dev/slack/events`
5. Wait for Slack to verify (green checkmark)
6. Subscribe to bot event: `message.channels`
7. Save changes

### 10. Configure Slack Bot Scopes

In **OAuth & Permissions** → **Bot Token Scopes**, add:

- `chat:write` — reply in threads
- `files:read` — download screenshots
- `channels:history` — read #bugs messages
- `channels:read` — channel info

Reinstall the app to workspace after adding scopes.

### 11. Test

Post a bug in `#bugs` with a screenshot. Within 2 minutes the bridge should:

- Reply in the thread: `Bug #1 in batch-XXXX — Devin is fixing it now`
- Create a Devin session at `app.devin.ai`

Post 3 more bugs. They should be picked up as one batch.

## Manual queue processing

If you don't want to wait for the 2-minute cron, hit:

```bash
curl -X POST https://devin-bug-bridge.your-subdomain.workers.dev/process
```

## Monitoring

View logs:

```bash
wrangler tail
```

## Files

- `src/index.js` — main worker logic
- `schema.sql` — D1 database schema
- `wrangler.toml` — Cloudflare config
- `package.json` — scripts and dependencies
