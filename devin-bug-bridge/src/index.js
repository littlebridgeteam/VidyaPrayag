/**
 * Devin Bug Bridge
 * Batches Slack bug reports (oldest 3 first) and sends them to Devin API.
 * All fixes land on the same branch (slack_fix).
 */

export default {
  async fetch(request, env, ctx) {
    const url = new URL(request.url);
    console.log(`[${request.method}] ${url.pathname}`);

    // Health check
    if (url.pathname === '/health' && request.method === 'GET') {
      return json({ status: 'ok' });
    }

    // Slack Events API
    if (url.pathname === '/slack/events' && request.method === 'POST') {
      let body;
      try {
        body = await request.json();
      } catch (e) {
        console.error('Failed to parse Slack event body:', e);
        return json({ error: 'bad json' }, 400);
      }
      console.log('Slack event received:', JSON.stringify(body).substring(0, 500));

      // Slack URL verification challenge
      if (body.type === 'url_verification') {
        console.log('URL verification challenge:', body.challenge);
        return new Response(body.challenge, { status: 200 });
      }

      // Process event async so Slack doesn't retry
      ctx.waitUntil(handleSlackEvent(body, env));
      return json({ ok: true });
    }

    // Manual trigger to process queue
    if (url.pathname === '/process' && request.method === 'POST') {
      ctx.waitUntil(processQueue(env));
      return json({ ok: true, message: 'Queue processing started' });
    }

    // Dashboard showing bug queue + commits on slack_fix
    if (url.pathname === '/dashboard' && request.method === 'GET') {
      const html = await renderDashboard(env);
      return new Response(html, { headers: { 'Content-Type': 'text/html' } });
    }

    return json({ error: 'Not found' }, 404);
  },

  async scheduled(event, env, ctx) {
    if (isAfterDeactivationDate()) {
      console.log('Devin bug bridge deactivated after free Kimi K2.7 window.');
      return;
    }
    ctx.waitUntil(processQueue(env));
  },
};

async function handleSlackEvent(body, env) {
  const event = body.event;
  if (!event) {
    console.log('No event in body');
    return;
  }

  console.log('Event type:', event.type, '| channel:', event.channel, '| expected:', env.SLACK_CHANNEL_BUGS);

  // Only handle new messages in #bugs
  if (event.type !== 'message') {
    console.log('Skipping: event type is', event.type, 'not message');
    return;
  }
  if (event.channel !== env.SLACK_CHANNEL_BUGS) {
    console.log('Skipping: channel', event.channel, 'does not match', env.SLACK_CHANNEL_BUGS);
    return;
  }
  // Ignore bot messages and edits
  if (event.bot_id || event.subtype === 'bot_message' || event.subtype === 'message_changed') {
    console.log('Skipping: bot message or edit');
    return;
  }
  if (!event.text) {
    console.log('Skipping: no text');
    return;
  }

  // Ignore short messages (likely test messages like "hello")
  if (event.text.trim().length < 20) {
    console.log('Skipping: message too short (< 20 chars):', event.text);
    return;
  }

  console.log('Bug accepted! text:', event.text.substring(0, 100));

  const files = event.files || [];

  await env.DB.prepare(
    `INSERT INTO bug_queue (slack_channel_id, slack_thread_ts, slack_message_ts, slack_user, text, files, status)
     VALUES (?, ?, ?, ?, ?, ?, ?)`
  ).bind(
    event.channel,
    event.thread_ts || null,
    event.ts,
    event.user || null,
    event.text,
    JSON.stringify(files),
    'pending'
  ).run();
}

async function processQueue(env) {
  if (isAfterDeactivationDate()) {
    console.log('processQueue skipped — free Kimi K2.7 window has ended.');
    return;
  }

  const maxBatch = parseInt(env.MAX_BATCH_SIZE || '3', 10);

  // Lock the oldest N pending bugs
  const { results } = await env.DB.prepare(
    `SELECT * FROM bug_queue
     WHERE status = 'pending'
     ORDER BY created_at ASC
     LIMIT ?`
  ).bind(maxBatch).all();

  if (!results || results.length === 0) return;

  const ids = results.map(r => r.id);
  const batchId = `batch-${Date.now()}`;

  await env.DB.prepare(
    `UPDATE bug_queue SET status = 'processing', batch_id = ?, updated_at = CURRENT_TIMESTAMP
     WHERE id IN (${ids.join(',')})`
  ).bind(batchId).run();

  try {
    // Build attachment URLs by uploading Slack images to Firebase Storage
    const bugs = [];
    const attachmentUrls = [];

    for (let i = 0; i < results.length; i++) {
      const bug = results[i];
      const files = JSON.parse(bug.files || '[]');
      const bugAttachmentUrls = [];

      for (const file of files) {
        if (file.mimetype && file.mimetype.startsWith('image/')) {
          const publicUrl = await uploadSlackImageToSupabase(file, env);
          if (publicUrl) {
            bugAttachmentUrls.push(publicUrl);
            attachmentUrls.push(publicUrl);
          }
        }
      }

      bugs.push({
        index: i + 1,
        id: bug.id,
        text: bug.text,
        slack_channel_id: bug.slack_channel_id,
        slack_thread_ts: bug.slack_thread_ts,
        slack_message_ts: bug.slack_message_ts,
        attachmentUrls: bugAttachmentUrls,
      });
    }

    // Send batch to local server (runs Devin CLI locally — free Kimi K2.7, no ACU)
    const result = await sendToLocalServer(bugs, batchId, env);

    // Update bugs with result
    await env.DB.prepare(
      `UPDATE bug_queue SET status = 'done', devin_session_id = ?, updated_at = CURRENT_TIMESTAMP
       WHERE id IN (${ids.join(',')})`
    ).bind(`local-${batchId}`).run();

    // Reply in each Slack thread
    for (const bug of bugs) {
      const reply = `Bug #${bug.index} in ${batchId} — Devin CLI (Kimi K2.7) is fixing it now locally.\nNo ACU cost — running on your machine.`;
      await postSlackReply(env, bug.slack_channel_id, bug.slack_message_ts, reply);
    }

  } catch (err) {
    console.error('processQueue error:', err);
    // Mark failed so we can retry later
    await env.DB.prepare(
      `UPDATE bug_queue SET status = 'failed', updated_at = CURRENT_TIMESTAMP
       WHERE id IN (${ids.join(',')})`
    ).run();
  }
}

async function uploadSlackImageToSupabase(file, env) {
  try {
    const url = file.url_private_download || file.url_private;
    if (!url) return null;

    // Download image from Slack
    const slackRes = await fetch(url, {
      headers: { Authorization: `Bearer ${env.SLACK_BOT_TOKEN}` },
    });
    if (!slackRes.ok) return null;

    const data = await slackRes.arrayBuffer();
    const contentType = file.mimetype || 'application/octet-stream';
    const filename = `${Date.now()}-${file.id || file.name}`;
    const path = `screenshots/${filename}`;

    // Upload to Supabase Storage via REST API
    const uploadUrl = `${env.SUPABASE_URL}/storage/v1/object/${env.SUPABASE_BUCKET}/${encodeURIComponent(path)}`;
    const sbRes = await fetch(uploadUrl, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${env.SUPABASE_SERVICE_KEY}`,
        'Content-Type': contentType,
      },
      body: data,
    });

    if (!sbRes.ok) {
      const text = await sbRes.text();
      console.error('Supabase upload failed:', text);
      return null;
    }

    // Public download URL for public bucket
    return `${env.SUPABASE_URL}/storage/v1/object/public/${env.SUPABASE_BUCKET}/${encodeURIComponent(path)}`;
  } catch (err) {
    console.error('uploadSlackImageToSupabase error:', err);
    return null;
  }
}

async function sendToLocalServer(bugs, batchId, env) {
  const serverUrl = env.LOCAL_SERVER_URL || 'http://localhost:7788';
  const authToken = env.LOCAL_SERVER_TOKEN || 'devin-bug-bridge-local-2026';

  const res = await fetch(`${serverUrl}/process-batch`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'X-Auth-Token': authToken,
    },
    body: JSON.stringify({
      bugs,
      batchId,
    }),
  });

  if (!res.ok) {
    const text = await res.text();
    throw new Error(`Local server error: ${res.status} ${text}`);
  }

  return await res.json();
}

function buildPrompt(bugs, env) {
  let prompt = `You are fixing ${bugs.length} bug(s) reported by the team.\n\n`;
  prompt += `STRICT RULES:\n`;
  prompt += `- Work ONLY in the repository: littlebridgeteam/VidyaPrayag\n`;
  prompt += `- Work ONLY on the branch: ${env.BRANCH_NAME}\n`;
  prompt += `- NEVER create a new branch.\n`;
  prompt += `- NEVER merge into main or any other branch.\n`;
  prompt += `- NEVER open a pull request.\n`;
  prompt += `- ALL fixes, commits, and pushes must happen on ${env.BRANCH_NAME}.\n\n`;
  prompt += `Follow this workflow for the entire batch:\n`;
  prompt += `1. Pull latest ${env.BRANCH_NAME}. If it doesn't exist, create it from main.\n`;
  prompt += `2. For each bug below, find the root cause, implement a minimal fix, and write a focused regression test.\n`;
  prompt += `3. Build the project. Fix all compile errors.\n`;
  prompt += `4. Run the full test suite. Fix all failures.\n`;
  prompt += `5. ONLY if everything builds and passes tests, commit each bug separately and push to ${env.BRANCH_NAME}.\n\n`;
  prompt += `CRITICAL: Do not commit until all bugs in the batch build and pass tests.\n\n`;

  for (const bug of bugs) {
    prompt += `--- BUG #${bug.index} ---\n`;
    prompt += `Description: ${bug.text}\n`;
    if (bug.attachmentUrls.length > 0) {
      prompt += `Screenshots: ${bug.attachmentUrls.join(', ')}\n`;
    }
    prompt += `\n`;
  }

  prompt += `Repository: littlebridgeteam/VidyaPrayag\n\n`;
  prompt += `When done, reply in each bug's Slack thread with:\n`;
  for (const bug of bugs) {
    prompt += `- Bug #${bug.index}: [summary]\n`;
  }
  prompt += `Also include: Build: PASSED | Tests: PASSED | Branch: ${env.BRANCH_NAME}\n`;
  prompt += `Do NOT mention any PR.\n`;

  return prompt;
}

async function postSlackReply(env, channel, threadTs, text) {
  await fetch('https://slack.com/api/chat.postMessage', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${env.SLACK_BOT_TOKEN}`,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      channel,
      thread_ts: threadTs,
      text,
    }),
  });
}

async function renderDashboard(env) {
  const repo = 'littlebridgeteam/VidyaPrayag';
  const branch = env.BRANCH_NAME;

  // Load bugs from D1
  const { results: bugs } = await env.DB.prepare(
    `SELECT * FROM bug_queue ORDER BY created_at DESC`
  ).all();

  // Load recent commits on slack_fix from GitHub (public API, no token needed for public repos)
  let commits = [];
  try {
    const ghRes = await fetch(`https://api.github.com/repos/${repo}/commits?sha=${branch}&per_page=20`);
    if (ghRes.ok) {
      commits = await ghRes.json();
    }
  } catch (err) {
    console.error('GitHub commits fetch error:', err);
  }

  const byStatus = (status) => bugs.filter(b => b.status === status);

  const rows = (items) => items.map(b => {
    const sessionLink = b.devin_session_id
      ? `<a href="https://app.devin.ai/sessions/${b.devin_session_id}" target="_blank">Devin session</a>`
      : '-';
    const slackLink = b.slack_message_ts
      ? `<a href="https://slack.com/archives/${b.slack_channel_id}/p${b.slack_message_ts.replace('.', '')}" target="_blank">Slack message</a>`
      : '-';
    return `<tr>
      <td>${b.id}</td>
      <td>${escapeHtml(b.text)}</td>
      <td>${b.status}</td>
      <td>${sessionLink}</td>
      <td>${slackLink}</td>
      <td>${b.created_at}</td>
    </tr>`;
  }).join('') || '<tr><td colspan="6">No bugs</td></tr>';

  const commitRows = commits.map(c => `<tr>
    <td><a href="${c.html_url}" target="_blank">${escapeHtml(c.sha.substring(0, 7))}</a></td>
    <td>${escapeHtml(c.commit.message.split('\n')[0])}</td>
    <td>${escapeHtml(c.commit.author.name)}</td>
    <td>${c.commit.author.date}</td>
  </tr>`).join('') || '<tr><td colspan="4">No commits found</td></tr>';

  return `<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>Devin Bug Bridge Dashboard</title>
  <style>
    body { font-family: system-ui, sans-serif; background: #0f172a; color: #e2e8f0; padding: 24px; }
    h1 { color: #38bdf8; }
    h2 { margin-top: 32px; color: #94a3b8; border-bottom: 1px solid #334155; padding-bottom: 8px; }
    table { width: 100%; border-collapse: collapse; margin-top: 12px; }
    th, td { padding: 10px; text-align: left; border-bottom: 1px solid #334155; }
    th { color: #cbd5e1; }
    a { color: #38bdf8; }
    .badge { display: inline-block; padding: 2px 8px; border-radius: 4px; font-size: 12px; }
    .status-pending { background: #f59e0b; color: #000; }
    .status-processing { background: #3b82f6; color: #fff; }
    .status-failed { background: #ef4444; color: #fff; }
    .status-done { background: #22c55e; color: #000; }
    .meta { color: #94a3b8; font-size: 14px; margin-bottom: 24px; }
  </style>
</head>
<body>
  <h1>Devin Bug Bridge Dashboard</h1>
  <p class="meta">
    Repo: <strong>${repo}</strong> | Branch: <strong>${branch}</strong> | Model: <strong>Kimi K2.7</strong><br>
    This dashboard only shows the <code>${branch}</code> branch. No other branch is touched.
  </p>

  <h2>Pending</h2>
  <table>
    <thead><tr><th>ID</th><th>Bug</th><th>Status</th><th>Session</th><th>Slack</th><th>Reported</th></tr></thead>
    <tbody>${rows(byStatus('pending'))}</tbody>
  </table>

  <h2>Processing</h2>
  <table>
    <thead><tr><th>ID</th><th>Bug</th><th>Status</th><th>Session</th><th>Slack</th><th>Reported</th></tr></thead>
    <tbody>${rows(byStatus('processing'))}</tbody>
  </table>

  <h2>Failed</h2>
  <table>
    <thead><tr><th>ID</th><th>Bug</th><th>Status</th><th>Session</th><th>Slack</th><th>Reported</th></tr></thead>
    <tbody>${rows(byStatus('failed'))}</tbody>
  </table>

  <h2>Recent commits on ${branch}</h2>
  <table>
    <thead><tr><th>SHA</th><th>Message</th><th>Author</th><th>Date</th></tr></thead>
    <tbody>${commitRows}</tbody>
  </table>

  <p class="meta" style="margin-top: 40px;">
    Auto-deactivation: <strong>2026-07-16 23:59 UTC</strong>
  </p>
</body>
</html>`;
}

function escapeHtml(str) {
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#039;');
}

function json(obj, status = 200) {
  return new Response(JSON.stringify(obj), {
    status,
    headers: { 'Content-Type': 'application/json' },
  });
}

function isAfterDeactivationDate() {
  // Free Kimi K2.7 access ends July 16, 2026 23:59:59 UTC.
  const cutoff = new Date('2026-07-16T23:59:59Z');
  return new Date() > cutoff;
}
