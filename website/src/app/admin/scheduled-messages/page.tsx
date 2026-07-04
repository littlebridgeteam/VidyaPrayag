"use client";
import { errorMessage } from "@/lib/errorUtils";


import { useState, useEffect, useCallback } from "react";
import { authRequest } from "@/lib/admin/client";
import { Card, CardHeader, EmptyState, FadeIn, Skeleton, Badge } from "@/components/admin/Primitives";
import { AdminButton, Modal } from "@/components/admin/Toolbar";
import { IconSchedule, IconClose } from "@/components/admin/icons";

interface ScheduledMessageDto {
  id: string;
  messageType: string;
  status: string;
  scheduledAt: string;
  dispatchedAt: string | null;
  title: string | null;
  bodyPreview: string | null;
  authorName: string | null;
  authorRole: string | null;
  audienceType: string | null;
  audienceLabel: string | null;
  retryCount: number;
  lastError: string | null;
}

const STATUS_FILTERS = [
  { value: "", label: "All" },
  { value: "SCHEDULED", label: "Scheduled" },
  { value: "DISPATCHED", label: "Dispatched" },
  { value: "CANCELLED", label: "Cancelled" },
  { value: "FAILED", label: "Failed" },
];

const MESSAGE_TYPES = ["ANNOUNCEMENT", "ADMIN_BROADCAST", "TEACHER_BROADCAST"];

function statusTone(s: string): "success" | "warning" | "danger" | "neutral" {
  if (s === "DISPATCHED") return "success";
  if (s === "SCHEDULED") return "warning";
  if (s === "CANCELLED" || s === "FAILED") return "danger";
  return "neutral";
}

export default function ScheduledMessagesPage() {
  const [messages, setMessages] = useState<ScheduledMessageDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [statusFilter, setStatusFilter] = useState("");
  const [busyId, setBusyId] = useState<string | null>(null);
  const [showCreate, setShowCreate] = useState(false);
  const [createForm, setCreateForm] = useState({
    messageType: "ANNOUNCEMENT",
    title: "",
    bodyPreview: "",
    scheduledAt: "",
    audienceType: "all",
    audienceLabel: "All school",
  });
  const [creating, setCreating] = useState(false);
  const [createError, setCreateError] = useState<string | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const qs = statusFilter ? `?status=${statusFilter}` : "";
      const res = await authRequest<{ messages: ScheduledMessageDto[] } | { scheduledMessages: ScheduledMessageDto[] } | ScheduledMessageDto[]>(
        `/api/v1/school/scheduled-messages${qs}`
      );
      const raw = res as Record<string, unknown>;
      setMessages((Array.isArray(raw) ? raw : (raw.messages as ScheduledMessageDto[]) ?? (raw.scheduledMessages as ScheduledMessageDto[])) ?? []);
    } catch (e) {
      setError(`Failed to load scheduled messages: ${errorMessage(e)}`);
    } finally {
      setLoading(false);
    }
  }, [statusFilter]);

  useEffect(() => { load(); }, [load]);

  const cancelMessage = useCallback(async (id: string) => {
    setBusyId(id);
    setError(null);
    try {
      await authRequest(`/api/v1/school/scheduled-messages/${id}`, { method: "DELETE" });
      setMessages(prev => prev.map(m => m.id === id ? { ...m, status: "CANCELLED" } : m));
    } catch (e) {
      setError(`Failed to cancel: ${errorMessage(e)}`);
    } finally {
      setBusyId(null);
    }
  }, []);

  const createMessage = useCallback(async () => {
    setCreating(true);
    setCreateError(null);
    try {
      const dt = new Date(createForm.scheduledAt);
      const iso = dt.toISOString();
      await authRequest("/api/v1/school/scheduled-messages", {
        method: "POST",
        body: {
          messageType: createForm.messageType,
          scheduledAt: iso,
          title: createForm.title,
          bodyPreview: createForm.bodyPreview,
          audienceType: createForm.audienceType,
          audienceLabel: createForm.audienceLabel,
          payload: { title: createForm.title, body: createForm.bodyPreview },
          addToCalendar: false,
        },
      });
      setShowCreate(false);
      setCreateForm({ messageType: "ANNOUNCEMENT", title: "", bodyPreview: "", scheduledAt: "", audienceType: "all", audienceLabel: "All school" });
      await load();
    } catch (e) {
      setCreateError(`Failed to create: ${errorMessage(e)}`);
    } finally {
      setCreating(false);
    }
  }, [createForm, load]);

  return (
    <div className="space-y-6">
      <FadeIn>
        <div className="flex items-center justify-between gap-3">
          <div className="flex items-center gap-3">
            <div className="flex h-10 w-10 items-center justify-center rounded-2xl bg-accent/10 text-accent-deep">
              <IconSchedule />
            </div>
            <div>
              <h1 className="text-[22px] font-bold tracking-tight text-navy-deep">Scheduled Messages</h1>
              <p className="text-[13px] text-ink-3">Announcements and broadcasts scheduled for future delivery.</p>
            </div>
          </div>
          <AdminButton onClick={() => setShowCreate(true)}>Schedule New</AdminButton>
        </div>
      </FadeIn>

      <FadeIn delay={0.05}>
        <Card>
          <div className="border-b border-navy/8 p-4">
            <div className="flex items-center gap-1.5">
              {STATUS_FILTERS.map(f => (
                <button key={f.value} onClick={() => setStatusFilter(f.value)} className={`shrink-0 rounded-full px-3 py-1.5 text-[12.5px] font-semibold transition-colors ${statusFilter === f.value ? "bg-navy-deep text-white" : "bg-navy/6 text-ink-2 hover:bg-navy/10"}`}>
                  {f.label}
                </button>
              ))}
            </div>
          </div>
          <CardHeader title="All Scheduled Messages" subtitle={`${messages.length} message${messages.length !== 1 ? "s" : ""}`} />
          {error && <p className="px-5 pt-3 text-[13px] font-medium text-danger">{error}</p>}
          {loading ? <div className="space-y-2 p-4">{Array.from({ length: 3 }).map((_, i) => <Skeleton key={i} className="h-14" />)}</div>
          : messages.length === 0 ? <EmptyState title="No scheduled messages" hint="Schedule a new message to get started." icon={<IconSchedule />} />
          : <div className="overflow-x-auto">
              <table className="w-full text-left text-[13px]">
                <thead className="text-[11px] uppercase tracking-wide text-ink-3 border-b border-navy/[0.06]">
                  <tr>
                    <th className="px-5 py-3 font-semibold">Title</th>
                    <th className="px-5 py-3 font-semibold">Type</th>
                    <th className="px-5 py-3 font-semibold">Scheduled For</th>
                    <th className="px-5 py-3 font-semibold">Author</th>
                    <th className="px-5 py-3 font-semibold">Status</th>
                    <th className="px-5 py-3 font-semibold">Action</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-navy/[0.03]">
                  {messages.map((m) => (
                    <tr key={m.id} className="hover:bg-navy/[0.02] transition-colors">
                      <td className="px-5 py-3 font-semibold text-navy-deep">{m.title ?? m.bodyPreview ?? "Untitled"}</td>
                      <td className="px-5 py-3 text-ink-2">{m.messageType}</td>
                      <td className="px-5 py-3 text-ink-3">{new Date(m.scheduledAt).toLocaleString()}</td>
                      <td className="px-5 py-3 text-ink-3">{m.authorName ?? "—"}</td>
                      <td className="px-5 py-3"><Badge tone={statusTone(m.status)}>{m.status}</Badge></td>
                      <td className="px-5 py-3">
                        {(m.status === "SCHEDULED" || m.status === "PENDING") && (
                          <AdminButton variant="danger" onClick={() => cancelMessage(m.id)} disabled={busyId === m.id}>
                            Cancel
                          </AdminButton>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>}
        </Card>
      </FadeIn>

      <Modal open={showCreate} onClose={() => setShowCreate(false)} title="Schedule New Message" description="Compose a message to be sent at a future date and time."
        footer={
          <>
            <AdminButton variant="ghost" onClick={() => setShowCreate(false)}>Cancel</AdminButton>
            <AdminButton onClick={createMessage} disabled={creating || !createForm.title || !createForm.scheduledAt}>
              {creating ? "Scheduling…" : "Schedule"}
            </AdminButton>
          </>
        }
      >
        <div className="space-y-4">
          {createError && <p className="text-[13px] font-medium text-danger">{createError}</p>}
          <div>
            <label className="mb-1 block text-[13px] font-semibold text-navy-deep">Type</label>
            <select value={createForm.messageType} onChange={(e) => setCreateForm(p => ({ ...p, messageType: e.target.value }))}
              className="w-full rounded-xl border border-navy/12 bg-white px-4 py-2.5 text-[14px] outline-none focus:border-accent">
              {MESSAGE_TYPES.map(t => <option key={t} value={t}>{t.replace(/_/g, " ")}</option>)}
            </select>
          </div>
          <div>
            <label className="mb-1 block text-[13px] font-semibold text-navy-deep">Title</label>
            <input type="text" value={createForm.title} onChange={(e) => setCreateForm(p => ({ ...p, title: e.target.value }))}
              placeholder="Message title…" className="w-full rounded-xl border border-navy/12 bg-white px-4 py-2.5 text-[14px] outline-none focus:border-accent" />
          </div>
          <div>
            <label className="mb-1 block text-[13px] font-semibold text-navy-deep">Body Preview</label>
            <textarea value={createForm.bodyPreview} onChange={(e) => setCreateForm(p => ({ ...p, bodyPreview: e.target.value }))}
              placeholder="Message body…" rows={3} className="w-full rounded-xl border border-navy/12 bg-white px-4 py-2.5 text-[14px] outline-none focus:border-accent" />
          </div>
          <div>
            <label className="mb-1 block text-[13px] font-semibold text-navy-deep">Schedule For</label>
            <input type="datetime-local" value={createForm.scheduledAt} onChange={(e) => setCreateForm(p => ({ ...p, scheduledAt: e.target.value }))}
              className="w-full rounded-xl border border-navy/12 bg-white px-4 py-2.5 text-[14px] outline-none focus:border-accent" />
          </div>
          <div>
            <label className="mb-1 block text-[13px] font-semibold text-navy-deep">Audience</label>
            <input type="text" value={createForm.audienceLabel} onChange={(e) => setCreateForm(p => ({ ...p, audienceLabel: e.target.value, audienceType: e.target.value.toLowerCase().replace(/\s/g, "_") }))}
              placeholder="e.g. All school, Class 8-A…" className="w-full rounded-xl border border-navy/12 bg-white px-4 py-2.5 text-[14px] outline-none focus:border-accent" />
          </div>
        </div>
      </Modal>
    </div>
  );
}
