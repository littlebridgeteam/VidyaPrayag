"use client";

import { useEffect, useState } from "react";
import { API_BASE_URL } from "@/lib/api";
import { readSession } from "@/lib/admin/session";
import type { BugDetailDto } from "@/lib/admin/platform-client";
import { Card, StatusBadge, PriorityBadge, SeverityBadge, LoadingState, ErrorState, EmptyState, Tabs, Button } from "@/components/admin/platform/PlatformUI";

export default function BugDetailPage({ params }: { params: { id: string } }) {
  const { id } = params;
  const [data, setData] = useState<BugDetailDto | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [activeTab, setActiveTab] = useState("details");
  const [comment, setComment] = useState("");

  const token = () => readSession()?.token ?? "";

  const load = () => {
    fetch(`${API_BASE_URL}/api/admin/platform/bugs/${id}`, {
      headers: { Authorization: `Bearer ${token()}` },
    })
      .then((r) => r.json())
      .then((j) => { if (j.success) setData(j.data); else setError(j.message); })
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, [id]);

  const submitComment = async () => {
    if (!comment.trim()) return;
    await fetch(`${API_BASE_URL}/api/admin/platform/bugs/${id}/comments`, {
      method: "POST",
      headers: { "Content-Type": "application/json", Authorization: `Bearer ${token()}` },
      body: JSON.stringify({ body: comment }),
    });
    setComment("");
    setLoading(true);
    load();
  };

  if (loading) return <LoadingState message="Loading bug…" />;
  if (error) return <ErrorState message={error} />;
  if (!data) return <EmptyState title="Bug not found" />;

  const b = data.bug;

  return (
    <div className="space-y-6">
      <div className="flex items-start justify-between">
        <div>
          <div className="flex items-center gap-3">
            <span className="font-mono text-xs text-slate-500">{b.bug_id}</span>
            <StatusBadge status={b.status} />
            <PriorityBadge priority={b.priority} />
            {b.severity && <SeverityBadge severity={b.severity} />}
          </div>
          <h2 className="mt-2 text-xl font-bold text-slate-200">{b.title}</h2>
        </div>
        <a href="/platform/bugs" className="text-xs text-slate-500 hover:text-slate-300">← Back to list</a>
      </div>

      <Tabs
        tabs={[
          { id: "details", label: "Details" },
          { id: "comments", label: "Comments", count: data.comments.length },
          { id: "activity", label: "Activity", count: data.activity.length },
          { id: "attachments", label: "Attachments", count: data.attachments.length },
        ]}
        active={activeTab}
        onChange={setActiveTab}
      />

      {activeTab === "details" && (
        <div className="grid grid-cols-1 gap-4 lg:grid-cols-2">
          <Card>
            <h3 className="mb-3 text-sm font-semibold text-slate-300">Description</h3>
            <p className="text-sm text-slate-400">{data.description ?? "No description provided."}</p>
          </Card>
          <Card>
            <h3 className="mb-3 text-sm font-semibold text-slate-300">Environment</h3>
            <dl className="space-y-2 text-xs">
              <Row label="Reproducibility" value={data.reproducibility} />
              <Row label="Environment" value={data.environment} />
              <Row label="Build Version" value={data.build_version} />
              <Row label="Platform" value={data.platform} />
              <Row label="Device" value={data.device} />
              <Row label="OS Version" value={data.os_version} />
              <Row label="Reported By" value={b.reported_by_name} />
              <Row label="Assigned To" value={b.assigned_to_name} />
              <Row label="SLA Due" value={b.sla_due_at ? new Date(b.sla_due_at).toLocaleString() : null} />
            </dl>
          </Card>
          {(data.expected_result || data.actual_result) && (
            <Card className="lg:col-span-2">
              <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                <div>
                  <h4 className="mb-2 text-xs font-semibold text-green-400">Expected Result</h4>
                  <p className="text-sm text-slate-400">{data.expected_result ?? "—"}</p>
                </div>
                <div>
                  <h4 className="mb-2 text-xs font-semibold text-red-400">Actual Result</h4>
                  <p className="text-sm text-slate-400">{data.actual_result ?? "—"}</p>
                </div>
              </div>
            </Card>
          )}
        </div>
      )}

      {activeTab === "comments" && (
        <div className="space-y-4">
          {data.comments.length === 0 ? <EmptyState title="No comments yet" /> : (
            <div className="space-y-3">
              {data.comments.map((c) => (
                <Card key={c.id}>
                  <div className="flex items-center justify-between">
                    <span className="text-sm font-medium text-slate-300">{c.author_name ?? "Unknown"}</span>
                    <span className="text-xs text-slate-500">{new Date(c.created_at).toLocaleString()}</span>
                  </div>
                  <p className="mt-2 text-sm text-slate-400">{c.body}</p>
                </Card>
              ))}
            </div>
          )}
          <Card>
            <textarea
              value={comment}
              onChange={(e) => setComment(e.target.value)}
              placeholder="Add a comment…"
              rows={3}
              className="w-full rounded-lg border border-slate-700 bg-slate-900 px-3 py-2 text-sm text-slate-200 placeholder-slate-600 focus:border-indigo-500 focus:outline-none"
            />
            <div className="mt-2 flex justify-end">
              <Button onClick={submitComment} disabled={!comment.trim()}>Post Comment</Button>
            </div>
          </Card>
        </div>
      )}

      {activeTab === "activity" && (
        <div className="space-y-2">
          {data.activity.length === 0 ? <EmptyState title="No activity logged" /> : (
            data.activity.map((a) => (
              <div key={a.id} className="flex items-center justify-between border-b border-slate-800 py-2 text-xs">
                <div className="flex items-center gap-2">
                  <span className="text-slate-400">{a.actor_name ?? "System"}</span>
                  <span className="text-slate-600">·</span>
                  <span className="text-slate-300">{a.action.replace(/_/g, " ")}</span>
                  {a.field && <span className="text-slate-500">on {a.field}</span>}
                  {a.old_value && <span className="text-slate-600">({a.old_value} → {a.new_value})</span>}
                </div>
                <span className="text-slate-600">{new Date(a.created_at).toLocaleString()}</span>
              </div>
            ))
          )}
        </div>
      )}

      {activeTab === "attachments" && (
        <div className="space-y-3">
          {data.attachments.length === 0 ? <EmptyState title="No attachments" /> : (
            <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
              {data.attachments.map((a) => (
                <Card key={a.id}>
                  <div className="flex items-center justify-between">
                    <span className="text-sm text-slate-300">{a.file_name}</span>
                    <span className="rounded bg-slate-800 px-1.5 py-0.5 text-[10px] text-slate-400">{a.file_type}</span>
                  </div>
                  <a href={a.file_url} target="_blank" rel="noopener noreferrer" className="mt-1 text-xs text-indigo-400 hover:text-indigo-300">View →</a>
                </Card>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  );
}

function Row({ label, value }: { label: string; value?: string | null }) {
  return (
    <div className="flex justify-between">
      <dt className="text-slate-500">{label}</dt>
      <dd className="text-slate-300">{value ?? "—"}</dd>
    </div>
  );
}
