"use client";

import { useState, useEffect, useCallback } from "react";
import { authRequest } from "@/lib/admin/client";
import { Card, CardHeader, EmptyState, FadeIn, Skeleton, Badge } from "@/components/admin/Primitives";
import { IconSchedule } from "@/components/admin/icons";

interface ScheduledMessageDto {
  id: string;
  messageType: string;
  status: string;
  scheduledAt: string;
  dispatchedAt: string | null;
  title: string | null;
  bodyPreview: string | null;
  authorName: string | null;
  audienceType: string;
  retryCount: number;
  lastError: string | null;
}

export default function ScheduledMessagesPage() {
  const [messages, setMessages] = useState<ScheduledMessageDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(async () => {
    try {
      const res = await authRequest<{ data: ScheduledMessageDto[] } | ScheduledMessageDto[]>("/api/v1/school/scheduled-messages");
      setMessages(Array.isArray(res) ? res : (res as { data: ScheduledMessageDto[] }).data ?? []);
    } catch (e) {
      setError(`Failed to load scheduled messages: ${(e as Error).message}`);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  const statusTone = (s: string): "success" | "warning" | "danger" | "neutral" => {
    if (s === "DISPATCHED") return "success";
    if (s === "PENDING") return "warning";
    if (s === "CANCELLED" || s === "FAILED") return "danger";
    return "neutral";
  };

  return (
    <div className="space-y-6">
      <FadeIn>
        <div className="flex items-center gap-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-2xl bg-accent/10 text-accent-deep">
            <IconSchedule />
          </div>
          <div>
            <h1 className="text-[22px] font-bold tracking-tight text-navy-deep">Scheduled Messages</h1>
            <p className="text-[13px] text-ink-3">Announcements and broadcasts scheduled for future delivery.</p>
          </div>
        </div>
      </FadeIn>

      <FadeIn delay={0.05}>
        <Card>
          <CardHeader title="All Scheduled Messages" subtitle={`${messages.length} message${messages.length !== 1 ? "s" : ""}`} />
          {loading ? <Skeleton className="h-40" /> : error ? <EmptyState title="Error" hint={error} icon={<IconSchedule />} /> : messages.length === 0 ? <EmptyState title="No scheduled messages" hint="Scheduled messages will appear here." icon={<IconSchedule />} /> : (
            <div className="overflow-x-auto">
              <table className="w-full text-left text-[13px]">
                <thead className="text-[11px] uppercase tracking-wide text-ink-3 border-b border-navy/[0.06]">
                  <tr>
                    <th className="px-5 py-3 font-semibold">Title</th>
                    <th className="px-5 py-3 font-semibold">Type</th>
                    <th className="px-5 py-3 font-semibold">Scheduled For</th>
                    <th className="px-5 py-3 font-semibold">Author</th>
                    <th className="px-5 py-3 font-semibold">Status</th>
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
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </Card>
      </FadeIn>
    </div>
  );
}
