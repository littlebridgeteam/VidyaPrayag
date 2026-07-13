/**
 * Local server that receives bug batches from the Cloudflare Worker
 * and runs Devin CLI locally (free Kimi K2.7, no ACU cost).
 *
 * Flow:
 *   Slack → Cloudflare Worker (queue) → POST /process-batch → this server → devin CLI
 */

import http from 'http';
import { spawn } from 'child_process';
import fs from 'fs/promises';
import path from 'path';
import os from 'os';

const PORT = 7788;
const REPO_DIR = 'C:\\Users\\HP\\Devin1\\VidyaPrayag';
const BRANCH = 'slack_fix';
const MODEL = 'kimi-k2.7';
const DEVIN_BIN = 'C:\\Users\\HP\\AppData\\Local\\devin\\cli\\bin\\devin.exe';

// Simple shared secret to prevent random requests
const AUTH_TOKEN = process.env.LOCAL_SERVER_TOKEN || 'devin-bug-bridge-local-2026';

function json(res, status, data) {
  res.writeHead(status, { 'Content-Type': 'application/json' });
  res.end(JSON.stringify(data));
}

async function handleBatch(req, res, body) {
  const { bugs, batchId } = JSON.parse(body);

  if (!bugs || !Array.isArray(bugs) || bugs.length === 0) {
    return json(res, 400, { error: 'No bugs provided' });
  }

  console.log(`\n[${new Date().toISOString()}] Received batch ${batchId} with ${bugs.length} bug(s)`);

  // Respond immediately so the Worker doesn't timeout
  json(res, 202, { ok: true, batchId, message: 'Batch accepted, processing in background' });

  // Process in background
  processBatchInBackground(bugs, batchId).catch(err => {
    console.error(`Background processing error for ${batchId}:`, err);
  });
}

async function processBatchInBackground(bugs, batchId) {
  console.log(`[${new Date().toISOString()}] Starting Devin CLI for ${batchId}...`);

  // Build the prompt for Devin CLI
  let prompt = `You are fixing ${bugs.length} bug(s) reported by the team.\n\n`;
  prompt += `STRICT RULES:\n`;
  prompt += `- Work ONLY in the repository: littlebridgeteam/VidyaPrayag\n`;
  prompt += `- Work ONLY on the branch: ${BRANCH}\n`;
  prompt += `- NEVER create a new branch.\n`;
  prompt += `- NEVER merge into main or any other branch.\n`;
  prompt += `- NEVER open a pull request.\n`;
  prompt += `- ALL fixes, commits, and pushes must happen on ${BRANCH}.\n\n`;
  prompt += `Follow this workflow for the entire batch:\n`;
  prompt += `1. Pull latest ${BRANCH}. If it doesn't exist, create it from main.\n`;
  prompt += `2. For each bug below, find the root cause, implement a minimal fix, and write a focused regression test.\n`;
  prompt += `3. Build the project. Fix all compile errors.\n`;
  prompt += `4. Run the full test suite. Fix all failures.\n`;
  prompt += `5. ONLY if everything builds and passes tests, commit each bug separately and push to ${BRANCH}.\n\n`;
  prompt += `CRITICAL: Do not commit until all bugs in the batch build and pass tests.\n\n`;

  for (const bug of bugs) {
    prompt += `--- BUG #${bug.index} ---\n`;
    prompt += `Description: ${bug.text}\n`;
    if (bug.attachmentUrls && bug.attachmentUrls.length > 0) {
      prompt += `Screenshots: ${bug.attachmentUrls.join(', ')}\n`;
    }
    prompt += `\n`;
  }

  prompt += `Repository: littlebridgeteam/VidyaPrayag\n`;
  prompt += `When done, reply with a summary of each fix.\n`;
  prompt += `Do NOT mention any PR.\n`;

  // Write prompt to temp file (Windows has command line length limits)
  const tmpFile = path.join(os.tmpdir(), `devin-batch-${Date.now()}.txt`);
  await fs.writeFile(tmpFile, prompt, 'utf-8');

  try {
    console.log(`Running devin CLI with --model ${MODEL}...`);
    console.log(`Prompt file: ${tmpFile}`);
    console.log(`Working dir: ${REPO_DIR}`);
    console.log(`--- Devin CLI output (streaming) ---\n`);

    const exportFile = path.join(os.tmpdir(), `devin-export-${batchId}.json`);

    const child = spawn(DEVIN_BIN, [
      '-p',
      '--model', MODEL,
      '--permission-mode', 'dangerous',
      '--prompt-file', tmpFile,
      '--export', exportFile,
    ], {
      cwd: REPO_DIR,
      stdio: ['ignore', 'pipe', 'pipe'],
    });

    let fullStdout = '';
    let fullStderr = '';

    child.stdout.on('data', (data) => {
      const text = data.toString();
      fullStdout += text;
      process.stdout.write(text);
    });

    child.stderr.on('data', (data) => {
      const text = data.toString();
      fullStderr += text;
      process.stderr.write(text);
    });

    await new Promise((resolve, reject) => {
      const timer = setTimeout(() => {
        child.kill();
        reject(new Error('Devin CLI timed out after 30 minutes'));
      }, 30 * 60 * 1000);

      child.on('close', (code) => {
        clearTimeout(timer);
        if (code === 0) resolve();
        else reject(new Error(`Devin CLI exited with code ${code}`));
      });

      child.on('error', (err) => {
        clearTimeout(timer);
        reject(err);
      });
    });

    console.log(`\n--- End of Devin CLI output ---`);
    console.log(`[${new Date().toISOString()}] Batch ${batchId} COMPLETED!`);

    // Try to read export file for full conversation
    try {
      const exportData = await fs.readFile(exportFile, 'utf-8');
      console.log(`\nFull session export saved to: ${exportFile}`);
    } catch {}

    // Clean up temp file
    await fs.unlink(tmpFile).catch(() => {});

    console.log(`\nBatch ${batchId} done. Check git log on ${BRANCH} for commits.`);
  } catch (err) {
    console.error(`\n[${new Date().toISOString()}] Batch ${batchId} FAILED:`, err.message);
    await fs.unlink(tmpFile).catch(() => {});
  }
}

const server = http.createServer(async (req, res) => {
  // Health check
  if (req.method === 'GET' && req.url === '/health') {
    return json(res, 200, { status: 'ok', model: MODEL, branch: BRANCH });
  }

  // Process batch
  if (req.method === 'POST' && req.url === '/process-batch') {
    // Check auth
    const auth = req.headers['x-auth-token'];
    if (auth !== AUTH_TOKEN) {
      return json(res, 401, { error: 'Unauthorized' });
    }

    let body = '';
    for await (const chunk of req) {
      body += chunk;
    }

    try {
      return await handleBatch(req, res, body);
    } catch (err) {
      console.error('Handler error:', err);
      return json(res, 500, { error: err.message });
    }
  }

  return json(res, 404, { error: 'Not found' });
});

server.listen(PORT, () => {
  console.log(`\n========================================`);
  console.log(`  Devin Bug Bridge — Local Server`);
  console.log(`========================================`);
  console.log(`  Port:   ${PORT}`);
  console.log(`  Repo:   ${REPO_DIR}`);
  console.log(`  Branch: ${BRANCH}`);
  console.log(`  Model:  ${MODEL} (FREE — no ACU cost)`);
  console.log(`  Auth:   ${AUTH_TOKEN.substring(0, 10)}...`);
  console.log(`========================================`);
  console.log(`\nWaiting for bug batches from Cloudflare Worker...`);
  console.log(`Health check: http://localhost:${PORT}/health\n`);
});
